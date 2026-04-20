---
title: "kotlinx.serialization"
description: "kotlinx.serialization - это официальная Kotlin библиотека для сериализации и десериализации объектов. Предоставляет type-safe и эффективную сериализацию для различных форматов, включая JSON, Protobuf, CBOR и другие."
tags:
  - libraries
  - kotlin
  - kotlin-kotlinx-serialization
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# kotlinx.serialization

**kotlinx.serialization** — это официальная **Kotlin** библиотека для сериализации и десериализации объектов. Предоставляет **type-safe** и эффективную сериализацию для различных форматов, включая **JSON**, **Protobuf**, **CBOR** и другие.

## Полезные ссылки

### Официальная документация
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) — **GitHub** репозиторий
- [kotlinx.serialization Guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md) — руководство
- [kotlinx.serialization API](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-core/kotlinx.serialization.html) — **API** документация

### См. также
- [[jackson|Jackson]] — **Jackson** для **JSON**
- [[java-gson|Gson]] — **Gson** для **JSON**
- [[java-protobuf|Protobuf]] — **Protobuf**

## Содержание

- [Основные возможности](#основные-возможности)
  - [Базовая сериализация](#базовая-сериализация)
  - [Настройка JSON формата](#настройка-json-формата)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Custom Serializers](#custom-serializers)
  - [Polymorphic сериализация](#polymorphic-сериализация)
  - [Contextual сериализация](#contextual-сериализация)
  - [Value Classes (Inline classes)](#value-classes-inline-classes)
- [Работа с различными форматами](#работа-с-различными-форматами)
  - [Protobuf](#protobuf)
  - [CBOR](#cbor)
  - [Properties](#properties)
  - [HOCON](#hocon)
- [Кастомизация сериализации](#кастомизация-сериализации)
  - [Field Naming Strategy](#field-naming-strategy)
  - [Transient поля](#transient-поля)
  - [Required поля](#required-поля)
  - [Optional поля с @EncodeDefault](#optional-поля-с-encodedefault)
- [Работа с коллекциями](#работа-с-коллекциями)
  - [Сериализация коллекций](#сериализация-коллекций)
  - [Кастомные коллекции](#кастомные-коллекции)
- [Enums и Sealed Classes](#enums-и-sealed-classes)
  - [Enum сериализация](#enum-сериализация)
  - [Sealed Classes](#sealed-classes)
- [Exception Handling](#exception-handling)
  - [Safe десериализация](#safe-десериализация)
  - [Custom Exception Handling](#custom-exception-handling)
- [Performance Optimization](#performance-optimization)
  - [Reuse Json instances](#reuse-json-instances)
  - [Streaming сериализация](#streaming-сериализация)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration Properties](#configuration-properties)
  - [REST Controller с сериализацией](#rest-controller-с-сериализацией)
  - [Repository с сериализацией](#repository-с-сериализацией)
- [Testing](#testing)
  - [Unit Testing сериализации](#unit-testing-сериализации)
  - [Integration Testing](#integration-testing)
- [Best Practices](#best-practices)
  - [Структура проекта](#структура-проекта)
  - [Кастомные сериализаторы](#кастомные-сериализаторы)
  - [Конфигурация JSON](#конфигурация-json)
  - [Error Handling](#error-handling)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Debugging сериализации](#debugging-сериализации)
- [Migration Guide](#migration-guide)
  - [From Gson to kotlinx.serialization](#from-gson-to-kotlinxserialization)
  - [From Jackson to kotlinx.serialization](#from-jackson-to-kotlinxserialization)
  - [From org.json to kotlinx.serialization](#from-orgjson-to-kotlinxserialization)
- [Experimental Features](#experimental-features)
  - [Inline Classes и Value Classes](#inline-classes-и-value-classes)
  - [Context Receivers (Kotlin 1.6.20+)](#context-receivers-kotlin-1620)
  - [Unsigned Types Support](#unsigned-types-support)

## Основные возможности

### Базовая сериализация

Класс с @**Serializable** и сериализация/десериализация через **Json.encodeToString**/**decodeFromString**.

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

/**
 * Базовая сериализация в kotlinx.serialization
 * @Serializable - аннотация для классов, которые нужно сериализовать
 * Компилятор Kotlin генерирует код сериализации автоматически
 */
@Serializable
data class User(
    val id: Long,           // Примитивный тип - сериализуется как число
    val name: String,       // Строка - сериализуется как строка JSON
    val email: String,      // Строка - сериализуется как строка JSON
    val age: Int? = null    // Nullable тип - если null, поле может отсутствовать в JSON
    // Значение по умолчанию null позволяет не передавать это поле при десериализации
)

fun main() {
    // Создание объекта для сериализации
    val user = User(1, "John Doe", "john@example.com", 30)

    // Сериализация в JSON
    // Json.encodeToString() - преобразует объект в JSON строку
    // Использует сгенерированный компилятором сериализатор
    val json = Json.encodeToString(user)
    println(json)  // {"id":1,"name":"John Doe","email":"john@example.com","age":30}
    // Результат: JSON строка с полями объекта

    // Десериализация из JSON
    // Json.decodeFromString<T>() - преобразует JSON строку в объект типа T
    // Требует указания типа (User) для правильной десериализации
    val deserializedUser = Json.decodeFromString<User>(json)
    println(deserializedUser)  // User(id=1, name=John Doe, email=john@example.com, age=30)
    // Результат: объект User с теми же значениями полей
}
```

### Настройка JSON формата
```kotlin
// Красивый вывод JSON
val prettyJson = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}

val user = User(1, "John", "john@example.com")
val jsonString = prettyJson.encodeToString(user)
// {
//   "id": 1,
//   "name": "John",
//   "email": "john@example.com"
// }

// Игнорирование неизвестных ключей
val lenientJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

// Регистронезависимые ключи
val caseInsensitiveJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    // Для регистронезависимости нужно использовать custom naming strategy
}

// Кастомные настройки
val customJson = Json {
    encodeDefaults = false          // Не кодировать default значения
    explicitNulls = false           // Не кодировать null значения
    allowStructuredMapKeys = true   // Разрешить structured map keys
    useAlternativeNames = true      // Использовать альтернативные имена
}
```

## Продвинутые возможности

### Custom Serializers
```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

// Кастомный сериализатор для LocalDate
@Serializable(with = LocalDateSerializer::class)
data class Event(
    val name: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
)

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}

// Использование
val event = Event("Conference", LocalDate.of(2023, 12, 25))
val json = Json.encodeToString(event)
println(json) // {"name":"Conference","date":"2023-12-25"}

val deserializedEvent = Json.decodeFromString<Event>(json)
```

### Polymorphic сериализация
```kotlin
import kotlinx.serialization.modules.*

@Serializable
sealed class Shape

@Serializable
data class Circle(val radius: Double) : Shape()

@Serializable
data class Rectangle(val width: Double, val height: Double) : Shape()

// Настройка полиморфной сериализации
val json = Json {
    serializersModule = SerializersModule {
        polymorphic(Shape::class) {
            subclass(Circle::class)
            subclass(Rectangle::class)
        }
    }
}

val shapes: List<Shape> = listOf(
    Circle(5.0),
    Rectangle(10.0, 20.0)
)

val serialized = json.encodeToString(shapes)
println(serialized)
// [{"type":"Circle","radius":5.0},{"type":"Rectangle","width":10.0,"height":20.0}]

val deserializedShapes = json.decodeFromString<List<Shape>>(serialized)
```

### Contextual сериализация
```kotlin
@Serializable
data class User(
    val id: Long,
    val name: String,
    @Contextual val createdAt: Instant
)

val json = Json {
    serializersModule = SerializersModule {
        contextual(Instant::class, InstantSerializer)
    }
}

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.toEpochMilli())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.ofEpochMilli(decoder.decodeLong())
    }
}

val user = User(1, "John", Instant.now())
val serialized = json.encodeToString(user)
val deserializedUser = json.decodeFromString<User>(serialized)
```

### Value Classes (Inline classes)
```kotlin
@JvmInline
@Serializable
value class Email(val value: String)

@JvmInline
@Serializable
value class UserId(val value: Long)

@Serializable
data class User(
    val id: UserId,
    val email: Email,
    val name: String
)

// Сериализация value classes
val user = User(UserId(123), Email("john@example.com"), "John")
val json = Json.encodeToString(user)
println(json) // {"id":123,"email":"john@example.com","name":"John"}

val deserializedUser = Json.decodeFromString<User>(json)
```

## Работа с различными форматами

### Protobuf
```kotlin
import kotlinx.serialization.protobuf.ProtoBuf

@Serializable
data class Person(
    val name: String,
    val age: Int,
    val email: String? = null
)

fun main() {
    val person = Person("John", 30, "john@example.com")

    // Сериализация в Protobuf
    val protobufData = ProtoBuf.encodeToByteArray(person)

    // Десериализация из Protobuf
    val deserializedPerson = ProtoBuf.decodeFromByteArray<Person>(protobufData)

    println("Protobuf size: ${protobufData.size} bytes")
    println("Original: $person")
    println("Deserialized: $deserializedPerson")
}
```

### CBOR
```kotlin
import kotlinx.serialization.cbor.Cbor

@Serializable
data class Configuration(
    val serverUrl: String,
    val port: Int,
    val features: List<String>,
    val settings: Map<String, String>
)

fun main() {
    val config = Configuration(
        "https://api.example.com",
        443,
        listOf("auth", "logging", "metrics"),
        mapOf("timeout" to "30s", "retries" to "3")
    )

    // CBOR - компактный бинарный формат
    val cborData = Cbor.encodeToByteArray(config)
    val deserializedConfig = Cbor.decodeFromByteArray<Configuration>(cborData)

    println("CBOR size: ${cborData.size} bytes")
}
```

### Properties
```kotlin
import kotlinx.serialization.properties.Properties

@Serializable
data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String,
    val maxConnections: Int
)

fun main() {
    val config = DatabaseConfig(
        "jdbc:postgresql://localhost:5432/mydb",
        "user",
        "password",
        10
    )

    // Сериализация в Properties формат
    val properties = Properties.encodeToStringMap(config)
    properties.forEach { (key, value) ->
        println("$key=$value")
    }

    // Десериализация из Map
    val deserializedConfig = Properties.decodeFromStringMap<DatabaseConfig>(properties)
}
```

### HOCON
```kotlin
import kotlinx.serialization.hocon.Hocon

@Serializable
data class AppConfig(
    val app: AppSettings,
    val database: DatabaseSettings
) {
    @Serializable
    data class AppSettings(
        val name: String,
        val version: String,
        val features: List<String>
    )

    @Serializable
    data class DatabaseSettings(
        val url: String,
        val poolSize: Int,
        val credentials: Credentials
    ) {
        @Serializable
        data class Credentials(
            val username: String,
            val password: String
        )
    }
}

fun main() {
    val config = AppConfig(
        AppSettings("MyApp", "1.0.0", listOf("auth", "logging")),
        DatabaseSettings(
            "jdbc:h2:mem:test",
            10,
            DatabaseSettings.Credentials("sa", "")
        )
    )

    // HOCON формат (Human-Optimized Config Object Notation)
    val hoconString = Hocon.encodeToString(config)
    println(hoconString)

    val deserializedConfig = Hocon.decodeFromString<AppConfig>(hoconString)
}
```

## Кастомизация сериализации

### Field Naming Strategy
```kotlin
// Snake_case
val snakeCaseJson = Json {
    namingStrategy = JsonNamingStrategy.SnakeCase
}

// CamelCase (default)
val camelCaseJson = Json {
    namingStrategy = JsonNamingStrategy.CamelCase
}

// Custom naming strategy
object CustomNamingStrategy : JsonNamingStrategy {
    override fun serialNameForJson(descriptor: SerialDescriptor, elementIndex: Int, serialName: String): String {
        return serialName.uppercase()
    }
}

val customJson = Json {
    namingStrategy = CustomNamingStrategy
}
```

### Transient поля
```kotlin
@Serializable
data class User(
    val id: Long,
    val name: String,
    val email: String,

    @Transient // Не сериализуется
    val passwordHash: String = "",

    @EncodeDefault // Всегда сериализуется, даже если default
    val version: Int = 1
)

val user = User(1, "John", "john@example.com", "hashed_password")
val json = Json.encodeToString(user)
// {"id":1,"name":"John","email":"john@example.com","version":1}
// passwordHash не включен, version включен несмотря на default значение
```

### Required поля
```kotlin
@Serializable
data class User(
    val id: Long,
    @Required // Поле обязательно для десериализации
    val name: String,
    val email: String? = null
)

// Required поля должны присутствовать в JSON, даже если nullable
val jsonWithRequired = """{"id":1,"name":"John","email":"john@example.com"}"""
val jsonWithoutRequired = """{"id":1,"email":"john@example.com"}""" // Ошибка десериализации

val user1 = Json.decodeFromString<User>(jsonWithRequired) // OK
// val user2 = Json.decodeFromString<User>(jsonWithoutRequired) // Exception
```

### Optional поля с @EncodeDefault
```kotlin
@Serializable
data class ApiResponse<T>(
    val data: T,
    val message: String? = null,
    @EncodeDefault
    val success: Boolean = true,
    val errorCode: Int? = null
)

// Всегда кодирует success=true, даже если это default значение
val response = ApiResponse("data", success = true)
val json = Json.encodeToString(response)
// {"data":"data","success":true}

val defaultResponse = ApiResponse("data")
val defaultJson = Json.encodeToString(defaultResponse)
// {"data":"data","success":true} - success все равно включен
```

## Работа с коллекциями

### Сериализация коллекций
```kotlin
@Serializable
data class Book(val title: String, val author: String)

@Serializable
data class Library(
    val name: String,
    val books: List<Book>,
    val genres: Set<String>,
    val bookMap: Map<String, Book> // ISBN -> Book
)

val library = Library(
    "City Library",
    listOf(
        Book("1984", "George Orwell"),
        Book("Brave New World", "Aldous Huxley")
    ),
    setOf("Dystopian", "Science Fiction"),
    mapOf(
        "978-0451524935" to Book("1984", "George Orwell"),
        "978-0060850524" to Book("Brave New World", "Aldous Huxley")
    )
)

val json = Json.encodeToString(library)
val deserializedLibrary = Json.decodeFromString<Library>(json)
```

### Кастомные коллекции
```kotlin
// Кастомный сериализатор для LinkedHashSet
object LinkedHashSetSerializer : KSerializer<LinkedHashSet<String>> {
    private val delegateSerializer = SetSerializer(String.serializer())

    override val descriptor: SerialDescriptor = delegateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: LinkedHashSet<String>) {
        encoder.encodeSerializableValue(delegateSerializer, value)
    }

    override fun deserialize(decoder: Decoder): LinkedHashSet<String> {
        return LinkedHashSet(decoder.decodeSerializableValue(delegateSerializer))
    }
}

@Serializable
data class CustomCollection(
    @Serializable(with = LinkedHashSetSerializer::class)
    val items: LinkedHashSet<String>
)
```

## Enums и Sealed Classes

### Enum сериализация
```kotlin
@Serializable
enum class Status {
    ACTIVE, INACTIVE, SUSPENDED
}

@Serializable
data class User(
    val id: Long,
    val name: String,
    val status: Status
)

val user = User(1, "John", Status.ACTIVE)
val json = Json.encodeToString(user)
// {"id":1,"name":"John","status":"ACTIVE"}

val deserializedUser = Json.decodeFromString<User>(json)
```

### Sealed Classes
```kotlin
@Serializable
sealed class PaymentMethod

@Serializable
data class CreditCard(
    val number: String,
    val expiryDate: String,
    val cvv: String
) : PaymentMethod()

@Serializable
data class PayPal(val email: String) : PaymentMethod()

@Serializable
data class BankTransfer(
    val accountNumber: String,
    val routingNumber: String
) : PaymentMethod()

@Serializable
data class Order(
    val id: String,
    val amount: Double,
    val paymentMethod: PaymentMethod
)

val order = Order("123", 99.99, CreditCard("4111111111111111", "12/25", "123"))
val json = Json.encodeToString(order)
// {"id":"123","amount":99.99,"paymentMethod":{"type":"CreditCard","number":"4111111111111111","expiryDate":"12/25","cvv":"123"}}

val deserializedOrder = Json.decodeFromString<Order>(json)
```

## Exception Handling

### Safe десериализация
```kotlin
import kotlinx.serialization.SerializationException

fun safeDeserializeUser(json: String): User? {
    return try {
        Json.decodeFromString<User>(json)
    } catch (e: SerializationException) {
        println("Failed to deserialize user: ${e.message}")
        null
    } catch (e: IllegalArgumentException) {
        println("Invalid user data: ${e.message}")
        null
    }
}

// Использование
val validJson = """{"id":1,"name":"John","email":"john@example.com"}"""
val invalidJson = """{"id":"invalid","name":"John"}"""

val validUser = safeDeserializeUser(validJson) // User object
val invalidUser = safeDeserializeUser(invalidJson) // null
```

### Custom Exception Handling
```kotlin
object SafeJson {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
    }

    inline fun <reified T> decodeOrNull(jsonString: String): T? {
        return try {
            json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    inline fun <reified T> decodeOrDefault(jsonString: String, default: T): T {
        return decodeOrNull<T>(jsonString) ?: default
    }

    inline fun <reified T> decodeListOrEmpty(jsonString: String): List<T> {
        return try {
            json.decodeFromString<List<T>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

// Использование
val users = SafeJson.decodeListOrEmpty<User>("""[{"id":1,"name":"John"}]""")
val defaultUser = SafeJson.decodeOrDefault("""{"invalid":"data"}""",
    User(0, "Default", "default@example.com"))
```

## Performance Optimization

### Reuse Json instances
```kotlin
// Создавайте Json экземпляры один раз
object JsonConfig {
    val default = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val pretty = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    val strict = Json {
        ignoreUnknownKeys = false
        isLenient = false
    }
}

// Использование
val user = User(1, "John", "john@example.com")
val json = JsonConfig.default.encodeToString(user)
val prettyJson = JsonConfig.pretty.encodeToString(user)
```

### Streaming сериализация
```kotlin
// Для больших объемов данных используйте streaming
fun serializeLargeList(users: List<User>): String {
    val stringBuilder = StringBuilder()
    stringBuilder.append('[')

    users.forEachIndexed { index, user ->
        if (index > 0) stringBuilder.append(',')
        stringBuilder.append(Json.encodeToString(user))
    }

    stringBuilder.append(']')
    return stringBuilder.toString()
}

fun deserializeLargeList(json: String): List<User> {
    // Для больших JSON используйте streaming парсинг
    val users = mutableListOf<User>()
    val jsonReader = JsonReader(json)

    jsonReader.beginArray()
    while (jsonReader.hasNext()) {
        val userJson = jsonReader.nextString()
        val user = Json.decodeFromString<User>(userJson)
        users.add(user)
    }
    jsonReader.endArray()

    return users
}
```

## Spring Boot Integration

### Configuration Properties
```kotlin
@ConfigurationProperties("app.api")
@ConstructorBinding
@Serializable
data class ApiConfig(
    val baseUrl: String,
    val timeout: Long = 5000,
    val retries: Int = 3,
    val headers: Map<String, String> = emptyMap()
)

@Configuration
class SerializationConfig {

    @Bean
    fun json(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }

    @Bean
    fun apiConfig(@Value("\${app.api.config}") configJson: String): ApiConfig {
        return Json.decodeFromString(configJson)
    }
}
```

### REST Controller с сериализацией
```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val json: Json = Json.Default
) {

    @PostMapping
    suspend fun createUser(@RequestBody userJson: String): ResponseEntity<String> {
        return try {
            val createRequest = json.decodeFromString<CreateUserRequest>(userJson)
            val user = userService.createUser(createRequest)
            val responseJson = json.encodeToString(user)
            ResponseEntity.ok(responseJson)
        } catch (e: SerializationException) {
            ResponseEntity.badRequest().body("""{"error":"Invalid JSON format"}""")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("""{"error":"Internal server error"}""")
        }
    }

    @GetMapping("/{id}")
    suspend fun getUser(@PathVariable id: Long): ResponseEntity<String> {
        val user = userService.getUser(id)
        return if (user != null) {
            val responseJson = json.encodeToString(user)
            ResponseEntity.ok(responseJson)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
```

### Repository с сериализацией
```kotlin
@Repository
class UserRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate,
    private val json: Json
) : UserRepository {

    override fun save(user: User): User {
        val jsonData = json.encodeToString(user)
        jdbcTemplate.update(
            "INSERT INTO users (id, data) VALUES (?, ?::jsonb)",
            user.id, jsonData
        )
        return user
    }

    override fun findById(id: Long): User? {
        return jdbcTemplate.query(
            "SELECT data FROM users WHERE id = ?",
            { rs, _ ->
                val jsonData = rs.getString("data")
                json.decodeFromString<User>(jsonData)
            },
            id
        ).firstOrNull()
    }

    override fun findAll(): List<User> {
        return jdbcTemplate.query(
            "SELECT data FROM users",
            { rs, _ ->
                val jsonData = rs.getString("data")
                json.decodeFromString<User>(jsonData)
            }
        )
    }
}
```

## Testing

### Unit Testing сериализации
```kotlin
import io.kotest.matchers.shouldBe
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.junit.jupiter.api.Test

class SerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `should serialize and deserialize user correctly`() {
        val user = User(1, "John Doe", "john@example.com", 30)

        val jsonString = json.encodeToString(user)
        val deserializedUser = json.decodeFromString<User>(jsonString)

        deserializedUser shouldBe user
    }

    @Test
    fun `should handle optional fields correctly`() {
        val userWithAge = User(1, "John", "john@example.com", 30)
        val userWithoutAge = User(2, "Jane", "jane@example.com")

        val json1 = json.encodeToString(userWithAge)
        val json2 = json.encodeToString(userWithoutAge)

        json1 shouldContain """"age":30"""
        json2 shouldNotContain """"age""""

        val deserialized1 = json.decodeFromString<User>(json1)
        val deserialized2 = json.decodeFromString<User>(json2)

        deserialized1.age shouldBe 30
        deserialized2.age shouldBe null
    }

    @Test
    fun `should handle polymorphic serialization`() {
        val shapes: List<Shape> = listOf(
            Circle(5.0),
            Rectangle(10.0, 20.0)
        )

        val polymorphicJson = Json {
            serializersModule = SerializersModule {
                polymorphic(Shape::class) {
                    subclass(Circle::class)
                    subclass(Rectangle::class)
                }
            }
        }

        val serialized = polymorphicJson.encodeToString(shapes)
        val deserialized = polymorphicJson.decodeFromString<List<Shape>>(serialized)

        deserialized shouldBe shapes
    }

    @Test
    fun `should handle custom serializers`() {
        val event = Event("Conference", LocalDate.of(2023, 12, 25))

        val jsonString = json.encodeToString(event)
        jsonString shouldContain """"2023-12-25""""

        val deserializedEvent = json.decodeFromString<Event>(jsonString)
        deserializedEvent.date shouldBe LocalDate.of(2023, 12, 25)
    }
}
```

### Integration Testing
```kotlin
@SpringBootTest
@AutoConfigureTestDatabase
class SerializationIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `should serialize and deserialize through REST API`() {
        val user = User(0, "John", "john@example.com", 30)

        // Создаем пользователя через API
        val createRequest = CreateUserRequest("John", "john@example.com")
        val requestJson = json.encodeToString(createRequest)

        val result = mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        )
        .andExpect(status().isOk)
        .andReturn()

        val responseJson = result.response.contentAsString
        val createdUser = json.decodeFromString<User>(responseJson)

        createdUser.name shouldBe "John"
        createdUser.email shouldBe "john@example.com"
        createdUser.id shouldBeGreaterThan 0

        // Получаем пользователя через API
        mockMvc.perform(get("/api/users/${createdUser.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("John"))
            .andExpect(jsonPath("$.email").value("john@example.com"))
    }

    @Test
    fun `should handle malformed JSON gracefully`() {
        val malformedJson = """{"name":"John","email":invalid}"""

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson)
        )
        .andExpect(status().isBadRequest)
        .andExpect(jsonPath("$.error").value("Invalid JSON format"))
    }
}
```

## Лучшие практики

### Структура проекта
```text
src/main/kotlin/
├── model/
│   ├── User.kt
│   ├── Order.kt
│   └── Product.kt
├── serialization/
│   ├── Serializers.kt          # Кастомные сериализаторы
│   ├── JsonConfig.kt           # Конфигурация JSON
│   └── SerialModules.kt        # Polymorphic modules
├── api/
│   ├── UserController.kt
│   └── OrderController.kt
└── repository/
    ├── UserRepository.kt
    └── OrderRepository.kt
```

### Кастомные сериализаторы
```kotlin
object Serializers {

    // Сериализатор для BigDecimal
    object BigDecimalSerializer : KSerializer<BigDecimal> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: BigDecimal) {
            encoder.encodeString(value.toPlainString())
        }

        override fun deserialize(decoder: Decoder): BigDecimal {
            return BigDecimal(decoder.decodeString())
        }
    }

    // Сериализатор для URL
    object UrlSerializer : KSerializer<URL> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("URL", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: URL) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): URL {
            return URL(decoder.decodeString())
        }
    }

    // Сериализатор для UUID
    object UuidSerializer : KSerializer<UUID> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: UUID) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): UUID {
            return UUID.fromString(decoder.decodeString())
        }
    }
}
```

### Конфигурация JSON
```kotlin
object JsonConfig {

    val apiJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
        explicitNulls = false
        prettyPrint = false
        serializersModule = SerializersModule {
            contextual(BigDecimal::class, Serializers.BigDecimalSerializer)
            contextual(URL::class, Serializers.UrlSerializer)
            contextual(UUID::class, Serializers.UuidSerializer)
        }
    }

    val loggingJson = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        encodeDefaults = true
    }

    val databaseJson = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            // Database-specific serializers
        }
    }
}
```

### Error Handling
```kotlin
sealed class SerializationError {
    data class InvalidJson(val json: String, val cause: Throwable) : SerializationError()
    data class MissingRequiredField(val field: String) : SerializationError()
    data class InvalidFieldValue(val field: String, val value: Any?) : SerializationError()
}

class SafeDeserializer(private val json: Json = Json.Default) {

    inline fun <reified T> deserialize(jsonString: String): Result<T> {
        return try {
            val result = json.decodeFromString<T>(jsonString)
            Result.success(result)
        } catch (e: SerializationException) {
            Result.failure(SerializationError.InvalidJson(jsonString, e))
        } catch (e: IllegalArgumentException) {
            Result.failure(SerializationError.InvalidFieldValue("unknown", null))
        }
    }

    fun <T> deserializeList(jsonString: String, deserializer: DeserializationStrategy<List<T>>): Result<List<T>> {
        return try {
            val result = json.decodeFromString(deserializer, jsonString)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(SerializationError.InvalidJson(jsonString, e))
        }
    }
}
```

## Решение проблем

### Common Issues
```kotlin
object Troubleshooting {

    // Проблема: Unknown key during deserialization
    val lenientJson = Json {
        ignoreUnknownKeys = true  // Решение
    }

    // Проблема: Null values not serialized
    val explicitNullsJson = Json {
        explicitNulls = true  // Решение
    }

    // Проблема: Default values not serialized
    val encodeDefaultsJson = Json {
        encodeDefaults = true  // Решение
    }

    // Проблема: Polymorphic serialization fails
    val polymorphicJson = Json {
        serializersModule = SerializersModule {
            polymorphic(BaseClass::class) {
                subclass(DerivedClass1::class)
                subclass(DerivedClass2::class)
            }
        }
    }

    // Проблема: Custom serializer not used
    @Serializable
    data class Data(
        @Serializable(with = CustomSerializer::class)  // Решение
        val value: CustomType
    )

    // Проблема: Large objects serialization slow
    fun serializeLargeObject(obj: LargeObject): String {
        // Используйте streaming для больших объектов
        val writer = StringWriter()
        Json.encodeToWriter(Json.Default, obj, writer)
        return writer.toString()
    }
}
```

### Debugging сериализации
```kotlin
object SerializationDebugger {

    private val debugJson = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    fun debugSerialize(obj: Any): String {
        return try {
            debugJson.encodeToString(obj)
        } catch (e: Exception) {
            "Serialization failed: ${e.message}"
        }
    }

    fun debugDeserialize(json: String, deserializer: DeserializationStrategy<*>): Any? {
        return try {
            Json.decodeFromString(deserializer, json)
        } catch (e: Exception) {
            println("Deserialization failed: ${e.message}")
            null
        }
    }

    fun validateJsonStructure(json: String): Boolean {
        return try {
            Json.parseToJsonElement(json)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun compareSerialized(obj1: Any, obj2: Any): Boolean {
        val json1 = debugJson.encodeToString(obj1)
        val json2 = debugJson.encodeToString(obj2)
        return json1 == json2
    }
}
```

## Migration Guide

### From Gson to kotlinx.serialization
```kotlin
// Gson
data class User(val name: String, val age: Int)

val gson = Gson()
val user = User("John", 30)
val json = gson.toJson(user)  // {"name":"John","age":30}
val fromJson = gson.fromJson(json, User::class.java)

// kotlinx.serialization
@Serializable
data class User(val name: String, val age: Int)

val user = User("John", 30)
val json = Json.encodeToString(user)  // {"name":"John","age":30}
val fromJson = Json.decodeFromString<User>(json)
```

### From Jackson to kotlinx.serialization
```kotlin
// Jackson
data class User(val name: String, val age: Int)

val mapper = ObjectMapper()
val user = User("John", 30)
val json = mapper.writeValueAsString(user)
val fromJson = mapper.readValue(json, User::class.java)

// kotlinx.serialization
@Serializable
data class User(val name: String, val age: Int)

val user = User("John", 30)
val json = Json.encodeToString(user)
val fromJson = Json.decodeFromString<User>(json)
```

### From org.json to kotlinx.serialization
```kotlin
// org.json
val jsonObject = JSONObject()
jsonObject.put("name", "John")
jsonObject.put("age", 30)
val json = jsonObject.toString()

val parsed = JSONObject(json)
val name = parsed.getString("name")

// kotlinx.serialization
@Serializable
data class User(val name: String, val age: Int)

val user = User("John", 30)
val json = Json.encodeToString(user)

val fromJson = Json.decodeFromString<User>(json)
```

## Experimental Features

### Inline Classes и Value Classes
```kotlin
// Kotlin 1.5+ inline classes
@JvmInline
@Serializable
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email format" }
    }
}

@JvmInline
@Serializable
value class UserId(val value: Long) {
    companion object {
        fun random() = UserId(Random.nextLong())
    }
}

@Serializable
data class User(
    val id: UserId,
    val email: Email,
    val name: String
)

// Использование
val user = User(UserId.random(), Email("john@example.com"), "John")
val json = Json.encodeToString(user)
// {"id":123456789,"email":"john@example.com","name":"John"}
```

### Context Receivers (Kotlin `1.6`.20+)
```kotlin
// Экспериментальная возможность
context(Json)
@Serializable
data class ContextualData(val value: String) {
    // Доступ к контексту сериализации
    val serializedWith: String get() = this@Json.toString()
}
```

### Unsigned Types Support
```kotlin
// Поддержка unsigned типов (экспериментально)
@Serializable
data class UnsignedData(
    val uByte: UByte,
    val uShort: UShort,
    val uInt: UInt,
    val uLong: ULong
)

val data = UnsignedData(255u, 65535u, 4294967295u, 18446744073709551615uL)
val json = Json.encodeToString(data)
// {"uByte":255,"uShort":65535,"uInt":4294967295,"uLong":18446744073709551615}
```


## Полезные ссылки
- [Официальная документация `kotlinx.serialization`](https://github.com/Kotlin/kotlinx.serialization)
- [Kotlin Serialization Guide](https://kotlinlang.org/docs/serialization.html)
- [Serialization Samples](https://github.com/Kotlin/kotlinx.serialization/tree/master/sample)
- [Kotlin Blog — Serialization](https://blog.jetbrains.com/kotlin/tag/serialization/)

## См. также
- [[kotlin-basics|Kotlin Basics]] — Основы **Kotlin**
- [[jackson|Jackson]] — Альтернативная сериализация
- [[java-gson|Gson]] — Другая **JSON** библиотека

