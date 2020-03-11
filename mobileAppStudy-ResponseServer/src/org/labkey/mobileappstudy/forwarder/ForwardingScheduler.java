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

import org.apache.log4j.Logger;
import org.labkey.api.collections.ConcurrentHashSet;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.mobileappstudy.MobileAppStudyManager;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ForwardingScheduler
{
    private static JobDetail job = null;
    private static final Logger logger = Logger.getLogger(ForwardingScheduler.class);
    private static final int INTERVAL_MINUTES = 5;
    private static final ForwardingScheduler instance = new ForwardingScheduler();
    private static  Set<String> enabledContainers = new ConcurrentHashSet<>();
    private TriggerKey triggerKey;

    private ForwardingScheduler()
    {
    }

    public static ForwardingScheduler get()
    {
        return instance;
    }

    public synchronized void schedule()
    {
        enabledContainers = refreshEnabledContainers();

        if (job == null)
        {
            job = JobBuilder.newJob(SurveyResponseForwardingJob.class)
                    .withIdentity(ForwardingScheduler.class.getCanonicalName(), ForwardingScheduler.class.getCanonicalName())
                    .usingJobData("surveyResponseForwarder", ForwardingScheduler.class.getCanonicalName())
                    .build();
        }

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(ForwardingScheduler.class.getCanonicalName(), ForwardingScheduler.class.getCanonicalName())
                .withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInMinutes(getIntervalMinutes()))
                .forJob(job)
                .build();

        this.triggerKey = trigger.getKey();

        try
        {
            StdSchedulerFactory.getDefaultScheduler().scheduleJob(job, trigger);
            logger.info(String.format("SurveyResponseForwarder scheduled to run every %1$S minutes. Next runtime %2$s", getIntervalMinutes(), trigger.getNextFireTime()));
        }
        catch (SchedulerException e)
        {
            logger.error("Failed to schedule SurveryResponseForwarder.", e);
        }
    }

    public synchronized void unschedule()
    {
        try
        {
            StdSchedulerFactory.getDefaultScheduler().unscheduleJob(triggerKey);
            logger.info(String.format("SurveyResponseForwarder has been unscheduled."));
        }
        catch (SchedulerException e)
        {
            logger.error("Failed to unschedule SurveryResponseForwarder.", e);
        }
    }

    private Set<String> refreshEnabledContainers()
    {
        Set<String> refreshedContainers = new HashSet<>();
        Collection<String> containers = MobileAppStudyManager.get().getStudyContainers();
        containers.stream().distinct().forEach(c -> {
            if (MobileAppStudyManager.get().isForwardingEnabled(ContainerManager.getForId(c)))
                refreshedContainers.add(c);
        });

        return refreshedContainers;
    }

    public Collection<String> enabledContainers()
    {
        return Collections.unmodifiableSet(enabledContainers);
    }

    public void enableContainer(Container c, boolean enable)
    {
        if (enable)
            enabledContainers.add(c.getId());
        else
            enabledContainers.remove(c.getId());
    }

    public boolean forwardingIsEnabled(Container c)
    {
        if (null == c)
            return false;

        return enabledContainers.contains(c.getId());
    }

    protected int getIntervalMinutes()
    {
        return INTERVAL_MINUTES;
    }
}
