package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class StatisticsDataSource extends RealmObject implements Serializable{
    private String type;
    private String key;
    private StatisticsActivity activity;

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

    public StatisticsActivity getActivity() {
        return activity;
    }

    public void setActivity(StatisticsActivity activity) {
        this.activity = activity;
    }
}
