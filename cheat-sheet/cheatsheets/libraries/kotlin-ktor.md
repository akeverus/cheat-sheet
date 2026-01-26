# Ktor

Ktor - это асинхронный веб-фреймворк для Kotlin, построенный на базе coroutines. Предоставляет DSL для создания HTTP сервисов, поддерживает различные серверы и клиенты.

## Содержание

- [Основные возможности](#основные-возможности)
  - [Создание простого сервера](#создание-простого-сервера)
  - [Routing](#routing)
  - [Content Negotiation](#content-negotiation)
  - [Authentication и Authorization](#authentication-и-authorization)
  - [Sessions](#sessions)
  - [CORS](#cors)
  - [Error Handling](#error-handling)
- [Продвинутые возможности](#продвинутые-возможности)
  - [WebSockets](#websockets)
  - [Static Content](#static-content)
- [HTTP Client](#http-client)
  - [Basic HTTP Client](#basic-http-client)
  - [Advanced Client Features](#advanced-client-features)
  - [Client DSL](#client-dsl)
- [Тестирование](#тестирование)
  - [Unit Testing Routes](#unit-testing-routes)
  - [Integration Testing](#integration-testing)
  - [Тестирование WebSockets](#тестирование-websockets)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Ktor в Spring Boot](#ktor-в-spring-boot)
  - [Configuration Properties](#configuration-properties)
- [Лучшие практики](#лучшие-практики)
  - [Application Structure](#application-structure)
  - [Dependency Injection](#dependency-injection)
  - [Error Handling Patterns](#error-handling-patterns)
  - [Validation](#validation)
- [Оптимизация производительности](#оптимизация-производительности)
  - [Async Operations](#async-operations)
  - [Connection Pooling](#connection-pooling)
  - [Caching](#caching)
- [Безопасность](#безопасность)
  - [HTTPS Configuration](#https-configuration)
  - [Rate Limiting](#rate-limiting)
- [Мониторинг и логирование](#мониторинг-и-логирование)
  - [Logging](#logging)
  - [Metrics](#metrics)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Spring MVC to Ktor](#from-spring-mvc-to-ktor)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Debugging](#debugging)
- [Экспериментальные возможности](#экспериментальные-возможности)
  - [Ktor 2.0+ Features](#ktor-20-features)

## Основные возможности

### Создание простого сервера
```kotlin
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Создание простого HTTP сервера на Ktor
 * Ktor - асинхронный веб-фреймворк для Kotlin, построенный на корутинах
 */
fun main() {
    // embeddedServer - создание встроенного сервера
    // Netty - движок сервера (альтернативы: Jetty, Tomcat, CIO)
    // port = 8080 - порт для прослушивания HTTP запросов
    embeddedServer(Netty, port = 8080) {
        // Конфигурация приложения внутри блока
        routing {
            // get() - обработчик GET запросов
            // "/" - путь маршрута (корневой путь)
            get("/") {
                // call - объект запроса/ответа (ApplicationCall)
                // respondText() - отправка текстового ответа
                // ContentType.Text.Plain - MIME тип ответа (text/plain)
                call.respondText("Hello, Ktor!", ContentType.Text.Plain)
            }

            // Маршрут для JSON ответа
            get("/json") {
                // Создание объекта данных
                val user = User("John", 30)
                // respond() - автоматическая сериализация объекта в JSON
                // Требует установки ContentNegotiation плагина
                call.respond(user)
            }
        }
    }.start(wait = true)
    // start() - запуск сервера
    // wait = true - блокирует поток до остановки сервера
}

// @Serializable - аннотация для kotlinx.serialization
// Позволяет автоматически сериализовать/десериализовать объекты
@Serializable
data class User(val name: String, val age: Int)
```

### Routing
```kotlin
/**
 * Конфигурация маршрутизации в Ktor
 * Routing позволяет определять обработчики для различных HTTP методов и путей
 */
fun Application.configureRouting() {
    routing {
        // Простые маршруты - обработка GET запросов
        get("/") {
            // call.respondText() - отправка простого текстового ответа
            call.respondText("Welcome to Ktor!")
        }

        // Параметры пути - извлечение значений из URL
        // {name} - именованный параметр пути
        get("/hello/{name}") {
            // call.parameters - доступ к параметрам пути и query параметрам
            // ["name"] - получение значения параметра "name" из пути
            // ?: "World" - значение по умолчанию если параметр отсутствует
            val name = call.parameters["name"] ?: "World"
            call.respondText("Hello, $name!")
        }

        // Параметры пути с валидацией
        get("/users/{id}") {
            // Извлечение и преобразование параметра пути
            val id = call.parameters["id"]?.toIntOrNull()  // Преобразование строки в Int
            if (id == null) {
                // HttpStatusCode.BadRequest - HTTP 400 (неверный запрос)
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get  // Прерывание обработки маршрута
            }

            // Получение данных из сервиса
            val user = userService.getUser(id)
            if (user == null) {
                // HttpStatusCode.NotFound - HTTP 404 (не найдено)
                call.respond(HttpStatusCode.NotFound, "User not found")
            } else {
                // Успешный ответ с данными пользователя
                call.respond(user)
            }
        }

        // Query параметры - параметры после "?" в URL
        // Пример: /search?q=test&limit=20
        get("/search") {
            // call.request.queryParameters - доступ к query параметрам
            // ["q"] - получение значения параметра "q"
            val query = call.request.queryParameters["q"] ?: ""  // Поисковый запрос
            // ["limit"] - получение параметра limit с преобразованием
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10  // Лимит результатов

            // Выполнение поиска
            val results = searchService.search(query, limit)
            call.respond(results)  // Возврат результатов поиска
        }

        // HTTP методы - обработка различных HTTP методов
        // POST - создание ресурса
        post("/users") {
            try {
                // call.receive<T>() - десериализация тела запроса в объект типа T
                // Требует установки ContentNegotiation плагина
                val user = call.receive<User>()
                
                // Создание пользователя через сервис
                val createdUser = userService.createUser(user)
                
                // HttpStatusCode.Created - HTTP 201 (ресурс создан)
                call.respond(HttpStatusCode.Created, createdUser)
            } catch (e: Exception) {
                // Обработка ошибок десериализации или валидации
                // HttpStatusCode.BadRequest - HTTP 400 (неверный запрос)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        // PUT - обновление ресурса
        put("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@put
            }

            // Получение данных для обновления из тела запроса
            val user = call.receive<User>()
            
            // Обновление пользователя
            val updatedUser = userService.updateUser(id, user)
            
            // Если пользователь не найден, вернуть 404, иначе вернуть обновленные данные
            call.respond(updatedUser ?: HttpStatusCode.NotFound)
        }

        // DELETE - удаление ресурса
        delete("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }

            // Удаление пользователя
            val deleted = userService.deleteUser(id)
            if (deleted) {
                // HttpStatusCode.NoContent - HTTP 204 (успешно, без содержимого)
                call.respond(HttpStatusCode.NoContent)
            } else {
                // HttpStatusCode.NotFound - HTTP 404 (ресурс не найден)
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
```

### Content Negotiation
```kotlin
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })

        // Дополнительные форматы
        xml()
        protobuf()
    }
}

// Теперь можно автоматически сериализовать/десериализовать объекты
routing {
    get("/users") {
        val users = userService.getAllUsers()
        call.respond(users) // Автоматически в JSON
    }

    post("/users") {
        val user = call.receive<User>() // Автоматически из JSON
        val created = userService.createUser(user)
        call.respond(created)
    }
}
```

## Продвинутые возможности

### Authentication и Authorization
```kotlin
import io.ktor.server.auth.*
import io.ktor.server.sessions.*

// Настройка аутентификации
fun Application.configureAuthentication() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == "admin" && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }

        jwt("auth-jwt") {
            realm = "ktor"
            verifier(JWT.require(Algorithm.HMAC256("secret")).build())
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }

        session<UserSession>("auth-session") {
            validate { session -> session }
            challenge { call.respondRedirect("/login") }
        }
    }
}

// Использование аутентификации
routing {
    authenticate("auth-basic") {
        get("/protected") {
            val principal = call.principal<UserIdPrincipal>()
            call.respondText("Hello, ${principal?.name}!")
        }
    }

    authenticate("auth-jwt") {
        get("/api/profile") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal?.payload?.getClaim("username")?.asString()
            call.respond(mapOf("username" to username))
        }
    }
}

data class UserSession(val name: String, val count: Int)
```

### Sessions
```kotlin
import io.ktor.server.sessions.*

fun Application.configureSessions() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60 * 60 * 24 // 24 hours
        }
    }
}

routing {
    get("/login") {
        call.sessions.set(UserSession("user123", 0))
        call.respondText("Logged in")
    }

    get("/count") {
        val session = call.sessions.get<UserSession>()
        if (session != null) {
            val newSession = session.copy(count = session.count + 1)
            call.sessions.set(newSession)
            call.respondText("Count: ${newSession.count}")
        } else {
            call.respondText("Not logged in")
        }
    }

    get("/logout") {
        call.sessions.clear<UserSession>()
        call.respondText("Logged out")
    }
}
```

### CORS
```kotlin
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)

        allowCredentials = true

        // Разрешить все origins (для разработки)
        anyHost()

        // Или конкретные hosts
        allowHost("localhost:3000")
        allowHost("example.com", schemes = listOf("https"))
    }
}
```

### Error Handling
```kotlin
import io.ktor.server.plugins.statuspages.*

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }

        exception<IllegalArgumentException> { call, cause ->
            call.respondText(text = "400: ${cause.message}", status = HttpStatusCode.BadRequest)
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(text = "404: Page not found", status = status)
        }

        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respondText(text = "401: Unauthorized", status = status)
        }
    }
}

// Кастомные исключения
class UserNotFoundException : Exception("User not found")
class ValidationException(val errors: List<String>) : Exception("Validation failed")

// Обработка в routing
routing {
    get("/users/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid ID")

            val user = userService.getUser(id)
                ?: throw UserNotFoundException()

            call.respond(user)
        } catch (e: IllegalArgumentException) {
            throw e // Будет обработано StatusPages
        } catch (e: UserNotFoundException) {
            call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message))
        }
    }
}
```

### WebSockets
```kotlin
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException

fun Application.configureWebSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
}

routing {
    webSocket("/chat") {
        send("You are connected!")

        try {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val text = frame.readText()
                        send("Echo: $text")
                    }
                    is Frame.Binary -> {
                        // Обработка бинарных данных
                    }
                    is Frame.Close -> {
                        // Обработка закрытия соединения
                    }
                    else -> {
                        // Другие типы фреймов
                    }
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            // WebSocket закрыт
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
```

### Static Content
```kotlin
import io.ktor.server.http.content.*

fun Application.configureStaticContent() {
    routing {
        // Статические файлы
        static("/static") {
            resources("static")
        }

        // Файлы из classpath
        static("/assets") {
            resources("assets")
        }

        // Файлы из директории
        static("/files") {
            files("uploads")
        }

        // Single page application
        static("/") {
            resources("static")
            defaultResource("index.html")
        }
    }
}
```

## HTTP Client

### Basic HTTP Client
```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun basicHttpClient() {
    val client = HttpClient(CIO)

    try {
        val response: HttpResponse = client.get("https://api.example.com/users")
        println("Status: ${response.status}")
        val body: String = response.bodyAsText()
        println("Body: $body")
    } finally {
        client.close()
    }
}
```

### Advanced Client Features
```kotlin
suspend fun advancedHttpClient() {
    val client = HttpClient(CIO) {
        // Настройка движка
        engine {
            maxConnectionsCount = 1000
            endpoint {
                maxConnectionsPerRoute = 100
                pipelineMaxSize = 20
                keepAliveTime = 5000
                connectTimeout = 5000
                connectAttempts = 5
            }
        }

        // Content negotiation
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }

        // Logging
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }

        // Default request
        defaultRequest {
            header("User-Agent", "Ktor Client")
            header("Accept", "application/json")
        }
    }

    try {
        // GET запрос
        val users: List<User> = client.get("https://api.example.com/users").body()

        // POST запрос
        val newUser = User("John", 30)
        val createdUser: User = client.post("https://api.example.com/users") {
            contentType(ContentType.Application.Json)
            setBody(newUser)
        }.body()

        // Запрос с параметрами
        val response = client.get("https://api.example.com/search") {
            parameter("q", "kotlin")
            parameter("limit", "10")
        }

        // Запрос с аутентификацией
        val secureResponse = client.get("https://api.example.com/profile") {
            bearerAuth("your-token")
        }

    } finally {
        client.close()
    }
}
```

### Client DSL
```kotlin
suspend fun clientDslExample() {
    val client = HttpClient(CIO)

    try {
        val response = client.request("https://api.example.com/data") {
            method = HttpMethod.Post

            headers {
                append("X-Custom-Header", "value")
                append(HttpHeaders.Authorization, "Bearer token")
            }

            parameter("key", "value")
            parameter("another", "param")

            contentType(ContentType.Application.Json)
            setBody("""{"data": "example"}""")
        }

        println("Response status: ${response.status}")
        println("Response headers: ${response.headers}")

    } finally {
        client.close()
    }
}
```

## Тестирование

### Unit Testing Routes
```kotlin
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }

        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Welcome to Ktor!", bodyAsText())
        }
    }

    @Test
    fun testGetUser() = testApplication {
        application {
            configureRouting()
            // Mock сервисы здесь
        }

        client.get("/users/1").apply {
            assertEquals(HttpStatusCode.OK, status)
            val user = Json.decodeFromString<User>(bodyAsText())
            assertEquals("John", user.name)
        }
    }

    @Test
    fun testCreateUser() = testApplication {
        application {
            configureRouting()
        }

        val userJson = Json.encodeToString(User("Jane", 25))

        client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(userJson)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            val createdUser = Json.decodeFromString<User>(bodyAsText())
            assertEquals("Jane", createdUser.name)
        }
    }
}
```

### Integration Testing
```kotlin
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [TestApplication::class]
)
class KtorIntegrationTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `should create and retrieve user`() {
        // Создание пользователя
        val user = User("John", 30)
        val createResponse = testRestTemplate.postForEntity(
            "/api/users",
            user,
            User::class.java
        )

        assertEquals(HttpStatus.CREATED, createResponse.statusCode)
        val createdUser = createResponse.body!!

        // Получение пользователя
        val getResponse = testRestTemplate.getForEntity(
            "/api/users/${createdUser.id}",
            User::class.java
        )

        assertEquals(HttpStatus.OK, getResponse.statusCode)
        assertEquals(createdUser, getResponse.body)
    }
}
```

### Тестирование WebSockets
```kotlin
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.*

class WebSocketTest {

    @Test
    fun testWebSocketEcho() = testApplication {
        application {
            configureWebSockets()
        }

        // Тестирование WebSocket
        val client = createClient {
            install(WebSockets)
        }

        client.webSocket("/chat") {
            // Отправка сообщения
            send(Frame.Text("Hello"))

            // Получение ответа
            val response = incoming.receive() as Frame.Text
            assertEquals("Echo: Hello", response.readText())
        }
    }
}
```

## Интеграция с Spring Boot

### Ktor в Spring Boot
```kotlin
@SpringBootApplication
class KtorSpringApplication

@Configuration
class KtorConfig {

    @Bean
    fun ktorServer(userService: UserService): NettyApplicationEngine {
        return embeddedServer(Netty, port = 8081) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }

            routing {
                get("/api/ktor/users") {
                    val users = userService.getAllUsers()
                    call.respond(users)
                }

                post("/api/ktor/users") {
                    val user = call.receive<User>()
                    val created = userService.createUser(user)
                    call.respond(HttpStatusCode.Created, created)
                }
            }
        }.start(wait = false)
    }
}
```

### Configuration Properties
```kotlin
@ConfigurationProperties("ktor")
data class KtorConfig(
    val port: Int = 8080,
    val host: String = "0.0.0.0",
    val cors: CorsConfig = CorsConfig()
) {
    data class CorsConfig(
        val allowedHosts: List<String> = listOf("localhost:3000"),
        val allowedMethods: List<String> = listOf("GET", "POST", "PUT", "DELETE")
    )
}

@Configuration
class KtorServerConfig(
    private val config: KtorConfig,
    private val userService: UserService
) {

    @Bean
    @Profile("ktor")
    fun ktorServer(): NettyApplicationEngine {
        return embeddedServer(Netty, host = config.host, port = config.port) {
            configureCORS(config.cors)
            configureRouting(userService)
        }.start(wait = false)
    }
}
```

## Лучшие практики

### Application Structure
```
src/main/kotlin/
├── Application.kt          # Точка входа
├── plugins/
│   ├── Routing.kt         # Маршрутизация
│   ├── Serialization.kt   # Сериализация
│   ├── Authentication.kt  # Аутентификация
│   └── Database.kt        # База данных
├── routes/
│   ├── UserRoutes.kt      # User API
│   ├── ProductRoutes.kt   # Product API
│   └── OrderRoutes.kt     # Order API
├── models/
│   ├── User.kt
│   ├── Product.kt
│   └── Order.kt
├── services/
│   ├── UserService.kt
│   ├── ProductService.kt
│   └── OrderService.kt
└── repositories/
    ├── UserRepository.kt
    ├── ProductRepository.kt
    └── OrderRepository.kt
```

### Dependency Injection
```kotlin
// Koin для DI
val appModule = module {
    single { Database.connect() }
    single { UserRepository(get()) }
    single { UserService(get()) }
}

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule)
    }
}

// Использование в routing
routing {
    val userService by inject<UserService>()

    get("/users") {
        val users = userService.getAllUsers()
        call.respond(users)
    }
}
```

### Error Handling Patterns
```kotlin
sealed class ApiError(val code: Int, val message: String) {
    class BadRequest(message: String = "Bad Request") : ApiError(400, message)
    class Unauthorized(message: String = "Unauthorized") : ApiError(401, message)
    class NotFound(message: String = "Not Found") : ApiError(404, message)
    class InternalError(message: String = "Internal Server Error") : ApiError(500, message)
}

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<ApiError> { call, error ->
            call.respond(error.code, mapOf("error" to error.message))
        }

        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError,
                mapOf("error" to "Internal server error"))
        }
    }
}

// Result-based error handling
suspend fun <T> ApplicationCall.respondResult(result: Result<T>) {
    result.fold(
        onSuccess = { data -> respond(data) },
        onFailure = { error ->
            when (error) {
                is ApiError -> respond(error.code, mapOf("error" to error.message))
                else -> respond(HttpStatusCode.InternalServerError,
                    mapOf("error" to "Internal server error"))
            }
        }
    )
}
```

### Validation
```kotlin
// DTO с валидацией
@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val age: Int
) {
    fun validate(): ValidationResult {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Name cannot be blank")
        if (!email.contains("@")) errors.add("Invalid email format")
        if (age < 18) errors.add("Age must be at least 18")

        return if (errors.isEmpty()) ValidationResult.Valid
        else ValidationResult.Invalid(errors)
    }
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}

// Использование в routes
post("/users") {
    val request = call.receive<CreateUserRequest>()

    when (val validation = request.validate()) {
        is ValidationResult.Valid -> {
            val user = userService.createUser(request)
            call.respond(HttpStatusCode.Created, user)
        }
        is ValidationResult.Invalid -> {
            call.respond(HttpStatusCode.BadRequest,
                mapOf("errors" to validation.errors))
        }
    }
}
```

## Оптимизация производительности

### Async Operations
```kotlin
// Правильное использование coroutines
routing {
    get("/slow-operation") {
        // Плохо: блокирует поток
        // val result = blockingOperation()
        // call.respond(result)

        // Хорошо: асинхронно
        val result = withContext(Dispatchers.IO) {
            blockingOperation()
        }
        call.respond(result)
    }

    get("/parallel-operations") {
        coroutineScope {
            val deferred1 = async(Dispatchers.IO) { operation1() }
            val deferred2 = async(Dispatchers.IO) { operation2() }

            val result1 = deferred1.await()
            val result2 = deferred2.await()

            call.respond(mapOf("result1" to result1, "result2" to result2))
        }
    }
}
```

### Connection Pooling
```kotlin
// Настройка HTTP клиента
val client = HttpClient(CIO) {
    engine {
        maxConnectionsCount = 1000
        endpoint {
            maxConnectionsPerRoute = 100
            pipelineMaxSize = 20
            keepAliveTime = 5000
            connectTimeout = 5000
            connectAttempts = 5
        }
    }
}
```

### Caching
```kotlin
// In-memory cache
val cache = ConcurrentHashMap<String, CacheEntry>()

data class CacheEntry(
    val data: Any,
    val timestamp: Long = System.currentTimeMillis()
)

val CACHE_DURATION = 5 * 60 * 1000 // 5 minutes

fun <T> cached(key: String, supplier: suspend () -> T): T {
    val entry = cache[key]
    if (entry != null && System.currentTimeMillis() - entry.timestamp < CACHE_DURATION) {
        @Suppress("UNCHECKED_CAST")
        return entry.data as T
    }

    val data = supplier()
    cache[key] = CacheEntry(data)
    return data
}

// Использование
routing {
    get("/cached-data") {
        val data = cached("data") {
            expensiveOperation()
        }
        call.respond(data)
    }
}
```

## Безопасность

### HTTPS Configuration
```kotlin
// HTTPS для сервера
fun Application.configureSecurity() {
    // Для разработки
    // install(HttpsRedirect)

    // Для production
    // install(HSTS)
}

// HTTPS клиент
val secureClient = HttpClient(CIO) {
    engine {
        https {
            serverName = "api.example.com"
            cipherSuites = CIOCipherSuites.SupportedSuites
            trustManager = // custom trust manager
        }
    }
}
```

### Rate Limiting
```kotlin
class RateLimitPlugin {
    private val requests = ConcurrentHashMap<String, MutableList<Long>>()

    fun isAllowed(clientId: String): Boolean {
        val now = System.currentTimeMillis()
        val clientRequests = requests.getOrPut(clientId) { mutableListOf() }

        // Очистка старых запросов (окно 1 минута)
        clientRequests.removeIf { now - it > 60_000 }

        // Проверка лимита (100 запросов в минуту)
        return if (clientRequests.size < 100) {
            clientRequests.add(now)
            true
        } else {
            false
        }
    }
}

val rateLimit = RateLimitPlugin()

fun Application.configureRateLimit() {
    intercept(ApplicationCallPipeline.Call) {
        val clientId = call.request.origin.remoteHost

        if (!rateLimit.isAllowed(clientId)) {
            call.respond(HttpStatusCode.TooManyRequests, "Rate limit exceeded")
            finish()
        }
    }
}
```

## Мониторинг и логирование

### Logging
```kotlin
import io.ktor.server.plugins.calllogging.*

fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/api") }
        format { call ->
            val status = call.response.status()
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val duration = call.processingTimeMillis()

            "$status: $method $uri (${duration}ms)"
        }
    }
}
```

### Metrics
```kotlin
// Micrometer integration
fun Application.configureMetrics() {
    val meterRegistry = SimpleMeterRegistry()

    intercept(ApplicationCallPipeline.Call) {
        val startTime = System.nanoTime()

        try {
            proceed()
        } finally {
            val duration = (System.nanoTime() - startTime) / 1_000_000.0
            val path = call.request.path()

            meterRegistry.timer("ktor.requests", "path", path).record(duration, TimeUnit.MILLISECONDS)
        }
    }

    routing {
        get("/metrics") {
            call.respond(meterRegistry.scrape())
        }
    }
}
```

## Руководство по миграции

### From Spring MVC to Ktor
```kotlin
// Spring MVC
@RestController
class UserController(val userService: UserService) {

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.getUser(id)
        return if (user != null) ResponseEntity.ok(user)
        else ResponseEntity.notFound().build()
    }
}

// Ktor equivalent
fun Application.userRoutes(userService: UserService) {
    routing {
        get("/users/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@get
            }

            val user = userService.getUser(id)
            if (user != null) {
                call.respond(user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
```

### From Express.js to Ktor
```javascript
// Express.js
app.get('/users/:id', async (req, res) => {
  const user = await userService.getUser(req.params.id);
  if (user) {
    res.json(user);
  } else {
    res.status(404).send('Not found');
  }
});
```

```kotlin
// Ktor equivalent
routing {
    get("/users/{id}") {
        val id = call.parameters["id"]?.toLongOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }

        val user = userService.getUser(id)
        if (user != null) {
            call.respond(user)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
```

## Устранение неполадок

### Common Issues
```kotlin
object KtorTroubleshooting {

    // Проблема: Port already in use
    fun startServerOnFreePort(): NettyApplicationEngine {
        return embeddedServer(Netty, port = 0) { // port = 0 - auto-assign
            // configuration
        }.start(wait = false)
    }

    // Проблема: Memory leaks
    suspend fun properResourceHandling() {
        val connection = acquireConnection()
        try {
            // use connection
        } finally {
            connection.close() // Always close resources
        }
    }

    // Проблема: Blocking operations
    suspend fun nonBlockingDatabaseCall() {
        // Плохо
        val result = runBlocking { databaseCall() }

        // Хорошо
        val result = withContext(Dispatchers.IO) { databaseCall() }
    }

    // Проблема: Exception handling
    fun Application.configureProperErrorHandling() {
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                // Логировать ошибку
                call.application.log.error("Unhandled exception", cause)
                call.respond(HttpStatusCode.InternalServerError, "Internal error")
            }
        }
    }

    // Проблема: Large request/response handling
    fun Application.configureLargePayloads() {
        install(ContentNegotiation) {
            // Увеличить лимиты
        }
    }
}
```

### Debugging
```kotlin
// Debug logging
fun Application.configureDebugLogging() {
    install(CallLogging) {
        level = Level.DEBUG
        format { call ->
            val method = call.request.httpMethod.value
            val uri = call.request.uri
            val status = call.response.status()
            val duration = call.processingTimeMillis()

            "[$status] $method $uri - ${duration}ms"
        }
    }
}

// Request/Response debugging
intercept(ApplicationCallPipeline.Call) {
    println("Request: ${call.request.httpMethod} ${call.request.uri}")
    println("Headers: ${call.request.headers.entries()}")

    proceed()

    println("Response: ${call.response.status()}")
}
```

## Экспериментальные возможности

### Ktor 3.0+ Features (Future)
```kotlin
// Предполагаемые возможности Ktor 3.0+

// Улучшенная поддержка Kotlin 1.8+
// Inline classes в сериализации
@Serializable
data class User(
    val id: UserId,
    val email: Email
)

@JvmInline
@Serializable
value class UserId(val value: Long)

@JvmInline
@Serializable
value class Email(val value: String)

// Context receivers для middleware
context(HttpCallPipelineContext)
suspend fun authenticate() {
    val token = getHeader("Authorization")
    if (token == null) {
        respond(HttpStatusCode.Unauthorized)
        return
    }
    // authentication logic
}

// Улучшенные WebSockets
webSocket("/chat") {
    // Bidirectional communication
    val session = acceptWebSocketSession()

    // Send and receive concurrently
    launch {
        for (message in session.incoming) {
            session.send("Echo: $message")
        }
    }
}

// Native coroutine debugging
// Автоматическое обнаружение coroutine leaks
// Улучшенные stack traces для suspend functions
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация Ktor](https://ktor.io/)
- [Ktor Documentation](https://ktor.io/docs/)
- [Ktor GitHub](https://github.com/ktorio/ktor)
- [Ktor Samples](https://github.com/ktorio/ktor/tree/main/ktor-samples)

## См. также
- [Kotlin Coroutines](kotlin-coroutines.md) - Асинхронное программирование
- [Kotlin Serialization](kotlin-kotlinx-serialization.md) - Сериализация данных
- [Spring WebFlux](spring-webflux.md) - Реактивное веб-программирование
