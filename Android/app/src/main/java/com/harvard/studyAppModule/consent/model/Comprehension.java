package com.harvard.studyAppModule.consent.model;

import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class Comprehension extends RealmObject {
    private RealmList<ComprehensionCorrectAnswers> correctAnswers;

    private RealmList<Steps> questions;

    private String passScore;

    public String getPassScore() {
        return passScore;
    }

    public void setPassScore(String passScore) {
        this.passScore = passScore;
    }

    public RealmList<ComprehensionCorrectAnswers> getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(RealmList<ComprehensionCorrectAnswers> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public RealmList<Steps> getQuestions() {
        return questions;
    }

    public void setQuestions(RealmList<Steps> questions) {
        this.questions = questions;
    }
}
