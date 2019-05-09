/*
 * Copyright (c) 2016-2019 LabKey Corporation
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
package org.labkey.test.commands.mobileappstudy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

    protected void parseResponse(String response)
    {
        JSONParser parser = new JSONParser();
        try
        {
            _jsonResponse = (JSONObject) parser.parse(response);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        setSuccess((Boolean) _jsonResponse.get(SUCCESS_TAG));

        if (getSuccess())
            parseSuccessfulResponse(_jsonResponse);
        else
            parseErrorResponse(_jsonResponse);
    }

    protected void parseSuccessfulResponse(JSONObject response)
    {
        //do nothing here
    }

    protected void parseErrorResponse(JSONObject response)
    {
        setExceptionMessage((String) response.get(EXCEPTION_MESSAGE_TAG));
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
            log("Post completed. Response body: " + getBody());

            int statusCode = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(response.getEntity());
            parseResponse(body);

            if (expectedStatusCode < 400 && StringUtils.isNotBlank(getExceptionMessage()))
                log("Unexpected error message: " + getExceptionMessage());

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
