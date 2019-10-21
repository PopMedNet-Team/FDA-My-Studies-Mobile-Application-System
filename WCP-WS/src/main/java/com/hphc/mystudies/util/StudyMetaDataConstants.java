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
package com.hphc.mystudies.util;

import java.text.SimpleDateFormat;

/**
 * Provides constants.
 * 
 * @author BTC
 *
 */
public interface StudyMetaDataConstants {

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String INACTIVE = "INACTIVE";

	public final String SESSION_EXPIRED_MSG = "Session expired.";
	public final String CONNECTION_ERROR_MSG = "Oops, something went wrong. Please try again after sometime.";
	public final String ACCOUNT_DEACTIVATE_ERROR_MSG = "Your account has been deactivated.";
	public final String INVALID_INPUT_ERROR_MSG = "Invalid input.";
	public final String LOGIN_FAILURE = "Please check your login credentials.";
	public final String INVALID_STUDY_ID = "Sorry, something went wrong. Please try again.";
	public final String INVALID_ACTIVITY_ID = "ActivityId does not exists. Please check";
	public final String INVALID_ENROLLMENT_TOKEN = "Sorry, the enrollment token is not valid. Please check and try again.";

	SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat SDF_DATE_TIME_12 = new SimpleDateFormat(
			"MM/dd/yyyy hh:mm a");
	SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat SDF_TIME_24 = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat SDF_TIME_12 = new SimpleDateFormat("hh:mm a");

	SimpleDateFormat SDF_MMDDYYYY = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat DISPLAY_DATE = new SimpleDateFormat("EEE, MMM dd, yyyy");
	SimpleDateFormat DISPLAY_DATE_TIME = new SimpleDateFormat(
			"EEE, MMM dd, yyyy 'at' hh:mm a");
	SimpleDateFormat DISPLAY_DATE_TIME_MIN = new SimpleDateFormat(
			"dd MMM yyyy  HH:mm:ss");
	SimpleDateFormat SDF_DATE_TIME_TIMEZONE = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");
	SimpleDateFormat SDF_DATE_TIME_TIMEZONE_MILLISECONDS = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	public static final String SDF_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String SDF_DATE_TIME_TIMEZONE_MILLISECONDS_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	public static final String SDF_DATE_PATTERN = "yyyy-MM-dd";
	
	public static final String PASS_SALT = "BTCSoft";

	public static final String STUDY_TYPE_GT = "GT";
	public static final String STUDY_TYPE_SD = "SD";
	public static final String STUDY_PLATFORM_TYPE_IOS = "I";
	public static final String STUDY_PLATFORM_TYPE_ANDROID = "A";

	public static final String STUDY_STATUS_PRE_LAUNCH = "Pre-launch";
	public static final String STUDY_STATUS_PRE_PUBLISH = "Pre-launch(Published)";
	public static final String STUDY_STATUS_ACTIVE = "Active";
	public static final String STUDY_STATUS_PUBLISH = "Published";
	public static final String STUDY_STATUS_PAUSED = "Paused";
	public static final String STUDY_STATUS_DEACTIVATED = "Deactivated";

	public static final String STUDY_ACTIVE = "Active";
	public static final String STUDY_PAUSED = "Paused";
	public static final String STUDY_UPCOMING = "Upcoming";
	public static final String STUDY_CLOSED = "Closed";

	public static final String STUDY_REF_CATEGORIES = "Categories";
	public static final String STUDY_REF_SPONSERS = "Research Sponsors";

	public static final String YES = "Yes";
	public static final String NO = "No";
	public static final String ALL = "All";

	public static final String INVALID_AUTHORIZATION = "Invalid Authorization key";
	public static final String INVALID_INPUT = "Invalid inputs";
	public static final String NO_RECORD = "No records found";

	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_HTML = "html";
	public static final String TYPE_PDF = "pdf";

	public static final String TYPE_TOKEN = "token";
	public static final String TYPE_BOTH = "combined";
	public static final String TYPE_TEST = "test";

	public static final String CONSENT_DOC_TYPE_NEW = "New";
	public static final String CONSENT_DOC_TYPE_AUTO = "Auto";
	public static final String REASON_FOR_CONSENT = "By agreeing to this you confirm that you read the consent and that you wish to take part in this research study.";

	public static final String ACTIVITY_ACTIVE_TASK = "task";
	public static final String ACTIVITY_QUESTIONNAIRE = "questionnaire";
	public static final String DASHBOARD_ACTIVE_TASK = "active task";
	public static final String DASHBOARD_QUESTIONNAIRE = "questionnaire";

	public static final String TYPE_UTF8 = "UTF-8";
	public static final String SDF_DAY = "EEEE";

	public static final String CONSENT_TYPE_RESEARCHKIT = "ResearchKit/ResearchStack";
	public static final String CONSENT_TYPE_CUSTOM = "Custom";

	public static final String QUESTIONAIRE_STEP_TYPE_INSTRUCTION = "Instruction";
	public static final String QUESTIONAIRE_STEP_TYPE_QUESTION = "Question";
	public static final String QUESTIONAIRE_STEP_TYPE_FORM = "Form";

	public static final String FREQUENCY_TYPE_ONE_TIME = "One time";
	public static final String FREQUENCY_TYPE_WITHIN_A_DAY = "Within a day";
	public static final String FREQUENCY_TYPE_DAILY = "Daily";
	public static final String FREQUENCY_TYPE_WEEKLY = "Weekly";
	public static final String FREQUENCY_TYPE_MONTHLY = "Monthly";
	public static final String FREQUENCY_TYPE_MANUALLY_SCHEDULE = "Manually Schedule";

	public static final String ACTIVITY_TYPE_ACTIVE_TASK = "AT";
	public static final String ACTIVITY_TYPE_QUESTIONAIRE = "QR";

	public static final String RESOURCE_AUDIENCE_TYPE_ALL = "All";
	public static final String RESOURCE_AUDIENCE_TYPE_LIMITED = "Limited";

	public static final String STUDY_SEQUENCE_Y = "Y";
	public static final String STUDY_SEQUENCE_N = "N";

	public static final String STUDY_PLATFORM_IOS = "ios";
	public static final String STUDY_PLATFORM_ANDROID = "android";
	public static final String STUDY_PLATFORM_BOTH = "both";
	public static final String STUDY_PLATFORM_ALL = "all";

	public static final String STUDY_WITHDRAW_CONFIG_DELETE_DATA = "delete_data";
	public static final String STUDY_WITHDRAW_CONFIG_ASK_USER = "ask_user";
	public static final String STUDY_WITHDRAW_CONFIG_NO_ACTION = "no_action";

	public static final String ACTIVITY_AT_FETAL_KICK_COUNTER = "fetalKickCounter";
	public static final String ACTIVITY_AT_SPATIAL_SPAN_MEMORY = "spatialSpanMemory";
	public static final String ACTIVITY_AT_TOWER_OF_HANOI = "towerOfHanoi";

	public static final String ACTIVE_TASK_ATTRIBUTE_TYPE_CONFIGURE = "configure_type";
	public static final String ACTIVE_TASK_ATTRIBUTE_TYPE_RESULT = "result_type";

	public static final String QUESTION_SCALE = "scale";
	public static final String QUESTION_CONTINUOUS_SCALE = "continuousScale";
	public static final String QUESTION_TEXT_SCALE = "textScale";
	public static final String QUESTION_VALUE_PICKER = "valuePicker";
	public static final String QUESTION_IMAGE_CHOICE = "imageChoice";
	public static final String QUESTION_TEXT_CHOICE = "textChoice";
	public static final String QUESTION_BOOLEAN = "boolean";
	public static final String QUESTION_NUMERIC = "numeric";
	public static final String QUESTION_TIME_OF_DAY = "timeOfDay";
	public static final String QUESTION_DATE = "date";
	public static final String QUESTION_TEXT = "text";
	public static final String QUESTION_EMAIL = "email";
	public static final String QUESTION_TIME_INTERVAL = "timeInterval";
	public static final String QUESTION_HEIGHT = "height";
	public static final String QUESTION_LOCATION = "location";

	public static final String STEP_CONDITION_DEFAULT = "default";
	public static final String STEP_DESTINATION_COMPLETION = "completion";

	public static final String QUESTION_NUMERIC_STYLE_INTEGER = "Integer";
	public static final String QUESTION_NUMERIC_STYLE_DECIMAL = "Decimal";

	public static final String QUESTION_RESPONSE_MASTERDATA_TYPE_DATE_DATE = "Date";
	public static final String QUESTION_RESPONSE_MASTERDATA_TYPE_DATE_DATETIME = "Date-Time";

	public static final String DAYS_OF_THE_CURRENT_WEEK = "Days of the current week";
	public static final String DAYS_OF_THE_CURRENT_MONTH = "Days of the current month";
	public static final String MULTIPLE_TIMES_A_DAY = "24 hours of current day";
	public static final String WEEKS_OF_THE_CURRENT_MONTH = "Weeks of the current month";
	public static final String MONTHS_OF_THE_CURRENT_YEAR = "Months of the current year";
	public static final String RUN_BASED = "Run-based";

	public static final String CHART_DAY_OF_WEEK = "days_of_week";
	public static final String CHART_DAYS_OF_MONTH = "days_of_month";
	public static final String CHART_WEEK_OF_MONTH = "weeks_of_month";
	public static final String CHART_MONTHS_OF_YEAR = "months_of_year";
	public static final String CHART_RUNS = "runs";
	public static final String CHART_HOURS_A_DAY = "hours_of_day";

	public static final String CHART_TYPE_PIE = "pie-chart";
	public static final String CHART_TYPE_BAR = "bar-chart";
	public static final String CHART_TYPE_LINE = "line-chart";

	public static final String DASHBOARD_STATISTICS = "statistics";
	public static final String DASHBOARD_CHARTS = "charts";

	public static final String NOTIFICATION_TYPE_GT = "GT";
	public static final String NOTIFICATION_TYPE_ST = "ST";

	public static final String NOTIFICATION_GATEWAY = "Gateway";
	public static final String NOTIFICATION_STANDALONE = "Study";

	public static final String NOTIFICATION_SUBTYPE_GENERAL = "Announcement";
	public static final String NOTIFICATION_SUBTYPE_ACTIVITY = "Activity";
	public static final String NOTIFICATION_SUBTYPE_STUDY = "Study";
	public static final String NOTIFICATION_SUBTYPE_RESOURCE = "Resource";
	public static final String NOTIFICATION_SUBTYPE_STUDY_EVENT = "studyEvent";

	public static final String NOTIFICATION_AUDIENCE_ALL = "All";
	public static final String NOTIFICATION_AUDIENCE_PARTICIPANTS = "Participants";
	public static final String NOTIFICATION_AUDIENCE_LIMITED = "Limited";

	public static final String STUDY_DEFAULT_VERSION = "1.0";

	public static final String STUDY_AUTH_TYPE_PLATFORM = "platform";
	public static final String STUDY_AUTH_TYPE_OS = "os";
	public static final String STUDY_AUTH_TYPE_BUNDLE_ID = "bundleId";

	public static final String ANCHORDATE_TYPE_QUESTION = "date-question";
	public static final String ANCHORDATE_TYPE_ENROLLMENT = "enrollment-date";

	public static final String INTERCEPTOR_URL_PING = "/ping";
	public static final String INTERCEPTOR_URL_MAIL = "/mail";
	public static final String INTERCEPTOR_URL_APP_VERSION = "/updateAppVersion";
	public static final String INTERCEPTOR_URL_DB_QUERY = "/testQuery";

	public static final String QUERY_INSERT = "INSERT";
	public static final String QUERY_UPDATE = "UPDATE";
	public static final String QUERY_DELETE = "DELETE";
	public static final String QUERY_ALTER = "ALTER";

	public static final String ACTIVITY_STATUS_ACTIVE = "active";
	public static final String ACTIVITY_STATUS_DELETED = "deleted";

	public static final String DEFAULT_MIN_TIME = "00:00:00";
	public static final String DEFAULT_MAX_TIME = "23:59:59";
	public static final String RESULT_TYPE_GROUPED = "grouped";

	public static final String COMPREHENSION_RESPONSE_STRUCTURE_ANY = "any";
	public static final String COMPREHENSION_RESPONSE_STRUCTURE_ALL = "all";

	public static final String SSM_INITIAL = "Initial_Span_spatial";
	public static final String SSM_MINIMUM = "Minimum_Span_spatial";
	public static final String SSM_MAXIMUM = "Maximum_Span_spatial";
	public static final String SSM_PLAY_SPEED = "Play_Speed_spatial";
	public static final String SSM_MAX_TEST = "Maximum_Tests_spatial";
	public static final String SSM_MAX_CONSECUTIVE_FAILURES = "Maximum_Consecutive_Failuress_spatial";
	public static final String SSM_REQUIRE_REVERSAL = "Require_reversal_spatial";
	public static final String SSM_SCORE = "Score_spatial";
	public static final String SSM_NO_OF_GAMES = "Number_of_Games_spatial";
	public static final String SSM_NO_OF_FAILURES = "Number_of_Failures_spatial";

	public static final Integer MAX_KICK_COUNT = 99;

	public static final String CB_RESPONSE_TYPE_IDS = "1,2,8,13,14";
	public static final String CB_RESPONSE_TYPE = "scale,continuousScale,numeric,timeInterval,height";
	public static final String CBO_EQUAL_TO = "e";
	public static final String CBO_NOT_EQUAL_TO = "ne";
	public static final String CBO_GREATER_THAN = "gt";
	public static final String CBO_LESSER_THAN = "lt";
	public static final String CBO_GREATER_THAN_OR_EQUAL_TO = "gte";
	public static final String CBO_LESSER_THAN_OR_EQUAL_TO = "lte";
	public static final String CBO_RANGE = "range";

	public static final String CBO_OPERATOR_EQUAL = "==";
	public static final String CBO_OPERATOR_NOT_EQUAL = "!=";
	public static final String CBO_OPERATOR_GREATER_THAN = ">";
	public static final String CBO_OPERATOR_LESSER_THAN = "<";

	public static final String WCP_DATE_RANGE_UNTILL_CURRENT = "Until current date";
	public static final String WCP_DATE_RANGE_AFTER_CURRENT = "After current date";
	public static final String WCP_DATE_RANGE_CUSTOM = "Custom";

	public static final String DATE_RANGE_UNTILL_CURRENT = "untilCurrent";
	public static final String DATE_RANGE_AFTER_CURRENT = "afterCurrent";
	public static final String DATE_RANGE_CUSTOM = "custom";

	public static final String CHART_DATA_SOURCE_SCORE = "score";
	public static final String CHART_DATA_SOURCE_NO_OF_GAMES = "numberOfGames";
	public static final String CHART_DATA_SOURCE_NO_OF_FAILURES = "numberOfFailures";

	public static final String FETAL_MAX_DURATION = "86400"; // 24:00 HH:mm in
																// seconds
	public static final String FETAL_MAX_DURATION_WCP = "23:59";

	public static final String FDA_SMD_NOTIFICATION_TITLE = "fda.smd.notification.title";
	public static final String FDA_SMD_MAIL_TITLE = "fda.smd.email.title";
	public static final String FDA_SMD_STUDY_THUMBNAIL_PATH = "fda.smd.study.thumbnailPath";
	public static final String FDA_SMD_RESOURCE_PDF_PATH = "fda.smd.resource.pdfPath";
	public static final String FDA_SMD_QUESTIONNAIRE_IMAGE = "fda.smd.questionnaire.image";
	public static final String FDA_SMD_STUDY_PAGE_PATH = "fda.smd.study.pagePath";

	public static final String FDA_SMD_PRIVACY_POLICY = "fda.smd.pricaypolicy";
	public static final String FDA_SMD_TERMS = "fda.smd.terms";
	
	public static final String ANCHOR_TYPE_ENROLLMENTDATE = "EnrollmentDate";
	public static final String ANCHOR_TYPE_ACTIVITYRESPONSE = "ActivityResponse";
	public static final String SCHEDULETYPE_REGULAR = "Regular";
	public static final String SCHEDULETYPE_ANCHORDATE= "AnchorDate";

}
