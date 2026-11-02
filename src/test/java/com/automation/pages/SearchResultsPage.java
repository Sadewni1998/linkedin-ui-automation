package com.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResultsPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By firstResultLink = By.xpath("(//li[contains(@class,'reusable-search__result-container')]//span[@dir='ltr']/ancestor::a)[1]");
    private By searchContainer = By.xpath("//div[contains(@class,'search-results-container')] | //main");

    public SearchResultsPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public boolean isResultsPageDisplayed() {
        wait.until(ExpectedConditions.urlContains("/search/results/"));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(searchContainer)).isDisplayed();
    }

    public String clickFirstResult() {
        WebElement link = wait.until(ExpectedConditions.elementToBeClickable(firstResultLink));
        String linkText = link.getText().trim();
        link.click();
        return linkText;
    }
}