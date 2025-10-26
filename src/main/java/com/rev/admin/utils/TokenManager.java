package com.rev.admin.utils;
import com.rev.admin.endpoints.AdminEndpoints;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


/**
 * Manages the multi-step process of acquiring the final St-Access-Token for authenticated API calls.
 */
public class TokenManager {
    // --- Config ---
    private static final String ADMIN_EMAIL = ConfigReader.get("adminEmail");
    private static final String ADMIN_PASSWORD = ConfigReader.get("adminPassword");
    private static final String ADMIN_PHONE = ConfigReader.get("adminPhone"); 
    private static final String TEMP_OTP = ConfigReader.get("tempOtp"); 
    private static final String TEMP_DEVICE_ID = "test-automation-device-id-123"; 

    // --- State Variables ---
    private static String finalSAccessToken;
    // Holds the Front-Token header required to authenticate Steps 2 & 3
    private static String sessionFrontToken; 
    public static String sessionCookie;

    /**
     * Public method to get the final St-Access-Token, initiating the full flow if necessary.
     */


    // Step 1: Login with Email and Password
    public static String[] loginWithEmailAndPassword() {
        String loginBody = ConfigReader.get("LoginBody");

        System.out.println("Initiating login (Step 1)...");
        Response response = RestAssured.given()
            .baseUri(ConfigReader.get("baseUrl"))
            .basePath(AdminEndpoints.LOGIN)
            .header("Accept", "application/json")
            .contentType(ContentType.JSON)
            .body(loginBody)
            .log().all()
            .post()
            .then()
            .log().all()
            .statusCode(200)
            .extract().response();

        // FIX: Extract the Front-Token header for the session flow
        
        if(response.header("Front-Token").isEmpty()) {
        	System.out.print("Front-Token Not Found");
        	
        }
        else
        sessionFrontToken = response.header("Front-Token");
        
        if(response.header("St-Access-Token").isEmpty())
        	System.out.print("Cookie Not Found");
        else
        sessionCookie = response.header("St-Access-Token");
        
        //System.out.println("Token Generated succesfully : " + sessionCookie);
        
        return new String [] {sessionFrontToken,sessionCookie};
    }
    
    // Step 2 & 3: Logic for OTP Flow
   /* private static void completeOtpFlow() {
        
        // CRITICAL WARNING/CHECK for Phone Number "null" issue
        if (ADMIN_PHONE == null || ADMIN_PHONE.equalsIgnoreCase("null")) {
            System.err.println("❌ CRITICAL: ADMIN_PHONE is reading as NULL or 'null' from config. Please set a valid phone number in your configuration file. Proceeding with 'null' in the request body.");
            // FIX: Removed the RuntimeException here to allow the test to proceed
        }

        // --- Step 2: Request Code Challenge ---
        System.out.println("Initiating code challenge request (Step 2)...");
        
        // Pass the Front-Token as a header for authentication
        Response codeResponse = AdminActions.requestCodeChallenge(sessionTokenForOtpFlow, ADMIN_PHONE);

        // FIX: Use the exact snake_case keys from the Step 2 response (pre_auth_session_id, code_id)
        String preAuthSessionId = codeResponse.jsonPath().getString("pre_auth_session_id");
        String codeId = codeResponse.jsonPath().getString("code_id");
        
        if (preAuthSessionId == null || codeId == null) {
            // This check ensures we have the necessary IDs for Step 3
            throw new RuntimeException("OTP Challenge failed (Step 2). Required IDs missing in 200 OK Response: " + codeResponse.getBody().asString());
        }
        
        System.out.println("✅ Extracted preAuthSessionId: " + preAuthSessionId.substring(0, 8) + "...");
        System.out.println("✅ Extracted codeId: " + codeId.substring(0, 8) + "...");
        
        // --- Step 3: Verify OTP ---
        System.out.println("Initiating OTP verification (Step 3)...");
        
        Response finalResponse = AdminActions.verifyOtp(
            sessionTokenForOtpFlow, // Use the Front-Token for authentication
            preAuthSessionId, 
            codeId, 
            TEMP_DEVICE_ID,
            ADMIN_PHONE, 
            TEMP_OTP
        );
        
        // Final token is the St-Access-Token header from the final response
        finalSAccessToken = finalResponse.header(getFinalAccessTokenKey());

        if (finalSAccessToken != null) {
             System.out.println("✅ Full multi-step login flow completed successfully. Final St-Access-Token retrieved.");
        } else {
             throw new RuntimeException("Failed to retrieve final access token after OTP verification. Response Body: " + finalResponse.getBody().asString());
        }
    }*/
}
