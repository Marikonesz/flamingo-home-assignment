package com.flamingo.qa.helpers.reporting;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Attaches failure artifacts to Allure and writes screenshots/traces under {@code target/}.
 * Disk file names include MDC test id when {@link com.flamingo.qa.helpers.common.TestContextExtension} is active.
 */
public final class AllureHelper {

    private static final Logger LOG = LoggerFactory.getLogger(AllureHelper.class);
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private AllureHelper() {
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content == null ? "" : content, ".txt");
    }

    public static void attachScreenshot(String name, byte[] pngBytes) {
        if (pngBytes == null || pngBytes.length == 0) {
            return;
        }
        Allure.addAttachment(name, "image/png", new ByteArrayInputStream(pngBytes), ".png");
    }

    public static void attachFile(String name, Path file, String mediaType, String fileExtension) {
        if (file == null || !Files.isRegularFile(file)) {
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(file);
            Allure.addAttachment(name, mediaType, new ByteArrayInputStream(bytes), fileExtension);
        } catch (IOException e) {
            attachText(name + " (attach error)", e.getMessage());
        }
    }

    public static Path saveScreenshotToDisk(byte[] pngBytes, String testName) {
        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            Path file = dir.resolve(artifactBaseName(testName) + "-" + LocalDateTime.now().format(TS) + ".png");
            Files.write(file, pngBytes);
            LOG.info("Saved screenshot: {}", file.toAbsolutePath());
            return file;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to save screenshot", e);
        }
    }

    public static Path newTracePath(String testName) {
        try {
            Path dir = tracesDir();
            return dir.resolve(artifactBaseName(testName) + "-" + LocalDateTime.now().format(TS) + ".zip");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to prepare trace path", e);
        }
    }

    public static Path tracesDir() {
        try {
            Path dir = Path.of("target", "traces");
            Files.createDirectories(dir);
            return dir;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create traces directory", e);
        }
    }

    public static String safeFileName(String testName) {
        return testName == null ? "test" : testName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String artifactBaseName(String testName) {
        String testId = MDC.get("testId");
        String safeTest = safeFileName(testName);
        if (testId == null || testId.isBlank()) {
            return safeTest;
        }
        return safeFileName(testId) + "-" + safeTest;
    }
}
