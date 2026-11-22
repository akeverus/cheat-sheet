---
title: "Вопросы на собеседовании: Kotlin коллекции"
description: "Полное покрытие коллекций в Kotlin: List, Set, Map, Sequence, мутабельность, builders, операции, windowed/chunked, сравнение с Java, производительность."
tags:
  - interview
  - programming-languages
  - kotlin-collections-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Kotlin коллекции"
  - "Kotlin collections interview"
  - "Kotlin коллекции собеседование"
prerequisites: []
next: []
updated: "2026-05-14"
---
# Вопросы на собеседовании: `Kotlin` коллекции

Полное покрытие коллекций в `Kotlin`: `List`, `Set`, `Map`, `Sequence`, мутабельность, builders, операции, `windowed`/`chunked`, сравнение с `Java`, производительность.

## Введение

В `Kotlin` коллекции разделены на **read-only** (интерфейсы `List`, `Set`, `Map`) и **mutable** (`MutableList`, `MutableSet`, `MutableMap`). Под капотом на `JVM` используются коллекции `Java`, но API и разделение по мутабельности — свои. На собеседованиях часто спрашивают про отличия от `Java`, `Sequence`, `buildList`/`buildMap`, оконные операции и типичные цепочки преобразований.

## Полезные ссылки

### Официальная документация

- [Kotlin Collections Overview](https://kotlinlang.org/docs/collections-overview.html) — иерархия типов, read-only vs mutable
- [Sequences](https://kotlinlang.org/docs/sequences.html) — ленивые последовательности
- [Constructing Collections](https://kotlinlang.org/docs/constructing-collections.html) — `buildList`, `buildMap`, фабрики
- [Collection Transformations](https://kotlinlang.org/docs/collection-transformations.html) — `map`, `flatMap`, `zip`, `associate`
- [Collection Parts](https://kotlinlang.org/docs/collection-parts.html) — `windowed`, `chunked`, `zipWithNext`
- [Grouping](https://kotlinlang.org/docs/collection-grouping.html) — `groupBy`, `groupingBy`
### Baeldung

- [Kotlin Collections Guide — Baeldung](https://www.baeldung.com/kotlin/collection-guide) — полное руководство по коллекциям Kotlin
- [Kotlin Collections API — Baeldung](https://www.baeldung.com/kotlin/collections-api) — обзор операций над коллекциями
- [Sequences in Kotlin — Baeldung](https://www.baeldung.com/kotlin/sequences) — ленивые последовательности и их применение
- [Difference Between Collection and Sequence in Kotlin — Baeldung](https://www.baeldung.com/kotlin/collection-vs-sequence) — когда использовать Collection, а когда Sequence

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы: типы, иерархия, создание**
- [Q1. (!) В чём разница между read-only и mutable коллекциями в Kotlin?](#q1--в-чём-разница-между-read-only-и-mutable-коллекциями-в-kotlin)
- [Q2. Наследует ли Map интерфейсы Collection и Iterable в Kotlin?](#q2-наследует-ли-map-интерфейсы-collection-и-iterable-в-kotlin)
- [Q3. Какие реализации создают mutableListOf, mutableSetOf и mutableMapOf на JVM?](#q3-какие-реализации-создают-mutablelistof-mutablesetof-и-mutablemapof-на-jvm)
- [Q4. Что возвращает emptyList() в Kotlin?](#q4-что-возвращает-emptylist-в-kotlin)
- [Q5. (!) Как buildList создаёт read-only список?](#q5--как-buildlist-создаёт-read-only-список)

**Mutable vs Immutable: глубокое погружение**
- [Q6. (!) Является ли read-only коллекция действительно неизменяемой?](#q6--является-ли-read-only-коллекция-действительно-неизменяемой)
- [Q7. Когда полезна persistent-коллекция из kotlinx.collections.immutable?](#q7-когда-полезна-persistent-коллекция-из-kotlinxcollectionsimmutable)

**Sequence vs Collection**
- [Q8. (!) Чем цепочка операций на Sequence отличается от цепочки на List?](#q8--чем-цепочка-операций-на-sequence-отличается-от-цепочки-на-list)
- [Q9. Чем создать потенциально бесконечную Sequence в Kotlin?](#q9-чем-создать-потенциально-бесконечную-sequence-в-kotlin)
- [Q10. (!) Когда использовать Sequence, а когда List (практический выбор)?](#q10--когда-использовать-sequence-а-когда-list-практический-выбор)

**Операции и преобразования**
- [Q11. Чем fold отличается от reduce на пустой коллекции?](#q11-чем-fold-отличается-от-reduce-на-пустой-коллекции)
- [Q12. Чем map отличается от flatMap при преобразовании элементов?](#q12-чем-map-отличается-от-flatmap-при-преобразовании-элементов)
- [Q13. (!) Что происходит с повторяющимися ключами в groupBy и associateBy?](#q13--что-происходит-с-повторяющимися-ключами-в-groupby-и-associateby)
- [Q14. Что делает eachCount после groupingBy по сравнению с groupBy?](#q14-что-делает-eachcount-после-groupingby-по-сравнению-с-groupby)
- [Q15. Чем zipWithNext отличается от chunked?](#q15-чем-zipwithnext-отличается-от-chunked)
- [Q16. Чем sorted/sortedBy отличаются от sort/sortBy?](#q16-чем-sortedsortedby-отличаются-от-sortsortby)
- [Q17. Какой контракт дают toList(), toSet() и toMutableList()?](#q17-какой-контракт-дают-tolist-toset-и-tomutablelist)
- [Q18. Как zip и unzip преобразуют пары элементов?](#q18-как-zip-и-unzip-преобразуют-пары-элементов)
- [Q19. Чем mapNotNull отличается от map { ... }.filterNotNull() для List?](#q19-чем-mapnotnull-отличается-от-map---filternotnull-для-list)

**Scope-функции и коллекции**
- [Q20. Что возвращает also в цепочке операций над коллекцией?](#q20-что-возвращает-also-в-цепочке-операций-над-коллекцией)

**Сравнение с Java**
- [Q21. (!) Как Kotlin/JVM связывает свои интерфейсы коллекций с Java Collections?](#q21--как-kotlinjvm-связывает-свои-интерфейсы-коллекций-с-java-collections)
- [Q22. Как учитывать nullability при получении Java-коллекции без аннотаций в Kotlin?](#q22-как-учитывать-nullability-при-получении-java-коллекции-без-аннотаций-в-kotlin)

**ArrayDeque и специализированные коллекции**
- [Q23. Какие операции на концах поддерживает Kotlin ArrayDeque?](#q23-какие-операции-на-концах-поддерживает-kotlin-arraydeque)

**Производительность и выбор**
- [Q24. (!) Как выбрать между List, Set и Map по способу доступа к данным?](#q24--как-выбрать-между-list-set-и-map-по-способу-доступа-к-данным)
- [Q25. (!) Как выбирать List, Sequence или Flow для результата API?](#q25--как-выбирать-list-sequence-или-flow-для-результата-api)
- [Q26. Чем scan/runningFold отличаются от runningReduce?](#q26-чем-scanrunningfold-отличаются-от-runningreduce)
- [Q27. Чем takeWhile отличается от filter при обходе коллекции?](#q27-чем-takewhile-отличается-от-filter-при-обходе-коллекции)

**Специализированные Map и Set**
- [Q28. Чем LinkedHashMap отличается от TreeMap по порядку и доступу?](#q28-чем-linkedhashmap-отличается-от-treemap-по-порядку-и-доступу)
- [Q29. Как различаются hashSetOf, linkedSetOf и sortedSetOf по порядку?](#q29-как-различаются-hashsetof-linkedsetof-и-sortedsetof-по-порядку)

**Производительность: Sequence на практике**
- [Q30. (!) Когда накладные расходы Sequence могут превысить выгоду?](#q30--когда-накладные-расходы-sequence-могут-превысить-выгоду)
- [Q31. Как any избегает промежуточного списка по сравнению с filter().isNotEmpty()?](#q31-как-any-избегает-промежуточного-списка-по-сравнению-с-filterisnotempty)

**Потокобезопасность**
- [Q32. (!) Гарантирует ли read-only тип Kotlin потокобезопасность коллекции?](#q32--гарантирует-ли-read-only-тип-kotlin-потокобезопасность-коллекции)

**Дополнительные операции**
- [Q33. Чем лямбда associate отличается от аргументов associateBy?](#q33-чем-лямбда-associate-отличается-от-аргументов-associateby)
- [Q34. Чем flatten отличается от flatMap?](#q34-чем-flatten-отличается-от-flatmap)
- [Q35. Чем minOrNull отличается от min на пустой коллекции?](#q35-чем-minornull-отличается-от-min-на-пустой-коллекции)
- [Q36. Чем singleOrNull отличается от single при пустой или многозначной коллекции?](#q36-чем-singleornull-отличается-от-single-при-пустой-или-многозначной-коллекции)

**Immutable-коллекции и продвинутые темы**
- [Q37. (!) Зачем persistent-коллекциям structural sharing при обновлении?](#q37--зачем-persistent-коллекциям-structural-sharing-при-обновлении)
- [Q38. (!) Чем отличаются Iterable, Collection и Sequence как контракты?](#q38--чем-отличаются-iterable-collection-и-sequence-как-контракты)
- [Q39. Как соотносятся sortedBy, sortedWith и compareBy?](#q39-как-соотносятся-sortedby-sortedwith-и-compareby)
- [Q40. (!) Чем `groupingBy` отличается от `groupBy` — ленивый vs eager grouping?](#q40--чем-groupingby-отличается-от-groupby--ленивый-vs-eager-grouping)
- [Q41. Чем chunked и windowed(size) различаются при шаге по умолчанию?](#q41-чем-chunked-и-windowedsize-различаются-при-шаге-по-умолчанию)
- [Q42. Что задают основной селектор associateBy, лямбда associateWith и лямбда associate?](#q42-что-задают-основной-селектор-associateby-лямбда-associatewith-и-лямбда-associate)
- [Q43. Чем scan отличается от fold по результату?](#q43-чем-scan-отличается-от-fold-по-результату)

---

## Q1. (!) В чём разница между read-only и mutable коллекциями в Kotlin?

Главное отличие — на уровне **системы типов**, а не реализации: read-only интерфейс просто не объявляет методы изменения, поэтому компилятор запрещает их вызвать. Сам объект под капотом может быть изменяемым.

**Read-only** интерфейсы (`List`, `Set`, `Map`) дают только чтение: доступ по индексу/ключу, итерацию, `size`, `contains`. Методов `add`, `remove`, `clear` у них нет — через read-only тип их не вызвать.

**Mutable** интерфейсы (`MutableList`, `MutableSet`, `MutableMap`) наследуют read-only собратьев и добавляют методы модификации. На JVM обе ссылки могут указывать на один объект; видимый интерфейс не требует копирования коллекции.

Из этого следует важный практический вывод: переменная типа `List` может указывать на `MutableList` (upcasting), но изменить коллекцию через ссылку типа `List` нельзя. Это контракт компилятора, а не защита в рантайме (см. Q6).

```kotlin
val readOnly: List<String> = listOf("a", "b")
// readOnly.add("c")  // ошибка компиляции: у List нет add

val mutable: MutableList<String> = mutableListOf("a", "b")
mutable.add("c")  // OK

// upcasting — безопасно для потребителя
fun process(items: List<Int>) { /* только чтение */ }
process(mutableListOf(1, 2, 3))
```

> **Что хотят услышать на собеседовании**: read-only != immutable. Это ограничение на этапе компиляции, а не гарантия неизменяемости в рантайме (см. Q6).

## Q2. Наследует ли Map интерфейсы Collection и Iterable в Kotlin?

Для `List` и `Set` есть read-only и mutable интерфейсы: каждый mutable-интерфейс наследует соответствующий read-only вариант. `Collection<T>` наследует `Iterable<T>`. `Map<K, V>` образует отдельную ветку и не наследует ни `Collection`, ни `Iterable`.

```mermaid
graph TD
    Iterable["Iterable&lt;T&gt;"]
    Collection["Collection&lt;T&gt;"]
    List["List&lt;T&gt;"]
    Set["Set&lt;T&gt;"]
    MutableIterable["MutableIterable&lt;T&gt;"]
    MutableCollection["MutableCollection&lt;T&gt;"]
    MutableList["MutableList&lt;T&gt;"]
    MutableSet["MutableSet&lt;T&gt;"]
    Map["Map&lt;K, V&gt;"]
    MutableMap["MutableMap&lt;K, V&gt;"]

    Iterable --> Collection
    Collection --> List
    Collection --> Set
    MutableIterable --> MutableCollection
    MutableCollection --> MutableList
    MutableCollection --> MutableSet
    Map --> MutableMap

    Iterable -.-> MutableIterable
    Collection -.-> MutableCollection
    List -.-> MutableList
    Set -.-> MutableSet
```

Что важно понимать в этой схеме:
- `Collection<T>` наследует `Iterable<T>`, поэтому любую коллекцию можно обойти через `for`.
- `Map` **не** наследует `Collection` (как и в `Java`) — это отдельная ветка пар ключ-значение.
- Read-only интерфейсы **ковариантны** (`out T`): `List<String>` можно присвоить переменной `List<Any>`. Это безопасно, потому что из read-only списка элементы только читаются.
- Mutable интерфейсы **инвариантны**: `MutableList<String>` присвоить в `MutableList<Any>` нельзя. Иначе через ссылку `MutableList<Any>` можно было бы записать туда `Int`, сломав исходный список строк, — инвариантность это предотвращает.

## Q3. Какие реализации создают mutableListOf, mutableSetOf и mutableMapOf на JVM?

| Фабрика на JVM | Обычная реализация | Свойство |
|---------------|--------------------|----------|
| `mutableListOf()` | `ArrayList` | индексированный список с дубликатами |
| `mutableSetOf()` | `LinkedHashSet` | уникальные элементы, порядок вставки |
| `mutableMapOf()` | `LinkedHashMap` | уникальные ключи, порядок вставки |

Это реализации обычных mutable-фабрик на JVM, а не обещание для любого объекта с интерфейсом `List`, `Set` или `Map`. Read-only фабрики и пустые коллекции могут использовать другие оптимизированные реализации. [Документация Kotlin](https://kotlinlang.org/docs/collection-types.html) отдельно называет эти реализации для `MutableList`, `MutableSet` и `MutableMap`.

## Q4. Что возвращает emptyList() в Kotlin?

`emptyList()` возвращает пустой read-only список; на JVM пустой экземпляр переиспользуется. Для списка с возможностью добавления элементов используйте `mutableListOf()`.

```kotlin
// Read-only
val list = listOf(1, 2, 3)           // List<Int>
val set = setOf("x", "y")            // Set<String>
val map = mapOf("a" to 1, "b" to 2)  // Map<String, Int>

// Пустые (синглтоны — не создают новый объект)
val empty = emptyList<String>()
val emptyS = emptySet<Int>()
val emptyM = emptyMap<String, Int>()

// Mutable
val mList = mutableListOf<Int>()        // MutableList<Int> (ArrayList)
val mSet = mutableSetOf<String>()       // MutableSet<String> (LinkedHashSet)
val mMap = mutableMapOf<String, Int>()  // MutableMap<String, Int> (LinkedHashMap)

// Специализированные
val hashSet = hashSetOf(1, 2, 3)           // HashSet — без гарантии порядка
val linkedSet = linkedSetOf(1, 2, 3)       // LinkedHashSet — порядок вставки
val sortedSet = sortedSetOf(3, 1, 2)       // TreeSet — натуральный порядок
val hashMap = hashMapOf("a" to 1)          // HashMap
val linkedMap = linkedMapOf("a" to 1)      // LinkedHashMap
val sortedMap = sortedMapOf("b" to 2, "a" to 1)  // TreeMap
```

`listOf()` может использовать оптимизированные реализации для пустого и малого результата. Не выводите конкретный класс объекта из интерфейса `List`.

## Q5. (!) Как buildList создаёт read-only список?

`buildList` / `buildSet` / `buildMap` строят **read-only** коллекцию, давая внутри lambda-блока временный **mutable**-builder. Внутри блока коллекция изменяема (`add`, `put`, условная логика), а на выходе фиксируется как read-only — без отдельного вызова `toList()`.

```kotlin
val list = buildList {
    // this: MutableList<String>
    add("alpha")
    add("beta")
    if (condition) add("gamma")
    addAll(otherList)
}
// list: List<String> — read-only

val map = buildMap {
    // this: MutableMap<String, Int>
    put("a", 1)
    put("b", 2)
    putAll(existingMap)
}
// map: Map<String, Int> — read-only

val set = buildSet {
    add(1)
    addAll(listOf(2, 3))
}
```

**Преимущества** перед `listOf()` / `mutableListOf()`:
- Результат сразу **read-only** — не нужно вызывать `toList()`
- **Условная логика** внутри блока — удобнее, чем тернарные конструкции
- Для `buildMap` — нет создания промежуточных `Pair`-объектов (в отличие от `mapOf(key to value)`)
- **Типы выводятся** автоматически из операций в блоке

> **На собеседовании**: `buildList` удобен, когда содержимое списка зависит от условий внутри блока.

## Q6. (!) Является ли read-only коллекция действительно неизменяемой?

**Не обязательно.** Read-only в Kotlin ограничивает операции через интерфейс, но не гарантирует неизменяемость объекта. Если тот же объект доступен через изменяемую ссылку, его содержимое можно поменять. Приведение к `MutableList` не превращает действительно неизменяемую реализацию в изменяемую и может завершиться ошибкой.

```kotlin
val readOnly: List<String> = mutableListOf("a", "b")

// через unsafe cast — runtime позволит
(readOnly as MutableList).add("c")
println(readOnly) // [a, b, c]

// из Java-кода — тоже можно изменить
// void modify(List<String> list) { list.add("oops"); }
```

Библиотека `kotlinx.collections.immutable` даёт persistent-коллекции: операция изменения возвращает новую версию, сохраняя прежнюю. Java-обёртка `Collections.unmodifiableList` запрещает запись через себя, но может отражать изменения исходной коллекции.

Конкретную реализацию `listOf()`, `setOf()` и `mapOf()` нельзя выводить только из read-only интерфейса: она может отличаться по размеру и платформе. Если нужна независимая копия или гарантия неизменяемости, выбирайте отдельный контракт и проверяйте его свойства.

> **Подводный камень**: read-only не защищает от конкурентной модификации того же объекта. Для общего изменяемого состояния нужна синхронизация или подходящая concurrent-коллекция; persistent-снимки требуют безопасной публикации.

## Q7. Когда полезна persistent-коллекция из kotlinx.collections.immutable?

Берите её, когда нужно хранить неизменяемые версии коллекции с эффективными обновлениями. Библиотека даёт **persistent-коллекции** (`PersistentList`, `PersistentMap`, `PersistentSet`) с функциональной моделью обновлений: каждая модификация возвращает новую коллекцию, но благодаря **structural sharing** не копирует всё содержимое целиком, а переиспользует неизменённые части — поэтому обновление дешёвое.

```kotlin
// Для этого примера нужна зависимость kotlinx-collections-immutable
val list = persistentListOf(1, 2, 3)
val updated = list.add(4)  // новый PersistentList, list не изменился

val map = persistentHashMapOf("a" to 1)
val updated2 = map.put("b", 2)
```

**Когда использовать:**
- **State management** (UI, Redux-подобные архитектуры) — безопасное хранение версий состояния
- **Многопоточность** — гарантия неизменяемости без блокировок
- **Event sourcing** — снимки состояния

**Когда НЕ нужно:**
- Локальные короткоживущие коллекции — обычные `MutableList` проще и быстрее
- Маленькие коллекции — overhead структурного шаринга не окупается

## Q8. (!) Чем цепочка операций на Sequence отличается от цепочки на List?

`Sequence<T>` обрабатывает цепочку промежуточных операций лениво. Операции `filter` и `map` над `List` выполняются сразу и дают отдельные результаты; над `Sequence` они откладывают обработку до терминальной операции (`toList()`, `first()`, `count()`). При этом сама цепочка `Sequence` создаёт служебные объекты, хотя промежуточные списки для этих операций не нужны.

```mermaid
graph LR
    subgraph "List (eager)"
        L1["[1,2,3,4,5]"] -->|"filter"| L2["[2,4]"]
        L2 -->|"map"| L3["[4,8]"]
    end

    subgraph "Sequence (lazy)"
        S1["1→filter→skip"] --> S2["2→filter→pass→map→4"]
        S2 --> S3["3→filter→skip"]
        S3 --> S4["4→filter→pass→map→8"]
        S4 --> S5["5→filter→skip"]
    end
```

| Аспект | `List` (eager) | `Sequence` (lazy) |
|--------|---------------|-------------------|
| Порядок выполнения | Вся коллекция на каждом шаге | Элемент проходит всю цепочку |
| Промежуточные коллекции | Создаются на каждом шаге | Не создаются |
| Ранняя остановка | Нет | Да (`first()`, `take()`) |
| Overhead | Минимальный | Итераторы, boxing |

```kotlin
// List: 2 промежуточных списка
listOf(1, 2, 3, 4, 5)
    .filter { it % 2 == 0 }  // → [2, 4]
    .map { it * 2 }           // → [4, 8]

// Sequence: 0 промежуточных списков
listOf(1, 2, 3, 4, 5).asSequence()
    .filter { it % 2 == 0 }
    .map { it * 2 }
    .toList()  // терминальная операция запускает pipeline
```

## Q9. Чем создать потенциально бесконечную Sequence в Kotlin?

Есть пять основных способов: из готовых элементов (`sequenceOf`), из существующей коллекции (`asSequence`), генератором (`generateSequence`), через builder с `yield` (`sequence { }`) и из итератора/Java-stream. Для бесконечных последовательностей подходят именно генератор и builder — они вычисляют элементы по требованию.

```kotlin
// 1. Из элементов
val s1 = sequenceOf("a", "b", "c")

// 2. Из коллекции
val s2 = listOf(1, 2, 3).asSequence()

// 3. generateSequence — с функцией-генератором
val naturals = generateSequence(1) { it + 1 }       // бесконечная
val finite = generateSequence(1) { if (it < 10) it + 1 else null }  // до null

// 4. sequence builder — с yield/yieldAll (suspend)
val fibonacci = sequence {
    var a = 0
    var b = 1
    while (true) {
        yield(a)
        val next = a + b
        a = b
        b = next
    }
}
println(fibonacci.take(8).toList()) // [0, 1, 1, 2, 3, 5, 8, 13]

// 5. Из итераторов или Java streams
iterator.asSequence()
stream.asSequence()
```

`yield()` возвращает одиночный элемент, `yieldAll()` — коллекцию, другую последовательность или итератор. Выполнение suspend-ируется между вызовами `yield` — это restricted coroutine (не полноценная корутина, а только `SequenceScope`).

## Q10. (!) Когда использовать Sequence, а когда List (практический выбор)?

**Используйте `Sequence`, когда:**
- Коллекция большая и цепочка содержит несколько промежуточных операций; выигрыш зависит от данных и операций
- Нужен только **частичный результат** (`first()`, `take(n)`, `any()`) — lazy evaluation остановится раньше
- Источник данных **потоковый** (файл, сеть, бесконечная генерация)

**Используйте `List`, когда:**
- Данных **мало** и операция простая — overhead `Sequence` не окупится
- Нужен **многократный доступ по индексу** или random access
- Результат нужен целиком и используется **несколько раз**
- Нужна **одна** операция без цепочки — промежуточных коллекций всего одна

**Правило большого пальца**: начинайте с коллекций; добавляйте `.asSequence()` когда профилирование показывает лишние аллокации или когда цепочка длинная на большом наборе данных.

```kotlin
// ✅ Sequence: большие данные, длинная цепочка, нужен один результат
lines.asSequence()
    .filter { it.isNotBlank() }
    .map { it.trim().lowercase() }
    .distinct()
    .sorted()
    .toList()

// ✅ List: маленькая коллекция, простая операция
listOf(1, 2, 3).map { it * 2 }
```

## Q11. Чем fold отличается от reduce на пустой коллекции?

`fold` принимает начальное значение и возвращает его на пустом входе. `reduce` начинает накопление с первого элемента, поэтому на пустой коллекции бросает исключение.

| Операция | Описание | Пример |
|----------|---------|--------|
| `map` | Преобразование каждого элемента | `list.map { it * 2 }` |
| `filter` | Оставить элементы по предикату | `list.filter { it > 0 }` |
| `fold` | Свёртка с начальным значением | `list.fold(0) { acc, e -> acc + e }` |
| `reduce` | Свёртка, начиная с первого элемента | `list.reduce { acc, e -> acc + e }` |
| `flatMap` | map + flatten | `list.flatMap { it.children }` |
| `find` / `firstOrNull` | Первый по предикату или null | `list.find { it > 5 }` |
| `any` / `all` / `none` | Проверка по предикату | `list.any { it < 0 }` |
| `count` | Количество (с предикатом или без) | `list.count { it > 0 }` |
| `partition` | Разбиение на два списка | `list.partition { it % 2 == 0 }` |
| `zip` | Объединение двух коллекций в пары | `a.zip(b)` |
| `flatten` | Список списков → один список | `listOfLists.flatten()` |
| `take` / `drop` | Взять / пропустить N элементов | `list.take(3)` |
| `distinct` | Убрать дубликаты | `list.distinct()` |
| `sumOf` | Сумма по селектору | `list.sumOf { it.price }` |

```kotlin
val numbers = listOf(1, -2, 3, -4, 5)

// partition: разделить на положительные и отрицательные
val (positive, negative) = numbers.partition { it > 0 }
// positive = [1, 3, 5], negative = [-2, -4]

// fold: суммирование с начальным значением
val sum = numbers.fold(100) { acc, n -> acc + n } // 103

// reduce vs fold: reduce бросает исключение на пустой коллекции!
val product = numbers.reduce { acc, n -> acc * n } // -120
```

**`fold` vs `reduce`** — частый вопрос. `fold` принимает явное начальное значение и поэтому безопасен на пустой коллекции (вернёт его). `reduce` стартует с первого элемента и на пустой коллекции бросает `UnsupportedOperationException`. Берите `fold`, когда коллекция может оказаться пустой или когда тип аккумулятора отличается от типа элементов.

## Q12. Чем map отличается от flatMap при преобразовании элементов?

**`map`** преобразует каждый элемент в **одно** значение — размер результата совпадает с исходным. **`flatMap`** преобразует каждый элемент в **коллекцию** и затем «склеивает» все результаты в одну плоскую последовательность. Эквивалент: `map { ... }.flatten()`.

```kotlin
val sentences = listOf("hello world", "kotlin collections")

// map → List<List<String>>
sentences.map { it.split(" ") }
// [["hello", "world"], ["kotlin", "collections"]]

// flatMap → List<String>
sentences.flatMap { it.split(" ") }
// ["hello", "world", "kotlin", "collections"]

// Практический пример: получить все заказы всех клиентов
data class Customer(val name: String, val orders: List<Order>)
val allOrders: List<Order> = customers.flatMap { it.orders }
```

**Когда использовать `flatMap`:**
- Один элемент «разворачивается» в несколько (one-to-many)
- Нужно объединить вложенные структуры в плоскую
- Работа с `Optional`/nullable через `listOfNotNull`

## Q13. (!) Что происходит с повторяющимися ключами в groupBy и associateBy?

Все четыре функции преобразуют коллекцию в структурированный результат, но с разной семантикой:

| Функция | Результат | Дубликаты ключей | Использование |
|---------|----------|-------------------|---------------|
| `groupBy` | `Map<K, List<T>>` | Все элементы сохраняются | Группировка |
| `associateBy` | `Map<K, T>` | Последний перезаписывает | Индексация по уникальному ключу |
| `associateWith` | `Map<T, V>` | Последний перезаписывает | Элемент → вычисленное значение |
| `associate` | `Map<K, V>` | Последний перезаписывает | Произвольная пара |
| `partition` | `Pair<List<T>, List<T>>` | N/A | Разделение на два списка |

```kotlin
data class User(val id: Int, val name: String, val dept: String)
val users = listOf(
    User(1, "Alice", "dev"), User(2, "Bob", "dev"),
    User(3, "Carol", "qa"), User(4, "Dave", "qa")
)

// groupBy: dept → список пользователей
users.groupBy { it.dept }
// {dev=[Alice, Bob], qa=[Carol, Dave]}

// associateBy: id → пользователь (уникальный ключ!)
users.associateBy { it.id }
// {1=Alice, 2=Bob, 3=Carol, 4=Dave}

// associateWith: пользователь → вычисленное значение
users.associateWith { it.name.length }
// {User(1,Alice,dev)=5, ...}

// partition: предикат → (true-список, false-список)
val (devs, others) = users.partition { it.dept == "dev" }
// devs=[Alice, Bob], others=[Carol, Dave]
```

> **Подводный камень**: при дубликатах ключей `associateBy`/`associateWith`/`associate` молча оставляют только последний элемент. Если ключи могут повторяться и нужны все значения — берите `groupBy`.

## Q14. Что делает eachCount после groupingBy по сравнению с groupBy?

Короткий ответ: `groupBy` сразу собирает `Map<K, List<T>>` — все списки лежат в памяти. `groupingBy` ничего не материализует, а возвращает промежуточный объект `Grouping`, к которому потом применяют **агрегацию за один проход, без построения списков**. Это выгодно, когда нужны не сами группы, а агрегат по ним (число, сумма, максимум).

```kotlin
val words = listOf("apple", "banana", "avocado", "blueberry", "cherry")

// groupBy + mapValues — два прохода, промежуточные списки
words.groupBy { it.first() }.mapValues { (_, v) -> v.size }
// {a=2, b=2, c=1}

// groupingBy + eachCount — один проход, без промежуточных списков
words.groupingBy { it.first() }.eachCount()
// {a=2, b=2, c=1}
```

| Операция `Grouping` | Описание |
|---------------------|---------|
| `eachCount()` | Количество элементов в каждой группе |
| `fold(init) { ... }` | Свёртка каждой группы |
| `reduce { ... }` | Редукция каждой группы |
| `aggregate { ... }` | Произвольная агрегация |

`groupingBy` эффективнее для больших коллекций, когда нужен агрегат, а не сами списки элементов.

## Q15. Чем zipWithNext отличается от chunked?

Три функции для работы с «окнами» элементов:

```mermaid
graph LR
    subgraph "chunked(3)"
        C1["[1,2,3]"] --> C2["[4,5,6]"] --> C3["[7,8]"]
    end

    subgraph "windowed(3, step=1)"
        W1["[1,2,3]"] --> W2["[2,3,4]"] --> W3["[3,4,5]"]
    end

    subgraph "zipWithNext"
        Z1["(1,2)"] --> Z2["(2,3)"] --> Z3["(3,4)"]
    end
```

```kotlin
val numbers = (1..8).toList()

// chunked — разбить на части фиксированного размера
numbers.chunked(3)
// [[1, 2, 3], [4, 5, 6], [7, 8]]

// chunked с трансформацией
numbers.chunked(3) { it.sum() }
// [6, 15, 15]

// windowed — скользящее окно
numbers.windowed(3)
// [[1,2,3], [2,3,4], [3,4,5], [4,5,6], [5,6,7], [6,7,8]]

// windowed с шагом и partial windows
numbers.windowed(size = 3, step = 2, partialWindows = true)
// [[1,2,3], [3,4,5], [5,6,7], [7,8]]

// zipWithNext — пары соседних элементов
numbers.zipWithNext()
// [(1,2), (2,3), (3,4), (4,5), (5,6), (6,7), (7,8)]

// Практика: проверить, что последовательность возрастающая
numbers.zipWithNext { a, b -> a < b }.all { it } // true
```

**Применение:**
- `chunked` — пакетная обработка (batch insert в БД, пагинация)
- `windowed` — скользящее среднее, поиск паттернов
- `zipWithNext` — проверка переходов, вычисление разностей

## Q16. Чем sorted/sortedBy отличаются от sort/sortBy?

Ключевое различие двух семейств — что они возвращают. Функции `sorted*` (для любого `Iterable`) не трогают исходник и **возвращают новый список**; функции `sort*` работают только на `MutableList`, сортируют **на месте** и возвращают `Unit`. Внутри каждого семейства выбор — по натуральному порядку, по селектору (`...By`) или по компаратору (`...With`).

| Функция | Описание | Тип |
|---------|---------|-----|
| `sorted()` | По натуральному порядку (`Comparable`) | Новый список |
| `sortedBy { }` | По результату селектора | Новый список |
| `sortedDescending()` | По убыванию | Новый список |
| `sortedByDescending { }` | По убыванию селектора | Новый список |
| `sortedWith(comparator)` | По кастомному компаратору | Новый список |
| `sort()` | In-place сортировка | `Unit` (MutableList) |
| `sortBy { }` | In-place по селектору | `Unit` (MutableList) |

```kotlin
data class User(val name: String, val age: Int)
val users = listOf(User("Bob", 30), User("Alice", 25), User("Carol", 30))

// Сортировка по одному полю
users.sortedBy { it.name }  // [Alice, Bob, Carol]

// Множественная сортировка: сначала по возрасту, потом по имени
users.sortedWith(compareBy({ it.age }, { it.name }))
// [Alice(25), Bob(30), Carol(30)]

// In-place (только MutableList)
val mutable = mutableListOf(3, 1, 2)
mutable.sort()  // изменяет mutable, возвращает Unit
```

> **Важно**: `sorted*` возвращают отсортированный `List` без изменения исходника; не проверяйте их по идентичности ссылок. `sort*` изменяют `MutableList` на месте и возвращают `Unit`.

## Q17. Какой контракт дают toList(), toSet() и toMutableList()?

Функции `to*()` возвращают коллекцию с нужным интерфейсом и содержимым. Это поверхностное преобразование: вложенные изменяемые элементы остаются общими. Для пустых и одноэлементных результатов реализация может переиспользовать объект; обещать новую ссылку для каждого вызова нельзя.

```kotlin
val sequence = (1..5).asSequence().filter { it % 2 == 0 }.map { it * 10 }

// Терминальная операция для Sequence
val list = sequence.toList()       // [20, 40] — read-only List
val set = sequence.toSet()         // {20, 40} — read-only Set
val mList = sequence.toMutableList() // [20, 40] — MutableList

// Создание независимой копии
val original = mutableListOf(1, 2, 3)
val copy = original.toList()
original.add(4)
println(copy) // [1, 2, 3] — не затронут
```

**Когда что использовать:**
- `toList()` — зафиксировать результат pipeline, получить read-only контракт
- `toSet()` — убрать дубликаты + быстрый `contains()`
- `toMutableList()` — нужна изменяемая копия для дальнейшей модификации
- `toSortedSet()` — уникальные элементы + сортировка
- `toMap()` — из списка пар в `Map`

## Q18. Как zip и unzip преобразуют пары элементов?

**`zip`** объединяет два списка поэлементно в пары. Результат по длине равен **меньшему** из списков:

```kotlin
val colors = listOf("red", "brown", "grey")
val animals = listOf("fox", "bear", "wolf", "cat")

// Инфиксная форма
colors zip animals
// [(red, fox), (brown, bear), (grey, wolf)] — "cat" отбрасывается

// С трансформацией
colors.zip(animals) { color, animal -> "$color $animal" }
// ["red fox", "brown bear", "grey wolf"]

// unzip — обратная операция
val pairs = listOf("one" to 1, "two" to 2, "three" to 3)
val (strings, ints) = pairs.unzip()
// strings = [one, two, three], ints = [1, 2, 3]
```

## Q19. Чем mapNotNull отличается от map { ... }.filterNotNull() для List?

```kotlin
// mapNotNull — map + filterNotNull в одной операции
val strings = listOf("1", "abc", "2", "def")
strings.mapNotNull { it.toIntOrNull() }
// [1, 2] — только успешно распарсенные

// mapIndexed — доступ к индексу
listOf("a", "b", "c").mapIndexed { index, value -> "$index:$value" }
// ["0:a", "1:b", "2:c"]

// mapKeys / mapValues — трансформация ключей или значений Map
val map = mapOf("one" to 1, "two" to 2)
map.mapKeys { (key, _) -> key.uppercase() }    // {ONE=1, TWO=2}
map.mapValues { (_, value) -> value * 10 }       // {one=10, two=20}

// mapIndexedNotNull — mapIndexed + filterNotNull
listOf("a", null, "c").mapIndexedNotNull { i, v -> v?.let { "$i:$it" } }
// ["0:a", "2:c"]
```

> **Рекомендация**: `mapNotNull` эффективнее связки `map { ... }.filterNotNull()` — один проход вместо двух и без промежуточного списка (для `List`).

## Q20. Что возвращает also в цепочке операций над коллекцией?

Scope-функции не специфичны для коллекций, но часто используются в цепочках:

```kotlin
// let — преобразовать результат цепочки
users.filter { it.isActive }
    .map { it.email }
    .let { emails -> sendNotification(emails) }

// also — side-effect без прерывания цепочки (логирование, отладка)
users.filter { it.age > 18 }
    .also { println("Найдено ${it.size} совершеннолетних") }
    .map { it.name }

// apply — настройка mutable-коллекции
val result = mutableListOf<String>().apply {
    add("first")
    addAll(otherList)
    sort()
}

// takeIf / takeUnless — условный возврат
list.takeIf { it.isNotEmpty() }?.first()
    ?: defaultValue
```

**Типичные паттерны:**
- `let` — передать результат в функцию
- `also` — логирование промежуточных результатов
- `apply` — инициализация mutable-коллекции
- `takeIf` — заменяет `if (list.isNotEmpty()) list.first() else null`

## Q21. (!) Как Kotlin/JVM связывает свои интерфейсы коллекций с Java Collections?

На JVM интерфейсы коллекций Kotlin сопоставляются с Java-коллекциями без обязательного копирования объекта. Kotlin различает read-only и mutable интерфейсы и добавляет extension-функции; Java видит Java API конкретного объекта. Видимый тип Kotlin не обещает, что запись из Java пройдёт для любой реализации.

| Аспект | Kotlin | Java |
|--------|--------|------|
| Мутабельность | Отдельные read-only и mutable интерфейсы | Один набор интерфейсов; существуют и неизменяемые реализации (`List.of`) |
| Ковариантность | `List<out T>` — ковариантна | `List<T>` — инвариантна |
| Null-safety | `List<String>` vs `List<String?>` | Нет разделения на уровне типов |
| Extension-функции | `map`, `filter`, `flatMap` встроены | Нужен `Stream API` (Java 8+) |
| Фабрики | `listOf`, `buildList`, `emptyList` | `List.of()` (Java 9+), `Arrays.asList()` |
| Порядок | Конкретные фабрики могут сохранять вставку | Зависит от реализации (`HashSet`, `LinkedHashSet`) |
| Операторы | `+`, `-`, `in` для коллекций | Нет |
| `Map` access | `map[key]`, `map[key] = value` | `map.get(key)`, `map.put(key, value)` |

```kotlin
// Kotlin — лаконичный синтаксис
val map = mapOf("a" to 1, "b" to 2)
if ("a" in map) println(map["a"])

val combined = listOf(1, 2) + listOf(3, 4) // [1, 2, 3, 4]
val without = listOf(1, 2, 3) - 2          // [1, 3]
```

Подробнее о совместимости — в [вопросах по Kotlin/Java Interop](kotlin-interop-java-interview.md).

## Q22. Как учитывать nullability при получении Java-коллекции без аннотаций в Kotlin?

Без аннотаций nullability Java API может дать Kotlin платформенный тип с неизвестной допускаемостью `null` для самой коллекции и её элементов. Компилятор допускает использование как nullable и non-null, но выбранное non-null представление само по себе не проверяет каждый элемент. Проверяйте данные и явно задавайте тип на границе.

```kotlin
// Java: List<String> getNames() — без аннотаций
val names = javaObject.names  // тип: (Mutable)List<String!>!

// Безопасный подход: явно объявить тип
val rawNames: List<String?> = javaObject.names  // допускаем null-элементы
val safeNames: List<String> = rawNames.filterNotNull()  // явная политика обработки null
val nullableNames: List<String?> = javaObject.names  // безопасно

// Конвертация
val kotlinList: List<String> = javaList.toList()           // read-only копия
val kotlinMutable: MutableList<String> = javaList.toMutableList()  // mutable копия

// Передача в Java — без конвертации (те же классы на JVM)
javaMethod(kotlinList)  // OK
```

**Правила безопасности:**
- Явно задавайте тип и политику обработки null на ненадёжной Java-границе
- `toList()` копирует содержимое на уровне коллекции, но не проверяет и не копирует глубоко её элементы
- Без аннотаций в `Java` — считайте элементы nullable (`List<String?>`)

Подробнее — в [вопросах по Kotlin/Java Interop](kotlin-interop-java-interview.md) и [Java Collections](../java/java-collections-interview.md).

## Q23. Какие операции на концах поддерживает Kotlin ArrayDeque?

`ArrayDeque` — двусторонняя очередь (deque): добавление и удаление с обоих концов за амортизированное O(1). Одна структура закрывает и стек (LIFO), и очередь (FIFO) — не нужно тащить `Stack` или `LinkedList`. Доступна в стандартной библиотеке Kotlin:

```kotlin
val deque = ArrayDeque(listOf(1, 2, 3))

// Стек (LIFO)
deque.addLast(4)       // push
deque.removeLast()     // pop → 4

// Очередь (FIFO)
deque.addLast(4)       // enqueue
deque.removeFirst()    // dequeue → 1

// Двусторонняя
deque.addFirst(0)      // [0, 2, 3, 4]
deque.addLast(5)       // [0, 2, 3, 4, 5]

println(deque.first()) // 0
println(deque.last())  // 5
```

**Отличия от `java.util.ArrayDeque`:**
- Kotlin-версия — multiplatform (работает на JS, Native)
- `ArrayDeque` удобен для частых операций на обоих концах; вставка в начало `ArrayList` требует сдвига элементов

## Q24. (!) Как выбрать между List, Set и Map по способу доступа к данным?

Выбор сводится к двум вопросам: нужен ли доступ по ключу (→ `Map`), нужна ли уникальность (→ `Set`), иначе `List`. Дальше — нужен ли порядок (hash против tree/linked). Сложность ключевых операций задаёт ответ:

| Операция | `ArrayList` | `LinkedList` | `HashSet` | `TreeSet` | `HashMap` | `TreeMap` |
|----------|-------------|-------------|-----------|-----------|-----------|-----------|
| Доступ по индексу | O(1) | O(n) | — | — | — | — |
| `add` (в конец) | O(1)* | O(1) | O(1)* | O(log n) | — | — |
| `add` (в начало) | O(n) | O(1) | — | — | — | — |
| `contains` | O(n) | O(n) | O(1)* | O(log n) | — | — |
| `get` / `put` | — | — | — | — | O(1)* | O(log n) |
| `remove` | O(n) | O(1)** | O(1)* | O(log n) | O(1)* | O(log n) |

\* — амортизированная сложность  
\** — O(1) если есть итератор, O(n) по значению

**Практические правила выбора:**

```mermaid
graph TD
    A["Нужен ли доступ по ключу?"] -->|Да| B["Map"]
    A -->|Нет| C["Нужна ли уникальность?"]
    C -->|Да| D["Set"]
    C -->|Нет| E["List"]
    B -->|"Нужен порядок?"| F["TreeMap / sortedMapOf"]
    B -->|"Нет"| G["HashMap / hashMapOf"]
    D -->|"Нужен порядок?"| H["TreeSet / sortedSetOf"]
    D -->|"Нет"| I["HashSet / hashSetOf"]
    E -->|"Частые вставки в начало?"| J["ArrayDeque"]
    E -->|"Нет"| K["ArrayList / listOf"]
```

## Q25. (!) Как выбирать List, Sequence или Flow для результата API?

Правило простое: возвращайте самый простой контракт, который покрывает задачу, и усложняйте только при необходимости — `List` → `Sequence` → `Flow`. `List` — для готового конечного результата, `Sequence` — для ленивой синхронной обработки, `Flow` — когда данные асинхронные или потоковые.

| Тип | Когда использовать | Характеристика |
|-----|-------------------|---------------|
| `List` | Результат конечен и нужен целиком | Eager, синхронный, random access |
| `Sequence` | Ленивые синхронные вычисления | Промежуточные операции ленивы; повторный обход зависит от источника |
| `Flow` | Асинхронные / потоковые данные | Lazy, async, backpressure, отмена |

```kotlin
// ✅ List — результат конечен, нужен сразу
fun getActiveUsers(): List<User> = repository.findAll().filter { it.isActive }

// ✅ Sequence — ленивая обработка большого файла
fun activeNames(users: List<User>): Sequence<String> =
    users.asSequence().filter { it.isActive }.map { it.name }

// ✅ Flow — стриминг из БД, WebSocket, события
fun observeChanges(): Flow<Change> = callbackFlow {
    val listener = { change: Change -> trySend(change) }
    register(listener)
    awaitClose { unregister(listener) }
}
```

**Принцип**: выбирайте самый простой контракт, покрывающий задачу. `List` → `Sequence` → `Flow` — по мере роста сложности. Подробнее о `Flow` — в [вопросах по Kotlin Coroutines](kotlin-coroutines-interview.md).

## Q26. Чем scan/runningFold отличаются от runningReduce?

Эти операторы вычисляют **накопленные промежуточные результаты** на каждом шаге — в отличие от `fold`/`reduce`, которые возвращают только финальное значение.

**`scan`** (псевдоним `runningFold`) — аналог `fold`, но возвращает все промежуточные состояния, включая начальное:

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// fold — только финальное значение
val sum = numbers.fold(0) { acc, n -> acc + n }  // 15

// scan — список всех промежуточных значений
val runningSum = numbers.scan(0) { acc, n -> acc + n }
// [0, 1, 3, 6, 10, 15]  — включая начальное значение 0

// Практический пример: накопленный доход
data class Transaction(val amount: Double)
val transactions = listOf(Transaction(100.0), Transaction(-30.0), Transaction(50.0))
val runningBalance = transactions.scan(0.0) { balance, tx -> balance + tx.amount }
// [0.0, 100.0, 70.0, 120.0]
```

**`runningReduce`** — как `reduce`, но без начального значения и с промежуточными результатами:

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val runningMax = numbers.runningReduce { max, n -> maxOf(max, n) }
// [1, 2, 3, 4, 5]  — бегущий максимум (список начинается с первого элемента)

// Нарастающее произведение
val runningProduct = numbers.runningReduce { acc, n -> acc * n }
// [1, 2, 6, 24, 120]
```

**Применение в `Sequence`** — для ленивой обработки больших потоков:

```kotlin
generateSequence(1) { it + 1 }          // бесконечная последовательность
    .scan(0) { acc, n -> acc + n }       // нарастающая сумма
    .takeWhile { it < 1000 }             // пока < 1000
    .last()                              // последнее значение
```

## Q27. Чем takeWhile отличается от filter при обходе коллекции?

`takeWhile` возвращает совпадающий начальный участок и прекращает проверку при первом несовпадении. `filter` продолжает проверять весь источник.

**`distinctBy`** — удаляет дубликаты по ключу (сохраняет первое вхождение):

```kotlin
data class User(val name: String, val department: String)
val users = listOf(
    User("Alice", "Engineering"),
    User("Bob", "Engineering"),
    User("Carol", "Marketing"),
    User("Dave", "Engineering")
)

// distinct — полное сравнение (equals)
// distinctBy — только по ключу
val departments = users.distinctBy { it.department }
// [User("Alice", "Engineering"), User("Carol", "Marketing")]
// — один представитель от каждого отдела

// Для примитивов
listOf(1, 2, 2, 3, 1, 4).distinct()  // [1, 2, 3, 4]
```

**`takeWhile`/`dropWhile`** — берёт/пропускает элементы **пока** выполняется условие. Останавливается при первом несовпадении:

```kotlin
val numbers = listOf(2, 4, 6, 7, 8, 10)

// takeWhile — берёт пока чётные
numbers.takeWhile { it % 2 == 0 }  // [2, 4, 6] — 7 ломает условие

// dropWhile — пропускает пока чётные
numbers.dropWhile { it % 2 == 0 }  // [7, 8, 10] — с первого нечётного до конца

// ВАЖНО: не то же самое, что filter!
numbers.filter { it % 2 == 0 }     // [2, 4, 6, 8, 10] — все чётные, включая 8 и 10
```

**`takeLastWhile`/`dropLastWhile`** — аналогично, но с конца:

```kotlin
val logs = listOf("INFO start", "INFO init", "ERROR fail", "INFO retry", "INFO ok")

// Последние INFO-записи после последней ошибки
logs.takeLastWhile { it.startsWith("INFO") }  // ["INFO retry", "INFO ok"]
logs.dropLastWhile { it.startsWith("INFO") }  // ["INFO start", "INFO init", "ERROR fail"]
```

**Практическая разница** между `takeWhile`/`filter`:
- `takeWhile` — **раннее прерывание** (эффективно для отсортированных данных)
- `filter` — **сплошное сканирование** (все элементы проверяются)

## Q28. Чем LinkedHashMap отличается от TreeMap по порядку и доступу?

`LinkedHashMap` и `TreeMap` — JVM-реализации `Map` с разными порядком обхода и стоимостью операций. `LinkedHashMap` хранит порядок вставки и даёт O(1) на доступ; `TreeMap` держит ключи отсортированными (O(log n)) и предоставляет навигацию по диапазонам (`headMap`/`tailMap`/`subMap`).

| Тип | Порядок | `get`/`put`/`containsKey` | Когда использовать |
|-----|---------|--------------------------|-------------------|
| `HashMap` (`hashMapOf`) | Непредсказуемый | O(1)* | Максимальная скорость, порядок не важен |
| `LinkedHashMap` (`linkedMapOf`, `mapOf`) | Порядок вставки | O(1)* | Нужен предсказуемый порядок итерации |
| `TreeMap` (`sortedMapOf`) | Натуральная сортировка ключей | O(log n) | Нужен отсортированный порядок |

```kotlin
// Пример текущего результата mapOf; для явного контракта реализации используйте linkedMapOf
val linked = mapOf("banana" to 2, "apple" to 1, "cherry" to 3)
linked.keys.toList()  // [banana, apple, cherry]

// sortedMapOf → TreeMap: сортировка по ключу
val sorted = sortedMapOf("banana" to 2, "apple" to 1, "cherry" to 3)
sorted.keys.toList()  // [apple, banana, cherry]

// TreeMap с кастомным компаратором
val byLength = sortedMapOf(compareBy { it.length }, "banana" to 2, "apple" to 1, "fig" to 3)
byLength.keys.toList()  // [fig, apple, banana]

// TreeMap-специфичные операции
val tree = sortedMapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4)
tree.headMap("c")      // {a=1, b=2} — ключи строго меньше "c"
tree.tailMap("c")      // {c=3, d=4} — ключи ≥ "c"
tree.subMap("b", "d")  // {b=2, c=3}
```

**Важно**: интерфейсы `Set` и `Map` не обещают порядок обхода. Если порядок вставки является частью контракта кода, используйте явные `linkedSetOf` и `linkedMapOf`.

## Q29. Как различаются hashSetOf, linkedSetOf и sortedSetOf по порядку?

Аналогично Map-коллекциям, три варианта Set отличаются реализацией и поведением:

| Функция | Реализация | Порядок | `contains` |
|---------|-----------|---------|-----------|
| `hashSetOf()` | `HashSet` | Произвольный | O(1)* |
| `linkedSetOf()` | `LinkedHashSet` | Порядок вставки | O(1)* |
| `sortedSetOf()` | `TreeSet` | Натуральная сортировка | O(log n) |

```kotlin
// hashSetOf — максимальная скорость, порядок не важен
val ids = hashSetOf(3, 1, 4, 1, 5, 9, 2)  // уникальные, порядок произвольный

// setOf / linkedSetOf — порядок вставки
val tags = setOf("kotlin", "java", "spring")
tags.toList()  // [kotlin, java, spring]

// sortedSetOf — отсортированное множество
val sorted = sortedSetOf("banana", "apple", "cherry")
sorted.first()  // apple — NavigableSet API
sorted.last()   // cherry
sorted.headSet("cherry")  // [apple, banana]

// sortedSetOf с компаратором
val byLength = sortedSetOf(compareBy { it.length }, "banana", "fig", "apple")
byLength.toList()  // [fig, apple, banana]
```

**Правило выбора**: если нужен только быстрый `contains`/`add` — `hashSetOf`. Если нужен предсказуемый порядок итерации — `setOf`. Если нужна сортировка или `headSet`/`tailSet` — `sortedSetOf`.

## Q30. (!) Когда накладные расходы Sequence могут превысить выгоду?

`Sequence` экономит на промежуточных коллекциях, но не бесплатен: вместо аллокаций списков он платит за итераторы и потерю инлайнинга. Поэтому на маленьких данных и коротких цепочках оверхед итераторов перевешивает экономию, и обычный `List` оказывается быстрее.

**Оверхед `Sequence`:**
- Создание объекта-итератора на каждую промежуточную операцию
- Boxing примитивов (нет специализированных `IntSequence` в stdlib)
- Инлайнинг lambda-функций работает хуже из-за индиректных вызовов через итератор

```kotlin
// Иллюстрация: итог зависит от размеров, операций и JVM.
// Измеряйте конкретную цепочку, если выбор влияет на производительность.
```

**Когда `Sequence` НЕ выгоден:**
- Небольшая коллекция, где стоимость ленивой цепочки может превысить экономию списков
- Одна полная операция без ранней остановки: экономия промежуточных коллекций обычно мала
- Операции **без early termination** и без длинных цепочек
- Частые вызовы в tight loop — оверхед объектов накапливается

```kotlin
// ✅ Sequence окупается: большие данные + длинная цепочка + early stop
File("huge.log").useLines { lines ->
    lines.filter { "ERROR" in it }
         .map { it.substringAfter("] ") }
         .first()  // early termination — читаем только до первой ошибки
}

// ❌ Sequence не нужен: маленькая коллекция, одна операция
listOf(1, 2, 3).asSequence().filter { it > 1 }.toList()  // избыточно
```

> **Совет**: профилируйте перед оптимизацией. `asSequence()` — не серебряная пуля, а инструмент для конкретных случаев.

## Q31. Как any избегает промежуточного списка по сравнению с filter().isNotEmpty()?

Если нужен только факт наличия подходящего элемента, `any(predicate)` останавливается на первом совпадении и возвращает Boolean. `filter(predicate).isNotEmpty()` сначала строит список совпадений. Остальные приёмы ниже полезны, когда действительно нужен результат преобразования.

```kotlin
// 1. Задайте начальную ёмкость, если размер известен
val list = ArrayList<String>(expectedSize)
val map = HashMap<String, Int>(expectedSize * 4 / 3 + 1)

// 2. mapNotNull вместо map + filterNotNull (один проход)
// ❌ Два прохода, промежуточный список
items.map { parse(it) }.filterNotNull()
// ✅ Один проход
items.mapNotNull { parse(it) }

// 3. any/none вместо filter + isNotEmpty/isEmpty
// ❌ Создаёт полный список совпадений
items.filter { it.isActive }.isNotEmpty()
// ✅ Останавливается на первом совпадении
items.any { it.isActive }
// Для отсутствия подходящих элементов: items.none { it.isActive }

// 4. sumOf/count вместо map + sum/size
// ❌ Промежуточный список Int
items.map { it.price }.sum()
// ✅ Без промежуточного списка
items.sumOf { it.price }

// 5. containsAll / intersect для пересечений
// ❌ Создаёт новую коллекцию
(a intersect b).isNotEmpty()
// ✅ Без создания новой коллекции (для Set)
a.any { it in b }

// 6. Sequence для длинных цепочек (см. Q30)
```

## Q32. (!) Гарантирует ли read-only тип Kotlin потокобезопасность коллекции?

**Нет.** Read-only интерфейс не синхронизирует доступ к объекту, который другой поток может менять через mutable-ссылку. Потокобезопасность зависит от конкретной реализации и способа публикации данных.

**Read-only коллекции** (`List`, `Set`, `Map`):
- Параллельное чтение неизменяемого и безопасно опубликованного снимка не требует записи в объект
- Небезопасны, если другой поток модифицирует ту же коллекцию через `MutableList`-ссылку

**Mutable коллекции** (`MutableList`, `MutableMap`, `MutableSet`):
- Под капотом — `ArrayList`, `LinkedHashSet`, `LinkedHashMap` из Java
- **Не синхронизированы** — конкурентная запись вызывает `ConcurrentModificationException` или тихую потерю данных

```kotlin
// ✅ Вариант 1: ConcurrentHashMap из Java
val concurrentMap = java.util.concurrent.ConcurrentHashMap<String, Int>()
// thread-safe put/get/remove; итератор слабо согласован с конкурентными изменениями

// ✅ Вариант 2: Collections.synchronizedList (полная синхронизация)
val syncList = java.util.Collections.synchronizedList(mutableListOf<Int>())
// итерацию нужно синхронизировать вручную:
synchronized(syncList) { for (item in syncList) { /* ... */ } }

// ✅ Вариант 3: CopyOnWriteArrayList — для частых чтений, редких записей
val cowList = java.util.concurrent.CopyOnWriteArrayList<String>()

// ✅ Вариант 4: kotlinx.collections.immutable — истинная неизменяемость
val immutable = persistentListOf(1, 2, 3)
// Безопасно читать из любого потока — нельзя изменить

// ✅ Вариант 5: StateFlow/SharedFlow в корутинах (для реактивных паттернов)
```

> **Правило**: read-only тип сам по себе не делает общий изменяемый объект безопасным. Для shared mutable state нужна синхронизация или подходящая concurrent-коллекция; для версий состояния подходят persistent-коллекции с безопасной публикацией.

## Q33. Чем лямбда associate отличается от аргументов associateBy?

`associate` принимает одну лямбду, возвращающую `Pair<K, V>`. У `associateBy` есть `keySelector` и необязательный `valueTransform`, поэтому он тоже может вычислить и ключ, и значение, но отдельными лямбдами. Вариант с `associate` создаёт краткоживущие `Pair`-объекты; это может иметь значение на горячем пути.

| Функция | Сигнатура лямбды | Возвращает |
|---------|-----------------|-----------|
| `associate { }` | `T -> Pair<K, V>` | `Map<K, V>` |
| `associateBy { }` | `T -> K` | `Map<K, T>` (ключ → элемент) |
| `associateWith { }` | `T -> V` | `Map<T, V>` (элемент → значение) |

```kotlin
data class User(val id: Int, val name: String, val email: String)
val users = listOf(User(1, "Alice", "alice@x.com"), User(2, "Bob", "bob@x.com"))

// associate — полная свобода: задаёте и ключ, и значение
users.associate { it.id to it.email }
// {1="alice@x.com", 2="bob@x.com"}

// associateBy — элемент становится значением, лямбда даёт ключ
users.associateBy { it.id }
// {1=User(1,"Alice",...), 2=User(2,"Bob",...)}

// associateBy с valueTransform: и ключ, и значение — лямбды
users.associateBy(keySelector = { it.id }, valueTransform = { it.name })
// {1="Alice", 2="Bob"}  — как associate, но лаконичнее

// associateWith — элемент становится ключом, лямбда даёт значение
users.associateWith { it.email }
// {User(1,...)="alice@x.com", User(2,...)="bob@x.com"}
```

**Когда что использовать:**
- `associateBy` — создать индекс по уникальному полю (id → объект)
- `associateWith` — обогатить элементы вычисленным значением (объект → результат)
- `associate` — нужны нестандартные ключ И значение одновременно

> **Внимание**: все три при дубликатах ключей молча перезаписывают значение (побеждает последний). Используйте `groupBy`, если дубликаты возможны.

## Q34. Чем flatten отличается от flatMap?

**`flatten`** принимает `Iterable<Iterable<T>>` (коллекцию коллекций) и «склеивает» их в одну плоскую коллекцию. Это терминальная операция без преобразования элементов.

**`flatMap`** = `map` + `flatten`: сначала преобразует каждый элемент в коллекцию, затем склеивает.

```kotlin
// flatten — уже есть коллекция коллекций
val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
nested.flatten()  // [1, 2, 3, 4, 5]

// flatMap — элемент нужно сначала преобразовать в коллекцию
val words = listOf("hello world", "kotlin rocks")
words.flatMap { it.split(" ") }  // ["hello", "world", "kotlin", "rocks"]

// Эквивалентность: flatMap == map + flatten
words.map { it.split(" ") }.flatten()  // тот же результат

// Практика: все теги всех постов
data class Post(val title: String, val tags: List<String>)
val posts = listOf(Post("A", listOf("kotlin", "jvm")), Post("B", listOf("kotlin", "spring")))

posts.flatMap { it.tags }.distinct()  // ["kotlin", "jvm", "spring"]
// vs flatten — потребовало бы map сначала:
posts.map { it.tags }.flatten().distinct()  // то же, но два шага
```

**Разница в производительности**: `flatMap` — один проход, `map + flatten` — два прохода и промежуточный список вложенных коллекций.

## Q35. Чем minOrNull отличается от min на пустой коллекции?

`minOrNull()` возвращает `null` на пустой коллекции. `min()` требует элемент и в этом случае бросает `NoSuchElementException`.

Агрегирующие и граничные операции над числовыми коллекциями:

```kotlin
val numbers = listOf(3, 1, 4, 1, 5, 9, 2, 6)

// Агрегаты
numbers.min()        // 1  (бросает NoSuchElementException на пустой)
numbers.minOrNull()  // 1? (null на пустой — безопасный вариант)
numbers.max()        // 9
numbers.maxOrNull()  // 9?
numbers.sum()        // 31
numbers.average()    // 3.875 (Double)
numbers.count()      // 8

// С селектором
data class Product(val name: String, val price: Double)
val products = listOf(Product("A", 10.0), Product("B", 25.0), Product("C", 5.0))

products.minByOrNull { it.price }  // Product("C", 5.0)
products.maxByOrNull { it.price }  // Product("B", 25.0)
products.sumOf { it.price }        // 40.0
products.minOf { it.price }        // 5.0  (только значение, не элемент)
products.maxOf { it.price }        // 25.0

// coerceIn — ограничить значение диапазоном
val value = 15
value.coerceIn(0, 10)   // 10  — обрезано сверху
(-5).coerceIn(0, 100)   // 0   — обрезано снизу
5.coerceIn(0, 10)       // 5   — не изменилось

// Применение: нормализация пагинации
val page = userInput.coerceIn(1, maxPages)
val pageSize = requestedSize.coerceIn(10, 100)
```

> **Важно**: `min()`/`max()` бросают `NoSuchElementException` на пустой коллекции; `minOrNull()`/`maxOrNull()` возвращают `null`. Предпочитайте `*OrNull`-варианты в production-коде.

## Q36. Чем singleOrNull отличается от single при пустой или многозначной коллекции?

`singleOrNull()` возвращает `null`, если элементов нет или их больше одного. `single()` в обоих случаях бросает исключение.

Функции доступа к конкретным элементам по позиции или предикату:

| Функция | Поведение на пустой | Поведение если не найдено |
|---------|--------------------|-----------------------------|
| `first()` | `NoSuchElementException` | `NoSuchElementException` |
| `firstOrNull()` | `null` | `null` |
| `last()` | `NoSuchElementException` | `NoSuchElementException` |
| `lastOrNull()` | `null` | `null` |
| `single()` | `NoSuchElementException` | `NoSuchElementException` |
| `singleOrNull()` | `null` | `null` (и если > 1 элемента) |
| `elementAt(n)` | `IndexOutOfBoundsException` | `IndexOutOfBoundsException` |
| `elementAtOrNull(n)` | `null` | `null` |
| `elementAtOrElse(n) { }` | Вызывает лямбду | Вызывает лямбду |

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// first / last
numbers.first()           // 1
numbers.first { it > 3 }  // 4 — по предикату
numbers.last()            // 5
numbers.last { it < 3 }   // 2

// single — ожидаем ровно один элемент
listOf(42).single()                              // 42
listOf(1, 2).single { it > 1 }                  // 2
listOf(1, 3, 5).singleOrNull { it % 2 == 0 }    // null (не найдено)
listOf(2, 4).singleOrNull { it % 2 == 0 }       // null (нашли 2 элемента!)

// elementAt — доступ по индексу с безопасными альтернативами
numbers.elementAt(2)                     // 3
numbers.elementAtOrNull(10)              // null
numbers.elementAtOrElse(10) { idx -> idx * 0 }  // 0 (вызвана лямбда с индексом)

// getOrElse / getOrNull для List (синтаксический сахар)
numbers.getOrElse(10) { -1 }  // -1
numbers.getOrNull(10)          // null
```

> **Рекомендация**: в production-коде предпочитайте `firstOrNull`/`singleOrNull` и явно обрабатывайте `null`-случай. `first()` без предиката безопасен, только если вы точно знаете, что коллекция не пуста, — зафиксируйте это через `require`/`check` или комментарий.

---

## Q37. (!) Зачем persistent-коллекциям structural sharing при обновлении?

Structural sharing сохраняет прежнюю версию persistent-коллекции и переиспользует неизменённые части структуры в новой версии.

**`kotlinx.collections.immutable`** — библиотека от JetBrains, добавляющая по-настоящему неизменяемые коллекции с поддержкой structural sharing.

**Ключевые типы:**
- `PersistentList<T>` — иммутабельный список
- `PersistentMap<K, V>` — иммутабельный Map
- `PersistentSet<T>` — иммутабельное множество

```kotlin
import kotlinx.collections.immutable.*

val list = persistentListOf(1, 2, 3)
val list2 = list.add(4)         // возвращает новый PersistentList
val list3 = list.removeAt(0)    // list по-прежнему [1, 2, 3]

val map = persistentMapOf("a" to 1, "b" to 2)
val map2 = map.put("c", 3)      // новый PersistentMap, старый не изменён

// Builder для батч-изменений (эффективнее, чем цепочка вызовов)
val result = persistentListOf<Int>().builder().apply {
    addAll(listOf(1, 2, 3))
    add(4)
    removeAt(0)
}.build()
```

**Structural sharing (структурное разделение):**
При изменении новая коллекция разделяет неизменённые части с предыдущей — нет полного копирования. Реализовано через HAMTs (Hash Array Mapped Tries) для Map/Set и RRB-Trees для List. Операция `add` имеет сложность O(log n), а не O(n).

```
list  = [1, 2, 3, 4, 5]
list2 = [1, 2, 3, 4, 5, 6]
         ↑— shared nodes —↑
```

**Когда использовать:**
- Многопоточный доступ без синхронизации (истинная неизменяемость)
- Event Sourcing, Redux-паттерн в Compose
- Функциональные алгоритмы с историей состояний
- Kotlin Multiplatform (в отличие от `java.util.Collections.unmodifiableList`)

**Отличие от `listOf()`**: read-only тип результата `listOf()` сам по себе не обещает неизменяемую реализацию. `PersistentList` сохраняет прежнюю версию при обновлении и переиспользует неизменённые части структуры.

## Q38. (!) Чем отличаются Iterable, Collection и Sequence как контракты?

`Iterable` предоставляет `iterator()`, а `Collection` добавляет `size` и `contains`. `Sequence` — отдельный контракт, чьи промежуточные операции обычно строят ленивую цепочку. Ни `Iterable`, ни `Sequence` не гарантируют повторный обход для любого источника. Выбирайте тип API по нужным операциям.

| Критерий | `Iterable<T>` | `Collection<T>` | `Sequence<T>` |
|----------|--------------|-----------------|---------------|
| Знает `size`? | Нет | Да | Нет |
| Повторный обход? | Не гарантирован интерфейсом | Зависит от реализации | Не гарантирован |
| Вычисление | Зависит от реализации | Зависит от реализации | Промежуточные операции ленивы |
| Промежуточные коллекции | Зависят от вызванных операций | Зависят от вызванных операций | Не нужны для обычных map/filter |
| Подходит для | Обхода | size/contains | Ленивой цепочки операций |

```kotlin
// Iterable — базовый контракт для for-цикла
fun processAll(items: Iterable<String>) { // гибко: List, Set, любой custom
    for (item in items) println(item)
}

// Collection — когда нужен size или contains
fun paginate(items: Collection<String>, page: Int, size: Int): List<String> {
    val total = items.size // нужен size
    return items.drop(page * size).take(size)
}

// Sequence — длинная цепочка с промежуточными операциями
fun processLargeFile(lines: Sequence<String>): List<String> =
    lines
        .filter { it.isNotBlank() }
        .map { it.trim().lowercase() }
        .filter { it.startsWith("error") }
        .take(100)
        .toList()  // только здесь материализуем
```

**Правила выбора:**
- API принимает коллекцию → используйте `Iterable<T>` (максимальная гибкость)
- Нужен `size` / `contains` → `Collection<T>`
- Нужен случайный доступ → `List<T>`
- Много промежуточных операций на большой коллекции → `Sequence<T>`
- Асинхронный поток данных → `Flow<T>` из Coroutines

## Q39. Как соотносятся sortedBy, sortedWith и compareBy?

Коротко: `sortedBy` сортирует по одному ключу, `sortedWith` принимает произвольный `Comparator`, а `compareBy` — это фабрика таких компараторов из нескольких ключей. `sortedBy { x }` — просто сахар над `sortedWith(compareBy { x })`; для сортировки по нескольким полям нужен `compareBy(...)` или цепочка `thenBy`.

```kotlin
data class Person(val name: String, val age: Int, val score: Double)
val people = listOf(Person("Alice", 30, 9.5), Person("Bob", 25, 9.5), Person("Carol", 25, 8.0))

// sortedBy — сортировка по одному полю (Comparable)
val byAge = people.sortedBy { it.age }
// [Bob(25), Carol(25), Alice(30)]

// sortedByDescending — обратный порядок
val byScoreDesc = people.sortedByDescending { it.score }

// sortedWith + Comparator — сложная сортировка по нескольким полям
val byAgeThenName = people.sortedWith(compareBy({ it.age }, { it.name }))
// [Bob(25), Carol(25), Alice(30)]  ← внутри одного возраста по имени

// compareBy — создаёт Comparator из нескольких ключей (лексикографически)
val comparator: Comparator<Person> = compareBy({ it.age }, { it.name })
val sorted = people.sortedWith(comparator)

// compareByDescending — убывающий порядок по ключу
val byScoreThenAgeDesc = people.sortedWith(
    compareByDescending<Person> { it.score }.thenBy { it.age }
)

// thenBy / thenByDescending — цепочка компараторов
val complex = compareBy<Person> { it.age }
    .thenByDescending { it.score }
    .thenBy { it.name }
val result = people.sortedWith(complex)
```

| Функция | Применение |
|---------|-----------|
| `sortedBy { key }` | Одно поле, натуральный порядок |
| `sortedByDescending { key }` | Одно поле, обратный порядок |
| `sortedWith(comparator)` | Произвольный Comparator |
| `compareBy({ f1 }, { f2 })` | Lexicographic Comparator из нескольких ключей |
| `thenBy` / `thenByDescending` | Вторичная сортировка в цепочке |

> **На собеседовании**: `sortedBy` — синтаксический сахар над `sortedWith(compareBy { ... })`. Понимание `compareBy` позволяет строить сложные компараторы без ручной реализации `Comparator`.

## Q40. (!) Чем `groupingBy` отличается от `groupBy` — ленивый vs eager grouping?

**`groupBy`** — eager: сразу создаёт `Map<K, List<V>>` с полными списками.

**`groupingBy`** — lazy: возвращает `Grouping<T, K>` — объект для последующих агрегаций без создания промежуточных списков.

```kotlin
val words = listOf("apple", "banana", "avocado", "blueberry", "cherry")

// groupBy — возвращает Map<Char, List<String>>
val byLetter: Map<Char, List<String>> = words.groupBy { it.first() }
// {'a' -> ["apple", "avocado"], 'b' -> ["banana", "blueberry"], 'c' -> ["cherry"]}

// groupingBy — возвращает Grouping<String, Char>
val grouping: Grouping<String, Char> = words.groupingBy { it.first() }

// eachCount — подсчёт без создания списков
val counts: Map<Char, Int> = grouping.eachCount()
// {'a' -> 2, 'b' -> 2, 'c' -> 1}

// fold — произвольная агрегация
val longest: Map<Char, String> = grouping.fold("") { acc, s ->
    if (s.length > acc.length) s else acc
}

// reduce — агрегация без начального значения
val shortest: Map<Char, String> = grouping.reduce { _, acc, s ->
    if (s.length < acc.length) s else acc
}

// aggregate — полный контроль (accumulator может быть null для первого элемента)
val countAndLongest = grouping.aggregate { _, acc: Pair<Int, String>?, s, first ->
    if (first) 1 to s else (acc!!.first + 1) to maxOf(acc.second, s, compareBy { it.length })
}
```

**Когда использовать:**
- `groupBy` → нужны сами группы (списки элементов)
- `groupingBy` → нужна только агрегация (count, sum, max) без хранения всех элементов

`groupingBy` эффективнее по памяти: не создаёт промежуточные `List`.

## Q41. Чем chunked и windowed(size) различаются при шаге по умолчанию?

**`chunked(size)`** — делит коллекцию на непересекающиеся блоки фиксированного размера:

```kotlin
val numbers = (1..10).toList()

// chunked — разбивка на блоки
val chunks: List<List<Int>> = numbers.chunked(3)
// [[1,2,3], [4,5,6], [7,8,9], [10]]  — последний может быть меньше

// chunked с transform — обработка каждого блока
val sums: List<Int> = numbers.chunked(3) { chunk -> chunk.sum() }
// [6, 15, 24, 10]

// Применение: batch insert в БД
fun batchInsert(records: List<Record>) {
    records.chunked(500) { batch ->
        repository.saveAll(batch)  // вставка по 500 за раз
    }
}
```

**`windowed(size, step, partialWindows)`** — скользящее окно с настраиваемым шагом:

```kotlin
// windowed — перекрывающиеся окна (по умолчанию step=1)
val windows: List<List<Int>> = numbers.windowed(3)
// [[1,2,3], [2,3,4], [3,4,5], [4,5,6], [5,6,7], [6,7,8], [7,8,9], [8,9,10]]

// step > 1 — шаг между окнами
val step2: List<List<Int>> = numbers.windowed(3, step = 2)
// [[1,2,3], [3,4,5], [5,6,7], [7,8,9]]

// partialWindows=true — включать неполные окна в конце
val partial: List<List<Int>> = numbers.windowed(3, partialWindows = true)
// [..., [9,10], [10]]

// windowed с transform — скользящее среднее
val movingAvg: List<Double> = numbers.windowed(3) { it.average() }
// [2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0]

// zipWithNext — частный случай windowed(2)
val diffs: List<Int> = numbers.zipWithNext { a, b -> b - a }
// [1, 1, 1, 1, 1, 1, 1, 1, 1]
```

| Функция | Перекрытие | Применение |
|---------|-----------|-----------|
| `chunked(n)` | Нет (блоки не пересекаются) | Батч-обработка, пагинация |
| `windowed(n)` | Да (step=1 по умолчанию) | Скользящее среднее, анализ трендов |
| `windowed(n, step)` | Настраиваемое | Семплирование потока |
| `zipWithNext` | Пары соседних | Вычисление дельт |

## Q42. Что задают основной селектор associateBy, лямбда associateWith и лямбда associate?

Все три строят `Map`, но отличаются ролью лямбды: `associateBy` берёт из неё **ключ** (значение — сам элемент), `associateWith` — **значение** (ключ — сам элемент), `associate` задаёт **обе части** пары. Запомнить просто по «направлению»: `By` — ключ, `With` — значение, голый `associate` — полный контроль.

```kotlin
data class User(val id: Int, val name: String, val email: String)
val users = listOf(User(1, "Alice", "a@x.com"), User(2, "Bob", "b@x.com"), User(3, "Carol", "c@x.com"))

// associateBy { keySelector } — ключ из элемента, значение = сам элемент
val byId: Map<Int, User> = users.associateBy { it.id }
// {1=User(1,"Alice",...), 2=User(2,"Bob",...)}

// associateBy(keySelector, valueTransform) — ключ и значение из элемента
val idToName: Map<Int, String> = users.associateBy({ it.id }, { it.name })
// {1="Alice", 2="Bob", 3="Carol"}

// associateWith { valueSelector } — ключ = сам элемент, значение из лямбды
val nameToEmail: Map<String, String> = users.map { it.name }.associateWith { name ->
    users.first { it.name == name }.email
}
// {"Alice"="a@x.com", "Bob"="b@x.com"}

// Чаще используют так:
val userToEmail: Map<User, String> = users.associateWith { it.email }

// associate { Pair } — полный контроль: обе части из лямбды
val emailToId: Map<String, Int> = users.associate { it.email to it.id }
// {"a@x.com"=1, "b@x.com"=2, "c@x.com"=3}
```

**Сводная таблица:**

| Функция | Ключ | Значение |
|---------|------|---------|
| `associateBy { k }` | Из лямбды | Сам элемент |
| `associateBy({ k }, { v })` | Из лямбды k | Из лямбды v |
| `associateWith { v }` | Сам элемент | Из лямбды |
| `associate { k to v }` | Из лямбды | Из лямбды |

> При дублирующихся ключах выигрывает **последний** элемент. Если нужно сохранить все значения — используйте `groupBy`.

## Q43. Чем scan отличается от fold по результату?

**`fold`** — сворачивает коллекцию в одно итоговое значение. **`scan`** — то же самое, но возвращает **все промежуточные результаты** (включая начальное значение).

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// fold — только итоговое значение
val sum: Int = numbers.fold(0) { acc, n -> acc + n }  // 15

// scan — все промежуточные накопления
val runningSum: List<Int> = numbers.scan(0) { acc, n -> acc + n }
// [0, 1, 3, 6, 10, 15]  — размер = size + 1 (включает начальное)

// runningFold — синоним scan (читаемее в некоторых контекстах)
val runningProduct: List<Int> = numbers.runningFold(1) { acc, n -> acc * n }
// [1, 1, 2, 6, 24, 120]

// runningReduce — без начального значения
val running: List<Int> = numbers.runningReduce { acc, n -> acc + n }
// [1, 3, 6, 10, 15]  — размер = size (без начального)

// Практическое применение: скользящий максимум
val prices = listOf(10, 7, 15, 12, 20, 8)
val runningMax: List<Int> = prices.runningReduce { max, price -> maxOf(max, price) }
// [10, 10, 15, 15, 20, 20]

// Применение: накопление состояния без изменяемых переменных
data class Stats(val count: Int, val sum: Int, val avg: Double)
val stats: List<Stats> = numbers.scan(Stats(0, 0, 0.0)) { acc, n ->
    val newCount = acc.count + 1
    val newSum = acc.sum + n
    Stats(newCount, newSum, newSum.toDouble() / newCount)
}
```

| Функция | Начальное | Возвращает | Размер результата |
|---------|----------|-----------|-----------------|
| `fold(init) { }` | Да | Одно значение | 1 |
| `scan(init) { }` | Да | Все шаги | n + 1 |
| `reduce { }` | Нет | Одно значение | 1 |
| `runningReduce { }` | Нет | Все шаги | n |

---

## See also

- [Основы Kotlin](kotlin-interview.md) — null-safety, классы, scope-функции, inline
- [Kotlin Coroutines](kotlin-coroutines-interview.md) — Flow как альтернатива Sequence для асинхронных потоков
- [Kotlin / Java Interop](kotlin-interop-java-interview.md) — как Kotlin-коллекции видны из Java
- [DSL в Kotlin](kotlin-dsl-interview.md) — buildList/buildMap как пример type-safe builders
- [Исключения в Kotlin](kotlin-exceptions-interview.md) — обработка ошибок при работе с коллекциями
- [Сериализация в Kotlin](kotlin-serialization-interview.md) — сериализация списков и Map
- [Java Collections](../java/java-collections-interview.md) — основа Kotlin-коллекций на уровне JVM
- [Java Concurrency](../java/java-concurrency-interview.md) — потокобезопасные коллекции для сравнения
- [Design Patterns](../../design-patterns/design-patterns-interview.md) — итератор, компоновщик над коллекциями
