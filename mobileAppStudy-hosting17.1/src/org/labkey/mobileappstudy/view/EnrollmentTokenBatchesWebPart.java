/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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
        addButton(bar, generateBtn);
    }

}
