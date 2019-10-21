package com.harvard.userModule.webserviceModel;

import java.util.ArrayList;

/**
 * Created by Rohit on 3/2/2017.
 */

public class UpdateUserPreferenceData {
    private ArrayList<PreferenceStudy> preferenceStudies;
    private ArrayList<PreferenceActivity> preferenceActivities;

    public ArrayList<PreferenceStudy> getPreferenceStudies() {
        return preferenceStudies;
    }

    public void setPreferenceStudies(ArrayList<PreferenceStudy> preferenceStudies) {
        this.preferenceStudies = preferenceStudies;
    }

    public ArrayList<PreferenceActivity> getPreferenceActivities() {
        return preferenceActivities;
    }

    public void setPreferenceActivities(ArrayList<PreferenceActivity> preferenceActivities) {
        this.preferenceActivities = preferenceActivities;
    }
}
