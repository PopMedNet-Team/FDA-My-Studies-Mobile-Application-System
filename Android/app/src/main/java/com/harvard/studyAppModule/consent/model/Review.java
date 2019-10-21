package com.harvard.studyAppModule.consent.model;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class Review extends RealmObject{
    private String reasonForConsent;

    private String signatureContent;

    private String signatureTitle;

    public String getReasonForConsent() {
        return reasonForConsent;
    }

    public void setReasonForConsent(String reasonForConsent) {
        this.reasonForConsent = reasonForConsent;
    }

    public String getSignatureContent() {
        return signatureContent;
    }

    public void setSignatureContent(String signatureContent) {
        this.signatureContent = signatureContent;
    }

    public String getSignatureTitle() {
        return signatureTitle;
    }

    public void setSignatureTitle(String signatureTitle) {
        this.signatureTitle = signatureTitle;
    }
}
