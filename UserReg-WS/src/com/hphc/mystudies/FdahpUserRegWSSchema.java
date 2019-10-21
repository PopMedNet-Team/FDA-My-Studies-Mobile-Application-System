/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.hphc.mystudies;

import org.labkey.api.data.DbSchema;
import org.labkey.api.data.DbSchemaType;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.dialect.SqlDialect;

public class FdahpUserRegWSSchema
{
    private static final FdahpUserRegWSSchema _instance = new FdahpUserRegWSSchema();
    public static final String NAME = "fdahpUserRegWS";
    public static FdahpUserRegWSSchema getInstance()
    {
        return _instance;
    }

    private FdahpUserRegWSSchema()
    {
        // private constructor to prevent instantiation from
        // outside this class: this singleton should only be
        // accessed via FdahpUserRegWSSchema.getInstance()
    }

    public DbSchema getSchema()
    {
        return DbSchema.get(NAME, DbSchemaType.Module);
    }

    public SqlDialect getSqlDialect()
    {
        return getSchema().getSqlDialect();
    }

    public TableInfo getParticipantDetails(){
        return getSchema().getTable("UserDetails");
    }

    public TableInfo getAuthInfo(){     return getSchema().getTable("AuthInfo"); }

    public TableInfo getParticipantStudies(){
        return getSchema().getTable("ParticipantStudies");
    }

    public TableInfo getParticipantActivities(){
        return getSchema().getTable("ParticipantActivities");
    }

    public TableInfo getStudyConsent(){
        return getSchema().getTable("StudyConsent");
    }

    public TableInfo getPasswordHistory(){
        return getSchema().getTable("PasswordHistory");
    }

    public TableInfo getLoginAttempts(){return getSchema().getTable("LoginAttempts");}

    public TableInfo getUserAppDetails(){
        return getSchema().getTable("UserAppDetails");
    }

    public TableInfo getAppPropertiesDetails(){
        return getSchema().getTable("AppPropertiesDetails");
    }
}
