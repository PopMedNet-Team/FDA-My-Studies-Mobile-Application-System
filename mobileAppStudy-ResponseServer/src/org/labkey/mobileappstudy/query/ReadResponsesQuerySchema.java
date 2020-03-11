/*
 * Copyright (c) 2017-2019 LabKey Corporation
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.collections.Sets;
import org.labkey.api.data.BaseColumnInfo;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerFilter;
import org.labkey.api.data.ForeignKey;
import org.labkey.api.data.TableInfo;
import org.labkey.api.exp.list.ListService;
import org.labkey.api.module.Module;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.FilteredTable;
import org.labkey.api.query.QuerySchema;
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

    private ReadResponsesQuerySchema(User user, Container container, @Nullable Participant participant)
    {
        super(NAME, "Special query schema that allows the mobile application to read responses submitted by the participant using that device", user, container, getListSchema(user, container).getDbSchema());
        _listSchema = getListSchema(user, container);
        _participant = participant;
        setRestricted(true);
    }

    private static UserSchema getListSchema(User user, Container container)
    {
        return ListService.get().getUserSchema(user, container);
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
                return new ReadResponsesQuerySchema(schema.getUser(), schema.getContainer(), null);
            }
        });
    }

    public static ReadResponsesQuerySchema get(Participant participant)
    {
        return new ReadResponsesQuerySchema(User.guest, participant.getContainer(), participant);
    }

    @Override
    public boolean canReadSchema() throws UnauthorizedException
    {
        // Either we have a participant filter or the user needs read permissions
        return null != _participant || super.canReadSchema();
    }

    @Override
    public @Nullable TableInfo createTable(String name, ContainerFilter cf)
    {
        TableInfo listTable = _listSchema.createTable(name, cf);
        return listTable == null ? null : new ResponseTable(listTable, _participant, this, cf);
    }

    private static class ResponseTable extends FilteredTable<ReadResponsesQuerySchema>
    {
        ResponseTable(@NotNull TableInfo table, @Nullable Participant participant, @NotNull ReadResponsesQuerySchema userSchema, ContainerFilter cf)
        {
            super(table, userSchema, cf);

            table.getColumns().forEach(this::addWrapColumn);  // Future: Limit to NotPHI columns only

            setDefaultVisibleColumns(table.getDefaultVisibleColumns());

            if (null != participant)
            {
                // TODO: Seems wrong... we should get the column from this, not _listTable... but if we do that, the assertCorrectParentTable() assert fails
                ColumnInfo pid = table.getColumn("ParticipantId");

                if (null != pid)  // Maybe throw instead?
                    addCondition(pid, participant.getRowId());
            }
        }

        private static final Set<String> NAUGHTY_COLUMNS = Sets.newCaseInsensitiveHashSet("CreatedBy", "ModifiedBy", "Container");

        @Override
        public BaseColumnInfo addWrapColumn(ColumnInfo column)
        {
            BaseColumnInfo wrapped = super.addWrapColumn(column);

            // Nuke standard columns that have foreign keys to other schemas... otherwise, selectRows and executeSql would blow up when these columns are selected
            if (NAUGHTY_COLUMNS.contains(wrapped.getColumnName()))
                wrapped.setFk((ForeignKey)null);

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
