/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.fda.studyAppModule.survayScheduler.model;

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
