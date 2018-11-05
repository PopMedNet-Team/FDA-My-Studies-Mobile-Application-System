/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.data.mobileappstudy;

import java.util.Date;

/**
 * Created by RyanS on 2/17/2017.
 */
public class NewSurvey extends Survey
{
    public NewSurvey(String appToken, String activityId, String version, Date start, Date end)
    {
        super(appToken, activityId, version, start, end);
    }

    public static final String  SCALE_FIELD = "ScaleField",
            CONTINUOUS_SCALE_FIELD = "ContinusousScaleField",
            INTEGER_FIELD = "IntegerField",
            DOUBLE_FIELD = "DoubleField",
            DATETIME_FIELD = "DateTimeField",
            DATE_FIELD = "DateField",
            BOOLEAN_FIELD = "BooleanField",
            TEXT_CHOICE_FIELD = "TextChoiceField",
            TEXT_SCALE_FIELD = "TextScaleField",
            VALUE_PICKER_FIELD = "ValuePickerField",
            IMAGE_CHOICE_FIELD = "ImageChoiceField",
            TIME_OF_DAY_FIELD = "TimeOfDayField",
            MAX_TEXT_FIELD = "MaxTextField",
            SHORT_TEXT_FIELD = "ShortTextField",
            EMAIL_FIELD = "EmailField",
            TIME_INTERVAL_FIELD = "TimeIntervalField",
            HEIGHT_FIELD = "HeightField",
            LOCATION_FIELD = "LocationField";

}
