package com.harvard.studyAppModule.custom.question;

import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 05/05/2017.
 */

public class TaskInstructionAnswerFormat extends ChoiceAnswerFormatCustom {

    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private final String desc;


    public TaskInstructionAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle style, String desc) {
        this.style = style;
        this.desc = desc;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.TaskinstructionStep;
    }
}