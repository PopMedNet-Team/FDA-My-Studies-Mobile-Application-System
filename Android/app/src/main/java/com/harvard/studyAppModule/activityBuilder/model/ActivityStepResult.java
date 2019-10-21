package com.harvard.studyAppModule.activityBuilder.model;

import java.util.Date;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ActivityStepResult {
    private String type;
    private ActivityStep step;
    private Date startTime;
    private Date endTime;
    private String skipped;
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ActivityStep getStep() {
        return step;
    }

    public void setStep(ActivityStep step) {
        this.step = step;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSkipped() {
        return skipped;
    }

    public void setSkipped(String skipped) {
        this.skipped = skipped;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
