/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.commands.mobileappstudy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;


public abstract class MobileAppCommand
{
    public static final String CONTROLLER_NAME = "mobileappstudy";

    protected static final String MESSAGE_TAG = "Message";
    protected static final String ERRORS_TAG = "Errors";
    protected static final String EXCEPTION_MESSAGE_TAG = "exception";
    protected static final String SUCCESS_TAG = "success";

    private String _exceptionMessage;
    private boolean _success;

    public boolean getSuccess()
    {
        return _success;
    }
    public void setSuccess(boolean success)
    {
        _success = success;
    }

    public String getExceptionMessage()
    {
        return _exceptionMessage;
    }
    public void setExceptionMessage(String exceptionMessage)
    {
        _exceptionMessage = exceptionMessage;
    }

    private Consumer<String> logger;
    protected boolean isExecuted = false;
    protected JSONObject _jsonResponse;

    public abstract HttpResponse execute(int expectedStatusCode);
    public abstract String getTargetURL();

    protected void parseResponse(JSONObject response)
    {
        _jsonResponse = response;
        setSuccess(response.getBoolean(SUCCESS_TAG));

        if (getSuccess())
            parseSuccessfulResponse(response);
        else
            parseErrorResponse(response);
    }

    protected void parseSuccessfulResponse(JSONObject response)
    {
        //do nothing here
    }

    protected void parseErrorResponse(JSONObject response)
    {
        setExceptionMessage(response.getString(EXCEPTION_MESSAGE_TAG));
    }

    public void log(String text)
    {
        logger.accept(text);
    }

    public void setLogger(Consumer<String> logger)
    {
        this.logger = logger;
    }

    protected HttpResponse execute(HttpUriRequest request, int expectedStatusCode)
    {
        HttpResponse response = null;
        log("Submitting request using url: " + request.getURI());

        try (CloseableHttpClient client = HttpClients.createDefault())
        {
            response = client.execute(request);
            isExecuted = true;
            log("Post completed.");

            int statusCode = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(response.getEntity());
            parseResponse(new JSONObject(body));

            if (expectedStatusCode < 400 && StringUtils.isNotBlank(getExceptionMessage()))
                log("Unexpected error message: " + getExceptionMessage());

            if (expectedStatusCode != statusCode)
                log(getBody());

            assertEquals("Unexpected response status", expectedStatusCode, statusCode);
            return response;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Test failed requesting the URL,", e);
        }
        finally
        {
            if (response != null)
                EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    public abstract String getBody();
}
