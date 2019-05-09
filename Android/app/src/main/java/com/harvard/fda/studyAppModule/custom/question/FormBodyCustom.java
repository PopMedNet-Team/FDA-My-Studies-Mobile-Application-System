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

package com.harvard.fda.studyAppModule.custom.question;

import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.activityBuilder.CustomSurveyViewTaskActivity;
import com.harvard.fda.studyAppModule.custom.ChoiceAnswerFormatCustom;
import com.harvard.fda.studyAppModule.custom.QuestionStepCustom;
import com.harvard.fda.studyAppModule.custom.StepResultCustom;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.ui.views.ObservableScrollView;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Naveen Raj on 02/02/2017.
 */
public class FormBodyCustom implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    private QuestionStepCustom step1;
    private StepResult<StepResult> result;
    private ChoiceAnswerFormatCustom format;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private List<StepBody> formStepChildren;
    private ArrayList<QuestionStep> questionSteps;
    private ArrayList<QuestionStep> tempquestionSteps;
    int j = 0;
    int formIncrement = 0;
    int actionBarHeight = 0;
    int navigationBarHeight = 0;

    public FormBodyCustom(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (ChoiceAnswerFormatCustom) ((QuestionStepCustom) step).getAnswerFormat1();
        tempquestionSteps = new ArrayList<>();
        if (result == null) {
            questionSteps = format.getformquestions();
            formIncrement = questionSteps.size();
        } else {
            ArrayList<QuestionStep> questionStepsDynamic = new ArrayList<>();
            questionSteps = format.getformquestions();
            formIncrement = questionSteps.size();
            int size = questionSteps.size();
            boolean looping = true;
            while (looping) {
                boolean b = true;
                for (int i = 0; i < size; i++) {
                    try {
                        if (questionSteps.get(i).getIdentifier().contains("_addMoreEnabled")) {
                            b = false;
                            questionSteps.remove(i);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                if (b)
                    break;
            }


            Map<String, StepResult> map = result.getResults();
            for (Map.Entry<String, StepResult> entry : map.entrySet()) {
                for (int j = 0; j < questionSteps.size(); j++) {
                    if (entry.getKey().contains("_addMoreEnabled") && entry.getKey().substring(0, entry.getKey().lastIndexOf("-")).equalsIgnoreCase(questionSteps.get(j).getIdentifier())) {
                        Log.e("entry.getKey()--=====", "" + entry.getKey());
                        if (questionSteps.get(j) instanceof QuestionStepCustom) {
                            QuestionStepCustom questionStep1 = new QuestionStepCustom(entry.getKey(), questionSteps.get(j).getTitle());
                            questionStep1.setAnswerFormat1(((QuestionStepCustom) questionSteps.get(j)).getAnswerFormat1());
                            questionStep1.setOptional(questionSteps.get(j).isOptional());
                            questionStep1.setStepTitle(questionSteps.get(j).getStepTitle());
                            questionStep1.setTitle(questionSteps.get(j).getTitle());
                            questionStep1.setText(questionSteps.get(j).getText());
                            questionStep1.setPlaceholder(questionSteps.get(j).getPlaceholder());
                            questionStep1.setStepLayoutClass(questionSteps.get(j).getStepLayoutClass());
                            questionStepsDynamic.add(questionStep1);
                        } else {
                            QuestionStep questionStep2 = new QuestionStep(entry.getKey(), questionSteps.get(j).getTitle());
                            questionStep2.setAnswerFormat(questionSteps.get(j).getAnswerFormat());
                            questionStep2.setOptional(questionSteps.get(j).isOptional());
                            questionStep2.setStepTitle(questionSteps.get(j).getStepTitle());
                            questionStep2.setTitle(questionSteps.get(j).getTitle());
                            questionStep2.setText(questionSteps.get(j).getText());
                            questionStep2.setPlaceholder(questionSteps.get(j).getPlaceholder());
                            questionStep2.setStepLayoutClass(questionSteps.get(j).getStepLayoutClass());
                            questionStepsDynamic.add(questionStep2);
                        }
                    }
                }
            }
            questionSteps.addAll(questionStepsDynamic);
        }
        j = questionSteps.size();
        this.step1 = (QuestionStepCustom) step;
    }

    @Override
    public View getBodyView(int viewType, final LayoutInflater inflater, final ViewGroup parent) {

        DisplayMetrics displayMetrics = inflater.getContext().getResources().getDisplayMetrics();
        final int height = displayMetrics.heightPixels;
        final LinearLayout body = (LinearLayout) inflater.inflate(R.layout.formbody, parent, false);
        final ObservableScrollView observableScrollView = (ObservableScrollView) parent.findViewById(R.id.rsb_content_container_scrollview);

        Resources resources = inflater.getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }

        TypedValue tv = new TypedValue();

        if (inflater.getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, inflater.getContext().getResources().getDisplayMetrics());
        }


        formStepChildren = new ArrayList<>(questionSteps.size());
        for (QuestionStep questionStep : questionSteps) {
            StepBody stepBody = createStepBody(questionStep, questionStep.getStepBodyClass());
            View bodyView = stepBody.getBodyView(VIEW_TYPE_COMPACT, inflater, body);
            body.addView(bodyView);
            formStepChildren.add(stepBody);
            tempquestionSteps.add(questionStep);
        }

        if (result != null) {
            questionSteps = format.getformquestions();
        }


        final TextView addmore = new TextView(inflater.getContext());
        addmore.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        addmore.setLayoutParams(layoutParams);
        addmore.setText(format.getRepeattxt());
        addmore.setTextColor(inflater.getContext().getResources().getColor(R.color.colorPrimary));
        addmore.setPaintFlags(addmore.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (format.isRepeatable()) {
            addmore.setVisibility(View.VISIBLE);
        } else {
            addmore.setVisibility(View.GONE);
        }
        final Handler handler = new Handler();

        addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View firstview = null;
                body.removeView(addmore);
                StepBody stepBody;
                for (int i = 0; i < questionSteps.size(); i++) {
                    if (!questionSteps.get(i).getIdentifier().contains("_addMoreEnabled")) {
                        if (questionSteps.get(i) instanceof QuestionStepCustom) {
                            QuestionStepCustom questionStep1 = new QuestionStepCustom(questionSteps.get(i).getIdentifier() + "-" + j + "_addMoreEnabled", questionSteps.get(i).getTitle());
                            questionStep1.setAnswerFormat1(((QuestionStepCustom) questionSteps.get(i)).getAnswerFormat1());
                            questionStep1.setOptional(questionSteps.get(i).isOptional());
                            questionStep1.setStepTitle(questionSteps.get(i).getStepTitle());
                            questionStep1.setTitle(questionSteps.get(i).getTitle());
                            questionStep1.setText(questionSteps.get(i).getText());
                            questionStep1.setPlaceholder(questionSteps.get(i).getPlaceholder());
                            questionStep1.setStepLayoutClass(questionSteps.get(i).getStepLayoutClass());
                            stepBody = createStepBody(questionStep1, questionSteps.get(i).getStepBodyClass());
                            tempquestionSteps.add(questionStep1);
                        } else {
                            QuestionStep questionStep2 = new QuestionStep(questionSteps.get(i).getIdentifier() + "-" + j + "_addMoreEnabled", questionSteps.get(i).getTitle());
                            questionStep2.setAnswerFormat(questionSteps.get(i).getAnswerFormat());
                            questionStep2.setOptional(questionSteps.get(i).isOptional());
                            questionStep2.setStepTitle(questionSteps.get(i).getStepTitle());
                            questionStep2.setTitle(questionSteps.get(i).getTitle());
                            questionStep2.setText(questionSteps.get(i).getText());
                            questionStep2.setPlaceholder(questionSteps.get(i).getPlaceholder());
                            questionStep2.setStepLayoutClass(questionSteps.get(i).getStepLayoutClass());
                            stepBody = createStepBody(questionStep2, questionSteps.get(i).getStepBodyClass());
                            tempquestionSteps.add(questionStep2);
                        }
                        ((CustomSurveyViewTaskActivity) inflater.getContext()).addformquestion(tempquestionSteps.get(tempquestionSteps.size() - 1), questionSteps.get(i).getIdentifier(), step.getIdentifier());

                        final View bodyView = stepBody.getBodyView(VIEW_TYPE_COMPACT, inflater, body);

                        body.addView(bodyView);


                        formStepChildren.add(stepBody);
                    }
                }
                j = formIncrement + j;
                Log.e("size then", "" + j + "   " + formIncrement);
                body.addView(addmore);
                body.invalidate();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        observableScrollView.smoothScrollBy(0, height - (actionBarHeight * 2) - navigationBarHeight);
                    }
                }, 100);

            }
        });


        body.addView(addmore);

        return body;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        StepResultCustom stepResultCustom = new StepResultCustom(step);
        for (StepBody child : formStepChildren) {
            StepResult childResult = child.getStepResult(skipped);
            if (childResult != null) {
                result.setResultForIdentifier(childResult.getIdentifier(), childResult);
                stepResultCustom.setResultForIdentifier(childResult.getIdentifier(), childResult);
            }
        }
        result.setResults(stepResultCustom.getResults());
        format.savetempformlist(tempquestionSteps);
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {

        for (int i = 0; i < formStepChildren.size(); i++) {
            BodyAnswer bodyAnswer = formStepChildren.get(i).getBodyAnswerState();
            StepResult childResult = formStepChildren.get(i).getStepResult(false);
            Log.e("bodyAnswer", "" + bodyAnswer.isValid() + "    " + tempquestionSteps.get(i).isOptional() + "  " + childResult.getResults().get("answer"));
            boolean valid = false;
            if (bodyAnswer.isValid()) {
                valid = true;
            } else {
                if (tempquestionSteps.get(i).isOptional()) {
                    //if answered invalid
                    //else valid
                    String answer = null;
                    Object o = childResult.getResults().get("answer");
                    if (o instanceof Object[]) {
                        Object[] objects = (Object[]) o;
                        if (objects.length > 0) {
                            if (objects[0] instanceof String) {
                                answer = "" + ((String) objects[0]);
                            } else if (objects[0] instanceof Integer) {
                                answer = "" + ((int) objects[0]);
                            }
                        }
                    } else {
                        answer = "" + childResult.getResults().get("answer");
                    }
                    if (answer == null || answer.equalsIgnoreCase("null")) {
                        answer = "";
                    }
                    if (answer.equalsIgnoreCase("")) {
                        valid = true;
                    }
                } else {
                    valid = false;
                }
            }
            if (!valid) {
                return bodyAnswer;
            }
        }
        return BodyAnswer.VALID;
    }

    @NonNull
    private StepBody createStepBody(QuestionStep questionStep, Class<?> stepBodyClass) {
        StepResult childResult = null;
        try {

            childResult = (StepResult) result.getResultForIdentifier(questionStep.getIdentifier());
        } catch (Exception e) {
            e.printStackTrace();
            childResult = null;
        }
        try {
            Constructor constructor = stepBodyClass.getConstructor(Step.class, StepResult.class);
            return (StepBody) constructor.newInstance(questionStep, childResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

