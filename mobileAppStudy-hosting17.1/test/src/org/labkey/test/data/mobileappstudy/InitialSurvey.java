/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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
