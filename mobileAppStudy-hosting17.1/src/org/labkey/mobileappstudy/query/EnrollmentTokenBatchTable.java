/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.query;

import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.JdbcType;
import org.labkey.api.data.SQLFragment;
import org.labkey.api.query.DetailsURL;
import org.labkey.api.query.ExprColumn;
import org.labkey.api.query.SimpleUserSchema;
import org.labkey.api.view.ActionURL;
import org.labkey.mobileappstudy.MobileAppStudyController;
import org.labkey.mobileappstudy.MobileAppStudySchema;

import java.util.Collections;

/**
 * Created by susanh on 10/11/16.
 */
public class EnrollmentTokenBatchTable extends SimpleUserSchema.SimpleTable<MobileAppStudyQuerySchema>
{
    public EnrollmentTokenBatchTable(MobileAppStudyQuerySchema schema)
    {
        super(schema, schema.getDbSchema().getTable(MobileAppStudySchema.ENROLLMENT_TOKEN_BATCH_TABLE));

        // wrap all the existing columns
        wrapAllColumns();

        ColumnInfo idColumn = getColumn("RowId");

        ActionURL base = new ActionURL(MobileAppStudyController.TokenListAction.class, getContainer());
        DetailsURL detailsURL = new DetailsURL(base, Collections.singletonMap("enrollmentTokens.BatchId/RowId~eq", "rowId"));

        idColumn.setURL(detailsURL);
        addCountColumn();
    }

    private void addCountColumn()
    {
        SQLFragment sql = new SQLFragment("(SELECT COUNT(*) FROM " + MobileAppStudySchema.NAME + "." + MobileAppStudySchema.ENROLLMENT_TOKEN_TABLE + " T  WHERE T.BatchId = " + ExprColumn.STR_TABLE_ALIAS + ".rowId" +
                " AND T.ParticipantId IS NOT NULL)");
        ExprColumn col = new ExprColumn(this, "TokensInUse", sql, JdbcType.INTEGER);
        col.setFormat("#,###");
        addColumn(col);
    }
}
