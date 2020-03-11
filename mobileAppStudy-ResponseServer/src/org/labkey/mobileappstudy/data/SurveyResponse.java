/*
 * Copyright (c) 2016-2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.mobileappstudy.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.labkey.api.data.Container;
import org.labkey.api.security.User;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyResponse
{
    public enum ResponseStatus {

        /** list order doesn't matter, but don't change id's unless you also update mobileappstudy.response.status **/
        PENDING(0, "Pending"),
        PROCESSED(1, "Processed"),
        ERROR(2, "Error"),
        FORWARDED(3, "Forwarded");

        private final int pkId;
        private final String displayText;

        ResponseStatus(int pkId, String displayText)
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
    private String _data;
    private Integer _participantId;
    private String _surveyVersion;
    private String _activityId;
    private ResponseStatus _status;
    private Date _processed;
    private User _processedBy;
    private String _errorMessage;
    private Date _created;
    private Container _container;
    private String _appToken;

    public SurveyResponse()
    {
    }

    public SurveyResponse(String participantId, String data, String activityId, String version)
    {
        setStatus(SurveyResponse.ResponseStatus.PENDING);
        setAppToken(participantId);
        setData(data);
        setSurveyVersion(version);
        setActivityId(activityId);
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

    public String getErrorMessage()
    {
        return _errorMessage;
    }
    public void setErrorMessage(String errorMessage)
    {
        _errorMessage = errorMessage;
    }

    public User getProcessedBy()
    {
        return _processedBy;
    }
    public void setProcessedBy(User processedBy)
    {
        _processedBy = processedBy;
    }

    public Date getProcessed()
    {
        return _processed;
    }
    public void setProcessed(Date processed)
    {
        _processed = processed;
    }

    /**
     * Use when looking for an explicit state
     * @return Current processing status of response
     */
    public ResponseStatus getStatus()
    {
        return _status;
    }
    public void setStatus(ResponseStatus status)
    {
        _status = status;
    }

    /**
     * Use when checking if response has been shredded yet
     * @return True if the response status is PROCESSED or FORWARDED, false otherwise.
     */
    public boolean isResponseProcessed()
    {
        return _status == ResponseStatus.FORWARDED || _status == ResponseStatus.PROCESSED;
    }

    public String getActivityId()
    {
        return _activityId;
    }
    public void setActivityId(String activityId)
    {
        _activityId = activityId;
    }

    public String getSurveyVersion()
    {
        return _surveyVersion;
    }
    public void setSurveyVersion(String surveyVersion)
    {
        _surveyVersion = surveyVersion;
    }

    public Integer getParticipantId()
    {
        return _participantId;
    }
    public void setParticipantId(Integer participantId)
    {
        _participantId = participantId;
    }

    public String getData()
    {
        return _data;
    }
    public void setData(String data)
    {
        _data = data;
    }

    public Integer getRowId()
    {
        return _rowId;
    }
    public void setRowId(Integer rowId)
    {
        _rowId = rowId;
    }

    public String getAppToken()
    {
        return _appToken;
    }
    public void setAppToken(String appToken)
    {
        _appToken = appToken;
    }

}
