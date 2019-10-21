package com.harvard.studyAppModule.events;

import com.harvard.webserviceModule.events.WCPConfigEvent;

/**
 * Created by Rajeesh on 4/12/2017.
 */

public class FeedbackEvent {
    private WCPConfigEvent wcpConfigEvent;

    public WCPConfigEvent getWcpConfigEvent() {
        return wcpConfigEvent;
    }

    public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
        this.wcpConfigEvent = wcpConfigEvent;
    }
}
