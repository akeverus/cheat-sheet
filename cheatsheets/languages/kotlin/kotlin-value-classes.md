---
title: "Value классы (Inline классы) в Kotlin"
description: "Краткое руководство по value классам (inline классам) в Kotlin - типобезопасные обертки без накладных расходов."
tags:
  - languages
  - kotlin
  - kotlin-value-classes
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Value классы (Inline классы) в Kotlin

Краткое руководство по **value** классам (inline классам) в **Kotlin** — типобезопасные обертки без накладных расходов.

**Последнее обновление**: 2024-01-`XX`

## Полезные ссылки

[Kotlin Documentation](https://kotlinlang.org/docs/home.html)
[Kotlin GitHub](https://github.com/JetBrains/kotlin)

## Содержание

- [Введение](#введение)
- [Базовое использование](#базовое-использование)
  - [Простой пример](#простой-пример)
  - [Value классы для единиц измерения](#value-классы-для-единиц-измерения)
- [Типобезопасность](#типобезопасность)
  - [Предотвращение ошибок](#предотвращение-ошибок)
  - [Типобезопасные обертки для строк](#типобезопасные-обертки-для-строк)
- [Практические примеры](#практические-примеры)
  - [API с типобезопасными параметрами](#api-с-типобезопасными-параметрами)
  - [Value классы для ID типов](#value-классы-для-id-типов)
  - [Value классы для работы с деньгами](#value-классы-для-работы-с-деньгами)
  - [Value классы для координат](#value-классы-для-координат)
- [Ограничения](#ограничения)
  - [Ограничения value классов](#ограничения-value-классов)
  - [Работа с nullable value классами](#работа-с-nullable-value-классами)
- [Best practices](#best-practices)
  - [1. Используйте value классы для предотвращения ошибок](#1-используйте-value-классы-для-предотвращения-ошибок)
  - [2. Добавляйте валидацию в конструкторы](#2-добавляйте-валидацию-в-конструкторы)
  - [3. Используйте для единиц измерения](#3-используйте-для-единиц-измерения)
  - [Практические примеры: Value классы для работы с временем](#практические-примеры-value-классы-для-работы-с-временем)
  - [Практические примеры: Value классы для работы с файлами](#практические-примеры-value-классы-для-работы-с-файлами)
  - [Практические примеры: Value классы для работы с сетью](#практические-примеры-value-классы-для-работы-с-сетью)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Value** классы (ранее inline классы) в **Kotlin** позволяют создавать типобезопасные обертки над примитивными типами или другими типами без создания дополнительного объекта во время выполнения. Это обеспечивает типобезопасность на этапе компиляции без потери производительности.

**Value** классы полезны для предотвращения ошибок при передаче неправильных типов, улучшения читаемости кода и обеспечения семантической правильности данных.

## Базовое использование

### Простой пример

```kotlin
// Value-классы для типобезопасных ID и строк (без аллокации в runtime)
@JvmInline
value class UserId(val value: Int)

@JvmInline
value class Email(val value: String)

@JvmInline
value class Password(val value: String)

data class User(
    val id: UserId,
    val email: Email,
    val password: Password
)

fun createUser(id: Int, email: String, password: String) {
    val user = User(
        id = UserId(id),      // Типобезопасная обертка
        email = Email(email),
        password = Password(password)
    )

    // Ошибка компиляции - нельзя перепутать параметры
    // val wrong = User(Email("test"), UserId(1), Password("pass"))
}
```

### Value классы для единиц измерения

```kotlin
// Единицы измерения: нельзя случайно смешать километры и метры
@JvmInline
value class Kilometers(val value: Double)

@JvmInline
value class Meters(val value: Double)

@JvmInline
value class Seconds(val value: Double)

data class Distance(val kilometers: Kilometers)
data class Duration(val seconds: Seconds)

fun calculateSpeed(distance: Distance, duration: Duration): Double {
    // Предотвращает ошибки при передаче неправильных единиц
    return distance.kilometers.value / (duration.seconds.value / 3600.0)
}

fun main() {
    val distance = Distance(Kilometers(100.0))
    val duration = Duration(Seconds(3600.0))
    val speed = calculateSpeed(distance, duration)
    println("Speed: $speed km/h") // Speed: 100.0 km/h
}
```

## Типобезопасность

### Предотвращение ошибок

```kotlin
@JvmInline
value class AccountId(val value: Long)

@JvmInline
value class TransactionId(val value: Long)

fun transferMoney(fromAccount: AccountId, toAccount: AccountId, amount: Double) {
    // Компилятор предотвращает передачу TransactionId вместо AccountId
}

fun main() {
    val accountId = AccountId(12345L)
    val transactionId = TransactionId(67890L)

    // Ошибка компиляции
    // transferMoney(transactionId, accountId, 100.0)

    // Правильно
    transferMoney(accountId, AccountId(54321L), 100.0)
}
```

### Типобезопасные обертки для строк

```kotlin
@JvmInline
value class Username(val value: String)

@JvmInline
value class DisplayName(val value: String)

data class Profile(
    val username: Username,
    val displayName: DisplayName
)

fun createProfile(username: String, displayName: String): Profile {
    return Profile(
        username = Username(username),  // Валидация может быть добавлена
        displayName = DisplayName(displayName)
    )
}
```

## Практические примеры

### API с типобезопасными параметрами

```kotlin
@JvmInline
value class ApiKey(val value: String)

@JvmInline
value class RequestId(val value: String)

class ApiClient(private val apiKey: ApiKey) {
    fun makeRequest(requestId: RequestId, endpoint: String): String {
        // API ключ и Request ID типобезопасны
        return "Request $requestId to $endpoint with key $apiKey"
    }
}

fun main() {
    val client = ApiClient(ApiKey("secret-key-123"))
    val requestId = RequestId("req-456")
    val response = client.makeRequest(requestId, "/api/users")
    println(response)
}
```

### Value классы для `ID` типов

```kotlin
@JvmInline
value class ProductId(val value: String)

@JvmInline
value class CategoryId(val value: String)

@JvmInline
value class OrderId(val value: String)

data class Product(
    val id: ProductId,
    val name: String,
    val categoryId: CategoryId,
    val price: Double
)

data class Order(
    val id: OrderId,
    val items: List<OrderItem>
)

data class OrderItem(
    val productId: ProductId,
    val quantity: Int
)

class ProductRepository {
    fun findById(id: ProductId): Product? {
        // Поиск продукта по ID
        return null
    }

    fun findByCategory(categoryId: CategoryId): List<Product> {
        // Поиск продуктов по категории
        return emptyList()
    }
}
```

### Value классы для работы с деньгами

```kotlin
@JvmInline
value class Amount(val cents: Long)

@JvmInline
value class Currency(val code: String)

data class Money(
    val amount: Amount,
    val currency: Currency
) {
    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add different currencies" }
        return Money(Amount(amount.cents + other.amount.cents), currency)
    }

    operator fun times(multiplier: Double): Money {
        return Money(Amount((amount.cents * multiplier).toLong()), currency)
    }
}

fun main() {
    val usd100 = Money(Amount(10000), Currency("USD"))
    val usd50 = Money(Amount(5000), Currency("USD"))
    val total = usd100 + usd50
    println("Total: ${total.amount.cents / 100.0} ${total.currency.code}")
}
```

### Value классы для координат

```kotlin
@JvmInline
value class Latitude(val value: Double)

@JvmInline
value class Longitude(val value: Double)

data class Location(
    val latitude: Latitude,
    val longitude: Longitude
) {
    init {
        require(latitude.value in -90.0..90.0) { "Invalid latitude" }
        require(longitude.value in -180.0..180.0) { "Invalid longitude" }
    }

    fun distanceTo(other: Location): Double {
        // Расчет расстояния между координатами
        val lat1 = Math.toRadians(latitude.value)
        val lat2 = Math.toRadians(other.latitude.value)
        val lon1 = Math.toRadians(longitude.value)
        val lon2 = Math.toRadians(other.longitude.value)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return 6371.0 * c // Расстояние в километрах
    }
}
```

## Ограничения

### Ограничения value классов

```kotlin
@JvmInline
value class Name(val value: String) {
    // ✅ Можно определять свойства
    val length: Int get() = value.length

    // ✅ Можно определять функции
    fun uppercase(): Name = Name(value.uppercase())

    // ✅ Можно реализовывать интерфейсы
    // init блоки не поддерживаются напрямую в value классах
}

// ❌ Нельзя наследоваться от других классов
// ❌ Нельзя иметь backing fields (кроме одного параметра)
// ❌ Нельзя быть inner классом
// ❌ Нельзя иметь init блоки
```

### Работа с nullable value классами

```kotlin
@JvmInline
value class UserId(val value: Int)

fun findUser(id: UserId?): User? {
    return if (id != null) {
        // Обработка
        null
    } else {
        null
    }
}

fun main() {
    val userId: UserId? = UserId(1)
    val user = findUser(userId)
}
```

## Best practices

### 1. Используйте value классы для предотвращения ошибок

```kotlin
// ✅ Хорошо - типобезопасность
fun processOrder(orderId: OrderId, customerId: CustomerId) { }

// ❌ Плохо - легко перепутать
fun processOrder(orderId: Long, customerId: Long) { }
```

### 2. Добавляйте валидацию в конструкторы

```kotlin
@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email format" }
    }
}

@JvmInline
value class PositiveInt(val value: Int) {
    init {
        require(value > 0) { "Value must be positive" }
    }
}
```

### 3. Используйте для единиц измерения

```kotlin
@JvmInline
value class Temperature(val celsius: Double) {
    fun toFahrenheit(): Double = celsius * 9.0 / 5.0 + 32.0
    fun toKelvin(): Double = celsius + 273.15
}
```

### Практические примеры: Value классы для работы с временем

```kotlin
@JvmInline
value class Timestamp(val millis: Long) {
    fun toInstant(): Instant = Instant.ofEpochMilli(millis)
    fun toLocalDateTime(): LocalDateTime = LocalDateTime.ofInstant(toInstant(), ZoneId.systemDefault())
}

@JvmInline
value class Duration(val millis: Long) {
    operator fun plus(other: Duration): Duration = Duration(millis + other.millis)
    operator fun minus(other: Duration): Duration = Duration(millis - other.millis)
    operator fun times(multiplier: Int): Duration = Duration(millis * multiplier)

    fun toSeconds(): Long = millis / 1000
    fun toMinutes(): Long = millis / 60000
    fun toHours(): Long = millis / 3600000
}
```

### Практические примеры: Value классы для работы с файлами

```kotlin
@JvmInline
value class FilePath(val value: String) {
    val extension: String
        get() = value.substringAfterLast('.', "")

    val name: String
        get() = value.substringAfterLast('/')

    val directory: FilePath
        get() = FilePath(value.substringBeforeLast('/'))

    fun exists(): Boolean = File(value).exists()
}

@JvmInline
value class FileSize(val bytes: Long) {
    fun toKB(): Double = bytes / 1024.0
    fun toMB(): Double = bytes / (1024.0 * 1024.0)
    fun toGB(): Double = bytes / (1024.0 * 1024.0 * 1024.0)
}
```

### Практические примеры: Value классы для работы с сетью

```kotlin
@JvmInline
value class Url(val value: String) {
    init {
        require(value.startsWith("http://") || value.startsWith("https://")) {
            "Invalid URL format"
        }
    }

    val protocol: String
        get() = value.substringBefore("://")

    val host: String
        get() = value.substringAfter("://").substringBefore("/")
}

@JvmInline
value class Port(val value: Int) {
    init {
        require(value in 1..65535) { "Port must be between 1 and 65535" }
    }
}

@JvmInline
value class IpAddress(val value: String) {
    init {
        require(isValid(value)) { "Invalid IP address format" }
    }

    companion object {
        private fun isValid(ip: String): Boolean {
            val parts = ip.split(".")
            if (parts.size != 4) return false
            return parts.all { part ->
                part.toIntOrNull()?.let { it in 0..255 } ?: false
            }
        }
    }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Value** классы — это инструмент **Kotlin** для создания типобезопасных оберток без накладных расходов на производительность. Они помогают предотвращать ошибки на этапе компиляции, улучшают читаемость кода и обеспечивают семантическую правильность данных.

Использование **value** классов для `ID` типов, единиц измерения, денежных сумм, координат, времени, файлов, сетевых адресов и других семантических типов позволяет создавать более надежный, понятный и безопасный код, который легче поддерживать и развивать.

## Дополнительные ресурсы

- [Kotlin Value Classes Documentation](https://kotlinlang.org/docs/inline-classes.html)
- [Kotlin Inline Classes](https://kotlinlang.org/docs/inline-classes.html)

## См. также

- [Kotlin Another](kotlin-another.md)
- [Основы Kotlin — Полное руководство](kotlin-basics.md)
- [Kotlin Collections: Grouping and Aggregation](kotlin-collections-grouping.md)
- [Kotlin Collections: List](kotlin-collections-list.md)
- [Kotlin Collections: Map](kotlin-collections-map.md)
