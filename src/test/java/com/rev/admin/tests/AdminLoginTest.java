package com.rev.admin.tests;

import com.rev.admin.utils.AdminActions;
import com.rev.admin.utils.TokenManager;
import com.rev.admin.base.BaseTest;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdminLoginTest extends BaseTest {
    
    // Keys used in the Step 2 response body
    private static final String SESSION_KEY = "pre_auth_session_id"; 
    private static final String CODE_KEY = "code_id"; 
    private static final String DEVICE_KEY = "device_id";
    private static final String PHONE = "9923970719";

  

    @Test
    public void adminLoginWithOtpTest() {
        
        // ----------------------------------------------------
        // Step 1: Login (POST /v1/admin/auth/signin) - SUCCESSFUL
        // ----------------------------------------------------
        Response loginResponse = TokenManager.loginWithEmailAndPassword();
        Assert.assertEquals(loginResponse.getStatusCode(), 200, "Step 1: Admin login failed (OTP initiation)");
        
        String frontToken = loginResponse.getHeader("Front-Token");
        // ... (save Front-Token logic here)

        
        // ----------------------------------------------------
        // 🟢 Step 2: Request Code Challenge (POST /v1/admin/signincode) - POTENTIAL FAILURE POINT
        // ----------------------------------------------------
        Response codeResponse = AdminActions.requestCodeChallenge(frontToken, PHONE);
        
        // 🚨 ADD BACK DEBUGGING FOR STEP 2 🚨
        System.out.println("\n--- DEBUG: Request Code Challenge Response (Step 2) ---");
        System.out.println("Status Code: " + codeResponse.getStatusCode());
        System.out.println("Response Body (Raw): \n" + codeResponse.getBody().asString());
        System.out.println("----------------------------------------------------------\n");

        Assert.assertEquals(codeResponse.getStatusCode(), 200, "Step 2: Request Code Challenge failed (Status Code).");

        // Extraction is the next likely failure point if the status code isn't 200
        String dynamicSessionId = codeResponse.jsonPath().getString(SESSION_KEY);
        String dynamicCodeId = codeResponse.jsonPath().getString(CODE_KEY);
        String dynamicDeviceId = codeResponse.jsonPath().getString(DEVICE_KEY); 

        if (dynamicSessionId == null || dynamicCodeId == null || dynamicDeviceId == null) {
             Assert.fail("Step 2 Failed: Did not receive required flow IDs (Session/Code/Device). Check Step 2 response body.");
        }
        
        // ----------------------------------------------------
        // 🟢 Step 3: Verify OTP (POST /v1/admin/signinconsumecode) - POTENTIAL FAILURE POINT
        // ----------------------------------------------------
        Response otpResponse = AdminActions.verifyOtp(
            dynamicSessionId, 
            dynamicCodeId, 
            dynamicDeviceId,
            PHONE, 
            "123456" 
        ); 
        
        // 🚨 ADD BACK DEBUGGING FOR STEP 3 🚨
        System.out.println("\n--- DEBUG: Final OTP Verification Response (Step 3) ---");
        System.out.println("Status Code: " + otpResponse.getStatusCode());
        System.out.println("Response Body (Raw): \n" + otpResponse.getBody().asString());
        System.out.println("-------------------------------------------------------\n");

        Assert.assertEquals(otpResponse.getStatusCode(), 200, "Step 3: Final OTP verification failed (Status Code).");
        
        // Final success check
        String finalUserId = otpResponse.jsonPath().getString("user.user_id"); 
        Assert.assertNotNull(finalUserId, "Step 3: OTP verification succeeded, but final user ID not found in body.");
        
        // Final success message if everything passed
        System.out.println("\n===============================================");
        System.out.println("✅✅ TEST PASSED: All 3 API steps successful. ✅✅");
        System.out.println("===============================================");
    }
}