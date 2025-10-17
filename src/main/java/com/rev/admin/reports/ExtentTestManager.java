package com.rev.admin.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    public static synchronized ExtentTest startTest(String testName, String description) {
        ExtentReports extent = ExtentManager.createInstance();
        ExtentTest test = extent.createTest(testName, description);
        testThread.set(test);
        return test;
    }

    public static synchronized ExtentTest getTest() {
        return testThread.get();
    }
}
