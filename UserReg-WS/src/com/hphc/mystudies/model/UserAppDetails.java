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
 * Created by Ronalin on 4/11/2019.
 */
public class UserAppDetails extends Entity
{
    private int _UserAppId;
    private String _UserId;
    private String _ApplicationId;
    private String _OrgId;
    private Date _CreatedOn;

    public Integer getUserAppId()
    {
        return _UserAppId;
    }

    public void setUserAppId(Integer id)
    {
        _UserAppId = id;
    }

    public String getUserId()
    {
        return _UserId;
    }

    public void setUserId(String userId)
    {
        _UserId = userId;
    }

    public Date getCreatedOn()
    {
        return _CreatedOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        _CreatedOn = createdOn;
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
