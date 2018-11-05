/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.components.mobileappstudy;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.WebTestHelper;
import org.labkey.test.components.WebPart;
import org.labkey.test.pages.mobileappstudy.TokenBatchPage;
import org.labkey.test.selenium.LazyWebElement;
import org.labkey.test.util.DataRegionTable;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;

public class TokenBatchesWebPart extends WebPart<TokenBatchesWebPart.ElementCache>
{
    public TokenBatchesWebPart(BaseWebDriverTest test)
    {
        super(test.getWrappedDriver(), Locators.dataRegionLocator.findElement(test.getDriver()));
        waitForReady();
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

    public TokenBatchPopup openNewBatchPopup()
    {
        getWrapper().log("Opening new batch request popup.");
        elementCache().newBatchButton.click();
        return new TokenBatchPopup(getWrapper().getDriver());
    }

    public Map<String, String> getBatchData(int batchId)
    {
        return getBatchData(String.valueOf(batchId));
    }

    public Map<String, String> getBatchData(String batchId)
    {
        DataRegionTable dataRegion = new DataRegionTable("enrollmentTokenBatches", getDriver());
        int rowIndex = dataRegion.getRowIndex("RowId", batchId);
        Map<String, String> data = new HashMap<>();
        for (String col : dataRegion.getColumnNames())
        {
            data.put(col, dataRegion.getDataAsText(rowIndex, col));
        }
        return data;
    }

    protected void waitForReady()
    {
        getWrapper().waitForElement(Locators.dataRegionLocator);
    }

    protected ElementCache elementCache()
    {
        return new ElementCache();
    }

    public class ElementCache extends WebPart.ElementCache
    {
        WebElement newBatchButton = new LazyWebElement(Locators.newBatchButton, getDriver());
    }

    public static class Locators
    {
        public static final Locator.XPathLocator newBatchButton = Locator.lkButton().withText("New Batch");
        protected static final Locator dataRegionLocator = Locator.xpath("//table[tbody/tr/th[@title='Enrollment Token Batches']]");
    }
}
