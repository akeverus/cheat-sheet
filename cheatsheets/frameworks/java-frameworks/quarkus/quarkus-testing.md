---
title: "Quarkus: Testing - Unit Tests, Integration Tests и @QuarkusTest"
description: "Полное руководство по тестированию в Quarkus: unit tests, integration tests, @QuarkusTest, mocking и best practices"
tags:
  - quarkus
  - testing
  - junit
  - mockito
  - integration-tests
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-data.md"]
updated: "2026-02-11"
related: ["quarkus-core.md", "quarkus-data.md"]
---

# Quarkus: Testing - Unit Tests, Integration Tests и @QuarkusTest



## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Quarkus: Testing - Unit Tests, Integration Tests и @QuarkusTest](#quarkus-testing-unit-tests-integration-tests-и-quarkustest)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Unit Tests](#unit-tests)
  - [Basic Unit Test](#basic-unit-test)
- [Integration Tests](#integration-tests)
  - [@QuarkusTest](#quarkustest)
- [Mocking](#mocking)
  - [@MockBean](#mockbean)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте @QuarkusTest для integration tests](#1-используйте-quarkustest-для-integration-tests)
  - [2. Используйте обычные тесты для unit tests](#2-используйте-обычные-тесты-для-unit-tests)
  - [3. Используйте Testcontainers для внешних зависимостей](#3-используйте-testcontainers-для-внешних-зависимостей)
- [Advanced Testing Patterns](#advanced-testing-patterns)
  - [Testing Reactive Code](#testing-reactive-code)
  - [Testing REST Endpoints](#testing-rest-endpoints)
  - [Testing with Testcontainers](#testing-with-testcontainers)
  - [Custom Test Resources](#custom-test-resources)
- [Mocking Strategies](#mocking-strategies)
  - [Mocking CDI Beans](#mocking-cdi-beans)
  - [Partial Mocking](#partial-mocking)
  - [Mocking Reactive Services](#mocking-reactive-services)
- [Test Profiles](#test-profiles)
  - [Использование Test Profiles](#использование-test-profiles)
- [application-test.properties](#application-testproperties)
  - [Custom Test Profile](#custom-test-profile)
- [Database Testing](#database-testing)
  - [Testing with H2](#testing-with-h2)
  - [Testing with Panache](#testing-with-panache)
  - [Transaction Management в тестах](#transaction-management-в-тестах)
- [Security Testing](#security-testing)
  - [Testing Security](#testing-security)
- [Performance Testing](#performance-testing)
  - [Load Testing](#load-testing)
  - [1. Разделяйте unit и integration тесты](#1-разделяйте-unit-и-integration-тесты)
  - [2. Используйте Testcontainers для внешних зависимостей](#2-используйте-testcontainers-для-внешних-зависимостей)
  - [3. Мокируйте только внешние зависимости](#3-мокируйте-только-внешние-зависимости)
  - [4. Используйте тестовые профили для конфигурации](#4-используйте-тестовые-профили-для-конфигурации)
  - [5. Очищайте данные между тестами](#5-очищайте-данные-между-тестами)
- [Contract Testing](#contract-testing)
  - [Pact Testing](#pact-testing)
  - [JMeter Integration](#jmeter-integration)
  - [6. Используйте contract testing для микросервисов](#6-используйте-contract-testing-для-микросервисов)
  - [Test Data Builders](#test-data-builders)
  - [Parameterized Tests](#parameterized-tests)
  - [Test Fixtures](#test-fixtures)
  - [Test Containers for Integration Testing](#test-containers-for-integration-testing)
  - [Contract Testing with Pact](#contract-testing-with-pact)
- [Test Data Management](#test-data-management)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** предоставляет мощные инструменты для тестирования, включая @**QuarkusTest** для интеграционных тестов, поддержку **Mockito** и другие возможности для создания надежных тестов.

### Основные возможности

- **@QuarkusTest**: Интеграционные тесты
- **Unit Tests**: Обычные **unit** тесты
- **Mocking**: Поддержка **Mockito**
- **Test Containers**: Интеграция с **Testcontainers**

## Unit Tests

### **Basic Unit Test**

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    
    @Test
    void testCreateUser() {
        UserService service = new UserService();
        User user = service.createUser(new User("John", "john@example.com"));
        assertNotNull(user);
        assertEquals("John", user.getName());
    }
}
```

## Integration Tests

### @**QuarkusTest**

```java
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserResourceTest {
    
    @Test
    void testGetUser() {
        given()
            .when().get("/api/users/1")
            .then()
            .statusCode(200)
            .body("name", is("John"));
    }
}
```

## Mocking

### @**MockBean**

```java
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

@QuarkusTest
public class UserResourceTest {
    
    @BeforeEach
    void setup() {
        UserService mockService = Mockito.mock(UserService.class);
        Mockito.when(mockService.findById(1L))
            .thenReturn(new User("John", "john@example.com"));
        QuarkusMock.installMockForType(mockService, UserService.class);
    }
}
```

## Лучшие практики

### 1. Используйте @**QuarkusTest** для **integration tests**

```java
// ✅ Хорошо
@QuarkusTest
public class IntegrationTest {
    // Интеграционный тест
}
```

### 2. Используйте обычные тесты для **unit tests**

```java
// ✅ Хорошо
public class UnitTest {
    // Unit тест без @QuarkusTest
}
```

### 3. Используйте **Testcontainers** для внешних зависимостей

```java
// ✅ Хорошо
@QuarkusTestResource(PostgresResource.class)
public class DatabaseTest {
    // Тест с Testcontainers
}
```

## Advanced Testing Patterns

### **Testing Reactive Code**

**Тестирование **reactive** кода с **Uni** и **Multi**:**

```java
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ReactiveServiceTest {
    
    @Inject
    ReactiveUserService service;
    
    @Test
    void testReactiveOperation() {
        Uni<User> result = service.getUser(1L);
        
        result
            .subscribe().withSubscriber(UniAssertSubscriber.create())
            .assertCompleted()
            .assertItem(user -> {
                assertEquals("John", user.getName());
            });
    }
}
```

### **Testing REST Endpoints**

**Тестирование **REST endpoints** с **REST Assured**:**

```java
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class UserResourceTest {
    
    @Test
    void testGetUser() {
        given()
            .pathParam("id", 1)
            .when().get("/api/users/{id}")
            .then()
            .statusCode(200)
            .body("name", is("John"))
            .body("email", is("john@example.com"));
    }
    
    @Test
    void testCreateUser() {
        User user = new User("Jane", "jane@example.com");
        
        given()
            .contentType("application/json")
            .body(user)
            .when().post("/api/users")
            .then()
            .statusCode(201)
            .body("name", is("Jane"));
    }
}
```

### **Testing with Testcontainers**

**Использование **Testcontainers** для интеграционных тестов:**

```java
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@QuarkusTest
@Testcontainers
@QuarkusTestResource(PostgresTestResource.class)
public class DatabaseTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Test
    void testDatabaseOperation() {
        // Тест с реальной БД в контейнере
    }
}
```

### **Custom Test Resources**

**Создание кастомных **test resources**:**

```java
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;

public class CustomTestResource implements QuarkusTestResourceLifecycleManager {
    
    @Override
    public Map<String, String> start() {
        // Инициализация тестового ресурса
        return Map.of(
            "quarkus.datasource.jdbc.url", "jdbc:h2:mem:test",
            "custom.property", "test-value"
        );
    }
    
    @Override
    public void stop() {
        // Очистка ресурса
    }
}
```

## Mocking Strategies

### **Mocking CDI Beans**

**Мокирование **CDI** бинов:**

```java
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

@QuarkusTest
public class ServiceTest {
    
    @BeforeEach
    void setup() {
        UserService mockService = Mockito.mock(UserService.class);
        Mockito.when(mockService.findById(1L))
            .thenReturn(new User("John", "john@example.com"));
        
        QuarkusMock.installMockForType(mockService, UserService.class);
    }
}
```

### **Partial Mocking**

**Частичное мокирование:**

```java
import org.mockito.Mockito;

@QuarkusTest
public class PartialMockTest {
    
    @BeforeEach
    void setup() {
        UserService service = Mockito.spy(UserService.class);
        Mockito.doReturn(new User("Mocked", "mocked@example.com"))
            .when(service).findById(1L);
        
        QuarkusMock.installMockForType(service, UserService.class);
    }
}
```

### **Mocking Reactive Services**

**Мокирование **reactive** сервисов:**

```java
import io.smallrye.mutiny.Uni;
import org.mockito.Mockito;

@BeforeEach
void setupReactiveMock() {
    ReactiveService mockService = Mockito.mock(ReactiveService.class);
    Mockito.when(mockService.getUser(1L))
        .thenReturn(Uni.createFrom().item(new User("John", "john@example.com")));
    
    QuarkusMock.installMockForType(mockService, ReactiveService.class);
}
```

## Test Profiles

### Использование **Test Profiles**

**Создание тестовых профилей:**

```properties
# application-test.properties
quarkus.datasource.jdbc.url=jdbc:h2:mem:test
quarkus.hibernate.orm.database.generation=drop-and-create
quarkus.log.level=DEBUG
```

**Активация профиля:**

```java
@QuarkusTest
@TestProfile(TestProfile.class)
public class ProfileTest {
    // Тест с активированным профилем
}
```

### **Custom Test Profile**

```java
import io.quarkus.test.junit.TestProfile;
import java.util.Map;

@TestProfile
public class CustomTestProfile implements QuarkusTestProfile {
    
    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "custom.property", "test-value",
            "quarkus.datasource.jdbc.url", "jdbc:h2:mem:test"
        );
    }
}
```

## Database Testing

### **Testing with H2**

**Тестирование с **H2 in-memory** БД:**

```properties
# application-test.properties
quarkus.datasource.jdbc.url=jdbc:h2:mem:testdb
quarkus.hibernate.orm.database.generation=drop-and-create
```

### **Testing with Panache**

**Тестирование **Panache entities**:**

```java
@QuarkusTest
public class PanacheTest {
    
    @Test
    void testPanacheQuery() {
        User user = new User("John", "john@example.com");
        user.persist();
        
        User found = User.findByName("John").await().indefinitely();
        assertNotNull(found);
        assertEquals("John", found.name);
    }
}
```

### **Transaction Management** в тестах

**Управление транзакциями в тестах:**

```java
import jakarta.transaction.Transactional;

@QuarkusTest
public class TransactionTest {
    
    @Test
    @Transactional
    void testWithTransaction() {
        User user = new User("John", "john@example.com");
        user.persist();
        
        // Транзакция будет откачена после теста
    }
}
```

## Security Testing

### **Testing Security**

**Тестирование **security**:**

```java
import io.quarkus.security.test.TestSecurity;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest {
    
    @Test
    @TestSecurity(user = "admin", roles = {"admin"})
    void testAdminEndpoint() {
        given()
            .when().get("/api/admin/users")
            .then()
            .statusCode(200);
    }
    
    @Test
    @TestSecurity(user = "user", roles = {"user"})
    void testUserEndpoint() {
        given()
            .when().get("/api/admin/users")
            .then()
            .statusCode(403);  // Forbidden
    }
}
```

## Performance Testing

### **Load Testing**

**Нагрузочное тестирование:**

```java
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@QuarkusTest
public class LoadTest {
    
    @Test
    void testConcurrentRequests() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CompletableFuture<?>[] futures = new CompletableFuture[100];
        
        for (int i = 0; i < 100; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                given()
                    .when().get("/api/users/1")
                    .then()
                    .statusCode(200);
            }, executor);
        }
        
        CompletableFuture.allOf(futures).join();
        executor.shutdown();
    }
}
```

## Contract Testing

### **Pact Testing**

**Использование **Pact** для **contract testing**:**

```java
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import org.junit.Rule;
import org.junit.Test;

public class PactContractTest {
    
    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("user-service", "localhost", 8080, this);
    
    @Pact(consumer = "my-consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
            .given("user exists")
            .uponReceiving("a request for user")
            .path("/users/1")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .stringType("name", "John")
                .stringType("email", "john@example.com"))
            .toPact();
    }
    
    @Test
    @PactVerification("user-service")
    public void testUserService() {
        // Тест с использованием Pact
    }
}
```

## Performance Testing

### **JMeter Integration**

**Интеграция с **JMeter**:**

```java
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@QuarkusTest
public class PerformanceTest {
    
    @Test
    void testConcurrentLoad() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(50);
        
        for (int i = 0; i < 1000; i++) {
            executor.submit(() -> {
                given()
                    .when().get("/api/users/1")
                    .then()
                    .statusCode(200);
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
```

## Advanced Testing Patterns

### **Test Data Builders**

**Строители тестовых данных:**

```java
public class UserTestDataBuilder {
    private String name = "Test User";
    private String email = "test@example.com";
    private Integer age = 25;
    
    public UserTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public User build() {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return user;
    }
}
```

### **Parameterized Tests**

**Параметризованные тесты:**

```java
@QuarkusTest
public class ParameterizedTest {
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testWithParameters(int value) {
        assertTrue(value > 0);
    }
    
    @ParameterizedTest
    @MethodSource("userProvider")
    void testWithMethodSource(User user) {
        assertNotNull(user.getName());
    }
    
    static Stream<User> userProvider() {
        return Stream.of(
            new User("User1", "user1@example.com"),
            new User("User2", "user2@example.com")
        );
    }
}
```

### **Test Fixtures**

**Тестовые фикстуры:**

```java
@QuarkusTest
public class TestWithFixtures {
    
    @BeforeEach
    void setup() {
        // Создание тестовых данных
        User user = new User("Test User", "test@example.com");
        user.persist();
    }
    
    @AfterEach
    void cleanup() {
        // Очистка после тестов
        User.deleteAll();
    }
}
```

## Advanced Testing Patterns

### **Test Containers for Integration Testing**

**Использование **Testcontainers** для интеграционных тестов:**

```java
@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
public class IntegrationTest {
    
    @Test
    void testDatabaseIntegration() {
        // Тест с реальной БД в контейнере
        User user = new User("Test", "test@example.com");
        user.persist();
        
        assertNotNull(user.id);
    }
}
```

### **Contract Testing with Pact**

**Contract testing**:**

```java
@Provider("user-service")
@PactFolder("pacts")
public class UserServiceContractTest {
    
    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
    
    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", 8080));
    }
}
```

### **Performance Testing**

**Тестирование производительности:**

```java
@QuarkusTest
public class PerformanceTest {
    
    @Test
    void testResponseTime() {
        long startTime = System.currentTimeMillis();
        
        given()
            .when().get("/api/users")
            .then()
            .statusCode(200);
        
        long duration = System.currentTimeMillis() - startTime;
        assertTrue(duration < 1000, "Response time should be less than 1 second");
    }
}
```

## Test Data Management

### **Test Data Builders**

**Строители тестовых данных:**

```java
public class UserTestDataBuilder {
    private String name = "Default Name";
    private String email = "default@example.com";
    private Integer age = 25;
    
    public static UserTestDataBuilder aUser() {
        return new UserTestDataBuilder();
    }
    
    public UserTestDataBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public UserTestDataBuilder withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public User build() {
        User user = new User();
        user.name = name;
        user.email = email;
        user.age = age;
        return user;
    }
}
```

### **Test Fixtures**

**Тестовые фикстуры:**

```java
@QuarkusTest
public class TestWithFixtures {
    
    @BeforeEach
    void setup() {
        // Создание тестовых данных
        User user = UserTestDataBuilder.aUser()
            .withName("Test User")
            .withEmail("test@example.com")
            .build();
        user.persist();
    }
    
    @AfterEach
    void cleanup() {
        // Очистка после тестов
        User.deleteAll();
    }
}
```


## Заключение

**Quarkus Testing** предоставляет мощные инструменты для тестирования приложений. Поддержка **unit** и **integration** тестов, **mocking**, **Testcontainers**, **contract testing**, **performance testing** и других продвинутых возможностей позволяет создавать надежные тестовые сценарии. Правильное использование тестовых паттернов, мокирование, управление тестовыми данными, конфигурацией, **contract testing** и **performance testing** являются ключевыми аспектами создания качественных тестов.

## Дополнительные ресурсы

- [**Quarkus Testing** Guide](https://quarkus.io/guides/getting-started-testing)
- [**JUnit** 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://site.mockito.org/)
- [Testcontainers](https://testcontainers.com/)
- [**REST Assured**](https://rest-assured.io/)
- [Pact](https://pact.io/)
