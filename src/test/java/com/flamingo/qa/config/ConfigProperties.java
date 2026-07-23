package com.flamingo.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

/**
 * Shared config property resolution: system property → env var → config.properties.
 */
public final class ConfigProperties {

    private static final Properties PROPERTIES = loadProperties();

    private ConfigProperties() {
    }

    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String defaultValue) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem.trim();
        }

        String envKey = key.toUpperCase(Locale.ROOT).replace('.', '_');
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }

        return Optional.ofNullable(PROPERTIES.getProperty(key)).orElse(defaultValue);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream stream = ConfigProperties.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (stream != null) {
                properties.load(stream);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config.properties", e);
        }
        return properties;
    }
}
