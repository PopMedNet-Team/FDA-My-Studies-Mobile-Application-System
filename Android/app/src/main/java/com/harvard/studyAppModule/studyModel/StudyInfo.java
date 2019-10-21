package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 2/28/2017.
 */

public class StudyInfo extends RealmObject {
    private String type;
    private String image;
    private String title;
    private String text;
    private String videoLink;
    private String website;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return videoLink;
    }

    public void setLink(String link) {
        this.videoLink = link;
    }
}
