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


> [!mcq]
>
> **Вопрос:** Что такое `sealed class` в Kotlin и какую проблему он решает по сравнению с обычным `abstract class`?
>
> ---
>
> #### A) `sealed class` — это синтаксический сахар над `enum class`: компилятор разворачивает его в enum с typed payload — ❌ Неверно
>
> **Что на самом деле:** `sealed` и `enum` — два разных механизма. `enum` хранит фиксированное число одинаково устроенных констант (один и тот же класс), а `sealed` объявляет ограниченное множество **разных подклассов** с произвольной структурой полей. JVM-байткод тоже разный: `enum` компилируется в `final class extends java.lang.Enum`, а `sealed` — в обычный abstract class плюс subclasses, помеченные `PermittedSubclasses` (Kotlin 1.5+ → Java 17 sealed).
>
> **Откуда путаница:** оба позволяют делать exhaustive `when`, оба «закрытые» иерархии. Поверхностно кажется, что разница только в имени keyword.
>
> **Если бы это было правдой:** нельзя было бы хранить разные поля в подклассах (`Success(data)` vs `Error(message)`), нельзя было бы создавать несколько инстансов одного варианта (`Success(orderA)` и `Success(orderB)`), и не было бы дженериков (`Result<out T>`).
>
> ---
>
> #### B) `sealed class` — это абстрактный класс с **закрытым множеством подклассов**, известным компилятору на этапе сборки; все наследники должны находиться в том же модуле (Kotlin 1.5+) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `sealed class` решает проблему «контролируемой иерархии типов»: обычный `abstract` или `open` класс может быть унаследован кем угодно, в любом модуле, в любое время — компилятор не знает полный список подтипов. Это ломает exhaustive `when`, ADT-моделирование и инварианты доменной модели.
>
> `sealed` фиксирует список разрешённых наследников на этапе компиляции. Правила:
> - До Kotlin 1.5 — все наследники в том же **файле**.
> - С Kotlin 1.5+ — все наследники в том же **модуле** (compilation unit), могут быть в разных пакетах.
> - Сам `sealed class` нельзя инстанцировать напрямую (как abstract).
> - Наследник может быть `class`, `data class`, `object`, `data object` (Kotlin 1.9+), или другим `sealed class`.
>
> **Пример:**
> ```kotlin
> // Без sealed — открытая иерархия, не контролируема
> abstract class ApiResponse  // кто угодно может добавить ApiResponse в чужом модуле
>
> // С sealed — закрытая иерархия
> sealed class ApiResponse<out T> {
>     data class Success<T>(val data: T, val etag: String) : ApiResponse<T>()
>     data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()
>     data object Loading : ApiResponse<Nothing>()
> }
>
> fun render(r: ApiResponse<User>): String = when (r) {
>     is ApiResponse.Success -> "User: ${r.data.name}"
>     is ApiResponse.Error   -> "Error ${r.code}: ${r.message}"
>     ApiResponse.Loading    -> "..."
>     // else не нужен — компилятор знает все варианты
> }
> ```
>
> **Когда применять:** Result/Either-обёртки (см. Arrow, kotlinx.coroutines `Result`), UI-state в Android (Jetpack Compose), domain events в event-sourced системах (Axon, Spring Modulith), DSL-узлы AST. В JetBrains, JetBrains Space, Square (Workflow library), Cash App широко используют sealed для navigation/state.
>
> **Подводные камни:** добавление нового варианта — **breaking change** для всех `when`-выражений в кодовой базе → запускайте сборку после добавления, чтобы компилятор показал все места. `sealed` нельзя делать `local` или `inner`, только top-level или nested в другом классе. При публикации как библиотеки клиенты в чужом модуле не смогут добавлять свои варианты — это и есть цель.
>
> **Связанные вопросы:** [[Q2]] — разница с `enum`; [[Q3]] — exhaustive `when`; [[Q8]] — ограничения Kotlin 1.5+.
>
> ---
>
> #### C) `sealed class` запрещает создавать любые экземпляры; работает только как marker interface для типизации компиляции — ❌ Неверно
>
> **Что на самом деле:** сам `sealed class` действительно не инстанцируется (как abstract), но его **наследники прекрасно создают экземпляры**. Это его основное назначение: `Result.Success(42)` — это полноценный объект с данными, который существует в рантайме. Без рантайм-экземпляров не было бы pattern matching.
>
> **Откуда путаница:** аналогия с marker interfaces (`Serializable`, `Cloneable`) — там тоже «нельзя инстанцировать». Но marker interface — это интерфейс без методов для метаданных, а `sealed` — иерархия с реальными экземплярами подклассов.
>
> **Если бы это было правдой:** sealed был бы бесполезен — нельзя было бы написать `val state: UiState = UiState.Loading`, нельзя было бы возвращать `Result.Success(user)` из репозитория. Это была бы compile-time only фича без runtime-смысла.
>
> ---
>
> #### D) `sealed class` гарантирует, что все наследники находятся в **одном пакете** (не модуле) — это позволяет компилятору проверять exhaustiveness — ❌ Неверно
>
> **Что на самом деле:** правило — **в том же модуле** (Gradle module, Maven artifact), не пакете. Наследники могут быть в разных пакетах одного модуля: `com.app.domain.Success` и `com.app.errors.Failure` для общего `com.app.Result` — валидно. До Kotlin 1.5 правило было ещё строже — тот же файл.
>
> **Откуда путаница:** в Java 17 `sealed` интерфейсы требуют `permits`-список с явными именами; неопытные могут спроецировать это правило на Kotlin как «один пакет». Также путаница идёт от visibility modifiers — `internal` действительно ограничен модулем.
>
> **Если бы это было правдой:** ADT с большим числом вариантов был бы вынужден лежать в одном пакете → перегруженные «свалки» типа `com.app.allstates`. Реальное правило (`module`) позволяет логично организовывать по пакетам.

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


> [!mcq]
>
> **Вопрос:** Чем `sealed class` принципиально отличается от `enum class` в Kotlin?
>
> ---
>
> #### A) `enum class` поддерживает дженерики и наследование от других классов, а `sealed class` — нет — ❌ Неверно
>
> **Что на самом деле:** ровно наоборот. `enum class` в Kotlin **не может** быть дженериком (`enum class Foo<T>` — ошибка компиляции), не может наследоваться от других классов (только от Enum), но может реализовывать интерфейсы. `sealed class`, напротив, поддерживает дженерики (`sealed class Result<out T>`), может наследоваться от других open/abstract классов, и его подклассы могут быть data class / object / sealed.
>
> **Откуда путаница:** в Java enum может иметь generic interface methods (`<T extends Comparable<T>>`). Также Java-style enum constants выглядят «классоподобно» с overridden методами — кажется, что они полноценны.
>
> **Если бы это было правдой:** не существовало бы паттерна `Result<T>` через sealed (он использует дженерики), и весь Arrow Either/Validated был бы невозможен на Kotlin.
>
> ---
>
> #### B) `enum` не позволяет хранить разные поля в разных вариантах, `sealed` позволяет — но в остальном оба работают одинаково — ❌ Неверно
>
> **Что на самом деле:** различий гораздо больше — это **главное**, но не единственное. `enum` — фиксированное количество singleton-экземпляров (нельзя сделать `Status.PENDING("a")` и `Status.PENDING("b")` — это один объект). `sealed` — у каждого подкласса своя arity: можно создать сколько угодно `Success(1)`, `Success(2)`, и каждый — отдельный объект. Также `enum` поддерживает `values()`/`valueOf()` и сериализуется по имени, `sealed` — нет.
>
> **Откуда путаница:** оба обеспечивают exhaustive `when`, поверхностное сходство велико. Если использовать sealed только с `object` (без данных) — отличие действительно почти стирается, но это уже не sealed-by-spirit.
>
> **Если бы это было правдой:** не было бы паттерна `Result.Success<User>(userInstance)` для каждого пользователя — у каждой `Success` свой `value`. Не было бы возможности использовать sealed для UiState с динамическим payload.
>
> ---
>
> #### C) `sealed class` — закрытая иерархия **разных подклассов** с произвольной структурой и собственными данными; `enum class` — фиксированный набор **singleton-констант** одинаковой структуры — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Ключевые различия в одной таблице:
>
> | Аспект | `enum class` | `sealed class` |
> |---|---|---|
> | Природа | Фиксированное число констант одного класса | Иерархия разных классов |
> | Поля | Одинаковые у всех констант | У каждого наследника свои |
> | Экземпляры | Один на константу (singleton) | Сколько угодно (если data class) |
> | Дженерики | Не поддерживает | Поддерживает (`Result<out T>`) |
> | Наследование от классов | Нет | Да (может extends open class) |
> | values()/valueOf() | Есть из коробки | Нужно реализовать вручную |
> | JVM bytecode | `final class extends Enum` | abstract class + `PermittedSubclasses` (Kotlin 1.5+) |
> | Сериализация | По имени константы | Нужен дискриминатор (kotlinx.serialization @SerialName) |
>
> **Пример:**
> ```kotlin
> // enum — одинаковая структура у всех
> enum class HttpMethod(val safe: Boolean, val idempotent: Boolean) {
>     GET(safe = true,  idempotent = true),
>     POST(safe = false, idempotent = false),
>     PUT(safe = false, idempotent = true),
>     DELETE(safe = false, idempotent = true);
> }
>
> // sealed — разные данные в каждом варианте
> sealed class HttpResponse {
>     data class Success(val body: String, val etag: String) : HttpResponse()
>     data class Redirect(val location: String, val permanent: Boolean) : HttpResponse()
>     data class ClientError(val status: Int, val problemJson: String) : HttpResponse()
>     data class ServerError(val status: Int, val retryAfter: Duration?) : HttpResponse()
> }
> ```
>
> **Когда применять:** **enum** — простые перечисления без data (статусы заказа, дни недели, протоколы), когда нужен `valueOf` для строкового маппинга (REST query params, env vars). **sealed** — ADT с разными payload (UiState, ApiResult, DomainEvent), когда нужны дженерики или подклассы должны нести разные данные.
>
> **Подводные камни:** `enum` нельзя расширить — добавление нового члена ломает binary compatibility для библиотек. У `sealed` нет `values()` — итерировать все подклассы нужно через reflection (`MyClass::class.sealedSubclasses`), и это работает только для top-level подклассов и только на JVM. `enum` сериализуется в JSON по умолчанию (имя), а `sealed` требует явного дискриминатора.
>
> **Связанные вопросы:** [[Q1]] — основы `sealed class`; [[Q3]] — exhaustive `when`; [[Q10]] — kotlinx.serialization для sealed.
>
> ---
>
> #### D) И `sealed`, и `enum` компилируются в одинаковый Java-байткод (`final class extends Enum`); разница только в синтаксисе Kotlin — ❌ Неверно
>
> **Что на самом деле:** байткод существенно разный. `enum` → `final class Foo extends java.lang.Enum<Foo>` с константами как `public static final` полями. `sealed class` → обычный `abstract class` плюс отдельные классы-наследники; в Kotlin 1.5+/Java 17 — с атрибутом `PermittedSubclasses` в class file. Это два разных JVM-механизма.
>
> **Откуда путаница:** оба «закрытые» с точки зрения exhaustiveness; кажется, что компилятор делает похожую проверку, значит и байткод похож.
>
> **Если бы это было правдой:** не работала бы Java-интероп с sealed (Java-код не видит `PermittedSubclasses`), и нельзя было бы создавать несколько экземпляров `Success` (enum constants — singleton). Также рефлексия (`sealedSubclasses` vs `enumConstants`) работала бы одинаково — а это не так.

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


> [!mcq]
>
> **Вопрос:** Что такое exhaustive `when` для `sealed class` и как корректно его обеспечить?
>
> ---
>
> #### A) Достаточно перечислить все типы через `is` — компилятор сам поймёт exhaustive, даже если `when` используется как statement без присваивания — ❌ Неверно
>
> **Что на самом деле:** в Kotlin 1.6 и ранее `when` **как statement** (без присваивания, без return) **не требует** exhaustive — компилятор молчит, даже если пропущены ветки. Только `when` **как expression** (результат присваивается, возвращается или передаётся) проверяется на полноту. С Kotlin 1.7+ для sealed/enum statement тоже даёт warning по умолчанию (флаг `-Xnon-exhaustive-when-statements`), а с 2.0 — error.
>
> **Откуда путаница:** в туториалах часто пишут «компилятор проверяет sealed» без уточнения statement vs expression. Также Scala/Rust проверяют match всегда — middle-разработчик переносит эту модель на Kotlin.
>
> **Если бы это было правдой:** не было бы знаменитого антипаттерна с `.also { }` в конце или с трюком `val _ = when (...)` для форсирования expression-формы. Это самая частая «тихая» ошибка в проде: добавили новый case в sealed, but забыли обновить when-statement.
>
> ---
>
> #### B) Нужно всегда добавлять `else -> throw IllegalStateException()` — это и есть exhaustive — ❌ Неверно
>
> **Что на самом деле:** `else` в `when` над sealed — **антипаттерн**, который **отключает** exhaustive-проверку. Если есть `else`, компилятор больше не сообщит «missing branch» при добавлении нового подкласса — `else` его поглотит, и баг уйдёт в runtime. Цель exhaustive — поймать пропуск **на компиляции**, а не throw в проде.
>
> **Откуда путаница:** Java `switch` исторически требовал `default`, многие линтеры до Java 14 ругались на отсутствие default. Этот рефлекс переносится в Kotlin. Также — общая привычка «defensive programming».
>
> **Если бы это было правдой:** sealed терял бы свою главную ценность — fail-fast на сборке. Если добавили `PaymentMethod.ApplePay`, а в `when` всё ещё `else -> throw`, баг проявится только когда реальный пользователь нажмёт Apple Pay в проде.
>
> ---
>
> #### C) Exhaustive `when` достигается только через kotlinx-kotlin-reflect: компилятор использует `sealedSubclasses` для проверки — ❌ Неверно
>
> **Что на самом деле:** exhaustive — **compile-time** проверка K2/K1 frontend компилятора, она не использует kotlin-reflect. Компилятор знает permitted subclasses из FIR (frontend IR) и проверяет покрытие веток. Kotlin-reflect — это рантайм-библиотека, она не участвует в проверке `when`. `sealedSubclasses` доступен только в reflect-режиме и не имеет отношения к компиляции.
>
> **Откуда путаница:** существование `KClass.sealedSubclasses` намекает на «список подклассов», и кажется, что компилятор тоже им пользуется. Также reflect-зависимость нужна для kotlinx.serialization, что путает.
>
> **Если бы это было правдой:** проекты без kotlin-reflect (минимальный Android, KMP-таргеты типа JS/Native, где reflect ограничен) не могли бы использовать exhaustive `when` — но они могут. Также cold-start увеличивался бы из-за загрузки reflect.
>
> ---
>
> #### D) `when` должен использоваться **как expression** (значение присваивается / возвращается); тогда компилятор требует покрытия всех вариантов sealed без `else`. Для statement-формы используют принуждение через `val _ = when {...}` или `.also { }` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Exhaustiveness в Kotlin исторически работала только для **expression** `when`. Причина в системе типов: expression должен производить значение, и компилятор обязан гарантировать, что хотя бы одна ветка выполнится. Для statement (Unit) такого требования не было — компилятор не нуждался в полноте.
>
> С Kotlin 1.7 поведение начали ужесточать: для statement `when` над sealed/enum/Boolean компилятор выдаёт warning. С Kotlin 2.0 (`languageVersion = "2.0"`) — это уже error.
>
> **Пример:**
> ```kotlin
> sealed class PaymentMethod {
>     data class Card(val pan: String) : PaymentMethod()
>     data class PayPal(val email: String) : PaymentMethod()
>     data object ApplePay : PaymentMethod()
> }
>
> // 1. Expression — exhaustive enforced, без else
> fun describe(m: PaymentMethod): String = when (m) {
>     is PaymentMethod.Card    -> "Card ${m.pan.takeLast(4)}"
>     is PaymentMethod.PayPal  -> "PayPal ${m.email}"
>     PaymentMethod.ApplePay   -> "Apple Pay"
>     // добавили новый вариант — компилятор покажет "when expression must be exhaustive"
> }
>
> // 2. Statement до Kotlin 1.7 — НЕ проверялось
> fun route(m: PaymentMethod) {
>     when (m) {                          // statement, не expression
>         is PaymentMethod.Card -> chargeCard(m.pan)
>         is PaymentMethod.PayPal -> chargePayPal(m.email)
>         // забыли ApplePay — компилятор молчал в 1.6, warn в 1.7+, error в 2.0
>     }
> }
>
> // 3. Принуждение statement → expression (трюк до 1.7)
> fun routeForced(m: PaymentMethod) {
>     val unused: Unit = when (m) {       // присвоение → expression
>         is PaymentMethod.Card -> chargeCard(m.pan)
>         is PaymentMethod.PayPal -> chargePayPal(m.email)
>         PaymentMethod.ApplePay -> chargeApplePay()
>     }
> }
> ```
>
> **Когда применять:** всегда, когда `when` работает с sealed/enum. Не добавляйте `else` — это «оптом подавляет» exhaustive. В Kotlin 2.0 включайте `languageVersion = "2.0"` в build.gradle.kts для строгой проверки.
>
> **Подводные камни:** при добавлении нового подкласса CI/линтер не подскажет, какие конкретно `when` сломались — нужно полное rebuild. Поведение exhaustive **различается между языковыми версиями**: код, компилирующийся в 1.6, может падать в 2.0. При смешении sealed с обычными типами в `when` (`when (x)` где `x: Any`) exhaustive не работает — нужно сначала smart-cast.
>
> **Связанные вопросы:** [[Q1]] — что такое sealed; [[Q4]] — sealed interface; [[Q15]] — типичные ошибки (`when` как statement).

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


> [!mcq]
>
> **Вопрос:** Чем `sealed interface` (Kotlin 1.5+) отличается от `sealed class`, и когда выбирать первое?
>
> ---
>
> #### A) `sealed interface` позволяет одному классу реализовать **несколько** sealed interfaces одновременно (multiple inheritance иерархий), но не может хранить состояние; `sealed class` ограничен одной иерархией, но хранит общие поля — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `sealed interface` появился в Kotlin 1.5 (2021) как более гибкий вариант sealed-иерархий. Главное преимущество — **класс может одновременно входить в несколько sealed-семейств**, что невозможно с sealed class (Kotlin/JVM не поддерживает множественное наследование классов).
>
> Сравнение:
>
> | Аспект | `sealed class` | `sealed interface` |
> |---|---|---|
> | Состояние (поля) | Да (через constructor) | Нет (как любой interface) |
> | Реализация методов | Да (open methods, abstract methods) | Только default-методы |
> | Multiple inheritance | Один наследник = один sealed class | Класс может implement несколько sealed interface |
> | Когда выбирать | Нужны общие поля/конструктор | Нужна гибкость иерархии |
> | Java интероп | abstract class | interface (Java 8+ default methods) |
>
> **Пример:**
> ```kotlin
> // Множественное наследование иерархий — невозможно с sealed class
> sealed interface Error
> sealed interface NetworkError : Error
> sealed interface IOError : Error
> sealed interface RetryableError : Error
>
> // TimeoutError — одновременно NetworkError И RetryableError
> data class TimeoutError(val seconds: Int) : NetworkError, RetryableError, IOError
> data class DnsError(val host: String) : NetworkError
> data class FileNotFound(val path: String) : IOError
> data class PermissionDenied(val path: String) : IOError  // не retryable
>
> // Pattern matching по разным «осям»
> fun shouldRetry(e: Error): Boolean = when (e) {
>     is RetryableError -> true
>     else -> false
> }
>
> fun log(e: Error): String = when (e) {
>     is NetworkError -> "net: $e"
>     is IOError      -> "io: $e"
>     else            -> "other: $e"   // компилятор: Error может быть и тем и другим
> }
> ```
>
> **Когда применять:** **sealed interface** — когда у иерархии нет общих полей/конструкторов, или когда нужны пересекающиеся категории (`NetworkError ∩ RetryableError`). **sealed class** — когда есть общий конструктор/поля (общий timestamp у всех событий, общий traceId у всех команд), или когда нужно `abstract fun` с реализациями в подклассах через `super.method()`. JetBrains в Kotlin stdlib (`kotlin.time.Duration`) и `kotlinx.coroutines` используют sealed interfaces для гибкости.
>
> **Подводные камни:** При множественной реализации интерфейсов в `when` exhaustiveness считается только по **одной оси** — компилятор не знает «комбинаций пересечений». Если `Error` реализует и `NetworkError`, и `IOError`, то `when(e: Error) { is NetworkError -> ...; is IOError -> ... }` не exhaustive по строгому смыслу — `TimeoutError` подходит обеим веткам, первая выигрывает. По умолчанию sealed interface объявляется в одном модуле, как sealed class (правило одинаковое с 1.5+). Java 17 sealed interface — отдельная история, requires `permits`.
>
> **Связанные вопросы:** [[Q1]] — основы sealed; [[Q7]] — sealed class vs interface choice; [[Q8]] — ограничения 1.5+.
>
> ---
>
> #### B) `sealed interface` — это синтаксический сахар над `sealed class` с одним абстрактным методом, без иных отличий — ❌ Неверно
>
> **Что на самом деле:** это два разных механизма. `sealed interface` — это **интерфейс** (как любой Kotlin interface): без primary constructor, без полей с состоянием, может содержать default-методы. `sealed class` — абстрактный класс с конструктором и потенциальными полями. Они компилируются в разный байткод (interface vs abstract class) и имеют разную семантику для Java-интеропа.
>
> **Откуда путаница:** аналогия с Scala 3, где `enum`/`case class` действительно похожие конструкции. В Kotlin они различаются как class vs interface на JVM-уровне.
>
> **Если бы это было правдой:** не было бы multiple inheritance возможностей, и не было бы смысла вводить отдельный `sealed interface` keyword в Kotlin 1.5 — достаточно было бы оставить `sealed class`.
>
> ---
>
> #### C) `sealed interface` запрещён в Kotlin/JVM, доступен только в Kotlin/Native и Kotlin/JS — ❌ Неверно
>
> **Что на самом деле:** `sealed interface` доступен на **всех таргетах** Kotlin (JVM, Native, JS, Wasm) с версии 1.5. На JVM компилируется в interface с `PermittedSubclasses` атрибутом class file (Java 17+). На Native/JS — реализуется через метаданные Kotlin.
>
> **Откуда путаница:** sealed классы в Java появились только в Java 17, и были долгое время preview-фичей. Кажется, что Kotlin тоже долго ждал JVM-поддержку.
>
> **Если бы это было правдой:** Spring Boot, Ktor, kotlinx.serialization не могли бы использовать sealed interface — а они активно используют (`ContentTransformationException`, `KSerializer`, и т.п.).
>
> ---
>
> #### D) `sealed interface` создаётся через `interface sealed Error { ... }`, порядок keyword другой чем у `sealed class` — ❌ Неверно
>
> **Что на самом деле:** синтаксис идентичен: `sealed interface Error` и `sealed class Error` — `sealed` всегда первый модификатор перед keyword типа. Никаких особых правил порядка нет.
>
> **Откуда путаница:** Java 17 имеет похожий синтаксис, но именно `sealed interface Foo permits Bar, Baz`. Можно подумать, что у Kotlin тоже какие-то особые правила.
>
> **Если бы это было правдой:** все примеры в документации Kotlin использовали бы новый синтаксис — но они используют `sealed interface Foo`. Также IDE не позволяла бы такой код.

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


> [!mcq]
>
> **Вопрос:** Почему пользовательский `sealed class Result<T, E>` предпочтительнее использования встроенного `kotlin.Result<T>` для моделирования ошибок в доменном слое?
>
> ---
>
> #### A) `kotlin.Result<T>` запрещён к использованию в production — компилятор не даёт его компилировать вне stdlib — ❌ Неверно
>
> **Что на самом деле:** `kotlin.Result<T>` **компилируется** и используется (особенно в coroutines), но имеет несколько серьёзных ограничений: его нельзя использовать **как тип возвращаемого значения** (Kotlin warning `Result is not designed to be returned from functions`), он завязан на `Throwable`, и его конструкторы `success/failure` имеют особую обработку. Это не запрет, а design choice.
>
> **Откуда путаница:** компиляторное предупреждение про return type многие воспринимают как «вообще нельзя». Также `Result` помечен `@SinceKotlin` и имел experimental-статус долгое время.
>
> **Если бы это было правдой:** не было бы `runCatching { ... }` в kotlin stdlib, и `kotlinx.coroutines` не использовала бы `Result` внутри (но использует — `Continuation<T>.resumeWith(result: Result<T>)`).
>
> ---
>
> #### B) `sealed class Result<out T, out E>` позволяет типизировать **error** (не только `Throwable`), даёт exhaustive `when`, поддерживает map/flatMap/fold — это полноценный Either, а `kotlin.Result` фиксирует `E = Throwable` и не подходит для доменных ошибок — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `kotlin.Result<T>` концептуально — `Either<Throwable, T>`. Его дизайн:
> - Error type **зафиксирован** как `Throwable` — нельзя смоделировать `Result<User, UserNotFoundError>`, где `UserNotFoundError` — sealed class из домена, не исключение.
> - Не рекомендуется как return type (warning) — он задумывался для callback-маппинга.
> - Нет `map`/`flatMap`, есть только `getOrElse`, `fold`, `onSuccess`, `onFailure`.
>
> Кастомный `sealed class Result<out T, out E>` решает обе проблемы.
>
> **Пример:**
> ```kotlin
> sealed class Result<out T, out E> {
>     data class Success<T>(val value: T) : Result<T, Nothing>()
>     data class Failure<E>(val error: E) : Result<Nothing, E>()
>
>     inline fun <R> map(f: (T) -> R): Result<R, E> = when (this) {
>         is Success -> Success(f(value))
>         is Failure -> this
>     }
>
>     inline fun <R> flatMap(f: (T) -> Result<R, @UnsafeVariance E>): Result<R, E> = when (this) {
>         is Success -> f(value)
>         is Failure -> this
>     }
>
>     inline fun <R> fold(onSuccess: (T) -> R, onFailure: (E) -> R): R = when (this) {
>         is Success -> onSuccess(value)
>         is Failure -> onFailure(error)
>     }
> }
>
> // Доменные ошибки как sealed — не Throwable
> sealed class UserError {
>     data class NotFound(val id: String) : UserError()
>     data class Banned(val until: Instant) : UserError()
>     data object DatabaseUnavailable : UserError()
> }
>
> fun fetchUser(id: String): Result<User, UserError> =
>     userRepo.findById(id)?.let { Result.Success(it) }
>         ?: Result.Failure(UserError.NotFound(id))
>
> // Композиция через flatMap
> val orderResult: Result<Order, UserError> =
>     fetchUser("u1")
>         .map { it.copy(name = it.name.trim()) }
>         .flatMap { createOrder(it) }    // <-- chain failures прозрачно
> ```
>
> **Когда применять:** Domain-Driven Design — ошибки как **часть домена**, а не исключения. Arrow library (`Either`, `Validated`) — идиоматический Kotlin functional подход. Hexagonal/clean architecture, где доменный слой не должен зависеть от `Throwable`. Spring WebFlux / Ktor handlers — типизировать API errors через sealed instead of exceptions.
>
> **Подводные камни:** `@UnsafeVariance` в `flatMap` — необходимое зло для variance (compiler не может вывести правильно). Custom `Result` теряет интеграцию с `runCatching` и continuation API — нужно делать `runCatching { ... }.fold(::Success) { Failure(toDomainError(it)) }` мост. Если используете и kotlin.Result, и custom Result — нужен явный naming (`AppResult` vs `KResult`), чтобы избежать import-конфликтов. Arrow `Either<L, R>` функционально эквивалентен — рассмотрите его перед своей реализацией.
>
> **Связанные вопросы:** [[Q1]] — основы sealed; [[Q6]] — ADT и sum types; [[Q12]] — type-safety против stringly-typed.
>
> ---
>
> #### C) Кастомный `sealed Result` использует Java reflection для exhaustive проверки — это медленно, поэтому `kotlin.Result` всегда предпочтительнее — ❌ Неверно
>
> **Что на самом деле:** exhaustive `when` для любого sealed — это **compile-time** проверка, без рантайм-стоимости. Sealed class в рантайме — обычная иерархия с быстрыми `instanceof`-проверками (одна-две инструкции байткода). Реflection здесь не задействован.
>
> **Откуда путаница:** kotlin-reflect действительно нужен для `KClass::sealedSubclasses`, и появляется заблуждение, что любая sealed-работа требует reflect.
>
> **Если бы это было правдой:** sealed были бы непригодны для hot-path (per-request decoding в Ktor, frame-by-frame UI rendering в Compose), а они там используются массово без перформанс-проблем.
>
> ---
>
> #### D) `kotlin.Result<T>` поддерживает covariant error type через `Result<T, in E>`, поэтому пользовательский sealed Result не нужен — ❌ Неверно
>
> **Что на самом деле:** `kotlin.Result<T>` — **однопараметрический**, и его error всегда `Throwable`. Никакого `<T, in E>` API не существует. Подпись `public value class Result<out T>` фиксирована и не имеет E-параметра.
>
> **Откуда путаница:** Either-аналоги (Arrow `Either<L, R>`, Scala) имеют двухпараметрические сигнатуры — кажется, что и `kotlin.Result` мог бы.
>
> **Если бы это было правдой:** не было бы нужды в Arrow.Either, не было бы DDD-подхода с типизированными ошибками — стандарт хватало бы. Но в реальности middle-крупные проекты массово делают свои Result/Either-обёртки.

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


> [!mcq]
>
> **Вопрос:** Что такое **Algebraic Data Types (ADT)** в типизированных языках, и как именно `sealed class` + `data class` Kotlin их реализуют?
>
> ---
>
> #### A) ADT — это объекты-singletons, аналог enum констант; sealed class — просто пересборка enum под новым именем — ❌ Неверно
>
> **Что на самом деле:** ADT — гораздо шире, чем перечисления. ADT — типы, построенные из двух базовых операций: **sum** («или») и **product** («и»). Enum — частный случай sum-типа без полей. ADT включает рекурсивные типы (`List = Nil | Cons(head, tail)`), параметризованные типы (`Option<T> = None | Some(T)`), вложенные структуры.
>
> **Откуда путаница:** в простых примерах ADT (`Color = Red | Green | Blue`) действительно похож на enum. Концептуальная разница теряется.
>
> **Если бы это было правдой:** не было бы paradigm-разницы между Haskell/Scala/Rust ADT и Java/C# enums. Также не было бы концепции «изоморфизма типов» из теории категорий, на которой построена Lean/Idris/Coq.
>
> ---
>
> #### B) ADT — это **только sum types** (sealed class); product types (data class) к ADT не относятся — ❌ Неверно
>
> **Что на самом деле:** ADT = **sum + product** в комбинации. Data class — это product type (записывает несколько полей вместе), sealed class — sum type (одно из нескольких). Полноценная ADT использует оба: `sealed class Tree { object Leaf : Tree(); data class Node(left: Tree, value: Int, right: Tree) : Tree() }` — sum (Leaf vs Node) с product внутри Node.
>
> **Откуда путаница:** в Kotlin keyword `sealed` ассоциируется с ADT, а data class — «просто DTO». Реально это две стороны одной концепции.
>
> **Если бы это было правдой:** ADT в Haskell бы существовал без records — но `data Person = Person { name :: String, age :: Int }` это product type, и он часть ADT-системы.
>
> ---
>
> #### C) ADT — типы, построенные комбинацией **sum** («или один из N»; реализуется через `sealed class`) и **product** («все поля одновременно»; реализуется через `data class`); вместе они дают exhaustive pattern matching и compile-time проверку доменных инвариантов — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> **Sum (или):** значение принадлежит ровно одному из N подтипов. В теории категорий — coproduct, обозначается `A + B`. В Kotlin — `sealed class JsonValue { object Null; data class Num(...); data class Str(...); ... }`.
>
> **Product (и):** значение содержит несколько полей одновременно. В теории — product, `A × B`. В Kotlin — `data class Person(val name: String, val age: Int)`.
>
> Комбинация даёт богатые типы:
> - `Option<T> = None | Some(T)` — sum, где один из вариантов хранит product (T в составе Some).
> - `List<T> = Nil | Cons(T, List<T>)` — рекурсивная sum с product.
> - `Tree<T> = Leaf | Node(Tree<T>, T, Tree<T>)` — sum, продукт хранит две рекурсивные ветки.
>
> **Пример:**
> ```kotlin
> // ADT для JSON: sum с продуктами внутри
> sealed class JsonValue {
>     data object Null : JsonValue()                                          // 0-arity product
>     data class Bool(val value: Boolean) : JsonValue()                       // 1-arity product
>     data class Num(val value: Double) : JsonValue()
>     data class Str(val value: String) : JsonValue()
>     data class Arr(val items: List<JsonValue>) : JsonValue()                // рекурсия
>     data class Obj(val fields: Map<String, JsonValue>) : JsonValue()        // product внутри
> }
>
> // Exhaustive pattern matching обеспечивает полноту обработки
> fun render(json: JsonValue): String = when (json) {
>     JsonValue.Null   -> "null"
>     is JsonValue.Bool -> json.value.toString()
>     is JsonValue.Num  -> json.value.toString()
>     is JsonValue.Str  -> "\"${json.value}\""
>     is JsonValue.Arr  -> json.items.joinToString(",", "[", "]", transform = ::render)
>     is JsonValue.Obj  -> json.fields.entries.joinToString(",", "{", "}") {
>         "\"${it.key}\":${render(it.value)}"
>     }
> }
>
> // Доменные инварианты на уровне типов
> sealed class PaymentOrder {
>     // "не может быть и draft, и confirmed одновременно" — закодировано в типе
>     data class Draft(val items: List<Item>) : PaymentOrder()
>     data class Confirmed(val items: List<Item>, val total: Money) : PaymentOrder()
>     data class Paid(val items: List<Item>, val total: Money, val tx: TxId) : PaymentOrder()
> }
> ```
>
> **Когда применять:** доменное моделирование в DDD — ADT кодируют бизнес-правила в типах («заказ либо draft, либо confirmed, либо paid»). Парсеры AST (compiler IR, Kotlin Tree-sitter, kotlinx-ast). Event sourcing — события как ADT (UserCreated | UserDeleted | UserUpdated). State machines (см. Q13). Arrow functional library построена на ADT.
>
> **Подводные камни:** ADT с большим числом вариантов (10+) становятся неудобными для `when` — рассмотрите Visitor pattern или sealed hierarchy nesting. Recursive ADT в Kotlin/JVM имеют накладные расходы на boxing — для perf-critical путей рассмотрите arrays of tags. Сериализация ADT нетривиальна — нужен дискриминатор (см. Q10).
>
> **Связанные вопросы:** [[Q1]] — основы sealed; [[Q5]] — Result как ADT; [[Q13]] — State machine как ADT.
>
> ---
>
> #### D) ADT — это специальная JVM-фича Java 17, доступная Kotlin только через interop с Java sealed — ❌ Неверно
>
> **Что на самом деле:** ADT — концепция теории типов, существовавшая задолго до JVM. Haskell имел ADT в 1990, ML — в 1970-х. Kotlin реализует ADT на собственном уровне через sealed (с 1.0/1.5+), без зависимости от Java 17 sealed. Java 17 sealed — это **аналогичная** фича в Java, но не источник Kotlin ADT.
>
> **Откуда путаница:** Java 17 popularised sealed для широкой Java-публики, и кажется, что Kotlin его «портировал».
>
> **Если бы это было правдой:** Kotlin 1.0–1.5 (2016-2021), когда Java 17 ещё не вышел, не поддерживал бы sealed — но Kotlin 1.0 уже имел `sealed class`. Также Kotlin/Native и Kotlin/JS, где нет JVM, не могли бы использовать sealed — но используют.

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


> [!mcq]
>
> **Вопрос:** При проектировании ограниченной иерархии в Kotlin: когда выбрать `sealed class`, а когда `sealed interface`?
>
> ---
>
> #### A) Всегда `sealed class` — interface не позволяет default-методы, поэтому код будет дублироваться в каждой реализации — ❌ Неверно
>
> **Что на самом деле:** Kotlin interface (как Java 8+ interface) **поддерживает default-методы** через obычные `fun`-методы с body: `interface Foo { fun method() = "default" }`. Также можно объявлять properties с custom getter (без backing field). Дублирование кода не неизбежно в sealed interface.
>
> **Откуда путаница:** Java < 8 не поддерживал default-методы, и старая привычка сидит глубоко. Также в Kotlin interface action items имеют ограничения по сравнению с class (no constructor, no init), что воспринимается как невозможность переиспользовать код.
>
> **Если бы это было правдой:** sealed interface не имел бы практического применения, и Kotlin 1.5 не добавил бы эту фичу.
>
> ---
>
> #### B) Всегда `sealed interface` — interface быстрее на JVM из-за invokeinterface оптимизаций — ❌ Неверно
>
> **Что на самом деле:** на современном HotSpot JIT разница между `invokevirtual` (class) и `invokeinterface` (interface) **исчезающе мала** — JIT часто инлайнит обе. Реальная разница для perf-critical только в monomorphic call sites (1 implementation), где class может быть чуть быстрее на cold path. В обычном app коде разница не измерима.
>
> **Откуда путаница:** до Java 7 invokeinterface действительно был дороже invokevirtual из-за iTable lookup. После Java 8+ — оптимизирован.
>
> **Если бы это было правдой:** все Spring-проекты на абстрактных class-ах нужно было бы рефакторить в interfaces — но никто такого не делает в реальности.
>
> ---
>
> #### C) Зависит от языка — для KMP всегда sealed interface, для JVM-only всегда sealed class — ❌ Неверно
>
> **Что на самом деле:** оба механизма доступны на всех Kotlin-таргетах с одинаковой семантикой. KMP-совместимость не диктует выбор — на JVM, Native, JS, Wasm sealed class и sealed interface работают одинаково.
>
> **Откуда путаница:** некоторые JVM-фичи (reflection, ServiceLoader) недоступны или ограничены в KMP, и кажется, что и sealed может иметь такие ограничения.
>
> **Если бы это было правдой:** в KMP-проектах нельзя было бы использовать sealed class в expect/actual — но можно, это стандартная KMP-практика.
>
> ---
>
> #### D) **`sealed class`** — когда нужны **общие поля/методы** в primary constructor или общая реализация в подклассах. **`sealed interface`** — когда нет общего состояния, или когда подклассы должны принадлежать **нескольким** иерархиям (multiple inheritance) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Решение определяется тремя осями:
>
> 1. **Нужны ли общие поля?** Да → `sealed class` (`sealed class Animal(val name: String, val age: Int)`). Нет → `sealed interface`.
> 2. **Нужно ли множественное наследование иерархий?** Да → только `sealed interface` (JVM не поддерживает multiple class inheritance).
> 3. **Library для других разработчиков?** Предпочитайте `sealed interface` — он более гибок для будущих расширений и легче эволюционирует.
>
> **Пример:**
> ```kotlin
> // sealed class — общий конструктор + поля
> sealed class DomainEvent(val timestamp: Instant, val traceId: String) {
>     class OrderCreated(t: Instant, tid: String, val orderId: String) : DomainEvent(t, tid)
>     class OrderShipped(t: Instant, tid: String, val orderId: String, val carrier: String) : DomainEvent(t, tid)
> }
> // Все DomainEvent имеют timestamp и traceId без дублирования
>
> // sealed interface — multiple inheritance иерархий
> sealed interface Animal { val name: String }
> sealed interface Mammal : Animal { val furColor: String }
> sealed interface Aquatic : Animal { val waterType: WaterType }
>
> data class Dolphin(override val name: String, override val furColor: String, override val waterType: WaterType)
>     : Mammal, Aquatic  // <-- ОБА: млекопитающее И водное
>
> data class Shark(override val name: String, override val waterType: WaterType) : Aquatic
> data class Cat(override val name: String, override val furColor: String) : Mammal
>
> // Можно сделать pattern matching по любой оси
> fun describeMammal(m: Mammal) = "${m.name}, fur=${m.furColor}"
> fun describeAquatic(a: Aquatic) = "${a.name}, water=${a.waterType}"
> ```
>
> **Когда применять:** **sealed class** — domain events с общим metadata, state machine с общими transitions-методами, UI states с общим toString. **sealed interface** — Markers (`Identifiable`, `Trackable`, `Cacheable`), cross-cutting concerns, библиотечные API (легче эволюционировать без breaking changes), DSL building blocks (Compose использует interface for `Modifier`).
>
> **Подводные камни:** **sealed interface** имеет лимит на supertypes — класс не может имплементировать одновременно sealed interface из **разных модулей** (правило permits ограничивает scope модулем). Если используете both — choose one consistently в проекте, иначе путаница в navigation. Иногда хочется и общие поля, и multiple inheritance — тогда используйте sealed interface + abstract base class отдельно.
>
> **Связанные вопросы:** [[Q1]] — основы sealed; [[Q4]] — sealed interface; [[Q8]] — ограничения 1.5+.

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


> [!mcq]
>
> **Вопрос:** Какие правила scope для наследников `sealed class` действуют в Kotlin 1.5+?
>
> ---
>
> #### A) Все прямые наследники должны находиться в **том же модуле** (compilation unit Gradle / Maven artifact), но могут быть в разных файлах и пакетах — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Kotlin 1.5 (май 2021) ослабил scope-правило для sealed:
>
> - **До 1.5:** все наследники должны быть в том **же файле** (`.kt`).
> - **С 1.5+:** все наследники должны быть в том **же модуле** (compilation unit, обычно Gradle subproject или Maven artifact), но могут быть в разных файлах и пакетах **внутри этого модуля**.
> - **Скоуп per backend:** на JVM это один Gradle `compileKotlin` task; на JS/Native — аналогично один compilation unit.
>
> Также в 1.5 появился `sealed interface` с тем же scope-правилом.
>
> Дополнительные ограничения:
> - `sealed class` не может быть `local` (внутри функции) или `inner` (nested non-static). Только top-level или nested в outer class.
> - Конструктор sealed class implicit `protected` (нельзя сделать `public` или `private`).
> - Сам sealed class — `abstract` (нельзя инстанцировать напрямую).
> - Прямые наследники могут быть открытыми (`open`), final, `data class`, `data object`, `object`, или другим `sealed class/interface`.
>
> **Пример:**
> ```kotlin
> // module: domain-payments (один Gradle subproject)
>
> // file: domain/Payment.kt
> package com.app.domain
> sealed class Payment {
>     abstract val amount: Money
> }
>
> // file: domain/methods/CardPayment.kt — другой пакет, тот же модуль
> package com.app.domain.methods
> import com.app.domain.Payment
> data class CardPayment(val pan: String, override val amount: Money) : Payment()
>
> // file: domain/methods/CryptoPayment.kt
> package com.app.domain.methods
> data class CryptoPayment(val wallet: String, override val amount: Money) : Payment()
>
> // module: api (другой Gradle subproject)
> // file: api/CustomPayment.kt
> package com.app.api
> // ОШИБКА компиляции: cannot extend Payment from different module
> // data class CustomPayment(...) : Payment()
> ```
>
> ```kotlin
> // local sealed запрещён
> fun foo() {
>     sealed class Local // ОШИБКА: Modifier 'sealed' is not applicable to 'local class'
> }
>
> // inner запрещён
> class Outer {
>     sealed inner class InnerSealed // ОШИБКА
>     // но nested (без inner) — OK
>     sealed class Nested
> }
> ```
>
> **Когда применять:** в multi-module проектах размещайте sealed hierarchy целиком в одном модуле (например, `domain` или `api`-shared). Если нужен расширяемый API — используйте обычный `interface` без sealed, либо вынесите sealed внутрь модуля библиотеки и опубликуйте без возможности extension (это и есть цель — закрытое множество). Spring projects: domain layer обычно `internal` модуль с sealed hierarchy.
>
> **Подводные камни:** в Gradle multi-module если один модуль зависит от другого и в зависимом модуле объявляется `class Foo : Payment()` — компилятор отдаст ошибку с не самым очевидным сообщением (`cannot inherit sealed type from different module`). Inline функции с sealed `when` могут компилироваться в неожиданное место — exhaustive проверяется в точке вызова, а не объявления. Для тестов sealed: тестовые подклассы должны лежать в **том же module**, что и sealed — обычно `src/test/kotlin` это OK (тесты часть того же compileTestKotlin task).
>
> **Связанные вопросы:** [[Q1]] — основы sealed; [[Q4]] — sealed interface scope такой же; [[Q15]] — типичные ошибки.
>
> ---
>
> #### B) Все наследники должны находиться в **том же файле**, как и до Kotlin 1.5 — изменений в 1.5 не было — ❌ Неверно
>
> **Что на самом деле:** Kotlin 1.5 расширил scope с file → module — это release-note item. Старое правило (file) применялось только до 1.5. Сейчас в KEEP-226 это явная фича 1.5.
>
> **Откуда путаница:** многие проекты до сих пор на устаревших туториалах с 1.4-эпохи, где file-scope ещё актуален.
>
> **Если бы это было правдой:** Kotlin 1.5/1.6/1.7/1.8/1.9/2.0 release notes лгали бы, и многие современные проекты (Ktor, Spring Modulith, Compose) не могли бы организовать domain layer как они организуют — в один модуль с несколькими файлами.
>
> ---
>
> #### C) С Kotlin 1.5+ наследники могут быть **в любом модуле**, если они помечены `permits-аннотацией — ❌ Неверно
>
> **Что на самом деле:** Kotlin **не имеет** аннотации `permits` (это Java 17 синтаксис). Правило в Kotlin строго фиксировано — module-scope, без возможности расширения. Это design choice: sealed закрыты по определению, ослабление = open class.
>
> **Откуда путаница:** Java 17 sealed взаимодействуют через `permits` keyword, можно представить, что Kotlin тоже это перенял.
>
> **Если бы это было правдой:** появилась бы лазейка для расширения sealed snaружи — что нарушало бы инвариант exhaustiveness. Компилятор не мог бы быть уверен, что видит все варианты при `when`.
>
> ---
>
> #### D) Sealed class в 1.5+ должен быть `open` (или `abstract`); `sealed` отдельно не подразумевает abstract — ❌ Неверно
>
> **Что на самом деле:** `sealed` **подразумевает** `abstract` — нельзя создать экземпляр sealed class напрямую. Не нужно писать `sealed abstract class Foo` — `sealed class Foo` достаточно. Внутри sealed класса можно иметь abstract или concrete методы.
>
> **Откуда путаница:** `open` — модификатор, разрешающий extension, и кажется похожим на sealed. Также Java `sealed` требует `abstract` явно в некоторых случаях.
>
> **Если бы это было правдой:** документация Kotlin изобиловала бы `sealed abstract class` примерами — но используется `sealed class`. Также появлялась бы возможность `sealed final class`, что бессмысленно.

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


> [!mcq]
>
> **Вопрос:** Как `sealed class` используется в Kotlin DSL для type-safe построения структур (например, валидация или query-builder)?
>
> ---
>
> #### A) Sealed class в DSL заменяет builder pattern — каждый шаг билдера выбирает один из вариантов sealed, и compiler гарантирует exhaustive обработку — ❌ Неверно
>
> **Что на самом деле:** sealed class в DSL обычно играет роль **результата** или **узла AST**, а не «шага билдера». Builder pattern в DSL реализуется через scoped functions (`apply`, `with`, `run`) и `@DslMarker` на receiver-классах. Sealed появляется как outcome (`Validation.Valid` / `Invalid`) или как node типы (`HtmlNode.Tag` / `Text`), но не заменяет builder.
>
> **Откуда путаница:** оба паттерна про «постройку чего-то типизированного» — кажется, что sealed может вытеснить builder.
>
> **Если бы это было правдой:** в Kotlin DSL не нужны были бы `@DslMarker`-аннотации (они для receiver scope, не для sealed) и type-safe builders в html-dsl/Kotlinx Coroutines builders. Но они существуют.
>
> ---
>
> #### B) `sealed class` используется как **type-safe outcome** или **AST-узел** DSL: builder собирает вход через scoped functions, а результирует sealed-вариантом (`Valid` / `Invalid`); это даёт exhaustive обработку на стороне вызова и невозможность создания невалидного результата — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Sealed class в DSL играет одну из ролей:
> 1. **Outcome / Result.** DSL принимает декларативное описание и возвращает sealed (`Validation`, `ParseResult`, `BuildResult`). Клиент обязан обработать оба варианта.
> 2. **AST nodes.** Каждый sealed-вариант — узел дерева (HtmlNode, JsonNode, QueryNode). DSL строит дерево, sealed обеспечивает exhaustive traversal.
> 3. **Marker для state.** Sealed-тип отмечает фазу DSL (например, `Pending` -> `Built`), не даёт смешивать.
>
> **Пример (Validation DSL):**
> ```kotlin
> sealed class Validation<out T> {
>     data class Valid<T>(val value: T) : Validation<T>()
>     data class Invalid(val errors: List<String>) : Validation<Nothing>()
> }
>
> @DslMarker
> annotation class ValidationDsl
>
> @ValidationDsl
> class ValidationBuilder<T>(private val target: T) {
>     private val errors = mutableListOf<String>()
>
>     fun require(condition: Boolean, message: () -> String) {
>         if (!condition) errors += message()
>     }
>
>     fun build(): Validation<T> =
>         if (errors.isEmpty()) Validation.Valid(target)
>         else Validation.Invalid(errors)
> }
>
> fun <T> validate(target: T, block: ValidationBuilder<T>.() -> Unit): Validation<T> =
>     ValidationBuilder(target).apply(block).build()
>
> // Использование
> val result = validate(user) {
>     require(target.age >= 18) { "Must be adult, was ${target.age}" }
>     require(target.email.contains("@")) { "Invalid email: ${target.email}" }
> }
>
> // Клиент обязан обработать оба варианта (exhaustive)
> when (result) {
>     is Validation.Valid    -> save(result.value)
>     is Validation.Invalid  -> showErrors(result.errors)
> }
> ```
>
> **Пример (AST nodes — HTML DSL):**
> ```kotlin
> sealed class HtmlNode {
>     data class Tag(val name: String, val attrs: Map<String, String>, val children: List<HtmlNode>) : HtmlNode()
>     data class Text(val content: String) : HtmlNode()
> }
>
> fun render(node: HtmlNode): String = when (node) {
>     is HtmlNode.Tag  -> "<${node.name}${node.attrs.entries.joinToString { " ${it.key}=\"${it.value}\"" }}>" +
>                        node.children.joinToString("") { render(it) } + "</${node.name}>"
>     is HtmlNode.Text -> node.content
> }
> ```
>
> **Когда применять:** Validation DSL, query builders (Exposed SQL DSL использует похожий подход), state machines с типизированными переходами, parser combinators (parsing -> sealed AST), Gradle build scripts (configuration -> sealed action types).
>
> **Подводные камни:** `@DslMarker` нужен, чтобы предотвратить случайный доступ к outer DSL scope внутри nested блоков — без него код компилируется, но семантика может быть неверной. При большом sealed AST (>20 вариантов) `when`-блоки становятся монстрами — рассмотрите Visitor pattern. Для DSL с мутируемым состоянием обертывайте builder, а не sealed напрямую — sealed-варианты должны быть immutable.
>
> **Связанные вопросы:** [[Q1]] — основы sealed; [[Q6]] — ADT как теоретическая основа; [[Q11]] — UiState DSL.
>
> ---
>
> #### C) Sealed class в DSL обязателен — без него DSL не может быть type-safe — ❌ Неверно
>
> **Что на самом деле:** Type-safe DSL в Kotlin обычно построены на **scoped functions с extension receivers + @DslMarker** — без sealed. Sealed появляется опционально, как тип результата или узла AST. Многие популярные DSL (kotlinx.html, Gradle Kotlin DSL, anko до его deprecation, ktor routing) **не используют** sealed в основе.
>
> **Откуда путаница:** sealed и DSL — обе «продвинутые Kotlin» темы, и можно подумать, что они взаимно необходимы.
>
> **Если бы это было правдой:** kotlinx.html был бы построен на sealed (он не построен — там обычная иерархия классов HTML elements), и Gradle DSL тоже (там receivers).
>
> ---
>
> #### D) `sealed class` в DSL заменяет `@DslMarker` — sealed-варианты автоматически изолируют scope — ❌ Неверно
>
> **Что на самом деле:** `@DslMarker` — отдельная аннотация-meta для предотвращения «двойного receiver» в nested DSL (когда внутри `html { body { ... } }` случайный вызов `body` снаружи не должен резолвиться). Sealed class **не имеет такой семантики**. Они решают разные задачи: sealed — закрытость иерархии, @DslMarker — scope isolation.
>
> **Откуда путаница:** обе фичи «продвинутый Kotlin DSL» — и можно подумать, что одна замещает другую.
>
> **Если бы это было правдой:** документация Kotlin DSL не упоминала бы `@DslMarker` отдельно — но это явная и независимая фича.

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


> [!mcq]
>
> **Вопрос:** Что нужно для корректной (де)сериализации `sealed class` через `kotlinx.serialization`, и как работает механизм дискриминатора типа?
>
> ---
>
> #### A) `kotlinx.serialization` автоматически распознаёт sealed без дополнительных аннотаций — достаточно `@Serializable` на sealed-родителе — ❌ Неверно
>
> **Что на самом деле:** нужно аннотировать **и родителя, и каждый подкласс** через `@Serializable`. Без этого compiler plugin не сгенерирует сериализаторы для подклассов, и runtime выбросит `SerializationException: Serializer for class ... is not found`. Дополнительно `@SerialName` рекомендуется для стабильности дискриминатора (FQN классов меняются при рефакторинге, явное имя — не меняется).
>
> **Откуда путаница:** kotlinx.serialization compiler plugin делает много автоматически (например, `@Serializable` на data class), и кажется, что для sealed тоже всё magic.
>
> **Если бы это было правдой:** не было бы знаменитой ошибки `Serializer for ... is not found` в Kotlin-стек оверфлоу — самой частой проблемы при сериализации sealed.
>
> ---
>
> #### B) Нужно сериализовать вручную через `JsonPrimitive` и `JsonObject`, kotlinx.serialization не поддерживает sealed напрямую — ❌ Неверно
>
> **Что на самом деле:** kotlinx.serialization **полностью поддерживает** sealed hierarchy "из коробки" с версии 0.20+. Sealed-сериализатор автоматически создаётся compiler-плагином и добавляет дискриминатор типа (по умолчанию поле `"type"`). Ручная сериализация — крайняя мера для custom JSON schemas (например, OpenAPI с oneOf без дискриминатора).
>
> **Откуда путаница:** в Java-мире (Jackson) sealed/polymorphism требует ручной конфигурации `@JsonTypeInfo`/`@JsonSubTypes`. Kotlin developers переносят этот опыт.
>
> **Если бы это было правдой:** ktor-server, который сейчас активно использует sealed для request/response типов, не работал бы — а он работает.
>
> ---
>
> #### C) Сериализация: на родителе `@Serializable`, на каждом подклассе `@Serializable @SerialName("tag")`. По умолчанию kotlinx добавляет поле `"type": "tag"` в JSON; дискриминатор настраивается через `Json { classDiscriminator = "_type" }` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `kotlinx.serialization` для sealed работает так:
> 1. Compiler plugin генерирует `KSerializer<Sealed>` который при сериализации добавляет ключ `type` (по умолчанию) со значением `@SerialName` подкласса.
> 2. При десериализации читает дискриминатор, выбирает соответствующий подкласс сериализатор.
> 3. Если `@SerialName` не указан — используется fully-qualified имя класса (хрупко, FQN меняется при refactor → breaking change для persisted данных).
> 4. Дискриминатор можно поменять через `Json { classDiscriminator = "_type" }` или использовать `JsonClassDiscriminator` per-class.
>
> **Пример:**
> ```kotlin
> import kotlinx.serialization.*
> import kotlinx.serialization.json.*
>
> @Serializable
> sealed class Event {
>     @Serializable
>     @SerialName("user_created")
>     data class UserCreated(val userId: String, val name: String) : Event()
>
>     @Serializable
>     @SerialName("user_deleted")
>     data class UserDeleted(val userId: String, val reason: String) : Event()
>
>     @Serializable
>     @SerialName("error")
>     data class Error(val code: Int, val message: String) : Event()
> }
>
> val json = Json { classDiscriminator = "_type"; prettyPrint = true }
>
> val event: Event = Event.UserCreated("u-1", "Alice")
> val s = json.encodeToString(event)
> // {
> //   "_type": "user_created",
> //   "userId": "u-1",
> //   "name": "Alice"
> // }
>
> val restored: Event = json.decodeFromString(s)
> // exhaustive when работает на restored
> val msg = when (restored) {
>     is Event.UserCreated -> "Created: ${restored.name}"
>     is Event.UserDeleted -> "Deleted: ${restored.userId} (${restored.reason})"
>     is Event.Error       -> "Error ${restored.code}"
> }
> ```
>
> **Когда применять:** event sourcing — событий хранятся в Kafka/PostgreSQL как JSON с дискриминатором. REST API с polymorphic responses (Stripe Events API, GitHub Webhooks). WebSocket protocols, где разные типы message в одном канале (Discord gateway, Slack RTM). Spring Modulith application events с JSON storage. В Discord Cassandra/ScyllaDB hot-path события сериализуются sealed.
>
> **Подводные камни:** при изменении `@SerialName` старые сериализованные данные становятся unreadable — поэтому имя дискриминатора фиксируйте раз и навсегда (как enum constant в БД). Дискриминатор-поле должно быть **уникальным** в JSON — если payload подкласса содержит свой `type`, будет конфликт; используйте custom `classDiscriminator`. Generics в sealed (например, `Result<T>`) требуют **дополнительной** конфигурации — `Json { ignoreUnknownKeys = true }` и явная типизация при decode. Polymorphic deserialization может быть медленнее моноклассической — для hot-path рассмотрите avro/protobuf.
>
> **Связанные вопросы:** [[Q1]] — основы sealed; [[Q5]] — Result pattern; [[Q14]] — variance в sealed.
>
> ---
>
> #### D) Нужно использовать `@JsonTypeInfo` и `@JsonSubTypes` (как в Jackson) — kotlinx.serialization копирует Jackson API — ❌ Неверно
>
> **Что на самом деле:** `kotlinx.serialization` — отдельная библиотека с собственным API. `@JsonTypeInfo`/`@JsonSubTypes` — это **Jackson** аннотации, и они не работают с kotlinx. Аналог в kotlinx — `@SerialName` + автоматический discovery через sealed.
>
> **Откуда путаница:** долгое время Jackson был стандартом для JVM, и developers по инерции ищут его аннотации в любой JVM-библиотеке.
>
> **Если бы это было правдой:** kotlinx.serialization не работал бы на Kotlin/Native (где Jackson нет), но он работает там — это KMP-библиотека.

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


> [!mcq]
>
> **Вопрос:** Почему `sealed class UiState` предпочтительнее «плоской» модели `data class UiState(loading, data, error)` для UI в Android/Compose?
>
> ---
>
> #### A) Sealed UiState быстрее на JIT из-за меньшего количества полей в каждом подклассе — ❌ Неверно
>
> **Что на самом деле:** перформанс не главная причина. Разница в скорости (если она есть) — наносекунды на UI thread, незаметные. Реальная причина — **корректность типов и невозможность невалидных комбинаций** на этапе компиляции.
>
> **Откуда путаница:** «sealed = меньше полей в варианте» звучит как perf-оптимизация.
>
> **Если бы это было правдой:** замеры BenchmarkRule показывали бы существенную разницу — но они не показывают (gold reference: Jetpack Compose benchmarks).
>
> ---
>
> #### B) Sealed UiState нужен для DataBinding XML — без него LiveData не работает в Android — ❌ Неверно
>
> **Что на самом деле:** LiveData/StateFlow работают с любым типом, включая обычный data class. DataBinding XML тоже не требует sealed. Главная причина — **type-safety на стороне Composable / Fragment**, exhaustive `when` при рендере.
>
> **Откуда путаница:** AAC components появились примерно в одно время с популяризацией sealed UI state в Android-сообществе.
>
> **Если бы это было правдой:** до появления sealed (Kotlin 1.0) Android не имел бы реактивного UI — но имел.
>
> ---
>
> #### C) Sealed UiState обязателен в Jetpack Compose — без него рекомпозиция не работает — ❌ Неверно
>
> **Что на самом деле:** Compose работает с любыми observable state holders. Recomposition triggered by `@Stable`/`@Immutable` снепшотами или `MutableState`. Sealed — это **паттерн моделирования**, не Compose-требование.
>
> **Откуда путаница:** примеры в Compose docs часто используют sealed для UiState, и кажется, что это требование фреймворка.
>
> **Если бы это было правдой:** Compose не работал бы с List<Item> или Map<K,V> напрямую — но работает.
>
> ---
>
> #### D) Sealed UiState **закодирует mutually exclusive состояния** на уровне типов: «loading И data одновременно» — невыразимо. Плоская data class с nullable полями допускает невалидные комбинации (`loading=true && data!=null && error!=null`), которые надо проверять рантайм-логикой и могут привести к UI inconsistency — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Антипаттерн «плоской» UiState:
> ```kotlin
> // BAD: невалидные комбинации возможны
> data class UiState(
>     val isLoading: Boolean = false,
>     val data: List<Order>? = null,
>     val error: String? = null,
>     val isEmpty: Boolean = false,
> )
> // 2^4 = 16 комбинаций, из них валидны только 4
> // (loading=true, прочее=null/false) — Loading
> // (data!=null) — Success
> // (error!=null) — Error
> // (isEmpty=true) — Empty
> // Остальные 12 — баги, например (loading=true, data=[...], error="oops")
> ```
>
> Sealed-подход делает невалидные состояния **невыразимыми**:
> ```kotlin
> sealed class UiState<out T> {
>     data object Loading : UiState<Nothing>()
>     data object Empty : UiState<Nothing>()
>     data class Success<T>(val data: T) : UiState<T>()
>     data class Error(val message: String, val retryable: Boolean = true) : UiState<Nothing>()
> }
>
> class OrdersViewModel : ViewModel() {
>     private val _state = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
>     val state: StateFlow<UiState<List<Order>>> = _state.asStateFlow()
>
>     fun load() = viewModelScope.launch {
>         _state.value = UiState.Loading
>         _state.value = try {
>             val orders = repo.getOrders()
>             if (orders.isEmpty()) UiState.Empty else UiState.Success(orders)
>         } catch (e: IOException) {
>             UiState.Error(e.message ?: "Network error", retryable = true)
>         }
>     }
> }
>
> @Composable
> fun OrdersScreen(viewModel: OrdersViewModel) {
>     val state by viewModel.state.collectAsStateWithLifecycle()
>     when (state) {                                  // exhaustive when
>         UiState.Loading       -> LoadingSpinner()
>         UiState.Empty         -> EmptyPlaceholder()
>         is UiState.Success    -> OrdersList(state.data)
>         is UiState.Error      -> ErrorScreen(state.message, onRetry = viewModel::load)
>     }
> }
> ```
>
> **Когда применять:** любой Reactive UI (Compose, SwiftUI parity, React/Flutter в KMP), Redux-style state management (Reaktive, Kotlin Redux), MVI architecture (Orbit, Mavericks). Используется в Square Cash, Tinder, Trello Android, Reddit для UI.
>
> **Подводные камни:** иногда нужна **частичная** state — например, «отображаем кэшированные данные ПОКА загружаются свежие». Это смешанное состояние трудно выразить с pure sealed — решение: вложенные states `UiState.Success(data, isRefreshing = true)` или отдельный slot `data class CompleteState(content, banner: BannerState)`. Sealed нельзя сериализовать в Bundle без custom Parcelable — для process death recovery нужны @Parcelize и осторожность с generics. Слишком мелкозернистые states (Loading, Refreshing, BackgroundSync, Idle) → state explosion → рассмотрите hierarchical state machines (SCXML, Statelyx).
>
> **Связанные вопросы:** [[Q1]] — sealed основа; [[Q5]] — Result похожий паттерн; [[Q13]] — state machine как развитие.

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


> [!mcq]
>
> **Вопрос:** Как `sealed class` заменяет «stringly typed» код (`paymentType: String` с branching по содержимому строки)?
>
> ---
>
> #### A) Sealed class **закрывает множество значений на этапе компиляции** и связывает с типом payload: вместо `String` параметра, который может быть произвольной строкой, у нас фиксированный набор подклассов с разными полями для каждого варианта — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Антипаттерн **stringly-typed** — кодирование state/categories/methods как строк (`"pending"`, `"shipped"`, `"card"`, `"paypal"`). Проблемы:
> - **Runtime errors:** опечатка `"paypall"` пройдёт компиляцию, упадёт в проде.
> - **Refactoring небезопасен:** rename константы — нужно искать вхождения в строках по всему коду.
> - **Нет полноты:** компилятор не знает все возможные значения, не помогает при добавлении нового.
> - **Слабая связь с данными:** `"card"` строка не знает про `cardNumber/cvv`, нужны отдельные nullable поля.
>
> Sealed class решает все четыре:
>
> **Пример:**
> ```kotlin
> // ПЛОХО: stringly typed + nullable fields
> data class Payment(
>     val type: String,                       // "card" | "paypal" | "crypto" | опечатка?
>     val cardNumber: String? = null,         // null если type != "card", но компилятор не знает
>     val cardCvv: String? = null,
>     val paypalEmail: String? = null,        // дублирование с email-полем User?
>     val walletAddress: String? = null,
>     val cryptoCurrency: String? = null,
> )
>
> fun process(p: Payment, amount: Money) {
>     when (p.type) {                         // компилятор: type — String, exhaustive невозможен
>         "card"   -> chargeCard(p.cardNumber!!, p.cardCvv!!, amount)    // !! опасно
>         "paypal" -> chargePayPal(p.paypalEmail!!, amount)
>         "crypto" -> chargeCrypto(p.walletAddress!!, p.cryptoCurrency!!, amount)
>         else     -> throw IllegalArgumentException("Unknown: ${p.type}")  // runtime
>     }
> }
>
> // ХОРОШО: sealed type-safe
> sealed class PaymentMethod {
>     data class Card(val number: String, val cvv: String) : PaymentMethod()  // полей всегда оба
>     data class PayPal(val email: String) : PaymentMethod()                  // email обязателен
>     data class Crypto(val walletAddress: String, val currency: String) : PaymentMethod()
> }
>
> fun process(m: PaymentMethod, amount: Money) = when (m) {
>     is PaymentMethod.Card   -> chargeCard(m.number, m.cvv, amount)         // smart cast, без !!
>     is PaymentMethod.PayPal -> chargePayPal(m.email, amount)
>     is PaymentMethod.Crypto -> chargeCrypto(m.walletAddress, m.currency, amount)
>     // exhaustive — добавили ApplePay в sealed → compile error пока не обработаем
> }
> ```
>
> **Когда применять:** доменные перечисления с разной структурой (payment methods, notification channels, identity providers — OAuth/SAML/SSO). Replacement для Spring `@RequestParam` String перечислений (используйте enum или конвертер в sealed). REST API request DTOs с polymorphic body. DB JSONB-колонки с разной структурой (event store, audit log payloads).
>
> **Подводные камни:** при общении с внешними API (REST, queues) всё равно нужен маппинг String ↔ sealed — это «граница системы». Делайте explicit `fromString(s: String): PaymentMethod?` с возвращаемым nullable для невалидных. При сериализации в JSON помните, что дискриминатор обычно — строка (см. Q10) — но это **контролируемая** строка, не proizвольная. Слишком много sealed-подклассов → consider тип-параметр или композицию (см. Q14).
>
> **Связанные вопросы:** [[Q1]] — sealed основы; [[Q6]] — ADT teorija; [[Q10]] — сериализация через дискриминатор.
>
> ---
>
> #### B) Sealed class исключительно для null-safety — это альтернативный синтаксис `Option<T>`/`Maybe<T>` — ❌ Неверно
>
> **Что на самом деле:** null-safety в Kotlin обеспечивается nullable-типами (`String?`) и safe-call оператором (`?.`). Sealed class — это более широкое понятие моделирования вариантов. Можно сделать `sealed class Option<T> { class None<T>; data class Some<T>(val v: T) }`, но это редко нужно — `T?` идиоматичнее.
>
> **Откуда путаница:** функциональные языки (Haskell `Maybe`, Scala `Option`) популяризируют sealed как замену null. Но Kotlin уже имеет nullable types.
>
> **Если бы это было правдой:** примеры sealed были бы все про Option/Maybe, но они про Result, UiState, AST, DomainEvent — гораздо шире.
>
> ---
>
> #### C) Sealed class конвертирует String в enum-подобные константы автоматически — компилятор делает это во время компиляции — ❌ Неверно
>
> **Что на самом деле:** автоматической конвертации **нет**. Sealed class — это **отдельная иерархия типов**; чтобы получить из строки sealed-вариант, нужен явный fromString-маппинг (или kotlinx.serialization decoder с дискриминатором). Компилятор не «магически» парсит строки.
>
> **Откуда путаница:** enum имеет `valueOf(s)` для строкового парсинга — кажется, что sealed тоже может.
>
> **Если бы это было правдой:** имя дискриминатора JSON всегда совпадало бы с именем класса (без явного `@SerialName`) — но `@SerialName` существует именно для этого.
>
> ---
>
> #### D) Sealed class запрещает использовать String внутри подклассов — для строк обязателен `value class` — ❌ Неверно
>
> **Что на самом деле:** подклассы sealed могут хранить любые поля, включая String. `data class Card(val number: String, val cvv: String)` — валидный sealed-подкласс. `value class` — отдельная фича для type-aliases без runtime overhead, не обязателен внутри sealed.
>
> **Откуда путаница:** value class часто используется для усиления type-safety поверх String (`@JvmInline value class CardNumber(val v: String)`) — кажется, что без него sealed «недостаточно строгий».
>
> **Если бы это было правдой:** все примеры sealed в документации Kotlin использовали бы value class — но они используют обычные String/Int.

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


> [!mcq]
>
> **Вопрос:** Как корректно реализовать **state machine** через sealed class, где переходы между состояниями типизированы, а невалидные переходы — невозможны на этапе компиляции?
>
> ---
>
> #### A) Все возможные переходы хранятся как глобальная Map<State, List<Event>> — sealed class не даёт type-safety для transitions — ❌ Неверно
>
> **Что на самом деле:** sealed class **способен** обеспечить type-safety переходов через **метод `next(event: Event)` на каждом конкретном состоянии**, реализующий **только разрешённые** переходы. Глобальная Map — слабо типизированный антипаттерн, теряющий compile-time проверку.
>
> **Откуда путаница:** state machine libraries (Tinder StateMachine, Statelyx) часто используют DSL с map-подобной декларацией — кажется, что это обязательный паттерн.
>
> **Если бы это было правдой:** sealed state machines не имели бы преимуществ над enum-based — но имеют.
>
> ---
>
> #### B) Каждое состояние — отдельный подкласс sealed `OrderState`, реализующий метод `next(event: OrderEvent): OrderState` с `when (event)`, в котором перечислены **только разрешённые из этого состояния** переходы; невалидные переходы либо отсутствуют (компилятор exhaustive не проверяет «отрицательное»), либо явно бросают `IllegalStateException` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Паттерн «state machine как sealed»:
> 1. `sealed class State` — все возможные состояния как подклассы.
> 2. `sealed class Event` — все возможные события/команды как подклассы.
> 3. `State.next(Event): State` — функция перехода, реализуется в каждом подклассе индивидуально.
> 4. Невалидный переход — `IllegalStateException` или возврат текущего state без изменений.
>
> Для **строгой type-safety** (невалидные переходы не компилируются вовсе) применяют **typed states** — каждое состояние имеет свой класс с разными методами:
>
> **Пример (классический подход):**
> ```kotlin
> sealed class OrderEvent {
>     data object Confirm : OrderEvent()
>     data object Ship : OrderEvent()
>     data object Deliver : OrderEvent()
>     data class Cancel(val reason: String) : OrderEvent()
> }
>
> sealed class OrderState {
>     abstract fun next(event: OrderEvent): OrderState
>
>     data object Pending : OrderState() {
>         override fun next(event: OrderEvent) = when (event) {
>             OrderEvent.Confirm   -> Confirmed(Instant.now())
>             is OrderEvent.Cancel -> Cancelled(event.reason)
>             else                 -> throw IllegalStateException("Pending → $event invalid")
>         }
>     }
>
>     data class Confirmed(val confirmedAt: Instant) : OrderState() {
>         override fun next(event: OrderEvent) = when (event) {
>             OrderEvent.Ship      -> Shipped(Instant.now(), confirmedAt)
>             is OrderEvent.Cancel -> Cancelled(event.reason)
>             else                 -> throw IllegalStateException("Confirmed → $event invalid")
>         }
>     }
>
>     data class Shipped(val shippedAt: Instant, val confirmedAt: Instant) : OrderState() {
>         override fun next(event: OrderEvent) = when (event) {
>             OrderEvent.Deliver -> Delivered(Instant.now())
>             else               -> throw IllegalStateException("Shipped → $event invalid")
>         }
>     }
>
>     data class Delivered(val deliveredAt: Instant) : OrderState() {
>         override fun next(event: OrderEvent): OrderState =
>             throw IllegalStateException("Order already delivered, no transitions")
>     }
>
>     data class Cancelled(val reason: String) : OrderState() {
>         override fun next(event: OrderEvent): OrderState =
>             throw IllegalStateException("Order already cancelled, no transitions")
>     }
> }
>
> // Использование
> var state: OrderState = OrderState.Pending
> state = state.next(OrderEvent.Confirm)       // → Confirmed
> state = state.next(OrderEvent.Ship)          // → Shipped
> state = state.next(OrderEvent.Cancel("..."))  // → IllegalStateException
> ```
>
> **Когда применять:** доменные state machines (Order, Subscription, KYC verification flow, OAuth grant), workflow engines (Booking.com confirmation pipeline), saga coordinators в distributed systems (Wolt order saga, Stripe Connect onboarding), Android navigation flows. Tinder StateMachine, Square Workflow — production-ready реализации.
>
> **Подводные камни:** «runtime exception на невалидном переходе» — не идеал, лучше типизировать сильнее. Альтернатива — **type-state pattern**: разные классы для каждого состояния с разными методами (`Pending.confirm(): Confirmed`, `Shipped` не имеет `confirm()`). Это даёт compile-time запрет, но усложняет хранение (нужно extract from sealed in runtime). Также State Machine с **side effects** (отправка email, обновление БД) требует transactional boundary — обычно команда обрабатывается в одной транзакции с persistent состоянием. Для distributed scenarios используйте Saga (Axon, Eventuate, Cadence) — sealed state-of-saga + compensation actions.
>
> **Связанные вопросы:** [[Q1]] — sealed основа; [[Q6]] — ADT для states/events; [[Q11]] — UiState как state machine для UI.
>
> ---
>
> #### C) State machine через sealed работает только в Kotlin Coroutines: state переходы происходят через suspendCoroutine — ❌ Неверно
>
> **Что на самом деле:** state machine — это **синхронный паттерн**, не требующий coroutines. Можно реализовать blocking (как в примере выше) или async (через `Flow<State>` или suspend functions). Зависимость от coroutines — выбор разработчика, не требование sealed.
>
> **Откуда путаница:** Kotlin Coroutines compiler plugin внутри использует state machine для реализации `suspend` функций (continuation = current state). Эту runtime-деталь путают с «sealed state machine».
>
> **Если бы это было правдой:** не было бы blocking state machines в Spring/Java мире (но они есть — Spring StateMachine, Akka FSM).
>
> ---
>
> #### D) Sealed state machine требует обязательного хранения в БД через JPA `@DiscriminatorColumn` — без этого не работает — ❌ Неверно
>
> **Что на самом деле:** state machine как concept не привязан к persistence. Можно держать state in-memory (для request-scoped, например, validation pipeline), в Redis, в JSON колонке БД, или вообще не персистить. `@DiscriminatorColumn` — JPA-механизм для single-table inheritance, опциональный.
>
> **Откуда путаница:** многие state machines в реальности персистируются, чтобы пережить рестарт — и JPA inheritance часто всплывает в этом контексте.
>
> **Если бы это было правдой:** in-memory state machines (например, Compose UI state) не работали бы — но работают.

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


> [!mcq]
>
> **Вопрос:** Что означают `out` и `in` modifiers в дженериках `sealed class Result<out T>` и `sealed class Consumer<in T>`, и почему `Nothing` часто появляется в sealed-иерархиях?
>
> ---
>
> #### A) `out T` означает, что подклассы могут мутировать T — это convenience для in-place modification — ❌ Неверно
>
> **Что на самом деле:** `out` означает **covariance**: `Result<Dog>` является подтипом `Result<Animal>`, если `Dog : Animal`. Это про **отношение между параметризованными типами**, не про мутацию. `out` запрещает использовать T как параметр функций — только как return type («производитель» T).
>
> **Откуда путаница:** keyword `out` ассоциируется с output parameters в C# (`out int x` — параметр функции, заполняемый внутри). Это разные понятия.
>
> **Если бы это было правдой:** sealed классы с `out T` не были бы immutable (data class основа), но они immutable.
>
> ---
>
> #### B) `out` и `in` — синонимы, выбор стилистический; компилятор обрабатывает их одинаково — ❌ Неверно
>
> **Что на самом деле:** они имеют **противоположную семантику**. `out T` (covariance) — `Result<Dog> : Result<Animal>`. `in T` (contravariance) — `Consumer<Animal> : Consumer<Dog>` (обратное направление!). Это **разные** правила подтипирования, и compiler enforce'ит соответствующие ограничения на использование T в сигнатурах.
>
> **Откуда путаница:** оба keyword «маленькие», в одной позиции — кажется, что взаимозаменяемы.
>
> **Если бы это было правдой:** PECS («Producer Extends, Consumer Super» из Java) не имел бы смысла, и `Comparator<Animal>` нельзя было бы использовать там, где ожидается `Comparator<Dog>` — но можно (contravariance).
>
> ---
>
> #### C) `out T` (covariance) — T используется только как **return type** (производитель T), позволяет `Result<Dog>` присваивать в `Result<Animal>`. `in T` (contravariance) — T используется только как **параметр функций** (потребитель T), позволяет `Consumer<Animal>` присваивать в `Consumer<Dog>`. `Nothing` — нижний тип, subtype всех типов; используется в sealed для подклассов «без T» (`Failure : Result<Nothing>` — присваивается в `Result<T>` любого T) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Variance — правила отношений между параметризованными типами:
>
> - **Covariant (`out T`):** если `Sub : Base`, то `Container<Sub> : Container<Base>`. Аналог в Java — `List<? extends Base>`. Возможно только если T встречается **только в out-позициях** (return types, val properties).
>
> - **Contravariant (`in T`):** если `Sub : Base`, то `Container<Base> : Container<Sub>` (обратно!). Аналог в Java — `List<? super Sub>`. Возможно только если T встречается **только в in-позициях** (parameters).
>
> - **Invariant (по умолчанию):** `Container<Sub>` НЕ подтип `Container<Base>`. T может быть в любой позиции.
>
> `Nothing` — bottom type системы типов Kotlin. Является subtype любого типа (включая `String`, `Int`, `Result<User>`). Применение: в sealed-варианте без данных делается `: Result<Nothing>`, и это можно присвоить в `Result<T>` любого T (благодаря covariance + `Nothing <: T`).
>
> **Пример:**
> ```kotlin
> // Covariant sealed Result
> sealed class Result<out T> {
>     data class Success<T>(val value: T) : Result<T>()
>     data class Failure(val error: Throwable) : Result<Nothing>()  // нет T → Nothing
>     data object Loading : Result<Nothing>()                       // singleton, нет T
> }
>
> val intResult: Result<Int> = Result.Success(42)
> val anyResult: Result<Any> = intResult                            // OK: covariance, Int <: Any
> val pending: Result<Int> = Result.Loading                         // OK: Result<Nothing> <: Result<Int>
> val failed: Result<User> = Result.Failure(IOException())          // OK: тот же механизм
>
> // Contravariant Consumer
> abstract class Consumer<in T> {
>     abstract fun consume(value: T)
> }
>
> val anyConsumer: Consumer<Any> = object : Consumer<Any>() {
>     override fun consume(value: Any) = println(value)
> }
> val intConsumer: Consumer<Int> = anyConsumer                      // OK: contravariance, anyConsumer принимает Any → справится с Int
>
> // Mixed: function type is contravariant in input, covariant in output
> val transformer: (Int) -> Any = anyToString                       // (Any) -> String — это подтип (Int) -> Any
> ```
>
> ```kotlin
> // Пример с flatMap и UnsafeVariance
> sealed class Result<out T, out E> {
>     data class Success<T>(val value: T) : Result<T, Nothing>()
>     data class Failure<E>(val error: E) : Result<Nothing, E>()
>
>     // flatMap имеет E в in-позиции, что нарушает out
>     inline fun <R> flatMap(
>         transform: (T) -> Result<R, @UnsafeVariance E>            // ← маркер «знаю, что делаю»
>     ): Result<R, E> = when (this) {
>         is Success -> transform(value)
>         is Failure -> this
>     }
> }
> ```
>
> **Когда применять:** `out T` — для «producer» типов (`Result<T>`, `Option<T>`, `Iterator<T>`, `List<out T>` — read-only). `in T` — для «consumer» типов (`Comparator<T>`, `Consumer<T>`, `Validator<T>`, `EventListener<T>`). `Nothing` — для empty-вариантов sealed (Empty, Loading, Error без payload), для `fail-fast` функций (`fun fail(): Nothing = throw ...`), для `TODO()` (return type — Nothing).
>
> **Подводные камни:** `@UnsafeVariance` нужен когда T используется одновременно как в out, так и in (типичное место — `equals`, `flatMap`, `fold`); это compile-time escape hatch, который compiler позволяет, перекладывая ответственность на программиста. Variance работает только для own-параметров класса (declaration-site variance), для конкретного use-case есть use-site variance (`Result<out User>`). Java-интероп: Kotlin's `out` мапится на Java's `? extends`, `in` — на `? super`, что иногда генерирует verbose signatures.
>
> **Связанные вопросы:** [[Q1]] — sealed основа; [[Q5]] — Result с variance; [[Q6]] — ADT с дженериками.
>
> ---
>
> #### D) `out T` запрещает использовать T как public property — это про инкапсуляцию полей — ❌ Неверно
>
> **Что на самом деле:** `out T` позволяет T как `val` (read-only property — это return-position, get-метод). Запрещает `var` с public setter (set-метод — parameter-position) и параметры открытых методов с T. Это правило про **позицию использования**, не про инкапсуляцию.
>
> **Откуда путаница:** ограничения `out` могут показаться encapsulation-related, потому что некоторые модификации запрещены.
>
> **Если бы это было правдой:** `data class Success<T>(val value: T) : Result<T>` не компилировался бы (data class properties — public val), но компилируется отлично.

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


> [!mcq]
>
> **Вопрос:** Какая самая частая (и самая «тихая») ошибка при работе с `sealed class` в production-коде?
>
> ---
>
> #### A) Использование `data class` вместо `class` для подкласса sealed — это ломает hashCode/equals — ❌ Неверно
>
> **Что на самом деле:** `data class` как подкласс sealed — это **рекомендуемая практика**. Data class даёт автогенерируемые `equals`/`hashCode`/`toString`/`copy`, что критично для сравнения state и debug-логов. Никаких поломок нет — это идиоматично.
>
> **Откуда путаница:** в Java иерархии с inheritance часто требуют ручной реализации equals для корректной работы — Kotlin data class это делает иначе (по primary constructor properties).
>
> **Если бы это было правдой:** документация Kotlin не рекомендовала бы data class для sealed-вариантов — но рекомендует.
>
> ---
>
> #### B) Использование `object` для singleton-вариантов без данных — это создаёт OOM при serialization — ❌ Неверно
>
> **Что на самом деле:** `object` — это singleton (один экземпляр на JVM), **меньше** памяти, не больше. Это **корректная** практика для Loading/Empty/идентичных-стейтов без данных. Сериализация работает через `@SerialName` дискриминатор без OOM.
>
> **Откуда путаница:** Kotlin 1.9 ввёл `data object` — некоторые думают, что обычный `object` теперь устарел или проблемный. На деле `data object` отличается только в `toString()` — даёт имя, не hash.
>
> **Если бы это было правдой:** Android UI state recommendations не использовали бы `object Loading` — но используют.
>
> ---
>
> #### C) Объявление sealed класса внутри функции (local class) — это запрещено компилятором с error message — ❌ Неверно (но это запрещено)
>
> **Что на самом деле:** действительно local sealed запрещён (Modifier 'sealed' is not applicable to local class), но это **compile-time error**, а не «тихая» ошибка в проде — её невозможно случайно протащить через CI.
>
> **Откуда путаница:** ограничение известно, но люди по ошибке считают его «возможной runtime-проблемой».
>
> **Если бы это было правдой:** не было бы build-failure на стадии компиляции — но есть.
>
> ---
>
> #### D) Использование `when` как **statement** (без присваивания, без `return`) вместо expression в Kotlin до 2.0 — компилятор НЕ требовал exhaustiveness; при добавлении нового sealed-подкласса баг тихо уходит в прод как «unknown case без обработки» — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Это исторически самый частый и самый коварный антипаттерн с sealed. До Kotlin 1.7 `when`-statement над sealed **не проверялся** на exhaustiveness — компилятор молча принимал неполное покрытие. При добавлении нового подкласса в существующий sealed, все `when`-statements нужно было искать вручную (Find Usages не идеален). С Kotlin 1.7+ — warning, с 2.0 — error.
>
> Дополнительные коварные ошибки:
> - Добавление **`else`-ветки** в `when` над sealed — отключает exhaustiveness даже для expression.
> - Использование `class Loading : Status()` вместо `object Loading` — создаются лишние экземпляры с разным `hashCode`/`equals`, что ломает comparison.
> - Sealed для слишком простых перечислений (где enum достаточно) — over-engineering.
>
> **Пример (антипаттерн и фикс):**
> ```kotlin
> sealed class PaymentResult {
>     data class Success(val txId: String) : PaymentResult()
>     data class Declined(val reason: String) : PaymentResult()
>     // Завтра кто-то добавит PendingReview...
> }
>
> // АНТИПАТТЕРН: when как statement, компилятор молчит до 1.7
> fun handle(result: PaymentResult) {
>     when (result) {                          // <-- statement, не expression
>         is PaymentResult.Success -> notify("Paid: ${result.txId}")
>         is PaymentResult.Declined -> notify("Declined: ${result.reason}")
>         // PendingReview не обработан — sealed добавили, when забыли
>     }
> }
>
> // Усугубляет: else "защищает" от exhaustive проверки
> fun handleWithElse(result: PaymentResult) {
>     when (result) {
>         is PaymentResult.Success -> notify("Paid")
>         is PaymentResult.Declined -> notify("Declined")
>         else -> Unit                         // <-- sealed теряет смысл
>     }
> }
>
> // ФИКС: expression-форма (присваивание/return)
> fun handleFixed(result: PaymentResult) {
>     val message: String = when (result) {    // <-- expression, exhaustive enforced
>         is PaymentResult.Success -> "Paid: ${result.txId}"
>         is PaymentResult.Declined -> "Declined: ${result.reason}"
>         // компилятор: missing branch PendingReview — сборка падает
>     }
>     notify(message)
> }
>
> // Альтернатива: трюк для statement
> fun handleForce(result: PaymentResult) {
>     val _: Unit = when (result) {           // <-- Unit-expression, тоже exhaustive
>         is PaymentResult.Success -> notify("Paid: ${result.txId}")
>         is PaymentResult.Declined -> notify("Declined: ${result.reason}")
>     }
> }
> ```
>
> **Когда применять:** аудит существующего кода — поиск `when (sealed) { }` без присваивания. Включайте strict mode в build: `languageVersion = "2.0"` и/или `freeCompilerArgs += "-Xnon-exhaustive-when-statements"`. Используйте detekt rule `NoElseInWhenStatement` или внутреннюю lint-rule. Code review checklist: каждый `when` с sealed-типом должен быть expression.
>
> **Подводные камни при исправлении:** «Force expression» через `val _ = when {}` — некрасивый, но рабочий хак. Лучше — рефакторинг функции, чтобы она возвращала значение (что часто полезно и для тестирования). Расширение sealed (`PendingReview`) — намеренно делайте этот рефакторинг в отдельном MR без других изменений — full rebuild покажет все места. Перед публикацией библиотеки с sealed: фиксируйте список вариантов в semver — добавление варианта = breaking change для клиентов.
>
> **Связанные вопросы:** [[Q1]] — основы sealed; [[Q3]] — exhaustive when подробнее; [[Q11]] — UiState и compose recomposition.

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
