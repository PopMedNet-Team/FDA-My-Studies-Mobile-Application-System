package com.harvard.studyAppModule.activityBuilder.model;

import java.util.ArrayList;

/**
 * Created by Rohit on 2/22/2017.
 */

public class ActivityStep {
    private String activityId;
    private String type;
    //question type
    private String resultType;
    private String key;
    private String title;
    private String text;
    private String skippable;
    private String groupName;
    private ArrayList<Destinations> destinationses = new ArrayList<>();

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getSkippable() {
        return skippable;
    }

    public void setSkippable(String skippable) {
        this.skippable = skippable;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<Destinations> getDestinationses() {
        return destinationses;
    }

    public void setDestinationses(ArrayList<Destinations> destinationses) {
        this.destinationses = destinationses;
    }
}
