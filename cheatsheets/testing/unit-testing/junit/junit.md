---
title: "JUnit 5"
description: "JUnit 5 — основной фреймворк для тестирования Java-приложений. Удобно описывать тесты аннотациями, проверять результат через утверждения (assertions), запускать параметризованные сценарии и управлять жизненным циклом и расширениями."
tags:
  - testing
  - unit-testing
  - junit
type: "reference"
difficulty: "intermediate"
aliases:
  - "JUnit 5"
  - "junit"
prerequisites: []
next:
  - "[[mockito]]"
  - "[[testcontainers]]"
updated: "2026-04-20"
---
# JUnit 5

**JUnit** 5 — основной фреймворк для тестирования Java-приложений. Удобно описывать тесты аннотациями, проверять результат через утверждения (assertions), запускать параметризованные сценарии и управлять жизненным циклом и расширениями.

## Полезные ссылки

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [JUnit 5 API](https://junit.org/junit5/docs/current/api/)
- [Baeldung — JUnit 5](https://www.baeldung.com/junit-5)
- [mockito](mockito.md) · [assertj](assertj.md) · [testcontainers](../../integration-testing/testcontainers/testcontainers.md)

## Содержание

- [Введение в JUnit 5](#введение-в-junit-5)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Основные аннотации](#основные-аннотации)
  - [Пример: жизненный цикл и один тест](#пример-жизненный-цикл-и-один-тест)
  - [@BeforeAll / @AfterAll](#beforeall-afterall)
- [Assertions (проверки)](#assertions-проверки)
  - [Пример: базовые проверки и исключения](#пример-базовые-проверки-и-исключения)
- [Assumptions (допущения)](#assumptions-допущения)
- [Параметризованные тесты](#параметризованные-тесты)
  - [Примеры](#примеры)
- [Тестирование исключений](#тестирование-исключений)
- [Группировка и порядок](#группировка-и-порядок)
  - [@Nested](#nested)
  - [@TestMethodOrder](#testmethodorder)
- [Расширения (Extensions)](#расширения-extensions)
- [Динамические тесты (@TestFactory)](#динамические-тесты-testfactory)
- [Дополнительные возможности](#дополнительные-возможности)
- [Миграция с JUnit 4](#миграция-с-junit-4)
- [Лучшие практики](#лучшие-практики)
- [Шпаргалка команд](#шпаргалка-команд)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [См. также](#см-также)
- [Заключение](#заключение)

## Введение в JUnit 5

JUnit 5 состоит из трёх частей:

| Часть | Назначение |
|-------|------------|
| **JUnit Platform** | Запуск тестов, TestEngine API, Launcher, консольный запуск |
| **JUnit Jupiter** | Модель тестов, расширения, реализация TestEngine |
| **JUnit Vintage** | Запуск тестов JUnit 3/4 |

Поддержка Java 8+, удобные проверки и параметризация.


## Установка и настройка

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.2</version>
        </plugin>
    </plugins>
</build>
```

### Gradle

```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
}
test {
    useJUnitPlatform()
}
```


## Основные аннотации

| Аннотация | Описание |
|-----------|----------|
| **@Test** | Метод — тест |
| **@BeforeEach** | Перед каждым тестом |
| **@AfterEach** | После каждого теста |
| **@BeforeAll** | Один раз перед всеми тестами (static или PER_CLASS) |
| **@AfterAll** | Один раз после всех тестов (static или PER_CLASS) |
| **@DisplayName** | Отображаемое имя теста/класса |
| **@Disabled** | Отключить тест |
| **@Tag** | Тег для фильтрации (unit, integration, slow) |

### Пример: жизненный цикл и один тест

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Сложение двух чисел")
    void testAddition() {
        assertEquals(5, calculator.add(2, 3));
    }

    @Test
    @Disabled("Временно отключен")
    void disabledTest() { }

    @Test
    @Tag("fast")
    void fastTest() {
        assertTrue(calculator.add(0, 0) == 0);
    }
}
```

### @BeforeAll / @AfterAll

Статические методы (или класс с `@TestInstance(Lifecycle.PER_CLASS)`):

```java
@BeforeAll
static void setUpDatabase() {
    connection = DatabaseConnection.create();
}

@AfterAll
static void closeDatabase() {
    connection.close();
}
```


## Assertions (проверки)

Используется класс `Assertions`. Сообщение об ошибке — последний аргумент (или лямбда для ленивой инициализации).

| Метод | Назначение |
|-------|------------|
| `assertEquals(expected, actual)` | Равенство |
| `assertTrue` / `assertFalse` | Булево условие |
| `assertNull` / `assertNotNull` | Проверка на null |
| `assertArrayEquals` | Массивы |
| `assertThrows(Класс, исполняемый)` | Ожидаемое исключение |
| `assertDoesNotThrow` | Исключения не должно быть |
| `assertAll(описание, исполняемые...)` | Несколько проверок (все выполняются) |

### Пример: базовые проверки и исключения

```java
@Test
void basicAssertions() {
    assertEquals(4, 2 + 2, "2+2 должно быть 4");
    assertTrue(5 > 3);
    assertNull(null);
    assertNotNull("Hello");
}

@Test
void exceptionAssertions() {
    var ex = assertThrows(IllegalArgumentException.class,
        () -> { throw new IllegalArgumentException("error"); });
    assertEquals("error", ex.getMessage());
}

@Test
void multipleAssertions() {
    var person = new Person("John", "Doe", 30);
    assertAll("person",
        () -> assertEquals("John", person.getFirstName()),
        () -> assertEquals("Doe", person.getLastName()),
        () -> assertEquals(30, person.getAge()));
}
```


## Assumptions (допущения)

При невыполнении условия тест пропускается (SKIPPED).

```java
import static org.junit.jupiter.api.Assumptions.*;

@Test
void testOnlyOnWindows() {
    assumeTrue(System.getProperty("os.name").contains("Windows"));
    // тест только на Windows
}

@Test
void testWithAssumption() {
    assumingThat("prod".equals(System.getenv("ENV")), () -> {
        // блок только при ENV=prod
    });
}
```

Условное выполнение по ОС/переменным: **@EnabledOnOs**, **@DisabledOnOs**, **@EnabledIfEnvironmentVariable**, **@DisabledIfEnvironmentVariable**.


## Параметризованные тесты

Аннотация **@ParameterizedTest** с источниками данных.

| Источник | Назначение |
|----------|------------|
| **@ValueSource** | Один аргумент: числа, строки и т.д. |
| **@CsvSource** | Несколько аргументов из CSV-строки |
| **@CsvFileSource** | CSV из файла в `src/test/resources` |
| **@MethodSource** | `Stream<Arguments>` или метод |
| **@NullSource** / **@EmptySource** | null и пустая строка/коллекция |

### Примеры

```java
@ParameterizedTest
@ValueSource(ints = {1, 3, 5, 7})
void testOddNumbers(int number) {
    assertTrue(number % 2 != 0);
}

@ParameterizedTest
@CsvSource({"2, 3, 5", "10, 20, 30"})
void testAddition(int a, int b, int expected) {
    assertEquals(expected, Calculator.add(a, b));
}

@ParameterizedTest
@MethodSource("provideTestData")
void testWithMethodSource(int a, int b, int expected) {
    assertEquals(expected, Calculator.add(a, b));
}
static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of(1, 2, 3),
        Arguments.of(5, 5, 10));
}
```


## Тестирование исключений

```java
@Test
void testException() {
    assertThrows(IllegalArgumentException.class, () -> Calculator.divide(10, 0));
}

@Test
void testNoException() {
    assertDoesNotThrow(() -> Calculator.add(2, 3));
}
```


## Группировка и порядок

### @Nested

Вложенные классы для логической группировки:

```java
class UserServiceTest {
    @Nested
    class CreateUserTests {
        @Test
        void testCreateValidUser() { }
    }
    @Nested
    class UpdateUserTests {
        @Test
        void testUpdateExistingUser() { }
    }
}
```

### @TestMethodOrder

- `MethodOrderer.OrderAnnotation.class` — по **@Order**
- `MethodOrderer.MethodName.class` — по имени метода
- `MethodOrderer.Random.class` — случайный порядок

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderedTest {
    @Test
    @Order(1)
    void testA() { }
    @Test
    @Order(2)
    void testB() { }
}
```


## Расширения (Extensions)

Регистрация: **@ExtendWith** на классе или глобально через `junit-platform.properties`.

| Расширение | Назначение |
|------------|------------|
| **MockitoExtension** | Моки: **@Mock**, **@InjectMocks** |
| **SpringExtension** | Контекст Spring для интеграционных тестов |

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @Test
    void test() { /* тест с моками */ }
}
```


## Динамические тесты (@TestFactory)

Генерация тестов во время выполнения:

```java
@TestFactory
Stream<DynamicTest> dynamicTests() {
    return Stream.of(
        DynamicTest.dynamicTest("Тест 1", () -> assertEquals(2, 1 + 1)),
        DynamicTest.dynamicTest("Тест 2", () -> assertEquals(4, 2 * 2)));
}
```


## Дополнительные возможности

| Возможность | Аннотация / API |
|-------------|------------------|
| Повтор теста n раз | **@RepeatedTest(n)** |
| Временная директория | **@TempDir Path tempDir** (автоудаление после теста) |
| Таймаут | **@Timeout(5)** или **@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)** |
| Условное выполнение | **@DisabledIf** / **@EnabledIf** (выражение или метод) |

Пример **@TempDir**:

```java
@Test
void testWithTempDir(@TempDir Path tempDir) throws IOException {
    var file = tempDir.resolve("test.txt");
    Files.writeString(file, "content");
    assertEquals("content", Files.readString(file));
}
```

Параллельный запуск — в `junit-platform.properties`:

```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.config.strategy=fixed
junit.jupiter.execution.parallel.config.fixed.parallelism=4
```


## Миграция с JUnit 4

| JUnit 4 | JUnit 5 |
|---------|---------|
| `@Before` | **@BeforeEach** |
| `@After` | **@AfterEach** |
| `@BeforeClass` | **@BeforeAll** (static или PER_CLASS) |
| `@AfterClass` | **@AfterAll** |
| `@Ignore` | **@Disabled** |
| `Assert.assertEquals` | `Assertions.assertEquals` |
| **@Rule** / **@ClassRule** | **Extensions** (@ExtendWith) |

Для запуска старых тестов JUnit 4 подключите **junit-vintage-engine**.


## Лучшие практики

- Использовать **@DisplayName** или понятные имена методов.
- Одна проверка на тест (где возможно).
- Параметризованные тесты для множества сценариев.
- Группировать связанные тесты через **@Nested**.
- Использовать assumptions для условного выполнения.
- Тестировать поведение, а не реализацию.
- Тесты независимы: изоляция через **@BeforeEach** и при необходимости **@Execution(SAME_THREAD)**.


## Шпаргалка команд

| Задача | Команда / аннотация |
|--------|----------------------|
| Запуск тестов | `./gradlew test` или `mvn test` |
| Класс | `./gradlew test --tests "com.example.OrderServiceTest"` |
| По тегам | В Gradle/Maven: `includeTags` / `excludeTags` |
| Параметризованный тест | **@ParameterizedTest** + **@ValueSource** / **@CsvSource** / **@MethodSource** |
| Жизненный цикл | **@BeforeAll**, **@AfterAll**, **@BeforeEach**, **@AfterEach** |
| Отключить тест | **@Disabled** или **@DisabledIf** |
| Временная директория | **@TempDir Path tempDir** |
| Таймаут | **@Timeout(5)** или **@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)** |


## Частые вопросы

**Как запускать только быстрые тесты?**
Теги **@Tag("fast")** и настройка Gradle/Maven: `includeTags "unit"`, `excludeTags "slow"`.

**Как тестировать асинхронный код?**
AssertJ: `assertThat(future).succeedsWithin(Duration.ofSeconds(5))` или Awaitility.

**Как организовать тестовые данные?**
**@ParameterizedTest** с **@CsvFileSource** или **@MethodSource**; общие данные — в **@BeforeEach** или фабриках.

**Миграция с JUnit 4 на JUnit 5?**
См. таблицу «Миграция с JUnit 4»; при необходимости подключите **junit-vintage-engine**.

**Тесты проходят локально, но падают в CI.**
Проверить: таймзоны, локаль, пути к файлам, переменные окружения. В CI — стабильное окружение; **@EnabledIf** только для локальных сценариев.

**Тесты нестабильны.**
Избегать общего состояния между тестами, таймеров без моков, зависимости от порядка. Изоляция: **@BeforeEach** с новыми экземплярами, при необходимости **@Execution(SAME_THREAD)**.

**@BeforeAll падает с NullPointerException.**
Метод **@BeforeAll** должен быть **static** (или класс с **@TestInstance(Lifecycle.PER_CLASS)**). В static-методе нельзя использовать нестатические поля.

**Параметризованный тест не находит данные.**
CSV: путь вида `resources = "/data.csv"` — файл в `src/test/resources`. **@MethodSource**: метод **static**, возвращает **Stream**, **Iterable** или **Arguments**.

**Запуск тестов одного класса:**
`./gradlew test --tests "com.example.OrderServiceTest"` или `mvn test -Dtest=OrderServiceTest`.


## Глоссарий

- **JUnit Platform** — основа для запуска тестовых фреймворков.
- **JUnit Jupiter** — модель программирования и расширения для написания тестов.
- **JUnit Vintage** — движок для тестов JUnit 3/4.
- **Extension** — расширение жизненного цикла (MockitoExtension, SpringExtension и др.).
- **Parameterized Test** — тест с несколькими наборами входных данных.
- **Dynamic Test** — тест, сгенерированный во время выполнения (@TestFactory).
- **Assertion** — проверка ожидаемого результата.
- **Assumption** — условие, при невыполнении которого тест пропускается.


## См. также

- [JUnit Advanced](junit-advanced.md) · [mockito](mockito.md) · [assertj](assertj.md) · [hamcrest](hamcrest.md) · [Обзор инструментов тестирования](../../testing-tools/testing-tools-overview.md)


## Заключение

JUnit 5 — стандарт для unit-тестов на Java: удобный жизненный цикл, параметризация, расширения и интеграция с Mockito и Spring. Для углубления используйте официальный User Guide и ссылки выше.
