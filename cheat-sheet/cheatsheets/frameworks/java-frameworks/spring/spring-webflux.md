# Spring WebFlux для Java

Комплексное руководство по Spring WebFlux: реактивному веб-фреймворку для создания неблокирующих, асинхронных веб-приложений с использованием Project Reactor. Подробно рассматриваются reactive streams, Mono/Flux, WebClient, functional endpoints, backpressure, concurrency и production deployment.

**Дата последнего обновления:** 2026-01-22

## Полезные ссылки

### Официальная документация
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) - Полная документация Spring WebFlux
- [Project Reactor](https://projectreactor.io/docs/core/release/reference/) - Документация Project Reactor
- [Reactive Streams](https://www.reactive-streams.org/) - Спецификация Reactive Streams

### Книги и ресурсы
- [Reactive Programming with RxJava](https://www.oreilly.com/library/view/reactive-programming-with/9781491931646/) - Классическая книга по реактивному программированию
- [Hands-On Reactive Programming in Spring 5](https://www.packtpub.com/product/hands-on-reactive-programming-in-spring-5/9781787284951) - Практическое руководство
- [Reactive Systems in Java](https://www.oreilly.com/library/view/reactive-systems-in/9781492091722/) - Современное руководство

### Статьи и туториалы
- [Spring WebFlux Tutorial](https://spring.io/guides/gs/reactive-rest-service/) - Официальный туториал
- [Reactive Programming](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-reactive-programming) - Концепции реактивного программирования
- [Backpressure in Reactive Streams](https://www.voxxed.com/2016/10/backpressure-reactive-streams/) - Backpressure объяснение

### См. также
- `frameworks/spring/spring-boot.md` - Spring Boot основы
- `frameworks/spring/spring-mvc.md` - Spring MVC (blocking)
- `frameworks/spring/spring-webflux.md` - Текущий файл
- `monitoring/observability.md` - Мониторинг реактивных приложений

## Содержание

- [Введение в Spring WebFlux](#введение-в-spring-webflux)
- [Reactive Programming основы](#reactive-programming-основы)
- [Project Reactor](#project-reactor)
- [Spring WebFlux архитектура](#spring-webflux-архитектура)
- [Функциональные endpoints](#функциональные-endpoints)
- [Аннотационные контроллеры](#аннотационные-контроллеры)
- [WebClient](#webclient)
- [Обработка ошибок](#обработка-ошибок)
- [Backpressure](#backpressure)
- [Тестирование](#тестирование)
- [Производительность и оптимизация](#производительность-и-оптимизация)
- [Production deployment](#production-deployment)
- [Best practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в Spring WebFlux

**Spring WebFlux** — это реактивный веб-фреймворк, построенный на принципах reactive programming. Он позволяет создавать неблокирующие, асинхронные веб-приложения, которые могут эффективно обрабатывать большое количество одновременных подключений с минимальным использованием ресурсов.

### Почему WebFlux?

WebFlux решает критические проблемы традиционных блокирующих веб-фреймворков:

1. **Высокая concurrency** — эффективная обработка тысяч одновременных подключений
2. **Resource efficiency** — минимальное использование памяти и CPU
3. **Elastic scaling** — автоматическое масштабирование под нагрузку
4. **Backpressure** — контроль потока данных для предотвращения перегрузки
5. **Functional programming** — функциональный стиль программирования
6. **Non-blocking I/O** — асинхронный ввод/вывод
7. **Reactive streams** — стандартизированный подход к reactive programming
8. **Integration** — бесшовная интеграция с reactive базами данных и системами

### Когда использовать WebFlux?

#### ✅ Идеально подходит для:
- **High-concurrency applications** — приложения с высокой одновременной нагрузкой
- **Microservices** — асинхронная коммуникация между сервисами
- **Real-time applications** — WebSocket, Server-Sent Events
- **Streaming data** — обработка потоков данных
- **I/O bound applications** — приложения с интенсивным I/O
- **Cloud-native applications** — масштабируемые облачные приложения
- **Reactive systems** — системы построенные на reactive принципах

#### ❌ Не подходит для:
- **Simple CRUD applications** — используйте Spring MVC
- **Blocking operations** — если весь код блокирующий
- **Small applications** — overhead не оправдан
- **Legacy integration** — если нужно интегрироваться с блокирующими API
- **Simple REST APIs** — Spring MVC проще для базовых задач
- **File uploads** — Spring MVC лучше для больших файлов

### Архитектурные преимущества

#### Non-blocking I/O
```java
// Traditional blocking approach
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    // Thread blocks waiting for database
    User user = userRepository.findById(id).get();  // BLOCKING
    return user;
}

// Reactive non-blocking approach
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    // Thread returns immediately, result delivered asynchronously
    return userRepository.findById(id);  // NON-BLOCKING
}
```

#### Backpressure handling
```java
// Consumer controls data flow
Flux<String> data = Flux.just("item1", "item2", "item3", "item4", "item5");

data.onBackpressureBuffer(2)  // Buffer only 2 items
    .delayElements(Duration.ofMillis(100))  // Process slowly
    .subscribe(
        item -> System.out.println("Processed: " + item),
        error -> System.err.println("Error: " + error),
        () -> System.out.println("Completed")
    );
```

## Reactive Programming основы

### Reactive Streams спецификация

**Reactive Streams** — это стандарт для асинхронной обработки потоков данных с non-blocking backpressure. Спецификация определяет четыре основных интерфейса:

#### Publisher
```java
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}
```

Publisher — это источник данных, который может быть подписан subscriber'ом. Publisher отвечает за:
- **Data production** — генерацию элементов данных
- **Subscription management** — управление подписками
- **Demand signaling** — реагирование на запросы данных от subscriber'а

#### Subscriber
```java
public interface Subscriber<T> {
    void onSubscribe(Subscription subscription);
    void onNext(T item);
    void onError(Throwable throwable);
    void onComplete();
}
```

Subscriber — это потребитель данных, который получает уведомления о:
- **Subscription** — успешной подписке
- **Next item** — следующем элементе данных
- **Error** — ошибке в потоке
- **Completion** — завершении потока

#### Subscription
```java
public interface Subscription {
    void request(long n);
    void cancel();
}
```

Subscription — это связь между Publisher и Subscriber, которая позволяет:
- **Request data** — запрашивать определенное количество элементов
- **Cancel subscription** — отменять подписку

#### Processor
```java
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```

Processor — это одновременно и Subscriber и Publisher, позволяющий трансформировать данные в потоке.

### Reactive vs Imperative

#### Imperative подход
```java
// Synchronous, blocking
public List<User> getUsers() {
    List<User> users = new ArrayList<>();

    for (Long userId : userIds) {
        User user = userRepository.findById(userId);  // BLOCKS thread
        users.add(user);
    }

    return users;  // Returns when all data is ready
}

// Problems:
// - Thread blocks waiting for each database call
// - Cannot handle large datasets efficiently
// - No backpressure - consumer cannot control data flow
// - No error recovery - if one fails, whole operation fails
```

#### Reactive подход
```java
// Asynchronous, non-blocking
public Flux<User> getUsers(Flux<Long> userIds) {
    return userIds
        .flatMap(userId -> userRepository.findById(userId))  // NON-BLOCKING
        .onErrorContinue((error, item) -> {                   // ERROR RESILIENCE
            log.error("Error processing user: " + item, error);
        })
        .buffer(10)                                           // BACKPRESSURE
        .delayElements(Duration.ofMillis(100));              // FLOW CONTROL
}

// Benefits:
// - Thread never blocks - returns immediately
// - Efficient resource usage - no thread per request
// - Backpressure - consumer controls data flow
// - Error resilience - can continue processing on errors
// - Composable - can chain multiple operations
```

## Project Reactor

### Mono и Flux

#### Mono
Mono представляет 0 или 1 элемент:

```java
// Empty Mono
Mono<Void> emptyMono = Mono.empty();

// Mono with value
Mono<String> monoWithValue = Mono.just("Hello");

// Mono from Callable (lazy)
Mono<String> lazyMono = Mono.fromCallable(() -> {
    System.out.println("Computing value...");
    return "Computed value";
});

// Mono from Future
Mono<String> monoFromFuture = Mono.fromFuture(completableFuture);

// Mono with error
Mono<String> errorMono = Mono.error(new RuntimeException("Something went wrong"));

// Mono with delay
Mono<String> delayedMono = Mono.just("Delayed")
    .delayElement(Duration.ofSeconds(1));
```

**Mono use cases:**
- **Single result** — результат database query (findById)
- **Optional value** — может быть пустым или содержать значение
- **Asynchronous computation** — результат длительной операции
- **HTTP response** — тело HTTP ответа
- **Configuration value** — получение конфигурационного параметра

#### Flux
Flux представляет 0 или N элементов (поток):

```java
// Empty Flux
Flux<Void> emptyFlux = Flux.empty();

// Flux with values
Flux<String> fluxWithValues = Flux.just("item1", "item2", "item3");

// Flux from Iterable
Flux<String> fluxFromList = Flux.fromIterable(Arrays.asList("a", "b", "c"));

// Flux from Stream
Flux<Integer> fluxFromStream = Flux.fromStream(Stream.of(1, 2, 3, 4, 5));

// Flux with range
Flux<Integer> rangeFlux = Flux.range(1, 100);

// Infinite Flux
Flux<Long> infiniteFlux = Flux.interval(Duration.ofSeconds(1));

// Flux with error
Flux<String> errorFlux = Flux.error(new RuntimeException("Flux error"));
```

**Flux use cases:**
- **Collections** — обработка списков элементов
- **Database results** — результаты query с множеством строк
- **File processing** — чтение файла построчно
- **Event streams** — поток событий или сообщений
- **Pagination** — постраничная загрузка данных
- **Time series** — временные ряды данных

### Operators

#### Transforming operators

**map()** — синхронная трансформация каждого элемента:
```java
Flux<String> names = Flux.just("john", "jane", "bob");

Flux<String> upperCaseNames = names.map(String::toUpperCase);
// Output: JOHN, JANE, BOB

Flux<User> users = userIds.map(id -> userRepository.findById(id));
// Each ID becomes a User object
```

**flatMap()** — асинхронная трансформация с возможностью изменения количества элементов:
```java
Flux<Long> userIds = Flux.just(1L, 2L, 3L);

Flux<User> users = userIds.flatMap(id -> userRepository.findById(id));
// Each ID becomes a Mono<User>, flattened into Flux<User>

Flux<Order> orders = userIds.flatMap(userId ->
    orderRepository.findOrdersByUserId(userId));
// One user can have multiple orders
```

**concatMap()** — последовательная асинхронная трансформация:
```java
Flux<Long> userIds = Flux.just(1L, 2L, 3L);

Flux<User> users = userIds.concatMap(id -> userRepository.findById(id));
// Processes user 1, then user 2, then user 3 (maintains order)
```

#### Filtering operators

**filter()** — фильтрация элементов:
```java
Flux<User> activeUsers = allUsers.filter(User::isActive);

Flux<Integer> evenNumbers = Flux.range(1, 10)
    .filter(number -> number % 2 == 0);
// Output: 2, 4, 6, 8, 10
```

**distinct()** — удаление дубликатов:
```java
Flux<String> uniqueNames = names.distinct();

Flux<User> uniqueUsers = users.distinct(User::getEmail);
```

**take() / skip()** — ограничение количества элементов:
```java
Flux<Integer> first5 = Flux.range(1, 100).take(5);
// Output: 1, 2, 3, 4, 5

Flux<Integer> skip10 = Flux.range(1, 100).skip(10);
// Output: 11, 12, 13, ...
```

#### Combining operators

**zip()** — комбинация элементов из нескольких потоков:
```java
Flux<String> names = Flux.just("John", "Jane", "Bob");
Flux<Integer> ages = Flux.just(25, 30, 35);

Flux<String> combined = Flux.zip(names, ages)
    .map(tuple -> tuple.getT1() + " is " + tuple.getT2() + " years old");
// Output: John is 25 years old, Jane is 30 years old, Bob is 35 years old
```

**merge()** — слияние потоков без сохранения порядка:
```java
Flux<String> stream1 = Flux.just("A", "B").delayElements(Duration.ofMillis(100));
Flux<String> stream2 = Flux.just("1", "2").delayElements(Duration.ofMillis(150));

Flux<String> merged = Flux.merge(stream1, stream2);
// Possible output: A, 1, B, 2 (order not guaranteed)
```

**concat()** — последовательное объединение потоков:
```java
Flux<String> stream1 = Flux.just("A", "B");
Flux<String> stream2 = Flux.just("1", "2");

Flux<String> concatenated = Flux.concat(stream1, stream2);
// Output: A, B, 1, 2 (order preserved)
```

#### Error handling operators

**onErrorReturn()** — возвращение fallback значения при ошибке:
```java
Mono<User> user = userRepository.findById(userId)
    .onErrorReturn(new User("default", "user"));
// If database error, returns default user
```

**onErrorResume()** — продолжение с альтернативным потоком:
```java
Mono<User> user = userRepository.findById(userId)
    .onErrorResume(error -> {
        if (error instanceof NotFoundException) {
            return userCache.getUser(userId);  // Try cache
        } else {
            return Mono.error(error);  // Re-throw other errors
        }
    });
```

**retry()** — повторение операции при ошибке:
```java
Mono<User> user = userRepository.findById(userId)
    .retry(3)  // Retry up to 3 times
    .timeout(Duration.ofSeconds(5));  // With timeout
```

**onErrorContinue()** — продолжение обработки при ошибке в элементе:
```java
Flux<User> users = userIds
    .flatMap(id -> userRepository.findById(id))
    .onErrorContinue((error, item) -> {
        log.error("Error processing user {}: {}", item, error);
        // Continue processing other users
    });
```

### Schedulers

#### publishOn() — переключение downstream execution
```java
Mono<String> result = Mono.fromCallable(() -> {
        // Execute on calling thread
        return blockingDatabaseCall();
    })
    .publishOn(Schedulers.boundedElastic())  // Switch to elastic thread pool
    .map(data -> {
        // Execute on elastic thread pool
        return processData(data);
    })
    .publishOn(Schedulers.parallel())  // Switch to parallel thread pool
    .map(processed -> {
        // Execute on parallel thread pool
        return serializeResult(processed);
    });
```

**Common schedulers:**
- **Schedulers.immediate()** — текущий поток (по умолчанию)
- **Schedulers.single()** — single reused thread
- **Schedulers.elastic()** — elastic thread pool (для I/O операций)
- **Schedulers.parallel()** — fixed thread pool (для CPU-bound операций)
- **Schedulers.boundedElastic()** — bounded elastic thread pool

#### subscribeOn() — переключение upstream execution
```java
Flux<String> data = Flux.fromIterable(largeList)
    .subscribeOn(Schedulers.boundedElastic())  // Upstream operations on elastic threads
    .map(item -> processItem(item))            // Still on elastic threads
    .publishOn(Schedulers.parallel())          // Switch to parallel for CPU work
    .map(processed -> transform(processed))    // On parallel threads
    .subscribe(result -> handleResult(result)); // On parallel threads
```

**subscribeOn vs publishOn:**
- **subscribeOn** — влияет на upstream operators (до publishOn)
- **publishOn** — влияет на downstream operators (после publishOn)
- **Можно использовать несколько publishOn** для разных фаз обработки

## Spring WebFlux архитектура

### HTTP Server adapters

#### Netty (по умолчанию)
```java
@Configuration
public class NettyConfig {

    @Bean
    public NettyReactiveWebServerFactory serverFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.setPort(8080);

        // Configure Netty
        factory.addServerCustomizers(server -> {
            // Event loop group configuration
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            server.group(bossGroup, workerGroup)
                  .channel(NioServerSocketChannel.class)
                  .childHandler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      protected void initChannel(SocketChannel ch) {
                          ch.pipeline().addLast(new HttpServerCodec());
                      }
                  });
        });

        return factory;
    }
}
```

**Netty advantages:**
- **High performance** — optimized for low latency and high throughput
- **Non-blocking I/O** — native support for reactive programming
- **Scalability** — handles thousands of concurrent connections
- **Customization** — extensive configuration options

#### Tomcat/Jetty
```java
@Configuration
public class TomcatConfig {

    @Bean
    public TomcatReactiveWebServerFactory tomcatFactory() {
        TomcatReactiveWebServerFactory factory = new TomcatReactiveWebServerFactory();
        factory.setPort(8080);

        factory.addServerCustomizers(server -> {
            // Tomcat specific configuration
            server.getConnector().setAsyncTimeout(30000);
            server.getConnector().setMaxKeepAliveRequests(100);
        });

        return factory;
    }
}
```

**Servlet container advantages:**
- **Compatibility** — works with existing servlet infrastructure
- **Management** — standard servlet container management
- **Debugging** — familiar servlet debugging tools

### Codec configuration

#### Jackson JSON codec
```java
@Configuration
public class CodecConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonDecoder(
            new Jackson2JsonDecoder(
                new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule())
            )
        );

        configurer.defaultCodecs().jackson2JsonEncoder(
            new Jackson2JsonEncoder(
                new ObjectMapper()
                    .registerModule(new JavaTimeModule())
            )
        );

        // Configure max in-memory size
        configurer.defaultCodecs().maxInMemorySize(512 * 1024); // 512KB
    }
}
```

**Codec responsibilities:**
- **Encoding** — преобразование Java объектов в HTTP response body
- **Decoding** — преобразование HTTP request body в Java объекты
- **Content negotiation** — выбор подходящего codec на основе Content-Type
- **Streaming** — поддержка streaming для больших payloads

### Exception handling

#### Global exception handler
```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFound(UserNotFoundException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage())));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(ValidationException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred")));
    }
}
```

**Exception handling flow:**
1. **Exception thrown** — исключение возникает в controller или service
2. **Exception propagation** — исключение распространяется через reactive chain
3. **Handler lookup** — WebFlux ищет подходящий @ExceptionHandler
4. **Error response** — handler возвращает Mono<ResponseEntity>
5. **Response encoding** — error response сериализуется и отправляется клиенту

## Функциональные endpoints

### RouterFunction

#### Basic routing
```java
@Configuration
public class FunctionalRoutes {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        return RouterFunctions.route()
            .GET("/api/users", handler::getAllUsers)
            .GET("/api/users/{id}", handler::getUserById)
            .POST("/api/users", handler::createUser)
            .PUT("/api/users/{id}", handler::updateUser)
            .DELETE("/api/users/{id}", handler::deleteUser)
            .build();
    }
}
```

**RouterFunction components:**
- **Request predicate** — условия для matching запроса (GET, POST, path pattern)
- **Handler function** — функция обработки запроса
- **ServerResponse** — реактивный HTTP response

#### Advanced routing
```java
@Configuration
public class AdvancedRoutes {

    @Bean
    public RouterFunction<ServerResponse> apiRoutes(UserHandler userHandler, OrderHandler orderHandler) {
        return RouterFunctions.route()
            // Path variables
            .GET("/api/users/{id}", userHandler::getUserById)
            .GET("/api/users/{userId}/orders", orderHandler::getUserOrders)

            // Query parameters
            .GET("/api/users", request -> {
                Optional<String> status = request.queryParam("status");
                if (status.isPresent()) {
                    return userHandler.getUsersByStatus(request);
                } else {
                    return userHandler.getAllUsers(request);
                }
            })

            // Content type matching
            .POST("/api/users", RequestPredicates.contentType(MediaType.APPLICATION_JSON), userHandler::createUser)

            // Header matching
            .GET("/api/admin/users", RequestPredicates.header("X-API-Key", "admin-key"), userHandler::getAllUsersAdmin)

            // Nested routes
            .path("/api/v2", builder -> builder
                .GET("/users", userHandler::getUsersV2)
                .POST("/users", userHandler::createUserV2)
            )

            // Filter chains
            .filter((request, next) -> {
                System.out.println("Request: " + request.method() + " " + request.path());
                return next.handle(request);
            })

            .build();
    }
}
```

### Handler functions

#### User handler
```java
@Component
public class UserHandler {

    private final UserService userService;
    private final Validator validator;

    public UserHandler(UserService userService, Validator validator) {
        this.userService = userService;
        this.validator = validator;
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        Flux<User> users = userService.getAllUsers();

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(users, User.class);
    }

    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Long userId = Long.valueOf(request.pathVariable("id"));

        return userService.getUserById(userId)
            .flatMap(user -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(CreateUserRequest.class)
            .doOnNext(this::validateRequest)
            .flatMap(createRequest -> userService.createUser(createRequest))
            .flatMap(user -> ServerResponse.created(
                    URI.create("/api/users/" + user.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user))
            .onErrorResume(ConstraintViolationException.class, ex ->
                ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new ErrorResponse("VALIDATION_ERROR", ex.getMessage())));
    }

    public Mono<ServerResponse> updateUser(ServerRequest request) {
        Long userId = Long.valueOf(request.pathVariable("id"));

        return request.bodyToMono(UpdateUserRequest.class)
            .flatMap(updateRequest -> userService.updateUser(userId, updateRequest))
            .flatMap(user -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        Long userId = Long.valueOf(request.pathVariable("id"));

        return userService.deleteUser(userId)
            .then(ServerResponse.noContent().build())
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    private void validateRequest(CreateUserRequest request) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
```

**Handler function patterns:**
- **Request extraction** — извлечение данных из ServerRequest
- **Business logic** — вызов service layer
- **Response building** — создание ServerResponse
- **Error handling** — обработка ошибок и edge cases
- **Content negotiation** — правильные media types и status codes

## Аннотационные контроллеры

### @RestController

#### Basic controller
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@RequestBody @Valid CreateUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable Long id,
                                @RequestBody @Valid UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
```

**Controller annotations:**
- **@RestController** — комбинация @Controller и @ResponseBody
- **@RequestMapping** — базовый путь для всех методов
- **@GetMapping, @PostMapping, etc.** — HTTP методы с путями
- **@PathVariable** — извлечение из URL path
- **@RequestBody** — десериализация request body
- **@ResponseStatus** — HTTP status code для response

### Advanced controller features

#### Request/response handling
```java
@RestController
@RequestMapping("/api/advanced")
public class AdvancedController {

    @GetMapping("/stream")
    public Flux<String> streamData() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> "Data item " + i)
            .take(10);  // Stream 10 items over 10 seconds
    }

    @GetMapping("/events")
    public Flux<ServerSentEvent<String>> serverSentEvents() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> ServerSentEvent.builder("Event " + i)
                .id(String.valueOf(i))
                .event("message")
                .build());
    }

    @PostMapping("/upload")
    public Mono<ResponseEntity<String>> uploadFile(
            @RequestPart("file") FilePart filePart,
            @RequestPart("metadata") Mono<UploadMetadata> metadataMono) {

        return metadataMono
            .flatMap(metadata -> {
                // Process file and metadata
                return filePart.transferTo(Path.of("/uploads/" + filePart.filename()))
                    .then(Mono.just("File uploaded successfully"));
            })
            .map(result -> ResponseEntity.ok(result))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                .body("Upload failed: " + e.getMessage())));
    }

    @GetMapping("/download/{filename}")
    public Mono<Void> downloadFile(@PathVariable String filename, ServerHttpResponse response) {
        Path filePath = Path.of("/files/" + filename);

        if (!Files.exists(filePath)) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.writeWith(Mono.empty());
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.getHeaders().setContentDisposition(
            ContentDisposition.attachment().filename(filename).build());

        DataBufferFactory bufferFactory = response.bufferFactory();
        Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(filePath, bufferFactory, 4096);

        return response.writeWith(dataBufferFlux);
    }
}
```

#### Validation и error handling
```java
@RestController
@RequestMapping("/api/validated")
@Validated
public class ValidatedController {

    @PostMapping("/users")
    public Mono<ResponseEntity<User>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request)
            .map(user -> ResponseEntity.created(
                    URI.create("/api/users/" + user.getId()))
                .body(user));
    }

    @PutMapping("/users/{id}")
    public Mono<ResponseEntity<User>> updateUser(
            @PathVariable @Min(1) Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        return userService.updateUser(id, request)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/search")
    public Flux<User> searchUsers(
            @RequestParam @Size(min = 2, max = 50) String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        return userService.searchUsers(query, PageRequest.of(page, size));
    }
}
```

#### Custom response types
```java
@RestController
@RequestMapping("/api/responses")
public class CustomResponseController {

    @GetMapping("/wrapped")
    public Mono<ApiResponse<User>> getUserWithWrapper(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(user -> ApiResponse.success(user))
            .defaultIfEmpty(ApiResponse.error("User not found"));
    }

    @GetMapping("/paged")
    public Mono<PageResponse<User>> getUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return userService.getUsersPaged(PageRequest.of(page, size))
            .map(pageResult -> new PageResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
            ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFound(UserNotFoundException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage())));
    }
}

// Response wrapper classes
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String error;

    // Static factory methods
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    public static <T> ApiResponse<T> error(String error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = error;
        return response;
    }
}

public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    // Constructor and getters
}
```

## WebClient

### Basic WebClient

#### Configuration
```java
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
            .baseUrl("http://api.example.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-API-Key", "my-api-key")
            .clientConnector(createReactorClientHttpConnector())
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(2 * 1024 * 1024)) // 2MB
            .filter(logRequest())
            .filter(logResponse())
            .build();
    }

    private ReactorClientHttpConnector createReactorClientHttpConnector() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(20))
                .addHandlerLast(new WriteTimeoutHandler(20)));

        return new ReactorClientHttpConnector(httpClient);
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.println("Response: " + clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
```

**WebClient configuration parameters:**
- **Base URL** — базовый URL для всех запросов
- **Default headers** — заголовки, добавляемые ко всем запросам
- **Client connector** — HTTP клиент (Netty по умолчанию)
- **Codecs** — сериализация/десериализация
- **Filters** — перехватчики запросов/ответов

### HTTP methods

#### GET requests
```java
@Service
public class UserWebClientService {

    private final WebClient webClient;

    public UserWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<User> getUserById(Long userId) {
        return webClient.get()
            .uri("/users/{id}", userId)
            .retrieve()
            .bodyToMono(User.class);
    }

    public Flux<User> getAllUsers() {
        return webClient.get()
            .uri("/users")
            .retrieve()
            .bodyToFlux(User.class);
    }

    public Mono<User> getUserWithQueryParams(String name, Integer age) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/users/search")
                .queryParam("name", name)
                .queryParam("age", age)
                .build())
            .retrieve()
            .bodyToMono(User.class);
    }

    public Mono<ResponseEntity<User>> getUserWithFullResponse(Long userId) {
        return webClient.get()
            .uri("/users/{id}", userId)
            .exchangeToMono(clientResponse -> {
                if (clientResponse.statusCode().is2xxSuccessful()) {
                    return clientResponse.bodyToMono(User.class)
                        .map(user -> ResponseEntity.ok(user));
                } else if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    return Mono.just(ResponseEntity.notFound().build());
                } else {
                    return clientResponse.createException()
                        .flatMap(Mono::error);
                }
            });
    }
}
```

#### POST/PUT requests
```java
@Service
public class UserMutationService {

    private final WebClient webClient;

    public UserMutationService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<User> createUser(CreateUserRequest request) {
        return webClient.post()
            .uri("/users")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(User.class);
    }

    public Mono<User> updateUser(Long userId, UpdateUserRequest request) {
        return webClient.put()
            .uri("/users/{id}", userId)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(User.class);
    }

    public Mono<Void> deleteUser(Long userId) {
        return webClient.delete()
            .uri("/users/{id}", userId)
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<User> createUserWithStreaming(CreateUserRequest request) {
        return webClient.post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(User.class);
    }

    public Flux<User> createUsersBatch(Flux<CreateUserRequest> requests) {
        return webClient.post()
            .uri("/users/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requests, CreateUserRequest.class)
            .retrieve()
            .bodyToFlux(User.class);
    }
}
```

### Advanced WebClient features

#### Error handling
```java
@Service
public class ResilientWebClientService {

    private final WebClient webClient;

    public ResilientWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<User> getUserWithRetry(Long userId) {
        return webClient.get()
            .uri("/users/{id}", userId)
            .retrieve()
            .bodyToMono(User.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> throwable instanceof WebClientException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                    new ExternalServiceException("External service unavailable after retries")))
            .timeout(Duration.ofSeconds(30))
            .onErrorResume(TimeoutException.class, ex ->
                Mono.error(new ExternalServiceTimeoutException("Request timed out")))
            .onErrorResume(WebClientResponseException.class, ex -> {
                if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    return Mono.empty();
                } else {
                    return Mono.error(new ExternalServiceException(
                        "External service error: " + ex.getMessage()));
                }
            });
    }

    public Mono<User> getUserWithFallback(Long userId) {
        return webClient.get()
            .uri("/users/{id}", userId)
            .retrieve()
            .bodyToMono(User.class)
            .onErrorResume(throwable -> {
                // Fallback to cache or default value
                return getUserFromCache(userId)
                    .switchIfEmpty(Mono.just(createDefaultUser(userId)));
            });
    }

    public Mono<User> getUserWithCircuitBreaker(Long userId) {
        // Using Resilience4j CircuitBreaker
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("userService");

        return Mono.fromCallable(() ->
            circuitBreaker.decorateCallable(() ->
                webClient.get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block() // Block for circuit breaker (not ideal, but necessary)
            ).call()
        );
    }
}
```

#### File upload/download
```java
@Service
public class FileWebClientService {

    private final WebClient webClient;

    public FileWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Void> uploadFile(Path filePath, String uploadUrl) {
        String filename = filePath.getFileName().toString();

        return webClient.post()
            .uri(uploadUrl)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(
                "file", filePath.toFile()))
            .retrieve()
            .bodyToMono(Void.class);
    }

    public Mono<Path> downloadFile(String downloadUrl, Path destination) {
        return webClient.get()
            .uri(downloadUrl)
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .exchangeToMono(clientResponse -> {
                if (clientResponse.statusCode().is2xxSuccessful()) {
                    return clientResponse.bodyToMono(DataBuffer.class)
                        .map(dataBuffer -> {
                            try {
                                // Write to file
                                Files.write(destination,
                                    dataBuffer.asByteBuffer().array(),
                                    StandardOpenOption.CREATE,
                                    StandardOpenOption.APPEND);
                                return destination;
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to write file", e);
                            }
                        })
                        .then(Mono.just(destination));
                } else {
                    return Mono.error(new RuntimeException("Download failed"));
                }
            });
    }

    public Flux<DataBuffer> streamFile(String fileUrl) {
        return webClient.get()
            .uri(fileUrl)
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .retrieve()
            .bodyToFlux(DataBuffer.class);
    }
}
```

## Обработка ошибок

### Reactive exception handling

#### Global error handler
```java
@Configuration
public class ErrorHandlingConfig {

    @Bean
    public WebExceptionHandler webExceptionHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            ServerHttpResponse response = exchange.getResponse();

            if (ex instanceof ValidationException) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.writeWith(Mono.fromCallable(() -> {
                    DataBuffer buffer = response.bufferFactory().wrap(
                        "{\"error\":\"VALIDATION_ERROR\",\"message\":\"" + ex.getMessage() + "\"}"
                            .getBytes(StandardCharsets.UTF_8));
                    return buffer;
                }));
            }

            if (ex instanceof NotFoundException) {
                response.setStatusCode(HttpStatus.NOT_FOUND);
                return response.writeWith(Mono.empty());
            }

            // Default error response
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.writeWith(Mono.fromCallable(() -> {
                DataBuffer buffer = response.bufferFactory().wrap(
                    "{\"error\":\"INTERNAL_ERROR\",\"message\":\"An unexpected error occurred\"}"
                        .getBytes(StandardCharsets.UTF_8));
                return buffer;
            }));
        };
    }
}
```

#### Controller-level error handling
```java
@RestController
@RequestMapping("/api/error-handling")
public class ErrorHandlingController {

    private final ExternalServiceClient externalClient;

    public ErrorHandlingController(ExternalServiceClient externalClient) {
        this.externalClient = externalClient;
    }

    @GetMapping("/resilient/{id}")
    public Mono<ResponseEntity<User>> getUserResilient(@PathVariable Long id) {
        return externalClient.getUser(id)
            .map(user -> ResponseEntity.ok(user))
            .onErrorResume(ExternalServiceException.class, ex ->
                Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(null)))
            .onErrorResume(TimeoutException.class, ex ->
                Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(null)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/fallback/{id}")
    public Mono<User> getUserWithFallback(@PathVariable Long id) {
        return externalClient.getUser(id)
            .onErrorResume(throwable -> {
                // Fallback to local cache or default
                return getUserFromCache(id)
                    .switchIfEmpty(Mono.just(createDefaultUser(id)));
            });
    }

    @GetMapping("/retry/{id}")
    public Mono<User> getUserWithRetry(@PathVariable Long id) {
        return externalClient.getUser(id)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .filter(throwable -> isRetryableError(throwable)))
            .timeout(Duration.ofSeconds(10));
    }

    @GetMapping("/circuit-breaker/{id}")
    public Mono<User> getUserWithCircuitBreaker(@PathVariable Long id) {
        return externalClient.getUser(id)
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    private boolean isRetryableError(Throwable throwable) {
        return throwable instanceof WebClientException ||
               throwable instanceof TimeoutException;
    }

    private Mono<User> getUserFromCache(Long id) {
        // Implementation
        return Mono.empty();
    }

    private User createDefaultUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Default User");
        return user;
    }
}
```

## Backpressure

### Understanding backpressure

#### What is backpressure?
Backpressure — это механизм контроля скорости производства данных потребителем. В реактивных системах producer может генерировать данные быстрее, чем consumer может их обработать. Backpressure позволяет consumer'у сигнализировать producer'у о снижении скорости.

```java
// Without backpressure - consumer overwhelmed
Flux.interval(Duration.ofMillis(1))  // Produces 1000 items/second
    .map(i -> intensiveComputation(i))  // Slow processing
    .subscribe(System.out::println);  // Consumer can't keep up

// With backpressure - controlled flow
Flux.interval(Duration.ofMillis(1))
    .onBackpressureBuffer(100)  // Buffer up to 100 items
    .map(i -> intensiveComputation(i))
    .subscribe(System.out::println);  // Consumer controls the flow
```

#### Backpressure strategies

**Buffer** — буферизация элементов:
```java
Flux<String> buffered = Flux.create(sink -> {
    for (int i = 0; i < 1000; i++) {
        sink.next("item" + i);
    }
    sink.complete();
})
.onBackpressureBuffer(100)  // Buffer up to 100 items
.subscribe(item -> {
    // Slow processing
    Thread.sleep(10);
    System.out.println(item);
});
```

**Drop** — отбрасывание элементов:
```java
Flux<String> dropped = Flux.interval(Duration.ofMillis(1))
    .map(i -> "item" + i)
    .onBackpressureDrop(item -> {
        System.out.println("Dropped: " + item);  // Log dropped items
    })
    .subscribe(item -> {
        // Slow processing
        Thread.sleep(10);
        System.out.println("Processed: " + item);
    });
```

**Latest** — сохранение только последнего элемента:
```java
Flux<String> latest = Flux.interval(Duration.ofMillis(1))
    .map(i -> "item" + i)
    .onBackpressureLatest()  // Keep only latest item
    .subscribe(item -> {
        // Slow processing
        Thread.sleep(10);
        System.out.println("Processed: " + item);
    });
```

**Error** — ошибка при перегрузке:
```java
Flux<String> error = Flux.interval(Duration.ofMillis(1))
    .map(i -> "item" + i)
    .onBackpressureError()  // Error on overflow
    .subscribe(
        item -> System.out.println("Processed: " + item),
        error -> System.err.println("Backpressure error: " + error)
    );
```

### Implementing backpressure

#### Custom producer with backpressure
```java
public class BackpressureAwareProducer {

    public Flux<String> produceWithBackpressure() {
        return Flux.create(sink -> {
            sink.onRequest(requested -> {
                // Produce only requested amount
                for (long i = 0; i < requested; i++) {
                    String item = generateItem();
                    sink.next(item);
                }
            });

            sink.onCancel(() -> {
                // Cleanup when cancelled
                cleanup();
            });

            sink.onDispose(() -> {
                // Cleanup when disposed
                cleanup();
            });
        }, FluxSink.OverflowStrategy.ERROR);
    }

    private String generateItem() {
        // Generate data item
        return "data-" + System.nanoTime();
    }

    private void cleanup() {
        // Cleanup resources
        System.out.println("Cleaning up producer");
    }
}
```

#### Consumer-controlled backpressure
```java
public class BackpressureControlledConsumer {

    private final AtomicLong requested = new AtomicLong(0);
    private final AtomicLong processed = new AtomicLong(0);

    public void consumeWithBackpressure(Flux<String> dataStream) {
        dataStream
            .doOnRequest(request -> {
                requested.addAndGet(request);
                System.out.println("Requested: " + request + ", Total requested: " + requested.get());
            })
            .subscribe(new BaseSubscriber<String>() {

                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    // Request initial batch
                    request(10);
                }

                @Override
                protected void hookOnNext(String value) {
                    processed.incrementAndGet();

                    // Slow processing
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Processed: " + value + " (" + processed.get() + ")");

                    // Request more when ready
                    if (processed.get() % 5 == 0) {
                        request(5);
                    }
                }

                @Override
                protected void hookOnComplete() {
                    System.out.println("Processing completed");
                }

                @Override
                protected void hookOnError(Throwable throwable) {
                    System.err.println("Processing error: " + throwable.getMessage());
                }
            });
    }
}
```

## Тестирование

### Unit testing reactive code

#### Testing Mono/Flux
```java
@SpringBootTest
public class ReactiveServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testGetUserById() {
        // Given
        Long userId = 1L;
        User expectedUser = new User(userId, "test@example.com", "Test User");

        // Mock the repository
        given(userRepository.findById(userId)).willReturn(Mono.just(expectedUser));

        // When
        Mono<User> result = userService.getUserById(userId);

        // Then
        StepVerifier.create(result)
            .expectNext(expectedUser)
            .verifyComplete();
    }

    @Test
    void testGetUserByIdNotFound() {
        // Given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Mono.empty());

        // When
        Mono<User> result = userService.getUserById(userId);

        // Then
        StepVerifier.create(result)
            .verifyComplete(); // Empty Mono completes without elements
    }

    @Test
    void testGetAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(
            new User(1L, "user1@example.com", "User 1"),
            new User(2L, "user2@example.com", "User 2")
        );

        given(userRepository.findAll()).willReturn(Flux.fromIterable(expectedUsers));

        // When
        Flux<User> result = userService.getAllUsers();

        // Then
        StepVerifier.create(result)
            .expectNext(expectedUsers.get(0))
            .expectNext(expectedUsers.get(1))
            .verifyComplete();
    }

    @Test
    void testCreateUserValidationError() {
        // Given
        CreateUserRequest invalidRequest = new CreateUserRequest("", ""); // Invalid

        given(userRepository.save(any(User.class)))
            .willReturn(Mono.error(new ValidationException("Invalid user data")));

        // When
        Mono<User> result = userService.createUser(invalidRequest);

        // Then
        StepVerifier.create(result)
            .expectError(ValidationException.class)
            .verify();
    }
}
```

#### Testing with virtual time
```java
@SpringBootTest
public class TimeBasedTest {

    @Autowired
    private EventService eventService;

    @Test
    void testDelayedEventProcessing() {
        // Use virtual time to speed up tests
        StepVerifier.withVirtualTime(() -> eventService.processDelayedEvent())
            .thenAwait(Duration.ofMinutes(5))  // Fast-forward 5 minutes
            .expectNext("Event processed")
            .verifyComplete();
    }

    @Test
    void testIntervalStream() {
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1))
                .map(i -> "Event " + i)
                .take(3))
            .thenAwait(Duration.ofSeconds(3))  // Fast-forward 3 seconds
            .expectNext("Event 0")
            .expectNext("Event 1")
            .expectNext("Event 2")
            .verifyComplete();
    }
}
```

### Integration testing

#### Testing WebFlux endpoints
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebFluxIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll().block();
    }

    @Test
    void testGetAllUsers() {
        // Given
        User user1 = new User(null, "user1@example.com", "User 1");
        User user2 = new User(null, "user2@example.com", "User 2");

        userRepository.saveAll(Flux.just(user1, user2)).blockLast();

        // When & Then
        webTestClient.get()
            .uri("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(User.class)
            .hasSize(2);
    }

    @Test
    void testCreateUser() {
        // Given
        CreateUserRequest request = new CreateUserRequest("newuser@example.com", "New User");

        // When & Then
        webTestClient.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated()
            .expectHeader().exists("Location")
            .expectBody(User.class)
            .value(user -> {
                assertThat(user.getEmail()).isEqualTo("newuser@example.com");
                assertThat(user.getName()).isEqualTo("New User");
                assertThat(user.getId()).isNotNull();
            });
    }

    @Test
    void testGetUserNotFound() {
        webTestClient.get()
            .uri("/api/users/999")
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void testValidationError() {
        // Given - invalid request
        CreateUserRequest invalidRequest = new CreateUserRequest("", "");

        // When & Then
        webTestClient.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void testStreamingEndpoint() {
        webTestClient.get()
            .uri("/api/stream")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
            .returnResult(String.class)
            .getResponseBody()
            .take(3)  // Take first 3 elements
            .as(StepVerifier::create)
            .expectNextMatches(item -> item.startsWith("Data item "))
            .expectNextMatches(item -> item.startsWith("Data item "))
            .expectNextMatches(item -> item.startsWith("Data item "))
            .thenCancel()
            .verify();
    }
}
```

#### Testing WebClient
```java
@SpringBootTest
public class WebClientTest {

    @Autowired
    private ExternalApiClient externalApiClient;

    @MockBean
    private WebClient webClient;

    @Test
    void testGetUserSuccess() {
        // Given
        Long userId = 1L;
        User expectedUser = new User(userId, "test@example.com", "Test User");

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/users/{id}", userId)).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(User.class)).willReturn(Mono.just(expectedUser));

        // When
        Mono<User> result = externalApiClient.getUser(userId);

        // Then
        StepVerifier.create(result)
            .expectNext(expectedUser)
            .verifyComplete();
    }

    @Test
    void testGetUserError() {
        // Given
        Long userId = 1L;

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        given(webClient.get()).willReturn(requestHeadersUriSpec);
        given(requestHeadersUriSpec.uri("/users/{id}", userId)).willReturn(requestHeadersSpec);
        given(requestHeadersSpec.retrieve()).willReturn(responseSpec);
        given(responseSpec.bodyToMono(User.class))
            .willReturn(Mono.error(new WebClientResponseException(
                "Not Found", 404, "Not Found", null, null, null)));

        // When
        Mono<User> result = externalApiClient.getUser(userId);

        // Then
        StepVerifier.create(result)
            .expectError(WebClientResponseException.class)
            .verify();
    }
}
```

## Производительность и оптимизация

### Reactor optimization

#### Operator fusion
```java
// Non-fused operators (each creates intermediate object)
Flux<Integer> nonFused = Flux.range(1, 100)
    .map(i -> i * 2)        // Creates intermediate Flux
    .filter(i -> i > 50)    // Creates another intermediate Flux
    .map(i -> i / 2);       // Creates another intermediate Flux

// Fused operators (optimized by Reactor)
Flux<Integer> fused = Flux.range(1, 100)
    .map(i -> i * 2)
    .filter(i -> i > 50)
    .map(i -> i / 2)
    .share();  // Enable multicasting for better performance
```

#### Parallel processing
```java
Flux.range(1, 100)
    .parallel(4)  // Process on 4 parallel rails
    .runOn(Schedulers.parallel())
    .map(this::cpuIntensiveOperation)
    .sequential()  // Merge back to single stream
    .subscribe(result -> processResult(result));
```

#### Batching operations
```java
// Database batch operations
Flux<User> users = userRepository.findAll();

users.buffer(50)  // Group into batches of 50
    .flatMap(userBatch -> {
        // Process batch
        return userRepository.saveAll(userBatch)
            .then(Mono.just(userBatch.size()));
    }, 2)  // Concurrent batch processing limit
    .reduce(0, Integer::sum)
    .subscribe(totalProcessed ->
        System.out.println("Processed " + totalProcessed + " users"));
```

### Connection pooling

#### Reactor Netty configuration
```java
@Configuration
public class OptimizedWebClientConfig {

    @Bean
    public WebClient optimizedWebClient() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .option(EpollChannelOption.TCP_KEEPCNT, 8)
            .doOnConnected(connection -> connection
                .addHandlerLast(new ReadTimeoutHandler(10))
                .addHandlerLast(new WriteTimeoutHandler(10)))
            .metrics(true, () -> new MicrometerHttpClientMetricsRecorder(
                MeterRegistry meterRegistry, "http.client"));

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
            .clientConnector(connector)
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(2 * 1024 * 1024)) // 2MB
            .build();
    }
}
```

### Memory optimization

#### Object pooling
```java
@Configuration
public class MemoryOptimizationConfig {

    @Bean
    public Schedulers schedulerWithPool() {
        // Custom scheduler with object pooling
        return Schedulers.newBoundedElastic(10, 100, "bounded-elastic", 60);
    }

    @Bean
    public WebClient memoryEfficientWebClient() {
        return WebClient.builder()
            .codecs(configurer -> {
                // Limit memory usage for codecs
                configurer.defaultCodecs().maxInMemorySize(512 * 1024); // 512KB

                // Use streaming for large responses
                configurer.defaultCodecs().enableLoggingRequestDetails(false);
            })
            .build();
    }
}
```

#### Streaming responses
```java
@RestController
public class StreamingController {

    @GetMapping(value = "/large-dataset", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> streamLargeDataset() {
        return userRepository.findAll()
            .delayElements(Duration.ofMillis(10))  // Control streaming rate
            .doOnNext(user -> {
                // Optional: log progress
                if (user.getId() % 1000 == 0) {
                    System.out.println("Streamed " + user.getId() + " users");
                }
            });
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(sequence -> ServerSentEvent.builder("Event " + sequence)
                .id(String.valueOf(sequence))
                .event("message")
                .build())
            .doOnCancel(() -> System.out.println("Client disconnected"));
    }
}
```

## Production deployment

### Configuration management

#### Environment-specific configuration
```yaml
# application-prod.yaml
spring:
  profiles:
    active: prod

server:
  port: 8080
  netty:
    connection-timeout: 2000
    max-initial-line-length: 4096
    max-header-size: 8192
    max-chunk-size: 8192

spring:
  reactor:
    netty:
      pool:
        type: elastic
        max-connections: 1000
        max-idle-time: 20s
        max-life-time: 60s

  codec:
    max-in-memory-size: 10MB

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus,heapdump
  metrics:
    export:
      prometheus:
        enabled: true
```

#### Docker configuration
```dockerfile
FROM openjdk:17-jre-slim

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create app user
RUN useradd -r -s /bin/false webfluxuser

# Set working directory
WORKDIR /app

# Copy application
COPY target/webflux-app.jar app.jar

# Change ownership
RUN chown webfluxuser:webfluxuser app.jar

# Switch to non-root user
USER webfluxuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseCompressedOops"

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### Kubernetes deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: webflux-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: webflux-app
  template:
    metadata:
      labels:
        app: webflux-app
    spec:
      containers:
      - name: webflux-app
        image: myregistry/webflux-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: JAVA_OPTS
          value: "-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XshowSettings:vm"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        volumeMounts:
        - name: config-volume
          mountPath: /app/config
      volumes:
      - name: config-volume
        configMap:
          name: webflux-config
---
apiVersion: v1
kind: Service
metadata:
  name: webflux-service
spec:
  selector:
    app: webflux-app
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

### Monitoring и alerting

#### Micrometer metrics
```java
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new CompositeMeterRegistry();
    }

    @Bean
    public WebFluxTagsProvider webFluxTagsProvider() {
        return new DefaultWebFluxTagsProvider() {
            @Override
            public Iterable<Tag> httpRequestTags(ServerWebExchange exchange,
                                               Throwable exception) {
                return Tags.concat(
                    super.httpRequestTags(exchange, exception),
                    Tags.of("custom.tag", getCustomTag(exchange))
                );
            }
        };
    }

    @Bean
    public MetricsWebFilter metricsWebFilter(MeterRegistry meterRegistry) {
        return new MetricsWebFilter(meterRegistry, webFluxTagsProvider(),
            "http.server.requests", Duration.ofMillis(10));
    }
}
```

#### Custom metrics
```java
@Service
public class ReactiveMetricsService {

    private final MeterRegistry meterRegistry;

    public ReactiveMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Flux<User> getUsersWithMetrics() {
        return userRepository.findAll()
            .doOnNext(user -> {
                // Count users processed
                Counter.builder("users.processed")
                    .tag("status", "active")
                    .register(meterRegistry)
                    .increment();
            })
            .doOnError(error -> {
                // Count errors
                Counter.builder("users.errors")
                    .tag("type", error.getClass().getSimpleName())
                    .register(meterRegistry)
                    .increment();
            });
    }

    public Mono<User> createUserWithMetrics(CreateUserRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);

        return userService.createUser(request)
            .doOnSuccess(user -> {
                sample.stop(Timer.builder("user.creation.time")
                    .tag("result", "success")
                    .register(meterRegistry));
            })
            .doOnError(error -> {
                sample.stop(Timer.builder("user.creation.time")
                    .tag("result", "error")
                    .register(meterRegistry));
            });
    }
}
```

## Best practices

### Application design

#### 1. Choose appropriate return types
```java
@RestController
public class ReturnTypeController {

    // Use Mono for single result
    @GetMapping("/user/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    // Use Flux for multiple results
    @GetMapping("/users")
    public Flux<User> getUsers() {
        return userService.findAll();
    }

    // Use Mono<Void> for no content responses
    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteById(id);
    }

    // Use Mono<ResponseEntity> for complex responses
    @PostMapping("/user")
    public Mono<ResponseEntity<User>> createUser(@RequestBody CreateUserRequest request) {
        return userService.create(request)
            .map(user -> ResponseEntity.created(
                    URI.create("/user/" + user.getId()))
                .body(user));
    }
}
```

#### 2. Handle blocking operations properly
```java
@Service
public class BlockingOperationService {

    // Bad: Blocking operation in reactive pipeline
    public Mono<User> getUserBlocking(Long id) {
        return Mono.fromCallable(() -> {
            // This blocks the thread!
            return blockingDatabaseCall(id);
        });
    }

    // Good: Use subscribeOn for blocking operations
    public Mono<User> getUserNonBlocking(Long id) {
        return Mono.fromCallable(() -> blockingDatabaseCall(id))
            .subscribeOn(Schedulers.boundedElastic());  // Offload to elastic scheduler
    }

    // Better: Use reactive database client
    public Mono<User> getUserReactive(Long id) {
        return reactiveDatabaseClient.findById(id);  // Fully reactive
    }
}
```

#### 3. Error handling patterns
```java
@Service
public class ErrorHandlingService {

    public Mono<User> getUserWithFallback(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .onErrorResume(UserNotFoundException.class, ex -> {
                // Log and return default user
                log.warn("User {} not found, returning default", id);
                return Mono.just(createDefaultUser());
            })
            .onErrorResume(DatabaseException.class, ex -> {
                // Retry on database errors
                return userRepository.findById(id)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
            })
            .onErrorResume(Exception.class, ex -> {
                // Fallback to cache
                return userCache.getUser(id);
            });
    }

    public Flux<User> getUsersWithErrorContainment() {
        return userRepository.findAll()
            .onErrorContinue((error, item) -> {
                // Log error but continue processing
                log.error("Error processing user {}: {}", item, error.getMessage());
            })
            .doOnError(error -> {
                // Handle stream-level errors
                log.error("Stream error: {}", error.getMessage());
            });
    }
}
```

### Performance optimization

#### 4. Connection and thread management
```java
@Configuration
public class PerformanceConfig {

    @Bean
    public ReactorResourceFactory reactorResourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);  // Isolate resources per server
        factory.setConnectionProvider(connectionProvider());
        factory.setLoopResources(loopResources());
        return factory;
    }

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("webflux-connections")
            .maxConnections(500)                    // Max connections
            .pendingAcquireMaxCount(1000)          // Max pending acquires
            .pendingAcquireTimeout(Duration.ofSeconds(30))  // Timeout
            .maxIdleTime(Duration.ofMinutes(5))    // Max idle time
            .maxLifeTime(Duration.ofHours(1))      // Max life time
            .build();
    }

    @Bean
    public LoopResources loopResources() {
        // CPU cores * 2 event loops
        int eventLoopCount = Runtime.getRuntime().availableProcessors() * 2;
        return LoopResources.create("webflux-loops", 1, eventLoopCount, true);
    }

    @Bean
    public WebClient optimizedWebClient(ReactorResourceFactory resourceFactory) {
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create(resourceFactory.getConnectionProvider())
                    .runOn(resourceFactory.getLoopResources())))
            .build();
    }
}
```

#### 5. Caching strategies
```java
@Configuration
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "orders");
    }
}

@Service
public class CachedUserService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public CachedUserService(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    public Mono<User> getUserById(Long id) {
        Cache cache = cacheManager.getCache("users");
        Cache.ValueWrapper cached = cache.get(id);

        if (cached != null) {
            return Mono.just((User) cached.get());
        }

        return userRepository.findById(id)
            .doOnNext(user -> cache.put(id, user));
    }

    public Mono<User> createUser(CreateUserRequest request) {
        return userRepository.save(new User(request))
            .doOnNext(user -> {
                // Invalidate related caches
                cacheManager.getCache("users").evictIfPresent(user.getId());
                // Could also publish cache invalidation event
            });
    }
}
```

### Testing strategies

#### 6. Test data management
```java
@SpringBootTest
public class TestDataManagementTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataInitializer dataInitializer;

    @BeforeEach
    void setupTestData() {
        // Clean and initialize test data reactively
        userRepository.deleteAll()
            .then(dataInitializer.initializeUsers())
            .then(dataInitializer.initializeOrders())
            .block();  // Block for test setup
    }

    @Test
    void testBusinessLogic() {
        // Test with known test data
        Flux<User> users = userRepository.findAll();

        StepVerifier.create(users)
            .expectNextCount(5)  // Expect 5 test users
            .verifyComplete();
    }

    @Test
    void testUserCreation() {
        CreateUserRequest request = new CreateUserRequest("test@example.com", "Test User");

        Mono<User> result = userService.createUser(request);

        StepVerifier.create(result)
            .assertNext(user -> {
                assertNotNull(user.getId());
                assertEquals("test@example.com", user.getEmail());
                assertEquals("Test User", user.getName());
            })
            .verifyComplete();

        // Verify database state
        StepVerifier.create(userRepository.count())
            .expectNext(6L)  // 5 initial + 1 created
            .verifyComplete();
    }
}
```

#### 7. Integration test patterns
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComprehensiveIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCompleteUserWorkflow() {
        // 1. Create user
        CreateUserRequest createRequest = new CreateUserRequest("workflow@example.com", "Workflow User");

        webTestClient.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(User.class)
            .value(user -> assertNotNull(user.getId()));

        // 2. Retrieve user
        webTestClient.get()
            .uri("/api/users")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(User.class)
            .value(users -> assertTrue(users.size() >= 1));

        // 3. Update user
        User createdUser = userRepository.findByEmail("workflow@example.com").block();
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setName("Updated Workflow User");

        webTestClient.put()
            .uri("/api/users/{id}", createdUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(User.class)
            .value(user -> assertEquals("Updated Workflow User", user.getName()));

        // 4. Delete user
        webTestClient.delete()
            .uri("/api/users/{id}", createdUser.getId())
            .exchange()
            .expectStatus().isNoContent();

        // 5. Verify deletion
        webTestClient.get()
            .uri("/api/users/{id}", createdUser.getId())
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void testConcurrentRequests() {
        // Test concurrent request handling
        List<Mono<ClientResponse>> requests = IntStream.range(0, 10)
            .mapToObj(i -> webTestClient.get()
                .uri("/api/users")
                .exchange()
                .returnResult(Void.class))
            .collect(Collectors.toList());

        // Execute all requests concurrently
        Flux<ClientResponse> responses = Flux.fromIterable(requests)
            .flatMap(mono -> mono, 5);  // Concurrent execution with limit

        StepVerifier.create(responses)
            .expectNextCount(10)
            .verifyComplete();
    }
}
```

### Operational practices

#### 8. Health checks and monitoring
```java
@Component
public class ReactiveHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient webClient;
    private final UserRepository userRepository;

    public ReactiveHealthIndicator(WebClient.Builder webClientBuilder,
                                  UserRepository userRepository) {
        this.webClient = webClientBuilder.baseUrl("http://external-service").build();
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Health> health() {
        return Mono.zip(
                checkDatabaseConnectivity(),
                checkExternalServiceConnectivity(),
                checkMemoryUsage()
            )
            .map(tuple -> {
                Health.Builder builder = Health.up();

                if (!tuple.getT1()) {
                    builder.down().withDetail("database", "unreachable");
                }

                if (!tuple.getT2()) {
                    builder.withDetail("externalService", "unreachable");
                }

                builder.withDetail("memoryUsage", tuple.getT3() + "%");

                return builder.build();
            })
            .onErrorResume(error -> Mono.just(Health.down(error).build()));
    }

    private Mono<Boolean> checkDatabaseConnectivity() {
        return userRepository.count()
            .map(count -> true)
            .onErrorReturn(false)
            .timeout(Duration.ofSeconds(5))
            .onErrorReturn(false);
    }

    private Mono<Boolean> checkExternalServiceConnectivity() {
        return webClient.get()
            .uri("/health")
            .retrieve()
            .bodyToMono(String.class)
            .map(response -> true)
            .onErrorReturn(false)
            .timeout(Duration.ofSeconds(5))
            .onErrorReturn(false);
    }

    private Mono<Double> checkMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return Mono.just(((double) usedMemory / totalMemory) * 100);
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Thread blocking issues
```
java.lang.IllegalStateException: block()/blockFirst()/blockLast() are blocking
```

**Решение:**
```java
// Bad: Blocking in reactive pipeline
@GetMapping("/users")
public List<User> getUsers() {
    return userService.getAllUsers().collectList().block();  // BLOCKS!
}

// Good: Return reactive types
@GetMapping("/users")
public Flux<User> getUsers() {
    return userService.getAllUsers();  // NON-BLOCKING
}

// For testing: Use StepVerifier
@Test
void testBlockingForTest() {
    List<User> users = userService.getAllUsers().collectList().block();
    assertTrue(users.size() > 0);
}
```

#### Memory leaks
```
OutOfMemoryError: Java heap space
```

**Решение:**
```java
// Problem: Infinite streams without limits
Flux<Long> infinite = Flux.interval(Duration.ofMillis(1));
// This will eventually cause OOM

// Solution: Add limits
Flux<Long> limited = Flux.interval(Duration.ofMillis(1))
    .take(1000)  // Limit the stream
    .onBackpressureBuffer(100);  // Add backpressure

// Problem: Buffering large datasets
Flux<User> allUsers = userRepository.findAll();
// If there are millions of users, this buffers everything

// Solution: Stream processing
Flux<User> streamingUsers = userRepository.findAll()
    .buffer(100)  // Process in batches
    .flatMap(batch -> processBatch(batch), 2);  // Concurrent batch processing
```

#### Slow performance
```
Requests taking 5+ seconds to complete
```

**Решение:**
```java
// Check thread pool configuration
@Configuration
public class ThreadPoolConfig {

    @Bean
    public Scheduler scheduler() {
        return Schedulers.newBoundedElastic(10, 100, "custom-scheduler");
    }
}

// Optimize database queries
public Flux<User> getUsersOptimized() {
    return userRepository.findAllActiveUsers()  // More specific query
        .publishOn(Schedulers.boundedElastic())  // Offload I/O
        .doOnNext(this::enrichUserData);         // Parallel enrichment
}

// Add caching
@Cacheable("users")
public Mono<User> getUserByIdCached(Long id) {
    return userRepository.findById(id);
}
```

### Debug techniques

#### Reactor debugging
```java
@Configuration
public class DebugConfig {

    @Bean
    public WebClient debugWebClient(WebClient.Builder builder) {
        return builder
            .filter((request, next) -> {
                System.out.println("Request: " + request.method() + " " + request.url());
                long startTime = System.nanoTime();
                return next.exchange(request)
                    .doOnNext(response -> {
                        long duration = (System.nanoTime() - startTime) / 1_000_000;
                        System.out.println("Response: " + response.statusCode() + " (" + duration + "ms)");
                    });
            })
            .build();
    }
}

// Enable Reactor debug mode
@Configuration
public class ReactorDebugConfig {

    @PostConstruct
    public void enableReactorDebug() {
        Hooks.onOperatorDebug();  // Enable operator stack traces
    }
}

// Custom operator for debugging
public class DebugOperator {

    public static <T> Flux<T> debugFlux(Flux<T> flux, String name) {
        return flux
            .doOnSubscribe(s -> System.out.println(name + ": subscribed"))
            .doOnNext(item -> System.out.println(name + ": " + item))
            .doOnComplete(() -> System.out.println(name + ": completed"))
            .doOnError(error -> System.err.println(name + ": error - " + error));
    }

    public static <T> Mono<T> debugMono(Mono<T> mono, String name) {
        return mono
            .doOnSubscribe(s -> System.out.println(name + ": subscribed"))
            .doOnNext(item -> System.out.println(name + ": " + item))
            .doOnSuccess(item -> System.out.println(name + ": success - " + item))
            .doOnError(error -> System.err.println(name + ": error - " + error));
    }
}
```

#### Performance monitoring
```java
@Service
public class PerformanceMonitor {

    private final MeterRegistry meterRegistry;

    public PerformanceMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public <T> Mono<T> monitorMono(String operationName, Mono<T> mono) {
        Timer.Sample sample = Timer.start(meterRegistry);

        return mono
            .doOnSuccess(result -> {
                sample.stop(Timer.builder("operation.duration")
                    .tag("operation", operationName)
                    .tag("result", "success")
                    .register(meterRegistry));
            })
            .doOnError(error -> {
                sample.stop(Timer.builder("operation.duration")
                    .tag("operation", operationName)
                    .tag("result", "error")
                    .register(meterRegistry));

                Counter.builder("operation.errors")
                    .tag("operation", operationName)
                    .tag("error_type", error.getClass().getSimpleName())
                    .register(meterRegistry)
                    .increment();
            });
    }

    public <T> Flux<T> monitorFlux(String operationName, Flux<T> flux) {
        return flux
            .doOnComplete(() -> {
                Counter.builder("operation.completed")
                    .tag("operation", operationName)
                    .register(meterRegistry)
                    .increment();
            })
            .doOnError(error -> {
                Counter.builder("operation.errors")
                    .tag("operation", operationName)
                    .tag("error_type", error.getClass().getSimpleName())
                    .register(meterRegistry)
                    .increment();
            });
    }
}
```

## Заключение

**Spring WebFlux** — это мощный фреймворк для создания реактивных веб-приложений, который полностью меняет подход к разработке высокопроизводительных систем. Использование Project Reactor и reactive programming позволяет создавать приложения, способные эффективно обрабатывать тысячи одновременных подключений с минимальным использованием ресурсов.

### Ключевые возможности:

1. **Reactive Programming** — асинхронная обработка данных с backpressure
2. **Non-blocking I/O** — эффективное использование потоков
3. **Functional Endpoints** — декларативная маршрутизация
4. **Annotation Controllers** — знакомый Spring MVC стиль
5. **WebClient** — реактивный HTTP клиент
6. **Backpressure Support** — контроль потока данных
7. **Server-Sent Events** — real-time коммуникация
8. **Streaming** — обработка больших объемов данных

### Архитектурные преимущества:

#### Performance:
- **High Concurrency** — тысячи одновременных подключений
- **Resource Efficiency** — минимальное потребление памяти/CPU
- **Elastic Scaling** — автоматическое масштабирование
- **Low Latency** — быстрая обработка запросов

#### Scalability:
- **Non-blocking I/O** — эффективное использование потоков
- **Backpressure** — предотвращение перегрузки
- **Reactive Streams** — стандартизированная обработка данных
- **Event-driven** — асинхронная обработка событий

### Когда использовать WebFlux:

✅ **High-throughput applications** — приложения с высокой нагрузкой
✅ **Microservices** — асинхронная коммуникация между сервисами
✅ **Real-time features** — WebSocket, Server-Sent Events
✅ **Streaming data** — обработка потоков данных
✅ **I/O bound applications** — интенсивные I/O операции
✅ **Reactive databases** — MongoDB, Cassandra, Redis reactive clients
✅ **Cloud-native** — масштабируемые облачные приложения

### Когда НЕ использовать:

❌ **Simple CRUD** — используйте Spring MVC для простых задач
❌ **Blocking operations** — весь код должен быть реактивным
❌ **Small applications** — overhead не оправдан
❌ **Legacy blocking APIs** — сложная интеграция
❌ **Simple REST APIs** — Spring MVC проще для базовых задач
❌ **File uploads** — Spring MVC лучше для больших файлов
❌ **Synchronous clients** — требуют адаптации

### Production considerations:

1. **Thread pool tuning** — правильная конфигурация schedulers
2. **Backpressure configuration** — управление потоками данных
3. **Error handling** — comprehensive обработка ошибок
4. **Monitoring** — метрики и алертинг
5. **Performance optimization** — connection pooling, caching
6. **Testing** — unit и integration тесты реактивного кода
7. **Deployment** — container и orchestration конфигурации

### Best practices summary:

1. **Choose reactive databases** — используйте reactive clients
2. **Handle blocking operations** — offload с subscribeOn
3. **Implement backpressure** — контролируйте потоки данных
4. **Error handling** — comprehensive стратегии обработки ошибок
5. **Testing** — StepVerifier для тестирования reactive streams
6. **Performance monitoring** — метрики и алертинг
7. **Configuration tuning** — оптимизация thread pools и connections
8. **Backpressure strategies** — buffer, drop, latest, error

Spring WebFlux представляет собой будущее веб-разработки в экосистеме Spring. Он позволяет создавать высокопроизводительные, масштабируемые приложения, которые могут эффективно работать под высокой нагрузкой. Правильное использование реактивного программирования открывает новые возможности для создания современных distributed систем. 🚀

**Далее: Spring Integration (enterprise integration patterns)**
