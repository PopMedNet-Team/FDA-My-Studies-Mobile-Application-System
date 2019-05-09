/*
 * Copyright (c) 2017-2019 LabKey Corporation
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
package org.labkey.mobileappstudy.surveydesign;

import org.apache.log4j.Logger;
import org.labkey.api.data.Container;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.mobileappstudy.MobileAppStudyModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Get MobileAppStudy SurveySchema from a resource file
 */
public class FileSurveyDesignProvider extends AbstractSurveyDesignProviderImpl
{
    public FileSurveyDesignProvider(Container container, Logger logger)
    {
        super(container, logger);
    }

    @Override
    public SurveyDesign getSurveyDesign(Container c, String studyId, String activityId, String version) throws InvalidDesignException
    {
        try
        {
            //TODO: make this more flexible
            StringBuilder sb = new StringBuilder();
            Path filePath = Paths.get(getBasePath(c), String.join("_", studyId, activityId, version) + ".json");
            Files.readAllLines(filePath).forEach(sb::append);

            return getSurveyDesign(sb.toString());
        }
        catch (IOException x)
        {
            throw new InvalidDesignException("Unable to read from SurveyDesign file", x);
        }
    }

    public static String getBasePath(Container c)
    {
        Module module = ModuleLoader.getInstance().getModule(MobileAppStudyModule.NAME);
        return module.getModuleProperties().get(MobileAppStudyModule.SURVEY_METADATA_DIRECTORY).getEffectiveValue(c);
    }
}
