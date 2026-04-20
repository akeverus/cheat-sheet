---
title: "Cucumber для Java"
description: "BDD-фреймворк для написания автотестов на естественном языке (Gherkin). Связывает бизнес-описания сценариев с исполняемым кодом."
tags:
  - testing
  - cucumber
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Cucumber для Java

BDD-фреймворк для написания автотестов на естественном языке (Gherkin). Связывает бизнес-описания сценариев с исполняемым кодом.

## Полезные ссылки

- [Cucumber Documentation](https://cucumber.io/docs/cucumber/) — основная документация
- [Gherkin Reference](https://cucumber.io/docs/gherkin/reference/) — синтаксис Gherkin
- [Cucumber Java](https://cucumber.io/docs/cucumber/getting-started/) — быстрый старт
- [BDD Best Practices](https://cucumber.io/docs/best-practices/) — лучшие практики

### См. также

- [[junit-advanced|JUnit Advanced]]
- [[rest-assured|REST Assured]]
- [[selenium|Selenium]]


## Содержание

- [Зачем Cucumber](#зачем-cucumber)
- [Подключение](#подключение)
  - [Maven (основные зависимости)](#maven-основные-зависимости)
  - [Gradle](#gradle)
  - [Запуск](#запуск)
- [Gherkin-синтаксис](#gherkin-синтаксис)
  - [Пример feature-файла](#пример-feature-файла)
  - [Rule (Cucumber 6+)](#rule-cucumber-6)
- [Step definitions](#step-definitions)
- [Data tables](#data-tables)
- [Scenario Outline](#scenario-outline)
- [Hooks и жизненный цикл](#hooks-и-жизненный-цикл)
- [Интеграция со Spring Boot](#интеграция-со-spring-boot)
  - [Конфигурация](#конфигурация)
  - [Общий контекст между шагами](#общий-контекст-между-шагами)
  - [API-тесты через Spring](#api-тесты-через-spring)
- [Параллельное выполнение](#параллельное-выполнение)
  - [JUnit 5 (junit-platform.properties)](#junit-5-junit-platformproperties)
  - [Maven Surefire](#maven-surefire)
  - [Потокобезопасные step definitions](#потокобезопасные-step-definitions)
- [Пользовательские типы параметров](#пользовательские-типы-параметров)
- [Рекомендации по организации проекта](#рекомендации-по-организации-проекта)
  - [Структура файлов](#структура-файлов)
  - [Правила хороших сценариев](#правила-хороших-сценариев)
  - [Ключевые принципы](#ключевые-принципы)
- [Решение проблем](#решение-проблем)
  - [Отладка: скриншот при падении](#отладка-скриншот-при-падении)
- [Когда использовать и когда нет](#когда-использовать-и-когда-нет)
- [Итоговые таблицы](#итоговые-таблицы)
  - [Основные аннотации Cucumber](#основные-аннотации-cucumber)
  - [Ключевые слова Gherkin](#ключевые-слова-gherkin)
  - [Конфигурация @CucumberOptions](#конфигурация-cucumberoptions)

## Зачем Cucumber

Cucumber решает проблему разрыва между бизнесом и разработкой: сценарии пишутся на человеческом языке (Gherkin), но при этом являются исполняемым кодом. Бизнес-аналитик может прочитать и понять тест, а разработчик — запустить его.

Основные преимущества:
- **Живая документация** — тесты всегда актуальны и описывают реальное поведение системы
- **Общий язык** — бизнес и разработка говорят на одном языке
- **Переиспользование шагов** — один раз написанный step definition используется в любых сценариях
- **Параметризация** — Scenario Outline позволяет проверять десятки вариантов одним сценарием


## Подключение

### Maven (основные зависимости)

```xml
<properties>
    <cucumber.version>7.14.0</cucumber.version>
</properties>

<dependencies>
    <!-- Ядро Cucumber -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
        <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>

    <!-- JUnit 5 -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
        <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>

    <!-- Spring (опционально) -->
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
        <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>

    <!-- HTML/JSON отчёты (опционально) -->
<dependency>
    <groupId>net.masterthought</groupId>
    <artifactId>cucumber-reporting</artifactId>
    <version>5.7.7</version>
    <scope>test</scope>
</dependency>
</dependencies>
```

### Gradle

```groovy
ext {
    cucumberVersion = '7.14.0'
}

dependencies {
    testImplementation "io.cucumber:cucumber-java:${cucumberVersion}"
    testImplementation "io.cucumber:cucumber-junit-platform-engine:${cucumberVersion}"
    testImplementation "io.cucumber:cucumber-spring:${cucumberVersion}"      // опционально
    testImplementation "net.masterthought:cucumber-reporting:5.7.7"          // опционально
    testImplementation "org.junit.jupiter:junit-jupiter:5.10.0"
}
```

### Запуск

```java
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.example.stepdefinitions",
    plugin = {"pretty", "html:target/cucumber-reports.html", "json:target/cucumber.json"},
    tags = "not @Ignore"
)
public class CucumberTestRunner {}
```


## Gherkin-синтаксис

Gherkin — язык описания поведения. Ключевые слова: `Feature`, `Scenario`, `Given`/`When`/`Then`, `And`/`But`, `Background`, `Scenario Outline`, `Examples`, `Rule`.

### Пример feature-файла

```gherkin
# src/test/resources/features/user-management.feature
@UserManagement
Feature: Управление пользователями
  Как администратор
  Я хочу управлять учётными записями
  Чтобы контролировать доступ к системе

  Background:
    Given система запущена
    And я авторизован как администратор

  @SmokeTest
  Scenario: Создание нового пользователя
    Given я на странице управления пользователями
    When я нажимаю кнопку "Создать пользователя"
    And заполняю данные:
      | Поле   | Значение         |
      | Логин  | john.doe         |
      | Email  | john@example.com |
      | Роль   | User             |
    And нажимаю "Сохранить"
    Then я вижу сообщение "Пользователь создан"
    And пользователь "john.doe" появился в списке

  @RegressionTest
  Scenario Outline: Проверка прав по ролям
    Given пользователь с ролью "<роль>"
    When он пытается открыть "<ресурс>"
    Then доступ должен быть "<результат>"

    Examples:
      | роль   | ресурс        | результат |
      | Admin  | /admin/users  | разрешён  |
      | User   | /admin/users  | запрещён  |
      | User   | /user/profile | разрешён  |
      | Guest  | /user/profile | запрещён  |
```

### Rule (Cucumber 6+)

`Rule` группирует сценарии по бизнес-правилу:

```gherkin
Feature: Правила интернет-магазина

  Rule: Бесплатная доставка при заказе от 5000 руб.

    Example: Заказ на 7500 руб.
      Given в корзине товаров на 7500 руб.
      When я оформляю заказ
      Then стоимость доставки = 0 руб.

    Example: Заказ на 2500 руб.
      Given в корзине товаров на 2500 руб.
      When я оформляю заказ
      Then стоимость доставки = 299 руб.
```


## Step definitions

Step definition — Java-метод, привязанный к Gherkin-шагу через аннотацию и регулярное выражение (или Cucumber expression).

```java
@SpringBootTest
@CucumberContextConfiguration
public class UserSteps {

    @Autowired
    private UserService userService;

    @Autowired
    private WebDriver driver;

    @Given("я авторизован как администратор")
    public void loginAsAdmin() {
        driver.get("http://localhost:8080/login");
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin123");
        driver.findElement(By.id("login-button")).click();

        WebElement adminPanel = driver.findElement(By.id("admin-panel"));
        assertThat(adminPanel.isDisplayed()).isTrue();
    }

    @When("я нажимаю кнопку {string}")
    public void clickButton(String buttonText) {
        WebElement button = driver.findElement(
            By.xpath("//button[text()='" + buttonText + "']"));
        button.click();
    }

    @When("заполняю данные:")
    public void fillUserDetails(DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        driver.findElement(By.id("username")).sendKeys(data.get("Логин"));
        driver.findElement(By.id("email")).sendKeys(data.get("Email"));

        Select roleSelect = new Select(driver.findElement(By.id("role")));
        roleSelect.selectByVisibleText(data.get("Роль"));
    }

    @Then("я вижу сообщение {string}")
    public void verifyMessage(String message) {
        WebElement msg = driver.findElement(By.className("success-message"));
        assertThat(msg.getText()).contains(message);
    }

    @Then("пользователь {string} появился в списке")
    public void verifyUserInList(String username) {
        WebElement userList = driver.findElement(By.id("user-list"));
        assertThat(userList.getText()).contains(username);
    }
}
```


## Data tables

Data table — табличные данные внутри шага. Способы преобразования:

| Метод                 | Результат                       | Когда использовать              |
|-----------------------|---------------------------------|---------------------------------|
| `asMap(K, V)`         | `Map<K, V>`                     | Ключ-значение (2 колонки)       |
| `asMaps(K, V)`        | `List<Map<K, V>>`              | Несколько строк с заголовками   |
| `asList(T)`           | `List<T>`                       | Одна колонка                    |
| `asLists(T)`          | `List<List<T>>`                | Несколько колонок без заголовков |

```java
@When("я создаю пользователей:")
public void createUsers(DataTable dataTable) {
    List<Map<String, String>> users = dataTable.asMaps(String.class, String.class);

    for (Map<String, String> userData : users) {
        User user = new User();
        user.setUsername(userData.get("username"));
        user.setEmail(userData.get("email"));
        user.setRole(UserRole.valueOf(userData.get("role").toUpperCase()));
        userService.createUser(user);
    }
}
```


## Scenario Outline

Параметризованный сценарий — один шаблон, много наборов данных через `Examples`:

```gherkin
Feature: Валидация кредитных карт

  Scenario Outline: Проверка номера карты
    When я проверяю карту "<номер>"
    Then результат = "<результат>", тип = "<тип>"

    Examples: Валидные карты
      | номер            | результат | тип        |
      | 4111111111111111 | valid     | Visa       |
      | 5555555555554444 | valid     | Mastercard |
      | 378282246310005  | valid     | Amex       |

    Examples: Невалидные карты
      | номер            | результат | тип     |
      | 1234567890123456 | invalid   | Unknown |
      | 4111111111111112 | invalid   | Visa    |
```

```java
@When("я проверяю карту {string}")
public void validateCard(String cardNumber) {
    result = paymentService.validateCreditCard(cardNumber);
}

@Then("результат = {string}, тип = {string}")
public void verifyResult(String expectedResult, String expectedType) {
    assertThat(result.isValid()).isEqualTo("valid".equals(expectedResult));
    assertThat(result.getCardType()).isEqualTo(expectedType);
}
```


## Hooks и жизненный цикл

Hooks выполняются до/после сценариев и шагов. Поддерживают фильтрацию по тегам.

```java
public class TestHooks {

    @Autowired
    private WebDriver driver;

    @Before
    public void setUp() {
        driver.manage().deleteAllCookies();
        driver.get("about:blank");
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed() && driver instanceof TakesScreenshot) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot.png");
        }
    }

    // Хук только для сценариев с тегом @WebUI
    @Before("@WebUI")
    public void setUpBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @After("@WebUI")
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Хук для мок-сервисов
    @Before("@MockExternalServices")
    public void startMocks() {
        mockServer.start();
        mockServer.loadMappings("test-mappings");
    }

    @After("@MockExternalServices")
    public void stopMocks() {
        if (mockServer.isRunning()) mockServer.stop();
    }
}
```

Порядок выполнения:

```text
@BeforeAll → @Before → @BeforeStep → шаг → @AfterStep → ... → @After → @AfterAll
```


## Интеграция со Spring Boot

### Конфигурация

```java
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfig {}
```

### Общий контекст между шагами

Для передачи данных между step definitions используется `@Scope("cucumber-glue")`:

```java
@Component
@Scope("cucumber-glue")
public class TestContext {

    private ResponseEntity<String> lastResponse;
    private User currentUser;
    private Map<String, Object> data = new HashMap<>();

    public ResponseEntity<String> getLastResponse() { return lastResponse; }
    public void setLastResponse(ResponseEntity<String> r) { this.lastResponse = r; }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User u) { this.currentUser = u; }

    public void put(String key, Object value) { data.put(key, value); }
    public <T> T get(String key, Class<T> type) { return type.cast(data.get(key)); }
}
```

### API-тесты через Spring

```java
public class ApiSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestContext ctx;

    @LocalServerPort
    private int port;

    @When("я делаю GET-запрос на {string}")
    public void getRequest(String endpoint) {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + endpoint, String.class);
        ctx.setLastResponse(response);
    }

    @Then("статус ответа = {int}")
    public void verifyStatus(int expected) {
        assertThat(ctx.getLastResponse().getStatusCodeValue()).isEqualTo(expected);
    }
}
```


## Параллельное выполнение

### JUnit 5 (junit-platform.properties)

```properties
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.config.strategy = dynamic
```

### Maven Surefire

```xml
<plugin>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M9</version>
    <configuration>
        <parallel>classes</parallel>
        <threadCount>4</threadCount>
    </configuration>
</plugin>
```

### Потокобезопасные step definitions

При параллельном запуске `WebDriver` нужно хранить в `ThreadLocal`:

```java
public class ThreadSafeSteps {

    private static final ThreadLocal<WebDriver> driverLocal = new ThreadLocal<>();

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox");
        driverLocal.set(new ChromeDriver(options));
    }

    @After
    public void tearDown() {
        WebDriver driver = driverLocal.get();
        if (driver != null) {
            driver.quit();
            driverLocal.remove();
        }
    }

    private WebDriver driver() { return driverLocal.get(); }

    @Given("я на странице входа")
    public void openLoginPage() {
        driver().get("http://localhost:8080/login");
    }
}
```


## Пользовательские типы параметров

```java
public class ParameterTypes {

    @ParameterType("admin|user|guest")
    public UserRole userRole(String role) {
        return UserRole.valueOf(role.toUpperCase());
    }

    @ParameterType("\\d{4}-\\d{2}-\\d{2}")
    public LocalDate date(String date) {
        return LocalDate.parse(date);
    }

    @ParameterType("\\d+\\.\\d{2}")
    public BigDecimal currency(String amount) {
        return new BigDecimal(amount);
    }
}
```

Также можно настроить глобальный трансформер для DataTable:

```java
public class CucumberConfig {

    @DefaultParameterTransformer
    @DefaultDataTableEntryTransformer
    @DefaultDataTableCellTransformer
    public Object transformer(Object fromValue, Type toValueType) {
        if (toValueType == BigDecimal.class && fromValue instanceof String)
            return new BigDecimal((String) fromValue);
        if (toValueType == LocalDate.class && fromValue instanceof String)
            return LocalDate.parse((String) fromValue);
        return fromValue;
    }
}
```


## Рекомендации по организации проекта

### Структура файлов

```text
src/test/resources/features/
├── authentication/
│   ├── login.feature
│   └── password-reset.feature
├── user-management/
│   ├── user-registration.feature
│   └── user-profile.feature
└── ecommerce/
    ├── shopping-cart.feature
    └── checkout.feature

src/test/java/com/example/
├── config/
│   └── CucumberSpringConfig.java
├── hooks/
│   └── TestHooks.java
├── context/
│   └── TestContext.java
└── stepdefinitions/
    ├── common/
    │   ├── NavigationSteps.java
    │   └── AuthSteps.java
    ├── user/
    │   └── UserManagementSteps.java
    └── order/
        └── OrderSteps.java
```

### Правила хороших сценариев

**Хорошо** — декларативный, бизнес-ориентированный стиль:

```gherkin
Scenario: Успешный вход
  Given я на странице входа
  When я ввожу валидные учётные данные
  Then я попадаю на главную страницу
```

**Плохо** — императивный, UI-привязанный стиль:

```gherkin
Scenario: Тест формы входа
  Given я перехожу на "/login"
  When я ввожу "user@test.com" в поле "#email"
  And я ввожу "password" в поле "#password"
  And я нажимаю на "#login-btn"
  Then я вижу элемент ".success-message"
```

### Ключевые принципы

- **Один сценарий — одно поведение.** Не смешивайте несколько проверок.
- **Шаги — переиспользуемые.** Выносите общие шаги в `common/`.
- **Background** — для общих предусловий, но не перегружайте (2-3 шага максимум).
- **Теги** — используйте `@SmokeTest`, `@RegressionTest`, `@API`, `@WebUI` для фильтрации запуска.
- **Данные** — чувствительные данные (пароли) — через переменные окружения, не хардкодить в `.feature`.


## Решение проблем

| Проблема | Причина | Решение |
|----------|---------|---------|
| `Undefined step` | Шаг в `.feature` не совпадает с аннотацией | Проверить текст шага; запустить с `dryRun = true` для генерации заглушек |
| `Duplicate step definition` | Два метода с одинаковым выражением | Убедиться, что выражения уникальны; использовать параметры вместо копирования |
| `Step definitions not found` | Неправильный `glue` path | В `@CucumberOptions` указать пакет со step definitions |
| `Spring context not loaded` | Нет `@CucumberContextConfiguration` | Добавить `@CucumberContextConfiguration` + `@SpringBootTest` на конфиг-класс |
| Flaky-тесты (мерцающие) | Race condition, нет явных ожиданий | Использовать `WebDriverWait` + `ExpectedConditions` вместо `Thread.sleep` |
| Медленные тесты | Тяжёлый Spring-контекст, много сценариев | Переиспользовать контекст; параллельный запуск; `@MockBean` для внешних сервисов |
| DataTable не парсится | Несовпадение заголовков или типов | Проверить заголовки таблицы; использовать `asMaps()` / `asMap()` |

### Отладка: скриншот при падении

```java
@After
public void takeScreenshotOnFailure(Scenario scenario) {
    if (scenario.isFailed() && driver instanceof TakesScreenshot) {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", scenario.getName() + ".png");

        // Также сохранить HTML страницы для анализа
        scenario.attach(driver.getPageSource().getBytes(), "text/html", "page-source.html");
    }
}
```


## Когда использовать и когда нет

| Подходит | Не подходит |
|----------|-------------|
| BDD-проекты с участием бизнеса | Юнит-тесты (лучше JUnit/Mockito) |
| Приёмочное тестирование (acceptance) | Нагрузочное тестирование (лучше Gatling/k6) |
| Живая документация требований | Простые проверки (избыточно) |
| Сложная бизнес-логика с множеством сценариев | Чисто технические тесты без бизнес-контекста |
| Кросс-функциональные команды | Проект без участия бизнес-аналитиков |
| E2E и API тестирование | Legacy-код без чёткой архитектуры |


## Итоговые таблицы

### Основные аннотации Cucumber

| Аннотация | Назначение |
|-----------|------------|
| `@Given` | Предусловие (начальное состояние) |
| `@When` | Действие пользователя |
| `@Then` | Проверка результата |
| `@And` / `@But` | Дополнительные шаги (синтаксический сахар) |
| `@Before` / `@After` | Хуки до/после сценария |
| `@BeforeStep` / `@AfterStep` | Хуки до/после каждого шага |
| `@BeforeAll` / `@AfterAll` | Глобальные хуки (раз на весь запуск) |
| `@ParameterType` | Пользовательский тип параметра |

### Ключевые слова Gherkin

| Слово | Назначение |
|-------|------------|
| `Feature` | Описание функциональности |
| `Scenario` | Конкретный тестовый сценарий |
| `Scenario Outline` | Параметризованный сценарий |
| `Examples` | Наборы данных для Scenario Outline |
| `Background` | Общие предусловия для всех сценариев в Feature |
| `Rule` | Группировка сценариев по бизнес-правилу |
| `@tag` | Тег для фильтрации и организации |

### Конфигурация @CucumberOptions

| Параметр | Описание | Пример |
|----------|----------|--------|
| `features` | Путь к `.feature` файлам | `"src/test/resources/features"` |
| `glue` | Пакет со step definitions и hooks | `"com.example.stepdefinitions"` |
| `plugin` | Форматы отчётов | `{"pretty", "html:target/report.html"}` |
| `tags` | Фильтр по тегам | `"@SmokeTest and not @Ignore"` |
| `dryRun` | Проверка без выполнения | `true` — генерирует заглушки для неопределённых шагов |
| `monochrome` | Чистый вывод в консоль | `true` |


