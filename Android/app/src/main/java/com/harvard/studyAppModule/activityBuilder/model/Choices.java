package com.harvard.studyAppModule.activityBuilder.model;

import com.google.gson.annotations.SerializedName;
import com.harvard.studyAppModule.custom.question.ChoiceTextOtherOption;

import io.realm.RealmObject;

/**
 * Created by Rohit on 2/23/2017.
 */
public class Choices extends RealmObject {
    private String text;
    private String value;
    @SerializedName("detail text")
    private String Detailtext;
    private String detail;
    private boolean exclusive;
    private String image;
    private String selectedImage;
    private ChoiceTextOtherOption other;

    public ChoiceTextOtherOption getOther() {
        return other;
    }

    public void setOther(ChoiceTextOtherOption other) {
        this.other = other;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(String selectedImage) {
        this.selectedImage = selectedImage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDetailtext() {
        return Detailtext;
    }

    public void setDetailtext(String detailtext) {
        Detailtext = detailtext;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }
}
