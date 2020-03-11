package com.harvard.studyAppModule.consent.ConsentSharingStepCustom;

import org.researchstack.backbone.step.QuestionStep;

/**
 * Created by Naveen Raj on 09/04/2017.
 */

public class ConsentSharingStepCustom extends QuestionStep {

    String mLoadmoretxt;

    public ConsentSharingStepCustom(String identifier, String loadmoretxt) {
        super(identifier);
        mLoadmoretxt = loadmoretxt;
        setOptional(false);
    }

    @Override
    public Class getStepBodyClass() {
        return SingleChoiceSharingStepBody.class;
    }

    public String getLoadmoretxt() {
        return mLoadmoretxt;
    }
}
