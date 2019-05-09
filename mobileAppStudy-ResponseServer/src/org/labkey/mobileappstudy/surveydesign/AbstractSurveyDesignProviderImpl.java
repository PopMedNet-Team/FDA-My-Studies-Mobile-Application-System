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
