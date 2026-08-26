package PageClasses;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class Homepage {

    private final WebDriver driver;

    @FindBy(id = "name")
    private WebElement name;

    @FindBy(id = "email")
    private WebElement email;

    @FindBy(id = "phone")
    private WebElement phone;

    @FindBy(id = "textarea")
    private WebElement address;

    @FindBy(id = "male")
    private WebElement maleGender;

    @FindBy(id = "sunday")
    private WebElement sunday;

    @FindBy(id = "country")
    private WebElement country;

    @FindBy(id = "colors")
    private WebElement colors;

    public Homepage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isPageLoaded() {
        return driver.getTitle().contains("Automation");
    }

    public boolean isNameDisplayed() {
        return name.isDisplayed();
    }

    public void enterName(String value) {
        name.sendKeys(value);
    }

    public void enterEmail(String value) {
        email.sendKeys(value);
    }

    public void enterPhone(String value) {
        phone.sendKeys(value);
    }

    public void enterAddress(String value) {
        address.sendKeys(value);
    }

    public void selectMaleGender() {
        maleGender.click();
    }

    public void selectSunday() {
        sunday.click();
    }

    public void selectCountry(String visibleText) {
        new Select(country).selectByVisibleText(visibleText);
    }

    public void selectColor(String visibleText) {
        new Select(colors).selectByVisibleText(visibleText);
    }
}
