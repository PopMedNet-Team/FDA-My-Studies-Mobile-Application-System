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
        cv.applyCustomView();
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
