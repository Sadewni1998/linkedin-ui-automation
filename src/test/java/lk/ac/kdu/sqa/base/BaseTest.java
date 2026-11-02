package lk.ac.kdu.sqa.base;

import java.io.File;
import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
    }

    // Custom helper method to capture screenshot at any specific step
    public void captureStepScreenshot(String stepName) {
        if (driver != null) {
            try {
                File screenshotDir = new File("screenshots");
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs();
                }
                String fileName = stepName + "_" + System.currentTimeMillis() + ".png";
                File destFile = new File(screenshotDir, fileName);

                TakesScreenshot ts = (TakesScreenshot) driver;
                File srcFile = ts.getScreenshotAs(OutputType.FILE);
                FileHandler.copy(srcFile, destFile);

                System.out.println(">>> STEP SCREENSHOT CAPTURED: " + destFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Could not capture step screenshot: " + e.getMessage());
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver != null) {
            try {
                File screenshotDir = new File("screenshots");
                if (!screenshotDir.exists()) {
                    screenshotDir.mkdirs();
                }

                String status;
                if (result.getStatus() == ITestResult.SUCCESS) {
                    status = "PASS";
                } else if (result.getStatus() == ITestResult.FAILURE) {
                    status = "FAIL";
                } else {
                    status = "SKIP";
                }

                String fileName = result.getMethod().getMethodName() 
                        + "_" + status 
                        + "_" + System.currentTimeMillis() 
                        + ".png";

                File destFile = new File(screenshotDir, fileName);
                
                TakesScreenshot ts = (TakesScreenshot) driver;
                File srcFile = ts.getScreenshotAs(OutputType.FILE);
                FileHandler.copy(srcFile, destFile);

                System.out.println(">>> TEST FINISHED SCREENSHOT: " + destFile.getAbsolutePath());

            } catch (IOException e) {
                System.out.println("Could not save screenshot: " + e.getMessage());
            } finally {
                driver.quit();
                driver = null;
            }
        }
    }
}