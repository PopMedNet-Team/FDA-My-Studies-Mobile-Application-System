package com.harvard.studyAppModule.studyModel;

import java.util.Date;

/**
 * Created by Naveen Raj on 06/13/2017.
 */

public class RunChart {
    private Date completedDate;
    private Date startDate;
    private Date enddDate;
    private String result;
    private String resultData;
    private String runId;

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEnddDate() {
        return enddDate;
    }

    public void setEnddDate(Date enddDate) {
        this.enddDate = enddDate;
    }
}
