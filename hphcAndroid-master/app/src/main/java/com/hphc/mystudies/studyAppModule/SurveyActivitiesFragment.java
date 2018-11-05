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

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hphc.mystudies.R;
import com.hphc.mystudies.notificationModule.NotificationModuleSubscriber;
import com.hphc.mystudies.offlineModule.model.OfflineData;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.storageModule.events.DatabaseEvent;
import com.hphc.mystudies.studyAppModule.activityBuilder.ActivityBuilder;
import com.hphc.mystudies.studyAppModule.activityBuilder.CustomSurveyViewTaskActivity;
import com.hphc.mystudies.studyAppModule.activityBuilder.StepsBuilder;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.ActivityRun;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.serviceModel.ActivityInfoData;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.hphc.mystudies.studyAppModule.acvitityListModel.ActivitiesWS;
import com.hphc.mystudies.studyAppModule.acvitityListModel.ActivityListData;
import com.hphc.mystudies.studyAppModule.acvitityListModel.Frequency;
import com.hphc.mystudies.studyAppModule.acvitityListModel.FrequencyRuns;
import com.hphc.mystudies.studyAppModule.consent.ConsentBuilder;
import com.hphc.mystudies.studyAppModule.consent.CustomConsentViewTaskActivity;
import com.hphc.mystudies.studyAppModule.consent.model.Consent;
import com.hphc.mystudies.studyAppModule.consent.model.CorrectAnswerString;
import com.hphc.mystudies.studyAppModule.consent.model.EligibilityConsent;
import com.hphc.mystudies.studyAppModule.events.GetActivityInfoEvent;
import com.hphc.mystudies.studyAppModule.events.GetActivityListEvent;
import com.hphc.mystudies.studyAppModule.events.GetResourceListEvent;
import com.hphc.mystudies.studyAppModule.events.GetUserStudyInfoEvent;
import com.hphc.mystudies.studyAppModule.events.GetUserStudyListEvent;
import com.hphc.mystudies.studyAppModule.studyModel.MotivationalNotification;
import com.hphc.mystudies.studyAppModule.studyModel.StudyHome;
import com.hphc.mystudies.studyAppModule.studyModel.StudyList;
import com.hphc.mystudies.studyAppModule.studyModel.StudyResource;
import com.hphc.mystudies.studyAppModule.studyModel.StudyUpdate;
import com.hphc.mystudies.studyAppModule.studyModel.StudyUpdateListdata;
import com.hphc.mystudies.studyAppModule.surveyScheduler.SurveyScheduler;
import com.hphc.mystudies.studyAppModule.surveyScheduler.model.ActivityStatus;
import com.hphc.mystudies.userModule.UserModulePresenter;
import com.hphc.mystudies.userModule.event.GetPreferenceEvent;
import com.hphc.mystudies.userModule.event.UpdatePreferenceEvent;
import com.hphc.mystudies.userModule.webserviceModel.Activities;
import com.hphc.mystudies.userModule.webserviceModel.ActivityData;
import com.hphc.mystudies.userModule.webserviceModel.LoginData;
import com.hphc.mystudies.userModule.webserviceModel.StudyData;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.SetDialogHelper;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.apiHelper.ConnectionDetector;
import com.hphc.mystudies.webserviceModule.apiHelper.HttpRequest;
import com.hphc.mystudies.webserviceModule.apiHelper.Responsemodel;
import com.hphc.mystudies.webserviceModule.events.RegistrationServerConfigEvent;
import com.hphc.mystudies.webserviceModule.events.WCPConfigEvent;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

import static android.os.Build.VERSION_CODES.M;

public class SurveyActivitiesFragment extends Fragment implements ApiCall.OnAsyncRequestComplete, ActivityCompat.OnRequestPermissionsResultCallback, CustomActivitiesDailyDialogClass.DialogClick {
    private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 102;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int GET_PREFERENCES = 112;
    private static final int STUDY_UPDATES = 113;
    private static final int CONSENT_METADATA = 114;
    private static final int CONSENT_RESPONSECODE = 115;
    private static final int CONSENT_COMPLETE = 116;
    private static final int UPDATE_STUDY_PREFERENCE = 119;
    private static final int STUDY_INFO = 10;
    private static final int RESOURCE_REQUEST_CODE = 213;
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private RelativeLayout mFilterBtn;
    private RecyclerView mSurveyActivitiesRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Context mContext;
    private int mCurrentRunId; // runid for webservice on click of activity
    private String mActivityStatus; // mActivityStatus for webservice on click of activity
    private String mActivityId; // mActivityId for webservice on click of activity
    private boolean mBranching; // mBranching for webservice on click of activity
    private String mActivityVersion; // mActivityVersion for webservice on click of activity

    public static final int ACTIVTTYLIST_RESPONSECODE = 100;
    public static final int ACTIVTTYINFO_RESPONSECODE = 101;
    public static final String YET_TO_START = "yetToJoin";
    public static final String IN_PROGRESS = "inProgress";
    public static final String COMPLETED = "completed";
    public static final String INCOMPLETE = "abandoned";

    public static final String STATUS_CURRENT = "Current";
    public static final String STATUS_UPCOMING = "Upcoming";
    public static final String STATUS_COMPLETED = "Completed";

    private ActivityListData activityListData;

    String signatureBase64 = "";
    String signatureDate = "";
    String firstName = "";
    String lastName = "";
    String eligibilityType = "";
    private static final String FILE_FOLDER = "FDA_PDF";
    private String mFileName;

    OrderedTask mTask;
    ActivityObj mActivityObj;
    private ActivityStatus mActivityStatusData;
    boolean locationPermission = false;
    private int mDeleteIndexNumberDB;
    private EligibilityConsent eligibilityConsent;
    private String mTitl;
    public static final String FROM_SURVAY = "survey";
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;
    private boolean mActivityUpdated = false;
    public static final String DELETE = "deleted";
    private static final String ACTIVE = "active";
    private static final String QSTUDY = "?studyId=";
    private static final String TIMEOUT = "timeout";
    private static final String HTTP_NOT_OK = "http_not_ok";
    private static final String STUDY_ID = "studyId";
    private static final String POST_OBJECT = "post_object";
    private static final String STUDY_ID_TO_SPLIT = "_STUDYID_";
    private static final String USER_ID = "userId";
    private static final String SESSION_EXPIRED = "session expired";
    private static final String FILE_PATH = "/data/data/com.harvard.fda/files/";
    private SurveyActivitiesListAdapter studyVideoAdapter;
    private int filterPos = 0;
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<ActivitiesWS> activitiesArrayList1 = new ArrayList<>();
    private ArrayList<ActivityStatus> currentRunStatusForActivities = new ArrayList<>();
    private StudyResource mStudyResource;
    StepsBuilder stepsBuilder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_activities, container, false);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj();
        try {
            AppController.getHelperHideKeyboard((Activity) mContext);
        } catch (Exception e) {
        }
        initializeXMLId(view);
        setTextForView();
        setFont();
        bindEvents();
        getStudyUpdateFomWS(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppController.getHelperHideKeyboard(getActivity());
        } catch (Exception e) {
        }
    }

    private void initializeXMLId(View view) {
        mBackBtn = (RelativeLayout) view.findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) view.findViewById(R.id.title);
        mFilterBtn = (RelativeLayout) view.findViewById(R.id.filterBtn);
        mSurveyActivitiesRecyclerView = (RecyclerView) view.findViewById(R.id.mSurveyActivitiesRecyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    }

    private void setTextForView() {
        mTitle.setText(mContext.getResources().getString(R.string.study_activities));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(getActivity(), "bold"));
        } catch (Exception e) {
        }
    }

    private void bindEvents() {
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
        mFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<String> mScheduledTime = new ArrayList<>();
                mScheduledTime.add(mContext.getResources().getString(R.string.all));
                mScheduledTime.add(mContext.getResources().getString(R.string.surveys1));
                mScheduledTime.add(mContext.getResources().getString(R.string.tasks1));
                CustomActivitiesDailyDialogClass c = new CustomActivitiesDailyDialogClass(mContext, mScheduledTime, filterPos, true, SurveyActivitiesFragment.this);
                c.show();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStudyUpdateFomWS(true);
            }
        });
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getStudyUpdateFomWS(boolean isSwipeToRefresh) {
        if (isSwipeToRefresh)
            AppController.getHelperProgressDialog().showSwipeListCustomProgress(getActivity(), R.drawable.transparent, false);
        else
            AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);

        GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
        HashMap<String, String> header = new HashMap();
        StudyList studyList = dbServiceSubscriber.getStudiesDetails(((SurveyActivity) mContext).getStudyId(), mRealm);
        String url = URLs.STUDY_UPDATES + QSTUDY + ((SurveyActivity) mContext).getStudyId() + "&studyVersion=" + studyList.getStudyVersion();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, STUDY_UPDATES, mContext, StudyUpdate.class, null, header, null, false, this);

        getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
    }

    private void setRecyclerView() {
        mSurveyActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSurveyActivitiesRecyclerView.setNestedScrollingEnabled(false);
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        GetActivityListEvent getActivityListEvent = new GetActivityListEvent();
        HashMap<String, String> header = new HashMap();
        String url = URLs.ACTIVITY_LIST + QSTUDY + ((SurveyActivity) mContext).getStudyId();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, ACTIVTTYLIST_RESPONSECODE, mContext, ActivityListData.class, null, header, null, false, this);

        getActivityListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetActivityList(getActivityListEvent);
    }

    private void callConsentMetaDataWebservice() {
        new callConsentMetaData().execute();
    }

    private class callConsentMetaData extends AsyncTask<String, Void, String> {
        String response = null;
        String responseCode = null;
        Responsemodel mResponseModel;

        @Override
        protected String doInBackground(String... params) {
            ConnectionDetector connectionDetector = new ConnectionDetector(mContext);


            String url = URLs.BASE_URL_WCP_SERVER + URLs.CONSENT_METADATA + QSTUDY + ((SurveyActivity) mContext).getStudyId();
            if (connectionDetector.isConnectingToInternet()) {
                mResponseModel = HttpRequest.getRequest(url, new HashMap<String, String>(), "WCP");
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
        protected void onPostExecute(String result) {
            AppController.getHelperProgressDialog().dismissDialog();
            onItemsLoadComplete();
            if (response != null) {
                if (response.equalsIgnoreCase(SESSION_EXPIRED)) {
                    AppController.getHelperProgressDialog().dismissDialog();
                    AppController.getHelperSessionExpired(mContext, SESSION_EXPIRED);
                } else if (response.equalsIgnoreCase(TIMEOUT)) {
                    AppController.getHelperProgressDialog().dismissDialog();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.connection_timeout), Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK) {

                    Gson gson = new GsonBuilder()
                            .setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return f.getDeclaringClass().equals(RealmObject.class);
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            })
                            .registerTypeAdapter(new TypeToken<RealmList<CorrectAnswerString>>() {
                            }.getType(), new TypeAdapter<RealmList<CorrectAnswerString>>() {

                                @Override
                                public void write(JsonWriter out, RealmList<CorrectAnswerString> value) throws IOException {
                                    // Ignore
                                }

                                @Override
                                public RealmList<CorrectAnswerString> read(JsonReader in) throws IOException {
                                    RealmList<CorrectAnswerString> list = new RealmList<CorrectAnswerString>();
                                    in.beginArray();
                                    while (in.hasNext()) {
                                        CorrectAnswerString surveyObjectString = new CorrectAnswerString();
                                        surveyObjectString.setAnswer(in.nextString());
                                        list.add(surveyObjectString);
                                    }
                                    in.endArray();
                                    return list;
                                }
                            })
                            .create();
                    eligibilityConsent = gson.fromJson(response, EligibilityConsent.class);
                    if (eligibilityConsent != null) {
                        eligibilityConsent.setStudyId(((SurveyActivity) mContext).getStudyId());
                        saveConsentToDB(eligibilityConsent);
                        startConsent(eligibilityConsent.getConsent(), eligibilityConsent.getEligibility().getType());
                    } else {
                        Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AppController.getHelperProgressDialog().dismissDialog();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.unable_to_retrieve_data), Toast.LENGTH_SHORT).show();
                }
            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(mContext, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        }
    }

    private void saveConsentToDB(EligibilityConsent eligibilityConsent) {
        DatabaseEvent databaseEvent = new DatabaseEvent();
        databaseEvent.setE(eligibilityConsent);
        databaseEvent.setmType(DBServiceSubscriber.TYPE_COPY_UPDATE);
        databaseEvent.setaClass(EligibilityConsent.class);
        databaseEvent.setmOperation(DBServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
        dbServiceSubscriber.insert(databaseEvent);
    }

    private void startConsent(Consent consent, String type) {
        eligibilityType = type;
        Toast.makeText(mContext, mContext.getResources().getString(R.string.please_review_the_updated_consent), Toast.LENGTH_SHORT).show();
        StudyList studyList = dbServiceSubscriber.getStudiesDetails(((SurveyActivity) mContext).getStudyId(), mRealm);
        mTitl = studyList.getTitle();
        ConsentBuilder consentBuilder = new ConsentBuilder();
        List<Step> consentStep = consentBuilder.createsurveyquestion(mContext, consent, studyList.getTitle());
        Task consentTask = new OrderedTask(StudyFragment.CONSENT, consentStep);
        Intent intent = CustomConsentViewTaskActivity.newIntent(mContext, consentTask, ((SurveyActivity) mContext).getStudyId(), "", mTitl, eligibilityType, "update");
        startActivityForResult(intent, CONSENT_RESPONSECODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            setRecyclerView();
        } else if (requestCode == CONSENT_RESPONSECODE) {
            if (resultCode == getActivity().RESULT_OK) {
                try {
                    TaskResult result = (TaskResult) data.getSerializableExtra(CustomConsentViewTaskActivity.EXTRA_TASK_RESULT);
                    signatureBase64 = (String) result.getStepResult("Signature")
                            .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

                    signatureDate = (String) result.getStepResult("Signature")
                            .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

                    String formResult = new Gson().toJson(result.getStepResult(mContext.getResources().getString(R.string.signature_form_step)).getResults());
                    JSONObject formResultObj = new JSONObject(formResult);
                    JSONObject fullNameObj = formResultObj.getJSONObject("First Name");
                    JSONObject fullNameResult = fullNameObj.getJSONObject("results");
                    firstName = fullNameResult.getString("answer");

                    JSONObject lastNameObj = formResultObj.getJSONObject("Last Name");
                    JSONObject lastNameResult = lastNameObj.getJSONObject("results");
                    lastName = lastNameResult.getString("answer");

                } catch (Exception e) {
                }


                genarateConsentPDF(signatureBase64);
                // encrypt the genarated pdf
                File encryptFile = AppController.genarateEncryptedConsentPDF(FILE_PATH, mFileName);
                //After encryption delete the pdf file
                if (encryptFile != null) {
                    File file = new File(FILE_PATH + mFileName + ".pdf");
                    file.delete();
                }
                Intent intent = new Intent(getActivity(), ConsentCompletedActivity.class);
                intent.putExtra(ConsentCompletedActivity.FROM, "survey");
                intent.putExtra(STUDY_ID, ((SurveyActivity) mContext).getStudyId());
                intent.putExtra("title", mTitl);
                intent.putExtra("eligibility", eligibilityType);
                intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
                // get the encrypted file path
                if (encryptFile != null) {
                    intent.putExtra("PdfPath", encryptFile.getAbsolutePath());
                }
                else
                {
                    intent.putExtra("PdfPath", "");
                }
                startActivityForResult(intent, CONSENT_COMPLETE);

            } else {
                Toast.makeText(mContext, R.string.consent_complete, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, StudyActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                mContext.startActivity(mainIntent);
                ((Activity) mContext).finish();
            }
        } else if (requestCode == CONSENT_COMPLETE) {
            if (resultCode == getActivity().RESULT_OK) {
                setRecyclerView();
            } else {
                Toast.makeText(mContext, R.string.consent_complete, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, StudyActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                mContext.startActivity(mainIntent);
                ((Activity) mContext).finish();
            }
        }
    }

    private void genarateConsentPDF(String signatureBase64) {
        OutputStream output = null;
        try {
            getFile(FILE_PATH);
            String timeStamp = AppController.getDateFormatType3();
            mFileName = timeStamp;
            String filePath = FILE_PATH + timeStamp + ".pdf";
            File myFile = new File(filePath);
            if (!myFile.exists())
                myFile.createNewFile();
            output = new FileOutputStream(myFile);

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setFullCompression();

            document.addCreationDate();
            document.setPageSize(PageSize.A4);
            document.setMargins(10, 10, 10, 10);

            document.open();
            Paragraph consentItem;
            if (eligibilityConsent != null && eligibilityConsent.getConsent() != null && eligibilityConsent.getConsent().getReview() != null && eligibilityConsent.getConsent().getReview().getSignatureContent() != null && !eligibilityConsent.getConsent().getReview().getSignatureContent().equalsIgnoreCase("")) {
                consentItem = new Paragraph(Html.fromHtml(eligibilityConsent.getConsent().getReview().getSignatureContent().toString()).toString());
            } else if (eligibilityConsent != null && eligibilityConsent.getConsent() != null && eligibilityConsent.getConsent().getVisualScreens() != null) {
                StringBuilder docBuilder;
                if (eligibilityConsent.getConsent().getVisualScreens().size() > 0) {
                    // Create our HTML to show the user and have them accept or decline.
                    docBuilder = new StringBuilder(
                            "</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
                    String title = mTitl;
                    docBuilder.append(String.format(
                            "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>",
                            title));


                    docBuilder.append("</div></br>");
                    for (int i = 0; i < eligibilityConsent.getConsent().getVisualScreens().size(); i++) {
                        docBuilder.append("<div>  <h4>" + eligibilityConsent.getConsent().getVisualScreens().get(i).getTitle() + "<h4> </div>");
                        docBuilder.append("</br>");
                        docBuilder.append("<div>" + eligibilityConsent.getConsent().getVisualScreens().get(i).getHtml() + "</div>");
                        docBuilder.append("</br>");
                        docBuilder.append("</br>");
                    }
                    consentItem = new Paragraph(Html.fromHtml(docBuilder.toString()).toString());
                } else {
                    consentItem = new Paragraph("");
                }
            } else {
                consentItem = new Paragraph("");
            }
            StringBuilder docBuilder = new StringBuilder(
                    "</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
            String participant = mContext.getResources().getString(R.string.participant);
            docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", participant));
            String detail = mContext.getResources().getString(R.string.agree_participate_research_study);
            docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
            consentItem.add(Html.fromHtml(docBuilder.toString()).toString());

            byte[] signatureBytes = Base64.decode(signatureBase64, Base64.DEFAULT);
            Image myImg = Image.getInstance(signatureBytes);
            myImg.setScaleToFitHeight(true);
            myImg.scalePercent(50f);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell(getCell(firstName + " " + lastName, PdfPCell.ALIGN_CENTER));
            table.addCell(getImage(myImg, PdfPCell.ALIGN_CENTER));
            table.addCell(getCell(signatureDate, PdfPCell.ALIGN_CENTER));
            consentItem.add(table);


            PdfPTable table1 = new PdfPTable(3);
            table1.setWidthPercentage(100);
            table1.addCell(getCell(mContext.getResources().getString(R.string.participans_name), PdfPCell.ALIGN_CENTER));
            table1.addCell(getCell(mContext.getResources().getString(R.string.participants_signature), PdfPCell.ALIGN_CENTER));
            table1.addCell(getCell(mContext.getResources().getString(R.string.date), PdfPCell.ALIGN_CENTER));
            consentItem.add(table1);

            document.add(consentItem);
            document.close();
        } catch (IOException | DocumentException e) {
            Toast.makeText(mContext, R.string.not_able_create_pdf, Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PdfPCell getImage(Image image, int alignment) {
        PdfPCell cell = new PdfPCell(image);
        cell.setPadding(10);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(10);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }


    private void getFile(String s) {
        File file = new File(s, FILE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    @Override
    public <T> void asyncResponse(T response, int responseCode) {

        if (responseCode == STUDY_UPDATES) {
            StudyUpdate studyUpdate = (StudyUpdate) response;
            studyUpdate.setStudyId(((SurveyActivity) mContext).getStudyId());
            StudyUpdateListdata studyUpdateListdata = new StudyUpdateListdata();

            RealmList<StudyUpdate> studyUpdates = new RealmList<>();
            studyUpdates.add(studyUpdate);
            studyUpdateListdata.setStudyUpdates(studyUpdates);
            dbServiceSubscriber.saveStudyUpdateListdataToDB(studyUpdateListdata);

            if (studyUpdate.getStudyUpdateData().getStatus().equalsIgnoreCase(getString(R.string.paused))) {
                AppController.getHelperProgressDialog().dismissDialog();
                onItemsLoadComplete();
                Toast.makeText(mContext, R.string.studyPaused, Toast.LENGTH_SHORT).show();
                ((Activity) mContext).finish();
            } else if (studyUpdate.getStudyUpdateData().getStatus().equalsIgnoreCase(getString(R.string.closed))) {
                AppController.getHelperProgressDialog().dismissDialog();
                onItemsLoadComplete();
                Toast.makeText(mContext, R.string.studyClosed, Toast.LENGTH_SHORT).show();
                ((Activity) mContext).finish();
            } else {

                if (studyUpdate.getStudyUpdateData().isResources()) {
                    dbServiceSubscriber.deleteResourcesFromDb(((SurveyActivity) mContext).getStudyId());
                }
                if (studyUpdate.getStudyUpdateData().isInfo()) {
                    dbServiceSubscriber.deleteStudyInfoFromDb(((SurveyActivity) mContext).getStudyId());
                }
                if (studyUpdate.getStudyUpdateData().isConsent()) {
                    callConsentMetaDataWebservice();
                } else {
                    StudyList studyList = dbServiceSubscriber.getStudyTitle(((SurveyActivity) mContext).getStudyId(), mRealm);
                    dbServiceSubscriber.updateStudyPreferenceVersionDB(((SurveyActivity) mContext).getStudyId(), studyList.getStudyVersion());
                    setRecyclerView();
                }
            }

        } else if (responseCode == CONSENT_METADATA) {
            eligibilityConsent = (EligibilityConsent) response;
            if (eligibilityConsent != null) {
                eligibilityConsent.setStudyId(((SurveyActivity) mContext).getStudyId());
                saveConsentToDB(eligibilityConsent);
                startConsent(eligibilityConsent.getConsent(), eligibilityConsent.getEligibility().getType());
            } else {
                Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == ACTIVTTYLIST_RESPONSECODE) {
            activityListData = (ActivityListData) response;
            activityListData.setStudyId(((SurveyActivity) mContext).getStudyId());

            GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
            HashMap<String, String> header = new HashMap();
            header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.auth), ""));
            header.put(USER_ID, AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));
            String url = URLs.ACTIVITY_STATE + QSTUDY + ((SurveyActivity) mContext).getStudyId();
            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", url, GET_PREFERENCES, mContext, ActivityData.class, null, header, null, false, this);

            getPreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performGetUserPreference(getPreferenceEvent);


        } else if (responseCode == GET_PREFERENCES) {
            ActivityData activityData1 = (ActivityData) response;
            activityData1.setStudyId(((SurveyActivity) mContext).getStudyId());
            ActivityData activityData = new ActivityData();
            RealmList<Activities> activities = new RealmList<>();
            activityData.setMessage(activityData1.getMessage());
            activityData.setActivities(activities);
            activityData.setStudyId(((SurveyActivity) mContext).getStudyId());
            ActivityData activityDataDB = dbServiceSubscriber.getActivityPreference(((SurveyActivity) mContext).getStudyId(), mRealm);
            if (activityDataDB == null) {
                for (int i = 0; i < activityData1.getActivities().size(); i++) {
                    activityData1.getActivities().get(i).setStudyId(((SurveyActivity) mContext).getStudyId());
                    if (activityData1.getActivities().get(i).getActivityVersion() != null) {
                        activityData.getActivities().add(activityData1.getActivities().get(i));
                    }
                }
                dbServiceSubscriber.updateActivityState(activityData);
                activityDataDB = dbServiceSubscriber.getActivityPreference(((SurveyActivity) mContext).getStudyId(), mRealm);
            }

            // If any activities available in Db we take from Db otherwise from Webservice

            StudyUpdate studyUpdate = dbServiceSubscriber.getStudyUpdateById(((SurveyActivity) mContext).getStudyId(), mRealm);


            // find any updates on available activity

            ArrayList<String> activityIds = new ArrayList<>();
            ArrayList<String> runIds = new ArrayList<>();
            if (studyUpdate != null && studyUpdate.getStudyUpdateData().isActivities()) {
                mActivityUpdated = true;
                if (activityDataDB != null && activityData != null) {
                    for (int i = 0; i < activityDataDB.getActivities().size(); i++) {
                        for (int j = 0; j < activityListData.getActivities().size(); j++) {
                            if (activityDataDB.getActivities().get(i).getActivityId().equalsIgnoreCase(activityListData.getActivities().get(j).getActivityId())) {
                                if (!activityDataDB.getActivities().get(i).getActivityVersion().equalsIgnoreCase(activityListData.getActivities().get(j).getActivityVersion())) {
                                    dbServiceSubscriber.updateActivityPreferenceVersion(activityListData.getActivities().get(j).getActivityVersion(), activityDataDB.getActivities().get(i));
                                    activityIds.add(activityDataDB.getActivities().get(i).getActivityId());
                                    runIds.add(activityDataDB.getActivities().get(i).getActivityRunId());
                                }
                                break;
                            }
                        }
                    }
                }
            }


            displayData(activityListData, activityIds, runIds, null);

        } else if (responseCode == ACTIVTTYINFO_RESPONSECODE) {
            AppController.getHelperProgressDialog().dismissDialog();
            onItemsLoadComplete();
            ActivityInfoData activityInfoData = (ActivityInfoData) response;
            if (activityInfoData != null) {
                launchSurvey(activityInfoData.getActivity());
            } else {
                Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            AppController.getHelperProgressDialog().dismissDialog();
            onItemsLoadComplete();
            LoginData loginData = (LoginData) response;
            if (loginData != null) {
                updateActivityInfo(mActivityId);
                dbServiceSubscriber.deleteOfflineDataRow(mDeleteIndexNumberDB);
                dbServiceSubscriber.updateActivityPreferenceDB(mActivityId, ((SurveyActivity) mContext).getStudyId(), mCurrentRunId, SurveyActivitiesFragment.IN_PROGRESS, mActivityStatusData.getTotalRun(), mActivityStatusData.getCompletedRun(), mActivityStatusData.getMissedRun(), mActivityVersion);
            } else {
                Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }

        } else if (responseCode == UPDATE_STUDY_PREFERENCE) {
            //check for notification
            AppController.getHelperProgressDialog().dismissDialog();
            mGetResourceListWebservice();
            onItemsLoadComplete();
            checkForNotification();
        } else if (responseCode == RESOURCE_REQUEST_CODE) {
            // call study info
            callGetStudyInfoWebservice();
            if (response != null) {
                mStudyResource = (StudyResource) response;
            }
        } else if (responseCode == STUDY_INFO) {
            if (response != null) {
                StudyHome studyHome = (StudyHome) response;
                ((SurveyActivity) mContext).getStudyId();
                String mStudyId = ((SurveyActivity) mContext).getStudyId();
                dbServiceSubscriber.saveStudyInfoToDB(studyHome);


                if (mStudyResource != null) {
                    // primary key mStudyId
                    mStudyResource.setmStudyId(mStudyId);
                    // remove duplicate and
                    dbServiceSubscriber.deleteStudyResourceDuplicateRow(mStudyId);
                    dbServiceSubscriber.saveResourceList(mStudyResource);
                }
            }
        } else {
            AppController.getHelperProgressDialog().dismissDialog();
            onItemsLoadComplete();
        }
    }

    private void mGetResourceListWebservice() {

        HashMap<String, String> header = new HashMap<>();
        String studyId = ((SurveyActivity) mContext).getStudyId();
        header.put(STUDY_ID, studyId);
        String url = URLs.RESOURCE_LIST + QSTUDY + studyId;
        GetResourceListEvent getResourceListEvent = new GetResourceListEvent();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, RESOURCE_REQUEST_CODE,
                getActivity(), StudyResource.class, null, header, null, false, this);

        getResourceListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetResourceListEvent(getResourceListEvent);
    }

    private void callGetStudyInfoWebservice() {
        String studyId = ((SurveyActivity) mContext).getStudyId();
        HashMap<String, String> header = new HashMap<>();
        String url = URLs.STUDY_INFO + QSTUDY + studyId;
        GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, STUDY_INFO, getActivity(), StudyHome.class, null, header, null, false, this);

        getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
    }

    private void checkForNotification() {
        if (((SurveyActivity) mContext).mFrom.equalsIgnoreCase("NotificationActivity")
                && ((SurveyActivity) mContext).mLocalNotification.equalsIgnoreCase("true")
                && !((SurveyActivity) mContext).mTo.equalsIgnoreCase("Resource")) {
            ((SurveyActivity) mContext).mFrom = "";
            ((SurveyActivity) mContext).mLocalNotification = "";
            ((SurveyActivity) mContext).mTo = "";
            int position = 0;
            for (int i = 0; i < studyVideoAdapter.items.size(); i++) {
                if (studyVideoAdapter.items.get(i).getActivityId() != null && studyVideoAdapter.items.get(i).getActivityId().equalsIgnoreCase(((SurveyActivity) mContext).mActivityId)) {
                    position = i;
                    break;
                }
            }
            StudyList studyList = dbServiceSubscriber.getStudiesDetails(((SurveyActivity) mContext).getStudyId(), mRealm);
            boolean paused;
            if (studyList.getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
                paused = true;
            } else {
                paused = false;
            }
            if (paused) {
                Toast.makeText(mContext, R.string.study_Joined_paused, Toast.LENGTH_SHORT).show();
            } else {
                if (studyVideoAdapter.mStatus.get(position).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT) && (studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS) || studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.YET_TO_START))) {
                    if (studyVideoAdapter.mCurrentRunStatusForActivities.get(position).isRunIdAvailable()) {
                        getActivityInfo(studyVideoAdapter.items.get(position).getActivityId(), studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getCurrentRunId(), studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getStatus(), studyVideoAdapter.items.get(position).getBranching(), studyVideoAdapter.items.get(position).getActivityVersion(), studyVideoAdapter.mCurrentRunStatusForActivities.get(position));
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.survey_message), Toast.LENGTH_SHORT).show();
                    }
                } else if (studyVideoAdapter.mStatus.get(position).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
                    Toast.makeText(mContext, R.string.upcoming_event, Toast.LENGTH_SHORT).show();
                } else if (studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)) {
                    Toast.makeText(mContext, R.string.incomple_event, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, R.string.completed_event, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void displayData(ActivityListData activityListData, ArrayList<String> activityIds, ArrayList<String> runIds, String errormsg) {
        new CalculateRuns(activityListData, activityIds, runIds, errormsg).execute();
    }

    @Override
    public void clicked(int positon) {
        StudyList studyList = dbServiceSubscriber.getStudiesDetails(((SurveyActivity) mContext).getStudyId(), mRealm);
        boolean paused;
        if (studyList.getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
            paused = true;
        } else {
            paused = false;
        }
        filterPos = positon;
        Filter filter = getFilterList();
        studyVideoAdapter = new SurveyActivitiesListAdapter(mContext, filter.getActivitiesArrayList1(), filter.getStatus(), filter.getCurrentRunStatusForActivities(), SurveyActivitiesFragment.this, paused);
        mSurveyActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSurveyActivitiesRecyclerView.setAdapter(studyVideoAdapter);
    }

    private class CalculateRuns extends AsyncTask<ArrayList<ActivitiesWS>, Void, ArrayList<ActivitiesWS>> {


        ArrayList<ActivitiesWS> currentactivityList = new ArrayList<>();
        ArrayList<String> currentStatus = new ArrayList<>();
        ArrayList<ActivityStatus> currentActivityStatus = new ArrayList<>();
        ArrayList<ActivitiesWS> upcomingactivityList = new ArrayList<>();
        ArrayList<String> upcomingStatus = new ArrayList<>();
        ArrayList<ActivityStatus> upcomingActivityStatus = new ArrayList<>();
        ArrayList<ActivitiesWS> completedactivityList = new ArrayList<>();
        ArrayList<String> completedStatus = new ArrayList<>();
        ArrayList<ActivityStatus> completedActivityStatus = new ArrayList<>();
        private boolean updateRun = true;
        private ActivityListData activityListData;
        private ActivityListData activityListData2 = new ActivityListData();
        private ArrayList<String> activityIds;
        private ArrayList<String> runIds;
        int completed = 0;
        int missed = 0;
        int total = 0;
        RealmList<ActivitiesWS> activitiesArrayList = new RealmList<>();

        Realm mRealm;
        String title = "";
        String errormsg;

        CalculateRuns(ActivityListData activityListData, ArrayList<String> activityIds, ArrayList<String> runIds, String errormsg) {
            this.activityListData = activityListData;
            this.activityIds = activityIds;
            this.runIds = runIds;
            this.errormsg = errormsg;
        }

        @Override
        protected ArrayList<ActivitiesWS> doInBackground(ArrayList<ActivitiesWS>... params) {
            mRealm = AppController.getRealmobj();

            try {
                currentactivityList.clear();
            } catch (Exception e) {
            }
            try {
                upcomingactivityList.clear();
            } catch (Exception e) {
            }
            try {
                completedactivityList.clear();
            } catch (Exception e) {
            }


            // find new activity and deleted activity

            RealmList<ActivitiesWS> activitiesWSesDeleted = new RealmList<>();
            RealmList<ActivitiesWS> newlyAdded = new RealmList<>();
            ActivityListData activityListDataDB = dbServiceSubscriber.getActivities(((SurveyActivity) mContext).getStudyId(), mRealm);
            if (activityListDataDB != null && activityListDataDB.getActivities() != null && activityListDataDB.getActivities().size() > 0) {
                ActivityListData activityListData1 = null;
                mActivityUpdated = false;
                activityListData1 = new ActivityListData();
                activityListData1.setStudyId(activityListDataDB.getStudyId());
                activityListData1.setMessage(activityListDataDB.getMessage());
                activityListData1.setAnchorDate(activityListDataDB.getAnchorDate());
                activityListData1.setWithdrawalConfig(activityListDataDB.getWithdrawalConfig());

                if (activityListData == null) {
                    activityListData1.getActivities().addAll(activityListDataDB.getActivities());
                } else {
                    activityListData1.getActivities().addAll(activityListData.getActivities());

                    for (int i = 0; i < activityListDataDB.getActivities().size(); i++) {
                        boolean activityAvailable = false;
                        for (int j = 0; j < activityListData.getActivities().size(); j++) {
                            if (activityListData.getActivities().get(j).getActivityId().equalsIgnoreCase(activityListDataDB.getActivities().get(i).getActivityId())) {
                                activityAvailable = true;
                            }
                        }
                        if (!activityAvailable) {
                            mActivityUpdated = true;
                            activitiesWSesDeleted.add(activityListDataDB.getActivities().get(i));
                            activityListData1.getActivities().add(activityListDataDB.getActivities().get(i));
                        }
                    }


                    for (int j = 0; j < activityListData.getActivities().size(); j++) {
                        boolean activityAvailable = false;
                        for (int i = 0; i < activityListDataDB.getActivities().size(); i++) {
                            if (activityListData.getActivities().get(j).getActivityId().equalsIgnoreCase(activityListDataDB.getActivities().get(i).getActivityId())) {
                                activityAvailable = true;
                                if (activityListData.getActivities().get(j).getState().equalsIgnoreCase(DELETE) && activityListDataDB.getActivities().get(i).getState().equalsIgnoreCase(ACTIVE)) {
                                    RealmResults<ActivityRun> activityRuns = dbServiceSubscriber.getAllActivityRunFromDB(((SurveyActivity) mContext).getStudyId(), activityListData.getActivities().get(j).getActivityId(), mRealm);
                                    try {
                                        dbServiceSubscriber.deleteAllRun(activityRuns);
                                    } catch (Exception e) {
                                    }
                                    dbServiceSubscriber.saveActivityState(activityListDataDB.getActivities().get(i), mRealm);
                                }
                            }
                        }
                        if (!activityAvailable) {
                            newlyAdded.add(activityListData.getActivities().get(j));
                        }
                    }

                }


                updateRun = false;

                activityListData2 = activityListData1;

            } else {
                mActivityUpdated = false;
                if (activityListData != null) {
                    insertAndUpdateToDB(activityListData);
                    activityListData2 = activityListData;
                }
            }


            if (activityListData2 != null) {
                activitiesArrayList.addAll(activityListData2.getActivities());
                SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, mRealm);
                StudyData studyPreferences = dbServiceSubscriber.getStudyPreference(mRealm);
                ActivityData activityData = dbServiceSubscriber.getActivityPreference(((SurveyActivity) mContext).getStudyId(), mRealm);

                Date joiningDate = survayScheduler.getJoiningDateOfStudy(studyPreferences, ((SurveyActivity) mContext).getStudyId());

                Date currentDate = new Date();

                if (mActivityUpdated) {
                    dbServiceSubscriber.deleteMotivationalNotification(((SurveyActivity) mContext).getStudyId());
                }

                if (newlyAdded.size() > 0) {
                    // insert to activitylist db
                    //activityListDataDB
                    for (int k = 0; k < newlyAdded.size(); k++) {
                        dbServiceSubscriber.addActivityWSList(activityListDataDB, newlyAdded.get(k));
                    }
                }

                for (int i = 0; i < activitiesArrayList.size(); i++) {
                    SimpleDateFormat simpleDateFormat = AppController.getDateFormatUTC1();
                    Date starttime = null, endtime = null;
                    try {
                        if (activitiesArrayList.get(i).getStartTime().equalsIgnoreCase("")) {
                            starttime = new Date();
                        } else {
                            starttime = simpleDateFormat.parse(activitiesArrayList.get(i).getStartTime().split("\\.")[0]);
                        }
                    } catch (ParseException e) {
                    }
                    try {
                        endtime = simpleDateFormat.parse(activitiesArrayList.get(i).getEndTime().split("\\.")[0]);
                    } catch (ParseException e) {
                    } catch (Exception e1) {
                    }

                    RealmResults<ActivityRun> activityRuns = dbServiceSubscriber.getAllActivityRunFromDB(((SurveyActivity) mContext).getStudyId(), activitiesArrayList.get(i).getActivityId(), mRealm);

                    boolean deleted = false;
                    for (int j = 0; j < activitiesWSesDeleted.size(); j++) {
                        if (activitiesWSesDeleted.get(j).getActivityId().equalsIgnoreCase(activitiesArrayList.get(i).getActivityId())) {
                            deleted = true;
                            try {
                                dbServiceSubscriber.deleteAllRun(activityRuns);
                            } catch (Exception e) {
                            }
                        }
                    }

                    if (updateRun || activityRuns == null || activityRuns.size() == 0) {
                        if (!deleted)
                            survayScheduler.setRuns(activitiesArrayList.get(i), ((SurveyActivity) mContext).getStudyId(), starttime, endtime, joiningDate, mContext);
                    } else if (activityIds.size() > 0 && activityIds.contains(activitiesArrayList.get(i).getActivityId())) {
                        dbServiceSubscriber.deleteActivityRunsFromDb(activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                        if (!deleted)
                            survayScheduler.setRuns(activitiesArrayList.get(i), ((SurveyActivity) mContext).getStudyId(), starttime, endtime, joiningDate, mContext);
                        // delete activity object that used for survey
                        dbServiceSubscriber.deleteActivityObjectFromDb(activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                        for (int j = 0; j < activityData.getActivities().size(); j++) {
                            if (activitiesArrayList.get(i).getActivityId().equalsIgnoreCase(activityData.getActivities().get(j).getActivityId()) && !activityData.getActivities().get(j).getStatus().equalsIgnoreCase(YET_TO_START)) {
                                // Delete response data
                                dbServiceSubscriber.deleteResponseDataFromDb(((SurveyActivity) mContext).getStudyId() + STUDY_ID_TO_SPLIT + activitiesArrayList.get(i).getActivityId() + "_" + runIds.get(activityIds.indexOf(activitiesArrayList.get(i).getActivityId())));
                            }
                        }

                    }


                    String currentDateString = AppController.getDateFormatUTC().format(currentDate);
                    try {
                        currentDate = AppController.getDateFormatUTC().parse(currentDateString);
                    } catch (ParseException e) {
                    }
                    Calendar calendarCurrentTime = Calendar.getInstance();
                    calendarCurrentTime.setTime(currentDate);
                    calendarCurrentTime.setTimeInMillis(calendarCurrentTime.getTimeInMillis() - survayScheduler.getOffset(mContext));
                    if (!deleted) {
                        ActivityStatus activityStatus = survayScheduler.getActivityStatus(activityData, ((SurveyActivity) mContext).getStudyId(), activitiesArrayList.get(i).getActivityId(), calendarCurrentTime.getTime());
                        if (activityStatus != null) {
                            if (activityStatus.getCompletedRun() >= 0) {
                                completed = completed + activityStatus.getCompletedRun();
                            }
                            if (activityStatus.getMissedRun() >= 0) {
                                missed = missed + activityStatus.getMissedRun();
                            }
                            if (activityStatus.getTotalRun() >= 0) {
                                total = total + activityStatus.getTotalRun();
                            }
                        }
                        if (!activitiesArrayList.get(i).getState().equalsIgnoreCase("deleted")) {
                            if (isWithinRange(starttime, endtime)) {
                                currentactivityList.add(activitiesArrayList.get(i));
                                currentActivityStatus.add(activityStatus);
                                currentStatus.add(STATUS_CURRENT);
                            } else if (checkafter(starttime)) {
                                upcomingactivityList.add(activitiesArrayList.get(i));
                                upcomingActivityStatus.add(activityStatus);
                                upcomingStatus.add(STATUS_UPCOMING);
                            } else {
                                completedactivityList.add(activitiesArrayList.get(i));
                                completedActivityStatus.add(activityStatus);
                                completedStatus.add(STATUS_COMPLETED);
                            }
                        } else {
                            NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                            try {
                                notificationModuleSubscriber.cancleActivityLocalNotificationByIds(mContext, activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                            } catch (Exception e) {
                            }
                            try {
                                notificationModuleSubscriber.cancleResourcesLocalNotificationByIds(mContext, activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                            } catch (Exception e) {
                            }
                        }
                    } else {
                        NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                        try {
                            notificationModuleSubscriber.cancleActivityLocalNotificationByIds(mContext, activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                        } catch (Exception e) {
                        }
                        try {
                            notificationModuleSubscriber.cancleResourcesLocalNotificationByIds(mContext, activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                        } catch (Exception e) {
                        }

                        //delete from activity list db
                        dbServiceSubscriber.deleteActivityWSList(activityListDataDB, activitiesArrayList.get(i).getActivityId());
                    }


                }

                activitiesArrayList.clear();
            } else {
                if (errormsg != null) {
                    Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
                }
            }

            // sort
            for (int i = 0; i < currentactivityList.size(); i++) {
                for (int j = i; j < currentactivityList.size(); j++) {
                    try {
                        if (AppController.getDateFormat().parse(currentactivityList.get(i).getStartTime()).after(AppController.getDateFormat().parse(currentactivityList.get(j).getStartTime()))) {
                            ActivitiesWS activitiesWS = currentactivityList.get(i);
                            currentactivityList.set(i, currentactivityList.get(j));
                            currentactivityList.set(j, activitiesWS);

                            ActivityStatus activityStatus = currentActivityStatus.get(i);
                            currentActivityStatus.set(i, currentActivityStatus.get(j));
                            currentActivityStatus.set(j, activityStatus);

                            String status = currentStatus.get(i);
                            currentStatus.set(i, currentStatus.get(j));
                            currentStatus.set(j, status);

                        }
                    } catch (ParseException e) {
                    }
                }
            }

            ArrayList<ActivitiesWS> yetToStartOrResumeList = new ArrayList<>();
            ArrayList<ActivitiesWS> otherList = new ArrayList<>();
            ArrayList<ActivityStatus> yetToStartOrResumeActivityStatusList = new ArrayList<>();
            ArrayList<ActivityStatus> otherActivityStatusList = new ArrayList<>();
            ArrayList<String> yetToStartOrResumeStatusList = new ArrayList<>();
            ArrayList<String> otherStatusList = new ArrayList<>();
            for (int i = 0; i < currentactivityList.size(); i++) {
                if (currentActivityStatus.get(i).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.YET_TO_START) || currentActivityStatus.get(i).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS)) {
                    yetToStartOrResumeList.add(currentactivityList.get(i));
                    yetToStartOrResumeActivityStatusList.add(currentActivityStatus.get(i));
                    yetToStartOrResumeStatusList.add(currentStatus.get(i));
                } else {
                    otherList.add(currentactivityList.get(i));
                    otherActivityStatusList.add(currentActivityStatus.get(i));
                    otherStatusList.add(currentStatus.get(i));
                }
            }
            try {
                currentactivityList.clear();
            } catch (Exception e) {
            }
            try {
                currentActivityStatus.clear();
            } catch (Exception e) {
            }
            try {
                currentStatus.clear();
            } catch (Exception e) {
            }
            currentactivityList.addAll(yetToStartOrResumeList);
            currentactivityList.addAll(otherList);

            currentActivityStatus.addAll(yetToStartOrResumeActivityStatusList);
            currentActivityStatus.addAll(otherActivityStatusList);

            currentStatus.addAll(yetToStartOrResumeStatusList);
            currentStatus.addAll(otherStatusList);


            for (int i = 0; i < upcomingactivityList.size(); i++) {
                for (int j = i; j < upcomingactivityList.size(); j++) {
                    try {
                        if (AppController.getDateFormat().parse(upcomingactivityList.get(i).getStartTime()).after(AppController.getDateFormat().parse(upcomingactivityList.get(j).getStartTime()))) {
                            ActivitiesWS activitiesWS = upcomingactivityList.get(i);
                            upcomingactivityList.set(i, upcomingactivityList.get(j));
                            upcomingactivityList.set(j, activitiesWS);

                            ActivityStatus activityStatus = upcomingActivityStatus.get(i);
                            upcomingActivityStatus.set(i, upcomingActivityStatus.get(j));
                            upcomingActivityStatus.set(j, activityStatus);

                            String status = upcomingStatus.get(i);
                            upcomingStatus.set(i, upcomingStatus.get(j));
                            upcomingStatus.set(j, status);

                        }
                    } catch (ParseException e) {
                    }
                }
            }

            for (int i = 0; i < completedactivityList.size(); i++) {
                for (int j = i; j < completedactivityList.size(); j++) {
                    try {
                        if (AppController.getDateFormat().parse(completedactivityList.get(i).getStartTime()).after(AppController.getDateFormat().parse(completedactivityList.get(j).getStartTime()))) {
                            ActivitiesWS activitiesWS = completedactivityList.get(i);
                            completedactivityList.set(i, completedactivityList.get(j));
                            completedactivityList.set(j, activitiesWS);

                            ActivityStatus activityStatus = completedActivityStatus.get(i);
                            completedActivityStatus.set(i, completedActivityStatus.get(j));
                            completedActivityStatus.set(j, activityStatus);

                            String status = completedStatus.get(i);
                            completedStatus.set(i, completedStatus.get(j));
                            completedStatus.set(j, status);

                        }
                    } catch (ParseException e) {
                    }
                }
            }

            // Checking the Empty values
            if (currentactivityList.isEmpty()) {
                ActivitiesWS w = new ActivitiesWS();
                w.setActivityId("");
                currentactivityList.add(w);
            }
            if (upcomingactivityList.isEmpty()) {
                ActivitiesWS w = new ActivitiesWS();
                w.setActivityId("");
                upcomingactivityList.add(w);
            }
            if (completedactivityList.isEmpty()) {
                ActivitiesWS w = new ActivitiesWS();
                w.setActivityId("");
                completedactivityList.add(w);
            }


            activitiesArrayList.addAll(currentactivityList);
            activitiesArrayList.addAll(upcomingactivityList);
            activitiesArrayList.addAll(completedactivityList);

            activitiesArrayList1.clear();
            for (int k = 0; k < activitiesArrayList.size(); k++) {
                if (!activitiesArrayList.get(k).getActivityId().equalsIgnoreCase("")) {
                    ActivitiesWS activitiesWS = new ActivitiesWS();

                    Frequency frequency = new Frequency();
                    RealmList<FrequencyRuns> frequencyRunses = new RealmList<>();
                    for (int j = 0; j < activitiesArrayList.get(k).getFrequency().getRuns().size(); j++) {
                        FrequencyRuns frequencyRuns = new FrequencyRuns();
                        frequencyRuns.setEndTime(activitiesArrayList.get(k).getFrequency().getRuns().get(j).getEndTime());
                        frequencyRuns.setStartTime(activitiesArrayList.get(k).getFrequency().getRuns().get(j).getStartTime());
                        frequencyRunses.add(frequencyRuns);
                    }
                    frequency.setRuns(frequencyRunses);
                    frequency.setType(activitiesArrayList.get(k).getFrequency().getType());

                    activitiesWS.setFrequency(frequency);
                    activitiesWS.setStartTime(activitiesArrayList.get(k).getStartTime());
                    activitiesWS.setEndTime(activitiesArrayList.get(k).getEndTime());
                    activitiesWS.setType(activitiesArrayList.get(k).getType());
                    activitiesWS.setActivityVersion(activitiesArrayList.get(k).getActivityVersion());
                    activitiesWS.setActivityId(activitiesArrayList.get(k).getActivityId());
                    activitiesWS.setBranching(activitiesArrayList.get(k).getBranching());
                    activitiesWS.setStatus(activitiesArrayList.get(k).getStatus());
                    activitiesWS.setTitle(activitiesArrayList.get(k).getTitle());

                    activitiesArrayList1.add(activitiesWS);
                } else {
                    activitiesArrayList1.add(activitiesArrayList.get(k));
                }
            }

            status.clear();
            // Checking the size is zero
            if (currentStatus.size() == 0) {
                currentStatus.add(STATUS_CURRENT);
            }
            if (upcomingStatus.size() == 0) {
                upcomingStatus.add(STATUS_UPCOMING);
            }
            if (completedStatus.size() == 0) {
                completedStatus.add(STATUS_COMPLETED);
            }
            status.addAll(currentStatus);
            status.addAll(upcomingStatus);
            status.addAll(completedStatus);

            currentRunStatusForActivities.clear();

            // Checking the Empty values
            if (currentActivityStatus.isEmpty())
                currentActivityStatus.add(new ActivityStatus());
            if (upcomingActivityStatus.isEmpty())
                upcomingActivityStatus.add(new ActivityStatus());
            if (completedActivityStatus.isEmpty())
                completedActivityStatus.add(new ActivityStatus());
            currentRunStatusForActivities.addAll(currentActivityStatus);
            currentRunStatusForActivities.addAll(upcomingActivityStatus);
            currentRunStatusForActivities.addAll(completedActivityStatus);

            StudyList studyList = dbServiceSubscriber.getStudiesDetails(((SurveyActivity) mContext).getStudyId(), mRealm);
            boolean paused;
            if (studyList.getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
                paused = true;
            } else {
                paused = false;
            }
            title = studyList.getTitle();
            Filter filter = getFilterList();
            studyVideoAdapter = new SurveyActivitiesListAdapter(mContext, filter.getActivitiesArrayList1(), filter.getStatus(), filter.getCurrentRunStatusForActivities(), SurveyActivitiesFragment.this, paused);

            dbServiceSubscriber.closeRealmObj(mRealm);
            return activitiesArrayList1;
        }

        @Override
        protected void onPostExecute(ArrayList<ActivitiesWS> result) {

            mRealm = AppController.getRealmobj();

            mSurveyActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mSurveyActivitiesRecyclerView.setAdapter(studyVideoAdapter);

            AppController.getHelperSharedPreference().writePreference(mContext, mContext.getResources().getString(R.string.completedRuns), Integer.toString(completed));
            AppController.getHelperSharedPreference().writePreference(mContext, mContext.getResources().getString(R.string.missedRuns), Integer.toString(missed));
            AppController.getHelperSharedPreference().writePreference(mContext, mContext.getResources().getString(R.string.totalRuns), Integer.toString(total));

            double completion = 0;
            MotivationalNotification motivationalNotification = dbServiceSubscriber.getMotivationalNotification(((SurveyActivity) mContext).getStudyId(), mRealm);
            if (total > 0)
                completion = (((double) completed + (double) missed) / (double) total) * 100d;

            boolean hundredPc = false;
            boolean fiftyPc = false;
            if (motivationalNotification == null) {
                if (completion >= 100) {
                    hundredPc = true;
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.study) + " " + title + " " + mContext.getResources().getString(R.string.percent_complete1), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));
                } else if (completion >= 50) {
                    fiftyPc = true;
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.study) + " " + title + " " + mContext.getResources().getString(R.string.percent_complete2), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));

                } else if (missed > 0) {
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.missed_activity) + " " + ((SurveyActivity) mContext).getTitle1() + " " + mContext.getResources().getString(R.string.we_encourage), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));

                }
            } else if (!motivationalNotification.isFiftyPc() && !motivationalNotification.isHundredPc()) {
                if (completion >= 100) {
                    hundredPc = true;
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.study) + " " + title + " " + mContext.getResources().getString(R.string.percent_complete1), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));
                } else if (completion >= 50) {
                    fiftyPc = true;
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.study) + " " + title + " " + mContext.getResources().getString(R.string.percent_complete2), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));
                }
            } else if (!motivationalNotification.isHundredPc()) {
                if (completion >= 100) {
                    hundredPc = true;
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.study) + " " + title + " " + mContext.getResources().getString(R.string.percent_complete1), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));
                }

            } else if (!motivationalNotification.isFiftyPc()) {
                if (!motivationalNotification.isHundredPc() && completion >= 50) {
                    fiftyPc = true;
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.study) + " " + title + " " + mContext.getResources().getString(R.string.percent_complete1), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));
                } else if (motivationalNotification.isHundredPc()) {
                    fiftyPc = true;
                }

            } else if (motivationalNotification.getMissed() != missed) {
                SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.missed_activity) + " " + ((SurveyActivity) mContext).getTitle1() + " " + mContext.getResources().getString(R.string.we_encourage), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));

            }

            if (motivationalNotification != null && motivationalNotification.isHundredPc()) {
                hundredPc = true;
            }

            if (motivationalNotification != null && motivationalNotification.isFiftyPc()) {
                fiftyPc = true;
            }

            // update motivational table
            MotivationalNotification motivationalNotification1 = new MotivationalNotification();
            motivationalNotification1.setStudyId(((SurveyActivity) mContext).getStudyId());
            motivationalNotification1.setFiftyPc(fiftyPc);
            motivationalNotification1.setHundredPc(hundredPc);
            motivationalNotification1.setMissed(missed);
            dbServiceSubscriber.saveMotivationalNotificationToDB(motivationalNotification1);

            dbServiceSubscriber.closeRealmObj(mRealm);
            double adherence = 0;
            if (((double) completed + (double) missed + 1d) > 0)
                adherence = (((double) completed + 1d) / ((double) completed + (double) missed + 1d)) * 100d;

            updateStudyState(Integer.toString((int) completion), Integer.toString((int) adherence));

        }

        @Override
        protected void onPreExecute() {
        }

    }

    public void updateStudyState(String completion, String adherence) {
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.auth), ""));
        header.put(USER_ID, AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        JSONArray studieslist = new JSONArray();
        JSONObject studiestatus = new JSONObject();
        try {
            studiestatus.put(STUDY_ID, ((SurveyActivity) mContext).getStudyId());
            studiestatus.put("completion", completion);
            studiestatus.put("adherence", adherence);

        } catch (JSONException e) {
        }

        studieslist.put(studiestatus);
        try {
            jsonObject.put("studies", studieslist);
        } catch (JSONException e) {
        }
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent(POST_OBJECT, URLs.UPDATE_STUDY_PREFERENCE, UPDATE_STUDY_PREFERENCE, mContext, LoginData.class, null, header, jsonObject, false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    private Filter getFilterList() {
        Filter filter = new Filter();
        ArrayList<ActivitiesWS> filterActivitiesArrayList1 = new ArrayList<>();
        ArrayList<String> filterStatus = new ArrayList<>();
        ArrayList<ActivityStatus> filterCurrentRunStatusForActivities = new ArrayList<>();
        boolean isCurrentAvailable = false;
        boolean isUpcommingAvailable = false;
        boolean isCompletedAvailable = false;
        if (filterPos == 0) {
            isCurrentAvailable = true;
            isUpcommingAvailable = true;
            isCompletedAvailable = true;
            filterActivitiesArrayList1.addAll(activitiesArrayList1);
            filterStatus.addAll(status);
            filterCurrentRunStatusForActivities.addAll(currentRunStatusForActivities);
        } else if (filterPos == 1) {

            for (int i = 0; i < activitiesArrayList1.size(); i++) {
                if (activitiesArrayList1.get(i).getType() == null || activitiesArrayList1.get(i).getType().equalsIgnoreCase("questionnaire")) {

                    if (status.get(i).equalsIgnoreCase(STATUS_CURRENT)) {
                        isCurrentAvailable = true;
                    } else if (status.get(i).equalsIgnoreCase(STATUS_UPCOMING)) {
                        if (!isCurrentAvailable) {
                            ActivitiesWS w = new ActivitiesWS();
                            w.setActivityId("");
                            filterActivitiesArrayList1.add(w);
                            filterStatus.add(STATUS_CURRENT);
                            filterCurrentRunStatusForActivities.add(new ActivityStatus());
                        }
                        isCurrentAvailable = true;
                        isUpcommingAvailable = true;
                    } else if (status.get(i).equalsIgnoreCase(STATUS_COMPLETED)) {
                        if (!isCurrentAvailable) {
                            ActivitiesWS w = new ActivitiesWS();
                            w.setActivityId("");
                            filterActivitiesArrayList1.add(w);
                            filterStatus.add(STATUS_CURRENT);
                            filterCurrentRunStatusForActivities.add(new ActivityStatus());

                        }
                        if (!isUpcommingAvailable) {
                            ActivitiesWS w = new ActivitiesWS();
                            w.setActivityId("");
                            filterActivitiesArrayList1.add(w);
                            filterStatus.add(STATUS_UPCOMING);
                            filterCurrentRunStatusForActivities.add(new ActivityStatus());
                        }
                        isCurrentAvailable = true;
                        isUpcommingAvailable = true;
                        isCompletedAvailable = true;
                    }

                    filterActivitiesArrayList1.add(activitiesArrayList1.get(i));
                    filterStatus.add(status.get(i));
                    filterCurrentRunStatusForActivities.add(currentRunStatusForActivities.get(i));
                }
            }
        } else if (filterPos == 2) {
            for (int i = 0; i < activitiesArrayList1.size(); i++) {
                if (activitiesArrayList1.get(i).getType() == null || activitiesArrayList1.get(i).getType().equalsIgnoreCase("task")) {

                    if (status.get(i).equalsIgnoreCase(STATUS_CURRENT)) {
                        isCurrentAvailable = true;
                    } else if (status.get(i).equalsIgnoreCase(STATUS_UPCOMING)) {
                        if (!isCurrentAvailable) {
                            ActivitiesWS w = new ActivitiesWS();
                            w.setActivityId("");
                            filterActivitiesArrayList1.add(w);
                            filterStatus.add(STATUS_CURRENT);
                            filterCurrentRunStatusForActivities.add(new ActivityStatus());
                        }
                        isCurrentAvailable = true;
                        isUpcommingAvailable = true;
                    } else if (status.get(i).equalsIgnoreCase(STATUS_COMPLETED)) {
                        if (!isCurrentAvailable) {
                            ActivitiesWS w = new ActivitiesWS();
                            w.setActivityId("");
                            filterActivitiesArrayList1.add(w);
                            filterStatus.add(STATUS_CURRENT);
                            filterCurrentRunStatusForActivities.add(new ActivityStatus());

                        }
                        if (!isUpcommingAvailable) {
                            ActivitiesWS w = new ActivitiesWS();
                            w.setActivityId("");
                            filterActivitiesArrayList1.add(w);
                            filterStatus.add(STATUS_UPCOMING);
                            filterCurrentRunStatusForActivities.add(new ActivityStatus());
                        }
                        isCurrentAvailable = true;
                        isUpcommingAvailable = true;
                        isCompletedAvailable = true;
                    }

                    filterActivitiesArrayList1.add(activitiesArrayList1.get(i));
                    filterStatus.add(status.get(i));
                    filterCurrentRunStatusForActivities.add(currentRunStatusForActivities.get(i));
                }
            }
        }

        if (!isCurrentAvailable) {
            ActivitiesWS w = new ActivitiesWS();
            w.setActivityId("");
            filterActivitiesArrayList1.add(w);
            filterStatus.add(STATUS_CURRENT);
            filterCurrentRunStatusForActivities.add(new ActivityStatus());

        }
        if (!isUpcommingAvailable) {
            ActivitiesWS w = new ActivitiesWS();
            w.setActivityId("");
            filterActivitiesArrayList1.add(w);
            filterStatus.add(STATUS_UPCOMING);
            filterCurrentRunStatusForActivities.add(new ActivityStatus());
        }
        if (!isCompletedAvailable) {
            ActivitiesWS w = new ActivitiesWS();
            w.setActivityId("");
            filterActivitiesArrayList1.add(w);
            filterStatus.add(STATUS_COMPLETED);
            filterCurrentRunStatusForActivities.add(new ActivityStatus());
        }

        filter.setActivitiesArrayList1(filterActivitiesArrayList1);
        filter.setCurrentRunStatusForActivities(filterCurrentRunStatusForActivities);
        filter.setStatus(filterStatus);
        return filter;
    }


    private <E> void insertAndUpdateToDB(E e) {
        DatabaseEvent databaseEvent = new DatabaseEvent();
        databaseEvent.setE(e);
        databaseEvent.setmType(DBServiceSubscriber.TYPE_COPY_UPDATE);
        databaseEvent.setaClass(EligibilityConsent.class);
        databaseEvent.setmOperation(DBServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
        dbServiceSubscriber.insert(databaseEvent);
    }

    boolean isWithinRange(Date starttime, Date endtime) {
        if (endtime == null) {
            return (new Date().after(starttime) || new Date().equals(starttime));
        } else {
            return (new Date().after(starttime) || new Date().equals(starttime)) && new Date().before(endtime);
        }
    }

    boolean checkafter(Date starttime) {
        return starttime.after(new Date());
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        if (statusCode.equalsIgnoreCase("401")) {
            onItemsLoadComplete();
            AppController.getHelperProgressDialog().dismissDialog();
            Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(mContext, errormsg);
        } else {
            if (responseCode == ACTIVTTYLIST_RESPONSECODE || responseCode == STUDY_UPDATES) {
                displayData(null, new ArrayList<String>(), new ArrayList<String>(), errormsg);
            } else if (responseCode == ACTIVTTYINFO_RESPONSECODE) {
                onItemsLoadComplete();
                AppController.getHelperProgressDialog().dismissDialog();
                launchSurvey(null);
            } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
                onItemsLoadComplete();
                AppController.getHelperProgressDialog().dismissDialog();
                dbServiceSubscriber.updateActivityPreferenceDB(mActivityId, ((SurveyActivity) mContext).getStudyId(), mCurrentRunId, SurveyActivitiesFragment.IN_PROGRESS, mActivityStatusData.getTotalRun(), mActivityStatusData.getCompletedRun(), mActivityStatusData.getMissedRun(), mActivityVersion);
                launchSurvey(null);
            } else {
                try {
                    onItemsLoadComplete();
                } catch (Exception e) {
                }
                try {
                    AppController.getHelperProgressDialog().dismissDialog();
                } catch (Exception e) {
                }
            }
        }
    }

    public void getActivityInfo(String activityId, int currentRunId, String status, boolean branching, String activityVersion, ActivityStatus activityStatus) {
        mCurrentRunId = currentRunId;
        mActivityStatus = status;
        mActivityStatusData = activityStatus;
        mActivityId = activityId;
        mBranching = branching;
        mActivityVersion = activityVersion;
        if (status.equalsIgnoreCase(YET_TO_START)) {
            updateUserPreference(((SurveyActivity) mContext).getStudyId(), status, activityId, mCurrentRunId);
        } else {
            updateActivityInfo(activityId);
        }
    }

    private void updateActivityInfo(String activityId) {
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);

        GetActivityInfoEvent getActivityInfoEvent = new GetActivityInfoEvent();
        HashMap<String, String> header = new HashMap();
        String url = URLs.ACTIVITY + QSTUDY + ((SurveyActivity) mContext).getStudyId() + "&activityId=" + activityId + "&activityVersion=" + mActivityVersion;
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, ACTIVTTYINFO_RESPONSECODE, mContext, ActivityInfoData.class, null, header, null, false, this);

        getActivityInfoEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetActivityInfo(getActivityInfoEvent);
    }


    private void launchSurvey(ActivityObj activity) {
        try {
            mActivityObj = new ActivityObj();
            mActivityObj = dbServiceSubscriber.getActivityBySurveyId(((SurveyActivity) mContext).getStudyId(), mActivityId, mRealm);
            if (mActivityObj == null && activity != null) {
                mActivityObj = activity;
                mActivityObj.setSurveyId(mActivityObj.getMetadata().getActivityId());
                mActivityObj.setStudyId(((SurveyActivity) mContext).getStudyId());
                dbServiceSubscriber.saveActivity(mActivityObj);
            }

            if (mActivityObj != null) {
                AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.mapCount), "0");
                stepsBuilder = new StepsBuilder(mContext, mActivityObj, mBranching, mRealm);
                mTask = ActivityBuilder.create(((SurveyActivity) mContext).getStudyId() + STUDY_ID_TO_SPLIT + mActivityObj.getSurveyId() + "_" + mCurrentRunId, stepsBuilder.getsteps(), mActivityObj, mBranching, dbServiceSubscriber);
                if (mTask.getSteps().size() > 0) {
                    for (int i = 0; i < mActivityObj.getSteps().size(); i++) {
                        if (mActivityObj.getSteps().get(i).getResultType().equalsIgnoreCase("location")) {
                            locationPermission = true;
                        }
                    }
                    if (locationPermission) {
                        if ((ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                            if (!hasPermissions(permission)) {
                                ActivityCompat.requestPermissions((Activity) mContext, permission, PERMISSION_REQUEST_CODE);
                            } else {
                                startsurvey();
                            }
                        } else {
                            startsurvey();
                        }
                    } else {
                        startsurvey();
                    }
                } else {
                    Toast.makeText(mContext, R.string.no_task_available, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, R.string.no_ableto_getdata, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, R.string.couldnot_launch_survey, Toast.LENGTH_SHORT).show();
        }
    }

    private void startsurvey() {
        Intent intent = CustomSurveyViewTaskActivity.newIntent(mContext, ((SurveyActivity) mContext).getStudyId() +STUDY_ID_TO_SPLIT + mActivityObj.getSurveyId() + "_" + mCurrentRunId, ((SurveyActivity) mContext).getStudyId(), mCurrentRunId, mActivityStatus, mActivityStatusData.getMissedRun(), mActivityStatusData.getCompletedRun(), mActivityStatusData.getTotalRun(), mActivityVersion, mActivityStatusData.getCurrentRunStartDate(), mActivityStatusData.getCurrentRunEndDate(), mActivityObj.getSurveyId(),mBranching);
        startActivityForResult(intent, 123);
    }

    public boolean hasPermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && mContext != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void updateUserPreference(String studyId, String status, String activityId, int activityRunId) {
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.auth), ""));
        header.put(USER_ID, AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        JSONArray activitylist = new JSONArray();
        JSONObject activityStatus = new JSONObject();
        JSONObject activityRun = new JSONObject();
        try {
            activityStatus.put(STUDY_ID, studyId);
            activityStatus.put("activityState", IN_PROGRESS);
            activityStatus.put("activityId", activityId);
            activityStatus.put("activityRunId", "" + activityRunId);
            activityStatus.put("bookmarked", "false");
            activityStatus.put("activityVersion", mActivityVersion);

            activityRun.put("total", mActivityStatusData.getTotalRun());
            activityRun.put("completed", mActivityStatusData.getCompletedRun());
            activityRun.put("missed", mActivityStatusData.getMissedRun());

            activityStatus.put("activityRun", activityRun);

        } catch (JSONException e) {
        }

        activitylist.put(activityStatus);

        try {
            jsonObject.put(STUDY_ID, studyId);
            jsonObject.put("activity", activitylist);
        } catch (JSONException e) {
        }

        /////////// offline data storing
        try {
            int number = dbServiceSubscriber.getUniqueID(mRealm);
            if (number == 0) {
                number = 1;
            } else {
                number += 1;
            }

            // studyId, activityId combines and handling the duplication
            String studyIdActivityId = studyId + activityId;

            OfflineData offlineData = dbServiceSubscriber.getActivityIdOfflineData(studyIdActivityId, mRealm);
            if (offlineData != null) {
                number = offlineData.getNumber();
            }
            mDeleteIndexNumberDB = number;
            AppController.pendingService(number, POST_OBJECT, URLs.UPDATE_ACTIVITY_PREFERENCE, "", jsonObject.toString(), "registration", "", "", studyIdActivityId);
        } catch (Exception e) {
        }

        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent(POST_OBJECT, URLs.UPDATE_ACTIVITY_PREFERENCE, UPDATE_USERPREFERENCE_RESPONSECODE, mContext, LoginData.class, null, header, jsonObject, false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(mContext, R.string.current_locationwillnot_used, Toast.LENGTH_SHORT).show();
            }
            startsurvey();

        }
    }

    @Override
    public void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }
}
