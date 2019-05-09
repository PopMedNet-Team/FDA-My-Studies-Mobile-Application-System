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
package org.labkey.mobileappstudy.surveydesign;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;
import org.labkey.mobileappstudy.data.SurveyMetadata;

import java.util.List;

/**
 * Created by iansigmon on 2/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyDesign
{
    private String _type;
    private SurveyMetadata _metadata;
    private List<SurveyStep> steps;

    public String getType()
    {
        return _type;
    }

    public void setType(String type)
    {
        _type = type;
    }

    public List<SurveyStep> getSteps()
    {
        return steps;
    }
    public void setSteps(List<SurveyStep> steps)
    {
        this.steps = steps;
    }

    public SurveyMetadata getMetadata()
    {
        return _metadata;
    }
    public void setMetadata(SurveyMetadata surveyMetadata)
    {
        _metadata = surveyMetadata;
    }

    @JsonIgnore
    public String getSurveyName()
    {
        return _metadata.getActivityId();
    }

    @JsonIgnore
    public boolean isValid()
    {
        return _metadata != null && !StringUtils.isEmpty(getSurveyName())
                && steps != null;
    }

}
