/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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
