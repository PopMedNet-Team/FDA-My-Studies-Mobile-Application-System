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

import org.labkey.test.Locator;
import org.labkey.test.components.ext4.RadioButton;
import org.labkey.test.components.ext4.Window;
import org.labkey.test.components.html.Input;
import org.labkey.test.pages.mobileappstudy.TokenListPage;
import org.labkey.test.selenium.LazyWebElement;
import org.labkey.test.util.Ext4Helper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.LoggedParam;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.labkey.test.components.html.Input.Input;

public class TokenBatchPopup extends Window<TokenBatchPopup.ElementCache>
{
    public TokenBatchPopup(WebDriver wd)
    {
        super("Generate Tokens", wd);
    }

    public boolean isSubmitEnabled()
    {
        return Locators.enabledSubmitButton.findElements(this).size() > 0;
    }

    public boolean isCancelEnabled()
    {
        return Locators.enabledCancelButton.findElements(this).size() > 0;
    }

    public void selectOtherBatchSize()
    {
        RadioButton otherCount = elementCache().findCountChoice("Other");
        otherCount.check();
    }

    @LogMethod(quiet = true)
    public void setOtherBatchSize(@LoggedParam String size)
    {
        elementCache().otherCountInput.set(size);
        Locators.enabledSubmitButton.waitForElement(this, 1000);
    }

    @LogMethod(quiet = true)
    public void selectBatchSize(@LoggedParam String size)
    {
        RadioButton countButton = elementCache().findCountChoice(size);
        if (countButton.isDisplayed())
            countButton.check();
        else
        {
            selectOtherBatchSize();
            setOtherBatchSize(size);
        }
    }

    @LogMethod(quiet = true)
    public TokenListPage createNewBatch(@LoggedParam String size)
    {
        selectBatchSize(size);
        return createNewBatch();
    }

    @LogMethod(quiet = true)
    public TokenListPage createNewBatch()
    {
        clickButton("Submit");
        return new TokenListPage(getWrapper());
    }

    @LogMethod(quiet = true)
    public void cancelNewBatch()
    {
        elementCache().cancelButton.click();
    }

    @Override
    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }

    protected class ElementCache extends Window.Elements
    {
        Input otherCountInput = Input(Locator.name("otherCount"), getWrapper().getDriver()).findWhenNeeded(this);
        WebElement submitButton =  new LazyWebElement(Locator.xpath("//div[contains(@class, 'x4-window')]").append(Ext4Helper.Locators.ext4Button("Submit")), this);
        WebElement cancelButton =  new LazyWebElement(Ext4Helper.Locators.ext4Button("Cancel"), this);

        RadioButton findCountChoice(String size)
        {
            return RadioButton.RadioButton().withLabel(size).findWhenNeeded(this);
        }
    }

    public static class Locators
    {
        public static final Locator.XPathLocator enabledSubmitButton = Ext4Helper.Locators.ext4Button("Submit").enabled();
        public static final Locator.XPathLocator enabledCancelButton = Ext4Helper.Locators.ext4Button("Cancel").enabled();
    }
}
