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

    private final By searchBox = By.cssSelector(
        "input[placeholder*='Search'], " +
        "input[aria-label*='Search'], " +
        ".search-global-typeahead__input"
    );

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

    // Clicks the first visible person profile result and waits for profile page to render
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

        // Wait until navigation to person's profile URL completes
        wait.until(ExpectedConditions.urlContains("linkedin.com/in/"));
        System.out.println("Opened profile successfully: " + driver.getCurrentUrl());

        // Wait a few seconds so the full profile page with details is clearly visible on screen
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}

        return expectedUrl;
    }
}