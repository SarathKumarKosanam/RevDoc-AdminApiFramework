package com.rev.admin.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class ExtentTestManager {

    // ThreadLocal holds a map where each thread gets its own ExtentTest object
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    /**
     * Creates a new ExtentTest and assigns it to the current thread.
     * It relies on ExtentManager to retrieve the single ExtentReports instance.
     */
    public static synchronized ExtentTest startTest(String testName, String description) {
        
        // Retrieve the initialized ExtentReports singleton instance
        ExtentReports extent = ExtentManager.getExtent();
        
        if (extent == null) {
            throw new IllegalStateException("ExtentReports has not been initialized. " +
                                            "Ensure ExtentManager.createInstance() is called in @BeforeSuite.");
        }
        
        // Create the test from the shared ExtentReports instance
        ExtentTest test = extent.createTest(testName, description);
        
        // Store the test in the ThreadLocal for the current thread
        testThread.set(test);
        
        return test;
    }
    
    /**
     * Retrieves the ExtentTest instance associated with the current thread.
     * This is what test classes use to log steps.
     */
    public static ExtentTest getTest() {
        return testThread.get();
    }
    
    /**
     * Removes the current thread's ExtentTest instance from the ThreadLocal map.
     * This is crucial to prevent memory leaks in thread-pooled environments (like TestNG).
     */
    public static void endTest() {
        testThread.remove();
    }
}