---
title: "Вопросы на собеседовании: Value-классы Kotlin"
description: "Kotlin @JvmInline value classes: доменные типы, JVM-представление, boxing, интеграция с библиотеками"
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
# Вопросы на собеседовании: Value-классы Kotlin

Здесь речь об однопольных `@JvmInline value class` на JVM в стабильном Kotlin 2.4. Это отдельные типы для компилятора, которые при прямом использовании могут представляться underlying-значением без отдельной обёртки. В generic-, interface- и nullable-позициях возможен boxing. Экспериментальные многопольные full value classes из Kotlin 2.5 Beta рассматриваются отдельно. Используются для `UserId`, `Email`, `Money` и других domain-типов.

Дата последнего обновления: 2026-11-06

## Полезные ссылки

### Официальная документация

- [Kotlin Inline Value Classes](https://kotlinlang.org/docs/inline-classes.html) — официальная документация
- [Baeldung: Kotlin Inline Classes](https://www.baeldung.com/kotlin/inline-classes) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что даёт однопольный @JvmInline value class на JVM?

**Коротко:** `value class` (до Kotlin 1.5 — `inline class`) — это обёртка вокруг одного значения, которая существует в системе типов, а в прямом использовании на JVM обычно представлена underlying-значением. Компилятор подставляет underlying-значение (`String`, `Long` и т.п.) напрямую в место использования — этот процесс называется `inlining`. В результате вы получаете отдельный тип для проверки на этапе компиляции, часто избегая выделения отдельной обёртки в куче.

Идея: взять «голый» примитив (`String`-идентификатор, `Long`-сумма в копейках) и одеть его в собственный тип, чтобы компилятор отличал один `String` от другого.

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

**Зачем нужны:**

- **Типобезопасность с возможностью избежать обёртки.** `UserId` и `OrderId` компилятор не даст перепутать, хотя под капотом оба — `String`. С «голыми» `String` такую ошибку поймал бы только тест в рантайме (или прод).
- **Валидация в `init`.** Инвариант проверяется в момент создания — невалидный `Email` (без `@`) просто невозможно сконструировать, дальше по коду он гарантированно корректен.
- **Самодокументирующийся код.** `Money(9999)` сразу читается как «сумма», а `9999L` — просто число, смысл которого надо угадывать по контексту.

**Главное отличие от обычного класса-обёртки:** `@JvmInline` может использовать underlying-значение без отдельного объекта. В generic-, interface- и nullable-позициях boxing возможен, но отдельный тип остаётся полезен для доменной модели.

## Q2. Чем однопольный @JvmInline value class отличается от data class?

**Коротко:** оба автогенерируют `equals`/`hashCode`/`toString`, но решают разные задачи (а вот `copy()` и `componentN()` — только `data class`). `data class` — это объектный тип для группы полей; JVM может оптимизировать конкретные аллокации. `value class` — это «тип-обёртка вокруг ровно одного значения», и в прямых позициях он может не создавать отдельный объект (значение инлайнится). Грубо: `data class` про удобство хранения данных, `value class` про дешёвую типобезопасность.

| Критерий | data class | value class |
|----------|-----------|-------------|
| Количество полей | Любое | Ровно одно (в stable Kotlin) |
| Представление в памяти | Объектный тип | Underlying-значение или boxed wrapper |
| `equals/hashCode` | Автогенерация по всем полям | Автогенерация по единственному полю |
| `copy()` | Да | Нет — компилятор генерирует только `equals`/`hashCode`/`toString` |
| Destructuring | Да | Нет — `componentN()` не генерируется |
| Наследование | Только от интерфейсов | Только от интерфейсов |
| Overhead | Выделение объекта в куче | Обычно 0 (inline) |

```kotlin
// data class — объектный тип
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

**Когда что брать:** несколько полей — только `data class` (в stable Kotlin у `value class` поле ровно одно). Одно значение, которому нужен отдельный доменный тип, — кандидат на `@JvmInline value class`; влияние boxing оценивайте по месту использования.

## Q3. Как value class представлен в JVM байт-коде?

**Коротко:** где это возможно, компилятор стирает обёртку и работает напрямую с underlying-значением, а имена функций, принимающих/возвращающих `value class`, искажает (`name mangling`) — добавляет суффикс-хеш. Манглинг нужен, чтобы две функции с одинаковой сигнатурой после инлайнинга (например, `fun f(x: UserId)` и `fun f(x: String)`) не сколлайдились в байт-коде и чтобы такой метод нельзя было случайно вызвать из Java как обычный.

Ниже: `Celsius` исчезает, остаётся `double`.

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

**Когда inlining НЕ происходит (обёртку приходится боксить в реальный объект):**

1. **Через интерфейс.** Когда `value class` передаётся как реализация интерфейса — нужен настоящий объект с таблицей методов.
2. **В коллекциях.** `List<UserId>` хранит `Object`-ссылки, поэтому каждый `UserId` упаковывается в объект (boxing), а при доступе — распаковывается.
3. **В generic-позициях.** Дженерики на JVM работают только со ссылочными типами, примитив туда не положить — снова бокс.
4. **Nullable.** Если underlying — примитив (`Int`, `Double`), то `UserId?` боксится: у примитива `null` нет. Но для ссылочного underlying есть нюанс: `UserId?` на базе `String` может представляться как `String?` без боксинга — `null` помещается прямо в ссылку. Бокс становится обязательным, когда nullable сам underlying-тип (например, `value class W(val v: String?)`): иначе `W(null)` и `null` были бы неотличимы.

Общий принцип: прямое использование часто сохраняет underlying-представление, а interface- и generic-позиции требуют boxing. Для nullable-позиции результат зависит от underlying-типа: например, `UserId?` над ненулевым `String` компилятор может передать как `String?`.

```kotlin
@JvmInline
value class UserId(val value: String)

fun process(ids: List<UserId>) {     // боксинг — ids хранит объекты
    for (id in ids) id.value         // unboxing при доступе
}
```

## Q4. Какие ограничения у однопольного @JvmInline value class?

Все ограничения вытекают из одного факта: чтобы обёртку можно было заменить на underlying-значение, у неё не должно быть собственного состояния и идентичности помимо этого значения. Отсюда следующие правила.

1. **Ровно одно `val` свойство в primary constructor** (в stable Kotlin; несколько значений — пока в preview). Подставить в место использования можно только одно значение, поэтому полей не может быть больше одного:

```kotlin
@JvmInline
value class Point(val x: Double)      // OK

@JvmInline
value class Point2D(val x: Double, val y: Double)  // ОШИБКА (пока preview)
```

2. **Только `val`, никаких `var`.** Обёртка иммутабельна: менять underlying-значение «на месте» негде, ведь объекта может вообще не быть.

```kotlin
@JvmInline
value class Counter(var count: Int)   // ОШИБКА
```

3. **Нельзя наследоваться от классов** (только реализовывать интерфейсы). Наследование требует реального объекта с предком в иерархии — это несовместимо с инлайнингом.

```kotlin
@JvmInline
value class UserId(val value: String) : Any()  // ОШИБКА
```

4. **`init` — только для валидации, без побочных эффектов.** Конструктор `value class` может вызываться очень часто и неявно, поэтому логирование, I/O и прочие эффекты в `init` — анти-паттерн; оставляйте там только `require`/проверки инвариантов:

```kotlin
@JvmInline
value class Age(val years: Int) {
    init {
        require(years >= 0)
        println("Creating age")       // лучше избегать — выполняется часто
    }
}
```

5. **Нет backing fields** — дополнительных полей хранить негде, поэтому свойства допустимы только вычисляемые (через `get()`), они выводятся из underlying-значения:

```kotlin
@JvmInline
value class UserId(val value: String) {
    // val cached: String = ...       // ОШИБКА — backing field запрещён

    val upperCase: String              // OK — computed property
        get() = value.uppercase()
}
```

6. **Запрещено сравнение по ссылке (`===`).** У `value class` нет стабильной идентичности: объекта в рантайме может вообще не быть, а при боксинге он создаётся заново. Поэтому компилятор запрещает `===` для value-типов — работает только структурное `==`. Частый follow-up на собеседовании: «а что вернёт `===`?» — ответ: «оно не скомпилируется».

## Q5. Как value class работает с интерфейсами?

**Коротко:** `value class` может реализовывать интерфейсы, но как только вы используете его *через* тип интерфейса, инлайнинг отключается и значение упаковывается в реальный объект (`boxing`). Причина: переменная типа `Identifier` хранит ссылку с виртуальной таблицей методов, а «плоское» underlying-значение такой ссылкой быть не может.

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

В вызове `describe(uid)` `uid` поднимается из плоского `String` в объект `Identifier` — происходит `boxing`. Это ровно та аллокация, ради избавления от которой `value class` обычно и заводят: парадокс в том, что интерфейс возвращает её обратно.

**Рекомендация:** если важна производительность горячего пути — не гоняйте `value class` через интерфейсы. Интерфейс уместен, когда типобезопасность важнее, чем экономия на аллокации.

## Q6. В чём разница между @JvmInline value class и обычным typealias?

**Коротко:** `typealias` — это лишь второе имя для уже существующего типа, новой проверки типов он не добавляет. `value class` — настоящий отдельный тип, который компилятор отличает от всех прочих. Поэтому `typealias UserId = String` и `String` взаимозаменяемы (компилятор пропустит подмену), а `value class UserId` и `String` — нет.

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

**Эмпирическое правило:** нужна защита от перепутывания типов — `value class`. Нужно лишь сократить длинное имя для читаемости (например, `ConsumerRecord<String, String>` → `OrderEvent`), а проверка типов не важна — `typealias`.

## Q7. Что проверить при использовании @JvmInline value class с JPA и Jackson?

**Коротко:** на границе с инфраструктурой проверьте фактическую поддержку `@JvmInline` типа. Для обычного JPA-поля может понадобиться `AttributeConverter` (обёртка ↔ колонка). Jackson Kotlin module поддерживает десериализацию value class начиная с версии 2.17; отдельный сериализатор нужен лишь для неподдержанного формата или особого контракта JSON.

JPA-конвертер: разворачивает `CustomerId` в `Long` при записи и заворачивает обратно при чтении. Важная оговорка: спецификация JPA **запрещает** применять `AttributeConverter` к `@Id`-, `@Version`- и relationship-атрибутам — поэтому конвертер висит на обычном поле (`customerId`), а идентификатор сущности остаётся «голым» `Long`.

```kotlin
@JvmInline
value class OrderId(val value: Long)

@JvmInline
value class CustomerId(val value: Long)

@Entity
class Order(
    @Id @GeneratedValue
    val id: Long,  // @Convert на @Id запрещён спецификацией JPA
    @Convert(converter = CustomerIdConverter::class)
    val customerId: CustomerId
)

@Converter(autoApply = false)
class CustomerIdConverter : AttributeConverter<CustomerId, Long> {
    override fun convertToDatabaseColumn(attribute: CustomerId?): Long? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: Long?): CustomerId? =
        dbData?.let { CustomerId(it) }
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

// Jackson Kotlin module: проверьте совместимую версию и конфигурацию ObjectMapper
@Bean
fun kotlinModule(): KotlinModule = KotlinModule.Builder().build()
```

**Подводный камень:** проблема маппинга или сериализации обычно проявится при выполнении, а не при компиляции. Для JPA-поля проверьте конвертер и ограничения спецификации для `@Id`; для DTO проверьте версию Jackson Kotlin module и фактический JSON round-trip.

## Q8. Какие типичные применения value class?

**Коротко:** все сценарии — это «domain primitives»: места, где у вас по факту лежит примитив, но семантически это не «просто `String`/`Long`/`Double`». `value class` превращает безымянное значение в осмысленный тип, который компилятор охраняет.

Основные группы применений:

- **Идентификаторы** (`UserId`, `OrderId`) — чтобы не подставить чужой id.
- **Деньги** (`Cents`) — храним целые копейки вместо `double`, избегая ошибок округления; операторы делают арифметику безопасной.
- **Единицы измерения** (`Meters`, `Seconds`) — нельзя случайно сложить метры с секундами.
- **Значения с инвариантом** (`Percentage` 0..100, `NonEmptyString`) — некорректное значение невозможно создать.
- **Domain-строки** (`IpAddress`, `Hostname`, `ApiKey`) — внешне все `String`, но перепутать их компилятор не даст.

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

## Q9. Чем однопольный @JvmInline value class Kotlin отличается от record Java 16+?

**Коротко:** их часто сравнивают, но цели разные. Java `record` — это лаконичный объектный тип с фиксированными компонентами, которых может быть несколько. JVM вправе оптимизировать выделение конкретного объекта, но у record нет языкового inline-представления underlying-значения. Kotlin `value class` — это типобезопасная обёртка вокруг *одного* значения, которую компилятор по возможности инлайнит и не аллоцирует. Иначе говоря, ближайший аналог `record` в Kotlin — это `data class`, а не однопольный `@JvmInline value class`. Экспериментальные многопольные full value classes Kotlin 2.5 Beta относятся к другому режиму языка.

| Критерий | Kotlin `value class` | Java `record` |
|----------|----------------------|---------------|
| Количество полей | Одно (stable) | Любое |
| JVM представление | Underlying-значение или boxed wrapper | Объектный класс |
| Immutability | Да (только `val`) | Да (final поля) |
| equals/hashCode | Автогенерация | Автогенерация |
| Наследование | Только от интерфейса | Только `Record` + интерфейсы |
| Destructuring | Нет — `componentN()` не генерируется | нет (есть pattern matching с Java 21) |
| Основная цель | Доменный тип с возможным inlining | Компактный носитель данных |

**Совместимость**: можно использовать оба в одном проекте.

## Q10. Как value class взаимодействует с коллекциями?

**Коротко:** в обычных коллекциях (`List`, `Set`, `Map`) `value class` теряет своё преимущество. Стандартные коллекции на JVM дженерик-параметризованы, а дженерики работают только со ссылочными типами — поэтому каждый элемент упаковывается в объект (`boxing`). Чтобы хранить много значений без боксинга, обходят коллекции стороной: используют специализированные массивы (`IntArray`, `ByteArray`) или собственную структуру поверх массива underlying-типа.

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

**На практике:** для нескольких десятков элементов боксинг незаметен — берите обычный `List`, читаемость важнее. Заморачиваться с массивами underlying-типа стоит только на действительно горячих путях с большими объёмами данных.

## Q11. Зачем однопольному value class на JVM нужна @JvmInline?

**Коротко:** в стабильном Kotlin 2.4 для однопольного inline value class на JVM требуется `@JvmInline`: она задаёт JVM-представление через underlying-значение. Это правило относится к описанному здесь inline-варианту; экспериментальные full value classes Kotlin 2.5 Beta устроены иначе.

```kotlin
// ОБЯЗАТЕЛЬНО для Kotlin 1.5+ на JVM
@JvmInline
value class UserId(val value: String)

// БЕЗ @JvmInline — ОШИБКА компиляции
value class UserId(val value: String)  // error: @JvmInline required
```

`value class` — общее понятие, а `@JvmInline` задаёт inline-представление для JVM. Новые full value classes в экспериментальной ветке языка не следует смешивать с этим контрактом. На Kotlin/Native и Kotlin/JS своё представление value-типов, там аннотация не нужна.

## Q12. Как передаётся прямой параметр @JvmInline value class простого suspend-метода на JVM?

**Коротко:** в проверенном простом `suspend`-методе параметр `@JvmInline` передаётся как underlying-значение, а имя функции манглится. Это не гарантия отсутствия boxing во всех корутинных сценариях: результат, generic-позиция или сохранённое состояние требуют отдельной проверки. Ловушка та же, что и везде: как только тип попадает в дженерик-позицию (`List<UserId>`, `Map<UserId, User>`), он боксится.

```kotlin
@JvmInline
value class UserId(val value: String)

suspend fun fetchUser(id: UserId): User {
    delay(100)
    return userRepo.findById(id.value)
}

// Под капотом — mangled method name:
// fun fetchUser-XYZ(id: String, continuation: Continuation): Object
// в этом методе id передаётся как String без объекта-обёртки UserId
```

**Ограничение:** `value class` в generic-позициях (`List<UserId>`, `Map<UserId, User>`) боксится. Типобезопасность при этом сохраняется; влияние на производительность оценивайте по конкретному вызову.

## Q13. Как тестировать value class?

**Коротко:** тестируется не «инфраструктура» обёртки (`equals`/`hashCode` сгенерированы компилятором и в проверке не нуждаются), а собственная логика типа: инварианты в `init` и поведение методов/операторов. То есть на каждый `require` пишут пару кейсов — «валидное значение принимается» и «невалидное бросает `IllegalArgumentException`» — а на операторы и computed-свойства проверяют результат.

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

Итог: фокус тестов — граничные значения валидации (как `-1` и `151` выше) и корректность доменных операций, а не автогенерированные `equals`/`hashCode`.

## Q14. Когда однопольный @JvmInline value class не подходит для модели?

`@JvmInline value class` имеет смысл, когда отдельный доменный тип выражает полезное различие или инвариант. Boxing уменьшает выигрыш в производительности, но не отменяет типобезопасность. Откажитесь от обёртки, если она не уточняет модель или если нужна структура из нескольких полей.

1. **Нужно несколько полей** — это не задача для `value class` (в stable Kotlin поле одно); берите `data class`.

2. **Постоянное использование через интерфейсы** — boxing может убрать выигрыш в аллокациях; если важен доменный тип, измерьте стоимость, прежде чем отказываться.

3. **Большие коллекции** — элементы обычно боксятся, но выбор типа всё равно зависит от модели и измеренной стоимости.

4. **JPA-идентификаторы сущностей** — стандартный `AttributeConverter` не применяется к `@Id`; проверьте возможности провайдера или используйте простой тип идентификатора в сущности.

5. **Простые internal-хелперы, где типобезопасность не нужна** — это лишняя сложность, `typealias` достаточно:

```kotlin
// Over-engineering — typealias достаточно
@JvmInline value class TaskHandler(val handler: (Task) -> Unit)

// Лучше:
typealias TaskHandler = (Task) -> Unit
```

6. **Если критична прозрачность отладки** — из-за инлайнинга и манглинга `value class` в стектрейсах и отладчике выглядит менее очевидно, чем обычный класс.

## Q15. Какие best practices при работе с value class?

Главная мысль: `@JvmInline value class` — инструмент доменного моделирования, а не только микрооптимизация. Применяйте его, чтобы превратить «голые» примитивы в осмысленные типы с гарантированными инвариантами, и держите эти типы рядом с границами модуля, где они не боксятся понапрасну.

1. **Заворачивайте domain primitives** — `UserId`, `Money`, `Email`, `Percentage` вместо «голых» `String`/`Long`.

2. **Проверяйте инвариант в `init`** — тогда некорректное значение невозможно создать в принципе:

```kotlin
@JvmInline
value class Port(val value: Int) {
    init { require(value in 1..65535) }
}
```

3. **Добавляйте operator-функции для доменной арифметики** — тогда операции остаются типобезопасными (`Money + Money`, а не «складываем два `Long`»):

```kotlin
@JvmInline
value class Money(val cents: Long) {
    operator fun plus(other: Money) = Money(cents + other.cents)
    operator fun times(multiplier: Int) = Money(cents * multiplier)
    operator fun compareTo(other: Money): Int = cents.compareTo(other.cents)
}
```

4. **Не натягивайте на большие структуры** — для группы полей это `data class`, а не `value class`.

5. **Прячьте сложное создание за фабрикой** — приватный конструктор плюс `companion`-метод позволяют нормализовать/отклонить вход (здесь — вернуть `null` для невалидного email):

```kotlin
@JvmInline
value class Email private constructor(val value: String) {
    companion object {
        fun of(raw: String): Email? =
            if (raw.matches(EMAIL_REGEX)) Email(raw.trim().lowercase()) else null
    }
}
```

6. **Проверяйте сериализацию** — для kotlinx.serialization используйте `@Serializable`, а с Jackson Kotlin module проверьте версию и round-trip. Собственный сериализатор нужен при особом JSON-контракте или неподдержанной конфигурации.

7. **Поясняйте намерение в комментарии** — зачем здесь именно `value class`, чтобы при рефакторинге его по незнанию не «упростили» до `typealias`, потеряв типобезопасность.

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
