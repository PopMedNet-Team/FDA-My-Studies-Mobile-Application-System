package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class ChartsConfiguration extends RealmObject implements Serializable{
    private String subType;
    private RealmList<ChartsTitle> titles;
    private RealmList<ChartSetting> settings;

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public RealmList<ChartsTitle> getTitles() {
        return titles;
    }

    public void setTitles(RealmList<ChartsTitle> titles) {
        this.titles = titles;
    }

    public RealmList<ChartSetting> getSettings() {
        return settings;
    }

    public void setSettings(RealmList<ChartSetting> settings) {
        this.settings = settings;
    }
}
