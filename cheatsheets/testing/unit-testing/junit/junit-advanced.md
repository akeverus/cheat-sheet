---
title: "JUnit Advanced для Java"
description: "Продвинутые техники тестирования с JUnit 5: расширения (extensions), динамические и параметризованные тесты, жизненный цикл, параллельный запуск, интеграция с Spring Boot и Testcontainers."
tags:
  - testing
  - unit-testing
  - junit-advanced
type: "reference"
difficulty: "intermediate"
aliases:
  - "JUnit Advanced для Java"
  - "junit advanced"
prerequisites: []
next:
  - "[[mockito-advanced]]"
updated: "2026-04-20"
---
# JUnit Advanced для Java

Продвинутые техники тестирования с JUnit 5: расширения (extensions), динамические и параметризованные тесты, жизненный цикл, параллельный запуск, интеграция с Spring Boot и Testcontainers.

**Дата:** 2026-02-06

## Полезные ссылки

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) — документация
- [JUnit 5 API](https://junit.org/junit5/docs/current/api/) — API
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers](https://www.testcontainers.org/)

**См. также:** [Mockito Advanced](mockito-advanced.md), [assertj](assertj.md), [Spring Testing](../../../frameworks/java-frameworks/spring/spring-testing.md)

## Содержание

- [Жизненный цикл тестов](#жизненный-цикл-тестов)
- [Extensions API](#extensions-api)
- [Параметризованные тесты](#параметризованные-тесты)
- [Динамические тесты](#динамические-тесты)
- [Вложенные тесты](#вложенные-тесты)
- [Условное выполнение](#условное-выполнение)
- [Параллельный запуск](#параллельный-запуск)
- [Интеграция Spring Boot](#интеграция-spring-boot)
- [Testcontainers](#testcontainers)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [FAQ](#faq)
- [Заключение](#заключение)
- [См. также](#см-также)

## Жизненный цикл тестов

Аннотации порядка выполнения:

| Аннотация   | Когда выполняется |
|------------|-------------------|
| `@BeforeAll`  | Один раз перед всеми тестами класса (статический метод) |
| `@BeforeEach` | Перед каждым тестовым методом |
| `@AfterEach`  | После каждого тестового метода |
| `@AfterAll`   | Один раз после всех тестов класса (статический метод) |

**TestInfo** и **TestReporter** позволяют получать метаданные теста и публиковать данные в отчёты.

Пример:

```java
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @BeforeAll
    static void setupAll() {
        // Один раз перед всеми тестами
    }

    @BeforeEach
    void setup(TestInfo testInfo) {
        // Перед каждым тестом
    }

    @Test
    void testCreateUser(TestInfo testInfo, TestReporter reporter) {
        reporter.publishEntry("key", "value");
        User user = userService.createUser("a@b.com", "Name");
        assertNotNull(user.getId());
    }

    @AfterEach
    void tearDown() {}
    @AfterAll
    static void tearDownAll() {}
}
```


## Extensions API

Подключение расширений:

- **@ExtendWith(ExtensionClass.class)** — объявление на классе или методе.
- **@RegisterExtension** — программная регистрация (поле `static` или `instance`).

Пример встроенного расширения и своего (замер времени):

```java
@ExtendWith(TimingExtension.class)
@SpringBootTest
public class ServiceTest {

    @Test
    void testOperation() {
        userService.createUser("a@b.com", "Name");
        assertNotNull(userService.findByEmail("a@b.com"));
    }
}

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Map<String, Long> start = new ConcurrentHashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext ctx) {
        start.put(ctx.getDisplayName(), System.nanoTime());
    }

    @Override
    public void afterTestExecution(ExtensionContext ctx) {
        long ns = System.nanoTime() - start.remove(ctx.getDisplayName());
        System.out.printf("Test '%s' took %.2f ms%n", ctx.getDisplayName(), ns / 1_000_000.0);
    }
}
```


## Параметризованные тесты

Один тест — много наборов данных. Источники аргументов:

| Аннотация      | Назначение |
|----------------|------------|
| `@ValueSource` | Примитивы, строки, классы |
| `@EnumSource`  | Значения enum |
| `@CsvSource`   | Строки CSV (удобно для нескольких аргументов) |
| `@MethodSource`| Метод, возвращающий `Stream<Arguments>` |

Один пример с `@CsvSource` и `@MethodSource`:

```java
    @ParameterizedTest
@CsvSource({
    "100.00, USD, SUCCESS",
    "0.00, USD, FAILED"
})
void testPayment(BigDecimal amount, String currency, String expectedStatus) {
    PaymentResult r = paymentService.process(new PaymentRequest(amount, currency));
    assertEquals(expectedStatus, r.getStatus());
    }

    @ParameterizedTest
@MethodSource("orderData")
void testOrderTotal(List<OrderItem> items, BigDecimal expectedTotal) {
    assertEquals(expectedTotal, orderService.calculateTotal(items));
}

static Stream<Arguments> orderData() {
        return Stream.of(
        Arguments.of(List.of(new OrderItem("A", BigDecimal.TEN, 2)), BigDecimal.valueOf(20)),
        Arguments.of(List.of(new OrderItem("B", BigDecimal.valueOf(5), 1)), BigDecimal.valueOf(5))
    );
}
```


## Динамические тесты

Тесты генерируются во время выполнения: **@TestFactory** возвращает `Stream<DynamicTest>`, `Collection<DynamicTest>` или `Iterable<DynamicTest>`.

Пример:

```java
    @TestFactory
    Stream<DynamicTest> testUserCreation() {
    return Stream.of("a@b.com", "b@c.com")
        .map(email -> DynamicTest.dynamicTest(
            "Create: " + email,
                () -> {
                User u = userService.createUser(email, "User");
                assertNotNull(u.getId());
                assertEquals(email, u.getEmail());
                }
            ));
    }

    @TestFactory
DynamicContainer testLifecycle() {
    return DynamicContainer.dynamicContainer("User lifecycle",
        Stream.of(
            DynamicTest.dynamicTest("Create", () -> { /* ... */ }),
            DynamicTest.dynamicTest("Update", () -> { /* ... */ }),
            DynamicTest.dynamicTest("Delete", () -> { /* ... */ })
        ));
}
```


## Вложенные тесты

**@Nested** — вложенные классы с тестами для группировки и общего контекста. Вложенные классы создаются заново для каждого внешнего теста (по умолчанию), что даёт изоляцию.

```java
@SpringBootTest
public class UserServiceNestedTest {

    @Autowired
    private UserService userService;
    private User testUser;

    @BeforeEach
    void setup() {
        testUser = userService.createUser("nested@example.com", "Nested");
    }

    @Nested
    @DisplayName("Создание пользователя")
    class Create {
        @Test
        void shouldCreateWithValidData() {
            User u = userService.createUser("a@b.com", "Name");
            assertNotNull(u.getId());
        }
        @Test
        void shouldFailWithInvalidEmail() {
            assertThrows(InvalidEmailException.class,
                () -> userService.createUser("invalid", "Name"));
        }
    }

    @Nested
    @DisplayName("Обновление")
    class Update {
        @Test
        void shouldUpdateName() {
            testUser.setName("New");
            User u = userService.updateUser(testUser);
            assertEquals("New", u.getName());
        }
    }
}
```


## Условное выполнение

Запуск или пропуск теста в зависимости от окружения или условий:

- **@EnabledIf("methodName")** / **@DisabledIf("methodName")** — метод возвращает `boolean`.
- **@EnabledOnOs(OS.LINUX)** / **@DisabledOnOs(OS.WINDOWS)**.
- **@EnabledOnJre(JRE.JAVA_17)** / **@DisabledIfSystemProperty** и др.

```java
    @Test
@EnabledIf("isDbAvailable")
void testWithDatabase() {
    User u = userService.createUser("a@b.com", "Name");
    assertNotNull(u.getId());
    }

    @Test
    @EnabledOnJre(JRE.JAVA_17)
void testJava17Feature() {
    assertTrue(service.usesJava17Feature());
}

boolean isDbAvailable() {
        try {
            userService.pingDatabase();
            return true;
        } catch (Exception e) {
            return false;
    }
}
```


## Параллельный запуск

В **src/test/resources/junit-platform.properties**:

```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.strategy=dynamic
```

Блокировка общих ресурсов — **@ResourceLock** (из `junit-platform-concurrency`):

```java
    @Test
    @ResourceLock(value = "database", mode = ResourceAccessMode.READ_WRITE)
void testWrite() {
    sharedService.write();
    }

    @Test
    @ResourceLock(value = "database", mode = ResourceAccessMode.READ)
void testRead() {
    sharedService.read();
}
```


## Интеграция Spring Boot

- **@SpringBootTest** — поднимает полный контекст приложения. Для интеграционных сценариев.
- **Срезы (test slices)** — только нужный слой:
  - **@WebMvcTest(Controller.class)** — только контроллер и MVC.
  - **@DataJpaTest** — только JPA и репозитории.
  - **@RestClientTest** — REST-клиент и мок сервера.
- **@TestConfiguration** + **@Import** — своя конфигурация для тестов (например, подмена бинов).

Пример среза контроллера:

```java
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        when(userService.findById(1L)).thenReturn(new User(1L, "a@b.com", "Name"));
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("a@b.com"));
    }
}
```


## Testcontainers

Запуск реальной БД (или других сервисов) в контейнерах для интеграционных тестов.

```java
@SpringBootTest
@Testcontainers
public class DatabaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void testPersistence() {
        User u = new User("a@b.com", "Name");
        userRepository.save(u);
        assertNotNull(u.getId());
        assertEquals("a@b.com", userRepository.findById(u.getId()).orElseThrow().getEmail());
    }
}
```

Несколько контейнеров — объявить несколько полей с **@Container** и зарегистрировать их свойства через **@DynamicPropertySource**.


## Лучшие практики

- **Организация:** вложенные тесты (`@Nested`) и понятные `@DisplayName`; отдельные пакеты для unit/integration/e2e.
- **Данные:** изолированные и повторяемые данные; общий builder или хелпер в test-пакете; при необходимости `@Transactional` для отката после теста.
- **Имена:** единый стиль, например `shouldDoX_whenY` или Given-When-Then в одном методе.
- **Изоляция:** не полагаться на порядок тестов; каждый тест сам подготавливает данные или использует `@BeforeEach`; для тяжёлого контекста — срезы вместо полного `@SpringBootTest`.
- **Расширения:** выносить общую логику (замер времени, повтор при падении, логирование) в extensions.
- **Условный запуск:** отключать нерелевантные тесты по окружению (`@EnabledIf`/`@DisabledIf`), чтобы CI и локальный запуск были быстрыми и предсказуемыми.


## Решение проблем

| Симптом | Возможная причина | Что сделать |
|--------|-------------------|-------------|
| Тесты не запускаются | JUnit 5 не подключён или не используется | `mvn test -Dtest=YourTest`; проверить `junit-jupiter` и `junit-platform-launcher`; для Gradle — `useJUnitPlatform()` |
| `@Autowired` равен null | Нет контекста Spring | Добавить `@SpringBootTest` или нужный срез (`@WebMvcTest`, `@DataJpaTest` и т.д.); для чистых unit-тестов создавать сервис вручную в `@BeforeEach` |
| Зависимость тестов друг от друга | Нестабильный порядок выполнения | Сделать тесты независимыми: своя подготовка данных в каждом тесте или в `@BeforeEach`; при необходимости `@TestMethodOrder` и `@Order`, но предпочтительна независимость |
| Долгий запуск | Полный контекст везде | Использовать срезы вместо `@SpringBootTest`; параллельный запуск; отключать тяжёлые тесты по тегам в CI |

**Отладка:** логирование в `@BeforeEach`/`@AfterEach`, `TestInfo`/`TestReporter`; проверка активного профиля и свойств (например, `spring.datasource.url`) в тестовой конфигурации.


## FAQ

**Когда использовать JUnit 5 Advanced?**
При сложных сценариях (параметризация, динамические тесты), интеграции с Spring Boot/Testcontainers, больших наборах тестов (параллельный запуск, условное выполнение), необходимости своих расширений и отчётов.

**Достаточно ли базового JUnit для простых unit-тестов?**
Да. Расширения, параметризация и динамические тесты имеют смысл при росте набора тестов и интеграционных сценариях.

**Где актуальная документация?**
[JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/), [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing) — см. блок «Полезные ссылки» в начале документа.


## Заключение

JUnit 5 Advanced даёт расширяемую модель тестов (Extensions API), параметризацию и динамическую генерацию тестов, вложенную структуру, условное и параллельное выполнение, удобную интеграцию с Spring Boot и Testcontainers. Это удобный выбор для сложных и интеграционных сценариев; для простых unit-тестов достаточно возможностей базового JUnit 5.

**Далее:** [Mockito Advanced](mockito-advanced.md) — продвинутые приёмы мокирования.

## См. также

- [AssertJ для Java](assertj.md)
- [Hamcrest для Java](hamcrest.md)
- [JUnit 5](junit.md)
- [Mockito Advanced для Java](mockito-advanced.md)
- [Mockito](mockito.md)
