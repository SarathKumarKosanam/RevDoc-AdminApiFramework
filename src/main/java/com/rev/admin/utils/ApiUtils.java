package com.rev.admin.utils;


import com.rev.admin.base.BaseTest;
import com.rev.admin.endpoints.AdminEndpoints;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ApiUtils extends BaseTest {



    
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

  
    public static Response sendPost(String endpoint, String body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("cookie", Cookie) 
                .body(body)
                .post(ConfigReader.get("baseUrl")+endpoint)
                .then()
                .extract().response();
    }
    public static Response sendPatch(String endpoint, String body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("cookie",Cookie) 
                .body(body)
                .patch(ConfigReader.get("baseUrl")+endpoint)
                .then()
                .extract().response();
    }
    public static Response sendPut(String endpoint, String body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("cookie",Cookie) 
                .body(body)
                .put(ConfigReader.get("baseUrl")+endpoint)
                .then()
                .extract().response();
    }
    
    
}
