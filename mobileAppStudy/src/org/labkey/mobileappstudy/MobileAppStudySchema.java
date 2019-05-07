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

import org.labkey.api.data.DbSchema;
import org.labkey.api.data.DbSchemaType;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.dialect.SqlDialect;

public class MobileAppStudySchema
{
    private static final MobileAppStudySchema _instance = new MobileAppStudySchema();
    public static final String NAME = "mobileappstudy";
    public static final String ENROLLMENT_TOKEN_BATCH_TABLE = "EnrollmentTokenBatch";
    public static final String ENROLLMENT_TOKEN_TABLE = "EnrollmentToken";
    public static final String STUDY_TABLE = "Study";
    public static final String PARTICIPANT_TABLE = "Participant";
    public static final String PARTICIPANT_STATUS_TABLE = "ParticipantStatus";
    public static final String RESPONSE_TABLE = "Response";
    public static final String RESPONSE_METADATA_TABLE = "ResponseMetadata";
    public static final String RESPONSE_STATUS_TABLE = "ResponseStatus";

    public static MobileAppStudySchema getInstance()
    {
        return _instance;
    }

    private MobileAppStudySchema()
    {
        // private constructor to prevent instantiation from
        // outside this class: this singleton should only be
        // accessed via org.labkey.mobileappstudy.MobileAppStudySchema.getInstance()
    }

    public DbSchema getSchema()
    {
        return DbSchema.get(NAME, DbSchemaType.Module);
    }

    public SqlDialect getSqlDialect()
    {
        return getSchema().getSqlDialect();
    }

    public TableInfo getTableInfoEnrollmentTokenBatch()
    {
        return getSchema().getTable(ENROLLMENT_TOKEN_BATCH_TABLE);
    }

    public TableInfo getTableInfoEnrollmentToken()
    {
        return getSchema().getTable(ENROLLMENT_TOKEN_TABLE);
    }

    public TableInfo getTableInfoParticipant()
    {
        return getSchema().getTable(PARTICIPANT_TABLE);
    }

    public TableInfo getTableInfoStudy()
    {
        return getSchema().getTable(STUDY_TABLE);
    }

    public TableInfo getTableInfoResponse()
    {
        return getSchema().getTable(RESPONSE_TABLE);
    }
    public TableInfo getTableInfoResponseMetadata()
    {
        return getSchema().getTable(RESPONSE_METADATA_TABLE);
    }

}
