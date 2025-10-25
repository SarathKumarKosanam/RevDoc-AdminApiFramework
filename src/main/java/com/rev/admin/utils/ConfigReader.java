package com.rev.admin.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties = new Properties();
    private static boolean isLoaded = false;

    // 🔹 Initialize method (optional explicit call)
    public static void init() {
        if (!isLoaded) {
            // Using a try-with-resources block ensures the FileInputStream is closed
            try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
                properties.load(fis);
                isLoaded = true;
                System.out.println("✅ Config file loaded successfully.");
            } catch (IOException e) {
                System.err.println("❌ Failed to load config.properties: " + e.getMessage());
                // Consider throwing a RuntimeException here to fail tests cleanly if config is mandatory
            }
        }
    }

    // 🔹 Get property value
    public static String get(String key) {
        if (!isLoaded) {
            init(); // auto-load if not yet loaded
        }
        return properties.getProperty(key);
    }
    
    // 🔹 NEW: Set property value (used for dynamic configuration like cookie name)
    /**
     * Sets or updates a property dynamically. This is used by the TokenManager 
     * to store the dynamically discovered session cookie name.
     * @param key The key to set (e.g., "dynamicSessionCookieName")
     * @param value The value to store
     */
    public static void set(String key, String value) {
        if (!isLoaded) {
            init(); 
        }
        properties.setProperty(key, value);
        // We don't print a message here to avoid log clutter for every dynamic set,
        // but you can add one if needed.
    }
}