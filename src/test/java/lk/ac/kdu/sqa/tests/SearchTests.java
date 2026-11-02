package lk.ac.kdu.sqa.tests;

import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.StaleElementReferenceException;
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
            throw new AssertionError("Could not load LinkedIn credentials.", e);
        }
        return credentials;
    }

    @Test
    public void verifySearchResultsDisplayed() {
        Properties credentials = loadCredentials();
        String email = credentials.getProperty("linkedin.email");
        String password = credentials.getProperty("linkedin.password");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        By feedSearchBox = By.cssSelector(
            "input[placeholder*='Search'], " +
            "input[aria-label*='Search']"
        );
        boolean loggedIn = new WebDriverWait(driver, Duration.ofMinutes(3))
            .until(d -> {
                String url = d.getCurrentUrl();
                if (url.contains("/login") || url.contains("/checkpoint/")) {
                    return false;
                }
                if (!url.contains("/feed")) {
                    return false;
                }
                for (WebElement element : d.findElements(feedSearchBox)) {
                    try {
                        if (element.isDisplayed() && element.isEnabled()) {
                            return true;
                        }
                    } catch (StaleElementReferenceException e) {
                    }
                }
                return false;
            });
        Assert.assertTrue(loggedIn, "The LinkedIn feed was not reached.");
        System.out.println("Login verified. Starting search.");

        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor("software testing");

        Assert.assertTrue(driver.getCurrentUrl().contains("/search/results/"), "Search results URL not found.");
        Assert.assertTrue(searchPage.areResultsDisplayed(), "No visible search result was found.");
        System.out.println("SEARCH TEST PASSED.");
    }
}