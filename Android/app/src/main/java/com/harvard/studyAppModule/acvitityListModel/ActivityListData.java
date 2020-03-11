package com.harvard.studyAppModule.acvitityListModel;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Naveen Raj on 04/06/2017.
 */

public class ActivityListData  extends RealmObject {
    private String message;

    private String withdrawalConfig;

    private AnchorDate anchorDate;

    @PrimaryKey
    private String studyId;

    private RealmList<ActivitiesWS> activities = new RealmList<>();

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWithdrawalConfig() {
        return withdrawalConfig;
    }

    public void setWithdrawalConfig(String withdrawalConfig) {
        this.withdrawalConfig = withdrawalConfig;
    }

    public AnchorDate getAnchorDate() {
        return anchorDate;
    }

    public void setAnchorDate(AnchorDate anchorDate) {
        this.anchorDate = anchorDate;
    }

    public RealmList<ActivitiesWS> getActivities() {
        return activities;
    }

    public void setActivities(RealmList<ActivitiesWS> activities) {
        this.activities = activities;
    }
}
