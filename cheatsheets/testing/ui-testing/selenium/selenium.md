---
title: "Selenium для Java"
description: "Краткое руководство по Selenium WebDriver для автоматизации веб-тестирования в Java: настройка, взаимодействия, Page Object, ожидания, data-driven тесты, параллельный запуск и интеграция со Spring Boot."
tags:
  - testing
  - ui-testing
  - selenium
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Selenium для Java

Краткое руководство по **Selenium WebDriver** для автоматизации веб-тестирования в Java: настройка, взаимодействия, Page Object, ожидания, data-driven тесты, параллельный запуск и интеграция со Spring Boot.


## Полезные ссылки

- [Selenium Documentation](https://www.selenium.dev/documentation/) — официальная документация
- [WebDriver API](https://www.selenium.dev/documentation/webdriver/) — API управления браузером
- [Page Object Model](https://www.selenium.dev/documentation/test_practice/page_object_model/) — паттерн Page Object
- [Waits](https://www.selenium.dev/documentation/webdriver/waits/) — ожидания и синхронизация

### См. также

- [[junit-advanced|JUnit Advanced]] — расширения JUnit
- [[assertj|AssertJ]] — утверждения
- [[rest-assured|REST Assured]] — тестирование API
- [[spring-testing|Spring Testing]] — тестирование Spring


## Содержание

- [Введение в Selenium](#введение-в-selenium)
- [Maven зависимости](#maven-зависимости)
- [Простое использование](#простое-использование)
- [Настройка WebDriver](#настройка-webdriver)
- [Взаимодействия с элементами](#взаимодействия-с-элементами)
- [Локаторы](#локаторы)
- [Ожидания](#ожидания)
- [Page Object Model](#page-object-model)
- [Data-driven тесты](#data-driven-тесты)
- [Скриншоты и JavaScript](#скриншоты-и-javascript)
- [Spring Boot и параллельный запуск](#spring-boot-и-параллельный-запуск)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем и FAQ](#решение-проблем-и-faq)
  - [Элемент не найден (NoSuchElementException, StaleElementReferenceException)](#элемент-не-найден-nosuchelementexception-staleelementreferenceexception)
  - [Элемент во фрейме или в Shadow DOM](#элемент-во-фрейме-или-в-shadow-dom)
  - [Тесты проходят локально, падают в CI](#тесты-проходят-локально-падают-в-ci)
  - [Разные браузеры ведут себя по-разному](#разные-браузеры-ведут-себя-по-разному)
  - [Медленные тесты](#медленные-тесты)
- [Сводные таблицы](#сводные-таблицы)
  - [Локаторы](#локаторы-1)
  - [Ожидания (кратко)](#ожидания-кратко)
  - [Когда использовать Selenium](#когда-использовать-selenium)
- [Заключение](#заключение)

## Введение в Selenium

**Selenium WebDriver** — инструмент для автоматизации браузеров. Позволяет писать тесты, которые взаимодействуют с веб-приложением как реальный пользователь.

**Зачем использовать Selenium:**

- Тестирование в разных браузерах (Chrome, Firefox, Edge, Safari)
- Поддержка Java, Python, C#, Ruby, JavaScript
- Паттерн Page Object для поддерживаемых тестов
- Явные и неявные ожидания элементов
- Режим без GUI (headless) для CI/CD
- Selenium Grid для распределённого запуска
- Скриншоты при падении теста


## Maven зависимости

**Основная зависимость (достаточна для всех браузеров):**

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

**JUnit 5 и WebDriverManager (управление драйверами):**

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.5.3</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**

```gradle
testImplementation 'org.seleniumhq.selenium:selenium-java:4.15.0'
testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
testImplementation 'io.github.bonigarcia:webdrivermanager:5.5.3'
```

**Важно:** Selenium 4 требует Java 8+. Драйверы можно не ставить вручную — WebDriverManager подгружает их автоматически.


## Простое использование

Минимальный тест: открыть страницу, заполнить форму, нажать кнопку, проверить результат.

```java
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;

public class SeleniumBasicTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    void testUserLogin() {
        driver.get("http://localhost:8080/login");

        WebElement username = driver.findElement(By.id("username"));
        WebElement password = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("login-button"));

        username.sendKeys("testuser");
        password.sendKeys("password");
        loginBtn.click();

        WebElement welcome = driver.findElement(By.className("welcome-message"));
        assertThat(welcome.getText()).contains("Welcome");
    }
}
```


## Настройка WebDriver

**Chrome (в том числе headless):**

```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
options.addArguments("--window-size=1920,1080");
WebDriver driver = new ChromeDriver(options);
```

**Firefox:**

```java
WebDriverManager.firefoxdriver().setup();
FirefoxOptions options = new FirefoxOptions();
options.addArguments("--headless");
WebDriver driver = new FirefoxDriver(options);
```

**Таймауты:**

```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
```

**Remote (Selenium Grid):**

```java
URL gridUrl = new URL("http://localhost:4444/wd/hub");
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
WebDriver driver = new RemoteWebDriver(gridUrl, options);
```


## Взаимодействия с элементами

| Действие           | Метод                    |
|--------------------|--------------------------|
| Ввод текста        | `element.sendKeys("текст")` |
| Очистка поля       | `element.clear()`        |
| Клик               | `element.click()`        |
| Выбор чекбокса     | `element.click()` (переключение) |
| Выбор из списка    | `new Select(element)` + `selectByVisibleText` / `selectByValue` / `selectByIndex` |
| Загрузка файла     | `fileInput.sendKeys(путьКФайлу)` |

**Пример: форма (текст, чекбокс, выпадающий список):**

```java
driver.get("http://localhost:8080/form");

WebElement name = driver.findElement(By.id("name"));
name.sendKeys("Иван");
name.clear();
name.sendKeys("Пётр");

WebElement checkbox = driver.findElement(By.id("agree"));
if (!checkbox.isSelected()) checkbox.click();

Select country = new Select(driver.findElement(By.id("country")));
country.selectByVisibleText("Россия");

driver.findElement(By.id("submit")).click();
```

**Клавиатура и мышь (Actions):**

```java
Actions actions = new Actions(driver);

// Наведение и клик
actions.moveToElement(menuItem).click().perform();

// Drag and drop
actions.dragAndDrop(source, target).perform();

// Горячие клавиши (Ctrl+A, Ctrl+C)
actions.keyDown(Keys.CONTROL).sendKeys("a").sendKeys("c").keyUp(Keys.CONTROL).perform();
```


## Локаторы

Поиск элементов задаётся через `By`:

| Стратегия        | Пример |
|------------------|--------|
| По ID            | `By.id("username")` |
| По атрибуту name | `By.name("password")` |
| По классу        | `By.className("btn-primary")` |
| По тегу          | `By.tagName("button")` |
| Текст ссылки     | `By.linkText("Вход")` |
| Часть текста     | `By.partialLinkText("Вход")` |
| CSS-селектор     | `By.cssSelector("#login .btn")` |
| XPath            | `By.xpath("//button[@type='submit']")` |

**Рекомендации:** предпочтительны `id`, `data-testid` или стабильные CSS-селекторы. XPath по позиции (`tr[2]`) легко ломается при изменении вёрстки.

**Пример:**

```java
WebElement byId = driver.findElement(By.id("username"));
WebElement byCss = driver.findElement(By.cssSelector("input[type='email']"));
WebElement byXpath = driver.findElement(By.xpath("//form//input[@name='email']"));

List<WebElement> buttons = driver.findElements(By.tagName("button"));
```


## Ожидания

- **Неявное ожидание (implicit):** применяется ко всем `findElement` до истечения таймаута.
- **Явное ожидание (explicit):** ждём конкретное условие (появление, кликабельность и т.д.).

**Не смешивать** неявное и явное с большими таймаутами — поведение станет непредсказуемым. Лучше короткий implicit (1–2 сек) и явные ожидания там, где нужно.

**Явное ожидание:**

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

// Появление элемента
WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));

// Элемент кликабелен
WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
btn.click();

// Текст в элементе
wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("status"), "Готово"));

// Своё условие
WebElement progress = wait.until(d -> {
    WebElement bar = d.findElement(By.id("progress-bar"));
    return bar.getCssValue("width").equals("100%") ? bar : null;
});
```

**FluentWait** — явное ожидание с настраиваемым интервалом опроса и игнорированием исключений:

```java
FluentWait<WebDriver> fluent = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))
    .pollingEvery(Duration.ofMillis(500))
    .ignoring(NoSuchElementException.class);

WebElement el = fluent.until(d -> d.findElement(By.id("dynamic")));
```


## Page Object Model

Страница описывается классом: локаторы и действия инкапсулированы, тест читается как сценарий.

**Базовый класс и страница входа:**

```java
public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected WebElement waitFor(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void type(By locator, String text) {
        WebElement el = waitFor(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }
}

public class LoginPage extends BasePage {
    private final By username = By.id("username");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.className("error-message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterUsername(String value) {
        type(username, value);
        return this;
    }

    public LoginPage enterPassword(String value) {
        type(password, value);
        return this;
    }

    public DashboardPage clickLogin() {
        click(loginButton);
        return new DashboardPage(driver);
    }

    public String getErrorMessage() {
        return waitFor(errorMessage).getText();
    }
}
```

**Использование в тесте:**

```java
@Test
void testSuccessfulLogin() {
    driver.get("http://localhost:8080/login");
    DashboardPage dashboard = new LoginPage(driver)
        .enterUsername("testuser")
        .enterPassword("password")
        .clickLogin();
    assertThat(dashboard.getWelcomeMessage()).contains("Welcome");
}
```


## Data-driven тесты

Один и тот же сценарий проверяется на разных данных с помощью `@ParameterizedTest` и `@MethodSource`.

```java
@ParameterizedTest
@MethodSource("loginData")
void testLoginScenarios(String user, String pass, boolean success, String expectedMessage) {
    driver.get("http://localhost:8080/login");
    driver.findElement(By.id("username")).sendKeys(user);
    driver.findElement(By.id("password")).sendKeys(pass);
    driver.findElement(By.id("login-button")).click();

    if (success) {
        WebElement welcome = driver.findElement(By.className("welcome-message"));
        assertThat(welcome.getText()).contains(expectedMessage);
        } else {
        WebElement err = driver.findElement(By.className("error-message"));
        assertThat(err.getText()).contains(expectedMessage);
    }
}

static Stream<Arguments> loginData() {
    return Stream.of(
        Arguments.of("validuser", "validpass", true, "Welcome"),
        Arguments.of("invalid", "invalid", false, "Invalid credentials"),
        Arguments.of("", "pass", false, "Username is required")
    );
}
```

Данные можно подставлять из CSV, JSON или БД — через свой метод, возвращающий `Stream<Arguments>`.


## Скриншоты и JavaScript

**Скриншот при падении:**

```java
try {
    // ... тест
} catch (AssertionError e) {
    TakesScreenshot ts = (TakesScreenshot) driver;
    byte[] bytes = ts.getScreenshotAs(OutputType.BYTES);
    Files.write(Paths.get("target/screenshots/failure.png"), bytes);
            throw e;
}
```

**Выполнение JavaScript:**

```java
JavascriptExecutor js = (JavascriptExecutor) driver;

String title = (String) js.executeScript("return document.title;");
js.executeScript("arguments[0].scrollIntoView(true);", element);
js.executeScript("arguments[0].click();", element);
```


## Spring Boot и параллельный запуск

**Spring Boot + Selenium (случайный порт):**

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebDriver
class SpringSeleniumTest {

    @Autowired
    private WebDriver driver;

    @LocalServerPort
    private int port;

    @Test
    void testHomePage() {
        driver.get("http://localhost:" + port + "/");
        WebElement h1 = driver.findElement(By.tagName("h1"));
        assertThat(h1.getText()).isNotEmpty();
    }
}
```

**Параллельный запуск (JUnit 5):** каждый поток со своим экземпляром `WebDriver` (например, через `ThreadLocal`). В `junit-platform.properties`:

```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.config.strategy=dynamic
```

В `@BeforeEach` создаёте драйвер и сохраняете в `ThreadLocal`, в `@AfterEach` вызываете `quit()` и удаляете из `ThreadLocal`.


## Лучшие практики

1. **Page Object** — не дублировать локаторы и действия в тестах; выносить в классы страниц.
2. **Явные ожидания** — для динамического контента использовать `WebDriverWait` и `ExpectedConditions`, а не только implicit wait.
3. **Стабильные локаторы** — предпочитать `id`, `data-testid`, стабильные CSS; избегать хрупких XPath по индексам.
4. **Один тест — один сценарий** — не проверять в одном методе десятки шагов.
5. **Скриншот при падении** — сохранять в артефакты CI для разбора.
6. **Изоляция** — каждый тест не должен зависеть от данных или состояния других тестов.
7. **Таймауты** — задавать разумные значения; не ставить избыточно большой implicit wait.
8. **Режим headless в CI** — использовать `--headless` и при необходимости отключать загрузку изображений для ускорения.


## Решение проблем и FAQ

### Элемент не найден (NoSuchElementException, StaleElementReferenceException)

- **Причина:** элемент ещё не появился или DOM обновился после получения ссылки.
- **Решение:** использовать явное ожидание вместо одного `findElement`:
```java
  WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dynamic")));
  ```
- После обновления списка/таблицы заново искать элемент, а не использовать старую ссылку.

### Элемент во фрейме или в Shadow DOM

- **Фрейм:** переключиться в фрейм, затем искать элемент, потом вернуться в основной контент:
```java
  driver.switchTo().frame("frameName");
  WebElement el = driver.findElement(By.id("inside-frame"));
  driver.switchTo().defaultContent();
  ```
- **Shadow DOM:** получить shadow root от хоста и искать внутри:
```java
  WebElement host = driver.findElement(By.id("host"));
  SearchContext shadow = host.getShadowRoot();
  WebElement inner = shadow.findElement(By.cssSelector(".inner"));
  ```

### Тесты проходят локально, падают в CI

- Проверить headless-режим и аргументы Chrome (`--no-sandbox`, `--disable-dev-shm-usage`).
- Увеличить таймауты для медленной среды или нестабильной сети.
- Убедиться, что приложение успевает подняться до первого запроса (например, через ожидание порта или health endpoint).

### Разные браузеры ведут себя по-разному

- Использовать общие действия (клик, ввод текста) и явные ожидания.
- Избегать зависимостей от точных координат или специфичных для одного браузера API, если не нужны именно они.

### Медленные тесты

- Уменьшить implicit wait; использовать явные ожидания только там, где нужно.
- В headless отключить загрузку картинок/части ресурсов при необходимости.
- Запускать тесты параллельно (JUnit 5 или TestNG).


## Сводные таблицы

### Локаторы

| Метод            | Когда использовать |
|------------------|--------------------|
| `By.id`          | Уникальный ID элемента |
| `By.cssSelector` | Сложные селекторы, атрибуты, классы |
| `By.xpath`       | Иерархия, текст, сложная логика |
| `By.name`        | Поля форм с атрибутом name |
| `By.linkText`    | Точный текст ссылки |
| `By.partialLinkText` | Часть текста ссылки |

### Ожидания (кратко)

| Тип       | Описание |
|-----------|----------|
| Implicit  | Глобальный таймаут для поиска элемента; не злоупотреблять большими значениями. |
| Explicit  | `WebDriverWait` + `ExpectedConditions` для появления, кликабельности, текста и т.д. |
| FluentWait| Явное ожидание с настраиваемым интервалом опроса и игнорированием исключений. |

### Когда использовать Selenium

| Подходит для | Не подходит для |
|---------------|------------------|
| End-to-end тестирование сценариев в браузере | Unit-тесты (JUnit, Mockito) |
| Регрессия UI, проверка вёрстки и потоков | Тестирование только API (REST Assured и др.) |
| Кросс-браузерные проверки | Нагрузочное тестирование (JMeter, Gatling) |
| Проверка сложных взаимодействий (drag-drop, загрузка файлов) | Мобильные приложения (Appium) |


## Заключение

Selenium WebDriver даёт полный контроль над браузером и позволяет писать стабильные end-to-end тесты на Java. Ключевые моменты:

- **Настройка:** зависимость `selenium-java`, при необходимости WebDriverManager; один раз настроить драйвер и таймауты.
- **Удобство поддержки:** Page Object, явные ожидания, стабильные локаторы и изолированные тесты.
- **Масштабирование:** data-driven сценарии, параллельный запуск, интеграция со Spring Boot и CI/CD.

Используйте Selenium для сценариев в реальном браузере; для модульных и API-тестов по-прежнему лучше применять JUnit, Mockito и инструменты вроде REST Assured.
