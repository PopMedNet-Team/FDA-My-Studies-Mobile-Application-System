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

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.labkey.test.categories.Git;
import org.labkey.test.commands.mobileappstudy.EnrollmentTokenValidationCommand;
import org.labkey.test.components.ext4.Error;
import org.labkey.test.components.mobileappstudy.TokenBatchPopup;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.pages.mobileappstudy.TokenListPage;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.LoggedParam;
import org.labkey.test.util.PortalHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@Category({Git.class})
public class ConfigAndEnrollTest extends BaseMobileAppStudyTest
{
    private static final String PROJECT_NAME01 = "ConfigAndEnrollTest TestStudyName01";
    private static final String PROJECT_NAME02 = "ConfigAndEnrollTest TestStudyName02";
    private static final String PROJECT_NAME03 = "ConfigAndEnrollTest CollectionToggling";
    private static final String PROJECT_NAME04 = "ConfigAndEnrollTest TestEnroll01";
    private static final String PROJECT_NAME05 = "ConfigAndEnrollTest TestEnroll02";

    protected final PortalHelper _portalHelper = new PortalHelper(this);

    @Override
    protected void doCleanup(boolean afterTest)
    {
        _containerHelper.deleteProject(PROJECT_NAME01, false);
        _containerHelper.deleteProject(PROJECT_NAME02, false);
        _containerHelper.deleteProject(PROJECT_NAME03, false);
        _containerHelper.deleteProject(PROJECT_NAME04, false);
        _containerHelper.deleteProject(PROJECT_NAME05, false);
    }

    @Test
    public void testStudyName()
    {
        final String PROMPT_NOT_ASSIGNED = "Enter the StudyId to be associated with this folder. The StudyId should be the same as it appears in the study design interface.";
        final String PROMPT_ASSIGNED = "The StudyId associated with this folder is $STUDY_NAME$.";
        final String STUDY_NAME01 = "StudyName01";  // Study names are case insensitive, so test it once.
        final String STUDY_NAME02 = "STUDYNAME02";
        final String REUSED_STUDY_NAME_ERROR = "There were problems storing the configuration. StudyId '$STUDY_NAME$' is already associated with a different container within this folder. Each study can be associated with only one container per folder.";

        String batchId, expectedTokenCount = "100";
        List<String> tokensToAssign = new ArrayList<>();
        SetupPage setupPage;

        _containerHelper.createProject(PROJECT_NAME01, "Mobile App Study");
        goToProjectHome(PROJECT_NAME01);

        setupPage = new SetupPage(this);

        log("Validate the prompt.");
        assertEquals("The prompt is not as expected.", PROMPT_NOT_ASSIGNED, setupPage.getStudySetupWebPart().getPrompt());
        setupPage.validateSubmitButtonDisabled();

        log("Set a study name.");
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME01);
        setupPage.validateSubmitButtonEnabled();
        setupPage.getStudySetupWebPart().clickSubmit();

        log("Validate that the submit button is disabled after you click it.");
        assertFalse("Submit button is showing as enabled, it should not be.", setupPage.getStudySetupWebPart().isSubmitEnabled());

        log("Remove the web part,bring it back and validate the study name is still there.");
        setupPage.getStudySetupWebPart().remove();
        _portalHelper.addWebPart("Mobile App Study Setup");
        setupPage = new SetupPage(this);

        log("Validate that the Study Short Name field is still set.");
        assertEquals("Study name did not persist after removing the web part.", STUDY_NAME01.toUpperCase(), setupPage.getStudySetupWebPart().getShortName());
        setupPage.validateSubmitButtonDisabled();

        log("Change the study name and submit.");
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME02);
        setupPage.validateSubmitButtonEnabled();
        setupPage.getStudySetupWebPart().clickSubmit();

        log("Create a new project and try to reuse the study name.");
        _containerHelper.createProject(PROJECT_NAME02, "Mobile App Study");
        goToProjectHome(PROJECT_NAME02);

        setupPage = new SetupPage(this);

        log("Set the study name to a value already saved.");
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME02);

        final Error error = setupPage.getStudySetupWebPart().submitAndExpectError();

        assertEquals("Error message text does not match", REUSED_STUDY_NAME_ERROR.replace("$STUDY_NAME$", STUDY_NAME02), error.getBody());
        error.clickOk();

        log("Reuse the first study name");

        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME01);

        setupPage.getStudySetupWebPart().clickSubmit();

        log("Now create some tokens and use them and then validate that the study name cannot be changed.");

        log("Create " + expectedTokenCount + " tokens.");
        TokenBatchPopup tokenBatchPopup = setupPage.getTokenBatchesWebPart().openNewBatchPopup();
        TokenListPage tokenListPage = tokenBatchPopup.createNewBatch(expectedTokenCount);

        batchId = tokenListPage.getBatchId();
        log("First batch id: " + batchId);

        tokensToAssign.add(tokenListPage.getToken(0));
        tokensToAssign.add(tokenListPage.getToken(1));
        tokensToAssign.add(tokenListPage.getToken(2));

        log("Go back to the setup page.");
        goToProjectHome(PROJECT_NAME02);


        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, batchId, expectedTokenCount, "0");

        log("Now assign some of the tokens.");
        assignTokens(tokensToAssign, PROJECT_NAME02, STUDY_NAME01);

        log("Looks like the assignment worked, now go check that the data is reflected in the setup page and that the study name cannot be changed.");

        goToProjectHome(PROJECT_NAME02);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, batchId, expectedTokenCount, Integer.toString(tokensToAssign.size()));

        log("Validate the prompt.");
        assertEquals("The prompt is not as expected.", PROMPT_ASSIGNED.replace("$STUDY_NAME$", STUDY_NAME01.toUpperCase()), setupPage.getStudySetupWebPart().getPrompt());
        assertFalse("The short name field is visible and it should not be.", setupPage.getStudySetupWebPart().isShortNameVisible());
    }

    @Test
    public void testCollectionToggling()
    {
        final String STUDY_NAME01 = "STUDYNAME01";

        _containerHelper.createProject(PROJECT_NAME03, "Mobile App Study");

        goToProjectHome(PROJECT_NAME03);
        SetupPage setupPage = new SetupPage(this);

        //Validate collection checkbox behavior
        log("Collection is initially disabled");
        assertFalse("Response collection is enabled at study creation", setupPage.getStudySetupWebPart().isResponseCollectionChecked());
        setupPage.validateSubmitButtonDisabled();

        log("Enabling response collection doesn't allow submit prior to a valid study name");
        setupPage.getStudySetupWebPart().checkResponseCollection();
        setupPage.validateSubmitButtonDisabled();

        log("Set a study name.");
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME01);
        setupPage.validateSubmitButtonEnabled();

        log("Disabling response collection allows study config submission");
        setupPage.getStudySetupWebPart().uncheckResponseCollection();
        setupPage.validateSubmitButtonEnabled();

        log("Clearing StudyId disables submit button");
        setupPage.getStudySetupWebPart().setShortName("");
        setupPage.validateSubmitButtonDisabled();
    }

    @Test
    public void testEnroll()
    {
        final String PROJECT01_STUDY_NAME = "TEST_ENROLL_STUDY01";
        final String PROJECT02_STUDY_NAME = "TEST_ENROLL_STUDY02";

        String proj01_batchId01, proj01_tokenCount01 = "100";
        String proj01_batchId02, proj01_tokenCount02 = "1,000";
        String proj01_batchId03, proj01_tokenCount03 = "7";
        String proj02_batchId01, proj02_tokenCount01 = "100";

        List<String> proj01_tokensToAssignBatch01 = new ArrayList<>();
        List<String> proj01_tokensNotAssignBatch01 = new ArrayList<>();
        List<String> proj01_tokensToAssignBatch02 = new ArrayList<>();
        List<String> proj01_tokensToAssignBatch03 = new ArrayList<>();
        List<String> proj02_tokensToAssignBatch01 = new ArrayList<>();

        _containerHelper.createProject(PROJECT_NAME04, "Mobile App Study");
        goToProjectHome(PROJECT_NAME04);

        SetupPage setupPage = new SetupPage(this);

        log("Set a study name.");
        setupPage.getStudySetupWebPart().setShortName(PROJECT01_STUDY_NAME)
                .clickSubmit();

        log("Create " + proj01_tokenCount01 + " tokens.");
        TokenBatchPopup tokenBatchPopup = setupPage.getTokenBatchesWebPart().openNewBatchPopup();
        TokenListPage tokenListPage = tokenBatchPopup.createNewBatch(proj01_tokenCount01);

        proj01_batchId01 = tokenListPage.getBatchId();
        log("First batch id: " + proj01_batchId01);

        proj01_tokensToAssignBatch01.add(tokenListPage.getToken(0));
        proj01_tokensToAssignBatch01.add(tokenListPage.getToken(1));
        proj01_tokensToAssignBatch01.add(tokenListPage.getToken(2));
        proj01_tokensToAssignBatch01.add(tokenListPage.getToken(3));
        proj01_tokensToAssignBatch01.add(tokenListPage.getToken(4));
        proj01_tokensToAssignBatch01.add(tokenListPage.getToken(5));
        proj01_tokensToAssignBatch01.add(tokenListPage.getToken(6));

        // Put aside a few tokens to be used later.
        proj01_tokensNotAssignBatch01.add(proj01_tokensToAssignBatch01.get(6));
        proj01_tokensNotAssignBatch01.add(proj01_tokensToAssignBatch01.get(5));
        proj01_tokensNotAssignBatch01.add(proj01_tokensToAssignBatch01.get(4));

        // Remove those tokens from this list.
        proj01_tokensToAssignBatch01.remove(6);
        proj01_tokensToAssignBatch01.remove(5);
        proj01_tokensToAssignBatch01.remove(4);

        log("Go back to the setup page.");
        goToProjectHome(PROJECT_NAME04);

        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, proj01_batchId01, proj01_tokenCount01, "0");

        log("Now create a second batch of tokens");

        log("Create " + proj01_tokenCount02 + " tokens.");
        tokenBatchPopup = setupPage.getTokenBatchesWebPart().openNewBatchPopup();
        tokenListPage = tokenBatchPopup.createNewBatch(proj01_tokenCount02);

        proj01_batchId02 = tokenListPage.getBatchId();
        log("Second batch id: " + proj01_batchId02);

        proj01_tokensToAssignBatch02.add(tokenListPage.getToken(0));
        proj01_tokensToAssignBatch02.add(tokenListPage.getToken(1));
        proj01_tokensToAssignBatch02.add(tokenListPage.getToken(2));

        log("Go back to the setup page.");
        goToProjectHome(PROJECT_NAME04);

        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, proj01_batchId02, proj01_tokenCount02, "0");

        log("Now assign some of the tokens from the first batch.");
        assignTokens(proj01_tokensToAssignBatch01, PROJECT_NAME04, PROJECT01_STUDY_NAME);

        log("Looks like the assignment for the first batch worked, now go check that the data is reflected in the setup page.");

        goToProjectHome(PROJECT_NAME04);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, proj01_batchId01, proj01_tokenCount01, Integer.toString(proj01_tokensToAssignBatch01.size()));

        log("Now assign some of the tokens from the second batch.");
        assignTokens(proj01_tokensToAssignBatch02, PROJECT_NAME04, PROJECT01_STUDY_NAME);

        log("Looks like the assignment for the second batch worked, now go check that the data is reflected in the setup page.");

        goToProjectHome(PROJECT_NAME04);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, proj01_batchId02, proj01_tokenCount02, Integer.toString(proj01_tokensToAssignBatch02.size()));

        log("Finally create a third batch of tokens");

        log("Create " + proj01_tokenCount03 + " tokens.");
        tokenBatchPopup = setupPage.getTokenBatchesWebPart().openNewBatchPopup();
        tokenBatchPopup.selectOtherBatchSize();
        tokenBatchPopup.setOtherBatchSize(proj01_tokenCount03);
        tokenListPage = tokenBatchPopup.createNewBatch();

        proj01_batchId03 = tokenListPage.getBatchId();
        log("Third batch id: " + proj01_batchId03);

        proj01_tokensToAssignBatch03.add(tokenListPage.getToken(0));

        log("Go back to the setup page.");
        goToProjectHome(PROJECT_NAME04);

        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, proj01_batchId03, proj01_tokenCount03, "0");

        log("Now assign some of the tokens from the third batch.");
        assignTokens(proj01_tokensToAssignBatch03, PROJECT_NAME04, PROJECT01_STUDY_NAME);

        log("Looks like the assignment for the third batch worked, now go check that the data is reflected in the setup page.");

        goToProjectHome(PROJECT_NAME04);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, proj01_batchId03, proj01_tokenCount03, Integer.toString(proj01_tokensToAssignBatch03.size()));

        log("Now create a second project and validate that tokens from the first project can't be assigned to this project.");
        _containerHelper.createProject(PROJECT_NAME05, "Mobile App Study");
        goToProjectHome(PROJECT_NAME05);

        setupPage = new SetupPage(this);

        log("Set a study name.");
        setupPage.getStudySetupWebPart().setShortName(PROJECT02_STUDY_NAME)
                .clickSubmit();

        log("Create " + proj02_tokenCount01 + " tokens.");
        tokenBatchPopup = setupPage.getTokenBatchesWebPart().openNewBatchPopup();
        tokenListPage = tokenBatchPopup.createNewBatch(proj02_tokenCount01);

        proj02_batchId01 = tokenListPage.getBatchId();
        log("First batch id: " + proj02_batchId01);

        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(0));
        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(1));
        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(2));
        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(3));
        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(4));

        log("Go back to the setup page.");
        goToProjectHome(PROJECT_NAME05);

        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, proj02_batchId01, proj02_tokenCount01, "0");

        log("Now assign some of the tokens from the second project.");
        assignTokens(proj02_tokensToAssignBatch01, PROJECT_NAME05, PROJECT02_STUDY_NAME);

        log("Looks like the assignment for the the second project worked, now go check that the data is reflected in the setup page.");

        goToProjectHome(PROJECT_NAME05);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, proj02_batchId01, proj02_tokenCount01, Integer.toString(proj02_tokensToAssignBatch01.size()));

        log("Now the real fun begins....");
        String failureMessage;

        log("Give data that can not be parsed.");
        String invalidToken = proj01_tokensNotAssignBatch01.get(1) + 1;
        failureMessage = assignTokenAndFail(invalidToken, PROJECT_NAME04, PROJECT01_STUDY_NAME);
        assertEquals("Wrong enrollment error.", "Invalid token: '" + invalidToken + "'", failureMessage);

        log("Do not provide a study name (but have a valid token).");
        failureMessage = assignTokenAndFail(proj01_tokensNotAssignBatch01.get(0), PROJECT_NAME04, "");
        assertEquals("Wrong enrollment error.", EnrollmentTokenValidationCommand.BLANK_STUDYID, failureMessage);

        log("Provide a study name that doesn't exists (but have a valid token).");
        failureMessage = assignTokenAndFail(proj01_tokensNotAssignBatch01.get(0), PROJECT_NAME04, "THIS_STUDY_IS_NOT_HERE");
        assertEquals("Wrong enrollment error.", "Study with StudyId 'THIS_STUDY_IS_NOT_HERE' does not exist", failureMessage);

        log("Try to assign a token that has already been assigned.");
        failureMessage = assignTokenAndFail(proj01_tokensToAssignBatch03.get(0), PROJECT_NAME04, PROJECT01_STUDY_NAME);
        assertEquals("Wrong enrollment error.", "Token already in use", failureMessage);

        log("Try to assign tokens from Project01 to a study in Project02.");
        failureMessage = assignTokenAndFail(proj01_tokensToAssignBatch01.get(0), PROJECT_NAME05, PROJECT02_STUDY_NAME);
        assertEquals("Wrong enrollment error.", "Unknown token: '" + proj01_tokensToAssignBatch01.get(0) + "'", failureMessage);

        log("Try to assign tokens from Project02 to a study in Project01.");
        failureMessage = assignTokenAndFail(proj02_tokensToAssignBatch01.get(3), PROJECT_NAME04, PROJECT01_STUDY_NAME);
        assertEquals("Wrong enrollment error.", "Unknown token: '" + proj02_tokensToAssignBatch01.get(3) + "'", failureMessage);

        log("Invalidate the checksum of a valid token.");
        invalidToken = proj01_tokensNotAssignBatch01.get(1);
        log("Token before changing checksum: " + invalidToken);
        char checkSum = invalidToken.charAt(invalidToken.length()-1);
        int intVal = checkSum;
        if(checkSum != 'Z')
            intVal = intVal + 1;
        else
            intVal = intVal - 1;
        checkSum = (char)intVal;
        invalidToken = invalidToken.substring(0, invalidToken.length()-1) + checkSum;
        log("Token after changing checksum: " + invalidToken);
        failureMessage = assignTokenAndFail(invalidToken, PROJECT_NAME04, PROJECT01_STUDY_NAME);
        assertEquals("Wrong enrollment error.", "Invalid token: '" + invalidToken + "'", failureMessage);

        log("Provide a token but the wrong (valid) study name.");
        failureMessage = assignTokenAndFail(proj01_tokensNotAssignBatch01.get(0), PROJECT_NAME05, PROJECT02_STUDY_NAME);
        assertEquals("Wrong enrollment error.", "Unknown token: '" + proj01_tokensNotAssignBatch01.get(0) + "'", failureMessage);

        log("Provide a valid token but no study name.");
        failureMessage = assignTokenAndFail(proj01_tokensNotAssignBatch01.get(2), PROJECT_NAME04, "");
        assertEquals("Wrong enrollment error.", EnrollmentTokenValidationCommand.BLANK_STUDYID, failureMessage);
    }

    @LogMethod
    private String assignTokenAndFail(@LoggedParam String token, @LoggedParam String projectName, @LoggedParam String studyName)
    {
        Connection connection = createDefaultConnection(false);
        try
        {
            CommandResponse response = assignToken(connection, token, projectName, studyName);
            throw new AssertionError("Token assignment should have failed: " + response.getText());
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to assign token", e);
        }
        catch (CommandException e)
        {
            String errorMessage = (String) e.getProperties().get("exception");
            log("Expected error: " + errorMessage);
            return errorMessage;
        }
    }

    private void validateGridInfo(SetupPage setupPage, String batchId, String expectedTokenCount, String expectedUsedCount)
    {
        Map<String, String> batchData = setupPage.getTokenBatchesWebPart().getBatchData(batchId);

        assertEquals("BatchId not as expected.", batchId, batchData.get("RowId"));
        assertEquals("Expected number of tokens not created.", expectedTokenCount, batchData.get("Count"));
        assertEquals("Number of tokens in use not as expected.", expectedUsedCount, batchData.get("TokensInUse"));
    }
}
