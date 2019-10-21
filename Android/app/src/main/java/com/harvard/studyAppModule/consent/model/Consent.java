package com.harvard.studyAppModule.consent.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class Consent extends RealmObject{
    private Sharing sharing;

    private RealmList<VisualScreens> visualScreens;

    private Comprehension comprehension;

    private Review review;

    private String version;

    public Sharing getSharing() {
        return sharing;
    }

    public void setSharing(Sharing sharing) {
        this.sharing = sharing;
    }

    public RealmList<VisualScreens> getVisualScreens() {
        return visualScreens;
    }

    public void setVisualScreens(RealmList<VisualScreens> visualScreens) {
        this.visualScreens = visualScreens;
    }

    public Comprehension getComprehension() {
        return comprehension;
    }

    public void setComprehension(Comprehension comprehension) {
        this.comprehension = comprehension;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
