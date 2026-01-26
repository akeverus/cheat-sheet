# Сериализация в Kotlin

Краткое руководство по сериализации в Kotlin - kotlinx.serialization, JSON, XML, Protocol Buffers и другие форматы.

**Последнее обновление**: 2024-01-XX

## Содержание

- [Введение](#введение)
- [Kotlinx.serialization](#kotlinx-serialization)
- [JSON сериализация](#json-сериализация)
- [Кастомные сериализаторы](#кастомные-сериализаторы)
- [Polymorphic сериализация](#polymorphic-сериализация)
- [Практические примеры](#практические-примеры)
- [Заключение](#заключение)

## Введение

Сериализация - это процесс преобразования объектов в формат, пригодный для хранения или передачи, и обратное преобразование (десериализация). Kotlin предоставляет мощную библиотеку kotlinx.serialization для типобезопасной сериализации в различные форматы.

Kotlinx.serialization использует компиляторный плагин KSP/KAPT для генерации сериализаторов во время компиляции, что обеспечивает типобезопасность и высокую производительность.

## Kotlinx.serialization

### Базовое использование

```kotlin
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String
)

fun main() {
    val user = User(1, "Alice", "alice@example.com")
    
    // Сериализация в JSON
    val json = Json.encodeToString(user)
    println(json) // {"id":1,"name":"Alice","email":"alice@example.com"}
    
    // Десериализация из JSON
    val deserialized = Json.decodeFromString<User>(json)
    println(deserialized) // User(id=1, name=Alice, email=alice@example.com)
}
```

### Настройка JSON сериализатора

```kotlin
val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
    encodeDefaults = true
}

@Serializable
data class Config(
    val name: String = "default",
    val version: Int = 1
)

fun main() {
    val config = Config()
    val json = json.encodeToString(config)
    println(json)
    // {
    //     "name": "default",
    //     "version": 1
    // }
}
```

### Сериализация вложенных объектов

```kotlin
@Serializable
data class Address(
    val street: String,
    val city: String,
    val zipCode: String
)

@Serializable
data class User(
    val id: Int,
    val name: String,
    val address: Address
)

fun main() {
    val user = User(
        id = 1,
        name = "Alice",
        address = Address("Main St", "New York", "10001")
    )
    
    val json = Json.encodeToString(user)
    println(json)
    // {"id":1,"name":"Alice","address":{"street":"Main St","city":"New York","zipCode":"10001"}}
}
```

## JSON сериализация

### Именование полей

```kotlin
@Serializable
data class User(
    @SerialName("user_id")
    val id: Int,
    
    @SerialName("user_name")
    val name: String,
    
    @SerialName("email_address")
    val email: String
)

fun main() {
    val user = User(1, "Alice", "alice@example.com")
    val json = Json.encodeToString(user)
    println(json)
    // {"user_id":1,"user_name":"Alice","email_address":"alice@example.com"}
}
```

### Пропуск полей

```kotlin
@Serializable
data class User(
    val id: Int,
    val name: String,
    
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    val password: String = "",
    
    @Transient
    val internalData: String = "internal"
)

fun main() {
    val user = User(1, "Alice", "secret", "data")
    val json = Json.encodeToString(user)
    println(json)
    // {"id":1,"name":"Alice"} - password и internalData не включены
}
```

### Nullable поля

```kotlin
@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String? = null,
    val phone: String? = null
)

fun main() {
    val user = User(1, "Alice", email = "alice@example.com")
    val json = Json.encodeToString(user)
    println(json)
    // {"id":1,"name":"Alice","email":"alice@example.com","phone":null}
}
```

### Коллекции и вложенные структуры

```kotlin
@Serializable
data class Order(
    val id: Int,
    val items: List<OrderItem>,
    val tags: Set<String>,
    val metadata: Map<String, String>
)

@Serializable
data class OrderItem(
    val productId: Int,
    val quantity: Int,
    val price: Double
)

fun main() {
    val order = Order(
        id = 1,
        items = listOf(
            OrderItem(1, 2, 19.99),
            OrderItem(2, 1, 29.99)
        ),
        tags = setOf("urgent", "fragile"),
        metadata = mapOf("source" to "web", "priority" to "high")
    )
    
    val json = Json.encodeToString(order)
    println(json)
}
```

## Кастомные сериализаторы

### Сериализация для сложных типов

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("LocalDate") {
        element<String>("value")
    }
    
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }
    
    override fun deserialize(decoder: Decoder): LocalDate {
        val string = decoder.decodeString()
        return LocalDate.parse(string, formatter)
    }
}

@Serializable
data class Event(
    val name: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
)

fun main() {
    val event = Event("Conference", LocalDate.of(2024, 6, 15))
    val json = Json.encodeToString(event)
    println(json) // {"name":"Conference","date":"2024-06-15"}
}
```

### Сериализация enum'ов

```kotlin
@Serializable
enum class Status {
    @SerialName("active")
    ACTIVE,
    
    @SerialName("inactive")
    INACTIVE,
    
    @SerialName("pending")
    PENDING
}

@Serializable
data class User(
    val id: Int,
    val name: String,
    val status: Status
)

fun main() {
    val user = User(1, "Alice", Status.ACTIVE)
    val json = Json.encodeToString(user)
    println(json) // {"id":1,"name":"Alice","status":"active"}
}
```

### Сериализация sealed классов

```kotlin
@Serializable
sealed class Result<out T> {
    @Serializable
    @SerialName("success")
    data class Success<T>(val value: T) : Result<T>()
    
    @Serializable
    @SerialName("error")
    data class Error(val message: String) : Result<Nothing>()
}

@Serializable
data class Response(
    val result: Result<String>
)

fun main() {
    val success = Response(Result.Success("Data loaded"))
    val json = Json.encodeToString(success)
    println(json)
    // {"result":{"type":"success","value":"Data loaded"}}
}
```

## Polymorphic сериализация

### Базовый polymorphic сериализатор

```kotlin
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json

@Serializable
sealed class Shape {
    @Serializable
    @SerialName("circle")
    data class Circle(val radius: Double) : Shape()
    
    @Serializable
    @SerialName("rectangle")
    data class Rectangle(val width: Double, val height: Double) : Shape()
}

@Serializable
data class Drawing(
    val shapes: List<Shape>
)

val json = Json {
    serializersModule = SerializersModule {
        polymorphic(Shape::class) {
            subclass(Shape.Circle::class)
            subclass(Shape.Rectangle::class)
        }
    }
}

fun main() {
    val drawing = Drawing(listOf(
        Shape.Circle(5.0),
        Shape.Rectangle(10.0, 20.0)
    ))
    
    val jsonString = json.encodeToString(Drawing.serializer(), drawing)
    println(jsonString)
}
```

## Практические примеры

### Сериализация API ответов

```kotlin
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class ApiError(
    val code: Int,
    val message: String,
    val details: Map<String, String> = emptyMap()
)

@Serializable
data class UserResponse(
    val users: List<User>
)

fun main() {
    val users = listOf(
        User(1, "Alice", "alice@example.com"),
        User(2, "Bob", "bob@example.com")
    )
    
    val response = ApiResponse(
        success = true,
        data = UserResponse(users)
    )
    
    val json = Json { prettyPrint = true }.encodeToString(response)
    println(json)
}
```

### Сериализация конфигураций

```kotlin
@Serializable
data class AppConfig(
    val database: DatabaseConfig,
    val server: ServerConfig,
    val features: FeaturesConfig
)

@Serializable
data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String,
    val poolSize: Int = 10
)

@Serializable
data class ServerConfig(
    val host: String = "localhost",
    val port: Int = 8080,
    val timeout: Long = 30000
)

@Serializable
data class FeaturesConfig(
    val enableCache: Boolean = true,
    val enableMetrics: Boolean = true,
    val enableTracing: Boolean = false
)

fun loadConfig(filePath: String): AppConfig {
    val json = File(filePath).readText()
    return Json.decodeFromString(json)
}

fun saveConfig(config: AppConfig, filePath: String) {
    val json = Json { prettyPrint = true }.encodeToString(config)
    File(filePath).writeText(json)
}
```

### Сериализация событий

```kotlin
@Serializable
sealed class Event {
    @Serializable
    @SerialName("user_created")
    data class UserCreated(
        val userId: String,
        val email: String,
        val timestamp: Long
    ) : Event()
    
    @Serializable
    @SerialName("user_updated")
    data class UserUpdated(
        val userId: String,
        val changes: Map<String, String>,
        val timestamp: Long
    ) : Event()
    
    @Serializable
    @SerialName("user_deleted")
    data class UserDeleted(
        val userId: String,
        val timestamp: Long
    ) : Event()
}

class EventSerializer {
    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Event::class) {
                subclass(Event.UserCreated::class)
                subclass(Event.UserUpdated::class)
                subclass(Event.UserDeleted::class)
            }
        }
    }
    
    fun serialize(event: Event): String {
        return json.encodeToString(Event.serializer(), event)
    }
    
    fun deserialize(jsonString: String): Event {
        return json.decodeFromString(Event.serializer(), jsonString)
    }
}
```

### Практические примеры: Сериализация для баз данных

```kotlin
@Serializable
data class UserEntity(
    @SerialName("id")
    val id: Long,
    
    @SerialName("username")
    val username: String,
    
    @SerialName("email")
    val email: String,
    
    @SerialName("created_at")
    @Serializable(with = InstantAsStringSerializer::class)
    val createdAt: Instant,
    
    @SerialName("metadata")
    @Serializable(with = JsonObjectSerializer::class)
    val metadata: JsonObject
)

object InstantAsStringSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
    
    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }
    
    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}
```

### Практические примеры: Сериализация для кэширования

```kotlin
class CacheService {
    private val json = Json { 
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    
    fun <T> serialize(value: T, serializer: KSerializer<T>): String {
        return json.encodeToString(serializer, value)
    }
    
    fun <T> deserialize(jsonString: String, serializer: KSerializer<T>): T {
        return json.decodeFromString(serializer, jsonString)
    }
    
    fun <T> cache(key: String, value: T, serializer: KSerializer<T>, ttl: Long = 3600) {
        val jsonString = serialize(value, serializer)
        // Сохранение в кэш с TTL
        saveToCache(key, jsonString, ttl)
    }
    
    fun <T> getFromCache(key: String, serializer: KSerializer<T>): T? {
        val jsonString = getFromCache(key) ?: return null
        return try {
            deserialize(jsonString, serializer)
        } catch (e: Exception) {
            null
        }
    }
}
```

### Практические примеры: Сериализация для очередей сообщений

```kotlin
@Serializable
sealed class Message {
    @Serializable
    @SerialName("user_created")
    data class UserCreated(val userId: String, val email: String) : Message()
    
    @Serializable
    @SerialName("order_placed")
    data class OrderPlaced(val orderId: String, val userId: String, val amount: Double) : Message()
    
    @Serializable
    @SerialName("payment_processed")
    data class PaymentProcessed(val transactionId: String, val orderId: String) : Message()
}

class MessageQueue {
    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Message::class) {
                subclass(Message.UserCreated::class)
                subclass(Message.OrderPlaced::class)
                subclass(Message.PaymentProcessed::class)
            }
        }
    }
    
    fun publish(message: Message) {
        val jsonString = json.encodeToString(Message.serializer(), message)
        // Отправка в очередь
        sendToQueue(jsonString)
    }
    
    fun consume(): Message? {
        val jsonString = receiveFromQueue() ?: return null
        return try {
            json.decodeFromString(Message.serializer(), jsonString)
        } catch (e: Exception) {
            null
        }
    }
}
```

## Заключение

Kotlinx.serialization предоставляет мощные инструменты для типобезопасной сериализации в различные форматы. Использование компиляторного плагина обеспечивает высокую производительность и безопасность типов во время компиляции.

Правильное использование сериализации для API ответов, конфигураций, событий, баз данных, кэширования, очередей сообщений и других сценариев позволяет создавать надежные, масштабируемые приложения, которые эффективно работают с различными форматами данных и обеспечивают совместимость между различными системами.

## Дополнительные ресурсы

- [Kotlinx.serialization Documentation](https://github.com/Kotlin/kotlinx.serialization)
- [Kotlinx.serialization Guide](https://kotlinlang.org/docs/serialization.html)

