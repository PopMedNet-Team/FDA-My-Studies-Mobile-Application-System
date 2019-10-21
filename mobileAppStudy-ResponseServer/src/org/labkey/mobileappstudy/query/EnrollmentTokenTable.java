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
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.ContainerFilter;
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
class EnrollmentTokenTable extends SimpleUserSchema.SimpleTable<MobileAppStudyQuerySchema>
{
    EnrollmentTokenTable(MobileAppStudyQuerySchema schema, ContainerFilter cf)
    {
        super(schema, schema.getDbSchema().getTable(MobileAppStudySchema.ENROLLMENT_TOKEN_TABLE), cf);

        // wrap all the existing columns
        wrapAllColumns();

        BaseColumnInfo idColumn = getMutableColumn("BatchId");

        ActionURL base = new ActionURL(MobileAppStudyController.TokenBatchAction.class, getContainer());
        DetailsURL detailsURL = new DetailsURL(base, Collections.singletonMap("query.RowId~eq", "batchId"));

        idColumn.setURL(detailsURL);

        // don't link out to the schema browser details (where the data can be edited).
        getMutableColumn("Token").setURL(null);

        BaseColumnInfo tokenColumn = getMutableColumn("Token");
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

        getMutableColumn("ParticipantId").setURL(null);
    }
}
