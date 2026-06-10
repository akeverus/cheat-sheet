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

---

## Q1. (!) Что делает `@SpringBootTest` и когда использовать?

`@SpringBootTest` поднимает **полный ApplicationContext** приложения: все бины, автоконфигурации, настройки из `application.yml`. Это самый реалистичный тип теста — код выполняется в окружении, максимально близком к production, но и самый медленный.

Под капотом аннотация находит класс с `@SpringBootApplication`, строит по нему весь контекст и **кеширует** его, чтобы переиспользовать в других тестах с такой же конфигурацией (см. Q13).

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

**Главный компромисс:** `@SpringBootTest` даёт максимальную достоверность ценой скорости. Test slices (Q3–Q6) грузят лишь нужный срез контекста и запускаются значительно быстрее — берите их по умолчанию, а полный контекст оставляйте для случаев, где иначе нельзя.

## Q2. Какие режимы `webEnvironment` есть в `@SpringBootTest`?

Параметр `webEnvironment` определяет, **поднимать ли реальный web-сервер** для теста и какой порт использовать. От выбора зависит, чем тестировать контроллеры: `MockMvc` (без сервера) или HTTP-клиентом по сети.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

| Режим | Поведение |
|---|---|
| `MOCK` (default) | Фиктивный web-environment, MockMvc без реального сервера |
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

**Эмпирическое правило:** `RANDOM_PORT` — для end-to-end тестов через реальный HTTP (`TestRestTemplate`/`WebTestClient`, проверяется и сериализация, и сетевой слой). `MOCK` — для быстрых тестов через `MockMvc`, когда реальный сервер не нужен.

## Q3. (!) Что такое test slices и зачем нужны?

**Test slice** — тестовая аннотация, которая поднимает только **один слой приложения** вместо всего контекста. Идея в том, что для теста контроллера незачем грузить JPA и БД, а для теста репозитория — web-слой и Security. Slice загружает лишь бины своего слоя, а соседние зависимости вы подменяете моками.

```
Полный @SpringBootTest:  [Web] + [Service] + [Persistence] + [Security] + ...
@WebMvcTest:             [Web] + моки остального
@DataJpaTest:            [Persistence] + моки остального
```

**Зачем это нужно:**
- **Скорость** — поднимается на порядок меньше бинов, тест стартует быстрее
- **Изоляция слоя** — падение теста однозначно указывает на проблему в этом слое, а не в соседнем коде
- **Готовая тестовая инфраструктура** — slice сам донастраивает окружение (например, `@DataJpaTest` подключает in-memory H2 и `TestEntityManager`)

**Принцип работы:** slice загружает только бины своего слоя. Всё, что лежит вне среза (сервисы для `@WebMvcTest`, внешние клиенты и т.п.), в контексте отсутствует — его нужно подставить через `@MockBean`, иначе контекст не соберётся.

## Q4. (!) Что такое `@WebMvcTest` и чем отличается от `@SpringBootTest`?

`@WebMvcTest` — это test slice, который поднимает **только web-слой MVC**: контроллеры, фильтры, `WebMvcConfigurer`, `HandlerMethodArgumentResolver`, Jackson, валидацию. Бизнес-бины (сервисы, репозитории) в контекст НЕ попадают, поэтому каждую зависимость контроллера нужно подставить через `@MockBean`.

Ключевое отличие от `@SpringBootTest`: тот грузит весь контекст с реальными сервисами и репозиториями, а `@WebMvcTest` — узкий срез, где всё ниже контроллера замокано. За счёт этого web-тест стартует заметно быстрее.

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
| Загружаемые бины | Только web-слой | Все бины |
| Сервисы/репозитории | Нужен `@MockBean` | Реальные (или `@MockBean`) |
| Скорость | Быстрее | Медленнее |
| MockMvc | Автоматически | Нужен `@AutoConfigureMockMvc` |
| Spring Security | Загружается | Загружается |

**Сценарии применения** — всё, что относится к web-контракту, а не к бизнес-логике:
- Маршрутизация URL → метод контроллера
- Срабатывание валидации (`@Valid`) и ответ 400 на невалидный запрос
- Сериализация/десериализация тела запроса и ответа
- Корректные HTTP-статусы и заголовки

Саму бизнес-логику тестируют отдельно (unit-тестом сервиса), а здесь сервис замокан — проверяется именно поведение web-слоя.

## Q5. Что такое `@DataJpaTest` и как настроить?

`@DataJpaTest` — test slice для **слоя персистентности**: поднимает Entity, репозитории Spring Data JPA, `EntityManager` и JPA-конфигурацию, но не трогает сервисы и web. По умолчанию он **подменяет реальную БД на in-memory H2** и оборачивает каждый тест в транзакцию с откатом, так что данные между тестами не текут.

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

Автоматический rollback после каждого теста даёт изоляцию бесплатно. Если он мешает (нужно проверить реальный commit), отключите его: `@Transactional(propagation = NOT_SUPPORTED)`.

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

`MockMvc` — инструмент Spring для тестирования web-слоя **без запуска реального HTTP-сервера и без сети**. Он прогоняет запрос напрямую через `DispatcherServlet`, проходя весь конвейер MVC (маппинг, фильтры, валидация, сериализация), но не открывая сокет. За счёт этого тесты быстрые и стабильные.

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

Тело запроса формируют через `ObjectMapper` (он уже автонастроен Spring Boot), отправляют с заголовком `Content-Type: application/json`, а ответ проверяют через `jsonPath()`. Обязательно покрывают и «несчастливый» путь — невалидный запрос должен вернуть 400, а не молча пройти.

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

**Про `jsonPath()`.** Он использует синтаксис JsonPath для адресации полей в JSON-ответе: `$` — корень, `$.field` — поле, `$.array[0]` — элемент массива, `$.array.length()` — функция над выборкой. Это позволяет проверять конкретные поля ответа, не десериализуя его в объект целиком.

## Q9. (!) Чем `@MockBean` отличается от `@Mock` из Mockito?

Оба создают mock, но живут на разных уровнях. `@Mock` — чистый Mockito, к Spring отношения не имеет: объект-заглушка, который вы вручную вставляете через `@InjectMocks`. `@MockBean` — аннотация Spring Boot Test: она **подменяет настоящий бин в `ApplicationContext`** mock-объектом, и Spring сам внедряет его во все зависимости через DI.

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

**Подводный камень.** `@MockBean` входит в ключ кеширования контекста: каждый уникальный набор `@MockBean` заставляет Spring собирать **новый** `ApplicationContext` вместо переиспользования кешированного (подробнее в Q13). Поэтому в slice-тестах его минимизируют, а одинаковые наборы моков выносят в общий базовый класс. `@Mock` на кеш не влияет вовсе — он вне Spring.

**Актуальный статус.** С Spring Boot 3.4 `@MockBean` помечена **deprecated**, а в Boot 4.0 удалена. Замена — `@MockitoBean` из Spring Framework 6.2 (пакет `org.springframework.test.context.bean.override.mockito`): семантика та же — подмена бина в контексте mock-объектом, — но механизм теперь живёт в самом Framework, а не в Boot. В новом коде используйте `@MockitoBean`.

## Q10. Что такое `@SpyBean` и когда нужен?

`@SpyBean` — это **частичный mock** (spy) в Spring-контексте: реальный бин со всей рабочей логикой, у которого можно перехватить отдельные методы — переопределить их поведение через `doReturn`/`doThrow` или проверить факт вызова через `verify`. Всё, что вы не переопределили, продолжает работать по-настоящему.

Нужен в двух случаях: проверить, что бин был вызван (`verify`), не подменяя его логику; либо точечно сломать один метод реального бина, чтобы протестировать обработку ошибки, оставив остальное настоящим.

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

**`@SpyBean` vs `@MockBean`:**
- `@MockBean` — заглушка целиком: по умолчанию все методы возвращают `null`/`0`/пустую коллекцию, реальной логики нет
- `@SpyBean` — реальный бин: логика работает, а замокать можно лишь те методы, которые вы явно переопределили

Как и `@MockBean`, `@SpyBean` участвует в ключе кеша контекста — злоупотреблять им не стоит.

**Актуальный статус.** Судьба у `@SpyBean` та же, что и у `@MockBean`: с Spring Boot 3.4 аннотация **deprecated**, в Boot 4.0 удалена. Замена — `@MockitoSpyBean` из Spring Framework 6.2; поведение прежнее (spy поверх реального бина в контексте), в новом коде используйте её.

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

`@DirtiesContext` помечает `ApplicationContext` как «грязный» — то есть его состояние повреждено и переиспользовать его нельзя. Spring **выбрасывает контекст из кеша и пересоздаёт** его (когда именно — задаёт `classMode`). Нужна аннотация в редких случаях, когда тест необратимо меняет общее состояние контекста: правит схему БД, переопределяет бины, ломает статический стейт, — и следующий тест получил бы испорченное окружение.

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

**Когда НЕ нужен.** В большинстве случаев `@DirtiesContext` избыточен: пересоздание контекста — дорогая операция, и оно бьёт по кешу (см. Q13), резко замедляя весь прогон. Сперва попробуйте более дешёвую изоляцию:
- Транзакции с rollback (по умолчанию в `@DataJpaTest`, либо `@Transactional` на тесте)
- Очистку данных в `@BeforeEach`/`@AfterEach`
- Testcontainers — каждый прогон стартует с чистой БД

`@DirtiesContext` оставляйте на тот случай, когда «грязное» именно состояние самого контекста, а не данные в БД.

## Q13. (!) Как Spring Boot кеширует ApplicationContext в тестах?

Поднять контекст — самая дорогая часть теста, поэтому Spring **кеширует и переиспускает** его между тест-классами. Ключ кеша — это комбинация всего, что влияет на состав контекста: набор аннотаций теста, импортированные конфигурации, активные профили, `properties`, а также объявленные `@MockBean`/`@SpyBean`. Если у двух тестов ключ совпал — они получают один и тот же кешированный контекст; если отличается хоть что-то — Spring собирает новый.

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
- Размер кеша ограничен (`spring.test.context.cache.maxSize`, по умолчанию 32): при переполнении контексты вытесняются по LRU и пересоздаются заново — ещё один повод не плодить варианты.
- Чтобы увидеть промахи кеша и понять, почему контекст пересобирается, включите лог: `logging.level.org.springframework.test.context.cache=DEBUG`.

## Q14. Как тестировать Spring Security (аутентификацию/авторизацию)?

Подключают модуль `spring-security-test` и подменяют реальную аутентификацию заранее заданным пользователем — не нужно прогонять настоящий логин или генерировать токены. Дальше через `MockMvc` проверяют, что доступ к эндпоинту зависит от роли: нужная роль → 200, чужая → 403, без аутентификации → 401.

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

**Тест с OAuth2/JWT.** Если приложение — ресурс-сервер и аутентификация идёт по JWT, токен подставляют через post-processor `jwt()` — реальный токен подписывать и валидировать не нужно, в `SecurityContext` кладётся уже готовый `Jwt` с нужными claim'ами:

```java
mockMvc.perform(get("/api/orders")
        .with(jwt().jwt(j -> j.subject("user-123")
                              .claim("roles", List.of("USER")))))
    .andExpect(status().isOk());
```

`jwt()` — это `SecurityMockMvcRequestPostProcessors.jwt()`; для form-login/basic есть аналогичные `user()`, `httpBasic()` и т.д.

## Q15. Как использовать Testcontainers с Spring Boot?

Testcontainers поднимает реальную БД/брокер в Docker-контейнере на время теста — это даёт ту же СУБД, что и в production, в отличие от in-memory H2. Задача интеграции одна: пробросить в Spring адрес и креды поднятого контейнера. В Spring Boot 3.1+ это делается автоматически через `@ServiceConnection`, в более старых версиях — вручную через `@DynamicPropertySource`.

**Spring Boot 3.1+ — нативная интеграция:**

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

**Итог:** `@ServiceConnection` (Spring Boot 3.1+) по типу контейнера сам настраивает нужные свойства — datasource, Redis, Kafka и др., — избавляя от ручного `@DynamicPropertySource`. На новых версиях это предпочтительный путь; старый способ остаётся для legacy-проектов и контейнеров, которые `@ServiceConnection` ещё не поддерживает.

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
