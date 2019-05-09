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
package org.labkey.mobileappstudy.view;

import org.labkey.api.query.QueryService;
import org.labkey.api.query.QuerySettings;
import org.labkey.api.query.QueryView;
import org.labkey.api.query.UserSchema;
import org.labkey.api.view.ViewContext;
import org.labkey.mobileappstudy.MobileAppStudySchema;

/**
 * Web part for showing a set of enrollment tokens.
 */
public class EnrollmentTokensWebPart extends QueryView
{
    public EnrollmentTokensWebPart(ViewContext viewContext)
    {
        super(QueryService.get().getUserSchema(viewContext.getUser(), viewContext.getContainer(), MobileAppStudySchema.NAME));
        setSettings(createQuerySettings(viewContext));
        setShowInsertNewButton(false);
        setShowImportDataButton(false);
        setShowDeleteButton(false);
        setShowReports(false);
        setShowUpdateColumn(false);
        setShowDetailsColumn(false);
    }

    private QuerySettings createQuerySettings(ViewContext viewContext)
    {
        UserSchema schema = getSchema();
        return schema.getSettings(viewContext,  "enrollmentTokens",  MobileAppStudySchema.ENROLLMENT_TOKEN_TABLE);
    }
}
