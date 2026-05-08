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
> - [x] JUnit 5 = `Platform` (запуск) + `Jupiter` (API: `@Test`, `@BeforeEach`) + `Vintage` (запуск JUnit 3/4) | Три модуля разделены ради независимой эволюции API и платформы; Gradle/Maven подключают `junit-jupiter`, IDE — `junit-platform-launcher`. ✓ ПРИМЕНЯТЬ: Spring Boot Starter Test тянет `junit-jupiter` + `junit-platform`, а Vintage добавляют отдельно для legacy-тестов. 📋 ПРАВИЛО: «Platform запускает, Jupiter пишет, Vintage переживает». 🔗 См. Q2, Q9.
> - [ ] JUnit 5 — это переименованный JUnit 4 с новой версией Maven artifact, API не менялся | Неверно: пакет сменился с `org.junit` на `org.junit.jupiter.api`, аннотации переименованы (`@Before` → `@BeforeEach`). ❌ ПОСЛЕДСТВИЕ: компиляция падает с `cannot find symbol: @Before` после миграции `junit:junit:4.x` → `junit-jupiter:5.x` без рефакторинга импортов.
> - [ ] JUnit 5 = один монолитный jar `junit-jupiter`, дополнительных модулей нет | Неверно: артефакт `junit-jupiter` — aggregator, который подтягивает `api`, `params`, `engine` отдельными jar; без `junit-platform-launcher` IDE не найдёт тесты. ❌ ПОСЛЕДСТВИЕ: Gradle подключил только `junit-jupiter-api` без `engine` → `No tests found for given includes` в CI, ноль тестов выполнено.
> - [ ] JUnit Vintage — это устаревший движок JUnit 5, удалён с версии 5.7 | Неверно: Vintage активно поддерживается, в `junit-vintage-engine` 5.10 и нужен для запуска JUnit 3/4 тестов на платформе JUnit 5. ❌ ПОСЛЕДСТВИЕ: команда выкинула `junit-vintage-engine` из POM при апгрейде Spring Boot 3 → 200+ legacy-тестов JUnit 4 не запускаются, регресс уехал в prod.

## Q2. Какие основные аннотации в JUnit 5?

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
> - [ ] `@BeforeAll` запускается перед каждым тестом класса, а `@BeforeEach` — ровно один раз на запуск тест-класса | Перепутано: `@BeforeAll` — раз на класс (фикстуры), `@BeforeEach` — перед каждым `@Test` (свежий state). ❌ ПОСЛЕДСТВИЕ: дорогой setup БД помещён в `@BeforeEach` под именем `@BeforeAll` → 200 тестов поднимают Testcontainers 200 раз, прогон 8 минут вместо 40 секунд, CI красный по таймауту.
> - [x] `@BeforeAll`/`@AfterAll` должны быть `static` при `PER_METHOD` (default); `@BeforeEach`/`@AfterEach` — instance перед/после каждого теста | JUnit создаёт новый инстанс на каждый `@Test`, поэтому `@BeforeAll` обязан быть `static` или включить `@TestInstance(PER_CLASS)`. ✓ ПРИМЕНЯТЬ: Spring Boot тесты используют `@BeforeAll static` для подъёма Testcontainers и `@BeforeEach` для очистки таблиц. 📋 ПРАВИЛО: «BeforeAll — static или PER_CLASS». 🔗 См. Q1, Q14.
> - [ ] `@Disabled` пропускает тест без записи в отчёт, как будто его нет | Неверно: `@Disabled` отображается в отчёте как `skipped` с причиной, видно в Surefire/Allure. ❌ ПОСЛЕДСТВИЕ: команда полагалась, что `@Disabled` "молча" скрывает тест → SonarQube quality gate FAIL по правилу `JUnitTestsShouldIncludeAssert` на сотне skipped-тестов.
> - [ ] `@RepeatedTest(N)` запускает тест параллельно в N потоках | Неверно: `@RepeatedTest` запускает последовательно N раз (для проверки flakiness/детерминированности); параллелизм настраивается через `junit.jupiter.execution.parallel.enabled`. ❌ ПОСЛЕДСТВИЕ: тест с `@RepeatedTest(1000)` ожидался как нагрузочный → блокирует CI на 30 минут вместо параллельной проверки race-condition.

## Q3. Как использовать параметризованные тесты?

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
> - [ ] `@ParameterizedTest` без источника (`@ValueSource`/`@MethodSource`) запустится один раз с null-аргументом | Неверно: JUnit бросит `PreconditionViolationException: Configuration error: You must configure at least one arguments source` ещё до запуска. ❌ ПОСЛЕДСТВИЕ: PR ревьювер не заметил отсутствие `@ValueSource` → весь тест-класс падает в CI с `ParameterResolutionException`, билд красный.
> - [ ] `@CsvSource` поддерживает только `String`, числа и enum приходят как строки и требуют ручного парсинга | Неверно: JUnit 5 автоматически конвертирует через `DefaultArgumentConverter` для примитивов, `enum`, `LocalDate`, `BigDecimal` (`fallback` JSR-310). ❌ ПОСЛЕДСТВИЕ: разработчик дублирует `Integer.parseInt(s)` в каждом тесте → лишние 200 строк boilerplate, при ошибке формата `NumberFormatException` маскирует реальный баг.
> - [x] `@ParameterizedTest` + источник (`@ValueSource`, `@CsvSource`, `@MethodSource`, `@EnumSource`) запускает тест по разу на каждый аргумент с уникальным именем в отчёте | Каждый набор аргументов — отдельный invocation, видно в IDE/CI как `shouldComputeLength(String, int)[1] hello, 5`. ✓ ПРИМЕНЯТЬ: Spring Cloud Stream использует `@MethodSource` для прогонки одного теста по всем сериализаторам Avro/Protobuf/JSON. 📋 ПРАВИЛО: «один тест — много данных, один баг — один вход». 🔗 См. Q8, Q11.
> - [ ] `@MethodSource("provider")` требует, чтобы метод `provider` был `public` и нестатический | Неверно: метод-источник должен быть `static` (или `PER_CLASS` lifecycle), иначе `JUnitException: Cannot invoke non-static method`. ❌ ПОСЛЕДСТВИЕ: разработчик сделал `provider()` instance-методом без `@TestInstance(PER_CLASS)` → тесты не стартуют, CI красный с `Could not find factory method`.

## Q4. Что такое assertAll и когда его использовать?

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
> - [ ] `assertAll` ловит и подавляет любые `RuntimeException` внутри лямбд, делая тест зелёным | Неверно: `assertAll` ловит только `AssertionError` и собирает их в `MultipleFailuresError`; обычные исключения пробрасываются и падают тест. ❌ ПОСЛЕДСТВИЕ: разработчик завернул проблемную логику в `assertAll`, надеясь "проглотить" `NullPointerException` → тест краснеет с другим стектрейсом, время на дебаг x3.
> - [ ] `assertAll` запускает ассерты в параллельных потоках и проверяет thread-safety | Неверно: ассерты внутри `assertAll` выполняются последовательно в одном потоке; параллелизм — это `@Execution(CONCURRENT)`. ❌ ПОСЛЕДСТВИЕ: команда написала race-condition тест через `assertAll`, ожидая параллельное выполнение → race никогда не воспроизводится в CI, баг ушёл в prod.
> - [ ] `assertAll` падает на первом неуспешном ассерте, как обычная цепочка `assertEquals` | Неверно: ключевая фича `assertAll` — выполнить ВСЕ лямбды и собрать все провалы; иначе зачем он нужен. ❌ ПОСЛЕДСТВИЕ: тест с `assertAll` упал на 1 из 5 ассертов, разработчик чинит только это → пушит, в CI вылазит ещё 3 провала, цикл повторяется 4 раза.
> - [x] `assertAll` выполняет все переданные `Executable` и собирает все `AssertionError` в один `MultipleFailuresError` (soft assertions) | Видно сразу все ошибки в одном объекте теста — экономит ре-ран; первый failed не прерывает остальные. ✓ ПРИМЕНЯТЬ: тесты DTO с десятками полей в Wolt order-service используют `assertAll` для верификации всех полей `OrderResponse` за один прогон. 📋 ПРАВИЛО: «assertAll — один объект, все промахи разом». 🔗 См. Q5, Q11.

## Q5. Как проверять исключения?

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
> - [x] `assertThrows(Type.class, () -> code)` возвращает пойманный exception, на нём проверяют сообщение/cause; `assertThrowsExactly` требует точный класс | Лямбда выполняется внутри ассерта; если исключения нет или тип не совпал — тест падает. ✓ ПРИМЕНЯТЬ: Spring Data репозитории тестируют `DataIntegrityViolationException` через `assertThrows`, проверяя `getMessage()` на нарушение constraint. 📋 ПРАВИЛО: «assertThrows возвращает — assertThrowsExactly придирается к классу». 🔗 См. Q4, Q11.
> - [ ] `@Test(expected = X.class)` — корректный синтаксис JUnit 5 для проверки исключений | Неверно: это синтаксис JUnit 4; в Jupiter `@Test` не имеет атрибута `expected`, только `assertThrows`. ❌ ПОСЛЕДСТВИЕ: миграция с JUnit 4 без рефакторинга → `@Test(expected=...)` компилируется (если случайно остался импорт `org.junit.Test`), но тест выполняется JUnit 4 движком и не валидируется JUnit 5, ложное чувство покрытия.
> - [ ] `assertThrows` на `CompletableFuture<Void>` ловит исключение из асинхронной задачи | Неверно: `CompletableFuture` не бросает синхронно — нужно `.get()` или `.join()` внутри лямбды, иначе assertion проходит на не выполненной задаче. ❌ ПОСЛЕДСТВИЕ: тест зелёный, но реальное исключение в `CompletableFuture.supplyAsync(...)` не проверено → баг асинхронной обработки заказов уехал в prod, alert от наблюдаемости через сутки.
> - [ ] `assertDoesNotThrow` молча игнорирует все исключения, не валит тест | Неверно: `assertDoesNotThrow` явно падает с `Unexpected exception thrown: ...`, если внутри лямбды что-то выброшено. ❌ ПОСЛЕДСТВИЕ: разработчик использовал `assertDoesNotThrow` чтобы "обернуть" сомнительный кусок и не разбираться → при флаки-исключении тест краснеет неожиданно, виноватым считают `assertDoesNotThrow`.

## Q6. Что такое @Nested и зачем оно нужно?

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
> - [ ] `@Nested` класс ОБЯЗАН быть `static`, иначе JUnit не найдёт inner-class и пропустит тесты | Наоборот: `@Nested` требует нестатический inner class — статический не получит доступ к `@BeforeEach` и полям outer-класса. ❌ ПОСЛЕДСТВИЕ: разработчик добавил `static` к nested-классу по привычке от JUnit 4 → `JUnitException: @Nested classes must not be static`, тесты не запускаются, билд красный.
> - [x] `@Nested` создаёт нестатический inner-класс с собственным `@BeforeEach`, наследует setup outer-класса; `BDD-стиль` `when X then Y` через `@DisplayName` | JUnit инстанцирует outer + inner на каждый тест, читаемые отчёты `OrderService › when valid › should charge`. ✓ ПРИМЕНЯТЬ: Booking.com booking-service группирует валидацию (valid/invalid/expired) через `@Nested` + `@DisplayName` для прозрачной матрицы покрытия. 📋 ПРАВИЛО: «@Nested = контекст + BDD-структура одного класса». 🔗 См. Q2, Q14.
> - [ ] `@BeforeEach` в outer-классе НЕ выполняется перед тестами в `@Nested`, нужно дублировать setup внутри nested | Неверно: outer `@BeforeEach` выполняется ПЕРЕД nested `@BeforeEach` (каскадный setup), это закреплено в JUnit Jupiter spec. ❌ ПОСЛЕДСТВИЕ: команда продублировала setup в каждом nested-классе → дрифт между outer/inner setup, тест зелёный, а в prod зависимость инициализирована не теми значениями, баг прячется.
> - [ ] `@Nested` нужен только для visual-группировки в IDE, не влияет на lifecycle | Неверно: nested-классы имеют независимый `@BeforeEach`/`@AfterEach`, могут переопределять `@TestInstance` lifecycle, входят в граф `ExtensionContext`. ❌ ПОСЛЕДСТВИЕ: разработчик ожидал, что `@Mock` из outer переиспользуется в nested без `@ExtendWith` → `NullPointerException` на mock в nested-тесте, фикс через час.

## Q7. Как JUnit 5 интегрируется с Mockito?

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
> - [ ] `MockitoExtension` инициализирует моки только при `@ExtendWith(MockitoExtension.class)` + явном вызове `MockitoAnnotations.openMocks(this)` в `@BeforeEach` (нужны оба) | Дублирование: либо Extension, либо `openMocks` — JUnit 5 + Mockito требуют ровно один способ инициализации, оба не нужны и конфликтуют. ❌ ПОСЛЕДСТВИЕ: двойная инициализация → mocks "сбрасываются" в середине теста, `verify(repository)` теряет вызовы, тест флаки в CI, неделя дебага.
> - [ ] `@MockBean` из `spring-boot-test` работает в чистом юнит-тесте без `@SpringBootTest`/`SpringExtension`, аналогично `@Mock` Mockito | Неверно: `@MockBean` требует Spring TestContext — иначе Spring не подменит бин в контексте, и mock инжектиться не будет. ❌ ПОСЛЕДСТВИЕ: разработчик использовал `@MockBean` в чистом `@ExtendWith(MockitoExtension.class)` тесте → mock создан, но не инжектится, `NullPointerException` в реальном бине, час дебага.
> - [x] `@ExtendWith(MockitoExtension.class)` + `@Mock`/`@InjectMocks` создаёт моки и внедряет их в SUT через конструктор/сеттеры/поля; default `Strictness.STRICT_STUBS` ругается на лишние стабы | Strict mode помогает находить мёртвые `when(...)` стабы, которые никогда не вызываются. ✓ ПРИМЕНЯТЬ: Netflix Hystrix-replacement тесты `Resilience4j` используют `@InjectMocks` для CircuitBreaker SUT с моками `MeterRegistry` + `EventPublisher`. 📋 ПРАВИЛО: «@InjectMocks по конструктору, @Mock по полю, Strict ловит мёртвые стабы». 🔗 См. Q9, Q13.
> - [ ] `@InjectMocks` инжектит моки только через `@Autowired` поля (Spring DI стилем) | Неверно: `@InjectMocks` использует свою стратегию (constructor → setter → field), независимо от Spring; `@Autowired` тут не при чём. ❌ ПОСЛЕДСТВИЕ: команда обмазала SUT `@Autowired` для `@InjectMocks` → Mockito пропускает поля без публичных setter, NullPointerException в момент вызова метода SUT.

## Q8. Что такое Dynamic Tests?

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
> - [ ] `@TestFactory` — синоним `@ParameterizedTest`, разница только в возвращаемом типе | Неверно: `@ParameterizedTest` берёт аргументы из источников, `@TestFactory` ВОЗВРАЩАЕТ `Stream<DynamicTest>`/`Collection`, динамически собранные в runtime. ❌ ПОСЛЕДСТВИЕ: разработчик заменил `@ParameterizedTest` на `@TestFactory` "для красоты" → теряет конвертацию аргументов и красивые имена `[1] hello, 5`, отчёт нечитаем.
> - [ ] `@TestFactory` методы могут быть `static` и без параметров инфраструктуры — они компилируются обычными `@Test` | Неверно: `@TestFactory` должен возвращать `Stream<? extends DynamicNode>`, `Iterable`, `Iterator` или `DynamicNode[]`; обычный `void @Test` JUnit отвергнет. ❌ ПОСЛЕДСТВИЕ: разработчик пометил `void` метод `@TestFactory` → `JUnitException: @TestFactory method must not return void`, билд красный.
> - [ ] `DynamicTest.dynamicTest(name, executable)` запускается параллельно автоматически, как stream-API | Неверно: даже `Stream.parallel()` источник не делает динамические тесты параллельными — нужно `@Execution(CONCURRENT)` + `junit-platform.properties`. ❌ ПОСЛЕДСТВИЕ: команда ждёт ускорения от `parallelStream()` → тесты идут последовательно, прогон 20 минут вместо ожидаемых 4.
> - [x] `@TestFactory` создаёт тесты в runtime из коллекции/стрима через `DynamicTest.dynamicTest(name, executable)`; набор/имена видны только после выполнения метода-фабрики | Полезно когда количество кейсов определяется внешними данными (CSV, БД, API) и неизвестно до старта. ✓ ПРИМЕНЯТЬ: LinkedIn schema-registry тесты генерируют `@TestFactory` по списку зарегистрированных Avro-схем — каждая схема = свой `DynamicTest`. 📋 ПРАВИЛО: «@TestFactory — тесты по факту данных, не по компиляции». 🔗 См. Q3, Q11.

## Q9. Что такое JUnit Extensions?

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
> - [x] Extension API — набор интерфейсов-callbacks (`BeforeAllCallback`, `AfterTestExecutionCallback`, `ParameterResolver`, `TestExecutionExceptionHandler`), регистрируемых через `@ExtendWith` или ServiceLoader | Заменяет `@Rule`/`@RunWith` JUnit 4 более гибко: расширения комбинируются и не конкурируют за единственный `@RunWith`. ✓ ПРИМЕНЯТЬ: Spring `SpringExtension`, Mockito `MockitoExtension`, Testcontainers `@Testcontainers` — все реализуют callback-интерфейсы. 📋 ПРАВИЛО: «Extension = callback на этапе lifecycle, комбинируется свободно». 🔗 См. Q7, Q13.
> - [ ] Несколько `@RunWith` можно комбинировать в JUnit 5 | Неверно: `@RunWith` — это JUnit 4 концепция, и она допускала ровно ОДИН runner; в JUnit 5 заменена на `@ExtendWith`, которая поддерживает много расширений. ❌ ПОСЛЕДСТВИЕ: попытка использовать `@RunWith(SpringJUnit4ClassRunner.class)` в JUnit 5 классе → тест компилируется с импортом `org.junit.runner.RunWith`, но Jupiter его игнорирует, Spring контекст не поднимается.
> - [ ] Кастомные расширения требуют написания собственного `TestEngine` | Неверно: `TestEngine` — это уровень JUnit Platform для совершенно нового движка (как Vintage); расширения работают на уровне Jupiter callback-интерфейсов. ❌ ПОСЛЕДСТВИЕ: команда полезла писать кастомный `TestEngine` для логирования времени → 2 недели разработки вместо 50 строк `BeforeTestExecutionCallback`.
> - [ ] `@ExtendWith` обязательно должен быть на классе, на методе не работает | Неверно: `@ExtendWith` применима к классу, методу и meta-аннотации; на методе расширение активно только для этого теста. ❌ ПОСЛЕДСТВИЕ: разработчик тащит тяжёлый `@ExtendWith(SpringExtension.class)` на весь класс ради одного теста → каждый юнит-тест поднимает Spring контекст, прогон класса 2 минуты вместо 5 секунд.

## Q10. Что такое Conditional Test Execution?

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
> - [ ] `@Disabled` и `assumeTrue(...)` идентичны: оба пропускают тест с пометкой `skipped` | Не равны: `@Disabled` — статический skip (всегда), `assumeTrue` — runtime check, при `false` тест в статусе `aborted`, не `skipped`. ❌ ПОСЛЕДСТВИЕ: команда отслеживает `skipped > 0` как алерт качества → `assumeTrue(env.equals("prod"))` молча отключает 100 тестов в dev, метрика не срабатывает.
> - [x] `@EnabledOnOs`, `@EnabledForJreRange`, `@EnabledIfEnvironmentVariable`, `@EnabledIf("method")` пропускают тест ДО запуска по условию; `Assumptions.assumeTrue` — внутри теста с `aborted` | Conditional аннотации проверяются JUnit платформой, не запускают тело теста; assumptions работают изнутри. ✓ ПРИМЕНЯТЬ: Discord миграция на ScyllaDB использует `@EnabledIfEnvironmentVariable(named="CASSANDRA_HOST")` для тестов, требующих живого кластера в integration suite. 📋 ПРАВИЛО: «Enabled* — на входе, assumeTrue — внутри». 🔗 См. Q12, Q13.
> - [ ] `@EnabledOnOs(OS.LINUX)` запускает тест только на Linux, на других ОС бросает `AssertionError` или `TestAbortedException` | Неверно: на не-Linux тест переходит в статус `disabled` с reason "Disabled on operating system: Mac OS X", не падает и не считается failure. ❌ ПОСЛЕДСТВИЕ: разработчик ожидал явную ошибку на macOS-агентах CI → `@EnabledOnOs` молча пропустила тест, фейк-зелёный билд, баг линукс-специфичной логики уехал в prod.
> - [ ] `assumeTrue` останавливает тест с `failed`-статусом, как `assertTrue(false)` останавливает с failure | Неверно: `assumeTrue(false)` даёт `aborted` (skip), а не `failed`; это ключевое отличие assumptions от assertions, и оно влияет на мониторинг качества. ❌ ПОСЛЕДСТВИЕ: команда использует `assumeTrue` для проверки бизнес-предусловия → реальный баг прячется под `aborted`, мониторинг тестов на `failed` ничего не видит, регресс в prod через неделю.

## Q11. Как работает AssertJ и чем он лучше встроенных assertions JUnit?

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
> - [ ] `assertThat(list).contains(x)` из AssertJ работает так же как `assertTrue(list.contains(x))` из JUnit, разница только в синтаксисе | Не только синтаксис: AssertJ при провале выводит весь `list` и ожидаемое значение с diff, JUnit показывает голое `expected true but was false`. ❌ ПОСЛЕДСТВИЕ: тест упал в CI с `expected true was false`, разработчик 30 минут в логах ищет какой элемент отсутствовал, AssertJ показал бы это сразу.
> - [ ] AssertJ `assertThat` нельзя комбинировать с `assertThrows` JUnit, используют только один стиль | Неверно: AssertJ имеет свой `assertThatThrownBy(() -> ...)` и `assertThatExceptionOfType(X.class).isThrownBy(...)`; JUnit `assertThrows` тоже работает. ❌ ПОСЛЕДСТВИЕ: команда переписывает все `assertThrows` ради единообразия → лишний рефакторинг 200 тестов вместо точечной замены там, где нужны цепочки `.hasMessageContaining(...).hasCauseInstanceOf(...)`.
> - [x] AssertJ — fluent API с цепочкой ассертов на одном объекте, типизированными матчерами для коллекций/`BigDecimal`/`LocalDate`, `extracting`/`satisfies` для глубоких проверок и красивыми diff-сообщениями | Цепочка `assertThat(x).isNotNull().hasSize(3).extracting(...)` короче и информативнее `assertEquals` в 5 строк. ✓ ПРИМЕНЯТЬ: Spring Boot reference тестов использует AssertJ как default; Wolt order-service проверяет `assertThat(order).hasFieldOrPropertyWithValue("status", PAID)`. 📋 ПРАВИЛО: «AssertJ = цепочка + diff на падении, JUnit = атом». 🔗 См. Q4, Q5.
> - [ ] AssertJ требует отдельного JUnit Engine для запуска, поэтому несовместим с `junit-jupiter-engine` и нужен `assertj-engine` | Неверно: AssertJ — обычная библиотека ассертов, не движок; работает с любым тест-фреймворком (JUnit 4/5, TestNG, Spock) без своих engine. ❌ ПОСЛЕДСТВИЕ: разработчик добавил несуществующий `assertj-engine` в Gradle → конфликт версий, билд красный с `Could not resolve org.assertj:assertj-engine`, два часа на разбор зависимостей.

## Q12. Как параллельно выполнять тесты?

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
> - [ ] `@Execution(CONCURRENT)` на классе достаточно — параллельность включена по умолчанию | Неверно: нужен ещё `junit.jupiter.execution.parallel.enabled=true` в `junit-platform.properties`; без флага параллелизм отключён. ❌ ПОСЛЕДСТВИЕ: команда поставила `@Execution(CONCURRENT)`, ждёт ускорения → прогон CI всё те же 30 минут, флага не заметили, две недели "оптимизации" впустую.
> - [ ] При параллельном запуске JUnit автоматически синхронизирует доступ к статическим полям | Неверно: JUnit не делает магии — статические `Map`/`AtomicInteger`/cached-resources разделяются между потоками без синхронизации. ❌ ПОСЛЕДСТВИЕ: тесты с общим `static counter` падают флаки в CI из-за race-condition, локально зелёные → "флаки тесты" обвиняют без причины.
> - [ ] `@ResourceLock("db")` блокирует базу данных на уровне Postgres | Неверно: `@ResourceLock` — это in-process semaphore JUnit, на реальную БД не влияет; нужен либо изолированный `@Sql` cleanup, либо отдельный schema на тест. ❌ ПОСЛЕДСТВИЕ: команда понадеялась, что `@ResourceLock("db")` спасёт от data races на shared Postgres → данные тестов перемешаны, тесты флаки только в CI с параллелизмом.
> - [x] Параллелизм включается через `parallel.enabled=true` + стратегию (`fixed`/`dynamic`); `@Execution(SAME_THREAD)` исключает тест, `@ResourceLock` сериализует тесты с общим in-process ресурсом | Не магия: shared state, статика, БД, файлы — потенциальные race; `ResourceLock` решает только in-process. ✓ ПРИМЕНЯТЬ: Spring Boot Reference прогоняет 5K тестов с `parallelism=4 fixed` + `@ResourceLock("system-property")` для меняющих `System.setProperty`. 📋 ПРАВИЛО: «флаг + стратегия + ResourceLock». 🔗 См. Q2, Q14.

## Q13. Как Spring Boot интегрируется с JUnit 5?

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
> - [ ] `@SpringBootTest` и `@WebMvcTest` поднимают полный контекст приложения, отличие только в имени | Неверно: `@SpringBootTest` поднимает ВЕСЬ контекст; `@WebMvcTest` — только web-слой (Controllers, Filters), без `@Service`/`@Repository`. ❌ ПОСЛЕДСТВИЕ: разработчик использовал `@SpringBootTest` для теста контроллера → каждый тест поднимает БД + Kafka + Redis, прогон класса 90 секунд вместо 3 секунд с `@WebMvcTest`.
> - [x] `@SpringBootTest` поднимает полный контекст; срезы `@WebMvcTest`, `@DataJpaTest`, `@JsonTest` поднимают только нужный слой; `SpringExtension` подключается автоматически через мета-аннотацию | Срезы кэшируют контекст по конфигурации, переиспользуют между тестами через `@DirtiesContext` rules. ✓ ПРИМЕНЯТЬ: Booking.com и Wolt используют `@DataJpaTest` для repository-тестов с H2/Testcontainers, экономя минуты на CI. 📋 ПРАВИЛО: «полный контекст — @SpringBootTest, срез — точечная аннотация». 🔗 См. Q7, Q9.
> - [ ] `@MockBean` создаёт Mockito-mock в текущем JVM, но не подменяет бин в Spring контексте | Неверно: `@MockBean` именно подменяет бин в `ApplicationContext`, autowired-зависимости получают mock; именно это делает его особенным. ❌ ПОСЛЕДСТВИЕ: разработчик обмазал тест `@Mock` вместо `@MockBean` → реальный `@Service` бин остался в контексте, тест ходит в БД, флаки + 10x медленнее.
> - [ ] `@DataJpaTest` НЕ откатывает транзакцию после теста, изменения остаются в БД | Наоборот: `@DataJpaTest` оборачивает каждый тест в транзакцию и откатывает её, обеспечивая изоляцию. ❌ ПОСЛЕДСТВИЕ: команда полагала, что `@DataJpaTest` сохраняет данные → следующий тест ждёт seed данных, но их нет → `EntityNotFoundException`, час дебага.

## Q14. Что такое Test Lifecycle и PER_CLASS vs PER_METHOD?

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
> - [ ] `PER_METHOD` (default) — один инстанс класса на ВСЕ тесты, поля шарятся; `PER_CLASS` — новый инстанс на каждый | Перепутаны лейблы наоборот: `PER_METHOD` создаёт новый инстанс на каждый тест, `PER_CLASS` — один на все. ❌ ПОСЛЕДСТВИЕ: разработчик ожидал шаринг состояния через поля при `PER_METHOD` → каждое поле сбрасывается на default, тесты падают непредсказуемо.
> - [ ] `@TestInstance(PER_CLASS)` автоматически делает тесты thread-safe для параллельного запуска | Неверно: `PER_CLASS` именно ОПАСЕН в параллельных тестах — общий инстанс шарит состояние полей между потоками. ❌ ПОСЛЕДСТВИЕ: команда включила `PER_CLASS` + `@Execution(CONCURRENT)` ради скорости → race на полях класса, флаки тесты только в CI, недели охоты на гост-баг.
> - [x] `PER_METHOD` (default) — новый инстанс класса на каждый `@Test` (изоляция); `PER_CLASS` — один инстанс на класс, разрешает нестатические `@BeforeAll`, экономит дорогой setup (Spring, Testcontainers), плата — общий state | Дешёвые тесты — `PER_METHOD`; дорогой setup — `PER_CLASS` + ручная очистка полей. ✓ ПРИМЕНЯТЬ: Spring Cloud Stream поднимает Kafka один раз через `PER_CLASS` для 50 тестов класса. 📋 ПРАВИЛО: «PER_METHOD изолирует, PER_CLASS экономит и чистит руками». 🔗 См. Q2, Q12.
> - [ ] `@BeforeAll` в `PER_CLASS` обязан быть `static`, как в `PER_METHOD` | Неверно: главная причина переключения на `PER_CLASS` — возможность нестатических `@BeforeAll`/`@AfterAll`, чтобы они видели поля инстанса и могли использовать DI-зависимости. ❌ ПОСЛЕДСТВИЕ: оставили `static @BeforeAll` после переключения → не получают доступ к полям инстанса, обходят это лишними `static` зависимостями, теряют преимущество `PER_CLASS`.

## Q15. Какие типичные ошибки в юнит-тестах?

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


> [!mcq]
> - [ ] Тестирование приватных методов через рефлексию даёт более полное покрытие, чем через публичный API | Наоборот: тестирование private делает тест хрупким — рефакторинг ломает тест без изменения поведения. ❌ ПОСЛЕДСТВИЕ: команда тестирует приватные методы через `ReflectionTestUtils` → переименование внутреннего метода ломает 50 тестов, два дня правок при нулевом изменении поведения.
> - [ ] `verify(repository).save(...)` всегда лучше, чем `assertThat(result)`, потому что точно проверяет вызовы | Не всегда: проверка вызовов тестирует РЕАЛИЗАЦИЮ; проверка состояния/результата — ПОВЕДЕНИЕ, более устойчиво к рефакторингу. ❌ ПОСЛЕДСТВИЕ: переход с `save()` на `saveAll()` для batch ломает 30 тестов с `verify(...).save(...)`, хотя поведение системы идентично.
> - [ ] Юнит-тест может ходить в реальную БД через `DriverManager.getConnection`, если БД in-memory | Это уже интеграционный тест, не юнит: зависит от внешнего ресурса, тормозит, не воспроизводим без env. ❌ ПОСЛЕДСТВИЕ: "юнит" suite в CI заваливается из-за PostgreSQL версии в Docker, тесты прогоняются 8 минут вместо 30 секунд, разработчик пропускает запуск локально.
> - [x] Юнит-тест проверяет ПОВЕДЕНИЕ через публичный API, минимум моков, следует Arrange-Act-Assert и не ходит в БД/сеть/время; интеграцию выносят в отдельный suite с `@SpringBootTest`/Testcontainers | Тесты с десятками моков и `verify(...)` сигналят о неправильном слое или плохом дизайне. ✓ ПРИМЕНЯТЬ: Netflix и Booking.com разделяют `unit/`, `integration/`, `e2e/` через `@Profile`, юниты идут за 1-2 минуты. 📋 ПРАВИЛО: «юнит — поведение по AAA». 🔗 См. Q7, Q13.

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
