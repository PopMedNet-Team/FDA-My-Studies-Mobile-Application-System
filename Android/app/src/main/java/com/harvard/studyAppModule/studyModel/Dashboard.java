package com.harvard.studyAppModule.studyModel;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Rohit on 5/5/2017.
 */

public class Dashboard extends RealmObject implements Serializable{
    private RealmList<Statistics> statistics;
    private RealmList<Charts> charts;

    public RealmList<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(RealmList<Statistics> statistics) {
        this.statistics = statistics;
    }

    public RealmList<Charts> getCharts() {
        return charts;
    }

    public void setCharts(RealmList<Charts> charts) {
        this.charts = charts;
    }
}
