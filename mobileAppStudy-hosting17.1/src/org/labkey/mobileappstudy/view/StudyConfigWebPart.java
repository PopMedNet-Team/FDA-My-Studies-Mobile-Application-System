/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.view;

import org.labkey.api.security.permissions.AdminPermission;
import org.labkey.api.view.JspView;
import org.labkey.api.view.ViewContext;
import org.labkey.mobileappstudy.MobileAppStudyManager;
import org.labkey.mobileappstudy.data.MobileAppStudy;

/**
 * Web part for associating a study short name with a container.
 */
public class StudyConfigWebPart extends JspView<MobileAppStudy>
{
    public StudyConfigWebPart(ViewContext viewContext)
    {
        super("/org/labkey/mobileappstudy/view/studySetup.jsp");
        setTitle("Study Setup");

        MobileAppStudy bean = MobileAppStudyManager.get().getStudy(viewContext.getContainer());
        bean = bean != null ? bean : new MobileAppStudy();

        //TODO: Determine permission level needed
        bean.setEditable(!MobileAppStudyManager.get().hasStudyParticipants(viewContext.getContainer()));
        bean.setCanChangeCollection(viewContext.getContainer().hasPermission(viewContext.getUser(), AdminPermission.class));

        this.setModelBean(bean);
    }
}
