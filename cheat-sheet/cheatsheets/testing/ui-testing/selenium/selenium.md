# Selenium для Java

Комплексное руководство по использованию Selenium WebDriver для автоматизации веб-тестирования в Java: page objects, waits, data-driven testing, parallel execution, Spring Boot интеграция.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [Selenium Documentation](https://www.selenium.dev/documentation/) - Основная документация
- [Selenium WebDriver](https://www.selenium.dev/documentation/webdriver/) - WebDriver API
- [Selenium Grid](https://www.selenium.dev/documentation/grid/) - Selenium Grid

### Java интеграции
- [Selenium Java](https://www.selenium.dev/documentation/webdriver/getting_started/install_library/) - Java bindings
- [JUnit 5 Integration](https://github.com/SeleniumHQ/selenium/tree/trunk/java#junit-5) - JUnit 5 поддержка
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing) - Spring Boot testing

### Best practices
- [Selenium Best Practices](https://www.selenium.dev/documentation/test_practices/) - Лучшие практики
- [Page Object Model](https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/) - Page Object паттерн
- [Waits and Synchronization](https://www.selenium.dev/documentation/webdriver/waits/) - Ожидания и синхронизация

### См. также
- `testing/junit-advanced.md` - JUnit расширения
- `testing/assertj.md` - Assertions
- `testing/rest-assured.md` - API testing
- `spring-testing.md` - Spring testing

## Содержание

- [Введение в Selenium](#введение-в-selenium)
- [WebDriver setup](#webdriver-setup)
- [Basic interactions](#basic-interactions)
- [Locators](#locators)
- [Waits](#waits)
- [Page Object Model](#page-object-model)
- [Data-driven testing](#data-driven-testing)
- [Advanced scenarios](#advanced-scenarios)
- [Spring Boot integration](#spring-boot-integration)
- [Parallel execution](#parallel-execution)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в Selenium

**Selenium WebDriver** — это инструмент для автоматизации веб-браузеров. Он позволяет писать тесты, которые взаимодействуют с веб-приложениями так же, как это делает реальный пользователь.

### Почему Selenium?

Selenium предоставляет мощные возможности для автоматизации веб-тестирования:

1. **Cross-browser Testing** — поддержка всех основных браузеров
2. **Multiple Languages** — bindings для Java, Python, C#, Ruby, JavaScript
3. **Page Object Model** — паттерн для maintainable тестов
4. **Wait Strategies** — различные стратегии ожидания элементов
5. **Headless Mode** — выполнение без GUI
6. **Grid Support** — распределенное выполнение тестов
7. **Screenshot Support** — создание скриншотов при ошибках
8. **Mobile Testing** — поддержка mobile браузеров

### Maven зависимости

**Selenium** имеет модульную архитектуру, где selenium-java включает все необходимые компоненты, а дополнительные модули позволяют оптимизировать зависимости для конкретных браузеров.

#### Core Selenium (обязательный)

**selenium-java** — основной модуль Selenium для Java с поддержкой всех браузеров.

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

**Что включает selenium-java:**
- **WebDriver API** — основной API для управления браузерами
- **All browser drivers** — драйверы для Chrome, Firefox, Edge, Safari
- **Support classes** — утилиты и вспомогательные классы
- **Grid client** — клиент для Selenium Grid
- **Remote WebDriver** — для удаленного выполнения
- **Page factory** — для Page Object Model
- **Expected conditions** — готовые условия ожидания
- **WebDriverWait** — классы для явных ожиданий

#### Специфические браузерные драйверы

**selenium-chrome-driver** — только Chrome и Chromium-based браузеры:

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-chrome-driver</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

**selenium-firefox-driver** — только Firefox:

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-firefox-driver</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

**selenium-edge-driver** — только Microsoft Edge:

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-edge-driver</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

**selenium-safari-driver** — только Safari:

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-safari-driver</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

#### Testing frameworks

**JUnit 5** (рекомендуемый):

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

**TestNG** (альтернативный):

```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.8.0</version>
    <scope>test</scope>
</dependency>
```

**Cucumber** для BDD тестирования:

```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>7.14.0</version>
    <scope>test</scope>
</dependency>
```

#### Утилиты и расширения

**Selenium Support** — дополнительные утилиты:

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-support</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

**Selenium Manager** — автоматическое управление драйверами:

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-manager</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

#### Spring Boot интеграция

**Spring Boot Starter Test включает Selenium:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!-- Selenium уже включен -->
</dependency>

<!-- Дополнительно для Selenium -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <scope>test</scope>
</dependency>
```

**Spring Boot Test с Selenium:**
```java
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SeleniumTest {

    @Autowired
    private WebDriver webDriver;

    // Тесты с инъекцией WebDriver
}
```

#### Docker для Selenium

**Selenium Grid в Docker:**

```yaml
version: '3.8'
services:
  selenium-hub:
    image: selenium/hub:4.15.0
    ports:
      - "4442:4442"
      - "4443:4443"
      - "4444:4444"

  chrome-node:
    image: selenium/node-chrome:4.15.0
    depends_on:
      - selenium-hub
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
    volumes:
      - /dev/shm:/dev/shm

  firefox-node:
    image: selenium/node-firefox:4.15.0
    depends_on:
      - selenium-hub
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
    volumes:
      - /dev/shm:/dev/shm
```

#### Gradle зависимости

**Для Gradle проектов с подробными конфигурациями:**

```gradle
dependencies {
    // Core Selenium - основной модуль для веб-автоматизации
    testImplementation 'org.seleniumhq.selenium:selenium-java:4.15.0'

    // JUnit 5 - основной testing framework
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    // TestNG - альтернативный testing framework
    testImplementation 'org.testng:testng:7.8.0'

    // Cucumber - для BDD тестирования
    testImplementation 'io.cucumber:cucumber-java:7.14.0'
    testImplementation 'io.cucumber:cucumber-junit:7.14.0'

    // Selenium Support - дополнительные утилиты
    testImplementation 'org.seleniumhq.selenium:selenium-support:4.15.0'

    // Selenium Manager - автоматическое управление драйверами
    testImplementation 'org.seleniumhq.selenium:selenium-manager:4.15.0'
}
```

#### Version management

**Рекомендуется использовать properties для версий:**

```gradle
ext {
    seleniumVersion = '4.15.0'
    junitVersion = '5.10.0'
    testngVersion = '7.8.0'
    cucumberVersion = '7.14.0'
}

dependencies {
    testImplementation "org.seleniumhq.selenium:selenium-java:${seleniumVersion}"
    testImplementation "org.junit.jupiter:junit-jupiter:${junitVersion}"
    testImplementation "org.testng:testng:${testngVersion}"
    testImplementation "io.cucumber:cucumber-java:${cucumberVersion}"
}
```

#### Миграция между версиями

**Selenium 3.x → 4.x:**

**Ключевые изменения в Selenium 4:**
- **W3C WebDriver Protocol** — полная поддержка W3C стандарта
- **Relative locators** — новые способы поиска элементов
- **Chrome DevTools** — интеграция с Chrome DevTools Protocol
- **Docker support** — официальные Docker образы
- **Grid improvements** — улучшенная архитектура Grid
- **Better logging** — улучшенное логирование
- **Java 8+** — минимальная версия Java 8

```xml
<!-- Selenium 3.x (legacy) -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>3.141.59</version>
</dependency>

<!-- Selenium 4.x (current) -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.15.0</version>
</dependency>
```

**Breaking changes в Selenium 4:**
- **Capabilities API** — изменен API для настроек браузера
- **Waits API** — улучшен API для ожиданий
- **Actions API** — новый fluent API для действий
- **Grid architecture** — полностью новая архитектура

#### IDE Configuration

**IntelliJ IDEA:**
1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. **Selenium классы будут автоматически распознаны**
3. **WebDriver переменные можно инспектировать в debug режиме**

**Eclipse:**
1. **Help → Eclipse Marketplace**
2. **Find**: "Selenium"
3. **Install**: Selenium IDE или WebDriver support

**VS Code:**
```json
{
    "java.test.config": {
        "name": "JUnit Jupiter + Selenium",
        "type": "junit",
        "request": "launch",
        "mainClass": "org.junit.platform.console.ConsoleLauncher",
        "args": ["--scan-classpath"],
        "vmargs": ["-ea"],
        "dependencies": [
            "org.seleniumhq.selenium:selenium-java:4.15.0"
        ]
    }
}
```

#### Troubleshooting зависимостей

**Проблема: WebDriver не найден**

```bash
# Проверить PATH
echo $PATH | grep -i driver

# Скачать драйверы вручную
# Chrome: https://chromedriver.chromium.org/downloads
# Firefox: https://github.com/mozilla/geckodriver/releases
# Edge: https://developer.microsoft.com/microsoft-edge/tools/webdriver/
```

**Проблема: Version mismatch**

```java
// Проверить версии
System.out.println("Selenium: " + org.openqa.selenium.WebDriver.class.getPackage().getImplementationVersion());

// Использовать WebDriverManager для автоматического управления
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.5.3</version>
    <scope>test</scope>
</dependency>
```

**Проблема: Browser не запускается**

```java
// Для headless режима
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");

WebDriver driver = new ChromeDriver(options);
```

**Проблема: Element not found**

```java
// Использовать явные ожидания
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("elementId")));
```

**Проблема: Slow tests**

```java
// Оптимизировать настройки браузера
ChromeOptions options = new ChromeOptions();
options.addArguments("--disable-extensions");
options.addArguments("--disable-plugins");
options.addArguments("--disable-images");
options.setPageLoadStrategy(PageLoadStrategy.EAGER);

WebDriver driver = new ChromeDriver(options);
```

**Проблема: Memory leaks**

```java
// Правильно закрывать драйвер
@AfterEach
void tearDown() {
    if (driver != null) {
        driver.quit(); // Закрывает все окна и завершает процесс
    }
}
```

<!-- Firefox Driver -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-firefox-driver</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>

<!-- Edge Driver -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-edge-driver</artifactId>
    <version>4.15.0</version>
    <scope>test</scope>
</dependency>
```

### Basic usage

#### Простое использование
```java
@SpringBootTest
public class SeleniumBasicTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        // Setup Chrome driver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testUserLogin() {
        driver.get("http://localhost:8080/login");

        // Find elements and interact
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        WebElement loginButton = driver.findElement(By.id("login-button"));

        // Perform actions
        usernameField.sendKeys("testuser");
        passwordField.sendKeys("password");
        loginButton.click();

        // Verify result
        WebElement welcomeMessage = driver.findElement(By.className("welcome-message"));
        assertThat(welcomeMessage.getText()).contains("Welcome");
    }
}
```

## WebDriver setup

### Browser configuration

#### Настройка различных браузеров
```java
@SpringBootTest
public class BrowserSetupTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        // Chrome options
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless"); // Run headless
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--window-size=1920,1080");

        // Setup driver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(chromeOptions);

        // Configure timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
    }

    @Test
    void testWithChrome() {
        driver.get("http://localhost:8080");
        assertThat(driver.getTitle()).contains("Application");
    }

    @Test
    void testFirefoxSetup() {
        // Firefox options
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headless");
        firefoxOptions.addPreference("dom.webnotifications.enabled", false);

        WebDriverManager.firefoxdriver().setup();
        WebDriver firefoxDriver = new FirefoxDriver(firefoxOptions);

        try {
            firefoxDriver.get("http://localhost:8080");
            assertThat(firefoxDriver.getTitle()).isNotEmpty();
        } finally {
            firefoxDriver.quit();
        }
    }

    @Test
    void testEdgeSetup() {
        // Edge options
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("--headless");
        edgeOptions.addArguments("--disable-gpu");

        WebDriverManager.edgedriver().setup();
        WebDriver edgeDriver = new EdgeDriver(edgeOptions);

        try {
            edgeDriver.get("http://localhost:8080");
            assertThat(edgeDriver.getTitle()).isNotEmpty();
        } finally {
            edgeDriver.quit();
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### Remote WebDriver

#### Selenium Grid и Remote execution
```java
@SpringBootTest
public class RemoteWebDriverTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() throws MalformedURLException {
        // Remote WebDriver for Selenium Grid
        URL gridUrl = new URL("http://localhost:4444/wd/hub");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        driver = new RemoteWebDriver(gridUrl, options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testRemoteExecution() {
        driver.get("http://localhost:8080");

        WebElement header = driver.findElement(By.tagName("h1"));
        assertThat(header.getText()).isNotEmpty();
    }

    @Test
    void testWithCapabilities() {
        // Desired capabilities
        ChromeOptions options = new ChromeOptions();
        options.setPlatformName(Platform.LINUX);
        options.setBrowserVersion("latest");

        // Additional capabilities
        options.setCapability("enableVNC", true);
        options.setCapability("enableVideo", true);
        options.setCapability("name", "Test Execution");

        URL gridUrl = URI.create("http://localhost:4444/wd/hub").toURL();
        WebDriver remoteDriver = new RemoteWebDriver(gridUrl, options);

        try {
            remoteDriver.get("http://localhost:8080");
            assertThat(remoteDriver.getTitle()).isNotEmpty();
        } finally {
            remoteDriver.quit();
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Basic interactions

### Element interactions

#### Основные взаимодействия с элементами
```java
@SpringBootTest
public class ElementInteractionsTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testTextInput() {
        driver.get("http://localhost:8080/form");

        WebElement nameField = driver.findElement(By.id("name"));
        WebElement emailField = driver.findElement(By.id("email"));

        // Text input
        nameField.sendKeys("John Doe");
        emailField.sendKeys("john@example.com");

        // Clear and re-enter
        nameField.clear();
        nameField.sendKeys("Jane Doe");

        // Submit form
        WebElement submitButton = driver.findElement(By.id("submit"));
        submitButton.click();

        // Verify result
        WebElement result = driver.findElement(By.id("result"));
        assertThat(result.getText()).contains("Jane Doe");
    }

    @Test
    void testCheckboxAndRadio() {
        driver.get("http://localhost:8080/form");

        // Checkboxes
        WebElement checkbox1 = driver.findElement(By.id("checkbox1"));
        WebElement checkbox2 = driver.findElement(By.id("checkbox2"));

        checkbox1.click();
        assertThat(checkbox1.isSelected()).isTrue();

        checkbox2.click();
        assertThat(checkbox2.isSelected()).isTrue();

        // Uncheck
        checkbox1.click();
        assertThat(checkbox1.isSelected()).isFalse();

        // Radio buttons
        WebElement radio1 = driver.findElement(By.id("radio1"));
        WebElement radio2 = driver.findElement(By.id("radio2"));

        radio1.click();
        assertThat(radio1.isSelected()).isTrue();
        assertThat(radio2.isSelected()).isFalse();

        radio2.click();
        assertThat(radio1.isSelected()).isFalse();
        assertThat(radio2.isSelected()).isTrue();
    }

    @Test
    void testSelectDropdown() {
        driver.get("http://localhost:8080/form");

        Select countrySelect = new Select(driver.findElement(By.id("country")));

        // Select by visible text
        countrySelect.selectByVisibleText("United States");

        // Select by value
        countrySelect.selectByValue("us");

        // Select by index
        countrySelect.selectByIndex(0);

        // Verify selection
        assertThat(countrySelect.getFirstSelectedOption().getText())
            .isEqualTo("United States");

        // Get all options
        List<WebElement> options = countrySelect.getOptions();
        assertThat(options).hasSizeGreaterThan(1);
    }

    @Test
    void testFileUpload() {
        driver.get("http://localhost:8080/upload");

        WebElement fileInput = driver.findElement(By.id("file"));
        WebElement uploadButton = driver.findElement(By.id("upload"));

        // Upload file
        String filePath = "/path/to/test/file.txt";
        fileInput.sendKeys(filePath);
        uploadButton.click();

        // Verify upload success
        WebElement successMessage = driver.findElement(By.className("success"));
        assertThat(successMessage.getText()).contains("uploaded successfully");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### Keyboard and mouse actions

#### Клавиатура и мышь
```java
@SpringBootTest
public class KeyboardMouseTest {

    private WebDriver driver;
    private Actions actions;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        actions = new Actions(driver);
    }

    @Test
    void testKeyboardActions() {
        driver.get("http://localhost:8080/form");

        WebElement textArea = driver.findElement(By.id("comments"));

        // Keyboard actions
        actions.click(textArea)
            .sendKeys("First line")
            .keyDown(Keys.CONTROL)
            .sendKeys("a") // Select all
            .keyUp(Keys.CONTROL)
            .sendKeys("c") // Copy
            .perform();

        // Move to another field and paste
        WebElement otherField = driver.findElement(By.id("summary"));
        actions.click(otherField)
            .keyDown(Keys.CONTROL)
            .sendKeys("v") // Paste
            .keyUp(Keys.CONTROL)
            .perform();

        assertThat(otherField.getAttribute("value")).isEqualTo("First line");
    }

    @Test
    void testMouseActions() {
        driver.get("http://localhost:8080/menu");

        WebElement menuItem = driver.findElement(By.id("menu-item"));

        // Mouse hover
        actions.moveToElement(menuItem).perform();

        // Verify submenu appears
        WebElement submenu = driver.findElement(By.id("submenu"));
        assertThat(submenu.isDisplayed()).isTrue();

        // Click submenu item
        WebElement submenuItem = driver.findElement(By.id("submenu-item"));
        actions.click(submenuItem).perform();

        // Verify navigation
        assertThat(driver.getCurrentUrl()).contains("submenu-page");
    }

    @Test
    void testDragAndDrop() {
        driver.get("http://localhost:8080/drag-drop");

        WebElement source = driver.findElement(By.id("draggable"));
        WebElement target = driver.findElement(By.id("droppable"));

        // Drag and drop
        actions.dragAndDrop(source, target).perform();

        // Verify drop was successful
        WebElement droppedItem = driver.findElement(By.id("dropped-item"));
        assertThat(droppedItem.isDisplayed()).isTrue();
        assertThat(droppedItem.getText()).contains("dropped");
    }

    @Test
    void testComplexActions() {
        driver.get("http://localhost:8080/canvas");

        WebElement canvas = driver.findElement(By.id("drawing-canvas"));

        // Draw on canvas using mouse
        actions.clickAndHold(canvas)
            .moveByOffset(50, 0)
            .moveByOffset(0, 50)
            .moveByOffset(-50, 0)
            .moveByOffset(0, -50)
            .release()
            .perform();

        // Verify drawing was created
        WebElement drawingResult = driver.findElement(By.id("drawing-result"));
        assertThat(drawingResult.getText()).contains("Drawing saved");
    }

    @Test
    void testKeyboardShortcuts() {
        driver.get("http://localhost:8080/editor");

        WebElement editor = driver.findElement(By.id("text-editor"));

        // Simulate Ctrl+B (bold)
        actions.click(editor)
            .sendKeys("Some text")
            .keyDown(Keys.CONTROL)
            .sendKeys("a") // Select all
            .sendKeys("b") // Bold
            .keyUp(Keys.CONTROL)
            .perform();

        // Verify formatting applied
        WebElement formattedText = driver.findElement(By.className("formatted-text"));
        assertThat(formattedText.getAttribute("class")).contains("bold");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Locators

### Different locator strategies

#### Стратегии поиска элементов
```java
@SpringBootTest
public class LocatorStrategiesTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testBasicLocators() {
        driver.get("http://localhost:8080/form");

        // ID locator
        WebElement usernameField = driver.findElement(By.id("username"));
        assertThat(usernameField.isDisplayed()).isTrue();

        // Name locator
        WebElement passwordField = driver.findElement(By.name("password"));
        assertThat(passwordField.isDisplayed()).isTrue();

        // Class name locator
        WebElement submitButton = driver.findElement(By.className("btn-submit"));
        assertThat(submitButton.isEnabled()).isTrue();

        // Tag name locator
        WebElement form = driver.findElement(By.tagName("form"));
        assertThat(form.isDisplayed()).isTrue();
    }

    @Test
    void testLinkLocators() {
        driver.get("http://localhost:8080/navigation");

        // Link text locator
        WebElement homeLink = driver.findElement(By.linkText("Home"));
        assertThat(homeLink.isDisplayed()).isTrue();

        // Partial link text locator
        WebElement aboutLink = driver.findElement(By.partialLinkText("About"));
        assertThat(aboutLink.isDisplayed()).isTrue();

        // Click link
        homeLink.click();
        assertThat(driver.getCurrentUrl()).endsWith("/home");
    }

    @Test
    void testXPathLocators() {
        driver.get("http://localhost:8080/table");

        // XPath by attribute
        WebElement firstRow = driver.findElement(By.xpath("//table/tbody/tr[1]"));
        assertThat(firstRow.isDisplayed()).isTrue();

        // XPath by text content
        WebElement activeUser = driver.findElement(By.xpath("//td[text()='Active']"));
        assertThat(activeUser.getText()).isEqualTo("Active");

        // XPath with contains
        WebElement emailCell = driver.findElement(By.xpath("//td[contains(@class, 'email')]"));
        assertThat(emailCell.isDisplayed()).isTrue();

        // XPath with parent/child relationship
        WebElement userRow = driver.findElement(By.xpath("//tr[td/text()='John Doe']"));
        WebElement userEmail = userRow.findElement(By.xpath("td[2]"));
        assertThat(userEmail.getText()).contains("@");
    }

    @Test
    void testCssSelectorLocators() {
        driver.get("http://localhost:8080/form");

        // CSS selector by ID
        WebElement usernameField = driver.findElement(By.cssSelector("#username"));
        assertThat(usernameField.isDisplayed()).isTrue();

        // CSS selector by class
        WebElement errorMessage = driver.findElement(By.cssSelector(".error-message"));
        assertThat(errorMessage.isDisplayed()).isFalse(); // Initially hidden

        // CSS selector by attribute
        WebElement requiredField = driver.findElement(By.cssSelector("input[required]"));
        assertThat(requiredField.isDisplayed()).isTrue();

        // CSS selector by pseudo-class
        WebElement firstInput = driver.findElement(By.cssSelector("input:first-of-type"));
        assertThat(firstInput.isDisplayed()).isTrue();

        // CSS selector with multiple conditions
        WebElement validInput = driver.findElement(By.cssSelector("input[type='email']:valid"));
        assertThat(validInput.isDisplayed()).isTrue();
    }

    @Test
    void testAdvancedLocators() {
        driver.get("http://localhost:8080/complex-page");

        // Multiple elements
        List<WebElement> menuItems = driver.findElements(By.className("menu-item"));
        assertThat(menuItems).hasSizeGreaterThan(3);

        // Find element within another element
        WebElement sidebar = driver.findElement(By.id("sidebar"));
        WebElement sidebarLink = sidebar.findElement(By.tagName("a"));
        assertThat(sidebarLink.isDisplayed()).isTrue();

        // Using JavaScript for complex locators
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement element = (WebElement) js.executeScript(
            "return document.querySelector('div[data-testid=\"special-element\"]');"
        );
        assertThat(element.isDisplayed()).isTrue();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Waits

### Implicit and explicit waits

#### Стратегии ожидания
```java
@SpringBootTest
public class WaitStrategiesTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));

        // Implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Explicit wait
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @Test
    void testImplicitWait() {
        driver.get("http://localhost:8080/dynamic-content");

        // Click button that loads content dynamically
        driver.findElement(By.id("load-content")).click();

        // Implicit wait will wait up to 10 seconds for element to appear
        WebElement dynamicContent = driver.findElement(By.id("dynamic-content"));
        assertThat(dynamicContent.getText()).contains("Loaded");
    }

    @Test
    void testExplicitWait() {
        driver.get("http://localhost:8080/ajax");

        // Click button that triggers AJAX request
        driver.findElement(By.id("ajax-button")).click();

        // Explicit wait for specific condition
        WebElement result = wait.until(ExpectedConditions
            .visibilityOfElementLocated(By.id("ajax-result")));

        assertThat(result.getText()).contains("Success");
    }

    @Test
    void testExpectedConditions() {
        driver.get("http://localhost:8080/form");

        // Wait for element to be clickable
        WebElement submitButton = wait.until(ExpectedConditions
            .elementToBeClickable(By.id("submit-button")));

        // Fill form
        driver.findElement(By.id("name")).sendKeys("Test User");
        driver.findElement(By.id("email")).sendKeys("test@example.com");

        // Wait for element to be enabled (if it was disabled initially)
        wait.until(ExpectedConditions
            .elementToBeClickable(submitButton));

        submitButton.click();

        // Wait for success message
        WebElement successMessage = wait.until(ExpectedConditions
            .visibilityOfElementLocated(By.className("success-message")));

        assertThat(successMessage.getText()).contains("Form submitted");
    }

    @Test
    void testCustomWaitConditions() {
        driver.get("http://localhost:8080/progress");

        driver.findElement(By.id("start-process")).click();

        // Custom wait condition
        wait.until(driver -> {
            WebElement progressBar = driver.findElement(By.id("progress-bar"));
            String width = progressBar.getCssValue("width");
            return width.equals("100%");
        });

        WebElement completionMessage = driver.findElement(By.id("completion-message"));
        assertThat(completionMessage.getText()).contains("Process completed");
    }

    @Test
    void testFluentWait() {
        driver.get("http://localhost:8080/slow-loading");

        // Fluent wait with custom polling and ignoring exceptions
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofSeconds(2))
            .ignoring(NoSuchElementException.class);

        WebElement slowElement = fluentWait.until(driver -> {
            WebElement element = driver.findElement(By.id("slow-element"));
            if (element.isDisplayed()) {
                return element;
            }
            return null;
        });

        assertThat(slowElement.getText()).contains("Finally loaded");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Page Object Model

### Basic page objects

#### Page Object паттерн
```java
// Base page class
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void clickElement(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void typeText(By locator, String text) {
        WebElement element = waitForElement(locator);
        element.clear();
        element.sendKeys(text);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }
}

// Login page
public class LoginPage extends BasePage {

    private By usernameField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");
    private By errorMessage = By.className("error-message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterUsername(String username) {
        typeText(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        typeText(passwordField, password);
        return this;
    }

    public DashboardPage clickLogin() {
        clickElement(loginButton);
        return new DashboardPage(driver);
    }

    public LoginPage clickLoginExpectingError() {
        clickElement(loginButton);
        return this;
    }

    public String getErrorMessage() {
        return waitForElement(errorMessage).getText();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return driver.findElement(errorMessage).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}

// Dashboard page
public class DashboardPage extends BasePage {

    private By welcomeMessage = By.className("welcome-message");
    private By logoutButton = By.id("logout-button");
    private By userMenu = By.id("user-menu");

    public DashboardPage(WebDriver driver) {
        super(driver);
        // Verify we're on the dashboard
        waitForElement(welcomeMessage);
    }

    public String getWelcomeMessage() {
        return waitForElement(welcomeMessage).getText();
    }

    public LoginPage logout() {
        clickElement(logoutButton);
        return new LoginPage(driver);
    }

    public UserProfilePage openUserMenu() {
        clickElement(userMenu);
        return new UserProfilePage(driver);
    }
}

// Usage in tests
@SpringBootTest
public class PageObjectTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testSuccessfulLogin() {
        driver.get("http://localhost:8080/login");

        DashboardPage dashboard = new LoginPage(driver)
            .enterUsername("testuser")
            .enterPassword("password")
            .clickLogin();

        assertThat(dashboard.getWelcomeMessage()).contains("Welcome");
    }

    @Test
    void testFailedLogin() {
        driver.get("http://localhost:8080/login");

        LoginPage loginPage = new LoginPage(driver)
            .enterUsername("invalid")
            .enterPassword("invalid")
            .clickLoginExpectingError();

        assertThat(loginPage.isErrorMessageDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Invalid credentials");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### Advanced page objects

#### Loadable components и component objects
```java
// Loadable component base class
public abstract class LoadableComponent<T extends LoadableComponent<T>> {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public LoadableComponent(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected abstract void load();

    protected abstract void isLoaded() throws Error;

    @SuppressWarnings("unchecked")
    public T get() {
        try {
            isLoaded();
            return (T) this;
        } catch (Error e) {
            load();
            isLoaded();
            return (T) this;
        }
    }
}

// Component base class
public abstract class BaseComponent {

    protected WebDriver driver;
    protected WebElement rootElement;
    protected WebDriverWait wait;

    public BaseComponent(WebDriver driver, WebElement rootElement) {
        this.driver = driver;
        this.rootElement = rootElement;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected WebElement findElement(By locator) {
        return rootElement.findElement(locator);
    }

    protected List<WebElement> findElements(By locator) {
        return rootElement.findElements(locator);
    }
}

// Login form component
public class LoginForm extends BaseComponent {

    private By usernameField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.id("login-button");
    private By rememberMeCheckbox = By.id("remember-me");

    public LoginForm(WebDriver driver, WebElement formElement) {
        super(driver, formElement);
    }

    public LoginForm enterUsername(String username) {
        findElement(usernameField).sendKeys(username);
        return this;
    }

    public LoginForm enterPassword(String password) {
        findElement(passwordField).sendKeys(password);
        return this;
    }

    public LoginForm checkRememberMe() {
        WebElement checkbox = findElement(rememberMeCheckbox);
        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        return this;
    }

    public void submit() {
        findElement(loginButton).click();
    }
}

// Navigation menu component
public class NavigationMenu extends BaseComponent {

    private By menuItems = By.cssSelector(".nav-item");
    private By userMenu = By.id("user-menu");
    private By logoutLink = By.id("logout-link");

    public NavigationMenu(WebDriver driver, WebElement navElement) {
        super(driver, navElement);
    }

    public List<String> getMenuItemTexts() {
        return findElements(menuItems).stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }

    public NavigationMenu clickMenuItem(String itemText) {
        findElements(menuItems).stream()
            .filter(item -> item.getText().equals(itemText))
            .findFirst()
            .ifPresent(WebElement::click);
        return this;
    }

    public UserMenu openUserMenu() {
        findElement(userMenu).click();
        WebElement userMenuElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("user-menu-dropdown")));
        return new UserMenu(driver, userMenuElement);
    }
}

// Enhanced login page with components
public class EnhancedLoginPage extends LoadableComponent<EnhancedLoginPage> {

    private WebDriver driver;
    private WebDriverWait wait;

    private By formLocator = By.id("login-form");
    private By pageTitle = By.tagName("h1");

    public EnhancedLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Override
    protected void load() {
        driver.get("http://localhost:8080/login");
    }

    @Override
    protected void isLoaded() throws Error {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
            assertThat(driver.findElement(pageTitle).getText()).isEqualTo("Login");
        } catch (Exception e) {
            throw new Error("Login page not loaded properly", e);
        }
    }

    public LoginForm getLoginForm() {
        WebElement formElement = wait.until(
            ExpectedConditions.visibilityOfElementLocated(formLocator));
        return new LoginForm(driver, formElement);
    }

    public DashboardPage login(String username, String password) {
        getLoginForm()
            .enterUsername(username)
            .enterPassword(password)
            .submit();

        return new DashboardPage(driver).get();
    }
}

// Usage
@SpringBootTest
public class ComponentTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testComponentBasedLogin() {
        EnhancedLoginPage loginPage = new EnhancedLoginPage(driver).get();

        DashboardPage dashboard = loginPage.login("testuser", "password");

        assertThat(dashboard.getWelcomeMessage()).contains("Welcome");
    }

    @Test
    void testNavigationMenu() {
        // Assuming we're logged in
        driver.get("http://localhost:8080/dashboard");

        WebElement navElement = driver.findElement(By.id("main-nav"));
        NavigationMenu navMenu = new NavigationMenu(driver, navElement);

        assertThat(navMenu.getMenuItemTexts()).contains("Dashboard", "Profile", "Settings");

        UserMenu userMenu = navMenu.openUserMenu();
        userMenu.clickLogout();
        // Should be redirected to login page
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Data-driven testing

### Test data providers

#### Data-driven подход с Selenium
```java
@SpringBootTest
public class DataDrivenSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    static Stream<Arguments> loginTestData() {
        return Stream.of(
            Arguments.of("validuser", "validpass", true, "Welcome"),
            Arguments.of("invaliduser", "invalidpass", false, "Invalid credentials"),
            Arguments.of("", "password", false, "Username is required"),
            Arguments.of("user", "", false, "Password is required"),
            Arguments.of("admin", "admin123", true, "Welcome Admin")
        );
    }

    @ParameterizedTest
    @MethodSource("loginTestData")
    void testLoginScenarios(String username, String password, boolean shouldSucceed, String expectedMessage) {
        driver.get("http://localhost:8080/login");

        // Enter credentials
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();

        if (shouldSucceed) {
            // Should be redirected to dashboard
            WebElement welcomeMessage = driver.findElement(By.className("welcome-message"));
            assertThat(welcomeMessage.getText()).contains(expectedMessage);
        } else {
            // Should show error message
            WebElement errorMessage = driver.findElement(By.className("error-message"));
            assertThat(errorMessage.getText()).contains(expectedMessage);
        }
    }

    static Stream<Arguments> formValidationData() {
        return Stream.of(
            Arguments.of(Map.of("name", "", "email", "test@example.com"), "Name is required"),
            Arguments.of(Map.of("name", "Test", "email", ""), "Email is required"),
            Arguments.of(Map.of("name", "Test", "email", "invalid-email"), "Invalid email format"),
            Arguments.of(Map.of("name", "Test", "email", "test@example.com"), null) // Valid case
        );
    }

    @ParameterizedTest
    @MethodSource("formValidationData")
    void testFormValidation(Map<String, String> formData, String expectedError) {
        driver.get("http://localhost:8080/form");

        // Fill form
        if (!formData.get("name").isEmpty()) {
            driver.findElement(By.id("name")).sendKeys(formData.get("name"));
        }
        if (!formData.get("email").isEmpty()) {
            driver.findElement(By.id("email")).sendKeys(formData.get("email"));
        }

        driver.findElement(By.id("submit")).click();

        if (expectedError != null) {
            WebElement errorMessage = driver.findElement(By.className("error-message"));
            assertThat(errorMessage.getText()).contains(expectedError);
        } else {
            WebElement successMessage = driver.findElement(By.className("success-message"));
            assertThat(successMessage.getText()).contains("Form submitted");
        }
    }

    @Test
    void testCsvDataDriven() {
        // Read test data from CSV
        List<Map<String, String>> testData = readCsvTestData("user-registration-data.csv");

        for (Map<String, String> userData : testData) {
            driver.get("http://localhost:8080/register");

            // Fill registration form
            driver.findElement(By.id("firstName")).sendKeys(userData.get("firstName"));
            driver.findElement(By.id("lastName")).sendKeys(userData.get("lastName"));
            driver.findElement(By.id("email")).sendKeys(userData.get("email"));
            driver.findElement(By.id("password")).sendKeys(userData.get("password"));

            // Select country
            Select countrySelect = new Select(driver.findElement(By.id("country")));
            countrySelect.selectByVisibleText(userData.get("country"));

            // Check newsletter if specified
            if ("true".equals(userData.get("newsletter"))) {
                driver.findElement(By.id("newsletter")).click();
            }

            driver.findElement(By.id("register")).click();

            // Verify result based on expected outcome
            if ("SUCCESS".equals(userData.get("expectedResult"))) {
                WebElement successMessage = driver.findElement(By.className("success"));
                assertThat(successMessage.getText()).contains("Registration successful");
            } else {
                WebElement errorMessage = driver.findElement(By.className("error"));
                assertThat(errorMessage.getText()).contains(userData.get("expectedError"));
            }
        }
    }

    private List<Map<String, String>> readCsvTestData(String filename) {
        // Implementation to read CSV file and return test data
        // This would typically use a CSV library like OpenCSV
        return List.of(
            Map.of(
                "firstName", "John",
                "lastName", "Doe",
                "email", "john@example.com",
                "password", "password123",
                "country", "United States",
                "newsletter", "true",
                "expectedResult", "SUCCESS"
            )
        );
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Advanced scenarios

### Screenshots and reporting

#### Скриншоты и отчеты
```java
@SpringBootTest
public class ScreenshotTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testWithScreenshotOnFailure() {
        try {
            driver.get("http://localhost:8080/form");

            driver.findElement(By.id("name")).sendKeys("Test User");
            driver.findElement(By.id("email")).sendKeys("invalid-email");

            driver.findElement(By.id("submit")).click();

            // This should fail due to invalid email
            WebElement successMessage = driver.findElement(By.className("success"));
            assertThat(successMessage.isDisplayed()).isTrue();

        } catch (AssertionError e) {
            // Take screenshot on failure
            takeScreenshot("form-validation-failure");
            throw e;
        }
    }

    @Test
    void testScreenshotComparison() {
        driver.get("http://localhost:8080/dashboard");

        // Take baseline screenshot
        byte[] baselineScreenshot = takeScreenshot("dashboard-baseline");

        // Perform some action
        driver.findElement(By.id("refresh")).click();

        // Take comparison screenshot
        byte[] comparisonScreenshot = takeScreenshot("dashboard-after-refresh");

        // Compare screenshots (simplified)
        assertThat(baselineScreenshot.length).isEqualTo(comparisonScreenshot.length);

        // More sophisticated comparison would use image comparison libraries
    }

    @Test
    void testElementScreenshot() {
        driver.get("http://localhost:8080/profile");

        WebElement profileCard = driver.findElement(By.className("profile-card"));

        // Take screenshot of specific element
        byte[] elementScreenshot = profileCard.getScreenshotAs(OutputType.BYTES);

        // Save or analyze the element screenshot
        saveScreenshot(elementScreenshot, "profile-card");

        // Verify screenshot was taken
        assertThat(elementScreenshot).isNotEmpty();
    }

    private byte[] takeScreenshot(String filename) {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);
        saveScreenshot(screenshotBytes, filename);
        return screenshotBytes;
    }

    private void saveScreenshot(byte[] screenshot, String filename) {
        try {
            Path screenshotPath = Paths.get("target", "screenshots", filename + ".png");
            Files.createDirectories(screenshotPath.getParent());
            Files.write(screenshotPath, screenshot);
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }

    @Test
    void testVisualRegression() {
        driver.get("http://localhost:8080/landing-page");

        // Take screenshot
        byte[] currentScreenshot = takeScreenshot("landing-page-current");

        // Load baseline screenshot
        byte[] baselineScreenshot = loadBaselineScreenshot("landing-page-baseline.png");

        // Compare with baseline (simplified)
        if (!Arrays.equals(currentScreenshot, baselineScreenshot)) {
            // Visual difference detected - this might indicate a regression
            System.out.println("Visual difference detected!");

            // In real implementation, you would use image comparison libraries
            // like Applitools Eyes, Percy, or custom image diff tools
        }
    }

    private byte[] loadBaselineScreenshot(String filename) {
        try {
            return Files.readAllBytes(Paths.get("src/test/resources/baselines", filename));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load baseline screenshot", e);
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### JavaScript execution

#### Выполнение JavaScript в браузере
```java
@SpringBootTest
public class JavaScriptTest {

    private WebDriver driver;
    private JavascriptExecutor jsExecutor;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        jsExecutor = (JavascriptExecutor) driver;
    }

    @Test
    void testJavaScriptExecution() {
        driver.get("http://localhost:8080/javascript-test");

        // Execute JavaScript to get page title
        String title = (String) jsExecutor.executeScript("return document.title;");
        assertThat(title).isEqualTo("JavaScript Test Page");

        // Execute JavaScript to click element
        jsExecutor.executeScript("document.getElementById('js-button').click();");

        // Verify result
        WebElement result = driver.findElement(By.id("result"));
        assertThat(result.getText()).contains("clicked");
    }

    @Test
    void testJavaScriptElementInteraction() {
        driver.get("http://localhost:8080/form");

        WebElement hiddenField = driver.findElement(By.id("hidden-field"));

        // Make hidden field visible using JavaScript
        jsExecutor.executeScript("arguments[0].style.display = 'block';", hiddenField);

        // Now we can interact with it
        hiddenField.sendKeys("Hidden value");

        // Submit form
        jsExecutor.executeScript("document.getElementById('form').submit();");

        // Verify submission
        WebElement successMessage = driver.findElement(By.className("success"));
        assertThat(successMessage.getText()).contains("Form submitted");
    }

    @Test
    void testJavaScriptValidation() {
        driver.get("http://localhost:8080/validation");

        // Check if JavaScript validation is working
        Boolean isValid = (Boolean) jsExecutor.executeScript(
            "return document.getElementById('email').checkValidity();"
        );

        assertThat(isValid).isFalse(); // Field is empty

        // Enter valid email
        driver.findElement(By.id("email")).sendKeys("test@example.com");

        isValid = (Boolean) jsExecutor.executeScript(
            "return document.getElementById('email').checkValidity();"
        );

        assertThat(isValid).isTrue();
    }

    @Test
    void testScrollAndViewport() {
        driver.get("http://localhost:8080/long-page");

        // Scroll to bottom
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        // Wait for content to load
        WebElement bottomElement = driver.findElement(By.id("bottom-element"));
        assertThat(bottomElement.isDisplayed()).isTrue();

        // Scroll to top
        jsExecutor.executeScript("window.scrollTo(0, 0);");

        // Scroll to specific element
        WebElement targetElement = driver.findElement(By.id("target-section"));
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", targetElement);

        assertThat(targetElement.isDisplayed()).isTrue();
    }

    @Test
    void testLocalStorage() {
        driver.get("http://localhost:8080/storage");

        // Set localStorage value
        jsExecutor.executeScript("localStorage.setItem('testKey', 'testValue');");

        // Get localStorage value
        String storedValue = (String) jsExecutor.executeScript(
            "return localStorage.getItem('testKey');"
        );

        assertThat(storedValue).isEqualTo("testValue");

        // Clear localStorage
        jsExecutor.executeScript("localStorage.clear();");

        String clearedValue = (String) jsExecutor.executeScript(
            "return localStorage.getItem('testKey');"
        );

        assertThat(clearedValue).isNull();
    }

    @Test
    void testAjaxMonitoring() {
        driver.get("http://localhost:8080/ajax-monitor");

        // Monitor AJAX requests
        jsExecutor.executeScript("""
            window.ajaxRequests = [];
            (function() {
                var origOpen = XMLHttpRequest.prototype.open;
                XMLHttpRequest.prototype.open = function() {
                    window.ajaxRequests.push(arguments);
                    this.addEventListener('load', function() {
                        console.log('AJAX request completed');
                    });
                    origOpen.apply(this, arguments);
                };
            })();
            """);

        // Trigger AJAX request
        driver.findElement(By.id("ajax-button")).click();

        // Wait for AJAX and check requests
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> {
            Long requestCount = (Long) jsExecutor.executeScript(
                "return window.ajaxRequests.length;"
            );
            return requestCount > 0;
        });

        // Verify AJAX request was made
        Long requestCount = (Long) jsExecutor.executeScript(
            "return window.ajaxRequests.length;"
        );

        assertThat(requestCount).isGreaterThan(0);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Spring Boot integration

### Spring Test with Selenium

#### Интеграция Spring Boot и Selenium
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebDriver
public class SpringSeleniumTest {

    @Autowired
    private WebDriver driver;

    @LocalServerPort
    private int port;

    @Test
    void testSpringBootEndpoint() {
        driver.get("http://localhost:" + port + "/api/test");

        WebElement response = driver.findElement(By.tagName("pre"));
        assertThat(response.getText()).contains("Spring Boot");
    }

    @Test
    void testThymeleafTemplate() {
        driver.get("http://localhost:" + port + "/");

        WebElement title = driver.findElement(By.tagName("h1"));
        assertThat(title.getText()).isEqualTo("Welcome to Spring Boot");

        WebElement thymeleafContent = driver.findElement(By.id("thymeleaf-content"));
        assertThat(thymeleafContent.getText()).contains("Rendered with Thymeleaf");
    }

    @Test
    void testRestControllerViaWebDriver() {
        driver.get("http://localhost:" + port + "/users");

        // The page displays users from the REST API
        List<WebElement> userElements = driver.findElements(By.className("user-item"));
        assertThat(userElements).hasSizeGreaterThan(0);

        // Click on a user to see details (calls REST API)
        userElements.get(0).click();

        WebElement userDetails = driver.findElement(By.id("user-details"));
        assertThat(userDetails.getText()).contains("User Details");
    }

    @Test
    void testFormSubmissionToRestEndpoint() {
        driver.get("http://localhost:" + port + "/user-form");

        // Fill form that will POST to REST endpoint
        driver.findElement(By.id("name")).sendKeys("Test User");
        driver.findElement(By.id("email")).sendKeys("test@example.com");
        driver.findElement(By.id("submit")).click();

        // Verify success message from REST response
        WebElement successMessage = driver.findElement(By.className("success"));
        assertThat(successMessage.getText()).contains("User created successfully");
    }
}

// Configuration for WebDriver
@Configuration
@Profile("test")
public class WebDriverConfig {

    @Bean
    @Primary
    public WebDriver webDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        return new ChromeDriver(options);
    }
}
```

## Parallel execution

### JUnit 5 parallel execution

#### Параллельное выполнение тестов
```java
@SpringBootTest
public class ParallelExecutionTest {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");

        // Use unique user data directory for each thread
        options.addArguments("--user-data-dir=/tmp/chrome-user-data-" + Thread.currentThread().getId());

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driverThreadLocal.set(driver);
    }

    @AfterEach
    void tearDown() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }

    private WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @Test
    void testLoginUser1() {
        WebDriver driver = getDriver();
        performLoginTest(driver, "user1", "password1");
    }

    @Test
    void testLoginUser2() {
        WebDriver driver = getDriver();
        performLoginTest(driver, "user2", "password2");
    }

    @Test
    void testLoginUser3() {
        WebDriver driver = getDriver();
        performLoginTest(driver, "user3", "password3");
    }

    @Test
    void testDashboardUser1() {
        WebDriver driver = getDriver();
        performDashboardTest(driver, "user1");
    }

    @Test
    void testDashboardUser2() {
        WebDriver driver = getDriver();
        performDashboardTest(driver, "user2");
    }

    private void performLoginTest(WebDriver driver, String username, String password) {
        driver.get("http://localhost:8080/login");

        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();

        WebElement welcomeMessage = driver.findElement(By.className("welcome-message"));
        assertThat(welcomeMessage.getText()).contains("Welcome");
    }

    private void performDashboardTest(WebDriver driver, String username) {
        // Assume user is already logged in or handle login
        driver.get("http://localhost:8080/dashboard");

        WebElement userInfo = driver.findElement(By.id("user-info"));
        assertThat(userInfo.getText()).contains(username);

        WebElement dashboardContent = driver.findElement(By.id("dashboard-content"));
        assertThat(dashboardContent.isDisplayed()).isTrue();
    }
}

// JUnit 5 parallel execution configuration
// junit-platform.properties
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.mode.classes.default = concurrent
junit.jupiter.execution.parallel.config.strategy = dynamic
```

### TestNG parallel execution

#### Параллельное выполнение с TestNG
```java
public class TestNGParallelTest {

    private WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("chrome") String browser) {
        switch (browser.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless");
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLogin() {
        driver.get("http://localhost:8080/login");

        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password");
        driver.findElement(By.id("login-button")).click();

        WebElement welcomeMessage = driver.findElement(By.className("welcome-message"));
        assertThat(welcomeMessage.getText()).contains("Welcome");
    }

    @Test
    public void testUserProfile() {
        driver.get("http://localhost:8080/profile");

        WebElement profileName = driver.findElement(By.id("profile-name"));
        assertThat(profileName.getText()).isNotEmpty();

        WebElement editButton = driver.findElement(By.id("edit-profile"));
        editButton.click();

        WebElement editForm = driver.findElement(By.id("edit-form"));
        assertThat(editForm.isDisplayed()).isTrue();
    }

    @Test
    public void testNavigation() {
        driver.get("http://localhost:8080/");

        driver.findElement(By.linkText("About")).click();
        assertThat(driver.getCurrentUrl()).contains("/about");

        driver.findElement(By.linkText("Contact")).click();
        assertThat(driver.getCurrentUrl()).contains("/contact");

        driver.findElement(By.linkText("Home")).click();
        assertThat(driver.getCurrentUrl()).endsWith("/");
    }
}
```

## Best practices

### 1. Test organization

#### Организация Selenium тестов
```java
@SpringBootTest
public class SeleniumBestPracticesTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        // Use WebDriverManager for automatic driver management
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images"); // Speed up tests

        driver = new ChromeDriver(options);

        // Configure timeouts appropriately
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));

        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("User should be able to login with valid credentials")
    void testSuccessfulLogin() {
        // Given: User is on login page
        driver.get("http://localhost:8080/login");

        // When: User enters valid credentials and submits
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.id("login-button")).click();

        // Then: User should be redirected to dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        WebElement welcomeMessage = driver.findElement(By.className("welcome-message"));
        assertThat(welcomeMessage.getText()).contains("Welcome, testuser");
    }

    @Test
    @DisplayName("User should see error message with invalid credentials")
    void testFailedLogin() {
        // Given: User is on login page
        driver.get("http://localhost:8080/login");

        // When: User enters invalid credentials
        driver.findElement(By.id("username")).sendKeys("invalid");
        driver.findElement(By.id("password")).sendKeys("invalid");
        driver.findElement(By.id("login-button")).click();

        // Then: Error message should be displayed
        WebElement errorMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.className("error-message")));
        assertThat(errorMessage.getText()).contains("Invalid credentials");
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("User should be able to update profile information")
    void testProfileUpdate() {
        // Given: User is logged in and on profile page
        loginAs("testuser", "password123");
        driver.get("http://localhost:8080/profile");

        // When: User updates profile information
        WebElement nameField = driver.findElement(By.id("full-name"));
        nameField.clear();
        nameField.sendKeys("Updated Name");

        WebElement bioField = driver.findElement(By.id("bio"));
        bioField.clear();
        bioField.sendKeys("Updated bio information");

        driver.findElement(By.id("save-profile")).click();

        // Then: Profile should be updated successfully
        WebElement successMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.className("success-message")));
        assertThat(successMessage.getText()).contains("Profile updated");

        // Verify the changes persisted
        driver.navigate().refresh();
        assertThat(nameField.getAttribute("value")).isEqualTo("Updated Name");
        assertThat(bioField.getText()).isEqualTo("Updated bio information");
    }

    @Test
    @DisplayName("Search functionality should work correctly")
    void testSearchFunctionality() {
        // Given: User is on search page
        driver.get("http://localhost:8080/search");

        // When: User searches for a term
        WebElement searchBox = driver.findElement(By.id("search-input"));
        searchBox.sendKeys("selenium");
        driver.findElement(By.id("search-button")).click();

        // Then: Search results should be displayed
        WebElement resultsContainer = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("search-results")));
        List<WebElement> results = resultsContainer.findElements(By.className("result-item"));

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getText().toLowerCase()).contains("selenium");

        // And: Search term should be highlighted
        WebElement highlightedTerm = results.get(0).findElement(By.className("highlight"));
        assertThat(highlightedTerm.getText().toLowerCase()).isEqualTo("selenium");
    }

    @Test
    @DisplayName("Form validation should prevent invalid submissions")
    void testFormValidation() {
        // Given: User is on contact form
        driver.get("http://localhost:8080/contact");

        // When: User tries to submit empty form
        driver.findElement(By.id("submit-contact")).click();

        // Then: Validation errors should be shown
        List<WebElement> errors = driver.findElements(By.className("field-error"));
        assertThat(errors).hasSizeGreaterThan(0);

        // And: Form should not be submitted
        assertThat(driver.getCurrentUrl()).doesNotContain("/contact/success");

        // When: User fills required fields
        driver.findElement(By.id("name")).sendKeys("Test User");
        driver.findElement(By.id("email")).sendKeys("test@example.com");
        driver.findElement(By.id("message")).sendKeys("Test message");
        driver.findElement(By.id("submit-contact")).click();

        // Then: Form should be submitted successfully
        wait.until(ExpectedConditions.urlContains("/contact/success"));
        WebElement successMessage = driver.findElement(By.className("success"));
        assertThat(successMessage.getText()).contains("Message sent");
    }

    private void loginAs(String username, String password) {
        driver.get("http://localhost:8080/login");
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }
}
```

### 2. Locator strategies

#### Лучшие практики для локаторов
```java
@SpringBootTest
public class LocatorStrategiesBestPracticesTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    void testPreferDataAttributes() {
        driver.get("http://localhost:8080/form");

        // Prefer data-testid attributes for test automation
        WebElement nameField = driver.findElement(By.cssSelector("[data-testid='name-input']"));
        WebElement emailField = driver.findElement(By.cssSelector("[data-testid='email-input']"));
        WebElement submitButton = driver.findElement(By.cssSelector("[data-testid='submit-button']"));

        nameField.sendKeys("Test User");
        emailField.sendKeys("test@example.com");
        submitButton.click();

        WebElement successMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='success-message']")));
        assertThat(successMessage.getText()).contains("Form submitted");
    }

    @Test
    void testSemanticLocators() {
        driver.get("http://localhost:8080/navigation");

        // Use semantic HTML elements and roles
        WebElement mainNav = driver.findElement(By.cssSelector("nav[role='navigation']"));
        WebElement homeLink = mainNav.findElement(By.cssSelector("a[aria-label='Home']"));
        WebElement userMenu = driver.findElement(By.cssSelector("button[aria-haspopup='menu']"));

        assertThat(mainNav.isDisplayed()).isTrue();
        assertThat(homeLink.isDisplayed()).isTrue();
        assertThat(userMenu.isDisplayed()).isTrue();

        // Test accessibility attributes
        assertThat(userMenu.getAttribute("aria-expanded")).isEqualTo("false");
        userMenu.click();
        assertThat(userMenu.getAttribute("aria-expanded")).isEqualTo("true");
    }

    @Test
    void testStableLocators() {
        driver.get("http://localhost:8080/dynamic-content");

        // Avoid fragile XPath expressions
        // Bad: //div[2]/table[1]/tbody/tr[3]/td[1]
        // Good: Use IDs, data attributes, or stable CSS selectors

        WebElement userTable = driver.findElement(By.id("user-table"));
        WebElement firstUserRow = userTable.findElement(By.cssSelector("tbody tr:first-child"));
        WebElement firstUserName = firstUserRow.findElement(By.cssSelector("td[data-field='name']"));

        assertThat(firstUserName.getText()).isNotEmpty();

        // Use contains() for text-based locators when text is stable
        WebElement activeUser = driver.findElement(By.xpath("//tr[contains(@class, 'user-row') and contains(.//td, 'Active')]"));
        assertThat(activeUser.isDisplayed()).isTrue();
    }

    @Test
    void testLocatorPerformance() {
        driver.get("http://localhost:8080/large-page");

        // ID locators are fastest
        long startTime = System.currentTimeMillis();
        WebElement elementById = driver.findElement(By.id("target-element"));
        long idTime = System.currentTimeMillis() - startTime;

        // CSS selectors are fast
        startTime = System.currentTimeMillis();
        WebElement elementByCss = driver.findElement(By.cssSelector(".target-element"));
        long cssTime = System.currentTimeMillis() - startTime;

        // XPath can be slower
        startTime = System.currentTimeMillis();
        WebElement elementByXpath = driver.findElement(By.xpath("//div[@class='target-element']"));
        long xpathTime = System.currentTimeMillis() - startTime;

        // All should find the same element
        assertThat(elementById.getText()).isEqualTo(elementByCss.getText());
        assertThat(elementById.getText()).isEqualTo(elementByXpath.getText());

        // Log performance for monitoring
        System.out.printf("Locator performance - ID: %dms, CSS: %dms, XPath: %dms%n",
            idTime, cssTime, xpathTime);
    }

    @Test
    void testLocatorReliability() {
        driver.get("http://localhost:8080/dynamic-page");

        // Test locator reliability with dynamic content
        WebElement dynamicElement = wait.until(driver -> {
            try {
                // Try most reliable locator first
                return driver.findElement(By.id("dynamic-content"));
            } catch (NoSuchElementException e) {
                try {
                    // Fallback to CSS selector
                    return driver.findElement(By.cssSelector("[data-dynamic='content']"));
                } catch (NoSuchElementException ex) {
                    // Final fallback to XPath
                    return driver.findElement(By.xpath("//div[@data-dynamic='content']"));
                }
            }
        });

        assertThat(dynamicElement.isDisplayed()).isTrue();
        assertThat(dynamicElement.getText()).contains("Dynamic content loaded");
    }

    @Test
    void testLocatorAbstraction() {
        driver.get("http://localhost:8080/complex-form");

        // Abstract locators into methods for reusability
        fillPersonalInfo("John", "Doe", "john@example.com");
        fillAddressInfo("123 Main St", "Springfield", "IL", "62701");
        selectPreferences(Arrays.asList("newsletter", "promotions"));

        submitForm();

        verifySubmissionSuccess();
    }

    private void fillPersonalInfo(String firstName, String lastName, String email) {
        driver.findElement(By.id("first-name")).sendKeys(firstName);
        driver.findElement(By.id("last-name")).sendKeys(lastName);
        driver.findElement(By.id("email")).sendKeys(email);
    }

    private void fillAddressInfo(String street, String city, String state, String zip) {
        driver.findElement(By.id("street")).sendKeys(street);
        driver.findElement(By.id("city")).sendKeys(city);
        driver.findElement(By.id("state")).sendKeys(state);
        driver.findElement(By.id("zip")).sendKeys(zip);
    }

    private void selectPreferences(List<String> preferences) {
        for (String preference : preferences) {
            WebElement checkbox = driver.findElement(By.cssSelector("input[type='checkbox'][value='" + preference + "']"));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
        }
    }

    private void submitForm() {
        driver.findElement(By.id("submit-form")).click();
    }

    private void verifySubmissionSuccess() {
        WebElement successMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.className("success-message")));
        assertThat(successMessage.getText()).contains("Form submitted successfully");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### 3. Wait strategies

#### Стратегии ожидания
```java
@SpringBootTest
public class WaitStrategiesBestPracticesTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));

        // Minimal implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

        // Explicit wait with appropriate timeout
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    void testExplicitWaitBestPractices() {
        driver.get("http://localhost:8080/ajax-form");

        // Fill form
        driver.findElement(By.id("name")).sendKeys("Test User");
        driver.findElement(By.id("email")).sendKeys("test@example.com");

        // Click submit - triggers AJAX
        driver.findElement(By.id("submit")).click();

        // Wait for success message with explicit conditions
        WebElement successMessage = wait.until(ExpectedConditions.and(
            ExpectedConditions.visibilityOfElementLocated(By.id("success-message")),
            ExpectedConditions.textToBePresentInElementLocated(By.id("success-message"), "Form submitted")
        ));

        assertThat(successMessage.getText()).contains("Form submitted");

        // Verify form is no longer visible (good UX pattern)
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("submit-form")));
    }

    @Test
    void testFluentWaitForComplexConditions() {
        driver.get("http://localhost:8080/progress-tracker");

        driver.findElement(By.id("start-process")).click();

        // Use FluentWait for complex polling conditions
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofMillis(500))
            .ignoring(StaleElementReferenceException.class);

        WebElement progressBar = fluentWait.until(driver -> {
            WebElement bar = driver.findElement(By.id("progress-bar"));
            String width = bar.getCssValue("width");

            // Wait until progress is at least 90%
            if (width.endsWith("px")) {
                int widthPx = Integer.parseInt(width.replace("px", ""));
                if (widthPx >= 360) { // Assuming 400px = 100%
                    return bar;
                }
            }

            return null; // Continue waiting
        });

        assertThat(progressBar.isDisplayed()).isTrue();

        // Verify completion message appears
        WebElement completionMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("completion-message")));
        assertThat(completionMessage.getText()).contains("Process completed");
    }

    @Test
    void testCustomExpectedConditions() {
        driver.get("http://localhost:8080/chat");

        WebElement messageInput = driver.findElement(By.id("message-input"));
        messageInput.sendKeys("Hello from Selenium!");
        driver.findElement(By.id("send-button")).click();

        // Custom expected condition for chat message
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                List<WebElement> messages = driver.findElements(By.className("chat-message"));
                return messages.stream()
                    .anyMatch(msg -> msg.getText().contains("Hello from Selenium!"));
            }

            @Override
            public String toString() {
                return "chat message containing 'Hello from Selenium!' to be present";
            }
        });

        // Verify message was sent
        List<WebElement> messages = driver.findElements(By.className("chat-message"));
        assertThat(messages).anyMatch(msg -> msg.getText().contains("Hello from Selenium!"));
    }

    @Test
    void testWaitForPageLoad() {
        driver.get("http://localhost:8080/slow-page");

        // Wait for page to be fully loaded
        wait.until(driver -> ((JavascriptExecutor) driver)
            .executeScript("return document.readyState").equals("complete"));

        // Wait for specific elements that indicate page is ready
        wait.until(ExpectedConditions.and(
            ExpectedConditions.visibilityOfElementLocated(By.id("main-content")),
            ExpectedConditions.visibilityOfElementLocated(By.id("navigation")),
            ExpectedConditions.elementToBeClickable(By.id("action-button"))
        ));

        // Now perform interactions
        driver.findElement(By.id("action-button")).click();

        WebElement result = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("action-result")));
        assertThat(result.getText()).contains("Action completed");
    }

    @Test
    void testWaitForNetworkRequests() {
        driver.get("http://localhost:8080/data-loading");

        // Start monitoring network requests (requires DevTools)
        // Note: This is a simplified example
        driver.findElement(By.id("load-data")).click();

        // Wait for network activity to complete
        wait.until(driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Long activeRequests = (Long) js.executeScript(
                "return window.performance.getEntriesByType('navigation').length;");
            return activeRequests > 0; // Simplified check
        });

        // Verify data loaded
        WebElement dataTable = driver.findElement(By.id("data-table"));
        assertThat(dataTable.findElements(By.tagName("tr"))).hasSizeGreaterThan(1);
    }

    @Test
    void testAvoidingRaceConditions() {
        driver.get("http://localhost:8080/multi-step-form");

        // Step 1: Fill personal info
        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("next-step-1")).click();

        // Wait for step 2 to load completely
        wait.until(ExpectedConditions.and(
            ExpectedConditions.invisibilityOfElementLocated(By.id("step-1")),
            ExpectedConditions.visibilityOfElementLocated(By.id("step-2")),
            ExpectedConditions.elementToBeClickable(By.id("address-field"))
        ));

        // Step 2: Fill address
        driver.findElement(By.id("address-field")).sendKeys("123 Main St");
        driver.findElement(By.id("city-field")).sendKeys("Springfield");
        driver.findElement(By.id("next-step-2")).click();

        // Wait for step 3
        wait.until(ExpectedConditions.and(
            ExpectedConditions.invisibilityOfElementLocated(By.id("step-2")),
            ExpectedConditions.visibilityOfElementLocated(By.id("step-3")),
            ExpectedConditions.elementToBeClickable(By.id("submit-form"))
        ));

        // Step 3: Submit
        driver.findElement(By.id("submit-form")).click();

        // Verify completion
        WebElement successMessage = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.className("success")));
        assertThat(successMessage.getText()).contains("Form completed successfully");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Element not found exceptions

**Symptoms:**
- NoSuchElementException or StaleElementReferenceException

**Solutions:**
```java
@SpringBootTest
public class ElementNotFoundTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(new ChromeOptions().addArguments("--headless"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    void testElementNotFoundHandling() {
        driver.get("http://localhost:8080/dynamic-page");

        // Bad: Direct findElement (may fail if element not ready)
        // WebElement element = driver.findElement(By.id("dynamic-element")); // May throw exception

        // Good: Use explicit wait
        WebElement element = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.id("dynamic-element")));

        assertThat(element.isDisplayed()).isTrue();
    }

    @Test
    void testStaleElementHandling() {
        driver.get("http://localhost:8080/dynamic-updates");

        WebElement counterElement = driver.findElement(By.id("counter"));

        // Read initial value
        String initialValue = counterElement.getText();

        // Trigger update that changes the DOM
        driver.findElement(By.id("increment")).click();

        // Bad: Using stale reference
        // String newValue = counterElement.getText(); // StaleElementReferenceException

        // Good: Re-find element after DOM change
        WebElement updatedCounter = wait.until(driver -> {
            WebElement element = driver.findElement(By.id("counter"));
            if (!element.getText().equals(initialValue)) {
                return element;
            }
            return null;
        });

        assertThat(updatedCounter.getText()).isNotEqualTo(initialValue);
    }

    @Test
    void testElementInFrame() {
        driver.get("http://localhost:8080/frames-page");

        // Switch to frame
        driver.switchTo().frame("content-frame");

        // Now find element inside frame
        WebElement frameElement = driver.findElement(By.id("frame-content"));
        assertThat(frameElement.getText()).contains("Frame content");

        // Switch back to main content
        driver.switchTo().defaultContent();
    }

    @Test
    void testShadowDOM() {
        driver.get("http://localhost:8080/shadow-dom");

        // Access shadow root
        WebElement hostElement = driver.findElement(By.id("shadow-host"));
        SearchContext shadowRoot = hostElement.getShadowRoot();

        // Find element inside shadow DOM
        WebElement shadowElement = shadowRoot.findElement(By.className("shadow-content"));
        assertThat(shadowElement.getText()).contains("Shadow content");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

#### Browser compatibility issues

**Symptoms:**
- Tests pass on one browser but fail on another

**Solutions:**
```java
@SpringBootTest
public class BrowserCompatibilityTest {

    private WebDriver driver;

    @BeforeEach
    @ParameterizedTest
    @ValueSource(strings = {"chrome", "firefox", "edge"})
    void setUp(String browser) {
        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--headless");
                driver = new EdgeDriver(edgeOptions);
                break;
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testCrossBrowserCompatibility() {
        driver.get("http://localhost:8080/");

        // Test basic functionality that should work across browsers
        WebElement header = driver.findElement(By.tagName("h1"));
        assertThat(header.isDisplayed()).isTrue();

        WebElement loginLink = driver.findElement(By.linkText("Login"));
        loginLink.click();

        // Wait for navigation
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/login"));

        // Test login form
        WebElement usernameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));

        // Check if elements are interactable
        assertThat(usernameField.isEnabled()).isTrue();
        assertThat(passwordField.isEnabled()).isTrue();

        // Basic interaction test
        usernameField.sendKeys("test");
        assertThat(usernameField.getAttribute("value")).isEqualTo("test");
    }

    @Test
    void testBrowserSpecificFeatures() {
        // Handle browser-specific differences
        if (driver instanceof ChromeDriver) {
            // Chrome-specific code
            System.out.println("Running on Chrome");
        } else if (driver instanceof FirefoxDriver) {
            // Firefox-specific code
            System.out.println("Running on Firefox");
        } else if (driver instanceof EdgeDriver) {
            // Edge-specific code
            System.out.println("Running on Edge");
        }

        driver.get("http://localhost:8080/browser-test");

        // Test feature that may behave differently across browsers
        WebElement testElement = driver.findElement(By.id("browser-specific-element"));

        // Verify element exists and is displayed
        assertThat(testElement.isDisplayed()).isTrue();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

#### Performance issues

**Symptoms:**
- Tests are slow or timeout

**Solutions:**
```java
@SpringBootTest
public class PerformanceOptimizationTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images"); // Speed up page loading
        options.addArguments("--disable-javascript"); // If JS not needed
        options.addArguments("--window-size=1920,1080");

        // Performance preferences
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.managed_default_content_settings.images", 2); // Block images
        prefs.put("profile.managed_default_content_settings.stylesheets", 2); // Block CSS
        options.setExperimentalOption("prefs", prefs);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        // Optimized timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(5));
    }

    @Test
    void testOptimizedPerformance() {
        long startTime = System.currentTimeMillis();

        driver.get("http://localhost:8080/");

        // Quick checks without heavy waits
        WebElement header = driver.findElement(By.tagName("h1"));
        assertThat(header.getText()).isNotEmpty();

        long loadTime = System.currentTimeMillis() - startTime;
        System.out.println("Page load time: " + loadTime + "ms");

        // Assert performance requirement
        assertThat(loadTime).isLessThan(5000L); // Less than 5 seconds
    }

    @Test
    void testBatchOperations() {
        driver.get("http://localhost:8080/batch-form");

        // Perform multiple operations efficiently
        List<String> names = Arrays.asList("User1", "User2", "User3", "User4", "User5");

        for (String name : names) {
            // Clear and fill field quickly
            WebElement nameField = driver.findElement(By.id("name"));
            nameField.clear();
            nameField.sendKeys(name);

            // Submit form
            driver.findElement(By.id("add-user")).click();

            // Quick verification without full wait
            WebElement userList = driver.findElement(By.id("user-list"));
            assertThat(userList.getText()).contains(name);
        }

        // Final verification
        WebElement userCount = driver.findElement(By.id("user-count"));
        assertThat(userCount.getText()).isEqualTo("5");
    }

    @Test
    void testParallelDataEntry() {
        driver.get("http://localhost:8080/multi-field-form");

        // Prepare data
        Map<String, String> formData = Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "email", "john@example.com",
            "phone", "123-456-7890",
            "address", "123 Main St"
        );

        // Fill all fields at once (as fast as possible)
        formData.forEach((fieldId, value) -> {
            WebElement field = driver.findElement(By.id(fieldId));
            field.sendKeys(value);
        });

        // Submit and verify
        driver.findElement(By.id("submit")).click();

        WebElement successMessage = driver.findElement(By.className("success"));
        assertThat(successMessage.getText()).contains("Form submitted");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### Debug techniques

#### Screenshot and logging
```java
@SpringBootTest
public class DebugTechniquesTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    void testWithScreenshotOnFailure() {
        try {
            driver.get("http://localhost:8080/form");

            driver.findElement(By.id("name")).sendKeys("Test User");
            driver.findElement(By.id("email")).sendKeys("invalid-email");

            driver.findElement(By.id("submit")).click();

            // This should fail due to invalid email
            WebElement successMessage = driver.findElement(By.className("success"));
            assertThat(successMessage.isDisplayed()).isTrue();

        } catch (AssertionError e) {
            // Take screenshot on failure
            takeScreenshot("form-validation-failure");
            throw e;
        }
    }

    @Test
    void testWithDetailedLogging() {
        // Enable detailed logging
        Logger.getLogger("org.openqa.selenium").setLevel(Level.INFO);

        driver.get("http://localhost:8080/");

        // Log page information
        System.out.println("Page title: " + driver.getTitle());
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Page source length: " + driver.getPageSource().length());

        // Log element information
        WebElement header = driver.findElement(By.tagName("h1"));
        System.out.println("Header text: " + header.getText());
        System.out.println("Header displayed: " + header.isDisplayed());
        System.out.println("Header enabled: " + header.isEnabled());

        assertThat(header.getText()).isNotEmpty();
    }

    @Test
    void testElementInspection() {
        driver.get("http://localhost:8080/form");

        WebElement formElement = driver.findElement(By.id("user-form"));

        // Inspect element properties
        System.out.println("Element tag: " + formElement.getTagName());
        System.out.println("Element text: " + formElement.getText());
        System.out.println("Element location: " + formElement.getLocation());
        System.out.println("Element size: " + formElement.getSize());

        // Inspect attributes
        System.out.println("Element id: " + formElement.getAttribute("id"));
        System.out.println("Element class: " + formElement.getAttribute("class"));
        System.out.println("Element style: " + formElement.getAttribute("style"));

        // Inspect CSS properties
        System.out.println("Display: " + formElement.getCssValue("display"));
        System.out.println("Visibility: " + formElement.getCssValue("visibility"));
        System.out.println("Background color: " + formElement.getCssValue("background-color"));

        assertThat(formElement.isDisplayed()).isTrue();
    }

    @Test
    void testNetworkLogging() {
        // Enable network logging (Chrome DevTools)
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);

        // This would require restarting driver with new options
        // For demonstration purposes only
        System.out.println("Network logging would be enabled here");
    }

    private void takeScreenshot(String filename) {
        try {
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            byte[] screenshotBytes = screenshot.getScreenshotAs(OutputType.BYTES);

            Path screenshotPath = Paths.get("target", "screenshots", filename + ".png");
            Files.createDirectories(screenshotPath.getParent());
            Files.write(screenshotPath, screenshotBytes);

            System.out.println("Screenshot saved: " + screenshotPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Заключение

**Selenium WebDriver** — это мощный инструмент для автоматизации веб-тестирования, предоставляющий полный контроль над браузером и веб-приложениями. Он позволяет писать надежные end-to-end тесты, которые имитируют реальное пользовательское взаимодействие.

### Ключевые возможности:

1. **Cross-browser Testing** — поддержка Chrome, Firefox, Edge, Safari
2. **Multiple Languages** — bindings для Java, Python, C#, Ruby, JavaScript
3. **Page Object Model** — паттерн для maintainable тестов
4. **Wait Strategies** — implicit, explicit, и fluent waits
5. **Rich Locators** — ID, name, class, XPath, CSS, link text
6. **Actions API** — complex user interactions (drag-drop, keyboard)
7. **Screenshot Support** — визуальная отладка и regression testing
8. **Grid Support** — distributed test execution
9. **Headless Mode** — выполнение без GUI для CI/CD

### Архитектурные преимущества:

#### End-to-End Testing:
- **Real Browser Testing** — тесты в реальных браузерах
- **User Journey Validation** — полное тестирование user flows
- **Integration Testing** — тестирование frontend-backend интеграции
- **Cross-browser Compatibility** — проверка совместимости
- **Visual Regression** — обнаружение визуальных изменений
- **Performance Validation** — проверка загрузки страниц
- **JavaScript Testing** — тестирование dynamic content

#### Automation Framework:
- **Reusable Components** — Page Objects и components
- **Data-driven Testing** — parameterized test execution
- **Parallel Execution** — одновременный запуск тестов
- **Screenshot on Failure** — автоматические скриншоты при ошибках
- **Test Reporting** — детальные отчеты о выполнении
- **CI/CD Integration** — интеграция в automated pipelines

### Когда использовать Selenium:

✅ **End-to-End Testing** — комплексное тестирование приложений
✅ **UI Regression Testing** — проверка изменений интерфейса
✅ **Cross-browser Testing** — совместимость с разными браузерами
✅ **User Acceptance Testing** — валидация с точки зрения пользователя
✅ **Integration Testing** — frontend-backend взаимодействие
✅ **Visual Testing** — проверка визуального оформления
✅ **Complex User Interactions** — drag-drop, file upload, etc.
✅ **JavaScript-heavy Apps** — SPA и dynamic content
✅ **Legacy Applications** — тестирование существующих систем

### Когда НЕ использовать:

❌ **Unit Testing** — для unit тестов лучше JUnit/Mockito
❌ **API Testing** — для API лучше REST Assured/WireMock
❌ **Performance Testing** — для load testing лучше JMeter/Gatling
❌ **Mobile Testing** — для mobile лучше Appium
❌ **Simple Assertions** — для basic checks избыточно
❌ **Fast Feedback** — медленнее unit тестов
❌ **Flaky Tests** — может быть unstable без proper waits
❌ **No Browser Access** — требуется браузер для выполнения

### Best practices:

1. **Page Object Model** — инкапсуляция page logic
2. **Explicit Waits** — избегать race conditions
3. **Stable Locators** — использовать data attributes
4. **Test Data Management** — proper test data lifecycle
5. **Screenshot on Failure** — визуальная отладка
6. **Parallel Execution** — скорость выполнения
7. **Cross-browser Testing** — compatibility validation
8. **Fluent Assertions** — readable test code
9. **Test Organization** — structured test suites
10. **CI/CD Integration** — automated execution

### Типы Testing по сложности:

#### Basic Selenium:
- **Element Location** — find elements by various locators
- **Simple Interactions** — click, type, select
- **Basic Assertions** — verify text, visibility, state
- **Single Page Tests** — tests within one page

#### Advanced Selenium:
- **Page Objects** — reusable page abstractions
- **Wait Strategies** — handling dynamic content
- **Complex Interactions** — drag-drop, keyboard shortcuts
- **Multi-page Flows** — navigation and state management

#### Specialized Selenium:
- **Cross-browser Testing** — multiple browser support
- **Parallel Execution** — concurrent test runs
- **Screenshot Testing** — visual regression detection
- **JavaScript Execution** — browser automation
- **File Operations** — upload/download testing
- **Network Monitoring** — request/response inspection

Selenium является essential инструментом для modern web application testing. Он предоставляет comprehensive solution для UI automation с excellent support для various testing scenarios и integrations. 🚀

**Далее: Cucumber (BDD testing)**
