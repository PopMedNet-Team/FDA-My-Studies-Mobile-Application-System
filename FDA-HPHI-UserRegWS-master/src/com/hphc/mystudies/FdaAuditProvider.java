/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies;

/**
 * Created by Ravinder on 5/16/2017.
 */
import org.labkey.api.audit.AbstractAuditTypeProvider;
import org.labkey.api.audit.AuditTypeEvent;
import org.labkey.api.audit.AuditTypeProvider;
import org.labkey.api.audit.query.AbstractAuditDomainKind;
import org.labkey.api.exp.PropertyDescriptor;
import org.labkey.api.exp.PropertyType;
import org.labkey.api.query.FieldKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FdaAuditProvider extends AbstractAuditTypeProvider implements AuditTypeProvider
{
    public static final String EVENT_TYPE = "FDA User Registration Events";
    public static final String FDA_AUDIT_EVENT = "FdaUserRegistrationAuditEvent";
    public static final String COLUMN_NAME_USERID = "userId";
    public static final String COLUMN_NAME_ACTIVITY = "Activity";
    public static final String COLUMN_NAME_ACTIVITY_DETAILS = "ActivityDetails";

    static final List<FieldKey> defaultVisibleColumns = new ArrayList<>();

    static {

        defaultVisibleColumns.add(FieldKey.fromParts(COLUMN_NAME_CREATED));
        defaultVisibleColumns.add(FieldKey.fromParts(COLUMN_NAME_CREATED_BY));
        defaultVisibleColumns.add(FieldKey.fromParts(COLUMN_NAME_IMPERSONATED_BY));
        defaultVisibleColumns.add(FieldKey.fromParts(COLUMN_NAME_USERID));
        defaultVisibleColumns.add(FieldKey.fromParts(COLUMN_NAME_ACTIVITY));
        defaultVisibleColumns.add(FieldKey.fromParts(COLUMN_NAME_ACTIVITY_DETAILS));
        defaultVisibleColumns.add(FieldKey.fromParts(COLUMN_NAME_COMMENT));
    }
    @Override
    protected AbstractAuditDomainKind getDomainKind()
    {
        return new FdaAuditDomainKind();
    }

    @Override
    public String getEventName()
    {
        return FDA_AUDIT_EVENT;
    }

    @Override
    public String getLabel()
    {
        return EVENT_TYPE;
    }

    @Override
    public String getDescription()
    {
        return "Information about general changes to Fda Registration Services.";
    }


    public static  class FdaAuditDomainKind extends AbstractAuditDomainKind{
        public static final String NAME = "FdaUserRegistrationAuditDomain";
        public static String NAMESPACE_PREFIX = "Audit-" + NAME;

        private final Set<PropertyDescriptor> _fields;

        public FdaAuditDomainKind()
        {
            super(FDA_AUDIT_EVENT);

            Set<PropertyDescriptor> fields = new LinkedHashSet<>();
            fields.add(createPropertyDescriptor("userId", PropertyType.STRING));
            fields.add(createPropertyDescriptor("Activity", PropertyType.STRING));
            fields.add(createPropertyDescriptor("ActivityDetails", PropertyType.STRING));
             _fields = Collections.unmodifiableSet(fields);
        }

        @Override
        public Set<PropertyDescriptor> getProperties()
        {
            return _fields;
        }

        @Override
        protected String getNamespacePrefix()
        {
            return NAMESPACE_PREFIX;
        }

        @Override
        public String getKindName()
        {
            return NAME;
        }
    }

    @Override
    public Map<FieldKey, String> legacyNameMap()
    {
        Map<FieldKey, String> legacyNames = super.legacyNameMap();
        legacyNames.put(FieldKey.fromParts("key1"), COLUMN_NAME_USERID);
        legacyNames.put(FieldKey.fromParts("key2"), COLUMN_NAME_ACTIVITY);
        legacyNames.put(FieldKey.fromParts("key3"), COLUMN_NAME_ACTIVITY_DETAILS);

        return legacyNames;
    }

    @Override
    public <K extends AuditTypeEvent> Class<K> getEventClass()
    {
        return (Class<K>)FdaAuditEvent.class;
    }

    @Override
    public List<FieldKey> getDefaultVisibleColumns()
    {
        return defaultVisibleColumns;
    }

    public static class FdaAuditEvent extends AuditTypeEvent {

        private String _activity;
        private String _activityDetails;
        private String _userId;

        public FdaAuditEvent(){
            super();
        }
        public FdaAuditEvent(String eventType,String container, String comment){
            super(FDA_AUDIT_EVENT,container,comment);
        }

        public String getActivity()
        {
            return _activity;
        }

        public void setActivity(String activity)
        {
            _activity = activity;
        }

        public String getActivityDetails()
        {
            return _activityDetails;
        }

        public void setActivityDetails(String activityDetails)
        {
            _activityDetails = activityDetails;
        }

        public String getUserId()
        {
            return _userId;
        }

        public void setUserId(String userId)
        {
            _userId = userId;
        }
    }
}
