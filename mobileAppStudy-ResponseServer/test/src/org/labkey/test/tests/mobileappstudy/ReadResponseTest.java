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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.remoteapi.Command;
import org.labkey.remoteapi.CommandException;
import org.labkey.remoteapi.CommandResponse;
import org.labkey.remoteapi.Connection;
import org.labkey.remoteapi.query.SelectRowsCommand;
import org.labkey.remoteapi.query.SelectRowsResponse;
import org.labkey.test.Locator;
import org.labkey.test.categories.Git;
import org.labkey.test.components.mobileappstudy.TokenBatchPopup;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.pages.mobileappstudy.TokenListPage;
import org.labkey.test.util.APIUserHelper;
import org.labkey.test.util.ListHelper;
import org.labkey.test.util.PortalHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Category({Git.class})
public class ReadResponseTest extends BaseMobileAppStudyTest
{
    final String PROJECT_NAME = getProjectName();
    final String PROJECT_STUDY_NAME = "TEST_READRESPONSE_STUDY";
    final String LIST_DIFF_DATATYPES = "TestListDiffDataTypes";
    final String LIST_SECOND = "SecondSimpleList";
    final String LIST_THIRD = "ThirdList";

    final String FIRST_STRING_FIELD_VALUE = "This is the string value for participant ";
    final String SECOND_STRING_FIELD_VALUE = "This is the second string value for participant ";
    final String THIRD_STRING_FIELD_VALUE = "This is the third string value for participant ";

    final String FIRST_MULTILINE_STRING_FIELD = "This is the \r\nfirst\r\nmulti-line for participant $\r\nand here is a new line.";
    final String SECOND_MULTILINE_STRING_FIELD = "This is the \r\nsecond\r\nmulti-line for participant $\r\nand here is a new line.";
    final String THIRD_MULTILINE_STRING_FIELD = "This is the \r\nthird\r\nmulti-line for participant $\r\nand here is a new line.";

    final String FIRST_FLAG_FIELD = "First flag ";
    final String SECOND_FLAG_FIELD = "Second flag ";
    final String THIRD_FLAG_FIELD = "Third flag ";

    final String FIRST_MANTISSA = ".0123456789";
    final String SECOND_MANTISSA = ".22222";
    final String THIRD_MANTISSA = ".3333";

    final String FIRST_DATE = "2017-03-17 11:11:11.000";
    final String SECOND_DATE = "2017-01-15 08:02:00.000";
    final String THIRD_DATE = "2016-11-20 14:25:00.000";

    final int FIRST_INT_OFFSET = 5;
    final int SECOND_INT_OFFSET = 7;
    final int THIRD_INT_OFFSET = 11;

    final String DESCRIPTION_VALUE_SECOND_LIST = "Description for ";
    final String DESCRIPTION_VALUE_THIRD_LIST = "This is a description in the third list ";

    static ParticipantInfo participantToSkip, participantWithMultipleRow, participantWithOneRow, participantForSql;

    protected final PortalHelper _portalHelper = new PortalHelper(this);

    @Override
    protected String getProjectName()
    {
        return "Read Response Test Project";
    }

    @Override
    void setupProjects()
    {
        _containerHelper.deleteProject(PROJECT_NAME, false);
        _containerHelper.createProject(PROJECT_NAME, "Mobile App Study");

        createBatchAndAssignTokens();

        setupList();

    }

    private void createBatchAndAssignTokens()
    {
        String batchId, tokenCount = "100";

        log("Creating the batch.");
        goToProjectHome(PROJECT_NAME);

        SetupPage setupPage = new SetupPage(this);

        log("Set a study name.");
        setupPage.getStudySetupWebPart().setShortName(PROJECT_STUDY_NAME)
                .clickSubmit();

        log("Create " + tokenCount + " tokens.");
        TokenBatchPopup tokenBatchPopup = setupPage.getTokenBatchesWebPart().openNewBatchPopup();
        TokenListPage tokenListPage = tokenBatchPopup.createNewBatch(tokenCount);

        batchId = tokenListPage.getBatchId();
        log("Batch Id: " + batchId);

        List<String> tokensToAssign = new ArrayList<>();
        tokensToAssign.add(tokenListPage.getToken(0));
        tokensToAssign.add(tokenListPage.getToken(1));
        tokensToAssign.add(tokenListPage.getToken(2));
        tokensToAssign.add(tokenListPage.getToken(3));
        tokensToAssign.add(tokenListPage.getToken(4));
        tokensToAssign.add(tokenListPage.getToken(5));
        tokensToAssign.add(tokenListPage.getToken(6));

        log("Validate that the correct info for the tokens is shown in the grid.");
        goToProjectHome();
        confirmBatchInfoCreated(setupPage, batchId, tokenCount, "0");

        log("Now assign some of the tokens from the batch.");

        assignTokens(tokensToAssign, PROJECT_NAME, PROJECT_STUDY_NAME);

    }

    private void setupList()
    {
        List<ParticipantInfo> participantsInfo = getTokens();

        goToProjectHome();

        log("Create a list with columns for each of the basic data types.");
        ListHelper.ListColumn participantIdColumn = new ListHelper.ListColumn("participantId", "participantId", ListHelper.ListColumnType.Integer, "");
        ListHelper.ListColumn stringTypeColumn = new ListHelper.ListColumn("stringField", "stringField", ListHelper.ListColumnType.String, "");
        ListHelper.ListColumn multiLineTypeColumn = new ListHelper.ListColumn("multiLineField", "multiLineField", ListHelper.ListColumnType.MultiLine, "");
        ListHelper.ListColumn booleanTypeColumn = new ListHelper.ListColumn("booleanField", "booleanField", ListHelper.ListColumnType.Boolean, "");
        ListHelper.ListColumn integerTypeColumn = new ListHelper.ListColumn("integerField", "integerField", ListHelper.ListColumnType.Integer, "");
        ListHelper.ListColumn doubleTypeColumn = new ListHelper.ListColumn("doubleField", "doubleField", ListHelper.ListColumnType.Double, "");
        ListHelper.ListColumn dateTimeTypeColumn = new ListHelper.ListColumn("dateTimeField", "dateTimeField", ListHelper.ListColumnType.DateTime, "");
        ListHelper.ListColumn flagTypeColumn = new ListHelper.ListColumn("flagField", "flagField", ListHelper.ListColumnType.Flag, "");
        ListHelper.ListColumn userColumn = new ListHelper.ListColumn("user", "user", ListHelper.ListColumnType.String, "", new ListHelper.LookupInfo(getProjectName(), "core", "Users"));

        _listHelper.createList(getProjectName(), LIST_DIFF_DATATYPES, ListHelper.ListColumnType.AutoInteger, "Key", participantIdColumn, stringTypeColumn, multiLineTypeColumn, booleanTypeColumn, integerTypeColumn, doubleTypeColumn, dateTimeTypeColumn, flagTypeColumn, userColumn);
        clickButton("Done");

        clickAndWait(Locator.linkWithText(LIST_DIFF_DATATYPES));

        int index = participantsInfo.size()/2;
        ReadResponseTest.participantToSkip = new ParticipantInfo(participantsInfo.get(index).getId(), participantsInfo.get(index).getAppToken());
        log("Not going to put participant: " + ReadResponseTest.participantToSkip.getId() + " (" + ReadResponseTest.participantToSkip.getAppToken() + ") into the list.");

        long participantId;
        Map<String, String> rowData;
        String userName = getCurrentUserName();

        for(ParticipantInfo participantInfo : participantsInfo)
        {
            if(participantInfo.getId() != ReadResponseTest.participantToSkip.getId())
            {

                // Convert the id to an int because it will be used in some of the numeric fields below.
                participantId = participantInfo.getId();

                rowData = new HashMap<>();

                rowData.put("participantId", Long.toString(participantId));
                rowData.put("stringField", FIRST_STRING_FIELD_VALUE + participantInfo.getId());
                rowData.put("multiLineField", FIRST_MULTILINE_STRING_FIELD.replace("$", Long.toString(participantId)));

                if ((participantId & 1) == 0)
                    rowData.put("booleanField", "true");
                else
                    rowData.put("booleanField", "false");

                rowData.put("integerField", Long.toString(participantId + FIRST_INT_OFFSET));

                rowData.put("doubleField", participantInfo.getId() + FIRST_MANTISSA);

                rowData.put("dateTimeField", FIRST_DATE);

                rowData.put("flagField", FIRST_FLAG_FIELD + participantInfo.getId());

                rowData.put("user", userName);

                _listHelper.insertNewRow(rowData);
            }
        }

        ReadResponseTest.participantWithMultipleRow = new ParticipantInfo(participantsInfo.get(0).getId(), participantsInfo.get(0).getAppToken());

        log("Now add a few more rows in the list for participant: " + ReadResponseTest.participantWithMultipleRow.getId() + " (" + ReadResponseTest.participantWithMultipleRow.getAppToken() + "). This is the only participant with multiple rows in the list.");

        participantId = ReadResponseTest.participantWithMultipleRow.getId();

        rowData = new HashMap<>();
        rowData.put("participantId", Long.toString(participantId));
        rowData.put("stringField", SECOND_STRING_FIELD_VALUE + ReadResponseTest.participantWithMultipleRow.getId());
        rowData.put("multiLineField", SECOND_MULTILINE_STRING_FIELD.replace("$", Long.toString(participantId)));
        rowData.put("booleanField", "true");
        rowData.put("integerField", Long.toString(participantId + SECOND_INT_OFFSET));
        rowData.put("doubleField", ReadResponseTest.participantWithMultipleRow.getId() + SECOND_MANTISSA);
        rowData.put("dateTimeField", SECOND_DATE);
        rowData.put("flagField", SECOND_FLAG_FIELD + ReadResponseTest.participantWithMultipleRow.getId());
        rowData.put("user", userName);
        _listHelper.insertNewRow(rowData);

        rowData = new HashMap<>();
        rowData.put("participantId", Long.toString(participantId));
        rowData.put("stringField", THIRD_STRING_FIELD_VALUE + ReadResponseTest.participantWithMultipleRow.getId());
        rowData.put("multiLineField", THIRD_MULTILINE_STRING_FIELD.replace("$", Long.toString(participantId)));
        rowData.put("booleanField", "false");
        rowData.put("integerField", Long.toString(participantId + THIRD_INT_OFFSET));
        rowData.put("doubleField", ReadResponseTest.participantWithMultipleRow.getId() + THIRD_MANTISSA);
        rowData.put("dateTimeField", THIRD_DATE);
        rowData.put("flagField", THIRD_FLAG_FIELD + ReadResponseTest.participantWithMultipleRow.getId());
        rowData.put("user", userName);
        _listHelper.insertNewRow(rowData);

        ReadResponseTest.participantWithOneRow = new ParticipantInfo(participantsInfo.get(1).getId(), participantsInfo.get(1).getAppToken());

        // Nothing particularly special about this participant, except that their integerField value will be the same as participantWithMultipleRow.
        ReadResponseTest.participantForSql = new ParticipantInfo(participantsInfo.get(participantsInfo.size() - 1).getId(), participantsInfo.get(participantsInfo.size() - 1).getAppToken());

        log("Create a simple second list that has no participantId but will work as a look-up.");
        goToProjectHome();
        integerTypeColumn = new ListHelper.ListColumn("integerField", "integerField", ListHelper.ListColumnType.Integer, "");
        stringTypeColumn = new ListHelper.ListColumn("Description", "Description", ListHelper.ListColumnType.String, "");

        _listHelper.createList(getProjectName(), LIST_SECOND, ListHelper.ListColumnType.AutoInteger, "Key", integerTypeColumn, stringTypeColumn);
        clickButton("Done");

        clickAndWait(Locator.linkWithText(LIST_SECOND));

        log("Now add two rows to " + LIST_SECOND);

        long idAsLong;
        idAsLong = ReadResponseTest.participantWithMultipleRow.getId();

        rowData = new HashMap<>();
        rowData.put("Description", DESCRIPTION_VALUE_SECOND_LIST + (idAsLong + FIRST_INT_OFFSET));
        rowData.put("integerField", Long.toString(idAsLong + FIRST_INT_OFFSET));
        _listHelper.insertNewRow(rowData);
        rowData = new HashMap<>();
        rowData.put("Description", DESCRIPTION_VALUE_SECOND_LIST + (idAsLong + SECOND_INT_OFFSET));
        rowData.put("integerField", Long.toString(idAsLong + SECOND_INT_OFFSET));
        _listHelper.insertNewRow(rowData);

        log("Create a third list that has a participantId column.");
        goToProjectHome();
        participantIdColumn = new ListHelper.ListColumn("participantId", "participantId", ListHelper.ListColumnType.Integer, "");
        integerTypeColumn = new ListHelper.ListColumn("integerField", "integerField", ListHelper.ListColumnType.Integer, "");
        stringTypeColumn = new ListHelper.ListColumn("Description", "Description", ListHelper.ListColumnType.String, "");

        _listHelper.createList(getProjectName(), LIST_THIRD, ListHelper.ListColumnType.AutoInteger, "Key", participantIdColumn, integerTypeColumn, stringTypeColumn);
        clickButton("Done");

        clickAndWait(Locator.linkWithText(LIST_THIRD));

        log("Now add a couple of rows to " + LIST_THIRD);

        idAsLong = ReadResponseTest.participantWithMultipleRow.getId();

        rowData = new HashMap<>();
        rowData.put("participantId", Long.toString(idAsLong));
        rowData.put("Description", DESCRIPTION_VALUE_THIRD_LIST + (idAsLong + SECOND_INT_OFFSET));
        rowData.put("integerField", Long.toString(idAsLong + SECOND_INT_OFFSET));
        _listHelper.insertNewRow(rowData);

        idAsLong = ReadResponseTest.participantForSql.getId();
        rowData = new HashMap<>();
        rowData.put("participantId", Long.toString(idAsLong));
        rowData.put("Description", DESCRIPTION_VALUE_THIRD_LIST + (idAsLong + FIRST_INT_OFFSET));
        rowData.put("integerField", Long.toString(idAsLong + FIRST_INT_OFFSET));
        _listHelper.insertNewRow(rowData);

        idAsLong = ReadResponseTest.participantWithOneRow.getId();
        rowData = new HashMap<>();
        rowData.put("participantId", Long.toString(idAsLong));
        rowData.put("Description", DESCRIPTION_VALUE_THIRD_LIST + (idAsLong + FIRST_INT_OFFSET));
        rowData.put("integerField", Long.toString(idAsLong + FIRST_INT_OFFSET));
        _listHelper.insertNewRow(rowData);

        log("Done creating the lists.");

    }

    private List<ParticipantInfo> getTokens()
    {
        List<ParticipantInfo> _participantInfos = new ArrayList<>();

        try
        {

            Connection cn = createDefaultConnection(false);
            SelectRowsCommand selectCmd = new SelectRowsCommand("mobileappstudy", "Participant");
            SelectRowsResponse rowsResponse = selectCmd.execute(cn, getProjectName());
            log("Row count: " + rowsResponse.getRows().size());

            for(Map<String, Object> row: rowsResponse.getRows())
            {
                _participantInfos.add(new ParticipantInfo(Integer.parseInt(row.get("RowId").toString()), row.get("AppToken").toString()));
            }
        }
        catch(CommandException ce)
        {
            fail("Command exception when running query: " + ce);
        }
        catch(IOException ioe)
        {
            fail("IO exception when running query: " + ioe);
        }

        log("Number of participants with AppTokens: " + _participantInfos.size());

        return _participantInfos;
    }

    @Test
    public void validateSelectRowsWithMultipleRows() throws CommandException, IOException
    {

        log("Call selectRows with participant " + ReadResponseTest.participantWithMultipleRow.getId() + " (" + ReadResponseTest.participantWithMultipleRow.getAppToken() + "). This participant should return multiple rows.");
        goToProjectHome();
        String participantAppToken = ReadResponseTest.participantWithMultipleRow.getAppToken();

        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("query.columns", "participantId, stringField, multiLineField, booleanField, integerField, doubleField, dateTimeField, flagField");
        params.put("participantId", participantAppToken);

        log("Columns parameter: " + params.get("query.columns"));

        CommandResponse rowsResponse = callSelectRows(params);

        log("Validate 3 rows were returned.");

        JSONArray jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + ReadResponseTest.participantWithMultipleRow.getId() + " (" + ReadResponseTest.participantWithMultipleRow.getAppToken() + ") not as expected.", 3, jsonArray.size());

        log("Validate the first item returned in the json.");
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("participantId", ReadResponseTest.participantWithMultipleRow.getId());
        expectedValues.put("stringField", FIRST_STRING_FIELD_VALUE + ReadResponseTest.participantWithMultipleRow.getId());
        expectedValues.put("multiLineField", FIRST_MULTILINE_STRING_FIELD.replace("$", Long.toString(ReadResponseTest.participantWithMultipleRow.getId())));

        long idAsInt = ReadResponseTest.participantWithMultipleRow.getId();
        if ((idAsInt & 1) == 0)
            expectedValues.put("booleanField", true);
        else
            expectedValues.put("booleanField", false);

        expectedValues.put("integerField", idAsInt + FIRST_INT_OFFSET);
        expectedValues.put("doubleField", Double.parseDouble(ReadResponseTest.participantWithMultipleRow.getId() + FIRST_MANTISSA));
        expectedValues.put("dateTimeField", FIRST_DATE);
        expectedValues.put("flagField", FIRST_FLAG_FIELD + ReadResponseTest.participantWithMultipleRow.getId());

        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        log("Validate the second item returned in the json.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", ReadResponseTest.participantWithMultipleRow.getId());
        expectedValues.put("stringField", SECOND_STRING_FIELD_VALUE + ReadResponseTest.participantWithMultipleRow.getId());
        expectedValues.put("multiLineField", SECOND_MULTILINE_STRING_FIELD.replace("$", Long.toString(ReadResponseTest.participantWithMultipleRow.getId())));
        expectedValues.put("booleanField", true);
        expectedValues.put("integerField", idAsInt + SECOND_INT_OFFSET);
        expectedValues.put("doubleField", Double.parseDouble(ReadResponseTest.participantWithMultipleRow.getId() + SECOND_MANTISSA));
        expectedValues.put("dateTimeField", SECOND_DATE);
        expectedValues.put("flagField", SECOND_FLAG_FIELD + ReadResponseTest.participantWithMultipleRow.getId());

        jsonObject = (JSONObject)jsonArray.get(1);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        log("Validate the third item returned in the json.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", ReadResponseTest.participantWithMultipleRow.getId());
        expectedValues.put("stringField", THIRD_STRING_FIELD_VALUE + ReadResponseTest.participantWithMultipleRow.getId());
        expectedValues.put("multiLineField", THIRD_MULTILINE_STRING_FIELD.replace("$", Long.toString(ReadResponseTest.participantWithMultipleRow.getId())));
        expectedValues.put("booleanField", false);
        expectedValues.put("integerField", idAsInt + THIRD_INT_OFFSET);
        expectedValues.put("doubleField", Double.parseDouble(ReadResponseTest.participantWithMultipleRow.getId() + THIRD_MANTISSA));
        expectedValues.put("dateTimeField", THIRD_DATE);
        expectedValues.put("flagField", THIRD_FLAG_FIELD + ReadResponseTest.participantWithMultipleRow.getId());

        jsonObject = (JSONObject)jsonArray.get(2);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        log("Looks good. Go home.");
        goToHome();
    }

    @Test
    public void validateSelectRowsWithOneRow() throws CommandException, IOException
    {

        log("Call selectRows with participant " + ReadResponseTest.participantWithOneRow.getId() + " (" + ReadResponseTest.participantWithOneRow.getAppToken() + "). This participant should return one row.");
        goToProjectHome();
        String participantAppToken = ReadResponseTest.participantWithOneRow.getAppToken();

        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("query.columns", "participantId, stringField, multiLineField, booleanField, integerField, doubleField, dateTimeField, flagField");
        params.put("participantId", participantAppToken);

        log("Columns parameter: " + params.get("query.columns"));

        CommandResponse rowsResponse = callSelectRows(params);

        log("Validate that 1 row is returend.");

        JSONArray jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + ReadResponseTest.participantWithOneRow.getId() + " (" + ReadResponseTest.participantWithOneRow.getAppToken() + ") not as expected.", 1, jsonArray.size());

        log("Validate the row returned.");
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("participantId", ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("stringField", FIRST_STRING_FIELD_VALUE + ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("multiLineField", FIRST_MULTILINE_STRING_FIELD.replace("$", Long.toString(ReadResponseTest.participantWithOneRow.getId())));

        long idAsInt = ReadResponseTest.participantWithOneRow.getId();
        if ((idAsInt & 1) == 0)
            expectedValues.put("booleanField", true);
        else
            expectedValues.put("booleanField", false);

        expectedValues.put("integerField", idAsInt + FIRST_INT_OFFSET);
        expectedValues.put("doubleField", Double.parseDouble(ReadResponseTest.participantWithOneRow.getId() + FIRST_MANTISSA));
        expectedValues.put("dateTimeField", FIRST_DATE);
        expectedValues.put("flagField", FIRST_FLAG_FIELD + ReadResponseTest.participantWithOneRow.getId());

        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        log("Looks good. Go home.");
        goToHome();
    }

    @Test
    public void validateSelectRowsWithNoRows() throws CommandException, IOException
    {

        log("Call selectRows with participant " + ReadResponseTest.participantToSkip.getId() + " (" + ReadResponseTest.participantToSkip.getAppToken() + "). This participant has no rows in the list.");
        goToProjectHome();
        String participantAppToken = ReadResponseTest.participantToSkip.getAppToken();

        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("query.columns", "participantId, stringField, multiLineField, booleanField, integerField, doubleField, dateTimeField, flagField");
        params.put("participantId", participantAppToken);

        log("Columns parameter: " + params.get("query.columns"));

        CommandResponse rowsResponse = callSelectRows(params);

        log("Validate that no rows are returned.");

        JSONArray jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + ReadResponseTest.participantToSkip.getId() + " (" + ReadResponseTest.participantToSkip.getAppToken() + ") not as expected.", 0, jsonArray.size());

        log("Looks good. Go home.");
        goToHome();
    }

    @Test
    public void validateSelectRowsColumnParameter() throws CommandException, IOException
    {

        String columnsToReturn = "integerField, participantId, stringField, dateTimeField, multiLineField";

        log("Call selectRows with participant " + ReadResponseTest.participantWithOneRow.getId() + " (" + ReadResponseTest.participantWithOneRow.getAppToken() + "). This participant should return one row.");
        log("Only these columns '" + columnsToReturn + "' should be returned.");

        goToProjectHome();
        String participantAppToken = ReadResponseTest.participantWithOneRow.getAppToken();

        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("query.columns", columnsToReturn);
        params.put("participantId", participantAppToken);

        log("Columns parameter: " + params.get("query.columns"));

        CommandResponse rowsResponse = callSelectRows(params);

        JSONArray jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + ReadResponseTest.participantWithOneRow.getId() + " (" + ReadResponseTest.participantWithOneRow.getAppToken() + ") not as expected.", 1, jsonArray.size());

        log("Validate row returned. Verify that only the expected columns are returned.");

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("participantId", ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("stringField", FIRST_STRING_FIELD_VALUE + ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("multiLineField", FIRST_MULTILINE_STRING_FIELD.replace("$", Long.toString(ReadResponseTest.participantWithOneRow.getId())));

        long idAsInt = ReadResponseTest.participantWithOneRow.getId();
        expectedValues.put("integerField", idAsInt + FIRST_INT_OFFSET);

        expectedValues.put("dateTimeField", FIRST_DATE);

        JSONObject jsonObject = (JSONObject)jsonArray.get(0);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        log("Call selectRows with no columns parameter, this should return all columns.");

        params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("participantId", participantAppToken);

        rowsResponse = callSelectRows(params);

        log("Validate that 1 row.");

        jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + ReadResponseTest.participantWithOneRow.getId() + " (" + ReadResponseTest.participantWithOneRow.getAppToken() + ") not as expected.", 1, jsonArray.size());

        log("Validate that the row returned has all of the columns.");

        expectedValues = new HashMap<>();
        expectedValues.put("participantId", ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("stringField", FIRST_STRING_FIELD_VALUE + ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("multiLineField", FIRST_MULTILINE_STRING_FIELD.replace("$", Long.toString(ReadResponseTest.participantWithOneRow.getId())));

        if ((idAsInt & 1) == 0)
            expectedValues.put("booleanField", true);
        else
            expectedValues.put("booleanField", false);

        expectedValues.put("integerField", idAsInt + FIRST_INT_OFFSET);
        expectedValues.put("doubleField", Double.parseDouble(ReadResponseTest.participantWithOneRow.getId() + FIRST_MANTISSA));
        expectedValues.put("dateTimeField", FIRST_DATE);
        expectedValues.put("flagField", FIRST_FLAG_FIELD + ReadResponseTest.participantWithOneRow.getId());

        APIUserHelper userHelper = new APIUserHelper(this);
        int userId = userHelper.getUserId(getCurrentUser());
        expectedValues.put("user", userId);

        jsonObject = (JSONObject)jsonArray.get(0);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        log("Now call selectRows with only bad column names.");
        params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("query.columns", "foo, bar");
        params.put("participantId", participantAppToken);

        log("Columns parameter: " + params.get("query.columns"));

        rowsResponse = callSelectRows(params);

        log("Validate that 1 row is returned.");

        jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + ReadResponseTest.participantWithOneRow.getId() + " (" + ReadResponseTest.participantWithOneRow.getAppToken() + ") not as expected.", 1, jsonArray.size());

        log("Since only invalid column names were passed no columns should be returned (other than the 'Key' column).");

        // If only invalid columns were provided no columns should be returned.
        expectedValues = new HashMap<>();

        jsonObject = (JSONObject)jsonArray.get(0);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        columnsToReturn = "integerField, participantId, foo, stringField, dateTimeField, bar, multiLineField";

        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("query.columns", columnsToReturn);
        params.put("participantId", participantAppToken);

        log("Now validate with a mix of valid and invalid columns. Column parameter: '" + params.get("query.columns") + "'.");

        rowsResponse = callSelectRows(params);

        log("Validate that 1 row is returned.");

        jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + ReadResponseTest.participantWithOneRow.getId() + " (" + ReadResponseTest.participantWithOneRow.getAppToken() + ") not as expected.", 1, jsonArray.size());

        log("Validate row returned.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("stringField", FIRST_STRING_FIELD_VALUE + ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("multiLineField", FIRST_MULTILINE_STRING_FIELD.replace("$", Long.toString(ReadResponseTest.participantWithOneRow.getId())));

        expectedValues.put("integerField", idAsInt + FIRST_INT_OFFSET);

        expectedValues.put("dateTimeField", FIRST_DATE);

        jsonObject = (JSONObject)jsonArray.get(0);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        log("Look at the 'special' columns. Specifically CreatedBy, ModifiedBy and Container. These are columns with FK into other lists outside of this project.");

        String containerId = getContainerId();
        userId = userHelper.getUserId(getCurrentUser());

        columnsToReturn = "participantId, CreatedBy, ModifiedBy, container";
        log("Column parameter: '" + columnsToReturn + "'.");

        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("query.columns", columnsToReturn);
        params.put("participantId", participantAppToken);

        rowsResponse = callSelectRows(params);

        log("Validate 1 row was returned.");

        jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + ReadResponseTest.participantWithOneRow.getId() + " (" + ReadResponseTest.participantWithOneRow.getAppToken() + ") not as expected.", 1, jsonArray.size());

        log("Validate that the columns have the expected values.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", ReadResponseTest.participantWithOneRow.getId());
        expectedValues.put("CreatedBy", userId);
        expectedValues.put("ModifiedBy", userId);
        expectedValues.put("container", containerId);

        jsonObject = (JSONObject)jsonArray.get(0);
        checkJsonObjectAgainstExpectedValues(expectedValues, jsonObject);

        log("Looks good. Go home.");
        goToHome();

    }

    @Test
    public void validateSelectRowsErrorConditions() throws CommandException, IOException
    {

        final String ERROR_NO_PARTICIPANTID = "ParticipantId not included in request";
        goToProjectHome();

        log("Call selectRows without a participantId.");
        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("query.queryName", LIST_DIFF_DATATYPES);
        params.put("query.columns", "participantId, stringField, multiLineField, booleanField, integerField, doubleField, dateTimeField, flagField");

        try
        {
            callSelectRows(params);
        }
        catch(CommandException ce)
        {
            Assert.assertTrue("Command exception did not include expected message: ", ce.getMessage().equals(ERROR_NO_PARTICIPANTID));
        }

        log("Looks good. Go home.");
        goToHome();

    }

    @Test
    public void validateExecuteSqlBasic() throws CommandException, IOException
    {
        Long participantId = ReadResponseTest.participantWithMultipleRow.getId();
        String participantAppToken = ReadResponseTest.participantWithMultipleRow.getAppToken();
        String sql = "select * from TestListDiffDataTypes";

        log("Call the executeSql action with sql: '" + sql + "' and participant " + participantId + " (" + participantAppToken + ").");
        goToProjectHome();

        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        params.put("participantId", participantAppToken);

        CommandResponse rowsResponse = callExecuteSql(params);

        log("Validate 3 rows were returned.");

        JSONArray jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + participantId + " (" + participantAppToken + ") not as expected.", 3, jsonArray.size());

        log("Validate the first item returned in the json.");
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("participantId", participantId);
        expectedValues.put("stringField", FIRST_STRING_FIELD_VALUE + participantId);
        expectedValues.put("multiLineField", FIRST_MULTILINE_STRING_FIELD.replace("$", Long.toString(participantId)));

        if ((participantId & 1) == 0)
            expectedValues.put("booleanField", true);
        else
            expectedValues.put("booleanField", false);

        expectedValues.put("integerField", participantId + FIRST_INT_OFFSET);
        expectedValues.put("doubleField", Double.parseDouble(participantId + FIRST_MANTISSA));
        expectedValues.put("dateTimeField", FIRST_DATE);
        expectedValues.put("flagField", FIRST_FLAG_FIELD + participantId);

        String containerId = getContainerId();
        APIUserHelper userHelper = new APIUserHelper(this);
        int userId = userHelper.getUserId(getCurrentUser());

        expectedValues.put("CreatedBy", userId);
        expectedValues.put("user", userId);
        expectedValues.put("ModifiedBy", userId);
        expectedValues.put("container", containerId);

        JSONObject jsonArrayEntry = (JSONObject)jsonArray.get(0);
        JSONObject jsonData = (JSONObject)jsonArrayEntry.get("data");

        checkJsonObjectAgainstExpectedValues(expectedValues, jsonData);

        log("Looks good. Go home.");
        goToHome();

    }

    @Test
    public void validateExecuteSqlNoRows() throws CommandException, IOException
    {
        Long participantId = ReadResponseTest.participantToSkip.getId();
        String participantAppToken = ReadResponseTest.participantToSkip.getAppToken();
        String sql = "select * from TestListDiffDataTypes";

        log("Call the executeSql action with sql: '" + sql + "' and participant " + participantId + " (" + participantAppToken + "). This participant has no rows in the list.");
        goToProjectHome();

        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        params.put("participantId", participantAppToken);

        CommandResponse rowsResponse = callExecuteSql(params);

        log("Validate no rows were returned.");

        JSONArray jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + participantId + " (" + participantAppToken + ") not as expected.", 0, jsonArray.size());

        log("Looks good. Go home.");
        goToHome();
    }

    @Test
    public void validateExecuteSqlJoinAndUnion() throws CommandException, IOException
    {

        goToProjectHome();

        long participantId = ReadResponseTest.participantWithMultipleRow.getId();
        String participantAppToken = ReadResponseTest.participantWithMultipleRow.getAppToken();

        log("First validate with a join clause.");
        String sql = "select SecondSimpleList.integerField, SecondSimpleList.Description, TestListDiffDataTypes.participantId from TestListDiffDataTypes inner join SecondSimpleList on SecondSimpleList.integerField = TestListDiffDataTypes.integerField";
        log("Call the executeSql action with sql: '" + sql + "' and participant " + participantId + " (" + participantAppToken + ").");

        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        params.put("participantId", participantAppToken);

        CommandResponse rowsResponse = callExecuteSql(params);

        log("Validate that 2 rows were returned.");

        JSONArray jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + participantId + " (" + participantAppToken + ") not as expected.", 2, jsonArray.size());

        log("Validate the first item returned in the json.");
        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("participantId", participantId);
        expectedValues.put("Description", DESCRIPTION_VALUE_SECOND_LIST + Long.toString(participantId + FIRST_INT_OFFSET));
        expectedValues.put("integerField", participantId + FIRST_INT_OFFSET);

        JSONObject jsonArrayEntry = (JSONObject)jsonArray.get(0);
        JSONObject jsonData = (JSONObject)jsonArrayEntry.get("data");

        checkJsonObjectAgainstExpectedValues(expectedValues, jsonData);

        log("Validate the second item returned in the json.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", participantId);
        expectedValues.put("Description", DESCRIPTION_VALUE_SECOND_LIST + Long.toString(participantId + SECOND_INT_OFFSET));
        expectedValues.put("integerField", participantId + SECOND_INT_OFFSET);

        jsonArrayEntry = (JSONObject)jsonArray.get(1);
        jsonData = (JSONObject)jsonArrayEntry.get("data");

        checkJsonObjectAgainstExpectedValues(expectedValues, jsonData);

        log("Now call executeSql with a union clause.");

        participantId = ReadResponseTest.participantForSql.getId();
        participantAppToken = ReadResponseTest.participantForSql.getAppToken();

        sql = "select participantId, integerField, stringField from TestListDiffDataTypes UNION select participantId, integerField, Description from ThirdList order by integerField";
        log("Call the executeSql action with sql: '" + sql + "' and participant " + participantId + " (" + participantAppToken + ").");
        goToProjectHome();

        params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        params.put("participantId", participantAppToken);

        rowsResponse = callExecuteSql(params);

        log("Validate 2 rows returned.");

        jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + participantId + " (" + participantAppToken + ") not as expected.", 2, jsonArray.size());

        log("Validate the first item returned in the json.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", participantId);
        expectedValues.put("stringField", DESCRIPTION_VALUE_THIRD_LIST + Long.toString(participantId + FIRST_INT_OFFSET));
        expectedValues.put("integerField", participantId + FIRST_INT_OFFSET);

        jsonArrayEntry = (JSONObject)jsonArray.get(0);
        jsonData = (JSONObject)jsonArrayEntry.get("data");

        checkJsonObjectAgainstExpectedValues(expectedValues, jsonData);

        log("Validate the second item returned in the json.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", participantId);
        expectedValues.put("stringField", FIRST_STRING_FIELD_VALUE + participantId);
        expectedValues.put("integerField", participantId + FIRST_INT_OFFSET);

        jsonArrayEntry = (JSONObject)jsonArray.get(1);
        jsonData = (JSONObject)jsonArrayEntry.get("data");

        checkJsonObjectAgainstExpectedValues(expectedValues, jsonData);

        log("Finally validate a join clause that includes all three lists.");

        participantId = ReadResponseTest.participantWithMultipleRow.getId();
        participantAppToken = ReadResponseTest.participantWithMultipleRow.getAppToken();

        sql = "select TestListDiffDataTypes.participantId, ThirdList.integerField, SecondSimpleList.Description from ((TestListDiffDataTypes inner join ThirdList on ThirdList.participantId = TestListDiffDataTypes.participantId) inner join SecondSImpleList on TestListDiffDataTypes.integerField = SecondSimpleList.integerField)";
        log("Call the executeSql action with sql: '" + sql + "' and participant " + participantId + " (" + participantAppToken + "). This participant has no rows in the list.");
        goToProjectHome();

        params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        params.put("participantId", participantAppToken);

        rowsResponse = callExecuteSql(params);

        log("Again validate that 2 rows are returned.");

        jsonArray = rowsResponse.getProperty("rows");
        Assert.assertEquals("Number of rows returned for participant " + participantId + " (" + participantAppToken + ") not as expected.", 2, jsonArray.size());

        log("Validate the first item returned in the json.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", participantId);
        expectedValues.put("Description", DESCRIPTION_VALUE_SECOND_LIST + Long.toString(participantId + FIRST_INT_OFFSET));
        expectedValues.put("integerField", participantId + SECOND_INT_OFFSET);

        jsonArrayEntry = (JSONObject)jsonArray.get(0);
        jsonData = (JSONObject)jsonArrayEntry.get("data");

        checkJsonObjectAgainstExpectedValues(expectedValues, jsonData);

        log("Validate the second item returned in the json.");
        expectedValues = new HashMap<>();
        expectedValues.put("participantId", participantId);
        expectedValues.put("Description", DESCRIPTION_VALUE_SECOND_LIST + Long.toString(participantId + SECOND_INT_OFFSET));
        expectedValues.put("integerField", participantId + SECOND_INT_OFFSET);

        jsonArrayEntry = (JSONObject)jsonArray.get(1);
        jsonData = (JSONObject)jsonArrayEntry.get("data");

        checkJsonObjectAgainstExpectedValues(expectedValues, jsonData);

        log("Looks good. Go home.");
        goToHome();

    }

    @Test
    public void validateExecuteSqlErrorConditions() throws CommandException, IOException
    {

        String sql = "select * from TestListDiffDataTypes";
        final String ERROR_NO_PARTICIPANTID = "ParticipantId not included in request";
        final String ERROR_TABLE_NOT_FOUND = "Query or table not found: core.Users";
        final String ERROR_INVALID_TOKEN = "Unexpected token:";

        goToProjectHome();

        log("Call executeSql without a participantId.");
        Map<String, Object> params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        log("Call the executeSql action with sql: '" + sql + "' and no participant parameter.");

        try
        {
            callExecuteSql(params);
        }
        catch(CommandException ce)
        {
            Assert.assertTrue("Command exception did not include expected message: ", ce.getMessage().equals(ERROR_NO_PARTICIPANTID));
        }

        log("Call executeSql looking only at an 'external' table.");
        long participantId = ReadResponseTest.participantWithMultipleRow.getId();
        String participantAppToken = ReadResponseTest.participantWithMultipleRow.getAppToken();
        sql = "select * from core.Users";
        params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        params.put("participantId", participantAppToken);

        log("Call executeSql action with sql: '" + sql + "' and participant " + participantId + " (" + participantAppToken + ").");

        try
        {
            callExecuteSql(params);
        }
        catch(CommandException ce)
        {
            Assert.assertTrue("Command exception did not include expected message: ", ce.getMessage().contains(ERROR_TABLE_NOT_FOUND));
        }

        log("Call the executeSql while joining to an 'external' table.");
        sql = "select TestListDiffDataTypes.participantId, TestListDiffDataTypes.user, core.Users.email from TestListDiffDataTypes inner join core.Users on TestListDiffDataTypes.user = core.Users.DisplayName";
        params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        params.put("participantId", participantAppToken);

        log("Call executeSql action with sql: '" + sql + "' and participant " + participantId + " (" + participantAppToken + ").");

        try
        {
            callExecuteSql(params);
        }
        catch(CommandException ce)
        {
            Assert.assertTrue("Command exception did not include expected message: ", ce.getMessage().contains(ERROR_TABLE_NOT_FOUND));
        }

        log("Call the executeSql with garbage as the sql.");
        sql = "select this should never ever ever work!";
        params = new HashMap<>();
        params.put("schemaName", "MobileAppResponse");
        params.put("sql", sql);
        params.put("participantId", participantAppToken);

        log("Call executeSql action with sql: '" + sql + "' and participant " + participantId + " (" + participantAppToken + ").");

        try
        {
            callExecuteSql(params);
        }
        catch(CommandException ce)
        {
            Assert.assertTrue("Command exception did not include expected message: ", ce.getMessage().contains(ERROR_INVALID_TOKEN));
        }

        log("Looks good. Go home.");
        goToHome();

    }

    private void confirmBatchInfoCreated(SetupPage setupPage, String batchId, String expectedTokenCount, String expectedUsedCount)
    {
        Map<String, String> batchData = setupPage.getTokenBatchesWebPart().getBatchData(batchId);

        assertEquals("BatchId not as expected.", batchId, batchData.get("RowId"));
        assertEquals("Expected number of tokens not created.", expectedTokenCount, batchData.get("Count"));
        assertEquals("Number of tokens in use not as expected.", expectedUsedCount, batchData.get("TokensInUse"));
    }

    private CommandResponse callSelectRows(Map<String, Object> params) throws IOException, CommandException
    {
        return callCommand("mobileAppStudy", "selectRows", params);
    }

    private CommandResponse callExecuteSql(Map<String, Object> params) throws IOException, CommandException
    {
        return callCommand("mobileAppStudy", "executeSql", params);
    }

    private CommandResponse callCommand(String controller, String action, Map<String, Object> params)  throws IOException, CommandException
    {
        Connection cn = createDefaultConnection(false);
        Command selectCmd = new Command(controller, action);
        selectCmd.setParameters(params);

        return selectCmd.execute(cn, getProjectName());
    }

    private void checkJsonObjectAgainstExpectedValues(Map<String, Object> expectedValues, JSONObject jsonObject)
    {
        Set<String> columns = expectedValues.keySet();

        for(String column : columns)
        {
            Assert.assertTrue("Expected column " + column + " was not in the jsonObject.", jsonObject.keySet().contains(column));

            Object  jsonObjectValue;
            if(jsonObject.get(column).getClass().getSimpleName().equals("JSONObject"))
            {
                // Need to do this if the object that is being compared came from an executeSql call.
                JSONObject jObject = (JSONObject)jsonObject.get(column);
                jsonObjectValue = jObject.get("value");
            }
            else
                jsonObjectValue = jsonObject.get(column);

            log("Validating column '" + column + "' which is a '" + jsonObjectValue.getClass().getName() + "' data type.");
//            log("Type of value returned by json: " + jsonObjectValue.getClass().getName());
//            log("Type of value expected: " + expectedValues.get(column).getClass().getName());

            switch(expectedValues.get(column).getClass().getSimpleName())
            {
                case "Integer":

                    // There is this odd case where the field is an integer but the json returns a long.
                    // Not worth worrying about, but will need to account for.
                    if(jsonObjectValue.getClass().getSimpleName().equals("Long"))
                    {
                        log("Have to do a caste. Expected field is an int, the json returned a long.");
                        long temp = (int)expectedValues.get(column);
                        Assert.assertEquals(column + " not as expected.", temp, jsonObjectValue);
                    }
                    else
                        Assert.assertEquals(column + " not as expected.", expectedValues.get(column), jsonObjectValue);

                    break;
                case "Double":
                    Assert.assertEquals(column + " not as expected.", Double.parseDouble(expectedValues.get(column).toString()), (double)jsonObjectValue, 0.0);
                    break;
                case "Boolean":
                    if ((boolean)expectedValues.get("booleanField"))
                        Assert.assertTrue("booleanField was not true (as expected).",(boolean)jsonObjectValue);
                    else
                        Assert.assertFalse("booleanField was not false (as expected).",(boolean)jsonObjectValue);
                    break;
                default:
                    // Long and String are the only types that don't need some kind of special casing.
                    Assert.assertEquals(column + " not as expected.", expectedValues.get(column), jsonObjectValue);
                    break;
            }

        }

        // If we've gotten to this point then we know that all of the expected columns and values were there.
        // Now we need to check that the jsonObject did not return any unexpected columns.
        String unexpectedJsonColum = "";
        boolean pass = true;
        for(Object jsonColumn : jsonObject.keySet())
        {
            // If the query returned all columns there are a few columns to ignore.
            // Ignore the 'Created', 'Key', 'EntityId', 'lastIndexed' and 'Modified' fields. These fields can be tricky to get an accurate expected value especially the timestamp fields.
            if((!expectedValues.keySet().contains(jsonColumn)) &&
                    (!jsonColumn.equals("Key") &&
                    !jsonColumn.equals("Created") &&
                    !jsonColumn.equals("Modified") &&
                    !jsonColumn.equals("lastIndexed") &&
                    !jsonColumn.equals("EntityId")))
            {
                unexpectedJsonColum = unexpectedJsonColum + "Found unexpected column '" + jsonColumn.toString() + "' in jsonObject.\r\n";
                pass = false;
            }
        }

        Assert.assertTrue(unexpectedJsonColum, pass);

    }

    private SelectRowsResponse getListInfo(String schema, String query) throws IOException, CommandException
    {
        Connection cn;
        SelectRowsCommand selectCmd  = new SelectRowsCommand(schema, query);
        cn = createDefaultConnection(false);
        return  selectCmd.execute(cn, getProjectName());
    }

    private class ParticipantInfo
    {
        protected long _participantId;
        protected String _appToken;

        public ParticipantInfo()
        {
            // Do nothing constructor.
        }

        public ParticipantInfo(long participantId, String appToken)
        {
            _participantId = participantId;
            _appToken = appToken;
        }

        public void setId(long id)
        {
            _participantId = id;
        }

        public long getId()
        {
            return _participantId;
        }

        public void setAppToken(String appToken)
        {
            _appToken = appToken;
        }

        public String getAppToken()
        {
            return _appToken;
        }

    }

}
