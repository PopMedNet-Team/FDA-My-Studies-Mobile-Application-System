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


import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Naveen Raj on 04/06/2017.
 */

public class ActivityListData  extends RealmObject {
    private String message;

    private String withdrawalConfig;

    private AnchorDate anchorDate;

    @PrimaryKey
    private String studyId;

    private RealmList<ActivitiesWS> activities = new RealmList<>();

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWithdrawalConfig() {
        return withdrawalConfig;
    }

    public void setWithdrawalConfig(String withdrawalConfig) {
        this.withdrawalConfig = withdrawalConfig;
    }

    public AnchorDate getAnchorDate() {
        return anchorDate;
    }

    public void setAnchorDate(AnchorDate anchorDate) {
        this.anchorDate = anchorDate;
    }

    public RealmList<ActivitiesWS> getActivities() {
        return activities;
    }

    public void setActivities(RealmList<ActivitiesWS> activities) {
        this.activities = activities;
    }
}
