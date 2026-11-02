package lk.ac.kdu.sqa.tests;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import lk.ac.kdu.sqa.base.BaseTest;
import lk.ac.kdu.sqa.pages.LoginPage;
import org.testng.SkipException;

public class LoginTests extends BaseTest {

    private Properties loadCredentials() {
        Properties credentials = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/credentials.properties")) {
            Assert.assertNotNull(input, "credentials.properties was not found");
            credentials.load(input);
        } catch (Exception e) {
            throw new AssertionError("Could not load LinkedIn credentials.", e);
        }
        Assert.assertNotNull(credentials.getProperty("linkedin.email"), "LinkedIn email is missing.");
        Assert.assertNotNull(credentials.getProperty("linkedin.password"), "LinkedIn password is missing.");
        return credentials;
    }

    // POSITIVE TEST: Login with valid credentials
    @Test(priority = 1)
    public void verifySuccessfulLogin() {
        Properties credentials = loadCredentials();
        String email = credentials.getProperty("linkedin.email");
        String password = credentials.getProperty("linkedin.password");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);

        // 1. Capture screenshot right after typing valid credentials!
        captureStepScreenshot("ValidCredentialsEntered");

        loginPage.clickSignIn();
        System.out.println("Sign-in submitted. Waiting for feed page...");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(45));
            boolean onFeed = wait.until(ExpectedConditions.urlContains("/feed"));
            
            Assert.assertTrue(onFeed, "Browser did not navigate to /feed.");
            System.out.println("POSITIVE LOGIN TEST PASSED. Logged-in URL: " + driver.getCurrentUrl());
            pauseForInspection();
        } catch (TimeoutException e) {
            Assert.fail("Login could not be verified within 45s. Current URL: " + driver.getCurrentUrl(), e);
        }
    }

    // NEGATIVE TEST: Login with an incorrect password
    @Test(priority = 2)
    public void verifyInvalidPasswordShowsError() {
        Properties credentials = loadCredentials();
        String email = credentials.getProperty("linkedin.email");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword("WrongPasswordForTestOnly_9182!");

        // 2. Capture screenshot right after typing invalid credentials!
        captureStepScreenshot("InvalidCredentialsEntered");

        loginPage.clickSignIn();
        System.out.println("Negative login test submitted.");

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            String result = wait.until(d -> {
                try {
                    String url = d.getCurrentUrl();
                    if (url.contains("/checkpoint/")) {
                        return "CHECKPOINT";
                    }
                    String pageText = d.findElement(By.tagName("body")).getText().toLowerCase();
                    for (WebElement message : d.findElements(By.cssSelector("[role='alert'], .form__label--error, .alert-content"))) {
                        if (message.isDisplayed()) {
                            pageText += " " + message.getText().toLowerCase();
                        }
                    }
                    if (pageText.contains("wrong email or password")
                            || pageText.contains("incorrect email or password")
                            || pageText.contains("incorrect username or password")
                            || pageText.contains("password you entered is incorrect")
                            || pageText.contains("your password was incorrect")
                            || pageText.contains("email or password is incorrect")) {
                        return "ERROR";
                    }
                } catch (Exception ignored) {
                }
                return null;
            });

            // 3. Capture screenshot of the error banner when it appears!
            captureStepScreenshot("InvalidPasswordErrorMessage");

            if ("CHECKPOINT".equals(result)) {
                throw new SkipException("LinkedIn displayed a security checkpoint. Test skipped.");
            }

            Assert.assertEquals(result, "ERROR", "Expected error message not found.");
            System.out.println("NEGATIVE LOGIN TEST PASSED.");
            pauseForInspection();
        } catch (TimeoutException e) {
            Assert.fail("Expected wrong-password error was not found within 20s. Current URL: " + driver.getCurrentUrl(), e);
        }
    }

    private void pauseForInspection() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}