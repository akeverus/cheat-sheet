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
updated: "2026-05-08"
---
# Вопросы на собеседовании: `JUnit 5`

## Q1. Что такое JUnit 5 и как он структурирован?

**JUnit 5** — это не один артефакт, а зонтик из трёх независимых sub-проектов. Такое разделение позволяет, например, IDE и сборщикам работать с тестами через стабильный Platform API, не завязываясь на конкретный API написания тестов.

- **JUnit Platform** — фундамент для запуска тестов: определяет `TestEngine` SPI, через который IDE и Maven/Gradle находят и запускают тесты. Сам тесты не пишет.
- **JUnit Jupiter** — новый API + движок для написания тестов (`@Test`, `@BeforeEach`, `assertThrows` и др.). Это то, что в обиходе и называют «JUnit 5».
- **JUnit Vintage** — движок-мост, позволяющий запускать старые тесты JUnit 3/4 на той же Platform. Нужен на время миграции.

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

**Главные отличия от JUnit 4** (часто спрашивают на собеседовании):
- Аннотации жизненного цикла переименованы яснее: `@Before` → `@BeforeEach`, `@BeforeClass` → `@BeforeAll`.
- `@Ignore` → `@Disabled`, а громоздкий `@RunWith` заменён на композируемый `@ExtendWith` — расширений можно навесить несколько, в JUnit 4 раннер был один.
- Требует Java 8+, что позволяет передавать ассерты как lambda (`assertThrows(..., () -> ...)`) и работать со streams в `@MethodSource`.
- Выразительнее API ассертов: `assertThrows` возвращает само исключение для проверки сообщения, `assertAll` группирует проверки.

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

## Q3. Как использовать параметризованные тесты?

Параметризованный тест прогоняет **один** метод на наборе входных данных — вместо копипасты пяти почти одинаковых `@Test`. Метод помечают `@ParameterizedTest` (вместо `@Test`) и добавляют источник данных. Каждый набор аргументов — отдельный прогон в отчёте.

Какой источник выбрать:
- **`@ValueSource`** — один параметр-примитив или строка. Самый простой случай.
- **`@CsvSource`** — несколько параметров в строке через запятую (прямо в коде).
- **`@CsvFileSource`** — те же CSV, но из ресурса; удобно для больших таблиц данных.
- **`@EnumSource`** — все (или отобранные) значения enum; гарантирует, что новый элемент enum не забудут протестировать.
- **`@MethodSource`** — самый мощный: источник — метод, возвращающий `Stream<Arguments>`. Нужен, когда аргументы — сложные объекты или их надо вычислять.

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

## Q4. Что такое assertAll и когда его использовать?

`assertAll` выполняет **все** переданные проверки, даже если какая-то упала, и в конце показывает список всех провалившихся сразу. Обычные ассерты работают по принципу fail-fast: первый же `assertEquals` бросает исключение, и про остальные проверки вы не узнаете, пока не почините первую и не перезапустите тест.

```java
@Test
void shouldCreateUser() {
    User user = userService.create("alice", "alice@example.com", 25);

    // Без assertAll — падает на первом, не видим остальных ошибок
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

**Подводный камень:** не оборачивайте в `assertAll` зависимые проверки. Если результат второй проверки имеет смысл только при пройденной первой (например, сначала `assertNotNull(list)`, потом `list.get(0)`), то на упавшем `null` вторая проверка кинет `NullPointerException`. Такие проверки оставляйте обычными — пусть тест останавливается раньше.

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
    assertThrowsExactly(NullPointerException.class,
        () -> someMethod());
    // RuntimeException не пройдёт, хотя NPE — подкласс
}
```

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
        when(repository.save(any())).thenThrow(new DataAccessException("DB error"));

        assertThrows(OrderCreationException.class,
            () -> service.create(new Order(...)));

        verify(emailService, never()).sendConfirmation(any());
    }
}
```

**Нюанс:** по умолчанию `MockitoExtension` работает в strict-режиме — если на мок задан `when(...)`, который тест ни разу не вызвал, тест упадёт с `UnnecessaryStubbingException`. Это помогает чистить мёртвые заглушки.

## Q8. Что такое Dynamic Tests?

Dynamic tests — это тесты, которые генерируются **во время выполнения**, а не объявляются методами на этапе компиляции. Метод помечают `@TestFactory`, и он возвращает `Stream` или `Collection` объектов `DynamicTest` — каждый со своим именем и lambda-телом. JUnit прогоняет их как отдельные тесты.

В чём разница с `@ParameterizedTest`: параметризованный тест берёт фиксированный набор данных и гоняет на нём один заранее написанный метод. Фабрика же сама решает в рантайме, **сколько** тестов создать и **что** каждый из них делает.

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

**Сценарий применения:** количество тестов заранее неизвестно — например, на каждую строку CSV-файла, на каждую запись из БД или на каждый файл в каталоге нужен свой прогон с осмысленным именем.

## Q9. Что такое JUnit Extensions?

Extensions — единый механизм, через который в жизненный цикл теста встраивают чужую логику: инициализацию моков, поднятие Spring-контекста, замер времени. В JUnit 4 для этого было два разных механизма (`@Rule` и `@RunWith`), причём раннер мог быть только один. В JUnit 5 всё свелось к расширениям, и их можно навешивать сколько угодно через `@ExtendWith`.

Расширение реализует один или несколько callback-интерфейсов, и JUnit вызывает его в нужный момент:
- `BeforeAllCallback` / `BeforeEachCallback` — до всех тестов / до каждого.
- `ParameterResolver` — подставляет аргументы в тест-методы (так работает внедрение `TestInfo`, мок-объектов и т.п.).
- `TestExecutionExceptionHandler` — перехватывает исключения из теста.

```java
// Популярные расширения
@ExtendWith({
    MockitoExtension.class,
    SpringExtension.class,
    TimingExtension.class
})
class MyTest { }

// Кастомное расширение для логирования времени
class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final String START_TIME = "start time";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        context.getStore(Namespace.GLOBAL).put(START_TIME, System.nanoTime());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        long start = context.getStore(Namespace.GLOBAL).remove(START_TIME, long.class);
        long duration = (System.nanoTime() - start) / 1_000_000;
        log.info("{} took {}ms", context.getRequiredTestMethod().getName(), duration);
    }
}
```

В примере выше `TimingExtension` ловит начало и конец каждого теста и логирует длительность, складывая отметку времени в `ExtensionContext.Store` — встроенное хранилище, переживающее между callback-ами.

## Q10. Что такое Conditional Test Execution?

Это набор аннотаций, которые позволяют **пропустить** тест в зависимости от окружения, а не от логики самого теста: ОС, версия JDK, переменная окружения, системное свойство или собственное условие. Тест, не прошедший условие, помечается как пропущенный (skipped), а не упавший — поэтому таким способом обходят тесты, которые в данном окружении заведомо не имеют смысла.

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
- Специализированные ассерты под типы: коллекции (`extracting`, `containsExactly`), `BigDecimal` (сравнение по значению, а не по `equals`), даты, `Optional`, исключения (`assertThatThrownBy`).

## Q12. Как параллельно выполнять тесты?

Параллельный запуск в JUnit 5 включается **флагом конфигурации**, а не аннотацией: по умолчанию он выключен. Нужно создать `junit-platform.properties` в `src/test/resources` и выставить `parallel.enabled=true`, после чего задать стратегию числа потоков (`fixed` с явным числом или `dynamic` от числа ядер) и режим по умолчанию (`concurrent` — параллельно, `same_thread` — последовательно).

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
    void updateSharedResource() { }  // тесты с этим ResourceLock сериализуются

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    void mustRunSequentially() { }
}
```

**Подводные камни:** параллельность вскрывает любой общий мутабельный state — статические поля, синглтоны, общую тестовую БД. Если такой ресурс не потокобезопасен, тесты становятся flaky (то падают, то нет). Решение — `@ResourceLock`: тесты, делящие один ресурс, JUnit сериализует между собой, оставляя остальные параллельными.

## Q13. Как Spring Boot интегрируется с JUnit 5?

Связующее звено — расширение `SpringExtension`: оно поднимает Spring-контекст и внедряет бины в тест через `@Autowired`. Явно его указывать почти не нужно — оно уже зашито в `@SpringBootTest` и тестовые срезы.

Главное на собеседовании — понимать разницу между **полным контекстом и срезами (test slices)**. Чем уже срез, тем быстрее тест и тем меньше лишних бинов поднимается:
- **`@SpringBootTest`** — поднимает весь контекст приложения. Мощно, но медленно; для end-to-end сценариев.
- **`@WebMvcTest`** — только web-слой (контроллеры, фильтры) с готовым `MockMvc`. Сервисы и репозитории сюда не попадают — их подменяют через `@MockBean`.
- **`@DataJpaTest`** — только JPA-слой: репозитории, `EntityManager`, по умолчанию in-memory БД и откат транзакции после каждого теста.

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
    @MockBean OrderService service;
}

// Только JPA-слой с H2 in-memory
@DataJpaTest
class OrderRepositoryTest {
    @Autowired OrderRepository repository;
    @Autowired TestEntityManager em;
}

// Кастомный срез — только компоненты для конкретной аннотации
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class SliceTest { }
```

**Нюанс производительности:** Spring кэширует поднятый контекст и переиспользует его между тестами с одинаковой конфигурацией. Поэтому держите конфигурации тестов единообразными — каждый уникальный набор аннотаций/`@MockBean`/`properties` плодит новый контекст и замедляет сборку.

## Q14. Что такое Test Lifecycle и PER_CLASS vs PER_METHOD?

Lifecycle определяет, **сколько экземпляров тест-класса** создаёт JUnit. По умолчанию режим `PER_METHOD`: на каждый тест создаётся новый объект класса. Это даёт изоляцию — поля, изменённые в одном тесте, не «протекают» в другой, и тесты не зависят от порядка запуска.

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

**Когда брать PER_CLASS:** дорогой setup, который незачем повторять на каждый тест (поднятие тяжёлого ресурса, нестатический `@BeforeAll`), либо когда нестатический метод-источник нужен для `@MethodSource`.

**Компромисс:** общий state между тестами легко делает их зависимыми от порядка и нестабильными. Если выбираете `PER_CLASS`, явно сбрасывайте изменяемое состояние в `@BeforeEach` либо избегайте мутабельных полей.

## Q15. Какие типичные ошибки в юнит-тестах?

Большинство проблем сводится к одному корню: тест привязан к тому, **как** код устроен внутри, а не к тому, **что** он делает снаружи. Такой тест ломается на любом рефакторинге, хотя поведение не изменилось. Самые частые проявления:

1. **Тестирование реализации, а не поведения** — проверяют приватные методы (часто через рефлексию). Тестируйте через публичный API: пока контракт цел, внутренности можно переписывать без правки тестов.

```java
// ПЛОХО — хрупкий тест
@Test void testInternalMethod() {
    orderService.calculateTotalInternal();  // тестируем приватный метод через рефлексию
}

// ХОРОШО — через публичный API
@Test void shouldReturnCorrectTotal() {
    Order order = ...;
    assertThat(order.total()).isEqualTo(new BigDecimal("100.00"));
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
