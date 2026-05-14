---
title: "Вопросы на собеседовании: Kotlin Value Classes"
description: "Kotlin Value Classes (inline classes): type-safe wrappers без runtime overhead, JVM-представление, ограничения, сравнение с data classes"
tags:
  - interview
  - kotlin
  - kotlin-value-classes-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Kotlin Value Classes"
  - "Kotlin Value Classes interview"
  - "Value Classes собеседование"
prerequisites:
  - "[[kotlin-value-classes]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Kotlin Value Classes`

`Value Classes` (бывшие `inline classes`) — типобезопасные обёртки вокруг примитивов или объектов без runtime overhead. Компилятор инлайнит значение в месте использования. Используются для `UserId`, `Email`, `Money` и других domain-типов.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Kotlin Inline Value Classes](https://kotlinlang.org/docs/inline-classes.html) — официальная документация
- [Baeldung: Kotlin Inline Classes](https://www.baeldung.com/kotlin/inline-classes) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое value class в Kotlin и зачем он нужен?

**Value class** (ранее inline class) — специальный тип класса, оборачивающий одно значение без создания отдельного объекта в памяти. Компилятор подставляет underlying-значение напрямую (`inlining`).

```kotlin
@JvmInline
value class UserId(val value: String)

@JvmInline
value class Money(val cents: Long)

@JvmInline
value class Email(val address: String) {
    init {
        require(address.contains("@")) { "Invalid email" }
    }
}

// Использование
val userId = UserId("user-123")
val price = Money(9999)  // cents
val email = Email("alice@example.com")
```

**Зачем нужны**:
1. **Type safety без runtime overhead** — `UserId` и `OrderId` нельзя перепутать, в отличие от обоих `String`.
2. **Валидация** в init блоке — невалидный email невозможно создать.
3. **Самодокументирующийся код** — `Money(9999)` понятнее чем `Long(9999)`.


> [!mcq]
>
> **Вопрос:** Что такое `value class` в Kotlin и какую главную задачу он решает?
>
> ---
>
> #### A) Типобезопасная обёртка над одним значением, которую компилятор инлайнит в underlying-тип на сайте использования — обычно без выделения объекта в куче — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `value class` (до Kotlin 1.5 — `inline class`) — это специальный класс, который существует только в Kotlin type system. На JVM компилятор подставляет underlying-значение напрямую везде, где это возможно, а методы класса превращаются в статические функции с mangled-именами (`UserId.getValue-impl(String)`). Получаем одновременно: (1) type safety на этапе компиляции — `UserId` и `OrderId` нельзя перепутать; (2) inline-представление в байт-коде — runtime overhead обычно нулевой. Boxing происходит только в специальных позициях: nullable, generic, интерфейсный тип, рефлексия.
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class UserId(val value: String)
>
> @JvmInline
> value class OrderId(val value: String)
>
> fun loadUser(id: UserId): User = repo.findById(id.value)
>
> val u = UserId("u-1")
> val o = OrderId("o-1")
> loadUser(u)        // OK
> // loadUser(o)     // ошибка компиляции — UserId != OrderId
> ```
>
> **Когда применять:**
> - Domain primitives — `UserId`, `OrderId`, `Email`, `Money`, `Percentage`, `Port`.
> - Места, где `String`/`Long`/`Int` массово передаются между сервисами и можно перепутать аргументы (Slack postmortems: «accidentally swapped userId and accountId»).
> - Валидация инвариантов в `init` (`require(value in 0..100)` для `Percentage`).
>
> **Подводные камни:**
> - В `List<UserId>`, `Map<UserId, …>`, `UserId?` происходит boxing — преимущество inline теряется.
> - При вызове через интерфейс Kotlin вынужден упаковать значение в обёртку.
> - Jackson/JPA по умолчанию не понимают value class — нужен `KotlinModule`/`AttributeConverter`.
>
> **Связанные вопросы:** [[Q2]] — отличия от `data class`; [[Q3]] — как представлен в байт-коде; [[Q8]] — типовые применения (UserId, Money, Email).
>
> ---
>
> #### B) Это синтаксический сахар над `data class` с одним полем, генерирующий `equals/hashCode/copy`, но всегда живущий как обычный объект в куче — ❌ Неверно
>
> **Что на самом деле:** value class — это не data class. Главное различие — представление в памяти. `data class` всегда создаёт объект в куче, value class в большинстве позиций (локальные переменные, параметры функций, return-типы) инлайнится в underlying-значение. `equals`/`hashCode` действительно генерируются, но `copy()` есть тоже у data class — это не отличительный признак.
>
> **Откуда путаница:** оба класса автогенерируют `equals/hashCode`, имеют ровно primary constructor с `val`-полями и поддерживают destructuring — внешне они похожи. Если игнорировать `@JvmInline` и слово «inline» в истории фичи, легко принять value class за «data class на одно поле».
>
> **Если бы это было правдой:** тогда обёртка `Money(cents)` в горячем цикле платежей создавала бы миллионы лишних объектов в Eden, GC pause росли бы пропорционально объёму платежей, и единственный смысл фичи (zero-cost domain types) исчез бы — проще было бы оставлять `Long`.
>
> ---
>
> #### C) Это `typealias` с проверкой типов на этапе компиляции — компилятор подставляет original type везде, никаких различий в семантике с typealias нет — ❌ Неверно
>
> **Что на самом деле:** `typealias UserId = String` — это синоним, который не вводит новый тип: `UserId` и `String` остаются взаимозаменяемыми, ошибку перепутывания не поймать. `value class UserId(val value: String)` — новый тип с собственным `equals`, валидацией в `init`, методами и operator-функциями. Inline в байт-коде — побочное преимущество, не главное.
>
> **Откуда путаница:** в обоих случаях «обёртываем строку и работаем как со строкой». Если не запускать компиляцию с реальной попыткой передать `OrderId` вместо `UserId`, разница не очевидна. Многие гайды на Хабре подают value class как «typealias с проверкой типов».
>
> **Если бы это было правдой:** валидация в `init` была бы невозможна (у typealias нет тела), нельзя было бы добавить operator-функции (`Money + Money`), и не работала бы перегрузка функций по value class — компилятор не смог бы различить `process(userId: UserId)` и `process(orderId: OrderId)`, поскольку оба превратились бы в `process(String)`.
>
> ---
>
> #### D) Класс, который JVM хранит на стеке вместо кучи — аналог C# `struct` или Java Project Valhalla `value record` — ❌ Неверно
>
> **Что на самом деле:** Kotlin `value class` в текущей реализации работает поверх обычной JVM — она не умеет хранить пользовательские типы на стеке (нет HotSpot stack-allocation для arbitrary types). Компилятор Kotlin делает «inline на уровне исходника»: подставляет underlying-значение (String/Long/…) на сайтах использования и переписывает методы в статические. Никакого специального stack-frame storage нет. Project Valhalla (когда дойдёт до production JDK) принесёт настоящие JVM value types, и тогда `@JvmInline` сможет на них опереться — но это будущее.
>
> **Откуда путаница:** маркетинг «zero-overhead» + знание про C# struct и Valhalla naturally приводят к «значит, оно на стеке». Документация Kotlin часто говорит «inlined» без уточнения, что это compile-time inlining, а не JVM stack allocation.
>
> **Если бы это было правдой:** value class работал бы только на JVM с Valhalla preview, и существующие JDK 17/21 LTS отказывали бы компиляции. Реально же `@JvmInline value class` работает на JDK 8+ — потому что это compile-time трюк, а не JVM-фича.

## Q2. Чем value class отличается от data class?

| Критерий | data class | value class |
|----------|-----------|-------------|
| Количество полей | Любое | Ровно одно (в stable Kotlin) |
| Представление в памяти | Отдельный объект | Inlined — underlying-значение |
| `equals/hashCode` | Автогенерация по всем полям | Автогенерация по единственному полю |
| `copy()` | Да | Да |
| Destructuring | Да | Да (для одного значения) |
| Наследование | Только от интерфейсов | Только от интерфейсов |
| Overhead | Выделение объекта в куче | Обычно 0 (inline) |

```kotlin
// data class — всегда объект в куче
data class DataUserId(val value: String)
val id1 = DataUserId("123")          // new Object
val id2 = DataUserId("123")          // new Object
id1 == id2                            // true, но разные instance

// value class — обычно просто String в байт-коде
@JvmInline
value class UserId(val value: String)
val id3 = UserId("123")               // без нового объекта (обычно)
val id4 = UserId("123")               // тоже без
id3 == id4                            // true
```


> [!mcq]
>
> **Вопрос:** В чём ключевое архитектурное отличие `value class` от `data class` в Kotlin (stable релизы)?
>
> ---
>
> #### A) Они полностью эквивалентны: `value class` — это `data class` с особой аннотацией; разница только в keyword — ❌ Неверно
>
> **Что на самом деле:** value class и data class — разные сущности. У `data class` нет ограничения на число полей, всегда выделяется объект в куче и поддерживается полный `componentN()` для destructuring всех полей. value class в stable Kotlin требует ровно одно `val`-поле, в большинстве позиций инлайнится, не имеет backing fields для дополнительных свойств. Аннотация `@JvmInline` обязательна для JVM target, не опциональна.
>
> **Откуда путаница:** оба keyword предваряются префиксом, оба генерируют `equals/hashCode`, оба поддерживают destructuring и `copy()`. Если читать только сигнатуры в IDE, отличий не видно. Bonus confusion — историческое название `inline class`, которое многие путают с `inline fun`.
>
> **Если бы это было правдой:** компилятор не отличал бы `data class A(val v: String)` от `value class A(val v: String)` — но реально вторая форма заставляет создавать `@JvmInline`, ограничивает поля и mangling, а первая создаёт полноценный POJO. Команды, мигрирующие domain primitives с data class на value class, не увидели бы reduction в GC overhead — а на практике видят (Yandex Lavka доклады, Booking.com Kotlin migration).
>
> ---
>
> #### B) `value class` ограничен одним `val`-полем и в большинстве позиций инлайнится в underlying-тип; `data class` поддерживает несколько полей и всегда живёт как объект в куче — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Stable Kotlin `value class`: ровно одно поле в primary constructor (обязательно `val`, не `var`), `@JvmInline` обязателен для JVM, в локальных переменных и параметрах функций инлайнится в underlying-значение, методы становятся static с mangled-именами для overload resolution. `data class`: любое число полей, всегда heap-allocated, генерирует полноценный `componentN()` для каждого поля, имеет реальные backing fields. Сходства — оба автоматически дают `equals/hashCode/toString/copy()` и поддерживают destructuring. MFVC (multi-field value classes) — preview-фича с Kotlin 1.8.20, в stable пока недоступна.
>
> **Пример:**
> ```kotlin
> // data class — два поля, всегда heap object
> data class Point(val x: Double, val y: Double)
>
> // value class — одно поле, inline
> @JvmInline
> value class Celsius(val value: Double) {
>     operator fun plus(other: Celsius) = Celsius(value + other.value)
> }
>
> // Использование
> val p = Point(1.0, 2.0)              // new Point в куче
> val t = Celsius(20.0) + Celsius(5.0) // обычно никаких new — только double-арифметика
>
> // Попытка двух полей в value class:
> // @JvmInline
> // value class Point2D(val x: Double, val y: Double)
> // → error: value class primary constructor must have only final read-only (val) property
> ```
>
> **Когда применять:**
> - `value class` — для domain primitives, обёртки одного значения (`UserId`, `Money`, `Email`).
> - `data class` — для составных объектов: `Address(street, city, zip)`, `OrderLine(productId, qty, price)`, DTO с многими полями для REST/Kafka.
> - Часто используют комбинацию: `data class Order(val id: OrderId, val customer: CustomerId, val total: Money)` — внутри композиции поля являются value class.
>
> **Подводные камни:**
> - Перенося существующие domain primitives с data class на value class, проверьте, не используются ли они массово в `List<…>` — там boxing нивелирует выгоду.
> - В Spring контроллере `@RequestBody data class` десериализуется Jackson «из коробки», а value class требует `KotlinModule.Builder().build()` и иногда custom deserializer.
> - `data class` поддерживает `componentN()` для всех полей — это используется в for-loop по map (`for ((k, v) in map)`); value class даёт только `component1()`.
>
> **Связанные вопросы:** [[Q1]] — что такое value class; [[Q4]] — конкретные ограничения value class; [[Q8]] — UserId/OrderId как value class.
>
> ---
>
> #### C) `value class` всегда быстрее, чем `data class`, для любых сценариев — поэтому домейные обёртки нужно всегда писать только через value class — ❌ Неверно
>
> **Что на самом деле:** value class имеет преимущество только в позициях, где компилятор может заинлайнить значение. Boxing активируется в: `List<UserId>`, `Map<UserId, …>`, `UserId?`, вызов через интерфейс, рефлексия, обработка через generic-функцию. В этих случаях value class может оказаться даже медленнее data class из-за дополнительного mangling и проверок при unboxing. Для составных domain-объектов (Address, OrderLine) value class неприменим — нужны несколько полей.
>
> **Откуда путаница:** маркетинг «zero-cost abstraction» создаёт иллюзию «всегда быстрее». В benchmark на простых кейсах (`UserId` как параметр функции) это правда. Но на горячих путях с коллекциями и интерфейсами картина меняется.
>
> **Если бы это было правдой:** Kotlin team давно бы deprecate `data class` для одно-полевых случаев. Реально же в официальной документации специально подчёркнуто: «значительный выигрыш только в подходящих сценариях; в коллекциях используется boxing».
>
> ---
>
> #### D) `data class` ограничен одним полем, а `value class` может иметь любое количество полей и хранит их в одной куче-структуре — ❌ Неверно
>
> **Что на самом деле:** всё наоборот. `data class` поддерживает любое разумное число полей (`data class Address(val street: String, val city: String, val zip: String)` — норма). `value class` в stable Kotlin требует ровно одно `val`-поле. Multi-field value classes (MFVC) существуют только в Kotlin 1.8.20+ как preview-фича с `@JvmInline`, не для production без флага `-Xvalue-classes`.
>
> **Откуда путаница:** реверс правильного утверждения. Если человек слышал «есть какое-то ограничение по полям, и одна из этих фич ограничена», но не помнит какая — путаница очевидна. Также влияет тот факт, что `data class` визуально кажется «более структурным» (больше синтаксиса), а value class — «единичным».
>
> **Если бы это было правдой:** мы бы писали `value class Address(val street, city, zip)` и теряли inline-преимущество (объект всё равно должен где-то жить), а `data class UserId(val value: String)` нельзя было бы создать — что разрушило бы кодовую базу любого Spring Boot проекта с `@Entity data class User(@Id val id: Long, …)`.

## Q3. Как value class представлен в JVM байт-коде?

Компилятор подставляет underlying-значение везде, где возможно, избегая выделения объекта:

```kotlin
@JvmInline
value class Celsius(val value: Double)

fun freezingPoint() = Celsius(0.0)

fun isFreezing(temp: Celsius) = temp.value <= 0.0

// После компиляции примерно:
// fun freezingPoint(): double { return 0.0 }
// fun isFreezing-ABC(double temp): boolean { return temp <= 0.0 }
//                   ^^^ mangled name для overload resolution
```

**Когда inlining НЕ происходит**:
1. При вызове методов на value class через интерфейс.
2. При использовании в коллекциях (`List<UserId>` → `List<Object>` после boxing).
3. При использовании в generic-позициях.
4. При nullable типах (`UserId?` → бокс).

```kotlin
@JvmInline
value class UserId(val value: String)

fun process(ids: List<UserId>) {     // боксинг — ids хранит объекты
    for (id in ids) id.value         // unboxing при доступе
}
```


> [!mcq]
>
> **Вопрос:** Как `@JvmInline value class` представлен в JVM байт-коде и когда происходит boxing?
>
> ---
>
> #### A) Компилятор всегда создаёт класс-обёртку с полем underlying-значения; никакого инлайнинга не происходит — `@JvmInline` влияет только на видимость в Java — ❌ Неверно
>
> **Что на самом деле:** класс действительно генерируется (нужен для `equals/hashCode`, рефлексии, boxing-кейсов), но в локальных переменных, параметрах функций и возвращаемых значениях компилятор подставляет underlying-значение напрямую. Методы класса превращаются в статические с mangled-именами: `UserId.getValue-impl(String)`, `UserId.equals-impl0(String, String)`. `@JvmInline` обязателен для JVM target и контролирует именно inlining, не только Java visibility.
>
> **Откуда путаница:** просматривая декомпилированный байт-код в IntelliJ через «Show Kotlin Bytecode», человек видит сгенерированный класс `UserId` и решает, что объект создаётся всегда. На самом деле в вызове `fun process(id: UserId)` сигнатура в байт-коде — `process-XYZ(String)`, без UserId-параметра.
>
> **Если бы это было правдой:** value class не давал бы никакого performance-выигрыша по сравнению с data class — каждый раз new object. Доклады Kotlin team на KotlinConf про zero-cost abstractions были бы маркетингом, а benchmark от JetBrains (≤1% overhead для value class vs raw primitive) не воспроизводились бы.
>
> ---
>
> #### B) Inlining работает только для value class, оборачивающих JVM-примитивы (Int, Long, Double) — `value class Wrap(val s: String)` всегда боксится — ❌ Неверно
>
> **Что на самом деле:** inlining работает для любого underlying-типа, не только примитивов. `value class UserId(val value: String)` в параметре функции передаётся как `String`, а не как `UserId` — никакой обёртки не создаётся. Underlying-тип может быть `String`, `Long`, `Int`, `ByteArray`, любой reference-тип. Главное — позиция использования: локальная переменная или параметр → inline, generic/nullable/interface → box.
>
> **Откуда путаница:** аналогия с Java autoboxing (`Integer`/`Long`) и primitive specializations в Kotlin (`IntArray`). Логично предположить «inline есть только там, где есть primitive». Реально граница inline проходит по другому критерию — статически известен ли точный тип в данной позиции.
>
> **Если бы это было правдой:** все value class для UserId/Email/IpAddress (а это 80% реальных применений) теряли бы преимущество, и фича была бы полезна только для редких `Money(val cents: Long)` или `Celsius(val value: Double)`. Реально же inline работает на reference-обёртках и доминирует в DDD-кодовых базах.
>
> ---
>
> #### C) Компилятор инлайнит underlying-значение в параметрах функций, локальных переменных и return-типах; boxing происходит в позициях `List<UserId>`, `UserId?`, при вызове через интерфейс и в generic-функциях — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Inline происходит, когда компилятор статически знает точный тип в позиции и underlying-тип «помещается» прямо туда. Для большинства функций сигнатура `fun process(id: UserId)` превращается в `process-ABC123(String id)` — никакого UserId-объекта в стек-фрейме нет, есть указатель на String. Boxing активируется в позициях, где сигнатура должна работать с любым типом: дженерики (`List<T>`, `Map<K, V>`), nullable (`UserId?` — нужно различать `null` и значение, поэтому обёртка), интерфейс (`Identifier` — динамический dispatch требует объекта), reflection (`Class<UserId>` — нужен Class object). Mangling — name mangling метода — нужен, чтобы перегрузки по разным value class не конфликтовали: `process(UserId)` и `process(OrderId)` дают `process-A(String)` и `process-B(String)` с разными суффиксами.
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class UserId(val value: String)
>
> // INLINE — сигнатура в bytecode: process$default-XYZ(String, …)
> fun process(id: UserId) = id.value.uppercase()
>
> // BOX — generic: List<Object>, каждый UserId упакован
> fun batchProcess(ids: List<UserId>) {
>     for (id in ids) id.value  // unboxing при доступе
> }
>
> // BOX — nullable: UserId? передаётся как UserId-object, не String
> fun maybeProcess(id: UserId?) = id?.value
>
> // BOX — интерфейс: динамический dispatch требует объект
> interface HasId { val id: String }
> @JvmInline
> value class TaggedId(override val id: String) : HasId
> fun describe(h: HasId) = h.id     // TaggedId.box(...)
>
> // Декомпилированное представление (упрощённо)
> // public static String process-XYZ(String id) { return id.toUpperCase(); }
> // public static void batchProcess(java.util.List ids) { ... ids.get(i) -> UserId obj ... }
> ```
>
> **Когда применять:**
> - Domain primitives, которые проходят через цепочки function calls (`UserId` в Spring controllers, services, repositories).
> - Числовые типы с unit semantics (`Celsius`, `Meters`, `Seconds`, `Money`) — все арифметические operator-функции инлайнятся.
> - Любые места, где boxing явно не активируется: чистый чейн вызовов, return-типы из репозиториев.
>
> **Подводные камни:**
> - Перегрузки по value class требуют осторожности — mangled names видны из Java коряво (`getValue-impl`), для Java-interop добавляйте `@JvmName`.
> - `List<UserId>` в горячем пути — рассмотрите `LongArray`/`IntArray` или собственный buffer с inline accessor.
> - Передача value class в reflection-based фреймворки (Jackson без KotlinModule, старые версии Hibernate) → boxing + потеря type safety на этапе сериализации.
>
> **Связанные вопросы:** [[Q1]] — общее описание value class; [[Q5]] — boxing при использовании через интерфейс; [[Q10]] — value class в коллекциях.
>
> ---
>
> #### D) Compiler полностью удаляет value class на этапе типизации — в байт-коде остаётся только underlying-тип, никаких следов value class класса нет — ❌ Неверно
>
> **Что на самом деле:** класс остаётся в байт-коде. Он нужен для нескольких сценариев: (1) boxing — когда тип нужно передать через generic/interface, JVM требует объект; (2) рефлексия — `KClass<UserId>` обращается к Class object; (3) `equals`/`hashCode` через `Any` — при `Object.equals(userId)` JVM зовёт виртуальный метод, который требует обёртки. Компилятор удаляет обёртку только в inline-позициях, но сам класс остаётся для остальных случаев.
>
> **Откуда путаница:** упрощённое объяснение «inline class инлайнится везде» приводит к выводу «значит, класса нет в байт-коде». Аналогия с C macros или Rust zero-cost abstractions. Реально JVM не позволяет «полностью убрать» тип, на который могут ссылаться другие байт-кодовые инструкции (`new`, `instanceof`, `getstatic`).
>
> **Если бы это было правдой:** `kotlin.reflect.full.memberProperties` не работала бы для value class, нельзя было бы написать `if (x is UserId)`, и сериализаторы (Jackson, kotlinx.serialization) не могли бы найти класс для регистрации. Реально все эти сценарии работают — значит, класс в байт-коде есть.

## Q4. Какие ограничения у value class?

1. **Ровно одно `val` свойство в primary constructor** (в stable Kotlin; multiple values в preview):

```kotlin
@JvmInline
value class Point(val x: Double)      // OK

@JvmInline
value class Point2D(val x: Double, val y: Double)  // ОШИБКА (пока preview)
```

2. **Нельзя `var` поля**:

```kotlin
@JvmInline
value class Counter(var count: Int)   // ОШИБКА
```

3. **Нельзя наследовать от классов** (только от интерфейсов):

```kotlin
@JvmInline
value class UserId(val value: String) : Any()  // ОШИБКА
```

4. **Нельзя `init` блоки с побочными эффектами** — только валидация:

```kotlin
@JvmInline
value class Age(val years: Int) {
    init {
        require(years >= 0)
        println("Creating age")       // лучше избегать — выполняется часто
    }
}
```

5. **Нельзя backing fields** — свойства должны делегировать underlying-значению:

```kotlin
@JvmInline
value class UserId(val value: String) {
    // val cached: String = ...       // ОШИБКА — backing field запрещён

    val upperCase: String              // OK — computed property
        get() = value.uppercase()
}
```


> [!mcq]
>
> **Вопрос:** Какие ограничения накладывает Kotlin на `value class` в stable релизе?
>
> ---
>
> #### A) Поле должно быть `var`, чтобы поддерживать `copy()`; наследование от классов разрешено; backing fields поддерживаются как у обычного класса — ❌ Неверно
>
> **Что на самом деле:** всё наоборот. Поле обязано быть `val`, не `var` — value class в Kotlin строго immutable. `copy()` генерируется и работает через создание новой инстанции с изменёнными значениями, не требуя `var`. Наследование от классов запрещено (`value class A : SomeClass()` — ошибка компиляции), разрешено только от интерфейсов. Backing fields для дополнительных свойств запрещены — можно объявлять только computed properties (`val foo: String get() = …`).
>
> **Откуда путаница:** из data class, который тоже поддерживает `copy()`, разработчик может вспомнить, что `var` поля там разрешены. Если автоматически перенести интуицию на value class — получится этот неверный вариант.
>
> **Если бы это было правдой:** value class превратился бы в обычный mutable wrapper с потенциалом thread-safety проблем (`val sharedId = UserId("x"); thread { sharedId.value = "y" }`). Immutability — фундамент domain-primitives паттерна; без неё value class терял бы свою главную ценность.
>
> ---
>
> #### B) Можно объявлять несколько `val`-полей (это была причина переименования из inline в value class) — ❌ Неверно
>
> **Что на самом деле:** stable Kotlin (включая 1.9 и 2.0) поддерживает только одно `val`-поле в primary constructor. Multi-field value classes (MFVC) — preview-фича с Kotlin 1.8.20, требующая флага компилятора `-Xvalue-classes`, не production-ready. Переименование `inline class` → `value class` сделано для align с будущим Project Valhalla и подчёркивает семантику «по значению», а не количество полей.
>
> **Откуда путаница:** новость о MFVC в Kotlin 1.8.20 многие восприняли как «теперь у value class может быть много полей», не заметив пометку preview/experimental. Также в Project Valhalla value records поддерживают несколько полей — это даёт ложное ощущение, что и Kotlin уже умеет.
>
> **Если бы это было правдой:** код `value class Point2D(val x: Double, val y: Double)` компилировался бы из коробки без флагов в Kotlin 1.9 stable, а гайды по миграции `data class → value class` массово рекомендовали бы это для DTO. Реально KEEP-237 (MFVC) всё ещё в Preview status, и `kotlinx.serialization` ещё не полностью поддерживает их.
>
> ---
>
> #### C) Запрещены `init` блоки полностью — валидацию приходится делать в factory методах через companion object — ❌ Неверно
>
> **Что на самом деле:** `init` блоки разрешены и активно используются для validation invariants. Ограничение в другом: внутри `init` стоит избегать побочных эффектов (логирование, I/O), поскольку value class может создаваться часто и эти эффекты будут множиться. Также init выполняется при boxing и при первом создании в inline-сайте. Factory методы через companion object — рекомендованный паттерн для сложного создания (нормализация, fallback), но не единственный способ валидации.
>
> **Откуда путаница:** между требованием «не побочные эффекты в init» и «не использовать init вовсе» один шаг. Также гайды по DDD часто продвигают factory-методы (`Email.of(raw: String): Email?`) как best practice, что создаёт впечатление «init нельзя».
>
> **Если бы это было правдой:** код `value class Port(val v: Int) { init { require(v in 1..65535) } }` не компилировался бы — а это идиоматичный пример валидации в каждом Kotlin-туториале (включая официальный kotlinlang.org).
>
> ---
>
> #### D) Ровно одно `val` поле в primary constructor; нельзя наследоваться от классов (только от интерфейсов); запрещены backing fields для дополнительных свойств; запрещён `var`; разрешены `init` блоки (для validation), computed properties и operator-функции — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Stable Kotlin (включая 2.0) накладывает на value class следующий набор ограничений: (1) Ровно одно `val`-поле в primary constructor — `var` запрещён, multi-field только в preview. (2) Наследование разрешено только от интерфейсов — `value class A : SomeClass()` запрещено, поскольку класс-обёртка не может иметь parent class в JVM смысле. (3) Запрет backing fields — нельзя добавить `val cached: String = …`, поскольку это потребовало бы реального объекта; разрешены только `val foo: String get() = …` (computed). (4) `init` блок разрешён и поощряется для валидации (`require(value in 0..100)`). (5) Можно объявлять методы, в том числе operator-функции (`operator fun plus`, `compareTo`). (6) Можно реализовывать интерфейсы — но это активирует boxing при вызове через interface type.
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class Money(val cents: Long) {
>
>     // init для валидации — OK
>     init {
>         require(cents >= 0) { "Money cannot be negative: $cents" }
>     }
>
>     // computed property — OK
>     val dollars: Double get() = cents / 100.0
>
>     // operator function — OK
>     operator fun plus(other: Money) = Money(cents + other.cents)
>
>     // val cached: String = ...  // ОШИБКА: value class has backing field
>     // var counter: Int = 0      // ОШИБКА: var not allowed
>
>     companion object {
>         // factory — OK
>         fun fromDollars(d: Double) = Money((d * 100).toLong())
>     }
> }
>
> // value class Bad(val a: Int, val b: Int)  // ОШИБКА (без preview-флага)
> // value class Inherit(val v: String) : SomeOpenClass()  // ОШИБКА
> ```
>
> **Когда применять:**
> - Всегда добавляйте `init { require(...) }` для invariants — это убирает невалидные состояния из системы типов (impossible states are unrepresentable).
> - Computed properties для derived data (`Percentage.asFraction get() = value / 100.0`).
> - Operator-функции для domain arithmetic (`Money + Money`, `Money * Int`).
> - Companion factory methods для сложного создания с возможным `null` (Email validation, Phone parsing).
>
> **Подводные камни:**
> - Логирование в `init` — плохая идея: при массовом создании value class (например, в цикле) логи захламят систему.
> - Реализация интерфейса полезна для polymorphism, но при вызове через interface type происходит boxing — `fun describe(id: Identifiable)` упакует value class.
> - Computed property без caching — каждый вызов делает вычисление заново; для дорогих операций используйте extension fun или отдельный helper.
> - Companion object на value class — это обычный объект в куче, не value class; на него ссылок без overhead не получится.
>
> **Связанные вопросы:** [[Q1]] — что такое value class; [[Q5]] — реализация интерфейсов и boxing; [[Q15]] — best practices (init validation, factory).

## Q5. Как value class работает с интерфейсами?

```kotlin
interface Identifier {
    val id: String
}

@JvmInline
value class UserId(override val id: String) : Identifier

fun describe(identifier: Identifier) {
    println(identifier.id)
}

val uid = UserId("user-1")
describe(uid)  // здесь uid УПАКОВЫВАЕТСЯ в объект Identifier
```

При использовании через интерфейс теряется преимущество inlining — создаётся обёртка-объект. Это называется "boxing".

**Best practice**: избегать интерфейсов для value-классов, если важна производительность.


> [!mcq]
>
> **Вопрос:** Что происходит с inline-преимуществом `value class`, когда он реализует интерфейс и передаётся через interface type?
>
> ---
>
> #### A) Происходит boxing — value class упаковывается в объект-обёртку, поскольку динамический dispatch через интерфейс требует JVM-объекта; для критичных по производительности путей этого надо избегать — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> JVM реализует динамический dispatch через `invokevirtual`/`invokeinterface`, которые работают с reference-типами в форме объектов. Когда value class реализует интерфейс и метод принимает interface type как параметр (`fun describe(id: Identifier)`), компилятор не может статически знать конкретный тип в сайте вызова — он принимает любой `Identifier`. Чтобы передать туда value class, нужна реальная JVM-обёртка (Java class object с полем underlying-значения), и компилятор автоматически вставляет boxing. Этот сценарий называется «interface-boxing» и описан в Kotlin docs про inline classes. Best practice — если важна производительность, избегать передачи value class через интерфейс; если interface нужен для polymorphism в холодном пути — boxing допустим.
>
> **Пример:**
> ```kotlin
> interface Identifier {
>     val id: String
> }
>
> @JvmInline
> value class UserId(override val id: String) : Identifier
>
> @JvmInline
> value class OrderId(override val id: String) : Identifier
>
> // Прямой вызов — INLINE, String передаётся как примитив
> fun byUserId(id: UserId) = repo.find(id.id)
>
> // Через interface — BOX, UserId упаковывается
> fun describeAny(i: Identifier) = println(i.id)
>
> val u = UserId("u-1")
> byUserId(u)         // bytecode: byUserId-XYZ("u-1")
> describeAny(u)      // bytecode: new UserId("u-1"); describeAny(boxedRef)
>
> // Декомпиляция
> // public static void byUserId-XYZ(String id) { ... }
> // public static void describeAny(Identifier i) { ... }  ← требует объект
> ```
>
> **Когда применять:**
> - Если value class нужен в polymorphic context (общий обработчик нескольких ID-типов), либо принять boxing, либо рефакторить через sealed interface + when expression без виртуального вызова.
> - В DDD-кодовых базах часто общие интерфейсы (`Identifier`, `DomainId`) добавляют для удобства, но в горячих путях лучше работать с конкретным value class напрямую.
> - Если interface часть API контракта (Spring controller method signature `fun process(req: Request)`), оставляйте interface и принимайте boxing — type safety важнее микросекунд.
>
> **Подводные камни:**
> - Boxing на каждом вызове — если метод вызывается миллионы раз/сек (hot path в Kafka consumer), GC pressure заметен. Профилируйте через JFR/async-profiler.
> - При имплементации интерфейса через value class методы интерфейса всегда вызываются через bridge с unboxing — overhead есть даже после JIT.
> - `if (x is Identifier)` для value class — тоже boxing (instanceof требует объекта).
> - `val list: List<Identifier> = listOf(UserId("1"), OrderId("2"))` — все элементы упакованы; для homogeneous-коллекции лучше `List<UserId>` (хотя generic тоже даёт boxing — см. Q10).
>
> **Связанные вопросы:** [[Q3]] — общая семантика inline/box; [[Q4]] — value class может implements interface; [[Q10]] — boxing в коллекциях.
>
> ---
>
> #### B) Никакого boxing не происходит — компилятор Kotlin использует mangled-имена для интерфейсных методов и сохраняет inline-преимущество через специальную таблицу методов — ❌ Неверно
>
> **Что на самом деле:** mangled names используются для overload resolution на собственных методах value class, не для интерфейсных вызовов. JVM не имеет механизма «inline call через интерфейс» в обычном байт-коде. Project Valhalla когда-нибудь принесёт `interface` over inline types, но в текущей реальности boxing неизбежен. Никаких «специальных таблиц методов» в Kotlin нет — компилятор просто вставляет `Type.box-impl(value)` перед вызовом.
>
> **Откуда путаница:** Kotlin compiler делает много магии с mangling и static methods для value class, и можно домыслить, что и интерфейсы покрыты. Также C# с `where T : struct` и Java Valhalla `value record` создают впечатление, что Kotlin тоже «решил эту проблему».
>
> **Если бы это было правдой:** не было бы предупреждения «consider that boxing happens when interface is used» в Kotlin compile-warnings, и в KotlinConf докладах не повторяли бы это как фундаментальное ограничение. Реально же это документированное поведение, прямо описанное в `kotlinlang.org/docs/inline-classes.html`.
>
> ---
>
> #### C) Реализация интерфейса для value class запрещена компилятором — value class не может implements anything — ❌ Неверно
>
> **Что на самом деле:** value class может реализовывать интерфейсы — это разрешено и часто используется. `value class UserId(val v: String) : Identifier, Comparable<UserId>` — совершенно валидный код. Запрет касается только наследования от классов (`value class A : SomeClass()` — ошибка). Реализация интерфейсов — стандартная фича.
>
> **Откуда путаница:** ограничение «нельзя наследоваться от классов» легко перепутать с «нельзя наследоваться вообще». Также value class — это «special» класс, и кажется, что у него много ограничений по сравнению с обычными.
>
> **Если бы это было правдой:** `Comparable<UserId>` для лексикографической сортировки списка UserId-ов был бы невозможен. Реально это работает и широко используется — например, `Money : Comparable<Money>` для `assertTrue(price1 < price2)` в тестах.
>
> ---
>
> #### D) Интерфейсные методы вызываются через `invokestatic` с underlying-значением, поэтому boxing не нужен — это специальное JVM-расширение в Kotlin 1.5+ — ❌ Неверно
>
> **Что на самом деле:** в JVM нет специального расширения для invoke на интерфейсе с inline аргументом. Метод интерфейса `Identifier.getId()` — это виртуальный вызов через interface dispatch (`invokeinterface`), требующий объект. Никакого «invokestatic с underlying» для интерфейсов Kotlin не делает — это противоречило бы JVM spec.
>
> **Откуда путаница:** value class собственные методы действительно компилируются в static (`UserId.getValue-impl`), и можно предположить, что интерфейсные тоже. Также `invokestatic` ассоциируется с быстрыми вызовами без объекта — соблазнительно думать, что Kotlin его использует везде.
>
> **Если бы это было правдой:** мы бы видели в decompile-выводе `INVOKESTATIC` инструкции для каждого `identifier.id` call. Реально там `INVOKEINTERFACE` + box-instructions перед ним. Это легко проверить через «Show Kotlin Bytecode» → «Decompile to Java».

## Q6. В чём разница между @JvmInline value class и обычным typealias?

```kotlin
// typealias — просто синоним, никакой type safety
typealias UserId = String
typealias OrderId = String

fun process(userId: UserId) { }

val orderId: OrderId = "order-1"
process(orderId)  // КОМПИЛИРУЕТСЯ! — они оба String
```

```kotlin
// value class — type safe
@JvmInline value class UserId(val value: String)
@JvmInline value class OrderId(val value: String)

fun process(userId: UserId) { }

val orderId = OrderId("order-1")
// process(orderId)  // ОШИБКА компиляции — UserId != OrderId
process(UserId(orderId.value))  // явное преобразование
```

**Правило**: value class для type safety, typealias — только для удобочитаемости (`ConsumerRecord<String, String>` → `OrderEvent`).


> [!mcq]
>
> **Вопрос:** В чём принципиальная разница между `typealias UserId = String` и `@JvmInline value class UserId(val value: String)`?
>
> ---
>
> #### A) Никакой разницы — оба создают новый тип, который компилятор различает; выбор сводится к стилевым предпочтениям команды — ❌ Неверно
>
> **Что на самом деле:** `typealias` НЕ создаёт новый тип — это исключительно синоним для уже существующего. Компилятор видит `typealias UserId = String` и трактует `UserId` и `String` как полностью взаимозаменяемые, без какой-либо проверки. `value class` создаёт настоящий новый тип в системе типов Kotlin — `UserId` и `String` несовместимы, требуется явное `.value`/`UserId(...)`. Это фундаментальное различие, не стилевое.
>
> **Откуда путаница:** в IDE подсветка работает похоже («умное» автодополнение показывает `UserId` в обоих случаях). Если не написать тест, где случайно передаётся `String` вместо `UserId`, разницу не заметить. Многие гайды первого уровня подают typealias как «лёгкую альтернативу value class».
>
> **Если бы это было правдой:** Knight Capital-style инцидент с перепутанными ID-аргументами (`processOrder(userId, orderId)` vs `processOrder(orderId, userId)`) был бы решён и через typealias. Реально — НЕТ: typealias не ловит такие баги, value class ловит на этапе компиляции.
>
> ---
>
> #### B) `typealias` — синоним без введения нового типа, компилятор не различает `UserId` и `String`; `value class` создаёт новый тип, проверяемый на этапе компиляции, плюс поддерживает `init` валидацию, operator-функции и методы — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `typealias UserId = String` — компилятор просто разворачивает `UserId` в `String` на этапе typecheck. Никакой type safety: `fun process(id: UserId)` принимает любую `String`, перепутывание аргументов незаметно. Нельзя добавить `init { require(...) }`, нельзя сделать `operator fun plus`, нельзя ограничить публичный constructor. `value class UserId(val value: String)` — новый тип. Компилятор требует явного конструктора (`UserId("u-1")`) и явного доступа к underlying (`uid.value`). Можно добавить валидацию в `init`, перегрузить операторы, реализовать интерфейсы. Inline-представление в байт-коде — побочное (но приятное) преимущество, а главное — type safety.
>
> **Пример:**
> ```kotlin
> // Вариант 1: typealias — никакой type safety
> typealias UserIdAlias = String
> typealias OrderIdAlias = String
>
> fun processUserAlias(id: UserIdAlias) { println(id) }
>
> val ord: OrderIdAlias = "order-1"
> processUserAlias(ord)         // КОМПИЛИРУЕТСЯ — оба String
> processUserAlias("raw-str")   // КОМПИЛИРУЕТСЯ — тоже String
>
> // Вариант 2: value class — строгий контроль
> @JvmInline value class UserIdVc(val value: String) {
>     init { require(value.isNotBlank()) }
> }
>
> @JvmInline value class OrderIdVc(val value: String)
>
> fun processUserVc(id: UserIdVc) { println(id.value) }
>
> val ord2 = OrderIdVc("order-1")
> // processUserVc(ord2)        // ОШИБКА: required UserIdVc, found OrderIdVc
> // processUserVc("raw-str")   // ОШИБКА: required UserIdVc, found String
> processUserVc(UserIdVc("u-1")) // OK
> ```
>
> **Когда применять:**
> - `value class` — для domain primitives, где важно отделить семантически разные значения (UserId vs OrderId vs ProductId).
> - `typealias` — для именования сложных generic-типов или функциональных типов: `typealias ResultHandler = (Either<Error, Data>) -> Unit`, `typealias UserCache = Map<UserId, User>`. Здесь главная цель — читаемость, а type safety уже обеспечена структурой.
> - В Spring/Ktor контроллерах для path-params используйте value class — Jackson/Kotlin Module нативно поддерживает их через `KotlinModule`.
>
> **Подводные камни:**
> - `typealias` не помогает с перепутыванием аргументов одного underlying-типа. Если у вас три String-id, и `typealias UserId = String`, `typealias OrderId = String`, `typealias ProductId = String` — компилятор спокойно пропустит передачу любой строки куда угодно.
> - Конверсия `String → UserId` в Spring контроллере (`@PathVariable id: UserId`) требует регистрации `Converter<String, UserId>` или Kotlin Module — без него получите 400 Bad Request с криптическим стектрейсом.
> - `value class` не серилизуется напрямую в JPA — нужен `AttributeConverter` (см. Q7).
>
> **Связанные вопросы:** [[Q1]] — общее описание value class; [[Q7]] — value class в JPA/Spring; [[Q14]] — когда typealias достаточно.
>
> ---
>
> #### C) `value class` создаёт новый тип, но плохо работает с Java-кодом, поэтому в смешанных проектах правильнее всегда использовать `typealias` — ❌ Неверно
>
> **Что на самом деле:** value class работает с Java, просто через mangled-имена и явный underlying-тип. Из Java вы будете видеть `UserId.getValue-impl(String)` вместо красивого `UserId.getValue()` — некрасиво, но рабочее. Для interop добавляют `@JvmName` для красивых имён или `@JvmField` для прямого доступа к полю. Использовать typealias «потому что Java» — false dichotomy: typealias из Java вообще не виден (Java видит сразу underlying-тип, ничего не зная об alias).
>
> **Откуда путаница:** mangled-имена в Java действительно неуклюжи. Если посмотреть `UserId.class` через javap или из Java code, картина пугает. Создаётся впечатление «нужно избегать value class в смешанных проектах».
>
> **Если бы это было правдой:** Spring Boot Kotlin проекты с Java-частью не использовали бы value class — но они используют (например, dependency на legacy Java-модуль с DTO). Решение — `@JvmName("getId") fun getValue(): String = value` или вынос Kotlin-only кода в отдельный модуль.
>
> ---
>
> #### D) `typealias` создаёт новый тип, проверяемый компилятором, а `value class` — лишь syntax sugar для wrapping без проверок — ❌ Неверно
>
> **Что на самом деле:** утверждение перевёрнуто. `typealias` — это синоним без введения типа, компилятор не различает alias и оригинал. `value class` — новый тип с компиляторной проверкой. Если попытаться передать `String` туда, где ждут `UserId` (value class), компиляция упадёт; если в `UserIdAlias` — успешно.
>
> **Откуда путаница:** реверс правильного утверждения. Если человек помнит, что «одно из них даёт type safety, а другое — нет», но забыл какое, может выбрать неверный вариант.
>
> **Если бы это было правдой:** все Kotlin DDD книги (Effective Kotlin, Kotlin in Action) учили бы использовать typealias для domain primitives. Реально они единодушно рекомендуют value class.

## Q7. Как использовать value class с Spring и JPA?

```kotlin
@JvmInline
value class OrderId(val value: Long)

@Entity
class Order(
    @Id @GeneratedValue
    @Convert(converter = OrderIdConverter::class)
    val id: OrderId,
    val customerId: String
)

@Converter(autoApply = false)
class OrderIdConverter : AttributeConverter<OrderId, Long> {
    override fun convertToDatabaseColumn(attribute: OrderId?): Long? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: Long?): OrderId? =
        dbData?.let { OrderId(it) }
}
```

```kotlin
// В REST контроллере — автоматическая десериализация через Jackson
@RestController
class OrderController {

    @GetMapping("/orders/{id}")
    fun getOrder(@PathVariable id: Long): Order {
        return orderService.findById(OrderId(id))
    }
}

// Kotlin + Jackson module для нативной поддержки
@Bean
fun kotlinModule(): KotlinModule = KotlinModule.Builder().build()
```

**Важно**: JPA и Jackson из коробки не понимают value class — нужны конвертеры/custom serializers.


> [!mcq]
>
> **Вопрос:** Что нужно сделать, чтобы JPA-entity с полем `value class OrderId` корректно работала с базой данных через Hibernate?
>
> ---
>
> #### A) Ничего — Hibernate 6+ автоматически распаковывает любые Kotlin value classes в underlying-тип без дополнительной настройки — ❌ Неверно
>
> **Что на самом деле:** Hibernate (включая 6.x) НЕ имеет нативной поддержки Kotlin value classes. Hibernate работает с Java-типами через `BasicType`/`UserType`/`AttributeConverter`, и Kotlin-специфичный inline трюк ему неизвестен. Без явной конфигурации поле `val id: OrderId` либо упадёт в runtime с `MappingException: Could not determine type for: OrderId`, либо Hibernate попытается сохранить как Serializable blob — что почти всегда не то, что нужно.
>
> **Откуда путаница:** Hibernate 6 добавил много новых типов и более умную автоконвертацию (`java.time`, enum, UUID). Kotlin developers могут предположить, что и value class попал в этот список. Также `kotlin-jpa` plugin создаёт illusion «всё работает из коробки» — но он только добавляет no-arg constructors, не помогает с value class.
>
> **Если бы это было правдой:** мы бы не видели массы StackOverflow-вопросов про «JPA + Kotlin value class converter». Реально это одна из самых частых проблем при миграции Spring Boot Kotlin сервисов с String-id на типизированные id. Production-инциденты: «сервис стартует, первый запрос крашится с MappingException».
>
> ---
>
> #### B) Нужно добавить `@JvmField` к полю в entity — это заставит Hibernate использовать прямой доступ к underlying-значению — ❌ Неверно
>
> **Что на самом деле:** `@JvmField` отключает Kotlin-генерированные getter/setter и делает поле public — это нужно для Java-interop, не для JPA. Hibernate работает не через прямой доступ к полю, а через метаданные типов (`BasicType` lookup), и `@JvmField` никак не помогает с маппингом OrderId на BIGINT. Также `@JvmField` несовместим с `val` в value class — value class не разрешает `@JvmField` на единственном поле, поскольку Kotlin делает свои inline-преобразования.
>
> **Откуда путаница:** `@JvmField` ассоциируется с «убрать обёртку, работать с raw полем», и кажется, что это решит проблему «Hibernate не понимает value class». На самом деле проблема не в getter/setter, а в отсутствии BasicType для OrderId — `@JvmField` это не решает.
>
> **Если бы это было правдой:** аннотация была бы документирована как JPA-фича в Hibernate User Guide. Реально `@JvmField` упоминается только в Kotlin/Java interop секции и никогда — в JPA-секции.
>
> ---
>
> #### C) Зарегистрировать `AttributeConverter<OrderId, Long>` с `@Converter` и привязать его к полю через `@Convert(converter = OrderIdConverter::class)` (или autoApply = true) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> JPA `AttributeConverter<X, Y>` — стандартный механизм маппинга кастомных типов на JDBC-типы (`X` — entity attribute, `Y` — database column). Создаёте класс, реализующий `convertToDatabaseColumn(OrderId): Long` и `convertToEntityAttribute(Long): OrderId`, помечаете его `@Converter`. Затем либо ставите `autoApply = true` (применится ко всем полям OrderId автоматически), либо явно указываете `@Convert(converter = OrderIdConverter::class)` на конкретном поле. Hibernate вызовет конвертер на каждом read/write, и в базе будет лежать `BIGINT`, а в Kotlin коде — типобезопасный `OrderId`. Альтернативно — Hibernate 6 `UserType` для большего контроля, но `AttributeConverter` достаточно в 95% случаев.
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class OrderId(val value: Long)
>
> @Converter(autoApply = true)
> class OrderIdConverter : AttributeConverter<OrderId, Long> {
>     override fun convertToDatabaseColumn(attribute: OrderId?): Long? =
>         attribute?.value
>
>     override fun convertToEntityAttribute(dbData: Long?): OrderId? =
>         dbData?.let { OrderId(it) }
> }
>
> @Entity
> @Table(name = "orders")
> class Order(
>     @Id
>     // autoApply=true → @Convert не нужен
>     val id: OrderId,
>
>     @Column(name = "customer_id")
>     val customerId: String,
>
>     @Column(name = "total_cents")
>     val total: Long
> )
>
> // Использование
> val order = orderRepository.findById(OrderId(42L)).orElseThrow()
> println(order.id.value)  // 42L
> ```
>
> **Когда применять:**
> - Любая Spring Data JPA / Hibernate entity с value class в поле (id, foreign key, доменный тип).
> - `autoApply = true` — когда конвертер нужен для всех использований этого value class. Удобно, минимизирует boilerplate.
> - Явный `@Convert(converter = X::class)` — когда один value class может маппиться по-разному в разных entity (редко) или нужно отключить autoApply для конкретного поля.
>
> **Подводные камни:**
> - `@Converter(autoApply = true)` глобально применяется — если есть две entity с одинаковым value class, но в одной из них вы хотите другой маппинг, это не получится через autoApply. Решение — autoApply=false и явный @Convert.
> - Конвертер вызывается на каждый read/write — для горячих путей это overhead. Профилируйте.
> - Jackson и Hibernate — это две разные конфигурации. JPA-конвертер не делает value class сериализуемым в JSON; для REST API дополнительно нужен `KotlinModule.Builder().build()` для ObjectMapper или custom deserializer.
> - В Spring Data JDBC (не JPA) — другой механизм: `AggregateReference` / `Converter<X, Y>`, регистрируется через `JdbcCustomConversions`.
>
> **Связанные вопросы:** [[Q1]] — общее описание value class; [[Q8]] — UserId/OrderId как типовое применение; [[Q14]] — когда value class неоправдан без конвертеров.
>
> ---
>
> #### D) Заменить value class на `data class` — JPA понимает data class из коробки и обработает его как embeddable — ❌ Неверно
>
> **Что на самом деле:** `data class` тоже не понимается JPA автоматически как embeddable — для embeddable нужна `@Embeddable` аннотация и часто `@AttributeOverrides`. Замена value class на data class решит проблему типа «Hibernate не видит OrderId», только если добавите `@Embeddable` + правильно настроите маппинг. Но тогда теряется главное преимущество value class — inline-представление; data class всегда heap-allocated, и для domain primitives это oversized. Правильное решение — оставить value class и добавить AttributeConverter.
>
> **Откуда путаница:** в Spring Boot туториалах `data class` используется массово для DTO и сущностей, и кажется, что «JPA дружит с data class». Но дружба ограничена: для top-level entity нужен `@Entity`, для встроенного типа — `@Embeddable`, для single-value wrapper — `@Convert` или `UserType`. Никакой автомагии нет.
>
> **Если бы это было правдой:** community-обсуждения «JPA + Kotlin value class» не появились бы, рекомендация «просто используйте data class» закрыла бы вопрос. Реально data class даёт похожий boilerplate (`@Embeddable @AttributeOverrides`) и теряет inline-преимущество — никто так не делает.

## Q8. Какие типичные применения value class?

```kotlin
// 1. Идентификаторы
@JvmInline value class UserId(val value: String)
@JvmInline value class OrderId(val value: Long)

// 2. Деньги (избежать double со всеми известными проблемами)
@JvmInline
value class Cents(val value: Long) {
    operator fun plus(other: Cents) = Cents(value + other.value)
    operator fun minus(other: Cents) = Cents(value - other.value)
    fun toDollars(): Double = value / 100.0
}

// 3. Единицы измерения
@JvmInline value class Meters(val value: Double)
@JvmInline value class Seconds(val value: Double)

fun speed(distance: Meters, time: Seconds): Double =
    distance.value / time.value

// 4. Параметры с ограничениями
@JvmInline
value class Percentage(val value: Int) {
    init { require(value in 0..100) }
}

@JvmInline
value class NonEmptyString(val value: String) {
    init { require(value.isNotEmpty()) }
}

// 5. Обёртки для domain-specific строк
@JvmInline value class IpAddress(val value: String)
@JvmInline value class Hostname(val value: String)
@JvmInline value class ApiKey(val value: String)

fun connect(ip: IpAddress, port: Int) { }
// connect(Hostname("localhost"), 8080)  // ОШИБКА — типы разные
```


> [!mcq]
>
> **Вопрос:** Какой из перечисленных сценариев — НЕ типовое применение `value class`?
>
> ---
>
> #### A) Domain primitives: `UserId`, `OrderId`, `Email`, `Money(val cents: Long)` с operator-функциями и валидацией в init — ❌ Неверно (это типичное применение)
>
> **Что на самом деле:** это самый распространённый use-case value class. Domain primitives — основная мотивация для введения этой фичи в Kotlin. Они дают type safety (`UserId` vs `OrderId` не перепутать), валидацию инвариантов (`Email.init { require(contains("@")) }`), удобное API для operator-функций (`Money + Money`). Все Kotlin DDD книги (Effective Kotlin, Kotlin in Action) ставят domain primitives в первый ряд применений.
>
> **Откуда путаница:** вопрос про «не типовое применение» — этот вариант это типовое.
>
> **Если бы это было правдой:** Kotlin community не строил бы вокруг этого паттерна целые библиотеки (kotools.types, arrow-core refinement types). Реально domain primitives — топовая причина перехода с raw String/Long на value class.
>
> ---
>
> #### B) Единицы измерения: `Meters(val value: Double)`, `Seconds(val value: Double)`, `Celsius(val value: Double)` для предотвращения смешивания систем единиц — ❌ Неверно (это типичное применение)
>
> **Что на самом деле:** unit types — классическое применение value class. Mars Climate Orbiter (1999, $327M loss) — это потеря миссии из-за перепутывания imperial и metric единиц измерения, которое value class предотвратил бы на этапе компиляции. В Kotlin Time API (`Duration`, `kotlin.time.Duration` представлена через value class) этот паттерн используется в стандартной библиотеке.
>
> **Откуда путаница:** этот вариант это типовое применение.
>
> **Если бы это было правдой:** `kotlin.time.Duration` в stdlib не была бы реализована как `@JvmInline value class Duration(val rawValue: Long)`. Реально — реализована именно так.
>
> ---
>
> #### C) Параметры с ограничениями: `Percentage(val v: Int) { init { require(v in 0..100) } }`, `Port(val v: Int) { init { require(v in 1..65535) } }`, `NonEmptyString` — ❌ Неверно (это типичное применение)
>
> **Что на самом деле:** refinement types через value class + init validation — стандартный паттерн «impossible states are unrepresentable». Если функция принимает `Port`, она гарантированно работает с валидным портом — не нужны runtime-проверки в каждом вызове.
>
> **Откуда путаница:** этот вариант это типовое применение.
>
> **Если бы это было правдой:** библиотеки типа kotools.types (NonEmptyString, PositiveInt, NonNegativeDouble) — это упражнение во value class + init validation. Реально это активно используется.
>
> ---
>
> #### D) Замена обычных составных DTO с 3-5 полями (`Address(street, city, zip, country)`) на multi-field value class для уменьшения GC pressure — ✓ Верно (это НЕ типичное применение)
>
> **Развёрнутое объяснение:**
>
> Composite DTO с несколькими полями — НЕ применение value class. В stable Kotlin value class требует ровно одно `val`-поле; multi-field value classes (MFVC) появились в Kotlin 1.8.20 как preview-фича и требуют флага компилятора `-Xvalue-classes`, не production-ready. Кроме того, даже когда MFVC станет stable, его применение ограничено: для `Address(street, city, zip)` обычный `data class` адекватен — это плоская immutable структура, а MFVC даст marginal performance-выигрыш и больше boxing-сюрпризов в коллекциях. Реальные применения value class: одно-полевые wrappers (UserId, Email, Money, Percentage, Port). Для составных доменных объектов — data class.
>
> **Пример:**
> ```kotlin
> // НЕПРАВИЛЬНО: попытка использовать value class для composite DTO
> // @JvmInline
> // value class Address(val street: String, val city: String, val zip: String)
> // → error: value class primary constructor must have only one final read-only (val) property
>
> // ПРАВИЛЬНО: data class для composite, value class для domain primitives внутри
> @JvmInline value class Street(val value: String)
> @JvmInline value class City(val value: String)
> @JvmInline value class Zip(val value: String) {
>     init { require(value.matches(Regex("\\d{5}"))) }
> }
>
> data class Address(
>     val street: Street,
>     val city: City,
>     val zip: Zip
> )
>
> // GC-pressure для composite DTO решается profiler-driven подходами:
> // - Object pools (если объекты дёшево пересоздаются и hot path)
> // - Mutation-friendly buffers (StringBuilder, ByteBuffer для serialization paths)
> // - НЕ value class — он не для этого
> ```
>
> **Когда применять:**
> - **value class** — для одного-полевых обёрток: ID, Email, Money, Percentage, Meters, Seconds, IpAddress.
> - **data class** — для составных immutable структур: Address, OrderLine, Coordinate, UserProfile.
> - Комбинируйте: внутри `data class` можно (и нужно) использовать `value class` для типизации полей.
>
> **Подводные камни:**
> - Соблазн «переписать все DTO на value class» — pitfall. MFVC preview не решает реальной проблемы GC pressure для composite DTO; решение там другое (профилирование, pooling).
> - Не использовать value class для callback типов (`TaskHandler = (Task) -> Unit`) — для функциональных типов есть typealias.
> - Иногда люди обворачивают value class в value class (`OrderIdHash(val v: UserId)`) — это допустимо, но усложняет понимание; обычно достаточно одного уровня wrapping.
>
> **Связанные вопросы:** [[Q1]] — что такое value class; [[Q2]] — отличие от data class; [[Q14]] — когда не стоит использовать value class.

## Q9. Чем value class Kotlin отличается от record Java 16+?

| Критерий | Kotlin `value class` | Java `record` |
|----------|----------------------|---------------|
| Количество полей | Одно (stable) | Любое |
| JVM представление | Inlined (часто) | Обычный класс в куче |
| Immutability | Да (только `val`) | Да (final поля) |
| equals/hashCode | Автогенерация | Автогенерация |
| Наследование | Только от интерфейса | Только `Record` + интерфейсы |
| Destructuring | componentN() | нет (есть pattern matching с Java 21) |
| Основная цель | Type-safe обёртки без overhead | Compact immutable data carriers |

**Совместимость**: можно использовать оба в одном проекте.


> [!mcq]
>
> **Вопрос:** Чем Kotlin `@JvmInline value class` принципиально отличается от Java `record` (Java 16+)?
>
> ---
>
> #### A) `value class` в stable Kotlin ограничен одним полем и в большинстве позиций инлайнится в underlying-тип, у `record` — любое количество полей и он всегда heap-allocated; record оптимизирован под compact immutable data, value class — под type-safe обёртки без overhead — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Java `record` (стандарт с JDK 16, доступен с JDK 14 как preview) — это immutable nominal data carrier. Поддерживает любое разумное число полей, автоматически генерирует canonical constructor, accessors (одним методом — `name()` вместо `getName()`), `equals/hashCode/toString`. На JVM record — обычный класс, унаследованный от `java.lang.Record`, всегда heap-allocated. Pattern matching (`switch` с record-patterns в JDK 21) — отличительная фича.
>
> Kotlin `@JvmInline value class` — type-safe wrapper над одним значением. В stable релизе ограничен одним `val`-полем, инлайнится в underlying-тип в большинстве позиций (boxing только для generics/nullable/interface). Не имеет автоматического pattern matching, но даёт zero-overhead type safety.
>
> Они решают разные задачи: record — для compact immutable data structures (Address, OrderLine, DTO), value class — для domain primitives (UserId, Money, Percentage). Часто используются вместе: `record Address(Street street, City city, Zip zip)` (Java) или `data class Address(val street: Street, val city: City, val zip: Zip)` (Kotlin), где Street/City/Zip — value class.
>
> **Пример:**
> ```kotlin
> // Kotlin value class — domain primitive
> @JvmInline
> value class UserId(val value: String)
>
> // Kotlin data class — composite
> data class User(val id: UserId, val email: String, val age: Int)
> ```
> ```java
> // Java record — composite immutable data
> public record User(String id, String email, int age) {
>     public User {
>         if (age < 0) throw new IllegalArgumentException();
>     }
> }
>
> // Pattern matching (JDK 21+)
> if (obj instanceof User(String id, String email, int age) && age >= 18) {
>     // ...
> }
> ```
>
> **Когда применять:**
> - **record** — для composite immutable data: DTOs для REST API, intermediate aggregates в pipeline, value objects с >1 поля.
> - **value class** — для domain primitives: ID-обёртки, единицы измерения, refinement types (Percentage, Port).
> - Совместное использование: record/data class composes value class — `record Order(OrderId id, UserId customer, Money total)`.
>
> **Подводные камни:**
> - Java record не решает MFVC-проблему: он всё равно heap-allocated. Project Valhalla когда-то принесёт value records — но не скоро.
> - Из Java вызов методов Kotlin value class — через mangled-имена (`getValue-impl`), некрасиво. Из Kotlin вызов Java record — обычные accessors, всё ок.
> - Pattern matching из Kotlin для Java record работает через `is`/`when` (Kotlin), но без deconstruction — это Java-фича.
> - В микросервисах с DTO: record и data class взаимозаменяемы, выбор зависит от primary language модуля.
>
> **Связанные вопросы:** [[Q1]] — что такое value class; [[Q2]] — отличия от data class; [[Q4]] — ограничения value class.
>
> ---
>
> #### B) Они полностью эквивалентны: `value class` — это Kotlin название для Java `record`, генерирующий тот же байт-код — ❌ Неверно
>
> **Что на самом деле:** разный байт-код. Kotlin value class генерирует обычный class с mangled static methods и в большинстве позиций инлайнится. Java record создаёт класс, наследующийся от `java.lang.Record`, с canonical constructor и accessor-методами — этот класс всегда heap-allocated, никакого inlining нет. Decompile-вывод этих типов выглядит совершенно по-разному.
>
> **Откуда путаница:** оба keyword обозначают «упрощённый immutable type», оба автогенерируют equals/hashCode/toString. Поверхностное сходство есть, но реализация и use cases разные.
>
> **Если бы это было правдой:** Kotlin Java interop вокруг value class был бы тривиальным (общий байт-код, общая семантика). Реально interop требует `@JvmName` и понимания mangled names — это и есть свидетельство того, что байт-код разный.
>
> ---
>
> #### C) `value class` поддерживает любое количество полей, `record` ограничен одним полем — ❌ Неверно
>
> **Что на самом деле:** всё ровно наоборот. `record` поддерживает любое разумное число полей (`record Point(int x, int y, int z)` — норма). `value class` в stable Kotlin требует ровно одного `val`-поля; MFVC (multi-field value classes) — preview-фича.
>
> **Откуда путаница:** реверс правильного утверждения. Без знания конкретной реализации легко перепутать «что чем ограничено». Также есть psychological bias: value class звучит как «more advanced», и кажется что он мощнее.
>
> **Если бы это было правдой:** существующие Java codebases с многими record-классами для DTO (Spring Boot 3+ туториалы) были бы невозможны. Реально record — отличный инструмент для composite immutable DTO, value class — для domain primitives. Это разные ниши.
>
> ---
>
> #### D) `record` доступен в Java 8+ и поддерживает наследование от любого класса; `value class` доступен только в Kotlin 1.9+ и не поддерживает destructuring — ❌ Неверно
>
> **Что на самом деле:** record доступен с JDK 16 (preview с JDK 14), не с Java 8. Record не поддерживает наследование от классов (только implicit `Record`-родитель + интерфейсы). Value class доступен с Kotlin 1.5 (как `value class` keyword; до этого был `inline class` с Kotlin 1.3). Value class поддерживает destructuring через `component1()` — `val (v) = userId` работает.
>
> **Откуда путаница:** в этом варианте несколько фактических ошибок сразу. Без сверки с документацией легко поверить «record старый, value class новый», или «value class — это same as inline class из старых версий». Реально оба относительно новые и каждый имеет свои ограничения.
>
> **Если бы это было правдой:** record был бы в любом Java 8 проекте, но реально он недоступен в legacy enterprise codebases на JDK 8/11. Также `val (id) = UserId("x")` работает уже сейчас — destructuring есть.

## Q10. Как value class взаимодействует с коллекциями?

```kotlin
@JvmInline
value class UserId(val value: String)

// При boxing'е в List теряется преимущество inlining
val ids: List<UserId> = listOf(UserId("1"), UserId("2"))
// Внутри: List<Object> с UserId-обёртками

// Для примитивов — лучше Array или specialized-коллекции
@JvmInline value class Count(val value: Int)

val counts: IntArray = intArrayOf(1, 2, 3)  // без boxing
// val counts: Array<Count> = arrayOf(Count(1), Count(2))  // с boxing

// Workaround для хранения множества без boxing — ByteArray/IntArray
class UserIdBuffer(private val values: Array<String>) {
    fun get(index: Int): UserId = UserId(values[index])
    fun size(): Int = values.size
}
```


> [!mcq]
>
> **Вопрос:** Что происходит, когда `value class UserId(val value: String)` помещается в `List<UserId>`?
>
> ---
>
> #### A) Кoтлин использует специализированную коллекцию `ValueClassList<UserId>`, которая хранит underlying-значения без boxing — ❌ Неверно
>
> **Что на самом деле:** в Kotlin stdlib нет специализированной `ValueClassList`. `List<UserId>` — это обычный `java.util.List<Object>`, в который значения попадают как обёрнутые UserId-объекты. Specializations есть только для примитивов (`IntArray`, `LongArray`, `DoubleArray`) — это JVM-native фича, не extension Kotlin. Для value class с примитивным underlying-типом теоретически можно сделать custom buffer, но stdlib его не предоставляет.
>
> **Откуда путаница:** Kotlin делает много магии вокруг value class, и логично предположить «они и коллекции придумали». Аналогия с `IntArray` (Kotlin-specific name для `int[]`) подкрепляет иллюзию. Реально специализированные коллекции — это про примитивы, не про value class.
>
> **Если бы это было правдой:** в Kotlin docs упоминалась бы `ValueClassList`, и каждый второй Q&A на StackOverflow про «boxing в List<UserId>» был бы закрыт ссылкой на эту коллекцию. Реально вопрос остаётся открытым, и решения — кастомные.
>
> ---
>
> #### B) Происходит boxing — каждый `UserId` упаковывается в обычный объект-обёртку, поскольку generics в JVM используют erasure до `Object`; преимущество inline теряется, но type safety на этапе компиляции сохраняется — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> JVM generics реализованы через type erasure: на уровне байт-кода `List<UserId>` неотличим от `List<Object>`. Все элементы хранятся как reference-типы, поэтому value class в коллекции упаковывается в UserId-обёртку (там же, где компилятор вызывает `UserId.box-impl(value)`). При итерации или доступе по индексу — unboxing для использования. Это типичная для JVM проблема (та же с `List<Integer>` vs `IntArray`), и Kotlin её не решает магически. Type safety сохраняется — компилятор всё равно различает `List<UserId>` и `List<OrderId>`, не даст случайно подмешать.
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class UserId(val value: String)
>
> // Прямой параметр — INLINE, нет boxing
> fun loadOne(id: UserId) = repo.find(id.value)
>
> // List — BOX, каждый UserId упакован
> fun loadMany(ids: List<UserId>) {
>     for (id in ids) {              // unbox at access
>         repo.find(id.value)         // inline снова при передаче
>     }
> }
>
> // Альтернатива для горячих путей — Array<String> с обёрткой
> class UserIdArray(private val values: Array<String>) {
>     val size: Int get() = values.size
>     operator fun get(i: Int): UserId = UserId(values[i])  // inline на get
> }
>
> // Декомпилированное представление
> // public static void loadMany(java.util.List ids) {
> //     for (Object o : ids) {
> //         String value = ((UserId) o).getValue-impl();   // unbox
> //         repo.find-XYZ(value);                            // inline call
> //     }
> // }
> ```
>
> **Когда применять:**
> - Для холодных путей (`List<UserId>` в DTO, передаваемой раз в HTTP-запрос) — boxing незаметен.
> - Для горячих путей (Kafka consumer, миллион сообщений/сек) — рассмотрите `Array<String>` с inline accessor, либо `LongArray` если underlying — Long.
> - Не пытайтесь использовать `Sequence<UserId>` для избегания boxing — sequences тоже используют generic Object для элементов.
>
> **Подводные камни:**
> - `Map<UserId, User>` — boxing для ключей; если ключи — Long, можно использовать `Long2ObjectMap` из Eclipse Collections / Koloboke.
> - `Set<UserId>` с большим количеством элементов — accumulates boxed objects, может стать заметным в heap dump.
> - `flatMap`, `map`, `filter` на `List<UserId>` — все промежуточные коллекции тоже boxed; для длинных цепочек последствия суммируются.
> - JIT иногда устраняет boxing через escape analysis, но это не гарантировано — нельзя на это полагаться при capacity planning.
>
> **Связанные вопросы:** [[Q3]] — общая семантика inline/box; [[Q5]] — boxing через интерфейс; [[Q12]] — value class в suspend функциях.
>
> ---
>
> #### C) Компилятор предупреждает об ошибке и требует использовать `Array<UserId>` вместо `List<UserId>` — `List` несовместим с value class — ❌ Неверно
>
> **Что на самом деле:** `List<UserId>` совершенно валидный код, никаких ошибок или предупреждений компилятор не выдаёт. Просто внутри происходит boxing — это семантически прозрачно для разработчика, но имеет performance implications. `Array<UserId>` тоже даёт boxing (в Java/Kotlin Array<RefType> — это Object[]); реально без boxing работают только primitive arrays (`LongArray`, `IntArray`).
>
> **Откуда путаница:** запрет звучит «защитнее» — если бы компилятор предупреждал, разработчики бы знали о boxing. Реально это silent — нужно знать или профилировать.
>
> **Если бы это было правдой:** Spring Data JPA вообще не работал бы с value class в коллекциях, и `findByCustomerIdIn(List<CustomerId>)` запросы вызывали бы compile errors. Реально это типовой паттерн, который работает (с overhead boxing).
>
> ---
>
> #### D) Boxing происходит, но только при первом обращении к элементу — Kotlin использует lazy unboxing с кэшированием — ❌ Неверно
>
> **Что на самом деле:** boxing происходит при добавлении элемента в коллекцию (`list.add(userId)` → `list.add(UserId.box-impl(userId))`), а unboxing — при каждом доступе (`list.get(0)` → `((UserId)obj).getValue-impl()`). Никакого lazy/cached поведения нет. Каждый элемент в коллекции хранится как boxed-объект всегда.
>
> **Откуда путаница:** «lazy» и «caching» — популярные оптимизационные паттерны в Kotlin (`lazy { }`, `LRUCache`), и хочется домыслить, что они тут применяются. Реально boxing/unboxing в коллекциях работает примитивно — без кэширования.
>
> **Если бы это было правдой:** memory footprint `List<UserId>` был бы оптимизирован — реально каждый элемент = boxed UserId object (16-32 bytes + UserId-wrapper + underlying String). Это видно в heap dump через VisualVM.

## Q11. Почему value class часто требует @JvmInline?

`@JvmInline` — аннотация, указывающая компилятору использовать inlined-представление на JVM.

```kotlin
// ОБЯЗАТЕЛЬНО для Kotlin 1.5+ на JVM
@JvmInline
value class UserId(val value: String)

// БЕЗ @JvmInline — ОШИБКА компиляции
value class UserId(val value: String)  // error: @JvmInline required
```

Причина: `value class` — более общий концепт (Project Valhalla в будущем). `@JvmInline` — конкретная реализация для JVM. Kotlin/Native и Kotlin/JS могут использовать value class без аннотации.


> [!mcq]
>
> **Вопрос:** Зачем для JVM target нужна аннотация `@JvmInline` перед `value class`?
>
> ---
>
> #### A) `@JvmInline` опциональна — компилятор Kotlin сам решает, инлайнить ли value class в зависимости от размера underlying-типа — ❌ Неверно
>
> **Что на самом деле:** для JVM target `@JvmInline` обязательна. Без неё `value class UserId(val value: String)` не скомпилируется — компилятор выдаст ошибку «Value class without @JvmInline annotation is not yet supported». Это не опциональная оптимизация, а обязательное указание target platform compiler-у на конкретное представление.
>
> **Откуда путаница:** в Kotlin/Native и Kotlin/JS аннотация не требуется (value class по умолчанию использует inline-представление для этих платформ). Если основной опыт у разработчика на multiplatform — он может предположить, что и для JVM это опционально.
>
> **Если бы это было правдой:** существующий код `value class UserId(val value: String)` компилировался бы без `@JvmInline`. Попробуйте — получите ошибку. Это легко проверяемое утверждение.
>
> ---
>
> #### B) `@JvmInline` нужна только для совместимости с Java — без неё value class работает, но Java не видит его как обычный класс — ❌ Неверно
>
> **Что на самом деле:** Java-видимость зависит от других механизмов (`@JvmName`, `@JvmField`, `@JvmStatic`), не от `@JvmInline`. `@JvmInline` управляет inline-представлением на JVM target. Без аннотации для JVM target вы получите compile error, а не «работает, но Java не видит».
>
> **Откуда путаница:** префикс `@Jvm…` ассоциируется с Java interop (`@JvmName`, `@JvmStatic` — действительно про Java visibility). Логично домыслить, что и `@JvmInline` про то же.
>
> **Если бы это было правдой:** Kotlin-only проекты (без Java) могли бы писать `value class` без аннотации. Реально — нет, для JVM target обязательно. Также Java-видимость value class в любом случае требует mangled-имён или `@JvmName` для красивых accessor-ов.
>
> ---
>
> #### C) `@JvmInline` явно указывает компилятору использовать inline-представление на JVM; `value class` — более общий концепт (потенциально для будущих JVM value types через Project Valhalla), и `@JvmInline` фиксирует конкретный механизм для текущей реализации — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Kotlin team разделил концепции «value class в системе типов Kotlin» (общий термин, описывающий semantics «равенство по содержанию, не по identity») и «конкретный механизм реализации на target platform». `@JvmInline` — это маркер, который фиксирует: «на JVM реализовать через inline (compile-time подстановка underlying-значения)». В будущем, когда Project Valhalla принесёт настоящие JVM value types, может появиться другая стратегия (`@JvmValhalla` или подобное), и существующий `@JvmInline` останется для backward compatibility. На Kotlin/Native и Kotlin/JS аннотация не нужна, поскольку там нет проблемы выбора между разными стратегиями реализации.
>
> **Пример:**
> ```kotlin
> // ОБЯЗАТЕЛЬНО для JVM target
> @JvmInline
> value class UserId(val value: String)
>
> // БЕЗ @JvmInline:
> // value class UserId(val value: String)
> // error: Value class without @JvmInline annotation is only supported in K/N and K/JS targets
>
> // Multiplatform code
> // commonMain — общий код
> expect value class UserId(val value: String)
>
> // jvmMain — JVM реализация
> @JvmInline
> actual value class UserId(actual val value: String)
>
> // nativeMain — Native реализация, аннотация не нужна
> actual value class UserId(actual val value: String)
> ```
>
> **Когда применять:**
> - Всегда для `value class` на JVM target — без аннотации не скомпилируется.
> - В multiplatform проектах `@JvmInline` пишется только в `jvmMain` source-set, в `commonMain` его быть не должно.
> - При миграции с старого `inline class` (Kotlin 1.4 и ниже) на новый синтаксис: `inline class X(val v: …) {}` → `@JvmInline value class X(val v: …) {}`. Старый синтаксис deprecated.
>
> **Подводные камни:**
> - Забыть `@JvmInline` в JVM-only проекте — типичная ошибка после copy-paste из multiplatform examples. Компилятор подскажет, но не сразу очевидно.
> - В будущем Project Valhalla может добавить новую аннотацию (например, `@JvmValhalla`), и тогда `@JvmInline` останется для legacy. Сейчас это не проблема.
> - `@JvmInline` не делает value class «более inline» — это binary marker, не оптимизация. Не пытайтесь использовать его как perf-hint на data class или обычный класс — он не применим.
>
> **Связанные вопросы:** [[Q1]] — что такое value class; [[Q3]] — как inline проявляется в байт-коде; [[Q4]] — ограничения value class.
>
> ---
>
> #### D) `@JvmInline` нужна для активации `equals`/`hashCode` автогенерации — без неё value class имеет только default Object identity equals — ❌ Неверно
>
> **Что на самом деле:** автогенерация `equals`/`hashCode` для value class активируется самим keyword `value class`, не аннотацией `@JvmInline`. Любой `value class` (с аннотацией или без, на JVM или Native) получает структурное равенство по своему единственному полю. `@JvmInline` управляет binary-представлением, не семантикой equals/hashCode.
>
> **Откуда путаница:** аналогия с data class: чтобы получить автогенерацию equals/hashCode, нужно ключевое слово `data`. Логично домыслить, что и для value class нужна какая-то «активирующая» аннотация. Но реально равенство по значению — это часть семантики value class по определению (это и означает «по значению»), и оно автоматически.
>
> **Если бы это было правдой:** `value class Foo(val v: String)` без `@JvmInline` использовал бы Object.equals (identity), и `Foo("x") == Foo("x")` возвращало бы false. Реально такой код не компилируется вообще на JVM, а на Kotlin/Native compile и работает с правильным equals.

## Q12. Как value class работает с корутинами и suspend функциями?

```kotlin
@JvmInline
value class UserId(val value: String)

suspend fun fetchUser(id: UserId): User {
    delay(100)
    return userRepo.findById(id.value)
}

// Под капотом — mangled method name:
// fun fetchUser-XYZ(id: String, continuation: Continuation): Object
// id передаётся как примитивный String, без boxing
```

**Ограничение**: value class в generic-позициях (List<UserId>, Map<UserId, User>) боксится, поэтому преимущество теряется.


> [!mcq]
>
> **Вопрос:** Что происходит с inline-представлением value class в параметрах `suspend`-функции?
>
> ---
>
> #### A) Suspend-функции не поддерживают value class — компилятор требует replace value class на data class в любых suspend-сигнатурах — ❌ Неверно
>
> **Что на самом деле:** suspend-функции прекрасно поддерживают value class. `suspend fun fetchUser(id: UserId): User` — валидная сигнатура. Никакой замены на data class не требуется. Корутины — это compile-time трансформация в Continuation-машину; value class остаётся value class и в обычном вызове, и через corutine context.
>
> **Откуда путаница:** suspend-функции выглядят «специальными», компилятор делает много магии (state machine, Continuation), и можно домыслить «значит, value class сюда не вписывается». Реально transformation work на уровне функции в целом, не на типах параметров.
>
> **Если бы это было правдой:** Spring WebFlux Kotlin / Ktor с suspend handlers не могли бы использовать value class для path-params. Реально это типовой паттерн (`suspend fun getUser(@PathVariable id: UserId): User`).
>
> ---
>
> #### B) Inline-представление полностью теряется в любой suspend функции, поскольку Continuation требует упаковки всех параметров в объект — ❌ Неверно
>
> **Что на самом деле:** хотя suspend-функция компилируется в state machine с Continuation, параметры функции (включая value class) на сайте вызова продолжают передаваться как inline-значения, если позиция позволяет. Внутри state machine они сохраняются в полях continuation-объекта (что является разовым boxing при suspension), но это не «всегда теряется» — это происходит только при реальной приостановке корутины. Если функция выполняется без suspension (нет реального await), inline-представление сохраняется на всём вызове.
>
> **Откуда путаница:** упоминание Continuation создаёт впечатление «всё всё равно в объекте». Реально Continuation создаётся только при реальной приостановке (suspension point), а fast-path выполнения может быть полностью inline.
>
> **Если бы это было правдой:** value class в suspend функциях не имел бы смысла, и все KotlinConf доклады про DDD + Coroutines рекомендовали бы избегать value class. Реально это идеальный комбо — type safety + async, без значимой потери производительности на не-suspending кодовых путях.
>
> ---
>
> #### C) Корутины автоматически создают специальные `inline suspend`-обёртки, в которых value class работает быстрее, чем в обычных функциях — ❌ Неверно
>
> **Что на самом деле:** никаких `inline suspend`-обёрток корутины не создают. `inline fun` и `suspend fun` — разные модификаторы. `inline suspend fun` существует (это inline lambda с suspend), но не имеет отношения к value class и не делает его «быстрее». Корутины влияют на flow управления (state machine), не на параметры представления.
>
> **Откуда путаница:** слово «inline» встречается и в `inline class`/value class, и в `inline fun`, и в `inline suspend`. Натуральная путаница — кажется, что все эти inline связаны. Реально это разные фичи с разной семантикой.
>
> **Если бы это было правдой:** в Kotlin docs упоминался бы «coroutine inline boost для value class». Реально такого нет; performance value class в suspend и non-suspend контекстах примерно одинаковая.
>
> ---
>
> #### D) Inline-представление сохраняется в параметрах: `suspend fun fetchUser(id: UserId)` в bytecode становится `fetchUser-XYZ(String id, Continuation cont)` — UserId передаётся как primitive String; boxing активируется только в стандартных позициях (List<UserId>, UserId?, generic, interface) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Suspend-функция при компиляции получает дополнительный параметр `Continuation<T>` и преобразуется в state machine. Mangling для value class работает как обычно: имя метода становится `fetchUser-<hash>(String, Continuation)`. Underlying-тип (String) подставляется напрямую, никакого boxing для самого UserId-параметра не происходит. При реальной приостановке корутины (suspension point) состояние сохраняется в Continuation — там значения полей сохраняются как Object (это касается всех параметров, не только value class), но это разовый boxing при suspension, не overhead на каждом вызове.
>
> На fast-path (когда suspend-функция выполняется синхронно без реального suspension) inline-представление работает полностью, без overhead. Это типично для функций, которые имеют suspend-сигнатуру для extension/composability, но в большинстве вызовов завершаются без await.
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class UserId(val value: String)
>
> suspend fun fetchUser(id: UserId): User {
>     val cached = cache.get(id)
>     if (cached != null) return cached     // fast path: нет suspension
>     return withContext(Dispatchers.IO) {  // suspension здесь
>         repo.findById(id.value)
>     }
> }
>
> // Bytecode представление (упрощённо)
> // public static Object fetchUser-XYZ(
> //     String id,                          ← inline UserId как String
> //     kotlin.coroutines.Continuation cont
> // ) {
> //     User cached = cache.get-impl(id);
> //     if (cached != null) return cached;
> //     return BuildersKt.withContext(
> //         Dispatchers.getIO(),
> //         new Function2<...>() { ... },   ← suspension boxes state
> //         cont
> //     );
> // }
> ```
>
> **Когда применять:**
> - Spring WebFlux / Ktor с suspend handlers и value class для path/query params — идиоматичный паттерн в Kotlin Spring Boot 3.
> - Repository pattern с suspend-методами: `suspend fun findById(id: UserId): User?` — type safety + non-blocking I/O.
> - Service layer на coroutines: `class UserService(val repo: UserRepo) { suspend fun process(id: UserId) = ... }`.
>
> **Подводные камни:**
> - `Flow<UserId>` (а также `Channel<UserId>`) — generic, поэтому элементы boxed. Если flow несёт миллионы UserId-ов, рассмотрите `Flow<String>` и оборачивание на consumer-стороне.
> - `suspend fun process(ids: List<UserId>)` — параметр Boxes (List generic), сам передаётся как reference. Внутри iteration unboxing на каждый элемент.
> - `async { computeUserId() }` возвращает `Deferred<UserId>` — generic, UserId boxed внутри Deferred.
> - Continuation сохраняет state при suspension — каждый параметр функции записывается в поле continuation; для value class это boxing на этот момент. Если функция часто suspendится в горячем пути — overhead заметен.
>
> **Связанные вопросы:** [[Q3]] — общая семантика inline/box; [[Q10]] — boxing в коллекциях (List<UserId>); [[Q5]] — boxing через интерфейс.

## Q13. Как тестировать value class?

```kotlin
@JvmInline
value class Age(val years: Int) {
    init {
        require(years in 0..150) { "Age must be 0..150, got $years" }
    }
}

class AgeTest {

    @Test
    fun `should accept valid age`() {
        val age = Age(25)
        assertThat(age.years).isEqualTo(25)
    }

    @Test
    fun `should reject negative age`() {
        assertThrows<IllegalArgumentException> {
            Age(-1)
        }
    }

    @Test
    fun `should reject too large age`() {
        val ex = assertThrows<IllegalArgumentException> {
            Age(151)
        }
        assertThat(ex.message).contains("151")
    }

    @Test
    fun `equals and hashCode work`() {
        val a1 = Age(30)
        val a2 = Age(30)
        assertThat(a1).isEqualTo(a2)
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode())
    }
}
```

Тестирование обычно — проверка валидации в init и поведения методов.


> [!mcq]
>
> **Вопрос:** Что именно нужно тестировать в `value class`, чтобы покрыть его поведение полноценно?
>
> ---
>
> #### A) Валидацию в `init` блоке (boundary cases — отрицательные, нулевые, граничные значения), `equals`/`hashCode` симметрию, поведение собственных методов и operator-функций; пример: `assertThrows<IllegalArgumentException> { Age(-1) }`, `assertThat(Age(25)).isEqualTo(Age(25))`, проверка `Money + Money` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Полноценные тесты для value class должны покрывать четыре аспекта: (1) Валидация в init — все boundary cases (значение ниже минимума, выше максимума, точно на границах, null/empty/whitespace для строк). (2) equals/hashCode — проверка симметрии и контракта (`a.equals(b) == b.equals(a)`, `a == b ⟹ a.hashCode() == b.hashCode()`). (3) Собственные методы и computed properties — `Money.dollars`, `Email.domain`, `Percentage.asFraction`. (4) Operator-функции — `Money + Money`, `Money * Int`, `Money compareTo Money`. Стандартные test-фреймворки (JUnit 5, Kotest, AssertJ, Kotlin's `assertEquals`) работают с value class без особых модификаций. Boxing в тестах допустим — fast tests, не hot path.
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class Age(val years: Int) {
>     init { require(years in 0..150) { "Age must be 0..150, got $years" } }
>
>     fun isAdult(): Boolean = years >= 18
> }
>
> @JvmInline
> value class Money(val cents: Long) {
>     init { require(cents >= 0) }
>     operator fun plus(other: Money) = Money(cents + other.cents)
>     val dollars: Double get() = cents / 100.0
> }
>
> class ValueClassTest {
>
>     // 1. Валидация — boundary cases
>     @Test
>     fun `Age accepts boundary values`() {
>         Age(0)      // нижняя граница
>         Age(150)    // верхняя граница
>         Age(25)     // обычное значение
>     }
>
>     @Test
>     fun `Age rejects out-of-range values`() {
>         val negative = assertThrows<IllegalArgumentException> { Age(-1) }
>         assertThat(negative.message).contains("-1")
>
>         val tooLarge = assertThrows<IllegalArgumentException> { Age(151) }
>         assertThat(tooLarge.message).contains("151")
>     }
>
>     // 2. equals / hashCode contract
>     @Test
>     fun `equals reflexive symmetric and consistent with hashCode`() {
>         val a = Age(30)
>         val b = Age(30)
>         val c = Age(31)
>
>         assertThat(a).isEqualTo(b)
>         assertThat(b).isEqualTo(a)  // symmetric
>         assertThat(a.hashCode()).isEqualTo(b.hashCode())
>         assertThat(a).isNotEqualTo(c)
>     }
>
>     // 3. Собственные методы
>     @Test
>     fun `Age isAdult returns true for 18 and above`() {
>         assertThat(Age(17).isAdult()).isFalse()
>         assertThat(Age(18).isAdult()).isTrue()
>         assertThat(Age(100).isAdult()).isTrue()
>     }
>
>     // 4. Operator functions
>     @Test
>     fun `Money plus combines cents correctly`() {
>         val result = Money(100) + Money(250)
>         assertThat(result).isEqualTo(Money(350))
>     }
>
>     // Property-based testing (Kotest)
>     @Test
>     fun `Age plus zero returns same Age (using Kotest forAll)`() {
>         checkAll(Arb.int(0..150)) { years ->
>             val age = Age(years)
>             assertThat(age.years).isEqualTo(years)
>         }
>     }
> }
> ```
>
> **Когда применять:**
> - Любой value class с `init` валидацией — тесты обязательны, особенно boundary cases.
> - Value class с собственными методами / operator-функциями — поведение должно покрываться отдельно.
> - В Spring Boot Kotlin проектах добавляйте `ValueClassTest` для каждого важного domain-primitive.
> - Property-based testing (Kotest `forAll`, jqwik) — отлично подходит для value class с числовым underlying-типом.
>
> **Подводные камни:**
> - Тестировать через mockito — Mockito не умеет мокать `final` value class из коробки; используйте `mockk` (Kotlin-native, лучше работает с value class).
> - Если value class имеет operator-функции (`+`, `-`, `*`), проверьте overflow для underlying Int/Long. `Money(Long.MAX_VALUE) + Money(1)` приведёт к wraparound.
> - В тестах boxing незаметен (всё медленно по сравнению с network/DB), не пытайтесь оптимизировать тесты по boxing.
> - Тесты на сериализацию (Jackson, kotlinx.serialization) — отдельная категория, обычно через integration tests с реальным `ObjectMapper`.
>
> **Связанные вопросы:** [[Q1]] — общее описание value class; [[Q4]] — init блок и валидация; [[Q15]] — best practices (init, factory).
>
> ---
>
> #### B) Тестировать value class не нужно — компилятор Kotlin гарантирует корректность всех value class в compile time — ❌ Неверно
>
> **Что на самом деле:** компилятор проверяет только структурные ограничения (одно `val`-поле, нет наследования от классов). Бизнес-логика в init и собственных методах (`Email init { require(contains("@")) }`, `Money + Money`) — это runtime behavior, который тестируется как любой другой код. Компилятор не знает, что email должен содержать `@`, или что Money не должна быть отрицательной.
>
> **Откуда путаница:** value class позиционируется как «type safety», и можно ошибочно расширить понимание до «вся корректность гарантирована типом». Реально type safety — это про межтиповые проверки (UserId vs OrderId), не про invariants внутри одного типа.
>
> **Если бы это было правдой:** случаи production-багов в domain primitives (валидный email становится `"plain text"`, money переполняется и становится negative) были бы невозможны. Реально такие баги встречаются и тесты их ловят.
>
> ---
>
> #### C) Тестировать нужно только через рефлексию — value class в JVM хранится как обёртка, и обычные assertions не работают — ❌ Неверно
>
> **Что на самом деле:** обычные assertions работают идеально. `assertThat(Age(25)).isEqualTo(Age(25))` — корректный JUnit/AssertJ код. Value class имеет нормальный `equals`/`hashCode` (по underlying-полю), и assertions сравнивают значения штатно. Никакая рефлексия не нужна.
>
> **Откуда путаница:** value class имеет «специальное» представление в байт-коде, и хочется домыслить, что и тестирование «специальное». Реально семантика всех тестовых assertion-ов одинаковая для всех типов.
>
> **Если бы это было правдой:** Kotest, JUnit 5, AssertJ книги/туториалы имели бы отдельные секции «тестирование value class». Реально нет — обычные паттерны работают.
>
> ---
>
> #### D) Тестировать только поведение в коллекциях — этот аспект имеет boxing-overhead и единственное место, где value class может вести себя неожиданно — ❌ Неверно
>
> **Что на самом деле:** поведение в коллекциях (boxing) — это performance-вопрос, а не correctness-вопрос. Boxing не меняет семантику: `List<UserId>` работает корректно, просто с overhead. Главное, что нужно тестировать — это бизнес-логика value class: валидация, методы, equals/hashCode. Тестирование boxing — это профилирование, а не unit tests.
>
> **Откуда путаница:** «boxing» — это known issue value class, и кажется, что именно его нужно «накрыть тестами». Реально boxing — это про performance, проверять которое в unit tests не имеет смысла (микробенчмарки нужны).
>
> **Если бы это было правдой:** unit tests включали бы микробенчмарки и измерения GC. Реально это разные категории — JMH/JFR для performance, JUnit для correctness.

## Q14. Когда НЕ стоит использовать value class?

1. **Нужно несколько полей** — используйте data class (в stable Kotlin).

2. **Постоянное использование через интерфейсы** — boxing уничтожает преимущество.

3. **Большие коллекции value class** — всё равно boxing.

4. **JPA entity identifiers без конвертеров** — JPA не понимает value class нативно.

5. **Для простых internal-helpers без типобезопасности**:

```kotlin
// Over-engineering — typealias достаточно
@JvmInline value class TaskHandler(val handler: (Task) -> Unit)

// Лучше:
typealias TaskHandler = (Task) -> Unit
```

6. **Если важна простота debug** — value class в IDE и стектрейсах выглядит менее явно.


> [!mcq]
>
> **Вопрос:** В каком сценарии `value class` принесёт больше проблем, чем пользы?
>
> ---
>
> #### A) Параметры контроллеров Spring WebFlux с `@PathVariable id: UserId` — для type safety REST API — ❌ Неверно (это хороший use-case)
>
> **Что на самом деле:** это идеальный use-case. `@PathVariable id: UserId` даёт type safety при работе с пути URL — нельзя случайно передать `OrderId` где ждут `UserId`. Требует `KotlinModule` для ObjectMapper или `Converter<String, UserId>` для path-binding, но это разовая настройка. Spring Boot 3 + Kotlin полностью поддерживает этот паттерн.
>
> **Откуда путаница:** этот вариант это хороший use-case, не плохой.
>
> **Если бы это было правдой:** Kotlin Spring Boot туториалы не рекомендовали бы этот паттерн. Реально — рекомендуют активно (включая официальный Spring Initializr для Kotlin DTOs).
>
> ---
>
> #### B) Высокочастотный поток value class через `List<UserId>` / `Flow<UserId>` в hot path Kafka-consumer-а, где boxing на каждом элементе создаёт значительный GC overhead — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Главная антимодель value class — массовое использование там, где компилятор вынужден активировать boxing на каждом значении. Hot path Kafka-consumer (миллион сообщений/сек) с `Flow<UserId>` будет создавать миллион boxed UserId-объектов в секунду — это значительная GC pressure, которая нивелирует преимущество value class по сравнению с raw String. Boxing активируется в: generic-параметрах (`List<UserId>`, `Map<UserId, …>`, `Flow<UserId>`), nullable (`UserId?`), interface-полиморфизме, reflection. В этих сценариях value class даёт type safety, но теряет performance-преимущество, ради которого его выбрали. Альтернативы: (1) использовать raw underlying-тип на hot path и оборачивать на API boundary; (2) кастомные специализированные коллекции (`Array<String>` с inline accessor); (3) если underlying — Long, рассмотреть `LongArray` или Eclipse Collections `LongArrayList`.
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class UserId(val value: String)
>
> // ПЛОХО: hot path с boxing на каждом сообщении
> @KafkaListener(topics = ["user-events"], concurrency = "20")
> suspend fun processEvents(events: Flow<UserId>) {  // Flow<UserId> = boxed
>     events.collect { userId ->                      // unbox at access
>         processUser(userId)
>     }
> }
>
> // ХОРОШО: raw String на hot path, value class на API boundary
> @KafkaListener(topics = ["user-events"], concurrency = "20")
> suspend fun processEventsRaw(events: Flow<String>) {  // Flow<String> = no box
>     events.collect { rawId ->
>         val userId = UserId(rawId)        // inline (на сайте вызова)
>         processUser(userId)               // inline передача
>     }
> }
>
> // Или кастомный UserId-buffer для batch-обработки
> class UserIdBuffer(val rawIds: LongArray) {
>     val size: Int get() = rawIds.size
>     operator fun get(i: Int): UserId = UserId(rawIds[i].toString())
> }
> ```
>
> **Когда применять (анти-паттерны):**
> - **Не использовать value class на hot paths с коллекциями.** Замените на raw underlying-тип внутри hot path, оборачивайте только при выходе из него.
> - **Не использовать как ID-тип для очень больших Map** (10M+ ключей) — boxed UserId-объекты съедят heap. Лучше `Long2ObjectMap` из Eclipse Collections.
> - **Не использовать в interface-heavy дизайне** — каждый interface-call упаковывает; либо избавляться от interface, либо принимать boxing.
> - **Не использовать как `@JvmInline value class CallbackHandler((Task) -> Unit)`** — для функциональных типов используйте typealias.
>
> **Подводные камни:**
> - Профилирование boxing — единственный надёжный способ; визуально по коду неочевидно. Используйте async-profiler с allocation-tracking, JFR.
> - Mixed paths — типичная ситуация. `UserId` хорош в DTO и Spring контроллерах, но проблематичен в Kafka stream-processing части того же сервиса. Решение — две разные сигнатуры для одной concepts (raw inside, wrapped outside).
> - Иногда micro-overhead boxing незначим (10K сообщений/сек — нечувствительно); правило «всегда оптимизируйте boxing» неверно. Профилируйте до принятия решений.
>
> **Связанные вопросы:** [[Q3]] — общая семантика inline/box; [[Q10]] — value class в коллекциях; [[Q12]] — value class в suspend/Flow.
>
> ---
>
> #### C) Domain primitive `Email(val value: String)` с валидацией в init — для предотвращения создания невалидных email — ❌ Неверно (это хороший use-case)
>
> **Что на самом деле:** это классический пример value class. Невалидный email невозможно создать — `Email("not_an_email")` упадёт в init. Тип гарантирует, что в коде ниже работаете с валидным email. Один из топовых примеров в Effective Kotlin / Kotlin in Action.
>
> **Откуда путаница:** этот вариант это хороший use-case.
>
> **Если бы это было правдой:** все DDD-руководства рекомендовали бы избегать value class для refinement types. Реально — наоборот, это flagship-применение.
>
> ---
>
> #### D) Money с operator-функциями (`Money + Money`, `Money * Int`) — для типобезопасной финансовой арифметики — ❌ Неверно (это хороший use-case)
>
> **Что на самом деле:** это правильное применение value class. Money с operator-функциями избегает Double-арифметики (с её известными проблемами round-off), даёт type safety (нельзя сложить Money(USD) и Money(EUR), если они разные value class). Доменно-моделированный Money — стандартный паттерн в финансовых системах.
>
> **Откуда путаница:** этот вариант это хороший use-case.
>
> **Если бы это было правдой:** Martin Fowler и DDD-сообщество не рекомендовали бы Money как первый пример value object. Реально — рекомендуют активно, и Kotlin value class — отличная реализация этого паттерна.

## Q15. Какие best practices при работе с value class?

1. **Используйте для domain primitives** — UserId, Money, Email, Percentage.

2. **Валидация в init** — invariant enforcement:

```kotlin
@JvmInline
value class Port(val value: Int) {
    init { require(value in 1..65535) }
}
```

3. **Добавляйте operator-функции для арифметики**:

```kotlin
@JvmInline
value class Money(val cents: Long) {
    operator fun plus(other: Money) = Money(cents + other.cents)
    operator fun times(multiplier: Int) = Money(cents * multiplier)
    operator fun compareTo(other: Money): Int = cents.compareTo(other.cents)
}
```

4. **Не используйте для больших структур** — используйте data class.

5. **Factory методы для сложного создания**:

```kotlin
@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun of(raw: String): Email? =
            if (raw.matches(EMAIL_REGEX)) Email(raw.trim().lowercase()) else null
    }
}
```

6. **Сериализация** — явно указывайте @Serializable и кастомный serializer для non-JVM target.

7. **Документируйте преимущества** в комментариях — чтобы будущие разработчики не заменили на typealias.


> [!mcq]
>
> **Вопрос:** Какая комбинация практик при разработке `value class` для domain primitives — наиболее правильная?
>
> ---
>
> #### A) Делать поле `var` для возможности модификации, отказаться от валидации в `init` (она замедляет создание), не добавлять operator-функции (они вызывают boxing) — ❌ Неверно
>
> **Что на самом деле:** `var` в value class запрещён компилятором — поле должно быть `val`. Валидация в init — рекомендуемая практика, init выполняется только при создании (один раз на жизненный цикл значения), overhead минимальный. Operator-функции не вызывают boxing сами по себе — они инлайнятся как обычные методы value class. Этот «совет» — анти-best-practice по всем пунктам.
>
> **Откуда путаница:** мифы про «overhead валидации» и «boxing operator-функций» иногда циркулируют в комьюнити. Реально валидация в init — это `require(...)` (один if + throw), а operator-функции компилируются в static-вызовы с inline-параметрами.
>
> **Если бы это было правдой:** код `value class Age(var years: Int)` компилировался бы — он не компилируется. Также все туториалы по Kotlin DDD рекомендовали бы избегать operator-функций — реально их рекомендуют как лучшую часть value class.
>
> ---
>
> #### B) Использовать value class только для одно-полевых случаев; обязательно добавлять `init { require(...) }` для валидации инвариантов; использовать companion factory методы для сложного создания; добавлять operator-функции там, где они улучшают читаемость — ❌ Неверно (но очень близко)
>
> **Что на самом деле:** это почти полный список best practices, но не самый полный. Не упомянуты: (1) избегание boxing на hot paths (List/Flow/interface); (2) явная регистрация Jackson Module / JPA конвертеров для интероп; (3) использование `@JvmName` для красивых accessor-ов из Java. Это близкий к правильному вариант, но более полный — в C.
>
> **Откуда путаница:** часто разработчики думают, что value class — это «просто wrapper с валидацией», и сюда добавляют operator + factory как «advanced practices». Реально комплексное правильное использование включает ещё несколько слоёв (perf, interop).
>
> **Если бы это было правдой:** в Spring Boot Kotlin проектах не возникало бы вопросов про MappingException на JPA-entity с value class и про path-binding в WebFlux. Реально эти вопросы регулярны и требуют additional best practices, которые тут не упомянуты.
>
> ---
>
> #### C) Использовать для domain primitives (одно-полевые `UserId`, `Money`, `Email`); валидация инвариантов в `init` (`require(...)`); operator-функции для domain-arithmetic (`Money + Money`); companion factory для сложного создания; явная регистрация Jackson `KotlinModule` и JPA `AttributeConverter` для interop; избегание массового использования в `List`/`Flow` на hot paths; `@JvmName` для красивых accessor-ов из Java — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Комплексная best practice по value class включает несколько слоёв:
>
> 1. **Применение** — только для domain primitives с одним underlying-значением. Composite — data class.
>
> 2. **Валидация** — `init { require(...) }` обязательна для типов с инвариантами (Percentage в 0..100, Port в 1..65535, NonEmptyString). Это превращает невалидные состояния в невозможные на этапе типов.
>
> 3. **API дизайн** — operator-функции (`+`, `-`, `*`, `compareTo`) для арифметики; companion factory (`Email.of(raw: String): Email?`) для сложного создания с возможным null; computed properties для derived data (`Percentage.asFraction`).
>
> 4. **Performance awareness** — избегайте `List<UserId>` / `Flow<UserId>` / interface-based polymorphism на hot paths (Kafka-consumer, batch-обработка миллионов записей). Профилируйте перед оптимизацией.
>
> 5. **Интероп** — для Jackson регистрируйте `KotlinModule.Builder().build()` в ObjectMapper. Для JPA добавляйте `AttributeConverter<X, Y>` с `@Converter(autoApply = true)`. Для Java-interop — `@JvmName("getId") fun getValue(): String = value` для красивого accessor.
>
> 6. **Серилизация** — для non-JVM target (Kotlin/JS, Native) явный `@Serializable` и кастомный serializer для kotlinx.serialization.
>
> 7. **Документация** — комментарии «зачем именно value class, а не typealias» в коде, чтобы будущие разработчики (через 6 месяцев) не заменили на typealias «для упрощения».
>
> **Пример:**
> ```kotlin
> @JvmInline
> value class Money private constructor(val cents: Long) {
>     init {
>         require(cents >= 0) { "Money cannot be negative: $cents cents" }
>         require(cents <= MAX_AMOUNT) { "Money exceeds max: $cents > $MAX_AMOUNT" }
>     }
>
>     val dollars: Double get() = cents / 100.0
>
>     operator fun plus(other: Money) = Money(cents + other.cents)
>     operator fun minus(other: Money): Money {
>         require(cents >= other.cents) { "Underflow: $cents - ${other.cents}" }
>         return Money(cents - other.cents)
>     }
>     operator fun times(multiplier: Int): Money {
>         require(multiplier >= 0) { "Negative multiplier" }
>         return Money(cents * multiplier)
>     }
>     operator fun compareTo(other: Money): Int = cents.compareTo(other.cents)
>
>     companion object {
>         private const val MAX_AMOUNT = 100_000_000_00L  // $100M
>         fun fromDollars(d: Double): Money = Money((d * 100).toLong())
>         fun zero(): Money = Money(0)
>     }
> }
>
> // Spring: ObjectMapper registration
> @Bean
> fun objectMapper(): ObjectMapper =
>     ObjectMapper().registerModule(KotlinModule.Builder().build())
>
> // JPA: AttributeConverter
> @Converter(autoApply = true)
> class MoneyConverter : AttributeConverter<Money, Long> {
>     override fun convertToDatabaseColumn(a: Money?) = a?.cents
>     override fun convertToEntityAttribute(d: Long?) = d?.let { Money(it / 1) }  // упрощено
> }
> ```
>
> **Когда применять:**
> - Любая Kotlin кодовая база с domain-логикой и REST/JPA/Kafka интегра-ями.
> - DDD-проекты, где value object — фундаментальный концепт.
> - Финансовые системы, e-commerce, telecom — там, где type mixing аргументов = serious incident (Knight Capital style).
>
> **Подводные камни:**
> - Не каждое поле должно быть value class — `String name` для DTO часто не требует обёртки. Применять там, где есть type-confusion риск или нужна валидация.
> - Operator-функции — мощно, но overload не отнимает у читателя ясности. `Money + Money` — да; `Money + Int` (смешение валюты и multiplier) — спорно, лучше `Money.times(Int)`.
> - Factory `of(raw): Email?` возвращает nullable — заставляет caller обрабатывать null; альтернатива throwing `of(raw): Email`. Выбор зависит от семантики (валидация в boundary vs deep в коде).
> - `AttributeConverter` с `autoApply = true` глобально применяется — если есть две entity с одинаковым value class, но разный маппинг, autoApply не подходит, используйте явный `@Convert`.
>
> **Связанные вопросы:** [[Q4]] — ограничения value class; [[Q7]] — JPA конвертеры; [[Q14]] — когда value class не нужен.
>
> ---
>
> #### D) Использовать value class везде, где есть поле `String` или `Long` — для максимизации type safety, без оглядки на boxing и interop проблемы — ❌ Неверно
>
> **Что на самом деле:** «везде» — anti-best-practice. Value class даёт type safety, но имеет реальные стоимости: boxing на hot paths, interop проблемы (Jackson/JPA настройка), psychological overhead для команды. Правильный подход — применять там, где есть конкретный риск перепутывания типов или нужна валидация. Для tail-логирования или внутренних ID, которые никогда не выходят за класс, value class — overkill.
>
> **Откуда путаница:** "type safety везде" звучит как universally good practice. Реально cost-benefit имеет смысл оценивать. Если поле никогда не путается с другим (`val errorCode: String` в log сервисе), value class — overengineering.
>
> **Если бы это было правдой:** все Kotlin кодовые базы перешли бы на 100% value class в полях. Реально typical Spring Boot Kotlin проект использует value class для key domain primitives (UserId, OrderId, Money) и оставляет `String`/`Long` для tail-полей (logs, internal counters).

## See also

- [Kotlin](kotlin-interview.md) — основы языка, classes, data classes
- [Kotlin Sealed Classes](kotlin-sealed-classes-interview.md) — sealed + value classes для ADT
- [Java Records](../java/java-records-interview.md) — Java аналог (но heap-allocated)
- [Kotlin Serialization](kotlin-serialization-interview.md) — сериализация value classes
- [Kotlin/Java Interop](kotlin-interop-java-interview.md) — как value classes выглядят из Java
- [Kotlin Collections](kotlin-collections-interview.md) — value classes в коллекциях (boxing penalty)
- [Kotlin Coroutines](kotlin-coroutines-interview.md) — value classes в corotines
- [Kotlin + Spring](kotlin-spring-interview.md) — value classes как request/response DTO
- [Domain-Driven Design](../../architecture/ddd-interview.md) — value objects паттерн
- [Java Generics](../java/java-generics-interview.md) — boxing при generic params
