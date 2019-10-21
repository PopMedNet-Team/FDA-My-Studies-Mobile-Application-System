package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;

public class WithdrawalConfig extends RealmObject {
    private String message;

    private String type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ClassPojo [message = " + message + ", type = " + type + "]";
    }
}
