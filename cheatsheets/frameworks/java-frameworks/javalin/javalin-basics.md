---
title: "Javalin: Основы"
description: "Легковесный Java и Kotlin веб-фреймворк для создания RESTful API и веб-приложений"
tags:
  - javalin
  - java
  - kotlin
  - rest
  - microservices
  - lightweight
type: "overview"
difficulty: "beginner"
aliases:
  - "Javalin"
  - "Основы"
  - "Javalin: Основы"
  - "javalin basics"
prerequisites:
  - "[[java-basics]]"
related:
  - "[[spark-basics]]"
  - "[[spring-rest]]"
next:
  - "[[javalin-routing]]"
  - "[[javalin-websocket]]"
updated: "2026-04-20"
---

# Javalin: Основы

## Полезные ссылки

- [Javalin Official Site](https://javalin.io)
- [Javalin Documentation](https://javalin.io/documentation)
- [Javalin on GitHub](https://github.com/javalin/javalin)
- [Javalin Examples](https://javalin.io/tutorials)

## Содержание

- [Введение в Javalin](#введение-в-javalin)
  - [Основные особенности](#основные-особенности)
- [Установка и настройка](#установка-и-настройка)
  - [Maven зависимость](#maven-зависимость)
  - [Gradle зависимость](#gradle-зависимость)
- [Базовое использование](#базовое-использование)
  - [Простой HTTP сервер](#простой-http-сервер)
  - [Различные HTTP методы](#различные-http-методы)
  - [Параметры пути](#параметры-пути)
  - [Query параметры](#query-параметры)
  - [Тело запроса](#тело-запроса)
- [Контекст (Context)](#контекст-context)
  - [Получение заголовков](#получение-заголовков)
  - [Установка заголовков](#установка-заголовков)
  - [Установка статус кода](#установка-статус-кода)
  - [Редирект](#редирект)
- [JSON обработка](#json-обработка)
  - [Отправка JSON](#отправка-json)
  - [Получение JSON](#получение-json)
- [Валидация](#валидация)
  - [Валидация запросов](#валидация-запросов)
- [Обработка ошибок](#обработка-ошибок)
  - [Обработка 404](#обработка-404)
  - [Обработка исключений](#обработка-исключений)
- [Конфигурация](#конфигурация)
  - [Настройка приложения](#настройка-приложения)
  - [Пользовательские настройки](#пользовательские-настройки)
- [Middleware](#middleware)
  - [Before Handlers](#before-handlers)
  - [After Handlers](#after-handlers)
  - [Exception Handlers](#exception-handlers)
  - [Error Handlers](#error-handlers)
- [Сессии](#сессии)
  - [Использование сессий](#использование-сессий)
- [WebSocket](#websocket)
  - [WebSocket Server](#websocket-server)
  - [WebSocket с JSON](#websocket-с-json)
- [База данных](#база-данных)
  - [JDBC Integration](#jdbc-integration)
  - [HikariCP Connection Pool](#hikaricp-connection-pool)
- [Аутентификация](#аутентификация)
  - [Basic Authentication](#basic-authentication)
  - [JWT Authentication](#jwt-authentication)
- [CORS](#cors)
  - [CORS Configuration](#cors-configuration)
- [File Upload](#file-upload)
  - [Multipart Form Data](#multipart-form-data)
- [Тестирование](#тестирование)
  - [Unit Testing](#unit-testing)
  - [Integration Testing](#integration-testing)
- [Шаблоны](#шаблоны)
  - [Thymeleaf](#thymeleaf)
  - [Velocity](#velocity)
- [Производительность](#производительность)
  - [Кеширование](#кеширование)
- [Развертывание](#развертывание)
  - [Fat JAR](#fat-jar)
  - [Docker](#docker)
- [Dependency Injection](#dependency-injection)
  - [Guice Integration](#guice-integration)
- [Async Processing](#async-processing)
  - [CompletableFuture](#completablefuture)
- [Advanced Routing](#advanced-routing)
  - [Route Groups](#route-groups)
  - [Path Matchers](#path-matchers)
- [Request/Response Advanced](#requestresponse-advanced)
  - [Request Headers](#request-headers)
  - [Response Headers](#response-headers)
  - [Cookies](#cookies)
- [Database Integration](#database-integration)
  - [JDBC with Connection Pool](#jdbc-with-connection-pool)
- [Testing Advanced](#testing-advanced)
  - [Mocking Services](#mocking-services)
- [Production Deployment](#production-deployment)
  - [JVM Tuning](#jvm-tuning)
  - [Graceful Shutdown](#graceful-shutdown)
- [Решение проблем](#решение-проблем)
  - [Common Issues](#common-issues)
- [Advanced Features](#advanced-features)
  - [Custom Response Mappers](#custom-response-mappers)
  - [Route Groups](#route-groups-1)
  - [Content Negotiation](#content-negotiation)
- [Лучшие практики](#лучшие-практики)
  - [1. Структура проекта](#1-структура-проекта)
  - [2. Error Handling](#2-error-handling)
  - [3. Logging](#3-logging)
- [Performance Optimization](#performance-optimization)
  - [Connection Pooling](#connection-pooling)
  - [Caching](#caching)
- [Advanced Topics](#advanced-topics)
  - [Custom Access Manager](#custom-access-manager)
  - [Event Handlers](#event-handlers)
  - [Plugin System](#plugin-system)
- [Production Checklist](#production-checklist)
  - [Performance](#performance)
  - [Security](#security)
  - [Deployment](#deployment)
- [Real-World Examples](#real-world-examples)
  - [Complete REST API](#complete-rest-api)
- [Advanced Patterns](#advanced-patterns)
  - [Service Layer Pattern](#service-layer-pattern)
  - [Repository Pattern](#repository-pattern)
- [Common Pitfalls and Solutions](#common-pitfalls-and-solutions)
  - [1. Memory Leaks](#1-memory-leaks)
  - [2. Connection Pool Issues](#2-connection-pool-issues)
  - [3. Error Handling](#3-error-handling)
- [Additional Resources](#additional-resources)
  - [Community](#community)
  - [Tools](#tools)
  - [Best Practices Summary](#best-practices-summary)
- [Summary](#summary)
  - [Ключевые преимущества](#ключевые-преимущества)
  - [Когда использовать Javalin](#когда-использовать-javalin)
- [Detailed Examples](#detailed-examples)
  - [Complete Application Structure](#complete-application-structure)
  - [Service Layer Implementation](#service-layer-implementation)
  - [Advanced Patterns](#advanced-patterns-1)
    - [Request/Response Interceptors](#requestresponse-interceptors)
    - [Database Integration](#database-integration-1)
    - [Authentication Middleware](#authentication-middleware)
    - [Caching Strategy](#caching-strategy)
    - [Dependency Injection Pattern](#dependency-injection-pattern)
- [Production Deployment](#production-deployment-1)
  - [Docker Configuration](#docker-configuration)
  - [Environment Configuration](#environment-configuration)
  - [Health Check Endpoint](#health-check-endpoint)
  - [Metrics Integration](#metrics-integration)
- [Advanced Features](#advanced-features-1)
  - [WebSocket Support](#websocket-support)
  - [Server-Sent Events (SSE)](#server-sent-events-sse)
  - [File Upload](#file-upload-1)
- [Решение проблем](#решение-проблем-1)
  - [Common Issues](#common-issues-1)
- [Migration Guide](#migration-guide)
  - [From Spark Java](#from-spark-java)
  - [From Spring Boot](#from-spring-boot)
- [Real-World Examples](#real-world-examples-1)
  - [RESTful API with CRUD Operations](#restful-api-with-crud-operations)
  - [API Versioning](#api-versioning)
- [Testing Strategies](#testing-strategies)
  - [Unit Testing](#unit-testing-1)
  - [Integration Testing](#integration-testing-1)
- [Лучшие практики (сводка)](#лучшие-практики-сводка)
- [См. также](#см-также)

## Введение в Javalin

**Javalin** — это легковесный веб-фреймворк для **Java** и **Kotlin**, который предоставляет простой и интуитивный **API** для создания **RESTful API** и веб-приложений.

### Основные особенности

- **Легковесный**: Минимальные зависимости
- **Простой API**: Интуитивный и понятный
- **Kotlin и Java**: Поддержка обоих языков
- **Jetty**: Встроенный **Jetty** сервер
- **WebSocket**: Встроенная поддержка **WebSocket**
- **Валидация**: Встроенная валидация запросов


## Установка и настройка

### Maven зависимость

```xml
<!-- Зависимость Javalin для Maven -->
<dependency>
    <groupId>io.javalin</groupId>
    <artifactId>javalin</artifactId>
    <version>5.6.1</version>
</dependency>
```

### Gradle зависимость

```gradle
// Зависимость Javalin для Gradle
dependencies {
    implementation 'io.javalin:javalin:5.6.1'
}
```


## Базовое использование

### Простой HTTP сервер

```java
// Создание и запуск приложения Javalin на порту 7000
import io.javalin.Javalin;

public class HelloWorld {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.get("/", ctx -> ctx.result("Hello World"));
    }
}
```

### Различные HTTP методы

```java
// Создание и запуск приложения на порту 7000 (минимальный пример)
Javalin app = Javalin.create().start(7000);

app.get("/users", ctx -> {
    ctx.json(getAllUsers());
});

app.post("/users", ctx -> {
    User user = ctx.bodyAsClass(User.class);
    createUser(user);
    ctx.status(201);
});

app.put("/users/:id", ctx -> {
    String id = ctx.pathParam("id");
    User user = ctx.bodyAsClass(User.class);
    updateUser(id, user);
});

app.delete("/users/:id", ctx -> {
    String id = ctx.pathParam("id");
    deleteUser(id);
    ctx.status(204);
});
```

### Параметры пути

```java
// Path-параметр :id из URL
app.get("/users/:id", ctx -> {
    String id = ctx.pathParam("id");
    User user = getUserById(id);
    ctx.json(user);
});
```

### Query параметры

```java
// Query-параметр q из URL
app.get("/search", ctx -> {
    String query = ctx.queryParam("q");
    List<Result> results = search(query);
    ctx.json(results);
});
```

### Тело запроса

```java
app.post("/users", ctx -> {
    User user = ctx.bodyAsClass(User.class);
    // Обработка пользователя
    ctx.json(user);
});
```


## Контекст (Context)

### Получение заголовков

```java
app.get("/api", ctx -> {
    String token = ctx.header("Authorization");
    // Использование токена
});
```

### Установка заголовков

```java
app.get("/api", ctx -> {
    ctx.header("X-Custom-Header", "value");
    ctx.json(data);
});
```

### Установка статус кода

```java
app.get("/api", ctx -> {
    ctx.status(201);
    ctx.json(data);
});
```

### Редирект

```java
app.get("/old", ctx -> {
    ctx.redirect("/new");
});
```


## JSON обработка

### Отправка JSON

```java
app.get("/user", ctx -> {
    User user = new User(1, "John Doe");
    ctx.json(user);
});
```

### Получение JSON

```java
app.post("/user", ctx -> {
    User user = ctx.bodyAsClass(User.class);
    // Обработка пользователя
    ctx.json(user);
});
```


## Валидация

### Валидация запросов

```java
// Обработка ошибок валидации Javalin
import io.javalin.validation.ValidationException;

app.post("/users", ctx -> {
    try {
        String name = ctx.queryParamAsClass("name")
            .check(n -> n.length() > 0, "Name cannot be empty")
            .get();
        // Обработка
    } catch (ValidationException e) {
        ctx.status(400).json(e.getErrors());
    }
});
```


## Обработка ошибок

### Обработка `404`

```java
// Обработчик ошибки 404
app.error(404, ctx -> {
    ctx.json("{\"error\":\"Not Found\"}");
});
```

### Обработка исключений

```java
// Глобальный перехват исключений
app.exception(Exception.class, (e, ctx) -> {
    ctx.status(500);
    ctx.json("{\"error\":\"" + e.getMessage() + "\"}");
});
```


## Конфигурация

### Настройка приложения

```java
// Настройка приложения: CORS, логирование, content-type
Javalin app = Javalin.create(config -> {
    config.showJavalinBanner = false;
    config.defaultContentType = "application/json";
    config.enableCorsForAllOrigins();
    config.enableDevLogging();
    config.requestLogger((ctx, ms) -> {
        System.out.println(ctx.method() + " " + ctx.path() + " took " + ms + "ms");
    });
}).start(7000);
```

### Пользовательские настройки

```java
Javalin app = Javalin.create(config -> {
    config.contextPath = "/api";
    config.maxRequestSize = 10_000_000; // 10MB
    config.staticFiles.add("/public");
    config.staticFiles.externalLocation("/path/to/files");
}).start(7000);
```


## Middleware

### Before Handlers

```java
// Before handler: выполняется до маршрута
app.before(ctx -> {
    System.out.println("Before: " + ctx.path());
});

app.before("/api/*", ctx -> {
    String token = ctx.header("Authorization");
    if (token == null) {
        ctx.status(401).result("Unauthorized");
    }
});
```

### After Handlers

```java
// After handler: заголовок X-Response-Time
app.after(ctx -> {
    ctx.header("X-Response-Time", String.valueOf(ctx.responseTime()));
});

app.after("/api/*", ctx -> {
    ctx.header("X-Custom-Header", "value");
});
```

### Exception Handlers

```java
app.exception(IllegalArgumentException.class, (e, ctx) -> {
    ctx.status(400).json("{\"error\":\"" + e.getMessage() + "\"}");
});

app.exception(Exception.class, (e, ctx) -> {
    ctx.status(500).json("{\"error\":\"Internal Server Error\"}");
});
```

### Error Handlers

```java
app.error(404, ctx -> {
    ctx.json("{\"error\":\"Not Found\"}");
});

app.error(500, ctx -> {
    ctx.json("{\"error\":\"Internal Server Error\"}");
});
```


## Сессии

### Использование сессий

```java
import io.javalin.http.Context;

app.post("/login", ctx -> {
    String username = ctx.formParam("username");
    ctx.sessionAttribute("username", username);
    ctx.json("{\"status\":\"logged in\"}");
});

app.get("/profile", ctx -> {
    String username = ctx.sessionAttribute("username");
    if (username == null) {
        ctx.redirect("/login");
    } else {
        ctx.json("{\"username\":\"" + username + "\"}");
    }
});

app.post("/logout", ctx -> {
    ctx.req().getSession().invalidate();
    ctx.redirect("/login");
});
```


## WebSocket

### WebSocket Server

```java
app.ws("/ws", ws -> {
    ws.onConnect(ctx -> {
        System.out.println("Connected: " + ctx.sessionId());
    });

    ws.onMessage(ctx -> {
        String message = ctx.message();
        System.out.println("Received: " + message);
        ctx.send("Echo: " + message);
    });

    ws.onClose(ctx -> {
        System.out.println("Closed: " + ctx.sessionId());
    });

    ws.onError(ctx -> {
        System.err.println("Error: " + ctx.error());
    });
});
```

### WebSocket с JSON

```java
app.ws("/ws", ws -> {
    ws.onMessage(ctx -> {
        JsonObject json = new JsonObject(ctx.message());
        String type = json.getString("type");

        if ("chat".equals(type)) {
            JsonObject response = new JsonObject()
                .put("type", "message")
                .put("text", json.getString("text"));
            ctx.send(response.encode());
        }
    });
});
```


## База данных

### JDBC Integration

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseExample {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mydb";

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/users", ctx -> {
            List<User> users = new ArrayList<>();

            try (Connection conn = DriverManager.getConnection(DB_URL, "user", "password");
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    users.add(user);
                }
            }

            ctx.json(users);
        });
    }
}
```

### HikariCP Connection Pool

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPoolExample {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("user");
        config.setPassword("password");
        dataSource = new HikariDataSource(config);
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/users/:id", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {

                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    ctx.json(user);
                } else {
                    ctx.status(404);
                }
            }
        });
    }
}
```


## Аутентификация

### Basic Authentication

```java
import io.javalin.security.BasicAuthCredentials;

app.before("/api/*", ctx -> {
    String authHeader = ctx.header("Authorization");
    if (authHeader == null || !authHeader.startsWith("Basic ")) {
        ctx.header("WWW-Authenticate", "Basic");
        ctx.status(401).result("Unauthorized");
    } else {
        String credentials = new String(Base64.getDecoder()
            .decode(authHeader.substring(6)));
        String[] parts = credentials.split(":");
        if (parts.length == 2 && authenticate(parts[0], parts[1])) {
            ctx.attribute("username", parts[0]);
        } else {
            ctx.status(401).result("Unauthorized");
        }
    }
});
```

### JWT Authentication

```java
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTExample {
    private static final Algorithm algorithm = Algorithm.HMAC256("secret");

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.post("/login", ctx -> {
            String username = ctx.formParam("username");
            String password = ctx.formParam("password");

            if (authenticate(username, password)) {
                String token = JWT.create()
                    .withSubject(username)
                    .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                    .sign(algorithm);
                ctx.json("{\"token\":\"" + token + "\"}");
            } else {
                ctx.status(401).json("{\"error\":\"Invalid credentials\"}");
            }
        });

        app.before("/api/*", ctx -> {
            String token = ctx.header("Authorization");
            if (token == null || !isValidToken(token)) {
                ctx.status(401).json("{\"error\":\"Unauthorized\"}");
            }
        });
    }
}
```


## CORS

### CORS Configuration

```java
app.before(ctx -> {
    ctx.header("Access-Control-Allow-Origin", "*");
    ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
});

app.options("/*", ctx -> {
    ctx.status(200);
});
```


## File Upload

### Multipart Form Data

```java
app.post("/upload", ctx -> {
    ctx.uploadedFiles("files").forEach(file -> {
        try {
            Files.copy(file.content(),
                Paths.get("uploads/" + file.filename()),
                StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
    ctx.json("{\"status\":\"uploaded\"}");
});
```


## Тестирование

### Unit Testing

```java
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;

public class RouteTest {
    @Test
    void testRoute() {
        JavalinTest.test(app, (server, client) -> {
            HttpResponse response = client.get("/users");
            assertEquals(200, response.code());
        });
    }
}
```

### Integration Testing

```java
import io.javalin.testtools.HttpClient;
import org.junit.jupiter.api.Test;

public class IntegrationTest {
    @Test
    void testEndpoint() {
        Javalin app = Javalin.create().start(0);
        app.get("/test", ctx -> ctx.json("OK"));

        HttpClient client = HttpClient.create(app);
        String response = client.get("/test");
        assertEquals("{\"result\":\"OK\"}", response);
    }
}
```


## Шаблоны

### Thymeleaf

```java
import io.javalin.rendering.template.JavalinThymeleaf;

app.get("/", ctx -> {
    Map<String, Object> model = new HashMap<>();
    model.put("name", "World");
    ctx.render("index.html", model);
});
```

### Velocity

```java
import io.javalin.rendering.template.JavalinVelocity;

app.get("/", ctx -> {
    Map<String, Object> model = new HashMap<>();
    model.put("name", "World");
    ctx.render("index.vm", model);
});
```


## Производительность

### Кеширование

```java
import java.util.concurrent.ConcurrentHashMap;

public class CachingExample {
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/data/:key", ctx -> {
            String key = ctx.pathParam("key");
            String value = cache.get(key);

            if (value == null) {
                value = fetchData(key);
                cache.put(key, value);
            }

            ctx.json(value);
        });
    }
}
```


## Развертывание

### Fat JAR

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Docker

```dockerfile
FROM openjdk:11-jre-slim
COPY target/myapp.jar /app/app.jar
EXPOSE 7000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```


## Dependency Injection

### Guice Integration

```java
import com.google.inject.Guice;
import com.google.inject.Injector;

public class GuiceExample {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MyModule());

        Javalin app = Javalin.create(config -> {
            config.plugins.register(new GuicePlugin(injector));
        }).start(7000);

        app.get("/users", ctx -> {
            UserService userService = ctx.appAttribute(Injector.class)
                .getInstance(UserService.class);
            ctx.json(userService.getAllUsers());
        });
    }
}
```


## Async Processing

### CompletableFuture

```java
app.get("/async", ctx -> {
    CompletableFuture.supplyAsync(() -> {
        // Асинхронная операция
        return fetchData();
    }).thenAccept(data -> {
        ctx.json(data);
    }).exceptionally(throwable -> {
        ctx.status(500).json("{\"error\":\"" + throwable.getMessage() + "\"}");
        return null;
    });
});
```


## Advanced Routing

### Route Groups

```java
app.routes(() -> {
    path("api", () -> {
        path("v1", () -> {
            get("users", ctx -> ctx.json(getAllUsers()));
            post("users", ctx -> {
                User user = ctx.bodyAsClass(User.class);
                ctx.json(createUser(user));
            });
        });
    });
});
```

### Path Matchers

```java
app.get("/users/{userId}/posts/{postId}", ctx -> {
    String userId = ctx.pathParam("userId");
    String postId = ctx.pathParam("postId");
    Post post = getPost(userId, postId);
    ctx.json(post);
});
```


## Request/Response Advanced

### Request Headers

```java
app.get("/api", ctx -> {
    String userAgent = ctx.header("User-Agent");
    String accept = ctx.header("Accept");
    String authorization = ctx.header("Authorization");

    // Обработка заголовков
    ctx.json("OK");
});
```

### Response Headers

```java
app.get("/api", ctx -> {
    ctx.header("X-Custom-Header", "value");
    ctx.header("X-Request-ID", UUID.randomUUID().toString());
    ctx.json("{\"status\":\"OK\"}");
});
```

### Cookies

```java
app.get("/set-cookie", ctx -> {
    ctx.cookie("token", "abc123", 3600);
    ctx.json("Cookie set");
});

app.get("/get-cookie", ctx -> {
    String token = ctx.cookie("token");
    ctx.json("{\"token\":\"" + token + "\"}");
});
```


## Database Integration

### JDBC with Connection Pool

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseExample {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("user");
        config.setPassword("password");
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/users/:id", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE id = ?")) {

                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setName(rs.getString("name"));
                    ctx.json(user);
                } else {
                    ctx.status(404);
                }
            }
        });
    }
}
```


## Testing Advanced

### Mocking Services

```java
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RouteTest {

    @Mock
    private UserService userService;

    @Test
    void testRoute() {
        when(userService.getUser(1L)).thenReturn(new User(1L, "John"));

        Javalin app = Javalin.create().start(0);
        app.get("/users/:id", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            ctx.json(userService.getUser(id));
        });

        // Тестирование
    }
}
```


## Production Deployment

### JVM Tuning

```bash
java -Xms256m -Xmx1g \
     -XX:+UseG1GC \
     -jar myapp.jar
```

### Graceful Shutdown

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    app.stop();
}));
```


## Решение проблем

### Common Issues

1. **Port `Already in` Use**: Изменить порт
2. **Memory Leaks**: Проверить **connection pools**
3. **Slow Responses**: Профилировать код


## Advanced Features

### Custom Response Mappers

```java
import io.javalin.http.Context;
import io.javalin.plugin.json.JsonMapper;

public class CustomJsonMapper implements JsonMapper {
    private final Gson gson = new Gson();

    @Override
    public String toJsonString(Object obj, Context ctx) {
        return gson.toJson(obj);
    }

    @Override
    public <T> T fromJsonString(String json, Class<T> targetClass, Context ctx) {
        return gson.fromJson(json, targetClass);
    }
}

// Использование
Javalin app = Javalin.create(config -> {
    config.jsonMapper(new CustomJsonMapper());
}).start(7000);
```

### Route Groups

```java
app.routes(() -> {
    path("api", () -> {
        path("v1", () -> {
            get("users", ctx -> ctx.json(getAllUsers()));
            post("users", ctx -> {
                User user = ctx.bodyAsClass(User.class);
                ctx.json(createUser(user));
            });
        });
    });
});
```

### Content Negotiation

```java
app.get("/data", ctx -> {
    String accept = ctx.header("Accept");
    if (accept != null && accept.contains("application/xml")) {
        ctx.contentType("application/xml");
        ctx.result(convertToXML(data));
    } else {
        ctx.json(data);
    }
});
```


## Лучшие практики

### 1. Структура проекта

```text
project/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           ├── Main.java
│       │           ├── routes/
│       │           │   └── UserRoutes.java
│       │           ├── controllers/
│       │           │   └── UserController.java
│       │           ├── services/
│       │           │   └── UserService.java
│       │           └── models/
│       │               └── User.java
│       └── resources/
└── pom.xml
```

### 2. Error Handling

```java
app.exception(IllegalArgumentException.class, (e, ctx) -> {
    ctx.status(400).json("{\"error\":\"" + e.getMessage() + "\"}");
});

app.exception(Exception.class, (e, ctx) -> {
    logger.error("Error", e);
    ctx.status(500).json("{\"error\":\"Internal Server Error\"}");
});
```

### 3. Logging

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingExample {
    private static final Logger logger = LoggerFactory.getLogger(LoggingExample.class);

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.requestLogger((ctx, ms) -> {
                logger.info("{} {} took {}ms", ctx.method(), ctx.path(), ms);
            });
        }).start(7000);
    }
}
```


## Performance Optimization

### Connection Pooling

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class PerformanceExample {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setUsername("user");
        config.setPassword("password");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        dataSource = new HikariDataSource(config);
    }

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/users/:id", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE id = ?")) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    ctx.json(user);
                } else {
                    ctx.status(404);
                }
            }
        });
    }
}
```

### Caching

```java
import java.util.concurrent.ConcurrentHashMap;

public class CachingExample {
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/data/:key", ctx -> {
            String key = ctx.pathParam("key");
            String value = cache.get(key);
            if (value == null) {
                value = fetchData(key);
                cache.put(key, value);
            }
            ctx.json(value);
        });
    }
}
```


## Advanced Topics

### Custom Access Manager

```java
import io.javalin.security.AccessManager;
import io.javalin.security.RouteRole;

public class CustomAccessManager implements AccessManager {
    @Override
    public void manage(Handler handler, Context ctx, Set<RouteRole> permittedRoles) {
        String role = ctx.sessionAttribute("role");
        if (role != null && permittedRoles.contains(Role.valueOf(role))) {
            handler.handle(ctx);
        } else {
            ctx.status(403).result("Forbidden");
        }
    }
}

// Использование
Javalin app = Javalin.create(config -> {
    config.accessManager(new CustomAccessManager());
}).start(7000);

app.get("/admin", ctx -> ctx.result("Admin area"), Role.ADMIN);
```

### Event Handlers

```java
app.events(event -> {
    event.serverStarted(() -> {
        System.out.println("Server started");
    });

    event.serverStopping(() -> {
        System.out.println("Server stopping");
    });

    event.serverStopped(() -> {
        System.out.println("Server stopped");
    });
});
```

### Plugin System

```java
import io.javalin.plugin.Plugin;

public class MyPlugin implements Plugin {
    @Override
    public void apply(Javalin app) {
        app.before(ctx -> {
            // Логика плагина
        });
    }
}

// Использование
Javalin app = Javalin.create(config -> {
    config.plugins.register(new MyPlugin());
}).start(7000);
```


## Production Checklist

### Performance

- [ ] Настроены **connection pools**
- [ ] Оптимизированы запросы
- [ ] Настроено кеширование
- [ ] Настроен **thread pool**
- [ ] Оптимизированы статические файлы

### Security

- [ ] Настроена аутентификация
- [ ] Настроена авторизация
- [ ] Настроены **CORS**
- [ ] Настроены **security headers**
- [ ] Проведен **security audit**

### Deployment

- [ ] Настроен CI/CD
- [ ] Настроен **Docker**
- [ ] Настроен мониторинг
- [ ] Настроен **backup**
- [ ] Настроен **graceful shutdown**


## Real-World Examples

### Complete REST API

```java
public class UserAPI {
    private static final UserService userService = new UserService();

    public static void setup(Javalin app) {
        // GET all users
        app.get("/api/users", ctx -> {
            int page = Integer.parseInt(ctx.queryParam("page") != null ?
                ctx.queryParam("page") : "0");
            int size = Integer.parseInt(ctx.queryParam("size") != null ?
                ctx.queryParam("size") : "20");
            ctx.json(userService.getAllUsers(page, size));
        });

        // GET user by id
        app.get("/api/users/:id", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            User user = userService.getUser(id);
            if (user != null) {
                ctx.json(user);
            } else {
                ctx.status(404).json("{\"error\":\"User not found\"}");
            }
        });

        // POST create user
        app.post("/api/users", ctx -> {
            User user = ctx.bodyAsClass(User.class);
            User created = userService.createUser(user);
            ctx.status(201).json(created);
        });

        // PUT update user
        app.put("/api/users/:id", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            User user = ctx.bodyAsClass(User.class);
            User updated = userService.updateUser(id, user);
            if (updated != null) {
                ctx.json(updated);
            } else {
                ctx.status(404).json("{\"error\":\"User not found\"}");
            }
        });

        // DELETE user
        app.delete("/api/users/:id", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            if (userService.deleteUser(id)) {
                ctx.status(204);
            } else {
                ctx.status(404).json("{\"error\":\"User not found\"}");
            }
        });
    }
}
```


## Advanced Patterns

### Service Layer Pattern

```java
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(int page, int size) {
        return userRepository.findAll(page, size);
    }

    public User getUser(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User createUser(User user) {
        validateUser(user);
        return userRepository.save(user);
    }

    public User updateUser(long id, User user) {
        User existing = getUser(id);
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        return userRepository.save(existing);
    }

    public boolean deleteUser(long id) {
        return userRepository.delete(id);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ValidationException("Name is required");
        }
    }
}
```

### Repository Pattern

```java
public interface UserRepository {
    List<User> findAll(int page, int size);
    Optional<User> findById(long id);
    User save(User user);
    boolean delete(long id);
}

public class UserRepositoryImpl implements UserRepository {
    private final DataSource dataSource;

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> findAll(int page, int size) {
        // Реализация
        return null;
    }

    // Реализация остальных методов
}
```


## Common Pitfalls and Solutions

### 1. Memory Leaks

**Проблема**: Утечки памяти

**Решение**:
```java
app.events(event -> {
    event.serverStopped(() -> {
        // Очистка ресурсов
        if (dataSource != null) {
            dataSource.close();
        }
    });
});
```

### 2. Connection Pool Issues

**Проблема**: Проблемы с пулом соединений

**Решение**:
```java
HikariConfig config = new HikariConfig();
config.setMaximumPoolSize(20);
config.setMinimumIdle(5);
config.setConnectionTimeout(30000);
config.setIdleTimeout(600000);
config.setMaxLifetime(1800000);
```

### 3. Error Handling

**Проблема**: Неправильная обработка ошибок

**Решение**:
```java
app.exception(IllegalArgumentException.class, (e, ctx) -> {
    ctx.status(400).json("{\"error\":\"" + e.getMessage() + "\"}");
});

app.exception(Exception.class, (e, ctx) -> {
    logger.error("Error", e);
    ctx.status(500).json("{\"error\":\"Internal Server Error\"}");
});
```


## Additional Resources

### Community

- **Stack Overflow**: тег `javalin`
- **GitHub Discussions**: обсуждения и вопросы

### Tools

- **Javalin Maven Plugin**
- **Javalin Gradle Plugin**

### Best Practices Summary

1. **Структура проекта**: Разделение на слои
2. **Error Handling**: Централизованная обработка ошибок
3. **Logging**: Использование **SLF4J**
4. **Testing**: **Unit** и **integration** тесты
5. **Security**: **JWT**, **Basic Auth**
6. **Performance**: **Connection pooling**, кеширование
7. **Deployment**: **Docker**, CI/CD


## Summary

**Javalin** — это легковесный веб-фреймворк для **Java** и **Kotlin**, который предоставляет простой и интуитивный **API** для создания **RESTful API** и веб-приложений.

### Ключевые преимущества

- **Простота**: Интуитивный **API**
- **Kotlin и Java**: Поддержка обоих языков
- **WebSocket**: Встроенная поддержка **WebSocket**
- **Валидация**: Встроенная валидация запросов

### Когда использовать Javalin

- **RESTful API**
- Веб-приложения
- Микросервисы
- Приложения на **Kotlin**


## Detailed Examples

### Complete Application Structure

```java
// Main.java
public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.enableCorsForAllOrigins();
            config.requestLogger((ctx, ms) -> {
                logger.info("{} {} took {}ms", ctx.method(), ctx.path(), ms);
            });
        }).start(7000);

        // Настройка routes
        UserRoutes.setup(app);
        PostRoutes.setup(app);

        // Настройка middleware
        setupMiddleware(app);

        // Настройка обработки ошибок
        setupErrorHandling(app);
    }

    private static void setupMiddleware(Javalin app) {
        app.before(ctx -> {
            // Логирование
            logger.debug("Request: {} {}", ctx.method(), ctx.path());
        });

        app.after(ctx -> {
            ctx.header("X-Response-Time", String.valueOf(ctx.responseTime()));
        });
    }

    private static void setupErrorHandling(Javalin app) {
        app.exception(ValidationException.class, (e, ctx) -> {
            ctx.status(400).json("{\"error\":\"" + e.getMessage() + "\"}");
        });

        app.exception(Exception.class, (e, ctx) -> {
            logger.error("Error", e);
            ctx.status(500).json("{\"error\":\"Internal Server Error\"}");
        });
    }
}
```

### Service Layer Implementation

```java
public class UserService {
    private final UserRepository userRepository;
    private final Map<String, User> cache = new ConcurrentHashMap<>();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(int page, int size) {
        return userRepository.findAll(page, size);
    }

    public User getUser(long id) {
        String key = "user:" + id;
        User user = cache.get(key);
        if (user == null) {
            user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
            cache.put(key, user);
        }
        return user;
    }

    public User createUser(User user) {
        validateUser(user);
        User created = userRepository.save(user);
        cache.put("user:" + created.getId(), created);
        return created;
    }

    public User updateUser(long id, User user) {
        User existing = getUser(id);
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        User updated = userRepository.save(existing);
        cache.put("user:" + updated.getId(), updated);
        return updated;
    }

    public boolean deleteUser(long id) {
        boolean deleted = userRepository.delete(id);
        if (deleted) {
            cache.remove("user:" + id);
        }
        return deleted;
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ValidationException("Name is required");
        }
        if (user.getEmail() == null || !isValidEmail(user.getEmail())) {
            throw new ValidationException("Valid email is required");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
```

### Advanced Patterns

#### Request/Response Interceptors

```java
public class RequestInterceptor {
    public static void setup(Javalin app) {
        app.before(ctx -> {
            // Timing
            ctx.attribute("startTime", System.currentTimeMillis());

            // Rate limiting
            String ip = ctx.ip();
            if (isRateLimited(ip)) {
                ctx.status(429).result("Too Many Requests");
                return;
            }
        });

        app.after(ctx -> {
            // Calculate duration
            Long startTime = ctx.attribute("startTime");
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                ctx.header("X-Response-Time", duration + "ms");
            }
        });
    }

    private static boolean isRateLimited(String ip) {
        // Implement rate limiting logic
        return false;
    }
}
```

#### Database Integration

```java
public class DatabaseManager {
    private static Connection connection;

    public static void initialize() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/mydb",
                "user",
                "password"
            );
        } catch (Exception e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.error("Error closing connection", e);
        }
    }
}
```

#### Authentication Middleware

```java
public class AuthMiddleware {
    public static void setup(Javalin app) {
        app.before("/api/*", ctx -> {
            String token = ctx.header("Authorization");
            if (token == null || !isValidToken(token)) {
                ctx.status(401).result("Unauthorized");
                return;
            }
            ctx.attribute("user", getUserFromToken(token));
        });
    }

    private static boolean isValidToken(String token) {
        // Implement token validation
        return token != null && token.startsWith("Bearer ");
    }

    private static User getUserFromToken(String token) {
        // Extract and validate user from token
        return new User();
    }
}
```

#### Caching Strategy

```java
public class CacheManager {
    private static final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public static <T> Optional<T> get(String key, Class<T> type) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return Optional.of(type.cast(entry.getValue()));
        }
        cache.remove(key);
        return Optional.empty();
    }

    public static void put(String key, Object value, long ttlMillis) {
        cache.put(key, new CacheEntry(value, System.currentTimeMillis() + ttlMillis));
    }

    public static void invalidate(String key) {
        cache.remove(key);
    }

    public static void clear() {
        cache.clear();
    }

    private static class CacheEntry {
        private final Object value;
        private final long expiry;

        CacheEntry(Object value, long expiry) {
            this.value = value;
            this.expiry = expiry;
        }

        Object getValue() {
            return value;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiry;
        }
    }
}
```

#### Dependency Injection Pattern

```java
public class ServiceRegistry {
    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    public static <T> void register(Class<T> type, T instance) {
        services.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        return (T) services.get(type);
    }
}

// Usage
public class UserRoutes {
    public static void setup(Javalin app) {
        UserService userService = ServiceRegistry.get(UserService.class);

        app.get("/api/users", ctx -> {
            List<User> users = userService.getAllUsers(0, 20);
            ctx.json(users);
        });
    }
}
```


## Production Deployment

### Docker Configuration

```dockerfile
FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/my-app.jar app.jar
EXPOSE 7000
CMD ["java", "-jar", "app.jar"]
```

### Environment Configuration

```java
public class Config {
    public static int getPort() {
        String port = System.getenv("PORT");
        return port != null ? Integer.parseInt(port) : 7000;
    }

    public static String getDatabaseUrl() {
        return System.getenv("DATABASE_URL");
    }

    public static String getJwtSecret() {
        return System.getenv("JWT_SECRET");
    }
}
```

### Health Check Endpoint

```java
app.get("/health", ctx -> {
    Map<String, Object> health = new HashMap<>();
    health.put("status", "UP");
    health.put("timestamp", System.currentTimeMillis());
    ctx.json(health);
});
```

### Metrics Integration

```java
public class MetricsPlugin implements Plugin {
    @Override
    public void register(Javalin app) {
        app.after(ctx -> {
            String path = ctx.path();
            String method = ctx.method();
            int status = ctx.status();

            // Record metrics
            metrics.counter("http.requests",
                "method", method,
                "path", path,
                "status", String.valueOf(status)
            ).increment();
        });
    }
}
```

## Advanced Features

### WebSocket Support

```java
app.ws("/ws", ws -> {
    ws.onConnect(ctx -> {
        System.out.println("Connected: " + ctx.sessionId());
    });

    ws.onMessage(ctx -> {
        String message = ctx.message();
        ctx.send("Echo: " + message);
    });

    ws.onClose(ctx -> {
        System.out.println("Closed: " + ctx.sessionId());
    });

    ws.onError(ctx -> {
        System.out.println("Error: " + ctx.error());
    });
});
```

### Server-Sent Events (SSE)

```java
app.get("/events", ctx -> {
    ctx.header("Content-Type", "text/event-stream");
    ctx.header("Cache-Control", "no-cache");

    ctx.sseClient().onConnect(client -> {
        client.send("Connected");
    });

    ctx.sseClient().onClose(client -> {
        System.out.println("SSE client disconnected");
    });
});
```

### File Upload

```java
app.post("/upload", ctx -> {
    ctx.uploadedFiles("files").forEach(file -> {
        String fileName = file.getFilename();
        InputStream content = file.getContent();

        // Save file
        Files.copy(content, Paths.get("uploads/" + fileName));

        ctx.result("File uploaded: " + fileName);
    });
});
```

## Решение проблем

### Common Issues

1. **Port `Already in` Use**: Убедитесь, что порт не занят другим приложением
2. **Static `Files Not` Found**: Проверьте путь к статическим файлам
3. **CORS Issues**: Настройте **CORS** заголовки правильно
4. **JSON `Parsing` Errors**: Убедитесь, что **JSON** валиден

## Migration Guide

### From Spark Java

```java
// Spark Java
get("/hello", (req, res) -> "Hello World");

// Javalin
app.get("/hello", ctx -> ctx.result("Hello World"));
```

### From Spring Boot

```java
// Spring Boot
@RestController
public class UserController {
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
}

// Javalin
app.get("/users", ctx -> {
    List<User> users = userService.getAllUsers();
    ctx.json(users);
});
```

## Real-World Examples

### RESTful API with CRUD Operations

```java
public class UserAPI {
    private final UserService userService;

    public UserAPI(UserService userService) {
        this.userService = userService;
    }

    public void setup(Javalin app) {
        // GET /users - Get all users
        app.get("/users", ctx -> {
            int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
            int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(20);
            List<User> users = userService.getAllUsers(page, size);
            ctx.json(users);
        });

        // GET /users/:id - Get user by ID
        app.get("/users/:id", ctx -> {
            long id = ctx.pathParamAsClass("id", Long.class).get();
            User user = userService.getUser(id)
                .orElseThrow(() -> new NotFoundResponse("User not found"));
            ctx.json(user);
        });

        // POST /users - Create new user
        app.post("/users", ctx -> {
            User user = ctx.bodyAsClass(User.class);
            User created = userService.createUser(user);
            ctx.status(201).json(created);
        });

        // PUT /users/:id - Update user
        app.put("/users/:id", ctx -> {
            long id = ctx.pathParamAsClass("id", Long.class).get();
            User user = ctx.bodyAsClass(User.class);
            User updated = userService.updateUser(id, user);
            ctx.json(updated);
        });

        // DELETE /users/:id - Delete user
        app.delete("/users/:id", ctx -> {
            long id = ctx.pathParamAsClass("id", Long.class).get();
            userService.deleteUser(id);
            ctx.status(204);
        });
    }
}
```

### API Versioning

```java
app.routes(() -> {
    path("/api/v1", () -> {
        path("/users", () -> {
            get(UserController::getAllUsers);
            get("/:id", UserController::getUser);
            post(UserController::createUser);
        });
    });

    path("/api/v2", () -> {
        path("/users", () -> {
            get(UserControllerV2::getAllUsers);
            get("/:id", UserControllerV2::getUser);
            post(UserControllerV2::createUser);
        });
    });
});
```

## Testing Strategies

### Unit Testing

```java
@Test
public void testUserCreation() {
    Javalin app = Javalin.create().start(0);
    UserService userService = new UserService();
    UserAPI userAPI = new UserAPI(userService);
    userAPI.setup(app);

    User user = new User();
    user.setName("John Doe");
    user.setEmail("john@example.com");

    String response = HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + app.port() + "/users"))
            .POST(HttpRequest.BodyPublishers.ofString(Jackson.toJsonString(user)))
            .build(),
            HttpResponse.BodyHandlers.ofString())
        .body();

    assertNotNull(response);
    app.stop();
}
```

### Integration Testing

```java
@Test
public void testUserAPI() {
    Javalin app = Javalin.create().start(0);
    UserService userService = new UserService();
    UserAPI userAPI = new UserAPI(userService);
    userAPI.setup(app);

    // Test GET /users
    String users = HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + app.port() + "/users"))
            .GET()
            .build(),
            HttpResponse.BodyHandlers.ofString())
        .body();

    assertNotNull(users);
    app.stop();
}
```

## Лучшие практики (сводка)

1. **Structure**: Организуйте код по модулям (routes, services, repositories)
2. **Error Handling**: Используйте **exception handlers** для централизованной обработки ошибок
3. **Validation**: Валидируйте все входные данные
4. **Security**: Реализуйте аутентификацию и авторизацию
5. **Performance**: Используйте кэширование для часто запрашиваемых данных
6. **Logging**: Логируйте все важные события
7. **Testing**: Пишите **unit** и **integration** тесты

## См. также

- [Dropwizard: Основы](../dropwizard/dropwizard-basics.md)
- [Micronaut: Actuator — Health Checks, Metrics и Endpoints](../micronaut/micronaut-actuator.md)
- [Micronaut: Основы](../micronaut/micronaut-basics.md)
- [Micronaut: Batch Processing — Job Processing и Scheduling](../micronaut/micronaut-batch.md)
- [Micronaut: Caching — Cache Abstraction и Redis Cache](../micronaut/micronaut-cache.md)
