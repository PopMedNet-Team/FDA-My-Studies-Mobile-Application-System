package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by USER on 07-06-2017.
 */

public class MotivationalNotification extends RealmObject {
    @PrimaryKey
    private String studyId;
    private boolean fiftyPc;
    private boolean hundredPc;
    int missed;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public boolean isFiftyPc() {
        return fiftyPc;
    }

    public void setFiftyPc(boolean fiftyPc) {
        this.fiftyPc = fiftyPc;
    }

    public boolean isHundredPc() {
        return hundredPc;
    }

    public void setHundredPc(boolean hundredPc) {
        this.hundredPc = hundredPc;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }
}
