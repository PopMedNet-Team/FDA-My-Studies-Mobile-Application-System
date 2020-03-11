package com.harvard.EligibilityModule;

import android.content.Context;

import com.harvard.R;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;
import com.harvard.studyAppModule.custom.QuestionStepCustom;
import com.harvard.studyAppModule.custom.question.ChoiceCustomImage;
import com.harvard.studyAppModule.custom.question.ChoiceText;
import com.harvard.studyAppModule.custom.question.ChoiceTextExclusive;
import com.harvard.studyAppModule.custom.question.MultiChoiceImageAnswerFormat;
import com.harvard.studyAppModule.custom.question.MultiChoiceTextAnswerFormat;
import com.harvard.studyAppModule.custom.question.SingleChoiceTextAnswerFormat;

import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
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
                                ChoiceText[] choices = new ChoiceText[activityQuestionStep.get(i).getFormat().getTextChoices().size()];
                                for (int j = 0; j < activityQuestionStep.get(i).getFormat().getTextChoices().size(); j++) {
                                    choices[j] = new ChoiceText(activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getDetail(),null);
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
                                    choices[j] = new ChoiceTextExclusive(activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getDetail(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).isExclusive(),null);
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
