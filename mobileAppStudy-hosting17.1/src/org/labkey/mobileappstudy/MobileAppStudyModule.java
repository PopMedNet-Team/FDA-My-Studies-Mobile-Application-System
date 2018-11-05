/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */

package org.labkey.mobileappstudy;

import org.jetbrains.annotations.NotNull;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.module.DefaultModule;
import org.labkey.api.module.ModuleContext;
import org.labkey.api.module.ModuleProperty;
import org.labkey.api.view.SimpleWebPartFactory;
import org.labkey.api.view.WebPartFactory;
import org.labkey.mobileappstudy.query.MobileAppStudyQuerySchema;
import org.labkey.mobileappstudy.query.ReadResponsesQuerySchema;
import org.labkey.mobileappstudy.view.EnrollmentTokenBatchesWebPart;
import org.labkey.mobileappstudy.view.StudyConfigWebPart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class MobileAppStudyModule extends DefaultModule
{
    public static final String NAME = "MobileAppStudy";
    public static final String SURVEY_METADATA_DIRECTORY = "SurveyMetadataDirectory";
    public static final String METADATA_SERVICE_BASE_URL = "MetadataServiceBaseUrl";
    public static final String METADATA_SERVICE_ACCESS_TOKEN = "MetadataServiceAccessToken";

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public double getVersion()
    {
        return 17.10;
    }

    @Override
    public boolean hasScripts()
    {
        return true;
    }

    @Override
    @NotNull
    protected Collection<WebPartFactory> createWebPartFactories()
    {
        ArrayList<WebPartFactory> list = new ArrayList<>();
        list.add(new SimpleWebPartFactory("Enrollment Token Batches", WebPartFactory.LOCATION_BODY, EnrollmentTokenBatchesWebPart.class, null));
        list.add(new SimpleWebPartFactory("Mobile App Study Setup", WebPartFactory.LOCATION_BODY, StudyConfigWebPart.class, null));
        return list;
    }

    @Override
    protected void init()
    {
        addController(MobileAppStudyController.NAME, MobileAppStudyController.class);
    }

    @Override
    public void doStartup(ModuleContext moduleContext)
    {
        ContainerManager.addContainerListener(new MobileAppStudyContainerListener());
        MobileAppStudyQuerySchema.register(this);
        ReadResponsesQuerySchema.register(this);

        ModuleProperty designDropDirectory = new ModuleProperty(this, SURVEY_METADATA_DIRECTORY);
        designDropDirectory.setCanSetPerContainer(true);
        designDropDirectory.setDescription("Directory in which to find survey design metadata files (for use when metadata service is not available).");
        this.addModuleProperty(designDropDirectory);

        ModuleProperty metadataServiceBaseURL = new ModuleProperty(this, METADATA_SERVICE_BASE_URL);
        metadataServiceBaseURL.setCanSetPerContainer(true);
        metadataServiceBaseURL.setDescription("Base URL for the Activity Metadata Service");
        this.addModuleProperty(metadataServiceBaseURL);

        ModuleProperty metadataServiceAccessToken = new ModuleProperty(this, METADATA_SERVICE_ACCESS_TOKEN);
        metadataServiceAccessToken.setCanSetPerContainer(true);
        metadataServiceAccessToken.setDescription("Token to be passed in the header of requests to the Activity Metadata Service to identify this client of that service.");
        this.addModuleProperty(metadataServiceAccessToken);
    }

    @Override
    @NotNull
    public Collection<String> getSummary(Container c)
    {
        return Collections.emptyList();
    }

    @Override
    @NotNull
    public Set<String> getSchemaNames()
    {
        return Collections.singleton(MobileAppStudySchema.NAME);
    }
}