package org.labkey.mobileappstudy.forwarder;

import org.apache.log4j.Logger;
import org.labkey.api.data.Container;
import org.labkey.api.pipeline.PipelineJob;
import org.labkey.api.security.User;
import org.labkey.mobileappstudy.MobileAppStudyManager;
import org.labkey.mobileappstudy.data.SurveyResponse;

import java.net.http.HttpClient;
import java.util.Collections;
import java.util.Map;

public abstract class Forwarder
{
    protected final Container container;
    protected final Map<String, String> properties;
    protected HttpClient client;
    private static final String FORWARD_JSON_FORMAT = "{\"type\": \"SurveyResponse\", \"metadata\": {\"activityid\": \"%1$s\", \"version\": \"%2$s\"}, \"token\": \"%3$s\", \"data\": %4$s }";
    protected Logger logger;

    public Forwarder(Container container, Logger logger)
    {
        this.logger = logger != null ? logger : Logger.getLogger(Forwarder.class.getName());
        this.container = container;
        this.properties = Collections.unmodifiableMap(new ForwarderProperties().getForwarderConnection(container));
    }

    public abstract PipelineJob.TaskStatus makeRequest(User user, SurveyResponse response) throws Exception;
    public abstract String getForwardingEndpoint();

    public PipelineJob.TaskStatus handleError(String msg)
    {
        logger.error(msg);
        MobileAppStudyManager.get().setForwardingJobUnsucessful(container);
        return PipelineJob.TaskStatus.error;
    }

    protected String getRequestBody(SurveyResponse response)
    {
        String token = MobileAppStudyManager.get().getEnrollmentToken(response.getContainer(), response.getParticipantId());
        return String.format(FORWARD_JSON_FORMAT, response.getActivityId(), response.getSurveyVersion(), token, response.getData());
    }
}
