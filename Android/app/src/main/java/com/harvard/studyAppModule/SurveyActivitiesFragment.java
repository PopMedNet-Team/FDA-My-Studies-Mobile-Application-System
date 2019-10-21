package com.harvard.studyAppModule;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
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
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.notificationModule.NotificationModuleSubscriber;
import com.harvard.offlineModule.model.OfflineData;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.storageModule.events.DatabaseEvent;
import com.harvard.studyAppModule.activityBuilder.ActivityBuilder;
import com.harvard.studyAppModule.activityBuilder.CustomSurveyViewTaskActivity;
import com.harvard.studyAppModule.activityBuilder.StepsBuilder;
import com.harvard.studyAppModule.activityBuilder.model.ActivityRun;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityInfoData;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.studyAppModule.acvitityListModel.ActivitiesWS;
import com.harvard.studyAppModule.acvitityListModel.ActivityListData;
import com.harvard.studyAppModule.acvitityListModel.AnchorDateSchedulingDetails;
import com.harvard.studyAppModule.acvitityListModel.Frequency;
import com.harvard.studyAppModule.acvitityListModel.FrequencyRuns;
import com.harvard.studyAppModule.consent.ConsentBuilder;
import com.harvard.studyAppModule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyAppModule.consent.model.Consent;
import com.harvard.studyAppModule.consent.model.CorrectAnswerString;
import com.harvard.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.studyAppModule.events.GetActivityInfoEvent;
import com.harvard.studyAppModule.events.GetActivityListEvent;
import com.harvard.studyAppModule.events.GetResourceListEvent;
import com.harvard.studyAppModule.events.GetUserStudyInfoEvent;
import com.harvard.studyAppModule.events.GetUserStudyListEvent;
import com.harvard.studyAppModule.studyModel.MotivationalNotification;
import com.harvard.studyAppModule.studyModel.StudyHome;
import com.harvard.studyAppModule.studyModel.StudyList;
import com.harvard.studyAppModule.studyModel.StudyResource;
import com.harvard.studyAppModule.studyModel.StudyUpdate;
import com.harvard.studyAppModule.studyModel.StudyUpdateListdata;
import com.harvard.studyAppModule.survayScheduler.SurvayScheduler;
import com.harvard.studyAppModule.survayScheduler.model.ActivityStatus;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.event.GetPreferenceEvent;
import com.harvard.userModule.event.UpdatePreferenceEvent;
import com.harvard.userModule.webserviceModel.Activities;
import com.harvard.userModule.webserviceModel.ActivityData;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.userModule.webserviceModel.Studies;
import com.harvard.userModule.webserviceModel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.SetDialogHelper;
import com.harvard.utils.URLs;
import com.harvard.utils.realm.Logger;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.apiHelper.ConnectionDetector;
import com.harvard.webserviceModule.apiHelper.HttpRequest;
import com.harvard.webserviceModule.apiHelper.Responsemodel;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.WCPConfigEvent;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int RESOURCE_REQUEST_CODE = 213;
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

    public final int ACTIVTTYLIST_RESPONSECODE = 100;
    public final int ACTIVTTYINFO_RESPONSECODE = 101;
    public static final String YET_To_START = "yetToJoin";
    public static final String IN_PROGRESS = "inProgress";
    public static final String COMPLETED = "completed";
    public static final String INCOMPLETE = "abandoned";

    public static final String STATUS_CURRENT = "Current";
    public static final String STATUS_UPCOMING = "Upcoming";
    public static final String STATUS_COMPLETED = "Completed";


    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_STUDYID = "ViewTaskActivity.ExtraStudyId";
    public static final String STUDYID = "ViewTaskActivity.StudyId";
    public static final String RUNID = "ViewTaskActivity.RunId";
    public static final String ACTIVITY_STATUS = "ViewTaskActivity.Status";
    public static final String MISSED_RUN = "ViewTaskActivity.MissedRun";
    public static final String COMPLETED_RUN = "ViewTaskActivity.CompletedRun";
    public static final String TOTAL_RUN = "ViewTaskActivity.TotalRun";
    public static final String EXTRA_SURVEYOBJ = "ViewTaskActivity.ExtraSurveyobj";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";
    public static final String ACTIVITY_VERSION = "ViewTaskActivity.Activity_Version";
    private static final String RUN_START_DATE = "ViewTaskActivity.RunStartDate";
    private static final String RUN_END_DATE = "ViewTaskActivity.RunEndDate";


    private boolean updatePreferenceToDB = false;
    private ActivityListData activityListData;

    String signatureBase64 = "";
    String signatureDate = "";
    String firstName = "";
    String lastName = "";
    String eligibilityType = "";
    private static File myFile, file, encryptFile;
    private static final String FILE_FOLDER = "FDA_PDF";
    private String mFileName;

    OrderedTask mTask;
    ActivityObj mActivityObj;
    private ActivityStatus mActivityStatusData;
    boolean locationPermission = false;
    private ActivitiesWS selectedActivity;
    private int mDeleteIndexNumberDB;
    private EligibilityConsent eligibilityConsent;
    private String mTitl;
    public static String FROM_SURVAY = "survey";
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;
    private boolean mActivityUpdated = false;
    public static String DELETE = "deleted";
    private String ACTIVE = "active";
    private SurveyActivitiesListAdapter studyVideoAdapter;
    private int filterPos = 0;
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<ActivitiesWS> activitiesArrayList1 = new ArrayList<>();
    private ArrayList<ActivityStatus> currentRunStatusForActivities = new ArrayList<>();
    private StudyResource mStudyResource;
    StepsBuilder stepsBuilder;

    ArrayList<AnchorDateSchedulingDetails> mArrayList;
    ActivityData activityDataDB;


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
        mRealm = AppController.getRealmobj(mContext);
        try {
            AppController.getHelperHideKeyboard((Activity) mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeXMLId(view);
        setTextForView();
        setFont();
        bindEvents();
        getStudyUpdateFomWS(false);

        Logger.showDbStats();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            AppController.getHelperHideKeyboard(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeXMLId(View view) {
        mBackBtn = (RelativeLayout) view.findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) view.findViewById(R.id.title);
        mFilterBtn = (RelativeLayout) view.findViewById(R.id.filterBtn);
        mSurveyActivitiesRecyclerView = (RecyclerView) view.findViewById(R.id.mSurveyActivitiesRecyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

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
        mTitle.setText(mContext.getResources().getString(R.string.study_activities));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(getActivity(), "bold"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
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
        String url = URLs.STUDY_UPDATES + "?studyId=" + ((SurveyActivity) mContext).getStudyId() + "&studyVersion=" + studyList.getStudyVersion();
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
        String url = URLs.ACTIVITY_LIST + "?studyId=" + ((SurveyActivity) mContext).getStudyId();
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


            String url = URLs.BASE_URL_WCP_SERVER + URLs.CONSENT_METADATA + "?studyId=" + ((SurveyActivity) mContext).getStudyId();
            if (connectionDetector.isConnectingToInternet()) {
                mResponseModel = HttpRequest.getRequest(url, new HashMap<String, String>(), "WCP");
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
        protected void onPostExecute(String result) {
            AppController.getHelperProgressDialog().dismissDialog();
            onItemsLoadComplete();
            if (response != null) {
                if (response.equalsIgnoreCase("session expired")) {
                    AppController.getHelperProgressDialog().dismissDialog();
                    AppController.getHelperSessionExpired(mContext, "session expired");
                } else if (response.equalsIgnoreCase("timeout")) {
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
                        saveConsentToDB(mContext,eligibilityConsent);
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

    private void saveConsentToDB(Context context,EligibilityConsent eligibilityConsent) {
        DatabaseEvent databaseEvent = new DatabaseEvent();
        databaseEvent.setE(eligibilityConsent);
        databaseEvent.setmType(DBServiceSubscriber.TYPE_COPY_UPDATE);
        databaseEvent.setaClass(EligibilityConsent.class);
        databaseEvent.setmOperation(DBServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
        dbServiceSubscriber.insert(context,databaseEvent);
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
                Intent intent = new Intent(getActivity(), ConsentCompletedActivity.class);
                intent.putExtra(ConsentCompletedActivity.FROM, "survey");
                intent.putExtra("studyId", ((SurveyActivity) mContext).getStudyId());
                intent.putExtra("title", mTitl);
                intent.putExtra("eligibility", eligibilityType);
                intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
                // get the encrypted file path
                intent.putExtra("PdfPath", data.getStringExtra("PdfPath"));
                startActivityForResult(intent, CONSENT_COMPLETE);

            } else {
                Toast.makeText(mContext, R.string.consent_complete, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, StudyActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(cn);
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
                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                mContext.startActivity(mainIntent);
                ((Activity) mContext).finish();
            }
        }
    }

    private void genarateConsentPDF(String signatureBase64) {
        try {
            getFile("/data/data/" + mContext.getPackageName() + "/files/");
            Date date = new Date();
            String timeStamp = AppController.getDateFormatType3();
            mFileName = timeStamp;
            String filePath = "/data/data/" + mContext.getPackageName() + "/files/" + timeStamp + ".pdf";
            myFile = new File(filePath);
            if (!myFile.exists())
                myFile.createNewFile();
            OutputStream output = new FileOutputStream(myFile);

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

            byte[] signatureBytes;
            Image myImg = null;
            if (signatureBase64 != null) {
                signatureBytes = Base64.decode(signatureBase64, Base64.DEFAULT);
                myImg = Image.getInstance(signatureBytes);
                myImg.setScaleToFitHeight(true);
                myImg.scalePercent(50f);
            }

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
            e.printStackTrace();
        }
    }

    public PdfPCell getImage(Image image, int alignment) {
        PdfPCell cell;
        if (image != null) {
            cell = new PdfPCell(image);
        } else {
            cell = new PdfPCell();
        }
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
        file = new File(s, FILE_FOLDER);
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
            dbServiceSubscriber.saveStudyUpdateListdataToDB(mContext,studyUpdateListdata);

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
                    dbServiceSubscriber.deleteResourcesFromDb(mContext,((SurveyActivity) mContext).getStudyId());
                }
                if (studyUpdate.getStudyUpdateData().isInfo()) {
                    dbServiceSubscriber.deleteStudyInfoFromDb(mContext,((SurveyActivity) mContext).getStudyId());
                }
                if (studyUpdate.getStudyUpdateData().isConsent()) {
                    callConsentMetaDataWebservice();
                } else {
                    StudyList studyList = dbServiceSubscriber.getStudyTitle(((SurveyActivity) mContext).getStudyId(), mRealm);
                    dbServiceSubscriber.updateStudyPreferenceVersionDB(mContext,((SurveyActivity) mContext).getStudyId(), studyList.getStudyVersion());
                    setRecyclerView();
                }
            }

        } else if (responseCode == CONSENT_METADATA) {
            eligibilityConsent = (EligibilityConsent) response;
            if (eligibilityConsent != null) {
                eligibilityConsent.setStudyId(((SurveyActivity) mContext).getStudyId());
                saveConsentToDB(mContext,eligibilityConsent);
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
            header.put("userId", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));
            String url = URLs.ACTIVITY_STATE + "?studyId=" + ((SurveyActivity) mContext).getStudyId();
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
            activityDataDB = dbServiceSubscriber.getActivityPreference(((SurveyActivity) mContext).getStudyId(), mRealm);
            if (activityDataDB == null) {
                for (int i = 0; i < activityData1.getActivities().size(); i++) {
                    activityData1.getActivities().get(i).setStudyId(((SurveyActivity) mContext).getStudyId());
                    if (activityData1.getActivities().get(i).getActivityVersion() != null) {
                        activityData.getActivities().add(activityData1.getActivities().get(i));
                    }
                }
                dbServiceSubscriber.updateActivityState(mContext,activityData);
                activityDataDB = dbServiceSubscriber.getActivityPreference(((SurveyActivity) mContext).getStudyId(), mRealm);
            }


            calculateStartAnsEndDateForActivities();

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
                //mActivityVersion
                dbServiceSubscriber.deleteOfflineDataRow(mContext,mDeleteIndexNumberDB);
                dbServiceSubscriber.updateActivityPreferenceDB(mContext,mActivityId, ((SurveyActivity) mContext).getStudyId(), mCurrentRunId, SurveyActivitiesFragment.IN_PROGRESS, mActivityStatusData.getTotalRun(), mActivityStatusData.getCompletedRun(), mActivityStatusData.getMissedRun(), mActivityVersion);
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
                dbServiceSubscriber.saveStudyInfoToDB(mContext,studyHome);


                if (mStudyResource != null) {
                    // primary key mStudyId
                    mStudyResource.setmStudyId(mStudyId);
                    // remove duplicate and
                    dbServiceSubscriber.deleteStudyResourceDuplicateRow(mContext,mStudyId);
                    dbServiceSubscriber.saveResourceList(mContext,mStudyResource);
                }
            }
        } else {
            AppController.getHelperProgressDialog().dismissDialog();
            onItemsLoadComplete();
        }
    }

    private void calculateStartAnsEndDateForActivities() {
        //call to resp server to get anchorDate
        mArrayList = new ArrayList<>();
        AnchorDateSchedulingDetails anchorDateSchedulingDetails;
        if (activityListData == null) {
            ActivityListData activityListDataTemp = dbServiceSubscriber.getActivities(((SurveyActivity) mContext).getStudyId(), mRealm);

            if (activityListDataTemp != null)
                activityListData = mRealm.copyFromRealm(activityListDataTemp);

        }
        if (activityDataDB == null) {
            ActivityData activityDataTemp = dbServiceSubscriber.getActivityPreference(((SurveyActivity) mContext).getStudyId(), mRealm);

            if (activityDataTemp != null)
                activityDataDB = mRealm.copyFromRealm(activityDataTemp);

        }
        if (activityListData != null && activityListData.getActivities() != null && activityDataDB != null) {
            for (int i = 0; i < activityListData.getActivities().size(); i++) {
                if (activityListData.getActivities().get(i).getSchedulingType() != null) {
                    if (activityListData.getActivities().get(i).getSchedulingType().equalsIgnoreCase("AnchorDate")) {
                        Studies studies = dbServiceSubscriber.getStudies(((SurveyActivity) mContext).getStudyId(), mRealm);
                        if (activityListData.getActivities().get(i).getAnchorDate().getSourceType().equalsIgnoreCase("ActivityResponse")) {
                            anchorDateSchedulingDetails = new AnchorDateSchedulingDetails();
                            anchorDateSchedulingDetails.setSourceActivityId(activityListData.getActivities().get(i).getAnchorDate().getSourceActivityId());
                            anchorDateSchedulingDetails.setSourceKey(activityListData.getActivities().get(i).getAnchorDate().getSourceKey());
                            anchorDateSchedulingDetails.setSourceFormKey(activityListData.getActivities().get(i).getAnchorDate().getSourceFormKey());


                            anchorDateSchedulingDetails.setSchedulingType(activityListData.getActivities().get(i).getSchedulingType());
                            anchorDateSchedulingDetails.setSourceType(activityListData.getActivities().get(i).getAnchorDate().getSourceType());
                            anchorDateSchedulingDetails.setStudyId(((SurveyActivity) mContext).getStudyId());
                            anchorDateSchedulingDetails.setParticipantId(studies.getParticipantId());
                            anchorDateSchedulingDetails.setTargetActivityId(activityListData.getActivities().get(i).getActivityId());

                            for (int j = 0; j < activityDataDB.getActivities().size(); j++)
                                if (activityDataDB.getActivities().get(j).getActivityId().equalsIgnoreCase(anchorDateSchedulingDetails.getSourceActivityId())) {
                                    anchorDateSchedulingDetails.setActivityState(activityDataDB.getActivities().get(j).getStatus());
                                    mArrayList.add(anchorDateSchedulingDetails);
                                    break;
                                }
                        } else {
                            // For enrollmentDate
                            anchorDateSchedulingDetails = new AnchorDateSchedulingDetails();
                            anchorDateSchedulingDetails.setSchedulingType(activityListData.getActivities().get(i).getSchedulingType());
                            anchorDateSchedulingDetails.setSourceType(activityListData.getActivities().get(i).getAnchorDate().getSourceType());
                            anchorDateSchedulingDetails.setStudyId(((SurveyActivity) mContext).getStudyId());
                            anchorDateSchedulingDetails.setParticipantId(studies.getParticipantId());
                            anchorDateSchedulingDetails.setTargetActivityId(activityListData.getActivities().get(i).getActivityId());
                            anchorDateSchedulingDetails.setAnchorDate(studies.getEnrolledDate());
                            mArrayList.add(anchorDateSchedulingDetails);
                        }
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

    private void metadataProcess() {

        for (int i = 0; i < mArrayList.size(); i++) {
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        SimpleDateFormat dateSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timezoneSimpleDateFormat = new SimpleDateFormat("Z");
        Date date;


        ArrayList<String> activityIds = new ArrayList<>();
        ArrayList<String> runIds = new ArrayList<>();


        if (activityListData != null)
            for (int i = 0; i < activityListData.getActivities().size(); i++) {
                if (activityListData.getActivities().get(i).getSchedulingType() != null) {
                    if (activityListData.getActivities().get(i).getSchedulingType().equalsIgnoreCase("AnchorDate")) {
                        for (int j = 0; j < mArrayList.size(); j++) {
                            if (activityListData.getActivities().get(i).getActivityId().equalsIgnoreCase(mArrayList.get(j).getTargetActivityId())) {
                                if (mArrayList.get(j).getAnchorDate() != null && !mArrayList.get(j).getAnchorDate().equalsIgnoreCase("")) {
                                    String startTime = "";
                                    String endTime = "";
                                    if (activityListData.getActivities().get(i).getAnchorDate() != null && activityListData.getActivities().get(i).getAnchorDate().getStart() != null) {
                                        if (!activityListData.getActivities().get(i).getAnchorDate().getStart().getTime().equalsIgnoreCase("")) {
                                            startTime = activityListData.getActivities().get(i).getAnchorDate().getStart().getTime();
                                        } else {
                                            startTime = "00:00:00";
                                        }
                                    }
                                    if (activityListData.getActivities().get(i).getAnchorDate() != null && activityListData.getActivities().get(i).getAnchorDate().getEnd() != null) {
                                        if (!activityListData.getActivities().get(i).getAnchorDate().getEnd().getTime().equalsIgnoreCase("")) {
                                            endTime = activityListData.getActivities().get(i).getAnchorDate().getEnd().getTime();
                                        } else {
                                            endTime = "23:59:59";
                                        }
                                    }


                                    // to do run calculation and expecting source question has answered
                                    RealmResults<ActivityRun> runs = dbServiceSubscriber.getAllActivityRunFromDB(activityListData.getStudyId(), activityListData.getActivities().get(i).getActivityId(), mRealm);
                                    if (runs == null || runs.size() == 0) {
                                        mActivityUpdated = true;
                                        activityIds.add(activityListData.getActivities().get(i).getActivityId());
                                        runIds.add("-1");
                                    }


                                    if (activityListData.getActivities().get(i).getFrequency().getType().equalsIgnoreCase("One Time")) {
                                        Calendar calendar;
                                        if (activityListData.getActivities().get(i).getAnchorDate() != null && activityListData.getActivities().get(i).getAnchorDate().getStart() != null) {
                                            calendar = Calendar.getInstance();
                                            try {
                                                date = simpleDateFormat.parse(mArrayList.get(j).getAnchorDate());
                                                calendar.setTime(date);
                                                calendar.add(Calendar.DATE, activityListData.getActivities().get(i).getAnchorDate().getStart().getAnchorDays());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            activityListData.getActivities().get(i).setStartTime(dateSimpleDateFormat.format(calendar.getTime()) + "T" + startTime + ".000" + timezoneSimpleDateFormat.format(calendar.getTime()));
                                        }
                                        if (activityListData.getActivities().get(i).getAnchorDate() != null && activityListData.getActivities().get(i).getAnchorDate().getEnd() != null) {
                                            calendar = Calendar.getInstance();
                                            try {
                                                date = simpleDateFormat.parse(mArrayList.get(j).getAnchorDate());
                                                calendar.setTime(date);
                                                calendar.add(Calendar.DATE, activityListData.getActivities().get(i).getAnchorDate().getEnd().getAnchorDays());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            activityListData.getActivities().get(i).setEndTime(dateSimpleDateFormat.format(calendar.getTime()) + "T" + endTime + ".000" + timezoneSimpleDateFormat.format(calendar.getTime()));
                                        }
                                    } else if (activityListData.getActivities().get(i).getFrequency().getType().equalsIgnoreCase("Daily")) {
                                        if (activityListData.getActivities().get(i).getStartTime().equalsIgnoreCase("")) {
                                            Calendar calendar = Calendar.getInstance();
                                            try {
                                                date = simpleDateFormat.parse(mArrayList.get(j).getAnchorDate());
                                                calendar.setTime(date);
                                                calendar.add(Calendar.DATE, activityListData.getActivities().get(i).getAnchorDate().getStart().getAnchorDays());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            activityListData.getActivities().get(i).setStartTime(dateSimpleDateFormat.format(calendar.getTime()) + "T" + startTime + ".000" + timezoneSimpleDateFormat.format(calendar.getTime()));
                                        }
                                        if (activityListData.getActivities().get(i).getEndTime().equalsIgnoreCase("")) {
                                            Calendar calendar = Calendar.getInstance();
                                            try {
                                                calendar.setTime(simpleDateFormat.parse(activityListData.getActivities().get(i).getStartTime()));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            calendar.add(Calendar.DATE, activityListData.getActivities().get(i).getAnchorDate().getEnd().getRepeatInterval());
                                            activityListData.getActivities().get(i).setEndTime(dateSimpleDateFormat.format(calendar.getTime()) + "T" + endTime + ".000" + timezoneSimpleDateFormat.format(calendar.getTime()));
                                        }
                                    } else if (activityListData.getActivities().get(i).getFrequency().getType().equalsIgnoreCase("Weekly")) {
                                        if (activityListData.getActivities().get(i).getStartTime().equalsIgnoreCase("")) {
                                            Calendar calendar = Calendar.getInstance();
                                            try {
                                                date = simpleDateFormat.parse(mArrayList.get(j).getAnchorDate());
                                                calendar.setTime(date);
                                                calendar.add(Calendar.DATE, activityListData.getActivities().get(i).getAnchorDate().getStart().getAnchorDays());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }


                                            activityListData.getActivities().get(i).setStartTime(dateSimpleDateFormat.format(calendar.getTime()) + "T" + startTime + ".000" + timezoneSimpleDateFormat.format(calendar.getTime()));
                                        }
                                        if (activityListData.getActivities().get(i).getEndTime().equalsIgnoreCase("")) {
                                            Calendar calendar = Calendar.getInstance();
                                            try {
                                                calendar.setTime(simpleDateFormat.parse(activityListData.getActivities().get(i).getStartTime()));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            calendar.add(Calendar.WEEK_OF_YEAR, activityListData.getActivities().get(i).getAnchorDate().getEnd().getRepeatInterval());
                                            activityListData.getActivities().get(i).setEndTime(dateSimpleDateFormat.format(calendar.getTime()) + "T" + endTime + ".000" + timezoneSimpleDateFormat.format(calendar.getTime()));
                                        }
                                    } else if (activityListData.getActivities().get(i).getFrequency().getType().equalsIgnoreCase("Monthly")) {
                                        if (activityListData.getActivities().get(i).getStartTime().equalsIgnoreCase("")) {
                                            Calendar calendar = Calendar.getInstance();
                                            try {
                                                date = simpleDateFormat.parse(mArrayList.get(j).getAnchorDate());
                                                calendar.setTime(date);
                                                calendar.add(Calendar.DATE, activityListData.getActivities().get(i).getAnchorDate().getStart().getAnchorDays());
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            activityListData.getActivities().get(i).setStartTime(dateSimpleDateFormat.format(calendar.getTime()) + "T" + startTime + ".000" + timezoneSimpleDateFormat.format(calendar.getTime()));
                                        }
                                        if (activityListData.getActivities().get(i).getEndTime().equalsIgnoreCase("")) {
                                            Calendar calendar = Calendar.getInstance();
                                            try {
                                                calendar.setTime(simpleDateFormat.parse(activityListData.getActivities().get(i).getStartTime()));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            calendar.add(Calendar.MONTH, activityListData.getActivities().get(i).getAnchorDate().getEnd().getRepeatInterval());
                                            activityListData.getActivities().get(i).setEndTime(dateSimpleDateFormat.format(calendar.getTime()) + "T" + endTime + ".000" + timezoneSimpleDateFormat.format(calendar.getTime()));
                                        }
                                    } else {
                                        //custom runs
                                        if (activityListData.getActivities().get(i).getStartTime().equalsIgnoreCase("") && activityListData.getActivities().get(i).getEndTime().equalsIgnoreCase("")) {
                                            Calendar startCalendar;
                                            Calendar endCalendar;
                                            for (int k = 0; k < activityListData.getActivities().get(i).getFrequency().getAnchorRuns().size(); k++) {
                                                startCalendar = Calendar.getInstance();
                                                endCalendar = Calendar.getInstance();

                                                //start runs
                                                try {
                                                    date = simpleDateFormat.parse(mArrayList.get(j).getAnchorDate());
                                                    startCalendar.setTime(date);
                                                    startCalendar.add(Calendar.DATE, activityListData.getActivities().get(i).getFrequency().getAnchorRuns().get(k).getStartDays());
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                activityListData.getActivities().get(i).getFrequency().getRuns().get(k).setStartTime(dateSimpleDateFormat.format(startCalendar.getTime()) + "T" + startTime + ".000" + timezoneSimpleDateFormat.format(startCalendar.getTime()));

                                                //end runs
                                                try {
                                                    date = simpleDateFormat.parse(mArrayList.get(j).getAnchorDate());
                                                    endCalendar.setTime(date);
                                                    endCalendar.add(Calendar.DATE, activityListData.getActivities().get(i).getFrequency().getAnchorRuns().get(k).getEndDays());
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                activityListData.getActivities().get(i).getFrequency().getRuns().get(k).setEndTime(dateSimpleDateFormat.format(endCalendar.getTime()) + "T" + endTime + ".000" + timezoneSimpleDateFormat.format(endCalendar.getTime()));

                                                activityListData.getActivities().get(i).setStartTime(activityListData.getActivities().get(i).getFrequency().getRuns().get(0).getStartTime());
                                                activityListData.getActivities().get(i).setEndTime(activityListData.getActivities().get(i).getFrequency().getRuns().get(k).getEndTime());


                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


//            boolean updateRun = true;
        // If any activities available in Db we take from Db otherwise from Webservice

        StudyUpdate studyUpdate = dbServiceSubscriber.getStudyUpdateById(((SurveyActivity) mContext).getStudyId(), mRealm);


        // find any updates on available activity



        if (activityDataDB != null && activityListData != null) { //&& activityData != null
            for (int j = 0; j < activityListData.getActivities().size(); j++) {
                boolean activityAvailable = false;
                 for (int i = 0; i < activityDataDB.getActivities().size(); i++) {

                    if (activityDataDB.getActivities().get(i).getActivityId().equalsIgnoreCase(activityListData.getActivities().get(j).getActivityId())) {
                        activityAvailable = true;
                        if (!activityDataDB.getActivities().get(i).getActivityVersion().equalsIgnoreCase(activityListData.getActivities().get(j).getActivityVersion())) {
                            mActivityUpdated = true;
                            //update ActivityWS DB with new version
                            dbServiceSubscriber.UpdateActivitiesWSVersion(activityListData.getActivities().get(j).getActivityId(), activityListData.getStudyId(), mRealm, activityListData.getActivities().get(j).getActivityVersion());
                            dbServiceSubscriber.updateActivityPreferenceVersion(mContext,activityListData.getActivities().get(j).getActivityVersion(), activityDataDB.getActivities().get(i));
                            if (activityIds.contains(activityDataDB.getActivities().get(i).getActivityId())) {
                                //change on 15/10/2019

                                runIds.set(activityIds.indexOf(activityDataDB.getActivities().get(i).getActivityId()), activityDataDB.getActivities().get(i).getActivityRunId());
                            } else {
                                //change on 15/10/2019
                                activityIds.add(activityDataDB.getActivities().get(i).getActivityId());

                                runIds.add(activityDataDB.getActivities().get(i).getActivityRunId());
                            }
                        }
                        break;
                    }
                }
                //change on 16/10/2019
                 if(!activityAvailable) {
                     ActivitiesWS activitiesWS = dbServiceSubscriber.getActivityObj(activityListData.getActivities().get(j).getActivityId(), activityListData.getStudyId(), mRealm);
                     if (activitiesWS != null && !activitiesWS.getActivityVersion().equalsIgnoreCase(activityListData.getActivities().get(j).getActivityVersion())) {
                         mActivityUpdated = true;
                         //update ActivityWS DB with new version
                         dbServiceSubscriber.UpdateActivitiesWSVersion(activityListData.getActivities().get(j).getActivityId(), activityListData.getStudyId(), mRealm, activityListData.getActivities().get(j).getActivityVersion());
                         if (!activityIds.contains(activityListData.getActivities().get(j).getActivityId())) {

                             activityIds.add(activityListData.getActivities().get(j).getActivityId());

                             runIds.add("-1");
                         }
                     }
                 }
            }
        }
        //change on 15/10/2019
        else if(activityDataDB == null && activityListData != null)
        {
            for (int j = 0; j < activityListData.getActivities().size(); j++) {
                ActivitiesWS activitiesWS = dbServiceSubscriber.getActivityObj(activityListData.getActivities().get(j).getActivityId(), activityListData.getStudyId(), mRealm);
                    if (activitiesWS != null && !activitiesWS.getActivityVersion().equalsIgnoreCase(activityListData.getActivities().get(j).getActivityVersion())) {
                        mActivityUpdated = true;
                        //update ActivityWS DB with new version
                        dbServiceSubscriber.UpdateActivitiesWSVersion(activityListData.getActivities().get(j).getActivityId(), activityListData.getStudyId(), mRealm, activityListData.getActivities().get(j).getActivityVersion());
                        if (!activityIds.contains(activityListData.getActivities().get(j).getActivityId())) {

                            activityIds.add(activityListData.getActivities().get(j).getActivityId());

                            runIds.add("-1");
                        }
                    }
            }
        }
//            }


        displayData(activityListData, activityIds, runIds, null);
    }


    private void mGetResourceListWebservice() {

        HashMap<String, String> header = new HashMap<>();
        String studyId = ((SurveyActivity) mContext).getStudyId();
        header.put("studyId", studyId);
        String url = URLs.RESOURCE_LIST + "?studyId=" + studyId;
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
        String url = URLs.STUDY_INFO + "?studyId=" + studyId;
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
                if (studyVideoAdapter.mStatus.get(position).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT) && (studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS) || studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.YET_To_START))) {
                    if (studyVideoAdapter.mCurrentRunStatusForActivities.get(position).isRunIdAvailable()) {
                        getActivityInfo(studyVideoAdapter.items.get(position).getActivityId(), studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getCurrentRunId(), studyVideoAdapter.mCurrentRunStatusForActivities.get(position).getStatus(), studyVideoAdapter.items.get(position).getBranching(), studyVideoAdapter.items.get(position).getActivityVersion(), studyVideoAdapter.mCurrentRunStatusForActivities.get(position), studyVideoAdapter.items.get(position));
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
            mRealm = AppController.getRealmobj(mContext);

            try {
                currentactivityList.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                upcomingactivityList.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                completedactivityList.clear();
            } catch (Exception e) {
                e.printStackTrace();
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
                                        dbServiceSubscriber.deleteAllRun(mContext,activityRuns);
                                    } catch (Exception e) {
                                        e.printStackTrace();
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
                    insertAndUpdateToDB(mContext,activityListData);
                    activityListData2 = activityListData;
                }
            }


            if (activityListData2 != null) {
                activitiesArrayList.addAll(activityListData2.getActivities());
                SurvayScheduler survayScheduler = new SurvayScheduler(dbServiceSubscriber, mRealm);
                StudyData studyPreferences = dbServiceSubscriber.getStudyPreference(mRealm);
                ActivityData activityData = dbServiceSubscriber.getActivityPreference(((SurveyActivity) mContext).getStudyId(), mRealm);

                Date joiningDate = survayScheduler.getJoiningDateOfStudy(studyPreferences, ((SurveyActivity) mContext).getStudyId());

                Date currentDate = new Date();

                if (mActivityUpdated) {
                    dbServiceSubscriber.deleteMotivationalNotification(mContext,((SurveyActivity) mContext).getStudyId());
                }

                if (newlyAdded.size() > 0) {
                    // insert to activitylist db
                    //activityListDataDB
                    for (int k = 0; k < newlyAdded.size(); k++) {
                        dbServiceSubscriber.addActivityWSList(mContext,activityListDataDB, newlyAdded.get(k));
                    }
                }

                for (int i = 0; i < activitiesArrayList.size(); i++) {
                    SimpleDateFormat simpleDateFormat = AppController.getDateFormatUTC1();
                    Date starttime = null, endtime = null;
                    if (activitiesArrayList.get(i) != null && activitiesArrayList.get(i).getSchedulingType() != null && activitiesArrayList.get(i).getSchedulingType().equalsIgnoreCase("AnchorDate")) {

                        if (!activitiesArrayList.get(i).getStartTime().equalsIgnoreCase("")) {
                            if ((activitiesArrayList.get(i).getEndTime().equalsIgnoreCase("") && activitiesArrayList.get(i).getAnchorDate() != null && activitiesArrayList.get(i).getAnchorDate().getEnd() != null) || !activitiesArrayList.get(i).getEndTime().equalsIgnoreCase("")) {
                                try {
                                    starttime = simpleDateFormat.parse(activitiesArrayList.get(i).getStartTime().split("\\.")[0]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    endtime = simpleDateFormat.parse(activitiesArrayList.get(i).getEndTime().split("\\.")[0]);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    } else {
                        try {
                            if (activitiesArrayList.get(i).getStartTime().equalsIgnoreCase("")) {
                                starttime = new Date();
                            } else {
                                starttime = simpleDateFormat.parse(activitiesArrayList.get(i).getStartTime().split("\\.")[0]);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            endtime = simpleDateFormat.parse(activitiesArrayList.get(i).getEndTime().split("\\.")[0]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                    RealmResults<ActivityRun> activityRuns = dbServiceSubscriber.getAllActivityRunFromDB(((SurveyActivity) mContext).getStudyId(), activitiesArrayList.get(i).getActivityId(), mRealm);

                    boolean deleted = false;
                    for (int j = 0; j < activitiesWSesDeleted.size(); j++) {
                        if (activitiesWSesDeleted.get(j).getActivityId().equalsIgnoreCase(activitiesArrayList.get(i).getActivityId())) {
                            deleted = true;
                            try {
                                dbServiceSubscriber.deleteAllRun(mContext,activityRuns);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (updateRun || activityRuns == null || activityRuns.size() == 0) {
                        if (!deleted)
                            survayScheduler.setRuns(activitiesArrayList.get(i), ((SurveyActivity) mContext).getStudyId(), starttime, endtime, joiningDate, mContext);
                    } else if (activityIds.size() > 0) {
                        // remove runs for these Ids and set runs once again
                        if (activityIds.contains(activitiesArrayList.get(i).getActivityId())) {
                            dbServiceSubscriber.deleteActivityRunsFromDb(mContext,activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                            if (!deleted)
                                survayScheduler.setRuns(activitiesArrayList.get(i), ((SurveyActivity) mContext).getStudyId(), starttime, endtime, joiningDate, mContext);
                            // delete activity object that used for survey
                            dbServiceSubscriber.deleteActivityObjectFromDb(mContext,activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                            for (int j = 0; j < activityData.getActivities().size(); j++) {
                                if (activitiesArrayList.get(i).getActivityId().equalsIgnoreCase(activityData.getActivities().get(j).getActivityId())) {
                                    if (!activityData.getActivities().get(j).getStatus().equalsIgnoreCase(YET_To_START)) {
                                        // Delete response data
                                        if (!runIds.get(activityIds.indexOf(activitiesArrayList.get(i).getActivityId())).equalsIgnoreCase("-1")) {
                                            dbServiceSubscriber.deleteResponseDataFromDb(mContext,((SurveyActivity) mContext).getStudyId() + "_STUDYID_" + activitiesArrayList.get(i).getActivityId() + "_" + runIds.get(activityIds.indexOf(activitiesArrayList.get(i).getActivityId())));
                                        }
                                    }
                                }
                            }

                        }
                    }


                    String currentDateString = AppController.getDateFormatUTC().format(currentDate);
                    try {
                        currentDate = AppController.getDateFormatUTC().parse(currentDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
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
                            if (starttime != null) {
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
                            }
                        } else {
                            NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                            try {
                                notificationModuleSubscriber.cancleActivityLocalNotificationByIds(mContext, activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                notificationModuleSubscriber.cancleResourcesLocalNotificationByIds(mContext, activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                        try {
                            notificationModuleSubscriber.cancleActivityLocalNotificationByIds(mContext, activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            notificationModuleSubscriber.cancleResourcesLocalNotificationByIds(mContext, activitiesArrayList.get(i).getActivityId(), ((SurveyActivity) mContext).getStudyId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //delete from activity list db
                        dbServiceSubscriber.deleteActivityWSList(mContext,activityListDataDB, activitiesArrayList.get(i).getActivityId());
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
                        e.printStackTrace();
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
                if (currentActivityStatus.get(i).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.YET_To_START) || currentActivityStatus.get(i).getStatus().equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS)) {
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
                e.printStackTrace();
            }
            try {
                currentActivityStatus.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                currentStatus.clear();
            } catch (Exception e) {
                e.printStackTrace();
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
                        e.printStackTrace();
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
                        e.printStackTrace();
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

            activityListDataDB = null;


            dbServiceSubscriber.closeRealmObj(mRealm);
            return activitiesArrayList1;
        }

        @Override
        protected void onPostExecute(ArrayList<ActivitiesWS> result) {

            mRealm = AppController.getRealmobj(mContext);

            mSurveyActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mSurveyActivitiesRecyclerView.setAdapter(studyVideoAdapter);

            AppController.getHelperSharedPreference().writePreference(mContext, mContext.getResources().getString(R.string.completedRuns), "" + completed);
            AppController.getHelperSharedPreference().writePreference(mContext, mContext.getResources().getString(R.string.missedRuns), "" + missed);
            AppController.getHelperSharedPreference().writePreference(mContext, mContext.getResources().getString(R.string.totalRuns), "" + total);

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

                } else if (missed > 0) {
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.missed_activity) + " " + ((SurveyActivity) mContext).getTitle1() + " " + mContext.getResources().getString(R.string.we_encourage), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));

                }
            } else if (!motivationalNotification.isFiftyPc() && !motivationalNotification.isHundredPc()) {
                if (completion >= 100) {
                    hundredPc = true;
                    SetDialogHelper.setNeutralDialog(mContext, mContext.getResources().getString(R.string.study) + " " + title + " " + mContext.getResources().getString(R.string.percent_complete1), false, mContext.getResources().getString(R.string.ok), mContext.getResources().getString(R.string.app_name));
                } else if (completion >= 50) {
                    fiftyPc = true;
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
            dbServiceSubscriber.saveMotivationalNotificationToDB(mContext,motivationalNotification1);

            dbServiceSubscriber.closeRealmObj(mRealm);
            double adherence = 0;
            if (((double) completed + (double) missed + 1d) > 0)
                adherence = (((double) completed + 1d) / ((double) completed + (double) missed + 1d)) * 100d;

            updateStudyState("" + (int) completion, "" + (int) adherence);

        }

        @Override
        protected void onPreExecute() {
        }

    }

    public void updateStudyState(String completion, String adherence) {
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        JSONArray studieslist = new JSONArray();
        JSONObject studiestatus = new JSONObject();
        try {
            studiestatus.put("studyId", ((SurveyActivity) mContext).getStudyId());
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
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_STUDY_PREFERENCE, UPDATE_STUDY_PREFERENCE, mContext, LoginData.class, null, header, jsonObject, false, this);

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


    private <E> void insertAndUpdateToDB(Context context,E e) {
        DatabaseEvent databaseEvent = new DatabaseEvent();
        databaseEvent.setE(e);
        databaseEvent.setmType(DBServiceSubscriber.TYPE_COPY_UPDATE);
        databaseEvent.setaClass(EligibilityConsent.class);
        databaseEvent.setmOperation(DBServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
        dbServiceSubscriber.insert(context,databaseEvent);
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

    boolean checkbefore(Date starttime) {
        return new Date().before(starttime);
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
                calculateStartAnsEndDateForActivities();
            } else if (responseCode == ACTIVTTYINFO_RESPONSECODE) {
                onItemsLoadComplete();
                AppController.getHelperProgressDialog().dismissDialog();
                launchSurvey(null);
            } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
                onItemsLoadComplete();
                AppController.getHelperProgressDialog().dismissDialog();
                dbServiceSubscriber.updateActivityPreferenceDB(mContext,mActivityId, ((SurveyActivity) mContext).getStudyId(), mCurrentRunId, SurveyActivitiesFragment.IN_PROGRESS, mActivityStatusData.getTotalRun(), mActivityStatusData.getCompletedRun(), mActivityStatusData.getMissedRun(), mActivityVersion);
                launchSurvey(null);
            } else {
                try {
                    onItemsLoadComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    AppController.getHelperProgressDialog().dismissDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getActivityInfo(String activityId, int currentRunId, String status, boolean branching, String activityVersion, ActivityStatus activityStatus, ActivitiesWS activitiesWS) {
        mCurrentRunId = currentRunId;
        mActivityStatus = status;
        mActivityStatusData = activityStatus;
        mActivityId = activityId;
        mBranching = branching;
        mActivityVersion = activityVersion;
        selectedActivity = activitiesWS;
        if (status.equalsIgnoreCase(YET_To_START)) {
            updatePreferenceToDB = true;
            updateUserPreference(((SurveyActivity) mContext).getStudyId(), status, activityId, mCurrentRunId);
        } else {
            updatePreferenceToDB = false;
            updateActivityInfo(activityId);
        }
    }

    private void updateActivityInfo(String activityId) {
        AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);

        GetActivityInfoEvent getActivityInfoEvent = new GetActivityInfoEvent();
        HashMap<String, String> header = new HashMap();
        String url = URLs.ACTIVITY + "?studyId=" + ((SurveyActivity) mContext).getStudyId() + "&activityId=" + activityId + "&activityVersion=" + mActivityVersion;
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
                dbServiceSubscriber.saveActivity(mContext,mActivityObj);
            }

            if (mActivityObj != null) {
                AppController.getHelperSharedPreference().writePreference(mContext, getString(R.string.mapCount), "0");
                stepsBuilder = new StepsBuilder(mContext, mActivityObj, mBranching, mRealm);
                mTask = ActivityBuilder.create(mContext,((SurveyActivity) mContext).getStudyId() + "_STUDYID_" + mActivityObj.getSurveyId() + "_" + mCurrentRunId, stepsBuilder.getsteps(), mActivityObj, mBranching, dbServiceSubscriber);
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
            e.printStackTrace();
        }
    }

    private void startsurvey() {

        Intent intent = CustomSurveyViewTaskActivity.newIntent(mContext, ((SurveyActivity) mContext).getStudyId() + "_STUDYID_" + mActivityObj.getSurveyId() + "_" + mCurrentRunId, ((SurveyActivity) mContext).getStudyId(), mCurrentRunId, mActivityStatus, mActivityStatusData.getMissedRun(), mActivityStatusData.getCompletedRun(), mActivityStatusData.getTotalRun(), mActivityVersion, mActivityStatusData.getCurrentRunStartDate(), mActivityStatusData.getCurrentRunEndDate(), mActivityObj.getSurveyId(), mBranching);
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
        header.put("userId", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        JSONArray activitylist = new JSONArray();
        JSONObject activityStatus = new JSONObject();
        JSONObject activityRun = new JSONObject();
        try {
            activityStatus.put("studyId", studyId);
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
            e.printStackTrace();
        }

        activitylist.put(activityStatus);

        try {
            jsonObject.put("studyId", studyId);
            jsonObject.put("activity", activitylist);
        } catch (JSONException e) {
            e.printStackTrace();
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


            mDeleteIndexNumberDB = number;
            AppController.pendingService(mContext,number, "post_object", URLs.UPDATE_ACTIVITY_PREFERENCE, "", jsonObject.toString(), "registration", "", "", studyIdActivityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //////////

        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_ACTIVITY_PREFERENCE, UPDATE_USERPREFERENCE_RESPONSECODE, mContext, LoginData.class, null, header, jsonObject, false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(mContext, R.string.current_locationwillnot_used, Toast.LENGTH_SHORT).show();
                }
                startsurvey();
                break;
        }
    }

    @Override
    public void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
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
