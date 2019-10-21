package com.harvard.studyAppModule.activityBuilder.model.serviceModel;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/02/2017.
 */
public class Destinations extends RealmObject implements Serializable{
    private String condition;
    private String destination;
    private String operator;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
