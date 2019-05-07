/*
 * Copyright (c) 2016-2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.test.data.mobileappstudy;

import java.util.Date;

/**
 * Created by iansigmon on 11/30/16.
 */
public class InitialSurvey extends Survey
{
    public InitialSurvey(String appToken, String activityId, String version, Date start, Date end)
    {
        super(appToken, activityId, version, start, end);
    }

    //Question IDs
    public static final String  LAST_PERIOD = "lastPeriod",
                                DUE_DATE = "dueDate",
                                BREAKFAST_TIME = "breakfastTime",
                                DATE_PREGNANCY_LEARNED = "datePregnancyLearned",
                                PLANNED_PREGNANCY = "plannedPregnancy",
                                BIRTH_CONTROL_TYPE = "birthControlType",
                                SUPPLEMENTS = "supplements",
                                FOLIC_ACID = "folicAcid",
                                FORMER_SMOKER = "formerSmoker",
                                NUM_PACKS_WEEK = "NumPacksWeek",
                                NUM_ALCOHOL_WEEK = "NumAlcoholWeek",
                                ILLNESS_WEEK = "IllnessWeek",
                                START_TIME = "StartTime",
                                END_TIME = "endTime";


}
