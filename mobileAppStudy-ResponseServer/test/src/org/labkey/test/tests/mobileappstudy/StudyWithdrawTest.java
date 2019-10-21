/*
 * Copyright (c) 2017-2019 LabKey Corporation
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

import org.apache.http.HttpResponse;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.labkey.test.TestFileUtils;
import org.labkey.test.categories.Git;
import org.labkey.test.commands.mobileappstudy.SubmitResponseCommand;
import org.labkey.test.commands.mobileappstudy.WithdrawParticipantCommand;
import org.labkey.test.data.mobileappstudy.AbstractQuestionResponse;
import org.labkey.test.data.mobileappstudy.ChoiceQuestionResponse;
import org.labkey.test.data.mobileappstudy.GroupedQuestionResponse;
import org.labkey.test.data.mobileappstudy.InitialSurvey;
import org.labkey.test.data.mobileappstudy.MedForm;
import org.labkey.test.data.mobileappstudy.QuestionResponse;
import org.labkey.test.data.mobileappstudy.Survey;
import org.labkey.test.pages.mobileappstudy.SetupPage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Category({Git.class})
public class StudyWithdrawTest extends BaseMobileAppStudyTest
{
    private static final String PROJECT_NAME = "StudyWithdrawTestProject";
    private static final String STUDY_NAME = "StudyWithdrawTestStudy";
    private final static String BASE_RESULTS = "{\n" +
            "\t\t\"start\": \"2016-09-06 15:48:13 +0000\",\n" +
            "\t\t\"end\": \"2016-09-06 15:48:45 +0000\",\n" +
            "\t\t\"results\": []\n" +
            "}";
    private static final List<String> TABLES = Arrays.asList("Response","ResponseMetadata","EnrollmentToken");
    private static final List<String> LISTS = Arrays.asList("InitialSurvey","InitialSurveyBirthControlType",
            "InitialSurveyOtc","InitialSurveyOtcMedCondition","InitialSurveyOtcMedName","InitialSurveyRx",
            "InitialSurveyRxGroup","InitialSurveyRxMedCondition","InitialSurveyRxMedName","InitialSurveyRxMedVsRxUse",
            "InitialSurveyRxWhyDifferent","InitialSurveySupplements");
    private final static String SURVEY_NAME = "InitialSurvey";
    private final static String SURVEY_VERSION = "123.9";
    private static String HAS_RESPONSES_DELETE;
    private static String HAS_RESPONSES_NO_DELETE;
    private static String NO_RESPONSES_DELETE;
    private static String WITHDRAWS_TWICE;
    private static String STAYS_IN;
    private static String HRD_KEY;
    private static String HRND_KEY;
    private static String NRD_KEY;
    private static String WT_KEY;
    private static String SI_KEY;

    @Override
    void setupProjects()
    {
        _containerHelper.deleteProject(getProjectName(),false);
        _containerHelper.createProject(getProjectName(), "Mobile App Study");
        _containerHelper.enableModule("MobileAppStudy");
        goToProjectHome();
        SetupPage setupPage = new SetupPage(this);
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME);
        setupPage.getStudySetupWebPart().checkResponseCollection();
        setupPage.getStudySetupWebPart().clickSubmit();
        HAS_RESPONSES_DELETE = getNewAppToken(PROJECT_NAME, STUDY_NAME, null );
        HAS_RESPONSES_NO_DELETE = getNewAppToken(PROJECT_NAME, STUDY_NAME, null );
        NO_RESPONSES_DELETE = getNewAppToken(PROJECT_NAME, STUDY_NAME, null );
        WITHDRAWS_TWICE = getNewAppToken(PROJECT_NAME, STUDY_NAME, null );
        STAYS_IN = getNewAppToken(PROJECT_NAME, STUDY_NAME, null );
        HRD_KEY = appTokenToParticipantId(HAS_RESPONSES_DELETE);
        HRND_KEY = appTokenToParticipantId(HAS_RESPONSES_NO_DELETE);
        NRD_KEY = appTokenToParticipantId(NO_RESPONSES_DELETE);
        WT_KEY = appTokenToParticipantId(WITHDRAWS_TWICE);
        SI_KEY = appTokenToParticipantId(STAYS_IN);
        setupLists();
        submitResponses(Arrays.asList(HAS_RESPONSES_DELETE,HAS_RESPONSES_NO_DELETE,WITHDRAWS_TWICE,STAYS_IN));

        log("Check that the calls to appTokenToParticipantId produced unique ids.");
        List<String> participantIds = new ArrayList<>();
        participantIds.add(HRD_KEY);
        log("HRD_KEY = " + HRD_KEY);

        log("HRND_KEY = " + HRND_KEY);
        if(participantIds.contains(HRND_KEY))
        {
            HRND_KEY = appTokenToParticipantId(HAS_RESPONSES_NO_DELETE);
            log("Value for HRND_KEY already used.generating a new one: " + HRND_KEY);
            participantIds.add(HRND_KEY);
        }

        log("NRD_KEY = " + NRD_KEY);
        if(participantIds.contains(NRD_KEY))
        {
            NRD_KEY = appTokenToParticipantId(NO_RESPONSES_DELETE);
            log("Value for NRD_KEY already used.generating a new one: " + NRD_KEY);
            participantIds.add(NRD_KEY);
        }

        log("WT_KEY = " + WT_KEY);
        if(participantIds.contains(WT_KEY))
        {
            WT_KEY = appTokenToParticipantId(WITHDRAWS_TWICE);
            log("Value for WT_KEY already used.generating a new one: " + WT_KEY);
            participantIds.add(WT_KEY);
        }

        log("SI_KEY = " + SI_KEY);
        if(participantIds.contains(SI_KEY))
        {
            SI_KEY = appTokenToParticipantId(STAYS_IN);
            log("Value for SI_KEY already used.generating a new one: " + SI_KEY);
            participantIds.add(SI_KEY);
        }

    }

    private void setupLists()
    {
        //Import static survey lists to populate
        goToProjectHome();
        //TODO: This archive has not been updated to match some of the newer BTC & dynamic schema changes
        //       specifically: SurveyId is now dynamically named in sub-lists to match the parent-list
        _listHelper.importListArchive(TestFileUtils.getSampleData("TestLists.lists.zip"));
        goToProjectHome();
        setSurveyMetadataDropDir();
    }

    @Nullable
    @Override
    protected String getProjectName()
    {
        return PROJECT_NAME;
    }

    @Test
    public void testParticipantWithdrawal()
    {
        goToProjectHome();

        //foreach, validate status update, data deletion, appToken null
        //invalid token
        WithdrawParticipantCommand command = new WithdrawParticipantCommand("BadToken",false, this::log);
        HttpResponse httpResponse = command.execute(400);
        SelectRowsResponse selectResponse = getMobileAppData("Participant");
        //valid token, no delete
        log("withdraw participant " + HRD_KEY + " delete responses");
        command = new WithdrawParticipantCommand(HAS_RESPONSES_DELETE, true, this::log);
        command.execute(200);
        //valid token, delete
        log("withdraw participant " + HRND_KEY + " do not delete responses");
        command = new WithdrawParticipantCommand(HAS_RESPONSES_NO_DELETE, false, this::log);
        command.execute(200);
        //no data
        log("withdraw participant " + NRD_KEY + " delete responses");
        command = new WithdrawParticipantCommand(NO_RESPONSES_DELETE, true, this::log);
        command.execute(200);
        //valid token, already withdrawn
        log("withdraw participant " + WT_KEY + " do not delete responses");
        command = new WithdrawParticipantCommand(WITHDRAWS_TWICE, false, this::log);
        command.execute(200);
        log("attempt to withdraw participant " + WT_KEY + " delete responses");
        command = new WithdrawParticipantCommand(WITHDRAWS_TWICE, true, this::log);
        command.execute(400);

        //all users except STAYS_IN should no longer be enrolled
        log("verify participant status");

        // Review all of the expected scenarios, before failing. Want to see if it is just one record
        // failed or if several of them did.
        StringBuilder errorMsg = new StringBuilder();

        if(isUserEnrolled(HRD_KEY))
            errorMsg.append("User " + HRD_KEY + " still shown as enrolled in Participant after withdrawing.\n");

        if(isUserEnrolled(HRND_KEY))
            errorMsg.append("User " + HRND_KEY + " still shown as enrolled in Participant after withdrawing.\n");

        if(isUserEnrolled(NRD_KEY))
            errorMsg.append("User " + NRD_KEY + " still shown as enrolled in Participant after withdrawing.\n");

        if(isUserEnrolled(WT_KEY))
            errorMsg.append("User " + WT_KEY + " still shown as enrolled in Participant after withdrawing.\n");

        //User who did not withdraw should be enrolled
        if(!isUserEnrolled(SI_KEY))
            errorMsg.append("User " + SI_KEY + " still shown as enrolled in Participant after withdrawing.\n");

        log("verify participant data deletion");
        //Users who have elected to have data deleted should have responses deleted
        if(tablesWithParticipantData(HRD_KEY).size()!=0)
            errorMsg.append("Data found for user " + HRD_KEY + " that should have been deleted.\n");

        if(tablesWithParticipantData(NRD_KEY).size()!=0)
            errorMsg.append("Data found for user " + NRD_KEY + " that should have been deleted.\n");

        //Users who have elected not to have data deleted should retain data
        if(tablesWithParticipantData(HRND_KEY).size()<=0)
            errorMsg.append("Data should have been retained for user " + HRND_KEY + " but was not.\n");

        if(tablesWithParticipantData(SI_KEY).size()<=0)
            errorMsg.append("Data should have been retained for user " + SI_KEY + " but was not.\n");

        if(tablesWithParticipantData(WT_KEY).size()<=0)
            errorMsg.append("Data should have been retained for user " + WT_KEY + " but was not.\n");

        //check tokens are null for withdrawn participants
        if(!isAppTokenNull(HAS_RESPONSES_DELETE))
            errorMsg.append("User " + HRD_KEY + " did not have appToken null after withdrawal.\n");

        if(!isAppTokenNull(HAS_RESPONSES_NO_DELETE))
            errorMsg.append("User " + HRND_KEY + " did not have appToken null after withdrawal.\n");

        if(!isAppTokenNull(NO_RESPONSES_DELETE))
            errorMsg.append("User " + NRD_KEY + " did not have appToken null after withdrawal.\n");

        if(!isAppTokenNull(WITHDRAWS_TWICE))
            errorMsg.append("User " + WT_KEY + " did not have appToken null after withdrawal.\n");

        //check tokens retained for remaining users
        if(isAppTokenNull(STAYS_IN))
            errorMsg.append("User " + SI_KEY + " did not withdraw but did not retain appToken.\n");

        Assert.assertTrue(errorMsg.toString(), errorMsg.length() == 0);
    }

    private SelectRowsResponse getMobileAppData(String table)
    {
        return getMobileAppData(table, MOBILEAPP_SCHEMA);
    }



    private void submitResponses(List<String> appTokens)
    {
        for (String appToken : appTokens)
        {
            String fieldName = InitialSurvey.PLANNED_PREGNANCY;
            AbstractQuestionResponse.SupportedResultType type = AbstractQuestionResponse.SupportedResultType.BOOL;

            QuestionResponse qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, false);
            submitQuestion(qr, appToken, 200);

            type = AbstractQuestionResponse.SupportedResultType.DATE;
            fieldName = InitialSurvey.DUE_DATE;

            Date dateVal = new Date();
            qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, dateVal);
            submitQuestion(qr, appToken, 200);

            type = AbstractQuestionResponse.SupportedResultType.TEXT;
            fieldName = InitialSurvey.ILLNESS_WEEK;
            String val = "I hate waffles";

            qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, val);
            submitQuestion(qr, appToken, 200);

            String field = InitialSurvey.SUPPLEMENTS;
            String value1 = "Pancakes";
            String value2 = "French Toast";

            qr = new ChoiceQuestionResponse( field, new Date(), new Date(), false,
                    value1, "Waffles", value2, "Crepes");
            submitQuestion(qr, appToken, 200);

            String rx = MedForm.MedType.Prescription.getDisplayName();
            qr = new ChoiceQuestionResponse("medName",
                    new Date(), new Date(), false, "Acetaminophen");
            QuestionResponse groupedQuestionResponse = new GroupedQuestionResponse(rx,
                    new Date(), new Date(), false, qr);
            submitQuestion(groupedQuestionResponse, appToken, 200);

            QuestionResponse groupedQuestionResponse1 = new GroupedQuestionResponse("rx",
                    new Date(), new Date(), false, new GroupedQuestionResponse("Group", new Date(), new Date(), false,
                    new QuestionResponse(AbstractQuestionResponse.SupportedResultType.BOOL, "Bool", new Date(), new Date(), false, true),
                    new QuestionResponse(AbstractQuestionResponse.SupportedResultType.NUMERIC, "Decimal", new Date(), new Date(), false, 3.14),
                    new QuestionResponse(AbstractQuestionResponse.SupportedResultType.TEXT, "Text", new Date(), new Date(), false, "I'm part of a grouped group"),
                    new QuestionResponse(AbstractQuestionResponse.SupportedResultType.DATE, "Date", new Date(), new Date(), false, new Date())
            ));
            submitQuestion(groupedQuestionResponse1, appToken, 200);
        }
    }

    /**
     * Wrap question response and submit to server via the API
     *
     * @param qr to send to server
     * @param appToken to use in request
     * @return error message of request if there is one.
     */
    private String submitQuestion(QuestionResponse qr, String appToken, int expectedStatusCode)
    {
        Survey survey = new InitialSurvey(appToken, SURVEY_NAME, SURVEY_VERSION, new Date(), new Date());
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
    @Override
    protected String submitSurvey(Survey survey, int expectedStatusCode)
    {
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, survey);
        cmd.execute(expectedStatusCode);
        String msg = cmd.getExceptionMessage();
        log("submitting response " + survey.getResponseJson());
        if(null != msg){log(msg);}
        return cmd.getExceptionMessage();
    }

    private boolean isUserEnrolled(String participantId)
    {
        SelectRowsResponse selectResponse = getMobileAppData("Participant");
        List<Map<String,Object>> rows = selectResponse.getRows();
        for(Map<String,Object> row : rows)
        {
            if(String.valueOf(row.get("RowId")).equals(participantId))
            {
                if(row.get("Status").equals(0)) return true;
            }
        }
        return false;
    }

    private boolean isAppTokenNull(String appToken)
    {
        SelectRowsResponse selectResponse = getMobileAppData("Participant");
        List<Map<String,Object>> rows = selectResponse.getRows();
        for(Map<String,Object> row : rows)
        {
            if(String.valueOf(row.get("AppToken")).equals(appToken))
            {
                return false;
            }
        }
        return true;
    }

    private String appTokenToParticipantId(String appToken)
    {
        SelectRowsResponse selectResponse = getMobileAppData("Participant");
        List<Map<String,Object>> rows = selectResponse.getRows();
        String participant = "";
        for(Map<String,Object> row : rows)
        {
            if(row.get("AppToken").equals(appToken))
            {
                participant=String.valueOf(row.get("RowId"));
            }
        }
        return participant;
    }

    private List<String> getCol(String table, String col, String schema)
    {
        List<String> column = new ArrayList<>();
        SelectRowsResponse selectResponse = getMobileAppData(table,schema);
        List<Map<String,Object>> rows = selectResponse.getRows();
        for(Map<String,Object> row : rows)
        {
            column.add(String.valueOf(row.get(col)));
        }
        return column;
    }

    private boolean isParticipantIdPresent(String table, String participantId, String schema)
    {
        List<String> col = getCol(table, "ParticipantId",schema);
        return col.contains(participantId);
    }

    private List<String> tablesWithParticipantData(String participantId)
    {
        List<String> tablesWithData = new ArrayList<>();
        for (String table : TABLES)
        {
            if (isParticipantIdPresent(table, participantId,MOBILEAPP_SCHEMA))
            {
                log(String.format("Found data for participant %s in table %s", participantId, table));
                tablesWithData.add(table);
            }
        }
        for (String table : LISTS)
            if (isParticipantIdPresent(table, participantId,LIST_SCHEMA))
            {
                log(String.format("Found data for participant %s in list %s", participantId, table));
                tablesWithData.add(table);
            }
        return tablesWithData;
    }
}
