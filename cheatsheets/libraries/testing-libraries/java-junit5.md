---
title: "JUnit 5"
description: "JUnit 5 - это новая генерация фреймворка для модульного тестирования Java. Полностью переработан по сравнению с JUnit 4, с новым API, расширяемостью и поддержкой современных Java фич."
tags:
  - libraries
  - testing-libraries
  - java-junit5
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# JUnit 5

**JUnit** 5 - это новая генерация фреймворка для модульного тестирования **Java**. Полностью переработан по сравнению с **JUnit** 4, с новым **API**, расширяемостью и поддержкой современных **Java** фич.

## Полезные ссылки

### Официальная документация
- [JUnit 5](https://junit.org/junit5/) — официальный сайт
- [JUnit 5 GitHub](https://github.com/junit-team/junit5) — репозиторий проекта
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) — руководство пользователя

### См. также
- [Mockito](../../testing/unit-testing/junit/mockito.md) — **Mockito** для мокирования
- [Unit-тестирование](../../testing/unit-testing/README.md) — **Unit** тестирование

## Содержание

- [Основные возможности](#основные-возможности)
  - [Основная структура теста](#основная-структура-теста)
  - [Assertions API](#assertions-api)
- [Аннотации и жизненный цикл](#аннотации-и-жизненный-цикл)
  - [Test Lifecycle](#test-lifecycle)
  - [Conditional Test Execution](#conditional-test-execution)
  - [Parameterized Tests](#parameterized-tests)
- [Extensions API](#extensions-api)
  - [Custom Extensions](#custom-extensions)
  - [Test Templates](#test-templates)
- [Dynamic Tests](#dynamic-tests)
  - [Dynamic Test Generation](#dynamic-test-generation)
- [Test Interfaces и Inheritance](#test-interfaces-и-inheritance)
  - [Test Interfaces](#test-interfaces)
- [Spring Boot Integration](#spring-boot-integration)
  - [Spring Extension](#spring-extension)
  - [Test Slices](#test-slices)
- [Testing Best Practices](#testing-best-practices)
  - [Test Organization](#test-organization)
  - [Custom Assertions](#custom-assertions)
- [Migration from JUnit 4](#migration-from-junit-4)
  - [Basic Migration](#basic-migration)
  - [Advanced Migration](#advanced-migration)
- [Performance Testing](#performance-testing)
  - [Benchmark Tests](#benchmark-tests)
- [Parallel Execution](#parallel-execution)
  - [Parallel Test Execution](#parallel-test-execution)
- [Custom Test Engines](#custom-test-engines)
  - [Implementing Custom Test Engine](#implementing-custom-test-engine)
- [Integration с другими инструментами](#integration-с-другими-инструментами)
  - [Mockito Integration](#mockito-integration)
  - [Testcontainers Integration](#testcontainers-integration)
- [Best Practices](#best-practices)
  - [Test Naming Conventions](#test-naming-conventions)
  - [Test Data Management](#test-data-management)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Debugging Tests](#debugging-tests)
- [Experimental Features](#experimental-features)
  - [JUnit 6 Preview Features](#junit-6-preview-features)

## Основные возможности

### Основная структура теста

Пример тестового класса **JUnit** 5 с `@Test`, `assertEquals` и `assertThrows`.

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void testAddition() {
        // Given
        int a = 5;
        int b = 3;

        // When
        int result = calculator.add(a, b);

        // Then
        assertEquals(8, result);
    }

    @Test
    void testDivision() {
        // When & Then
        assertThrows(ArithmeticException.class, () -> calculator.divide(10, 0));
    }
}
```

### **Assertions API**
```java
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

public class AssertionsExamples {

    @Test
    void standardAssertions() {
        // Простые assertions
        assertEquals(2, 1 + 1);
        assertNotEquals(3, 1 + 1);

        assertTrue(1 < 2);
        assertFalse(1 > 2);

        assertNull(null);
        assertNotNull(new Object());

        assertSame("literal", "literal");
        assertNotSame(new String("literal"), new String("literal"));
    }

    @Test
    void arrayAssertions() {
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};

        assertArrayEquals(expected, actual);
    }

    @Test
    void collectionAssertions() {
        List<String> list = List.of("a", "b", "c");

        assertIterableEquals(List.of("a", "b", "c"), list);
        assertTrue(list.contains("b"));
    }

    @Test
    void mapAssertions() {
        Map<String, Integer> map = Map.of("a", 1, "b", 2);

        assertEquals(2, map.size());
        assertTrue(map.containsKey("a"));
        assertEquals(1, map.get("a"));
    }

    @Test
    void exceptionAssertions() {
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        });

        assertEquals("/ by zero", exception.getMessage());
    }

    @Test
    void timeoutAssertions() {
        assertTimeout(Duration.ofSeconds(1), () -> {
            Thread.sleep(500);
        });
    }

    @Test
    void groupedAssertions() {
        Person person = new Person("John", "Doe", 30);

        assertAll("person properties",
            () -> assertEquals("John", person.getFirstName()),
            () -> assertEquals("Doe", person.getLastName()),
            () -> assertEquals(30, person.getAge()),
            () -> assertTrue(person.getAge() > 18)
        );
    }
}
```

## Аннотации и жизненный цикл

### **Test Lifecycle**
```java
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Один экземпляр класса для всех тестов
public class LifecycleTest {

    private DatabaseConnection connection;

    @BeforeAll // Выполняется один раз перед всеми тестами
    static void setupAll() {
        System.out.println("Setting up test suite...");
    }

    @BeforeEach // Выполняется перед каждым тестом
    void setup() {
        connection = new DatabaseConnection();
        connection.connect();
    }

    @Test
    @DisplayName("Test database insertion") // Человеко-читаемое имя
    void testInsert() {
        // Test implementation
        assertTrue(connection.insert("test data"));
    }

    @Test
    @Disabled("Temporarily disabled") // Пропустить тест
    void testDisabled() {
        // Этот тест не выполнится
    }

    @AfterEach // Выполняется после каждого теста
    void tearDown() {
        connection.disconnect();
    }

    @AfterAll // Выполняется один раз после всех тестов
    static void tearDownAll() {
        System.out.println("Cleaning up test suite...");
    }
}
```

### **Conditional Test Execution**
```java
import org.junit.jupiter.api.condition.*;

/
 * Демонстрация условного выполнения тестов в JUnit 5
 * Условные тесты позволяют выполнять тесты только при определенных условиях
 */
public class ConditionalTests {

    /
     * Тест выполняется только на Windows
     * @EnabledOnOs позволяет указать операционные системы на которых тест должен выполняться
     */
    @Test
    @EnabledOnOs(OS.WINDOWS)  // Тест выполнится только если ОС = Windows
    void testOnWindows() {
        // Windows-specific test - тест специфичный для Windows
        // Например, тестирование Windows-специфичных путей или API
    }

    /
     * Тест выполняется только на Linux или Mac
     * Можно указать несколько ОС через массив
     */
    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})  // Тест выполнится только если ОС = Linux или Mac
    void testOnUnix() {
        // Unix-specific test - тест специфичный для Unix-подобных систем
        // Например, тестирование Unix-специфичных путей или команд
    }

    /
     * Тест выполняется только на Java 11
     * @EnabledOnJre позволяет указать версию JRE на которой тест должен выполняться
     */
    @Test
    @EnabledOnJre(JRE.JAVA_11)  // Тест выполнится только если JRE = Java 11
    void testOnJava11() {
        // Java 11 specific features - тест использует фичи доступные только в Java 11
        // Например, var в лямбдах, новые методы String и т.д.
    }

    /
     * Тест выполняется только если системное свойство env = test
     * @EnabledIfSystemProperty проверяет системные свойства перед выполнением теста
     */
    @Test
    @EnabledIfSystemProperty(named = "env", matches = "test")  // Тест выполнится только если -Denv=test
    void testInTestEnvironment() {
        // Только в тестовой среде - тест выполнится только если запущен с -Denv=test
        // Полезно для разделения тестов по окружениям (dev, test, prod)
    }

    /
     * Тест с кастомным условием
     * @EnabledIf позволяет указать метод который возвращает boolean для проверки условия
     */
    @Test
    @EnabledIf("customCondition")  // Тест выполнится только если customCondition() возвращает true
    void testWithCustomCondition() {
        // Выполняется только если customCondition() возвращает true
        // Позволяет создавать сложные условия для выполнения теста
    }

    /
     * Кастомное условие для выполнения теста
     * Метод должен быть static и возвращать boolean
     * @return true если тест должен выполняться, false если пропустить
     */
    static boolean customCondition() {
        // Проверяем системное свойство run.custom.tests
        // Тест выполнится только если запущен с -Drun.custom.tests=true
        return System.getProperty("run.custom.tests", "false").equals("true");
    }
}
```

### **Parameterized Tests**
```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

/
 * Демонстрация параметризованных тестов в JUnit 5
 * Параметризованные тесты позволяют выполнить один тест с разными входными данными
 */
public class ParameterizedTests {

    /
     * Параметризованный тест с простыми значениями
     * @ValueSource предоставляет массив простых значений для теста
     * @param number параметр теста (будет равен 1, 2, 3, 4, 5 по очереди)
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})  // Тест выполнится 5 раз с каждым значением из массива
    void testWithValueSource(int number) {
        // Проверяем что число положительное
        // Тест выполнится 5 раз: с number=1, number=2, number=3, number=4, number=5
        assertTrue(number > 0);
    }

    /
     * Параметризованный тест с значениями enum
     * @EnumSource предоставляет все значения enum для теста
     * @param timeUnit параметр теста (будет равен каждому значению TimeUnit enum)
     */
    @ParameterizedTest
    @EnumSource(TimeUnit.class)  // Тест выполнится для каждого значения TimeUnit enum
    void testWithEnumSource(TimeUnit timeUnit) {
        // Проверяем что timeUnit не null
        // Тест выполнится для каждого значения: NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS
        assertNotNull(timeUnit);
    }

    /
     * Параметризованный тест с методом-провайдером
     * @MethodSource указывает метод который предоставляет данные для теста
     * @param argument параметр теста (будет равен каждому значению из Stream)
     */
    @ParameterizedTest
    @MethodSource("stringProvider")  // Используем метод stringProvider() как источник данных
    void testWithMethodSource(String argument) {
        // Проверяем что аргумент не null
        // Тест выполнится 3 раза: с argument="apple", argument="banana", argument="cherry"
        assertNotNull(argument);
    }

    /
     * Метод-провайдер данных для параметризованного теста
     * Метод должен быть static и возвращать Stream, Iterable, Iterator или массив
     * @return Stream строк для использования в тесте
     */
    static Stream<String> stringProvider() {
        // Возвращаем Stream со строками для тестирования
        return Stream.of("apple", "banana", "cherry");
    }

    /
     * Параметризованный тест с CSV данными
     * @CsvSource позволяет указать данные в формате CSV прямо в аннотации
     * @param fruit первый параметр теста (строка)
     * @param rank второй параметр теста (число)
     */
    @ParameterizedTest
    @CsvSource({
        "apple,  1",   // Первая строка: fruit="apple", rank=1
        "banana, 2",   // Вторая строка: fruit="banana", rank=2
        "cherry, 3"    // Третья строка: fruit="cherry", rank=3
    })
    void testWithCsvSource(String fruit, int rank) {
        // Проверяем что fruit не null и rank положительный
        // Тест выполнится 3 раза с каждой парой значений из CSV
        assertNotNull(fruit);
        assertTrue(rank > 0);
    }

    @ParameterizedTest
    @ArgumentsSource(CustomArgumentsProvider.class) // Кастомный провайдер
    void testWithCustomProvider(String input, int expected) {
        assertEquals(expected, input.length());
    }

    static class CustomArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                Arguments.of("hello", 5),
                Arguments.of("world", 5),
                Arguments.of("test", 4)
            );
        }
    }
}
```

## **Extensions API**

### **Custom Extensions**
```java
// Создание кастомной extension
public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Map<String, Long> timings = new ConcurrentHashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        timings.put(context.getDisplayName(), System.nanoTime());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        long startTime = timings.remove(context.getDisplayName());
        long duration = System.nanoTime() - startTime;

        System.out.println(context.getDisplayName() + " took " + (duration / 1_000_000) + " ms");
    }
}

// Использование extension
@ExtendWith(TimingExtension.class)
public class ExtendedTest {

    @Test
    void testWithTiming() {
        // Test code
    }
}
```

### **Test Templates**
```java
@TestTemplate
@ExtendWith(DatabaseTestTemplateInvocationContextProvider.class)
void databaseTest(String databaseUrl) {
    // Этот метод будет вызван для каждого databaseUrl
}

public class DatabaseTestTemplateInvocationContextProvider
        implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
            ExtensionContext context) {

        return Stream.of(
            invocationContext("jdbc:h2:mem:test1"),
            invocationContext("jdbc:h2:mem:test2"),
            invocationContext("jdbc:postgresql://localhost/test")
        );
    }

    private TestTemplateInvocationContext invocationContext(String url) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return "Database: " + url;
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return List.of(new DatabaseExtension(url));
            }
        };
    }
}
```

## **Dynamic Tests**

### **Dynamic Test Generation**
```java
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class DynamicTests {

    @TestFactory
    Collection<DynamicTest> dynamicTestsFromCollection() {
        return Arrays.asList(
            DynamicTest.dynamicTest("1st dynamic test", () -> assertTrue(true)),
            DynamicTest.dynamicTest("2nd dynamic test", () -> assertEquals(4, 2 * 2))
        );
    }

    @TestFactory
    Stream<DynamicTest> dynamicTestsFromStream() {
        return Stream.of("apple", "banana", "cherry")
            .map(fruit -> DynamicTest.dynamicTest("Test " + fruit,
                () -> assertTrue(fruit.length() > 2)));
    }

    @TestFactory
    Stream<DynamicTest> dynamicDatabaseTests() {
        return databaseConfigurations()
            .map(config -> DynamicTest.dynamicTest(
                "Test database: " + config.getName(),
                () -> testDatabaseConnection(config)
            ));
    }
}
```

## **Test Interfaces** и **Inheritance**

### **Test Interfaces**
```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public interface TestLifecycleLogger {

    @BeforeAll
    default void beforeAllTests() {
        System.out.println("Before all tests in " + getClass().getSimpleName());
    }

    @AfterAll
    default void afterAllTests() {
        System.out.println("After all tests in " + getClass().getSimpleName());
    }
}

public interface DatabaseTests {

    @BeforeEach
    default void setupDatabase() {
        // Setup database connection
    }

    @AfterEach
    default void cleanupDatabase() {
        // Cleanup database
    }
}

// Использование интерфейсов
public class UserServiceTest implements TestLifecycleLogger, DatabaseTests {

    @Test
    void testUserCreation() {
        // Test implementation
    }
}
```

## **Spring Boot Integration**

### **Spring Extension**
```java
@SpringBootTest
@ExtendWith(SpringExtension.class) // Или @ExtendWith(SpringJUnit4ClassRunner.class) для обратной совместимости
public class SpringBootTest {

    @Autowired
    private UserService userService;

    @Test
    void testUserService() {
        User user = userService.createUser("John", "john@example.com");
        assertNotNull(user.getId());
        assertEquals("John", user.getName());
    }

    @Test
    @Transactional // Автоматический rollback после теста
    void testDatabaseOperation() {
        userService.saveUser(new User("Test", "test@example.com"));
        // Изменения будут отменены после теста
    }
}
```

### **Test Slices**
```java
@JdbcTest // Только JDBC компоненты
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        User user = new User("John", "john@example.com");
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }
}

@WebMvcTest(UserController.class) // Только web компоненты
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetUser() throws Exception {
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"));
    }
}

@DataJpaTest // Только JPA компоненты
public class UserJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserPersistence() {
        User user = new User("John", "john@example.com");
        User saved = entityManager.persistAndFlush(user);

        assertNotNull(saved.getId());
        assertEquals("John", saved.getName());
    }
}
```

## **Testing Best Practices**

### **Test Organization**
```java
// Структура тестов
public class UserServiceTest {

    // Constants
    private static final String VALID_EMAIL = "test@example.com";
    private static final String INVALID_EMAIL = "invalid-email";

    // Test data builders
    private User createValidUser() {
        return User.builder()
            .name("John Doe")
            .email(VALID_EMAIL)
            .age(30)
            .build();
    }

    // Test groups
    @Nested
    @DisplayName("User Creation")
    class UserCreationTests {

        @Test
        @DisplayName("Should create user with valid data")
        void shouldCreateUserWithValidData() {
            // Given
            User user = createValidUser();

            // When
            User created = userService.createUser(user);

            // Then
            assertNotNull(created.getId());
            assertEquals(user.getName(), created.getName());
        }

        @Test
        @DisplayName("Should throw exception for invalid email")
        void shouldThrowExceptionForInvalidEmail() {
            // Given
            User user = User.builder()
                .name("John Doe")
                .email(INVALID_EMAIL)
                .build();

            // When & Then
            assertThrows(InvalidEmailException.class, () ->
                userService.createUser(user));
        }
    }

    @Nested
    @DisplayName("User Retrieval")
    class UserRetrievalTests {

        @Test
        void shouldReturnUserById() {
            // Test implementation
        }

        @Test
        void shouldReturnEmptyForNonexistentUser() {
            // Test implementation
        }
    }
}
```

### **Custom Assertions**
```java
public class UserAssertions {

    public static void assertValidUser(User user) {
        assertAll("user validation",
            () -> assertNotNull(user.getId()),
            () -> assertNotNull(user.getName()),
            () -> assertFalse(user.getName().trim().isEmpty()),
            () -> assertNotNull(user.getEmail()),
            () -> assertTrue(user.getEmail().contains("@")),
            () -> assertTrue(user.getAge() >= 18),
            () -> assertTrue(user.getAge() <= 120)
        );
    }

    public static void assertUsersEqual(User expected, User actual) {
        assertAll("user equality",
            () -> assertEquals(expected.getName(), actual.getName()),
            () -> assertEquals(expected.getEmail(), actual.getEmail()),
            () -> assertEquals(expected.getAge(), actual.getAge())
        );
    }
}

// Использование
@Test
void testUserCreation() {
    User user = userService.createUser("John", "john@example.com", 30);
    UserAssertions.assertValidUser(user);
}
```

## **Migration from JUnit** 4

### **Basic Migration**
```java
// JUnit 4
import org.junit.Test;
import static org.junit.Assert.*;

public class JUnit4Test {

    @Before
    public void setUp() {
        // setup
    }

    @Test
    public void testSomething() {
        assertEquals(2, 1 + 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testException() {
        throw new IllegalArgumentException();
    }
}

// JUnit 5
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JUnit5Test {

    @BeforeEach
    void setUp() {
        // setup
    }

    @Test
    void testSomething() {
        assertEquals(2, 1 + 1);
    }

    @Test
    void testException() {
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException();
        });
    }
}
```

### **Advanced Migration**
```java
// JUnit 4 с Rules
public class JUnit4RuleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testWithRules() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Invalid argument");

        // test code
    }
}

// JUnit 5 с Extensions
@ExtendWith(TemporaryFolderExtension.class)
public class JUnit5ExtensionTest {

    @Test
    void testWithExtensions(@TempDir Path tempDir) {
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Invalid argument");
        }, "Invalid argument");
    }
}
```

## **Performance Testing**

### **Benchmark Tests**
```java
public class PerformanceTest {

    @Test
    void testPerformance() {
        assertTimeout(Duration.ofSeconds(1), () -> {
            // Code that should complete within 1 second
            expensiveOperation();
        });
    }

    @RepeatedTest(10) // Повторить тест 10 раз
    void testRepeatedPerformance(RepetitionInfo repetitionInfo) {
        System.out.println("Repetition #" + repetitionInfo.getCurrentRepetition());

        long startTime = System.nanoTime();
        performOperation();
        long endTime = System.nanoTime();

        System.out.println("Duration: " + (endTime - startTime) / 1_000_000 + " ms");
    }

    @Test
    void testAveragePerformance() {
        List<Long> durations = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            long startTime = System.nanoTime();
            performOperation();
            long endTime = System.nanoTime();
            durations.add(endTime - startTime);
        }

        double average = durations.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);

        System.out.println("Average duration: " + average / 1_000_000 + " ms");

        // Assert that average is acceptable
        assertTrue(average < 100_000_000, "Average operation too slow"); // < 100ms
    }
}
```

## **Parallel Execution**

### **Parallel Test Execution**
```java
// junit-platform.properties
junit.jupiter.execution.parallel.enabled = true
junit.jupiter.execution.parallel.mode.default = concurrent
junit.jupiter.execution.parallel.config.strategy = dynamic

@Execution(CONCURRENT) // Параллельное выполнение тестов в классе
public class ParallelTests {

    @Test
    void test1() {
        // Этот тест может выполняться параллельно с другими
    }

    @Test
    void test2() {
        // Этот тест может выполняться параллельно с другими
    }

    @Test
    @Execution(SAME_THREAD) // Этот тест выполняется в том же потоке
    void testSequential() {
        // Sequential test
    }
}
```

## **Custom Test Engines**

### **Implementing Custom Test Engine**
```java
public class CustomTestEngine implements TestEngine {

    @Override
    public String getId() {
        return "custom-test-engine";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest,
                                   UniqueId uniqueId) {
        // Логика обнаружения тестов
        return null;
    }

    @Override
    public void execute(ExecutionRequest executionRequest) {
        // Логика выполнения тестов
    }
}

// Регистрация через ServiceLoader
// META-INF/services/org.junit.platform.engine.TestEngine
```

## **Integration** с другими инструментами

### **Mockito Integration**
```java
@ExtendWith(MockitoExtension.class)
public class MockitoIntegrationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testWithMockito() {
        // Given
        User user = new User("John", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.createUser("John", "john@example.com");

        // Then
        assertEquals("John", result.getName());
        verify(userRepository).save(any(User.class));
    }
}
```

### **Testcontainers Integration**
```java
@SpringBootTest
@Testcontainers
public class TestcontainersIntegrationTest {

    @Container
    private static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void testWithRealDatabase() {
        User user = new User("John", "john@example.com");
        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
    }
}
```

## Лучшие практики

### **Test Naming Conventions**
```java
public class TestNamingExamples {

    // Good naming - describes behavior
    @Test
    void shouldReturnEmptyListWhenNoUsersExist() {
        // Test implementation
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Test implementation
    }

    @Test
    void shouldSaveUserToDatabase() {
        // Test implementation
    }

    // Bad naming - not descriptive
    @Test
    void test1() {
        // What does this test?
    }

    @Test
    void userTest() {
        // Too vague
    }
}
```

### **Test Data Management**
```java
public class TestDataManagement {

    // Test Data Builders
    public static class UserBuilder {
        private String name = "Default Name";
        private String email = "default@example.com";
        private int age = 25;

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(name, email, age);
        }

        public static UserBuilder validUser() {
            return new UserBuilder();
        }

        public static UserBuilder invalidUser() {
            return new UserBuilder()
                .name("")
                .email("invalid-email");
        }
    }

    // Test data fixtures
    @Test
    void testWithBuilderPattern() {
        User user = UserBuilder.validUser()
            .name("John Doe")
            .email("john@example.com")
            .build();

        assertNotNull(userService.createUser(user));
    }

    // CSV-based test data
    @ParameterizedTest
    @CsvFileSource(resources = "/test-users.csv", numLinesToSkip = 1)
    void testWithCsvData(String name, String email, int age) {
        User user = new User(name, email, age);
        assertNotNull(userService.createUser(user));
    }
}
```

## Решение проблем

### **Common Issues**
```java
public class JUnit5Troubleshooting {

    // Проблема: Tests not running
    @Test // Убедитесь, что используется правильная аннотация
    void testMethod() {
        // JUnit 4 uses @Test from org.junit.Test
        // JUnit 5 uses @Test from org.junit.jupiter.api.Test
    }

    // Проблема: Test instance per method
    @TestInstance(TestInstance.Lifecycle.PER_METHOD) // Default behavior
    public class TestClass {

        private int counter = 0;

        @Test
        void test1() {
            counter++;
            assertEquals(1, counter);
        }

        @Test
        void test2() {
            counter++;
            assertEquals(1, counter); // Каждый тест получает новый экземпляр
        }
    }

    // Проблема: Extension not working
    @ExtendWith(CustomExtension.class)
    public class ExtensionTest {

        @RegisterExtension // Альтернативный способ регистрации
        static CustomExtension extension = new CustomExtension();

        @Test
        void testWithExtension() {
            // Test code
        }
    }
}
```

### **Debugging Tests**
```java
public class TestDebugging {

    // Логирование тестов
    private static final Logger logger = LoggerFactory.getLogger(TestDebugging.class);

    @BeforeEach
    void logTestStart(TestInfo testInfo) {
        logger.info("Starting test: {}", testInfo.getDisplayName());
    }

    @AfterEach
    void logTestEnd(TestInfo testInfo, TestReporter testReporter) {
        logger.info("Finished test: {}", testInfo.getDisplayName());

        // Дополнительная информация
        testReporter.publishEntry("test.class", testInfo.getTestClass().get().getSimpleName());
        testReporter.publishEntry("test.method", testInfo.getTestMethod().get().getName());
    }

    // Conditional test execution based on debug mode
    @Test
    @DisabledIf("java.lang.System.getProperty('debug.tests') == null")
    void debugOnlyTest() {
        // Этот тест выполняется только в debug режиме
        System.out.println("Debug test executed");
    }
}
```

## **Experimental Features**

### **JUnit** 6 **Preview Features**
```java
// Предполагаемые возможности JUnit 6
// (основанные на текущих планах развития)

// Улучшенная поддержка модульной системы Java
// Лучшая интеграция с современными Java фичам
// Улучшенный parallel execution
// Native support for property-based testing

// Пример возможного API
@PropertyBasedTest
public class PropertyBasedTest {

    @Property
    void stringConcatenationLength(@ForAll String a, @ForAll String b) {
        String result = a + b;
        assertEquals(a.length() + b.length(), result.length());
    }

    @Property
    void listReverseIsInvolution(@ForAll List<Integer> list) {
        List<Integer> reversed = ListUtils.reverse(list);
        List<Integer> doubleReversed = ListUtils.reverse(reversed);
        assertEquals(list, doubleReversed);
    }
}
```


## Полезные ссылки
- [Официальная документация JUnit 5](https://junit.org/junit5/)
- [JUnit 5 GitHub](https://github.com/junit-team/junit5)
- [JUnit 5 Samples](https://github.com/junit-team/junit5-samples)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

## См. также
- [Mockito](../../testing/unit-testing/junit/mockito.md), [Mockito Advanced](../../testing/unit-testing/junit/mockito-advanced.md) — моки и стабы
- [AssertJ](../../testing/unit-testing/junit/assertj.md) — **Fluent assertions**
- [Testcontainers](https://www.testcontainers.org/) — интеграционные тесты

