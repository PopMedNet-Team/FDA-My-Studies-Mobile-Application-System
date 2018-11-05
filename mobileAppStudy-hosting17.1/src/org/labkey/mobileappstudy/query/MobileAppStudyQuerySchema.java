/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.query;

import org.jetbrains.annotations.Nullable;
import org.labkey.api.data.Container;
import org.labkey.api.data.DbSchema;
import org.labkey.api.data.DbSchemaType;
import org.labkey.api.data.EnumTableInfo;
import org.labkey.api.data.TableInfo;
import org.labkey.api.module.Module;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.query.SimpleUserSchema;
import org.labkey.api.security.User;
import org.labkey.mobileappstudy.MobileAppStudyModule;
import org.labkey.mobileappstudy.data.Participant;
import org.labkey.mobileappstudy.data.SurveyResponse;

import static org.labkey.mobileappstudy.MobileAppStudySchema.ENROLLMENT_TOKEN_BATCH_TABLE;
import static org.labkey.mobileappstudy.MobileAppStudySchema.ENROLLMENT_TOKEN_TABLE;
import static org.labkey.mobileappstudy.MobileAppStudySchema.PARTICIPANT_STATUS_TABLE;
import static org.labkey.mobileappstudy.MobileAppStudySchema.RESPONSE_STATUS_TABLE;

public class MobileAppStudyQuerySchema extends SimpleUserSchema
{
    public static final String NAME = "mobileappstudy";
    public static final String DESCRIPTION = "Provides data about study enrollment and survey responses";

    public MobileAppStudyQuerySchema(User user, Container container)
    {
        super(NAME, DESCRIPTION, user, container, DbSchema.get(NAME, DbSchemaType.Module));
    }

    public static void register(final MobileAppStudyModule module)
    {
        DefaultSchema.registerProvider(NAME, new DefaultSchema.SchemaProvider(module)
        {
            @Override
            public QuerySchema createSchema(DefaultSchema schema, Module module)
            {
                return new MobileAppStudyQuerySchema(schema.getUser(), schema.getContainer());
            }
        });
    }

    @Nullable
    @Override
    public TableInfo createTable(String name)
    {
        if (ENROLLMENT_TOKEN_BATCH_TABLE.equalsIgnoreCase(name))
        {
            return new EnrollmentTokenBatchTable(this);
        }
        else if (ENROLLMENT_TOKEN_TABLE.equalsIgnoreCase(name))
        {
            return new EnrollmentTokenTable(this);
        }
        else if(RESPONSE_STATUS_TABLE.equalsIgnoreCase(name))
        {
            return new EnumTableInfo<>(
                SurveyResponse.ResponseStatus.class,
                this,
                SurveyResponse.ResponseStatus::getPkId,
                true,
                "Possible states a SurveyResponse might be in"
            );
        }
        else if(PARTICIPANT_STATUS_TABLE.equalsIgnoreCase(name))
        {
            return new EnumTableInfo<>(
                    Participant.ParticipantStatus.class,
                    this,
                    Participant.ParticipantStatus::getPkId,
                    true,
                    "Possible states a Participant might be in"
            );
        }
        else
            return super.createTable(name);
    }
}
