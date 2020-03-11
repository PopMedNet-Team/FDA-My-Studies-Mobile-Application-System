/*
 * Copyright (c) 2019 LabKey Corporation
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

import com.google.common.net.MediaType;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.Nullable;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.PostCommand;
import org.labkey.test.WebTestHelper;
import org.labkey.test.categories.Git;
import org.labkey.test.commands.mobileappstudy.SubmitResponseCommand;
import org.labkey.test.components.mobileappstudy.ForwardingTab;
import org.labkey.test.components.mobileappstudy.TokenBatchPopup;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.pages.mobileappstudy.TokenListPage;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.PipelineStatusTable;
import org.labkey.test.util.TestLogger;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpClassCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.StringBody;
import org.mockserver.verify.VerificationTimes;
import org.openqa.selenium.support.ui.FluentWait;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockserver.model.HttpRequest.request;

@Category({Git.class})
public class ForwardResponseTest extends BaseMobileAppStudyTest
{
    protected static final int PORT = 8082;
    private static final String FORWARDING_URL = "http://localhost:" + PORT;
    private static final String FORWARDING_USER = "forwarding_test_user";
    private static final String FORWARDING_PASSWORD = "password";

    public static final String OAUTH_TOKEN_URL_PATH = "oauthRequest";  //Request new auth tokens here
    public static final String OAUTH_TOKEN_FIELD = "access_token";      //Response field containing requested auth token
    public static final String OAUTH_TOKEN_HEADER = "Authorization";            //Header to send token in to endpoint
    public static final String OAUTH_ENDPOINT_PATH = "oauth_endpoint";  //Forward responses here
    public static final String OAUTH_ENDPOINT_PATH2 = "oauth_endpoint2";  //Forward responses here
    public static final String OAUTH_VALID_TEST_TOKEN = "GoodToken";
    public static final String OAUTH_INVALID_TEST_TOKEN = "NotGood";


    protected static ClientAndServer mockServer = null;

    //Needs to match SurveyResponsePipelineJob.FORWARD_JSON_FORMAT
    private static final String FORWARD_BODY_FORMAT = "{\"type\": \"SurveyResponse\", \"metadata\": {\"activityid\": \"%1$s\", \"version\": \"%2$s\"}, \"token\": \"%3$s\", \"data\": %4$s }";

    //Create study
    public final static String STUDY_NAME01 = "ForwardingSuccessOAuth";  // Study names are case insensitive
    public final static String STUDY_NAME02 = "ForwardingFailedOAuth";
    public final static String STUDY_NAME03 = "ForwardingIfAtFirstOAuth";
    public final static String STUDY_NAME04 = "ForwardingSuccess";  // Study names are case insensitive
    public final static String STUDY_NAME05 = "ForwardingFailed";
    public final static String STUDY_NAME06 = "ForwardingIfAtFirst";
    private final static String BASE_PROJECT_NAME = "Response Forwarding Test Project";
    private final static String PROJECT_NAME01 = BASE_PROJECT_NAME + " " + STUDY_NAME01;
    private final static String PROJECT_NAME02 = BASE_PROJECT_NAME + " " + STUDY_NAME02;
    private final static String PROJECT_NAME03 = BASE_PROJECT_NAME + " " + STUDY_NAME03;
    private final static String PROJECT_NAME04 = BASE_PROJECT_NAME + " " + STUDY_NAME04 + " BasicAuth";
    private final static String PROJECT_NAME05 = BASE_PROJECT_NAME + " " + STUDY_NAME05 + " BasicAuth";
    private static final String[] PROJECTS = new String[]{PROJECT_NAME01, PROJECT_NAME02, PROJECT_NAME03, PROJECT_NAME04, PROJECT_NAME05};
    private final static String SURVEY_NAME = "FakeForwardingSurvey";
    private final static String FORWARDING_PIPELINE_JOB_FORMAT = "Survey Response forwarding for %1$s";


    @Override
    protected @Nullable String getProjectName()
    {
        return null;
    }

    @Override
    protected void doCleanup(boolean afterTest)
    {
        for (String projectName : PROJECTS)
        {
            _containerHelper.deleteProject(projectName, afterTest);
        }
    }

    private void initMockserver()
    {
        if((null == mockServer) || (!mockServer.isRunning()))
            mockServer = ClientAndServer.startClientAndServer(PORT);

        int count = 1;

        while(!mockServer.isRunning() & count < 10)
        {
            log("Waiting for mockServer to start.");
            count++;
            sleep(1000);
        }

        mockServer.reset();

        if(mockServer.isRunning()) {
            addRequestMatcher(mockServer, STUDY_NAME01, this::log);
            addRequestMatcher(mockServer, STUDY_NAME02, this::log);
            addRequestMatcher(mockServer, STUDY_NAME03, this::log);
            addRequestMatcher(mockServer, STUDY_NAME04, this::log);
            addRequestMatcher(mockServer, STUDY_NAME05, this::log);
            addRequestMatcher(mockServer, OAUTH_TOKEN_URL_PATH, this::log);
            addRequestMatcher(mockServer, OAUTH_ENDPOINT_PATH, this::log);
            addRequestMatcher(mockServer, OAUTH_ENDPOINT_PATH2, this::log);
            addRequestMatcher(mockServer, FORWARDING_URL, this::log);
        }
        else {
            log("Mockserver is not running, could not add RequestMatcher.");
        }
    }

    private static void addRequestMatcher(ClientAndServer mockServer, String requestPath, Consumer<String> log )
    {
        log.accept(String.format("Adding a response for %1$s requests.", requestPath));
        mockServer.when(
                request()
                        .withMethod("POST")
                        .withPath("/" + requestPath)
        ).respond(HttpClassCallback.callback("org.labkey.test.mockserver.mobileappstudy.MockServerPostCallback"));
    }

    @Override
    void setupProjects()
    {
        setupProject(STUDY_NAME01, PROJECT_NAME01, SURVEY_NAME, true);
        setupProject(STUDY_NAME02, PROJECT_NAME02, SURVEY_NAME, true);
        setupProject(STUDY_NAME03, PROJECT_NAME03, SURVEY_NAME, true);
        setupProject(STUDY_NAME04, PROJECT_NAME04, SURVEY_NAME, true);
        setupProject(STUDY_NAME05, PROJECT_NAME05, SURVEY_NAME, true);

        setSurveyMetadataDropDir();
        initMockserver();
    }

    private void enableOAuthForwarding(String projectName, String tokenRequestPath, String tokenField, String tokenHeader, String forwardingPath)
    {
        log(String.format("Enabling OAuth forwarding for %1$s", projectName));
        goToProjectHome(projectName);
        ForwardingTab forwardingTab = ForwardingTab.beginAt(this);

        String tokenURL = FORWARDING_URL + "/" + tokenRequestPath;
        String endpointURL = FORWARDING_URL + "/" + forwardingPath;


        forwardingTab.setOauthCredentials(tokenURL, tokenField, tokenHeader, endpointURL);
        forwardingTab.submit();
    }

    private void enableBasicAuthForwarding(String projectName, String username, String password, String forwardingPath)
    {
        log(String.format("Enabling basic forwarding for %1$s", projectName));
        goToProjectHome(projectName);
        ForwardingTab forwardingTab = ForwardingTab.beginAt(this);

        forwardingTab.setBasicAuthCredentials(username, password, forwardingPath);
        forwardingTab.submit();
    }

    private TokenListPage createTokenBatch(SetupPage setupPage)
    {
        String tokenBatchId, tokenCount = "100";

        log("Create " + tokenCount + " tokens.");
        TokenBatchPopup tokenBatchPopup = setupPage.getTokenBatchesWebPart().openNewBatchPopup();
        TokenListPage tokenListPage = tokenBatchPopup.createNewBatch(tokenCount);

        tokenBatchId = tokenListPage.getBatchId();
        log("First batch id: " + tokenBatchId);

        return tokenListPage;
    }

    private HttpRequest getMockRequest(String urlPath, String activityId, String version, String enrollmentToken)
    {
        return request()
                .withMethod("POST")
                .withPath("/" + urlPath)
                .withBody(new StringBody(
                    String.format(FORWARD_BODY_FORMAT, activityId, version, enrollmentToken, BASE_RESULTS.replaceAll("\\s", "")),
                    MediaType.PLAIN_TEXT_UTF_8)
                );
    }

    @Test
    public void testOauthForwardResponse()
    {
        goToProjectHome(PROJECT_NAME01);
        enableOAuthForwarding(PROJECT_NAME01, OAUTH_TOKEN_URL_PATH, OAUTH_TOKEN_FIELD, OAUTH_TOKEN_HEADER, OAUTH_ENDPOINT_PATH);

        testSuccessfulForwardResponse(PROJECT_NAME01, STUDY_NAME01, OAUTH_ENDPOINT_PATH);
    }

    @Test
    public void testBasicAuthForwardResponse()
    {
        String projectName = PROJECT_NAME04;

        goToProjectHome(projectName);
        enableBasicAuthForwarding(projectName, FORWARDING_USER, FORWARDING_PASSWORD, FORWARDING_URL + "/" + STUDY_NAME04);

        testSuccessfulForwardResponse(projectName, STUDY_NAME04, STUDY_NAME04);
    }

    @Test
    public void testBasicAuthFailedForwardResponse() throws Exception
    {
        String projectName = PROJECT_NAME05;

        goToProjectHome(projectName);
        enableBasicAuthForwarding(projectName, FORWARDING_USER, FORWARDING_PASSWORD, FORWARDING_URL + "/" + STUDY_NAME05);

        testFailedForwardResponse(projectName, STUDY_NAME05);
    }

    @Test
    public void testOAuthFailedForwardResponse() throws Exception
    {
        String projectName = PROJECT_NAME02;

        goToProjectHome(projectName);
        enableOAuthForwarding(projectName, OAUTH_TOKEN_URL_PATH, OAUTH_TOKEN_FIELD, OAUTH_TOKEN_HEADER, STUDY_NAME02);

        testFailedForwardResponse(projectName, STUDY_NAME02);
    }

    private void testSuccessfulForwardResponse(String projectName, String studyName, String endpointPath)
    {
        goToProjectHome(projectName);
        SetupPage setupPage = new SetupPage(this);
        TokenListPage tokenListPage = createTokenBatch(setupPage);
        String myToken = tokenListPage.getToken(0);

        checkErrors();
        PipelineStatusTable pst = goToDataPipeline();

        log("Testing successfully forwarding response");
        submitResponse(projectName, studyName, myToken);

        String forwardingJobDescription = String.format(FORWARDING_PIPELINE_JOB_FORMAT, projectName);
        //TODO: this may be flaky as Timer job may create one in the interim...
        waitForPipelineJobsToComplete(1, forwardingJobDescription, false);
        assertTrue("Forwarding job failed unexpectedly.", "Complete".equalsIgnoreCase(pst.getJobStatus(forwardingJobDescription)));

        HttpRequest req = getMockRequest(endpointPath, SURVEY_NAME, "1", myToken);
        mockServer.verify(req, VerificationTimes.once()); //Will throw an AssertionError if not found correct number of times.

        log("Checking pipeline job log");
        //TODO: this may be flaky as Timer job may create one in the interim...
        pst.clickStatusLink(forwardingJobDescription);
        assertTextPresent("Forwarding completed. 1 response(s) sent to");
    }

    private void testFailedForwardResponse(String project, String study) throws IOException
    {
        checkErrors();
        PipelineStatusTable pst = goToDataPipeline();

        log("Testing failed forwarding of survey response");
        submitResponse(project, study, null);

        String forwardingJobDescription = String.format(FORWARDING_PIPELINE_JOB_FORMAT, project);
        waitForPipelineJobsToComplete(1, forwardingJobDescription, true);
        assertTrue("Forwarding job Passed unexpectedly.", "Error".equalsIgnoreCase(pst.getJobStatus(forwardingJobDescription)));

        log("Checking pipeline job log");
        pst.clickStatusLink(forwardingJobDescription);
        assertTextPresent("ERROR: Stopping forwarding job.");
        checkExpectedErrors(1);
        disableForwarding(project); // Disable so that failed retries don't cause collateral failures
    }

    @Test
    public void testEnableAndDisableForwarding()
    {
        String project = PROJECT_NAME03;
        String study = STUDY_NAME03;

        goToProjectHome(project);
        HttpRequest req = request().withMethod("POST").withPath("/" + OAUTH_ENDPOINT_PATH2);
        log("Testing forwarding of prior survey responses");

        checkErrors();
        PipelineStatusTable pst = goToDataPipeline();
        int oldCount = pst.getDataRowCount();

        log("Submitting responses prior to enabling forwarding");
        submitResponse(project, study, null);
        submitResponse(project, study, null);
        submitResponse(project, study, null);
        int responseCount = 3;

        sleep(2000);  //Give pipeline job a chance to start processing
        pst = goToDataPipeline();  //refresh page
        int newCount = pst.getDataRowCount();
        assertEquals("Unexpected new pipeline job", oldCount, newCount);
        mockServer.verify(req, VerificationTimes.exactly(0));  //Will throw AssertionError if count doesn't match

        assertEquals("Response forwarding pipeline job count not as expected", 0, pst.getDataRowCount()); //Allow delta of 1 in the event scheduled job runs
        enableOAuthForwarding(project, OAUTH_TOKEN_URL_PATH, OAUTH_TOKEN_FIELD, OAUTH_TOKEN_HEADER, OAUTH_ENDPOINT_PATH2);
        pst = goToDataPipeline();

        log("Submitting response to trigger forwarding now that it is enabled");
        submitResponse(project, study, null);
        responseCount++;

        String forwardingJobDescription = String.format(FORWARDING_PIPELINE_JOB_FORMAT, project);
        waitForPipelineJobsToComplete(1, forwardingJobDescription, false);
        assertTrue("Forwarding job failed unexpectedly.", "Complete".equalsIgnoreCase(pst.getJobStatus(forwardingJobDescription)));

        mockServer.verify(req, VerificationTimes.exactly(responseCount)); //Will throw an AssertionError if not found correct number of times.

        log("Checking pipeline job log");
        //TODO: this may be flaky as Timer job may create one in the interim...
        pst.clickStatusLink(forwardingJobDescription);
        assertTextPresent(String.format("Forwarding completed. %1$s response(s) sent to", responseCount));

        log("Clearing mockserver request logs");
        mockServer.clear(req);

        goToProjectHome(project);
        ForwardingTab tab = ForwardingTab.beginAt(this);
//        tab.validateSubmitButtonDisabled();
        tab.disableForwarding();
//        tab.validateSubmitButtonEnabled();
        tab.submit();

        goToProjectHome(project);
        pst = goToDataPipeline();
        oldCount = pst.getDataRowCount();
        submitResponse(project, study, null);
        sleep(2000);  //Give pipeline job a chance to start processing
        pst = goToDataPipeline();
        newCount = pst.getDataRowCount();

        assertEquals("Unexpected new pipeline job", oldCount, newCount);
        mockServer.verify(req, VerificationTimes.exactly(0));  //Will throw AssertionError if count doesn't match
    }

    private String submitResponse(String projectName, String studyName, String batchtoken)
    {
        String appToken = getNewAppToken(projectName, studyName, batchtoken);
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "1", appToken, BASE_RESULTS);
        cmd.execute(200);
        assertTrue("Submission failed, expected success", cmd.getSuccess());

        return appToken;
    }

    @AfterClass
    @LogMethod
    public static void afterClassCleanup() throws IOException
    {
        for (String projectName : PROJECTS)
        {
            disableForwarding(projectName);
        }

        if(null != mockServer)
        {
            TestLogger.log("Stopping the mockserver.");
            mockServer.stop();

            TestLogger.log("Waiting for the mockserver to stop.");
            new FluentWait<>(mockServer).withMessage("waiting for the mockserver to stop.").until(mockServer -> !mockServer.isRunning());
            TestLogger.log("The mockserver is stopped.");
        }
    }

    @LogMethod
    private static void disableForwarding(String containerPath) throws IOException
    {
        PostCommand<CommandResponse> command = new PostCommand<>("mobileAppStudy", "forwardingSettings");
        Map<String, Object> params = new HashMap<>();
        params.put("forwardingType", "Disabled");
        command.setParameters(params);
        Connection connection = WebTestHelper.getRemoteApiConnection();

        try
        {
            command.execute(connection, containerPath);
        }
        catch (CommandException e)
        {
            if (e.getStatusCode() != HttpStatus.SC_NOT_FOUND)
            {
                TestLogger.warn("Failed to disable mobileAppStudy response forwarding in '" + containerPath + "'");
                e.printStackTrace();
            }
        }
    }
}
