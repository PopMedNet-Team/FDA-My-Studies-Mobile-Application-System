package com.harvard.studyAppModule.survayScheduler.model;

/**
 * Created by Rohit on 5/7/2017.
 */

public class CompletionAdeherenceCalc {
    private double completion;
    private double adherence;
    private boolean noCompletedAndMissed;
    private boolean activityAvailable;

    public boolean isNoCompletedAndMissed() {
        return noCompletedAndMissed;
    }

    public void setNoCompletedAndMissed(boolean noCompletedAndMissed) {
        this.noCompletedAndMissed = noCompletedAndMissed;
    }

    public boolean isActivityAvailable() {
        return activityAvailable;
    }

    public void setActivityAvailable(boolean activityAvailable) {
        this.activityAvailable = activityAvailable;
    }

    public double getCompletion() {
        return completion;
    }

    public void setCompletion(double completion) {
        this.completion = completion;
    }

    public double getAdherence() {
        return adherence;
    }

    public void setAdherence(double adherence) {
        this.adherence = adherence;
    }
}
