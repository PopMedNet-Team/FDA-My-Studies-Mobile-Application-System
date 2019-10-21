package com.harvard.userModule.event;

/**
 * Created by Rohit on 2/17/2017.
 */

public class SetTouchIDEvent {
    private String userID;
    private String authToken;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
