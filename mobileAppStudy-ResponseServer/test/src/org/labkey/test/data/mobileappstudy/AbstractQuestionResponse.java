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

/**
 * Created by iansigmon on 11/23/16.
 */
public abstract class AbstractQuestionResponse implements QuestionResult
{
    public enum SupportedResultType
    {
        BOOL("boolean", "%1$s"),
        TIME_OF_DAY("timeOfDay", "%1$s"),
        CHOICE("textchoice", "[%1$s]"),
        DATE("date", "\"%1$tY-%1$tm-%1$td\""),
        GROUPED_RESULT("grouped", "[%1$s]"),
        NUMERIC("numeric", "%1$s"),
        SCALE("scale", "%1$s"),
        TEXT("text", "\"%1$s\"");

        SupportedResultType(String displayText, String formatString)
        {
            this.displayText = displayText;
            _formatString = formatString;
        }

        private final String displayText;
        private final String _formatString;

        public final String getDisplayText()
        {
            return displayText;
        }
        public final String getFormatString()
        {
            return _formatString;
        }
    }

    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private SupportedResultType _type;
    private String _questionId;
    private Date _start;
    private Date _end;
    private boolean _skipped;

    private boolean _omitType = false;
    private boolean _omitQuestionId = false;
    private boolean _omitStart = false;
    private boolean _omitEnd = false;
    private boolean _omitSkipped = false;
    private boolean _omitResult = false;

    public boolean getSkipped()
    {
        return _skipped;
    }
    public void setSkipped(boolean skipped)
    {
        _skipped = skipped;
    }
    public void setOmitSkipped(boolean omitSkipped)
    {
        _omitSkipped = omitSkipped;
    }

    public Date getEnd()
    {
        return _end;
    }
    public void setEnd(Date end)
    {
        _end = end;
    }
    public void setOmitEnd(boolean omitEnd)
    {
        _omitEnd = omitEnd;
    }

    public Date getStart()
    {
        return _start;
    }
    public void setStart(Date start)
    {
        _start = start;
    }
    public void setOmitStart(boolean omitStart)
    {
        _omitStart = omitStart;
    }

    public String getQuestionId()
    {
        return _questionId;
    }
    public void setQuestionId(String questionId)
    {
        _questionId = questionId;
    }
    public void setOmitQuestionId(boolean omitQuestionId)
    {
        _omitQuestionId = omitQuestionId;
    }

    public SupportedResultType getType()
    {
        return _type;
    }
    public void setType(SupportedResultType type)
    {
        _type = type;
    }
    public void setOmitType(boolean omitType)
    {
        _omitType = omitType;
    }

    public abstract String getJsonString();
    public abstract Object getResult();
    public void setOmitResult(boolean flag)
    {
        _omitResult = flag;
    }

    String getQuestionResponseJsonFormat()
    {
        ArrayList<String> props = new ArrayList();
        if (!_omitType)
            props.add("\"resultType\": \"%1$s\"");
        if (!_omitQuestionId)
            props.add("\"key\": \"%2$s\"");
        if (!_omitStart)
            props.add("\"start\": \"%3$s\"");
        if (!_omitEnd)
            props.add("\"end\": \"%4$s\"");
        if (!_omitSkipped)
            props.add("\"skipped\": %5$b");
        if (!_omitResult)
            props.add("\"value\": %6$s");

        return "{\n" + String.join(",\n", props) + "}";
    }

}
