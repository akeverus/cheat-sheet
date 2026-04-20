---
title: "TestNG"
description: "TestNG — фреймворк для тестирования на JVM (Java и др.): группы тестов, зависимости между методами, параметризация (@DataProvider, @Parameters), конфигурация через XML или аннотации, параллельный запуск, встроенные HTML/XML-отчёты. Часто используется с Selenium для UI-автоматизац"
tags:
  - testing
  - unit-testing
  - testng
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# TestNG

**TestNG** — фреймворк для тестирования на JVM (Java и др.): группы тестов, зависимости между методами, параметризация (`@DataProvider`, `@Parameters`), конфигурация через XML или аннотации, параллельный запуск, встроенные HTML/XML-отчёты. Часто используется с Selenium для UI-автоматизации и в проектах с гибким порядком запуска и отчётами для CI.

**Дата:** 2026-02-06

## Полезные ссылки

- [TestNG — Official Documentation](https://testng.org/doc/documentation-main.html)
- [TestNG — Parameters and DataProviders](https://testng.org/doc/documentation-main.html#parameters)
- [Baeldung — TestNG](https://www.baeldung.com/testng)
- [Allure TestNG](https://docs.qameta.io/allure/#_testng)
- [Unit Testing](../) · [JUnit](../junit/junit.md) · [Mockito](../../../libraries/testing-libraries/java-mockito.md) · [Selenium](../../ui-testing/selenium/selenium.md)

## Содержание

- [Введение](#введение)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Основные аннотации](#основные-аннотации)
  - [@Test](#test)
  - [Конфигурация: @Before* / @After*](#конфигурация-before-after)
- [Группы тестов](#группы-тестов)
- [Зависимости между тестами](#зависимости-между-тестами)
- [Параметры и DataProvider](#параметры-и-dataprovider)
  - [@Parameters (из XML)](#parameters-из-xml)
  - [@DataProvider](#dataprovider)
- [Конфигурация жизненного цикла](#конфигурация-жизненного-цикла)
- [Проверки (Assertions)](#проверки-assertions)
- [Параллельное выполнение](#параллельное-выполнение)
- [XML-сьюты](#xml-сьюты)
- [Отчёты и CI](#отчёты-и-ci)
- [Лучшие практики](#лучшие-практики)
- [Частые вопросы и решение проблем](#частые-вопросы-и-решение-проблем)
  - [Таблица: симптом → причина → действие](#таблица-симптом-причина-действие)
  - [Краткие ответы на частые вопросы](#краткие-ответы-на-частые-вопросы)
- [Глоссарий и таблицы](#глоссарий-и-таблицы)
- [Заключение](#заключение)

## Введение

**TestNG** (Test Next Generation) — фреймворк для тестирования на JVM с акцентом на группы, зависимости, параметризацию и конфигурацию через XML или аннотации. Часто используется с Selenium, в интеграционных и юнит-тестах.

**Зачем TestNG:**

- **Группы** — `@Test(groups = "smoke")`, запуск по группам из suite или командной строки.
- **Зависимости** — `@Test(dependsOnMethods = "login")` для порядка без ручного вызова.
- **Параметры** — `@Parameters` из XML, `@DataProvider` для передачи данных в тесты.
- **Жизненный цикл** — `@BeforeSuite`/`@AfterSuite`, `@BeforeTest`/`@AfterTest`, `@BeforeMethod`/`@AfterMethod`, `@BeforeClass`/`@AfterClass`.
- **Параллельный запуск** — в XML: методы, тесты, классы, сьюты.
- **Отчёты** — встроенные HTML, XML для Jenkins/CI, интеграция с Allure.

**Основные понятия:** тест = метод с `@Test`; suite = набор тестов в XML; группа = категория для выборочного запуска; DataProvider = метод, возвращающий данные для параметризованных тестов.


## Установка и настройка

**Требования:** Java 8+ (JVM), Maven или Gradle.

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.9.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.2</version>
            <configuration>
                <suiteXmlFiles>
                    <suiteXmlFile>testng.xml</suiteXmlFile>
                </suiteXmlFiles>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Gradle

```groovy
dependencies {
    testImplementation 'org.testng:testng:7.9.0'
}

test {
    useTestNG()
    // или: useTestNG { suiteXmlFiles = ['src/test/resources/testng.xml'] }
}
```

**Проверка:** `mvn test` или `./gradlew test`.


## Основные аннотации

### @Test

Метод с аннотацией `@Test` — тестовый метод.

```java
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class CalculatorTest {

    @Test
    public void testAddition() {
        Calculator calc = new Calculator();
        assertEquals(calc.add(2, 3), 5);
    }

    @Test(description = "Проверка вычитания")
    public void testSubtraction() {
        assertEquals(new Calculator().subtract(5, 3), 2);
    }
}
```

**Частые атрибуты @Test:**

| Атрибут | Описание | Пример |
|---------|----------|--------|
| `groups` | Группы теста | `groups = {"smoke", "regression"}` |
| `dependsOnMethods` | Зависимость от методов | `dependsOnMethods = {"login"}` |
| `dependsOnGroups` | Зависимость от групп | `dependsOnGroups = {"init"}` |
| `enabled` | Включён ли тест | `enabled = false` |
| `priority` | Порядок (меньше — раньше) | `priority = 1` |
| `timeOut` | Таймаут, мс | `timeOut = 5000` |
| `expectedExceptions` | Ожидаемое исключение | `expectedExceptions = IllegalArgumentException.class` |
| `dataProvider` | Имя DataProvider | `dataProvider = "users"` |
| `alwaysRun` | Выполнять при падении зависимостей | `alwaysRun = true` |

### Конфигурация: @Before* / @After*

- **@BeforeMethod / @AfterMethod** — до и после каждого тестового метода.
- **@BeforeClass / @AfterClass** — один раз до/после всех методов в классе (удобно для подключения к БД).
- **@BeforeSuite / @AfterSuite** — один раз в начале/конце сьюта.
- **@BeforeTest / @AfterTest** — в контексте XML `<test>`: один раз до/после всех классов внутри одного `<test>`.
- **@BeforeGroups / @AfterGroups** — до первой и после последней группы с заданным именем.

Пример жизненного цикла:

```java
public class LifecycleTest {

    @BeforeMethod
    public void setUp() {
        System.out.println("Перед каждым тестом");
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("После каждого теста");
    }

    @Test
    public void test1() {
        // тест
    }
}
```


## Группы тестов

Группы позволяют помечать тесты и запускать только выбранные категории.

```java
@Test(groups = "smoke")
public void smokeTest() {
    assertTrue(true);
}

@Test(groups = {"regression", "ui"})
public void regressionUiTest() {
    // тест
}
```

**Запуск по группам:**

- В **testng.xml**: в `<test>` задать `<groups><run><include name="smoke"/></run></groups>`.
- Maven: `mvn test -Dgroups=smoke,regression`.


## Зависимости между тестами

Порядок выполнения задаётся через зависимости.

```java
@Test
public void login() {
    // логин
}

@Test(dependsOnMethods = "login")
public void createOrder() {
    // создание заказа после логина
}

@Test(dependsOnMethods = {"login", "createOrder"})
public void checkout() {
    // оформление заказа
}
```

Если `login()` падает, зависимые тесты помечаются как skipped. Чтобы зависимый тест выполнялся всегда — `alwaysRun = true`. Зависимость от групп: `dependsOnGroups = "init"`.


## Параметры и DataProvider

### @Parameters (из XML)

В **testng.xml** задать `<parameter name="browser" value="chrome"/>`, в тесте принять:

```java
@Test
@Parameters({"browser", "baseUrl"})
public void openPage(String browser, String baseUrl) {
    // driver.get(baseUrl); ...
}
```

### @DataProvider

Метод возвращает набор данных; тест вызывается для каждой строки.

```java
@DataProvider(name = "users")
public Object[][] users() {
    return new Object[][]{
        {"user1", "pass1"},
        {"user2", "pass2"}
    };
}

@Test(dataProvider = "users")
public void loginTest(String user, String pass) {
    login(user, pass);
    assertTrue(isLoggedIn());
}
```

DataProvider может быть в другом классе: `dataProviderClass = DataProviders.class`. Параметры из XML приходят строками — при необходимости конвертировать в коде (например, `Integer.parseInt`).


## Конфигурация жизненного цикла

Порядок по уровню (сверху вниз):

| Уровень | Аннотации |
|---------|-----------|
| Suite | `@BeforeSuite` → … → `@AfterSuite` |
| Test (XML `<test>`) | `@BeforeTest` → … → `@AfterTest` |
| Class | `@BeforeClass` → … → `@AfterClass` |
| Group | `@BeforeGroups` → … → `@AfterGroups` |
| Method | `@BeforeMethod` → … → `@AfterMethod` |

Внутри одного уровня порядок между классами/методами не гарантируется без зависимостей или `priority`.


## Проверки (Assertions)

Класс `org.testng.Assert`: жёсткие проверки (при падении тест останавливается) и мягкие — **SoftAssert** (накапливают ошибки, выброс в конце).

**Жёсткие проверки:**

```java
import static org.testng.Assert.*;

@Test
public void assertions() {
    assertEquals(actual, expected, "Сообщение при падении");
    assertTrue(condition);
    assertNull(obj);
    assertNotNull(obj);
}
```

**Мягкие проверки:**

```java
@Test
public void softAssertions() {
    SoftAssert sa = new SoftAssert();
    sa.assertEquals(1, 2, "first");
    sa.assertTrue(false, "second");
    sa.assertAll(); // выбросит все накопленные ошибки
}
```

**Ожидаемое исключение:**

```java
@Test(expectedExceptions = IllegalArgumentException.class)
public void expectException() {
    service.validate(null);
}
```


## Параллельное выполнение

В **testng.xml** в корне сьюта:

```xml
<suite name="Suite" parallel="methods" thread-count="4">
    <test name="Tests">
        <classes>
            <class name="com.example.ParallelTests"/>
        </classes>
    </test>
</suite>
```

Значения `parallel`: `methods`, `tests`, `classes`, `instances`. Для одного класса в одном потоке: `@Test(singleThreaded = true)` на классе.


## XML-сьюты

Пример **testng.xml**:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Full Suite" verbose="1">
    <parameter name="env" value="test"/>
    <listeners>
        <listener class-name="com.example.MyListener"/>
    </listeners>
    <test name="Smoke" preserve-order="true">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <classes>
            <class name="com.example.SmokeTests"/>
        </classes>
    </test>
    <test name="Regression">
        <classes>
            <class name="com.example.RegressionTests"/>
        </classes>
    </test>
</suite>
```

Атрибуты `<suite>`: `parallel`, `thread-count`, `configfailurepolicy` (skip/continue при падении конфигурации). Атрибут `<test>`: `preserve-order` — сохранять порядок классов.


## Отчёты и CI

- **Встроенные отчёты:** в каталоге `test-output/` — HTML (`index.html`, `emailable-report.html`) и XML (`testng-results.xml`) для CI.
- **Listeners:** реализация `ITestListener`, `IReporter` для своей логики (например, при падении). Регистрация: `<listeners><listener class-name="com.example.MyListener"/></listeners>` или `@Listeners(MyListener.class)` на классе.
- **Allure:** зависимость allure-testng, слушатель в XML или через `@Listeners`; отчёт: `allure generate test-output/allure-results -o allure-report`.
- **CI:** Maven Surefire — `suiteXmlFile`, при необходимости `-Dgroups=smoke`. Jenkins — плагин JUnit (парсит testng-results.xml) или Allure. Gradle — `useTestNG()`, при необходимости `systemProperty 'groups', 'smoke'` в блоке `test`.


## Лучшие практики

1. **Группы** — разделяйте smoke/regression/integration, запускайте выборочно.
2. **Зависимости** — не злоупотребляйте; предпочитайте независимые тесты.
3. **DataProvider** — тяжёлые данные выносите в отдельный класс или внешний источник.
4. **Один assertion на тест** — где возможно, для ясности.
5. **Конфигурация** — тяжёлые ресурсы в `@BeforeClass`/`@BeforeSuite`, лёгкую подготовку в `@BeforeMethod`.
6. **Параллельность** — избегайте общего изменяемого состояния; при необходимости `singleThreaded` или потокобезопасные фикстуры.
7. **Имена** — осмысленные имена методов и групп для отчётов.
8. **Listeners** — для скриншотов при падении, логирования, интеграции с Allure.
9. **Пропуск по условию** — вместо `enabled = false` можно в начале метода вызвать `throw new SkipException("причина")`.
10. **Параметры из Maven** — передавать `-DparamName=value`; в XML (TestNG 7+) можно использовать плейсхолдеры или читать в коде `System.getProperty("paramName")`.


## Частые вопросы и решение проблем

### Таблица: симптом → причина → действие

| Симптом | Возможная причина | Действие |
|--------|--------------------|----------|
| Тесты не запускаются | Нет TestNG в classpath или неверный suite | Проверить зависимость Maven/Gradle и путь к testng.xml |
| Зависимый тест пропущен | Упала зависимость | Проверить порядок и логику; при необходимости `alwaysRun = true` |
| Параметры null | Имя в XML не совпадает с `@Parameters` | Сверить имена в XML и аннотации |
| DataProvider не вызывается | Неверное имя или сигнатура | Имя в `dataProvider` должно совпадать; метод возвращает `Object[][]` или `Iterator<Object[]>` |
| Параллельные тесты падают | Общее состояние между тестами | Убрать общее состояние или использовать `singleThreaded` / потокобезопасные фикстуры |
| Конфигурация упала — все тесты skipped | При падении @Before* тесты в контексте пропускаются | Исправить конфигурацию; в XML можно `configfailurepolicy="continue"` |
| Группы не находятся | Группа не в `<run><include>` или опечатка | Проверить имена в XML и `-Dgroups` |
| Тест падает по таймауту | Долгое выполнение или зависание | Увеличить `timeOut` или оптимизировать тест |
| Слушатель не срабатывает | Не зарегистрирован или не в classpath | Проверить `<listener class-name="..."/>` или `@Listeners` на классе |
| Конфликт с JUnit | Оба фреймворка в classpath | Исключить JUnit из тестовой конфигурации или отдельные модули |

### Краткие ответы на частые вопросы

- **Чем TestNG отличается от JUnit?** Группы, зависимости, богатый жизненный цикл, параметры из XML, встроенные HTML-отчёты. JUnit 5 проще, расширяется через Extensions.
- **Когда использовать dependsOnMethods?** Когда тест логически должен выполняться только после другого (например, логин перед UI). Не злоупотреблять — длинные цепочки усложняют отладку.
- **Как передать параметры из Maven в TestNG?** В testng.xml использовать плейсхолдеры (если поддерживается) или в коде: `System.getProperty("paramName", "default")`.
- **Можно ли запускать TestNG без XML?** Да: Maven Surefire и Gradle находят классы с `@Test` по соглашению. XML нужен для групп, параметров, порядка, параллельности.
- **Как интегрировать с Selenium?** WebDriver создавать в `@BeforeMethod` или `@BeforeClass`, хранить в поле; в `@AfterMethod`/`@AfterClass` вызывать `quit()`. Для параллелизма — свой экземпляр на поток.
- **Как пропустить тест программно?** `throw new org.testng.SkipException("причина")` в начале метода.
- **TestNG с Kotlin/Groovy?** Да; те же аннотации и API, классы на Kotlin/Groovy.
- **Порядок классов в XML?** `<test preserve-order="true">` сохраняет порядок элементов `<class>`.
- **Что такое @Factory?** Создаёт экземпляры тестового класса (например, с разными параметрами); каждый экземпляр — отдельный тест. Для динамической генерации набора тестов.
- **SoftAssert vs Assert?** SoftAssert накапливает ошибки и не прерывает тест сразу; в конце вызывается `assertAll()`, тогда выбрасывается исключение со всеми ошибками.
- **Повтор упавших тестов?** Встроенного retry нет; можно реализовать через `IRetryAnalyzer` и `@Test(retryAnalyzer = MyRetryAnalyzer.class)`.
- **Исключить группу из запуска?** В XML в `<groups><run>` использовать `<exclude name="slow"/>`. Maven: `-DexcludedGroups=slow` (если плагин поддерживает).


## Глоссарий и таблицы

**Глоссарий:**

| Термин | Описание |
|--------|----------|
| Suite | Набор тестов, описанный в XML или аннотациями |
| Test (XML) | Элемент `<test>` в testng.xml; группа классов с общими параметрами/группами |
| Group | Категория тестов для выборочного запуска |
| DataProvider | Метод, поставляющий данные для параметризованных тестов |
| Configuration method | Метод с аннотацией @Before* или @After* |
| Listener | Реализация интерфейса TestNG (например, ITestListener) для перехвата событий |
| SoftAssert | Накопление проверок с выбросом всех ошибок в конце |

**Аннотации конфигурации:**

| Аннотация | Когда выполняется |
|-----------|-------------------|
| @BeforeSuite / @AfterSuite | Один раз до/после всего сьюта |
| @BeforeTest / @AfterTest | Один раз до/после всех классов в `<test>` |
| @BeforeClass / @AfterClass | Один раз до/после методов класса |
| @BeforeGroups / @AfterGroups | До первой и после последней группы |
| @BeforeMethod / @AfterMethod | До/после каждого тестового метода |

**Параллельность (XML):**

| Значение `parallel` | Уровень |
|--------------------|---------|
| `methods` | Методы в классе параллельно |
| `tests` | Элементы `<test>` параллельно |
| `classes` | Классы параллельно |
| `instances` | Экземпляры теста параллельно |


## Заключение

**TestNG** — мощный фреймворк для тестирования на JVM с группами, зависимостями, параметризацией и гибкой конфигурацией через XML. Используйте группы для smoke/regression, DataProvider для параметризованных сценариев, конфигурационные методы для подготовки и очистки. Документация: [TestNG — Official Documentation](https://testng.org/doc/documentation-main.html); см. также [Unit Testing](../), [JUnit](../junit/junit.md).
