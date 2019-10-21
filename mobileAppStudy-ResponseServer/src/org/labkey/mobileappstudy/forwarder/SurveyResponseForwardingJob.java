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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.pipeline.PipeRoot;
import org.labkey.api.pipeline.PipelineJob;
import org.labkey.api.pipeline.PipelineService;
import org.labkey.api.pipeline.PipelineValidationException;
import org.labkey.api.security.LimitedUser;
import org.labkey.api.security.PrincipalType;
import org.labkey.api.security.User;
import org.labkey.api.security.roles.ReaderRole;
import org.labkey.api.security.roles.RoleManager;
import org.labkey.api.util.ConfigurationException;
import org.labkey.api.view.ViewBackgroundInfo;
import org.labkey.mobileappstudy.MobileAppStudyManager;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class SurveyResponseForwardingJob implements org.quartz.Job, Callable<String>
{
    private static final Logger logger = Logger.getLogger(SurveyResponseForwardingJob.class);
    private static volatile Set<String> unsuccessful = new HashSet<>();

    // Private service user
    private static User forwardingUser;
    private static synchronized User getForwardingUser()
    {
        if (forwardingUser == null)
        {
            forwardingUser = new LimitedUser(User.getSearchUser(), new int[0], Collections.singleton(RoleManager.getRole(ReaderRole.class)), false);
            forwardingUser.setPrincipalType(PrincipalType.SERVICE);
        }
        return forwardingUser;
    }

    @Override
    public String call() throws Exception
    {
        User user = getForwardingUser();
        ForwardingScheduler.get().enabledContainers().forEach(c -> call(user, ContainerManager.getForId(c)));

        return "Pipeline jobs enqueued.";
    }

    public void call(User user, Container c)
    {
        if (!validateCall(c))
            return;

        try
        {
            logger.info(String.format("Adding pipeline job to forward responses for container [%1$s].", c.getName()));
            enqueuePipelineJob(user, c);
        }
        catch (ConfigurationException e)
        {
            logger.error(e.getLocalizedMessage());
            setUnsuccessful(c);
        }
    }

    private boolean validateCall(Container c)
    {
        String msg = null;

        if (null == c)
        {
            logger.debug("SurveyResponseForwardingJob, response forwarding job called with a 'null' container");
            msg = "Forwarding not enabled for null container.";
        }
        //Since this can be called outside of timer job, check if enabled
        else if (!ForwardingScheduler.get().forwardingIsEnabled(c))
        {
            msg = String.format("Forwarding not enabled for container [%1$s].", c.getName());
        }
        //Check if container was unsuccessful recently
        else if (unsuccessful.contains(c.getId()))
        {
            msg = String.format("Not forwarding survey responses for container [%1$s] because target endpoint is marked as unsuccessful.", c.getName());
        }
        // Check if anything to process
        else if (!MobileAppStudyManager.get().hasResponsesToForward(c))
        {
            msg = String.format("No responses to forward for [%1$s]", c.getName());
        }

        if (StringUtils.isNotBlank(msg))
        {
            logger.debug(msg);
            return false;
        }
        else
            return true;
    }

    private void enqueuePipelineJob(User user, Container c)
    {
        ViewBackgroundInfo vbi = new ViewBackgroundInfo(c, user, null);
        PipeRoot root = PipelineService.get().findPipelineRoot(c);

        if (null == root)
            throw new ConfigurationException(String.format("Invalid pipeline configuration: No pipeline root for container [%1$s]", c.getId()));

        if (!root.isValid())
            throw new ConfigurationException(String.format("Invalid pipeline root configuration: %1$s", root.getRootPath().getPath()));

        try
        {
            PipelineJob job = new SurveyResponsePipelineJob(vbi, root);
            logger.debug(String.format("Queuing forwarder for container %1$s on [thread %2$s to %3$s]",
                    c.getName(), Thread.currentThread().getName(), PipelineService.get().toString()));
            PipelineService.get().queueJob(job);
            logger.debug(String.format("Job [%1$s] added", job.getJobGUID()));
        }
        catch (PipelineValidationException e)
        {
            setUnsuccessful(c);
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void execute(JobExecutionContext context)
    {
        try
        {
            // Reset success status
            unsuccessful = new HashSet<>();
            this.call();
        }
        catch (Exception e)
        {
            logger.error("The timer triggered SurveyResponse forwarding job failed with an error: " + e.getLocalizedMessage(), e);
        }
    }

    public void setUnsuccessful(Container c)
    {
        unsuccessful.add(c.getId());
    }
}
