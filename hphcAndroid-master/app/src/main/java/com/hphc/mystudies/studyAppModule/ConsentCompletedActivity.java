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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.hphc.mystudies.R;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.studyAppModule.ResponseServerModel.ResponseServerData;
import com.hphc.mystudies.studyAppModule.consent.model.EligibilityConsent;
import com.hphc.mystudies.studyAppModule.events.EnrollIdEvent;
import com.hphc.mystudies.studyAppModule.events.GetUserStudyListEvent;
import com.hphc.mystudies.studyAppModule.events.UpdateEligibilityConsentStatusEvent;
import com.hphc.mystudies.studyAppModule.studyModel.Study;
import com.hphc.mystudies.studyAppModule.studyModel.StudyList;
import com.hphc.mystudies.studyAppModule.studyModel.StudyUpdate;
import com.hphc.mystudies.userModule.UserModulePresenter;
import com.hphc.mystudies.userModule.event.GetPreferenceEvent;
import com.hphc.mystudies.userModule.event.UpdatePreferenceEvent;
import com.hphc.mystudies.userModule.webserviceModel.LoginData;
import com.hphc.mystudies.userModule.webserviceModel.Studies;
import com.hphc.mystudies.userModule.webserviceModel.StudyData;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCallResponseServer;
import com.hphc.mystudies.webserviceModule.events.RegistrationServerConfigEvent;
import com.hphc.mystudies.webserviceModule.events.ResponseServerConfigEvent;
import com.hphc.mystudies.webserviceModule.events.WCPConfigEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.crypto.CipherInputStream;

import io.realm.Realm;

import static android.os.Build.VERSION_CODES.M;

public class ConsentCompletedActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete, ApiCallResponseServer.OnAsyncRequestComplete {

    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int STUDY_UPDATES = 205;
    private static final int GET_PREFERENCES = 2016;
    private TextView mConsentCompleteTxt;
    private TextView mTextSecRow;
    private TextView mViewpdf;
    private TextView mNext;

    private static final int ENROLL_ID_RESPONSECODE = 100;
    private static final int UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE = 101;
    private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 102;
    private static final String STUDYIDCONSTANT = "studyId";
    private static final String REGULAR = "regular";
    private static final String USERIDCONSTANT = "userId";
    private static final String STATUSCONSTANT = "status";
    private static final String PDFPATH = "PdfPath";

    public static final String FROM = "from";
    private boolean mClick = true;
    String enrolleddate;
    File mSharingFile = null;
    private String participantId = "";
    private String comingFrom = "";
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;
    private boolean mCompletionAdherenceStatus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_completed);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj();

        initializeXMLId();
        try {
            if (getIntent().getStringExtra(FROM) != null && getIntent().getStringExtra(FROM).equalsIgnoreCase(SurveyActivitiesFragment.FROM_SURVAY)) {
                comingFrom = SurveyActivitiesFragment.FROM_SURVAY;
            }
        } catch (Exception e) {
            comingFrom = "";
        }
        setFont();
        mViewpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ActivityCompat.checkSelfPermission(ConsentCompletedActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(ConsentCompletedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(permission)) {
                        ActivityCompat.requestPermissions((Activity) ConsentCompletedActivity.this, permission, PERMISSION_REQUEST_CODE);
                    } else {
                        displayPDF();
                    }
                } else {
                    displayPDF();
                }
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClick) {
                    mClick = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mClick = true;
                        }
                    }, 3000);
                    AppController.getHelperProgressDialog().showProgress(ConsentCompletedActivity.this, "", "", false);
                    if (getIntent().getStringExtra("type") != null && getIntent().getStringExtra("type").equalsIgnoreCase("update")) {
                        Studies mStudies = dbServiceSubscriber.getStudies(getIntent().getStringExtra(STUDYIDCONSTANT), mRealm);
                        if (mStudies != null)
                            participantId = mStudies.getParticipantId();
                        mCompletionAdherenceStatus = false;
                        getStudySate();
                    } else {
                        enrollId();
                    }
                }
            }
        });
    }

    public boolean hasPermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(ConsentCompletedActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(ConsentCompletedActivity.this, R.string.permission_deniedDate, Toast.LENGTH_SHORT).show();
            } else {
                displayPDF();
            }

        }
    }

    private void initializeXMLId() {
        mConsentCompleteTxt = (TextView) findViewById(R.id.consentcomplete);
        mTextSecRow = (TextView) findViewById(R.id.mTextSecRow);
        mViewpdf = (TextView) findViewById(R.id.viewpdf);
        mNext = (TextView) findViewById(R.id.next);
    }

    @Override
    public void onBackPressed() {
    }

    private void setFont() {
        try {
            mConsentCompleteTxt.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, REGULAR));
            mTextSecRow.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, "light"));
            mViewpdf.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, REGULAR));
            mNext.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, REGULAR));
        } catch (Exception e) {
        }
    }

    private void updateEligibilityConsent() {
        UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent = new UpdateEligibilityConsentStatusEvent();
        HashMap headerparams = new HashMap();
        headerparams.put("auth", AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getString(R.string.auth), ""));
        headerparams.put(USERIDCONSTANT, AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getString(R.string.userid), ""));

        EligibilityConsent eligibilityConsent = dbServiceSubscriber.getConsentMetadata(getIntent().getStringExtra(STUDYIDCONSTANT), mRealm);
        JSONObject body = new JSONObject();
        try {
            body.put(STUDYIDCONSTANT, getIntent().getStringExtra(STUDYIDCONSTANT));
            body.put("eligibility", true);

            JSONObject consentbody = new JSONObject();
            consentbody.put("version", eligibilityConsent.getConsent().getVersion());
            consentbody.put(STATUSCONSTANT, "Completed");
            try {
                consentbody.put("pdf", convertFileToString(getIntent().getStringExtra(PDFPATH)));
            } catch (IOException e) {
                consentbody.put("pdf", "");
            }

            body.put("consent", consentbody);

            body.put("sharing", "");
        } catch (JSONException e) {
        }

        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_ELIGIBILITY_CONSENT, UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE, ConsentCompletedActivity.this, LoginData.class, null, headerparams, body, false, ConsentCompletedActivity.this);
        updateEligibilityConsentStatusEvent.setRegistrationServerConfigEvent(registrationServerConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performUpdateEligibilityConsent(updateEligibilityConsentStatusEvent);
    }

    //after enrolling
    @Override
    public <T> void asyncResponse(T response, int responseCode, String serverType) {
        if (responseCode == ENROLL_ID_RESPONSECODE) {
            ResponseServerData responseServerData = (ResponseServerData) response;
            if (responseServerData.isSuccess()) {
                participantId = responseServerData.getData().getAppToken();
                updateuserpreference();
            } else {
                Toast.makeText(this, responseServerData.getException(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //after enrolling
    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE) {
            LoginData loginData = (LoginData) response;
            if (loginData != null) {
                getStudyUpdateFomWS();
            } else {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == STUDY_UPDATES) {
            StudyUpdate studyUpdate = (StudyUpdate) response;
            AppController.getHelperProgressDialog().dismissDialog();

            dbServiceSubscriber.updateStudyPreferenceDB(getIntent().getStringExtra(STUDYIDCONSTANT), StudyFragment.IN_PROGRESS, enrolleddate, participantId, AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getResources().getString(R.string.studyVersion), ""));
            dbServiceSubscriber.savePdfData(getIntent().getStringExtra(STUDYIDCONSTANT), getIntent().getStringExtra(PDFPATH));

            AppController.getHelperSharedPreference().writePreference(ConsentCompletedActivity.this, getResources().getString(R.string.studyStatus), StudyFragment.IN_PROGRESS);

            Study study = dbServiceSubscriber.getStudyListFromDB(mRealm);
            dbServiceSubscriber.updateStudyWithStudyId(getIntent().getStringExtra(STUDYIDCONSTANT), study, studyUpdate.getCurrentVersion());
            dbServiceSubscriber.updateStudyPreferenceVersionDB(getIntent().getStringExtra(STUDYIDCONSTANT), studyUpdate.getCurrentVersion());

            if (!comingFrom.equalsIgnoreCase(SurveyActivitiesFragment.FROM_SURVAY)) {
                Intent intent = new Intent(ConsentCompletedActivity.this, SurveyActivity.class);
                intent.putExtra(STUDYIDCONSTANT, getIntent().getStringExtra(STUDYIDCONSTANT));
                startActivity(intent);
            } else {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
            }
            finish();
        } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            LoginData loginData = (LoginData) response;
            if (loginData != null) {
                getStudySate();

            } else {
                Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == GET_PREFERENCES) {
            StudyData studies = (StudyData) response;
            if (studies != null) {
                for (int i = 0; i < studies.getStudies().size(); i++) {
                    if (getIntent().getStringExtra(STUDYIDCONSTANT).equalsIgnoreCase(studies.getStudies().get(i).getStudyId()))
                        enrolleddate = studies.getStudies().get(i).getEnrolledDate();
                }
                updateEligibilityConsent();

            } else {
                Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getStudySate() {
        GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getResources().getString(R.string.auth), ""));
        header.put(USERIDCONSTANT, AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getResources().getString(R.string.userid), ""));
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", URLs.STUDY_STATE, GET_PREFERENCES, ConsentCompletedActivity.this, StudyData.class, null, header, null, false, this);

        getPreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performGetUserPreference(getPreferenceEvent);
    }

    private void getStudyUpdateFomWS() {
        AppController.getHelperProgressDialog().showProgress(ConsentCompletedActivity.this, "", "", false);
        GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
        HashMap<String, String> header = new HashMap();
        StudyList studyList = dbServiceSubscriber.getStudiesDetails(getIntent().getStringExtra(STUDYIDCONSTANT), mRealm);
        String url = URLs.STUDY_UPDATES + "?studyId=" + getIntent().getStringExtra(STUDYIDCONSTANT) + "&studyVersion=" + studyList.getStudyVersion();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, STUDY_UPDATES, ConsentCompletedActivity.this, StudyUpdate.class, null, header, null, false, this);

        getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
    }


    @Override
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String statusCode, T response) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            AppController.getHelperSessionExpired(this, errormsg);
        } else if (responseCode == ENROLL_ID_RESPONSECODE) {
            ResponseServerData responseServerData = (ResponseServerData) response;
            if (responseServerData != null) {
                Toast.makeText(this, responseServerData.getException().toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            AppController.getHelperSessionExpired(this, errormsg);
        } else if (responseCode == UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE) {
            Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        }
    }


    private void displayPDF() {

        File file = getEncryptedFilePath(getIntent().getStringExtra(PDFPATH));

        try {
            mSharingFile = copy(file);
        } catch (NullPointerException e) {
        }

        if (mSharingFile != null && mSharingFile.exists()) {
            LayoutInflater li = LayoutInflater.from(ConsentCompletedActivity.this);
            View promptsView = li.inflate(R.layout.pdfdisplayview, null);
            PDFView pdfView = (PDFView) promptsView.findViewById(R.id.pdf);
            TextView share = (TextView) promptsView.findViewById(R.id.share);
            AlertDialog.Builder db = new AlertDialog.Builder(ConsentCompletedActivity.this);
            db.setView(promptsView);
            final File finalMSharingFile = mSharingFile;
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setData(Uri.parse("mailto:"));
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.signed_consent));
                    shareIntent.setType("application/pdf");
                    Uri fileUri = FileProvider.getUriForFile(ConsentCompletedActivity.this, "com.myfileprovider", finalMSharingFile);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    startActivity(shareIntent);
                }
            });
            pdfView.documentFitsView();
            pdfView.useBestQuality(true);
            pdfView.fromFile(file)
                    .enableAnnotationRendering(true)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .load();
            db.show();
        } else {
            Toast.makeText(this, R.string.consentPdfNotAvailable, Toast.LENGTH_SHORT).show();
        }
    }

    private File getEncryptedFilePath(String filePath) {
        OutputStream output = null;
        File file = null;
        try {
            CipherInputStream cis = AppController.genarateDecryptedConsentPDF(filePath);
            byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
            file = new File("/data/data/com.harvard.fda/files/" + "temp" + ".pdf");
            if (!file.exists() && file == null) {
                file.createNewFile();
            }
            output = new FileOutputStream(file);
            output.write(byteArray);

        } catch (IOException e) {
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
            }
        }
        return file;
    }

    private String convertFileToString(String filepath) throws IOException {
        CipherInputStream cis = AppController.genarateDecryptedConsentPDF(filepath);
        byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public void updateuserpreference() {
        Studies studies = dbServiceSubscriber.getStudies(getIntent().getStringExtra(STUDYIDCONSTANT), mRealm);
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.auth), ""));
        header.put(USERIDCONSTANT, AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        JSONArray studieslist = new JSONArray();
        JSONObject studiestatus = new JSONObject();
        try {
            if (studies != null) {
                studiestatus.put(STUDYIDCONSTANT, studies.getStudyId());
                studiestatus.put(STATUSCONSTANT, StudyFragment.IN_PROGRESS);
            } else {
                studiestatus.put(STUDYIDCONSTANT, getIntent().getStringExtra(STUDYIDCONSTANT));
                studiestatus.put(STATUSCONSTANT, StudyFragment.IN_PROGRESS);
            }
            if (participantId != null && !participantId.equalsIgnoreCase("")) {
                studiestatus.put("participantId", participantId);
            }
            if (mCompletionAdherenceStatus) {
                studiestatus.put("completion", "0");
                studiestatus.put("adherence", "0");
            } else {
                mCompletionAdherenceStatus = true;
            }

        } catch (JSONException e) {
        }

        studieslist.put(studiestatus);
        try {
            jsonObject.put("studies", studieslist);
        } catch (JSONException e) {
        }
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_STUDY_PREFERENCE, UPDATE_USERPREFERENCE_RESPONSECODE, this, LoginData.class, null, header, jsonObject, false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    private void enrollId() {
        EnrollIdEvent enrollIdEvent = new EnrollIdEvent();
        ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent("get", URLs.ENROLL_ID + "studyId=" + getIntent().getStringExtra(STUDYIDCONSTANT) + "&token=" + getIntent().getStringExtra("enrollId"), ENROLL_ID_RESPONSECODE, ConsentCompletedActivity.this, ResponseServerData.class, null, null, null, false, ConsentCompletedActivity.this);

        enrollIdEvent.setResponseServerConfigEvent(responseServerConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performEnrollId(enrollIdEvent);
    }


    public File copy(File src) {
        File file = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            String primaryStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getString(R.string.title), "") + "_" + getString(R.string.signed_consent) + ".pdf";
            file = new File(primaryStoragePath);
            if (!file.exists())
                file.createNewFile();

            in = new FileInputStream(src);
            out = new FileOutputStream(file);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }

            } catch (IOException e) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }

        return file;
    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        if (mSharingFile != null && mSharingFile.exists()) {
            mSharingFile.delete();
        }
        super.onDestroy();
    }
}
