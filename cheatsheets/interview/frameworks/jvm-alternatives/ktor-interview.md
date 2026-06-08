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

`Ktor` — асинхронный фреймворк от JetBrains для построения server- и client-приложений на Kotlin. Построен на корутинах и расширяется через **plugins** (раньше — features). Главная идея — лёгкость и явность: минимум скрытого поведения и тесная интеграция с экосистемой Kotlin.

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
- [Q9. (!) Какие виды параметров и вложенных маршрутов поддерживает routing DSL?](#q9--какие-виды-параметров-и-вложенных-маршрутов-поддерживает-routing-dsl)
- [Q10. Как обрабатывать path и query параметры?](#q10-как-обрабатывать-path-и-query-параметры)
- [Q11. Что такое type-safe routing через @Resource?](#q11-что-такое-type-safe-routing-через-resource)

**Сериализация**
- [Q12. (!) Какие сериализаторы поддерживает Ktor?](#q12--какие-сериализаторы-поддерживает-ktor)
- [Q13. (!) Как настраивается ContentNegotiation?](#q13--как-настраивается-contentnegotiation)

**Корутины и асинхронность**
- [Q14. (!) Как Ktor использует корутины?](#q14--как-ktor-использует-корутины)
- [Q15. (!) Что такое CoroutineScope у ApplicationCall?](#q15--что-такое-coroutinescope-у-applicationcall)
- [Q16. Как применить структурную concurrency в эндпойнте?](#q16-как-применить-структурную-concurrency-в-эндпойнте)

**Ktor Client**
- [Q17. (!) Что такое Ktor Client и какие у него возможности?](#q17--что-такое-ktor-client-и-какие-у-него-возможности)
- [Q18. Какие client engines доступны?](#q18-какие-client-engines-доступны)
- [Q19. (!) Как реализовать retry, timeout и logging в клиенте?](#q19--как-реализовать-retry-timeout-и-logging-в-клиенте)

**Аутентификация и безопасность**
- [Q20. (!) Какие схемы аутентификации поддерживает Ktor?](#q20--какие-схемы-аутентификации-поддерживает-ktor)
- [Q21. (!) Как реализовать JWT-аутентификацию в Ktor?](#q21--как-реализовать-jwt-аутентификацию-в-ktor)
- [Q22. Как настраиваются CORS, CSRF и security-заголовки?](#q22-как-настраиваются-cors-csrf-и-security-заголовки)

**DI**
- [Q23. (!) Как реализовать DI в Ktor?](#q23--как-реализовать-di-в-ktor)
- [Q24. Что выбрать — Koin или ручное внедрение зависимостей?](#q24-что-выбрать--koin-или-ручное-внедрение-зависимостей)

**Конфигурация**
- [Q25. Как организовать конфигурацию (application.conf)?](#q25-как-организовать-конфигурацию-applicationconf)
- [Q26. Как работать с переменными окружения и профилями?](#q26-как-работать-с-переменными-окружения-и-профилями)

**Тестирование**
- [Q27. (!) Как тестировать Ktor-приложения?](#q27--как-тестировать-ktor-приложения)
- [Q28. Чем `testApplication` отличается от `withTestApplication`?](#q28-чем-testapplication-отличается-от-withtestapplication)

**Сравнение и production**
- [Q29. (!) Когда выбирать Ktor вместо Spring Boot?](#q29--когда-выбирать-ktor-вместо-spring-boot)
- [Q30. (!) Как сделать Ktor production-ready: метрики, healthcheck, graceful shutdown?](#q30--как-сделать-ktor-production-ready-метрики-healthcheck-graceful-shutdown)
- [Q31. Что быстрее — Ktor или Spring WebFlux?](#q31-что-быстрее--ktor-или-spring-webflux)
- [Q32. (!) Какие минусы у Ktor?](#q32--какие-минусы-у-ktor)

## Q1. (!) Что такое Ktor и зачем он нужен?

`Ktor` — фреймворк от JetBrains для построения **асинхронных серверных и клиентских приложений** на Kotlin. Вместо аннотаций и автоконфигурации он предлагает явный **DSL** для настройки и маршрутизации, а в основе лежат **корутины** — поэтому каждый запрос обрабатывается без блокировки потока.

Главная идея — минимум скрытого поведения: всё, что делает приложение, видно в коде конфигурации, а нужная функциональность подключается явным `install()`.

**Ключевые особенности:**
- Lightweight — минимум магии, явная конфигурация, ничего не происходит «само собой»
- Асинхронность по умолчанию — все обработчики `suspend`, I/O не блокирует поток
- Модульность — функциональность добавляется plugins (до 2.0 — features), а не зашита в ядро
- Multiplatform для клиента — один и тот же код работает на JVM, Android, iOS, JS, Native
- Тесная интеграция с экосистемой Kotlin (Coroutines, Serialization)

**Сценарии применения:** REST API, WebSocket, gateway-сервисы, мобильные клиенты, легковесные микросервисы — то есть случаи, где ценят простоту и быстрый старт.

## Q2. (!) Чем Ktor отличается от Spring Boot?

Коротко: Ktor — это явный минималистичный DSL-фреймворк на корутинах, Spring Boot — большой декларативный стек с автоконфигурацией и встроенным DI. Ktor даёт контроль и быстрый старт ценой того, что многое приходится собирать руками; Spring Boot даёт готовые интеграции ценой «магии» и более тяжёлого рантайма.

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

**Когда лучше Ktor:**
- Проект целиком на Kotlin
- Нужен низкий cold start (serverless, Lambda) — старт меньше секунды против секунд у Spring
- Важна явность, минимум «магии» и автоконфигурации
- Простой сервис без тяжёлых enterprise-требований

**Когда лучше Spring Boot:**
- Нужно много готовых интеграций (security, data, batch, integration)
- Большая команда с Java-бэкграундом
- Enterprise-стек: Kafka, Cassandra, Hibernate с поддержкой «из коробки»

## Q3. (!) Какие движки (engines) поддерживает Ktor?

Engine в Ktor — это реализация HTTP-сервера, которая принимает соединения и передаёт их в приложение. Ядро Ktor от движка не зависит: один и тот же код роутинга работает поверх любого из них, а выбор движка влияет на производительность и способ деплоя.

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

**Netty против CIO:** Netty быстрее под высокой нагрузкой и зрелее, поэтому он — выбор по умолчанию. CIO легче и написан целиком на Kotlin (нет Java reflection в hot path), что удобно, когда хочется минимум JVM-зависимостей.

## Q4. Как создать простейшее Ktor-приложение?

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

Запуск через `./gradlew run`. Здесь видна суть подхода Ktor: сервер собирается явно в `embeddedServer`, маршруты описываются в DSL `routing`, и нет ни аннотаций, ни автоматического сканирования classpath — приложение делает ровно то, что написано.

## Q5. (!) Что такое plugin в Ktor?

`Plugin` (до 2.0 — `Feature`) — это **переиспользуемый компонент**, который добавляет приложению новую возможность: сериализацию, логирование, аутентификацию, сжатие и т.д. Поскольку ядро Ktor минимально, почти вся функциональность подключается явно через `install()` — это и есть способ расширения фреймворка.

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

Технически plugin работает как перехватчик: он вешает обработчики на разные стадии прохождения запроса через pipeline — `Setup`, `Monitoring`, `Plugins`, `Call`, `Fallback`. Порядок стадий определяет, когда именно plugin вмешается (например, `Monitoring` — раньше бизнес-логики, что удобно для логирования и метрик).

## Q6. (!) Какие основные plugins существуют?

Plugins покрывают типичные потребности веб-сервиса — сериализацию, безопасность, наблюдаемость, ограничение нагрузки. На собеседовании достаточно помнить, какой plugin за что отвечает; вот основные:

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

## Q7. Как написать собственный plugin?

Собственный plugin создаётся функцией `createApplicationPlugin`: ей передают имя, опциональную конфигурацию и блок, где навешиваются hooks на стадии запроса. Внутри блока через `onCall` (и родственные `onCallReceive`/`onCallRespond`) можно вмешаться в обработку — например, добавить заголовок к каждому ответу.

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

`createApplicationPlugin` доступен с Ktor 2.0. Основные hooks: `onCall` (перед обработкой запроса), `onCallReceive` (при чтении тела) и `onCallRespond` (при формировании ответа).

## Q8. (!) Как работает маршрутизация в Ktor?

Маршрутизация задаётся через DSL `routing`: внутри него методами `get`, `post`, `route` и т.п. описываются **обработчики HTTP-запросов**. Маршруты можно вкладывать друг в друга через `route`, чтобы выделить общий префикс пути (например, `/api/v1`) и не повторять его.

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

Под капотом маршруты складываются в **дерево маршрутов** (routing tree): входящий запрос сопоставляется с ним по пути, HTTP-методу и content-type, и побеждает наиболее специфичный узел.

## Q9. (!) Какие виды параметров и вложенных маршрутов поддерживает routing DSL?

Routing DSL умеет извлекать параметры из пути в разных формах — обязательные, опциональные, wildcard и по регулярному выражению, — а также группировать маршруты по общим признакам (префикс, аутентификация, заголовки). Доступ к значениям — через `call.parameters`.

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

## Q10. Как обрабатывать path и query параметры?

Path-параметры (часть пути, например `{id}`) читаются через `call.parameters`, query-параметры (после `?`) — через `call.request.queryParameters`. Оба возвращают строку или `null`, поэтому преобразование типов и значения по умолчанию делают вручную (`toLongOrNull()`, `?: default`). Заголовки берутся через `call.request.header(...)`, тело запроса — через `call.receive*`.

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

## Q11. Что такое type-safe routing через @Resource?

Это способ описывать маршруты не строками, а **классами с аннотацией `@Resource`** — структура URL и его параметры выражаются типами. Включается plugin `Resources`. Главное преимущество: компилятор проверяет наличие и типы параметров, а ссылки можно генерировать через `href(...)` — никаких magic strings, которые легко рассинхронизировать с обработчиком.

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

**Плюсы:** компилятор проверяет правильность параметров, нет magic strings, рефакторинг URL безопасен. **Минус:** больше шаблонного кода с классами, чем при обычных строковых маршрутах.

## Q12. (!) Какие сериализаторы поддерживает Ktor?

Сериализация в Ktor подключается через plugin `ContentNegotiation`, а конкретный формат и его backend выбираются явно. Поддерживаются основные форматы — JSON, XML, YAML, бинарные CBOR/ProtoBuf, — причём для каждого можно выбрать реализацию.

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

**Рекомендация:** `kotlinx.serialization` — выбор по умолчанию для Ktor. Он генерирует код сериализации на этапе компиляции, не использует рефлексию и нативно интегрирован с Kotlin, включая работу с корутинами.

## Q13. (!) Как настраивается ContentNegotiation?

`ContentNegotiation` отвечает за **автоматическую сериализацию и десериализацию** тела запроса/ответа. При установке plugin'а регистрируют конвертеры для нужных форматов, а дальше Ktor сам выбирает формат: тело запроса разбирает по `Content-Type`, а ответ кодирует по `Accept`-заголовку клиента. Благодаря этому в обработчике достаточно `call.receive<T>()` и `call.respond(obj)` — без ручного парсинга.

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

**Граничный случай:** если ни один зарегистрированный конвертер не подходит под `Accept`-заголовок, Ktor вернёт `406 Not Acceptable`.

## Q14. (!) Как Ktor использует корутины?

**Все обработчики Ktor — это `suspend`-функции, а каждый запрос выполняется в отдельной корутине.** Ключевое следствие: пока обработчик ждёт I/O (запрос к БД, вызов другого сервиса), он не держит поток — корутина приостанавливается, а поток освобождается под другие запросы. Поэтому небольшой пул потоков обслуживает тысячи одновременных соединений.

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

В отличие от Spring MVC, где на каждый запрос выделяется отдельный поток и он блокируется на время I/O, Ktor поток **не блокирует** — отсюда лучшая масштабируемость под высокой конкуррентностью.

**Подводный камень:** выгода теряется, если внутри обработчика вызвать блокирующий код (JDBC, файловый I/O) напрямую — он заблокирует поток событийного цикла. Такие операции нужно выносить в `withContext(Dispatchers.IO)`, как в примере выше.

## Q15. (!) Что такое CoroutineScope у ApplicationCall?

`ApplicationCall` (тот самый `call` в обработчиках) сам является `CoroutineScope`, привязанным к жизненному циклу запроса. Практический смысл: любые корутины, запущенные в этом scope, живут ровно столько, сколько живёт запрос, и завершаются вместе с ним.

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

**Важно:** при отмене запроса (например, клиент закрыл соединение) все дочерние корутины **отменяются автоматически** — это структурная concurrency в действии. За счёт этого не утекают фоновые задачи, привязанные к уже мёртвому запросу.

Подробнее — в [Kotlin Coroutines](../../programming-languages/kotlin/kotlin-coroutines-interview.md).

## Q16. Как применить структурную concurrency в эндпойнте?

Когда нужно сходить за несколькими источниками данных параллельно, их оборачивают в `coroutineScope` и запускают через `async`. Структурная concurrency гарантирует две вещи: scope не завершится, пока не отработают все дочерние корутины, а если любая из них упадёт — остальные будут отменены, и исключение прокинется наружу. Ограничить время выполнения помогает `withTimeoutOrNull`.

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

## Q17. (!) Что такое Ktor Client и какие у него возможности?

`Ktor Client` — это асинхронный HTTP-клиент на корутинах, парный к серверной части Ktor. Главное отличие от обычных JVM-клиентов — multiplatform: один и тот же код работает на JVM, Android, iOS, JS и Native, а конкретная реализация транспорта подбирается через engine. Расширяется он теми же plugins, что и сервер: `ContentNegotiation` для сериализации, `Logging`, `HttpTimeout`, `HttpRequestRetry` и др.

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

**Подводный камень:** `HttpClient` владеет пулом соединений и потоками engine, поэтому его создают один раз и переиспользуют, а в конце жизни обязательно закрывают через `close()` — иначе утекут ресурсы.

## Q18. Какие client engines доступны?

Как и на сервере, клиентский engine — это сменный транспорт под единым API: код запросов не меняется, меняется только реализация под платформу или нужный набор возможностей. На JVM популярны OkHttp (зрелый, много фич) и стандартный JDK-`HttpClient`, на мобильных — Darwin (iOS/macOS), для multiplatform — pure-Kotlin CIO.

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

## Q19. (!) Как реализовать retry, timeout и logging в клиенте?

Всё это — отдельные plugins, которые подключаются в конфигурации клиента, а не пишутся руками:

- **Timeout** — plugin `HttpTimeout` задаёт три независимых таймаута: на весь запрос, на установку соединения и на чтение сокета.
- **Retry** — plugin `HttpRequestRetry` повторяет запрос по условию (например, при 5xx) с настраиваемой стратегией задержки, включая `exponentialDelay()`.
- **Logging** — plugin `Logging` пишет запросы/ответы; через `sanitizeHeader` можно скрыть чувствительные заголовки вроде `Authorization`.
- **DefaultRequest** — задаёт общие параметры (базовый URL, общие заголовки) для всех запросов.

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

`HttpRequestRetry` поддерживает произвольное условие повтора и exponential backoff — это удобнее и надёжнее, чем городить собственную логику retry поверх клиента.

## Q20. (!) Какие схемы аутентификации поддерживает Ktor?

Аутентификация в Ktor — это plugin `Authentication`, внутри которого регистрируют одну или несколько именованных схем. Каждая схема описывает, как извлечь учётные данные из запроса и проверить их через блок `validate`, возвращающий `Principal` при успехе или `null` при отказе. Затем маршруты защищают через `authenticate("имя-схемы") { ... }`.

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

## Q21. (!) Как реализовать JWT-аутентификацию в Ktor?

JWT настраивается схемой `jwt` внутри plugin'а `Authentication`. Нужно задать три вещи: `verifier` — проверку подписи и обязательных claim'ов (issuer, audience, алгоритм), `validate` — дополнительную бизнес-проверку payload'а с возвратом `JWTPrincipal`, и опционально `challenge` — ответ при невалидном токене. Секрет и параметры issuer/audience обычно читают из конфигурации, а не хардкодят.

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

## Q22. Как настраиваются CORS, CSRF и security-заголовки?

В Ktor безопасность не включена «из коробки» — её собирают из отдельных plugins. `CORS` разрешает кросс-доменные запросы (явно перечисляют методы, заголовки, хосты), `DefaultHeaders` добавляет защитные заголовки ко всем ответам, `HSTS` форсит HTTPS. Это плата за минимализм: то, что Spring Security делает декларативно, здесь настраивается вручную.

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

**CSRF:** готовой встроенной защиты, как в Spring Security, нет — её реализуют через `Sessions` plugin (например, токен в сессии) или собственный перехватчик. Это типичный пример того, что в Ktor security-механизмы нужно достраивать самому.

## Q23. (!) Как реализовать DI в Ktor?

В Ktor **нет встроенного DI-контейнера** — в отличие от Spring, фреймворк не управляет жизненным циклом зависимостей сам. Это осознанное решение в духе минимализма: способ внедрения зависимостей выбирает разработчик.

Основные подходы — от самого простого к более масштабируемым:

1. **Manual DI** — создавать зависимости вручную и передавать их параметрами в функции конфигурации; достаточно для небольших проектов
2. **Koin** — самый популярный DI-контейнер в экосистеме Kotlin, есть готовая интеграция `koin-ktor`
3. **Kodein-DI** — альтернативный лёгкий контейнер
4. **Dagger/Hilt** — реже, в основном для multiplatform/Android-сценариев

## Q24. Что выбрать — Koin или ручное внедрение зависимостей?

Эмпирическое правило: для маленького сервиса достаточно ручного DI (создать объекты и пробросить параметрами в `module`), а как только графа зависимостей становится много и появляются разные scope, удобнее Koin. Koin подключается plugin'ом `install(Koin)`, модули описывают фабрики (`single`, `factory`), а в обработчиках зависимости достают через `inject<T>()`.

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

**Итог:** ручной DI достаточно для маленьких проектов и даёт максимальную прозрачность; Koin окупается на проектах побольше, где зависимостей много и их хочется собирать декларативно.

## Q25. Как организовать конфигурацию (application.conf)?

Внешняя конфигурация Ktor по умолчанию хранится в `application.conf` в формате HOCON. В нём задают порт, список модулей приложения и любые свои секции (jwt, database). Значения читают из кода через `environment.config.property("ключ").getString()`. Главная фишка HOCON — подстановка из окружения через `${?VAR}`: если переменная задана, она переопределяет дефолт, иначе берётся значение из файла.

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

Вместо HOCON можно использовать YAML (`application.yaml`) — для этого нужна зависимость `ktor-server-config-yaml`.

## Q26. Как работать с переменными окружения и профилями?

Переменные окружения подставляются прямо в HOCON через синтаксис `${?VAR_NAME}` — это основной способ параметризовать конфиг под среду без отдельного механизма профилей.

```bash
# Запуск с другим конфигом
java -Dconfig.file=application-prod.conf -jar app.jar

# Через env var
KTOR_DEPLOYMENT_PORT=9090 ./gradlew run
```

**Важный нюанс:** встроенных профилей (`spring.profiles.active`) в Ktor нет. Разделение dev/prod делают вручную:
- держат отдельные `application.conf` под каждую среду и подсовывают нужный через `-Dconfig.file`
- или комбинируют конфиги через `Config.withFallback`, накладывая среду-специфичные значения поверх базовых

## Q27. (!) Как тестировать Ktor-приложения?

Начиная с Ktor 2.0, тесты пишут через билдер `testApplication`. Он поднимает приложение **в памяти, без открытия реального порта**, и даёт тот же `HttpClient`, что используется в production-коде. Это удобно: тесты идут быстро, а API запросов одинаков с боевым.

Внутри `testApplication` блок `application { ... }` подключает ваш `Application.module`, а `client.get(...)`/`client.post(...)` отправляют запросы и проверяют ответы. Для тел в JSON клиенту тоже ставят `ContentNegotiation`.

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

Ключевой момент: `testApplication` запускает сервер в памяти и не открывает сетевой порт — поэтому тесты изолированы и не конфликтуют по портам.

## Q28. Чем `testApplication` отличается от `withTestApplication`?

Это два поколения тестового API: `withTestApplication` — старый способ из Ktor 1.x, `testApplication` — современный из 2.x.

| Подход | Версия |
|--------|--------|
| `withTestApplication { handleRequest(...) }` | Ktor 1.x (deprecated) |
| `testApplication { client.get(...) }` | Ktor 2.x+ |

Принципиальная разница в том, как формируется запрос. Старый `withTestApplication` работает через низкоуровневый `handleRequest`, где заголовки и тело собираются вручную. Новый `testApplication` использует тот же `HttpClient`, что и production-код, поэтому тесты читаются идиоматичнее и ближе к реальному вызову. В новых проектах используют только `testApplication`.

## Q29. (!) Когда выбирать Ktor вместо Spring Boot?

Выбор сводится к компромиссу «контроль и лёгкость против готовых интеграций и экосистемы». Ktor выигрывает там, где ценят простой стек и быстрый старт; Spring Boot — там, где важны зрелые enterprise-возможности из коробки.

**Выбирай Ktor, когда:**
- Команда и проект целиком на Kotlin
- Нужен низкий cold start (AWS Lambda, Cloud Functions)
- Это микросервис без тяжёлых enterprise-требований
- Важны простота, явность, минимум магии
- Нужен multiplatform-клиент (iOS + JVM из одного кода)
- Хочется полного контроля без скрытой автоконфигурации

**Выбирай Spring Boot, когда:**
- Большая смешанная команда (Java + Kotlin)
- Нужны готовые интеграции (Spring Data JPA, Spring Security, Batch, Integration)
- Действуют enterprise-стандарты и важна поддержка вендорами
- Удобнее аннотации и декларативный стиль
- Нужно много готовых starters

## Q30. (!) Как сделать Ktor production-ready: метрики, healthcheck, graceful shutdown?

В отличие от Spring Boot с готовым Actuator, в Ktor эти вещи собираются вручную, но из стандартных кирпичей:

- **Метрики** — plugin `MicrometerMetrics` с нужным registry (например, Prometheus); экспортируются отдельным эндпойнтом
- **Healthcheck** — обычный маршрут вроде `/health`, который отвечает статусом сервиса
- **Graceful shutdown** — `server.stop(gracePeriod, timeout)` в shutdown-hook: сервер перестаёт принимать новые соединения и даёт текущим запросам завершиться

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

**Компромисс:** готового набора production-эндпойнтов, как у Spring Boot Actuator, нет — больше ручной работы, но и полный контроль над тем, что именно выставляется наружу. Подробнее — в [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md).

## Q31. Что быстрее — Ktor или Spring WebFlux?

Оба построены на асинхронной non-blocking модели поверх Netty, поэтому по производительности они близки:

- **Ktor (Netty + coroutines):** ~150-200K req/s в простых benchmark'ах
- **Spring WebFlux (Netty + Reactor):** ~140-180K req/s

Разница обычно в пределах 5-15% и сильно зависит от характера нагрузки и настройки. Ktor чаще немного впереди на простых запросах за счёт меньшего overhead, но на практике это редко решающий фактор.

Важнее другое: **синхронный Spring MVC** (поток на запрос) заметно проигрывает обоим под высокой конкуррентностью — именно там async-модель и даёт основной выигрыш.

Подробнее — в [Spring WebFlux](../spring/spring-webflux-interview.md).

## Q32. (!) Какие минусы у Ktor?

Почти все минусы — обратная сторона минимализма: чего нет в ядре, то приходится добавлять и поддерживать самому.

1. **Меньше готовых интеграций** — для PostgreSQL, Redis, Kafka обёртки нередко пишут самостоятельно
2. **Нет встроенного DI** — приходится выбирать сторонний (Koin/Kodein)
3. **Меньше документации и ответов на StackOverflow** по сравнению со Spring
4. **Нет security-настроек по умолчанию** — CORS, CSRF, rate limiting конфигурируют руками
5. **Меньшее community** — меньше библиотек, плагинов и статей
6. **Production-обвязка вручную** — метрики, healthcheck, graceful shutdown собирают самостоятельно
7. **Нет официальной интеграции с ORM** — есть Exposed (тоже от JetBrains), Hibernate, Ktorm, но это сторонние решения
8. **Нет аннотаций** — кому-то это плюс (явность), кому-то минус (больше шаблонного кода)

**Вывод:** Ktor — для команд, готовых **писать больше кода** в обмен на больший **контроль** и более **простой, предсказуемый стек**.

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

- [Quarkus](quarkus-interview.md)
- [Vert.x](vertx-interview.md)
- [Spring AOP](../spring/spring-aop-interview.md)
- [Spring Batch](../spring/spring-batch-interview.md)
- [Spring Boot Actuator](../spring/spring-boot-actuator-interview.md)
