package com.harvard.studyAppModule.custom.question;

import com.harvard.R;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.utils.TextUtils;

/**
 * Created by Naveen Raj on 06/16/2017.
 */

public class DecimalUnitAnswerFormat extends ChoiceAnswerFormatCustom {
    private float minValue;
    private float maxValue;
    private String unit;

    /**
     * Creates an answer format with the specified min and max values
     *
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value, or 0f for unlimited
     */
    public DecimalUnitAnswerFormat(float minValue, float maxValue, String unit) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.unit = unit;
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.Decimal;
    }

    /**
     * Returns the min value
     *
     * @return returns the min value
     */
    public float getMinValue() {
        return minValue;
    }

    /**
     * Returns the max value, or 0f for no maximum
     *
     * @return returns the max value, or 0f for no maximum
     */
    public float getMaxValue() {
        return maxValue;
    }


    public String getUnit() {
        return unit;
    }

    public BodyAnswer validateAnswer(String inputString) {
        // If no answer is recorded
        if (inputString == null || TextUtils.isEmpty(inputString) || inputString.equalsIgnoreCase("-") || inputString.equalsIgnoreCase(".")) {
            return BodyAnswer.INVALID;
        } else {
            // Parse value from editText
            Float floatAnswer = Float.valueOf(inputString);
            if (floatAnswer < minValue) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_under, String.valueOf(getMinValue()));
            } else if (floatAnswer > maxValue) {
                return new BodyAnswer(false,
                        R.string.rsb_invalid_answer_integer_over, String.valueOf(getMaxValue()));
            }
        }

        return BodyAnswer.VALID;
    }
}
