package com.harvard.studyAppModule.studyModel;

import com.harvard.studyAppModule.StudyFragment;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rohit on 3/6/2017.
 */

public class StudyList extends RealmObject{
    private String title;
    private String category;
    private String sponsorName;
    private String studyVersion;
    private String tagline;
    private String status;
    private String studyStatus = StudyFragment.YET_TO_JOIN;
    private String logo;
    @PrimaryKey
    private String studyId;
    private boolean bookmarked = false;
    private String pdfPath = "";

    public String getStudyVersion() {
        return studyVersion;
    }

    public void setStudyVersion(String studyVersion) {
        this.studyVersion = studyVersion;
    }

    public StudySetting getSettings() {
        return settings;
    }

    public void setSettings(StudySetting settings) {
        this.settings = settings;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    private StudySetting settings;

    public StudySetting getSetting() {
        return settings;
    }

    public void setSetting(StudySetting setting) {
        this.settings = setting;
    }

    public String getStudyStatus() {
        return studyStatus;
    }

    public void setStudyStatus(String studyStatus) {
        this.studyStatus = studyStatus;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }
}
