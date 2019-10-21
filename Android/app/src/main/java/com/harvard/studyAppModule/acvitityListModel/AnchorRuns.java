package com.harvard.studyAppModule.acvitityListModel;

import io.realm.RealmObject;

public class AnchorRuns extends RealmObject {

    private int startDays;

    private int endDays;

    private String time;

    public int getStartDays() {
        return startDays;
    }

    public void setStartDays(int startDays) {
        this.startDays = startDays;
    }

    public int getEndDays() {
        return endDays;
    }

    public void setEndDays(int endDays) {
        this.endDays = endDays;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
