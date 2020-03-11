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
package org.labkey.test.commands.mobileappstudy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.labkey.test.WebTestHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by RyanS on 1/23/2017.
 */
public class WithdrawParticipantCommand extends MobileAppCommand
{
    private static final String APP_TOKEN_JSON_FIELD = "appToken";
    protected static final String CONTROLLER_NAME = "mobileappstudy";
    protected static final String ACTION_NAME = "withdrawfromstudy";

    private String _participantId;
    private Boolean _delete;

    public WithdrawParticipantCommand(String participantId, Boolean delete, Consumer<String> logger)
    {
        _participantId = participantId;
        _delete = delete;
        setLogger(logger);
    }

    public void setParticipantId(String participantId)
    {
        _participantId = participantId;
    }
    public void setDelete(Boolean delete){_delete = delete;}

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
        params.put("delete", String.valueOf(getDelete()));
        if (StringUtils.isNotBlank(getParticipantId()))
            params.put("participantId", getParticipantId());
        return WebTestHelper.buildURL(CONTROLLER_NAME, ACTION_NAME, params);
    }

    public String getParticipantId()
    {
//        if (!isExecuted)
//            throw new IllegalStateException("Enroll command has not been executed yet");

        return _participantId;
    }

    public Boolean getDelete()
    {
        return _delete;
    }

//    @Override
//    protected void parseSuccessfulResponse(JSONObject response)
//    {
//        _participantId = response.getJSONObject("data").getString(APP_TOKEN_JSON_FIELD);
//    }

    @Override
    public String getBody()
    {
        return "";
    }
}
