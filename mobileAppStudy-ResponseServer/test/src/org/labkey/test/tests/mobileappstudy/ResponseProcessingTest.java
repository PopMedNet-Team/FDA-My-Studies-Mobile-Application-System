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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.Locator;
import org.labkey.test.TestFileUtils;
import org.labkey.test.categories.Git;
import org.labkey.test.data.mobileappstudy.AbstractQuestionResponse.SupportedResultType;
import org.labkey.test.data.mobileappstudy.ChoiceQuestionResponse;
import org.labkey.test.data.mobileappstudy.GroupedQuestionResponse;
import org.labkey.test.data.mobileappstudy.InitialSurvey;
import org.labkey.test.data.mobileappstudy.MedForm;
import org.labkey.test.data.mobileappstudy.QuestionResponse;
import org.labkey.test.data.mobileappstudy.Survey;
import org.labkey.test.pages.mobileappstudy.ResponseQueryPage;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.util.DataRegionTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({Git.class})
public class ResponseProcessingTest extends BaseMobileAppStudyTest
{
    private final static String STUDY_NAME01 = "ResponseProcessing";  // Study names are case insensitive
    private final static String PROJECT_NAME01 = "Response Processing Project " + STUDY_NAME01;

    //Survey Setup
    private final static String SURVEY_NAME = "InitialSurvey";
    private final static String SURVEY_VERSION = "123.9";

    @Override
    protected @Nullable String getProjectName()
    {
        return PROJECT_NAME01;
    }

    @Override
    void setupProjects()
    {
        //Setup a study
        _containerHelper.createProject(PROJECT_NAME01, "Mobile App Study");
        goToProjectHome(PROJECT_NAME01);
        SetupPage setupPage = new SetupPage(this);
        setupPage.getStudySetupWebPart().checkResponseCollection();
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME01);
        setupPage.validateSubmitButtonEnabled();
        setupPage.getStudySetupWebPart().clickSubmit();

        setupLists();
        setSurveyMetadataDropDir();
        goToProjectHome(PROJECT_NAME01);
    }

    private void setupLists()
    {
        //TODO: This archive has not been updated to match some of the newer BTC & dynamic schema changes
        //       specifically: SurveyId is now dynamically named in sub-lists to match the parent-list
        _listHelper.importListArchive(TestFileUtils.getSampleData("TestLists.lists.zip"));
    }

    @Before
    public void prep()
    {
        goToProjectHome(PROJECT_NAME01);
    }

    @Test
    public void testBoolResultType() throws ParseException
    {
        int submissionCount = 0; //used for validation
        int errorCount = 0;

        SupportedResultType type = SupportedResultType.BOOL;

        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        String fieldName = InitialSurvey.PLANNED_PREGNANCY;
        String fieldHeader = "Planned Pregnancy";

        //Test skipped
        String skipToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        QuestionResponse qr = new QuestionResponse(type, fieldName, new Date(), new Date(), true, true);
        log("Testing Question skipped = true for type [" + type + "]");
        submitQuestion(qr, skipToken, 200);

        //Test boolean value
        String valToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, true);
        log("Testing Question Type [" + type + "] vs value type [boolean]");
        submitQuestion(qr, valToken, 200);

        //Test integer value
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, 200);
        log("Testing Question Type [" + type + "] vs value type [int]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test double
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Double.valueOf(1.3));
        log("Testing Question Type [" + type + "] vs value type [double]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test Date
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, "\"2016-02-22\"");
        log("Testing Question Type [" + type + "] vs value type [Date]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test String
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, "\"Test Waffles\"");
        log("Testing Question Type [" + type + "] vs value type [String]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test collection
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Arrays.asList("\"Test\"", "2"));
        log("Testing Question Type [" + type + "] vs value type [Collection<String>]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test null
        String nullToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, null);
        log("Testing Question Type [" + type + "] vs value type [null]");
        submitQuestion(qr, nullToken, 200);

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount);
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME));
        assertBlankValue(skipToken, fieldHeader, "Invalid skipped value");
        assertBlankValue(nullToken, fieldHeader, "Invalid null value");
        assertSubmittedValue(valToken, fieldHeader, "Submitted value not present", String.valueOf(true));

        checkExpectedErrors(errorCount);
    }

    private void viewSurveyRequests(String appToken)
    {
        DataRegionTable table = new DataRegionTable("query", getDriver());

        table.openCustomizeGrid();
        table.getCustomizeView().clearFilters();
        table.getCustomizeView().addFilter("ParticipantId/AppToken", "Equals", appToken);
        table.getCustomizeView().applyCustomView();
    }

    @Test
    public void testTimeOfDayResultType()
    {
        SupportedResultType type = SupportedResultType.TIME_OF_DAY;
        String fieldName = InitialSurvey.BREAKFAST_TIME;
        String fieldHeader = "Breakfast Time";

        String value = "08:13:32";

        String valToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        QuestionResponse qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, value);
        qr.setFormatString("\"%1$s\"");
        log("Testing Question Type [" + type + "] vs value type [String]");
        submitQuestion(qr, valToken, 200);

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(valToken, 1, 1);

        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME));
        assertSubmittedValue(valToken, fieldHeader, "Submitted value not present", value);
        checkExpectedErrors(0);
    }


    @Test
    public void testDateResultType()
    {
        int submissionCount = 0; //used for validation
        int errorCount = 0;

        SupportedResultType type = SupportedResultType.DATE;

        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        String fieldName = InitialSurvey.DUE_DATE;
        String fieldHeader = "Due Date";

        Date value = new Date();

        //Test skipped
        String skipToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        QuestionResponse qr = new QuestionResponse(type, fieldName, new Date(), new Date(), true, value);
        log("Testing Question skipped = true for type [" + type + "]");
        submitQuestion(qr, skipToken, 200);

        //Test boolean value
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, true);
        qr.setFormatString("%1$s"); // need to override the Date format string
        log("Testing Question Type [" + type + "] vs value type [boolean]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test integer value
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, 200);
        qr.setFormatString("%1$s"); // need to override the Date format string
        log("Testing Question Type [" + type + "] vs value type [int]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test double
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Double.valueOf(1.3));
        qr.setFormatString("%1$s"); // need to override the Date format string
        log("Testing Question Type [" + type + "] vs value type [double]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test Date
        String valToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, value);
        log("Testing Question Type [" + type + "] vs value type [Date]");
        submitQuestion(qr, valToken, 200);

        //Test String
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, "\"Test Waffles\"");
        qr.setFormatString("%1$s"); // need to override the Date format string
        log("Testing Question Type [" + type + "] vs value type [String]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test collection
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Arrays.asList("\"Test\"", "2"));
        qr.setFormatString("%1$s"); // need to override the Date format string
        log("Testing Question Type [" + type + "] vs value type [Collection<String>]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test null
        String nullToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, null);
        qr.setFormatString("%1$s"); // need to override the Date format string
        log("Testing Question Type [" + type + "] vs value type [null]");
        submitQuestion(qr, nullToken, 200);

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount);
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME));
        assertBlankValue(skipToken, fieldHeader, "Invalid skipped value");
        assertBlankValue(nullToken, fieldHeader, "Invalid null value");
        assertSubmittedValue(valToken, fieldHeader, "Submitted value not present", new SimpleDateFormat("yyyy-MM-dd 00:00").format(value));

        checkExpectedErrors(errorCount);
    }

    @Test
    public void testScaleResultType()
    {
        int submissionCount = 0; //used for validation
        int errorCount = 0;

        SupportedResultType type = SupportedResultType.SCALE;

        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        String fieldName = InitialSurvey.NUM_ALCOHOL_WEEK;
        String fieldHeader = "Num Alcohol Week";
        double value = 400;

        //Test skipped
        String skipToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        QuestionResponse qr = new QuestionResponse(type, fieldName, new Date(), new Date(), true, value);
        log("Testing Question skipped = true for type [" + type + "]");
        submitQuestion(qr, skipToken, 200);

        //Test boolean value
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, true);
        log("Testing Question Type [" + type + "] vs value type [boolean]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test integer value
        String valToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, value);
        log("Testing Question Type [" + type + "] vs value type [int]");
        submitQuestion(qr, valToken, 200);

        //Test integer min value
        String minToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Double.MIN_VALUE);
        log("Testing Question Type [" + type + "] vs value int.min");
        submitQuestion(qr, minToken, 200);

        //Test integer max value
        String maxToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Double.MAX_VALUE);
        log("Testing Question Type [" + type + "] vs value int.max");
        submitQuestion(qr, maxToken, 200);

        //Test long value
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Long.MAX_VALUE);
        log("Testing Question Type [" + type + "] vs value type [long]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test double
        String doubleToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        double doubleValue = 1.3;
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Double.valueOf(doubleValue));
        log("Testing Question Type [" + type + "] vs value type [double]");
        submitQuestion(qr, doubleToken, 200);

        //Test Date
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, new Date());
        qr.setFormatString("\"%1$s\"");
        log("Testing Question Type [" + type + "] vs value type [Date]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test String
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, "\"Test Waffles\"");
        log("Testing Question Type [" + type + "] vs value type [String]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test collection
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Arrays.asList("\"Test\"", "2"));
        log("Testing Question Type [" + type + "] vs value type [Collection<String>]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test null
        String nullToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, null);
        log("Testing Question Type [" + type + "] vs value type [null]");
        submitQuestion(qr, nullToken, 200);

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount);
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME));

        assertBlankValue(skipToken, fieldHeader, "Invalid skipped value");
        assertBlankValue(nullToken, fieldHeader, "Invalid null value");
        assertSubmittedValue(valToken, fieldHeader, "Submitted value not present", String.valueOf(value));
        assertSubmittedValue(minToken, fieldHeader, "Min value not present", String.valueOf(Double.MIN_VALUE));
        assertSubmittedValue(maxToken, fieldHeader, "Max value not present", String.valueOf(Double.MAX_VALUE));
        assertSubmittedValue(doubleToken, fieldHeader, "Submitted value not present", String.valueOf(doubleValue));

        checkExpectedErrors(errorCount);
    }

    private void assertSubmittedValue(String appToken, String fieldHeader, String assertMsg, String expectedValue )
    {
        viewSurveyRequests(appToken);

        DataRegionTable table = new DataRegionTable("query", getDriver());
        table.afterPageLoad();
        String value = table.getDataAsText(0, fieldHeader);
        assertEquals(assertMsg, expectedValue, value);
    }

    private void assertBlankValue(String appToken, String fieldHeader, String assertMsg )
    {
        viewSurveyRequests(appToken);
        DataRegionTable table = new DataRegionTable("query", getDriver());
        String value = table.getDataAsText(0, fieldHeader);
        assertTrue(assertMsg, StringUtils.isBlank(value));
    }

    @Test
    public void testNumberResultType()
    {
        int submissionCount = 0; //used for validation
        int successfulProcessingExpected = 0;
        int errorCount = 0;

        SupportedResultType type = SupportedResultType.NUMERIC;

        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        String fieldName = InitialSurvey.NUM_ALCOHOL_WEEK;
        String fieldHeader = "Num Alcohol Week";
        Double value = 3.14;

        //Test skipped
        String skipToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        QuestionResponse qr = new QuestionResponse(type, fieldName, new Date(), new Date(), true, value);
        log("Testing Question skipped = true for type [" + type + "]");
        submitQuestion(qr, skipToken, 200);

        //Test boolean value
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, true);
        log("Testing Question Type [" + type + "] vs value type [boolean]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test integer value
        int intVal = 4;
        String intToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, intVal);
        log("Testing Question Type [" + type + "] vs value type [int]");
        submitQuestion(qr, intToken, 200);

        //Test double
        String valToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, value);
        log("Testing Question Type [" + type + "] vs value type [double]");
        submitQuestion(qr, valToken, 200);

        //Test Date
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, new Date());
        qr.setFormatString("\"%1$s\"");
        log("Testing Question Type [" + type + "] vs value type [Date]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test String
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, "\"Test Waffles\"");
        log("Testing Question Type [" + type + "] vs value type [String]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test collection
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Arrays.asList("\"Test\"", "2"));
        log("Testing Question Type [" + type + "] vs value type [Collection<String>]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test null
        String nullToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, null);
        log("Testing Question Type [" + type + "] vs value type [null]");
        submitQuestion(qr, nullToken, 200);

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount);
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME));
        assertBlankValue(skipToken, fieldHeader, "Invalid skipped value");
        assertBlankValue(nullToken, fieldHeader, "Invalid null value");
        assertSubmittedValue(valToken, fieldHeader, "Submitted value not present", String.valueOf(value));
        assertSubmittedValue(intToken, fieldHeader, "Double value not present", String.valueOf(Double.valueOf(intVal)));

        checkExpectedErrors(errorCount);
    }

    @Test
    public void testTextResultType()
    {
        int submissionCount = 0; //used for validation
        int errorCount = 0;

        SupportedResultType type = SupportedResultType.TEXT;

        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        String fieldName = InitialSurvey.ILLNESS_WEEK;
        String fieldHeader = "Illness Week";
        String value = "I \u9829 waffles";

        //Test skipped
        String skipToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        QuestionResponse qr = new QuestionResponse(type, fieldName, new Date(), new Date(), true, value);
        log("Testing Question skipped = true for type [" + type + "]");
        submitQuestion(qr, skipToken, 200);

        //Test boolean value
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, true);
        qr.setFormatString("%1$s");
        log("Testing Question Type [" + type + "] vs value type [boolean]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test integer value
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, 200);
        qr.setFormatString("%1$s");
        log("Testing Question Type [" + type + "] vs value type [int]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test double
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Double.valueOf(1.3));
        qr.setFormatString("%1$s");
        log("Testing Question Type [" + type + "] vs value type [double]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test Date
        String dateToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        Date dateVal = new Date();
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, dateVal);
        log("Testing Question Type [" + type + "] vs value type [Date]");
        submitQuestion(qr, dateToken, 200);

        //Test String
        String valToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, value);
        log("Testing Question Type [" + type + "] vs value type [String]");
        submitQuestion(qr, valToken, 200);

        //Test collection
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, Arrays.asList("\"Test\"", "2"));
        qr.setFormatString("%1$s");
        log("Testing Question Type [" + type + "] vs value type [Collection<String>]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        errorCount++;

        //Test null
        String nullToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, null);
        qr.setFormatString("%1$s");
        log("Testing Question Type [" + type + "] vs value type [null]");
        submitQuestion(qr, nullToken, 200);

        //Test empty string
        String emptyToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, "");
        log("Testing Question Type [" + type + "] Îvs value type [null]");
        submitQuestion(qr, emptyToken, 200);

        //Test white space
        String wsToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        qr = new QuestionResponse(type, fieldName, new Date(), new Date(), false, "\\t \\n");
        log("Testing Question Type [" + type + "] vs value type [null]");
        submitQuestion(qr, wsToken, 200);

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount);
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME));
        assertBlankValue(skipToken, fieldHeader, "Invalid skipped value");
        assertBlankValue(nullToken, fieldHeader, "Invalid null value");
        assertSubmittedValue(valToken, fieldHeader, "Submitted value not present", String.valueOf(value));
        assertBlankValue(emptyToken, fieldHeader, "Invalid empty string value");
        assertBlankValue(wsToken, fieldHeader, "Invalid whitespace string value");
        assertSubmittedValue(dateToken, fieldHeader, "Date value not treated as string", String.valueOf(dateVal));

        checkExpectedErrors(errorCount);
    }

    @Test
    public void testChoiceType()
    {
        int submissionCount = 0; //used for validation
        int successfulProcessingExpected = 0;

        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null);
        String field = InitialSurvey.SUPPLEMENTS;
        String fieldHeader = "Supplements";

        String value1 = "Pancakes";
        String value2 = "French Toast";

        //Test with array
        QuestionResponse qr = new ChoiceQuestionResponse( field, new Date(), new Date(), false,
                value1, "Waffles", value2, "Crepes");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Test repeat
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Test skipping
        qr.setSkipped(true);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Test with Empty
        qr = new ChoiceQuestionResponse( field, new Date(), new Date(), false);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        qr.setSkipped(true);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Test with null not skipped
        qr = new QuestionResponse(SupportedResultType.CHOICE, field, new Date(), new Date(), false, null);
        qr.setFormatString("%1$s");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        qr.setSkipped(true);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Test single with array brackets
        String singleValue = "Giant Waffles";
        qr = new ChoiceQuestionResponse( field, new Date(), new Date(), false, singleValue);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        qr.setSkipped(true);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Test single without array brackets
        qr = new QuestionResponse(SupportedResultType.CHOICE, field, new Date(), new Date(), false, "Single");
        qr.setFormatString("\"%1$s\"");
        submitQuestion(qr, appToken, 200);
        submissionCount++;

        int intValue = 6;
        String intValueString = String.valueOf(intValue);
        qr = new QuestionResponse(SupportedResultType.CHOICE, field, new Date(), new Date(), false, intValue);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        double doubleValue = 3.14;
        String doubleValueString = String.valueOf(doubleValue);
        qr = new QuestionResponse(SupportedResultType.CHOICE, field, new Date(), new Date(), false, doubleValue);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        Date dateValue = new Date();
        String dateValueString = String.valueOf(dateValue);
        qr = new QuestionResponse(SupportedResultType.CHOICE, field, new Date(), new Date(), false, dateValue);
        qr.setFormatString("[\"%1$s\"]");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        int mixValue1 = 22;
        String mixValue2 = "Mix String";
        String mixIntString = String.valueOf(mixValue1);
        qr = new QuestionResponse(SupportedResultType.CHOICE, field, new Date(), new Date(), false, Arrays.asList(mixValue1, "\""+mixValue2+"\""));
        qr.setFormatString("%1$s");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount, successfulProcessingExpected);
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME + fieldHeader));

        DataRegionTable table = new DataRegionTable("query", getDriver());
        List<String> supplements = table.getColumnDataAsText(fieldHeader);

        assertExpectedValueCount(supplements, value1, 2);
        assertExpectedValueCount(supplements, value2, 2);
        assertExpectedValueCount(supplements, singleValue, 1);
        assertExpectedValueCount(supplements, intValueString, 1);
        assertExpectedValueCount(supplements, doubleValueString, 1);
        assertExpectedValueCount(supplements, dateValueString, 1);
        assertExpectedValueCount(supplements, mixIntString, 1);
        assertExpectedValueCount(supplements, mixValue2, 1);

        //Request w/o brackets
        checkExpectedErrors(1);
    }

    private void assertExpectedValueCount(List list, String value, int rowCount)
    {
        assertEquals("Value [\"" + value +"\"] found an unexpected number of times", rowCount, Collections.frequency(list, value));
    }

    @Test
    public void testGroupedType()
    {
        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );

        String rx = MedForm.MedType.Prescription.getDisplayName();

        int submissionCount = 0; //used for validation
        int successfulProcessingExpected = 0;

        //Single result
        QuestionResponse qr = new ChoiceQuestionResponse("medName",
                new Date(), new Date(), false, "Acetaminophen");
        QuestionResponse groupedQuestionResponse = new GroupedQuestionResponse(rx,
                new Date(), new Date(), false, qr);
        submitQuestion(groupedQuestionResponse, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Group within a group


        QuestionResponse groupedQuestionResponse1 = new GroupedQuestionResponse("rx",
            new Date(), new Date(), false, new GroupedQuestionResponse("Group", new Date(), new Date(), false,
                new QuestionResponse(SupportedResultType.BOOL, "Bool", new Date(), new Date(), false, true),
                new QuestionResponse(SupportedResultType.NUMERIC, "Decimal", new Date(), new Date(), false, 3.14),
                new QuestionResponse(SupportedResultType.TEXT, "Text", new Date(), new Date(), false, "I'm part of a grouped group"),
                new QuestionResponse(SupportedResultType.DATE, "Date", new Date(), new Date(), false, new Date())
            ));
        submitQuestion(groupedQuestionResponse1, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Test Arrays passed
        MedForm medForm = new MedForm();
        medForm.addMed("Advil");
        medForm.addMedCondition("sleep deprivation");

        MedForm medForm1 = new MedForm();
        medForm1.addMed("Tylenol", "Aspirin");
        medForm1.addMedCondition("Fever");

        //Single response with multiple answers
        QuestionResponse groupedQuestionResponse2 = new GroupedQuestionResponse(rx,
                new Date(), new Date(), false, medForm);
        submitQuestion(groupedQuestionResponse2, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Repeat same data
        submitQuestion(groupedQuestionResponse2, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Repeat same data skipped
        groupedQuestionResponse2.setSkipped(true);
        submitQuestion(groupedQuestionResponse2, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //multiple responses with multiple answers
        QuestionResponse groupedQuestionResponse4 = new GroupedQuestionResponse(rx,
                new Date(), new Date(), false, medForm, medForm1);
        submitQuestion(groupedQuestionResponse4, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        QuestionResponse groupedQuestionResponse5 = new GroupedQuestionResponse(rx,
                new Date(), new Date(), false, medForm, new MedForm());
        submitQuestion(groupedQuestionResponse5, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Result array containing a skipped response
        MedForm skippedForm = new MedForm();
        QuestionResponse qr1 = skippedForm.addMed("Test");
        qr1.setSkipped(true);
        skippedForm.addMedCondition("Fever");

        QuestionResponse groupedQuestionResponse8 = new GroupedQuestionResponse(rx,
                new Date(), new Date(), false, medForm, skippedForm);
        submitQuestion(groupedQuestionResponse8, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //test no values passed not skipped
        QuestionResponse groupedQuestionResponse6 = new GroupedQuestionResponse(rx,
                new Date(), new Date(), false);
        submitQuestion(groupedQuestionResponse6, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //test no values passed skipped
        QuestionResponse groupedQuestionResponse7 = new GroupedQuestionResponse(rx,
                new Date(), new Date(), true);
        submitQuestion(groupedQuestionResponse7, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Go to Response table
        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.viewResponseTable();
        responses.filterByAppToken(appToken);
        assertEquals("Unexpected number of requests", submissionCount, responses.getResponseCount());
        Collection<String> statuses = responses.getStatuses();
        assertEquals("Unexpected number of successfully processed requests", successfulProcessingExpected, Collections.frequency(statuses, "PROCESSED"));
        assertEquals("Unexpected number of unsuccessfully processed requests", submissionCount-successfulProcessingExpected, Collections.frequency(statuses, "ERROR"));

        //Go to the Rx Meds list
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME + "RxMedName"));
        DataRegionTable table = new DataRegionTable("query", getDriver());

        //Filter to only our requests
        table.openCustomizeGrid();
        table.getCustomizeView().clearFilters();
        //Leaving this reference to SurveyId instead of altering the lists archive. //TODO: Change SurveyId to ActivityId
        table.getCustomizeView().addFilter("ParticipantId/AppToken", "Equals", appToken);
        table.getCustomizeView().applyCustomView(WAIT_FOR_PAGE);


        List values = table.getColumnDataAsText("MedName");
        assertEquals("Unexpected number of meds", 5, Collections.frequency(values, "Advil"));
        assertEquals("Unexpected number of meds", 1, Collections.frequency(values, "Tylenol"));
        assertEquals("Unexpected number of meds", 1, Collections.frequency(values, "Aspirin"));
        assertEquals("Unexpected number of meds", 1, Collections.frequency(values, "Acetaminophen"));
    }

    @Test
    public void testMissingResponseProperties()
    {
        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null);
        String field = InitialSurvey.DUE_DATE;
        String fieldHeader = "Due Date";
        Date value = new Date();

        Date start = new Date();
        Date end = new Date();

        int submissionCount = 0;
        int successfulProcessingExpected = 0;
        int expectedErrorCount = 0;

        //Test normal accepted
        QuestionResponse qr = new QuestionResponse(SupportedResultType.DATE, field,
                new Date(), new Date(), false, value);
        Survey survey = new InitialSurvey(appToken, SURVEY_NAME, SURVEY_VERSION, new Date(), new Date());
        survey.addResponse(qr);
        submitSurvey(survey, 200);
        submissionCount++;
        successfulProcessingExpected++;

        survey.setOmitStart(true);
        submitSurvey(survey, 200);
        survey.setOmitStart(false);
        submissionCount++;
        successfulProcessingExpected++;

        survey.setOmitEnd(true);
        submitSurvey(survey, 200);
        survey.setOmitEnd(false);
        submissionCount++;
        successfulProcessingExpected++;

        survey.setOmitResults(true);
        submitSurvey(survey, 200);
        survey.setOmitResults(false);
        submissionCount++;
        expectedErrorCount++;

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount, successfulProcessingExpected);
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME));
        viewSurveyRequests(appToken);

        DataRegionTable table = new DataRegionTable("query", getDriver());
        List<String> values = table.getColumnDataAsText(fieldHeader);

        assertExpectedValueCount(values, new SimpleDateFormat("yyyy-MM-dd 00:00").format(value), successfulProcessingExpected);

        values = table.getColumnDataAsText("Start Time");
        assertExpectedValueCount(values, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(start), successfulProcessingExpected - 1);
        assertExpectedValueCount(values, " ", 1); //Check missing requests

        values = table.getColumnDataAsText("End Time");
        assertExpectedValueCount(values, new SimpleDateFormat("yyyy-MM-dd HH:mm").format(end), successfulProcessingExpected - 1);
        assertExpectedValueCount(values, " ", 1); //Check missing requests

        checkExpectedErrors(expectedErrorCount);
    }

    @Test
    public void testMissingResultProperties()
    {
        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        String field = InitialSurvey.NUM_ALCOHOL_WEEK;
        String fieldHeader = "Num Alcohol Week";
        double value = 0.5;

        int submissionCount = 0;
        int successfulProcessingExpected = 0;
        int expectedErrorCount = 0;

        //Test normal accepted
        QuestionResponse qr = new QuestionResponse(SupportedResultType.NUMERIC, field,
                new Date(), new Date(), false, value);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Test missing End time
        qr.setOmitEnd(true);
        log("Testing Question submission with missing EndTime property");
        submitQuestion(qr, appToken, 200);
        qr.setOmitEnd(false);
        submissionCount++;
        successfulProcessingExpected++;

        //Test missing Start time
        qr.setOmitStart(true);
        log("Testing Question submission with missing StartTime property");
        submitQuestion(qr, appToken, 200);
        qr.setOmitStart(false);
        submissionCount++;
        successfulProcessingExpected++;

        //Test missing Skipped
        qr.setOmitSkipped(true);
        log("Testing Question submission with missing Skipped property");
        submitQuestion(qr, appToken, 200);
        qr.setOmitSkipped(false);
        submissionCount++;
        successfulProcessingExpected++;

        //Test missing QuestionId
        qr.setOmitQuestionId(true);
        log("Testing Question submission with missing Identifier property");
        submitQuestion(qr, appToken, 200);
        qr.setOmitQuestionId(false);
        submissionCount++;
        expectedErrorCount++;

        //Test missing Type
        qr.setOmitType(true);
        log("Testing Question submission with missing type property");
        submitQuestion(qr, appToken, 200);
        qr.setOmitType(false);
        submissionCount++;
        successfulProcessingExpected++;

        //Test missing Result
        qr.setOmitResult(true);
        log("Testing Question submission with missing Result property and Skipped = false");
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++; //TODO: I would expect this to cause an error

        //Test missing Result but skipped
        qr.setSkipped(true);
        log("Testing Question submission with missing Result property and Skipped = true");
        submitQuestion(qr, appToken, 200);
        qr.setOmitResult(false);
        submissionCount++;
        successfulProcessingExpected++; //TODO: I would not expect this to cause an error

        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount, successfulProcessingExpected);
        goToManageLists();
        click(Locator.linkWithText(SURVEY_NAME));
        viewSurveyRequests(appToken);

        DataRegionTable table = new DataRegionTable("query", getDriver());
        List<String> values = table.getColumnDataAsText(fieldHeader);

        assertExpectedValueCount(values, String.valueOf(value), successfulProcessingExpected - 3);
        assertExpectedValueCount(values, " ", 3); //Check missing result requests

        checkExpectedErrors(expectedErrorCount);
    }

    @Test
    public void testReprocessAction()
    {
        String appToken = getNewAppToken(PROJECT_NAME01, STUDY_NAME01, null );
        String field = InitialSurvey.NUM_ALCOHOL_WEEK;
        double value = 0.5;

        int submissionCount = 0;
        int successfulProcessingExpected = 0;
        int expectedErrorCount = 0;

        //Submit successfully processed response
        QuestionResponse qr = new QuestionResponse(SupportedResultType.NUMERIC, field,
                new Date(), new Date(), false, value);
        submitQuestion(qr, appToken, 200);
        submissionCount++;
        successfulProcessingExpected++;

        //Submit error response
        qr.setOmitQuestionId(true);
        log("Testing Question submission with missing Identifier property");
        submitQuestion(qr, appToken, SURVEY_NAME, SURVEY_VERSION, 200);
        qr.setOmitQuestionId(false);
        submissionCount++;
        expectedErrorCount++;

        //Go to Response table
        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, submissionCount, successfulProcessingExpected);

        responses.filterByAppToken(appToken);
        String responseId = responses.getResponseId(0);
        responses.reprocessRows(0);
        expectedErrorCount++;
        responses.assertSuccessfulReprocessAlert(0, responseId);

        responses.reprocessRows(1);
        responses.assertSuccessfulReprocessAlert(1);

        checkExpectedErrors(expectedErrorCount);
    }

    private String submitQuestion(QuestionResponse qr, String appToken, int expectedStatusCode)
    {
        return super.submitQuestion(qr, appToken, SURVEY_NAME, SURVEY_VERSION, 200);
    }
}

