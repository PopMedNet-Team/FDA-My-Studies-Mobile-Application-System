package com.harvard.userModule.event;

import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

/**
 * Created by Naveen Raj on 03/24/2017.
 */

public class GetPreferenceEvent {
    private RegistrationServerConfigEvent mRegistrationServerConfigEvent ;

    public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setmRegistrationServerConfigEvent(RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
        this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
    }
}
