/*
 * Copyright (c) 2019 LabKey Corporation
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
