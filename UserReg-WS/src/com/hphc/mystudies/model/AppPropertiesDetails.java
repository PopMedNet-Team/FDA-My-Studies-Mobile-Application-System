/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.hphc.mystudies.model;

import org.labkey.api.data.Entity;

import java.util.Date;

/**
 * Created by Ronalin on 5/20/2019.
 */
public class AppPropertiesDetails extends Entity
{
    private int _Id;
    private String _AppId;
    private String _OrgId;
    private String _IosBundleId;
    private String _AndroidBundleId;
    private String _IosCertificate;
    private String _IosCertificatePassword;
    private String _AndroidServerKey;
    private String _Email;
    private String _EmailPassword;
    private String _RegEmailSub;
    private String _RegEmailBody;
    private String _ForgotEmailSub;
    private String _ForgotEmailBody;
    private boolean _MethodHandler;
    private Date _CreatedOn;

    public boolean isMethodHandler()
    {
        return _MethodHandler;
    }

    public void setMethodHandler(boolean methodHandler)
    {
        _MethodHandler = methodHandler;
    }

    public String getEmail()
    {
        return _Email;
    }

    public void setEmail(String email)
    {
        _Email = email;
    }

    public String getEmailPassword()
    {
        return _EmailPassword;
    }

    public void setEmailPassword(String emailPassword)
    {
        _EmailPassword = emailPassword;
    }

    public int getId()
    {
        return _Id;
    }

    public void setId(int id)
    {
        _Id = id;
    }

    public String getAppId()
    {
        return _AppId;
    }

    public void setAppId(String appId)
    {
        _AppId = appId;
    }

    public Date getCreatedOn()
    {
        return _CreatedOn;
    }

    public void setCreatedOn(Date createdOn)
    {
        _CreatedOn = createdOn;
    }

    public String getOrgId()
    {
        return _OrgId;
    }

    public void setOrgId(String orgId)
    {
        _OrgId = orgId;
    }

    public String getIosBundleId()
    {
        return _IosBundleId;
    }

    public void setIosBundleId(String iosBundleId)
    {
        _IosBundleId = iosBundleId;
    }

    public String getAndroidBundleId()
    {
        return _AndroidBundleId;
    }

    public void setAndroidBundleId(String androidBundleId)
    {
        _AndroidBundleId = androidBundleId;
    }

    public String getIosCertificate()
    {
        return _IosCertificate;
    }

    public void setIosCertificate(String iosCertificate)
    {
        _IosCertificate = iosCertificate;
    }

    public String getIosCertificatePassword()
    {
        return _IosCertificatePassword;
    }

    public void setIosCertificatePassword(String iosCertificatePassword)
    {
        _IosCertificatePassword = iosCertificatePassword;
    }

    public String getAndroidServerKey()
    {
        return _AndroidServerKey;
    }

    public void setAndroidServerKey(String androidServerKey)
    {
        _AndroidServerKey = androidServerKey;
    }

    public String getRegEmailBody()
    {
        return _RegEmailBody;
    }

    public void setRegEmailBody(String regEmailBody)
    {
        _RegEmailBody = regEmailBody;
    }

    public String getForgotEmailBody()
    {
        return _ForgotEmailBody;
    }

    public void setForgotEmailBody(String forgotEmailBody)
    {
        _ForgotEmailBody = forgotEmailBody;
    }

    public String getRegEmailSub()
    {
        return _RegEmailSub;
    }

    public void setRegEmailSub(String regEmailSub)
    {
        _RegEmailSub = regEmailSub;
    }

    public String getForgotEmailSub()
    {
        return _ForgotEmailSub;
    }

    public void setForgotEmailSub(String forgotEmailSub)
    {
        _ForgotEmailSub = forgotEmailSub;
    }
}
