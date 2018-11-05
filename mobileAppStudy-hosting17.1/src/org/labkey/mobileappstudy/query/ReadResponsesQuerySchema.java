/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.query;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.collections.Sets;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.Container;
import org.labkey.api.data.TableInfo;
import org.labkey.api.module.Module;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.FilteredTable;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.query.QueryService;
import org.labkey.api.query.SchemaKey;
import org.labkey.api.query.UserSchema;
import org.labkey.api.security.User;
import org.labkey.api.security.UserPrincipal;
import org.labkey.api.security.permissions.Permission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.util.StringExpression;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.UnauthorizedException;
import org.labkey.mobileappstudy.data.Participant;

import java.util.Set;

/**
 * Created by adam on 1/31/2017.
 */
public class ReadResponsesQuerySchema extends UserSchema
{
    public static final String NAME = "MobileAppResponse";

    private final UserSchema _listSchema;
    private final @Nullable Participant _participant;

    private ReadResponsesQuerySchema(User user, Container container, UserSchema listSchema, @Nullable Participant participant)
    {
        super(NAME, "Special query schema that allows the mobile application to read responses submitted by the participant using that device", user, container, listSchema.getDbSchema());
        _listSchema = listSchema;
        _participant = participant;
        setRestricted(true);
    }

    public static void register(Module module)
    {
        DefaultSchema.registerProvider(NAME, new DefaultSchema.SchemaProvider(module)
        {
            @Override
            public boolean isAvailable(DefaultSchema schema, Module module)
            {
                return true;
            }

            public QuerySchema createSchema(DefaultSchema schema, Module module)
            {
                UserSchema list = schema.getUserSchema("lists");

                return new ReadResponsesQuerySchema(schema.getUser(), schema.getContainer(), list, null);
            }
        });
    }

    public static ReadResponsesQuerySchema get(Participant participant)
    {
        Container c = participant.getContainer();
        UserSchema list = QueryService.get().getUserSchema(User.guest, c, SchemaKey.fromParts("lists"));

        return new ReadResponsesQuerySchema(User.guest, c, list, participant);
    }

    @Override
    public boolean canReadSchema() throws UnauthorizedException
    {
        // Either we have a participant filter or the user needs read permissions
        return null != _participant || super.canReadSchema();
    }

    @Nullable
    @Override
    public TableInfo createTable(String name)
    {
        TableInfo listTable = _listSchema.createTable(name);
        return listTable == null ? null : new ResponseTable(listTable, _participant, this);
    }

    private static class ResponseTable extends FilteredTable<ReadResponsesQuerySchema>
    {
        private final TableInfo _listTable;

        public ResponseTable(@NotNull TableInfo table, @Nullable Participant participant, @NotNull ReadResponsesQuerySchema userSchema)
        {
            super(table, userSchema);

            _listTable = table;
            _listTable.getColumns().forEach(this::addWrapColumn);  // Future: Limit to NotPHI columns only

            setDefaultVisibleColumns(_listTable.getDefaultVisibleColumns());

            if (null != participant)
            {
                // TODO: Seems wrong... we should get the column from this, not _listTable... but if we do that, the assertCorrectParentTable() assert fails
                ColumnInfo pid = _listTable.getColumn("ParticipantId");

                if (null != pid)  // Maybe throw instead?
                    addCondition(pid, participant.getRowId());
            }
        }

        private static final Set<String> NAUGHTY_COLUMNS = Sets.newCaseInsensitiveHashSet("CreatedBy", "ModifiedBy", "Container");

        @Override
        public ColumnInfo addWrapColumn(ColumnInfo column)
        {
            ColumnInfo wrapped = super.addWrapColumn(column);

            // Nuke standard columns that have foreign keys to other schemas... otherwise, selectRows and executeSql would blow up when these columns are selected
            if (NAUGHTY_COLUMNS.contains(wrapped.getColumnName()))
                wrapped.setFk(null);

            return wrapped;
        }

        @Override
        public boolean hasPermission(@NotNull UserPrincipal user, @NotNull Class<? extends Permission> perm)
        {
            return ReadPermission.class == perm;
        }

        @Override
        public ActionURL getGridURL(Container container)
        {
            return null;
        }

        @Override
        public StringExpression getDetailsURL(@Nullable Set<FieldKey> columns, Container container)
        {
            return null;
        }
    }

    @Override
    public Set<String> getTableNames()
    {
        return _listSchema.getTableNames();
    }
}
