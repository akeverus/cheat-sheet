---
title: "Вопросы на собеседовании: Spring Boot Testing"
description: "Spring Boot тестирование: @SpringBootTest, @WebMvcTest, @DataJpaTest, MockMvc, @MockBean, test slices, ApplicationContext caching, @TestConfiguration"
tags:
  - interview
  - spring
  - spring-testing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Boot Testing"
  - "Spring Boot Testing interview"
  - "@WebMvcTest interview"
prerequisites:
  - "[[spring-testing]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Spring Boot Testing`

Spring Boot предоставляет мощную тестовую инфраструктуру: от лёгких unit-тестов до полных интеграционных тестов с реальным HTTP-сервером. Ключевая тема — **test slices**: загрузка только нужной части контекста для скорости.

Дата последнего обновления: 2026-04-20.

## Полезные ссылки

### Официальная документация

- [Spring Boot Testing Reference](https://docs.spring.io/spring-boot/reference/testing/index.html) — полная документация
- [Spring Test Reference](https://docs.spring.io/spring-framework/reference/testing.html)

### Baeldung tutorials

- [Testing in Spring Boot](https://www.baeldung.com/spring-boot-testing) — обзор
- [MockMvc vs WebMvcTest](https://www.baeldung.com/spring-mockmvc-vs-webmvctest)
- [Integration Testing in Spring](https://www.baeldung.com/integration-testing-in-spring)
- [Spring Security Integration Tests](https://www.baeldung.com/spring-security-integration-tests)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**@SpringBootTest**
- [Q1. (!) Что делает `@SpringBootTest` и когда использовать?](#q1-что-делает-springboottest-и-когда-использовать)
- [Q2. Какие режимы `webEnvironment` есть в `@SpringBootTest`?](#q2-какие-режимы-webenvironment-есть-в-springboottest)

**Test Slices**
- [Q3. (!) Что такое test slices и зачем нужны?](#q3-что-такое-test-slices-и-зачем-нужны)
- [Q4. (!) Что такое `@WebMvcTest` и чем отличается от `@SpringBootTest`?](#q4-что-такое-webmvctest-и-чем-отличается-от-springboottest)
- [Q5. Что такое `@DataJpaTest` и как настроить?](#q5-что-такое-datajpatest-и-как-настроить)
- [Q6. Какие ещё test slices есть в Spring Boot?](#q6-какие-ещё-test-slices-есть-в-spring-boot)

**MockMvc**
- [Q7. (!) Что такое `MockMvc` и как им пользоваться?](#q7-что-такое-mockmvc-и-как-им-пользоваться)
- [Q8. Как тестировать REST endpoint с JSON через MockMvc?](#q8-как-тестировать-rest-endpoint-с-json-через-mockmvc)

**@MockBean и @SpyBean**
- [Q9. (!) Чем `@MockBean` отличается от `@Mock` из Mockito?](#q9-чем-mockbean-отличается-от-mock-из-mockito)
- [Q10. Что такое `@SpyBean` и когда нужен?](#q10-что-такое-spybean-и-когда-нужен)

**Конфигурация тестов**
- [Q11. Что такое `@TestConfiguration` и зачем нужна?](#q11-что-такое-testconfiguration-и-зачем-нужна)
- [Q12. Что такое `@DirtiesContext` и когда использовать?](#q12-что-такое-dirtiescontext-и-когда-использовать)
- [Q13. (!) Как Spring Boot кеширует ApplicationContext в тестах?](#q13-как-spring-boot-кеширует-applicationcontext-в-тестах)

**Security и интеграция**
- [Q14. Как тестировать Spring Security (аутентификацию/авторизацию)?](#q14-как-тестировать-spring-security-аутентификациюавторизацию)
- [Q15. Как использовать Testcontainers с Spring Boot?](#q15-как-использовать-testcontainers-с-spring-boot)

---

## Q1. (!) Что делает `@SpringBootTest` и когда использовать?

Что делает `@SpringBootTest` и когда использовать?

`@SpringBootTest` загружает **полный ApplicationContext** Spring Boot: все бины, автоконфигурации, `application.yml`.

```java
@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void createOrder_shouldPersistToDatabase() {
        Order order = orderService.create(new OrderRequest("CUST-1"));
        assertThat(orderRepository.findById(order.getId())).isPresent();
    }
}
```

**Когда использовать:**
- Интеграционные тесты, проверяющие взаимодействие нескольких компонентов
- Тесты с реальной БД / Testcontainers
- Тесты на HTTP-уровне с `RANDOM_PORT`

**Когда НЕ использовать:**
- Unit-тесты отдельных классов — не нужен Spring-контекст
- Тесты контроллеров — лучше `@WebMvcTest`
- Тесты репозиториев — лучше `@DataJpaTest`

`@SpringBootTest` — самый медленный тип: запускает весь контекст. Test slices (Q3-Q6) запускаются значительно быстрее.

## Q2. Какие режимы `webEnvironment` есть в `@SpringBootTest`?

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

| Режим | Поведение |
|---|---|
| `MOCK` (default) | Фиктивный web-environment, MockMvc без реального сервера |
| `RANDOM_PORT` | Запускает реальный сервер на случайном порту |
| `DEFINED_PORT` | Реальный сервер на порту из конфигурации (8080) |
| `NONE` | Не загружает web-environment вообще |

```java
// RANDOM_PORT — для тестирования через TestRestTemplate/WebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
class OrderControllerHttpTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createOrder_returns201() {
        ResponseEntity<Order> resp = restTemplate.postForEntity(
            "/api/orders", new OrderRequest("CUST-1"), Order.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
```

`RANDOM_PORT` — для end-to-end тестов через HTTP. `MOCK` — для тестов через MockMvc.

## Q3. (!) Что такое test slices и зачем нужны?

**Test slice** — тестовая аннотация, загружающая только определённый слой приложения, не поднимая весь контекст.

```
Полный @SpringBootTest:  [Web] + [Service] + [Persistence] + [Security] + ...
@WebMvcTest:             [Web] + моки остального
@DataJpaTest:            [Persistence] + моки остального
```

**Преимущества:**
- Значительно быстрее (меньше бинов для инициализации)
- Изоляция слоя (не влияет соседний код)
- Встроенная конфигурация (in-memory DB для @DataJpaTest)

**Принцип:** test slice загружает только бины, относящиеся к нужному слою. Всё остальное нужно мокировать через `@MockBean`.

## Q4. (!) Что такое `@WebMvcTest` и чем отличается от `@SpringBootTest`?

`@WebMvcTest` загружает **только web-слой**: контроллеры, фильтры, `WebMvcConfigurer`, `HandlerMethodArgumentResolver`. Сервисы и репозитории — нужно мокировать.

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;  // моки сервиса

    @MockBean
    private OrderMapper mapper;

    @Test
    void createOrder_returns201() throws Exception {
        Order order = new Order(1L, "CUST-1");
        when(orderService.create(any())).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                .contentType(APPLICATION_JSON)
                .content("""{ "customerId": "CUST-1" }"""))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

| Параметр | `@WebMvcTest` | `@SpringBootTest(MOCK)` |
|---|---|---|
| Загружаемые бины | Только web-слой | Все бины |
| Сервисы/репозитории | Нужен `@MockBean` | Реальные (или `@MockBean`) |
| Скорость | Быстрее | Медленнее |
| MockMvc | Автоматически | Нужен `@AutoConfigureMockMvc` |
| Spring Security | Загружается | Загружается |

**Используйте `@WebMvcTest` для:**
- Проверки mapping URL → контроллер
- Проверки валидации (`@Valid`)
- Проверки маппинга запрос/ответ
- Проверки HTTP-статусов

## Q5. Что такое `@DataJpaTest` и как настроить?

`@DataJpaTest` загружает **только JPA-слой**: Entity, Repository, JPA-конфигурацию. По умолчанию — in-memory H2.

```java
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;  // удобный helper для тестов

    @Test
    void findByEmail_shouldReturnUser() {
        entityManager.persist(new User("alice@mail.com", "Alice"));
        entityManager.flush();

        Optional<User> found = userRepository.findByEmail("alice@mail.com");
        assertThat(found).isPresent().get().extracting(User::getName).isEqualTo("Alice");
    }

    @Test
    void save_withDuplicateEmail_throwsException() {
        entityManager.persist(new User("bob@mail.com", "Bob"));
        entityManager.flush();

        assertThatThrownBy(() -> userRepository.saveAndFlush(new User("bob@mail.com", "Bob2")))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
}
```

**Настройка для реальной БД:**

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// replace=NONE → использовать настроенную БД (Testcontainers, например)
class UserRepositoryRealDbTest { }
```

По умолчанию `@DataJpaTest` оборачивает каждый тест в транзакцию и делает rollback после. Отключить: `@Transactional(propagation = NOT_SUPPORTED)`.

## Q6. Какие ещё test slices есть в Spring Boot?

| Аннотация | Что загружает |
|---|---|
| `@WebMvcTest` | Web MVC слой (контроллеры, MockMvc) |
| `@WebFluxTest` | WebFlux слой (reactive controllers, WebTestClient) |
| `@DataJpaTest` | JPA repositories, TestEntityManager |
| `@DataJdbcTest` | Spring Data JDBC repositories |
| `@DataMongoTest` | MongoDB repositories |
| `@DataRedisTest` | Redis repositories |
| `@DataNeo4jTest` | Neo4j repositories |
| `@JsonTest` | JSON serialization (Jackson ObjectMapper) |
| `@RestClientTest` | HTTP client тесты (RestTemplate, RestClient) |
| `@WebServiceClientTest` | SOAP web service clients |

```java
// @JsonTest — проверить сериализацию/десериализацию
@JsonTest
class OrderDtoJsonTest {

    @Autowired
    private JacksonTester<OrderDto> json;

    @Test
    void serialize_shouldProduceExpectedJson() throws Exception {
        OrderDto dto = new OrderDto(1L, "CUST-1", BigDecimal.TEN);
        assertThat(json.write(dto)).isEqualToJson("expected-order.json");
    }
}
```

## Q7. (!) Что такое `MockMvc` и как им пользоваться?

`MockMvc` — Spring-инструмент для тестирования web-слоя **без запуска реального HTTP-сервера**: симулирует HTTP-запросы через Spring DispatcherServlet.

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService service;

    @Test
    void getOrder_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(new Order(1L, "CUST-1"));

        mockMvc.perform(
                get("/api/orders/1")
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.customerId").value("CUST-1"))
            .andDo(print());  // вывести запрос/ответ в лог
    }

    @Test
    void getOrder_notFound_returns404() throws Exception {
        when(service.findById(99L)).thenThrow(new OrderNotFoundException(99L));

        mockMvc.perform(get("/api/orders/99"))
            .andExpect(status().isNotFound());
    }
}
```

MockMvc fluent API:
- `perform(request)` — выполнить запрос
- `andExpect(matcher)` — проверить
- `andDo(handler)` — побочное действие (print, log)
- `andReturn()` — получить `MvcResult`

## Q8. Как тестировать REST endpoint с JSON через MockMvc?

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // Spring Boot autoconfigures it

    @MockBean
    private OrderService service;

    @Test
    void createOrder_validRequest_returns201() throws Exception {
        OrderRequest request = new OrderRequest("CUST-1", List.of("SKU-001"));
        Order created = new Order(42L, "CUST-1");

        when(service.create(any(OrderRequest.class))).thenReturn(created);

        mockMvc.perform(
                post("/api/orders")
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/api/orders/42")))
            .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void createOrder_invalidRequest_returns400() throws Exception {
        mockMvc.perform(
                post("/api/orders")
                    .contentType(APPLICATION_JSON)
                    .content("{}")  // пустой body — нарушение @NotNull
            )
            .andExpect(status().isBadRequest());
    }
}
```

`jsonPath()` использует JsonPath синтаксис: `$.field`, `$.array[0]`, `$.array.length()`.

## Q9. (!) Чем `@MockBean` отличается от `@Mock` из Mockito?

| Параметр | `@Mock` (Mockito) | `@MockBean` (Spring Boot Test) |
|---|---|---|
| Spring-контекст | Не участвует | Добавляет mock в ApplicationContext |
| DI | Ручной (через `@InjectMocks`) | Автоматический (Spring DI) |
| Сброс стейта | После метода (с `@ExtendWith`) | После каждого теста |
| Кеш контекста | Не влияет | Загрязняет кеш → пересоздание контекста |
| Использование | Unit тесты | Slice тесты / `@SpringBootTest` |

```java
// @Mock — unit test без Spring:
class OrderServiceTest {
    @Mock OrderRepository repository;
    @InjectMocks OrderService service;
}

// @MockBean — slice test со Spring:
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @MockBean OrderService service;  // Spring подставит mock в контроллер
}
```

**Важно:** каждый уникальный набор `@MockBean` приводит к созданию нового ApplicationContext (загрязняет кеш). Минимизируйте `@MockBean` для ускорения тестов.

## Q10. Что такое `@SpyBean` и когда нужен?

`@SpyBean` — частичный mock в Spring-контексте: реальный бин, но с возможностью мокировать отдельные методы.

```java
@SpringBootTest
class OrderEventTest {

    @SpyBean
    private OrderEventPublisher eventPublisher;

    @Autowired
    private OrderService orderService;

    @Test
    void createOrder_shouldPublishEvent() {
        orderService.create(new OrderRequest("CUST-1"));

        // Проверяем вызов реального бина
        verify(eventPublisher, times(1)).publish(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_withEmailFailure_shouldNotPropagateError() {
        // Переопределить только один метод spy
        doThrow(new RuntimeException("Email down"))
            .when(eventPublisher).sendEmail(any());

        // Остальное работает по-настоящему
        assertThatCode(() -> orderService.create(new OrderRequest("CUST-1")))
            .doesNotThrowAnyException();
    }
}
```

`@SpyBean` vs `@MockBean`:
- `@MockBean` — всё мокируется (возвращает null/0 по умолчанию)
- `@SpyBean` — работает реально, можно переопределить конкретные методы

## Q11. Что такое `@TestConfiguration` и зачем нужна?

`@TestConfiguration` — конфигурационный класс **только для тестов**, добавляющий бины в Spring-контекст:

```java
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurity(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                   .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                   .build();
    }
}
```

```java
@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)  // подключить тестовый конфиг
class OrderControllerSecurityTest {
    // Security отключена для тестов
}
```

**Отличие от `@Configuration`:** `@TestConfiguration` не подбирается при component scanning основного приложения — только при явном `@Import` или внутри тест-класса.

**Вложенный класс:**

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @TestConfiguration
    static class AdditionalConfig {
        @Bean
        public ClockService clockService() {
            return () -> Instant.parse("2026-01-01T00:00:00Z");
        }
    }
}
```

## Q12. Что такое `@DirtiesContext` и когда использовать?

`@DirtiesContext` помечает ApplicationContext как "грязный" → Spring пересоздаёт его после теста/класса:

```java
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StatefulIntegrationTest {
    // Контекст пересоздаётся после КАЖДОГО теста
}

@SpringBootTest
@DirtiesContext  // default: AFTER_CLASS — после всех тестов в классе
class DatabaseMigrationTest {
    // Тест изменяет схему БД — нужно пересоздание контекста
}
```

**Уровни `classMode`:**

| Значение | Когда пересоздаётся |
|---|---|
| `AFTER_CLASS` (default) | После всех тестов класса |
| `BEFORE_CLASS` | До начала тестов класса |
| `AFTER_EACH_TEST_METHOD` | После каждого метода |
| `BEFORE_EACH_TEST_METHOD` | До каждого метода |

**Когда НЕ нужен:** в большинстве случаев `@DirtiesContext` избыточен и замедляет тесты. Лучше:
- Изолировать тесты через транзакции с rollback
- Использовать `@BeforeEach` для очистки данных
- Использовать Testcontainers (каждый тест получает чистую БД)

## Q13. (!) Как Spring Boot кеширует ApplicationContext в тестах?

Spring кеширует `ApplicationContext` по **комбинации ключей**: аннотации теста, конфигурация, активные профили, `@MockBean`/`@SpyBean`.

```
@SpringBootTest             → Key A
@SpringBootTest + @MockBean X → Key B  (другой контекст!)
@SpringBootTest + @MockBean Y → Key C  (ещё один контекст!)
```

**Последствие:** множество `@MockBean` в разных тест-классах = множество контекстов = медленные тесты.

**Оптимизация:**

```java
// Общий базовый класс для тестов с одинаковыми мок-бинами
@SpringBootTest
public abstract class BaseIntegrationTest {
    @MockBean EmailService emailService;
    @MockBean PaymentService paymentService;
}

class OrderTest extends BaseIntegrationTest {
    // Использует кешированный контекст из BaseIntegrationTest
}

class UserTest extends BaseIntegrationTest {
    // Тот же кешированный контекст!
}
```

**Мониторинг:** включить `spring.test.context.cache.maxSize` (default: 32 контекста). Логировать с `logging.level.org.springframework.test.context.cache=DEBUG`.

## Q14. Как тестировать Spring Security (аутентификацию/авторизацию)?

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

**`@WithMockUser`** — симулировать аутентифицированного пользователя:

```java
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean AdminService service;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminData_withAdminRole_returns200() throws Exception {
        mockMvc.perform(get("/admin/data"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAdminData_withUserRole_returns403() throws Exception {
        mockMvc.perform(get("/admin/data"))
            .andExpect(status().isForbidden());
    }

    @Test
    void getAdminData_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/admin/data"))
            .andExpect(status().isUnauthorized());
    }
}
```

**JWT/OAuth тест:**

```java
mockMvc.perform(get("/api/orders")
        .with(jwt().jwt(j -> j.subject("user-123")
                              .claim("roles", List.of("USER")))))
    .andExpect(status().isOk());
```

`SecurityMockMvcRequestPostProcessors.jwt()` — для OAuth2 JWT тестирования.

## Q15. Как использовать Testcontainers с Spring Boot?

**Spring Boot 3.1+ нативная интеграция:**

```java
// src/test/java/...
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection  // автоматически настраивает spring.datasource.*
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16");
    }

    @Bean
    @ServiceConnection
    RedisContainer redisContainer() {
        return new RedisContainer("redis:7");
    }
}

// Тест
@SpringBootTest
@ImportTestcontainers(TestcontainersConfig.class)
class UserRepositoryTest {
    @Autowired UserRepository repository;

    @Test
    void findAll_withRealDatabase() {
        // Реальный PostgreSQL через Testcontainers
    }
}
```

**Старый способ (без @ServiceConnection):**

```java
@SpringBootTest
class LegacyIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
```

`@ServiceConnection` (Spring Boot 3.1+) — автоматически настраивает datasource/redis/kafka из контейнера, не нужно `@DynamicPropertySource`.

## See also

- [Spring Boot](spring-boot-interview.md) — автоконфигурация, `@SpringBootApplication`, production features
- [Spring Framework](spring-framework-interview.md) — ApplicationContext, BeanFactory, тестовый контекст
- [Spring MVC](spring-mvc-interview.md) — `@WebMvcTest`, контроллеры, MockMvc handler mapping
- [Spring Data JPA](spring-data-jpa-interview.md) — `@DataJpaTest`, TestEntityManager, repository тесты
- [Spring Security](spring-security-interview.md) — `@WithMockUser`, jwt(), security filter chain тестирование
- [Spring AOP](spring-aop-interview.md) — `@SpyBean` для проверки вызовов через AOP-proxy
- [Spring Events](spring-events-interview.md) — `@RecordApplicationEvents`, `ApplicationEvents` в тестах
- [Mockito](../../testing/mockito-interview.md) — `@Mock`, `@InjectMocks`, verify, stubbing — без Spring
- [Testcontainers](../../testing/testcontainers-interview.md) — интеграция с Spring Boot, @ServiceConnection, @DynamicPropertySource
- [Unit Testing](../../testing/unit-testing-interview.md) — принципы юнит-тестирования, пирамида тестов, AAA
- [Шпаргалка: Spring Testing: Полное руководство по те](../../../frameworks/java-frameworks/spring/spring-testing.md) — теория
