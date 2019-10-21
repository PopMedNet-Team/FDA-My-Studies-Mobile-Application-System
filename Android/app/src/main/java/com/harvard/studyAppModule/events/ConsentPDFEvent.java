package com.harvard.studyAppModule.events;

import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

/**
 * Created by Naveen Raj on 04/04/2017.
 */

public class ConsentPDFEvent {
    private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

    public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setmRegistrationServerConfigEvent(RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
        this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
    }
}
