package com.ebay.automation.pages;

import com.ebay.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchResultsPage extends BasePage {

    private static final int MAX_LOAD_ATTEMPTS = 3;
    private static final Duration RESULTS_WAIT = Duration.ofSeconds(15);

    // ---------- Locators ----------
    private static final By RESULTS_HEADING = By.cssSelector("h1.srp-controls__count-heading");
    private static final By RESULT_ITEMS =
            By.cssSelector("ul.srp-results li.s-item, ul.srp-results li.s-card");

    // Filter locators are built from the data file values (nothing hardcoded)
    private static By filterTitle(String filterName) {
        return By.xpath("//span[contains(@class,'details__label')][normalize-space()='" + filterName + "']");
    }

    private static By filterCheckbox(String filterName, String filterValue) {
        return By.xpath("//input[@name='su-selection-group__aspect-" + filterName
                + "' and @value='" + filterValue + "']");
    }

    // The checkbox input is hidden by eBay; the clickable element is the link that wraps it
    private static By filterOptionLink(String filterName, String filterValue) {
        return By.xpath("//input[@name='su-selection-group__aspect-" + filterName
                + "' and @value='" + filterValue + "']/ancestor::a[1]");
    }

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Page load (resilient) ----------
    /**
     * Waits for the results heading. eBay sometimes returns a temporary error page instead of the
     * results, so if the heading does not show up the page is refreshed and the wait is retried.
     */
    private void waitForResultsPage() {
        for (int attempt = 1; attempt <= MAX_LOAD_ATTEMPTS; attempt++) {
            try {
                new WebDriverWait(driver, RESULTS_WAIT)
                        .until(ExpectedConditions.visibilityOfElementLocated(RESULTS_HEADING));
                return;
            } catch (TimeoutException e) {
                if (attempt == MAX_LOAD_ATTEMPTS) {
                    throw e;
                }
                driver.navigate().refresh();
            }
        }
    }

    // ---------- Shipping location ----------
    /**
     * eBay only shows some filters (e.g. Transmission for vehicles) for certain ship-to locations.
     * The location is set through the same URL parameters eBay uses (_stpos = ZIP, _fcid = country id).
     */
    public SearchResultsPage setShipToLocation(String zip, String countryId) {
        waitForResultsPage(); // make sure the search results page is fully loaded first
        String url = getCurrentUrl();
        String separator = url.contains("?") ? "&" : "?";
        navigateTo(url + separator + "_stpos=" + zip + "&_fcid=" + countryId);
        waitForResultsPage();
        return this;
    }

    // ---------- Results ----------
    public String getResultsHeadingText() {
        waitForResultsPage();
        return getText(RESULTS_HEADING);
    }

    /** Extracts the number of results from the heading, e.g. "110,000+ results for ..." -> 110000 */
    public int getResultsCount() {
        Matcher matcher = Pattern.compile("[\\d,]+").matcher(getResultsHeadingText());
        if (matcher.find()) {
            return Integer.parseInt(matcher.group().replace(",", ""));
        }
        throw new IllegalStateException("Could not read results count from: " + getResultsHeadingText());
    }

    public int getDisplayedItemsCount() {
        return findElements(RESULT_ITEMS).size();
    }

    public boolean isResultsHeadingDisplayed() {
        try {
            waitForResultsPage();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---------- Filters ----------
    public boolean isFilterAvailable(String filterName) {
        return isDisplayed(filterTitle(filterName));
    }

    public SearchResultsPage applyFilter(String filterName, String filterValue) {
        By optionLink = filterOptionLink(filterName, filterValue);
        scrollToElement(optionLink);
        jsClick(optionLink);
        waitForUrlContains(filterValue);
        waitForResultsPage();
        return this;
    }

    public boolean isFilterApplied(String filterName, String filterValue) {
        String url = URLDecoder.decode(getCurrentUrl(), StandardCharsets.UTF_8);
        if (url.contains(filterName + "=" + filterValue)) {
            return true;
        }
        try {
            return driver.findElement(filterCheckbox(filterName, filterValue)).isSelected();
        } catch (Exception e) {
            return false;
        }
    }
}
