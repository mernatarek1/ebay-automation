package com.ebay.automation.tests;

import com.ebay.automation.listeners.RetryAnalyzer;
import com.ebay.automation.pages.HomePage;
import com.ebay.automation.pages.SearchResultsPage;
import com.ebay.automation.utils.ExtentTestManager;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EbaySearchTest extends BaseTest {

    @Test(description = "Search for a car on eBay and filter by Transmission = Manual",
            retryAnalyzer = RetryAnalyzer.class)
    public void searchAndFilterByTransmission() {

        // Step 1: Navigate to eBay and validate the home page
        ExtentTestManager.info("Navigate to " + testData.getBaseUrl());
        HomePage homePage = new HomePage(driver).open(testData.getBaseUrl());

        Assert.assertTrue(
                homePage.getPageTitle().contains(testData.getHomePageTitleContains()),
                "Page title should contain '" + testData.getHomePageTitleContains()
                        + "' but was: " + homePage.getPageTitle());
        Assert.assertTrue(homePage.isSearchBoxDisplayed(),
                "Search box should be displayed on the home page");
        ExtentTestManager.info("Home page validated. Title: " + homePage.getPageTitle());

        // Step 2: Search for the term
        ExtentTestManager.info("Search for '" + testData.getSearchTerm() + "'");
        SearchResultsPage resultsPage = homePage.searchFor(testData.getSearchTerm());

        // Step 3: Set the ship-to location so that the vehicle filters are available
        ExtentTestManager.info("Set ship-to location: ZIP " + testData.getShipToZip()
                + ", country id " + testData.getShipToCountryId());
        resultsPage.setShipToLocation(testData.getShipToZip(), testData.getShipToCountryId());

        // Step 4: Validate the results
        Assert.assertTrue(resultsPage.isResultsHeadingDisplayed(),
                "Results heading should be displayed");
        Assert.assertTrue(
                resultsPage.getResultsHeadingText().toLowerCase()
                        .contains(testData.getSearchTerm().toLowerCase()),
                "Results heading should mention the search term. Actual: "
                        + resultsPage.getResultsHeadingText());

        int resultsCount = resultsPage.getResultsCount();
        Assert.assertTrue(resultsCount > 0, "Search should return at least one result");
        Assert.assertTrue(resultsPage.getDisplayedItemsCount() > 0,
                "At least one result item should be displayed");

        // Step 5: Log the number of results
        ExtentTestManager.info("Number of search results for '" + testData.getSearchTerm()
                + "': " + resultsCount);

        // Step 6: Apply the filter from the left side
        Assert.assertTrue(resultsPage.isFilterAvailable(testData.getFilterName()),
                "Filter '" + testData.getFilterName() + "' should be available");

        ExtentTestManager.info("Apply filter " + testData.getFilterName() + " -> " + testData.getFilterValue());
        resultsPage.applyFilter(testData.getFilterName(), testData.getFilterValue());

        // Step 7: Validate the filter was applied
        Assert.assertTrue(
                resultsPage.isFilterApplied(testData.getFilterName(), testData.getFilterValue()),
                "Filter " + testData.getFilterName() + " = " + testData.getFilterValue()
                        + " should be applied");

        int filteredCount = resultsPage.getResultsCount();
        ExtentTestManager.info("Number of results after filter "
                + testData.getFilterName() + " = " + testData.getFilterValue()
                + ": " + filteredCount);

        Assert.assertTrue(filteredCount <= resultsCount,
                "Filtered results (" + filteredCount + ") should not exceed the original results ("
                        + resultsCount + ")");
    }
}
