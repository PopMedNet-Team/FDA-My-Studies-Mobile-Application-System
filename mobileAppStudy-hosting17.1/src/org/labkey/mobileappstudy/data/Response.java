/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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
        this._startTime = startTime;
    }

    public Date getEndTime()
    {
        return _endTime;
    }

    public void setEndTime(Date endTime)
    {
        this._endTime = endTime;
    }

    public void setStart(Date start)
    {
        this._startTime = start;
    }

    public void setEnd(Date end)
    {
        this._endTime = end;
    }

    public List<SurveyResult> getResults()
    {
        return _results;
    }

    public void setResults(List<SurveyResult> results)
    {
        this._results = results;
    }
}
