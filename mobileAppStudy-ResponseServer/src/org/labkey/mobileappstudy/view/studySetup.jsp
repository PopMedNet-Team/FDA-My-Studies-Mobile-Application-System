<%
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
%>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.template.ClientDependencies" %>
<%@ page import="org.labkey.mobileappstudy.data.MobileAppStudy" %>
<%@ page import="org.labkey.mobileappstudy.MobileAppStudyManager" %>
<%@ page import="org.labkey.mobileappstudy.forwarder.ForwarderProperties" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page extends="org.labkey.api.jsp.JspBase" %>
<%@ taglib prefix="labkey" uri="http://www.labkey.org/taglib" %>

<%!
    @Override
    public void addClientDependencies(ClientDependencies dependencies)
    {
        dependencies.add("Ext4");
        dependencies.add("mobileAppStudy/panel/studySetup.js");
    }
%>
<%
    JspView<MobileAppStudy> me = (JspView<MobileAppStudy>) HttpView.currentView();
    MobileAppStudy bean = me.getModelBean();

    String renderId = "labkey-mobileappstudy-studysetup";
    String shortName = bean.getShortName();
    Boolean isEditable = bean.getEditable();
    Boolean canChangeCollection = bean.getCanChangeCollection();
    boolean collectionEnabled = bean.getCollectionEnabled();

    Map<String, String> forwardingProperties = MobileAppStudyManager.get().getForwardingProperties(getContainer());
    boolean forwardingEnabled = Boolean.valueOf(forwardingProperties.get(ForwarderProperties.ENABLED_PROPERTY_NAME));
    String forwardingURL = forwardingProperties.get(ForwarderProperties.URL_PROPERTY_NAME);
    String forwardingUser = forwardingProperties.get(ForwarderProperties.USER_PROPERTY_NAME);
    String forwardingPassword = StringUtils.isNotBlank(forwardingProperties.get(ForwarderProperties.USER_PROPERTY_NAME)) ?
            ForwarderProperties.PASSWORD_PLACEHOLDER :
            "";

%>
<style type="text/css">
    .labkey-warning  { color: red; }
</style>

<labkey:errors></labkey:errors>
<div id="<%= h(renderId)%>" class="requests-editor"></div>

<script type="text/javascript">
    Ext4.onReady(function(){

        Ext4.create('LABKEY.MobileAppStudy.StudySetupPanel',
                {
                    renderTo            : <%= q(renderId) %>,
                    shortName           : <%= qh(shortName) %>,
                    isEditable          : <%= isEditable %>,
                    canChangeCollection : <%= canChangeCollection %>,
                    collectionEnabled   : <%= collectionEnabled %>,
                    forwardingEnabled   : <%= forwardingEnabled %>,
                    forwardingUrl       : <%= q(forwardingURL) %>,
                    forwardingUsername  : <%= q(forwardingUser) %>,
                    forwardingPassword  : <%= q(forwardingPassword) %>
                }
        );
    });
</script>
