# QA Automation Test Suite

REST (Restful Booker) + GraphQL (Hygraph Video) + UI (DemoQA Practice Form) on JUnit 5, Rest Assured, Playwright, AssertJ, Allure.

## Prerequisites

- Java 11+
- Maven 3.6+
- Chrome / Chromium browser (Firefox and WebKit also supported via Playwright)

```bash
mvn -q exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

## How to Run

```bash
# Run all tests
mvn clean test

# Run only API tests
mvn test -Dgroups=api

# Run only UI tests
mvn test -Dgroups=ui

# Optional
mvn test -Dgroups=ui -Dui.browser=firefox
mvn test -Dthreads=4
mvn test -Dshard.total=3 -Dshard.index=0
mvn test -Dretry.count=1
mvn test -Dgroups=FLA-UI-001
```

Logs include TMS id (`[FLA-…]`). On UI failure: screenshot + Playwright trace → Allure and `target/screenshots|traces/`.

```bash
mvn allure:serve
# or: mvn allure:report && open target/site/allure-maven-plugin/index.html
npx playwright show-trace target/traces/<file>.zip
```

## Architecture

```
config/          # ApiConfig, UiConfig, ExecutionConfig
api/client|service/
browser/         # BrowserFactory, PlaywrightManager (browser reuse, fresh context/test)
models/api|ui/   # POJOs, Page Objects, components
helpers/         # JSON, HTTP logs, booking cleanup, sharding, Allure
tests/base|api|ui/
```

Tests call services/pages via `BaseApiTest` / `BaseUiTest` — not raw Rest Assured / Playwright.

## Test Strategy

Prioritized framework architecture and maintainable scenarios over raw test count.

- **REST:** self-contained auth + CRUD with AssertJ on payloads; DELETE expects HTTP 201; unique data + `@AfterEach` cleanup.
- **GraphQL (Video):** pagination/variables, entity by id, fragment + nested `publishedBy`; negatives for invalid id, malformed query, unknown field.
- **UI (Option A — Practice Form):** POM covering fill, file upload, date picker, dropdowns, success modal; plus validation negatives (empty required, invalid email/mobile).
- **Stability:** Playwright auto-wait + expect timeouts; AssertJ soft assertions for multi-field checks; screenshots/traces on UI failure; Surefire retries (`-Dretry.count`).

TMS ids: `FLA-REST-001…004`, `FLA-GQL-001…007`, `FLA-UI-001…004` (`@Tag` + `@TmsLink`).

## Challenges & Solutions

- Restful Booker resets data periodically → unique payloads + booking cleanup in `@AfterEach`.
- Hygraph may return HTTP 400 for parse/validation errors (not always 200) → assert status and `errors`/`data` shape.
- DemoQA ads intercept clicks → block known ad domains in `PlaywrightManager`.
- Subjects field: Enter can submit the whole form → select autocomplete option by click.
- Parallel workers need isolated UI sessions → reuse Browser per thread, new Context/Page per test.
- Sharded CI polluted Allure with skips → exclude foreign-shard tests at JUnit discovery (`PostDiscoveryFilter`).

## CI / Allure Pages

Workflow: **Actions → Flamingo QA Automation** (browser / threads / shards).

- Suite-wide shards; merge Allure → GitHub Pages
- Job **Summary** lists Allure URLs; artifact `allure-report` always uploaded

**Report:** https://marikonesz.github.io/flamingo-home-assignment/allure/latest/  
History: `/allure/` · archives: `/allure/history/run-<n>-<sha>/` · root redirects to latest.

Pages source: **Settings → Pages → GitHub Actions**. History tab needs ≥2 publishes.

## What I Would Add With More Time

- `@ParameterizedTest` matrix for GraphQL negatives / UI validation
- Richer Allure categories (product vs infra failures)
- Contract / OpenAPI checks for Restful Booker
- Visual regression for Practice Form
- Optional Web Tables suite (assignment Option B) alongside the form
