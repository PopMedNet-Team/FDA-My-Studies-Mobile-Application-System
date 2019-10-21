/*
 * Copyright (c) 2016-2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.test.tests.mobileappstudy;

import org.jetbrains.annotations.Nullable;
import org.junit.BeforeClass;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.PostCommand;
import org.labkey.remoteapi.query.SelectRowsCommand;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.ModulePropertyValue;
import org.labkey.test.TestFileUtils;
import org.labkey.test.commands.mobileappstudy.EnrollParticipantCommand;
import org.labkey.test.commands.mobileappstudy.SubmitResponseCommand;
import org.labkey.test.data.mobileappstudy.InitialSurvey;
import org.labkey.test.data.mobileappstudy.QuestionResponse;
import org.labkey.test.data.mobileappstudy.Survey;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.util.ListHelper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.LoggedParam;
import org.labkey.test.util.Maps;
import org.labkey.test.util.PostgresOnlyTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by iansigmon on 12/9/16.
 */
public abstract class BaseMobileAppStudyTest extends BaseWebDriverTest implements PostgresOnlyTest
{
    protected static final String MOBILEAPP_SCHEMA = "mobileappstudy";
    protected static final String LIST_SCHEMA = "lists";
    protected final static String BASE_RESULTS = "{\n" +
            "\t\t\"start\": \"2016-09-06T15:48:13.000+0000\",\n" +
            "\t\t\"end\": \"2016-09-06T15:48:45.000+0000\",\n" +
            "\t\t\"results\": []\n" +
            "}";

    @Override
    protected @Nullable String getProjectName()
    {
        return null;
    }

    @Override
    protected BrowserType bestBrowser()
    {
        return BrowserType.CHROME;
    }

    @Override
    public List<String> getAssociatedModules()
    {
        return Collections.singletonList("MobileAppStudy");
    }

    /**
     * Get apptoken associated to a participant and study via the API
     * @param project study container folder
     * @param studyShortName study parameter
     * @param batchToken get
     * @return the appToken string
     */
    String getNewAppToken(String project, String studyShortName, String batchToken)
    {
        log("Requesting app token for project [" + project +"] and study [" + studyShortName + "]");
        EnrollParticipantCommand cmd = new EnrollParticipantCommand(project, studyShortName, batchToken, this::log);

        cmd.execute(200);
        String appToken = cmd.getAppToken();
        assertNotNull("AppToken was null", appToken);
        log("AppToken received: " + appToken);

        return appToken;
    }

    protected boolean mobileAppTableExists(String table, String schema) throws CommandException, IOException
    {
        Connection cn = createDefaultConnection(true);
        SelectRowsCommand selectCmd = new SelectRowsCommand(schema, table);
        selectCmd.setColumns(Arrays.asList("*"));

        try
        {
            selectCmd.execute(cn, getCurrentContainerPath());
            return true;
        }
        catch (CommandException e)
        {
            if (e.getStatusCode() == 404)
                return false;
            else
                throw e;
        }
    }

    protected SelectRowsResponse getMobileAppData(String table, String schema)
    {
        Connection cn = createDefaultConnection(true);
        SelectRowsCommand selectCmd = new SelectRowsCommand(schema, table);
        selectCmd.setColumns(Arrays.asList("*"));

        SelectRowsResponse selectResp;
        try
        {
            selectResp = selectCmd.execute(cn, getCurrentContainerPath());
        }
        catch (CommandException | IOException e)
        {
            log(e.getMessage());
            throw new RuntimeException(e);
        }

        return selectResp;
    }

    protected SelectRowsResponse getMobileAppDataWithRetry(String table, String schema)
    {
        int waitTime = 1000;
        while (waitTime < 45000)
        {
            try
            {
                return getMobileAppData(table, schema);
            }
            catch (RuntimeException e)
            {
                if (waitTime > 30000)
                    throw e;

                log("Waiting " + waitTime + " before retrying");
                sleep(waitTime);
                waitTime *= 2;
            }
        }

        return null;
    }

    @LogMethod
    protected void assignTokens(List<String> tokensToAssign, String projectName, String studyName)
    {
        Connection connection = createDefaultConnection(false);
        for(String token : tokensToAssign)
        {
            try
            {
                CommandResponse response = assignToken(connection, token, projectName, studyName);
                assertEquals(true, response.getProperty("success"));
                log("Token assigned.");
            }
            catch (IOException | CommandException e)
            {
                throw new RuntimeException("Failed to assign token", e);
            }
        }
    }

    @LogMethod
    protected CommandResponse assignToken(Connection connection, @LoggedParam String token, @LoggedParam String projectName, @LoggedParam String studyName) throws IOException, CommandException
    {
        Command command = new PostCommand("mobileappstudy", "enroll");
        HashMap<String, Object> params = new HashMap<>(Maps.of("shortName", studyName, "token", token));
        command.setParameters(params);
        log("Assigning token: " + token);
        return command.execute(connection, projectName);
    }

    @BeforeClass
    public static void doSetup()
    {
        BaseMobileAppStudyTest initTest = (BaseMobileAppStudyTest) getCurrentTest();
        initTest.setupProjects();
    }

    void setupProjects()
    {
        //Do nothing as default, Tests can override if needed
    }

    /**
     * Wrap question response and submit to server via the API
     *
     * @param qr to send to server
     * @param appToken to use in request
     * @return error message of request if there is one.
     */
    protected String submitQuestion(QuestionResponse qr, String appToken, String surveyName, String surveyVersion, int expectedStatusCode)
    {
        Survey survey = new InitialSurvey(appToken, surveyName, surveyVersion, new Date(), new Date());
        survey.addResponse(qr);

        return submitSurvey(survey, expectedStatusCode);
    }

    /**
     * Submit the survey to server via API
     *
     * @param survey to submit
     * @param expectedStatusCode status code to expect from server
     * @return error message from response (if it exists)
     */
    protected String submitSurvey(Survey survey, int expectedStatusCode)
    {
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, survey);
        cmd.execute(expectedStatusCode);

        return cmd.getExceptionMessage();
    }

    protected void setSurveyMetadataDropDir()
    {
        ModulePropertyValue val = new ModulePropertyValue("MobileAppStudy", "/", "SurveyMetadataDirectory", TestFileUtils.getLabKeyRoot() + "/server/optionalModules/mobileAppStudy/test/sampledata/SurveyMetadata/");
        setModuleProperties(Arrays.asList(val));
    }

    protected void setupProject(String studyName, String projectName, String surveyName, boolean enableResponseCollection)
    {
        _containerHelper.createProject(projectName, "Mobile App Study");
        log("Set a study name.");
        goToProjectHome(projectName);
        SetupPage setupPage = new SetupPage(this);
        setupPage.getStudySetupWebPart().setShortName(studyName);
        if (enableResponseCollection)
            setupPage.getStudySetupWebPart().checkResponseCollection();
        setupPage.validateSubmitButtonEnabled();
        setupPage.getStudySetupWebPart().clickSubmit();
        _listHelper.createList(projectName, surveyName, ListHelper.ListColumnType.AutoInteger, "Key");
    }
}
