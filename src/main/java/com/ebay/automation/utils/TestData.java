package com.ebay.automation.utils;

/**
 * Plain object that mirrors testData.json.
 * To add a new data field: add it to the JSON file and add a field + getter/setter here.
 */
public class TestData {

    private String baseUrl;
    private String browser;
    private String homePageTitleContains;
    private String searchTerm;
    private String shipToZip;
    private String shipToCountryId;
    private String filterName;
    private String filterValue;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }

    public String getHomePageTitleContains() { return homePageTitleContains; }
    public void setHomePageTitleContains(String homePageTitleContains) { this.homePageTitleContains = homePageTitleContains; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public String getShipToZip() { return shipToZip; }
    public void setShipToZip(String shipToZip) { this.shipToZip = shipToZip; }

    public String getShipToCountryId() { return shipToCountryId; }
    public void setShipToCountryId(String shipToCountryId) { this.shipToCountryId = shipToCountryId; }

    public String getFilterName() { return filterName; }
    public void setFilterName(String filterName) { this.filterName = filterName; }

    public String getFilterValue() { return filterValue; }
    public void setFilterValue(String filterValue) { this.filterValue = filterValue; }
}
