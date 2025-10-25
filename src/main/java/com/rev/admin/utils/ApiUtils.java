package com.rev.admin.utils;

import com.rev.admin.endpoints.AdminEndpoints;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ApiUtils {

    // 💡 CRITICAL FIX: Missing method added to support ProcedrueLoading.java
    public static Response sendGetUsingBaseSpec(String endpoint) {
        return RestAssured.given()
                .when()
                .get(endpoint)
                .then()
                .extract().response();
    }
    
    // Sends the request using the token explicitly as an 'sAccessToken' cookie
    public static Response SendGetWithAccessTokenCookie(String endpoint, String sAccessTokenValue) {
        return RestAssured.given()
                .header("Content-Type", ContentType.JSON)
                // Alternative: Use the custom header name seen in your logs
                .header("cookie", sAccessTokenValue) 
                .log().headers()
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();
    }

    
    // Remaining methods (sendPostWithFrontToken, sendGetWithToken, SendGetWithBearer, etc.)
    // ... (These methods are assumed to be correct from previous exchanges) ...
    // Since you only need the fix, I will only include the critical methods here.
    
    public static Response sendPost(String endpoint, String body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .post(ConfigReader.get("baseUrl")+endpoint)
                .then()
                .extract().response();
    }
}