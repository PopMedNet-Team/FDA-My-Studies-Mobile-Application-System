/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.data.mobileappstudy;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by iansigmon on 11/23/16.
 */
public class QuestionResponse extends AbstractQuestionResponse
{
    private Object _result;

    public Object getResult()
    {
        return _result;
    }
    public void setResult(Object result)
    {
        _result = result;
    }

    protected QuestionResponse(Object result)
    {
        setResult(result);
    }

    public QuestionResponse(SupportedResultType resultType, String questionId, Date start, Date end, boolean skipped, Object result)
    {
        setType(resultType);
        setQuestionId(questionId);
        setStart(start);
        setEnd(end);
        setSkipped(skipped);
        setResult(result);
    }

    public String getJsonString()
    {
        return String.format(getQuestionResponseJsonFormat(), getType().getDisplayText(), getQuestionId(), DATE_FORMAT.format(getStart()),
                DATE_FORMAT.format(getEnd()), getSkipped(), getResultJsonString());
    }

    protected String getResultJsonString()
    {
        return String.format(getFormatString(), getResult());
    }

    private String _formatString;

    //Allow overridding the type string
    public String getFormatString()
    {
        return StringUtils.defaultIfBlank(_formatString, getType().getFormatString());
    }

    public void setFormatString(String formatString)
    {
        _formatString = formatString;
    }
}
