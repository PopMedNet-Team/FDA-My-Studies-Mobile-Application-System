package com.harvard.studyAppModule.custom.question;

import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 04/14/2017.
 */

public class HeightAnswerFormat extends ChoiceAnswerFormatCustom {

    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private final String placeholder;
    private final String measurementSystem;


    public HeightAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle style, String placeholder, String measurementSystem) {
        this.style = style;
        this.placeholder = placeholder;
        this.measurementSystem = measurementSystem;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getMeasurementSystem() {
        return measurementSystem;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.Height;
    }
}
