---
title: "Вопросы на собеседовании: Тестирование в Spring Boot"
description: "Spring Boot тестирование: @SpringBootTest, @WebMvcTest, @DataJpaTest, MockMvc, @MockBean/@MockitoBean, test slices, кеш ApplicationContext"
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
updated: "2026-10-07"
---
# Вопросы на собеседовании: Тестирование в Spring Boot

Spring Boot предоставляет мощную тестовую инфраструктуру: от лёгких unit-тестов до полных интеграционных тестов с реальным HTTP-сервером. Ключевая тема — **test slices**: загрузка только нужной части контекста для скорости.

Дата последнего обновления: 2026-10-07. Примеры с `@MockBean` и `@SpyBean` в старых Q показывают синтаксис Spring Boot до 3.4; для Boot 3.4+ используйте `@MockitoBean` и `@MockitoSpyBean` из Spring Framework 6.2+, а в Boot 4 старые аннотации удалены (см. Q9/Q10).

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
- [Q1. (!) Что делает `@SpringBootTest` и когда использовать?](#q1--что-делает-springboottest-и-когда-использовать)
- [Q2. Какие режимы `webEnvironment` есть в `@SpringBootTest`?](#q2-какие-режимы-webenvironment-есть-в-springboottest)

**Test Slices**
- [Q3. (!) Что такое test slices и зачем нужны?](#q3--что-такое-test-slices-и-зачем-нужны)
- [Q4. (!) Что такое `@WebMvcTest` и чем отличается от `@SpringBootTest`?](#q4--что-такое-webmvctest-и-чем-отличается-от-springboottest)
- [Q5. Что такое `@DataJpaTest` и как настроить?](#q5-что-такое-datajpatest-и-как-настроить)
- [Q6. Какие ещё test slices есть в Spring Boot?](#q6-какие-ещё-test-slices-есть-в-spring-boot)

**MockMvc**
- [Q7. (!) Что такое `MockMvc` и как им пользоваться?](#q7--что-такое-mockmvc-и-как-им-пользоваться)
- [Q8. Как тестировать REST endpoint с JSON через MockMvc?](#q8-как-тестировать-rest-endpoint-с-json-через-mockmvc)

**@MockBean и @SpyBean**
- [Q9. (!) Чем `@MockBean` отличается от `@Mock` из Mockito?](#q9--чем-mockbean-отличается-от-mock-из-mockito)
- [Q10. Что такое `@SpyBean` и когда нужен?](#q10-что-такое-spybean-и-когда-нужен)

**Конфигурация тестов**
- [Q11. Что такое `@TestConfiguration` и зачем нужна?](#q11-что-такое-testconfiguration-и-зачем-нужна)
- [Q12. Что такое `@DirtiesContext` и когда использовать?](#q12-что-такое-dirtiescontext-и-когда-использовать)
- [Q13. (!) Как Spring Boot кеширует ApplicationContext в тестах?](#q13--как-spring-boot-кеширует-applicationcontext-в-тестах)

**Security и интеграция**
- [Q14. Как тестировать Spring Security (аутентификацию/авторизацию)?](#q14-как-тестировать-spring-security-аутентификациюавторизацию)
- [Q15. Как использовать Testcontainers с Spring Boot?](#q15-как-использовать-testcontainers-с-spring-boot)
- [Q16. Что такое `RestTestClient` в Spring Framework 7 и когда он лучше `MockMvc`/`WebTestClient`?](#q16-что-такое-resttestclient-в-spring-framework-7-и-когда-он-лучше-mockmvcwebtestclient)

---

## Q1. (!) Что делает `@SpringBootTest` и когда использовать?

`@SpringBootTest` запускает контекст выбранной конфигурации через Spring Boot. Состав бинов зависит от активных профилей, условий автоконфигурации, свойств и тестовых подмен.

Если конфигурация не задана явно, Boot ищет основную конфигурацию приложения, обычно класс с `@SpringBootApplication`. Spring TestContext может переиспользовать контекст для тестов с тем же ключом (Q13). [Spring Boot 3.5: Testing Spring Boot Applications](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html).

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

**Когда использовать** — когда важно проверить связку нескольких слоёв вместе:
- Интеграционные тесты, проверяющие взаимодействие нескольких компонентов (сервис + репозиторий + транзакции)
- Тесты с реальной БД / Testcontainers
- End-to-end тесты на HTTP-уровне с `RANDOM_PORT`

**Когда НЕ использовать** — когда хватит более узкого инструмента:
- Unit-тесты отдельных классов — Spring-контекст не нужен вовсе, достаточно Mockito
- Тесты контроллеров — лучше `@WebMvcTest` (поднимает только web-слой)
- Тесты репозиториев — лучше `@DataJpaTest` (поднимает только JPA-слой)

Полный контекст подходит для проверки взаимодействия слоёв. Для теста одного слоя тестовый срез обычно требует меньше бинов и быстрее запускается (Q3–Q6).

## Q2. Какие режимы `webEnvironment` есть в `@SpringBootTest`?

Параметр `webEnvironment` определяет, **поднимать ли реальный web-сервер** для теста и какой порт использовать. От выбора зависит, чем тестировать контроллеры: `MockMvc` (без сервера) или HTTP-клиентом по сети.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

| Режим | Поведение |
|---|---|
| `MOCK` (default) | Загружает тестовый web-контекст без сервера. Без веб-стека может загрузить обычный контекст |
| `RANDOM_PORT` | Запускает реальный сервер на случайном порту |
| `DEFINED_PORT` | Реальный сервер на порту из конфигурации (8080) |
| `NONE` | Не загружает web-environment вообще |

`RANDOM_PORT` берёт случайный свободный порт, чтобы параллельные тесты не конфликтовали; `DEFINED_PORT` нужен редко — например, когда внешняя система обращается на фиксированный адрес.

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

`RANDOM_PORT` подходит для проверки через реальный HTTP. В режиме `MOCK` сервер не запускается. Для `MockMvc` в полном MVC-контексте добавьте `@AutoConfigureMockMvc`: сам `MOCK` клиент не подключает. [Spring Boot 3.5: Testing Spring Boot Applications](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html).

## Q3. (!) Что такое test slices и зачем нужны?

**Test slice** — тестовая конфигурация, которая ограничивает сканирование компонентов и набор автоконфигураций под конкретный слой. Например, MVC-срез подключает инфраструктуру MVC, а JPA-срез — инфраструктуру персистентности.

```
Полный @SpringBootTest:  [Web] + [Service] + [Persistence] + [Security] + ...
@WebMvcTest:             [MVC] + нужные импорты или подмены
@DataJpaTest:            [JPA] + нужные импорты или подмены
```

**Зачем это нужно:**
- **Скорость** — меньший контекст обычно быстрее запускается.
- **Изоляция слоя** — состав проверяемых компонентов виден из конфигурации теста.
- **Тестовая инфраструктура** — например, `@DataJpaTest` настраивает `TestEntityManager` и применяет политику выбора тестового DataSource.

Если срез не включает зависимость, нужную этому тесту, добавьте её через `@Import` или подмените через `@MockitoBean`.

## Q4. (!) Что такое `@WebMvcTest` и чем отличается от `@SpringBootTest`?

`@WebMvcTest` ограничивает сканирование MVC-компонентами, например контроллерами, фильтрами и `WebMvcConfigurer`. Положенную вне среза зависимость можно добавить через `@Import` или подменить через `@MockitoBean`.

`@WebMvcTest` автоматически настраивает `MockMvc`. Полная конфигурация приложения загружается через `@SpringBootTest`, где для `MockMvc` нужна `@AutoConfigureMockMvc`. В примере ниже Spring Security подключён. Цепочка должна разрешать `POST /api/orders` пользователю с `ROLE_USER`. При включённом CSRF запросу нужен валидный токен. [Spring Security: CSRF в MockMvc](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/csrf.html).

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;  // моки сервиса

    @MockitoBean
    private OrderMapper mapper;

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_returns201() throws Exception {
        Order order = new Order(1L, "CUST-1");
        when(orderService.create(any())).thenReturn(order);

        mockMvc.perform(post("/api/orders").with(csrf())
                .contentType(APPLICATION_JSON)
                .content("""
                    { "customerId": "CUST-1" }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }
}
```

| Параметр | `@WebMvcTest` | `@SpringBootTest(MOCK)` |
|---|---|---|
| Загружаемые бины | MVC-срез и добавленные компоненты | Выбранная конфигурация приложения |
| Сервисы/репозитории | При необходимости импорт или `@MockitoBean` | По конфигурации, возможны тестовые подмены |
| Скорость | Быстрее | Медленнее |
| MockMvc | Автоматически | Нужен `@AutoConfigureMockMvc` |
| Spring Security | Автоконфигурация при наличии Security, нужную цепочку можно импортировать | По конфигурации приложения |

**Сценарии применения** — всё, что относится к web-контракту, а не к бизнес-логике:
- Маршрутизация URL → метод контроллера
- Срабатывание валидации (`@Valid`) и ответ 400 на невалидный запрос
- Сериализация/десериализация тела запроса и ответа
- Корректные HTTP-статусы и заголовки

Саму бизнес-логику тестируют отдельно (unit-тестом сервиса), а здесь сервис замокан — проверяется именно поведение web-слоя.

## Q5. Что такое `@DataJpaTest` и как настроить?

`@DataJpaTest` загружает JPA-срез с сущностями, репозиториями и JPA-инфраструктурой. Тесты по умолчанию транзакционные и откатываются. Spring Boot выбирает доступную встроенную БД по classpath, если политика допускает замену. С Boot 3.4 политика по умолчанию `NON_TEST` сохраняет распознанные тестовые подключения, которые автоконфигурированы. `replace = NONE` явно отключает замену. [Spring Boot 3.5: AutoConfigureTestDatabase.Replace](https://docs.spring.io/spring-boot/3.5/api/java/org/springframework/boot/test/autoconfigure/jdbc/AutoConfigureTestDatabase.Replace.html).

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

**Подводный камень.** Замена на H2 удобна для скорости, но H2 — это не ваша production-СУБД: специфичный SQL, типы и ограничения PostgreSQL/MySQL она проверит неполно. Когда важна достоверность, прогоняйте репозиторные тесты на реальной БД через Testcontainers, отключив подмену: `@AutoConfigureTestDatabase(replace = NONE)` (см. пример выше).

`@Commit` фиксирует управляемую тестом транзакцию после выполнения тестового метода. Для явной фиксации и проверки в том же методе используйте `TestTransaction.flagForCommit()` и `TestTransaction.end()`. `@Transactional(propagation = NOT_SUPPORTED)` отключает саму тестовую транзакцию. Транзакции приложения при этом по-прежнему работают по своей конфигурации. [Spring TestContext: транзакции](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html).

## Q6. Какие ещё test slices есть в Spring Boot?

Помимо `@WebMvcTest` и `@DataJpaTest`, Spring Boot даёт slice почти под каждый технологический слой. Идея одна и та же — поднять только нужную часть контекста, — а отличается набор автоконфигураций.

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

`MockMvc` выполняет запрос через `DispatcherServlet` без запуска HTTP-сервера и сетевого соединения. Он проверяет обработку MVC и вызывает фильтры, подключённые к конкретной конфигурации `MockMvc`. [Spring MVC: фильтры MockMvc](https://docs.spring.io/spring-framework/reference/testing/mockmvc/hamcrest/filters.html).

Работа с ним строится как fluent-цепочка: формируем запрос → выполняем → проверяем ответ через matchers.

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

**Основные методы fluent API:**
- `perform(request)` — выполнить смоделированный запрос
- `andExpect(matcher)` — проверить ответ (статус, заголовок, тело через `jsonPath`); можно сцеплять несколько проверок
- `andDo(handler)` — побочное действие, например `print()` для вывода запроса/ответа в лог при отладке
- `andReturn()` — получить `MvcResult` для ручных проверок, когда стандартных matchers не хватает

## Q8. Как тестировать REST endpoint с JSON через MockMvc?

Тело отправляют как `application/json`, а ответ проверяют через `jsonPath()`. `{}` — корректный JSON-объект. В примере DTO имеет обязательные поля, контроллер применяет `@Valid @RequestBody`, а ошибки валидации обрабатываются как 400. Оба POST должны пройти Security: цепочка разрешает запрос пользователю с `ROLE_USER`, а `csrf()` добавляет валидный токен при включённой защите. Иначе фильтры могут вернуть 403 до проверки тела. [Spring MVC: RequestBody](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html) · [Spring Security: CSRF в MockMvc](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/csrf.html).

```java
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;  // Spring Boot autoconfigures it

    @MockitoBean
    private OrderService service;

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_validRequest_returns201() throws Exception {
        OrderRequest request = new OrderRequest("CUST-1", List.of("SKU-001"));
        Order created = new Order(42L, "CUST-1");

        when(service.create(any(OrderRequest.class))).thenReturn(created);

        mockMvc.perform(
                post("/api/orders").with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", containsString("/api/orders/42")))
            .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_invalidRequest_returns400() throws Exception {
        mockMvc.perform(
                post("/api/orders").with(csrf())
                    .contentType(APPLICATION_JSON)
                    .content("{}")  // JSON-объект без обязательных полей DTO
            )
            .andExpect(status().isBadRequest());
    }
}
```

**Про `jsonPath()`.** Он использует синтаксис JsonPath для адресации полей в JSON-ответе: `$` — корень, `$.field` — поле, `$.array[0]` — элемент массива, `$.array.length()` — функция над выборкой. Это позволяет проверять конкретные поля ответа, не десериализуя его в объект целиком.

## Q9. (!) Чем `@MockBean` отличается от `@Mock` из Mockito?

Оба создают mock, но живут на разных уровнях. `@Mock` — чистый Mockito, к Spring отношения не имеет: его передают в тестируемый объект через конструктор либо `@InjectMocks` (Mockito сам пытается внедрить помеченные моки). `@MockBean` — прежняя аннотация Spring Boot Test: она **регистрирует mock в `ApplicationContext`**, заменяя подходящий бин, а Spring внедряет его через DI.

| Параметр | `@Mock` (Mockito) | `@MockBean` (Spring Boot Test) |
|---|---|---|
| Spring-контекст | Не участвует | Добавляет mock в ApplicationContext |
| DI | Конструктор или `@InjectMocks` через Mockito | Spring DI |
| Сброс стейта | После метода (с `@ExtendWith`) | После каждого теста |
| Кеш контекста | Не влияет | Подмена бина участвует в ключе кеша |
| Использование | Unit тесты | Slice тесты / `@SpringBootTest` |

```java
// @Mock — unit test без Spring:
class OrderServiceTest {
    @Mock OrderRepository repository;
    @InjectMocks OrderService service;
}

// @MockitoBean — slice test со Spring Boot 3.4+:
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @MockitoBean OrderService service;  // Spring подставит mock в контроллер
}
```

Подмены бинов участвуют в формировании ключа кеша. Тесты с одинаковым ключом могут переиспользовать живой контекст в том же JVM-процессе. Локальный мок Mockito через `@Mock` не меняет ключ (Q13). [Spring TestContext: кеширование](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html).

**Актуальный статус.** С Spring Boot 3.4 `@MockBean` помечена **deprecated**, а в Boot 4.0 удалена. Замена — `@MockitoBean` из Spring Framework 6.2 (пакет `org.springframework.test.context.bean.override.mockito`). По умолчанию она **заменяет или создаёт** бин (`REPLACE_OR_CREATE`); `enforceOverride = true` требует существующий бин и выявляет ошибочный тестовый контекст. При нескольких кандидатах нужен qualifier или имя. Имя поля тоже может служить fallback qualifier и влиять на ключ кеша контекста, поэтому в соседних тестах его сохраняют одинаковым. Подменённый mock не проходит обычное создание AOP-прокси оригинального бина. [Spring Framework: bean overrides](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-mockitobean.html).

## Q10. Что такое `@SpyBean` и когда нужен?

`@SpyBean` — это **частичный mock** (spy) в Spring-контексте: реальный бин со всей рабочей логикой, у которого можно перехватить отдельные методы — переопределить их поведение через `doReturn`/`doThrow` или проверить факт вызова через `verify`. Всё, что вы не переопределили, продолжает работать по-настоящему.

Нужен в двух случаях: проверить, что бин был вызван (`verify`), не подменяя его логику; либо точечно сломать один метод реального бина, чтобы протестировать обработку ошибки, оставив остальное настоящим.

```java
@SpringBootTest
class OrderEventTest {

    @MockitoSpyBean
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

**`@SpyBean` vs `@MockBean`:**
- `@MockBean` — заглушка целиком: по умолчанию все методы возвращают `null`/`0`/пустую коллекцию, реальной логики нет
- `@SpyBean` — реальный бин: логика работает, а замокать можно лишь те методы, которые вы явно переопределили

Как и `@MockBean`, `@SpyBean` участвует в ключе кеша контекста — злоупотреблять им не стоит.

**Актуальный статус.** `@SpyBean` deprecated с Spring Boot 3.4 и удалена в Boot 4.0; замена — `@MockitoSpyBean` из Spring Framework 6.2. Её стратегия `WRAP` требует **ровно один существующий** подходящий бин; в отличие от `@MockitoBean`, отсутствующий бин она не создаёт. Для spy используйте `doReturn(...)`/`doThrow(...)` при заглушении: `when(spy.method())` может вызвать настоящий метод уже во время настройки теста. При AOP-прокси spy оборачивается прокси; учитывайте это при проверке вызовов. [Spring Framework: Mockito spy override](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-mockitobean.html).

## Q11. Что такое `@TestConfiguration` и зачем нужна?

`@TestConfiguration` — конфигурационный класс, который добавляет или переопределяет бины **только в тестах**. Ключевое отличие от обычного `@Configuration`: он **не подхватывается component scan** основного приложения и подключается лишь явно — через `@Import` или как вложенный `static`-класс внутри теста. Это позволяет держать тестовые бины (заглушку Security, фиксированные `Clock`, тестовый клиент) рядом с тестом и не загрязнять ими production-контекст.

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

**Вложенный класс** — частый вариант, когда конфигурация нужна одному тесту: объявляют `@TestConfiguration static class` прямо внутри тест-класса, и Spring подхватит его автоматически.

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

`@DirtiesContext` закрывает контекст и удаляет его из кеша в момент, заданный режимом. Новый контекст создаётся при следующем запросе той же конфигурации. Аннотация подходит для теста, который изменяет состояние общего контекста. [Spring TestContext: DirtiesContext](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-dirtiescontext.html).

```java
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StatefulIntegrationTest {
    // Текущий контекст закрывается после каждого метода
}

@SpringBootTest
@DirtiesContext  // default: AFTER_CLASS — после всех тестов в классе
class DatabaseMigrationTest {
    // Закрытие контекста не откатывает миграцию во внешней БД
}
```

**Уровни `classMode`:**

| Значение | Когда контекст закрывается и удаляется из кеша |
|---|---|
| `AFTER_CLASS` (default) | После всех тестов класса |
| `BEFORE_CLASS` | До начала тестов класса |
| `AFTER_EACH_TEST_METHOD` | После каждого метода |
| `BEFORE_EACH_TEST_METHOD` | До каждого метода |

**Когда НЕ нужен.** В большинстве случаев `@DirtiesContext` избыточен: пересоздание контекста — дорогая операция, и оно бьёт по кешу (см. Q13), резко замедляя весь прогон. Сперва попробуйте более дешёвую изоляцию:
- Транзакции с rollback (по умолчанию в `@DataJpaTest`, либо `@Transactional` на тесте)
- Очистку данных в `@BeforeEach`/`@AfterEach`
- Testcontainers — каждый прогон стартует с чистой БД

После миграции внешнюю БД нужно очистить или восстановить отдельно. Закрытие контекста не меняет её схему.

## Q13. (!) Как Spring Boot кеширует ApplicationContext в тестах?

Spring TestContext кеширует контексты в пределах JVM-процесса. В ключ входят параметры конфигурации, например её классы и загрузчик, активные профили, тестовые свойства и `ContextCustomizer`, в том числе подмены бинов. Живой контекст с тем же ключом можно использовать повторно. Произвольная аннотация влияет на кеш только если меняет один из параметров ключа. [Spring TestContext: кеширование](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html).

```
@SpringBootTest             → Key A
@SpringBootTest + @MockBean X → Key B  (другой контекст!)
@SpringBootTest + @MockBean Y → Key C  (ещё один контекст!)
```

**Последствие:** разнобой в `@MockBean`, профилях и `@TestPropertySource` по тест-классам плодит десятки разных контекстов, каждый из которых поднимается с нуля — и прогон тестов резко замедляется.

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

**Рекомендации:**
- Сводите конфигурации тестов к нескольким стандартным вариантам — чем меньше уникальных ключей, тем выше попадание в кеш.
- Размер кеша ограничен (`spring.test.context.cache.maxSize`, по умолчанию 32): при переполнении контексты закрываются при вытеснении по LRU и создаются снова при следующем запросе — ещё один повод не плодить варианты.
- Чтобы увидеть промахи кеша и понять, почему контекст пересобирается, включите лог: `logging.level.org.springframework.test.context.cache=DEBUG`.

## Q14. Как тестировать Spring Security (аутентификацию/авторизацию)?

Модуль `spring-security-test` позволяет проверить авторизацию с подготовленной аутентификацией. В примере цепочка требует `ROLE_ADMIN` для `/admin/data` и использует HTTP Basic. Запрос без аутентификации получает 401 от `AuthenticationEntryPoint` этой цепочки. С form login он может получить редирект на страницу входа. [Spring Security: ExceptionTranslationFilter](https://docs.spring.io/spring-security/reference/7.1/api/java/org/springframework/security/web/access/ExceptionTranslationFilter.html).

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

**`@WithMockUser`** — подставляет в `SecurityContext` аутентифицированного пользователя с заданными ролями прямо перед тестом:

```java
@WebMvcTest(AdminController.class)
@Import(AdminSecurityConfig.class)
class AdminControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean AdminService service;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminData_withAdminRole_returns200() throws Exception {
        when(service.getData()).thenReturn("admin data");
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

Здесь `AdminController` вызывает `service.getData()`. В тест импортируют конфигурацию с проверяемой цепочкой:

```java
@Configuration(proxyBeanMethods = false)
class AdminSecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/data").hasRole("ADMIN")
                .anyRequest().denyAll())
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
```

**Тест с OAuth2/JWT.** В отдельном тесте с цепочкой resource server `jwt()` подставляет подготовленную аутентификацию. Здесь правило для `/api/orders` требует `ROLE_USER`: authority нужно задать явно или передать конвертер, который его маппит. Claim `roles` сам по себе не даёт `ROLE_USER`.

```java
mockMvc.perform(get("/api/orders")
        .with(jwt().jwt(j -> j.subject("user-123"))
            .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
    .andExpect(status().isOk());
```

`jwt()` позволяет проверить авторизацию с готовой аутентификацией. Для проверки декодирования, подписи и срока действия токена отдельно тестируют путь через `JwtDecoder`. [Spring Security: MockMvc OAuth2](https://docs.spring.io/spring-security/reference/7.0/servlet/test/mockmvc/oauth2.html).

## Q15. Как использовать Testcontainers с Spring Boot?

Testcontainers поднимает реальную БД/брокер в Docker-контейнере на время теста — это даёт ту же СУБД, что и в production, в отличие от in-memory H2. Задача интеграции одна: пробросить в Spring адрес и креды поднятого контейнера. В Spring Boot 3.1+ это делается автоматически через `@ServiceConnection`, в более старых версиях — вручную через `@DynamicPropertySource`.

**Spring Boot 3.1+ — нативная интеграция.** Добавьте тестовую зависимость `org.springframework.boot:spring-boot-testcontainers` и нужный модуль Testcontainers.

```java
// src/test/java/...
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

    @Bean
    @ServiceConnection  // Boot создаёт JdbcConnectionDetails
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
@Import(TestcontainersConfig.class)
class UserRepositoryTest {
    @Autowired UserRepository repository;

    @Test
    void findAll_withRealDatabase() {
        // Реальный PostgreSQL через Testcontainers
    }
}
```

Конфигурацию с `@Bean`-методами подключают обычным `@Import`. Отдельная аннотация `@ImportTestcontainers` — для другого случая: она подтягивает класс со **static-полями** контейнеров (без `@Bean`-методов), смешивать эти два механизма нельзя.

**Старый способ (до 3.1, без `@ServiceConnection`):** класс помечают `@Testcontainers` (JUnit-расширение, которое стартует и останавливает контейнеры с `@Container`), контейнер объявляют как `static`-поле (чтобы он стартовал один раз на класс), а адрес и креды прокидывают вручную через `@DynamicPropertySource`:

```java
@Testcontainers
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

`@ServiceConnection` позволяет Boot создать `ConnectionDetails` для поддерживаемого контейнера. Автоконфигурация использует эти сведения с приоритетом над свойствами подключения. Для неподдерживаемого контейнера можно передать свойства через `@DynamicPropertySource`. [Spring Boot 3.5: Service Connections](https://docs.spring.io/spring-boot/3.5/reference/testing/testcontainers.html#testing.testcontainers.service-connections).

## Q16. Что такое `RestTestClient` в Spring Framework 7 и когда он лучше `MockMvc`/`WebTestClient`?

`RestTestClient` (`Spring Framework 7.0`) — новый тестовый HTTP-клиент с fluent builder-API; внутри использует `RestClient`, а снаружи даёт цепочку проверок ответа.

Умеет привязываться к разным целям:

- к live-серверу по HTTP (полный сетевой стек и конвертация сообщений);
- к `MockMvc` (mock-запрос/ответ, без сети);
- к `WebApplicationContext`, к отдельным контроллерам или `RouterFunction`.

```java
RestTestClient client = RestTestClient.bindToController(new OrderController()).build();

client.get().uri("/orders/1")
    .exchange()
    .expectStatus().isOk()
    .expectBody(Order.class);
```

Когда лучше:

- против `MockMvc` — более читаемый fluent-API проверок (как у `WebTestClient`), но для **блокирующего** стека.
- против `WebTestClient` — тот тянет реактивные зависимости; `RestTestClient` нужен, когда стек не реактивный.

Пустое тело у обоих клиентов проверяется через `expectBody().isEmpty()`. API `WebTestClient` допускает `null` для отсутствующего тела. [WebTestClient.BodyContentSpec](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/reactive/server/WebTestClient.BodyContentSpec.html) · [RestTestClient](https://docs.spring.io/spring-framework/reference/testing/resttestclient.html).

**Итог:** `RestTestClient` — лёгкий fluent-клиент для тестов блокирующего стека на `Spring Framework 7+`: читаемее `MockMvc` и без реактивных зависимостей `WebTestClient`.

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
