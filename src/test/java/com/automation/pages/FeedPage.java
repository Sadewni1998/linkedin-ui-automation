package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FeedPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By globalNav = By.id("global-nav");
    private By searchBox = By.xpath("//input[contains(@class,'search-global-typeahead__input')]");

    public FeedPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(globalNav)).isDisplayed()
                && driver.getCurrentUrl().contains("/feed");
    }

    public void searchFor(String query) {
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(searchBox));
        searchInput.click();
        searchInput.clear();
        searchInput.sendKeys(query);
        searchInput.sendKeys(Keys.ENTER);
    }
}