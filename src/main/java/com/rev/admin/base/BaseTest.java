package com.rev.admin.base;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

import com.rev.admin.utils.AuthHeaderProvider;
import com.rev.admin.utils.ConfigReader;
import com.rev.admin.utils.TokenManager;
import com.rev.admin.reports.ExtentManager;

import io.restassured.RestAssured;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

public class BaseTest {

    protected static ExtentReports extent;
    protected static ExtentTest test;

    // 🔹 Extent Report initialization (runs once per suite)
    @BeforeSuite
    public void initReport() {
        ConfigReader.init(); // keep your existing configuration loading
        extent = ExtentManager.createInstance();
        System.out.println("✅ Extent Report initialized successfully");
    }

    // 🔹 RestAssured setup (runs before every test class)
 // 🔹 RestAssured setup (runs before every test class)
    @BeforeClass
    public void setup() {
        // 1. Set Base URI once
        RestAssured.baseURI = ConfigReader.get("baseUrl");
        
        // 2. CHECK AUTHENTICATION STATUS
        String token = TokenManager.getBearerToken();
        String cookie = TokenManager.getSessionCookie();
        
        boolean isAuthenticated = (token != null && !token.isEmpty()) || 
                                  (cookie != null && !cookie.isEmpty());
                                  
        if (isAuthenticated) {
            // AUTHENTICATED PATH: Apply the stored credential
            String authKey = AuthHeaderProvider.getAuthKey();
            String authValue = AuthHeaderProvider.getAuthValue();

            if (AuthHeaderProvider.isCookieAuth()) {
                RestAssured.requestSpecification = RestAssured.given()
                    .cookie(authKey, authValue)
                    .contentType("application/json");
                System.out.println("✅ BaseTest setup: Request Spec configured with Cookie: " + authKey);
            } else {
                RestAssured.requestSpecification = RestAssured.given()
                    .header(authKey, authValue)
                    .contentType("application/json");
                System.out.println("✅ BaseTest setup: Request Spec configured with Header: " + authKey);
            }
        } else {
            // UN-AUTHENTICATED PATH: Build a simple spec to allow initial login/OTP tests to run
            RestAssured.requestSpecification = RestAssured.given()
                .contentType("application/json");
            System.out.println("⚠️ BaseTest setup: Running UN-authenticated spec. Authentication must be handled by the test method.");
        }
    }
    // 🔹 Flush report after suite execution
    @AfterSuite
    public void tearDownReport() {
        if (extent != null) {
            extent.flush();
            System.out.println("📊 Extent Report generated successfully.");
        }
    }
}
