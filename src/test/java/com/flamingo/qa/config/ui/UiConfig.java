package com.flamingo.qa.config.ui;

import com.flamingo.qa.config.ConfigProperties;

public final class UiConfig {

    private UiConfig() {
    }

    public static String baseUrl() {
        return ConfigProperties.get("ui.baseUrl");
    }

    public static BrowserType browser() {
        return BrowserType.from(ConfigProperties.get("ui.browser", "chromium"));
    }

    public static boolean headless() {
        return Boolean.parseBoolean(ConfigProperties.get("ui.headless", "true"));
    }

    public static long defaultTimeoutMs() {
        return Long.parseLong(ConfigProperties.get("ui.defaultTimeoutMs", "15000"));
    }
}
