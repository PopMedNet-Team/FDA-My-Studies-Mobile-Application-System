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

import org.labkey.api.data.Container;

import java.util.Date;

/**
 * Representation of a database object for Participant in the mobileappstudy schema
 */
public class Participant
{
    public enum ParticipantStatus {
        Enrolled(0, "Enrolled"),    //Actively accepting responses
        Withdrawn(1, "Withdrawn");  //Electively removed from study, no longer accepting responses & existing data may have been deleted

        private final int pkId;
        private final String displayText;
        ParticipantStatus(int pkId, String displayText)
        {
            this.pkId = pkId;
            this.displayText = displayText;
        }

        public String getDisplayText()
        {
            return displayText;
        }
        public final int getPkId()
        {
            return pkId;
        }
    }

    private Integer _rowId;
    private String _appToken;
    private Integer _studyId;
    private Container _container;
    private Date _created;
    private ParticipantStatus _status;

    public String getAppToken()
    {
        return _appToken;
    }

    public void setAppToken(String appToken)
    {
        _appToken = appToken;
    }

    public Integer getStudyId()
    {
        return _studyId;
    }

    public void setStudyId(Integer studyId)
    {
        _studyId = studyId;
    }

    public Container getContainer()
    {
        return _container;
    }

    public void setContainer(Container container)
    {
        _container = container;
    }

    public Date getCreated()
    {
        return _created;
    }

    public void setCreated(Date created)
    {
        _created = created;
    }

    public Integer getRowId()
    {
        return _rowId;
    }

    public void setRowId(Integer rowId)
    {
        _rowId = rowId;
    }

    public ParticipantStatus getStatus()
    {
        return _status;
    }
    public void setStatus(ParticipantStatus status)
    {
        _status = status;
    }
}
