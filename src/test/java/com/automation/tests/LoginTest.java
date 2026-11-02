package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.FeedPage;
import com.automation.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "Task 2: Successful login with valid credentials")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateTo(config.getProperty("base.url"));

        loginPage.login(config.getProperty("valid.username"), config.getProperty("valid.password"));

        FeedPage feedPage = new FeedPage(driver, wait);
        Assert.assertTrue(feedPage.isLoaded(), "Feed page was not loaded successfully after login.");
    }

    @Test(priority = 2, description = "Task 2: Negative test with invalid password")
    public void testInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateTo(config.getProperty("base.url"));

        loginPage.login(config.getProperty("valid.username"), "WrongPassword@123");

        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.length() > 0, "No error message was displayed for invalid password.");
    }
}