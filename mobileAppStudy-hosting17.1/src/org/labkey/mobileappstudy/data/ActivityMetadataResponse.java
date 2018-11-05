/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.labkey.mobileappstudy.surveydesign.SurveyDesign;

/**
 * Created by susanh on 5/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityMetadataResponse
{
    private String message;
    private SurveyDesign activity;

    public SurveyDesign getActivity()
    {
        return activity;
    }

    public void setActivity(SurveyDesign activity)
    {
        this.activity = activity;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
