package com.harvard.studyAppModule.activityBuilder.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Rohit on 2/23/2017.
 */

public class Format extends RealmObject {
    private int maxValue;
    private int minValue;
    private RealmList<Choices> textChoices;
    private int maxFractionDigits;
    private String minDate;
    private String maxDate;
    private String measurementSystem;
    private RealmList<Choices> imageChoices;
    private boolean useCurrentLocation;
    private String style;
    private String unit;
    private String maxDesc;
    private String minDesc;
    private String maxImage;
    private String minImage;
    private String text;
    private String value;
    @SerializedName("detail text")
    private String detailtext;
    private String exclusive;
    private String image;
    @SerializedName("selected image")
    private String selectedImage;
    private String selectionStyle;
    private int maxLength;
    private String validationRegex;
    private String invalidMessage;
    private boolean multipleLines;
    private String placeholder;
    private boolean vertical;
    @SerializedName("default")
    private String defaultValue;
    private int step;
    private int duration;
    private int kickCount;
    private String dateRange;

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public int getKickCount() {
        return kickCount;
    }

    public void setKickCount(int kickCount) {
        this.kickCount = kickCount;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(String selectedImage) {
        this.selectedImage = selectedImage;
    }

    public int getMaxFractionDigits() {
        return maxFractionDigits;
    }

    public void setMaxFractionDigits(int maxFractionDigits) {
        this.maxFractionDigits = maxFractionDigits;
    }

    public RealmList<Choices> getImageChoices() {
        return imageChoices;
    }

    public void setImageChoices(RealmList<Choices> imageChoices) {
        this.imageChoices = imageChoices;
    }

    public boolean isUseCurrentLocation() {
        return useCurrentLocation;
    }

    public void setUseCurrentLocation(boolean useCurrentLocation) {
        this.useCurrentLocation = useCurrentLocation;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMaxDesc() {
        return maxDesc;
    }

    public void setMaxDesc(String maxDesc) {
        this.maxDesc = maxDesc;
    }

    public String getMinDesc() {
        return minDesc;
    }

    public void setMinDesc(String minDesc) {
        this.minDesc = minDesc;
    }

    public String getMaxImage() {
        return maxImage;
    }

    public void setMaxImage(String maxImage) {
        this.maxImage = maxImage;
    }

    public String getMinImage() {
        return minImage;
    }

    public void setMinImage(String minImage) {
        this.minImage = minImage;
    }

    public void setMeasurementSystem(String measurementSystem) {
        this.measurementSystem = measurementSystem;
    }

    public String getMeasurementSystem() {
        return measurementSystem;
    }

    public String getSelectionStyle() {
        return selectionStyle;
    }

    public void setSelectionStyle(String selectionStyle) {
        this.selectionStyle = selectionStyle;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public void setValidationRegex(String validationRegex) {
        this.validationRegex = validationRegex;
    }

    public String getInvalidMessage() {
        return invalidMessage;
    }

    public void setInvalidMessage(String invalidMessage) {
        this.invalidMessage = invalidMessage;
    }

    public boolean isMultipleLines() {
        return multipleLines;
    }

    public void setMultipleLines(boolean multipleLines) {
        this.multipleLines = multipleLines;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }
//
//    public int getDefaultValue() {
//        return defaultValue;
//    }
//
//    public void setDefaultValue(int defaultValue) {
//        this.defaultValue = defaultValue;
//    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
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
        return detailtext;
    }

    public void setDetailtext(String detailtext) {
        this.detailtext = detailtext;
    }

    public String getExclusive() {
        return exclusive;
    }

    public void setExclusive(String exclusive) {
        this.exclusive = exclusive;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public RealmList<Choices> getTextChoices() {
        return textChoices;
    }

    public void setTextChoices(RealmList<Choices> textChoices) {
        this.textChoices = textChoices;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }
}
