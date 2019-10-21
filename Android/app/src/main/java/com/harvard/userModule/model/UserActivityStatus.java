package com.harvard.userModule.model;

/**
 * Created by Rohit on 2/14/2017.
 */

public class UserActivityStatus {
    private String mActivityStatus;
    private boolean mBookmarked;
    private String mActivityId;
    private String mStudyId;
    private String mActivityVersion;

    public String getmActivityStatus() {
        return mActivityStatus;
    }

    public void setmActivityStatus(String mActivityStatus) {
        this.mActivityStatus = mActivityStatus;
    }

    public boolean ismBookmarked() {
        return mBookmarked;
    }

    public void setmBookmarked(boolean mBookmarked) {
        this.mBookmarked = mBookmarked;
    }

    public String getmActivityId() {
        return mActivityId;
    }

    public void setmActivityId(String mActivityId) {
        this.mActivityId = mActivityId;
    }

    public String getmStudyId() {
        return mStudyId;
    }

    public void setmStudyId(String mStudyId) {
        this.mStudyId = mStudyId;
    }

    public String getmActivityVersion() {
        return mActivityVersion;
    }

    public void setmActivityVersion(String mActivityVersion) {
        this.mActivityVersion = mActivityVersion;
    }
}
