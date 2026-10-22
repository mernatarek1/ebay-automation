package com.ebay.automation.utils;

import com.aventstack.extentreports.ExtentTest;

/** Keeps the current ExtentTest per thread and lets tests log steps to console and report. */
public class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    private ExtentTestManager() {
    }

    public static ExtentTest start(String name, String description) {
        ExtentTest test = ExtentManager.getInstance().createTest(name, description);
        TEST.set(test);
        return test;
    }

    public static ExtentTest get() {
        return TEST.get();
    }

    public static void info(String message) {
        System.out.println("[STEP] " + message);
        if (TEST.get() != null) {
            TEST.get().info(message);
        }
    }

    public static void end() {
        TEST.remove();
    }
}
