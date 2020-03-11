package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/13/2017.
 */

public class StudyUpdateData extends RealmObject {
    private boolean consent;
    private boolean activities;
    private boolean resources;
    private boolean info;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isConsent() {
        return consent;
    }

    public void setConsent(boolean consent) {
        this.consent = consent;
    }

    public boolean isActivities() {
        return activities;
    }

    public void setActivities(boolean activities) {
        this.activities = activities;
    }

    public boolean isResources() {
        return resources;
    }

    public void setResources(boolean resources) {
        this.resources = resources;
    }

    public boolean isInfo() {
        return info;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }
}
