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
- [Q1. (!) Что такое интеграционное тестирование и чем оно отличается от unit-тестирования?](#q1--что-такое-интеграционное-тестирование-и-чем-оно-отличается-от-unit-тестирования)
- [Q2. Какие типы интеграционного тестирования существуют?](#q2-какие-типы-интеграционного-тестирования-существуют)
- [Q3. (!) Как работает `@SpringBootTest` и когда его использовать?](#q3--как-работает-springboottest-и-когда-его-использовать)
- [Q4. (!) Что такое Test Slices и какие бывают?](#q4--что-такое-test-slices-и-какие-бывают)

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
- [Q16. Как управлять тестовыми данными через `@Sql` и Flyway?](#q16-как-управлять-тестовыми-данными-через-sql-и-flyway)
- [Q17. В чём опасность `@Transactional` на интеграционных тестах?](#q17-в-чём-опасность-transactional-на-интеграционных-тестах)

**Мокирование внешних сервисов**
- [Q18. (!) Как использовать `WireMock` для мокирования HTTP-сервисов?](#q18--как-использовать-wiremock-для-мокирования-http-сервисов)
- [Q19. В чём разница между `@MockBean` и `@SpyBean`?](#q19-в-чём-разница-между-mockbean-и-spybean)
- [Q20. Как тестировать с `WireMock`: сценарии ошибок и задержки?](#q20-как-тестировать-с-wiremock-сценарии-ошибок-и-задержки)

**Contract Testing и микросервисы**
- [Q21. (!) Что такое контрактное тестирование?](#q21--что-такое-контрактное-тестирование)
- [Q22. Как работает Consumer-Driven Contract Testing с Pact?](#q22-как-работает-consumer-driven-contract-testing-с-pact)
- [Q23. Как использовать Spring Cloud Contract?](#q23-как-использовать-spring-cloud-contract)
- [Q24. Как тестировать микросервисы в изоляции?](#q24-как-тестировать-микросервисы-в-изоляции)

**Kafka, асинхронность и messaging**
- [Q25. (!) Как тестировать Kafka с Testcontainers?](#q25--как-тестировать-kafka-с-testcontainers)
- [Q26. Как тестировать асинхронные операции?](#q26-как-тестировать-асинхронные-операции)
- [Q27. Как тестировать message-driven архитектуру?](#q27-как-тестировать-message-driven-архитектуру)

**Организация и CI/CD**
- [Q28. Как организовать интеграционные тесты в CI/CD?](#q28-как-организовать-интеграционные-тесты-в-cicd)
- [Q29. Как ускорить интеграционные тесты?](#q29-как-ускорить-интеграционные-тесты)
- [Q30. Как организовать параллельное выполнение тестов?](#q30-как-организовать-параллельное-выполнение-тестов)

**Стабильность и Best Practices**
- [Q31. (!) Как бороться с flaky-тестами?](#q31--как-бороться-с-flaky-тестами)
- [Q32. Что такое Test Fixtures и как их организовать?](#q32-что-такое-test-fixtures-и-как-их-организовать)
- [Q33. Как тестировать кеширование?](#q33-как-тестировать-кеширование)
- [Q34. Что такое Smoke Testing после деплоя?](#q34-что-такое-smoke-testing-после-деплоя)
- [Q35. (!) Какие best practices для интеграционного тестирования?](#q35--какие-best-practices-для-интеграционного-тестирования)

**Продвинутые техники**
- [Q36. (!) Как использовать `@DataJpaTest` для тестирования репозиториев?](#q36--как-использовать-datajpatest-для-тестирования-репозиториев)
- [Q37. Как тестировать JSON-сериализацию с `@JsonTest`?](#q37-как-тестировать-json-сериализацию-с-jsontest)
- [Q38. (!) Как использовать `RestAssured` для интеграционных API-тестов?](#q38--как-использовать-restassured-для-интеграционных-api-тестов)
- [Q39. Как тестировать Spring Security в интеграционных тестах?](#q39-как-тестировать-spring-security-в-интеграционных-тестах)
- [Q40. Как использовать `@RestClientTest` для тестирования HTTP-клиентов?](#q40-как-использовать-restclienttest-для-тестирования-http-клиентов)

---

## Q1. (!) Что такое интеграционное тестирование и чем оно отличается от `unit`-тестирования?

**Интеграционное тестирование** (`integration testing`) -- это уровень тестирования, при котором несколько компонентов объединяются и проверяется корректность их взаимодействия с реальными зависимостями (БД, очереди, HTTP-сервисы).

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

```mermaid
graph TB
    subgraph "Тестовая пирамида"
        E2E["E2E Tests<br/>мало, медленные"]
        INT["Integration Tests<br/>средне, секунды"]
        UNIT["Unit Tests<br/>много, быстрые"]
    end

    E2E --- INT --- UNIT

    subgraph "Integration Testing покрывает"
        DB["База данных"]
        MQ["Очереди сообщений"]
        HTTP["HTTP-сервисы"]
        CACHE["Кеш"]
    end

    INT --> DB
    INT --> MQ
    INT --> HTTP
    INT --> CACHE
```

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

Интеграционные тесты нужны когда: (1) проверяется взаимодействие с БД (SQL-запросы, маппинг); (2) тестируется HTTP-клиент к внешнему сервису; (3) проверяется корректность конфигурации `Spring`; (4) тестируется транзакционная логика.


> [!mcq]
> - [ ] Integration-тест проверяет один класс с замоканными зависимостями через `Mockito.when()` | Это определение unit-теста, а не integration. Integration работает с реальной инфраструктурой. ❌ ПОСЛЕДСТВИЕ: команда называет mock-only тесты «интеграционными», SQL-баги в `@Query` доходят до production — упавший pg-driver не ловится.
> - [ ] Integration-тест поднимает весь кластер сервисов в Kubernetes и гоняет user-flow | Это E2E-тест, а не integration. Integration ограничен одним сервисом + его инфра-зависимостями. ❌ ПОСЛЕДСТВИЕ: CI-pipeline 40 минут на каждый PR, разработчики игнорируют падения, флапы списывают на «инфру».
> - [ ] Integration-тест проверяет UI через Selenium с реальным браузером | Это UI/E2E-тест на верхушке пирамиды. Integration живёт ниже — на уровне backend-компонентов. ❌ ПОСЛЕДСТВИЕ: 200 Selenium-тестов на каждый коммит, p95 времени pipeline 25 минут, regression-feedback запаздывает на полдня.
> - [x] Integration-тест проверяет связку компонентов с реальной БД/брокером через `@SpringBootTest` + Testcontainers | Поднимается часть `ApplicationContext` + Docker-контейнер с Postgres/Kafka, проверяется SQL/маппинг/контракты. ✓ ПРИМЕНЯТЬ: Spring Boot + Testcontainers `PostgreSQLContainer` для тестов JPA-репозиториев и REST-эндпоинтов с реальным драйвером. 📋 ПРАВИЛО: «Integration = реальная инфра в Docker, не моки и не браузер». 🔗 См. Q3, Q9, Q14.

## Q2. Какие типы интеграционного тестирования существуют?

### 1. Big Bang

Все компоненты интегрируются одновременно. Просто в реализации, но трудно локализовать ошибки.

### 2. Top-Down

Начинаем с верхних слоёв (`API`/`UI`) и спускаемся вниз, используя `stub` для нижних уровней. Раннее тестирование пользовательских сценариев.

### 3. Bottom-Up

Начинаем с нижних слоёв (`Repository`, `Service`) и поднимаемся. Нижние уровни проверены первыми, не нужны stub.

### 4. Sandwich / Hybrid

Комбинация `top-down` и `bottom-up`. Параллельное тестирование разных уровней.

### 5. Component Integration (рекомендуемый для Spring)

Тестирование группы связанных компонентов как единого блока. В `Spring Boot` это реализуется через `@SpringBootTest` или test slices (`@WebMvcTest`, `@DataJpaTest`).

```mermaid
graph LR
    subgraph "Component Integration Test"
        C[Controller] --> S[Service]
        S --> R[Repository]
        R --> DB[(PostgreSQL<br/>Testcontainers)]
    end

    subgraph "Mocked"
        EXT[Внешний API<br/>WireMock]
    end

    S --> EXT
```

На собеседовании обычно спрашивают не о классификации, а о практическом подходе: какие слои тестируете вместе, какие мокируете, и почему.


> [!mcq]
> - [ ] Big Bang — собрать всё одновременно — оптимально для крупных Spring-приложений | Big Bang затрудняет локализацию ошибок: упало 5 тестов из 200 — непонятно, где причина. ❌ ПОСЛЕДСТВИЕ: regression в 50K LOC monolith, debugging 3 дня вместо 30 минут.
> - [x] Component Integration рекомендуется для Spring Boot — тестирует группу связанных компонентов через `@SpringBootTest`/test slices | Поднимается узкий срез контекста (Web/JPA/etc.), быстро и точно. ✓ ПРИМЕНЯТЬ: Netflix и LinkedIn гоняют тысячи Component Integration тестов на slice-уровне; Spring Boot test slices экономят 10× время сборки контекста. 📋 ПРАВИЛО: «Component Integration — золотой стандарт Spring Boot». 🔗 См. Q3, Q4, Q6.
> - [ ] Top-Down всегда лучше Bottom-Up для микросервисов | Это ложная универсальность — выбор зависит от того, какие слои уже стабильны. Top-Down требует stub'ов нижних уровней. ❌ ПОСЛЕДСТВИЕ: команда пишет 30 stub'ов вместо реальных сервисов, тесты зелёные а интеграция падает на staging.
> - [ ] Sandwich/Hybrid не существует в Spring | Sandwich — реальная стратегия (комбинация Top-Down и Bottom-Up); Spring Boot test slices фактически реализуют Sandwich. ❌ ПОСЛЕДСТВИЕ: архитектор отказывается от tiered подхода, в monorepo с 80 сервисами все тесты Big Bang, регрессия не находится.

## Q3. (!) Как работает `@SpringBootTest` и когда его использовать?

`@SpringBootTest` поднимает **полный** `ApplicationContext` приложения (или его подмножество). Это самый тяжёлый, но и самый полный вариант интеграционного теста.

### Режимы `webEnvironment`

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

- Для тестирования **только** контроллера -- используйте `@WebMvcTest`
- Для тестирования **только** репозитория -- используйте `@DataJpaTest`
- Для тестирования бизнес-логики без Spring -- обычный `unit`-тест

`@SpringBootTest` поднимает все бины, что медленно. Подробнее о test slices -- в [вопросах по unit-тестированию](unit-testing-interview.md).


> [!mcq]
> - [ ] `@SpringBootTest` без `webEnvironment` поднимает реальный Tomcat на порту 8080 | По умолчанию `webEnvironment = MOCK` — реального HTTP-сервера НЕТ. Для real HTTP нужен `RANDOM_PORT`. ❌ ПОСЛЕДСТВИЕ: `TestRestTemplate` падает с `ConnectException`, разработчик 3 часа ищет причину, пока не находит дефолт `MOCK`.
> - [ ] `webEnvironment = DEFINED_PORT` — стандарт для CI, гарантирует стабильность | `DEFINED_PORT` берёт порт из `application.yml`; в параллельном CI с несколькими тестами получаем конфликт `Address already in use`. ❌ ПОСЛЕДСТВИЕ: Jenkins-агент с 4 параллельными jobs — 3 из 4 интеграционных тестов падают рандомно, флапы списывают на «инфру».
> - [ ] `@SpringBootTest` всегда быстрее `@WebMvcTest` за счёт кеша контекста | Наоборот — `@WebMvcTest` поднимает только web-slice (~1-2 сек), `@SpringBootTest` — весь контекст (5-15 сек). ❌ ПОСЛЕДСТВИЕ: 200 controller-тестов с `@SpringBootTest` собирают суммарно 30 минут вместо 3 минут на slice'ах.
> - [x] `@SpringBootTest(webEnvironment = RANDOM_PORT)` поднимает полный контекст + Tomcat на свободном порту | Реальный HTTP-сервер; `TestRestTemplate`/`WebTestClient` работают через сеть. ✓ ПРИМЕНЯТЬ: `RANDOM_PORT` — стандарт для full-stack тестов в Spring Boot; нет конфликтов в параллельном CI, GitHub Actions/Jenkins matrix builds стабильны. 📋 ПРАВИЛО: «RANDOM_PORT для real HTTP, MOCK для MockMvc». 🔗 См. Q5, Q6, Q8.

## Q4. (!) Что такое `Test Slices` и какие бывают?

**Test Slices** -- аннотации Spring Boot, которые поднимают **только нужный срез** `ApplicationContext`, а не всё приложение. Это значительно ускоряет тесты.

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

Ключевое: `@DataJpaTest` по умолчанию заменяет `DataSource` на `H2`. Чтобы использовать реальную БД через `Testcontainers`, нужно `@AutoConfigureTestDatabase(replace = NONE)`.


> [!mcq]
> - [x] `@DataJpaTest` поднимает только JPA-слой и по умолчанию заменяет `DataSource` на in-memory H2 | Slice-аннотация: `EntityManager`, `@Repository`-бины; H2 заменяется через `@AutoConfigureTestDatabase(replace = NONE)` для Postgres. ✓ ПРИМЕНЯТЬ: тесты JPA-репозиториев в Spring Boot 3 + Testcontainers Postgres — slice стартует за 1-2 сек. 📋 ПРАВИЛО: «slice = только нужный срез контекста, не весь app». 🔗 См. Q14, Q36.
> - [ ] `@WebMvcTest` поднимает все `@Service` и `@Repository` бины автоматически | Наоборот — `@WebMvcTest` поднимает только web-слой (контроллеры, фильтры, advice); сервисы нужно мокать `@MockBean`. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет `@Autowired UserService` в тест, получает `NoSuchBeanDefinitionException`, теряет 2 часа на debug.
> - [ ] Все test slices используют один и тот же `ApplicationContext`-кеш | У каждого slice — свой контекст-cache key (на основе аннотаций); смешение `@WebMvcTest` + `@MockBean(X)` пересоздаёт контекст. ❌ ПОСЛЕДСТВИЕ: 500 тестов с разными `@MockBean` пересоздают контекст 500 раз, CI 45 минут вместо 5.
> - [ ] `@DataJpaTest` всегда работает с production Postgres без дополнительной настройки | По умолчанию `@DataJpaTest` подменяет `DataSource` на embedded H2 — это false-positive: PG-specific SQL не ловится. ❌ ПОСЛЕДСТВИЕ: `JSONB`/`array_agg` зелёные на H2, валятся на проде с `ERROR: function does not exist`.

## Q5. (!) Как тестировать REST-контроллеры через `MockMvc`?

`MockMvc` выполняет HTTP-запросы **без реального сервера** -- запросы идут через `DispatcherServlet` в памяти. Это быстрее, чем поднимать HTTP.

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

`@WebMvcTest` поднимает только web-слой: контроллеры, `@ControllerAdvice`, фильтры, конвертеры. Все зависимости контроллера нужно мокировать через `@MockBean`.


> [!mcq]
> - [x] `MockMvc` через `DispatcherServlet` в памяти + зависимости через `@MockBean` в `@WebMvcTest(UserController.class)` | Slice поднимает только web-слой: контроллер + advice + filters; сервисы мокаются. ✓ ПРИМЕНЯТЬ: Spring REST Docs + MockMvc генерирует API-документацию из тестов; стандарт для controller unit/slice-тестов в Spring Boot 3. 📋 ПРАВИЛО: «MockMvc = в памяти, без HTTP-сокета». 🔗 См. Q3, Q6, Q8.
> - [ ] `MockMvc` отправляет реальные HTTP-запросы через `localhost:8080` | `MockMvc` идёт через `DispatcherServlet` в памяти БЕЗ HTTP-стека; для real HTTP нужен `TestRestTemplate`/`WebTestClient` с `RANDOM_PORT`. ❌ ПОСЛЕДСТВИЕ: команда ставит `Wireshark`, чтобы поймать пакеты от MockMvc — пакетов нет, debugging тупик.
> - [ ] `@WebMvcTest` поднимает реальные `@Service` и `@Repository` зависимости контроллера | Slice мокает зависимости — нужно явно `@MockBean UserService`. ❌ ПОСЛЕДСТВИЕ: тест падает с `UnsatisfiedDependencyException`, разработчик добавляет `@SpringBootTest`, время прогона 50 controller-тестов растёт с 5 сек до 5 минут.
> - [ ] `MockMvc` нельзя использовать с `@SpringBootTest` — только с `@WebMvcTest` | Можно: `@SpringBootTest + @AutoConfigureMockMvc` даёт MockMvc поверх полного контекста. ❌ ПОСЛЕДСТВИЕ: разработчик дублирует тесты в WebMvcTest и SpringBootTest без переиспользования утилит, поддержка test-suite растёт ×2.

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

На собеседовании важно: `@WebMvcTest` -- для юнит-тестов контроллера (моки сервисов). `@SpringBootTest` + `@AutoConfigureMockMvc` -- для интеграционных тестов, где нужна реальная БД и реальные сервисы.


> [!mcq]
> - [ ] `@WebMvcTest` + `MockMvc` использует реальную БД через `Testcontainers` по умолчанию | Slice не поднимает `@DataSource`/JPA — БД нет; для БД нужен `@SpringBootTest`. ❌ ПОСЛЕДСТВИЕ: команда мокает `UserRepository`, тест зелёный, но `@Query` с `JOIN FETCH` падает на проде с `LazyInitializationException`.
> - [ ] `@SpringBootTest` без `@AutoConfigureMockMvc` автоматически даёт `MockMvc` бин | Без `@AutoConfigureMockMvc` (или явной конфигурации) `@Autowired MockMvc` не инжектится. ❌ ПОСЛЕДСТВИЕ: тест падает на старте контекста, junior 2 часа ищет, что забыл аннотацию.
> - [ ] Оба подхода поднимают одинаковый объём контекста — разница только в синтаксисе | `@WebMvcTest` — slice (~1 сек, web-only); `@SpringBootTest` — full context (5-15 сек). ❌ ПОСЛЕДСТВИЕ: 500 controller-тестов с `@SpringBootTest` собирают 1.5 часа в CI; миграция на `@WebMvcTest` даёт время 5 минут.
> - [x] `@WebMvcTest` — slice с моками, `@SpringBootTest + @AutoConfigureMockMvc` — full context с реальными бинами и БД | Первый для controller-unit, второй для full e2e через MockMvc. ✓ ПРИМЕНЯТЬ: layered подход — `@WebMvcTest` для логики controller'а (валидация, маппинг), `@SpringBootTest` для критичных user flows с реальной БД. 📋 ПРАВИЛО: «slice — для логики controller, full — для сквозного flow». 🔗 См. Q3, Q4, Q5.

## Q7. Как использовать `WebTestClient` для реактивных и блокирующих приложений?

`WebTestClient` -- клиент из `Spring WebFlux` для тестирования HTTP-эндпоинтов. Работает с реактивными и с обычными `Spring MVC` приложениями (начиная с Spring Boot 2.4+).

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

Преимущество `WebTestClient` перед `TestRestTemplate`: fluent API, поддержка реактивных типов (`Mono`, `Flux`), лучшая поддержка streaming. Подробнее о реактивном тестировании -- в [вопросах по Spring WebFlux](../frameworks/spring/spring-webflux-interview.md).


> [!mcq]
> - [ ] `WebTestClient` работает только с `WebFlux` приложениями, для MVC нужен `TestRestTemplate` | С Spring Boot 2.4+ `WebTestClient` работает и с MVC через `RANDOM_PORT`. ❌ ПОСЛЕДСТВИЕ: команда дублирует тесты на двух клиентах при миграции с MVC на WebFlux, 200 тестов переписывается заново.
> - [x] `WebTestClient` через `@SpringBootTest(webEnvironment = RANDOM_PORT)` работает и с MVC, и с WebFlux | Fluent API: `.exchange().expectStatus().isOk().expectBody().jsonPath(...)`; bound к real server или к `WebTestClient.bindToController()`. ✓ ПРИМЕНЯТЬ: миграция с `RestAssured` на `WebTestClient` в Spring Boot 3 — единый клиент для MVC и Reactive стэков. 📋 ПРАВИЛО: «WebTestClient = универсальный fluent client для HTTP-тестов». 🔗 См. Q5, Q8, Q38.
> - [ ] `WebTestClient.exchange()` блокирует поток в reactive-режиме | `.exchange()` возвращает `Mono`/блокирует только для finalizer'ов (`.expectStatus()`) — это нормально для тестов. ❌ ПОСЛЕДСТВИЕ: разработчик пишет custom non-blocking wrapper, тесты усложняются, никто не понимает поток ассертов.
> - [ ] `WebTestClient` не поддерживает `bodyValue()` — только `body(BodyInserters.fromValue())` | `bodyValue(obj)` — стандартное API с Spring 5.2+; `BodyInserters` — legacy. ❌ ПОСЛЕДСТВИЕ: код-ревью гоняет PR туда-обратно из-за устаревшего синтаксиса, время merge'а в 2× больше.

## Q8. Как тестировать REST API через `TestRestTemplate`?

`TestRestTemplate` -- обёртка над `RestTemplate` для тестов с реальным HTTP-сервером (требует `RANDOM_PORT` или `DEFINED_PORT`).

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

`TestRestTemplate` не бросает исключения при 4xx/5xx (в отличие от обычного `RestTemplate`), что удобно для проверки ошибочных сценариев.


> [!mcq]
> - [ ] `TestRestTemplate` бросает исключение на 5xx и 4xx ответах | Наоборот — он не бросает на 4xx/5xx, возвращает `ResponseEntity` с любым статусом; это удобно для тестирования error-сценариев. ❌ ПОСЛЕДСТВИЕ: разработчик оборачивает все вызовы в try/catch ожидая исключения, тесты на 404 выдают false-negative.
> - [ ] `TestRestTemplate` использует те же interceptor'ы, что и production `RestTemplate` | Это отдельный бин, конфигурация по умолчанию минимальная (без auth, без metrics); нужно добавлять явно. ❌ ПОСЛЕДСТВИЕ: production-RestTemplate с OAuth2-interceptor работает, тестовый — нет, тесты пропускают баг с auth-header.
> - [x] `TestRestTemplate` инжектится с `@SpringBootTest(webEnvironment = RANDOM_PORT)` и работает поверх real HTTP | Auto-configured бин с base URL = random port; вызовы идут через сеть к реальному Tomcat. ✓ ПРИМЕНЯТЬ: e2e-тесты в Spring Boot для REST-эндпоинтов с реальной БД через Testcontainers — стандарт до WebTestClient. 📋 ПРАВИЛО: «TestRestTemplate = real HTTP, не бросает на 4xx/5xx». 🔗 См. Q3, Q5, Q7.
> - [ ] `TestRestTemplate` нельзя использовать с `BasicAuth` — только `RestTemplate` | `.withBasicAuth(user, pass)` — встроенный метод; для тестов с Security удобный API. ❌ ПОСЛЕДСТВИЕ: команда пишет custom HTTP-клиент с Apache HttpClient вместо встроенного API, поддержка раздваивается.

## Q9. (!) Что такое `Testcontainers` и зачем он нужен?

`Testcontainers` -- Java-библиотека для запуска `Docker`-контейнеров в `JUnit`-тестах. Позволяет тестировать с реальными базами данных, очередями и другими сервисами вместо in-memory заменителей.

```mermaid
graph TB
    subgraph "JUnit Test"
        TEST[Тестовый класс]
    end

    subgraph "Testcontainers управляет"
        TC[Testcontainers Library]
        TC --> PG[(PostgreSQL)]
        TC --> RD[(Redis)]
        TC --> KF[(Kafka)]
    end

    subgraph "Docker"
        PG
        RD
        KF
    end

    TEST --> TC
    TEST -- "@DynamicPropertySource" --> PG
    TEST -- "@DynamicPropertySource" --> RD

    style TC fill:#4a9,stroke:#333
```

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


> [!mcq]
> - [x] `Testcontainers` запускает Docker-контейнеры (Postgres, Kafka, Redis) для тестов с реальной инфраструктурой | Java-API над Docker; контейнер стартует в `@BeforeAll`, останавливается в `@AfterAll`; решает проблему false-positives на H2. ✓ ПРИМЕНЯТЬ: Spring Boot 3 + `@ServiceConnection` + `PostgreSQLContainer` — стандарт интеграционных тестов на Postgres-specific фичах (`JSONB`, `array_agg`). 📋 ПРАВИЛО: «Testcontainers = real infra в Docker = production-parity». 🔗 См. Q10, Q11, Q14.
> - [ ] `Testcontainers` запускает Docker-in-Docker — нужен privileged-mode в CI | Стандартно используется host-Docker через mounted socket (`/var/run/docker.sock`); DinD — частный случай. ❌ ПОСЛЕДСТВИЕ: команда настраивает privileged Jenkins-агенты, создавая security-дыру (escape из контейнера к host).
> - [ ] `Testcontainers` не работает с Postgres — только с MySQL и MongoDB | Postgres — самый популярный модуль (`PostgreSQLContainer`); поддержка >50 модулей. ❌ ПОСЛЕДСТВИЕ: команда мигрирует на Embedded Postgres (медленный, не Docker), теряет parity с production.
> - [ ] `Testcontainers` подменяет `DataSource` через bytecode-magic во время старта | Подключение явное — через `@DynamicPropertySource` или `@ServiceConnection` (Spring Boot 3.1+). ❌ ПОСЛЕДСТВИЕ: разработчик не понимает, как подмешать URL — забывает `@DynamicPropertySource`, тест падает с `ConnectionRefused` на default URL.

## Q10. Как подключить `Testcontainers` через `@DynamicPropertySource`?

`@DynamicPropertySource` позволяет динамически задать `Spring`-свойства **после** старта контейнера (порт и хост неизвестны заранее).

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

Правила: метод с `@DynamicPropertySource` должен быть `static`; контейнер должен быть запущен **до** вызова метода (аннотация `@Container` + `static` поле гарантирует это).


> [!mcq]
> - [ ] `@DynamicPropertySource` метод должен быть instance-level (не static) | Должен быть `static` — Spring читает свойства до создания инстанса теста. ❌ ПОСЛЕДСТВИЕ: тест падает на `ApplicationContextInitializationException`, разработчик 1 час ищет проблему в Docker-конфиге.
> - [ ] `@DynamicPropertySource` принимает `Map<String,String>` напрямую | Принимает `DynamicPropertyRegistry` и регистрирует `Supplier`-ы — ленивая инициализация после старта контейнера. ❌ ПОСЛЕДСТВИЕ: hardcoded URL `jdbc:postgresql://localhost:5432`, тест работает локально, падает в CI с другим маппингом портов.
> - [x] `@DynamicPropertySource static void props(DynamicPropertyRegistry r) { r.add("spring.datasource.url", postgres::getJdbcUrl); }` | Static-метод; supplier'ы вызываются после старта контейнера, получают правильный URL и random-порт. ✓ ПРИМЕНЯТЬ: Spring Boot 2.5+ стандарт для Testcontainers до появления `@ServiceConnection`; работает с любыми контейнерами. 📋 ПРАВИЛО: «static + Supplier = ленивая привязка после старта Docker». 🔗 См. Q9, Q11, Q14.
> - [ ] `@DynamicPropertySource` работает только с `@SpringBootTest`, не работает в slice-тестах | Работает в любом тесте с `@TestPropertySource`/Spring TestContext — включая `@DataJpaTest`, `@WebMvcTest`. ❌ ПОСЛЕДСТВИЕ: команда дублирует Postgres-конфиг в каждом slice вручную, поддержка 200 тестов превращается в кошмар.

## Q11. (!) Что такое `@ServiceConnection` в Spring Boot 3.1+?

Начиная с Spring Boot 3.1, появилась аннотация `@ServiceConnection`, которая **автоматически** конфигурирует свойства подключения к контейнеру без ручного `@DynamicPropertySource`.

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

`@ServiceConnection` работает через `ConnectionDetails` SPI и поддерживает: `PostgreSQL`, `MySQL`, `MariaDB`, `MongoDB`, `Redis`, `Kafka`, `RabbitMQ`, `Elasticsearch`, `Cassandra` и другие.

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

`@TestConfiguration` с `@ServiceConnection` позволяет определить контейнеры в отдельном классе и использовать их при локальном запуске приложения:

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


> [!mcq]
> - [ ] `@ServiceConnection` доступен только для Postgres, остальные контейнеры через `@DynamicPropertySource` | Поддержка >20 модулей: Postgres, MongoDB, Redis, Kafka, RabbitMQ, Neo4j. ❌ ПОСЛЕДСТВИЕ: команда смешивает оба подхода без причины, code-review ловит inconsistency.
> - [ ] `@ServiceConnection` появился в Spring Boot 2.7 | Появился в Spring Boot 3.1+; в 2.x — только `@DynamicPropertySource`. ❌ ПОСЛЕДСТВИЕ: команда на 2.7 копирует пример из туториала, тест падает на старте с unknown-annotation.
> - [ ] `@ServiceConnection` требует ручной регистрации `ConnectionDetails` через `@Bean` | Auto-configured — Spring Boot сам сопоставляет тип контейнера с `JdbcConnectionDetails`/`KafkaConnectionDetails`. ❌ ПОСЛЕДСТВИЕ: разработчик пишет 50 строк boilerplate-конфига, code-review требует упрощения.
> - [x] `@ServiceConnection` на `@Container` поле автоматически подмешивает URL/credentials в Spring Boot 3.1+ | Заменяет boilerplate `@DynamicPropertySource` для известных модулей; работает через `ConnectionDetails`-абстракцию. ✓ ПРИМЕНЯТЬ: новый стандарт Spring Boot 3.1+ для `PostgreSQLContainer`, `KafkaContainer`, `RedisContainer` — 1 строка вместо 5. 📋 ПРАВИЛО: «`@ServiceConnection` = zero-config Testcontainers в Spring Boot 3.1+». 🔗 См. Q9, Q10, Q14.

## Q12. Как переиспользовать контейнеры между тестовыми классами?

По умолчанию каждый тестовый класс с `@Container` запускает **новый** контейнер. Это медленно. Решения:

### 1. Singleton-паттерн (абстрактный базовый класс)

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

`withReuse(true)` -- контейнер остаётся после завершения тестов и переиспользуется при следующем запуске. Полезно для локальной разработки, но **не рекомендуется** для CI (риск грязного состояния).


> [!mcq]
> - [ ] Использовать `@Container` (instance-level) — контейнер пересоздаётся между тестами | Это и есть проблема: на 200 тестах = 200 стартов Postgres × 5 сек = 17 минут оверхеда. ❌ ПОСЛЕДСТВИЕ: CI-time на интеграционных тестах растёт с 5 до 30 минут, разработчики ждут feedback и переключаются на другие задачи.
> - [ ] Singleton container в `static` поле + manual `.close()` в shutdown hook | Нужен `withReuse(true)` + `~/.testcontainers.properties` с `testcontainers.reuse.enable=true`; manual close ломает reuse. ❌ ПОСЛЕДСТВИЕ: контейнер закрывается между классами, reuse не работает, время сборки не уменьшается.
> - [x] `static` контейнер + `.withReuse(true)` + `testcontainers.reuse.enable=true` в `~/.testcontainers.properties` | Контейнер живёт между запусками gradle/maven (не убивается после теста), Testcontainers находит существующий по `hash(image+config)`. ✓ ПРИМЕНЯТЬ: локально reuse экономит 5-10 сек на старт каждого test-run; в CI обычно отключают (чистый state на каждый build). 📋 ПРАВИЛО: «reuse = static + withReuse(true) + opt-in flag в home». 🔗 См. Q9, Q10, Q29.
> - [ ] `@TestInstance(Lifecycle.PER_CLASS)` сам по себе включает reuse контейнеров | Управляет lifecycle JUnit-инстанса теста, не Testcontainers. Reuse — отдельный механизм. ❌ ПОСЛЕДСТВИЕ: разработчик меняет lifecycle, ожидает экономии — ничего не происходит, тест-suite по-прежнему медленный.

## Q13. Как тестировать с несколькими контейнерами одновременно?

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

При нескольких контейнерах используйте `@ServiceConnection` где возможно, а `@DynamicPropertySource` -- для контейнеров без автоматической поддержки.


> [!mcq]
> - [ ] Контейнеры стартуют в произвольном порядке — порядок не гарантируется | `@Container` поля стартуют в порядке объявления (для static полей); зависимости можно через `dependsOn()` или `Network`. ❌ ПОСЛЕДСТВИЕ: Kafka стартует до Zookeeper, тест падает на startup.
> - [ ] Все контейнеры обязаны быть в одной `Network` для коммуникации с тестом | Тест видит контейнеры через mapped-port на host; общая `Network` нужна только для inter-container связей (Kafka↔Zookeeper). ❌ ПОСЛЕДСТВИЕ: разработчик создаёт Network для simple Postgres-теста, тест падает с DNS-resolution issues.
> - [x] Несколько `static @Container` полей + общий `Network.newNetwork()` для inter-container связей | Например, Kafka+Postgres+WireMock; `Network` гарантирует, что контейнеры видят друг друга по DNS-имени. ✓ ПРИМЕНЯТЬ: e2e-тесты Saga-flow с Kafka для брокера + Postgres для outbox + WireMock для downstream. 📋 ПРАВИЛО: «много контейнеров = static + общий Network для inter-container DNS». 🔗 См. Q9, Q12, Q25.
> - [ ] `@Testcontainers` ограничен 1 контейнером на тест-класс | Никаких ограничений — можно объявлять любое число `@Container` полей. ❌ ПОСЛЕДСТВИЕ: команда дробит интеграционный тест на 5 классов вместо одного, теряет cohesion и кеш контекста.

## Q14. (!) Как тестировать с реальной базой данных?

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


> [!mcq]
> - [ ] Использовать H2 in-memory с PostgreSQL-compatibility mode | H2 имитирует не все фичи: `JSONB`, `array_agg`, `LATERAL JOIN`, оконные функции работают по-разному. ❌ ПОСЛЕДСТВИЕ: `@Query("SELECT * FROM users WHERE data->>'email' = :email")` зелёный на H2, падает на проде с `function does not exist`.
> - [x] Testcontainers с тем же образом, что и production (`postgres:16` если на проде 16) | Production-parity: то же поведение, те же extension'ы, тот же SQL-диалект. ✓ ПРИМЕНЯТЬ: Wolt и Booking.com гоняют интеграционные тесты на образе Postgres из production registry — нет surprises на release. 📋 ПРАВИЛО: «образ теста = образ production». 🔗 См. Q9, Q10, Q36.
> - [ ] Embedded Postgres (`zonkyio/embedded-postgres`) — лучшая альтернатива Testcontainers | Embedded Postgres — отдельный binary, не Docker; работает, но устаревает; Testcontainers — стандарт. ❌ ПОСЛЕДСТВИЕ: команда поддерживает legacy на embedded, новые тесты — на Testcontainers, два подхода в одном репо.
> - [ ] Shared dev-database с `@Sql` cleanup между тестами | Shared DB ломает изоляцию: параллельные тесты конфликтуют по данным; cleanup может пропустить FK. ❌ ПОСЛЕДСТВИЕ: 4 параллельных PR-теста ломают друг другу данные, флапы 30%+ в Jenkins.

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

Ключевое: если тестовый метод **сам** помечен `@Transactional`, Spring делает rollback после теста -- это удобно для изоляции, но скрывает баги (см. Q17).


> [!mcq]
> - [ ] `@Transactional` на тесте автоматически даёт изоляцию между параллельными тестами | `@Transactional` rollback'ит изменения, но не изолирует от concurrent writers; для параллельности нужны разные DB или схемы. ❌ ПОСЛЕДСТВИЕ: parallel-runner запускает 4 теста одновременно, deadlock на одной таблице, 50% флапов.
> - [x] `@Transactional` на тест-методе откатывает изменения после теста (default rollback = true) | Spring Test обёртывает тест в транзакцию и rollback'ит в конце; быстрее, чем DELETE/TRUNCATE. ✓ ПРИМЕНЯТЬ: тесты JPA-репозиториев — `@DataJpaTest` уже включает `@Transactional` по умолчанию; стандарт Spring Boot. 📋 ПРАВИЛО: «`@Transactional` в тесте = auto-rollback в конце». 🔗 См. Q17, Q36.
> - [ ] Default rollback можно отключить только через `@Rollback(false)` на классе | Можно через `@Rollback(false)` на методе/классе или через `@Commit`; оба варианта работают. ❌ ПОСЛЕДСТВИЕ: разработчик копирует пример с `@Commit`, не понимает разницу с `@Rollback(false)`, code-review гоняет.
> - [ ] `@Transactional` тест видит изменения, сделанные в `@Async`-методе | `@Async` запускает в другом потоке = другая транзакция; тест видит только commited данные. ❌ ПОСЛЕДСТВИЕ: тест на event-listener зелёный (rollback скрывает баг), на проде событие теряется.

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

Рекомендация: `Flyway` для схемы, `@Sql` для тестовых данных. Не мешайте тестовые данные в production-миграции.


> [!mcq]
> - [ ] `@Sql("/data.sql")` запускает скрипт после каждого теста | По умолчанию `@Sql` запускает скрипт ДО теста (`ExecutionPhase.BEFORE_TEST_METHOD`). ❌ ПОСЛЕДСТВИЕ: разработчик ожидает cleanup, получает накопление данных, тесты ломаются на 5-м запуске.
> - [ ] Flyway-миграции отключены автоматически в `@SpringBootTest` | Flyway применяет миграции на старт контекста — это и нужно для тестов; отключение требует `spring.flyway.enabled=false`. ❌ ПОСЛЕДСТВИЕ: тест работает на пустой БД, схема не создаётся, все запросы падают с `relation does not exist`.
> - [x] Flyway применяет migrations на старт контекста + `@Sql("/test-data.sql")` подмешивает фикстуры на тест | Flyway создаёт схему как на проде, `@Sql` готовит тестовые данные на конкретный сценарий. ✓ ПРИМЕНЯТЬ: production-parity схема через Flyway + точечные фикстуры через `@Sql` — стандарт Spring Boot интеграционных тестов. 📋 ПРАВИЛО: «Flyway для схемы, `@Sql` для данных». 🔗 См. Q14, Q15, Q32.
> - [ ] `@Sql` поддерживает только plain SQL, не работает с `INSERT...SELECT` | `@Sql` запускает любой SQL, поддерживаемый драйвером (включая `INSERT...SELECT`, CTE, hooks). ❌ ПОСЛЕДСТВИЕ: команда пишет данные через repositories.save() в `@BeforeEach` вместо `@Sql`, тест-suite разрастается.

## Q17. В чём опасность `@Transactional` на интеграционных тестах?

`@Transactional` на тесте делает rollback после каждого теста, что обеспечивает изоляцию. Но это **скрывает реальное поведение**:

1. **Lazy loading** -- в тесте всё работает (одна транзакция), а в production -- `LazyInitializationException`
2. **Flush** -- данные могут не достигнуть БД (нет `flush` до rollback), и SQL-ошибки не проявятся
3. **Транзакционные границы** -- `@Transactional(propagation = REQUIRES_NEW)` внутри сервиса не тестируется корректно

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

На собеседовании этот вопрос показывает глубокое понимание. Рекомендация: для интеграционных тестов **не** ставить `@Transactional` на тестовый метод; вместо этого -- очистка данных в `@AfterEach` или использование `Testcontainers` с чистым контейнером.


> [!mcq]
> - [ ] `@Transactional` всегда улучшает изоляцию тестов — нет причин не использовать | Скрывает баги: `@Async`/`@TransactionalEventListener` не отрабатывает, partial-commit'ы не видны, Hibernate `flush` может не срабатывать. ❌ ПОСЛЕДСТВИЕ: production-баг в `@TransactionalEventListener(AFTER_COMMIT)` не ловится в тестах с `@Transactional` — события не публикуются после rollback.
> - [ ] `@Transactional` на тесте включает Hibernate auto-flush для всех операций | Это ровно проблема: тест не видит реальные SQL до flush, реальный execution plan скрыт от теста. ❌ ПОСЛЕДСТВИЕ: тест зелёный, но в проде из-за `cascade=ALL` падает FK-violation, который скрывался Hibernate'ом в первой транзакции.
> - [x] `@Transactional` на тесте скрывает реальный flush, AFTER_COMMIT-listener'ы и auto-rollback на checked exceptions | Для критичных flow используют `@Transactional(propagation = NOT_SUPPORTED)` или ручной cleanup. ✓ ПРИМЕНЯТЬ: тесты outbox-pattern и event-listener'ов в Wolt/Booking — без `@Transactional` на тесте, явный TRUNCATE в `@AfterEach`. 📋 ПРАВИЛО: «`@Transactional` на тесте = ложная безопасность для AFTER_COMMIT флоу». 🔗 См. Q15, Q16.
> - [ ] `@Transactional` гарантирует, что каждый тест видит свежий `EntityManager` | EM связан с транзакцией; new EM на каждый тест — да, но изменения видны через 1st-level cache, не через DB. ❌ ПОСЛЕДСТВИЕ: тест видит данные через persistence context, реальный SQL запрос не выполняется, cache hit маскирует баг в `@Query`.

## Q18. (!) Как использовать `WireMock` для мокирования HTTP-сервисов?

`WireMock` -- инструмент для мокирования HTTP API. Spring Boot Cloud Contract включает `@AutoConfigureWireMock` для автоматической настройки.

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

```mermaid
graph LR
    TEST[Тест] --> SVC[PaymentClient]
    SVC --> WM[WireMock<br/>localhost:random_port]
    WM -- "stub response" --> SVC

    style WM fill:#f96,stroke:#333
```


> [!mcq]
> - [ ] WireMock подменяет HTTP-клиент через bytecode-инструментацию | WireMock — отдельный HTTP-сервер на random-порту; клиент шлёт реальные HTTP-запросы на `localhost:port`. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает interceptor-magic, удивляется DNS-resolution issues, теряет 2 часа.
> - [x] WireMock запускает stub-HTTP-сервер; URL внешнего сервиса подменяется через property на `http://localhost:wiremockPort` | `WireMockExtension` или `WireMockContainer` (Testcontainers); stub'ы через `stubFor(get(...).willReturn(...))`. ✓ ПРИМЕНЯТЬ: интеграционные тесты HTTP-клиентов без зависимости от downstream-сервиса; стандарт в Booking.com и Yandex Lavka. 📋 ПРАВИЛО: «WireMock = реальный HTTP-сервер с mock-ответами». 🔗 См. Q19, Q20, Q40.
> - [ ] WireMock работает только с Java-клиентами, не с reactive `WebClient` | Работает с любым HTTP-клиентом — WireMock не знает о клиенте, отдаёт HTTP-ответ. ❌ ПОСЛЕДСТВИЕ: команда мигрирует с RestTemplate на WebClient, переписывает все WireMock-stub'ы заново «на всякий случай».
> - [ ] WireMock-stub'ы статичны и не поддерживают динамический response | `transformers` и `ResponseTemplating` дают динамические ответы (current time, request body parts). ❌ ПОСЛЕДСТВИЕ: тест с timestamp в response пишется через `@RegisterExtension` с custom-stub'ом, 30 строк boilerplate.

## Q19. В чём разница между `@MockBean` и `@SpyBean`?

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

Важно: `@MockBean` и `@SpyBean` **инвалидируют** `ApplicationContext` кеш Spring. Если разные тесты мокируют разные бины, Spring перезапускает контекст для каждого, что замедляет тесты. Решение: группировать тесты с одинаковым набором моков или использовать `@MockitoBean` (Spring Boot 3.4+).


> [!mcq]
> - [x] `@MockBean` заменяет бин полным mock'ом (Mockito), `@SpyBean` оборачивает реальный бин с возможностью stub'а | Mock — нет real behavior; Spy — real-by-default + selective stub. ✓ ПРИМЕНЯТЬ: `@SpyBean` для частичной подмены — например, real `OrderService` + stub только `paymentClient.charge()` для проверки compensation. 📋 ПРАВИЛО: «Mock — всё false, Spy — real + точечный stub». 🔗 См. Q5, Q18, Q40.
> - [ ] `@MockBean` и `@SpyBean` идентичны — выбор стилистический | Радикальная разница: Mock сбрасывает real behavior (return null/0/false), Spy сохраняет. ❌ ПОСЛЕДСТВИЕ: `@MockBean SecurityFilterChain` без stub'а — все security-тесты пропускают auth, возвращая null; production-баг с дырой в Spring Security проходит код-ревью.
> - [ ] `@MockBean` сохраняет original-implementation для непрописанных методов | Это поведение `@SpyBean`. У `@MockBean` непрописанные методы возвращают null/0/false. ❌ ПОСЛЕДСТВИЕ: `@MockBean UserRepository` без stub'а `findById` возвращает null, сервис падает с NPE — разработчик 2 часа ищет баг.
> - [ ] `@SpyBean` нельзя использовать с `final`-классами без mockito-inline | Spring Boot 2.5+ автоматически подключает mockito-inline; final mock'ается из коробки. ❌ ПОСЛЕДСТВИЕ: команда переписывает финальные классы как non-final ради тестов, нарушая инкапсуляцию.

## Q20. Как тестировать с `WireMock`: сценарии ошибок и задержки?

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

WireMock Scenarios позволяют моделировать stateful-поведение: первый запрос отвечает одним образом, второй -- другим. Полезно для тестирования retry-логики и circuit breaker.


> [!mcq]
> - [ ] Тестировать timeout через `Thread.sleep()` в реальном клиенте | Sleep удлиняет тест, не имитирует server-delay; нужно `withFixedDelay(ms)` в WireMock-stub. ❌ ПОСЛЕДСТВИЕ: тест занимает реальные 5 сек на каждый retry, suite растёт до часа в CI.
> - [ ] Имитировать 503 через ручной mock — WireMock возвращает только 200 | WireMock возвращает любой статус: `aResponse().withStatus(503)`. ❌ ПОСЛЕДСТВИЕ: команда не тестирует Resilience4j circuit breaker на 503-ответах, в проде circuit не открывается, каскадный отказ.
> - [x] `stubFor(get("/api").willReturn(aResponse().withStatus(503).withFixedDelay(2000)))` для 503 + 2s delay | WireMock умеет статусы, delays, scenarios (state machine) — полная имитация failure-modes. ✓ ПРИМЕНЯТЬ: тесты Resilience4j circuit breaker, retry, bulkhead — Wolt и Yandex Lavka гоняют сценарии 503/timeout/connection-reset через WireMock. 📋 ПРАВИЛО: «WireMock умеет всё: status, delay, scenarios, fault-injection». 🔗 См. Q18, Q19, Q31.
> - [ ] WireMock не поддерживает ConnectionReset — нужен Toxiproxy | `Fault.CONNECTION_RESET_BY_PEER` встроен; Toxiproxy для более сложных сетевых сбоев. ❌ ПОСЛЕДСТВИЕ: команда настраивает Toxiproxy для простого reset-теста, добавляя ещё один контейнер в CI.

## Q21. (!) Что такое контрактное тестирование?

**Контрактное тестирование** (`Contract Testing`) -- подход, при котором проверяется соблюдение контрактов (интерфейсов) между сервисами. Consumer определяет ожидания, Provider верифицирует соответствие.

```mermaid
graph LR
    subgraph "Consumer (OrderService)"
        CT[Consumer Test]
        CT -- "генерирует" --> PACT[Pact-файл]
    end

    subgraph "Provider (UserService)"
        PT[Provider Test]
        PACT -- "верифицирует" --> PT
    end

    subgraph "Pact Broker"
        PB[Pact Broker]
    end

    PACT --> PB
    PB --> PT
```

### Зачем нужно

- **Раннее обнаружение** breaking changes до деплоя
- **Независимость** команд: не нужен общий staging для проверки интеграции
- **Документация**: контракт -- актуальная спецификация API
- **Скорость**: быстрее E2E-тестов

### Два основных инструмента

1. **Pact** -- language-agnostic, consumer-driven, JSON-based
2. **Spring Cloud Contract** -- Spring-экосистема, Groovy/YAML DSL, генерация тестов

Подробнее о микросервисной архитектуре -- в [вопросах по микросервисам](../architecture/microservices-interview.md).


> [!mcq]
> - [ ] Контрактное тестирование = e2e-тесты двух микросервисов с реальной сетью | Это integration/e2e, а не contract testing. Contract testing работает с описанием API без поднятия обоих сервисов вместе. ❌ ПОСЛЕДСТВИЕ: команда поднимает 30 сервисов в Kubernetes для contract-проверки, CI 1.5 часа, никто не запускает локально.
> - [x] Contract testing проверяет совместимость API между producer и consumer на основе общего контракта (Pact, Spring Cloud Contract) | Producer и consumer тестируются независимо против одного и того же контракта; сломанный контракт ловится в CI до deployment. ✓ ПРИМЕНЯТЬ: Wolt и Booking.com гоняют Pact-broker между десятками микросервисов; contract failure блокирует deploy producer'а до согласования. 📋 ПРАВИЛО: «Contract — общий язык producer↔consumer без e2e». 🔗 См. Q22, Q23, Q24.
> - [ ] Contract testing нужен только для GraphQL API | Подходит для любых API: REST, GraphQL, gRPC, messaging (Kafka). ❌ ПОСЛЕДСТВИЕ: команда на REST решает, что contract testing им не нужен, breaking-change в response ломает 5 consumer'ов в production.
> - [ ] Contract testing заменяет integration tests полностью | Не заменяет — дополняет: contract проверяет схему API, integration — поведение с инфраструктурой. ❌ ПОСЛЕДСТВИЕ: команда удаляет integration tests, миграция БД ломает SQL-запросы — contract test не ловит, проблема в проде.

## Q22. Как работает Consumer-Driven Contract Testing с `Pact`?

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

Consumer генерирует Pact-файл (JSON); Provider верифицирует, что его API соответствует контракту. Файлы хранятся в Pact Broker или в репозитории.


> [!mcq]
> - [ ] Producer пишет контракт, consumer проверяет своё API против него | Наоборот — Consumer-Driven значит consumer определяет ожидания, producer обязан удовлетворять. ❌ ПОСЛЕДСТВИЕ: producer добавляет breaking-change, consumer ломается в production — CDCT не срабатывает, тест-стратегия неверна.
> - [x] Consumer пишет ожидания (Pact-файл), publish'ит в Pact Broker; producer верифицирует свой API против всех Pact-файлов consumer'ов | Pull-model: producer не знает о consumer'ах, broker агрегирует контракты. ✓ ПРИМЕНЯТЬ: Pact-broker в Pactflow.io используют Atlassian, Spotify, IBM для координации десятков команд микросервисов. 📋 ПРАВИЛО: «Consumer определяет, producer верифицирует — pull через broker». 🔗 См. Q21, Q23, Q24.
> - [ ] Pact-файлы хранятся в Git вместе с кодом consumer'а | Можно, но broker — стандарт: централизованный хаб, версионирование, can-i-deploy gate. ❌ ПОСЛЕДСТВИЕ: 50 Pact-файлов разбросаны по 50 репо, producer не знает кто его consumer, breaking-changes пропускаются.
> - [ ] Pact работает только с REST, не с messaging | Pact поддерживает messaging-контракты (Kafka, RabbitMQ) с Pact V3+. ❌ ПОСЛЕДСТВИЕ: команда на event-driven архитектуре отказывается от contract testing, schema-evolution ломает event consumer'ов.

## Q23. Как использовать `Spring Cloud Contract`?

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

Spring Cloud Contract генерирует WireMock-стабы из контрактов и публикует их как Maven-артефакт. Consumer скачивает стабы и тестирует свой клиент. Подробнее о Spring Cloud -- в [вопросах по Spring Cloud](../frameworks/spring/spring-cloud-interview.md).


> [!mcq]
> - [ ] Spring Cloud Contract — это Pact с другим именем | Разные подходы: Pact = Consumer-Driven (consumer пишет первый); SCC = Producer-Driven (producer пишет Groovy/YAML контракт, генерирует stub'ы). ❌ ПОСЛЕДСТВИЕ: команда смешивает оба, конфликты broker'ов, никто не понимает source of truth.
> - [ ] SCC требует обязательную интеграцию с Kafka — REST не поддерживается | SCC исторически начался с REST, поддерживает и messaging (Kafka, RabbitMQ). ❌ ПОСЛЕДСТВИЕ: REST-команда отказывается от SCC, выбирает Pact, монорепо с двумя contract-инструментами.
> - [x] SCC — Producer-Driven: producer пишет Groovy/YAML контракты, генерирует WireMock-stub'ы для consumer'ов | Stub'ы публикуются в Maven/Nexus, consumer тянет нужную версию через `@AutoConfigureStubRunner`. ✓ ПРИМЕНЯТЬ: Spring-команды (Pivotal/VMware) используют SCC; стандарт в Spring-экосистеме, тесная интеграция с `WireMock` и `@AutoConfigureStubRunner`. 📋 ПРАВИЛО: «SCC = Producer-Driven, Pact = Consumer-Driven». 🔗 См. Q21, Q22, Q24.
> - [ ] SCC-контракты пишутся только на Java | Контракты пишутся на Groovy DSL или YAML; Java — отдельная история (Java DSL появился позже). ❌ ПОСЛЕДСТВИЕ: команда без знания Groovy отказывается от SCC, выбирает Pact, теряет интеграцию со Spring Boot stub-runner.

## Q24. Как тестировать микросервисы в изоляции?

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

Стратегия для микросервисов:
1. **Unit-тесты** -- бизнес-логика без Spring
2. **Component tests** -- один сервис + его БД, моки для других сервисов
3. **Contract tests** -- проверка API-контрактов между сервисами
4. **E2E** -- минимальное количество, критичные пути


> [!mcq]
> - [ ] Поднимать всю топологию микросервисов в Kubernetes для каждого PR | E2E на каждый PR = 30+ мин CI; не масштабируется на >10 сервисов. ❌ ПОСЛЕДСТВИЕ: Netflix/Booking отказались от полного e2e на PR-уровне; команды копировали этот антипаттерн, CI 1.5 часа.
> - [x] Изолированные интеграционные тесты сервиса + WireMock для downstream + Contract testing для совместимости API | Каждый сервис тестируется отдельно с моками внешних зависимостей; контракт гарантирует совместимость на уровне API. ✓ ПРИМЕНЯТЬ: Netflix Hystrix-эра, Booking.com microservices testing pyramid — изоляция + contract вместо full e2e. 📋 ПРАВИЛО: «изоляция + WireMock + contract = быстрая обратная связь». 🔗 См. Q18, Q21, Q22.
> - [ ] Один общий staging-environment с реальными сервисами заменяет contract testing | Staging — медленный feedback (1 PR = 1 deploy + smoke), contract testing даёт feedback за минуты. ❌ ПОСЛЕДСТВИЕ: bug-fix цикл занимает дни вместо часов, разработчики тратят время на «деплой и смотрим».
> - [ ] Замокать ВСЁ — БД, очереди, downstream сервисы — это и есть «изоляция» | Mocking everything = ничего не тестируется реально; SQL, контракт, асинхронность остаются непокрытыми. ❌ ПОСЛЕДСТВИЕ: 100% покрытие на моках, prod падает с `relation does not exist` — миграции не проверены.

## Q25. (!) Как тестировать `Kafka` с `Testcontainers`?

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

`EmbeddedKafka` быстрее (не нужен Docker), но менее realistic. `Testcontainers Kafka` ближе к production. Подробнее -- в [вопросах по Kafka](../messaging/kafka-interview.md).


> [!mcq]
> - [ ] Использовать `EmbeddedKafka` — Testcontainers лишний, слишком медленный | EmbeddedKafka не имеет Zookeeper-парности с production-Kafka, версия привязана к `spring-kafka-test`; Testcontainers даёт production-parity. ❌ ПОСЛЕДСТВИЕ: тесты зелёные на EmbeddedKafka, на проде Kafka 3.x с KRaft падают unsupported-features ошибки.
> - [ ] `KafkaContainer` без `withEmbeddedZookeeper()` использует внешний Zookeeper | Confluent Kafka в Testcontainers поднимает Zookeeper встроенно; KRaft-mode (без ZK) — отдельный образ. ❌ ПОСЛЕДСТВИЕ: команда настраивает отдельный ZK-контейнер, удваивая ресурсы CI.
> - [x] `KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))` + `@DynamicPropertySource` для `spring.kafka.bootstrap-servers` | Полноценный Kafka-broker в Docker; `kafka.getBootstrapServers()` даёт URL. ✓ ПРИМЕНЯТЬ: тесты `@KafkaListener` consumer'ов с реальным брокером в CI; стандарт в DM/Wolt для event-driven сервисов. 📋 ПРАВИЛО: «KafkaContainer + getBootstrapServers — production-parity для Kafka-тестов». 🔗 См. Q9, Q11, Q26.
> - [ ] Kafka-тесты не требуют `Awaitility` — `Thread.sleep(1000)` достаточно | Async consumer обрабатывает event'ы с непредсказуемой задержкой; sleep — флапающий антипаттерн. ❌ ПОСЛЕДСТВИЕ: тест с `sleep(1000)` падает в CI 10% времени, флапы списывают на «инфру», реальный race-condition остаётся.

## Q26. Как тестировать асинхронные операции?

### `Awaitility` -- стандарт для ожидания асинхронных результатов

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

Правило: **никогда** не используйте `Thread.sleep()` в тестах. Используйте `Awaitility`, `CompletableFuture.get(timeout)` или `CountDownLatch`.


> [!mcq]
> - [ ] `Thread.sleep(5000)` перед `assert` — даём async-обработчику время отработать | Фиксированный сон не реагирует на скорость окружения: локально `500ms` хватает, в CI на загруженном агенте нужно `8s`. ❌ ПОСЛЕДСТВИЕ: тест зелёный локально, флакающий в CI на 5% запусков; команда привыкает к красным билдам и пропускает реальные регрессии.
> - [ ] `CountDownLatch` без `await(timeout, UNIT)` — блокируемся до `latch.await()` | Без timeout зависший async-handler превращает падающий тест в висящий — JUnit убьёт процесс только по `forkEvery` лимиту через 10+ минут. ❌ ПОСЛЕДСТВИЕ: pipeline стоит 15 минут на одном теste, GitHub Actions runner отваливается по `job timeout`.
> - [ ] `assert` сразу после async-вызова — рассчитываем на синхронность в тестовом профиле | Тест проверяет не финальное состояние, а гонку: иногда async успевает до assert, иногда нет. ❌ ПОСЛЕДСТВИЕ: false negative покрытия — тест проходит, но реально не проверяет async-логику; баг с потерянными уведомлениями уходит в prod.
> - [x] `Awaitility.await().atMost(10s).pollInterval(500ms).untilAsserted(() -> ...)` | Polling до выполнения условия или timeout: реагирует на скорость окружения, явно описывает ожидаемое состояние. ✓ ПРИМЕНЯТЬ: `Awaitility` рекомендован документацией Spring для тестов `@Async`/`@EventListener` и используется в Spring Boot test suite. 📋 ПРАВИЛО: «sleep — гадание, await — наблюдение». 🔗 См. Q27, Q31.

## Q27. Как тестировать `message-driven` архитектуру?

### Стратегия тестирования

```mermaid
graph LR
    subgraph "Producer Test"
        P[Producer] -- "отправляет" --> T1[Topic]
        T1 -- "проверяем" --> ASSERT1[Assert: сообщение<br/>отправлено]
    end

    subgraph "Consumer Test"
        T2[Topic] -- "подаём" --> C[Consumer]
        C -- "обрабатывает" --> DB[(DB)]
        DB -- "проверяем" --> ASSERT2[Assert: данные<br/>сохранены]
    end

    subgraph "E2E Test"
        P2[Producer] --> T3[Topic] --> C2[Consumer] --> DB2[(DB)]
    end
```

1. **Тест продюсера**: отправляем сообщение, проверяем, что оно попало в топик
2. **Тест консьюмера**: кладём сообщение в топик, проверяем обработку (данные в БД, вызов сервиса)
3. **E2E**: полная цепочка через реальный брокер
4. **Тест идемпотентности**: отправляем одно сообщение дважды, проверяем, что обработано один раз
5. **Тест DLQ**: отправляем невалидное сообщение, проверяем, что попало в dead letter queue


> [!mcq]
> - [ ] Один E2E-тест: продюсер → топик → консьюмер → БД, и больше ничего | Один сценарий покрывает «happy path», но не ловит retry-логику, идемпотентность, DLQ. ❌ ПОСЛЕДСТВИЕ: дубликат сообщения после `Kafka rebalance` создаёт два заказа в БД — обнаружено через жалобу клиента в prod, не в CI.
> - [ ] Только unit-тесты на `KafkaTemplate.send()` через `@MockBean` | Мокирование `KafkaTemplate` пропускает реальную сериализацию `JSON`/`Avro` и партиционирование. ❌ ПОСЛЕДСТВИЕ: `SerializationException` на разнице schema registry между dev и prod — Wolt 2021 потерял 4 часа заказов из-за несовместимого `Avro`-схемы.
> - [x] Раздельные тесты: продюсер (assert: сообщение в топике), консьюмер (assert: обработка из топика), идемпотентность (двойная отправка), DLQ (невалидное сообщение) | Каждый тест изолирует одну ответственность; `EmbeddedKafka`/`Testcontainers` запускает реальный брокер. ✓ ПРИМЕНЯТЬ: LinkedIn (origin Kafka) и Confluent рекомендуют 4-уровневую стратегию для своих internal services. 📋 ПРАВИЛО: «продюсер, консьюмер, retry, DLQ — четыре теста, не один». 🔗 См. Q25, Q31.
> - [ ] Тестируем продюсер и консьюмер через `kafka-console-producer` в Bash скрипте | Внешний скрипт вне JUnit-цикла: нет интеграции с CI-репортом, нет повторного запуска при падении, нет изоляции между тестами. ❌ ПОСЛЕДСТВИЕ: shared topic между параллельными CI-job → сообщения смешиваются, false negative; команда теряет день на поиск «призрачного» бага.

## Q28. Как организовать интеграционные тесты в `CI/CD`?

### Разделение тестов по стадиям

```mermaid
graph LR
    subgraph "CI Pipeline"
        UNIT["Unit Tests<br/>1-2 мин"] --> INT["Integration Tests<br/>5-10 мин"]
        INT --> CT["Contract Tests<br/>2-3 мин"]
        CT --> E2E["E2E Tests<br/>10-20 мин"]
    end

    UNIT -- "fail fast" --> STOP1[Stop]
    INT -- "fail" --> STOP2[Stop]
```

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

`Testcontainers` делает CI-настройку проще: не нужно поднимать `services` в pipeline -- контейнеры запускаются автоматически из кода тестов.


> [!mcq]
> - [ ] Все тесты (unit + integration + E2E) в одном Gradle-таске `test` без разделения | На каждый коммит крутится 30-минутная пирамида целиком, fail-fast не работает: упавший unit-тест ждёт окончания E2E. ❌ ПОСЛЕДСТВИЕ: 50+ минут feedback на PR, разработчики коммитят без локальной проверки и ловят падения через час.
> - [ ] Запуск integration-тестов через `services:` блок в GitHub Actions с фиксированными портами | `services: postgres` поднимает контейнер до job-а, требует ручную настройку `JDBC URL`, не работает с динамическими портами Testcontainers. ❌ ПОСЛЕДСТВИЕ: миграция между CI-провайдерами (GH Actions → GitLab) требует переписывания pipeline на `docker-compose`, потеря 2 недель.
> - [ ] Все тесты как `@SpringBootTest` без слайсов, `@Tag` не используется | Каждый тест поднимает полный контекст, кэш `ApplicationContext` рвётся на разных конфигурациях. ❌ ПОСЛЕДСТВИЕ: integration suite растёт с 5 до 25 минут за полгода, команда отключает ⅓ тестов «временно» и забывает.
> - [x] Раздельные таски (`test`, `integrationTest`) с `@Tag` фильтрацией; Testcontainers для БД/Kafka; integration зависит от unit через `shouldRunAfter`; `check.dependsOn integrationTest` | Unit fail-fast через 2 мин, integration изолирован, Testcontainers работает на любом CI с Docker. ✓ ПРИМЕНЯТЬ: Spring Boot собственный CI использует эту схему — `gradle test` отдельно от `integrationTest`. 📋 ПРАВИЛО: «fail fast снизу вверх по пирамиде». 🔗 См. Q9, Q29.

## Q29. Как ускорить интеграционные тесты?

### 1. Переиспользование `ApplicationContext`

Spring кеширует `ApplicationContext` между тестами с одинаковой конфигурацией. `@MockBean` **ломает** кеш -- минимизируйте его использование.

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


> [!mcq]
> - [ ] `@MockBean` на каждый тест-класс — изолирует зависимости, ускоряет за счёт мокирования | `@MockBean` создаёт уникальную сигнатуру `ApplicationContext`, ломает Spring TestContext cache: каждый класс с уникальным набором `@MockBean` поднимает контекст заново. ❌ ПОСЛЕДСТВИЕ: 200 интеграционных тестов с разными `@MockBean` = 200 startup'ов контекста по 8 секунд = +25 минут к pipeline.
> - [x] Стабильная конфигурация контекста (один базовый класс) + singleton-контейнеры (`static` Testcontainers с `withReuse`) + test slices (`@DataJpaTest`/`@WebMvcTest`) где возможно + parallel execution в JUnit 5 | Кэш `ApplicationContext` переиспользуется (40-60% экономии), контейнеры стартуют один раз на JVM, slices грузят меньше бинов. ✓ ПРИМЕНЯТЬ: Booking.com описал эту схему в blog post «Speeding up Spring Boot tests» (2022) — снижение CI с 35 до 9 минут. 📋 ПРАВИЛО: «один контекст, один контейнер, slice вместо boot». 🔗 См. Q12, Q30.
> - [ ] Запускать тесты на in-memory H2 вместо Testcontainers PostgreSQL | H2 быстрее запускается (нет Docker overhead), но не повторяет диалект Postgres: `JSONB`, `ON CONFLICT`, оконные функции. ❌ ПОСЛЕДСТВИЕ: тест зелёный на H2, миграция падает в prod на `JSONB GIN index` — потеря 30 минут downtime на rollback Flyway.
> - [ ] Отключить `@Transactional` rollback и переиспользовать данные между тестами | Без rollback тесты делят shared state, порядок выполнения становится критичен. ❌ ПОСЛЕДСТВИЕ: тест A создаёт пользователя `alice`, тест B падает с `unique constraint`; запуск в разном порядке даёт разные результаты — невоспроизводимые падения.

## Q30. Как организовать параллельное выполнение тестов?

### JUnit 5 параллельность

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


> [!mcq]
> - [ ] `parallel.enabled=true` + общая фикстура с фиксированными ID (`user.id = 1`) во всех тестах | Параллельные тесты пишут/читают одну строку, гонка приводит к dirty reads. ❌ ПОСЛЕДСТВИЕ: тест зелёный последовательно, флакающий с `parallel=true` в 30% запусков; команда отключает параллельность и теряет 50% потенциального ускорения.
> - [ ] Параллельность + `@DirtiesContext` на каждом тесте для изоляции | `@DirtiesContext` уничтожает `ApplicationContext` после теста, при параллельном выполнении контекст пересоздаётся постоянно. ❌ ПОСЛЕДСТВИЕ: 100 тестов × 8s startup = +13 минут pipeline; ускорение от parallel перекрывается разрушением кэша.
> - [ ] Параллельность только на классах с `Thread.sleep(1)` для разнесения старта | Sleep не решает race condition на shared data, лишь маскирует на «тёплом» CI. ❌ ПОСЛЕДСТВИЕ: на загруженном CI sleep не помогает, тесты падают; на быстром локальном — проходят. Невоспроизводимые баги.
> - [x] `parallel.mode.classes.default=concurrent` + уникальные данные в каждом тесте (`UUID.randomUUID()`) + `@ResourceLock` на shared resource (Redis ключ, файл) | Изоляция по данным устраняет гонки, `@ResourceLock` сериализует доступ к разделяемому ресурсу там, где изоляция невозможна. ✓ ПРИМЕНЯТЬ: JUnit 5 official docs рекомендует `@ResourceLock(SYSTEM_PROPERTIES)` для тестов, мутирующих `System.setProperty`. 📋 ПРАВИЛО: «уникальные данные параллельны, общие — под замком». 🔗 См. Q15, Q31.

## Q31. (!) Как бороться с flaky-тестами?

**Flaky test** -- тест, который то проходит, то падает без изменений в коде. Основные причины и решения:

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

Мониторинг flaky-тестов: отслеживайте % прохождения каждого теста в CI. Если тест проходит < 99% запусков, он flaky и требует исправления.


> [!mcq]
> - [ ] Retry-плагин CI: автоматически перезапускать упавший тест 3 раза, считать успехом если хоть раз прошёл | Retry маскирует реальные баги: race condition в production коде проходит как «случайность». ❌ ПОСЛЕДСТВИЕ: GitHub 2018 — race condition в worker pool маскировался retry-логикой 6 месяцев, проявился под нагрузкой как 24-часовой downtime.
> - [ ] Увеличить все timeout до 60s — большинство flaky уйдёт | Большие timeout скрывают деградацию (5s → 30s response time остаётся «зелёным»), удлиняют pipeline на падающих сценариях. ❌ ПОСЛЕДСТВИЕ: pipeline 90 минут на ровном месте; пропущена регрессия latency, обнаружена клиентами в prod.
> - [x] Инжектить `Clock` для контроля времени, использовать `Awaitility` вместо `sleep`, `RANDOM_PORT`, `UUID` для данных, мониторить % прохождения каждого теста (< 99% = flaky → чинить или удалять) | Устранение причин (время, порты, shared state, race), а не симптомов. ✓ ПРИМЕНЯТЬ: Spotify публично описал Test Quality Score (% прохождения) и автоудаление тестов < 95% — снижение flaky с 20% до 1.5%. 📋 ПРАВИЛО: «99% или удалить — flaky хуже отсутствия». 🔗 См. Q26, Q30.
> - [ ] `@Order` аннотация для фиксации порядка тестов — устраняет зависимость от порядка | Фиксация порядка превращает зависимость в feature, тесты становятся неизолированы по дизайну. ❌ ПОСЛЕДСТВИЕ: добавление нового теста в середину ломает 5 последующих; рефакторинг занимает день вместо часа.

**Test Fixtures** -- подготовка окружения и данных для тестов. Правильная организация фикстур критична для поддержки тестов.

### Паттерны

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

### Организация

- `@BeforeAll` -- дорогая инициализация (один раз на класс): запуск контейнеров
- `@BeforeEach` -- данные для изоляции: seed, очистка
- Базовые классы (`AbstractIntegrationTest`) -- общие контейнеры и конфигурация
- `@Sql` -- декларативная загрузка SQL-данных


> [!mcq]
> - [ ] Хранить все тестовые объекты в JSON-fixture файлах — единственный правильный подход | ❌ ПОСЛЕДСТВИЕ: JSON-fixtures хороши для статичных payloads, но для частых вариаций (premium/blocked/admin users) дублирование разрастается; нужен builder + Object Mother
> - [x] Комбинация: Test Data Builder (`TestUserBuilder().withEmail(...).premium().build()`) для гибких вариаций одного типа, Object Mother (`TestData.premiumUser()`, `TestData.paidOrder(user)`) для типовых сценариев, `@BeforeAll` для дорогой инициализации контейнеров, `@BeforeEach` для seed/cleanup на каждый тест, базовые классы (`AbstractIntegrationTest`) для общих контейнеров, `@Sql` для декларативной загрузки данных | ✓ ПРИМЕНЯТЬ: Builder при 5+ вариациях одного объекта, Object Mother для коротких имён "premiumUser/blockedUser", AbstractIntegrationTest для shared containers 📋 ПРАВИЛО: Builder = гибкость, Mother = читаемость; уровни setup → @BeforeAll vs @BeforeEach по стоимости 🔗 См. Q33
> - [ ] Использовать production-данные напрямую в тестах | ❌ ПОСЛЕДСТВИЕ: prod-data может содержать PII (GDPR violation), меняется со временем (flaky tests), нарушает изоляцию; всегда генерировать synthetic test data
> - [ ] Один большой `@BeforeAll` для всех данных — экономит время | ❌ ПОСЛЕДСТВИЕ: tests становятся зависимыми (порядок выполнения важен), накапливается state между тестами; нарушение isolation приведёт к flaky tests

## Q33. Как тестировать кеширование?

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

Тестируйте: (1) попадание в кеш; (2) промах; (3) `eviction` при обновлении; (4) `TTL` (через мок `Clock`). Не тестируйте саму библиотеку кеширования -- тестируйте логику приложения.


> [!mcq]
> - [ ] Тестировать саму библиотеку кеширования (Caffeine/Redis) — проверить, что она кеширует | ❌ ПОСЛЕДСТВИЕ: библиотеки уже протестированы их авторами; дублирование тестов; тесты упадут при апгрейде версии без причины
> - [ ] Только TTL — остальное не критично | ❌ ПОСЛЕДСТВИЕ: пропускаются ключевые сценарии — eviction при update (stale data в production), cache miss handling, проверка попадания в кеш
> - [ ] @MockBean всего кеша — реальный кеш не нужен | ❌ ПОСЛЕДСТВИЕ: мок не проверит реальное кеш-поведение приложения (правильность ключей, TTL, eviction policy); баги в @Cacheable конфигурации не вскроются
> - [x] Тестировать четыре аспекта: (1) cache hit — повторный вызов возвращает данные без обращения к DB через `@SpyBean` репозитория + `verify(repo, times(1))`; (2) cache miss — первый вызов идёт в DB; (3) eviction при `@CacheEvict`/update — следующий вызов снова идёт в DB; (4) TTL через мок `Clock`. Использовать реальный Redis через `GenericContainer` + `@ServiceConnection`; НЕ тестировать саму библиотеку, только логику приложения | ✓ ПРИМЕНЯТЬ: при использовании `@Cacheable`/`@CacheEvict`/`@CachePut`, при кастомных key generators 📋 ПРАВИЛО: testing application caching logic, not the cache library itself 🔗 См. Q34

## Q34. Что такое `Smoke Testing` после деплоя?

**Smoke test** -- минимальный набор проверок после деплоя: приложение запустилось, БД доступна, ключевые эндпоинты отвечают.

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

Smoke-тесты: быстрые (секунды), запускаются в pipeline после каждого деплоя. При падении -- rollback или алерт. Не заменяют полные интеграционные тесты. Подробнее о стратегиях деплоя -- в [вопросах по стратегиям деплоя](../cicd/deployment-strategies-interview.md).


> [!mcq]
> - [ ] Smoke test — это полный регресс перед деплоем | ❌ ПОСЛЕДСТВИЕ: путаница с регрессионным тестированием; smoke — быстрая проверка (секунды), регресс — часы; разные цели, разные стадии pipeline
> - [x] Smoke test — минимальный набор быстрых проверок после деплоя: контекст приложения поднялся (`@SpringBootTest`), `/actuator/health` отвечает UP, главные эндпоинты возвращают 200; запускается в pipeline после каждого деплоя; при падении — automatic rollback или alert; не заменяет полные интеграционные тесты, цель — поймать «явные» поломки сразу | ✓ ПРИМЕНЯТЬ: после blue/green switch, canary rollout, любой production deploy 📋 ПРАВИЛО: smoke = «приложение жив?», секунды на ответ, gate для rollback 🔗 См. Q35
> - [ ] Smoke test должен покрывать все business scenarios | ❌ ПОСЛЕДСТВИЕ: если smoke покрывает всё — он перестаёт быть быстрым; deploy pipeline становится 30+ минут вместо секунд; рекомендуется отдельные слои — smoke + integration + e2e
> - [ ] Smoke test не нужен, если есть unit + integration тесты | ❌ ПОСЛЕДСТВИЕ: pre-deploy тесты не отлавливают runtime-проблемы (config, env vars, network); smoke — last line of defense на самой развёрнутой системе

## Q35. (!) Какие best practices для интеграционного тестирования?

### 1. Используйте `Testcontainers` вместо `H2`

Реальная БД ловит реальные баги. `H2` отличается от `PostgreSQL` в поведении SQL, `JSONB`, оконных функций.

### 2. Не ставьте `@Transactional` на интеграционные тесты

Скрывает `LazyInitializationException` и не тестирует транзакционные границы (см. Q17).

### 3. Изолируйте тесты

Каждый тест должен работать независимо. Очищайте данные в `@AfterEach` или используйте уникальные данные.

### 4. Минимизируйте `@MockBean`

Каждый уникальный набор `@MockBean` создаёт новый `ApplicationContext` и замедляет тесты.

### 5. Используйте test slices

`@WebMvcTest`, `@DataJpaTest` быстрее `@SpringBootTest`. Используйте полный контекст только когда нужен.

### 6. Организуйте тесты по уровням

```
src/test/java/          — unit-тесты
src/integrationTest/    — интеграционные тесты
```

### 7. Не дублируйте unit-покрытие

Интеграционные тесты проверяют **взаимодействие**, а не бизнес-логику. Логику тестируйте unit-тестами.

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


> [!mcq]
> - [ ] Ставить `@Transactional` на все интеграционные тесты для автоматического rollback | ❌ ПОСЛЕДСТВИЕ: скрывает `LazyInitializationException` (entity manager не закрывается между запросами в проде), не тестирует транзакционные границы реальной системы; flaky-баги вылезут только в проде
> - [ ] Использовать H2 для скорости — Testcontainers слишком медленные | ❌ ПОСЛЕДСТВИЕ: H2 отличается от PostgreSQL: JSONB, оконные функции, dialect-specific SQL; тесты зелёные, prod падает; запуск H2 «for speed» = false economy
> - [ ] Каждый тест должен делить state с другими для эффективности | ❌ ПОСЛЕДСТВИЕ: tests становятся order-dependent — flaky tests, невозможность parallel execution; isolation — фундамент стабильности интеграционных тестов
> - [x] Best practices: (1) Testcontainers вместо H2 — реальный PostgreSQL ловит реальные баги; (2) НЕ ставить `@Transactional` — скрывает LazyInit, не тестирует boundaries; (3) изолировать тесты — `@AfterEach` cleanup или уникальные данные; (4) минимизировать `@MockBean` — каждый уникальный набор создаёт новый ApplicationContext; (5) test slices (`@WebMvcTest`, `@DataJpaTest`) быстрее `@SpringBootTest`; (6) разделить unit и integration по papka (`src/test/java` vs `src/integrationTest`); (7) не дублировать unit-покрытие; (8) мониторить flaky-тесты; (9) разделять fast/slow по pipeline-стадиям; (10) Given-When-Then для читаемости | ✓ ПРИМЕНЯТЬ: чеклист при code review интеграционных тестов, при настройке pipeline для разделения fast/slow 📋 ПРАВИЛО: real DB + no @Transactional + isolation + slices + GWT 🔗 См. Q36

## Q36. (!) Как использовать `@DataJpaTest` для тестирования репозиториев?

`@DataJpaTest` — это test slice, который поднимает только слой JPA: `EntityManager`, репозитории и Flyway/Liquibase миграции. Остальные компоненты Spring Boot не загружаются.

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

По умолчанию `@DataJpaTest` использует H2 in-memory БД. Чтобы использовать реальную БД через Testcontainers, добавьте `@AutoConfigureTestDatabase(replace = NONE)`.


> [!mcq]
> - [ ] `@DataJpaTest` поднимает весь Spring контекст, включая контроллеры и сервисы | ❌ ПОСЛЕДСТВИЕ: путаница с `@SpringBootTest`; `@DataJpaTest` — slice, загружает только JPA-слой, что критично для скорости (~5x быстрее full context)
> - [ ] По умолчанию `@DataJpaTest` использует production БД из application.yml | ❌ ПОСЛЕДСТВИЕ: ровно наоборот — по умолчанию `@DataJpaTest` подменяет на embedded H2; чтобы использовать real DB через Testcontainers нужен `@AutoConfigureTestDatabase(replace = NONE)`
> - [x] `@DataJpaTest` — test slice, поднимает только JPA-слой (EntityManager, репозитории, Flyway/Liquibase), не загружает контроллеры и сервисы — намного быстрее `@SpringBootTest`. По умолчанию использует H2 in-memory; для real PostgreSQL — `@AutoConfigureTestDatabase(replace = NONE)` + `@Testcontainers`. `TestEntityManager` (обёртка) даёт `persistAndFlush()` и `clear()` для контроля first-level cache | ✓ ПРИМЕНЯТЬ: для тестирования repository queries, @Query methods, кастомных JPA mappings; для проверки named queries и projections 📋 ПРАВИЛО: repository tests → @DataJpaTest + @AutoConfigureTestDatabase(NONE) + Testcontainers 🔗 См. Q37
> - [ ] `@DataJpaTest` не поддерживает `TestEntityManager` — нужен обычный EntityManager | ❌ ПОСЛЕДСТВИЕ: `TestEntityManager` — специально предоставляется `@DataJpaTest` для контроля над персистенцией (persistAndFlush, clear); отказ от него лишит способности контролировать first-level cache в тестах

## Q37. Как тестировать JSON-сериализацию с `@JsonTest`?

`@JsonTest` загружает только конфигурацию Jackson (или Gson/JSONB) — без MVC-слоя и БД. Это быстрый способ проверить `@JsonComponent`, кастомные сериализаторы и формат DTO.

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


> [!mcq]
> - [ ] `@JsonTest` поднимает MVC-слой для проверки сериализации в endpoint | ❌ ПОСЛЕДСТВИЕ: путаница с `@WebMvcTest`; `@JsonTest` — самый узкий slice, только Jackson/Gson/JSONB конфигурация, без MVC и DB
> - [ ] Использовать обычный `ObjectMapper` напрямую в тесте | ❌ ПОСЛЕДСТВИЕ: без `@JsonTest` теряются кастомные `@JsonComponent`, авто-конфигурации Jackson из application.yml; тест не отразит реальное поведение сериализации
> - [x] `@JsonTest` — самый узкий test slice, загружает только Jackson/Gson/JSONB конфигурацию (без MVC, БД, services). Использует `JacksonTester<DTO>` для типизированной сериализации/десериализации: `json.write(obj)` → `JsonContent` с `hasJsonPathStringValue("$.field", "value")`, `json.parse(jsonString)` → `ObjectContent` с `usingRecursiveComparison()`. Идеален для проверки `@JsonComponent`, кастомных сериализаторов, формата дат, `@JsonInclude(NON_NULL)` | ✓ ПРИМЕНЯТЬ: при кастомных сериализаторах, для проверки contract сериализации DTO, при изменении Jackson version 📋 ПРАВИЛО: @JsonTest для serialization concerns без MVC/DB overhead 🔗 См. Q38
> - [ ] `@JsonTest` тестирует только сериализацию, десериализация невозможна | ❌ ПОСЛЕДСТВИЕ: `JacksonTester.parse(json)` возвращает `ObjectContent` для проверки результата десериализации; ложное ограничение лишит половины use-cases

## Q38. (!) Как использовать `RestAssured` для интеграционных API-тестов?

`REST Assured` — библиотека для тестирования REST API с удобным DSL в стиле `given-when-then`. В отличие от `MockMvc`, тестирует реальный HTTP-стек.

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


> [!mcq]
> - [ ] `RestAssured` и `MockMvc` одинаковы — выбор не важен | ❌ ПОСЛЕДСТВИЕ: они тестируют разное: RestAssured — реальный HTTP стек (RANDOM_PORT, фильтры, сервлеты), MockMvc — in-process mock (быстрее, но обходит часть стека); выбор «без разницы» приведёт к пропуску багов в фильтрах
> - [x] `RestAssured` — DSL для testing REST API через реальный HTTP стек (`@SpringBootTest(webEnvironment = RANDOM_PORT)` + `RestAssured.port = port`). Синтаксис `given().contentType().body() → when().post() → then().statusCode().body("field", equalTo(...))`. Тестирует реальные фильтры, сервлеты, security; медленнее MockMvc; идеален для E2E API-тестов. Альтернатива `RestAssuredMockMvc` для @WebMvcTest без полного сервера | ✓ ПРИМЕНЯТЬ: для E2E API-тестов где важна проверка всего стека (security, filters), для contract validation с Hamcrest matchers 📋 ПРАВИЛО: real HTTP + BDD стиль → RestAssured; in-process slice → MockMvc 🔗 См. Q39
> - [ ] `RestAssured` всегда лучше `MockMvc` — используйте только его | ❌ ПОСЛЕДСТВИЕ: RestAssured медленнее (реальный сервер старт + HTTP roundtrip); для unit-уровня controller logic MockMvc в 5-10x быстрее; смешение «one tool fits all» приведёт к долгому CI
> - [ ] `RestAssured` работает только с JSON — XML не поддерживается | ❌ ПОСЛЕДСТВИЕ: ложное ограничение; RestAssured исходно поддерживает и XML (XPath/XSD), и JSON (JsonPath); важно для legacy SOAP API

## Q39. Как тестировать `Spring Security` в интеграционных тестах?

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


> [!mcq]
> - [x] Через `@WithMockUser(username, roles)` — JUnit ставит SecurityContext перед тестом; для проверки `401 Unauthorized` — тест без аннотации; для `403 Forbidden` — `@WithMockUser` с недостаточной ролью; для кастомных схем (JWT) — собственная аннотация через `@WithSecurityContext(factory = ...)` с `WithSecurityContextFactory`, который собирает `JwtAuthenticationToken` и кладёт в `SecurityContextHolder` | ✓ ПРИМЕНЯТЬ: для тестирования @PreAuthorize/hasRole в контроллерах, для проверки 401/403 ответов, для JWT-based аутентификации через кастомные аннотации 📋 ПРАВИЛО: WithMockUser для роль-based, WithSecurityContext factory для JWT/claims 🔗 См. Q40
> - [ ] Хардкодить authentication через `SecurityContextHolder.getContext().setAuthentication()` в каждом тесте | ❌ ПОСЛЕДСТВИЕ: дублирование boilerplate + забывать cleanup → leak between tests; `@WithMockUser`/`@WithSecurityContext` делают это декларативно с auto cleanup
> - [ ] Отключать Security полностью в тестах через `@AutoConfigureMockMvc(addFilters = false)` | ❌ ПОСЛЕДСТВИЕ: тесты «зелёные», но security rules не проверены; в проде /admin endpoint открыт всем; отключение filters допустимо для не-security тестов, но не для security-specific
> - [ ] `@WithMockUser` работает только в `@SpringBootTest`, в `@WebMvcTest` не работает | ❌ ПОСЛЕДСТВИЕ: ложное ограничение; `@WithMockUser` отлично работает в `@WebMvcTest` (и даже там основной use-case), путаница приведёт к лишнему full context

## Q40. Как использовать `@RestClientTest` для тестирования HTTP-клиентов?

`@RestClientTest` — test slice для тестирования компонентов, использующих `RestTemplate` или `RestClient`. Загружает только конфигурацию HTTP-клиентов и `MockRestServiceServer`.

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

`@RestClientTest` — аналог `@WebMvcTest` для исходящих HTTP-вызовов. Он изолирует клиент от реальных внешних сервисов и позволяет проверить маппинг запросов/ответов без поднятия полного контекста.


> [!mcq]
> - [ ] `@RestClientTest` поднимает полный Spring контекст для проверки HTTP-клиента | ❌ ПОСЛЕДСТВИЕ: путаница с `@SpringBootTest`; `@RestClientTest` — slice, загружает только конфигурацию HTTP-клиентов и MockRestServiceServer; full context — медленнее в 5-10x
> - [ ] Использовать WireMock внутри `@SpringBootTest` — единственный способ тестировать клиента | ❌ ПОСЛЕДСТВИЕ: WireMock работает, но избыточен для тестирования одного клиента; `@RestClientTest` + `MockRestServiceServer` решает ту же задачу быстрее (без поднятия HTTP сервера)
> - [ ] Делать реальные вызовы к внешнему API в тестах | ❌ ПОСЛЕДСТВИЕ: flaky tests (зависят от availability сторонних сервисов), долго, риск утечки данных/credentials, неконтролируемые сценарии ошибок (5xx без поломки real API)
> - [x] `@RestClientTest(MyClient.class)` — test slice для тестирования `RestTemplate`/`RestClient`-based компонентов. Автоматически предоставляет `MockRestServiceServer` — мокирует HTTP requests на уровне Spring HTTP client. Синтаксис: `mockServer.expect(requestTo(url)).andExpect(method(POST)).andExpect(content().contentType(JSON)).andRespond(withSuccess(jsonBody, JSON))`; `mockServer.verify()` для проверки что все expectations met; для error scenarios — `withServerError()`, `withBadRequest()` | ✓ ПРИМЕНЯТЬ: для тестирования RestTemplate/RestClient-based интеграций с внешними API; для проверки маппинга request/response, retry logic, error handling 📋 ПРАВИЛО: outbound HTTP testing → @RestClientTest + MockRestServiceServer (не WireMock + full context) 🔗 См. See also

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
