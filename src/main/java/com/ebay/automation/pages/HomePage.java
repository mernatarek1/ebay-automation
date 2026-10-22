package com.ebay.automation.pages;

import com.ebay.automation.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

public class HomePage extends BasePage {

    // ---------- Locators ----------
    private static final By SEARCH_BOX = By.cssSelector("#gh-ac");
    private static final By SEARCH_BUTTON = By.cssSelector("#gh-search-btn, #gh-btn");
    private static final By LOGO = By.cssSelector("#gh-logo");
    private static final By POPUP_CLOSE_BUTTON = By.cssSelector(".lightbox-dialog__close");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ---------- Actions ----------
    public HomePage open(String url) {
        // eBay sometimes returns a temporary "Error Page"; retry a few times before continuing
        for (int attempt = 1; attempt <= 3; attempt++) {
            navigateTo(url);
            if (!isErrorPage()) {
                break;
            }
            pause(2000);
        }
        dismissPopupIfPresent();
        return this;
    }

    private boolean isErrorPage() {
        return getPageTitle().toLowerCase().contains("error");
    }

    private void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** eBay may show a dialog (e.g. shipping address / location) that blocks clicks. Closes it if present. */
    public HomePage dismissPopupIfPresent() {
        if (!clickIfPresent(POPUP_CLOSE_BUTTON, Duration.ofSeconds(5))) {
            new Actions(driver).sendKeys(Keys.ESCAPE).perform();
        }
        return this;
    }

    public SearchResultsPage searchFor(String searchTerm) {
        type(SEARCH_BOX, searchTerm);
        click(SEARCH_BUTTON);
        return new SearchResultsPage(driver);
    }

    // ---------- Validations ----------
    public boolean isSearchBoxDisplayed() {
        return isDisplayed(SEARCH_BOX);
    }

    public boolean isLogoDisplayed() {
        return isDisplayed(LOGO);
    }
}
