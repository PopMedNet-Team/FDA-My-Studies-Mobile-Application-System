package com.harvard.userModule.model;

/**
 * Created by Rohit on 2/14/2017.
 */

public class Settings {
    private boolean mRemoteNotifications;
    private boolean mLocalNotifications;
    private boolean mTouchId;
    private boolean mPasscode;

    public boolean ismRemoteNotifications() {
        return mRemoteNotifications;
    }

    public void setmRemoteNotifications(boolean mRemoteNotifications) {
        this.mRemoteNotifications = mRemoteNotifications;
    }

    public boolean ismLocalNotifications() {
        return mLocalNotifications;
    }

    public void setmLocalNotifications(boolean mLocalNotifications) {
        this.mLocalNotifications = mLocalNotifications;
    }

    public boolean ismTouchId() {
        return mTouchId;
    }

    public void setmTouchId(boolean mTouchId) {
        this.mTouchId = mTouchId;
    }

    public boolean ismPasscode() {
        return mPasscode;
    }

    public void setmPasscode(boolean mPasscode) {
        this.mPasscode = mPasscode;
    }
}
