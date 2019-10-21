package org.labkey.test.components.mobileappstudy;

import org.labkey.test.Locator;
import org.labkey.test.components.BodyWebPart;
import org.labkey.test.components.html.Input;
import org.labkey.test.selenium.LazyWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasicAuthWebPart extends BodyWebPart<BasicAuthWebPart.ElementCache>
{
    public BasicAuthWebPart(WebDriver driver, WebElement webPartElement)
    {
        super(driver, webPartElement);
    }
    
    public void setUsername(String username)
    {
        elementCache().username.setValue(username);
    }

    public void setPassword(String password)
    {
        elementCache().password.setValue(password);
    }

    public void setBasicURL(String basicURL)
    {
        elementCache().basicURL.setValue(basicURL);
    }

    @Override
    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }
    
    public class ElementCache extends BodyWebPart.ElementCache
    {
        Input username = new Input(new LazyWebElement(Locator.input("username"), this), getDriver());
        Input password = new Input(new LazyWebElement(Locator.input("password"), this), getDriver());
        Input basicURL = new Input(new LazyWebElement(Locator.input("basicURL"), this), getDriver());
    }
}
