package com.harvard.studyAppModule.studyModel;

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
