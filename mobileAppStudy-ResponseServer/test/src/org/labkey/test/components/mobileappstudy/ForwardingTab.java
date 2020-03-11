package org.labkey.test.components.mobileappstudy;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.WebTestHelper;
import org.labkey.test.components.html.RadioButton;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.pages.admin.FolderManagementTab;
import org.labkey.test.selenium.LazyWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ForwardingTab extends LabKeyPage<ForwardingTab.ElementCache> implements FolderManagementTab
{
    private WebDriver _driver;

    public ForwardingTab() {}

    public ForwardingTab(BaseWebDriverTest driver)
    {
        super(driver);
    }

    public static ForwardingTab beginAt(BaseWebDriverTest driver)
    {
        return beginAt(driver, driver.getCurrentContainerPath());
    }

    public static ForwardingTab beginAt(BaseWebDriverTest driver, String containerPath)
    {
        driver.beginAt(WebTestHelper.buildURL("mobileappstudy", containerPath, "forwardingSettings"));
        return new ForwardingTab(driver);
    }

    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }

    @Override
    public void setDriver(WebDriver driver)
    {
        _driver = driver;
    }

    @Override
    public String getTabId()
    {
        return "forwarding";
    }

    public void setOauthCredentials(String tokenURL, String tokenField, String tokenHeader, String endpointURL)
    {
        //Enable oauth
        ElementCache ec = newElementCache();
        ec.oauthRadioButton.check();

        OAuthWebPart webPart = ec.oAuthWebPart;
        webPart.setTokenUrl(tokenURL);
        webPart.setTokenField(tokenField);
        webPart.setTokenHeader(tokenHeader);
        webPart.setEndpointURL(endpointURL);
    }

    public void setBasicAuthCredentials(String username, String password, String endpointURL)
    {
        //Enable oauth
        ElementCache ec = newElementCache();
        ec.basicRadioButton.check();

        BasicAuthWebPart webPart = ec.basicAuthWebPart;
        webPart.setUsername(username);
        webPart.setPassword(password);
        webPart.setBasicURL(endpointURL);
    }

    public void disableForwarding()
    {
        newElementCache().disableRadioButton.check();
    }

    public void submit()
    {
        shortWait().until(ExpectedConditions.elementToBeClickable(Locator.button("Submit")));
        clickButton("Submit");
    }

    protected class ElementCache extends LabKeyPage.ElementCache
    {
        RadioButton disableRadioButton = new RadioButton.RadioButtonFinder().withNameAndValue("forwardingType", "Disabled").findWhenNeeded(this);
        RadioButton basicRadioButton = new RadioButton.RadioButtonFinder().withNameAndValue("forwardingType", "Basic").findWhenNeeded(this);
        RadioButton oauthRadioButton = new RadioButton.RadioButtonFinder().withNameAndValue("forwardingType", "OAuth").findWhenNeeded(this);

        OAuthWebPart oAuthWebPart = new OAuthWebPart(getDriver(), new LazyWebElement(Locator.id("oauthPanel"), this));
        BasicAuthWebPart basicAuthWebPart = new BasicAuthWebPart(getDriver(), new LazyWebElement(Locator.id("basicAuthPanel"), this));
    }
}