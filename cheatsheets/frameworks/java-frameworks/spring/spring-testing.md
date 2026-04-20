---
title: "Spring Testing: Полное руководство по тестированию"
description: "Комплексное руководство по Spring Testing: @SpringBootTest, @WebMvcTest, @DataJpaTest, MockMvc, Testcontainers, mocking и best practices"
tags:
  - spring
  - testing
  - junit
  - mockito
  - testcontainers
  - integration-tests
  - java
difficulty: "intermediate"
prerequisites: ["spring/spring-core.md", "spring/spring-boot.md"]
next: ["spring/spring-security.md", "spring/spring-data-jpa.md"]
updated: "2026-04-20"
related: ["spring/spring-boot.md", "java/java-basics.md"]
---

# Spring Testing: Полное руководство по тестированию


### См. также
- [[spring-testing-interview|Вопросы на собеседовании]] — подготовка к интервью

## Полезные ссылки

[Официальная документация Spring](https://docs.spring.io/)
[Spring Projects](https://spring.io/projects)

## Содержание

- [Введение в Spring Testing](#введение-в-spring-testing)
  - [Основные возможности](#основные-возможности)
  - [Типы тестов](#типы-тестов)
- [Настройка тестирования](#настройка-тестирования)
  - [Зависимости](#зависимости)
  - [Структура тестов](#структура-тестов)
- [@SpringBootTest](#springboottest)
  - [Базовое использование](#базовое-использование)
  - [Настройка контекста](#настройка-контекста)
  - [Использование профилей](#использование-профилей)
- [@WebMvcTest](#webmvctest)
  - [Базовое использование](#базовое-использование-1)
  - [Тестирование с валидацией](#тестирование-с-валидацией)
- [@DataJpaTest](#datajpatest)
  - [Базовое использование](#базовое-использование-2)
  - [Использование SQL скриптов](#использование-sql-скриптов)
- [MockMvc](#mockmvc)
  - [Базовые операции](#базовые-операции)
  - [Тестирование с заголовками](#тестирование-с-заголовками)
- [Testcontainers](#testcontainers)
  - [Настройка Testcontainers](#настройка-testcontainers)
  - [Использование с PostgreSQL](#использование-с-postgresql)
  - [Использование с Redis](#использование-с-redis)
- [Mocking](#mocking)
  - [@MockBean](#mockbean)
  - [@SpyBean](#spybean)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте правильные аннотации для типа теста](#1-используйте-правильные-аннотации-для-типа-теста)
  - [2. Изолируйте тесты](#2-изолируйте-тесты)
  - [3. Используйте Testcontainers для реальных БД](#3-используйте-testcontainers-для-реальных-бд)
  - [4. Mock внешние зависимости](#4-mock-внешние-зависимости)
  - [5. Используйте AssertJ для читаемых assertions](#5-используйте-assertj-для-читаемых-assertions)
- [Продвинутое тестирование](#продвинутое-тестирование)
  - [Тестирование с @MockBean и @SpyBean](#тестирование-с-mockbean-и-spybean)
  - [Тестирование с @TestConfiguration](#тестирование-с-testconfiguration)
  - [Тестирование с @DynamicPropertySource](#тестирование-с-dynamicpropertysource)
  - [Тестирование WebFlux](#тестирование-webflux)
  - [Тестирование с @Sql](#тестирование-с-sql)
  - [Тестирование транзакций](#тестирование-транзакций)
  - [Тестирование с @DirtiesContext](#тестирование-с-dirtiescontext)
  - [Тестирование с @TestPropertySource](#тестирование-с-testpropertysource)
  - [Тестирование с @ActiveProfiles](#тестирование-с-activeprofiles)
  - [Тестирование с @MockitoSettings](#тестирование-с-mockitosettings)
  - [Тестирование производительности](#тестирование-производительности)
  - [Тестирование с WireMock](#тестирование-с-wiremock)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в Spring Testing

**Spring Testing** предоставляет мощные инструменты для тестирования **Spring** приложений на всех уровнях: от **unit** тестов до **integration** тестов. **Spring Boot** дополнительно упрощает тестирование с помощью автоматической конфигурации тестового контекста.

### Основные возможности

- **@SpringBootTest**: **Integration** тесты с полным контекстом приложения
- **@WebMvcTest**: Тестирование веб-слоя без полного контекста
- **@DataJpaTest**: Тестирование **JPA** репозиториев
- **MockMvc**: Тестирование контроллеров
- **Testcontainers**: Интеграция с реальными базами данных
- **Mocking**: Поддержка **Mockito** и других **mocking** фреймворков

### Типы тестов

1. **Unit Tests**: Тестирование отдельных компонентов изолированно
2. **Integration Tests**: Тестирование взаимодействия компонентов
3. **Web Layer Tests**: тестирование контроллеров и веб-слоя
4. **Data Layer Tests**: тестирование репозиториев и БД
5. **End-`to-End` Tests**: Тестирование всего приложения

## Настройка тестирования

### Зависимости

**Зависимость **spring-`boot-starter`-test** (pom.xml):**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Структура тестов

**Структура каталогов тестов (для справки):**

```text
src/test/java/
├── com/example/
│   ├── unit/
│   │   └── UserServiceTest.java
│   ├── integration/
│   │   └── UserIntegrationTest.java
│   ├── web/
│   │   └── UserControllerTest.java
│   └── repository/
│       └── UserRepositoryTest.java
```

## @SpringBootTest

@**SpringBootTest** загружает полный контекст приложения для **integration** тестов.

### Базовое использование

```java
// Интеграционный тест с полной загрузкой контекста
@SpringBootTest
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateUser() {
        // Given
        User user = new User("John", "john@example.com", 30);

        // When
        User created = userService.create(user);

        // Then
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("John");

        Optional<User> found = userRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John");
    }
}
```

### Настройка контекста

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {
    // Тесты без веб-контекста
}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {
    // Тесты с веб-сервером на случайном порту

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetUser() {
        ResponseEntity<User> response = restTemplate.getForEntity(
            "/api/users/1", User.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

### Использование профилей

```java
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    // Использует application-test.properties
}
```

## @WebMvcTest

@**WebMvcTest** загружает только веб-слой для тестирования контроллеров.

### Базовое использование

```java
// Срез-тест только контроллера (без полного контекста)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        // Given
        User user = new User(1L, "John", "john@example.com", 30);
        when(userService.findById(1L)).thenReturn(user);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testCreateUser() throws Exception {
        // Given
        User user = new User("John", "john@example.com", 30);
        User created = new User(1L, "John", "john@example.com", 30);
        when(userService.create(any(User.class))).thenReturn(created);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John\",\"email\":\"john@example.com\",\"age\":30}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John"));
    }
}
```

### Тестирование с валидацией

```java
@WebMvcTest(UserController.class)
class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateUserWithInvalidData() throws Exception {
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"email\":\"invalid-email\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists());
    }
}
```

## @DataJpaTest

@**DataJpaTest** загружает только **JPA** компоненты для тестирования репозиториев.

### Базовое использование

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSaveUser() {
        // Given
        User user = new User("John", "john@example.com", 30);

        // When
        User saved = userRepository.save(user);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("John");

        User found = entityManager.find(User.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John");
    }

    @Test
    void testFindByEmail() {
        // Given
        User user = new User("John", "john@example.com", 30);
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmail("john@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }
}
```

### Использование SQL скриптов

```java
@DataJpaTest
@Sql("/test-data.sql")
class UserRepositoryTest {
    // Тестовые данные загружаются из test-data.sql
}

@DataJpaTest
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserRepositoryWithCleanupTest {
    // Данные загружаются перед тестом и очищаются после
}
```

## MockMvc

**MockMvc** позволяет тестировать контроллеры без запуска сервера.

### Базовые операции

```java
@WebMvcTest(UserController.class)
class UserControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testGetUser() throws Exception {
        User user = new User(1L, "John", "john@example.com", 30);
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void testCreateUser() throws Exception {
        User user = new User("John", "john@example.com", 30);
        User created = new User(1L, "John", "john@example.com", 30);
        when(userService.create(any(User.class))).thenReturn(created);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/users/1"));
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User(1L, "Jane", "jane@example.com", 25);
        when(userService.update(eq(1L), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Jane"));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
            .andExpect(status().isNoContent());
    }
}
```

### Тестирование с заголовками

```java
@Test
void testGetUserWithAuth() throws Exception {
    mockMvc.perform(get("/api/users/1")
            .header("Authorization", "Bearer token123"))
        .andExpect(status().isOk());
}

@Test
void testGetUserWithCustomHeader() throws Exception {
    mockMvc.perform(get("/api/users/1")
            .header("X-Custom-Header", "value"))
        .andExpect(status().isOk())
        .andExpect(header().string("X-Response-Header", "value"));
}
```

## Testcontainers

**Testcontainers** позволяет использовать реальные базы данных в тестах.

### Настройка Testcontainers

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

### Использование с PostgreSQL

```java
@SpringBootTest
@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
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
    void testSaveAndFind() {
        User user = new User("John", "john@example.com", 30);
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John");
    }
}
```

### Использование с Redis

```java
@SpringBootTest
@Testcontainers
class CacheServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:6-alpine")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private CacheService cacheService;

    @Test
    void testCache() {
        cacheService.put("key", "value");
        String value = cacheService.get("key");
        assertThat(value).isEqualTo("value");
    }
}
```

## Mocking

### @MockBean

```java
@SpringBootTest
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void testFindById() {
        // Given
        User user = new User(1L, "John", "john@example.com", 30);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User found = userService.findById(1L);

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("John");
        verify(userRepository).findById(1L);
    }
}
```

### @SpyBean

```java
@SpringBootTest
class UserServiceSpyTest {

    @SpyBean
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser() {
        User user = new User("John", "john@example.com", 30);
        User created = userService.create(user);

        verify(emailService).sendWelcomeEmail(created);
    }
}
```

## Лучшие практики

### 1. Используйте правильные аннотации для типа теста

```java
// ✅ Хорошо - для integration тестов
@SpringBootTest

// ✅ Хорошо - для веб-слоя
@WebMvcTest

// ✅ Хорошо - для репозиториев
@DataJpaTest
```

### 2. Изолируйте тесты

```java
// ✅ Хорошо - каждый тест независим
@Test
@Transactional
@Rollback
void testCreateUser() {
    // Тест создает и очищает свои данные
}
```

### 3. Используйте Testcontainers для реальных БД

```java
// ✅ Хорошо
@Testcontainers
@SpringBootTest
class DatabaseTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");
}
```

### 4. Mock внешние зависимости

```java
// ✅ Хорошо
@MockBean
private ExternalApiClient externalApiClient;
```

### 5. Используйте AssertJ для читаемых assertions

```java
// ✅ Хорошо
assertThat(user).isNotNull();
assertThat(user.getName()).isEqualTo("John");
assertThat(user.getEmail()).contains("@");

// ❌ Плохо
assertNotNull(user);
assertEquals("John", user.getName());
assertTrue(user.getEmail().contains("@"));
```

## Продвинутое тестирование

### Тестирование с @MockBean и @SpyBean

```java
@SpringBootTest
class MockBeanTest {

    @MockBean
    private UserRepository userRepository;

    @SpyBean
    private UserService userService;

    @Test
    void testWithMockBean() {
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(new User("John", "john@example.com")));

        User user = userService.findUserById(1L);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John");
        verify(userRepository, times(1)).findById(1L);
    }
}
```

### Тестирование с @TestConfiguration

```java
@SpringBootTest
class TestConfigurationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public UserRepository testUserRepository() {
            return new InMemoryUserRepository();
        }
    }

    @Autowired
    private UserService userService;

    @Test
    void testWithTestConfiguration() {
        User user = userService.findUserById(1L);
        assertThat(user).isNotNull();
    }
}
```

### Тестирование с @DynamicPropertySource

```java
@SpringBootTest
@Testcontainers
class DynamicPropertySourceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testWithDynamicProperties() {
        // Тест использует динамически настроенные свойства
    }
}
```

### Тестирование WebFlux

```java
@SpringBootTest
@AutoConfigureWebTestClient
class WebFluxTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testWebFluxEndpoint() {
        webTestClient.get()
            .uri("/api/users")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(User.class)
            .hasSize(2);
    }

    @Test
    void testWebFluxPost() {
        User user = new User("John", "john@example.com");

        webTestClient.post()
            .uri("/api/users")
            .bodyValue(user)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(User.class)
            .value(u -> assertThat(u.getName()).isEqualTo("John"));
    }
}
```

### Тестирование с @Sql

```java
@SpringBootTest
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SqlTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testWithSqlScripts() {
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(3);
    }
}
```

### Тестирование транзакций

```java
@SpringBootTest
@Transactional
class TransactionalTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    void testWithRollback() {
        User user = new User("Test", "test@example.com");
        userRepository.save(user);

        assertThat(userRepository.count()).isEqualTo(1);
        // После теста изменения откатятся
    }

    @Test
    @Commit
    void testWithCommit() {
        User user = new User("Test", "test@example.com");
        userRepository.save(user);

        // Изменения будут закоммичены
    }
}
```

### Тестирование с @DirtiesContext

```java
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DirtiesContextTest {

    @Test
    void test1() {
        // Контекст будет пересоздан после этого теста
    }

    @Test
    void test2() {
        // Контекст будет пересоздан после этого теста
    }
}
```

### Тестирование с @TestPropertySource

```java
@SpringBootTest
@TestPropertySource(properties = {
    "app.feature.enabled=true",
    "app.timeout=5000"
})
class PropertySourceTest {

    @Value("${app.feature.enabled}")
    private boolean featureEnabled;

    @Test
    void testWithProperties() {
        assertThat(featureEnabled).isTrue();
    }
}
```

### Тестирование с @ActiveProfiles

```java
@SpringBootTest
@ActiveProfiles("test")
class ProfileTest {

    @Test
    void testWithTestProfile() {
        // Тест использует test профиль
    }
}
```

### Тестирование с @MockitoSettings

```java
@SpringBootTest
@MockitoSettings(strictness = Strictness.LENIENT)
class MockitoSettingsTest {

    @MockBean
    private UserRepository userRepository;

    @Test
    void testWithLenientMocks() {
        // Неиспользуемые моки не вызовут ошибок
    }
}
```

### Тестирование производительности

```java
@SpringBootTest
class PerformanceTest {

    @Autowired
    private UserService userService;

    @Test
    void testPerformance() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            userService.findUserById(1L);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertThat(duration).isLessThan(1000); // Меньше 1 секунды
    }
}
```

### Тестирование с WireMock

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class WireMockTest {

    @Test
    void testWithWireMock() {
        stubFor(get(urlEqualTo("/api/external"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"data\": \"test\"}")));

        // Тест с мокированным внешним API
    }
}
```


## Заключение

**Spring Testing** предоставляет мощные инструменты для тестирования приложений на всех уровнях. Правильное использование @**SpringBootTest**, @**WebMvcTest**, @**DataJpaTest**, **MockMvc**, **Testcontainers**, @**MockBean**, @**SpyBean**, @**TestConfiguration**, @**DynamicPropertySource**, **WebFlux** тестирования, @**Sql**, транзакционного тестирования, @**DirtiesContext**, @**TestPropertySource**, @**ActiveProfiles**, производительности и других продвинутых возможностей позволяет создавать надежные, поддерживаемые и эффективные тесты.

## Дополнительные ресурсы

- [**Spring Testing** Documentation](https://docs.spring.io/spring-framework/reference/testing.html)
- [**Spring Boot** Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Testcontainers Documentation](https://testcontainers.com/)
- [Mockito Documentation](https://site.mockito.org/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [WireMock Documentation](https://wiremock.org/docs/)

## См. также

- [[spring-actuator|Spring Actuator: Полное руководство по мониторингу и управлению]]
- [[spring-ai|Spring AI]]
- [[spring-aop|Spring AOP: Полное руководство по аспектно-ориентированному программированию]]
- [[spring-batch|Spring Batch для Java]]
- [[spring-boot|Spring Boot — Полное руководство]]
