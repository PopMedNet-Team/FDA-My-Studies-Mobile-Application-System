package com.harvard.studyAppModule.studyModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 2/28/2017.
 */

public class StudyResource extends RealmObject {
    private String message;
    @PrimaryKey
    private String mStudyId;

    public String getmStudyId() {
        return mStudyId;
    }

    public void setmStudyId(String mStudyId) {
        this.mStudyId = mStudyId;
    }
    private RealmList<Resource> resources;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RealmList<Resource> getResources() {
        return resources;
    }

    public void setResources(RealmList<Resource> resources) {
        this.resources = resources;
    }
}
