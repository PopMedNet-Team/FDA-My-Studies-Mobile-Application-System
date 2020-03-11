package org.labkey.mobileappstudy.forwarder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthenticationException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.cache.Cache;
import org.labkey.api.cache.CacheManager;
import org.labkey.api.data.Container;
import org.labkey.api.pipeline.PipelineJob;
import org.labkey.api.security.User;
import org.labkey.mobileappstudy.MobileAppStudyManager;
import org.labkey.mobileappstudy.data.SurveyResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OAuthForwarder extends Forwarder
{
    private static final int MAX_RETRIES = 1;
    private static final Cache<Container, String> authTokenCache =
            CacheManager.getBlockingCache(100, TimeUnit.HOURS.toMillis(1), "Mobile app forwarding tokens",
                    (key, args) -> requestNewAuthToken(key, Logger.getLogger(OAuthForwarder.class)));

    public OAuthForwarder(Container container, Logger logger)
    {
        super(container, logger);
    }


    @Override
    public PipelineJob.TaskStatus makeRequest(User user, SurveyResponse response) throws Exception
    {
        return makeRequest(user, response, 0);
    }

    private PipelineJob.TaskStatus makeRequest(User user, SurveyResponse response, int attempts) throws Exception
    {
        if (attempts >= MAX_RETRIES)
            return handleError("Exceeded max retry attempts");

        String authToken = authTokenCache.get(container);
        if (StringUtils.isBlank(authToken))
        {
            return handleError("Unable to obtain auth token.");
        }


        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(getForwardingEndpoint()))
                .header(properties.get(ForwarderProperties.TOKEN_HEADER), "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(getRequestBody(response)))
                .build();

        var httpResponse = getClient().send(req, HttpResponse.BodyHandlers.ofString());
        int statusCode = httpResponse.statusCode();

        if (retryForwarding(statusCode))
        {
            authTokenCache.remove(container);
            logger.debug("Request was unauthorized. Clearing token, and attempting retry.");
            return makeRequest(user, response, attempts + 1);
        }
        else if (statusCode < 200 || 300 < statusCode)
        {
            return handleError(String.format("Stopping forwarding job. ResponseId [%1$s] received error response %2$s:\n%3$s", response.getRowId(), httpResponse.statusCode(), httpResponse.body()));
        }
        else
        {
            logger.info(String.format("Successfully forwarded response [%1$s].", response.getRowId()));
            MobileAppStudyManager.get().updateProcessingStatus(user, response.getRowId(), SurveyResponse.ResponseStatus.FORWARDED);
            return PipelineJob.TaskStatus.running;
        }
    }

    private boolean retryForwarding(int statusCode)
    {
        return statusCode == 401; //Unauthorized implies token is invalid/expired, so retry after getting new auth token
    }

    protected HttpClient getClient()
    {
        if (client == null)
        {
            client = HttpClient.newBuilder()
                    .build();
        }

        return client;
    }

    @Override
    public String getForwardingEndpoint()
    {
        return properties.get(ForwarderProperties.OAUTH_URL);
    }

    @Nullable
    private static String requestNewAuthToken(@NotNull Container container, @NotNull Logger logger) //throws IOException, InterruptedException, AuthenticationException
    {
        logger.info("Requesting new auth token.");

        Map<String, String> properties = new ForwarderProperties().getForwarderConnection(container);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(properties.get(ForwarderProperties.TOKEN_REQUEST_URL)))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try
        {
            var httpResponse = HttpClient.newBuilder().build().send(req, HttpResponse.BodyHandlers.ofString());
            int statusCode = httpResponse.statusCode();

            if (statusCode < 200 || 300 < statusCode)
            {
                MobileAppStudyManager.get().setForwardingJobUnsucessful(container);
                throw new AuthenticationException(String.format("Unable to obtain new authentication token. Token request status code [%1$s]. Please check forwarding configuration.", statusCode));
            }

            logger.info("Successfully obtained new auth token.");
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> jsonMap = mapper.readValue(httpResponse.body(), new TypeReference<Map<String, String>>() {});

            return jsonMap.getOrDefault(properties.get(ForwarderProperties.TOKEN_FIELD), "");
        }
        catch (IOException | InterruptedException | AuthenticationException e)
        {
            logger.error(String.format("Unable to obtain new auth token for container [%1$s]. Error message: %2$s ", container.getName(), e.getMessage()), e);
            return null;
        }
    }
}