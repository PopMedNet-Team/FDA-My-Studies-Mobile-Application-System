<%
/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
%>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.template.ClientDependencies" %>
<%@ page import="org.labkey.mobileappstudy.data.MobileAppStudy" %>
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
                    collectionEnabled   : <%= collectionEnabled %>
                }
        );
    });
</script>
