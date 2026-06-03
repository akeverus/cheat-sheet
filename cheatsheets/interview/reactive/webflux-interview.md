---
title: "Вопросы на собеседовании: Spring WebFlux"
description: "Spring WebFlux: реактивный веб, RouterFunction, WebClient, Server-Sent Events, реактивная безопасность, обработка ошибок"
tags:
  - interview
  - reactive
  - spring-webflux-interview
  - webflux
  - spring
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring WebFlux"
  - "Spring WebFlux interview"
  - "Spring WebFlux собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring WebFlux`

Гид по вопросам собеседования на тему `Spring WebFlux` для `Senior Java Developer`. Охватывает реактивный веб-слой, функциональный роутинг (`RouterFunction`/`HandlerFunction`), `WebClient`, `Server-Sent Events`, реактивную безопасность (`Spring Security` + `WebFlux`), обработку ошибок и конфигурацию.

**`Spring WebFlux`** — реактивный веб-фреймворк, представленный в `Spring 5`. Построен на `Project Reactor` и работает поверх `Netty` (по умолчанию) или `Servlet 3.1+` контейнеров. Позволяет обрабатывать тысячи конкурентных соединений при минимальном количестве потоков.

## Полезные ссылки

### Официальная документация

- [Spring WebFlux Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) — официальная документация
- [WebClient Reference](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client) — документация по `WebClient`
- [Spring Security + WebFlux](https://docs.spring.io/spring-security/reference/reactive/index.html) — безопасность в реактивном стеке

### Статьи Baeldung

- [Introduction to Spring WebFlux](https://www.baeldung.com/spring-webflux) — введение
- [Spring WebFlux Filters](https://www.baeldung.com/spring-webflux-filters) — фильтры
- [Spring Security 5 for Reactive Applications](https://www.baeldung.com/spring-security-5-reactive) — безопасность
- [Spring WebFlux and SSE](https://www.baeldung.com/spring-server-sent-events) — `Server-Sent Events`
- [Guide to WebClient](https://www.baeldung.com/spring-5-webclient) — работа с `WebClient`

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Spring WebFlux**
- [Q1. Чем Spring WebFlux отличается от Spring MVC?](#q1-чем-spring-webflux-отличается-от-spring-mvc)
- [Q2. Когда использовать WebFlux, а когда MVC?](#q2-когда-использовать-webflux-а-когда-mvc)
- [Q3. Что такое событийный цикл (event loop) в WebFlux?](#q3-что-такое-событийный-цикл-event-loop-в-webflux)
- [Q4. Какие серверы поддерживает Spring WebFlux?](#q4-какие-серверы-поддерживает-spring-webflux)

**Аннотационная модель**
- [Q5. Как работают @RestController и @RequestMapping в WebFlux?](#q5-как-работают-restcontroller-и-requestmapping-в-webflux)
- [Q6. Что такое @RequestBody и @ResponseBody в реактивном контексте?](#q6-что-такое-requestbody-и-responsebody-в-реактивном-контексте)

**Функциональный роутинг**
- [Q7. Что такое RouterFunction и HandlerFunction?](#q7-что-такое-routerfunction-и-handlerfunction)
- [Q8. Как составить сложный роутер из нескольких маршрутов?](#q8-как-составить-сложный-роутер-из-нескольких-маршрутов)
- [Q9. Чем ServerRequest/ServerResponse отличаются от HttpServletRequest?](#q9-чем-serverrequestserverresponse-отличаются-от-httpservletrequest)
- [Q10. Как добавить WebFilter к функциональному роутеру?](#q10-как-добавить-webfilter-к-функциональному-роутеру)

**WebClient**
- [Q11. Что такое WebClient и чем он лучше RestTemplate?](#q11-что-такое-webclient-и-чем-он-лучше-resttemplate)
- [Q12. Как настроить таймауты в WebClient?](#q12-как-настроить-таймауты-в-webclient)
- [Q13. Как реализовать повторные попытки (retry) в WebClient?](#q13-как-реализовать-повторные-попытки-retry-в-webclient)
- [Q14. Как стримить большой ответ через WebClient?](#q14-как-стримить-большой-ответ-через-webclient)
- [Q15. Как добавить ExchangeFilterFunction для логирования?](#q15-как-добавить-exchangefilterfunction-для-логирования)

**Server-Sent Events**
- [Q16. Что такое Server-Sent Events и как их реализовать в WebFlux?](#q16-что-такое-server-sent-events-и-как-их-реализовать-в-webflux)
- [Q17. Чем SSE отличается от WebSocket?](#q17-чем-sse-отличается-от-websocket)

**Обработка ошибок**
- [Q18. Как обрабатывать ошибки в WebFlux-контроллерах?](#q18-как-обрабатывать-ошибки-в-webflux-контроллерах)
- [Q19. Что такое WebExceptionHandler?](#q19-что-такое-webexceptionhandler)
- [Q20. Как вернуть кастомный HTTP-статус при ошибке?](#q20-как-вернуть-кастомный-http-статус-при-ошибке)
- [Q21. Как работает onErrorResume/onErrorReturn?](#q21-как-работает-onerrorresumeonerrorreturn)

**Реактивная безопасность**
- [Q22. Как настроить Spring Security для WebFlux?](#q22-как-настроить-spring-security-для-webflux)
- [Q23. Что такое ReactiveUserDetailsService?](#q23-что-такое-reactiveuserdetailsservice)
- [Q24. Как получить текущего пользователя в реактивном контексте?](#q24-как-получить-текущего-пользователя-в-реактивном-контексте)
- [Q25. Как применять @PreAuthorize в WebFlux?](#q25-как-применять-preauthorize-в-webflux)

**Конфигурация и производительность**
- [Q26. Как настроить codec для больших тел запросов?](#q26-как-настроить-codec-для-больших-тел-запросов)
- [Q27. Зачем избегать блокирующих операций в event loop?](#q27-зачем-избегать-блокирующих-операций-в-event-loop)
- [Q28. Как выполнить блокирующий вызов безопасно в WebFlux?](#q28-как-выполнить-блокирующий-вызов-безопасно-в-webflux)

---

## Q1. Чем Spring WebFlux отличается от Spring MVC?

Это две разные модели обработки запросов в одном Spring-стеке. Ключевая разница — как фреймворк использует потоки.

- **`Spring MVC`** — синхронная/блокирующая модель на основе Servlet API: на каждый запрос выделяется отдельный поток, и он занят, пока ответ не готов. Ждёт ответа от БД — поток простаивает.
- **`Spring WebFlux`** — реактивная/неблокирующая модель: небольшое число потоков обрабатывает все запросы через событийный цикл. Поток не ждёт I/O, а переключается на другие запросы; результат приходит через callback.

Отсюда следует и остальное: WebFlux масштабируется по числу соединений, а не по числу потоков, и поддерживает backpressure из коробки (через Reactive Streams) — MVC такого механизма не имеет.

| Аспект | Spring MVC | Spring WebFlux |
|---|---|---|
| Модель | Блокирующая, один поток/запрос | Неблокирующая, event loop |
| Сервер | Tomcat, Jetty (Servlet) | Netty, Undertow, Servlet 3.1+ |
| Типы возврата | `Object`, `ResponseEntity` | `Mono`, `Flux` |
| Backpressure | Нет | Есть (через Reactive Streams) |
| Лучше для | Традиционных CRUD-приложений | Высококонкурентных, стриминговых сервисов |

```java
// MVC
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    return userService.findById(id); // блокирует поток
}

// WebFlux
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return userService.findById(id); // не блокирует
}
```

## Q2. Когда использовать WebFlux, а когда MVC?

Решает не «мода на реактив», а характер нагрузки и доступная экосистема. Главный вопрос — есть ли у вас I/O-bound нагрузка с множеством одновременных соединений и полностью реактивный стек зависимостей.

**Предпочесть WebFlux:**
- Много конкурентных соединений с долгим ожиданием I/O (API-gateway, чаты, стриминг) — именно здесь экономия потоков даёт выигрыш.
- Нужна нативная поддержка SSE / WebSocket.
- Все зависимости — реактивные (R2DBC, реактивный Redis, reactive Mongo): иначе блокирующий вызов всё равно «съест» преимущество.

**Предпочесть Spring MVC:**
- Команда не знакома с реактивным программированием (реактивный код труднее писать и отлаживать).
- Есть блокирующие зависимости (JDBC, блокирующие клиенты) — тогда WebFlux не даст выигрыша, а только усложнит код.
- Простые CRUD-операции с небольшим числом пользователей — здесь оверхед реактива не окупается.

**Эмпирическое правило:** реактив окупается на I/O-bound нагрузке с тысячами соединений; на CPU-bound задачах и при блокирующих драйверах выигрыша почти нет.

## Q3. Что такое событийный цикл (event loop) в WebFlux?

Event loop — это небольшой пул потоков, который мультиплексирует множество соединений: вместо «поток на запрос» один поток обслуживает тысячи соединений, переключаясь между ними по мере готовности I/O.

`Netty` запускает N event loop потоков (обычно `Runtime.getRuntime().availableProcessors() * 2`). Каждый поток обрабатывает множество соединений через неблокирующий I/O. Пока ответ от сети или БД не пришёл, поток не простаивает — он занимается другими соединениями; при завершении I/O-операции callback планируется обратно на тот же event loop, и реактивная цепочка продолжается.

```
Запрос → event loop thread → реактивная цепочка → I/O callback → продолжение цепочки → ответ
```

**Подводный камень:** нельзя блокировать event loop (`Thread.sleep`, `block()`, JDBC). Поскольку один поток держит тысячи соединений, любая блокировка заморозит обработку всех соединений на этом потоке — это самая частая причина деградации WebFlux-приложений.

## Q4. Какие серверы поддерживает Spring WebFlux?

WebFlux работает на любом сервере с поддержкой неблокирующего I/O — это либо нативно реактивный движок (Netty, Undertow), либо Servlet-контейнер с Non-Blocking I/O из Servlet 3.1.

- **Netty** — по умолчанию, лучший выбор для чисто реактивных приложений (изначально событийный, без Servlet-прослойки).
- **Undertow** — высокопроизводительный, поддерживает неблокирующий I/O.
- **Tomcat 8.5+** / **Jetty 9.3+** — через Servlet 3.1 Non-Blocking I/O; используют, когда нужно остаться на привычном Servlet-контейнере.

Сменить сервер можно, исключив стартер Netty и подключив нужный контейнер:

```xml
<!-- Заменить Netty на Tomcat -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-reactor-netty</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
</dependency>
```

## Q5. Как работают @RestController и @RequestMapping в WebFlux?

Точно так же, как в Spring MVC: аннотационная модель идентична, те же `@RestController`, `@GetMapping`, `@PathVariable`. Отличие одно — в типах возврата: контроллер возвращает `Mono<T>` (одно значение) или `Flux<T>` (поток значений), а не обычные объекты. Это позволяет переиспользовать привычный код контроллеров без перехода на функциональный роутинг.

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Flux<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDto>> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> create(@RequestBody @Valid Mono<CreateUserRequest> request) {
        return request.flatMap(userService::create);
    }
}
```

## Q6. Что такое @RequestBody и @ResponseBody в реактивном контексте?

Аннотации те же, но работают неблокирующе. `@RequestBody` можно объявить как `Mono<T>` или `Flux<T>` — тогда тело запроса читается лениво, только при подписке, а не вычитывается целиком в момент входа в метод. `@ResponseBody` (включён в `@RestController`) автоматически сериализует `Mono`/`Flux` в JSON по мере появления элементов.

Это даёт два сценария:
- **`Mono<T>`** — обычное тело, прочитанное реактивно (вместо блокирующего парсинга).
- **`Flux<DataBuffer>`** — стриминговая загрузка: большой файл пишется по кускам, не загружаясь целиком в память.

```java
// Принять тело как Mono — ленивое чтение
@PutMapping("/{id}")
public Mono<UserDto> update(
        @PathVariable Long id,
        @RequestBody Mono<UpdateUserRequest> requestMono) {
    return requestMono.flatMap(req -> userService.update(id, req));
}

// Принять тело как Flux<DataBuffer> — стриминговая загрузка файла
@PostMapping(value = "/upload", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public Mono<Void> upload(@RequestBody Flux<DataBuffer> body) {
    return DataBufferUtils.write(body, Path.of("/tmp/upload"), StandardOpenOption.CREATE);
}
```

## Q7. Что такое RouterFunction и HandlerFunction?

Это функциональная альтернатива аннотационной модели: вместо `@RestController` маршруты описываются кодом, а не аннотациями. Удобно для динамической композиции роутов и когда хочется держать маршрутизацию и логику явно разделёнными.

- **`RouterFunction<T>`** — функциональный аналог `@RequestMapping`: принимает `ServerRequest` и возвращает `Optional<HandlerFunction<T>>` (нашёл подходящий обработчик — отдал его, нет — пустой `Optional`, запрос уходит дальше по цепочке).
- **`HandlerFunction<T>`** — сам обработчик запроса: принимает `ServerRequest`, возвращает `Mono<ServerResponse>`.

На практике роуты собираются билдером `RouterFunctions.route()`, а обработчики выносятся в отдельный `@Component`-handler:

```java
@Configuration
public class UserRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return RouterFunctions.route()
                .GET("/api/v1/users", handler::findAll)
                .GET("/api/v1/users/{id}", handler::findById)
                .POST("/api/v1/users", handler::create)
                .PUT("/api/v1/users/{id}", handler::update)
                .DELETE("/api/v1/users/{id}", handler::delete)
                .build();
    }
}

@Component
public class UserHandler {

    private final UserService userService;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll(), UserDto.class);
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return userService.findById(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
```

## Q8. Как составить сложный роутер из нескольких маршрутов?

Маршруты группируют через `RouterFunctions.route().nest()` — это аналог общего префикса `@RequestMapping` на классе. Вложенные роутеры объявляются отдельно (по ресурсам), а `nest()` навешивает на них общий путь или условие (`RequestPredicates.path`, заголовок, content-type). Так роутинг остаётся читаемым даже при десятках эндпоинтов.

```java
@Bean
public RouterFunction<ServerResponse> apiRoutes(
        UserHandler userHandler,
        OrderHandler orderHandler) {

    RouterFunction<ServerResponse> userRoutes = RouterFunctions.route()
            .GET("", userHandler::findAll)
            .GET("/{id}", userHandler::findById)
            .POST("", userHandler::create)
            .build();

    RouterFunction<ServerResponse> orderRoutes = RouterFunctions.route()
            .GET("", orderHandler::findAll)
            .POST("", orderHandler::create)
            .build();

    return RouterFunctions.route()
            .nest(RequestPredicates.path("/api/v1/users"), () -> userRoutes)
            .nest(RequestPredicates.path("/api/v1/orders"), () -> orderRoutes)
            .build();
}
```

## Q9. Чем ServerRequest/ServerResponse отличаются от HttpServletRequest?

Главное отличие — неблокирующая работа с телом и иммутабельность. `HttpServletRequest` читает тело через блокирующий `InputStream`; `ServerRequest` отдаёт его как `Mono`/`Flux` (`bodyToMono`/`bodyToFlux`), который вычитывается реактивно, без захвата потока.

- **Иммутабельность:** объекты не мутируются «на месте» — новый ответ строится билдером (`ServerResponse.ok()...`), что безопаснее в конкурентной среде.
- **Реактивное тело:** `request.bodyToMono(...)` возвращает publisher, а не готовый объект; данные приходят при подписке.
- **Доступ к метаданным** (query-параметры, заголовки) — через типобезопасные методы `queryParam`, `headers().firstHeader`.

```java
public Mono<ServerResponse> create(ServerRequest request) {
    // Тело — Mono, читается неблокирующе
    return request.bodyToMono(CreateUserRequest.class)
            .flatMap(userService::create)
            .flatMap(created -> ServerResponse
                    .created(URI.create("/api/v1/users/" + created.id()))
                    .bodyValue(created));
}

// Доступ к заголовкам и query params
public Mono<ServerResponse> search(ServerRequest request) {
    String query = request.queryParam("q").orElse("");
    String authHeader = request.headers().firstHeader("Authorization");
    return userService.search(query)
            .collectList()
            .flatMap(users -> ServerResponse.ok().bodyValue(users));
}
```

## Q10. Как добавить WebFilter к функциональному роутеру?

`WebFilter` — это реактивный аналог `Servlet Filter`: он перехватывает запрос до обработчика и видит весь `ServerWebExchange`. Подключить его можно на двух уровнях:

- **Глобально** — объявить бин, реализующий `WebFilter`; он применяется ко всем запросам (логирование, трейсинг, метрики).
- **Локально** — вызвать `.filter()` на конкретном роутере; фильтр сработает только для этой группы роутов (например, проверка заголовка для админских эндпоинтов).

Внутри фильтра обязательно вызвать `chain.filter(exchange)`, иначе запрос не пойдёт дальше; результат можно дополнить операторами (`doFinally` для замера времени):

```java
// Глобальный фильтр — бин
@Component
public class RequestLoggingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Request: {} {}", exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());
        long start = System.currentTimeMillis();
        return chain.filter(exchange)
                .doFinally(signal -> log.info("Completed in {} ms",
                        System.currentTimeMillis() - start));
    }
}

// Локальный фильтр — только для группы роутов
RouterFunction<ServerResponse> protectedRoutes = RouterFunctions.route()
        .GET("/admin/stats", adminHandler::stats)
        .build()
        .filter((request, next) -> {
            // Проверить заголовок Authorization
            if (request.headers().firstHeader("X-Admin-Key") == null) {
                return ServerResponse.status(HttpStatus.FORBIDDEN).build();
            }
            return next.handle(request);
        });
```

## Q11. Что такое WebClient и чем он лучше RestTemplate?

`WebClient` — реактивный неблокирующий HTTP-клиент, рекомендованная замена блокирующему `RestTemplate` (последний в режиме сопровождения). Главное преимущество — вызов внешнего сервиса не держит поток: пока ждём ответ, поток обслуживает другие запросы.

Чем лучше `RestTemplate`:
- **Неблокирующий** — возвращает `Mono`/`Flux`, не занимает поток на время запроса.
- **Стриминг** — умеет читать ответ построчно (`bodyToFlux`), не загружая всё тело в память.
- **Встроенные** retry, таймауты, фильтры (`ExchangeFilterFunction`) — без сторонних обёрток.
- **Fluent API** — единый билдер для GET/POST, заголовков, тела и обработки статусов.

```java
// Создание
WebClient webClient = WebClient.builder()
        .baseUrl("https://api.example.com")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

// GET — одиночный объект
Mono<UserDto> user = webClient.get()
        .uri("/users/{id}", 42L)
        .retrieve()
        .bodyToMono(UserDto.class);

// POST — отправить тело
Mono<OrderDto> order = webClient.post()
        .uri("/orders")
        .bodyValue(new CreateOrderRequest("item-1", 3))
        .retrieve()
        .bodyToMono(OrderDto.class);

// GET — список
Flux<UserDto> users = webClient.get()
        .uri("/users")
        .retrieve()
        .bodyToFlux(UserDto.class);
```

## Q12. Как настроить таймауты в WebClient?

Таймауты задаются не на самом `WebClient`, а на нижележащем `HttpClient` от Reactor Netty, который затем подключается к билдеру через `ReactorClientHttpConnector`. Это важно понимать на собеседовании: у `WebClient` нет одного «глобального» таймаута — есть несколько разных, и обычно нужно выставить их все.

- **Connect timeout** (`CONNECT_TIMEOUT_MILLIS`) — сколько ждать установления TCP-соединения.
- **Response timeout** (`responseTimeout`) — сколько ждать первого байта ответа после отправки запроса.
- **Read/Write timeout** (`ReadTimeoutHandler`/`WriteTimeoutHandler`) — простой канала между байтами при чтении/записи.

Без явных таймаутов зависший сервер удержит соединение надолго, поэтому выставлять их — обязательная практика для продакшена.

```java
HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)   // connection timeout
        .responseTimeout(Duration.ofSeconds(10))               // response timeout
        .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));

WebClient webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl("https://api.example.com")
        .build();
```

## Q13. Как реализовать повторные попытки (retry) в WebClient?

Повторы вешаются оператором `retryWhen` из Project Reactor поверх реактивной цепочки. Ключ к хорошему ответу — не просто «повторить N раз», а правильная стратегия:

- **`Retry.backoff(maxAttempts, firstBackoff)`** — экспоненциальная задержка между попытками вместо мгновенного шквала повторов (защищает падающий сервис от добивания).
- **`maxBackoff`** — потолок задержки, чтобы интервалы не росли бесконечно.
- **`.filter(...)`** — повторять только на «временных» ошибках (5xx, таймаут), а не на 4xx, где повтор бессмыслен.
- **`onRetryExhaustedThrow`** — что бросить, когда попытки исчерпаны (иначе Reactor завернёт ошибку в `RetryExhaustedException`).

**Подводный камень:** обычно ошибку сначала превращают в исключение через `onStatus`, и уже по его типу фильтруют повторы — как в примере ниже.

```java
Mono<UserDto> userWithRetry = webClient.get()
        .uri("/users/{id}", id)
        .retrieve()
        .onStatus(HttpStatusCode::is5xxServerError, response ->
                Mono.error(new ServiceUnavailableException("Server error")))
        .bodyToMono(UserDto.class)
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .maxBackoff(Duration.ofSeconds(10))
                .filter(ex -> ex instanceof ServiceUnavailableException)
                .onRetryExhaustedThrow((spec, signal) ->
                        new RuntimeException("Max retries exceeded", signal.failure())));
```

## Q14. Как стримить большой ответ через WebClient?

Ключ — `bodyToFlux` вместо `bodyToMono`: элементы приходят по одному и сразу уходят дальше по цепочке, поэтому весь ответ никогда не лежит в памяти целиком. Чтобы это работало, сервер должен отдавать поток, а не один большой JSON-массив — отсюда форматы `application/x-ndjson` (по объекту на строку) или `text/event-stream` (SSE).

Так можно перекачивать гигабайтные ответы при постоянном потреблении памяти. Backpressure при этом регулирует темп: оператор `buffer(100)` копит элементы пачками, а `flatMap` сохраняет их, не давая источнику обгонять потребителя.

```java
// Стриминг NDJSON
Flux<UserDto> streamedUsers = webClient.get()
        .uri("/users/stream")
        .accept(MediaType.APPLICATION_NDJSON)
        .retrieve()
        .bodyToFlux(UserDto.class);

// Обработка с backpressure
streamedUsers
        .buffer(100)           // буферизация пачками по 100
        .flatMap(batch -> userRepository.saveAll(batch))
        .subscribe();
```

## Q15. Как добавить ExchangeFilterFunction для логирования?

`ExchangeFilterFunction` — это перехватчик для исходящих вызовов `WebClient` (аналог `HandlerInterceptor`/`WebFilter`, но на стороне клиента). Он встраивается в цепочку запрос → ответ и видит каждый вызов: удобно для логирования, добавления заголовков (например, токена авторизации), сбора метрик.

Есть две фабрики:
- **`ofRequestProcessor`** — обрабатывает исходящий запрос (логировать метод/URL, дописать заголовок).
- **`ofResponseProcessor`** — обрабатывает входящий ответ (проверить статус, залогировать ошибки).

Фильтры регистрируются на билдере через `.filter(...)` и применяются ко всем запросам этого клиента в порядке добавления.

```java
ExchangeFilterFunction loggingFilter = ExchangeFilterFunction.ofRequestProcessor(
        request -> {
            log.info("WebClient request: {} {}", request.method(), request.url());
            return Mono.just(request);
        });

ExchangeFilterFunction errorHandler = ExchangeFilterFunction.ofResponseProcessor(
        response -> {
            if (response.statusCode().isError()) {
                log.warn("WebClient error response: {}", response.statusCode());
            }
            return Mono.just(response);
        });

WebClient webClient = WebClient.builder()
        .filter(loggingFilter)
        .filter(errorHandler)
        .build();
```

## Q16. Что такое Server-Sent Events и как их реализовать в WebFlux?

`SSE` (Server-Sent Events) — простой однонаправленный протокол поверх обычного HTTP: клиент открывает одно соединение, а сервер пушит в него события по мере появления (content-type `text/event-stream`). Браузер сам переподключается при обрыве — отдельный клиентский код для этого не нужен.

В WebFlux SSE поддержан нативно — достаточно вернуть `Flux` с правильным content-type, и фреймворк сам стримит элементы:
- **`Flux<ServerSentEvent<T>>`** — полный контроль над событием: `id` (для возобновления), `event` (тип), `data`, `comment` (heartbeat для удержания соединения).
- **`Flux<T>` + `produces = TEXT_EVENT_STREAM_VALUE`** — упрощённый вариант: WebFlux сам обернёт каждый элемент в SSE-кадр.

**Сценарий применения:** новостные ленты, котировки, прогресс длительной задачи — всё, где данные текут от сервера к клиенту.

```java
@GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> streamEvents() {
    return Flux.interval(Duration.ofSeconds(1))
            .map(sequence -> ServerSentEvent.<String>builder()
                    .id(String.valueOf(sequence))
                    .event("message")
                    .data("Event #" + sequence)
                    .comment("heartbeat")
                    .build());
}

// Или просто Flux<String> — WebFlux сам добавит SSE-заголовки
@GetMapping(value = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<PriceDto> streamPrices() {
    return priceService.getPriceUpdates(); // Flux<PriceDto>
}
```

## Q17. Чем SSE отличается от WebSocket?

Коротко: SSE — однонаправленный поток сервер → клиент поверх обычного HTTP; WebSocket — полнодуплексный канал с собственным протоколом. SSE проще (это просто долгоживущий HTTP-ответ, работает через прокси и с авто-переподключением), но не умеет слать данные от клиента к серверу. WebSocket мощнее и двунаправлен, но требует upgrade-handshake и ручного управления переподключением.

**Как выбрать:** нужен только пуш от сервера (ленты, уведомления, статус) — берите SSE; нужен обмен в обе стороны (чат, игры, совместное редактирование) — WebSocket.

| Аспект | SSE | WebSocket |
|---|---|---|
| Направление | Только сервер → клиент | Двунаправленное |
| Протокол | HTTP/1.1 | WS (upgrade от HTTP) |
| Переподключение | Автоматическое | Ручное |
| Формат | Текст | Текст или бинарный |
| Backpressure | Нет | Нет из коробки |
| Использование | Новостные ленты, статус задачи | Чат, игры, совместное редактирование |

## Q18. Как обрабатывать ошибки в WebFlux-контроллерах?

Так же, как в Spring MVC: глобально через `@ExceptionHandler` в классе с `@RestControllerAdvice`. Разница лишь в типе возврата — метод возвращает `Mono<ResponseEntity<T>>` (или сам `ResponseEntity`), а не блокирующий объект.

Принцип: на каждый класс исключений — свой метод-обработчик, который маппит ошибку в HTTP-статус и тело. Полезно завести и «catch-all» метод на `Exception` — он логирует непредвиденные ошибки и отдаёт безопасный 500, не светя стектрейс наружу. Этот подход покрывает аннотационные контроллеры; для функционального роутинга и фильтров нужен `WebExceptionHandler` (см. Q19).

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(UserNotFoundException ex) {
        return Mono.just(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage())));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(ValidationException ex) {
        return Mono.just(ResponseEntity
                .badRequest()
                .body(new ErrorResponse(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return Mono.just(ResponseEntity
                .internalServerError()
                .body(new ErrorResponse("Internal server error")));
    }
}
```

## Q19. Что такое WebExceptionHandler?

`WebExceptionHandler` — низкоуровневый обработчик ошибок на уровне всего веб-слоя: он перехватывает исключения раньше, чем `@ExceptionHandler`, и работает с сырым `ServerWebExchange`. Нужен, когда ошибка возникает там, куда `@RestControllerAdvice` не дотягивается, — в `WebFilter`, в функциональном роутере или в самой инфраструктуре WebFlux.

**Нюанс с порядком:** свой обработчик помечают `@Order` со значением меньше -1, чтобы он встал перед штатным `DefaultErrorWebExceptionHandler` (у того порядок -1). Поскольку API здесь сырое, ответ формируется вручную — через `exchange.getResponse()` и запись `DataBuffer`. Необработанные исключения нужно пробрасывать дальше (`Mono.error(ex)`), иначе они «проглотятся».

```java
@Component
@Order(-2) // Выше DefaultErrorWebExceptionHandler (порядок -1)
public class CustomWebExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof AccessDeniedException) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("{\"error\":\"Access denied\"}".getBytes(StandardCharsets.UTF_8));
            exchange.getResponse().getHeaders()
                    .setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
        return Mono.error(ex); // Передать дальше
    }
}
```

## Q20. Как вернуть кастомный HTTP-статус при ошибке?

Способ зависит от того, где возникает ошибка — внутри вашего сервиса или при вызове внешнего по `WebClient`:

- **`@ResponseStatus` на исключении** — самый простой путь для своих ошибок: бросаете типизированное исключение (например, `UserNotFoundException` с `@ResponseStatus(NOT_FOUND)`), и Spring сам выставит нужный статус. В реактивной цепочке это обычно `switchIfEmpty(Mono.error(...))`.
- **`onStatus` в `WebClient`** — для ответов внешнего сервиса: ловите конкретный статус (404, 5xx) и превращаете его в своё доменное исключение, не давая «чужому» коду протечь к клиенту.
- **`WebExchangeBindException`** — формируется автоматически при провале валидации `@Valid` и маппится в 400.

```java
// В контроллере через исключение
@GetMapping("/{id}")
public Mono<UserDto> getById(@PathVariable Long id) {
    return userService.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)));
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public static class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }
}

// В WebClient — обработка статуса ответа
webClient.get().uri("/users/{id}", id)
        .retrieve()
        .onStatus(status -> status.value() == 404,
                response -> Mono.error(new UserNotFoundException(id)))
        .onStatus(HttpStatusCode::is5xxServerError,
                response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new ServiceException(body))))
        .bodyToMono(UserDto.class);
```

## Q21. Как работает onErrorResume/onErrorReturn?

Это операторы восстановления после ошибки в реактивной цепочке. Когда апстрим бросает ошибку, нормальный поток обрывается, и эти операторы перехватывают её и продолжают вместо завершения с исключением. Выбор оператора зависит от того, что нужно подставить взамен:

- **`onErrorReturn(T)`** — подставить готовое статичное значение (константный fallback). Самый простой случай.
- **`onErrorResume(Throwable -> Mono<T>)`** — переключиться на целую запасную последовательность, которую ещё надо вычислить (сходить в кэш, в другой сервис). Можно фильтровать по типу исключения и выстраивать цепочку fallback'ов от частного к общему.
- **`onErrorMap(...)`** — не восстанавливает, а преобразует одно исключение в другое (например, низкоуровневое `DatabaseException` в доменное `ServiceException`), чтобы наружу шли осмысленные ошибки.

```java
// onErrorReturn — fallback значение
Mono<UserDto> user = userService.findById(id)
        .onErrorReturn(new UserDto(-1L, "Unknown"));

// onErrorResume — запасная логика
Mono<UserDto> userWithFallback = userService.findById(id)
        .onErrorResume(DatabaseException.class, ex ->
                cacheService.findById(id))          // попробовать кэш
        .onErrorResume(ex ->
                Mono.just(UserDto.defaultUser()));  // крайний запасной вариант

// onErrorMap — преобразование ошибки
Mono<UserDto> withMappedError = userService.findById(id)
        .onErrorMap(DatabaseException.class,
                ex -> new ServiceException("DB unavailable", ex));
```

## Q22. Как настроить Spring Security для WebFlux?

Конфигурация концептуально та же, что в MVC, но на реактивных типах. Вместо `SecurityFilterChain` объявляют бин `SecurityWebFilterChain`, а правила строят через `ServerHttpSecurity` (реактивный аналог `HttpSecurity`). Включается всё аннотацией `@EnableWebFluxSecurity`; для метод-секьюрити (`@PreAuthorize`) добавляют `@EnableReactiveMethodSecurity`.

Отличия в нейминге API: `authorizeExchange` вместо `authorizeRequests`, `pathMatchers` вместо `antMatchers`, `anyExchange` вместо `anyRequest`. Логика та же — публичные пути через `permitAll`, остальное — по ролям и `authenticated`.

```java
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/public/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/users").hasRole("USER")
                        .pathMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()))
                .build();
    }
}
```

## Q23. Что такое ReactiveUserDetailsService?

Реактивный аналог `UserDetailsService`: интерфейс, по которому Spring Security при аутентификации по логину/паролю достаёт пользователя. Разница в сигнатуре — метод `findByUsername` возвращает `Mono<UserDetails>` вместо синхронного `UserDetails`, поэтому поиск в реактивном репозитории (R2DBC, reactive Mongo) не блокирует поток.

**Нюанс:** если пользователь не найден, нельзя вернуть `null` — в реактивном мире это пустой `Mono`. Чтобы Security получила корректную ошибку, пустоту явно превращают в `UsernameNotFoundException` через `switchIfEmpty(Mono.error(...))`.

```java
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> User.withUsername(user.username())
                        .password(user.passwordHash())
                        .roles(user.roles().toArray(String[]::new))
                        .build())
                .switchIfEmpty(Mono.error(
                        new UsernameNotFoundException("User not found: " + username)));
    }
}
```

## Q24. Как получить текущего пользователя в реактивном контексте?

В WebFlux нельзя пользоваться обычным `SecurityContextHolder` — он хранит контекст в `ThreadLocal`, а реактивная цепочка перескакивает между потоками, и ThreadLocal «потеряется». Вместо этого контекст безопасности живёт в Reactor Context и достаётся реактивно:

- **`ReactiveSecurityContextHolder.getContext()`** — возвращает `Mono<SecurityContext>`; из него цепочкой `map`/`flatMap` берут `Authentication` и имя пользователя. Подходит для сервисного слоя.
- **`@AuthenticationPrincipal`** — в контроллере; принципала можно объявить как `Mono<UserDetails>` и дождаться его в цепочке.

```java
// В сервисе
public Mono<UserDto> getCurrentUser() {
    return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getName)
            .flatMap(userRepository::findByUsername);
}

// В контроллере через @AuthenticationPrincipal
@GetMapping("/me")
public Mono<UserDto> getMe(@AuthenticationPrincipal Mono<UserDetails> principal) {
    return principal.flatMap(user -> userService.findByUsername(user.getUsername()));
}
```

## Q25. Как применять @PreAuthorize в WebFlux?

Сначала включают метод-секьюрити аннотацией `@EnableReactiveMethodSecurity` (реактивный аналог `@EnableMethodSecurity`) — без неё `@PreAuthorize` просто не сработает. После этого аннотация навешивается на методы, возвращающие `Mono`/`Flux`, и проверка прав происходит реактивно — в момент подписки на результат, а не при вызове метода.

Внутри выражения доступны те же SpEL-конструкции: проверка роли (`hasRole('ADMIN')`) и сравнение с текущим пользователем (`#username == authentication.name`) для правила «админ видит всех, обычный пользователь — только себя».

```java
@Service
public class AdminService {

    @PreAuthorize("hasRole('ADMIN')")
    public Flux<UserDto> getAllUsers() {
        return userRepository.findAll().map(userMapper::toDto);
    }

    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public Mono<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }
}
```

## Q26. Как настроить codec для больших тел запросов?

По умолчанию WebFlux буферизует в памяти не больше `256KB` тела (`maxInMemorySize`) — это защита от OOM при больших или вредоносных запросах. Если легитимные тела крупнее, при превышении лимита прилетит `DataBufferLimitException`, и его нужно поднять.

Сделать это можно двумя путями:
- **Кодом** — реализовать `WebFluxConfigurer` и переопределить `configureHttpMessageCodecs`, выставив нужный `maxInMemorySize`.
- **Конфигом** — свойство `spring.codec.max-in-memory-size` в `application.yml`.

**Рекомендация:** поднимать лимит ровно до нужного, а по-настоящему большие данные (файлы) стримить через `Flux<DataBuffer>`, а не держать в памяти.

```java
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10 MB
    }
}

// Или через application.yml
// spring.codec.max-in-memory-size: 10MB
```

## Q27. Зачем избегать блокирующих операций в event loop?

Потому что в WebFlux мало потоков, и каждый из них держит тысячи соединений. Event loop в Netty — это всего несколько потоков; стоит одному из них «зависнуть» на `Thread.sleep`, `block()` или JDBC-вызове — и все соединения, привязанные к этому потоку, замирают вместе с ним. В модели «поток на запрос» (MVC) блокировка тормозит один запрос; в WebFlux — целую пачку.

**Симптомы:** под нагрузкой растут latency и таймауты, хотя CPU недогружен — классический признак заблокированного event loop. **Решение:** любую неизбежно блокирующую работу (legacy-клиент, JDBC) выносить на отдельный пул `Schedulers.boundedElastic()` через `subscribeOn` (см. Q28).

```java
// НЕПРАВИЛЬНО — блокируем event loop
@GetMapping("/users/{id}")
public Mono<UserDto> getUser(@PathVariable Long id) {
    UserDto user = jdbcTemplate.queryForObject(...); // блокирует!
    return Mono.just(user);
}

// ПРАВИЛЬНО — переносим блокирующую работу на boundedElastic
@GetMapping("/users/{id}")
public Mono<UserDto> getUser(@PathVariable Long id) {
    return Mono.fromCallable(() -> jdbcTemplate.queryForObject(...))
               .subscribeOn(Schedulers.boundedElastic());
}
```

## Q28. Как выполнить блокирующий вызов безопасно в WebFlux?

Идея — увести блокирующий вызов с event loop на специальный пул, чтобы он не замораживал обработку других соединений. Делается это в три шага:

1. Обернуть блокирующий вызов в `Mono.fromCallable(...)` (или `Mono.fromSupplier`) — он выполнится лениво, при подписке.
2. Перевести его на отдельный пул через `.subscribeOn(Schedulers.boundedElastic())`. Этот шедулер создан именно для блокирующих операций: он динамически расширяется и ограничен сверху (по умолчанию ~10× числа ядер) с очередью задач — поэтому всплеск блокирующих вызовов не породит бесконечное число потоков.
3. Подстраховаться `timeout(...)`, чтобы зависший legacy не держал поток вечно, и при необходимости смаппить `TimeoutException` в доменную ошибку.

**Важно:** так делают только для неизбежно блокирующих интеграций (JDBC, старые SDK). Реактивные драйверы (R2DBC, reactive Mongo) переносить на `boundedElastic` не нужно — они и так неблокирующие.

```java
@Service
public class LegacyIntegrationService {

    private final LegacyClient legacyClient; // блокирующий клиент

    public Mono<LegacyResponse> callLegacy(String request) {
        return Mono.fromCallable(() -> legacyClient.call(request)) // блокирующий вызов
                   .subscribeOn(Schedulers.boundedElastic())        // на отдельном пуле
                   .timeout(Duration.ofSeconds(5))                  // таймаут
                   .onErrorMap(TimeoutException.class,
                               ex -> new ServiceTimeoutException("Legacy timeout"));
    }
}
```

## See also

- [Project Reactor](project-reactor-interview.md)
- [Reactive Streams](reactive-streams-interview.md)
- [RxJava](rxjava-interview.md)
- [Reactive Patterns](reactive-patterns-interview.md)
- [Тестирование реактивного кода](reactive-testing-interview.md)
- [Spring Framework](../frameworks/spring/spring-framework-interview.md)
- [Spring Boot](../frameworks/spring/spring-boot-interview.md)
