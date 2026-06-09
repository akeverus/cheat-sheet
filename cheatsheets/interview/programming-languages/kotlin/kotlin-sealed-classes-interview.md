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

**Sealed class** — абстрактный класс с закрытым (известным компилятору на этапе сборки) набором прямых наследников. Иначе говоря, это способ сказать «у этого типа ровно вот столько форм, и других не будет».

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

**Какую проблему решает.** Часто нужна иерархия с фиксированным набором вариантов — например, результат операции это ровно `Success`, `Error` или `Loading`. С обычным `abstract class` этот набор открыт: любой код может добавить ещё один подкласс, и компилятор не сможет ни проверить полноту обработки, ни предупредить о пропущенном случае.

`Sealed class` закрывает множество наследников: компилятор знает их все. Из этого вытекают два главных эффекта:

- **Exhaustive `when`** — в `when` по sealed-типу можно перечислить все варианты без ветки `else`, а при добавлении нового подкласса сборка упадёт там, где его забыли обработать.
- **Контроль над иерархией** — никто извне не расширит тип неожиданным вариантом.

**Где живут наследники.** `Sealed` гарантирует, что все прямые наследники находятся в том же модуле (до Kotlin 1.5 ограничение было жёстче — в том же файле).

## Q2. Чем sealed class отличается от enum?

Ключевое отличие: `enum` — это фиксированный набор **значений** одного и того же типа с одинаковой структурой, а `sealed class` — фиксированный набор **подтипов**, каждый со своей структурой и любым числом экземпляров.

| Критерий | enum class | sealed class |
|----------|------------|--------------|
| Количество экземпляров | Фиксированное (один на constant) | Неограниченное (можно создавать много Success) |
| Данные | Одинаковые поля для всех констант | Разные поля для каждого подкласса |
| Иерархия | Плоская | Произвольная (sealed may contain sealed) |
| Наследование | Нельзя наследовать другим классам | Можно |
| Применение | Простые enum (статусы, типы) | Алгебраические типы данных (ADT) |

**Практический критерий выбора.** Если всем вариантам нужны одни и те же поля (или полей нет вовсе) — берите `enum`: он легче и нагляднее. Как только разным вариантам нужны *разные* данные (у `Confirmed` — время, у `Cancelled` — причина и сумма возврата) — `enum` уже не подходит, нужен `sealed class`.

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

**Exhaustive `when`** — это `when`, в котором перечислены все варианты sealed-типа, поэтому ветка `else` не нужна. Ценность не в краткости, а в том, что компилятор *требует* полноты: добавили новый подкласс — и все `when`, где его забыли, перестают компилироваться. Так компилятор показывает точные места, куда нужно добавить обработку, вместо тихой ошибки в рантайме.

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

**Как добиться exhaustive-проверки.** Полноту компилятор требует от `when`-*выражения* — когда его результат куда-то идёт (присваивается, возвращается, передаётся как аргумент). `when` в роли *statement* (значение отбрасывается) по умолчанию довольствуется неявным `else`. Поэтому, если нужна проверка полноты:

- используйте `when` как выражение (присвойте результат или верните его), либо
- заставьте компилятор трактовать его как выражение — частый приём — дописать `.also { }` в конце.

## Q4. Чем sealed interface отличается от sealed class?

**Sealed interface** (Kotlin 1.5+) — интерфейс с закрытым набором реализаций: даёт ту же exhaustive-проверку в `when`, что и sealed class, но снимает ограничение одиночного наследования. Отличия от sealed class:

- **Множественная реализация.** Класс может реализовать несколько sealed-интерфейсов сразу — то есть один тип способен принадлежать нескольким закрытым иерархиям одновременно. С sealed class так нельзя: наследоваться можно только от одного класса.
- **Нет состояния.** Как у любого интерфейса, у sealed interface не может быть полей-конструктора. Нужны общие данные или логика в базе — берите sealed class.

Пример ниже показывает первый пункт: `TimeoutError` входит и в `NetworkError`, и в `IOError`.

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

**Эмпирическое правило.** Если общие поля и реализация не нужны — берите sealed interface: он гибче (множественная реализация) и не навязывает базовому типу состояние. Sealed class оставляйте на случаи, когда вариантам действительно нужна общая база.

## Q5. Как реализовать паттерн Result с sealed class?

`Result` — это sum type из двух вариантов: `Success` с полезным значением и `Failure` с ошибкой. Идея паттерна — сделать ошибку *значением*, а не исключением: вызывающий обязан явно разобрать оба случая в `when`, и ни один путь нельзя забыть. Это убирает невидимые `throw` из сигнатур и делает обработку ошибок частью типа.

Ключевые методы — `map`/`flatMap` (преобразовать успех, пробросив ошибку как есть) и `onSuccess`/`onFailure` (побочные действия без распаковки). Все они помечены `inline`, чтобы лямбды не создавали лишних объектов.

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

**ADT (алгебраические типы данных)** — типы, которые конструируются из двух базовых операций. Их называют «алгебраическими», потому что число возможных значений типа считается арифметически: для product — *умножением*, для sum — *сложением* числа значений составных частей.

- **Sum type («или»)**: значение принадлежит ровно одному из нескольких вариантов. В Kotlin sum type — это `sealed class` / `sealed interface`: `JsonValue` — это `Null` *или* `Bool` *или* `Num` и т.д.
- **Product type («и»)**: значение объединяет несколько полей сразу. В Kotlin product type — это `data class`: `Obj` хранит `Map` *и* ничего больше.

Вместе они дают выразительность: sealed-иерархия из data-классов описывает любую древовидную структуру (как JSON ниже), а exhaustive `when` гарантирует, что обработаны все её формы.

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

**Контекст.** ADT — фундамент функционального программирования: на них держится моделирование данных в Haskell, Scala, Rust. Kotlin sealed classes — прямой аналог sum types из Scala и Haskell, поэтому те же приёмы (моделировать домен типами, а не флагами) переносятся в Kotlin почти один в один.

## Q7. Когда использовать sealed class vs interface?

Решает один вопрос: нужна ли вариантам **общая база** (поля в конструкторе или готовая реализация методов)?

- **Sealed class** — да, нужна. У всех `Animal` есть `name`, `age` и общий `description()` — их естественно вынести в базовый класс.
- **Sealed interface** — нет, общего состояния не требуется, зато хочется множественной реализации. Тип может одновременно быть `Serializable` и `Comparable`, что с одиночным наследованием класса невозможно.

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

Все ограничения вытекают из одного требования: компилятор должен видеть *все* наследники, чтобы гарантировать exhaustive `when`.

1. **Наследники — в том же модуле.** До Kotlin 1.5 они были обязаны лежать в одном файле; с 1.5 граница расширена до модуля, причём допускаются разные пакеты. Код из другого модуля расширить sealed-тип не сможет — иначе компилятор потерял бы контроль над списком вариантов.

2. **Sealed-тип нельзя объявить local или inner** — только top-level или nested в другом классе. У локальных и inner-типов нет стабильной видимости для всего модуля, поэтому закрытую иерархию на них не построить.

3. **Sealed class не наследуется напрямую от другого sealed class.** Когда нужна многоуровневая иерархия, используют композицию или sealed interface (его можно вкладывать в другой sealed interface).

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

Sealed class здесь — это **типобезопасный результат** работы DSL. Билдер собирает ошибки, а на выходе отдаёт `Validation`, который однозначно говорит «успех» (`Valid` со значением) или «провал» (`Invalid` со списком ошибок) — без `null` и без флагов.

В примере связка из трёх частей: sealed-тип `Validation` как результат, класс-билдер `ValidationBuilder` с lambda-with-receiver методами (`require`, `validate`) и входная функция `validate(target) { ... }`, которая запускает блок и вызывает `build()`. Вложенный `validate` показывает, как DSL рекурсивно проверяет под-объекты, собирая их ошибки в общий список.

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

Пометьте `@Serializable` и сам sealed class, и каждый его подкласс. `kotlinx.serialization` поддерживает полиморфизм «из коробки»: для закрытой иерархии вариант определяется автоматически, отдельная регистрация (как для open-полиморфизма) не нужна.

**Как работает дискриминатор.** При сериализации в JSON добавляется служебное поле `type` со строкой-меткой подтипа; при десериализации по нему восстанавливается нужный класс. `@SerialName` задаёт значение этой метки (по умолчанию взялось бы полное имя класса) — поэтому в JSON стоит читаемое `"user_created"`, а не путь к классу.

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

Само *имя* поля-дискриминатора (по умолчанию `type`) тоже настраивается — через `Json { classDiscriminator = "_type" }`. Это удобно, когда формат на той стороне ожидает другое имя ключа.

## Q11. Как моделировать состояния UI с sealed class?

Экран в каждый момент находится ровно в одном состоянии: грузится, показывает данные, пуст или в ошибке. Sealed-тип `UiState` делает эти состояния **взаимоисключающими по типу** — нельзя случайно держать одновременно `isLoading = true` и непустой список, как было бы с набором булевых флагов. Это устраняет целый класс рассинхронов в UI.

Связка стандартная для Android/Compose: ViewModel хранит текущее состояние в `StateFlow<UiState<...>>`, а экран реагирует через exhaustive `when` — на каждое состояние своя ветка, и компилятор не даст забыть ни одну. Обратите внимание: `Loading`/`Empty` — это `object` (данных нет, singleton), а `Success`/`Error` — `data class`, потому что несут полезную нагрузку.

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

«Stringly typed» — это когда вариант кодируется строкой (`"card"`, `"paypal"`), а связанные с ним данные передаются отдельными параметрами. Проблемы: опечатка в строке всплывёт только в рантайме, нерелевантные поля приходится тащить для всех типов, а ветка `else` маскирует забытый случай вместо ошибки сборки.

Sealed class превращает варианты в типы: у каждого свои поля (`Card` хранит `number`/`cvv`, `Crypto` — `walletAddress`/`currency`), а `when` без `else` становится exhaustive. Невалидная строка просто не выразима — компилятор ловит ошибку до запуска.

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

State machine — это набор состояний и разрешённых переходов между ними по событиям. Sealed class моделирует обе оси: `OrderState` — закрытый набор состояний, `OrderEvent` — закрытый набор событий.

**Приём в примере.** Каждое состояние само знает свои переходы: метод `next(event)` через `when` по событию возвращает следующее состояние, а на недопустимое событие бросает `IllegalStateException`. Так логика переходов живёт рядом с состоянием, а не в одном гигантском `when`. Терминальные состояния (`Delivered`, `Cancelled`) не имеют валидных переходов и сразу бросают исключение. Минус подхода — некорректный переход ловится в рантайме (через `throw`), а не компилятором.

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

Variance отвечает на вопрос: если `Int` — подтип `Any`, то будет ли `Result<Int>` подтипом `Result<Any>`? Ответ зависит от того, как тип-параметр используется внутри, и задаётся модификаторами `out` (ковариантность) и `in` (контравариантность).

- **`out T` (ковариантность).** `T` встречается только на «выходе» — в возвращаемых значениях, а не в параметрах. Тогда `Result<Int>` *является* `Result<Any>`: читая `Int`-результат как `Any`, мы ничего не нарушаем. Бонус: `object Empty : Result<Nothing>()` подходит под любой `Result<T>`, потому что `Nothing` — подтип всех типов.
- **`in T` (контравариантность).** `T` встречается только на «входе» — в параметрах. Тогда направление подтипирования переворачивается: `Consumer<Any>` можно использовать как `Consumer<Int>` (потребитель «чего угодно» справится и с `Int`).

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

**Эмпирическое правило.** `out` — для «производящих» типов, которые отдают значения наружу (`Result` с `Success`); `in` — для «потребляющих», которые принимают значения внутрь (`Comparator`).

## Q15. Какие типичные ошибки при работе с sealed class?

Большинство ошибок сводятся к одному: теряется главное преимущество sealed — проверка полноты компилятором.

1. **`when` как statement вместо expression** — теряете exhaustive-проверку. Без присваивания/возврата компилятор подразумевает `else`, и при добавлении нового подкласса ветка для него тихо отсутствует:

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

2. **Забыть `object` для singleton-вариантов.** Вариант без данных (`Loading`) должен быть `object` — тогда это единственный экземпляр. С `class` каждый вызов создаёт новый объект и ломает сравнение по ссылке:

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

3. **Sealed class там, где хватило бы enum.** Для простого перечисления без данных (статусы, типы) sealed — это over-engineering: enum легче и читается понятнее.

4. **Попытка «открыть» sealed через `open`.** Sealed-класс закрыт по своей природе, и в Kotlin 1.5+ правила sealed/open/final стали строже. Не пытайтесь обойти закрытость — если набор вариантов должен быть открытым, sealed вам просто не подходит.

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
