/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.surveydesign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.labkey.api.data.Container;
import org.labkey.mobileappstudy.data.ActivityMetadataResponse;

import java.io.IOException;

/**
 * Created by iansigmon on 2/2/17.
 */
public abstract class AbstractSurveyDesignProviderImpl implements SurveyDesignProvider
{
    protected Container container;
    protected final Logger logger;

    public AbstractSurveyDesignProviderImpl(Container container, Logger logger)
    {
        this.container = container;
        this.logger = logger;
    }

    protected SurveyDesign getSurveyDesign(String contents) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        ActivityMetadataResponse response = mapper.readValue(contents, ActivityMetadataResponse.class);
        return response == null ? null : response.getActivity();
    }
}
