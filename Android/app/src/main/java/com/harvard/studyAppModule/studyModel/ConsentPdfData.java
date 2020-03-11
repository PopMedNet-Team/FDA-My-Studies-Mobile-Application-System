package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Naveen Raj on 05/04/2017.
 */

public class ConsentPdfData extends RealmObject {
    @PrimaryKey
    private String StudyId;
    private String pdfPath;

    public String getStudyId() {
        return StudyId;
    }

    public void setStudyId(String studyId) {
        StudyId = studyId;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
}
