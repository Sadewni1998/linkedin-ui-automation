package lk.ac.kdu.sqa.tests;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import lk.ac.kdu.sqa.base.BaseTest;
import lk.ac.kdu.sqa.pages.LoginPage;
import lk.ac.kdu.sqa.pages.SearchPage;

public class SearchResultTests extends BaseTest {

    private Properties loadCredentials() {
        Properties credentials = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/credentials.properties")) {
            Assert.assertNotNull(input, "credentials.properties was not found");
            credentials.load(input);
        } catch (Exception e) {
            throw new AssertionError("Could not load credentials.", e);
        }
        return credentials;
    }

    @Test
    public void verifyFirstPeopleResultOpens() {
        Properties credentials = loadCredentials();
        String email = credentials.getProperty("linkedin.email");
        String password = credentials.getProperty("linkedin.password");

        // 1. Login to LinkedIn
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        // 2. Wait until feed page loads
        new WebDriverWait(driver, Duration.ofSeconds(45))
            .until(ExpectedConditions.urlContains("/feed"));

        // 3. Search for "software testing"
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor("software testing");
        Assert.assertTrue(searchPage.areResultsDisplayed(), "Search results should be visible.");

        // Capture screenshot of search results page
        captureStepScreenshot("SearchResultsPage");

        // 4. Click the first person profile from the results
        String profileUrl = searchPage.clickFirstPeopleResult();
        Assert.assertTrue(profileUrl.contains("linkedin.com/in/"), "Expected a person profile link.");
        Assert.assertTrue(driver.getCurrentUrl().contains("linkedin.com/in/"), "Profile page did not load.");

        // 5. Capture screenshot of the opened Profile page
        captureStepScreenshot("ProfilePageOpened");

        System.out.println("SEARCH RESULT TEST PASSED. Profile Opened: " + driver.getCurrentUrl());
    }
}