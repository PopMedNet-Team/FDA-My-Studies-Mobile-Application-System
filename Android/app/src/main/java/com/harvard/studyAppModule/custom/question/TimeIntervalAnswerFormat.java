package com.harvard.studyAppModule.custom.question;

import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 04/13/2017.
 */

public class TimeIntervalAnswerFormat extends ChoiceAnswerFormatCustom {

    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private final int step;
    private final String defaultvalue;


    public TimeIntervalAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle style, int step,String defaultvalue) {
        this.style = style;
        this.step = step;
        this.defaultvalue = defaultvalue;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public int getStep() {
        return step;
    }

    public String getDefaultvalue() {
        return defaultvalue;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.TimeInterval;
    }


}
