---
title: "Vert.x: Основы"
description: "Реактивный toolkit для JVM для создания асинхронных, event-driven приложений"
tags:
  - vertx
  - java
  - kotlin
  - reactive
  - async
  - non-blocking
  - event-driven
difficulty: "intermediate"
prerequisites: ["java/java-basics.md", "java/java-concurrency-basics.md"]
next: ["vertx-core.md", "vertx-web.md", "vertx-reactive.md"]
updated: "2026-02-11"
related: ["quarkus/quarkus-reactive.md", "spring/spring-webflux.md"]
---

# Vert.x: Основы

**Дата обновления:** 2026-02-11

## Полезные ссылки

- [Eclipse Vert.x — документация](https://vertx.io/docs/)
- [Vert.x Java API](https://vertx.io/docs/apidocs/)
- [Vert.x — GitHub](https://github.com/eclipse-vertx/vert.x)
- [Vert.x Guide for Java Developers](https://vertx.io/docs/guide-for-java-devs/)
- [Vert.x Examples](https://github.com/vert-x3/vertx-examples)

## Содержание

- [Введение в Vert.x](#введение-в-vertx)
  - [Основные особенности](#основные-особенности)
  - [Архитектура Vert.x](#архитектура-vertx)
  - [Event Loop Model](#event-loop-model)
- [Установка и настройка](#установка-и-настройка)
  - [Maven зависимость](#maven-зависимость)
  - [Gradle зависимость](#gradle-зависимость)
  - [Структура проекта](#структура-проекта)
- [Основные концепции](#основные-концепции)
  - [Verticle](#verticle)
  - [Event Bus](#event-bus)
  - [AsyncResult и Future](#asyncresult-и-future)
- [HTTP Server](#http-server)
  - [Базовый HTTP сервер](#базовый-http-сервер)
  - [Работа с JSON](#работа-с-json)
- [HTTP Client](#http-client)
- [Worker Verticles](#worker-verticles)
- [Конфигурация](#конфигурация)
  - [DeploymentOptions](#deploymentoptions)
  - [Получение конфигурации в Verticle](#получение-конфигурации-в-verticle)
- [Обработка ошибок](#обработка-ошибок)
- [Антипаттерны](#антипаттерны)
- [WebSocket](#websocket)
- [Работа с БД](#работа-с-бд)
  - [PostgreSQL Client](#postgresql-client)
  - [Prepared Queries](#prepared-queries)
  - [Транзакции](#транзакции)
- [Файловая система](#файловая-система)
- [Таймеры и периодические задачи](#таймеры-и-периодические-задачи)
- [Shared Data](#shared-data)
- [Router и маршрутизация](#router-и-маршрутизация)
- [Безопасность](#безопасность)
  - [HTTPS Server](#https-server)
  - [JWT аутентификация](#jwt-аутентификация)
  - [CORS](#cors)
- [Тестирование](#тестирование)
- [Метрики и мониторинг](#метрики-и-мониторинг)
- [Развёртывание](#развёртывание)
  - [Fat JAR](#fat-jar)
  - [Docker](#docker)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Заключение](#заключение)

## Введение в Vert.x

**Vert.x** — это **toolkit** для создания асинхронных приложений на **JVM**. Vert.x использует **event-driven**, **non-blocking** подход, что позволяет обрабатывать множество соединений с минимальным количеством потоков.

### Основные особенности

- **Event-driven**: реактивно-асинхронная архитектура
- **Non-blocking**: неблокирующий I/O
- **Polyglot**: поддержка **Java**, **Kotlin**, **Scala**, **Groovy**, **JavaScript**, **Ruby**
- **Reactive**: интеграция с **Reactive Streams**
- **Lightweight**: минимальный runtime
- **High performance**: отличная производительность благодаря event loop

### Архитектура Vert.x

```text
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │───▶│   Event Loop     │───▶│   Verticles     │
│    Code         │    │   (Non-blocking) │    │   (Workers)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┴───────────────────────┘
                           Event Bus
```

### Event Loop Model

**Vert.x** использует **event loop** модель, где один поток обрабатывает множество событий:

- **Event Loop**: обрабатывает неблокирующие операции
- **Worker Verticles**: для блокирующих операций
- **Event Bus**: обмен сообщениями между verticles

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

### Структура проекта

```text
project/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/
│       │       └── MainVerticle.java
│       └── resources/
└── pom.xml
```

---

## Основные концепции

### Verticle

**Verticle** — базовая единица развёртывания в Vert.x. Verticle изолирован и может обрабатывать события.

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

Развёртывание:

```java
import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new SimpleVerticle(), result -> {
            if (result.succeeded()) {
                System.out.println("Verticle deployed successfully");
            } else {
                System.err.println("Failed to deploy: " + result.cause());
            }
        });
    }
}
```

### Event Bus

**Event Bus** — шина сообщений для обмена между verticles.

Отправка сообщений:

```java
public class SenderVerticle extends AbstractVerticle {

    @Override
    public void start() {
        EventBus eventBus = vertx.eventBus();
        eventBus.send("news.uk.sport", "Yay! Someone kicked a ball");
        eventBus.request("news.uk.sport", "Request", reply -> {
            if (reply.succeeded()) {
                System.out.println("Reply: " + reply.result().body());
            }
        });
    }
}
```

Получение сообщений:

```java
public class ReceiverVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.eventBus().consumer("news.uk.sport", message -> {
            System.out.println("Received: " + message.body());
            message.reply("Got it!");
        });
    }
}
```

### AsyncResult и Future

Callback-based API:

```java
vertx.createHttpServer()
    .requestHandler(req -> req.response().end("Hello World"))
    .listen(8080, result -> {
        if (result.succeeded()) {
            System.out.println("Server started on port 8080");
        } else {
            System.err.println("Failed: " + result.cause());
        }
    });
```

Future-based API:

```java
vertx.createHttpServer()
    .requestHandler(req -> req.response().end("Hello World"))
    .listen(8080)
    .onSuccess(server -> System.out.println("Server started"))
    .onFailure(cause -> System.err.println("Failed: " + cause));
```

---

## HTTP Server

### Базовый HTTP сервер

```java
public class HttpServerVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer()
            .requestHandler(request -> {
                request.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
            })
            .listen(8080, result -> {
                if (result.succeeded()) {
                    System.out.println("HTTP server started on port 8080");
                } else {
                    System.err.println("Failed: " + result.cause());
                }
            });
    }
}
```

### Работа с JSON

```java
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

request.bodyHandler(buffer -> {
    JsonObject json = buffer.toJsonObject();
    String name = json.getString("name");
    int age = json.getInteger("age");

    JsonObject response = new JsonObject()
        .put("status", "success")
        .put("message", "Hello " + name);

    request.response()
        .putHeader("content-type", "application/json")
        .end(response.encode());
});
```

---

## HTTP Client

```java
import io.vertx.core.http.HttpClient;

HttpClient client = vertx.createHttpClient();

client.get(8080, "localhost", "/api/users")
    .send(ar -> {
        if (ar.succeeded()) {
            HttpResponse<?> response = ar.result();
            response.bodyHandler(body -> System.out.println(body.toString()));
        }
    });
```

POST с телом:

```java
JsonObject json = new JsonObject()
    .put("name", "John Doe")
    .put("email", "john@example.com");

client.post(8080, "localhost", "/api/users")
    .putHeader("content-type", "application/json")
    .sendJson(json, ar -> {
        if (ar.succeeded()) {
            System.out.println("Status: " + ar.result().statusCode());
        }
    });
```

---

## Worker Verticles

Worker Verticles выполняют блокирующий код в отдельном пуле потоков.

```java
public class WorkerVerticle extends AbstractVerticle {

    @Override
    public void start() {
        System.out.println("Worker on: " + Thread.currentThread().getName());
        try {
            Thread.sleep(5000);
            System.out.println("Blocking operation completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

Развёртывание:

```java
DeploymentOptions options = new DeploymentOptions()
    .setWorker(true)
    .setWorkerPoolSize(10);

vertx.deployVerticle(new WorkerVerticle(), options);
```

---

## Конфигурация

### DeploymentOptions

```java
JsonObject config = new JsonObject()
    .put("http.port", 8080)
    .put("db.host", "localhost")
    .put("db.port", 5432);

DeploymentOptions options = new DeploymentOptions()
    .setConfig(config)
    .setInstances(4);

vertx.deployVerticle(new ConfigurableVerticle(), options);
```

### Получение конфигурации в Verticle

```java
public class ConfigurableVerticle extends AbstractVerticle {

    @Override
    public void start() {
        JsonObject config = config();
        int httpPort = config.getInteger("http.port", 8080);
        String dbHost = config.getString("db.host", "localhost");
    }
}
```

---

## Обработка ошибок

```java
vertx.createHttpServer()
    .requestHandler(request -> {
        try {
            processRequest(request);
        } catch (Exception e) {
            request.response()
                .setStatusCode(500)
                .end("Error: " + e.getMessage());
        }
    })
    .listen(8080, result -> {
        if (result.failed()) {
            System.err.println("Failed to start: " + result.cause());
        }
    });
```

Глобальный обработчик исключений:

```java
vertx.exceptionHandler(throwable -> {
    System.err.println("Unhandled: " + throwable.getMessage());
});
```

---

## Антипаттерны

### 1. Не блокировать Event Loop

```java
// Плохо
Thread.sleep(1000);

// Хорошо
vertx.setTimer(1000, id -> {
    // Код выполнится через 1 секунду
});
```

### 2. Использовать Worker Verticles для блокирующего кода

```java
// Плохо — блокирует event loop
String result = database.query();

// Хорошо
DeploymentOptions options = new DeploymentOptions().setWorker(true);
vertx.deployVerticle(new DatabaseVerticle(), options);
```

### 3. Закрывать ресурсы в stop()

```java
@Override
public void stop() {
    if (server != null) {
        server.close();
    }
}
```

### 4. Использовать Future для композиции

```java
Future.all(future1, future2)
    .onSuccess(results -> {
        String r1 = results.resultAt(0);
        String r2 = results.resultAt(1);
    })
    .onFailure(cause -> System.err.println("Failed: " + cause));
```

---

## WebSocket

### WebSocket Server

```java
vertx.createHttpServer()
    .webSocketHandler(ws -> {
        ws.textMessageHandler(message -> {
            System.out.println("Received: " + message);
            ws.writeTextMessage("Echo: " + message);
        });
        ws.closeHandler(v -> System.out.println("Client disconnected"));
    })
    .listen(8080);
```

### WebSocket Client

```java
client.webSocket(8080, "localhost", "/ws", ar -> {
    if (ar.succeeded()) {
        WebSocket ws = ar.result();
        ws.textMessageHandler(msg -> System.out.println("Received: " + msg));
        ws.writeTextMessage("Hello from client");
    }
});
```

---

## Работа с БД

### PostgreSQL Client

```java
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;

PoolOptions poolOptions = new PoolOptions().setMaxSize(10);
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
        }
    });
```

### Prepared Queries

```java
client.preparedQuery("SELECT * FROM users WHERE id = $1")
    .execute(Tuple.of(1), ar -> {
        if (ar.succeeded()) {
            RowSet<Row> rows = ar.result();
            for (Row row : rows) {
                System.out.println(row.getString("name"));
            }
        }
    });
```

### Транзакции

```java
client.getConnection(ar -> {
    if (ar.succeeded()) {
        SqlConnection conn = ar.result();
        conn.begin(tx -> {
            if (tx.succeeded()) {
                conn.preparedQuery("INSERT INTO users (name) VALUES ($1)")
                    .execute(Tuple.of("John"), insert -> {
                        if (insert.succeeded()) {
                            tx.result().commit();
                        } else {
                            tx.result().rollback();
                        }
                    });
            }
        });
    }
});
```

---

## Файловая система

```java
FileSystem fs = vertx.fileSystem();

fs.readFile("data.txt", ar -> {
    if (ar.succeeded()) {
        System.out.println("Content: " + ar.result().toString());
    }
});

fs.writeFile("output.txt", Buffer.buffer("Hello Vert.x!"), ar -> {
    if (ar.succeeded()) {
        System.out.println("File written");
    }
});
```

---

## Таймеры и периодические задачи

```java
// Одноразовый таймер (1 сек)
long timerId = vertx.setTimer(1000, id -> {
    System.out.println("Timer fired!");
});
vertx.cancelTimer(timerId);

// Периодическая задача (5 сек)
long periodicId = vertx.setPeriodic(5000, id -> {
    System.out.println("Periodic task");
});
vertx.cancelTimer(periodicId);
```

---

## Shared Data

```java
SharedData sharedData = vertx.sharedData();

sharedData.getLocalMap("myMap", ar -> {
    if (ar.succeeded()) {
        LocalMap<String, String> map = ar.result();
        map.put("key1", "value1");
    }
});

sharedData.getLock("myLock", ar -> {
    if (ar.succeeded()) {
        Lock lock = ar.result();
        try {
            // Критическая секция
        } finally {
            lock.release();
        }
    }
});
```

---

## Router и маршрутизация

```java
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

Router router = Router.router(vertx);

router.get("/api/users").handler(this::getUsers);
router.post("/api/users").handler(this::createUser);
router.get("/api/users/:id").handler(this::getUser);
router.put("/api/users/:id").handler(this::updateUser);
router.delete("/api/users/:id").handler(this::deleteUser);

vertx.createHttpServer()
    .requestHandler(router)
    .listen(8080);
```

Path и Query параметры:

```java
router.get("/api/users/:id/posts/:postId").handler(ctx -> {
    String userId = ctx.pathParam("id");
    String postId = ctx.pathParam("postId");
    String sort = ctx.queryParams().get("sort");
});
```

---

## Безопасность

### HTTPS Server

```java
import io.vertx.core.net.JksOptions;

HttpServerOptions options = new HttpServerOptions()
    .setSsl(true)
    .setKeyStoreOptions(new JksOptions()
        .setPath("keystore.jks")
        .setPassword("password"));

vertx.createHttpServer(options)
    .requestHandler(req -> req.response().end("HTTPS"))
    .listen(8443);
```

### JWT аутентификация

```java
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.handler.JWTAuthHandler;

JWTAuth jwt = JWTAuth.create(vertx, new JWTAuthOptions()
    .setKeyStore(new KeyStoreOptions()
        .setPath("keystore.jks")
        .setPassword("password")));

router.route("/api/*").handler(JWTAuthHandler.create(jwt));
```

### CORS

```java
import io.vertx.ext.web.handler.CorsHandler;

router.route().handler(CorsHandler.create()
    .addOrigin("http://localhost:3000")
    .allowedMethods(HttpMethod.GET, HttpMethod.POST)
    .allowedHeaders(Set.of("Content-Type", "Authorization")));
```

---

## Тестирование

```java
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

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

---

## Метрики и мониторинг

```java
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;

VertxOptions options = new VertxOptions()
    .setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));

Vertx vertx = Vertx.vertx(options);
MetricsService metricsService = MetricsService.create(vertx);

vertx.setPeriodic(5000, id -> {
    JsonObject metrics = metricsService.getMetricsSnapshot(vertx);
    System.out.println(metrics.encodePrettily());
});
```

---

## Развёртывание

### Fat JAR

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.5.0</version>
    <executions>
        <execution>
            <phase>package</phase>
            <goals><goal>shade</goal></goals>
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

Запуск:

```bash
java -jar myapp-fat.jar
```

### Docker

```dockerfile
FROM eclipse-temurin:11-jre
COPY target/myapp-fat.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

---

## Лучшие практики

1. **Количество инстансов**: `setInstances(4)` — по числу CPU cores
2. **Кластеризация**: используйте Hazelcast для shared Event Bus
3. **Не блокировать Event Loop**: используйте `vertx.setTimer()` вместо `Thread.sleep()`
4. **Worker Verticles**: для JDBC, блокирующих вызовов
5. **Future композиция**: `Future.all()`, `Future.any()` вместо callback hell
6. **Закрытие ресурсов**: в `stop()` вертикла
7. **Connection Pooling**: настраивайте `maxSize` для БД клиентов

---

## Решение проблем

### Blocked thread detected

**Симптомы**: `Thread vert.x-eventloop-thread-0 has been blocked for X ms`, предупреждения в логах.

**Причина**: блокирующий код выполняется в event loop (Thread.sleep, синхронный I/O, тяжёлые вычисления).

**Решение**:
- Перенесите блокирующий код в Worker Verticle
- Используйте `vertx.setTimer()` вместо `Thread.sleep()`
- Для JDBC используйте `executeBlocking()` или отдельный worker

### Event loop не отвечает

**Симптомы**: приложение зависает, запросы не обрабатываются.

**Решение**:
- Увеличьте `setEventLoopPoolSize()` в VertxOptions
- Проверьте, нет ли блокирующих операций
- Включите `setBlockedThreadCheckInterval(1000)` для диагностики

### OutOfMemory при большом количестве соединений

**Симптомы**: OOM при высоком RPS или множестве WebSocket соединений.

**Решение**:
- Увеличьте heap: `-Xmx2g`
- Настройте `setMaxWebSocketFrameSize()` для WebSocket
- Используйте back-pressure: `pause()`/`resume()` на ReadStream

### Ошибки подключения к БД в кластере

**Симптомы**: connection refused при clustered deployment.

**Решение**:
- Убедитесь, что БД доступна из всех нод
- Проверьте `connectOptions` и pool size
- Для кластера используйте общий менеджер (Hazelcast) и корректные настройки сети

### Verticle не развёртывается

**Симптомы**: `DeploymentException`, verticle не стартует.

**Решение**:
- Проверьте логи: `result.cause()` в callback deploy
- Убедитесь, что `startPromise.complete()` вызывается при успехе
- При ошибке: `startPromise.fail(throwable)`

---

## Заключение

**Vert.x** — мощный toolkit для создания высокопроизводительных асинхронных приложений на JVM. Event-driven архитектура и non-blocking I/O позволяют эффективно использовать ресурсы при высокой нагрузке.

### Ключевые преимущества

- **Высокая производительность**: event loop, минимум потоков
- **Reactive**: Future, Reactive Streams
- **Polyglot**: Java, Kotlin, JavaScript и др.
- **Модульность**: vertx-core, vertx-web, vertx-mysql-client и т.д.

### Когда использовать Vert.x

- Высоконагруженные REST API
- WebSocket приложения
- Микросервисы с реактивным стеком
- Real-time системы
