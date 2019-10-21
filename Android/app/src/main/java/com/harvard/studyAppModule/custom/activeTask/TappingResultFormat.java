package com.harvard.studyAppModule.custom.activeTask;

import java.io.Serializable;

/**
 * Created by Naveen Raj on 11/06/2017.
 */

public class TappingResultFormat implements Serializable {
    private String duration;
    private double value;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
