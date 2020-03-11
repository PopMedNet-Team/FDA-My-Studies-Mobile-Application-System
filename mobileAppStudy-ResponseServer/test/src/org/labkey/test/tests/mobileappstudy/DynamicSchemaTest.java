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
import org.labkey.test.TestFileUtils;
import org.labkey.test.categories.Git;
import org.labkey.test.commands.mobileappstudy.SubmitResponseCommand;
import org.labkey.test.pages.list.BeginPage;
import org.labkey.test.pages.mobileappstudy.ResponseQueryPage;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.util.PortalHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category({Git.class})
public class DynamicSchemaTest extends BaseMobileAppStudyTest
{
    private static final String PROJECT_NAME = "DynamicSchemaTestProject";
    private static final String STUDY_NAME = "DYNAMICSCHEMASTUDY";
    private static final String SURVEY_NAME = "NewSurvey";
    private static final String LIST_SCHEMA = "lists";

    private static List<Map<String,Object>> newSurveyMap;
    private static List<Map<String,Object>> newSurveyGroupedMap;
    private static List<Map<String,Object>> newSurveyGroupedSubGroupedMap;
    private static List<Map<String,Object>> newSurveyGroupedTextChoiceField;
    private static List<Map<String,Object>> newSurveyTextChoiceField;

    protected
    @Nullable String getProjectName()
    {
        return PROJECT_NAME;
    }

    @Override
    void setupProjects()
    {
        //Setup a study
        _containerHelper.deleteProject(PROJECT_NAME, false);
        _containerHelper.createProject(PROJECT_NAME, "Mobile App Study");
        goToProjectHome(PROJECT_NAME);
        SetupPage setupPage = new SetupPage(this);
        setupPage.getStudySetupWebPart().checkResponseCollection();
        setupPage.getStudySetupWebPart().setShortName(STUDY_NAME);
        setupPage.validateSubmitButtonEnabled();
        setupPage.getStudySetupWebPart().clickSubmit();

        //setupLists();
        setSurveyMetadataDropDir();
        goToProjectHome(PROJECT_NAME);
        PortalHelper portalHelper = new PortalHelper(this);
        portalHelper.addWebPart("Lists");

        //Send initial response to get lists into a known state
        log("Setting initial state, response txt 1");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_1--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "1", appToken, responseString);
        cmd.execute(200);

        getMobileAppDataWithRetry("NewSurvey", "lists");
    }

    @Test
    public void testAddSingleQuestion()
    {
        //adds a numeric field of type Double to NewStudy, name Double2, value 2.02
        resetListState();
        log("Submitting response with single question added. Response text 2");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_2--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "2", appToken, responseString);
        cmd.execute(200);
        waitForResults(newSurveyMap, "NewSurvey");
        assertEquals("Unexpected new row count in NewSurvey after adding single response with single question added. Response text 2",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        assertEquals("Did not find new expected column DoubleField2 in NewSurvey after adding single response with single question added. Response text 2","DoubleField2", getAddedColumns(newSurveyMap,getTableData("NewSurvey")).get(0));
        assertEquals("Unexpected number of new columns in NewSurvey after adding single response with single question added. Response text 2", 1, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single question added. Response text 2",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single question added. Response text 2",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single question added. Response text 2",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single question added. Response text 2",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question added. Response text 2",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single question added. Response text 2",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single question added. Response text 2",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single question added. Response text 2",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
    }

    @Test
    public void testDropSingleQuestion()
    {
        resetListState();
        log("Submitting response with single question removed. Response text 3");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_3--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "3", appToken, responseString);
        cmd.execute(200);
        waitForResults(newSurveyMap, "NewSurvey");
        assertEquals("Unexpected new row count in NewSurvey after adding response with single question removed. Response text 3",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        assertEquals("Unexpected number of new columns NewSurvey after adding response with single question removed. Response text 3", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after response with single question removed. Response text 3",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        assertEquals("Unexpected number of new columns NewSurveyGroupedList after adding response with single question removed. Response text 3",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after response with single question removed. Response text 3",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding response with single question removed. Response text 3",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question removed. Response text 3",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding response with single question removed. Response text 3",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after response with dropped question with single question removed. Response text 3",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding response with single question removed. Response text 3",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
    }

    @Test
    public void testAddSingleQuestionToGrouped()
    {
        resetListState();
        log("Submitting response with a single question added to group. Response text 4");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_4--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "4", appToken, responseString);
        cmd.execute(200);
        waitForResults(newSurveyMap, "NewSurvey");
        assertEquals("Unexpected new row count in NewSurvey after adding single response with a single question added to group. Response text 4",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        assertEquals("Unexpected additional column in NewSurvey after adding single response with a single question added to group. Response text 4", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with a single question added to group. Response text 4",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with a single question added to group. Response text 4",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with a single question added to group. Response text 4",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with a single question added to group. Response text 4",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with a single question added to group. Response text 4",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with a single question added to group. Response text 4",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with a single question added to group. Response text 4",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with a single question added to group. Response text 4",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
    }

    @Test
    public void testDropSingleQuestionFromGrouped()
    {
        resetListState();
        log("Submitting response with single question removed from group. Response text 5");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_5--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "5", appToken, responseString);
        cmd.execute(200);
        waitForResults(newSurveyMap, "NewSurvey");
        assertEquals("Unexpected new row count in NewSurvey after adding single response with single question removed from group. Response text 5",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        assertEquals("Unexpected additional column in NewSurvey after adding single response with single question removed from group. Response text 5 ", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single question removed from group. Response text 5",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single question removed from group. Response text 5",1, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single question removed from group. Response text 5",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single question removed from group. Response text 5",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question removed from group. Response text 5",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single question removed from group. Response text 5",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single question removed from group. Response text 5",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single question removed from group. Response text 5",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
    }

    @Test
    public void testAddSingleQuestionToSubGroupedList()
    {
        resetListState();
        log("Submitting response with single question added to sub subgroup. Response text 6");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_6--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "6", appToken, responseString);
        cmd.execute(200);
        waitForResults(newSurveyMap, "NewSurvey");
        assertEquals("Unexpected new row count in NewSurvey after adding single response with single question added to sub subgroup. Response text 6",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        assertEquals("Unexpected additional column in NewSurvey after adding single response with single question added to sub subgroup. Response text 6", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single question added to sub subgroup. Response text 6",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single question added to sub subgroup. Response text 6",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single question added to sub subgroup. Response text 6",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single question added to sub subgroup. Response text 6",1, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question added to sub subgroup. Response text 6",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single question added to sub subgroup. Response text 6",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single question added to sub subgroup. Response text 6",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single question added to sub subgroup. Response text 6",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
    }



    @Test
    public void testDropSingleQuestionFromSubGroupedList()
    {
        resetListState();
        log("Submitting response with single question removed from sub. Response text 7");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_7--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "7", appToken, responseString);
        cmd.execute(200);
        waitForResults(newSurveyMap, "NewSurvey");
        assertEquals("Unexpected new row count in NewSurvey after adding single response with single question removed from sub. Response text 7",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        assertEquals("Unexpected number of new columns in NewSurvey after adding single response with single question removed from sub. Response text 7", 1, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single question removed from sub. Response text 7",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single question removed from sub. Response text 7",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single question removed from sub. Response text 7",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single question removed from sub. Response text 7",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question removed from sub. Response text 7",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single question removed from sub. Response text 7",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single question removed from sub. Response text 7",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single question removed from sub. Response text 7",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
    }

    @Test
    public void testAddChoice()
    {
        resetListState();
        log("Submitting response with single group added. Response text 8");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_8--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "8", appToken, responseString);
        cmd.execute(200);
        waitForResults(newSurveyMap, "NewSurvey");
        assertEquals("Unexpected new row count in NewSurvey after adding single response with single group added. Response text 8", 1, getNewRowCount(newSurveyMap, getTableData("NewSurvey")));
        assertEquals("Unexpected number of new columns in NewSurvey after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single group added. Response text 8", 1, getNewRowCount(newSurveyGroupedMap, getTableData("NewSurveyGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyGroupedMap, getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single group added. Response text 8", 1, getNewRowCount(newSurveyGroupedSubGroupedMap, getTableData("NewSurveyGroupedListSubGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyGroupedSubGroupedMap, getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single group added. Response text 8", 2, getNewRowCount(newSurveyTextChoiceField, getTableData("NewSurveyTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyTextChoiceField, getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single group added. Response text 8", 2, getNewRowCount(newSurveyGroupedTextChoiceField, getTableData("NewSurveyGroupedListTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyGroupedTextChoiceField, getTableData("NewSurveyGroupedListTextChoiceField")).size());

        assertEquals("Unexpected new row count in NewSurveyNewTextChoiceField after adding single response with new text choice field. Response text 8", 2, getTableData("NewSurveyNewTextChoiceField").size());
        assertEquals("Unexpected number of new columns in NewSurveyNewTextChoiceField with new text choice field. Response text 8", 2, getTableData("NewSurveyNewTextChoiceField").size());
    }

    @Test
    public void testRemoveGroup()
    {
        resetListState();
        log("Submitting response with group removed. Response text 9");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_9--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "9", appToken, responseString);
        cmd.execute(200);
        waitForResults(newSurveyMap, "NewSurvey");
        assertEquals("Unexpected new row count in NewSurvey after adding single response with group removed. Response text 9",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        assertEquals("Unexpected number of new columns in NewSurvey with group removed. Response text 9", 1, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with group removed. Response text 9",0,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedList with group removed. Response text 9",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response",0,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList with group removed. Response text 9",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField with group removed. Response text 9",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response",0,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField with group removed. Response text 9",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
    }

    @Test
    public void testNewGroup()
    {
        resetListState();
        log("Submitting response with group removed. Response text 13");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_13--RESPONSE.json");
        log("getResponseFromFile(\"DYNAMICSCHEMASTUDY_NewSurvey_13--RESPONSE.json\"): " + responseString);
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "13", appToken, responseString);
        cmd.execute(200);

        waitForResults(newSurveyGroupedMap, "NewSurveyGroupedList");

        StringBuilder errorMsg = new StringBuilder();

        if(getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")) != 1)
            errorMsg.append("Unexpected new row count in NewSurveyGroupedListAdded after adding single response with group and subgroup added. Response text 13. Expected 1 found " + getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")) + "\n");

        if(getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size() != 0)
            errorMsg.append("Unexpected number of new columns in NewSurveyGroupedListAdded with group and subgroup added. Response text 13. Expected 0 Found " + getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size() + "\n");

        if(getTableData("NewSurveyGroupedListAddedSubGroupedList").size() != 1)
            errorMsg.append("Unexpected new row count in NewSurveyGroupedListAddedSubGroupedList after adding single response with group and subgroup added. Response text 13. Expected 1 Found " + getTableData("NewSurveyGroupedListAddedSubGroupedList").size() + "\n");

        if(getTableData("NewSurveyGroupedListAddedSubGroupedList").size() != 1)
            errorMsg.append("Unexpected number of new columns in NewSurveyGroupedListAddedSubGroupedList with group and subgroup added. Response text 13. Expected 1 Found " + getTableData("NewSurveyGroupedListAddedSubGroupedList").size() + "\n");

        if(getTableData("NewSurveyGroupedListAddedTextChoiceField").size() != 2)
            errorMsg.append("Unexpected new row count in NewSurveyAddedTextChoiceField after adding single response with group and subgroup added. Response text 13. Expected 2 Found " + getTableData("NewSurveyGroupedListAddedTextChoiceField").size() + "\n");

        if(getTableData("NewSurveyGroupedListAddedTextChoiceField").size() != 2)
            errorMsg.append("Unexpected number of new columns in NewSurveyAddedTextChoiceField with group and subgroup added. Response text 13. Expected 2 Found " + getTableData("NewSurveyGroupedListAddedTextChoiceField").size() + "\n");

        if(getTableData("NewSurveyGroupedListAddedTextChoiceField").size() != 2)
            errorMsg.append("Unexpected new row count in NewSurveyAddedGroupedListTextChoiceField after adding single response with group and subgroup added. Response text 13. Expected 2 Found " + getTableData("NewSurveyGroupedListAddedTextChoiceField").size() + "\n");

        if(getTableData("NewSurveyGroupedListAddedTextChoiceField").size() != 2)
            errorMsg.append("Unexpected number of new columns in NewSurveyAddedGroupedListTextChoiceField with group and subgroup added. Response text 13. Expected 2 Found "+ getTableData("NewSurveyGroupedListAddedTextChoiceField").size() + "\n");

        assertTrue(errorMsg.toString(), errorMsg.length() == 0);
    }

    @Test
    public void testMismatchedResponseAndSchema()
    {

        goToProjectHome(PROJECT_NAME);

        //We do not currently confirm if the survey metadata contained in schema file matches the filename (Response survey metadata)
        resetListState();
        log("Current body text: " + getBodyText());
        log("Submitting response with mismatched schema. Response text 10");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_10--RESPONSE.json");
        log("getResponseFromFile(\"DYNAMICSCHEMASTUDY_NewSurvey_10--RESPONSE.json\"): " + responseString);
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "10", appToken, responseString); //Schema name in the metadata is: "NewSurvey_Mismatch"
        cmd.execute(200);
        sleep(5000); // wait for response shredder to have time to do its work

        goToProjectHome(PROJECT_NAME);  //refresh Project page
        refresh();

        StringBuilder errorMsg = new StringBuilder();

        if(getNewRows(newSurveyMap,getTableData("NewSurvey")).size() != 1)
            errorMsg.append("Unexpected new row count in NewSurvey after adding single response with mismatched schema. Response text 10. Expected 1 found " + getNewRows(newSurveyMap,getTableData("NewSurvey")).size() + "\n");

        if(getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size() > 1)
            errorMsg.append("Unexpected number of new columns in NewSurvey with mismatched schema. Response text 10. Expected 0 or 1 Found " + getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size() + "\n");

        if(getNewRows(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size() != 1)
            errorMsg.append("Unexpected new row count in NewSurveyGroupedList after adding single response with mismatched schema. Response text 10. Expected 1 Found " + getNewRows(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size() + "\n");

        if(getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size() != 0)
            errorMsg.append("Unexpected number of new columns in NewSurveyGroupedList with mismatched schema. Response text 10. Expected 0 Found " + getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size() + "\n");

        if(getNewRows(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size() != 1)
            errorMsg.append("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with mismatched schema. Response text 10. Expected 1 Found " + getNewRows(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size() + "\n");

        if(getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size() != 0)
            errorMsg.append("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList with mismatched schema. Response text 10. Expected 0 Found " + getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size() + "\n");

        if(getNewRows(newSurveyGroupedTextChoiceField,getTableData("NewSurveyTextChoiceField")).size() != 2)
            errorMsg.append("Unexpected new row count in NewSurveyTextChoiceField after adding single response with mismatched schema. Response text 10. Expected 2 Found " + getNewRows(newSurveyGroupedTextChoiceField,getTableData("NewSurveyTextChoiceField")).size() + "\n");

        if(getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size() != 0)
            errorMsg.append("Unexpected number of new columns in NewSurveyTextChoiceField with mismatched schema. Response text 10. Expected 0 Found " + getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size() + "\n");

        if(getNewRows(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size() != 2)
            errorMsg.append("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with mismatched schema. Response text 10. Expected 2 Found " + getNewRows(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size() + "\n");

        if(getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size() != 0)
            errorMsg.append("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with mismatched schema. Response text 10. Expected 0 Found " + getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size() + "\n");


        log("Check mismatch lists created in addition to existing lists");
        final BeginPage beginPage = goToManageLists();
        List<String> lists = beginPage.getGrid().getColumnDataAsText("Name");
        if (lists.size() < 10)
        {
            log("Not all lists present. Napping again to see if they show up.");
            sleep(5000);
            lists = beginPage.getGrid().getColumnDataAsText("Name");
        }
        List<String> finalLists = lists;
        Set<String> missingLists = Stream.of("NewSurvey", "NewSurvey_Mismatch", "NewSurvey_MismatchGroupedList",
                "NewSurvey_MismatchGroupedListSubGroupedList", "NewSurvey_MismatchGroupedListTextChoiceField",
                "NewSurvey_MismatchTextChoiceField", "NewSurveyGroupedList", "NewSurveyGroupedListSubGroupedList",
                "NewSurveyGroupedListTextChoiceField", "NewSurveyTextChoiceField")
                .filter( name -> !finalLists.contains(name))
                .collect(Collectors.toSet());
        assertEquals("Lists that should have been created are missing: " + String.join(",", missingLists), 0, missingLists.size());

        assertTrue(errorMsg.toString(), errorMsg.length() == 0);

    }


    @Test
    public void testMalformedSchema()
    {
        resetListState();
        checkErrors();
        log("Submitting response with malformed schema. Response text 11");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_11--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "11", appToken, responseString);
        cmd.execute(200);
        sleep(5000);
        assertEquals("Unexpected new row count in NewSurvey after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyMap,getTableData("NewSurvey")).size());
        assertEquals("Unexpected number of new columns in NewSurvey after addidn single response with malformed schema. Response text 11", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with malformed schema. Response text 11",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with malformed schema. Response text 11",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with malformed schema. Response text 11",0, getAddedColumns(newSurveyTextChoiceField, getTableData("NewSurveyTextChoiceField")).size());
        assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
        assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with malformed schema. Response text 11",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
        checkExpectedErrors(1);
    }


    @Test
    public void testEmptySchema()
    {
        resetListState();
        checkErrors();
        log("Submitting response with empty schema object. Response text 11");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_empty_1.0--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, "empty", "1.0", appToken, responseString);
        cmd.execute(200);
        sleep(5000);
        ResponseQueryPage responses = new ResponseQueryPage(this);
        responses.assertResponseErrorCounts(appToken, 1);
        checkExpectedErrors(1);
    }

    @Test
    public void testFetalKickCountActiveTask() throws Exception
    {
        List<Map<String, Object>> beforeTableData;
        if (!mobileAppTableExists("FKC-74counter", LIST_SCHEMA))
            beforeTableData = new ArrayList<>();
        else
            beforeTableData = getTableData("FKC-74counter");

        log("Submitting response with fetalKickCounter active task (FKC-74)");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_FKC-74_1.0--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, "FKC-74", "1.0", appToken, responseString);
        cmd.execute(200);
        sleep(5000);
        List<Map<String, Object>> afterTableData = getTableData("FKC-74counter");
        assertEquals("Unexpected new row count in FKC-74counter after adding single response.",1, getNewRowCount(beforeTableData, afterTableData));
        assertTrue("Expected field 'duration' not found", afterTableData.get(0).containsKey("duration"));
        assertTrue("Expected field 'count' not found", afterTableData.get(0).containsKey("count"));
    }

    @Test
    public void testTowersOfHanoiActiveTask() throws Exception
    {
        String tableName = "TOH-75towers";
        List<Map<String, Object>> beforeTableData;
        if (!mobileAppTableExists(tableName, LIST_SCHEMA))
            beforeTableData = new ArrayList<>();
        else
            beforeTableData = getTableData(tableName);

        log("Submitting response with towerOfHanoi active task (TOH-75)");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_TOH-75_1.0--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, "TOH-75", "1.0", appToken, responseString);
        cmd.execute(200);
        waitForResults(beforeTableData, tableName);
        List<Map<String, Object>> afterTableData = getTableData(tableName);
        assertEquals("Unexpected new row count in " + tableName + " after adding single response.",1, getNewRowCount(beforeTableData, afterTableData));
        assertTrue("Expected field 'puzzleWasSolved' not found", afterTableData.get(0).containsKey("puzzleWasSolved"));
        assertTrue("Expected field 'numberOfMoves' not found", afterTableData.get(0).containsKey("numberOfMoves"));
    }

    @Test
    public void testSpatialSpanMemoryActiveTask() throws Exception
    {
        String tableName = "SSM-76spatialSpan";
        List<Map<String, Object>> beforeTableData;
        if (!mobileAppTableExists(tableName, LIST_SCHEMA))
            beforeTableData = new ArrayList<>();
        else
            beforeTableData = getTableData(tableName);

        log("Submitting response with spatialSpanMemory active task (SSM-76)");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_SSM-76_1.0--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, "SSM-76", "1.0", appToken, responseString);
        cmd.execute(200);
        waitForResults(beforeTableData, tableName);
        List<Map<String, Object>> afterTableData = getTableData(tableName);
        assertEquals("Unexpected new row count in " + tableName + " after adding single response.",1, getNewRowCount(beforeTableData, afterTableData));
        assertTrue("Expected field 'score' not found", afterTableData.get(0).containsKey("score"));
        assertTrue("Expected field 'numberOfGames' not found", afterTableData.get(0).containsKey("numberOfGames"));
        assertTrue("Expected field 'numberOfFailures' not found", afterTableData.get(0).containsKey("numberOfFailures"));
    }

    private List<Map<String, Object>> getInitialTableData(String tableName) throws Exception
    {
        return mobileAppTableExists(tableName, LIST_SCHEMA) ?
                getTableData(tableName):
                new ArrayList<>();
    }

    static final String OTHER_TEXT_TITLE = "_Other_Text";

    @Test
    public void testOtherOption_columnPresent() throws Exception
    {
        String baseTableName = "OtherOption";

        String noOtherOption = "NoOption";
        String optionRequired = "OptionRequired";
        String optionNotRequired = "OptionNotRequired";
        String multipleNoOption = "MultipleNoOption";
        String multipleRequired = "MultipleRequiredOptions";
        String mixedMultiple = "MixedMultiple";

        //TODO add optionRequiredNoSelections
        String noOtherOptionTableName = baseTableName + noOtherOption;
        String optionRequiredTableName = baseTableName + optionRequired;
        String optionNotRequiredTableName = baseTableName + optionNotRequired;
        String multipleNoOptionTableName = baseTableName + multipleNoOption;
        String multipleRequiredTableName = baseTableName + multipleRequired;
        String mixedMultipleTableName = baseTableName + mixedMultiple;

        List<Map<String, Object>> baseTableData = getInitialTableData(baseTableName);

        log("Submitting response with OtherOption active task (OtherOption)");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_OtherOption_1--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, "OtherOption", "1", appToken, responseString);
        cmd.execute(200);
        waitForResults(baseTableData, baseTableName);

        // Verify no other block parses and gets expected responses and columns
        List<Map<String, Object>> afterTable = getTableData(noOtherOptionTableName);
        assertEquals("Unexpected number of rows in  for no `other` option.", 2, afterTable.size());
        assertEquals("Unexpected number of columns for no `other` option.", 16, afterTable.get(0).keySet().size());

        // Verify other block parses and gets expected responses and columns
        afterTable = getTableData(optionRequiredTableName);
        assertOtherOption(afterTable, optionRequired, 1, "other choice name", "The other text value");

        afterTable = getTableData(optionNotRequiredTableName);
        assertOtherOptionColumnNotPresent(afterTable, optionNotRequired, 1, "other choice name");

        afterTable = getTableData(multipleRequiredTableName);
        assertOtherOption(afterTable, multipleRequired, 0, "I has other", null);
        assertOtherOption(afterTable, multipleRequired, 1, "other A", "other text value");
        assertOtherOption(afterTable, multipleRequired, 2, "B", "text value B");

        afterTable = getTableData(multipleNoOptionTableName);
        assertOtherOptionColumnNotPresent(afterTable, multipleNoOption, 0, "I has other");
        assertOtherOptionColumnNotPresent(afterTable, multipleNoOption, 1, "other choice name");
        assertOtherOptionColumnNotPresent(afterTable, multipleNoOption, 2, "other choice B");

        afterTable = getTableData(mixedMultipleTableName);
        assertOtherOption(afterTable, mixedMultiple, 0, "choice A", null);
        assertOtherOption(afterTable, mixedMultiple, 1, "other B", "other text value");
        assertOtherOption(afterTable, mixedMultiple, 2, "C", "C$(*&%^)@_!<>");
    }

    @Test
    public void testOtherOption_update() throws Exception
    {
        String baseTableName = "OtherOptionUpdate";

        List<Map<String, Object>> baseTableData = getInitialTableData(baseTableName);

        log("Submitting first response with OtherOption for update test");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_OtherOptionUpdate_1--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, "OtherOptionUpdate", "1", appToken, responseString);
        cmd.execute(200);
        waitForResults(baseTableData, baseTableName);

        //Verify setup
        String noOption = "NoOption";
        String otherOption = "OtherOption";
        String noRepeat = "Norepeat";
        String groupedList = "groupedList";
        String subgroup = "SubGroupedList";

        assertInitialOtherOptionTableState(baseTableName + noOption, noOption, 0, "I has textChoices");
        assertInitialOtherOptionTableState(baseTableName + otherOption, otherOption, 0, "I has other");
        assertInitialOtherOptionTableState(baseTableName + groupedList + noRepeat, noRepeat, 0, "NoRepeat");
        assertInitialOtherOptionTableState(baseTableName + groupedList + otherOption, otherOption, 0, "otherOption");
        assertInitialOtherOptionTableState(baseTableName + groupedList + subgroup + otherOption, otherOption, 0, "Version10");

        baseTableData = getInitialTableData(baseTableName);
        log("Submitting OtherOption v2 update");
        responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_OtherOptionUpdate_2--RESPONSE.json");
        appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        cmd = new SubmitResponseCommand(this::log, "OtherOptionUpdate", "2", appToken, responseString);
        cmd.execute(200);
        waitForResults(baseTableData, baseTableName);

        log("Checking for other optional text column and values after update");
        assertOtherOptionColumnNotPresent(getTableData(baseTableName + noOption), noOption, 2, "follow up");
        assertOtherOption(getTableData(baseTableName + otherOption), otherOption, 3, "other choice name", "The other text value");
        assertOtherOption(getTableData(baseTableName + groupedList + otherOption), otherOption, 2, "other choice name", "The other text value");
        assertOtherOption(getTableData(baseTableName + groupedList + subgroup + otherOption), otherOption, 3, "other choice name", "The other text value");
    }

    @Test
    public void testOtherOption_emptyValues() throws Exception
    {
        String baseTableName = "OtherOptionBlanks";

        String otherOption = "OtherOption";
        List<Map<String, Object>> baseTableData = getInitialTableData(baseTableName);

        log("Submitting first response with OtherOption for empty values test");
        String appToken = getNewAppToken(PROJECT_NAME, STUDY_NAME, null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_OtherOptionBlanks_1--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, baseTableName, "1", appToken, responseString);
        cmd.execute(200);
        waitForResults(baseTableData, baseTableName);

        int i = 0;
        String tableName = baseTableName + otherOption;
        List<Map<String, Object>> tableData = getTableData(tableName);

        log("Checking normal other option parsing with value");
        assertOtherOption(tableData, otherOption, i++, "A Option", null);
        assertOtherOption(tableData, otherOption, i++, "B Option", null);
        assertOtherOption(tableData, otherOption, i++, "Not Blank Text", "Other Text");

        log("Verifying parse \"\" as other option text");
        assertOtherOption(tableData, otherOption, i++, "OptionA", null);
        assertOtherOption(tableData, otherOption, i++, "Empty String Text", null);

        log("Parsing other option with missing 'text' field");
        assertOtherOption(tableData, otherOption, i++, "OptionB", null);
        assertOtherOption(tableData, otherOption, i++, "Missing TextField", null);

        log("Parsing normal response with no other option present");
        assertOtherOption(tableData, otherOption, i++, "No Other", null);
        assertOtherOption(tableData, otherOption, i++, "Option", null);
    }

    private void assertInitialOtherOptionTableState(String tableName, String fieldName, int rowId, String expectedValue)
    {
        assertOtherOptionColumnNotPresent(getTableData(tableName), fieldName, rowId, expectedValue);
    }

    private void assertOtherOptionColumnNotPresent(List<Map<String, Object>> table, String fieldName, int rowId, String expectedValueText)
    {
        String optionField = fieldName + OTHER_TEXT_TITLE;
        assertTrue(String.format("Expected field '%1$s' not found", fieldName), table.get(rowId).containsKey(fieldName));
        assertFalse(String.format("Unexpected other option text field '%1$s' present", optionField), table.get(rowId).containsKey(optionField));
        assertEquals(String.format("Expected field '%1$s' has incorrect value", fieldName), expectedValueText, table.get(rowId).get(fieldName));
    }

    private void assertOtherOption(List<Map<String, Object>> table, String fieldName, int rowId, String expectedValueText, String expectedOtherText)
    {
        String optionField = fieldName + OTHER_TEXT_TITLE;
        assertTrue(String.format("Expected field '%1$s' not found", fieldName), table.get(rowId).containsKey(fieldName));
        assertTrue(String.format("Expected field '%1$s' not found", optionField), table.get(rowId).containsKey(optionField));
        assertEquals(String.format("Expected field '%1$s' has incorrect value", fieldName), expectedValueText, table.get(rowId).get(fieldName));
        assertEquals(String.format("Expected field '%1$s' has incorrect value", optionField), expectedOtherText, table.get(rowId).get(optionField));
    }


    private List<Map<String,Object>> getTableData(String table)
    {
        if(! getCurrentRelativeURL().contains(getProjectName()))
        {
            goToProjectHome();
        }
        log("retrieving table "+table);
        return getMobileAppDataWithRetry(table,LIST_SCHEMA).getRows();
    }

    private String getResponseFromFile(String filename)
    {
        return TestFileUtils.getFileContents(TestFileUtils.getSampleData("SurveyMetadata/"+filename));
    }

    private int getNewRowCount(List<Map<String,Object>> tableBefore,List<Map<String,Object>> tableAfter)
    {
        return getNewRows(tableBefore,tableAfter).size();
    }

    private List<Map<String,Object>> getNewRows(List<Map<String,Object>> tableBefore, List<Map<String,Object>> tableAfter)
    {
        assertEquals("Rows missing ", 0, getMissingRows(new ArrayList(tableBefore),new ArrayList(tableAfter)).size());
        List<Map<String,Object>> newRows = new ArrayList<>();
        List<String> beforeKeys = extractKeys(tableBefore);
        log("Keys stored in map: " + concatKeys(beforeKeys));
        List<String> afterKeys = extractKeys(tableAfter);
        log("Keys stored in table: " + concatKeys(afterKeys));
        afterKeys.removeAll(beforeKeys);
        log("Keys recognized as new: " + concatKeys((afterKeys)));
        for(Map<String,Object> row : tableAfter)
        {
            log("comparing row key "+ String.valueOf(row.get("Key") + " against new rows " + concatKeys(afterKeys)));
            if (afterKeys.contains(String.valueOf(row.get("Key"))))
            {
                log("matched key " + String.valueOf(row.get("Key")) + " with row and added to collection");
                newRows.add(row);
            }
        }
        log("new row count is " + newRows.size());
        return newRows;
    }

    private List<Map<String,Object>> getMissingRows(List<Map<String,Object>> tableBefore,List<Map<String,Object>> tableAfter)
    {
        List<Map<String,Object>> missingRows = new ArrayList<>();
        List<String> beforeKeys = extractKeys(tableBefore);
        log("get missing");
        log("Keys stored in map: " + concatKeys(beforeKeys));
        List<String> afterKeys = extractKeys(tableAfter);
        log("Keys stored in table: " + concatKeys(afterKeys));
        beforeKeys.removeAll(afterKeys);
        for(Map<String,Object> row : tableBefore)
        {
            if(beforeKeys.contains(String.valueOf(row.get("Key"))))
            {
                missingRows.add(row);
            }
        }
        return missingRows;
    }

    private String concatKeys(List<String> keys)
    {
        String ret = "";
        for(String key : keys)
        {
            ret = ret.concat(key+", ");
        }
        return ret;
    }

    private List<String> getAddedColumns (List<Map<String,Object>> tableBefore, List<Map<String,Object>> tableAfter)
    {
        Set beforeCols = tableBefore.get(0).keySet();
        Set afterCols = tableAfter.get(0).keySet();
        assertTrue("Column lost",beforeCols.size() <= afterCols.size());
        afterCols.removeAll(beforeCols);
        List<String> added = new ArrayList<>();
        afterCols.forEach(c -> added.add(c.toString()));
        return added;
    }

    private List<String> extractKeys(List<Map<String,Object>> table)
    {
        List<String> keys = new ArrayList<>();
        for(Map<String,Object> row:table)
        {
            keys.add(String.valueOf(row.get("Key")));
        }
        return keys;
    }

    private List<String> getColumnNames(String table)
    {
        return new ArrayList(getTableData(table).get(0).keySet());
    }

    private String getPrintableColumnNames(String table)
    {
        String colNameString = "";
        List<String> colNames = getColumnNames(table);
        for(String colName : colNames)
        {
            colNameString = colNameString + colName + " ";
        }
        colNameString = colNameString + table;
        return colNameString;
    }

    public void resetListState()
    {
        sleep(2000); //give the server a second to process the results and insert
        log("reset lists");
        newSurveyMap = new ArrayList(getTableData("NewSurvey"));
        log("NewSurvey list now has " + String.valueOf(newSurveyMap.size() + " rows"));
        newSurveyGroupedMap = new ArrayList(getTableData("NewSurveyGroupedList"));
        log("NewSurveyGroupedList now has " + String.valueOf( newSurveyGroupedMap.size()) + " rows");
        newSurveyGroupedSubGroupedMap = new ArrayList(getTableData("NewSurveyGroupedListSubGroupedList"));
        log("NewSurveyGroupedSubGroupedList now has " + String.valueOf(newSurveyGroupedSubGroupedMap.size()) + " rows");
        newSurveyGroupedTextChoiceField = new ArrayList(getTableData("NewSurveyGroupedListTextChoiceField"));
        log("NewSurveyGroupedTextChoiceList now has " + String.valueOf(newSurveyGroupedTextChoiceField.size()) + " rows");
        newSurveyTextChoiceField = new ArrayList(getTableData("NewSurveyTextChoiceField"));
        log("NewSurveyTextChoiceList now has " + String.valueOf(newSurveyTextChoiceField.size()) + " rows");
    }

    private void waitForResults(List<Map<String, Object>> oldData, String newDataTableName )
    {
        log("Napping to wait for results to be shredded");
        long napTime = 0;
        long napInterval = 1000;
        long maxNapTime = 10000;
        while (napTime < maxNapTime && getNewRowCount(oldData, getTableData(newDataTableName)) == 0)
        {
            sleep(napInterval);
            napTime += napInterval;
            log("Total nap time: " + napTime);
        }
    }
}
