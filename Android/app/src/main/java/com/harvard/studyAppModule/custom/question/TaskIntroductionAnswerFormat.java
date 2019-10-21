package com.harvard.studyAppModule.custom.question;

import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 05/05/2017.
 */

public class TaskIntroductionAnswerFormat extends ChoiceAnswerFormatCustom {

    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private final int drawable;
    private final int title;
    private final int desc;


    public TaskIntroductionAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle style, int drawable, int title, int desc) {
        this.style = style;
        this.drawable = drawable;
        this.title = title;
        this.desc = desc;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getTitle() {
        return title;
    }

    public int getDesc() {
        return desc;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.TaskIntroStep;
    }


}