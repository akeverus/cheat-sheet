---
title: "Вопросы на собеседовании: Selenium WebDriver"
description: "Selenium WebDriver: локаторы (By), ожидания (implicit/explicit/fluent), Page Object Model, параллельные тесты, Selenium Grid, альтернативы Playwright/Cypress"
tags:
  - interview
  - testing
  - selenium-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Selenium WebDriver"
  - "Selenium WebDriver interview"
  - "Selenium собеседование"
prerequisites:
  - "[[selenium]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Selenium WebDriver`

`Selenium WebDriver` — стандартный инструмент для E2E тестирования веб-интерфейсов. Предоставляет API для управления браузером (Chrome, Firefox, Safari). Важная тема для Java-разработчиков, занимающихся автоматизацией UI или поддерживающих legacy тесты.

Дата последнего обновления: 2026-05-15

## Полезные ссылки

### Официальная документация

- [Selenium Docs](https://www.selenium.dev/documentation/) — официальная документация
- [Baeldung: Selenium](https://www.baeldung.com/java-selenium-with-junit-and-testng) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Selenium WebDriver и как устроена его архитектура?

**Selenium WebDriver** — библиотека, которая управляет реальным браузером так, как это делал бы человек: открывает страницы, кликает, вводит текст, читает содержимое. Общение с браузером идёт по протоколу WebDriver (с 2018 года — W3C-стандарт), поэтому один и тот же код работает с Chrome, Firefox, Safari и Edge.

**Как устроена архитектура.** Ключевая идея — между тестом и браузером стоит отдельный процесс-драйвер. Тест не дёргает браузер напрямую: он отправляет HTTP-команды драйверу (`chromedriver`, `geckodriver`), а тот переводит их в нативные вызовы конкретного браузера. Благодаря этой прослойке Selenium и остаётся кросс-браузерным — меняется только драйвер, не код теста.

```text
Test Code → Selenium WebDriver API → JSON Wire Protocol → Browser Driver → Browser
         (Java/Python/JS)            (HTTP)               (chromedriver)  (Chrome)
```

Цепочка читается слева направо: тест на Java/Python/JS вызывает API → команда сериализуется в HTTP-запрос → драйвер браузера принимает его → браузер выполняет действие и возвращает результат тем же путём обратно.

```java
// Базовый пример
WebDriver driver = new ChromeDriver();
driver.get("https://example.com");
WebElement button = driver.findElement(By.id("submit"));
button.click();
driver.quit();
```

**Компоненты экосистемы**:
- **WebDriver** — API, которым пишут тесты (`findElement`, `click`, `get` и т.д.).
- **Browser Driver** (`chromedriver`, `geckodriver` и др.) — мост между WebDriver и браузером; для каждого браузера свой и версия драйвера должна совпадать с версией браузера.
- **Selenium Grid** — распределённый запуск: один хаб раздаёт тесты на множество машин и браузеров.
- **Selenium IDE** — браузерное расширение с записью-воспроизведением действий; годится для быстрого прототипа, но не для поддерживаемых тестов.

## Q2. Какие локаторы есть в Selenium и когда какой использовать?

Локатор — это способ указать WebDriver, какой элемент на странице найти. Selenium предлагает восемь стратегий через класс `By`; выбор локатора напрямую влияет на скорость и стабильность теста.

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

**Приоритет при выборе локатора** (от лучшего к худшему):
1. **`id`** — самый быстрый и однозначный: браузер ищет по `id` нативно. Используйте, когда у элемента есть стабильный `id`.
2. **`name`** — удобен для полей форм.
3. **`cssSelector`** — основная рабочая лошадка. Предпочтительнее XPath: короче, читаемее и быстрее, потому что движок CSS у браузера нативный.
4. **`xpath`** — только там, где CSS бессилен: поиск по тексту элемента, переход вверх по иерархии (к родителю), сложная навигация по дереву.

**Подводный камень.** Локаторы по видимому тексту (`linkText`, XPath с `text()`) ломаются при локализации: смена языка интерфейса — и тест перестаёт находить кнопку. Для устойчивости опирайтесь на технические атрибуты, в идеале — на специально добавленный `data-testid`, который не меняется при правках вёрстки или текста.

## Q3. В чём разница между implicit и explicit wait?

Ожидания нужны, потому что современная страница подгружает элементы асинхронно (AJAX, анимации), и к моменту поиска элемент может ещё не появиться. Главное отличие в области действия: **implicit wait — глобальный таймаут на любой поиск элемента**, а **explicit wait — ожидание конкретного условия для конкретного элемента**.

- **Implicit wait** включается один раз и действует на все последующие `findElement`: если элемент не найден сразу, WebDriver будет повторять попытки до истечения таймаута. Просто, но негибко — нельзя дождаться, например, «кнопка стала кликабельной».
- **Explicit wait** (`WebDriverWait` + `ExpectedConditions`) ждёт именно нужное состояние: появление, видимость, кликабельность, наличие текста. Это самый гибкий и рекомендуемый подход.
- **Fluent wait** — explicit wait с тонкой настройкой: задаёте интервал опроса (polling) и список исключений, которые нужно игнорировать во время ожидания.

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

| Тип wait | Область действия | Особенность |
|----------|------------------|-------------|
| Implicit | Все вызовы `findElement` | Удобно, но глобально — трудно контролировать в отдельной точке |
| Explicit | Конкретный элемент и условие | Самый гибкий, рекомендуемый по умолчанию |
| Fluent | Полный контроль polling и игнорируемых исключений | Для кастомных условий ожидания |

**Подводный камень: не смешивайте implicit и explicit wait.** Их таймауты складываются непредсказуемо (например, explicit-ожидание `30s` поверх implicit `10s` может фактически ждать дольше из-за вложенного ретрая), и итоговое поведение становится трудно объяснимым. Практика: выключите implicit wait и используйте только explicit.

## Q4. Почему `Thread.sleep()` — плохая практика в UI-тестах?

Коротко: `Thread.sleep()` ждёт фиксированное время, а не нужное событие. Он не знает, готов элемент или нет, — просто слепо стоит указанное число секунд. Это даёт худшее из двух миров: тест и медленный, и нестабильный одновременно.

```java
// ПЛОХО
driver.findElement(By.id("load")).click();
Thread.sleep(5000);   // ждём фиксированное время
driver.findElement(By.id("result")).getText();
```

**Подводные камни**:
1. **Медленно** — ждём все 5 секунд, даже если элемент готов уже через одну. На сотнях тестов это десятки лишних минут прогона.
2. **Flaky** — при медленной сети или нагрузке 5 секунд может не хватить, и тест упадёт на ровном месте. Подобрать «достаточное» число невозможно: либо медленно, либо ненадёжно.
3. **Не реагирует на состояние страницы** — ждёт время, а не условие. Решение — `WebDriverWait`, который опрашивает страницу и продолжает сразу, как только нужный элемент появился (но не дольше таймаута).

```java
// ХОРОШО
driver.findElement(By.id("load")).click();

WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement result = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("result")));
String text = result.getText();
```

`ExpectedConditions` — набор готовых условий ожидания на любой случай: `visibilityOf` (элемент виден), `invisibilityOf` (исчез — например, спиннер загрузки), `elementToBeClickable` (виден и кликабелен), `textToBePresentInElement` (появился нужный текст), `alertIsPresent` (всплыл alert), `frameToBeAvailableAndSwitchToIt` (фрейм готов, и мы в него переключились) и др.

**Рекомендация.** Подбирайте условие под реальную цель: для клика — `elementToBeClickable` (просто `presenceOf` не гарантирует, что по элементу можно кликнуть), для чтения текста — `visibilityOf`.

## Q5. Что такое Page Object Pattern?

**Page Object (POM)** — паттерн, в котором каждая страница (или крупный экран) приложения представлена отдельным классом. Внутри него спрятаны две вещи: локаторы элементов и действия пользователя на странице (`login()`, `addToCart()`). Тест работает не с локаторами напрямую, а с понятными методами объекта-страницы.

Главная идея — **отделить «что делает тест» от «как технически устроена страница»**. Сам сценарий остаётся стабильным, а хрупкие детали (CSS-селекторы, ID) живут в одном месте.

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

Обратите внимание на приём: метод `login()` возвращает `DashboardPage` — следующую страницу, на которую попадает пользователь после входа. Так цепочка переходов читается естественно, а IDE подсказывает доступные на новой странице действия.

**Плюсы**:
- **Переиспользование** — `login()` написан один раз и доступен из любого теста.
- **Поддерживаемость** — при изменении вёрстки правишь локатор в одном месте, а не в десятках тестов.
- **Читаемость** — тест выглядит как описание сценария на бизнес-языке (`loginPage.login(...)`), а не как набор `findElement`.

## Q6. Что такое Page Factory и как работают @FindBy?

**Page Factory** — встроенный в Selenium механизм инициализации Page Object, который заменяет ручные `driver.findElement(...)` на декларативные аннотации над полями. Локатор пишется прямо над `WebElement` через `@FindBy`, а вызов `PageFactory.initElements(driver, this)` связывает поля с элементами страницы.

Аннотации:
- **`@FindBy`** — один локатор на одно поле.
- **`@FindBys`** — цепочка локаторов по логике **И** (вложенность): искать `<a>` внутри `.list-item`.
- **`@FindAll`** — несколько локаторов по логике **ИЛИ**: подойдёт любой совпавший.

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

**Ключевая особенность.** Поля `WebElement` — это не сами элементы, а ленивые прокси. Реальный `findElement` выполняется не при инициализации, а в момент каждого обращения к полю. Поэтому если страница перерисовалась и старая ссылка устарела, прокси найдёт элемент заново — это частично спасает от `StaleElementReferenceException`.

**Подводный камень.** Прокси находит элемент при каждом обращении, но не перепроверяет его автоматически между двумя строками вашего кода: если элемент исчез после получения ссылки внутри одной операции, `StaleElementReferenceException` всё равно возможен. Поэтому современная практика всё чаще предпочитает явные `driver.findElement` с `WebDriverWait` вместо PageFactory.

## Q7. Как работать с alerts, frames и окнами (windows)?

Эти три сущности объединяет одно: они живут вне обычного DOM текущей страницы, поэтому WebDriver не видит их по умолчанию — нужно явно **переключить контекст** методами `switchTo()`.

- **Alert** — нативное браузерное окно (`alert`/`confirm`/`prompt`). Его нельзя найти через `findElement`; только `switchTo().alert()`, после чего доступны `accept()`, `dismiss()`, `getText()`, `sendKeys()`.
- **Frame/iframe** — отдельный вложенный документ. Пока вы не переключитесь в него, элементы внутри фрейма «невидимы». После работы обязательно вернитесь в основной документ через `defaultContent()`.
- **Window/Tab** — каждое окно и вкладка имеют свой идентификатор (`window handle`). Чтобы работать в новой вкладке, нужно переключиться на её handle.

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

**Подводный камень.** `driver.close()` закрывает только текущее окно, а `driver.quit()` завершает всю сессию и все окна. После закрытия окна WebDriver не переключается на оставшееся автоматически — нужно явно вызвать `switchTo().window(...)`, иначе следующая команда упадёт с `NoSuchWindowException`.

## Q8. Что такое Selenium Grid и когда его использовать?

**Selenium Grid** — инфраструктура для распределённого запуска тестов: вместо одной машины тесты выполняются параллельно на пуле машин и в разных браузерах. Схема «хаб + ноды»: тест шлёт команды на хаб (через `RemoteWebDriver`), а хаб распределяет их по подходящим нодам — машинам с установленными браузерами.

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

**Когда применять**:
- **Cross-browser testing** — прогнать одни и те же тесты в Chrome, Firefox, Safari, Edge сразу, чтобы поймать браузер-специфичные баги.
- **Параллелизация ради скорости** — 100 тестов на 10 нодах выполняются примерно в 10 раз быстрее, чем последовательно на одной машине.
- **Проверка под разные ОС** — нода под Safari только на macOS, нода под Edge на Windows и т.д.

**Альтернативы.** Поднимать и обслуживать собственный Grid дорого, поэтому на практике чаще берут готовые решения: **Docker Selenium** и **Selenoid** (Grid в контейнерах, на своей инфраструктуре) или облачные фермы **BrowserStack** и **Sauce Labs** (десятки реальных браузеров и устройств без своего железа).

## Q9. Как запускать UI-тесты параллельно?

Параллельный запуск ускоряет прогон, но упирается в одно фундаментальное ограничение: **экземпляр `WebDriver` не потокобезопасен**. Если несколько потоков работают с одним драйвером, команды перемешиваются и тесты падают непредсказуемо. Поэтому базовое правило — **свой `WebDriver` на каждый поток (или тест)**.

Технически это решается двумя способами:
- **Новый драйвер на каждый тест** — простой `@BeforeEach`/`@AfterEach` (см. ниже). Подходит, пока driver не нужно шарить между Page Object.
- **`ThreadLocal<WebDriver>`** — драйвер привязан к потоку: разные потоки видят разные экземпляры через один и тот же `getDriver()`. Это позволяет обращаться к драйверу из любого места кода, не передавая его параметром.

TestNG исторически проще для параллельных UI-тестов: уровень параллелизма (`methods`/`classes`/`tests`) и число потоков задаются прямо в `suite.xml`.

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

## Q10. Как бороться с нестабильными (flaky) тестами?

Flaky-тест — это тест, который то проходит, то падает без изменений в коде. Главная причина в UI — гонка между тестом и асинхронной отрисовкой страницы. Поэтому борьба идёт по двум направлениям: **устранить причину нестабильности** (правильные ожидания и локаторы) и **снизить ущерб от оставшейся** (ретраи, диагностика, изоляция).

Практические приёмы:
1. **Ретраи** (`@RetryingTest`) — пластырь, а не лечение: маскируют нестабильность, поэтому применяйте осознанно и не вместо нормальных ожиданий.
2. **Явные ожидания вместо `sleep`** — убирают саму гонку (см. Q4).
3. **Стабильные локаторы** — `data-testid` не ломается при рефакторинге CSS и текста (см. Q2).
4. **Скриншоты при падении** — без них отлаживать «иногда падает в CI» почти невозможно.
5. **Изоляция явно flaky-тестов** — пометить тегом и временно отключить с тикетом, чтобы они не «зашумляли» зелёный билд, пока их чинят.

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

## Q11. Какие альтернативы Selenium существуют и чем они отличаются?

Все альтернативы выросли из главных болей Selenium: он медленный, склонен к flaky-тестам и требует ручных ожиданий. Новые инструменты в первую очередь добавляют **auto-wait** (автоматическое ожидание готовности элемента перед действием), что снимает большую часть нестабильности.

| Инструмент | Плюсы | Минусы |
|------------|-------|--------|
| **Selenium** | Зрелый, кросс-браузерный, любые языки | Медленный, склонен к flaky |
| **Playwright** | Быстрый, auto-wait, кросс-браузерный | Моложе (с 2020) |
| **Cypress** | Простой, живой debug в браузере | В основном Chromium-семейство |
| **Puppeteer** | Глубокий контроль над Chrome | Только Chrome, без кросс-браузерности |
| **WebDriverIO** | Node.js, mocha-стиль | Слабее поддержка Java |
| **TestCafe** | Не требует WebDriver, auto-wait | Моложе, меньше плагинов |

**Главный тренд — Playwright.** Он совмещает кросс-браузерность Selenium со встроенным auto-wait: в примере ниже `click()` сам дождётся, пока кнопка появится и станет кликабельной, без явного `WebDriverWait`.

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

## Q12. Как организовать Selenium-тесты в CI/CD?

В CI нет экрана и графической оболочки, поэтому браузер запускают в **headless-режиме** — без окна, только движок рендеринга. Три ключевых момента надёжного UI-прогона в пайплайне:
- **Headless + правильные флаги** — `--no-sandbox` и `--disable-dev-shm-usage` обязательны в Docker/CI, иначе Chrome падает на старте из-за ограничений песочницы и нехватки `/dev/shm`.
- **Сохранение артефактов при падении** — скриншоты (и при желании видео/логи) грузятся как artifact только `if: failure()`. Без них «упало в CI, локально не воспроизводится» не отладить.
- **Управление драйверами** — версия `chromedriver` должна совпадать с версией браузера; **WebDriverManager** скачивает и подбирает нужную версию автоматически, избавляя от ручного обновления путей.

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

## Q13. Что такое BDD-тесты с Cucumber?

**BDD (Behavior-Driven Development)** — подход, где тест описывает поведение системы на естественном языке, понятном не только разработчику. **Cucumber** реализует его так: сценарий пишется в `.feature`-файле на синтаксисе **Gherkin** (`Given`/`When`/`Then`), а каждая его строка связывается с Java-методом — *step definition*. Связка **Cucumber + Selenium** означает, что эти шаги под капотом дёргают WebDriver.

Как это работает: `Given/When/Then` из сценария сопоставляется по шаблону (`@Given("I am on the login page")`) с методом; `{string}` извлекает параметр прямо из текста шага и передаёт его в метод.

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

**Когда применять.** BDD оправдан, когда сценарии пишут или читают не-разработчики (QA, бизнес-аналитики, заказчик) и важна общая «живая документация». Если же тесты пишут и читают только инженеры, прослойка Gherkin добавляет лишний слой поддержки без выгоды — проще обычные тесты с Page Object.

## Q14. Какие анти-паттерны Selenium стоит избегать?

Все типичные анти-паттерны бьют по двум вещам — **стабильности** и **стоимости поддержки**. Ниже самые частые.

1. **`Thread.sleep()` вместо `WebDriverWait`** — фиксированная задержка делает тест и медленным, и нестабильным (см. Q4).

2. **Хрупкие локаторы — XPath по позиции в дереве.** Такой путь ломается от любого нового `<div>` в вёрстке; опирайтесь на стабильный атрибут:

```java
// ПЛОХО
By.xpath("/html/body/div[2]/div[3]/table/tr[2]/td[5]")

// ХОРОШО
By.cssSelector("[data-testid='user-email']")
```

3. **Общее состояние (shared state) между тестами** — когда один тест меняет данные, на которые опирается следующий. Тесты становятся зависимыми от порядка запуска и ломаются при параллелизации. Каждый тест должен сам готовить и убирать свои данные.

4. **UI-тесты как замена unit-тестам** — покрывать ими то, что дешевле проверить на нижних уровнях пирамиды. UI-тесты медленные, flaky и дорогие в поддержке.

5. **Проверка бизнес-логики через UI.** Гонять весь UI-флоу ради проверки числа на экране — медленно и хрупко. Ту же логику быстрее и надёжнее проверить прямым запросом к API:

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

6. **Отсутствие cleanup** — забытый `driver.quit()` оставляет процессы браузера и драйвера висеть. На CI это быстро съедает память и роняет агента. Вызывайте `quit()` в `@AfterEach`/`@After`, даже если тест упал.

## Q15. Когда использовать UI-тесты, а когда — нет?

Главный критерий: UI-тесты дорогие и медленные, поэтому ими покрывают только то, что **нельзя проверить дешевле на нижних уровнях** пирамиды. Берите их, когда важна именно сборка целого пути пользователя через настоящий браузер.

**Использовать UI-тесты для**:
- **Критичных пользовательских сценариев** — вход, оформление заказа, регистрация. Сломаются они — теряется бизнес.
- **Интеграции UI и backend** — что данные от API действительно корректно дошли до экрана.
- **Визуальных и responsive-проверок** — элементы на месте, вёрстка не разъезжается.
- **Smoke-тестов** — приложение стартует и основные фичи в принципе работают.

**НЕ использовать UI-тесты для**:
- **Бизнес-логики** — её дешевле и надёжнее покрыть unit/integration-тестами на backend.
- **Множества граничных случаев** — каждый такой кейс через UI перегружает время прогона; их место в unit-тестах.
- **Проверки API** — прямые API-тесты быстрее, стабильнее и не зависят от вёрстки.

**Test pyramid**:
```text
        /\     UI tests (10-15%)
       /  \    Integration tests (20-30%)
      /    \   Unit tests (60-70%)
     /______\
```

Форма пирамиды отражает правило: основание — много быстрых дешёвых unit-тестов, узкая вершина — мало UI-тестов. Перевёрнутая пирамида (много UI, мало unit) — классический анти-паттерн: прогон долгий, билд нестабильный.

**Эмпирическое правило.** UI-тесты дорогие (медленные, склонны к flaky), поэтому их набор должен быть **минимально достаточным** — только то, что реально защищает ключевые сценарии.

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
- [Cucumber и BDD](cucumber-bdd-interview.md) — Gherkin-сценарии, step definitions которых под капотом дёргают `Selenium WebDriver` (см. Q13)
