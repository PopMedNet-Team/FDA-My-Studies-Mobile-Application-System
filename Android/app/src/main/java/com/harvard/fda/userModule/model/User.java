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

package com.harvard.fda.userModule.model;

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
