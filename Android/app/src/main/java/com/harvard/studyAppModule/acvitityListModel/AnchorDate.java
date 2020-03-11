package com.harvard.studyAppModule.acvitityListModel;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 04/06/2017.
 */

public class AnchorDate  extends RealmObject {
    private QuestionInfo questionInfo;

    private String type;

    public QuestionInfo getQuestionInfo() {
        return questionInfo;
    }

    public void setQuestionInfo(QuestionInfo questionInfo) {
        this.questionInfo = questionInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
