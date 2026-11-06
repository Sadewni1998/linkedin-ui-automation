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

public class SearchTests extends BaseTest {

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
    public void verifySearchQueryExecutionAndResults() {
        Properties credentials = loadCredentials();
        String email = credentials.getProperty("linkedin.email");
        String password = credentials.getProperty("linkedin.password");

        // 1. Login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        new WebDriverWait(driver, Duration.ofSeconds(45))
            .until(ExpectedConditions.urlContains("/feed"));

        // 2. Search for "software testing"
        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor("software testing");

        // 3. Verify search box retained the query
        String enteredQuery = searchPage.getEnteredSearchText();
        Assert.assertTrue(enteredQuery.toLowerCase().contains("software testing"), 
            "Search input should retain 'software testing'");

        // 4. Verify search results page is rendered
        boolean resultsVisible = searchPage.areResultsDisplayed();
        Assert.assertTrue(resultsVisible, "Search results should be rendered for 'software testing'");

        // 5. Capture screenshot for TC03
        captureStepScreenshot("TC03_SoftwareTestingSearchResults");

        System.out.println("TC03 (Software Testing Search Execution) PASSED successfully.");
    }
}