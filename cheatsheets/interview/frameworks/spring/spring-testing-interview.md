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

**Сценарий для Boot 3.5.** Приложение добавляет профиль только в `main`. В тестовом окружении этот профиль больше нигде не задан. Что произойдёт при обычном `@SpringBootTest` и при запуске с `useMainMethod = ALWAYS`?

```java
@SpringBootApplication
class OrdersApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OrdersApplication.class);
        app.setAdditionalProfiles("local-orders");
        app.run(args);
    }
}

@SpringBootTest(classes = OrdersApplication.class)
class DefaultStartupTest {}

@SpringBootTest(
    classes = OrdersApplication.class,
    useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class MainStartupTest {}
```

По умолчанию Boot создаёт контекст теста через `SpringApplication`, пропуская пользовательский `main`. В первом тесте профиль `local-orders` поэтому не активируется. Во втором Boot вызывает `main` и применяет добавленный там профиль. Режим `ALWAYS` требует наличия `main`; `WHEN_AVAILABLE` использует его при наличии и допускает обычную загрузку в остальных случаях. [Spring Boot 3.5: использование main в тестах](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html#testing.spring-boot-applications.using-main).

**Пример интеграционного теста сервиса и репозитория.**

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

**Граница транзакции при реальном HTTP.** В Spring Boot 3.5 с Servlet-сервером при `RANDOM_PORT` или `DEFINED_PORT` тестовый метод и обработчик запроса выполняются в разных потоках. Если добавить к тесту выше `@Transactional`, Spring TestContext по умолчанию откатит транзакцию теста. Если сервис уже зафиксировал заказ в собственной транзакции при обработке POST, заказ останется в БД. Для такого теста очищайте данные после запроса отдельной фиксируемой операцией или используйте изолированную БД. По той же причине тестовые данные, подготовленные в незавершённой транзакции теста, могут быть невидимы обработчику HTTP. [Spring Boot 3.5 — Testing Spring Boot Applications](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html).

## Q3. (!) Что такое test slices и зачем нужны?

**Test slice** — тестовая конфигурация, которая ограничивает сканирование компонентов и набор автоконфигураций под конкретный слой. Например, MVC-срез подключает инфраструктуру MVC, а JPA-срез — инфраструктуру персистентности.

В Boot 3.5 есть два отдельных ограничения. Фильтры среза ограничивают компонентное сканирование, а аннотация среза подключает свой набор автоконфигураций. Явный `@Import` добавляет выбранную конфигурацию целиком, включая её `@Bean`-методы. Поэтому импорт общего класса с настройками HTTP и БД может потянуть зависимости БД в MVC-тест. Если основной класс использует собственный `@ComponentScan`, зарегистрируйте `TypeExcludeFilter`, чтобы сохранить фильтрацию срезов. [Spring Boot 3.5: Testing Spring Boot Applications](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html).

**Зачем это нужно:**
- **Скорость** — меньший контекст обычно быстрее запускается.
- **Изоляция слоя** — состав проверяемых компонентов виден из конфигурации теста.
- **Тестовая инфраструктура** — например, `@DataJpaTest` настраивает `TestEntityManager` и применяет политику выбора тестового DataSource.

Если срез не включает зависимость, нужную этому тесту, добавьте её через `@Import` или подмените через `@MockitoBean`.

## Q4. (!) Что такое `@WebMvcTest` и чем отличается от `@SpringBootTest`?

`@WebMvcTest` ограничивает сканирование MVC-компонентами, например контроллерами, фильтрами и `WebMvcConfigurer`. Положенную вне среза зависимость можно добавить через `@Import` или подменить через `@MockitoBean`.

`@WebMvcTest` автоматически настраивает `MockMvc`. Полная конфигурация приложения загружается через `@SpringBootTest`, где для `MockMvc` нужна `@AutoConfigureMockMvc`. В примере Spring Security подключён, а правила приложения объявлены в отдельном классе `OrderSecurityConfiguration` с бином `SecurityFilterChain`. Обычная конфигурация с таким фабричным методом может остаться за пределами сканирования среза. Импорт подключает именно эту цепочку. Она должна разрешать `POST /api/orders` пользователю с `ROLE_USER`. При включённом CSRF запросу нужен валидный токен. [Spring Security: CSRF в MockMvc](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/csrf.html).

```java
@WebMvcTest(OrderController.class)
@Import(OrderSecurityConfiguration.class)
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
| Объём запуска | Обычно меньше бинов | Зависит от конфигурации и повторного использования кеша |
| MockMvc | Автоматически | Нужен `@AutoConfigureMockMvc` |
| Spring Security | Автоконфигурация при наличии Security, нужную цепочку можно импортировать | По конфигурации приложения |

Если тест возвращает 401 или 403 при ожидаемом 201, проверьте, какая цепочка загружена, затем условия аутентификации, авторизации и CSRF. Валидный CSRF-токен выполняет только условие CSRF-проверки. [Spring Boot 3.5: конфигурация для тестовых срезов](https://docs.spring.io/spring-boot/3.5/how-to/testing.html).

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
        entityManager.clear();

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

В первом тесте `flush()` синхронизирует изменения с БД в текущей транзакции. Следующий `clear()` отсоединяет подготовленную сущность, чтобы при проверке чтения контекст не вернул тот же управляемый экземпляр. Порядок важен: при `clear()` до `flush()` несинхронизированные изменения теряются.

Тест с дубликатом предполагает действующее ограничение уникальности email в тестовой схеме и проверку ограничения при записи. Если ограничение отложено до фиксации, `saveAndFlush()` может завершиться успешно, а ошибка проявится при commit. В таком сценарии проверяйте саму фиксацию. [Spring TestContext 6.2 — транзакционные тесты](https://docs.spring.io/spring-framework/reference/6.2/testing/testcontext-framework/tx.html).

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

В Spring Boot 3.5 `@RestClientTest(OrdersClient.class)` подходит для компонента, который получает настроенный Spring `RestClient.Builder` или `RestTemplateBuilder`. Например, задайте в `MockRestServiceServer` ожидание GET к API заказов и ответ JSON, вызовите метод `OrdersClient`, проверьте разобранные данные и выполните `server.verify()`. Такой тест проверяет формирование запроса и обработку ответа без сетевого сервера.

Если компонент сам вызывает `RestClient.create()`, он обходит настроенный Spring builder. Ожидания автоконфигурированного `MockRestServiceServer` к этому клиенту не привязаны. Передайте builder через конструктор, чтобы срез мог подключить заглушку к клиенту. [Spring Boot 3.5 — RestClientTest](https://docs.spring.io/spring-boot/3.5/api/java/org/springframework/boot/test/autoconfigure/web/client/RestClientTest.html)

## Q7. (!) Что такое `MockMvc` и как им пользоваться?

`MockMvc` выполняет запрос через `DispatcherServlet` без запуска HTTP-сервера и сетевого соединения. Он проверяет обработку MVC и вызывает фильтры, подключённые к конкретной конфигурации `MockMvc`. [Spring MVC: фильтры MockMvc](https://docs.spring.io/spring-framework/reference/testing/mockmvc/hamcrest/filters.html).

Работа с ним строится как fluent-цепочка: формируем запрос → выполняем → проверяем ответ через matchers.

```java
@WebMvcTest(OrderController.class)
@WithMockUser(roles = "USER")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
            .andDo(print());
    }

    @Test
    void getOrder_notFound_returns404() throws Exception {
        when(service.findById(99L)).thenThrow(new OrderNotFoundException(99L));

        mockMvc.perform(get("/api/orders/99"))
            .andExpect(status().isNotFound());
    }
}
```

Пример рассчитан на Spring Boot 3.5. При подключённых Spring Security и `spring-security-test` аннотация `@WithMockUser` задаёт тестовую аутентификацию. Правила загруженной цепочки должны разрешать пользователю с ролью `USER` оба GET-запроса. Иначе цепочка может отклонить запрос до вызова контроллера. Для результата 404 также предполагается, что MVC-срез загружает обработчик, сопоставляющий `OrderNotFoundException` с этим статусом. [Boot 3.5 — WebMvcTest](https://docs.spring.io/spring-boot/3.5/api/java/org/springframework/boot/test/autoconfigure/web/servlet/WebMvcTest.html) · [Spring Security 6.5 — аутентификация в MockMvc](https://docs.spring.io/spring-security/reference/6.5/servlet/test/mockmvc/authentication.html)

**Основные методы fluent API:**
- `perform(request)` — выполнить смоделированный запрос
- `andExpect(matcher)` — проверить ответ (статус, заголовок, тело через `jsonPath`); можно сцеплять несколько проверок
- `andDo(handler)` — побочное действие, например `print()` для вывода деталей запроса и ответа в стандартный вывод
- `andReturn()` — получить `MvcResult` для ручных проверок, когда стандартных matchers не хватает

## Q8. Как тестировать REST endpoint с JSON через MockMvc?

В Spring Boot 3.5 тест запускает MVC с фильтрами Spring Security. Цепочка разрешает `POST /api/orders` пользователю с `ROLE_USER`, CSRF-защита включена, обработчик отказа стандартный. У DTO поле `customerId` помечено `@NotBlank`, аргумент контроллера объявлен как `@Valid @RequestBody` без `BindingResult`. Какой статус получит каждый запрос и на каком этапе завершится обработка?

```java
// Тест выполняется с @WithMockUser(roles = "USER").
var first = mockMvc.perform(post("/api/orders")
    .contentType(APPLICATION_JSON)
    .content("{}"));

var second = mockMvc.perform(post("/api/orders")
    .with(csrf())
    .contentType(APPLICATION_JSON)
    .content("{}"));
```

В первом запросе CSRF-фильтр отклоняет POST со статусом 403 до вызова MVC. Во втором запросе `csrf()` добавляет действительный токен. Конвертер читает `{}` как корректный JSON и создаёт DTO с пустым обязательным полем. Валидация аргумента вызывает `MethodArgumentNotValidException`, который стандартная обработка MVC переводит в 400. В обоих случаях метод контроллера и сервис создания заказа остаются невызванными. [Spring Security 6.5: CSRF в MockMvc](https://docs.spring.io/spring-security/reference/6.5/servlet/test/mockmvc/csrf.html) · [Spring Framework 6.2: RequestBody](https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller/ann-methods/requestbody.html).

В полном тесте успешная ветка дополнительно проверяет статус 201, заголовок `Location` и поле ответа через `jsonPath()`. В ветке с ошибкой можно проверить разрешённое MVC-исключение, чтобы отличить отказ валидации DTO от других причин статуса 400.

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
            .andExpect(status().isBadRequest())
            .andExpect(result -> assertInstanceOf(
                MethodArgumentNotValidException.class, result.getResolvedException()));
        verifyNoInteractions(service);
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
| Жизненный цикл мока | `MockitoExtension` инициализирует поля перед каждым тестом и завершает сессию Mockito после теста | Spring TestContext управляет сбросом мока отдельно от жизненного цикла контекста. Для `@MockBean` и `@MockitoBean` по умолчанию действует `MockReset.AFTER` |
| Кеш контекста | Не влияет | Подмена бина участвует в ключе кеша |
| Использование | Unit тесты | Slice тесты / `@SpringBootTest` |

**Сценарий для Spring Framework 6.2.19.** В контексте два singleton-бина типа `OrderService`: `liveOrders` отмечен `@Primary`, а `archiveOrders` — нет. Какой бин подменит следующий тест? Как выбрать другой?

```java
@SpringBootTest
class OrderRoutingTest {
    @MockitoBean OrderService archiveOrders;
}
```

Из оставшихся подходящих кандидатов реализация 6.2.19 проверяет `@Primary` перед резервным сопоставлением по имени поля. Поэтому тест заменит `liveOrders`. Чтобы подменить архивный сервис, укажите `@MockitoBean(name = "archiveOrders", enforceOverride = true)`. Тогда ошибка в имени приведёт к сбою подготовки контекста вместо появления нового мока. [Исходник Spring Framework 6.2.19](https://github.com/spring-projects/spring-framework/blob/v6.2.19/spring-test/src/main/java/org/springframework/test/context/bean/override/BeanOverrideBeanFactoryPostProcessor.java#L340-L366).

**Сравнение локального мока и подмены бина.**

```java
// @Mock — unit test без Spring:
@ExtendWith(MockitoExtension.class)
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

[Исходник MockitoExtension](https://github.com/mockito/mockito/blob/main/mockito-extensions/mockito-junit-jupiter/src/main/java/org/mockito/junit/jupiter/MockitoExtension.java) · [Spring Framework: MockitoBean API](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/context/bean/override/mockito/MockitoBean.html).

Подмены бинов участвуют в формировании ключа кеша. Тесты с одинаковым ключом могут переиспользовать живой контекст в том же JVM-процессе. Локальный мок Mockito через `@Mock` не меняет ключ (Q13). [Spring TestContext: кеширование](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html).

**Актуальный статус.** С Spring Boot 3.4 `@MockBean` помечена **deprecated**, а в Boot 4.0 удалена. Замена — `@MockitoBean` из Spring Framework 6.2 (пакет `org.springframework.test.context.bean.override.mockito`). По умолчанию она **заменяет или создаёт** бин (`REPLACE_OR_CREATE`); `enforceOverride = true` требует существующий бин и выявляет ошибочный тестовый контекст. Если по типу подходят несколько бинов, Spring может выбрать единственный `@Primary`. Для адресной подмены можно указать имя бина в аннотации. Имя поля тоже может служить fallback qualifier и влиять на ключ кеша контекста, поэтому в соседних тестах его сохраняют одинаковым. Подменённый mock не проходит обычное создание AOP-прокси оригинального бина. [Spring Framework: bean overrides](https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-mockitobean.html).

**Подмена в иерархии контекстов.** В интеграционном тесте `RootConfig` задаёт корневой контекст, а `WebConfig` — дочерний, где объявлен единственный singleton-бин типа `OrderService`. Если мок нужен только в дочернем контексте, укажите имя уровня. Начиная со Spring Framework 6.2.6, `contextName` совпадает с `name` соответствующей `@ContextConfiguration`. С пустым `contextName` подмена применяется на всех уровнях, поэтому стратегия по умолчанию может создать лишний мок в корневом контексте.

```java
@ExtendWith(SpringExtension.class)
@ContextHierarchy({
    @ContextConfiguration(name = "root", classes = RootConfig.class),
    @ContextConfiguration(name = "web", classes = WebConfig.class)
})
class OrderWebIntegrationTest {
    @MockitoBean(contextName = "web", enforceOverride = true)
    OrderService orderService;
}
```

Если в `web` отсутствует подходящий бин, `enforceOverride = true` приводит к ошибке при подготовке контекста. Это помогает заметить, что тест загрузил другую конфигурацию. [Spring Framework 6.2: MockitoBean API](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/test/context/bean/override/mockito/MockitoBean.html#contextName()).

## Q10. Что такое `@SpyBean` и когда нужен?

`@SpyBean` — это **частичный mock** (spy) в Spring-контексте: реальный бин со всей рабочей логикой, у которого можно перехватить отдельные методы — переопределить их поведение через `doReturn`/`doThrow` или проверить факт вызова через `verify`. Всё, что вы не переопределили, продолжает работать по-настоящему.

Spy полезен в двух случаях: проверить, что бин был вызван (`verify`), не подменяя его логику; либо точечно сломать один метод реального бина, чтобы протестировать обработку ошибки, оставив остальное настоящим.

В примере `OrderService.create()` синхронно вызывает `publish(...)` и отдельно обращается к `sendEmail(...)` через внедрённый `OrderEventPublisher`. По контракту сервиса сбой отправки письма не должен прерывать создание заказа.

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

Подмены Mockito входят в ключ кеша контекста. Для `@MockitoSpyBean` в Spring Framework 6.2 по умолчанию действует `MockReset.AFTER`: после каждого тестового метода сбрасываются настройки Mockito и история вызовов. Состояние, которое изменили реальные методы бина или внешние операции, сохраняется и требует собственной изоляции тестов.

**Актуальный статус.** `@SpyBean` deprecated с Spring Boot 3.4 и удалена в Boot 4.0; замена — `@MockitoSpyBean` из Spring Framework 6.2. Её стратегия `WRAP` требует **ровно один существующий** подходящий singleton-бин; в отличие от `@MockitoBean`, отсутствующий бин она не создаёт. Для spy используйте `doReturn(...)`/`doThrow(...)` при заглушении: `when(spy.method())` может вызвать настоящий метод уже во время настройки теста. Для бина с Spring AOP точный порядок обёртывания зависит от того, на каком этапе создаётся spy. В реализации 6.2.19 Spring регистрирует слушатель Mockito, который при `verify(...)` раскрывает Spring AOP-прокси до целевого объекта, если Spring AOP доступен. Факт вызова метода и эффект advice, например открытие транзакции, проверяются отдельно. [Javadoc MockitoSpyBean 6.2](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/test/context/bean/override/mockito/MockitoSpyBean.html) · [Исходник Spring Framework 6.2.19](https://github.com/spring-projects/spring-framework/blob/v6.2.19/spring-test/src/main/java/org/springframework/test/context/bean/override/mockito/MockitoSpyBeanOverrideHandler.java).

## Q11. Что такое `@TestConfiguration` и зачем нужна?

В Spring Boot 3.5 `@TestConfiguration` добавляет определения бинов в тестовый контекст. Верхнеуровневый класс исключается штатным сканированием Boot. Для конкретного теста его можно подключить через `@Import`. Вложенный статический класс с `@TestConfiguration` Boot подключает вместе с основной конфигурацией теста. Вложенный обычный `@Configuration` при автоматическом поиске может занять место основной конфигурации. [Spring Boot 3.5 — тестовая конфигурация](https://docs.spring.io/spring-boot/3.5/reference/testing/spring-boot-applications.html#testing.spring-boot-applications.detecting-configuration).

Импорт дополнительной конфигурации сам по себе сохраняет существующие определения. При регистрации второго определения с тем же именем действует политика переопределения бинов. В Boot 3.5 она по умолчанию запрещает такую замену. Для целевой подмены зависимости в тесте можно использовать `@MockitoBean` из Q9. [Spring Boot 3.5 — свойства приложения](https://docs.spring.io/spring-boot/3.5/appendix/application-properties/index.html#application-properties.core.spring.main.allow-bean-definition-overriding).

```java
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurity(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(a -> a.anyRequest().permitAll())
                   .build();
    }
}
```

```java
@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)  // подключить тестовый конфиг
class OrderControllerTest {
    // Тесты GET-маршрутов контроллера
}
```

Если импортированная цепочка первой подходит запросу, её правило `permitAll()` разрешает доступ без аутентификации. Остальные фильтры цепочки продолжают выполняться, включая проверку CSRF для запросов, которым нужен токен. При нескольких цепочках результат зависит от их порядка и условий применимости. [Spring Security 6.5 — FilterChainProxy](https://docs.spring.io/spring-security/reference/6.5/servlet/architecture.html#servlet-filterchainproxy).

Альтернативный способ подключения конфигурации для одного теста — вложенный статический класс.

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

**Область сброса.** У аннотации на методе по умолчанию действует `AFTER_METHOD`. При `@ContextHierarchy` режим `EXHAUSTIVE` может закрыть другие ветви кеша с общим предком. Например, два дочерних контекста используют один родительский. Если тест изменил состояние только своего дочернего контекста, `hierarchyMode = CURRENT_LEVEL` удалит его и его потомков, сохранив родителя и соседнюю ветвь. [Spring Framework 6.2 — DirtiesContext](https://docs.spring.io/spring-framework/reference/6.2/testing/annotations/integration-spring/annotation-dirtiescontext.html).

**Изоляция данных.** Повторная загрузка контекста занимает время. Если тест меняет только строки в БД, используйте откат тестовой транзакции для записей внутри неё или явно очищайте данные. Запись через HTTP на реальный сервер может зафиксироваться в другой транзакции, как в Q2. Один экземпляр контейнера с БД может обслуживать несколько тестов, пока они используют кешированный контекст. Для чистой БД согласуйте срок жизни контейнера с нужной границей изоляции или восстанавливайте исходные данные между тестами. [Spring Boot 3.5 — жизненный цикл контейнеров](https://docs.spring.io/spring-boot/3.5/reference/testing/testcontainers.html#testing.testcontainers.lifecycle).


## Q13. (!) Как Spring Boot кеширует ApplicationContext в тестах?

Spring TestContext кеширует контексты в пределах JVM-процесса. В ключ входят параметры конфигурации, например её классы и загрузчик, активные профили, тестовые свойства и `ContextCustomizer`, в том числе подмены бинов. Живой контекст с тем же ключом можно использовать повторно. Произвольная аннотация влияет на кеш только если меняет один из параметров ключа. [Spring TestContext: кеширование](https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html).

**Сценарий для Spring Boot 3.5 / Framework 6.2.** Два тестовых класса запускаются в одной JVM. Конфигурация приложения и профили совпадают. В приложении один бин типа `EmailService`. Могут ли классы использовать один контекст?

```java
@SpringBootTest
class OrderTest {
    @MockitoBean EmailService emailService;
}

@SpringBootTest
class UserTest {
    @MockitoBean EmailService mailer;
}
```

Обе подмены выбирают один тип бина. При этом имя поля может участвовать в выборе кандидата как резервный квалификатор и учитывается в конфигурации подмены для кеша. Разные имена полей здесь дают разные ключи контекста. Единое объявление подмены позволяет переиспользовать контекст, если совпадают и остальные части ключа. То же правило квалификаторов действует для `@MockitoSpyBean`. [Spring Framework 6.2: MockitoBean и MockitoSpyBean](https://docs.spring.io/spring-framework/reference/6.2/testing/annotations/integration-spring/annotation-mockitobean.html).

**Оптимизация:**

```java
// Общий базовый класс для тестов с одинаковыми мок-бинами
@SpringBootTest
public abstract class BaseIntegrationTest {
    @MockitoBean EmailService emailService;
    @MockitoBean PaymentService paymentService;
}

class OrderTest extends BaseIntegrationTest {
    // Наследует тестовую конфигурацию и объявления подмен
}

class UserTest extends BaseIntegrationTest {
    // Наследует тот же набор подмен
}
```

**Рекомендации:**
- Сводите конфигурации тестов к нескольким стандартным вариантам — чем меньше уникальных ключей, тем выше попадание в кеш.
- Размер кеша ограничен (`spring.test.context.cache.maxSize`, по умолчанию 32): при переполнении контексты закрываются при вытеснении по LRU и создаются снова при следующем запросе — ещё один повод не плодить варианты.
- Чтобы увидеть промахи кеша и понять, почему контекст пересобирается, включите лог: `logging.level.org.springframework.test.context.cache=DEBUG`.

**Динамические свойства и кеш.** Spring учитывает методы `@DynamicPropertySource` как часть конфигурации контекста. Значение, которое возвращает зарегистрированный `Supplier`, само по себе не меняет ключ кеша. Если два подкласса наследуют один метод, но используют разные адреса БД, уже созданный `DataSource` может сохранить старый адрес. Для отдельных контекстов можно пометить базовый класс `@DirtiesContext`. Если БД должна быть общей для тестов с одним контекстом, привяжите к нему и жизненный цикл контейнера, как в Q15. [Spring Framework 6.2: DynamicPropertySource](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/test/context/DynamicPropertySource.html).

## Q14. Как тестировать Spring Security (аутентификацию/авторизацию)?

Модуль `spring-security-test` позволяет проверить авторизацию с подготовленной аутентификацией. В примере цепочка требует `ROLE_ADMIN` для `/admin/data` и использует HTTP Basic. Запрос без аутентификации получает 401 от `AuthenticationEntryPoint` этой цепочки. С form login он может получить редирект на страницу входа. [Spring Security: ExceptionTranslationFilter](https://docs.spring.io/spring-security/reference/6.5/api/java/org/springframework/security/web/access/ExceptionTranslationFilter.html).

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

**Проверка аутентификации HTTP Basic.** `@WithMockUser` заранее устанавливает аутентификацию, поэтому тест с ним проверяет правило доступа, но не проверяет пароль. Для проверки пароля используйте ту же цепочку и тестовый `UserDetailsService` с пользователем `admin`, ролью `ADMIN` и корректно закодированным паролем. В этих тестах уберите `@WithMockUser` и отправляйте заголовок через `httpBasic()`. [Spring Security 6.5: HTTP Basic в MockMvc](https://docs.spring.io/spring-security/reference/6.5/servlet/test/mockmvc/http-basic.html).

```java
when(service.getData()).thenReturn("admin data");

mockMvc.perform(get("/admin/data").with(httpBasic("admin", testPassword)))
    .andExpect(status().isOk());

mockMvc.perform(get("/admin/data").with(httpBasic("admin", "wrong-password")))
    .andExpect(status().isUnauthorized());
```

**Тест с OAuth2/JWT.** В отдельном тесте с цепочкой resource server `jwt()` подставляет подготовленную аутентификацию. Здесь правило для `/api/orders` требует `ROLE_USER`: authority нужно задать явно или передать конвертер, который его маппит. Claim `roles` сам по себе не даёт `ROLE_USER`.

```java
mockMvc.perform(get("/api/orders")
        .with(jwt().jwt(j -> j.subject("user-123"))
            .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
    .andExpect(status().isOk());
```

`jwt()` позволяет проверить авторизацию с готовой аутентификацией. Чтобы проверить прохождение bearer-токена через фильтр и преобразование claims в authorities, отправьте заголовок `Authorization: Bearer …` и настройте тестовый `JwtDecoder`, возвращающий нужный `Jwt`. Если декодер подменён, проверка подписи и срока действия остаётся за пределами теста. Для неё используйте реальный декодер и подписанные тестовые токены. [Spring Security: MockMvc OAuth2](https://docs.spring.io/spring-security/reference/6.5/servlet/test/mockmvc/oauth2.html).

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
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>("redis:7").withExposedPorts(6379);
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

Конфигурацию с `@Bean`-методами подключают обычным `@Import`. Отдельная аннотация `@ImportTestcontainers` — для другого случая: она подтягивает класс со **static-полями** контейнеров (без `@Bean`-методов для этих контейнеров).

**JUnit управляет контейнером, свойства регистрируются вручную.** класс помечают `@Testcontainers` (JUnit-расширение, которое стартует и останавливает контейнеры с `@Container`), контейнер объявляют как `static`-поле (чтобы он стартовал один раз на класс), а адрес и креды прокидывают вручную через `@DynamicPropertySource`:

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

**Когда тесты проходят отдельно, а вместе падают.** Два класса наследуют статический `@Container` и метод `@DynamicPropertySource` из общей базы. После первого класса JUnit-расширение останавливает PostgreSQL. Spring может сохранить контекст в кеше, а при старте второго класса контейнер может получить новый порт. Если второй класс получил тот же контекст, его пул подключений по-прежнему настроен на старый адрес. Сбой проявится при обращении к БД.

Если классы должны переиспользовать и контекст, и БД, объявите контейнер Spring-бином, как в первом примере. В Boot 3.5 контейнеры-бины запускаются до остальных бинов и останавливаются после их уничтожения при закрытии контекста. `@ServiceConnection` передаёт параметры подключения и может использоваться также на поле `@Container`. В последнем случае контейнером по-прежнему управляет расширение JUnit. [Spring Boot 3.5: жизненный цикл контейнеров](https://docs.spring.io/spring-boot/3.5/reference/testing/testcontainers.html#testing.testcontainers.lifecycle).

## Q16. Что такое `RestTestClient` в Spring Framework 7 и когда он лучше `MockMvc`/`WebTestClient`?

`RestTestClient` появился в Spring Framework 7.0. Он строит синхронные запросы через `RestClient` и предоставляет fluent API для проверки HTTP-ответов. Для приложений на Boot 3.5 со штатным Spring Framework 6.2 применимы `MockMvc` и `WebTestClient`.

- **Отдельный контроллер.** `bindToController(controller)` создаёт standalone-настройку MockMvc. Передайте нужные зависимости контроллера и явно добавьте компоненты MVC, от которых зависит проверяемое поведение.
- **Servlet-конфигурация приложения.** `bindToApplicationContext(context)` строит MockMvc на основе `WebApplicationContext`. `bindTo(mockMvc)` использует уже настроенный MockMvc, например с подключённой цепочкой фильтров. Для функциональных маршрутов Spring MVC есть `bindToRouterFunction(routes)`. Эти режимы работают без сетевого соединения.
- **Запущенное приложение.** `bindToServer().baseUrl(url)` отправляет настоящий HTTP-запрос. Этот режим подходит и для запущенного WebFlux-сервера. Выбор блокирующего клиента относится к тесту и сам по себе не определяет стек сервера.

Например, для проверки маршрута и JSON одного MVC-контроллера достаточно standalone-настройки. Чтобы проверить также настроенные приложением фильтры, можно переиспользовать MockMvc с этими фильтрами.

```java
RestTestClient client = RestTestClient.bindTo(mockMvc).build();

client.get().uri("/orders/1")
    .exchange()
    .expectStatus().isOk()
    .expectBody()
    .jsonPath("$.id").isEqualTo(1);
```

Используйте API `MockMvc` напрямую, если тесту нужны специфичные MVC-проверки, например ошибки биндинга или атрибуты модели. `WebTestClient` подходит для реактивной проверки, включая потоковые ответы, и поддерживает mock-привязку к WebFlux. У обоих HTTP-клиентов пустое тело проверяется через `expectBody().isEmpty()`.

[Spring Framework 7.0 — RestTestClient](https://docs.spring.io/spring-framework/reference/7.0/testing/resttestclient.html) · [WebTestClient](https://docs.spring.io/spring-framework/reference/7.0/testing/webtestclient.html).


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
