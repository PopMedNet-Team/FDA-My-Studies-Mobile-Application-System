/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.utils;

public class URLs {


    public static final String BASE_URL_WCP_SERVER = "https://hphci-fdama-st-wcp-01.labkey.com/StudyMetaData/";
    public static final String BASE_URL_REGISTRATION_SERVER = "https://hphci-fdama-st-ur-01.labkey.com/fdahpUserRegWS/";
    public static final String BASE_URL_RESPONSE_SERVER = "https://hphci-fdama-st-ds-01.labkey.com/";

    /**
     * Registration Server
     */
    public static final String LOGIN = "login.api";
    public static final String REGISTER_USER = "register.api";
    public static final String RESEND_CONFIRMATION = "resendConfirmation.api";
    public static final String CONFIRM_REGISTER_USER = "verify.api";
    public static final String FORGOT_PASS = "forgotPassword.api";
    public static final String CHANGE_PASS = "changePassword.api";
    public static final String GET_USER_PROFILE = "userProfile.api";
    public static final String UPDATE_USER_PROFILE = "updateUserProfile.api";
    public static final String UPDATE_STUDY_PREFERENCE = "updateStudyState.api";
    public static final String UPDATE_ACTIVITY_PREFERENCE = "updateActivityState.api";
    public static final String LOGOUT = "logout.api";
    public static final String CONSENT_METADATA = "eligibilityConsent";
    public static final String DELETE_ACCOUNT = "deactivate.api";
    public static final String ACTIVITY = "activity";
    public static final String STUDY_STATE = "studyState.api";
    public static final String ACTIVITY_STATE = "activityState.api";
    public static final String WITHDRAW = "withdraw.api";
    public static final String UPDATE_ELIGIBILITY_CONSENT = "updateEligibilityConsentStatus.api";
    public static final String REFRESH_TOKEN = BASE_URL_REGISTRATION_SERVER + "refreshToken.api";
    public static final String CONSENTPDF = "consentPDF.api";
    /**
     * WCP server
     */
    public static final String STUDY_INFO = "studyInfo";
    public static final String STUDY_LIST = "studyList";
    public static final String STUDY_UPDATES = "studyUpdates";
    public static final String ACTIVITY_LIST = "activityList";
    public static final String CONTACT_US = "contactUs";
    public static final String FEEDBACK = "feedback";
    public static final String RESOURCE_LIST = "resources";
    public static final String NOTIFICATIONS = "notifications";
    public static final String DASHBOARD_INFO = "studyDashboard";
    public static final String GET_CONSENT_DOC = "consentDocument";
    public static final String GET_TERMS_AND_CONDITION = "termsPolicy";
    /**
     * Response server
     */

    public static final String VALIDATE_ENROLLMENT_ID = "mobileappstudy-validateenrollmenttoken.api?";
    public static final String ENROLL_ID = "mobileappstudy-enroll.api?";
    public static final String PROCESS_RESPONSE = "mobileappstudy-processResponse.api";
    public static final String WITHDRAWFROMSTUDY = "withdrawFromStudy";
    public static final String PROCESSRESPONSEDATA = BASE_URL_RESPONSE_SERVER + "mobileappstudy-executeSQL.api?";
}