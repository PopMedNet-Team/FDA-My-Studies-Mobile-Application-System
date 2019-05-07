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
package org.labkey.mobileappstudy.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by iansigmon on 11/3/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyMetadata
{
    private String _studyId;
    private String _activityId;
    private String _version;

    public String getVersion()
    {
        return _version;
    }
    public void setVersion(String version)
    {
        _version = version;
    }

    public String getActivityId()
    {
        return _activityId;
    }
    public void setActivityId(String activityId)
    {
        _activityId = activityId;
    }

    public String getStudyId()
    {
        return _studyId;
    }
    public void setStudyId(String studyId)
    {
        _studyId = studyId;
    }
}
