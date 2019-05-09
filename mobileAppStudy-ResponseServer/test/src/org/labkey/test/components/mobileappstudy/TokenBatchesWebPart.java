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
package org.labkey.test.components.mobileappstudy;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.WebTestHelper;
import org.labkey.test.components.BodyWebPart;
import org.labkey.test.pages.mobileappstudy.TokenBatchPage;
import org.labkey.test.selenium.LazyWebElement;
import org.labkey.test.util.DataRegionTable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

import static org.labkey.test.util.DataRegionTable.DataRegion;

public class TokenBatchesWebPart extends BodyWebPart<TokenBatchesWebPart.ElementCache>
{
    public TokenBatchesWebPart(WebDriver driver)
    {
        super(driver, "Enrollment Token Batches");
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

    public Map<String, String> getBatchData(String batchId)
    {
        return elementCache().tokenBatchDataRegion.getRowDataAsMap("RowId", batchId);
    }

    protected void waitForReady()
    {
        elementCache().tokenBatchDataRegion.getComponentElement().isDisplayed();
    }

    @Override
    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }

    public class ElementCache extends BodyWebPart.ElementCache
    {
        WebElement newBatchButton = new LazyWebElement(Locator.lkButton("New Batch"), this);
        DataRegionTable tokenBatchDataRegion = DataRegion(getDriver()).withName("enrollmentTokenBatches").findWhenNeeded(this);
    }
}
