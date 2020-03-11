package com.harvard.userModule.event;

import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

/**
 * Created by Rohit on 3/20/2017.
 */

public class ResendEmailEvent {
    private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

    public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setmRegistrationServerConfigEvent(RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
        this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
    }
}
