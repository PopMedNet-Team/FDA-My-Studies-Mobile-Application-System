package com.harvard.studyAppModule.custom.question;

import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;

import org.researchstack.backbone.utils.TextUtils;

/**
 * Created by Naveen Raj on 08/01/2017.
 */

public class EmailAnswerFormatCustom extends ChoiceAnswerFormatCustom {
    private int MAX_EMAIL_LENGTH;

    public EmailAnswerFormatCustom(int maxEmailLength) {
        this.MAX_EMAIL_LENGTH = maxEmailLength;
    }

    public boolean isAnswerValid(String text) {
        return TextUtils.isValidEmail(text);
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.Email;
    }

    public int getMaxEmailLength() {
        return MAX_EMAIL_LENGTH;
    }
}