/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.tests.mobileappstudy;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.Locator;
import org.labkey.test.WebTestHelper;
import org.labkey.test.categories.Git;
import org.labkey.test.commands.mobileappstudy.EnrollmentTokenValidationCommand;
import org.labkey.test.components.mobileappstudy.TokenBatchPopup;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.pages.mobileappstudy.TokenListPage;
import org.labkey.test.util.PortalHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({Git.class})
public class ConfigAndEnrollTest extends BaseMobileAppStudyTest
{
    protected final PortalHelper _portalHelper = new PortalHelper(this);

    @Override
    protected String getProjectName()
    {
        return "ConfigAndEnrollTest Project";
    }

    @Test
    public void testStudyName()
    {
        final String PROMPT_NOT_ASSIGNED = "Enter the StudyId to be associated with this folder. The StudyId should be the same as it appears in the study design interface.";
        final String PROMPT_ASSIGNED = "The StudyId associated with this folder is $STUDY_NAME$.";
        final String STUDY_NAME01 = "StudyName01";  // Study names are case insensitive, so test it once.
        final String STUDY_NAME02 = "STUDYNAME02";
        final String REUSED_STUDY_NAME_ERROR = "There were problems storing the configuration. StudyId '$STUDY_NAME$' is already associated with a different container within this folder. Each study can be associated with only one container per folder.";
        final String PROJECT_NAME01 = getProjectName() + " TestStudyName01";
        final String PROJECT_NAME02 = getProjectName() + " TestStudyName02";

        String batchId, expectedTokenCount = "100";
        List<String> tokensToAssign = new ArrayList<>();
        SetupPage setupPage;

        _containerHelper.deleteProject(PROJECT_NAME01, false);
        _containerHelper.deleteProject(PROJECT_NAME02, false);

        _containerHelper.createProject(PROJECT_NAME01, "Mobile App Study");
        goToProjectHome(PROJECT_NAME01);

        setupPage = new SetupPage(this);

        log("Validate the prompt.");
        assertEquals("The prompt is not as expected.", PROMPT_NOT_ASSIGNED, setupPage.studySetupWebPart.getPrompt());
        setupPage.validateSubmitButtonDisabled();

        log("Set a study name.");
        setupPage.studySetupWebPart.setShortName(STUDY_NAME01);
        setupPage.validateSubmitButtonEnabled();
        setupPage.studySetupWebPart.clickSubmit();

        log("Validate that the submit button is disabled after you click it.");
        assertFalse("Submit button is showing as enabled, it should not be.", setupPage.studySetupWebPart.isSubmitEnabled());

        log("Remove the web part,bring it back and validate the study name is still there.");
        setupPage.studySetupWebPart.delete();

        _portalHelper.addWebPart("Mobile App Study Setup");

        log("Validate that the Study Short Name field is still set.");
        assertEquals("Study name did not persist after removing the web part.", STUDY_NAME01.toUpperCase(), setupPage.studySetupWebPart.getShortName());
        setupPage.validateSubmitButtonDisabled();

        log("Change the study name and submit.");
        setupPage.studySetupWebPart.setShortName(STUDY_NAME02);
        setupPage.validateSubmitButtonEnabled();
        setupPage.studySetupWebPart.clickSubmit();

        log("Create a new project and try to reuse the study name.");
        _containerHelper.createProject(PROJECT_NAME02, "Mobile App Study");
        goToProjectHome(PROJECT_NAME02);

        setupPage = new SetupPage(this);

        log("Set the study name to a value already saved.");
        setupPage.studySetupWebPart.setShortName(STUDY_NAME02);

        setupPage.studySetupWebPart.clickSubmit();

        // These all fail to find the alert.
//        assertAlert(REUSED_STUDY_NAME_ERROR.replace("$STUDY_NAME$", STUDY_NAME02));
//        acceptAlert();
//        waitForAlert(REUSED_STUDY_NAME_ERROR.replace("$STUDY_NAME$", STUDY_NAME02), 5000);

        // So I did it the hard way.
        waitForElement(Locator.css("div.x4-message-box"));
        assertEquals("Error message text does not match", REUSED_STUDY_NAME_ERROR.replace("$STUDY_NAME$", STUDY_NAME02), getText(Locator.css("div.x4-message-box div.x4-form-display-field")));
        clickButton("OK", 0);

        log("Reuse the first study name");

        setupPage.studySetupWebPart.setShortName(STUDY_NAME01);

        setupPage.studySetupWebPart.clickSubmit();

        log("Now create some tokens and use them and then validate that the study name cannot be changed.");

        log("Create " + expectedTokenCount + " tokens.");
        TokenBatchPopup tokenBatchPopup = setupPage.tokenBatchesWebPart.openNewBatchPopup();
        TokenListPage tokenListPage = tokenBatchPopup.createNewBatch(expectedTokenCount);

        batchId = tokenListPage.getBatchId();
        log("First batch id: " + batchId);

        tokensToAssign.add(tokenListPage.getToken(0));
        tokensToAssign.add(tokenListPage.getToken(1));
        tokensToAssign.add(tokenListPage.getToken(2));

        log("Go back to the setup page.");
        clickTab("Setup");

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
        assertEquals("The prompt is not as expected.", PROMPT_ASSIGNED.replace("$STUDY_NAME$", STUDY_NAME01.toUpperCase()), setupPage.studySetupWebPart.getPrompt());
        assertFalse("The short name field is visible and it should not be.", setupPage.studySetupWebPart.isShortNameVisible());

        log("Prompt was as expected. Go home.");
        goToHome();
    }

    @Test
    public void testStudyNameShared()
    {
        final String PROJECT_NAME01 = getProjectName() + " DataPartner1";
        final String PROJECT_NAME02 = getProjectName() + " DataPartner2";
        final String STUDY_FOLDER_NAME = "StudyFolder";
        final String SHORT_NAME = "Shared";

        _containerHelper.deleteProject(PROJECT_NAME01, false);
        _containerHelper.deleteProject(PROJECT_NAME02, false);

        _containerHelper.createProject(PROJECT_NAME01, "Collaboration");
        _containerHelper.createSubfolder(PROJECT_NAME01, STUDY_FOLDER_NAME,"Mobile App Study");

        SetupPage setupPage = new SetupPage(this);
        setupPage.studySetupWebPart.setShortName(SHORT_NAME);
        setupPage.studySetupWebPart.clickSubmit();

        _containerHelper.createProject(PROJECT_NAME02, "Collaboration");
        _containerHelper.createSubfolder(PROJECT_NAME02, STUDY_FOLDER_NAME, "Mobile App Study");

        setupPage.studySetupWebPart.setShortName(SHORT_NAME);
        setupPage.studySetupWebPart.clickSubmit();
        goToProjectHome(PROJECT_NAME02);
        clickFolder(STUDY_FOLDER_NAME);
        assertEquals("Study name not saved for second project", SHORT_NAME.toUpperCase(), setupPage.studySetupWebPart.getShortName());
    }

    @Test
    public void testCollectionToggling()
    {
        final String STUDY_NAME01 = "STUDYNAME01";
        final String PROJECT_NAME01 = getProjectName() + " TestStudyName01";

        _containerHelper.deleteProject(PROJECT_NAME01, false);

        _containerHelper.createProject(PROJECT_NAME01, "Mobile App Study");

        goToProjectHome(PROJECT_NAME01);
        SetupPage setupPage = new SetupPage(this);

        //Validate collection checkbox behavior
        log("Collection is initially disabled");
        assertFalse("Response collection is enabled at study creation", setupPage.studySetupWebPart.isResponseCollectionChecked());

        log("Enabling response collection doesn't allow submit prior to a valid study name");
        setupPage.studySetupWebPart.checkResponseCollection();
        setupPage.validateSubmitButtonDisabled();

        log("Set a study name.");
        setupPage.studySetupWebPart.setShortName(STUDY_NAME01);
        setupPage.validateSubmitButtonEnabled();

        log("Disabling response collection allows study config submission");
        setupPage.studySetupWebPart.uncheckResponseCollection();
        setupPage.validateSubmitButtonEnabled();
        setupPage.studySetupWebPart.clickSubmit();
    }

    @Test
    public void testEnroll()
    {
        final String PROJECT_NAME01 = getProjectName() + " TestEnroll01";
        final String PROJECT_NAME02 = getProjectName() + " TestEnroll02";
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

        _containerHelper.deleteProject(PROJECT_NAME01, false);
        _containerHelper.deleteProject(PROJECT_NAME02, false);

        _containerHelper.createProject(PROJECT_NAME01, "Mobile App Study");
        goToProjectHome(PROJECT_NAME01);

        SetupPage setupPage = new SetupPage(this);

        log("Set a study name.");
        setupPage.studySetupWebPart.setShortName(PROJECT01_STUDY_NAME)
                .clickSubmit();

        log("Create " + proj01_tokenCount01 + " tokens.");
        TokenBatchPopup tokenBatchPopup = setupPage.tokenBatchesWebPart.openNewBatchPopup();
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
        clickTab("Setup");

        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, proj01_batchId01, proj01_tokenCount01, "0");

        log("Now create a second batch of tokens");

        log("Create " + proj01_tokenCount02 + " tokens.");
        tokenBatchPopup = setupPage.tokenBatchesWebPart.openNewBatchPopup();
        tokenListPage = tokenBatchPopup.createNewBatch(proj01_tokenCount02);

        proj01_batchId02 = tokenListPage.getBatchId();
        log("Second batch id: " + proj01_batchId02);

        proj01_tokensToAssignBatch02.add(tokenListPage.getToken(0));
        proj01_tokensToAssignBatch02.add(tokenListPage.getToken(1));
        proj01_tokensToAssignBatch02.add(tokenListPage.getToken(2));

        log("Go back to the setup page.");
        clickTab("Setup");

        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, proj01_batchId02, proj01_tokenCount02, "0");

        log("Now assign some of the tokens from the first batch.");
        assignTokens(proj01_tokensToAssignBatch01, PROJECT_NAME01, PROJECT01_STUDY_NAME);

        log("Looks like the assignment for the first batch worked, now go check that the data is reflected in the setup page.");

        goToProjectHome(PROJECT_NAME01);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, proj01_batchId01, proj01_tokenCount01, Integer.toString(proj01_tokensToAssignBatch01.size()));

        log("Now assign some of the tokens from the second batch.");
        assignTokens(proj01_tokensToAssignBatch02, PROJECT_NAME01, PROJECT01_STUDY_NAME);

        log("Looks like the assignment for the second batch worked, now go check that the data is reflected in the setup page.");

        goToProjectHome(PROJECT_NAME01);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, proj01_batchId02, proj01_tokenCount02, Integer.toString(proj01_tokensToAssignBatch02.size()));

        log("Finally create a third batch of tokens");

        log("Create " + proj01_tokenCount03 + " tokens.");
        tokenBatchPopup = setupPage.tokenBatchesWebPart.openNewBatchPopup();
        tokenBatchPopup.selectOtherBatchSize();
        tokenBatchPopup.setOtherBatchSize(proj01_tokenCount03);
        tokenListPage = tokenBatchPopup.createNewBatch();

        proj01_batchId03 = tokenListPage.getBatchId();
        log("Third batch id: " + proj01_batchId03);

        proj01_tokensToAssignBatch03.add(tokenListPage.getToken(0));

        log("Go back to the setup page.");
        clickTab("Setup");

        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, proj01_batchId03, proj01_tokenCount03, "0");

        log("Now assign some of the tokens from the third batch.");
        assignTokens(proj01_tokensToAssignBatch03, PROJECT_NAME01, PROJECT01_STUDY_NAME);

        log("Looks like the assignment for the third batch worked, now go check that the data is reflected in the setup page.");

        goToProjectHome(PROJECT_NAME01);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, proj01_batchId03, proj01_tokenCount03, Integer.toString(proj01_tokensToAssignBatch03.size()));

        log("Now create a second project and validate that tokens from the first project can't be assigned to this project.");
        _containerHelper.createProject(PROJECT_NAME02, "Mobile App Study");
        goToProjectHome(PROJECT_NAME02);

        setupPage = new SetupPage(this);

        log("Set a study name.");
        setupPage.studySetupWebPart.setShortName(PROJECT02_STUDY_NAME)
                .clickSubmit();

        log("Create " + proj02_tokenCount01 + " tokens.");
        tokenBatchPopup = setupPage.tokenBatchesWebPart.openNewBatchPopup();
        tokenListPage = tokenBatchPopup.createNewBatch(proj02_tokenCount01);

        proj02_batchId01 = tokenListPage.getBatchId();
        log("First batch id: " + proj02_batchId01);

        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(0));
        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(1));
        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(2));
        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(3));
        proj02_tokensToAssignBatch01.add(tokenListPage.getToken(4));

        log("Go back to the setup page.");
        clickTab("Setup");

        log("Validate that the correct info for the tokens is shown in the grid.");
        validateGridInfo(setupPage, proj02_batchId01, proj02_tokenCount01, "0");

        log("Now assign some of the tokens from the second project.");
        assignTokens(proj02_tokensToAssignBatch01, PROJECT_NAME02, PROJECT02_STUDY_NAME);

        log("Looks like the assignment for the the second project worked, now go check that the data is reflected in the setup page.");

        goToProjectHome(PROJECT_NAME02);

        // Get a new setupPage.
        setupPage = new SetupPage(this);
        validateGridInfo(setupPage, proj02_batchId01, proj02_tokenCount01, Integer.toString(proj02_tokensToAssignBatch01.size()));

        log("Now the real fun begins....");
        String failurePage;

        log("Give data that can not be parsed.");
        String invalidToken = proj01_tokensNotAssignBatch01.get(1) + 1;
        failurePage = assignTokenAndFail(invalidToken, PROJECT_NAME01, PROJECT01_STUDY_NAME);
        assertTrue("Json result did not contain error message: \"Invalid token: '" + invalidToken + "'\"", failurePage.contains("Invalid token: '" + invalidToken + "'"));

        log("Do not provide a study name (but have a valid token).");
        failurePage = assignTokenAndFail(proj01_tokensNotAssignBatch01.get(0), PROJECT_NAME01, "");
        assertTrue("Json result did not contain \"success\" : false", failurePage.contains("\"success\" : false"));
        assertTrue("Json result did not contain error msg \"" + EnrollmentTokenValidationCommand.BLANK_STUDYID +"\".", failurePage.contains(EnrollmentTokenValidationCommand.BLANK_STUDYID));

        log("Provide a study name that doesn't exists (but have a valid token).");
        failurePage = assignTokenAndFail(proj01_tokensNotAssignBatch01.get(0), PROJECT_NAME01, "THIS_STUDY_IS_NOT_HERE");
        assertTrue("Json result did not contain \"success\" : false", failurePage.contains("\"success\" : false"));
        assertTrue("Json result did not contain error message: \"Study with StudyId 'THIS_STUDY_IS_NOT_HERE' does not exist\"", failurePage.contains("StudyId 'THIS_STUDY_IS_NOT_HERE' does not exist"));

        log("Try to assign a token that has already been assigned.");
        failurePage = assignTokenAndFail(proj01_tokensToAssignBatch03.get(0), PROJECT_NAME01, PROJECT01_STUDY_NAME);
        assertTrue("Json result did not contain \"success\" : false", failurePage.contains("\"success\" : false"));
        assertTrue("Json did not contain error message: \"Token already in use\"", failurePage.contains("Token already in use"));

        log("Try to assign tokens from Project01 to a study in Project02.");
        failurePage = assignTokenAndFail(proj01_tokensToAssignBatch01.get(0), PROJECT_NAME02, PROJECT02_STUDY_NAME);
        assertTrue("Json result did not contain \"success\" : false", failurePage.contains("\"success\" : false"));
        assertTrue("Json result did not which token was in error.", failurePage.contains("Unknown token: '" + proj01_tokensToAssignBatch01.get(0) + "'"));

        log("Try to assign tokens from Project02 to a study in Project01.");
        failurePage = assignTokenAndFail(proj02_tokensToAssignBatch01.get(3), PROJECT_NAME01, PROJECT01_STUDY_NAME);
        assertTrue("Json result did not contain \"success\" : false", failurePage.contains("\"success\" : false"));
        assertTrue("Json result did not which token was in error.", failurePage.contains("Unknown token: '" + proj02_tokensToAssignBatch01.get(3) + "'"));

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
        failurePage = assignTokenAndFail(invalidToken, PROJECT_NAME01, PROJECT01_STUDY_NAME);
        assertTrue("Json result did not contain \"success\" : false", failurePage.contains("\"success\" : false"));
        assertTrue("Json result did not contain error message: \"Invalid token: '" + invalidToken + "'\".", failurePage.contains("Invalid token: '" + invalidToken + "'"));

        log("Provide a token but the wrong (valid) study name.");
        failurePage = assignTokenAndFail(proj01_tokensNotAssignBatch01.get(0), PROJECT_NAME02, PROJECT02_STUDY_NAME);
        assertTrue("Json result did not contain \"success\" : false", failurePage.contains("\"success\" : false"));
        assertTrue("Json result did not contain error: \"Unknown token: '" + proj01_tokensNotAssignBatch01.get(0) + "'", failurePage.contains("Unknown token: '" + proj01_tokensNotAssignBatch01.get(0) + "'"));

        log("Provide a valid token but no study name.");
        failurePage = assignTokenAndFail(proj01_tokensNotAssignBatch01.get(2), PROJECT_NAME01, "");
        assertTrue("Json result did not contain \"success\" : false", failurePage.contains("\"success\" : false"));
        assertTrue("Json result did not contain error: \"" + EnrollmentTokenValidationCommand.BLANK_STUDYID + "\".", failurePage.contains(EnrollmentTokenValidationCommand.BLANK_STUDYID));

        log("Looks good. Go home.");
        goToHome();
    }

    private String assignTokenAndFail(String tokenToAssign, String projectName, String studyName)
    {
        final String API_STRING = WebTestHelper.getBaseURL() + "/mobileappstudy/$PROJECT_NAME$/enroll.api?shortName=$STUDY_NAME$&token=";
        String apiUrl;

        apiUrl = API_STRING.replace("$PROJECT_NAME$", projectName).replace("$STUDY_NAME$", studyName) + tokenToAssign;
        log("Failure url is: " + apiUrl);
        beginAt(apiUrl);
        return getDriver().getPageSource();
    }

    private void validateGridInfo(SetupPage setupPage, String batchId, String expectedTokenCount, String expectedUsedCount)
    {
        Map<String, String> batchData = setupPage.tokenBatchesWebPart.getBatchData(batchId);

        assertEquals("BatchId not as expected.", batchId, batchData.get("RowId"));
        assertEquals("Expected number of tokens not created.", expectedTokenCount, batchData.get("Count"));
        assertEquals("Number of tokens in use not as expected.", expectedUsedCount, batchData.get("TokensInUse"));
    }
}
