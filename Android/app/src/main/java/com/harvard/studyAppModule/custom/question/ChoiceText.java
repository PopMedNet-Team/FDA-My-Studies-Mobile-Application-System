package com.harvard.studyAppModule.custom.question;

import java.io.Serializable;

/**
 * Created by Naveen Raj on 05/13/2017.
 */

public class ChoiceText<T> implements Serializable {

    private String text;
    private T value;
    private String detail;
    private ChoiceTextOtherOption other;

    public ChoiceText(String text, T value, String detail, ChoiceTextOtherOption other) {
        this.text = text;
        this.value = value;
        this.detail = detail;
        this.other = other;
    }

    public ChoiceTextOtherOption getOther() {
        return other;
    }

    public T getValue() {
        return value;
    }

    public String getDetail() {
        return detail;
    }

    public String getText() {
        return text;
    }

    public void setOther(ChoiceTextOtherOption other) {
        this.other = other;
    }
}
