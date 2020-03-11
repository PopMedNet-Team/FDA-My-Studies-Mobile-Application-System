package com.harvard.studyAppModule;

import com.harvard.FDAEventBus;
import com.harvard.base.BaseSubscriber;
import com.harvard.studyAppModule.events.ContactUsEvent;
import com.harvard.studyAppModule.events.EnrollIdEvent;
import com.harvard.studyAppModule.events.FeedbackEvent;
import com.harvard.studyAppModule.events.GetActivityInfoEvent;
import com.harvard.studyAppModule.events.GetActivityListEvent;
import com.harvard.studyAppModule.events.GetConsentMetaDataEvent;
import com.harvard.studyAppModule.events.GetResourceListEvent;
import com.harvard.studyAppModule.events.GetUserStudyInfoEvent;
import com.harvard.studyAppModule.events.GetUserStudyListEvent;
import com.harvard.studyAppModule.events.ProcessResponseDataEvent;
import com.harvard.studyAppModule.events.ProcessResponseEvent;
import com.harvard.studyAppModule.events.UpdateEligibilityConsentStatusEvent;
import com.harvard.studyAppModule.events.VerifyEnrollmentIdEvent;
import com.harvard.studyAppModule.events.WithdrawFromStudyEvent;
import com.harvard.userModule.event.GetTermsAndConditionEvent;

/**
 * Created by Rohit on 2/21/2017.
 */

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
