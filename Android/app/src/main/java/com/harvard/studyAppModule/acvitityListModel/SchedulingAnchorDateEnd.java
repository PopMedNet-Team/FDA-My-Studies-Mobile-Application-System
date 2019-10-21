package com.harvard.studyAppModule.acvitityListModel;

import io.realm.RealmObject;

public class SchedulingAnchorDateEnd extends RealmObject {
    private int anchorDays;

    private int repeatInterval;

    private String time;

    public int getAnchorDays() {
        return anchorDays;
    }

    public void setAnchorDays(int anchorDays) {
        this.anchorDays = anchorDays;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
