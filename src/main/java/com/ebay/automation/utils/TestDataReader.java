package com.ebay.automation.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reads the external test data (JSON) so that no test data is hardcoded in the tests.
 * The file location can be overridden with: -Dtestdata.file=/testdata/other.json
 */
public class TestDataReader {

    private static final String DEFAULT_FILE = "/testdata/testData.json";
    private static TestData cached;

    private TestDataReader() {
    }

    public static synchronized TestData getTestData() {
        if (cached == null) {
            String file = System.getProperty("testdata.file", DEFAULT_FILE);
            try (InputStream stream = TestDataReader.class.getResourceAsStream(file)) {
                if (stream == null) {
                    throw new IllegalStateException("Test data file not found on classpath: " + file);
                }
                cached = new ObjectMapper().readValue(stream, TestData.class);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read test data file: " + file, e);
            }
        }
        return cached;
    }
}
