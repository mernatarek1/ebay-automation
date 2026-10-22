package com.ebay.automation.tests;

import com.ebay.automation.base.DriverFactory;
import com.ebay.automation.utils.TestData;
import com.ebay.automation.utils.TestDataReader;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected WebDriver driver;
    protected TestData testData;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        testData = TestDataReader.getTestData();
        DriverFactory.initDriver(testData.getBrowser());
        driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
