---
title: "Вопросы на собеседовании: Kotlin Sealed Classes"
description: "Kotlin Sealed Classes и Sealed Interfaces: ограниченные иерархии типов, exhaustive when, Algebraic Data Types, сравнение с enum и Java sealed classes"
tags:
  - interview
  - kotlin
  - kotlin-sealed-classes-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Kotlin Sealed Classes"
  - "Kotlin Sealed собеседование"
  - "Sealed Classes вопросы"
prerequisites:
  - "[[kotlin-sealed-classes]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Kotlin Sealed Classes`

`Sealed classes` в Kotlin — ограниченные иерархии типов, известные компилятору полностью на этапе сборки. Позволяют реализовать Algebraic Data Types (ADT) с exhaustive pattern matching через `when`. Важная тема для функционального Kotlin.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Kotlin Sealed Classes](https://kotlinlang.org/docs/sealed-classes.html) — официальная документация
- [Baeldung: Kotlin Sealed Classes](https://www.baeldung.com/kotlin/sealed-classes) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое sealed class и какую проблему он решает?

**Sealed class** — абстрактный класс с закрытым набором подклассов, известных во время компиляции.

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

**Проблема без sealed**: когда нужна иерархия с ограниченным набором типов (Success/Error/Loading), обычный abstract class позволяет кому угодно добавить подкласс → потеря контроля.

**Решение**: sealed гарантирует, что все прямые подклассы находятся в том же модуле (до Kotlin 1.5 — в том же файле).

## Q2. Чем sealed class отличается от enum?

| Критерий | enum class | sealed class |
|----------|------------|--------------|
| Количество экземпляров | Фиксированное (один на constant) | Неограниченное (можно создавать много Success) |
| Данные | Одинаковые поля для всех констант | Разные поля для каждого подкласса |
| Иерархия | Плоская | Произвольная (sealed may contain sealed) |
| Наследование | Нельзя наследовать другим классам | Можно |
| Применение | Простые enum (статусы, типы) | Алгебраические типы данных (ADT) |

```kotlin
// enum — одинаковая структура
enum class OrderStatus { PENDING, CONFIRMED, CANCELLED }

// sealed — разная структура
sealed class OrderState {
    object Pending : OrderState()                          // без данных
    data class Confirmed(val confirmedAt: Instant) : OrderState()
    data class Cancelled(val reason: String, val refund: BigDecimal) : OrderState()
}
```

## Q3. Что такое exhaustive when и как его добиться?

**Exhaustive when** — `when`-выражение, покрывающее все возможные варианты sealed class. Компилятор гарантирует полноту — при добавлении нового подкласса сборка упадёт.

```kotlin
sealed class Shape {
    data class Circle(val radius: Double) : Shape()
    data class Square(val side: Double) : Shape()
    data class Triangle(val base: Double, val height: Double) : Shape()
}

fun area(shape: Shape): Double = when (shape) {  // when-выражение → exhaustive
    is Shape.Circle -> PI * shape.radius * shape.radius
    is Shape.Square -> shape.side * shape.side
    is Shape.Triangle -> 0.5 * shape.base * shape.height
    // else НЕ нужен — компилятор знает, что всё покрыто
}

// Если добавить новый подкласс → ошибка компиляции
// data class Rectangle(...) : Shape()  // fun area: missing branch
```

**Важно**: `when` как statement (без присвоения) требует `else` по умолчанию. Чтобы сделать exhaustive — используйте как expression или добавьте `.also { }` в конце.

## Q4. Чем sealed interface отличается от sealed class?

**Sealed interface** (Kotlin 1.5+) — интерфейс с закрытым набором реализаций. Отличия от sealed class:
- Можно реализовать несколько sealed interfaces одновременно (multiple inheritance).
- Нельзя иметь состояние (как обычный interface).

```kotlin
sealed interface Error
sealed interface IOError : Error
sealed interface NetworkError : Error

data class FileNotFound(val path: String) : IOError
data class TimeoutError(val seconds: Int) : NetworkError, IOError  // оба
data class ConnectionRefused(val host: String) : NetworkError
```

```kotlin
// Применение
fun handle(error: Error): String = when (error) {
    is IOError -> "IO problem"
    is NetworkError -> "Network problem"
    // компилятор знает оба случая не exhaustive для пересечения
}

fun handleIO(error: IOError): String = when (error) {
    is FileNotFound -> "Missing: ${error.path}"
    is TimeoutError -> "Timeout: ${error.seconds}s"
}
```

**Правило**: sealed interface предпочтительнее sealed class когда не нужны общие поля/методы — даёт больше гибкости.

## Q5. Как реализовать паттерн Result с sealed class?

```kotlin
sealed class Result<out T, out E> {
    data class Success<T>(val value: T) : Result<T, Nothing>()
    data class Failure<E>(val error: E) : Result<Nothing, E>()

    inline fun <R> map(transform: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    inline fun <R> flatMap(transform: (T) -> Result<R, @UnsafeVariance E>): Result<R, E> =
        when (this) {
            is Success -> transform(value)
            is Failure -> this
        }

    inline fun onSuccess(action: (T) -> Unit): Result<T, E> = apply {
        if (this is Success) action(value)
    }

    inline fun onFailure(action: (E) -> Unit): Result<T, E> = apply {
        if (this is Failure) action(error)
    }
}
```

```kotlin
// Использование
fun fetchUser(id: String): Result<User, UserError> = try {
    Result.Success(userRepo.findById(id))
} catch (e: NotFoundException) {
    Result.Failure(UserError.NotFound(id))
}

fetchUser("123")
    .map { it.name.uppercase() }
    .onSuccess { println("Got: $it") }
    .onFailure { println("Error: $it") }
```

## Q6. Что такое ADT (Algebraic Data Types) и как sealed class их реализует?

**ADT (алгебраические типы данных)** — типы, построенные как:
- **Sum type (Или)**: значение одного из нескольких типов. Sealed class — это sum type.
- **Product type (И)**: значение содержит несколько полей. Data class — это product type.

```kotlin
// Sum type: JsonValue - ЛИБО Null, Bool, Number, String, Array, Object
sealed class JsonValue {
    object Null : JsonValue()
    data class Bool(val value: Boolean) : JsonValue()
    data class Num(val value: Double) : JsonValue()
    data class Str(val value: String) : JsonValue()
    data class Arr(val items: List<JsonValue>) : JsonValue()
    data class Obj(val fields: Map<String, JsonValue>) : JsonValue()  // Product type внутри
}

// Pattern matching через exhaustive when
fun render(json: JsonValue): String = when (json) {
    is JsonValue.Null -> "null"
    is JsonValue.Bool -> json.value.toString()
    is JsonValue.Num -> json.value.toString()
    is JsonValue.Str -> "\"${json.value}\""
    is JsonValue.Arr -> json.items.joinToString(",", "[", "]", transform = ::render)
    is JsonValue.Obj -> json.fields.entries.joinToString(",", "{", "}") {
        "\"${it.key}\":${render(it.value)}"
    }
}
```

ADT — фундамент функционального программирования; Kotlin sealed classes близки к sum types Scala/Haskell.

## Q7. Когда использовать sealed class vs interface?

```kotlin
// Sealed class — когда нужны общие поля/методы
sealed class Animal(val name: String, val age: Int) {
    abstract fun makeSound(): String
    fun description() = "$name is $age years old"
}

class Dog(name: String, age: Int) : Animal(name, age) {
    override fun makeSound() = "Woof"
}

// Sealed interface — когда нет общего состояния, нужна гибкость
sealed interface Serializable {
    fun toJson(): String
}

sealed interface Comparable {
    fun compareTo(other: Any): Int
}

// Класс может реализовать несколько sealed interfaces
data class User(val name: String) : Serializable, Comparable {
    override fun toJson() = "{\"name\":\"$name\"}"
    override fun compareTo(other: Any) = ...
}
```

## Q8. Какие ограничения у sealed class в Kotlin 1.5+?

1. **Прямые наследники в том же модуле** (а не только файле как в < 1.5). Можно в разных пакетах.

2. **Sealed нельзя делать local или inner** — должны быть top-level или nested в другом классе.

3. **Нельзя наследовать sealed class от другого sealed** напрямую — но можно использовать композицию или sealed interface.

```kotlin
// В Kotlin 1.5+
// Файл a.kt
sealed class Base

// Файл b.kt в том же модуле
data class Child1(...) : Base()  // ОК

// Файл другого модуля
data class Forbidden(...) : Base()  // ОШИБКА
```

## Q9. Как использовать sealed class в Kotlin DSL?

```kotlin
sealed class Validation<out T> {
    data class Valid<T>(val value: T) : Validation<T>()
    data class Invalid(val errors: List<String>) : Validation<Nothing>()
}

// DSL для валидации
class ValidationBuilder<T>(private val target: T) {
    private val errors = mutableListOf<String>()

    fun require(condition: Boolean, message: () -> String) {
        if (!condition) errors += message()
    }

    fun <V> validate(value: V, block: V.(ValidationBuilder<V>) -> Unit) {
        val nested = ValidationBuilder(value)
        value.block(nested)
        errors.addAll(nested.errors)
    }

    fun build(): Validation<T> = if (errors.isEmpty())
        Validation.Valid(target) else Validation.Invalid(errors)
}

fun <T> validate(target: T, block: ValidationBuilder<T>.() -> Unit): Validation<T> =
    ValidationBuilder(target).apply(block).build()

// Использование
val result = validate(user) {
    require(target.age >= 18) { "Must be adult" }
    require(target.email.contains("@")) { "Invalid email" }
}
```

## Q10. Как сериализовать sealed class с kotlinx.serialization?

```kotlin
@Serializable
sealed class Event {
    @Serializable
    @SerialName("user_created")
    data class UserCreated(val userId: String, val name: String) : Event()

    @Serializable
    @SerialName("user_deleted")
    data class UserDeleted(val userId: String) : Event()

    @Serializable
    @SerialName("error")
    data class Error(val message: String) : Event()
}

// Использование
val event: Event = Event.UserCreated("u-1", "Alice")
val json = Json.encodeToString(event)
// {"type":"user_created","userId":"u-1","name":"Alice"}

val restored = Json.decodeFromString<Event>(json)
```

`@SerialName` задаёт дискриминатор типа для десериализации. Можно настроить имя поля дискриминатора через `Json { classDiscriminator = "_type" }`.

## Q11. Как моделировать состояния UI с sealed class?

```kotlin
// Классический паттерн для Android/Compose
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retryable: Boolean = true) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

class OrdersViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Order>>> = _state.asStateFlow()

    fun loadOrders() = viewModelScope.launch {
        _state.value = UiState.Loading
        _state.value = try {
            val orders = repo.getOrders()
            if (orders.isEmpty()) UiState.Empty
            else UiState.Success(orders)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Unknown", retryable = true)
        }
    }
}

// В Composable
when (val state = viewModel.state.collectAsState().value) {
    UiState.Loading -> LoadingSpinner()
    UiState.Empty -> EmptyPlaceholder()
    is UiState.Success -> OrdersList(state.data)
    is UiState.Error -> ErrorScreen(state.message, onRetry = viewModel::loadOrders)
}
```

## Q12. Как sealed class помогает избежать null и "Stringly typed" код?

```kotlin
// ПЛОХО: stringly typed
fun processPayment(paymentType: String, amount: Double) {
    when (paymentType) {
        "card" -> ...
        "paypal" -> ...
        "crypto" -> ...
        else -> throw IllegalArgumentException("Unknown type: $paymentType")
    }
}

// ЛУЧШЕ: sealed class как type-safe замена
sealed class PaymentMethod {
    data class Card(val number: String, val cvv: String) : PaymentMethod()
    data class PayPal(val email: String) : PaymentMethod()
    data class Crypto(val walletAddress: String, val currency: String) : PaymentMethod()
}

fun processPayment(method: PaymentMethod, amount: Double) {
    when (method) {
        is PaymentMethod.Card -> chargeCard(method.number, method.cvv, amount)
        is PaymentMethod.PayPal -> chargePayPal(method.email, amount)
        is PaymentMethod.Crypto -> chargeCrypto(method.walletAddress, method.currency, amount)
        // exhaustive — никакой строковой магии и ошибок runtime
    }
}
```

## Q13. Как реализовать State Machine через sealed class?

```kotlin
sealed class OrderEvent {
    object Confirm : OrderEvent()
    object Ship : OrderEvent()
    object Deliver : OrderEvent()
    data class Cancel(val reason: String) : OrderEvent()
}

sealed class OrderState {
    abstract fun next(event: OrderEvent): OrderState

    object Pending : OrderState() {
        override fun next(event: OrderEvent) = when (event) {
            OrderEvent.Confirm -> Confirmed(Instant.now())
            is OrderEvent.Cancel -> Cancelled(event.reason)
            else -> throw IllegalStateException("Invalid transition from Pending")
        }
    }

    data class Confirmed(val confirmedAt: Instant) : OrderState() {
        override fun next(event: OrderEvent) = when (event) {
            OrderEvent.Ship -> Shipped(Instant.now())
            is OrderEvent.Cancel -> Cancelled(event.reason)
            else -> throw IllegalStateException("Invalid transition from Confirmed")
        }
    }

    data class Shipped(val shippedAt: Instant) : OrderState() {
        override fun next(event: OrderEvent) = when (event) {
            OrderEvent.Deliver -> Delivered(Instant.now())
            else -> throw IllegalStateException("Invalid transition from Shipped")
        }
    }

    data class Delivered(val deliveredAt: Instant) : OrderState() {
        override fun next(event: OrderEvent): OrderState =
            throw IllegalStateException("Order already delivered")
    }

    data class Cancelled(val reason: String) : OrderState() {
        override fun next(event: OrderEvent): OrderState =
            throw IllegalStateException("Order already cancelled")
    }
}
```

## Q14. Что такое variance в sealed class (in/out)?

```kotlin
// Covariant (out) — подтипы T можно использовать везде, где ожидается T
sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    object Empty : Result<Nothing>()  // Nothing — subtype of ANY T
}

val intResult: Result<Int> = Result.Success(42)
val anyResult: Result<Any> = intResult  // ОК благодаря out

// Contravariant (in) — T используется только как параметр
sealed class Consumer<in T> {
    abstract fun consume(value: T)
}

val anyConsumer: Consumer<Any> = object : Consumer<Any>() {
    override fun consume(value: Any) { println(value) }
}
val intConsumer: Consumer<Int> = anyConsumer  // ОК благодаря in
```

**`out`** обычно используется для "производящих" типов (Result<Success>), **`in`** — для "потребляющих" (Comparator).

## Q15. Какие типичные ошибки при работе с sealed class?

1. **`when` как statement вместо expression** — не получите exhaustive проверку:

```kotlin
// ПЛОХО: when как statement — else подразумевается, exhaustive не сработает
when (result) {
    is Success -> println("ok")
    is Failure -> println("fail")
    // добавили новый подкласс — компилятор молчит
}

// ХОРОШО: expression с присваиванием
val message = when (result) {
    is Success -> "ok"
    is Failure -> "fail"
}
```

2. **Забыть `object` для singleton-вариантов** — создание лишних инстансов:

```kotlin
// ПЛОХО
sealed class Status {
    class Loading : Status()  // каждый вызов создаёт новый
}

// ХОРОШО
sealed class Status {
    object Loading : Status()  // singleton
}
```

3. **Использование sealed class там, где enum достаточно** — over-engineering для простых перечислений без данных.

4. **Открытие sealed через `open`** — в Kotlin 1.5+ sealed и open/final правила стали строже; sealed class sealed by default.

## See also

- [Kotlin](kotlin-interview.md) — основы языка, классы, объекты
- [Kotlin Serialization](kotlin-serialization-interview.md) — сериализация sealed иерархий
- [Kotlin DSL](kotlin-dsl-interview.md) — DSL с использованием sealed classes
- [Kotlin Value Classes](kotlin-value-classes-interview.md) — inline value classes
- [Kotlin Coroutines](kotlin-coroutines-interview.md) — Result как sealed result wrapper
- [Kotlin Exceptions](kotlin-exceptions-interview.md) — sealed classes как альтернатива исключениям
- [Kotlin Flow](kotlin-flow-interview.md) — Flow events как sealed типы
- [Java Pattern Matching](../java/java-pattern-matching-interview.md) — Java аналог через sealed + pattern matching
- [Java Records](../java/java-records-interview.md) — records + sealed для ADT в Java
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — sealed classes vs visitor pattern
