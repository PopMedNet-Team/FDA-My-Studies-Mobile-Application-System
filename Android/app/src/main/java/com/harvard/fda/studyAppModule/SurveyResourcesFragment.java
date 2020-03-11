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

package com.harvard.fda.studyAppModule;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.harvard.fda.AppConfig;
import com.harvard.fda.R;
import com.harvard.fda.notificationModule.NotificationModuleSubscriber;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.acvitityListModel.AnchorDateSchedulingDetails;
import com.harvard.fda.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.fda.studyAppModule.events.GetResourceListEvent;
import com.harvard.fda.studyAppModule.events.GetUserStudyInfoEvent;
import com.harvard.fda.studyAppModule.events.WithdrawFromStudyEvent;
import com.harvard.fda.studyAppModule.studyModel.NotificationDbResources;
import com.harvard.fda.studyAppModule.studyModel.Resource;
import com.harvard.fda.studyAppModule.studyModel.StudyHome;
import com.harvard.fda.studyAppModule.studyModel.StudyResource;
import com.harvard.fda.userModule.UserModulePresenter;
import com.harvard.fda.userModule.event.UpdatePreferenceEvent;
import com.harvard.fda.userModule.webserviceModel.Activities;
import com.harvard.fda.userModule.webserviceModel.LoginData;
import com.harvard.fda.userModule.webserviceModel.Studies;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.fda.webserviceModule.apiHelper.ConnectionDetector;
import com.harvard.fda.webserviceModule.apiHelper.HttpRequest;
import com.harvard.fda.webserviceModule.apiHelper.Responsemodel;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.fda.webserviceModule.events.ResponseServerConfigEvent;
import com.harvard.fda.webserviceModule.events.WCPConfigEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SurveyResourcesFragment<T> extends Fragment implements ApiCall.OnAsyncRequestComplete, ApiCallResponseServer.OnAsyncRequestComplete {

    private static final int STUDY_INFO = 10;
    private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 100;
    private int RESOURCE_REQUEST_CODE = 213;
    private static final int WITHDRAWFROMSTUDY = 105;
    private RecyclerView mStudyRecyclerView;
    private Context mContext;
    private AppCompatTextView mTitle;
    private RealmList<Resource> mResourceArrayList;
    private String mStudyId;
    RelativeLayout mBackBtn;
    private StudyHome mStudyHome;
    private StudyResource mStudyResource;
    DBServiceSubscriber dbServiceSubscriber;
    public static String RESOURCES = "resources";
    Realm mRealm;
    Studies mStudies;
    private String mRegistrationServer = "false";
    ArrayList<AnchorDateSchedulingDetails> mArrayList;

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
        mRealm = AppController.getRealmobj(mContext);
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
                if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                    Intent intent = new Intent(mContext, StudyActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    mContext.startActivity(mainIntent);
                    ((Activity) mContext).finish();
                } else {
                    ((SurveyActivity) mContext).openDrawer();
                }
            }
        });


        AppCompatImageView backBtnimg = view.findViewById(R.id.backBtnimg);
        AppCompatImageView menubtnimg = view.findViewById(R.id.menubtnimg);

        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
            backBtnimg.setVisibility(View.VISIBLE);
            menubtnimg.setVisibility(View.GONE);
        } else {
            backBtnimg.setVisibility(View.GONE);
            menubtnimg.setVisibility(View.VISIBLE);
        }
    }

    private void setTextForView() {
        mTitle.setText(getResources().getString(R.string.resources));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(getActivity(), "bold"));
        } catch (Exception e) {
            e.printStackTrace();
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

            if (response != null) {
                mStudyResource = (StudyResource) response;

                callGetStudyInfoWebservice();
            }


        } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {

            dbServiceSubscriber.updateStudyWithddrawnDB(mContext,mStudyId, StudyFragment.WITHDRAWN);
            dbServiceSubscriber.deleteActivityDataRow(mContext,mStudyId);
            dbServiceSubscriber.deleteActivityWSData(mContext,mStudyId);

            if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                Intent intent = new Intent(mContext, StudyActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                mContext.startActivity(mainIntent);
                ((Activity) mContext).finish();
            } else {
                Intent intent = new Intent(mContext, StandaloneActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                mContext.startActivity(mainIntent);
                ((Activity) mContext).finish();
            }
        } else if (responseCode == STUDY_INFO) {
            if (response != null) {
                mStudyHome = (StudyHome) response;
                mStudyHome.setmStudyId(mStudyId);
                dbServiceSubscriber.saveStudyInfoToDB(mContext,mStudyHome);


                if (mStudyResource != null) {
                    mResourceArrayList = mStudyResource.getResources();
                    if (mResourceArrayList == null) {
                        mResourceArrayList = new RealmList<>();
                    }
                    addStaticVal();

                    // primary key mStudyId
                    mStudyResource.setmStudyId(mStudyId);
                    // remove duplicate and
                    dbServiceSubscriber.deleteStudyResourceDuplicateRow(mContext,mStudyId);
                    dbServiceSubscriber.saveResourceList(mContext,mStudyResource);


                    calculatedResources(mResourceArrayList);
                }
            }
        }

    }

    private void calculatedResources(RealmList<Resource> resourceArrayList) {
        //call to resp server to get anchorDate
        mArrayList = new ArrayList<>();
        mResourceArrayList = resourceArrayList;
        AnchorDateSchedulingDetails anchorDateSchedulingDetails;
        Studies studies = dbServiceSubscriber.getStudies(((SurveyActivity) mContext).getStudyId(), mRealm);

        for (int i = 0; i < mResourceArrayList.size(); i++) {
            if (mResourceArrayList.get(i).getAvailability() != null && mResourceArrayList.get(i).getAvailability().getAvailabilityType() != null) {
                if (mResourceArrayList.get(i).getAvailability().getAvailabilityType().equalsIgnoreCase("AnchorDate")) {

                    if (mResourceArrayList.get(i).getAvailability().getSourceType().equalsIgnoreCase("ActivityResponse")) {
                        anchorDateSchedulingDetails = new AnchorDateSchedulingDetails();
                        anchorDateSchedulingDetails.setSourceActivityId(mResourceArrayList.get(i).getAvailability().getSourceActivityId());
                        anchorDateSchedulingDetails.setSourceKey(mResourceArrayList.get(i).getAvailability().getSourceKey());
                        anchorDateSchedulingDetails.setSourceFormKey(mResourceArrayList.get(i).getAvailability().getSourceFormKey());

                        anchorDateSchedulingDetails.setSchedulingType(mResourceArrayList.get(i).getAvailability().getAvailabilityType());
                        anchorDateSchedulingDetails.setSourceType(mResourceArrayList.get(i).getAvailability().getSourceType());
                        anchorDateSchedulingDetails.setStudyId(((SurveyActivity) mContext).getStudyId());
                        anchorDateSchedulingDetails.setParticipantId(studies.getParticipantId());
                        //targetActivityid is resourceId in this case just to handle with case variable
                        anchorDateSchedulingDetails.setTargetActivityId(mResourceArrayList.get(i).getResourcesId());

                        Activities activities = dbServiceSubscriber.getActivityPreferenceBySurveyId(((SurveyActivity) mContext).getStudyId(), anchorDateSchedulingDetails.getSourceActivityId(), mRealm);
                        if (activities != null) {
                            anchorDateSchedulingDetails.setActivityState(activities.getStatus());
                            mArrayList.add(anchorDateSchedulingDetails);
                        }
                    } else {
                        // For enrollmentDate
                        anchorDateSchedulingDetails = new AnchorDateSchedulingDetails();
                        anchorDateSchedulingDetails.setSchedulingType(mResourceArrayList.get(i).getAvailability().getAvailabilityType());
                        anchorDateSchedulingDetails.setSourceType(mResourceArrayList.get(i).getAvailability().getSourceType());
                        anchorDateSchedulingDetails.setStudyId(((SurveyActivity) mContext).getStudyId());
                        anchorDateSchedulingDetails.setParticipantId(studies.getParticipantId());
                        //targetActivityid is resourceId in this case just to handle with case variable
                        anchorDateSchedulingDetails.setTargetActivityId(mResourceArrayList.get(i).getResourcesId());
                        anchorDateSchedulingDetails.setAnchorDate(studies.getEnrolledDate());
                        mArrayList.add(anchorDateSchedulingDetails);
                    }
                }
            }
        }

        if (!mArrayList.isEmpty()) {
            callLabkeyService(0);
        } else {
            metadataProcess();
        }
    }

    private void mSetResourceAdapter() {
        RealmList<Resource> resources = new RealmList<>();
        if (mResourceArrayList != null) {
            for (int i = 0; i < mResourceArrayList.size(); i++) {
                if (mResourceArrayList.get(i).getAudience() != null && mResourceArrayList.get(i).getAudience().equalsIgnoreCase("All")) {
                    if (mResourceArrayList.get(i).getAvailability() != null && mResourceArrayList.get(i).getAvailability().getAvailableDate() != null && !mResourceArrayList.get(i).getAvailability().getAvailableDate().equalsIgnoreCase("")) {
                        try {

                            Calendar currentday = Calendar.getInstance();


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
                            e.printStackTrace();
                        }
                    } else {
                        resources.add(mResourceArrayList.get(i));
                    }

                } else if (mResourceArrayList.get(i).getAudience() != null && mResourceArrayList.get(i).getAudience().equalsIgnoreCase("Limited")) {
                    if (mResourceArrayList.get(i).getAvailability().getAvailabilityType().equalsIgnoreCase("AnchorDate")) {
                        if (mResourceArrayList.get(i).getAvailability().getSourceType().equalsIgnoreCase("ActivityResponse")) {
                            if (mResourceArrayList.get(i).getAvailability().getAvailableDate().equalsIgnoreCase("")) {
                                StepRecordCustom stepRecordCustom = dbServiceSubscriber.getSurveyResponseFromDB(((SurveyActivity) mContext).getStudyId() + "_STUDYID_" + mStudyHome.getAnchorDate().getQuestionInfo().getActivityId(), mStudyHome.getAnchorDate().getQuestionInfo().getKey(), mRealm);
                                if (stepRecordCustom != null) {
                                    Calendar startCalender = Calendar.getInstance();

                                    Calendar endCalender = Calendar.getInstance();


                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(stepRecordCustom.getResult());
                                        startCalender.setTime(AppController.getDateFormat().parse("" + jsonObject.get("answer")));
                                        startCalender.add(Calendar.DATE, mResourceArrayList.get(i).getAvailability().getStartDays());
                                        if (mResourceArrayList.get(i).getAvailability().getStartTime() == null || mResourceArrayList.get(i).getAvailability().getStartTime().equalsIgnoreCase("")) {
                                            startCalender.set(Calendar.HOUR_OF_DAY, 0);
                                            startCalender.set(Calendar.MINUTE, 0);
                                            startCalender.set(Calendar.SECOND, 0);
                                        } else {
                                            String[] time = mResourceArrayList.get(i).getAvailability().getStartTime().split(":");
                                            startCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                                            startCalender.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                                            startCalender.set(Calendar.SECOND, Integer.parseInt(time[2]));
                                        }
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

                                        endCalender.setTime(AppController.getDateFormat().parse("" + jsonObject.get("answer")));
                                        endCalender.add(Calendar.DATE, mResourceArrayList.get(i).getAvailability().getEndDays());

                                        if (mResourceArrayList.get(i).getAvailability().getEndTime() == null || mResourceArrayList.get(i).getAvailability().getEndTime().equalsIgnoreCase("")) {
                                            endCalender.set(Calendar.HOUR_OF_DAY, 23);
                                            endCalender.set(Calendar.MINUTE, 59);
                                            endCalender.set(Calendar.SECOND, 59);
                                        } else {
                                            String[] time = mResourceArrayList.get(i).getAvailability().getEndTime().split(":");
                                            endCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                                            endCalender.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                                            endCalender.set(Calendar.SECOND, Integer.parseInt(time[2]));
                                        }

                                        Calendar currentday = Calendar.getInstance();

                                        if ((currentday.getTime().after(startCalender.getTime()) || currentday.getTime().equals(startCalender.getTime())) && (currentday.getTime().before(endCalender.getTime()) || currentday.getTime().equals(endCalender.getTime()))) {
                                            resources.add(mResourceArrayList.get(i));
                                        }
                                    } catch (JSONException | ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        } else {
                            // if anchordate is enrollment date
                            Calendar startCalender = Calendar.getInstance();
//                        startCalender.setTime(stepRecordCustom.getCompleted());
                            Calendar endCalender = Calendar.getInstance();
//                        endCalender.setTime(stepRecordCustom.getCompleted());
                            try {
                                for (int j = 0; j < mArrayList.size(); j++) {
                                    if (mResourceArrayList.get(i).getResourcesId().equalsIgnoreCase(mArrayList.get(j).getTargetActivityId())) {
                                        startCalender.setTime(AppController.getDateFormat().parse(mArrayList.get(j).getAnchorDate()));
                                        startCalender.add(Calendar.DATE, mResourceArrayList.get(i).getAvailability().getStartDays());

                                        endCalender.setTime(AppController.getDateFormat().parse(mArrayList.get(j).getAnchorDate()));
                                        endCalender.add(Calendar.DATE, mResourceArrayList.get(i).getAvailability().getEndDays());
                                        break;
                                    }
                                }
                                if (mResourceArrayList.get(i).getAvailability().getStartTime() == null || mResourceArrayList.get(i).getAvailability().getStartTime().equalsIgnoreCase("")) {
                                    startCalender.set(Calendar.HOUR_OF_DAY, 0);
                                    startCalender.set(Calendar.MINUTE, 0);
                                    startCalender.set(Calendar.SECOND, 0);
                                } else {
                                    String[] time = mResourceArrayList.get(i).getAvailability().getStartTime().split(":");
                                    startCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                                    startCalender.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                                    startCalender.set(Calendar.SECOND, Integer.parseInt(time[2]));
                                }

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

                                if (mResourceArrayList.get(i).getAvailability().getEndTime() == null || mResourceArrayList.get(i).getAvailability().getEndTime().equalsIgnoreCase("")) {
                                    endCalender.set(Calendar.HOUR_OF_DAY, 23);
                                    endCalender.set(Calendar.MINUTE, 59);
                                    endCalender.set(Calendar.SECOND, 59);
                                } else {
                                    String[] time = mResourceArrayList.get(i).getAvailability().getEndTime().split(":");
                                    endCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                                    endCalender.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                                    endCalender.set(Calendar.SECOND, Integer.parseInt(time[2]));
                                }

                                Calendar currentday = Calendar.getInstance();


                                if ((currentday.getTime().after(startCalender.getTime()) || currentday.getTime().equals(startCalender.getTime())) && (currentday.getTime().before(endCalender.getTime()) || currentday.getTime().equals(endCalender.getTime()))) {
                                    resources.add(mResourceArrayList.get(i));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        resources.add(mResourceArrayList.get(i));
                    }
                } else if (mResourceArrayList.get(i).getAudience() == null) {
                    resources.add(mResourceArrayList.get(i));
                }
            }
        } else {
            addStaticVal();
        }
        mStudyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mStudyRecyclerView.setNestedScrollingEnabled(false);
        ResourcesListAdapter resourcesListAdapter = new ResourcesListAdapter(getActivity(), resources, this);
        mStudyRecyclerView.setAdapter(resourcesListAdapter);
    }



    private class ResponseData extends AsyncTask<String, Void, String> {

        String response = null;
        String responseCode = null;
        int position;
        Responsemodel mResponseModel;
        AnchorDateSchedulingDetails anchorDateSchedulingDetails;

        ResponseData(int position, AnchorDateSchedulingDetails anchorDateSchedulingDetails) {
            this.position = position;
            this.anchorDateSchedulingDetails = anchorDateSchedulingDetails;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionDetector connectionDetector = new ConnectionDetector(mContext);

            if (connectionDetector.isConnectingToInternet()) {
                mResponseModel = HttpRequest.getRequest(URLs.PROCESSRESPONSEDATA + "sql=SELECT%20%22" + anchorDateSchedulingDetails.getSourceKey() + "%22%20FROM%20%22" + anchorDateSchedulingDetails.getSourceActivityId() + anchorDateSchedulingDetails.getSourceFormKey() + "%22&participantId=" + anchorDateSchedulingDetails.getParticipantId(), new HashMap<String, String>(), "Response");
                Log.e("PROCESSRESPONSEDATA", "" + URLs.PROCESSRESPONSEDATA + "sql=SELECT%20%22" + anchorDateSchedulingDetails.getSourceKey() + "%22%20FROM%20%22" + anchorDateSchedulingDetails.getSourceActivityId() + anchorDateSchedulingDetails.getSourceFormKey() + "%22&participantId=" + anchorDateSchedulingDetails.getParticipantId());
                responseCode = mResponseModel.getResponseCode();
                response = mResponseModel.getResponseData();
                if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
                    response = "timeout";
                } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
                    response = "error";
                } else if (Integer.parseInt(responseCode) >= 201 && Integer.parseInt(responseCode) < 300 && response.equalsIgnoreCase("")) {
                    response = "No data";
                } else if (Integer.parseInt(responseCode) >= 400 && Integer.parseInt(responseCode) < 500 && response.equalsIgnoreCase("http_not_ok")) {
                    response = "client error";
                } else if (Integer.parseInt(responseCode) >= 500 && Integer.parseInt(responseCode) < 600 && response.equalsIgnoreCase("http_not_ok")) {
                    response = "server error";
                } else if (response.equalsIgnoreCase("http_not_ok")) {
                    response = "Unknown error";
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = "session expired";
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK && !response.equalsIgnoreCase("")) {
                    response = response;
                } else {
                    response = getString(R.string.unknown_error);
                }
            }
            return response;
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                if (response.equalsIgnoreCase("session expired")) {
                    AppController.getHelperProgressDialog().dismissDialog();
                    AppController.getHelperSessionExpired(mContext, "session expired");
                } else if (response.equalsIgnoreCase("timeout")) {
                    metadataProcess();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.connection_timeout), Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(responseCode) == 500) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(mResponseModel.getResponseData()));
                        String exception = String.valueOf(jsonObject.get("exception"));
                        if (exception.contains("Query or table not found")) {
                            //call remaining service
                            callLabkeyService(this.position);
                        } else {
                            metadataProcess();
                        }
                    } catch (JSONException e) {
                        metadataProcess();
                        e.printStackTrace();
                    }
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = (JSONArray) jsonObject.get("rows");
                        Gson gson = new Gson();

                        JSONObject jsonObject1 = (JSONObject) new JSONObject(String.valueOf(jsonArray.get(0))).get("data");
                        Type type = new TypeToken<Map<String, Object>>() {
                        }.getType();
                        Map<String, Object> myMap = gson.fromJson(String.valueOf(jsonObject1), type);
                        Object value = null;
                        for (Map.Entry<String, Object> entry : myMap.entrySet()) {
                            String key = entry.getKey();
                            String valueobj = gson.toJson(entry.getValue());
                            Map<String, Object> vauleMap = gson.fromJson(String.valueOf(valueobj), type);
                            value = vauleMap.get("value");
                            try {
                                Date anchordate = AppController.getLabkeyDateFormat().parse("" + value);
                                value = AppController.getDateFormat().format(anchordate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        //updating results back to DB
                        StepRecordCustom stepRecordCustom = new StepRecordCustom();
                        JSONObject jsonObject2 = new JSONObject();
                        jsonObject2.put("answer", "" + value);
                        stepRecordCustom.setResult(jsonObject2.toString());
                        stepRecordCustom.setActivityID(anchorDateSchedulingDetails.getStudyId() + "_STUDYID_" + anchorDateSchedulingDetails.getSourceActivityId());
                        stepRecordCustom.setStepId(anchorDateSchedulingDetails.getSourceKey());
                        stepRecordCustom.setTaskStepID(anchorDateSchedulingDetails.getStudyId() + "_STUDYID_" + anchorDateSchedulingDetails.getSourceActivityId() + "_" + 1 + "_" + anchorDateSchedulingDetails.getSourceKey());
                        dbServiceSubscriber.updateStepRecord(mContext,stepRecordCustom);

                        mArrayList.get(this.position).setAnchorDate("" + value);


                        callLabkeyService(this.position);
                    } catch (Exception e) {
                        e.printStackTrace();
                        metadataProcess();
                    }
                } else {
                    metadataProcess();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.unable_to_retrieve_data), Toast.LENGTH_SHORT).show();
                }
            } else {
                metadataProcess();
                Toast.makeText(mContext, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void metadataProcess() {
        AppController.getHelperProgressDialog().dismissDialog();
        mSetResourceAdapter();
    }


    private void setRemainder(Calendar startCalender, String activityId, String studyId, String
            notificationTest, String resourceId) {
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
//                    if (db.getStudyResource(mStudyId).getResources() == null) {
                    if (dbServiceSubscriber.getStudyResource(mStudyId, mRealm) == null) {
                        Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
                    } else if (dbServiceSubscriber.getStudyResource(mStudyId, mRealm).getResources() == null) {
                        Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
                    } else {
                        mResourceArrayList = dbServiceSubscriber.getStudyResource(mStudyId, mRealm).getResources();
                        if (mResourceArrayList == null || mResourceArrayList.size() == 0) {
                            Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
                        } else {
                            calculatedResources(mResourceArrayList);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
            e.printStackTrace();
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
            ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent("post_json", URLs.WITHDRAWFROMSTUDY, WITHDRAWFROMSTUDY, mContext, LoginData.class, params, null, null, false, this);
            withdrawFromStudyEvent.setResponseServerConfigEvent(responseServerConfigEvent);
            StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
            studyModulePresenter.performWithdrawFromStudy(withdrawFromStudyEvent);
        } catch (Exception e) {
            AppController.getHelperProgressDialog().dismissDialog();
            e.printStackTrace();
        }
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode, String serverType) {
        // WITHDRAWFROMSTUDY: once 'leave study'  call responseServerWithdrawFromStudy after that we need to call updateuserpreference();
        if (responseCode != WITHDRAWFROMSTUDY)
            AppController.getHelperProgressDialog().dismissDialog();

        if (responseCode == WITHDRAWFROMSTUDY) {
            // delete data from local db
            dbServiceSubscriber.deleteActivityRunsFromDbByStudyID(mContext,((SurveyActivity) mContext).getStudyId());
            dbServiceSubscriber.deleteResponseFromDb(((SurveyActivity) mContext).getStudyId(), mRealm);
            updateuserpreference();
        }
    }

    @Override
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String
            statusCode, T response) {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }


    private void callLabkeyService(int position) {
        if (mArrayList.size() > position) {
            AnchorDateSchedulingDetails anchorDateSchedulingDetails = mArrayList.get(position);
            if (anchorDateSchedulingDetails.getSourceType().equalsIgnoreCase("ActivityResponse") && anchorDateSchedulingDetails.getActivityState().equalsIgnoreCase("completed")) {
                Realm realm = AppController.getRealmobj(mContext);
                StepRecordCustom stepRecordCustom = dbServiceSubscriber.getSurveyResponseFromDB(anchorDateSchedulingDetails.getStudyId() + "_STUDYID_" + anchorDateSchedulingDetails.getSourceActivityId(), anchorDateSchedulingDetails.getSourceKey(), realm);
                if (stepRecordCustom != null) {
                    String value = "";
                    try {
                        JSONObject jsonObject = new JSONObject(stepRecordCustom.getResult());
                        value = jsonObject.getString("answer");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mArrayList.get(position).setAnchorDate("" + value);

                    callLabkeyService(position + 1);
                } else {
                    new ResponseData(position, anchorDateSchedulingDetails).execute();
                }
                dbServiceSubscriber.closeRealmObj(realm);
            } else {
                callLabkeyService(position + 1);
            }
        } else {
            metadataProcess();
        }
    }
}
