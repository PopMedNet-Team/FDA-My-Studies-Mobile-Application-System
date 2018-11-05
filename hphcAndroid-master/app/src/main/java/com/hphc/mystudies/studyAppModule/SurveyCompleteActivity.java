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
package com.hphc.mystudies.studyAppModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import com.hphc.mystudies.R;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.studyAppModule.activityBuilder.CustomSurveyViewTaskActivity;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.hphc.mystudies.studyAppModule.custom.Result.StepRecordCustom;
import com.hphc.mystudies.studyAppModule.events.ProcessResponseEvent;
import com.hphc.mystudies.userModule.UserModulePresenter;
import com.hphc.mystudies.userModule.event.UpdatePreferenceEvent;
import com.hphc.mystudies.userModule.webserviceModel.Activities;
import com.hphc.mystudies.userModule.webserviceModel.LoginData;
import com.hphc.mystudies.userModule.webserviceModel.Studies;
import com.hphc.mystudies.userModule.webserviceModel.StudyData;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCallResponseServer;
import com.hphc.mystudies.webserviceModule.events.RegistrationServerConfigEvent;
import com.hphc.mystudies.webserviceModule.events.ResponseServerConfigEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

public class SurveyCompleteActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete, ApiCallResponseServer.OnAsyncRequestComplete {

    private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 100;
    private static final int PROCESS_RESPONSE_RESPONSECODE = 101;
    private static final int UPDATE_STUDY_PREFERENCE = 102;
    private static final String STDYID_FOR_SLITTING = "_STUDYID_";
    private static final String STDYID = "studyId";
    private static final String POST_OBJECT = "post_object";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String RESULT_TYPE = "resultType";
    private static final String SKIPPED = "skipped";
    private static final String VALUE = "value";
    private static final String IDENTIFIER = "identifier";
    private static final String RESULTS = "results";
    private static final String REGISTRATION = "registration";
    private static final String ANSWER = "answer";
    private TextView mSurveyCompleted;
    private TextView mNext;
    private TextView mSurveyCompletedThankyou;
    public static final String EXTRA_STUDYID = "ViewTaskActivity.ExtraStudyId";
    public static final String STUDYID = "ViewTaskActivity.StudyId";
    Realm realm;
    DBServiceSubscriber dbServiceSubscriber;
    private double completion = 0;
    private double adherence = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_complete);
        dbServiceSubscriber = new DBServiceSubscriber();
        realm = AppController.getRealmobj();
        initializeXMLId();
        setFont();
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNext.setClickable(false);
                mNext.setEnabled(false);
                updateProcessResponse();
            }
        });
    }

    private void updateProcessResponse() {
        AppController.getHelperProgressDialog().showProgress(SurveyCompleteActivity.this, "", "", false);
        ProcessResponseEvent processResponseEvent = new ProcessResponseEvent();

        String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
        surveyId = surveyId.substring(0, surveyId.lastIndexOf('_'));
        String activityId[] = surveyId.split(STDYID_FOR_SLITTING);
        ActivityObj activityObj = dbServiceSubscriber.getActivityBySurveyId(getIntent().getStringExtra(STUDYID), activityId[1], realm);
        RealmResults<Activities> activitiesRealmResults = realm.where(Activities.class).equalTo(STDYID, getIntent().getStringExtra(STUDYID)).findAll();
        Activities activities = null;
        for (int i = 0; i < activitiesRealmResults.size(); i++) {
            if (activitiesRealmResults.get(i).getActivityId().equalsIgnoreCase(activityId[1]))
                activities = activitiesRealmResults.get(i);
        }

        Studies studies = realm.where(Studies.class).equalTo(STDYID, getIntent().getStringExtra(STUDYID)).findFirst();
        String participantId = "";
        if (studies != null)
            participantId = studies.getParticipantId();

        if (activities != null && activityObj != null) {

            ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent(POST_OBJECT, URLs.PROCESS_RESPONSE, PROCESS_RESPONSE_RESPONSECODE, this, LoginData.class, null, null, getResponseDataJson(activityObj, activities, participantId), false, this);
            processResponseEvent.setResponseServerConfigEvent(responseServerConfigEvent);
            StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
            studyModulePresenter.performProcessResponse(processResponseEvent);

        } else {
            AppController.getHelperProgressDialog().dismissDialog();
            Toast.makeText(this, getResources().getString(R.string.unable_to_submit_result), Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject getResponseDataJson(ActivityObj activityObj, Activities activities, String participantId) {
        JSONObject ProcessResponsejson = new JSONObject();
        try {
            ProcessResponsejson.put("type", activityObj.getType());

            JSONObject InfoJson = new JSONObject();
            InfoJson.put(STDYID, activities.getStudyId());
            InfoJson.put("activityId", activities.getActivityId());
            InfoJson.put("name", activityObj.getMetadata().getName());
            InfoJson.put("version", activities.getActivityVersion());
            InfoJson.put("activityRunId", activities.getActivityRunId());

            ProcessResponsejson.put("metadata", InfoJson);
            ProcessResponsejson.put("participantId", "" + participantId);
            ProcessResponsejson.put("data", generateresult(activityObj, getIntent().getStringExtra(EXTRA_STUDYID)));
        } catch (JSONException e) {
        }

        return ProcessResponsejson;
    }

    private JSONObject generateresult(ActivityObj activityObj, String stringExtra) {

        try {
            JSONObject dataobj = new JSONObject();
            dataobj.put(START_TIME, activityObj.getMetadata().getStartDate());
            dataobj.put(END_TIME, activityObj.getMetadata().getEndDate());

            JSONArray resultarray = new JSONArray();
            JsonParser jsonParser = new JsonParser();
            RealmResults<StepRecordCustom> StepRecord = realm.where(StepRecordCustom.class).equalTo("taskId", stringExtra).findAll();
            boolean formskipped = true;
            for (int i = 0; i < activityObj.getSteps().size(); i++) {
                for (int j = 0; j < StepRecord.size(); j++) {
                    if (StepRecord.get(j).getStepId().equalsIgnoreCase(activityObj.getSteps().get(i).getKey())) {


                        if (!activityObj.getSteps().get(i).getType().equalsIgnoreCase("task")) {
                            JSONObject resultarrobj = new JSONObject();
                            resultarrobj.put(RESULT_TYPE, activityObj.getSteps().get(i).getResultType());
                            resultarrobj.put("key", activityObj.getSteps().get(i).getKey());

                            resultarrobj.put(START_TIME, AppController.getDateFormat().format(StepRecord.get(j).getStarted()));
                            resultarrobj.put(END_TIME, AppController.getDateFormat().format(StepRecord.get(j).getCompleted()));
                            if (!activityObj.getSteps().get(i).getResultType().equalsIgnoreCase("grouped")) {
                                if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                    resultarrobj.put(SKIPPED, true);
                                    resultarrobj.put(VALUE, "");
                                } else {
                                    if (StepRecord.get(j).getResult().equalsIgnoreCase("{\"answer\":[]}")) {
                                        resultarrobj.put(SKIPPED, true);
                                    } else {
                                        resultarrobj.put(SKIPPED, false);
                                    }
                                    resultarrobj.put(VALUE, findPrimitiveData(jsonParser.parse(StepRecord.get(j).getResult()), activityObj, i));
                                }
                            } else {
                                Map<String, Object> map = (Map<String, Object>) parseData(jsonParser.parse(StepRecord.get(j).getResult()));
                                JSONArray jsonArrayMain = new JSONArray();

                                int k = 0;
                                boolean update;
                                boolean createResult = true;
                                while (createResult) {
                                    JSONArray jsonArray = new JSONArray();
                                    update = false;
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                                        Map<String, Object> mapEntry = (Map<String, Object>) entry.getValue();
                                        JSONObject jsonObject = new JSONObject();
                                        String identifier = (String) mapEntry.get(IDENTIFIER);
                                        if (k == 0) {
                                            if (!identifier.contains("_addMoreEnabled")) {
                                                for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                    if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                        for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                            if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get(IDENTIFIER))) {
                                                                jsonObject.put(RESULT_TYPE, activityObj.getSteps().get(c).getSteps().get(c1).getResultType());
                                                            }
                                                        }
                                                    }
                                                }
                                                jsonObject.put("key", mapEntry.get(IDENTIFIER));
                                                jsonObject.put(START_TIME, mapEntry.get("startDate"));
                                                jsonObject.put(END_TIME, mapEntry.get("endDate"));


                                                Map<String, Object> mapEntryResult = (Map<String, Object>) mapEntry.get(RESULTS);
                                                Object o = mapEntryResult.get(ANSWER);
                                                if (o instanceof Object[]) {
                                                    Object[] objects = (Object[]) o;
                                                    if (objects.length > 0) {
                                                        if (objects[0] instanceof Integer) {
                                                            JSONArray jsonArray1 = new JSONArray();
                                                            for (int l = 0; l < objects.length; l++) {
                                                                for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                                    if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                                        for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                                            if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get(IDENTIFIER))) {
                                                                                jsonArray1.put(activityObj.getSteps().get(c).getSteps().get(c1).getFormat().getTextChoices().get((int) objects[l]).getValue());
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            jsonObject.put(VALUE, jsonArray1);
                                                        } else if (objects[0] instanceof String) {
                                                            JSONArray jsonArray1 = new JSONArray();
                                                            for (int l = 0; l < objects.length; l++) {
                                                                jsonArray1.put((String) objects[l]);
                                                            }
                                                            jsonObject.put(VALUE, jsonArray1);
                                                        }
                                                    } else {
                                                        jsonObject.put(VALUE, new JSONArray());
                                                    }
                                                } else {
                                                    jsonObject.put(VALUE, mapEntryResult.get(ANSWER));
                                                }

                                                try {
                                                    if (jsonObject.get(VALUE) == null || jsonObject.get(VALUE).toString().equalsIgnoreCase("") || jsonObject.get(VALUE).toString().equalsIgnoreCase("[]")) {
                                                        resultarrobj.put(SKIPPED, true);
                                                        jsonObject.put(SKIPPED, true);
                                                    } else {
                                                        resultarrobj.put(SKIPPED, false);
                                                        jsonObject.put(SKIPPED, false);
                                                        formskipped = false;
                                                    }
                                                } catch (JSONException e) {
                                                    resultarrobj.put(SKIPPED, true);
                                                    jsonObject.put(SKIPPED, true);
                                                }

                                                jsonArray.put(jsonObject);
                                                update = true;
                                            }
                                        } else if (identifier.contains(k + "_addMoreEnabled")) {
                                            for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                    for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                        if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get(IDENTIFIER))) {
                                                            jsonObject.put(RESULT_TYPE, activityObj.getSteps().get(c).getSteps().get(c1).getResultType());
                                                        }
                                                    }
                                                }
                                            }
                                            jsonObject.put("key", mapEntry.get(IDENTIFIER).toString().substring(0, mapEntry.get(IDENTIFIER).toString().lastIndexOf('-')));
                                            jsonObject.put(START_TIME, mapEntry.get("startDate"));
                                            jsonObject.put(END_TIME, mapEntry.get("endDate"));
                                            jsonObject.put(SKIPPED, false);
                                            Map<String, Object> mapEntryResult = (Map<String, Object>) mapEntry.get(RESULTS);
                                            Object o = mapEntryResult.get(ANSWER);
                                            if (o instanceof Object[]) {
                                                Object[] objects = (Object[]) o;
                                                if (objects.length > 0) {
                                                    if (objects[0] instanceof Integer) {
                                                        JSONArray jsonArray1 = new JSONArray();
                                                        for (int l = 0; l < objects.length; l++) {
                                                            for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                                if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                                    for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                                        if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get(IDENTIFIER))) {
                                                                            jsonArray1.put(activityObj.getSteps().get(c).getSteps().get(c1).getFormat().getTextChoices().get((int) objects[l]).getValue());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        jsonObject.put(VALUE, jsonArray1);
                                                    } else if (objects[0] instanceof String) {
                                                        JSONArray jsonArray1 = new JSONArray();
                                                        for (int l = 0; l < objects.length; l++) {
                                                            jsonArray1.put((String) objects[l]);
                                                        }
                                                        jsonObject.put(VALUE, jsonArray1);
                                                    }
                                                } else {
                                                    jsonObject.put(VALUE, new JSONArray());
                                                }
                                            } else {
                                                jsonObject.put(VALUE, mapEntryResult.get(ANSWER));
                                            }


                                            try {
                                                if (jsonObject.get(VALUE) == null || jsonObject.get(VALUE).toString().equalsIgnoreCase("") || jsonObject.get(VALUE).toString().equalsIgnoreCase("[]")) {
                                                    resultarrobj.put(SKIPPED, true);
                                                    jsonObject.put(SKIPPED, true);
                                                } else {
                                                    resultarrobj.put(SKIPPED, false);
                                                    jsonObject.put(SKIPPED, false);
                                                    formskipped = false;
                                                }
                                            } catch (JSONException e) {
                                                resultarrobj.put(SKIPPED, true);
                                                jsonObject.put(SKIPPED, true);
                                            }


                                            jsonArray.put(jsonObject);
                                            update = true;
                                        }
                                    }
                                    k = k + jsonArray.length();
                                    if (update) {
                                        jsonArrayMain.put(jsonArray);
                                    } else {
                                        createResult = false;
                                    }
                                }
                                if (formskipped) {
                                    resultarrobj.put(SKIPPED, true);
                                    resultarrobj.put(VALUE, new JSONArray());
                                } else {
                                    resultarrobj.put(SKIPPED, false);
                                    resultarrobj.put(VALUE, jsonArrayMain);
                                }
                            }
                            resultarray.put(resultarrobj);

                            dataobj.put(RESULTS, resultarray);
                        } else {

                            JSONObject durationobj = new JSONObject();
                            durationobj.put(RESULT_TYPE, "numeric");
                            durationobj.put("key", "duration");
                            durationobj.put(START_TIME, null);
                            durationobj.put(END_TIME, null);
                            if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                durationobj.put(SKIPPED, true);
                            } else {
                                durationobj.put(SKIPPED, false);
                            }
                            JSONObject activejsonObject = new JSONObject(StepRecord.get(j).getResult());
                            JSONObject answerjsonobj=activejsonObject.getJSONObject(ANSWER);
                            durationobj.put(VALUE,answerjsonobj.getString("duration"));

                            JSONObject valueobj = new JSONObject();
                            valueobj.put(RESULT_TYPE, "numeric");
                            valueobj.put("key", "count");
                            valueobj.put(START_TIME, null);
                            valueobj.put(END_TIME, null);
                            if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                valueobj.put(SKIPPED, true);
                            } else {
                                valueobj.put(SKIPPED, false);
                            }
                            valueobj.put(VALUE, answerjsonobj.getInt(VALUE));

                            resultarray.put(durationobj);
                            resultarray.put(valueobj);

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put(VALUE, resultarray);
                            jsonObject.put(START_TIME, AppController.getDateFormat().format(StepRecord.get(j).getStarted()));
                            jsonObject.put(END_TIME, AppController.getDateFormat().format(StepRecord.get(j).getCompleted()));
                            jsonObject.put(RESULT_TYPE, "grouped");
                            jsonObject.put("key", activityObj.getSteps().get(i).getKey());
                            if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                jsonObject.put(SKIPPED, true);
                            } else {
                                jsonObject.put(SKIPPED, false);
                            }
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);

                            dataobj.put(RESULTS, jsonArray);
                        }
                    }
                }
            }
            return dataobj;
        } catch (JSONException e) {
        }
        return new JSONObject();
    }

    private void initializeXMLId() {
        mSurveyCompleted = (TextView) findViewById(R.id.surveyCompleted);
        mSurveyCompletedThankyou = (TextView) findViewById(R.id.surveyCompletedThankyou);
        mNext = (TextView) findViewById(R.id.nextButton);
    }


    private void setFont() {
        try {
            mSurveyCompleted.setTypeface(AppController.getTypeface(SurveyCompleteActivity.this, "regular"));
            mSurveyCompletedThankyou.setTypeface(AppController.getTypeface(SurveyCompleteActivity.this, "regular"));
        } catch (Exception e) {
        }
    }

    public void updateUserPreference() {
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.userid), ""));
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent(POST_OBJECT, URLs.UPDATE_ACTIVITY_PREFERENCE, UPDATE_USERPREFERENCE_RESPONSECODE, this, LoginData.class, null, header, getActivityPreferenceJson(), false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    private JSONObject getActivityPreferenceJson() {
        String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
        surveyId = surveyId.substring(0, surveyId.lastIndexOf('_'));
        String activityId[] = surveyId.split(STDYID_FOR_SLITTING);

        JSONObject jsonObject = new JSONObject();

        JSONArray activitylist = new JSONArray();
        JSONObject activityStatus = new JSONObject();
        JSONObject activityRun = new JSONObject();
        try {
            activityStatus.put(STDYID, getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID));
            activityStatus.put("activityState", SurveyActivitiesFragment.COMPLETED);
            activityStatus.put("activityId", activityId[1]);
            activityStatus.put("activityRunId", "" + getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0));
            activityStatus.put("bookmarked", "false");
            activityStatus.put("activityVersion", "" + getIntent().getStringExtra(CustomSurveyViewTaskActivity.ACTIVITY_VERSION));

            int completedRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.COMPLETED_RUN, 0);
            completedRun = completedRun + 1;
            int currentRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0);
            int missedRun = currentRun - completedRun;

            activityRun.put("total", getIntent().getIntExtra(CustomSurveyViewTaskActivity.TOTAL_RUN, 0));
            activityRun.put("completed", completedRun);
            activityRun.put("missed", missedRun);

            activityStatus.put("activityRun", activityRun);

        } catch (JSONException e) {
        }

        activitylist.put(activityStatus);

        try {
            jsonObject.put(STDYID, getIntent().getStringExtra(STUDYID));
            jsonObject.put("activity", activitylist);
        } catch (JSONException e) {
        }


        return jsonObject;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            LoginData loginData = (LoginData) response;
            if (loginData != null) {

                //calculate completion and adherence
                int completed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.completedRuns), ""));
                int missed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.missedRuns), ""));
                int total = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.totalRuns), ""));

                if ((double) total > 0)
                    completion = (((double) completed + (double) missed + 1d) / (double) total) * 100d;

                if (((double) completed + (double) missed + 1d) > 0)
                    adherence = (((double) completed + 1d) / ((double) completed + (double) missed + 1d)) * 100d;

                updateStudyState(Integer.toString((int) completion), Integer.toString( (int) adherence));


            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == UPDATE_STUDY_PREFERENCE) {
            AppController.getHelperProgressDialog().dismissDialog();
            String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
            surveyId = surveyId.substring(0, surveyId.lastIndexOf('_'));
            String activityId[] = surveyId.split(STDYID_FOR_SLITTING);
            int completedRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.COMPLETED_RUN, 0);
            completedRun = completedRun + 1;
            int currentRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0);
            int missedRun = currentRun - completedRun;
            dbServiceSubscriber.updateActivityPreferenceDB(activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0), SurveyActivitiesFragment.COMPLETED, getIntent().getIntExtra(CustomSurveyViewTaskActivity.TOTAL_RUN, 0), completedRun, missedRun, getIntent().getStringExtra(CustomSurveyViewTaskActivity.ACTIVITY_VERSION));
            StudyData studyData = dbServiceSubscriber.getStudyPreferencesListFromDB(realm);
            Studies studies = null;
            if (studyData != null) {
                for (int i = 0; i < studyData.getStudies().size(); i++) {
                    if (studyData.getStudies().get(i).getStudyId().equalsIgnoreCase(getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID))) {
                        studies = studyData.getStudies().get(i);
                    }
                }
            }
            if (studies != null) {
                dbServiceSubscriber.updateStudyPreference(studies, completion, adherence);
            }
            dbServiceSubscriber.updateActivityRunToDB(activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0));
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            AppController.getHelperProgressDialog().dismissDialog();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(SurveyCompleteActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(SurveyCompleteActivity.this, errormsg);
        } else {


            /////////// offline data storing activity preference
            try {
                if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
                    int number = dbServiceSubscriber.getUniqueID(realm);
                    if (number == 0) {
                        number = 1;
                    } else {
                        number += 1;
                    }
                    AppController.pendingService(number, POST_OBJECT, URLs.UPDATE_ACTIVITY_PREFERENCE, "", getActivityPreferenceJson().toString(), REGISTRATION, "", "", "");
                }
            } catch (Exception e) {
            }
            try {
                int number = dbServiceSubscriber.getUniqueID(realm);
                if (number == 0) {
                    number = 1;
                } else {
                    number += 1;
                }

                //calculate completion and adherence
                int completed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.completedRuns), ""));
                int missed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.missedRuns), ""));
                int total = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.totalRuns), ""));

                if ((double) total > 0)
                    completion = (((double) completed + (double) missed + 1d) / (double) total) * 100d;

                if (((double) completed + (double) missed + 1d) > 0)
                    adherence = (((double) completed + 1d) / ((double) completed + (double) missed + 1d)) * 100d;

                AppController.pendingService(number, POST_OBJECT, URLs.UPDATE_STUDY_PREFERENCE, "", getStudyPreferenceJson(Integer.toString((int) completion), Integer.toString((int) adherence)).toString(), REGISTRATION, "", "", "");
            } catch (Exception e) {
            }


            try {
                String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
                surveyId = surveyId.substring(0, surveyId.lastIndexOf('_'));
                int completedRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.COMPLETED_RUN, 0);
                completedRun = completedRun + 1;
                int currentRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0);
                int missedRun = currentRun - completedRun;
                String activityId[] = surveyId.split(STDYID_FOR_SLITTING);
                dbServiceSubscriber.updateActivityPreferenceDB(activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0), SurveyActivitiesFragment.COMPLETED, getIntent().getIntExtra(CustomSurveyViewTaskActivity.TOTAL_RUN, 0), completedRun, missedRun, getIntent().getStringExtra(CustomSurveyViewTaskActivity.ACTIVITY_VERSION));
                dbServiceSubscriber.updateActivityRunToDB(activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0));
            } catch (Exception e) {
            }
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    private static Object findPrimitiveData(JsonElement jsonElement, ActivityObj activityObj, int position) {
        JsonObject obj = jsonElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
        JsonPrimitive prim = null;
        for (Map.Entry<String, JsonElement> entry : entitySet) {
            if (entry.getValue().isJsonArray()) {
                JsonArray arr = entry.getValue().getAsJsonArray();
                JSONArray jsonArray = new JSONArray();
                for (JsonElement anArr : arr) {
                    int valueposition = 0;
                    for (int i = 0; i < activityObj.getSteps().get(position).getFormat().getTextChoices().size(); i++) {
                        if (activityObj.getSteps().get(position).getFormat().getTextChoices().get(i).getValue().equalsIgnoreCase("" + anArr.getAsString())) {
                            valueposition = i;
                        }
                    }
                    jsonArray.put(activityObj.getSteps().get(position).getFormat().getTextChoices().get(valueposition).getValue());
                }
                return jsonArray;
            } else {
                if (entry.getValue().isJsonPrimitive())
                    prim = entry.getValue().getAsJsonPrimitive();
                else
                    return entry.getValue().getAsJsonObject();
            }
        }
        if (prim != null) {
            if (prim.isBoolean()) {
                return prim.getAsBoolean();
            } else if (prim.isString()) {
                return prim.getAsString();
            } else if (prim.isNumber()) {
                String num = prim.getAsNumber().toString();

                if (num.contains(".")) {
                    return Double.parseDouble(num);
                } else {
                    try {
                        return Integer.parseInt(num);
                    } catch (Exception e) {
                        return Long.parseLong(num);
                    }
                }

            }
        }
        return null;
    }

    public static Object parseData(JsonElement jsonElement) {
        Map<String, Object> map = new LinkedTreeMap<String, Object>();
        if (jsonElement.isJsonArray()) {
            JsonArray arr = jsonElement.getAsJsonArray();
            Object list[] = new Object[arr.size()];
            int i = 0;
            for (JsonElement anArr : arr) {
                list[i] = parseData(anArr);
                i++;
            }
            return list;
        } else if (jsonElement.isJsonPrimitive()) {
            return findPrimitiveData(jsonElement);
        } else {
            JsonObject objStep = jsonElement.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entitySetStep = objStep.entrySet();
            for (Map.Entry<String, JsonElement> entryStep : entitySetStep) {
                map.put(entryStep.getKey(), parseData(entryStep.getValue()));
            }
        }

        return map;

    }

    private static Object findPrimitiveData(JsonElement jsonElement) {
        JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
        if (prim.isBoolean()) {
            return prim.getAsBoolean();
        } else if (prim.isString()) {
            return prim.getAsString();
        } else if (prim.isNumber()) {
            String num = prim.getAsNumber().toString();

            if (num.contains(".")) {
                return Double.parseDouble(num);
            } else {
                try {
                    return Integer.parseInt(num);
                } catch (Exception e) {
                    return Long.parseLong(num);
                }
            }
        }
        return null;
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode, String serverType) {

        if (responseCode == PROCESS_RESPONSE_RESPONSECODE) {
            LoginData loginData = (LoginData) response;
            if (loginData != null) {
                updateUserPreference();
            } else {
                AppController.getHelperProgressDialog().dismissDialog();
            }
        } else {
            AppController.getHelperProgressDialog().dismissDialog();
        }

    }

    @Override
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String statusCode, T response) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(SurveyCompleteActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(SurveyCompleteActivity.this, errormsg);
        } else {

            String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
            surveyId = surveyId.substring(0, surveyId.lastIndexOf('_'));
            String activityId[] = surveyId.split(STDYID_FOR_SLITTING);
            ActivityObj activityObj = dbServiceSubscriber.getActivityBySurveyId(getIntent().getStringExtra(STUDYID), activityId[1], realm);

            RealmResults<Activities> activitiesRealmResults = realm.where(Activities.class).equalTo(STDYID, getIntent().getStringExtra(STUDYID)).findAll();
            Activities activities = null;
            for (int i = 0; i < activitiesRealmResults.size(); i++) {
                if (activitiesRealmResults.get(i).getActivityId().equalsIgnoreCase(activityId[1]))
                    activities = activitiesRealmResults.get(i);
            }

            Studies studies = realm.where(Studies.class).equalTo(STDYID, getIntent().getStringExtra(STUDYID)).findFirst();
            String participantId = "";
            if (studies != null)
                participantId = studies.getParticipantId();

            try {
                int number = dbServiceSubscriber.getUniqueID(realm);
                if (number == 0) {
                    number = 1;
                } else {
                    number += 1;
                }
                AppController.pendingService(number, POST_OBJECT, URLs.PROCESS_RESPONSE, "", getResponseDataJson(activityObj, activities, participantId).toString(), "response", "", "", "");
            } catch (Exception e) {
            }

            try {
                int number = dbServiceSubscriber.getUniqueID(realm);
                if (number == 0) {
                    number = 1;
                } else {
                    number += 1;
                }
                AppController.pendingService(number, POST_OBJECT, URLs.UPDATE_ACTIVITY_PREFERENCE, "", getActivityPreferenceJson().toString(), REGISTRATION, "", "", "");
            } catch (Exception e) {
            }
            try {
                int number = dbServiceSubscriber.getUniqueID(realm);
                if (number == 0) {
                    number = 1;
                } else {
                    number += 1;
                }

                //calculate completion and adherence
                int completed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.completedRuns), ""));
                int missed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.missedRuns), ""));
                int total = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.totalRuns), ""));

                if ((double) total > 0)
                    completion = (((double) completed + (double) missed + 1d) / (double) total) * 100d;

                if (((double) completed + (double) missed + 1d) > 0)
                    adherence = (((double) completed + 1d) / ((double) completed + (double) missed + 1d)) * 100d;

                AppController.pendingService(number, POST_OBJECT, URLs.UPDATE_STUDY_PREFERENCE, "", getStudyPreferenceJson(Integer.toString((int) completion), Integer.toString ((int) adherence)).toString(), REGISTRATION, "", "", "");
            } catch (Exception e) {
            }

            int completedRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.COMPLETED_RUN, 0);
            completedRun = completedRun + 1;
            int currentRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0);
            int missedRun = currentRun - completedRun;
            dbServiceSubscriber.updateActivityPreferenceDB(activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0), SurveyActivitiesFragment.COMPLETED, getIntent().getIntExtra(CustomSurveyViewTaskActivity.TOTAL_RUN, 0), completedRun, missedRun, getIntent().getStringExtra(CustomSurveyViewTaskActivity.ACTIVITY_VERSION));
            dbServiceSubscriber.updateActivityRunToDB(activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0));
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.closeRealmObj(realm);
        super.onDestroy();
    }

    public void updateStudyState(String completion, String adherence) {
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.userid), ""));
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent(POST_OBJECT, URLs.UPDATE_STUDY_PREFERENCE, UPDATE_STUDY_PREFERENCE, this, LoginData.class, null, header, getStudyPreferenceJson(completion, adherence), false, this);
        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    private JSONObject getStudyPreferenceJson(String completion, String adherence) {
        JSONObject jsonObject = new JSONObject();

        JSONArray studieslist = new JSONArray();
        JSONObject studiestatus = new JSONObject();
        try {
            studiestatus.put(STDYID, getIntent().getStringExtra(STUDYID));
            studiestatus.put("completion", completion);
            studiestatus.put("adherence", adherence);

        } catch (JSONException e) {
        }

        studieslist.put(studiestatus);
        try {
            jsonObject.put("studies", studieslist);
        } catch (JSONException e) {
        }

        return jsonObject;
    }

}
