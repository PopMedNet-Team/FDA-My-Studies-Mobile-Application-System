package com.harvard.userModule.model;

/**
 * Created by Rohit on 3/27/2017.
 */

public class TermsAndConditionData {
    private String message;
    private String terms;
    private String privacy;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
