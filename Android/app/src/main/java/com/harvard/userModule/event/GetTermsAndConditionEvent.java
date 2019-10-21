package com.harvard.userModule.event;

import com.harvard.webserviceModule.events.WCPConfigEvent;

/**
 * Created by Rohit on 3/27/2017.
 */

public class GetTermsAndConditionEvent {
    private WCPConfigEvent wcpConfigEvent;

    public WCPConfigEvent getWcpConfigEvent() {
        return wcpConfigEvent;
    }

    public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
        this.wcpConfigEvent = wcpConfigEvent;
    }
}
