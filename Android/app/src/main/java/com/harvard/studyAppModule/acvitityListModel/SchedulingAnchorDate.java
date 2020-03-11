package com.harvard.studyAppModule.acvitityListModel;

import io.realm.RealmObject;

public class SchedulingAnchorDate extends RealmObject {
    private String sourceType;

    private String sourceActivityId;

    private String sourceKey;

    private String sourceFormKey;

    public String getSourceFormKey() {
        return sourceFormKey;
    }

    public void setSourceFormKey(String sourceFormKey) {
        this.sourceFormKey = sourceFormKey;
    }

    private SchedulingAnchorDateStart start;

    private SchedulingAnchorDateEnd end;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSourceActivityId() {
        return sourceActivityId;
    }

    public void setSourceActivityId(String sourceActivityId) {
        this.sourceActivityId = sourceActivityId;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public SchedulingAnchorDateStart getStart() {
        return start;
    }

    public void setStart(SchedulingAnchorDateStart start) {
        this.start = start;
    }

    public SchedulingAnchorDateEnd getEnd() {
        return end;
    }

    public void setEnd(SchedulingAnchorDateEnd end) {
        this.end = end;
    }
}
