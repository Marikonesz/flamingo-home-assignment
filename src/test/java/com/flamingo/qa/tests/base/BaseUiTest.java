package com.flamingo.qa.tests.base;

import com.flamingo.qa.browser.PlaywrightManager;
import com.flamingo.qa.helpers.common.TestContextExtension;
import com.flamingo.qa.helpers.common.TestLog;
import com.flamingo.qa.helpers.reporting.AllureHelper;
import com.flamingo.qa.helpers.sharding.Sharded;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Shared setup for UI tests: tags, sharding, browser lifecycle, failure artifacts, test-id logging.
 */
@Tag("ui")
@Sharded
@ExtendWith(TestContextExtension.class)
public abstract class BaseUiTest {

    @RegisterExtension
    final TestWatcher failureArtifacts = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            captureFailureArtifacts(context.getDisplayName());
        }

        @Override
        public void testAborted(ExtensionContext context, Throwable cause) {
            captureFailureArtifacts(context.getDisplayName());
        }
    };

    @BeforeEach
    void startPlaywright() {
        PlaywrightManager.startTest();
    }

    @AfterEach
    void stopPlaywright() {
        PlaywrightManager.endTest();
    }

    protected Page page() {
        return PlaywrightManager.page();
    }

    private void captureFailureArtifacts(String testName) {
        saveTraceOnFailure(testName);
        saveScreenshotOnFailure(testName);
        attachPageDebugInfo();
    }

    private void saveTraceOnFailure(String testName) {
        try {
            Path traceZip = AllureHelper.newTracePath(testName);
            PlaywrightManager.saveTrace(traceZip);
            if (Files.isRegularFile(traceZip)) {
                AllureHelper.attachFile("Playwright trace", traceZip, "application/zip", ".zip");
                TestLog.step("Saved Playwright trace", traceZip.toAbsolutePath().toString());
            } else {
                AllureHelper.attachText("Playwright trace", "Trace file was not created: " + traceZip);
            }
        } catch (Exception e) {
            AllureHelper.attachText(
                    "Trace save error",
                    Optional.ofNullable(e.getMessage()).orElse(e.toString()));
        }
    }

    private void saveScreenshotOnFailure(String testName) {
        try {
            Page page = PlaywrightManager.page();
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            AllureHelper.attachScreenshot("Failure screenshot", screenshot);
            AllureHelper.saveScreenshotToDisk(screenshot, testName);
        } catch (Exception e) {
            AllureHelper.attachText(
                    "Screenshot error",
                    Optional.ofNullable(e.getMessage()).orElse(e.toString()));
        }
    }

    private void attachPageDebugInfo() {
        try {
            Page page = PlaywrightManager.page();
            AllureHelper.attachText("Page URL", page.url());
            AllureHelper.attachText("Page HTML", page.content());
        } catch (Exception e) {
            AllureHelper.attachText(
                    "Page debug error",
                    Optional.ofNullable(e.getMessage()).orElse(e.toString()));
        }
    }
}
