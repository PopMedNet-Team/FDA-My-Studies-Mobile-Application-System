package com.harvard.studyAppModule.consent.model;

import com.harvard.studyAppModule.activityBuilder.model.Eligibility;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class EligibilityConsent extends RealmObject {
    private String message;

    @PrimaryKey
    private String studyId;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    private Consent consent;

    private Eligibility eligibility;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }

    public Eligibility getEligibility() {
        return eligibility;
    }

    public void setEligibility(Eligibility eligibility) {
        this.eligibility = eligibility;
    }
}
