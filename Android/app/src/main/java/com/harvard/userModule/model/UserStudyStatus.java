package com.harvard.userModule.model;

/**
 * Created by Rohit on 2/14/2017.
 */

public class UserStudyStatus {
    private String mStudyStatus;     // StudyStatus are Yet To Join, In Progress, Completed, Not Eligible, Withdrawn
    private boolean mBookmarked;
    private String mStudyId;

    public String getmStudyStatus() {
        return mStudyStatus;
    }

    public void setmStudyStatus(String mStudyStatus) {
        this.mStudyStatus = mStudyStatus;
    }

    public boolean ismBookmarked() {
        return mBookmarked;
    }

    public void setmBookmarked(boolean mBookmarked) {
        this.mBookmarked = mBookmarked;
    }

    public String getmStudyId() {
        return mStudyId;
    }

    public void setmStudyId(String mStudyId) {
        this.mStudyId = mStudyId;
    }
}
