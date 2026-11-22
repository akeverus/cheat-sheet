---
title: "Вопросы на собеседовании: DSL в Kotlin"
description: "Полный набор вопросов по Kotlin DSL — lambda с receiver, @DslMarker, type-safe builders, Gradle Kotlin DSL, Exposed, Ktor, kotlinx.html, context receivers, scope-функции, infix, operator overloading, реальные паттерны"
tags:
  - interview
  - programming-languages
  - kotlin-dsl-interview
  - kotlin
  - dsl
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "DSL в Kotlin"
  - "Kotlin DSL interview"
  - "Kotlin DSL собеседование"
prerequisites:
  - "[[kotlin-dsl]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `DSL` в `Kotlin`

Ответы по созданию **Domain-Specific Language** в `Kotlin` — `type-safe builders`, lambda с receiver, `@DslMarker`, `Gradle Kotlin DSL`, `Exposed`, `Ktor`, `context receivers`, scope-функции и реальные паттерны. На собеседованиях часто спрашивают про механизм lambda с receiver, ограничение scope через `@DslMarker`, конкретные DSL из экосистемы (`Gradle`, `Ktor`, `Exposed`) и отличия от обычного fluent API.

## Полезные ссылки

### Официальная документация

- [Type-Safe Builders](https://kotlinlang.org/docs/type-safe-builders.html) — официальная документация Kotlin по type-safe builders
- [Lambda with Receiver](https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver) — спецификация lambda с receiver
- [Scope Functions](https://kotlinlang.org/docs/scope-functions.html) — `apply`, `with`, `run`, `let`, `also`
- [Gradle Kotlin DSL Primer](https://docs.gradle.org/current/userguide/kotlin_dsl.html) — руководство по Gradle Kotlin DSL
- [Exposed Wiki](https://github.com/JetBrains/Exposed/wiki) — документация ORM-фреймворка Exposed
- [Ktor Routing](https://ktor.io/docs/routing-in-ktor.html) — маршрутизация в Ktor

### Baeldung

- [Building DSLs in Kotlin — Baeldung](https://www.baeldung.com/kotlin/dsl) — построение type-safe DSL: lambda с receiver, builders
- [Kotlin DSL for Gradle — Baeldung](https://www.baeldung.com/kotlin/gradle-dsl) — использование Kotlin DSL вместо Groovy в Gradle
- [Spring Security with Kotlin DSL — Baeldung](https://www.baeldung.com/kotlin/spring-security-dsl) — конфигурация Spring Security через Kotlin DSL
- [HTML Builder in Kotlin — Baeldung](https://www.baeldung.com/kotlin/html-generation) — kotlinx.html DSL для генерации HTML

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы DSL**
- [Q1. (!) Внешний и вложенный receiver DSL пометили разными `@DslMarker`-аннотациями. Почему свойство внешнего receiver-а по-прежнему доступно внутри?](#q1)
- [Q2. (!) После настройки `@DslMarker` внутри вложенного блока написали `this@server.host = ...`. Почему это обращение к внешнему receiver-у разрешено?](#q2)
- [Q3. Почему внутри DSL-блока вызывается метод builder-класса, а не одноимённая extension-функция с той же сигнатурой?](#q3)

**Type-Safe Builders**
- [Q4. (!) Почему функция сборки вернула `Boolean` от последнего выражения DSL-блока вместо собранного объекта?](#q4)
- [Q5. Почему при повторном использовании одного builder-объекта настройка первого результата оказалась во втором?](#q5)
- [Q6. (!) Почему изменение builder-а после `build()` изменило уже возвращённый объект?](#q6)
- [Q7. Обязательное поле builder-а объявлено через `lateinit`, но его не заполнили. Почему ошибка возникла при чтении результата, а не при сборке?](#q7)

**Операторы и инфиксы в DSL**
- [Q8. Почему `return` внутри lambda, переданной `inline`-функции, завершил вызывающую функцию?](#q8)
- [Q9. Почему extension-функция для базового типа вызывается вместо extension-функции для фактического подтипа?](#q9)
- [Q10. У вложенной lambda с receiver нет свойства `host`, а общий `@DslMarker` не скрывает внешний receiver. Почему обращение к `host` меняет внешний объект?](#q10)

**Scope-функции и DSL**
- [Q11. (!) Почему компилятор запрещает сохранить обычный inline lambda-параметр в поле?](#q11)
- [Q12. Почему компилятор запретил вызвать lambda-параметр `inline`-функции из анонимного объекта?](#q12)

**Gradle Kotlin DSL**
- [Q13. (!) В builder-е оставили `noinline` lambda-параметр. Почему после добавления `inline` число созданных объектов lambda не уменьшилось?](#q13)
- [Q14. Обобщённая функция-обёртка пытается вызвать `inline reified`-проверку со своим типом `T`, но компилятор отклоняет вызов. Почему?](#q14)
- [Q15. Почему выражение `a shl 1 + 2` даёт не тот результат, который ожидают при чтении слева направо?](#q15)

**Exposed DSL**
- [Q16. (!) У объекта есть `invoke(() -> Unit)` и `invoke(Builder.() -> Unit)`. Почему вызов `obj { println("ok") }` неоднозначен?](#q16)
- [Q17. Во вложенном DSL-блоке нет своего `unaryPlus`, а внешний receiver доступен. Почему оператор добавил элемент во внешнюю коллекцию?](#q17)

**Ktor Routing DSL**
- [Q18. (!) У элемента списка вызвали `items[i].copy(...)`, но результат не записали обратно. Почему значение в списке не изменилось?](#q18)
- [Q19. Почему перестановка двух вызовов в type-safe builder компилируется, но нарушает поведение при выполнении?](#q19)

**HTML DSL (kotlinx.html)**
- [Q20. Почему отложенная lambda увидела новое значение изменённой переменной вместо значения при создании?](#q20)

**Context Receivers**
- [Q21. (!) Проверка в теле `build.gradle.kts` сработала при запуске посторонней задачи. Почему она выполнилась, хотя эта задача её не вызывает?](#q21)

**Продвинутые приёмы**
- [Q22. Почему после применения Gradle-плагина через `apply(plugin = ...)` в `build.gradle.kts` недоступны type-safe accessors?](#q22)
- [Q23. (!) Почему после `plugins { id(...) apply false }` в том же build-скрипте нет type-safe accessors плагина?](#q23)
- [Q24. Почему Ktor выбрал маршрут с литеральным сегментом, хотя маршрут с `{param}` объявлен раньше?](#q24)
- [Q25. Почему маршрут с дополнительным сегментом, вложенный в `{param...}`, не получает запросы с этим сегментом?](#q25)

**Сравнение и паттерны**
- [Q26. Почему плагин Ktor, установленный внутри одного маршрута, не обрабатывает соседние маршруты?](#q26)
- [Q27. `Query` из Exposed создали внутри `transaction {}`, а итерировать начали после выхода из блока. Почему чтение завершилось ошибкой?](#q27)
- [Q28. Почему после выборки N DAO-сущностей Exposed с разными владельцами обращение к не загруженным заранее ссылкам даёт N дополнительных запросов?](#q28)
- [Q29. (!) Почему подстановка параметра в SQL через конкатенацию строки изменила результат запроса?](#q29)
- [Q30. DSL принимает обычную `Builder.() -> Unit` lambda. Почему после `launch` блок продолжил работу, не дождавшись suspend-вызова?](#q30)
- [Q31. Почему повторное применение DSL-блока дважды добавило записи в общий список?](#q31)
- [Q32. Почему builder с незаданным обязательным параметром собрал объект без ошибки, а сбой случился при использовании?](#q32)

**Gradle Kotlin DSL и Test Data Builders**
- [Q33. Почему при вызове функции с trailing lambda пропущенный параметр получил значение по умолчанию?](#q33)
- [Q34. В `buildList { add(Cat()); add(Dog()) }` компилятор принимает оба подкласса `Animal`. Почему тип списка не фиксируется по первому элементу?](#q34)
- [Q35. Почему опечатка в строковом ключе `operator set` не была обнаружена при компиляции DSL?](#q35)

**Operator Overloading и реальные DSL**
- [Q36. Почему `@DslMarker` на функции builder-а не скрыл внешний receiver во вложенном блоке?](#q36)
- [Q37. В Kotlin 2.4 старое объявление `context(Logger)` не компилируется без специального флага. Как записать зависимость от `Logger` на актуальном синтаксисе?](#q37)

**Паттерны и сравнения**
- [Q38. Почему `val Config.token get() = UUID.randomUUID()` возвращает новое значение при каждом чтении?](#q38)
- [Q39. Валидационный DSL создаёт новые scope для вложенных блоков и вызывается часто. Откуда берутся дополнительные объекты, если builder-функции не `inline`?](#q39)
- [Q40. Почему внутренний Kotlin DSL не может добавить синтаксис, которого нет в Kotlin?](#q40)

---

<a id="q1"></a>

## Q1. (!) Внешний и вложенный receiver DSL пометили разными `@DslMarker`-аннотациями. Почему свойство внешнего receiver-а по-прежнему доступно внутри?

**Domain-Specific Language** (DSL) — язык, заточенный под конкретную предметную область, а не под универсальное программирование. Ключевое разделение проходит по тому, отдельный это язык или код на хост-языке:

- **Внешний DSL** — самостоятельный язык со своим синтаксисом и парсером (SQL, CSS, регулярные выражения). Требует собственного лексера/парсера и не проверяется компилятором основного языка: ошибка в строке всплывёт только в рантайме.
- **Внутренний DSL** (embedded DSL) — DSL, построенный средствами хост-языка. В `Kotlin` это обычный код, организованный так, что читается как декларативное описание: конфигурация, разметка, маршруты, тестовые сценарии. Парсер не нужен — компилятор `Kotlin` сам разбирает и проверяет конструкцию.

```kotlin
// Внутренний DSL — это обычный Kotlin
html {
    body {
        p { +"Hello, World!" }
    }
}

// Внешний DSL — отдельный язык
// SELECT * FROM users WHERE active = true
```

**Почему внутренний DSL в `Kotlin` выигрывает:**

- **Типобезопасность** — компилятор проверяет структуру ещё до запуска, невалидную вложенность не собрать.
- **IDE-поддержка** — автодополнение, навигация и рефакторинг работают «из коробки», потому что это обычный код.
- **Нет накладных расходов** — парсить нечего, конструкция компилируется напрямую.
- **Полная мощь языка** — внутри DSL-блоков доступны условия, циклы и переменные `Kotlin`.

Проверки компилятора при этом не дают изоляции контекстов сами по себе: `@DslMarker` затеняет только те implicit receiver-ы, которые помечены одной и той же маркерной аннотацией через тип receiver-а, поэтому при разных аннотациях у внешнего и вложенного контекста внешний receiver остаётся в области видимости и его свойства присваиваются прямо из вложенного блока.

<a id="q2"></a>

## Q2. (!) После настройки `@DslMarker` внутри вложенного блока написали `this@server.host = ...`. Почему это обращение к внешнему receiver-у разрешено?

**Lambda с receiver** — lambda-выражение, у которого есть неявный `this`, указывающий на объект-receiver. Тип записывается как `T.() -> R` (читается: «функция на `T`, возвращающая `R`»).

```kotlin
// Обычная lambda
val greet: (String) -> String = { name -> "Hello, $name" }

// Lambda с receiver — this указывает на StringBuilder
val build: StringBuilder.() -> Unit = {
    append("Hello, ")  // вызываем метод через неявный this
    append("World!")
}

val result = StringBuilder().apply(build) // "Hello, World!"
```

Это основа DSL потому, что внутри блока методы и свойства receiver-а вызываются **без префикса** — `this` подставляется неявно. Так создаётся контекст «внутри этого блока мы находимся в объекте типа `T`», и вложенные `html { body { } }` читаются как разметка, а не как цепочка вызовов.

Ограничение `@DslMarker` действует только на этот неявный `this`: явное обращение к внешнему receiver-у через `this@server.host = ...` или через захваченную снаружи переменную маркер не блокирует, поэтому в таком месте свойство внешнего контекста выставляется из вложенного блока по-прежнему.

```mermaid
graph TD
    A["config { }"] -->|"receiver = Config"| B["this = Config()"]
    B --> C["host = 'localhost'"]
    B --> D["port = 8080"]
    B --> E["ssl { }"]
    E -->|"receiver = SslConfig"| F["this = SslConfig()"]
    F --> G["enabled = true"]
    F --> H["certPath = '/certs'"]
```

**Чем отличается от extension-функции.** Extension-функция объявляется как `fun T.name()`, а lambda с receiver — как значение типа `T.() -> Unit`. Оба дают неявный `this`, но lambda с receiver передаётся как аргумент: именно это и позволяет вложить один DSL-блок в другой.

<a id="q3"></a>

## Q3. Почему внутри DSL-блока вызывается метод builder-класса, а не одноимённая extension-функция с той же сигнатурой?

Ни одно средство не делает DSL в одиночку — читаемость возникает из их сочетания. Lambda с receiver задаёт контекст, extension-функции добавляют в него методы, `@DslMarker` ограничивает scope, операторы и infix убирают синтаксический шум.

При этом extension-функция не переопределяет член класса: разрешение вызова статическое, и при совпадении имени и сигнатуры внутри блока, где receiver — сам класс, всегда побеждает его собственный метод, а extension достаётся только вызовам на типах, у которых такого члена нет.

Вот ключевые механизмы:

| Средство | Роль в DSL | Пример |
|----------|-----------|--------|
| Lambda с receiver (`T.() -> Unit`) | Вложенные контексты | `html { body { } }` |
| Extension-функции | Расширение API без наследования | `fun Tag.div(init: Div.() -> Unit)` |
| Extension properties | Контекстные свойства | `val Context.currentUser` |
| `@DslMarker` | Ограничение scope | Запрет `head { head { } }` |
| Operator overloading | Синтаксический сахар | `+"text"`, `config["key"]` |
| Infix-функции | Читаемые пары | `x shouldBe 42` |
| Именованные аргументы | Читаемые параметры | `div(class = "card")` |
| Default-аргументы | Опциональные параметры | `fun port(p: Int = 8080)` |
| `inline` | Устранение накладных расходов | `inline fun html(...)` |
| Trailing lambda | Вынос блока за скобки | `route("/api") { get { } }` |

<a id="q4"></a>

## Q4. (!) Почему функция сборки вернула `Boolean` от последнего выражения DSL-блока вместо собранного объекта?

**Type-Safe Builder** — паттерн, при котором иерархическая структура (дерево узлов) строится вызовами функций с lambda с receiver, а компилятор гарантирует корректность вложенности. Каждая функция принимает `T.() -> Unit`: внутри lambda доступны только методы текущего контекста, поэтому положить `title` туда, где его быть не должно, просто не получится — это ошибка компиляции, а не рантайма.

```mermaid
graph TD
    A["html { }"] --> B["HTML"]
    B -->|"head { }"| C["Head"]
    B -->|"body { }"| D["Body"]
    C -->|"title { }"| E["Title"]
    D -->|"div { }"| F["Div"]
    D -->|"p { }"| G["P"]
    F -->|"+text"| H["TextElement"]
```

**Как это устроено.** Классы представляют узлы (`HTML`, `Body`, `Div`); каждый хранит список дочерних элементов и методы для добавления потомков. Метод-потомок делает три вещи: принимает lambda с receiver типа дочернего узла, создаёт его экземпляр, вызывает на нём lambda и добавляет узел в коллекцию родителя:

```kotlin
abstract class Tag(val name: String) {
    val children = mutableListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()          // Инициализация через lambda с receiver
        children.add(tag)   // Добавление в дерево
        return tag
    }
}

class HTML : Tag("html") {
    fun head(init: Head.() -> Unit) = initTag(Head(), init)
    fun body(init: Body.() -> Unit) = initTag(Body(), init)
}

class Body : Tag("body") {
    fun div(init: Div.() -> Unit) = initTag(Div(), init)
    fun p(init: P.() -> Unit) = initTag(P(), init)
}
```

Точка входа — top-level функция:

```kotlin
fun html(init: HTML.() -> Unit): HTML = HTML().apply(init)
```

**Что сказать на интервью.** Главное — показать понимание механики: каждый вложенный блок это вызов метода на receiver-е, который создаёт дочерний узел, инициализирует его через lambda и подвешивает в дерево родителя. Внешне похоже на разметку, под капотом — обычные вызовы функций.

Точка входа держится на `apply` именно потому, что он возвращает сам receiver, тогда как `run`, `with` и `let` отдают наружу значение последнего выражения блока — и завершающая проверка вроде `isValid()` подменит собранный объект своим `Boolean`.

<a id="q5"></a>

## Q5. Почему при повторном использовании одного builder-объекта настройка первого результата оказалась во втором?

Любой type-safe builder собирается по одной и той же схеме из четырёх шагов.

Схема рассчитана на то, что точка входа создаёт новый экземпляр класса-контекста на каждый вызов: контекст хранит изменяемое состояние в `var`-свойствах и полях вложенных блоков, поэтому переиспользование одного экземпляра, лежащего в общем поле, не сбрасывает присвоенное ранее и следующая сборка стартует с настройками предыдущей.

Разберём её на DSL для конфигурации сервера:

```kotlin
// 1. Определяем классы-контексты
@ServerDsl
class ServerConfig {
    var host = "localhost"
    var port = 8080
    private var _ssl: SslConfig? = null

    fun ssl(init: SslConfig.() -> Unit) {
        _ssl = SslConfig().apply(init)
    }

    fun build(): Server = Server(host, port, _ssl)
}

@ServerDsl
class SslConfig {
    var enabled = false
    var certPath = ""
    var keyPath = ""
}

// 2. Определяем @DslMarker
@DslMarker
annotation class ServerDsl

// 3. Точка входа
fun server(init: ServerConfig.() -> Unit): Server {
    return ServerConfig().apply(init).build()
}

// 4. Использование
val myServer = server {
    host = "0.0.0.0"
    port = 443
    ssl {
        enabled = true
        certPath = "/etc/ssl/cert.pem"
        keyPath = "/etc/ssl/key.pem"
    }
}
```

**Итого, четыре шага:**

1. Создать классы-контексты с нужными свойствами (`var host`) и методами вложенных блоков (`fun ssl`).
2. Пометить их `@DslMarker`, чтобы изолировать scope вложенных receiver-ов.
3. Написать top-level функцию-точку входа (`fun server`), которая создаёт контекст, применяет к нему lambda и вызывает `build()`.
4. Использовать DSL — свойства задаются как присваивания, подконфигурации как вложенные блоки.

<a id="q6"></a>

## Q6. (!) Почему изменение builder-а после `build()` изменило уже возвращённый объект?

**`@DslMarker`** — мета-аннотация из `kotlin.annotation`: ею помечают свою аннотацию, а той уже — классы-контексты DSL. Она решает проблему **утечки scope**. По умолчанию во вложенной lambda с receiver видны методы **всех** внешних receiver-ов сразу, и DSL начинает принимать бессмысленные конструкции.

```kotlin
// БЕЗ @DslMarker — компилируется, но бессмысленно
html {
    body {
        head { }  // Вызывает html.head() — ошибка логики!
    }
}
```

С `@DslMarker` компилятор ограничивает видимость: если два receiver-а помечены одним маркером, из вложенной lambda виден только **ближайший** из них. Доступ к внешнему остаётся, но требует явного `this@label` — и это уже осознанное действие, а не случайность:

```kotlin
@DslMarker
annotation class HtmlDsl

@HtmlDsl
abstract class Tag(val name: String) { ... }

// Теперь компилятор запретит:
html {
    body {
        head { }  // ❌ Ошибка компиляции!
        this@html.head { }  // ✅ Явный доступ — если действительно нужен
    }
}
```

Маркер объявляется один раз, а все аннотированные им классы образуют группу с контролируемым scope. Подклассы наследуют маркер автоматически — пометить достаточно базовый `Tag`, и правило распространится на все его наследники.

Отдельно от scope лежит владение состоянием: если `build()` отдаёт наружу сам изменяемый контекст или его коллекции без копирования, возвращённый объект и класс-контекст ссылаются на одни и те же данные, и свойство, присвоенное в контекст уже после возврата, проступает в отданном объекте.

<a id="q7"></a>

## Q7. Обязательное поле builder-а объявлено через `lateinit`, но его не заполнили. Почему ошибка возникла при чтении результата, а не при сборке?

**Без `@DslMarker`** действует стандартное правило разрешения имён: компилятор ищет метод сначала в ближайшем receiver, а не найдя — поднимается по цепочке внешних receiver-ов. Поэтому внутри `body { }` доступны методы и `Body`, и `HTML`, и любого охватывающего контекста — отсюда и утечка.

**С `@DslMarker`** поведение меняется: если два receiver-а помечены одним маркером, видимым остаётся только **самый внутренний** из них, а внешние одноимённого маркера затеняются.

```mermaid
graph TB
    subgraph "Без @DslMarker"
        A1["html { }"] -->|"this = HTML"| B1["body { }"]
        B1 -->|"this = Body + HTML"| C1["div { }"]
        C1 -->|"this = Div + Body + HTML"| D1["head { } ← доступен!"]
    end
    subgraph "С @DslMarker"
        A2["html { }"] -->|"this = HTML"| B2["body { }"]
        B2 -->|"this = Body только"| C2["div { }"]
        C2 -->|"this = Div только"| D2["head { } ← ❌ ошибка"]
    end
```

Аннотацию можно применить на уровне класса или на уровне типа в сигнатуре функции:

```kotlin
// На уровне класса — все подклассы наследуют
@HtmlDsl abstract class Tag(val name: String)

// На уровне типа — точечно для конкретных lambda
fun html(init: @HtmlDsl HTML.() -> Unit): HTML { ... }
```

**Граничный случай.** Допустимые target-ы для `@DslMarker` — `CLASS`, `TYPE`, `TYPEALIAS`. На функции или свойства аннотацию повесить можно, но на контроль scope это **не влияет** — ограничение работает только через тип receiver-а.

Гарантии компилятора здесь вообще ограничены структурой вложенности и не касаются заполненности значений: свойства контекста объявлены как `var` с дефолтом или `lateinit`, поэтому не заданное обязательное свойство проходит `build()` без единой проверки и обнаруживается только при первом обращении к нему, если сам `build()` не валидирует его через `require`/`checkNotNull`.

<a id="q8"></a>

## Q8. Почему `return` внутри lambda, переданной `inline`-функции, завершил вызывающую функцию?

Перегрузка операторов в `Kotlin` задаёт поведение `+`, `-`, `[]`, `in`, `()` для своих типов. В DSL её применяют, чтобы убрать синтаксический шум: вместо `add(...)` или `put(...)` пишут привычный символ, и блок читается ближе к естественному языку.

Блок, переданный `inline`-функции, компилируется прямо в тело вызывающей функции, поэтому голый `return` внутри такого блока — non-local return: он завершает внешнюю функцию целиком, а выйти только из самого блока позволяет помеченный `return@label`.

**`unaryPlus` — добавление текста в builder:**

```kotlin
abstract class TagWithText(name: String) : Tag(name) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

// Использование: div { +"Hello, World!" }
```

**`get`/`set` — доступ к конфигурации:**

```kotlin
class Config {
    private val map = mutableMapOf<String, Any>()

    operator fun get(key: String): Any? = map[key]
    operator fun set(key: String, value: Any) { map[key] = value }
}

// Использование: config["host"] = "localhost"
```

**`contains` — проверка принадлежности:**

```kotlin
class RoleSet {
    private val roles = mutableSetOf<String>()
    operator fun contains(role: String) = role in roles
    fun add(role: String) { roles.add(role) }
}

// Использование: if ("admin" in roles) { ... }
```

**`rangeTo` — диапазоны в DSL:**

```kotlin
data class Version(val major: Int, val minor: Int) : Comparable<Version> { ... }
operator fun Version.rangeTo(other: Version) = VersionRange(this, other)

// Использование: Version(1,0)..Version(2,0)
```

**Эмпирическое правило.** Оператор должен быть семантически уместен: `+` для добавления контента читается интуитивно, а вот произвольная перегрузка (`-` как «удалить», `*` как «повторить») заставляет читателя лезть в документацию и только вредит DSL.

<a id="q9"></a>

## Q9. Почему extension-функция для базового типа вызывается вместо extension-функции для фактического подтипа?

**`invoke` convention** — если у класса определён `operator fun invoke(...)`, его экземпляр можно вызывать как функцию: `obj(args)` разворачивается в `obj.invoke(args)`. В DSL это позволяет «вызвать» объект-билдер или синглтон, передав ему путь и конфигурирующую lambda.

Extension-функции не участвуют в динамической диспетчеризации: компилятор выбирает их статически по объявленному типу выражения, поэтому если одноимённые extension-функции написаны и для супертипа, и для конкретного класса, а объект передан под типом супертипа, вызовется extension-функция супертипа независимо от фактического типа объекта во время выполнения.

```kotlin
class RouteBuilder {
    private val routes = mutableListOf<Route>()

    operator fun invoke(path: String, init: RouteConfig.() -> Unit) {
        routes.add(RouteConfig(path).apply(init).build())
    }

    fun build(): List<Route> = routes.toList()
}

// Использование
val routes = RouteBuilder()
routes("/api/users") {
    method = GET
    handler = { listUsers() }
}
routes("/api/orders") {
    method = POST
    handler = { createOrder() }
}
```

Другой сценарий — объект-синглтон как «вызываемая функция»:

```kotlin
object Query {
    operator fun invoke(table: String, init: QueryBuilder.() -> Unit): SqlQuery {
        return QueryBuilder(table).apply(init).build()
    }
}

// Использование: Query("users") { where("active = true") }
```

**Компромисс.** `invoke` делает DSL лаконичнее, но и рискованнее: строка `routes("/api") { }` выглядит как обычный вызов функции, хотя `routes` — это объект. Применяйте, когда семантика «вызова» очевидна из контекста, иначе читатель не поймёт, что перед ним.

<a id="q10"></a>

## Q10. У вложенной lambda с receiver нет свойства `host`, а общий `@DslMarker` не скрывает внешний receiver. Почему обращение к `host` меняет внешний объект?

**Инфиксная функция** (`infix fun`) вызывается без точки и скобок: `a to b` вместо `a.to(b)`. За счёт этого пара «значение—действие» читается как фраза на английском, что особенно ценно в тестах и конфигурации.

Вложенные lambda с receiver накладывают свои implicit receiver-ы друг на друга, и если у ближайшего receiver-а нужного свойства нет, компилятор молча разрешает обращение к одноимённому свойству внешнего receiver-а — отсечь этот перескок по области видимости можно только аннотацией `@DslMarker` на общем базовом типе builder-ов.

Требования жёсткие: это member- или extension-функция, ровно один параметр, без `vararg` и без default-значений.

```kotlin
// Тестовый DSL (Kotest-стиль)
infix fun <T> T.shouldBe(expected: T) {
    if (this != expected) throw AssertionError("Expected $expected but was $this")
}

infix fun <T> T.shouldNotBe(unexpected: T) {
    if (this == unexpected) throw AssertionError("Should not be $unexpected")
}

// Использование
result shouldBe 42
name shouldNotBe ""
```

```kotlin
// Конфигурационный DSL
class PermissionBuilder {
    val permissions = mutableListOf<Pair<String, String>>()

    infix fun String.can(action: String) {
        permissions.add(this to action)
    }
}

fun permissions(init: PermissionBuilder.() -> Unit) =
    PermissionBuilder().apply(init)

// Использование
permissions {
    "admin" can "delete"
    "editor" can "write"
    "viewer" can "read"
}
```

**Когда применять.** Инфикс особенно хорош для assertion-ов в тестах (`result shouldBe 42`) и пар ключ-значение (`"admin" can "delete"`). Но злоупотреблять не стоит: `a plus b` читается хуже привычного `a + b`, а длинные цепочки инфиксов без скобок становятся неоднозначными по приоритету.

<a id="q11"></a>

## Q11. (!) Почему компилятор запрещает сохранить обычный inline lambda-параметр в поле?

Scope-функции `Kotlin` — `apply`, `with`, `run`, `let`, `also` — это, по сути, готовые DSL-примитивы из stdlib.

Обычный inline lambda-параметр нельзя сохранить в поле или передать в функцию, которая удержит его как значение: компилятор запрещает такому параметру выходить за пределы вызова. Его можно вызвать прямо или передать другому inline-параметру. Если блок нужно хранить, параметр помечают `noinline`; для вызова из анонимного объекта может подойти `crossinline`, но хранить его как значение всё равно нельзя.

Каждая принимает lambda (с receiver или без) и задаёт контекст выполнения; различаются они двумя осями: как доступен объект (`this` или `it`) и что функция возвращает (сам объект или результат lambda):

| Функция | Receiver (`this`) | Аргумент (`it`) | Возвращает | DSL-роль |
|---------|-------------------|------------------|------------|----------|
| `apply` | да | нет | `this` | Конфигурация объекта |
| `with` | да | нет | результат lambda | Работа с объектом |
| `run` | да | нет | результат lambda | Инициализация + вычисление |
| `let` | нет | да | результат lambda | Трансформация |
| `also` | нет | да | `this` | Side-эффект |

Среди них `apply` — основа большинства DSL-builder-ов: он даёт `this` для конфигурации без префикса и возвращает сам объект, что идеально ложится на паттерн «создать → настроить → отдать»:

```kotlin
// apply — сердце паттерна builder
fun server(init: ServerConfig.() -> Unit): Server =
    ServerConfig().apply(init).build()

// with — альтернатива для работы с существующим объектом
with(config) {
    host = "0.0.0.0"
    port = 443
}

// run — инициализация + получение результата
val connection = DatabaseConfig().run {
    url = "jdbc:postgresql://localhost/db"
    username = "admin"
    connect()  // возвращает Connection
}
```

Подробнее о scope-функциях — в [основах Kotlin](kotlin-interview.md).

<a id="q12"></a>

## Q12. Почему компилятор запретил вызвать lambda-параметр `inline`-функции из анонимного объекта?

Обе функции возвращают исходный объект, поэтому обе подходят для цепочек. Ключевое различие — **способ доступа к объекту**: `apply` даёт его как receiver (`this`), а `also` — как параметр (`it`):

```kotlin
// apply — receiver (this), идеально для DSL-конфигурации
val config = ServerConfig().apply {
    host = "0.0.0.0"      // this.host = ...
    port = 8080            // this.port = ...
    ssl {                  // this.ssl { ... }
        enabled = true
    }
}

// also — параметр (it), полезно для side-эффектов
val config = ServerConfig().apply {
    host = "0.0.0.0"
    port = 8080
}.also {
    logger.info("Created config: ${it.host}:${it.port}")
    validate(it)
}
```

В DSL почти всегда используют `apply` (или самостоятельно реализованные lambda с receiver), потому что `this` позволяет обращаться к свойствам и методам **без префикса** — это создаёт чистый декларативный синтаксис. `also` полезен для **логирования**, **валидации** и других side-эффектов, где нужно обратиться к объекту по имени.

Блок, переданный `inline`-функции, по умолчанию допускает non-local return, поэтому вызвать его изнутри другого контекста выполнения — вложенной не-inline lambda, анонимного объекта или локального класса — компилятор запрещает, пока параметр не помечен `crossinline`, снимающим право на non-local return.

<a id="q13"></a>

## Q13. (!) В builder-е оставили `noinline` lambda-параметр. Почему после добавления `inline` число созданных объектов lambda не уменьшилось?

**Gradle Kotlin DSL** — это скрипты сборки `build.gradle.kts` вместо `build.gradle` на Groovy. Под капотом `Gradle` компилирует каждый файл как обычный `Kotlin`-код, где тело блоков выполняется в контексте receiver-ов `Project`, `DependencyHandler`, `PluginDependenciesSpec` и других — поэтому всё, что вы пишете, статически типизировано и проверяется компилятором.

```kotlin
// build.gradle.kts
plugins {
    kotlin("jvm") version "2.0.0"          // type-safe accessor
    id("org.springframework.boot") version "3.3.0"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
```

Преимущества перед Groovy DSL:

| Аспект | Groovy DSL | Kotlin DSL |
|--------|-----------|------------|
| Типизация | Динамическая | Статическая |
| IDE-поддержка | Ограниченная | Полная (автодополнение, навигация) |
| Ошибки | В runtime | При компиляции |
| Рефакторинг | Ненадёжный | Надёжный |
| Документация API | Нужно искать | Доступна через IDE |

**Как это работает.** `Gradle` генерирует **type-safe accessors** — extension-функции и свойства, специфичные для применённых плагинов. Например, плагин `java` даёт accessor `tasks.test`, а плагин `application` — блок `application { mainClass.set("...") }`. Именно поэтому автодополнение «знает» про задачи и конфигурации конкретного проекта, а не предлагает универсальный набор.

Модификатор `inline` позволяет подставить тело обычного inline lambda-параметра в место вызова. Параметр `noinline` остаётся значением функции: его можно сохранить или передать дальше, но его тело не подставляется. Поэтому один `inline` не гарантирует уменьшения числа объектов в замере; фактические выделения памяти зависят также от захватов переменных и оптимизаций платформы.

<a id="q14"></a>

## Q14. Обобщённая функция-обёртка пытается вызвать `inline reified`-проверку со своим типом `T`, но компилятор отклоняет вызов. Почему?

`reified` делает тип-параметр доступным в месте встраивания `inline`-функции. Обычная обобщённая обёртка `fun <T> wrapper()` не может передать свой `T` в вызов `inline fun <reified T> checkType()`: компилятор отклонит такой код, а не выполнит проверку по верхней границе типа. Обёртка должна сама быть `inline fun <reified T> wrapper()` либо принимать токен типа, например `KClass<T>`.

**Type-safe accessors** — Kotlin extension-функции и свойства, которые `Gradle` генерирует для доступных элементов модели: задач, конфигураций и extension-ов плагинов. Набор для build-скрипта определяется сразу после блока `plugins { }`; элементы плагина, применённого позже в теле скрипта, в него не попадают.

```kotlin
plugins {
    java                        // Генерирует accessors для Java-задач
    id("org.springframework.boot") version "3.3.0"
}

// Type-safe accessor для задачи (вместо tasks.getByName("test"))
tasks.test {
    useJUnitPlatform()
    jvmArgs("-Xmx1g")
}

// Type-safe accessor для конфигурации зависимостей
dependencies {
    implementation("com.example:lib:1.0")       // Accessor от java-плагина
    testImplementation("org.junit:junit:5.10")
}

// Без type-safe accessors (legacy-способ)
dependencies {
    add("implementation", "com.example:lib:1.0")
}
```

**Подводный камень.** Accessors генерируются только для плагинов из блока `plugins { }`. Если плагин подключён через legacy-синтаксис `apply plugin:`, типизированных accessors не будет — придётся возвращаться к строковым именам конфигураций (`add("implementation", ...)`) и терять автодополнение.

При прямом вызове `checkType<String>()` конкретный тип известен компилятору. Добавление только `inline` к обёртке не исправляет ошибку: её собственный тип-параметр тоже нужно объявить `reified`.

<a id="q15"></a>

## Q15. Почему выражение `a shl 1 + 2` даёт не тот результат, который ожидают при чтении слева направо?

Оба механизма решают одну задачу — убрать дублирование конфигурации между модулями, но с разных сторон.

Инфиксный вызов имеет более низкий приоритет, чем арифметические операторы, поэтому в выражении `a shl 1 + 2` сначала вычисляется `1 + 2`, и infix-функция получает уже готовую сумму, а не левый операнд по порядку чтения — исправляется это только явными скобками.

**`buildSrc`** — специальная директория в корне `Gradle`-проекта: код из неё автоматически компилируется и становится доступен во всех `build.gradle.kts`. Это место для общей логики сборки (версии, хелперы, плагины).

**Convention plugins** — переиспользуемые плагины, которые описывают «соглашения» (конвенции) для типовых модулей. Их кладут в `buildSrc` и применяют в каждом модуле одной строкой:

```kotlin
// buildSrc/src/main/kotlin/kotlin-conventions.gradle.kts
plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
```

```kotlin
// modules/my-module/build.gradle.kts
plugins {
    id("kotlin-conventions")  // Применяем конвенцию
}

dependencies {
    implementation("com.example:my-lib:1.0")  // Только специфичные зависимости
}
```

**Что это даёт:**

- **DRY** — общая конфигурация живёт в одном месте, а не копируется по модулям.
- **Типобезопасность** — convention plugin компилируется как `Kotlin`, ошибки ловятся при сборке плагина.
- **Композиция** — можно завести несколько конвенций (`kotlin-conventions`, `spring-conventions`, `test-conventions`) и комбинировать их в модуле под его нужды.

<a id="q16"></a>

## Q16. (!) У объекта есть `invoke(() -> Unit)` и `invoke(Builder.() -> Unit)`. Почему вызов `obj { println("ok") }` неоднозначен?

**`Exposed`** — `Kotlin`-библиотека от JetBrains для работы с базами данных. DSL-подход строит SQL-запросы через типобезопасные выражения:

```kotlin
// Определение таблицы
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    val email = varchar("email", 100)
    val active = bool("active").default(true)

    override val primaryKey = PrimaryKey(id)
}

// Запросы через DSL
transaction {
    // INSERT
    Users.insert {
        it[name] = "Alice"
        it[email] = "alice@example.com"
    }

    // SELECT с условием
    val activeUsers = Users
        .select(Users.name, Users.email)
        .where { Users.active eq true }
        .map { row -> row[Users.name] to row[Users.email] }

    // UPDATE
    Users.update({ Users.id eq 1 }) {
        it[active] = false
    }

    // JOIN
    val result = (Users innerJoin Orders)
        .select(Users.name, Orders.total)
        .where { Orders.total greater 100.0 }
}
```

**Как это устроено под капотом.** `Table` — это объект с колонками типа `Column<T>`, и тип колонки переносится в выражения, поэтому `Users.active eq true` типобезопасен. Методы `select`, `insert`, `update` возвращают builder-ы запросов; `where` принимает lambda `SqlExpressionBuilder.() -> Op<Boolean>`; `eq`, `greater`, `like` — это infix-функции на `Column<T>`, из которых и собирается дерево условий. Всё выполняется внутри `transaction { }` — lambda с receiver `Transaction`, который управляет соединением и коммитом.

Запись `obj { ... }` вызывает `obj.invoke { ... }`. Если у объекта есть и `invoke(() -> Unit)`, и `invoke(Builder.() -> Unit)`, блок `{ println("ok") }` подходит обеим сигнатурам. Компилятор Kotlin 2.4.10 сообщает о неоднозначности, а не выбирает перегрузку по порядку объявления или одному лишь наличию receiver-а. Разведите такие операции по именам либо передайте аргумент с явно заданным функциональным типом.

<a id="q17"></a>

## Q17. Во вложенном DSL-блоке нет своего `unaryPlus`, а внешний receiver доступен. Почему оператор добавил элемент во внешнюю коллекцию?

`Exposed` предлагает два подхода к работе с данными, и выбор между ними — это компромисс «контроль над SQL против удобства работы с объектами»:

| Аспект | DSL | DAO |
|--------|-----|-----|
| Стиль | SQL-подобный, запросы | ORM, сущности |
| Определение | `object Users : Table()` | `class User : Entity()` |
| Запрос | `Users.select { ... }` | `User.find { ... }` |
| Результат | `ResultRow` (map) | Типизированный объект |
| Ленивые связи | Нет | Да (`referencedOn`) |
| Производительность | Выше (ближе к SQL) | Ниже (объекты, lazy loading) |
| Гибкость SQL | Полная | Ограниченная |

```kotlin
// DAO-подход
class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)
    var name by Users.name
    var email by Users.email
    val orders by OrderEntity referrersOn Orders.userId
}

// Использование DAO
transaction {
    val user = UserEntity.new {
        name = "Bob"
        email = "bob@example.com"
    }
    val users = UserEntity.find { Users.active eq true }
}
```

**Когда что выбирать.** DSL предпочтителен для сложных запросов, агрегаций и отчётов — там, где важен контроль над генерируемым SQL и производительность. DAO удобнее для CRUD-операций с объектами и навигации по связям, но платит за это ленивой загрузкой и дополнительными запросами.

Во вложенном DSL-блоке внешний receiver остаётся в области видимости, и если у внутреннего receiver нет собственной перегрузки `unaryPlus`/`plusAssign`, компилятор без ошибки привязывает `+` к ближайшему подходящему внешнему receiver, из-за чего элемент добавляется в коллекцию внешнего builder-а; закрыть такой захват можно только аннотацией `@DslMarker`.

<a id="q18"></a>

## Q18. (!) У элемента списка вызвали `items[i].copy(...)`, но результат не записали обратно. Почему значение в списке не изменилось?

**`Ktor`** описывает маршруты через вложенные lambda с receiver, и структура кода повторяет структуру URL-дерева. Каждый блок задаёт контекст типа `Route`, внутри которого доступны `get`, `post`, `put`, `delete` (регистрация обработчика) и `route` (вложенный сегмент пути):

```kotlin
fun Application.configureRouting() {
    routing {
        route("/api") {
            route("/users") {
                get {
                    val users = userService.findAll()
                    call.respond(users)
                }
                get("/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val user = userService.findById(id)
                    call.respond(user ?: HttpStatusCode.NotFound)
                }
                post {
                    val request = call.receive<CreateUserRequest>()
                    val user = userService.create(request)
                    call.respond(HttpStatusCode.Created, user)
                }
            }
        }
    }
}
```

```mermaid
graph TD
    A["routing { }"] -->|Route| B["route('/api') { }"]
    B -->|Route| C["route('/users') { }"]
    C -->|GET /api/users| D["get { }"]
    C -->|"GET /api/users/{id}"| E["get('/{id}') { }"]
    C -->|POST /api/users| F["post { }"]
```

**Механика по шагам:** `routing` — это extension-функция на `Application`, принимающая `Routing.() -> Unit`; `route` — extension на `Route`, создающая вложенный сегмент; `get`/`post` регистрируют обработчик с lambda `PipelineContext<Unit, ApplicationCall>.() -> Unit`, внутри которой через `call` доступны параметры запроса и ответ. Вложенность блоков напрямую формирует путь: `route("/api") { route("/users") { get { } } }` даёт `GET /api/users`.

Обращение `items[i]` вызывает `operator get` и получает элемент списка. Вызов `copy(...)` у неизменяемого data class создаёт новый объект, но сам по себе не заменяет элемент в коллекции. Для замены нужна обратная запись через `operator set`: `items[i] = items[i].copy(...)`.

<a id="q19"></a>

## Q19. Почему перестановка двух вызовов в type-safe builder компилируется, но нарушает поведение при выполнении?

В `Ktor` вся конфигурация приложения строится через extension-функции на `Application` и вложенные lambda с receiver — каждый уровень настройки получает свой типизированный контекст:

```kotlin
fun main() {
    embeddedServer(Netty, port = 8080) {
        // this = Application
        install(ContentNegotiation) {
            // this = ContentNegotiationConfig
            json(Json {
                // this = JsonBuilder
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
        install(StatusPages) {
            exception<Throwable> { call, cause ->
                call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Error")
            }
        }
        configureRouting()
        configureSecurity()
    }.start(wait = true)
}

// Модульная конфигурация через extension-функции
fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "my-app"
            verifier(jwtVerifier)
            validate { credential -> /* ... */ }
        }
    }
}
```

**Ключевая идея.** Каждый `install(Feature) { ... }` принимает lambda с receiver, тип которой — конфигурация именно этой фичи, поэтому внутри блока доступны только её настройки. А extension-функции на `Application` (`configureRouting`, `configureSecurity`) позволяют разнести конфигурацию по файлам, не теряя DSL-стиль. Это паттерн **модульного DSL** — частый вопрос на собеседованиях, потому что показывает, как масштабировать DSL без «полотна» в одном `main`.

При этом type-safe builder проверяет только типы и область видимости вызовов, но не их порядок: DSL-блок последовательно мутирует состояние конфигурации, поэтому переставленные `install`-вызовы остаются корректной программой и компилируются молча, а рассогласование вскрывается уже при выполнении — на старте сервера или на первом запросе.

<a id="q20"></a>

## Q20. Почему отложенная lambda увидела новое значение изменённой переменной вместо значения при создании?

**`kotlinx.html`** — библиотека, которая генерирует HTML прямо из `Kotlin`-кода через type-safe builder-ы. Каждый HTML-тег представлен классом, а методы внутри него соответствуют разрешённым дочерним тегам — поэтому невалидную разметку (`li` вне списка) не собрать:

```kotlin
val page = createHTML().html {
    head {
        title { +"My Page" }
        link(rel = "stylesheet", href = "/css/style.css")
    }
    body {
        div(classes = "container") {
            h1 { +"Welcome" }
            ul {
                for (item in items) {
                    li(classes = if (item.active) "active" else "") {
                        a(href = item.url) { +item.name }
                    }
                }
            }
        }
    }
}
```

| Аспект | `kotlinx.html` | Шаблонизатор (Thymeleaf, FreeMarker) |
|--------|----------------|--------------------------------------|
| Типобезопасность | Полная (компилятор) | Нет (строки) |
| IDE-поддержка | Автодополнение | Ограниченная |
| Логика | Kotlin (`for`, `if`, `when`) | Свой язык шаблонов |
| Ошибки | При компиляции | При рендеринге |
| Разделение | Код = разметка | HTML отдельно от логики |
| Дизайнеры | Не могут работать | Могут редактировать HTML |

**Где применяется.** `kotlinx.html` используют в `Ktor` для серверного рендеринга и в Kotlin/JS для DOM-манипуляций. Главное преимущество перед шаблонизаторами — полная мощь `Kotlin` прямо внутри builder-а: циклы, условия и функции для генерации разметки, без отдельного языка шаблонов. Расплата — разметку нельзя отдать дизайнеру и она компилируется вместе с кодом.

Отдельно стоит помнить, что lambda захватывает саму переменную, а не её значение: отложенный DSL-блок при позднем вызове читает текущее состояние захваченной изменяемой переменной, а не то, что было в момент сохранения, и зафиксировать снимок можно только копированием в локальный `val` до создания lambda.

<a id="q21"></a>

## Q21. (!) Проверка в теле `build.gradle.kts` сработала при запуске посторонней задачи. Почему она выполнилась, хотя эта задача её не вызывает?

**Context receivers** (experimental) — механизм, позволяющий функции требовать **несколько контекстов** одновременно, без наследования и без передачи их параметрами. Исходный дизайн с флагом `-Xcontext-receivers` объявлен устаревшим, а на смену ему пришли **context parameters** (синтаксис `context(name: Type)`, флаг `-Xcontext-parameters`) — преемник для тех же задач.

**Какую проблему решают.** Обычная extension-функция имеет ровно **один** receiver. Если функция логически нужна сразу в контексте `Transaction` и `Logger`, один из них приходится тащить параметром — это шумит в сигнатуре и в каждом вызове. Context receivers убирают этот компромисс: оба контекста становятся неявными.

```kotlin
// Без context receivers — один receiver, второй через параметр
fun Transaction.loggedInsert(logger: Logger, table: Table, body: InsertStatement.() -> Unit) {
    logger.info("Inserting into ${table.tableName}")
    table.insert(body)
}

// С context receivers — оба контекста неявные
context(Transaction, Logger)
fun Table.loggedInsert(body: InsertStatement.() -> Unit) {
    info("Inserting into $tableName")  // Logger.info
    insert(body)                        // Transaction.insert
}
```

**Зачем это DSL.** Появляется возможность объявлять функции, вызвать которые можно **только** при наличии нужных контекстов, не загромождая при этом receiver. Компилятор сам проверит, что функция вызвана в правильном месте:

```kotlin
context(TransactionContext)
fun UserRepository.findActive(): List<User> = 
    Users.select { Users.active eq true }.map { it.toUser() }

// Используется только внутри transaction { }
transaction {
    val users = userRepo.findActive()  // ✅ Контекст доступен
}
// userRepo.findActive()  // ❌ Вне transaction — ошибка компиляции
```

**Статус.** Фича остаётся experimental, и сам дизайн пережил смену: ранний вариант с флагом `-Xcontext-receivers` признан устаревшим, его заменили **context parameters** (`context(name: Type)`, флаг `-Xcontext-parameters`). Идея сохраняется: несколько неявных контекстов закрывают часть сценариев `@DslMarker` и extension-функций, позволяя строить более выразительные и безопасные DSL без подмены контекста параметрами. При подготовке к собеседованию проверяйте текущий статус фичи и актуальный флаг по официальной документации.

Код прямо в теле `build.gradle.kts` выполняется при конфигурации проекта, а не как действие выбранной задачи. Поэтому проверка может сработать и при запуске посторонней задачи. Если Gradle переиспользует действительную запись configuration cache, фазу конфигурации он пропускает и тело скрипта не выполняет. Проверку, нужную только при выполнении задачи, помещают в её действие (`doFirst`/`doLast`), а задачу можно регистрировать лениво через `tasks.register`.

<a id="q22"></a>

## Q22. Почему после применения Gradle-плагина через `apply(plugin = ...)` в `build.gradle.kts` недоступны type-safe accessors?

**Extension properties** добавляют типу свойство, не меняя сам класс. В DSL они удобны тем, что внутри нужного receiver-а появляется вычисляемое значение, которое читается как встроенное поле, — но логика остаётся снаружи класса:

```kotlin
// В контексте маршрута Ktor — удобный доступ к параметрам
val PipelineContext<Unit, ApplicationCall>.userId: Int
    get() = call.parameters["userId"]?.toIntOrNull()
        ?: throw BadRequestException("Invalid userId")

// В контексте конфигурации — вычисляемые свойства
val ServerConfig.baseUrl: String
    get() = "${if (sslEnabled) "https" else "http"}://$host:$port"

// В контексте тестового DSL
val TestContext.randomEmail: String
    get() = "test-${UUID.randomUUID()}@example.com"
```

**Ограничение.** Extension property не имеет backing field — есть только getter (и setter для `var`, если хранилище лежит снаружи). Поэтому хранить состояние в нём нельзя, он лишь вычисляет значение из receiver-а. Внутри DSL-блока обращение выглядит как к обычному полю: `this.baseUrl` или просто `baseUrl`.

В Gradle Kotlin DSL набор type-safe accessors определяется сразу после блока `plugins { }`, до выполнения остального тела скрипта. Если плагин применён позже через `apply(plugin = "...")`, его элементы не попадают в этот набор; настройки можно задать через `configure<T> { }` или `the<T>()`.

<a id="q23"></a>

## Q23. (!) Почему после `plugins { id(...) apply false }` в том же build-скрипте нет type-safe accessors плагина?

**`inline`** позволяет подставить тело lambda в место вызова и избежать создания объекта для этого аргумента. Это не обещание, что каждый вызов не-inline функции обязательно создаст новый объект: lambda без захваченных значений может переиспользоваться.

В Gradle Kotlin DSL type-safe accessors строятся по набору плагинов, которые блок `plugins { }` применил именно к текущему проекту, поэтому при объявлении с `apply false` или при применении плагина к дочерним проектам внутри `subprojects { }` к текущему проекту он не применён и accessors для его extension-ов в этом скрипте не появляются. При `apply false` нет и самого extension-а в текущем проекте: чтобы настраивать его, сначала применяют плагин.

```kotlin
// Без inline — lambda передаётся как объект функции
fun html(block: HTML.() -> Unit): HTML = HTML().apply(block)

// С inline — нет аллокации, код встраивается
inline fun html(block: HTML.() -> Unit): HTML = HTML().apply(block)
```

```mermaid
graph LR
    subgraph "Без inline"
        A1["Вызов html { }"] --> B1["Создание Function объекта"]
        B1 --> C1["HTML().apply(fn)"]
        C1 --> D1["fn.invoke(html)"]
    end
    subgraph "С inline"
        A2["Вызов html { }"] --> B2["HTML()"]
        B2 --> C2["Код блока прямо здесь"]
    end
```

Когда использовать `inline` в DSL:

- **Да**: top-level функции-точки входа (`html { }`, `config { }`, `server { }`), которые вызываются часто и lambda исполняется сразу
- **Нет**: если lambda сохраняется для отложенного вызова (event handler, callback) — `inline` запретит хранить обычный inline-параметр (для хранения нужен `noinline`; `crossinline` разрешает вызов из вложенного контекста, но не хранение)

Дополнительный бонус `inline` — поддержка `reified` для generic-параметров:

```kotlin
inline fun <reified T> Config.get(key: String): T {
    val value = properties[key]
    return when (T::class) {
        String::class -> value as T
        Int::class -> value?.toInt() as T
        else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
    }
}

// Использование: val port: Int = config.get("port")
```

<a id="q24"></a>

## Q24. Почему Ktor выбрал маршрут с литеральным сегментом, хотя маршрут с `{param}` объявлен раньше?

Уровень обязательности задаётся типом параметра и наличием у него default-значения — часть проверок ложится на компилятор, часть на `build()`.

**Обязательный блок** — это параметр без default-значения: компилятор просто не даст вызвать функцию без него:

```kotlin
fun html(init: HTML.() -> Unit): HTML = HTML().apply(init)
// html() — ❌ ошибка компиляции
// html { } — ✅
```

**Опциональный блок** — nullable lambda с default `null`:

```kotlin
class ServerConfig {
    private var _ssl: SslConfig? = null

    fun ssl(init: (SslConfig.() -> Unit)? = null) {
        _ssl = SslConfig().apply { init?.invoke(this) }
    }
}

// Оба варианта валидны:
server { ssl() }              // SSL с defaults
server { ssl { port = 443 } } // SSL с настройками
server { /* без SSL */ }       // Без SSL
```

**Обязательные поля** можно валидировать в `build()`:

```kotlin
class DatabaseConfig {
    var url: String? = null
    var username: String? = null
    var password: String? = null

    fun build(): DataSource {
        requireNotNull(url) { "Database URL is required" }
        requireNotNull(username) { "Username is required" }
        return DataSource(url!!, username!!, password ?: "")
    }
}
```

**Итог.** Стандартный паттерн — сочетать оба механизма: compile-time проверки гарантируют наличие обязательных блоков, а runtime-валидация в `build()` ловит незаполненные поля и невалидные комбинации, которые типами не выразить.

В routing-DSL Ktor порядок объявления маршрутов не определяет выбор обработчика: маршруты складываются в дерево селекторов, и для каждого запроса берётся ветка с наибольшим качеством совпадения, где константный сегмент пути весит больше, чем `{param}` или wildcard, — поэтому при общем начале пути запрос уходит в более специфичный маршрут, даже если он записан ниже.

<a id="q25"></a>

## Q25. Почему маршрут с дополнительным сегментом, вложенный в `{param...}`, не получает запросы с этим сегментом?

Для гибкого API подходы комбинируют — каждый закрывает свой тип параметров. Вот пять основных:

```kotlin
// 1. Именованные аргументы — для простых параметров
fun div(id: String? = null, classes: String = "", init: Div.() -> Unit) { ... }
div(id = "main", classes = "container") { +"content" }

// 2. Свойства receiver-а — для конфигурации
class ServerConfig {
    var host = "localhost"
    var port = 8080
    var workers = Runtime.getRuntime().availableProcessors()
}
server { host = "0.0.0.0"; port = 443 }

// 3. Вложенные блоки — для сложной подконфигурации
class ServerConfig {
    fun ssl(init: SslConfig.() -> Unit) { ... }
    fun logging(init: LoggingConfig.() -> Unit) { ... }
}
server {
    ssl { certPath = "/certs/cert.pem" }
    logging { level = Level.INFO }
}

// 4. Infix-функции — для пар ключ-значение
class HeadersBuilder {
    infix fun String.to(value: String) { headers[this] = value }
}
headers { "Content-Type" to "application/json" }

// 5. Vararg — для списков
fun tags(vararg values: String) { ... }
tags("kotlin", "dsl", "interview")
```

**Что ценят на собеседовании** — понимание, какой подход когда уместен: именованные аргументы для «плоских» параметров, свойства receiver-а для конфигурации, вложенные блоки для иерархической подконфигурации, infix для читаемых пар ключ-значение, `vararg` для списков.

В routing-DSL Ktor tailcard-селектор `{param...}` потребляет все оставшиеся сегменты пути. Поэтому вложенный в него маршрут, которому нужен ещё один сегмент, не сможет совпасть с этим же запросом: отдельного остатка пути больше нет. Маршруты с собственными сегментами размещают рядом с tailcard, чтобы Ktor сравнивал их как отдельные ветки.

<a id="q26"></a>

## Q26. Почему плагин Ktor, установленный внутри одного маршрута, не обрабатывает соседние маршруты?

| Аспект | Fluent API | DSL (Kotlin) |
|--------|-----------|-------------|
| Структура | Плоская цепочка | Вложенные блоки |
| Синтаксис | `builder.setX().setY().build()` | `config { x = ...; y { } }` |
| Контекст | Один объект по цепочке | Каждый блок — свой receiver |
| Вложенность | Нет (или awkward) | Естественная |
| Scope control | Нет | `@DslMarker` |
| Типичный язык | Java, C# | Kotlin, Groovy, Scala |

```kotlin
// Fluent API (Java-стиль)
val server = ServerBuilder()
    .host("0.0.0.0")
    .port(443)
    .ssl(SslBuilder().certPath("/cert").keyPath("/key").build())
    .build()

// Kotlin DSL
val server = server {
    host = "0.0.0.0"
    port = 443
    ssl {
        certPath = "/cert"
        keyPath = "/key"
    }
}
```

**Суть различия.** DSL читается как конфигурация или разметка, fluent API — как последовательность команд. Технически оба типобезопасны и оба строят объект; разница — в модели чтения (декларативная против императивной) и в форме данных. DSL естественно ложится на иерархические структуры за счёт вложенных receiver-ов, fluent API — на линейные pipeline-ы, где вложенность превращается в неудобные цепочки вложенных билдеров.

В Ktor вызов `install(...)` внутри блока `route(...)` регистрирует plugin в pipeline именно этого узла дерева маршрутов, поэтому его перехватчики отрабатывают только на этом маршруте и его вложенных потомках, а соседние маршруты идут по собственному pipeline — чтобы обработка действовала на все маршруты, plugin ставят на уровне `Application`.

<a id="q27"></a>

## Q27. `Query` из Exposed создали внутри `transaction {}`, а итерировать начали после выхода из блока. Почему чтение завершилось ошибкой?

Тестовые DSL не вводят ничего нового — они комбинируют уже знакомые приёмы (**infix-функции**, **extension-функции**, **lambda с receiver**), чтобы assertion-ы и сценарии читались как обычный текст:

```kotlin
// Kotest-стиль: infix + extension
class StringSpec : FunSpec({
    test("string length") {
        "hello" shouldHaveLength 5
        "hello".shouldStartWith("hel")
        "hello" shouldBe "hello"
    }

    test("collection matchers") {
        listOf(1, 2, 3) shouldContain 2
        listOf(1, 2, 3) shouldHaveSize 3
        listOf(1, 2, 3).shouldBeSorted()
    }

    test("exception matchers") {
        shouldThrow<IllegalArgumentException> {
            parseAge(-1)
        }.message shouldBe "Age must be positive"
    }
})
```

```kotlin
// BDD-стиль: вложенные блоки
class UserServiceSpec : BehaviorSpec({
    given("a registered user") {
        val user = createUser("alice@test.com")

        `when`("login with correct password") {
            val result = authService.login(user.email, "correct")

            then("returns success token") {
                result.shouldBeInstanceOf<LoginResult.Success>()
                result.token.shouldNotBeBlank()
            }
        }

        `when`("login with wrong password") {
            val result = authService.login(user.email, "wrong")

            then("returns failure") {
                result.shouldBeInstanceOf<LoginResult.Failure>()
            }
        }
    }
})
```

**Как это устроено:** `shouldBe` — infix extension-функция на `Any?`; `given`/`when`/`then` — функции с lambda с receiver `BehaviorSpecRootScope`, которые формируют дерево спецификации; `shouldThrow<T>` — inline reified-функция, ловящая исключение нужного типа. То есть весь читаемый синтаксис — это те же базовые приёмы Kotlin DSL, перенесённые на тестовый домен.

В Exposed `Query` не выполняет SQL при одном лишь построении: запрос исполняется при потреблении результатов. При этом для построения запроса тоже может понадобиться транзакционный контекст и соединение; утверждение, что соединение впервые берётся только при итерации, неверно. Итерируйте запрос и материализуйте нужные строки через `toList()` внутри `transaction {}`, если они понадобятся после выхода из блока.

Подробнее о тестировании в `Kotlin` — в [основах Kotlin](kotlin-interview.md).

<a id="q28"></a>

## Q28. Почему после выборки N DAO-сущностей Exposed с разными владельцами обращение к не загруженным заранее ссылкам даёт N дополнительных запросов?

| DSL | Область | Ключевой механизм |
|-----|---------|-------------------|
| **Gradle Kotlin DSL** | Сборка проекта | Lambda с receiver на `Project` |
| **Ktor** | HTTP-сервер | Extension-функции на `Application`, `Route` |
| **Exposed** | SQL-запросы | Type-safe builder на `Table`, `Transaction` |
| **kotlinx.html** | HTML-генерация | Type-safe builder с `@HtmlTagMarker` |
| **Kotest** | Тестирование | Infix-функции, lambda с receiver |
| **Koin** | DI-контейнер | `module { single { } factory { } }` |
| **Compose** | UI (Android/Desktop) | `@Composable` + lambda с receiver |
| **Arrow** | FP-паттерны | Monad comprehensions, DSL для Either/Option |
| **kotlinx.serialization** | Сериализация | `Json { prettyPrint = true }` |
| **Spring Security DSL** | Безопасность | Lambda-конфигурация вместо chained builder-ов |
| **Detekt** | Статический анализ | Конфигурация правил |

Обратите внимание: при всём разнообразии доменов в основе почти каждого DSL лежат одни и те же два механизма — lambda с receiver и extension-функции. Именно поэтому, разобравшись в одном Kotlin DSL, легко читать остальные.

В DAO-слое Exposed ссылки `referencedOn` и `referrersOn` загружаются при первом обращении. Если выбрать N сущностей с разными связанными объектами без предзагрузки, обход может добавить N запросов к исходному `SELECT`: это сценарий N+1. Общие владельцы и кэш могут уменьшить число запросов. Для предзагрузки используют `.with(Entity::owner)` или `load(...)` внутри той же `transaction`.

```kotlin
// Koin DSL
val appModule = module {
    single { DatabaseConnection(get()) }
    factory { UserRepository(get()) }
    viewModel { UserViewModel(get()) }
}

// Compose DSL
@Composable
fun UserCard(user: User) {
    Card(modifier = Modifier.padding(16.dp)) {
        Text(text = user.name, style = MaterialTheme.typography.h6)
        Text(text = user.email, style = MaterialTheme.typography.body2)
    }
}

// Spring Security Kotlin DSL
http {
    authorizeHttpRequests {
        authorize("/api/public/**", permitAll)
        authorize("/api/admin/**", hasRole("ADMIN"))
        authorize(anyRequest, authenticated)
    }
    oauth2ResourceServer { jwt { } }
}
```

<a id="q29"></a>

## Q29. (!) Почему подстановка параметра в SQL через конкатенацию строки изменила результат запроса?

Большинство ошибок в DSL — это либо потеря контроля над scope, либо подмена читаемости «магией». Шесть самых частых:

**1. Отсутствие `@DslMarker`** — методы внешних scope утекают во вложенные блоки, порождая компилируемые, но бессмысленные конструкции и труднонаходимые баги:

```kotlin
// Плохо: body доступен внутри div из-за утечки scope
html { body { div { body { } } } }  // Компилируется, но бессмысленно
```

**2. Слишком глубокая вложенность** — DSL становится трудночитаемым:

```kotlin
// Плохо
config {
    server {
        network {
            ssl {
                certificate {
                    path { ... }  // 6 уровней — чрезмерно
                }
            }
        }
    }
}
```

**3. Неочевидная семантика операторов** — `+` и `-` для нестандартных действий:

```kotlin
// Плохо: что значит минус на Route?
-route("/api")  // Удаление? Отключение? Неочевидно
```

**4. Отсутствие `inline`** — накладные расходы на аллокацию lambda при каждом вызове.

**5. Mutable state без валидации** — DSL позволяет установить невалидную комбинацию свойств:

```kotlin
// Плохо: port может быть отрицательным
server { port = -1 }

// Лучше: валидация в build() или через setter
var port: Int = 8080
    set(value) {
        require(value in 1..65535) { "Port must be 1..65535" }
        field = value
    }
```

**6. Нет точки входа** — DSL доступен только через конструктор + apply, а не через удобную top-level функцию.

Ошибка того же рода — собирать условие выборки склейкой строк вместо типобезопасных операторов DSL: подставленное значение попадает в текст запроса как его часть, и кавычки или служебные символы внутри него меняют саму структуру условия, тогда как выражение вида `Users.name eq value` передаёт значение отдельным связанным параметром и структуру запроса изменить не может.

<a id="q30"></a>

## Q30. DSL принимает обычную `Builder.() -> Unit` lambda. Почему после `launch` блок продолжил работу, не дождавшись suspend-вызова?

Короткий ответ: DSL оправдан, когда он многократно экономит усилия на чтении кода, и не оправдан, когда добавляет сложности больше, чем убирает. Конкретные ориентиры:

**Стоит создавать DSL, когда:**
- Конфигурация **иерархическая** и вложенная (маршруты, деревья, разметка)
- API используется **многократно** разными людьми или командами
- Важна **читаемость** для не-разработчиков (BDD-тесты, конфигурация)
- Есть **повторяющийся паттерн** с вариациями (SQL-запросы, UI-компоненты)
- Нужна **type-safety** вместо строковых конфигов (YAML, JSON, XML)

**Не стоит создавать DSL, когда:**
- Достаточно **обычного API** с именованными аргументами
- Конфигурация **плоская** (несколько полей без вложенности)
- DSL будет использоваться **один раз** — стоимость разработки не окупится
- Команда **не знакома** с Kotlin-специфичными приёмами (lambda с receiver, operator overloading)
- Задача **линейная** — fluent API или обычные функции проще и понятнее

```mermaid
graph TD
    A["Нужен ли DSL?"] -->|"Иерархическая структура?"| B{"Да"}
    A -->|"Плоская конфигурация?"| C{"Нет"}
    B -->|"Многократное использование?"| D{"Да"}
    B -->|"Одноразовый код?"| C
    D -->|"Команда знает Kotlin?"| E["Создавать DSL ✅"]
    D -->|"Команда на Java?"| F["Fluent API ✅"]
    C --> G["Обычный API / именованные аргументы ✅"]
```

Золотое правило: DSL оправдан, когда **стоимость чтения кода с DSL значительно ниже**, чем без него, и эта экономия проявляется **многократно** за время жизни проекта.

В вопросе DSL принимает обычную `Builder.() -> Unit` lambda, поэтому прямой вызов suspend-функции в ней невозможен. `launch` возвращает `Job` без ожидания результата: следующие строки блока могут выполниться раньше корутины. Если результат нужен внутри блока, его тип меняют на `suspend Builder.() -> Unit` и вызывают suspend-функцию напрямую. В таком блоке можно также вызвать `Job.join()`. Завершение общего coroutine scope само по себе не меняет порядок строк блока.

<a id="q31"></a>

## Q31. Почему повторное применение DSL-блока дважды добавило записи в общий список?

**Spring Beans DSL** — нативный `Kotlin`-DSL для регистрации бинов через функциональный API: бины описываются явно в блоке `beans { }`, без рефлексии, component scan и `@Configuration`-аннотаций. За счёт этого старт быстрее и приложение дружелюбнее к GraalVM Native Image.

При этом блок настройки остаётся обычной lambda с receiver, а не декларацией: builder исполняет её заново при каждом применении — повторном `apply(init)`, повторной регистрации того же инициализатора, переиспользовании одного экземпляра builder-а, — и поскольку тело блока дописывает элементы в общий mutable-список, а не заменяет его, каждое лишнее исполнение добавляет ещё один комплект записей.

```kotlin
// Традиционный подход через @Configuration
@Configuration
class AppConfig {
    @Bean
    fun userRepository(): UserRepository = JdbcUserRepository()

    @Bean
    fun userService(repo: UserRepository): UserService = UserServiceImpl(repo)
}

// Kotlin Beans DSL — через beans { }
val beans = beans {
    bean<JdbcUserRepository>()             // регистрация по типу
    bean<UserServiceImpl>()                // зависимость инжектируется автоматически

    // Явная регистрация с кастомной логикой
    bean {
        val repo = ref<UserRepository>()   // получить зарегистрированный бин
        UserServiceImpl(repo, env["app.timeout"].toInt())
    }

    // Условная регистрация
    profile("production") {
        bean<ProdDataSource>()
    }
    profile("test") {
        bean<TestDataSource>()
    }
}

// Регистрация в SpringApplication
fun main() {
    runApplication<MyApp>() {
        addInitializers(beans)
    }
}
```

**Преимущества Beans DSL:**

| Аспект | XML / `@Configuration` | Kotlin Beans DSL |
|---|---|---|
| Рефлексия | Да (проксирование CGLIB) | Нет |
| Производительность старта | Медленнее | Быстрее |
| Типобезопасность | Слабая (строки в XML, аннотации) | Полная (Kotlin types) |
| IDE-поддержка | Ограниченная для XML | Полная для Kotlin DSL |
| Условная регистрация | `@Conditional` / XML | `if`/`when` / `profile { }` |
| AOT-компиляция | Ограничена из-за рефлексии | Дружественен к GraalVM |

```kotlin
// Более продвинутый пример: конфигурация с env-параметрами
val beans = beans {
    bean {
        val config = ref<AppConfig>()
        DataSource().apply {
            url = config.databaseUrl
            username = config.dbUser
            password = config.dbPassword
            maximumPoolSize = config.poolSize
        }
    }

    bean<UserRepository> {
        JdbcUserRepository(ref())
    }

    // Функциональные роуты (Spring WebFlux)
    bean {
        router {
            GET("/users") { req ->
                val users = ref<UserService>().findAll()
                ok().bodyValueAndAwait(users)
            }
            POST("/users") { req ->
                val dto = req.awaitBody<CreateUserDto>()
                val user = ref<UserService>().create(dto)
                created(URI("/users/${user.id}")).bodyValueAndAwait(user)
            }
        }
    }
}
```

**Когда выбирать Beans DSL:**
- Новые Spring Boot + Kotlin проекты (особенно с WebFlux)
- Цель — быстрый старт и GraalVM Native Image
- Много условной конфигурации (сложнее читать через `@Conditional`)
- Отказ от CGLIB-проксирования `@Configuration` классов

<a id="q32"></a>

## Q32. Почему builder с незаданным обязательным параметром собрал объект без ошибки, а сбой случился при использовании?

Идея паттерна — отделить изменяемое состояние сборки от неизменяемого результата. Функция-билдер принимает lambda с receiver на mutable `Builder`-класс, выполняет её (lambda заполняет поля), а затем `build()` собирает из накопленного состояния immutable-объект. Снаружи остаётся чистый декларативный блок, внутри — управляемая мутабельность.

Отсюда и слабое место паттерна: обязательные параметры объявлены в builder-е как `var` с дефолтным значением, поэтому компилятор не в состоянии потребовать их заполнения — незаданный `url` остаётся пустой строкой, а `build()` без явных `require`/`checkNotNull` собирает такой объект молча, и несоответствие проявляется только при использовании результата.

```kotlin
// Шаг 1: неизменяемый объект (результат)
data class HttpRequest(
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val body: String?,
    val timeout: Long
)

// Шаг 2: mutable builder с DSL-методами
@HttpDsl
class HttpRequestBuilder {
    var method: String = "GET"
    var url: String = ""
    var body: String? = null
    var timeout: Long = 30_000
    private val headers = mutableMapOf<String, String>()

    fun header(name: String, value: String) {
        headers[name] = value
    }

    fun authorization(token: String) = header("Authorization", "Bearer $token")
    fun contentType(type: String) = header("Content-Type", type)
    fun json() = contentType("application/json")

    internal fun build() = HttpRequest(method, url, headers.toMap(), body, timeout)
}

// Шаг 3: top-level функция-точка входа
@DslMarker annotation class HttpDsl

fun httpRequest(init: HttpRequestBuilder.() -> Unit): HttpRequest =
    HttpRequestBuilder().apply(init).build()

// Использование
val request = httpRequest {
    method = "POST"
    url = "https://api.example.com/users"
    authorization("my-token")
    json()
    body = """{"name": "Alice"}"""
    timeout = 5_000
}
```

**Паттерн с вложенными builder-ами:**

```kotlin
// Для сложных структур — вложенные DSL-блоки
data class Pipeline(val stages: List<Stage>)
data class Stage(val name: String, val steps: List<String>)

@DslMarker annotation class PipelineDsl

@PipelineDsl
class PipelineBuilder {
    private val stages = mutableListOf<Stage>()

    fun stage(name: String, init: StageBuilder.() -> Unit) {
        stages += StageBuilder(name).apply(init).build()
    }

    fun build() = Pipeline(stages.toList())
}

@PipelineDsl
class StageBuilder(private val name: String) {
    private val steps = mutableListOf<String>()

    fun step(command: String) { steps += command }
    fun build() = Stage(name, steps.toList())
}

fun pipeline(init: PipelineBuilder.() -> Unit) = PipelineBuilder().apply(init).build()

val ci = pipeline {
    stage("build") {
        step("./gradlew build")
        step("./gradlew test")
    }
    stage("deploy") {
        step("docker build .")
        step("kubectl apply -f k8s/")
    }
}
```

<a id="q33"></a>

## Q33. Почему при вызове функции с trailing lambda пропущенный параметр получил значение по умолчанию?

**Gradle Kotlin DSL** — это, пожалуй, самый известный продакшн-DSL в экосистеме, и устроен он ровно из тех же кирпичей, что и любой учебный Kotlin DSL: lambda с receiver, extension-функции, type-safe accessors и `@DslMarker`. Разбирая `build.gradle.kts`, легко увидеть теорию в работе.

Такой блок — это последний параметр функции с типом lambda, вынесенный за скобки, поэтому все остальные аргументы передаются только внутри скобок: не указанный там параметр с умолчанием молча берёт дефолтное значение, вызов остаётся корректным для компилятора, и заданной оказывается лишь конфигурация из самого блока.

**Ключевые механизмы в build.gradle.kts:**

```kotlin
// plugins { } — это extension-функция на PluginDependenciesSpec
plugins {
    id("org.springframework.boot") version "3.2.0"
    kotlin("jvm") version "1.9.22"  // kotlin() — infix-подобный синтаксис
}

// dependencies { } — extension-функция на DependencyHandlerScope
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.junit.jupiter:junit-jupiter")
    // implementation, testImplementation — operator invoke на ConfigurationContainer
}

// tasks { } — extension-функция на TaskContainer
tasks {
    withType<Test> {
        useJUnitPlatform()
    }
    compileKotlin {       // type-safe accessor — сгенерированная extension-функция
        kotlinOptions.jvmTarget = "17"
    }
}
```

**Type-safe accessors** — Gradle генерирует их из установленных плагинов. Например, при наличии `kotlin("jvm")` появляются функции `compileKotlin { }`, `kotlin { }`, `sourceSets { }`.

**Convention plugins в buildSrc:**

```kotlin
// buildSrc/src/main/kotlin/spring-boot-convention.gradle.kts
plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
}

// Применяется в модулях:
// plugins { id("spring-boot-convention") }
```

**Преимущества перед Groovy DSL:**
- Полная статическая типизация — IDE находит ошибки сразу
- Нет динамических методов — автодополнение работает надёжно
- Рефакторинг работает корректно (переименование, навигация)
- Kotlin-идиомы (data class, when expression) в buildSrc-коде

**Типичные проблемы:**
- `Cannot access` — type-safe accessor не сгенерирован, нужно использовать строковое API (`tasks.named("compileKotlin")`)
- Медленная сборка при первом использовании из-за генерации accessors
- Groovy-функции (`ext { }`) конфликтуют с Kotlin-идиомами

<a id="q34"></a>

## Q34. В `buildList { add(Cat()); add(Dog()) }` компилятор принимает оба подкласса `Animal`. Почему тип списка не фиксируется по первому элементу?

**`reified`** сохраняет тип `T` доступным в runtime внутри `inline`-функций — благодаря встраиванию тип не стирается. В DSL это нужно для type-safe фабрик: можно написать `handler<OrderHandler> { }` и получить экземпляр нужного класса без явной передачи `Class<T>`.

`buildList` использует builder inference: компилятор учитывает ограничения из всего блока, а не фиксирует `T` по первому `add`. Например, `buildList { add(Cat()); add(Dog()) }` принимает оба подкласса и выводит подходящий общий тип. Если API требует именно `Animal`, тип можно задать явно: `buildList<Animal> { ... }` или `val animals: List<Animal> = buildList { ... }`. При явном `buildList<Cat>` вызов `add(Dog())` уже будет ошибкой компиляции.

```kotlin
// Регистрация по типу в DSL без явного указания класса
inline fun <reified T : Handler> ApplicationDsl.handler(
    noinline init: T.() -> Unit = {}
): T {
    val instance = context.getBean(T::class.java)
    instance.init()
    return instance
}

// Использование
application {
    handler<OrderHandler> {
        timeout = 30
    }
}
```

**Ограничения reified в builders:**

| Ограничение | Причина | Обход |
|-------------|---------|-------|
| Только в `inline`-функциях | `reified` недоступен в обычных функциях | Принять `KClass<T>` параметром |
| Нет рекурсивных вызовов | `inline`-функции не могут быть рекурсивными | Вынести рекурсию в не-inline функцию |
| Нельзя хранить лямбду | `noinline` снижает эффективность | Принять функциональный тип явно |
| Ограничения generics | `List<T>` — тип стирается только в `T`, не в контейнере | `typeOf<List<String>>()` из stdlib |

```kotlin
// Ошибка: нельзя использовать T после стирания вне inline
fun <T : Any> build(klass: KClass<T>, init: T.() -> Unit): T {
    val instance = klass.createInstance()  // Reflection
    instance.init()
    return instance
}

// Правильно: inline + reified
inline fun <reified T : Any> build(noinline init: T.() -> Unit): T =
    T::class.createInstance().apply(init)
```

**`typeOf<T>()`** — позволяет получить полный `KType` включая generic-параметры:

```kotlin
inline fun <reified T> registerSerializer() {
    val type = typeOf<T>()  // KType с полными generic-параметрами
    registry[type] = createSerializer(type)
}

registerSerializer<List<String>>()  // Корректно получает List<String>, не List<*>
```

<a id="q35"></a>

## Q35. Почему опечатка в строковом ключе `operator set` не была обнаружена при компиляции DSL?

**Test Data Builder** через Kotlin DSL создаёт тестовые фикстуры декларативно: все поля имеют разумные дефолты, а тест переопределяет только то, что важно для проверки. Это заметно чище конструктора с десятком позиционных параметров, где половина значений — заглушки «лишь бы скомпилировалось».

Компилятор проверяет имена свойств и функций receiver-класса builder-а. Строковый ключ, переданный в `operator set`, остаётся обычным значением, поэтому опечатка проходит сборку. Она обнаружится только тогда, когда сам DSL проверит ключ или попытается использовать связанную с ним настройку.

```kotlin
// Доменный объект
data class Order(
    val id: Long,
    val customerId: Long,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val createdAt: Instant
)

// DSL Builder
class OrderBuilder {
    var id: Long = 1L
    var customerId: Long = 100L
    var status: OrderStatus = OrderStatus.PENDING
    var createdAt: Instant = Instant.now()
    private val items = mutableListOf<OrderItem>()

    fun item(init: OrderItemBuilder.() -> Unit) {
        items += OrderItemBuilder().apply(init).build()
    }

    fun build() = Order(
        id = id,
        customerId = customerId,
        items = items.ifEmpty { listOf(OrderItemBuilder().build()) },
        status = status,
        totalAmount = items.sumOf { it.price },
        createdAt = createdAt
    )
}

fun order(init: OrderBuilder.() -> Unit = {}): Order =
    OrderBuilder().apply(init).build()

// В тестах:
@Test
fun `should cancel pending order`() {
    val pendingOrder = order {
        id = 42L
        status = OrderStatus.PENDING
        item { productId = 1L; price = BigDecimal("99.99") }
        item { productId = 2L; price = BigDecimal("49.99") }
    }

    val cancelledOrder = orderService.cancel(pendingOrder)
    assertThat(cancelledOrder.status).isEqualTo(OrderStatus.CANCELLED)
}

// Дефолтный объект — один вызов без параметров
val anyOrder = order()
```

**Преимущества перед обычными конструкторами:**
- Все параметры имеют разумные дефолты — тест указывает только значимые
- Читаемость: `order { status = PAID }` vs `Order(1L, 100L, emptyList(), PAID, ...)`
- Переиспользование: `fun paidOrder() = order { status = OrderStatus.PAID }`
- Вложенность: `item { }` создаёт связанные объекты декларативно

<a id="q36"></a>

## Q36. Почему `@DslMarker` на функции builder-а не скрыл внешний receiver во вложенном блоке?

**Operator overloading** в DSL применяют там, где операция естественно выглядит как унарный или математический оператор: «добавить элемент» через `+`, «обратиться по ключу» через `[]`, «настроить блок» через `()`. Цель — убрать имена методов и приблизить код к привычной нотации.

Ограничение неявных receiver-ов включается, когда один DSL-маркер применён к их типам: к классам receiver-ов, типам lambda с receiver или подходящим typealias. Аннотация на самой builder-функции scope не ограничивает, поэтому внешний receiver остаётся доступным.

```kotlin
// unaryPlus для добавления элементов
class HtmlListBuilder {
    private val items = mutableListOf<String>()
    operator fun String.unaryPlus() { items += this }
    fun build() = items.toList()
}

fun ul(init: HtmlListBuilder.() -> Unit): List<String> =
    HtmlListBuilder().apply(init).build()

val list = ul {
    +"First item"
    +"Second item"
    +"Third item"
}
// Именно так работает kotlinx.html!
```

**`invoke` для вложенных конфигураций:**

```kotlin
class RouteBuilder(val path: String) {
    operator fun invoke(init: RouteBuilder.() -> Unit) = apply(init)
    fun get(handler: () -> String) { /* регистрация */ }
}

// DSL с invoke:
routes {
    "/api/users" {       // вызывается invoke на RouteBuilder
        get { "users list" }
    }
}
```

**`get`/`set` для конфигурации словарём:**

```kotlin
class ConfigBuilder {
    private val props = mutableMapOf<String, Any>()
    operator fun get(key: String): Any? = props[key]
    operator fun set(key: String, value: Any) { props[key] = value }
}

val config = ConfigBuilder().apply {
    this["host"] = "localhost"
    this["port"] = 8080
}
```

**`rangeTo` и `contains` для валидационного DSL:**

```kotlin
infix fun Int.shouldBeIn(range: IntRange): Int {
    require(this in range) { "$this not in $range" }
    return this
}

val age = 25 shouldBeIn (0..150)
```

**Правила применения operator overloading в DSL:**
- Используйте только там, где семантика оператора очевидна без комментария
- `unaryPlus` уместен для «добавить элемент» (HTML, списки, коллекции)
- `invoke` уместен для «настроить блок» объекта по значению
- Не перегружайте арифметические операторы без явной математической семантики

<a id="q37"></a>

## Q37. В Kotlin 2.4 старое объявление `context(Logger)` не компилируется без специального флага. Как записать зависимость от `Logger` на актуальном синтаксисе?

Эти четыре имени стоит держать наготове на собеседовании — они покрывают разные домены, но построены на одних механизмах (lambda с receiver, extension-функции, операторы).

Старые context receivers с записью `context(Logger)` были экспериментальными и заменены context parameters. В Kotlin 2.4 context parameters уже стабильны: `context(logger: Logger) fun announce() { logger.log("ready") }` компилируется без `-Xcontext-parameters`. Новый параметр не становится неявным receiver-ом в теле функции, поэтому к его методам обращаются через имя `logger`. Старое объявление без имени на Kotlin 2.4 компилятор отклоняет.

Краткий обзор:

**Anko (устаревший, Android)** — DSL для Android UI:
```kotlin
// Anko Layouts (deprecated, заменён Jetpack Compose)
verticalLayout {
    val name = editText()
    button("Say Hello") {
        onClick { toast("Hello, ${name.text}!") }
    }
}
```
Anko был первым примером широко используемого Kotlin DSL. Сейчас заменён Jetpack Compose, который сам является DSL на базе composable-функций.

**Exposed — SQL DSL для Kotlin:**
```kotlin
// Table DSL
object Users : Table() {
    val id = long("id").autoIncrement()
    val name = varchar("name", 50)
    val email = varchar("email", 100).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

// Query DSL — composable, типобезопасный
val activeUsers = Users
    .select(Users.name)
    .where { Users.name like "%admin%" }
    .orderBy(Users.name)
    .limit(10)
    .map { it[Users.name] }

// Insert DSL
Users.insert {
    it[name] = "Alice"
    it[email] = "alice@example.com"
}
```

**Ktor — сервер и клиент DSL:**
```kotlin
// Сервер
embeddedServer(Netty, port = 8080) {
    install(ContentNegotiation) { json() }
    routing {
        get("/health") { call.respond("OK") }
        route("/api") {
            post("/users") { /* handler */ }
        }
    }
}.start(wait = true)

// HTTP Client DSL
val client = HttpClient(CIO) {
    install(ContentNegotiation) { json() }
    defaultRequest { header("X-Api-Key", apiKey) }
}
val response = client.get("https://api.example.com/users") {
    parameter("page", 1)
}
```

**kotlinx.html:**
```kotlin
val html = createHTML().html {
    body {
        h1 { +"Hello" }
        ul { repeat(3) { li { +"Item $it" } } }
    }
}
```

**Spring Security Kotlin DSL:**
```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http {
        authorizeHttpRequests {
            authorize("/public/**", permitAll)
            authorize(anyRequest, authenticated)
        }
        oauth2Login { }
        csrf { disable() }
    }
    return http.build()
}
```

<a id="q38"></a>

## Q38. Почему `val Config.token get() = UUID.randomUUID()` возвращает новое значение при каждом чтении?

Оба подхода создают сконфигурированный объект, но различаются по двум ключевым осям — вложенность конфигурации и совместимость с Java.

У extension-свойства нет собственного backing field. В примере `val Config.token get() = UUID.randomUUID()` getter вызывает генератор при каждом чтении, поэтому результат меняется. Само по себе отсутствие поля не означает обязательного пересчёта: getter может вернуть значение, сохранённое в `Config` или другом хранилище. Для стабильной настройки builder-а обычно используют его обычное свойство.

Из этих осей и складывается выбор:

| Критерий | DSL (lambda с receiver) | Builder паттерн (Java-стиль) |
|----------|-------------------------|------------------------------|
| Синтаксис | `config { host = "..." }` | `Config.builder().host("...").build()` |
| Вложенность | Отлично — нативные блоки | Плохо — цепочки flatMap/nested builders |
| Java-интероп | Плохо — неудобно из Java | Отлично — стандартный Java-паттерн |
| Валидация | В `build()` или `init`-блоке | В `build()` |
| IDE-поддержка | Отличная в Kotlin | Отличная везде |
| Иммутабельность | Через `val` в builder | Через `build()` возвращает immutable |
| Компиляция | Требует Kotlin | Работает из Java/Groovy/Scala |

**Когда выбрать DSL:**
- Проект только на Kotlin
- Нужна иерархическая/вложенная конфигурация (HTML, роутинг, тесты)
- Структура похожа на декларативное описание (CI/CD pipeline, HTML, UI)
- Основная аудитория — Kotlin-разработчики

**Когда выбрать Builder:**
- Библиотека используется из Java
- Линейная, плоская конфигурация без вложенности
- Нужна совместимость с Java frameworks (Jackson, Hibernate)

```kotlin
// DSL — выигрывает при вложенности
val pipeline = pipeline {
    stage("build") {
        step("./gradlew build")
        environment { set("JAVA_HOME", "/usr/lib/jvm/java-17") }
    }
}

// Builder — выигрывает при Java-интеропе
val config = ServerConfig.builder()
    .host("localhost")
    .port(8080)
    .build()
// Легко вызвать из Java: ServerConfig.builder().host("localhost").build()
```

**Гибридный подход** — Builder с поддержкой DSL:
```kotlin
class ServerConfig private constructor(val host: String, val port: Int) {
    class Builder {
        var host: String = "localhost"
        var port: Int = 8080
        fun build() = ServerConfig(host, port)
    }
    companion object {
        fun builder() = Builder()
        operator fun invoke(init: Builder.() -> Unit) =
            Builder().apply(init).build()
    }
}

// Java-стиль:
val c1 = ServerConfig.builder().apply { host = "prod" }.build()
// DSL-стиль:
val c2 = ServerConfig { host = "prod"; port = 443 }
```

<a id="q39"></a>

## Q39. Валидационный DSL создаёт новые scope для вложенных блоков и вызывается часто. Откуда берутся дополнительные объекты, если builder-функции не `inline`?

**Validation DSL** позволяет объявить правила валидации декларативно и собрать **все** ошибки за один проход. Это его главное отличие от fail-fast через исключения: пользователь видит сразу полный список проблем, а не первую попавшуюся. Ошибки накапливаются в списке внутри scope, а результатом становится `Valid` или `Invalid(errors)`.

Цена такой записи — объекты scope и их коллекции: каждый вход во вложенный блок создаёт новый scope со своим mutable-списком. Не-`inline` lambda может потребовать и объект функции, особенно при захвате внешних переменных. Для не захватывающей lambda компилятор может переиспользовать объект; точное число аллокаций нужно измерять на выбранной платформе и версии компилятора.

```kotlin
// Результат валидации
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}

// Builder правил
class ValidationScope<T>(val value: T) {
    private val errors = mutableListOf<String>()

    fun rule(message: String, predicate: T.() -> Boolean) {
        if (!value.predicate()) errors += message
    }

    infix fun String.ifBlank(message: String) {
        if ((value as? String)?.isBlank() == true) errors += message
    }

    fun build(): ValidationResult =
        if (errors.isEmpty()) ValidationResult.Valid
        else ValidationResult.Invalid(errors)
}

fun <T> validate(value: T, init: ValidationScope<T>.() -> Unit): ValidationResult =
    ValidationScope(value).apply(init).build()

// Использование
data class UserRegistration(val name: String, val email: String, val age: Int)

fun validateUser(user: UserRegistration): ValidationResult = validate(user) {
    rule("Name must not be blank") { name.isNotBlank() }
    rule("Email must contain @") { email.contains("@") }
    rule("Age must be 18+") { age >= 18 }
    rule("Age must be realistic") { age <= 150 }
}

val result = validateUser(UserRegistration("", "invalid", 15))
// Invalid(errors=["Name must not be blank", "Email must contain @", "Age must be 18+"])
```

**Расширенный DSL с полями:**

```kotlin
class ObjectValidationScope<T>(val obj: T) {
    private val errors = mutableListOf<String>()

    fun <F> field(name: String, selector: T.() -> F, init: FieldScope<F>.() -> Unit) {
        val fieldValue = obj.selector()
        val fieldScope = FieldScope(name, fieldValue)
        fieldScope.init()
        errors += fieldScope.errors
    }

    fun build() = errors.toList()
}

class FieldScope<F>(val name: String, val value: F) {
    val errors = mutableListOf<String>()

    fun notNull(message: String = "$name must not be null") {
        if (value == null) errors += message
    }
    fun matches(regex: Regex, message: String = "$name has invalid format") {
        if (value is String && !value.matches(regex)) errors += message
    }
}
```

**Интеграция с Spring Boot:**
```kotlin
@Component
class UserValidator : Validator {
    override fun validate(user: UserRegistration) = validate(user) {
        rule("Name required") { name.isNotBlank() }
        rule("Valid email required") { email.matches(EMAIL_REGEX) }
    }
}
```

<a id="q40"></a>

## Q40. Почему внутренний Kotlin DSL не может добавить синтаксис, которого нет в Kotlin?

Граница между двумя видами проходит по одному вопросу: разбирает синтаксис отдельный парсер или компилятор хост-языка.

**Внешний DSL** — самостоятельный язык с собственным синтаксисом и парсером (SQL, YAML, Dockerfile, Regex, CSS). Даёт полную свободу синтаксиса, но ценой отдельного инструментария.

**Внутренний DSL** (embedded DSL) — DSL, построенный средствами host-языка. В Kotlin его собирают из lambda с receiver, operator overloading, infix-функций и `@DslMarker`, а проверяет его обычный компилятор Kotlin.

**Сравнение подходов:**

| Аспект | Внешний DSL | Внутренний Kotlin DSL |
|--------|-------------|----------------------|
| Синтаксис | Полная свобода | Ограничен синтаксисом Kotlin |
| Типобезопасность | Только runtime | Compile-time проверки |
| IDE поддержка | Требует отдельного плагина | Бесплатно — вся Kotlin IDE |
| Разработка | Нужен парсер/лексер | Нет накладных расходов |
| Обучение | Новый синтаксис для пользователей | Kotlin-разработчик уже знает |
| Отладка | Сложно — отдельный runtime | Kotlin debugger работает |
| Расширяемость | Изменение парсера | Extension-функции |
| Non-Kotlin вызов | Как строка | Требует Kotlin JVM |

**Когда внешний DSL оправдан:**
- Пользователи не программисты (бизнес-аналитики, devops)
- Нужна машинная обработка/трансформация (парсинг YAML в CI/CD)
- Синтаксис принципиально не вписывается в Kotlin (Regex, SQL)
- Файлы конфигурации, которые редактирует не-разработчик

**Когда Kotlin внутренний DSL оправдан:**
- Все пользователи — Kotlin-разработчики
- Нужна типобезопасность и IDE поддержка
- DSL используется внутри Kotlin-кода (тесты, конфигурация, UI)
- Скорость разработки важнее гибкости синтаксиса

```kotlin
// Внутренний DSL — полная проверка компилятором
val query = query<User> {
    where { age greaterThan 18 }
    orderBy { name ascending true }
    limit(10)
}
// Ошибка типа поймается сразу — age не String, нельзя вызвать contains

// Внешний DSL — ошибка только в runtime
val sql = "SELECT * FROM users WHERE age > '18'"  // Сравнение числа со строкой
```

**Компромисс — мультиуровневый подход:** внешний DSL для файлов конфигурации + Kotlin DSL для программного API (пример: Gradle поддерживает и TOML-каталог зависимостей, и Kotlin DSL для `build.gradle.kts`).

## See also

- [Основы Kotlin](kotlin-interview.md) — extension-функции и лямбды как основа DSL
- [Коллекции в Kotlin](kotlin-collections-interview.md) — buildList/buildMap как пример DSL-паттерна
- [Корутины в Kotlin](kotlin-coroutines-interview.md) — корутины активно используют DSL-стиль (launch, async)
- [Kotlin/Java interop](kotlin-interop-java-interview.md) — как DSL выглядит при вызове из Java
- [Исключения в Kotlin](kotlin-exceptions-interview.md) — обработка ошибок в DSL-блоках
- [Сериализация в Kotlin](kotlin-serialization-interview.md) — JsonElement DSL как пример внутреннего DSL
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — Builder и Fluent API как альтернативы DSL
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — Spring Security DSL и Beans DSL на Kotlin
- [Java Core](../java/java-core-interview.md) — сравнение с Java Builder-паттерном

- [Kotlin Coroutines](kotlin-coroutines-interview.md)
- [исключения в Kotlin](kotlin-exceptions-interview.md)
- [интероп Kotlin и Java](kotlin-interop-java-interview.md)
- [Kotlin](kotlin-interview.md)
- [сериализация в Kotlin](kotlin-serialization-interview.md)
- [Шпаргалка: Kotlin DSL](../../../languages/kotlin/kotlin-dsl.md) — теория
