package com.harvard.studyAppModule.consent.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 08/23/2017.
 */

public class ComprehensionCorrectAnswers extends RealmObject {
    private RealmList<CorrectAnswerString> answer;

    private String key;

    private String evaluation;

    public RealmList<CorrectAnswerString> getAnswer() {
        return answer;
    }

    public void setAnswer(RealmList<CorrectAnswerString> answer) {
        this.answer = answer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }
}
