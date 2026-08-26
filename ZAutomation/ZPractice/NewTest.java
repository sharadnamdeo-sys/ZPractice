package ZAutomation.ZPractice;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;

public class NewTest {
	
	
	WebDriver driver;
    String baseUrl = "https://testautomationpractice.blogspot.com/";

  @Test
  public void f() {
	  driver.get(baseUrl);
	  
  }
  @BeforeMethod
  public void setUp() {
      System.setProperty("webdriver.chrome.driver", "C:\\Users\\Admin\\Downloads\\chromedriver_win32 (1)\\chromedriver.exe");
      driver = new ChromeDriver();
      driver.manage().window().maximize();
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
      driver.get(baseUrl);
  }

  @AfterMethod
  public void tearDown() {
      if (driver != null) {
          driver.quit();
      }
  }


}
