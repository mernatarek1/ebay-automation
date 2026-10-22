# eBay Web Test Automation

Selenium WebDriver (Java) framework that automates a search-and-filter flow on [ebay.com](https://www.ebay.com/), built with the Page Object Model design pattern.

## Automated scenario

1. Navigate to eBay and validate the home page (page title and search box).
2. Search for `mazda mx-5`.
3. Validate the search results (heading, number of results, displayed items).
4. Print/log the number of results.
5. From the left-hand filters, select **Transmission -> Manual** and validate the filter was applied.

## Tech stack

| Purpose | Tool |
|---|---|
| Language / build | Java 17, Maven |
| Browser automation | Selenium WebDriver 4 (Selenium Manager downloads the driver automatically) |
| Test runner | TestNG |
| Reporting | ExtentReports (HTML) |
| Test data | External JSON file (Jackson) |

## Project structure

```
ebay-automation/
├── pom.xml
├── testng.xml                         # TestNG suite (registers the listener)
├── src/main/java/com/ebay/automation/
│   ├── base/
│   │   ├── DriverFactory.java         # Browser start/stop (Chrome, Firefox, Edge)
│   │   └── BasePage.java              # Common browser actions: click, type, waits, scroll...
│   ├── pages/
│   │   ├── HomePage.java              # Page Object: home page
│   │   └── SearchResultsPage.java     # Page Object: results page and filters
│   └── utils/
│       ├── TestData.java              # Maps the JSON test data
│       ├── TestDataReader.java        # Reads the JSON file
│       ├── ExtentManager.java         # HTML report setup
│       ├── ExtentTestManager.java     # Step logging to console and report
│       └── ScreenshotUtils.java
└── src/test/
    ├── java/com/ebay/automation/
    │   ├── tests/
    │   │   ├── BaseTest.java          # Browser setup/teardown
    │   │   └── EbaySearchTest.java    # The test scenario
    │   └── listeners/
    │       ├── TestListener.java      # Report + screenshot on failure
    │       └── RetryAnalyzer.java     # Retries a failed test once
    └── resources/testdata/
        └── testData.json              # External test data
```

## Prerequisites

- JDK 17 or higher
- Maven 3.8 or higher
- Google Chrome (or Firefox / Edge)
- Internet access

## How to run

```bash
mvn clean test
```

The browser opens in normal (non-headless) mode, runs the scenario, and closes at the end.

## Test data

Nothing is hardcoded in the tests. All data is read from `src/test/resources/testdata/testData.json`:

```json
{
  "baseUrl": "https://www.ebay.com/",
  "browser": "chrome",
  "homePageTitleContains": "eBay",
  "searchTerm": "mazda mx-5",
  "shipToZip": "10001",
  "shipToCountryId": "1",
  "filterName": "Transmission",
  "filterValue": "Manual"
}
```

To change the browser, search term, or filter, edit this file only. To use another data file: `mvn clean test -Dtestdata.file=/testdata/other.json` (the file must be on the test classpath).

## Reports

After a run, the HTML report is generated at:

```
reports/ExtentReport.html
```

It contains the executed steps, the logged number of results, and the pass/fail status. When an attempt fails, a screenshot is embedded in the report and also saved in `reports/screenshots/`. The page URL and title at the moment of failure are printed to the console.

## Design notes

- **Page Object Model:** each page has its own class with its locators and actions; tests contain only the scenario and validations.
- **BasePage:** wraps common browser actions with explicit waits (no implicit waits, no hardcoded sleeps in the flow).
- **Data-driven:** all test data comes from JSON.
- **Thread-safe driver:** `DriverFactory` uses `ThreadLocal`, so parallel execution can be added later.
- **Filter locators are data-driven:** they are built from `filterName` and `filterValue`, so another filter can be tested by editing the JSON.

## Known notes about the eBay site

- **Ship-to location:** eBay shows the *Transmission* filter only when the ship-to location allows vehicle listings. From some regions (e.g. Egypt) the filter is not displayed. The test therefore sets the ship-to location (ZIP and country id in the JSON) by adding the `_stpos` and `_fcid` URL parameters, the same parameters eBay uses when the location is changed from the page.
- **Temporary error pages:** eBay occasionally returns a temporary "Sorry, something went wrong" page. The framework handles this by retrying the page load and by re-running a failed test once.
- **Page changes:** eBay changes its markup from time to time; locators are kept in the page classes so they can be updated in one place.
