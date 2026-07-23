package com.flamingo.qa.browser;

import com.flamingo.qa.config.ui.UiConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import java.nio.file.Path;

/**
 * Reuses one Browser per worker thread; opens a fresh Context/Page for each test.
 * Starts a Playwright trace for every UI test (saved on failure).
 */
public final class PlaywrightManager {

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> TRACE_STOPPED = new ThreadLocal<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(PlaywrightManager::shutdownCurrentThreadQuietly));
    }

    private PlaywrightManager() {
    }

    /**
     * Ensures a browser exists for this thread and opens a new isolated context + page.
     */
    public static void startTest() {
        ensureBrowser();
        closeContextQuietly();
        TRACE_STOPPED.remove();

        BrowserContext context = BROWSER.get().newContext(new Browser.NewContextOptions()
                .setViewportSize(1440, 900));
        context.setDefaultTimeout(UiConfig.defaultTimeoutMs());

        // Reduce DemoQA flakiness from third-party ads/analytics.
        context.route("**/*", route -> {
            String url = route.request().url();
            if (url.contains("googlesyndication")
                    || url.contains("doubleclick")
                    || url.contains("adservice")
                    || url.contains("carbonads")
                    || url.contains("google-analytics")
                    || url.contains("googletagmanager")) {
                route.abort();
            } else {
                route.resume();
            }
        });

        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        CONTEXT.set(context);
        PAGE.set(context.newPage());
    }

    /**
     * Stops tracing and writes a Playwright trace zip (open with `npx playwright show-trace`).
     */
    public static void saveTrace(Path traceZip) {
        BrowserContext context = CONTEXT.get();
        if (context == null || Boolean.TRUE.equals(TRACE_STOPPED.get())) {
            return;
        }
        context.tracing().stop(new Tracing.StopOptions().setPath(traceZip));
        TRACE_STOPPED.set(true);
    }

    /**
     * Closes only the current test context/page; keeps the browser for the next test on this thread.
     */
    public static void endTest() {
        discardTraceIfNeeded();
        closeContextQuietly();
        TRACE_STOPPED.remove();
    }

    public static Page page() {
        Page page = PAGE.get();
        if (page == null) {
            throw new IllegalStateException("Playwright test context is not started for current thread");
        }
        return page;
    }

    private static void discardTraceIfNeeded() {
        BrowserContext context = CONTEXT.get();
        if (context == null || Boolean.TRUE.equals(TRACE_STOPPED.get())) {
            return;
        }
        try {
            context.tracing().stop();
        } catch (Exception ignored) {
            // best-effort: context may already be closing
        }
        TRACE_STOPPED.set(true);
    }

    private static void ensureBrowser() {
        if (PLAYWRIGHT.get() != null) {
            return;
        }
        Playwright playwright = Playwright.create();
        Browser browser = BrowserFactory.launch(playwright);
        PLAYWRIGHT.set(playwright);
        BROWSER.set(browser);
    }

    private static void closeContextQuietly() {
        closeQuietly(PAGE.get());
        closeQuietly(CONTEXT.get());
        PAGE.remove();
        CONTEXT.remove();
    }

    private static void shutdownCurrentThreadQuietly() {
        discardTraceIfNeeded();
        closeContextQuietly();
        closeQuietly(BROWSER.get());
        closeQuietly(PLAYWRIGHT.get());
        BROWSER.remove();
        PLAYWRIGHT.remove();
        TRACE_STOPPED.remove();
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
            // best-effort cleanup
        }
    }
}
