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
import org.apache.http.client.methods.HttpPost;
import org.json.simple.JSONObject;
import org.labkey.test.WebTestHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class EnrollParticipantCommand extends MobileAppCommand
{
    private static final String APP_TOKEN_JSON_FIELD = "appToken";
    protected static final String CONTROLLER_NAME = "mobileappstudy";
    protected static final String ACTION_NAME = "enroll";

    private String _batchToken;
    private String _studyName;
    private String _appToken;
    private String _projectName;

    public String getProjectName()
    {
        return _projectName;
    }
    public void setProjectName(String projectName)
    {
        _projectName = projectName;
    }

    public EnrollParticipantCommand(String project, String studyName, String batchToken, Consumer<String> logger)
    {
        _studyName = studyName;
        _batchToken = batchToken;
        _projectName = project;

        setLogger(logger);
    }

    public String getStudyName()
    {
        return _studyName;
    }
    public void setStudyName(String studyName)
    {
        _studyName = studyName;
    }

    public String getBatchToken()
    {
        return _batchToken;
    }
    public void setBatchToken(String batchToken)
    {
        _batchToken = batchToken;
    }

    @Override
    public HttpResponse execute(int expectedStatusCode)
    {
        HttpPost post = new HttpPost(getTargetURL());
        return execute(post, expectedStatusCode);
    }

    @Override
    public String getTargetURL()
    {
        Map<String, String> params = new HashMap<>();
        params.put("studyId", getStudyName());
        if (StringUtils.isNotBlank(getBatchToken()))
            params.put("token", getBatchToken());
        return WebTestHelper.buildURL(CONTROLLER_NAME, getProjectName(), ACTION_NAME, params);
    }

    public String getAppToken()
    {
        if (!isExecuted)
            throw new IllegalStateException("Enroll command has not been executed yet");

        return _appToken;
    }

    @Override
    protected void parseSuccessfulResponse(JSONObject response)
    {
        _appToken = (String) ((JSONObject) response.get("data")).get(APP_TOKEN_JSON_FIELD);
    }

    @Override
    public String getBody()
    {
        return "";
    }
}
