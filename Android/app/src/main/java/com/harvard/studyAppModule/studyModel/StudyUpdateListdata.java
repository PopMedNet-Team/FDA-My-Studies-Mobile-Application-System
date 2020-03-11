package com.harvard.studyAppModule.studyModel;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 5/13/2017.
 */

public class StudyUpdateListdata extends RealmObject {
    @PrimaryKey
    private int id = 1;
    private RealmList<StudyUpdate> studyUpdates;

    public RealmList<StudyUpdate> getStudyUpdates() {
        return studyUpdates;
    }

    public void setStudyUpdates(RealmList<StudyUpdate> studyUpdates) {
        this.studyUpdates = studyUpdates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
