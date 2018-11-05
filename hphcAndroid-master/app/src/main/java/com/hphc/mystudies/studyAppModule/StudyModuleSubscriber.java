/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.studyAppModule;

import com.hphc.mystudies.FDAEventBus;
import com.hphc.mystudies.base.BaseSubscriber;
import com.hphc.mystudies.studyAppModule.events.ContactUsEvent;
import com.hphc.mystudies.studyAppModule.events.EnrollIdEvent;
import com.hphc.mystudies.studyAppModule.events.FeedbackEvent;
import com.hphc.mystudies.studyAppModule.events.GetActivityInfoEvent;
import com.hphc.mystudies.studyAppModule.events.GetActivityListEvent;
import com.hphc.mystudies.studyAppModule.events.GetConsentMetaDataEvent;
import com.hphc.mystudies.studyAppModule.events.GetResourceListEvent;
import com.hphc.mystudies.studyAppModule.events.GetUserStudyInfoEvent;
import com.hphc.mystudies.studyAppModule.events.GetUserStudyListEvent;
import com.hphc.mystudies.studyAppModule.events.ProcessResponseDataEvent;
import com.hphc.mystudies.studyAppModule.events.ProcessResponseEvent;
import com.hphc.mystudies.studyAppModule.events.UpdateEligibilityConsentStatusEvent;
import com.hphc.mystudies.studyAppModule.events.VerifyEnrollmentIdEvent;
import com.hphc.mystudies.studyAppModule.events.WithdrawFromStudyEvent;
import com.hphc.mystudies.userModule.event.GetTermsAndConditionEvent;


public class StudyModuleSubscriber extends BaseSubscriber {
    public void onEvent(GetUserStudyInfoEvent getUserStudyInfoEvent) {
        FDAEventBus.postEvent(getUserStudyInfoEvent.getWcpConfigEvent());
    }


    public void onEvent(GetConsentMetaDataEvent getConsentMetaDataEvent) {
        FDAEventBus.postEvent(getConsentMetaDataEvent.getWcpConfigEvent());
    }

    public void onEvent(VerifyEnrollmentIdEvent verifyEnrollmentIdEvent) {
        FDAEventBus.postEvent(verifyEnrollmentIdEvent.getResponseServerConfigEvent());
    }

    public void onEvent(GetUserStudyListEvent getUserStudyListEvent) {
        FDAEventBus.postEvent(getUserStudyListEvent.getWcpConfigEvent());
    }

    public void onEvent(GetTermsAndConditionEvent getTermsAndConditionEvent) {
        FDAEventBus.postEvent(getTermsAndConditionEvent.getWcpConfigEvent());
    }

    public void onEvent(EnrollIdEvent enrollIdEvent) {
        FDAEventBus.postEvent(enrollIdEvent.getResponseServerConfigEvent());
    }

    public void onEvent(GetActivityListEvent getActivityListEvent) {
        FDAEventBus.postEvent(getActivityListEvent.getWcpConfigEvent());
    }

    public void onEvent(GetActivityInfoEvent getActivityInfoEvent) {
        FDAEventBus.postEvent(getActivityInfoEvent.getWcpConfigEvent());
    }

    public void onEvent(ContactUsEvent contactUsEvent) {
        FDAEventBus.postEvent(contactUsEvent.getWcpConfigEvent());
    }

    public void onEvent(FeedbackEvent feedbackEvent) {
        FDAEventBus.postEvent(feedbackEvent.getWcpConfigEvent());
    }

    public void onEvent(GetResourceListEvent getResourceListEvent) {
        FDAEventBus.postEvent(getResourceListEvent.getWcpConfigEvent());
    }

    public void onEvent(UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent) {
        FDAEventBus.postEvent(updateEligibilityConsentStatusEvent.getRegistrationServerConfigEvent());
    }

    public void onEvent(ProcessResponseEvent processResponseEvent) {
        FDAEventBus.postEvent(processResponseEvent.getResponseServerConfigEvent());
    }

    public void onEvent(WithdrawFromStudyEvent withdrawFromStudyEvent) {
        FDAEventBus.postEvent(withdrawFromStudyEvent.getResponseServerConfigEvent());
    }

    public void onEvent(ProcessResponseDataEvent processResponseDataEvent) {
        FDAEventBus.postEvent(processResponseDataEvent.getResponseServerConfigEvent());
    }

}
