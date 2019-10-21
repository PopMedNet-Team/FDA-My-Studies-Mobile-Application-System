package com.harvard.studyAppModule.ResponseServerModel;

import java.util.ArrayList;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class ResponseServerData {
    private ArrayList<Errors> errors;

    private String exception;

    private boolean success;

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ArrayList<Errors> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<Errors> errors) {
        this.errors = errors;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
