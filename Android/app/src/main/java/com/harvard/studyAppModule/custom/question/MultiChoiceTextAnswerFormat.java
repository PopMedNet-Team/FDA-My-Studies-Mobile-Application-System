package com.harvard.studyAppModule.custom.question;

import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 05/13/2017.
 */

public class MultiChoiceTextAnswerFormat<T> extends ChoiceAnswerFormatCustom {
    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private ChoiceTextExclusive<T>[] choicechoices;

    public MultiChoiceTextAnswerFormat(CustomAnswerStyle style, ChoiceTextExclusive[] choicechoices) {
        this.style = style;
        this.choicechoices = choicechoices;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public ChoiceTextExclusive<T>[] getTextChoices() {
        return choicechoices;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.MultipleTextChoice;
    }
}
