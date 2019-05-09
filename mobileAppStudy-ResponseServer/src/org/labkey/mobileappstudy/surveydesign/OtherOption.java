package org.labkey.mobileappstudy.surveydesign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OtherOption
{
    private boolean _textfieldReq = false;
    public boolean getTextFieldReq()
    {
        return _textfieldReq;
    }
    public void setTextfieldReq(Boolean textfieldReq)
    {
        _textfieldReq = textfieldReq;
    }
}
