package com.harvard.studyAppModule.activityBuilder.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Rohit on 4/13/2017.
 */

public class ActivityRun  extends RealmObject {
    private int runId;
    private Date startDate;
    private Date endDate;
    private String activityId;
    private String studyId;
    private boolean isCompleted;

    public int getRunId() {
        return runId;
    }

    public void setRunId(int runId) {
        this.runId = runId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
