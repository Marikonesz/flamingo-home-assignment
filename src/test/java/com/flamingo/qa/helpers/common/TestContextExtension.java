package com.flamingo.qa.helpers.common;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;

/**
 * Puts a unique test id into SLF4J MDC so HTTP and step logs correlate with a test run.
 * Prefers the first {@code FLA-*} JUnit tag; falls back to the test method name.
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
