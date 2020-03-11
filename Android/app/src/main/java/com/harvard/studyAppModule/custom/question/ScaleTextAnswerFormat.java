package com.harvard.studyAppModule.custom.question;


import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 01/30/2017.
 */
public class ScaleTextAnswerFormat extends ChoiceAnswerFormatCustom {

    private final CustomAnswerStyle style;
    private final String defaultval;
    private final boolean vertical;
    private final ChoiceTextExclusive[] mChoiceTextExclusive;


    public ScaleTextAnswerFormat(CustomAnswerStyle style, ChoiceTextExclusive[] choiceTextExclusive, String defaultval, boolean vertical) {
        this.style = style;
        this.defaultval = defaultval;
        this.vertical = vertical;
        this.mChoiceTextExclusive = choiceTextExclusive;
    }

    public String getDefaultval() {
        return defaultval;
    }

    public boolean isVertical() {
        return vertical;
    }

    public ChoiceTextExclusive[] getChoiceTextExclusive() {
        return mChoiceTextExclusive;
    }

    @Override
    public QuestionType getQuestionType() {
        return Type.ScaleText;
    }
}
