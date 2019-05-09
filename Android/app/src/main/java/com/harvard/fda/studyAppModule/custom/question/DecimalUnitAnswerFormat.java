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

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.custom.ChoiceAnswerFormatCustom;

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
