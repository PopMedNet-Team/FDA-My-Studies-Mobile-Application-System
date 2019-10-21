package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class Statistics extends RealmObject implements Serializable{
    private String title;
    private String displayName;
    private String statType;
    private String unit;
    private String calculation;
    private StatisticsDataSource dataSource;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatType() {
        return statType;
    }

    public void setStatType(String statType) {
        this.statType = statType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCalculation() {
        return calculation;
    }

    public void setCalculation(String calculation) {
        this.calculation = calculation;
    }

    public StatisticsDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(StatisticsDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
