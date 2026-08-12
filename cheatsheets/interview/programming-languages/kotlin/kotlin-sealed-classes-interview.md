---
title: "Вопросы на собеседовании: Sealed-классы Kotlin"
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
# Вопросы на собеседовании: Sealed-классы Kotlin

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

- **Exhaustive `when`** — в `when` по sealed-типу можно перечислить все варианты без ветки `else`, а при добавлении нового подкласса сборка упадёт там, где его забыли обработать (для `when`-выражения так было всегда; для `when`-инструкции — молча до Kotlin 1.6, предупреждение в 1.6, ошибка с 1.7, см. Q3).
- **Контроль над иерархией** — извне не добавят новый *прямой* вариант. Косвенные наследники возможны, если прямой наследник сам расширяем: от `open` или `abstract` наследника можно наследоваться откуда угодно, от `sealed` — по-прежнему только внутри его модуля и пакета. В любом случае они попадают в уже существующую ветку `when`.

**Где живут наследники.** `Sealed` гарантирует, что все прямые наследники объявлены в том же модуле и в том же пакете (до Kotlin 1.5 ограничение было жёстче — в том же файле). Сам sealed-класс неявно `abstract`: создать его экземпляр напрямую нельзя, а конструктор по умолчанию `protected` — виден наследникам. Закрытость держится не на видимости конструктора, а на правиле компилятора о месте объявления: попытка отнаследоваться из другого пакета даёт «a class can only extend a sealed class or interface declared in the same package».

## Q2. Чем sealed class отличается от enum?

Ключевое отличие: `enum` — это фиксированный набор **значений** одного и того же типа с одинаковой структурой, а `sealed class` — фиксированный набор **прямых наследников**, каждый со своей структурой и своим числом экземпляров.

| Критерий | enum class | sealed class |
|----------|------------|--------------|
| Количество экземпляров | Фиксированное (один на constant) | Зависит от подтипа: у подтипа-класса сколько угодно (`Confirmed`, `Cancelled`), у подтипа-`object` ровно один (`Pending`) |
| Данные | Общий для всех констант набор полей, видимый через тип enum (тело отдельной константы может добавить члены, но снаружи они не видны) | Разные поля для каждого подкласса |
| Иерархия | Плоская на уровне типа: константа с телом — анонимный подкласс, наружу не видимый | Произвольная (sealed может содержать вложенный sealed) |
| Наследование | Снаружи не наследуется, класс не наследует, интерфейсы реализовать может | Подтипы наследуют сам sealed-тип и лежат в том же пакете и модуле; база может наследовать класс. Закрыт набор прямых наследников — не-final (`open`) подтип расширяем дальше |
| Применение | Простые перечисления (статусы, типы) | Алгебраические типы данных (ADT) |

**Практический критерий выбора.** Если всем вариантам нужны одни и те же поля (или полей нет вовсе) — берите `enum`: он легче и нагляднее. Как только разным вариантам нужны *разные* данные (у `Confirmed` — время, у `Cancelled` — причина и сумма возврата) — `enum` уже не подходит, нужен `sealed class`.

```kotlin
// enum — одинаковая структура
enum class OrderStatus { PENDING, CONFIRMED, CANCELLED }

// sealed — разная структура
sealed class OrderState {
    data object Pending : OrderState()                     // без данных; data object даёт осмысленный toString (Kotlin 1.9+)
    data class Confirmed(val confirmedAt: Instant) : OrderState()
    data class Cancelled(val reason: String, val refund: BigDecimal) : OrderState()
}
```

## Q3. Что такое exhaustive when и как его добиться?

**Exhaustive `when`** — это `when`, в котором перечислены все варианты sealed-типа, поэтому ветка `else` не нужна. Если subject nullable (`Shape?`), одних веток по подтипам мало: нужна ещё ветка `null` (или `else`), либо nullable снимают до `when`. Ценность не в краткости, а в том, что компилятор *требует* полноты: добавили новый подкласс — и все `when`, где его забыли, перестают компилироваться. Так компилятор показывает точные места, куда нужно добавить обработку, вместо тихой ошибки в рантайме.

```kotlin
import kotlin.math.PI

sealed class Shape {
    data class Circle(val radius: Double) : Shape()
    data class Square(val side: Double) : Shape()
    data class Triangle(val base: Double, val height: Double) : Shape()
}

fun area(shape: Shape): Double = when (shape) {  // все подтипы перечислены → when полон
    is Shape.Circle -> PI * shape.radius * shape.radius
    is Shape.Square -> shape.side * shape.side
    is Shape.Triangle -> 0.5 * shape.base * shape.height
    // else НЕ нужен — компилятор знает, что всё покрыто
}

// Если добавить в тело Shape новый подкласс → ошибка компиляции
// data class Rectangle(val width: Double, val height: Double) : Shape()
// area больше не компилируется. kotlinc 2.x:
// 'when' expression must be exhaustive. Add the 'is Rectangle' branch or an 'else' branch.
```

**Нужно ли что-то делать, чтобы проверка сработала.** С Kotlin 1.7 — нет: компилятор требует полноты от `when` по sealed-типу (а также по enum и `Boolean`), и неважно, используется он как *выражение* или как *statement* (значение отбрасывается). На произвольных типах вроде `String` или `Int` такого требования нет. Непокрытый вариант без `else` — ошибка компиляции. История вопроса: до Kotlin 1.6 неполный `when`-statement компилировался молча — отсюда старые приёмы вроде `.also { }`, `.let { }` или extension-property `.exhaustive`, превращавшие statement в выражение, чтобы включить проверку вручную. В 1.6 к такому statement добавилось предупреждение, а с 1.7 это ошибка компиляции, и приём стал не нужен.

## Q4. Чем sealed interface отличается от sealed class?

**Sealed interface** (Kotlin 1.5+) — интерфейс с закрытым набором прямых реализаций (компилятор знает непосредственных наследников; если сам наследник объявлен open, его ветка снаружи уже не закрыта): даёт ту же exhaustive-проверку в `when`, что и sealed class, но снимает ограничение одиночного наследования. Отличия от sealed class:

- **Множественная реализация.** Класс может реализовать несколько sealed-интерфейсов сразу — то есть один тип способен принадлежать нескольким закрытым иерархиям одновременно. С sealed class так нельзя: наследоваться можно только от одного класса.
- **Нет состояния.** Как у любого интерфейса, у sealed interface не может быть полей-конструктора и backing field: объявить общее свойство (`val code: Int`) он умеет, а хранить его — нет, хранение ложится на каждую реализацию. Нужны общие поля в базе — берите sealed class.

Пример ниже показывает первый пункт: `TimeoutError` входит и в `NetworkError`, и в `IOError`.

```kotlin
sealed interface AppError
sealed interface IOError : AppError
sealed interface NetworkError : AppError

data class FileNotFound(val path: String) : IOError
data class TimeoutError(val seconds: Int) : NetworkError, IOError  // оба
data class ConnectionRefused(val host: String) : NetworkError
```

```kotlin
// Применение: пересекающийся тип разбирают ПЕРВЫМ
fun handle(error: AppError): String = when (error) {
    is TimeoutError -> "Timeout"          // входит и в IOError, и в NetworkError
    is IOError -> "IO problem"
    is NetworkError -> "Network problem"
    // exhaustive: у AppError ровно два прямых наследника — IOError и NetworkError, оба покрыты
}

// Так — не надо: TimeoutError уйдёт в первую подходящую ветку,
// и сетевой таймаут отчитается как "IO problem"
// when (error) { is IOError -> "IO problem"; is NetworkError -> "Network problem" }

fun handleIO(error: IOError): String = when (error) {
    is FileNotFound -> "Missing: ${error.path}"
    is TimeoutError -> "Timeout: ${error.seconds}s"
}
```

**Эмпирическое правило.** Разделитель — состояние, а не реализация: общую реализацию (дефолтные тела методов, свойства с `get()`) интерфейс несёт сам. Если вариантам не нужно общее *хранимое* состояние — берите sealed interface: он гибче за счёт множественной реализации. Sealed class оставляйте на случаи, когда базовому типу нужны собственные поля.

## Q5. Как реализовать паттерн Result с sealed class?

`Result` — это sum type из двух вариантов: `Success` с полезным значением и `Failure` с ошибкой. Идея паттерна — сделать ошибку *значением*, а не исключением: когда результат разбирают `when`'ом, компилятор требует покрыть обе ветки (exhaustive when по sealed-типу), а при работе через `map`/`onSuccess`/`onFailure` ошибка никуда не девается — едет в обёртке до места разбора. Это убирает невидимые `throw` из сигнатур и делает обработку ошибок частью типа.

Ключевые методы — `map`/`flatMap` (преобразовать успех, пробросив ошибку как есть) и `onSuccess`/`onFailure` (побочные действия без распаковки). Все они помечены `inline`, чтобы лямбды не создавали лишних объектов. Имя `Result` перекрывает `kotlin.Result` из автоимпортируемого пакета `kotlin` (там своя обёртка над значением или `Throwable`): компиляции это не ломает, но при чтении и импортах из других пакетов появляется двусмысленность — в проекте удобнее назвать тип `Either`/`Outcome` либо импортировать свой явно.

```kotlin
sealed class Result<out T, out E> {
    data class Success<out T>(val value: T) : Result<T, Nothing>()
    data class Failure<out E>(val error: E) : Result<Nothing, E>()

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

val result = fetchUser("123")
    .map { it.name.uppercase() }
    .onSuccess { println("Got: $it") }
    .onFailure { println("Error: $it") }

// Финальный разбор: полноту веток Success/Failure проверяет компилятор
val message = when (result) {
    is Result.Success -> "Пользователь: ${result.value}"
    is Result.Failure -> "Ошибка: ${result.error}"
}
```

## Q6. Что такое ADT (Algebraic Data Types) и как sealed class их реализует?

**ADT (алгебраические типы данных)** — типы, которые конструируются из двух базовых операций. Их называют «алгебраическими», потому что число возможных значений типа считается арифметически: для product — *умножением*, для sum — *сложением* числа значений составных частей.

- **Sum type («или»)**: значение принадлежит ровно одному из нескольких вариантов. В Kotlin sum type — это `sealed class` / `sealed interface`: `JsonValue` — это `Null` *или* `Bool` *или* `Num` и т.д.
- **Product type («и»)**: значение объединяет несколько полей сразу. В Kotlin product type — это `data class`: `data class Point(val x: Int, val y: Int)` хранит `x` *и* `y` сразу. В примере ниже ветвь `Obj` вырождена до одного поля `fields` — произведение там не видно, оно проявляется, когда полей несколько.

К этим двум операциям добавляется третий приём — рекурсивная ссылка типа на самого себя (`List<JsonValue>` внутри `Arr`, `Map<String, JsonValue>` внутри `Obj`). Вместе они дают выразительность: sealed-иерархия из data-классов описывает древовидную структуру (как JSON ниже), а exhaustive `when` гарантирует, что обработаны все её формы.

```kotlin
// Sum type: JsonValue - ЛИБО Null, Bool, Num, Str, Arr, Obj
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

Решает один вопрос: нужны ли вариантам **общие хранимые данные** — параметры конструктора и backing fields? Метод с готовым телом умеет объявить и интерфейс, поэтому разделителем служит состояние, а не наличие реализации.

- **Sealed class** — да, нужны. У всех `Animal` есть `name` и `age`, которые кто-то должен хранить; `description()` уехал в базовый класс за компанию с полями, а не потому, что интерфейс не умеет тел методов.
- **Sealed interface** — нет, хранимого состояния не требуется, зато один тип должен выступать сразу в нескольких ролях: собственные `Jsonable` и `Ranked` реализуются вместе, что при одиночном наследовании класса недоступно. Сам модификатор `sealed` тут отвечает не за множественную реализацию, а за закрытый набор наследников: они обязаны лежать в том же модуле и пакете, зато `when` по ним получается исчерпывающим.

```kotlin
// Sealed class — когда вариантам нужны общие хранимые поля
sealed class Animal(val name: String, val age: Int) {
    abstract fun makeSound(): String
    fun description() = "$name is $age years old"
}

class Dog(name: String, age: Int) : Animal(name, age) {
    override fun makeSound() = "Woof"
}

// Sealed interface — когда общего состояния нет, а тип нужен сразу в нескольких ролях.
// Имена свои, доменные: стандартные java.io.Serializable и kotlin.Comparable здесь ни при чём
sealed interface Jsonable {
    fun toJson(): String
}

sealed interface Ranked {
    fun rank(): Int
}

// Класс может реализовать несколько sealed-интерфейсов
data class User(val name: String) : Jsonable, Ranked {
    override fun toJson() = "{\"name\":\"$name\"}"
    override fun rank() = name.length
}

data class Team(val title: String) : Jsonable, Ranked {
    override fun toJson() = "{\"title\":\"$title\"}"
    override fun rank() = title.length
}

// Ради чего sealed: набор реализаций закрыт (тот же модуль и пакет), when без else
fun label(item: Ranked): String = when (item) {
    is User -> "пользователь, ранг ${item.rank()}"
    is Team -> "команда, ранг ${item.rank()}"
}
```

## Q8. Какие ограничения у sealed class в Kotlin 1.5+?

Все ограничения вытекают из одного требования: компилятор должен видеть *все* наследники, чтобы гарантировать exhaustive `when`.

1. **Прямые наследники — в том же модуле и пакете.** До Kotlin 1.5 они были обязаны лежать в одном файле; с 1.5 их можно разложить по разным файлам, но пакет и модуль остаются общими. Код из другого модуля или пакета не может наследовать sealed-тип напрямую — иначе компилятор потерял бы контроль над списком вариантов. Косвенно — может: если в том же пакете объявлен `open class Child : Base()`, то `class GrandChild : Child()` компилируется откуда угодно, включая другой модуль. Exhaustive `when` от этого не страдает: он проверяется по прямым наследникам, а `GrandChild` попадает в ветку `Child`.

2. **Наследник не может быть локальным или анонимным объектом** — только top-level или nested в другом именованном классе, интерфейсе или объекте, и у него должно быть полное имя. У локальных и анонимных типов нет стабильного имени, поэтому в закрытый список вариантов их не внести. Отдельная деталь: enum-класс не может наследоваться от sealed class, но реализовать sealed interface ему разрешено.

3. **Конструкторы — только `protected` (по умолчанию) или `private`.** До 1.5 дефолтом был `private` — с раскладкой наследников по файлам пакета такой дефолт перестал работать, поэтому с 1.5 он `protected`. Сам sealed class абстрактен, экземпляр базового типа создать нельзя — только экземпляры наследников. А вот наследовать один sealed class от другого — можно: `sealed class B : A()` легален, и многоуровневые sealed-иерархии (под-семейства вариантов внутри общего типа) — нормальная практика.

```kotlin
// В Kotlin 1.5+
// Файл a.kt
package com.example.domain

sealed class Base

// Файл b.kt — тот же пакет и модуль
package com.example.domain

data class Child1(val id: Long) : Base()  // ОК

// Файл c.kt — другой пакет (то же самое и для другого модуля)
package com.example.api

import com.example.domain.Base

data class Forbidden(val id: Long) : Base()  // ОШИБКА
```

## Q9. Как использовать sealed class в Kotlin DSL?

Sealed class здесь — это **типобезопасный результат** работы DSL. Билдер собирает ошибки, а на выходе отдаёт `Validation`, который однозначно говорит «успех» (`Valid` со значением) или «провал» (`Invalid` со списком ошибок) — без `null` и без флагов.

В примере связка из трёх частей: sealed-тип `Validation` как результат, класс-билдер `ValidationBuilder` с методами `require` (условие плюс ленивое сообщение `() -> String`) и `validate` (вложенный блок с receiver'ом-билдером) и входная функция `validate(target) { ... }`, которая запускает блок и вызывает `build()`. Вложенный `validate` показывает, как DSL рекурсивно проверяет под-объекты, собирая их ошибки в общий список.

```kotlin
sealed class Validation<out T> {
    data class Valid<T>(val value: T) : Validation<T>()
    data class Invalid(val errors: List<String>) : Validation<Nothing>()
}

// DSL для валидации
class ValidationBuilder<T>(val target: T) {
    private val errors = mutableListOf<String>()

    fun require(condition: Boolean, message: () -> String) {
        if (!condition) errors += message()
    }

    fun <V> validate(value: V, block: ValidationBuilder<V>.() -> Unit) {
        val nested = ValidationBuilder(value)
        nested.block()
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
    validate(target.address) {
        require(target.city.isNotBlank()) { "City required" }
    }
}
```

## Q10. Как сериализовать sealed class с kotlinx.serialization?

Пометьте `@Serializable` и сам sealed class, и каждый его подкласс. `kotlinx.serialization` поддерживает полиморфизм «из коробки»: для закрытой иерархии вариант определяется автоматически, отдельная регистрация (как для open-полиморфизма) не нужна.

**Как работает дискриминатор.** Дискриминатор появляется тогда, когда работает сериализатор базового типа: статический тип значения — `Event` (как в примере ниже) или сериализатор передан явно, `Json.encodeToString(Event.serializer(), value)`. В этом случае в JSON добавляется служебное поле `type` со строкой-меткой подтипа, а при десериализации по нему восстанавливается нужный класс. Если же статический тип значения — подкласс (`Json.encodeToString(Event.UserCreated("u-1", "Alice"))`), возьмётся его собственный сериализатор, и поля `type` в JSON не будет. `@SerialName` задаёт значение этой метки: по умолчанию ею стало бы полное имя класса с пакетом и вложенностью — например, `com.example.Event.UserCreated`. При таком контракте переименование или переезд класса ломает совместимость формата, а явный `@SerialName` его фиксирует — поэтому в JSON стоит читаемое `"user_created"`, а не путь к классу.

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

Связка стандартная для Android/Compose: ViewModel хранит текущее состояние в `StateFlow<UiState<...>>`, а экран реагирует через exhaustive `when` — на каждое состояние своя ветка, и компилятор не даст забыть ни одну. Обратите внимание: `Loading`/`Empty` — варианты без данных, поэтому они синглтоны, а `Success`/`Error` — `data class`, потому что несут полезную нагрузку. С Kotlin 1.9 такие синглтоны идиоматично объявлять `data object`: тот же единственный экземпляр, но с человекочитаемым `toString()` («Loading» вместо имени класса с хэшем) — это и есть главное отличие от обычного `object`. Сгенерированные `equals`/`hashCode` добавляют предсказуемость в пограничных сценариях, где единственность экземпляра нарушается (десериализация, рефлексия), и дают симметрию с соседними data class-вариантами. Ещё одна деталь для листинга ниже: внутри `viewModelScope.launch` отмену не стоит превращать в `UiState.Error` — `CancellationException` пробрасывают дальше, иначе уход с экрана или повторный запрос покажет ошибку вместо тихого сворачивания работы.

```kotlin
// Классический паттерн для Android/Compose
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retryable: Boolean = true) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}

class OrdersViewModel(private val repo: OrderRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Order>>> = _state.asStateFlow()

    fun loadOrders() = viewModelScope.launch {
        _state.value = UiState.Loading
        _state.value = try {
            val orders = repo.getOrders()
            if (orders.isEmpty()) UiState.Empty
            else UiState.Success(orders)
        } catch (e: CancellationException) {
            throw e
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

«Stringly typed» — это когда вариант кодируется строкой (`"card"`, `"paypal"`), а связанные с ним данные передаются отдельными параметрами. Проблемы: опечатка в строке обнаруживается только в рантайме, а нерелевантные поля приходится тащить для всех типов — обычно как nullable-параметры, которые в каждой ветке разворачивают проверками или `!!`.

Sealed class превращает варианты в типы: у каждого свои поля (`Card` хранит `number`/`cvv`, `Crypto` — `walletAddress`/`currency`), причём поля обязательные и non-null — nullable-заглушки под чужие варианты уходят из модели. `when` без `else` становится exhaustive: для when-выражения полнота требовалась всегда, для when-оператора над sealed это warning в Kotlin 1.6 и ошибка компиляции начиная с Kotlin 1.7. Ветка `else`, наоборот, гасит проверку полноты: новый наследник молча уходит в неё вместо ошибки сборки. Компилятор закрывает набор прямых наследников в пределах модуля; `open`-наследник расширяем снаружи, но ветка `is Card` покрывает и его потомков. Невалидная строка просто не выразима — компилятор ловит ошибку до запуска.

```kotlin
// ПЛОХО: stringly typed
fun processPayment(
    paymentType: String,
    amount: Double,
    cardNumber: String? = null,
    cvv: String? = null,
    email: String? = null,
    walletAddress: String? = null
) {
    when (paymentType) {
        "card" -> ...
        "paypal" -> ...
        "crypto" -> ...
        else -> throw IllegalArgumentException("Unknown type: $paymentType")
    }
}

// ЛУЧШЕ: sealed class как type-safe замена
sealed class PaymentMethod {
    // CVV после авторизации не хранят — здесь он лишь показывает набор полей варианта
    data class Card(val number: String, val cvv: String) : PaymentMethod()
    data class PayPal(val email: String) : PaymentMethod()
    data class Crypto(val walletAddress: String, val currency: String) : PaymentMethod()
}

fun processPayment(method: PaymentMethod, amount: Double) {
    when (method) {
        is PaymentMethod.Card -> chargeCard(method.number, method.cvv, amount)
        is PaymentMethod.PayPal -> chargePayPal(method.email, amount)
        is PaymentMethod.Crypto -> chargeCrypto(method.walletAddress, method.currency, amount)
        // exhaustive — забытый вариант не соберётся; неизвестной строки внутри домена уже нет
    }
}
```

## Q13. Как реализовать State Machine через sealed class?

State machine — это набор состояний и разрешённых переходов между ними по событиям. Sealed class моделирует обе оси: `OrderState` — закрытый набор состояний, `OrderEvent` — закрытый набор событий.

**Приём в примере.** Каждое состояние само знает свои переходы: у нетерминальных состояний `next(event)` — это `when` по событию, который возвращает следующее состояние, а на недопустимое бросает `IllegalStateException`. Так логика переходов живёт рядом с состоянием, а не в одном гигантском `when`. Терминальные состояния (`Delivered`, `Cancelled`) не имеют валидных переходов: их `next` — сразу `throw`, без `when`. Минус подхода — некорректный переход ловится в рантайме (через `throw`), а не компилятором.

```kotlin
import java.time.Instant

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

    // наследник sealed-типа обязан быть именованным: анонимный object так не унаследуешь
    object Printing : Consumer<Any>() {
        override fun consume(value: Any) { println(value) }
    }
}

val anyConsumer: Consumer<Any> = Consumer.Printing
val intConsumer: Consumer<Int> = anyConsumer  // ОК благодаря in
```

**Эмпирическое правило.** `out` — для «производящих» типов, которые отдают значения наружу (`Result` с `Success`); `in` — для «потребляющих», которые принимают значения внутрь. Привычный пример потребителя — `Comparator`, но объявлен он инвариантно: `kotlin.Comparator` — это typealias для `java.util.Comparator`, а declaration-site variance в Java нет. Контравариантность там добавляется на месте использования: `fun <T> Iterable<T>.sortedWith(comparator: Comparator<in T>)`. Из объявленных контравариантными в stdlib — например, `kotlin.Function1<in P1, out R>`.

## Q15. Какие типичные ошибки при работе с sealed class?

Большинство ошибок сводятся к одному: теряется главное преимущество sealed — проверка полноты компилятором.

1. **Ветка `else` в `when` по sealed-типу** — теряете exhaustive-проверку. С Kotlin 1.7 компилятор требует полноты от любого `when` по sealed-типу — и как выражения, и как statement (в 1.6 неполный statement давал warning, до 1.6 диагностики не было). Но стоит написать `else`, и проверка отключается: новый подкласс молча проваливается в эту ветку:

```kotlin
// ПЛОХО: else отключает exhaustive-проверку
when (result) {
    is Success -> println("ok")
    else -> println("fail")
    // добавили новый подкласс — он молча уйдёт в else
}

// ХОРОШО: все варианты перечислены явно, else не нужен
val message = when (result) {
    is Success -> "ok"
    is Failure -> "fail"
}
```

2. **Забыть `object` для singleton-вариантов.** Вариант без данных (`Loading`) должен быть `object` — тогда это единственный экземпляр; с Kotlin 1.9 идиоматичнее `data object`: тот же singleton, но с читаемым `toString()` и корректным `equals` (полезно, например, после десериализации, когда может возникнуть второй экземпляр). С обычным `class` каждый вызов создаёт новый объект и ломает сравнение по ссылке:

```kotlin
// ПЛОХО
sealed class Status {
    class Loading : Status()  // каждый вызов Loading() создаёт новый экземпляр: == и === для двух таких объектов дают false
}

// ХОРОШО
sealed class Status {
    object Loading : Status()  // singleton
}
```

3. **Sealed class там, где хватило бы enum.** Для простого перечисления без данных (статусы, типы) sealed — это over-engineering: enum легче и читается понятнее.

4. **Попытка «открыть» sealed через `open`.** Sealed-класс закрыт по своей природе: пометить его `open` компилятор не даёт ни в одной версии. В Kotlin 1.5 правила sealed, наоборот, ослабили — sealed interface стал стабильным, а наследников разрешили размещать в любом файле того же пакета и модуля. Не пытайтесь обойти закрытость — если набор вариантов должен быть открытым, sealed вам просто не подходит.

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
