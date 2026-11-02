package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.FeedPage;
import com.automation.pages.LoginPage;
import com.automation.pages.SearchResultsPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SearchTest extends BaseTest {

    @Test(priority = 1, description = "Task 3: Search for a term and verify results page")
    public void testGlobalSearch() {
        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateTo(config.getProperty("base.url"));
        loginPage.login(config.getProperty("valid.username"), config.getProperty("valid.password"));

        FeedPage feedPage = new FeedPage(driver, wait);
        Assert.assertTrue(feedPage.isLoaded(), "Login failed before performing search.");

        String query = config.getProperty("search.term");
        feedPage.searchFor(query);

        SearchResultsPage resultsPage = new SearchResultsPage(driver, wait);
        Assert.assertTrue(resultsPage.isResultsPageDisplayed(), "Search results page failed to load.");
        Assert.assertTrue(driver.getCurrentUrl().contains("keywords="), "Search URL does not contain keyword parameter.");
    }

    @Test(priority = 2, description = "Task 4: Click the first search result and verify destination")
    public void testClickSearchResult() {
        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.navigateTo(config.getProperty("base.url"));
        loginPage.login(config.getProperty("valid.username"), config.getProperty("valid.password"));

        FeedPage feedPage = new FeedPage(driver, wait);
        feedPage.searchFor(config.getProperty("search.term"));

        SearchResultsPage resultsPage = new SearchResultsPage(driver, wait);
        Assert.assertTrue(resultsPage.isResultsPageDisplayed(), "Search results page failed to load.");

        resultsPage.clickFirstResult();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/search/results/")));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            currentUrl.contains("/in/") || currentUrl.contains("/company/") || currentUrl.contains("/school/"),
            "Destination page is not a profile/company/school page: " + currentUrl
        );
    }
}