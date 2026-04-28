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

`Spring MVC` — синхронная/блокирующая модель на основе Servlet API (один поток на запрос).  
`Spring WebFlux` — реактивная/неблокирующая модель: небольшое число потоков обрабатывает все запросы через событийный цикл.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// WebFlux
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return userService.findById(id); // не блокирует
}
```

## Q2. Когда использовать WebFlux, а когда MVC?

Выбор зависит от характера нагрузки и экосистемы.

**Предпочесть WebFlux:**
- Много конкурентных соединений с долгим ожиданием I/O (API-gateway, чаты, стриминг)
- Нужна нативная поддержка SSE / WebSocket
- Все зависимости — реактивные (R2DBC, реактивный Redis, reactive Mongo)

**Предпочесть Spring MVC:**
- Команда не знакома с реактивным программированием
- Есть блокирующие зависимости (JDBC, блокирующие клиенты)
- Простые CRUD-операции с небольшим числом пользователей


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> Не стоит смешивать блокирующий и реактивный I/O — выгода от WebFlux теряется при любом `block()` в event loop.

## Q3. Что такое событийный цикл (event loop) в WebFlux?

`Netty` запускает N event loop потоков (обычно `Runtime.getRuntime().availableProcessors() * 2`). Каждый поток обрабатывает множество соединений через неблокирующий I/O. При завершении I/O операции callback планируется обратно на тот же event loop.

```
Запрос → event loop thread → реактивная цепочка → I/O callback → продолжение цепочки → ответ
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
Важно: нельзя блокировать event loop (`Thread.sleep`, `block()`, JDBC) — это заморозит обработку всех соединений на данном потоке.

## Q4. Какие серверы поддерживает Spring WebFlux?

- **Netty** — по умолчанию, лучший выбор для чисто реактивных приложений
- **Undertow** — высокопроизводительный, поддерживает неблокирующий I/O
- **Tomcat 8.5+** / **Jetty 9.3+** — через Servlet 3.1 Non-Blocking I/O


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

Аннотационная модель идентична Spring MVC. Отличие — в типах возврата: контроллер должен возвращать `Mono<T>` или `Flux<T>`, а не обычные объекты.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> create(@RequestBody @Valid Mono<CreateUserRequest> request) {
        return request.flatMap(userService::create);
    }
}
```

## Q6. Что такое @RequestBody и @ResponseBody в реактивном контексте?

`@RequestBody` можно принять как `Mono<T>` — тело запроса читается лениво, только при подписке. `@ResponseBody` автоматически сериализует `Mono`/`Flux` в JSON.

```java
// Принять тело как Mono — ленивое чтение
@PutMapping("/{id}")
public Mono<UserDto> update(
        @PathVariable Long id,
        @RequestBody Mono<UpdateUserRequest> requestMono) {
    return requestMono.flatMap(req -> userService.update(id, req));
}


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// Принять тело как Flux<DataBuffer> — стриминговая загрузка файла
@PostMapping(value = "/upload", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public Mono<Void> upload(@RequestBody Flux<DataBuffer> body) {
    return DataBufferUtils.write(body, Path.of("/tmp/upload"), StandardOpenOption.CREATE);
}
```

## Q7. Что такое RouterFunction и HandlerFunction?

`RouterFunction<T>` — функциональный аналог `@RequestMapping`: принимает `ServerRequest` и возвращает `Optional<HandlerFunction<T>>`.  
`HandlerFunction<T>` — обработчик запроса: принимает `ServerRequest`, возвращает `Mono<ServerResponse>`.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
    public Mono<ServerResponse> findById(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return userService.findById(id)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
```

## Q8. Как составить сложный роутер из нескольких маршрутов?

Используется `RouterFunctions.route().nest()` для группировки по общему пути или условию.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
    return RouterFunctions.route()
            .nest(RequestPredicates.path("/api/v1/users"), () -> userRoutes)
            .nest(RequestPredicates.path("/api/v1/orders"), () -> orderRoutes)
            .build();
}
```

## Q9. Чем ServerRequest/ServerResponse отличаются от HttpServletRequest?

`ServerRequest` / `ServerResponse` — иммутабельные, реактивные. Тело читается как `Mono`/`Flux`, а не через blocking `InputStream`.

```java
public Mono<ServerResponse> create(ServerRequest request) {
    // Тело — Mono, читается неблокирующе
    return request.bodyToMono(CreateUserRequest.class)
            .flatMap(userService::create)
            .flatMap(created -> ServerResponse
                    .created(URI.create("/api/v1/users/" + created.id()))
                    .bodyValue(created));
}


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

`WebFilter` перехватывает все запросы до их обработки. Применяется глобально через Spring-бин или локально через `.filter()` на роутере.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

`WebClient` — реактивный HTTP-клиент, пришедший на смену блокирующему `RestTemplate`. Возвращает `Mono`/`Flux`, поддерживает стриминг, встроенный retry, таймауты и фильтры.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// GET — список
Flux<UserDto> users = webClient.get()
        .uri("/users")
        .retrieve()
        .bodyToFlux(UserDto.class);
```

## Q12. Как настроить таймауты в WebClient?

Таймауты настраиваются на уровне `HttpClient` (Netty) и передаются в `WebClient`.

```java
HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)   // connection timeout
        .responseTimeout(Duration.ofSeconds(10))               // response timeout
        .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)));


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
WebClient webClient = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl("https://api.example.com")
        .build();
```

## Q13. Как реализовать повторные попытки (retry) в WebClient?

Используется оператор `retryWhen` из Project Reactor с настраиваемой стратегией.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

`bodyToFlux` + `application/x-ndjson` или `text/event-stream` позволяют обрабатывать ответ построчно без загрузки всего в память.

```java
// Стриминг NDJSON
Flux<UserDto> streamedUsers = webClient.get()
        .uri("/users/stream")
        .accept(MediaType.APPLICATION_NDJSON)
        .retrieve()
        .bodyToFlux(UserDto.class);


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// Обработка с backpressure
streamedUsers
        .buffer(100)           // буферизация пачками по 100
        .flatMap(batch -> userRepository.saveAll(batch))
        .subscribe();
```

## Q15. Как добавить ExchangeFilterFunction для логирования?

`ExchangeFilterFunction` — аналог `HandlerInterceptor` для `WebClient`. Применяется в цепочке запрос/ответ.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
WebClient webClient = WebClient.builder()
        .filter(loggingFilter)
        .filter(errorHandler)
        .build();
```

## Q16. Что такое Server-Sent Events и как их реализовать в WebFlux?

`SSE` — однонаправленный протокол: сервер пушит события клиенту через одно HTTP-соединение (`text/event-stream`). WebFlux нативно поддерживает SSE через возврат `Flux<ServerSentEvent<T>>`.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// Или просто Flux<String> — WebFlux сам добавит SSE-заголовки
@GetMapping(value = "/prices", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<PriceDto> streamPrices() {
    return priceService.getPriceUpdates(); // Flux<PriceDto>
}
```

## Q17. Чем SSE отличается от WebSocket?


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
| Аспект | SSE | WebSocket |
|---|---|---|
| Направление | Только сервер → клиент | Двунаправленное |
| Протокол | HTTP/1.1 | WS (upgrade от HTTP) |
| Переподключение | Автоматическое | Ручное |
| Формат | Текст | Текст или бинарный |
| Backpressure | Нет | Нет из коробки |
| Использование | Новостные ленты, статус задачи | Чат, игры, совместное редактирование |

## Q18. Как обрабатывать ошибки в WebFlux-контроллерах?

Используется `@ExceptionHandler` в `@RestControllerAdvice` — работает так же, как в MVC, но возвращает `Mono<ResponseEntity<T>>`.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

`WebExceptionHandler` — низкоуровневый обработчик, перехватывает исключения до `@ExceptionHandler`. Используется, когда нужно обработать ошибки на уровне `WebFilter` или функционального роутера.

```java
@Component
@Order(-2) // Выше DefaultErrorWebExceptionHandler (порядок -1)
public class CustomWebExceptionHandler implements WebExceptionHandler {


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

Несколько способов: `@ResponseStatus`, `onStatus` в `WebClient`, или через `WebExchangeBindException`.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

- `onErrorReturn(T)` — заменяет ошибку заданным значением
- `onErrorResume(Throwable -> Mono<T>)` — переключается на запасную последовательность

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// onErrorMap — преобразование ошибки
Mono<UserDto> withMappedError = userService.findById(id)
        .onErrorMap(DatabaseException.class,
                ex -> new ServiceException("DB unavailable", ex));
```

## Q22. Как настроить Spring Security для WebFlux?

Используется `SecurityWebFilterChain` вместо `SecurityFilterChain`. Конфигурация через `ServerHttpSecurity`.

```java
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

Реактивный аналог `UserDetailsService`. Используется Spring Security для аутентификации по логину/паролю.

```java
@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
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

Использовать `ReactiveSecurityContextHolder` — реактивный аналог `SecurityContextHolder`.

```java
// В сервисе
public Mono<UserDto> getCurrentUser() {
    return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getName)
            .flatMap(userRepository::findByUsername);
}


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// В контроллере через @AuthenticationPrincipal
@GetMapping("/me")
public Mono<UserDto> getMe(@AuthenticationPrincipal Mono<UserDetails> principal) {
    return principal.flatMap(user -> userService.findByUsername(user.getUsername()));
}
```

## Q25. Как применять @PreAuthorize в WebFlux?

После включения `@EnableReactiveMethodSecurity` аннотация работает реактивно: проверяет права при подписке на `Mono`/`Flux`.

```java
@Service
public class AdminService {

    @PreAuthorize("hasRole('ADMIN')")
    public Flux<UserDto> getAllUsers() {
        return userRepository.findAll().map(userMapper::toDto);
    }


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public Mono<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(userMapper::toDto);
    }
}
```

## Q26. Как настроить codec для больших тел запросов?

По умолчанию WebFlux ограничивает размер тела `256KB`. Лимит настраивается через `WebFluxConfigurer`.

```java
@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10 MB
    }
}


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// Или через application.yml
// spring.codec.max-in-memory-size: 10MB
```

## Q27. Зачем избегать блокирующих операций в event loop?

Event loop в Netty — это несколько потоков, каждый из которых обрабатывает тысячи соединений. Один `Thread.sleep` или JDBC-вызов заблокирует обработку всех соединений на данном потоке.

```java
// НЕПРАВИЛЬНО — блокируем event loop
@GetMapping("/users/{id}")
public Mono<UserDto> getUser(@PathVariable Long id) {
    UserDto user = jdbcTemplate.queryForObject(...); // блокирует!
    return Mono.just(user);
}


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
// ПРАВИЛЬНО — переносим блокирующую работу на boundedElastic
@GetMapping("/users/{id}")
public Mono<UserDto> getUser(@PathVariable Long id) {
    return Mono.fromCallable(() -> jdbcTemplate.queryForObject(...))
               .subscribeOn(Schedulers.boundedElastic());
}
```

## Q28. Как выполнить блокирующий вызов безопасно в WebFlux?

`Schedulers.boundedElastic()` — пул потоков специально для блокирующих операций. Динамически расширяется, имеет максимальный размер очереди.

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


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Вариант А | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант В | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
> - [ ] Вариант С | Почему неверно 2-3 предложения Частая ошибка в реальном коде.
- [Project Reactor](project-reactor-interview.md)
- [Reactive Streams](reactive-streams-interview.md)
- [RxJava](rxjava-interview.md)
- [Reactive Patterns](reactive-patterns-interview.md)
- [Тестирование реактивного кода](reactive-testing-interview.md)
- [Spring Framework](../frameworks/spring/spring-framework-interview.md)
- [Spring Boot](../frameworks/spring/spring-boot-interview.md)
