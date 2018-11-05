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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.hphc.mystudies.R;
import com.hphc.mystudies.notificationModule.NotificationModuleSubscriber;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.studyAppModule.acvitityListModel.ActivitiesWS;
import com.hphc.mystudies.studyAppModule.custom.Result.StepRecordCustom;
import com.hphc.mystudies.studyAppModule.events.GetResourceListEvent;
import com.hphc.mystudies.studyAppModule.events.GetUserStudyInfoEvent;
import com.hphc.mystudies.studyAppModule.events.WithdrawFromStudyEvent;
import com.hphc.mystudies.studyAppModule.studyModel.NotificationDbResources;
import com.hphc.mystudies.studyAppModule.studyModel.Resource;
import com.hphc.mystudies.studyAppModule.studyModel.StudyHome;
import com.hphc.mystudies.studyAppModule.studyModel.StudyResource;
import com.hphc.mystudies.userModule.UserModulePresenter;
import com.hphc.mystudies.userModule.event.UpdatePreferenceEvent;
import com.hphc.mystudies.userModule.webserviceModel.LoginData;
import com.hphc.mystudies.userModule.webserviceModel.Studies;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCallResponseServer;
import com.hphc.mystudies.webserviceModule.apiHelper.ConnectionDetector;
import com.hphc.mystudies.webserviceModule.apiHelper.HttpRequest;
import com.hphc.mystudies.webserviceModule.apiHelper.Responsemodel;
import com.hphc.mystudies.webserviceModule.events.RegistrationServerConfigEvent;
import com.hphc.mystudies.webserviceModule.events.ResponseServerConfigEvent;
import com.hphc.mystudies.webserviceModule.events.WCPConfigEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.hphc.mystudies.utils.AppController.getLabkeyDateFormat;

public class SurveyResourcesFragment extends Fragment implements ApiCall.OnAsyncRequestComplete, ApiCallResponseServer.OnAsyncRequestComplete {

    private static final int STUDY_INFO = 10;
    private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 100;
    private static final int RESOURCE_REQUEST_CODE = 213;
    private static final int WITHDRAWFROMSTUDY = 105;
    private static final String STUDYID_TO_SPLIT = "_STUDYID_";
    private static final String ANSWER = "answer";
    private static final String TIMEOUT = "timeout";
    private static final String HTTP_NOT_OK = "http_not_ok";
    private static final String SESSION_EXPIRED = "session expired";
    private static final String VALUE = "value";
    private RecyclerView mStudyRecyclerView;
    private Context mContext;
    private AppCompatTextView mTitle;
    private RealmList<Resource> mResourceArrayList;
    private String mStudyId;
    RelativeLayout mBackBtn;
    private StudyHome mStudyHome;
    private StudyResource mStudyResource;
    DBServiceSubscriber dbServiceSubscriber;
    public static final String RESOURCES = "resources";
    Realm mRealm;
    Studies mStudies;
    private String mRegistrationServer = "false";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_resources, container, false);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj();
        initializeXMLId(view);
        setTextForView();
        setFont();
        mStudies = dbServiceSubscriber.getStudies(((SurveyActivity) mContext).getStudyId(), mRealm);
        mGetResourceListWebservice();

        return view;
    }

    private void mGetResourceListWebservice() {

        AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
        HashMap<String, String> header = new HashMap<>();
        mStudyId = ((SurveyActivity) mContext).getStudyId();
        header.put("studyId", mStudyId);
        String url = URLs.RESOURCE_LIST + "?studyId=" + mStudyId;
        GetResourceListEvent getResourceListEvent = new GetResourceListEvent();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, RESOURCE_REQUEST_CODE,
                getActivity(), StudyResource.class, null, header, null, false, this);

        getResourceListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetResourceListEvent(getResourceListEvent);
    }

    private void callGetStudyInfoWebservice() {
        AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
        HashMap<String, String> header = new HashMap<>();
        String url = URLs.STUDY_INFO + "?studyId=" + mStudyId;
        GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, STUDY_INFO, getActivity(), StudyHome.class, null, header, null, false, this);

        getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
    }

    private void initializeXMLId(View view) {
        mTitle = (AppCompatTextView) view.findViewById(R.id.title);
        mStudyRecyclerView = (RecyclerView) view.findViewById(R.id.studyRecyclerView);
        mBackBtn = (RelativeLayout) view.findViewById(R.id.backBtn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, StudyActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                mContext.startActivity(mainIntent);
                ((Activity) mContext).finish();
            }
        });
    }

    private void setTextForView() {
        mTitle.setText(getResources().getString(R.string.resources));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(getActivity(), "bold"));
        } catch (Exception e) {
        }
    }


    @Override
    public <T> void asyncResponse(T response, int responseCode) {


        // RESOURCE_REQUEST_CODE: while coming screen, every time after resourcelist service calling study info
        // stop and again start progress bar, to avoid that using this
        if (responseCode != RESOURCE_REQUEST_CODE)
            AppController.getHelperProgressDialog().dismissDialog();

        if (responseCode == RESOURCE_REQUEST_CODE) {
            // call study info
            callGetStudyInfoWebservice();
            if (response != null) {
                mStudyResource = (StudyResource) response;
            }
        } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {

            dbServiceSubscriber.updateStudyWithddrawnDB(mStudyId, StudyFragment.WITHDRAWN);
            dbServiceSubscriber.deleteActivityDataRow(mStudyId);
            dbServiceSubscriber.deleteActivityWSData(mStudyId);

            Intent intent = new Intent(mContext, StudyActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            mContext.startActivity(mainIntent);
            ((Activity) mContext).finish();
        } else if (responseCode == STUDY_INFO && response != null) {
            mStudyHome = (StudyHome) response;
            mStudyHome.setmStudyId(mStudyId);
            dbServiceSubscriber.saveStudyInfoToDB(mStudyHome);


            if (mStudyResource != null) {
                mResourceArrayList = mStudyResource.getResources();
                if (mResourceArrayList == null) {
                    mResourceArrayList = new RealmList<>();
                }
                addStaticVal();
                //                    mSetResourceAdapter();
                if (mStudyHome != null && mStudies != null)
                    new ResponseData(((SurveyActivity) mContext).getStudyId(), mStudyHome.getAnchorDate().getQuestionInfo().getActivityId(), mStudies.getParticipantId()).execute();
                // primary key mStudyId
                mStudyResource.setmStudyId(mStudyId);
                // remove duplicate and
                dbServiceSubscriber.deleteStudyResourceDuplicateRow(mStudyId);
                dbServiceSubscriber.saveResourceList(mStudyResource);
            }
        }

    }

    private void mSetResourceAdapter() {
        RealmList<Resource> resources = new RealmList<>();
        for (int i = 0; i < mResourceArrayList.size(); i++) {
            if (mResourceArrayList.get(i).getAudience() != null && mResourceArrayList.get(i).getAudience().equalsIgnoreCase("All")) {
                if (mResourceArrayList.get(i).getAvailability() != null && mResourceArrayList.get(i).getAvailability().getAvailableDate() != null && !mResourceArrayList.get(i).getAvailability().getAvailableDate().equalsIgnoreCase("")) {
                    try {

                        Calendar currentday = Calendar.getInstance();
                        currentday.set(Calendar.HOUR, 0);
                        currentday.set(Calendar.MINUTE, 0);
                        currentday.set(Calendar.SECOND, 0);
                        currentday.set(Calendar.AM_PM, Calendar.AM);


                        Calendar expiryDate = Calendar.getInstance();
                        expiryDate.setTime(AppController.getDateFormatType10().parse(mResourceArrayList.get(i).getAvailability().getExpiryDate()));
                        expiryDate.set(Calendar.HOUR, 11);
                        expiryDate.set(Calendar.MINUTE, 59);
                        expiryDate.set(Calendar.SECOND, 59);
                        expiryDate.set(Calendar.AM_PM, Calendar.PM);

                        Calendar availableDate = Calendar.getInstance();
                        availableDate.setTime(AppController.getDateFormatType10().parse(mResourceArrayList.get(i).getAvailability().getAvailableDate()));
                        availableDate.set(Calendar.HOUR, 0);
                        availableDate.set(Calendar.MINUTE, 0);
                        availableDate.set(Calendar.SECOND, 0);
                        availableDate.set(Calendar.AM_PM, Calendar.AM);

                        if ((currentday.getTime().before(expiryDate.getTime()) || currentday.getTime().equals(expiryDate.getTime())) && (currentday.getTime().after(availableDate.getTime()) || currentday.getTime().equals(availableDate.getTime()))) {
                            resources.add(mResourceArrayList.get(i));
                        }
                    } catch (ParseException e) {
                    }
                } else {
                    resources.add(mResourceArrayList.get(i));
                }

            } else if (mResourceArrayList.get(i).getAudience() != null && mResourceArrayList.get(i).getAudience().equalsIgnoreCase("Limited")) {
                if (mResourceArrayList.get(i).getAvailability().getAvailableDate().equalsIgnoreCase("")) {
                    StepRecordCustom stepRecordCustom = dbServiceSubscriber.getSurveyResponseFromDB(((SurveyActivity) mContext).getStudyId() + STUDYID_TO_SPLIT + mStudyHome.getAnchorDate().getQuestionInfo().getActivityId(), mStudyHome.getAnchorDate().getQuestionInfo().getKey(), mRealm);
                    if (stepRecordCustom != null) {
                        Calendar startCalender = Calendar.getInstance();
                        Calendar endCalender = Calendar.getInstance();
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(stepRecordCustom.getResult());
                            startCalender.setTime(AppController.getDateFormat().parse("" + jsonObject.get(ANSWER)));
                            startCalender.add(Calendar.DATE, mResourceArrayList.get(i).getAvailability().getStartDays());
                            startCalender.set(Calendar.HOUR, 0);
                            startCalender.set(Calendar.MINUTE, 0);
                            startCalender.set(Calendar.SECOND, 0);
                            startCalender.set(Calendar.AM_PM, Calendar.AM);
                            NotificationDbResources notificationsDb = null;
                            RealmResults<NotificationDbResources> notificationsDbs = dbServiceSubscriber.getNotificationDbResources(mStudyHome.getAnchorDate().getQuestionInfo().getActivityId(), ((SurveyActivity) mContext).getStudyId(), RESOURCES, mRealm);
                            if (notificationsDbs != null && notificationsDbs.size() > 0) {
                                for (int j = 0; j < notificationsDbs.size(); j++) {
                                    if (notificationsDbs.get(j).getResourceId().equalsIgnoreCase(mResourceArrayList.get(i).getResourcesId())) {
                                        notificationsDb = notificationsDbs.get(j);
                                        break;
                                    }
                                }
                            }
                            if (notificationsDb == null) {
                                setRemainder(startCalender, mStudyHome.getAnchorDate().getQuestionInfo().getActivityId(), ((SurveyActivity) mContext).getStudyId(), mResourceArrayList.get(i).getNotificationText(), mResourceArrayList.get(i).getResourcesId());
                            }

                            endCalender.setTime(AppController.getDateFormat().parse("" + jsonObject.get(ANSWER)));
                            endCalender.add(Calendar.DATE, mResourceArrayList.get(i).getAvailability().getEndDays());
                            endCalender.set(Calendar.HOUR, 11);
                            endCalender.set(Calendar.MINUTE, 59);
                            endCalender.set(Calendar.SECOND, 59);
                            endCalender.set(Calendar.AM_PM, Calendar.PM);


                            Calendar currentday = Calendar.getInstance();
                            currentday.set(Calendar.HOUR, 0);
                            currentday.set(Calendar.MINUTE, 0);
                            currentday.set(Calendar.SECOND, 0);
                            currentday.set(Calendar.AM_PM, Calendar.AM);

                            if ((currentday.getTime().after(startCalender.getTime()) || currentday.getTime().equals(startCalender.getTime())) && (currentday.getTime().before(endCalender.getTime()) || currentday.getTime().equals(endCalender.getTime()))) {
                                resources.add(mResourceArrayList.get(i));
                            }
                        } catch (JSONException | ParseException e) {
                        }

                    }
                }

            } else if (mResourceArrayList.get(i).getAudience() == null) {
                resources.add(mResourceArrayList.get(i));
            }
        }
        mStudyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mStudyRecyclerView.setNestedScrollingEnabled(false);
        ResourcesListAdapter resourcesListAdapter = new ResourcesListAdapter(getActivity(), resources, this);
        mStudyRecyclerView.setAdapter(resourcesListAdapter);
    }

    private class ResponseData extends AsyncTask<String, Void, String> {

        String activityId;
        String participateId;
        String response = null;
        String responseCode = null;
        String studyId;
        Responsemodel mResponseModel;

        ResponseData(String studyId, String activityId, String participateId) {
            this.studyId = studyId;
            this.activityId = activityId;
            this.participateId = participateId;
        }

        @Override
        protected String doInBackground(String... params) {
            ConnectionDetector connectionDetector = new ConnectionDetector(mContext);

            if (connectionDetector.isConnectingToInternet()) {
                mResponseModel = HttpRequest.getRequest(URLs.PROCESSRESPONSEDATA + "sql=SELECT%20*%20FROM%20%22" + activityId + "%22&participantId=" + participateId, new HashMap<String, String>(), "Response");
                responseCode = mResponseModel.getResponseCode();
                response = mResponseModel.getResponseData();
                if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase(TIMEOUT)) {
                    response = TIMEOUT;
                } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
                    response = "error";
                } else if (Integer.parseInt(responseCode) >= 201 && Integer.parseInt(responseCode) < 300 && response.equalsIgnoreCase("")) {
                    response = "No data";
                } else if (Integer.parseInt(responseCode) >= 400 && Integer.parseInt(responseCode) < 500 && response.equalsIgnoreCase(HTTP_NOT_OK)) {
                    response = "client error";
                } else if (Integer.parseInt(responseCode) >= 500 && Integer.parseInt(responseCode) < 600 && response.equalsIgnoreCase(HTTP_NOT_OK)) {
                    response = "server error";
                } else if (response.equalsIgnoreCase(HTTP_NOT_OK)) {
                    response = "Unknown error";
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = SESSION_EXPIRED;
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK && !response.equalsIgnoreCase("")) {
                } else {
                    response = getString(R.string.unknown_error);
                }
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                if (response.equalsIgnoreCase(SESSION_EXPIRED)) {
                    AppController.getHelperProgressDialog().dismissDialog();
                    AppController.getHelperSessionExpired(mContext, SESSION_EXPIRED);
                } else if (response.equalsIgnoreCase(TIMEOUT)) {
                    mSetResourceAdapter();
                    AppController.getHelperProgressDialog().dismissDialog();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.connection_timeout), Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(responseCode) == 500) {
                    try {
                        mSetResourceAdapter();
                        AppController.getHelperProgressDialog().dismissDialog();
                    } catch (Exception e) {
                        mSetResourceAdapter();
                        AppController.getHelperProgressDialog().dismissDialog();
                    }
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK) {
                    try {
                        SimpleDateFormat simpleDateFormat = getLabkeyDateFormat();
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("rows");
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = (JSONObject) new JSONObject(String.valueOf(jsonArray.get(i))).get("data");
                            Type type = new TypeToken<Map<String, Object>>() {
                            }.getType();
                            Map<String, Object> myMap = gson.fromJson(String.valueOf(jsonObject1), type);
                            StepRecordCustom stepRecordCustom = new StepRecordCustom();
                            Date completedDate = new Date();
                            int duration = 0;
                            try {
                                Object completedDateValMap = gson.toJson(myMap.get("Created"));
                                Map<String, Object> completedDateVal = gson.fromJson(String.valueOf(completedDateValMap), type);
                                completedDate = simpleDateFormat.parse(String.valueOf(completedDateVal.get(VALUE)));
                            } catch (JsonSyntaxException | ParseException e) {
                            }
                            try {
                                Object durationValMap = gson.toJson(myMap.get("duration"));
                                Map<String, Object> completedDateVal = gson.fromJson(String.valueOf(durationValMap), type);
                                duration = Integer.parseInt(String.valueOf(completedDateVal.get(VALUE)));
                            } catch (JsonSyntaxException e) {
                            }
                            for (Map.Entry<String, Object> entry : myMap.entrySet()) {
                                String key = entry.getKey();
                                String valueobj = gson.toJson(entry.getValue());
                                Map<String, Object> vauleMap = gson.fromJson(String.valueOf(valueobj), type);
                                Object value = vauleMap.get(VALUE);
                                if (!key.equalsIgnoreCase("container")
                                        && !key.equalsIgnoreCase("ParticipantId")
                                        && !key.equalsIgnoreCase("EntityId")
                                        && !key.equalsIgnoreCase("Modified")
                                        && !key.equalsIgnoreCase("lastIndexed")
                                        && !key.equalsIgnoreCase("ModifiedBy")
                                        && !key.equalsIgnoreCase("CreatedBy")
                                        && !key.equalsIgnoreCase("Key")
                                        && !key.equalsIgnoreCase("Created")) {
                                    int runId = dbServiceSubscriber.getActivityRunForStatsAndCharts(activityId, studyId, completedDate, mRealm);
                                    stepRecordCustom.setStepId(key);
                                    stepRecordCustom.setStudyId(studyId);
                                    stepRecordCustom.setActivityID(studyId + STUDYID_TO_SPLIT + activityId);
                                    stepRecordCustom.setTaskId(studyId + STUDYID_TO_SPLIT + activityId + "_" + runId);
                                    stepRecordCustom.setTaskStepID(studyId + STUDYID_TO_SPLIT + activityId + "_" + runId + "_" + key);
                                    stepRecordCustom.setCompleted(completedDate);
                                    stepRecordCustom.setStarted(completedDate);
                                    JSONObject jsonObject2 = new JSONObject();
                                    try {
                                        Date anchordate = AppController.getLabkeyDateFormat().parse("" + value);
                                        value = AppController.getDateFormat().format(anchordate);
                                    } catch (ParseException e) {
                                    }
                                    ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(activityId, studyId, mRealm);
                                    if (activityObj.getType().equalsIgnoreCase("task")) {
                                        JSONObject jsonObject3 = new JSONObject();
                                        jsonObject3.put(VALUE, value);
                                        jsonObject3.put("duration", duration);

                                        jsonObject2.put(ANSWER, jsonObject3);
                                    } else {
                                        jsonObject2.put(ANSWER, value);
                                    }
                                    stepRecordCustom.setResult(String.valueOf(jsonObject2));
                                    Number currentIdNum = dbServiceSubscriber.getStepRecordCustomId(mRealm);
                                    if (currentIdNum == null) {
                                        stepRecordCustom.setId(1);
                                    } else {
                                        stepRecordCustom.setId(currentIdNum.intValue() + 1);
                                    }
                                    dbServiceSubscriber.updateStepRecord(stepRecordCustom);
                                }
                            }
                        }
                        AppController.getHelperProgressDialog().dismissDialog();
                        mSetResourceAdapter();
                    } catch (Exception e) {
                        mSetResourceAdapter();
                        AppController.getHelperProgressDialog().dismissDialog();
                    }
                } else {
                    mSetResourceAdapter();
                    AppController.getHelperProgressDialog().dismissDialog();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.unable_to_retrieve_data), Toast.LENGTH_SHORT).show();
                }
            } else {
                mSetResourceAdapter();
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(mContext, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setRemainder(Calendar startCalender, String activityId, String studyId, String notificationTest, String resourceId) {
        NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
        notificationModuleSubscriber.generateAnchorDateLocalNotification(startCalender.getTime(), activityId, studyId, mContext, notificationTest, resourceId);
    }

    private void addStaticVal() {
        ArrayList<String> labelArray = new ArrayList<String>();
        ArrayList<Resource> mTempResourceArrayList = new ArrayList<>();
        mTempResourceArrayList.addAll(mResourceArrayList);
        mResourceArrayList.clear();
        labelArray.add(getResources().getString(R.string.about_study));
        labelArray.add(getResources().getString(R.string.consent_pdf));
        labelArray.add(getResources().getString(R.string.leave_study));

        for (int i = 0; i < labelArray.size(); i++) {
            Resource r = new Resource();
            r.setTitle(labelArray.get(i));
            mResourceArrayList.add(r);
        }
        mResourceArrayList.addAll(mTempResourceArrayList);

        mTempResourceArrayList.clear();
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(mContext, errormsg);
        } else {
            // offline functionality
            if (responseCode == RESOURCE_REQUEST_CODE) {
                try {
                    if (dbServiceSubscriber.getStudyResource(mStudyId, mRealm) == null) {
                        Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
                    } else if (dbServiceSubscriber.getStudyResource(mStudyId, mRealm).getResources() == null) {
                        Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
                    } else {
                        mResourceArrayList = dbServiceSubscriber.getStudyResource(mStudyId, mRealm).getResources();
                        if (mResourceArrayList == null || mResourceArrayList.size() == 0) {
                            Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                mStudyHome = dbServiceSubscriber.getWithdrawalType(mStudyId, mRealm);
                                if (mStudyHome != null) {
                                    if (mStudies != null)
                                        new ResponseData(((SurveyActivity) mContext).getStudyId(), mStudyHome.getAnchorDate().getQuestionInfo().getActivityId(), mStudies.getParticipantId()).execute();
                                } else {
                                    Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public String getType() {
        return mStudyHome.getWithdrawalConfig().getType();
    }

    public String getLeaveStudyMessage() {
        return mStudyHome.getWithdrawalConfig().getMessage();
    }

    public void updateuserpreference() {
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("studyId", ((SurveyActivity) mContext).getStudyId());
            jsonObject.put("deleteData", mRegistrationServer);
        } catch (JSONException e) {
        }

        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("delete_object", URLs.WITHDRAW, UPDATE_USERPREFERENCE_RESPONSECODE, mContext, LoginData.class, null, header, jsonObject, false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    public void responseServerWithdrawFromStudy(String flag) {
        mRegistrationServer = flag;
        AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
        try {
            Studies studies = dbServiceSubscriber.getParticipantId(mStudyId, mRealm);
            HashMap<String, String> params = new HashMap<>();
            params.put("participantId", studies.getParticipantId());
            params.put("delete", flag);
            WithdrawFromStudyEvent withdrawFromStudyEvent = new WithdrawFromStudyEvent();
            ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent("post", URLs.WITHDRAWFROMSTUDY, WITHDRAWFROMSTUDY, mContext, LoginData.class, params, null, null, false, this);
            withdrawFromStudyEvent.setResponseServerConfigEvent(responseServerConfigEvent);
            StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
            studyModulePresenter.performWithdrawFromStudy(withdrawFromStudyEvent);
        } catch (Exception e) {
            AppController.getHelperProgressDialog().dismissDialog();
        }
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode, String serverType) {
        if (responseCode != WITHDRAWFROMSTUDY)
            AppController.getHelperProgressDialog().dismissDialog();

        if (responseCode == WITHDRAWFROMSTUDY) {
            // delete data from local db
            dbServiceSubscriber.deleteActivityRunsFromDbByStudyID(((SurveyActivity) mContext).getStudyId());
            dbServiceSubscriber.deleteResponseFromDb(((SurveyActivity) mContext).getStudyId(), mRealm);
            updateuserpreference();
        }
    }

    @Override
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String statusCode, T response) {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }
}
