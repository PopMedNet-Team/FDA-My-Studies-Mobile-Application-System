package com.harvard.studyAppModule.custom.question;

import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 07/04/2017.
 */

public class SingleChoiceTextAnswerFormat<T> extends ChoiceAnswerFormatCustom {
    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private ChoiceText<T>[] choicechoices;

    public SingleChoiceTextAnswerFormat(CustomAnswerStyle style, ChoiceText[] choicechoices) {
        this.style = style;
        this.choicechoices = choicechoices;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public ChoiceText<T>[] getTextChoices() {
        return choicechoices;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.SingleTextChoice;
    }
}
