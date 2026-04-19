---
title: "Micronaut: Testing - Unit Tests, Integration Tests и Mocking"
description: "Полное руководство по тестированию в Micronaut: unit tests, integration tests, mocking, test containers и best practices"
tags:
  - micronaut
  - testing
  - junit
  - mockito
  - integration-tests
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-cloud.md", "micronaut-graalvm.md"]
updated: "2026-02-11"
related: ["micronaut-http.md", "micronaut-data.md"]
---

# Micronaut: Testing - Unit Tests, Integration Tests и Mocking



## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: Testing - Unit Tests, Integration Tests и Mocking](#micronaut-testing-unit-tests-integration-tests-и-mocking)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Testing](#настройка-testing)
  - [Зависимости](#зависимости)
- [Unit Tests](#unit-tests)
  - [Простые Unit Tests](#простые-unit-tests)
- [Integration Tests](#integration-tests)
  - [@MicronautTest](#micronauttest)
  - [HTTP Client Testing](#http-client-testing)
  - [Reactive Testing](#reactive-testing)
- [Database Testing](#database-testing)
  - [In-Memory Database](#in-memory-database)
  - [Test Containers](#test-containers)
- [Mocking](#mocking)
  - [@MockBean](#mockbean)
  - [Spying](#spying)
- [Security Testing](#security-testing)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте @MicronautTest для Integration Tests](#1-используйте-micronauttest-для-integration-tests)
  - [2. Изолируйте тесты](#2-изолируйте-тесты)
  - [3. Используйте Test Containers для Real Database Testing](#3-используйте-test-containers-для-real-database-testing)
  - [4. Mock External Dependencies](#4-mock-external-dependencies)
  - [5. Используйте Assertions Library](#5-используйте-assertions-library)
- [Property-based Testing](#property-based-testing)
  - [Micronaut Test Resources](#micronaut-test-resources)
  - [Test Containers Integration](#test-containers-integration)
- [Performance Testing](#performance-testing)
  - [Benchmark Tests](#benchmark-tests)
- [Contract Testing](#contract-testing)
  - [Pact Testing](#pact-testing)
- [Test Configuration](#test-configuration)
  - [Test-specific Configuration](#test-specific-configuration)
  - [Test Environment Variables](#test-environment-variables)
- [Parameterized Tests](#parameterized-tests)
  - [JUnit 5 Parameterized Tests](#junit-5-parameterized-tests)
- [Test Execution Order](#test-execution-order)
  - [Ordered Tests](#ordered-tests)
- [Test Lifecycle](#test-lifecycle)
  - [Test Lifecycle Hooks](#test-lifecycle-hooks)
- [Test Fixtures](#test-fixtures)
  - [Test Data Builders](#test-data-builders)
- [Test Utilities](#test-utilities)
  - [Test Helpers](#test-helpers)
  - [Test Ordering](#test-ordering)
- [Test Parallelization](#test-parallelization)
  - [Parallel Test Execution](#parallel-test-execution)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет мощные инструменты для тестирования приложений. Благодаря **compile-time dependency injection**, тестирование становится быстрее и проще, так как не требуется полный контекст приложения для большинства тестов.

### Основные возможности

- **@MicronautTest**: Аннотация для интеграционных тестов
- **Mocking**: Поддержка **Mockito** и других **mocking** фреймворков
- **HTTP Client Testing**: тестирование **HTTP endpoints**
- **Database Testing**: Тестирование с реальными БД или **in-memory**
- **Test Containers**: Интеграция с **Testcontainers**
- **Reactive Testing**: Тестирование реактивных компонентов

## Настройка Testing

### Зависимости

**build.gradle:**

```gradle
dependencies {
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
}
```

## Unit Tests

### Простые **Unit Tests**

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private EmailService emailService;
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        userService = new UserService(userRepository, emailService);
    }
    
    @Test
    void testCreateUser() {
        // Given
        User user = new User("John", "john@example.com", 30);
        User savedUser = new User(1L, "John", "john@example.com", 30);
        
        when(userRepository.save(user)).thenReturn(savedUser);
        doNothing().when(emailService).sendWelcomeEmail(savedUser);
        
        // When
        User result = userService.createUser(user);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).save(user);
        verify(emailService).sendWelcomeEmail(savedUser);
    }
    
    @Test
    void testGetUserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUser(userId);
        });
    }
}
```

## Integration Tests

### @**MicronautTest**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class UserServiceIntegrationTest {
    
    @Inject
    UserService userService;
    
    @Inject
    UserRepository userRepository;
    
    @Test
    void testCreateAndFindUser() {
        // Given
        User user = new User("John", "john@example.com", 30);
        
        // When
        User created = userService.createUser(user);
        User found = userService.getUser(created.getId());
        
        // Then
        assertNotNull(found);
        assertEquals("John", found.getName());
        assertEquals("john@example.com", found.getEmail());
    }
}
```

### **HTTP Client Testing**

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class UserControllerTest {
    
    @Inject
    @Client("/")
    HttpClient client;
    
    @Test
    void testGetUser() {
        // When
        HttpRequest<?> request = HttpRequest.GET("/api/users/1");
        HttpResponse<User> response = client.toBlocking().exchange(request, User.class);
        
        // Then
        assertEquals(200, response.code());
        assertNotNull(response.body());
        assertEquals(1L, response.body().getId());
    }
    
    @Test
    void testCreateUser() {
        // Given
        User user = new User("John", "john@example.com", 30);
        HttpRequest<User> request = HttpRequest.POST("/api/users", user);
        
        // When
        HttpResponse<User> response = client.toBlocking().exchange(request, User.class);
        
        // Then
        assertEquals(201, response.code());
        assertNotNull(response.body());
        assertEquals("John", response.body().getName());
    }
}
```

### **Reactive Testing**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.Duration;

@MicronautTest
public class ReactiveUserServiceTest {
    
    @Inject
    UserService userService;
    
    @Test
    void testGetUserReactive() {
        // When
        Mono<User> userMono = userService.getUser(1L);
        
        // Then
        StepVerifier.create(userMono)
            .expectNextMatches(user -> 
                user.getId().equals(1L) && 
                user.getName().equals("John")
            )
            .verifyComplete();
    }
    
    @Test
    void testGetUserNotFoundReactive() {
        // When
        Mono<User> userMono = userService.getUser(999L);
        
        // Then
        StepVerifier.create(userMono)
            .expectError(UserNotFoundException.class)
            .verify();
    }
    
    @Test
    void testGetUserWithTimeout() {
        // When
        Mono<User> userMono = userService.getUser(1L)
            .timeout(Duration.ofSeconds(1));
        
        // Then
        StepVerifier.create(userMono)
            .expectNextCount(1)
            .verifyComplete();
    }
}
```

## Database Testing

### **In-Memory Database**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import io.micronaut.transaction.annotation.Transactional;

@MicronautTest(transactional = false)
public class UserRepositoryTest {
    
    @Inject
    UserRepository userRepository;
    
    @Test
    @Transactional
    void testSaveAndFind() {
        // Given
        User user = new User("John", "john@example.com", 30);
        
        // When
        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }
}
```

### **Test Containers**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

@Testcontainers
@MicronautTest
public class UserRepositoryTestContainersTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Inject
    UserRepository userRepository;
    
    @Test
    void testWithRealDatabase() {
        // Given
        User user = new User("John", "john@example.com", 30);
        
        // When
        User saved = userRepository.save(user);
        Optional<User> found = userRepository.findById(saved.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getName());
    }
}
```

## Mocking

### @**MockBean**

```java
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import static org.mockito.Mockito.*;

@MicronautTest
public class UserServiceMockTest {
    
    @Inject
    UserService userService;
    
    @Inject
    EmailService emailService;
    
    @MockBean(EmailService.class)
    EmailService emailService() {
        return mock(EmailService.class);
    }
    
    @Test
    void testCreateUserWithMock() {
        // Given
        User user = new User("John", "john@example.com", 30);
        
        // When
        User created = userService.createUser(user);
        
        // Then
        verify(emailService).sendWelcomeEmail(created);
    }
}
```

### **Spying**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import static org.mockito.Mockito.*;

@MicronautTest
public class UserServiceSpyTest {
    
    @Inject
    UserService userService;
    
    @Inject
    UserRepository userRepository;
    
    @Test
    void testWithSpy() {
        // Given
        UserRepository spy = spy(userRepository);
        User user = new User("John", "john@example.com", 30);
        
        // When
        User created = spy.save(user);
        
        // Then
        verify(spy).save(user);
    }
}
```

## Security Testing

```java
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

@MicronautTest
public class SecuredControllerTest {
    
    @Inject
    @Client("/")
    HttpClient client;
    
    @Test
    void testSecuredEndpointWithoutAuth() {
        // When
        HttpRequest<?> request = HttpRequest.GET("/api/admin/users");
        HttpResponse<?> response = client.toBlocking().exchange(request);
        
        // Then
        assertEquals(401, response.code());
    }
    
    @Test
    void testSecuredEndpointWithAuth() {
        // Given
        UsernamePasswordCredentials credentials = 
            new UsernamePasswordCredentials("admin", "password");
        HttpRequest<?> loginRequest = HttpRequest.POST("/login", credentials);
        HttpResponse<BearerAccessRefreshToken> loginResponse = 
            client.toBlocking().exchange(loginRequest, BearerAccessRefreshToken.class);
        
        String token = loginResponse.body().getAccessToken();
        
        // When
        HttpRequest<?> request = HttpRequest.GET("/api/admin/users")
            .bearerAuth(token);
        HttpResponse<?> response = client.toBlocking().exchange(request);
        
        // Then
        assertEquals(200, response.code());
    }
}
```

## Лучшие практики

### 1. Используйте @**MicronautTest** для **Integration Tests**

```java
// ✅ Хорошо
@MicronautTest
public class UserServiceIntegrationTest {
    // ...
}
```

### 2. Изолируйте тесты

```java
// ✅ Хорошо - каждый тест независим
@Test
@Transactional
void testCreateUser() {
    // Тест создает свои данные
}
```

### 3. Используйте **Test Containers** для **Real Database Testing**

```java
// ✅ Хорошо
@Testcontainers
@MicronautTest
public class DatabaseTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");
    // ...
}
```

### 4. **Mock External Dependencies**

```java
// ✅ Хорошо
@MockBean(ExternalApiClient.class)
ExternalApiClient externalApiClient() {
    return mock(ExternalApiClient.class);
}
```

### 5. Используйте **Assertions Library**

```java
// ✅ Хорошо - используйте AssertJ или Hamcrest
import static org.assertj.core.api.Assertions.*;

assertThat(user).isNotNull();
assertThat(user.getName()).isEqualTo("John");
```

## Property-based Testing

### **Micronaut Test Resources**

**application-`test.yml`:**

```yaml
micronaut:
  test:
    resources:
      enabled: true
      containers:
        postgres:
          image-name: postgres:13
          exposed-ports:
            - 5432
```

### **Test Containers Integration**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;

@Testcontainers
@MicronautTest
public class DatabaseIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @Inject
    UserRepository userRepository;
    
    @Test
    void testDatabaseOperations() {
        // Тесты с реальной БД
    }
}
```

## Performance Testing

### **Benchmark Tests**

```java
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@MicronautTest
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class PerformanceTest {
    
    @Inject
    UserService userService;
    
    @Benchmark
    public void benchmarkGetUser() {
        userService.getUser(1L);
    }
    
    @Test
    void runBenchmark() throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(this.getClass().getName() + ".*")
            .forks(1)
            .build();
        new Runner(opt).run();
    }
}
```

## Contract Testing

### **Pact Testing**

```java
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

@MicronautTest
public class ContractTest {
    
    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("user-service", this);
    
    @Pact(consumer = "my-consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
            .given("user exists")
            .uponReceiving("a request for user")
            .path("/users/1")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body("{\"id\":1,\"name\":\"John\"}")
            .toPact();
    }
    
    @Test
    @PactVerification("user-service")
    void testUserServiceContract() {
        // Тест контракта
    }
}
```

## Test Configuration

### **Test-specific Configuration**

**application-`test.yml`:**

```yaml
datasources:
  default:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver

micronaut:
  test:
    resources:
      enabled: true
```

### **Test Environment Variables**

```java
@MicronautTest(environments = "test")
public class EnvironmentTest {
    // Тесты с test environment
}
```

## Parameterized Tests

### **JUnit** 5 **Parameterized Tests**

```java
@MicronautTest
public class ParameterizedUserTest {
    
    @ParameterizedTest
    @ValueSource(ints = {18, 25, 30, 40})
    void testUsersByAge(int age) {
        List<User> users = userService.findByAge(age);
        assertThat(users).isNotEmpty();
    }
    
    @ParameterizedTest
    @CsvSource({
        "John, john@example.com",
        "Jane, jane@example.com"
    })
    void testCreateUser(String name, String email) {
        User user = userService.createUser(name, email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
    }
}
```

## Test Execution Order

### **Ordered Tests**

```java
@MicronautTest
@TestMethodOrder(OrderAnnotation.class)
public class OrderedUserTest {
    
    @Test
    @Order(1)
    void testCreateUser() {
        // Создание пользователя
    }
    
    @Test
    @Order(2)
    void testGetUser() {
        // Получение пользователя
    }
    
    @Test
    @Order(3)
    void testDeleteUser() {
        // Удаление пользователя
    }
}
```

## Test Lifecycle

### **Test Lifecycle Hooks**

```java
@MicronautTest
public class LifecycleTest {
    
    @BeforeEach
    void setUp() {
        // Настройка перед каждым тестом
    }
    
    @AfterEach
    void tearDown() {
        // Очистка после каждого теста
    }
    
    @BeforeAll
    static void setUpAll() {
        // Настройка перед всеми тестами
    }
    
    @AfterAll
    static void tearDownAll() {
        // Очистка после всех тестов
    }
}
```

## Test Fixtures

### **Test Data Builders**

```java
public class UserTestBuilder {
    private String name = "John Doe";
    private String email = "john@example.com";
    private Integer age = 30;
    
    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }
    
    public UserTestBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public UserTestBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserTestBuilder withAge(Integer age) {
        this.age = age;
        return this;
    }
    
    public User build() {
        return new User(name, email, age);
    }
}

// Использование
User user = UserTestBuilder.aUser()
    .withName("Jane")
    .withEmail("jane@example.com")
    .withAge(25)
    .build();
```

## Test Utilities

### **Test Helpers**

```java
public class TestHelpers {
    public static User createTestUser() {
        return new User("Test User", "test@example.com", 25);
    }
    
    public static List<User> createTestUsers(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new User("User " + i, "user" + i + "@example.com", 20 + i))
            .collect(Collectors.toList());
    }
}
```

## Test Execution Order

### **Test Ordering**

```java
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@MicronautTest
public class OrderedTest {
    
    @Test
    @Order(1)
    void firstTest() {
        // Первый тест
    }
    
    @Test
    @Order(2)
    void secondTest() {
        // Второй тест
    }
}
```

## Test Parallelization

### **Parallel Test Execution**

**junit-`platform.properties`:**

```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
```




## Заключение

**Micronaut** предоставляет мощные инструменты для тестирования, которые упрощают написание **unit** и **integration** тестов. Благодаря **compile-time** `DI`, тесты выполняются быстро и не требуют полного контекста приложения. Поддержка **Test Containers**, **property-based testing**, **performance testing**, **contract testing**, **parameterized tests**, **test configuration**, **lifecycle hooks**, **test fixtures**, **test utilities**, **test ordering**, **parallelization** и других продвинутых возможностей позволяет создавать надежные тестовые сценарии.

## Дополнительные ресурсы

- [**Micronaut Testing** Documentation](https://micronaut-projects.github.io/micronaut-test/latest/guide/)
- [**JUnit** 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://site.mockito.org/)
- [Testcontainers](https://testcontainers.com/)
- [Pact](https://pact.io/)
- [AssertJ](https://assertj.github.io/doc/)
