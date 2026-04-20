---
title: "Вопросы на собеседовании: Selenium WebDriver"
description: "Selenium WebDriver: локаторы (By), ожидания (implicit/explicit/fluent), Page Object Model, параллельные тесты, Selenium Grid, альтернативы Playwright/Cypress"
tags:
  - interview
  - testing
  - selenium-interview
aliases:
  - "Selenium WebDriver interview"
  - "Selenium собеседование"
  - "UI testing interview"
  - "E2E testing interview"
  - "Page Object Model interview"
difficulty: "intermediate"
updated: "2026-04-20"
---
# Вопросы на собеседовании: `Selenium WebDriver`

`Selenium WebDriver` — стандартный инструмент для E2E тестирования веб-интерфейсов. Предоставляет API для управления браузером (Chrome, Firefox, Safari). Важная тема для Java-разработчиков, занимающихся автоматизацией UI или поддерживающих legacy тесты.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Selenium Docs](https://www.selenium.dev/documentation/) — официальная документация
- [Baeldung: Selenium](https://www.baeldung.com/java-selenium-with-junit-and-testng) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Selenium WebDriver и его архитектура?

**Selenium WebDriver** — библиотека для автоматизации браузеров через WebDriver protocol (W3C стандарт).

**Архитектура**:
```text
Test Code → Selenium WebDriver API → JSON Wire Protocol → Browser Driver → Browser
         (Java/Python/JS)            (HTTP)               (chromedriver)  (Chrome)
```

```java
// Базовый пример
WebDriver driver = new ChromeDriver();
driver.get("https://example.com");
WebElement button = driver.findElement(By.id("submit"));
button.click();
driver.quit();
```

**Компоненты**:
- **WebDriver** — API для управления браузером.
- **Browser Driver** (chromedriver, geckodriver, etc.) — мост между WebDriver и браузером.
- **Selenium Grid** — распределённое выполнение тестов.
- **Selenium IDE** — запись-воспроизведение (для быстрого прототипирования).

## Q2. Какие локаторы есть и когда какой использовать?

```java
driver.findElement(By.id("username"));           // ID — самый быстрый
driver.findElement(By.name("email"));             // name атрибут
driver.findElement(By.className("btn-primary")); // class
driver.findElement(By.tagName("button"));        // tag
driver.findElement(By.linkText("Click me"));     // полный текст ссылки
driver.findElement(By.partialLinkText("Click"));  // частичный текст
driver.findElement(By.cssSelector("button.primary[type='submit']"));
driver.findElement(By.xpath("//button[@type='submit' and contains(text(), 'Submit')]"));
```

**Приоритет при выборе локатора**:
1. **`id`** — быстрый, уникальный (если есть).
2. **`name`** — для форм.
3. **`cssSelector`** — предпочтительнее XPath (быстрее, читаемее).
4. **`xpath`** — только когда CSS не хватает (text-based поиск, navigation по иерархии).

**Плохая практика**: локаторы по visible text на разных языках — ломаются при локализации.

## Q3. В чём разница между implicit и explicit wait?

```java
// Implicit wait — глобальный таймаут для findElement
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

// Explicit wait — для конкретного элемента/условия
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
element.click();

// Fluent wait — с тонкой настройкой polling
Wait<WebDriver> fluentWait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))
    .pollingEvery(Duration.ofMillis(500))
    .ignoring(NoSuchElementException.class);

WebElement element = fluentWait.until(d -> d.findElement(By.id("result")));
```

| Тип wait | Область | Частые ошибки |
|----------|---------|---------------|
| Implicit | Все findElement вызовы | Глобально — сложно контролировать |
| Explicit | Конкретный элемент/условие | Самый гибкий |
| Fluent | Полный контроль polling | Для кастомных условий |

**НЕ рекомендуется смешивать** implicit и explicit wait — поведение непредсказуемо.

## Q4. Почему `Thread.sleep()` — плохая практика?

```java
// ПЛОХО
driver.findElement(By.id("load")).click();
Thread.sleep(5000);   // ждём фиксированное время
driver.findElement(By.id("result")).getText();
```

Проблемы:
1. **Медленно** — ждём 5 сек даже если элемент готов через 1 сек.
2. **Flaky** — при медленной сети 5 сек может быть мало.
3. **Не реагирует на изменения** — ждёт время, а не условие.

```java
// ХОРОШО
driver.findElement(By.id("load")).click();

WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement result = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("result")));
String text = result.getText();
```

`ExpectedConditions` — набор готовых проверок: `visibilityOf`, `invisibilityOf`, `elementToBeClickable`, `textToBePresentInElement`, `alertIsPresent`, `frameToBeAvailableAndSwitchToIt` и др.

## Q5. Что такое Page Object Pattern?

**Page Object** — паттерн, в котором каждая страница приложения представлена Java-классом. Локаторы и действия инкапсулированы.

```java
// PageObject
public class LoginPage {
    private final WebDriver driver;

    @FindBy(id = "username")       // PageFactory аннотация
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public DashboardPage login(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        submitButton.click();
        return new DashboardPage(driver);   // возврат следующей страницы
    }
}

// Тест
@Test
void shouldLogin() {
    driver.get("https://example.com/login");

    LoginPage loginPage = new LoginPage(driver);
    DashboardPage dashboard = loginPage.login("alice", "password");

    assertThat(dashboard.getWelcomeMessage()).contains("Welcome, Alice");
}
```

**Преимущества**:
- **Переиспользование** — `login()` доступен из любого теста.
- **Maintenance** — при изменении UI локаторы меняются в одном месте.
- **Читаемость** — тест читается как сценарий.

## Q6. Что такое Page Factory и как работают @FindBy?

```java
public class UserPage {
    @FindBy(id = "user-name")
    private WebElement userName;

    @FindBy(css = ".user-avatar img")
    private WebElement avatar;

    @FindBys({
        @FindBy(className = "list-item"),
        @FindBy(tagName = "a")
    })
    private List<WebElement> listLinks;

    @FindAll({
        @FindBy(id = "primary-button"),
        @FindBy(id = "secondary-button")
    })
    private WebElement anyButton;

    public UserPage(WebDriver driver) {
        PageFactory.initElements(driver, this);  // инициализирует поля с @FindBy
    }
}
```

**Особенность PageFactory**: `WebElement` поля — lazy proxy. Реальный поиск происходит при каждом обращении (решает StaleElementReferenceException от переходов).

## Q7. Как работать с alerts, frames, windows?

```java
// Alerts
driver.findElement(By.id("trigger-alert")).click();
Alert alert = driver.switchTo().alert();
System.out.println(alert.getText());
alert.sendKeys("Text for prompt");  // только для prompt
alert.accept();                      // или dismiss()

// Frames
driver.switchTo().frame("frameName");          // по name/id
driver.switchTo().frame(0);                     // по индексу
driver.switchTo().frame(driver.findElement(By.css("iframe")));  // по элементу
// ...работа внутри фрейма
driver.switchTo().defaultContent();             // возврат в top-level

// Windows / Tabs
String mainWindow = driver.getWindowHandle();

driver.findElement(By.id("open-new")).click();

Set<String> windows = driver.getWindowHandles();
for (String handle : windows) {
    if (!handle.equals(mainWindow)) {
        driver.switchTo().window(handle);
        break;
    }
}
// ...работа в новом окне
driver.close();                        // закрыть текущее
driver.switchTo().window(mainWindow);  // вернуться
```

## Q8. Что такое Selenium Grid и когда его использовать?

**Selenium Grid** — distributed execution для параллельного запуска тестов на разных машинах/браузерах.

```text
                     Hub (Grid 4: Router + Distributor)
                     /      |           \
              Node 1      Node 2        Node 3
           (Chrome)    (Firefox)      (Safari mac)
```

```java
// Запуск на Grid
ChromeOptions options = new ChromeOptions();
WebDriver driver = new RemoteWebDriver(
    new URL("http://selenium-hub:4444/wd/hub"),
    options
);
```

```bash
# Запуск Hub
java -jar selenium-server-4.x.x.jar hub

# Запуск Node
java -jar selenium-server-4.x.x.jar node --hub http://hub:4444
```

**Применение**:
- **Cross-browser testing** — Chrome, Firefox, Safari, Edge одновременно.
- **Параллелизация** — 100 тестов на 10 nodes → 10x ускорение.
- **Распределение по OS** — Windows, Linux, macOS.

Альтернативы: Docker Selenium, Selenoid, BrowserStack, Sauce Labs.

## Q9. Как запускать тесты параллельно?

```java
// JUnit 5 — параллельные тесты через junit-platform.properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.config.fixed.parallelism=4

// Но WebDriver обычно НЕ thread-safe → каждый тест свой driver
@BeforeEach
void setUp() {
    driver = new ChromeDriver();  // новый driver на каждый тест
}

@AfterEach
void tearDown() {
    if (driver != null) driver.quit();
}
```

```xml
<!-- TestNG — проще для параллельных UI тестов -->
<suite name="UI Tests" parallel="methods" thread-count="4">
    <test name="Tests">
        <classes>
            <class name="com.example.LoginTest"/>
            <class name="com.example.CheckoutTest"/>
        </classes>
    </test>
</suite>
```

```java
// ThreadLocal WebDriver для параллельного выполнения
public class DriverFactory {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) driver.set(new ChromeDriver());
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
```

## Q10. Как обрабатывать флейки (flaky) тесты?

```java
// 1. Retry аннотация
@Test
@RetryingTest(3)   // JUnit Pioneer
void flaky() { ... }

// 2. Явные ожидания вместо sleep
wait.until(ExpectedConditions.elementToBeClickable(element));

// 3. Стабильные локаторы (id, data-testid вместо CSS классов)
@FindBy(css = "[data-testid='submit-button']")   // не ломается при CSS рефакторинге
WebElement submitButton;

// 4. Скриншоты при падении для диагностики
@AfterEach
void captureScreenshotOnFailure(TestInfo info) {
    if (testFailed) {
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot,
            new File("screenshots/" + info.getDisplayName() + ".png"));
    }
}

// 5. Фильтрация flaky из CI
@Tag("flaky")
@Disabled("Flaky — tracked in JIRA-123")
void stillFlakyTest() { ... }
```

## Q11. Какие Selenium-альтернативы существуют?

| Инструмент | Плюсы | Минусы |
|------------|-------|--------|
| **Selenium** | Зрелый, широкая поддержка | Slow, flaky |
| **Playwright** | Быстрее, auto-wait, multi-browser | Моложе (2020) |
| **Cypress** | Простой, живой debug | Только Chrome-based |
| **Puppeteer** | Chrome-only | Нет multi-browser |
| **WebDriverIO** | Node.js, mocha-стиль | Меньше Java-поддержки |
| **TestCafe** | Без WebDriver, auto-wait | Моложе, меньше плагинов |

**Playwright набирает популярность**:

```java
// Playwright Java
try (Playwright playwright = Playwright.create()) {
    Browser browser = playwright.chromium().launch();
    Page page = browser.newPage();
    page.navigate("https://example.com");
    page.locator("button[type=submit]").click();  // auto-wait
    page.waitForURL("**/dashboard");
}
```

## Q12. Как организовать тесты в CI/CD?

```yaml
# GitHub Actions
name: UI Tests
on: [push, pull_request]
jobs:
  selenium-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with: { java-version: '17' }

      - name: Run UI tests
        run: ./mvnw test -Dtest="*UITest"

      - name: Upload screenshots
        if: failure()
        uses: actions/upload-artifact@v3
        with:
          name: screenshots
          path: screenshots/
```

```java
// Headless browser для CI
ChromeOptions options = new ChromeOptions();
options.addArguments(
    "--headless=new",
    "--no-sandbox",
    "--disable-dev-shm-usage",
    "--window-size=1920,1080"
);
WebDriver driver = new ChromeDriver(options);
```

**WebDriverManager** — автоматическое управление драйверами:

```java
// Раньше: вручную скачивать chromedriver, указывать путь
// С WebDriverManager:
WebDriverManager.chromedriver().setup();  // скачивает нужную версию
WebDriver driver = new ChromeDriver();
```

## Q13. Что такое BDD тесты с Cucumber?

**Cucumber + Selenium** = BDD UI тесты:

```gherkin
# login.feature
Feature: User Login

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I enter username "alice"
    And I enter password "secret123"
    And I click the login button
    Then I should see the dashboard
    And I should see welcome message "Welcome, Alice"
```

```java
// Step definitions
public class LoginSteps {
    private WebDriver driver;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;

    @Given("I am on the login page")
    public void onLoginPage() {
        driver.get("https://example.com/login");
        loginPage = new LoginPage(driver);
    }

    @When("I enter username {string}")
    public void enterUsername(String username) {
        loginPage.setUsername(username);
    }

    @Then("I should see welcome message {string}")
    public void shouldSeeMessage(String message) {
        assertThat(dashboardPage.getWelcomeMessage()).isEqualTo(message);
    }
}
```

**Применение**: когда QA/BA пишут сценарии на естественном языке.

## Q14. Какие Selenium anti-patterns стоит избегать?

1. **Thread.sleep()** вместо WebDriverWait.

2. **Хрупкие локаторы** — XPath по tree position:

```java
// ПЛОХО
By.xpath("/html/body/div[2]/div[3]/table/tr[2]/td[5]")

// ХОРОШО
By.cssSelector("[data-testid='user-email']")
```

3. **Тесты с shared state** — изменения предыдущего теста влияют на следующий.

4. **UI тесты как замена unit тестам** — медленные, flaky, дорогие в поддержке.

5. **Проверка логики через UI** — тестировать через API быстрее:

```java
// ПЛОХО (20 секунд)
loginPage.login("alice", "pass");
dashboardPage.navigateToOrders();
ordersPage.filterByStatus("PENDING");
assertThat(ordersPage.getOrderCount()).isEqualTo(5);

// ХОРОШО — API тест
Response response = given().auth().basic("alice", "pass")
    .when().get("/api/orders?status=PENDING")
    .then().extract().response();
assertThat(response.jsonPath().getList("items")).hasSize(5);
```

6. **Отсутствие cleanup** — driver не quit() → утечка процессов.

## Q15. Когда использовать UI-тесты, а когда — нет?

**Использовать UI-тесты для**:
- **Critical user journeys** — login, checkout, signup.
- **Integration между UI и backend** — данные от API корректно отображаются.
- **Визуальные проверки** — элементы на месте, responsive design.
- **Smoke tests** — приложение стартует и основные фичи работают.

**НЕ использовать UI-тесты для**:
- **Бизнес-логика** — тестируется unit/integration тестами на backend.
- **Edge cases** — перегружают время прогона.
- **Тестирование API** — прямые API-тесты быстрее и надёжнее.

**Test pyramid**:
```text
        /\     UI tests (10-15%)
       /  \    Integration tests (20-30%)
      /    \   Unit tests (60-70%)
     /______\
```

UI-тесты — **дорогие** (медленные, flaky), должны быть **минимально достаточными**.

## See also

- [REST Assured](rest-assured-interview.md) — API-level тестирование (дополняет UI)
- [Test Automation](test-automation-interview.md) — автоматизация в CI/CD
- [Integration Testing](integration-testing-interview.md) — стратегии интеграционного тестирования
- [Test Strategies](test-strategies-interview.md) — пирамида тестирования
- [JUnit 5](junit-interview.md) — JUnit для запуска Selenium тестов
- [Testcontainers](testcontainers-interview.md) — Selenium в Docker через Testcontainers
- [Mockito](mockito-interview.md) — mocking в unit-тестах сервисов
- [Contract Testing](contract-testing-interview.md) — альтернатива UI-тестам для contract validation
- [Performance Testing](../performance/performance-testing-interview.md) — UI performance через Selenium + Lighthouse
- [Chaos Engineering](chaos-engineering-interview.md) — отдельная область тестирования устойчивости
