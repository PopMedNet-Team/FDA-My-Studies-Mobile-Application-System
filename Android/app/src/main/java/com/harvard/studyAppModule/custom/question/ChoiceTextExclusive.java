package com.harvard.studyAppModule.custom.question;

import java.io.Serializable;

/**
 * Created by Naveen Raj on 05/13/2017.
 */

public class ChoiceTextExclusive<T> implements Serializable {

    private String text;
    private T value;
    private String detail;
    private boolean exclusive;
    private ChoiceTextOtherOption other;

    public ChoiceTextExclusive(String text, T value, String detail, boolean exclusive, ChoiceTextOtherOption other) {
        this.text = text;
        this.value = value;
        this.detail = detail;
        this.exclusive = exclusive;
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

    public boolean isExclusive() {
        return exclusive;
    }

    public String getText() {
        return text;
    }

    public void setOther(ChoiceTextOtherOption other) {
        this.other = other;
    }
}
