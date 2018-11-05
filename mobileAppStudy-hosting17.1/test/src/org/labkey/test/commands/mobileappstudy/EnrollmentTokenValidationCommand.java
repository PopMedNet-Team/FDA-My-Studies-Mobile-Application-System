/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.commands.mobileappstudy;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.labkey.test.WebTestHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class EnrollmentTokenValidationCommand extends MobileAppCommand
{
    protected static final String CONTROLLER_NAME = "mobileappstudy";
    protected static final String ACTION_NAME = "validateenrollmenttoken";
    public static final String INVLAID_STUDYID_FORMAT = "Study with StudyId '%1$s' does not exist";
    public static final String INVALID_TOKEN_FORMAT = "Invalid token: '%1$s'";
    public static final String BLANK_STUDYID = "StudyId is required";
    public static final String TOKEN_REQUIRED = "Token is required";

    private String _batchToken;
    private String _studyName;
    private String _projectName;

    public String getProjectName()
    {
        return _projectName;
    }
    public void setProjectName(String projectName)
    {
        _projectName = projectName;
    }

    public EnrollmentTokenValidationCommand(String project, String studyName, String batchToken, Consumer<String> logger)
    {
        _studyName = studyName;
        _batchToken = batchToken;
        _projectName = project;

        setLogger(logger);
    }

    public String getStudyName()
    {
        return _studyName;
    }
    public void setStudyName(String studyName)
    {
        _studyName = studyName;
    }

    public String getBatchToken()
    {
        return _batchToken;
    }
    public void setBatchToken(String batchToken)
    {
        _batchToken = batchToken;
    }

    @Override
    public HttpResponse execute(int expectedStatusCode)
    {
        HttpPost post = new HttpPost(getTargetURL());
        return execute(post, expectedStatusCode);
    }

    @Override
    public String getTargetURL()
    {
        Map<String, String> params = new HashMap<>();
        params.put("studyId", getStudyName());
        if (StringUtils.isNotBlank(getBatchToken()))
            params.put("token", getBatchToken());
        return WebTestHelper.buildURL(CONTROLLER_NAME, getProjectName(), ACTION_NAME, params);
    }

    @Override
    public String getBody()
    {
        return "";
    }
}
