package com.harvard.studyAppModule.custom.question;

import com.harvard.R;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.utils.TextUtils;

/**
 * Created by Naveen Raj on 06/16/2017.
 */

public class IntegerUnitAnswerFormat extends ChoiceAnswerFormatCustom {
    private int maxValue;
    private int minValue;
    private String unit;

    /**
     * Creates an integer answer format with the specified min and max values.
     *
     * @param minValue minimum allowed value
     * @param maxValue maximum allowed value, 0 if no max
     */
    public IntegerUnitAnswerFormat(int minValue, int maxValue,String unit)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.unit = unit;
    }

    @Override
    public QuestionType getQuestionType()
    {
        return Type.Integer;
    }

    /**
     * Returns the maximum allowed value for the question, 0 if no max
     *
     * @return the max value, 0 if no max
     */
    public int getMaxValue()
    {
        return maxValue;
    }

    /**
     * Returns the minimum allowed value for the question
     *
     * @return returns the minimum allowed value for the question
     */
    public int getMinValue()
    {
        return minValue;
    }

    public String getUnit() {
        return unit;
    }

    public BodyAnswer validateAnswer(String inputString)
    {

        // If no answer is recorded
        if(TextUtils.isEmpty(inputString) || inputString.equalsIgnoreCase("-"))
        {
            return BodyAnswer.INVALID;
        }
        else
        {
            // Parse value from editText
            Integer intAnswer = Integer.valueOf(inputString);
            if(intAnswer < getMinValue())
            {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_under,
                        String.valueOf(getMinValue()));
            }

            else if(intAnswer > getMaxValue())
            {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_over,
                        String.valueOf(getMaxValue()));
            }

        }

        return BodyAnswer.VALID;
    }
}
