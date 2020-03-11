package com.harvard.studyAppModule.activityBuilder.model.serviceModel;

/**
 * Created by Naveen Raj on 04/11/2017.
 */

public class ActivityInfoData {
    private String message;
    private ActivityObj activity;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ActivityObj getActivity() {
        return activity;
    }

    public void setActivity(ActivityObj activity) {
        this.activity = activity;
    }
}
