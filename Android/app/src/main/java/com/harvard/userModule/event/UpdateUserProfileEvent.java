package com.harvard.userModule.event;

import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

/**
 * Created by Rohit on 3/2/2017.
 */

public class UpdateUserProfileEvent {
    private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

    public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setmRegistrationServerConfigEvent(RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
        this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
    }
}
