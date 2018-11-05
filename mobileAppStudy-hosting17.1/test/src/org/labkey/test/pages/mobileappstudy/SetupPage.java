/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.pages.mobileappstudy;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.components.mobileappstudy.StudySetupWebPart;
import org.labkey.test.components.mobileappstudy.TokenBatchesWebPart;
import org.labkey.test.pages.LabKeyPage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SetupPage extends LabKeyPage
{

    BaseWebDriverTest _test;
    public StudySetupWebPart studySetupWebPart;
    public TokenBatchesWebPart tokenBatchesWebPart;

    public SetupPage(BaseWebDriverTest test)
    {
        super(test);
        _test = test;
        tokenBatchesWebPart = new TokenBatchesWebPart(_test);
        studySetupWebPart = new StudySetupWebPart(_test);
    }

    public void validateSubmitButtonDisabled()
    {
        log("Validate that the submit button is disabled.");
        assertFalse("Submit button is showing as enabled, it should not be.", studySetupWebPart.isSubmitEnabled());
    }

    public void validateSubmitButtonEnabled()
    {
        log("Validate that the submit button is now enabled.");
        assertTrue("Submit button is not showing as enabled, it should be.", studySetupWebPart.isSubmitEnabled());
    }
}
