package com.flamingo.qa.browser;

import com.flamingo.qa.config.ui.BrowserType;
import com.flamingo.qa.config.ui.UiConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;

import java.util.List;

public final class BrowserFactory {

    private BrowserFactory() {
    }

    public static Browser launch(Playwright playwright) {
        return launch(playwright, UiConfig.browser(), UiConfig.headless());
    }

    public static Browser launch(Playwright playwright, BrowserType browserType, boolean headless) {
        LaunchOptions options = new LaunchOptions().setHeadless(headless);
        switch (browserType) {
            case FIREFOX:
                return playwright.firefox().launch(options);
            case WEBKIT:
                return playwright.webkit().launch(options);
            case CHROMIUM:
            default:
                // Chromium-only flags for CI containers with limited /dev/shm.
                options.setArgs(List.of("--disable-dev-shm-usage", "--no-sandbox"));
                return playwright.chromium().launch(options);
        }
    }
}
