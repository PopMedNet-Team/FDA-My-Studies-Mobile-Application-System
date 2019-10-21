package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 3/22/2017.
 */

public class ConsentDocumentData extends RealmObject {
    private String message;
    private ConsentDocObj consent;

    @PrimaryKey
    private String mStudyId;

    public String getmStudyId() {
        return mStudyId;
    }

    public void setmStudyId(String mStudyId) {
        this.mStudyId = mStudyId;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ConsentDocObj getConsent() {
        return consent;
    }

    public void setConsent(ConsentDocObj consent) {
        this.consent = consent;
    }
}
