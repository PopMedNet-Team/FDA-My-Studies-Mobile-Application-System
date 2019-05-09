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

package com.harvard.fda.studyAppModule;

import com.harvard.fda.studyAppModule.acvitityListModel.ActivitiesWS;
import com.harvard.fda.studyAppModule.survayScheduler.model.ActivityStatus;

import java.util.ArrayList;

/**
 * Created by Rohith on 8/28/2017.
 */

public class Filter {
    private ArrayList<String> status = new ArrayList<>();
    private ArrayList<ActivitiesWS> activitiesArrayList1 = new ArrayList<>();
    private ArrayList<ActivityStatus> currentRunStatusForActivities = new ArrayList<>();

    public ArrayList<String> getStatus() {
        return status;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }

    public ArrayList<ActivitiesWS> getActivitiesArrayList1() {
        return activitiesArrayList1;
    }

    public void setActivitiesArrayList1(ArrayList<ActivitiesWS> activitiesArrayList1) {
        this.activitiesArrayList1 = activitiesArrayList1;
    }

    public ArrayList<ActivityStatus> getCurrentRunStatusForActivities() {
        return currentRunStatusForActivities;
    }

    public void setCurrentRunStatusForActivities(ArrayList<ActivityStatus> currentRunStatusForActivities) {
        this.currentRunStatusForActivities = currentRunStatusForActivities;
    }
}
