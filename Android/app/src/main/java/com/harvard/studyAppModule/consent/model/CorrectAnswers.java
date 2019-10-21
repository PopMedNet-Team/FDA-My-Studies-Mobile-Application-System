package com.harvard.studyAppModule.consent.model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class CorrectAnswers extends RealmObject implements Serializable{
    private String answer;

    private String key;

    private String evaluation;

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
