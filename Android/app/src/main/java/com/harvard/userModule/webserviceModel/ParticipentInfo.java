package com.harvard.userModule.webserviceModel;

/**
 * Created by Rohit on 3/2/2017.
 */

public class ParticipentInfo {
    private String studyId;
    private String participantId;
    private String appToken;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }
}
