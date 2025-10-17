package com.rev.admin.utils;

import java.util.Base64;

public class AuthHeaderProvider {

    public static String getAuthValue() {
        String authType = ConfigReader.get("authType").toUpperCase();

        switch (authType) {
            case "BEARER":
            case "OAUTH2":
                String token = TokenManager.getBearerToken();
                return (token != null) ? "Bearer " + token : null; 
            
            case "COOKIE":
                return TokenManager.getSessionCookie(); 
            
            case "BASIC":
                String credentials = ConfigReader.get("username") + ":" + ConfigReader.get("password");
                String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
                return "Basic " + encoded;
                
            default:
                return ConfigReader.get("authValue"); 
        }
    }
    
    public static String getAuthKey() {
        String authType = ConfigReader.get("authType").toUpperCase();

        switch (authType) {
            case "BEARER":
            case "BASIC":
            case "OAUTH2":
                return "Authorization";
            
            case "COOKIE":
                String dynamicName = ConfigReader.get("dynamicSessionCookieName");
                if (dynamicName != null) {
                    return dynamicName;
                }
                return "JSESSIONID"; 
                
            default:
                return ConfigReader.get("authKey"); 
        }
    }
    
    public static boolean isCookieAuth() {
        String authType = ConfigReader.get("authType");
        return authType != null && authType.equalsIgnoreCase("COOKIE");
    }
}