package com.harvard.studyAppModule;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.harvard.R;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.ResponseServerModel.ResponseServerData;
import com.harvard.studyAppModule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.studyAppModule.events.EnrollIdEvent;
import com.harvard.studyAppModule.events.GetUserStudyListEvent;
import com.harvard.studyAppModule.events.UpdateEligibilityConsentStatusEvent;
import com.harvard.studyAppModule.studyModel.Study;
import com.harvard.studyAppModule.studyModel.StudyList;
import com.harvard.studyAppModule.studyModel.StudyUpdate;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.event.GetPreferenceEvent;
import com.harvard.userModule.event.UpdatePreferenceEvent;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.userModule.webserviceModel.Studies;
import com.harvard.userModule.webserviceModel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.ResponseServerConfigEvent;
import com.harvard.webserviceModule.events.WCPConfigEvent;

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

    private final int ENROLL_ID_RESPONSECODE = 100;
    private final int UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE = 101;
    private final int UPDATE_USERPREFERENCE_RESPONSECODE = 102;

    public static String FROM = "from";
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
        mRealm = AppController.getRealmobj(this);

        initializeXMLId();
        try {
            if (getIntent().getStringExtra(FROM) != null && getIntent().getStringExtra(FROM).equalsIgnoreCase(SurveyActivitiesFragment.FROM_SURVAY)) {
                comingFrom = SurveyActivitiesFragment.FROM_SURVAY;
            }
        } catch (Exception e) {
            comingFrom = "";
            e.printStackTrace();
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
                    if (!comingFrom.equalsIgnoreCase(SurveyActivitiesFragment.FROM_SURVAY)) {
                        Intent intent = new Intent(ConsentCompletedActivity.this, SurveyActivity.class);
                        intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
                        startActivity(intent);
                    } else {
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                    }
                    finish();
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
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(ConsentCompletedActivity.this, R.string.permission_deniedDate, Toast.LENGTH_SHORT).show();
                } else {
                    displayPDF();
                }
                break;
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
            mConsentCompleteTxt.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, "regular"));
            mTextSecRow.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, "light"));
            mViewpdf.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, "regular"));
            mNext.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update_eligibility_consent() {
        UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent = new UpdateEligibilityConsentStatusEvent();
        HashMap headerparams = new HashMap();
        headerparams.put("auth", AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getString(R.string.auth), ""));
        headerparams.put("userId", AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getString(R.string.userid), ""));

        EligibilityConsent eligibilityConsent = dbServiceSubscriber.getConsentMetadata(getIntent().getStringExtra("studyId"), mRealm);
        JSONObject body = new JSONObject();
        try {
            body.put("studyId", getIntent().getStringExtra("studyId"));
            body.put("eligibility", true);

            JSONObject consentbody = new JSONObject();
            consentbody.put("version", eligibilityConsent.getConsent().getVersion());
            consentbody.put("status", "Completed");
            try {
                consentbody.put("pdf", convertFileToString(getIntent().getStringExtra("PdfPath")));
            } catch (IOException e) {
                e.printStackTrace();
                consentbody.put("pdf", "");
            }

            body.put("consent", consentbody);

            body.put("sharing", "");
        } catch (JSONException e) {
            e.printStackTrace();
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

            dbServiceSubscriber.updateStudyPreferenceDB(this,getIntent().getStringExtra("studyId"), StudyFragment.IN_PROGRESS, enrolleddate, participantId, AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getResources().getString(R.string.studyVersion), ""));
            dbServiceSubscriber.savePdfData(this,getIntent().getStringExtra("studyId"), getIntent().getStringExtra("PdfPath"));

            AppController.getHelperSharedPreference().writePreference(ConsentCompletedActivity.this, getResources().getString(R.string.studyStatus), StudyFragment.IN_PROGRESS);

            Study study = dbServiceSubscriber.getStudyListFromDB(mRealm);
            dbServiceSubscriber.updateStudyWithStudyId(this,getIntent().getStringExtra("studyId"), study, studyUpdate.getCurrentVersion());
            dbServiceSubscriber.updateStudyPreferenceVersionDB(this,getIntent().getStringExtra("studyId"), studyUpdate.getCurrentVersion());

            if (!comingFrom.equalsIgnoreCase(SurveyActivitiesFragment.FROM_SURVAY)) {
                Intent intent = new Intent(ConsentCompletedActivity.this, SurveyActivity.class);
                intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
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
                    if (getIntent().getStringExtra("studyId").equalsIgnoreCase(studies.getStudies().get(i).getStudyId()))
                        enrolleddate = studies.getStudies().get(i).getEnrolledDate();
                }
                update_eligibility_consent();

            } else {
                Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getStudySate() {
        GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getResources().getString(R.string.userid), ""));
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", URLs.STUDY_STATE, GET_PREFERENCES, ConsentCompletedActivity.this, StudyData.class, null, header, null, false, this);

        getPreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performGetUserPreference(getPreferenceEvent);
    }

    private void getStudyUpdateFomWS() {
        AppController.getHelperProgressDialog().showProgress(ConsentCompletedActivity.this, "", "", false);
        GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
        DBServiceSubscriber dbServiceSubscriber = new DBServiceSubscriber();
        HashMap<String, String> header = new HashMap();
        StudyList studyList = dbServiceSubscriber.getStudiesDetails(getIntent().getStringExtra("studyId"), mRealm);
        String url = URLs.STUDY_UPDATES + "?studyId=" + getIntent().getStringExtra("studyId") + "&studyVersion=" + studyList.getStudyVersion();
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

        File file = getEncryptedFilePath(getIntent().getStringExtra("PdfPath"));

        try {
            mSharingFile = copy(file);
        } catch (IOException e) {
            e.printStackTrace();
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
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getString(R.string.title), "") + " - Consent");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.signed_consent));
                    shareIntent.setType("application/pdf");
                    Uri fileUri = FileProvider.getUriForFile(ConsentCompletedActivity.this, getString(R.string.FileProvider_authorities), finalMSharingFile);
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
        try {
            CipherInputStream cis = AppController.genarateDecryptedConsentPDF(filePath);
            byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
            File file = new File("/data/data/"+getPackageName()+"/files/" + "temp" + ".pdf");
            if (!file.exists() && file == null) {
                file.createNewFile();
            }
            OutputStream output = new FileOutputStream(file);
            output.write(byteArray);
            output.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertFileToString(String filepath) throws IOException {
        CipherInputStream cis = AppController.genarateDecryptedConsentPDF(filepath);
        byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public void updateuserpreference() {
        Studies studies = dbServiceSubscriber.getStudies(getIntent().getStringExtra("studyId"), mRealm);
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        JSONArray studieslist = new JSONArray();
        JSONObject studiestatus = new JSONObject();
        try {
            if (studies != null) {
                studiestatus.put("studyId", studies.getStudyId());
                studiestatus.put("status", StudyFragment.IN_PROGRESS);
            } else {
                studiestatus.put("studyId", getIntent().getStringExtra("studyId"));
                studiestatus.put("status", StudyFragment.IN_PROGRESS);
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
            e.printStackTrace();
        }

        studieslist.put(studiestatus);
        try {
            jsonObject.put("studies", studieslist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_STUDY_PREFERENCE, UPDATE_USERPREFERENCE_RESPONSECODE, this, LoginData.class, null, header, jsonObject, false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    private void enrollId() {
        EnrollIdEvent enrollIdEvent = new EnrollIdEvent();
        HashMap<String, String> params = new HashMap<>();
        params.put("studyId", getIntent().getStringExtra("studyId"));
        params.put("token", getIntent().getStringExtra("enrollId"));

        ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent("post_json", URLs.ENROLL_ID, ENROLL_ID_RESPONSECODE, ConsentCompletedActivity.this, ResponseServerData.class, params, null, null, false, ConsentCompletedActivity.this);

        enrollIdEvent.setResponseServerConfigEvent(responseServerConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performEnrollId(enrollIdEvent);
    }


    public File copy(File src) throws IOException {
        String primaryStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AppController.getHelperSharedPreference().readPreference(ConsentCompletedActivity.this, getString(R.string.title), "") + "_" + getString(R.string.signed_consent) + ".pdf";
        File file = new File(primaryStoragePath);
        if (!file.exists())
            file.createNewFile();

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(file);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

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
