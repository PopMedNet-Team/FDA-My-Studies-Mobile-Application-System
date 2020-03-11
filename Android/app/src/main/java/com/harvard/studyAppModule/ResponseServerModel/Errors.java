package com.harvard.studyAppModule.ResponseServerModel;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class Errors {
    private String message;

    private String id;

    private String field;

    private String msg;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
