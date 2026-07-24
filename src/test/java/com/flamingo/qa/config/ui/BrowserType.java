package com.flamingo.qa.config.ui;

import java.util.Locale;

/** Playwright browser engines supported by {@link com.flamingo.qa.browser.BrowserFactory}. */
public enum BrowserType {
    CHROMIUM,
    FIREFOX,
    WEBKIT;

    public static BrowserType from(String value) {
        if (value == null || value.isBlank()) {
            return CHROMIUM;
        }
        return BrowserType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
