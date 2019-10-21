package com.harvard.userModule.webserviceModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 5/4/2017.
 */

public class StudyData extends RealmObject {
    @PrimaryKey
    private String userId;
    private RealmList<Studies> studies;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public RealmList<Studies> getStudies() {
        return studies;
    }

    public void setStudies(RealmList<Studies> studies) {
        this.studies = studies;
    }
}
