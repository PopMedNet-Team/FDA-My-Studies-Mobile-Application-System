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

package com.harvard.fda.EligibilityModule;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.activityBuilder.model.Eligibility;
import com.harvard.fda.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.fda.studyAppModule.consent.model.CorrectAnswers;
import com.harvard.fda.studyAppModule.custom.StepSwitcherCustom;
import com.harvard.fda.utils.AppController;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by Naveen Raj on 03/03/2017.
 */

public class CustomViewTaskActivity<T> extends AppCompatActivity implements StepCallbacks {
    public static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
    public static final String EXTRA_STUDYID = "ViewTaskActivity.ExtraStudyId";
    public static final String ACTIVITYID = "ViewTaskActivity.ActivityId";
    public static final String STUDYID = "ViewTaskActivity.StudyId";
    public static final String ENROLLID = "ViewTaskActivity.EnrollId";
    public static final String PDF_TITLE = "ViewTaskActivity.pdfTitle";
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
    private static final String ELIGIBILITY = "ViewTaskActivity.eligibility";
    private static final String TYPE = "ViewTaskActivity.type";


    private StepSwitcherCustom root;

    private Step currentStep;
    private Task task;
    private String StudyId;
    private String enrollId;
    private String eligibility;
    private String type;
    private String pdfTitle;
    private String mActivityStatus;
    private TaskResult taskResult;
    ActivityObj mActivityObject;
    private Date currentRunStartDate;
    private Date currentRunEndDate;
    int currentStepPosition;
    String mActivityId;
    ArrayList<CorrectAnswers> correctAnswers;
    public static String RESOURCES = "resources";


    public static Intent newIntent(Context context, Task task, String surveyId, String studyId, Eligibility correctAnswers, String title, String enrollId,String eligibility,String type) {
        Intent intent = new Intent(context, CustomViewTaskActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        intent.putExtra(EXTRA_STUDYID, surveyId);
        intent.putExtra(PDF_TITLE, title);
        intent.putExtra(STUDYID, studyId);
        intent.putExtra(ENROLLID, enrollId);
        intent.putExtra(ELIGIBILITY, eligibility);
        intent.putExtra(TYPE, type);

        ArrayList<CorrectAnswers> correctAnswersArrayList = new ArrayList<>();
        correctAnswersArrayList.addAll(correctAnswers.getCorrectAnswers());

        intent.putExtra("correctanswer", correctAnswersArrayList);
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

        mActivityStatus = getIntent().getStringExtra(ACTIVITY_STATUS);
        mActivityId = getIntent().getStringExtra(ACTIVITYID);
        pdfTitle = getIntent().getStringExtra(PDF_TITLE);
        currentRunStartDate = (Date) getIntent().getSerializableExtra(RUN_START_DATE);
        currentRunEndDate = (Date) getIntent().getSerializableExtra(RUN_END_DATE);
        correctAnswers = (ArrayList<CorrectAnswers>) getIntent().getSerializableExtra("correctanswer");

        root = (StepSwitcherCustom) findViewById(R.id.container);

        if (savedInstanceState == null) {
            task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
            StudyId = (String) getIntent().getSerializableExtra(STUDYID);
            enrollId = (String) getIntent().getSerializableExtra(ENROLLID);
            eligibility = (String) getIntent().getSerializableExtra(ELIGIBILITY);
            type = (String) getIntent().getSerializableExtra(TYPE);
            taskResult = new TaskResult(task.getIdentifier());
            taskResult.setStartDate(new Date());
        } else {
            task = (Task) savedInstanceState.getSerializable(EXTRA_TASK);
            StudyId = (String) savedInstanceState.getSerializable(STUDYID);
            enrollId = (String) savedInstanceState.getSerializable(ENROLLID);
            eligibility = (String) savedInstanceState.getSerializable(ELIGIBILITY);
            type = (String) savedInstanceState.getSerializable(TYPE);
            taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
            currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
        }
        mActivityObject = (ActivityObj) getIntent().getSerializableExtra(EXTRA_SURVEYOBJ);

        task.validateParameters();


        if (currentStep == null) {
            currentStep = task.getStepAfterStep(null, taskResult);
        }

        showStep(currentStep);
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

    protected void showNextStep() {
        boolean eligible = checkStepResult(currentStep, taskResult);
        Step nextStep;
        if (eligible || currentStep.getIdentifier().equalsIgnoreCase("Eligibility Test")) {
            nextStep = task.getStepAfterStep(currentStep, taskResult);
            if (nextStep == null) {
                saveAndFinish();
            } else {
                showStep(nextStep);
            }
        } else {
            Intent intent = new Intent(this, NotEligibleActivity.class);
            intent.putExtra("studyId",""+StudyId);
            startActivity(intent);
            finish();
        }
    }

    private boolean checkStepResult(Step currentStep, TaskResult taskResult) {
        String answer = "";
        for (int i = 0; i < correctAnswers.size(); i++) {
            if (correctAnswers.get(i).getKey().equalsIgnoreCase(currentStep.getIdentifier())) {
                Map<String, StepResult> map = taskResult.getResults();
                for (Map.Entry<String, StepResult> pair : map.entrySet()) {
                    if (pair.getKey().equalsIgnoreCase(currentStep.getIdentifier())) {
                        try {
                            StepResult stepResult = pair.getValue();
                            Object o = stepResult.getResults().get("answer");
                            if (o instanceof Object[]) {
                                Object[] objects = (Object[]) o;
                                if (objects[0] instanceof String) {
                                    answer = "" + ((String) objects[0]);
                                } else if (objects[0] instanceof Integer) {
                                    answer = "" + ((int) objects[0]);
                                }
                            } else {
                                answer = "" + stepResult.getResults().get("answer");
                            }
                        } catch (Exception e) {
                            answer = "";
                            e.printStackTrace();
                        }
                    }
                    if (answer.equalsIgnoreCase(correctAnswers.get(i).getAnswer())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    protected void showPreviousStep() {
        Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
        if (previousStep == null) {
            finish();
        } else {
            showStep(previousStep);
        }
    }

    private void showStep(Step step) {
        //branching logic here
        currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult)
                .getCurrent();
        int newStepPosition = task.getProgressOfCurrentStep(step, taskResult).getCurrent();

        StepLayout stepLayout = getLayoutForStep(step);
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


        Intent intent = new Intent(this, EligibleActivity.class);
        intent.putExtra("studyId", StudyId);
        intent.putExtra("enrollId", enrollId);
        intent.putExtra("title", pdfTitle);
        intent.putExtra("eligibility", eligibility);
        intent.putExtra("type", type);
        startActivity(intent);
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
//            showConfirmExitDialog();
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
        outState.putSerializable(STUDYID, StudyId);
        outState.putSerializable(ENROLLID, enrollId);
        outState.putSerializable(ELIGIBILITY, eligibility);
        outState.putSerializable(TYPE, type);
        outState.putSerializable(EXTRA_STEP, currentStep);
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
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle).setTitle(R.string.app_name)
                .setMessage(R.string.exit_activity)
                .setPositiveButton(R.string.endtask, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        alertDialog.show();
    }

    @Override
    public void onCancelStep() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void setActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}

