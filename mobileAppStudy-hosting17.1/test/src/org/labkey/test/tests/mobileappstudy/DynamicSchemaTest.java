/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.tests.mobileappstudy;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.TestFileUtils;
import org.labkey.test.categories.Git;
import org.labkey.test.commands.mobileappstudy.SubmitResponseCommand;
import org.labkey.test.pages.mobileappstudy.ResponseQueryPage;
import org.labkey.test.pages.mobileappstudy.SetupPage;
import org.labkey.test.util.PortalHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by RyanS on 2/16/2017.
 */
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
        setupPage.studySetupWebPart.checkResponseCollection();
        setupPage.studySetupWebPart.setShortName(STUDY_NAME);
        setupPage.validateSubmitButtonEnabled();
        setupPage.studySetupWebPart.clickSubmit();

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
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with single question added. Response text 2",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        Assert.assertEquals("Did not find new expected column DoubleField2 in NewSurvey after adding single response with single question added. Response text 2","DoubleField2", getAddedColumns(newSurveyMap,getTableData("NewSurvey")).get(0));
        Assert.assertEquals("Unexpected number of new columns in NewSurvey after adding single response with single question added. Response text 2", 1, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single question added. Response text 2",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single question added. Response text 2",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single question added. Response text 2",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single question added. Response text 2",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question added. Response text 2",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single question added. Response text 2",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single question added. Response text 2",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single question added. Response text 2",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
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
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding response with single question removed. Response text 3",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        Assert.assertEquals("Unexpected number of new columns NewSurvey after adding response with single question removed. Response text 3", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after response with single question removed. Response text 3",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns NewSurveyGroupedList after adding response with single question removed. Response text 3",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after response with single question removed. Response text 3",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding response with single question removed. Response text 3",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question removed. Response text 3",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding response with single question removed. Response text 3",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after response with dropped question with single question removed. Response text 3",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding response with single question removed. Response text 3",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
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
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with a single question added to group. Response text 4",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        Assert.assertEquals("Unexpected additional column in NewSurvey after adding single response with a single question added to group. Response text 4", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with a single question added to group. Response text 4",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with a single question added to group. Response text 4",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with a single question added to group. Response text 4",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with a single question added to group. Response text 4",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with a single question added to group. Response text 4",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with a single question added to group. Response text 4",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());;
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with a single question added to group. Response text 4",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with a single question added to group. Response text 4",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
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
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with single question removed from group. Response text 5",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        Assert.assertEquals("Unexpected additional column in NewSurvey after adding single response with single question removed from group. Response text 5 ", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single question removed from group. Response text 5",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single question removed from group. Response text 5",1, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single question removed from group. Response text 5",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single question removed from group. Response text 5",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question removed from group. Response text 5",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single question removed from group. Response text 5",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single question removed from group. Response text 5",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single question removed from group. Response text 5",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
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
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with single question added to sub subgroup. Response text 6",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        Assert.assertEquals("Unexpected additional column in NewSurvey after adding single response with single question added to sub subgroup. Response text 6", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single question added to sub subgroup. Response text 6",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single question added to sub subgroup. Response text 6",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single question added to sub subgroup. Response text 6",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single question added to sub subgroup. Response text 6",1, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question added to sub subgroup. Response text 6",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single question added to sub subgroup. Response text 6",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single question added to sub subgroup. Response text 6",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single question added to sub subgroup. Response text 6",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
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
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with single question removed from sub. Response text 7",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        Assert.assertEquals("Unexpected number of new columns in NewSurvey after adding single response with single question removed from sub. Response text 7", 1, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single question removed from sub. Response text 7",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single question removed from sub. Response text 7",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single question removed from sub. Response text 7",1,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single question removed from sub. Response text 7",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single question removed from sub. Response text 7",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single question removed from sub. Response text 7",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single question removed from sub. Response text 7",2,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single question removed from sub. Response text 7",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
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
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with single group added. Response text 8", 1, getNewRowCount(newSurveyMap, getTableData("NewSurvey")));
        Assert.assertEquals("Unexpected number of new columns in NewSurvey after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with single group added. Response text 8", 1, getNewRowCount(newSurveyGroupedMap, getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyGroupedMap, getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with single group added. Response text 8", 1, getNewRowCount(newSurveyGroupedSubGroupedMap, getTableData("NewSurveyGroupedListSubGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyGroupedSubGroupedMap, getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with single group added. Response text 8", 2, getNewRowCount(newSurveyTextChoiceField, getTableData("NewSurveyTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyTextChoiceField, getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with single group added. Response text 8", 2, getNewRowCount(newSurveyGroupedTextChoiceField, getTableData("NewSurveyGroupedListTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with single group added. Response text 8", 0, getAddedColumns(newSurveyGroupedTextChoiceField, getTableData("NewSurveyGroupedListTextChoiceField")).size());

        Assert.assertEquals("Unexpected new row count in NewSurveyNewTextChoiceField after adding single response with new text choice field. Response text 8", 2, getTableData("NewSurveyNewTextChoiceField").size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyNewTextChoiceField with new text choice field. Response text 8", 2, getTableData("NewSurveyNewTextChoiceField").size());
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
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with group removed. Response text 9",1,getNewRowCount(newSurveyMap,getTableData("NewSurvey")));
        Assert.assertEquals("Unexpected number of new columns in NewSurvey with group removed. Response text 9", 1, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with group removed. Response text 9",0,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList with group removed. Response text 9",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response",0,getNewRowCount(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList with group removed. Response text 9",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response",2,getNewRowCount(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField with group removed. Response text 9",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response",0,getNewRowCount(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField with group removed. Response text 9",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
    }

    @Test
    public void testNewGroup()
    {
        resetListState();
        log("Submitting response with group removed. Response text 13");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_13--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "13", appToken, responseString);
        cmd.execute(200);
        sleep(5000);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListAdded after adding single response with group and subgroup added. Response text 13",1,getNewRowCount(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")));
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListAdded with group and subgroup added. Response text 13",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListAddedSubGroupedList after adding single response with group and subgroup added. Response text 13",1,getTableData("NewSurveyGroupedListAddedSubGroupedList").size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListAddedSubGroupedList with group and subgroup added. Response text 13",1, getTableData("NewSurveyGroupedListAddedSubGroupedList").size());
        Assert.assertEquals("Unexpected new row count in NewSurveyAddedTextChoiceField after adding single response with group and subgroup added. Response text 13",2,getTableData("NewSurveyGroupedListAddedTextChoiceField").size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyAddedTextChoiceField with group and subgroup added. Response text 13",2, getTableData("NewSurveyGroupedListAddedTextChoiceField").size());
        Assert.assertEquals("Unexpected new row count in NewSurveyAddedGroupedListTextChoiceField after adding single response with group and subgroup added. Response text 13",2,getTableData("NewSurveyGroupedListAddedTextChoiceField").size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyAddedGroupedListTextChoiceField with group and subgroup added. Response text 13",2, getTableData("NewSurveyGroupedListAddedTextChoiceField").size());
    }

    @Test
    public void testMismatchedResponseAndSchema()
    {
        //We do not currently confirm if the survey metadata contained in schema file matches the filename (Response survey metadata)
        resetListState();
        log("Submitting response with mismatched schema. Response text 10");
        String appToken = getNewAppToken(PROJECT_NAME,STUDY_NAME,null);
        String responseString = getResponseFromFile("DYNAMICSCHEMASTUDY_NewSurvey_10--RESPONSE.json");
        SubmitResponseCommand cmd = new SubmitResponseCommand(this::log, SURVEY_NAME, "10", appToken, responseString); //Schema name in the metadata is: "NewSurvey_Mismatch"
        cmd.execute(200);
        sleep(5000);

        goToProjectHome(PROJECT_NAME);  //refresh Project page

        log("Check mismatch lists created in addition to existing lists");
        assertTextPresentInThisOrder("NewSurvey", "NewSurvey_Mismatch", "NewSurvey_MismatchGroupedList",
                "NewSurvey_MismatchGroupedListSubGroupedList", "NewSurvey_MismatchGroupedListTextChoiceField",
                "NewSurvey_MismatchTextChoiceField", "NewSurveyGroupedList", "NewSurveyGroupedListSubGroupedList",
                "NewSurveyGroupedListTextChoiceField", "NewSurveyTextChoiceField");

        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with mismatched schema. Response text 10", 1, getNewRows(newSurveyMap,getTableData("NewSurvey")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurvey with mismatched schema. Response text 10", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with mismatched schema. Response text 10", 1,getNewRows(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList with mismatched schema. Response text 10",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with mismatched schema. Response text 10",1,getNewRows(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList with mismatched schema. Response text 10",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with mismatched schema. Response text 10",2,getNewRows(newSurveyGroupedTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField with mismatched schema. Response text 10",0, getAddedColumns(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with mismatched schema. Response text 10",2,getNewRows(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with mismatched schema. Response text 10",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
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
        Assert.assertEquals("Unexpected new row count in NewSurvey after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyMap,getTableData("NewSurvey")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurvey after addidn single response with malformed schema. Response text 11", 0, getAddedColumns(newSurveyMap, getTableData("NewSurvey")).size(),1);
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedList after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedList after adding single response with malformed schema. Response text 11",0, getAddedColumns(newSurveyGroupedMap,getTableData("NewSurveyGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListSubGroupedList after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListSubGroupedList after adding single response with malformed schema. Response text 11",0, getAddedColumns(newSurveyGroupedSubGroupedMap,getTableData("NewSurveyGroupedListSubGroupedList")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyTextChoiceField after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyTextChoiceField,getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyTextChoiceField after adding single response with malformed schema. Response text 11",0, getAddedColumns(newSurveyTextChoiceField, getTableData("NewSurveyTextChoiceField")).size());
        Assert.assertEquals("Unexpected new row count in NewSurveyGroupedListTextChoiceField after adding single response with malformed schema. Response text 11",0,getNewRows(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
        Assert.assertEquals("Unexpected number of new columns in NewSurveyGroupedListTextChoiceField after adding single response with malformed schema. Response text 11",0, getAddedColumns(newSurveyGroupedTextChoiceField,getTableData("NewSurveyGroupedListTextChoiceField")).size());
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
    public void testFetalKickCountActiveTask()
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
        Assert.assertEquals("Unexpected new row count in FKC-74counter after adding single response.",1, getNewRowCount(beforeTableData, afterTableData));
        Assert.assertTrue("Expected field 'duration' not found", afterTableData.get(0).containsKey("duration"));
        Assert.assertTrue("Expected field 'count' not found", afterTableData.get(0).containsKey("count"));
    }

    @Test
    public void testTowersOfHanoiActiveTask()
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
        sleep(5000);
        List<Map<String, Object>> afterTableData = getTableData(tableName);
        Assert.assertEquals("Unexpected new row count in " + tableName + " after adding single response.",1, getNewRowCount(beforeTableData, afterTableData));
        Assert.assertTrue("Expected field 'puzzleWasSolved' not found", afterTableData.get(0).containsKey("puzzleWasSolved"));
        Assert.assertTrue("Expected field 'numberOfMoves' not found", afterTableData.get(0).containsKey("numberOfMoves"));
    }

    @Test
    public void testSpatialSpanMemoryActiveTask()
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
        sleep(5000);
        List<Map<String, Object>> afterTableData = getTableData(tableName);
        Assert.assertEquals("Unexpected new row count in " + tableName + " after adding single response.",1, getNewRowCount(beforeTableData, afterTableData));
        Assert.assertTrue("Expected field 'score' not found", afterTableData.get(0).containsKey("score"));
        Assert.assertTrue("Expected field 'numberOfGames' not found", afterTableData.get(0).containsKey("numberOfGames"));
        Assert.assertTrue("Expected field 'numberOfFailures' not found", afterTableData.get(0).containsKey("numberOfFailures"));
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
        Assert.assertEquals("Rows missing ", 0, getMissingRows(new ArrayList(tableBefore),new ArrayList(tableAfter)).size());
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

    private List<String> getAddedColumns (List<Map<String,Object>> tableBefore,List<Map<String,Object>> tableAfter)
    {
        Set beforeCols = tableBefore.get(0).keySet();
        Set afterCols = tableAfter.get(0).keySet();
        Assert.assertTrue("Column lost",beforeCols.size() <= afterCols.size());
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

    //@After
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
}
