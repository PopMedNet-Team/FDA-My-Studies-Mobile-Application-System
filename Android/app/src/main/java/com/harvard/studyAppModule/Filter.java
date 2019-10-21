package com.harvard.studyAppModule;

import com.harvard.studyAppModule.acvitityListModel.ActivitiesWS;
import com.harvard.studyAppModule.survayScheduler.model.ActivityStatus;

import java.util.ArrayList;

/**
 * Created by Rohith on 8/28/2017.
 */

public class Filter {
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<ActivitiesWS> activitiesArrayList1 = new ArrayList<>();
    private ArrayList<ActivityStatus> currentRunStatusForActivities = new ArrayList<>();

    public ArrayList<String> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }

    public ArrayList<ActivitiesWS> getActivitiesArrayList1() {
        return activitiesArrayList1;
    }

    public void setActivitiesArrayList1(ArrayList<ActivitiesWS> activitiesArrayList1) {
        this.activitiesArrayList1 = activitiesArrayList1;
    }

    public ArrayList<ActivityStatus> getCurrentRunStatusForActivities() {
        return currentRunStatusForActivities;
    }

    public void setCurrentRunStatusForActivities(ArrayList<ActivityStatus> currentRunStatusForActivities) {
        this.currentRunStatusForActivities = currentRunStatusForActivities;
    }
}
