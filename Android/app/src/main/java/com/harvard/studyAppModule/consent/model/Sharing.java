package com.harvard.studyAppModule.consent.model;

import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class Sharing extends RealmObject{
    private String text;

    private String title;

    private String learnMore;

    private String allowWithoutSharing;

    private String longDesc;

    private String shortDesc;

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

    public String getLearnMore() {
        return learnMore;
    }

    public void setLearnMore(String learnMore) {
        this.learnMore = learnMore;
    }

    public String getAllowWithoutSharing() {
        return allowWithoutSharing;
    }

    public void setAllowWithoutSharing(String allowWithoutSharing) {
        this.allowWithoutSharing = allowWithoutSharing;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }
}
