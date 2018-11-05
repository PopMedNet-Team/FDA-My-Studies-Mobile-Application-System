/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.query;

import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.DataColumn;
import org.labkey.api.data.RenderContext;
import org.labkey.api.query.DetailsURL;
import org.labkey.api.query.SimpleUserSchema;
import org.labkey.api.view.ActionURL;
import org.labkey.mobileappstudy.MobileAppStudyController;
import org.labkey.mobileappstudy.MobileAppStudySchema;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;

/**
 * Created by susanh on 10/11/16.
 */
public class EnrollmentTokenTable extends SimpleUserSchema.SimpleTable<MobileAppStudyQuerySchema>
{
    public EnrollmentTokenTable(MobileAppStudyQuerySchema schema)
    {
        super(schema, schema.getDbSchema().getTable(MobileAppStudySchema.ENROLLMENT_TOKEN_TABLE));

        // wrap all the existing columns
        wrapAllColumns();

        ColumnInfo idColumn = getColumn("BatchId");

        ActionURL base = new ActionURL(MobileAppStudyController.TokenBatchAction.class, getContainer());
        DetailsURL detailsURL = new DetailsURL(base, Collections.singletonMap("query.RowId~eq", "batchId"));

        idColumn.setURL(detailsURL);

        // don't link out to the schema browser details (where the data can be edited).
        getColumn("Token").setURL(null);

        ColumnInfo tokenColumn = getColumn("Token");
        tokenColumn.setDisplayColumnFactory(colInfo -> new DataColumn(colInfo)
        {
            @Override
            public void renderGridCellContents(RenderContext ctx, Writer out) throws IOException
            {
                if (null != getValue(ctx))
                {
                    out.write("<span style='font-family: monospace'>" + getValue(ctx) + "</span>");
                }

            }
        });

        getColumn("ParticipantId").setURL(null);

    }
}
