package com.example.visual;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import com.github.romankh3.image.comparison.ImageComparison;
import com.github.romankh3.image.comparison.ImageComparisonUtil;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class AiVisualTest {

    private WebDriver driver;
    private static final String BASELINE = "baseline.png";
    private static final String CURRENT = "current.png";
    private static final String DIFF = "diff.png";

    @Before
    public void setUp() {
        // Set the path to your chromedriver executable if needed:
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Hp\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe\\");
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testGoogleHomeVisual() throws IOException {
        driver.get("https://www.google.com");
        // Wait for the page to load
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Take screenshot
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File currentFile = new File(CURRENT);
        org.apache.commons.io.FileUtils.copyFile(screenshot, currentFile);

        File baselineFile = new File(BASELINE);
        if (!baselineFile.exists()) {
            // Save baseline image if it doesn't exist
            org.apache.commons.io.FileUtils.copyFile(currentFile, baselineFile);
            Assert.fail("Baseline image created. Re-run the test to compare.");
        }

        // Compare current screenshot with baseline
        ImageComparison comparison = new ImageComparison(
                ImageIO.read(baselineFile),
                ImageIO.read(currentFile)
        );

        // Save a diff image if differences exist
        comparison.setDestination(new File(DIFF));
        double differencePercent = comparison.compareImages().getDifferencePercent();

        if (differencePercent > 0.0) {
            Assert.fail("Visual differences found! Check diff.png");
        }
    }
}
