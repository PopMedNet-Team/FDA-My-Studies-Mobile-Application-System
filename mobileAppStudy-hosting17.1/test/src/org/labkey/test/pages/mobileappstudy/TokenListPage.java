/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */

package org.labkey.test.pages.mobileappstudy;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.WebDriverWrapper;
import org.labkey.test.WebTestHelper;
import org.labkey.test.components.CustomizeView;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.DataRegionTable;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class TokenListPage extends LabKeyPage<TokenListPage.ElementCache>
{
    public TokenListPage(WebDriverWrapper test)
    {
        super(test);
    }

    public static TokenListPage beginAt(BaseWebDriverTest test, String containerPath)
    {
        test.beginAt(WebTestHelper.buildURL("mobileappstudy", containerPath, "tokenList"));
        return new TokenListPage(test);
    }

    public int getNumTokens()
    {
        DataRegionTable dataRegion = new DataRegionTable("enrollmentTokens", getDriver());
        return dataRegion.getDataRowCount();
    }

    public void goToBatches()
    {
        doAndWaitForPageToLoad(() -> elementCache().tokenBatchLink.click());
    }

    public String getBatchId()
    {
        // There are two different urls to get to this page. However both urls have only one parameter, which is the batch id.
        Map<String, String> params;
        params = getUrlParameters();
        return (String)params.values().toArray()[0];
    }

    public String getToken(int index)
    {
        return getColumnData(index, "Token");
    }

    public String getColumnData(int index, String columnName)
    {
        DataRegionTable dataRegion = new DataRegionTable("enrollmentTokens", getDriver());
        return dataRegion.getDataAsText(index, columnName);
    }

    public void addColumnsToView(String ... columns)
    {
        DataRegionTable dataRegion = new DataRegionTable("enrollmentTokens", getDriver());
        CustomizeView cv = dataRegion.openCustomizeGrid();
        cv.showHiddenItems();
        for(String column : columns)
        {
            cv.addColumn(column);
        }
        cv.clickViewGrid();
    }

    public List<List<String>> getRows(String ... columnNames)
    {
        DataRegionTable dataRegion = new DataRegionTable("enrollmentTokens", getDriver());
        return dataRegion.getRows(columnNames);
    }

    @Override
    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }

    protected class ElementCache extends LabKeyPage.ElementCache
    {
        WebElement tokenBatchLink = Locator.linkWithText("Token Batches").findWhenNeeded(this);
    }
}
