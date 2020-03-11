package com.harvard.studyAppModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import com.harvard.R;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.activityBuilder.CustomSurveyViewTaskActivity;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.studyAppModule.events.ProcessResponseEvent;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.event.UpdatePreferenceEvent;
import com.harvard.userModule.webserviceModel.Activities;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.userModule.webserviceModel.Studies;
import com.harvard.userModule.webserviceModel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.ResponseServerConfigEvent;

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
    private TextView mSurveyCompleted;
    private TextView mNext;
    private TextView mSurveyCompletedThankyou;
    public static final String EXTRA_STUDYID = "ViewTaskActivity.ExtraStudyId";
    public static final String STUDYID = "ViewTaskActivit" +
            "y.StudyId";
    public static final String RUNID = "ViewTaskActivity.RunId";
    private int mDeleteIndexNumberDB;
    Realm realm;
    DBServiceSubscriber dbServiceSubscriber;
    private double completion = 0;
    private double adherence = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_complete);
        dbServiceSubscriber = new DBServiceSubscriber();
        realm = AppController.getRealmobj(this);
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
        surveyId = surveyId.substring(0, surveyId.lastIndexOf("_"));
        String activityId[] = surveyId.split("_STUDYID_");
        ActivityObj activityObj = dbServiceSubscriber.getActivityBySurveyId(getIntent().getStringExtra(STUDYID), activityId[1], realm);

        RealmResults<Activities> activitiesRealmResults = realm.where(Activities.class).equalTo("studyId", getIntent().getStringExtra(STUDYID)).findAll();
        Activities activities = null;
        for (int i = 0; i < activitiesRealmResults.size(); i++) {
            if (activitiesRealmResults.get(i).getActivityId().equalsIgnoreCase(activityId[1]))
                activities = activitiesRealmResults.get(i);
        }

        Studies studies = realm.where(Studies.class).equalTo("studyId", getIntent().getStringExtra(STUDYID)).findFirst();
        String participantId = "";
        if (studies != null)
            participantId = studies.getParticipantId();

        if (activities != null && activityObj != null) {


            ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent("post_object", URLs.PROCESS_RESPONSE, PROCESS_RESPONSE_RESPONSECODE, this, LoginData.class, null, null, getResponseDataJson(activityObj, activities, participantId), false, this);
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
            InfoJson.put("studyId", activities.getStudyId());
            InfoJson.put("activityId", activities.getActivityId());
            InfoJson.put("name", activityObj.getMetadata().getName());
            InfoJson.put("version", activities.getActivityVersion());
            InfoJson.put("activityRunId", activities.getActivityRunId());

            ProcessResponsejson.put("metadata", InfoJson);
            ProcessResponsejson.put("participantId", "" + participantId);
            ProcessResponsejson.put("data", generateresult(activityObj, getIntent().getStringExtra(EXTRA_STUDYID)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ProcessResponsejson;
    }

    private JSONObject generateresult(ActivityObj activityObj, String stringExtra) {

        try {
            JSONObject dataobj = new JSONObject();
            dataobj.put("startTime", activityObj.getMetadata().getStartDate());
            dataobj.put("endTime", activityObj.getMetadata().getEndDate());
            dataobj.put("resultType", activityObj.getType());

            JSONArray resultarray = new JSONArray();
            JsonParser jsonParser = new JsonParser();
            RealmResults<StepRecordCustom> StepRecord = realm.where(StepRecordCustom.class).equalTo("taskId", stringExtra).findAll();
            boolean formskipped = true;
            for (int i = 0; i < activityObj.getSteps().size(); i++) {
                for (int j = 0; j < StepRecord.size(); j++) {
                    if (StepRecord.get(j).getStepId().equalsIgnoreCase(activityObj.getSteps().get(i).getKey())) {


                        if (!activityObj.getSteps().get(i).getType().equalsIgnoreCase("task")) {
                            JSONObject resultarrobj = new JSONObject();
                            resultarrobj.put("resultType", activityObj.getSteps().get(i).getResultType());
                            resultarrobj.put("key", activityObj.getSteps().get(i).getKey());

                            resultarrobj.put("startTime", AppController.getDateFormat().format(StepRecord.get(j).getStarted()));
                            resultarrobj.put("endTime", AppController.getDateFormat().format(StepRecord.get(j).getCompleted()));
                            if (!activityObj.getSteps().get(i).getResultType().equalsIgnoreCase("grouped")) {
                                if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                    resultarrobj.put("skipped", true);
                                    resultarrobj.put("value", "");
                                } else {
                                    if (StepRecord.get(j).getResult().equalsIgnoreCase("{\"answer\":[]}")) {
                                        resultarrobj.put("skipped", true);
                                    } else {
                                        resultarrobj.put("skipped", false);
                                    }
                                    resultarrobj.put("value", findPrimitiveData(jsonParser.parse(StepRecord.get(j).getResult()), activityObj, i));
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
                                        String identifier = (String) mapEntry.get("identifier");
                                        if (k == 0) {
                                            if (!identifier.contains("_addMoreEnabled")) {
                                                for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                    if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                        for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                            if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get("identifier"))) {
                                                                jsonObject.put("resultType", activityObj.getSteps().get(c).getSteps().get(c1).getResultType());
                                                            }
                                                        }
                                                    }
                                                }
                                                jsonObject.put("key", mapEntry.get("identifier"));
                                                jsonObject.put("startTime", mapEntry.get("startDate"));
                                                jsonObject.put("endTime", mapEntry.get("endDate"));


                                                Map<String, Object> mapEntryResult = (Map<String, Object>) mapEntry.get("results");
                                                Object o = mapEntryResult.get("answer");
                                                if (o instanceof Object[]) {
                                                    Object[] objects = (Object[]) o;
                                                    if (objects.length > 0) {
                                                        if (objects[0] instanceof Integer) {
                                                            JSONArray jsonArray1 = new JSONArray();
                                                            for (int l = 0; l < objects.length; l++) {
                                                                for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                                    if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                                        for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                                            if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get("identifier"))) {
                                                                                jsonArray1.put(activityObj.getSteps().get(c).getSteps().get(c1).getFormat().getTextChoices().get((int) objects[l]).getValue());
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            jsonObject.put("value", jsonArray1);
                                                        } else if (objects[0] instanceof String) {
                                                            JSONArray jsonArray1 = new JSONArray();
                                                            for (int l = 0; l < objects.length; l++) {
                                                                jsonArray1.put((String) objects[l]);
                                                            }
                                                            jsonObject.put("value", jsonArray1);
                                                        }
                                                    } else {
                                                        jsonObject.put("value", new JSONArray());
                                                    }
                                                } else {
                                                    jsonObject.put("value", mapEntryResult.get("answer"));
                                                }

                                                try {
                                                    if (jsonObject.get("value") == null) {
                                                        resultarrobj.put("skipped", true);
                                                        jsonObject.put("skipped", true);
                                                    } else if (jsonObject.get("value").toString().equalsIgnoreCase("")) {
                                                        resultarrobj.put("skipped", true);
                                                        jsonObject.put("skipped", true);
                                                    } else if (jsonObject.get("value").toString().equalsIgnoreCase("[]")) {
                                                        resultarrobj.put("skipped", true);
                                                        jsonObject.put("skipped", true);
                                                    } else {
                                                        resultarrobj.put("skipped", false);
                                                        jsonObject.put("skipped", false);
                                                        formskipped = false;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    resultarrobj.put("skipped", true);
                                                    jsonObject.put("skipped", true);
                                                }

                                                jsonArray.put(jsonObject);
                                                update = true;
                                            }
                                        } else if (identifier.contains(k + "_addMoreEnabled")) {
                                            for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                    for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                        if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get("identifier"))) {
                                                            jsonObject.put("resultType", activityObj.getSteps().get(c).getSteps().get(c1).getResultType());
                                                        }
                                                    }
                                                }
                                            }
                                            jsonObject.put("key", mapEntry.get("identifier").toString().substring(0, mapEntry.get("identifier").toString().lastIndexOf("-")));
                                            jsonObject.put("startTime", mapEntry.get("startDate"));
                                            jsonObject.put("endTime", mapEntry.get("endDate"));
                                            jsonObject.put("skipped", false);
                                            Map<String, Object> mapEntryResult = (Map<String, Object>) mapEntry.get("results");
                                            Object o = mapEntryResult.get("answer");
                                            if (o instanceof Object[]) {
                                                Object[] objects = (Object[]) o;
                                                if (objects.length > 0) {
                                                    if (objects[0] instanceof Integer) {
                                                        JSONArray jsonArray1 = new JSONArray();
                                                        for (int l = 0; l < objects.length; l++) {
                                                            for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                                if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                                    for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                                        if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get("identifier"))) {
                                                                            jsonArray1.put(activityObj.getSteps().get(c).getSteps().get(c1).getFormat().getTextChoices().get((int) objects[l]).getValue());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        jsonObject.put("value", jsonArray1);
                                                    } else if (objects[0] instanceof String) {
                                                        JSONArray jsonArray1 = new JSONArray();
                                                        for (int l = 0; l < objects.length; l++) {
                                                            jsonArray1.put((String) objects[l]);
                                                        }
                                                        jsonObject.put("value", jsonArray1);
                                                    }
                                                } else {
                                                    jsonObject.put("value", new JSONArray());
                                                }
                                            } else {
                                                jsonObject.put("value", mapEntryResult.get("answer"));
                                            }


                                            try {
                                                if (jsonObject.get("value") == null) {
                                                    resultarrobj.put("skipped", true);
                                                    jsonObject.put("skipped", true);
                                                } else if (jsonObject.get("value").toString().equalsIgnoreCase("")) {
                                                    resultarrobj.put("skipped", true);
                                                    jsonObject.put("skipped", true);
                                                } else if (jsonObject.get("value").toString().equalsIgnoreCase("[]")) {
                                                    resultarrobj.put("skipped", true);
                                                    jsonObject.put("skipped", true);
                                                } else {
                                                    resultarrobj.put("skipped", false);
                                                    jsonObject.put("skipped", false);
                                                    formskipped = false;
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                resultarrobj.put("skipped", true);
                                                jsonObject.put("skipped", true);
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
                                    resultarrobj.put("skipped", true);
                                    resultarrobj.put("value", new JSONArray());
                                } else {
                                    resultarrobj.put("skipped", false);
                                    resultarrobj.put("value", jsonArrayMain);
                                }
                            }
                            resultarray.put(resultarrobj);

                            dataobj.put("results", resultarray);
                        } else {

                            JSONObject durationobj = new JSONObject();
                            durationobj.put("resultType", "numeric");
                            durationobj.put("key", "duration");
                            durationobj.put("startTime", null);
                            durationobj.put("endTime", null);
                            if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                durationobj.put("skipped", true);
                            } else {
                                durationobj.put("skipped", false);
                            }
                            JSONObject activejsonObject = new JSONObject(StepRecord.get(j).getResult());
                            JSONObject answerjsonobj = activejsonObject.getJSONObject("answer");
                            durationobj.put("value", answerjsonobj.getString("duration"));

                            JSONObject valueobj = new JSONObject();
                            valueobj.put("resultType", "numeric");
                            valueobj.put("key", "count");
                            valueobj.put("startTime", null);
                            valueobj.put("endTime", null);
                            if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                valueobj.put("skipped", true);
                            } else {
                                valueobj.put("skipped", false);
                            }
                            valueobj.put("value", answerjsonobj.getInt("value"));

                            resultarray.put(durationobj);
                            resultarray.put(valueobj);

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("value", resultarray);
                            jsonObject.put("startTime", AppController.getDateFormat().format(StepRecord.get(j).getStarted()));
                            jsonObject.put("endTime", AppController.getDateFormat().format(StepRecord.get(j).getCompleted()));
                            jsonObject.put("resultType", "grouped");
                            jsonObject.put("key", activityObj.getSteps().get(i).getKey());
                            if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                jsonObject.put("skipped", true);
                            } else {
                                jsonObject.put("skipped", false);
                            }
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(jsonObject);

                            dataobj.put("results", jsonArray);
                        }
                    }
                }
            }
            return dataobj;
        } catch (JSONException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    public void updateUserPreference() {
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.userid), ""));


        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_ACTIVITY_PREFERENCE, UPDATE_USERPREFERENCE_RESPONSECODE, this, LoginData.class, null, header, getActivityPreferenceJson(), false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    private JSONObject getActivityPreferenceJson() {
        String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
        surveyId = surveyId.substring(0, surveyId.lastIndexOf("_"));
        String activityId[] = surveyId.split("_STUDYID_");

        JSONObject jsonObject = new JSONObject();

        JSONArray activitylist = new JSONArray();
        JSONObject activityStatus = new JSONObject();
        JSONObject activityRun = new JSONObject();
        try {
            activityStatus.put("studyId", getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID));
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
            e.printStackTrace();
        }

        activitylist.put(activityStatus);

        try {
            jsonObject.put("studyId", getIntent().getStringExtra(STUDYID));
            jsonObject.put("activity", activitylist);
        } catch (JSONException e) {
            e.printStackTrace();
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

                updateStudyState("" + (int) completion, "" + (int) adherence);


            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == UPDATE_STUDY_PREFERENCE) {
            AppController.getHelperProgressDialog().dismissDialog();
            String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
            surveyId = surveyId.substring(0, surveyId.lastIndexOf("_"));
            String activityId[] = surveyId.split("_STUDYID_");
            int completedRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.COMPLETED_RUN, 0);
            completedRun = completedRun + 1;
            int currentRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0);
            int missedRun = currentRun - completedRun;
            dbServiceSubscriber.updateActivityPreferenceDB(this,activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0), SurveyActivitiesFragment.COMPLETED, getIntent().getIntExtra(CustomSurveyViewTaskActivity.TOTAL_RUN, 0), completedRun, missedRun, getIntent().getStringExtra(CustomSurveyViewTaskActivity.ACTIVITY_VERSION));
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
                dbServiceSubscriber.updateStudyPreference(this,studies, completion, adherence);
            }
            dbServiceSubscriber.updateActivityRunToDB(this,activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0));
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
                    mDeleteIndexNumberDB = number;
                    AppController.pendingService(this,number, "post_object", URLs.UPDATE_ACTIVITY_PREFERENCE, "", getActivityPreferenceJson().toString(), "registration", "", "", "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ////////// offline data storing activity preference finish


            /////////// offline data storing study preference
            try {
                int number = dbServiceSubscriber.getUniqueID(realm);
                if (number == 0) {
                    number = 1;
                } else {
                    number += 1;
                }
                mDeleteIndexNumberDB = number;

                //calculate completion and adherence
                int completed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.completedRuns), ""));
                int missed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.missedRuns), ""));
                int total = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.totalRuns), ""));

                if ((double) total > 0)
                    completion = (((double) completed + (double) missed + 1d) / (double) total) * 100d;

                if (((double) completed + (double) missed + 1d) > 0)
                    adherence = (((double) completed + 1d) / ((double) completed + (double) missed + 1d)) * 100d;

                AppController.pendingService(this,number, "post_object", URLs.UPDATE_STUDY_PREFERENCE, "", getStudyPreferenceJson("" + (int) completion, "" + (int) adherence).toString(), "registration", "", "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ////////// offline data storing study preference finish


            try {
                String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
                surveyId = surveyId.substring(0, surveyId.lastIndexOf("_"));
                int completedRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.COMPLETED_RUN, 0);
                completedRun = completedRun + 1;
                int currentRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0);
                int missedRun = currentRun - completedRun;
                String activityId[] = surveyId.split("_STUDYID_");
                dbServiceSubscriber.updateActivityPreferenceDB(this,activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0), SurveyActivitiesFragment.COMPLETED, getIntent().getIntExtra(CustomSurveyViewTaskActivity.TOTAL_RUN, 0), completedRun, missedRun, getIntent().getStringExtra(CustomSurveyViewTaskActivity.ACTIVITY_VERSION));
                dbServiceSubscriber.updateActivityRunToDB(this,activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0));
            } catch (Exception e) {
                e.printStackTrace();
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
                    for (int i = 0; i < activityObj.getSteps().get(position).getFormat().getTextChoices().size(); i++) {
                        if (activityObj.getSteps().get(position).getFormat().getTextChoices().get(i).getOther() != null) {
                            try {
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(anArr.getAsString());
                                    jsonArray.put(jsonObject);
                                } catch (JSONException e) {
                                }
                            } catch (Exception e) {
                            }
                        } else if (activityObj.getSteps().get(position).getFormat().getTextChoices().get(i).getValue().equalsIgnoreCase("" + anArr.getAsString())) {
                            jsonArray.put(activityObj.getSteps().get(position).getFormat().getTextChoices().get(i).getValue());
                        }
                    }
                }
                return jsonArray;
            } else {
                if (entry.getValue().isJsonPrimitive())
                    prim = entry.getValue().getAsJsonPrimitive();
                else
                    return entry.getValue().getAsJsonObject();
            }
        }
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
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String
            statusCode, T response) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(SurveyCompleteActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(SurveyCompleteActivity.this, errormsg);
        } else {
            /////////// offline data storing for response server

            String surveyId = getIntent().getStringExtra(CustomSurveyViewTaskActivity.EXTRA_STUDYID);
            surveyId = surveyId.substring(0, surveyId.lastIndexOf("_"));
            String activityId[] = surveyId.split("_STUDYID_");
            ActivityObj activityObj = dbServiceSubscriber.getActivityBySurveyId(getIntent().getStringExtra(STUDYID), activityId[1], realm);

            RealmResults<Activities> activitiesRealmResults = realm.where(Activities.class).equalTo("studyId", getIntent().getStringExtra(STUDYID)).findAll();
            Activities activities = null;
            for (int i = 0; i < activitiesRealmResults.size(); i++) {
                if (activitiesRealmResults.get(i).getActivityId().equalsIgnoreCase(activityId[1]))
                    activities = activitiesRealmResults.get(i);
            }

            Studies studies = realm.where(Studies.class).equalTo("studyId", getIntent().getStringExtra(STUDYID)).findFirst();
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
                mDeleteIndexNumberDB = number;
                AppController.pendingService(this,number, "post_object", URLs.PROCESS_RESPONSE, "", getResponseDataJson(activityObj, activities, participantId).toString(), "response", "", "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            /////////////////////////// offline data storing for response server finish


            /////////// offline data storing activity preference
            try {
                int number = dbServiceSubscriber.getUniqueID(realm);
                if (number == 0) {
                    number = 1;
                } else {
                    number += 1;
                }
                mDeleteIndexNumberDB = number;
                AppController.pendingService(this,number, "post_object", URLs.UPDATE_ACTIVITY_PREFERENCE, "", getActivityPreferenceJson().toString(), "registration", "", "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ////////// offline data storing activity preference finish


            /////////// offline data storing study preference
            try {
                int number = dbServiceSubscriber.getUniqueID(realm);
                if (number == 0) {
                    number = 1;
                } else {
                    number += 1;
                }
                mDeleteIndexNumberDB = number;

                //calculate completion and adherence
                int completed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.completedRuns), ""));
                int missed = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.missedRuns), ""));
                int total = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(SurveyCompleteActivity.this, getResources().getString(R.string.totalRuns), ""));

                if ((double) total > 0)
                    completion = (((double) completed + (double) missed + 1d) / (double) total) * 100d;

                if (((double) completed + (double) missed + 1d) > 0)
                    adherence = (((double) completed + 1d) / ((double) completed + (double) missed + 1d)) * 100d;

                AppController.pendingService(this,number, "post_object", URLs.UPDATE_STUDY_PREFERENCE, "", getStudyPreferenceJson("" + (int) completion, "" + (int) adherence).toString(), "registration", "", "", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ////////// offline data storing study preference finish


            ////////// Update Db and leave

            int completedRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.COMPLETED_RUN, 0);
            completedRun = completedRun + 1;
            int currentRun = getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0);
            int missedRun = currentRun - completedRun;
            dbServiceSubscriber.updateActivityPreferenceDB(this,activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0), SurveyActivitiesFragment.COMPLETED, getIntent().getIntExtra(CustomSurveyViewTaskActivity.TOTAL_RUN, 0), completedRun, missedRun, getIntent().getStringExtra(CustomSurveyViewTaskActivity.ACTIVITY_VERSION));
            dbServiceSubscriber.updateActivityRunToDB(this,activityId[1], getIntent().getStringExtra(CustomSurveyViewTaskActivity.STUDYID), getIntent().getIntExtra(CustomSurveyViewTaskActivity.RUNID, 0));
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


        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_STUDY_PREFERENCE, UPDATE_STUDY_PREFERENCE, this, LoginData.class, null, header, getStudyPreferenceJson(completion, adherence), false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    private JSONObject getStudyPreferenceJson(String completion, String adherence) {
        JSONObject jsonObject = new JSONObject();

        JSONArray studieslist = new JSONArray();
        JSONObject studiestatus = new JSONObject();
        try {
            studiestatus.put("studyId", getIntent().getStringExtra(STUDYID));
            studiestatus.put("completion", completion);
            studiestatus.put("adherence", adherence);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        studieslist.put(studiestatus);
        try {
            jsonObject.put("studies", studieslist);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

}
