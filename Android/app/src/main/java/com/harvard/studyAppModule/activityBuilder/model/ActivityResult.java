package com.harvard.studyAppModule.activityBuilder.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Rohit on 2/23/2017.
 */

public class ActivityResult {
    private String type;
    private Activity activity;
    private Date startTime;
    private Date endTime;
    private ArrayList<ActivityStepResult> result;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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

    public ArrayList<ActivityStepResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<ActivityStepResult> result) {
        this.result = result;
    }
}
