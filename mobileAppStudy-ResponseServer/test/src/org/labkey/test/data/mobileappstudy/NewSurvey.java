/*
 * Copyright (c) 2017-2019 LabKey Corporation
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
