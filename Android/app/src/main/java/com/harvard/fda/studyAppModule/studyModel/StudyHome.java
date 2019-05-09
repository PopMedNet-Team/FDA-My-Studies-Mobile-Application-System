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

package com.harvard.fda.studyAppModule.studyModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 3/6/2017.
 * Study Home data - Overview
 */

public class StudyHome extends RealmObject {
    private String message;
    private String studyWebsite;
    private RealmList<StudyInfo> info;
    private Branding branding;
    private WithdrawalConfig withdrawalConfig;
    private AnchorDateStudyHome anchorDate;
    @PrimaryKey
    private String mStudyId;

    public String getmStudyId() {
        return mStudyId;
    }

    public void setmStudyId(String mStudyId) {
        this.mStudyId = mStudyId;
    }

    public String getStudyWebsite() {
        return studyWebsite;
    }

    public void setStudyWebsite(String studyWebsite) {
        this.studyWebsite = studyWebsite;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RealmList<StudyInfo> getInfo() {
        return info;
    }

    public void setInfo(RealmList<StudyInfo> info) {
        this.info = info;
    }

    public Branding getBranding() {
        return branding;
    }

    public void setBranding(Branding branding) {
        this.branding = branding;
    }


    public WithdrawalConfig getWithdrawalConfig() {
        return withdrawalConfig;
    }

    public void setWithdrawalConfig(WithdrawalConfig withdrawalConfig) {
        this.withdrawalConfig = withdrawalConfig;
    }


    public AnchorDateStudyHome getAnchorDate() {
        return anchorDate;
    }

    public void setAnchorDate(AnchorDateStudyHome anchorDate) {
        this.anchorDate = anchorDate;
    }
}
