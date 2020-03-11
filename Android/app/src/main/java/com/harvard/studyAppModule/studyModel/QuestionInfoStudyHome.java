package com.harvard.studyAppModule.studyModel;

import io.realm.RealmObject;

public class QuestionInfoStudyHome extends RealmObject {
    private String activityId;

    private String activityVersion;

    private String key;

    public String getActivityId ()
    {
        return activityId;
    }

    public void setActivityId (String activityId)
    {
        this.activityId = activityId;
    }

    public String getActivityVersion ()
    {
        return activityVersion;
    }

    public void setActivityVersion (String activityVersion)
    {
        this.activityVersion = activityVersion;
    }

    public String getKey ()
    {
        return key;
    }

    public void setKey (String key)
    {
        this.key = key;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [activityId = "+activityId+", activityVersion = "+activityVersion+", key = "+key+"]";
    }
}
