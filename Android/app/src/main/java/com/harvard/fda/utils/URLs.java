/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.fda.utils;


public class URLs {


    public static final String BASE_URL_WCP_SERVER = "Your WCP server URL";
    public static final String BASE_URL_REGISTRATION_SERVER = "Your Registration server URL";
    public static final String BASE_URL_RESPONSE_SERVER = "Your Response server URL";


    /**
     * Registration Server
     */
    public static String LOGIN = "login.api";
    public static String REGISTER_USER = "register.api";
    public static String RESEND_CONFIRMATION = "resendConfirmation.api";
    public static String CONFIRM_REGISTER_USER = "verify.api";
    public static String FORGOT_PASSWORD = "forgotPassword.api";
    public static String CHANGE_PASSWORD = "changePassword.api";
    public static String GET_USER_PROFILE = "userProfile.api";
    public static String UPDATE_USER_PROFILE = "updateUserProfile.api";
    public static String UPDATE_STUDY_PREFERENCE = "updateStudyState.api";
    public static String UPDATE_ACTIVITY_PREFERENCE = "updateActivityState.api";
    public static String LOGOUT = "logout.api";
    public static String CONSENT_METADATA = "eligibilityConsent";
    public static String DELETE_ACCOUNT = "deactivate.api";
    public static String ACTIVITY = "activity";
    public static String STUDY_STATE = "studyState.api";
    public static String ACTIVITY_STATE = "activityState.api";
    public static String WITHDRAW = "withdraw.api";
    public static String UPDATE_ELIGIBILITY_CONSENT = "updateEligibilityConsentStatus.api";
    public static String REFRESH_TOKEN = BASE_URL_REGISTRATION_SERVER + "refreshToken.api";
    public static String CONSENTPDF = "consentPDF.api";
    public static String VERSION_CHECKER = "versionInfo.api";
    /**
     * WCP server
     */
    public static String STUDY_INFO = "studyInfo";
    public static String STUDY_LIST = "studyList";
    public static String SPECIFIC_STUDY = "study";
    public static String STUDY_UPDATES = "studyUpdates";
    public static String ACTIVITY_LIST = "activityList";
    public static String CONTACT_US = "contactUs";
    public static String FEEDBACK = "feedback";
    public static String RESOURCE_LIST = "resources";
    public static String NOTIFICATIONS = "notifications";
    public static String DASHBOARD_INFO = "studyDashboard";
    public static String GET_TERMS_AND_CONDITION = "termsPolicy";
    public static String GET_CONSENT_DOC = "consentDocument";
    /**
     * Response server
     */

    public static String VALIDATE_ENROLLMENT_ID = "mobileappstudy-validateenrollmenttoken.api?";
    public static String ENROLL_ID = "mobileappstudy-enroll.api?";
    public static String PROCESS_RESPONSE = "mobileappstudy-processResponse.api";
    public static String WITHDRAWFROMSTUDY = "withdrawFromStudy";
    public static String PROCESSRESPONSEDATA = BASE_URL_RESPONSE_SERVER + "mobileappstudy-executeSQL.api?";
}