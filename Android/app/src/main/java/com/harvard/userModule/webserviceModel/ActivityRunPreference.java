package com.harvard.userModule.webserviceModel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/4/2017.
 */

public class ActivityRunPreference extends RealmObject {
    private int total;
    private int missed;
    private int completed;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }
}
