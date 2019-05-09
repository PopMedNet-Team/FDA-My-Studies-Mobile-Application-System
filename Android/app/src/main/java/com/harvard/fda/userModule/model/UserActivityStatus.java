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
