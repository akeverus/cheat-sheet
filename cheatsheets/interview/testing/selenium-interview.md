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


> [!mcq]
>
> **Вопрос:** Что представляет собой Selenium WebDriver с точки зрения архитектуры взаимодействия с браузером?
>
> ---
>
> #### B) Selenium WebDriver — это плагин для браузера, который встраивается в Chrome/Firefox через расширение и напрямую манипулирует DOM из JavaScript-кода — ❌ Неверно
>
> **Что на самом деле:** WebDriver работает не как расширение браузера, а как внешний клиент. Java-код вызывает WebDriver API, который отправляет HTTP-запросы по W3C WebDriver Protocol к отдельному процессу `chromedriver`/`geckodriver`. Этот процесс уже общается с браузером через нативные debugger-протоколы (CDP для Chrome).
>
> **Откуда путаница:** Selenium IDE действительно является расширением браузера для record-and-playback — это часто смешивают с WebDriver.
>
> **Если бы это было правдой:** Тесты ломались бы при каждом обновлении браузера и не могли бы работать в headless/CI-окружении без UI.
>
> ---
>
> #### A) Selenium WebDriver — это библиотека на Java/Python/JS, которая управляет браузером через отдельный driver-процесс по W3C WebDriver Protocol поверх HTTP — ✓ Верно
>
> **Развёрнутое объяснение:** WebDriver — клиент-серверная архитектура. Тестовый код (клиент) на Java/Python/JS вызывает методы API (`findElement`, `click`), которые сериализуются в JSON и отправляются HTTP-запросами к browser driver (`chromedriver`, `geckodriver`, `msedgedriver`). Driver транслирует команды в нативный протокол браузера и возвращает результат. С Selenium 4 протокол стандартизирован W3C (раньше был JSON Wire Protocol).
>
> **Пример:**
> ```java
> // Под капотом каждый вызов = HTTP-запрос к chromedriver
> WebDriver driver = new ChromeDriver(); // POST /session
> driver.get("https://example.com");      // POST /session/<id>/url
> WebElement el = driver.findElement(By.id("submit")); // POST /session/<id>/element
> el.click();                              // POST /session/<id>/element/<el-id>/click
> driver.quit();                           // DELETE /session/<id>
> ```
>
> **Когда применять:** E2E-тестирование, cross-browser автоматизация, smoke-тесты в CI, scraping legacy-сайтов без API.
>
> **Подводные камни:** Каждая операция = network roundtrip → тесты медленнее unit-тестов на 2-3 порядка. Несовместимость версий driver и browser ломает session.
>
> **Связанные вопросы:** [[Q8]] — Selenium Grid, [[Q11]] — альтернативы (Playwright использует CDP напрямую).
>
> ---
>
> #### C) Selenium WebDriver — это JavaScript-фреймворк, который инжектится в страницу и выполняет тесты внутри браузера в том же event-loop, что и приложение — ❌ Неверно
>
> **Что на самом деле:** Это описание Cypress, а не Selenium. Cypress действительно выполняется внутри браузера и имеет прямой доступ к window/document, что делает его быстрее, но ограничивает single-tab/same-origin. Selenium же управляет браузером извне через driver-процесс.
>
> **Откуда путаница:** Cypress часто противопоставляют Selenium, и архитектуры путают.
>
> **Если бы это было правдой:** Не было бы поддержки multi-tab сценариев и cross-domain навигации, которые в Selenium работают без проблем.
>
> ---
>
> #### D) Selenium WebDriver — это headless-браузер на базе WebKit, встроенный в JVM, который рендерит страницы без графического вывода — ❌ Неверно
>
> **Что на самом деле:** Selenium сам не рендерит ничего — он управляет настоящими браузерами (Chrome, Firefox, Safari, Edge). Headless-режим — это опция самих браузеров (`--headless=new` в Chrome), а не функция Selenium. Описанное похоже на HtmlUnit или старый PhantomJS.
>
> **Откуда путаница:** В CI часто используют Selenium + headless Chrome, что создаёт впечатление, будто это одно целое.
>
> **Если бы это было правдой:** Не нужны были бы chromedriver/geckodriver и cross-browser тесты были бы бессмысленны.

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


> [!mcq]
>
> **Вопрос:** Какой приоритет локаторов в Selenium считается best practice для стабильных и быстрых тестов?
>
> ---
>
> #### A) XPath по абсолютному пути (`/html/body/div[2]/...`) — наиболее точный способ, потому что описывает уникальное место элемента в дереве — ❌ Неверно
>
> **Что на самом деле:** Абсолютный XPath — антипаттерн. Любое изменение разметки (вставка `<div>` обёртки, перенос блока) ломает весь набор тестов. Кроме того, XPath-движок браузера медленнее CSS-селектора (особенно в Chrome).
>
> **Откуда путаница:** IDE и DevTools часто предлагают «Copy XPath», что выдаёт именно абсолютный путь — новички берут это как руководство.
>
> **Если бы это было правдой:** Каждый редизайн фронтенда требовал бы переписывания всех UI-тестов даже без смены функциональности.
>
> ---
>
> #### B) Только `linkText` и `partialLinkText`, потому что они описывают намерение пользователя — ❌ Неверно
>
> **Что на самом деле:** Текстовые локаторы работают только для `<a>` тегов и ломаются при локализации (en/ru/de версии сайта). Это хороший выбор для read-only тестов на одном языке, но не основной приоритет.
>
> **Откуда путаница:** BDD-стиль («I click on link 'Sign up'») создаёт ощущение, что текст — естественный локатор.
>
> **Если бы это было правдой:** Сайты с i18n требовали бы дублировать тесты под каждый язык.
>
> ---
>
> #### C) Сначала `id` или `data-testid`, потом `cssSelector` с атрибутами, и только в крайнем случае `xpath` с относительными путями — ✓ Верно
>
> **Развёрнутое объяснение:** Приоритет основан на стабильности и скорости. `id` — самый быстрый (нативный `getElementById`) и обычно уникальный. `data-testid` — атрибут, специально добавленный разработчиками для тестов, не зависит от стилей/копирайта. `cssSelector` быстрее XPath и читаемее. XPath нужен только когда требуются возможности, недоступные в CSS: поиск по тексту (`contains(text(), 'X')`), навигация вверх по дереву (`ancestor::`), индексы (`[position()=2]`).
>
> **Пример:**
> ```java
> // 1. ID — идеально
> driver.findElement(By.id("submit-btn"));
>
> // 2. data-testid — рекомендуется в SPA (React/Vue)
> driver.findElement(By.cssSelector("[data-testid='checkout-button']"));
>
> // 3. CSS с атрибутами — следующий приоритет
> driver.findElement(By.cssSelector("button[type='submit'].primary"));
>
> // 4. XPath — только для специфичных кейсов
> driver.findElement(By.xpath("//tr[td[contains(., 'alice@example.com')]]/td[3]/button"));
> ```
>
> **Когда применять:** Команда контролирует фронтенд и может добавлять `data-testid`. Для legacy без id — относительные CSS-селекторы по структуре формы.
>
> **Подводные камни:** CSS-классы — плохой выбор (часто меняются дизайнерами/Tailwind-генератором). Локаторы по `nth-child` хрупкие при добавлении элементов.
>
> **Связанные вопросы:** [[Q5]] — Page Object, [[Q10]] — flaky тесты, [[Q14]] — anti-patterns.
>
> ---
>
> #### D) `tagName` — самый универсальный, потому что любой HTML-элемент имеет тег и его не переименуют — ❌ Неверно
>
> **Что на самом деле:** `tagName` почти всегда возвращает множество элементов (на странице сотни `<div>` и `<button>`). Используется только в комбинации с другими фильтрами или внутри `findElements(...)` для итерации. Сам по себе как primary locator бесполезен.
>
> **Откуда путаница:** Логично рассуждать, что теги стабильнее классов, но проблема не в стабильности, а в неуникальности.
>
> **Если бы это было правдой:** Пришлось бы каждый раз итерироваться по сотням элементов и фильтровать в Java-коде.

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


> [!mcq]
>
> **Вопрос:** Чем принципиально отличаются implicit wait и explicit wait в Selenium WebDriver?
>
> ---
>
> #### A) Implicit wait блокирует поток до завершения всех HTTP-запросов страницы, explicit wait блокирует только текущий метод — ❌ Неверно
>
> **Что на самом деле:** Ни тот, ни другой не следят за HTTP-запросами. Implicit wait — это таймаут для повторных попыток `findElement`, если элемент не найден сразу. Explicit wait — polling-цикл, проверяющий заданное условие. Оба не имеют отношения к network activity.
>
> **Откуда путаница:** Логично хотеть «дождаться загрузки страницы», и `pageLoadTimeout` действительно следит за `document.readyState`, но это третья настройка, не implicit/explicit wait.
>
> **Если бы это было правдой:** Не было бы проблемы с динамическими элементами после AJAX — implicit wait «волшебно» решал бы всё.
>
> ---
>
> #### C) Implicit wait — это глобальный таймаут для всех `findElement` (применяется ко всем поискам), explicit wait — это `WebDriverWait.until(condition)` для конкретного условия в конкретном месте — ✓ Верно
>
> **Развёрнутое объяснение:** Implicit wait настраивается один раз на сессию через `driver.manage().timeouts().implicitlyWait(...)` и заставляет WebDriver повторять любой `findElement`/`findElements` в течение указанного времени, пока элемент не появится. Explicit wait — это `WebDriverWait` с `ExpectedConditions` (или custom `Function`), который ждёт конкретного состояния: `elementToBeClickable`, `visibilityOf`, `textToBePresentInElement`, `invisibilityOfElementLocated`. Explicit мощнее: можно ждать не просто «появился в DOM», а «появился И стал кликабельным».
>
> **Пример:**
> ```java
> // Implicit (один раз на всю сессию)
> driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
> driver.findElement(By.id("delayed")); // подождёт до 5 сек, если не найден
>
> // Explicit (для конкретного условия)
> WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
> WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
> // ждёт пока элемент: в DOM + visible + enabled + not overlapped
> btn.click();
>
> // Кастомное условие через лямбду
> wait.until(d -> ((JavascriptExecutor) d)
>     .executeScript("return document.readyState").equals("complete"));
> ```
>
> **Когда применять:** Implicit — для базового запаса прочности на медленных страницах. Explicit — обязательно для динамических элементов (AJAX, SPA, модалок). Best practice — НЕ использовать implicit вообще, только explicit для каждого ожидания.
>
> **Подводные камни:** Смешивание implicit + explicit приводит к непредсказуемым суммарным таймаутам (баг описан в Selenium docs). После Selenium 4 implicit стал ещё более problematic с относительными локаторами.
>
> **Связанные вопросы:** [[Q4]] — Thread.sleep, [[Q10]] — flaky тесты.
>
> ---
>
> #### B) Implicit wait работает на стороне браузера через JS-инжекцию, explicit — на стороне Java-кода через polling — ❌ Неверно
>
> **Что на самом деле:** Оба механизма реализованы на стороне клиента (Java). Implicit wait — это retry-loop внутри chromedriver/W3C-сервера, а explicit wait — polling-loop внутри `WebDriverWait` в Java. Никакая JS-инжекция в страницу при этом не делается.
>
> **Откуда путаница:** В Selenium можно делать `executeScript` для проверки JS-условий — это смешивают с механизмом wait.
>
> **Если бы это было правдой:** Implicit wait не работал бы при CSP, запрещающем inline scripts.
>
> ---
>
> #### D) Explicit wait и implicit wait — синонимы; современный Selenium 4 называет их одинаково, а API только legacy — ❌ Неверно
>
> **Что на самом деле:** Это два разных механизма и в Selenium 3, и в Selenium 4. В Selenium 4 добавлены relative locators и обновлён API таймаутов (`Duration` вместо `long+TimeUnit`), но различие осталось. В документации Selenium прямо рекомендуют использовать только explicit и избегать implicit.
>
> **Откуда путаница:** Selenium действительно много раз менял API, что создаёт ощущение, будто всё переименовано.
>
> **Если бы это было правдой:** Не было бы знаменитой проблемы смешивания двух типов ожиданий с непредсказуемым результатом.

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


> [!mcq]
>
> **Вопрос:** Почему `Thread.sleep(5000)` в UI-тестах считается анти-паттерном по сравнению с `WebDriverWait`?
>
> ---
>
> #### A) `Thread.sleep` приостанавливает JVM полностью, в том числе chromedriver, что роняет соединение с браузером — ❌ Неверно
>
> **Что на самом деле:** `Thread.sleep` приостанавливает только текущий поток теста. JVM продолжает работать, chromedriver — это отдельный процесс ОС, а браузер работает параллельно. Никакого разрыва соединения не происходит — наоборот, sleep полностью безопасен с точки зрения процессов, проблема в его семантике.
>
> **Откуда путаница:** Слово «блокирует» звучит так, будто блокирует всё. Но WebDriver session — heartbeat-based, она не упадёт от паузы в 5 секунд.
>
> **Если бы это было правдой:** Sleep в любом UI-тесте всегда падал бы с `SessionNotFoundException`, что не соответствует реальности.
>
> ---
>
> #### D) `Thread.sleep` ждёт фиксированное время независимо от состояния страницы — медленно при готовности раньше и flaky при готовности позже; `WebDriverWait` ждёт ровно столько, сколько нужно условию — ✓ Верно
>
> **Развёрнутое объяснение:** Фиксированная пауза — это компромисс между двумя плохими крайностями: слишком короткая → flaky test на медленных CI-runner'ах или при сетевых задержках; слишком длинная → тесты выполняются часами на ровном месте. `WebDriverWait` использует polling (по умолчанию каждые 500 мс) и возвращается сразу как только условие выполнено. Это и быстрее, и стабильнее. Дополнительный плюс — explicit wait может проверять не только «элемент в DOM», но и «кликабельный», «видимый», «содержит текст».
>
> **Пример:**
> ```java
> // АНТИПАТТЕРН
> driver.findElement(By.id("submit")).click();
> Thread.sleep(5000); // молимся, что страница успеет загрузиться
> assertEquals("Success", driver.findElement(By.id("status")).getText());
>
> // BEST PRACTICE
> driver.findElement(By.id("submit")).click();
> WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
> WebElement status = wait.until(ExpectedConditions
>     .textToBe(By.id("status"), "Success"));
> // вернётся за 200 мс если статус появился; до 10 сек на медленной сети
> ```
>
> **Когда применять:** Всегда заменять Thread.sleep на WebDriverWait + конкретное `ExpectedCondition`. Исключение — debug/локальный run с явным комментарием.
>
> **Подводные камни:** Если условие невозможно выразить через `ExpectedConditions`, можно написать кастомный `Function<WebDriver, T>` — это всё равно лучше sleep. Иногда нужен короткий sleep после `click()` на анимированной модалке — обычно решается через `elementToBeClickable` или ожидание `aria-hidden=false`.
>
> **Связанные вопросы:** [[Q3]] — типы wait, [[Q10]] — flaky тесты.
>
> ---
>
> #### C) `Thread.sleep` не работает в JUnit 5 — нужен `@Timeout`, который автоматически вызывает `WebDriverWait` — ❌ Неверно
>
> **Что на самом деле:** `Thread.sleep` работает в любом тест-фреймворке (JUnit 4/5, TestNG, Spock). Это просто стандартный метод `java.lang.Thread`. `@Timeout` в JUnit 5 — это assertion, которая падает, если тест выполняется дольше указанного времени, а не замена sleep. С `WebDriverWait` он никак не связан.
>
> **Откуда путаница:** Названия похожи (timeout/wait), но семантика разная.
>
> **Если бы это было правдой:** Старые тесты с `Thread.sleep` не компилировались бы под JUnit 5.
>
> ---
>
> #### B) `Thread.sleep` бросает `InterruptedException`, который в Selenium-тестах нельзя обработать, поэтому код не компилируется — ❌ Неверно
>
> **Что на самом деле:** `InterruptedException` — checked exception, его нужно обработать через try-catch или `throws`, но это никак не «нельзя в Selenium». Тесты прекрасно компилируются и работают, просто это плохая практика по причинам семантики, а не компиляции.
>
> **Откуда путаница:** Раздражение от checked exception иногда воспринимается как «запрет на использование».
>
> **Если бы это было правдой:** Не было бы массового анти-паттерна — компилятор просто блокировал бы его.

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


> [!mcq]
>
> **Вопрос:** Какова главная цель Page Object Pattern в UI-тестировании?
>
> ---
>
> #### A) Ускорить выполнение тестов за счёт кэширования WebElement в полях класса — больше не нужно делать findElement при каждом обращении — ❌ Неверно
>
> **Что на самом деле:** Page Object — про maintainability, а не про производительность. Более того, в современном PageFactory `WebElement` поля — это lazy-прокси, и `findElement` всё равно вызывается при каждом обращении (для защиты от `StaleElementReferenceException`). Кэширование сделало бы тесты flaky.
>
> **Откуда путаница:** Кажется, что хранение элементов в полях = кэш. Но за этим скрывается прокси, а не реальный element.
>
> **Если бы это было правдой:** Любой переход между страницами ломал бы тесты, так как кэшированные элементы становятся stale.
>
> ---
>
> #### B) Инкапсулировать локаторы и взаимодействия со страницей в отдельные классы, чтобы при изменении UI правки делались в одном месте, а тесты читались как сценарии — ✓ Верно
>
> **Развёрнутое объяснение:** Page Object разделяет «что делает пользователь» (бизнес-сценарий) и «как это сделать в DOM» (CSS-селекторы, последовательность кликов). Если разработчики переименуют `id="login"` в `id="signin"`, нужно поменять одну строку в `LoginPage`, а не 50 тестов. Тест становится самодокументируемым: `loginPage.login(user, pass).goToDashboard().expectWelcome("Alice")` читается без знания HTML-структуры. Это применение Single Responsibility Principle к UI-тестам.
>
> **Пример:**
> ```java
> public class CheckoutPage {
>     private final WebDriver driver;
>
>     @FindBy(id = "shipping-address") private WebElement shippingAddress;
>     @FindBy(css = "[data-testid='card-number']") private WebElement cardNumber;
>     @FindBy(css = "button.place-order") private WebElement placeOrderBtn;
>
>     public CheckoutPage(WebDriver driver) {
>         this.driver = driver;
>         PageFactory.initElements(driver, this);
>     }
>
>     public OrderConfirmationPage placeOrder(Address addr, Card card) {
>         shippingAddress.sendKeys(addr.toString());
>         cardNumber.sendKeys(card.number());
>         placeOrderBtn.click();
>         return new OrderConfirmationPage(driver);
>     }
> }
>
> // Тест читается как business scenario:
> orderConfirmation = new ShopPage(driver)
>     .addToCart("laptop")
>     .goToCheckout()
>     .placeOrder(address, card);
> assertThat(orderConfirmation.getOrderId()).isNotNull();
> ```
>
> **Когда применять:** Любой проект с >5 UI-тестами. Особенно при частых редизайнах фронтенда или большой команде QA.
>
> **Подводные камни:** God Object — не делайте один класс на весь сайт. Один Page Object = одна логическая страница/компонент. Возврат следующей страницы из метода (fluent chain) делает связи явными.
>
> **Связанные вопросы:** [[Q6]] — Page Factory, [[Q14]] — anti-patterns.
>
> ---
>
> #### C) Создать единый базовый класс `BasePage`, от которого наследуются все тесты, чтобы переопределять методы — ❌ Неверно
>
> **Что на самом деле:** Это классический misuse через наследование. Page Object — про композицию: каждая страница это отдельный класс, и тесты используют их как обычные объекты. `BasePage` может существовать для общих методов (waitForLoad, getCurrentUrl), но это не суть паттерна. Наследование тестов от Page классов — антипаттерн (тест НЕ ЯВЛЯЕТСЯ страницей).
>
> **Откуда путаница:** В Java принято решать переиспользование через наследование, что приводит к фабриканту иерархий.
>
> **Если бы это было правдой:** Тесты не могли бы переходить между страницами, так как каждый класс представлял бы одну страницу.
>
> ---
>
> #### D) Запрограммировать поведение страницы в JavaScript и инжектить через `executeScript`, чтобы избежать медленных WebDriver-вызовов — ❌ Неверно
>
> **Что на самом деле:** Это не Page Object, а JS-инжекция (антипаттерн в UI-тестах). Page Object — это структурный паттерн уровня Java-кода: классы инкапсулируют локаторы и действия. JS используется только в крайних случаях (например, scroll into view), и это не имеет отношения к Page Object.
>
> **Откуда путаница:** Желание ускорить тесты приводит к идее «обойти WebDriver», что нарушает саму суть UI-теста.
>
> **Если бы это было правдой:** Тесты тестировали бы JS-код вместо реального user flow — теряли бы основное преимущество E2E.

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


> [!mcq]
>
> **Вопрос:** Что делает `PageFactory.initElements(driver, this)` с полями, помеченными `@FindBy`?
>
> ---
>
> #### A) Сразу при инициализации находит все элементы в DOM и сохраняет их как обычные `WebElement` ссылки — поэтому страницу нужно полностью загрузить до создания Page Object — ❌ Неверно
>
> **Что на самом деле:** PageFactory НЕ ищет элементы при `initElements`. Вместо этого он создаёт lazy-прокси (через `java.lang.reflect.Proxy`), которые при каждом методе (`click`, `sendKeys`) выполняют поиск заново. Поэтому Page Object можно создавать до загрузки страницы.
>
> **Откуда путаница:** Eager-инициализация — интуитивное предположение для «фабрики».
>
> **Если бы это было правдой:** Любая динамическая страница ломала бы тесты, так как элементы загружаются позже AJAX-запросов.
>
> ---
>
> #### B) Заменяет поля на динамические прокси, которые при каждом вызове метода (`click`, `sendKeys`) выполняют `findElement` заново, что защищает от `StaleElementReferenceException` — ✓ Верно
>
> **Развёрнутое объяснение:** PageFactory использует reflection и java.lang.reflect.Proxy. Каждое поле, помеченное `@FindBy`, заменяется на прокси-объект, реализующий интерфейс `WebElement`. Этот прокси хранит `By`-локатор, и каждый вызов метода (`click()`, `getText()`, `isDisplayed()`) триггерит новый `driver.findElement(by)`. Это решает классическую проблему: после `driver.navigate()` или AJAX-обновления старая ссылка на DOM-элемент становится stale, и работа с ней бросает `StaleElementReferenceException`. С прокси такой проблемы нет.
>
> **Пример:**
> ```java
> public class SearchPage {
>     // ВНУТРИ: java.lang.reflect.Proxy реализующий WebElement
>     @FindBy(id = "query") private WebElement queryField;
>     @FindBy(css = "button[type=submit]") private WebElement searchBtn;
>     @FindBy(css = ".result") private List<WebElement> results;
>
>     public SearchPage(WebDriver driver) {
>         PageFactory.initElements(driver, this);
>     }
>
>     public List<String> search(String term) {
>         queryField.sendKeys(term);   // findElement выполняется здесь
>         searchBtn.click();           // findElement выполняется здесь снова
>         return results.stream()      // findElements выполняется здесь
>             .map(WebElement::getText)
>             .toList();
>     }
> }
> ```
>
> **Когда применять:** Динамические страницы с AJAX, SPA с навигацией без полной перезагрузки. Особенно полезно когда между действиями DOM может перестроиться.
>
> **Подводные камни:** `List<WebElement>` с @FindBy — прокси для всего списка, но при каждом обращении к элементу ВНУТРИ списка тоже происходит поиск, что замедляет циклы. Для очень частых обращений лучше явный `driver.findElements()` с кешированием в локальную переменную в рамках одного метода.
>
> **Связанные вопросы:** [[Q5]] — Page Object, [[Q10]] — flaky тесты.
>
> ---
>
> #### C) Компилирует поля в bytecode-инструкции, которые встраиваются в Selenium driver через ASM, чтобы избежать reflection в runtime — ❌ Неверно
>
> **Что на самом деле:** PageFactory работает целиком через runtime reflection и `java.lang.reflect.Proxy` — никакого bytecode-magic (ASM, Javassist, ByteBuddy) там нет. Это простой инструмент стандартной Java reflection API.
>
> **Откуда путаница:** Mockito, Hibernate, Spring используют bytecode-инструментирование, и по аналогии можно предположить то же для PageFactory.
>
> **Если бы это было правдой:** PageFactory требовал бы `-javaagent` или специальной сборки.
>
> ---
>
> #### D) Регистрирует Selenium-listeners на каждое поле, чтобы автоматически делать скриншот при падении теста — ❌ Неверно
>
> **Что на самом деле:** PageFactory не имеет отношения к скриншотам или listeners. Для скриншотов используется `TakesScreenshot` интерфейс и явный вызов в `@AfterEach`. Listeners в Selenium 4 — это `WebDriverListener` для глобальных событий, тоже не связано с PageFactory.
>
> **Откуда путаница:** «Factory» звучит как «фреймворк со всем-всем», но это маленькая утилита для одной задачи.
>
> **Если бы это было правдой:** Был бы простой способ автоскриншотов без отдельных JUnit extensions.

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


> [!mcq]
>
> **Вопрос:** Что произойдёт, если после открытия нового окна (target="_blank") сразу попытаться найти элемент в новом окне через `driver.findElement(...)` без переключения контекста?
>
> ---
>
> #### A) WebDriver автоматически переключится на новое окно, так как оно стало активным в фокусе браузера — ❌ Неверно
>
> **Что на самом деле:** WebDriver session привязана к window handle и НЕ переключается автоматически. Даже если новое окно «активно» в браузере с точки зрения пользователя, driver продолжит работать с тем окном, в котором был на момент последнего `switchTo().window(...)`. Это часто путает новичков.
>
> **Откуда путаница:** Опыт ручного использования браузера — клик на ссылку с target="_blank" фокусирует новое окно для человека.
>
> **Если бы это было правдой:** Не было бы паттерна сохранения `mainWindow = driver.getWindowHandle()` перед открытием нового окна.
>
> ---
>
> #### C) WebDriver останется в старом окне и `findElement` будет искать в его DOM — нужно явно переключиться через `driver.switchTo().window(newHandle)`, предварительно получив handle через `getWindowHandles()` — ✓ Верно
>
> **Развёрнутое объяснение:** В Selenium контекст окна нужно переключать вручную. Типичный паттерн: до клика сохранить `String mainWindow = driver.getWindowHandle()`, после клика получить все handles через `Set<String> all = driver.getWindowHandles()`, найти отличный от mainWindow и `switchTo().window(newHandle)`. После работы с новым окном — `driver.close()` (закрывает только текущее окно) и обязательно `switchTo().window(mainWindow)`, иначе следующие операции упадут с `NoSuchWindowException`.
>
> **Пример:**
> ```java
> // 1. Сохраняем main window
> String mainWindow = driver.getWindowHandle();
> Set<String> oldHandles = driver.getWindowHandles();
>
> // 2. Триггерим открытие нового окна
> driver.findElement(By.linkText("Open in new tab")).click();
>
> // 3. Ждём появления нового handle
> new WebDriverWait(driver, Duration.ofSeconds(10))
>     .until(d -> d.getWindowHandles().size() > oldHandles.size());
>
> // 4. Переключаемся на новый
> Set<String> newHandles = driver.getWindowHandles();
> newHandles.removeAll(oldHandles);
> String newWindow = newHandles.iterator().next();
> driver.switchTo().window(newWindow);
>
> // 5. Работаем в новом окне
> assertThat(driver.getTitle()).isEqualTo("New Page");
>
> // 6. Закрываем и возвращаемся
> driver.close();
> driver.switchTo().window(mainWindow);
> ```
>
> **Когда применять:** OAuth-логин (popup-окна), download-страницы, любые ссылки с target="_blank". Также для iframe нужен `switchTo().frame()` и обратно `switchTo().defaultContent()`.
>
> **Подводные камни:** `driver.close()` закрывает текущее окно, `driver.quit()` — всю сессию. Если забыли вернуться на mainWindow после `close()`, следующая операция упадёт. После `defaultContent()` возврат на верхний уровень из любого вложенного frame.
>
> **Связанные вопросы:** [[Q14]] — anti-patterns, [[Q10]] — flaky тесты (window timing).
>
> ---
>
> #### B) WebDriver бросит `WindowAmbiguousException`, требуя явно указать, на каком окне работать — ❌ Неверно
>
> **Что на самом деле:** Такого исключения в Selenium нет. WebDriver просто молча продолжит работать с предыдущим окном. Возможные ошибки: `NoSuchElementException` (если ищем элемент, которого нет в старом окне) или `NoSuchWindowException` (если старое окно закрылось).
>
> **Откуда путаница:** Логично ожидать явной ошибки от автоматизации.
>
> **Если бы это было правдой:** Было бы проще отлаживать многооконные сценарии — но реальность сложнее.
>
> ---
>
> #### D) Все операции пойдут в новое окно по умолчанию, потому что WebDriver всегда работает с последним открытым окном — ❌ Неверно
>
> **Что на самом деле:** WebDriver привязан к конкретному window handle, а не «последнему». Без явного `switchTo().window()` все операции остаются в текущем контексте. То же касается iframe — нужен `switchTo().frame()`.
>
> **Откуда путаница:** «Window of focus» в OS-терминологии действительно последнее активное.
>
> **Если бы это было правдой:** Невозможно было бы работать с несколькими окнами параллельно в одном тесте.

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


> [!mcq]
>
> **Вопрос:** Какова основная роль Hub в архитектуре Selenium Grid 4?
>
> ---
>
> #### A) Hub запускает все браузеры локально и распределяет нагрузку между ядрами CPU — ❌ Неверно
>
> **Что на самом деле:** Hub сам по себе НЕ запускает браузеры — это делают Node-машины. Hub лишь принимает запросы от клиентов и роутит их к подходящему Node на основе capabilities (browser, version, OS). Локальный запуск браузеров — это standalone mode (`java -jar selenium-server standalone`), не Grid.
>
> **Откуда путаница:** Слово «Hub» ассоциируется с «центром управления», что наводит на мысль о выполнении на нём.
>
> **Если бы это было правдой:** Не было бы смысла в Grid — это был бы просто standalone Selenium Server.
>
> ---
>
> #### D) Hub в Grid 4 — это набор компонентов (Router, Distributor, Session Map, New Session Queue), принимающий запросы тестов и распределяющий их по Node-машинам с подходящими capabilities — ✓ Верно
>
> **Развёрнутое объяснение:** В Selenium Grid 4 (radical rewrite по сравнению с Grid 3) Hub разбит на отдельные компоненты, что позволяет масштабировать каждый независимо. Router принимает входящий HTTP-запрос от теста, Distributor находит свободный Node с нужными capabilities (например, Firefox 120 на Linux), Session Map хранит соответствие session-id → node, New Session Queue буферизует запросы при нехватке Node. Сам browser-instance создаётся и работает на Node-машине. Hub можно развернуть как single binary (`hub`) или как distributed микросервисы для облака.
>
> **Пример:**
> ```bash
> # Hub
> java -jar selenium-server-4.27.0.jar hub --port 4444
>
> # Node 1 (Linux + Chrome)
> java -jar selenium-server-4.27.0.jar node \
>   --hub http://hub:4444 \
>   --max-sessions 5
>
> # Node 2 (macOS + Safari, отдельная машина)
> java -jar selenium-server-4.27.0.jar node --hub http://hub:4444
>
> # Тест — указывает URL только Hub
> WebDriver driver = new RemoteWebDriver(
>     new URL("http://hub:4444/wd/hub"),
>     new ChromeOptions()  // Hub найдёт node с Chrome
> );
> ```
>
> **Когда применять:** Cross-browser regression (нужны Safari/Edge/Firefox), параллельный запуск 100+ тестов в CI, распределённое тестирование на разных OS. Альтернативы: BrowserStack/Sauce Labs (Grid-as-a-Service), Selenoid/Moon (Docker-based).
>
> **Подводные камни:** Сетевая задержка между Hub и Node добавляет latency к каждой команде. Версии Grid и driver должны совпадать. Без `--max-sessions` Node может перегрузиться. Для Kubernetes лучше использовать distributed mode с Helm chart.
>
> **Связанные вопросы:** [[Q9]] — параллельные тесты, [[Q12]] — CI/CD.
>
> ---
>
> #### C) Hub — это headless-браузер, который рендерит HTML на сервере и возвращает screenshot клиенту — ❌ Неверно
>
> **Что на самом деле:** Hub не браузер и не рендерит ничего. Headless-режим — опция настоящих браузеров (Chrome `--headless=new`), запущенных на Node. Hub — это диспетчер запросов.
>
> **Откуда путаница:** Headless-сценарии часто работают через Grid, но это два независимых концепта.
>
> **Если бы это было правдой:** Не нужны были бы chromedriver/geckodriver на Node-машинах.
>
> ---
>
> #### B) Hub компилирует тесты из Selenese-DSL в нативные команды браузера через JIT-оптимизатор — ❌ Неверно
>
> **Что на самом деле:** В Selenium WebDriver нет Selenese DSL — это была часть Selenium 1 (Selenium RC), удалённая много лет назад. Тесты пишутся на обычной Java/Python/JS и общаются через W3C WebDriver Protocol (HTTP+JSON). Никакой JIT-компиляции команд нет.
>
> **Откуда путаница:** Selenese существовал в Selenium IDE (HTML-based) и оставил след в документации старых лет.
>
> **Если бы это было правдой:** Тесты на разных языках работали бы по-разному в Grid.

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


> [!mcq]
>
> **Вопрос:** Что произойдёт, если в параллельных UI-тестах разные потоки используют один и тот же `WebDriver` instance через статическое поле?
>
> ---
>
> #### B) Тесты ускорятся в N раз, потому что WebDriver thread-safe и распараллеливание происходит автоматически на уровне HTTP-запросов — ❌ Неверно
>
> **Что на самом деле:** WebDriver НЕ thread-safe — это явно указано в документации. Команды отправляются последовательно через одну session, и если два потока вызывают `driver.findElement` одновременно, поведение неопределённое: race conditions, session corruption, либо просто блокировка одного потока.
>
> **Откуда путаница:** RemoteWebDriver работает через HTTP, что наводит на мысль о stateless-протоколе. Но session — stateful.
>
> **Если бы это было правдой:** Не было бы паттернов с `ThreadLocal<WebDriver>` в каждом фреймворке параллельных UI-тестов.
>
> ---
>
> #### A) Потоки будут конкурировать за один browser session: команды перемешаются, элементы будут искаться не в том окне, тесты станут flaky и непредсказуемыми — ✓ Верно
>
> **Развёрнутое объяснение:** WebDriver session — stateful: она хранит текущее окно, текущий frame, cookies, последний найденный элемент. Если поток A делает `driver.get("/page1")`, а одновременно поток B делает `driver.findElement(...)`, поток B может искать в /page1 (вместо своей страницы) или вообще получить undefined-behavior. Решение — `ThreadLocal<WebDriver>`: каждый поток создаёт свою browser-сессию, изоляция гарантирована. В JUnit 5 это сочетают с `@TestInstance(Lifecycle.PER_CLASS)` и `parallel=concurrent`. В TestNG — `@BeforeMethod` создаёт driver на каждый метод.
>
> **Пример:**
> ```java
> public class DriverManager {
>     private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
>
>     public static WebDriver get() {
>         WebDriver d = DRIVER.get();
>         if (d == null) {
>             ChromeOptions opts = new ChromeOptions().addArguments("--headless=new");
>             d = new ChromeDriver(opts);
>             DRIVER.set(d);
>         }
>         return d;
>     }
>
>     public static void quit() {
>         WebDriver d = DRIVER.get();
>         if (d != null) {
>             d.quit();
>             DRIVER.remove(); // важно! иначе утечка между запусками
>         }
>     }
> }
>
> // junit-platform.properties
> // junit.jupiter.execution.parallel.enabled=true
> // junit.jupiter.execution.parallel.mode.default=concurrent
> // junit.jupiter.execution.parallel.config.strategy=fixed
> // junit.jupiter.execution.parallel.config.fixed.parallelism=4
>
> @AfterEach
> void tearDown() { DriverManager.quit(); }
> ```
>
> **Когда применять:** Большой suite UI-тестов (50+), хочется сократить время с 2 часов до 20 минут. Обычно 4-8 параллельных driver на CI-runner с 16 ГБ RAM.
>
> **Подводные камни:** Тесты должны быть полностью изолированными (нет shared state в БД, нет fixtures на одного пользователя). Грид нужен для cross-machine параллельности. ThreadLocal.remove() обязателен иначе утечка browser-процессов.
>
> **Связанные вопросы:** [[Q8]] — Selenium Grid, [[Q14]] — anti-patterns.
>
> ---
>
> #### C) JUnit 5 автоматически создаст копии WebDriver для каждого потока через ClassLoader-isolation — ❌ Неверно
>
> **Что на самом деле:** JUnit 5 не делает ClassLoader-isolation между потоками. Это OSGi/ServiceLoader-механизмы для других случаев. JUnit просто запускает методы в разных потоках одного ClassLoader, и статические поля общие.
>
> **Откуда путаница:** TestNG имел `@Test(threadPoolSize=...)`, что создавало впечатление магии.
>
> **Если бы это было правдой:** Не нужен был бы ThreadLocal в подавляющем большинстве парралельных тестов.
>
> ---
>
> #### D) Selenium 4 автоматически создаёт изолированные BiDi-каналы для каждого потока, поэтому статический driver безопасен — ❌ Неверно
>
> **Что на самом деле:** BiDi (Bidirectional) — протокол для двусторонней коммуникации с браузером в Selenium 4 (события, network интерсепты), но он не решает проблему thread-safety одного `WebDriver` instance. Это улучшение API, а не concurrency-механизм.
>
> **Откуда путаница:** Новые фичи Selenium 4 (BiDi, relative locators, CDP) звучат как «всё улучшилось».
>
> **Если бы это было правдой:** Документация Selenium 4 убрала бы предупреждение о thread-safety, но оно осталось.

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


> [!mcq]
>
> **Вопрос:** Что является ОСНОВНОЙ причиной flaky UI-тестов в Selenium?
>
> ---
>
> #### A) Недостаточная мощность CI-runner: при увеличении CPU/RAM flaky-тесты автоматически становятся стабильными — ❌ Неверно
>
> **Что на самом деле:** Мощность CI помогает, но не решает корень. Flaky тесты — это race conditions между тестом и приложением (UI ещё не обновилось, элемент уже исчез/перерисован, AJAX в процессе). Даже на самой быстрой машине race conditions остаются — просто сдвигается окно их проявления. Решение — синхронизация на уровне условий (WebDriverWait), а не «накачка железа».
>
> **Откуда путаница:** На слабых runner'ах flaky тесты падают чаще, что создаёт иллюзию что проблема в производительности.
>
> **Если бы это было правдой:** Тесты в production были бы 100% стабильны на dedicated-серверах.
>
> ---
>
> #### B) Race conditions между состоянием UI и действиями теста: жёсткие `Thread.sleep`, хрупкие локаторы (классы, абсолютный XPath), отсутствие явных ожиданий после AJAX — ✓ Верно
>
> **Развёрнутое объяснение:** Главная причина — несинхронизация. UI-тест — это асинхронное взаимодействие, где есть три временные шкалы: тест, браузер, сервер. Если тест предполагает, что после `click()` элемент сразу появится — он будет flaky на медленной сети или нагруженном сервере. Решение — явные ожидания (`WebDriverWait` + `ExpectedConditions`), стабильные локаторы (`id`, `data-testid` вместо `.btn-primary-v2`), полная изоляция тестов друг от друга (нет shared user/data), retry-логика как safety-net, но не основное решение.
>
> **Пример:**
> ```java
> // АНТИПАТТЕРН (flaky)
> driver.findElement(By.id("login")).click();
> Thread.sleep(2000);
> driver.findElement(By.className("dashboard-title")).getText();
> // ↑ flaky при медленной загрузке + класс может смениться при редизайне
>
> // BEST PRACTICE
> driver.findElement(By.id("login")).click();
> WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
> WebElement title = wait.until(ExpectedConditions
>     .visibilityOfElementLocated(By.cssSelector("[data-testid='dashboard-title']")));
> String text = title.getText();
>
> // Retry как safety net
> @RetryingTest(maxAttempts = 2)
> void unstableScenario() { ... }
>
> // Скриншот при падении для диагностики
> @AfterEach
> void screenshotOnFailure(TestInfo info) {
>     if (testFailed) {
>         File f = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
>         Files.copy(f.toPath(), Paths.get("screenshots", info.getDisplayName() + ".png"));
>     }
> }
> ```
>
> **Когда применять:** При первых признаках нестабильности — не откладывайте. Установите retry+logging+screenshots сразу, чтобы накапливать данные для диагностики.
>
> **Подводные камни:** Retry скрывает реальные баги — используйте его осторожно, всегда логируйте retry-events. Видеозапись (Selenoid/Moon, BrowserStack) — мощный инструмент диагностики, особенно для timing-issues.
>
> **Связанные вопросы:** [[Q3]] — implicit vs explicit wait, [[Q4]] — Thread.sleep, [[Q14]] — anti-patterns.
>
> ---
>
> #### C) Использование headless-режима — реальный Chrome намного стабильнее, чем `--headless=new` — ❌ Неверно
>
> **Что на самом деле:** Headless и headful режимы дают почти идентичные результаты в плане flakyness. Старый headless (до Chrome 109) имел проблемы с rendering некоторых элементов, но `--headless=new` (new headless mode) — это полноценный Chrome без UI. Flakyness не связана с headful/headless.
>
> **Откуда путаница:** Тесты на CI (headless) падают чаще, чем локально — но из-за разницы окружения (медленный диск, нет GPU), а не headless самого.
>
> **Если бы это было правдой:** Никто не использовал бы headless в CI.
>
> ---
>
> #### D) Отсутствие `try-catch` вокруг каждого `findElement` — если ловить `NoSuchElementException` и повторять, flakyness исчезнет — ❌ Неверно
>
> **Что на самом деле:** Try-catch-retry на каждом findElement — это и есть Implicit Wait под другим именем, со всеми его недостатками. Более того, это маскирует проблемы (тест прошёл, но из-за неправильного состояния). Правильно — выразить ожидание явно через WebDriverWait + ExpectedCondition.
>
> **Откуда путаница:** Defensive programming в Java приучает к try-catch на всё.
>
> **Если бы это было правдой:** Не было бы паттерна Page Object с lazy proxy через PageFactory.

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


> [!mcq]
>
> **Вопрос:** Чем Playwright принципиально отличается от Selenium с точки зрения работы с UI и почему это снижает flakyness?
>
> ---
>
> #### A) Playwright написан на Rust, что даёт ему системные преимущества в скорости по сравнению с Java-Selenium — ❌ Неверно
>
> **Что на самом деле:** Playwright написан на TypeScript/Node.js (с биндингами для Java, Python, .NET). Производительность достигается не за счёт языка, а за счёт архитектуры: прямое использование Chrome DevTools Protocol (вместо HTTP roundtrip), встроенный auto-wait, контроль над событиями браузера.
>
> **Откуда путаница:** Сейчас популярны быстрые tooling-инструменты на Rust (Bun, Biome), это создаёт ассоциацию.
>
> **Если бы это было правдой:** Не было бы биндингов на JVM, и Java-разработчики не использовали бы Playwright.
>
> ---
>
> #### D) Playwright использует CDP (Chrome DevTools Protocol) напрямую и имеет встроенный auto-wait: каждый `locator.click()` сам ждёт пока элемент visible+enabled+stable, нет необходимости в WebDriverWait — ✓ Верно
>
> **Развёрнутое объяснение:** Selenium общается с браузером через W3C WebDriver Protocol (HTTP+JSON), что добавляет latency и не даёт доступа к внутренним событиям браузера. Playwright использует CDP — WebSocket-протокол отладочных команд Chrome — и аналогичные нативные API для Firefox/WebKit. Это даёт: (1) низкую latency (на порядок быстрее, чем HTTP), (2) события сети/JS/console в реальном времени, (3) встроенный auto-wait — `locator.click()` ждёт пока элемент станет actionable (visible, enabled, stable, receiver of events). В Selenium для того же нужно `WebDriverWait` + `ExpectedConditions.elementToBeClickable`. Кроме того, Playwright имеет лучшую parallel-модель (browser context = isolated session) и нативную поддержку network interception.
>
> **Пример:**
> ```java
> // Selenium — нужен явный wait
> WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
> wait.until(ExpectedConditions.elementToBeClickable(By.id("submit"))).click();
>
> // Playwright — auto-wait built in
> page.locator("#submit").click(); // сам ждёт actionable до timeout (30s default)
>
> // Network interception (в Selenium требует BiDi, в Playwright нативно)
> page.route("**/api/users", route -> {
>     route.fulfill(new Route.FulfillOptions()
>         .setStatus(200)
>         .setBody("[{\"id\":1,\"name\":\"Mock\"}]"));
> });
>
> // Isolated browser context для parallel tests
> BrowserContext context = browser.newContext(); // свежие cookies, storage
> Page page = context.newPage();
> ```
>
> **Когда применять:** Новые проекты, особенно SPA с тяжёлым AJAX. Команды, страдающие от flakyness в Selenium. Тесты с network mocking. НЕ применять, если у вас Safari как основная цель (Playwright поддерживает WebKit, но не Safari напрямую) или legacy-кодовая база с большой инвестицией в Selenium PageObject.
>
> **Подводные камни:** Java-биндинги Playwright — обёртка над Node.js процессом (запускает headless Node для общения с браузерами). Это добавляет startup-overhead. Также Playwright моложе, экосистема меньше (плагины, отчёты).
>
> **Связанные вопросы:** [[Q3]] — wait в Selenium, [[Q10]] — flaky тесты, [[Q14]] — anti-patterns.
>
> ---
>
> #### C) Playwright — это плагин Selenium, добавляющий улучшенные локаторы, но архитектура та же — ❌ Неверно
>
> **Что на самом деле:** Playwright — независимый фреймворк от Microsoft (создан в 2020 году командой, ранее работавшей над Puppeteer в Google). Никаких общих компонентов с Selenium нет. Это конкурент, а не расширение.
>
> **Откуда путаница:** Both работают с браузерами через protocols, синтаксис похож (`page.locator()` vs `driver.findElement()`).
>
> **Если бы это было правдой:** Установка Playwright требовала бы наличия Selenium-сервера.
>
> ---
>
> #### B) Playwright работает только в Chromium-браузерах (как Cypress) — нет поддержки Firefox и WebKit — ❌ Неверно
>
> **Что на самом деле:** Playwright поддерживает Chromium, Firefox и WebKit (движок Safari) из коробки — это его ключевое преимущество над Cypress (который был ограничен Chromium до версии 10). Один и тот же тест запускается во всех трёх движках.
>
> **Откуда путаница:** Cypress долго был «only Chrome», Playwright и Cypress часто упоминаются вместе.
>
> **Если бы это было правдой:** Playwright не позиционировался бы как «cross-browser» в маркетинге.

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


> [!mcq]
>
> **Вопрос:** Какой компонент в Selenium 4.6+ заменил необходимость явно использовать WebDriverManager или вручную скачивать chromedriver?
>
> ---
>
> #### B) Selenium Grid — теперь Hub автоматически скачивает все driver'ы при первом запросе — ❌ Неверно
>
> **Что на самом деле:** Grid и driver-management — разные подсистемы. Grid не занимается скачиванием driver'ов на клиентской стороне. Driver нужен и для standalone-использования.
>
> **Откуда путаница:** Grid 4 действительно много улучшил, что наводит на мысль о «всё в одном».
>
> **Если бы это было правдой:** Не было бы смысла в Selenium Manager как отдельной фиче.
>
> ---
>
> #### A) Selenium Manager — встроенный CLI-инструмент, который автоматически детектит версию установленного браузера и скачивает совместимый driver при создании `new ChromeDriver()` — ✓ Верно
>
> **Развёрнутое объяснение:** Selenium Manager — новая фича, добавленная в Selenium 4.6 (ноябрь 2022) как «native driver management». Это маленький CLI-инструмент на Rust, бандлируется внутрь selenium-java jar. При первом вызове `new ChromeDriver()` он: (1) определяет версию установленного Chrome, (2) ищет соответствующий chromedriver в кэше, (3) если нет — скачивает с officialных source-ов (Chrome for Testing API), (4) запускает chromedriver. Это полностью заменяет WebDriverManager от Boni Garcia, который раньше был стандартом. WebDriverManager всё ещё работает и используется в legacy-проектах, но новые проекты не нуждаются в нём.
>
> **Пример:**
> ```java
> // Раньше (Selenium 3 + WebDriverManager)
> WebDriverManager.chromedriver().setup();
> WebDriver driver = new ChromeDriver();
>
> // Раньше (вручную)
> System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
> WebDriver driver = new ChromeDriver();
>
> // Сейчас (Selenium 4.6+)
> WebDriver driver = new ChromeDriver(); // Selenium Manager делает всё сам
>
> // Кеш в ~/.cache/selenium/
> // CLI: selenium-manager --browser chrome (для отладки)
> ```
>
> **Когда применять:** Любые новые проекты на Selenium 4.6+. Headless CI с регулярно обновляющимся Chrome (нет необходимости вручную bump-ить driver-версию).
>
> **Подводные камни:** Требуется интернет при первом запуске (или предзагруженный кэш). В корпоративных средах за proxy может потребоваться `HTTPS_PROXY`. Если установлено несколько браузеров, нужно указать `ChromeOptions().setBinary("/path/to/chrome")`.
>
> **Связанные вопросы:** [[Q1]] — архитектура, [[Q8]] — Selenium Grid.
>
> ---
>
> #### C) Maven plugin `selenium-maven-plugin` — теперь driver-ы качаются автоматически на этапе `mvn test` — ❌ Неверно
>
> **Что на самом деле:** Такого плагина в современной Selenium-экосистеме нет (есть устаревший плагин для Selenium RC, но он не имеет отношения к driver-management). Driver-management работает на runtime через Selenium Manager, а не на этапе сборки.
>
> **Откуда путаница:** Логично ожидать Maven-интеграцию для Java-проекта.
>
> **Если бы это было правдой:** Не работало бы в Gradle-проектах.
>
> ---
>
> #### D) Docker Selenium images — нужно всегда запускать тесты в контейнере, где chromedriver уже установлен — ❌ Неверно
>
> **Что на самом деле:** Docker-образы (`selenium/standalone-chrome`) — это один из способов запуска, но не замена driver-management. Многие команды запускают тесты локально/в CI без Docker, и им нужен механизм управления driver'ами — это и есть Selenium Manager.
>
> **Откуда путаница:** Docker действительно решает проблему «нужного chromedriver», но это другой подход.
>
> **Если бы это было правдой:** Selenium не запускался бы без Docker, что не соответствует реальности.

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


> [!mcq]
>
> **Вопрос:** Какова главная ценность связки Cucumber + Selenium по сравнению с обычными JUnit-тестами на Page Object?
>
> ---
>
> #### A) Cucumber-тесты выполняются в 5 раз быстрее, потому что Gherkin компилируется в нативные Selenium-команды без посредников — ❌ Неверно
>
> **Что на самом деле:** Cucumber НЕ ускоряет выполнение — он добавляет слой поверх обычных Selenium-вызовов. Gherkin-сценарии парсятся и матчатся со step-definitions через регулярные выражения, что фактически добавляет небольшой overhead. Скорость не является преимуществом Cucumber.
>
> **Откуда путаница:** «Естественный язык» звучит как «компиляция», но это runtime-pattern-matching.
>
> **Если бы это было правдой:** Cucumber использовали бы для performance-критичных тестов.
>
> ---
>
> #### B) Cucumber не нужен — он только добавляет boilerplate (feature-файлы + step-definitions) и усложняет maintenance без реальной пользы — ❌ Неверно
>
> **Что на самом деле:** Это критика, валидная для команд, где никто, кроме разработчиков, тесты не читает. Но в командах с PO/BA/QA, где требования обсуждаются через сценарии — Cucumber даёт real value: единый язык, прямая связь требование→тест, документация = тесты (живая спецификация). Утверждение «всегда лишний» — overstatement.
>
> **Откуда путаница:** Многие команды действительно используют Cucumber «for show» без BDD-процесса, что и создаёт boilerplate без выгоды.
>
> **Если бы это было правдой:** Cucumber не использовался бы тысячами компаний продакшен.
>
> ---
>
> #### C) Cucumber позволяет описывать сценарии на естественном языке (Gherkin: Given/When/Then), что даёт shared language между бизнесом, QA и разработчиками — спецификация становится executable документацией — ✓ Верно
>
> **Развёрнутое объяснение:** Главная ценность Cucumber — это не технический speed-up, а коммуникация. В классическом BDD-процессе (Behaviour-Driven Development) Three Amigos (PO + QA + Dev) совместно пишут acceptance-criteria в Gherkin. Бизнес-аналитик видит сценарии «как живые требования», QA автоматизирует их через step-definitions с Selenium внутри, разработчики проверяют, что код реализует ровно эти сценарии. Получается living documentation: тесты не отстают от документации, потому что они и есть документация. Без BDD-процесса (если фичу-файлы пишет один dev в одиночку) Cucumber превращается в overhead.
>
> **Пример:**
> ```gherkin
> # checkout.feature - читаемо для PO/QA/BA
> Feature: Checkout
>   As a customer
>   I want to pay for items in my cart
>   So that I receive my order
>
>   Background:
>     Given I am logged in as "alice@example.com"
>     And I have items in my cart:
>       | product   | qty |
>       | Laptop    | 1   |
>       | Mouse     | 2   |
>
>   Scenario: Successful checkout with credit card
>     When I go to checkout
>     And I enter shipping address "123 Main St"
>     And I pay with card "4111111111111111"
>     Then I see order confirmation
>     And I receive email "order-confirmation"
> ```
>
> ```java
> @When("I pay with card {string}")
> public void payWithCard(String cardNumber) {
>     checkoutPage.enterCardNumber(cardNumber)
>                 .clickPay();
> }
> ```
>
> **Когда применять:** Команды с активной коммуникацией бизнес-QA-dev, проекты с регулируемыми acceptance-criteria (страхование, банкинг), команды, переходящие на BDD/specification-by-example. НЕ применять для тестов разработчиков «себе» — лучше plain JUnit.
>
> **Подводные камни:** Антипаттерн — слишком технические step'ы («I click button with id submit») — это imperative, не BDD. Правильно — declarative («I confirm my order»). Регексы в @When/@Then становятся неуправляемыми при большом количестве — используйте Cucumber Expressions ({string}, {int}).
>
> **Связанные вопросы:** [[Q5]] — Page Object внутри step-definitions, [[Q15]] — test pyramid.
>
> ---
>
> #### D) Cucumber заменяет PageObject Pattern — теперь не нужно писать классы для страниц, всё описывается в feature-файлах — ❌ Неверно
>
> **Что на самом деле:** Cucumber и PageObject ОРТОГОНАЛЬНЫ. Cucumber описывает business-сценарии, PageObject инкапсулирует UI-взаимодействия. Step-definitions внутри используют PageObject'ы. Без PageObject step-definitions превращаются в спагетти из `driver.findElement(...)`.
>
> **Откуда путаница:** Оба паттерна про «организацию тестов», но решают разные проблемы.
>
> **Если бы это было правдой:** Не было бы примеров «Cucumber + PageObject» в туториалах.

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


> [!mcq]
>
> **Вопрос:** Какой подход является ХУДШИМ anti-pattern в Selenium-тестах с точки зрения долгосрочной поддерживаемости?
>
> ---
>
> #### B) Использование `WebDriverManager.chromedriver().setup()` для управления driver'ами — ❌ Неверно
>
> **Что на самом деле:** WebDriverManager — нормальный инструмент, использовавшийся годами в production. С Selenium 4.6 он избыточен (Selenium Manager делает то же встроенно), но это не anti-pattern и не вредит maintainability.
>
> **Откуда путаница:** В Selenium 4.6+ WebDriverManager обычно не нужен, но «не нужен» != «anti-pattern».
>
> **Если бы это было правдой:** Тысячи проектов с WDM имели бы проблемы с maintenance.
>
> ---
>
> #### A) Тестирование бизнес-логики через UI (например, проверка фильтров заказов через клики), тогда как тот же сценарий проверяется быстрее и надёжнее через API-тесты — ✓ Верно
>
> **Развёрнутое объяснение:** Этот anti-pattern — самый дорогой. UI-тест выполняется на порядки медленнее API-теста (20 секунд vs 200 мс), требует поднятого браузера и frontend, ломается при редизайнах UI несвязанных с бизнес-логикой, flaky из-за timing-issues. Test Pyramid (Mike Cohn) явно предписывает: бизнес-логика → unit/integration; интеграция UI ↔ backend → пара smoke-тестов на ключевых user journey (login, checkout). Если вы проверяете, что фильтр возвращает 5 заказов — делайте API call. Если проверяете, что фильтр RENDERS в UI корректно — UI тест нужен, но один на фильтр, а не на каждую комбинацию данных.
>
> **Пример:**
> ```java
> // АНТИПАТТЕРН — проверка бизнес-логики через UI
> @Test // ~20 секунд, flaky
> void shouldFilter5PendingOrders() {
>     loginPage.login("alice", "pass");
>     dashboardPage.goToOrders();
>     ordersPage.selectFilter("PENDING");
>     waitFor(() -> ordersPage.getOrderCount() == 5);
>     assertEquals(5, ordersPage.getOrderCount());
> }
>
> // BEST PRACTICE — API тест
> @Test // ~200 ms, стабильный
> void apiShouldReturn5PendingOrders() {
>     given().auth().basic("alice", "pass")
>         .when().get("/api/orders?status=PENDING")
>         .then().statusCode(200)
>                .body("items", hasSize(5));
> }
>
> // Дополнительно — один smoke UI-тест на отображение
> @Test
> void uiShouldRenderOrdersList() {
>     loginPage.login(testUser);
>     dashboardPage.goToOrders();
>     assertThat(ordersPage.isVisible()).isTrue();
>     assertThat(ordersPage.getColumns()).contains("Order ID", "Status", "Total");
> }
> ```
>
> **Когда применять:** ВСЕГДА сначала проверять: можно ли проверить это без UI? Через API? Через unit-тест? UI оставлять для visual/integration smoke.
>
> **Подводные камни:** Команды часто оправдывают «проверим всё через UI потому что это E2E». Real E2E — не «всё через UI», а минимально достаточный набор happy paths.
>
> **Связанные вопросы:** [[Q15]] — test pyramid, [[Q10]] — flaky тесты, [[Q11]] — альтернативы.
>
> ---
>
> #### C) Использование `data-testid` атрибутов как локаторов вместо CSS-классов — ❌ Неверно
>
> **Что на самом деле:** `data-testid` — это, наоборот, BEST PRACTICE. Это явный контракт между разработчиком и тестировщиком, стабильный к редизайнам стилей. Многие команды (React, Vue) используют `data-testid` как стандарт. CSS-классы — anti-pattern, потому что меняются с CSS-рефакторингом.
>
> **Откуда путаница:** «Дополнительные атрибуты в HTML» иногда воспринимаются как «загрязнение».
>
> **Если бы это было правдой:** React Testing Library не рекомендовал бы `data-testid`.
>
> ---
>
> #### D) Создание отдельного Page Object класса для каждой страницы приложения — ❌ Неверно
>
> **Что на самом деле:** Page Object на каждую страницу — это и есть рекомендованный подход. Anti-pattern — наоборот, ОТСУТСТВИЕ Page Object (когда тесты напрямую делают `driver.findElement` в каждом тесте). Или другая крайность — один God Object на весь сайт.
>
> **Откуда путаница:** Сначала может казаться overhead'ом — много классов на маленькое приложение.
>
> **Если бы это было правдой:** Все ведущие туториалы по Selenium не рекомендовали бы Page Object.

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


> [!mcq]
>
> **Вопрос:** Согласно классической Test Pyramid, какое распределение тестов в зрелом проекте считается оптимальным?
>
> ---
>
> #### A) 50% UI / 30% integration / 20% unit — потому что UI ближе всего к user experience, и это главное, что нужно проверять — ❌ Неверно
>
> **Что на самом деле:** Это «ice-cream cone» — известный anti-pattern, противоположный Test Pyramid. Большое количество UI-тестов делает CI медленным (часы вместо минут), flaky, и дорогим в поддержке. UE — не главный critic, но baseline; основная проверка бизнес-логики идёт на нижних слоях.
>
> **Откуда путаница:** Кажется логичным «больше тестов на UE, лучше качество».
>
> **Если бы это было правдой:** Test Pyramid не существовала бы как концепция.
>
> ---
>
> #### B) 60-70% unit / 20-30% integration / 10-15% UI — быстрые и дешёвые тесты — внизу, дорогие и медленные UI — наверху, минимально достаточно для smoke happy paths — ✓ Верно
>
> **Развёрнутое объяснение:** Test Pyramid (Mike Cohn) — фундаментальный принцип организации тестового набора. Снизу — много (тысячи) unit-тестов: они быстрые (1-10 мс), стабильные, проверяют логику изолированно. Посередине — сотни integration-тестов: проверяют взаимодействие компонентов (репозитории + БД, контроллеры + сервисы, HTTP-клиенты через WireMock). Сверху — десятки (максимум 100-200) UI-тестов: проверяют, что happy paths работают end-to-end. Логика проверяется на нижних уровнях, UI — только на критичных user journeys. Это даёт быструю обратную связь (CI прогон за 10 минут), стабильность (мало flaky-тестов), управляемые затраты на maintenance.
>
> **Пример:**
> ```text
>           /\
>          /UI\         10-15% — 50-200 тестов
>         /----\         login, checkout, registration smoke
>        / Int. \       20-30% — 200-500 тестов
>       /--------\       Spring Boot @SpringBootTest, Testcontainers,
>      /   Unit   \      WireMock для HTTP клиентов
>     /------------\    60-70% — 1000-5000 тестов
>                        Mockito, чистые service-методы
>
> // Время прогона:
> Unit:        2 минуты   (5000 × 20 мс параллельно)
> Integration: 5 минут    (300 × 1 сек)
> UI:          10 минут   (100 × 30 сек параллельно на Grid)
> Итого CI:    ~20 минут
> ```
>
> **Когда применять:** Любой средний/крупный проект на Java. Стартап в early-stage — может позволить больше unit, меньше integration (бизнес-логика часто меняется).
>
> **Подводные камни:** Слепое следование пропорциям — не догма. Микросервис с тонкой бизнес-логикой может иметь больше integration-тестов на API-контракты. Frontend-heavy SPA (тонкий backend) — больше UI-тестов имеет смысл, но тогда лучше Playwright/Cypress, чем Selenium.
>
> **Связанные вопросы:** [[Q11]] — альтернативы, [[Q14]] — anti-patterns, [[Q12]] — CI/CD.
>
> ---
>
> #### C) 100% UI-тестов — это покрывает реальное использование пользователями, всё остальное (unit, integration) только мешает — ❌ Неверно
>
> **Что на самом деле:** Это крайняя версия ice-cream cone, известна как «cup cake» антипаттерн. CI прогон — часы, flakyness 30%+, любой UI редизайн ломает всё. Невозможно поддерживать долгосрочно.
>
> **Откуда путаница:** Иногда команды без backend-разработчиков (только QA) приходят к этому естественным путём.
>
> **Если бы это было правдой:** Все известные проекты имели бы 100% UI-тестов, чего на практике нет.
>
> ---
>
> #### D) Только unit-тесты — UI-тесты бесполезны, потому что они flaky и медленные, integration-тесты дублируют unit — ❌ Неверно
>
> **Что на самом деле:** Это «ledge» антипаттерн — отсутствие проверок интеграции. Unit-тесты ловят логические баги в изоляции, но не находят: неправильную JSON-сериализацию, ошибки SQL-запросов, проблемы DI/конфигурации, регрессии в HTTP-контрактах. Integration и UI smoke-тесты обязательны как safety net.
>
> **Откуда путаница:** TDD-фанатизм иногда приводит к «всё mockируем».
>
> **Если бы это было правдой:** Бизнес-критичные баги (типа «оплата проходит, заказ не создаётся») не ловились бы до production.

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
