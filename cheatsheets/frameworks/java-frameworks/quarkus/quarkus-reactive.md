---
title: "Quarkus: Reactive — Mutiny и Reactive Messaging"
description: "Полное руководство по reactive программированию в Quarkus: Mutiny, reactive messaging, reactive streams и best practices"
tags:
  - quarkus
  - reactive
  - mutiny
  - reactive-messaging
  - streams
  - java
type: "overview"
difficulty: "intermediate"
aliases:
  - "Quarkus"
  - "quarkus reactive"
prerequisites:
  - "[[quarkus-basics]]"
  - "[[quarkus-rest]]"
related:
  - "[[quarkus-rest]]"
  - "[[quarkus-testing]]"
next:
  - "[[quarkus-rest]]"
  - "[[quarkus-testing]]"
updated: "2026-04-20"
---

# Quarkus: Reactive — Mutiny и Reactive Messaging

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Mutiny Basics](#mutiny-basics)
  - [Uni — Single Item](#uni-single-item)
  - [Multi — Multiple Items](#multi-multiple-items)
- [Reactive Operations](#reactive-operations)
  - [Transformations](#transformations)
  - [Combining Streams](#combining-streams)
- [Reactive Messaging](#reactive-messaging)
  - [Message Producer](#message-producer)
  - [Message Consumer](#message-consumer)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте Uni для single items](#1-используйте-uni-для-single-items)
  - [2. Используйте Multi для streams](#2-используйте-multi-для-streams)
  - [3. Обрабатывайте ошибки](#3-обрабатывайте-ошибки)
- [Advanced Mutiny Operations](#advanced-mutiny-operations)
  - [FlatMap для асинхронных операций](#flatmap-для-асинхронных-операций)
  - [Retry и Timeout](#retry-и-timeout)
  - [Subscription и Cancellation](#subscription-и-cancellation)
  - [Backpressure Handling](#backpressure-handling)
- [Reactive Messaging Patterns](#reactive-messaging-patterns)
  - [Request-Reply Pattern](#request-reply-pattern)
  - [Message Transformation](#message-transformation)
  - [Error Handling в Reactive Messaging](#error-handling-в-reactive-messaging)
- [Reactive Database Access](#reactive-database-access)
  - [Reactive Hibernate](#reactive-hibernate)
  - [Reactive Panache](#reactive-panache)
- [Reactive REST Clients](#reactive-rest-clients)
  - [Async REST Client](#async-rest-client)
  - [Использование Reactive Client](#использование-reactive-client)
- [Reactive WebSockets](#reactive-websockets)
  - [WebSocket Server](#websocket-server)
- [Performance Optimization](#performance-optimization)
  - [Оптимизация потоков](#оптимизация-потоков)
  - [Connection Pooling](#connection-pooling)
- [Testing Reactive Code](#testing-reactive-code)
  - [Тестирование Uni](#тестирование-uni)
  - [Тестирование Multi](#тестирование-multi)
- [Reactive Context Propagation](#reactive-context-propagation)
  - [Context Propagation](#context-propagation)
- [Reactive Error Recovery](#reactive-error-recovery)
  - [Circuit Breaker Pattern](#circuit-breaker-pattern)
- [Reactive Backpressure](#reactive-backpressure)
  - [Backpressure Handling](#backpressure-handling-1)
- [Advanced Reactive Patterns](#advanced-reactive-patterns)
  - [Parallel Processing](#parallel-processing)
  - [Reactive Caching](#reactive-caching)
- [Reactive Performance Optimization](#reactive-performance-optimization)
  - [Backpressure Strategies](#backpressure-strategies)
  - [Thread Pool Optimization](#thread-pool-optimization)
- [Reactive Error Handling Patterns](#reactive-error-handling-patterns)
  - [Error Recovery Strategies](#error-recovery-strategies)
  - [Error Classification](#error-classification)
- [Reactive Testing Patterns](#reactive-testing-patterns)
  - [Testing Uni](#testing-uni)
  - [Testing Multi](#testing-multi)
- [Reactive Performance Monitoring](#reactive-performance-monitoring)
  - [Throughput Monitoring](#throughput-monitoring)
  - [Latency Tracking](#latency-tracking)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus** построен на реактивных принципах и использует **Mutiny** как основную библиотеку для **reactive** программирования. Это позволяет создавать неблокирующие, высокопроизводительные приложения.

### Основные возможности

- **Mutiny**: Реактивная библиотека
- **Reactive Messaging**: Асинхронная обработка сообщений
- **Reactive Streams**: Поддержка **Reactive Streams**
- **Non-blocking I/O**: Неблокирующий ввод/вывод

## Mutiny Basics

### Uni — Single Item

```java
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/users")
public class UserResource {

    @GET
    @Path("/{id}")
    public Uni<User> getUser(Long id) {
        return Uni.createFrom().item(() -> userService.findById(id));
    }
}
```

### Multi — Multiple Items

```java
import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/users")
public class UserResource {

    @GET
    public Multi<User> getAllUsers() {
        return Multi.createFrom().items(userService.findAll().stream());
    }
}
```

## Reactive Operations

### Transformations

```java
import io.smallrye.mutiny.Uni;

public Uni<String> processUser(Long id) {
    return Uni.createFrom().item(() -> userService.findById(id))
        .map(user -> user.getName().toUpperCase())
        .onFailure().recoverWithItem("Unknown");
}
```

### Combining Streams

```java
import io.smallrye.mutiny.Uni;

public Uni<CombinedResult> combineData(Long userId, Long orderId) {
    Uni<User> user = getUser(userId);
    Uni<Order> order = getOrder(orderId);

    return Uni.combine().all().unis(user, order)
        .combinedWith((u, o) -> new CombinedResult(u, o));
}
```

## Reactive Messaging

### Message Producer

```java
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageProducer {

    @Outgoing("users")
    public Multi<User> produceUsers() {
        return Multi.createFrom().items(userService.findAll().stream());
    }
}
```

### Message Consumer

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageConsumer {

    @Incoming("users")
    public void consumeUser(User user) {
        System.out.println("Received user: " + user.getName());
    }
}
```

## Лучшие практики

### 1. Используйте Uni для single items

```java
// ✅ Хорошо
public Uni<User> getUser(Long id) {
    return Uni.createFrom().item(() -> userService.findById(id));
}
```

### 2. Используйте Multi для streams

```java
// ✅ Хорошо
public Multi<User> getUsers() {
    return Multi.createFrom().items(users.stream());
}
```

### 3. Обрабатывайте ошибки

```java
// ✅ Хорошо
return uni.onFailure().recoverWithItem(defaultValue);
```

## Advanced Mutiny Operations

### FlatMap для асинхронных операций

**FlatMap** позволяет выполнять асинхронные операции и возвращать новый **Uni** или **Multi**:**

```java
import io.smallrye.mutiny.Uni;

public Uni<UserProfile> getUserProfile(Long userId) {
    return getUser(userId)
        .flatMap(user -> {
            Uni<List<Order>> orders = getOrders(userId);
            Uni<List<Address>> addresses = getAddresses(userId);

            return Uni.combine().all().unis(orders, addresses)
                .combinedWith((o, a) -> new UserProfile(user, o, a));
        });
}
```

### Retry и Timeout

**Mutiny** предоставляет встроенные механизмы для **retry** и **timeout**:**

```java
import io.smallrye.mutiny.Uni;
import java.time.Duration;

public Uni<String> fetchDataWithRetry() {
    return Uni.createFrom().item(() -> fetchFromExternalService())
        .onFailure().retry().withBackOff(Duration.ofSeconds(1))
            .atMost(3)
        .ifNoItem().after(Duration.ofSeconds(5))
            .fail()
        .onFailure().recoverWithItem("Default value");
}
```

### Subscription и Cancellation

**Управление подписками и отмена операций:**

```java
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.Cancellable;

public class DataStream {

    private Cancellable subscription;

    public void startStream() {
        subscription = Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onItem().transform(tick -> fetchData())
            .subscribe().with(
                data -> processData(data),
                failure -> handleError(failure),
                () -> onComplete()
            );
    }

    public void stopStream() {
        if (subscription != null) {
            subscription.cancel();
        }
    }
}
```

### Backpressure Handling

**Обработка **backpressure** в **Multi**:**

```java
import io.smallrye.mutiny.Multi;

public Multi<Data> processWithBackpressure() {
    return Multi.createFrom().items(dataStream)
        .onOverflow().buffer(100)  // Буферизация при переполнении
        .onItem().transform(this::processData)
        .onFailure().retry().withBackOff(Duration.ofSeconds(1));
}
```

## Reactive Messaging Patterns

### Request-Reply Pattern

**Реализация **request-reply** паттерна с **reactive messaging**:**

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RequestReplyProcessor {

    @Incoming("requests")
    @Outgoing("replies")
    public Uni<Reply> processRequest(Request request) {
        return processAsync(request)
            .map(result -> new Reply(request.getId(), result));
    }
}
```

### Message Transformation

**Трансформация сообщений в **reactive pipeline**:**

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageTransformer {

    @Incoming("raw-events")
    @Outgoing("processed-events")
    public Multi<ProcessedEvent> transform(Multi<RawEvent> events) {
        return events
            .onItem().transform(this::enrichEvent)
            .onItem().transform(this::validateEvent)
            .onFailure().retry().withBackOff(Duration.ofSeconds(1));
    }

    private ProcessedEvent enrichEvent(RawEvent event) {
        // Обогащение события дополнительными данными
        return new ProcessedEvent(event, getMetadata(event));
    }

    private ProcessedEvent validateEvent(ProcessedEvent event) {
        if (!event.isValid()) {
            throw new ValidationException("Invalid event");
        }
        return event;
    }
}
```

### Error Handling в Reactive Messaging

**Обработка ошибок в **reactive messaging**:**

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ErrorHandlingConsumer {

    @Incoming("events")
    public Uni<Void> consumeWithErrorHandling(Event event) {
        return processEvent(event)
            .onFailure().invoke(failure -> {
                logError(failure);
                sendToDeadLetterQueue(event, failure);
            })
            .onFailure().recoverWithItem(() -> {
                // Продолжить обработку после ошибки
                return null;
            });
    }
}
```

## Reactive Database Access

### Reactive Hibernate

**Использование **Hibernate Reactive** для неблокирующего доступа к БД:**

```java
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.hibernate.reactive.mutiny.Mutiny;

@Path("/users")
public class ReactiveUserResource {

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @GET
    @Path("/{id}")
    public Uni<User> getUser(Long id) {
        return sessionFactory.withSession(session ->
            session.find(User.class, id)
        );
    }

    @GET
    public Multi<User> getAllUsers() {
        return sessionFactory.withSession(session ->
            session.createQuery("SELECT u FROM User u", User.class)
                .getResults()
        );
    }
}
```

### Reactive Panache

**Использование **Panache** для упрощенного **reactive** доступа:**

```java
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;

@Entity
public class User extends PanacheEntity {
    public String name;
    public String email;

    public static Uni<User> findByName(String name) {
        return find("name", name).firstResult();
    }

    public static Multi<User> findAllActive() {
        return find("active", true).stream();
    }
}
```

## Reactive REST Clients

### Async REST Client

**Создание асинхронных **REST** клиентов:**

```java
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import io.smallrye.mutiny.Uni;

@RegisterRestClient
@Path("/api")
public interface UserServiceClient {

    @GET
    @Path("/users/{id}")
    Uni<User> getUser(@PathParam("id") Long id);

    @GET
    @Path("/users")
    Multi<User> getAllUsers();
}
```

### Использование Reactive Client

```java
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/proxy")
public class ProxyResource {

    @Inject
    @RestClient
    UserServiceClient userService;

    @GET
    @Path("/users/{id}")
    public Uni<User> proxyGetUser(Long id) {
        return userService.getUser(id)
            .onFailure().retry().atMost(3)
            .onFailure().recoverWithItem(() -> createDefaultUser());
    }
}
```

## Reactive WebSockets

### WebSocket Server

**Создание **reactive WebSocket** сервера:**

```java
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import io.smallrye.mutiny.Multi;

@ServerEndpoint("/chat")
public class ChatEndpoint {

    @OnOpen
    public void onOpen(Session session) {
        Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onItem().transform(tick -> getLatestMessages())
            .subscribe().with(
                messages -> sendToClient(session, messages),
                failure -> handleError(session, failure)
            );
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        processMessage(message)
            .subscribe().with(
                result -> broadcast(result),
                failure -> handleError(session, failure)
            );
    }
}
```

## Performance Optimization

### Оптимизация потоков

**Оптимизация обработки потоков данных:**

```java
import io.smallrye.mutiny.Multi;

public Multi<ProcessedData> optimizeStream(Multi<RawData> input) {
    return input
        .onItem().transformToUni(this::processAsync)
            .merge()  // Параллельная обработка
        .select().where(this::isValid)
        .group().intoLists().of(100)  // Группировка для батч-обработки
        .onItem().transform(this::processBatch);
}
```

### Connection Pooling

**Настройка **connection pooling** для **reactive** клиентов:**

```properties
# application.properties
quarkus.datasource.reactive.max-size=20
quarkus.datasource.reactive.idle-timeout=30m
quarkus.datasource.reactive.max-lifetime=60m
```

## Testing Reactive Code

### Тестирование Uni

```java
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ReactiveServiceTest {

    @Inject
    ReactiveService service;

    @Test
    void testUni() {
        Uni<String> result = service.processData("input");

        result
            .subscribe().withSubscriber(UniAssertSubscriber.create())
            .assertCompleted()
            .assertItem("expected-output");
    }
}
```

### Тестирование Multi

```java
import io.smallrye.mutiny.helpers.test.AssertSubscriber;

@Test
void testMulti() {
    Multi<Integer> numbers = Multi.createFrom().items(1, 2, 3, 4, 5);

    AssertSubscriber<Integer> subscriber = numbers
        .subscribe().withSubscriber(AssertSubscriber.create(5));

    subscriber
        .assertCompleted()
        .assertItems(1, 2, 3, 4, 5);
}
```

## Reactive Context Propagation

### Context Propagation

**Передача контекста в **reactive** потоках:**

```java
import io.smallrye.context.SmallRyeContextManager;
import io.smallrye.context.api.ManagedExecutorConfig;
import jakarta.inject.Inject;

@ApplicationScoped
public class ContextPropagationService {

    @Inject
    SmallRyeContextManager contextManager;

    public Uni<String> processWithContext(String input) {
        String contextValue = getCurrentContext();

        return Uni.createFrom().item(() -> process(input))
            .runSubscriptionOn(contextManager.newManagedExecutor()
                .withThreadContext()
                .build())
            .map(result -> result + " (context: " + contextValue + ")");
    }
}
```

## Reactive Error Recovery

### Circuit Breaker Pattern

**Реализация **circuit breaker**:**

```java
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import io.smallrye.mutiny.Uni;

@ApplicationScoped
public class CircuitBreakerService {

    @CircuitBreaker(
        requestVolumeThreshold = 10,
        failureRatio = 0.5,
        delay = 5000
    )
    public Uni<String> callExternalService(String input) {
        return externalService.call(input)
            .onFailure().retry().withBackOff(Duration.ofSeconds(1))
            .atMost(3);
    }
}
```

## Reactive Backpressure

### Backpressure Handling

**Обработка **backpressure**:**

```java
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.subscription.BackPressureStrategy;

@ApplicationScoped
public class BackpressureService {

    public Multi<Data> processWithBackpressure(Multi<Data> input) {
        return input
            .onOverflow().buffer(100)  // Буферизация
            .onOverflow().drop()       // Или отбрасывание
            .onItem().transform(this::process);
    }
}
```

## Advanced Reactive Patterns

### Parallel Processing

**Параллельная обработка:**

```java
@ApplicationScoped
public class ParallelProcessingService {

    public Multi<Result> processParallel(Multi<Item> items) {
        return items
            .onItem().transformToUni(item ->
                Uni.createFrom().item(() -> processItem(item))
                    .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
            )
            .merge()
            .withConcurrency(10);
    }
}
```

### Reactive Caching

**Реактивное кеширование:**

```java
@ApplicationScoped
public class ReactiveCacheService {

    private final Cache<String, Uni<User>> cache =
        Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public Uni<User> getUser(Long id) {
        String key = "user:" + id;
        return cache.get(key, k ->
            userRepository.findByIdAsync(id)
        );
    }
}
```

## Reactive Performance Optimization

### Backpressure Strategies

**Стратегии **backpressure**:**

```java
@ApplicationScoped
public class BackpressureService {

    public Multi<Item> processWithBackpressure(Multi<Item> items) {
        return items
            .onOverflow().buffer(100)  // Буферизация
            .onOverflow().drop()       // Пропуск элементов
            .onOverflow().latest()     // Только последний элемент
            .onItem().transform(this::process);
    }
}
```

### Thread Pool Optimization

**Оптимизация пула потоков:**

```properties
quarkus.thread-pool.core-threads=10
quarkus.thread-pool.max-threads=50
quarkus.thread-pool.queue-size=1000
```

## Reactive Error Handling Patterns

### Error Recovery Strategies

**Стратегии восстановления после ошибок:**

```java
@ApplicationScoped
public class ErrorRecoveryService {

    public Uni<String> processWithRecovery(String input) {
        return processData(input)
            .onFailure().recoverWithItem("default")
            .onFailure(TimeoutException.class).recoverWithUni(() ->
                retryProcess(input)
            )
            .onFailure().retry().atMost(3)
            .onFailure().invoke(error -> log.error("Processing failed", error));
    }
}
```

### Error Classification

**Классификация ошибок:**

```java
@ApplicationScoped
public class ErrorClassificationService {

    public Uni<Result> processWithClassification(String input) {
        return processData(input)
            .onFailure(RetryableException.class).retry().atMost(3)
            .onFailure(NonRetryableException.class).recoverWithItem(
                Result.failure("Non-retryable error")
            )
            .onFailure().transform(error ->
                new ProcessingException("Processing failed", error)
            );
    }
}
```

## Reactive Testing Patterns

### Testing Uni

**Тестирование **Uni**:**

```java
@QuarkusTest
public class UniTest {

    @Test
    void testUni() {
        Uni<String> uni = service.getData();

        String result = uni.await().atMost(Duration.ofSeconds(5));
        assertEquals("expected", result);
    }
}
```

### Testing Multi

**Тестирование **Multi**:**

```java
@QuarkusTest
public class MultiTest {

    @Test
    void testMulti() {
        Multi<String> multi = service.getDataStream();

        List<String> results = multi
            .collect().asList()
            .await().atMost(Duration.ofSeconds(5));

        assertEquals(3, results.size());
    }
}
```

## Reactive Performance Monitoring

### Throughput Monitoring

**Мониторинг пропускной способности:**

```java
@ApplicationScoped
public class ThroughputMonitor {

    @Inject
    MeterRegistry registry;

    public Uni<String> processWithMonitoring(String input) {
        Timer.Sample sample = Timer.start(registry);
        return processData(input)
            .onItem().invoke(result -> {
                sample.stop(registry.timer("processing.duration"));
                registry.counter("processing.count").increment();
            });
    }
}
```

### Latency Tracking

**Отслеживание задержек:**

```java
@ApplicationScoped
public class LatencyTracker {

    @Inject
    MeterRegistry registry;

    public Uni<String> processWithLatencyTracking(String input) {
        long startTime = System.currentTimeMillis();
        return processData(input)
            .onItem().invoke(result -> {
                long latency = System.currentTimeMillis() - startTime;
                registry.gauge("processing.latency", latency);
            });
    }
}
```


## Заключение

**Quarkus Reactive** предоставляет мощные инструменты для **reactive** программирования. Поддержка **Mutiny**, **reactive messaging**, **reactive streams**, **context propagation**, **error recovery**, **backpressure handling** и других продвинутых возможностей позволяет создавать неблокирующие, высокопроизводительные приложения. Правильное использование **reactive** паттернов, обработка ошибок, оптимизация производительности, тестирование и управление **backpressure** являются ключевыми аспектами создания надежных **reactive** приложений.

## Дополнительные ресурсы

- [**Quarkus Reactive** Guide](https://quarkus.io/guides/getting-started-reactive)
- [Mutiny Documentation](https://smallrye.io/docs/smallrye-mutiny/)
- [**Reactive Streams** Specification](https://www.reactive-streams.org/)
- [**Hibernate Reactive**](https://hibernate.org/reactive/)
- [**MicroProfile Reactive Messaging**](https://download.eclipse.org/microprofile/microprofile-reactive-messaging-3.0.html)
- [**Circuit Breaker** Pattern](https://martinfowler.com/bliki/CircuitBreaker.html)

## См. также

- [Quarkus: Actuator — Health Checks и Metrics](quarkus-actuator.md)
- [Quarkus: Основы](quarkus-basics.md)
- [Quarkus: Cache — Кеширование данных](quarkus-cache.md)
- [Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh](quarkus-cloud.md)
- [Quarkus: Core — CDI, Bean Scopes и Configuration](quarkus-core.md)
