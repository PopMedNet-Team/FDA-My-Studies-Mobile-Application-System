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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.labkey.api.data.Container;

import java.util.Date;

/**
 * Representation of the mobileappstudy.responsemetadata table.
 */
public class ResponseMetadata
{
    private Integer _rowId;
    private Container _container;
    private String _listName;
    private Integer _activityId;
    private String _fieldName;
    private Date _startTime;
    private Date _endTime;
    private Boolean _skipped;
    private Date _created;
    private Integer _participantId;

    public Integer getRowId()
    {
        return _rowId;
    }

    public void setRowId(Integer rowId)
    {
        _rowId = rowId;
    }

    @JsonIgnore
    public Container getContainer()
    {
        return _container;
    }

    @JsonIgnore
    public void setContainer(Container container)
    {
        _container = container;
    }

    public String getListName()
    {
        return _listName;
    }

    public void setListName(String listName)
    {
        _listName = listName;
    }

    public Integer getActivityId()
    {
        return _activityId;
    }

    public void setActivityId(Integer activityId)
    {
        _activityId = activityId;
    }

    public String getFieldName()
    {
        return _fieldName;
    }

    public void setFieldName(String fieldName)
    {
        _fieldName = fieldName;
    }

    public Date getStartTime()
    {
        return _startTime;
    }

    public void setStart(Date startTime)
    {
        _startTime = startTime;
    }

    public void setStartTime(Date startTime)
    {
        _startTime = startTime;
    }

    public Date getEndTime()
    {
        return _endTime;
    }

    public void setEnd(Date endTime)
    {
        _endTime = endTime;
    }

    public void setEndTime(Date endTime)
    {
        _endTime = endTime;
    }

    public Boolean getSkipped()
    {
        return _skipped != null && _skipped;
    }

    public void setSkipped(Boolean skipped)
    {
        _skipped = skipped;
    }

    public Date getCreated()
    {
        return _created;
    }

    public void setCreated(Date created)
    {
        _created = created;
    }

    public Integer getParticipantId()
    {
        return _participantId;
    }

    public void setParticipantId(Integer participantId)
    {
        _participantId = participantId;
    }
}
