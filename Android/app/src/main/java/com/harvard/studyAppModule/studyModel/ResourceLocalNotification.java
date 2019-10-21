package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 6/9/2017.
 */

public class ResourceLocalNotification extends RealmObject {
    private String studyId;
    private String activityId;
    boolean resourceNotificationSet;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public boolean isResourceNotificationSet() {
        return resourceNotificationSet;
    }

    public void setResourceNotificationSet(boolean resourceNotificationSet) {
        this.resourceNotificationSet = resourceNotificationSet;
    }
}
