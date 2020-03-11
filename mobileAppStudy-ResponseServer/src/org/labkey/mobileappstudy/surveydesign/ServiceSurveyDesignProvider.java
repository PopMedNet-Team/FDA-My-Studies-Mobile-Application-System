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

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.labkey.api.data.Container;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.mobileappstudy.MobileAppStudyModule;

import java.net.URI;

/**
 * Created by susanh on 3/10/17.
 */
public class ServiceSurveyDesignProvider extends AbstractSurveyDesignProviderImpl
{
    private static final String STUDY_ID_PARAM = "studyId";
    private static final String ACTIVITY_ID_PARAM = "activityId";
    private static final String VERSION_PARAM = "activityVersion";

    public ServiceSurveyDesignProvider(Container container, Logger logger)
    {
        super(container, logger);
    }

    @Override
    public SurveyDesign getSurveyDesign(Container c, String shortName, String activityId, String version) throws Exception
    {
        URIBuilder uriBuilder = new URIBuilder(getServiceUrl(c));
        uriBuilder.setParameter(STUDY_ID_PARAM, shortName);
        uriBuilder.setParameter(ACTIVITY_ID_PARAM, activityId);
        uriBuilder.setParameter(VERSION_PARAM, version);
        URI uri = uriBuilder.build();
        try (CloseableHttpClient httpclient = HttpClients.createDefault())
        {
            HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader("Authorization", "Basic " + getServiceToken(c));

            try (CloseableHttpResponse response = httpclient.execute(httpGet))
            {
                ResponseHandler<String> handler = new BasicResponseHandler();
                StatusLine status = response.getStatusLine();

                if (status.getStatusCode() == HttpStatus.SC_OK || status.getStatusCode() == HttpStatus.SC_CREATED)
                {
                    return getSurveyDesign(handler.handleResponse(response));
                }
                else
                {
                    throw new Exception(String.format("Received response status %d using uri %s",  status.getStatusCode(), uri));
                }
            }
        }
    }

    private static String getServiceToken(Container container)
    {
        Module module = ModuleLoader.getInstance().getModule(MobileAppStudyModule.NAME);
        return module.getModuleProperties().get(MobileAppStudyModule.METADATA_SERVICE_ACCESS_TOKEN).getEffectiveValue(container);
    }

    private static String getServiceUrl(Container container)
    {
        Module module = ModuleLoader.getInstance().getModule(MobileAppStudyModule.NAME);
        String value = module.getModuleProperties().get(MobileAppStudyModule.METADATA_SERVICE_BASE_URL).getEffectiveValue(container);
        return value == null ? null : value.trim();
    }

    public static Boolean isConfigured(Container c)
    {
        return !StringUtils.isEmpty(getServiceToken(c)) && !StringUtils.isEmpty(getServiceUrl(c));
    }
}
