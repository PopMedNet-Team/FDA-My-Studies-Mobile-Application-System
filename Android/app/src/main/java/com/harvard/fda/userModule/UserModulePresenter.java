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
import com.harvard.fda.studyAppModule.events.ConsentPDFEvent;
import com.harvard.fda.studyAppModule.events.DeleteAccountEvent;
import com.harvard.fda.userModule.event.ChangePasswordEvent;
import com.harvard.fda.userModule.event.ForgotPasswordEvent;
import com.harvard.fda.userModule.event.GetPreferenceEvent;
import com.harvard.fda.userModule.event.GetUserProfileEvent;
import com.harvard.fda.userModule.event.LoginEvent;
import com.harvard.fda.userModule.event.LogoutEvent;
import com.harvard.fda.userModule.event.RegisterUserEvent;
import com.harvard.fda.userModule.event.ResendEmailEvent;
import com.harvard.fda.userModule.event.UpdatePreferenceEvent;
import com.harvard.fda.userModule.event.UpdateUserProfileEvent;
import com.harvard.fda.userModule.event.VerifyUserEvent;

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
