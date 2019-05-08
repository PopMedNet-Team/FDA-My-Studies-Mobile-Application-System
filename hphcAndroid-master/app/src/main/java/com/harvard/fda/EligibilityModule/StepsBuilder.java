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

import android.content.Context;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.fda.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.fda.studyAppModule.custom.ChoiceAnswerFormatCustom;
import com.harvard.fda.studyAppModule.custom.QuestionStepCustom;
import com.harvard.fda.studyAppModule.custom.question.ChoiceCustomImage;
import com.harvard.fda.studyAppModule.custom.question.ChoiceTextExclusive;
import com.harvard.fda.studyAppModule.custom.question.MultiChoiceImageAnswerFormat;
import com.harvard.fda.studyAppModule.custom.question.MultiChoiceTextAnswerFormat;
import com.harvard.fda.studyAppModule.custom.question.SingleChoiceTextAnswerFormat;

import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Naveen Raj on 06/07/2017.
 */

public class StepsBuilder {
    private List<Step> steps;

    public StepsBuilder(Context context, RealmList<Steps> stepsRealmList, boolean addEligibility) {
        steps = createsurveyquestion(context, stepsRealmList, addEligibility);
    }

    public List<Step> getsteps() {
        return steps;
    }

    private List<Step> createsurveyquestion(Context context, RealmList<Steps> activityQuestionStep, boolean addEligibility) {
        ArrayList<Step> steps = new ArrayList<>();
        if (activityQuestionStep != null) {
            if (!addEligibility && activityQuestionStep.size() > 0) {
                InstructionStep instructionStep = new InstructionStep("Eligibility Test", "Eligibility Test", "Please answer the questions that follow to help ascertain your eligibility for this study.");
                instructionStep.setStepTitle(R.string.notxt);
                instructionStep.setOptional(false);
                steps.add(instructionStep);
            }

            for (int i = 0; i < activityQuestionStep.size(); i++) {
                if (activityQuestionStep.get(i).getType().equalsIgnoreCase("question")) {
                    switch (activityQuestionStep.get(i).getResultType()) {

                        case "imageChoice":
                            QuestionStepCustom multichoiceStep = new QuestionStepCustom(activityQuestionStep.get(i).getKey());

                            ChoiceCustomImage[] choicechoices = new ChoiceCustomImage[activityQuestionStep.get(i).getFormat().getImageChoices().size()];
                            for (int j = 0; j < activityQuestionStep.get(i).getFormat().getImageChoices().size(); j++) {
                                choicechoices[j] = new ChoiceCustomImage(activityQuestionStep.get(i).getFormat().getImageChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getImageChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getImageChoices().get(j).getImage(), activityQuestionStep.get(i).getFormat().getImageChoices().get(j).getSelectedImage());
                            }
                            MultiChoiceImageAnswerFormat multichoiceFormat = new MultiChoiceImageAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.MultipleImageChoice, choicechoices);
                            multichoiceStep.setTitle(activityQuestionStep.get(i).getTitle());
                            multichoiceStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            multichoiceStep.setAnswerFormat1(multichoiceFormat);
                            multichoiceStep.setStepTitle(R.string.notxt);
                            multichoiceStep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(multichoiceStep);
                            break;

                        case "textChoice":
                            if (activityQuestionStep.get(i).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                Choice[] choices = new Choice[activityQuestionStep.get(i).getFormat().getTextChoices().size()];
                                for (int j = 0; j < activityQuestionStep.get(i).getFormat().getTextChoices().size(); j++) {
                                    choices[j] = new Choice(activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getDetailtext());
                                }
                                SingleChoiceTextAnswerFormat choiceAnswerFormat = new SingleChoiceTextAnswerFormat(AnswerFormatCustom.CustomAnswerStyle.SingleTextChoice, choices);
                                QuestionStepCustom multiStep = new QuestionStepCustom(activityQuestionStep.get(i).getKey());

                                multiStep.setTitle(activityQuestionStep.get(i).getTitle());
                                multiStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                                multiStep.setAnswerFormat1(choiceAnswerFormat);
                                multiStep.setStepTitle(R.string.notxt);
                                multiStep.setOptional(activityQuestionStep.get(i).isSkippable());
                                steps.add(multiStep);
                            } else {
                                ChoiceTextExclusive[] choices = new ChoiceTextExclusive[activityQuestionStep.get(i).getFormat().getTextChoices().size()];
                                for (int j = 0; j < activityQuestionStep.get(i).getFormat().getTextChoices().size(); j++) {
                                    choices[j] = new ChoiceTextExclusive(activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getDetailtext(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).isExclusive());
                                }
                                MultiChoiceTextAnswerFormat choiceAnswerFormat = new MultiChoiceTextAnswerFormat(AnswerFormatCustom.CustomAnswerStyle.MultipleTextChoice, choices);
                                QuestionStepCustom multiStep = new QuestionStepCustom(activityQuestionStep.get(i).getKey());

                                multiStep.setTitle(activityQuestionStep.get(i).getTitle());
                                multiStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                                multiStep.setAnswerFormat1(choiceAnswerFormat);
                                multiStep.setStepTitle(R.string.notxt);
                                multiStep.setOptional(activityQuestionStep.get(i).isSkippable());
                                steps.add(multiStep);
                            }
                            break;

                        case "boolean":
                            QuestionStep booleanStep = new QuestionStep(activityQuestionStep.get(i).getKey());

                            booleanStep.setTitle(activityQuestionStep.get(i).getTitle());
                            booleanStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            booleanStep.setAnswerFormat(new BooleanAnswerFormat(context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no)));
                            booleanStep.setStepTitle(R.string.notxt);
                            booleanStep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(booleanStep);
                            break;
                    }
                }
            }
        }
        return steps;
    }
}
