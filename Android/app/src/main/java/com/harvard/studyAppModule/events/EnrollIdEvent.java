package com.harvard.studyAppModule.events;

import com.harvard.webserviceModule.events.ResponseServerConfigEvent;

/**
 * Created by Naveen Raj on 04/04/2017.
 */

public class EnrollIdEvent {
    private ResponseServerConfigEvent mResponseServerConfigEvent;

    public ResponseServerConfigEvent getResponseServerConfigEvent() {
        return mResponseServerConfigEvent;
    }

    public void setResponseServerConfigEvent(ResponseServerConfigEvent responseServerConfigEvent) {
        mResponseServerConfigEvent = responseServerConfigEvent;
    }
}
