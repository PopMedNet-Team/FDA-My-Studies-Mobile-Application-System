package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class ChartSetting extends RealmObject implements Serializable{
    private String barColor;
    private int numberOfPoints;
    private RealmList<ChartPointValues> pointValues;

    public String getBarColor() {
        return barColor;
    }

    public void setBarColor(String barColor) {
        this.barColor = barColor;
    }

    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    public RealmList<ChartPointValues> getPointValues() {
        return pointValues;
    }

    public void setPointValues(RealmList<ChartPointValues> pointValues) {
        this.pointValues = pointValues;
    }
}
