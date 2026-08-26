package TestClasses;

import BasePackage.Baseclass;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginPage extends Baseclass {

    @Test(groups = "smoke")
    public void loginPageOpens() {
        PageClasses.Homepage loginPage = new PageClasses.Homepage(driver);
        Assert.assertTrue(loginPage.isPageLoaded());
        Assert.assertTrue(loginPage.isNameDisplayed());
        loginPage.enterName("Sharad");
    }

    @Test
    public void HomePageForm() {
        PageClasses.Homepage home = new PageClasses.Homepage(driver);
        Assert.assertTrue(home.isPageLoaded());
        home.enterName("Hello");
        home.enterEmail("sharadnamdeo@gmail.com");
        home.enterPhone("1234567890");
        home.enterAddress("Kolkata");
        home.selectMaleGender();
        home.selectSunday();
        home.selectCountry("Japan");
        home.selectColor("White");
    }


}