package com.harvard.studyAppModule.custom.question;


import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

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
