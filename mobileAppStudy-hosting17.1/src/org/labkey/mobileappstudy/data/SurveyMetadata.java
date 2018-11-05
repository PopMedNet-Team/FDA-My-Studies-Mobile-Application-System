/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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
