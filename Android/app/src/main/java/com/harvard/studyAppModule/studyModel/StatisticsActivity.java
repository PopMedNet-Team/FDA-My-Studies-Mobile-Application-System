package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class StatisticsActivity extends RealmObject implements Serializable{
    private String activityId;
    private String version;

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
