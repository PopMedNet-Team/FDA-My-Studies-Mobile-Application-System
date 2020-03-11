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
package org.labkey.mobileappstudy.query;

import org.labkey.api.data.BaseColumnInfo;
import org.labkey.api.data.ContainerFilter;
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
class EnrollmentTokenBatchTable extends SimpleUserSchema.SimpleTable<MobileAppStudyQuerySchema>
{
    EnrollmentTokenBatchTable(MobileAppStudyQuerySchema schema, ContainerFilter cf)
    {
        super(schema, schema.getDbSchema().getTable(MobileAppStudySchema.ENROLLMENT_TOKEN_BATCH_TABLE), cf);

        // wrap all the existing columns
        wrapAllColumns();

        BaseColumnInfo idColumn = getMutableColumn("RowId");

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
