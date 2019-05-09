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

package com.harvard.fda.studyAppModule.consent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.harvard.fda.EligibilityModule.ComprehensionFailureActivity;
import com.harvard.fda.EligibilityModule.ComprehensionSuccessActivity;
import com.harvard.fda.R;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.consent.model.ComprehensionCorrectAnswers;
import com.harvard.fda.studyAppModule.consent.model.Consent;
import com.harvard.fda.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.fda.studyAppModule.custom.StepSwitcherCustom;
import com.harvard.fda.utils.AppController;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Naveen Raj on 03/01/2017.
 */
public class CustomConsentViewTaskActivity extends AppCompatActivity implements StepCallbacks {
    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
    public static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";
    public static final String STUDYID = "ViewTaskActivity.studyID";
    public static final String ENROLLID = "ViewTaskActivity.enrollID";
    public static final String PDFTITLE = "ViewTaskActivity.pdfTitle";
    public static final String ELIGIBILITY = "ViewTaskActivity.eligibility";
    public static final String TYPE = "ViewTaskActivity.type";

    private StepSwitcherCustom root;

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
    int score = 0;
    int passScore = 0;
    Consent mConsent;
    RealmList<ComprehensionCorrectAnswers> comprehensionCorrectAnswerses;
    Step previousStep;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;

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

        if (savedInstanceState == null) {


            studyId = getIntent().getStringExtra(STUDYID);
            pdfTitle = getIntent().getStringExtra(PDFTITLE);

            EligibilityConsent eligibilityConsent = dbServiceSubscriber.getConsentMetadata(studyId, mRealm);
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

            EligibilityConsent eligibilityConsent = dbServiceSubscriber.getConsentMetadata(studyId, mRealm);
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
//        }
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
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_TASK_RESULT, taskResult);
        resultIntent.putExtra(TYPE, type);
        setResult(RESULT_OK, resultIntent);
        finish();
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
