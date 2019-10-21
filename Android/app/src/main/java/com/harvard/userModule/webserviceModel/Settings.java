package com.harvard.userModule.webserviceModel;

import io.realm.RealmObject;

/**
 * Created by Rohit on 3/2/2017.
 */

public class Settings extends RealmObject {
    private boolean localNotifications;
    private boolean remoteNotifications;
    private boolean passcode;
    private String reminderLeadTime;
    private boolean touchId;

    public boolean isLocalNotifications() {
        return localNotifications;
    }

    public void setLocalNotifications(boolean localNotifications) {
        this.localNotifications = localNotifications;
    }

    public boolean isRemoteNotifications() {
        return remoteNotifications;
    }

    public void setRemoteNotifications(boolean remoteNotifications) {
        this.remoteNotifications = remoteNotifications;
    }

    public boolean isPasscode() {
        return passcode;
    }

    public void setPasscode(boolean passcode) {
        this.passcode = passcode;
    }

    public String getRemindersTime() {
        return reminderLeadTime;
    }

    public void setRemindersTime(String remindersTime) {
        this.reminderLeadTime = remindersTime;
    }

    public boolean isTouchId() {
        return touchId;
    }

    public void setTouchId(boolean touchId) {
        this.touchId = touchId;
    }
}
