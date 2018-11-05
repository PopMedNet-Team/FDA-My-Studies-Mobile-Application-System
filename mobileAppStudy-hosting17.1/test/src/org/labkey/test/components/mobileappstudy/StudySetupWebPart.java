/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.components.mobileappstudy;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.WebDriverWrapper;
import org.labkey.test.components.WebPart;
import org.labkey.test.components.ext4.Window;
import org.labkey.test.selenium.LazyWebElement;
import org.labkey.test.util.Ext4Helper;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StudySetupWebPart extends WebPart<StudySetupWebPart.ElementCache>
{
    private final Ext4Helper _ext4Helper = new Ext4Helper(getWrapper());
    private final static String SAVE_CONFIRMATION = "Configuration Saved";
    public StudySetupWebPart(BaseWebDriverTest test)
    {
        super(test.getWrappedDriver(), StudySetupWebPart.Locators.dataRegionLocator.findElement(test.getDriver()));
        waitForReady();
    }

    public void checkResponseCollection()
    {
        _ext4Helper.checkCheckbox(Locators.collectionEnabledCheckbox);
    }

    public void uncheckResponseCollection()
    {
        _ext4Helper.uncheckCheckbox(Locators.collectionEnabledCheckbox);
    }

    public boolean isResponseCollectionChecked()
    {
        return _ext4Helper.isChecked(Locators.collectionEnabledCheckbox);
    }

    public boolean isCollectionCheckboxVisible()
    {
        return elementCache().collectionCheckbox.isDisplayed();
    }

    public boolean isCollectionCheckboxEnabled()
    {
        return elementCache().collectionCheckbox.isEnabled();
    }

    @Override
    public WebElement getComponentElement()
    {
        try
        {
            return StudySetupWebPart.Locators.dataRegionLocator.findElement(getDriver());
        }
        catch(NoSuchElementException nsee)
        {
            return null;
        }
    }

    @Override
    protected void waitForReady()
    {
        getWrapper().waitForElement(StudySetupWebPart.Locators.dataRegionLocator);
    }

    @Override
    public String getTitle()
    {
        return getComponentElement().findElement(By.tagName("th")).getText();
    }

    public String getPrompt()
    {
        return elementCache().shortNamePrompt.getText();
    }

    public String getShortName()
    {
        String textValue;

        if(isShortNameVisible())
            textValue = getWrapper().getFormElement(elementCache().shortNameField);
        else
            textValue = "";

        return textValue;
    }

    public StudySetupWebPart setShortName(String shortName)
    {
        getWrapper().setFormElement(elementCache().shortNameField, shortName);
        getWrapper().waitForFormElementToEqual(elementCache().shortNameField, shortName);
        WebDriverWrapper.sleep(500);
        return this;
    }

    public boolean isShortNameVisible()
    {
        return getWrapper().isElementPresent(Locators.shortNameField) && elementCache().shortNameField.isDisplayed();
    }

    public boolean isSubmitEnabled()
    {
        WebDriverWrapper.sleep(500);
        String classValue = elementCache().submitButton.getAttribute("class");
        return !classValue.toLowerCase().contains("x4-btn-disabled");
    }

    public void clickSubmit()
    {
        WebDriverWrapper.sleep(500);

        boolean collectionEnabled = isResponseCollectionChecked();
        elementCache().submitButton.click();
        if (!collectionEnabled)
        {
            acceptCollectionWarning();
        }

        getWrapper().waitForText(SAVE_CONFIRMATION);
    }

    protected ElementCache elementCache()
    {
        return new ElementCache();
    }

    public void acceptCollectionWarning()
    {
        ResponseCollectionDialog warning = new ResponseCollectionDialog(getDriver());
        assertNotNull("Warning dialog not found", warning);
        warning.clickOk();
    }

    public class ElementCache extends WebPart.ElementCache
    {
        WebElement submitButton = new LazyWebElement(Locators.submitButton, getDriver());
        WebElement shortNameField = new LazyWebElement(Locators.shortNameField, getDriver());
        WebElement shortNamePrompt = new LazyWebElement(Locators.shortNamePrompt, getDriver());
        WebElement collectionCheckbox = new LazyWebElement(Locators.collectionEnabledCheckbox, getDriver());
    }

    public static class Locators
    {
        protected static final Locator.XPathLocator dataRegionLocator = Locator.xpath("//table[tbody/tr/th[@title='Study Setup']]");
        protected static final Locator.XPathLocator shortNamePrompt = dataRegionLocator.append("//div[@id='labkey-mobileappstudy-studysetup']//span/div/div/div[contains(@class, 'x4-panel-body')]/span/div");
        protected static final Locator.XPathLocator shortNameField = Locator.input("studyId");
        protected static final Locator.XPathLocator submitButton = dataRegionLocator.append(Ext4Helper.Locators.ext4Button("Submit"));
        protected static final Locator.XPathLocator collectionEnabledCheckbox = Locator.tag("input").withAttribute("type", "button").withAttributeContaining("id", "collectionEnabled");
    }

    public class ResponseCollectionDialog extends Window
    {
        public static final String WARNING_TITLE = "Response collection stopped";

        public ResponseCollectionDialog(WebDriver wd)
        {
            super(WARNING_TITLE, wd);
            assertTrue("Dialog's title is not as expected", WARNING_TITLE.equals(this.getTitle()));
        }

        public void clickCancel()
        {
            clickButton("Cancel", 0);
            waitForClose();
        }

        public void clickOk()
        {
            clickButton("OK", 0);

            //TODO: Issue 28463: Ext.Msg reuses the WebElement for the dialog so don't wait for close, as may already be reopened
            // waitForClose();     //
            getWrapper().longWait();
        }
    }
}
