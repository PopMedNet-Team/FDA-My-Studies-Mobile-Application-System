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
package org.labkey.mobileappstudy.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * A response element in a SurveyResponse type.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response
{
    private Date _startTime;
    private Date _endTime;
    private List<SurveyResult> _results;


    public static Response getResponseObject(String responseString) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(responseString, Response.class);
    }

    public Date getStartTime()
    {
        return _startTime;
    }

    public void setStartTime(Date startTime)
    {
        _startTime = startTime;
    }

    public Date getEndTime()
    {
        return _endTime;
    }

    public void setEndTime(Date endTime)
    {
        _endTime = endTime;
    }

    public void setStart(Date start)
    {
        _startTime = start;
    }

    public void setEnd(Date end)
    {
        _endTime = end;
    }

    public List<SurveyResult> getResults()
    {
        return _results;
    }

    public void setResults(List<SurveyResult> results)
    {
        _results = results;
    }
}
