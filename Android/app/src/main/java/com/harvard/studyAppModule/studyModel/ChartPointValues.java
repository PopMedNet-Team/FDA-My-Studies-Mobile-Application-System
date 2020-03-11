package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class ChartPointValues extends RealmObject implements Serializable {
    private String pointValues;

    public String getPointValues() {
        return pointValues;
    }

    public void setPointValues(String pointValues) {
        this.pointValues = pointValues;
    }
}
