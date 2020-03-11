package com.harvard.studyAppModule.custom;

import android.content.Context;

import org.researchstack.backbone.ui.step.body.BodyAnswer;

/**
 * Created by Naveen Raj on 09/06/2017.
 */

public class BodyAnswerCustom extends BodyAnswer {
    String reason;

    public BodyAnswerCustom(boolean isValid, String reason, String... params) {
        super(isValid, 0, params);
        this.reason = reason;
    }

    @Override
    public String getString(Context context) {
        return reason;
    }
}