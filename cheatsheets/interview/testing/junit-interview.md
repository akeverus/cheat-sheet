---
title: "Вопросы на собеседовании: JUnit 5"
description: "JUnit 5 (Jupiter): аннотации, жизненный цикл PER_CLASS/PER_METHOD, параметризованные и динамические тесты, @Nested, Extensions, интеграция с Mockito и Spring"
tags:
  - interview
  - testing
  - junit-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "JUnit 5"
  - "JUnit 5 interview"
  - "JUnit собеседование"
prerequisites:
  - "[[junit]]"
next: []
updated: "2026-11-05"
---
# Вопросы на собеседовании: `JUnit 5`

## Содержание

- [Q1. Что такое JUnit 5 и как он структурирован?](#q1)
- [Q2. Какие основные аннотации в JUnit 5?](#q2)
- [Q3. Как использовать параметризованные тесты?](#q3)
- [Q4. Что такое assertAll и когда его использовать?](#q4)
- [Q5. Как проверять исключения?](#q5)
- [Q6. Что такое @Nested и зачем оно нужно?](#q6)
- [Q7. Как JUnit 5 интегрируется с Mockito?](#q7)
- [Q8. Что такое Dynamic Tests?](#q8)
- [Q9. Что такое JUnit Extensions?](#q9)
- [Q10. Что такое Conditional Test Execution?](#q10)
- [Q11. Как работает AssertJ и чем он лучше встроенных assertions JUnit?](#q11)
- [Q12. Как параллельно выполнять тесты?](#q12)
- [Q13. Как Spring Boot интегрируется с JUnit 5?](#q13)
- [Q14. Что такое Test Lifecycle и PER_CLASS vs PER_METHOD?](#q14)
- [Q15. Какие типичные ошибки в юнит-тестах?](#q15)

<a id="q1"></a>
## Q1. Что такое JUnit 5 и как он структурирован?

**JUnit 5** — это не один артефакт, а зонтик из трёх независимых sub-проектов. Такое разделение позволяет, например, IDE и сборщикам работать с тестами через стабильный Platform API, не завязываясь на конкретный API написания тестов.

- **JUnit Platform** — фундамент для запуска тестов: определяет `TestEngine` SPI, через который IDE и Maven/Gradle находят и запускают тесты. Сам тесты не пишет.
- **JUnit Jupiter** — новый API + движок для написания тестов (`@Test`, `@BeforeEach`, `assertThrows` и др.). Это то, что в обиходе и называют «JUnit 5».
- **JUnit Vintage** — движок-мост, позволяющий запускать старые тесты JUnit 3/4 на той же Platform. Нужен на время миграции.

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>${junit.version}</version>
    <scope>test</scope>
</dependency>
```

Для показанного XML задайте собственное свойство `junit.version`. Если версия артефакта управляется импортированным BOM, явный элемент `<version>` обычно убирают: сам импорт BOM не обязан определять свойство с таким именем.

**Главные отличия от JUnit 4** (часто спрашивают на собеседовании):
- Аннотации жизненного цикла переименованы яснее: `@Before` → `@BeforeEach`, `@BeforeClass` → `@BeforeAll`.
- `@Ignore` → `@Disabled`, а громоздкий `@RunWith` заменён на композируемый `@ExtendWith` — расширений можно навесить несколько, в JUnit 4 раннер был один.
- Требует Java 8+, что позволяет передавать ассерты как lambda (`assertThrows(..., () -> ...)`) и работать со streams в `@MethodSource`. Следующая мажорная линия, JUnit 6, поднимает планку до Java 17 — на 5.x это не влияет.
- Выразительнее API ассертов: `assertThrows` возвращает само исключение для проверки сообщения, `assertAll` группирует проверки.

<a id="q2"></a>
## Q2. Какие основные аннотации в JUnit 5?

Базовый набор делится на три группы: **жизненный цикл** (когда выполнять код), **сам тест** (что считать тестом) и **управление выполнением** (пропуск, повтор, таймаут).

- `@Test` — помечает метод как тест. `@DisplayName` задаёт читаемое имя в отчёте вместо имени метода.
- `@BeforeAll` / `@AfterAll` — выполняются один раз на класс (по умолчанию должны быть `static`).
- `@BeforeEach` / `@AfterEach` — выполняются перед/после **каждого** теста; сюда выносят повторяющийся setup.
- `@Disabled` — временно отключает тест; в скобках указывают причину (видно в отчёте).
- `@Timeout` — валит тест, если он не уложился в срок.
- `@RepeatedTest(n)` — запускает один тест n раз (полезно для нестабильных сценариев); `RepetitionInfo` даёт номер повтора.
- `@Nested` — группирует связанные тесты во вложенный класс (см. Q6).

```java
class OrderServiceTest {

    @BeforeAll
    static void initClass() {
        // один раз перед всеми тестами класса (static!)
    }

    @BeforeEach
    void setUp() {
        // перед каждым тестом
    }

    @Test
    @DisplayName("Заказ должен создаваться при валидных данных")
    void shouldCreateOrder() {
        // тест
    }

    @Test
    @Disabled("Нужна реализация в ticket-123")
    void shouldCancelOrder() { }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void shouldCompleteInFiveSeconds() { }

    @RepeatedTest(10)
    void shouldWorkRepeatedly(RepetitionInfo info) {
        log.info("Repetition {} of {}", info.getCurrentRepetition(), info.getTotalRepetitions());
    }

    @Nested
    class OrderValidationTests {
        @Test void shouldRejectNegativeAmount() { }
    }

    @AfterEach
    void tearDown() { }

    @AfterAll
    static void cleanupClass() { }
}
```

<a id="q3"></a>
## Q3. Как использовать параметризованные тесты?

Параметризованный тест прогоняет **один** метод на наборе входных данных — вместо копипасты пяти почти одинаковых `@Test`. Метод помечают `@ParameterizedTest` (вместо `@Test`) и добавляют источник данных. Каждый набор аргументов — отдельный прогон в отчёте.

Какой источник выбрать:
- **`@ValueSource`** — один аргумент данных на вызов: поддерживаемый примитивный литерал, `String` или `Class`. Дополнительный служебный параметр вроде `TestInfo` может поступать через `ParameterResolver`, после аргументов источника.
- **`@CsvSource`** — несколько параметров в строке через запятую (прямо в коде).
- **`@CsvFileSource`** — те же CSV, но из ресурса; удобно для больших таблиц данных.
- **`@EnumSource`** — без фильтров перебирает все константы enum, включая добавленные. `names`/`mode` ограничивают набор, поэтому при фильтрации охват новых констант нужно проверять отдельно.
- **`@MethodSource`** — метод вычисляет аргументы во время запуска; `Stream<Arguments>` — частая, но не единственная форма. Поддерживаются также подходящие коллекции, массивы, `Iterable`, `Iterator` и примитивные streams. Источник внутри класса обычно `static`; при `PER_CLASS` допустим нестатический, внешний источник всегда `static`.

```java
class ValidationTest {

    // @ValueSource — простые значения
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t"})
    void shouldRejectBlankStrings(String input) {
        assertThat(isBlank(input)).isTrue();
    }

    // @CsvSource — несколько параметров
    @ParameterizedTest
    @CsvSource({
        "'hello', 5",
        "'world', 5",
        "'java', 4"
    })
    void shouldComputeLength(String input, int expected) {
        assertThat(input.length()).isEqualTo(expected);
    }

    // @CsvFileSource — из файла
    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    void fromCsvFile(String name, int age) { }

    // @EnumSource — все значения enum
    @ParameterizedTest
    @EnumSource(OrderStatus.class)
    void shouldHandleAllStatuses(OrderStatus status) { }

    // @MethodSource — сложные объекты через метод
    @ParameterizedTest
    @MethodSource("orderProvider")
    void shouldProcessOrders(Order order, boolean expectedValid) {
        assertThat(validator.isValid(order)).isEqualTo(expectedValid);
    }

    static Stream<Arguments> orderProvider() {
        return Stream.of(
            Arguments.of(new Order("u-1", TEN), true),
            Arguments.of(new Order("u-1", ZERO), false),
            Arguments.of(new Order("", TEN), false)
        );
    }
}
```

<a id="q4"></a>
## Q4. Что такое assertAll и когда его использовать?

`assertAll` выполняет переданные `Executable`, собирая обычные исключения и assertion failures в `MultipleFailuresError`: падение одной лямбды не отменяет соседние. Невосстановимая ошибка, например `OutOfMemoryError`, прерывает выполнение сразу. Обычные ассерты работают по принципу fail-fast: первый же `assertEquals` бросает исключение, и про остальные проверки вы не узнаете, пока не почините первую и не перезапустите тест.

```java
@Test
void shouldCreateUser() {
    User user = userService.create("alice", "alice@example.com", 25);

    // С assertAll — независимые проверки выполняются и после обычного failure
    assertAll("user creation",
        () -> assertEquals("alice", user.getName()),
        () -> assertEquals("alice@example.com", user.getEmail()),
        () -> assertEquals(25, user.getAge()),
        () -> assertNotNull(user.getId()),
        () -> assertTrue(user.isActive())
    );
    // Если 2 ассерта упали — увидим оба в выводе
}
```

**Сценарий применения:** проверка нескольких независимых полей одного объекта, когда хочется за один прогон увидеть все расхождения.

**Подводный камень:** не оборачивайте в `assertAll` зависимые проверки. Если результат второй проверки имеет смысл только при пройденной первой (например, сначала `assertNotNull(list)`, потом `list.get(0)`), то на упавшем `null` вторая проверка кинет `NullPointerException`. Оставьте их последовательными либо поместите guard и зависимые шаги в одну лямбду: `() -> { assertNotNull(list); assertFalse(list.isEmpty()); assertEquals("Alice", list.get(0)); }`. Внутри этой лямбды первый failure остановит дальнейшие шаги, а другие независимые лямбды группы продолжатся.

<a id="q5"></a>
## Q5. Как проверять исключения?

В JUnit 5 исключение проверяют через `assertThrows`: он принимает ожидаемый тип и lambda с проверяемым кодом, **возвращает само исключение** и валит тест, если оно не выброшено. Это сразу решает две проблемы старого стиля `@Test(expected=...)`: можно проверить сообщение/поля исключения и точно знать, что упала именно нужная строка, а не случайный код в начале метода.

- **`assertThrows`** — выброшено исключение указанного типа **или его подкласса**.
- **`assertThrowsExactly`** — строго указанный тип; подкласс не пройдёт.
- **`assertDoesNotThrow`** — код отработал без исключений (полезно для регрессионных проверок).

```java
// ПЛОХО (JUnit 4 стиль)
@Test(expected = IllegalArgumentException.class)
void shouldThrow() { service.doSomething(null); }

// ХОРОШО (JUnit 5)
@Test
void shouldThrowOnNull() {
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> service.doSomething(null)
    );
    assertEquals("Input cannot be null", ex.getMessage());
}

// Проверка, что исключение НЕ выброшено
@Test
void shouldNotThrow() {
    assertDoesNotThrow(() -> service.validInput());
}

// assertThrowsExactly — ТОЛЬКО указанный тип, без подклассов
@Test
void shouldThrowExactType() {
    // код бросает NullPointerException:
    assertThrows(RuntimeException.class, () -> someMethod());        // ПРОЙДЁТ: NPE — подкласс
    assertThrowsExactly(RuntimeException.class, () -> someMethod()); // УПАДЁТ: тип не совпал точно
    assertThrowsExactly(NullPointerException.class, () -> someMethod()); // ПРОЙДЁТ
}
```

<a id="q6"></a>
## Q6. Что такое @Nested и зачем оно нужно?

`@Nested` помечает внутренний (нестатический) класс, тесты которого JUnit запускает как часть внешнего. Это способ сгруппировать тесты по контексту — например, «когда заказ валиден» и «когда невалиден» — внутри одного тест-класса, не разнося их по разным файлам.

```java
@DisplayName("OrderService")
class OrderServiceTest {

    OrderService service;

    @BeforeEach
    void setUp() { service = new OrderService(); }

    @Nested
    @DisplayName("when order is valid")
    class ValidOrder {
        @Test
        @DisplayName("should create successfully")
        void shouldCreate() { ... }

        @Test
        @DisplayName("should charge payment")
        void shouldCharge() { ... }
    }

    @Nested
    @DisplayName("when order is invalid")
    class InvalidOrder {
        @Test void shouldRejectEmpty() { ... }
        @Test void shouldRejectNegative() { ... }
    }
}
```

**Зачем это нужно:**
- Разбивает длинный плоский тест-класс на смысловые секции.
- У каждого nested-класса свой `@BeforeEach`: общий setup идёт во внешнем классе, а узкий — во вложенном. `@BeforeEach` внешнего класса выполняется и для вложенных тестов.
- Иерархия отражается в отчёте: `OrderService › when order is valid › should create successfully` — по имени теста сразу понятен контекст.

<a id="q7"></a>
## Q7. Как JUnit 5 интегрируется с Mockito?

Интеграция идёт через расширение `@ExtendWith(MockitoExtension.class)`. Оно перед каждым тестом создаёт моки по полям с `@Mock` и внедряет их в объект с `@InjectMocks` — вручную вызывать `MockitoAnnotations.openMocks()` уже не нужно. Дальше работаете как обычно: `when(...).thenReturn(...)` задаёт поведение мока, `verify(...)` проверяет, что метод был (или не был) вызван.

```java
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderService service;  // автоматически внедрит mocks

    @Test
    void shouldSaveOrder() {
        Order order = new Order(...);
        when(repository.save(any())).thenReturn(order);

        service.create(order);

        verify(repository).save(order);
        verify(emailService).sendConfirmation(order.email());
    }

    @Test
    void shouldHandleSaveFailure() {
        when(repository.save(any())).thenThrow(new DataAccessResourceFailureException("DB error"));

        assertThrows(OrderCreationException.class,
            () -> service.create(new Order(...)));

        verify(emailService, never()).sendConfirmation(any());
    }
}
```

**Нюанс:** по умолчанию `MockitoExtension` работает в strict-режиме — если на мок задан `when(...)`, который тест ни разу не вызвал, тест упадёт с `UnnecessaryStubbingException`. Это помогает чистить мёртвые заглушки.

<a id="q8"></a>
## Q8. Что такое Dynamic Tests?

Dynamic tests — это тесты, которые генерируются **во время выполнения**, а не объявляются методами на этапе компиляции. Метод помечают `@TestFactory`: он возвращает `Stream`, `Collection`, `Iterable`, `Iterator` или массив `DynamicNode`. Узлы бывают `DynamicTest` с именем и выполняемым телом, а также `DynamicContainer` с дочерними узлами. JUnit прогоняет их как отдельные тесты.

У `@ParameterizedTest` одно тело метода вызывается с аргументами, которые тоже могут вычисляться во время запуска, например через `@MethodSource`. `@TestFactory` создаёт узлы с собственными именами и выполняемыми телами. Различие — в модели тестов, а не в наличии runtime-данных.

```java
@TestFactory
Stream<DynamicTest> dynamicTestsFromStream() {
    return Stream.of("alice", "bob", "charlie")
        .map(name -> DynamicTest.dynamicTest(
            "Validate user: " + name,
            () -> assertTrue(userValidator.isValid(name))
        ));
}

@TestFactory
Collection<DynamicTest> dynamicTestsFromCollection() {
    return List.of(
        DynamicTest.dynamicTest("Add positive", () -> assertEquals(5, 2 + 3)),
        DynamicTest.dynamicTest("Add zero", () -> assertEquals(5, 5 + 0))
    );
}
```

**Сценарий применения:** нужно построить дерево проверок с отдельными телами и именами. Строки CSV и вычисляемые входы также подходят для параметризованных тестов — сами по себе они не требуют `@TestFactory`.

**Lifecycle:** в Jupiter 5.10.3 `@BeforeEach`/`@AfterEach` и соответствующие extension callbacks вызываются для фабрики, а не для каждого созданного `DynamicTest`. Захваченное состояние между дочерними проверками автоматически не сбрасывается. Для отдельного setup каждого случая его организуют внутри выполняемого тела либо выбирают параметризованный тест.

<a id="q9"></a>
## Q9. Что такое JUnit Extensions?

Extensions — единый механизм, через который в жизненный цикл теста встраивают чужую логику: инициализацию моков, поднятие Spring-контекста, замер времени. В JUnit 4 для этого было два разных механизма (`@Rule` и `@RunWith`), причём раннер мог быть только один. В JUnit 5 эти роли выполняют расширения, и несколько расширений можно подключить через `@ExtendWith`.

Расширение реализует один или несколько callback-интерфейсов, и JUnit вызывает его в нужный момент:
- `BeforeAllCallback` / `BeforeEachCallback` — до всех тестов / до каждого.
- `ParameterResolver` — подставляет аргументы в тест-методы (так работает внедрение `TestInfo`, мок-объектов и т.п.).
- `TestExecutionExceptionHandler` — перехватывает исключения из теста.

Несколько совместимых расширений можно перечислить в `@ExtendWith`, например `@ExtendWith({MockitoExtension.class, TimingExtension.class})`. Ниже отдельный пример без Spring и Mockito:

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

@ExtendWith(TimingExtension.class)
class TimingTest {
    @Test
    void example() { }
}

// Кастомное расширение для вывода времени
class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final String START_TIME = "start time";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        context.getStore(Namespace.create(TimingExtension.class)).put(START_TIME, System.nanoTime());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        long start = context.getStore(Namespace.create(TimingExtension.class)).remove(START_TIME, long.class);
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.printf("%s took %dms%n", context.getRequiredTestMethod().getName(), duration);
    }
}
```

Отметка времени лежит в Store контекста конкретного тестового вызова, а namespace отделяет данные расширения от других расширений в этом контексте. `Namespace.GLOBAL` сам по себе не означает Store корневого контекста. Эти callbacks окружают исполнение тестового метода: пользовательские `@BeforeEach`/`@AfterEach` в этот интервал не входят. Это простой диагностический замер, не benchmark; динамические дети фабрики не получают отдельный такой lifecycle (см. Q8).

<a id="q10"></a>
## Q10. Что такое Conditional Test Execution?

Условные аннотации решают, запускать ли тест: встроенные условия учитывают ОС, JDK, переменные и системные свойства, а собственное `@EnabledIf`/`ExecutionCondition` может вычислять более общее условие. Это решение о запуске, не assertion результата. Штатно отключённый тест считается skipped; исключение при вычислении условия — ошибка, а не обычный пропуск.

Зачем это нужно вместо `@Disabled`: `@Disabled` глушит тест всегда, а условные аннотации включают его именно там, где он должен работать (например, тест с реальной БД — только в CI, где БД поднята).

```java
// Запускать только на Linux
@Test
@EnabledOnOs(OS.LINUX)
void onlyOnLinux() { }

// Запускать только на JDK 17+
@Test
@EnabledForJreRange(min = JRE.JAVA_17)
void requiresJava17() { }

// По переменной окружения
@Test
@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
void runOnlyInCi() { }

// По системному свойству
@Test
@DisabledIfSystemProperty(named = "os.arch", matches = ".*arm.*")
void skipOnArm() { }

// Кастомное условие
@Test
@EnabledIf("isDatabaseAvailable")
void withDatabase() { }

static boolean isDatabaseAvailable() {
    try (Connection c = DriverManager.getConnection(TEST_URL)) {
        return true;
    } catch (SQLException e) {
        return false;
    }
}
```

<a id="q11"></a>
## Q11. Как работает AssertJ и чем он лучше встроенных assertions JUnit?

AssertJ — сторонняя библиотека ассертов с fluent-API: одна точка входа `assertThat(actual)`, дальше цепочка проверок через автодополнение IDE. Она не заменяет JUnit, а дополняет — JUnit запускает тесты, AssertJ проверяет результат выразительнее, чем встроенные `assertEquals`/`assertTrue`.

Ключевая идея — читаемость и точные сообщения об ошибке: `assertThat(orders).hasSize(3)` при провале сам напишет, какой размер был на самом деле, тогда как `assertTrue(orders.size() == 3)` скажет лишь «expected true but was false».

```java
import static org.assertj.core.api.Assertions.*;

@Test
void assertjExample() {
    List<Order> orders = orderService.findRecent();

    // Одной строкой: not-null, size, contains, extracted field
    assertThat(orders)
        .isNotNull()
        .hasSize(3)
        .extracting(Order::status)
        .containsExactly(PENDING, CONFIRMED, SHIPPED);

    assertThat(orders.get(0))
        .isInstanceOf(PriorityOrder.class)
        .hasFieldOrPropertyWithValue("customerId", "c-1")
        .satisfies(o -> {
            assertThat(o.total()).isGreaterThan(BigDecimal.ZERO);
            assertThat(o.items()).hasSizeGreaterThanOrEqualTo(1);
        });

    assertThatThrownBy(() -> orderService.cancel(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Order ID cannot be null");
}
```

**Плюсы над встроенными ассертами:**
- Цепочки проверок на одном объекте без повтора `assertThat`.
- Понятные сообщения об ошибке «из коробки».
- Специализированные ассерты под типы: коллекции (`extracting`, `containsExactly`), даты, `Optional`, исключения (`assertThatThrownBy`). Для `BigDecimal` числовое равенство без учёта scale проверяет `isEqualByComparingTo`; обычный `isEqualTo` различает `1.0` и `1.00`.

У JUnit `assertEquals(expected, actual)` тоже есть expected/actual в диагностике. Преимущество `hasSize` в примере видно относительно `assertTrue(size == n)`, а не всех встроенных assertions.

<a id="q12"></a>
## Q12. Как параллельно выполнять тесты?

Параллельный запуск в JUnit 5 включается **флагом конфигурации**, а не аннотацией: по умолчанию он выключен. Параметр `junit.jupiter.execution.parallel.enabled=true` включает механизм, а execution modes отдельно разрешают конкурентное выполнение узлов. Параметры можно передать через `junit-platform.properties`, Launcher API или настройки JVM/сборщика. Пример ниже использует файл ресурсов; он не обязателен. `fixed` задаёт желаемый parallelism явно, `dynamic` вычисляет его по числу доступных процессоров.

Гранулярность управляется отдельно для методов и для классов, а на уровне конкретного теста/класса режим переопределяют аннотацией `@Execution`.

```properties
# src/test/resources/junit-platform.properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.strategy=fixed
junit.jupiter.execution.parallel.config.fixed.parallelism=4
```

```java
// Аннотации для контроля параллельности
@Execution(ExecutionMode.CONCURRENT)  // тесты класса параллельно
class ParallelTest {

    @Test
    @ResourceLock(value = "database", mode = ResourceAccessMode.READ_WRITE)
    void updateSharedResource() { }  // конфликтует с READ/READ_WRITE того же ключа

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    void runOnParentThread() { }
}
```

**Общий ресурс:** все обращающиеся к нему тесты должны согласованно объявить одинаковый ключ `@ResourceLock`. `READ` совместим с другими читателями, `READ_WRITE` — исключительный доступ относительно объявивших этот ключ тестов Jupiter. Блокировка охватывает соответствующие `@BeforeEach`/`@AfterEach`, но не защищает от неаннотированного кода или внешних процессов. `SAME_THREAD` означает поток родительского узла, а не глобальную блокировку ресурса.

В Jupiter 5.10.3 `fixed.parallelism=4` задаёт желаемую параллельность, а не жёсткий предел потоков: ForkJoinPool может создавать дополнительные потоки при блокировках. Максимальный размер пула настраивается отдельно, например `junit.jupiter.execution.parallel.config.fixed.max-pool-size`; его значение должно быть не меньше parallelism. Классы с `PER_CLASS` или заданным `MethodOrderer` требуют отдельного внимания к `@Execution(CONCURRENT)` для конкурентного выполнения методов и безопасности состояния.

<a id="q13"></a>
## Q13. Как Spring Boot интегрируется с JUnit 5?

Связующее звено — расширение `SpringExtension`: оно поднимает Spring-контекст и внедряет бины в тест через `@Autowired`. Явно его указывать почти не нужно — оно уже зашито в `@SpringBootTest` и тестовые срезы.

Главное на собеседовании — понимать разницу между **полным контекстом и срезами (test slices)**. Срез ограничивает конфигурацию и обычно уменьшает стоимость старта; фактическое время зависит от состава контекста:
- **`@SpringBootTest`** — загружает конфигурацию приложения для проверки взаимодействия слоёв. По умолчанию `webEnvironment=MOCK`, реальный HTTP-сервер не запускается. Для HTTP-проверки нужен, например, `RANDOM_PORT`.
- **`@WebMvcTest`** — MVC-компоненты и `MockMvc`. Обычные `@Service`/`@Repository` не включаются сканированием среза. Нужную зависимость контроллера можно подменить через `@MockBean` в Boot 3.3.2 или осознанно добавить через `@Import`.
- **`@DataJpaTest`** — JPA-срез с репозиториями и `EntityManager`; тесты по умолчанию транзакционны и откатываются после выполнения. В Boot 3.3.2 по умолчанию применяется замена DataSource встроенной тестовой БД, для которой нужна зависимость, например H2. Это поведение можно изменить через `@AutoConfigureTestDatabase`; JPA-пример ниже предполагает JPA-зависимости, которых JDBC-код этого проекта не использует.

```java
// Полный контекст Spring Boot
@SpringBootTest
class ApplicationIntegrationTest {
    @Autowired OrderService orderService;
}

// Только web-слой (без сервисов)
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired MockMvc mvc;
    @MockBean OrderService service;   // org.springframework.boot.test.mock.mockito
}

// Только JPA-слой; для встроенной БД нужна зависимость, например H2
@DataJpaTest
class OrderRepositoryTest {
    @Autowired OrderRepository repository;
    @Autowired TestEntityManager em;
}

// Явно заданная конфигурация Spring Test; это ещё не автоматический Boot-срез
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class SliceTest { }
```

**Версия проекта:** здесь используется Spring Boot 3.3.2, поэтому пример применяет `@MockBean` из `spring-boot-test`. Начиная с Spring Boot 3.4 эта аннотация устарела в пользу `@MockitoBean` из `spring-test`; при переходе на более новую версию пример нужно обновить.

**Кэш контекста:** переиспользование происходит внутри одного процесса при совпадении ключа конфигурации: классов/ресурсов контекста, профилей, свойств, context customizers (в том числе подмен бинов) и других параметров. Не любая новая аннотация означает новый контекст. `@DirtiesContext`, вытеснение из кэша и отдельные fork-процессы ограничивают переиспользование.

<a id="q14"></a>
## Q14. Что такое Test Lifecycle и PER_CLASS vs PER_METHOD?

Lifecycle определяет, **сколько экземпляров тест-класса** создаёт JUnit. Стандартный default — `PER_METHOD`: для каждого обычного тестового вызова создаётся новый экземпляр класса. Default можно изменить параметром `junit.jupiter.testinstance.lifecycle.default`; локальная `@TestInstance` задаёт режим явно. Новый экземпляр заново инициализирует его обычные поля, но не сбрасывает static, общие объекты по ссылкам, Spring singleton или внешнюю БД и не гарантирует независимости от порядка. Для dynamic tests действует lifecycle фабрики (Q8).

Режим `PER_CLASS` (`@TestInstance(Lifecycle.PER_CLASS)`) создаёт **один** экземпляр на весь класс. Поля становятся общими между тестами, зато `@BeforeAll`/`@AfterAll` больше не обязаны быть `static` — у них есть доступ к полям инстанса.

```java
// PER_METHOD (default)
class MyTest {
    private int counter = 0;  // заново для каждого теста

    @Test void test1() { counter++; assertThat(counter).isEqualTo(1); }
    @Test void test2() { counter++; assertThat(counter).isEqualTo(1); }  // тоже 1!
}

// PER_CLASS — один инстанс класса на все тесты
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PerClassTest {
    private int counter = 0;  // общий для всех тестов!

    @BeforeAll
    void setUp() {  // НЕ static благодаря PER_CLASS
        System.out.println("once for all tests");
    }

    @Test void test1() { counter++; }
    @Test void test2() { counter++; }  // counter = 2 если test1 был первым
}
```

**Когда брать PER_CLASS:** нужен нестатический `@BeforeAll`/`@AfterAll` или внутренний нестатический `@MethodSource`. Дорогой ресурс можно подготовить один раз и через статический `@BeforeAll` при `PER_METHOD`; сам по себе его размер или время старта не требуют `PER_CLASS`. В примере с default предполагается отсутствие глобального переопределения lifecycle.

**Компромисс:** общий state между тестами легко делает их зависимыми от порядка и нестабильными. Если выбираете `PER_CLASS`, явно сбрасывайте изменяемое состояние в `@BeforeEach` либо избегайте мутабельных полей.

<a id="q15"></a>
## Q15. Какие типичные ошибки в юнит-тестах?

Одна из причин хрупкости — привязка теста к внутреннему устройству кода вместо наблюдаемого поведения. Такой тест ломается при рефакторинге, хотя поведение не изменилось. Есть и другие ошибки: нестабильные данные, общее изменяемое состояние и внешние зависимости в unit-тестах.

1. **Тестирование реализации, а не поведения** — проверяют приватные методы (часто через рефлексию). Тестируйте через публичный API: пока контракт цел, внутренности можно переписывать без правки тестов.

```java
// ПЛОХО — тест привязан к приватному методу
@Test void testInternalMethod() {
    BigDecimal total = ReflectionTestUtils.invokeMethod(orderService, "calculateTotalInternal");
    assertThat(total).isEqualByComparingTo("100.00");
}

// ХОРОШО — через публичный API
@Test void shouldReturnCorrectTotal() {
    Order order = orderService.createOrder(...);
    assertThat(order.total()).isEqualByComparingTo("100.00");
}
```

2. **Слишком много mocks** — если для одного теста нужно 10 моков, это сигнал: либо у класса слишком много зависимостей (проблема дизайна), либо вы взяли не тот уровень теста и здесь уместнее integration test.

```java
// Если тест требует 10 моков — возможно нужен integration test
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock Repo1 r1; @Mock Repo2 r2; @Mock Api1 a1; ... // смелл
}
```

3. **Проверка вызовов моков вместо результата** — `verify(repository).save(...)` фиксирует деталь реализации и хрупок. Где можно, проверяйте итоговое состояние или возвращённое значение; `verify` оставляйте для взаимодействий без наблюдаемого результата (отправка письма, публикация события).

```java
// ПЛОХО — тест узнаёт детали реализации
verify(repository).save(any());  // хрупко

// ЛУЧШЕ — проверка состояния/результата
assertThat(service.getAllOrders()).hasSize(1);
```

4. **Flaky tests** — нестабильность из-за зависимости от текущего времени, порядка запуска или случайных данных. Лечится фиксированным `Clock`, отказом от общего state и сидами для генераторов.

5. **Нет структуры Arrange-Act-Assert** — подготовка, действие и проверка размазаны по методу, тест трудно читать. Держите три фазы визуально разделёнными.

6. **Поход в реальную БД/сеть из unit-теста** — это уже интеграционный тест: он медленный и нестабильный. Такие тесты выносят в отдельный набор/профиль и не смешивают с быстрыми unit-тестами.

## See also

- [Mockito](mockito-interview.md) — мокирование в unit-тестах
- [Unit Testing](unit-testing-interview.md) — best practices модульного тестирования
- [Testcontainers](testcontainers-interview.md) — интеграционные тесты с реальными БД/брокерами
- [Spring Testing](../frameworks/spring/spring-testing-interview.md) — тестирование Spring + JUnit integration
- [Integration Testing](integration-testing-interview.md) — стратегии интеграционных тестов
- [Test Strategies](test-strategies-interview.md) — пирамида тестирования
- [Mutation Testing](mutation-testing-interview.md) — проверка качества тестов (PIT)
- [Property-Based Testing](property-based-testing-interview.md) — jqwik как JUnit extension
- [REST Assured](rest-assured-interview.md) — тестирование REST API
- [Contract Testing](contract-testing-interview.md) — Pact + JUnit
