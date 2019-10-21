/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.hphc.mystudies.bean;

import org.json.JSONArray;

/**
 * Created by Ravinder on 5/3/2017.
 */
public class NotificationBean
{
    private String _studyId;
    private String _customStudyId;
    private String _notificationText;
    private String _notificationTitle;
    private String _notificationType;
    private String _notificationSubType;
    private Integer _notificationId;
    private String _deviceType;

    private JSONArray _deviceToken;
    private String _appId;

    public String getStudyId()
    {
        return _studyId;
    }

    public void setStudyId(String studyId)
    {
        _studyId = studyId;
    }

    public String getCustomStudyId()
    {
        return _customStudyId;
    }

    public void setCustomStudyId(String customStudyId)
    {
        _customStudyId = customStudyId;
    }

    public String getNotificationText()
    {
        return _notificationText;
    }

    public void setNotificationText(String notificationText)
    {
        _notificationText = notificationText;
    }

    public String getNotificationTitle()
    {
        return _notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle)
    {
        _notificationTitle = notificationTitle;
    }

    public String getNotificationType()
    {
        return _notificationType;
    }

    public void setNotificationType(String notificationType)
    {
        _notificationType = notificationType;
    }

    public String getNotificationSubType()
    {
        return _notificationSubType;
    }

    public void setNotificationSubType(String notificationSubType)
    {
        _notificationSubType = notificationSubType;
    }

    public JSONArray getDeviceToken()
    {
        return _deviceToken;
    }

    public void setDeviceToken(JSONArray deviceToken)
    {
        _deviceToken = deviceToken;
    }

    public Integer getNotificationId()
    {
        return _notificationId;
    }

    public void setNotificationId(Integer notificationId)
    {
        _notificationId = notificationId;
    }

    public String getDeviceType()
    {
        return _deviceType;
    }

    public void setDeviceType(String deviceType)
    {
        _deviceType = deviceType;
    }

    public String getAppId()
    {
        return _appId;
    }

    public void setAppId(String appId)
    {
        _appId = appId;
    }

}
