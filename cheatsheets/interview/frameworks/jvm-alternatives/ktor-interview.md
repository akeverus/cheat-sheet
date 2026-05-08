---
title: "Вопросы на собеседовании: Ktor"
description: "Ktor — асинхронный Kotlin-фреймворк от JetBrains. Серверные и клиентские плагины, маршрутизация, корутины, DI, аутентификация, Netty/CIO, тестирование"
tags:
  - interview
  - frameworks
  - ktor-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Ktor"
  - "Ktor interview"
  - "Ktor собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Ktor`

`Ktor` — асинхронный фреймворк от JetBrains для построения server- и client-приложений на Kotlin. Построен на корутинах, использует **plugins** (раньше features) для расширения функциональности. Легковесный, явный, тесно интегрирован с экосистемой Kotlin.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Ktor Official Documentation](https://ktor.io/docs/)
- [Ktor GitHub](https://github.com/ktorio/ktor)
- [Ktor Server Plugins](https://ktor.io/docs/server-plugins.html)
- [Ktor Client Documentation](https://ktor.io/docs/client.html)
- [Ktor Tutorials — Baeldung](https://www.baeldung.com/kotlin/ktor)
- [Ktor vs Spring Boot — Baeldung](https://www.baeldung.com/kotlin/spring-boot-ktor-comparison)
- [Kotlin Coroutines — Baeldung](https://www.baeldung.com/kotlin/coroutines-guide)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Ktor и зачем он нужен?](#q1--что-такое-ktor-и-зачем-он-нужен)
- [Q2. (!) Чем Ktor отличается от Spring Boot?](#q2--чем-ktor-отличается-от-spring-boot)
- [Q3. (!) Какие движки (engines) поддерживает Ktor?](#q3--какие-движки-engines-поддерживает-ktor)
- [Q4. Как создать простейшее Ktor-приложение?](#q4-как-создать-простейшее-ktor-приложение)

**Plugins (бывшие Features)**
- [Q5. (!) Что такое plugin в Ktor?](#q5--что-такое-plugin-в-ktor)
- [Q6. (!) Какие основные plugins существуют?](#q6--какие-основные-plugins-существуют)
- [Q7. Как написать собственный plugin?](#q7-как-написать-собственный-plugin)

**Маршрутизация**
- [Q8. (!) Как работает маршрутизация в Ktor?](#q8--как-работает-маршрутизация-в-ktor)
- [Q9. (!) Routing DSL — вложенные маршруты, параметры?](#q9--routing-dsl--вложенные-маршруты-параметры)
- [Q10. Как обрабатывать path и query параметры?](#q10-как-обрабатывать-path-и-query-параметры)
- [Q11. Type-safe routing через @Resource?](#q11-type-safe-routing-через-resource)

**Сериализация**
- [Q12. (!) Какие сериализаторы поддерживает Ktor?](#q12--какие-сериализаторы-поддерживает-ktor)
- [Q13. (!) ContentNegotiation — как настраивается?](#q13--contentnegotiation--как-настраивается)

**Корутины и асинхронность**
- [Q14. (!) Как Ktor использует корутины?](#q14--как-ktor-использует-корутины)
- [Q15. (!) Что такое CoroutineScope ApplicationCall?](#q15--что-такое-coroutinescope-applicationcall)
- [Q16. Как обработать структурную concurrency в эндпойнте?](#q16-как-обработать-структурную-concurrency-в-эндпойнте)

**Ktor Client**
- [Q17. (!) Что такое Ktor Client и его возможности?](#q17--что-такое-ktor-client-и-его-возможности)
- [Q18. Какие client engines доступны?](#q18-какие-client-engines-доступны)
- [Q19. (!) Как реализовать retry, timeout, logging в клиенте?](#q19--как-реализовать-retry-timeout-logging-в-клиенте)

**Аутентификация и безопасность**
- [Q20. (!) Какие схемы аутентификации поддерживает Ktor?](#q20--какие-схемы-аутентификации-поддерживает-ktor)
- [Q21. (!) Реализация JWT в Ktor?](#q21--реализация-jwt-в-ktor)
- [Q22. CORS, CSRF, Headers — настройка?](#q22-cors-csrf-headers--настройка)

**DI**
- [Q23. (!) Как реализовать DI в Ktor?](#q23--как-реализовать-di-в-ktor)
- [Q24. Koin или встроенные средства?](#q24-koin-или-встроенные-средства)

**Конфигурация**
- [Q25. Как организовать конфигурацию (application.conf)?](#q25-как-организовать-конфигурацию-applicationconf)
- [Q26. Environment variables и profiles?](#q26-environment-variables-и-profiles)

**Тестирование**
- [Q27. (!) Как тестировать Ktor-приложения?](#q27--как-тестировать-ktor-приложения)
- [Q28. testApplication vs withTestApplication?](#q28-testapplication-vs-withtestapplication)

**Сравнение и production**
- [Q29. (!) Когда выбирать Ktor вместо Spring Boot?](#q29--когда-выбирать-ktor-вместо-spring-boot)
- [Q30. (!) Production-ready: метрики, healthcheck, graceful shutdown?](#q30--production-ready-метрики-healthcheck-graceful-shutdown)
- [Q31. Производительность Ktor vs Spring WebFlux?](#q31-производительность-ktor-vs-spring-webflux)
- [Q32. (!) Какие минусы Ktor?](#q32--какие-минусы-ktor)

## Q1. (!) Что такое Ktor и зачем он нужен?

`Ktor` — фреймворк от JetBrains для построения **асинхронных серверных и клиентских приложений** на Kotlin. Построен на **корутинах**, использует **DSL** для конфигурации и роутинга.

**Ключевые особенности:**
- Lightweight — минимум магии, явная конфигурация
- Асинхронный по умолчанию — все обработчики `suspend`
- Modular — функциональность через plugins (раньше features)
- Multiplatform для клиента (JVM, Android, iOS, JS, Native)
- Тесно интегрирован с экосистемой Kotlin (Coroutines, Serialization)

**Применение:** REST API, WebSocket, gateway-сервисы, мобильные клиенты, легковесные микросервисы.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q2. (!) Чем Ktor отличается от Spring Boot? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Критерий | Ktor | Spring Boot |
|----------|------|-------------|
| Язык | Kotlin (first-class) | Java/Kotlin |
| Стиль | Functional + DSL | Annotation-based |
| DI | Внешний (Koin/Kodein) | Встроенный |
| Magic | Минимум | Много (autoconfig, AOP) |
| Размер JAR | ~5-10 MB | 20-50 MB |
| Cold start | < 1 сек | 3-15 сек |
| Концепция | Plugins (явно подключаемые) | Starters + autoconfig |
| Async model | Coroutines | WebFlux (Reactor) или Servlet |
| Размер сообщества | Меньше | Огромное |

**Когда Ktor лучше:**
- Чисто Kotlin-проект
- Нужен низкий cold start (serverless, Lambda)
- Хочется явности, мало "магии"
- Простой сервис без heavy enterprise needs

**Когда Spring Boot:**
- Много готовых интеграций (security, data, batch, integration)
- Большая команда с Java-бэкграундом
- Enterprise: Kafka, Cassandra, Hibernate с поддержкой "из коробки"


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q3. (!) Какие движки (engines) поддерживает Ktor? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Engine = реализация HTTP-сервера. Ktor можно запускать на:

| Engine | Особенности |
|--------|-------------|
| **Netty** | Default, async non-blocking, отлично для high-throughput |
| **Jetty** | Servlet API, можно деплоить в стандартный контейнер |
| **Tomcat** | То же — Servlet API |
| **CIO** | Pure-Kotlin реализация на корутинах, без Netty/Jetty |
| **ServletApplicationEngine** | Для деплоя в WAR/Servlet container |

```kotlin
// Netty — самый частый выбор
fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") { call.respondText("Hello, Ktor!") }
        }
    }.start(wait = true)
}

// CIO — pure Kotlin
embeddedServer(CIO, port = 8080) { ... }.start(wait = true)
```

**Netty vs CIO:** Netty быстрее на high-load, CIO легче и pure-Kotlin (нет Java reflection в hot path).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q4. Как создать простейшее Ktor-приложение? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
}

// Application.kt
fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello, World!")
            }
            get("/greet/{name}") {
                val name = call.parameters["name"] ?: "stranger"
                call.respondText("Hello, $name!")
            }
        }
    }.start(wait = true)
}
```

Запуск через `./gradlew run`. Никаких аннотаций, никакого автоматического сканирования.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. (!) Что такое plugin в Ktor? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`Plugin` (до 2.0 — `Feature`) — **переиспользуемый компонент**, расширяющий функциональность Ktor. Подключается через `install()`.

```kotlin
embeddedServer(Netty, port = 8080) {
    install(ContentNegotiation) {
        json()
    }
    install(CallLogging) {
        level = Level.INFO
    }
    install(Compression) {
        gzip()
        deflate()
    }
    install(Authentication) {
        jwt("auth-jwt") { /* ... */ }
    }

    routing { /* ... */ }
}
```

**Plugin может вешать обработчики** на разные стадии запроса: `Setup`, `Monitoring`, `Plugins`, `Call`, `Fallback`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. (!) Какие основные plugins существуют? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Plugin | Назначение |
|--------|------------|
| `ContentNegotiation` | Сериализация JSON/XML/YAML |
| `Authentication` | JWT, OAuth, Basic, Session |
| `CORS` | Cross-Origin Resource Sharing |
| `Compression` | gzip, deflate |
| `CallLogging` | Логирование запросов |
| `StatusPages` | Кастомные обработчики ошибок |
| `WebSockets` | WebSocket-эндпойнты |
| `Sessions` | Управление сессиями |
| `Metrics-Micrometer` | Интеграция с Prometheus, Datadog |
| `RateLimit` | Rate limiting (с 2.2+) |
| `RequestValidation` | Валидация входных данных |
| `Resources` | Type-safe routing |
| `HSTS`, `XForwardedHeader`, `ForwardedHeader` | Security headers |
| `DefaultHeaders` | Server header, X-Powered-By |
| `AutoHeadResponse` | Авто-обработка HEAD-запросов |


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как написать собственный plugin? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
// API plugin (старая версия)
class MyPluginConfig {
    var enabled: Boolean = true
}

val MyPlugin = createApplicationPlugin(
    name = "MyPlugin",
    createConfiguration = ::MyPluginConfig
) {
    val config = pluginConfig
    onCall { call ->
        if (config.enabled) {
            call.response.header("X-Custom-Header", "value")
        }
    }
}

// Использование
install(MyPlugin) {
    enabled = true
}
```

`createApplicationPlugin` доступен с Ktor 2.0. Hooks: `onCall`, `onCallReceive`, `onCallRespond`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. (!) Как работает маршрутизация в Ktor? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`routing` — DSL для определения **обработчиков HTTP-запросов**:

```kotlin
routing {
    get("/users") { call.respond(userService.findAll()) }

    post("/users") {
        val user = call.receive<User>()
        userService.save(user)
        call.respond(HttpStatusCode.Created, user)
    }

    route("/api/v1") {
        get("/health") { call.respondText("OK") }

        route("/users") {
            get { call.respond(userService.findAll()) }
            get("{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val user = userService.findById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(user)
            }
        }
    }
}
```

Под капотом — **routing tree** с матчингом по path, methods, content-types.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. (!) Routing DSL — вложенные маршруты, параметры? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
routing {
    // Path параметры
    get("/items/{id}") {
        val id = call.parameters["id"]
    }

    // Wildcard
    get("/files/{path...}") {
        val path = call.parameters.getAll("path")
    }

    // Опциональный параметр
    get("/articles/{lang?}") {
        val lang = call.parameters["lang"] ?: "en"
    }

    // Регулярные выражения
    get(Regex("/articles/(?<year>\\d{4})/(?<month>\\d{2})")) { /* ... */ }

    // Группировка по auth, headers
    authenticate("auth-jwt") {
        route("/admin") {
            get("/dashboard") { /* ... */ }
        }
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как обрабатывать path и query параметры? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
get("/search") {
    val query = call.request.queryParameters["q"]
    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
    call.respond(searchService.find(query, page))
}

get("/users/{id}") {
    val id = call.parameters["id"]?.toLongOrNull()
        ?: throw IllegalArgumentException("Invalid id")
}

// Headers
get("/me") {
    val token = call.request.header("Authorization")
}

// Receive body
post("/users") {
    val user = call.receive<User>()  // через ContentNegotiation
    val text = call.receiveText()    // raw text
    val parameters = call.receiveParameters() // form-urlencoded
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Type-safe routing через @Resource? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

С `Resources` plugin можно описывать маршруты через **классы с аннотациями**:

```kotlin
@Resource("/users")
class Users {
    @Resource("{id}")
    class Id(val parent: Users = Users(), val id: Long)
}

install(Resources)

routing {
    get<Users> { call.respond(userService.findAll()) }
    get<Users.Id> { req ->
        val user = userService.findById(req.id)
        call.respond(user ?: HttpStatusCode.NotFound)
    }
}

// Type-safe генерация URL
val url = application.href(Users.Id(id = 42L)) // "/users/42"
```

Плюсы: компилятор проверяет правильность параметров, нет magic strings.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. (!) Какие сериализаторы поддерживает Ktor? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Формат | Backend |
|--------|---------|
| JSON | `kotlinx.serialization`, Jackson, Gson |
| XML | `kotlinx.serialization`, Jackson XML |
| YAML | `kotlinx.serialization` (с 2.3+) |
| CBOR, ProtoBuf | `kotlinx.serialization` |

```kotlin
install(ContentNegotiation) {
    json(Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = false
    })
}

@Serializable
data class User(val id: Long, val name: String)

// В обработчике:
post("/users") {
    val user = call.receive<User>()  // автодесериализация
    call.respond(user)               // автосериализация
}
```

`kotlinx.serialization` — рекомендуемый выбор: compile-time, без рефлексии, хорошо работает с corutinами.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. (!) ContentNegotiation — как настраивается? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`ContentNegotiation` отвечает за **автоматическую сериализацию/десериализацию** в зависимости от `Content-Type` и `Accept` заголовков.

```kotlin
install(ContentNegotiation) {
    json()                                                  // application/json
    xml()                                                   // application/xml
    register(ContentType.Application.ProtoBuf, ProtoBufConverter())
}

// Обработчик автоматически выбирает формат
get("/user") {
    val user = User(1, "Alice")
    call.respond(user) // JSON или XML — по Accept header
}
```

Если ни один зарегистрированный formatter не подходит — `406 Not Acceptable`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. (!) Как Ktor использует корутины? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Все обработчики Ktor — `suspend` функции.** Каждый запрос запускается в отдельной корутине, что позволяет масштабироваться **тысячами одновременных запросов** на небольшом числе потоков.

```kotlin
get("/data") {
    // Это выполняется в корутине, не блокирует thread pool
    val result = withContext(Dispatchers.IO) {
        database.query("SELECT * FROM users")
    }
    call.respond(result)
}

// Параллельные запросы
get("/aggregate") {
    coroutineScope {
        val users = async { userService.findAll() }
        val products = async { productService.findAll() }
        call.respond(mapOf("users" to users.await(), "products" to products.await()))
    }
}
```

В отличие от Spring MVC (один thread на запрос), Ktor **не блокирует** thread пока ждёт I/O.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. (!) Что такое CoroutineScope ApplicationCall? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`ApplicationCall` (он же `call` в обработчиках) реализует `CoroutineScope` со scope'ом, привязанным к жизненному циклу запроса.

```kotlin
get("/long") {
    // launch создаёт fire-and-forget корутину в scope запроса
    call.application.launch {
        // Эта работа отменится, если соединение оборвётся
        backgroundTask()
    }
    call.respondText("Started")
}
```

**Важно:** при отмене запроса (клиент закрыл соединение) — все дочерние корутины **автоматически отменяются**. Это структурная concurrency.

Подробнее — в [Kotlin Coroutines](../../programming-languages/kotlin/kotlin-coroutines-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Как обработать структурную concurrency в эндпойнте? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
get("/dashboard") {
    coroutineScope {
        val a = async { fetchA() }
        val b = async { fetchB() }
        val c = async { fetchC() }
        // Если любой бросит — все остальные отменятся (structured concurrency)
        call.respond(Dashboard(a.await(), b.await(), c.await()))
    }
}

// Timeout
get("/slow") {
    val result = withTimeoutOrNull(5.seconds) {
        slowOperation()
    } ?: return@get call.respond(HttpStatusCode.GatewayTimeout)
    call.respond(result)
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. (!) Что такое Ktor Client и его возможности? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`Ktor Client` — HTTP-клиент на корутинах. Multiplatform (JVM, Android, iOS, JS, Native).

```kotlin
val client = HttpClient(CIO) {
    install(ContentNegotiation) { json() }
    install(Logging) { level = LogLevel.INFO }
    install(HttpTimeout) {
        requestTimeoutMillis = 5000
    }
}

suspend fun fetchUser(id: Long): User {
    return client.get("https://api.example.com/users/$id").body()
}

// POST с телом
suspend fun createUser(user: User): User {
    return client.post("https://api.example.com/users") {
        contentType(ContentType.Application.Json)
        setBody(user)
    }.body()
}

client.close() // важно при завершении
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. Какие client engines доступны? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Engine | Платформа | Особенности |
|--------|-----------|-------------|
| **CIO** | Multiplatform | Pure Kotlin, корутины |
| **OkHttp** | JVM, Android | Mature, много фич |
| **Apache** | JVM | Apache HttpComponents |
| **Java** | JVM 11+ | Стандартный `HttpClient` JDK |
| **Curl** | Native | Через libcurl |
| **Js** | JS | Browser fetch |
| **Darwin** | iOS, macOS | NSURLSession |

```kotlin
val client = HttpClient(OkHttp) {
    engine {
        config { followRedirects(true) }
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) Как реализовать retry, timeout, logging в клиенте? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
val client = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = 5000
        connectTimeoutMillis = 1000
        socketTimeoutMillis = 10000
    }

    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 3)
        exponentialDelay()
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
        sanitizeHeader { it == HttpHeaders.Authorization }
    }

    install(DefaultRequest) {
        url("https://api.example.com")
        header(HttpHeaders.Authorization, "Bearer $token")
    }
}
```

`HttpRequestRetry` поддерживает custom condition, exponential backoff. Удобнее, чем писать свой Retry.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. (!) Какие схемы аутентификации поддерживает Ktor? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Схема | Описание |
|-------|----------|
| `basic` | HTTP Basic auth |
| `digest` | HTTP Digest |
| `bearer` | Custom bearer token |
| `jwt` | JWT-токены (RS256, HS256, ...) |
| `oauth` | OAuth 1.0/2.0 (для клиента) |
| `session` | Cookie sessions |
| `form` | Form-based login |
| `ldap` | LDAP |

```kotlin
install(Authentication) {
    basic("auth-basic") {
        realm = "Access to /admin"
        validate { credentials ->
            if (credentials.name == "admin" && credentials.password == "secret") {
                UserIdPrincipal(credentials.name)
            } else null
        }
    }

    jwt("auth-jwt") {
        realm = "Access to /api"
        verifier(JWT.require(Algorithm.HMAC256("secret")).build())
        validate { credential ->
            if (credential.payload.getClaim("username").asString() != "")
                JWTPrincipal(credential.payload) else null
        }
    }
}

routing {
    authenticate("auth-jwt") {
        get("/api/me") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            call.respond(mapOf("hello" to username))
        }
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. (!) Реализация JWT в Ktor? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
fun Application.configureSecurity() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = "myapp"
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString().isNotBlank()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token expired or invalid")
            }
        }
    }
}

// Генерация токена
fun generateToken(username: String): String =
    JWT.create()
       .withIssuer(jwtIssuer)
       .withAudience(jwtAudience)
       .withClaim("username", username)
       .withExpiresAt(Date(System.currentTimeMillis() + 60_000))
       .sign(Algorithm.HMAC256(jwtSecret))
```

Подробнее — в [JWT](../../security/jwt-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. CORS, CSRF, Headers — настройка? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
install(CORS) {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
    allowHeader(HttpHeaders.Authorization)
    allowHost("client.example.com", schemes = listOf("https"))
    allowCredentials = true
}

install(DefaultHeaders) {
    header("X-Frame-Options", "DENY")
    header("X-Content-Type-Options", "nosniff")
}

install(HSTS) {
    maxAgeInSeconds = 365.days.inWholeSeconds
}
```

CSRF — через `Sessions` plugin или собственная middleware. В Ktor нет встроенной защиты, как у Spring Security.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. (!) Как реализовать DI в Ktor? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

В Ktor **нет встроенного DI**. Подходы:

1. **Manual DI** — передавать зависимости в функции конфигурации
2. **Koin** — самый популярный для Kotlin
3. **Kodein-DI** — альтернатива
4. **Dagger/Hilt** — реже, для multiplatform


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. Koin или встроенные средства? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
// build.gradle.kts
implementation("io.insert-koin:koin-ktor:3.5.0")
implementation("io.insert-koin:koin-logger-slf4j:3.5.0")

// Application.kt
val appModule = module {
    single { UserRepository() }
    single { UserService(get()) }
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        install(Koin) {
            slf4jLogger()
            modules(appModule)
        }

        routing {
            val userService by inject<UserService>()
            get("/users") { call.respond(userService.findAll()) }
        }
    }.start(wait = true)
}
```

**Manual DI:**

```kotlin
fun Application.module(userService: UserService = UserService()) {
    routing {
        get("/users") { call.respond(userService.findAll()) }
    }
}
```

Manual DI достаточно для маленьких проектов. Koin — для всего остального.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. Как организовать конфигурацию (application.conf)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```hocon
// resources/application.conf
ktor {
    deployment {
        port = 8080
        port = ${?PORT}
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}

jwt {
    secret = "secret"
    secret = ${?JWT_SECRET}
    issuer = "myapp"
    audience = "users"
}

database {
    url = "jdbc:postgresql://localhost:5432/mydb"
    driver = "org.postgresql.Driver"
}
```

```kotlin
val secret = environment.config.property("jwt.secret").getString()
val port = environment.config.propertyOrNull("server.port")?.getString()?.toInt() ?: 8080
```

Можно использовать YAML с `application.yaml` (требует `ktor-server-config-yaml`).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. Environment variables и profiles? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`${?VAR_NAME}` — подстановка из env. Для разных environment'ов:

```bash
# Запуск с другим конфигом
java -Dconfig.file=application-prod.conf -jar app.jar

# Через env var
KTOR_DEPLOYMENT_PORT=9090 ./gradlew run
```

Нет встроенных profiles как в Spring. Подходы:
- Разные `application.conf` для dev/prod
- Условия в HOCON: `if (...)` через `Config.withFallback`


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. (!) Как тестировать Ktor-приложения? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

С Ktor 2.0+ — `testApplication`:

```kotlin
class UserApiTest {
    @Test
    fun `GET users returns list`() = testApplication {
        application {
            module() // ваш Application.module
        }

        client.get("/users").apply {
            assertEquals(HttpStatusCode.OK, status)
            val users = body<List<User>>()
            assertTrue(users.isNotEmpty())
        }
    }

    @Test
    fun `POST user creates new user`() = testApplication {
        application { module() }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val response = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(User(0, "Alice"))
        }
        assertEquals(HttpStatusCode.Created, response.status)
    }
}
```

`testApplication` запускает в-memory сервер, не открывая порт.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. testApplication vs withTestApplication? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Подход | Версия |
|--------|--------|
| `withTestApplication { handleRequest(...) }` | Ktor 1.x (deprecated) |
| `testApplication { client.get(...) }` | Ktor 2.x+ |

Новый подход проще — используется тот же `HttpClient` API, что и в production. Старый — менее идиоматичный.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q29. (!) Когда выбирать Ktor вместо Spring Boot? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Выбирай Ktor когда:**
- Чисто Kotlin-команда, проект на Kotlin
- Нужен низкий cold start (AWS Lambda, Cloud Functions)
- Микросервис без heavy enterprise needs
- Хочется простоты, явности, минимума магии
- Multiplatform клиент (iOS + JVM)
- Хочется control над всем (нет autoconfig)

**Выбирай Spring Boot когда:**
- Большая команда, смешанная (Java + Kotlin)
- Нужны готовые интеграции (Spring Data JPA, Spring Security, Batch, Integration)
- Enterprise стандарты, поддержка вендорами
- Нужны annotation-based и declarative подходы
- Куча готовых starters


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q30. (!) Production-ready: метрики, healthcheck, graceful shutdown? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```kotlin
install(MicrometerMetrics) {
    registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
}

routing {
    get("/health") { call.respond(mapOf("status" to "UP")) }
    get("/metrics") {
        call.respond(prometheusRegistry.scrape())
    }
}

// Graceful shutdown
val server = embeddedServer(Netty, port = 8080) { ... }
Runtime.getRuntime().addShutdownHook(Thread {
    server.stop(gracePeriodMillis = 1000, timeoutMillis = 5000)
})
server.start(wait = true)
```

В отличие от Spring Boot Actuator, готового набора endpoints нет — собираешь руками. Подробнее — в [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q31. Производительность Ktor vs Spring WebFlux? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Оба построены на async модели:
- **Ktor (Netty + coroutines):** ~150-200K req/s в простых benchmark'ах
- **Spring WebFlux (Netty + Reactor):** ~140-180K req/s

Разница в пределах 5-15%, обычно зависит от workload и настройки. **Ktor** обычно немного быстрее на простых запросах за счёт меньшего overhead, но разница не критична.

**Spring MVC (синхронный)** — заметно медленнее обоих под high concurrency.

Подробнее — в [Spring WebFlux](../spring/spring-webflux-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q32. (!) Какие минусы Ktor? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **Меньше готовых интеграций** — для PostgreSQL, Redis, Kafka надо писать обёртки самим
2. **Нет встроенного DI** — выбирать сторонний (Koin/Kodein)
3. **Меньше документации/StackOverflow** по сравнению со Spring
4. **Нет автоматических security best practices** — CORS, CSRF, rate limiting настраивать руками
5. **Меньшее community** — меньше библиотек, плагинов, статей
6. **Production setup** требует больше рук — метрики, healthcheck, graceful shutdown
7. **Нет официальной интеграции с ORM** — Exposed (тоже от JetBrains) или Hibernate, Ktorm — но всё неофициально
8. **Annotations нет** — кому-то это плюс, кому-то минус

Ktor — для команд, готовых **писать больше кода**, но взамен получать больше **контроля** и более **простой стек**.

---

## See also

- [Spring Boot](../spring/spring-boot-interview.md) — основной конкурент
- [Spring WebFlux](../spring/spring-webflux-interview.md) — реактивный аналог
- [Spring MVC](../spring/spring-mvc-interview.md) — синхронная альтернатива
- [Kotlin](../../programming-languages/kotlin/kotlin-interview.md) — основной язык Ktor
- [Kotlin Coroutines](../../programming-languages/kotlin/kotlin-coroutines-interview.md) — основа async-модели Ktor
- [DSL в Kotlin](../../programming-languages/kotlin/kotlin-dsl-interview.md) — routing DSL построен на этом
- [Сериализация в Kotlin](../../programming-languages/kotlin/kotlin-serialization-interview.md) — kotlinx.serialization
- [Quarkus](quarkus-interview.md) — другой lightweight JVM фреймворк
- [Micronaut](micronaut-interview.md) — compile-time DI альтернатива
- [Vert.x](vertx-interview.md) — event-driven JVM фреймворк
- [JWT](../../security/jwt-interview.md) — реализация в Ktor
- [OAuth2](../../security/oauth2-interview.md) — auth integration
- [Микросервисы](../../architecture/microservices-interview.md) — где Ktor хорош
- [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md) — production observability контраст


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Micronaut](micronaut-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Quarkus](quarkus-interview.md)
- [Vert.x](vertx-interview.md)
- [Spring AOP](../spring/spring-aop-interview.md)
- [Spring Batch](../spring/spring-batch-interview.md)
- [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md)
