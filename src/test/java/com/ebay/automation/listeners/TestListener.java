package com.ebay.automation.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.ebay.automation.base.DriverFactory;
import com.ebay.automation.utils.ExtentManager;
import com.ebay.automation.utils.ExtentTestManager;
import com.ebay.automation.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;

/** Builds the HTML report and captures a screenshot (plus URL/title) whenever an attempt fails. */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTestManager.start(result.getMethod().getMethodName(), result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.get().pass("Test passed");
        ExtentTestManager.end();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logFailure(result, "Test failed");
        ExtentTestManager.end();
    }

    /** TestNG reports a failed attempt that will be retried as "skipped". */
    @Override
    public void onTestSkipped(ITestResult result) {
        logFailure(result, "Attempt failed - test will be retried");
        ExtentTestManager.end();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
        System.out.println("Execution report: " + new File(ExtentManager.REPORT_PATH).getAbsolutePath());
    }

    private void logFailure(ITestResult result, String label) {
        ExtentTest test = ExtentTestManager.get();
        if (test == null) {
            return;
        }
        if (result.getThrowable() != null) {
            test.fail(result.getThrowable());
        } else {
            test.fail(label);
        }

        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            return;
        }
        try {
            String info = "URL: " + driver.getCurrentUrl() + " | TITLE: " + driver.getTitle();
            System.out.println("[" + label + "] " + info);
            test.info(info);
            test.addScreenCaptureFromBase64String(ScreenshotUtils.captureBase64(driver), label);
            String file = ScreenshotUtils.saveToFile(driver, result.getMethod().getMethodName());
            System.out.println("[" + label + "] Screenshot saved: " + file);
        } catch (Exception e) {
            System.out.println("Could not capture screenshot: " + e.getMessage());
        }
    }
}
