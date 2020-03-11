package com.harvard.userModule.model;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/14/2017.
 */

public class User {
    private String mFirstName;
    private String mLastName;
    private String mEmailId;
    private String mUserId;
    private boolean mVerified;
    private String mAuthToken;
    private String mUserType;
    private Settings mSettings;
    private ArrayList<UserStudyStatus> mUserStudyStatuses = new ArrayList<>();
    private ArrayList<UserActivityStatus> mUserActivityStatuses = new ArrayList<>();

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmEmailId() {
        return mEmailId;
    }

    public void setmEmailId(String mEmailId) {
        this.mEmailId = mEmailId;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public boolean ismVerified() {
        return mVerified;
    }

    public void setmVerified(boolean mVerified) {
        this.mVerified = mVerified;
    }

    public String getmAuthToken() {
        return mAuthToken;
    }

    public void setmAuthToken(String mAuthToken) {
        this.mAuthToken = mAuthToken;
    }

    public String getmUserType() {
        return mUserType;
    }

    public void setmUserType(String mUserType) {
        this.mUserType = mUserType;
    }

    public Settings getmSettings() {
        return mSettings;
    }

    public void setmSettings(Settings mSettings) {
        this.mSettings = mSettings;
    }

    public ArrayList<UserStudyStatus> getmUserStudyStatuses() {
        return mUserStudyStatuses;
    }

    public void setmUserStudyStatuses(ArrayList<UserStudyStatus> mUserStudyStatuses) {
        this.mUserStudyStatuses = mUserStudyStatuses;
    }

    public ArrayList<UserActivityStatus> getmUserActivityStatuses() {
        return mUserActivityStatuses;
    }

    public void setmUserActivityStatuses(ArrayList<UserActivityStatus> mUserActivityStatuses) {
        this.mUserActivityStatuses = mUserActivityStatuses;
    }
}
