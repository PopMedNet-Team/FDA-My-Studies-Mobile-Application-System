package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;

public class AnchorDateStudyHome extends RealmObject {
    private QuestionInfoStudyHome questionInfo;

    private String type;

    public QuestionInfoStudyHome getQuestionInfo ()
    {
        return questionInfo;
    }

    public void setQuestionInfo (QuestionInfoStudyHome questionInfoStudyHome)
    {
        this.questionInfo = questionInfoStudyHome;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [questionInfo = "+questionInfo+", type = "+type+"]";
    }
}
