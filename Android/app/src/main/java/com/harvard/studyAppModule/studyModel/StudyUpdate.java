package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 5/13/2017.
 */

public class StudyUpdate extends RealmObject {
    @PrimaryKey
    private String studyId;
    private String message;
    private String currentVersion;
    private StudyUpdateData updates;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public StudyUpdateData getUpdates() {
        return updates;
    }

    public void setUpdates(StudyUpdateData updates) {
        this.updates = updates;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public StudyUpdateData getStudyUpdateData() {
        return updates;
    }

    public void setStudyUpdateData(StudyUpdateData updates) {
        this.updates = updates;
    }
}
