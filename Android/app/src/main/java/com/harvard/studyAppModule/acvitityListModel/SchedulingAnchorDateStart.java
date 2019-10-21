package com.harvard.studyAppModule.acvitityListModel;

import io.realm.RealmObject;

public class SchedulingAnchorDateStart extends RealmObject {
    private int anchorDays;

    private int dayOfWeek;

    private String dateOfMonth;

    private String time;

    public int getAnchorDays() {
        return anchorDays;
    }

    public void setAnchorDays(int anchorDays) {
        this.anchorDays = anchorDays;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDateOfMonth() {
        return dateOfMonth;
    }

    public void setDateOfMonth(String dateOfMonth) {
        this.dateOfMonth = dateOfMonth;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
