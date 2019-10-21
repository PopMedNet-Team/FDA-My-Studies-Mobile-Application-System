package com.harvard.userModule.webserviceModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 5/4/2017.
 */

public class ActivityData extends RealmObject {

    @PrimaryKey
    private String studyId;
    private String message;
    private RealmList<Activities> activities;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RealmList<Activities> getActivities() {
        return activities;
    }

    public void setActivities(RealmList<Activities> activities) {
        this.activities = activities;
    }
}
