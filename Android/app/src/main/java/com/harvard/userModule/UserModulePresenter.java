package com.harvard.userModule;

import com.harvard.FDAEventBus;
import com.harvard.studyAppModule.events.ConsentPDFEvent;
import com.harvard.studyAppModule.events.DeleteAccountEvent;
import com.harvard.userModule.event.ChangePasswordEvent;
import com.harvard.userModule.event.ForgotPasswordEvent;
import com.harvard.userModule.event.GetPreferenceEvent;
import com.harvard.userModule.event.GetUserProfileEvent;
import com.harvard.userModule.event.LoginEvent;
import com.harvard.userModule.event.LogoutEvent;
import com.harvard.userModule.event.RegisterUserEvent;
import com.harvard.userModule.event.ResendEmailEvent;
import com.harvard.userModule.event.UpdatePreferenceEvent;
import com.harvard.userModule.event.UpdateUserProfileEvent;
import com.harvard.userModule.event.VerifyUserEvent;

/**
 * Created by Rohit on 2/20/2017.
 */

public class UserModulePresenter {
    public void performLogin(LoginEvent loginEvent) {
        FDAEventBus.postEvent(loginEvent);
    }

    public void performRegistration(RegisterUserEvent registerUserEvent) {
        FDAEventBus.postEvent(registerUserEvent);
    }

    public void performVerifyRegistration(VerifyUserEvent verifyUserEvent) {
        FDAEventBus.postEvent(verifyUserEvent);
    }

    public void performForgotPassword(ForgotPasswordEvent forgotPasswordEvent) {
        FDAEventBus.postEvent(forgotPasswordEvent);
    }

    public void performChangePassword(ChangePasswordEvent changePasswordEvent) {
        FDAEventBus.postEvent(changePasswordEvent);
    }

    public void performGetUserProfile(GetUserProfileEvent getUserProfileEvent) {
        FDAEventBus.postEvent(getUserProfileEvent);
    }

    public void performUpdateUserProfile(UpdateUserProfileEvent updateUserProfileEvent) {
        FDAEventBus.postEvent(updateUserProfileEvent);
    }

    public void performUpdateUserPreference(UpdatePreferenceEvent updatePreferenceEvent) {
        FDAEventBus.postEvent(updatePreferenceEvent);
    }

    public void performGetUserPreference(GetPreferenceEvent getPreferenceEvent) {
        FDAEventBus.postEvent(getPreferenceEvent);
    }

    public void performLogout(LogoutEvent logoutEvent) {
        FDAEventBus.postEvent(logoutEvent);
    }

    public void performDeleteAccount(DeleteAccountEvent deleteAccountEvent) {
        FDAEventBus.postEvent(deleteAccountEvent);
    }

    public void performResendEmail(ResendEmailEvent resendEmailEvent) {
        FDAEventBus.postEvent(resendEmailEvent);
    }

    public void performConsentPDF(ConsentPDFEvent consentPDFEvent) {
        FDAEventBus.postEvent(consentPDFEvent);
    }


}
