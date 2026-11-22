---
title: "Вопросы на собеседовании: REST-клиенты Spring"
description: "HTTP-клиенты Spring: RestTemplate (deprecation в документации 7.0, @Deprecated с 7.1), RestClient (Spring 6.1), WebClient, @HttpExchange декларативный интерфейс"
tags:
  - interview
  - frameworks
  - spring-rest-client-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring REST Clients"
  - "Spring REST Client interview"
prerequisites:
  - "[[spring-rest]]"
next: []
updated: "2026-10-07"
---
# Вопросы на собеседовании: REST-клиенты Spring

В Spring существует три основных HTTP-клиента: `RestTemplate` (классический синхронный клиент, deprecation в документации Spring 7.0, аннотация `@Deprecated` с 7.1), `WebClient` (реактивный, Spring 5+), и `RestClient` (новый синхронный, Spring 6.1). На собеседованиях проверяют понимание различий, миграцию с RestTemplate и декларативный подход через `@HttpExchange`.

Дата последнего обновления: 2026-10-07

## Полезные ссылки

### Официальная документация

- [Spring RestClient](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#rest-client) — документация RestClient
- [Baeldung: Spring RestClient](https://www.baeldung.com/spring-boot-restclient) — руководство по RestClient
- [Baeldung: HTTP Interface (@HttpExchange)](https://www.baeldung.com/spring-6-http-interface) — декларативные HTTP-клиенты

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Сравнение клиентов**
- [Q1. (!) Какие HTTP-клиенты есть в Spring и чем они отличаются?](#q1--какие-http-клиенты-есть-в-spring-и-чем-они-отличаются)
- [Q2. Почему RestTemplate перевели в maintenance mode?](#q2-почему-resttemplate-перевели-в-maintenance-mode)

**RestClient (Spring 6.1)**
- [Q3. (!) Как работает RestClient?](#q3--как-работает-restclient)
- [Q4. Чем retrieve() отличается от exchange()?](#q4-чем-retrieve-отличается-от-exchange)
- [Q5. Как обрабатывать ошибки в RestClient?](#q5-как-обрабатывать-ошибки-в-restclient)
- [Q6. Как десериализовать коллекцию через RestClient?](#q6-как-десериализовать-коллекцию-через-restclient)

**WebClient**
- [Q7. (!) Когда использовать WebClient вместо RestClient?](#q7--когда-использовать-webclient-вместо-restclient)
- [Q8. Как сделать синхронный вызов через WebClient?](#q8-как-сделать-синхронный-вызов-через-webclient)

**@HttpExchange — декларативный клиент**
- [Q9. (!) Что такое @HttpExchange и как им пользоваться?](#q9--что-такое-httpexchange-и-как-им-пользоваться)
- [Q10. Какие аннотации параметров поддерживает @HttpExchange?](#q10-какие-аннотации-параметров-поддерживает-httpexchange)
- [Q11. Как подключить @HttpExchange к RestClient vs WebClient?](#q11-как-подключить-httpexchange-к-restclient-vs-webclient)

**Конфигурация и best practices**
- [Q12. Как настроить таймауты, базовый URL и заголовки?](#q12-как-настроить-таймауты-базовый-url-и-заголовки)
- [Q13. Как мигрировать с RestTemplate на RestClient?](#q13-как-мигрировать-с-resttemplate-на-restclient)
- [Q14. Как спроектировать исходящий HTTP-вызов с ограничением времени, повторами и наблюдаемостью?](#q14-как-спроектировать-исходящий-http-вызов-с-ограничением-времени-повторами-и-наблюдаемостью)

---

## Q1. (!) Какие HTTP-клиенты есть в Spring и чем они отличаются?

В Spring три HTTP-клиента, и различает их в первую очередь **модель выполнения** (синхронная vs реактивная) и **поколение API**. `RestTemplate` — классика на доживании, `RestClient` — современный синхронный клиент, `WebClient` — реактивный.

| | `RestTemplate` | `RestClient` | `WebClient` |
|---|---|---|---|
| Версия | Spring 3+ | Spring 6.1+ | Spring 5+ (WebFlux) |
| Стиль | Синхронный | Синхронный fluent | Реактивный |
| Статус | Maintenance mode в 6.x, deprecation в документации 7.0, `@Deprecated` с 7.1 | Актуальный | Актуальный |
| Стиль API | Методы перегрузки | Fluent builder chain | Reactive (Mono/Flux) |
| Зависимость | spring-web | spring-web | spring-webflux |
| Применение | Legacy код | Новые Spring 6 проекты | Реактивный стек |

Ключевая мысль: `RestClient` создан как современная синхронная замена `RestTemplate` — он берёт fluent-API от `WebClient`, но работает блокирующе, без реактивных типов. Поэтому он может переиспользовать настройки старого `RestTemplate` (конвертеры, фабрику запросов, интерсепторы).

```mermaid
graph LR
    RT["RestTemplate<br/>deprecation в docs 7.0<br/>@Deprecated с 7.1"] -->|"заменяется; отдаёт конвертеры,<br/>фабрику запросов, интерсепторы"| RC["RestClient<br/>Spring 6.1"]
    WC["WebClient<br/>reactive"] -.->|"заимствован только стиль fluent-API"| RC
```

**Как выбрать:**
- Новый проект на Spring 6.1+, классический блокирующий стек (Spring MVC) → **RestClient**.
- Spring WebFlux, высокая конкурентность, нужны `Mono`/`Flux` → **WebClient**.
- Legacy-код, который ещё не мигрировали → **RestTemplate** (deprecation объявлена в справочной документации 7.0, аннотация `@Deprecated` появилась в 7.1).

---

## Q2. Почему RestTemplate перевели в maintenance mode?

Причина не в том, что он плохо работает, а в том, что его API родом из эпохи Java 5 и плохо стареет. Накопились дизайн-проблемы, которые невозможно исправить, не сломав обратную совместимость:

- **Перегрузка методов вместо fluent-API.** Для одной и той же операции есть несколько методов: `getForObject()`, `getForEntity()`, `exchange()`. Запомнить, какой когда нужен, тяжело, а сигнатуры с varargs и `Object...` легко перепутать.
- **Сложная расширяемость.** Добавить свою логику (например, единую обработку статусов) неудобно — приходится оборачивать или подменять компоненты.
- **Нет удобной поддержки** современных форматов и паттернов работы с ответом.

`RestClient` решает всё это единым fluent builder и при этом совместим с той же инфраструктурой — `HttpMessageConverter`, `ClientHttpRequestFactory`. Поэтому миграция дёшева: ту же фабрику запросов и конвертеры можно передать в новый клиент.

**Статус по версиям.** В Spring Framework 6.x `RestTemplate` был в режиме поддержки. В 7.0 deprecation объявили в справочной документации. В 7.1 класс получил `@Deprecated(since = "7.1", forRemoval = true)`. Для нового синхронного кода Spring рекомендует `RestClient`, для реактивных и потоковых сценариев — `WebClient`. [Release Notes 7.0](https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-7.0-Release-Notes/d2df491db0e5856e31b1694e5c6efdf727bcb517) · [RestTemplate 7.1 Javadoc](https://docs.spring.io/spring-framework/docs/7.1.x-SNAPSHOT/javadoc-api/org/springframework/web/client/RestTemplate.html).

---

## Q3. (!) Как работает RestClient?

`RestClient` — это синхронный fluent-клиент: вы собираете запрос цепочкой методов и в конце вызываете терминальную операцию, которая блокирующе выполняет HTTP-вызов и возвращает результат. Каждый вызов строится по одной схеме: выбрать метод (`get()`/`post()`/…) → задать URI и тело → `retrieve()` → извлечь ответ (`body()`, `toEntity()`, `toBodilessEntity()`).

В Spring Framework 6.2 вызов `retrieve()` только возвращает `ResponseSpec`. Если цепочка DELETE заканчивается на `retrieve()`, сервер не получит запрос. В примере ниже `toBodilessEntity()` отправляет запрос и обрабатывает статус ответа. При HTTP 204 результат содержит статус и заголовки без тела, а при HTTP 404 стандартный обработчик выбросит исключение, даже если вызывающий код игнорирует возвращаемое значение. [Spring Framework 6.2 — получение ответа](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html#rest-restclient-response) · [ResponseSpec.toBodilessEntity](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/web/client/RestClient.ResponseSpec.html#toBodilessEntity())

`RestClient.builder()` создаёт переиспользуемый клиент с общими настройками (базовый URL, заголовки по умолчанию). Один такой клиент потокобезопасен — его создают один раз и держат как бин.

```java
// Создание
RestClient client = RestClient.builder()
    .baseUrl("https://api.example.com")
    .defaultHeader("Authorization", "Bearer " + token)
    .build();

// GET
User user = client.get()
    .uri("/users/{id}", 42)
    .retrieve()
    .body(User.class);

// POST
User created = client.post()
    .uri("/users")
    .contentType(MediaType.APPLICATION_JSON)
    .body(new CreateUserRequest("Alice"))
    .retrieve()
    .body(User.class);

// PUT
client.put()
    .uri("/users/{id}", 42)
    .contentType(MediaType.APPLICATION_JSON)
    .body(updatedUser)
    .retrieve()
    .toBodilessEntity();

// DELETE
client.delete()
    .uri("/users/{id}", 42)
    .retrieve()
    .toBodilessEntity();
```

---

## Q4. Чем retrieve() отличается от exchange()?

Коротко: `retrieve()` — простой путь «дай мне тело», `exchange()` — полный контроль над сырым ответом. Разница в том, кто решает, что считать ошибкой.

- **`retrieve()`** сам трактует статус: 4xx/5xx по умолчанию превращаются в исключение (`RestClientException` и его наследники). Вы получаете готовое тело или исключение — промежуточного состояния нет.
- **`exchange()`** отдаёт вам объекты запроса и ответа, и вы сами решаете, что с ними делать: проверить статус, прочитать заголовки, разобрать тело по-разному в зависимости от кода. Никакой автоматической обработки ошибок — всё на вас.

Типичный сценарий для `exchange()`: код 404 — это не ошибка, а «ресурса нет», и его нужно превратить в `Optional.empty()`, а не в исключение.

```java
// retrieve() — просто и удобно
User user = client.get()
    .uri("/users/1")
    .retrieve()
    .body(User.class);

// exchange() — кастомная логика по статусу
Optional<User> user = client.get()
    .uri("/users/{id}", id)
    .exchange((request, response) -> {
        if (HttpStatus.NOT_FOUND.isSameCodeAs(response.getStatusCode())) {
            return Optional.empty();
        } else if (response.getStatusCode().is2xxSuccessful()) {
            return Optional.ofNullable(response.bodyTo(User.class));
        }
        throw new ServiceException("Unexpected: " + response.getStatusCode());
    });
```

В лямбде `exchange()` ответ имеет тип `ConvertibleClientHttpResponse`: он даёт как сырой `getBody()`, так и `bodyTo(User.class)` через настроенные конвертеры. `bodyTo()` возвращает `null` при пустом теле, поэтому здесь нужен `Optional.ofNullable()`. Это расширение `ClientHttpResponse`, а не метод базового интерфейса. См. [Javadoc Spring Framework 6.2](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/web/client/RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse.html).

Помимо обработки статусов, `exchange()` даёт доступ к сырому ответу: чтению тела как `InputStream` и прочим низкоуровневым деталям. Заголовки при этом доступны и через `retrieve()` — их отдаёт `toEntity(...)` вместе с телом и статусом; разница не в самом доступе к заголовкам, а в том, что `exchange()` пускает к ответу до и помимо разбора тела.

**Когда закрывается ответ.** В Spring Framework 6.2 обычный `exchange(callback)` закрывает HTTP-ответ после выхода из callback, в том числе при исключении. Если callback вернёт `response.getBody()`, вызывающий получит поток от уже закрытого ответа. При скачивании файла скопируйте тело в выходной поток внутри callback и верните, например, число записанных байт. У перегрузки `exchange(callback, false)` закрытие становится обязанностью вызывающего кода. Если чтение должно продолжаться за пределами callback, передавайте сам закрываемый ответ или объект, который им владеет. Закрывайте ответ и на пути ошибки, иначе незавершённое чтение может удерживать соединение из пула. [Spring Framework 6.2 — контракт exchange](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/web/client/RestClient.RequestHeadersSpec.html#exchange(org.springframework.web.client.RestClient.RequestHeadersSpec.ExchangeFunction,boolean)) · [DefaultRestClient 6.2.19 — закрытие в finally](https://github.com/spring-projects/spring-framework/blob/v6.2.19/spring-web/src/main/java/org/springframework/web/client/DefaultRestClient.java#L562-L572).

Для обычного преобразования тела начинайте с `retrieve()`. Выбирайте `exchange()`, когда требуется контролировать сырой ответ или преобразовать статус в иной результат, например 404 в `Optional.empty()`. См. [Spring Framework 6.2: RestClient](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html).

---

## Q5. Как обрабатывать ошибки в RestClient?

Главный инструмент — `onStatus()`: он перехватывает ответы по предикату статуса и позволяет бросить своё доменное исключение вместо стандартного. Это работает поверх `retrieve()`, до того как тело будет извлечено. Есть два уровня:

- **`onStatus(...)` на отдельном запросе** — обработка для конкретного вызова (например, 404 → `UserNotFoundException`).
- **`defaultStatusHandler(...)` на builder'е клиента** — глобальное правило для всех запросов этого клиента (например, 429 → `RateLimitException`).

```java
// Глобальный обработчик через defaultStatusHandler:
RestClient client = RestClient.builder()
    .baseUrl("https://api.example.com")
    .defaultStatusHandler(
        status -> status.value() == 429,
        (req, resp) -> { throw new RateLimitException(); }
    )
    .build();

// onStatus — для конкретных кодов
User user = client.get()
    .uri("/users/{id}", id)
    .retrieve()
    .onStatus(status -> status.value() == 404,
        (request, response) -> {
            throw new UserNotFoundException("User not found: " + id);
        })
    .onStatus(HttpStatusCode::is5xxServerError,
        (request, response) -> {
            throw new ServiceUnavailableException("Remote service error");
        })
    .body(User.class);
```

Обработчики `onStatus` для конкретного ответа проверяются раньше обработчиков `defaultStatusHandler` клиента. Предикат на все 4xx перехватил бы и 429 до глобального правила. [RestClient.Builder](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/web/client/RestClient.Builder.html).

Если ни один обработчик не подошёл, `retrieve()` сам бросает стандартные исключения: `HttpClientErrorException` для 4xx и `HttpServerErrorException` для 5xx (оба — наследники `RestClientException`). То есть «ничего не делать» — это тоже стратегия: ошибки не проглатываются молча, а превращаются в исключения.

---

## Q6. Как десериализовать коллекцию через RestClient?

Для одиночного объекта хватает `body(User.class)`, но для `List<User>` так не получится: из-за **стирания типов** (type erasure) в рантайме `List<User>` неотличим от `List<Object>`, и Jackson не узнает, во что разворачивать элементы. Решение — `ParameterizedTypeReference`: анонимный подкласс сохраняет полный generic-тип через рефлексию, и десериализатор получает точную информацию о `List<User>`.

```java
// Один объект — обычно просто:
User user = client.get().uri("/users/1")
    .retrieve().body(User.class);

// Список — нужен ParameterizedTypeReference:
List<User> users = client.get().uri("/users")
    .retrieve()
    .body(new ParameterizedTypeReference<List<User>>() {});
    // или короче, начиная с Java 9: .body(new ParameterizedTypeReference<>() {})

// Map:
Map<String, Object> data = client.get().uri("/info")
    .retrieve()
    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
```

См. [Spring Framework 6.2: `retrieve().body(ParameterizedTypeReference)`](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html).

---

## Q7. (!) Когда использовать WebClient вместо RestClient?

Выбор определяется **контрактом вызова**. Для обычного синхронного HTTP-запроса подходит `RestClient`. Если нужно неблокирующе скомпоновать несколько запросов, использовать `Mono`/`Flux` или потоковый ответ, подходит `WebClient`. В WebFlux-цепочке блокирующий вызов `RestClient` задержал бы поток; в Spring MVC `WebClient` тоже допустим, когда его асинхронные возможности действительно нужны. Название серверного стека само по себе не заменяет анализ вызова.

**RestClient** (синхронный) — когда:
- Традиционное Spring MVC приложение (модель thread-per-request).
- Простые REST-вызовы, реактивность не нужна.
- Хочется максимальной простоты кода.

**WebClient** (реактивный) — когда:
- Нужен неблокирующий исходящий вызов в реактивной цепочке, например в Spring WebFlux.
- Нужны `Mono<T>`/`Flux<T>` как возвращаемые типы (чтобы не блокировать поток).
- Высокая конкурентность при малом числе потоков — реактивная модель здесь экономит ресурсы.
- Нужен стриминг ответа (`Flux<ServerSentEvent>`), а не один цельный результат.

```java
// WebClient в реактивном стеке:
Mono<User> userMono = webClient.get()
    .uri("/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class);

Flux<User> allUsers = webClient.get()
    .uri("/users")
    .retrieve()
    .bodyToFlux(User.class);
```

**Подводный камень:** `WebClient.block()` делает **вызывающий поток ожидающим**, хотя транспорт клиента остаётся неблокирующим. Это допустимый способ получить синхронный результат, но не превращает блокирующий код в асинхронный. Для простого синхронного сценария `RestClient` обычно проще; если несколько ответов нужно скомпоновать, лучше собрать реактивную цепочку и ждать результат лишь на допустимой границе. См. [Spring Framework 6.2: REST Clients](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html) и [синхронное использование WebClient](https://docs.spring.io/spring-framework/reference/6.2/web/webflux-webclient/client-synchronous.html).

---

## Q8. Как сделать синхронный вызов через WebClient?

Синхронный результат из `WebClient` получают блокирующим ожиданием. `Mono.block()` ждёт значение или пустое завершение. `Flux.blockFirst()` ждёт первый элемент либо пустое завершение, а `blockLast()` ждёт завершения и возвращает последний элемент, если он был. Ошибки потока передаются вызывающему коду. [Reactor Flux Javadoc](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#blockFirst()).

```java
// .block() превращает реактивный вызов в блокирующий
User user = webClient.get()
    .uri("/users/{id}", id)
    .retrieve()
    .bodyToMono(User.class)
    .block();  // блокирующая граница на подходящем потоке
```

На потоках, где Reactor запрещает блокирующие API, `block()`, `blockFirst()` и `blockLast()` приводят к `IllegalStateException`. На других потоках вызов может ждать и занимать поток исполнителя. Это снижает пропускную способность при большом числе одновременных запросов. [Reactor Schedulers Javadoc](https://projectreactor.io/docs/core/release/api/reactor/core/scheduler/Schedulers.html#isInNonBlockingThread()).

Блокирующее ожидание подходит для явно синхронной границы на потоке, где оно разрешено. В реактивном обработчике обычно возвращают `Mono` или `Flux` и дают фреймворку управлять подпиской.

---

## Q9. (!) Что такое @HttpExchange и как им пользоваться?

`@HttpExchange` (Spring 6) — декларативный способ описать HTTP-клиент: вы пишете только Java-интерфейс с аннотированными методами, а Spring сам генерирует реализацию-прокси, которая под капотом делает реальные вызовы. Это встроенный в Spring Framework аналог `@FeignClient` из Spring Cloud — без отдельной зависимости.

Идея в том, чтобы убрать ручную «сборку» запросов: метод интерфейса `User getUser(long id)` уже описывает контракт, а где именно крутится `RestClient` или `WebClient` — деталь конфигурации. Это делает клиентский код типобезопасным и читаемым.

Подключение в три шага:

```java
// 1. Объявляем интерфейс
@HttpExchange("/users")
public interface UserClient {

    @GetExchange("/{id}")
    User getUser(@PathVariable long id);

    @GetExchange
    List<User> getAllUsers(@RequestParam String role);

    @PostExchange
    User createUser(@RequestBody CreateUserRequest request);

    @PutExchange("/{id}")
    User updateUser(@PathVariable long id, @RequestBody User user);

    @DeleteExchange("/{id}")
    void deleteUser(@PathVariable long id);
}

// 2. Регистрируем бин в @Configuration
@Bean
UserClient userClient(RestClient.Builder builder) {
    RestClient restClient = builder.baseUrl("https://users.service").build();
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
    return factory.createClient(UserClient.class);
}

// 3. Используем как обычный бин
@Service
public class OrderService {
    @Autowired UserClient userClient;

    public Order placeOrder(Long userId) {
        User user = userClient.getUser(userId);
        // ...
    }
}
```

Обратите внимание: аннотации на методах (`@GetExchange`, `@PostExchange` и т.д.) — это клиентские аналоги серверных `@GetMapping`/`@PostMapping`, а пути из `@HttpExchange("/users")` и метода складываются. Сам же интерфейс не зависит от того, какой клиент его исполняет — `RestClient` или `WebClient` подключается на шаге конфигурации (см. Q11).

---

## Q10. Какие аннотации параметров поддерживает @HttpExchange?

Параметры метода описываются теми же аннотациями, что и в серверных контроллерах Spring MVC, — это сделано намеренно, чтобы не учить новый набор. Каждая говорит, в какую часть HTTP-запроса попадёт аргумент:

| Аннотация | Описание |
|---|---|
| `@PathVariable` | Переменная в пути URL |
| `@RequestParam` | Параметр тела при `application/x-www-form-urlencoded`, иначе query-параметр |
| `@RequestHeader` | HTTP-заголовок |
| `@RequestBody` | Тело запроса |
| `@CookieValue` | Cookie |
| `@RequestPart` | Часть multipart-запроса |
| `URI` | URL запроса вместо URL из аннотации |
| `UriBuilderFactory` | Фабрика для подстановки другого базового URI с сохранением сегментов пути из аннотации |
| `HttpMethod` | Динамический метод HTTP |

`URI`, `UriBuilderFactory` и `HttpMethod` распознаются по типу аргумента. `HttpMethod` переопределяет метод из аннотации. [Spring Framework 6.2 REST Clients](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html) · [HttpExchange Javadoc](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/web/service/annotation/HttpExchange.html).

```java
@HttpExchange
public interface SearchClient {

    @GetExchange("/search")
    SearchResult search(
        @RequestParam String query,
        @RequestParam(defaultValue = "1") Integer page,  // не int: defaultValue сработает только при null
        @RequestHeader("Accept-Language") String lang
    );

    @PostExchange
    ResponseEntity<Void> uploadFile(
        @RequestHeader("X-Upload-Token") String token,
        @RequestBody byte[] content,
        URI targetUri
    );
}
```

---

## Q11. Как подключить @HttpExchange к RestClient vs WebClient?

Для ручной сборки вы оборачиваете клиент в соответствующий адаптер, отдаёте его `HttpServiceProxyFactory`, и фабрика создаёт прокси по интерфейсу. `RestClientAdapter` поддерживает синхронные сигнатуры, `WebClientAdapter` — также реактивные. Вид возвращаемого значения интерфейса остаётся частью контракта.

```java
@HttpExchange("/users")
interface UserClient {
    @GetExchange("/{id}")
    User getById(@PathVariable Long id);
}
```

**С RestClient (синхронный, Spring 6.1+):**
```java
RestClient restClient = RestClient.builder().baseUrl(url).build();
RestClientAdapter adapter = RestClientAdapter.create(restClient);
HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
UserClient client = factory.createClient(UserClient.class);
```

**С WebClient (реактивный):**
```java
WebClient webClient = WebClient.builder().baseUrl(url).build();
WebClientAdapter adapter = WebClientAdapter.create(webClient);
HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
UserClient client = factory.createClient(UserClient.class);
```

С такой синхронной сигнатурой оба адаптера могут построить прокси, но `WebClientAdapter` при возврате `User` ждёт результат и не делает вызывающий код неблокирующим. Чтобы возвращать `Mono<User>` или поток `Flux<User>`, нужен реактивный адаптер и изменение сигнатуры интерфейса **вместе с его потребителями**. Прокси изолирует настройку транспорта, но не меняет модель выполнения сам.

При **ручной сборке** базовый URL задают на клиенте до создания адаптера: `RestClient.builder().baseUrl("https://users.service")` или `WebClient.builder().baseUrl(...)`. Для автоконфигурируемых HTTP Service групп в **Spring Boot 4.x** доступно свойство `spring.http.serviceclient.<group-name>.base-url` вместе с настройками таймаутов и заголовков. Это другой путь конфигурации, поэтому проверяйте версию Boot и способ регистрации клиента. См. [Spring Framework 6.2: HTTP Interface](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html) и [Spring Boot 4: Calling REST Services](https://docs.spring.io/spring-boot/4.0/reference/io/rest-client.html).

---

## Q12. Как настроить таймауты, базовый URL и заголовки?

Разделение ответственности простое: **таймауты и работа с сокетами** живут в `ClientHttpRequestFactory` (это транспортный слой), а **базовый URL, заголовки по умолчанию и интерсепторы** задаются на `RestClient.builder()`. Поэтому сначала собирают фабрику с нужными таймаутами и передают её в builder через `requestFactory()`.

Для базовых нужд хватает `SimpleClientHttpRequestFactory`. Если нужен пул соединений, их вытеснение по TTL и тонкая настройка — берут `HttpComponentsClientHttpRequestFactory` поверх Apache HttpClient.

```java
// Таймауты через ClientHttpRequestFactory
SimpleClientHttpRequestFactory simpleFactory = new SimpleClientHttpRequestFactory();
simpleFactory.setConnectTimeout(Duration.ofSeconds(5));
simpleFactory.setReadTimeout(Duration.ofSeconds(10));

// Или через Apache HttpClient 5 (более гибко: пул, TTL соединений):
PoolingHttpClientConnectionManager connManager =
    PoolingHttpClientConnectionManagerBuilder.create().build();
connManager.setDefaultConnectionConfig(ConnectionConfig.custom()
    .setConnectTimeout(Timeout.ofSeconds(5))
    .setSocketTimeout(Timeout.ofSeconds(10))
    .setTimeToLive(TimeValue.ofSeconds(10))
    .build());
CloseableHttpClient httpClient = HttpClients.custom()
    .setConnectionManager(connManager)
    .evictExpiredConnections()
    .build();
HttpComponentsClientHttpRequestFactory apacheFactory =
    new HttpComponentsClientHttpRequestFactory(httpClient);
apacheFactory.setConnectionRequestTimeout(Duration.ofMillis(300));

// RestClient с настройками:
RestClient client = RestClient.builder()
    .baseUrl("https://api.example.com/v2")
    .requestFactory(apacheFactory)
    .defaultHeader("Authorization", "Bearer " + apiKey)
    .defaultHeader("Accept", "application/json")
    .defaultUriVariables(Map.of("version", "2"))
    .build();
```

**Ожидание соединения из пула.** Если все соединения для маршрута заняты и пул не может выдать ещё одно, запрос сначала ждёт выдачи соединения. В примере Spring Framework 6.2 с Apache HttpClient 5.6 это ожидание ограничено 300 мс через `setConnectionRequestTimeout`. Оно может закончиться ошибкой ещё до отправки запроса. `connectTimeout` относится к установлению нового соединения, а `socketTimeout` задаёт базовый таймаут сокета. Повторно используемое соединение не требует нового подключения. Общий бюджет вызова включает эти этапы вместе с обработкой ответа и возможными повторами (Q14). [Spring Framework 6.2 — фабрика Apache HttpClient](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/http/client/HttpComponentsClientHttpRequestFactory.html#setConnectionRequestTimeout(java.time.Duration)) · [Apache HttpClient 5.6 — параметры запроса](https://hc.apache.org/httpcomponents-client-5.6.x/current/httpclient5/apidocs/org/apache/hc/client5/http/config/RequestConfig.Builder.html#setConnectionRequestTimeout-org.apache.hc.core5.util.Timeout-).

**Интерсептор для логирования.** `requestInterceptor` оборачивает каждый вызов: вы видите исходящий запрос, передаёте управление дальше через `execution.execute(...)` и затем читаете ответ. Удобно для логирования, добавления заголовков на лету или сбора метрик:
```java
RestClient client = RestClient.builder()
    .requestInterceptor((request, body, execution) -> {
        log.debug("→ {} {}", request.getMethod(), request.getURI());
        ClientHttpResponse response = execution.execute(request, body);
        log.debug("← {}", response.getStatusCode());
        return response;
    })
    .build();
```

В приложении со Spring Boot для штатных HTTP-метрик и передачи trace context внедряйте автоконфигурированный `RestClient.Builder`. Прямой вызов `RestClient.builder()` в примере выше не получает эти настройки автоматически; собственный интерсептор логирования их не заменяет. Подробности об общем бюджете вызова — в Q14.

См. [Spring Framework 6.2: `SimpleClientHttpRequestFactory`](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/http/client/SimpleClientHttpRequestFactory.html) и [Apache HttpClient 5.6: connection manager](https://hc.apache.org/httpcomponents-client-5.6.x/connection-management.html).

---

## Q13. Как мигрировать с RestTemplate на RestClient?

Миграция дешёвая, потому что у клиентов общая инфраструктура. Есть два пути, и обычно их сочетают.

**Шаг 1 — переиспользовать настройки.** `RestClient.create(oldTemplate)` переносит конвертеры сообщений, фабрику запросов с её таймаутами, интерсепторы и обработчик ошибок. В Spring Framework 6.2 настроенный `UriTemplateHandler` переносится, если он реализует `UriBuilderFactory`. Обычный `UriTemplateHandler` без этого интерфейса не переносится. Если фабрика использует стандартные настройки `RestTemplate`, клиент выбирает свои настройки URI по умолчанию. Проверь URI-шаблоны и кодирование переменных при миграции. [DefaultRestClientBuilder 6.2.19](https://raw.githubusercontent.com/spring-projects/spring-framework/v6.2.19/spring-web/src/main/java/org/springframework/web/client/DefaultRestClientBuilder.java).

```java
RestTemplate oldTemplate = ...; // уже настроенный с interceptors, converters
RestClient client = RestClient.create(oldTemplate);
// Сохраняет MessageConverters, RequestFactory, Interceptors
```

**Шаг 2 — переписать вызовы.** Основные перегруженные методы `RestTemplate` имеют прямой fluent-аналог (у редких вроде `postForLocation()` или `optionsForAllow()` его придётся собрать вручную). Логика та же — меняется только форма записи:
```java
// RestTemplate
String result = restTemplate.getForObject(url, String.class);
// RestClient
String result = restClient.get().uri(url).retrieve().body(String.class);

// RestTemplate POST
ResponseEntity<User> response = restTemplate.postForEntity(url, body, User.class);
// RestClient
ResponseEntity<User> response = restClient.post().uri(url)
    .body(body).retrieve().toEntity(User.class);

// RestTemplate exchange
ResponseEntity<List<User>> resp = restTemplate.exchange(
    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
// RestClient
ResponseEntity<List<User>> resp = restClient.get().uri(url)
    .retrieve().toEntity(new ParameterizedTypeReference<>() {});
```

См. [Spring Framework 6.2: миграция с RestTemplate](https://docs.spring.io/spring-framework/reference/6.2/integration/rest-clients.html) и [`RestClient.create(RestTemplate)`](https://docs.spring.io/spring-framework/docs/6.2.x/javadoc-api/org/springframework/web/client/RestClient.html).

---

## Q14. Как спроектировать исходящий HTTP-вызов с ограничением времени, повторами и наблюдаемостью?

Для исходящего вызова `RestClient` задайте **общий бюджет времени** всей операции: ожидание свободного соединения из пула, подключение, ответ, паузы и повторные попытки должны укладываться в него. `RestClient` синхронный; отдельные настройки транспорта не складываются автоматически в такой deadline. Например, `connectionRequestTimeout` Apache HttpClient ограничивает ожидание соединения из пула, `connectTimeout` — установление соединения, `responseTimeout` — ожидание ответа. [Документация Apache HttpClient 5.6](https://hc.apache.org/httpcomponents-client-5.6.x/current/httpclient5/apidocs/org/apache/hc/client5/http/config/RequestConfig.Builder.html) прямо отмечает, что `responseTimeout` не является deadline и может быть превышен при повторном выполнении запроса. Настройку фабрики и пула см. в Q12.

Практический порядок для вызова `GET /users/{id}`:

1. Определить бюджет логической операции, например 2 секунды, и ограничить отдельно ожидание соединения, подключение и ответ. Перед каждой новой попыткой проверять оставшееся время; если оно исчерпано, не начинать повтор. При необходимости жёсткой верхней границы выбрать механизм отмены, который действительно прерывает используемый HTTP-транспорт: таймаут ожидания `Future` сам по себе не доказывает остановку уже выполняющегося блокирующего запроса.
2. Ограничить число повторов и выбирать сбои по контракту удалённого сервиса: например, временный сетевой сбой или `503` для безопасного `GET`. `429` и `503` могут сопровождаться `Retry-After`; пауза имеет смысл лишь в оставшемся бюджете. Бизнес-`4xx` обрабатывать как ошибки вызова (Q5), а не повторять без разбора. `POST` с побочным эффектом повторять только если сервер обеспечивает идемпотентность операции, например проверяет ключ и дедуплицирует запросы; потерянный ответ не означает, что первый запрос не был применён. Количество попыток и условия повтора настраиваются в [Resilience4j Retry](resilience4j-interview.md#q9-как-настроить-retry-в-spring-boot); семантика методов — в [RFC 9110, ` 9.2.2](https://www.rfc-editor.org/rfc/rfc9110.html#section-9.2.2).
3. Использовать переиспользуемый клиент и, если нагрузка требует, ограниченный пул соединений с таймаутом выдачи соединения. В Spring Boot строить клиент из **внедрённого автоконфигурированного** `RestClient.Builder`: тогда Actuator создаёт наблюдения `http.client.requests` и настроенная трассировка передаёт контекст исходящему запросу. Статический `RestClient.builder()` эти Boot-настройки сам не получает. Для HTTP-метрик сохранять шаблон маршрута, а не подставленный идентификатор пользователя как тег. См. [Spring Boot Metrics](https://docs.spring.io/spring-boot/reference/actuator/metrics.html#actuator.metrics.supported.http-clients) и [Tracing](https://docs.spring.io/spring-boot/reference/actuator/tracing.html#actuator.micrometer-tracing.propagating-traces).

`Resilience4j` отвечает за политику повторов и, при необходимости, circuit breaker/bulkhead; транспортный клиент — за соединение и его фазовые таймауты. `TimeLimiter` для асинхронного результата не превращает блокирующий `RestClient` в автоматически отменяемый вызов. Обе политики надо согласовать с бюджетом вызывающего сервиса, чтобы несколько попыток не пережили его ответ.

---

## See also

- [Spring WebFlux](spring-webflux-interview.md) — WebClient в реактивном стеке
- [Spring Framework](spring-framework-interview.md) — архитектура Spring, MessageConverters
- [Spring Boot](spring-boot-interview.md) — автоконфигурация HTTP-клиентов
- [HTTP & REST](../../api/http-rest-interview.md) — HTTP-методы, статус-коды, заголовки
- [Spring Security](spring-security-interview.md) — OAuth2 с RestClient/WebClient
- [Spring Testing](spring-testing-interview.md) — MockServer, WireMock для тестирования HTTP-клиентов
- [gRPC](../../api/grpc-interview.md) — альтернатива REST для межсервисного взаимодействия
- [Микросервисы](../../architecture/microservices-interview.md) — паттерны межсервисного взаимодействия
- [Resilience4j](resilience4j-interview.md) — Retry, TimeLimiter, CircuitBreaker и Bulkhead
