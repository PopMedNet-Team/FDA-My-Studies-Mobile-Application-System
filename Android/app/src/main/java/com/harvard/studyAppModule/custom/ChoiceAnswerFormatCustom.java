package com.harvard.studyAppModule.custom;

import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.QuestionStep;

import java.util.ArrayList;

/**
 * Created by Naveen Raj on 10/26/2016.
 */
public class ChoiceAnswerFormatCustom extends AnswerFormatCustom {
    private AnswerFormatCustom.CustomAnswerStyle answerStyle;
    private Choice[] choices;
    private QuestionStep mQuestionStep;
    private ArrayList<QuestionStep> mQuestionSteps;
    ArrayList<QuestionStep> questionStepArrayList;
    boolean repeatable;
    String repeattxt;

    /**
     * Creates an answer format with the specified answerStyle(single or multichoice) and collection
     * of choices.
     *
     * @param answerStyle either MultipleChoice or SingleChoice
     * @param choices     an array of {@link Choice} objects, all of the same type
     */
    public ChoiceAnswerFormatCustom(AnswerFormatCustom.CustomAnswerStyle answerStyle, QuestionStep questionStep, Choice... choices) {
        this.answerStyle = answerStyle;
        this.choices = choices.clone();
        mQuestionStep = questionStep;
    }

    public ChoiceAnswerFormatCustom() {
    }

    public ChoiceAnswerFormatCustom(CustomAnswerStyle form, QuestionStepCustom questionStep, ArrayList<QuestionStep> questionSteps, boolean repeatable, String repeattxt) {
        this.answerStyle = form;
        this.repeatable = repeatable;
        this.repeattxt = repeattxt;
        mQuestionStep = questionStep;
        mQuestionSteps = questionSteps;
    }

    /**
     * Returns a multiple choice or single choice question type, which will decide which {@link
     * org.researchstack.backbone.ui.step.body.StepBody} to use to display this question.
     *
     * @return the question type for this answer format
     */

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        if (answerStyle == CustomAnswerStyle.MultipleImageChoice)
            return Type.MultipleImageChoice;
        else if (answerStyle == CustomAnswerStyle.MultipleTextChoice)
            return Type.MultipleTextChoice;
        else if (answerStyle == CustomAnswerStyle.SingleTextChoice)
            return Type.SingleTextChoice;
        else if (answerStyle == CustomAnswerStyle.Audio)
            return Type.Audio;
        else if (answerStyle == CustomAnswerStyle.Tapping)
            return Type.Tapping;
        else if (answerStyle == CustomAnswerStyle.stepcount)
            return Type.stepcount;
        else if (answerStyle == CustomAnswerStyle.valuePicker)
            return Type.valuePicker;
        else if (answerStyle == CustomAnswerStyle.Scale)
            return Type.Scale;
//        else if (answerStyle == CustomAnswerStyle.TimeofDay)
//            return Type.TimeofDay;
        else if (answerStyle == CustomAnswerStyle.Location)
            return Type.Location;
        else if (answerStyle == CustomAnswerStyle.Form)
            return Type.Form;
        else if (answerStyle == CustomAnswerStyle.ContinousScale)
            return Type.ContinousScale;
        else if (answerStyle == CustomAnswerStyle.TimeInterval)
            return Type.TimeInterval;
        else if (answerStyle == CustomAnswerStyle.Height)
            return Type.Height;
        else if (answerStyle == CustomAnswerStyle.TextRegex)
            return Type.TextRegex;
        else if (answerStyle == CustomAnswerStyle.TaskIntroStep)
            return Type.TaskIntroStep;
        else if (answerStyle == CustomAnswerStyle.TaskinstructionStep)
            return Type.TaskinstructionStep;
        else if (answerStyle == CustomAnswerStyle.Integer)
            return Type.Integer;
        else if (answerStyle == CustomAnswerStyle.Decimal)
            return Type.Decimal;
        return Type.None;
    }


    /**
     * Returns a copy of the choice array
     *
     * @return a copy of the choices for this question
     */
    public Choice[] getChoices() {
        return choices.clone();
    }

    public QuestionStep getquestiontype() {
        return mQuestionStep;
    }

    public ArrayList<QuestionStep> getformquestions() {
        return mQuestionSteps;
    }

    public void savetempformlist(ArrayList<QuestionStep> questionSteps) {
        mQuestionSteps = questionSteps;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public String getRepeattxt() {
        return repeattxt;
    }
}
