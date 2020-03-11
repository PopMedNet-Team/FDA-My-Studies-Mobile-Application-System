package com.harvard.studyAppModule.activityBuilder.model.serviceModel;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/02/2017.
 */
public class ActivityObj extends RealmObject {
    private String type;
    private Info metadata;
    private String surveyId;
    private String studyId;
    private RealmList<Steps> steps;


    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Info getMetadata() {
        return metadata;
    }

    public void setMetadata(Info metadata) {
        this.metadata = metadata;
    }


    public RealmList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(RealmList<Steps> steps) {
        this.steps = steps;
    }

}
