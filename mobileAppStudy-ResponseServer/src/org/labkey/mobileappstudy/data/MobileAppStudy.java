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
package org.labkey.mobileappstudy.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.labkey.api.data.Container;
import org.labkey.api.security.User;

import java.util.Date;

/**
 * Model of database table representing a study in the mobileappstudy schema.
 */
public class MobileAppStudy
{
    private Integer _rowId;
    private String _shortName;
    private Boolean _isEditable = true;
    private Container _container;
    private Date _created;
    private User _createdBy;
    private boolean _collectionEnabled;
    private Boolean _canChangeCollection;

    public String getShortName()
    {
        return _shortName;
    }

    public void setShortName(String shortName)
    {
        _shortName = shortName;
    }

    public Boolean getEditable()
    {
        return _isEditable;
    }

    public void setEditable(Boolean editable)
    {
        _isEditable = editable;
    }

    public Container getContainer()
    {
        return _container;
    }

    public void setContainer(Container container)
    {
        _container = container;
    }

    public Date getCreated()
    {
        return _created;
    }

    public void setCreated(Date created)
    {
        _created = created;
    }

    @JsonIgnore
    public User getCreatedBy()
    {
        return _createdBy;
    }

    public void setCreatedBy(User createdBy)
    {
        _createdBy = createdBy;
    }

    public Integer getRowId()
    {
        return _rowId;
    }

    public void setRowId(Integer rowId)
    {
        _rowId = rowId;
    }

    public boolean getCollectionEnabled() {
        return _collectionEnabled;
    }

    public void setCollectionEnabled(boolean collectionEnabled) {
        _collectionEnabled = collectionEnabled;
    }

    public Boolean getCanChangeCollection() {
        return _canChangeCollection;
    }

    public void setCanChangeCollection(Boolean canChangeCollection) {
        _canChangeCollection = canChangeCollection;
    }


}
