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
public class UserDetails extends Entity
{
    private Integer _Id;
    private String _FirstName;
    private String _LastName;
    private String _Email;
    private Boolean _UsePassCode=false;
    private Boolean _TouchId=false;
    private Boolean _LocalNotificationFlag=false;
    private Boolean _RemoteNotificationFlag=false;
    private Integer Status;
    private String _Password;
    private String _ReminderLeadTime;
    private String _SecurityToken;
    private String _UserId;
    private Boolean _TempPassword=false;
    private String _Locale;
    private String _ResetPassword;
    private Date _VerificationDate;
    private Date _TempPasswordDate;
    private Date _PasswordUpdatedDate;
    private String _ApplicationId;
    private String _OrgId;

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

    public Integer getId()
    {
        return _Id;
    }

    public void setId(Integer id)
    {
        _Id = id;
    }

    public String getFirstName()
    {
        return _FirstName;
    }

    public void setFirstName(String firstName)
    {
        _FirstName = firstName;
    }

    public String getLastName()
    {
        return _LastName;
    }

    public void setLastName(String lastName)
    {
        _LastName = lastName;
    }

    public String getEmail()
    {
        return _Email;
    }

    public void setEmail(String email)
    {
        _Email = email;
    }

    public Boolean getUsePassCode()
    {
        return _UsePassCode;
    }

    public void setUsePassCode(Boolean usePassCode)
    {
        _UsePassCode = usePassCode;
    }

    public Boolean getTouchId()
    {
        return _TouchId;
    }

    public void setTouchId(Boolean touchId)
    {
        _TouchId = touchId;
    }

    public Boolean getLocalNotificationFlag()
    {
        return _LocalNotificationFlag;
    }

    public void setLocalNotificationFlag(Boolean localNotificationFlag)
    {
        _LocalNotificationFlag = localNotificationFlag;
    }

    public Boolean getRemoteNotificationFlag()
    {
        return _RemoteNotificationFlag;
    }

    public void setRemoteNotificationFlag(Boolean remoteNotificationFlag)
    {
        _RemoteNotificationFlag = remoteNotificationFlag;
    }

    public Integer getStatus()
    {
        return Status;
    }

    public void setStatus(Integer status)
    {
        Status = status;
    }

    public String getPassword()
    {
        return _Password;
    }

    public void setPassword(String password)
    {
        _Password = password;
    }

    public String getReminderLeadTime()
    {
        return _ReminderLeadTime;
    }

    public void setReminderLeadTime(String reminderLeadTime)
    {
        _ReminderLeadTime = reminderLeadTime;
    }

    public String getSecurityToken()
    {
        return _SecurityToken;
    }

    public void setSecurityToken(String securityToken)
    {
        _SecurityToken = securityToken;
    }

    public String getUserId()
    {
        return _UserId;
    }

    public void setUserId(String userId)
    {
        _UserId = userId;
    }

    public Boolean getTempPassword()
    {
        return _TempPassword;
    }

    public void setTempPassword(Boolean tempPassword)
    {
        _TempPassword = tempPassword;
    }

    public String getLocale()
    {
        return _Locale;
    }

    public void setLocale(String locale)
    {
        _Locale = locale;
    }

    public Date getVerificationDate()
    {
        return _VerificationDate;
    }

    public void setVerificationDate(Date verificationDate)
    {
        _VerificationDate = verificationDate;
    }

    public Date getTempPasswordDate()
    {
        return _TempPasswordDate;
    }

    public void setTempPasswordDate(Date tempPasswordDate)
    {
        _TempPasswordDate = tempPasswordDate;
    }

    public String getResetPassword()
    {
        return _ResetPassword;
    }

    public void setResetPassword(String resetPassword)
    {
        _ResetPassword = resetPassword;
    }

    public Date getPasswordUpdatedDate()
    {
        return _PasswordUpdatedDate;
    }

    public void setPasswordUpdatedDate(Date passwordUpdatedDate)
    {
        _PasswordUpdatedDate = passwordUpdatedDate;
    }
}
