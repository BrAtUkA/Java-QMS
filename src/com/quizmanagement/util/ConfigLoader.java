package com.quizmanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading application configuration from properties files.
 * Supports loading from classpath (bundled in JAR) with external file override.
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        
        // Load from classpath (inside JAR or src folder)
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
                logger.info("Configuration loaded from classpath: {}", CONFIG_FILE);
            } else {
                logger.error("Configuration file not found in classpath: {}", CONFIG_FILE);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration file", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Get a configuration property value.
     * @param key the property key
     * @return the property value, or null if not found
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get a configuration property value with a default.
     * @param key the property key
     * @param defaultValue the default value if key not found
     * @return the property value, or defaultValue if not found
     */
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get a configuration property as an integer.
     * @param key the property key
     * @param defaultValue the default value if key not found or not a valid integer
     * @return the property value as int, or defaultValue if not found/invalid
     */
    public static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for key '{}': {}, using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Get a configuration property as a long.
     * @param key the property key
     * @param defaultValue the default value if key not found or not a valid long
     * @return the property value as long, or defaultValue if not found/invalid
     */
    public static long getLong(String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid long value for key '{}': {}, using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Reload configuration from file.
     */
    public static void reload() {
        loadProperties();
    }
}
