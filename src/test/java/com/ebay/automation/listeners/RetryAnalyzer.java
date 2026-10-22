package com.ebay.automation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Re-runs a failed test once, to absorb temporary eBay errors (e.g. its "Sorry" error page). */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 1;
    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < MAX_RETRIES) {
            attempt++;
            return true;
        }
        return false;
    }
}
