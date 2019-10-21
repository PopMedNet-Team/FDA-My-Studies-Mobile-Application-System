package org.labkey.mobileappstudy.forwarder;

import org.apache.log4j.Logger;
import org.labkey.api.data.Container;
import org.labkey.api.pipeline.PipelineJob;
import org.labkey.api.security.User;
import org.labkey.mobileappstudy.MobileAppStudyManager;
import org.labkey.mobileappstudy.data.SurveyResponse;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BasicAuthForwarder extends Forwarder
{
    public BasicAuthForwarder(Container container, Logger logger)
    {
        super(container, logger);
    }

    protected HttpClient getClient()
    {
        String username = properties.get(ForwarderProperties.USER_PROPERTY_NAME);
        String pass = properties.get(ForwarderProperties.PASSWORD_PROPERTY_NAME);

        if (client == null)
        {
            client = HttpClient.newBuilder()
                    .authenticator(getPasswordAuthenticator(username, pass))
                    .build();
        }
        return client;
    }

    @Override
    public PipelineJob.TaskStatus makeRequest(User user, SurveyResponse response) throws Exception
    {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getForwardingEndpoint()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(getRequestBody(response)))
                .build();

        var httpResponse = getClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() < 200 || 300 < httpResponse.statusCode())
            return handleError(String.format("Stopping forwarding job. ResponseId [%1$s] received error response %2$s:\n%3$s", response.getRowId(), httpResponse.statusCode(), httpResponse.body()));

        logger.info(String.format("Successfully forwarded response [%1$s].", response.getRowId()));
        MobileAppStudyManager.get().updateProcessingStatus(user, response.getRowId(), SurveyResponse.ResponseStatus.FORWARDED);

        return PipelineJob.TaskStatus.running;
    }

    @Override
    public String getForwardingEndpoint()
    {
        return properties.get(ForwarderProperties.URL_PROPERTY_NAME);
    }

    /**
     * Provide a basic auth authenticator
     * @param username for auth
     * @param pass for auth
     * @return java.net.Authenticator to use for basic auth with provided credentials
     */
    private Authenticator getPasswordAuthenticator(String username, String pass)
    {
        return new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, pass.toCharArray());
            }
        };
    }
}
