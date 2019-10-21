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
package org.labkey.mobileappstudy.forwarder;

import org.apache.commons.lang3.StringUtils;
import org.labkey.api.data.Container;
import org.labkey.api.data.PropertyManager;

import java.util.Map;
import java.util.Set;

public class ForwarderProperties
{
    private static final String FORWARDER_CATEGORY = "MobileAppForwarder";
    public static final String PASSWORD_PLACEHOLDER = "***REDACTED***";
    public static final String URL_PROPERTY_NAME = "URL";
    public static final String USER_PROPERTY_NAME = "USER";
    public static final String PASSWORD_PROPERTY_NAME = "PASSWORD";
    public static final String ENABLED_PROPERTY_NAME = "ENABLED";
    public static final String FORWARDING_TYPE = "FORWARDING_TYPE";
    public static final String TOKEN_REQUEST_URL = "TOKEN_REQUEST_URL";
    public static final String TOKEN_FIELD = "TOKEN_FIELD";
    public static final String TOKEN_HEADER = "TOKEN_HEADER";
    public static final String OAUTH_URL = "OAUTH_URL";

    public static final Set<String> PROPERTIES = Set.of(
        PASSWORD_PLACEHOLDER,
        URL_PROPERTY_NAME,
        USER_PROPERTY_NAME,
        PASSWORD_PROPERTY_NAME,
        ENABLED_PROPERTY_NAME,
        FORWARDING_TYPE,
        TOKEN_REQUEST_URL,
        TOKEN_FIELD,
        TOKEN_HEADER,
        OAUTH_URL);

    /**
     * Set the connection properties for the forwarding endpoint
     * @param container containing study
     * @param newConfig Set of properties to save
     */
    public void setForwarderProperties(Container container, Map<String, String> newConfig)
    {
        PropertyManager.PropertyMap propertyMap = PropertyManager.getEncryptedStore().getWritableProperties(container, FORWARDER_CATEGORY ,true);
        newConfig.keySet().forEach((key) -> {
            if (PROPERTIES.contains(key))
            {
                String propValue = newConfig.get(key);

                //If property key is basic auth password, and the value is blank or our placeholder, then skip
                if (PASSWORD_PROPERTY_NAME.equals(key) && (StringUtils.isBlank(propValue) || propValue.equals(PASSWORD_PLACEHOLDER)))
                    return;

                propertyMap.put(key, propValue);
            }
        });

        propertyMap.save();
    }

    public void setForwarderDisabled(Container container, ForwardingType authType)
    {
        PropertyManager.PropertyMap propertyMap = PropertyManager.getEncryptedStore().getWritableProperties(container, FORWARDER_CATEGORY ,true);
        propertyMap.put(FORWARDING_TYPE, authType.name());
        propertyMap.save();
    }

    /**
     * Get a forwarder connection properties
     * @param container container to get properties for
     * @return Map of strings of connection properties
     */
    public Map<String, String> getForwarderConnection(Container container)
    {
        return PropertyManager.getEncryptedStore().getProperties(container, FORWARDER_CATEGORY);
    }

    /**
     * Check is forwarding enabled for the study container
     * @param container to check
     * @return True if forwarding is enabled
     */
    public static ForwardingType getForwardingType(Container container)
    {
        String authType = PropertyManager.getEncryptedStore().getProperties(container, FORWARDER_CATEGORY).getOrDefault(FORWARDING_TYPE, ForwardingType.Disabled.name());
        return ForwardingType.valueOf(authType);
    }

}
