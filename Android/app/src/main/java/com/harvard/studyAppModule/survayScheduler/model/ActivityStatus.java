package com.harvard.studyAppModule.survayScheduler.model;

import java.util.Date;

/**
 * Created by Rohit on 4/19/2017.
 */

public class ActivityStatus {
    private String status;
    private int currentRunId;
    private int totalRun;
    private int missedRun;
    private int completedRun;
    private boolean runIdAvailable;
    private Date currentRunStartDate;
    private Date currentRunEndDate;

    public Date getCurrentRunStartDate() {
        return currentRunStartDate;
    }

    public void setCurrentRunStartDate(Date currentRunStartDate) {
        this.currentRunStartDate = currentRunStartDate;
    }

    public Date getCurrentRunEndDate() {
        return currentRunEndDate;
    }

    public void setCurrentRunEndDate(Date currentRunEndDate) {
        this.currentRunEndDate = currentRunEndDate;
    }

    public boolean isRunIdAvailable() {
        return runIdAvailable;
    }

    public void setRunIdAvailable(boolean runIdAvailable) {
        this.runIdAvailable = runIdAvailable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCurrentRunId() {
        return currentRunId;
    }

    public void setCurrentRunId(int currentRunId) {
        this.currentRunId = currentRunId;
    }

    public int getTotalRun() {
        return totalRun;
    }

    public void setTotalRun(int totalRun) {
        this.totalRun = totalRun;
    }

    public int getMissedRun() {
        return missedRun;
    }

    public void setMissedRun(int missedRun) {
        this.missedRun = missedRun;
    }

    public int getCompletedRun() {
        return completedRun;
    }

    public void setCompletedRun(int completedRun) {
        this.completedRun = completedRun;
    }
}
