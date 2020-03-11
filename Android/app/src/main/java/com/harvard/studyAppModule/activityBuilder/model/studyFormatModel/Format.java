package com.harvard.studyAppModule.activityBuilder.model.studyFormatModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rohit on 2/23/2017.
 */

public class Format {
    private int maxValue;
    private int minValue;
    @SerializedName("default")
    private int defaultQue;

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getDefaultQue() {
        return defaultQue;
    }

    public void setDefaultQue(int defaultQue) {
        this.defaultQue = defaultQue;
    }
}
