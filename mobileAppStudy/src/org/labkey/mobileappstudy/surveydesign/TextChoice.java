package org.labkey.mobileappstudy.surveydesign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextChoice
{
    private OtherOption _other = null;
    public OtherOption getOther()
    {
        return _other;
    }

    public void setOther(OtherOption other)
    {
        _other = other;
    }

    public boolean hasOtherOption()
    {
        return getOther() != null && getOther().getTextFieldReq();
    }
}
