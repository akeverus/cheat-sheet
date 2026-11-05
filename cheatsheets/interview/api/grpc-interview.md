---
title: "Вопросы на собеседовании: gRPC"
description: "Практичные вопросы и ответы по gRPC для Java-разработчика: Protocol Buffers, типы RPC, каналы и стабы, интерсепторы, обработка ошибок, streaming, безопасность, gRPC в Spring Boot, сравнение с REST."
tags:
  - interview
  - api
  - grpc-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "gRPC"
  - "gRPC interview"
  - "gRPC собеседование"
prerequisites:
  - "[[grpc]]"
next: []
updated: "2026-11-05"
---
# Вопросы на собеседовании: `gRPC`

Практичные вопросы и ответы по `gRPC`: определение сервисов через `Protocol Buffers`, типы вызовов, streaming, интерсепторы, обработка ошибок, безопасность, интеграция со `Spring Boot` и сравнение с `REST`.

**`gRPC`** (gRPC Remote Procedure Call) — высокопроизводительный фреймворк удалённых вызовов процедур, разработанный Google. Использует `HTTP/2` для транспорта и `Protocol Buffers` для сериализации, что обеспечивает компактность данных и низкую задержку. Широко применяется в микросервисных архитектурах для межсервисного взаимодействия.

## Полезные ссылки

### Официальная документация

- [gRPC — Official Site](https://grpc.io/) — официальный сайт проекта
- [Core concepts, architecture and lifecycle](https://grpc.io/docs/what-is-grpc/core-concepts/) — ключевые концепции gRPC
- [gRPC Java Quick Start](https://grpc.io/docs/languages/java/quickstart/) — быстрый старт на Java
- [Protocol Buffers Language Guide (proto3)](https://protobuf.dev/programming-guides/proto3/) — спецификация proto3
- [gRPC Status Codes](https://grpc.io/docs/guides/status-codes/) — коды ошибок gRPC
- [Introduction to gRPC | Baeldung](https://www.baeldung.com/grpc-introduction) — введение в gRPC на Java
- [Introduction to gRPC with Spring Boot | Baeldung](https://www.baeldung.com/spring-boot-grpc) — gRPC + Spring Boot
- [Streaming with gRPC in Java | Baeldung](https://www.baeldung.com/java-grpc-streaming) — streaming в Java
- [Error Handling in gRPC | Baeldung](https://www.baeldung.com/grpcs-error-handling) — обработка ошибок

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы gRPC**
- [Q1. Что такое gRPC и какие проблемы он решает?](#q1)
- [Q2. Что такое Protocol Buffers и зачем они нужны?](#q2)
- [Q3. Как определить сервис в `.proto` файле?](#q3)
- [Q4. Как происходит генерация кода из `.proto` файлов в Java?](#q4)
- [Q5. Что такое `Channel` и `Stub` в gRPC?](#q5)

**Типы RPC вызовов**
- [Q6. Какие типы RPC поддерживает gRPC?](#q6)
- [Q7. Как реализовать Server Streaming RPC?](#q7)
- [Q8. Как реализовать Client Streaming RPC?](#q8)
- [Q9. Как работает Bidirectional Streaming?](#q9)

**Архитектура и транспорт**
- [Q10. Почему gRPC использует HTTP/2 и какие преимущества это даёт?](#q10)
- [Q11. Как работает мультиплексирование в HTTP/2 и как gRPC его использует?](#q11)
- [Q12. Какова архитектура gRPC приложения?](#q12)

**Interceptors и Metadata**
- [Q13. Что такое Interceptors в gRPC и зачем они нужны?](#q13)
- [Q14. Что такое Metadata в gRPC?](#q14)

**Обработка ошибок**
- [Q15. Как устроена обработка ошибок в gRPC?](#q15)
- [Q16. Какие стандартные Status Codes существуют в gRPC?](#q16)

**Deadlines, Timeouts и надёжность**
- [Q17. Как работают Deadlines и Timeouts в gRPC?](#q17)
- [Q18. Как настроить Retry Policy в gRPC?](#q18)

**Балансировка нагрузки и Service Discovery**
- [Q19. Какие модели балансировки нагрузки поддерживает gRPC?](#q19)
- [Q20. Что такое Name Resolver в gRPC?](#q20)

**Безопасность**
- [Q21. Как обеспечить безопасность gRPC-соединений?](#q21)
- [Q22. Как реализовать аутентификацию в gRPC?](#q22)

**gRPC и Spring Boot**
- [Q23. Как интегрировать gRPC со Spring Boot?](#q23)
- [Q24. Как тестировать gRPC-сервисы?](#q24)

**Health Checking и Reflection**
- [Q25. Что такое gRPC Health Checking Protocol?](#q25)
- [Q26. Что такое Server Reflection и зачем она нужна?](#q26)

**gRPC-Web и совместимость**
- [Q27. Что такое gRPC-Web и зачем он нужен?](#q27)

**Сравнение и выбор**
- [Q28. Чем gRPC отличается от REST?](#q28)
- [Q29. Как обеспечить обратную совместимость в Protocol Buffers?](#q29)
- [Q30. Когда стоит использовать gRPC, а когда REST?](#q30)

**Продвинутые темы**
- [Q31. Как правильно управлять Channel и Stub в долгоживущих приложениях?](#q31)
- [Q32. Какие лучшие практики дизайна proto3 схем нужно знать?](#q32)
- [Q33. Как организовать observability для gRPC-сервисов?](#q33)

**Экосистема и интеграции**
- [Q34. Что такое gRPC-Web и как он работает в браузерах?](#q34)
- [Q35. gRPC vs REST vs GraphQL: сравнение и выбор протокола?](#q35)
- [Q36. Правила backwards compatibility в Protocol Buffers: как эволюционировать схему?](#q36)
- [Q37. Как настроить gRPC load balancing в Kubernetes?](#q37)
- [Q38. Как реализовать retry policy в gRPC через ServiceConfig?](#q38)
- [Q39. Как интегрировать gRPC с Spring Boot через grpc-spring-boot-starter?](#q39)
- [Q40. Что такое transcoding gRPC ↔ REST через google.api.http аннотации?](#q40)

---

<a id="q1"></a>

## Q1. Что такое gRPC и какие проблемы он решает?

`gRPC` (gRPC Remote Procedure Call) — высокопроизводительный open-source фреймворк удалённых вызовов процедур от Google, переданный в `CNCF`. Идея проста: вы описываете сервис в `.proto`-файле, а вызов удалённого метода выглядит для кода как обычный локальный вызов функции — сериализацию, транспорт и десериализацию gRPC берёт на себя.

В основе три кита: транспорт `HTTP/2`, бинарная сериализация `Protocol Buffers` и контракт-first подход через `.proto`. Именно из этой комбинации вытекают все его свойства — компактность, низкая задержка и строгая типизация.

**Ключевые характеристики:**

- Использует `HTTP/2` как транспортный протокол
- Сериализация через `Protocol Buffers` (protobuf) — компактный бинарный формат
- Строгая типизация через `.proto` файлы (контракт-first подход)
- Поддержка streaming (server, client, bidirectional)
- Автоматическая генерация клиентского и серверного кода на 10+ языках
- Встроенная поддержка deadline/timeout, cancellation, interceptors

**Какие проблемы решает:**

| Проблема | Решение в gRPC |
|----------|----------------|
| Большой overhead JSON/XML | Бинарная сериализация protobuf (до 10x компактнее) |
| Отсутствие строгого контракта в REST | `.proto` файл как единый источник правды |
| Нет streaming в HTTP/1.1 | Четыре типа RPC включая bidirectional streaming |
| Ручное написание клиентов | Автогенерация стабов для любого языка |
| Head-of-line blocking | Мультиплексирование `HTTP/2` |

**На собеседовании важно подчеркнуть:** gRPC создан под межсервисное взаимодействие внутри микросервисной архитектуры — там, где важны производительность и строгий контракт между командами. А вот для публичных API чаще берут REST: он нативно работает в браузере, а gRPC требует прокси (gRPC-Web) и хуже поддаётся отладке «руками».

<a id="q2"></a>

## Q2. Что такое Protocol Buffers и зачем они нужны?

`Protocol Buffers` (protobuf) — механизм сериализации структурированных данных от Google, не привязанный к языку или платформе. В gRPC он играет сразу две роли: служит языком описания контракта (IDL — Interface Definition Language) и форматом, в котором данные летят по сети.

**Зачем они нужны.** protobuf решает две задачи разом. Во-первых, даёт единый строгий контракт: схема в `.proto` описывает структуру сообщений и из неё генерируется код для любого языка, поэтому клиент и сервер физически не могут разойтись в форматах. Во-вторых, сериализует данные в компактный бинарный вид — без имён полей, без кавычек и скобок, как в JSON. За счёт этого payload меньше, а парсинг быстрее.

**Пример `.proto` файла:**

```protobuf
syntax = "proto3";

package com.example.grpc;

option java_multiple_files = true;
option java_package = "com.example.grpc.user";

message User {
  int64 id = 1;
  string name = 2;
  string email = 3;
  UserRole role = 4;
  repeated string tags = 5;        // список
  optional string bio = 6;         // необязательное поле

  enum UserRole {
    UNKNOWN = 0;
    ADMIN = 1;
    USER = 2;
  }
}

message GetUserRequest {
  int64 id = 1;
}

message GetUserResponse {
  User user = 1;
}
```

**Ключевые особенности proto3:**

- Каждое поле имеет **уникальный номер** (field number), и именно он, а не имя поля, записывается в бинарный поток. Это ключевой факт: благодаря ему поле можно безболезненно переименовать, а имена не занимают места в payload.
- Поля с номерами 1–15 кодируют tag одним байтом, 16+ — двумя. Поэтому самые частые поля держат в диапазоне 1–15.
- Значения по умолчанию заданы неявно: `0` для чисел, пустая строка, `false`, пустой список. В proto3 такие поля просто не сериализуются (экономия места), а на приёме подставляется дефолт.
- Поддержка `oneof` (взаимоисключающие поля), `map`, `Any` и well-known types вроде `Timestamp`.
- Бинарный формат компактнее JSON на 60–80% и сериализуется/десериализуется в 3–10 раз быстрее.

**Типы данных protobuf:**

| Protobuf тип | Java тип | Описание |
|-------------|----------|----------|
| `int32` / `int64` | `int` / `long` | Целые числа |
| `float` / `double` | `float` / `double` | Числа с плавающей точкой |
| `bool` | `boolean` | Логическое значение |
| `string` | `String` | Строка (UTF-8) |
| `bytes` | `ByteString` | Байтовый массив |
| `repeated` | `List<T>` | Список элементов |
| `map<K,V>` | `Map<K,V>` | Словарь |

<a id="q3"></a>

## Q3. Как определить сервис в `.proto` файле?

Сервис описывается блоком `service` и содержит набор RPC-методов, которые сможет вызывать клиент. Каждый метод задаёт тип запроса и тип ответа, а ключевое слово `stream` превращает любую из сторон в поток. Этот блок — и есть контракт: из него generator создаёт и серверную «заготовку», и клиентские стабы.

```protobuf
syntax = "proto3";

package com.example.grpc;

option java_multiple_files = true;
option java_package = "com.example.grpc.user";

service UserService {
  // Unary RPC — один запрос, один ответ
  rpc GetUser (GetUserRequest) returns (GetUserResponse);

  // Server Streaming — один запрос, поток ответов
  rpc ListUsers (ListUsersRequest) returns (stream UserResponse);

  // Client Streaming — поток запросов, один ответ
  rpc UploadUsers (stream UploadUserRequest) returns (UploadUsersResponse);

  // Bidirectional Streaming — поток в обе стороны
  rpc ChatUsers (stream ChatMessage) returns (stream ChatMessage);
}
```

**Правила определения сервисов:**

- Каждый RPC-метод принимает ровно **один** message-тип и возвращает **один** message-тип
- Ключевое слово `stream` определяет потоковую передачу
- Один `.proto` файл может содержать несколько сервисов
- Имена сервисов и методов используются при генерации Java-классов

**Из этого `.proto` файла генерируются:**
- `UserServiceGrpc` — базовый класс с серверным стабом (`UserServiceImplBase`)
- `UserServiceGrpc.UserServiceBlockingStub` — синхронный клиентский стаб
- `UserServiceGrpc.UserServiceStub` — асинхронный клиентский стаб
- `UserServiceGrpc.UserServiceFutureStub` — стаб на основе `ListenableFuture`

<a id="q4"></a>

## Q4. Как происходит генерация кода из `.proto` файлов в Java?

Код генерирует компилятор `protoc`: сам он умеет создавать только message-классы, а за RPC-стабы отвечает плагин `protoc-gen-grpc-java`. Запускать `protoc` руками не нужно — в Gradle это вешается на сборку через `protobuf-gradle-plugin`, и при каждом `build` код из `.proto` пересоздаётся автоматически.

```groovy
plugins {
    id 'com.google.protobuf' version '0.9.4'
}

dependencies {
    implementation 'io.grpc:grpc-netty-shaded:1.83.1'
    implementation 'io.grpc:grpc-protobuf:1.83.1'
    implementation 'io.grpc:grpc-stub:1.83.1'
    compileOnly 'org.apache.tomcat:annotations-api:6.0.53'
}

protobuf {
    protoc {
        artifact = 'com.google.protobuf:protoc:4.27.0'
    }
    plugins {
        grpc {
            artifact = 'io.grpc:protoc-gen-grpc-java:1.83.1'
        }
    }
    generateProtoTasks {
        all()*.plugins {
            grpc {}
        }
    }
}
```

**Что генерируется:**

1. **Message-классы** — immutable Java-объекты для каждого `message` в `.proto` (с builder-паттерном)
2. **Service-стабы** — абстрактные классы для реализации сервера и клиентские стабы
3. **Descriptor-ы** — метаданные для reflection и сериализации

```java
// Сгенерированный builder для message
User user = User.newBuilder()
    .setId(1L)
    .setName("Иван")
    .setEmail("ivan@example.com")
    .setRole(User.UserRole.ADMIN)
    .addTags("java")
    .addTags("grpc")
    .build();
```

<a id="q5"></a>

## Q5. Что такое `Channel` и `Stub` в gRPC?

Это два слоя клиента, разделённые по ответственности: `Channel` отвечает за «как доставить байты», `Stub` — за «какой метод вызвать».

**`Channel`** — абстракция соединения с сервером. За этим объектом скрыт пул `HTTP/2`-подключений, балансировка нагрузки и автоматическое переподключение. Он тяжёлый, потокобезопасный и долгоживущий: создаётся один раз и переиспользуется.

**`Stub`** — сгенерированный из `.proto` клиент, через который вы вызываете RPC-методы как обычные функции. Стаб лёгкий, привязан к `Channel` и делегирует ему всю транспортную работу. На одном канале можно держать сколько угодно стабов.

```java
// Создание канала
ManagedChannel channel = ManagedChannelBuilder
    .forAddress("localhost", 9090)
    .usePlaintext()  // без TLS (только для разработки!)
    .build();

// Blocking stub — синхронные вызовы
UserServiceGrpc.UserServiceBlockingStub blockingStub =
    UserServiceGrpc.newBlockingStub(channel);

// Async stub — асинхронные вызовы с StreamObserver
UserServiceGrpc.UserServiceStub asyncStub =
    UserServiceGrpc.newStub(channel);

// Future stub — возвращает ListenableFuture (только для unary)
UserServiceGrpc.UserServiceFutureStub futureStub =
    UserServiceGrpc.newFutureStub(channel);

// Вызов через blocking stub
GetUserResponse response = blockingStub.getUser(
    GetUserRequest.newBuilder().setId(1L).build()
);

// Не забываем закрыть канал при завершении
channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
```

**Типы стабов:**

| Тип стаба | Unary | Server Streaming | Client Streaming | Bidi |
|-----------|-------|-----------------|-----------------|------|
| `BlockingStub` | да | да (Iterator) | нет | нет |
| `Stub` (async) | да | да | да | да |
| `FutureStub` | да | нет | нет | нет |

**Важно:** `Channel` потокобезопасен и должен переиспользоваться. Создание нового канала на каждый запрос — антипаттерн.

<a id="q6"></a>

## Q6. Какие типы RPC поддерживает gRPC?

gRPC поддерживает четыре типа вызовов — они отличаются тем, сколько сообщений (одно или поток) передаёт каждая из сторон:

```mermaid
graph TD
    subgraph "Unary RPC"
        C1[Клиент] -->|1 запрос| S1[Сервер]
        S1 -->|1 ответ| C1
    end
    subgraph "Server Streaming"
        C2[Клиент] -->|1 запрос| S2[Сервер]
        S2 -->|поток ответов| C2
    end
    subgraph "Client Streaming"
        C3[Клиент] -->|поток запросов| S3[Сервер]
        S3 -->|1 ответ| C3
    end
    subgraph "Bidirectional Streaming"
        C4[Клиент] <-->|поток| S4[Сервер]
    end
```

**1. Unary RPC** — классический запрос-ответ:

```protobuf
rpc GetUser (GetUserRequest) returns (GetUserResponse);
```

**2. Server Streaming RPC** — сервер отправляет поток сообщений в ответ на один запрос:

```protobuf
rpc ListUsers (ListUsersRequest) returns (stream UserResponse);
```
Используется для: получения больших списков, подписки на события, скачивания файлов.

**3. Client Streaming RPC** — клиент отправляет поток, сервер возвращает один ответ:

```protobuf
rpc UploadUsers (stream UploadUserRequest) returns (UploadUsersResponse);
```
Используется для: загрузки файлов, batch-операций, агрегации данных.

**4. Bidirectional Streaming RPC** — оба направления работают независимо:

```protobuf
rpc Chat (stream ChatMessage) returns (stream ChatMessage);
```
Используется для: чатов, real-time обновлений, двусторонней синхронизации.

**На собеседовании:** часто просят назвать все 4 типа и привести по примеру на каждый. Подчеркните, что любой streaming возможен именно благодаря `HTTP/2`: один stream держит долгий двунаправленный поток фреймов, тогда как в `HTTP/1.1` это потребовало бы хаков вроде long-polling или WebSocket.

<a id="q7"></a>

## Q7. Как реализовать Server Streaming RPC?

В server streaming сервер на один запрос клиента отправляет поток ответов. На уровне кода это значит, что метод не возвращает значение, а получает `StreamObserver` и вызывает `onNext()` столько раз, сколько нужно сообщений, завершая поток через `onCompleted()`. Клиент же читает ответы как `Iterator`.

**Определение в `.proto`:**

```protobuf
service StockService {
  rpc WatchStock (StockRequest) returns (stream StockPrice);
}

message StockRequest {
  string symbol = 1;
}

message StockPrice {
  string symbol = 1;
  double price = 2;
  int64 timestamp = 3;
}
```

**Серверная реализация:**

```java
public class StockServiceImpl extends StockServiceGrpc.StockServiceImplBase {

    @Override
    public void watchStock(StockRequest request,
                           StreamObserver<StockPrice> responseObserver) {
        String symbol = request.getSymbol();

        // Эмуляция потока обновлений цен
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            StockPrice price = StockPrice.newBuilder()
                .setSymbol(symbol)
                .setPrice(getLatestPrice(symbol))
                .setTimestamp(System.currentTimeMillis())
                .build();

            responseObserver.onNext(price);    // отправить сообщение
        }, 0, 1, TimeUnit.SECONDS);

        // При отмене клиентом — остановить executor
        Context.current().addListener(context -> {
            executor.shutdown();
            responseObserver.onCompleted();    // завершить поток
        }, MoreExecutors.directExecutor());
    }
}
```

**Нюанс:** пример учебный, и в продакшен его переносить as-is нельзя — у него два слабых места. Во-первых, `onNext()` зовётся по таймеру без проверки готовности потока: в grpc-java `onNext()` не блокируется, и если клиент читает медленнее, чем сервер пишет, сообщения копятся в очереди gRPC без ограничения. Во-вторых, `onCompleted()` здесь вызывается в листенере отмены, то есть уже **после** того, как клиент отменил вызов: завершать отменённый stream бессмысленно (ответ клиенту всё равно не уйдёт), а попытка `onNext()` по отменённому вызову бросит `StatusRuntimeException`. Правильная реакция на отмену — просто освободить ресурсы (остановить executor), а саму отмену удобнее ловить через `setOnCancelHandler()`.

**Backpressure (application-level flow control).** `HTTP/2` даёт транспортный flow control, но он лишь притормаживает отправку байтов в сокет — сообщения, уже переданные в `onNext()`, буферизуются в памяти процесса, и при быстром продюсере и медленном потребителе это прямой путь к OOM. Рабочий паттерн в grpc-java: привести `responseObserver` к `ServerCallStreamObserver`, писать только пока `isReady()` возвращает `true`, а продолжение отправки вешать на колбэк `setOnReadyHandler()` — он сработает, когда транспорт снова готов принимать данные. Для входящих потоков (client/bidirectional streaming, см. Q8–Q9) есть зеркальный механизм: `disableAutoRequest()` отключает автоматический запрос следующих сообщений, и сервер сам вызывает `request(n)`, когда обработал текущие, — иначе входящие сообщения точно так же накапливаются быстрее, чем потребляются.

**Клиент (blocking):**

```java
Iterator<StockPrice> prices = blockingStub.watchStock(
    StockRequest.newBuilder().setSymbol("GOOG").build()
);

while (prices.hasNext()) {
    StockPrice price = prices.next();
    System.out.printf("%s: $%.2f%n", price.getSymbol(), price.getPrice());
}
```

<a id="q8"></a>

## Q8. Как реализовать Client Streaming RPC?

Здесь всё зеркально server streaming: поток сообщений шлёт клиент, а сервер отдаёт один ответ. Поэтому серверный метод не принимает запрос напрямую, а **возвращает** `StreamObserver` для входящих сообщений — его `onNext()` вызывается на каждый чанк от клиента, а `onCompleted()` сигналит, что данные кончились, и пора отдать итоговый ответ. Типичный сценарий — заливка файла по частям или агрегация batch-данных.

**Определение в `.proto`:**

```protobuf
service FileService {
  rpc UploadFile (stream FileChunk) returns (UploadStatus);
}
```

**Серверная реализация:**

```java
@Override
public StreamObserver<FileChunk> uploadFile(
        StreamObserver<UploadStatus> responseObserver) {

    return new StreamObserver<FileChunk>() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int chunkCount = 0;

        @Override
        public void onNext(FileChunk chunk) {
            buffer.write(chunk.getData().toByteArray(), 0,
                         chunk.getData().size());
            chunkCount++;
        }

        @Override
        public void onError(Throwable t) {
            logger.error("Upload failed", t);
        }

        @Override
        public void onCompleted() {
            // Все чанки получены — сохраняем файл
            saveFile(buffer.toByteArray());
            responseObserver.onNext(UploadStatus.newBuilder()
                .setSuccess(true)
                .setMessage("Uploaded " + chunkCount + " chunks")
                .build());
            responseObserver.onCompleted();
        }
    };
}
```

**Клиент (async):**

```java
StreamObserver<UploadStatus> responseObserver = new StreamObserver<>() {
    @Override
    public void onNext(UploadStatus status) {
        System.out.println("Upload: " + status.getMessage());
    }
    @Override
    public void onError(Throwable t) { /* обработка ошибки */ }
    @Override
    public void onCompleted() { /* завершение */ }
};

StreamObserver<FileChunk> requestObserver =
    asyncStub.uploadFile(responseObserver);

// Отправляем файл по частям
byte[] fileData = Files.readAllBytes(Path.of("large-file.bin"));
int chunkSize = 64 * 1024; // 64 KB
for (int i = 0; i < fileData.length; i += chunkSize) {
    int end = Math.min(i + chunkSize, fileData.length);
    requestObserver.onNext(FileChunk.newBuilder()
        .setData(ByteString.copyFrom(fileData, i, end - i))
        .build());
}
requestObserver.onCompleted();
```

<a id="q9"></a>

## Q9. Как работает Bidirectional Streaming?

В bidirectional streaming обе стороны обмениваются потоками сообщений **независимо** друг от друга, по одному `HTTP/2`-stream. Ключевое слово «независимо»: это не пинг-понг «запрос-ответ-запрос», а два параллельных потока. Сервер не обязан отвечать ровно одним сообщением на каждое клиентское — он может прислать пять ответов на один запрос или вовсе слать данные по своему таймеру. Порядок чтения и записи произвольный.

```java
// Серверная реализация чата
@Override
public StreamObserver<ChatMessage> chat(
        StreamObserver<ChatMessage> responseObserver) {

    return new StreamObserver<ChatMessage>() {
        @Override
        public void onNext(ChatMessage message) {
            // Обработать входящее сообщение и отправить ответ
            ChatMessage reply = ChatMessage.newBuilder()
                .setSender("server")
                .setText("Echo: " + message.getText())
                .setTimestamp(System.currentTimeMillis())
                .build();
            responseObserver.onNext(reply);
        }

        @Override
        public void onError(Throwable t) {
            logger.error("Chat stream error", t);
        }

        @Override
        public void onCompleted() {
            responseObserver.onCompleted();
        }
    };
}
```

**Ключевые моменты:**
- Потоки клиента и сервера **независимы** — сервер может отправить 5 сообщений на 1 клиентское
- Каждая сторона завершает свой поток вызовом `onCompleted()`
- Идеально для real-time взаимодействия: чаты, совместное редактирование, игровые сессии
- Работает поверх одного `HTTP/2` stream

<a id="q10"></a>

## Q10. Почему gRPC использует HTTP/2 и какие преимущества это даёт?

`HTTP/2` — фундамент производительности gRPC: именно его возможности дают и параллелизм, и streaming, и экономию на заголовках. Разберём по пунктам, что конкретно он приносит.

**1. Мультиплексирование:**
Несколько RPC-вызовов идут параллельно по одному TCP-соединению, каждый в своём stream. В `HTTP/1.1` для параллелизма приходилось открывать отдельные соединения — это решало проблему head-of-line blocking ценой накладных расходов на каждое подключение.

**2. Бинарный фрейминг:**
`HTTP/2` передаёт данные в бинарных фреймах, а не текстовых. Это эффективнее для парсинга и уменьшает overhead.

**3. Сжатие заголовков (HPACK):**
Заголовки сжимаются и дедуплицируются между запросами, экономя трафик.

**4. Двунаправленные stream-ы:**
Каждый `HTTP/2` stream двунаправленный: пока он открыт, обе стороны могут слать DATA-фреймы. Именно на этом построен streaming в gRPC — при server streaming сервер отправляет поток сообщений DATA-фреймами **в рамках того же stream**, который открыл клиент. Частое заблуждение: HTTP/2 Server Push (`PUSH_PROMISE`) gRPC **не использует** — это браузерный механизм проактивной доставки ресурсов, к gRPC-стримингу он отношения не имеет.

**5. Flow Control:**
Встроенное управление потоком данных на уровне stream и соединения предотвращает перегрузку.

```mermaid
graph LR
    subgraph "HTTP/1.1"
        C1[Клиент] -->|Соединение 1| R1[Запрос 1]
        C1 -->|Соединение 2| R2[Запрос 2]
        C1 -->|Соединение 3| R3[Запрос 3]
    end
    subgraph "HTTP/2"
        C2[Клиент] -->|Одно соединение| MUX[Мультиплексор]
        MUX --> S1[Stream 1]
        MUX --> S2[Stream 2]
        MUX --> S3[Stream 3]
    end
```

**Производительность gRPC vs REST:**
- Payload: на 60-80% компактнее благодаря protobuf
- Latency: до 7-10x ниже в high-throughput сценариях
- Пропускная способность: значительно выше при множественных параллельных вызовах

<a id="q11"></a>

## Q11. Как работает мультиплексирование в HTTP/2 и как gRPC его использует?

Мультиплексирование — это разделение одного TCP-соединения на множество независимых логических потоков (**stream**). Каждый stream — это самостоятельный двунаправленный канал фреймов со своим идентификатором, и фреймы разных stream-ов свободно перемежаются в одном соединении. gRPC ложится на эту модель буквально один-в-один: один RPC = один stream.

**Как gRPC использует мультиплексирование:**

- Один `ManagedChannel` = одно (или несколько) TCP-соединение
- Каждый RPC-вызов — отдельный `HTTP/2` stream
- Stream-ы не блокируют друг друга (нет head-of-line blocking на уровне HTTP)
- Клиент может делать тысячи параллельных RPC через один канал

**Конфигурация в Java:**

```java
ManagedChannel channel = ManagedChannelBuilder
    .forAddress("service.example.com", 443)
    .maxInboundMessageSize(16 * 1024 * 1024)  // 16 MB
    .keepAliveTime(30, TimeUnit.SECONDS)
    .keepAliveTimeout(5, TimeUnit.SECONDS)
    .build();
```

**Важно:** хотя head-of-line blocking отсутствует на уровне HTTP/2, он остаётся на уровне TCP. При потере пакета все stream-ы в одном соединении ждут ретрансмиссии. Это одна из мотиваций для `HTTP/3` (QUIC).

<a id="q12"></a>

## Q12. Какова архитектура gRPC приложения?

Архитектуру удобно читать как путь одного вызова: приложение зовёт метод стаба → клиентские интерсепторы добавляют cross-cutting логику → канал сериализует запрос в protobuf и шлёт по `HTTP/2` → на сервере данные десериализуются, проходят серверные интерсепторы и попадают в реализацию сервиса с бизнес-логикой. Ответ идёт тем же путём в обратную сторону.

```mermaid
graph TB
    subgraph Клиент
        APP[Приложение] --> STUB[Stub]
        STUB --> CI[Client Interceptors]
        CI --> CH[Channel]
        CH --> LB[Load Balancer]
        LB --> SER[Serializer / Protobuf]
    end

    SER -->|HTTP/2| NET((Сеть))

    NET -->|HTTP/2| DSER

    subgraph Сервер
        DSER[Deserializer / Protobuf] --> SI[Server Interceptors]
        SI --> IMPL[Service Implementation]
        IMPL --> BL[Бизнес-логика]
    end
```

**Основные компоненты:**

1. **`.proto` файл** — контракт между клиентом и сервером
2. **Сгенерированный код** — стабы, message-классы, service base
3. **`Channel`** — управление соединением на клиенте
4. **`Server`** — приём входящих RPC на сервере
5. **`Interceptors`** — cross-cutting concerns (логирование, аутентификация)
6. **`StreamObserver`** — callback-интерфейс для обработки потоковых сообщений
7. **Сериализатор** — protobuf marshalling/unmarshalling

<a id="q13"></a>

## Q13. Что такое Interceptors в gRPC и зачем они нужны?

`Interceptors` — это middleware, которое вклинивается в RPC-вызов до и после бизнес-логики, чтобы добавить сквозную функциональность (логирование, метрики, аутентификацию) в одном месте, а не дублировать её в каждом методе. Прямой аналог — `Servlet Filter` или перехватчики `Spring MVC`. Логика та же: цепочка обёрток вокруг вызова, каждая может что-то сделать с запросом/ответом и передать управление дальше через `next`.

**Два типа** — по тому, на какой стороне перехватывается вызов:

**1. `ServerInterceptor`** — перехватывает входящие вызовы на сервере:

```java
public class LoggingServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String methodName = call.getMethodDescriptor().getFullMethodName();
        long start = System.nanoTime();
        logger.info("gRPC call: {}", methodName);

        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onComplete() {
                long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
                logger.info("gRPC call {} completed in {} ms", methodName, elapsed);
                super.onComplete();
            }
        };
    }
}
```

**2. `ClientInterceptor`** — перехватывает исходящие вызовы на клиенте:

```java
public class AuthClientInterceptor implements ClientInterceptor {
    private static final Metadata.Key<String> AUTH_KEY =
        Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<>(
                next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> listener, Metadata headers) {
                headers.put(AUTH_KEY, "Bearer " + getToken());
                super.start(listener, headers);
            }
        };
    }
}
```

**Регистрация:**

```java
// Серверный interceptor
Server server = ServerBuilder.forPort(9090)
    .addService(ServerInterceptors.intercept(
        new UserServiceImpl(), new LoggingServerInterceptor()))
    .build();

// Клиентский interceptor
ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
    .intercept(new AuthClientInterceptor())
    .build();
```

**Типичные применения:** логирование, метрики, аутентификация, трассировка, валидация, rate limiting.

<a id="q14"></a>

## Q14. Что такое Metadata в gRPC?

`Metadata` — это пары ключ-значение, которые едут рядом с RPC-вызовом, но не входят в само тело сообщения. Прямой аналог — HTTP-заголовки. Смысл разделения тот же: бизнес-данные кладут в message, а служебную информацию (токен авторизации, trace-id, версию клиента) — в metadata, чтобы её можно было читать в интерсепторах, не разбирая payload.

```java
// Определение ключей metadata
Metadata.Key<String> TRACE_ID_KEY =
    Metadata.Key.of("x-trace-id", Metadata.ASCII_STRING_MARSHALLER);
Metadata.Key<byte[]> BINARY_KEY =
    Metadata.Key.of("data-bin", Metadata.BINARY_BYTE_MARSHALLER);

// Отправка metadata на клиенте (через interceptor)
Metadata headers = new Metadata();
headers.put(TRACE_ID_KEY, UUID.randomUUID().toString());

// Чтение metadata на сервере
String traceId = headers.get(TRACE_ID_KEY);
```

**Типы metadata:**

| Тип | Суффикс ключа | Маршаллер | Пример |
|-----|---------------|-----------|--------|
| Текстовый | любой | `ASCII_STRING_MARSHALLER` | `authorization`, `x-request-id` |
| Бинарный | `-bin` | `BINARY_BYTE_MARSHALLER` | `token-bin`, `payload-bin` |

**Три точки передачи:**
1. **Initial metadata** — заголовки запроса (клиент → сервер)
2. **Response headers** — заголовки ответа (сервер → клиент, перед первым сообщением)
3. **Trailing metadata / trailers** — завершающие метаданные (сервер → клиент, после последнего сообщения)

Metadata активно используется для передачи trace-id (distributed tracing), токенов аутентификации, информации о пользователе и других cross-cutting данных. Доступ к metadata осуществляется через `Interceptors`, подробнее в [вопросах по HTTP и REST](http-rest-interview.md).

<a id="q15"></a>

## Q15. Как устроена обработка ошибок в gRPC?

В gRPC ошибка — это не исключение в привычном смысле, а объект `Status`: числовой **код** из фиксированного набора плюс опциональное текстовое **описание**. Сервер не «бросает» ошибку клиенту произвольно, а завершает вызов конкретным статусом через `responseObserver.onError(...)`. На клиенте этот статус прилетает как `StatusRuntimeException`.

Отличие от HTTP важно понимать: gRPC использует **собственный** набор кодов (`NOT_FOUND`, `UNAVAILABLE`, …), а не HTTP-статусы. HTTP/2 на транспорте почти всегда отдаёт `200 OK` — реальный результат вызова лежит в gRPC-статусе, который передаётся в trailing metadata.

**Отправка ошибки на сервере:**

```java
@Override
public void getUser(GetUserRequest request,
                    StreamObserver<GetUserResponse> responseObserver) {
    User user = userRepository.findById(request.getId());

    if (user == null) {
        // Простая ошибка
        responseObserver.onError(Status.NOT_FOUND
            .withDescription("User not found: " + request.getId())
            .asRuntimeException());
        return;
    }

    responseObserver.onNext(toResponse(user));
    responseObserver.onCompleted();
}
```

**Расширенная обработка ошибок с деталями (Rich Error Model):**

Когда одного кода и строки мало (например, нужно сообщить, какие именно поля невалидны), gRPC позволяет приложить к статусу структурированные детали через `Any` — например стандартный тип `BadRequest` с перечнем нарушений по полям. Клиент сможет распаковать их и показать осмысленную ошибку.

```java
// Отправка ошибки с дополнительными деталями
com.google.rpc.Status status = com.google.rpc.Status.newBuilder()
    .setCode(Code.INVALID_ARGUMENT.getNumber())
    .setMessage("Validation failed")
    .addDetails(Any.pack(BadRequest.newBuilder()
        .addFieldViolations(FieldViolation.newBuilder()
            .setField("email")
            .setDescription("Invalid email format")
            .build())
        .build()))
    .build();

responseObserver.onError(
    StatusProto.toStatusRuntimeException(status));
```

**Обработка ошибок на клиенте:**

```java
try {
    GetUserResponse response = blockingStub.getUser(request);
} catch (StatusRuntimeException e) {
    Status status = e.getStatus();
    switch (status.getCode()) {
        case NOT_FOUND:
            logger.warn("User not found: {}", status.getDescription());
            break;
        case DEADLINE_EXCEEDED:
            logger.error("Request timed out");
            break;
        case UNAVAILABLE:
            logger.error("Service unavailable, retry later");
            break;
        default:
            logger.error("gRPC error: {} - {}",
                status.getCode(), status.getDescription());
    }
}
```

<a id="q16"></a>

## Q16. Какие стандартные Status Codes существуют в gRPC?

gRPC определяет 17 стандартных кодов в `io.grpc.Status.Code` — единый словарь ошибок для всех языков. Знать их полезно не для зубрёжки, а чтобы корректно реагировать на клиенте: одни коды стоит ретраить, другие — нет (см. Q18). Ниже — полный список с HTTP-аналогами для интуиции:

| Код | Число | Описание | HTTP-аналог |
|-----|-------|----------|-------------|
| `OK` | 0 | Успех | 200 |
| `CANCELLED` | 1 | Вызов отменён (клиентом) | 499 |
| `UNKNOWN` | 2 | Неизвестная ошибка | 500 |
| `INVALID_ARGUMENT` | 3 | Невалидный аргумент | 400 |
| `DEADLINE_EXCEEDED` | 4 | Timeout истёк | 504 |
| `NOT_FOUND` | 5 | Ресурс не найден | 404 |
| `ALREADY_EXISTS` | 6 | Ресурс уже существует | 409 |
| `PERMISSION_DENIED` | 7 | Нет прав | 403 |
| `RESOURCE_EXHAUSTED` | 8 | Ресурс исчерпан (rate limit) | 429 |
| `FAILED_PRECONDITION` | 9 | Не выполнено предусловие | 400 |
| `ABORTED` | 10 | Операция прервана (конфликт) | 409 |
| `OUT_OF_RANGE` | 11 | Выход за пределы | 400 |
| `UNIMPLEMENTED` | 12 | Метод не реализован | 501 |
| `INTERNAL` | 13 | Внутренняя ошибка сервера | 500 |
| `UNAVAILABLE` | 14 | Сервис недоступен | 503 |
| `DATA_LOSS` | 15 | Потеря данных | 500 |
| `UNAUTHENTICATED` | 16 | Не аутентифицирован | 401 |

**Частая ошибка на собеседовании:** путают `UNAUTHENTICATED` (нет токена / невалидный токен) и `PERMISSION_DENIED` (токен валиден, но прав недостаточно).

<a id="q17"></a>

## Q17. Как работают Deadlines и Timeouts в gRPC?

`Deadline` — это абсолютная точка во времени («не позже 12:00:05»), после которой RPC считается провалившимся со статусом `DEADLINE_EXCEEDED`. Главное отличие от обычного timeout — в природе значения. Timeout относителен («ждать 5 секунд») и каждый сервис в цепочке отсчитывает его заново. Deadline абсолютен, поэтому **пропагируется** по всей цепочке вызовов: если сервис A с дедлайном 5 с уже потратил 0.2 с и зовёт сервис B, тот получает не свежие 5 с, а оставшиеся 4.8 с. Это защищает от ситуации, когда суммарное время по цепочке неконтролируемо растёт.

```java
// Установка deadline на клиенте
GetUserResponse response = blockingStub
    .withDeadlineAfter(5, TimeUnit.SECONDS)
    .getUser(request);

// Альтернативно — абсолютный deadline
Deadline deadline = Deadline.after(3, TimeUnit.SECONDS);
GetUserResponse response = blockingStub
    .withDeadline(deadline)
    .getUser(request);
```

**Проверка deadline на сервере:**

```java
@Override
public void getUser(GetUserRequest request,
                    StreamObserver<GetUserResponse> responseObserver) {
    // Проверить, не истёк ли deadline до начала работы
    if (Context.current().isCancelled()) {
        responseObserver.onError(Status.CANCELLED
            .withDescription("Request already cancelled")
            .asRuntimeException());
        return;
    }

    // Долгая операция — периодически проверяем deadline
    for (int i = 0; i < steps; i++) {
        if (Context.current().getDeadline().isExpired()) {
            responseObserver.onError(Status.DEADLINE_EXCEEDED
                .withDescription("Processing timed out")
                .asRuntimeException());
            return;
        }
        processStep(i);
    }
}
```

```mermaid
sequenceDiagram
    participant Client
    participant ServiceA
    participant ServiceB

    Client->>ServiceA: RPC (deadline = 5s)
    Note over ServiceA: Осталось 4.8s
    ServiceA->>ServiceB: RPC (deadline = 4.8s, propagated)
    Note over ServiceB: Осталось 4.5s
    ServiceB-->>ServiceA: Response
    ServiceA-->>Client: Response
```

**Рекомендации:**
- Всегда задавайте deadline на клиенте — без него зависший сервер заставит вызов висеть бесконечно и держать ресурсы.
- Deadline пропагируется автоматически, но полагаться на это можно лишь внутри gRPC: на границе с не-gRPC-вызовами (БД, HTTP) дедлайн нужно конвертировать в их собственные таймауты вручную.
- На сервере перед дорогими операциями проверяйте `Context.current().isCancelled()` — нет смысла начинать работу, если клиент уже не ждёт ответа.

<a id="q18"></a>

## Q18. Как настроить Retry Policy в gRPC?

gRPC поддерживает прозрачный retry для ограниченных случаев, когда серверное приложение ещё не обработало вызов. Подсистема повторов включена по умолчанию, но **retry policy по умолчанию нет**. Политику для более широких повторов задают через ServiceConfig: число попыток, задержки и коды статуса.

```json
{"methodConfig":[{"name":[{"service":"example.UserService","method":"GetUser"}],"retryPolicy":{"maxAttempts":3,"initialBackoff":"0.1s","maxBackoff":"1s","backoffMultiplier":2,"retryableStatusCodes":["UNAVAILABLE"]}}]}
```

Перед включением повтора проверьте идемпотентность операции и риск повторного эффекта. В grpc-java `enableRetry()` существует, но включать подсистему отдельным вызовом обычно не требуется; `disableRetry()` её отключает. Для ServiceConfig и default конфигурации см. Q38.

---


<a id="q19"></a>

## Q19. Какие модели балансировки нагрузки поддерживает gRPC?

gRPC поддерживает две модели, и выбор между ними — это выбор, кто принимает решение о маршрутизации:

**1. Proxy-based (серверная):**
Между клиентом и backend стоит внешний балансировщик (Envoy, nginx, HAProxy) и распределяет запросы. Клиент знает только адрес прокси и ничего — о backend-серверах. Плюс: вся логика централизована, клиент простой. Минус: лишний сетевой хоп и единая точка, которую тоже надо масштабировать.

**2. Client-side (клиентская):**
Клиент сам получает список адресов (через NameResolver) и сам решает, куда слать каждый запрос. Лишнего хопа нет, но логика балансировки живёт в каждом клиенте. Встроенные политики:

```java
// Round Robin — запросы по очереди между серверами
ManagedChannel channel = ManagedChannelBuilder
    .forTarget("dns:///my-service.example.com")
    .defaultLoadBalancingPolicy("round_robin")
    .build();

// Pick First (по умолчанию) — первый доступный сервер
ManagedChannel channel = ManagedChannelBuilder
    .forTarget("dns:///my-service.example.com")
    .defaultLoadBalancingPolicy("pick_first")
    .build();
```

```mermaid
graph LR
    subgraph "Proxy Load Balancing"
        C1[Клиент] --> LB[Load Balancer]
        LB --> S1[Server 1]
        LB --> S2[Server 2]
        LB --> S3[Server 3]
    end
```

**Нюанс с gRPC и L4 балансировщиками:** обычные TCP-балансировщики (L4) распределяют только *соединения*, а не отдельные RPC. Поскольку gRPC мультиплексирует вызовы в одном соединении, все вызовы попадут на один backend. Нужен L7-балансировщик (Envoy, Istio) или client-side балансировка.

<a id="q20"></a>

## Q20. Что такое Name Resolver в gRPC?

`NameResolver` — компонент, превращающий логическое имя сервиса (`user-service`) в конкретный список адресов backend-ов. Это первый шаг любой client-side балансировки: сначала резолвер находит, куда вообще можно слать запрос, и только потом load balancer выбирает один адрес из списка. По сути это и есть встроенный в gRPC механизм service discovery, а схема в target (`dns:///`, `xds:///`) выбирает конкретный резолвер.

**Встроенные резолверы:**
- `dns:///` — стандартный DNS-резолвер (по умолчанию)
- `static:///` — статический список адресов
- `xds:///` — интеграция с xDS (Envoy, Istio)

```java
// DNS-based service discovery
ManagedChannel channel = ManagedChannelBuilder
    .forTarget("dns:///user-service.default.svc.cluster.local:9090")
    .defaultLoadBalancingPolicy("round_robin")
    .build();
```

**Кастомный resolver** — можно написать для интеграции с `Consul`, `Eureka`, `ZooKeeper`:

```java
public class ConsulNameResolverProvider extends NameResolverProvider {
    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        return new ConsulNameResolver(targetUri.getAuthority());
    }

    @Override
    public String getDefaultScheme() {
        return "consul";
    }
}

// Использование: consul:///user-service
```

В [вопросах по микросервисам](../architecture/microservices-interview.md) рассматривается service discovery более детально.

<a id="q21"></a>

## Q21. Как обеспечить безопасность gRPC-соединений?

Безопасность gRPC состоит из защиты канала и проверки полномочий вызова. TLS подтверждает сервер и шифрует трафик; mTLS дополнительно проверяет клиентский сертификат. Токен и права на конкретный RPC проверяют отдельно, обычно через call credentials и серверный interceptor.

В grpc-java стандартный channel builder ориентирован на TLS, а `usePlaintext()` явно задаёт незашифрованное соединение. Это не означает, что любой gRPC сервер уже настроен безопасно: клиент и сервер должны согласовать сертификаты и транспорт. В тестах и локальных сценариях настройки могут отличаться.

---


<a id="q22"></a>

## Q22. Как реализовать аутентификацию в gRPC?

Аутентификация в gRPC — это две симметричные половины. На клиенте `CallCredentials` подкладывает токен в metadata каждого вызова (механизм, заточенный именно под учётные данные и умеющий обновлять токен асинхронно). На сервере `ServerInterceptor` достаёт токен из metadata, проверяет его и либо пропускает вызов дальше, либо рубит его статусом `UNAUTHENTICATED`.

**JWT-аутентификация:**

```java
// Клиентская часть — передача токена
public class JwtCallCredentials extends CallCredentials {
    private final String token;

    public JwtCallCredentials(String token) {
        this.token = token;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo,
            Executor executor, MetadataApplier applier) {
        executor.execute(() -> {
            Metadata headers = new Metadata();
            headers.put(
                Metadata.Key.of("authorization",
                    Metadata.ASCII_STRING_MARSHALLER),
                "Bearer " + token);
            applier.apply(headers);
        });
    }
}

// Использование
UserServiceGrpc.UserServiceBlockingStub stub = blockingStub
    .withCallCredentials(new JwtCallCredentials(jwtToken));
```

**Серверная валидация через Interceptor:**

```java
public class JwtServerInterceptor implements ServerInterceptor {
    private static final Context.Key<String> USER_ID_KEY =
        Context.key("userId");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String authHeader = headers.get(
            Metadata.Key.of("authorization",
                Metadata.ASCII_STRING_MARSHALLER));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            call.close(Status.UNAUTHENTICATED
                .withDescription("Missing or invalid token"), new Metadata());
            return new ServerCall.Listener<>() {};
        }

        String token = authHeader.substring(7);
        String userId = validateAndExtractUserId(token);

        Context ctx = Context.current().withValue(USER_ID_KEY, userId);
        return Contexts.interceptCall(ctx, call, headers, next);
    }
}
```

Подробнее про паттерны аутентификации — в [вопросах по аутентификации и авторизации](../security/authentication-authorization-patterns-interview.md) и [OAuth2](../security/oauth2-interview.md).

<a id="q23"></a>

## Q23. Как интегрировать gRPC со Spring Boot?

Голый gRPC ничего не знает о Spring: сервер и каналы пришлось бы поднимать руками и связывать с бинами самому. Стартеры снимают эту рутину — поднимают gRPC-сервер как часть жизненного цикла приложения, регистрируют сервисы и стабы через DI по аннотациям. Есть два основных подхода:

**1. `net.devh:grpc-spring-boot-starter` (yidongnan) — зрелый, широко используемый:**

```groovy
dependencies {
    implementation 'net.devh:grpc-spring-boot-starter:3.1.0.RELEASE'
}
```

```java
// Серверный gRPC-сервис — аннотация @GrpcService
@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    public UserGrpcService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void getUser(GetUserRequest request,
                        StreamObserver<GetUserResponse> responseObserver) {
        User user = userRepository.findById(request.getId())
            .orElseThrow();

        responseObserver.onNext(GetUserResponse.newBuilder()
            .setUser(toProto(user))
            .build());
        responseObserver.onCompleted();
    }
}
```

```yaml
# application.yml
grpc:
  server:
    port: 9090
  client:
    user-service:
      address: dns:///user-service:9090
      negotiation-type: tls
```

```java
// Клиент — аннотация @GrpcClient
@Service
public class UserClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    public User getUser(long id) {
        GetUserResponse response = userStub.getUser(
            GetUserRequest.newBuilder().setId(id).build());
        return fromProto(response.getUser());
    }
}
```

**2. Spring gRPC (официальный, с Spring Boot 3.4+):**

```groovy
dependencies {
    implementation 'org.springframework.grpc:spring-grpc-spring-boot-starter'
}
```

Предоставляет нативную интеграцию со Spring ecosystem: `@GrpcService`, auto-configuration, Spring Security integration, `@GrpcClient`.

**Выбор подхода:**
- `net.devh:grpc-spring-boot-starter` (yidongnan) — проверен временем, большое сообщество, стабилен
- Spring gRPC — официальный проект Spring, активно развивается, лучшая интеграция с Spring экосистемой

Подробнее об экосистеме Spring — в [вопросах по Spring Boot](../frameworks/spring/spring-boot-interview.md).

<a id="q24"></a>

## Q24. Как тестировать gRPC-сервисы?

Для быстрого теста RPC в Java можно создать `InProcessServerBuilder` и `InProcessChannelBuilder` с одним именем. Клиент вызывает настоящий stub, сервер проходит через gRPC обработчик, а сокет и порт не нужны. Такой тест полезен для контрактов, статусов и интерсепторов.

In-process транспорт не заменяет сетевой интеграционный тест: он не доказывает работу TLS, маршрутизацию, HTTP/2 фрейминг и ограничения реального соединения. Эти свойства проверяют отдельным тестом с сетевым сервером.

---


<a id="q25"></a>

## Q25. Что такое gRPC Health Checking Protocol?

`Health Checking` — стандартный gRPC-сервис (`grpc.health.v1.Health`), через который инфраструктура спрашивает «жив ли ты и готов ли принимать трафик». Смысл стандартизации в том, что проверять здоровье можно тем же протоколом, что и бизнес-вызовы, — не нужен отдельный HTTP-эндпоинт. У него два метода: `Check` (разовый опрос) и `Watch` (server-streaming подписка на изменения статуса). Статус задаётся отдельно для каждого сервиса, а пустая строка означает «весь сервер целиком».

**Protobuf-определение:**

```protobuf
service Health {
  rpc Check (HealthCheckRequest) returns (HealthCheckResponse);
  rpc Watch (HealthCheckRequest) returns (stream HealthCheckResponse);
}

message HealthCheckRequest {
  string service = 1;  // пустая строка = проверка всего сервера
}

message HealthCheckResponse {
  enum ServingStatus {
    UNKNOWN = 0;
    SERVING = 1;
    NOT_SERVING = 2;
    SERVICE_UNKNOWN = 3;
  }
  ServingStatus status = 1;
}
```

**Реализация в Java:**

```java
// Добавление health service
HealthStatusManager healthManager = new HealthStatusManager();

Server server = ServerBuilder.forPort(9090)
    .addService(new UserServiceImpl())
    .addService(healthManager.getHealthService())
    .build();

// Обновление статуса
healthManager.setStatus("com.example.UserService",
    ServingStatus.SERVING);

// При проблемах
healthManager.setStatus("com.example.UserService",
    ServingStatus.NOT_SERVING);
```

**Интеграция с Kubernetes:**

```yaml
# Kubernetes gRPC health check (с K8s 1.24+)
livenessProbe:
  grpc:
    port: 9090
  initialDelaySeconds: 10
readinessProbe:
  grpc:
    port: 9090
  initialDelaySeconds: 5
```

Подробнее о health checking и мониторинге — в [вопросах по observability](../monitoring/observability-interview.md) и [Kubernetes](../devops/kubernetes-interview.md).

<a id="q26"></a>

## Q26. Что такое Server Reflection и зачем она нужна?

`Server Reflection` — это сервис, через который сам сервер отдаёт описание своих сервисов, методов и сообщений в рантайме. Зачем он нужен: обычно клиенту для вызова gRPC требуется сгенерированный из `.proto` код, но инструменты вроде `grpcurl` или API gateway этого кода не имеют. Reflection решает проблему — клиент спрашивает у сервера «что ты умеешь» и формирует запрос на лету. По роли это аналог Swagger/OpenAPI для REST: машиночитаемое самоописание API.

**Включение reflection:**

```java
// Зависимость
// io.grpc:grpc-services

Server server = ServerBuilder.forPort(9090)
    .addService(new UserServiceImpl())
    .addService(ProtoReflectionService.newInstance())  // reflection
    .build();
```

**Использование с `grpcurl` (CLI-клиент для gRPC):**

```bash
# Список сервисов
grpcurl -plaintext localhost:9090 list

# Описание сервиса
grpcurl -plaintext localhost:9090 describe com.example.UserService

# Вызов метода
grpcurl -plaintext -d '{"id": 1}' \
  localhost:9090 com.example.UserService/GetUser
```

**Когда полезно:**
- Отладка и ручное тестирование через `grpcurl`
- Автоматическая генерация документации
- Service mesh / API gateway для динамического обнаружения сервисов

**В production:** reflection обычно включают только на dev/staging или ограничивают доступ. На production он может раскрыть внутреннюю структуру API.

<a id="q27"></a>

## Q27. Что такое gRPC-Web и зачем он нужен?

gRPC-Web даёт браузерному коду доступ к gRPC сервису через адаптированный формат. Обычно браузер обращается к прокси, например Envoy, который переводит запрос в обычный gRPC; некоторые серверы могут понимать gRPC-Web напрямую.

Классический gRPC-Web поддерживает unary и server streaming. Client streaming и bidirectional streaming через него недоступны. Контракт `.proto` остаётся общим, но клиентский код и транспорт отличаются от обычного gRPC Java.

---


<a id="q28"></a>

## Q28. Чем gRPC отличается от REST?

gRPC обычно описывает методы и сообщения в `.proto`, генерирует клиентский код и передаёт бинарные сообщения по HTTP/2. REST организует HTTP API вокруг ресурсов и методов HTTP. REST не требует JSON: представление и схема выбираются контрактом сервиса.

Сравнивайте конкретные требования: браузерных клиентов, потоковую передачу, кэширование, нагрузку и опыт команды. gRPC удобен для типизированных RPC и потоков; ресурсный HTTP API часто проще для внешних интеграций. Протоколы могут сосуществовать в одной системе.

---


<a id="q29"></a>

## Q29. Как обеспечить обратную совместимость в Protocol Buffers?

В бинарном protobuf номер поля входит в wire format, поэтому опубликованный номер не меняют и не используют повторно. Новые поля добавляют под новыми номерами. После удаления поля резервируют и номер, и имя.

```proto
message User {
  reserved 4;
  reserved "old_name";
  string name = 1;
}
```

Переименование поля при прежнем номере не меняет бинарный tag, но меняет сгенерированный API и может сломать ProtoJSON или TextProto. Тип поля, oneof, repeated и новые значения enum тоже оценивают по реальным старым клиентам. Несовместимые изменения выносят в новый контракт.

---


<a id="q30"></a>

## Q30. Когда стоит использовать gRPC, а когда REST?

gRPC часто выбирают для типизированных межсервисных вызовов, потоков и клиентов с согласованным `.proto` контрактом. REST удобен для ресурсных HTTP API и интеграций, где важны обычные адреса, методы и HTTP-кэширование.

Граница «внутри кластера — gRPC, снаружи — REST» полезна как частый сценарий, но не правило. Публичный gRPC API возможен, а внутренний REST API бывает проще. Решение зависит от клиентов, сетевой среды и стоимости поддержки.

---


<a id="q31"></a>

## Q31. Как правильно управлять Channel и Stub в долгоживущих приложениях?

`ManagedChannel` обычно создают как долгоживущий объект для конкретного target, используют повторно и закрывают при остановке приложения. Создавать его на каждый RPC дорого. Несколько лёгких stub можно построить поверх одного канала и настроить для них разные deadlines и credentials.

Не обязательно держать ровно один канал на всё приложение: разные сервисы, политики балансировки и нагрузка могут требовать нескольких. Deadline задают на stub или отдельный вызов. Keepalive без активных вызовов доступен настройкой канала, но его используют с учётом политики сервера.

---


<a id="q32"></a>

## Q32. Какие лучшие практики дизайна proto3 схем нужно знать?

Хорошая proto3-схема — это схема, которую можно безопасно развивать годами, не ломая ни одного клиента. Поэтому большинство практик крутится вокруг одной мысли: эволюция должна быть аддитивной (добавляем, не меняем), а номера полей — неприкосновенны. Ниже — конкретные правила, которые из этого следуют.

**Правила обратной совместимости:**

```protobuf
// Версия 1 (стабильная)
message CreateOrderRequest {
  string customer_id = 1;
  repeated OrderLineItem items = 2;
  // Удалять поля нельзя! Зарезервируйте номера:
  // reserved 3, 4;
  // reserved "old_field";
}

// Версия 2 (обратно совместимая)
message CreateOrderRequest {
  string customer_id = 1;
  repeated OrderLineItem items = 2;
  // Новые поля — только добавлять, с новыми номерами
  string promo_code = 5;            // поле 3 и 4 — зарезервированы
  google.protobuf.Timestamp eta = 6;
  reserved 3, 4;                    // старые номера зарезервированы
}
```

**Лучшие практики:**

| Практика | Описание |
|----------|----------|
| **Не переиспользуйте номера полей** | Старые клиенты могут неправильно интерпретировать данные |
| **`reserved` для удалённых полей** | Предотвращает случайное переиспользование номера |
| **`optional` для необязательных полей** | В proto3 все поля опциональны по умолчанию; `optional` явно добавляет `has_*` методы |
| **`google.protobuf.Timestamp`** | Вместо `int64` для времени (стандартный тип) |
| **`google.protobuf.Any`** | Для полиморфных данных (но с осторожностью) |
| **Enum: первое значение = 0** | В proto3 `0` — default value; нулевое значение — "UNKNOWN"/"UNSPECIFIED" |
| **`oneof` для взаимоисключающих полей** | Явное моделирование вариативности |

```protobuf
syntax = "proto3";
package com.example.orders.v1;

import "google/protobuf/timestamp.proto";

message Order {
  string order_id = 1;              // snake_case в proto → camelCase в Java
  string customer_id = 2;
  repeated OrderLine lines = 3;
  OrderStatus status = 4;
  google.protobuf.Timestamp created_at = 5;

  // oneof для взаимоисключающих источников оплаты
  oneof payment_method {
    CardPayment card = 6;
    BankTransfer transfer = 7;
  }

  reserved 8, 9;  // номера удалённых полей: повторно использовать нельзя
}

enum OrderStatus {
  ORDER_STATUS_UNSPECIFIED = 0;  // всегда 0 как default
  ORDER_STATUS_PENDING = 1;
  ORDER_STATUS_SHIPPED = 2;
  ORDER_STATUS_DELIVERED = 3;
  ORDER_STATUS_CANCELLED = 4;
}
```

**Организация proto-файлов:**

```
protos/
  com/example/orders/v1/
    order.proto
    order_service.proto
  com/example/common/v1/
    money.proto
    address.proto
```

**Версионирование API через пакет:** `com.example.orders.v1` и `com.example.orders.v2` дают отдельные имена сервисов и сообщений; совместимость и маршрутизацию всё равно проверяют.

<a id="q33"></a>

## Q33. Как организовать observability для gRPC-сервисов?

Для наблюдаемости gRPC нужны RPC-aware сигналы: метод, статус, длительность, размер сообщений и число попыток. Обычные метрики прокси по HTTP-пути не дают этой картины при бинарном трафике и общем соединении HTTP/2.

gRPC поддерживает OpenTelemetry-метрики, включая `grpc.client.call.duration` и `grpc.server.call.duration`. Интеграция со Spring или Micrometer зависит от выбранного стартера и версии. Interceptors полезны для прикладных логов и дополнительного контекста, но не являются единственным способом получить метрики и трассировку.

Передавайте trace context через metadata. В логах фиксируйте метод, gRPC status, длительность и идентификатор трассы без токенов и тела чувствительных сообщений. Пороговые значения SLO выбирают по измеренной нагрузке сервиса, а не по универсальным числам.


<a id="q34"></a>

## Q34. Что такое gRPC-Web и как он работает в браузерах?

Браузерный JavaScript не даёт полного управления HTTP/2 trailers, которые использует обычный gRPC. gRPC-Web применяет совместимый формат и обычно проходит через прокси к gRPC backend; совместимая реализация сервера может обработать его без отдельного Envoy.

Классический gRPC-Web поддерживает unary и server streaming, но не client и bidi streaming. Перекодирование может добавлять накладные расходы. Существуют и другие браузерные RPC-протоколы, поэтому утверждать, что любой браузерный клиент обязан использовать gRPC-Web через отдельный прокси, нельзя.

---


<a id="q35"></a>

## Q35. gRPC vs REST vs GraphQL: сравнение и выбор протокола?

REST, GraphQL и gRPC сравнивают по потребителям и форме работы:

- **REST-подход:** ресурсные HTTP API, браузерные и внешние интеграции, обычная семантика методов и кэша.
- **GraphQL:** разные клиентские выборки связанных данных; схема задаёт доступные поля, а клиент выбирает нужные.
- **gRPC:** типизированные RPC, кодогенерация и потоковые вызовы, часто между сервисами.

Выбор зависит от клиентов, сетевой среды, кэширования, потоков, нагрузки и поддержки инструментов. Производительность не должна быть единственным критерием. Подходы могут сосуществовать: внешний HTTP API и внутренние RPC — один из возможных вариантов, а не обязательная архитектура.


<a id="q36"></a>

## Q36. Правила backwards compatibility в Protocol Buffers: как эволюционировать схему?

При эволюции protobuf сохраняют номера опубликованных полей и проверяют wire type, кардинальность и смысл данных. После удаления резервируют номер и имя. Не добавляют обязательные поля для старых клиентов.

Переименование при сохранении номера совместимо с бинарным форматом, но может нарушить ProtoJSON, TextProto и сгенерированный код. Некоторые числовые изменения имеют одинаковый wire type, но старый клиент может потерять значение; поэтому их не объявляют безопасными без проверки. Новые значения enum проверяют на поведении старых клиентов при неизвестном значении.

---


<a id="q37"></a>

## Q37. Как настроить gRPC load balancing в Kubernetes?

Kubernetes Service с ClusterIP обычно балансирует на уровне соединения. Долгоживущий HTTP/2 канал может закрепиться за одним подом, поэтому равномерность отдельных RPC не гарантирована.

Один вариант — headless Service, который возвращает адреса подов, и политика клиента `round_robin`. Одних адресов недостаточно: gRPC по умолчанию использует `pick_first`. Другой вариант — L7-прокси или сервисная сетка, которая балансирует RPC. Выбор зависит от discovery, политики соединений и инфраструктуры.

---


<a id="q38"></a>

## Q38. Как реализовать retry policy в gRPC через ServiceConfig?

ServiceConfig описывает поведение клиента: балансировку, timeout и retry policy для сервисов или методов. Клиент получает конфигурацию через name resolver, например xDS, либо задаёт default конфигурацию программно. Обычный gRPC сервер не отправляет её автоматически в RPC-ответе.

Политика повторов задаёт `maxAttempts`, интервалы backoff и `retryableStatusCodes`. Без неё остаются только ограниченные прозрачные повторы. Подсистема повторов обычно включена по умолчанию; `disableRetry()` отключает её. Для неидемпотентных методов политику выбирают особенно осторожно.

---


<a id="q39"></a>

## Q39. Как интегрировать gRPC с Spring Boot через grpc-spring-boot-starter?

`net.devh:grpc-spring-boot-starter` — сторонняя интеграция gRPC и Spring Boot. Реализацию сгенерированной базы помечают `@GrpcService`, а клиентский stub внедряют через `@GrpcClient`. Каналы, адреса и сервер настраивают в конфигурации приложения. Стартер также поддерживает глобальные interceptors.

```java
@GrpcService
class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {
    // Реализация методов из .proto
}
```

Не сводите библиотеку к обязательному набору ровно трёх аннотаций. Проверяйте конкретную версию стартера и не смешивайте его API с официальным Spring gRPC, где регистрация клиентов устроена иначе.

---


<a id="q40"></a>

## Q40. Что такое transcoding gRPC ↔ REST через google.api.http аннотации?

**gRPC Transcoding** позволяет отдать один и тот же сервис сразу в двух видах — как gRPC и как REST/JSON — не дублируя бизнес-логику. Вы размечаете методы в `.proto` аннотациями `google.api.http` (какой HTTP-метод и путь соответствует RPC), а прокси (Envoy, Cloud Endpoints, grpc-gateway) на лету переводит входящие HTTP/JSON-запросы в gRPC-вызовы и обратно. Внутренние сервисы при этом ходят по быстрому gRPC, а внешние клиенты и браузеры — по привычному REST.

**Аннотации в `.proto`:**

```protobuf
syntax = "proto3";
import "google/api/annotations.proto";

service OrderService {
  rpc GetOrder (GetOrderRequest) returns (Order) {
    option (google.api.http) = {
      get: "/v1/orders/{order_id}"
    };
  }

  rpc CreateOrder (CreateOrderRequest) returns (Order) {
    option (google.api.http) = {
      post: "/v1/orders"
      body: "*"
    };
  }

  rpc ListOrders (ListOrdersRequest) returns (ListOrdersResponse) {
    option (google.api.http) = {
      get: "/v1/orders"
      // query params маппируются автоматически из полей запроса
    };
  }
}

message GetOrderRequest {
  string order_id = 1;  // → path param {order_id}
}
```

**Конфигурация Envoy для transcoding:**

```yaml
http_filters:
  - name: envoy.filters.http.grpc_json_transcoder
    typed_config:
      "@type": type.googleapis.com/envoy.extensions.filters.http.grpc_json_transcoder.v3.GrpcJsonTranscoder
      proto_descriptor: "/etc/envoy/api.pb"
      services: ["com.example.OrderService"]
      print_options:
        add_whitespace: true
        always_print_primitive_fields: true
        preserve_proto_field_names: true
```

**Альтернатива — grpc-gateway (Go):** генерирует reverse-proxy из аннотаций, более популярен в Go-экосистеме.

**Результат транскодинга:**

```
REST: GET /v1/orders/123
  ↓ Envoy transcoder
gRPC: OrderService.GetOrder({order_id: "123"})
  ↓ gRPC response
REST: HTTP 200 {"id":"123","status":"DELIVERED","total":99.9}
```

**Когда использовать:** когда нужно поддерживать и gRPC-клиенты (внутренние сервисы) и REST-клиенты (внешние партнёры, браузеры) без дублирования бизнес-логики.

---

## See also

- [HTTP и REST](http-rest-interview.md) — сравнение REST и gRPC: когда выбирать каждый подход, JSON vs Protobuf, версионирование контрактов
- [GraphQL](graphql-interview.md) — ещё одна альтернатива REST: flexible queries vs строгие контракты, разные сценарии применения
- [Микросервисная архитектура](../architecture/microservices-interview.md) — gRPC как основной протокол межсервисного взаимодействия внутри кластера
- [Сетевые протоколы](../architecture/networking-interview.md) — gRPC работает поверх HTTP/2: multiplexing, flow control, header compression
- [Spring Boot](../frameworks/spring/spring-boot-interview.md) — интеграция gRPC со Spring Boot: grpc-spring-boot-starter, ServerInterceptor, аннотации
- [Распределённые системы](../architecture/distributed-systems-interview.md) — межсервисное взаимодействие, deadline propagation, retry политики
- [API Design Best Practices](api-design-best-practices-interview.md)
- [API Versioning](api-versioning-interview.md)
- [OpenAPI / Swagger](openapi-swagger-interview.md)
- [Richardson Maturity Model (REST)](rest-maturity-interview.md)
- [Шпаргалка: gRPC](../../development/api/grpc/grpc.md) — теория
