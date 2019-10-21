package com.harvard.userModule.webserviceModel;

import java.util.ArrayList;

/**
 * Created by Rohit on 3/2/2017.
 */

public class UpdateProfileRequestData {
    private ProfileUpdate profile;
    private Settings settings;
    private Info info;


    private ArrayList<ParticipentInfo> participantInfo;

    public ProfileUpdate getProfileUpdate() {
        return profile;
    }

    public void setProfileUpdate(ProfileUpdate profile) {
        this.profile = profile;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public ArrayList<ParticipentInfo> getParticipantInfo() {
        return participantInfo;
    }

    public void setParticipantInfo(ArrayList<ParticipentInfo> participantInfo) {
        this.participantInfo = participantInfo;
    }
}
