package com.rev.admin.utils;

import com.rev.admin.endpoints.AdminEndpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.http.ContentType;

public class AdminActions {

    // NOTE: This method should ideally be fixed to use input parameters.
    public static Response loginWithEmailWithRequestPayload(String email, String password) {
    	 // 🔴 CRITICAL: Use the input parameters here, not hardcoded ones!
    	 String body = String.format("{\"formFields\":[{\"id\":\"email\",\"value\":\"%s\"},{\"id\":\"password\",\"value\":\"%s\"}]}",
                                     email, password);
    	 return RestAssured.given()
                 .contentType(ContentType.JSON)
                 .body(body)
                 .post(AdminEndpoints.LOGIN);
    }
    
    public static Response requestCodeChallenge(String frontToken, String phone) {
        // Confirmed payload: only 'phone' is used in the request body
        String body = String.format("{\"phone\":\"%s\"}", phone);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                // Use the Front-Token from the Step 1 response in the Authorization header
                .header("Authorization", "Bearer " + frontToken) 
                .body(body)
                .post(AdminEndpoints.REQUEST_CODE); // Posts to /v1/admin/signincode
    }

    public static Response verifyOtp(String preAuthSessionId, String codeId, String deviceId, String phone, String userInputCode) {
        // Confirmed payload structure
        String body = String.format("{\"pre_auth_session_id\":\"%s\",\"code_id\":\"%s\",\"device_id\":\"%s\",\"phone\":\"%s\",\"user_input_code\":\"%s\"}",
                                    preAuthSessionId, codeId, deviceId, phone, userInputCode);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post(AdminEndpoints.VERIFY_OTP);
    }
}
