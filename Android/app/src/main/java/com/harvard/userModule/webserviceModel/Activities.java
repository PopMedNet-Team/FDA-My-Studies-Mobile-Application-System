package com.harvard.userModule.webserviceModel;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/24/2017.
 */

public class Activities extends RealmObject {
//    @PrimaryKey
    private String activityId;

    private String activityVersion;

    private String studyId;

    private String activityState;

    private String activityRunId;

    private String bookmarked;

    private ActivityRunPreference activityRun;

    public ActivityRunPreference getActivityRun() {
        return activityRun;
    }

    public void setActivityRun(ActivityRunPreference activityRun) {
        this.activityRun = activityRun;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityVersion() {
        return activityVersion;
    }

    public void setActivityVersion(String activityVersion) {
        this.activityVersion = activityVersion;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getStatus() {
        return activityState;
    }

    public void setStatus(String status) {
        this.activityState = status;
    }

    public String getActivityRunId() {
        return activityRunId;
    }

    public void setActivityRunId(String activityRunId) {
        this.activityRunId = activityRunId;
    }

    public String getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(String bookmarked) {
        this.bookmarked = bookmarked;
    }
}
