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


import com.harvard.fda.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.fda.studyAppModule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 04/13/2017.
 */

public class ContinousScaleAnswerFormat extends ChoiceAnswerFormatCustom {

    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private final int maxfraction;
    private final int minValue;
    private final int maxValue;
    private final boolean vertical;
    private final String maxDesc;
    private final String minDesc;
    private final String maxImage;
    private final String minImage;
    private final String defaultval;


    public ContinousScaleAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle style, int maxfraction, int minValue, int maxValue, boolean vertical, String maxDesc, String minDesc, String maxImage, String minImage, String defaultval) {
        this.style = style;
        this.maxfraction = maxfraction;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.vertical = vertical;

        this.minImage = minImage;
        this.maxImage = maxImage;
        this.minDesc = minDesc;
        this.maxDesc = maxDesc;
        this.defaultval = defaultval;
    }

    public String getDefaultval() {
        return defaultval;
    }

    public String getMaxDesc() {
        return maxDesc;
    }

    public String getMinDesc() {
        return minDesc;
    }

    public String getMaxImage() {
        return maxImage;
    }

    public String getMinImage() {
        return minImage;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public int getMaxfraction() {
        return maxfraction;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public boolean isVertical() {
        return vertical;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.ContinousScale;
    }
}
