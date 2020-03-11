package com.harvard.studyAppModule.consent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.harvard.EligibilityModule.ComprehensionFailureActivity;
import com.harvard.EligibilityModule.ComprehensionSuccessActivity;
import com.harvard.R;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.ConsentCompletedActivity;
import com.harvard.studyAppModule.ResponseServerModel.ResponseServerData;
import com.harvard.studyAppModule.StudyFragment;
import com.harvard.studyAppModule.StudyModulePresenter;
import com.harvard.studyAppModule.SurveyActivitiesFragment;
import com.harvard.studyAppModule.SurveyActivity;
import com.harvard.studyAppModule.consent.model.ComprehensionCorrectAnswers;
import com.harvard.studyAppModule.consent.model.Consent;
import com.harvard.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.studyAppModule.custom.StepSwitcherCustom;
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
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;
import org.researchstack.backbone.ui.step.layout.StepLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.CipherInputStream;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Naveen Raj on 03/01/2017.
 */
public class CustomConsentViewTaskActivity extends AppCompatActivity implements StepCallbacks, ApiCall.OnAsyncRequestComplete, ApiCallResponseServer.OnAsyncRequestComplete {
    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";
    public static final String STUDYID = "ViewTaskActivity.studyID";
    public static final String ENROLLID = "ViewTaskActivity.enrollID";
    public static final String PDFTITLE = "ViewTaskActivity.pdfTitle";
    public static final String ELIGIBILITY = "ViewTaskActivity.eligibility";
    public static final String TYPE = "ViewTaskActivity.type";

    private StepSwitcherCustom root;
    private static final String FILE_FOLDER = "FDA_PDF";
    private static final int STUDY_UPDATES = 205;
    private boolean mClick = true;
    private boolean mCompletionAdherenceStatus = true;
    private final int ENROLL_ID_RESPONSECODE = 100;

    private Step currentStep;
    private Task task;
    private TaskResult taskResult;
    Step nextStep, checkNextStep;
    private String studyId;
    private String enrollId;
    private String pdfTitle;
    private String eligibility;
    public static final String CONSENT = "consent";
    private String type;
    private String participantId = "";
    int score = 0;
    int passScore = 0;
    Consent mConsent;
    RealmList<ComprehensionCorrectAnswers> comprehensionCorrectAnswerses;
    Step previousStep;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;
    private final int UPDATE_USERPREFERENCE_RESPONSECODE = 102;
    private static final int GET_PREFERENCES = 2016;
    private final int UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE = 101;
    String enrolleddate;
    EligibilityConsent eligibilityConsent;
    StudyList studyList;
    String pdfPath;

    public static Intent newIntent(Context context, Task task, String studyId, String enrollId, String pdfTitle, String eligibility, String type) {
        Intent intent = new Intent(context, CustomConsentViewTaskActivity.class);
        intent.putExtra(STUDYID, studyId);
        intent.putExtra(ENROLLID, enrollId);
        intent.putExtra(PDFTITLE, pdfTitle);
        intent.putExtra(ELIGIBILITY, eligibility);
        intent.putExtra(TYPE, type);
        return intent;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setResult(RESULT_CANCELED);
        super.setContentView(R.layout.stepswitchercustom);


        Toolbar toolbar = (Toolbar) findViewById(org.researchstack.backbone.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        root = (StepSwitcherCustom) findViewById(R.id.container);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj(this);
        studyList = dbServiceSubscriber.getStudiesDetails(getIntent().getStringExtra(STUDYID), mRealm);

        if (savedInstanceState == null) {


            studyId = getIntent().getStringExtra(STUDYID);
            pdfTitle = getIntent().getStringExtra(PDFTITLE);

            eligibilityConsent = dbServiceSubscriber.getConsentMetadata(studyId, mRealm);
            mConsent = eligibilityConsent.getConsent();
            ConsentBuilder consentBuilder = new ConsentBuilder();
            List<Step> consentstep = consentBuilder.createsurveyquestion(CustomConsentViewTaskActivity.this, mConsent, pdfTitle);

            task = new OrderedTask(CONSENT, consentstep);
            enrollId = getIntent().getStringExtra(ENROLLID);

            eligibility = getIntent().getStringExtra(ELIGIBILITY);
            type = getIntent().getStringExtra(TYPE);
            taskResult = new TaskResult(task.getIdentifier());
            taskResult.setStartDate(new Date());
        } else {
            studyId = (String) savedInstanceState.getSerializable(STUDYID);
            pdfTitle = (String) savedInstanceState.getSerializable(PDFTITLE);

            eligibilityConsent = dbServiceSubscriber.getConsentMetadata(studyId, mRealm);
            mConsent = eligibilityConsent.getConsent();
            ConsentBuilder consentBuilder = new ConsentBuilder();
            List<Step> consentstep = consentBuilder.createsurveyquestion(CustomConsentViewTaskActivity.this, mConsent, pdfTitle);
            task = new OrderedTask(CONSENT, consentstep);

            enrollId = (String) savedInstanceState.getSerializable(ENROLLID);

            eligibility = (String) savedInstanceState.getSerializable(ELIGIBILITY);
            type = (String) savedInstanceState.getSerializable(TYPE);
            taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
            currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
        }


        task.validateParameters();


        if (currentStep == null) {
            currentStep = task.getStepAfterStep(null, taskResult);
        }


        comprehensionCorrectAnswerses = mConsent.getComprehension().getCorrectAnswers();
        passScore = Integer.parseInt(mConsent.getComprehension().getPassScore());

        showStep(currentStep);
    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }

    protected Step getCurrentStep() {
        return currentStep;
    }

    protected void showNextStep() {
        boolean shownext = true;
        if (shownext) {
            nextStep = task.getStepAfterStep(currentStep, taskResult);
            if (calcPassScore(currentStep, taskResult)) {
                score = score + 1;
            }
            if (nextStep == null) {
                saveAndFinish();
            } else {
                String checkIdentifier;
                if (mConsent.getSharing().getTitle().equalsIgnoreCase("") && mConsent.getSharing().getText().equalsIgnoreCase("") && mConsent.getSharing().getShortDesc().equalsIgnoreCase("") && mConsent.getSharing().getLongDesc().equalsIgnoreCase("")) {
                    checkIdentifier = "review";
                } else {
                    checkIdentifier = "sharing";
                }

                if (mConsent.getComprehension().getQuestions().size() > 0 && nextStep.getIdentifier().equalsIgnoreCase(checkIdentifier)) {
                    if (score >= passScore) {
                        Intent intent = new Intent(this, ComprehensionSuccessActivity.class);
                        startActivityForResult(intent, 123);
                    } else {
                        Intent intent = new Intent(this, ComprehensionFailureActivity.class);
                        intent.putExtra("enrollId", enrollId);
                        intent.putExtra("studyId", studyId);
                        intent.putExtra("title", pdfTitle);
                        intent.putExtra("eligibility", eligibility);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    showStep(nextStep);
                }
            }
        } else {
            Toast.makeText(this, "You can't join study without sharing your data", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private boolean calcPassScore(Step currentStep, TaskResult taskResult) {
        ArrayList<String> answer = new ArrayList<>();
        for (int i = 0; i < comprehensionCorrectAnswerses.size(); i++) {
            if (comprehensionCorrectAnswerses.get(i).getKey().equalsIgnoreCase(currentStep.getIdentifier())) {
                Map<String, StepResult> map = taskResult.getResults();
                for (Map.Entry<String, StepResult> pair : map.entrySet()) {
                    if (pair.getKey().equalsIgnoreCase(currentStep.getIdentifier())) {
                        try {
                            StepResult stepResult = pair.getValue();
                            Object o = stepResult.getResults().get("answer");
                            if (o instanceof Object[]) {
                                Object[] objects = (Object[]) o;
                                for (int j = 0; j < objects.length; j++) {
                                    if (objects[j] instanceof String) {
                                        for (int k = 0; k < comprehensionCorrectAnswerses.get(i).getAnswer().size(); k++) {
                                            if (((String) objects[j]).equalsIgnoreCase(comprehensionCorrectAnswerses.get(i).getAnswer().get(k).getAnswer())) {
                                                answer.add("" + ((String) objects[j]));
                                            }
                                        }
                                    }
                                }
                                if (comprehensionCorrectAnswerses.get(i).getEvaluation().equalsIgnoreCase("all")) {
                                    if (objects.length == comprehensionCorrectAnswerses.get(i).getAnswer().size() && answer.size() >= comprehensionCorrectAnswerses.get(i).getAnswer().size()) {
                                        return true;
                                    }
                                } else {
                                    if (answer.size() > 0) {
                                        for (int k = 0; k < answer.size(); k++) {
                                            boolean correctAnswer = false;
                                            for (int j = 0; j < comprehensionCorrectAnswerses.get(i).getAnswer().size(); j++) {
                                                if (answer.get(k).equalsIgnoreCase(comprehensionCorrectAnswerses.get(i).getAnswer().get(j).getAnswer())) {
                                                    correctAnswer = true;
                                                }
                                            }
                                            if (!correctAnswer) {
                                                return false;
                                            }
                                        }
                                        return true;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            showStep(nextStep);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.survey_menu, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), 0, s.length(), 0);
        item.setTitle(s);
        return true;
    }


    protected void showPreviousStep() {
        previousStep = task.getStepBeforeStep(currentStep, taskResult);
        if (previousStep == null) {
            finish();
        } else {
            if (currentStep.getIdentifier().equalsIgnoreCase("sharing") || ((currentStep.getIdentifier().equalsIgnoreCase("review") && !previousStep.getIdentifier().equalsIgnoreCase("sharing")))) {
                finish();
            } else {
                if (calcPassScore(previousStep, taskResult)) {
                    score = score - 1;
                }
                showStep(previousStep);
            }
        }
    }

    private void showStep(Step step) {
        int currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult)
                .getCurrent();
        int newStepPosition = task.getProgressOfCurrentStep(step, taskResult).getCurrent();


        StepLayout stepLayout = getLayoutForStep(step);
        stepLayout.getLayout().getId();
        stepLayout.getLayout().setTag(org.researchstack.backbone.R.id.rsb_step_layout_id, step.getIdentifier());
        root.show(stepLayout,
                newStepPosition >= currentStepPosition
                        ? StepSwitcherCustom.SHIFT_LEFT
                        : StepSwitcherCustom.SHIFT_RIGHT);

        currentStep = step;
        AppController.getHelperHideKeyboard(this);
    }

    protected StepLayout getLayoutForStep(Step step) {
        // Change the title on the activity
        String title = task.getTitleForStep(this, step);
        setActionBarTitle(title);

        // Get result from the TaskResult, can be null
        StepResult result = taskResult.getStepResult(step.getIdentifier());

        // Return the Class & constructor
        StepLayout stepLayout = createLayoutFromStep(step);
        stepLayout.initialize(step, result);
        stepLayout.setCallbacks(this);

        return stepLayout;
    }

    @NonNull
    private StepLayout createLayoutFromStep(Step step) {
        try {
            Class cls = step.getStepLayoutClass();
            Constructor constructor = cls.getConstructor(Context.class);
            return (StepLayout) constructor.newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveAndFinish() {

        taskResult.setEndDate(new Date());
        if (mClick) {
            mClick = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mClick = true;
                }
            }, 3000);
            AppController.getHelperProgressDialog().showProgress(CustomConsentViewTaskActivity.this, "", "", false);
            if (getIntent().getStringExtra(TYPE) != null && getIntent().getStringExtra(TYPE).equalsIgnoreCase("update")) {
                Studies mStudies = dbServiceSubscriber.getStudies(getIntent().getStringExtra(STUDYID), mRealm);
                if (mStudies != null)
                    participantId = mStudies.getParticipantId();
                mCompletionAdherenceStatus = false;
                getStudySate();
            } else {
                enrollId();
            }
        }








        /*Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
        resultIntent.putExtra(TYPE, type);
        setResult(RESULT_OK, resultIntent);
        finish();*/
    }


    private void enrollId() {
        EnrollIdEvent enrollIdEvent = new EnrollIdEvent();
        HashMap<String, String> params = new HashMap<>();
        params.put("studyId", getIntent().getStringExtra(STUDYID));
        params.put("token", getIntent().getStringExtra(ENROLLID));

        ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent("post_json", URLs.ENROLL_ID, ENROLL_ID_RESPONSECODE, CustomConsentViewTaskActivity.this, ResponseServerData.class, params, null, null, false, CustomConsentViewTaskActivity.this);

        enrollIdEvent.setResponseServerConfigEvent(responseServerConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performEnrollId(enrollIdEvent);
    }

    public void updateuserpreference() {
        Studies studies = dbServiceSubscriber.getStudies(getIntent().getStringExtra(STUDYID), mRealm);
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
//                studiestatus.put("bookmarked", studies.isBookmarked());
            } else {
                studiestatus.put("studyId", getIntent().getStringExtra(STUDYID));
                studiestatus.put("status", StudyFragment.IN_PROGRESS);
//                studiestatus.put("bookmarked", false);
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

//            SimpleDateFormat simpleDateFormat = AppController.getDateFormatUTC();
//            enrolleddate = simpleDateFormat.format(new Date());
//            studiestatus.put("enrolledDate", enrolleddate);
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



            AppController.getHelperSharedPreference().writePreference(CustomConsentViewTaskActivity.this, getResources().getString(R.string.studyStatus), StudyFragment.IN_PROGRESS);

            /*StudyUpdate studyUpdate = dbServiceSubscriber.getStudyUpdateById(getIntent().getStringExtra("studyId"));
            String studyVersion = "";
            if(studyUpdate != null && studyUpdate.getCurrentVersion() != null)
            {
                studyVersion = studyUpdate.getCurrentVersion();
            }
            else {
                StudyList studyList = dbServiceSubscriber.getStudyTitle(getIntent().getStringExtra("studyId"));
                studyVersion = studyList.getStudyVersion();
            }*/
            Study study = dbServiceSubscriber.getStudyListFromDB(mRealm);
            dbServiceSubscriber.updateStudyWithStudyId(this,getIntent().getStringExtra(STUDYID), study, studyUpdate.getCurrentVersion());
            dbServiceSubscriber.updateStudyPreferenceVersionDB(this,getIntent().getStringExtra(STUDYID), studyUpdate.getCurrentVersion());

           /* if (!comingFrom.equalsIgnoreCase(SurveyActivitiesFragment.FROM_SURVAY)) {
                Intent intent = new Intent(CustomConsentViewTaskActivity.this, SurveyActivity.class);
                intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
                startActivity(intent);
            } else {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
            }
            finish();*/

            dbServiceSubscriber.updateStudyPreferenceDB(this,getIntent().getStringExtra(STUDYID), StudyFragment.IN_PROGRESS, enrolleddate, participantId, AppController.getHelperSharedPreference().readPreference(CustomConsentViewTaskActivity.this, getResources().getString(R.string.studyVersion), ""));
            dbServiceSubscriber.savePdfData(this,getIntent().getStringExtra(STUDYID), pdfPath);
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
            resultIntent.putExtra(TYPE, type);
            resultIntent.putExtra("PdfPath", pdfPath);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            LoginData loginData = (LoginData) response;
            if (loginData != null) {
//                update_eligibility_consent();
                getStudySate();

            } else {
                Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == GET_PREFERENCES) {
            StudyData studies = (StudyData) response;
            if (studies != null) {
                for (int i = 0; i < studies.getStudies().size(); i++) {
                    if (getIntent().getStringExtra(STUDYID).equalsIgnoreCase(studies.getStudies().get(i).getStudyId()))
                        enrolleddate = studies.getStudies().get(i).getEnrolledDate();
                }
                update_eligibility_consent();

            } else {
                Toast.makeText(this, getResources().getString(R.string.unable_to_parse), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFile(String s) {
        File file = new File(s, FILE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private String genarateConsentPDF() {

        String filepath = "";
        try {
            String signatureBase64 = (String) taskResult.getStepResult("Signature")
                    .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

            String signatureDate = (String) taskResult.getStepResult("Signature")
                    .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

            String formResult = new Gson().toJson(taskResult.getStepResult(getResources().getString(R.string.signature_form_step)).getResults());
            JSONObject formResultObj = new JSONObject(formResult);
            JSONObject fullNameObj = formResultObj.getJSONObject("First Name");
            JSONObject fullNameResult = fullNameObj.getJSONObject("results");
            String firstName = fullNameResult.getString("answer");

            JSONObject lastNameObj = formResultObj.getJSONObject("Last Name");
            JSONObject lastNameResult = lastNameObj.getJSONObject("results");
            String lastName = lastNameResult.getString("answer");





            getFile("/data/data/" + getPackageName() + "/files/");
            String timeStamp = AppController.getDateFormatType3();
            String mFileName = timeStamp;
            String filePath = "/data/data/" + getPackageName() + "/files/" + timeStamp + ".pdf";
            File myFile = new File(filePath);
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
                    String title = studyList.getTitle();
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
//            Paragraph consentItem = new Paragraph();
//            ElementList list = XMLWorkerHelper.parseToElementList(Html.fromHtml(eligibilityConsent.getConsent().getReview().getSignatureContent().toString()).toString(), null);
//            for (Element element : list) {
//                consentItem.add(element);
//            }
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

            // encrypt the genarated pdf
            File encryptFile = AppController.genarateEncryptedConsentPDF("/data/data/" + getPackageName() + "/files/", mFileName);
            filepath = encryptFile.getAbsolutePath();
            //After encryption delete the pdf file
            if (encryptFile != null) {
                File file = new File("/data/data/" + getPackageName() + "/files/" + mFileName + ".pdf");
                file.delete();
            }

        } catch (IOException | DocumentException e) {
            Toast.makeText(CustomConsentViewTaskActivity.this, R.string.not_able_create_pdf, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return filepath;
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


    private void getStudyUpdateFomWS() {
        AppController.getHelperProgressDialog().showProgress(CustomConsentViewTaskActivity.this, "", "", false);
        GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
        DBServiceSubscriber dbServiceSubscriber = new DBServiceSubscriber();
        HashMap<String, String> header = new HashMap();
//        header.put("studyId", studyId);
//        header.put("studyVersion", studyVersion);

        String url = URLs.STUDY_UPDATES + "?studyId=" + getIntent().getStringExtra(STUDYID) + "&studyVersion=" + studyList.getStudyVersion();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, STUDY_UPDATES, CustomConsentViewTaskActivity.this, StudyUpdate.class, null, header, null, false, this);

        getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
    }

    private void update_eligibility_consent() {
        pdfPath = genarateConsentPDF();
        UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent = new UpdateEligibilityConsentStatusEvent();
        HashMap headerparams = new HashMap();
        headerparams.put("auth", AppController.getHelperSharedPreference().readPreference(CustomConsentViewTaskActivity.this, getString(R.string.auth), ""));
        headerparams.put("userId", AppController.getHelperSharedPreference().readPreference(CustomConsentViewTaskActivity.this, getString(R.string.userid), ""));

        EligibilityConsent eligibilityConsent = dbServiceSubscriber.getConsentMetadata(getIntent().getStringExtra(STUDYID), mRealm);
        JSONObject body = new JSONObject();
        try {
            body.put("studyId", getIntent().getStringExtra(STUDYID));
            body.put("eligibility", true);

            JSONObject consentbody = new JSONObject();
            consentbody.put("version", eligibilityConsent.getConsent().getVersion());
            consentbody.put("status", "Completed");
            try {
                consentbody.put("pdf", convertFileToString(pdfPath));
            } catch (IOException e) {
                e.printStackTrace();
                consentbody.put("pdf", "");
            }

            body.put("consent", consentbody);

            body.put("sharing", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_ELIGIBILITY_CONSENT, UPDATE_ELIGIBILITY_CONSENT_RESPONSECODE, CustomConsentViewTaskActivity.this, LoginData.class, null, headerparams, body, false, CustomConsentViewTaskActivity.this);
        updateEligibilityConsentStatusEvent.setRegistrationServerConfigEvent(registrationServerConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performUpdateEligibilityConsent(updateEligibilityConsentStatusEvent);
    }

    private String convertFileToString(String filepath) throws IOException {
//        InputStream inputStream = new FileInputStream(filepath);
//        byte[] byteArray = IOUtils.toByteArray(inputStream);
//        byte[] byteArray = decryptPDF(filepath);
        CipherInputStream cis = AppController.genarateDecryptedConsentPDF(filepath);
        byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    private void getStudySate() {
        GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(CustomConsentViewTaskActivity.this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(CustomConsentViewTaskActivity.this, getResources().getString(R.string.userid), ""));
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", URLs.STUDY_STATE, GET_PREFERENCES, CustomConsentViewTaskActivity.this, StudyData.class, null, header, null, false, this);

        getPreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performGetUserPreference(getPreferenceEvent);
    }




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


    @Override
    protected void onPause() {
        hideKeyboard();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            notifyStepOfBackPress();
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        notifyStepOfBackPress();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_TASK, task);
        outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
        outState.putSerializable(EXTRA_STEP, currentStep);
        outState.putSerializable(STUDYID, studyId);
        outState.putSerializable(ENROLLID, enrollId);
        outState.putSerializable(PDFTITLE, pdfTitle);
        outState.putSerializable(ELIGIBILITY, eligibility);
        outState.putSerializable(TYPE, type);
    }

    private void notifyStepOfBackPress() {
        StepLayout currentStepLayout = (StepLayout) findViewById(org.researchstack.backbone.R.id.rsb_current_step);
        currentStepLayout.isBackEventConsumed();
    }

//    @Override
//    public void onDataReady() {
//        super.onDataReady();
//
//        if (currentStep == null) {
//            currentStep = task.getStepAfterStep(null, taskResult);
//        }
//
//        showStep(currentStep);
//    }
//
//    @Override
//    public void onDataFailed() {
//        super.onDataFailed();
//        Toast.makeText(this, org.researchstack.backbone.R.string.rsb_error_data_failed, Toast.LENGTH_LONG).show();
//        finish();
//    }

    @Override
    public void onSaveStep(int action, Step step, StepResult result) {
        onSaveStepResult(step.getIdentifier(), result);

        onExecuteStepAction(action);
    }

    protected void onSaveStepResult(String id, StepResult result) {
        taskResult.setStepResultForStepIdentifier(id, result);
    }

    protected void onExecuteStepAction(int action) {
        if (action == StepCallbacks.ACTION_NEXT) {
            showNextStep();
        } else if (action == StepCallbacks.ACTION_PREV) {
            showPreviousStep();
        } else if (action == StepCallbacks.ACTION_END) {
            showConfirmExitDialog();
        } else if (action == StepCallbacks.ACTION_NONE) {
            // Used when onSaveInstanceState is called of a view. No action is taken.
        } else {
            throw new IllegalArgumentException("Action with value " + action + " is invalid. " +
                    "See StepCallbacks for allowable arguments");
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive() && imm.isAcceptingText()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    private void showConfirmExitDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).setTitle(
                R.string.exit_message)
                .setMessage(org.researchstack.backbone.R.string.lorem_medium)
                .setPositiveButton(R.string.end_task, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CustomConsentViewTaskActivity.this.finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .create();
        alertDialog.show();
    }

    @Override
    public void onCancelStep() {
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setMessage("Sorry, this study does not allow you to proceed for the selection you just made. Click OK to quit enrolling for the study or Cancel to change your selection.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(12345);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
