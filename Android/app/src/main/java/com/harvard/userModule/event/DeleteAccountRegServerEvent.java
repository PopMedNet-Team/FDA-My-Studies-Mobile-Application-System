package com.harvard.userModule.event;

import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

/**
 * Created by Rohit on 2/17/2017.
 */

public class DeleteAccountRegServerEvent {
    private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

    public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setmRegistrationServerConfigEvent(RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
        this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
    }
}
