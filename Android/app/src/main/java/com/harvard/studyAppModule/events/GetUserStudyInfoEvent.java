package com.harvard.studyAppModule.events;

import com.harvard.webserviceModule.events.WCPConfigEvent;

/**
 * Created by Rohit on 3/3/2017.
 */

public class GetUserStudyInfoEvent {
    private WCPConfigEvent wcpConfigEvent;

    public WCPConfigEvent getWcpConfigEvent() {
        return wcpConfigEvent;
    }

    public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
        this.wcpConfigEvent = wcpConfigEvent;
    }
}
