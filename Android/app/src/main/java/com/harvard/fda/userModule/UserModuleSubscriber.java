/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.fda.userModule;

import com.harvard.fda.FDAEventBus;
import com.harvard.fda.base.BaseSubscriber;
import com.harvard.fda.storageModule.events.DatabaseEvent;
import com.harvard.fda.studyAppModule.events.ConsentPDFEvent;
import com.harvard.fda.studyAppModule.events.DeleteAccountEvent;
import com.harvard.fda.userModule.event.ChangePasswordEvent;
import com.harvard.fda.userModule.event.DeleteAccountRegServerEvent;
import com.harvard.fda.userModule.event.DeleteAccountResServerEvent;
import com.harvard.fda.userModule.event.ForgotPasswordEvent;
import com.harvard.fda.userModule.event.GetPreferenceEvent;
import com.harvard.fda.userModule.event.GetUserEvent;
import com.harvard.fda.userModule.event.GetUserProfileEvent;
import com.harvard.fda.userModule.event.LoginEvent;
import com.harvard.fda.userModule.event.LogoutEvent;
import com.harvard.fda.userModule.event.ParticipentEnrollmentEvent;
import com.harvard.fda.userModule.event.RegisterUserEvent;
import com.harvard.fda.userModule.event.ResendEmailEvent;
import com.harvard.fda.userModule.event.SetPasscodeEvent;
import com.harvard.fda.userModule.event.SetTouchIDEvent;
import com.harvard.fda.userModule.event.SetUserEvent;
import com.harvard.fda.userModule.event.SignOutEvent;
import com.harvard.fda.userModule.event.TouchIdSigninEvent;
import com.harvard.fda.userModule.event.UpdatePreferenceEvent;
import com.harvard.fda.userModule.event.UpdateUserProfileEvent;
import com.harvard.fda.userModule.event.ValidatePasscodeEvent;
import com.harvard.fda.userModule.event.ValidateTouchIDEvent;
import com.harvard.fda.userModule.event.VerifyUserEvent;

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
