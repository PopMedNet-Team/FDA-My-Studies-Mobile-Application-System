package com.harvard.studyAppModule.events;

import com.harvard.webserviceModule.events.WCPConfigEvent;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class GetConsentMetaDataEvent {
    private WCPConfigEvent wcpConfigEvent;

    public WCPConfigEvent getWcpConfigEvent() {
        return wcpConfigEvent;
    }

    public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
        this.wcpConfigEvent = wcpConfigEvent;
    }
}
