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

package org.labkey.mobileappstudy;

import org.jetbrains.annotations.NotNull;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.module.DefaultModule;
import org.labkey.api.module.ModuleContext;
import org.labkey.api.module.ModuleProperty;
import org.labkey.api.view.FolderManagement;
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
import java.util.function.Predicate;

public class MobileAppStudyModule extends DefaultModule
{
    public static final String NAME = "MobileAppStudy";
    public static final String SURVEY_METADATA_DIRECTORY = "SurveyMetadataDirectory";
    public static final String METADATA_SERVICE_BASE_URL = "MetadataServiceBaseUrl";
    public static final String METADATA_SERVICE_ACCESS_TOKEN = "MetadataServiceAccessToken";

    /**
     * Predicate that can be used to check if a container has this module active
     */
    public final Predicate<Container> IS_ACTIVE = container -> container.hasActiveModuleByName(getName());

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public double getVersion()
    {
        return 19.20;
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
        designDropDirectory.setInputFieldWidth(500);
        this.addModuleProperty(designDropDirectory);

        ModuleProperty metadataServiceBaseURL = new ModuleProperty(this, METADATA_SERVICE_BASE_URL);
        metadataServiceBaseURL.setCanSetPerContainer(true);
        metadataServiceBaseURL.setDescription("Base URL for the Activity Metadata Service");
        metadataServiceBaseURL.setInputFieldWidth(500);
        this.addModuleProperty(metadataServiceBaseURL);

        ModuleProperty metadataServiceAccessToken = new ModuleProperty(this, METADATA_SERVICE_ACCESS_TOKEN);
        metadataServiceAccessToken.setCanSetPerContainer(true);
        metadataServiceAccessToken.setDescription("Token to be passed in the header of requests to the Activity Metadata Service to identify this client of that service.");
        metadataServiceAccessToken.setInputFieldWidth(500);
        this.addModuleProperty(metadataServiceAccessToken);

        FolderManagement.addTab(FolderManagement.TYPE.FolderManagement, "Response Forwarding", "forwarding",
                IS_ACTIVE, MobileAppStudyController.ForwardingSettingsAction.class);

        //Startup shredding and forwarder jobs
        MobileAppStudyManager.get().doStartup();
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