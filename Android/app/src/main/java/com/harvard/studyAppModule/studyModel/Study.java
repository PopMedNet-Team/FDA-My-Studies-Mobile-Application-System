package com.harvard.studyAppModule.studyModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 2/28/2017.
 * Study list data
 */

public class Study extends RealmObject {
    private String message;
    @PrimaryKey
    private int id = 1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private RealmList<StudyList> studies;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RealmList<StudyList> getStudies() {
        return studies;
    }

    public void setStudies(RealmList<StudyList> studies) {
        this.studies = studies;
    }
}
