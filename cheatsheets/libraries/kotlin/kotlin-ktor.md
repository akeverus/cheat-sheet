---
title: "Kotlin Ktor"
description: "Кратко: полное руководство по Ktor - асинхронному фреймворку для создания веб-серверов и клиентов на Kotlin. Рассматриваются маршрутизация, обработка запросов, аутентификация, WebSockets и развертывание."
tags:
  - libraries
  - kotlin
  - kotlin-ktor
type: "overview"
difficulty: "intermediate"
aliases:
  - "Kotlin Ktor"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kotlin Ktor

Кратко: полное руководство по **Ktor** — асинхронному фреймворку для создания веб-серверов и клиентов на **Kotlin**. Рассматриваются маршрутизация, обработка запросов, аутентификация, **WebSockets** и развертывание.

## Полезные ссылки

### Официальная документация
- [Ktor Documentation](https://ktor.io/docs/)
- [Ktor API Reference](https://api.ktor.io/)

### Обучающие материалы
- [Ktor Tutorial](https://ktor.io/docs/creating-http-apis.html)

### См. также
- [Основы Kotlin](../../languages/kotlin/kotlin-basics.md)
- [Корутины и асинхронность](../../languages/kotlin/kotlin-concurrency-basics.md)
- [Дополнительные темы Kotlin](../../languages/kotlin/kotlin-another.md)

- [Konfig](kotlin-konfig.md)
- [Kotlin Exposed](kotlin-exposed.md)
## Содержание

- [Введение в Ktor](#введение-в-ktor)
  - [Основные преимущества Ktor](#основные-преимущества-ktor)
  - [Архитектура Ktor](#архитектура-ktor)
- [Настройка проекта](#настройка-проекта)
  - [Gradle настройка](#gradle-настройка)
  - [Maven настройка](#maven-настройка)
- [Создание сервера](#создание-сервера)
  - [Базовый сервер](#базовый-сервер)
  - [Структура приложения](#структура-приложения)
  - [Конфигурация через application.conf](#конфигурация-через-applicationconf)
- [Маршрутизация](#маршрутизация)
  - [Базовые маршруты](#базовые-маршруты)
  - [Вложенные маршруты](#вложенные-маршруты)
  - [Параметры пути](#параметры-пути)
  - [Типизированные параметры](#типизированные-параметры)
  - [Query параметры](#query-параметры)
- [Обработка запросов и ответов](#обработка-запросов-и-ответов)
  - [Чтение тела запроса](#чтение-тела-запроса)
  - [Отправка ответов](#отправка-ответов)
  - [Статус коды](#статус-коды)
- [Middleware и Interceptors](#middleware-и-interceptors)
  - [Application Call Pipeline](#application-call-pipeline)
  - [Создание кастомного Interceptor](#создание-кастомного-interceptor)
- [Аутентификация и авторизация](#аутентификация-и-авторизация)
  - [Базовая аутентификация](#базовая-аутентификация)
  - [JWT аутентификация](#jwt-аутентификация)
  - [Авторизация](#авторизация)
- [Работа с JSON](#работа-с-json)
  - [Настройка JSON сериализации](#настройка-json-сериализации)
  - [Сериализация данных классов](#сериализация-данных-классов)
- [WebSockets](#websockets)
- [HTTP Client](#http-client)
- [Тестирование](#тестирование)
- [Развертывание](#развертывание)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Custom Plugins](#custom-plugins)
  - [Content Negotiation](#content-negotiation)
  - [Status Pages](#status-pages)
- [Аутентификация и авторизация](#аутентификация-и-авторизация-1)
  - [JWT Authentication](#jwt-authentication)
  - [Session Authentication](#session-authentication)
- [Работа с базами данных](#работа-с-базами-данных)
  - [Exposed ORM](#exposed-orm)
  - [Database Migrations](#database-migrations)
- [Тестирование Ktor приложений](#тестирование-ktor-приложений)
  - [Тестирование маршрутов](#тестирование-маршрутов)
  - [Мокирование зависимостей](#мокирование-зависимостей)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Кэширование ответов](#кэширование-ответов)
  - [Compression](#compression)
  - [Connection Pooling](#connection-pooling)
- [Мониторинг и логирование](#мониторинг-и-логирование)
  - [Structured Logging](#structured-logging)
  - [Метрики](#метрики)
- [Безопасность](#безопасность)
  - [CORS](#cors)
  - [Rate Limiting](#rate-limiting)
- [Продвинутые техники Ktor](#продвинутые-техники-ktor)
  - [Custom Routing DSL](#custom-routing-dsl)
  - [Dependency Injection](#dependency-injection)
- [Масштабирование Ktor приложений](#масштабирование-ktor-приложений)
  - [Горизонтальное масштабирование](#горизонтальное-масштабирование)
  - [Load Balancing](#load-balancing)
- [Продвинутые техники Ktor](#продвинутые-техники-ktor-1)
  - [Кастомные плагины и middleware](#кастомные-плагины-и-middleware)
  - [Работа с WebSockets](#работа-с-websockets)
- [Продвинутые техники Ktor](#продвинутые-техники-ktor-2)
  - [Работа с файлами и загрузками](#работа-с-файлами-и-загрузками)
  - [Работа с сессиями](#работа-с-сессиями)
- [Дополнительные техники Ktor](#дополнительные-техники-ktor)
  - [Работа с Content Negotiation](#работа-с-content-negotiation)
  - [Работа с Status Pages](#работа-с-status-pages)
- [Дополнительные техники Ktor](#дополнительные-техники-ktor-1)
  - [Работа с HTTP клиентом](#работа-с-http-клиентом)
  - [Работа с multipart запросами](#работа-с-multipart-запросами)
- [Дополнительные техники Ktor](#дополнительные-техники-ktor-2)
  - [Работа с middleware](#работа-с-middleware)
- [Лучшие практики](#лучшие-практики)
- [Практические примеры использования](#практические-примеры-использования)
  - [Создание REST API](#создание-rest-api)
  - [Обработка WebSocket соединений](#обработка-websocket-соединений)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Итоговые рекомендации](#итоговые-рекомендации)

## Введение в Ktor

**Ktor** — это асинхронный фреймворк для создания веб-приложений на **Kotlin**, разработанный **JetBrains**. Он построен на корутинах и предоставляет легковесное решение для создания как серверных приложений, так и **HTTP**-клиентов.

### Основные преимущества Ktor

- **Асинхронность**: полная поддержка корутин **Kotlin** для неблокирующих операций
- **Легковесность**: минимальные зависимости, модульная архитектура
- **Гибкость**: легко настраиваемый через систему плагинов
- **Type-safe**: полная поддержка типов **Kotlin** и **null-safety**
- **Multiplatform**: поддержка **JVM**, **Native**, **JavaScript**

### Архитектура Ktor

**Ktor** использует модульную архитектуру, где функциональность добавляется через плагины (ранее назывались Features). Каждый плагин отвечает за определенную функциональность: маршрутизация, сериализация, аутентификация и т.д.

## Настройка проекта

### Gradle настройка

**Для создания **Ktor** сервера необходимо добавить соответствующие зависимости в `build.gradle.kts`:**

```kotlin
plugins {
    kotlin("jvm") version "1.9.0"
    application
}

dependencies {
    // Ktor сервер
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")

    // Плагины
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("io.ktor:ktor-server-auth:2.3.5")
    implementation("io.ktor:ktor-server-cors:2.3.5")

    // Логирование
    implementation("ch.qos.logback:logback-classic:1.4.11")
}
```

### Maven настройка

**Для **Maven** проекта зависимости добавляются в `pom.xml`:**

```xml
<dependencies>
    <dependency>
        <groupId>io.ktor</groupId>
        <artifactId>ktor-server-core</artifactId>
        <version>2.3.5</version>
    </dependency>
    <dependency>
        <groupId>io.ktor</groupId>
        <artifactId>ktor-server-netty</artifactId>
        <version>2.3.5</version>
    </dependency>
</dependencies>
```

## Создание сервера

### Базовый сервер

**Самый простой способ создать **Ktor** сервер — использовать встроенную функцию `embeddedServer`:**

```kotlin
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello, World!")
            }
        }
    }.start(wait = true)
}
```

Этот код создает **HTTP** сервер на порту `8080`, который отвечает текстом "**Hello**, **World**!" на **GET** запрос к корневому пути.

### Структура приложения

**Для более сложных приложений рекомендуется использовать модульную структуру:**

```kotlin
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/") {
            call.respondText("Hello, World!")
        }
    }
}

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}
```

Модуль приложения определяет конфигурацию и маршруты. Это позволяет легко тестировать приложение и переиспользовать конфигурацию.

### Конфигурация через application.conf

**Ktor** поддерживает конфигурацию через **HOCON** файл `application.conf`:**

```hocon
ktor {
    deployment {
        port = 8080
        host = "0.0.0.0"
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}
```

Это позволяет изменять конфигурацию без перекомпиляции кода.

## Маршрутизация

Маршрутизация в **Ktor** выполняется через **DSL**, который позволяет декларативно описывать структуру **API**.

### Базовые маршруты

```kotlin
routing {
    get("/") {
        call.respondText("Home")
    }

    get("/about") {
        call.respondText("About")
    }

    post("/users") {
        // Обработка POST запроса
    }
}
```

Каждый **HTTP** метод имеет соответствующую функцию в **DSL**: `get`, `post`, `put`, `delete`, `patch`, `head`, `options`.

### Вложенные маршруты

**Для организации сложной структуры **API** используются вложенные маршруты:**

```kotlin
routing {
    route("/api") {
        route("/v1") {
            get("/users") {
                call.respondText("Users v1")
            }
        }
        route("/v2") {
            get("/users") {
                call.respondText("Users v2")
            }
        }
    }
}
```

Вложенные маршруты позволяют группировать связанные **endpoints** и применять общие настройки к группе маршрутов.

### Параметры пути

**Ktor** поддерживает извлечение параметров из пути **URL**:**

```kotlin
get("/users/{id}") {
    val id = call.parameters["id"]
    call.respondText("User ID: $id")
}

get("/users/{userId}/posts/{postId}") {
    val userId = call.parameters["userId"]
    val postId = call.parameters["postId"]
    call.respondText("User $userId, Post $postId")
}
```

Параметры извлекаются из пути и доступны через `call.parameters`. Это позволяет создавать **RESTful API** с динамическими путями.

### Типизированные параметры

**Для автоматического преобразования параметров в нужные типы:**

```kotlin
get("/users/{id}") {
    val id = call.parameters["id"]?.toIntOrNull()
    if (id != null) {
        call.respondText("User ID: $id")
    } else {
        call.respond(HttpStatusCode.BadRequest, "Invalid ID")
    }
}
```

### Query параметры

**Доступ к **query** параметрам осуществляется через `call.request.queryParameters`:**

```kotlin
get("/search") {
    val query = call.request.queryParameters["q"]
    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
    call.respondText("Search: $query, Page: $page")
}
```

**Query** параметры используются для фильтрации, пагинации и других опциональных параметров запроса.

## Обработка запросов и ответов

### Чтение тела запроса

**Для чтения данных из тела запроса используется **Content Negotiation**:**

```kotlin
data class User(val name: String, val email: String)

post("/users") {
    val user = call.receive<User>()
    // Обработка пользователя
    call.respond(HttpStatusCode.Created, user)
}
```

Функция `receive<T>()` автоматически десериализует тело запроса в указанный тип, если установлен соответствующий плагин сериализации.

### Отправка ответов

**Ktor** предоставляет несколько способов отправки ответов:**

```kotlin
// Текстовый ответ
call.respondText("Hello")

// JSON ответ
call.respond(mapOf("message" to "Hello"))

// С указанием статуса
call.respond(HttpStatusCode.Created, user)

// Поток данных
call.respondBytes(byteArray)

// Файл
call.respondFile(File("path/to/file"))
```

Каждый метод `respond` автоматически устанавливает соответствующие заголовки **Content-Type**.

### Статус коды

**Для явного указания **HTTP** статус кода:**

```kotlin
call.respond(HttpStatusCode.OK, data)
call.respond(HttpStatusCode.Created, data)
call.respond(HttpStatusCode.NotFound)
call.respond(HttpStatusCode.BadRequest, "Error message")
```

Использование правильных статус кодов важно для создания понятного **REST API**.

## Middleware и Interceptors

### Application Call Pipeline

**Ktor** использует **pipeline** для обработки запросов. Каждый плагин добавляет свои **interceptors** в **pipeline**:**

```kotlin
install(CallLogging) {
    level = Level.INFO
    filter { call -> !call.request.path().startsWith("/health") }
}
```

**Interceptors** выполняются в определенном порядке и могут модифицировать запрос или ответ.

### Создание кастомного Interceptor

```kotlin
fun Application.configureCustomInterceptor() {
    intercept(ApplicationCallPipeline.Call) {
        val startTime = System.currentTimeMillis()
        proceed()
        val duration = System.currentTimeMillis() - startTime
        call.response.headers.append("X-Response-Time", "${duration}ms")
    }
}
```

Кастомные **interceptors** позволяют добавлять логику, которая должна выполняться для каждого запроса: логирование, измерение времени, добавление заголовков.

## Аутентификация и авторизация

### Базовая аутентификация

**Ktor** поддерживает различные методы аутентификации через плагин **Authentication**:**

```kotlin
install(Authentication) {
    basic("auth-basic") {
        realm = "Access to the API"
        validate { credentials ->
            if (credentials.name == "user" && credentials.password == "password") {
                UserIdPrincipal(credentials.name)
            } else {
                null
            }
        }
    }
}

routing {
    authenticate("auth-basic") {
        get("/protected") {
            val principal = call.principal<UserIdPrincipal>()
            call.respondText("Hello, ${principal?.name}")
        }
    }
}
```

Базовая аутентификация использует стандартный **HTTP Basic Auth** механизм, где **credentials** передаются в заголовке **Authorization**.

### JWT аутентификация

**Для более безопасной аутентификации используется **JWT**:**

```kotlin
install(Authentication) {
    jwt("auth-jwt") {
        realm = "API"
        verifier(jwtVerifier)
        validate { credential ->
            if (credential.payload.getClaim("username").asString() != "") {
                JWTPrincipal(credential.payload)
            } else {
                null
            }
        }
    }
}
```

**JWT** позволяет создавать **stateless** аутентификацию, где токен содержит всю необходимую информацию и не требует хранения сессий на сервере.

### Авторизация

**Авторизация проверяет права доступа после аутентификации:**

```kotlin
routing {
    authenticate("auth-jwt") {
        route("/admin") {
            authorize("admin") {
                get("/users") {
                    // Только для администраторов
                }
            }
        }
    }
}
```

## Работа с JSON

### Настройка JSON сериализации

**Для работы с **JSON** используется **kotlinx.serialization**:**

```kotlin
install(ContentNegotiation) {
    json(Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    })
}
```

Настройка `prettyPrint` делает **JSON** читаемым, `isLenient` позволяет более гибко парсить **JSON**, `ignoreUnknownKeys` игнорирует неизвестные поля при десериализации.

### Сериализация данных классов

```kotlin
@Serializable
data class User(val id: Int, val name: String, val email: String)

post("/users") {
    val user = call.receive<User>()
    // user автоматически десериализован из JSON
    call.respond(user)
}
```

Аннотация `@Serializable` позволяет автоматически сериализовать и десериализовать **data** классы.

## WebSockets

**WebSockets** позволяют создавать двустороннюю связь между клиентом и сервером:**

```kotlin
routing {
    webSocket("/chat") {
        send("Connected!")
        for (frame in incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                send("Echo: $text")
            }
        }
    }
}
```

**WebSocket** соединение остается открытым и позволяет отправлять сообщения в обе стороны без необходимости создания новых **HTTP** запросов.

## HTTP Client

**Ktor** также предоставляет **HTTP** клиент для выполнения запросов:**

```kotlin
val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

val response: HttpResponse = client.get("https://api.example.com/users")
val users: List<User> = response.body()
```

**HTTP** клиент поддерживает все основные **HTTP** методы и может использоваться как в серверных, так и в клиентских приложениях.

## Тестирование

**Ktor** предоставляет специальные инструменты для тестирования:**

```kotlin
class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, World!", response.bodyAsText())
    }
}
```

`testApplication` создает тестовое окружение, которое позволяет тестировать маршруты без запуска реального сервера.

## Развертывание

**Ktor** приложения можно развертывать различными способами:**

- **Standalone**: встроенный сервер (Netty, `Jetty`, Tomcat)
- **Docker**: контейнеризация приложения
- **Cloud**: развертывание на облачных платформах

Каждый способ имеет свои преимущества и подходит для разных сценариев использования.

Каждый способ развертывания имеет свои преимущества и подходит для разных сценариев использования. **Standalone** подходит для простых приложений, **Docker** для контейнеризации, а **Cloud** для масштабируемых решений.

## Продвинутые возможности

### Custom Plugins

**Ktor** позволяет создавать собственные плагины для переиспользования функциональности:**

```kotlin
class CustomHeaderPlugin(config: Configuration) {
    val headerName = config.headerName

    companion object Plugin : BaseApplicationPlugin<Configuration, CustomHeaderPlugin> {
        override val key = AttributeKey<CustomHeaderPlugin>("CustomHeader")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): CustomHeaderPlugin {
            val config = Configuration().apply(configure)
            val plugin = CustomHeaderPlugin(config)

            pipeline.intercept(ApplicationCallPipeline.Call) {
                call.response.headers.append(
                    plugin.headerName,
                    "CustomValue"
                )
            }

            return plugin
        }

        class Configuration {
            var headerName = "X-Custom-Header"
        }
    }
}

// Использование
fun Application.module() {
    install(CustomHeaderPlugin) {
        headerName = "X-My-Header"
    }
}
```

**Custom plugins** позволяют инкапсулировать переиспользуемую функциональность и создавать модульную архитектуру приложения.

### Content Negotiation

**Ktor** поддерживает автоматическую сериализацию и десериализацию через **Content Negotiation**:**

```kotlin
fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        post("/users") {
            val user = call.receive<User>()
            call.respond(user)
        }
    }
}
```

**Content Negotiation** автоматически определяет формат данных на основе заголовков запроса и ответа, упрощая работу с различными форматами.

### Status Pages

**Обработка **HTTP** статусов и исключений:**

```kotlin
fun Application.module() {
    install(StatusPages) {
        exception<AuthenticationException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
        }
        exception<AuthorizationException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden, "Forbidden")
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse("Not Found")
            )
        }
    }
}
```

**Status Pages** позволяют централизованно обрабатывать исключения и **HTTP** статусы, что упрощает управление ошибками.

## Аутентификация и авторизация

### JWT Authentication

```kotlin
fun Application.module() {
    install(Authentication) {
        jwt("jwt-auth") {
            verifier(jwtService.verifier)
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    routing {
        authenticate("jwt-auth") {
            get("/protected") {
                val principal = call.principal<JWTPrincipal>()
                call.respond("Hello, ${principal!!.payload.getClaim("username")}")
            }
        }
    }
}
```

**JWT authentication** позволяет создавать **stateless** аутентификацию, что особенно полезно для **REST API** и микросервисов.

### Session Authentication

```kotlin
fun Application.module() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 24 // 24 часа
        }
    }

    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                session
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
            }
        }
    }

    routing {
        post("/login") {
            val username = call.receive<LoginRequest>().username
            call.sessions.set(UserSession(username))
            call.respond("Logged in")
        }

        authenticate("auth-session") {
            get("/profile") {
                val session = call.sessions.get<UserSession>()
                call.respond("Profile: ${session?.username}")
            }
        }
    }
}
```

**Session authentication** подходит для традиционных веб-приложений, где нужно поддерживать состояние сессии.

## Работа с базами данных

### Exposed ORM

**Ktor** часто используется с **Exposed** для работы с базами данных:**

```kotlin
fun Application.module() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/mydb",
        driver = "org.postgresql.Driver",
        user = "user",
        password = "password"
    )

    routing {
        get("/users") {
            val users = transaction {
                Users.selectAll().map { it.toUser() }
            }
            call.respond(users)
        }

        post("/users") {
            val user = call.receive<User>()
            transaction {
                Users.insert {
                    it[name] = user.name
                    it[email] = user.email
                }
            }
            call.respond(HttpStatusCode.Created)
        }
    }
}
```

**Exposed** предоставляет типобезопасный **DSL** для работы с базами данных, что идеально сочетается с **Kotlin**.

### Database Migrations

```kotlin
fun Application.module() {
    val database = Database.connect(
        "jdbc:h2:mem:test",
        driver = "org.h2.Driver"
    )

    transaction(database) {
        SchemaUtils.create(Users, Posts)
    }

    // Или использовать Flyway
    val flyway = Flyway.configure()
        .dataSource("jdbc:h2:mem:test", "", "")
        .load()
    flyway.migrate()
}
```

Миграции базы данных позволяют версионировать схему БД и применять изменения последовательно.

## Тестирование Ktor приложений

Продвинутое тестирование **Ktor** приложений с различными подходами.

### Тестирование маршрутов

```kotlin
class ApplicationTest {
    @Test
    fun testGetUser() = testApplication {
        val response = client.get("/users/1")
        assertEquals(HttpStatusCode.OK, response.status)
        val user = response.body<User>()
        assertEquals("Alice", user.name)
    }

    @Test
    fun testPostUser() = testApplication {
        val user = User(name = "Bob", email = "bob@example.com")
        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }
}
```

`testApplication` создает изолированное тестовое окружение, которое позволяет тестировать маршруты без запуска реального сервера.

### Мокирование зависимостей

```kotlin
@Test
fun testWithMockedService() = testApplication {
    val mockService = mockk<UserService>()
    every { mockService.findById(1) } returns User(id = 1, name = "Alice")

    application {
        routing {
            get("/users/{id}") {
                val id = call.parameters["id"]!!.toInt()
                val user = mockService.findById(id)
                call.respond(user ?: HttpStatusCode.NotFound)
            }
        }
    }

    val response = client.get("/users/1")
    assertEquals(HttpStatusCode.OK, response.status)
}
```

Мокирование зависимостей позволяет тестировать маршруты изолированно от внешних сервисов и баз данных.

## Производительность и оптимизация

### Кэширование ответов

```kotlin
fun Application.module() {
    install(CachingHeaders) {
        options { call, content ->
            when (content.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(
                    CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60)
                )
                else -> null
            }
        }
    }
}
```

Кэширование уменьшает нагрузку на сервер и улучшает производительность для статических ресурсов.

### Compression

```kotlin
fun Application.module() {
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024)
        }
    }
}
```

Сжатие ответов уменьшает размер передаваемых данных и улучшает производительность, особенно для медленных соединений.

### Connection Pooling

```kotlin
val httpClient = HttpClient(CIO) {
    engine {
        maxConnectionsCount = 1000
        connectionIdleTimeoutMillis = 5000
    }
}
```

Настройка пула соединений позволяет эффективно управлять сетевыми ресурсами и обрабатывать большое количество одновременных запросов.

## Мониторинг и логирование

### Structured Logging

```kotlin
fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> !call.request.path().startsWith("/health") }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }
}
```

Структурированное логирование позволяет лучше понимать поведение приложения и быстрее находить проблемы.

### Метрики

```kotlin
fun Application.module() {
    install(MicrometerMetrics) {
        // Настройка метрик
    }

    routing {
        get("/metrics") {
            // Возврат метрик
        }
    }
}
```

Метрики позволяют мониторить производительность приложения и выявлять узкие места.

## Безопасность

### CORS

```kotlin
fun Application.module() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }
}
```

**CORS** настройки позволяют контролировать, какие домены могут обращаться к **API**, что критично для безопасности веб-приложений.

### Rate Limiting

```kotlin
fun Application.module() {
    install(RateLimiter) {
        registerLimiter("api") {
            rateLimiter(10, Duration.ofSeconds(1))
        }
    }

    routing {
        rateLimit("api") {
            get("/api/data") {
                call.respond("Data")
            }
        }
    }
}
```

**Rate limiting** предотвращает злоупотребление **API** и защищает от **DDoS** атак, ограничивая количество запросов от одного клиента.

Этот файл содержит полное руководство по **Ktor**, покрывающее все основные аспекты создания веб-приложений с использованием этого фреймворка, включая продвинутые возможности, аутентификацию, работу с базами данных, тестирование, оптимизацию производительности, мониторинг и безопасность.

## Продвинутые техники Ktor

### Custom Routing DSL

**Создание пользовательского **DSL** для маршрутизации:**

```kotlin
class ApiRouting {
    private val routes = mutableListOf<Route>()

    fun api(init: ApiRouting.() -> Unit) {
        init()
    }

    fun get(path: String, handler: suspend ApplicationCall.() -> Unit) {
        routes.add(Route("GET", path, handler))
    }

    fun post(path: String, handler: suspend ApplicationCall.() -> Unit) {
        routes.add(Route("POST", path, handler))
    }

    fun applyTo(application: Application) {
        application.routing {
            routes.forEach { route ->
                when (route.method) {
                    "GET" -> get(route.path, route.handler)
                    "POST" -> post(route.path, route.handler)
                }
            }
        }
    }

    data class Route(
        val method: String,
        val path: String,
        val handler: suspend ApplicationCall.() -> Unit
    )
}

// Использование
val apiRoutes = ApiRouting().apply {
    api {
        get("/users") {
            call.respond(getUsers())
        }
        post("/users") {
            val user = call.receive<User>()
            call.respond(createUser(user))
        }
    }
}
```

**Custom Routing DSL** позволяет создавать более выразительный и модульный код маршрутизации.

### Dependency Injection

**Интеграция **Dependency Injection** в **Ktor** приложения:**

```kotlin
// Использование Koin для DI
fun Application.module() {
    install(Koin) {
        modules(applicationModule)
    }

    routing {
        val userService by inject<UserService>()

        get("/users") {
            val users = userService.getAllUsers()
            call.respond(users)
        }
    }
}

// Модуль Koin
val applicationModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserServiceImpl(get()) }
}
```

**Dependency Injection** упрощает управление зависимостями и делает код более тестируемым.

## Масштабирование Ktor приложений

### Горизонтальное масштабирование

**Настройка **Ktor** для горизонтального масштабирования:**

```kotlin
fun Application.module() {
    // Использование SharedFlow для распределения состояния
    val sharedState = MutableSharedFlow<State>(replay = 1)

    // Использование Redis для распределенного кэша
    val redisCache = RedisCache(redisConnection)

    routing {
        get("/state") {
            val state = redisCache.get<State>("current-state")
            call.respond(state ?: getDefaultState())
        }

        post("/state") {
            val state = call.receive<State>()
            redisCache.set("current-state", state)
            sharedState.emit(state)
            call.respond(HttpStatusCode.OK)
        }
    }
}
```

Горизонтальное масштабирование позволяет увеличивать производительность приложения путем добавления дополнительных экземпляров.

### Load Balancing

**Настройка **load balancing** для **Ktor** приложений:**

```kotlin
// Конфигурация для работы за load balancer
fun Application.module() {
    install(XForwardedHeaderSupport)
    install(ForwardedHeaderSupport)

    // Использование реального IP клиента
    routing {
        get("/client-ip") {
            val clientIp = call.request.header("X-Forwarded-For")
                ?: call.request.origin.remoteHost
            call.respond(mapOf("ip" to clientIp))
        }
    }
}
```

Правильная настройка для работы за **load balancer** обеспечивает корректную работу приложения в распределенной среде.

## Продвинутые техники Ktor

### Кастомные плагины и middleware

**Создание кастомных плагинов для переиспользования функциональности:**

```kotlin
// Кастомный плагин для логирования
class LoggingPlugin(config: Configuration) {
    val logLevel = config.logLevel

    companion object Plugin : BaseApplicationPlugin<Configuration, LoggingPlugin> {
        override val key = AttributeKey<LoggingPlugin>("Logging")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): LoggingPlugin {
            val config = Configuration().apply(configure)
            val plugin = LoggingPlugin(config)

            pipeline.intercept(ApplicationCallPipeline.Call) {
                val startTime = System.currentTimeMillis()
                val method = call.request.httpMethod.value
                val path = call.request.path()

                proceed()

                val duration = System.currentTimeMillis() - startTime
                val status = call.response.status()

                println("[${plugin.logLevel}] $method $path - $status (${duration}ms)")
            }

            return plugin
        }

        class Configuration {
            var logLevel = "INFO"
        }
    }
}

// Использование
fun Application.module() {
    install(LoggingPlugin) {
        logLevel = "DEBUG"
    }
}
```

Кастомные плагины позволяют инкапсулировать переиспользуемую функциональность и создавать модульную архитектуру.

### Работа с WebSockets

**Продвинутые техники работы с **WebSockets**:**

```kotlin
// WebSocket сервер
fun Application.module() {
    routing {
        webSocket("/chat") {
            val session = this
            val user = call.request.queryParameters["user"] ?: "Anonymous"

            send("Welcome, $user!")

            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            broadcast("$user: $text")
                        }
                        is Frame.Close -> {
                            close()
                        }
                        else -> {}
                    }
                }
            } catch (e: Exception) {
                close()
            }
        }
    }
}

// WebSocket клиент
class WebSocketClient {
    private val client = HttpClient {
        install(WebSockets)
    }

    suspend fun connect(url: String, messageHandler: (String) -> Unit) {
        client.webSocket(url) {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    messageHandler(frame.readText())
                }
            }
        }
    }

    suspend fun send(message: String) {
        client.webSocket(url) {
            send(message)
        }
    }
}
```

**WebSockets** позволяют создавать двустороннюю связь между клиентом и сервером для **real-time** приложений.

## Продвинутые техники Ktor

### Работа с файлами и загрузками

**Обработка загрузки и скачивания файлов:**

```kotlin
// Загрузка файлов
fun Application.module() {
    routing {
        post("/upload") {
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: "uploaded"
                        val file = File("uploads/$fileName")
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        call.respond(HttpStatusCode.OK, "File uploaded: $fileName")
                    }
                    else -> {}
                }
            }
        }

        // Скачивание файлов
        get("/download/{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@get
            val file = File("uploads/$fileName")

            if (file.exists()) {
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName, fileName
                    ).toString()
                )
                call.respondFile(file)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

// Обработка больших файлов
suspend fun handleLargeFileUpload(call: ApplicationCall) {
    val multipartData = call.receiveMultipart()
    multipartData.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                val tempFile = File.createTempFile("upload", ".tmp")
                try {
                    part.streamProvider().use { input ->
                        tempFile.outputStream().buffered().use { output ->
                            input.copyTo(output)
                        }
                    }
                    // Обработка файла
                    processFile(tempFile)
                } finally {
                    tempFile.delete()
                }
            }
            else -> {}
        }
    }
}
```

Работа с файлами позволяет обрабатывать загрузку и скачивание файлов в веб-приложениях.

### Работа с сессиями

**Использование сессий для хранения состояния:**

```kotlin
// Настройка сессий
fun Application.module() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAge = Duration.days(7)
        }
    }

    install(Authentication) {
        session<UserSession>("session") {
            validate { session ->
                if (session != null) {
                    UserIdPrincipal(session.userId)
                } else {
                    null
                }
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }

    routing {
        post("/login") {
            val credentials = call.receive<LoginCredentials>()
            val user = userService.authenticate(credentials)

            if (user != null) {
                call.sessions.set(UserSession(user.id, user.name))
                call.respond(mapOf("status" to "success"))
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }
        }

        post("/logout") {
            call.sessions.clear<UserSession>()
            call.respond(mapOf("status" to "logged out"))
        }

        authenticate("session") {
            get("/profile") {
                val session = call.sessions.get<UserSession>()
                val user = userService.findById(session?.userId ?: return@get)
                call.respond(user)
            }
        }
    }
}

data class UserSession(val userId: Long, val userName: String)
```

Сессии позволяют хранить состояние пользователя между запросами и обеспечивать аутентификацию.

Этот файл содержит полное руководство по **Ktor**, покрывающее все основные аспекты создания веб-приложений с использованием этого фреймворка, включая продвинутые возможности, аутентификацию, работу с базами данных, тестирование, оптимизацию производительности, мониторинг, безопасность, масштабирование, кастомные плагины, **WebSockets**, работу с файлами и сессиями.

## Дополнительные техники Ktor

### Работа с Content Negotiation

**Использование **Content Negotiation** для различных форматов:**

```kotlin
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.xml.*

fun Application.module() {
    install(ContentNegotiation) {
        json()
        xml()
    }

    routing {
        get("/users") {
            val users = userService.getAllUsers()
            call.respond(users)  // Автоматически выберет формат на основе Accept заголовка
        }

        post("/users") {
            val user = call.receive<User>()  // Автоматически десериализует на основе Content-Type
            val created = userService.createUser(user)
            call.respond(created)
        }
    }
}
```

**Content Negotiation** позволяет автоматически выбирать формат данных на основе заголовков запроса.

### Работа с Status Pages

**Обработка **HTTP** статусов:**

```kotlin
import io.ktor.features.*

fun Application.module() {
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Not found"))
        }

        exception<ValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Validation failed"))
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(ErrorResponse("Resource not found"))
        }
    }
}
```

**Status Pages** позволяют централизованно обрабатывать ошибки и статусы **HTTP**.

Этот файл содержит полное руководство по **Ktor**, покрывающее все основные аспекты создания веб-приложений с использованием этого фреймворка, включая продвинутые возможности, аутентификацию, работу с базами данных, тестирование, оптимизацию производительности, мониторинг, безопасность, масштабирование, кастомные плагины, **WebSockets**, работу с файлами, сессиями, **Content Negotiation** и **Status Pages**.

## Дополнительные техники Ktor

### Работа с HTTP клиентом

**Использование **Ktor HTTP** клиента:**

```kotlin
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// Создание HTTP клиента
val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

// GET запрос
suspend fun getUsers(): List<User> {
    val response: HttpResponse = client.get("https://api.example.com/users")
    return response.body<List<User>>()
}

// POST запрос
suspend fun createUser(user: User): User {
    val response: HttpResponse = client.post("https://api.example.com/users") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }
    return response.body<User>()
}

// PUT запрос
suspend fun updateUser(id: Long, user: User): User {
    val response: HttpResponse = client.put("https://api.example.com/users/$id") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }
    return response.body<User>()
}

// DELETE запрос
suspend fun deleteUser(id: Long) {
    client.delete("https://api.example.com/users/$id")
}
```

**Ktor HTTP** клиент позволяет легко работать с **REST API** и другими **HTTP** сервисами.

### Работа с multipart запросами

**Обработка **multipart** данных:**

```kotlin
// Отправка multipart данных
suspend fun uploadFile(file: File, metadata: Map<String, String>) {
    client.post("https://api.example.com/upload") {
        setBody(
            MultiPartFormDataContent(
                formData {
                    append("file", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/octet-stream")
                        append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                    })
                    metadata.forEach { (key, value) ->
                        append(key, value)
                    }
                }
            )
        )
    }
}
```

**Multipart** запросы позволяют отправлять файлы и другие данные в одном запросе.

Этот файл содержит полное руководство по **Ktor**, покрывающее все основные аспекты создания веб-приложений с использованием этого фреймворка, включая продвинутые возможности, аутентификацию, работу с базами данных, тестирование, оптимизацию производительности, мониторинг, безопасность, масштабирование, кастомные плагины, **WebSockets**, работу с файлами, сессиями, **Content Negotiation**, **Status Pages**, **HTTP** клиентом и **multipart** запросами.

## Дополнительные техники Ktor

### Работа с middleware

**Создание и использование **middleware**:**

```kotlin
// Кастомный middleware
fun Application.configureMiddleware() {
    intercept(ApplicationCallPipeline.Call) {
        val startTime = System.currentTimeMillis()

        proceed()

        val duration = System.currentTimeMillis() - startTime
        call.response.header("X-Response-Time", duration.toString())
    }
}

// Middleware для логирования
fun Application.configureLogging() {
    intercept(ApplicationCallPipeline.Call) {
        val method = call.request.httpMethod.value
        val path = call.request.path()
        val status = call.response.status()

        println("$method $path - $status")

        proceed()
    }
}
```

**Middleware** позволяет добавлять **cross-cutting concerns** к обработке запросов.

## Лучшие практики

- **Плагины вместо глобального состояния**: используйте `install()` для плагинов вместо синглтонов
- **Структура проекта**: разделяйте маршрутизацию, плагины и бизнес-логику по модулям
- **Асинхронность**: выполняйте I/O операции в корутинах, не блокируйте диспетчер
- **Безопасность**: всегда применяйте CORS, rate limiting и аутентификацию для публичных endpoints
- **Тестирование**: используйте `withTestApplication` для изолированных тестов маршрутов

## Практические примеры использования

### Создание REST API

**Пример создания **REST API** с использованием **Ktor**:**

```kotlin
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        route("/api/users") {
            get {
                val users = userService.getAllUsers()
                call.respond(users)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: throw BadRequestException("Invalid user ID")
                val user = userService.getUser(id)
                call.respond(user)
            }

            post {
                val user = call.receive<User>()
                val createdUser = userService.createUser(user)
                call.respond(HttpStatusCode.Created, createdUser)
            }
        }
    }
}
```

**Ktor** позволяет создавать чистые и типобезопасные **REST API**.

### Обработка WebSocket соединений

**Пример обработки **WebSocket** соединений:**

```kotlin
fun Application.module() {
    routing {
        webSocket("/chat") {
            val session = this
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    // Обработка сообщения
                    session.send("Echo: $text")
                }
            }
        }
    }
}
```

**WebSocket** позволяет создавать интерактивные приложения с реальным временем.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Ktor** предоставляет современный и асинхронный фреймворк для создания веб-приложений в **Kotlin**. Понимание основных концепций **Ktor**, работы с плагинами, аутентификацией, **WebSockets**, **HTTP** клиентом, **middleware** и другими возможностями позволяет создавать эффективные и масштабируемые веб-приложения. Правильное использование **Ktor** помогает создавать отзывчивые приложения с эффективной обработкой запросов и управлением состоянием.

## Дополнительные ресурсы

**Для дальнейшего изучения **Ktor** рекомендуется:**

- **Ktor Documentation**: **https**://**ktor.io**/**docs**/**welcome.html**
- **Ktor GitHub**: **https**://**github.com**/**ktorio**/**ktor**
- **Ktor Samples**: **https**://**github.com**/**ktorio**/**ktor-samples**

Этот файл содержит полное руководство по **Ktor**, покрывающее все основные аспекты создания веб-приложений с использованием этого фреймворка, включая продвинутые возможности, аутентификацию, работу с базами данных, тестирование, оптимизацию производительности, мониторинг, безопасность, масштабирование, кастомные плагины, **WebSockets**, работу с файлами, сессиями, **Content Negotiation**, **Status Pages**, **HTTP** клиентом, **multipart** запросами, **middleware**, практические примеры использования, заключение и дополнительные ресурсы.

## Итоговые рекомендации

**При работе с **Ktor** рекомендуется:**

1. Использовать плагины для модульности приложения
2. Применять **Content Negotiation** для работы с различными форматами данных
3. Использовать **Status Pages** для централизованной обработки ошибок
4. Применять аутентификацию и авторизацию для защиты **endpoints**
5. Оптимизировать производительность с помощью **async** обработки и кэширования

Этот файл содержит полное руководство по **Ktor**, покрывающее все основные аспекты создания веб-приложений с использованием этого фреймворка, включая продвинутые возможности, аутентификацию, работу с базами данных, тестирование, оптимизацию производительности, мониторинг, безопасность, масштабирование, кастомные плагины, **WebSockets**, работу с файлами, сессиями, **Content Negotiation**, **Status Pages**, **HTTP** клиентом, **multipart** запросами, **middleware**, практические примеры использования, заключение, дополнительные ресурсы и итоговые рекомендации.

