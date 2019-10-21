/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.model;

import org.labkey.api.data.Entity;

/**
 * Created by Ravinder on 1/31/2017.
 */
public class ParticipantActivities extends Entity
{
    private Integer _Id;
    private String _ParticipantId;
    private String _StudyId;
    private String _ActivityId;
    private Integer _ActivityCompleteId;
    private String _ActivityType;
    private Boolean _Bookmark=false;
    private String _Status;
    private String _ActivityVersion;
    private String _ActivityState;
    private String _ActivityRunId;

    private Integer _Total;
    private Integer _Completed;
    private Integer _Missed;
    private String _ApplicationId;
    private String _OrgId;

    public Integer getId()
    {
        return _Id;
    }

    public void setId(Integer id)
    {
        _Id = id;
    }

    public String getParticipantId()
    {
        return _ParticipantId;
    }

    public void setParticipantId(String participantId)
    {
        _ParticipantId = participantId;
    }

    public String getActivityId()
    {
        return _ActivityId;
    }

    public void setActivityId(String activityId)
    {
        _ActivityId = activityId;
    }

    public Integer getActivityCompleteId()
    {
        return _ActivityCompleteId;
    }

    public void setActivityCompleteId(Integer activityCompleteId)
    {
        _ActivityCompleteId = activityCompleteId;
    }

    public String getActivityState()
    {
        return _ActivityState;
    }

    public void setActivityState(String activityState)
    {
        _ActivityState = activityState;
    }

    public String getActivityType()
    {
        return _ActivityType;
    }

    public void setActivityType(String activityType)
    {
        _ActivityType = activityType;
    }

    public Boolean getBookmark()
    {
        return _Bookmark;
    }

    public void setBookmark(Boolean bookmark)
    {
        _Bookmark = bookmark;
    }

    public String getStatus()
    {
        return _Status;
    }

    public void setStatus(String status)
    {
        _Status = status;
    }

    public String getActivityVersion()
    {
        return _ActivityVersion;
    }

    public void setActivityVersion(String activityVersion)
    {
        _ActivityVersion = activityVersion;
    }

    public String getStudyId()
    {
        return _StudyId;
    }

    public void setStudyId(String studyId)
    {
        _StudyId = studyId;
    }

    public String getActivityRunId()
    {
        return _ActivityRunId;
    }

    public void setActivityRunId(String activityRunId)
    {
        _ActivityRunId = activityRunId;
    }

    public Integer getTotal()
    {
        return _Total;
    }

    public void setTotal(Integer total)
    {
        _Total = total;
    }

    public Integer getCompleted()
    {
        return _Completed;
    }

    public void setCompleted(Integer completed)
    {
        _Completed = completed;
    }

    public Integer getMissed()
    {
        return _Missed;
    }

    public void setMissed(Integer missed)
    {
        _Missed = missed;
    }

    public String getApplicationId()
    {
        return _ApplicationId;
    }

    public void setApplicationId(String applicationId)
    {
        _ApplicationId = applicationId;
    }

    public String getOrgId()
    {
        return _OrgId;
    }

    public void setOrgId(String orgId)
    {
        _OrgId = orgId;
    }
}
