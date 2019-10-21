package com.harvard.userModule.webserviceModel;

/**
 * Created by Rohit on 3/1/2017.
 */

public class LoginData {
    private String auth;
    private boolean verified;
    private String message;
    private String userId;
    private String refreshToken;
    private boolean resetPassword = false;

    public boolean getResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(boolean resetPassword) {
        this.resetPassword = resetPassword;
    }

    public String getAuth() {
        return auth;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
