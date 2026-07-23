# QA Automation Test Suite

Automated API (REST + GraphQL) and UI tests for the Flamingo QA home assignment.

## Prerequisites

- Java 11+
- Maven 3.6+
- Chrome/Chromium is the default UI browser (Firefox and WebKit also supported via Playwright)

Install Playwright browsers once:

```bash
mvn -q exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
# optional:
# mvn -q exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install firefox"
# mvn -q exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install webkit"
```

## How to Run

```bash
# Run all tests
mvn clean test

# Run only API tests
mvn test -Dgroups=api

# Run only UI tests
mvn test -Dgroups=ui

# Choose browser (default: chromium)
mvn test -Dgroups=ui -Dui.browser=firefox
mvn test -Dgroups=ui -Dui.browser=webkit

# Suite-wide sharding (entire suite split across N JVMs)
mvn test -Dshard.total=3 -Dshard.index=0
mvn test -Dshard.total=3 -Dshard.index=1
mvn test -Dshard.total=3 -Dshard.index=2

# Parallel threads inside one shard/JVM (default: 2)
mvn test -Dthreads=4

# Re-run failed tests once (Surefire; like Playwright Test retries — default 1)
mvn test -Dretry.count=1
```

**Debugging logs:** every line includes TMS id (`[FLA-…]`). HTTP request/response go through SLF4J; UI/API steps as `→ ...`.

On UI failure:
- screenshot → Allure + `target/screenshots/`
- Playwright trace zip → Allure attachment + `target/traces/` (CI artifact `playwright-traces-shard-*`)

Open locally: `npx playwright show-trace target/traces/<file>.zip`

### Allure report (local)

```bash
mvn clean test
mvn allure:serve
# or
mvn allure:report
open target/site/allure-maven-plugin/index.html
```

## Architecture

Layers first; `models` hold descriptive objects only (pages, components, request/response):

```
config/
  api/           # ApiConfig
  ui/            # UiConfig, BrowserType
  ConfigProperties, ExecutionConfig
api/
  client/        # shared RestClient, GraphQlClient (one per JVM/shard)
  service/       # AuthService (cached token), BookingService, MovieGraphQlService
browser/         # BrowserFactory, PlaywrightManager (reuse browser, fresh context/test)
models/
  api/           # request/response POJOs
  ui/
    page/        # Page Objects (PracticeFormPage, BasePage)
    component/   # FormSuccessModal
helpers/
  api/           # JsonHelper
  reporting/     # AllureHelper
  sharding/      # suite-wide shard filter (@Sharded)
tests/
  base/          # BaseApiTest, BaseBookingApiTest (booking cleanup), BaseUiTest
  api/rest|graphql
  ui/
```

Tests extend base classes and call services/pages — not raw drivers.

## Test Strategy

- **REST (Restful Booker):** self-contained auth + CRUD; AssertJ on payloads; DELETE expects HTTP 201.
- **GraphQL (Hygraph Video):** pagination/variables, single entity by id, fragments + nested `publishedBy`, plus negative cases (invalid id, malformed query, unknown field).
- **UI (DemoQA Practice Form):** POM for form fill, file upload, date picker, state/city dropdowns, submit + success modal; screenshot + Playwright trace on failure; Surefire re-runs failed tests (`-Dretry.count`).
- Prioritized maintainable architecture and meaningful assertions over raw test count.

### Test case IDs (TMS)

Each test has a unique id via JUnit `@Tag` + Allure `@TmsLink` (same value):

| ID | Area |
|----|------|
| `FLA-REST-001` … `004` | Restful Booker auth/CRUD |
| `FLA-GQL-001` … `007` | Hygraph GraphQL |
| `FLA-UI-001` … `002` | Practice Form |

Example: `mvn test -Dgroups=FLA-UI-001`

## Challenges & Solutions

- Restful Booker resets data periodically → unique payloads + cleanup in `@AfterEach`.
- Hygraph returns HTTP 400 for parse/validation errors (not always 200) → assert status and `errors`/`data` shape.
- DemoQA ads can intercept clicks → PlaywrightManager blocks known ad domains.
- Practice form uses custom widgets (react-datepicker, react-select) → interact via native select options / keyboard Enter rather than brittle XPaths.
- Parallel workers need isolated UI sessions → reuse Browser per thread, new Context/Page per test.
- Shared stateless API clients per shard; admin token cached after first auth.

## CI / GitHub Pages

GitHub Actions workflow [`.github/workflows/ci.yml`](.github/workflows/ci.yml):

- Splits the **entire suite** into shards (`shard.total` / `shard.index`)
- Runs `-Dthreads` workers inside each shard
- Uses `-Dui.browser` (default `chromium`) for all shards
- Merges Allure results, restores previous `history/` for trends
- Publishes to GitHub Pages:

  - `/allure/latest/` — latest report (trends source)
  - `/allure/history/run-<number>-<sha>/` — archived runs
  - `/allure/index.html` — history index

After the first successful run on `main`/`master`, enable GitHub Pages for the `gh-pages` branch.

Report URL pattern:

`https://<user>.github.io/<repo>/allure/latest/`

## What I Would Add With More Time

- Contract tests / OpenAPI validation for Restful Booker
- Soft assertions and richer Allure categories
- Visual regression for Practice Form
- Data-driven negative GraphQL matrix
