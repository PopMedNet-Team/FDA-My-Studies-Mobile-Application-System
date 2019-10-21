package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class Charts extends RealmObject implements Serializable {
    private String title;
    private String displayName;
    private String type;
    private boolean scrollable;

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    private ChartsConfiguration configuration;
    private ChartDataSource dataSource;

    public ChartDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(ChartDataSource dataSource) {
        this.dataSource = dataSource;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ChartsConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ChartsConfiguration configuration) {
        this.configuration = configuration;
    }
}
