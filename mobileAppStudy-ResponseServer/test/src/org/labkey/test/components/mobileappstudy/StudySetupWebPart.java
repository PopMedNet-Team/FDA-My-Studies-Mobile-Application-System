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
import org.labkey.test.WebDriverWrapper;
import org.labkey.test.components.BodyWebPart;
import org.labkey.test.components.ext4.Checkbox;
import org.labkey.test.components.ext4.Error;
import org.labkey.test.components.ext4.Message;
import org.labkey.test.components.html.Input;
import org.labkey.test.util.Ext4Helper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.labkey.test.components.html.Input.Input;

public class StudySetupWebPart extends BodyWebPart<StudySetupWebPart.ElementCache>
{
    public StudySetupWebPart(WebDriver driver)
    {
        super(driver, "Study Setup");
    }

    public StudySetupWebPart checkResponseCollection()
    {
        elementCache().collectionCheckbox.check();
        return this;
    }

    public StudySetupWebPart uncheckResponseCollection()
    {
        elementCache().collectionCheckbox.uncheck();
        return this;
    }

    public boolean isResponseCollectionChecked()
    {
        return elementCache().collectionCheckbox.isChecked();
    }

    public boolean isCollectionCheckboxVisible()
    {
        return elementCache().collectionCheckbox.isDisplayed();
    }

    public boolean isCollectionCheckboxEnabled()
    {
        return elementCache().collectionCheckbox.isEnabled();
    }

    public String getPrompt()
    {
        return elementCache().shortNamePrompt.getText();
    }

    public String getShortName()
    {
        return elementCache().shortNameField.get();
    }

    public StudySetupWebPart setShortName(String shortName)
    {
        return setField(elementCache().shortNameField, shortName);
    }

    public boolean isShortNameVisible()
    {
        return elementCache().shortNameField.getComponentElement().isDisplayed();
    }

    public boolean isSubmitEnabled()
    {
        WebDriverWrapper.sleep(500);
        String classValue = elementCache().submitButton.getAttribute("class");
        return !classValue.toLowerCase().contains("x4-btn-disabled");
    }

    public void clickSubmit()
    {
        submit();
        getWrapper().shortWait().until(ExpectedConditions.visibilityOf(elementCache().successMessage));
    }

    public Error submitAndExpectError()
    {
        submit();
        return new Error(getDriver());
    }

    private void submit()
    {
        if (!isSubmitEnabled())
            throw new IllegalStateException("Submit button not enabled");

        boolean collectionEnabled = isResponseCollectionChecked();
        elementCache().submitButton.click();
        if (!collectionEnabled)
        {
            acceptCollectionWarning();
        }
    }

    public void acceptCollectionWarning()
    {
        ResponseCollectionDialog warning = new ResponseCollectionDialog(getDriver());
        warning.clickOk();
    }

    private StudySetupWebPart setField(Input field, String value)
    {
        field.set(value);
        field.blur();
        getWrapper().waitForFormElementToEqual(field.getComponentElement(), value);
        WebDriverWrapper.sleep(500);
        return this;
    }

    @Override
    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }

    public class ElementCache extends BodyWebPart.ElementCache
    {
        final WebElement submitButton = Ext4Helper.Locators.ext4Button("Submit").findWhenNeeded(this);
        final WebElement shortNamePrompt = Locator.tagWithClass("div", "studysetup-prompt").findWhenNeeded(this);
        final Input shortNameField = Input(Locator.input("studyId"), getDriver()).findWhenNeeded(this);
        final Checkbox collectionCheckbox = Checkbox.Ext4Checkbox().locatedBy(Locator.id("collectionEnabled-inputEl")).findWhenNeeded(this);
        final Checkbox forwardingCheckbox = Checkbox.Ext4Checkbox().locatedBy(Locator.id("forwardingEnabled-inputEl")).findWhenNeeded(this);
        final Input urlField = Input(Locator.input("url"), getDriver()).findWhenNeeded(this);
        final Input userField = Input(Locator.input("username"), getDriver()).findWhenNeeded(this);
        final Input passwordField = Input(Locator.input("password"), getDriver()).findWhenNeeded(this);

        final WebElement successMessage = Locator.tagWithText("label", "Configuration Saved").findWhenNeeded(this);
    }

    public class ResponseCollectionDialog extends Message
    {
        public static final String WARNING_TITLE = "Response collection stopped";

        public ResponseCollectionDialog(WebDriver wd)
        {
            super(WARNING_TITLE, wd);
        }

        public void clickCancel()
        {
            clickButton("Cancel", true);
        }

        public void clickOk()
        {
            clickButton("OK", true);
        }
    }
}
