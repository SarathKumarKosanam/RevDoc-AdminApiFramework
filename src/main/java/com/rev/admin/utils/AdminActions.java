package com.rev.admin.utils;

import com.rev.admin.endpoints.AdminEndpoints;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * Utility methods for performing specific admin actions, like the multi-step authentication flow.
 */
public class AdminActions {

    // Step 2: Request Code Challenge
    public static Response requestCodeChallenge(String sessionToken, String phone) {
        
        // Use simple flat JSON structure as it fixed the 422 error previously.
        String challengeBody = "{"
            + "\"phone\": \"" + phone + "\""
            + "}";

        return RestAssured.given()
                .baseUri(ConfigReader.get("baseUrl"))
                .basePath(AdminEndpoints.REQUEST_CODE)
                .header("Accept", "application/json")
                // FIX: Use the Front-Token Header for session continuation (Step 2)
                .header("Front-Token", sessionToken) 
                .contentType(ContentType.JSON)
                .body(challengeBody)
                .log().all()
                .post()
                .then()
                .log().all()
                .statusCode(200) // Expect 200 on success
                .extract().response();
    }

    // Step 3: Verify OTP
    public static Response verifyOtp(String sessionToken, String preAuthSessionId, String codeId, String deviceId, String phone, String otp) {
        
        // FIX: Rebuilding the body to use snake_case keys at the root 
        // and using 'user_input_code' for the OTP value, as required by the 422 error detail.
        String verifyBody = "{"
            // Required snake_case fields at the root level
            + "\"pre_auth_session_id\":\"" + preAuthSessionId + "\"," 
            + "\"code_id\":\"" + codeId + "\"," 
            + "\"device_id\":\"" + deviceId + "\"," 
            + "\"phone\":\"" + phone + "\","
            
            // The OTP value, as named by the API error detail
            + "\"user_input_code\":\"" + otp + "\"" 
            + "}";
        
        // CRITICAL: The endpoint path for Step 3 is /v1/admin/signinconsumecode, 
        // which AdminEndpoints.VERIFY_OTP already uses.
        
        return RestAssured.given()
                .baseUri(ConfigReader.get("baseUrl"))
                .basePath(AdminEndpoints.VERIFY_OTP)
                .header("Accept", "application/json")
                // Use the Front-Token Header for session continuation (Step 3)
                .header("Front-Token", sessionToken) 
                .contentType(ContentType.JSON)
                .body(verifyBody)
                .log().all()
                .post()
                .then()
                .log().all()
                 // Expect 200 on final success
                .extract().response();
    }
}
