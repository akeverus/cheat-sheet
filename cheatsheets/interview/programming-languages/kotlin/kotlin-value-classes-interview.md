---
title: "Вопросы на собеседовании: Kotlin Value Classes"
description: "Kotlin Value Classes (inline classes): type-safe wrappers без runtime overhead, JVM-представление, ограничения, сравнение с data classes"
tags:
  - interview
  - kotlin
  - kotlin-value-classes-interview
aliases:
  - "Kotlin Value Classes interview"
  - "Kotlin inline classes interview"
  - "Value Classes собеседование"
  - "inline class вопросы"
difficulty: "intermediate"
updated: "2026-04-25"
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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Чем value class отличается от data class?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Как value class представлен в JVM байт-коде?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Какие ограничения у value class?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Как value class работает с интерфейсами?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. В чём разница между @JvmInline value class и обычным typealias?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Как использовать value class с Spring и JPA?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Какие типичные применения value class?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Чем value class Kotlin отличается от record Java 16+?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Как value class взаимодействует с коллекциями?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Почему value class часто требует @JvmInline?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Как value class работает с корутинами и suspend функциями?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Как тестировать value class?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Когда НЕ стоит использовать value class?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Какие best practices при работе с value class?

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

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [Kotlin](kotlin-interview.md) — основы языка, classes, data classes
- [Kotlin Sealed Classes](kotlin-sealed-classes-interview.md) — sealed + value classes для ADT
- [Java Records](../java/java-records-interview.md) — Java аналог (но heap-allocated)
- [Kotlin Serialization](kotlin-serialization-interview.md) — сериализация value classes
- [Kotlin/Java Interop](kotlin-interop-java-interview.md) — как value classes выглядят из Java
- [Kotlin Collections](kotlin-collections-interview.md) — value classes в коллекциях (boxing penalty)
- [Kotlin Coroutines](kotlin-coroutines-interview.md) — value classes в corotines
- [Kotlin + Spring](kotlin-spring-interview.md) — value classes как request/response DTO
- [Domain-Driven Design](../../architecture/ddd-interview.md) — value objects паттерн
- [Java Generics](../java/java-generics-interview.md) — boxing при generic params
