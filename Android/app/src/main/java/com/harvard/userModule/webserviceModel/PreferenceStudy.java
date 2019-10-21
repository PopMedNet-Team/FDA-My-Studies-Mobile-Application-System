package com.harvard.userModule.webserviceModel;

/**
 * Created by Rohit on 3/2/2017.
 */

public class PreferenceStudy {
    private String studyId;
    private String status;
    private String bookmarked;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(String bookmarked) {
        this.bookmarked = bookmarked;
    }
}
