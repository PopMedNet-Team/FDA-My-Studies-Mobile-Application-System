package com.harvard.studyAppModule.events;

import com.harvard.webserviceModule.events.WCPConfigEvent;

/**
 * Created by Shakeel on 12-04-2017.
 */

public class GetResourceListEvent {

    private WCPConfigEvent wcpConfigEvent;

    public WCPConfigEvent getWcpConfigEvent() {
        return wcpConfigEvent;
    }

    public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
        this.wcpConfigEvent = wcpConfigEvent;
    }

}
