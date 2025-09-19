package com.selenide.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


 // ConfigReader loads configuration values from config.properties.
 // Supports overriding via system properties (useful in CI/CD).

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = new FileInputStream("src/test/resources/config/config.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("⚠️ Could not load config.properties file", e);
        }
    }

    /**
     * Gets a property value by key.
     * System properties (-Dkey=value) override config.properties.
     */
    public static String get(String key) {
        return System.getProperty(key, properties.getProperty(key));
    }

    /**
     * Gets a property value with a default if missing.
     */
    public static String get(String key, String defaultValue) {
        return System.getProperty(key, properties.getProperty(key, defaultValue));
    }

    /**
     * Gets an int property (throws if missing).
     */
    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    /**
     * Gets an int property with a default.
     */
    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        return (value != null) ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * Gets a boolean property (throws if missing).
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * Gets a boolean property with a default.
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }
}
