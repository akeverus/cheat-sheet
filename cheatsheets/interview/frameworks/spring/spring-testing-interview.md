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
updated: "2026-04-25"
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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Что такое `@DataJpaTest` и как настроить? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Какие ещё test slices есть в Spring Boot? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Что такое `MockMvc` и как им пользоваться? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как тестировать REST endpoint с JSON через MockMvc? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Чем `@MockBean` отличается от `@Mock` из Mockito? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Что такое `@SpyBean` и когда нужен? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Что такое `@TestConfiguration` и зачем нужна? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Что такое `@DirtiesContext` и когда использовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Как Spring Boot кеширует ApplicationContext в тестах? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как тестировать Spring Security (аутентификацию/авторизацию)? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Как использовать Testcontainers с Spring Boot? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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

---

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Spring Boot](spring-boot-interview.md) — автоконфигурация, `@SpringBootApplication`, production features ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
