package lk.ac.kdu.sqa.pages;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators for LinkedIn global search box
    private final By searchBox = By.cssSelector(
        "input[placeholder*='Search'], " +
        "input[aria-label*='Search'], " +
        ".search-global-typeahead__input"
    );

    // Locators for search result items
    private final By resultCards = By.cssSelector(
        "main a[href*='/in/'], " +
        "main a[href*='/company/'], " +
        "main a[href*='/jobs/search-results/'], " +
        "main a[href*='/feed/update/']"
    );

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // Handles dynamically loaded or re-rendered search box without throwing StaleElementReferenceException
    private WebElement getFreshSearchBox() {
        return wait.until(d -> {
            for (WebElement element : d.findElements(searchBox)) {
                try {
                    if (element.isDisplayed() && element.isEnabled()) {
                        return element;
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
            return null;
        });
    }

    // Types the search term and executes the search
    public void searchFor(String term) {
        String normalizedTerm = term.trim().replaceAll("\\s+", " ");
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean inputReady = false;
        for (int attempt = 1; attempt <= 5; attempt++) {
            try {
                WebElement box = getFreshSearchBox();
                try {
                    box.click();
                } catch (Exception e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", box);
                }

                box.sendKeys(Keys.CONTROL, "a");
                box.sendKeys(Keys.DELETE);
                box.sendKeys(normalizedTerm);

                String currentValue = box.getAttribute("value");
                if (currentValue != null && currentValue.contains(normalizedTerm)) {
                    inputReady = true;
                    box.sendKeys(Keys.ENTER);
                    break;
                }
            } catch (StaleElementReferenceException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }

        if (!inputReady) {
            WebElement box = getFreshSearchBox();
            box.sendKeys(normalizedTerm, Keys.ENTER);
        }

        System.out.println("Search term submitted: " + term);

        // Wait until navigation to search results URL completes
        wait.until(d -> {
            try {
                String url = URLDecoder.decode(d.getCurrentUrl(), StandardCharsets.UTF_8).toLowerCase();
                return url.contains("/search/results/");
            } catch (Exception e) {
                return false;
            }
        });
        System.out.println("Search results loaded. Current URL: " + driver.getCurrentUrl());
    }

    // Verifies whether search result cards are visible
    public boolean areResultsDisplayed() {
        return wait.until(d -> {
            for (WebElement result : d.findElements(resultCards)) {
                try {
                    if (result.isDisplayed()) {
                        return true;
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
            return false;
        });
    }

    // Returns the value currently entered in the search box
    public String getEnteredSearchText() {
        WebElement box = getFreshSearchBox();
        return box.getAttribute("value");
    }

    // Clicks the first person profile link (/in/) from search results and waits for profile page to render
    public String clickFirstPeopleResult() {
        By peopleLinks = By.cssSelector("main a[href*='/in/']");

        WebElement profileLink = wait.until(d -> {
            for (WebElement element : d.findElements(peopleLinks)) {
                try {
                    String href = element.getAttribute("href");
                    if (element.isDisplayed() && element.isEnabled() && href != null && href.contains("/in/")) {
                        return element;
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
            return null;
        });

        String expectedUrl = profileLink.getAttribute("href");
        System.out.println("Clicking on Profile URL: " + expectedUrl);

        try {
            profileLink.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", profileLink);
        }

        // Wait until profile URL is loaded
        wait.until(ExpectedConditions.urlContains("linkedin.com/in/"));
        System.out.println("Opened profile successfully: " + driver.getCurrentUrl());

        // Pause to ensure profile elements are visually loaded for screenshots
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}

        return expectedUrl;
    }
}