package com.flamingo.qa.helpers.common;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;

/**
 * Puts unique TMS test id into MDC so every log line is attributable to a test.
 */
public class TestContextExtension implements BeforeEachCallback, AfterEachCallback {

    public static final String MDC_TEST_ID = "testId";

    @Override
    public void beforeEach(ExtensionContext context) {
        String testId = resolveTestId(context);
        MDC.put(MDC_TEST_ID, testId);
        TestLog.step("TEST START", context.getDisplayName());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        TestLog.step("TEST END", context.getDisplayName());
        MDC.remove(MDC_TEST_ID);
    }

    private static String resolveTestId(ExtensionContext context) {
        return context.getTags().stream()
                .filter(tag -> tag.startsWith("FLA-"))
                .sorted()
                .findFirst()
                .orElseGet(() -> context.getRequiredTestMethod().getName());
    }
}
