package com.harvard.studyAppModule.custom.question;

import java.io.Serializable;

import io.realm.RealmObject;

public class ChoiceTextOtherOption extends RealmObject implements Serializable {

    private String placeholder;
    private boolean isMandatory;
    private boolean textfieldReq;

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isTextfieldReq() {
        return textfieldReq;
    }

    public void setTextfieldReq(boolean textfieldReq) {
        this.textfieldReq = textfieldReq;
    }
}
