package org.labkey.test.components.mobileappstudy;

import org.labkey.test.Locator;
import org.labkey.test.components.BodyWebPart;
import org.labkey.test.selenium.LazyWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.labkey.test.components.html.Input;

public class OAuthWebPart extends BodyWebPart<OAuthWebPart.ElementCache>
{
    public OAuthWebPart(WebDriver driver, WebElement webPartElement)
    {
        super(driver, webPartElement);
    }

    public void setTokenUrl(String tokenUrl)
    {
        elementCache().tokenUrlInput.setValue(tokenUrl);
    }

    public void setTokenField(String tokenField)
    {
        elementCache().tokenFieldInput.setValue(tokenField);
    }

    public void setTokenHeader(String header)
    {
        elementCache().tokenHeaderInput.setValue(header);
    }

    public void setEndpointURL(String endpointURL)
    {
        elementCache().endpointUrlInput.setValue(endpointURL);
    }

    @Override
    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }

    public class ElementCache extends BodyWebPart.ElementCache
    {
        Input tokenUrlInput = new Input(new LazyWebElement(Locator.input("tokenRequestURL"), this), getDriver());
        Input tokenFieldInput = new Input(new LazyWebElement(Locator.input("tokenField"), this), getDriver());
        Input tokenHeaderInput = new Input(new LazyWebElement(Locator.input("header"), this), getDriver());
        Input endpointUrlInput = new Input(new LazyWebElement(Locator.input("oauthURL"), this), getDriver());
    }
}
