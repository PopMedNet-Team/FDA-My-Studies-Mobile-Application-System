/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.data.mobileappstudy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by iansigmon on 11/30/16.
 */
public abstract class Survey extends Form
{
    private static final String RESPONSE_FORMAT = "{\n" + "%1$s\n" + "}";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private String _appToken;
    private String _activityId;
    private String _version;
    private Date _start;
    private Date _end;

    private boolean _omitStart;
    private boolean _omitEnd;
    private boolean _omitResults;

    public Survey(String appToken, String activityId, String version, Date start, Date end)
    {
        _appToken = appToken;
        _activityId = activityId;
        _version = version;
        _start = start;
        _end = end;
    }

    public String getVersion()
    {
        return _version;
    }

    public String getActivityId()
    {
        return _activityId;
    }

    public String getAppToken()
    {
        return _appToken;
    }

    public Date getEnd()
    {
        return _end;
    }

    public Date getStart()
    {
        return _start;
    }

    public void setOmitResults(boolean omitResults)
    {
        _omitResults = omitResults;
    }

    public void setOmitEnd(boolean omitEnd)
    {
        _omitEnd = omitEnd;
    }

    public void setOmitStart(boolean omitStart)
    {
        _omitStart = omitStart;
    }



    public String getResponseJson()
    {
        String format = getFormatString();

        return String.format(getFormatString(), DATE_FORMAT.format(getStart()), DATE_FORMAT.format(getEnd()),
            String.join(",\n",
                    responses.stream().map(QuestionResponse::getJsonString).collect(Collectors.toList())
            )
        );
    }

    private String getFormatString()
    {
        ArrayList<String> props = new ArrayList();
        if (!_omitStart)
            props.add("\"start\": \"%1$s\"");
        if (!_omitEnd)
            props.add("\"end\": \"%2$s\"");
        if (!_omitResults)
            props.add("\"results\": [%3$s]");

        return "{\n" + String.join(",\n", props) + "}";
    }
}
