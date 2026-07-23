package com.flamingo.qa.models.ui.page;

import com.flamingo.qa.config.ui.UiConfig;
import com.flamingo.qa.helpers.common.TestLog;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;

/**
 * Shared Page Object foundation: holds Page and navigates using configured base URL.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    protected void open(String path) {
        String url = UiConfig.baseUrl().replaceAll("/$", "") + path;
        TestLog.step("Navigate", url);
        page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
    }
}
