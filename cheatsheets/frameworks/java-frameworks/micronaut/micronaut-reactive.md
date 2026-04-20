---
title: "Micronaut: Reactive Programming — RxJava, Reactor и Reactive Streams"
description: "Полное руководство по реактивному программированию в Micronaut: RxJava, Project Reactor, Reactive Streams и async operations"
tags:
  - micronaut
  - reactive
  - rxjava
  - reactor
  - reactive-streams
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-testing.md", "micronaut-cloud.md"]
updated: "2026-04-20"
related: ["micronaut-http.md", "micronaut-data.md"]
---

# Micronaut: Reactive Programming — RxJava, Reactor и Reactive Streams

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Reactive Streams](#reactive-streams)
  - [Основы](#основы)
- [RxJava](#rxjava)
  - [Настройка](#настройка)
  - [Reactive Controllers](#reactive-controllers)
  - [Reactive Services](#reactive-services)
  - [Error Handling](#error-handling)
- [Project Reactor](#project-reactor)
  - [Настройка](#настройка-1)
  - [Reactive Controllers с Reactor](#reactive-controllers-с-reactor)
  - [Reactive Services с Reactor](#reactive-services-с-reactor)
- [Reactive HTTP Client](#reactive-http-client)
  - [Настройка](#настройка-2)
  - [Использование Reactive HTTP Client](#использование-reactive-http-client)
- [Reactive Database Access (R2DBC)](#reactive-database-access-r2dbc)
  - [Настройка](#настройка-3)
  - [Reactive Repository](#reactive-repository)
  - [Использование Reactive Repository](#использование-reactive-repository)
- [Backpressure](#backpressure)
  - [Обработка Backpressure](#обработка-backpressure)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте правильные Schedulers](#1-используйте-правильные-schedulers)
  - [2. Обрабатывайте ошибки](#2-обрабатывайте-ошибки)
  - [3. Используйте timeout](#3-используйте-timeout)
  - [4. Избегайте блокирующих операций](#4-избегайте-блокирующих-операций)
  - [5. Используйте правильные типы](#5-используйте-правильные-типы)
- [Продвинутые паттерны](#продвинутые-паттерны)
  - [Combining Reactive Streams](#combining-reactive-streams)
  - [Error Recovery](#error-recovery)
  - [Backpressure Strategies](#backpressure-strategies)
- [Reactive Streams Operators](#reactive-streams-operators)
  - [Transformation Operators](#transformation-operators)
  - [Combining Operators](#combining-operators)
  - [Filtering Operators](#filtering-operators)
  - [Error Handling Operators](#error-handling-operators)
  - [Buffering and Windowing](#buffering-and-windowing)
- [Reactive Testing](#reactive-testing)
  - [StepVerifier](#stepverifier)
- [Schedulers](#schedulers)
  - [Scheduler Configuration](#scheduler-configuration)
- [Hot vs Cold Publishers](#hot-vs-cold-publishers)
  - [Cold Publisher](#cold-publisher)
  - [Hot Publisher](#hot-publisher)
- [Reactive Testing](#reactive-testing-1)
  - [Testing Reactive Streams](#testing-reactive-streams)
- [Reactive Error Handling](#reactive-error-handling)
  - [Error Recovery Strategies](#error-recovery-strategies)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** полностью поддерживает реактивное программирование через **Reactive Streams API**, **RxJava** и **Project Reactor**. Это позволяет создавать высокопроизводительные, неблокирующие приложения, которые эффективно используют ресурсы.

### Основные возможности

- **Reactive Streams**: Стандартный **Reactive Streams API**
- **RxJava**: Полная поддержка **RxJava** 2 и 3
- **Project Reactor**: Интеграция с **Reactor** (Mono, Flux)
- **Non-blocking I/O**: Неблокирующие операции ввода-вывода
- **Backpressure**: Автоматическая обработка **backpressure**
- **Reactive HTTP Client**: реактивный **HTTP** клиент
- **Reactive Database Access**: реактивный доступ к БД через **R2DBC**

## Reactive Streams

### Основы

**Reactive Streams** — это стандарт для асинхронной обработки потоков данных с необязательной обратной связью (backpressure).

```java
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class ReactiveStreamsExample {

    public Publisher<String> createPublisher() {
        return subscriber -> {
            subscriber.onSubscribe(new Subscription() {
                private boolean cancelled = false;

                @Override
                public void request(long n) {
                    if (!cancelled && n > 0) {
                        subscriber.onNext("Item 1");
                        subscriber.onNext("Item 2");
                        subscriber.onNext("Item 3");
                        subscriber.onComplete();
                    }
                }

                @Override
                public void cancel() {
                    cancelled = true;
                }
            });
        };
    }
}
```

## RxJava

### Настройка

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.rxjava3:micronaut-rxjava3")
    // или для RxJava 2
    // implementation("io.micronaut.rxjava2:micronaut-rxjava2")
}
```

### Reactive Controllers

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Observable;

@Controller("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Get("/{id}")
    public Single<User> getUser(Long id) {
        return Single.fromCallable(() -> userService.findById(id))
            .subscribeOn(Schedulers.io());
    }

    @Get
    public Observable<User> getAllUsers() {
        return Observable.fromIterable(userService.findAll())
            .subscribeOn(Schedulers.io());
    }

    @Get("/stream")
    public Observable<User> streamUsers() {
        return Observable.interval(1, TimeUnit.SECONDS)
            .map(i -> userService.findById(i))
            .filter(Objects::nonNull)
            .take(10);
    }
}
```

### Reactive Services

```java
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Single<User> createUser(User user) {
        return Single.fromCallable(() -> userRepository.save(user))
            .subscribeOn(Schedulers.io())
            .flatMap(savedUser ->
                emailService.sendWelcomeEmail(savedUser)
                    .map(v -> savedUser)
            );
    }

    public Observable<User> findAllUsers() {
        return Observable.fromIterable(userRepository.findAll())
            .subscribeOn(Schedulers.io());
    }

    public Single<List<User>> findUsersByAge(Integer minAge) {
        return Observable.fromIterable(userRepository.findAll())
            .filter(user -> user.getAge() >= minAge)
            .toList()
            .subscribeOn(Schedulers.io());
    }
}
```

### Error Handling

```java
@Singleton
public class UserService {

    public Single<User> getUser(Long id) {
        return Single.fromCallable(() -> userRepository.findById(id))
            .subscribeOn(Schedulers.io())
            .flatMap(optional ->
                optional.map(Single::just)
                    .orElse(Single.error(new UserNotFoundException(id)))
            )
            .onErrorResumeNext(throwable -> {
                if (throwable instanceof UserNotFoundException) {
                    return Single.error(throwable);
                }
                return Single.error(new ServiceException("Failed to get user", throwable));
            });
    }

    public Observable<User> streamUsers() {
        return Observable.fromIterable(userRepository.findAll())
            .subscribeOn(Schedulers.io())
            .onErrorResumeNext(Observable.empty())
            .retry(3);
    }
}
```

## Project Reactor

### Настройка

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.reactor:micronaut-reactor")
}
```

### Reactive Controllers с Reactor

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Controller("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Get("/{id}")
    public Mono<User> getUser(Long id) {
        return Mono.fromCallable(() -> userService.findById(id))
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Get
    public Flux<User> getAllUsers() {
        return Flux.fromIterable(userService.findAll())
            .subscribeOn(Schedulers.boundedElastic());
    }

    @Get("/stream")
    public Flux<User> streamUsers() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> userService.findById(i))
            .filter(Objects::nonNull)
            .take(10);
    }
}
```

### Reactive Services с Reactor

```java
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Mono<User> createUser(User user) {
        return Mono.fromCallable(() -> userRepository.save(user))
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(savedUser ->
                emailService.sendWelcomeEmail(savedUser)
                    .thenReturn(savedUser)
            );
    }

    public Flux<User> findAllUsers() {
        return Flux.fromIterable(userRepository.findAll())
            .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<List<User>> findUsersByAge(Integer minAge) {
        return Flux.fromIterable(userRepository.findAll())
            .filter(user -> user.getAge() >= minAge)
            .collectList()
            .subscribeOn(Schedulers.boundedElastic());
    }
}
```

## Reactive HTTP Client

### Настройка

**application.yml:**

```yaml
micronaut:
  http:
    client:
      read-timeout: 30s
      connect-timeout: 10s
```

### Использование Reactive HTTP Client

```java
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.rxjava3.core.Single;

@Client("https://api.example.com")
public interface ExternalApiClient {

    @Get("/users/{id}")
    Single<User> getUser(Long id);

    @Get("/users")
    Observable<User> getAllUsers();
}
```

```java
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private final ExternalApiClient apiClient;

    public UserService(ExternalApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Single<User> getUserFromExternalApi(Long id) {
        return apiClient.getUser(id)
            .timeout(5, TimeUnit.SECONDS)
            .retry(3);
    }
}
```

## Reactive Database Access (R2DBC)

### Настройка

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.data:micronaut-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql")
}
```

**application.yml:**

```yaml
r2dbc:
  datasources:
    default:
      url: r2dbc:postgresql://localhost:5432/mydb
      username: ${DB_USERNAME}
      password: ${DB_PASSWORD}
```

### Reactive Repository

```java
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.reactive.ReactiveStreamsRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository
        extends ReactiveStreamsRepository<User, Long> {

    Mono<User> findByEmail(String email);

    Flux<User> findByAgeGreaterThan(Integer age);

    Mono<Long> countByAgeGreaterThan(Integer age);
}
```

### Использование Reactive Repository

```java
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@Singleton
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    public Mono<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Flux<User> findAdults() {
        return userRepository.findByAgeGreaterThan(17);
    }
}
```

## Backpressure

### Обработка Backpressure

```java
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public Flux<String> createBackpressureAwarePublisher() {
    return Flux.create(emitter -> {
        for (int i = 0; i < 1000; i++) {
            if (emitter.requestedFromDownstream() == 0) {
                // Ждем запроса от downstream
                break;
            }
            emitter.next("Item " + i);
        }
        emitter.complete();
    }, FluxSink.OverflowStrategy.BUFFER);
}
```

## Лучшие практики

### 1. Используйте правильные Schedulers

```java
// ✅ Хорошо - для I/O операций
Single.fromCallable(() -> database.query())
    .subscribeOn(Schedulers.io());

// ✅ Хорошо - для CPU-intensive операций
Single.fromCallable(() -> heavyComputation())
    .subscribeOn(Schedulers.computation());
```

### 2. Обрабатывайте ошибки

```java
// ✅ Хорошо
Mono.fromCallable(() -> riskyOperation())
    .onErrorResume(throwable -> {
        log.error("Error occurred", throwable);
        return Mono.just(defaultValue);
    });
```

### 3. Используйте timeout

```java
// ✅ Хорошо
Mono.fromCallable(() -> externalApiCall())
    .timeout(Duration.ofSeconds(5))
    .retry(3);
```

### 4. Избегайте блокирующих операций

```java
// ❌ Плохо
public User getUser(Long id) {
    return userRepository.findById(id).block(); // Блокирует поток
}

// ✅ Хорошо
public Mono<User> getUser(Long id) {
    return userRepository.findById(id);
}
```

### 5. Используйте правильные типы

```java
// ✅ Хорошо - один элемент
Mono<User> getUser(Long id);

// ✅ Хорошо - множество элементов
Flux<User> getAllUsers();

// ✅ Хорошо - может быть пустым
Mono<Optional<User>> findUser(Long id);
```

## Продвинутые паттерны

### Combining Reactive Streams

```java
@Singleton
public class UserService {

    public Mono<User> getUserWithOrders(Long userId) {
        Mono<User> userMono = userRepository.findById(userId);
        Flux<Order> ordersFlux = orderRepository.findByUserId(userId);

        return userMono.zipWith(ordersFlux.collectList())
            .map(tuple -> {
                User user = tuple.getT1();
                List<Order> orders = tuple.getT2();
                user.setOrders(orders);
                return user;
            });
    }

    public Flux<User> getUsersWithParallelProcessing() {
        return userRepository.findAll()
            .flatMap(user ->
                Mono.fromCallable(() -> enrichUser(user))
                    .subscribeOn(Schedulers.boundedElastic())
            )
            .parallel(4)
            .runOn(Schedulers.parallel())
            .sequential();
    }
}
```

### Error Recovery

```java
@Singleton
public class ResilientUserService {

    public Mono<User> getUserWithRetry(Long id) {
        return userRepository.findById(id)
            .retry(3)
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
            .onErrorResume(UserNotFoundException.class,
                ex -> Mono.just(createDefaultUser()))
            .onErrorResume(TimeoutException.class,
                ex -> getUserFromCache(id));
    }

    public Flux<User> getAllUsersWithCircuitBreaker() {
        return Flux.fromIterable(userRepository.findAll())
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume(CircuitBreakerOpenException.class,
                ex -> getCachedUsers());
    }
}
```

### Backpressure Strategies

```java
@Singleton
public class BackpressureService {

    public Flux<User> streamUsersWithBackpressure() {
        return Flux.create(emitter -> {
            userRepository.findAll()
                .subscribe(
                    user -> emitter.next(user),
                    error -> emitter.error(error),
                    () -> emitter.complete()
                );
        }, FluxSink.OverflowStrategy.BUFFER);
    }

    public Flux<User> streamUsersWithDrop() {
        return Flux.create(emitter -> {
            // Drop strategy - пропускает элементы при переполнении
        }, FluxSink.OverflowStrategy.DROP);
    }

    public Flux<User> streamUsersWithLatest() {
        return Flux.create(emitter -> {
            // Latest strategy - сохраняет только последний элемент
        }, FluxSink.OverflowStrategy.LATEST);
    }
}
```

## Reactive Streams Operators

### Transformation Operators

```java
@Singleton
public class TransformationService {

    public Flux<String> transformUsers(Flux<User> users) {
        return users
            .map(user -> user.getName().toUpperCase())
            .filter(name -> name.length() > 3)
            .distinct()
            .sort();
    }

    public Mono<User> enrichUser(Mono<User> userMono) {
        return userMono
            .flatMap(user ->
                Mono.zip(
                    getProfile(user.getId()),
                    getOrders(user.getId())
                )
                .map(tuple -> {
                    user.setProfile(tuple.getT1());
                    user.setOrders(tuple.getT2());
                    return user;
                })
            );
    }
}
```

### Combining Operators

```java
@Singleton
public class CombiningService {

    public Flux<User> combineUsers(Flux<User> users1, Flux<User> users2) {
        return Flux.merge(users1, users2)
            .distinct(User::getId);
    }

    public Flux<User> zipUsers(Flux<User> users1, Flux<User> users2) {
        return Flux.zip(users1, users2)
            .map(tuple -> {
                User user1 = tuple.getT1();
                User user2 = tuple.getT2();
                // Combine users
                return user1;
            });
    }

    public Flux<User> concatUsers(Flux<User> users1, Flux<User> users2) {
        return Flux.concat(users1, users2);
    }
}
```

### Filtering Operators

```java
@Singleton
public class FilteringService {

    public Flux<User> filterAdults(Flux<User> users) {
        return users
            .filter(user -> user.getAge() >= 18)
            .take(100)
            .skip(10);
    }

    public Flux<User> distinctUsers(Flux<User> users) {
        return users
            .distinct(User::getEmail)
            .distinctUntilChanged();
    }
}
```

### Error Handling Operators

```java
@Singleton
public class ErrorHandlingService {

    public Flux<User> handleErrors(Flux<User> users) {
        return users
            .onErrorResume(error -> {
                log.error("Error occurred", error);
                return Flux.just(createDefaultUser());
            })
            .onErrorReturn(createDefaultUser())
            .retry(3)
            .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)));
    }

    public Mono<User> handleTimeout(Mono<User> userMono) {
        return userMono
            .timeout(Duration.ofSeconds(5))
            .onErrorResume(TimeoutException.class, ex ->
                getUserFromCache()
            );
    }
}
```

### Buffering and Windowing

```java
@Singleton
public class BufferingService {

    public Flux<List<User>> bufferUsers(Flux<User> users) {
        return users
            .buffer(10)
            .buffer(Duration.ofSeconds(1))
            .bufferTimeout(10, Duration.ofSeconds(1));
    }

    public Flux<Flux<User>> windowUsers(Flux<User> users) {
        return users
            .window(10)
            .window(Duration.ofSeconds(1));
    }
}
```

## Reactive Testing

### StepVerifier

```java
@MicronautTest
public class ReactiveTestingTest {

    @Inject
    UserService userService;

    @Test
    void testReactiveStream() {
        Flux<User> users = userService.findAllUsers();

        StepVerifier.create(users)
            .expectNextCount(10)
            .expectNextMatches(user -> user.getAge() >= 18)
            .expectComplete()
            .verify(Duration.ofSeconds(5));
    }

    @Test
    void testErrorHandling() {
        Mono<User> userMono = userService.getUser(999L);

        StepVerifier.create(userMono)
            .expectError(UserNotFoundException.class)
            .verify();
    }
}
```

## Schedulers

### Scheduler Configuration

```java
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import jakarta.inject.Singleton;

@Singleton
public class SchedulerService {

    public Mono<User> getUserWithScheduler(Long id) {
        return Mono.fromCallable(() -> userRepository.findById(id))
            .subscribeOn(Schedulers.boundedElastic())
            .map(optional -> optional.orElseThrow());
    }

    public Flux<User> processUsersWithParallelScheduler(Flux<User> users) {
        return users
            .parallel(4)
            .runOn(Schedulers.parallel())
            .map(this::processUser)
            .sequential();
    }

    private User processUser(User user) {
        // Обработка пользователя
        return user;
    }
}
```

## Hot vs Cold Publishers

### Cold Publisher

```java
@Singleton
public class ColdPublisherService {

    public Flux<User> getColdUserStream() {
        return Flux.fromIterable(userRepository.findAll())
            .delayElements(Duration.ofSeconds(1));
    }
}
```

### Hot Publisher

```java
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Singleton
public class HotPublisherService {
    private final Sinks.Many<User> userSink = Sinks.many().multicast().onBackpressureBuffer();

    public Flux<User> getUserStream() {
        return userSink.asFlux();
    }

    public void emitUser(User user) {
        userSink.tryEmitNext(user);
    }
}
```

## Reactive Testing

### Testing Reactive Streams

```java
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import jakarta.inject.Singleton;

@Singleton
public class ReactiveTestService {

    public Flux<User> getUsers() {
        return Flux.just(
            new User("John", "john@example.com"),
            new User("Jane", "jane@example.com")
        );
    }

    @Test
    void testUserStream() {
        Flux<User> users = getUsers();

        StepVerifier.create(users)
            .expectNextMatches(user -> user.getName().equals("John"))
            .expectNextMatches(user -> user.getName().equals("Jane"))
            .expectComplete()
            .verify();
    }
}
```

## Reactive Error Handling

### Error Recovery Strategies

```java
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import java.time.Duration;

@Singleton
public class ErrorRecoveryService {

    public Flux<User> getUsersWithRetry() {
        return userRepository.findAll()
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                .maxBackoff(Duration.ofSeconds(10))
                .doBeforeRetry(retrySignal ->
                    log.warn("Retrying after error: {}", retrySignal.failure())))
            .onErrorResume(error -> {
                log.error("Failed to get users after retries", error);
                return Flux.just(new User("Default", "default@example.com"));
            });
    }
}
```

## Заключение

**Micronaut** предоставляет полную поддержку реактивного программирования через **Reactive Streams**, **RxJava** и **Project Reactor**. Правильное использование реактивных паттернов, обработки ошибок, **backpressure**, **combining streams**, **error recovery**, операторов трансформации, фильтрации, буферизации, **windowing**, **schedulers**, **hot**/**cold publishers**, **reactive testing**, **error recovery strategies** и других продвинутых возможностей позволяет создавать высокопроизводительные, неблокирующие приложения, которые эффективно используют ресурсы.

## Дополнительные ресурсы

- [**Micronaut Reactive** Documentation](https://micronaut-projects.github.io/micronaut-rxjava3/latest/guide/)
- [**RxJava** Documentation](https://github.com/ReactiveX/RxJava/wiki)
- [Project **Reactor** Documentation](https://projectreactor.io/docs/core/release/reference/)
- [**Reactive Streams** Specification](https://www.reactive-streams.org/)
- [**Reactor Reference** Guide](https://projectreactor.io/docs/core/release/reference/)

## См. также

- [Micronaut: Actuator — Health Checks, Metrics и Endpoints](micronaut-actuator.md)
- [Micronaut: Основы](micronaut-basics.md)
- [Micronaut: Batch Processing — Job Processing и Scheduling](micronaut-batch.md)
- [Micronaut: Caching — Cache Abstraction и Redis Cache](micronaut-cache.md)
- [Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing](micronaut-cloud.md)
