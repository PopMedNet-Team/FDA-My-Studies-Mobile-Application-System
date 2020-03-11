package com.harvard.studyAppModule.events;

import com.harvard.webserviceModule.events.ResponseServerConfigEvent;

/**
 * Created by Naveen Raj on 06/19/2017.
 */

public class ProcessResponseDataEvent {
    private ResponseServerConfigEvent mResponseServerConfigEvent;

    public ResponseServerConfigEvent getResponseServerConfigEvent() {
        return mResponseServerConfigEvent;
    }

    public void setResponseServerConfigEvent(ResponseServerConfigEvent responseServerConfigEvent) {
        mResponseServerConfigEvent = responseServerConfigEvent;
    }
}
