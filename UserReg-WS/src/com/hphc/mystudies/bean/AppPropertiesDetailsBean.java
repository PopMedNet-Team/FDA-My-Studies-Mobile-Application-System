package com.hphc.mystudies.bean;

public class AppPropertiesDetailsBean
{
    private String _AppId;
    private String _AppType;
    private String _OrgId;
    private String _IosBundleId;
    private String _AndroidBundleId;
    private String _IosCertificate;
    private String _IosCertificatePassword;
    private String _Email;
    private String _EmailPassword;
    private String _AndroidServerKey;
    private String _RegisterEmailSubject;
    private String _RegisterEmailBody;
    private String _ForgotPassEmailSubject;
    private String _ForgotPassEmailBody;
    private boolean _MethodHandler;

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

    public String getAppId()
    {
        return _AppId;
    }

    public void setAppId(String appId)
    {
        _AppId = appId;
    }

    public String getAppType()
    {
        return _AppType;
    }

    public void setAppType(String appType)
    {
        _AppType = appType;
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

    public String getRegisterEmailSubject()
    {
        return _RegisterEmailSubject;
    }

    public void setRegisterEmailSubject(String registerEmailSubject)
    {
        _RegisterEmailSubject = registerEmailSubject;
    }

    public String getRegisterEmailBody()
    {
        return _RegisterEmailBody;
    }

    public void setRegisterEmailBody(String registerEmailBody)
    {
        _RegisterEmailBody = registerEmailBody;
    }

    public String getForgotPassEmailSubject()
    {
        return _ForgotPassEmailSubject;
    }

    public void setForgotPassEmailSubject(String forgotPassEmailSubject)
    {
        _ForgotPassEmailSubject = forgotPassEmailSubject;
    }

    public String getForgotPassEmailBody()
    {
        return _ForgotPassEmailBody;
    }

    public void setForgotPassEmailBody(String forgotPassEmailBody)
    {
        _ForgotPassEmailBody = forgotPassEmailBody;
    }
}