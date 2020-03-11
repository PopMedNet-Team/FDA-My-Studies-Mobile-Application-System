package com.harvard.studyAppModule.acvitityListModel;

public class AnchorDateSchedulingDetails {
    private String studyId;

    private String schedulingType;

    private String sourceActivityId;

    private String targetActivityId;

    private String sourceKey;

    private String sourceFormKey;

    public String getSourceFormKey() {
        return sourceFormKey;
    }

    public void setSourceFormKey(String sourceFormKey) {
        this.sourceFormKey = sourceFormKey;
    }

    private String activityState;

    private String anchorDate;

    private String participantId;

    private String sourceType;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTargetActivityId() {
        return targetActivityId;
    }

    public void setTargetActivityId(String targetActivityId) {
        this.targetActivityId = targetActivityId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
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

    public String getActivityState() {
        return activityState;
    }

    public void setActivityState(String activityState) {
        this.activityState = activityState;
    }

    public String getAnchorDate() {
        return anchorDate;
    }

    public void setAnchorDate(String anchorDate) {
        this.anchorDate = anchorDate;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getSchedulingType() {
        return schedulingType;
    }

    public void setSchedulingType(String schedulingType) {
        this.schedulingType = schedulingType;
    }
}
