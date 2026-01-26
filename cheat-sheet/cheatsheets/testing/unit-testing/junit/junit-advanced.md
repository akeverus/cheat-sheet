# JUnit Advanced для Java

Комплексное руководство по продвинутым техникам тестирования с JUnit 5: extensions, dynamic tests, parameterized tests, test lifecycle, parallel execution и интеграция с Spring Boot.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Официальная документация
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) - Основная документация
- [JUnit 5 API](https://junit.org/junit5/docs/current/api/) - API документация
- [JUnit 5 Extensions](https://junit.org/junit5/docs/current/user-guide/#extensions) - Расширения

### Spring Boot интеграция
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing) - Spring Boot testing
- [Testcontainers](https://www.testcontainers.org/) - Контейнеры для тестов
- [@SpringBootTest](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/SpringBootTest.html) - Spring Boot тесты

### Best practices
- [Testing on the Toilet](https://testing.googleblog.com/) - Google testing blog
- [JUnit Best Practices](https://phauer.com/2019/modern-best-practices-testing-java/) - Современные практики
- [Test-Driven Development](https://martinfowler.com/bliki/TestDrivenDevelopment.html) - TDD

### См. также
- `testing/mockito-advanced.md` - Mockito для mocking
- `testing/assertj.md` - Fluent assertions
- `testing/spring-testing.md` - Spring testing

## Содержание

- [Введение в JUnit 5](#введение-в-junit-5)
- [Test lifecycle](#test-lifecycle)
- [Extensions API](#extensions-api)
- [Parameterized tests](#parameterized-tests)
- [Dynamic tests](#dynamic-tests)
- [Nested tests](#nested-tests)
- [Conditional test execution](#conditional-test-execution)
- [Parallel execution](#parallel-execution)
- [Spring Boot integration](#spring-boot-integration)
- [Testcontainers integration](#testcontainers-integration)
- [Custom extensions](#custom-extensions)
- [Performance testing](#performance-testing)
- [Test reporting](#test-reporting)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в JUnit 5

**JUnit 5** — это новая генерация JUnit framework, полностью переписанная для поддержки современных подходов к тестированию. JUnit 5 состоит из трех основных модулей: JUnit Platform, JUnit Jupiter и JUnit Vintage.

### Архитектура JUnit 5

```
┌─────────────────────────────────────────────────────────┐
│                    JUnit Platform                       │
│  ┌─────────────────────────────────────────────────┐    │
│  │ TestEngine API                                  │    │
│  │ - Discovery                                      │    │
│  │ - Execution                                       │    │
│  │ - Reporting                                       │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                          │
                 ┌────────┴────────┐
                 ▼                 ▼
        ┌─────────────────┐ ┌─────────────────┐
        │ JUnit Jupiter   │ │ JUnit Vintage   │
        │ (JUnit 5)       │ │ (JUnit 3/4)     │
        │ - @Test         │ │ Compatibility   │
        │ - @BeforeEach   │ │ Layer           │
        │ - Extensions    │ │                 │
        │ - Assertions    │ └─────────────────┘
        └─────────────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Test Code       │
        │ - Test Classes  │
        │ - Test Methods  │
        │ - Extensions    │
        └─────────────────┘
```

### Maven зависимости

**JUnit 5** состоит из нескольких модулей, каждый из которых отвечает за определенную функциональность. Правильная настройка зависимостей гарантирует корректную работу всех возможностей фреймворка.

#### Основные зависимости JUnit 5

**Базовый набор для JUnit Jupiter:**
```xml
<!-- JUnit Jupiter - основной engine для JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>

<!-- JUnit Platform - основа для запуска тестов -->
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-launcher</artifactId>
    <version>1.10.0</version>
    <scope>test</scope>
</dependency>

<!-- JUnit Vintage - для поддержки JUnit 3/4 (опционально) -->
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <version>5.10.0</version>
    <scope>test</scope>
</dependency>
```

**Почему именно эти зависимости:**

1. **junit-jupiter** — Содержит все основные аннотации (@Test, @BeforeEach) и assertions
2. **junit-platform-launcher** — Позволяет запускать тесты из IDE и build tools
3. **junit-vintage-engine** — Обеспечивает обратную совместимость с JUnit 4

**Spring Boot интеграция:**
```xml
<!-- Spring Boot Starter Test - включает JUnit 5 + другие testing libraries -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!-- Версия управляется Spring Boot parent -->
</dependency>
```

**Что включает spring-boot-starter-test:**
- **JUnit 5** — Основной testing framework
- **Spring Test** — Интеграция с Spring контекстом
- **Mockito** — Для создания mock объектов
- **AssertJ** — Fluent assertions
- **Hamcrest** — Matcher-based assertions
- **JSONAssert** — Для тестирования JSON
- **JsonPath** — XPath-подобный синтаксис для JSON

**Дополнительные полезные зависимости:**
```xml
<!-- Testcontainers для интеграционных тестов -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.18.3</version>
    <scope>test</scope>
</dependency>

<!-- REST Assured для API тестирования -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.3.2</version>
    <scope>test</scope>
</dependency>

<!-- WireMock для mocking HTTP -->
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.35.0</version>
    <scope>test</scope>
</dependency>
```

#### Gradle зависимости

**Для Gradle проектов:**
```gradle
dependencies {
    // JUnit 5
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

    // Spring Boot Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    // Testcontainers
    testImplementation 'org.testcontainers:junit-jupiter:1.18.3'

    // REST Assured
    testImplementation 'io.rest-assured:rest-assured:5.3.2'
}
```

**Конфигурация Test task в Gradle:**
```gradle
test {
    useJUnitPlatform()

    // Включаем только определенные теги
    includeTags 'unit', 'integration'

    // Исключаем определенные теги
    excludeTags 'slow'

    // Настраиваем JVM аргументы
    jvmArgs '-Xmx1g', '-XX:+HeapDumpOnOutOfMemoryError'

    // Настраиваем системные свойства
    systemProperty 'spring.profiles.active', 'test'

    // Отчеты
    reports {
        html.required = true
        junitXml.required = true
    }

    // Параллельное выполнение
    maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
}
```

#### Настройка IDE

**IntelliJ IDEA:**
1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. **Use Gradle from**: 'gradle-wrapper.properties' file
3. **Run tests using**: IntelliJ IDEA (для лучшей интеграции)

**Eclipse:**
1. **Help → Eclipse Marketplace**
2. **Find**: "JUnit 5"
3. **Install**: JUnit 5 Support

**VS Code:**
```json
{
    "java.test.config": {
        "name": "JUnit Jupiter",
        "type": "junit",
        "request": "launch",
        "mainClass": "org.junit.platform.console.ConsoleLauncher",
        "args": ["--scan-classpath"],
        "vmargs": ["-ea"]
    }
}
```

### Структура проекта

**Рекомендуемая структура для тестов:**
```
src/
├── main/java/
│   └── com/example/
│       ├── Application.java
│       ├── service/
│       │   ├── UserService.java
│       │   └── OrderService.java
│       └── repository/
│           └── UserRepository.java
└── test/java/
    └── com/example/
        ├── ApplicationTests.java
        ├── service/
        │   ├── UserServiceTest.java
        │   ├── UserServiceIntegrationTest.java
        │   └── OrderServiceTest.java
        ├── repository/
        │   └── UserRepositoryTest.java
        └── utils/
            └── TestDataBuilder.java
```

**Принципы организации:**
1. **Тесты рядом с кодом** — Test class для каждого production class
2. **Отдельные пакеты** — unit, integration, e2e тесты в разных пакетах
3. **Test utilities** — Общие helper классы в utils пакете
4. **Resource файлы** — Тестовые конфиги в src/test/resources

## Test lifecycle

### Lifecycle annotations

#### Basic lifecycle
```java
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @BeforeAll
    static void setupAll() {
        // Выполняется один раз перед всеми тестами в классе
        System.out.println("Setting up test environment");
    }

    @BeforeEach
    void setup() {
        // Выполняется перед каждым тестом
        // Инициализация тестовых данных
    }

    @Test
    void testCreateUser() {
        // Тестовый метод
        User user = userService.createUser("john@example.com", "John Doe");
        assertNotNull(user.getId());
    }

    @Test
    void testFindUser() {
        // Другой тестовый метод
        User user = userService.findById(1L);
        assertEquals("john@example.com", user.getEmail());
    }

    @AfterEach
    void tearDown() {
        // Выполняется после каждого теста
        // Очистка данных
    }

    @AfterAll
    static void tearDownAll() {
        // Выполняется один раз после всех тестов в классе
        System.out.println("Cleaning up test environment");
    }
}
```

#### Lifecycle с exceptions
```java
public class LifecycleExceptionTest {

    @BeforeAll
    static void setupAll() {
        try {
            // Setup that might fail
            initializeTestDatabase();
        } catch (Exception e) {
            // Log and rethrow or handle
            throw new RuntimeException("Failed to setup test environment", e);
        }
    }

    @BeforeEach
    void setup(TestInfo testInfo) {
        System.out.println("Running test: " + testInfo.getDisplayName());

        try {
            // Test-specific setup
            createTestData(testInfo.getTestMethod().get().getName());
        } catch (Exception e) {
            fail("Setup failed for test: " + testInfo.getDisplayName(), e);
        }
    }

    @Test
    void testWithCleanup() {
        // Test logic
        assertTrue(doSomething());

        // Cleanup in test if needed
        cleanupTestData();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        try {
            // Cleanup
            cleanupTestData();
        } catch (Exception e) {
            System.err.println("Cleanup failed for test: " + testInfo.getDisplayName());
        }
    }

    @AfterAll
    static void tearDownAll() {
        try {
            // Global cleanup
            destroyTestDatabase();
        } catch (Exception e) {
            System.err.println("Global cleanup failed");
        }
    }
}
```

### TestInfo и TestReporter

#### Test metadata
```java
@SpringBootTest
public class MetadataTest {

    @Autowired
    private TestReporter testReporter;

    @Test
    @DisplayName("Create user with valid data")
    @Tag("integration")
    void testCreateUser(TestInfo testInfo) {
        // Access test metadata
        String displayName = testInfo.getDisplayName();
        String testClass = testInfo.getTestClass().get().getSimpleName();
        String testMethod = testInfo.getTestMethod().get().getName();
        Set<String> tags = testInfo.getTags();

        testReporter.publishEntry("test.class", testClass);
        testReporter.publishEntry("test.method", testMethod);
        testReporter.publishEntry("test.tags", String.join(",", tags));

        // Test logic
        User user = userService.createUser("test@example.com", "Test User");

        // Report test results
        testReporter.publishEntry("user.id", user.getId().toString());
        testReporter.publishEntry("user.email", user.getEmail());
    }

    @Test
    void testWithCustomReporting(TestReporter testReporter) {
        testReporter.publishEntry("start.time", Instant.now().toString());

        try {
            // Test that might take time
            Thread.sleep(1000);
            assertTrue(true);

            testReporter.publishEntry("result", "SUCCESS");

        } catch (Exception e) {
            testReporter.publishEntry("result", "FAILED");
            testReporter.publishEntry("error", e.getMessage());
            throw e;
        } finally {
            testReporter.publishEntry("end.time", Instant.now().toString());
        }
    }
}
```

## Extensions API

### Built-in extensions

#### @ExtendWith
```java
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
public class LoggingTest {

    @Autowired
    private UserService userService;

    @Test
    void testLogging(CapturedOutput output) {
        userService.createUser("test@example.com", "Test User");

        // Verify logging output
        assertThat(output.getOut()).contains("User created");
        assertThat(output.getErr()).doesNotContain("ERROR");
    }
}
```

#### @RegisterExtension
```java
@SpringBootTest
public class DatabaseTest {

    @RegisterExtension
    static DatabaseExtension database = new DatabaseExtension();

    @Autowired
    private UserRepository userRepository;

    @Test
    void testDatabaseOperations() {
        // Database is set up by extension
        User user = new User("test@example.com", "Test User");
        userRepository.save(user);

        assertNotNull(user.getId());
    }
}

public class DatabaseExtension implements BeforeAllCallback, AfterAllCallback {

    private PostgreSQLContainer<?> postgres;

    @Override
    public void beforeAll(ExtensionContext context) {
        postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

        postgres.start();

        // Set system properties for Spring
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (postgres != null) {
            postgres.stop();
        }
    }
}
```

### Custom extensions

#### Timing extension
```java
public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Map<String, Long> executionTimes = new ConcurrentHashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String testName = context.getDisplayName();
        executionTimes.put(testName, System.nanoTime());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        String testName = context.getDisplayName();
        long startTime = executionTimes.remove(testName);
        long duration = System.nanoTime() - startTime;

        double durationMs = duration / 1_000_000.0;
        System.out.printf("Test '%s' took %.2f ms%n", testName, durationMs);

        // Store duration for reporting
        context.getStore(Namespace.create("timing"))
            .put("duration", durationMs);
    }

    public static double getDuration(ExtensionContext context) {
        return context.getStore(Namespace.create("timing"))
            .get("duration", Double.class);
    }
}

// Usage
@ExtendWith(TimingExtension.class)
@SpringBootTest
public class PerformanceTest {

    @Test
    void testSlowOperation() {
        // Some slow operation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Can access timing data if needed
        // double duration = TimingExtension.getDuration(extensionContext);
    }
}
```

#### Retry extension
```java
public class RetryExtension implements TestExecutionExceptionHandler {

    private final int maxRetries;

    public RetryExtension(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
            throws Throwable {

        for (int i = 1; i <= maxRetries; i++) {
            try {
                System.out.printf("Retrying test '%s' (attempt %d/%d)%n",
                    context.getDisplayName(), i + 1, maxRetries + 1);

                // Re-run the test
                Method testMethod = context.getTestMethod().get();
                Object testInstance = context.getTestInstance().get();

                testMethod.invoke(testInstance);

                // If successful, return
                return;

            } catch (Throwable t) {
                if (i == maxRetries) {
                    // All retries failed, throw original exception
                    throw throwable;
                }

                // Wait before retry
                try {
                    Thread.sleep(1000 * i); // Exponential backoff
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw throwable;
                }
            }
        }
    }
}

// Usage
@ExtendWith(RetryExtension.class)
@SpringBootTest
public class FlakyTest {

    @Test
    void testUnstableOperation() {
        // Test that might be flaky
        assertTrue(unstableOperation());
    }

    private boolean unstableOperation() {
        // Simulate occasional failure
        return Math.random() > 0.7;
    }
}
```

## Parameterized tests

### @ValueSource
```java
@SpringBootTest
public class UserValidationTest {

    @Autowired
    private UserService userService;

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "ab", "validemail@domain.com"})
    void testEmailValidation(String email) {
        boolean isValid = userService.isValidEmail(email);

        if (email.isEmpty() || email.length() < 3) {
            assertFalse(isValid, "Email should be invalid: " + email);
        } else if (email.contains("@")) {
            assertTrue(isValid, "Email should be valid: " + email);
        } else {
            assertFalse(isValid, "Email should be invalid: " + email);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 18, 19, 25, 65, 120})
    void testAgeValidation(int age) {
        boolean isValid = userService.isValidAge(age);

        if (age >= 18 && age <= 65) {
            assertTrue(isValid, "Age should be valid: " + age);
        } else {
            assertFalse(isValid, "Age should be invalid: " + age);
        }
    }
}
```

### @EnumSource
```java
public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED, DELETED
}

@SpringBootTest
public class UserStatusTest {

    @Autowired
    private UserService userService;

    @ParameterizedTest
    @EnumSource(UserStatus.class)
    void testStatusTransitions(UserStatus initialStatus) {
        User user = new User("test@example.com", "Test User");
        user.setStatus(initialStatus);

        for (UserStatus newStatus : UserStatus.values()) {
            if (initialStatus != newStatus) {
                // Test status transition
                boolean canTransition = userService.canTransitionTo(user, newStatus);
                assertNotNull(canTransition); // Some assertion based on business rules
            }
        }
    }

    @ParameterizedTest
    @EnumSource(value = UserStatus.class, names = {"ACTIVE", "INACTIVE"})
    void testActiveStatusOperations(UserStatus status) {
        User user = createUserWithStatus(status);

        // Test operations available for active/inactive users
        boolean canLogin = userService.canLogin(user);
        boolean canUpdateProfile = userService.canUpdateProfile(user);

        if (status == UserStatus.ACTIVE) {
            assertTrue(canLogin);
            assertTrue(canUpdateProfile);
        } else if (status == UserStatus.INACTIVE) {
            assertFalse(canLogin);
            assertTrue(canUpdateProfile);
        }
    }
}
```

### @MethodSource
```java
@SpringBootTest
public class OrderCalculationTest {

    @Autowired
    private OrderService orderService;

    @ParameterizedTest
    @MethodSource("orderCalculationData")
    void testOrderTotalCalculation(OrderCalculationData data) {
        Order order = createOrderWithItems(data.items);
        BigDecimal total = orderService.calculateTotal(order);

        assertEquals(data.expectedTotal, total);
    }

    @ParameterizedTest
    @MethodSource("discountData")
    void testDiscountApplication(BigDecimal originalPrice, BigDecimal discountPercent,
                               BigDecimal expectedPrice) {
        BigDecimal finalPrice = orderService.applyDiscount(originalPrice, discountPercent);
        assertEquals(expectedPrice, finalPrice);
    }

    static Stream<OrderCalculationData> orderCalculationData() {
        return Stream.of(
            new OrderCalculationData(
                List.of(new OrderItem("item1", BigDecimal.valueOf(10.0), 2)),
                BigDecimal.valueOf(20.0)
            ),
            new OrderCalculationData(
                List.of(
                    new OrderItem("item1", BigDecimal.valueOf(15.0), 1),
                    new OrderItem("item2", BigDecimal.valueOf(25.0), 1)
                ),
                BigDecimal.valueOf(40.0)
            ),
            new OrderCalculationData(
                List.of(new OrderItem("item1", BigDecimal.valueOf(100.0), 0)),
                BigDecimal.ZERO
            )
        );
    }

    static Stream<Arguments> discountData() {
        return Stream.of(
            Arguments.of(BigDecimal.valueOf(100.0), BigDecimal.valueOf(10.0), BigDecimal.valueOf(90.0)),
            Arguments.of(BigDecimal.valueOf(50.0), BigDecimal.valueOf(25.0), BigDecimal.valueOf(37.5)),
            Arguments.of(BigDecimal.valueOf(200.0), BigDecimal.ZERO, BigDecimal.valueOf(200.0))
        );
    }

    static class OrderCalculationData {
        final List<OrderItem> items;
        final BigDecimal expectedTotal;

        OrderCalculationData(List<OrderItem> items, BigDecimal expectedTotal) {
            this.items = items;
            this.expectedTotal = expectedTotal;
        }
    }
}
```

### @CsvSource
```java
@SpringBootTest
public class PaymentProcessingTest {

    @Autowired
    private PaymentService paymentService;

    @ParameterizedTest
    @CsvSource({
        "100.00, USD, SUCCESS, Payment processed successfully",
        "50.50, EUR, SUCCESS, Payment processed successfully",
        "0.00, USD, FAILED, Invalid payment amount",
        "-10.00, USD, FAILED, Invalid payment amount"
    })
    void testPaymentProcessing(BigDecimal amount, String currency,
                             String expectedStatus, String expectedMessage) {
        PaymentRequest request = new PaymentRequest(amount, currency);
        PaymentResult result = paymentService.processPayment(request);

        assertEquals(expectedStatus, result.getStatus());
        assertEquals(expectedMessage, result.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "visa, 4111111111111111, true",
        "mastercard, 5555555555554444, true",
        "amex, 378282246310005, true",
        "invalid, 1234567890123456, false"
    }, delimiter = ',')
    void testCardValidation(String cardType, String cardNumber, boolean expectedValid) {
        boolean isValid = paymentService.isValidCard(cardNumber);
        assertEquals(expectedValid, isValid);
    }
}
```

## Dynamic tests

### @TestFactory
```java
@SpringBootTest
public class DynamicUserTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @TestFactory
    Stream<DynamicTest> testUserCreation() {
        List<UserCreationData> testData = getUserCreationTestData();

        return testData.stream()
            .map(data -> DynamicTest.dynamicTest(
                "Create user: " + data.email,
                () -> {
                    User user = userService.createUser(data.email, data.name);

                    assertNotNull(user.getId());
                    assertEquals(data.email, user.getEmail());
                    assertEquals(data.name, user.getName());
                    assertEquals(UserStatus.ACTIVE, user.getStatus());
                }
            ));
    }

    @TestFactory
    Stream<DynamicTest> testUserValidation() {
        List<String> validEmails = List.of(
            "user@example.com",
            "test.email+tag@gmail.com",
            "user@subdomain.example.com"
        );

        List<String> invalidEmails = List.of(
            "",
            "invalid-email",
            "@example.com",
            "user@"
        );

        Stream<DynamicTest> validTests = validEmails.stream()
            .map(email -> DynamicTest.dynamicTest(
                "Valid email: " + email,
                () -> assertTrue(userService.isValidEmail(email))
            ));

        Stream<DynamicTest> invalidTests = invalidEmails.stream()
            .map(email -> DynamicTest.dynamicTest(
                "Invalid email: " + email,
                () -> assertFalse(userService.isValidEmail(email))
            ));

        return Stream.concat(validTests, invalidTests);
    }

    @TestFactory
    Stream<DynamicTest> testDatabaseOperations() {
        // Dynamic tests based on database state
        List<User> existingUsers = userRepository.findAll();

        if (existingUsers.isEmpty()) {
            return Stream.of(DynamicTest.dynamicTest(
                "No users in database",
                () -> {
                    // Create a test user
                    User user = userService.createUser("test@example.com", "Test User");
                    assertNotNull(user.getId());
                }
            ));
        } else {
            return existingUsers.stream()
                .map(user -> DynamicTest.dynamicTest(
                    "Verify user: " + user.getEmail(),
                    () -> {
                        User found = userService.findById(user.getId());
                        assertEquals(user.getEmail(), found.getEmail());
                        assertEquals(user.getName(), found.getName());
                    }
                ));
        }
    }

    private List<UserCreationData> getUserCreationTestData() {
        return List.of(
            new UserCreationData("john@example.com", "John Doe"),
            new UserCreationData("jane@example.com", "Jane Smith"),
            new UserCreationData("bob@example.com", "Bob Johnson")
        );
    }

    static class UserCreationData {
        final String email;
        final String name;

        UserCreationData(String email, String name) {
            this.email = email;
            this.name = name;
        }
    }
}
```

### Dynamic containers
```java
@SpringBootTest
public class DynamicContainerTest {

    @Autowired
    private UserService userService;

    @TestFactory
    DynamicContainer testUserLifecycle() {
        return DynamicContainer.dynamicContainer("User Lifecycle Tests",
            Stream.of(
                DynamicTest.dynamicTest("Create User", () -> {
                    User user = userService.createUser("lifecycle@example.com", "Lifecycle User");
                    assertNotNull(user.getId());
                }),

                DynamicContainer.dynamicContainer("User Operations",
                    Stream.of(
                        DynamicTest.dynamicTest("Update User", () -> {
                            User user = userService.findByEmail("lifecycle@example.com");
                            user.setName("Updated Name");
                            userService.updateUser(user);
                            assertEquals("Updated Name", user.getName());
                        }),

                        DynamicTest.dynamicTest("Find User", () -> {
                            User user = userService.findByEmail("lifecycle@example.com");
                            assertNotNull(user);
                            assertEquals("lifecycle@example.com", user.getEmail());
                        })
                    )
                ),

                DynamicTest.dynamicTest("Delete User", () -> {
                    User user = userService.findByEmail("lifecycle@example.com");
                    userService.deleteUser(user.getId());

                    assertThrows(UserNotFoundException.class, () -> {
                        userService.findById(user.getId());
                    });
                })
            )
        );
    }

    @TestFactory
    DynamicContainer testConfigurationBasedTests() {
        List<String> configurations = getTestConfigurations();

        return DynamicContainer.dynamicContainer("Configuration Tests",
            configurations.stream()
                .map(config -> DynamicContainer.dynamicContainer(
                    "Config: " + config,
                    Stream.of(
                        DynamicTest.dynamicTest("Load Config", () -> {
                            // Test loading configuration
                            assertNotNull(loadConfiguration(config));
                        }),

                        DynamicTest.dynamicTest("Validate Config", () -> {
                            Configuration cfg = loadConfiguration(config);
                            assertTrue(isValidConfiguration(cfg));
                        }),

                        DynamicTest.dynamicTest("Apply Config", () -> {
                            Configuration cfg = loadConfiguration(config);
                            applyConfiguration(cfg);
                            assertTrue(isConfigurationApplied(cfg));
                        })
                    )
                ))
        );
    }

    private List<String> getTestConfigurations() {
        return List.of("dev", "test", "prod");
    }

    private Configuration loadConfiguration(String config) {
        // Implementation
        return new Configuration(config);
    }

    private boolean isValidConfiguration(Configuration config) {
        // Implementation
        return true;
    }

    private void applyConfiguration(Configuration config) {
        // Implementation
    }

    private boolean isConfigurationApplied(Configuration config) {
        // Implementation
        return true;
    }
}
```

## Nested tests

### @Nested
```java
@SpringBootTest
public class UserServiceNestedTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setup() {
        // Create a test user for nested tests
        testUser = userService.createUser("nested@example.com", "Nested User");
    }

    @AfterEach
    void cleanup() {
        // Clean up test user
        if (testUser != null && testUser.getId() != null) {
            userRepository.deleteById(testUser.getId());
        }
    }

    @Nested
    @DisplayName("User Creation")
    class UserCreationTests {

        @Test
        @DisplayName("should create user with valid data")
        void shouldCreateUserWithValidData() {
            User user = userService.createUser("create@example.com", "Create User");

            assertNotNull(user.getId());
            assertEquals("create@example.com", user.getEmail());
            assertEquals("Create User", user.getName());
            assertEquals(UserStatus.ACTIVE, user.getStatus());
        }

        @Test
        @DisplayName("should fail with invalid email")
        void shouldFailWithInvalidEmail() {
            assertThrows(InvalidEmailException.class, () -> {
                userService.createUser("invalid-email", "Test User");
            });
        }

        @Test
        @DisplayName("should fail with duplicate email")
        void shouldFailWithDuplicateEmail() {
            userService.createUser("duplicate@example.com", "First User");

            assertThrows(DuplicateEmailException.class, () -> {
                userService.createUser("duplicate@example.com", "Second User");
            });
        }
    }

    @Nested
    @DisplayName("User Retrieval")
    class UserRetrievalTests {

        @Test
        @DisplayName("should find user by id")
        void shouldFindUserById() {
            User found = userService.findById(testUser.getId());

            assertNotNull(found);
            assertEquals(testUser.getId(), found.getId());
            assertEquals(testUser.getEmail(), found.getEmail());
        }

        @Test
        @DisplayName("should find user by email")
        void shouldFindUserByEmail() {
            User found = userService.findByEmail(testUser.getEmail());

            assertNotNull(found);
            assertEquals(testUser.getEmail(), found.getEmail());
        }

        @Test
        @DisplayName("should return null for non-existent user")
        void shouldReturnNullForNonExistentUser() {
            User found = userService.findById(999L);
            assertNull(found);
        }
    }

    @Nested
    @DisplayName("User Updates")
    class UserUpdateTests {

        @Test
        @DisplayName("should update user name")
        void shouldUpdateUserName() {
            testUser.setName("Updated Name");
            User updated = userService.updateUser(testUser);

            assertEquals("Updated Name", updated.getName());
        }

        @Test
        @DisplayName("should update user email")
        void shouldUpdateUserEmail() {
            testUser.setEmail("updated@example.com");
            User updated = userService.updateUser(testUser);

            assertEquals("updated@example.com", updated.getEmail());
        }

        @Nested
        @DisplayName("Email Validation")
        class EmailValidationTests {

            @Test
            @DisplayName("should reject invalid email format")
            void shouldRejectInvalidEmailFormat() {
                testUser.setEmail("invalid-email");

                assertThrows(InvalidEmailException.class, () -> {
                    userService.updateUser(testUser);
                });
            }

            @Test
            @DisplayName("should reject duplicate email")
            void shouldRejectDuplicateEmail() {
                // Create another user
                User anotherUser = userService.createUser("another@example.com", "Another User");

                // Try to update to existing email
                anotherUser.setEmail(testUser.getEmail());

                assertThrows(DuplicateEmailException.class, () -> {
                    userService.updateUser(anotherUser);
                });
            }
        }
    }

    @Nested
    @DisplayName("User Deletion")
    class UserDeletionTests {

        @Test
        @DisplayName("should delete existing user")
        void shouldDeleteExistingUser() {
            Long userId = testUser.getId();

            userService.deleteUser(userId);

            assertThrows(UserNotFoundException.class, () -> {
                userService.findById(userId);
            });
        }

        @Test
        @DisplayName("should not fail when deleting non-existent user")
        void shouldNotFailWhenDeletingNonExistentUser() {
            // Should not throw exception
            userService.deleteUser(999L);
        }
    }
}
```

## Conditional test execution

### @EnabledIf
```java
@SpringBootTest
public class ConditionalTest {

    @Autowired
    private UserService userService;

    @Test
    @EnabledIf("isDatabaseAvailable")
    void testDatabaseOperations() {
        // Test only runs if database is available
        User user = userService.createUser("conditional@example.com", "Conditional User");
        assertNotNull(user.getId());
    }

    @Test
    @EnabledIf("isExternalServiceAvailable")
    void testExternalServiceIntegration() {
        // Test only runs if external service is available
        boolean result = userService.syncWithExternalService();
        assertTrue(result);
    }

    @Test
    @EnabledIf("isProductionEnvironment")
    void testProductionOnlyFeatures() {
        // Test only runs in production
        boolean hasAdvancedFeatures = userService.hasAdvancedFeatures();
        assertTrue(hasAdvancedFeatures);
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void testLinuxSpecificFeatures() {
        // Test only runs on Linux
        String osSpecificPath = userService.getOsSpecificPath();
        assertTrue(osSpecificPath.startsWith("/"));
    }

    @Test
    @EnabledOnJre(JRE.JAVA_17)
    void testJava17Features() {
        // Test only runs on Java 17
        boolean usesJava17Features = userService.usesJava17Features();
        assertTrue(usesJava17Features);
    }

    // Condition methods
    boolean isDatabaseAvailable() {
        try {
            userService.pingDatabase();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    boolean isExternalServiceAvailable() {
        try {
            return userService.checkExternalServiceHealth();
        } catch (Exception e) {
            return false;
        }
    }

    boolean isProductionEnvironment() {
        return "production".equals(System.getProperty("environment"));
    }
}
```

### @DisabledIf
```java
@SpringBootTest
public class DisabledTest {

    @Autowired
    private UserService userService;

    @Test
    @DisabledIf("isDatabaseSlow")
    void testPerformanceCriticalOperations() {
        // Skip test if database is slow
        long startTime = System.nanoTime();
        userService.performComplexOperation();
        long duration = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(duration < 5000, "Operation should complete within 5 seconds");
    }

    @Test
    @DisabledIf("isExternalApiDown")
    void testExternalApiIntegration() {
        // Skip if external API is down
        boolean result = userService.callExternalApi();
        assertTrue(result);
    }

    @Test
    @DisabledIf("isMemoryLow")
    void testMemoryIntensiveOperations() {
        // Skip if memory is low
        userService.performMemoryIntensiveOperation();
        assertTrue(true);
    }

    @Test
    @DisabledOnOs({OS.WINDOWS, OS.MAC})
    void testLinuxOnlyFeatures() {
        // Only run on Linux
        String feature = userService.getLinuxSpecificFeature();
        assertNotNull(feature);
    }

    // Condition methods
    boolean isDatabaseSlow() {
        try {
            long startTime = System.nanoTime();
            userService.simpleDatabaseQuery();
            long duration = (System.nanoTime() - startTime) / 1_000_000;
            return duration > 1000; // Skip if simple query takes more than 1 second
        } catch (Exception e) {
            return true; // Skip if database is unavailable
        }
    }

    boolean isExternalApiDown() {
        try {
            return !userService.isExternalApiHealthy();
        } catch (Exception e) {
            return true; // Skip on any error
        }
    }

    boolean isMemoryLow() {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        double usedPercentage = ((double) (totalMemory - freeMemory) / totalMemory) * 100;
        return usedPercentage > 80; // Skip if more than 80% memory used
    }
}
```

## Parallel execution

### Parallel test execution
```xml
<!-- junit-platform.properties -->
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.strategy=dynamic
```

```java
@SpringBootTest
public class ParallelTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserCreation1() {
        User user = userService.createUser("parallel1@example.com", "Parallel User 1");
        assertNotNull(user.getId());
    }

    @Test
    void testUserCreation2() {
        User user = userService.createUser("parallel2@example.com", "Parallel User 2");
        assertNotNull(user.getId());
    }

    @Test
    void testUserCreation3() {
        User user = userService.createUser("parallel3@example.com", "Parallel User 3");
        assertNotNull(user.getId());
    }

    @Test
    void testUserCreation4() {
        User user = userService.createUser("parallel4@example.com", "Parallel User 4");
        assertNotNull(user.getId());
    }
}
```

### Resource locking
```java
@SpringBootTest
public class ResourceLockTest {

    @Autowired
    private SharedResourceService sharedService;

    @Test
    @ResourceLock(value = "database", mode = ResourceAccessMode.READ_WRITE)
    void testDatabaseWriteOperation() {
        // This test has exclusive access to database
        sharedService.performWriteOperation();
    }

    @Test
    @ResourceLock(value = "database", mode = ResourceAccessMode.READ)
    void testDatabaseReadOperation1() {
        // This test can run in parallel with other read operations
        sharedService.performReadOperation();
    }

    @Test
    @ResourceLock(value = "database", mode = ResourceAccessMode.READ)
    void testDatabaseReadOperation2() {
        // This test can run in parallel with other read operations
        sharedService.performReadOperation();
    }

    @Test
    @ResourceLock("external-api")
    void testExternalApiCall1() {
        // Exclusive access to external API
        sharedService.callExternalApi();
    }

    @Test
    @ResourceLock("external-api")
    void testExternalApiCall2() {
        // Exclusive access to external API
        sharedService.callExternalApi();
    }
}
```

## Spring Boot integration

### @SpringBootTest
```java
@SpringBootTest
public class FullApplicationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Test
    void testCompleteUserFlow() {
        // Create user
        User user = userService.createUser("flow@example.com", "Flow User");
        assertNotNull(user.getId());

        // Create order
        Order order = orderService.createOrder(user.getId(),
            List.of(new OrderItem("item1", BigDecimal.valueOf(10.0), 2)));
        assertNotNull(order.getId());

        // Process payment
        PaymentResult payment = paymentService.processPayment(order.getId());
        assertEquals("SUCCESS", payment.getStatus());

        // Verify order status
        Order updatedOrder = orderService.findById(order.getId());
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());
    }
}
```

### Test slices
```java
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        User user = new User(1L, "test@example.com", "Test User");
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("Test User"));
    }
}

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testFindByEmail() {
        User user = new User("test@example.com", "Test User");
        entityManager.persist(user);

        User found = userRepository.findByEmail("test@example.com");
        assertNotNull(found);
        assertEquals("test@example.com", found.getEmail());
    }
}

@RestClientTest(UserApiClient.class)
public class UserApiClientTest {

    @Autowired
    private UserApiClient apiClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void testGetUserFromApi() {
        String responseJson = "{\"id\":1,\"email\":\"api@example.com\",\"name\":\"API User\"}";
        server.expect(requestTo("/api/users/1"))
            .andRespond(withSuccess(responseJson, MediaType.APPLICATION_JSON));

        User user = apiClient.getUser(1L);

        assertNotNull(user);
        assertEquals("api@example.com", user.getEmail());
    }
}
```

### @TestConfiguration
```java
@SpringBootTest
public class TestConfigurationTest {

    @Autowired
    private UserService userService;

    @Test
    void testWithTestConfiguration() {
        // Test configuration is automatically applied
        User user = userService.createUser("config@example.com", "Config User");
        assertNotNull(user.getId());
    }
}

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public UserService testUserService() {
        return new TestUserService();
    }

    @Bean
    public TestDataInitializer testDataInitializer() {
        return new TestDataInitializer();
    }
}

class TestUserService implements UserService {

    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public User createUser(String email, String name) {
        Long id = idGenerator.incrementAndGet();
        User user = new User(id, email, name);
        users.put(id, user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    // Other methods...
}
```

## Testcontainers integration

### Basic Testcontainers
```java
@SpringBootTest
@Testcontainers
public class DatabaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testUserPersistence() {
        User user = new User("container@example.com", "Container User");
        userRepository.save(user);

        assertNotNull(user.getId());

        User found = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("container@example.com", found.getEmail());
    }
}
```

### Multiple containers
```java
@SpringBootTest
@Testcontainers
public class MultiContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Container
    static RedisContainer redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("6.2"))
        .withExposedPorts(6379);

    @Container
    static GenericContainer<?> rabbitMq = new GenericContainer<>("rabbitmq:3-management")
        .withExposedPorts(5672, 15672);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Database
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Redis
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);

        // RabbitMQ
        registry.add("spring.rabbitmq.host", rabbitMq::getHost);
        registry.add("spring.rabbitmq.port", rabbitMq::getFirstMappedPort);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void testMultiServiceIntegration() {
        // Test database
        User user = userService.createUser("multi@example.com", "Multi User");
        assertNotNull(user.getId());

        // Test Redis
        redisTemplate.opsForValue().set("test:key", "test-value");
        String value = redisTemplate.opsForValue().get("test:key");
        assertEquals("test-value", value);

        // Test RabbitMQ
        rabbitTemplate.convertAndSend("test-exchange", "test-key", "test-message");
        // Verify message was sent (implementation depends on test setup)
    }
}
```

## Custom extensions

### Audit extension
```java
public class AuditExtension implements BeforeAllCallback, AfterAllCallback,
                                       BeforeEachCallback, AfterEachCallback,
                                       BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger logger = LoggerFactory.getLogger(AuditExtension.class);
    private final Map<String, TestExecutionData> executionData = new ConcurrentHashMap<>();

    @Override
    public void beforeAll(ExtensionContext context) {
        logger.info("Starting test class: {}", context.getDisplayName());
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        String testName = context.getDisplayName();
        executionData.put(testName, new TestExecutionData(testName));
        logger.debug("Starting test: {}", testName);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String testName = context.getDisplayName();
        TestExecutionData data = executionData.get(testName);
        if (data != null) {
            data.setStartTime(System.nanoTime());
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        String testName = context.getDisplayName();
        TestExecutionData data = executionData.get(testName);
        if (data != null) {
            data.setEndTime(System.nanoTime());
            data.setDuration(data.getEndTime() - data.getStartTime());
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        String testName = context.getDisplayName();
        TestExecutionData data = executionData.get(testName);
        if (data != null) {
            logger.info("Test completed: {} - Duration: {} ms",
                testName, data.getDuration() / 1_000_000);
            executionData.remove(testName);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        logger.info("Completed test class: {}", context.getDisplayName());
        logTestStatistics();
    }

    private void logTestStatistics() {
        // Log overall statistics
        logger.info("Test execution summary: {} tests executed", executionData.size());
    }

    static class TestExecutionData {
        private final String testName;
        private long startTime;
        private long endTime;
        private long duration;

        public TestExecutionData(String testName) {
            this.testName = testName;
        }

        // Getters and setters
    }
}
```

### Performance monitoring extension
```java
public class PerformanceExtension implements BeforeTestExecutionCallback,
                                             AfterTestExecutionCallback {

    private static final Map<String, Long> startTimes = new ConcurrentHashMap<>();
    private static final Map<String, List<Long>> executionTimes = new ConcurrentHashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String testName = context.getDisplayName();
        startTimes.put(testName, System.nanoTime());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        String testName = context.getDisplayName();
        Long startTime = startTimes.remove(testName);

        if (startTime != null) {
            long duration = System.nanoTime() - startTime;
            executionTimes.computeIfAbsent(testName, k -> new ArrayList<>()).add(duration);

            double durationMs = duration / 1_000_000.0;
            System.out.printf("Test '%s' executed in %.2f ms%n", testName, durationMs);

            // Check for performance regression
            checkPerformanceRegression(testName, duration);
        }
    }

    private void checkPerformanceRegression(String testName, long currentDuration) {
        List<Long> previousRuns = executionTimes.get(testName);
        if (previousRuns.size() > 1) {
            // Calculate average of previous runs
            double averagePrevious = previousRuns.subList(0, previousRuns.size() - 1)
                .stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

            double regressionThreshold = 1.5; // 50% slower
            if (currentDuration > averagePrevious * regressionThreshold) {
                System.err.printf("WARNING: Performance regression in test '%s'! " +
                    "Current: %.2f ms, Average: %.2f ms%n",
                    testName,
                    currentDuration / 1_000_000.0,
                    averagePrevious / 1_000_000.0);
            }
        }
    }

    public static Map<String, Double> getAverageExecutionTimes() {
        return executionTimes.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0) / 1_000_000.0 // Convert to milliseconds
            ));
    }
}
```

## Performance testing

### Benchmark tests
```java
@SpringBootTest
@ExtendWith(PerformanceExtension.class)
public class PerformanceBenchmarkTest {

    @Autowired
    private UserService userService;

    @Test
    void benchmarkUserCreation() {
        int iterations = 1000;

        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            User user = userService.createUser(
                "benchmark" + i + "@example.com",
                "Benchmark User " + i
            );
            assertNotNull(user.getId());
        }

        long totalTime = System.nanoTime() - startTime;
        double avgTimePerOperation = (double) totalTime / iterations / 1_000_000; // ms

        System.out.printf("Created %d users in %.2f ms (avg: %.2f ms per user)%n",
            iterations, totalTime / 1_000_000.0, avgTimePerOperation);

        // Assert performance requirements
        assertTrue(avgTimePerOperation < 10, "User creation should be faster than 10ms on average");
    }

    @Test
    void benchmarkUserRetrieval() {
        // Setup test data
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = userService.createUser(
                "retrieve" + i + "@example.com",
                "Retrieve User " + i
            );
            users.add(user);
        }

        // Benchmark retrieval
        int iterations = 1000;
        long startTime = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            User user = users.get(i % users.size());
            User found = userService.findById(user.getId());
            assertNotNull(found);
        }

        long totalTime = System.nanoTime() - startTime;
        double avgTimePerOperation = (double) totalTime / iterations / 1_000_000;

        System.out.printf("Retrieved %d users in %.2f ms (avg: %.2f ms per retrieval)%n",
            iterations, totalTime / 1_000_000.0, avgTimePerOperation);

        assertTrue(avgTimePerOperation < 5, "User retrieval should be faster than 5ms on average");
    }

    @Test
    void testConcurrentPerformance() throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<Void>> futures = new ArrayList<>();

        long startTime = System.nanoTime();

        // Submit concurrent tasks
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Future<Void> future = executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    User user = userService.createUser(
                        "concurrent" + threadId + "_" + j + "@example.com",
                        "Concurrent User " + threadId + "_" + j
                    );
                    assertNotNull(user.getId());
                }
                return null;
            });
            futures.add(future);
        }

        // Wait for completion
        for (Future<Void> future : futures) {
            future.get();
        }

        long totalTime = System.nanoTime() - startTime;
        int totalOperations = threadCount * operationsPerThread;
        double avgTimePerOperation = (double) totalTime / totalOperations / 1_000_000;

        System.out.printf("Concurrent test: %d threads, %d total operations in %.2f ms (avg: %.2f ms per operation)%n",
            threadCount, totalOperations, totalTime / 1_000_000.0, avgTimePerOperation);

        executor.shutdown();

        // Assert concurrent performance
        assertTrue(avgTimePerOperation < 20, "Concurrent operations should be reasonably fast");
    }
}
```

## Test reporting

### Custom test execution listener
```java
public class CustomTestExecutionListener implements TestExecutionListener {

    private final Map<String, TestResult> testResults = new ConcurrentHashMap<>();
    private Instant suiteStartTime;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        suiteStartTime = Instant.now();
        System.out.println("Test suite started at: " + suiteStartTime);
        System.out.println("Total tests: " + testPlan.countTestIdentifiers(TestDescriptor::getType,
            TestDescriptor.Type.TEST));
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        Instant suiteEndTime = Instant.now();
        Duration suiteDuration = Duration.between(suiteStartTime, suiteEndTime);

        System.out.println("Test suite finished at: " + suiteEndTime);
        System.out.println("Total duration: " + suiteDuration);

        generateTestReport();
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            TestResult result = new TestResult(testIdentifier.getDisplayName());
            result.setStartTime(Instant.now());
            testResults.put(testIdentifier.getUniqueId(), result);
        }
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (testIdentifier.isTest()) {
            TestResult result = testResults.get(testIdentifier.getUniqueId());
            if (result != null) {
                result.setEndTime(Instant.now());
                result.setStatus(testExecutionResult.getStatus());
                result.setThrowable(testExecutionResult.getThrowable().orElse(null));
            }
        }
    }

    private void generateTestReport() {
        System.out.println("\n=== Test Execution Report ===");

        Map<TestExecutionResult.Status, Long> statusCounts = testResults.values().stream()
            .collect(Collectors.groupingBy(TestResult::getStatus, Collectors.counting()));

        statusCounts.forEach((status, count) ->
            System.out.println(status + ": " + count));

        // Performance summary
        List<TestResult> successfulTests = testResults.values().stream()
            .filter(result -> result.getStatus() == TestExecutionResult.Status.SUCCESSFUL)
            .collect(Collectors.toList());

        if (!successfulTests.isEmpty()) {
            double avgDuration = successfulTests.stream()
                .mapToLong(result -> result.getDuration().toMillis())
                .average()
                .orElse(0.0);

            System.out.printf("Average test duration: %.2f ms%n", avgDuration);
        }

        // Failed tests details
        List<TestResult> failedTests = testResults.values().stream()
            .filter(result -> result.getStatus() == TestExecutionResult.Status.FAILED)
            .collect(Collectors.toList());

        if (!failedTests.isEmpty()) {
            System.out.println("\nFailed tests:");
            failedTests.forEach(result -> {
                System.out.println("  - " + result.getTestName());
                if (result.getThrowable() != null) {
                    System.out.println("    Error: " + result.getThrowable().getMessage());
                }
            });
        }
    }

    static class TestResult {
        private final String testName;
        private Instant startTime;
        private Instant endTime;
        private TestExecutionResult.Status status;
        private Throwable throwable;

        public TestResult(String testName) {
            this.testName = testName;
        }

        // Getters and setters
        public String getTestName() { return testName; }
        public TestExecutionResult.Status getStatus() { return status; }
        public void setStatus(TestExecutionResult.Status status) { this.status = status; }
        public Throwable getThrowable() { return throwable; }
        public void setThrowable(Throwable throwable) { this.throwable = throwable; }
        public void setStartTime(Instant startTime) { this.startTime = startTime; }
        public void setEndTime(Instant endTime) { this.endTime = endTime; }

        public Duration getDuration() {
            return startTime != null && endTime != null ?
                Duration.between(startTime, endTime) : Duration.ZERO;
        }
    }
}
```

### JUnit configuration for custom listener
```java
// src/test/resources/junit-platform.properties
junit.platform.test_execution_listeners=com.example.CustomTestExecutionListener
```

## Best practices

### 1. Test organization
```java
@SpringBootTest
@DisplayName("User Management System")
public class UserManagementTestSuite {

    @Nested
    @DisplayName("User Registration")
    class UserRegistrationTests {

        @Test
        @DisplayName("should create user with valid email and name")
        void shouldCreateUserWithValidData() {
            // Test implementation
        }

        @Test
        @DisplayName("should reject registration with invalid email")
        void shouldRejectInvalidEmail() {
            // Test implementation
        }
    }

    @Nested
    @DisplayName("User Authentication")
    class UserAuthenticationTests {

        @Test
        @DisplayName("should authenticate user with correct credentials")
        void shouldAuthenticateWithCorrectCredentials() {
            // Test implementation
        }
    }
}
```

### 2. Test data management
```java
@SpringBootTest
public class TestDataManagementTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TestDataBuilder testDataBuilder;

    @BeforeEach
    void setup() {
        // Clean up before each test
        testDataBuilder.cleanup();

        // Setup fresh test data
        testDataBuilder.createUsers(5);
        testDataBuilder.createOrders(10);
    }

    @AfterEach
    void cleanup() {
        // Clean up after each test
        testDataBuilder.cleanup();
    }

    @Test
    void testBusinessLogic() {
        // Test uses fresh, isolated data
        List<User> users = userService.findAllUsers();
        assertEquals(5, users.size());
    }
}

@Component
public class TestDataBuilder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void createUsers(int count) {
        for (int i = 0; i < count; i++) {
            User user = new User("user" + i + "@example.com", "User " + i);
            userRepository.save(user);
        }
    }

    @Transactional
    public void createOrders(int count) {
        List<User> users = userRepository.findAll();
        for (int i = 0; i < count; i++) {
            User user = users.get(i % users.size());
            Order order = new Order(user.getId(), BigDecimal.valueOf(10.0 * (i + 1)));
            orderRepository.save(order);
        }
    }

    @Transactional
    public void cleanup() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }
}
```

### 3. Test naming conventions
```java
@SpringBootTest
public class NamingConventionsTest {

    // Method naming patterns
    @Test
    void shouldCreateUser_whenValidDataProvided() {
        // Test implementation
    }

    @Test
    void shouldThrowException_whenInvalidEmailProvided() {
        // Test implementation
    }

    @Test
    void shouldReturnUser_whenUserExists() {
        // Test implementation
    }

    @Test
    void shouldReturnEmptyOptional_whenUserDoesNotExist() {
        // Test implementation
    }

    // Given-When-Then pattern
    @Test
    void givenValidUserData_whenCreatingUser_thenUserIsCreated() {
        // Given
        String email = "test@example.com";
        String name = "Test User";

        // When
        User user = userService.createUser(email, name);

        // Then
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
    }

    // Data-driven test naming
    @ParameterizedTest
    @CsvSource({
        "valid@example.com, Valid User, SUCCESS",
        "invalid-email, Test User, EMAIL_INVALID",
        "existing@example.com, Test User, EMAIL_EXISTS"
    })
    void shouldValidateUserCreation(String email, String name, String expectedResult) {
        // Test implementation with dynamic naming
        String testName = String.format("Creating user with email '%s' should result in %s",
            email, expectedResult);
        // Note: JUnit 5 doesn't support dynamic test names in parameterized tests
        // Use @DisplayName with method source instead
    }
}
```

### 4. Test isolation
```java
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IsolatedTest {

    @Autowired
    private UserService userService;

    @MockBean
    private EmailService emailService; // Isolate external dependencies

    @Test
    void testUserCreation_independent() {
        // Each test gets a fresh application context
        User user = userService.createUser("isolated@example.com", "Isolated User");
        assertNotNull(user.getId());

        // Verify email was sent (mocked)
        verify(emailService).sendWelcomeEmail(user.getEmail());
    }

    @Test
    void testAnotherOperation_independent() {
        // This test gets a fresh context too
        List<User> users = userService.findAllUsers();
        assertTrue(users.isEmpty()); // Fresh context, no users
    }
}

// Alternative: Use @Transactional for database isolation
@SpringBootTest
public class TransactionalIsolationTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    void testDatabaseOperations_isolated() {
        // Changes are rolled back after test
        User user = userService.createUser("transactional@example.com", "Transactional User");
        assertNotNull(user.getId());

        // Verify user exists within transaction
        User found = userService.findById(user.getId());
        assertNotNull(found);
    }

    @Test
    @Transactional
    void testAnotherDatabaseOperation_isolated() {
        // No users from previous test (rolled back)
        List<User> users = userService.findAllUsers();
        assertTrue(users.isEmpty());
    }
}
```

### 5. Test automation
```java
@SpringBootTest
public class AutomatedTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TestAutomationHelper automationHelper;

    @Test
    void testCompleteUserWorkflow() {
        // Automated test using helper methods
        User user = automationHelper.createRandomUser();
        Order order = automationHelper.createOrderForUser(user);
        Payment payment = automationHelper.processPaymentForOrder(order);

        // Assertions
        assertNotNull(user.getId());
        assertNotNull(order.getId());
        assertNotNull(payment.getId());
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }

    @TestFactory
    Stream<DynamicTest> testAllUserWorkflows() {
        return automationHelper.getAllWorkflowScenarios().stream()
            .map(scenario -> DynamicTest.dynamicTest(
                "Test workflow: " + scenario.getName(),
                () -> automationHelper.executeWorkflowScenario(scenario)
            ));
    }
}

@Component
public class TestAutomationHelper {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    public User createRandomUser() {
        String email = "user" + System.nanoTime() + "@example.com";
        String name = "Random User " + System.nanoTime();
        return userService.createUser(email, name);
    }

    public Order createOrderForUser(User user) {
        return orderService.createOrder(user.getId(),
            List.of(new OrderItem("item1", BigDecimal.valueOf(10.0), 1)));
    }

    public Payment processPaymentForOrder(Order order) {
        return paymentService.processPayment(order.getId(), order.getTotalAmount());
    }

    public List<WorkflowScenario> getAllWorkflowScenarios() {
        return List.of(
            new WorkflowScenario("Happy Path", this::executeHappyPathWorkflow),
            new WorkflowScenario("Payment Failure", this::executePaymentFailureWorkflow),
            new WorkflowScenario("User Not Found", this::executeUserNotFoundWorkflow)
        );
    }

    public void executeWorkflowScenario(WorkflowScenario scenario) {
        scenario.getExecutor().run();
    }

    // Workflow implementations
    private void executeHappyPathWorkflow() {
        User user = createRandomUser();
        Order order = createOrderForUser(user);
        Payment payment = processPaymentForOrder(order);

        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
    }

    private void executePaymentFailureWorkflow() {
        User user = createRandomUser();
        Order order = createOrderForUser(user);

        // Simulate payment failure
        assertThrows(PaymentException.class, () ->
            paymentService.processPayment(order.getId(), BigDecimal.ZERO));
    }

    private void executeUserNotFoundWorkflow() {
        assertThrows(UserNotFoundException.class, () ->
            orderService.createOrder(999L, List.of()));
    }

    static class WorkflowScenario {
        private final String name;
        private final Runnable executor;

        public WorkflowScenario(String name, Runnable executor) {
            this.name = name;
            this.executor = executor;
        }

        public String getName() { return name; }
        public Runnable getExecutor() { return executor; }
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Tests not running
```bash
# Check if JUnit 5 is properly configured
mvn test -Dtest=YourTest

# Check test discovery
mvn test -Dmaven.surefire.debug=true

# Verify test class naming
# Test classes should end with Test, Tests, or be annotated with @Test
```

#### Dependency injection issues
```java
// Common issue: Missing @SpringBootTest
@SpringBootTest  // Required for Spring context
public class MyTest {

    @Autowired
    private MyService service; // Will be null without @SpringBootTest

    @Test
    void testService() {
        assertNotNull(service);
    }
}

// For unit tests without Spring context
public class MyUnitTest {

    private MyService service;

    @BeforeEach
    void setup() {
        service = new MyService(); // Manual instantiation
    }
}
```

#### Test execution order issues
```java
// Problem: Tests depend on execution order
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderedTest {

    @Test
    @Order(1)
    void testCreateData() {
        // This should run first
    }

    @Test
    @Order(2)
    void testUseData() {
        // This depends on testCreateData
    }
}

// Better: Make tests independent
@SpringBootTest
public class IndependentTest {

    @Autowired
    private TestDataSetup testDataSetup;

    @BeforeEach
    void setup() {
        // Each test sets up its own data
        testDataSetup.createTestData();
    }

    @Test
    void testOperation1() {
        // Independent test
    }

    @Test
    void testOperation2() {
        // Independent test
    }
}
```

#### Performance issues
```java
// Problem: Slow tests due to heavy setup
@SpringBootTest  // Loads full application context
public class SlowTest {

    @Test
    void testHeavyOperation() {
        // Takes 30+ seconds due to full context
    }
}

// Solution: Use test slices
@DataJpaTest  // Only loads JPA context
public class FastRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void testRepositoryOperation() {
        // Much faster, only JPA context
    }
}
```

### Debug techniques

#### Test debugging
```java
@SpringBootTest
public class DebugTest {

    @Autowired
    private UserService userService;

    @Test
    void debugTestExecution() {
        // Enable debug logging
        Logger logger = LoggerFactory.getLogger("com.example");
        // Set to DEBUG level programmatically if needed

        System.out.println("Starting test execution");

        try {
            User user = userService.createUser("debug@example.com", "Debug User");
            System.out.println("User created: " + user);

            assertNotNull(user.getId());

        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    void testWithBreakpoints() {
        // Set breakpoints in IDE for debugging
        User user = userService.createUser("breakpoint@example.com", "Breakpoint User");

        // Add debug assertions
        assertAll(
            () -> assertNotNull(user, "User should not be null"),
            () -> assertNotNull(user.getId(), "User ID should not be null"),
            () -> assertEquals("breakpoint@example.com", user.getEmail(), "Email should match")
        );
    }
}
```

#### Test configuration debugging
```java
@Configuration
public class TestDebugConfig {

    @PostConstruct
    public void debugConfiguration() {
        System.out.println("=== Test Configuration Debug ===");

        // Check active profiles
        String[] activeProfiles = getActiveProfiles();
        System.out.println("Active profiles: " + Arrays.toString(activeProfiles));

        // Check environment properties
        String dbUrl = System.getProperty("spring.datasource.url");
        System.out.println("Database URL: " + dbUrl);

        // Check bean registration
        ApplicationContext context = getApplicationContext();
        String[] beanNames = context.getBeanDefinitionNames();
        System.out.println("Registered beans: " + beanNames.length);

        // Check for specific beans
        if (context.containsBean("userService")) {
            System.out.println("✓ UserService bean found");
        } else {
            System.out.println("✗ UserService bean not found");
        }
    }

    private String[] getActiveProfiles() {
        // Implementation to get active profiles
        return new String[]{"test"};
    }

    private ApplicationContext getApplicationContext() {
        // Implementation to get application context
        return null; // Placeholder
    }
}
```

#### Test data debugging
```java
@SpringBootTest
public class TestDataDebugTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataInitializer dataInitializer;

    @BeforeEach
    void debugTestData() {
        System.out.println("=== Test Data Debug ===");

        // Check test data initialization
        dataInitializer.initialize();

        // Verify test data
        List<User> users = userRepository.findAll();
        System.out.println("Users in database: " + users.size());

        users.forEach(user ->
            System.out.println("  - " + user.getEmail() + " (" + user.getId() + ")"));

        // Check database connection
        try {
            userRepository.count();
            System.out.println("✓ Database connection OK");
        } catch (Exception e) {
            System.out.println("✗ Database connection failed: " + e.getMessage());
        }
    }

    @Test
    void testWithDataVerification() {
        // Verify test data before test logic
        assertTrue(userRepository.count() > 0, "Test data should be initialized");

        // Actual test logic
        User user = userRepository.findByEmail("test@example.com");
        assertNotNull(user, "Test user should exist");
    }
}
```

## Заключение

**JUnit 5 Advanced** предоставляет мощные возможности для создания комплексных, поддерживаемых и эффективных тестов в Java-приложениях. Архитектура на основе extensions, поддержка параллельного выполнения, parameterized и dynamic tests делают JUnit 5 идеальным выбором для современного тестирования.

### Ключевые возможности:

1. **Extensions API** — гибкая система расширений для кастомизации поведения тестов
2. **Parameterized tests** — поддержка data-driven тестирования
3. **Dynamic tests** — генерация тестов во время выполнения
4. **Nested tests** — иерархическая организация тестов
5. **Conditional execution** — условное выполнение тестов
6. **Parallel execution** — параллельное выполнение тестов
7. **Spring Boot integration** — глубокая интеграция с Spring
8. **Testcontainers** — интеграция с контейнерами для тестирования

### Архитектурные преимущества:

#### Extensibility:
- **Extensions API** — возможность создания custom extensions
- **Test lifecycle hooks** — контроль над жизненным циклом тестов
- **Custom annotations** — создание domain-specific тестовых аннотаций

#### Performance:
- **Parallel execution** — ускорение выполнения тестов
- **Conditional tests** — пропуск ненужных тестов
- **Resource management** — эффективное управление ресурсами

### Когда использовать JUnit 5 Advanced:

✅ **Complex test scenarios** — parameterized и dynamic tests
✅ **Integration testing** — Spring Boot и Testcontainers интеграция
✅ **Large test suites** — parallel execution и conditional tests
✅ **Custom test frameworks** — extensions API
✅ **Microservices testing** — nested и hierarchical tests
✅ **Performance testing** — benchmark и timing extensions
✅ **CI/CD pipelines** — conditional execution и custom reporting
✅ **Test automation** — dynamic test generation

### Когда НЕ использовать:

❌ **Simple unit tests** — базовые возможности JUnit достаточны
❌ **Legacy JUnit 4** — использовать для совместимости
❌ **Performance-critical** — overhead от extensions
❌ **Small projects** — избыточная сложность
❌ **No Spring** — некоторые возможности специфичны для Spring

### Best practices:

1. **Test organization** — nested tests и clear naming
2. **Test data management** — isolated и repeatable tests
3. **Test automation** — reduce manual testing efforts
4. **Performance considerations** — optimize test execution
5. **Custom extensions** — encapsulate common testing logic
6. **Conditional execution** — skip inappropriate tests
7. **Parallel execution** — speed up test suites
8. **Test reporting** — custom reporting and metrics

### Spring Boot Testing:

- **@SpringBootTest** — full application context
- **Test slices** — focused testing (@WebMvcTest, @DataJpaTest)
- **@TestConfiguration** — custom test configuration
- **Testcontainers** — database and service containers

### Advanced Features:

- **Extensions** — custom test behavior
- **Parameterized tests** — data-driven testing
- **Dynamic tests** — runtime test generation
- **Nested tests** — hierarchical test organization
- **Conditional tests** — environment-specific execution
- **Parallel execution** — concurrent test execution

JUnit 5 Advanced является стандартом для современного тестирования Java-приложений. Правильное использование его возможностей обеспечивает высокое качество кода, надежность и поддерживаемость тестов. 🚀

**Далее: Mockito Advanced (продвинутые техники mocking)**
