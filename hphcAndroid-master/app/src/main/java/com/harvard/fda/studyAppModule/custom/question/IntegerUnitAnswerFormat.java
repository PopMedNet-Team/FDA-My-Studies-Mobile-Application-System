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
