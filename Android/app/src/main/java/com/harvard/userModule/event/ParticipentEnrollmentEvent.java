package com.harvard.userModule.event;

import com.harvard.webserviceModule.events.ResponseServerConfigEvent;

/**
 * Created by Rohit on 2/17/2017.
 */

public class ParticipentEnrollmentEvent {
    private ResponseServerConfigEvent mResponseServerConfigEvent;

    public ResponseServerConfigEvent getmResponseServerConfigEvent() {
        return mResponseServerConfigEvent;
    }

    public void setmResponseServerConfigEvent(ResponseServerConfigEvent mResponseServerConfigEvent) {
        this.mResponseServerConfigEvent = mResponseServerConfigEvent;
    }
}
