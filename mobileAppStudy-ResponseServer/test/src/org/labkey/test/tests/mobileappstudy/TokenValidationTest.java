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

import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.categories.Git;
import org.labkey.test.commands.mobileappstudy.EnrollmentTokenValidationCommand;
import org.labkey.test.components.mobileappstudy.TokenBatchPopup;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.pages.mobileappstudy.TokenListPage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test case to exercise the MobileAppStudy ValidateEnrollmentTokenAction
 */
@Category({Git.class})
public class TokenValidationTest extends BaseMobileAppStudyTest
{
    static final String PROJECT_NAME01 = "TokenValidationTest Project 1";
    static final String STUDY_NAME01 = "TOKENVALIDATION";

    static final String PROJECT_NAME02 = "TokenValidationTest Project 2";
    static final String STUDY_NAME02 = "BLANKTOKENVALIDATION";


    @Override
    void setupProjects()
    {
        _containerHelper.deleteProject(PROJECT_NAME01, false);
        _containerHelper.createProject(PROJECT_NAME01, "Mobile App Study");
        _containerHelper.deleteProject(PROJECT_NAME02, false);
        _containerHelper.createProject(PROJECT_NAME02, "Mobile App Study");

        goToProjectHome(PROJECT_NAME01);
        SetupPage setupPage = new SetupPage(this);
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME01);
        setupPage.validateSubmitButtonEnabled();
        setupPage.getStudySetupWebPart().clickSubmit();

        log("Create tokens.");
        TokenBatchPopup tokenBatchPopup = setupPage.getTokenBatchesWebPart().openNewBatchPopup();
        tokenBatchPopup.createNewBatch("100");

        goToProjectHome(PROJECT_NAME02);
        setupPage = new SetupPage(this);
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME02);
        setupPage.validateSubmitButtonEnabled();
        setupPage.getStudySetupWebPart().clickSubmit();
    }

    @Nullable
    @Override
    protected String getProjectName()
    {
        return null;
    }

    @Test
    public void testSuccess()
    {
        TokenListPage tokenListPage = TokenListPage.beginAt(this, PROJECT_NAME01);
        String token = tokenListPage.getToken(0);

        log("Token validation action: successful token request");
        EnrollmentTokenValidationCommand cmd = new EnrollmentTokenValidationCommand(PROJECT_NAME01, STUDY_NAME01, token, this::log);
        cmd.execute(200);
        assertTrue("Enrollment token validation failed when it shouldn't have", cmd.getSuccess());
    }

    @Test
    public void testInvalidStudy()
    {
        TokenListPage tokenListPage = TokenListPage.beginAt(this, PROJECT_NAME01);
        String token = tokenListPage.getToken(0);

        log("Token validation action: invalid StudyId");
        EnrollmentTokenValidationCommand cmd = new EnrollmentTokenValidationCommand(PROJECT_NAME01, STUDY_NAME01 + "_INVALIDSTUDYNAME", token, this::log);
        cmd.execute(400);
        assertFalse("Enrollment token validation succeeded with an invalid studyId", cmd.getSuccess());
        assertEquals("Unexpected error message", String.format(EnrollmentTokenValidationCommand.INVLAID_STUDYID_FORMAT, STUDY_NAME01 + "_INVALIDSTUDYNAME"), cmd.getExceptionMessage());
    }

    @Test
    public void testNoStudy()
    {
        TokenListPage tokenListPage = TokenListPage.beginAt(this, PROJECT_NAME01);
        String token = tokenListPage.getToken(0);

        log("Token validation action: no StudyId");
        EnrollmentTokenValidationCommand cmd = new EnrollmentTokenValidationCommand(PROJECT_NAME01, null, token, this::log);
        cmd.execute(400);
        assertFalse("Enrollment token validation succeeded without the studyId", cmd.getSuccess());
        assertEquals("Unexpected error message", EnrollmentTokenValidationCommand.BLANK_STUDYID, cmd.getExceptionMessage());
    }

    @Test
    public void testInvalidToken()
    {
        TokenListPage tokenListPage = TokenListPage.beginAt(this, PROJECT_NAME01);
        String token = tokenListPage.getToken(0);

        log("Token validation action: Invalid Token");
        EnrollmentTokenValidationCommand cmd = new EnrollmentTokenValidationCommand(PROJECT_NAME01, STUDY_NAME01, token + "thisIsAnInvalidToken", this::log);
        cmd.execute(400);
        assertFalse("Enrollment token validation succeeded when it shouldn't have", cmd.getSuccess());
        assertEquals("Unexpected error message", String.format(EnrollmentTokenValidationCommand.INVALID_TOKEN_FORMAT, token + "thisIsAnInvalidToken".toUpperCase()), cmd.getExceptionMessage());
    }

    @Test
    public void testBlankTokenWBatch()
    {
        log("Token validation action: Invalid Blank Token");
        EnrollmentTokenValidationCommand cmd = new EnrollmentTokenValidationCommand(PROJECT_NAME01, STUDY_NAME01, null, this::log);
        cmd.execute(400);
        assertFalse("Enrollment token validation succeeded when it shouldn't have", cmd.getSuccess());
        assertEquals("Unexpected error message", EnrollmentTokenValidationCommand.TOKEN_REQUIRED, cmd.getExceptionMessage());
    }

    @Test
    public void testBlankTokenNoBatch()
    {
        //Note: This uses the secondary project because once a batch is created blank tokens are no longer accepted
        log("Token validation action: successful blank token request");
        EnrollmentTokenValidationCommand cmd = new EnrollmentTokenValidationCommand(PROJECT_NAME02, STUDY_NAME02, null, this::log);
        cmd.execute(200);
        assertTrue("Blank Enrollment token validation failed when it shouldn't have", cmd.getSuccess());
    }
}
