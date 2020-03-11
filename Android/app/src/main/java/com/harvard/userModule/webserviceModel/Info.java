package com.harvard.userModule.webserviceModel;

/**
 * Created by Rohit on 3/2/2017.
 */

public class Info {
    private String os;
    private String appVersion;
    private String deviceToken;

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
