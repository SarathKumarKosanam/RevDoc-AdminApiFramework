package com.rev.admin.utils;

import java.util.Base64;

public class AuthHeaderProvider {

    /**
     * Determines and returns the value of the authentication credential 
     * based on the 'authType' set in ConfigReader.
     * * For Cookie auth, it retrieves the final sAccessToken from TokenManager.
     */
    public static String getAuthValue() {
        // Read auth type from configuration
        String authType = ConfigReader.get("authType").toUpperCase();

        switch (authType) {
            case "BEARER":
            case "OAUTH2":
                // For Bearer/OAuth, return "Bearer <token>"
                String token = TokenManager.getFinalSAccessToken(); // Use the final token
                return (token != null) ? "Bearer " + token : null; 
            
            case "COOKIE":
                // For Cookie auth, return the raw cookie value (sAccessToken)
                return TokenManager.getFinalSAccessToken(); 
            
            case "BASIC":
                // For Basic auth, encode username:password
                String credentials = ConfigReader.get("username") + ":" + ConfigReader.get("password");
                String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
                return "Basic " + encoded;
                
            default:
                // Fallback to a custom value defined in config
                return ConfigReader.get("authValue"); 
        }
    }
    
    /**
     * Determines and returns the key/name of the authentication credential 
     * based on the 'authType' set in ConfigReader.
     */
    public static String getAuthKey() {
        String authType = ConfigReader.get("authType").toUpperCase();

        switch (authType) {
            case "BEARER":
            case "BASIC":
            case "OAUTH2":
                return "Authorization";
            
            case "COOKIE":
                // For Cookie auth, return the final cookie name ("sAccessToken")
                return TokenManager.getFinalAccessTokenKey(); 
                
            default:
                // Fallback to a custom key defined in config
                return ConfigReader.get("authKey"); 
        }
    }
    
    /**
     * Helper method to check if the current configuration uses Cookie authentication.
     */
    public static boolean isCookieAuth() {
        String authType = ConfigReader.get("authType");
        return authType != null && authType.equalsIgnoreCase("COOKIE");
    }
}