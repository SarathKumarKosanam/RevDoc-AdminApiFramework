package com.rev.admin.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.Map;

public class TokenManager {

    private static String bearerToken;
    private static String sessionCookie;

    // Common names for session cookies to check
    private static final String[] COMMON_COOKIE_NAMES = {
        "JSESSIONID", "session_id", "PHPSESSID", "ASP.NET_SessionId", "auth_token", "SESSION"
    };
    
    // --- Public Getters (MUST NOT initiate login) ---
    public static String getBearerToken() {
        return bearerToken;
    }
    
    public static String getSessionCookie() {
        return sessionCookie;
    }
    
    // --- Public Setters (Used by AdminLoginTest after successful multi-step login) ---
    public static void setBearerToken(String token) {
        bearerToken = token;
        sessionCookie = null; // Clear cookie if setting token
        System.out.println("✅ Bearer Token updated: " + bearerToken);
    }

    // Includes the cookieName parameter to save for AuthHeaderProvider
    public static void setSessionCookie(String cookieValue, String cookieName) {
        sessionCookie = cookieValue;
        bearerToken = null; // Clear token if setting cookie
        // Save the name dynamically so AuthHeaderProvider knows what to look for
        ConfigReader.set("dynamicSessionCookieName", cookieName); 
        System.out.println("✅ Session Cookie updated: " + sessionCookie + " (Name: " + cookieName + ")");
    }
    
    // --- Exposed Login Method (Called by AdminLoginTest.java - Step 1) ---
    public static Response loginWithEmailAndPassword() {
        String loginBody = "{"
            + "\"formFields\": ["
            + "  {\"id\":\"email\", \"value\":\"" + ConfigReader.get("adminEmail") + "\"},"
            + "  {\"id\":\"password\", \"value\":\"" + ConfigReader.get("adminPassword") + "\"}"
            + "]"
            + "}";

        Response response = RestAssured.given()
                .baseUri(ConfigReader.get("baseUrl"))
                .basePath(ConfigReader.get("loginPath"))
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .body(loginBody)
                .log().all()
                .post();

        System.out.println("\n--- Login API Debug Info ---");
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body (Raw): \n" + response.asString());
        System.out.println("DEBUG: All received cookies from response: " + response.getCookies());
        System.out.println("----------------------------\n");

        return response;
    }
    
 // Inside TokenManager.java (Focus on the saveCredentialFromResponse method)

    public static void saveCredentialFromResponse(Response response, String expectedKey) {
        String token = null;

        // 1. Check response body JSON (key: st-access-token)
        try {
            token = response.jsonPath().getString(expectedKey); // expectedKey is "st-access-token"
        } catch (Exception ignored) {}

        // 2. Check headers for St-Access-Token (case-sensitive and insensitive)
        if (token == null || token.isEmpty()) {
            token = response.getHeader("St-Access-Token"); 
        }
        
        // 3. Check for the temporary token name as the final token (last resort)
        if (token == null || token.isEmpty()) {
            token = response.getHeader("Front-Token"); 
        }
        
        // 4. Check for standard Authorization header (Bearer token)
        if (token == null || token.isEmpty()) {
            String authHeader = response.getHeader("Authorization"); 
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }
        
        // 5. Check cookies (your logs say this is empty, but we check anyway)
        // No specific cookie name found, so skipping explicit check based on logs.
        
        if (token == null || token.isEmpty()) {
            // Print the headers and cookies again, in case the previous logging was partial.
            System.err.println("Token not found. Retrying debug log...");
            // Re-print the headers/cookies here if the final attempt fails 
            // to verify what the client actually receives.
            
            throw new RuntimeException("Final authentication step successful, but token retrieval failed. Please manually inspect the Step 3 network response headers (including Set-Cookie) for the exact token key.");
        }

        // Save the found token (assuming you have a method for this)
        // TokenManager.setGlobalAccessToken(token); 
    }
    
    private static String accessToken; // or use ThreadLocal<String>

    public static void setGlobalAccessToken(String token) {
        // Implement your global token storage here (e.g., static field or ThreadLocal)
        // Example:
        accessToken = token; 
    }
}