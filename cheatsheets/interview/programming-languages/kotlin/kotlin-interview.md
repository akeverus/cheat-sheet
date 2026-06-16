---
title: "Вопросы на собеседовании: Kotlin"
description: "Комплексное руководство по вопросам собеседования на тему Kotlin: null-safety, классы, корутины, функциональное программирование, делегирование, scope-функции, DSL и интероп с Java."
tags:
  - interview
  - programming-languages
  - kotlin-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Kotlin"
  - "Kotlin interview"
  - "Kotlin собеседование"
prerequisites:
  - "[[kotlin-basics]]"
next: []
updated: "2026-05-08"
---
# Вопросы на собеседовании: `Kotlin`

Комплексное руководство по вопросам собеседования на тему `Kotlin` для `Senior Java/Kotlin Developer`. Включает детальные объяснения концепций, практические примеры на `Kotlin/JVM`, best practices и troubleshooting.

## Полезные ссылки

### Официальная документация

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html) — официальная документация
- [Kotlin Language Specification](https://kotlinlang.org/spec/spec.html) — спецификация языка
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html) — руководство по корутинам

### Baeldung

- [Kotlin Interview Questions — Baeldung](https://www.baeldung.com/kotlin/interview-questions) — подборка вопросов на Baeldung
- [Introduction to the Kotlin Language — Baeldung](https://www.baeldung.com/kotlin/intro) — введение в Kotlin: классы, null-safety, extension functions
- [A Guide to Kotlin's Any, Unit, Nothing — Baeldung](https://www.baeldung.com/kotlin/any-unit-nothing-tutorial) — специальные типы Kotlin
- [Guide to the when{} Block in Kotlin — Baeldung](https://www.baeldung.com/kotlin/when) — when-выражение и его возможности

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы Kotlin**
- [Q1. (!) Что такое `Kotlin` и почему он стал популярен?](#q1--что-такое-kotlin-и-почему-он-стал-популярен)
- [Q2. (!) В чём ключевые отличия Kotlin от Java?](#q2--в-чём-ключевые-отличия-kotlin-от-java)
- [Q3. Какова система типов в Kotlin?](#q3-какова-система-типов-в-kotlin)

**Null-safety**
- [Q4. (!) Как реализована null-safety в Kotlin?](#q4--как-реализована-null-safety-в-kotlin)
- [Q5. В чём разница между `?.` и `!!`?](#q5-в-чём-разница-между--и-)
- [Q6. Что такое оператор Элвис `?:` и `let` для работы с null?](#q6-что-такое-оператор-элвис--и-let-для-работы-с-null)

**Функции и лямбды**
- [Q7. (!) Что такое лямбда-выражения и функциональные типы?](#q7--что-такое-лямбда-выражения-и-функциональные-типы)
- [Q8. Что такое функции высшего порядка?](#q8-что-такое-функции-высшего-порядка)
- [Q9. (!) Что такое функции расширения и как они работают?](#q9--что-такое-функции-расширения-и-как-они-работают)
- [Q10. (!) Что такое `inline` функции и зачем они нужны?](#q10--что-такое-inline-функции-и-зачем-они-нужны)
- [Q11. Что такое `infix` функции?](#q11-что-такое-infix-функции)

**Scope-функции**
- [Q12. (!) Что такое scope-функции (`let`, `run`, `with`, `apply`, `also`)?](#q12--что-такое-scope-функции-let-run-with-apply-also)

**Классы и объекты**
- [Q13. (!) Что такое `data class`?](#q13--что-такое-data-class)
- [Q14. (!) Что такое `sealed class` и `sealed interface`?](#q14--что-такое-sealed-class-и-sealed-interface)
- [Q15. (!) В чём разница между `class`, `object` и `companion object`?](#q15--в-чём-разница-между-class-object-и-companion-object)
- [Q16. Какие типы конструкторов есть в Kotlin?](#q16-какие-типы-конструкторов-есть-в-kotlin)
- [Q17. Что такое `enum class` и чем он отличается от `sealed class`?](#q17-что-такое-enum-class-и-чем-он-отличается-от-sealed-class)
- [Q18. Что такое `value class` (inline class)?](#q18-что-такое-value-class-inline-class)

**Свойства и модификаторы**
- [Q19. (!) В чём разница между `var`, `val` и `const val`?](#q19--в-чём-разница-между-var-val-и-const-val)
- [Q20. (!) В чём разница между `lazy` и `lateinit`?](#q20--в-чём-разница-между-lazy-и-lateinit)
- [Q21. Что такое `Visibility Modifiers` в Kotlin?](#q21-что-такое-visibility-modifiers-в-kotlin)
- [Q22. Что такое ключевое слово `open` и почему классы по умолчанию `final`?](#q22-что-такое-ключевое-слово-open-и-почему-классы-по-умолчанию-final)
- [Q23. Как работают пользовательские геттеры и сеттеры?](#q23-как-работают-пользовательские-геттеры-и-сеттеры)

**Generics и вариантность**
- [Q24. (!) Что такое `Generics` и как работает вариантность (`in`, `out`)?](#q24--что-такое-generics-и-как-работает-вариантность-in-out)
- [Q25. Что такое `reified` type parameters?](#q25-что-такое-reified-type-parameters)
- [Q26. Что такое `Type Inference`?](#q26-что-такое-type-inference)

**Делегирование**
- [Q27. (!) Что такое делегирование классов и свойств?](#q27--что-такое-делегирование-классов-и-свойств)

**Операторы и выражения**
- [Q28. Что такое выражение `when` и чем оно лучше `switch`?](#q28-что-такое-выражение-when-и-чем-оно-лучше-switch)
- [Q29. Что такое перегрузка операторов?](#q29-что-такое-перегрузка-операторов)
- [Q30. В чём разница между `==` и `===`?](#q30-в-чём-разница-между--и-)
- [Q31. Как работает деструктуризация (`destructuring declarations`)?](#q31-как-работает-деструктуризация-destructuring-declarations)

**Коллекции и последовательности**
- [Q32. (!) В чём разница между `List` и `MutableList`?](#q32--в-чём-разница-между-list-и-mutablelist)
- [Q33. В чём разница между `Sequence` и `Iterable`?](#q33-в-чём-разница-между-sequence-и-iterable)
- [Q34. В чём разница между `map` и `flatMap`?](#q34-в-чём-разница-между-map-и-flatmap)

**Корутины (обзор)**
- [Q35. (!) Что такое корутины и чем они отличаются от потоков?](#q35--что-такое-корутины-и-чем-они-отличаются-от-потоков)
- [Q36. Что такое `CoroutineScope`, `Job` и `Dispatcher`?](#q36-что-такое-coroutinescope-job-и-dispatcher)
- [Q37. В чём разница между `launch` и `async`?](#q37-в-чём-разница-между-launch-и-async)

**Интероп с Java**
- [Q38. (!) Какая польза от `@JvmStatic`, `@JvmOverloads` и `@JvmField`?](#q38--какая-польза-от-jvmstatic-jvmoverloads-и-jvmfield)
- [Q39. Что такое плагины `allOpen` и `noArg`?](#q39-что-такое-плагины-allopen-и-noarg)

**Продвинутые темы**
- [Q40. Что такое `Reflection API` в Kotlin?](#q40-что-такое-reflection-api-в-kotlin)
- [Q41. Как работает интерполяция строк (string templates)?](#q41-как-работает-интерполяция-строк-string-templates)
- [Q42. Что такое `contracts` в Kotlin?](#q42-что-такое-contracts-в-kotlin)
- [Q43. (!) Какие best practices при написании идиоматичного Kotlin-кода?](#q43--какие-best-practices-при-написании-идиоматичного-kotlin-кода)
- [Q44. Что такое `typealias` и когда его использовать?](#q44-что-такое-typealias-и-когда-его-использовать)
- [Q45. Что такое `object expression` (анонимный объект) и чем отличается от `object declaration`?](#q45-что-такое-object-expression-анонимный-объект-и-чем-отличается-от-object-declaration)

---

## Q1. (!) Что такое `Kotlin` и почему он стал популярен?

`Kotlin` — статически типизированный язык от `JetBrains`, компилируемый под несколько платформ: `JVM`, `JavaScript` и `Native`. С 2019 года Google рекомендует его как основной язык для Android-разработки.

Популярность языка объясняется не каким-то одним «киллер-фичером», а сочетанием прагматичных решений: он убирает самые болезненные места Java (NPE, многословность), но при этом не ломает совместимость с ней. Главные причины:

| Характеристика | Описание |
|---|---|
| **Null-safety** | Система типов предотвращает `NullPointerException` на уровне компиляции |
| **Лаконичность** | На 30-40% меньше boilerplate-кода по сравнению с Java |
| **Интероп с Java** | 100% совместимость — можно вызывать Java из Kotlin и наоборот |
| **Корутины** | Встроенная поддержка асинхронного программирования |
| **Multiplatform** | Один код для JVM, JS, iOS, Desktop |

Ключевой практический аргумент — постепенная миграция. `Kotlin` компилируется в обычный `JVM`-байткод, совместимый со всей экосистемой Java ([Spring Boot](../../frameworks/spring/spring-boot-interview.md), `Hibernate`, `Gradle` с Kotlin DSL), поэтому в существующий Java-проект можно добавлять Kotlin-классы по одному, не переписывая всё сразу.

## Q2. (!) В чём ключевые отличия Kotlin от Java?

Главная идея Kotlin — взять JVM и устранить хронические боли Java: `NullPointerException`, многословность и небезопасные приведения типов. Отличия удобно разбить на три группы: безопасность (null-safety, smart cast), лаконичность (`data class`, scope-функции, `object`) и более современная модель (корутины, extension functions, значения по умолчанию).

| Аспект | `Kotlin` | `Java` |
|---|---|---|
| Null-safety | Встроена в систему типов (`String?`) | Отсутствует (только аннотации `@Nullable`) |
| Data-классы | `data class` автоматически генерирует `equals`, `hashCode`, `toString`, `copy` | Нужен `record` (Java 16+) или Lombok |
| Расширения | Extension functions / properties | Нет аналога |
| Корутины | `suspend fun`, структурированная конкурентность | `CompletableFuture`, Virtual Threads (Java 21+) |
| Smart casts | Автоматическое приведение типа после проверки | Явный cast |
| По умолчанию | Классы `final`, свойства `val` | Классы открыты, поля `mutable` |
| Scope-функции | `let`, `run`, `apply`, `also`, `with` | Нет |
| Singleton | `object` declaration | Ручная реализация |

```kotlin
// Kotlin: 1 строка
data class User(val name: String, val age: Int)

// Java: ~50 строк (без record/lombok)
// equals, hashCode, toString, getters, constructor...
```

Подробнее о совместимости — [интероп Kotlin и Java](kotlin-interop-java-interview.md).

## Q3. Какова система типов в Kotlin?

Система типов Kotlin — единая иерархия с корнем `Any`, без деления на «примитивы» и «объекты» на уровне языка. Программист всегда работает с типами-объектами (`Int`, `Boolean`, `Char`), а компилятор уже сам, где это безопасно, разворачивает их в JVM-примитивы — поэтому за единообразие синтаксиса не приходится платить производительностью.

```mermaid
graph TD
    Any["Any — корень иерархии"]
    Any --> Number
    Any --> String
    Any --> Boolean
    Any --> Char
    Any --> Unit["Unit (аналог void)"]
    Number --> Int
    Number --> Long
    Number --> Double
    Number --> Float
    Any --> Collection
    Collection --> List
    Collection --> Set
    Collection --> Map
    Nothing["Nothing — подтип всех типов"]
```

Особую роль играют три «специальных» типа — их часто спрашивают именно для проверки понимания системы типов:

- **`Any`** — корень иерархии, аналог `Object` в Java, но без `wait()`/`notify()` (они вынесены из языка как небезопасные).
- **`Unit`** — аналог `void`, но это полноценный тип-singleton с единственным значением. Благодаря этому функцию, возвращающую `Unit`, можно подставлять туда, где ожидается значение (например, в обобщённый код).
- **`Nothing`** — подтип сразу всех типов. Функция, возвращающая `Nothing`, никогда не завершается нормально (`throw` или бесконечный цикл). Это позволяет компилятору понимать, что после вызова такой функции код недостижим — отсюда смарт-каст в примере ниже.
- **Nullable-типы** — `String` и `String?` это два разных типа: `String?` = «либо `String`, либо `null`». Различие проверяется компилятором, а не в рантайме.

```kotlin
fun fail(message: String): Nothing {
    throw IllegalArgumentException(message)
}

val result: String = input ?: fail("input is null") // компилируется!
```

## Q4. (!) Как реализована null-safety в Kotlin?

Суть null-safety в том, что `null` становится частью системы типов, а не рантайм-сюрпризом. Тип `T` гарантированно не содержит `null`, а «может быть null» выражается отдельным типом `T?`. Компилятор не даёт обратиться к члену nullable-значения без явной проверки — поэтому большинство `NullPointerException` отлавливается ещё на компиляции.

```kotlin
var name: String = "Kotlin"   // не может быть null
var nickname: String? = null  // может быть null

// name = null  // Ошибка компиляции!
```

Основные механизмы работы с `null`:

```kotlin
val user: User? = findUser()

// 1. Safe call — ?. (возвращает null если объект null)
val len: Int? = user?.name?.length

// 2. Elvis operator — ?:  (значение по умолчанию)
val displayName: String = user?.name ?: "Anonymous"

// 3. Smart cast — после проверки компилятор знает, что не null
if (user != null) {
    println(user.name) // user уже User, не User?
}

// 4. Safe cast — as? (null вместо ClassCastException)
val str: String? = value as? String

// 5. Not-null assertion — !! (выбрасывает NPE если null)
val forcedName: String = user!!.name // опасно!

// 6. let — идиоматичный способ работы с nullable
user?.let { println("User: ${it.name}") }
```

**На собеседовании** стоит сделать два акцента. Первый: `!!` — это code smell. Он отключает проверку компилятора, поэтому каждое его появление требует обоснования; идиоматичны `?.`, `?:` и `let`. Второй: при вызове Java-кода типы приходят как **platform types** (`String!`) — компилятор не знает их nullability, проверку на null не навязывает, и ответственность за неё ложится на разработчика. Именно через Java-интероп NPE и просачиваются в Kotlin-код.

## Q5. В чём разница между `?.` и `!!`?

Оба оператора работают с nullable-значением, но реагируют на `null` противоположно: `?.` тихо пропускает вызов и отдаёт `null`, а `!!` требует, чтобы значения точно не было null, и падает с исключением, если оно всё-таки null.

**Оператор безопасного вызова `?.`** — если объект `null`, выражение возвращает `null` без исключений:

```kotlin
val name: String? = null
println(name?.length)    // null (без NPE)
println(name?.uppercase()) // null
```

**Not-null assertion `!!`** — выбрасывает `NullPointerException` если значение `null` (с Kotlin 1.4; до этого был отдельный `KotlinNullPointerException`, ныне deprecated):

```kotlin
val name: String? = null
println(name!!.length)   // NullPointerException!
```

**Эмпирическое правило**: `!!` оправдан только когда вы абсолютно уверены, что значение не `null`, и сознательно предпочитаете немедленный crash тихому распространению ошибки. В production-коде это, как правило, анти-паттерн — почти всегда есть более выразительная замена (`?:`, `requireNotNull`, ранний `return`). Уместен он скорее в тестах, где падение «здесь и сейчас» удобнее, чем `null`, всплывший где-то дальше.

## Q6. Что такое оператор Элвис `?:` и `let` для работы с null?

Это два главных идиоматичных инструмента для nullable-значений: `?:` подставляет значение по умолчанию (или прерывает выполнение), когда слева `null`, а `let` выполняет блок только если значение не null. Вместе они заменяют громоздкие `if (x != null) ... else ...`.

**Оператор Элвис `?:`** — возвращает левый операнд, если он не `null`, иначе правый:

```kotlin
val name: String? = null
val displayName = name ?: "Unknown"      // "Unknown"
val length = name?.length ?: 0           // 0
val result = name ?: throw IllegalStateException("name is required")
val fallback = name ?: return            // ранний выход из функции
```

**`let`** — scope-функция для безопасной работы с nullable:

```kotlin
val email: String? = getEmail()

// Блок выполняется только если email не null
email?.let { nonNullEmail ->
    sendVerification(nonNullEmail)
    log("Sent to $nonNullEmail")
}

// Цепочка с let и Elvis
val formatted = input?.let { parse(it) }?.let { validate(it) } ?: default
```

## Q7. (!) Что такое лямбда-выражения и функциональные типы?

Лямбда-выражение — это анонимная функция, записанная как значение, которое можно сохранить в переменную, передать в другую функцию или вернуть из неё. Это возможно потому, что в Kotlin функции — **граждане первого класса**: у функций есть собственные типы (функциональные типы вида `(Int, Int) -> Int`), и переменная такого типа хранит вызываемое значение.

```kotlin
// Функциональный тип: (Int, Int) -> Int
val sum: (Int, Int) -> Int = { a, b -> a + b }

// it — неявное имя единственного параметра
val double: (Int) -> Int = { it * 2 }

// Trailing lambda — лямбда вынесена за скобки
val evens = listOf(1, 2, 3, 4, 5).filter { it % 2 == 0 }

// Ссылка на функцию (function reference)
fun isPositive(n: Int) = n > 0
val positives = listOf(-1, 2, -3, 4).filter(::isPositive)
```

Функциональный тип с `receiver` — основа для DSL (подробнее в [DSL в Kotlin](kotlin-dsl-interview.md)):

```kotlin
// Тип: String.() -> Unit — лямбда с receiver String
fun buildGreeting(block: StringBuilder.() -> Unit): String {
    return StringBuilder().apply(block).toString()
}

val greeting = buildGreeting {
    append("Hello, ")
    append("Kotlin!")
}
```

## Q8. Что такое функции высшего порядка?

Функция высшего порядка (higher-order function, HOF) — это функция, которая принимает другую функцию как параметр или возвращает функцию. Возможно это благодаря тому, что функции в Kotlin — значения первого класса (см. Q7). HOF — основа декларативного стиля: вместо ручного цикла вы описываете «что сделать с элементом», а как именно его обойти, решает сама функция.

```kotlin
// Принимает функцию как параметр
fun calculate(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

val sum = calculate(10, 5) { x, y -> x + y }       // 15
val diff = calculate(10, 5) { x, y -> x - y }      // 5

// Возвращает функцию
fun multiplier(factor: Int): (Int) -> Int = { it * factor }
val triple = multiplier(3)
println(triple(5))  // 15
```

Стандартная библиотека `Kotlin` активно использует HOF: `map`, `filter`, `reduce`, `fold`, `groupBy`, `flatMap` и др. (подробнее в [коллекциях Kotlin](kotlin-collections-interview.md)).

## Q9. (!) Что такое функции расширения и как они работают?

Функции расширения (extension functions) позволяют «дописать» новый метод к уже существующему классу — **без изменения его исходного кода** и **без наследования**. Это лишь синтаксический сахар: внутри `this` указывает на объект-получатель, но реального доступа к внутренностям класса функция не получает. Главная польза — вызывать утилитарную логику в естественном виде `obj.helper()` вместо `Helper.process(obj)`.

```kotlin
// Расширение для String
fun String.wordCount(): Int = this.split("\\s+".toRegex()).size

println("Hello Kotlin World".wordCount()) // 3

// Расширение для коллекций
fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null

// Extension property
val String.lastChar: Char
    get() = this[length - 1]
```

**Важные нюансы** (часто спрашивают на собеседовании):

1. **Разрешаются статически** — выбор функции делается по объявленному типу переменной на этапе компиляции, а не по фактическому типу объекта в рантайме. Это принципиальное отличие от обычных (виртуальных) методов, и именно здесь чаще всего ошибаются:
```kotlin
open class Shape
class Circle : Shape()

fun Shape.name() = "Shape"
fun Circle.name() = "Circle"

val shape: Shape = Circle()
println(shape.name()) // "Shape" — не "Circle"!
```

2. **Не могут обращаться к `private`/`protected` членам** класса
3. **Компилируются в статические методы** — `fun String.foo()` станет `public static void foo(String $this)`
4. Член класса имеет приоритет над extension с тем же именем

## Q10. (!) Что такое `inline` функции и зачем они нужны?

`inline` приказывает компилятору подставить тело функции прямо в место вызова, вместо обычного вызова метода. Проблема, которую это решает: каждая лямбда по умолчанию компилируется в отдельный объект `Function`, и при частых вызовах (например, внутри цикла) это лишние аллокации. Когда функция `inline`, и она сама, и переданная в неё лямбда встраиваются в код вызывающего — объект лямбды не создаётся вовсе, а накладные расходы на вызов исчезают.

```kotlin
inline fun <T> measureTime(block: () -> T): T {
    val start = System.nanoTime()
    val result = block()
    println("Took ${System.nanoTime() - start} ns")
    return result
}

// При компиляции тело measureTime подставляется inline
val data = measureTime { loadFromDatabase() }
```

Дополнительный бонус: только из inline-лямбды разрешён нелокальный `return` (выход из внешней функции), а из inline-функции можно делать `reified` type-параметры (см. Q25).

**Когда использовать**: для функций с лямбда-параметрами, чтобы убрать аллокацию объекта `Function`. Именно поэтому все стандартные scope-функции (`let`, `run`, `apply`, `also`, `with`) объявлены `inline`. А вот инлайнить функции без лямбд почти бесполезно — выигрыша нет, зато раздувается байткод.

**`noinline`** — запрещает инлайнинг конкретного лямбда-параметра (например, если эту лямбду нужно сохранить в переменную или передать дальше):

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) { ... }
```

**`crossinline`** — лямбда остаётся встроенной, но из неё запрещён нелокальный `return`. Нужен, когда лямбда вызывается не напрямую, а из другого контекста (например, внутри `Thread`), где такой `return` был бы небезопасен:

```kotlin
inline fun runInThread(crossinline block: () -> Unit) {
    Thread { block() }.start() // block не может сделать return из runInThread
}
```

## Q11. Что такое `infix` функции?

`infix` — модификатор, который разрешает вызывать функцию в инфиксной форме, без точки и скобок: `a power b` вместо `a.power(b)`. Цель — читаемость кода, близкого к естественному языку (отсюда `1 to "a"`, `5 in 1..10`). Модификатор применим, только если функция: (1) является членом класса или extension-функцией и (2) принимает ровно один параметр.

```kotlin
infix fun Int.power(exp: Int): Int {
    var result = 1
    repeat(exp) { result *= this }
    return result
}

val result = 2 power 10   // 1024 (вместо 2.power(10))

// Стандартные infix-функции:
val pair = "key" to "value"       // Pair
val check = 5 in 1..10            // contains
val result2 = true and false      // Boolean
```

## Q12. (!) Что такое scope-функции (`let`, `run`, `with`, `apply`, `also`)?

Scope-функции — пять стандартных функций, выполняющих блок кода «в контексте» объекта, чтобы обращаться к нему компактно, без повторения имени переменной. Они различаются всего по двум осям, и именно их и спрашивают: **как объект доступен внутри** (`this` или `it`) и **что функция возвращает** (сам объект или результат лямбды). Из этих двух осей и выводится выбор нужной функции.

| Функция | Объект как | Возвращает | Типичное использование |
|---------|-----------|-----------|----------------------|
| `let` | `it` | результат лямбды | null-check, преобразование |
| `run` | `this` | результат лямбды | конфигурация + вычисление |
| `with` | `this` | результат лямбды | группировка вызовов (не extension) |
| `apply` | `this` | сам объект | конфигурация объекта (builder) |
| `also` | `it` | сам объект | побочные эффекты (логирование) |

```kotlin
// let — работа с nullable
val name: String? = "Kotlin"
name?.let { println("Length: ${it.length}") }

// apply — конфигурация объекта
val config = HttpClient().apply {
    timeout = 30_000
    retries = 3
    baseUrl = "https://api.example.com"
}

// also — побочный эффект (logging, validation)
val user = createUser()
    .also { log.info("Created user: ${it.id}") }
    .also { require(it.isValid()) }

// run — вычисление с контекстом
val greeting = user.run { "Hello, $name! Age: $age" }

// with — группировка вызовов
with(canvas) {
    drawRect(0, 0, 100, 100)
    drawText("Hello", 50, 50)
    drawLine(0, 0, 100, 100)
}
```

**На собеседовании** удобно держать в голове связку «возвращает → используй»: нужен сам объект (конфигурация, side-effect) — бери `apply`/`also`; нужен результат вычисления — бери `let`/`run`/`with`. А `this`-вариант (`run`, `apply`, `with`) удобен, когда внутри много вызовов методов объекта, а `it`-вариант (`let`, `also`) — когда объект передаётся как аргумент или есть риск спутать его с внешним `this`.

## Q13. (!) Что такое `data class`?

`data class` — это класс «для хранения данных», для которого компилятор сам, на основе свойств из первичного конструктора, генерирует рутину: `equals()`, `hashCode()`, `toString()`, `copy()` и набор `componentN()` для деструктуризации. Тем самым исчезает boilerplate, который в Java приходится писать руками (или генерировать IDE/Lombok), а сам класс получает семантику равенства «по значению».

```kotlin
data class User(
    val name: String,
    val email: String,
    val age: Int = 0
)

val user1 = User("Alice", "alice@mail.com", 30)
val user2 = user1.copy(name = "Bob")  // копия с изменённым полем

// Деструктуризация
val (name, email, age) = user1

// toString()
println(user1) // User(name=Alice, email=alice@mail.com, age=30)

// equals по значению
println(user1 == User("Alice", "alice@mail.com", 30)) // true
```

**Ограничения и нюансы:**
- Должен иметь хотя бы один параметр в primary constructor
- Параметры конструктора — `val` или `var` (рекомендуется `val` для иммутабельности)
- Не может быть `abstract`, `open`, `sealed` или `inner`
- `equals`/`hashCode` учитывают **только** свойства из primary constructor
- Свойства в `body` класса **не участвуют** в `equals`/`hashCode` — частый источник багов

```kotlin
data class Tricky(val id: Int) {
    var name: String = ""  // НЕ участвует в equals/hashCode!
}
```

## Q14. (!) Что такое `sealed class` и `sealed interface`?

`sealed class` — абстрактный класс с закрытым (известным компилятору) набором прямых наследников. Все они должны лежать в том же модуле и пакете (с Kotlin 1.5+; раньше требовался тот же файл). Главная ценность — исчерпывающий `when`: раз компилятор знает все варианты, он проверяет, что вы обработали каждый, и не требует ветки `else`. Это идеальный инструмент для моделирования замкнутого множества состояний (результат запроса, события UI, узлы AST).

```kotlin
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

// Исчерпывающий when — компилятор проверяет все варианты
fun handleResult(result: NetworkResult<String>): String = when (result) {
    is NetworkResult.Success -> "Data: ${result.data}"
    is NetworkResult.Error   -> "Error ${result.code}: ${result.message}"
    is NetworkResult.Loading -> "Loading..."
    // else не нужен — все варианты покрыты!
}
```

```mermaid
graph TD
    Sealed["sealed class NetworkResult"]
    Sealed --> Success["data class Success"]
    Sealed --> Error["data class Error"]
    Sealed --> Loading["data object Loading"]
    style Sealed fill:#f9f,stroke:#333
```

**Sealed vs Enum:**

| | `sealed class` | `enum class` |
|---|---|---|
| Экземпляры | Каждый подкласс может иметь разное состояние | Фиксированные singleton-экземпляры |
| Иерархия | Разные классы с разными свойствами | Одинаковая структура |
| `when` | Исчерпывающая проверка | Исчерпывающая проверка |

`sealed interface` (Kotlin 1.5+) — то же, но класс может реализовать несколько sealed interfaces.

## Q15. (!) В чём разница между `class`, `object` и `companion object`?

Кратко: `class` — это шаблон, по которому создают много экземпляров; `object` — синглтон, единственный экземпляр которого создаёт сам язык; `companion object` — это `object`, привязанный к конкретному классу и заменяющий «статические» члены из Java. Разберём по порядку.

```kotlin
// 1. class — обычный класс, можно создавать экземпляры
class UserService(private val repo: UserRepository)

// 2. object — синглтон (один экземпляр на весь процесс)
object DatabaseConfig {
    val url = "jdbc:postgresql://localhost/db"
    fun connect() { /* ... */ }
}
DatabaseConfig.connect() // доступ через имя объекта

// 3. companion object — «статические» члены класса
class User(val name: String) {
    companion object Factory {
        fun create(name: String) = User(name)
        const val MAX_NAME_LENGTH = 50
    }
}
val user = User.create("Alice")   // вызов через имя класса
```

**Ключевые отличия:**
- `object` — глобальный синглтон, потокобезопасная ленивая инициализация
- `companion object` — привязан к классу, может реализовывать интерфейсы, может использоваться как factory
- В JVM `companion object` компилируется во вложенный класс `Companion`

## Q16. Какие типы конструкторов есть в Kotlin?

В Kotlin два вида конструкторов плюс блоки инициализации. **Первичный конструктор** объявляется прямо в заголовке класса и обычно используется как основной. **Вторичные конструкторы** объявляются в теле и обязаны в итоге делегировать первичному. Логику, не помещающуюся в заголовок, выносят в `init`-блоки.

```kotlin
// Primary constructor — в заголовке класса
class User(val name: String, val age: Int = 0)

// Secondary constructor — в теле класса, ДОЛЖЕН делегировать к primary
class User(val name: String) {
    var email: String = ""

    constructor(name: String, email: String) : this(name) {
        this.email = email
    }
}

// init-блок — выполняется как часть primary constructor
class User(val name: String) {
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
    }
}
```

**Порядок инициализации:** primary constructor параметры -> свойства и `init`-блоки (сверху вниз) -> secondary constructor body.

## Q17. Что такое `enum class` и чем он отличается от `sealed class`?

`enum class` задаёт фиксированный набор именованных констант-синглтонов одного типа; каждой можно приписать данные и поведение. От `sealed class` он отличается главным: у всех значений enum одинаковая структура (поля те же, меняются лишь значения), тогда как наследники sealed-класса могут иметь совершенно разный набор полей.

```kotlin
enum class HttpStatus(val code: Int, val description: String) {
    OK(200, "Success"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error");

    fun isSuccess() = code in 200..299
}

// Использование
val status = HttpStatus.OK
println(status.code)         // 200
println(HttpStatus.valueOf("OK")) // HttpStatus.OK
HttpStatus.entries.forEach { println(it) } // итерация
```

Для моделирования **состояний с разным набором данных** используйте `sealed class` (см. Q14), для **фиксированных констант** — `enum class`.

## Q18. Что такое `value class` (inline class)?

`value class` (ранее `inline class`) — это типобезопасная обёртка над одним значением, которая в большинстве случаев не создаёт объект в рантайме. Компилятор «разворачивает» обёртку и работает напрямую с обёрнутым значением, поэтому вы получаете отдельный тип без накладных расходов на аллокацию и разыменование.

```kotlin
@JvmInline
value class Email(val value: String) {
    init { require(value.contains("@")) { "Invalid email" } }
}

@JvmInline
value class UserId(val id: Long)

// Компилятор использует Long напрямую, без создания объекта
fun findUser(id: UserId): User = repository.findById(id.id)
```

**Сценарий применения**: type-safety без аллокаций. В сигнатуре `fun send(to: Email, from: Email)` параметры нельзя перепутать местами, а в `fun send(to: String, from: String)` — легко (компилятор не заметит). Важный нюанс: обёртка всё же боксируется обратно в объект там, где нужен ссылочный тип — например, когда значение кладётся в nullable-переменную или коллекцию `List<Email>`.

## Q19. (!) В чём разница между `var`, `val` и `const val`?

Три уровня «неизменности»: `var` — изменяемая переменная; `val` — read-only ссылка (присвоить заново нельзя, но это не гарантирует неизменность самого объекта); `const val` — настоящая константа, вычисляемая на этапе компиляции и подставляемая в код как литерал.

| | `var` | `val` | `const val` |
|---|---|---|---|
| Изменяемость | Mutable | Read-only (immutable reference) | Compile-time constant |
| Инициализация | Любой момент | При объявлении или `lazy` | Во время компиляции |
| Тип | Любой | Любой | Примитивы и `String` |
| Где | Везде | Везде | Top-level, `object`, `companion object` |

```kotlin
var counter = 0           // можно менять
counter = 1

val list = mutableListOf(1, 2, 3)  // ссылка неизменна, содержимое — да!
// list = otherList // Ошибка!
list.add(4)               // OK — содержимое мутабельно

const val MAX_SIZE = 100  // подставляется как литерал при компиляции
```

**Важно**: `val` — **не** константа. Это read-only ссылка. Custom getter может возвращать разные значения:

```kotlin
val currentTime: Long
    get() = System.currentTimeMillis()  // каждый раз разное!
```

## Q20. (!) В чём разница между `lazy` и `lateinit`?

Оба механизма откладывают инициализацию свойства, но решают разные задачи. `lazy` — для `val`, который вы хотите вычислить лениво, при первом обращении, и затем закэшировать (вы сами задаёте, *как* его вычислить). `lateinit` — для `var`, который вы инициализируете позже извне (DI-контейнер, `setUp` в тесте), но при объявлении значения ещё нет. Главное практическое правило: если инициализирующая логика известна на месте — `lazy`; если значение придёт снаружи — `lateinit`.

| | `lazy` | `lateinit` |
|---|---|---|
| Ключевое слово | `val` | `var` |
| Тип | Любой | Не примитив |
| Потокобезопасность | По умолчанию да (`LazyThreadSafetyMode.SYNCHRONIZED`) | Нет |
| Nullable | Может быть nullable | Не может |
| Проверка инициализации | Всегда инициализирован при доступе | `::prop.isInitialized` |

```kotlin
// lazy — вычисляется при первом доступе
val heavyObject: ExpensiveService by lazy {
    println("Initializing...")
    ExpensiveService()  // вызывается один раз
}

// lateinit — инициализируется позже (DI, setUp в тестах)
class UserServiceTest {
    lateinit var service: UserService

    @BeforeEach
    fun setUp() {
        service = UserService(mockRepo)
    }

    @Test
    fun test() {
        // Если забыли setUp — UninitializedPropertyAccessException
        if (::service.isInitialized) { /* safe */ }
    }
}
```

## Q21. Что такое `Visibility Modifiers` в Kotlin?

Модификаторы видимости управляют тем, откуда виден объявленный элемент. В Kotlin их четыре, и по умолчанию всё `public`. Главное отличие от Java — наличие `internal` (видимость в пределах модуля) вместо `package-private`.

| Модификатор | Класс / Top-level | Член класса |
|---|---|---|
| `public` (по умолчанию) | Виден везде | Виден везде |
| `internal` | Виден в модуле | Виден в модуле |
| `protected` | Недоступен для top-level | Виден в подклассах |
| `private` | Виден в файле | Виден в классе |

**Отличие от Java**: в Kotlin нет `package-private` (по умолчанию в Java). Вместо этого — `internal` (видимость модуля), что лучше подходит для мультимодульных проектов. По умолчанию в Kotlin всё `public`.

## Q22. Что такое ключевое слово `open` и почему классы по умолчанию `final`?

В `Kotlin` все классы и методы **`final`** по умолчанию — их нельзя наследовать/переопределять без явного `open`.

```kotlin
open class Animal {
    open fun speak() = "..."      // можно переопределить
    fun breathe() = "breathing"   // нельзя переопределить
}

class Dog : Animal() {
    override fun speak() = "Woof!"
    // override fun breathe() — ошибка компиляции!
}
```

**Почему по умолчанию `final`**: принцип "Design for inheritance or prohibit it" (Effective Java, Item 19). Случайное наследование — частый источник багов. Если класс не спроектирован для расширения, он должен быть `final`.

Плагин `kotlin-allopen` делает классы с определённой аннотацией `open` автоматически — необходимо для `Spring` (AOP-прокси требуют не-final классов).

## Q23. Как работают пользовательские геттеры и сеттеры?

В Kotlin вы объявляете не поле, а свойство (property), и под свойством по умолчанию стоят автоматические `get`/`set`. Их можно переопределить, чтобы добавить валидацию, ленивое вычисление или ограничить доступ. Внутри аксессора к скрытому хранилищу обращаются через ключевое слово `field` (backing field). Если `field` не используется (свойство вычисляется на лету), хранилище вообще не создаётся.

```kotlin
class Temperature {
    var celsius: Double = 0.0
        set(value) {
            require(value >= -273.15) { "Below absolute zero" }
            field = value  // field — backing field
        }

    // Computed property — нет backing field
    val fahrenheit: Double
        get() = celsius * 9 / 5 + 32

    // Private setter — извне только чтение
    var updateCount: Int = 0
        private set

    fun update(value: Double) {
        celsius = value
        updateCount++
    }
}
```

`field` — специальный идентификатор для обращения к backing field внутри get/set. Если свойство не использует `field` (computed property), backing field не создаётся.

## Q24. (!) Что такое `Generics` и как работает вариантность (`in`, `out`)?

Дженерики позволяют писать код, параметризованный типом (`List<T>`), а вариантность отвечает на вопрос: «если `Cat` — подтип `Animal`, то является ли `Box<Cat>` подтипом `Box<Animal>`?». В Kotlin вариантность задаётся **в месте объявления** типа (declaration-site) — модификаторами `out` и `in` прямо у параметра. Это удобнее, чем use-site variance в Java, где `? extends`/`? super` приходится повторять на каждом использовании.

Смысл модификаторов прост:
- **`out T`** — тип `T` только «производится» (возвращается). Такой класс ковариантен: `Source<Cat>` можно подставить туда, где ждут `Source<Animal>`.
- **`in T`** — тип `T` только «потребляется» (принимается параметром). Класс контравариантен: `Sink<Animal>` подходит туда, где ждут `Sink<Cat>`.

```kotlin
// out = ковариантность (producer) — аналог ? extends в Java
interface Source<out T> {
    fun next(): T          // T только в out-позиции (возвращаемый тип)
}

// in = контравариантность (consumer) — аналог ? super в Java
interface Sink<in T> {
    fun put(item: T)       // T только в in-позиции (параметр)
}

// Invariant — и producer, и consumer
class MutableBox<T>(var value: T)
```

Мнемоника **PECS** (Producer Extends, Consumer Super) или в Kotlin — **`out` = produce, `in` = consume**.

```kotlin
// Star projection — аналог ? в Java
fun printAll(list: List<*>) {
    list.forEach { println(it) }  // it: Any?
}
```

Подробнее о generics — [Generics в Java](../java/java-generics-interview.md).

## Q25. Что такое `reified` type parameters?

Из-за стирания типов (type erasure) на JVM обобщённый параметр `T` в обычной функции в рантайме недоступен — нельзя написать `T::class` или `value is T`. Модификатор `reified` решает это для inline-функций: поскольку их тело подставляется в место вызова, компилятор знает конкретный тип на месте и «материализует» его. Поэтому `reified` доступен только вместе с `inline`.

```kotlin
// Без reified — нужен Class<T> параметр
fun <T> toJson(obj: T, clazz: Class<T>): String = mapper.writeValueAsString(obj)

// С reified — тип доступен как T
inline fun <reified T> fromJson(json: String): T {
    return mapper.readValue(json, T::class.java)  // T::class доступен!
}

// Проверка типа
inline fun <reified T> isInstance(value: Any): Boolean = value is T

// Использование
val user = fromJson<User>("""{"name":"Alice"}""")
println(isInstance<String>("hello"))  // true
```

Без `reified` выражения `T::class` и `value is T` не компилируются из-за стирания типов.

## Q26. Что такое `Type Inference`?

Вывод типов (type inference) — это способность компилятора сам определить тип переменной, выражения или возвращаемого значения по контексту, чтобы его не приходилось писать руками. При этом язык остаётся статически типизированным: тип фиксируется на компиляции, просто его не нужно дублировать в коде.

```kotlin
val name = "Kotlin"          // String
val numbers = listOf(1, 2, 3) // List<Int>
val map = mapOf("a" to 1)    // Map<String, Int>

// Тип возвращаемого значения выводится
fun double(x: Int) = x * 2   // : Int (выведен)

// Но для public API рекомендуется указывать явно
fun createService(): UserService = UserServiceImpl()
```

**Когда указывать тип явно**: public API (для читаемости и стабильности), сложные выражения, где тип неочевиден.

## Q27. (!) Что такое делегирование классов и свойств?

Делегирование — это «передача работы другому объекту», и Kotlin встраивает этот паттерн прямо в язык через ключевое слово `by`. Есть два независимых вида: делегирование реализации интерфейса (класс перепоручает методы другому объекту вместо наследования) и делегирование свойств (его `get`/`set` обрабатывает объект-делегат). Оба избавляют от ручного шаблонного кода — «композиция вместо наследования» получается почти бесплатно.

**Делегирование класса** — реализация интерфейса делегируется другому объекту:

```kotlin
interface Logger {
    fun log(message: String)
}

class ConsoleLogger : Logger {
    override fun log(message: String) = println("[LOG] $message")
}

// UserService реализует Logger, делегируя вызовы в logger
class UserService(private val logger: Logger) : Logger by logger {
    fun createUser(name: String) {
        log("Creating user $name")  // вызывается logger.log()
    }
}
```

**Делегирование свойств** — get/set делегируются объекту-делегату:

```kotlin
// Стандартные делегаты
val lazyValue: String by lazy { computeExpensiveValue() }
var observed: String by Delegates.observable("initial") { _, old, new ->
    println("Changed from $old to $new")
}
var cached: String by Delegates.vetoable("valid") { _, _, new ->
    new.isNotBlank()  // отклоняет пустые значения
}

// Map-делегат — свойства из Map
class Config(map: Map<String, Any>) {
    val host: String by map
    val port: Int by map
}
val config = Config(mapOf("host" to "localhost", "port" to 8080))
```

**Кастомный делегат:**

```kotlin
class Trimmed : ReadWriteProperty<Any?, String> {
    private var value = ""
    override fun getValue(thisRef: Any?, property: KProperty<*>) = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value.trim()
    }
}

class Form {
    var name: String by Trimmed()  // автоматически trim при set
}
```

## Q28. Что такое выражение `when` и чем оно лучше `switch`?

`when` — это аналог `switch`, но не «оператор выбора по константе», а полноценное выражение, которое возвращает значение и умеет ветвиться по произвольным условиям: проверке типа (со смарт-кастом), диапазонам, любым булевым выражениям. Плюс у него нет коварного fall-through из Java — ветки не «проваливаются» друг в друга.

```kotlin
// Как выражение (возвращает значение)
val result = when (status) {
    HttpStatus.OK -> "Success"
    HttpStatus.NOT_FOUND -> "Not found"
    HttpStatus.INTERNAL_ERROR -> "Server error"
}

// Проверка типа (smart cast)
fun describe(obj: Any): String = when (obj) {
    is String  -> "String of length ${obj.length}"  // smart cast!
    is Int     -> "Integer: $obj"
    is List<*> -> "List of size ${obj.size}"
    else       -> "Unknown"
}

// Диапазоны и условия
fun classify(n: Int) = when {
    n < 0      -> "Negative"
    n in 1..10 -> "Small"
    n in 11..100 -> "Medium"
    else       -> "Large"
}

// Sealed class — else не нужен
sealed class Shape
data class Circle(val r: Double) : Shape()
data class Rect(val w: Double, val h: Double) : Shape()

fun area(shape: Shape): Double = when (shape) {
    is Circle -> Math.PI * shape.r * shape.r
    is Rect   -> shape.w * shape.h
    // Компилятор знает все варианты — else не нужен
}
```

**Преимущества перед `switch`**: нет fall-through, поддержка произвольных выражений, smart cast, exhaustive check для sealed/enum.

## Q29. Что такое перегрузка операторов?

Перегрузка операторов позволяет задать поведение стандартных символов (`+`, `-`, `*`, `[]`, `in` и др.) для своих типов. Делается это не произвольно: за каждым оператором закреплено фиксированное имя функции (`plus`, `minus`, `get`…), и достаточно объявить функцию с этим именем и модификатором `operator`. После этого `a + b` компилятор разворачивает в `a.plus(b)`. Цель — выразительный код там, где операторная запись естественна (векторы, деньги, матрицы, диапазоны).

```kotlin
data class Vector(val x: Double, val y: Double) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar)
    operator fun unaryMinus() = Vector(-x, -y)
}

val v1 = Vector(1.0, 2.0)
val v2 = Vector(3.0, 4.0)
println(v1 + v2)       // Vector(4.0, 6.0)
println(v1 * 2.0)      // Vector(2.0, 4.0)
println(-v1)            // Vector(-1.0, -2.0)
```

Стандартные операторы: `plus` (+), `minus` (-), `times` (*), `div` (/), `rem` (%), `rangeTo` (..), `contains` (in), `get`/`set` ([]), `invoke` (()), `compareTo` (<, >, <=, >=).

## Q30. В чём разница между `==` и `===`?

Это два разных вопроса о равенстве. `==` спрашивает «равны ли значения по содержанию», `===` — «это вообще один и тот же объект в памяти». Обратите внимание: в Kotlin `==` (в отличие от Java) — это `equals`, а не сравнение ссылок, поэтому привычная по Java ловушка «сравнивать строки через `==`» здесь не возникает.

- **`==`** — структурное равенство, вызывает `equals()` с защитой от null: `a == b` -> `a?.equals(b) ?: (b === null)`.
- **`===`** — ссылочное равенство (один и тот же объект в памяти).

```kotlin
val a = "hello"
val b = "hello"
println(a == b)    // true — содержимое одинаково
println(a === b)   // true — строки интернированы

data class Point(val x: Int, val y: Int)
val p1 = Point(1, 2)
val p2 = Point(1, 2)
println(p1 == p2)  // true — data class генерирует equals по полям
println(p1 === p2) // false — разные объекты в куче
```

## Q31. Как работает деструктуризация (`destructuring declarations`)?

Деструктуризация — это синтаксис, который «распаковывает» объект сразу в несколько переменных за одно объявление. Механика простая: запись `val (a, b) = obj` компилятор разворачивает в `val a = obj.component1(); val b = obj.component2()`. То есть работает она с любым типом, у которого есть `operator fun componentN()` — а `data class` генерирует их автоматически.

```kotlin
// data class автоматически генерирует componentN()
data class User(val name: String, val age: Int)
val (name, age) = User("Alice", 30)

// В циклах
for ((key, value) in mapOf("a" to 1, "b" to 2)) {
    println("$key -> $value")
}

// В лямбдах
listOf(User("Alice", 30), User("Bob", 25))
    .forEach { (name, age) -> println("$name is $age") }

// Пропуск компонентов
val (_, email) = getUserData()
```

**Подводный камень**: компоненты сопоставляются по позиции, а не по имени. Если в `data class` поменять порядок свойств, все существующие деструктуризации молча начнут читать не те значения — компилятор ошибки не увидит.

## Q32. (!) В чём разница между `List` и `MutableList`?

Kotlin разделяет коллекции на два интерфейса: `List` — только для чтения (нет методов `add`/`remove`), `MutableList` — для чтения и изменения. Это разделение существует на уровне типов и помогает выражать намерение: принимая `List`, функция декларирует, что не будет модифицировать коллекцию. **Важная оговорка**: `List` гарантирует лишь отсутствие методов изменения у *этого* интерфейса, но не неизменность самих данных — за тем же объектом может стоять `MutableList` (см. пример ниже).

```mermaid
graph TD
    Iterable --> Collection
    Collection --> List["List (read-only)"]
    Collection --> Set["Set (read-only)"]
    Collection --> MutableCollection
    MutableCollection --> MutableList
    MutableCollection --> MutableSet
    List -.->|"реализует"| MutableList
```

```kotlin
val readOnly: List<String> = listOf("a", "b", "c")
// readOnly.add("d")  // Ошибка компиляции!

val mutable: MutableList<String> = mutableListOf("a", "b", "c")
mutable.add("d")  // OK

// List — не гарантирует иммутабельность! Это просто read-only view
val underlying = mutableListOf(1, 2, 3)
val readOnlyView: List<Int> = underlying
underlying.add(4)
println(readOnlyView) // [1, 2, 3, 4] — сюрприз!

// Для настоящей иммутабельности — копирование:
val immutable = underlying.toList()
```

Подробнее — [Kotlin коллекции](kotlin-collections-interview.md).

## Q33. В чём разница между `Sequence` и `Iterable`?

`Iterable` вычисляется жадно (eager) и создаёт промежуточную коллекцию на каждом шаге, а `Sequence` обрабатывает элементы лениво (lazy) и поэлементно, без промежуточных списков.

| | `Iterable` (коллекции) | `Sequence` |
|---|---|---|
| Вычисление | Eager (сразу) | Lazy (по требованию) |
| Промежуточные коллекции | Создаются на каждом шаге | Не создаются |
| Подходит | Маленькие коллекции | Большие коллекции, цепочки операций |

```kotlin
// Iterable — каждый шаг создаёт новый List
val result1 = (1..1_000_000)
    .filter { it % 2 == 0 }     // создаёт List
    .map { it * 2 }              // создаёт ещё один List
    .take(10)                    // создаёт ещё один List

// Sequence — поэлементная обработка, без промежуточных списков
val result2 = (1..1_000_000).asSequence()
    .filter { it % 2 == 0 }     // lazy
    .map { it * 2 }              // lazy
    .take(10)                    // lazy
    .toList()                    // терминальная операция — запускает конвейер
```

**Sequence** в Kotlin аналогичен `Stream` API в Java 8+, но проще в использовании и не требует `parallel()`.

## Q34. В чём разница между `map` и `flatMap`?

`map` преобразует каждый элемент один к одному, а `flatMap` — один ко многим с последующим «раскрытием» вложенных коллекций в один плоский список.

```kotlin
val words = listOf("Hello World", "Kotlin is great")

// map: List<T> -> List<R> (1 к 1)
val lengths = words.map { it.length }   // [11, 15]

// flatMap: List<T> -> List<R> (1 ко многим, результат «раскрывается»)
val chars = words.flatMap { it.toList() }
// [H, e, l, l, o, ' ', W, o, r, l, d, K, o, t, l, i, n, ...]

// Пример: получить все теги всех постов
data class Post(val tags: List<String>)
val posts = listOf(Post(listOf("kotlin", "jvm")), Post(listOf("spring", "kotlin")))
val allTags = posts.flatMap { it.tags }  // [kotlin, jvm, spring, kotlin]
val uniqueTags = allTags.toSet()         // [kotlin, jvm, spring]
```

`flatMap` = `map` + `flatten`. Используется когда из каждого элемента получается коллекция, и нужен плоский результат.

## Q35. (!) Что такое корутины и чем они отличаются от потоков?

Корутина — это вычисление, которое можно приостановить (`suspend`) и позже возобновить, не блокируя при этом поток. Ключевое отличие от потоков: пока корутина «ждёт» (например, ответ по сети), она освобождает поток ОС для другой работы, тогда как заблокированный поток просто простаивает. Поэтому корутины несравнимо легче — на одном пуле потоков их могут крутиться миллионы. По сути это инструмент для асинхронного кода, который выглядит как обычный последовательный. Подробные вопросы — в [Kotlin Coroutines](kotlin-coroutines-interview.md).

```mermaid
graph LR
    subgraph Thread["Один поток ОС"]
        C1["Coroutine 1<br>suspend...resume"]
        C2["Coroutine 2<br>suspend...resume"]
        C3["Coroutine 3<br>suspend...resume"]
    end
    style Thread fill:#e1f5fe
```

| | Потоки (Threads) | Корутины (Coroutines) |
|---|---|---|
| Стоимость создания | ~1 MB стека | ~несколько сотен байт |
| Количество | Тысячи (ограничено ОС) | Миллионы |
| Блокировка | Блокирует поток ОС | Приостанавливает (suspend) без блокировки |
| Переключение | Context switch ОС (дорого) | Переключение в user-space (дёшево) |
| Отмена | Прерывание (`interrupt`) | Структурированная отмена (`cancel`) |

```kotlin
suspend fun fetchUser(): User {
    // suspend-функция не блокирует поток
    val response = httpClient.get("https://api.example.com/user") // suspend point
    return response.body()
}

// Запуск корутины
val scope = CoroutineScope(Dispatchers.IO)
scope.launch {
    val user = fetchUser()
    withContext(Dispatchers.Main) {
        updateUI(user)
    }
}
```

## Q36. Что такое `CoroutineScope`, `Job` и `Dispatcher`?

Это три кита, на которых держится запуск корутин. Грубо: `CoroutineScope` отвечает за «где живут» корутины и их жизненный цикл, `Job` — это «ручка» одной корутины (статус, отмена, ожидание), а `Dispatcher` решает, «на каком потоке» она исполняется. Связаны они в иерархию: scope содержит корневой `Job`, а у каждой запущенной корутины есть свой дочерний `Job` — отсюда структурированная отмена «сверху вниз».

| Компонент | Назначение |
|---|---|
| **`CoroutineScope`** | Определяет жизненный цикл корутин. Отмена scope отменяет все дочерние корутины |
| **`Job`** | «Ручка» корутины — можно проверить статус, дождаться завершения, отменить |
| **`Dispatcher`** | Определяет, на каком потоке/пуле выполняется корутина |

Стандартные диспетчеры:

| Dispatcher | Пул потоков | Использование |
|---|---|---|
| `Dispatchers.Default` | Shared pool (кол-во CPU ядер) | CPU-bound задачи |
| `Dispatchers.IO` | Отдельный пул (до 64 потоков) | IO-операции (сеть, диск, БД) |
| `Dispatchers.Main` | UI-поток | Обновление UI (Android) |
| `Dispatchers.Unconfined` | Текущий поток | Тесты, специальные случаи |

Подробнее — [Kotlin Coroutines](kotlin-coroutines-interview.md).

## Q37. В чём разница между `launch` и `async`?

Оба билдера запускают корутину, но различаются результатом. `launch` — это «запустил и забыл»: он возвращает `Job` (только для управления — статус, отмена) и не отдаёт результата. `async` рассчитан на получение значения: он возвращает `Deferred<T>`, у которого `await()` приостанавливает корутину до готовности результата. Поэтому `async` — основной инструмент для параллельных вычислений с последующим сбором результатов.

```kotlin
val scope = CoroutineScope(Dispatchers.IO)

// launch — fire-and-forget, возвращает Job
val job: Job = scope.launch {
    doSomething() // результат не нужен
}

// async — возвращает Deferred<T>, результат можно получить через await()
val deferred: Deferred<User> = scope.async {
    fetchUser()
}
val user: User = deferred.await() // suspend до получения результата

// Параллельное выполнение
coroutineScope {
    val user = async { fetchUser() }
    val orders = async { fetchOrders() }
    // Оба запроса выполняются параллельно
    processData(user.await(), orders.await())
}
```

**`launch`** — когда не нужен результат (отправка события, логирование). **`async`** — когда нужен результат и параллельное выполнение.

## Q38. (!) Какая польза от `@JvmStatic`, `@JvmOverloads` и `@JvmField`?

Это аннотации, делающие Kotlin-код удобным для вызова из Java. Они нужны потому, что некоторые конструкции Kotlin компилируются в неочевидный для Java байткод: `companion object` превращается во вложенный класс `Companion`, а параметры по умолчанию в Java вообще не видны. Перечисленные аннотации просят компилятор сгенерировать дополнительный, привычный для Java интерфейс (подробнее — [интероп Kotlin и Java](kotlin-interop-java-interview.md)).

```kotlin
class Config {
    companion object {
        @JvmStatic  // Генерирует настоящий static метод в Java
        fun default() = Config()

        @JvmField   // Прямой доступ к полю из Java (без getter)
        val VERSION = "1.0"
    }

    // Java может вызывать с 1, 2 или 3 аргументами
    @JvmOverloads
    fun init(host: String = "localhost", port: Int = 8080, ssl: Boolean = false) { }
}
```

| Аннотация | Без неё (в Java) | С ней (в Java) |
|---|---|---|
| `@JvmStatic` | `Config.Companion.default()` | `Config.default()` |
| `@JvmField` | `Config.Companion.getVERSION()` | `Config.VERSION` |
| `@JvmOverloads` | Только полная сигнатура | Перегрузки с default-значениями |

## Q39. Что такое плагины `allOpen` и `noArg`?

**`kotlin-allopen`** — делает классы с указанной аннотацией `open` (не `final`). Необходим для Spring Framework, где AOP-прокси требуют не-final классов.

```groovy
// build.gradle.kts
plugins {
    kotlin("plugin.allopen") version "1.9.0"
    kotlin("plugin.noarg") version "1.9.0"
}

allOpen {
    annotation("org.springframework.stereotype.Service")
    annotation("org.springframework.stereotype.Component")
}
```

**`kotlin-noarg`** — генерирует конструктор без аргументов для классов с указанной аннотацией. Необходим для JPA/Hibernate, которые создают экземпляры через reflection.

```groovy
noArg {
    annotation("jakarta.persistence.Entity")
}
```

`kotlin-spring` и `kotlin-jpa` — предварительно настроенные обёртки над `allOpen` и `noArg` для Spring/JPA.

## Q40. Что такое `Reflection API` в Kotlin?

Reflection — это API, позволяющий программе в рантайме «заглянуть внутрь себя»: узнать структуру класса, перечислить его свойства и функции, вызвать их по имени. `Kotlin Reflection` (`kotlin-reflect`) — надстройка над Java-рефлексией, которая понимает специфичные для Kotlin сущности: nullable-типы, свойства с `get`/`set`, `data class`, видимости. На нём строятся сериализация и DI-фреймворки.

```kotlin
import kotlin.reflect.full.*

data class User(val name: String, val age: Int)

// KClass — аналог java.lang.Class
val kClass = User::class
println(kClass.simpleName)         // "User"
println(kClass.memberProperties)   // [val User.age, val User.name]

// Ссылки на свойства и функции
val nameProp = User::name
println(nameProp.get(User("Alice", 30)))  // "Alice"

// Доступ к private через Java Reflection
val field = User::class.java.getDeclaredField("name")
field.isAccessible = true
```

**Зачем**: сериализация, DI-фреймворки, тестирование. `kotlin-reflect` — отдельная зависимость (~2.5 MB), в production добавляйте осознанно.

## Q41. Как работает интерполяция строк (string templates)?

Интерполяция (string templates) позволяет вставлять значения переменных и выражений прямо в строковый литерал: `$name` для простой переменной и `${...}` для произвольного выражения. Это безопаснее и читаемее ручной конкатенации через `+`, а под капотом компилятор всё равно собирает результат через `StringBuilder`.

```kotlin
val name = "Kotlin"
val version = 2.0

// Простая подстановка
println("Hello, $name!")                    // Hello, Kotlin!

// Выражение в фигурных скобках
println("Name length: ${name.length}")       // Name length: 6
println("Next version: ${version + 0.1}")    // Next version: 2.1

// Многострочные строки (raw strings)
val json = """
    {
        "name": "$name",
        "version": $version
    }
""".trimIndent()

// Экранирование $
println("Price: ${'$'}9.99")  // Price: $9.99
```

Строковые шаблоны компилируются в `StringBuilder.append()` — эффективнее ручной конкатенации.

## Q42. Что такое `contracts` в Kotlin?

Contracts (экспериментальная фича) — это способ «подсказать» компилятору неочевидную связь между результатом функции и её аргументами, чтобы он мог точнее делать smart cast и анализ потока. Без контракта компилятор не знает, что внутри функции вы проверили аргумент на null; с контрактом — учитывает это после вызова. По сути вы декларируете гарантию вида «если функция вернула `true`, то аргумент точно не null».

```kotlin
import kotlin.contracts.*

@OptIn(ExperimentalContracts::class)
fun String?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return this != null && this.isNotEmpty()
}

// Благодаря контракту компилятор знает: если вернулось true, то значение не null
val name: String? = getName()
if (name.isNotNullOrEmpty()) {
    println(name.length)  // Smart cast: String? -> String
}
```

Стандартные функции `require`, `check`, `let`, `run` и т.д. уже используют contracts внутри.

## Q43. (!) Какие best practices при написании идиоматичного Kotlin-кода?

Идиоматичный Kotlin сводится к одной идее: предпочитать декларативность и неизменяемость, а возможности языка использовать по назначению вместо переноса привычек из Java. Ключевые правила:

1. **`val` по умолчанию** — используйте `var` только когда мутабельность действительно нужна
2. **`data class` для DTO** — вместо boilerplate equals/hashCode/toString
3. **Scope-функции** — `apply` для конфигурации, `let` для null-check, `also` для побочных эффектов
4. **Extension functions** — вместо utility-классов с static-методами
5. **`sealed class`** для ADT — моделирование состояний с исчерпывающим `when`
6. **`Sequence`** для цепочек операций на больших коллекциях
7. **Именованные аргументы** — `createUser(name = "Alice", admin = false)` вместо `createUser("Alice", false)`
8. **Default parameters** — вместо перегрузки методов
9. **`require`/`check`/`error`** — вместо ручного throw для preconditions
10. **Корутины** — вместо callback hell и `CompletableFuture`

```kotlin
// Анти-паттерн
fun process(list: List<String>): List<String> {
    val result = mutableListOf<String>()
    for (item in list) {
        if (item.isNotBlank()) {
            result.add(item.uppercase())
        }
    }
    return result
}

// Идиоматичный Kotlin
fun process(list: List<String>): List<String> =
    list.filter { it.isNotBlank() }
        .map { it.uppercase() }
```

## Q44. Что такое `typealias` и когда его использовать?

`typealias` вводит более короткое или говорящее имя для уже существующего типа. Это именно псевдоним, а не новый тип: на уровне JVM `typealias UserId = String` и `String` — полностью взаимозаменяемы, поэтому никакой дополнительной типобезопасности он не даёт. Его задача — читаемость, а не защита от ошибок.

```kotlin
// Упрощение сложных типов
typealias UserMap = Map<String, List<User>>
typealias Predicate<T> = (T) -> Boolean
typealias EventHandler = suspend (Event) -> Unit

// Псевдонимы для функциональных типов
typealias Comparator<T> = (T, T) -> Int

// Применение
fun findUsers(predicate: Predicate<User>): List<User> =
    users.filter(predicate)

val byName: Predicate<User> = { it.name.isNotBlank() }
val result = findUsers(byName)
```

Типичные случаи использования:
- **Длинные generic-типы** — `Map<String, List<Pair<Int, String>>>` → `typealias`
- **Функциональные типы** — именование `(Event) -> Unit` для читаемости
- **Псевдонимы для сторонних типов** — упрощение перехода между библиотеками

**Отличие от `value class`:** `typealias` не даёт type-safety (компилятор принимает оригинальный тип вместо псевдонима). `value class` — это новый тип.

```kotlin
typealias UserId = String
typealias ProductId = String

fun findUser(id: UserId) { /* ... */ }

val productId: ProductId = "prod-1"
findUser(productId)  // Компилируется! — нет type-safety

// Для type-safety нужен value class:
@JvmInline value class UserId(val value: String)
@JvmInline value class ProductId(val value: String)
// findUser(ProductId("x"))  — ошибка компиляции ✅
```

## Q45. Что такое `object expression` (анонимный объект) и чем отличается от `object declaration`?

Оба используют ключевое слово `object`, но это разные вещи. `object declaration` (`object Foo { }`) — это именованный синглтон: один экземпляр на всё приложение, создаваемый лениво. `object expression` (`object : Interface { }`) — анонимный объект, создаваемый на месте при каждом выполнении, прямой аналог анонимного класса в Java. Проще говоря: declaration — «единственный объект с именем», expression — «одноразовая реализация на лету».

```kotlin
// object expression — анонимный объект (создаётся каждый раз заново)
val comparator = object : Comparator<String> {
    override fun compare(a: String, b: String) = a.length - b.length
}

// Можно реализовывать несколько интерфейсов
val handler = object : EventListener, Closeable {
    override fun onEvent(e: Event) { println(e) }
    override fun close() { println("closed") }
}

// Без базового типа — просто анонимная структура данных
val point = object {
    val x = 10
    val y = 20
}
println(point.x)  // 10 (доступно в локальном контексте)
```

**Отличия от `object declaration`:**

| | `object declaration` | `object expression` |
|---|---|---|
| Синтаксис | `object MySingleton { }` | `object : Interface { }` |
| Создание | Singleton — один экземпляр на всё время | Новый объект при каждом вызове |
| Имя | Именованный | Анонимный |
| Инициализация | Lazy (при первом обращении) | Немедленно |
| Применение | Синглтоны, утилиты, factory | Реализация интерфейса на месте |

```kotlin
// object declaration — Singleton
object Registry {
    private val entries = mutableMapOf<String, Any>()
    fun register(key: String, value: Any) { entries[key] = value }
}

// object expression — одноразовая реализация
button.addClickListener(object : ClickListener {
    override fun onClick() { doSomething() }
})

// В Kotlin предпочтительнее SAM-конверсия (если интерфейс один метод):
button.addClickListener { doSomething() }
```

Официальный Kotlin Coding Conventions: [kotlinlang.org/docs/coding-conventions.html](https://kotlinlang.org/docs/coding-conventions.html)

---

## See also

- [Kotlin Coroutines](kotlin-coroutines-interview.md) — подробные вопросы по корутинам и Flow
- [Kotlin коллекции](kotlin-collections-interview.md) — List, Set, Map, Sequence и операции
- [Исключения в Kotlin](kotlin-exceptions-interview.md) — обработка ошибок, Result, sealed hierarchy
- [DSL в Kotlin](kotlin-dsl-interview.md) — domain-specific languages и type-safe builders
- [Интероп Kotlin и Java](kotlin-interop-java-interview.md) — @JvmStatic, platform types, SAM
- [Сериализация в Kotlin](kotlin-serialization-interview.md) — `kotlinx.serialization` и форматы
- [Java Core](../java/java-core-interview.md) — основы Java для сравнения с Kotlin
- [Java Concurrency](../java/java-concurrency-interview.md) — многопоточность JVM, сравнение с корутинами
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — Kotlin со Spring Boot и Spring Data
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — паттерны, реализованные на Kotlin

- [Kotlin Coroutines](kotlin-coroutines-interview.md)
- [DSL в Kotlin](kotlin-dsl-interview.md)
- [исключения в Kotlin](kotlin-exceptions-interview.md)
- [интероп Kotlin и Java](kotlin-interop-java-interview.md)
- [сериализация в Kotlin](kotlin-serialization-interview.md)
