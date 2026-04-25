---
title: "Вопросы на собеседовании: JUnit 5"
description: "JUnit 5 (Jupiter): аннотации, жизненный цикл PER_CLASS/PER_METHOD, параметризованные и динамические тесты, @Nested, Extensions, интеграция с Mockito и Spring"
tags:
  - interview
  - testing
  - junit-interview
aliases:
  - "JUnit 5 interview"
  - "JUnit собеседование"
  - "Jupiter interview"
  - "JUnit вопросы"
  - "Java unit testing interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `JUnit 5`

## Q1. Что такое JUnit 5 и как он структурирован?

**JUnit 5 (Jupiter)** — современная версия JUnit с модульной архитектурой, состоящая из трёх sub-проектов:

- **JUnit Platform** — фундамент для запуска тестов (используется IDE и Maven/Gradle).
- **JUnit Jupiter** — API для написания тестов (`@Test`, `@BeforeEach` и др.).
- **JUnit Vintage** — совместимость с JUnit 3/4 тестами.

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

**Отличия от JUnit 4**:
- `@Before` → `@BeforeEach`, `@BeforeClass` → `@BeforeAll`
- `@Ignore` → `@Disabled`, `@RunWith` → `@ExtendWith`
- Поддержка Java 8+ (lambdas, streams)
- Более выразительный API для ассертов (`assertThrows`, `assertAll`)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Какие основные аннотации в JUnit 5?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Как использовать параметризованные тесты?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Что такое assertAll и когда его использовать?

`assertAll` — группирует несколько ассертов, выполняя их все даже при падении одного:

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

Применение: когда нужно проверить несколько независимых аспектов одного объекта и видеть все ошибки сразу.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Как проверять исключения?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Что такое @Nested и зачем оно нужно?

`@Nested` — вложенные классы для группировки связанных тестов.

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

Преимущества:
- Структурирует длинный тест-класс.
- Каждый nested class имеет свой `@BeforeEach`.
- Читаемые имена в отчёте: `OrderService › when order is valid › should create successfully`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Как JUnit 5 интегрируется с Mockito?

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

`MockitoExtension` инициализирует моки через `@Mock` без вызова `MockitoAnnotations.openMocks()`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Что такое Dynamic Tests?

`@TestFactory` — создание тестов во время выполнения (в отличие от `@Test` который известен на этапе компиляции):

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

Применение: генерация тестов из файла/БД, когда количество тестов определяется данными.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Что такое JUnit Extensions?

**Extensions** — механизм расширения JUnit (аналог `@Rule` в JUnit 4, но мощнее).

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

Расширения реализуют интерфейсы-callbacks: `BeforeAllCallback`, `ParameterResolver`, `TestExecutionExceptionHandler` и др.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Что такое Conditional Test Execution?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как работает AssertJ и чем он лучше встроенных assertions JUnit?

AssertJ — fluent assertion библиотека, часто используется вместе с JUnit.

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

Преимущества над JUnit assertions: цепочки ассертов, авто-описание ошибки, коллекции/BigDecimal/LocalDate специализации.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Как параллельно выполнять тесты?

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

**Предостережения**: параллельные тесты требуют thread-safety shared state; неправильное использование ведёт к flaky tests.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Как Spring Boot интегрируется с JUnit 5?

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

`SpringExtension` автоматически подключается через `@SpringBootTest` и срезы (`@WebMvcTest`, `@DataJpaTest`).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Что такое Test Lifecycle и PER_CLASS vs PER_METHOD?

По умолчанию JUnit создаёт новый экземпляр тест-класса для каждого теста (`PER_METHOD`) — изолирует тесты друг от друга.

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

**Применение PER_CLASS**: дорогостоящий setup (Spring context, БД миграции), который не должен повторяться.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Какие типичные ошибки в юнит-тестах?

1. **Тестирование реализации, а не поведения**:

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

2. **Слишком много mocks** — знак неправильного дизайна или неправильного слоя теста:

```java
// Если тест требует 10 моков — возможно нужен integration test
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock Repo1 r1; @Mock Repo2 r2; @Mock Api1 a1; ... // смелл
}
```

3. **Проверка mock calls вместо результата**:

```java
// ПЛОХО — тест узнаёт детали реализации
verify(repository).save(any());  // хрупко

// ЛУЧШЕ — проверка состояния/результата
assertThat(service.getAllOrders()).hasSize(1);
```

4. **Flaky tests** от зависимости от времени/порядка/случайных данных.

5. **No Arrange-Act-Assert** — размазанная логика, сложно читается.

6. **Pойти сразу в БД/сеть в unit тесте** — это интеграционный тест, должен быть в отдельной папке/профиле.

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [Mockito](mockito-interview.md) — мокирование в unit-тестах
- [Unit Testing](unit-testing-interview.md) — best practices модульного тестирования
- [Testcontainers](testcontainers-interview.md) — интеграционные тесты с реальными БД/брокерами
- [Spring Testing](../frameworks/spring/spring-testing-interview.md) — тестирование Spring + JUnit integration
- [Integration Testing](integration-testing-interview.md) — стратегии интеграционных тестов
- [Test Strategies](test-strategies-interview.md) — пирамида тестирования
- [Mutation Testing](mutation-testing-interview.md) — проверка качества тестов (PIT)
- [Property-Based Testing](property-based-testing-interview.md) — jqwik как JUnit extension
- [REST Assured](rest-assured-interview.md) — тестирование REST API
- [Contract Testing](contract-testing-interview.md) — Pact + JUnit
