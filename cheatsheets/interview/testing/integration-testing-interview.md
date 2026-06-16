---
title: "Вопросы на собеседовании: Integration Testing"
description: "Интеграционное тестирование в Spring Boot: @SpringBootTest, Testcontainers, MockMvc, WebTestClient, WireMock, contract testing, test slices и best practices."
tags:
  - interview
  - testing
  - integration-testing-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Integration Testing"
  - "Integration Testing interview"
  - "Интеграционное тестирование"
prerequisites: []
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `Integration Testing`

Интеграционное тестирование проверяет взаимодействие компонентов приложения с реальной инфраструктурой (`DB`, очереди, внешние `API`). Этот файл покрывает `Spring Boot Testing`, `Testcontainers`, `MockMvc`, `WebTestClient`, `WireMock`, контрактное тестирование и организацию тестов в `CI/CD`.

**Интеграционное тестирование** занимает среднюю часть тестовой пирамиды: проверяет реальные связки между модулями и инфраструктурой, но при этом не требует полного развёртывания всей системы.

## Полезные ссылки

### Официальная документация

- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing) — официальная документация по тестированию
- [Testcontainers](https://www.testcontainers.org/) — документация Testcontainers
- [WireMock](https://wiremock.org/docs/) — мокирование HTTP-сервисов
- [Testing Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html) — практическая тестовая пирамида
- [Contract Testing — Pact](https://docs.pact.io/) — контрактное тестирование
- [Baeldung: Integration Testing in Spring](https://www.baeldung.com/integration-testing-in-spring) — подробный гайд
- [Baeldung: Spring Boot Testcontainers](https://www.baeldung.com/spring-boot-testcontainers-integration-test) — Testcontainers + Spring Boot
- [Baeldung: Built-in Testcontainers Support](https://www.baeldung.com/spring-boot-built-in-testcontainers) — Spring Boot 3.1+ поддержка

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы интеграционного тестирования**
- [Q1. (!) Что такое интеграционное тестирование и чем оно отличается от `unit`-тестирования?](#q1--что-такое-интеграционное-тестирование-и-чем-оно-отличается-от-unit-тестирования)
- [Q2. Какие типы интеграционного тестирования существуют?](#q2-какие-типы-интеграционного-тестирования-существуют)
- [Q3. (!) Как работает `@SpringBootTest` и когда его использовать?](#q3--как-работает-springboottest-и-когда-его-использовать)
- [Q4. (!) Что такое `Test Slices` и какие бывают?](#q4--что-такое-test-slices-и-какие-бывают)

**`MockMvc` и `WebTestClient`**
- [Q5. (!) Как тестировать REST-контроллеры через `MockMvc`?](#q5--как-тестировать-rest-контроллеры-через-mockmvc)
- [Q6. В чём разница между `MockMvc` с `@WebMvcTest` и `@SpringBootTest`?](#q6-в-чём-разница-между-mockmvc-с-webmvctest-и-springboottest)
- [Q7. Как использовать `WebTestClient` для реактивных и блокирующих приложений?](#q7-как-использовать-webtestclient-для-реактивных-и-блокирующих-приложений)
- [Q8. Как тестировать REST API через `TestRestTemplate`?](#q8-как-тестировать-rest-api-через-testresttemplate)

**`Testcontainers`**
- [Q9. (!) Что такое `Testcontainers` и зачем он нужен?](#q9--что-такое-testcontainers-и-зачем-он-нужен)
- [Q10. Как подключить `Testcontainers` через `@DynamicPropertySource`?](#q10-как-подключить-testcontainers-через-dynamicpropertysource)
- [Q11. (!) Что такое `@ServiceConnection` в Spring Boot 3.1+?](#q11--что-такое-serviceconnection-в-spring-boot-31)
- [Q12. Как переиспользовать контейнеры между тестовыми классами?](#q12-как-переиспользовать-контейнеры-между-тестовыми-классами)
- [Q13. Как тестировать с несколькими контейнерами одновременно?](#q13-как-тестировать-с-несколькими-контейнерами-одновременно)

**Тестирование с базами данных**
- [Q14. (!) Как тестировать с реальной базой данных?](#q14--как-тестировать-с-реальной-базой-данных)
- [Q15. Как тестировать транзакции в интеграционных тестах?](#q15-как-тестировать-транзакции-в-интеграционных-тестах)
- [Q16. Как управлять тестовыми данными через `@Sql` и `Flyway`?](#q16-как-управлять-тестовыми-данными-через-sql-и-flyway)
- [Q17. В чём опасность `@Transactional` на интеграционных тестах?](#q17-в-чём-опасность-transactional-на-интеграционных-тестах)

**Мокирование внешних сервисов**
- [Q18. (!) Как использовать `WireMock` для мокирования HTTP-сервисов?](#q18--как-использовать-wiremock-для-мокирования-http-сервисов)
- [Q19. В чём разница между `@MockBean` и `@SpyBean`?](#q19-в-чём-разница-между-mockbean-и-spybean)
- [Q20. Как тестировать с `WireMock`: сценарии ошибок и задержки?](#q20-как-тестировать-с-wiremock-сценарии-ошибок-и-задержки)

**Contract Testing и микросервисы**
- [Q21. (!) Что такое контрактное тестирование?](#q21--что-такое-контрактное-тестирование)
- [Q22. Как работает Consumer-Driven Contract Testing с `Pact`?](#q22-как-работает-consumer-driven-contract-testing-с-pact)
- [Q23. Как использовать `Spring Cloud Contract`?](#q23-как-использовать-spring-cloud-contract)
- [Q24. Как тестировать микросервисы в изоляции?](#q24-как-тестировать-микросервисы-в-изоляции)

**Kafka, асинхронность и messaging**
- [Q25. (!) Как тестировать `Kafka` с `Testcontainers`?](#q25--как-тестировать-kafka-с-testcontainers)
- [Q26. Как тестировать асинхронные операции?](#q26-как-тестировать-асинхронные-операции)
- [Q27. Как тестировать `message-driven` архитектуру?](#q27-как-тестировать-message-driven-архитектуру)

**Организация и CI/CD**
- [Q28. Как организовать интеграционные тесты в `CI/CD`?](#q28-как-организовать-интеграционные-тесты-в-cicd)
- [Q29. Как ускорить интеграционные тесты?](#q29-как-ускорить-интеграционные-тесты)
- [Q30. Как организовать параллельное выполнение тестов?](#q30-как-организовать-параллельное-выполнение-тестов)

**Стабильность и Best Practices**
- [Q31. (!) Как бороться с flaky-тестами?](#q31--как-бороться-с-flaky-тестами)
- [Q32. Что такое Test Fixtures и как их организовать?](#q32-что-такое-test-fixtures-и-как-их-организовать)
- [Q33. Как тестировать кеширование?](#q33-как-тестировать-кеширование)
- [Q34. Что такое `Smoke Testing` после деплоя?](#q34-что-такое-smoke-testing-после-деплоя)
- [Q35. (!) Какие best practices для интеграционного тестирования?](#q35--какие-best-practices-для-интеграционного-тестирования)

**Продвинутые техники**
- [Q36. (!) Как использовать `@DataJpaTest` для тестирования репозиториев?](#q36--как-использовать-datajpatest-для-тестирования-репозиториев)
- [Q37. Как тестировать JSON-сериализацию с `@JsonTest`?](#q37-как-тестировать-json-сериализацию-с-jsontest)
- [Q38. (!) Как использовать `RestAssured` для интеграционных API-тестов?](#q38--как-использовать-restassured-для-интеграционных-api-тестов)
- [Q39. Как тестировать `Spring Security` в интеграционных тестах?](#q39-как-тестировать-spring-security-в-интеграционных-тестах)
- [Q40. Как использовать `@RestClientTest` для тестирования HTTP-клиентов?](#q40-как-использовать-restclienttest-для-тестирования-http-клиентов)

---

## Q1. (!) Что такое интеграционное тестирование и чем оно отличается от `unit`-тестирования?

**Интеграционное тестирование** проверяет, что несколько компонентов работают вместе с реальными зависимостями (БД, очереди, HTTP-сервисы), а не только в изоляции. Unit-тест отвечает на вопрос «правильна ли логика одного класса?», интеграционный — «правильно ли этот класс общается с базой, очередью и соседними сервисами?».

Главное отличие: unit-тест заменяет все зависимости моками, интеграционный — поднимает их по-настоящему. Поэтому он ловит баги, которые моки скрывают: неверный SQL, рассинхрон схемы и сущности, ошибку в конфигурации Spring, неправильную сериализацию JSON.

### Отличия от `unit testing`

| Аспект | `Unit Testing` | `Integration Testing` |
|--------|---------------|----------------------|
| Scope | Один класс/метод | Группа компонентов + инфраструктура |
| Зависимости | `Mock`/`stub` | Реальные (`DB`, `Kafka`, `Redis`) |
| Скорость | Миллисекунды | Секунды-минуты |
| Изоляция | Полная | Частичная |
| Настройка | Минимальная | `Docker`, конфигурация, данные |
| Стабильность | Высокая | Может быть flaky |
| Цель | Логика, алгоритмы | Интеграция, контракты, SQL |

**Тестовая пирамида** (сверху вниз — от редких медленных к многочисленным быстрым):

- `E2E Tests` — мало, медленные;
- `Integration Tests` — средне, секунды;
- `Unit Tests` — много, быстрые.

Уровни связаны последовательно: `E2E` опирается на `Integration`, тот — на `Unit`.

**Что покрывает Integration Testing** — взаимодействие с реальной инфраструктурой:

- База данных;
- Очереди сообщений;
- HTTP-сервисы;
- Кеш.

### Пример: `unit` vs `integration`

```java
// Unit-тест — тестируем только бизнес-логику
@Test
void shouldCalculateDiscount() {
    DiscountService service = new DiscountService();

    BigDecimal discount = service.calculateDiscount(
        BigDecimal.valueOf(100), UserType.PREMIUM);

    assertEquals(BigDecimal.valueOf(15.00), discount);
}

// Integration-тест — тестируем взаимодействие с БД
@SpringBootTest
@Testcontainers
class OrderIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
    }

    @Autowired
    private OrderService orderService;

    @Test
    void shouldSaveOrderWithDiscount() {
        Order order = orderService.createOrder(userId, productId, 1);

        assertNotNull(order.getId());
        assertEquals(BigDecimal.valueOf(850), order.getTotalPrice());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }
}
```

**Когда нужен именно интеграционный тест.** Если проверяемое поведение зависит от чего-то внешнего по отношению к классу, мок этого «внешнего» сделает тест бессмысленным:

- взаимодействие с БД — SQL-запросы, маппинг сущностей, ограничения схемы;
- HTTP-клиент к внешнему сервису — корректность запроса и разбора ответа;
- конфигурация `Spring` — собирается ли контекст, верно ли связаны бины;
- транзакционная логика — коммит при успехе, rollback при ошибке.

Бизнес-логику без внешних зависимостей (расчёт скидки, валидация) тестировать интеграционно не нужно — это работа unit-тестов, они в разы быстрее.

## Q2. Какие типы интеграционного тестирования существуют?

Классические подходы отличаются **порядком**, в котором компоненты собираются вместе. Выбор порядка влияет на то, насколько легко локализовать ошибку и сколько заглушек (`stub`) придётся написать.

### 1. Big Bang

Все компоненты интегрируются разом. Реализовать просто (ничего не надо мокировать), но при падении непонятно, какой из узлов сломался — ошибку трудно локализовать.

### 2. Top-Down

Идём сверху вниз: от верхних слоёв (`API`/`UI`) к нижним, заменяя ещё не готовые нижние уровни на `stub`. Плюс — рано проверяем пользовательские сценарии; минус — нужно писать заглушки.

### 3. Bottom-Up

Идём снизу вверх: от `Repository` и `Service` к контроллерам. Нижние слои проверены первыми и реальны, поэтому заглушки не нужны; зато сквозные сценарии видны позже всех.

### 4. Sandwich / Hybrid

Комбинация top-down и bottom-up: верх и низ тестируют параллельно, встречаясь в середине. Быстрее по времени, но дороже в организации.

### 5. Component Integration (рекомендуемый для Spring)

Группу связанных компонентов тестируют как единый блок. Именно так строят тесты в `Spring Boot`: `@SpringBootTest` поднимает весь контекст, а test slices (`@WebMvcTest`, `@DataJpaTest`) — только нужный срез.

Как это выглядит на практике. Реальная цепочка `Component Integration Test`:

- `Controller` → `Service` → `Repository` → `PostgreSQL` (через `Testcontainers`).

При этом `Service` обращается к замоканному внешнему API:

- `Service` → внешний API (`WireMock`, блок `Mocked`).

То есть собственная инфраструктура (БД) поднимается по-настоящему, а соседний внешний сервис заменяется заглушкой.

На собеседовании обычно спрашивают не о классификации, а о практическом подходе: какие слои тестируете вместе, какие мокируете, и почему.

## Q3. (!) Как работает `@SpringBootTest` и когда его использовать?

`@SpringBootTest` поднимает **полный** `ApplicationContext` — все бины приложения, как при реальном запуске. Это самый тяжёлый, но и самый достоверный вид теста: проверяется не отдельный слой, а вся связка целиком, включая auto-configuration.

Цена за достоверность — скорость: контекст со всеми бинами стартует секунды, поэтому `@SpringBootTest` берут только когда реально нужна полная сборка. Для проверки одного слоя есть более лёгкие test slices (см. ниже).

### Режимы `webEnvironment`

Параметр `webEnvironment` управляет тем, поднимать ли HTTP-сервер и какой клиент использовать для запросов.

| Режим | Что делает | Когда использовать |
|-------|-----------|-------------------|
| `MOCK` (default) | `MockServletContext`, нет HTTP-сервера | Тесты через `MockMvc` |
| `RANDOM_PORT` | Запуск реального HTTP-сервера | Тесты через `TestRestTemplate`/`WebTestClient` |
| `DEFINED_PORT` | HTTP на порту из `application.yml` | Редко, конфликт портов |
| `NONE` | Без web-окружения | Тесты сервисного слоя |

```java
// Полный контекст с реальным сервером
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class FullIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateUser() {
        var request = new UserRequest("john@example.com", "secret");

        ResponseEntity<UserResponse> response = restTemplate
            .postForEntity("/api/users", request, UserResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }
}
```

### Когда НЕ использовать `@SpringBootTest`

Правило простое: не поднимайте весь контекст ради одного слоя.

- тестируете **только** контроллер — `@WebMvcTest`;
- тестируете **только** репозиторий — `@DataJpaTest`;
- тестируете бизнес-логику без Spring — обычный `unit`-тест без аннотаций.

Каждый лишний `@SpringBootTest` замедляет сборку, поэтому держите его для сквозных сценариев. Подробнее о test slices — в [вопросах по unit-тестированию](unit-testing-interview.md).

## Q4. (!) Что такое `Test Slices` и какие бывают?

**Test Slices** — аннотации Spring Boot, которые поднимают **только нужный срез** контекста, а не всё приложение. Например, `@WebMvcTest` загружает контроллеры и web-инфраструктуру, но не трогает сервисы и БД. За счёт того, что бинов на порядок меньше, такой тест стартует доли секунды против секунд у `@SpringBootTest` — и при этом остаётся честным интеграционным тестом своего слоя.

### Основные test slices

| Аннотация | Что поднимает | Для чего |
|-----------|--------------|---------|
| `@WebMvcTest` | Контроллеры, `MockMvc`, фильтры | REST-контроллеры |
| `@DataJpaTest` | `JPA`, `EntityManager`, `Repositories` | Репозитории, SQL |
| `@DataJdbcTest` | `JDBC`, `JdbcTemplate` | JDBC-репозитории |
| `@JdbcTest` | `DataSource`, `JdbcTemplate` | Чистый JDBC |
| `@DataMongoTest` | MongoDB repositories | MongoDB |
| `@DataRedisTest` | Redis repositories | Redis |
| `@JsonTest` | `ObjectMapper`, `JacksonTester` | JSON сериализация |
| `@RestClientTest` | `RestTemplateBuilder`, `MockRestServiceServer` | REST-клиенты |
| `@WebFluxTest` | `WebFlux` контроллеры, `WebTestClient` | Reactive endpoints |

```java
// @DataJpaTest — только JPA-слой, embedded H2 по умолчанию
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindByEmail() {
        entityManager.persistAndFlush(
            new User("john@example.com", "password"));

        Optional<User> found = userRepository.findByEmail("john@example.com");

        assertTrue(found.isPresent());
        assertEquals("john@example.com", found.get().getEmail());
    }
}
```

**Подводный камень.** `@DataJpaTest` по умолчанию подменяет `DataSource` на встроенную `H2` — даже если в проекте PostgreSQL. Поэтому диалект-специфичный SQL может «работать» в тесте и падать в production. Чтобы тест шёл против реальной БД через `Testcontainers`, отключите подмену: `@AutoConfigureTestDatabase(replace = NONE)`.

## Q5. (!) Как тестировать REST-контроллеры через `MockMvc`?

`MockMvc` прогоняет HTTP-запросы через `DispatcherServlet` прямо в памяти, **без реального сервера и сетевого сокета**. Запрос проходит весь web-слой — маршрутизацию, фильтры, конвертеры, валидацию, обработку исключений, — но без накладных расходов на TCP. Поэтому такие тесты на порядок быстрее, чем с поднятым Tomcat.

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnUser() throws Exception {
        when(userService.findById(1L))
            .thenReturn(new UserDto(1L, "John", "john@example.com"));

        mockMvc.perform(get("/api/users/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"))
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(userService.findById(99L))
            .thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("User not found: 99"));
    }

    @Test
    void shouldValidateInput() throws Exception {
        String invalidJson = """
            {"name": "", "email": "not-an-email"}
            """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray());
    }
}
```

`@WebMvcTest` поднимает только web-слой: контроллеры, `@ControllerAdvice`, фильтры, конвертеры. Сервисы и репозитории в контекст НЕ попадают, поэтому каждую зависимость контроллера приходится подменять `@MockBean` — иначе контекст не соберётся. Это и есть смысл слайса: проверить контроллер изолированно (маршруты, статусы, валидация, формат JSON), не затягивая в тест реальную бизнес-логику и БД.

## Q6. В чём разница между `MockMvc` с `@WebMvcTest` и `@SpringBootTest`?

| Аспект | `@WebMvcTest` + `MockMvc` | `@SpringBootTest` + `MockMvc` |
|--------|--------------------------|-------------------------------|
| Контекст | Только web-слой | Полный `ApplicationContext` |
| Сервисы | `@MockBean` (моки) | Реальные бины |
| БД | Нет | Реальная (Testcontainers) |
| Скорость | Быстро (< 1 сек) | Медленно (5-15 сек) |
| Цель | Тест контроллера в изоляции | Полный сквозной тест через HTTP |

```java
// @SpringBootTest + MockMvc — полный сквозной тест
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAndRetrieveUser() throws Exception {
        // Create
        String json = """
            {"name": "John", "email": "john@example.com"}
            """;

        String location = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getHeader("Location");

        // Retrieve — реальный сервис + реальная БД
        mockMvc.perform(get(location))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("John"));
    }
}
```

**Как ответить на собеседовании.** Оба варианта используют один и тот же `MockMvc`, разница — в том, что стоит за контроллером. `@WebMvcTest` даёт быстрый изолированный тест контроллера с моками сервисов. `@SpringBootTest` + `@AutoConfigureMockMvc` прогоняет запрос через реальные сервисы и реальную БД — это уже сквозной тест ценой более медленного старта.

## Q7. Как использовать `WebTestClient` для реактивных и блокирующих приложений?

`WebTestClient` — неблокирующий клиент из `Spring WebFlux` для тестирования HTTP-эндпоинтов с fluent-API. Хотя родом он из реактивного стека, начиная с Spring Boot 2.4+ его можно навешивать и на обычные `Spring MVC`-приложения — в том числе на `MockMvc` без реального сервера. Это позволяет писать тесты в едином стиле независимо от того, реактивное приложение или блокирующее.

```java
// С реальным сервером (RANDOM_PORT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserApiWebTestClientTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldCreateUser() {
        webTestClient.post().uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(new UserRequest("John", "john@example.com"))
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.name").isEqualTo("John");
    }

    @Test
    void shouldReturnUserList() {
        webTestClient.get().uri("/api/users")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(UserResponse.class)
            .hasSize(3)
            .value(users -> {
                assertTrue(users.stream()
                    .anyMatch(u -> u.getEmail().equals("john@example.com")));
            });
    }
}
```

**Чем лучше `TestRestTemplate`.** `WebTestClient` — современная замена: цепочка проверок читается как описание запроса (`post().uri().bodyValue().exchange().expectStatus()`), есть нативная поддержка реактивных типов `Mono`/`Flux` и streaming-ответов. `TestRestTemplate` старше и удобен в основном для блокирующих REST-вызовов. Подробнее о реактивном тестировании — в [вопросах по Spring WebFlux](../frameworks/spring/spring-webflux-interview.md).

## Q8. Как тестировать REST API через `TestRestTemplate`?

`TestRestTemplate` — приспособленная для тестов обёртка над `RestTemplate`. Она бьёт по **реальному** HTTP-серверу, поэтому требует поднятого сервера: `webEnvironment = RANDOM_PORT` (или `DEFINED_PORT`). Базовый URL подставляется автоматически, поэтому в запросах указывают только путь (`/api/users`).

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateUser() {
        UserRequest request = new UserRequest("john@example.com", "secret");

        ResponseEntity<UserResponse> response = restTemplate
            .postForEntity("/api/users", request, UserResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
    }

    @Test
    void shouldHandleAuthentication() {
        // TestRestTemplate с Basic Auth
        ResponseEntity<UserResponse> response = restTemplate
            .withBasicAuth("admin", "password")
            .getForEntity("/api/admin/users/1", UserResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldHandleErrors() {
        ResponseEntity<ErrorResponse> response = restTemplate
            .getForEntity("/api/users/999", ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
```

**Ключевое отличие от `RestTemplate`.** Обычный `RestTemplate` на ответ 4xx/5xx бросает исключение, и проверять ошибочный сценарий пришлось бы через try/catch. `TestRestTemplate` исключение НЕ бросает — он просто возвращает `ResponseEntity` с нужным статусом, и код ошибки проверяется обычным `assertEquals`. Именно поэтому он удобен для негативных тестов.

## Q9. (!) Что такое `Testcontainers` и зачем он нужен?

`Testcontainers` — Java-библиотека, которая поднимает `Docker`-контейнеры прямо из кода теста и сама гасит их после прогона. Идея простая: вместо in-memory заменителей (`H2`, embedded-брокеров) тест работает с **той же** базой, очередью или кешем, что и production — настоящим PostgreSQL, Kafka, Redis. Жизненным циклом контейнера управляет библиотека, разработчику не нужно вручную поднимать `docker compose`.

Как устроено по слоям:

- **JUnit Test** — тестовый класс. Он обращается к `Testcontainers Library`, а нужные параметры подключения получает через `@DynamicPropertySource` (к `PostgreSQL` и `Redis` в примере).
- **Testcontainers управляет** — `Testcontainers Library` поднимает и контролирует контейнеры: `PostgreSQL`, `Redis`, `Kafka`.
- **Docker** — сами контейнеры (`PostgreSQL`, `Redis`, `Kafka`) исполняются как Docker-контейнеры.

### Преимущества перед `H2` / embedded

| Аспект | `H2` (embedded) | `Testcontainers` |
|--------|-----------------|-------------------|
| SQL-совместимость | Отличается от production | Идентична production |
| Специфичные фичи | Не поддерживает (`JSONB`, `ARRAY`) | Полная поддержка |
| Настройка | Зависимость в `pom.xml` | Нужен `Docker` |
| Скорость | Быстро | Старт контейнера 2-5 сек |
| Надёжность результата | Может пропустить баг | Ловит реальные проблемы |

```java
@SpringBootTest
@Testcontainers
class ProductRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16-alpine")
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
    private ProductRepository productRepository;

    @Test
    void shouldUsePostgresJsonb() {
        // Тестируем PostgreSQL-специфичный JSONB
        Product product = new Product("Laptop", Map.of(
            "cpu", "Intel i7",
            "ram", "16GB"
        ));
        productRepository.save(product);

        List<Product> found = productRepository
            .findByAttributesContaining("cpu", "Intel");
        assertEquals(1, found.size());
    }
}
```

## Q10. Как подключить `Testcontainers` через `@DynamicPropertySource`?

Проблема в том, что хост и порт контейнера известны только **после** его старта: Docker маппит внутренний порт на случайный свободный на хосте. Прописать их в `application.yml` заранее нельзя. `@DynamicPropertySource` решает это — он вычисляет `Spring`-свойства лениво, уже после запуска контейнера, через ссылки на его методы (`postgres::getJdbcUrl`).

```java
@SpringBootTest
@Testcontainers
class MultiContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Container
    static GenericContainer<?> redis =
        new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void shouldWorkWithBothServices() {
        // PostgreSQL
        User user = userRepository.save(new User("john@example.com"));
        assertNotNull(user.getId());

        // Redis
        redisTemplate.opsForValue().set("user:" + user.getId(), "cached");
        assertEquals("cached",
            redisTemplate.opsForValue().get("user:" + user.getId()));
    }
}
```

**Правила, без которых не заработает:**

- метод с `@DynamicPropertySource` обязан быть `static` — Spring зовёт его при подготовке контекста, до создания экземпляра теста;
- контейнер должен быть уже запущен к этому моменту. Это гарантирует связка `static`-поле + `@Container`: JUnit стартует такой контейнер один раз на класс ещё до того, как собирается контекст.

## Q11. (!) Что такое `@ServiceConnection` в Spring Boot 3.1+?

`@ServiceConnection` (Spring Boot 3.1+) убирает ручную возню с `@DynamicPropertySource`: достаточно навесить её на контейнер, и Spring Boot **сам** свяжет его с нужным бином (`DataSource`, `RedisConnectionFactory` и т. д.). Spring распознаёт тип контейнера и подставляет URL, логин и пароль автоматически — шаблонный код из Q10 просто исчезает.

```java
// До Spring Boot 3.1 — ручная конфигурация
@Container
static PostgreSQLContainer<?> postgres =
    new PostgreSQLContainer<>("postgres:16");

@DynamicPropertySource
static void props(DynamicPropertyRegistry r) {
    r.add("spring.datasource.url", postgres::getJdbcUrl);
    r.add("spring.datasource.username", postgres::getUsername);
    r.add("spring.datasource.password", postgres::getPassword);
}

// Spring Boot 3.1+ — автоматическая конфигурация
@Container
@ServiceConnection
static PostgreSQLContainer<?> postgres =
    new PostgreSQLContainer<>("postgres:16");
// Всё! Spring Boot сам настроит DataSource
```

### Поддерживаемые контейнеры

Магия работает через SPI `ConnectionDetails`: для каждого известного образа есть свой провайдер, который и достаёт параметры подключения. Из коробки поддержаны `PostgreSQL`, `MySQL`, `MariaDB`, `MongoDB`, `Redis`, `Kafka`, `RabbitMQ`, `Elasticsearch`, `Cassandra` и ещё ряд сервисов. Для контейнера, которого в списке нет, всё так же остаётся `@DynamicPropertySource`.

```java
@SpringBootTest
@Testcontainers
class ModernTestcontainersTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Container
    @ServiceConnection
    static GenericContainer<?> redis =
        new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldAutoConfigureConnections() {
        // Никакого @DynamicPropertySource!
        User user = userRepository.save(new User("john@example.com"));
        assertNotNull(user.getId());
    }
}
```

### Testcontainers для DevServices (Spring Boot 3.1+)

Ту же `@ServiceConnection` можно вынести в отдельный `@TestConfiguration` и переиспользовать двумя способами: подключать к тестам через `@Import`, а ещё — поднимать инфраструктуру **при локальном запуске** приложения через `./gradlew bootTestRun`. Тогда не нужно держать запущенный `docker compose` для локальной разработки — контейнеры стартуют вместе с приложением:

```java
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16");
    }

    @Bean
    @ServiceConnection
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
    }
}

// Использование: ./gradlew bootTestRun
// или в тестах:
@SpringBootTest
@Import(TestcontainersConfig.class)
class MyTest { ... }
```

## Q12. Как переиспользовать контейнеры между тестовыми классами?

По умолчанию JUnit поднимает контейнер для каждого класса с `@Container` заново, а после класса гасит его. На большом наборе тестов старт контейнера повторяется десятки раз и становится главным тормозом. Два способа поднять контейнер один раз:

### 1. Singleton-паттерн (абстрактный базовый класс)

Контейнер объявляют в `static`-инициализаторе базового класса и стартуют вручную (`POSTGRES.start()`). Статический блок выполняется один раз на всю JVM, а JUnit его не глушит (нет `@Container`), поэтому все наследники делят один контейнер. Гасит его уже сам Docker по завершении процесса (Ryuk).

```java
// Базовый класс — контейнер запускается один раз на все тесты
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb");
        POSTGRES.start(); // Запуск один раз
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        r.add("spring.datasource.username", POSTGRES::getUsername);
        r.add("spring.datasource.password", POSTGRES::getPassword);
    }
}

// Тесты наследуют базовый класс
@SpringBootTest
class UserServiceTest extends AbstractIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateUser() {
        User user = userService.create("john@example.com");
        assertNotNull(user.getId());
    }
}
```

### 2. Reusable Containers (Testcontainers 1.19+)

```java
// В ~/.testcontainers.properties:
// testcontainers.reuse.enable=true

@Container
static PostgreSQLContainer<?> postgres =
    new PostgreSQLContainer<>("postgres:16")
        .withReuse(true); // Контейнер НЕ удаляется после тестов
```

`withReuse(true)` идёт дальше singleton: контейнер не удаляется даже после завершения JVM и подхватывается при следующем запуске тестов. На локальной машине это экономит секунды старта при каждом прогоне.

**Когда НЕ применять.** В CI reuse вреден: контейнер живёт между прогонами, в нём остаются данные прошлого запуска, и тесты начинают зависеть от грязного состояния. В CI всегда нужен чистый контейнер, поэтому reuse оставляют только для локальной разработки.

## Q13. Как тестировать с несколькими контейнерами одновременно?

Реальный сценарий часто задействует сразу несколько внешних систем — БД для хранения, Kafka для событий, Redis для кеша. Все контейнеры объявляются в одном тестовом классе; `Testcontainers` стартует их параллельно. Большинство образов (включая Redis — см. Q11) подключается через `@ServiceConnection`; `@DynamicPropertySource` остаётся ручной альтернативой, когда нужен полный контроль над свойствами. Ниже Redis для разнообразия показан именно через `@DynamicPropertySource` — это валидный способ, хотя его `@ServiceConnection` тоже поддерживает.

```java
@SpringBootTest
@Testcontainers
class MultiServiceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Container
    @ServiceConnection
    static KafkaContainer kafka =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Container
    static GenericContainer<?> redis =
        new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry r) {
        r.add("spring.data.redis.host", redis::getHost);
        r.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private OrderService orderService;

    @Test
    void shouldProcessOrderWithAllServices() {
        // Использует PostgreSQL для хранения,
        // Kafka для событий, Redis для кеша
        Order order = orderService.createOrder(userId, productId, 1);

        assertNotNull(order.getId());
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
    }
}
```

**Эмпирическое правило.** `@ServiceConnection` — для всего, что библиотека умеет настраивать сама; `@DynamicPropertySource` — только для контейнеров без автоматической поддержки. Так в тесте остаётся минимум шаблонного кода.

## Q14. (!) Как тестировать с реальной базой данных?

Выбор сводится к одному вопросу: насколько важна совместимость с production-БД. Если запрос простой и переносимый — хватит быстрой `H2`. Если используются диалект-специфичные вещи (`JSONB`, оконные функции, нативные запросы) — нужен `Testcontainers` с настоящей БД, иначе тест либо пропустит баг, либо вовсе не запустит такой SQL.

### Стратегия выбора

| Подход | Когда использовать |
|--------|-------------------|
| `H2` in-memory | Быстрые тесты, простой SQL без DB-специфики |
| `Testcontainers` | Интеграционные тесты, DB-специфичные фичи |
| `@DataJpaTest` + `H2` | Unit-тесты репозитория |
| `@DataJpaTest` + `Testcontainers` | Интеграционные тесты репозитория |
| `@SpringBootTest` + `Testcontainers` | Полный сквозной тест |

### Пример с `@DataJpaTest` и `Testcontainers`

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OrderRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldFindOrdersByStatus() {
        // Arrange
        Order order1 = new Order(userId, BigDecimal.valueOf(100), OrderStatus.PAID);
        Order order2 = new Order(userId, BigDecimal.valueOf(200), OrderStatus.PENDING);
        entityManager.persistAndFlush(order1);
        entityManager.persistAndFlush(order2);

        // Act
        List<Order> paidOrders = orderRepository
            .findByStatus(OrderStatus.PAID);

        // Assert
        assertEquals(1, paidOrders.size());
        assertEquals(BigDecimal.valueOf(100), paidOrders.get(0).getTotal());
    }

    @Test
    void shouldUseNativeQuery() {
        // PostgreSQL-специфичный запрос
        entityManager.persistAndFlush(new Order(userId,
            BigDecimal.valueOf(100), OrderStatus.PAID));

        BigDecimal total = orderRepository.calculateTotalRevenue();

        assertEquals(BigDecimal.valueOf(100), total);
    }
}
```

Подробнее о работе с `JPA` и `Hibernate` -- в [вопросах по Hibernate](../databases/hibernate-interview.md) и [Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md).

## Q15. Как тестировать транзакции в интеграционных тестах?

```java
@SpringBootTest
@Testcontainers
class TransactionIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRollbackOnFailure() {
        // Arrange
        User user = userRepository.save(new User("john@example.com"));

        // Act — orderService.createWithPayment() помечен @Transactional
        assertThrows(PaymentException.class, () -> {
            orderService.createWithPayment(user.getId(),
                productId, invalidCard);
        });

        // Assert — заказ НЕ должен быть сохранён (rollback)
        assertEquals(0, orderRepository.countByUserId(user.getId()));
    }

    @Test
    void shouldCommitOnSuccess() {
        User user = userRepository.save(new User("jane@example.com"));

        Order order = orderService.createWithPayment(
            user.getId(), productId, validCard);

        // Данные сохранены (commit)
        assertTrue(orderRepository.findById(order.getId()).isPresent());
    }
}
```

**Что именно проверяем.** Транзакционный тест должен убедиться в двух исходах: при ошибке внутри `@Transactional`-метода данные откатываются целиком (`countByUserId == 0`), при успехе — фиксируются и видны новому запросу. Здесь важна тонкость: если `@Transactional` повесить на **сам тестовый метод**, Spring откатит его транзакцию после теста. Для изоляции удобно, но такой откат маскирует реальные баги — почему именно, разбирается в Q17.

## Q16. Как управлять тестовыми данными через `@Sql` и `Flyway`?

### `@Sql` -- декларативная загрузка данных

```java
@SpringBootTest
@Testcontainers
class DataDrivenTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Test
    @Sql(scripts = "/test-data/users.sql",
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/test-data/cleanup.sql",
         executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void shouldFindPremiumUsers() {
        List<User> premium = userRepository
            .findByType(UserType.PREMIUM);
        assertEquals(3, premium.size());
    }
}
```

### `Flyway` в тестах

```java
@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
    "spring.flyway.locations=classpath:db/migration,classpath:db/testdata"
})
class FlywayIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Test
    void shouldApplyMigrations() {
        // Flyway применяет миграции автоматически при старте
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users", Integer.class);
        assertNotNull(count);
    }
}
```

**Как разделить ответственность.** У инструментов разные роли: `Flyway` отвечает за **схему** (та же, что в production — миграции прогоняются при старте контекста), а `@Sql` — за **данные конкретного теста** (декларативно, до и после метода). Не кладите тестовые INSERT-ы в production-миграции: они уедут на прод вместе со схемой. Тестовые данные держите отдельно — в `@Sql`-скриптах или отдельной Flyway-локации, как `classpath:db/testdata` выше.

## Q17. В чём опасность `@Transactional` на интеграционных тестах?

`@Transactional` на тестовом методе откатывает изменения после каждого теста — данные чистятся сами, изоляция бесплатна. Соблазн большой, но плата в том, что тест выполняется в условиях, **отличных от production**: весь тест идёт в одной транзакции, которой в реальном коде нет. Из-за этого тест зеленеет там, где прод упадёт:

1. **Lazy loading.** Внутри теста ленивые коллекции догружаются — сессия ещё открыта. В production к моменту обращения транзакция уже закрыта, и тот же код даёт `LazyInitializationException`.
2. **Отложенный flush.** Hibernate сбрасывает изменения в БД лениво. Если транзакция откатывается без `flush`, SQL до базы не доходит — нарушения ограничений и ошибки запросов просто не проявятся.
3. **Транзакционные границы.** Метод с `@Transactional(propagation = REQUIRES_NEW)` внутри сервиса в обёртке из теста-транзакции ведёт себя не так, как в бою, — поведение вложенных транзакций не проверяется честно.

```java
// ПЛОХО — тест скрывает LazyInitializationException
@Test
@Transactional // rollback после теста
void shouldGetUserOrders() {
    User user = userRepository.findById(1L).get();
    // Работает в тесте (одна транзакция), падает в production
    List<Order> orders = user.getOrders();
    assertFalse(orders.isEmpty());
}

// ХОРОШО — тест без @Transactional, ручная очистка
@Test
void shouldGetUserOrders() {
    User user = userService.getUserWithOrders(1L);
    assertFalse(user.getOrders().isEmpty());
}

@AfterEach
void cleanup() {
    orderRepository.deleteAll();
    userRepository.deleteAll();
}
```

Этот вопрос на собеседовании отделяет тех, кто понимает транзакции, от тех, кто просто «слышал про `@Transactional`».

**Рекомендация.** Для интеграционных тестов не вешайте `@Transactional` на тестовый метод — пусть код работает в тех же транзакционных границах, что и в production. Чистоту данных обеспечивайте иначе: очисткой в `@AfterEach`, уникальными данными на каждый тест или чистым контейнером `Testcontainers`.

## Q18. (!) Как использовать `WireMock` для мокирования HTTP-сервисов?

`WireMock` поднимает настоящий HTTP-сервер, который отвечает заранее заданными заглушками (`stub`). Это нужно, чтобы протестировать **свой** HTTP-клиент, не дёргая реальный внешний сервис: вы описываете, на какой запрос какой ответ вернуть, и проверяете, что клиент правильно его формирует и разбирает. В отличие от `@MockBean` на клиенте, здесь тестируется и сам сетевой слой — сериализация, заголовки, обработка статусов. Модуль Spring Cloud Contract даёт аннотацию `@AutoConfigureWireMock`, которая поднимает сервер автоматически.

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0) // Рандомный порт
@TestPropertySource(properties = {
    "payment.service.url=http://localhost:${wiremock.server.port}"
})
class PaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    @Test
    void shouldProcessPayment() {
        stubFor(post(urlEqualTo("/api/charge"))
            .withRequestBody(matchingJsonPath("$.amount",
                equalTo("100.00")))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                        "transactionId": "txn_123",
                        "status": "SUCCESS"
                    }
                    """)));

        PaymentResult result = paymentClient.charge(
            BigDecimal.valueOf(100), "4111111111111111");

        assertEquals("txn_123", result.getTransactionId());
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());

        // Проверяем, что запрос был отправлен
        verify(postRequestedFor(urlEqualTo("/api/charge"))
            .withHeader("Content-Type",
                equalTo("application/json")));
    }
}
```

Поток вызовов по порядку: тест дёргает `PaymentClient`, тот шлёт HTTP-запрос на `WireMock` (`localhost:random_port`), а `WireMock` возвращает клиенту заранее заданный stub-ответ.

## Q19. В чём разница между `@MockBean` и `@SpyBean`?

Обе аннотации подменяют бин в `ApplicationContext`, но по-разному. `@MockBean` ставит **полную заглушку**: реальной логики нет, все методы по умолчанию возвращают `null`/`0`/`false`, пока вы не зададите поведение через `when(...)`. `@SpyBean` оборачивает **реальный** бин: методы выполняют настоящий код, но вызовы можно перехватывать, считать и при желании переопределять. Кратко: mock — «всё фейк, что не задал»; spy — «всё настоящее, что не переопределил».

| Аспект | `@MockBean` | `@SpyBean` |
|--------|------------|-----------|
| Поведение | Полная замена бина | Оборачивает реальный бин |
| По умолчанию | Возвращает `null`/`0`/`false` | Вызывает реальный метод |
| Когда использовать | Нужна полная изоляция от зависимости | Нужно перехватить часть вызовов |

```java
@SpringBootTest
class NotificationTest {

    // Полная замена — emailService не отправляет реальные письма
    @MockBean
    private EmailService emailService;

    // Частичный мок — реальная логика + перехват
    @SpyBean
    private AuditService auditService;

    @Autowired
    private UserService userService;

    @Test
    void shouldCreateUserAndNotify() {
        when(emailService.send(any()))
            .thenReturn(true);

        userService.createUser("john@example.com");

        verify(emailService).send(argThat(
            email -> email.getTo().equals("john@example.com")));
        // auditService вызван реально, но можно проверить
        verify(auditService).log(eq("USER_CREATED"), any());
    }
}
```

**Скрытая цена — кеш контекста.** Spring кеширует `ApplicationContext` и переиспользует между тестами с одинаковой конфигурацией. Но `@MockBean`/`@SpyBean` меняют состав бинов, поэтому Spring считает такую конфигурацию уникальной и поднимает **отдельный** контекст. Если в разных тестах мокаются разные наборы бинов, контекст пересоздаётся снова и снова — и весь набор замедляется.

**Как смягчить:** группировать тесты с одинаковым набором моков (общий контекст переиспользуется) и переходить на `@MockitoBean`/`@MockitoSpyBean` (Spring Boot 3.4+) — современную замену устаревающим `@MockBean`/`@SpyBean`.

## Q20. Как тестировать с `WireMock`: сценарии ошибок и задержки?

Главная ценность `WireMock` — воспроизводить «плохое» поведение внешнего сервиса, которое в обычном тесте не получишь: таймаут, ответ 5xx, нестабильность с восстановлением. Именно на таких сценариях проверяется устойчивость клиента — отрабатывают ли таймауты, маппинг ошибок и retry-логика.

- **Задержка** — `withFixedDelay(ms)` заставляет проверять таймаут клиента.
- **Ошибка сервера** — `withStatus(500)` проверяет, превращается ли 5xx в нужное исключение.
- **Stateful-сценарий** — через `inScenario(...)` и состояния первый запрос отвечает 503, следующий 200, что проверяет retry.

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class PaymentErrorScenariosTest {

    @Autowired
    private PaymentClient paymentClient;

    @Test
    void shouldHandleTimeout() {
        stubFor(post(urlEqualTo("/api/charge"))
            .willReturn(aResponse()
                .withFixedDelay(5000) // 5 секунд задержка
                .withStatus(200)));

        assertThrows(PaymentTimeoutException.class, () ->
            paymentClient.charge(BigDecimal.TEN, card));
    }

    @Test
    void shouldHandleServerError() {
        stubFor(post(urlEqualTo("/api/charge"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")));

        assertThrows(PaymentServiceException.class, () ->
            paymentClient.charge(BigDecimal.TEN, card));
    }

    @Test
    void shouldRetryOnTransientError() {
        // Первый вызов — 503, второй — 200
        stubFor(post(urlEqualTo("/api/charge"))
            .inScenario("Retry")
            .whenScenarioStateIs(STARTED)
            .willReturn(aResponse().withStatus(503))
            .willSetStateTo("RECOVERED"));

        stubFor(post(urlEqualTo("/api/charge"))
            .inScenario("Retry")
            .whenScenarioStateIs("RECOVERED")
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {"transactionId": "txn_456", "status": "SUCCESS"}
                    """)));

        PaymentResult result = paymentClient.charge(
            BigDecimal.TEN, card);

        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        verify(2, postRequestedFor(urlEqualTo("/api/charge")));
    }
}
```

**Как работают Scenarios.** Обычный stub отвечает на каждый запрос одинаково. Scenario добавляет состояние: stub срабатывает только в нужном состоянии (`whenScenarioStateIs`) и переводит сценарий в следующее (`willSetStateTo`). Так и моделируется «упал, потом восстановился» — то, без чего не проверить retry и circuit breaker.

## Q21. (!) Что такое контрактное тестирование?

**Контрактное тестирование** проверяет, что два сервиса по-прежнему «понимают» друг друга, не поднимая их вместе. Consumer (вызывающий) фиксирует свои ожидания к API — какие поля и форматы ему нужны, — а Provider (поставщик) автоматически проверяет, что его реальный ответ этим ожиданиям соответствует.

Зачем так, а не E2E: контракт ловит breaking change в API **на стороне provider'а до деплоя**, без общего стенда и без подъёма обеих систем. Если provider случайно убрал поле или сменил тип — его сборка покраснеет, ещё до того как consumer об этом узнает в проде.

Поток данных между сторонами:

- **Consumer (`OrderService`)** — `Consumer Test` генерирует `Pact-файл`.
- `Pact-файл` публикуется в **`Pact Broker`**.
- **Provider (`UserService`)** — `Pact Broker` отдаёт `Pact-файл` в `Provider Test`, который верифицирует им реальный API. (Без брокера тот же `Pact-файл` может попадать в `Provider Test` напрямую.)

### Зачем нужно

- **Раннее обнаружение** breaking changes до деплоя
- **Независимость** команд: не нужен общий staging для проверки интеграции
- **Документация**: контракт -- актуальная спецификация API
- **Скорость**: быстрее E2E-тестов

### Два основных инструмента

1. **Pact** -- language-agnostic, consumer-driven, JSON-based
2. **Spring Cloud Contract** -- Spring-экосистема, Groovy/YAML DSL, генерация тестов

Подробнее о микросервисной архитектуре -- в [вопросах по микросервисам](../architecture/microservices-interview.md).

## Q22. Как работает Consumer-Driven Contract Testing с `Pact`?

Подход называется consumer-driven, потому что контракт диктует потребитель. Цикл из двух шагов:

1. **Consumer** в своём тесте описывает ожидаемый ответ, гоняет клиент против встроенного mock-сервера Pact и при успехе генерирует pact-файл (JSON с примерами запросов/ответов).
2. **Provider** берёт этот файл и проигрывает каждое взаимодействие против реального API, предварительно подготовив нужное состояние через `@State`.

### Consumer side

```java
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "UserService", port = "8081")
class OrderServiceConsumerTest {

    @Pact(consumer = "OrderService")
    public RequestResponsePact getUserPact(PactDslWithProvider builder) {
        return builder
            .given("User with ID 123 exists")
            .uponReceiving("A request for user details")
            .path("/api/users/123")
            .method("GET")
            .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .numberType("id", 123)
                .stringType("name", "John Doe")
                .stringType("email", "john@example.com"))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "getUserPact")
    void shouldGetUserDetails(MockServer mockServer) {
        UserClient client = new UserClient(mockServer.getUrl());

        User user = client.getUser(123L);

        assertEquals(123L, user.getId());
        assertEquals("John Doe", user.getName());
    }
}
```

### Provider side

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("UserService")
@PactFolder("pacts")
class UserServiceProviderTest {

    @Autowired
    private UserRepository userRepository;

    @State("User with ID 123 exists")
    public void userExists() {
        userRepository.save(new User(123L, "John Doe", "john@example.com"));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
```

Связующее звено — **Pact Broker**: consumer публикует туда pact-файлы, provider их оттуда забирает для верификации. Broker заодно хранит историю версий и показывает матрицу совместимости (какая версия consumer'а работает с какой версией provider'а). Для простых случаев файлы можно держать прямо в репозитории (`@PactFolder`).

## Q23. Как использовать `Spring Cloud Contract`?

Spring Cloud Contract решает ту же задачу, что Pact, но «в обратную сторону»: контракт пишут **на стороне provider'а** (Groovy или YAML DSL), и из него генерируется и тест для provider'а, и заглушки для consumer'а. Поток такой:

1. провайдер описывает контракт — пару «запрос → ответ»;
2. плагин генерирует из контракта тест, который проверяет реальный контроллер провайдера;
3. тот же контракт публикуется как артефакт со стабами (WireMock), который потребитель скачивает и гоняет против своего клиента.

### 1. Определение контракта (Groovy DSL)

```groovy
// src/test/resources/contracts/shouldReturnUser.groovy
Contract.make {
    request {
        method 'GET'
        url '/api/users/123'
    }
    response {
        status 200
        body(
            id: 123,
            name: "John Doe",
            email: "john@example.com"
        )
        headers {
            contentType(applicationJson())
        }
    }
}
```

### 2. Автоматически сгенерированный тест (Provider)

Spring Cloud Contract автоматически генерирует тестовый класс из контракта.

### 3. Stub на стороне Consumer

```java
@SpringBootTest
@AutoConfigureStubRunner(
    ids = "com.example:user-service:+:stubs:8082",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class OrderServiceContractTest {

    @Autowired
    private UserClient userClient;

    @Test
    void shouldGetUserFromStub() {
        User user = userClient.getUser(123L);

        assertEquals(123L, user.getId());
        assertEquals("John Doe", user.getName());
    }
}
```

**Pact vs Spring Cloud Contract.** Pact — consumer-driven и language-agnostic (контракт диктует потребитель, инструмент кросс-языковой). Spring Cloud Contract — producer-driven и заточен под Spring/JVM: один и тот же контракт даёт и тест провайдера, и WireMock-стаб для потребителя через `@AutoConfigureStubRunner`. Подробнее о Spring Cloud — в [вопросах по Spring Cloud](../frameworks/spring/spring-cloud-interview.md).

## Q24. Как тестировать микросервисы в изоляции?

Изолированно — значит проверять **один** сервис целиком, но без поднятия всей экосистемы. Реальной оставляют собственную инфраструктуру сервиса (его БД через `Testcontainers`), а соседние сервисы заменяют моками или контрактными стабами. Так тест остаётся быстрым и стабильным: падение соседнего сервиса или общего стенда на него не влияет.

### Component Testing -- тестирование одного сервиса

```java
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
    })
@Testcontainers
class UserServiceComponentTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @MockBean
    private EmailService emailService; // Мок внешнего сервиса

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateUser() {
        when(emailService.sendWelcomeEmail(anyString()))
            .thenReturn(true);

        var request = new UserRequest("john@example.com", "secret");
        ResponseEntity<UserResponse> response = restTemplate
            .postForEntity("/api/users", request, UserResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(emailService).sendWelcomeEmail("john@example.com");
    }
}
```

**Пирамида для микросервисов** — от быстрых и многочисленных к медленным и редким:

1. **Unit-тесты** — бизнес-логика без Spring; их большинство.
2. **Component tests** — один сервис + его БД, соседи замоканы; проверяют сервис целиком.
3. **Contract tests** — стыки между сервисами вместо дорогих интеграций (см. Q21–Q23).
4. **E2E** — реальная связка нескольких сервисов; только критичные пути, минимум штук, потому что медленны и хрупки.

## Q25. (!) Как тестировать `Kafka` с `Testcontainers`?

`KafkaContainer` поднимает настоящий брокер в Docker, а `@ServiceConnection` автоматически прописывает `bootstrap.servers`. Дальше тест работает как обычное приложение: `KafkaTemplate` шлёт событие, реальный `@KafkaListener` его обрабатывает.

Главная тонкость — **асинхронность**. Между `send()` и обработкой консьюмером проходит время, поэтому проверять результат сразу нельзя. Правильный способ — `Awaitility` (`await().atMost(...).untilAsserted(...)`), который опрашивает условие до готовности или таймаута. `Thread.sleep` здесь запрещён: либо флакает, либо тормозит.

```java
@SpringBootTest
@Testcontainers
class KafkaIntegrationTest {

    @Container
    @ServiceConnection
    static KafkaContainer kafka =
        new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Autowired
    private OrderEventConsumer consumer;

    @Test
    void shouldProduceAndConsumeEvent() {
        // Отправляем событие
        OrderEvent event = new OrderEvent(1L, "CREATED",
            BigDecimal.valueOf(100));
        kafkaTemplate.send("order-events", event);

        // Ждём обработки консьюмером
        await().atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                verify(consumer).handleOrderEvent(argThat(
                    e -> e.getOrderId().equals(1L)));
            });
    }

    @Test
    void shouldHandleDeserializationError() {
        // Отправляем невалидные данные
        kafkaTemplate.send("order-events",
            "invalid-key", null);

        // Проверяем, что ошибка обработана (DLQ)
        await().atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                List<String> dlqMessages = consumeFromDlq();
                assertFalse(dlqMessages.isEmpty());
            });
    }
}
```

Альтернатива без Docker -- `@EmbeddedKafka` из `spring-kafka-test`:

```java
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = {"order-events"},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
class EmbeddedKafkaTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldSendMessage() {
        kafkaTemplate.send("order-events", "test-message");
        // ...
    }
}
```

**Что выбрать.** `@EmbeddedKafka` — это in-JVM брокер: не нужен Docker, старт быстрее, удобно в CI без Docker. Но он не идентичен production-брокеру и может разойтись в поведении. `Testcontainers` гоняет настоящий образ Kafka — медленнее, зато достовернее. Эмпирическое правило: `@EmbeddedKafka` для быстрых тестов логики продюсера/консьюмера, `Testcontainers` — когда важна точность интеграции. Подробнее — в [вопросах по Kafka](../messaging/kafka-interview.md).

## Q26. Как тестировать асинхронные операции?

Проблема асинхронного теста: результат появляется не в момент вызова, а «когда-нибудь потом» — после обработки в другом потоке. Проверять сразу после вызова рано (данных ещё нет), а ставить фиксированный `Thread.sleep` плохо вдвойне: на медленной машине не дождётся (флак), на быстрой — впустую потратит время.

Правильный приём — **активное ожидание условия**: опрашивать систему до тех пор, пока ожидаемое не наступит, но не дольше таймаута.

### `Awaitility` -- стандарт для ожидания асинхронных результатов

`await().atMost(timeout).untilAsserted(...)` периодически выполняет блок assert'ов и считает тест успешным при первом прохождении; если за `atMost` условие не выполнилось — падает. Так тест завершается сразу по готовности, а не по таймеру.

```java
@SpringBootTest
@Testcontainers
class AsyncIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldSendNotificationAsync() {
        // Act — создание заказа запускает асинхронную отправку
        Order order = orderService.createOrder(userId, productId, 1);

        // Assert — ждём, пока async-обработчик создаст уведомление
        await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(500))
            .untilAsserted(() -> {
                List<Notification> notifications =
                    notificationRepository.findByOrderId(order.getId());
                assertEquals(1, notifications.size());
                assertEquals("ORDER_CREATED",
                    notifications.get(0).getType());
            });
    }

    @Test
    void shouldCompleteCompletableFuture() {
        CompletableFuture<PaymentResult> future =
            paymentService.processAsync(orderId, card);

        PaymentResult result = assertTimeout(
            Duration.ofSeconds(5),
            () -> future.get());

        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
    }
}
```

**Правило.** Никакого `Thread.sleep()` в тестах — он либо флакает, либо замедляет. Ждите по условию: `Awaitility` для опроса состояния, `CompletableFuture.get(timeout)` для результата фьючера, `CountDownLatch` для синхронизации с фоновым потоком.

## Q27. Как тестировать `message-driven` архитектуру?

В message-driven системе продюсер и консьюмер развязаны через брокер и не вызывают друг друга напрямую. Поэтому их тестируют по отдельности — продюсера и консьюмера — и лишь часть сценариев гоняют сквозь реальный брокер. Отдельно проверяют то, что свойственно именно очередям: идемпотентность (дубль сообщения) и обработку «ядовитых» сообщений (DLQ).

### Стратегия тестирования

Три уровня проверки и потоки данных в каждом:

- **Producer Test**: `Producer` отправляет в `Topic` → проверяем (assert: сообщение отправлено).
- **Consumer Test**: подаём сообщение из `Topic` в `Consumer` → он обрабатывает и пишет в `DB` → проверяем (assert: данные сохранены).
- **E2E Test**: полная цепочка `Producer` → `Topic` → `Consumer` → `DB`.

1. **Тест продюсера**: отправляем сообщение, проверяем, что оно попало в топик
2. **Тест консьюмера**: кладём сообщение в топик, проверяем обработку (данные в БД, вызов сервиса)
3. **E2E**: полная цепочка через реальный брокер
4. **Тест идемпотентности**: отправляем одно сообщение дважды, проверяем, что обработано один раз
5. **Тест DLQ**: отправляем невалидное сообщение, проверяем, что попало в dead letter queue

## Q28. Как организовать интеграционные тесты в `CI/CD`?

Ключевая идея — **fail fast**: дешёвые и быстрые тесты идут первыми, и пайплайн обрывается на первом же провале, не тратя время на дорогие стадии. Поэтому unit-тесты гоняют раньше интеграционных, интеграционные — раньше contract и E2E.

### Разделение тестов по стадиям

Стадии `CI Pipeline` идут по нарастанию стоимости, каждая после предыдущей:

- `Unit Tests` (1-2 мин) → `Integration Tests` (5-10 мин) → `Contract Tests` (2-3 мин) → `E2E Tests` (10-20 мин).

Принцип fail fast обрывает пайплайн на первом провале: если падают `Unit Tests` или `Integration Tests`, дальнейшие стадии не запускаются (Stop).

### Gradle конфигурация

```groovy
// build.gradle
tasks.register('integrationTest', Test) {
    description = 'Runs integration tests'
    group = 'verification'

    testClassesDirs = sourceSets.integrationTest.output.classesDirs
    classpath = sourceSets.integrationTest.runtimeClasspath

    useJUnitPlatform {
        includeTags 'integration'
    }

    shouldRunAfter test
}

check.dependsOn integrationTest
```

### GitHub Actions

```yaml
name: Integration Tests
on: [push, pull_request]

jobs:
  integration-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Run integration tests
        run: ./gradlew integrationTest
        # Testcontainers использует Docker, который
        # доступен в GitHub Actions из коробки
```

**Почему `Testcontainers` упрощает CI.** Без него инфраструктуру (БД, брокер) пришлось бы поднимать вручную в pipeline — через `services:` в GitHub Actions/GitLab CI и синхронизировать с тестами. С `Testcontainers` всё описано в коде теста и стартует само; от CI требуется только доступный Docker-демон, который в большинстве раннеров есть из коробки.

## Q29. Как ускорить интеграционные тесты?

Два главных источника медленности — пересоздание `ApplicationContext` и повторный старт контейнеров. Поэтому самые крупные выигрыши дают приёмы, которые делают и то и другое **один раз**.

### 1. Переиспользование `ApplicationContext`

Spring кеширует контекст и переиспользует его между тестами с одинаковой конфигурацией — это самый большой рычаг скорости. Любой `@MockBean`/`@SpyBean` делает конфигурацию уникальной и заставляет поднять отдельный контекст (см. Q19), поэтому используйте их экономно.

### 2. Singleton-контейнеры

```java
// Один контейнер на все тесты (см. Q12)
public abstract class AbstractIntegrationTest {
    static final PostgreSQLContainer<?> POSTGRES;
    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16");
        POSTGRES.start();
    }
}
```

### 3. Parallel execution

```properties
# junit-platform.properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
```

### 4. Test slices вместо `@SpringBootTest`

Используйте `@WebMvcTest`, `@DataJpaTest` где возможно -- они поднимают меньше бинов.

### 5. Lazy initialization в тестовом профиле

```yaml
# application-test.yml
spring:
  main:
    lazy-initialization: true
```

### Метрики

| Техника | Экономия |
|---------|---------|
| Singleton-контейнеры | 30-50% времени |
| Кеш `ApplicationContext` | 40-60% |
| Test slices | 50-70% для отдельных тестов |
| Parallel execution | 30-50% (зависит от CPU) |

## Q30. Как организовать параллельное выполнение тестов?

Параллелизм ускоряет прогон, но безопасен только при одном условии: тесты не делят изменяемое состояние. Поэтому задача из двух частей — включить параллельность в JUnit 5 и обеспечить изоляцию данных, чтобы тесты не мешали друг другу в общей БД.

### JUnit 5 параллельность

JUnit 5 управляет параллелизмом через `junit-platform.properties`: `parallel.enabled=true` включает его, а `classes.default=concurrent` гоняет классы одновременно. Стратегия `fixed` с `parallelism=N` задаёт число потоков.

```properties
# src/test/resources/junit-platform.properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=same_thread
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.strategy=fixed
junit.jupiter.execution.parallel.config.fixed.parallelism=4
```

### Изоляция данных при параллельности

```java
@SpringBootTest
@Testcontainers
class ParallelSafeTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16");

    @Test
    void shouldIsolateData() {
        // Уникальные данные для каждого теста
        String uniqueEmail = "user-" + UUID.randomUUID() + "@test.com";
        User user = userRepository.save(new User(uniqueEmail));

        User found = userRepository.findByEmail(uniqueEmail).orElseThrow();
        assertEquals(uniqueEmail, found.getEmail());
    }
}
```

Главное правило: тесты при параллельном выполнении не должны зависеть от общего состояния. Каждый тест создаёт свои данные с уникальными идентификаторами.

## Q31. (!) Как бороться с flaky-тестами?

**Flaky-тест** — тест, который то проходит, то падает на одном и том же коде. Он опаснее честно красного: команда привыкает «перезапустить, авось позеленеет», и в итоге перестаёт верить CI вообще. Почти всегда корень — недетерминизм: общее состояние, зависимость от времени или порядка, гонки. Лечение — не «перезапускать», а убирать сам источник недетерминизма.

Основные причины и решения:

| Причина | Решение |
|---------|---------|
| Зависимость от порядка выполнения | Изоляция данных, очистка в `@AfterEach` |
| Таймауты (сеть, Docker) | Увеличить timeout, использовать `Awaitility` |
| Зависимость от времени | Инжектить `Clock`, мокировать время |
| Порт уже занят | `RANDOM_PORT`, динамические порты |
| Shared state | Уникальные данные (UUID), отдельные схемы |
| Race conditions | `Awaitility` вместо `Thread.sleep`, `@ResourceLock` |

```java
@SpringBootTest
class StableTest {

    // Инжектируем Clock для контроля времени
    @Autowired
    private Clock clock;

    @MockBean
    private Clock testClock;

    @BeforeEach
    void setUp() {
        // Фиксированное время — тест детерминирован
        when(testClock.instant())
            .thenReturn(Instant.parse("2026-01-15T10:00:00Z"));
        when(testClock.getZone())
            .thenReturn(ZoneId.of("UTC"));
    }

    @Test
    void shouldNotDependOnCurrentTime() {
        Subscription sub = subscriptionService.create(userId);

        // Всегда одинаковый результат
        assertEquals(LocalDate.of(2026, 2, 15),
            sub.getExpiresAt());
    }
}
```

**Как ловить.** Заведите в CI метрику процента прохождения по каждому тесту. Тест, который зеленеет реже 99% запусков, считается flaky и идёт в починку или в карантин — пока он «мигает», доверие к набору падает.

## Q32. Что такое Test Fixtures и как их организовать?

**Test Fixtures** — заранее подготовленное окружение и данные, на которых выполняется тест. Чем больше тестов, тем важнее их организация: если каждый тест собирает объекты руками, любое изменение модели ломает десятки тестов сразу. Цель фикстур — собрать создание тестовых данных в одном месте и сделать его читаемым.

### Паттерны

Два классических способа убрать дублирование при создании объектов:

```java
// 1. Builder — гибкое создание тестовых объектов
public class TestUserBuilder {
    private String email = "default@test.com";
    private String name = "Test User";
    private UserType type = UserType.REGULAR;

    public TestUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public TestUserBuilder premium() {
        this.type = UserType.PREMIUM;
        return this;
    }

    public User build() {
        return new User(email, name, type);
    }
}

// 2. Object Mother — фабрика типовых объектов
public class TestData {
    public static User premiumUser() {
        return new TestUserBuilder()
            .withEmail("premium@test.com")
            .premium()
            .build();
    }

    public static Order paidOrder(User user) {
        return new Order(user, BigDecimal.valueOf(100),
            OrderStatus.PAID);
    }
}

// Использование
@Test
void shouldApplyPremiumDiscount() {
    User user = TestData.premiumUser();
    Order order = orderService.create(user.getId(), productId);
    assertEquals(BigDecimal.valueOf(85), order.getTotal());
}
```

### Где что готовить

Распределение по жизненному циклу — главный принцип: дорогое делаем один раз, изолирующее — на каждый тест.

- `@BeforeAll` — дорогая инициализация один раз на класс (запуск контейнеров);
- `@BeforeEach` — то, что нужно изолировать между тестами: seed данных, очистка;
- базовый класс (`AbstractIntegrationTest`) — общие контейнеры и конфигурация для всех наследников;
- `@Sql` — декларативная загрузка SQL-данных под конкретный тест.

## Q33. Как тестировать кеширование?

Кеш проверяется не значениями, а **числом обращений к источнику**. Идея: подменить репозиторий через `@SpyBean` и считать вызовы. Если кеш работает, второй запрос за тем же ключом не должен дойти до БД — счётчик `verify(repo, times(1))` останется прежним. А `eviction` проверяется наоборот: после обновления данных кеш должен сброситься, и следующий запрос обязан снова сходить в источник.

```java
@SpringBootTest
@Testcontainers
class CacheIntegrationTest {

    @Container
    @ServiceConnection
    static GenericContainer<?> redis =
        new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @Autowired
    private ProductService productService;

    @SpyBean
    private ProductRepository productRepository;

    @Test
    void shouldCacheProductLookup() {
        Long productId = 1L;

        // Первый вызов — идёт в БД
        Product first = productService.findById(productId);
        verify(productRepository, times(1)).findById(productId);

        // Второй вызов — из кеша, БД не вызывается
        Product second = productService.findById(productId);
        verify(productRepository, times(1)).findById(productId); // всё ещё 1

        assertEquals(first.getName(), second.getName());
    }

    @Test
    void shouldEvictCacheOnUpdate() {
        Long productId = 1L;

        // Загружаем в кеш
        productService.findById(productId);

        // Обновление инвалидирует кеш
        productService.updatePrice(productId, BigDecimal.valueOf(200));

        // Следующий вызов снова идёт в БД
        productService.findById(productId);
        verify(productRepository, times(2)).findById(productId);
    }
}
```

**Что покрыть:** попадание в кеш (hit), промах (miss → поход в БД), `eviction` при обновлении, истечение `TTL` (через мок `Clock`, чтобы не ждать реально). **Чего не делать:** не проверяйте работу самой библиотеки кеширования — она уже протестирована. Ваша задача — проверить, что *ваша* конфигурация (`@Cacheable`/`@CacheEvict` на нужных методах, правильные ключи) ведёт себя как задумано.

## Q34. Что такое `Smoke Testing` после деплоя?

**Smoke test** — минимальный набор проверок сразу после деплоя, отвечающий на один вопрос: «вообще запустилось и живо?». Контекст поднялся, health-эндпоинт отдаёт `UP`, ключевые маршруты отвечают. Это не проверка функциональности, а быстрый сигнал «деплой удался / откатываемся» — поэтому такие тесты должны быть считанные секунды.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("smoke")
class SmokeTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void applicationStarts() {
        // Если контекст поднялся — приложение работает
    }

    @Test
    void healthEndpointIsUp() {
        ResponseEntity<String> response = restTemplate
            .getForEntity("/actuator/health", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"UP\""));
    }

    @Test
    void mainPageResponds() {
        ResponseEntity<String> response = restTemplate
            .getForEntity("/", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
```

**Место в процессе.** Smoke-тесты гоняют в pipeline сразу после каждого деплоя; их падение — триггер на rollback или алерт. Они не заменяют полноценные интеграционные тесты (те уже отработали раньше, до выкатки), а лишь подтверждают, что развёрнутая сборка живёт в целевом окружении. Подробнее о стратегиях деплоя — в [вопросах по стратегиям деплоя](../cicd/deployment-strategies-interview.md).

## Q35. (!) Какие best practices для интеграционного тестирования?

Большинство правил сводятся к двум целям: тесты должны быть **достоверными** (ловить настоящие баги) и **быстрыми** (давать обратную связь, а не тормозить разработку).

### 1. Используйте `Testcontainers` вместо `H2`

Реальная БД ловит реальные баги. `H2` расходится с `PostgreSQL` в поведении SQL, `JSONB`, оконных функций — тест на `H2` может зеленеть там, где прод падает.

### 2. Не ставьте `@Transactional` на интеграционные тесты

Скрывает `LazyInitializationException` и не тестирует транзакционные границы (см. Q17).

### 3. Изолируйте тесты

Каждый тест должен работать независимо. Очищайте данные в `@AfterEach` или используйте уникальные данные.

### 4. Минимизируйте `@MockBean`

Каждый уникальный набор `@MockBean` ломает кеш контекста и заставляет Spring поднимать его заново (см. Q19) — на большом наборе это главный скрытый тормоз.

### 5. Используйте test slices

`@WebMvcTest`, `@DataJpaTest` быстрее `@SpringBootTest`. Используйте полный контекст только когда нужен.

### 6. Организуйте тесты по уровням

```
src/test/java/          — unit-тесты
src/integrationTest/    — интеграционные тесты
```

### 7. Не дублируйте unit-покрытие

Интеграционные тесты проверяют **взаимодействие** (SQL, контракты, конфигурацию), а не ветвления бизнес-логики. Все варианты алгоритма гоняйте дешёвыми unit-тестами, а в интеграционных оставляйте один-два «счастливых пути» через всю связку.

### 8. Мониторьте flaky-тесты

Нестабильные тесты подрывают доверие к CI. Чините или удаляйте.

### 9. Быстрая обратная связь

Разделяйте тесты по скорости: fast (unit) запускаются на каждый коммит, slow (integration) -- на PR.

### 10. Читаемость: `Given-When-Then`

```java
@Test
void shouldRejectOrderForBlockedUser() {
    // Given
    User blockedUser = userRepository.save(
        TestData.blockedUser());

    // When / Then
    assertThrows(UserBlockedException.class, () ->
        orderService.createOrder(blockedUser.getId(), productId));
}
```

Подробнее о стратегиях тестирования -- в [вопросах по стратегиям тестирования](test-strategies-interview.md).

## Q36. (!) Как использовать `@DataJpaTest` для тестирования репозиториев?

`@DataJpaTest` — test slice для слоя персистентности: он поднимает `EntityManager`, репозитории Spring Data и прогоняет миграции Flyway/Liquibase, но НЕ загружает контроллеры и сервисы. Это даёт быстрый и сфокусированный тест репозитория. Бонусом каждый тест по умолчанию оборачивается в транзакцию с автоматическим rollback — данные между тестами не текут (но помните про оговорку из Q17).

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // отключаем H2, используем реальную БД
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;  // обёртка над EntityManager для тестов

    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User("alice@example.com", "Alice");
        entityManager.persistAndFlush(user);   // сохраняем и сбрасываем кеш
        entityManager.clear();                 // очищаем first-level cache

        // When
        Optional<User> found = userRepository.findByEmail("alice@example.com");

        // Then
        assertThat(found).isPresent()
            .get()
            .extracting(User::getName)
            .isEqualTo("Alice");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllActiveUsers() {
        // Given
        entityManager.persist(new User("alice@example.com", "Alice", true));
        entityManager.persist(new User("bob@example.com", "Bob", false));
        entityManager.flush();

        // When
        List<User> activeUsers = userRepository.findAllByActiveTrue();

        // Then
        assertThat(activeUsers).hasSize(1)
            .extracting(User::getEmail)
            .containsExactly("alice@example.com");
    }
}
```

### Отличие `@DataJpaTest` от других слайсов

| Слайс | Загружает | Не загружает |
|-------|-----------|-------------|
| `@DataJpaTest` | JPA, репозитории, EntityManager, Flyway | Контроллеры, сервисы, `@Service` бины |
| `@WebMvcTest` | MVC контроллеры, `@ControllerAdvice`, Security | Сервисы, репозитории, БД |
| `@JsonTest` | Jackson ObjectMapper, `@JsonComponent` | Контроллеры, сервисы, репозитории |
| `@SpringBootTest` | Весь контекст | — |

**Не забыть про H2.** Как и в Q4, по умолчанию `@DataJpaTest` подменяет реальный `DataSource` встроенной H2. Чтобы тест шёл против настоящей БД через Testcontainers — обязателен `@AutoConfigureTestDatabase(replace = NONE)`. Заметьте также `entityManager.clear()` в тесте: он сбрасывает first-level cache Hibernate, иначе `findByEmail` мог бы вернуть объект прямо из кеша сессии, минуя SQL, и тест не проверил бы реальный запрос.

## Q37. Как тестировать JSON-сериализацию с `@JsonTest`?

`@JsonTest` поднимает только конфигурацию маппинга JSON (Jackson, или Gson/JSON-B) — без web-слоя, сервисов и БД. Зачем отдельный слайс: формат ответа API часто важен сам по себе (имена полей, формат дат, какие поля скрыты), и проверять его через полный `@WebMvcTest` избыточно. `@JsonTest` даёт `JacksonTester`, который сериализует и десериализует объект и сверяет результат по JSON-path.

```java
@JsonTest
class OrderDtoJsonTest {

    @Autowired
    private JacksonTester<OrderDto> json;

    @Test
    void shouldSerializeOrderDto() throws Exception {
        OrderDto order = new OrderDto(
            1L,
            "Alice",
            BigDecimal.valueOf(99.99),
            OrderStatus.PENDING,
            LocalDateTime.of(2026, 4, 13, 12, 0)
        );

        // Сериализация в JSON
        JsonContent<OrderDto> result = json.write(order);

        assertThat(result).hasJsonPathNumberValue("$.id", 1L);
        assertThat(result).hasJsonPathStringValue("$.customerName", "Alice");
        assertThat(result).hasJsonPathNumberValue("$.amount", 99.99);
        assertThat(result).hasJsonPathStringValue("$.status", "PENDING");
        // Проверяем формат даты
        assertThat(result).hasJsonPathStringValue("$.createdAt", "2026-04-13T12:00:00");
        // Убеждаемся, что поле отсутствует (игнорируется)
        assertThat(result).doesNotHaveJsonPathValue("$.internalField");
    }

    @Test
    void shouldDeserializeOrderDto() throws Exception {
        String json = """
            {
              "id": 1,
              "customerName": "Alice",
              "amount": 99.99,
              "status": "PENDING"
            }
            """;

        // Десериализация из JSON
        ObjectContent<OrderDto> result = this.json.parse(json);

        assertThat(result).usingRecursiveComparison()
            .ignoringFields("createdAt")
            .isEqualTo(new OrderDto(1L, "Alice", BigDecimal.valueOf(99.99), OrderStatus.PENDING, null));
    }

    @Test
    void shouldHandleNullFields() throws Exception {
        OrderDto order = new OrderDto(1L, null, null, OrderStatus.PENDING, null);
        JsonContent<OrderDto> result = json.write(order);

        // По умолчанию null-поля включаются
        assertThat(result).hasJsonPathValue("$.customerName");
        // или исключаются при @JsonInclude(NON_NULL)
    }
}
```

**Что обычно проверяют:** точные имена полей контракта, формат даты/времени, что внутренние поля не утекают наружу (`doesNotHaveJsonPathValue`), и поведение при `null` (включается поле или скрывается через `@JsonInclude(NON_NULL)`). Это ловит случайные breaking changes в контракте API задолго до интеграционных тестов.

## Q38. (!) Как использовать `RestAssured` для интеграционных API-тестов?

`REST Assured` — библиотека для тестирования REST API с выразительным DSL в стиле `given-when-then` (задаём запрос → шлём → проверяем ответ). Ключевое отличие от `MockMvc`: запросы идут по **настоящей** сети к поднятому серверу (`RANDOM_PORT`), поэтому проходят весь стек — фильтры, сервлеты, сериализацию, реальные HTTP-статусы. Это делает её естественным выбором для E2E-тестов API.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }

    @Test
    void shouldCreateOrder() {
        String requestBody = """
            {
              "customerId": 1,
              "items": [{"productId": 42, "quantity": 2}]
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/orders")
        .then()
            .statusCode(201)
            .header("Location", containsString("/api/v1/orders/"))
            .body("status", equalTo("PENDING"))
            .body("customerId", equalTo(1))
            .body("id", notNullValue());
    }

    @Test
    void shouldReturn404ForUnknownOrder() {
        given()
            .pathParam("id", 99999)
        .when()
            .get("/orders/{id}")
        .then()
            .statusCode(404)
            .body("error", equalTo("Order not found"))
            .body("timestamp", notNullValue());
    }

    @Test
    void shouldReturnOrdersList() {
        given()
            .queryParam("status", "PENDING")
            .queryParam("page", 0)
            .queryParam("size", 10)
        .when()
            .get("/orders")
        .then()
            .statusCode(200)
            .body("content", hasSize(greaterThanOrEqualTo(0)))
            .body("page.size", equalTo(10));
    }
}
```

### `RestAssured` vs `MockMvc`

| Критерий | `RestAssured` | `MockMvc` |
|----------|---------------|-----------|
| Реальный HTTP | Да (`RANDOM_PORT`) | Нет (in-process) |
| Скорость | Медленнее | Быстрее |
| Настройка | Проще для API-тестов | Требует `MockMvcRequestBuilders` |
| Middleware | Тестирует реальные фильтры/сервлеты | Мокирует HTTP-слой |
| Подходит для | E2E API-тестов | Слайс-тестов контроллеров |

## Q39. Как тестировать `Spring Security` в интеграционных тестах?

Тест безопасности проверяет три исхода: неаутентифицированный запрос отвергается (401), аутентифицированный без нужной роли — запрещён (403), с нужной ролью — проходит (200/204). Подделывать реальный логин не нужно — `spring-security-test` даёт аннотацию `@WithMockUser`, которая кладёт готовый `Authentication` в `SecurityContext` прямо перед тестом. Для нестандартной аутентификации (например, JWT) пишут свой `@WithSecurityContext`-фабрику, как `WithMockJwtToken` ниже.

```java
@WebMvcTest(OrderController.class)
class OrderControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void shouldReturn200ForAuthenticatedUser() throws Exception {
        when(orderService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/orders"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void shouldReturn403WhenUserLacksAdminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/1"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldAllowAdminToDeleteOrder() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/1"))
            .andExpect(status().isNoContent());
        verify(orderService).delete(1L);
    }
}

// Кастомный SecurityContext для JWT-аутентификации
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwtTokenFactory.class)
public @interface WithMockJwtToken {
    String subject() default "user";
    String[] scopes() default {"read"};
}

class WithMockJwtTokenFactory implements WithSecurityContextFactory<WithMockJwtToken> {
    @Override
    public SecurityContext createSecurityContext(WithMockJwtToken annotation) {
        Jwt jwt = Jwt.withTokenValue("mock-token")
            .header("alg", "none")
            .claim("sub", annotation.subject())
            .claim("scope", String.join(" ", annotation.scopes()))
            .build();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        return ctx;
    }
}

// Использование кастомной аннотации
@Test
@WithMockJwtToken(subject = "user-123", scopes = {"orders:read"})
void shouldAllowJwtUserToReadOrders() throws Exception {
    mockMvc.perform(get("/api/v1/orders"))
        .andExpect(status().isOk());
}
```

## Q40. Как использовать `@RestClientTest` для тестирования HTTP-клиентов?

`@RestClientTest` — это слайс для **исходящих** HTTP-вызовов: он поднимает только инфраструктуру HTTP-клиентов (`RestTemplateBuilder`/`RestClient`) и подставляет `MockRestServiceServer`. Этот mock-сервер перехватывает запросы клиента «на лету» — без реального сетевого соединения и без поднятия отдельного сервера (как у WireMock). На нём задают ожидаемый запрос и ответ, а затем проверяют, что клиент верно сформировал запрос и разобрал ответ.

```java
// Тестируемый HTTP-клиент
@Component
public class PaymentGatewayClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentGatewayClient(RestTemplateBuilder builder,
                                 @Value("${payment.gateway.url}") String baseUrl) {
        this.restTemplate = builder.build();
        this.baseUrl = baseUrl;
    }

    public PaymentResult charge(ChargeRequest request) {
        return restTemplate.postForObject(
            baseUrl + "/charge", request, PaymentResult.class);
    }
}

// Тест с @RestClientTest
@RestClientTest(PaymentGatewayClient.class)
class PaymentGatewayClientTest {

    @Autowired
    private PaymentGatewayClient client;

    @Autowired
    private MockRestServiceServer mockServer;  // автоматически создаётся

    @Value("${payment.gateway.url}")
    private String gatewayUrl;

    @Test
    void shouldChargeSuccessfully() throws Exception {
        mockServer.expect(requestTo(gatewayUrl + "/charge"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andRespond(withSuccess("""
                {"transactionId": "txn-123", "status": "SUCCESS"}
                """, MediaType.APPLICATION_JSON));

        PaymentResult result = client.charge(new ChargeRequest("card-456", 99.99));

        assertThat(result.getTransactionId()).isEqualTo("txn-123");
        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        mockServer.verify();  // убеждаемся, что все ожидания выполнены
    }

    @Test
    void shouldHandlePaymentGatewayError() {
        mockServer.expect(requestTo(gatewayUrl + "/charge"))
            .andRespond(withServerError());

        assertThatThrownBy(() -> client.charge(new ChargeRequest("card-456", 99.99)))
            .isInstanceOf(RestClientException.class);
    }
}
```

**Где он в ряду инструментов.** `@WebMvcTest` тестирует входящие запросы (ваш контроллер), `@RestClientTest` — исходящие (ваш клиент к чужому API). По сравнению с WireMock из Q18 он легче: не поднимает HTTP-сервер, а перехватывает вызовы в памяти, поэтому быстрее, но и не проверяет реальный сетевой слой. Правило: `@RestClientTest` — для быстрой проверки маппинга запросов/ответов, WireMock — когда нужно протестировать таймауты, реальные коды и сетевое поведение.

---

## See also

- [Chaos Engineering](chaos-engineering-interview.md)
- [Contract Testing](contract-testing-interview.md)
- [Load Testing](load-testing-interview.md)
- [Mockito](mockito-interview.md)
- [Mutation Testing](mutation-testing-interview.md)
- [Property-based Testing](property-based-testing-interview.md)
- [Unit Testing](unit-testing-interview.md) — изолированные тесты без внешних зависимостей
- [Стратегии тестирования](test-strategies-interview.md) — пирамида тестов, выбор уровня
- [Test Automation](test-automation-interview.md) — автоматизация и запуск в pipeline
- [Testcontainers](testcontainers-interview.md) — подробная шпаргалка по Docker-контейнерам для тестов
- [Spring Data JPA](../frameworks/spring/spring-data-jpa-interview.md) — тестирование слоя репозиториев с `@DataJpaTest`
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — конфигурация, профили, auto-configuration
- [Docker](../devops/docker-interview.md) — контейнеризация, на которой построен Testcontainers
- [Kafka](../messaging/kafka-interview.md) — тестирование event-driven архитектуры
