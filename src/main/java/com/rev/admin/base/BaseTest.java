package com.rev.admin.base;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.ITestResult;
import java.lang.reflect.Method;

import com.rev.admin.utils.AuthHeaderProvider;
import com.rev.admin.utils.ConfigReader;
import com.rev.admin.utils.TokenManager;
import com.rev.admin.reports.ExtentManager;
import com.rev.admin.reports.ExtentTestManager;

import io.restassured.RestAssured;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.annotations.Test; // Added Test annotation import

public class BaseTest {
	
	public String FrontToken;
	public String Cookie;

protected static ExtentReports extent;
// Removed: protected static ExtentTest test; // ExtentTest is now managed by ExtentTestManager

// 🔹 Extent Report initialization (runs once per suite)
@BeforeSuite
public void initReport() {
ConfigReader.init();	
extent = ExtentManager.createInstance();
System.out.println("✅ Extent Report initialized successfully");
}
    
    // 🔹 Extent Test setup (runs before every test method)
    @BeforeMethod
    public void startExtentTest(Method method) {
        String testName = method.getName();
        // Extracting description from the @Test annotation if available
        String description = method.getAnnotation(Test.class) != null ? method.getAnnotation(Test.class).description() : testName;
        
        ExtentTestManager.startTest(testName, description);
    }

// 🔹 RestAssured setup (runs before every test class)
@BeforeClass
public void setUpFrontTokeAndCookie() {
// 1. Set Base URI once
//  RestAssured.baseURI = ConfigReader.get("baseUrl");

// 2. RUN FULL AUTH FLOW AND GET FINAL COOKIE/TOKEN
// This single call triggers the 3-step login if not yet done.
String[] Tokens = TokenManager.loginWithEmailAndPassword();

this.FrontToken = Tokens[0];
this.Cookie = "sAccessToken="+Tokens[1];
}

    // 🔹 Extent Test finalizer (runs after every test method)
    @AfterMethod
    public void endExtentTest(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();

        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test Failed: " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.log(Status.SKIP, "Test Skipped: " + result.getSkipCausedBy());
        } else {
            test.log(Status.PASS, "Test Passed");
        }
        // Flushing is handled in @AfterSuite
    }

// 🔹 Flush report after suite execution
@AfterSuite
public void tearDownReport() {
if (extent != null) {
extent.flush();
System.out.println("📊 Extent Report generated successfully");}
}
}
