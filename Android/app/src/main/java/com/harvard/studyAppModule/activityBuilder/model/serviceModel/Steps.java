package com.harvard.studyAppModule.activityBuilder.model.serviceModel;


import com.harvard.studyAppModule.activityBuilder.model.Format;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naveen Raj on 03/02/2017.
 */
public class Steps extends RealmObject {
    private String type;
    private String resultType;
    private String key;
    private String title;
    private String text;
    private boolean skippable;
    private String groupName;
    private boolean repeatable;
    private String repeatableText;
    private RealmList<Destinations> destinations;
    private String healthDataKey;
    private Format format;
    private RealmList<Steps> steps;

    public RealmList<Steps> getSteps() {
        return steps;
    }

    public void setSteps(RealmList<Steps> steps) {
        this.steps = steps;
    }

    public String getRepeatableText() {
        return repeatableText;
    }

    public void setRepeatableText(String repeatableText) {
        this.repeatableText = repeatableText;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getType() {
        return type;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
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


    public boolean isSkippable() {
        return skippable;
    }

    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }

    public RealmList<Destinations> getDestinations() {
        return destinations;
    }

    public void setDestinations(RealmList<Destinations> destinations) {
        this.destinations = destinations;
    }

    public String getHealthDataKey() {
        return healthDataKey;
    }

    public void setHealthDataKey(String healthDataKey) {
        this.healthDataKey = healthDataKey;
    }
}
