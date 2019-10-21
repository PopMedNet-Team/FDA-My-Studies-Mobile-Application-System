/*
 * Copyright (c) 2019 LabKey Corporation
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
package org.labkey.mobileappstudy.forwarder;

import org.labkey.api.data.Container;
import org.labkey.api.pipeline.PipeRoot;
import org.labkey.api.pipeline.PipelineJob;
import org.labkey.api.util.FileUtil;
import org.labkey.api.util.URLHelper;
import org.labkey.api.view.ViewBackgroundInfo;
import org.labkey.mobileappstudy.MobileAppStudyManager;
import org.labkey.mobileappstudy.data.SurveyResponse;

import java.io.File;
import java.util.Collection;

public class SurveyResponsePipelineJob extends PipelineJob
{

    // For serialization
    protected SurveyResponsePipelineJob()
    {}

    public SurveyResponsePipelineJob(ViewBackgroundInfo vbi, PipeRoot root)
    {
        super(null, vbi, root);
        setLogFile(new File(root.getLogDirectory(), FileUtil.makeFileNameWithTimestamp("surveyResponseForwarder", "log")));
    }

    @Override
    public URLHelper getStatusHref()
    {
        return null;
    }

    @Override
    public String getDescription()
    {
        return String.format("Survey Response forwarding for %1$s", getContainer().getName());
    }

    @Override
    public void run()
    {
        this.setStatus(TaskStatus.running);
        Container container = getContainer();

        Collection<SurveyResponse> responses = MobileAppStudyManager.get().getResponsesByStatus(SurveyResponse.ResponseStatus.PROCESSED, container);
        if (responses.size() == 0)
        {
            info("No responses to forward");
            this.setStatus(TaskStatus.complete);
            return;
        }

        Forwarder forwarder = ForwarderProperties.getForwardingType(container).getForwarder(getContainer(), getLogger());
        if (forwarder == null)
        {
            info("Forwarding not enabled. Please verify configuration for this container.");
            this.setStatus(TaskStatus.error);
            return;
        }

        String url = forwarder.getForwardingEndpoint();
        debug(String.format("Forwarding %1$s response(s) to: %2$s", responses.size(), url));
        for (SurveyResponse response : responses)
        {
            try
            {
                if (forwarder.makeRequest(getUser(), response) == TaskStatus.error)
                {
                    //Error handled within request method
                    setStatus(TaskStatus.error);
                    return;
                }
            }
            catch (Throwable e)
            {
                this.setStatus(TaskStatus.error);
                error(String.format("Failed forwarding responseId [%1$s] with: %2$s", response.getRowId(), e.getLocalizedMessage()), e);
                MobileAppStudyManager.get().setForwardingJobUnsucessful(container);
                return;
            }
        }

        info(String.format("Forwarding completed. %1$s response(s) sent to %2$s.", responses.size(), url));
        this.setStatus(TaskStatus.complete);
    }
}
