/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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

    public WithdrawParticipantCommand(String participantId,Boolean delete, Consumer<String> logger)
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
