package com.harvard.studyAppModule.consent.model;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class VisualScreens extends RealmObject {
    private String text;

    private String title;

    private boolean visualStep;

    private String description;

    private String html;

    private String type;

    private String url;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVisualStep() {
        return visualStep;
    }

    public void setVisualStep(boolean visualStep) {
        this.visualStep = visualStep;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
