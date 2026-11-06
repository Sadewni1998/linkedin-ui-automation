package lk.ac.kdu.sqa.tests;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
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
    public void verifyValidSearchAndOpenProfile() {
        Properties credentials = loadCredentials();
        String email = credentials.getProperty("linkedin.email");
        String password = credentials.getProperty("linkedin.password");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        new WebDriverWait(driver, Duration.ofSeconds(45))
            .until(ExpectedConditions.urlContains("/feed"));

        // Valid search for "software testing"
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor("software testing");
        Assert.assertTrue(searchPage.areResultsDisplayed(), "Search results should be visible.");

        // Capture screenshot of valid search results
        captureStepScreenshot("TC04_ValidSearchResultsPage");

        // Click the first person profile from the results
        String profileUrl = searchPage.clickFirstPeopleResult();
        Assert.assertTrue(profileUrl.contains("linkedin.com/in/"), "Expected a person profile link.");
        Assert.assertTrue(driver.getCurrentUrl().contains("linkedin.com/in/"), "Profile page did not load.");

        // Capture screenshot inside the opened profile
        captureStepScreenshot("TC04_ProfilePageOpened");

        System.out.println("TC04 (Valid Search & Open Profile) PASSED successfully.");
    }
}