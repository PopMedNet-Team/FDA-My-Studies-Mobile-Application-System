/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */

package org.labkey.test.pages.mobileappstudy;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.WebTestHelper;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.selenium.LazyWebElement;
import org.labkey.test.util.DataRegionTable;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

public class TokenBatchPage extends LabKeyPage<TokenBatchPage.ElementCache>
{
    public TokenBatchPage(BaseWebDriverTest test)
    {
        super(test);
    }

    public static TokenBatchPage beginAt(BaseWebDriverTest test, String containerPath)
    {
        test.beginAt(WebTestHelper.buildURL("mobileappstudy", containerPath, "tokenBatch"));
        return new TokenBatchPage(test);
    }

    public boolean isNewBatchPresent()
    {
        return elementCache().newBatchButton.isDisplayed();
    }

    public boolean isNewBatchEnabled()
    {
        return elementCache().newBatchButton.isEnabled();
    }

    public void openNewBatchPopup()
    {
        log("Opening new batch request popup.");
        elementCache().newBatchButton.click();
    }

    public Map<String, String> getBatchData(int batchId)
    {
        DataRegionTable dataRegion = new DataRegionTable("enrollmentTokenBatches", getDriver());
        int rowIndex = dataRegion.getRowIndex("Batch Id", String.valueOf(batchId));
        Map<String, String> data = new HashMap<>();
        for (String col : dataRegion.getColumnNames())
        {
            data.put(col, dataRegion.getDataAsText(rowIndex, col));
        }
        return data;
    }

    @Override
    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }

    protected class ElementCache extends LabKeyPage.ElementCache
    {
        WebElement newBatchButton = new LazyWebElement(Locator.lkButton().withText("New Batch"), this);
    }
}
