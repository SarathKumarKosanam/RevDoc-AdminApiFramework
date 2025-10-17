package com.rev.admin.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ApiUtils {

    // Generic GET
    public static Response sendGet(String endpoint, String token) {
        return RestAssured.given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .get(endpoint)
                .then()
                .extract().response();
    }

    // Generic POST
    public static Response sendPost(String endpoint, String body, String token) {
        return RestAssured.given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .body(body)
                .post(endpoint)
                .then()
                .extract().response();
    }

    // Generic PATCH
    public static Response sendPatch(String endpoint, String body, String token) {
        return RestAssured.given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .body(body)
                .patch(endpoint)
                .then()
                .extract().response();
    }

    // Generic DELETE
    public static Response sendDelete(String endpoint, String token) {
        return RestAssured.given()
                .header("Authorization", token)
                .contentType(ContentType.JSON)
                .delete(endpoint)
                .then()
                .extract().response();
    }
}
