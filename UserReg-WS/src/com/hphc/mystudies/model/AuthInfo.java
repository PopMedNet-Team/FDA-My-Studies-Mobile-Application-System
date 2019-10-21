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
public class AuthInfo extends Entity
{
    private int _AuthId;
    private String _ParticipantId;
    private String _DeviceToken;
    private String _DeviceType;
    private Date _CreatedOn;
    private Date _ModifiedOn;
    private String _AuthKey;
    private String _IosAppVersion;
    private String _AndroidAppVersion;

    private Date _SessionExpiredDate;
    private Boolean _RemoteNotificationFlag=false;
    private String _RefreshToken;
    private String _ApplicationId;
    private String _OrgId;


    public int getAuthId()
    {
        return _AuthId;
    }

    public void setAuthId(int authId)
    {
        _AuthId = authId;
    }

    public String getParticipantId()
    {
        return _ParticipantId;
    }

    public void setParticipantId(String participantId)
    {
        _ParticipantId = participantId;
    }

    public String getDeviceToken()
    {
        return _DeviceToken;
    }

    public void setDeviceToken(String deviceToken)
    {
        _DeviceToken = deviceToken;
    }

    public String getDeviceType()
    {
        return _DeviceType;
    }

    public void setDeviceType(String deviceType)
    {
        _DeviceType = deviceType;
    }

    public Date getCreatedOn()
    {
        return _CreatedOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        _CreatedOn = createdOn;
    }

    public Date getModifiedOn()
    {
        return _ModifiedOn;
    }

    public void setModifiedOn(Date modifiedOn)
    {
        _ModifiedOn = modifiedOn;
    }

    public String getAuthKey()
    {
        return _AuthKey;
    }

    public void setAuthKey(String authKey)
    {
        _AuthKey = authKey;
    }

    public String getIosAppVersion()
    {
        return _IosAppVersion;
    }

    public void setIosAppVersion(String iosAppVersion)
    {
        _IosAppVersion = iosAppVersion;
    }

    public String getAndroidAppVersion()
    {
        return _AndroidAppVersion;
    }

    public void setAndroidAppVersion(String androidAppVersion)
    {
        _AndroidAppVersion = androidAppVersion;
    }

    public Date getSessionExpiredDate()
    {
        return _SessionExpiredDate;
    }

    public void setSessionExpiredDate(Date sessionExpiredDate)
    {
        _SessionExpiredDate = sessionExpiredDate;
    }

    public Boolean getRemoteNotificationFlag()
    {
        return _RemoteNotificationFlag;
    }

    public void setRemoteNotificationFlag(Boolean remoteNotificationFlag)
    {
        _RemoteNotificationFlag = remoteNotificationFlag;
    }

    public String getRefreshToken()
    {
        return _RefreshToken;
    }

    public void setRefreshToken(String refreshToken)
    {
        _RefreshToken = refreshToken;
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
