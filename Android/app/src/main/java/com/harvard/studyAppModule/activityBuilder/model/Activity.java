package com.harvard.studyAppModule.activityBuilder.model;

import java.util.Date;

/**
 * Created by Rohit on 2/23/2017.
 */

public class Activity {
    private String type;
    private String activityId;
    private String studyId;
    private String name;
    private String version;
    private Date lastModified;
    private int userStatus;
    private Date startDate;
    private Date endDate;
    private boolean branching;
    private boolean randomization;
    private Schedule schedule;
    private String steps;
    private ActivityResult result;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
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

    public boolean isBranching() {
        return branching;
    }

    public void setBranching(boolean branching) {
        this.branching = branching;
    }

    public boolean isRandomization() {
        return randomization;
    }

    public void setRandomization(boolean randomization) {
        this.randomization = randomization;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public ActivityResult getResult() {
        return result;
    }

    public void setResult(ActivityResult result) {
        this.result = result;
    }
}
