package com.harvard.studyAppModule;

import com.harvard.FDAEventBus;
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
 * Created by Rohit on 3/6/2017.
 */

public class StudyModulePresenter {
    public void performGetGateWayStudyInfo(GetUserStudyInfoEvent getUserStudyInfoEvent) {
        FDAEventBus.postEvent(getUserStudyInfoEvent);
    }


    public void performGetConsentMetaData(GetConsentMetaDataEvent getConsentMetaDataEvent) {
        FDAEventBus.postEvent(getConsentMetaDataEvent);
    }

    public void performVerifyEnrollmentId(VerifyEnrollmentIdEvent verifyEnrollmentIdEvent) {
        FDAEventBus.postEvent(verifyEnrollmentIdEvent);
    }

    public void performEnrollId(EnrollIdEvent enrollIdEvent) {
        FDAEventBus.postEvent(enrollIdEvent);
    }

    public void performUpdateEligibilityConsent(UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent) {
        FDAEventBus.postEvent(updateEligibilityConsentStatusEvent);
    }

    public void performGetGateWayStudyList(GetUserStudyListEvent userStudyListEvent) {
        FDAEventBus.postEvent(userStudyListEvent);
    }

    public void performGetTermsAndCondition(GetTermsAndConditionEvent getTermsAndConditionEvent) {
        FDAEventBus.postEvent(getTermsAndConditionEvent);
    }

    public void performGetActivityList(GetActivityListEvent getActivityListEvent) {
        FDAEventBus.postEvent(getActivityListEvent);
    }

    public void performGetActivityInfo(GetActivityInfoEvent getActivityInfoEvent) {
        FDAEventBus.postEvent(getActivityInfoEvent);
    }

    public void performContactUsEvent(ContactUsEvent contactUsEvent) {
        FDAEventBus.postEvent(contactUsEvent);
    }

    public void performContactUsEvent(FeedbackEvent feedbackEvent) {
        FDAEventBus.postEvent(feedbackEvent);
    }

    public void performGetResourceListEvent(GetResourceListEvent getResourceListEvent) {
        FDAEventBus.postEvent(getResourceListEvent);
    }

    public void performProcessResponse(ProcessResponseEvent processResponseEvent) {
        FDAEventBus.postEvent(processResponseEvent);
    }

    public void performWithdrawFromStudy(WithdrawFromStudyEvent withdrawFromStudyEvent) {
        FDAEventBus.postEvent(withdrawFromStudyEvent);
    }
    public void performProcessData(ProcessResponseDataEvent processResponseDataEvent) {
        FDAEventBus.postEvent(processResponseDataEvent);
    }

}
