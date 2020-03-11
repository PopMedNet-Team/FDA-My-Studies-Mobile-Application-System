package com.harvard.studyAppModule.activityBuilder.model;

import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.studyAppModule.consent.model.CorrectAnswers;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Rohit on 2/23/2017.
 */
public class Eligibility extends RealmObject {
    private String type;
    private String tokenTitle;
    private RealmList<Steps> test;
    private RealmList<CorrectAnswers> correctAnswers;

    public String getTokenTitle() {
        return tokenTitle;
    }

    public void setTokenTitle(String tokenTitle) {
        this.tokenTitle = tokenTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RealmList<Steps> getTest() {
        return test;
    }

    public void setTest(RealmList<Steps> test) {
        this.test = test;
    }

    public RealmList<CorrectAnswers> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(RealmList<CorrectAnswers> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
