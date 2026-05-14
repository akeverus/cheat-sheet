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

## Q1. Что делает `@SpringBootTest` и когда использовать?

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


> [!mcq]
>
> **Вопрос:** Что делает `@SpringBootTest` и в каких сценариях его выбор оправдан по сравнению с slice-тестами?
>
> ---
>
> #### A) `@SpringBootTest` загружает только web-слой, как `@WebMvcTest`, но дополнительно поднимает Tomcat — ❌ Неверно
>
> **Что на самом деле:** `@SpringBootTest` загружает **полный** ApplicationContext: все `@Component`/`@Service`/`@Repository`/`@Configuration`, все автоконфигурации Spring Boot из `spring.factories`/`AutoConfiguration.imports`. Web-слой — лишь одна из частей. Tomcat запускается опционально через `webEnvironment = RANDOM_PORT/DEFINED_PORT`, по умолчанию `MOCK` (без сервера).
>
> **Откуда путаница:** разработчик видит, что в `@SpringBootTest` доступен MockMvc и думает «это супер-WebMvcTest». На деле MockMvc становится доступен только при добавлении `@AutoConfigureMockMvc`, а контекст содержит ВСЁ приложение.
>
> **Если бы это было правдой:** не было бы смысла в slice-аннотациях — все бы просто писали `@SpringBootTest`. На практике именно из-за разницы в scope test suite на 500 тестов с `@SpringBootTest` собирается 8-12 минут вместо 30 секунд на slice-тестах.
>
> ---
>
> #### B) `@SpringBootTest` загружает полный ApplicationContext (все бины + автоконфигурации + `application.yml`); применять для интеграционных тестов, требующих взаимодействия нескольких слоёв (Service ↔ Repository ↔ DB), а для одиночного слоя предпочтительны slice-тесты (`@WebMvcTest`, `@DataJpaTest`) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `@SpringBootTest` ищет основной класс с `@SpringBootApplication` (поднимаясь по пакетам) и запускает Spring Boot startup как в продакшене: применяет автоконфигурации, читает профили, создаёт все бины. Это самый «честный» тип теста — поведение максимально близко к production. Цена — медленный старт (несколько секунд на контекст) и риск тестировать слишком много за раз. Для тестов одного слоя Spring Boot предлагает slice-аннотации: они загружают только релевантную часть и автоматически мокируют остальное.
>
> **Пример:**
> ```java
> @SpringBootTest
> @AutoConfigureMockMvc
> class OrderE2ETest {
>
>     @Autowired private MockMvc mockMvc;
>     @Autowired private OrderRepository repository;
>
>     @Test
>     void createOrder_persistsAndReturnsCreated() throws Exception {
>         mockMvc.perform(post("/api/orders")
>                 .contentType(APPLICATION_JSON)
>                 .content("""{"customerId":"CUST-1"}"""))
>             .andExpect(status().isCreated());
>
>         assertThat(repository.findAll()).hasSize(1);
>     }
> }
> ```
>
> **Когда применять:**
> - End-to-end тесты бизнес-сценария через несколько слоёв (Controller → Service → Repository → DB).
> - Smoke-тесты на старте CI: «контекст вообще поднимается?»
> - Тестирование автоконфигураций и стартеров.
> - В связке с Testcontainers — близкая к production интеграция (PostgreSQL, Kafka, Redis).
>
> **Подводные камни:**
> - Каждый уникальный набор `@MockBean` создаёт новый контекст в кеше — следите за `org.springframework.test.context.cache=DEBUG`.
> - `webEnvironment = RANDOM_PORT` поднимает Tomcat — не забывайте `@LocalServerPort` для адреса.
> - При `MOCK` (default) реальный сервер НЕ запускается, TestRestTemplate работать не будет — нужен MockMvc.
>
> **Связанные вопросы:** [[Q2]] — режимы `webEnvironment`; [[Q3]] — test slices; [[Q13]] — кеширование ApplicationContext.
>
> ---
>
> #### C) `@SpringBootTest` всегда поднимает реальный HTTP-сервер на порту 8080 — ❌ Неверно
>
> **Что на самом деле:** по умолчанию `webEnvironment = MOCK` — НИКАКОЙ реальный сервер не поднимается. Для реального сервера нужно явно указать `webEnvironment = RANDOM_PORT` (рекомендуется) или `DEFINED_PORT` (для 8080). `MOCK` использует MockMvc через DispatcherServlet без сетевого слоя.
>
> **Откуда путаница:** в JUnit-туториалах часто показывают `@SpringBootTest(webEnvironment = RANDOM_PORT)` и оставляют впечатление что так всегда. Default-режим `MOCK` менее заметен.
>
> **Если бы это было правдой:** параллельный запуск тестов в CI на одной VM конфликтовал бы за порт 8080 — `BindException: Address already in use`. На практике именно поэтому default — `MOCK`, а для HTTP-тестов используют `RANDOM_PORT`.
>
> ---
>
> #### D) `@SpringBootTest` запускает только Spring Test Context без Boot-специфичных автоконфигураций — ❌ Неверно
>
> **Что на самом деле:** `@SpringBootTest` НАСЛЕДУЕТСЯ от `@BootstrapWith(SpringBootTestContextBootstrapper.class)` и явно включает все Boot-автоконфигурации через `SpringBootContextLoader`. Это его главное отличие от чистого `@ContextConfiguration` из Spring Test — Boot magic (data source autoconfigure, Jackson, Web MVC) работает.
>
> **Откуда путаница:** есть Spring Test (`@ContextConfiguration`, `@RunWith(SpringRunner.class)`) — действительно без Boot-specific логики. Можно спутать с `@SpringBootTest`.
>
> **Если бы это было правдой:** в тесте бы не работали `@ConfigurationProperties`, `application.yml` не читался бы, JPA-репозитории не создавались бы автоматически. Spring Boot smart defaults бы просто исчезли — тест перестал бы отражать production.

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


> [!mcq]
>
> **Вопрос:** Какие режимы `webEnvironment` существуют в `@SpringBootTest` и в чём ключевая разница между `MOCK` и `RANDOM_PORT`?
>
> ---
>
> #### A) `MOCK` запускает Tomcat на random-порту, `RANDOM_PORT` — на 8080 — ❌ Неверно
>
> **Что на самом деле:** `MOCK` (default) НЕ запускает реальный сервер вообще — создаётся mock-окружение через `MockServletContext`. `RANDOM_PORT` запускает реальный embedded Tomcat/Jetty/Undertow на случайном свободном порту (порт можно получить через `@LocalServerPort`). `DEFINED_PORT` запускает на конфигурируемом порту (по умолчанию 8080).
>
> **Откуда путаница:** «random port» и «random» в `MOCK` звучат похоже. На самом деле `MOCK` — про mock-объекты, а `RANDOM_PORT` — про случайный TCP-порт.
>
> **Если бы это было правдой:** параллельные `MOCK`-тесты падали бы с `BindException` на CI runner. В реальности `MOCK` идеально параллелится — нет сетевого I/O вообще.
>
> ---
>
> #### B) Все режимы идентичны, разница только в логировании — ❌ Неверно
>
> **Что на самом деле:** режимы кардинально различаются по архитектуре. `MOCK` → запросы идут через MockMvc/DispatcherServlet БЕЗ сокета. `RANDOM_PORT`/`DEFINED_PORT` → реальный HTTP через сокет, доступен TestRestTemplate/WebTestClient. `NONE` → веб-слой вообще не загружается (для тестов без Web — например, batch-задач).
>
> **Откуда путаница:** все режимы синтаксически выглядят одинаково (`webEnvironment = X`). Реальные различия видны только когда подключаешь TestRestTemplate (в `MOCK` он будет `null`).
>
> **Если бы это было правдой:** Spring Boot не имел бы причин иметь 4 разных значения enum. На практике выбор режима меняет: запускается ли Tomcat, нужен ли `@LocalServerPort`, какие тесты возможны.
>
> ---
>
> #### C) `MOCK` (default) — фиктивный web-environment без сокета, тесты через MockMvc; `RANDOM_PORT` — реальный embedded-сервер на случайном порту для тестов через TestRestTemplate/WebTestClient; `DEFINED_PORT` — реальный сервер на сконфигурированном порту; `NONE` — web-слой не загружается — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `webEnvironment` определяет, как Spring подготовит web-окружение в тесте. `MOCK` подходит для подавляющего большинства тестов (он быстр и идемпотентен): DispatcherServlet работает «in-memory», но всё остальное (фильтры, advice, security) — реальное. `RANDOM_PORT` нужен для end-to-end тестов через HTTP-стек: например, проверка реальной сериализации, CORS, headers. `DEFINED_PORT` редок — почти всегда лучше `RANDOM_PORT` (избегает порт-конфликтов в CI). `NONE` — для не-веб приложений (batch, scheduler-only).
>
> **Пример:**
> ```java
> // RANDOM_PORT для HTTP end-to-end теста
> @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
> class OrderHttpTest {
>
>     @LocalServerPort int port;
>     @Autowired TestRestTemplate rest;
>
>     @Test
>     void createOrder_via_http() {
>         ResponseEntity<Order> r = rest.postForEntity(
>             "http://localhost:" + port + "/api/orders",
>             new OrderRequest("CUST-1"), Order.class);
>         assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED);
>     }
> }
>
> // MOCK для большинства тестов
> @SpringBootTest
> @AutoConfigureMockMvc
> class OrderMvcTest {
>     @Autowired MockMvc mockMvc;
>     // никаких портов, реальный сервер не нужен
> }
> ```
>
> **Когда применять:**
> - `MOCK` — основной режим: быстрее, можно параллелить, MockMvc даёт fluent assertions.
> - `RANDOM_PORT` — проверка реального HTTP: WebSocket, SSE, header propagation, CORS preflight.
> - `DEFINED_PORT` — редко: при необходимости фиксированного порта (например, тесты Docker compose, где другой контейнер знает имя/порт).
> - `NONE` — batch jobs, scheduler-driven приложения без REST API.
>
> **Подводные камни:**
> - `MOCK` не выполняет реальную сериализацию HTTP — нюансы Content-Type negotiation могут отличаться от продакшена.
> - В `RANDOM_PORT` `@LocalServerPort` валиден только в test-классе, не в `@Configuration`.
> - При `NONE` нельзя автоматически получить MockMvc — это не web-режим.
>
> **Связанные вопросы:** [[Q1]] — общее назначение `@SpringBootTest`; [[Q7]] — MockMvc fluent API; [[Q15]] — Testcontainers с RANDOM_PORT.
>
> ---
>
> #### D) `RANDOM_PORT` использует mock TCP стек без реального socket binding — ❌ Неверно
>
> **Что на самом деле:** `RANDOM_PORT` запускает РЕАЛЬНЫЙ embedded Tomcat/Jetty с реальным `ServerSocket.bind()` на свободный порт (Spring находит его через `ServerSocketFactory`). Никаких mock-сокетов — реальный TCP listener, реальная HTTP-обработка.
>
> **Откуда путаница:** «mock» в `MOCK` и слово «test» в `@SpringBootTest` могут навести на мысль, что Spring всегда подделывает networking.
>
> **Если бы это было правдой:** нельзя было бы проверить ничего, что требует реального HTTP — WebSocket, SSE, header parsing nuances. На самом деле `RANDOM_PORT` именно для таких проверок и используется.

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


> [!mcq]
>
> **Вопрос:** Что такое test slice и какие бины он загружает по сравнению с полным `@SpringBootTest`?
>
> ---
>
> #### A) Test slice загружает ВЕСЬ ApplicationContext, но логирует только бины своего слоя — ❌ Неверно
>
> **Что на самом деле:** test slice физически загружает только подмножество бинов, относящихся к нужному слою. Реализовано через `@TypeExcludeFilters` + специальный `AutoConfigurationImportFilter`: каждый slice имеет свой `*TypeExcludeFilter` (например, `WebMvcTypeExcludeFilter`), который исключает `@Service`, `@Repository`, `@Component` из основного приложения.
>
> **Откуда путаница:** если в логах теста видны меньше бинов, чем в проде, можно подумать что они «отфильтрованы при выводе». На самом деле они физически отсутствуют в контексте.
>
> **Если бы это было правдой:** не было бы выигрыша по скорости — а на практике slice-тест поднимается в 5-10 раз быстрее, чем `@SpringBootTest`. Это именно потому, что бинов меньше.
>
> ---
>
> #### B) Test slice — это `@Profile("test")` для тестового запуска — ❌ Неверно
>
> **Что на самом деле:** test slice — это **отдельный механизм**, не связанный с профилями. Реализован через композицию `@BootstrapWith` + `@OverrideAutoConfiguration(enabled=false)` + `@TypeExcludeFilters`. Профили (`@ActiveProfiles`) можно использовать ВНУТРИ slice-теста ортогонально.
>
> **Откуда путаница:** и slice и `@Profile` управляют тем, какие бины активны. Кажется что это одно и то же.
>
> **Если бы это было правдой:** для slice-теста пришлось бы помечать профилем каждый продакшен-класс — `@Profile("!test")` на сервис, чтобы он не загружался. На практике никто этого не делает, потому что slice работает независимо.
>
> ---
>
> #### C) Test slice — это шаблон с моками для определённого слоя — ❌ Неверно
>
> **Что на самом деле:** slice сам по себе НЕ создаёт моки. Он лишь убирает бины «соседних» слоёв из контекста. Если контроллеру нужен `OrderService`, и есть `@WebMvcTest` — этот сервис надо явно мокировать через `@MockBean`. Slice не угадывает зависимости автоматически.
>
> **Откуда путаница:** многие читали туториалы, где `@WebMvcTest` идёт парой с `@MockBean OrderService`, и думают что моки — часть slice'а.
>
> **Если бы это было правдой:** не нужно было бы писать `@MockBean` руками — Spring сам мокировал бы всё в контексте. На практике без `@MockBean` тест упадёт на старте: `UnsatisfiedDependencyException`.
>
> ---
>
> #### D) Test slice — тестовая аннотация, загружающая только определённый слой приложения (например, web для `@WebMvcTest` или JPA для `@DataJpaTest`); остальные бины НЕ создаются, отсутствующие зависимости нужно мокировать через `@MockBean`; даёт значительный выигрыш в скорости старта — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Test slices работают через комбинацию аннотаций:
> 1. `@OverrideAutoConfiguration(enabled = false)` — отключает все автоконфигурации.
> 2. `@ImportAutoConfiguration` — включает только релевантные (например, `WebMvcAutoConfiguration`).
> 3. `@TypeExcludeFilters({WebMvcTypeExcludeFilter.class})` — исключает не-web компоненты (`@Service`, `@Component`, кроме `@Controller`).
> 4. `@AutoConfigureMockMvc` (для web) — настраивает MockMvc.
>
> Результат: контекст содержит только нужные бины. Это даёт 5-10× ускорение и изолирует тестируемый слой.
>
> **Пример:**
> ```java
> // @WebMvcTest содержит только web-слой
> @WebMvcTest(OrderController.class)
> class OrderControllerTest {
>     @Autowired MockMvc mockMvc;
>     @MockBean OrderService orderService;   // обязательно — иначе UnsatisfiedDependency
>     @MockBean OrderMapper mapper;          // обязательно
>
>     @Test
>     void getOrder_returns200() throws Exception {
>         when(orderService.findById(1L)).thenReturn(new Order(1L, "CUST-1"));
>         mockMvc.perform(get("/api/orders/1")).andExpect(status().isOk());
>     }
> }
> ```
>
> **Когда применять:**
> - `@WebMvcTest` — для тестов контроллеров (routing, validation, status codes).
> - `@DataJpaTest` — для тестов репозиториев (queries, JPA mappings, constraints).
> - `@JsonTest` — для проверки сериализации JSON.
> - `@RestClientTest` — для тестов HTTP-клиентов (`RestTemplate`/`RestClient`).
>
> **Подводные камни:**
> - `@MockBean` обязателен для каждой зависимости — забыли мокать `OrderMapper` → `UnsatisfiedDependencyException`.
> - Spring Security включается в `@WebMvcTest` — без `@WithMockUser` или `csrf()` POST/PUT упадут с 403.
> - Slice-аннотации НЕ комбинируются друг с другом — нельзя `@WebMvcTest + @DataJpaTest`. Для интеграционного теста используется `@SpringBootTest`.
>
> **Связанные вопросы:** [[Q4]] — `@WebMvcTest` детально; [[Q5]] — `@DataJpaTest`; [[Q6]] — обзор всех slice-аннотаций; [[Q9]] — `@MockBean` для slice-тестов.

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


> [!mcq]
>
> **Вопрос:** Чем `@WebMvcTest` принципиально отличается от `@SpringBootTest` и как корректно подготовить контекст для теста контроллера?
>
> ---
>
> #### A) `@WebMvcTest` загружает только web-слой (контроллеры, `WebMvcConfigurer`, фильтры, argument resolvers), сервисы и репозитории НЕ создаются — нужно мокировать через `@MockBean`; MockMvc настраивается автоматически; работает быстрее `@SpringBootTest` благодаря меньшему контексту — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `@WebMvcTest` через `WebMvcTypeExcludeFilter` исключает из сканирования все `@Service`, `@Component`, `@Repository`, `@Configuration` (кроме `@RestControllerAdvice`, `Converter`, `Filter`, `HandlerInterceptor`, `WebMvcConfigurer`). Это даёт минимальный контекст, идеальный для тестирования URL-роутинга, валидации, исключений, сериализации.
>
> Если указать класс контроллера в параметре (`@WebMvcTest(OrderController.class)`), будет загружен только он — остальные контроллеры приложения исключаются. Без параметра загружаются все контроллеры.
>
> **Пример:**
> ```java
> @WebMvcTest(OrderController.class)
> class OrderControllerTest {
>
>     @Autowired private MockMvc mockMvc;
>     @MockBean private OrderService orderService;
>     @MockBean private OrderMapper mapper;
>
>     @Test
>     void createOrder_validatesRequestBody() throws Exception {
>         mockMvc.perform(post("/api/orders")
>                 .contentType(APPLICATION_JSON)
>                 .content("{}"))  // missing required customerId
>             .andExpect(status().isBadRequest())
>             .andExpect(jsonPath("$.errors[0].field").value("customerId"));
>     }
> }
> ```
>
> **Когда применять:**
> - Проверка mapping URL → controller method.
> - Проверка валидации (`@Valid`, `@NotNull`, custom validators).
> - Проверка статус-кодов и заголовков ответа.
> - Проверка работы `@ControllerAdvice` и exception handlers.
> - Проверка сериализации/десериализации DTO.
>
> **Подводные камни:**
> - Spring Security автоматически активна в `@WebMvcTest` — POST/PUT без `csrf()` упадут на 403. Решение: `mockMvc.perform(post(...).with(csrf()))`.
> - Каждый уникальный набор `@MockBean` создаёт новый контекст → фрагментирует кеш и замедляет общий suite.
> - JPA-репозитории и `@DataSource` НЕ создаются — если контроллер инжектит репозиторий напрямую (анти-паттерн), нужно мокировать.
> - При множестве контроллеров без параметра `@WebMvcTest` загрузит все — это замедлит тест и потребует мокировать ВСЕ их зависимости.
>
> **Связанные вопросы:** [[Q3]] — общее понятие test slice; [[Q7]] — MockMvc детали; [[Q9]] — `@MockBean` особенности; [[Q14]] — тестирование Spring Security в `@WebMvcTest`.
>
> ---
>
> #### B) `@WebMvcTest` загружает весь Spring-контекст, но запускает только web-слой Tomcat — ❌ Неверно
>
> **Что на самом деле:** `@WebMvcTest` НЕ загружает реальный Tomcat и НЕ грузит весь контекст. Это slice-аннотация: загружает только web-инфраструктуру (DispatcherServlet, ControllerAdvice, MessageConverter, MockMvc) и явно указанные контроллеры. Сервисы/репозитории отсутствуют.
>
> **Откуда путаница:** в обоих аннотациях слово «web» наводит на мысль о реальном сервере. На самом деле `@WebMvcTest` использует только MockMvc без сетевого слоя.
>
> **Если бы это было правдой:** не было бы 5-10× ускорения — контекст занимал бы те же 8 секунд что `@SpringBootTest`. На практике slice-тест поднимается за 1-2 секунды.
>
> ---
>
> #### C) `@WebMvcTest` это псевдоним `@SpringBootTest` для контроллеров — ❌ Неверно
>
> **Что на самом деле:** аннотации фундаментально разные. `@SpringBootTest` загружает ВСЁ приложение; `@WebMvcTest` — только web-слой с автоматическими исключениями. Разные `ContextLoader`, разные `TypeExcludeFilters`, разный набор автоконфигураций (slice использует `@ImportAutoConfiguration` whitelist, `@SpringBootTest` — все автоконфигурации).
>
> **Откуда путаница:** в обоих случаях можно тестировать контроллеры, отсюда иллюзия эквивалентности.
>
> **Если бы это было правдой:** не было бы причин для существования двух разных аннотаций. На практике выбор аннотации меняет: время сборки контекста, набор бинов, необходимость `@MockBean`.
>
> ---
>
> #### D) В `@WebMvcTest` MockMvc нужно настраивать вручную через `@AutoConfigureMockMvc` — ❌ Неверно
>
> **Что на самом деле:** `@WebMvcTest` САМ применяет `@AutoConfigureMockMvc` — никаких дополнительных аннотаций не требуется. Достаточно `@Autowired MockMvc mockMvc;` в тесте. В отличие от `@SpringBootTest`, где `@AutoConfigureMockMvc` нужен явно (там нет slice-логики).
>
> **Откуда путаница:** в `@SpringBootTest` действительно нужен `@AutoConfigureMockMvc`. Можно по аналогии решить что и в `@WebMvcTest` тоже.
>
> **Если бы это было правдой:** примеры из официальной документации Spring Boot не работали бы — там не пишут `@AutoConfigureMockMvc` рядом с `@WebMvcTest`. На практике это лишнее, slice сам настраивает MockMvc.

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


> [!mcq]
>
> **Вопрос:** Что делает `@DataJpaTest` и как корректно настроить его для тестов на реальной БД (например, PostgreSQL через Testcontainers)?
>
> ---
>
> #### A) `@DataJpaTest` всегда работает только с H2 in-memory БД, для PostgreSQL нужен `@SpringBootTest` — ❌ Неверно
>
> **Что на самом деле:** `@DataJpaTest` использует **embedded** БД по умолчанию (H2/HSQLDB/Derby — что есть на classpath), но это поведение **переопределяется** через `@AutoConfigureTestDatabase(replace = Replace.NONE)`. С этой аннотацией используется реально настроенный datasource — например, Testcontainers PostgreSQL.
>
> **Откуда путаница:** в гайдах часто показывают H2 как «default». На практике для тестов, проверяющих SQL-функционал PostgreSQL (JSONB, window functions), переключение на реальный PostgreSQL обязательно — и делается оно прямо в `@DataJpaTest`.
>
> **Если бы это было правдой:** все интеграционные тесты репозиториев приходилось бы писать с `@SpringBootTest`. На практике `@DataJpaTest + Replace.NONE + Testcontainers` — самый частый и легковесный подход.
>
> ---
>
> #### B) `@DataJpaTest` не делает транзакций — каждый тест видит данные предыдущего — ❌ Неверно
>
> **Что на самом деле:** `@DataJpaTest` оборачивает каждый тест-метод в транзакцию и **откатывает её** после теста (`@Transactional` с rollback=true применяется автоматически). Это даёт изоляцию тестов на одной БД без необходимости пересоздавать схему.
>
> **Откуда путаница:** при чтении кода `@DataJpaTest` нет видимого `@Transactional` — кажется, что транзакций нет. На деле он включён через мета-аннотации.
>
> **Если бы это было правдой:** тесты на одном файле БД были бы flaky — порядок выполнения определял бы результаты. На практике `@DataJpaTest` гарантирует чистое состояние на каждом тесте через rollback.
>
> ---
>
> #### C) `@DataJpaTest` загружает только JPA-слой (Entity, Repository, JPA-конфигурацию + `TestEntityManager`); по умолчанию использует embedded БД и оборачивает каждый тест в транзакцию с rollback; для теста на реальной БД нужно `@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)` плюс настроенный datasource (например, через Testcontainers `@ServiceConnection`) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Slice `@DataJpaTest` через `DataJpaTypeExcludeFilter` оставляет в контексте: `@Entity`, `JpaRepository`, `EntityManagerFactory`, `TransactionManager`, `TestEntityManager`. Сервисы, контроллеры, security исключаются.
>
> Поведение по умолчанию:
> 1. Embedded DB вместо реальной (`@AutoConfigureTestDatabase(replace = Replace.ANY)`).
> 2. `@Transactional` с автоматическим rollback после каждого теста.
> 3. SQL-логи включены (`spring.jpa.show-sql = true`).
> 4. `TestEntityManager` — обёртка над `EntityManager` для удобной подготовки данных.
>
> **Пример:**
> ```java
> @DataJpaTest
> @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
> @Testcontainers
> class UserRepositoryPostgresTest {
>
>     @Container
>     @ServiceConnection
>     static PostgreSQLContainer<?> postgres =
>         new PostgreSQLContainer<>("postgres:16");
>
>     @Autowired UserRepository repository;
>     @Autowired TestEntityManager em;
>
>     @Test
>     void findByEmail_returnsUser() {
>         em.persist(new User("alice@mail.com", "Alice"));
>         em.flush();
>
>         assertThat(repository.findByEmail("alice@mail.com"))
>             .map(User::getName).contains("Alice");
>     }
> }
> ```
>
> **Когда применять:**
> - Тесты JPA-запросов (`@Query`, derived queries, criteria API).
> - Проверка constraints и unique-индексов на уровне БД.
> - Тесты на маппинг Entity → таблица (особенно `@OneToMany`, `@ManyToOne`).
> - С Testcontainers — для DB-specific фич (PostgreSQL JSONB, full-text search).
>
> **Подводные камни:**
> - Автоматический rollback скрывает баги — например, `flush()` ошибки видны только после явного `em.flush()`.
> - Без `Replace.NONE` ваш PostgreSQL-datasource игнорируется в пользу H2 — тест проходит, но не отражает production.
> - `@DataJpaTest` не загружает `@EntityListeners` с зависимостями на `@Service` — они будут `null`.
> - Lazy loading может работать иначе в тесте (одна транзакция на весь тест) vs в продакшене.
>
> **Связанные вопросы:** [[Q3]] — slices общая идея; [[Q6]] — другие data slices (`@DataJdbcTest`, `@DataMongoTest`); [[Q15]] — Testcontainers с `@ServiceConnection`.
>
> ---
>
> #### D) `@DataJpaTest` поднимает Tomcat и контроллеры, чтобы можно было тестировать репозиторий через REST — ❌ Неверно
>
> **Что на самом деле:** `@DataJpaTest` — slice для JPA-слоя БЕЗ web-инфраструктуры. Никакого Tomcat, никаких контроллеров. Тестирование идёт напрямую через `@Autowired Repository`.
>
> **Откуда путаница:** если читать про «slice» и понимать как «облегчённый SpringBootTest», можно подумать что web остаётся.
>
> **Если бы это было правдой:** время старта `@DataJpaTest` было бы сравнимо с `@SpringBootTest`. На практике slice стартует за секунду — благодаря отсутствию web-слоя.

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


> [!mcq]
>
> **Вопрос:** Какие test slices, кроме `@WebMvcTest` и `@DataJpaTest`, есть в Spring Boot и для каких задач они предназначены?
>
> ---
>
> #### A) Только `@WebMvcTest` и `@DataJpaTest` — других slice-аннотаций нет — ❌ Неверно
>
> **Что на самом деле:** Spring Boot предоставляет 10+ slice-аннотаций. Полный список из `spring-boot-test-autoconfigure`:
> - `@WebMvcTest`, `@WebFluxTest` — web-слой (servlet/reactive).
> - `@DataJpaTest`, `@DataJdbcTest`, `@DataMongoTest`, `@DataNeo4jTest`, `@DataRedisTest`, `@DataR2dbcTest`, `@DataCassandraTest`, `@DataLdapTest` — для разных хранилищ.
> - `@JsonTest`, `@RestClientTest`, `@WebServiceClientTest`, `@JdbcTest`, `@GraphQlTest`.
>
> **Откуда путаница:** в туториалах фокусируются на `@WebMvcTest` и `@DataJpaTest` как самых частых. Остальные slice-аннотации менее известны.
>
> **Если бы это было правдой:** для теста ObjectMapper-сериализации пришлось бы поднимать `@SpringBootTest` (минуты на CI вместо секунды через `@JsonTest`). На практике slice есть почти для каждой задачи.
>
> ---
>
> #### B) Slice-аннотации — это просто алиасы `@SpringBootTest` с разными профилями — ❌ Неверно
>
> **Что на самом деле:** каждый slice — отдельная композиция аннотаций со своим `*TypeExcludeFilter` и whitelist автоконфигураций. Например, `@JsonTest` оставляет только `JacksonAutoConfiguration` + `GsonAutoConfiguration` + `JsonTesters`. `@RestClientTest` оставляет `RestTemplateAutoConfiguration` + `MockRestServiceServerAutoConfiguration`.
>
> **Откуда путаница:** все slice-аннотации выглядят похоже и используются как «упрощённый SpringBootTest». Кажется что это просто скрытые профили.
>
> **Если бы это было правдой:** профили `@Profile("test-json")` и т.д. встречались бы в коде Spring Boot. На практике механизм совсем другой — `@TypeExcludeFilters`.
>
> ---
>
> #### C) Slice-аннотации существуют только для реляционных БД — ❌ Неверно
>
> **Что на самом деле:** есть slice для NoSQL: `@DataMongoTest`, `@DataNeo4jTest`, `@DataRedisTest`, `@DataCassandraTest`, `@DataElasticsearchTest`. Каждый поднимает соответствующую инфраструктуру (например, `@DataMongoTest` использует Flapdoodle embedded MongoDB).
>
> **Откуда путаница:** в Spring исторически JPA — самая видная часть стека. Кажется что slice-механизм заточен под него.
>
> **Если бы это было правдой:** тесты Mongo/Redis-репозиториев требовали бы `@SpringBootTest` — но Spring Boot Test модуль предоставляет именно специализированные slices для них.
>
> ---
>
> #### D) Spring Boot предоставляет slice-аннотации для разных задач: `@WebFluxTest` (reactive web + WebTestClient); `@DataJdbcTest` / `@DataMongoTest` / `@DataRedisTest` / `@DataNeo4jTest` / `@DataR2dbcTest` / `@DataCassandraTest` (NoSQL и reactive repositories); `@JsonTest` (Jackson/Gson сериализация с `JacksonTester`); `@RestClientTest` (RestTemplate/RestClient + MockRestServiceServer); `@WebServiceClientTest` (SOAP); `@JdbcTest` (JdbcTemplate без JPA); `@GraphQlTest` — все используют тот же механизм исключения «не своих» бинов — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Slice-аннотации структурированы в три семейства:
> 1. **Web slices:** `@WebMvcTest` (servlet, MockMvc), `@WebFluxTest` (reactive, WebTestClient), `@GraphQlTest` (GraphQL).
> 2. **Data slices:** `@DataJpaTest`, `@DataJdbcTest`, `@DataR2dbcTest`, `@DataMongoTest`, `@DataRedisTest`, `@DataCassandraTest`, `@DataNeo4jTest`, `@DataLdapTest`, `@DataElasticsearchTest`.
> 3. **Integration helpers:** `@JsonTest` (сериализация), `@RestClientTest` (HTTP-клиенты с моком сервера), `@WebServiceClientTest` (SOAP), `@JdbcTest` (чистый JDBC без JPA).
>
> Все они реализованы через одну инфраструктуру: `*TypeExcludeFilter` + whitelist автоконфигураций.
>
> **Пример:**
> ```java
> // @JsonTest для проверки сериализации DTO
> @JsonTest
> class OrderDtoJsonTest {
>     @Autowired JacksonTester<OrderDto> json;
>
>     @Test
>     void serialize_matchesExpectedJson() throws Exception {
>         OrderDto dto = new OrderDto(1L, "CUST-1", BigDecimal.TEN);
>         assertThat(json.write(dto))
>             .extractingJsonPathStringValue("@.customerId").isEqualTo("CUST-1");
>     }
> }
>
> // @RestClientTest для тестов HTTP-клиента
> @RestClientTest(PaymentClient.class)
> class PaymentClientTest {
>     @Autowired PaymentClient client;
>     @Autowired MockRestServiceServer server;
>
>     @Test
>     void charge_returnsTransactionId() {
>         server.expect(requestTo("/api/charge"))
>               .andRespond(withSuccess("{\"id\":\"txn-1\"}", APPLICATION_JSON));
>         assertThat(client.charge(100)).isEqualTo("txn-1");
>     }
> }
> ```
>
> **Когда применять:**
> - `@JsonTest` — изоляция тестов сериализации (custom serializers, naming strategies).
> - `@RestClientTest` — unit-тест HTTP-клиента с моком сервера через `MockRestServiceServer`.
> - `@DataR2dbcTest` — reactive репозитории на R2DBC.
> - `@GraphQlTest` — schema validation, query resolvers без поднятия HTTP.
>
> **Подводные камни:**
> - `@DataMongoTest` по умолчанию хочет Flapdoodle embedded Mongo — для production-like нужен `@AutoConfigureDataMongo` + Testcontainers.
> - `@RestClientTest` мокает только сервер, а не сам клиент — нельзя проверить retry/timeout логику без дополнительных моков.
> - Композиция slice-аннотаций не работает: нельзя `@DataJpaTest + @WebMvcTest` — каждый имеет свой `TypeExcludeFilter`.
>
> **Связанные вопросы:** [[Q3]] — концепция slice; [[Q4]] — `@WebMvcTest`; [[Q5]] — `@DataJpaTest`.

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


> [!mcq]
>
> **Вопрос:** Что такое `MockMvc` и в каких сценариях его использование оправдано по сравнению с `TestRestTemplate`/`WebTestClient`?
>
> ---
>
> #### A) `MockMvc` — это HTTP-клиент с моком сервера, подключается к реальному Tomcat — ❌ Неверно
>
> **Что на самом деле:** `MockMvc` — наоборот: НЕ имеет реального сервера и НЕ открывает сокет. Использует `MockHttpServletRequest` + `DispatcherServlet` для симуляции HTTP-запросов прямо в JVM. Под капотом — `TestDispatcherServlet`, который проходит через стандартный pipeline Spring MVC (interceptors, controller, exception handler), но всё in-process.
>
> **Откуда путаница:** слово «Mock» и «Mvc» создают впечатление о моке клиента/сервера. На деле это исполнитель Spring MVC pipeline без сетевого слоя.
>
> **Если бы это было правдой:** `MockMvc` зависел бы от свободного порта, не параллелился бы хорошо в CI. На практике он быстрее и предсказуемее именно благодаря отсутствию сокетов.
>
> ---
>
> #### B) `MockMvc` — Spring-инструмент для тестирования web-слоя БЕЗ запуска реального HTTP-сервера: симулирует HTTP-запросы напрямую через DispatcherServlet (in-process), даёт fluent API (`perform → andExpect → andDo → andReturn`); применять когда нужно тестировать routing/валидацию/статусы быстро и предсказуемо; для end-to-end через реальный HTTP — использовать `TestRestTemplate`/`WebTestClient` с `webEnvironment = RANDOM_PORT` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `MockMvc` работает следующим образом:
> 1. `MockHttpServletRequest` создаётся через builders (`get(url)`, `post(url)`, и т.д.).
> 2. `TestDispatcherServlet` принимает request — но в памяти, без сокета.
> 3. Запрос проходит через реальный Spring MVC pipeline: HandlerMapping → Interceptors → Controller → HandlerExceptionResolver → ViewResolver.
> 4. Ответ упаковывается в `MockHttpServletResponse`.
> 5. Fluent matchers (`andExpect`) проверяют ответ.
>
> Это даёт скорость (нет TCP) + полноту (реальный DispatcherServlet pipeline). Единственное, чего MockMvc не покрывает — реальная HTTP-сериализация заголовков и сокет-уровневые вещи (keep-alive, chunking).
>
> **Пример:**
> ```java
> @WebMvcTest(OrderController.class)
> class OrderControllerTest {
>
>     @Autowired MockMvc mockMvc;
>     @MockBean OrderService service;
>
>     @Test
>     void getOrder_notFound_returns404() throws Exception {
>         when(service.findById(99L))
>             .thenThrow(new OrderNotFoundException(99L));
>
>         mockMvc.perform(get("/api/orders/99"))
>             .andExpect(status().isNotFound())
>             .andExpect(jsonPath("$.error").value("ORDER_NOT_FOUND"))
>             .andDo(print());      // dump request/response в консоль
>     }
> }
> ```
>
> **Когда применять:**
> - Тесты routing и URL-mapping.
> - Проверка валидации (`@Valid`) и handler exception mappers.
> - Тесты Spring Security (`with(user(...))`, `with(csrf())`, `with(jwt())`).
> - Тесты `@ControllerAdvice` для глобальной обработки ошибок.
> - Быстрые контроллер-тесты (slice `@WebMvcTest`).
>
> **Подводные камни:**
> - `MockMvc` НЕ выполняет реальную HTTP-сериализацию — нюансы Content-Type negotiation могут отличаться от продакшена.
> - `andDo(print())` пишет в System.out — в CI логи могут засоряться.
> - При работе с async-контроллерами (`Callable`, `DeferredResult`) нужен `andExpect(request().asyncStarted())` + `asyncDispatch()`.
> - В `@SpringBootTest` MockMvc нужно явно включить через `@AutoConfigureMockMvc` — в `@WebMvcTest` он включён сам.
>
> **Связанные вопросы:** [[Q4]] — `@WebMvcTest` + MockMvc; [[Q8]] — JSON через MockMvc; [[Q14]] — Spring Security в MockMvc.
>
> ---
>
> #### C) `MockMvc` симулирует только GET-запросы, для POST/PUT нужен TestRestTemplate — ❌ Неверно
>
> **Что на самом деле:** `MockMvc` поддерживает ВСЕ HTTP-методы: `get`, `post`, `put`, `patch`, `delete`, `head`, `options`, `request` (для нестандартных методов). Доступны fluent builders: `.contentType()`, `.content(jsonBody)`, `.header()`, `.cookie()`, `.with(csrf())`.
>
> **Откуда путаница:** в простых примерах часто показывают только `get(url)`. Создаётся впечатление что это всё что умеет MockMvc.
>
> **Если бы это было правдой:** не было бы тестов на создание ресурсов через POST — а это самая частая операция в API. На практике `mockMvc.perform(post(...).content(...))` — нормальная идиома.
>
> ---
>
> #### D) `MockMvc` — устаревший API, в Spring Boot 3+ заменён на `WebTestClient` — ❌ Неверно
>
> **Что на самом деле:** `MockMvc` активно поддерживается в Spring Boot 3.x и не помечен как deprecated. `WebTestClient` — это **reactive** клиент из Spring WebFlux, он применяется для WebFlux-приложений (`@WebFluxTest`). Для traditional Spring MVC servlet-приложений MockMvc остаётся стандартом. В Spring Framework 6.2 даже добавили `MockMvc.perform(...)` интеграцию с `WebTestClient` для единообразия API.
>
> **Откуда путаница:** WebTestClient выглядит «новее» (reactive API), и есть гайды по миграции на него. Но миграция оправдана только если приложение само reactive.
>
> **Если бы это было правдой:** Spring Boot 3.x reference guide рекомендовал бы WebTestClient везде. На практике в документации MockMvc — основной инструмент для MVC-тестов.

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


> [!mcq]
>
> **Вопрос:** Как корректно тестировать REST endpoint с JSON-payload через `MockMvc` и какие matchers использовать для проверки ответа?
>
> ---
>
> #### A) Отправлять JSON через `.contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))`, проверять статус через `status().isCreated()`, заголовки через `header().string(...)`, JSON-поля через `jsonPath("$.field").value(...)`; для типизированного парсинга ответа — `andReturn().getResponse().getContentAsString()` → `objectMapper.readValue(...)` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Идиома MockMvc для POST с JSON: builders `post(url)` + `.contentType(APPLICATION_JSON)` + `.content(jsonString)`. Сериализация делается через `ObjectMapper` (Spring Boot регистрирует его в контексте автоматически), который респектит конфигурацию приложения (модули `JavaTimeModule`, `kebab-case`, `@JsonIgnore`).
>
> Для проверки ответа есть несколько matchers:
> - `status()` — HTTP-код (`isOk`, `isCreated`, `isBadRequest`, `isUnauthorized`).
> - `header()` — заголовки (`Location`, `Content-Type`, custom).
> - `jsonPath()` — поля JSON через JsonPath синтаксис (`$.id`, `$.items[0].sku`, `$.errors.length()`).
> - `content().json(...)` — сравнение всего payload через JSONassert (поддерживает strict/lenient режимы).
>
> Для получения объекта ответа: `MvcResult result = mockMvc.perform(...).andReturn();` → `String body = result.getResponse().getContentAsString();` → `OrderResponse dto = objectMapper.readValue(body, OrderResponse.class);`.
>
> **Пример:**
> ```java
> mockMvc.perform(post("/api/orders")
>         .contentType(APPLICATION_JSON)
>         .content(objectMapper.writeValueAsString(new OrderRequest("CUST-1"))))
>     .andExpect(status().isCreated())
>     .andExpect(header().string("Location", containsString("/api/orders/")))
>     .andExpect(jsonPath("$.id").exists())
>     .andExpect(jsonPath("$.customer").value("CUST-1"));
> ```
>
> **Когда применять:**
> - Тесты POST/PUT endpoints с JSON body.
> - Проверка контракта API (поля, статусы, заголовки).
> - Проверка валидации (`@Valid` + `MethodArgumentNotValidException`).
> - Negative cases: 400/404/409 для ошибочных входов.
>
> **Подводные камни:**
> - `jsonPath("$.field").value(42)` строго сравнивает тип — `42` (int) НЕ равен `42L` (long), используйте `value(42)` для int, `value(42L)` для long.
> - `content().json("{\"id\":1}", true)` — strict mode (порядок и все поля), `false` — lenient (подмножество).
> - При `@RestControllerAdvice` важно тестировать формат ошибки (`$.error`, `$.timestamp`) — иначе изменение глобального handler-а сломает контракт без сигнала.
> - Не забывайте про CSRF в Spring Security: для POST нужно `.with(csrf())` если фильтр включён.
>
> **Связанные вопросы:** [[Q7]] — основы MockMvc; [[Q14]] — Spring Security + JSON тесты.
>
> ---
>
> #### B) Использовать `RestAssured` вместо MockMvc — это единственный способ отправить JSON в тесте — ❌ Неверно
>
> **Что на самом деле:** MockMvc нативно поддерживает JSON через `.contentType(APPLICATION_JSON).content(...)`. RestAssured — отдельная библиотека для тестирования через реальный HTTP-сервер (применяется с `webEnvironment = RANDOM_PORT`), не замена MockMvc, а альтернатива для end-to-end.
>
> **Откуда путаница:** RestAssured популярен в acceptance/E2E-тестах с fluent BDD-стилем (`given().when().then()`), что создаёт впечатление о его универсальности.
>
> **Если бы это было правдой:** все Spring Boot reference примеры были бы на RestAssured. На практике документация показывает именно MockMvc для контроллер-тестов.
>
> ---
>
> #### C) JSON нужно вручную собирать в `String`, без `ObjectMapper`, иначе тест зависит от рантайма приложения — ❌ Неверно
>
> **Что на самом деле:** наоборот — использование `ObjectMapper` из контекста делает тест устойчивее к рефакторингу DTO. Если поле переименовали, тест упадёт на этапе сериализации (раньше), а не на jsonPath (что менее очевидно). Конкатенация строк JSON — антипаттерн: при добавлении поля все тесты ломаются.
>
> **Откуда путаница:** в простых примерах туториалов часто пишут JSON inline: `.content("{\"name\":\"foo\"}")`. Это работает, но плохо масштабируется.
>
> **Если бы это было правдой:** при изменении DTO пришлось бы вручную править десятки JSON-строк в тестах. На практике `objectMapper.writeValueAsString(dto)` автоматически следует за изменениями DTO.
>
> ---
>
> #### D) `jsonPath()` не работает в MockMvc — нужно парсить response как String и сравнивать через `equals` — ❌ Неверно
>
> **Что на самом деле:** `jsonPath()` — стандартный matcher MockMvc (`org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath`). Использует JsonPath библиотеку Jayway для навигации по JSON-структуре: `$.items[0].id`, `$.errors[?(@.code=='X')]`, `$.array.length()`.
>
> **Откуда путаница:** возможно, конфьюзится с XPath (для XML). Или с ситуацией когда забыли добавить `jayway-jsonpath` в classpath (он включён транзитивно через `spring-boot-starter-test`).
>
> **Если бы это было правдой:** все официальные примеры Spring Test использовали бы парсинг строк. На практике `jsonPath` — основной инструмент.

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


> [!mcq]
>
> **Вопрос:** Чем `@MockBean` (Spring Boot Test) принципиально отличается от `@Mock` (Mockito) и почему добавление `@MockBean` влияет на скорость прогона test suite?
>
> ---
>
> #### A) `@MockBean` и `@Mock` — синонимы, разница только в пакете импорта — ❌ Неверно
>
> **Что на самом деле:** это разные инструменты с разной семантикой. `@Mock` создаёт mock-объект Mockito, который надо вручную подставить в зависимый класс через `@InjectMocks` (или `MockitoAnnotations.openMocks(this)`). `@MockBean` создаёт mock и **регистрирует его в Spring ApplicationContext**, заменяя реальный бин — все `@Autowired` зависимости получают именно mock.
>
> **Откуда путаница:** оба создают mock, оба работают через Mockito под капотом, имена похожи.
>
> **Если бы это было правдой:** не было бы влияния на кеш контекста, slice-тесты не нуждались бы в `@MockBean`. На практике именно различие в scope (JVM vs Spring container) определяет производительность.
>
> ---
>
> #### B) `@Mock` создаёт mock-объект уровня JVM (для unit-тестов БЕЗ Spring), `@MockBean` регистрирует mock в `ApplicationContext`, заменяя бин — каждый уникальный набор `@MockBean` создаёт новый context cache entry (поэтому `@MockBean` загрязняет кеш и замедляет test suite); `@Mock` использовать в чистых unit-тестах, `@MockBean` — в `@SpringBootTest`/slice-тестах когда нужно подменить зависимость в DI-графе — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring TestContext Framework кеширует `ApplicationContext` по «ключу»: комбинация аннотаций, профилей, `@TestPropertySource`, `@ContextConfiguration` и **набора `@MockBean`/`@SpyBean`**. Если в `TestA` указан `@MockBean EmailService`, а в `TestB` — `@MockBean PaymentService`, это **два разных контекста** (две full Spring start-up). При 50+ тестовых классах с уникальными `@MockBean` суммарное время может вырасти с 30 секунд до 10 минут.
>
> Mockito `@Mock` живёт только внутри JVM, активируется через `MockitoAnnotations.openMocks(this)` или `@ExtendWith(MockitoExtension.class)`. Не влияет на Spring совсем — потому что Spring о нём ничего не знает.
>
> **Пример:**
> ```java
> // Unit test без Spring:
> @ExtendWith(MockitoExtension.class)
> class OrderServiceTest {
>     @Mock OrderRepository repository;
>     @InjectMocks OrderService service;        // Mockito подставит mock вручную
> }
>
> // Slice test со Spring:
> @WebMvcTest(OrderController.class)
> class OrderControllerTest {
>     @MockBean OrderService service;           // Spring подставит mock в контроллер через DI
> }
> ```
>
> **Когда применять:**
> - `@Mock` — для unit-тестов класса в изоляции (быстро, без context).
> - `@MockBean` — для slice/`@SpringBootTest`, где нужно подменить бин в DI-графе.
> - В одном проекте обычно ОБА: бизнес-логика покрывается `@Mock`, integration-paths — `@MockBean`.
>
> **Подводные камни:**
> - Минимизируйте `@MockBean` через общий базовый класс (`abstract class BaseIntegrationTest`) — все наследники переиспользуют контекст.
> - Не путать с `@MockitoBean` (новый в Spring 6.2 — замена устаревшему `@MockBean`).
> - `@MockBean` сбрасывает mock после каждого теста — не нужно явно вызывать `reset(mock)`.
> - Mockito-mock через `@Mock` не сбрасывается между тестами по умолчанию (без extension) — может приводить к leak состояния.
>
> **Связанные вопросы:** [[Q10]] — `@SpyBean` (partial mock); [[Q13]] — кеш контекста и `@MockBean`.
>
> ---
>
> #### C) `@MockBean` работает только в unit-тестах без Spring, `@Mock` — в Spring-тестах — ❌ Неверно
>
> **Что на самом деле:** наоборот. `@MockBean` определена в `org.springframework.boot.test.mock.mockito` — она требует Spring контекста (без `@SpringBootTest`/slice-аннотации просто не сработает). `@Mock` из `org.mockito` работает где угодно, включая Spring-тесты, но требует ручной wiring через `@InjectMocks`.
>
> **Откуда путаница:** слово «Bean» иногда ассоциируется с «упрощённой версией» — а тут наоборот, «Bean» = «бин Spring».
>
> **Если бы это было правдой:** не было бы способа подменить бин в context — это сломало бы всю модель slice-тестов. На практике `@MockBean` — основной способ подмены в Spring Test.
>
> ---
>
> #### D) Использование `@MockBean` НЕ влияет на скорость теста — он переиспользует существующий контекст — ❌ Неверно
>
> **Что на самом деле:** `@MockBean` — один из ключевых факторов context cache invalidation. Spring рассматривает набор `@MockBean` как часть «отпечатка» контекста: разные наборы = разные кеш-entries = разные full Spring start-up. Логи `org.springframework.test.context.cache=DEBUG` покажут `cache.size`, `hitCount`, `missCount`, `parentCount`.
>
> **Откуда путаница:** локальный запуск одного теста быстрый, и эффект становится заметен только на CI с десятками классов.
>
> **Если бы это было правдой:** не было бы рекомендации Spring docs «минимизируйте `@MockBean`». На практике это один из главных source медленного CI.

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


> [!mcq]
>
> **Вопрос:** Что такое `@SpyBean` в Spring Boot Test и в каких сценариях он предпочтительнее `@MockBean`?
>
> ---
>
> #### A) `@SpyBean` — то же что `@MockBean`, просто с другим именем — ❌ Неверно
>
> **Что на самом деле:** `@SpyBean` создаёт **partial mock** (Mockito spy): сохраняет реальный бин и оборачивает его в spy, который по умолчанию делегирует вызовы реальному методу. `@MockBean` создаёт полностью искусственный mock — все методы возвращают defaults (`null`/`0`/`false`/empty collections) пока не настроены через `when().thenReturn()`.
>
> **Откуда путаница:** обе аннотации регистрируют бин в контекст через `BeanPostProcessor` (`MockitoPostProcessor`), оба используют Mockito.
>
> **Если бы это было правдой:** не было бы смысла иметь две аннотации. На практике разница в стратегии стаббинга кардинальная.
>
> ---
>
> #### B) `@SpyBean` запускает реальный код бина и одновременно мокирует все его методы — оба поведения активны параллельно — ❌ Неверно
>
> **Что на самом деле:** для каждого вызова применяется ЛИБО реальный метод, ЛИБО stub — не оба сразу. По умолчанию spy делегирует на реальный метод; если для метода настроен `doReturn(...).when(spy).method(...)`, то применяется stub. Параллельного исполнения нет, иначе семантика была бы неопределённой.
>
> **Откуда путаница:** spy кажется «двойным» — и слежение, и мок. Но «слежение» (verify) и «стаббинг» — разные операции, не параллельные.
>
> **Если бы это было правдой:** возникали бы side effects от реального метода даже при stub-е — нельзя было бы корректно мокировать поведение. На практике stub полностью замещает реальный вызов.
>
> ---
>
> #### C) `@SpyBean` оборачивает реальный бин в Mockito spy: по умолчанию делегирует вызовы реальной реализации, но позволяет переопределить отдельные методы через `doReturn(...).when(spyBean).method(...)`; применяется когда нужно протестировать реальную бизнес-логику но подменить один-два побочных эффекта (отправку email, внешний HTTP, time-dependent методы) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `@SpyBean` идеален для сценария «работает почти всё кроме одной точки»: представьте `OrderEventPublisher` с методами `publish()` и `sendEmail()`. Реальный `publish()` тестируется как есть, а `sendEmail()` мокается, чтобы не дёргать SMTP-сервер.
>
> **Важно про стаббинг spy:** используйте `doReturn/doThrow/doAnswer` форму, а не `when(...).thenReturn(...)`. Причина: `when(spy.method()).thenReturn(...)` СНАЧАЛА вызовет реальный метод (для оценки выражения), что может вызвать NPE или побочный эффект. `doReturn(x).when(spy).method(...)` не вызывает реальный метод вообще.
>
> **Пример:**
> ```java
> @SpringBootTest
> class OrderServiceIntegrationTest {
>
>     @SpyBean
>     private OrderEventPublisher publisher;
>
>     @Autowired
>     private OrderService service;
>
>     @Test
>     void create_whenEmailDown_doesNotPropagateError() {
>         // Мокаем только один метод spy
>         doThrow(new RuntimeException("Email down"))
>             .when(publisher).sendEmail(any());
>
>         // Остальные методы (например publish to Kafka) работают реально
>         assertThatCode(() -> service.create(new OrderRequest("CUST-1")))
>             .doesNotThrowAnyException();
>
>         verify(publisher).publish(any(OrderCreatedEvent.class));   // реальный
>         verify(publisher).sendEmail(any());                        // mock
>     }
> }
> ```
>
> **Когда применять:**
> - Подменить «дорогой» побочный эффект (email, SMS, HTTP-вызов внешнего API).
> - Time-dependent методы (`Clock.now()` → подменить на фиксированный момент).
> - Проверить вызовы AOP-методов (`@Async`, `@Transactional` proxies) — spy сохраняет proxy.
> - Логирование/аудит — реальный код пишет в log, тест проверяет факт вызова.
>
> **Подводные камни:**
> - `final` методы Mockito не может перекрыть без `mockito-inline` (в Spring Boot 3.x — by default через `org.mockito:mockito-core` с inline mock-maker).
> - Spy на `@Component` с AOP-обёрткой: иногда spy оборачивается не на сам бин, а на CGLIB proxy — verify может не сработать. Решение: `@SpyBean(reset = MockReset.AFTER)`.
> - `@SpyBean` тоже загрязняет кеш контекста, как `@MockBean`.
> - При тестировании self-invocation внутри класса (метод A вызывает метод B того же класса напрямую, без proxy) — spy не перехватит B.
>
> **Связанные вопросы:** [[Q9]] — `@MockBean` vs `@Mock`; [[Q13]] — кеш контекста и `@SpyBean`.
>
> ---
>
> #### D) `@SpyBean` работает только с final-классами и интерфейсами — ❌ Неверно
>
> **Что на самом деле:** ровно наоборот в части intent — `@SpyBean` оборачивает экземпляр любого Spring-бина. Ограничение Mockito: spy на final классе требует `mockito-inline` mock-maker. В Spring Boot 3.x это поведение по умолчанию, поэтому работает почти всегда. С интерфейсами spy формально не имеет смысла (нет реализации для делегирования) — нужна конкретная реализация.
>
> **Откуда путаница:** возможно, конфьюзится с ограничением «final классы — проблема для proxy».
>
> **Если бы это было правдой:** нельзя было бы делать spy для `@Service` (обычные классы, не final, не интерфейс) — а это самый частый use-case.

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


> [!mcq]
>
> **Вопрос:** Что такое `@TestConfiguration` и чем она принципиально отличается от обычной `@Configuration`?
>
> ---
>
> #### A) `@TestConfiguration` — алиас `@Configuration` с другим именем, никаких отличий в поведении нет — ❌ Неверно
>
> **Что на самом деле:** ключевое отличие — `@TestConfiguration` НЕ участвует в component scanning основного приложения. Класс с `@TestConfiguration` подбирается ТОЛЬКО когда явно импортирован через `@Import(TestConfig.class)` или объявлен вложенным static-классом внутри тест-класса.
>
> **Откуда путаница:** обе мета-аннотации имеют `@Configuration` внутри, обе позволяют объявлять `@Bean`-методы.
>
> **Если бы это было правдой:** test-конфигурации попадали бы в production контекст и переопределяли реальные бины. На практике именно discoverability разница имеет ключевое значение для изоляции тестов.
>
> ---
>
> #### B) `@TestConfiguration` нельзя использовать со slice-тестами (`@WebMvcTest`, `@DataJpaTest`) — ❌ Неверно
>
> **Что на самом деле:** `@TestConfiguration` отлично работает со slice-тестами — обычно именно для них и применяется. Slice-аннотации (`@WebMvcTest`) исключают большинство `@Component` из контекста; `@TestConfiguration` через `@Import` или вложенный класс добавляет нужные бины обратно. Пример: добавить custom `WebMvcConfigurer` или подменить `Clock` фиксированным значением.
>
> **Откуда путаница:** slice-тесты часто описывают как «минимальный контекст», и кажется что добавление конфигурации нарушает изоляцию.
>
> **Если бы это было правдой:** не было бы способа добавить тестовые бины в slice-тест — а это базовая потребность (custom validators, фиксированный Clock).
>
> ---
>
> #### C) `@TestConfiguration` обязательно должна быть в `src/test/java`, иначе приложение не запустится — ❌ Неверно
>
> **Что на самом деле:** физически Spring не проверяет classpath-путь — `@TestConfiguration` можно положить и в `src/main/java`. Но это плохой стиль: продакшен-код видит test-only артефакты. Конвенция — держать в `src/test/java`. При component scan основного приложения `@TestConfiguration` всё равно исключается, потому что класс не помечен `@Component` через стандартный фильтр.
>
> **Откуда путаница:** location в `src/test/java` — best practice, и кажется обязательной.
>
> **Если бы это было правдой:** `@TestConfiguration` в `src/main` ломала бы продакшен. На практике она просто инертна в production-контексте.
>
> ---
>
> #### D) `@TestConfiguration` — мета-аннотация на основе `@Configuration` для определения **тестовых бинов**: НЕ участвует в основном component scanning приложения, активируется только через `@Import(TestConfig.class)` или как вложенный static-класс внутри тест-класса; применяется для добавления/переопределения бинов (mock-инфраструктуры, фиксированный Clock, упрощённая SecurityFilterChain, инструментальные ObjectMapper) без затрагивания production-конфигурации — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Boot включает `@TestConfiguration` через `TypeExcludeFilter` (`SpringBootTest` сканирует пакет `@SpringBootApplication`, но `@TestConfiguration` отфильтровывается из этого scan-а). Активация — два пути:
> 1. **`@Import(TestConfig.class)`** на тест-классе — явная декларация.
> 2. **Вложенный `static` класс** внутри тест-класса с `@TestConfiguration` — автоматически подхватывается.
>
> Внутри `@TestConfiguration` можно объявлять `@Bean`-методы. Если бин с тем же типом/именем уже есть в контексте — будет конфликт; чтобы переопределить production-бин, добавьте `@Primary` или `spring.main.allow-bean-definition-overriding=true` (не рекомендуется).
>
> **Пример:**
> ```java
> @TestConfiguration
> public class TestClockConfig {
>     @Bean
>     @Primary  // перекрывает production Clock
>     Clock fixedClock() {
>         return Clock.fixed(Instant.parse("2026-05-15T12:00:00Z"), ZoneOffset.UTC);
>     }
> }
>
> @SpringBootTest
> @Import(TestClockConfig.class)
> class OrderTimestampTest {
>     @Autowired Clock clock;
>     // clock возвращает фиксированный момент
> }
> ```
>
> **Когда применять:**
> - Подменить инфраструктурные бины (Clock, RandomGenerator, IdGenerator).
> - Тестовый SecurityFilterChain (отключить CSRF, разрешить всё).
> - Добавить mock-инструментацию (`@RecordApplicationEvents`, custom `MeterRegistry`).
> - Подменить external client (вместо реального REST-клиента — stub возвращающий fixture).
>
> **Подводные камни:**
> - Без `@Import` или вложенности `@TestConfiguration` НЕ активируется — частая ошибка: создать класс и удивляться, что бин не подставлен.
> - Изменение `@Bean` методов влияет на кеш контекста — два разных `@TestConfiguration` = два разных context cache entries.
> - При `proxyBeanMethods = false` методы `@Bean` друг друга НЕ вызывают через CGLIB — это влияет на singleton-семантику.
> - `@TestConfiguration` inside `@WebMvcTest` нужно объявлять через `@Import`, а не как вложенный класс — иначе slice может его не подхватить.
>
> **Связанные вопросы:** [[Q3]] — test slices; [[Q12]] — `@DirtiesContext` для context override.

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


> [!mcq]
>
> **Вопрос:** Что делает аннотация `@DirtiesContext`, какие у неё `classMode`/`methodMode` опции и почему её надо применять с осторожностью?
>
> ---
>
> #### A) `@DirtiesContext` помечает `ApplicationContext` как «грязный» — Spring TestContext Framework пересоздаёт контекст согласно настройкам `classMode` (`AFTER_CLASS` default, `BEFORE_CLASS`, `AFTER_EACH_TEST_METHOD`, `BEFORE_EACH_TEST_METHOD`) / `methodMode` (`AFTER_METHOD`, `BEFORE_METHOD`); применяется когда тест необратимо изменяет состояние singleton-бинов, схему БД, environment, конфигурацию контекста; **должна использоваться с осторожностью**: каждое пересоздание контекста = full Spring start-up (секунды-десятки секунд), массовое использование делает CI-пайплайн минутами медленнее — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `@DirtiesContext` сигнализирует Spring TestContext Cache: «не переиспользуй этот context для следующих тестов, выкини его». Реализация: `DirtiesContextTestExecutionListener` после теста вызывает `TestContextManager.markApplicationContextDirty(...)` → cache evict → следующий тест с тем же ключом получает новый context.
>
> **Опции для класса (`classMode`):**
> - `AFTER_CLASS` (default) — пересоздать после всех тестов класса.
> - `BEFORE_CLASS` — пересоздать перед началом.
> - `AFTER_EACH_TEST_METHOD` — пересоздать после КАЖДОГО теста.
> - `BEFORE_EACH_TEST_METHOD` — пересоздать перед каждым.
>
> **Опции для метода (`methodMode`):** `AFTER_METHOD` (default), `BEFORE_METHOD`.
>
> **Когда оправдано:**
> - Тест изменяет схему БД через DDL без отката (`CREATE TABLE`, `ALTER`).
> - Тест меняет environment variables / system properties, влияющие на бины.
> - Тест изменяет внутреннее состояние singleton-бина без способа его восстановить.
> - Перед serialization/cache тестами нужен «свежий» контекст.
>
> **Пример:**
> ```java
> @SpringBootTest
> @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
> class SchemaMigrationTest {
>     @Test
>     void migrateToV2_addsColumn() {
>         // Тест меняет схему БД через Flyway/Liquibase
>     }
> }
> ```
>
> **Когда НЕ нужен (антипаттерны):**
> - Очистка данных таблицы между тестами — лучше `@Transactional` + rollback.
> - Изоляция state бизнес-логики — лучше `@BeforeEach { repository.deleteAll(); }`.
> - Изоляция БД между тестами — лучше Testcontainers с fresh DB.
> - Тестирование mock-ов — `@MockBean` автоматически сбрасывается после каждого теста.
>
> **Подводные камни:**
> - На больших test suite массовое `@DirtiesContext(AFTER_EACH_TEST_METHOD)` превращает 30-секундный прогон в 10 минут.
> - Не действует на `@SpringBootTest(webEnvironment = RANDOM_PORT)` если Tomcat уже занял порт — может быть `BindException` при retake.
> - Несовместим с параллельным запуском тестов внутри класса (`@Execution(CONCURRENT)`) — пересоздание может race с другими методами.
>
> **Связанные вопросы:** [[Q11]] — `@TestConfiguration` для тестовых бинов; [[Q13]] — кеш контекста; [[Q15]] — Testcontainers как альтернатива.
>
> ---
>
> #### B) `@DirtiesContext` отключает Spring TestContext Cache для всего test suite — ❌ Неверно
>
> **Что на самом деле:** `@DirtiesContext` влияет ТОЛЬКО на конкретный контекст, помеченный аннотацией. Остальные контексты в cache не затрагиваются. Полностью отключить cache можно через системное свойство `spring.test.context.cache.maxSize=0`, но это используется крайне редко (debug).
>
> **Откуда путаница:** «dirty» звучит как «invalidate all».
>
> **Если бы это было правдой:** одно использование `@DirtiesContext` в проекте парализовало бы весь CI. На практике cache продолжает работать для других тестов.
>
> ---
>
> #### C) `@DirtiesContext` нужно ставить на каждый интеграционный тест для гарантии чистоты — ❌ Неверно
>
> **Что на самом деле:** это антипаттерн. Spring предоставляет более лёгкие механизмы изоляции: `@Transactional` (откат после теста), `@Sql` (cleanup-скрипты), `@BeforeEach`. `@DirtiesContext` — крайняя мера, когда другие способы недоступны. Spring Boot reference прямо рекомендует minimize.
>
> **Откуда путаница:** разработчики, столкнувшиеся с flaky-тестом из-за state leak, часто добавляют `@DirtiesContext` как «безопасную дефолтную меру» — а потом удивляются длительности CI.
>
> **Если бы это было правдой:** test suite на 200 тестах работал бы 30+ минут. На практике хорошо настроенные проекты не имеют `@DirtiesContext` вообще.
>
> ---
>
> #### D) `@DirtiesContext` работает только с PostgreSQL, для других БД нужны другие подходы — ❌ Неверно
>
> **Что на самом деле:** `@DirtiesContext` работает на уровне Spring ApplicationContext, а НЕ на уровне БД. Это аннотация Spring TestContext Framework, ей всё равно какая БД используется (или нет ли её вовсе). Она пересоздаёт контекст — что косвенно может пересоздать DataSource и в нём pool, но к конкретной БД не привязана.
>
> **Откуда путаница:** часто `@DirtiesContext` используется в DB-тестах, где DDL изменения требуют пересоздания контекста. Это создаёт иллюзию связи.
>
> **Если бы это было правдой:** не было бы возможности использовать аннотацию в тестах без БД (например, конфигурация-онли тесты). На практике она универсальна.

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


> [!mcq]
>
> **Вопрос:** Как Spring TestContext Framework кеширует `ApplicationContext` между тестами и какие факторы инвалидируют кеш-entry?
>
> ---
>
> #### A) Кешируется один контекст на весь test suite — поэтому добавление `@MockBean` не влияет на время прогона — ❌ Неверно
>
> **Что на самом деле:** кешируется несколько контекстов одновременно (LRU cache, default `maxSize = 32`). Каждый уникальный «отпечаток» конфигурации создаёт отдельный entry. Добавление `@MockBean` — один из главных факторов инвалидации, потому что разные наборы mock-бинов меняют ключ кеша → создаётся новый context из scratch.
>
> **Откуда путаница:** разработчик видит, что один тест запускается быстро (контекст переиспользуется) и распространяет это на весь suite.
>
> **Если бы это было правдой:** не было бы рекомендации Spring docs «минимизируйте уникальные конфигурации тестов». На практике это главный bottleneck CI.
>
> ---
>
> #### B) Spring TestContext Framework держит LRU-кеш `ApplicationContext` (по умолчанию `maxSize = 32`), ключ кеша — комбинация: `@ContextConfiguration`-классы/locations, активные профили, `@TestPropertySource`, набор `@MockBean`/`@SpyBean`, `webEnvironment`, `@DirtiesContext`-флаг; уникальный ключ = новый context start-up; для оптимизации — выносить общие `@MockBean` в abstract base class, чтобы наследники переиспользовали один контекст; мониторинг — `logging.level.org.springframework.test.context.cache=DEBUG` показывает `hitCount/missCount/size` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Внутри `TestContextManager` живёт `DefaultCacheAwareContextLoaderDelegate` → он обращается к `DefaultContextCache`. Cache реализован как `Map<MergedContextConfiguration, ApplicationContext>` с LRU-eviction. `MergedContextConfiguration` — это «отпечаток»: hash от всех конфигурационных аспектов теста.
>
> **Факторы, формирующие ключ:**
> - `@SpringBootConfiguration` / `@Configuration`-классы.
> - Активные профили (`@ActiveProfiles`).
> - `@TestPropertySource` (значения properties).
> - `@MockBean` / `@SpyBean` — типы и имена бинов.
> - `webEnvironment` (`MOCK`/`RANDOM_PORT`/`NONE`).
> - Locations (XML/Groovy files).
> - Parent context, если есть.
>
> **Стратегии оптимизации:**
> ```java
> @SpringBootTest
> abstract class BaseIntegrationTest {
>     @MockBean EmailService emailService;
>     @MockBean PaymentService paymentService;
> }
>
> class OrderTest extends BaseIntegrationTest { /* same context */ }
> class UserTest extends BaseIntegrationTest { /* same context — cache hit */ }
> class ReviewTest extends BaseIntegrationTest { /* same context — cache hit */ }
> ```
>
> **Когда применять знание:**
> - Анализ медленного CI: «почему тесты идут 8 минут?» → проверить `cache.size` через debug-логи.
> - Рефакторинг test base class: вынести общие `@MockBean` для cache reuse.
> - Принятие решения о `@DirtiesContext` — каждый раз = полный startup, обходится дорого.
>
> **Подводные камни:**
> - `maxSize` по умолчанию 32 — на больших проектах LRU начинает выталкивать кеш-entries; следующий тест с тем же ключом снова делает full startup. Можно увеличить через `-Dspring.test.context.cache.maxSize=64`.
> - Каждый `@DirtiesContext` evict-ит entry — но не сам кеш.
> - Если у вас 50 уникальных конфигураций — все 50 контекстов будут поднимать JVM (heap, threads, BD-pools).
> - Не путать с Mockito mock reset — это другой механизм, работает после каждого теста независимо от cache.
>
> **Связанные вопросы:** [[Q9]] — `@MockBean` влияет на cache; [[Q12]] — `@DirtiesContext`; [[Q11]] — `@TestConfiguration` тоже формирует ключ.
>
> ---
>
> #### C) Каждый тестовый класс ВСЕГДА получает новый `ApplicationContext` — кеш отключён по умолчанию — ❌ Неверно
>
> **Что на самом деле:** наоборот — кеш ВКЛЮЧЁН по умолчанию. Это одна из главных оптимизаций Spring Test, без которой test suite на 200+ классах был бы катастрофически медленным. Кеш формируется на основе «отпечатка» конфигурации: два класса с одинаковыми аннотациями = один общий context.
>
> **Откуда путаница:** разработчик видит время старта контекста и предполагает, что это происходит для каждого теста.
>
> **Если бы это было правдой:** test suite на 50 классах с `@SpringBootTest` занимал бы 50 × 5 секунд = 4+ минуты только на startup. На практике общий startup ~5 секунд (один контекст), а тесты выполняются мгновенно.
>
> ---
>
> #### D) Кеш контекста сохраняется на диск между билдами CI — ❌ Неверно
>
> **Что на самом деле:** кеш живёт в памяти JVM текущего процесса. Между билдами CI (новый процесс) — старт с нуля. Spring не сериализует `ApplicationContext` на диск, потому что бины часто содержат native-resources (соединения, потоки, прокси), которые нельзя сериализовать корректно.
>
> **Откуда путаница:** в проектах с Gradle Build Cache можно встретить кеш test-output на диске — но это другой уровень (результаты тестов, а не сам Spring контекст).
>
> **Если бы это было правдой:** CI-пайплайны были бы намного быстрее. На практике каждый билд = full Spring startup.

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


> [!mcq]
>
> **Вопрос:** Какие инструменты Spring Security Test использовать для тестирования аутентификации/авторизации (`@WithMockUser`, `with(user())`, `with(jwt())`) и в чём их различия?
>
> ---
>
> #### A) Можно тестировать Security только через реальный auth-сервер (Keycloak в Testcontainers) — `@WithMockUser` не даёт реального покрытия — ❌ Неверно
>
> **Что на самом деле:** `@WithMockUser` и аналоги официально рекомендуются Spring Security как стандартный способ unit/slice-тестирования. Они корректно симулируют `SecurityContext` (заполняют `Authentication` с нужными ролями/принципалом), и Spring Security обрабатывает запрос ровно как в production. Реальный Keycloak имеет смысл только для end-to-end проверок OAuth flow (login, redirect, token exchange) — в 90% случаев это излишне.
>
> **Откуда путаница:** mock пользователя кажется «недостаточно реальным» для security-чувствительных проверок.
>
> **Если бы это было правдой:** test suite на каждый эндпоинт занимал бы минуты — Keycloak start-up небыстрый. На практике `@WithMockUser` — быстро и достаточно.
>
> ---
>
> #### B) `@WithMockUser` отключает Spring Security полностью — фильтры не применяются — ❌ Неверно
>
> **Что на самом деле:** `@WithMockUser` НЕ отключает Security — наоборот, заполняет `SecurityContext` mock-аутентификацией ДО выполнения запроса. Все фильтры (`SecurityFilterChain`, `FilterSecurityInterceptor`, `MethodSecurityInterceptor`) работают как обычно и принимают решения на основе ролей mock-пользователя.
>
> **Откуда путаница:** в простых тестах `@WithMockUser(roles = "USER")` «магически» делает запрос успешным, что может выглядеть как обход Security.
>
> **Если бы это было правдой:** тесты не могли бы покрывать сценарии `401`/`403` — а это базовая возможность `@WithMockUser`. На практике именно фильтры решают, что вернуть.
>
> ---
>
> #### C) Подключить `spring-security-test`; `@WithMockUser(roles = "ADMIN")` — на тестовый метод/класс для симуляции аутентифицированного пользователя; `mockMvc.with(user("alice").roles("USER"))` — request-level подмена в одном запросе; `mockMvc.with(jwt().jwt(j -> j.claim("scope", "read")))` — для OAuth2/JWT эндпоинтов; `mockMvc.with(csrf())` — для POST/PUT/DELETE (CSRF-токен); для негативных проверок (`401`/`403`) — обычный запрос без `@WithMockUser` / с недостаточной ролью — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `spring-security-test` (gradle: `testImplementation 'org.springframework.security:spring-security-test'`) даёт три уровня инструментов:
>
> 1. **Класс/метод аннотации** (`@WithMockUser`, `@WithUserDetails`, `@WithAnonymousUser`, `@WithSecurityContext`) — заполняют `SecurityContext` через TestExecutionListener до начала теста.
> 2. **Request-level postprocessors** (`SecurityMockMvcRequestPostProcessors`): `user(...)`, `jwt()`, `oauth2Login()`, `oidcLogin()`, `csrf()` — применяются через `.with(...)` к конкретному запросу MockMvc.
> 3. **Matchers** (`SecurityMockMvcResultMatchers`): `authenticated()`, `unauthenticated()`, `withAuthentication(...)` — для проверки итогового состояния.
>
> **Пример комплексного теста:**
> ```java
> @WebMvcTest(OrderController.class)
> class OrderSecurityTest {
>
>     @Autowired MockMvc mockMvc;
>
>     @Test
>     void getOrders_anonymous_returns401() throws Exception {
>         mockMvc.perform(get("/api/orders"))
>             .andExpect(status().isUnauthorized());
>     }
>
>     @Test
>     @WithMockUser(roles = "USER")
>     void getOrders_user_returns200() throws Exception {
>         mockMvc.perform(get("/api/orders"))
>             .andExpect(status().isOk());
>     }
>
>     @Test
>     void deleteOrder_jwt_withScope_returns204() throws Exception {
>         mockMvc.perform(delete("/api/orders/1")
>                 .with(jwt().jwt(j -> j.claim("scope", "orders:delete")))
>                 .with(csrf()))
>             .andExpect(status().isNoContent());
>     }
> }
> ```
>
> **Когда применять:**
> - Все role-based endpoints (`@PreAuthorize`, `hasRole(...)`).
> - JWT-authenticated REST API (`@AuthenticationPrincipal Jwt`).
> - OAuth2 client/resource server endpoints.
> - CSRF-protected POST/PUT/DELETE.
> - Method-level security (`@Secured`, `@PreAuthorize`) — те же инструменты работают.
>
> **Подводные камни:**
> - `@WithMockUser` НЕ подходит для JWT-эндпоинтов, ожидающих `Jwt`-principal: используйте `with(jwt())`.
> - CSRF включён по умолчанию — без `with(csrf())` POST/PUT/DELETE вернут 403. Часто забывают.
> - В `@WebMvcTest` security AutoConfiguration может не подхватиться — нужно `@WebMvcTest(controllers = X.class, includeFilters = ...)` или вручную `@Import(SecurityConfig.class)`.
> - `@WithUserDetails("alice")` требует `UserDetailsService` в контексте — иначе `UsernameNotFoundException`.
> - При параллельных тестах `SecurityContext` уровня класса может протечь через `SecurityContextHolder.STRATEGY_THREADLOCAL`.
>
> **Связанные вопросы:** [[Q7]] — MockMvc; [[Q4]] — `@WebMvcTest` slice; [[Q11]] — `@TestConfiguration` для test SecurityFilterChain.
>
> ---
>
> #### D) `with(jwt())` работает только если в контексте запущен реальный OAuth2-сервер — ❌ Неверно
>
> **Что на самом деле:** `with(jwt())` создаёт mock-JWT и регистрирует его в `SecurityContext` БЕЗ обращения к реальному authorization server. Это in-process подмена `Authentication`. JWK validation, signature checks — всё пропускается, потому что mock-JWT помечен как уже валидный. Для real-flow тестирования нужен `@SpringBootTest` + Keycloak/WireMock.
>
> **Откуда путаница:** JWT в production требует authorization server для валидации — кажется, что и в тестах будет так.
>
> **Если бы это было правдой:** slice-тесты `@WebMvcTest` не работали бы со Security — а они отлично работают именно благодаря тому, что `with(jwt())` не требует реального сервера.

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


> [!mcq]
>
> **Вопрос:** Как правильно интегрировать Testcontainers со Spring Boot 3.1+ и какие преимущества даёт `@ServiceConnection` по сравнению с `@DynamicPropertySource`?
>
> ---
>
> #### A) Testcontainers и Spring Boot несовместимы — нужно вручную поднимать БД через `docker-compose` — ❌ Неверно
>
> **Что на самом деле:** Testcontainers — стандарт de-facto для integration-тестов со Spring Boot. С версии 3.1 интеграция стала нативной через `@ServiceConnection` — никаких `docker-compose`, никаких `@DynamicPropertySource`. Достаточно объявить контейнер как `@Bean` и пометить `@ServiceConnection`.
>
> **Откуда путаница:** в legacy-проектах часто встречается ручной setup через `docker-compose` и externally-managed контейнеры.
>
> **Если бы это было правдой:** не было бы официального `spring-boot-testcontainers` модуля и stop-the-show поддержки на SpringIO talks. На практике это первоклассный способ.
>
> ---
>
> #### B) `@DynamicPropertySource` устарел — нельзя использовать в Spring Boot 3+ — ❌ Неверно
>
> **Что на самом деле:** `@DynamicPropertySource` НЕ deprecated — он остаётся валидным API для случаев, когда нужна полная гибкость (нестандартные properties, кастомные контейнеры без поддержки `@ServiceConnection`). `@ServiceConnection` — сахар поверх него для типовых случаев (PostgreSQL, Redis, Kafka, MongoDB, RabbitMQ и др.).
>
> **Откуда путаница:** `@ServiceConnection` рекомендуется как best practice, что создаёт впечатление вытеснения `@DynamicPropertySource`.
>
> **Если бы это было правдой:** разработчикам пришлось бы массово рефакторить legacy-проекты. На практике `@DynamicPropertySource` сохранён для обратной совместимости и нестандартных случаев.
>
> ---
>
> #### C) `@ServiceConnection` создаёт постоянное TCP-соединение между тестом и контейнером — закрытие убивает все следующие тесты — ❌ Неверно
>
> **Что на самом деле:** `@ServiceConnection` НЕ управляет соединениями — он управляет CONFIGURATION. Аннотация говорит Spring Boot: «возьми host/port/credentials этого контейнера и подставь в `spring.datasource.*` (или `spring.redis.*`, и т.д.)». Реальный pool соединений (`HikariCP`, Lettuce, и т.д.) создаётся Spring как обычно.
>
> **Откуда путаница:** слово «Connection» вводит в заблуждение — это про подключение конфигурации, а не TCP.
>
> **Если бы это было правдой:** Spring Boot не имел бы смысла поддерживать аннотацию — managed lifecycle конфликтовал бы с pool management. На практике это чисто конфигурационный механизм.
>
> ---
>
> #### D) `@ServiceConnection` (Spring Boot 3.1+) автоматически связывает Testcontainer с Spring Boot autoconfiguration: для контейнера типа `PostgreSQLContainer<?>` Spring подставит `spring.datasource.url/username/password` без явного `@DynamicPropertySource`; объявляется как `@Bean` в `@TestConfiguration`, импортируется через `@Import(TestcontainersConfig.class)` или регистрируется через `@Bean static` в тест-классе; поддерживает PostgreSQL/MySQL/Redis/Kafka/RabbitMQ/MongoDB/Elasticsearch и др.; преимущества — меньше boilerplate, типобезопасность, переиспользование контейнера между тестами (singleton-pattern) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Под капотом `@ServiceConnection` использует `ContainerConnectionSource` + `ConnectionDetailsFactory`. Каждый тип контейнера имеет свою фабрику: `PostgresContainerConnectionDetailsFactory` для PostgreSQL, `KafkaContainerConnectionDetailsFactory` для Kafka и т.д. Они умеют извлекать host/port/credentials из контейнера и создавать соответствующий `ConnectionDetails`-бин, который Spring Boot autoconfiguration предпочитает обычным properties.
>
> **Полная конфигурация:**
> ```java
> // src/test/java/.../TestcontainersConfig.java
> @TestConfiguration(proxyBeanMethods = false)
> public class TestcontainersConfig {
>
>     @Bean
>     @ServiceConnection
>     PostgreSQLContainer<?> postgres() {
>         return new PostgreSQLContainer<>("postgres:16-alpine")
>             .withReuse(true);  // singleton — не пересоздавать между тестами
>     }
>
>     @Bean
>     @ServiceConnection
>     KafkaContainer kafka() {
>         return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
>     }
> }
>
> @SpringBootTest
> @Import(TestcontainersConfig.class)
> class UserRepositoryIntegrationTest {
>     @Autowired UserRepository repository;
>
>     @Test
>     void persistAndFind() {
>         User u = repository.save(new User("alice"));
>         assertThat(repository.findById(u.getId())).isPresent();
>     }
> }
> ```
>
> **Для container reuse между тестами:** `.withReuse(true)` + установить `testcontainers.reuse.enable=true` в `~/.testcontainers.properties`. Это драматически ускоряет CI — контейнер живёт между билдами (запускается один раз).
>
> **Когда применять:**
> - Integration-тесты с реальной БД (postgres, MySQL).
> - Тесты с Kafka producers/consumers.
> - Тесты Redis (cache, distributed lock).
> - Elasticsearch search-queries.
> - Любой external dependency, для которого есть Testcontainer.
>
> **Подводные камни:**
> - Docker должен быть запущен на CI runner-е (требует privileged mode или docker socket mount).
> - При параллельных тестах в одной JVM container reuse конфликтует с `@DirtiesContext` — pool может попытаться connect к убитому контейнеру.
> - `@ServiceConnection` не работает для самописных контейнеров без `*ConnectionDetailsFactory` — для них нужен `@DynamicPropertySource`.
> - Тесты, использующие `@ServiceConnection`, попадают в свой context cache entry — не смешивать с `@SpringBootTest` без контейнеров.
> - Image pull при первом запуске может быть медленным — на CI лучше pre-pull через docker pre-step.
>
> **Связанные вопросы:** [[Q1]] — `@SpringBootTest` базовая; [[Q5]] — `@DataJpaTest` (часто комбинируется); [[Q13]] — кеш контекста с Testcontainers.

---

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
