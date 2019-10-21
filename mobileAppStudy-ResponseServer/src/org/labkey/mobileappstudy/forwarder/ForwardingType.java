package org.labkey.mobileappstudy.forwarder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.labkey.api.data.Container;
import org.labkey.api.security.User;
import org.labkey.mobileappstudy.MobileAppStudyController;
import org.springframework.validation.Errors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.labkey.api.action.SpringActionController.ERROR_MSG;
import static org.labkey.api.action.SpringActionController.ERROR_REQUIRED;
import static org.labkey.mobileappstudy.forwarder.ForwarderProperties.FORWARDING_TYPE;
import static org.labkey.mobileappstudy.forwarder.ForwarderProperties.OAUTH_URL;
import static org.labkey.mobileappstudy.forwarder.ForwarderProperties.PASSWORD_PROPERTY_NAME;
import static org.labkey.mobileappstudy.forwarder.ForwarderProperties.TOKEN_FIELD;
import static org.labkey.mobileappstudy.forwarder.ForwarderProperties.TOKEN_HEADER;
import static org.labkey.mobileappstudy.forwarder.ForwarderProperties.TOKEN_REQUEST_URL;
import static org.labkey.mobileappstudy.forwarder.ForwarderProperties.URL_PROPERTY_NAME;
import static org.labkey.mobileappstudy.forwarder.ForwarderProperties.USER_PROPERTY_NAME;

public enum ForwardingType
{
    Disabled
            {
                @Override
                public void validateConfig(MobileAppStudyController.ForwardingSettingsForm form, Errors errors)
                {
                    //Nothing to validate
                }

                @Override
                public void setForwardingProperties(Container container, MobileAppStudyController.ForwardingSettingsForm form)
                {
                    new ForwarderProperties().setForwarderDisabled(container, this);
                }

                @Override
                public Forwarder getForwarder(Container container, Logger logger)
                {
                    return null;
                }
            },
    OAuth
            {
                @Override
                public void validateConfig(MobileAppStudyController.ForwardingSettingsForm form, Errors errors)
                {
                    if (StringUtils.isBlank(form.getOauthURL()))
                        errors.rejectValue("oauthURL", ERROR_REQUIRED, "Field cannot be blank.");
                    else try
                    {
                        new URL(form.getOauthURL());
                    }
                    catch (MalformedURLException e)
                    {
                        errors.rejectValue("oauthURL", ERROR_MSG, "Forwarding endpoint URL is malformed");
                    }


                    if (StringUtils.isBlank(form.getTokenRequestURL()))
                        errors.rejectValue("tokenRequestURL", ERROR_REQUIRED, "Field cannot be blank.");
                    else try
                    {
                        new URL(form.getTokenRequestURL());
                    }
                    catch (MalformedURLException e)
                    {
                        errors.rejectValue("tokenRequestURL", ERROR_MSG, "Token request URL is malformed");
                    }

                }

                @Override
                public
                void setForwardingProperties(Container container, MobileAppStudyController.ForwardingSettingsForm form)
                {
                    Map<String, String> propertyMap = new HashMap<>();
                    propertyMap.put(TOKEN_REQUEST_URL, form.getTokenRequestURL());
                    propertyMap.put(TOKEN_FIELD, form.getTokenField());
                    propertyMap.put(TOKEN_HEADER, form.getHeader());
                    propertyMap.put(OAUTH_URL, form.getOauthURL());
                    propertyMap.put(FORWARDING_TYPE, form.getForwardingType().name());

                    new ForwarderProperties().setForwarderProperties(container, propertyMap);
                }

                @Override
                public Forwarder getForwarder(Container container, Logger logger)
                {
                    return new OAuthForwarder(container, logger);
                }
            },
    Basic
            {
                @Override
                public void validateConfig(MobileAppStudyController.ForwardingSettingsForm form, Errors errors)
                {

                    if (StringUtils.isBlank(form.getBasicURL()))
                        errors.rejectValue("basicURL", ERROR_REQUIRED, "Field cannot be blank.");
                    else try
                    {
                        new URL(form.getBasicURL());
                    }
                    catch (MalformedURLException e)
                    {
                        errors.rejectValue("basicURL", ERROR_MSG, "Malformed URL");
                    }

                    if (StringUtils.isBlank(form.getUsername()))
                        errors.rejectValue("username", ERROR_REQUIRED, "Field cannot be blank.");
                    if ( StringUtils.isBlank(form.getPassword()))
                        errors.rejectValue("password", ERROR_REQUIRED, "Field cannot be blank.");
                }


                @Override
                public void setForwardingProperties(Container container, MobileAppStudyController.ForwardingSettingsForm form)
                {
                    Map<String, String> propertyMap = new HashMap<>();
                    propertyMap.put(USER_PROPERTY_NAME, form.getUsername());
                    propertyMap.put(PASSWORD_PROPERTY_NAME, form.getPassword());
                    propertyMap.put(URL_PROPERTY_NAME, form.getBasicURL());
                    propertyMap.put(FORWARDING_TYPE, form.getForwardingType().name());

                    new ForwarderProperties().setForwarderProperties(container, propertyMap);
                }

                @Override
                public Forwarder getForwarder(Container container, Logger logger)
                {
                    return new BasicAuthForwarder(container, logger);
                }
            };

    public abstract void validateConfig(MobileAppStudyController.ForwardingSettingsForm form, Errors errors);
    public abstract void setForwardingProperties(Container container, MobileAppStudyController.ForwardingSettingsForm form);
    public abstract Forwarder getForwarder(Container container, Logger logger);
}