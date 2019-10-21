package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 2/28/2017.
 */

public class ConsentPDF extends RealmObject {
    private String message;

    private String sharing;

    @PrimaryKey
    private String studyId;

    private ConsentData consent;

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

    public String getSharing() {
        return sharing;
    }

    public void setSharing(String sharing) {
        this.sharing = sharing;
    }

    public ConsentData getConsent() {
        return consent;
    }

    public void setConsent(ConsentData consent) {
        this.consent = consent;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", sharing = " + sharing + ", consent = " + consent + "]";
    }
}
