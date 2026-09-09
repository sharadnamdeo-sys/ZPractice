package TestClasses;

import BasePackage.Baseclass;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AutoTest extends Baseclass {

    @Test
    public void homePageLoadsSuccessfully() {
        PageClasses.Homepage home = getHomePage();
        Assert.assertTrue(home.isPageLoaded(), "Home page did not load");
        Assert.assertTrue(home.isNameDisplayed(), "Name field is not visible");
    }

    @Test
    public void formSubmissionHappyPath() {
        PageClasses.Homepage home = getHomePage();

        home.enterName("John");
        home.enterEmail("john@example.com");
        home.enterPhone("9876543210");
        home.enterAddress("123 Main Street");
        home.selectMaleGender();
        home.selectSunday();
        home.selectCountry("India");
        home.selectColor("Red");

        Assert.assertTrue(home.isPageLoaded(), "Page should still be available after valid form entry");
    }

    @Test
    public void multipleSelectionsHappyPath() {
        PageClasses.Homepage home = getHomePage();

        home.enterName("Alice");
        home.enterEmail("alice@example.com");
        home.enterPhone("9988776655");
        home.enterAddress("45 Park Road");
        home.selectMaleGender();
        home.selectSunday();
        home.selectCountry("Japan");
        home.selectColor("Blue");

        Assert.assertTrue(home.isPageLoaded(), "Page should remain loaded after valid selections");
    }
}
