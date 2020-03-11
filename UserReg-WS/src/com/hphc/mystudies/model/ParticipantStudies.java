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

import java.util.Date;

/**
 * Created by Ravinder on 1/31/2017.
 */
public class ParticipantStudies extends Entity
{
    private Integer _Id;
    private String _StudyId;
    private String _Status;
    private Boolean _Bookmark=false;
    private Boolean _Eligbibility=false;
    private Boolean _ConsentStatus=false;
    private Date _EnrolledDate;
    private String _ParticipantId;
    private String _UserId;
    private String _Sharing;
    private Integer _Completion;
    private Integer _Adherence;
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

    public String getStudyId()
    {
        return _StudyId;
    }

    public void setStudyId(String studyId)
    {
        _StudyId = studyId;
    }

    public String getStatus()
    {
        return _Status;
    }

    public void setStatus(String status)
    {
        _Status = status;
    }

    public Boolean getBookmark()
    {
        return _Bookmark;
    }

    public void setBookmark(Boolean bookmark)
    {
        _Bookmark = bookmark;
    }

    public Boolean getEligbibility()
    {
        return _Eligbibility;
    }

    public void setEligbibility(Boolean eligbibility)
    {
        _Eligbibility = eligbibility;
    }

    public Boolean getConsentStatus()
    {
        return _ConsentStatus;
    }

    public void setConsentStatus(Boolean consentStatus)
    {
        _ConsentStatus = consentStatus;
    }

    public Date getEnrolledDate()
    {
        return _EnrolledDate;
    }

    public void setEnrolledDate(Date enrolledDate)
    {
        _EnrolledDate = enrolledDate;
    }

    public String getParticipantId()
    {
        return _ParticipantId;
    }

    public void setParticipantId(String participantId)
    {
        _ParticipantId = participantId;
    }

    public String getUserId()
    {
        return _UserId;
    }

    public void setUserId(String userId)
    {
        _UserId = userId;
    }

    public String getSharing()
    {
        return _Sharing;
    }

    public void setSharing(String sharing)
    {
        _Sharing = sharing;
    }

    public Integer getCompletion()
    {
        return _Completion;
    }

    public void setCompletion(Integer completion)
    {
        _Completion = completion;
    }

    public Integer getAdherence()
    {
        return _Adherence;
    }

    public void setAdherence(Integer adherence)
    {
        _Adherence = adherence;
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
