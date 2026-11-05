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
updated: "2026-11-05"
---
# Вопросы на собеседовании: `Selenium WebDriver`

`Selenium WebDriver` — стандартный инструмент для E2E тестирования веб-интерфейсов. Предоставляет API для управления браузером (Chrome, Firefox, Safari). Важная тема для Java-разработчиков, занимающихся автоматизацией UI или поддерживающих legacy тесты.

Дата последнего обновления: 2026-11-05

## Полезные ссылки

### Официальная документация

- [Selenium Docs](https://www.selenium.dev/documentation/) — официальная документация
- [Baeldung: Selenium](https://www.baeldung.com/java-selenium-with-junit-and-testng) — практическое введение

## Содержание

- [Q1. Что такое Selenium WebDriver и как устроена его архитектура?](#q1)
- [Q2. Какие локаторы есть в Selenium и когда какой использовать?](#q2)
- [Q3. В чём разница между implicit, explicit и fluent wait?](#q3)
- [Q4. Почему `Thread.sleep()` — плохая практика в UI-тестах?](#q4)
- [Q5. Что такое Page Object Pattern?](#q5)
- [Q6. Что такое Page Factory и как работают @FindBy?](#q6)
- [Q7. Как работать с alerts, frames и окнами (windows)?](#q7)
- [Q8. Что такое Selenium Grid и когда его использовать?](#q8)
- [Q9. Как запускать UI-тесты параллельно?](#q9)
- [Q10. Как бороться с нестабильными (flaky) тестами?](#q10)
- [Q11. Какие альтернативы Selenium существуют и чем они отличаются?](#q11)
- [Q12. Как организовать Selenium-тесты в CI/CD?](#q12)
- [Q13. Что такое BDD-тесты с Cucumber?](#q13)
- [Q14. Какие анти-паттерны Selenium стоит избегать?](#q14)
- [Q15. Когда использовать UI-тесты, а когда — нет?](#q15)
- [See also](#see-also)

<a id="q1"></a>
## Q1. Что такое Selenium WebDriver и как устроена его архитектура?

Selenium WebDriver предоставляет программный API управления браузером: навигация, поиск элементов, ввод, действия и чтение состояния. Сам Selenium-клиент не является HTML-движком и не сводится к JavaScript внутри страницы или записи движений мыши.

Для **WebDriver Classic** локальную схему можно представить так:

```text
Код теста → Selenium API → HTTP-команды WebDriver → browser driver → браузер
```

При удалённом запуске RemoteWebDriver обращается к endpoint Grid/провайдера, который направляет команды нужной сессии. Selenium также поддерживает **WebDriver BiDi** с WebSocket и событиями от браузера: схема HTTP request/response не описывает все каналы Selenium.

Фрагмент для окружения с совместимыми Chrome и driver:

```java
WebDriver driver = new ChromeDriver();
try {
    driver.get("https://example.com");
    System.out.println(driver.getTitle());
} finally {
    driver.quit();
}
```

Browser driver служит мостом к конкретному браузеру; требуются **совместимые версии**, а не универсальное буквальное совпадение всех номеров версий. Selenium Manager помогает с управлением driver (Q12). Grid предоставляет инфраструктуру удалённых сессий (Q8), Selenium IDE — отдельный инструмент record/playback. Alert API обрабатывает JavaScript-диалоги, а не произвольные нативные окна ОС.

<a id="q2"></a>
## Q2. Какие локаторы есть в Selenium и когда какой использовать?

Локатор описывает, какой элемент нужен тесту. Основной критерий — устойчивый и однозначный контракт с приложением, а не безусловная лестница скорости.

Фрагменты для уже созданного driver:

```java
driver.findElement(By.id("username"));
driver.findElement(By.name("email"));
driver.findElement(By.className("primary-button"));
driver.findElement(By.tagName("button"));
driver.findElement(By.linkText("Sign in"));
driver.findElement(By.partialLinkText("Sign"));
driver.findElement(By.cssSelector("[data-testid='submit-button']"));
driver.findElement(By.xpath("//button[normalize-space()='Submit']"));
```

Стабильный уникальный id обычно удобен; если его нет, используйте компактный CSS или XPath с нужной выразительностью. XPath допустим не только тогда, когда CSS «запрещён»: важны читаемость, область поиска и поддерживаемые признаки. Рекомендации документации предпочитать ID/CSS не являются замером любого селектора на любой странице.

LinkText ищет полный видимый текст ссылки, partialLinkText — его часть; это не поиск по произвольному атрибуту любого DOM-узла. ClassName ожидает один класс, а комбинацию классов можно выразить CSS.

Переводимый текст меняется при локализации, позиционный XPath зависит от структуры. Data-testid полезен, только если команда поддерживает его как стабильный контракт. Если цель теста — проверить локализованный текст, такая проверка текста, напротив, может быть намеренной.

<a id="q3"></a>
## Q3. В чём разница между implicit, explicit и fluent wait?

Implicit wait — таймаут **поиска элементов в сессии**, а explicit wait — цикл ожидания выбранного условия. Implicit влияет на findElement/findElements и может быть перенастроен; это не одноразовая настройка только одного элемента.

Explicit-условие может проверять видимость, текст, URL, число окон или другое нужное состояние. В Java **WebDriverWait уже наследует FluentWait<WebDriver>**, поэтому polling и игнорируемые исключения настраиваются у него, а не требуют перехода к иному виду implicit wait.

Фрагмент с явным ожиданием и отключённым implicit:

```java
driver.manage().timeouts().implicitlyWait(Duration.ZERO);
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.pollingEvery(Duration.ofMillis(200));
WebElement result = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("result")));
```

Более общий FluentWait позволяет задать свою политику:

```java
Wait<WebDriver> fluent = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(10))
    .pollingEvery(Duration.ofMillis(200))
    .ignoring(org.openqa.selenium.NoSuchElementException.class);
Boolean ready = fluent.until(d ->
    "Ready".equals(d.findElement(By.id("result-status")).getText()));
```

Не смешивайте implicit и explicit без понимания вложенных вызовов: поиск внутри условия сам может ждать implicit timeout. Итоговое время не вычисляется универсально как max или сумма двух значений; длительность команд также влияет на фактическое ожидание.

В Selenium Java **4.25.0** elementToBeClickable проверяет visible и enabled. Это не гарантия отсутствия overlay, завершения backend-операции или неизменности DOM после проверки. Выбирайте условие по следующему действию и контракту приложения.

<a id="q4"></a>
## Q4. Почему `Thread.sleep()` — плохая практика в UI-тестах?

Thread.sleep ждёт время, а не готовность страницы. Фиксированная пауза может оказаться лишней при быстром ответе и недостаточной при медленном. Она не проверяет ни появление данных, ни завершение операции.

Для учебного приложения, где `result-status` показывает `Ready` после загрузки:

```java
driver.findElement(By.id("load")).click();
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.textToBe(By.id("result-status"), "Ready"));
```

Wait вычисляет условие и повторяет его с polling-интервалом, заканчивая при успехе. Polling interval не равен полному timeout. Простой presence проверяет наличие DOM-узла, visibility — видимость; ни одно из этих условий автоматически не означает готовность бизнес-результата. Между успешной проверкой и действием остаётся возможность изменения страницы.

Sleep может завершиться InterruptedException; spurious wakeup из контракта Object.wait не следует переносить на него. Точность планировщика не абсолютна. Универсального правила «sleep около 60 секунд удаляет WebDriver session» нет: Grid/провайдер могут иметь отдельную idle-политику.

Намеренно моделируемая задержка допустима по цели теста, но не заменяет синхронизацию с состоянием UI. Таймаут ожидания ограничивает политику polling; он не отменяет длительность вложенной браузерной команды.

<a id="q5"></a>
## Q5. Что такое Page Object Pattern?

Page Object инкапсулирует локаторы и операции страницы или её части. Повторяемые блоки можно выделять в **Page Component**; обязательного соответствия «одна целая страница — ровно один класс» нет. Тест выражает сценарий через операции, а детали DOM остаются локальными для объекта.

Пример классов для учебной формы с username/password, submit и welcome. Driver создаёт и закрывает вызывающий тест; показаны сами объекты страниц:

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

class LoginPage {
    private final WebDriver driver;
    LoginPage(WebDriver driver) {
        this.driver = driver;
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.visibilityOfElementLocated(By.id("username")));
    }
    DashboardPage login(String username, String password) {
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        return new DashboardPage(driver);
    }
}

class DashboardPage {
    private final WebDriver driver;
    DashboardPage(WebDriver driver) {
        this.driver = driver;
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
            ExpectedConditions.visibilityOfElementLocated(By.id("welcome")));
    }
    String getWelcomeMessage() {
        return driver.findElement(By.id("welcome")).getText();
    }
}
```

В тесте результат проверяют отдельно: вызвать login, затем сравнить dashboard.getWelcomeMessage с ожидаемым сообщением. Бизнес-assertions обычно принадлежат тесту; проверка загрузки/готовности нужной страницы при создании Page Object допустима. Возвращаемый объект следующей страницы помогает выразить переход, но не отменяет проверку результата сценария.

Паттерн уменьшает дублирование знаний о DOM и локализует изменения. Он не гарантирует, что любой рефакторинг интерфейса оставит сценарий неизменным, и не требует PageFactory — обычные By/поиск тоже подходят.

<a id="q6"></a>
## Q6. Что такое Page Factory и как работают @FindBy?

PageFactory в стандартной реализации Selenium инициализирует поля Page Object ленивыми proxies. Одна аннотация `@FindBy` сама Java-поле не заполняет: нужна обработка, например PageFactory.initElements. При вызове метода proxy выполняется поиск элемента; без `@CacheLookup` сохранённая ссылка не используется как постоянный cache.

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

class UserPage {
    @FindBy(id = "user-name") WebElement userName;
    @FindBys({@FindBy(className = "list-item"), @FindBy(tagName = "a")})
    List<WebElement> listLinks;
    @FindAll({@FindBy(id = "primary-button"), @FindBy(id = "secondary-button")})
    WebElement anyButton;

    UserPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}
```

В **Selenium Java 4.25.0**:

- `@FindBys` / `ByChained` последовательно ищет следующий уровень внутри результатов предыдущего: в примере ссылки внутри list-item. Это не пересечение признаков одного узла.
- `@FindAll` / `ByAll` задаёт альтернативные локаторы. Метод `findElement` берёт первый доступный результат по порядку локаторов; `findElements` собирает результаты всех локаторов, без гарантии DOM-порядка или удаления повторов.
- `@CacheLookup` меняет cache-поведение. Повторный поиск без него не делает lookup/action атомарными: элемент может устареть между ними, поэтому stale всё ещё возможен.

PageFactory — способ организации полей, а не механизм ожидания любого нужного состояния. Для динамического UI могут быть понятнее явные By и WebDriverWait. Пользовательские decorators/locators способны менять стандартное поведение, поэтому эти свойства не следует приписывать произвольной интеграции по одному имени аннотации.

<a id="q7"></a>
## Q7. Как работать с alerts, frames и окнами (windows)?

Различайте JavaScript-диалоги, вложенные документы и окна. HTML modal остаётся обычной частью DOM. JS alert/confirm/prompt обслуживает Alert API, а не CSS-поиск его кнопок; это не управление произвольным диалогом ОС.

Фрагмент для сценария с JS prompt:

```java
driver.findElement(By.id("trigger-prompt")).click();
Alert prompt = new WebDriverWait(driver, Duration.ofSeconds(10))
    .until(ExpectedConditions.alertIsPresent());
prompt.sendKeys("answer");
prompt.accept(); // либо dismiss по сценарию
```

Сам элемент iframe находится в DOM родителя, но его документ — другой browsing context. Вход возможен по zero-based index, name/id или WebElement. FindElement сам обратно не переключается: parentFrame поднимает на один уровень, defaultContent возвращает к top-level.

```java
driver.switchTo().frame(driver.findElement(By.cssSelector("iframe.payment")));
// команды внутри документа frame
driver.switchTo().defaultContent();
```

Окна/вкладки имеют opaque handle. Java window(nameOrHandle) принимает handle или window name, а не title как контракт идентификации. Порядок Set handles не задаёт порядок открытия. Пример для сценария, ожидающего **ровно одно новое окно**:

```java
String original = driver.getWindowHandle();
Set<String> before = driver.getWindowHandles();
driver.findElement(By.id("open-new")).click();
String opened = new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
    Set<String> added = new HashSet<>(d.getWindowHandles());
    added.removeAll(before);
    return added.size() == 1 ? added.iterator().next() : null;
});
try {
    driver.switchTo().window(opened);
    // действия и assertions в новом окне
    driver.close();
} finally {
    driver.switchTo().window(original);
}
```

Если ожидается несколько окон, выберите нужное по явному критерию вместо произвольного первого handle. Close закрывает текущее окно; quit завершает сессию. После close нужно явно выбрать оставшееся окно, а исходное окно должно оставаться доступным по контракту сценария.

<a id="q8"></a>
## Q8. Что такое Selenium Grid и когда его использовать?

Selenium Grid обслуживает удалённые браузерные сессии на доступных Node. Тест использует RemoteWebDriver; параллельное исполнение Java-методов задаёт runner. Несколько потоков теста без свободных совместимых browser slots не дают автоматического линейного ускорения.

В Grid 4 новую сессию Router передаёт в New Session Queue; Distributor подбирает подходящий слот Node и сохраняет соответствие session/Node в Session Map. Для команд **существующей** сессии Router получает её Node из Session Map и направляет команду туда. Distributor не выбирает новый Node для каждого click.

```java
WebDriver driver = new RemoteWebDriver(
    new URL("http://localhost:4444"), new ChromeOptions());
try {
    driver.get("https://example.com");
} finally {
    driver.quit();
}
```

Grid 4 допускает Standalone, Hub/Node и полностью distributed размещение компонентов. Standalone объединяет компоненты в одном процессе; Hub/Node — не единственная обязательная топология. Для уже имеющегося server JAR пример standalone-команды:

```bash
java -jar selenium-server.jar standalone
```

Grid полезен для нескольких браузеров/окружений и независимых конкурентных сессий. Доступность браузера определяется Node и capabilities: например, Safari требует подходящего macOS-узла. Пропускная способность зависит от слотов, очереди, длительности тестов, сети и ресурсов приложения.

Выбор между своим Grid, контейнерной инфраструктурой и облачным провайдером зависит от контроля окружения, затрат и требуемой матрицы. Наличие Grid не заменяет отдельные сессии/данные, ожидания состояния страницы и cleanup в тестах.

<a id="q9"></a>
## Q9. Как запускать UI-тесты параллельно?

Параллельные тесты должны владеть отдельными браузерными сессиями. Один WebDriver не следует одновременно использовать из нескольких потоков. Кроме driver, изолируйте пользователей, данные, файлы и другие общие ресурсы; Grid распределяет сессии, а параллельность тестовых методов задаёт runner.

В Jupiter параллельное исполнение включают configuration parameters. Например:

```properties
# src/test/resources/junit-platform.properties — один из способов передачи настроек
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.config.strategy=fixed
junit.jupiter.execution.parallel.config.fixed.parallelism=4
```

Параметры можно передать и через build/JVM/Launcher. При обычном PER_METHOD новый экземпляр тестового класса позволяет хранить driver в поле: создать в BeforeEach, гарантировать cleanup в AfterEach. При PER_CLASS и concurrent-методах одно поле остаётся общей изменяемой ссылкой; одного BeforeEach недостаточно для изоляции.

Если нужен ThreadLocal, очищайте его **в том же потоке**, даже когда quit бросает:

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        WebDriver current = driver.get();
        if (current == null) {
            current = new ChromeDriver();
            driver.set(current);
        }
        return current;
    }

    public static void quitDriver() {
        WebDriver current = driver.get();
        try {
            if (current != null) current.quit();
        } finally {
            driver.remove();
        }
    }
}
```

Вызывайте quitDriver в AfterEach/finally, включая путь с ошибкой теста. Если создание driver бросило до driver.set, ThreadLocal не получает незавершённое значение. Remove удаляет локальную ссылку, но **не доказывает**, что удалённая сессия или процесс успешно освобождены после ошибки quit; такой сбой нужно диагностировать отдельно.

TestNG также поддерживает параллелизм, например через suite XML:

```xml
<suite name="UI Tests" parallel="methods" thread-count="4">
    <test name="Tests">
        <classes><class name="com.example.LoginTest"/></classes>
    </test>
</suite>
```

<a id="q10"></a>
## Q10. Как бороться с нестабильными (flaky) тестами?

Flaky-тест меняет исход без ожидаемого изменения проверяемого поведения. Сначала выясните причину: гонка отрисовки, неверный локатор, конфликт данных, недоступность сервиса или ресурсная проблема требуют разных исправлений. Большой implicit timeout и фиксированный sleep не решают весь этот набор причин.

- Ожидайте наблюдаемое состояние приложения через WebDriverWait. ElementToBeClickable проверяет visibility/enabled, но само по себе не гарантирует завершения бизнес-операции или отсутствия перекрывающего элемента.
- Выбирайте устойчивый уникальный локатор. Data-testid полезен, если команда поддерживает его как контракт; сам атрибут не гарантирует стабильности.
- Сохраняйте достаточную диагностику исходного сбоя: screenshot, сообщения driver/браузера, данные и время событий. Изолируйте тестовые данные и сессии.
- Ограниченный retry может временно уменьшить ущерб, но должен сохранять видимость первого сбоя. Это отдельная библиотека/интеграция, а не встроенное действие Jupiter @Tag.

Пример для учебного приложения: после открытия `/results` элемент `result-status` должен показать `Ready`. DriverFactory взят из Q9; UI и его локаторы должны существовать в вашем стенде.

```java
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultUiTest {
    @Test
    @Tag("flaky")
    void showsReadyResult() {
        WebDriver driver = DriverFactory.getDriver();
        driver.get(System.getProperty("ui.baseUrl", "http://localhost:8080") + "/results");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        assertTrue(wait.until(ExpectedConditions.textToBe(By.id("result-status"), "Ready")));
    }

    @AfterEach
    void tearDown() {
        DriverFactory.quitDriver();
    }
}
```

Здесь @Test регистрирует Jupiter-тест, @Tag только помечает его. Включение/исключение тега настраивают при запуске; один тег не отключает тест и не добавляет повторы. Disabled отключает тест и требует осмысленной причины. Timeout и условие выбирают по контракту операции, сохраняя bounded ожидание.

<a id="q11"></a>
## Q11. Какие альтернативы Selenium существуют и чем они отличаются?

Сравнивайте браузерную матрицу, язык, модель ожиданий и инфраструктуру. Универсальный вывод «новый инструмент всегда быстрее и устраняет flaky» без условий и замера неверен.

| Инструмент | Существенные отличия |
|------------|---------------------|
| Selenium | WebDriver Classic/BiDi, разные языки и браузеры; удалённые сессии через RemoteWebDriver/Grid |
| Playwright | Chromium, Firefox и WebKit; locator-действия включают actionability checks и auto-wait |
| Cypress | Chrome-семейство и Firefox; поддержка WebKit отмечена экспериментальной |
| Puppeteer | Поддержка Chrome и Firefox; из неё не следует поддержка Safari |

Матрица зависит от версии и окружения; сверяйте её с официальной документацией. Playwright WebKit не является запуском приложения Apple Safari: для Safari-specific риска это различие существенно.

У Playwright click без force обычно проверяет visibility, stable, receives-events и enabled, а не только наличие узла. Эти проверки не заменяют ожидание бизнес-результата после действия. Небольшой Java-фрагмент без внешнего учебного сайта:

```java
try (Playwright playwright = Playwright.create()) {
    Browser browser = playwright.chromium().launch();
    Page page = browser.newPage();
    page.setContent("<button>Submit</button>");
    page.locator("button").click();
    browser.close();
}
```

Выбор инструмента для проекта требует проверить нужные браузеры, ограничения окружения и поддержку команды. Устойчивые локаторы, изоляция и осмысленные assertions остаются нужны при любом auto-wait. Здесь не проводится performance-сравнение или рекомендация мигрировать существующий набор.

<a id="q12"></a>
## Q12. Как организовать Selenium-тесты в CI/CD?

Для CI нужны воспроизводимое окружение, совместимые browser/driver, изолированные тестовые сессии и диагностика. Headless удобен без видимого дисплея; это возможность браузера, а не запрет запускать CI с графическим окружением.

**Selenium Manager** встроен начиная с 4.6 и может подобрать driver без отдельной библиотеки WebDriverManager. Это fallback, а не обещание исправить любой явно закреплённый путь. В Java 4.25.0 заданные service executable/system property могут обойти Manager. Учитывайте доступность браузера, cache/сеть, proxy и права процесса; требуется совместимость версий, а не универсальное полное совпадение их строк.

Фрагмент создания Chrome для подходящего CI-образа:

```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless=new", "--window-size=1920,1080");
WebDriver driver = new ChromeDriver(options);
try {
    // действия и assertions
} finally {
    driver.quit();
}
```

Не добавляйте no-sandbox и другие flags автоматически: настройки зависят от образа, модели прав и ресурсов. Проверьте запуск в целевом окружении. Grid также требует доступных browser slots и совместимых capabilities.

Пример GitHub Actions **для Maven-проекта с wrapper**, а не workflow этого репозитория. Браузер должен быть доступен в образе. Версии actions здесь конкретный пример, не заявление о последних релизах:

```yaml
name: UI Tests
on: [push, pull_request]
jobs:
  selenium-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
      - name: Run UI tests
        run: ./mvnw test -Dtest="*UITest"
      - name: Upload screenshots
        if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: screenshots
          path: screenshots/
```

Upload не создаёт screenshots: их должен сохранить обработчик падения, например через TakesScreenshot, до cleanup driver. У upload-artifact v4 отсутствие файлов по умолчанию даёт warning, а не доказательство наличия диагностики. Сохраняйте также логи, проверяйте тестовые фильтры и фактически обнаруженные тесты; общий зелёный job не подтверждает, что нужный UI-набор был исполнен.

<a id="q13"></a>
## Q13. Что такое BDD-тесты с Cucumber?

BDD — совместная проработка поведения системы на примерах. Cucumber исполняет сценарии Gherkin через step definitions; в UI-сценарии glue может использовать WebDriver. Page Object полезен, но необязателен; Cucumber применим и к API без браузера.

Given/When/Then задают роль и структуру шагов. При поиске step definition сопоставляется **текст после keyword**, поэтому смена Given на Then не создаёт отдельное пространство имён для одинакового текста. `{string}` извлекает строковый параметр выражения.

```gherkin
Feature: User Login

  Scenario: Successful login with valid credentials
    Given I am on the login page
    When I log in as "alice" with password "secret123"
    Then I should see welcome message "Welcome, Alice"
```

Полный glue-класс с явным lifecycle driver. Пример предполагает учебное приложение с полями username/password, submit-кнопкой и элементом welcome. Использованы **Cucumber** Before/After, не одноимённые lifecycle-аннотации другого runner:

```java
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {
    private WebDriver driver;

    @Before
    public void startBrowser() {
        driver = new ChromeDriver();
    }

    @Given("I am on the login page")
    public void onLoginPage() {
        driver.get(System.getProperty("ui.baseUrl", "http://localhost:8080") + "/login");
    }

    @When("I log in as {string} with password {string}")
    public void logIn(String username, String password) {
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    @Then("I should see welcome message {string}")
    public void shouldSeeMessage(String message) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        assertTrue(wait.until(ExpectedConditions.textToBe(By.id("welcome"), message)));
    }

    @After
    public void stopBrowser() {
        WebDriver current = driver;
        driver = null;
        if (current != null) current.quit();
    }
}
```

В обычном JVM glue lifecycle Cucumber создаёт новый экземпляр glue-класса для каждого scenario. При собственном DI-контейнере обеспечьте соответствующий scenario scope; не храните driver в общем static-поле. After должен попытаться закрыть сессию и при сбое сценария; null-проверка учитывает неуспешное создание driver. Если quit не удался, очистка Java-поля не доказывает освобождения удалённой сессии.

Gherkin полезен, когда примеры служат общей проверяемой документацией. Сам по себе текст сценария не создаёт driver, не реализует шаги и не гарантирует ценности BDD-процесса.

<a id="q14"></a>
## Q14. Какие анти-паттерны Selenium стоит избегать?

Анти-паттерн определяется тем, что мешает проверяемому риску или делает обратную связь ненадёжной, а не самим наличием UI-сценария.

1. **Фиксированный sleep вместо состояния.** Пауза не проверяет готовность. Намеренная задержка по цели теста — отдельный случай; для синхронизации выбирайте нужное условие и ограниченное ожидание.
2. **Хрупкий локатор.** Позиционный XPath может выбрать другой элемент после перестановки узлов. Это не означает, что любой новый div ломает любой XPath. Поддерживаемый уникальный атрибут обычно даёт более ясный контракт.
3. **Скрытая зависимость между тестами.** Один сценарий не должен незаметно готовить изменяемые данные для следующего. Неизменяемые общие fixtures допустимы, если тесты друг другу не мешают.
4. **Общая конкурентная browser session.** Изолируйте driver, пользователей и данные; ThreadLocal сам по себе не задаёт lifecycle теста.
5. **Забытый cleanup.** Закрывайте сессию при успешном и неуспешном тесте, учитывая ошибку создания и исключение quit. Завершение клиентской JVM не гарантирует освобождения удалённого Grid slot.
6. **Лишнее дублирование подробных правил через UI.** Комбинации расчёта скидок обычно дешевле проверять unit/API-тестами. Но сквозное оформление заказа может требовать UI, поскольку проверяет интеграцию интерфейса с сервисами.

Не заменяйте выбор уровня универсальным запретом «бизнес-логику никогда не проверяют через UI». Оставьте репрезентативные пользовательские пути там, где важен браузер, а подробные варианты правил проверяйте ниже. Без замера нельзя обещать конкретное число секунд выигрыша от такой замены.

<a id="q15"></a>
## Q15. Когда использовать UI-тесты, а когда — нет?

Выбирайте уровень теста по риску и необходимой уверенности. UI-тест полезен, когда нужен настоящий браузерный путь и связь интерфейса с системой; множество независимых комбинаций правил обычно даёт более быструю диагностику на unit/API/integration уровне.

| Проверяемый риск | Разумный выбор |
|-----------------|----------------|
| Критичный login/checkout через реальный интерфейс | Несколько репрезентативных UI-сценариев |
| Комбинации расчёта скидки, валидации и edge cases | Unit/API/integration по границе правила |
| Отображение ответа backend и связность UI-пути | UI плюс более узкие проверки контракта/API |
| Визуальная регрессия и responsive поведение | Явные критерии, размеры viewport, baseline и инструмент сравнения/проверок |

Один screenshot ещё не является visual assertion: нужен проверяемый критерий отличий. Selenium управляет браузером, но сам факт запуска не доказывает качество вёрстки. Аналогично API-проверка не подтверждает работоспособность пользовательского пути через экран.

```text
        /\     UI: важные браузерные пути
       /  \    Интеграционные проверки границ
      /    \   Быстрые проверки правил и компонентов
     /______\
```

Пирамида — ориентир стоимости обратной связи, а не фиксированные проценты или обязательная форма для любого продукта. Состав набора зависит от архитектуры, рисков, длительности прогона и стоимости поддержки. Подробные правила обычно проверяют ниже, сохраняя нужное покрытие интеграции и критичных бизнес-путей через UI.

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
