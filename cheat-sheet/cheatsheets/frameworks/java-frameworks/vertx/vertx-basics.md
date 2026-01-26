---
title: "Vert.x: Основы"
description: "Реактивный toolkit для JVM для создания асинхронных, неблокирующих приложений"
tags: ["vertx", "java", "kotlin", "reactive", "async", "non-blocking", "event-driven"]
difficulty: "intermediate"
prerequisites: ["java/java-basics.md", "java/java-concurrency-basics.md"]
next: ["vertx-core.md", "vertx-web.md", "vertx-reactive.md"]
updated: "2026-01-16"
related: ["quarkus/quarkus-reactive.md", "spring/spring-webflux.md"]
---

# Vert.x: Основы

## Введение в Vert.x

Vert.x - это toolkit для создания реактивных приложений на JVM. Vert.x использует event-driven, non-blocking архитектуру, что позволяет создавать высокопроизводительные, масштабируемые приложения.

### Основные особенности

- **Event-driven**: Событийно-ориентированная архитектура
- **Non-blocking**: Полностью неблокирующий I/O
- **Polyglot**: Поддержка Java, Kotlin, Scala, Groovy, JavaScript, Ruby
- **Reactive**: Встроенная поддержка reactive streams
- **Lightweight**: Минимальные зависимости
- **High performance**: Высокая производительность благодаря event loop

### Архитектура Vert.x

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │───▶│   Event Loop    │───▶│   Verticles     │
│    Code         │    │   (Non-block)  │    │   (Workers)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┴───────────────────────┘
                           Event Bus
```

### Event Loop Model

Vert.x использует event loop модель, где один поток обрабатывает множество событий:

- **Event Loop**: Обрабатывает события асинхронно
- **Worker Verticles**: Для блокирующих операций
- **Event Bus**: Механизм обмена сообщениями между verticles

---

## Установка и настройка

### Maven зависимость

```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-core</artifactId>
    <version>4.5.0</version>
</dependency>
```

### Gradle зависимость

```gradle
dependencies {
    implementation 'io.vertx:vertx-core:4.5.0'
}
```

### Базовая структура проекта

```
project/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── MainVerticle.java
│       └── resources/
└── pom.xml
```

---

## Основные концепции

### Verticle

Verticle - это основная единица развертывания в Vert.x. Verticle инкапсулирует код для обработки событий.

#### Простой Verticle

```java
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class SimpleVerticle extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("Verticle started!");
        startPromise.complete();
    }
    
    @Override
    public void stop() {
        System.out.println("Verticle stopped!");
    }
}
```

#### Запуск Verticle

```java
import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        
        vertx.deployVerticle(new SimpleVerticle(), result -> {
            if (result.succeeded()) {
                System.out.println("Verticle deployed successfully");
            } else {
                System.err.println("Failed to deploy verticle: " + result.cause());
            }
        });
    }
}
```

### Event Bus

Event Bus - это механизм обмена сообщениями между verticles.

#### Отправка сообщений

```java
import io.vertx.core.eventbus.EventBus;

public class SenderVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        
        // Отправка простого сообщения
        eventBus.send("news.uk.sport", "Yay! Someone kicked a ball");
        
        // Отправка с ответом
        eventBus.request("news.uk.sport", "Yay! Someone kicked a ball", reply -> {
            if (reply.succeeded()) {
                System.out.println("Received reply: " + reply.result().body());
            }
        });
    }
}
```

#### Получение сообщений

```java
public class ReceiverVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        
        // Простой consumer
        eventBus.consumer("news.uk.sport", message -> {
            System.out.println("Received news: " + message.body());
        });
        
        // Consumer с ответом
        eventBus.consumer("news.uk.sport", message -> {
            System.out.println("Received news: " + message.body());
            message.reply("Got it!");
        });
    }
}
```

### AsyncResult и Future

Vert.x использует callback-based API с AsyncResult и Future для обработки асинхронных операций.

#### Использование AsyncResult

```java
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public class AsyncExample extends AbstractVerticle {
    
    public void example() {
        vertx.createHttpServer()
            .requestHandler(req -> {
                req.response().end("Hello World");
            })
            .listen(8080, new Handler<AsyncResult<HttpServer>>() {
                @Override
                public void handle(AsyncResult<HttpServer> result) {
                    if (result.succeeded()) {
                        System.out.println("Server started on port 8080");
                    } else {
                        System.err.println("Failed to start server: " + result.cause());
                    }
                }
            });
    }
}
```

#### Использование Future

```java
import io.vertx.core.Future;

public class FutureExample extends AbstractVerticle {
    
    public void example() {
        Future<HttpServer> future = vertx.createHttpServer()
            .requestHandler(req -> req.response().end("Hello World"))
            .listen(8080);
        
        future.onSuccess(server -> {
            System.out.println("Server started on port 8080");
        }).onFailure(cause -> {
            System.err.println("Failed to start server: " + cause);
        });
    }
}
```

---

## HTTP Server

### Простой HTTP Server

```java
import io.vertx.core.http.HttpServer;

public class HttpServerVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        
        server.requestHandler(request -> {
            request.response()
                .putHeader("content-type", "text/plain")
                .end("Hello from Vert.x!");
        });
        
        server.listen(8080, result -> {
            if (result.succeeded()) {
                System.out.println("HTTP server started on port 8080");
            } else {
                System.err.println("Failed to start HTTP server: " + result.cause());
            }
        });
    }
}
```

### Обработка различных HTTP методов

```java
public class HttpMethodsVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        vertx.createHttpServer()
            .requestHandler(request -> {
                HttpMethod method = request.method();
                String path = request.path();
                
                if (method == HttpMethod.GET && "/api/users".equals(path)) {
                    handleGetUsers(request);
                } else if (method == HttpMethod.POST && "/api/users".equals(path)) {
                    handlePostUser(request);
                } else if (method == HttpMethod.PUT && path.startsWith("/api/users/")) {
                    handlePutUser(request);
                } else if (method == HttpMethod.DELETE && path.startsWith("/api/users/")) {
                    handleDeleteUser(request);
                } else {
                    request.response()
                        .setStatusCode(404)
                        .end("Not Found");
                }
            })
            .listen(8080);
    }
    
    private void handleGetUsers(HttpServerRequest request) {
        // Логика получения пользователей
        request.response()
            .putHeader("content-type", "application/json")
            .end("[{\"id\":1,\"name\":\"John\"}]");
    }
    
    private void handlePostUser(HttpServerRequest request) {
        request.bodyHandler(buffer -> {
            // Обработка тела запроса
            request.response()
                .setStatusCode(201)
                .end("User created");
        });
    }
    
    private void handlePutUser(HttpServerRequest request) {
        String userId = request.path().substring("/api/users/".length());
        request.bodyHandler(buffer -> {
            // Обновление пользователя
            request.response()
                .putHeader("content-type", "application/json")
                .end("{\"id\":" + userId + ",\"updated\":true}");
        });
    }
    
    private void handleDeleteUser(HttpServerRequest request) {
        String userId = request.path().substring("/api/users/".length());
        // Удаление пользователя
        request.response()
            .setStatusCode(204)
            .end();
    }
}
```

### Работа с JSON

```java
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

public class JsonExample extends AbstractVerticle {
    
    @Override
    public void start() {
        vertx.createHttpServer()
            .requestHandler(request -> {
                if (request.method() == HttpMethod.POST) {
                    request.bodyHandler(buffer -> {
                        JsonObject json = buffer.toJsonObject();
                        String name = json.getString("name");
                        int age = json.getInteger("age");
                        
                        JsonObject response = new JsonObject()
                            .put("status", "success")
                            .put("message", "Hello " + name + ", you are " + age + " years old");
                        
                        request.response()
                            .putHeader("content-type", "application/json")
                            .end(response.encode());
                    });
                } else {
                    request.response()
                        .setStatusCode(405)
                        .end("Method Not Allowed");
                }
            })
            .listen(8080);
    }
}
```

---

## HTTP Client

### Простой HTTP Client

```java
import io.vertx.core.http.HttpClient;

public class HttpClientExample extends AbstractVerticle {
    
    @Override
    public void start() {
        HttpClient client = vertx.createHttpClient();
        
        client.get(8080, "localhost", "/api/users")
            .send(ar -> {
                if (ar.succeeded()) {
                    HttpResponse<Void> response = ar.result();
                    System.out.println("Status code: " + response.statusCode());
                    response.bodyHandler(body -> {
                        System.out.println("Response body: " + body.toString());
                    });
                } else {
                    System.err.println("Request failed: " + ar.cause());
                }
            });
    }
}
```

### POST запрос с телом

```java
public class HttpClientPostExample extends AbstractVerticle {
    
    @Override
    public void start() {
        HttpClient client = vertx.createHttpClient();
        
        JsonObject json = new JsonObject()
            .put("name", "John Doe")
            .put("email", "john@example.com");
        
        client.post(8080, "localhost", "/api/users")
            .putHeader("content-type", "application/json")
            .sendJson(json, ar -> {
                if (ar.succeeded()) {
                    HttpResponse<Void> response = ar.result();
                    System.out.println("Status code: " + response.statusCode());
                } else {
                    System.err.println("Request failed: " + ar.cause());
                }
            });
    }
}
```

---

## Worker Verticles

Worker Verticles используются для выполнения блокирующих операций.

### Создание Worker Verticle

```java
public class WorkerVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        // Этот код выполняется в worker thread pool
        System.out.println("Worker verticle running on: " + Thread.currentThread().getName());
        
        // Блокирующая операция
        try {
            Thread.sleep(5000);
            System.out.println("Blocking operation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### Развертывание Worker Verticle

```java
public class DeployWorkerExample {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        
        DeploymentOptions options = new DeploymentOptions()
            .setWorker(true)
            .setWorkerPoolSize(10);
        
        vertx.deployVerticle(new WorkerVerticle(), options, result -> {
            if (result.succeeded()) {
                System.out.println("Worker verticle deployed");
            }
        });
    }
}
```

---

## Конфигурация

### Конфигурация через DeploymentOptions

```java
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

public class ConfigExample {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        
        JsonObject config = new JsonObject()
            .put("http.port", 8080)
            .put("db.host", "localhost")
            .put("db.port", 5432);
        
        DeploymentOptions options = new DeploymentOptions()
            .setConfig(config)
            .setInstances(4);
        
        vertx.deployVerticle(new ConfigurableVerticle(), options);
    }
}
```

### Получение конфигурации в Verticle

```java
public class ConfigurableVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        JsonObject config = config();
        int httpPort = config.getInteger("http.port", 8080);
        String dbHost = config.getString("db.host", "localhost");
        int dbPort = config.getInteger("db.port", 5432);
        
        System.out.println("HTTP Port: " + httpPort);
        System.out.println("DB Host: " + dbHost);
        System.out.println("DB Port: " + dbPort);
    }
}
```

---

## Обработка ошибок

### Обработка ошибок в AsyncResult

```java
public class ErrorHandlingExample extends AbstractVerticle {
    
    @Override
    public void start() {
        vertx.createHttpServer()
            .requestHandler(request -> {
                try {
                    // Обработка запроса
                    processRequest(request);
                } catch (Exception e) {
                    request.response()
                        .setStatusCode(500)
                        .end("Internal Server Error: " + e.getMessage());
                }
            })
            .listen(8080, result -> {
                if (result.failed()) {
                    System.err.println("Failed to start server: " + result.cause());
                    // Логирование или отправка уведомления
                }
            });
    }
    
    private void processRequest(HttpServerRequest request) {
        // Логика обработки
    }
}
```

### Глобальный обработчик исключений

```java
public class GlobalErrorHandler {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        
        // Глобальный обработчик необработанных исключений
        vertx.exceptionHandler(throwable -> {
            System.err.println("Unhandled exception: " + throwable.getMessage());
            throwable.printStackTrace();
        });
        
        vertx.deployVerticle(new MyVerticle());
    }
}
```

---

## Лучшие практики

### 1. Не блокировать Event Loop

```java
// ❌ Плохо - блокирует event loop
public void badExample() {
    try {
        Thread.sleep(1000); // Блокирующая операция
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

// ✅ Хорошо - использует timer
public void goodExample() {
    vertx.setTimer(1000, id -> {
        // Код выполнится через 1 секунду без блокировки
    });
}
```

### 2. Использовать Worker Verticles для блокирующих операций

```java
// ❌ Плохо - блокирующая операция в event loop
public void badExample() {
    String result = database.query(); // Блокирующая операция
}

// ✅ Хорошо - использует worker verticle
DeploymentOptions options = new DeploymentOptions().setWorker(true);
vertx.deployVerticle(new DatabaseVerticle(), options);
```

### 3. Правильная обработка ресурсов

```java
public class ResourceManagementExample extends AbstractVerticle {
    private HttpServer server;
    
    @Override
    public void start() {
        server = vertx.createHttpServer()
            .requestHandler(req -> req.response().end("Hello"))
            .listen(8080);
    }
    
    @Override
    public void stop() {
        if (server != null) {
            server.close();
        }
    }
}
```

### 4. Использовать Future для композиции

```java
public class FutureCompositionExample extends AbstractVerticle {
    
    public void example() {
        Future<String> future1 = getData1();
        Future<String> future2 = getData2();
        
        Future.all(future1, future2)
            .onSuccess(results -> {
                String result1 = results.resultAt(0);
                String result2 = results.resultAt(1);
                // Обработка результатов
            })
            .onFailure(cause -> {
                System.err.println("Failed: " + cause);
            });
    }
    
    private Future<String> getData1() {
        return Future.future(promise -> {
            // Асинхронная операция
            promise.complete("Data 1");
        });
    }
    
    private Future<String> getData2() {
        return Future.future(promise -> {
            // Асинхронная операция
            promise.complete("Data 2");
        });
    }
}
```

---

## WebSocket

### WebSocket Server

```java
import io.vertx.core.http.ServerWebSocket;

public class WebSocketServerVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        vertx.createHttpServer()
            .webSocketHandler(ws -> {
                System.out.println("WebSocket connection from: " + ws.remoteAddress());
                
                ws.textMessageHandler(message -> {
                    System.out.println("Received message: " + message);
                    ws.writeTextMessage("Echo: " + message);
                });
                
                ws.closeHandler(v -> {
                    System.out.println("WebSocket closed");
                });
                
                ws.exceptionHandler(throwable -> {
                    System.err.println("WebSocket error: " + throwable.getMessage());
                });
            })
            .listen(8080);
    }
}
```

### WebSocket Client

```java
import io.vertx.core.http.WebSocket;

public class WebSocketClientExample extends AbstractVerticle {
    
    @Override
    public void start() {
        HttpClient client = vertx.createHttpClient();
        
        client.webSocket(8080, "localhost", "/ws", ar -> {
            if (ar.succeeded()) {
                WebSocket ws = ar.result();
                
                ws.textMessageHandler(message -> {
                    System.out.println("Received: " + message);
                });
                
                ws.writeTextMessage("Hello from client");
                
                ws.closeHandler(v -> {
                    System.out.println("Connection closed");
                });
            } else {
                System.err.println("Failed to connect: " + ar.cause());
            }
        });
    }
}
```

### WebSocket с JSON

```java
public class WebSocketJsonExample extends AbstractVerticle {
    
    @Override
    public void start() {
        vertx.createHttpServer()
            .webSocketHandler(ws -> {
                ws.textMessageHandler(message -> {
                    try {
                        JsonObject json = new JsonObject(message);
                        String type = json.getString("type");
                        
                        if ("chat".equals(type)) {
                            String text = json.getString("text");
                            JsonObject response = new JsonObject()
                                .put("type", "message")
                                .put("text", text)
                                .put("timestamp", System.currentTimeMillis());
                            ws.writeTextMessage(response.encode());
                        }
                    } catch (Exception e) {
                        ws.writeTextMessage(new JsonObject()
                            .put("type", "error")
                            .put("message", e.getMessage()).encode());
                    }
                });
            })
            .listen(8080);
    }
}
```

---

## База данных

### PostgreSQL Client

```java
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class DatabaseVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(10);
        
        PgPool client = PgPool.pool(vertx, new PgConnectOptions()
            .setPort(5432)
            .setHost("localhost")
            .setDatabase("mydb")
            .setUser("user")
            .setPassword("password"), poolOptions);
        
        client.query("SELECT * FROM users")
            .execute(ar -> {
                if (ar.succeeded()) {
                    RowSet<Row> rows = ar.result();
                    for (Row row : rows) {
                        System.out.println("User: " + row.getString("name"));
                    }
                } else {
                    System.err.println("Query failed: " + ar.cause());
                }
            });
    }
}
```

### Подготовленные запросы

```java
public class PreparedQueryExample extends AbstractVerticle {
    
    @Override
    public void start() {
        PgPool client = PgPool.pool(vertx, new PgConnectOptions()
            .setHost("localhost")
            .setPort(5432)
            .setDatabase("mydb")
            .setUser("user")
            .setPassword("password"));
        
        // Подготовленный запрос
        client.preparedQuery("SELECT * FROM users WHERE id = $1")
            .execute(Tuple.of(1), ar -> {
                if (ar.succeeded()) {
                    RowSet<Row> rows = ar.result();
                    if (rows.size() > 0) {
                        Row row = rows.iterator().next();
                        System.out.println("User: " + row.getString("name"));
                    }
                }
            });
    }
}
```

### Транзакции

```java
public class TransactionExample extends AbstractVerticle {
    
    @Override
    public void start() {
        PgPool client = PgPool.pool(vertx, new PgConnectOptions()
            .setHost("localhost")
            .setPort(5432)
            .setDatabase("mydb")
            .setUser("user")
            .setPassword("password"));
        
        client.getConnection(ar -> {
            if (ar.succeeded()) {
                SqlConnection conn = ar.result();
                
                conn.begin(trans -> {
                    if (trans.succeeded()) {
                        Transaction tx = trans.result();
                        
                        conn.preparedQuery("INSERT INTO users (name) VALUES ($1)")
                            .execute(Tuple.of("John"), insert -> {
                                if (insert.succeeded()) {
                                    conn.preparedQuery("UPDATE accounts SET balance = balance - 100 WHERE user_id = $1")
                                        .execute(Tuple.of(1), update -> {
                                            if (update.succeeded()) {
                                                tx.commit(commit -> {
                                                    if (commit.succeeded()) {
                                                        System.out.println("Transaction committed");
                                                    }
                                                });
                                            } else {
                                                tx.rollback();
                                            }
                                        });
                                } else {
                                    tx.rollback();
                                }
                            });
                    }
                });
            }
        });
    }
}
```

### MySQL Client

```java
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.mysqlclient.MySQLConnectOptions;

public class MySQLExample extends AbstractVerticle {
    
    @Override
    public void start() {
        MySQLPool client = MySQLPool.pool(vertx, new MySQLConnectOptions()
            .setPort(3306)
            .setHost("localhost")
            .setDatabase("mydb")
            .setUser("user")
            .setPassword("password"));
        
        client.query("SELECT * FROM users")
            .execute(ar -> {
                if (ar.succeeded()) {
                    RowSet<Row> rows = ar.result();
                    for (Row row : rows) {
                        System.out.println("User: " + row.getString("name"));
                    }
                }
            });
    }
}
```

---

## Файловая система

### Чтение файла

```java
import io.vertx.core.file.FileSystem;

public class FileSystemExample extends AbstractVerticle {
    
    @Override
    public void start() {
        FileSystem fs = vertx.fileSystem();
        
        fs.readFile("data.txt", ar -> {
            if (ar.succeeded()) {
                Buffer buffer = ar.result();
                System.out.println("File content: " + buffer.toString());
            } else {
                System.err.println("Failed to read file: " + ar.cause());
            }
        });
    }
}
```

### Запись файла

```java
public class FileWriteExample extends AbstractVerticle {
    
    @Override
    public void start() {
        FileSystem fs = vertx.fileSystem();
        
        Buffer buffer = Buffer.buffer("Hello Vert.x!");
        fs.writeFile("output.txt", buffer, ar -> {
            if (ar.succeeded()) {
                System.out.println("File written successfully");
            } else {
                System.err.println("Failed to write file: " + ar.cause());
            }
        });
    }
}
```

### Асинхронное копирование файла

```java
public class FileCopyExample extends AbstractVerticle {
    
    @Override
    public void start() {
        FileSystem fs = vertx.fileSystem();
        
        fs.copy("source.txt", "destination.txt", ar -> {
            if (ar.succeeded()) {
                System.out.println("File copied successfully");
            } else {
                System.err.println("Failed to copy file: " + ar.cause());
            }
        });
    }
}
```

### Работа с директориями

```java
public class DirectoryExample extends AbstractVerticle {
    
    @Override
    public void start() {
        FileSystem fs = vertx.fileSystem();
        
        // Создание директории
        fs.mkdir("newdir", ar -> {
            if (ar.succeeded()) {
                System.out.println("Directory created");
            }
        });
        
        // Чтение директории
        fs.readDir("mydir", ar -> {
            if (ar.succeeded()) {
                List<String> files = ar.result();
                files.forEach(file -> System.out.println("File: " + file));
            }
        });
        
        // Рекурсивное чтение
        fs.readDir("mydir", ".*", ar -> {
            if (ar.succeeded()) {
                List<String> files = ar.result();
                files.forEach(System.out::println);
            }
        });
    }
}
```

---

## Timers и Periodic Tasks

### Одноразовый таймер

```java
public class TimerExample extends AbstractVerticle {
    
    @Override
    public void start() {
        // Выполнить через 1 секунду
        long timerId = vertx.setTimer(1000, id -> {
            System.out.println("Timer fired!");
        });
        
        // Отменить таймер
        vertx.cancelTimer(timerId);
    }
}
```

### Периодический таймер

```java
public class PeriodicExample extends AbstractVerticle {
    
    @Override
    public void start() {
        // Выполнять каждые 5 секунд
        long periodicId = vertx.setPeriodic(5000, id -> {
            System.out.println("Periodic task executed");
        });
        
        // Остановить периодический таймер
        vertx.cancelTimer(periodicId);
    }
}
```

### Планировщик задач

```java
public class SchedulerExample extends AbstractVerticle {
    private long periodicId;
    
    @Override
    public void start() {
        // Запустить задачу каждую минуту
        periodicId = vertx.setPeriodic(60000, id -> {
            performScheduledTask();
        });
    }
    
    @Override
    public void stop() {
        if (periodicId > 0) {
            vertx.cancelTimer(periodicId);
        }
    }
    
    private void performScheduledTask() {
        System.out.println("Scheduled task executed at: " + new Date());
    }
}
```

---

## Shared Data

### Local Map

```java
import io.vertx.core.shareddata.SharedData;

public class SharedDataExample extends AbstractVerticle {
    
    @Override
    public void start() {
        SharedData sharedData = vertx.sharedData();
        
        sharedData.getLocalMap("myMap", ar -> {
            if (ar.succeeded()) {
                LocalMap<String, String> map = ar.result();
                map.put("key1", "value1");
                map.put("key2", "value2");
                
                String value = map.get("key1");
                System.out.println("Value: " + value);
            }
        });
    }
}
```

### Async Map

```java
public class AsyncMapExample extends AbstractVerticle {
    
    @Override
    public void start() {
        SharedData sharedData = vertx.sharedData();
        
        sharedData.getAsyncMap("myAsyncMap", ar -> {
            if (ar.succeeded()) {
                AsyncMap<String, String> map = ar.result();
                
                map.put("key", "value", putResult -> {
                    if (putResult.succeeded()) {
                        map.get("key", getResult -> {
                            if (getResult.succeeded()) {
                                String value = getResult.result();
                                System.out.println("Value: " + value);
                            }
                        });
                    }
                });
            }
        });
    }
}
```

### Locks

```java
public class LockExample extends AbstractVerticle {
    
    @Override
    public void start() {
        SharedData sharedData = vertx.sharedData();
        
        sharedData.getLock("myLock", ar -> {
            if (ar.succeeded()) {
                Lock lock = ar.result();
                
                try {
                    // Критическая секция
                    performCriticalOperation();
                } finally {
                    lock.release();
                }
            }
        });
    }
    
    private void performCriticalOperation() {
        // Операция, требующая синхронизации
    }
}
```

---

## Reactive Streams

### Publisher и Subscriber

```java
import io.vertx.core.streams.Pump;
import io.vertx.core.streams.ReadStream;
import io.vertx.core.streams.WriteStream;

public class StreamsExample extends AbstractVerticle {
    
    @Override
    public void start() {
        ReadStream<Buffer> readStream = createReadStream();
        WriteStream<Buffer> writeStream = createWriteStream();
        
        // Pump автоматически передает данные из ReadStream в WriteStream
        Pump pump = Pump.pump(readStream, writeStream);
        pump.start();
        
        readStream.endHandler(v -> {
            writeStream.end();
        });
    }
    
    private ReadStream<Buffer> createReadStream() {
        // Создание ReadStream
        return null;
    }
    
    private WriteStream<Buffer> createWriteStream() {
        // Создание WriteStream
        return null;
    }
}
```

### Обработка потока данных

```java
public class DataStreamExample extends AbstractVerticle {
    
    @Override
    public void start() {
        ReadStream<Buffer> stream = createDataStream();
        
        stream.handler(buffer -> {
            // Обработка каждого блока данных
            System.out.println("Received: " + buffer.length() + " bytes");
        });
        
        stream.endHandler(v -> {
            System.out.println("Stream ended");
        });
        
        stream.exceptionHandler(throwable -> {
            System.err.println("Stream error: " + throwable.getMessage());
        });
        
        stream.resume();
    }
    
    private ReadStream<Buffer> createDataStream() {
        return null;
    }
}
```

---

## Кластеризация

### Создание кластера

```java
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class ClusterExample {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions()
            .setClusterManager(new HazelcastClusterManager());
        
        Vertx.clusteredVertx(options, ar -> {
            if (ar.succeeded()) {
                Vertx vertx = ar.result();
                System.out.println("Cluster started");
                
                vertx.deployVerticle(new MyVerticle());
            } else {
                System.err.println("Failed to start cluster: " + ar.cause());
            }
        });
    }
}
```

### Event Bus в кластере

```java
public class ClusteredEventBusExample extends AbstractVerticle {
    
    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        
        // Сообщения автоматически распространяются по кластеру
        eventBus.consumer("cluster.address", message -> {
            System.out.println("Received in cluster: " + message.body());
        });
        
        // Отправка сообщения в кластер
        eventBus.publish("cluster.address", "Hello from cluster");
    }
}
```

---

## Безопасность

### HTTPS Server

```java
import io.vertx.core.net.JksOptions;

public class HttpsServerExample extends AbstractVerticle {
    
    @Override
    public void start() {
        HttpServerOptions options = new HttpServerOptions()
            .setSsl(true)
            .setKeyStoreOptions(new JksOptions()
                .setPath("keystore.jks")
                .setPassword("password"));
        
        vertx.createHttpServer(options)
            .requestHandler(req -> {
                req.response().end("Hello from HTTPS");
            })
            .listen(8443);
    }
}
```

### Аутентификация

```java
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.handler.JWTAuthHandler;

public class AuthExample extends AbstractVerticle {
    
    @Override
    public void start() {
        JWTAuth jwt = JWTAuth.create(vertx, new JWTAuthOptions()
            .setKeyStore(new KeyStoreOptions()
                .setPath("keystore.jks")
                .setPassword("password")));
        
        Router router = Router.router(vertx);
        
        router.route("/api/*").handler(JWTAuthHandler.create(jwt));
        
        router.get("/api/protected").handler(ctx -> {
            ctx.response().end("Protected resource");
        });
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080);
    }
}
```

### CORS

```java
import io.vertx.ext.web.handler.CorsHandler;

public class CorsExample extends AbstractVerticle {
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        
        router.route().handler(CorsHandler.create()
            .addOrigin("http://localhost:3000")
            .allowedMethods(HttpMethod.GET, HttpMethod.POST)
            .allowedHeaders(Set.of("Content-Type", "Authorization")));
        
        router.get("/api/data").handler(ctx -> {
            ctx.json(new JsonObject().put("data", "value"));
        });
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080);
    }
}
```

---

## Тестирование

### Unit тестирование Verticle

```java
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class VerticleTest {
    
    @Test
    void testVerticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MyVerticle(), testContext.succeeding(id -> {
            testContext.completeNow();
        }));
    }
}
```

### Тестирование HTTP сервера

```java
@ExtendWith(VertxExtension.class)
public class HttpServerTest {
    
    @Test
    void testHttpServer(Vertx vertx, VertxTestContext testContext) {
        vertx.createHttpServer()
            .requestHandler(req -> req.response().end("OK"))
            .listen(8080)
            .onComplete(testContext.succeeding(server -> {
                HttpClient client = vertx.createHttpClient();
                client.get(8080, "localhost", "/")
                    .send(testContext.succeeding(response -> {
                        testContext.verify(() -> {
                            assertEquals(200, response.statusCode());
                            testContext.completeNow();
                        });
                    }));
            }));
    }
}
```

### Тестирование Event Bus

```java
@ExtendWith(VertxExtension.class)
public class EventBusTest {
    
    @Test
    void testEventBus(Vertx vertx, VertxTestContext testContext) {
        EventBus eventBus = vertx.eventBus();
        
        eventBus.consumer("test.address", message -> {
            testContext.verify(() -> {
                assertEquals("test message", message.body());
                testContext.completeNow();
            });
        });
        
        eventBus.send("test.address", "test message");
    }
}
```

---

## Мониторинг и метрики

### Использование Dropwizard Metrics

```java
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

public class MetricsExample extends AbstractVerticle {
    
    @Override
    public void start() {
        VertxOptions options = new VertxOptions()
            .setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
        
        Vertx vertx = Vertx.vertx(options);
        MetricsService metricsService = MetricsService.create(vertx);
        
        vertx.setPeriodic(5000, id -> {
            JsonObject metrics = metricsService.getMetricsSnapshot(vertx);
            System.out.println("Metrics: " + metrics.encodePrettily());
        });
    }
}
```

### Кастомные метрики

```java
import io.vertx.ext.dropwizard.Counter;
import io.vertx.ext.dropwizard.Timer;

public class CustomMetricsExample extends AbstractVerticle {
    
    @Override
    public void start() {
        MetricsService metricsService = MetricsService.create(vertx);
        
        Counter requestCounter = metricsService.counter("requests");
        Timer responseTimer = metricsService.timer("response-time");
        
        vertx.createHttpServer()
            .requestHandler(req -> {
                requestCounter.inc();
                Timer.Context context = responseTimer.time();
                
                req.response().end("OK");
                
                context.stop();
            })
            .listen(8080);
    }
}
```

---

## Производительность

### Оптимизация Event Loop

```java
public class PerformanceExample extends AbstractVerticle {
    
    @Override
    public void start() {
        // Использование нескольких event loops
        VertxOptions options = new VertxOptions()
            .setEventLoopPoolSize(4); // Количество event loops
        
        Vertx vertx = Vertx.vertx(options);
        
        // Оптимизация worker pool
        options.setWorkerPoolSize(20);
    }
}
```

### Connection Pooling

```java
public class ConnectionPoolExample extends AbstractVerticle {
    
    @Override
    public void start() {
        PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(10)
            .setMaxWaitQueueSize(100);
        
        PgPool pool = PgPool.pool(vertx, new PgConnectOptions()
            .setHost("localhost")
            .setPort(5432)
            .setDatabase("mydb"), poolOptions);
        
        // Пул автоматически управляет соединениями
        pool.query("SELECT * FROM users")
            .execute(ar -> {
                // Обработка результата
            });
    }
}
```

### Кеширование

```java
import io.vertx.core.shareddata.LocalMap;

public class CachingExample extends AbstractVerticle {
    
    @Override
    public void start() {
        SharedData sharedData = vertx.sharedData();
        
        sharedData.getLocalMap("cache", ar -> {
            if (ar.succeeded()) {
                LocalMap<String, String> cache = ar.result();
                
                vertx.createHttpServer()
                    .requestHandler(req -> {
                        String key = req.path();
                        String cached = cache.get(key);
                        
                        if (cached != null) {
                            req.response().end(cached);
                        } else {
                            // Получить данные
                            String data = fetchData(key);
                            cache.put(key, data);
                            req.response().end(data);
                        }
                    })
                    .listen(8080);
            }
        });
    }
    
    private String fetchData(String key) {
        // Получение данных из источника
        return "data";
    }
}
```

---

## Развертывание

### Создание Fat JAR

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.4</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <transformers>
                    <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                        <manifestEntries>
                            <Main-Class>io.vertx.core.Launcher</Main-Class>
                            <Main-Verticle>com.example.MainVerticle</Main-Verticle>
                        </manifestEntries>
                    </transformer>
                </transformers>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Запуск приложения

```bash
java -jar myapp-fat.jar
```

### Docker

```dockerfile
FROM openjdk:11-jre-slim
COPY target/myapp-fat.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

---

## Troubleshooting

### Отладка Event Loop

```java
public class DebugExample extends AbstractVerticle {
    
    @Override
    public void start() {
        // Включение блокирующих проверок
        VertxOptions options = new VertxOptions()
            .setBlockedThreadCheckInterval(1000)
            .setMaxEventLoopExecuteTime(2000000000); // 2 секунды в наносекундах
        
        Vertx vertx = Vertx.vertx(options);
    }
}
```

### Логирование

```java
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class LoggingExample extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExample.class);
    
    @Override
    public void start() {
        logger.info("Verticle started");
        logger.debug("Debug message");
        logger.warn("Warning message");
        logger.error("Error message", new Exception("Error"));
    }
}
```

### Профилирование

```java
public class ProfilingExample extends AbstractVerticle {
    
    @Override
    public void start() {
        // Включение метрик для профилирования
        VertxOptions options = new VertxOptions()
            .setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
        
        Vertx vertx = Vertx.vertx(options);
    }
}
```

---

## Router и Routing

### Использование Router

```java
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RouterExample extends AbstractVerticle {
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        
        router.get("/api/users").handler(this::getUsers);
        router.post("/api/users").handler(this::createUser);
        router.get("/api/users/:id").handler(this::getUser);
        router.put("/api/users/:id").handler(this::updateUser);
        router.delete("/api/users/:id").handler(this::deleteUser);
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080);
    }
    
    private void getUsers(RoutingContext ctx) {
        JsonArray users = new JsonArray()
            .add(new JsonObject().put("id", 1).put("name", "John"))
            .add(new JsonObject().put("id", 2).put("name", "Jane"));
        ctx.response()
            .putHeader("content-type", "application/json")
            .end(users.encode());
    }
    
    private void createUser(RoutingContext ctx) {
        JsonObject user = ctx.getBodyAsJson();
        // Создание пользователя
        ctx.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json")
            .end(user.encode());
    }
    
    private void getUser(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        JsonObject user = new JsonObject()
            .put("id", id)
            .put("name", "User " + id);
        ctx.json(user);
    }
    
    private void updateUser(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        JsonObject user = ctx.getBodyAsJson();
        // Обновление пользователя
        ctx.json(user);
    }
    
    private void deleteUser(RoutingContext ctx) {
        String id = ctx.pathParam("id");
        // Удаление пользователя
        ctx.response().setStatusCode(204).end();
    }
}
```

### Path параметры и Query параметры

```java
public class ParametersExample extends AbstractVerticle {
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        
        router.get("/api/users/:id/posts/:postId").handler(ctx -> {
            String userId = ctx.pathParam("id");
            String postId = ctx.pathParam("postId");
            String sort = ctx.queryParams().get("sort");
            String order = ctx.queryParams().get("order");
            
            JsonObject response = new JsonObject()
                .put("userId", userId)
                .put("postId", postId)
                .put("sort", sort)
                .put("order", order);
            
            ctx.json(response);
        });
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080);
    }
}
```

### Middleware

```java
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.TimeoutHandler;

public class MiddlewareExample extends AbstractVerticle {
    
    @Override
    public void start() {
        Router router = Router.router(vertx);
        
        // Глобальные middleware
        router.route().handler(LoggerHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(TimeoutHandler.create(5000));
        
        // CORS middleware
        router.route("/api/*").handler(CorsHandler.create()
            .addOrigin("*")
            .allowedMethods(Set.of(HttpMethod.GET, HttpMethod.POST)));
        
        router.get("/api/data").handler(ctx -> {
            ctx.json(new JsonObject().put("data", "value"));
        });
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080);
    }
}
```

### Sub-routers

```java
public class SubRouterExample extends AbstractVerticle {
    
    @Override
    public void start() {
        Router mainRouter = Router.router(vertx);
        Router apiRouter = Router.router(vertx);
        Router userRouter = Router.router(vertx);
        
        // Настройка user router
        userRouter.get("/").handler(ctx -> ctx.json(getAllUsers()));
        userRouter.get("/:id").handler(ctx -> {
            String id = ctx.pathParam("id");
            ctx.json(getUser(id));
        });
        userRouter.post("/").handler(ctx -> {
            JsonObject user = ctx.getBodyAsJson();
            ctx.json(createUser(user));
        });
        
        // Монтирование sub-routers
        apiRouter.mountSubRouter("/users", userRouter);
        mainRouter.mountSubRouter("/api", apiRouter);
        
        vertx.createHttpServer()
            .requestHandler(mainRouter)
            .listen(8080);
    }
    
    private JsonArray getAllUsers() {
        return new JsonArray();
    }
    
    private JsonObject getUser(String id) {
        return new JsonObject().put("id", id);
    }
    
    private JsonObject createUser(JsonObject user) {
        return user;
    }
}
```

---

## Kafka Integration

### Kafka Producer

```java
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

public class KafkaProducerExample extends AbstractVerticle {
    
    @Override
    public void start() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config);
        
        vertx.createHttpServer()
            .requestHandler(req -> {
                req.bodyHandler(buffer -> {
                    KafkaProducerRecord<String, String> record = 
                        KafkaProducerRecord.create("my-topic", buffer.toString());
                    
                    producer.send(record, ar -> {
                        if (ar.succeeded()) {
                            req.response().end("Message sent");
                        } else {
                            req.response().setStatusCode(500).end("Error: " + ar.cause());
                        }
                    });
                });
            })
            .listen(8080);
    }
}
```

### Kafka Consumer

```java
import io.vertx.kafka.client.consumer.KafkaConsumer;

public class KafkaConsumerExample extends AbstractVerticle {
    
    @Override
    public void start() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "my-group");
        config.put("auto.offset.reset", "earliest");
        
        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);
        
        consumer.handler(record -> {
            System.out.println("Received: " + record.value());
        });
        
        consumer.subscribe("my-topic", ar -> {
            if (ar.succeeded()) {
                System.out.println("Subscribed to topic");
            }
        });
    }
}
```

---

## Redis Integration

### Redis Client

```java
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;

public class RedisExample extends AbstractVerticle {
    
    @Override
    public void start() {
        Redis redis = Redis.createClient(vertx, "redis://localhost:6379");
        RedisAPI api = RedisAPI.api(redis);
        
        // SET операция
        api.set(Arrays.asList("key", "value"), ar -> {
            if (ar.succeeded()) {
                System.out.println("Key set");
            }
        });
        
        // GET операция
        api.get("key", ar -> {
            if (ar.succeeded()) {
                Response response = ar.result();
                System.out.println("Value: " + response.toString());
            }
        });
        
        // Использование в HTTP handler
        vertx.createHttpServer()
            .requestHandler(req -> {
                String key = req.getParam("key");
                api.get(key, ar -> {
                    if (ar.succeeded()) {
                        Response response = ar.result();
                        req.response().end(response.toString());
                    } else {
                        req.response().setStatusCode(404).end();
                    }
                });
            })
            .listen(8080);
    }
}
```

---

## Service Discovery

### Регистрация сервиса

```java
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class ServiceDiscoveryExample extends AbstractVerticle {
    
    @Override
    public void start() {
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        
        HttpServer server = vertx.createHttpServer()
            .requestHandler(req -> req.response().end("Hello"))
            .listen(8080, ar -> {
                if (ar.succeeded()) {
                    // Регистрация сервиса
                    HttpEndpoint.createRecord("my-service", "localhost", 8080, "/",
                        new JsonObject().put("api.name", "my-api"))
                        .setHandler(recordResult -> {
                            if (recordResult.succeeded()) {
                                Record record = recordResult.result();
                                discovery.publish(record, publishResult -> {
                                    if (publishResult.succeeded()) {
                                        System.out.println("Service published");
                                    }
                                });
                            }
                        });
                }
            });
    }
}
```

### Поиск сервиса

```java
public class ServiceLookupExample extends AbstractVerticle {
    
    @Override
    public void start() {
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        
        HttpEndpoint.getClient(discovery, 
            new JsonObject().put("name", "my-service"), ar -> {
                if (ar.succeeded()) {
                    HttpClient client = ar.result();
                    client.get("/", response -> {
                        // Использование клиента
                    }).end();
                }
            });
    }
}
```

---

## Circuit Breaker

### Использование Circuit Breaker

```java
import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;

public class CircuitBreakerExample extends AbstractVerticle {
    
    @Override
    public void start() {
        CircuitBreaker breaker = CircuitBreaker.create("my-circuit-breaker", vertx,
            new CircuitBreakerOptions()
                .setMaxFailures(5)
                .setTimeout(2000)
                .setResetTimeout(10000));
        
        vertx.createHttpServer()
            .requestHandler(req -> {
                breaker.execute(future -> {
                    // Выполнение операции
                    performOperation(future);
                }).onComplete(ar -> {
                    if (ar.succeeded()) {
                        req.response().end(ar.result().toString());
                    } else {
                        req.response().setStatusCode(500).end("Error");
                    }
                });
            })
            .listen(8080);
    }
    
    private void performOperation(Promise<String> promise) {
        // Асинхронная операция
        vertx.setTimer(1000, id -> {
            promise.complete("Success");
        });
    }
}
```

---

## Retry Pattern

### Реализация Retry

```java
public class RetryExample extends AbstractVerticle {
    
    public <T> Future<T> retry(Future<T> operation, int maxRetries) {
        Promise<T> promise = Promise.promise();
        retryInternal(operation, maxRetries, 0, promise);
        return promise.future();
    }
    
    private <T> void retryInternal(Future<T> operation, int maxRetries, 
                                   int currentAttempt, Promise<T> promise) {
        operation.onComplete(ar -> {
            if (ar.succeeded()) {
                promise.complete(ar.result());
            } else if (currentAttempt < maxRetries) {
                vertx.setTimer(1000 * (currentAttempt + 1), id -> {
                    retryInternal(operation, maxRetries, currentAttempt + 1, promise);
                });
            } else {
                promise.fail(ar.cause());
            }
        });
    }
    
    @Override
    public void start() {
        Future<String> operation = Future.future(promise -> {
            // Операция, которая может завершиться ошибкой
            if (Math.random() > 0.5) {
                promise.complete("Success");
            } else {
                promise.fail("Failed");
            }
        });
        
        retry(operation, 3).onComplete(ar -> {
            if (ar.succeeded()) {
                System.out.println("Operation succeeded: " + ar.result());
            } else {
                System.err.println("Operation failed after retries: " + ar.cause());
            }
        });
    }
}
```

---

## Graceful Shutdown

### Корректное завершение работы

```java
public class GracefulShutdownExample extends AbstractVerticle {
    private HttpServer server;
    private List<Long> timers = new ArrayList<>();
    
    @Override
    public void start() {
        server = vertx.createHttpServer()
            .requestHandler(req -> req.response().end("OK"))
            .listen(8080);
        
        // Регистрация обработчика завершения
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            shutdown();
        }));
    }
    
    private void shutdown() {
        System.out.println("Shutting down gracefully...");
        
        // Отмена всех таймеров
        timers.forEach(vertx::cancelTimer);
        
        // Закрытие сервера
        server.close(ar -> {
            if (ar.succeeded()) {
                System.out.println("Server closed");
                vertx.close();
            }
        });
    }
}
```

---

## Best Practices и Patterns

### 1. Вертикальное масштабирование

```java
// Развертывание нескольких экземпляров verticle
DeploymentOptions options = new DeploymentOptions()
    .setInstances(4); // 4 экземпляра на каждый CPU core

vertx.deployVerticle(new MyVerticle(), options);
```

### 2. Горизонтальное масштабирование

```java
// Использование кластера для горизонтального масштабирования
VertxOptions options = new VertxOptions()
    .setClusterManager(new HazelcastClusterManager());

Vertx.clusteredVertx(options, ar -> {
    if (ar.succeeded()) {
        Vertx vertx = ar.result();
        // Развертывание в кластере
    }
});
```

### 3. Обработка больших файлов

```java
public class LargeFileExample extends AbstractVerticle {
    
    @Override
    public void start() {
        vertx.createHttpServer()
            .requestHandler(req -> {
                if (req.path().equals("/upload")) {
                    req.setExpectMultipart(true);
                    req.uploadHandler(upload -> {
                        String filename = upload.filename();
                        Pump.pump(upload, vertx.fileSystem()
                            .openBlocking("uploads/" + filename, new OpenOptions().setWrite(true)))
                            .start();
                    });
                }
            })
            .listen(8080);
    }
}
```

### 4. Batch Processing

```java
public class BatchProcessingExample extends AbstractVerticle {
    
    public void processBatch(List<String> items, int batchSize) {
        List<List<String>> batches = partition(items, batchSize);
        
        Future<Void> future = Future.succeededFuture();
        for (List<String> batch : batches) {
            future = future.compose(v -> processBatch(batch));
        }
        
        future.onComplete(ar -> {
            if (ar.succeeded()) {
                System.out.println("All batches processed");
            }
        });
    }
    
    private Future<Void> processBatch(List<String> batch) {
        return Future.future(promise -> {
            // Обработка батча
            vertx.setTimer(100, id -> promise.complete());
        });
    }
    
    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }
}
```

### 5. Rate Limiting

```java
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitingExample extends AbstractVerticle {
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final int maxRequests = 100;
    private final long windowMs = 60000; // 1 минута
    
    @Override
    public void start() {
        // Сброс счетчика каждую минуту
        vertx.setPeriodic(windowMs, id -> {
            requestCount.set(0);
        });
        
        vertx.createHttpServer()
            .requestHandler(req -> {
                if (requestCount.get() >= maxRequests) {
                    req.response()
                        .setStatusCode(429)
                        .end("Too Many Requests");
                } else {
                    requestCount.incrementAndGet();
                    req.response().end("OK");
                }
            })
            .listen(8080);
    }
}
```

---

## Полезные ссылки

- [Официальный сайт Vert.x](https://vertx.io/)
- [Документация Vert.x](https://vertx.io/docs/)
- [Vert.x на GitHub](https://github.com/eclipse-vertx/vert.x)
- [Примеры Vert.x](https://github.com/vert-x3/vertx-examples)
- [Vert.x Guide](https://vertx.io/docs/guide-for-java-devs/)

---

**Дата последнего обновления:** 2026-01-16

## Содержание

- [Введение в Vert.x](#�-ведение-в-vert-x)
  - [Основные особенности](#�-�-новн�-е-о�-обенно�-�-и)
  - [Архитектура Vert.x](#�-�-�-и�-ек�-�-�-а-vert-x)



