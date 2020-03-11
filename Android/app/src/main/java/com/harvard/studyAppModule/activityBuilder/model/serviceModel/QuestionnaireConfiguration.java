package com.harvard.studyAppModule.activityBuilder.model.serviceModel;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/02/2017.
 */
public class QuestionnaireConfiguration extends RealmObject{
    private boolean branching;
    private boolean randomization;
    private String frequency;

    public boolean isBranching() {
        return branching;
    }

    public void setBranching(boolean branching) {
        this.branching = branching;
    }

    public boolean isRandomization() {
        return randomization;
    }

    public void setRandomization(boolean randomization) {
        this.randomization = randomization;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
