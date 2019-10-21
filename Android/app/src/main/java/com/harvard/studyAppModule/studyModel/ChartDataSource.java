package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class ChartDataSource extends RealmObject implements Serializable{
    private String type;
    private String key;
    private String timeRangeType;
    private String startTime;
    private String endTime;
    private ChartDataSourceActivity activity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTimeRangeType() {
        return timeRangeType;
    }

    public void setTimeRangeType(String timeRangeType) {
        this.timeRangeType = timeRangeType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ChartDataSourceActivity getActivity() {
        return activity;
    }

    public void setActivity(ChartDataSourceActivity activity) {
        this.activity = activity;
    }
}
