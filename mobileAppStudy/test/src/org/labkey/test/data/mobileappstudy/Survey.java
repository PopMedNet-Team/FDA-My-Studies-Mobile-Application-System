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
