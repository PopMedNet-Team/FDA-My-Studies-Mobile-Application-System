/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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
