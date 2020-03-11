package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 2/28/2017.
 */

public class Branding extends RealmObject {
    private String titleFont;
    private String bgColor;
    private String tintColor;
    private String logo;

    public String getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(String titleFont) {
        this.titleFont = titleFont;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getTintColor() {
        return tintColor;
    }

    public void setTintColor(String tintColor) {
        this.tintColor = tintColor;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
