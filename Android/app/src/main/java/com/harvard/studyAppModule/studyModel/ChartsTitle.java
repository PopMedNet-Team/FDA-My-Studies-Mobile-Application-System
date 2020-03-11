package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class ChartsTitle extends RealmObject implements Serializable{
    private String titles;

    public String getTitles() {
        return titles;
    }

    public void setTitles(String titles) {
        this.titles = titles;
    }
}
