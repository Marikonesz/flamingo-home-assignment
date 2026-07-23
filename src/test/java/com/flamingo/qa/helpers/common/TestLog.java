package com.flamingo.qa.helpers.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Console-friendly step logging for debugging UI and API flows.
 */
public final class TestLog {

    private static final Logger LOG = LoggerFactory.getLogger("com.flamingo.qa.steps");

    private TestLog() {
    }

    public static void step(String message) {
        LOG.info("→ {}", message);
    }

    public static void step(String message, Object arg) {
        LOG.info("→ {} | {}", message, arg);
    }

    public static void step(String message, Object arg1, Object arg2) {
        LOG.info("→ {} | {} | {}", message, arg1, arg2);
    }
}
