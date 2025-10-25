package com.rev.admin.tests;

import com.rev.admin.base.BaseTest;
import com.rev.admin.endpoints.AdminEndpoints;
import com.rev.admin.utils.ApiUtils;
import com.rev.admin.reports.ExtentTestManager; // New Import
import com.aventstack.extentreports.ExtentTest; // New Import
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
// import org.testng.Reporter; // Removed this import
import com.rev.admin.utils.ConfigReader;

public class AdminLoginTest extends BaseTest {


    // Keys used in the Step 2 response body
    private static final String SESSION_KEY = "pre_auth_session_id";
    private static final String CODE_KEY = "code_id";
    private static final String DEVICE_KEY = "device_id";

    // Variables for the active Step 3 logic
    private static final String PHONE = "9923970719";
    private static final String HARDCODED_TEST_CODE = "123456";

    @Test
    public void adminLoginWithOtpTest() {

        // 1. Get the Extent Test instance for the current thread
        ExtentTest test = ExtentTestManager.getTest();

        // ----------------------------------------------------
        // Step 1: Login (POST /v1/admin/auth/signin)
        // ----------------------------------------------------
        test.info("Starting Step 1: Initiating Admin Login (POST /auth/signin)");
        Response loginResponse = ApiUtils.sendPost(AdminEndpoints.LOGIN, ConfigReader.get("LoginBody"));

        try {
            Assert.assertEquals(loginResponse.getStatusCode(), 200, "Step 1: Admin login failed (OTP initiation)");
            test.pass("Step 1 Passed. Status Code: 200");
        } catch (AssertionError e) {
            // Explicitly mark failure in Extent Report
            test.fail("Step 1 Failed. Expected Status 200, but got " + loginResponse.getStatusCode() + ". Error: " + e.getMessage());
            throw e; // Re-throw to fail the TestNG test
        }


        // ----------------------------------------------------
        // Step 2: Request Code Challenge (POST /v1/admin/signincode)
        // ----------------------------------------------------
        test.info("Starting Step 2: Requesting Sign-in Code Challenge (POST /signincode)");
        Response codeResponse = ApiUtils.sendPost(AdminEndpoints.REQUEST_CODE, ConfigReader.get("SignInBody"));

        // DEBUGGING FOR STEP 2 (Using Extent Logger)
        test.info("--- DEBUG: Request Code Challenge Response (Step 2) ---");
        test.info("Status Code: " + codeResponse.getStatusCode());
        // Using <pre> tag to preserve the JSON formatting in the HTML report
        test.info("Response Body (Raw): <pre>" + codeResponse.getBody().asString() + "</pre>");
        test.info("----------------------------------------------------------");

        try {
            Assert.assertEquals(codeResponse.getStatusCode(), 200, "Step 2: Request Code Challenge failed (Status Code).");
            test.pass("Step 2 Passed. Status Code: 200");
        } catch (AssertionError e) {
            test.fail("Step 2 Failed. Expected Status 200, but got " + codeResponse.getStatusCode() + ". Error: " + e.getMessage());
            throw e;
        }

        // Extraction is critical for the next step
        String dynamicSessionId = codeResponse.jsonPath().getString(SESSION_KEY);
        String dynamicCodeId = codeResponse.jsonPath().getString(CODE_KEY);
        String dynamicDeviceId = codeResponse.jsonPath().getString(DEVICE_KEY);

        if (dynamicSessionId == null || dynamicCodeId == null || dynamicDeviceId == null) {
             test.fail("Step 2 Failed: Did not receive required flow IDs (Session/Code/Device). Check Step 2 response body.");
             Assert.fail("Step 2 Failed: Did not receive required flow IDs (Session/Code/Device). Check Step 2 response body.");
        }
        else {
            // Logs extracted IDs using Extent Logger
        	test.info("Successfully extracted IDs for Step 3:");
            test.info("Session ID: " + dynamicSessionId);
            test.info("Code ID: " + dynamicCodeId);
            test.info("Device ID: " + dynamicDeviceId);
        }

       // ----------------------------------------------------
       // Step 3: Verify OTP (POST /v1/admin/signinconsumecode)
       // ----------------------------------------------------
       test.info("Starting Step 3: Verifying OTP (POST /signinconsumecode)");
       String OTPAPIBody = "{\"pre_auth_session_id\":\"" +
    		   dynamicSessionId + "\",\"code_id\":\"" +
    		   dynamicCodeId + "\",\"device_id\":\"" +
    		   dynamicDeviceId + "\",\"user_input_code\":\"" +
    		   HARDCODED_TEST_CODE + "\",\"phone\":\"" + PHONE + "\"}";

        Response otpResponse = ApiUtils.sendPost(AdminEndpoints.VERIFY_OTP, OTPAPIBody);

        // DEBUGGING FOR STEP 3 (Using Extent Logger)
        test.info("--- DEBUG: Final OTP Verification Response (Step 3) ---");
        test.info("Request Body Sent: <pre>" + OTPAPIBody + "</pre>");
        test.info("Status Code: " + otpResponse.getStatusCode());
        test.info("Response Body (Raw): <pre>" + otpResponse.getBody().asString() + "</pre>");
        test.info("-------------------------------------------------------");

        try {
            Assert.assertEquals(otpResponse.getStatusCode(), 200, "Step 3: Final OTP verification failed (Status Code).");
            test.pass("Step 3 Passed. Status Code: 200.");
        } catch (AssertionError e) {
            test.fail("Step 3 Failed. Expected Status 200, but got " + otpResponse.getStatusCode() + ". Error: " + e.getMessage());
            throw e;
        }

        // Final success check: Ensure we received a user ID (and by extension, a token)
        String finalUserId = otpResponse.jsonPath().getString("user.user_id");

        try {
            Assert.assertNotNull(finalUserId, "Step 3: OTP verification succeeded, but final user ID not found in body.");
            test.pass("Final Assertion Passed: User ID found (" + finalUserId + ") and login flow complete.");
        } catch (AssertionError e) {
            test.fail("Final Assertion Failed: User ID not found. Error: " + e.getMessage());
            throw e;
        }

        // Log final artifact (cookie)
        test.info("Final Cookie: " + otpResponse.getHeader("cookie"));

        test.pass("===============================================");
        test.pass("✅✅ FULL TEST PASSED: All 3 API steps successful. ✅✅");
        test.pass("===============================================");
    }
}
