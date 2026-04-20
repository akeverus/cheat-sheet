---
title: "Kotlin Collections: Grouping and Aggregation"
description: "Кратко: руководство по группировке и агрегации коллекций в Kotlin: groupBy, groupingBy, агрегатные функции и продвинутые операции."
tags:
  - languages
  - kotlin
  - kotlin-collections-grouping
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kotlin Collections: Grouping and Aggregation

Кратко: руководство по группировке и агрегации коллекций в **Kotlin**: **groupBy**, **groupingBy**, агрегатные функции и продвинутые операции.

## Полезные ссылки

### Официальная документация
- [Kotlin Collections Overview](https://kotlinlang.org/docs/collections-overview.html)
- [Kotlin Grouping API](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/grouping-by.html)

### Обучающие материалы
- [Kotlin Collections Guide](https://www.baeldung.com/kotlin/collections-api)

### См. также
- [Основы Kotlin](kotlin-basics.md)
- [Списки (List)](kotlin-collections-list.md)
- [Словари (Map)](kotlin-collections-map.md)
- [Операции над коллекциями](kotlin-collections-operations.md)
- [Java Streams](../java/java-streams-fp.md)

## Содержание

- [Группировка](#группировка)
  - [groupBy](#groupby)

## Группировка

Группировка позволяет разделить коллекцию на группы на основе некоторого критерия. Это одна из самых мощных операций при работе с данными, так как позволяет организовывать и анализировать данные по категориям.

### groupBy

Функция `groupBy` группирует элементы коллекции по ключу, который вычисляется для каждого элемента. Результатом является словарь (Map), где ключи — это значения функции группировки, а значения — списки элементов, соответствующих этому ключу.

```kotlin
val list = listOf("apple", "banana", "apricot", "blueberry")

// Группировка по первому символу
val grouped = list.groupBy { it[0] }
// {'a': ["apple", "apricot"], 'b': ["banana", "blueberry"]}
```

`groupBy` проходит по всем элементам коллекции, вычисляет ключ для каждого элемента с помощью предоставленной функции, и помещает элемент в соответствующую группу. Если несколько элементов имеют одинаковый ключ, они попадают в одну группу. Это делает `groupBy` идеальным для категоризации данных.

// Группировка с трансформацией ключа
**val byLength** = **list.groupBy** { **it.length** }
// {5: ["**apple**"], 6: ["**banana**", "**apricot**"], 9: ["**blueberry**"]}

// Группировка с трансформацией значения
**val grouped** = **list.groupBy**(
    **keySelector** = { it[0] },
    **valueTransform** = { **it.uppercase**() }
)
// {'a': ["**APPLE**", "**APRICOT**"], 'b': ["**BANANA**", "**BLUEBERRY**"]}
```text

### groupBy с фильтрацией

```
val list = `listOf`("apple", "banana", "apricot", "blueberry", "avocado")

// Группировка и фильтрация
val grouped = list
    .filter { `it.length` > 5 }
    .`groupBy` { it[0] }
// {'b': ["banana", "blueberry"]}

// Группировка с условием
val grouped = list.`groupBy` {
    if (`it.length` > 6) "long" else "short"
}
// {"short": ["apple", "banana"], "long": ["apricot", "blueberry", "avocado"]}
```text

## GroupingBy

### Базовое использование

```
val list = `listOf`("apple", "banana", "apricot", "blueberry")

// `groupingBy` создает объект `Grouping`
val grouping = list.`groupingBy` { it[0] }

// Преобразование в Map
val map = grouping.`eachCount()`
// {'a': 2, 'b': 2}
```text

### eachCount

```
val list = `listOf`("apple", "banana", "apricot", "blueberry", "avocado")

// Подсчет элементов в каждой группе
val counts = list.`groupingBy` { it[0] }.`eachCount()`
// {'a': 3, 'b': 2}

// С фильтрацией
val counts = list.`groupingBy` { it[0] }
    .`eachCount` { `it.length` > 5 }
// {'a': 1, 'b': 2} (только длинные слова)
```text

### eachFold

```
val list = `listOf`("apple", "banana", "apricot", "blueberry")

// Сворачивание каждой группы
val folded = list.`groupingBy` { it[0] }
    .fold(0) { acc, element -> acc + `element.length` }
// {'a': 12, 'b': 15} (сумма длин в каждой группе)

// С начальным значением и трансформацией
val folded = list.`groupingBy` { it[0] }
    .fold(`emptyList`<`String`>()) { acc, element ->
        acc + `element.uppercase`()
    }
// {'a': ["`APPLE`", "`APRICOT`"], 'b': ["`BANANA`", "`BLUEBERRY`"]}
```text

### eachReduce

```
val numbers = `listOf`(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Сворачивание каждой группы
val reduced = numbers.`groupingBy` { it % 3 }
    .reduce { key, accumulator, element -> accumulator + element }
// {0: 18, 1: 22, 2: 15} (сумма по остаткам от деления на 3)

// С трансформацией
val reduced = numbers.`groupingBy` { it % 3 }
    .reduce { key, acc, element -> `maxOf`(acc, element) }
// {0: 9, 1: 10, 2: 8} (максимум в каждой группе)
```text

### aggregate

```
val list = `listOf`("apple", "banana", "apricot", "blueberry")

// Агрегация с полным контролем
val aggregated = list.`groupingBy` { it[0] }
    .aggregate { key, accumulator: Int?, element, first ->
        if (first) `element.length`
        else (accumulator ?: 0) + `element.length`
    }
// {'a': 12, 'b': 15} (сумма длин)

// Более сложная агрегация
val aggregated = list.`groupingBy` { it[0] }
    .aggregate { key, accumulator: `List`<`String`>?, element, first ->
        if (first) `listOf`(element)
        else (accumulator ?: `emptyList()`) + element
    }
// {'a': ["apple", "apricot"], 'b': ["banana", "blueberry"]}
```text

## Агрегация

### Суммирование по группам

```
data class `Product`(val name: `String`, val category: `String`, val price: `Double`)

val products = `listOf`(
    `Product`("`Laptop`", "`Electronics`", `999.99`),
    `Product`("`Phone`", "`Electronics`", `699.99`),
    `Product`("`Book`", "`Education`", `19.99`),
    `Product`("Pen", "`Education`", `2.99`)
)

// Сумма цен по категориям
val `totalByCategory` = products.`groupBy` { `it.category` }
    .`mapValues` { (_, products) -> products.`sumOf` { `it.price` } }
// {"`Electronics`": `1699.98`, "`Education`": `22.98`}

// С использованием `groupingBy`
val `totalByCategory2` = products.`groupingBy` { `it.category` }
    .fold(`0.0`) { acc, product -> acc + `product.price` }
// {"`Electronics`": `1699.98`, "`Education`": `22.98`}
```text

### Среднее по группам

```
val products = `listOf`(
    `Product`("`Laptop`", "`Electronics`", `999.99`),
    `Product`("`Phone`", "`Electronics`", `699.99`),
    `Product`("`Book`", "`Education`", `19.99`),
    `Product`("Pen", "`Education`", `2.99`)
)

// Средняя цена по категориям
val `avgByCategory` = products.`groupBy` { `it.category` }
    .`mapValues` { (_, products) -> `products.map` { `it.price` }.average() }
// {"`Electronics`": `849.99`, "`Education`": `11.49`}

// С использованием aggregate
val `avgByCategory2` = products.`groupingBy` { `it.category` }
    .aggregate { key, accumulator: `Pair`<`Double`, Int>?, element, first ->
        if (first) {
            `element.price to 1`
        } else {
            val (sum, count) = accumulator ?: (`0.0 to 0`)
            (sum + `element.price`) to (count + 1)
        }
    }
    .`mapValues` { (_, pair) -> `pair.first` / `pair.second` }
```text

## Подсчет

### eachCount

```
val list = `listOf`("apple", "banana", "apricot", "blueberry", "avocado")

// Подсчет элементов в каждой группе
val counts = list.`groupingBy` { it[0] }.`eachCount()`
// {'a': 3, 'b': 2}

// Подсчет с условием
val counts = list.`groupingBy` { it[0] }
    .`eachCount` { `it.length` > 5 }
// {'a': 1, 'b': 2} (только длинные слова)
```text

### Подсчет уникальных значений

```
val list = `listOf`("apple", "banana", "apricot", "blueberry", "avocado")

// Количество уникальных длин в каждой группе
val `uniqueLengths` = list.`groupingBy` { it[0] }
    .aggregate { key, accumulator: Set<Int>?, element, first ->
        if (first) `setOf`(`element.length`)
        else (accumulator ?: `emptySet()`) + `element.length`
    }
    .`mapValues` { (_, set) -> `set.size` }
// {'a': 2, 'b': 1} (количество разных длин)
```text

## Суммирование

### Сумма по группам

```
data class `Sale`(val product: `String`, val amount: `Double`, val region: `String`)

val sales = `listOf`(
    `Sale`("`Laptop`", `999.99`, "`North`"),
    `Sale`("`Phone`", `699.99`, "`North`"),
    `Sale`("`Laptop`", `999.99`, "`South`"),
    `Sale`("`Tablet`", `399.99`, "`South`")
)

// Сумма продаж по регионам
val `totalByRegion` = sales.`groupingBy` { `it.region` }
    .fold(`0.0`) { acc, sale -> acc + `sale.amount` }
// {"`North`": `1699.98`, "`South`": `1399.98`}

// Сумма по продуктам
val `totalByProduct` = sales.`groupingBy` { `it.product` }
    .fold(`0.0`) { acc, sale -> acc + `sale.amount` }
// {"`Laptop`": `1999.98`, "`Phone`": `699.99`, "`Tablet`": `399.99`}
```text

### Сумма с условием

```
val sales = `listOf`(
    `Sale`("`Laptop`", `999.99`, "`North`"),
    `Sale`("`Phone`", `699.99`, "`North`"),
    `Sale`("`Laptop`", `999.99`, "`South`"),
    `Sale`("`Tablet`", `399.99`, "`South`")
)

// Сумма только дорогих продаж (>500)
val `totalExpensive` = sales.`groupingBy` { `it.region` }
    .fold(`0.0`) { acc, sale ->
        acc + if (`sale.amount` > `500`) `sale.amount else 0.0`
    }
// {"`North`": `1699.98`, "`South`": `999.99`}
```text

## Минимум и максимум

### Минимум и максимум по группам

```
data class `Score`(val student: `String`, val subject: `String`, val score: Int)

val scores = `listOf`(
    `Score`("`Alice`", "`Math`", 95),
    `Score`("`Alice`", "`Science`", 88),
    `Score`("Bob", "`Math`", 92),
    `Score`("Bob", "`Science`", 90)
)

// Максимальный балл по предметам
val `maxBySubject` = scores.`groupingBy` { `it.subject` }
    .reduce { key, accumulator, element ->
        `maxOf`(`accumulator.score`, `element.score`).let {
            `Score`("", key, it)
        }
    }
    .`mapValues` { (_, score) -> `score.score` }
// {"`Math`": 95, "`Science`": 90}

// Минимальный балл по студентам
val `minByStudent` = scores.`groupingBy` { `it.student` }
    .reduce { key, accumulator, element ->
        `minOf`(`accumulator.score`, `element.score`).let {
            `Score`(key, "", it)
        }
    }
    .`mapValues` { (_, score) -> `score.score` }
// {"`Alice`": 88, "Bob": 90}
```text

## Fold и Reduce в группировке

### Fold с начальным значением

```
val numbers = `listOf`(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Сумма по остаткам от деления на 3
val `sumByRemainder` = numbers.`groupingBy` { it % 3 }
    .fold(0) { acc, element -> acc + element }
// {0: 18, 1: 22, 2: 15}

// Произведение по остаткам
val `productByRemainder` = numbers.`groupingBy` { it % 3 }
    .fold(1) { acc, element -> acc * element }
// {0: `162`, 1: `280`, 2: 80}
```text

### Reduce без начального значения

```
val numbers = `listOf`(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Максимум по остаткам
val `maxByRemainder` = numbers.`groupingBy` { it % 3 }
    .reduce { key, accumulator, element ->
        `maxOf`(accumulator, element)
    }
// {0: 9, 1: 10, 2: 8}

// Минимум по остаткам
val `minByRemainder` = numbers.`groupingBy` { it % 3 }
    .reduce { key, accumulator, element ->
        `minOf`(accumulator, element)
    }
// {0: 3, 1: 1, 2: 2}
```text

## Продвинутые операции

### Множественная группировка

```
data class `Order`(val customer: `String`, val product: `String`, val quantity: Int, val price: `Double`)

val orders = `listOf`(
    `Order`("`Alice`", "`Laptop`", 1, `999.99`),
    `Order`("`Alice`", "`Phone`", 2, `699.99`),
    `Order`("Bob", "`Laptop`", 1, `999.99`),
    `Order`("Bob", "`Tablet`", 3, `399.99`)
)

// Группировка по клиенту, затем по продукту
val `byCustomerAndProduct` = orders
    .`groupBy` { `it.customer` }
    .`mapValues` { (_, orders) ->
        orders.`groupBy` { `it.product` }
            .`mapValues` { (_, orders) ->
                orders.`sumOf` { `it.quantity` * `it.price` }
            }
    }
// {"`Alice`": {"`Laptop`": `999.99`, "`Phone`": `1399.98`},
//  "Bob": {"`Laptop`": `999.99`, "`Tablet`": `1199.97`}}
```text

### Группировка с трансформацией

```
val list = `listOf`("apple", "banana", "apricot", "blueberry")

// Группировка с преобразованием значений
val grouped = list.`groupBy`(
    `keySelector` = { it[0] },
    `valueTransform` = { `it.uppercase`() }
)
// {'a': ["`APPLE`", "`APRICOT`"], 'b': ["`BANANA`", "`BLUEBERRY`"]}

// Группировка с преобразованием ключей
val `byLength` = list.`groupBy` { `it.length` }
    .`mapKeys` { "length_${`it.key`}" }
// {"`length_5`": ["apple"], "`length_6`": ["banana", "apricot"], "`length_9`": ["blueberry"]}
```text

### Фильтрация групп

```
val list = `listOf`("apple", "banana", "apricot", "blueberry", "avocado")

// Группировка и фильтрация групп
val `filteredGroups` = list.`groupBy` { it[0] }
    .filter { (_, words) -> `words.size` > 1 }
// {'a': ["apple", "apricot", "avocado"], 'b': ["banana", "blueberry"]}

// Группировка с условием на ключ
val grouped = list.`groupBy` {
    if (`it.length` > 6) "long" else "short"
}
// {"short": ["apple", "banana"], "long": ["apricot", "blueberry", "avocado"]}
```text

## Лучшие практики

### Выбор метода группировки

```
// Используйте `groupBy` для простой группировки
val grouped = list.`groupBy` { `it.category` }

// Используйте `groupingBy` для агрегации
val aggregated = list.`groupingBy` { `it.category` }
    .fold(`0.0`) { acc, item -> acc + `item.price` }

// Используйте aggregate для сложной логики
val complex = list.`groupingBy` { `it.category` }
    .aggregate { key, acc, element, first ->
        // сложная логика
    }
```text

### Производительность

```
// `groupBy` создает Map сразу
val grouped = `largeList`.`groupBy` { `it.key` — O(n)

// `groupingBy` создает объект `Grouping` (ленивый)
val grouping = `largeList`.`groupingBy` { `it.key` — O(1)
val counts = grouping.`eachCount` — O(n) (вычисляется здесь)

// Используйте sequences для больших коллекций
val grouped = `largeList`.`asSequence()`
    .`groupBy` { `it.key` }
    .`toMap()`
```text

### Идиоматичный Kotlin

```
// Используйте деструктуризацию
for ((key, values) in grouped) {
    println("$key: $values")
}

// Используйте `mapValues` для трансформации
val transformed = grouped.`mapValues` { (_, values) ->
    `values.sum`()
}

// Комбинируйте операции
val result = data
    .filter { it.`isValid()` }
    .`groupBy` { `it.category` }
    .`mapValues` { (_, items) -> items.`sumOf` { `it.value` } }
```text

## Продвинутые техники группировки

### Многоуровневая группировка

Для сложных сценариев может потребоваться многоуровневая группировка:

```
data class `Order`(val category: `String`, val region: `String`, val amount: `Double`)

val orders = `listOf`(
    `Order`("`Electronics`", "`US`", `100.0`),
    `Order`("`Electronics`", "`EU`", `150.0`),
    `Order`("`Clothing`", "`US`", `50.0`)
)

// Группировка по нескольким ключам
val `multiLevel` = orders.`groupBy` { `it.category` }
    .`mapValues` { (_, orders) ->
        orders.`groupBy` { `it.region` }
    }
// {"`Electronics`": {"`US`": [...], "`EU`": [...]}, "`Clothing`": {"`US`": [...]}}

// Использование `Pair` для составных ключей
val `byCompositeKey` = orders.`groupBy` { `it.category` to `it.region` }
// {(`Electronics`, `US`): [...], (`Electronics`, `EU`): [...], ...}
```text

Многоуровневая группировка позволяет создавать сложные иерархические структуры данных для анализа.

### Группировка с условиями

Группировка с применением условий к элементам:

```
val numbers = `listOf`(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Группировка с фильтрацией элементов в группах
val grouped = numbers.`groupBy` { it % 3 }
    .`mapValues` { (_, values) ->
        `values.filter` { it > 5 }
    }

// Группировка с условием на ключ
val conditional = numbers.`groupBy` {
    when {
        it < 3 -> "small"
        it < 7 -> "medium"
        else -> "large"
    }
}
```text

Условная группировка позволяет создавать динамические категории на основе свойств элементов.

### Группировка с сортировкой

Комбинирование группировки с сортировкой для упорядоченных результатов:

```
val items = `listOf`(
    `Item`("A", 10),
    `Item`("B", 20),
    `Item`("A", 15),
    `Item`("B", 25)
)

// Группировка с сортировкой внутри групп
val grouped = items.`groupBy` { `it.category` }
    .`mapValues` { (_, items) ->
        items.`sortedByDescending` { `it.value` }
    }

// Группировка с сортировкой ключей
val `sortedGroups` = items.`groupBy` { `it.category` }
    .`toSortedMap`(`compareBy` { it })
```text

Сортировка в сочетании с группировкой позволяет создавать упорядоченные структуры данных для анализа и представления.

## Анализ данных

### Статистический анализ

Группировка часто используется для статистического анализа:

```
data class `Sale`(val product: `String`, val amount: `Double`, val date: `LocalDate`)

val sales = `listOf`(/... /)

// Среднее значение по группам
val `avgByProduct` = sales.`groupBy` { `it.product` }
    .`mapValues` { (_, sales) ->
        `sales.map` { `it.amount` }.average()
    }

// Медиана по группам
fun <T : `Comparable`<T>> `List`<T>.median(): T? {
    return sorted().`getOrNull`(size / 2)
}

val `medianByProduct` = sales.`groupBy` { `it.product` }
    .`mapValues` { (_, sales) ->
        `sales.map` { `it.amount` }.median()
    }

// Стандартное отклонение
fun `List`<`Double`>.`stdDev()`: `Double` {
    val mean = average()
    val variance = map { (it — mean).pow(2) }.average()
    return sqrt(variance)
}

val `stdDevByProduct` = sales.`groupBy` { `it.product` }
    .`mapValues` { (_, sales) ->
        `sales.map` { `it.amount` }.`stdDev()`
    }
```text

Статистический анализ с группировкой позволяет получать insights из данных и выявлять закономерности.

### Временной анализ

Группировка по времени для временного анализа:

```
val events = `listOf`(/события с временными метками /)

// Группировка по дням
val `byDay` = events.`groupBy` { `it.timestamp`.`toLocalDate()` }

// Группировка по часам
val `byHour` = events.`groupBy` {
    `it.timestamp`.`toLocalTime()`.hour
}

// Группировка по периодам
val `byPeriod` = events.`groupBy` {
    when {
        `it.timestamp.hour in 6`..11 -> "`Morning`"
        `it.timestamp.hour in 12`..17 -> "`Afternoon`"
        `it.timestamp.hour in 18`..23 -> "`Evening`"
        else -> "`Night`"
    }
}
```text

Временной анализ позволяет выявлять временные закономерности и тренды в данных.

## Производительность группировки

### Оптимизация больших коллекций

Для больших коллекций важно оптимизировать операции группировки:

```
// Используйте sequences для ленивой группировки
val `largeList` = (1..1_000_000).`toList()`

val grouped = `largeList`.`asSequence()`
    .`groupBy` { it % `1000` }
    .`toMap()`

// Используйте `groupingBy` для отложенной агрегации
val grouping = `largeList`.`groupingBy` { it % `1000` }
val counts = grouping.`eachCount()`  // Вычисляется только при вызове

// Параллельная группировка для очень больших коллекций
val `parallelGrouped` = `largeList`.`parallelStream()`
    .collect(`groupingBy`({ it % `1000` }, counting()))
```text

Оптимизация особенно важна для коллекций с миллионами элементов, где накладные расходы на группировку могут быть значительными.

### Кэширование результатов группировки

Для повторно используемых результатов группировки может быть полезно кэширование:

```
class `CachedGrouping`<T, K>(private val `keySelector`: (T) -> K) {
    private var `cache`: Map<K, `List`<T>>? = `null`
    private var `lastData`: `Collection`<T>? = `null`

    fun group(data: `Collection`<T>): Map<K, `List`<T>> {
        return if (data === `lastData` && `cache` != `null`) {
            `cache`!!
        } else {
            val result = data.`groupBy`(`keySelector`)
            `cache` = result
            `lastData` = data
            result
        }
    }
}

// Использование
val grouping = `CachedGrouping`<`User`> { `it.department` }
val grouped1 = `grouping.group`(users)  // Выполняется группировка
val grouped2 = `grouping.group`(users)  // Используется кэш
```text

Кэширование полезно, когда группировка выполняется многократно над одними и теми же данными.

Этот файл содержит полное руководство по группировке и агрегации коллекций в Kotlin, покрывающее все основные аспекты от базовых операций до продвинутых техник, анализа данных и оптимизации производительности.

## Продвинутые техники агрегации

### Кастомные агрегатные функции

Создание пользовательских агрегатных функций:

```
// Кастомная агрегатная функция для медианы
fun <T : `Comparable`<T>> `List`<T>.median(): T? {
    return if (`isEmpty()`) `null`
    else sorted()[size / 2]
}

// Кастомная агрегатная функция для моды
fun <T> `List`<T>.mode(): T? {
    return `groupBy` { it }
        .`maxByOrNull` { `it.value.size` }
        ?.key
}

// Кастомная агрегатная функция для квартилей
fun `List`<Int>.quartiles(): `Triple`<Int, Int, Int>? {
    return if (size < 4) `null`
    else {
        val sorted = sorted()
        val q1 = sorted[size / 4]
        val q2 = sorted[size / 2]
        val q3 = sorted[3 * size / 4]
        `Triple`(q1, q2, q3)
    }
}

// Использование
val numbers = `listOf`(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
val median = `numbers.median`()  // 5 или 6
val quartiles = `numbers.quartiles`()  // (3, 5, 8)
```text

Кастомные агрегатные функции позволяют выполнять специфичные для домена вычисления.

### Сложные агрегации

Выполнение сложных агрегаций с использованием кастомных функций:

```
// Многоуровневая агрегация
data class `Sale`(val product: `String`, val category: `String`, val amount: `Double`, val date: `LocalDate`)

val sales = `listOf`(/... /)

// Агрегация по нескольким уровням
val analysis = sales
    .`groupBy` { `it.category` }
    .`mapValues` { (_, `categorySales`) ->
        `categorySales`.`groupBy` { `it.product` }
            .`mapValues` { (_, `productSales`) ->
                `mapOf`(
                    "total" to `productSales`.`sumOf` { `it.amount` },
                    "average" to `productSales`.map { `it.amount` }.average(),
                    "count" to `productSales`.size,
                    "max" to `productSales`.`maxOfOrNull` { `it.amount` },
                    "min" to `productSales`.`minOfOrNull` { `it.amount` }
                )
            }
    }

// Временная агрегация
val `timeAnalysis` = sales
    .`groupBy` { `it.date`.`toLocalDate()` }
    .`mapValues` { (_, `daySales`) ->
        `mapOf`(
            "total" to `daySales`.`sumOf` { `it.amount` },
            "transactions" to `daySales`.size,
            "average" to `daySales`.map { `it.amount` }.average()
        )
    }
```text

Сложные агрегации позволяют получать детальную аналитику данных на различных уровнях.

## Работа с большими данными

### Стриминговая агрегация

Агрегация больших объемов данных через streaming:

```
// Стриминговая агрегация для больших коллекций
fun `streamAggregate`(items: `Sequence`<`Item`>): Map<`String`, `AggregateResult`> {
    val aggregator = `mutableMapOf`<`String`, `MutableList`<`Double`>>()

    items.`forEach` { item ->
        aggregator.`getOrPut`(`item.category`) { `mutableListOf()` }
            .add(`item.value`)
    }

    return aggregator.`mapValues` { (_, values) ->
        `AggregateResult`(
            sum = `values.sum`(),
            average = `values.average`(),
            count = `values.size`,
            max = values.`maxOrNull()` ?: `0.0`,
            min = values.`minOrNull()` ?: `0.0`
        )
    }
}

data class `AggregateResult`(
    val sum: `Double`,
    val average: `Double`,
    val count: Int,
    val max: `Double`,
    val min: `Double`
)

// Использование
val `largeDataset` = `generateSequence` { `generateItem()` }
    .take(1_000_000)

val aggregates = `streamAggregate`(`largeDataset`)
```text

Стриминговая агрегация позволяет обрабатывать большие объемы данных без загрузки всех данных в память.

### Инкрементальная агрегация

Инкрементальное обновление агрегатов при добавлении новых данных:

```
// Инкрементальная агрегация
class `IncrementalAggregator`<T, K> {
    private val aggregates = `mutableMapOf`<K, `AggregateState`>()

    data class `AggregateState`(
        var sum: `Double` = `0.0`,
        var count: Int = 0,
        var max: `Double` = `Double`.NEGATIVE_INFINITY,
        var min: `Double` = `Double`.POSITIVE_INFINITY
    ) {
        val average: `Double`
            get() = if (count > 0) sum / count `else 0.0`
    }

    fun add(key: K, value: `Double`) {
        val state = aggregates.`getOrPut`(key) { `AggregateState()` }
        `state.sum` += value
        `state.count`++
        `state.max` = `maxOf`(`state.max`, value)
        `state.min` = `minOf`(`state.min`, value)
    }

    fun `getAggregates()`: Map<K, `AggregateState`> {
        return aggregates.`toMap()`
    }
}

// Использование
val aggregator = `IncrementalAggregator`<`Sale`, `String`>()

sales.`forEach` { sale ->
    `aggregator.add`(`sale.category`, `sale.amount`)
}

val aggregates = aggregator.`getAggregates()`
```text

Инкрементальная агрегация позволяет обновлять агрегаты без пересчета всех данных, что улучшает производительность для больших объемов данных.

## Работа с временными рядами

### Агрегация временных данных

Группировка и агрегация данных по времени:

```
// Группировка по времени
data class `Event`(val timestamp: `Long`, val value: `Double`)

fun `aggregateByTime`(events: `List`<`Event`>, interval: `Long`): Map<`Long`, `Double`> {
    return events
        .`groupBy` { event ->
            // Округление до интервала
            (`event.timestamp` / interval) * interval
        }
        .`mapValues` { (_, events) ->
            `events.map` { `it.value` }.average()
        }
}

// Группировка по дням
fun `aggregateByDay`(events: `List`<`Event`>): Map<`LocalDate`, `Double`> {
    return events
        .`groupBy` { event ->
            `Instant`.`ofEpochMilli`(`event.timestamp`)
                .`atZone`(`ZoneId`.`systemDefault()`)
                .`toLocalDate()`
        }
        .`mapValues` { (_, events) ->
            `events.map` { `it.value` }.sum()
        }
}

// Группировка по часам
fun `aggregateByHour`(events: `List`<`Event`>): Map<`LocalDateTime`, `Double`> {
    return events
        .`groupBy` { event ->
            `Instant`.`ofEpochMilli`(`event.timestamp`)
                .`atZone`(`ZoneId`.`systemDefault()`)
                .`toLocalDateTime()`
                .`truncatedTo`(`ChronoUnit`.`HOURS`)
        }
        .`mapValues` { (_, events) ->
            `events.map` { `it.value` }.average()
        }
}
```text

Агрегация временных данных позволяет анализировать данные по различным временным интервалам.

### Скользящие окна для временных рядов

Использование скользящих окон для анализа временных рядов:

```
// Скользящее среднее
fun `movingAverage`(values: `List`<`Double`>, `windowSize`: Int): `List`<`Double`> {
    return `values.windowed`(`windowSize`) { window ->
        `window.average`()
    }
}

// Скользящее окно с различными функциями
fun `movingWindow`(
    values: `List`<`Double`>,
    `windowSize`: Int,
    aggregator: (`List`<`Double`>) -> `Double`
): `List`<`Double`> {
    return `values.windowed`(`windowSize`) { window ->
        aggregator(window)
    }
}

// Использование
val prices = `listOf`(`100.0`, `102.0`, `101.0`, `103.0`, `105.0`, `104.0`, `106.0`)
val `movingAvg` = `movingAverage`(102.0, 103.0, 104.0, 105.0)
val `movingMax` = `movingWindow`(prices, 3) { it.`maxOrNull()` ?: `0.0` }
val `movingMin` = `movingWindow`(prices, 3) { it.`minOrNull()` ?: `0.0` }
```text

Скользящие окна позволяют анализировать временные ряды и выявлять тренды в данных.

Этот файл содержит полное руководство по группировке и агрегации коллекций в Kotlin, покрывающее все основные аспекты от базовых операций до продвинутых техник, анализа данных, оптимизации производительности, работы с большими данными и временными рядами.

## Дополнительные техники агрегации

### Работа с накоплением данных

Использование накопления для агрегации:

```
// Накопление с начальным значением
fun <T, R> `List`<T>.accumulate(
    initial: R,
    operation: (R, T) -> R
): R {
    var accumulator = initial
    for (element in this) {
        accumulator = operation(accumulator, element)
    }
    return accumulator
}

// Использование
val numbers = `listOf`(1, 2, 3, 4, 5)
val sum = `numbers.accumulate`(0) { acc, value -> acc + value }  // 15
val product = `numbers.accumulate`(120)

// Накопление с промежуточными результатами
fun <T, R> `List`<T>.scan(initial: R, operation: (R, T) -> R): `List`<R> {
    val result = `mutableListOf`(initial)
    var accumulator = initial
    for (element in this) {
        accumulator = operation(accumulator, element)
        `result.add`(accumulator)
    }
    return result
}

// Использование
val `runningSum` = `numbers.scan`(0) { acc, value -> acc + value }
// [0, 1, 3, 6, 10, 15]
```text

Накопление позволяет эффективно агрегировать данные с сохранением промежуточных результатов.

### Работа с окнами данных

Использование окон для анализа данных:

```
// Скользящее окно с агрегацией
fun <T, R> `List`<T>.`windowedAggregate`(
    size: Int,
    step: Int = 1,
    aggregator: (`List`<T>) -> R
): `List`<R> {
    return `this.windowed`(size, step, `partialWindows` = `false`)
        .map(aggregator)
}

// Использование
val prices = `listOf`(`100.0`, `102.0`, `101.0`, `103.0`, `105.0`, `104.0`, `106.0`)
val `movingAverage` = prices.`windowedAggregate`(3) { `it.average`() }
// [101.0, `102.0`, `103.0`, `104.0`, `105.0`]

val `movingMax` = prices.`windowedAggregate`(3) { it.`maxOrNull()` ?: `0.0` }
val `movingMin` = prices.`windowedAggregate`(3) { it.`minOrNull()` ?: `0.0` }
```text

Окна данных позволяют анализировать локальные паттерны в данных.

Этот файл содержит полное руководство по группировке и агрегации коллекций в Kotlin, покрывающее все основные аспекты от базовых операций до продвинутых техник, анализа данных, оптимизации производительности, работы с большими данными, временными рядами, накоплением данных и работой с окнами данных.

## Дополнительные техники агрегации

### Работа с гистограммами

Создание гистограмм для анализа данных:

```
// Создание гистограммы
fun <T> `List`<T>.histogram(bins: Int = 10): Map<Int, Int> {
    val min = this.`minOrNull()`?.let { it as? `Comparable`<*> } ?: return `emptyMap()`
    val max = this.`maxOrNull()`?.let { it as? `Comparable`<*> } ?: return `emptyMap()`

    val range = (max as `Number`).`toDouble()` - (min as `Number`).`toDouble()`
    val `binSize` = range / bins

    return this.`groupBy` { value ->
        val `numValue` = (value as? `Number`)?.`toDouble()` ?: `0.0`
        ((`numValue` - (min as `Number`).`toDouble()` / `binSize`).`toInt()`.`coerceIn`(0, bins — 1)
    }.`mapValues` { `it.value.size` }
}

// Использование
val numbers = `listOf`(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
val histogram = `numbers.histogram`(5)
// {0: 2, 1: 2, 2: 2, 3: 2, 4: 2}
```text

Гистограммы позволяют визуализировать распределение данных.

Этот файл содержит полное руководство по группировке и агрегации коллекций в Kotlin, покрывающее все основные аспекты от базовых операций до продвинутых техник, анализа данных, оптимизации производительности, работы с большими данными, временными рядами, накоплением данных, работой с окнами данных и гистограммами.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Группировка и агрегация коллекций являются важными операциями для анализа и обработки данных. Понимание различных техник группировки, агрегации, работы с временными рядами, накопления данных и создания гистограмм позволяет эффективно обрабатывать большие объемы данных и извлекать полезную информацию. Правильное использование этих техник помогает создавать более эффективные и масштабируемые приложения.

Этот файл содержит полное руководство по группировке и агрегации коллекций в Kotlin, покрывающее все основные аспекты от базовых операций до продвинутых техник, анализа данных, оптимизации производительности, работы с большими данными, временными рядами, накоплением данных, работой с окнами данных, гистограммами, заключение и дополнительные ресурсы.

## Практические примеры использования

### Анализ продаж по категориям

Пример анализа продаж с использованием группировки:

```
data class `Sale`(val product: `String`, val category: `String`, val amount: `Double`, val date: `LocalDate`)

fun `analyzeSalesByCategory`(sales: `List`<`Sale`>): Map<`String`, `SaleStats`> {
    return sales.`groupBy` { `it.category` }
        .`mapValues` { (_, sales) ->
            `SaleStats`(
                total = sales.`sumOf` { `it.amount` },
                average = `sales.map` { `it.amount` }.average(),
                count = `sales.size`
            )
        }
}

data class `SaleStats`(val total: `Double`, val average: `Double`, val count: Int)
```text

Группировка позволяет эффективно анализировать данные по категориям.

### Агрегация временных рядов

Пример агрегации данных по времени:

```
data class `Event`(val timestamp: `Long`, val value: `Double`)

fun `aggregateByHour`(events: `List`<`Event`>): Map<`Long`, `Double`> {
    return events.`groupBy` { event ->
        // Округление до часа
        (`event.timestamp` / 3600000) * 3600000
    }.`mapValues` { (_, events) ->
        `events.map` { `it.value` }.average()
    }
}
```text

Группировка по времени позволяет анализировать временные ряды.

### Многоуровневая группировка

Пример многоуровневой группировки данных:

```
data class `Sale`(val product: `String`, val category: `String`, val region: `String`, val amount: `Double`)

fun `groupByCategoryAndRegion`(sales: `List`<`Sale`>): Map<`String`, Map<`String`, `List`<`Sale`>>> {
    return sales.`groupBy` { `it.category` }
        .`mapValues` { (_, sales) ->
            sales.`groupBy` { `it.region` }
        }
}

fun `aggregateByCategoryAndRegion`(sales: `List`<`Sale`>): Map<`String`, Map<`String`, `Double`>> {
    return sales.`groupBy` { `it.category` }
        .`mapValues` { (_, sales) ->
            sales.`groupBy` { `it.region` }
                .`mapValues` { (_, sales) ->
                    sales.`sumOf` { `it.amount` }
                }
        }
}

// Использование
val sales = `listOf`(
    `Sale`("`Product1`", "`Electronics`", "`North`", `100.0`),
    `Sale`("`Product2`", "`Electronics`", "`South`", `200.0`),
    `Sale`("`Product3`", "`Clothing`", "`North`", `150.0`)
)
val grouped = `groupByCategoryAndRegion`(sales)
val aggregated = `aggregateByCategoryAndRegion`(sales)
```text

Многоуровневая группировка позволяет анализировать данные по нескольким измерениям.

### Условная группировка

Пример группировки с условиями:

```
fun `groupByCondition`(items: `List`<Int>): Map<`String`, `List`<Int>> {
    return items.`groupBy` { value ->
        when {
            value < 0 -> "negative"
            value == 0 -> "zero"
            value <= 10 -> "small"
            value <= `100` -> "medium"
            else -> "large"
        }
    }
}

// Группировка с предикатом
fun <T> `List`<T>.`partitionBy`(predicate: (T) -> `Boolean`): `Pair`<`List`<T>, `List`<T>> {
    return `this.partition`(predicate)
}

// Использование
val numbers = `listOf`(-5, 0, 5, 15, 50, `150`)
val grouped = `groupByCondition`(numbers)
val (evens, odds) = numbers.`partitionBy` { it % 2 == 0 }
```text

Условная группировка позволяет классифицировать данные по различным критериям.

### Практические примеры: Анализ данных продаж

```
data class `Sale`(
    val product: `String`,
    val category: `String`,
    val amount: `Double`,
    val date: `LocalDate`,
    val region: `String`
)

class `SalesAnalyzer`(private val sales: `List`<`Sale`>) {
    // Группировка по категориям с суммарным объемом продаж
    fun `salesByCategory()`: Map<`String`, `Double`> {
        return sales.`groupBy` { `it.category` }
            .`mapValues` { (_, sales) -> sales.`sumOf` { `it.amount` } }
    }

    // Группировка по регионам с топ-3 продуктами
    fun `topProductsByRegion`(limit: Int = 3): Map<`String`, `List`<`Pair`<`String`, `Double`>>> {
        return sales.`groupBy` { `it.region` }
            .`mapValues` { (_, `regionSales`) ->
                `regionSales`.`groupBy` { `it.product` }
                    .`mapValues` { (_, `productSales`) -> `productSales`.`sumOf` { `it.amount` } }
                    .`toList()`
                    .`sortedByDescending` { `it.second` }
                    .take(limit)
            }
    }

    // Продажи по месяцам
    fun `salesByMonth()`: Map<`YearMonth`, `Double`> {
        return sales.`groupingBy` { `YearMonth`.from(`it.date`) }
            .aggregate { _, accumulator: `Double`?, element, _ ->
                (accumulator ?: `0.0`) + `element.amount`
            }
    }

    // Статистика по категориям
    fun `categoryStatistics()`: Map<`String`, `CategoryStats`> {
        return sales.`groupBy` { `it.category` }
            .`mapValues` { (_, `categorySales`) ->
                `CategoryStats`(
                    `totalSales` = `categorySales`.`sumOf` { `it.amount` },
                    `averageSale` = `categorySales`.map { `it.amount` }.average(),
                    count = `categorySales`.size,
                    `maxSale` = `categorySales`.`maxOf` { `it.amount` },
                    `minSale` = `categorySales`.`minOf` { `it.amount` }
                )
            }
    }
}

data class `CategoryStats`(
    val `totalSales`: `Double`,
    val `averageSale`: `Double`,
    val count: Int,
    val `maxSale`: `Double`,
    val `minSale`: `Double`
)
```text

### Практические примеры: Группировка пользователей

```
data class `User`(
    val id: `Long`,
    val name: `String`,
    val age: Int,
    val city: `String`,
    val `subscriptionType`: `String`,
    val `registrationDate`: `LocalDate`
)

class `UserAnalyzer`(private val users: `List`<`User`>) {
    // Группировка по городам с количеством пользователей
    fun `usersByCity()`: Map<`String`, Int> {
        return users.`groupingBy` { `it.city` }
            .`eachCount()`
    }

    // Группировка по возрастным группам
    fun `usersByAgeGroup()`: Map<`String`, `List`<`User`>> {
        return users.`groupBy` { user ->
            when (`user.age`) {
                `in 0`..17 -> "`Minor`"
                `in 18`..25 -> "`Young Adult`"
                `in 26`..40 -> "`Adult`"
                `in 41`..60 -> "`Middle Age`"
                else -> "`Senior`"
            }
        }
    }

    // Группировка по типу подписки с средним возрастом
    fun `averageAgeBySubscription()`: Map<`String`, `Double`> {
        return users.`groupBy` { it.`subscriptionType` }
            .`mapValues` { (_, users) -> `users.map` { `it.age` }.average() }
    }

    // Новые пользователи по месяцам
    fun `newUsersByMonth()`: Map<`YearMonth`, Int> {
        return users.`groupingBy` { `YearMonth`.from(it.`registrationDate`) }
            .`eachCount()`
    }
}
```text

### Практические примеры: Работа с временными рядами

```
data class `TimeSeriesPoint`(
    val timestamp: `Instant`,
    val value: `Double`,
    val category: `String`
)

class `TimeSeriesAnalyzer`(private val points: `List`<`TimeSeriesPoint`>) {
    // Группировка по часам
    fun `groupByHour()`: Map<Int, `List`<`TimeSeriesPoint`>> {
        return points.`groupBy` {
            `it.timestamp`.`atZone`(`ZoneId`.`systemDefault()`).hour
        }
    }

    // Группировка по дням недели
    fun `groupByDayOfWeek()`: Map<`DayOfWeek`, `List`<`TimeSeriesPoint`>> {
        return points.`groupBy` {
            `it.timestamp`.`atZone`(`ZoneId`.`systemDefault()`).`dayOfWeek`
        }
    }

    // Агрегация по категориям и дням
    fun `aggregateByCategoryAndDay()`: Map<`Pair`<`String`, `LocalDate`>, `Double`> {
        return points.`groupingBy` {
            `Pair`(
                `it.category`,
                `it.timestamp`.`atZone`(`ZoneId`.`systemDefault()`).`toLocalDate()`
            )
        }
        .aggregate { _, accumulator: `Double`?, element, _ ->
            (accumulator ?: `0.0`) + `element.value`
        }
    }

    // Скользящее среднее по окнам
    fun `movingAverage`(`windowSize`: Int): `List`<`Double`> {
        return `points.map` { `it.value` }
            .windowed(`windowSize`, `partialWindows` = `true`)
            .map { `it.average`() }
    }
}
```text

Этот файл содержит полное руководство по группировке и агрегации коллекций в Kotlin, покрывающее все основные аспекты от базовых операций до продвинутых техник, анализа данных, оптимизации производительности, работы с большими данными, временными рядами, накоплением данных, работой с окнами данных, гистограммами, практические примеры использования для анализа продаж, пользователей, временных рядов, включая многоуровневую и условную группировку, заключение и дополнительные ресурсы.

## Дополнительные ресурсы

Для дальнейшего изучения группировки и агрегации коллекций в Kotlin рекомендуется:

- Kotlin Collections Documentation: https://kotlinlang.org/docs/collections-overview.html
- Kotlin Grouping Operations: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/group-by.html

```