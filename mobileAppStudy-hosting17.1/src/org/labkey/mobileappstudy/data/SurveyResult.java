/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.labkey.mobileappstudy.surveydesign.SurveyStep;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by susanh on 11/28/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyResult extends ResponseMetadata
{

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private String _resultType;
    private String _key;
    private Object _value;
    private Object _parsedValue;
    private String _listName;

    public SurveyStep.StepResultType getStepResultType() { return SurveyStep.StepResultType.getStepResultType(getResultType()); }

    public String getResultType()
    {
        return _resultType;
    }

    public void setResultType(String resultType)
    {
        this._resultType = resultType;
    }

    public String getKey()
    {
        return _key;
    }

    public void setKey(String key)
    {
        this._key = key;
    }

    @Override
    public String getListName()
    {
        return _listName;
    }

    @Override
    public void setListName(String listName)
    {
        this._listName = listName;
    }

    public Object getValue()
    {
        return _value;
    }
    public void setValue(Object result) throws IllegalArgumentException
    {
        _value = result;
    }


    public Object getParsedValue()
    {
        if (_parsedValue == null && !getSkipped() && _value != null)
            setParsedValue();
        return _parsedValue;
    }

    private void setParsedValue()
    {
        switch (getStepResultType())
        {
            case Date:
                if (_value instanceof String)
                {
                    // first try to parse it as a DateTime value
                    try
                    {
                        this._parsedValue = DATE_TIME_FORMAT.parse((String) _value);
                    }
                    catch (ParseException e1) // then try as a Date value
                    {
                        try
                        {
                            this._parsedValue = DATE_FORMAT.parse((String) _value);
                        }
                        catch (ParseException e2)
                        {
                            throw new IllegalArgumentException("Invalid date string format for field '" + getKey() + "' (" + _value + ")");
                        }
                    }
                }
                else
                    throw new IllegalArgumentException("Value type for Date field '" + getKey() + "' expected to be String but got "+ _value.getClass());
                break;
            case Boolean:
                if (_value instanceof Boolean)
                    this._parsedValue = _value;
                else
                    throw new IllegalArgumentException("Value type for field '" + getKey() + "' expected to be Boolean but got " + _value.getClass());
                break;
            case TextChoice:
                if (_value instanceof List)
                {
                    this._parsedValue = _value;
                }
                else
                    throw new IllegalArgumentException("Value type for choice field '" + getKey() + "' expected to be ArrayList but got " + _value.getClass());
                break;
            case Numeric:
            case Height:
            case Scale:
            case ContinuousScale:
            case TimeInterval:
                if (_value instanceof Double || _value instanceof Float || _value instanceof Integer)
                {
                    this._parsedValue = _value;
                }
                else
                    throw new IllegalArgumentException("Value type for field '" + getKey() + "' expected to be Integer or Float but got " + _value.getClass());
                break;
            case GroupedResult:
            case FetalKickCounter:
            case TowerOfHanoi:
            case SpatialSpanMemory:
                if (_value instanceof List)
                {
                    this._parsedValue = convertSurveyResults((List) _value);
                }
                else
                    throw new IllegalArgumentException("Value type for grouped result field '" + getKey() + "' expected to be ArrayList but got " + _value.getClass());
                break;
            case TextScale:
            case ValuePicker:
            case ImageChoice:
            case Email:
            case Location:
            case Text:
            case TimeOfDay:
                if (_value instanceof String)
                    this._parsedValue = _value;
                else
                    throw new IllegalArgumentException("Value type for field '" + getKey() + "' expected to be String but got " + _value.getClass());
                break;
        }
    }


    /**
     * recursively convert a list of survey results, which may itself contain lists of survey results, into a list of objects.
     * The leaves of this object tree are of type SurveyResult.
     *
     * @param list The list of objects to be converted
     * @return a list of either SurveyResult objects or List containing the result of a conversion.
     */
    private List<Object> convertSurveyResults(List list)
    {
        List<Object> results = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (Object item : list)
        {
            if (item instanceof List)
            {
                results.add(convertSurveyResults((List) item));
            }
            else if (item instanceof LinkedHashMap)
            {
                results.add(mapper.convertValue(item, SurveyResult.class));
            }
            else
            {
                throw new IllegalArgumentException("Value type for grouped result field '" + getKey() + "' expected to be ArrayList or HashMap but got " + item.getClass());
            }
        }
        return results;
    }
}
