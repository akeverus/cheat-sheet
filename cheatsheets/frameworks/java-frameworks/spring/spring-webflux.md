---
title: "Spring WebFlux для Java"
description: "Комплексное руководство по Spring WebFlux: реактивному веб-фреймворку для создания неблокирующих, асинхронных веб-приложений с использованием Project Reactor. Подробно рассматриваются reactive streams, Mono/Flux, WebClient, functional endpoints, backpressure, concurrency и produc"
tags:
  - frameworks
  - java-frameworks
  - spring-webflux
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Spring WebFlux для Java

Комплексное руководство по **Spring WebFlux**: реактивному веб-фреймворку для создания неблокирующих, асинхронных веб-приложений с использованием **Project Reactor**. Подробно рассматриваются **reactive streams**, **Mono**/**Flux**, **WebClient**, **functional endpoints**, **backpressure**, **concurrency** и **production deployment**.

## Полезные ссылки

### Официальная документация
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html) — полная документация **Spring WebFlux**
- [Project Reactor](https://projectreactor.io/docs/core/release/reference/) — документация **Project Reactor**
- [Reactive Streams](https://www.reactive-streams.org/) — спецификация **Reactive Streams**

### Книги и ресурсы
- [Reactive Programming with RxJava](https://www.oreilly.com/library/view/reactive-programming-with/9781491931646/) — классическая книга по реактивному программированию
- [Hands-On Reactive Programming in Spring 5](https://www.packtpub.com/product/hands-on-reactive-programming-in-spring-5/9781787284951) — практическое руководство
- [Reactive Systems in Java](https://www.oreilly.com/library/view/reactive-systems-in/9781492091641/) — современное руководство

### Статьи и туториалы
- [Spring WebFlux Tutorial](https://spring.io/guides/gs/reactive-rest-service/) — официальный туториал
- [Reactive Programming](https://www.baeldung.com/java-reactive-systems) — концепции реактивного программирования
- [Backpressure in Reactive Streams](https://www.reactive-streams.org/) — **Backpressure** объяснение

### См. также
- [[spring-boot|Spring Boot]] — **Spring Boot** основы
- [[spring-mvc|Spring MVC]] — **Spring MVC** (blocking)
- [Мониторинг](../../../monitoring/) — мониторинг реактивных приложений

## Содержание

- [Введение в Spring WebFlux](#введение-в-spring-webflux)
  - [Почему WebFlux?](#почему-webflux)
  - [Когда использовать WebFlux?](#когда-использовать-webflux)
 - [ Идеально подходит для:](#идеально-подходит-для)
 - [ Не подходит для:](#не-подходит-для)
  - [Архитектурные преимущества](#архитектурные-преимущества)
    - [Non-blocking I/O](#non-blocking-io)
    - [Backpressure handling](#backpressure-handling)
- [Reactive Programming основы](#reactive-programming-основы)
  - [Reactive Streams спецификация](#reactive-streams-спецификация)
    - [Publisher](#publisher)
    - [Subscriber](#subscriber)
    - [Subscription](#subscription)
    - [Processor](#processor)
  - [Reactive vs Imperative](#reactive-vs-imperative)
    - [Imperative подход](#imperative-подход)
    - [Reactive подход](#reactive-подход)
- [Project Reactor](#project-reactor)
  - [Mono и Flux](#mono-и-flux)
    - [Mono](#mono)
    - [Flux](#flux)
  - [Operators](#operators)
    - [Transforming operators](#transforming-operators)
    - [Filtering operators](#filtering-operators)
    - [Combining operators](#combining-operators)
    - [Error handling operators](#error-handling-operators)
  - [Schedulers](#schedulers)
    - [publishOn() — переключение downstream execution](#publishon-переключение-downstream-execution)
    - [subscribeOn() — переключение upstream execution](#subscribeon-переключение-upstream-execution)
- [Spring WebFlux архитектура](#spring-webflux-архитектура)
  - [HTTP Server adapters](#http-server-adapters)
    - [Netty (по умолчанию)](#netty-по-умолчанию)
    - [Tomcat/Jetty](#tomcatjetty)
  - [Codec configuration](#codec-configuration)
    - [Jackson JSON codec](#jackson-json-codec)
  - [Exception handling](#exception-handling)
    - [Global exception handler](#global-exception-handler)
- [Функциональные endpoints](#функциональные-endpoints)
  - [RouterFunction](#routerfunction)
    - [Basic routing](#basic-routing)
    - [Advanced routing](#advanced-routing)
  - [Handler functions](#handler-functions)
    - [User handler](#user-handler)
- [Аннотационные контроллеры](#аннотационные-контроллеры)
  - [@RestController](#restcontroller)
    - [Basic controller](#basic-controller)
  - [Advanced controller features](#advanced-controller-features)
    - [Request/response handling](#requestresponse-handling)
    - [Validation и error handling](#validation-и-error-handling)
    - [Custom response types](#custom-response-types)
- [WebClient](#webclient)
  - [Basic WebClient](#basic-webclient)
    - [Configuration](#configuration)
  - [HTTP methods](#http-methods)
    - [GET requests](#get-requests)
    - [POST/PUT requests](#postput-requests)
  - [Advanced WebClient features](#advanced-webclient-features)
    - [Error handling](#error-handling)
    - [File upload/download](#file-uploaddownload)
- [Обработка ошибок](#обработка-ошибок)
  - [Reactive exception handling](#reactive-exception-handling)
    - [Global error handler](#global-error-handler)
    - [Controller-level error handling](#controller-level-error-handling)
- [Backpressure](#backpressure)
  - [Understanding backpressure](#understanding-backpressure)
    - [What is backpressure?](#what-is-backpressure)
    - [Backpressure strategies](#backpressure-strategies)
  - [Implementing backpressure](#implementing-backpressure)
    - [Custom producer with backpressure](#custom-producer-with-backpressure)
    - [Consumer-controlled backpressure](#consumer-controlled-backpressure)
- [Тестирование](#тестирование)
  - [Unit testing reactive code](#unit-testing-reactive-code)
    - [Testing Mono/Flux](#testing-monoflux)
    - [Testing with virtual time](#testing-with-virtual-time)
  - [Integration testing](#integration-testing)
    - [Testing WebFlux endpoints](#testing-webflux-endpoints)
    - [Testing WebClient](#testing-webclient)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Reactor optimization](#reactor-optimization)
    - [Operator fusion](#operator-fusion)
    - [Parallel processing](#parallel-processing)
    - [Batching operations](#batching-operations)
  - [Connection pooling](#connection-pooling)
    - [Reactor Netty configuration](#reactor-netty-configuration)
  - [Memory optimization](#memory-optimization)
    - [Object pooling](#object-pooling)
    - [Streaming responses](#streaming-responses)
- [Production deployment](#production-deployment)
  - [Configuration management](#configuration-management)
    - [Environment-specific configuration](#environment-specific-configuration)
- [application-prod.yaml](#application-prodyaml)
    - [Docker configuration](#docker-configuration)
- [Install required packages](#install-required-packages)
- [Create app user](#create-app-user)
- [Set working directory](#set-working-directory)
- [Copy application](#copy-application)
- [Change ownership](#change-ownership)
- [Switch to non-root user](#switch-to-non-root-user)
- [Health check](#health-check)
- [JVM tuning for containers](#jvm-tuning-for-containers)
- [Start application](#start-application)
    - [Kubernetes deployment](#kubernetes-deployment)
  - [Monitoring и alerting](#monitoring-и-alerting)
    - [Micrometer metrics](#micrometer-metrics)
    - [Custom metrics](#custom-metrics)
- [Best practices](#best-practices)
  - [Application design](#application-design)
    - [1. Choose appropriate return types](#1-choose-appropriate-return-types)
    - [2. Handle blocking operations properly](#2-handle-blocking-operations-properly)
    - [3. Error handling patterns](#3-error-handling-patterns)
  - [Performance optimization](#performance-optimization)
    - [4. Connection and thread management](#4-connection-and-thread-management)
    - [5. Caching strategies](#5-caching-strategies)
  - [Testing strategies](#testing-strategies)
    - [6. Test data management](#6-test-data-management)
    - [7. Integration test patterns](#7-integration-test-patterns)
  - [Operational practices](#operational-practices)
    - [8. Health checks and monitoring](#8-health-checks-and-monitoring)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
    - [Thread blocking issues](#thread-blocking-issues)
    - [Memory leaks](#memory-leaks)
    - [Slow performance](#slow-performance)
  - [Debug techniques](#debug-techniques)
    - [Reactor debugging](#reactor-debugging)
    - [Performance monitoring](#performance-monitoring)
- [Заключение](#заключение)
  - [Ключевые возможности:](#ключевые-возможности)
    - [Performance:](#performance)
    - [Scalability:](#scalability)
  - [Когда НЕ использовать:](#когда-не-использовать)
  - [Production considerations:](#production-considerations)
  - [Best practices summary:](#best-practices-summary)

## Введение в Spring WebFlux

**Spring WebFlux** — это реактивный веб-фреймворк, построенный на принципах **reactive programming**. Он позволяет создавать неблокирующие, асинхронные веб-приложения, которые могут эффективно обрабатывать большое количество одновременных подключений с минимальным использованием ресурсов.

### Почему WebFlux?

**WebFlux** решает критические проблемы традиционных блокирующих веб-фреймворков:**

1. **Высокая concurrency** — эффективная обработка тысяч одновременных подключений
2. **Resource efficiency** — минимальное использование памяти и **CPU**
3. **Elastic scaling** — автоматическое масштабирование под нагрузку
4. **Backpressure** — контроль потока данных для предотвращения перегрузки
5. **Functional programming** — функциональный стиль программирования
6. **Non-blocking I/O** — асинхронный ввод/вывод
7. **Reactive streams** — стандартизированный подход к **reactive programming**
8. **Integration** — бесшовная интеграция с **reactive** базами данных и системами

### Когда использовать WebFlux?

#### Идеально подходит для:
- **High-concurrency applications** — приложения с высокой одновременной нагрузкой
- **Microservices** — асинхронная коммуникация между сервисами
- **Real-time applications** — **WebSocket**, **Server-Sent Events**
- **Streaming data** — обработка потоков данных
- **I/O bound applications** — приложения с интенсивным I/O
- **Cloud-native applications** — масштабируемые облачные приложения
- **Reactive systems** — системы построенные на **reactive** принципах

#### Не подходит для:
- **`Simple CRUD` applications** — используйте **Spring MVC**
- **Blocking operations** — если весь код блокирующий
- **Small applications** — **overhead** не оправдан
- **Legacy integration** — если нужно интегрироваться с блокирующими **API**
- **`Simple REST` APIs** — **Spring MVC** проще для базовых задач
- **File uploads** — **Spring MVC** лучше для больших файлов

### Архитектурные преимущества

#### Non-blocking I/O

Сравнение блокирующего и реактивного (non-blocking) подхода в **Spring WebFlux**.

**Блокирующий подход (Spring MVC) и реактивный (WebFlux):**

```java
// Традиционный блокирующий подход (Spring MVC)
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    // Поток блокируется в ожидании базы данных
    User user = userRepository.findById(id).get();  // BLOCKING
    return user;
}

// Реактивный неблокирующий подход (WebFlux)
@GetMapping("/users/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    // Поток освобождается немедленно, результат доставляется асинхронно
    return userRepository.findById(id);  // NON-BLOCKING
}
```

#### Backpressure handling
```java
// Потребитель контролирует поток данных (backpressure)
Flux<String> data = Flux.just("item1", "item2", "item3", "item4", "item5");

data.onBackpressureBuffer(2)  // Буферизация только 2 элементов
    .delayElements(Duration.ofMillis(100))  // Медленная обработка
    .subscribe(
        item -> System.out.println("Processed: " + item),  // Обработка элемента
        error -> System.err.println("Error: " + error),    // Обработка ошибки
        () -> System.out.println("Completed")              // Завершение
    );
```

## Reactive Programming основы

### Reactive Streams спецификация

**Reactive Streams** — это стандарт для асинхронной обработки потоков данных с **non-blocking backpressure**. Спецификация определяет четыре основных интерфейса:**

#### Publisher
```java
// Интерфейс Publisher — источник данных
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);  // Подписка subscriber'а
}
```

**Publisher** — это источник данных, который может быть подписан **subscriber**'ом. **Publisher** отвечает за:**
- **Data production** — генерацию элементов данных
- **Subscription management** — управление подписками
- **Demand signaling** — реагирование на запросы данных от **subscriber**'а

#### Subscriber
```java
// Интерфейс Subscriber — потребитель данных
public interface Subscriber<T> {
    void onSubscribe(Subscription subscription);  // Вызывается при подписке
    void onNext(T item);                          // Получение следующего элемента
    void onError(Throwable throwable);            // Обработка ошибки
    void onComplete();                            // Сигнал завершения потока
}
```

**Subscriber** — это потребитель данных, который получает уведомления о:**
- **Subscription** — успешной подписке
- **Next item** — следующем элементе данных
- **Error** — ошибке в потоке
- **Completion** — завершении потока

#### Subscription
```java
// Интерфейс Subscription — управление потоком между Publisher и Subscriber
public interface Subscription {
    void request(long n);  // Запрос n элементов (backpressure)
    void cancel();         // Отмена подписки
}
```

**Subscription** — это связь между **Publisher** и **Subscriber**, которая позволяет:**
- **Request data** — запрашивать определенное количество элементов
- **Cancel subscription** — отменять подписку

#### Processor
```java
// Processor — одновременно Subscriber и Publisher для трансформации данных
public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {
}
```

**Processor** — это одновременно и **Subscriber** и **Publisher**, позволяющий трансформировать данные в потоке.

### Reactive vs Imperative

#### Imperative подход
```java
// Синхронный блокирующий подход
public List<User> getUsers() {
    List<User> users = new ArrayList<>();

    for (Long userId : userIds) {
        User user = userRepository.findById(userId);  // БЛОКИРУЕТ поток
        users.add(user);
    }

    return users;  // Возвращается только когда все данные готовы
}

// Проблемы императивного подхода:
// - Поток блокируется на каждом вызове БД
// - Неэффективная обработка больших наборов данных
// - Нет backpressure — потребитель не контролирует поток
// - Нет восстановления после ошибок — одна ошибка ломает всё
```

#### Reactive подход
```java
// Асинхронный неблокирующий подход
public Flux<User> getUsers(Flux<Long> userIds) {
    return userIds
        .flatMap(userId -> userRepository.findById(userId))  // НЕ БЛОКИРУЕТ
        .onErrorContinue((error, item) -> {                   // Устойчивость к ошибкам
            log.error("Error processing user: " + item, error);
        })
        .buffer(10)                                           // Backpressure — буферизация
        .delayElements(Duration.ofMillis(100));              // Контроль потока
}

// Преимущества реактивного подхода:
// - Поток никогда не блокируется — возвращается немедленно
// - Эффективное использование ресурсов — не нужен поток на каждый запрос
// - Backpressure — потребитель контролирует поток данных
// - Устойчивость к ошибкам — обработка продолжается при ошибках
// - Композиция — можно цепочить множество операций
```

## Project Reactor

### Mono и Flux

#### Mono
**Mono** представляет 0 или 1 элемент:**

```java
// Пустой Mono (0 элементов)
Mono<Void> emptyMono = Mono.empty();

// Mono с одним значением
Mono<String> monoWithValue = Mono.just("Hello");

// Mono из Callable (ленивое вычисление)
Mono<String> lazyMono = Mono.fromCallable(() -> {
    System.out.println("Computing value...");  // Выполняется только при подписке
    return "Computed value";
});

// Mono из CompletableFuture
Mono<String> monoFromFuture = Mono.fromFuture(completableFuture);

// Mono с ошибкой
Mono<String> errorMono = Mono.error(new RuntimeException("Something went wrong"));

// Mono с задержкой
Mono<String> delayedMono = Mono.just("Delayed")
    .delayElement(Duration.ofSeconds(1));  // Задержка 1 секунда
```

**Mono use cases:**
- **Single result** — результат **database query** (findById)
- **Optional value** — может быть пустым или содержать значение
- **Asynchronous computation** — результат длительной операции
- **HTTP response** — тело **HTTP** ответа
- **Configuration value** — получение конфигурационного параметра

#### Flux
**Flux** представляет 0 или N элементов (поток):**

```java
// Пустой Flux (0 элементов)
Flux<Void> emptyFlux = Flux.empty();

// Flux с несколькими значениями
Flux<String> fluxWithValues = Flux.just("item1", "item2", "item3");

// Flux из коллекции Iterable
Flux<String> fluxFromList = Flux.fromIterable(Arrays.asList("a", "b", "c"));

// Flux из Java Stream
Flux<Integer> fluxFromStream = Flux.fromStream(Stream.of(1, 2, 3, 4, 5));

// Flux с диапазоном чисел
Flux<Integer> rangeFlux = Flux.range(1, 100);  // От 1 до 100

// Бесконечный Flux (генерирует элементы каждую секунду)
Flux<Long> infiniteFlux = Flux.interval(Duration.ofSeconds(1));

// Flux с ошибкой
Flux<String> errorFlux = Flux.error(new RuntimeException("Flux error"));
```

**Flux use cases:**
- **Collections** — обработка списков элементов
- **Database results** — результаты **query** с множеством строк
- **File processing** — чтение файла построчно
- **Event streams** — поток событий или сообщений
- **Pagination** — постраничная загрузка данных
- **Time series** — временные ряды данных

### Operators

#### Transforming operators

**map()** — синхронная трансформация каждого элемента:**
```java
// map() — преобразование каждого элемента синхронно
Flux<String> names = Flux.just("john", "jane", "bob");

Flux<String> upperCaseNames = names.map(String::toUpperCase);
// Результат: JOHN, JANE, BOB

Flux<User> users = userIds.map(id -> userRepository.findById(id));
// Каждый ID преобразуется в объект User
```

**flatMap()** — асинхронная трансформация с возможностью изменения количества элементов:**
```java
// flatMap() — асинхронная трансформация с выравниванием потоков
Flux<Long> userIds = Flux.just(1L, 2L, 3L);

Flux<User> users = userIds.flatMap(id -> userRepository.findById(id));
// Каждый ID становится Mono<User>, выравнивается в Flux<User>

Flux<Order> orders = userIds.flatMap(userId ->
    orderRepository.findOrdersByUserId(userId));
// Один пользователь может иметь несколько заказов
```

**concatMap()** — последовательная асинхронная трансформация:**
```java
// concatMap() — сохраняет порядок элементов (в отличие от flatMap)
Flux<Long> userIds = Flux.just(1L, 2L, 3L);

Flux<User> users = userIds.concatMap(id -> userRepository.findById(id));
// Обрабатывает user 1, затем user 2, затем user 3 (порядок сохраняется)
```

#### Filtering operators

**filter()** — фильтрация элементов:**
```java
// filter() — отбор элементов по условию
Flux<User> activeUsers = allUsers.filter(User::isActive);  // Только активные пользователи

Flux<Integer> evenNumbers = Flux.range(1, 10)
    .filter(number -> number % 2 == 0);  // Только чётные числа
// Результат: 2, 4, 6, 8, 10
```

**distinct()** — удаление дубликатов:**
```java
// distinct() — удаление повторяющихся элементов
Flux<String> uniqueNames = names.distinct();  // Уникальные имена

Flux<User> uniqueUsers = users.distinct(User::getEmail);  // Уникальные по email
```

**take() / skip()** — ограничение количества элементов:**
```java
// take() — взять первые N элементов
Flux<Integer> first5 = Flux.range(1, 100).take(5);
// Результат: 1, 2, 3, 4, 5

// skip() — пропустить первые N элементов
Flux<Integer> skip10 = Flux.range(1, 100).skip(10);
// Результат: 11, 12, 13, ...
```

#### Combining operators

**zip()** — комбинация элементов из нескольких потоков:**
```java
// zip() — объединяет элементы из разных потоков попарно
Flux<String> names = Flux.just("John", "Jane", "Bob");
Flux<Integer> ages = Flux.just(25, 30, 35);

Flux<String> combined = Flux.zip(names, ages)
    .map(tuple -> tuple.getT1() + " is " + tuple.getT2() + " years old");
// Результат: John is 25 years old, Jane is 30 years old, Bob is 35 years old
```

**merge()** — слияние потоков без сохранения порядка:**
```java
// merge() — параллельное слияние потоков (порядок не гарантируется)
Flux<String> stream1 = Flux.just("A", "B").delayElements(Duration.ofMillis(100));
Flux<String> stream2 = Flux.just("1", "2").delayElements(Duration.ofMillis(150));

Flux<String> merged = Flux.merge(stream1, stream2);
// Возможный результат: A, 1, B, 2 (порядок зависит от времени)
```

**concat()** — последовательное объединение потоков:**
```java
// concat() — последовательное слияние потоков (порядок сохраняется)
Flux<String> stream1 = Flux.just("A", "B");
Flux<String> stream2 = Flux.just("1", "2");

Flux<String> concatenated = Flux.concat(stream1, stream2);
// Результат: A, B, 1, 2 (сначала stream1, затем stream2)
```

#### Error handling operators

**onErrorReturn()** — возвращение **fallback** значения при ошибке:**
```java
// onErrorReturn() — возвращает значение по умолчанию при ошибке
Mono<User> user = userRepository.findById(userId)
    .onErrorReturn(new User("default", "user"));
// При ошибке БД возвращается пользователь по умолчанию
```

**onErrorResume()** — продолжение с альтернативным потоком:**
```java
// onErrorResume() — переключение на альтернативный источник при ошибке
Mono<User> user = userRepository.findById(userId)
    .onErrorResume(error -> {
        if (error instanceof NotFoundException) {
            return userCache.getUser(userId);  // Пробуем кэш
        } else {
            return Mono.error(error);  // Пробрасываем другие ошибки
        }
    });
```

**retry()** — повторение операции при ошибке:**
```java
// retry() — автоматический повтор операции при сбое
Mono<User> user = userRepository.findById(userId)
    .retry(3)  // Повторить до 3 раз
    .timeout(Duration.ofSeconds(5));  // С таймаутом 5 секунд
```

**onErrorContinue()** — продолжение обработки при ошибке в элементе:**
```java
// onErrorContinue() — пропустить ошибочный элемент и продолжить
Flux<User> users = userIds
    .flatMap(id -> userRepository.findById(id))
    .onErrorContinue((error, item) -> {
        log.error("Error processing user {}: {}", item, error);
        // Продолжаем обработку остальных пользователей
    });
```

### Schedulers

#### publishOn() — переключение downstream execution
```java
// publishOn() — переключает поток выполнения для последующих операций
Mono<String> result = Mono.fromCallable(() -> {
        // Выполняется на вызывающем потоке
        return blockingDatabaseCall();
    })
    .publishOn(Schedulers.boundedElastic())  // Переключение на elastic пул
    .map(data -> {
        // Выполняется на elastic пуле потоков
        return processData(data);
    })
    .publishOn(Schedulers.parallel())  // Переключение на parallel пул
    .map(processed -> {
        // Выполняется на parallel пуле потоков
        return serializeResult(processed);
    });
```

**Common schedulers:**
- **Schedulers.immediate()** — текущий поток (по умолчанию)
- **Schedulers.single()** — **single reused thread**
- **Schedulers.elastic()** — **elastic thread pool** (для I/O операций)
- **Schedulers.parallel()** — **fixed thread pool** (для `CPU-bound` операций)
- **Schedulers.`boundedElastic()`** — **bounded elastic thread pool**

#### subscribeOn() — переключение upstream execution
```java
// subscribeOn() — влияет на поток выполнения всей цепочки вверх по потоку
Flux<String> data = Flux.fromIterable(largeList)
    .subscribeOn(Schedulers.boundedElastic())  // Upstream операции на elastic потоках
    .map(item -> processItem(item))            // Всё ещё на elastic потоках
    .publishOn(Schedulers.parallel())          // Переключение на parallel для CPU
    .map(processed -> transform(processed))    // На parallel потоках
    .subscribe(result -> handleResult(result)); // На parallel потоках
```

**subscribeOn vs `publishOn`:**
- **subscribeOn** — влияет на **upstream operators** (до publishOn)
- **publishOn** — влияет на **downstream operators** (после publishOn)
- **Можно использовать несколько publishOn** для разных фаз обработки

## Spring WebFlux архитектура

### HTTP Server adapters

#### Netty (по умолчанию)
```java
// Конфигурация Netty сервера для WebFlux
@Configuration
public class NettyConfig {

    @Bean
    public NettyReactiveWebServerFactory serverFactory() {
        NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
        factory.setPort(8080);  // Порт сервера

        // Настройка Netty
        factory.addServerCustomizers(server -> {
            // Конфигурация event loop групп
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);  // Принимает соединения
            EventLoopGroup workerGroup = new NioEventLoopGroup(); // Обрабатывает запросы

            server.group(bossGroup, workerGroup)
                  .channel(NioServerSocketChannel.class)
                  .childHandler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      protected void initChannel(SocketChannel ch) {
                          ch.pipeline().addLast(new HttpServerCodec());  // HTTP кодек
                      }
                  });
        });

        return factory;
    }
}
```

**Netty advantages:**
- **High performance** — **optimized for low latency and high throughput**
- **Non-blocking I/O** — **native support for reactive programming**
- **Scalability** — **handles thousands** of **concurrent connections**
- **Customization** — **extensive configuration options**

#### Tomcat/Jetty
```java
// Конфигурация Tomcat сервера для WebFlux
@Configuration
public class TomcatConfig {

    @Bean
    public TomcatReactiveWebServerFactory tomcatFactory() {
        TomcatReactiveWebServerFactory factory = new TomcatReactiveWebServerFactory();
        factory.setPort(8080);  // Порт сервера

        factory.addServerCustomizers(server -> {
            // Специфичная настройка Tomcat
            server.getConnector().setAsyncTimeout(30000);        // Таймаут 30 секунд
            server.getConnector().setMaxKeepAliveRequests(100);  // Max keep-alive запросов
        });

        return factory;
    }
}
```

**Servlet container advantages:**
- **Compatibility** — **works with existing servlet infrastructure**
- **Management** — **standard servlet container management**
- **Debugging** — **familiar servlet debugging tools**

### Codec configuration

#### Jackson JSON codec
```java
// Конфигурация Jackson кодеков для JSON сериализации
@Configuration
public class CodecConfig implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        // Настройка JSON декодера (для чтения request body)
        configurer.defaultCodecs().jackson2JsonDecoder(
            new Jackson2JsonDecoder(
                new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .registerModule(new JavaTimeModule())  // Поддержка Java 8 Date/Time
            )
        );

        // Настройка JSON энкодера (для записи response body)
        configurer.defaultCodecs().jackson2JsonEncoder(
            new Jackson2JsonEncoder(
                new ObjectMapper()
                    .registerModule(new JavaTimeModule())
            )
        );

        // Максимальный размер данных в памяти
        configurer.defaultCodecs().maxInMemorySize(512 * 1024); // 512KB
    }
}
```

**Codec responsibilities:**
- **Encoding** — преобразование **Java** объектов в **HTTP response body**
- **Decoding** — преобразование **HTTP request body** в **Java** объекты
- **Content negotiation** — выбор подходящего **codec** на основе **Content-Type**
- **Streaming** — поддержка **streaming** для больших **payloads**

### Exception handling

#### Global exception handler
```java
// Глобальный обработчик исключений для WebFlux
@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработка ошибки "пользователь не найден"
    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFound(UserNotFoundException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage())));
    }

    // Обработка ошибки валидации
    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(ValidationException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage())));
    }

    // Обработка всех остальных исключений
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred")));
    }
}
```

**Exception handling flow:**
1. **Exception thrown** — исключение возникает в **controller** или **service**
2. **Exception propagation** — исключение распространяется через **reactive chain**
3. **Handler lookup** — **WebFlux** ищет подходящий @**ExceptionHandler**
4. **Error response** — **handler** возвращает **Mono**<**ResponseEntity**>
5. **Response encoding** — **error response** сериализуется и отправляется клиенту

## Функциональные endpoints

### RouterFunction

#### Basic routing
```java
// Базовая маршрутизация с RouterFunction
@Configuration
public class FunctionalRoutes {

    @Bean
    public RouterFunction<ServerResponse> userRoutes(UserHandler handler) {
        // Функциональное определение REST API маршрутов
        return RouterFunctions.route()
            .GET("/api/users", handler::getAllUsers)           // Список пользователей
            .GET("/api/users/{id}", handler::getUserById)      // Получение по ID
            .POST("/api/users", handler::createUser)           // Создание
            .PUT("/api/users/{id}", handler::updateUser)       // Обновление
            .DELETE("/api/users/{id}", handler::deleteUser)    // Удаление
            .build();
    }
}
```

**RouterFunction components:**
- **Request predicate** — условия для **matching** запроса (GET, `POST`, path pattern)
- **Handler function** — функция обработки запроса
- **ServerResponse** — реактивный **HTTP response**

#### Advanced routing
```java
// Расширенная маршрутизация с условиями и фильтрами
@Configuration
public class AdvancedRoutes {

    @Bean
    public RouterFunction<ServerResponse> apiRoutes(UserHandler userHandler, OrderHandler orderHandler) {
        return RouterFunctions.route()
            // Path variables — переменные пути
            .GET("/api/users/{id}", userHandler::getUserById)
            .GET("/api/users/{userId}/orders", orderHandler::getUserOrders)

            // Query parameters — параметры запроса
            .GET("/api/users", request -> {
                Optional<String> status = request.queryParam("status");
                if (status.isPresent()) {
                    return userHandler.getUsersByStatus(request);
                } else {
                    return userHandler.getAllUsers(request);
                }
            })

            // Content type matching — проверка Content-Type
            .POST("/api/users", RequestPredicates.contentType(MediaType.APPLICATION_JSON), userHandler::createUser)

            // Header matching — проверка заголовка
            .GET("/api/admin/users", RequestPredicates.header("X-API-Key", "admin-key"), userHandler::getAllUsersAdmin)

            // Nested routes — вложенные маршруты
            .path("/api/v2", builder -> builder
                .GET("/users", userHandler::getUsersV2)
                .POST("/users", userHandler::createUserV2)
            )

            // Filter chains — цепочка фильтров для логирования
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
// Обработчик для функциональных эндпоинтов (без аннотаций)
@Component
public class UserHandler {

    private final UserService userService;
    private final Validator validator;

    public UserHandler(UserService userService, Validator validator) {
        this.userService = userService;
        this.validator = validator;
    }

    // Получение всех пользователей
    public Mono<ServerResponse> getAllUsers(ServerRequest request) {
        Flux<User> users = userService.getAllUsers();

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(users, User.class);
    }

    // Получение пользователя по ID
    public Mono<ServerResponse> getUserById(ServerRequest request) {
        Long userId = Long.valueOf(request.pathVariable("id"));  // Извлечение ID из пути

        return userService.getUserById(userId)
            .flatMap(user -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());  // 404 если не найден
    }

    // Создание пользователя с валидацией
    public Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(CreateUserRequest.class)
            .doOnNext(this::validateRequest)  // Валидация запроса
            .flatMap(createRequest -> userService.createUser(createRequest))
            .flatMap(user -> ServerResponse.created(
                    URI.create("/api/users/" + user.getId()))  // 201 Created с Location
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user))
            .onErrorResume(ConstraintViolationException.class, ex ->
                ServerResponse.badRequest()  // 400 при ошибке валидации
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new ErrorResponse("VALIDATION_ERROR", ex.getMessage())));
    }

    // Обновление пользователя
    public Mono<ServerResponse> updateUser(ServerRequest request) {
        Long userId = Long.valueOf(request.pathVariable("id"));

        return request.bodyToMono(UpdateUserRequest.class)
            .flatMap(updateRequest -> userService.updateUser(userId, updateRequest))
            .flatMap(user -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    // Удаление пользователя
    public Mono<ServerResponse> deleteUser(ServerRequest request) {
        Long userId = Long.valueOf(request.pathVariable("id"));

        return userService.deleteUser(userId)
            .then(ServerResponse.noContent().build())  // 204 No Content
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    // Приватный метод валидации
    private void validateRequest(CreateUserRequest request) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
```

**Handler function patterns:**
- **Request extraction** — извлечение данных из **ServerRequest**
- **Business logic** — вызов **service layer**
- **Response building** — создание **ServerResponse**
- **Error handling** — обработка ошибок и **edge cases**
- **Content negotiation** — правильные **media types** и **status codes**

## Аннотационные контроллеры

### @RestController

#### Basic controller
```java
// Базовый REST контроллер с реактивными типами
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users — возвращает поток пользователей
    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET /api/users/{id} — один пользователь (Mono)
    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // POST /api/users — создание пользователя
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)  // HTTP 201
    public Mono<User> createUser(@RequestBody @Valid CreateUserRequest request) {
        return userService.createUser(request);
    }

    // PUT /api/users/{id} — обновление пользователя
    @PutMapping("/{id}")
    public Mono<User> updateUser(@PathVariable Long id,
                                @RequestBody @Valid UpdateUserRequest request) {
        return userService.updateUser(id, request);
    }

    // DELETE /api/users/{id} — удаление пользователя
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)  // HTTP 204
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
```

**Controller annotations:**
- **@RestController** — комбинация @**Controller** и @**ResponseBody**
- **@RequestMapping** — базовый путь для всех методов
- **`@GetMapping`, `@PostMapping`, etc.** — **HTTP** методы с путями
- **@PathVariable** — извлечение из **URL path**
- **@RequestBody** — десериализация **request body**
- **@ResponseStatus** — **HTTP status code** для **response**

### Advanced controller features

#### Request/response handling
```java
// Продвинутые возможности работы с запросами и ответами
@RestController
@RequestMapping("/api/advanced")
public class AdvancedController {

    // Стриминг данных — элементы отправляются по мере генерации
    @GetMapping("/stream")
    public Flux<String> streamData() {
        return Flux.interval(Duration.ofSeconds(1))  // Каждую секунду
            .map(i -> "Data item " + i)
            .take(10);  // 10 элементов за 10 секунд
    }

    // Server-Sent Events (SSE) для push-уведомлений
    @GetMapping("/events")
    public Flux<ServerSentEvent<String>> serverSentEvents() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> ServerSentEvent.builder("Event " + i)
                .id(String.valueOf(i))     // ID события
                .event("message")           // Тип события
                .build());
    }

    // Загрузка файла с метаданными (multipart)
    @PostMapping("/upload")
    public Mono<ResponseEntity<String>> uploadFile(
            @RequestPart("file") FilePart filePart,
            @RequestPart("metadata") Mono<UploadMetadata> metadataMono) {

        return metadataMono
            .flatMap(metadata -> {
                // Обработка файла и метаданных
                return filePart.transferTo(Path.of("/uploads/" + filePart.filename()))
                    .then(Mono.just("File uploaded successfully"));
            })
            .map(result -> ResponseEntity.ok(result))
            .onErrorResume(e -> Mono.just(ResponseEntity.badRequest()
                .body("Upload failed: " + e.getMessage())));
    }

    // Скачивание файла с использованием DataBuffer
    @GetMapping("/download/{filename}")
    public Mono<Void> downloadFile(@PathVariable String filename, ServerHttpResponse response) {
        Path filePath = Path.of("/files/" + filename);

        if (!Files.exists(filePath)) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            return response.writeWith(Mono.empty());
        }

        // Установка заголовков для скачивания
        response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
        response.getHeaders().setContentDisposition(
            ContentDisposition.attachment().filename(filename).build());

        // Чтение файла чанками по 4KB
        DataBufferFactory bufferFactory = response.bufferFactory();
        Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(filePath, bufferFactory, 4096);

        return response.writeWith(dataBufferFlux);
    }
}
```

#### Validation и error handling
```java
// Контроллер с Bean Validation (JSR-380)
@RestController
@RequestMapping("/api/validated")
@Validated  // Активация валидации для параметров методов
public class ValidatedController {

    // Валидация @RequestBody через @Valid
    @PostMapping("/users")
    public Mono<ResponseEntity<User>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request)
            .map(user -> ResponseEntity.created(
                    URI.create("/api/users/" + user.getId()))
                .body(user));
    }

    // Валидация @PathVariable и @RequestBody
    @PutMapping("/users/{id}")
    public Mono<ResponseEntity<User>> updateUser(
            @PathVariable @Min(1) Long id,  // ID должен быть >= 1
            @Valid @RequestBody UpdateUserRequest request) {

        return userService.updateUser(id, request)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());  // 404 если не найден
    }

    // Валидация параметров запроса с ограничениями
    @GetMapping("/users/search")
    public Flux<User> searchUsers(
            @RequestParam @Size(min = 2, max = 50) String query,  // Длина 2-50 символов
            @RequestParam(defaultValue = "0") @Min(0) int page,    // page >= 0
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {  // size 1-100

        return userService.searchUsers(query, PageRequest.of(page, size));
    }
}
```

#### Custom response types
```java
// Контроллер с кастомными типами ответов
@RestController
@RequestMapping("/api/responses")
public class CustomResponseController {

    // Обёртка успех/ошибка для единообразного API ответа
    @GetMapping("/wrapped")
    public Mono<ApiResponse<User>> getUserWithWrapper(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(user -> ApiResponse.success(user))           // Успешный ответ
            .defaultIfEmpty(ApiResponse.error("User not found"));  // Ошибка
    }

    // Пагинированный ответ
    @GetMapping("/paged")
    public Mono<PageResponse<User>> getUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return userService.getUsersPaged(PageRequest.of(page, size))
            .map(pageResult -> new PageResponse<>(
                pageResult.getContent(),        // Данные страницы
                pageResult.getNumber(),         // Номер страницы
                pageResult.getSize(),           // Размер страницы
                pageResult.getTotalElements(),  // Всего элементов
                pageResult.getTotalPages()      // Всего страниц
            ));
    }

    // Обработчик исключений на уровне контроллера
    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFound(UserNotFoundException ex) {
        return Mono.just(ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("USER_NOT_FOUND", ex.getMessage())));
    }
}

// Классы-обёртки для ответов API
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String error;

    // Фабричные методы для создания ответов
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
// Конфигурация WebClient — реактивного HTTP клиента
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
            .baseUrl("http://api.example.com")  // Базовый URL для всех запросов
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("X-API-Key", "my-api-key")  // API ключ по умолчанию
            .clientConnector(createReactorClientHttpConnector())  // Netty коннектор
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(2 * 1024 * 1024))  // Максимум 2MB в памяти
            .filter(logRequest())    // Фильтр логирования запросов
            .filter(logResponse())   // Фильтр логирования ответов
            .build();
    }

    // Создание Reactor HTTP коннектора с таймаутами
    private ReactorClientHttpConnector createReactorClientHttpConnector() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)  // Таймаут подключения 10с
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(20))   // Таймаут чтения 20с
                .addHandlerLast(new WriteTimeoutHandler(20)));  // Таймаут записи 20с

        return new ReactorClientHttpConnector(httpClient);
    }

    // Фильтр для логирования исходящих запросов
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            System.out.println("Request: " + clientRequest.method() + " " + clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    // Фильтр для логирования входящих ответов
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            System.out.println("Response: " + clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
```

**WebClient configuration parameters:**
- **Base URL** — базовый **URL** для всех запросов
- **Default headers** — заголовки, добавляемые ко всем запросам
- **Client connector** — **HTTP** клиент (Netty по умолчанию)
- **Codecs** — сериализация/десериализация
- **Filters** — перехватчики запросов/ответов

### HTTP methods

#### GET requests
```java
// Сервис для GET запросов через WebClient
@Service
public class UserWebClientService {

    private final WebClient webClient;

    public UserWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Простой GET запрос с path variable
    public Mono<User> getUserById(Long userId) {
        return webClient.get()
            .uri("/users/{id}", userId)  // Path variable подстановка
            .retrieve()
            .bodyToMono(User.class);  // Десериализация в Mono<User>
    }

    // GET запрос, возвращающий поток (Flux)
    public Flux<User> getAllUsers() {
        return webClient.get()
            .uri("/users")
            .retrieve()
            .bodyToFlux(User.class);  // Десериализация в Flux<User>
    }

    // GET запрос с query параметрами
    public Mono<User> getUserWithQueryParams(String name, Integer age) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/users/search")
                .queryParam("name", name)    // ?name=...
                .queryParam("age", age)       // &age=...
                .build())
            .retrieve()
            .bodyToMono(User.class);
    }

    // Расширенная обработка ответа с доступом к статусу и заголовкам
    public Mono<ResponseEntity<User>> getUserWithFullResponse(Long userId) {
        return webClient.get()
            .uri("/users/{id}", userId)
            .exchangeToMono(clientResponse -> {
                if (clientResponse.statusCode().is2xxSuccessful()) {
                    return clientResponse.bodyToMono(User.class)
                        .map(user -> ResponseEntity.ok(user));
                } else if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                    return Mono.just(ResponseEntity.notFound().build());  // 404
                } else {
                    return clientResponse.createException()
                        .flatMap(Mono::error);  // Пробросить ошибку
                }
            });
    }
}
```

#### POST/PUT requests
```java
// Сервис для мутирующих запросов (POST, PUT, DELETE)
@Service
public class UserMutationService {

    private final WebClient webClient;

    public UserMutationService(WebClient webClient) {
        this.webClient = webClient;
    }

    // POST запрос для создания пользователя
    public Mono<User> createUser(CreateUserRequest request) {
        return webClient.post()
            .uri("/users")
            .bodyValue(request)  // Сериализация тела запроса
            .retrieve()
            .bodyToMono(User.class);
    }

    // PUT запрос для обновления пользователя
    public Mono<User> updateUser(Long userId, UpdateUserRequest request) {
        return webClient.put()
            .uri("/users/{id}", userId)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(User.class);
    }

    // DELETE запрос для удаления пользователя
    public Mono<Void> deleteUser(Long userId) {
        return webClient.delete()
            .uri("/users/{id}", userId)
            .retrieve()
            .bodyToMono(Void.class);  // Пустой ответ
    }

    // POST с явным указанием Content-Type и BodyInserters
    public Mono<User> createUserWithStreaming(CreateUserRequest request) {
        return webClient.post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(User.class);
    }

    // Batch создание из реактивного потока запросов
    public Flux<User> createUsersBatch(Flux<CreateUserRequest> requests) {
        return webClient.post()
            .uri("/users/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .body(requests, CreateUserRequest.class)  // Стриминг запросов
            .retrieve()
            .bodyToFlux(User.class);
    }
}
```

### Advanced WebClient features

#### Error handling
```java
// Устойчивый к ошибкам сервис с retry, timeout, fallback
@Service
public class ResilientWebClientService {

    private final WebClient webClient;

    public ResilientWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Запрос с автоматическим retry при ошибках
    public Mono<User> getUserWithRetry(Long userId) {
        return webClient.get()
            .uri("/users/{id}", userId)
            .retrieve()
            .bodyToMono(User.class)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))  // 3 попытки с экспоненциальным backoff
                .filter(throwable -> throwable instanceof WebClientException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                    new ExternalServiceException("External service unavailable after retries")))
            .timeout(Duration.ofSeconds(30))  // Общий таймаут 30 секунд
            .onErrorResume(TimeoutException.class, ex ->
                Mono.error(new ExternalServiceTimeoutException("Request timed out")))
            .onErrorResume(WebClientResponseException.class, ex -> {
                if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    return Mono.empty();  // 404 — пустой результат
                } else {
                    return Mono.error(new ExternalServiceException(
                        "External service error: " + ex.getMessage()));
                }
            });
    }

    // Запрос с fallback на кэш или значение по умолчанию
    public Mono<User> getUserWithFallback(Long userId) {
        return webClient.get()
            .uri("/users/{id}", userId)
            .retrieve()
            .bodyToMono(User.class)
            .onErrorResume(throwable -> {
                // При ошибке пробуем кэш или возвращаем дефолтное значение
                return getUserFromCache(userId)
                    .switchIfEmpty(Mono.just(createDefaultUser(userId)));
            });
    }

    // Интеграция с Resilience4j Circuit Breaker
    public Mono<User> getUserWithCircuitBreaker(Long userId) {
        // Создание Circuit Breaker с настройками по умолчанию
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("userService");

        return Mono.fromCallable(() ->
            circuitBreaker.decorateCallable(() ->
                webClient.get()
                    .uri("/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block()  // Блокировка для Circuit Breaker (не идеально)
            ).call()
        );
    }
}
```

#### File upload/download
```java
// Сервис для работы с файлами через WebClient
@Service
public class FileWebClientService {

    private final WebClient webClient;

    public FileWebClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    // Загрузка файла на сервер (multipart/form-data)
    public Mono<Void> uploadFile(Path filePath, String uploadUrl) {
        String filename = filePath.getFileName().toString();

        return webClient.post()
            .uri(uploadUrl)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(
                "file", filePath.toFile()))  // Multipart field с файлом
            .retrieve()
            .bodyToMono(Void.class);
    }

    // Скачивание файла с сервера
    public Mono<Path> downloadFile(String downloadUrl, Path destination) {
        return webClient.get()
            .uri(downloadUrl)
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .exchangeToMono(clientResponse -> {
                if (clientResponse.statusCode().is2xxSuccessful()) {
                    return clientResponse.bodyToMono(DataBuffer.class)
                        .map(dataBuffer -> {
                            try {
                                // Запись данных в файл
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

    // Стриминг файла чанками (для больших файлов)
    public Flux<DataBuffer> streamFile(String fileUrl) {
        return webClient.get()
            .uri(fileUrl)
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .retrieve()
            .bodyToFlux(DataBuffer.class);  // Поток DataBuffer чанков
    }
}
```

## Обработка ошибок

### Reactive exception handling

#### Global error handler
```java
// Глобальный обработчик ошибок через WebExceptionHandler
@Configuration
public class ErrorHandlingConfig {

    @Bean
    public WebExceptionHandler webExceptionHandler() {
        return (ServerWebExchange exchange, Throwable ex) -> {
            ServerHttpResponse response = exchange.getResponse();

            // Обработка ошибки валидации — 400 Bad Request
            if (ex instanceof ValidationException) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.writeWith(Mono.fromCallable(() -> {
                    DataBuffer buffer = response.bufferFactory().wrap(
                        "{\"error\":\"VALIDATION_ERROR\",\"message\":\"" + ex.getMessage() + "\"}"
                            .getBytes(StandardCharsets.UTF_8));
                    return buffer;
                }));
            }

            // Обработка ошибки "не найдено" — 404 Not Found
            if (ex instanceof NotFoundException) {
                response.setStatusCode(HttpStatus.NOT_FOUND);
                return response.writeWith(Mono.empty());
            }

            // Ответ по умолчанию для всех остальных ошибок — 500
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
// Контроллер с различными паттернами обработки ошибок
@RestController
@RequestMapping("/api/error-handling")
public class ErrorHandlingController {

    private final ExternalServiceClient externalClient;

    public ErrorHandlingController(ExternalServiceClient externalClient) {
        this.externalClient = externalClient;
    }

    // Устойчивый endpoint с обработкой разных типов ошибок
    @GetMapping("/resilient/{id}")
    public Mono<ResponseEntity<User>> getUserResilient(@PathVariable Long id) {
        return externalClient.getUser(id)
            .map(user -> ResponseEntity.ok(user))
            .onErrorResume(ExternalServiceException.class, ex ->
                Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(null)))  // 503 при недоступности внешнего сервиса
            .onErrorResume(TimeoutException.class, ex ->
                Mono.just(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body(null)))  // 504 при таймауте
            .defaultIfEmpty(ResponseEntity.notFound().build());  // 404 если нет данных
    }

    // Endpoint с fallback на кэш или значение по умолчанию
    @GetMapping("/fallback/{id}")
    public Mono<User> getUserWithFallback(@PathVariable Long id) {
        return externalClient.getUser(id)
            .onErrorResume(throwable -> {
                // При любой ошибке пробуем кэш или дефолт
                return getUserFromCache(id)
                    .switchIfEmpty(Mono.just(createDefaultUser(id)));
            });
    }

    // Endpoint с автоматическим retry при ошибках
    @GetMapping("/retry/{id}")
    public Mono<User> getUserWithRetry(@PathVariable Long id) {
        return externalClient.getUser(id)
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))  // 3 попытки
                .filter(throwable -> isRetryableError(throwable)))  // Только для retryable ошибок
            .timeout(Duration.ofSeconds(10));  // Общий таймаут
    }

    // Endpoint с Circuit Breaker (Resilience4j)
    @GetMapping("/circuit-breaker/{id}")
    public Mono<User> getUserWithCircuitBreaker(@PathVariable Long id) {
        return externalClient.getUser(id)
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    // Проверка, можно ли повторить запрос при данной ошибке
    private boolean isRetryableError(Throwable throwable) {
        return throwable instanceof WebClientException ||
               throwable instanceof TimeoutException;
    }

    private Mono<User> getUserFromCache(Long id) {
        // Реализация получения из кэша
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
**Backpressure** — это механизм контроля скорости производства данных потребителем. В реактивных системах **producer** может генерировать данные быстрее, чем **consumer** может их обработать. **Backpressure** позволяет **consumer**'у сигнализировать **producer**'у о снижении скорости.

```java
// Без backpressure — потребитель перегружен
Flux.interval(Duration.ofMillis(1))  // Генерирует 1000 элементов/секунду
    .map(i -> intensiveComputation(i))  // Медленная обработка
    .subscribe(System.out::println);  // Потребитель не успевает

// С backpressure — контролируемый поток
Flux.interval(Duration.ofMillis(1))
    .onBackpressureBuffer(100)  // Буфер до 100 элементов
    .map(i -> intensiveComputation(i))
    .subscribe(System.out::println);  // Потребитель контролирует поток
```

#### Backpressure strategies

**Buffer** — буферизация элементов:**
```java
// Стратегия Buffer — накопление элементов в буфере
Flux<String> buffered = Flux.create(sink -> {
    for (int i = 0; i < 1000; i++) {
        sink.next("item" + i);  // Генерация 1000 элементов
    }
    sink.complete();
})
.onBackpressureBuffer(100)  // Буфер до 100 элементов, остальные ждут
.subscribe(item -> {
    // Медленная обработка
    Thread.sleep(10);
    System.out.println(item);
});
```

**Drop** — отбрасывание элементов:**
```java
// Стратегия Drop — отбрасывание лишних элементов
Flux<String> dropped = Flux.interval(Duration.ofMillis(1))
    .map(i -> "item" + i)
    .onBackpressureDrop(item -> {
        System.out.println("Dropped: " + item);  // Логирование отброшенных
    })
    .subscribe(item -> {
        // Медленная обработка (10мс на элемент)
        Thread.sleep(10);
        System.out.println("Processed: " + item);
    });
```

**Latest** — сохранение только последнего элемента:**
```java
// Стратегия Latest — сохраняем только последний элемент
Flux<String> latest = Flux.interval(Duration.ofMillis(1))
    .map(i -> "item" + i)
    .onBackpressureLatest()  // Хранит только последний непрочитанный элемент
    .subscribe(item -> {
        // Медленная обработка — пропускаем промежуточные элементы
        Thread.sleep(10);
        System.out.println("Processed: " + item);
    });
```

**Error** — ошибка при перегрузке:**
```java
// Стратегия Error — выброс ошибки при переполнении
Flux<String> error = Flux.interval(Duration.ofMillis(1))
    .map(i -> "item" + i)
    .onBackpressureError()  // IllegalStateException при переполнении
    .subscribe(
        item -> System.out.println("Processed: " + item),
        error -> System.err.println("Backpressure error: " + error)  // Обработка ошибки
    );
```

### Implementing backpressure

#### Custom producer with backpressure
```java
// Кастомный producer с поддержкой backpressure
public class BackpressureAwareProducer {

    public Flux<String> produceWithBackpressure() {
        return Flux.create(sink -> {
            // Обработчик запроса на n элементов от subscriber'а
            sink.onRequest(requested -> {
                // Генерируем только запрошенное количество элементов
                for (long i = 0; i < requested; i++) {
                    String item = generateItem();
                    sink.next(item);
                }
            });

            // Обработчик отмены подписки
            sink.onCancel(() -> {
                cleanup();  // Очистка ресурсов
            });

            // Обработчик завершения
            sink.onDispose(() -> {
                cleanup();  // Очистка ресурсов
            });
        }, FluxSink.OverflowStrategy.ERROR);  // Стратегия — ошибка при переполнении
    }

    private String generateItem() {
        // Генерация элемента данных
        return "data-" + System.nanoTime();
    }

    private void cleanup() {
        // Очистка ресурсов producer'а
        System.out.println("Cleaning up producer");
    }
}
```

#### Consumer-controlled backpressure
```java
// Consumer с ручным контролем backpressure
public class BackpressureControlledConsumer {

    private final AtomicLong requested = new AtomicLong(0);  // Счётчик запрошенных
    private final AtomicLong processed = new AtomicLong(0);  // Счётчик обработанных

    public void consumeWithBackpressure(Flux<String> dataStream) {
        dataStream
            .doOnRequest(request -> {
                requested.addAndGet(request);
                System.out.println("Requested: " + request + ", Total requested: " + requested.get());
            })
            .subscribe(new BaseSubscriber<String>() {

                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    // Запрашиваем начальную партию элементов
                    request(10);
                }

                @Override
                protected void hookOnNext(String value) {
                    processed.incrementAndGet();

                    // Медленная обработка (имитация)
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Processed: " + value + " (" + processed.get() + ")");

                    // Запрашиваем ещё элементы когда готовы
                    if (processed.get() % 5 == 0) {
                        request(5);  // Запрос следующих 5 элементов
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
// Тестирование реактивного сервиса с использованием StepVerifier
@SpringBootTest
public class ReactiveServiceTest {

    @Autowired
    private UserService userService;

    // Тест успешного получения пользователя
    @Test
    void testGetUserById() {
        // Given — подготовка данных
        Long userId = 1L;
        User expectedUser = new User(userId, "test@example.com", "Test User");

        // Мокируем репозиторий
        given(userRepository.findById(userId)).willReturn(Mono.just(expectedUser));

        // When — выполнение
        Mono<User> result = userService.getUserById(userId);

        // Then — проверка с StepVerifier
        StepVerifier.create(result)
            .expectNext(expectedUser)  // Ожидаем конкретного пользователя
            .verifyComplete();         // Проверяем завершение без ошибок
    }

    // Тест сценария "не найдено"
    @Test
    void testGetUserByIdNotFound() {
        // Given
        Long userId = 999L;
        given(userRepository.findById(userId)).willReturn(Mono.empty());  // Пустой Mono

        // When
        Mono<User> result = userService.getUserById(userId);

        // Then
        StepVerifier.create(result)
            .verifyComplete();  // Пустой Mono завершается без элементов
    }

    // Тест получения списка пользователей (Flux)
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

        // Then — проверка порядка элементов
        StepVerifier.create(result)
            .expectNext(expectedUsers.get(0))
            .expectNext(expectedUsers.get(1))
            .verifyComplete();
    }

    // Тест ошибки валидации
    @Test
    void testCreateUserValidationError() {
        // Given — невалидный запрос
        CreateUserRequest invalidRequest = new CreateUserRequest("", "");

        given(userRepository.save(any(User.class)))
            .willReturn(Mono.error(new ValidationException("Invalid user data")));

        // When
        Mono<User> result = userService.createUser(invalidRequest);

        // Then — ожидаем ошибку определённого типа
        StepVerifier.create(result)
            .expectError(ValidationException.class)
            .verify();
    }
}
```

#### Testing with virtual time
```java
// Тестирование time-based операций с виртуальным временем
@SpringBootTest
public class TimeBasedTest {

    @Autowired
    private EventService eventService;

    // Тест с ускорением времени (не ждать реальные 5 минут)
    @Test
    void testDelayedEventProcessing() {
        // Используем виртуальное время для ускорения тестов
        StepVerifier.withVirtualTime(() -> eventService.processDelayedEvent())
            .thenAwait(Duration.ofMinutes(5))  // Перемотка на 5 минут вперёд
            .expectNext("Event processed")
            .verifyComplete();
    }

    // Тест интервального потока
    @Test
    void testIntervalStream() {
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1))
                .map(i -> "Event " + i)
                .take(3))  // Берём только 3 элемента
            .thenAwait(Duration.ofSeconds(3))  // Перемотка на 3 секунды
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
// Интеграционные тесты WebFlux с WebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebFluxIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;  // Реактивный HTTP клиент для тестов

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll().block();  // Очистка БД перед каждым тестом
    }

    // Тест GET списка пользователей
    @Test
    void testGetAllUsers() {
        // Given — подготовка данных
        User user1 = new User(null, "user1@example.com", "User 1");
        User user2 = new User(null, "user2@example.com", "User 2");

        userRepository.saveAll(Flux.just(user1, user2)).blockLast();

        // When & Then — запрос и проверка
        webTestClient.get()
            .uri("/api/users")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()                               // 200 OK
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBodyList(User.class)
            .hasSize(2);  // Ожидаем 2 пользователя
    }

    // Тест POST создания пользователя
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
            .expectStatus().isCreated()           // 201 Created
            .expectHeader().exists("Location")    // Заголовок Location
            .expectBody(User.class)
            .value(user -> {
                assertThat(user.getEmail()).isEqualTo("newuser@example.com");
                assertThat(user.getName()).isEqualTo("New User");
                assertThat(user.getId()).isNotNull();  // ID должен быть сгенерирован
            });
    }

    // Тест 404 Not Found
    @Test
    void testGetUserNotFound() {
        webTestClient.get()
            .uri("/api/users/999")  // Несуществующий ID
            .exchange()
            .expectStatus().isNotFound();
    }

    // Тест ошибки валидации
    @Test
    void testValidationError() {
        // Given — невалидный запрос
        CreateUserRequest invalidRequest = new CreateUserRequest("", "");

        // When & Then
        webTestClient.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(invalidRequest)
            .exchange()
            .expectStatus().isBadRequest();  // 400 Bad Request
    }

    // Тест стримингового endpoint (SSE)
    @Test
    void testStreamingEndpoint() {
        webTestClient.get()
            .uri("/api/stream")
            .accept(MediaType.TEXT_EVENT_STREAM)  // SSE формат
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
            .returnResult(String.class)
            .getResponseBody()
            .take(3)  // Берём первые 3 элемента
            .as(StepVerifier::create)
            .expectNextMatches(item -> item.startsWith("Data item "))
            .expectNextMatches(item -> item.startsWith("Data item "))
            .expectNextMatches(item -> item.startsWith("Data item "))
            .thenCancel()  // Отменяем подписку
            .verify();
    }
}
```

#### Testing WebClient
```java
// Тестирование WebClient с моками
@SpringBootTest
public class WebClientTest {

    @Autowired
    private ExternalApiClient externalApiClient;

    @MockBean
    private WebClient webClient;  // Мокируем WebClient

    // Тест успешного запроса
    @Test
    void testGetUserSuccess() {
        // Given — настройка моков для цепочки вызовов WebClient
        Long userId = 1L;
        User expectedUser = new User(userId, "test@example.com", "Test User");

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        // Настраиваем цепочку моков
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

    // Тест ошибки 404 от внешнего API
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
                "Not Found", 404, "Not Found", null, null, null)));  // Симуляция 404

        // When
        Mono<User> result = externalApiClient.getUser(userId);

        // Then — ожидаем WebClientResponseException
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
// Несовмещённые операторы (каждый создаёт промежуточный объект)
Flux<Integer> nonFused = Flux.range(1, 100)
    .map(i -> i * 2)        // Создаёт промежуточный Flux
    .filter(i -> i > 50)    // Создаёт ещё один промежуточный Flux
    .map(i -> i / 2);       // Создаёт ещё один промежуточный Flux

// Совмещённые операторы (оптимизировано Reactor)
Flux<Integer> fused = Flux.range(1, 100)
    .map(i -> i * 2)
    .filter(i -> i > 50)
    .map(i -> i / 2)
    .share();  // Включает multicasting для лучшей производительности
```

#### Parallel processing
```java
// Параллельная обработка на нескольких потоках
Flux.range(1, 100)
    .parallel(4)  // Разделение на 4 параллельных "рельса"
    .runOn(Schedulers.parallel())  // Выполнение на parallel scheduler
    .map(this::cpuIntensiveOperation)  // CPU-интенсивная операция
    .sequential()  // Слияние обратно в один поток
    .subscribe(result -> processResult(result));
```

#### Batching operations
```java
// Пакетные операции с базой данных
Flux<User> users = userRepository.findAll();

users.buffer(50)  // Группировка в пакеты по 50 элементов
    .flatMap(userBatch -> {
        // Обработка пакета
        return userRepository.saveAll(userBatch)
            .then(Mono.just(userBatch.size()));  // Возвращаем размер пакета
    }, 2)  // Максимум 2 пакета параллельно
    .reduce(0, Integer::sum)  // Суммируем все обработанные
    .subscribe(totalProcessed ->
        System.out.println("Processed " + totalProcessed + " users"));
```

### Connection pooling

#### Reactor Netty configuration
```java
// Оптимизированная конфигурация WebClient с пулом соединений
@Configuration
public class OptimizedWebClientConfig {

    @Bean
    public WebClient optimizedWebClient() {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)  // Таймаут подключения
            .option(ChannelOption.SO_KEEPALIVE, true)            // Keep-alive включен
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)        // Idle таймаут 5 минут
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)        // Интервал проверки 60с
            .option(EpollChannelOption.TCP_KEEPCNT, 8)           // 8 попыток keep-alive
            .doOnConnected(connection -> connection
                .addHandlerLast(new ReadTimeoutHandler(10))      // Таймаут чтения 10с
                .addHandlerLast(new WriteTimeoutHandler(10)))    // Таймаут записи 10с
            .metrics(true, () -> new MicrometerHttpClientMetricsRecorder(
                MeterRegistry meterRegistry, "http.client"));    // Метрики

        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
            .clientConnector(connector)
            .codecs(configurer -> configurer
                .defaultCodecs()
                .maxInMemorySize(2 * 1024 * 1024))  // Максимум 2MB в памяти
            .build();
    }
}
```

### Memory optimization

#### Object pooling
```java
// Оптимизация использования памяти
@Configuration
public class MemoryOptimizationConfig {

    @Bean
    public Schedulers schedulerWithPool() {
        // Кастомный scheduler с пулом потоков
        // 10 потоков, 100 задач в очереди, TTL 60 секунд
        return Schedulers.newBoundedElastic(10, 100, "bounded-elastic", 60);
    }

    @Bean
    public WebClient memoryEfficientWebClient() {
        return WebClient.builder()
            .codecs(configurer -> {
                // Ограничение использования памяти для кодеков
                configurer.defaultCodecs().maxInMemorySize(512 * 1024); // 512KB

                // Отключение логирования деталей для экономии памяти
                configurer.defaultCodecs().enableLoggingRequestDetails(false);
            })
            .build();
    }
}
```

#### Streaming responses
```java
// Контроллер для стриминга данных без загрузки в память целиком
@RestController
public class StreamingController {

    // Стриминг большого датасета с контролем скорости
    @GetMapping(value = "/large-dataset", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<User> streamLargeDataset() {
        return userRepository.findAll()
            .delayElements(Duration.ofMillis(10))  // Контроль скорости стриминга
            .doOnNext(user -> {
                // Логирование прогресса каждые 1000 записей
                if (user.getId() % 1000 == 0) {
                    System.out.println("Streamed " + user.getId() + " users");
                }
            });
    }

    // Server-Sent Events для push-уведомлений клиенту
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))  // Событие каждую секунду
            .map(sequence -> ServerSentEvent.builder("Event " + sequence)
                .id(String.valueOf(sequence))
                .event("message")
                .build())
            .doOnCancel(() -> System.out.println("Client disconnected"));  // При отключении
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
// Конфигурация метрик с Micrometer
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new CompositeMeterRegistry();  // Комбинированный реестр метрик
    }

    // Кастомный провайдер тегов для HTTP запросов
    @Bean
    public WebFluxTagsProvider webFluxTagsProvider() {
        return new DefaultWebFluxTagsProvider() {
            @Override
            public Iterable<Tag> httpRequestTags(ServerWebExchange exchange,
                                               Throwable exception) {
                // Добавляем кастомные теги к стандартным
                return Tags.concat(
                    super.httpRequestTags(exchange, exception),
                    Tags.of("custom.tag", getCustomTag(exchange))
                );
            }
        };
    }

    // Фильтр для сбора метрик HTTP запросов
    @Bean
    public MetricsWebFilter metricsWebFilter(MeterRegistry meterRegistry) {
        return new MetricsWebFilter(meterRegistry, webFluxTagsProvider(),
            "http.server.requests", Duration.ofMillis(10));
    }
}
```

#### Custom metrics
```java
// Сервис с кастомными метриками для мониторинга
@Service
public class ReactiveMetricsService {

    private final MeterRegistry meterRegistry;

    public ReactiveMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Получение пользователей со счётчиком обработанных
    public Flux<User> getUsersWithMetrics() {
        return userRepository.findAll()
            .doOnNext(user -> {
                // Счётчик обработанных пользователей
                Counter.builder("users.processed")
                    .tag("status", "active")
                    .register(meterRegistry)
                    .increment();
            })
            .doOnError(error -> {
                // Счётчик ошибок с типом ошибки
                Counter.builder("users.errors")
                    .tag("type", error.getClass().getSimpleName())
                    .register(meterRegistry)
                    .increment();
            });
    }

    // Создание пользователя с измерением времени
    public Mono<User> createUserWithMetrics(CreateUserRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);  // Старт таймера

        return userService.createUser(request)
            .doOnSuccess(user -> {
                // Запись успешного времени выполнения
                sample.stop(Timer.builder("user.creation.time")
                    .tag("result", "success")
                    .register(meterRegistry));
            })
            .doOnError(error -> {
                // Запись времени при ошибке
                sample.stop(Timer.builder("user.creation.time")
                    .tag("result", "error")
                    .register(meterRegistry));
            });
    }
}
```

## Лучшие практики

### Application design

#### 1. Choose appropriate return types
```java
// Выбор правильного возвращаемого типа
@RestController
public class ReturnTypeController {

    // Mono для одного результата (0 или 1 элемент)
    @GetMapping("/user/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    // Flux для множества результатов (0-N элементов)
    @GetMapping("/users")
    public Flux<User> getUsers() {
        return userService.findAll();
    }

    // Mono<Void> для ответов без тела (204 No Content)
    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteById(id);
    }

    // Mono<ResponseEntity> для сложных ответов с контролем статуса и заголовков
    @PostMapping("/user")
    public Mono<ResponseEntity<User>> createUser(@RequestBody CreateUserRequest request) {
        return userService.create(request)
            .map(user -> ResponseEntity.created(
                    URI.create("/user/" + user.getId()))  // 201 с Location header
                .body(user));
    }
}
```

#### 2. Handle blocking operations properly
```java
// Правильная обработка блокирующих операций
@Service
public class BlockingOperationService {

    // ПЛОХО: Блокирующая операция в реактивном pipeline
    public Mono<User> getUserBlocking(Long id) {
        return Mono.fromCallable(() -> {
            // Это блокирует event loop поток!
            return blockingDatabaseCall(id);
        });
    }

    // ХОРОШО: Используем subscribeOn для изоляции блокирующих операций
    public Mono<User> getUserNonBlocking(Long id) {
        return Mono.fromCallable(() -> blockingDatabaseCall(id))
            .subscribeOn(Schedulers.boundedElastic());  // Перенос на elastic scheduler
    }

    // ЛУЧШЕ: Использовать реактивный клиент БД
    public Mono<User> getUserReactive(Long id) {
        return reactiveDatabaseClient.findById(id);  // Полностью реактивный
    }
}
```

#### 3. Error handling patterns
```java
// Паттерны обработки ошибок в реактивном коде
@Service
public class ErrorHandlingService {

    // Многоуровневая обработка ошибок с fallback
    public Mono<User> getUserWithFallback(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .onErrorResume(UserNotFoundException.class, ex -> {
                // Логируем и возвращаем дефолтного пользователя
                log.warn("User {} not found, returning default", id);
                return Mono.just(createDefaultUser());
            })
            .onErrorResume(DatabaseException.class, ex -> {
                // Retry при ошибках БД
                return userRepository.findById(id)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)));
            })
            .onErrorResume(Exception.class, ex -> {
                // Последний fallback — кэш
                return userCache.getUser(id);
            });
    }

    // Изоляция ошибок — продолжение обработки при ошибке элемента
    public Flux<User> getUsersWithErrorContainment() {
        return userRepository.findAll()
            .onErrorContinue((error, item) -> {
                // Логируем ошибку, но продолжаем обработку
                log.error("Error processing user {}: {}", item, error.getMessage());
            })
            .doOnError(error -> {
                // Обработка ошибки на уровне потока
                log.error("Stream error: {}", error.getMessage());
            });
    }
}
```

### Performance optimization

#### 4. Connection and thread management
```java
// Управление соединениями и потоками для производительности
@Configuration
public class PerformanceConfig {

    @Bean
    public ReactorResourceFactory reactorResourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);  // Изоляция ресурсов на сервер
        factory.setConnectionProvider(connectionProvider());
        factory.setLoopResources(loopResources());
        return factory;
    }

    // Пул соединений для WebClient
    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("webflux-connections")
            .maxConnections(500)                    // Максимум соединений
            .pendingAcquireMaxCount(1000)          // Максимум ожидающих
            .pendingAcquireTimeout(Duration.ofSeconds(30))  // Таймаут получения
            .maxIdleTime(Duration.ofMinutes(5))    // Макс. время простоя
            .maxLifeTime(Duration.ofHours(1))      // Макс. время жизни
            .build();
    }

    // Event loop пул (Netty)
    @Bean
    public LoopResources loopResources() {
        // Количество ядер CPU * 2 event loop'а
        int eventLoopCount = Runtime.getRuntime().availableProcessors() * 2;
        return LoopResources.create("webflux-loops", 1, eventLoopCount, true);
    }

    @Bean
    public WebClient optimizedWebClient(ReactorResourceFactory resourceFactory) {
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create(resourceFactory.getConnectionProvider())
                    .runOn(resourceFactory.getLoopResources())))  // Общий пул ресурсов
            .build();
    }
}
```

#### 5. Caching strategies
```java
// Конфигурация кэширования
@Configuration
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("users", "orders");  // Кэши users и orders
    }
}

// Сервис с кэшированием пользователей
@Service
public class CachedUserService {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public CachedUserService(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    // Получение пользователя с проверкой кэша
    public Mono<User> getUserById(Long id) {
        Cache cache = cacheManager.getCache("users");
        Cache.ValueWrapper cached = cache.get(id);

        if (cached != null) {
            return Mono.just((User) cached.get());  // Из кэша
        }

        return userRepository.findById(id)
            .doOnNext(user -> cache.put(id, user));  // Сохраняем в кэш при загрузке
    }

    // Создание пользователя с инвалидацией кэша
    public Mono<User> createUser(CreateUserRequest request) {
        return userRepository.save(new User(request))
            .doOnNext(user -> {
                // Инвалидация связанных кэшей
                cacheManager.getCache("users").evictIfPresent(user.getId());
                // Можно также публиковать событие инвалидации
            });
    }
}
```

### Testing strategies

#### 6. Test data management
```java
// Управление тестовыми данными в реактивных тестах
@SpringBootTest
public class TestDataManagementTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestDataInitializer dataInitializer;

    @BeforeEach
    void setupTestData() {
        // Очистка и инициализация тестовых данных реактивно
        userRepository.deleteAll()
            .then(dataInitializer.initializeUsers())
            .then(dataInitializer.initializeOrders())
            .block();  // Блокируем для настройки теста
    }

    @Test
    void testBusinessLogic() {
        // Тест с известными тестовыми данными
        Flux<User> users = userRepository.findAll();

        StepVerifier.create(users)
            .expectNextCount(5)  // Ожидаем 5 тестовых пользователей
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

        // Проверка состояния БД после создания
        StepVerifier.create(userRepository.count())
            .expectNext(6L)  // 5 начальных + 1 созданный
            .verifyComplete();
    }
}
```

#### 7. Integration test patterns
```java
// Комплексные интеграционные тесты
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ComprehensiveIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    // Тест полного жизненного цикла пользователя (CRUD)
    @Test
    void testCompleteUserWorkflow() {
        // 1. Создание пользователя
        CreateUserRequest createRequest = new CreateUserRequest("workflow@example.com", "Workflow User");

        webTestClient.post()
            .uri("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(createRequest)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(User.class)
            .value(user -> assertNotNull(user.getId()));

        // 2. Получение списка пользователей
        webTestClient.get()
            .uri("/api/users")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(User.class)
            .value(users -> assertTrue(users.size() >= 1));

        // 3. Обновление пользователя
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

        // 4. Удаление пользователя
        webTestClient.delete()
            .uri("/api/users/{id}", createdUser.getId())
            .exchange()
            .expectStatus().isNoContent();

        // 5. Проверка удаления (404)
        webTestClient.get()
            .uri("/api/users/{id}", createdUser.getId())
            .exchange()
            .expectStatus().isNotFound();
    }

    // Тест параллельной обработки запросов
    @Test
    void testConcurrentRequests() {
        // Подготовка 10 параллельных запросов
        List<Mono<ClientResponse>> requests = IntStream.range(0, 10)
            .mapToObj(i -> webTestClient.get()
                .uri("/api/users")
                .exchange()
                .returnResult(Void.class))
            .collect(Collectors.toList());

        // Выполнение всех запросов параллельно
        Flux<ClientResponse> responses = Flux.fromIterable(requests)
            .flatMap(mono -> mono, 5);  // Максимум 5 параллельно

        StepVerifier.create(responses)
            .expectNextCount(10)
            .verifyComplete();
    }
}
```

### Operational practices

#### 8. Health checks and monitoring
```java
// Реактивный health indicator для Spring Actuator
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
        // Параллельная проверка всех зависимостей
        return Mono.zip(
                checkDatabaseConnectivity(),       // Доступность БД
                checkExternalServiceConnectivity(), // Доступность внешнего сервиса
                checkMemoryUsage()                  // Использование памяти
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

    // Проверка подключения к БД
    private Mono<Boolean> checkDatabaseConnectivity() {
        return userRepository.count()
            .map(count -> true)
            .onErrorReturn(false)
            .timeout(Duration.ofSeconds(5))
            .onErrorReturn(false);
    }

    // Проверка доступности внешнего сервиса
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

    // Расчёт использования памяти (процент)
    private Mono<Double> checkMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return Mono.just(((double) usedMemory / totalMemory) * 100);
    }
}
```

## Решение проблем

### Распространенные проблемы

#### Thread blocking issues
```text
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
```text
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
```text
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

**Spring WebFlux** — это мощный фреймворк для создания реактивных веб-приложений, который полностью меняет подход к разработке высокопроизводительных систем. Использование **Project Reactor** и **reactive programming** позволяет создавать приложения, способные эффективно обрабатывать тысячи одновременных подключений с минимальным использованием ресурсов.

### Ключевые возможности:

1. **Reactive Programming** — асинхронная обработка данных с **backpressure**
2. **Non-blocking I/O** — эффективное использование потоков
3. **Functional Endpoints** — декларативная маршрутизация
4. **Annotation Controllers** — знакомый **Spring MVC** стиль
5. **WebClient** — реактивный **HTTP** клиент
6. **Backpressure Support** — контроль потока данных
7. **Server-`Sent` Events** — **real-time** коммуникация
8. **Streaming** — обработка больших объемов данных

### Архитектурные преимущества:

#### Performance:
- **High Concurrency** — тысячи одновременных подключений
- **Resource Efficiency** — минимальное потребление памяти/**CPU**
- **Elastic Scaling** — автоматическое масштабирование
- **Low Latency** — быстрая обработка запросов

#### Scalability:
- **Non-blocking I/O** — эффективное использование потоков
- **Backpressure** — предотвращение перегрузки
- **Reactive Streams** — стандартизированная обработка данных
- **Event-driven** — асинхронная обработка событий

### Когда использовать WebFlux:

**High-throughput applications** — приложения с высокой нагрузкой
**Microservices** — асинхронная коммуникация между сервисами
**Real-time features** — **WebSocket**, **Server-Sent Events**
**Streaming data** — обработка потоков данных
**I/O bound applications** — интенсивные I/O операции
**Reactive databases** — **MongoDB**, **Cassandra**, **Redis reactive clients**
**Cloud-native** — масштабируемые облачные приложения

### Когда НЕ использовать:

**Simple CRUD** — используйте **Spring MVC** для простых задач
**Blocking operations** — весь код должен быть реактивным
**Small applications** — **overhead** не оправдан
**Legacy blocking APIs** — сложная интеграция
**`Simple REST` APIs** — **Spring MVC** проще для базовых задач
**File uploads** — **Spring MVC** лучше для больших файлов
**Synchronous clients** — требуют адаптации

### Production considerations:

1. **Thread pool tuning** — правильная конфигурация **schedulers**
2. **Backpressure configuration** — управление потоками данных
3. **Error handling** — **comprehensive** обработка ошибок
4. **Monitoring** — метрики и алертинг
5. **Performance optimization** — **connection pooling**, **caching**
6. **Testing** — **unit** и **integration** тесты реактивного кода
7. **Deployment** — **container** и **orchestration** конфигурации

### Best practices summary:

1. **Choose reactive databases** — используйте **reactive clients**
2. **Handle blocking operations** — **offload** с **subscribeOn**
3. **Implement backpressure** — контролируйте потоки данных
4. **Error handling** — **comprehensive** стратегии обработки ошибок
5. **Testing** — **StepVerifier** для тестирования **reactive streams**
6. **Performance monitoring** — метрики и алертинг
7. **Configuration tuning** — оптимизация **thread pools** и **connections**
8. **Backpressure strategies** — **buffer**, **drop**, **latest**, **error**

**Spring WebFlux** представляет собой будущее веб-разработки в экосистеме **Spring**. Он позволяет создавать высокопроизводительные, масштабируемые приложения, которые могут эффективно работать под высокой нагрузкой. Правильное использование реактивного программирования открывает новые возможности для создания современных **distributed** систем.

**Далее: `Spring Integration` (enterprise integration patterns)**
