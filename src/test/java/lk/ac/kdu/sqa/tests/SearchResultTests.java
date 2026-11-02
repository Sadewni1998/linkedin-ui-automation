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

        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSignIn();

        By feedSearchBox = By.cssSelector(
            "input[placeholder*='Search'], " +
            "input[aria-label*='Search'], " +
            "input[role='combobox']"
        );
        new WebDriverWait(driver, Duration.ofMinutes(3))
            .until(d -> {
                if (!d.getCurrentUrl().contains("/feed")) {
                    return false;
                }
                for (WebElement element : d.findElements(feedSearchBox)) {
                    try {
                        if (element.isDisplayed()) {
                            return true;
                        }
                    } catch (StaleElementReferenceException e) {
                    }
                }
                return false;
            });

        SearchPage searchPage = new SearchPage(driver);
        searchPage.searchFor("software testing");
        Assert.assertTrue(searchPage.areResultsDisplayed(), "Search results should be visible.");

        String expectedUrl = searchPage.clickFirstPeopleResult();
        Assert.assertTrue(expectedUrl.contains("linkedin.com/in/"), "The selected result should be a profile.");
        Assert.assertTrue(driver.getCurrentUrl().contains("linkedin.com/in/"), "The profile did not open.");
        System.out.println("SEARCH RESULT TEST PASSED.");
    }
}