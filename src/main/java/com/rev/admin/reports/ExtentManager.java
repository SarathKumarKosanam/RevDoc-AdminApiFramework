package com.rev.admin.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {
    
    private static ExtentReports extent;

    /**
     * Initializes the ExtentReports instance if it hasn't been created yet.
     * This should be called only once, typically in the @BeforeSuite method.
     */
    public static ExtentReports createInstance() {
        if (extent == null) {
            // Generate a unique timestamp for the report file name
            String timestamp = new SimpleDateFormat("dd-MM-yyyy_hh-mm a").format(new Date()).replace(" ", "_");
            String reportPath = "target/RevDocAdminAPIReport_" + timestamp + ".html";

            // Configure the Spark Reporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("Admin API Test Report");
            sparkReporter.config().setReportName("RevDoc Admin API Automation");
            sparkReporter.config().setTheme(Theme.DARK);

            // Create the ExtentReports instance and attach the reporter
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // Set system information
            extent.setSystemInfo("Project", "RevDoc Admin");
            extent.setSystemInfo("Tester", "Sarath");
            extent.setSystemInfo("Environment", "QA");
        }
        return extent;
    }
    
    /**
     * Retrieves the single, initialized ExtentReports instance.
     * This is used by ExtentTestManager to create new tests.
     */
    public static ExtentReports getExtent() {
        // Returns the static instance created by createInstance()
        return extent;
    }
}