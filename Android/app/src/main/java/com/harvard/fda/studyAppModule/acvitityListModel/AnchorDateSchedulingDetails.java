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

package com.harvard.fda.studyAppModule.acvitityListModel;

public class AnchorDateSchedulingDetails {
    private String studyId;

    private String schedulingType;

    private String sourceActivityId;

    private String targetActivityId;

    private String sourceKey;

    private String sourceFormKey;

    public String getSourceFormKey() {
        return sourceFormKey;
    }

    public void setSourceFormKey(String sourceFormKey) {
        this.sourceFormKey = sourceFormKey;
    }

    private String activityState;

    private String anchorDate;

    private String participantId;

    private String sourceType;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTargetActivityId() {
        return targetActivityId;
    }

    public void setTargetActivityId(String targetActivityId) {
        this.targetActivityId = targetActivityId;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getSourceActivityId() {
        return sourceActivityId;
    }

    public void setSourceActivityId(String sourceActivityId) {
        this.sourceActivityId = sourceActivityId;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getActivityState() {
        return activityState;
    }

    public void setActivityState(String activityState) {
        this.activityState = activityState;
    }

    public String getAnchorDate() {
        return anchorDate;
    }

    public void setAnchorDate(String anchorDate) {
        this.anchorDate = anchorDate;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getSchedulingType() {
        return schedulingType;
    }

    public void setSchedulingType(String schedulingType) {
        this.schedulingType = schedulingType;
    }
}
