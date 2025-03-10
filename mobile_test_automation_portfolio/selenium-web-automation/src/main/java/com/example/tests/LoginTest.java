import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.ITestResult;
import org.testng.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;

import com.example.pages.*;
import com.example.utils.SecurityUtils;

import java.io.File;
import java.time.Duration;
import java.util.*;

public class LoginTest extends BaseTest {

    private WebDriver driver;
    private LoginPage loginPage;
    private HomePage homePage;
    private WebDriverWait wait;
    private long testStartTime;

    @BeforeClass
    public void setupTestSuite() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        System.setProperty("webdriver.gecko.driver", "path/to/geckodriver");
        System.setProperty("webdriver.edge.driver", "path/to/msedgedriver");
    }

    @BeforeMethod
    public void setupTest() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        testStartTime = System.currentTimeMillis();
        driver.get("https://example.com/login");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName());
        }
        System.out.println("Test execution time: " + (System.currentTimeMillis() - testStartTime) + " ms");
        if (driver != null) driver.quit();
    }

    @Test(priority = 1)
    public void testSuccessfulLogin() {
        loginPage.login("valid_user", "valid_password");
        Assert.assertTrue(homePage.isHomePageLoaded(), "Home page wasn't loaded after login");

        Cookie sessionCookie = driver.manage().getCookieNamed("session_id");
        Assert.assertNotNull(sessionCookie, "Session cookie not created");
        Assert.assertTrue(sessionCookie.getExpiry().after(new Date()), "Session cookie expired");
    }

    @Test(dataProvider = "invalidLoginData", priority = 2)
    public void testInvalidLogin(String username, String password, String expectedErrorMessage) {
        loginPage.login(username, password);
        Assert.assertEquals(loginPage.getErrorMessage(), expectedErrorMessage);
        Assert.assertTrue(loginPage.isLoginPageDisplayed());
    }

    @Test(priority = 3)
    public void testSqlInjectionLogin() {
        String injection = "' OR '1'='1";
        loginPage.login(injection, injection);
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "SQL Injection vulnerability detected!");
    }

    @Test(priority = 4)
    public void testRememberMeFunctionality() {
        loginPage.loginWithRememberMe("remember_user", "valid_password");
        Assert.assertTrue(homePage.isHomePageLoaded());

        List<Cookie> cookies = new ArrayList<>(driver.manage().getCookies());
        driver.quit();
        driver = new ChromeDriver();

        driver.get("https://example.com");
        for (Cookie cookie : cookies) {
            driver.manage().addCookie(cookie);
        }

        driver.get("https://example.com/dashboard");
        homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isHomePageLoaded(), "Remember me failed");
    }

    @Test(priority = 5)
    public void testMultiFactorAuthentication() {
        loginPage.login("mfa_user", "valid_password");
        Assert.assertTrue(loginPage.isMfaScreenDisplayed());

        loginPage.enterMfaCode("123456");
        Assert.assertTrue(homePage.isHomePageLoaded(), "MFA login failed");
    }

    @Test(priority = 6)
    public void testSessionTimeoutHandling() {
        loginPage.login("valid_user", "valid_password");
        Assert.assertTrue(homePage.isHomePageLoaded());

        Cookie sessionCookie = driver.manage().getCookieNamed("session_id");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, -10);
        Cookie expiredCookie = new Cookie.Builder(sessionCookie.getName(), sessionCookie.getValue())
                .domain(sessionCookie.getDomain()).path(sessionCookie.getPath())
                .expiresOn(cal.getTime()).build();

        driver.manage().deleteCookieNamed("session_id");
        driver.manage().addCookie(expiredCookie);

        driver.get("https://example.com/profile");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    private void takeScreenshot(String testName) {
        try {
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(source, new File("./screenshots/" + testName + ".png"));
        } catch (Exception e) {
            System.out.println("Screenshot error: " + e.getMessage());
        }
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] getInvalidLoginData() {
        return new Object[][]{
                {"invalid_user", "invalid_pass", "Invalid username or password"},
                {"", "password", "Username is required"},
                {"username", "", "Password is required"}
        };
    }
}
