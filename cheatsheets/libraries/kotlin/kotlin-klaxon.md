---
title: "Klaxon"
description: "Klaxon - это легковесная JSON библиотека для Kotlin, предоставляющая простое и идиоматичное API для работы с JSON. В отличие от kotlinx.serialization, Klaxon использует reflection и предоставляет более гибкий подход к парсингу."
tags:
  - libraries
  - kotlin
  - kotlin-klaxon
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Klaxon

**Klaxon** — это легковесная **JSON** библиотека для **Kotlin**, предоставляющая простое и идиоматичное **API** для работы с **JSON**. В отличие от **kotlinx.serialization**, **Klaxon** использует **reflection** и предоставляет более гибкий подход к парсингу.

## Полезные ссылки
- [Официальная документация Klaxon](https://github.com/cbeust/klaxon)
- [Klaxon Wiki](https://github.com/cbeust/klaxon/wiki)
- [Getting Started](https://github.com/cbeust/klaxon#usage)
- [Kotlin JSON Libraries Comparison](https://kotlinlang.org/docs/serialization.html)

## Содержание

- [Основные возможности](#основные-возможности)
  - [Basic JSON Parsing](#basic-json-parsing)
  - [JSON Serialization](#json-serialization)
  - [JSON to Object Conversion](#json-to-object-conversion)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Custom Field Names](#custom-field-names)
  - [Polymorphic Types](#polymorphic-types)
  - [Path Queries](#path-queries)
  - [Streaming Parsing](#streaming-parsing)
- [Кастомные конвертеры](#кастомные-конвертеры)
  - [Date/Time Converter](#datetime-converter)
  - [Enum Converter](#enum-converter)
  - [Optional Fields](#optional-fields)
- [JSON Path Queries](#json-path-queries)
  - [Advanced Path Operations](#advanced-path-operations)
- [Интеграция с Kotlin](#интеграция-с-kotlin)
  - [Inline Classes](#inline-classes)
  - [Sealed Classes](#sealed-classes)
  - [Operator Overloading](#operator-overloading)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Configuration](#configuration)
  - [Service Layer](#service-layer)
  - [Controller](#controller)
- [Тестирование](#тестирование)
  - [Unit Testing](#unit-testing)
  - [Integration Testing](#integration-testing)
- [Лучшие практики](#лучшие-практики)
  - [Performance Optimization](#performance-optimization)
  - [Error Handling](#error-handling)
  - [Validation](#validation)
- [Устранение неполадок](#устранение-неполадок)
  - [Common Issues](#common-issues)
  - [Debugging JSON Processing](#debugging-json-processing)
- [Руководство по миграции](#руководство-по-миграции)
  - [From Gson to Klaxon](#from-gson-to-klaxon)
  - [From Jackson to Klaxon](#from-jackson-to-klaxon)
  - [From org.json to Klaxon](#from-orgjson-to-klaxon)
- [См. также](#см-также)

## Основные возможности

### Basic JSON Parsing

Парсинг **JSON**-строки в **JsonObject** через **Parser.default**().

```kotlin
import com.beust.klaxon.*
import java.io.StringReader

/
 * Базовый парсинг JSON в Klaxon
 * Klaxon использует reflection для парсинга JSON в объекты
 * Parser - основной класс для парсинга JSON
 */
// Парсинг JSON строки
// Тройные кавычки позволяют создавать многострочные строки без экранирования
val jsonString = """{"name": "John", "age": 30, "city": "New York"}"""
// JSON строка с объектом содержащим три поля

// Parser.default() - создание парсера с настройками по умолчанию
// parse(StringReader(jsonString)) - парсинг JSON из StringReader
// as JsonObject - приведение результата к JsonObject (корневой объект JSON)
val jsonObject = Parser.default().parse(StringReader(jsonString)) as JsonObject
// Результат: JsonObject с полями name, age, city

// Доступ к полям JSON объекта
// jsonObject["key"] - получение значения по ключу (возвращает Any?)
// as String - приведение типа к String
val name = jsonObject["name"] as String  // "John"
val age = jsonObject["age"] as Int       // 30
val city = jsonObject["city"] as String  // "New York"
// Приведение типов необходимо так как JSON значения имеют тип Any?

println("$name is $age years old and lives in $city")
// Вывод: John is 30 years old and lives in New York
```

### JSON Serialization
```kotlin
// Сериализация объекта в JSON
data class Person(val name: String, val age: Int, val city: String)

val person = Person("Alice", 25, "London")
val jsonString = Klaxon().toJsonString(person)

println(jsonString) // {"name": "Alice", "age": 25, "city": "London"}
```

### JSON to Object Conversion
```kotlin
// Преобразование JSON в объекты
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val profile: Profile
)

data class Profile(
    val bio: String,
    val website: String?
)

val json = """
{
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "profile": {
        "bio": "Software developer",
        "website": "https://johndoe.com"
    }
}
"""

val user = Klaxon().parse<User>(json)
println(user?.name) // John Doe
println(user?.profile?.bio) // Software developer
```

## Продвинутые возможности

### Custom Field Names
```kotlin
// Кастомные имена полей
data class ApiResponse(
    @Json(name = "user_id")
    val userId: Int,

    @Json(name = "full_name")
    val fullName: String,

    @Json(name = "created_at")
    val createdAt: Instant,

    @Json(ignored = true)
    val internalField: String = "internal"
)

val json = """
{
    "user_id": 123,
    "full_name": "Jane Smith",
    "created_at": "2023-01-15T10:30:00Z",
    "internal_field": "should be ignored"
}
"""

val response = Klaxon().parse<ApiResponse>(json)
println(response?.userId) // 123
println(response?.fullName) // Jane Smith
println(response?.internalField) // internal (default value)
```

### Polymorphic Types
```kotlin
// Работа с полиморфными типами
abstract class Shape

data class Circle(val radius: Double) : Shape()

data class Rectangle(val width: Double, val height: Double) : Shape()

data class Drawing(
    val name: String,
    val shapes: List<Shape>
)

// Кастомный конвертер для полиморфизма
class ShapeConverter : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == Shape::class.java

    override fun fromJson(jv: JsonValue): Any? {
        val jsonObject = jv.obj ?: return null
        val type = jsonObject["type"] as String

        return when (type) {
            "circle" -> Klaxon().parseFromJsonObject<Circle>(jsonObject)
            "rectangle" -> Klaxon().parseFromJsonObject<Rectangle>(jsonObject)
            else -> throw IllegalArgumentException("Unknown shape type: $type")
        }
    }

    override fun toJson(value: Any): String {
        val klaxon = Klaxon()
        return when (value) {
            is Circle -> """{"type": "circle", ${klaxon.toJsonString(value).drop(1)}"""
            is Rectangle -> """{"type": "rectangle", ${klaxon.toJsonString(value).drop(1)}"""
            else -> klaxon.toJsonString(value)
        }
    }
}

// Использование
val klaxon = Klaxon().converter(ShapeConverter())

val drawing = Drawing("My Drawing", listOf(
    Circle(5.0),
    Rectangle(10.0, 20.0)
))

val json = klaxon.toJsonString(drawing)
val parsedDrawing = klaxon.parse<Drawing>(json)
```

### Path Queries
```kotlin
// Запросы к JSON с использованием путей
val json = """
{
    "users": [
        {"name": "Alice", "age": 25, "city": "London"},
        {"name": "Bob", "age": 30, "city": "Paris"},
        {"name": "Charlie", "age": 35, "city": "Berlin"}
    ],
    "metadata": {
        "total": 3,
        "page": 1
    }
}
"""

val jsonObject = Parser.default().parse(StringReader(json)) as JsonObject

// Доступ к элементам массива
val firstUser = jsonObject.array<JsonObject>("users")?.get(0)
val aliceName = firstUser?.get("name")

// Поиск по условиям
val adults = jsonObject.array<JsonObject>("users")
    ?.filter { (it["age"] as Number).toInt() >= 30 }

println("Adults: ${adults?.map { it["name"] }}") // [Bob, Charlie]

// Доступ к вложенным объектам
val total = jsonObject.obj("metadata")?.get("total")
println("Total users: $total")
```

### Streaming Parsing
```kotlin
// Потоковый парсинг больших JSON файлов
class LargeJsonProcessor {

    fun processLargeJson(file: File) {
        Parser.default().parse(file.reader()).let { jsonValue ->
            when (jsonValue) {
                is JsonObject -> processObject(jsonValue)
                is JsonArray<*> -> processArray(jsonValue)
                else -> println("Unexpected JSON structure")
            }
        }
    }

    private fun processObject(obj: JsonObject) {
        obj.entries.forEach { (key, value) ->
            when (value) {
                is JsonObject -> {
                    println("Processing object: $key")
                    processObject(value)
                }
                is JsonArray<*> -> {
                    println("Processing array: $key")
                    processArray(value)
                }
                else -> {
                    println("$key: $value")
                }
            }
        }
    }

    private fun processArray(array: JsonArray<*>) {
        array.forEach { item ->
            when (item) {
                is JsonObject -> processObject(item)
                is JsonArray<*> -> processArray(item)
                else -> println("Array item: $item")
            }
        }
    }
}

// Использование
val processor = LargeJsonProcessor()
processor.processLargeJson(File("large-data.json"))
```

## Кастомные конвертеры

### Date/Time Converter
```kotlin
// Кастомный конвертер для дат
class InstantConverter : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == Instant::class.java

    override fun fromJson(jv: JsonValue): Any? {
        return try {
            Instant.parse(jv.inside)
        } catch (e: Exception) {
            null
        }
    }

    override fun toJson(value: Any): String {
        return "\"${(value as Instant).toString()}\""
    }
}

// Кастомный конвертер для LocalDate
class LocalDateConverter : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == LocalDate::class.java

    override fun fromJson(jv: JsonValue): Any? {
        return try {
            LocalDate.parse(jv.inside)
        } catch (e: Exception) {
            null
        }
    }

    override fun toJson(value: Any): String {
        return "\"${(value as LocalDate).toString()}\""
    }
}

// Использование
data class Event(
    val title: String,
    val date: LocalDate,
    val createdAt: Instant
)

val klaxon = Klaxon()
    .converter(InstantConverter())
    .converter(LocalDateConverter())

val event = Event(
    "Conference",
    LocalDate.of(2023, 12, 25),
    Instant.now()
)

val json = klaxon.toJsonString(event)
val parsedEvent = klaxon.parse<Event>(json)
```

### Enum Converter
```kotlin
// Кастомный конвертер для enum с кастомными значениями
enum class Status(val code: String) {
    ACTIVE("A"),
    INACTIVE("I"),
    SUSPENDED("S");

    companion object {
        fun fromCode(code: String): Status? = values().find { it.code == code }
    }
}

class StatusConverter : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == Status::class.java

    override fun fromJson(jv: JsonValue): Any? {
        val code = jv.inside as? String
        return code?.let { Status.fromCode(it) }
    }

    override fun toJson(value: Any): String {
        val status = value as Status
        return "\"${status.code}\""
    }
}

// Использование
data class User(
    val id: Int,
    val name: String,
    val status: Status
)

val klaxon = Klaxon().converter(StatusConverter())

val user = User(1, "John", Status.ACTIVE)
val json = klaxon.toJsonString(user) // {"id":1,"name":"John","status":"A"}

val parsedUser = klaxon.parse<User>(json)
println(parsedUser?.status) // ACTIVE
```

### Optional Fields
```kotlin
// Обработка опциональных полей
data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double?,
    val tags: List<String> = emptyList()
)

class ProductConverter : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == Product::class.java

    override fun fromJson(jv: JsonValue): Any? {
        val obj = jv.obj ?: return null

        return Product(
            id = obj.int("id") ?: 0,
            name = obj.string("name") ?: "",
            description = obj.string("description"),
            price = obj.double("price"),
            tags = obj.array<String>("tags") ?: emptyList()
        )
    }

    override fun toJson(value: Any): String {
        val product = value as Product
        val klaxon = Klaxon()

        val jsonMap = mutableMapOf<String, Any>(
            "id" to product.id,
            "name" to product.name
        )

        product.description?.let { jsonMap["description"] = it }
        product.price?.let { jsonMap["price"] = it }
        if (product.tags.isNotEmpty()) {
            jsonMap["tags"] = product.tags
        }

        return klaxon.toJsonString(jsonMap)
    }
}
```

## JSON Path Queries

### Advanced Path Operations
```kotlin
// Продвинутые операции с JSON путями
class JsonPathProcessor {

    fun findByPath(json: String, path: String): Any? {
        val jsonObject = Parser.default().parse(StringReader(json)) as? JsonObject
            ?: return null

        return resolvePath(jsonObject, path.split("."))
    }

    private fun resolvePath(obj: JsonObject, path: List<String>): Any? {
        var current: Any? = obj

        for (segment in path) {
            current = when (current) {
                is JsonObject -> current[segment]
                is JsonArray<*> -> {
                    val index = segment.toIntOrNull()
                    index?.let { current.getOrNull(it) }
                }
                else -> return null
            } ?: return null
        }

        return current
    }

    fun updateByPath(json: String, path: String, newValue: Any): String {
        val jsonObject = Parser.default().parse(StringReader(json)) as JsonObject
        updatePath(jsonObject, path.split("."), newValue)
        return Klaxon().toJsonString(jsonObject)
    }

    private fun updatePath(obj: JsonObject, path: List<String>, value: Any) {
        val lastSegment = path.last()
        val parentPath = path.dropLast(1)

        var current: Any = obj
        for (segment in parentPath) {
            current = when (current) {
                is JsonObject -> current.getOrPut(segment) { JsonObject() }
                else -> return
            }
        }

        (current as JsonObject)[lastSegment] = value
    }
}

// Использование
val processor = JsonPathProcessor()

val json = """
{
    "user": {
        "profile": {
            "name": "John",
            "settings": {
                "theme": "dark"
            }
        }
    }
}
"""

val name = processor.findByPath(json, "user.profile.name")
println("Name: $name") // John

val updatedJson = processor.updateByPath(json, "user.profile.settings.theme", "light")
println("Updated JSON: $updatedJson")
```

## Интеграция с Kotlin

### Inline Classes
```kotlin
// Работа с inline classes
@JvmInline
value class UserId(val value: Int)

@JvmInline
value class Email(val value: String)

data class User(
    val id: UserId,
    val email: Email,
    val name: String
)

// Кастомный конвертер для inline classes
class UserConverter : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == User::class.java

    override fun fromJson(jv: JsonValue): Any? {
        val obj = jv.obj ?: return null

        return User(
            id = UserId(obj.int("id") ?: 0),
            email = Email(obj.string("email") ?: ""),
            name = obj.string("name") ?: ""
        )
    }

    override fun toJson(value: Any): String {
        val user = value as User
        return Klaxon().toJsonString(mapOf(
            "id" to user.id.value,
            "email" to user.email.value,
            "name" to user.name
        ))
    }
}
```

### Sealed Classes
```kotlin
// Работа с sealed classes
sealed class Result<out T>

data class Success<T>(val data: T) : Result<T>()
data class Error(val message: String) : Result<Nothing>()

class ResultConverter<T> : Converter {
    override fun canConvert(cls: Class<*>): Boolean = cls == Result::class.java

    override fun fromJson(jv: JsonValue): Any? {
        val obj = jv.obj ?: return null
        val type = obj.string("type")

        return when (type) {
            "success" -> {
                val data = obj["data"]
                Success(data)
            }
            "error" -> {
                val message = obj.string("message") ?: "Unknown error"
                Error(message)
            }
            else -> null
        }
    }

    override fun toJson(value: Any): String {
        val klaxon = Klaxon()
        return when (value) {
            is Success<*> -> """{"type": "success", "data": ${klaxon.toJsonString(value.data)}}"""
            is Error -> """{"type": "error", "message": "${value.message}"}"""
            else -> klaxon.toJsonString(value)
        }
    }
}
```

### Operator Overloading
```kotlin
// Кастомные операторы для удобства
operator fun JsonObject.get(path: String): Any? {
    return resolvePath(this, path.split("."))
}

operator fun JsonObject.set(path: String, value: Any) {
    updatePath(this, path.split("."), value)
}

private fun resolvePath(obj: JsonObject, path: List<String>): Any? {
    var current: Any? = obj
    for (segment in path) {
        current = when (current) {
            is JsonObject -> current[segment]
            is JsonArray<*> -> segment.toIntOrNull()?.let { current.getOrNull(it) }
            else -> return null
        } ?: return null
    }
    return current
}

private fun updatePath(obj: JsonObject, path: List<String>, value: Any) {
    val lastSegment = path.last()
    val parentPath = path.dropLast(1)

    var current: Any = obj
    for (segment in parentPath) {
        current = when (current) {
            is JsonObject -> current.getOrPut(segment) { JsonObject() }
            else -> return
        }
    }

    (current as JsonObject)[lastSegment] = value
}

// Использование операторов
val json = Klaxon().parse<JsonObject>(jsonString)
val name = json["user.name"]
json["user.settings.theme"] = "dark"
```

## Интеграция с Spring Boot

### Configuration
```kotlin
@Configuration
class KlaxonConfig {

    @Bean
    fun klaxon(): Klaxon {
        return Klaxon()
            .converter(InstantConverter())
            .converter(LocalDateConverter())
            .converter(StatusConverter())
    }
}
```

### Service Layer
```kotlin
@Service
class ApiService(
    private val klaxon: Klaxon,
    private val httpClient: HttpClient
) {

    suspend fun fetchUsers(): List<User> {
        val response = httpClient.get("https://api.example.com/users")
        val jsonString = response.bodyAsText()

        return klaxon.parseArray<User>(jsonString) ?: emptyList()
    }

    suspend fun createUser(user: User): User? {
        val jsonString = klaxon.toJsonString(user)
        val response = httpClient.post("https://api.example.com/users") {
            contentType(ContentType.Application.Json)
            setBody(jsonString)
        }

        return if (response.status == HttpStatusCode.Created) {
            klaxon.parse<User>(response.bodyAsText())
        } else {
            null
        }
    }

    fun processJsonData(jsonData: String): ProcessingResult {
        return try {
            val jsonObject = Parser.default().parse(StringReader(jsonData)) as JsonObject

            val users = jsonObject.array<JsonObject>("users")
                ?.mapNotNull { klaxon.parseFromJsonObject<User>(it) }
                ?: emptyList()

            val metadata = jsonObject.obj("metadata")
            val totalCount = metadata?.int("total") ?: 0

            ProcessingResult.Success(users, totalCount)
        } catch (e: Exception) {
            ProcessingResult.Error("Failed to process JSON: ${e.message}")
        }
    }
}

sealed class ProcessingResult {
    data class Success(val users: List<User>, val totalCount: Int) : ProcessingResult()
    data class Error(val message: String) : ProcessingResult()
}
```

### Controller
```kotlin
@RestController
@RequestMapping("/api/klaxon")
class KlaxonController(
    private val apiService: ApiService,
    private val klaxon: Klaxon
) {

    @GetMapping("/users")
    suspend fun getUsers(): ResponseEntity<String> {
        return try {
            val users = apiService.fetchUsers()
            val jsonResponse = mapOf(
                "success" to true,
                "data" to users,
                "count" to users.size
            )
            ResponseEntity.ok(klaxon.toJsonString(jsonResponse))
        } catch (e: Exception) {
            val errorResponse = mapOf(
                "success" to false,
                "error" to e.message
            )
            ResponseEntity.status(HttpStatusCode.InternalServerError)
                .body(klaxon.toJsonString(errorResponse))
        }
    }

    @PostMapping("/users")
    suspend fun createUser(@RequestBody userJson: String): ResponseEntity<String> {
        return try {
            val user = klaxon.parse<User>(userJson)
            if (user == null) {
                return ResponseEntity.badRequest()
                    .body("""{"error": "Invalid JSON format"}""")
            }

            val createdUser = apiService.createUser(user)
            if (createdUser != null) {
                ResponseEntity.status(HttpStatusCode.Created)
                    .body(klaxon.toJsonString(createdUser))
            } else {
                ResponseEntity.status(HttpStatusCode.InternalServerError)
                    .body("""{"error": "Failed to create user"}""")
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest()
                .body("""{"error": "Invalid request: ${e.message}"}""")
        }
    }

    @PostMapping("/process")
    suspend fun processJson(@RequestBody jsonData: String): ResponseEntity<String> {
        val result = apiService.processJsonData(jsonData)

        return when (result) {
            is ProcessingResult.Success -> {
                val response = mapOf(
                    "success" to true,
                    "users" to result.users,
                    "totalCount" to result.totalCount
                )
                ResponseEntity.ok(klaxon.toJsonString(response))
            }
            is ProcessingResult.Error -> {
                val response = mapOf(
                    "success" to false,
                    "error" to result.message
                )
                ResponseEntity.badRequest().body(klaxon.toJsonString(response))
            }
        }
    }
}
```

## Тестирование

### Unit Testing
```kotlin
class KlaxonTest {

    private lateinit var klaxon: Klaxon

    @BeforeEach
    fun setUp() {
        klaxon = Klaxon()
            .converter(InstantConverter())
            .converter(StatusConverter())
    }

    @Test
    fun `should serialize user to JSON correctly`() {
        val user = User(1, "John", "john@example.com", Status.ACTIVE)

        val json = klaxon.toJsonString(user)

        assertTrue(json.contains("\"id\":1"))
        assertTrue(json.contains("\"name\":\"John\""))
        assertTrue(json.contains("\"email\":\"john@example.com\""))
        assertTrue(json.contains("\"status\":\"A\""))
    }

    @Test
    fun `should deserialize JSON to user correctly`() {
        val json = """{"id":1,"name":"John","email":"john@example.com","status":"A"}"""

        val user = klaxon.parse<User>(json)

        assertNotNull(user)
        assertEquals(1, user?.id)
        assertEquals("John", user?.name)
        assertEquals("john@example.com", user?.email)
        assertEquals(Status.ACTIVE, user?.status)
    }

    @Test
    fun `should handle polymorphic types correctly`() {
        val shapes: List<Shape> = listOf(
            Circle(5.0),
            Rectangle(10.0, 20.0)
        )

        val klaxonWithConverter = klaxon.converter(ShapeConverter())
        val json = klaxonWithConverter.toJsonString(shapes)
        val parsedShapes = klaxonWithConverter.parseArray<Shape>(json)

        assertNotNull(parsedShapes)
        assertEquals(2, parsedShapes?.size)
        assertTrue(parsedShapes?.get(0) is Circle)
        assertTrue(parsedShapes?.get(1) is Rectangle)
    }

    @Test
    fun `should handle path queries correctly`() {
        val json = """
        {
            "users": [
                {"name": "Alice", "age": 25},
                {"name": "Bob", "age": 30}
            ],
            "metadata": {"total": 2}
        }
        """

        val jsonObject = Parser.default().parse(StringReader(json)) as JsonObject

        val firstUserName = jsonObject.array<JsonObject>("users")?.get(0)?.get("name")
        val totalUsers = jsonObject.obj("metadata")?.get("total")

        assertEquals("Alice", firstUserName)
        assertEquals(2, totalUsers)
    }

    @Test
    fun `should handle custom converters correctly`() {
        val event = Event(
            "Conference",
            LocalDate.of(2023, 12, 25),
            Instant.now()
        )

        val json = klaxon.toJsonString(event)
        val parsedEvent = klaxon.parse<Event>(json)

        assertNotNull(parsedEvent)
        assertEquals(event.title, parsedEvent?.title)
        assertEquals(event.date, parsedEvent?.date)
        assertNotNull(parsedEvent?.createdAt)
    }

    @Test
    fun `should handle optional fields correctly`() {
        val productWithDescription = Product(1, "Laptop", "Gaming laptop", 999.99)
        val productWithoutDescription = Product(2, "Mouse", null, 29.99)

        val json1 = klaxon.toJsonString(productWithDescription)
        val json2 = klaxon.toJsonString(productWithoutDescription)

        assertTrue(json1.contains("\"description\":\"Gaming laptop\""))
        assertFalse(json2.contains("\"description\""))

        val parsed1 = klaxon.parse<Product>(json1)
        val parsed2 = klaxon.parse<Product>(json2)

        assertEquals("Gaming laptop", parsed1?.description)
        assertNull(parsed2?.description)
    }
}
```

### Integration Testing
```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KlaxonIntegrationTest {

    @Autowired
    private lateinit var klaxon: Klaxon

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Test
    fun `should create user via REST API with Klaxon serialization`() {
        val user = User(0, "Integration", "integration@example.com", Status.ACTIVE)
        val userJson = klaxon.toJsonString(user)

        val response = testRestTemplate.postForEntity(
            "/api/klaxon/users",
            HttpEntity(userJson, HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }),
            String::class.java
        )

        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseBody = response.body
        assertNotNull(responseBody)

        val createdUser = klaxon.parse<User>(responseBody!!)
        assertNotNull(createdUser)
        assertNotNull(createdUser?.id)
        assertEquals("Integration", createdUser?.name)
    }

    @Test
    fun `should handle JSON processing correctly`() {
        val jsonData = """
        {
            "users": [
                {"id": 1, "name": "Alice", "email": "alice@example.com"},
                {"id": 2, "name": "Bob", "email": "bob@example.com"}
            ],
            "metadata": {
                "total": 2,
                "page": 1
            }
        }
        """

        val response = testRestTemplate.postForEntity(
            "/api/klaxon/process",
            HttpEntity(jsonData, HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }),
            String::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)

        val responseJson = response.body
        val result = klaxon.parseJsonObject(StringReader(responseJson!!))

        assertTrue(result.boolean("success") ?: false)
        val users = result.array<JsonObject>("users")
        assertEquals(2, users?.size)
        assertEquals(2, result.obj("totalCount")?.int("totalCount"))
    }

    @Test
    fun `should handle invalid JSON gracefully`() {
        val invalidJson = """{"name": "Test", "invalid": json}"""

        val response = testRestTemplate.postForEntity(
            "/api/klaxon/users",
            HttpEntity(invalidJson, HttpHeaders().apply {
                contentType = MediaType.APPLICATION_JSON
            }),
            String::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

        val responseJson = response.body
        val errorResponse = klaxon.parseJsonObject(StringReader(responseJson!!))
        assertFalse(errorResponse.boolean("success") ?: true)
        assertNotNull(errorResponse.string("error"))
    }
}
```

## Лучшие практики

### Performance Optimization
```kotlin
// Переиспользование Klaxon экземпляров
object JsonUtils {
    val klaxon = Klaxon()
        .converter(InstantConverter())
        .converter(StatusConverter())

    inline fun <reified T> parse(json: String): T? = klaxon.parse<T>(json)

    inline fun <reified T> parseArray(json: String): List<T>? = klaxon.parseArray<T>(json)

    fun toJson(obj: Any): String = klaxon.toJsonString(obj)
}

// Потоковая обработка больших JSON
class StreamingJsonProcessor {

    fun processLargeJsonFile(file: File, processor: (JsonObject) -> Unit) {
        Parser.default().parse(file.reader()).let { root ->
            when (root) {
                is JsonObject -> processObject(root, processor)
                is JsonArray<*> -> root.forEach { item ->
                    if (item is JsonObject) {
                        processor(item)
                    }
                }
                else -> throw IllegalArgumentException("Unsupported JSON structure")
            }
        }
    }

    private fun processObject(obj: JsonObject, processor: (JsonObject) -> Unit) {
        processor(obj)

        // Рекурсивная обработка вложенных объектов
        obj.entries.forEach { (_, value) ->
            when (value) {
                is JsonObject -> processObject(value, processor)
                is JsonArray<*> -> value.forEach { item ->
                    if (item is JsonObject) {
                        processor(item)
                    }
                }
            }
        }
    }
}
```

### Error Handling
```kotlin
// Безопасный JSON парсинг
sealed class JsonParseResult<out T> {
    data class Success<T>(val data: T) : JsonParseResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : JsonParseResult<Nothing>()
}

class SafeKlaxonParser(private val klaxon: Klaxon = Klaxon()) {

    inline fun <reified T> parseSafe(json: String): JsonParseResult<T> {
        return try {
            val result = klaxon.parse<T>(json)
            if (result != null) {
                JsonParseResult.Success(result)
            } else {
                JsonParseResult.Error("Parsed result is null")
            }
        } catch (e: KlaxonException) {
            JsonParseResult.Error("JSON parsing error", e)
        } catch (e: Exception) {
            JsonParseResult.Error("Unexpected error during parsing", e)
        }
    }

    fun parseJsonSafe(json: String): JsonParseResult<JsonObject> {
        return try {
            val result = Parser.default().parse(StringReader(json))
            if (result is JsonObject) {
                JsonParseResult.Success(result)
            } else {
                JsonParseResult.Error("Expected JSON object, got ${result?.javaClass?.simpleName}")
            }
        } catch (e: Exception) {
            JsonParseResult.Error("JSON parsing error", e)
        }
    }
}

// Использование
val parser = SafeKlaxonParser()

when (val result = parser.parseSafe<User>(jsonString)) {
    is JsonParseResult.Success -> {
        val user = result.data
        println("Parsed user: ${user.name}")
    }
    is JsonParseResult.Error -> {
        println("Failed to parse: ${result.message}")
        result.cause?.printStackTrace()
    }
}
```

### Validation
```kotlin
// JSON Schema validation
class JsonValidator(private val klaxon: Klaxon = Klaxon()) {

    fun validateUser(json: String): ValidationResult {
        return try {
            val user = klaxon.parse<User>(json)
            if (user == null) {
                return ValidationResult.Invalid(listOf("Failed to parse user"))
            }

            val errors = mutableListOf<String>()

            if (user.name.isBlank()) {
                errors.add("Name cannot be blank")
            }

            if (user.email.isBlank()) {
                errors.add("Email cannot be blank")
            } else if (!user.email.contains("@")) {
                errors.add("Invalid email format")
            }

            if (user.age < 0 || user.age > 150) {
                errors.add("Age must be between 0 and 150")
            }

            return if (errors.isEmpty()) {
                ValidationResult.Valid(user)
            } else {
                ValidationResult.Invalid(errors)
            }
        } catch (e: Exception) {
            ValidationResult.Invalid(listOf("JSON parsing failed: ${e.message}"))
        }
    }

    fun validateJsonStructure(json: String, requiredFields: List<String>): ValidationResult {
        return try {
            val jsonObject = Parser.default().parse(StringReader(json)) as? JsonObject
                ?: return ValidationResult.Invalid(listOf("Expected JSON object"))

            val missingFields = requiredFields.filter { !jsonObject.containsKey(it) }

            return if (missingFields.isEmpty()) {
                ValidationResult.Valid(jsonObject)
            } else {
                ValidationResult.Invalid(missingFields.map { "Missing required field: $it" })
            }
        } catch (e: Exception) {
            ValidationResult.Invalid(listOf("JSON validation failed: ${e.message}"))
        }
    }
}

sealed class ValidationResult {
    data class Valid(val data: Any) : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}
```

## Устранение неполадок

### Common Issues
```kotlin
object KlaxonTroubleshooting {

    // Проблема: NullPointerException при доступе к полям
    fun safeFieldAccess(json: String) {
        val jsonObject = Parser.default().parse(StringReader(json)) as? JsonObject

        // Безопасный доступ
        val name = jsonObject?.string("name") ?: "Unknown"
        val age = jsonObject?.int("age") ?: 0
        val tags = jsonObject?.array<String>("tags") ?: emptyList()

        println("Name: $name, Age: $age, Tags: $tags")
    }

    // Проблема: ClassCastException при парсинге
    inline fun <reified T> safeParse(klaxon: Klaxon, json: String): T? {
        return try {
            klaxon.parse<T>(json)
        } catch (e: ClassCastException) {
            println("Type mismatch during parsing: ${e.message}")
            null
        } catch (e: KlaxonException) {
            println("JSON parsing error: ${e.message}")
            null
        }
    }

    // Проблема: MissingFieldException для nullable полей
    @Test
    fun `should handle missing nullable fields`() {
        data class OptionalUser(
            val id: Int,
            val name: String,
            val email: String? = null,
            val age: Int? = null
        )

        val json = """{"id":1,"name":"John"}""" // email и age отсутствуют

        val user = Klaxon().parse<OptionalUser>(json)

        assertNotNull(user)
        assertEquals(1, user?.id)
        assertEquals("John", user?.name)
        assertNull(user?.email)  // null по умолчанию
        assertNull(user?.age)    // null по умолчанию
    }

    // Проблема: Infinite recursion при циклических ссылках
    @Test
    fun `should handle cyclic references carefully`() {
        // Избегать циклических ссылок в моделях данных
        // Использовать специальные идентификаторы вместо вложенных объектов

        data class UserRef(
            val id: Int,
            val name: String,
            val managerId: Int? // Ссылка по ID вместо вложенного объекта
        )

        data class Department(
            val id: Int,
            val name: String,
            val managerId: Int,
            val employeeIds: List<Int> // Список ID вместо объектов
        )

        val department = Department(1, "IT", 100, listOf(101, 102, 103))
        val json = Klaxon().toJsonString(department)

        // Парсинг безопасен от циклических ссылок
        val parsed = Klaxon().parse<Department>(json)
        assertNotNull(parsed)
    }

    // Проблема: Performance с большими JSON
    fun optimizeLargeJsonProcessing() {
        // Использовать streaming API для больших файлов
        val processor = StreamingJsonProcessor()

        processor.processLargeJsonFile(File("large-data.json")) { jsonObject ->
            // Обработка каждого объекта по отдельности
            val id = jsonObject.int("id")
            val name = jsonObject.string("name")

            // Сохранить в базу или обработать
            processItem(id, name)
        }
    }

    private fun processItem(id: Int?, name: String?) {
        // Обработка элемента
        println("Processing item $id: $name")
    }
}
```

### Debugging JSON Processing
```kotlin
class JsonDebugger(private val klaxon: Klaxon = Klaxon()) {

    fun debugJsonStructure(json: String) {
        try {
            val parsed = Parser.default().parse(StringReader(json))
            printJsonStructure(parsed, 0)
        } catch (e: Exception) {
            println("Failed to parse JSON: ${e.message}")
        }
    }

    private fun printJsonStructure(value: Any?, indent: Int) {
        val prefix = "  ".repeat(indent)

        when (value) {
            is JsonObject -> {
                println("${prefix}Object {")
                value.entries.forEach { (key, v) ->
                    print("$prefix  $key: ")
                    printJsonStructure(v, 0)
                }
                println("${prefix}}")
            }
            is JsonArray<*> -> {
                println("${prefix}Array [${value.size}]")
                value.take(3).forEach { item ->
                    printJsonStructure(item, indent + 1)
                }
                if (value.size > 3) {
                    println("${prefix}  ... and ${value.size - 3} more items")
                }
            }
            else -> {
                println("$value (${value?.javaClass?.simpleName})")
            }
        }
    }

    fun compareJson(json1: String, json2: String) {
        try {
            val obj1 = Parser.default().parse(StringReader(json1))
            val obj2 = Parser.default().parse(StringReader(json2))

            if (obj1 == obj2) {
                println("JSON objects are equal")
            } else {
                println("JSON objects differ:")
                debugJsonStructure(json1)
                println("--- vs ---")
                debugJsonStructure(json2)
            }
        } catch (e: Exception) {
            println("Failed to compare JSON: ${e.message}")
        }
    }

    fun validateJsonSchema(json: String, schema: Map<String, Any>): List<String> {
        val errors = mutableListOf<String>()

        try {
            val jsonObject = Parser.default().parse(StringReader(json)) as? JsonObject
                ?: run {
                    errors.add("Expected JSON object")
                    return errors
                }

            validateObject(jsonObject, schema, "", errors)
        } catch (e: Exception) {
            errors.add("JSON parsing error: ${e.message}")
        }

        return errors
    }

    private fun validateObject(
        obj: JsonObject,
        schema: Map<String, Any>,
        path: String,
        errors: MutableList<String>
    ) {
        schema.forEach { (key, expectedType) ->
            val currentPath = if (path.isEmpty()) key else "$path.$key"
            val value = obj[key]

            when (expectedType) {
                "string" -> if (value !is String) errors.add("$currentPath should be string")
                "number" -> if (value !is Number) errors.add("$currentPath should be number")
                "boolean" -> if (value !is Boolean) errors.add("$currentPath should be boolean")
                is Map<*, *> -> {
                    if (value is JsonObject) {
                        @Suppress("UNCHECKED_CAST")
                        validateObject(value, expectedType as Map<String, Any>, currentPath, errors)
                    } else {
                        errors.add("$currentPath should be object")
                    }
                }
            }
        }
    }
}

// Использование
val debugger = JsonDebugger()
debugger.debugJsonStructure(jsonString)
debugger.compareJson(json1, json2)

val schema = mapOf(
    "name" to "string",
    "age" to "number",
    "active" to "boolean",
    "address" to mapOf(
        "street" to "string",
        "city" to "string"
    )
)

val validationErrors = debugger.validateJsonSchema(jsonString, schema)
```

## Руководство по миграции

### From Gson to Klaxon
```kotlin
// Gson
data class User(val name: String, val age: Int)

val gson = Gson()
val user = User("John", 30)
val json = gson.toJson(user) // {"name":"John","age":30}
val fromJson = gson.fromJson(json, User::class.java)

// Klaxon
data class User(val name: String, val age: Int)

val klaxon = Klaxon()
val user = User("John", 30)
val json = klaxon.toJsonString(user) // {"name":"John","age":30}
val fromJson = klaxon.parse<User>(json)
```

### From Jackson to Klaxon
```kotlin
// Jackson
data class User(val name: String, val age: Int)

val mapper = ObjectMapper()
val user = User("John", 30)
val json = mapper.writeValueAsString(user)
val fromJson = mapper.readValue(json, User::class.java)

// Klaxon
data class User(val name: String, val age: Int)

val klaxon = Klaxon()
val user = User("John", 30)
val json = klaxon.toJsonString(user)
val fromJson = klaxon.parse<User>(json)
```

### From org.json to Klaxon
```kotlin
// org.json
val jsonObject = JSONObject()
jsonObject.put("name", "John")
jsonObject.put("age", 30)
val json = jsonObject.toString()

val parsed = JSONObject(json)
val name = parsed.getString("name")

// Klaxon
val user = User("John", 30)
val json = Klaxon().toJsonString(user)

val parsed = Parser.default().parse(StringReader(json)) as JsonObject
val name = parsed.string("name")
```
## См. также
- [[kotlin-kotlinx-serialization|kotlinx.serialization]] — Официальная **Kotlin** сериализация
- [[java-gson|Gson]] — **Google JSON** библиотека
- [[jackson|Jackson]] — **Jackson JSON** процессор

