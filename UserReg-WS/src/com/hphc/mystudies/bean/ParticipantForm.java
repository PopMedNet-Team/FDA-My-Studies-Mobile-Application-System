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
package com.hphc.mystudies.bean;

import org.labkey.api.action.ReturnUrlForm;

import java.util.Date;

/**
 * Created by Ravinder on 2/2/2017.
 */
public class ParticipantForm extends ReturnUrlForm
{
    private String _userId;
    private String _firstName;
    private String _lastName;
    private String _emailId;
    private String _password;
    private Boolean _usePassCode;
    private Boolean _touchId;
    private Boolean _localNotification;
    private Boolean _remoteNotification;
    private Boolean _reminderFlag;
    private String _auth;
    private Integer _status;
    private Boolean _tempPassword;
    private Date _tempPasswordDate;


    public String getUserId()
    {
        return _userId;
    }

    public void setUserId(String userId)
    {
        _userId = userId;
    }

    public String getFirstName()
    {
        return _firstName;
    }

    public void setFirstName(String firstName)
    {
        _firstName = firstName;
    }

    public String getLastName()
    {
        return _lastName;
    }

    public void setLastName(String lastName)
    {
        _lastName = lastName;
    }

    public String getEmailId()
    {
        return _emailId;
    }

    public void setEmailId(String emailId)
    {
        _emailId = emailId;
    }

    public String getPassword()
    {
        return _password;
    }

    public void setPassword(String password)
    {
        _password = password;
    }

    public Boolean getUsePassCode()
    {
        return _usePassCode;
    }

    public void setUsePassCode(Boolean usePassCode)
    {
        _usePassCode = usePassCode;
    }

    public Boolean getTouchId()
    {
        return _touchId;
    }

    public void setTouchId(Boolean touchId)
    {
        _touchId = touchId;
    }

    public Boolean getLocalNotification()
    {
        return _localNotification;
    }

    public void setLocalNotification(Boolean localNotification)
    {
        _localNotification = localNotification;
    }

    public Boolean getRemoteNotification()
    {
        return _remoteNotification;
    }

    public void setRemoteNotification(Boolean remoteNotification)
    {
        _remoteNotification = remoteNotification;
    }


    public Boolean getReminderFlag()
    {
        return _reminderFlag;
    }

    public void setReminderFlag(Boolean reminderFlag)
    {
        _reminderFlag = reminderFlag;
    }

    public String getAuth()
    {
        return _auth;
    }

    public void setAuth(String auth)
    {
        _auth = auth;
    }

    public Integer getStatus()
    {
        return _status;
    }

    public void setStatus(Integer status)
    {
        _status = status;
    }

    public Boolean getTempPassword()
    {
        return _tempPassword;
    }

    public void setTempPassword(Boolean tempPassword)
    {
        _tempPassword = tempPassword;
    }

    public Date getTempPasswordDate()
    {
        return _tempPasswordDate;
    }

    public void setTempPasswordDate(Date tempPasswordDate)
    {
        _tempPasswordDate = tempPasswordDate;
    }
}
