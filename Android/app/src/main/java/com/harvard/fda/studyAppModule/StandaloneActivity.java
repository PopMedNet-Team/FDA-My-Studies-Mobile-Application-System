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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.harvard.fda.AppConfig;
import com.harvard.fda.MyFirebaseMessagingService;
import com.harvard.fda.R;
import com.harvard.fda.notificationModule.AlarmReceiver;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.storageModule.events.DatabaseEvent;
import com.harvard.fda.studyAppModule.consent.ConsentBuilder;
import com.harvard.fda.studyAppModule.consent.CustomConsentViewTaskActivity;
import com.harvard.fda.studyAppModule.consent.model.Consent;
import com.harvard.fda.studyAppModule.consent.model.CorrectAnswerString;
import com.harvard.fda.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.fda.studyAppModule.events.ConsentPDFEvent;
import com.harvard.fda.studyAppModule.events.GetUserStudyInfoEvent;
import com.harvard.fda.studyAppModule.events.GetUserStudyListEvent;
import com.harvard.fda.studyAppModule.studyModel.ConsentDocumentData;
import com.harvard.fda.studyAppModule.studyModel.ConsentPDF;
import com.harvard.fda.studyAppModule.studyModel.Study;
import com.harvard.fda.studyAppModule.studyModel.StudyList;
import com.harvard.fda.studyAppModule.studyModel.StudyUpdate;
import com.harvard.fda.studyAppModule.studyModel.StudyUpdateListdata;
import com.harvard.fda.studyAppModule.survayScheduler.SurvayScheduler;
import com.harvard.fda.studyAppModule.survayScheduler.model.CompletionAdeherenceCalc;
import com.harvard.fda.userModule.UserModulePresenter;
import com.harvard.fda.userModule.event.GetPreferenceEvent;
import com.harvard.fda.userModule.webserviceModel.Studies;
import com.harvard.fda.userModule.webserviceModel.StudyData;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.apiHelper.ConnectionDetector;
import com.harvard.fda.webserviceModule.apiHelper.HttpRequest;
import com.harvard.fda.webserviceModule.apiHelper.Responsemodel;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.fda.webserviceModule.events.WCPConfigEvent;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class StandaloneActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

    private final int SPECIFIC_STUDY = 10;
    private final int GET_PREFERENCES = 11;
    public static final int GET_CONSENT_DOC = 204;
    private static final int STUDY_UPDATES = 201;
    private static final int CONSENTPDF = 206;
    private static final int CONSENT_RESPONSECODE = 203;
    public static final String CONSENT = "consent";
    DBServiceSubscriber dbServiceSubscriber;
    Realm realm;
    RealmList<StudyList> studyListArrayList;

    public static final String YET_TO_JOIN = "yetToJoin";
    public static final String IN_PROGRESS = "inProgress";
    public static final String COMPLETED = "completed";
    public static final String NOT_ELIGIBLE = "notEligible";
    public static final String WITHDRAWN = "withdrawn";


    public static final String ACTIVE = "active";
    public static final String UPCOMING = "upcoming";
    public static final String PAUSED = "paused";
    public static final String CLOSED = "closed";

    String signatureBase64 = "";
    String signatureDate = "";
    String firstName = "";
    String lastName = "";
    String eligibilityType = "";
    private String mCalledFor = "";
    private String mFrom = "";
    private String mActivityId;
    private String mLocalNotification;
    private String mLatestConsentVersion = "0";

    private ArrayList<CompletionAdeherenceCalc> completionAdeherenceCalcs = new ArrayList<>();

    private static File myFile, file, encryptFile;
    private static final String FILE_FOLDER = "FDA_PDF";
    private String mFileName;
    public static String FROM = "from";

    String mtitle;
    private String mStudyId;
    Study mStudy;
    private EligibilityConsent eligibilityConsent;
    private String intentFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standalone);
        mStudyId = AppConfig.StudyId;

        if (getIntent().getStringExtra(FROM) != null) {
            intentFrom = getIntent().getStringExtra(FROM);
        } else {
            intentFrom = "";
        }

        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_standalone))) {
            if (!AppController.getHelperSharedPreference().readPreference(StandaloneActivity.this, getResources().getString(R.string.userid), "").equalsIgnoreCase("")) {

                dbServiceSubscriber = new DBServiceSubscriber();
                realm = AppController.getRealmobj(this);
                studyListArrayList = new RealmList<>();

                AppController.getHelperProgressDialog().showProgress(StandaloneActivity.this, "", "", false);
                GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
                HashMap<String, String> header = new HashMap();
                HashMap<String, String> params = new HashMap();
                params.put("studyId", AppConfig.StudyId);
                WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", URLs.SPECIFIC_STUDY + "?studyId=" + AppConfig.StudyId, SPECIFIC_STUDY, StandaloneActivity.this, Study.class, params, header, null, false, this);

                getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
                StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
                studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
            } else {
                Intent intent = new Intent(StandaloneActivity.this, StandaloneStudyInfoActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                finish();
            }
        } else {
            Intent intent = new Intent(StandaloneActivity.this, StudyActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(cn);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == SPECIFIC_STUDY) {
            if (response != null) {
                mStudy = (Study) response;
                for (int i = 0; i < mStudy.getStudies().size(); i++) {
                    if (mStudy.getStudies().get(i).getStudyId().equalsIgnoreCase(AppConfig.StudyId)) {
                        studyListArrayList.add(mStudy.getStudies().get(i));
                        mStudy.setStudies(studyListArrayList);
                    }
                }
                AppController.getHelperProgressDialog().dismissDialog();
                if (!studyListArrayList.isEmpty()) {
                    if (studyListArrayList.get(0).getStatus().equalsIgnoreCase(ACTIVE)) {
                        AppController.getHelperProgressDialog().showProgress(StandaloneActivity.this, "", "", false);

                        dbServiceSubscriber.saveStudyListToDB(this,mStudy);
                        GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
                        HashMap<String, String> header = new HashMap();
                        header.put("auth", AppController.getHelperSharedPreference().readPreference(StandaloneActivity.this, getResources().getString(R.string.auth), ""));
                        header.put("userId", AppController.getHelperSharedPreference().readPreference(StandaloneActivity.this, getResources().getString(R.string.userid), ""));
                        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", URLs.STUDY_STATE, GET_PREFERENCES, StandaloneActivity.this, StudyData.class, null, header, null, false, this);

                        getPreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
                        UserModulePresenter userModulePresenter = new UserModulePresenter();
                        userModulePresenter.performGetUserPreference(getPreferenceEvent);
                    } else {
                        Toast.makeText(this, "This study is " + studyListArrayList.get(0).getStatus(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Study not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(StandaloneActivity.this, R.string.error_retriving_data, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (responseCode == GET_PREFERENCES) {
            StudyData studies = (StudyData) response;
            boolean userAlreadyJoined = false;
            if (studies != null) {
                studies.setUserId(AppController.getHelperSharedPreference().readPreference(StandaloneActivity.this, getString(R.string.userid), ""));

                StudyData studyData = dbServiceSubscriber.getStudyPreferencesListFromDB(realm);
                if (studyData == null) {
                    int size = studies.getStudies().size();
                    for (int i = 0; i < size; i++) {
                        if (!studies.getStudies().get(i).getStudyId().equalsIgnoreCase(AppConfig.StudyId)) {
                            studies.getStudies().remove(i);
                            size = size - 1;
                            i--;
                        }
                    }
                    dbServiceSubscriber.saveStudyPreferencesToDB(this,studies);
                } else {
                    studies = studyData;
                }

                AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.title), "" + studyListArrayList.get(0).getTitle());
                AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.bookmark), "" + studyListArrayList.get(0).isBookmarked());
                AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.status), "" + studyListArrayList.get(0).getStatus());
                AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.studyStatus), "" + studyListArrayList.get(0).getStudyStatus());
                AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.position), "" + 0);
                AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.enroll), "" + studyListArrayList.get(0).getSetting().isEnrolling());
                AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.rejoin), "" + studyListArrayList.get(0).getSetting().getRejoin());
                AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.studyVersion), "" + studyListArrayList.get(0).getStudyVersion());


                RealmList<Studies> userPreferenceStudies = studies.getStudies();
                if (userPreferenceStudies != null) {
                    for (int i = 0; i < userPreferenceStudies.size(); i++) {
                        for (int j = 0; j < studyListArrayList.size(); j++) {
                            if (userPreferenceStudies.get(i).getStudyId().equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
                                studyListArrayList.get(j).setBookmarked(userPreferenceStudies.get(i).isBookmarked());
                                studyListArrayList.get(j).setStudyStatus(userPreferenceStudies.get(i).getStatus());
                                userAlreadyJoined = true;


                                if (studyListArrayList.get(0).getStudyStatus().equalsIgnoreCase(IN_PROGRESS)) {
                                    getStudyUpdate(studyListArrayList.get(j).getStudyId(), studyListArrayList.get(j).getStudyVersion(), studyListArrayList.get(j).getTitle(), "", "", "", "");
                                } else {
                                    Intent intent = new Intent(StandaloneActivity.this, StudyInfoActivity.class);
                                    intent.putExtra("studyId", studyListArrayList.get(j).getStudyId());
                                    intent.putExtra("title", studyListArrayList.get(j).getTitle());
                                    intent.putExtra("bookmark", studyListArrayList.get(j).isBookmarked());
                                    intent.putExtra("status", studyListArrayList.get(j).getStatus());
                                    intent.putExtra("studyStatus", studyListArrayList.get(j).getStudyStatus());
                                    intent.putExtra("position", "0");
                                    intent.putExtra("enroll", "" + studyListArrayList.get(j).getSetting().isEnrolling());
                                    intent.putExtra("rejoin", "" + studyListArrayList.get(j).getSetting().getRejoin());
                                    startActivity(intent);
                                    finish();
                                }
                                break;
                            }
                        }
                    }
                    if (!userAlreadyJoined && !studyListArrayList.isEmpty()) {

                        studyListArrayList.get(0).setBookmarked(false);
                        studyListArrayList.get(0).setStudyStatus(YET_TO_JOIN);

                        Intent intent = new Intent(StandaloneActivity.this, StudyInfoActivity.class);
                        intent.putExtra("studyId", studyListArrayList.get(0).getStudyId());
                        intent.putExtra("title", studyListArrayList.get(0).getTitle());
                        intent.putExtra("bookmark", studyListArrayList.get(0).isBookmarked());
                        intent.putExtra("status", studyListArrayList.get(0).getStatus());
                        intent.putExtra("studyStatus", studyListArrayList.get(0).getStudyStatus());
                        intent.putExtra("position", "0");
                        intent.putExtra("enroll", "" + studyListArrayList.get(0).getSetting().isEnrolling());
                        intent.putExtra("rejoin", "" + studyListArrayList.get(0).getSetting().getRejoin());
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(StandaloneActivity.this, R.string.error_retriving_data, Toast.LENGTH_SHORT).show();
                    finish();
                }
                setStudyList(false);
                checkForNotification(getIntent());
                AppController.getHelperProgressDialog().dismissDialog();
            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(StandaloneActivity.this, R.string.error_retriving_data, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (responseCode == STUDY_UPDATES) {
            StudyUpdate studyUpdate = (StudyUpdate) response;
            studyUpdate.setStudyId(mStudyId);
            StudyUpdateListdata studyUpdateListdata = new StudyUpdateListdata();
            RealmList<StudyUpdate> studyUpdates = new RealmList<>();
            studyUpdates.add(studyUpdate);
            studyUpdateListdata.setStudyUpdates(studyUpdates);
            dbServiceSubscriber.saveStudyUpdateListdataToDB(this,studyUpdateListdata);

            if (studyUpdate.getStudyUpdateData().isResources()) {
                dbServiceSubscriber.deleteResourcesFromDb(this,mStudyId);
            }
            if (studyUpdate.getStudyUpdateData().isInfo()) {
                dbServiceSubscriber.deleteStudyInfoFromDb(this,mStudyId);
            }
            if (studyUpdate.getStudyUpdateData().isConsent()) {
                callConsentMetaDataWebservice();
            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Intent intent = new Intent(StandaloneActivity.this, SurveyActivity.class);
                intent.putExtra("studyId", mStudyId);
                intent.putExtra("to", mCalledFor);
                intent.putExtra("from", mFrom);
                intent.putExtra("activityId", mActivityId);
                intent.putExtra("localNotification", mLocalNotification);
                startActivity(intent);
                finish();
            }
        } else if (responseCode == GET_CONSENT_DOC) {
            ConsentDocumentData mConsentDocumentData = (ConsentDocumentData) response;
            mLatestConsentVersion = mConsentDocumentData.getConsent().getVersion();

            callGetConsentPDFWebservice();

        } else if (responseCode == CONSENTPDF) {
            ConsentPDF consentPDFData = (ConsentPDF) response;
            if (mLatestConsentVersion != null && consentPDFData != null && consentPDFData.getConsent() != null && consentPDFData.getConsent().getVersion() != null) {
                if (!consentPDFData.getConsent().getVersion().equalsIgnoreCase(mLatestConsentVersion)) {
                    callConsentMetaDataWebservice();
                } else {
                    AppController.getHelperProgressDialog().dismissDialog();
                    Intent intent = new Intent(StandaloneActivity.this, SurveyActivity.class);
                    intent.putExtra("studyId", mStudyId);
                    intent.putExtra("to", mCalledFor);
                    intent.putExtra("from", mFrom);
                    intent.putExtra("activityId", mActivityId);
                    intent.putExtra("localNotification", mLocalNotification);
                    startActivity(intent);
                    finish();
                }
            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Intent intent = new Intent(StandaloneActivity.this, SurveyActivity.class);
                intent.putExtra("studyId", mStudyId);
                intent.putExtra("to", mCalledFor);
                intent.putExtra("from", mFrom);
                intent.putExtra("activityId", mActivityId);
                intent.putExtra("localNotification", mLocalNotification);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(StandaloneActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(StandaloneActivity.this, errormsg);
        } else if (responseCode == STUDY_UPDATES || responseCode == GET_CONSENT_DOC || responseCode == CONSENTPDF) {
            Intent intent = new Intent(StandaloneActivity.this, SurveyActivity.class);
            intent.putExtra("studyId", mStudyId);
            intent.putExtra("to", mCalledFor);
            intent.putExtra("from", mFrom);
            intent.putExtra("activityId", mActivityId);
            intent.putExtra("localNotification", mLocalNotification);
            startActivity(intent);
            finish();
        } else {


            // offline handling
            mStudy = dbServiceSubscriber.getStudyListFromDB(realm);
            if (mStudy != null && mStudy.getStudies() != null && !mStudy.getStudies().isEmpty()) {
                studyListArrayList = mStudy.getStudies();
                studyListArrayList = dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, realm);
                setStudyList(true);

                //like click
                if (studyListArrayList.get(0).getStudyStatus().equalsIgnoreCase(IN_PROGRESS)) {
                    getStudyUpdate(studyListArrayList.get(0).getStudyId(), studyListArrayList.get(0).getStudyVersion(), studyListArrayList.get(0).getTitle(), "", "", "", "");
                } else {
                    Intent intent = new Intent(StandaloneActivity.this, StudyInfoActivity.class);
                    intent.putExtra("studyId", studyListArrayList.get(0).getStudyId());
                    intent.putExtra("title", studyListArrayList.get(0).getTitle());
                    intent.putExtra("bookmark", studyListArrayList.get(0).isBookmarked());
                    intent.putExtra("status", studyListArrayList.get(0).getStatus());
                    intent.putExtra("studyStatus", studyListArrayList.get(0).getStudyStatus());
                    intent.putExtra("position", "0");
                    intent.putExtra("enroll", "" + studyListArrayList.get(0).getSetting().isEnrolling());
                    intent.putExtra("rejoin", "" + studyListArrayList.get(0).getSetting().getRejoin());
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(StandaloneActivity.this, errormsg, Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }


    public void checkForNotification(Intent intent1) {
        if (!intentFrom.equalsIgnoreCase("")) {
            intentFrom = "";
            String type = intent1.getStringExtra(MyFirebaseMessagingService.TYPE);
            String subType = intent1.getStringExtra(MyFirebaseMessagingService.SUBTYPE);
            String studyId = intent1.getStringExtra(MyFirebaseMessagingService.STUDYID);
            String audience = intent1.getStringExtra(MyFirebaseMessagingService.AUDIENCE);

            String localNotification = "";
            if (intent1.getStringExtra(AlarmReceiver.LOCAL_NOTIFICATION) != null) {
                localNotification = intent1.getStringExtra(AlarmReceiver.LOCAL_NOTIFICATION);
            }
            String activityIdNotification = "";
            if (intent1.getStringExtra(AlarmReceiver.ACTIVITYID) != null) {
                activityIdNotification = intent1.getStringExtra(AlarmReceiver.ACTIVITYID);
            }


            if (!AppController.getHelperSharedPreference().readPreference(StandaloneActivity.this, getResources().getString(R.string.userid), "").equalsIgnoreCase("")) {
                if (type != null) {
                    if (type.equalsIgnoreCase("Gateway")) {
                        if (subType.equalsIgnoreCase("Study")) {
                            Study mStudy = dbServiceSubscriber.getStudyListFromDB(realm);
                            if (mStudy != null) {
                                RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                                studyListArrayList = dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, realm);
                                boolean isStudyAvailable = false;
                                for (int i = 0; i < studyListArrayList.size(); i++) {
                                    if (studyId.equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                                        try {
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.title), "" + studyListArrayList.get(i).getTitle());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.bookmark), "" + studyListArrayList.get(i).isBookmarked());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.status), "" + studyListArrayList.get(i).getStatus());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.studyStatus), "" + studyListArrayList.get(i).getStudyStatus());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.position), "" + i);
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.enroll), "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.rejoin), "" + studyListArrayList.get(i).getSetting().getRejoin());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.studyVersion), "" + studyListArrayList.get(i).getStudyVersion());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(getString(R.string.active)) && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                                            Intent intent = new Intent(StandaloneActivity.this, SurveyActivity.class);
                                            intent.putExtra("studyId", studyId);
                                            startActivity(intent);
                                            finish();
                                        } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(getString(R.string.paused))) {
                                            Toast.makeText(StandaloneActivity.this, R.string.study_paused, Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(getString(R.string.closed))) {
                                            Toast.makeText(StandaloneActivity.this, R.string.study_resume, Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), StudyInfoActivity.class);
                                            intent.putExtra("studyId", studyListArrayList.get(i).getStudyId());
                                            intent.putExtra("title", studyListArrayList.get(i).getTitle());
                                            intent.putExtra("bookmark", studyListArrayList.get(i).isBookmarked());
                                            intent.putExtra("status", studyListArrayList.get(i).getStatus());
                                            intent.putExtra("studyStatus", studyListArrayList.get(i).getStudyStatus());
                                            intent.putExtra("position", "" + i);
                                            intent.putExtra("enroll", "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                            intent.putExtra("rejoin", "" + studyListArrayList.get(i).getSetting().getRejoin());
                                            startActivity(intent);
                                            finish();
                                        }
                                        isStudyAvailable = true;
                                        break;
                                    }
                                }
                                if (!isStudyAvailable) {
                                    Toast.makeText(StandaloneActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(StandaloneActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    } else if (type.equalsIgnoreCase("Study")) {
                        if (subType.equalsIgnoreCase("Activity") || subType.equalsIgnoreCase("Resource")) {
                            Study mStudy = dbServiceSubscriber.getStudyListFromDB(realm);
                            if (mStudy != null) {
                                RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                                studyListArrayList = dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, realm);
                                boolean isStudyAvailable = false;
                                boolean isStudyJoined = false;
                                for (int i = 0; i < studyListArrayList.size(); i++) {
                                    Log.e("studylist ", "" + studyListArrayList.get(i).getStudyId());
                                    if (studyId.equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                                        isStudyAvailable = true;
                                        try {
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.title), "" + studyListArrayList.get(i).getTitle());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.bookmark), "" + studyListArrayList.get(i).isBookmarked());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.status), "" + studyListArrayList.get(i).getStatus());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.studyStatus), "" + studyListArrayList.get(i).getStudyStatus());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.position), "" + i);
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.enroll), "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                            AppController.getHelperSharedPreference().writePreference(StandaloneActivity.this, getString(R.string.rejoin), "" + studyListArrayList.get(i).getSetting().getRejoin());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(getString(R.string.active)) && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                                            if (subType.equalsIgnoreCase("Resource")) {
                                                getStudyUpdate(studyListArrayList.get(i).getStudyId(), studyListArrayList.get(i).getStudyVersion(), studyListArrayList.get(i).getTitle(), "Resource", "NotificationActivity", activityIdNotification, localNotification);
                                            } else {
                                                getStudyUpdate(studyListArrayList.get(i).getStudyId(), studyListArrayList.get(i).getStudyVersion(), studyListArrayList.get(i).getTitle(), "", "NotificationActivity", activityIdNotification, localNotification);
                                            }
                                            isStudyJoined = true;
                                            break;
                                        } else {
                                            isStudyJoined = false;
                                            break;
                                        }
                                    }
                                }
                                if (!isStudyAvailable) {
                                    Toast.makeText(StandaloneActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if (!isStudyJoined) {
                                    Toast.makeText(StandaloneActivity.this, R.string.studyNotJoined, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(StandaloneActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(StandaloneActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    public void getStudyUpdate(String studyId, String studyVersion, String title, String calledFor, String from, String activityId, String localNotification) {

        mFrom = from;
        mtitle = title;
        mStudyId = studyId;
        mActivityId = activityId;
        mLocalNotification = localNotification;
        mCalledFor = calledFor;

        StudyData studyData = dbServiceSubscriber.getStudyPreferences(realm);
        Studies studies = null;
        if (studyData != null && studyData.getStudies() != null) {
            for (int i = 0; i < studyData.getStudies().size(); i++) {
                if (studyData.getStudies().get(i).getStudyId().equalsIgnoreCase(studyId)) {
                    studies = studyData.getStudies().get(i);
                }
            }
        }
        if (studies != null && studies.getVersion() != null && !studies.getVersion().equalsIgnoreCase(studyVersion)) {
            getStudyUpdateFomWS(studyId, studies.getVersion());
        } else {
            getCurrentConsentDocument(studyId);
        }
    }

    private void getStudyUpdateFomWS(String studyId, String studyVersion) {
        AppController.getHelperProgressDialog().showProgress(StandaloneActivity.this, "", "", false);
        GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
        HashMap<String, String> header = new HashMap();
        String url = URLs.STUDY_UPDATES + "?studyId=" + studyId + "&studyVersion=" + studyVersion;
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, STUDY_UPDATES, StandaloneActivity.this, StudyUpdate.class, null, header, null, false, this);

        getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
    }

    private void getCurrentConsentDocument(String studyId) {
        HashMap<String, String> header = new HashMap<>();
        String url = URLs.GET_CONSENT_DOC + "?studyId=" + studyId + "&consentVersion=&activityId=&activityVersion=";
        AppController.getHelperProgressDialog().showProgress(StandaloneActivity.this, "", "", false);
        GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, GET_CONSENT_DOC, StandaloneActivity.this, ConsentDocumentData.class, null, header, null, false, StandaloneActivity.this);

        getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
    }

    private void callGetConsentPDFWebservice() {
        ConsentPDFEvent consentPDFEvent = new ConsentPDFEvent();
        HashMap<String, String> header = new HashMap<>();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(StandaloneActivity.this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(StandaloneActivity.this, getResources().getString(R.string.userid), ""));
        String url = URLs.CONSENTPDF + "?studyId=" + mStudyId + "&consentVersion=";
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", url, CONSENTPDF, StandaloneActivity.this, ConsentPDF.class, null, header, null, false, StandaloneActivity.this);
        consentPDFEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performConsentPDF(consentPDFEvent);
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
            ConnectionDetector connectionDetector = new ConnectionDetector(StandaloneActivity.this);


            String url = URLs.BASE_URL_WCP_SERVER + URLs.CONSENT_METADATA + "?studyId=" + mStudyId;
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

            if (response != null) {
                if (response.equalsIgnoreCase("session expired")) {
                    AppController.getHelperProgressDialog().dismissDialog();
                    AppController.getHelperSessionExpired(StandaloneActivity.this, "session expired");
                } else if (response.equalsIgnoreCase("timeout")) {
                    AppController.getHelperProgressDialog().dismissDialog();
                    Toast.makeText(StandaloneActivity.this, getResources().getString(R.string.connection_timeout), Toast.LENGTH_SHORT).show();
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
                        eligibilityConsent.setStudyId(mStudyId);
                        saveConsentToDB(StandaloneActivity.this,eligibilityConsent);
                        startConsent(eligibilityConsent.getConsent(), eligibilityConsent.getEligibility().getType());
                    } else {
                        Toast.makeText(StandaloneActivity.this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    AppController.getHelperProgressDialog().dismissDialog();
                    Toast.makeText(StandaloneActivity.this, getResources().getString(R.string.unable_to_retrieve_data), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(StandaloneActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        protected void onPreExecute() {
            AppController.getHelperProgressDialog().showProgress(StandaloneActivity.this, "", "", false);
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
        Toast.makeText(StandaloneActivity.this, getResources().getString(R.string.please_review_the_updated_consent), Toast.LENGTH_SHORT).show();
        ConsentBuilder consentBuilder = new ConsentBuilder();
        List<Step> consentstep = consentBuilder.createsurveyquestion(StandaloneActivity.this, consent, mtitle);
        Task consentTask = new OrderedTask(CONSENT, consentstep);
        Intent intent = CustomConsentViewTaskActivity.newIntent(StandaloneActivity.this, consentTask, mStudyId, "", mtitle, eligibilityType, "update");
        startActivityForResult(intent, CONSENT_RESPONSECODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONSENT_RESPONSECODE) {
            if (resultCode == RESULT_OK) {
                try {
                    TaskResult result = (TaskResult) data.getSerializableExtra(CustomConsentViewTaskActivity.EXTRA_TASK_RESULT);
                    signatureBase64 = (String) result.getStepResult("Signature")
                            .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

                    signatureDate = (String) result.getStepResult("Signature")
                            .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

                    String formResult = new Gson().toJson(result.getStepResult(getResources().getString(R.string.signature_form_step)).getResults());
                    JSONObject formResultObj = new JSONObject(formResult);
                    JSONObject fullNameObj = formResultObj.getJSONObject("First Name");
                    JSONObject fullNameResult = fullNameObj.getJSONObject("results");
                    firstName = fullNameResult.getString("answer");

                    JSONObject lastNameObj = formResultObj.getJSONObject("Last Name");
                    JSONObject lastNameResult = lastNameObj.getJSONObject("results");
                    lastName = lastNameResult.getString("answer");

                } catch (Exception e) {
                    e.printStackTrace();
                }


                genarateConsentPDF(signatureBase64);
                // encrypt the genarated pdf
                encryptFile = AppController.genarateEncryptedConsentPDF("/data/data/" + getPackageName() + "/files/", mFileName);
                //After encryption delete the pdf file
                if (encryptFile != null) {
                    File file = new File("/data/data/" + getPackageName() + "/files/" + mFileName + ".pdf");
                    file.delete();
                }
                Intent intent = new Intent(StandaloneActivity.this, ConsentCompletedActivity.class);
//                intent.putExtra("enrollId", getIntent().getStringExtra("enrollId"));
                intent.putExtra("studyId", mStudyId);
                intent.putExtra("title", mtitle);
                intent.putExtra("eligibility", eligibilityType);
                intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
                // get the encrypted file path
                intent.putExtra("PdfPath", encryptFile.getAbsolutePath());
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please complete the consent to continue", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void genarateConsentPDF(String signatureBase64) {
        try {
            getFile("/data/data/" + getPackageName() + "/files/");
            Date date = new Date();
            String timeStamp = AppController.getDateFormatType3();
            mFileName = timeStamp;
            String filePath = "/data/data/" + getPackageName() + "/files/" + timeStamp + ".pdf";
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
                    String title = mtitle;
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
            String participant = getResources().getString(R.string.participant);
            docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", participant));
            String detail = getResources().getString(R.string.agree_participate_research_study);
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
            table1.addCell(getCell(getResources().getString(R.string.participans_name), PdfPCell.ALIGN_CENTER));
            table1.addCell(getCell(getResources().getString(R.string.participants_signature), PdfPCell.ALIGN_CENTER));
            table1.addCell(getCell(getResources().getString(R.string.date), PdfPCell.ALIGN_CENTER));
            consentItem.add(table1);

            document.add(consentItem);
            document.close();
        } catch (IOException | DocumentException e) {
            Toast.makeText(StandaloneActivity.this, R.string.not_able_create_pdf, Toast.LENGTH_SHORT).show();
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


    private void setStudyList(boolean offline) {
        if (!offline) {
            dbServiceSubscriber.saveStudyListToDB(this,mStudy);
        }


        ArrayList<StudyList> activeInprogress = new ArrayList<>();
        ArrayList<StudyList> activeYetToJoin = new ArrayList<>();
        ArrayList<StudyList> activeOthers = new ArrayList<>();
        ArrayList<StudyList> upComing = new ArrayList<>();
        ArrayList<StudyList> paused = new ArrayList<>();
        ArrayList<StudyList> closed = new ArrayList<>();
        ArrayList<StudyList> others = new ArrayList<>();


        ArrayList<CompletionAdeherenceCalc> activeInprogressCompletionAdeherenceCalc = new ArrayList<>();
        ArrayList<CompletionAdeherenceCalc> activeYetToJoinCompletionAdeherenceCalc = new ArrayList<>();
        ArrayList<CompletionAdeherenceCalc> activeOthersCompletionAdeherenceCalc = new ArrayList<>();
        ArrayList<CompletionAdeherenceCalc> upComingCompletionAdeherenceCalc = new ArrayList<>();
        ArrayList<CompletionAdeherenceCalc> pausedCompletionAdeherenceCalc = new ArrayList<>();
        ArrayList<CompletionAdeherenceCalc> closedCompletionAdeherenceCalc = new ArrayList<>();
        ArrayList<CompletionAdeherenceCalc> othersCompletionAdeherenceCalc = new ArrayList<>();

        CompletionAdeherenceCalc completionAdeherenceCalc;
        CompletionAdeherenceCalc completionAdeherenceCalcSort = null;

        SurvayScheduler survayScheduler = new SurvayScheduler(dbServiceSubscriber, realm);
        for (int i = 0; i < studyListArrayList.size(); i++) {
            if (!AppController.getHelperSharedPreference().readPreference(StandaloneActivity.this, getResources().getString(R.string.userid), "").equalsIgnoreCase("")) {
                completionAdeherenceCalc = survayScheduler.completionAndAdherenceCalculation(studyListArrayList.get(i).getStudyId(), StandaloneActivity.this);
                if (completionAdeherenceCalc.isActivityAvailable()) {
                    completionAdeherenceCalcSort = completionAdeherenceCalc;
                } else {
                    Studies studies = dbServiceSubscriber.getStudies(studyListArrayList.get(i).getStudyId(), realm);
                    if (studies != null) {
                        try {
                            CompletionAdeherenceCalc completionAdeherenceCalculation = new CompletionAdeherenceCalc();
                            completionAdeherenceCalculation.setCompletion(studies.getCompletion());
                            completionAdeherenceCalculation.setAdherence(studies.getAdherence());
                            completionAdeherenceCalculation.setActivityAvailable(false);
                            completionAdeherenceCalcSort = completionAdeherenceCalculation;
                        } catch (Exception e) {
                            CompletionAdeherenceCalc completionAdeherenceCalculation = new CompletionAdeherenceCalc();
                            completionAdeherenceCalculation.setAdherence(0);
                            completionAdeherenceCalculation.setCompletion(0);
                            completionAdeherenceCalculation.setActivityAvailable(false);
                            completionAdeherenceCalcSort = completionAdeherenceCalculation;
                            e.printStackTrace();
                        }
                    } else {
                        CompletionAdeherenceCalc completionAdeherenceCalculation = new CompletionAdeherenceCalc();
                        completionAdeherenceCalculation.setAdherence(0);
                        completionAdeherenceCalculation.setCompletion(0);
                        completionAdeherenceCalculation.setActivityAvailable(false);
                        completionAdeherenceCalcs.add(completionAdeherenceCalculation);
                        completionAdeherenceCalcSort = completionAdeherenceCalculation;
                    }

                }

            }
            if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE) && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(IN_PROGRESS)) {
                activeInprogress.add(studyListArrayList.get(i));
                try {
                    activeInprogressCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE) && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(YET_TO_JOIN)) {
                activeYetToJoin.add(studyListArrayList.get(i));
                try {
                    activeYetToJoinCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE)) {
                activeOthers.add(studyListArrayList.get(i));
                try {
                    activeOthersCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(UPCOMING)) {
                upComing.add(studyListArrayList.get(i));
                try {
                    upComingCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(PAUSED)) {
                paused.add(studyListArrayList.get(i));
                try {
                    pausedCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(CLOSED)) {
                closed.add(studyListArrayList.get(i));
                try {
                    closedCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                others.add(studyListArrayList.get(i));
                try {
                    othersCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (offline) {
            try {
                studyListArrayList = dbServiceSubscriber.clearStudyList(studyListArrayList, realm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, activeInprogress, realm);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, activeYetToJoin, realm);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, activeOthers, realm);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, upComing, realm);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, paused, realm);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, closed, realm);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, others, realm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                studyListArrayList.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                studyListArrayList.addAll(activeInprogress);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                studyListArrayList.addAll(activeYetToJoin);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                studyListArrayList.addAll(activeOthers);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                studyListArrayList.addAll(upComing);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                studyListArrayList.addAll(paused);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                studyListArrayList.addAll(closed);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                studyListArrayList.addAll(others);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        try {
            completionAdeherenceCalcs.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            completionAdeherenceCalcs.addAll(activeInprogressCompletionAdeherenceCalc);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            completionAdeherenceCalcs.addAll(activeYetToJoinCompletionAdeherenceCalc);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            completionAdeherenceCalcs.addAll(activeOthersCompletionAdeherenceCalc);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            completionAdeherenceCalcs.addAll(upComingCompletionAdeherenceCalc);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            completionAdeherenceCalcs.addAll(pausedCompletionAdeherenceCalc);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            completionAdeherenceCalcs.addAll(closedCompletionAdeherenceCalc);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            completionAdeherenceCalcs.addAll(othersCompletionAdeherenceCalc);
        } catch (Exception e) {
            e.printStackTrace();
        }


        activeInprogress.clear();
        activeInprogress = null;
        activeInprogressCompletionAdeherenceCalc.clear();
        activeInprogressCompletionAdeherenceCalc = null;

        activeYetToJoin.clear();
        activeYetToJoin = null;
        activeYetToJoinCompletionAdeherenceCalc.clear();
        activeYetToJoinCompletionAdeherenceCalc = null;

        activeOthers.clear();
        activeOthers = null;
        activeOthersCompletionAdeherenceCalc.clear();
        activeOthersCompletionAdeherenceCalc = null;

        upComing.clear();
        upComing = null;
        upComingCompletionAdeherenceCalc.clear();
        upComingCompletionAdeherenceCalc = null;

        paused.clear();
        paused = null;
        pausedCompletionAdeherenceCalc.clear();
        pausedCompletionAdeherenceCalc = null;

        closed.clear();
        closed = null;
        closedCompletionAdeherenceCalc.clear();
        closedCompletionAdeherenceCalc = null;

        others.clear();
        others = null;
        othersCompletionAdeherenceCalc.clear();
        othersCompletionAdeherenceCalc = null;


    }
    @Override
    protected void onDestroy() {
        if (dbServiceSubscriber != null && realm != null)
            dbServiceSubscriber.closeRealmObj(realm);
        super.onDestroy();
    }

}
