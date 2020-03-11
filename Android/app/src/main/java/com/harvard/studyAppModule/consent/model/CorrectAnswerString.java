package com.harvard.studyAppModule.consent.model;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 08/23/2017.
 */

public class CorrectAnswerString extends RealmObject{
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
