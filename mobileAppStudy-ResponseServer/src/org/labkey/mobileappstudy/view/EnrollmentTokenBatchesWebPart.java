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

import org.labkey.api.data.ActionButton;
import org.labkey.api.data.ButtonBar;
import org.labkey.api.query.QueryService;
import org.labkey.api.query.QuerySettings;
import org.labkey.api.query.QueryView;
import org.labkey.api.query.UserSchema;
import org.labkey.api.view.DataView;
import org.labkey.api.view.ViewContext;
import org.labkey.api.view.template.ClientDependency;
import org.labkey.mobileappstudy.MobileAppStudySchema;

/**
 * Web part for displaying current enrollment token batches and allowing
 * for the generation of new batches.
 */
public class EnrollmentTokenBatchesWebPart extends QueryView
{
    public EnrollmentTokenBatchesWebPart(ViewContext viewContext)
    {
        super(QueryService.get().getUserSchema(viewContext.getUser(), viewContext.getContainer(), MobileAppStudySchema.NAME));
        setTitle("Enrollment Token Batches");
        setSettings(createQuerySettings(viewContext));
        addClientDependency(ClientDependency.fromPath("Ext4"));
        addClientDependency(ClientDependency.fromPath("mobileAppStudy/panel/enrollmentTokenBatchFormPanel.js"));
        setShowInsertNewButton(false);
        setShowImportDataButton(false);
        setShowDeleteButton(false);
        setShowReports(false);
        setShowUpdateColumn(false);
    }

    private QuerySettings createQuerySettings(ViewContext viewContext)
    {
        UserSchema schema = getSchema();
        return schema.getSettings(viewContext, "enrollmentTokenBatches", MobileAppStudySchema.ENROLLMENT_TOKEN_BATCH_TABLE);
    }

    protected void populateButtonBar(DataView view, ButtonBar bar)
    {
        super.populateButtonBar(view, bar);
        ActionButton generateBtn = new ActionButton("New Batch");
        generateBtn.setScript("Ext4.create('LABKEY.MobileAppStudy.EnrollmentTokenBatchFormPanel', {gridButton: this}).show();");
        bar.add(generateBtn);
    }
}
