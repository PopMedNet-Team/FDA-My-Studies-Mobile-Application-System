package org.labkey.mobileappstudy.data;

import java.util.Map;

public class TextChoiceResult
{
    private Object value;
    public Object getValue()
    {
        return value;
    }
    private String otherText = null;
    public String getOtherText()
    {
        return otherText;
    }

    public TextChoiceResult(Object val)
    {
        if (val instanceof Map)
        {
            Map<String, Object> valMap = (Map<String, Object>) val;
            value = valMap.getOrDefault("other", "");
            otherText = valMap.getOrDefault("text", "").toString();
        }
        else
            value = val;
    }
}
