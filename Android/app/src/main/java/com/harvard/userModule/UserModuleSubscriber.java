package com.harvard.userModule;

import com.harvard.FDAEventBus;
import com.harvard.base.BaseSubscriber;
import com.harvard.studyAppModule.events.ConsentPDFEvent;
import com.harvard.studyAppModule.events.DeleteAccountEvent;
import com.harvard.userModule.event.ChangePasswordEvent;
import com.harvard.userModule.event.DeleteAccountRegServerEvent;
import com.harvard.userModule.event.DeleteAccountResServerEvent;
import com.harvard.userModule.event.ForgotPasswordEvent;
import com.harvard.userModule.event.GetPreferenceEvent;
import com.harvard.userModule.event.GetUserEvent;
import com.harvard.userModule.event.GetUserProfileEvent;
import com.harvard.userModule.event.LoginEvent;
import com.harvard.userModule.event.LogoutEvent;
import com.harvard.userModule.event.ParticipentEnrollmentEvent;
import com.harvard.userModule.event.RegisterUserEvent;
import com.harvard.userModule.event.ResendEmailEvent;
import com.harvard.userModule.event.SetPasscodeEvent;
import com.harvard.userModule.event.SetTouchIDEvent;
import com.harvard.userModule.event.SetUserEvent;
import com.harvard.userModule.event.SignOutEvent;
import com.harvard.userModule.event.TouchIdSigninEvent;
import com.harvard.userModule.event.UpdatePreferenceEvent;
import com.harvard.userModule.event.UpdateUserProfileEvent;
import com.harvard.userModule.event.ValidatePasscodeEvent;
import com.harvard.userModule.event.ValidateTouchIDEvent;
import com.harvard.userModule.event.VerifyUserEvent;

/**
 * Created by Rohit on 2/14/2017.
 */

public class UserModuleSubscriber extends BaseSubscriber {
    /**
     *  get User
     */
    public void onEvent(GetUserEvent event) {
    }

    /**
     *  Set user
     */
    public void onEvent(SetUserEvent event) {
    }

    /**
     *  Register user
     */
    public void onEvent(RegisterUserEvent registerUserEvent)
    {
        FDAEventBus.postEvent(registerUserEvent.getmRegistrationServerConfigEvent());
    }

    /**
     * verify account
     */
    public void onEvent(VerifyUserEvent verifyUserEvent)
    {
        FDAEventBus.postEvent(verifyUserEvent.getmRegistrationServerConfigEvent());
    }

    /**
     * set touchId
     */
    public void onEvent(SetTouchIDEvent setTouchIDEvent)
    {

    }

    /**
     * validate touchId
     */
    public void onEvent(ValidateTouchIDEvent validateTouchIDEvent)
    {

    }

    /**
     * set passcode
     */
    public void onEvent(SetPasscodeEvent setPasscodeEvent)
    {
    }

    /**
     * validate passcode
     */
    public void onEvent(ValidatePasscodeEvent validatePasscodeEvent)
    {
    }

    /**
     * Login
     */
    public void onEvent(LoginEvent loginEvent)
    {
        FDAEventBus.postEvent(loginEvent.getmRegistrationServerConfigEvent());
    }


    /**
     * touchId login
     */
    public void onEvent(TouchIdSigninEvent touchIdSigninEvent)
    {
        FDAEventBus.postEvent(touchIdSigninEvent.getmRegistrationServerConfigEvent());
    }

    /**
     * Participent Enrollment
     */
    public void onEvent(ParticipentEnrollmentEvent participentEnrollmentEvent)
    {
        FDAEventBus.postEvent(participentEnrollmentEvent.getmResponseServerConfigEvent());
    }

    /**
     * Forgot password
     */
    public void onEvent(ForgotPasswordEvent forgotPasswordEvent)
    {
        FDAEventBus.postEvent(forgotPasswordEvent.getmRegistrationServerConfigEvent());
    }

    /**
     * change password
     */
    public void onEvent(ChangePasswordEvent changePasswordEvent)
    {
        FDAEventBus.postEvent(changePasswordEvent.getmRegistrationServerConfigEvent());
    }

    /**
     * delete account from registration server
     */
    public void onEvent(DeleteAccountRegServerEvent deleteAccountRegServerEvent)
    {
        FDAEventBus.postEvent(deleteAccountRegServerEvent.getmRegistrationServerConfigEvent());
    }

    /**
     * delete account from response server
     */
    public void onEvent(DeleteAccountResServerEvent deleteAccountResServerEvent)
    {
        FDAEventBus.postEvent(deleteAccountResServerEvent.getmResponseServerConfigEvent());
    }

    /**
     * Sign out
     */
    public void onEvent(SignOutEvent signOutEvent)
    {
        FDAEventBus.postEvent(signOutEvent.getmRegistrationServerConfigEvent());
    }

    public void onEvent(GetUserProfileEvent getUserProfileEvent)
    {
        FDAEventBus.postEvent(getUserProfileEvent.getmRegistrationServerConfigEvent());
    }

    public void onEvent(UpdateUserProfileEvent updateUserProfileEvent)
    {
        FDAEventBus.postEvent(updateUserProfileEvent.getmRegistrationServerConfigEvent());
    }

    public void onEvent(UpdatePreferenceEvent updatePreferenceEvent)
    {
        FDAEventBus.postEvent(updatePreferenceEvent.getmRegistrationServerConfigEvent());
    }

    public void onEvent(ResendEmailEvent resendEmailEvent)
    {
        FDAEventBus.postEvent(resendEmailEvent.getmRegistrationServerConfigEvent());
    }

    public void onEvent(LogoutEvent logoutEvent)
    {
        FDAEventBus.postEvent(logoutEvent.getmRegistrationServerConfigEvent());
    }

    public void onEvent(GetPreferenceEvent getPreferenceEvent)
    {
        FDAEventBus.postEvent(getPreferenceEvent.getmRegistrationServerConfigEvent());
    }

    public void onEvent(DeleteAccountEvent deleteAccountEvent)
    {
        FDAEventBus.postEvent(deleteAccountEvent.getmRegistrationServerConfigEvent());
    }
    public void onEvent(ConsentPDFEvent consentPDFEvent)
    {
        FDAEventBus.postEvent(consentPDFEvent.getmRegistrationServerConfigEvent());
    }



}
