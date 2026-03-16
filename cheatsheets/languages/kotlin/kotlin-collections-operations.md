---
title: "Kotlin Collections: Operations"
description: "Кратко: полное руководство по операциям над коллекциями в Kotlin: трансформации, фильтрация, агрегация, группировка и другие операции."
tags: ["languages", "kotlin", "kotlin-collections-operations"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Kotlin Collections: Operations

Кратко: полное руководство по операциям над коллекциями в **Kotlin**: трансформации, фильтрация, агрегация, группировка и другие операции.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [Kotlin Collections Overview](https://kotlinlang.org/docs/collections-overview.html)
- [Kotlin Collection Operations](https://kotlinlang.org/docs/collection-operations.html)

### **Baeldung**
- [Kotlin Collections Guide](https://www.baeldung.com/kotlin/collections-api)

### См. также
- [Основы Kotlin](kotlin-basics.md)
- [Списки (List)](kotlin-collections-list.md)
- [Множества (Set)](kotlin-collections-set.md)
- [Словари (Map)](kotlin-collections-map.md)
- [Последовательности (Sequences)](kotlin-collections-sequences.md)
- [Группировка и агрегация](kotlin-collections-grouping.md)
- [Java Streams](../java/java-streams-fp.md)

## Содержание

- [Трансформации](#трансформации)
  - [**Map**](#map)
  - [FlatMap](#flatmap)
  - [**Zip**](#zip)
  - [**Windowed** и **Chunked**](#windowed-и-chunked)
- [Фильтрация](#фильтрация)
  - [Базовые фильтры](#базовые-фильтры)
  - [**Take** и **Drop**](#take-и-drop)
  - [**Partition**](#partition)
- [Агрегация](#агрегация)
  - [**Sum** и **Average**](#sum-и-average)
  - [**Min** и **Max**](#min-и-max)
  - [**Fold** и **Reduce**](#fold-и-reduce)
  - [**Count**](#count)
- [Поиск и выборка](#поиск-и-выборка)
  - [Поиск элементов](#поиск-элементов)
  - [Поиск индексов](#поиск-индексов)
- [Сортировка](#сортировка)
- [Объединение и разделение](#объединение-и-разделение)
  - [Объединение](#объединение)
  - [Разделение](#разделение)
- [Работа с индексами](#работа-с-индексами)
- [Уникальность](#уникальность)
- [Сравнение и проверка](#сравнение-и-проверка)
  - [Проверка условий](#проверка-условий)
  - [Сравнение коллекций](#сравнение-коллекций)
- [Лучшие практики](#лучшие-практики)
  - [Цепочки операций](#цепочки-операций)
  - [Производительность](#производительность)
  - [Идиоматичный **Kotlin**](#идиоматичный-kotlin)
  - [Оптимизация](#оптимизация)
- [Продвинутые техники](#продвинутые-техники)
  - [Работа с вложенными коллекциями](#работа-с-вложенными-коллекциями)
  - [Параллельная обработка](#параллельная-обработка)
  - [Ленивые вычисления с **Sequences**](#ленивые-вычисления-с-sequences)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Измерение производительности](#измерение-производительности)
  - [Оптимизация памяти](#оптимизация-памяти)
  - [Кэширование результатов](#кэширование-результатов)
- [Работа с типами](#работа-с-типами)
  - [**Type-safe** операции](#type-safe-операции)
  - [**Generic** операции](#generic-операции)
- [Работа с параллельными коллекциями](#работа-с-параллельными-коллекциями)
  - [Потокобезопасные коллекции](#потокобезопасные-коллекции)
- [Оптимизация производительности коллекций](#оптимизация-производительности-коллекций)
  - [Измерение и профилирование](#измерение-и-профилирование)
  - [Кэширование результатов операций](#кэширование-результатов-операций)
- [Специализированные операции](#специализированные-операции)
  - [Операции для работы с числами](#операции-для-работы-с-числами)
  - [Операции для работы со строками](#операции-для-работы-со-строками)
- [Дополнительные операции](#дополнительные-операции)
  - [Работа с предикатами](#работа-с-предикатами)
  - [Работа с трансформациями](#работа-с-трансформациями)
  - [Работа с агрегацией](#работа-с-агрегацией)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Практические примеры использования](#практические-примеры-использования)
  - [Обработка данных в цепочке](#обработка-данных-в-цепочке)
  - [Трансформация и фильтрация](#трансформация-и-фильтрация)
  - [Специализированные трансформации](#специализированные-трансформации)
  - [Практические примеры: Обработка больших наборов данных](#практические-примеры-обработка-больших-наборов-данных)
  - [Практические примеры: Фильтрация и трансформация данных](#практические-примеры-фильтрация-и-трансформация-данных)
  - [Практические примеры: Агрегация данных](#практические-примеры-агрегация-данных)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Трансформации

Трансформации позволяют преобразовывать элементы коллекции в другие значения или типы. Это одна из самых распространенных операций при работе с коллекциями.

### **Map**

Операция `**map**` применяет функцию преобразования к каждому элементу коллекции и возвращает новую коллекцию с преобразованными элементами. Это эквивалентно циклу, который проходит по всем элементам и применяет к ним функцию.

```kotlin
val list = listOf(1, 2, 3, 4, 5)

// Простое преобразование
val doubled = list.map { it * 2 }              // [2, 4, 6, 8, 10]
val strings = list.map { it.toString() }       // ["1", "2", "3", "4", "5"]
```

Операция `**map**` создает новую коллекцию того же размера, что и исходная. Каждый элемент исходной коллекции преобразуется в один элемент результирующей коллекции. Это делает `**map**` идеальным для простых преобразований типов или значений.

// С индексом
**val indexed** = **list.mapIndexed** { **index**, **value** ->
    "$**index**: $**value**"
}                                               // ["0: 1", "1: 2", "2: 3", "3: 4", "4: 5"]

// С фильтрацией **null**
**val nullable** = **listOf(**1, `null`, 2, `null`, 3**)
**val mapped** = **nullable.mapNotNull** { it?.**let** { it * 2 } }  // [2, 4, 6]
```

### FlatMap

Операция `flatMap` применяет функцию, которая возвращает коллекцию, к каждому элементу, а затем "разворачивает" (flatten) все результирующие коллекции в одну плоскую коллекцию. Это полезно, когда одно значение может соответствовать нескольким значениям в результате.

```kotlin
val list = `listOf`(1, 2, 3)

// `FlatMap` - преобразует и "разворачивает"
val `flatMapped` = list.`flatMap` { 
    `listOf`(it, it * 2) 
}                                               // [1, 2, 2, 4, 3, 6]
```

В отличие от `map`, который создает коллекцию коллекций, `flatMap` создает одну плоскую коллекцию. Это эквивалентно вызову `map`, а затем `flatten`. `flatMap` особенно полезен при работе с вложенными структурами данных или когда нужно преобразовать один элемент в несколько.

// С индексом
val indexed = list.flatMapIndexed { index, value ->
    listOf(index, value)
}                                               // [0, 1, 1, 2, 2, 3]

// Flatten - просто разворачивает
val nested = listOf(listOf(1, 2), listOf(3, 4))
val flat = nested.flatten()                    // [1, 2, 3, 4]
```

### **Zip**

```kotlin
val list1 = listOf(1, 2, 3)
val list2 = listOf("a", "b", "c")

// Zip в пары
val zipped = list1.zip(list2)                  // [(1, "a"), (2, "b"), (3, "c")]

// Zip с трансформацией
val zipped = list1.zip(list2) { a, b ->
    "$a$b"
}                                               // ["1a", "2b", "3c"]

// Zip с разной длиной
val list3 = listOf(1, 2)
val zipped2 = list1.zip(list3)                 // [(1, 1), (2, 2)]

// Unzip
val pairs = listOf(1 to "a", 2 to "b", 3 to "c")
val (numbers, letters) = pairs.unzip()
// numbers = [1, 2, 3], letters = ["a", "b", "c"]
```

### **Windowed** и **Chunked**

```kotlin
val list = listOf(1, 2, 3, 4, 5)

// Windowed - скользящее окно
list.windowed(3)                                // [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
list.windowed(3, step = 2)                       // [[1, 2, 3], [3, 4, 5]]
list.windowed(3, partialWindows = true)         // [[1, 2, 3], [2, 3, 4], [3, 4, 5], [4, 5], [5]]

// Chunked - разбиение на части
list.chunked(2)                                 // [[1, 2], [3, 4], [5]]
list.chunked(3)                                 // [[1, 2, 3], [4, 5]]
```

## Фильтрация

### Базовые фильтры

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6)

// filter
val evens = list.filter { it % 2 == 0 }        // [2, 4, 6]

// filterNot
val odds = list.filterNot { it % 2 == 0 }      // [1, 3, 5]

// filterIndexed
val filtered = list.filterIndexed { index, value ->
    index % 2 == 0 && value > 2
}                                               // [3, 5]

// filterIsInstance
val mixed = listOf(1, "a", 2, "b", 3)
val numbers = mixed.filterIsInstance<Int>()    // [1, 2, 3]

// filterNotNull
val nullable = listOf(1, null, 2, null, 3)
val nonNull = nullable.filterNotNull()         // [1, 2, 3]
```

### **Take** и **Drop**

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// take - взять первые N элементов
list.take(3)                                    // [1, 2, 3]

// takeWhile - взять пока условие true
list.takeWhile { it < 5 }                       // [1, 2, 3, 4]

// takeLast - взять последние N элементов
list.takeLast(3)                                // [8, 9, 10]

// takeLastWhile - взять последние пока условие true
list.takeLastWhile { it > 7 }                   // [8, 9, 10]

// drop - пропустить первые N элементов
list.drop(3)                                    // [4, 5, 6, 7, 8, 9, 10]

// dropWhile - пропустить пока условие true
list.dropWhile { it < 5 }                       // [5, 6, 7, 8, 9, 10]

// dropLast - пропустить последние N элементов
list.dropLast(3)                                // [1, 2, 3, 4, 5, 6, 7]

// dropLastWhile - пропустить последние пока условие true
list.dropLastWhile { it > 7 }                   // [1, 2, 3, 4, 5, 6, 7]
```

### **Partition**

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6)

// Разделение на две части
val (evens, odds) = list.partition { it % 2 == 0 }
// evens = [2, 4, 6], odds = [1, 3, 5]
```

## Агрегация

### **Sum** и **Average**

```kotlin
val list = listOf(1, 2, 3, 4, 5)

// sum
val sum = list.sum()                            // 15
val sumBy = listOf("a", "ab", "abc").sumOf { it.length }  // 6

// average
val average = list.average()                    // 3.0
```

### **Min** и **Max**

```kotlin
val list = listOf(3, 1, 4, 1, 5, 9, 2, 6)

// min / max
val min = list.minOrNull()                      // 1
val max = list.maxOrNull()                      // 9

// minBy / maxBy
val names = listOf("Alice", "Bob", "Charlie")
val shortest = names.minByOrNull { it.length }  // "Bob"
val longest = names.maxByOrNull { it.length }   // "Charlie"

// minWith / maxWith (с Comparator)
val minWith = list.minWithOrNull(compareBy { it })  // 1
val maxWith = list.maxWithOrNull(compareByDescending { it })  // 9
```

### **Fold** и **Reduce**

```kotlin
val list = listOf(1, 2, 3, 4, 5)

// reduce - накапливает значение
val sum = list.reduce { acc, value -> acc + value }  // 15
val product = list.reduce { acc, value -> acc * value }  // 120

// fold - с начальным значением
val sum2 = list.fold(0) { acc, value -> acc + value }  // 15
val product2 = list.fold(1) { acc, value -> acc * value }  // 120

// runningFold - все промежуточные значения
val running = list.runningFold(0) { acc, value -> acc + value }
// [0, 1, 3, 6, 10, 15]

// runningReduce - все промежуточные значения
val running2 = list.runningReduce { acc, value -> acc + value }
// [1, 3, 6, 10, 15]
```

### **Count**

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6)

// count
val count = list.count()                        // 6
val evenCount = list.count { it % 2 == 0 }      // 3
```

## Поиск и выборка

### Поиск элементов

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6)

// find - найти первый элемент
val found = list.find { it > 3 }                // 4
val found2 = list.first { it > 3 }              // 4
val found3 = list.firstOrNull { it > 10 }       // null

// findLast - найти последний элемент
val lastFound = list.findLast { it < 5 }        // 4
val lastFound2 = list.last { it < 5 }           // 4
val lastFound3 = list.lastOrNull { it > 10 }    // null

// single - найти единственный элемент
val single = listOf(42).single()                // 42
val singleOrNull = list.singleOrNull { it == 3 }  // 3
```

### Поиск индексов

```kotlin
val list = listOf("a", "b", "c", "b", "d")

// indexOf
val index = list.indexOf("b")                    // 1
val index2 = list.indexOf("x")                   // -1

// indexOfFirst
val firstIndex = list.indexOfFirst { it == "b" }  // 1

// indexOfLast
val lastIndex = list.indexOfLast { it == "b" }   // 3

// lastIndexOf
val lastIndex2 = list.lastIndexOf("b")           // 3
```

## Сортировка

```kotlin
val list = listOf(3, 1, 4, 1, 5, 9, 2, 6)

// sorted
val sorted = list.sorted()                      // [1, 1, 2, 3, 4, 5, 6, 9]
val sortedDesc = list.sortedDescending()        // [9, 6, 5, 4, 3, 2, 1, 1]

// sortedBy
val names = listOf("Alice", "Bob", "Charlie")
val byLength = names.sortedBy { it.length }     // ["Bob", "Alice", "Charlie"]
val byLengthDesc = names.sortedByDescending { it.length }

// sortedWith
val sortedWith = list.sortedWith(compareBy { it })
val sortedWithDesc = list.sortedWith(compareByDescending { it })

// Для изменяемых списков (in-place)
val mutable = mutableListOf(3, 1, 4, 1, 5)
mutable.sort()                                  // изменяет исходный список
mutable.sortBy { it }
mutable.sortDescending()
mutable.sortWith(compareBy { it })
```

## Объединение и разделение

### Объединение

```kotlin
val list1 = listOf(1, 2, 3)
val list2 = listOf(4, 5, 6)

// plus
val combined = list1 + list2                    // [1, 2, 3, 4, 5, 6]
val withElement = list1 + 4                     // [1, 2, 3, 4]

// plusElement
val withElement2 = list1.plusElement(4)         // [1, 2, 3, 4]

// union (для Set)
val set1 = setOf(1, 2, 3)
val set2 = setOf(3, 4, 5)
val union = set1 union set2                    // {1, 2, 3, 4, 5}
```

### Разделение

```kotlin
val list = listOf(1, 2, 3, 4, 5, 6)

// chunked
val chunks = list.chunked(2)                    // [[1, 2], [3, 4], [5, 6]]
val chunks3 = list.chunked(3)                  // [[1, 2, 3], [4, 5, 6]]

// windowed
val windows = list.windowed(3)                   // [[1, 2, 3], [2, 3, 4], [3, 4, 5], [4, 5, 6]]
val windowsStep = list.windowed(3, step = 2)    // [[1, 2, 3], [3, 4, 5]]
```

## Работа с индексами

```kotlin
val list = listOf("a", "b", "c", "d", "e")

// Доступ по индексу
val element = list[2]                           // "c"
val safe = list.getOrNull(10)                  // null
val withDefault = list.getOrElse(10) { "default" }  // "default"

// Срезы
val sublist = list.subList(1, 3)               // ["b", "c"]
val slice = list.slice(0..2)                   // ["a", "b", "c"]
val sliceIndices = list.slice(listOf(0, 2, 4))  // ["a", "c", "e"]
```

## Уникальность

```kotlin
val list = listOf(1, 2, 2, 3, 3, 3, 4)

// distinct
val unique = list.distinct()                    // [1, 2, 3, 4]

// distinctBy
val names = listOf("Alice", "Bob", "Charlie", "Alice")
val uniqueByLength = names.distinctBy { it.length }  // ["Alice", "Bob", "Charlie"]

// toSet
val set = list.toSet()                          // {1, 2, 3, 4}
```

## Сравнение и проверка

### Проверка условий

```kotlin
val list = listOf(1, 2, 3, 4, 5)

// any
val hasEven = list.any { it % 2 == 0 }         // true
val hasNegative = list.any { it < 0 }          // false

// all
val allPositive = list.all { it > 0 }           // true
val allEven = list.all { it % 2 == 0 }         // false

// none
val noNegative = list.none { it < 0 }          // true
val noEven = list.none { it % 2 == 0 }         // false

// contains
val contains3 = list.contains(3)                // true
val contains10 = list.contains(10)              // false
val contains3Op = 3 in list                    // true
val contains10Op = 10 !in list                 // true

// containsAll
val containsAll = list.containsAll(listOf(2, 4))  // true
```

### Сравнение коллекций

```kotlin
val list1 = listOf(1, 2, 3)
val list2 = listOf(1, 2, 3)
val list3 = listOf(3, 2, 1)

// Равенство
list1 == list2                                 // true (порядок важен)
list1 == list3                                 // false

// Сравнение содержимого (без учета порядка)
list1.toSet() == list3.toSet()                // true
```

## Лучшие практики

### Цепочки операций

```kotlin
// Предпочитайте функциональные цепочки
val result = data
    .filter { it.isValid() }
    .map { it.transform() }
    .filter { it.isReady() }
    .take(10)
    .toList()

// Вместо императивного стиля
val result = mutableListOf<Transformed>()
for (item in data) {
    if (item.isValid()) {
        val transformed = item.transform()
        if (transformed.isReady()) {
            result.add(transformed)
            if (result.size >= 10) break
        }
    }
}
```

### Производительность

```kotlin
// Используйте sequences для больших коллекций
val large = (1..1_000_000).asSequence()
    .filter { it % 2 == 0 }
    .map { it * 2 }
    .take(10)
    .toList()

// Избегайте множественных проходов
// Плохо:
val filtered = list.filter { it > 0 }
val doubled = filtered.map { it * 2 }

// Хорошо:
val result = list.filter { it > 0 }.map { it * 2 }
```

### Идиоматичный **Kotlin**

```kotlin
// Используйте деструктуризацию
for ((index, value) in list.withIndex()) {
    println("$index: $value")
}

// Используйте when для работы с коллекциями
when {
    list.isEmpty() -> println("Empty")
    list.size == 1 -> println("Single: ${list.first()}")
    else -> println("Multiple: ${list.size}")
}

// Используйте расширения
fun <T> List<T>.secondOrNull(): T? = getOrNull(1)
val second = listOf(1, 2, 3).secondOrNull()  // 2
```

### Оптимизация

```kotlin
// Используйте take для раннего завершения
val result = sequence
    .filter { it > 0 }
    .take(10)  // останавливается после 10 элементов
    .toList()

// Используйте distinct для удаления дубликатов
val unique = list.distinct()

// Используйте partition для разделения
val (evens, odds) = list.partition { it % 2 == 0 }
```

## Продвинутые техники

### Работа с вложенными коллекциями

**Обработка вложенных структур данных требует специальных подходов:**

```kotlin
val nested = listOf(
    listOf(1, 2, 3),
    listOf(4, 5),
    listOf(6, 7, 8, 9)
)

// Flatten - разворачивание вложенных коллекций
val flattened = nested.flatten()  // [1, 2, 3, 4, 5, 6, 7, 8, 9]

// flatMap - разворачивание с трансформацией
val doubled = nested.flatMap { it.map { n -> n * 2 } }  // [2, 4, 6, 8, 10, 12, 14, 16, 18]

// Работа с глубоко вложенными структурами
data class Node(val value: Int, val children: List<Node>)

fun Node.flatten(): List<Int> {
    return listOf(value) + children.flatMap { it.flatten() }
}
```

Работа с вложенными коллекциями часто требует рекурсивных подходов или специальных операторов для разворачивания структур.

### Параллельная обработка

**Для больших коллекций может быть полезной параллельная обработка:**

```kotlin
import java.util.concurrent.ForkJoinPool

// Параллельная обработка через ForkJoinPool
fun <T, R> List<T>.parallelMap(
    pool: ForkJoinPool = ForkJoinPool.commonPool(),
    transform: (T) -> R
): List<R> {
    return pool.submit {
        this.parallelStream()
            .map(transform)
            .toList()
    }.get()
}

// Использование
val largeList = (1..1_000_000).toList()
val result = largeList.parallelMap { it * 2 }
```

Параллельная обработка может значительно ускорить обработку больших коллекций, но требует осторожности из-за накладных расходов на синхронизацию.

### Ленивые вычисления с **Sequences**

**Sequences** позволяют отложить вычисления до момента, когда результат действительно нужен:**

```kotlin
// Создание бесконечной последовательности
val infinite = generateSequence(1) { it + 1 }

// Ленивая фильтрация и трансформация
val result = infinite
    .filter { it % 2 == 0 }
    .map { it * 2 }
    .take(10)
    .toList()  // Вычисления происходят только здесь

// Создание последовательности с условием остановки
val fibonacci = generateSequence(1 to 1) { (a, b) -> b to (a + b) }
    .map { it.first }
    .take(10)
    .toList()
```

Ленивые вычисления позволяют работать с потенциально бесконечными последовательностями и оптимизировать использование памяти.

## Производительность и оптимизация

### Измерение производительности

**Важно измерять производительность операций для выявления узких мест:**

```kotlin
fun <T> measureTime(operation: () -> T): Pair<T, Long> {
    val startTime = System.nanoTime()
    val result = operation()
    val duration = System.nanoTime() - startTime
    return result to duration
}

// Использование
val (result, time) = measureTime {
    largeList.filter { it > 0 }.map { it * 2 }
}
println("Operation took ${time / 1_000_000}ms")
```

Измерение производительности помогает выявить узкие места и выбрать оптимальный подход для конкретной задачи.

### Оптимизация памяти

**Для больших коллекций важно оптимизировать использование памяти:**

```kotlin
// Используйте sequences для цепочек операций
val result = largeList.asSequence()
    .filter { it > 0 }
    .map { it * 2 }
    .take(1000)
    .toList()  // Создается только финальная коллекция

// Избегайте промежуточных коллекций
// Плохо:
val filtered = list.filter { it > 0 }  // Промежуточная коллекция
val mapped = filtered.map { it * 2 }   // Еще одна промежуточная коллекция

// Хорошо:
val result = list.asSequence()
    .filter { it > 0 }
    .map { it * 2 }
    .toList()  // Только одна финальная коллекция
```

Оптимизация памяти особенно важна для приложений, работающих с большими объемами данных или ограниченными ресурсами.

### Кэширование результатов

**Для дорогих операций может быть полезно кэширование:**

```kotlin
class CachedOperation<T, R>(private val operation: (T) -> R) {
    private val cache = mutableMapOf<T, R>()
    
    fun execute(input: T): R {
        return cache.getOrPut(input) {
            operation(input)
        }
    }
}

// Использование
val expensiveOperation = CachedOperation { n: Int ->
    // Дорогая операция
    (1..n).sum()
}

val result1 = expensiveOperation.execute(1000)  // Выполняется операция
val result2 = expensiveOperation.execute(1000)  // Используется кэш
```

Кэширование особенно полезно для операций, которые часто вызываются с одинаковыми параметрами.

## Работа с типами

### **Type-safe** операции

**Kotlin** позволяет создавать типобезопасные операции над коллекциями:**

```kotlin
// Операции, специфичные для типа
inline fun <reified T> List<*>.filterIsInstance(): List<T> {
    return filterIsInstance<T>()
}

// Использование
val mixed = listOf(1, "hello", 2, "world", 3)
val numbers: List<Int> = mixed.filterIsInstance<Int>()
val strings: List<String> = mixed.filterIsInstance<String>()
```

**Type-safe** операции предотвращают ошибки во время выполнения и делают код более безопасным.

### **Generic** операции

**Создание переиспользуемых операций с **generics**:**

```kotlin
// Универсальная функция для группировки и агрегации
fun <T, K, R> Collection<T>.groupAndAggregate(
    keySelector: (T) -> K,
    aggregator: (List<T>) -> R
): Map<K, R> {
    return groupBy(keySelector).mapValues { (_, values) ->
        aggregator(values)
    }
}

// Использование
val users = listOf(
    User("Alice", 25),
    User("Bob", 30),
    User("Alice", 28)
)

val byName = users.groupAndAggregate(
    keySelector = { it.name },
    aggregator = { it.map { u -> u.age }.average() }
)
```

**Generic** операции позволяют создавать переиспользуемый код, который работает с различными типами данных.

Этот файл содержит полное руководство по операциям над коллекциями в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности и работы с типами.

## Работа с параллельными коллекциями

### Параллельная обработка

**Использование параллельных потоков для обработки больших коллекций:**

```kotlin
import java.util.concurrent.ForkJoinPool

// Параллельная обработка через ForkJoinPool
fun <T, R> List<T>.parallelMap(
    pool: ForkJoinPool = ForkJoinPool.commonPool(),
    transform: (T) -> R
): List<R> {
    return pool.submit<List<R>> {
        this.parallelStream()
            .map(transform)
            .toList()
    }.get()
}

// Использование
val largeList = (1..1_000_000).toList()
val doubled = largeList.parallelMap { it * 2 }

// Параллельная фильтрация
fun <T> List<T>.parallelFilter(
    pool: ForkJoinPool = ForkJoinPool.commonPool(),
    predicate: (T) -> Boolean
): List<T> {
    return pool.submit<List<T>> {
        this.parallelStream()
            .filter(predicate)
            .toList()
    }.get()
}

// Параллельная агрегация
fun <T> List<T>.parallelReduce(
    pool: ForkJoinPool = ForkJoinPool.commonPool(),
    initial: T,
    operation: (T, T) -> T
): T {
    return pool.submit<T> {
        this.parallelStream()
            .reduce(initial) { acc, value -> operation(acc, value) }
            .orElse(initial)
    }.get()
}
```

Параллельная обработка может значительно ускорить обработку больших коллекций, но требует осторожности из-за накладных расходов на синхронизацию.

### Потокобезопасные коллекции

**Работа с потокобезопасными коллекциями для параллельной обработки:**

```kotlin
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

// Использование потокобезопасных коллекций
val concurrentMap = ConcurrentHashMap<String, Int>()
val copyOnWriteList = CopyOnWriteArrayList<Int>()

// Параллельная обработка с потокобезопасными коллекциями
fun processInParallel(items: List<Item>) {
    val results = ConcurrentHashMap<Int, List<Item>>()
    
    items.parallelStream().forEach { item ->
        val key = item.category
        results.compute(key) { _, value ->
            (value ?: emptyList()) + item
        }
    }
}

// Синхронизация доступа к коллекциям
class ThreadSafeCollection<T> {
    private val items = mutableListOf<T>()
    private val lock = Any()
    
    fun add(item: T) {
        synchronized(lock) {
            items.add(item)
        }
    }
    
    fun remove(item: T) {
        synchronized(lock) {
            items.remove(item)
        }
    }
    
    fun getAll(): List<T> {
        synchronized(lock) {
            return items.toList()
        }
    }
}
```

Потокобезопасные коллекции обеспечивают корректную работу в многопоточной среде и предотвращают состояния гонки.

## Оптимизация производительности коллекций

### Измерение и профилирование

**Измерение производительности операций для выявления узких мест:**

```kotlin
// Профилирование операций над коллекциями
class CollectionProfiler {
    fun <T, R> profileOperation(
        name: String,
        collection: Collection<T>,
        operation: (Collection<T>) -> R
    ): Pair<R, Long> {
        val startTime = System.nanoTime()
        val result = operation(collection)
        val duration = System.nanoTime() - startTime
        
        println("$name: ${duration / 1_000_000}ms")
        return result to duration
    }
}

// Использование
val profiler = CollectionProfiler()
val largeList = (1..1_000_000).toList()

val (result1, time1) = profiler.profileOperation("Filter + Map") {
    it.filter { it > 0 }.map { it * 2 }
}

val (result2, time2) = profiler.profileOperation("Sequence Filter + Map") {
    it.asSequence().filter { it > 0 }.map { it * 2 }.toList()
}
```

Профилирование операций помогает выявлять узкие места и выбирать оптимальные подходы для конкретных задач.

### Кэширование результатов операций

**Кэширование результатов дорогих операций:**

```kotlin
// Кэширование результатов операций
class CachedOperation<T, R>(
    private val operation: (Collection<T>) -> R
) {
    private var cache: Pair<Collection<T>, R>? = null
    
    fun execute(collection: Collection<T>): R {
        return if (cache?.first === collection && cache != null) {
            cache!!.second
        } else {
            val result = operation(collection)
            cache = collection to result
            result
        }
    }
    
    fun clearCache() {
        cache = null
    }
}

// Использование
val expensiveOperation = CachedOperation<List<Int>> { list ->
    list.filter { it > 0 }
        .map { it * 2 }
        .sorted()
        .distinct()
}

val result1 = expensiveOperation.execute(largeList)  // Выполняется операция
val result2 = expensiveOperation.execute(largeList)  // Используется кэш
```

Кэширование особенно полезно для операций, которые часто выполняются над одними и теми же данными.

## Специализированные операции

### Операции для работы с числами

**Специализированные операции для числовых коллекций:**

```kotlin
// Статистические операции
fun List<Int>.statistics(): Statistics {
    return Statistics(
        sum = this.sum(),
        average = this.average(),
        min = this.minOrNull() ?: 0,
        max = this.maxOrNull() ?: 0,
        median = this.sorted()[this.size / 2],
        mode = this.groupBy { it }.maxByOrNull { it.value.size }?.key ?: 0
    )
}

data class Statistics(
    val sum: Int,
    val average: Double,
    val min: Int,
    val max: Int,
    val median: Int,
    val mode: Int
)

// Использование
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
val stats = numbers.statistics()
println("Sum: ${stats.sum}, Average: ${stats.average}, Median: ${stats.median}")

// Корреляция между двумя списками
fun List<Double>.correlation(other: List<Double>): Double {
    require(this.size == other.size) { "Lists must have the same size" }
    
    val thisMean = this.average()
    val otherMean = other.average()
    
    val numerator = this.zip(other).sumOf { (x, y) ->
        (x - thisMean) * (y - otherMean)
    }
    
    val thisVariance = this.sumOf { (it - thisMean).pow(2) }
    val otherVariance = other.sumOf { (it - otherMean).pow(2) }
    
    return numerator / sqrt(thisVariance * otherVariance)
}

// Использование
val x = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
val y = listOf(2.0, 4.0, 6.0, 8.0, 10.0)
val correlation = x.correlation(y)  // ~1.0 (положительная корреляция)
```

Специализированные операции для чисел позволяют выполнять статистический анализ данных.

### Операции для работы со строками

**Специализированные операции для строковых коллекций:**

```kotlin
// Конкатенация строк
fun List<String>.joinWithSeparator(separator: String = ", "): String {
    return this.joinToString(separator)
}

// Фильтрация по паттерну
fun List<String>.filterByPattern(pattern: Regex): List<String> {
    return this.filter { pattern.matches(it) }
}

// Группировка по длине
fun List<String>.groupByLength(): Map<Int, List<String>> {
    return this.groupBy { it.length }
}

// Поиск общих подстрок
fun List<String>.commonPrefix(): String {
    if (isEmpty()) return ""
    
    val first = this[0]
    var prefixLength = first.length
    
    for (i in 1 until this.size) {
        var j = 0
        while (j < prefixLength && j < this[i].length && first[j] == this[i][j]) {
            j++
        }
        prefixLength = j
    }
    
    return first.substring(0, prefixLength)
}

// Использование
val strings = listOf("hello", "help", "helicopter")
val prefix = strings.commonPrefix()  // "he"
```

Специализированные операции для строк позволяют эффективно работать с текстовыми данными.

Этот файл содержит полное руководство по операциям над коллекциями в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, работы с типами, параллельной обработки, специализированных операций и работы с большими данными.

## Дополнительные операции

### Работа с предикатами

**Использование предикатов для фильтрации:**

```kotlin
// Комбинирование предикатов
fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean {
    return { this(it) && other(it) }
}

fun <T> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean {
    return { this(it) || other(it) }
}

fun <T> ((T) -> Boolean).not(): (T) -> Boolean {
    return { !this(it) }
}

// Использование
val isEven = { n: Int -> n % 2 == 0 }
val isPositive = { n: Int -> n > 0 }
val isEvenAndPositive = isEven.and(isPositive)
val isOddOrNegative = isEven.not().or(isPositive.not())

val numbers = listOf(-2, -1, 0, 1, 2, 3, 4, 5)
val filtered = numbers.filter(isEvenAndPositive)  // [2, 4]

// Создание предикатов из условий
fun <T> createPredicate(condition: (T) -> Boolean): (T) -> Boolean {
    return condition
}

val isLongString = createPredicate<String> { it.length > 10 }
val isShortString = isLongString.not()
```

Предикаты позволяют создавать переиспользуемые условия для фильтрации.

### Работа с трансформациями

**Продвинутые техники трансформации данных:**

```kotlin
// Трансформация с индексами
fun <T, R> List<T>.mapIndexedNotNull(transform: (Int, T) -> R?): List<R> {
    return this.mapIndexed(transform).filterNotNull()
}

// Трансформация с аккумулятором
fun <T, R> List<T>.mapAccumulate(
    initial: R,
    transform: (R, T) -> Pair<R, R>
): List<R> {
    var accumulator = initial
    return this.map { element ->
        val (newAcc, result) = transform(accumulator, element)
        accumulator = newAcc
        result
    }
}

// Использование
val numbers = listOf(1, 2, 3, 4, 5)
val runningProducts = numbers.mapAccumulate(1) { acc, value ->
    val product = acc * value
    product to product
}  // [1, 2, 6, 24, 120]
```

Трансформации позволяют эффективно преобразовывать данные с сохранением контекста.

Этот файл содержит полное руководство по операциям над коллекциями в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, работы с типами, параллельной обработки, специализированных операций, работы с большими данными, предикатами и трансформациями.

## Дополнительные операции

### Работа с агрегацией

**Продвинутые техники агрегации:**

```kotlin
// Множественная агрегация
fun <T> List<T>.aggregate(
    vararg aggregators: (List<T>) -> Any
): List<Any> {
    return aggregators.map { it(this) }
}

// Использование
val numbers = listOf(1, 2, 3, 4, 5)
val results = numbers.aggregate(
    { it.sum() },
    { it.average() },
    { it.maxOrNull() },
    { it.minOrNull() }
)
// [15, 3.0, 5, 1]

// Агрегация с группировкой
fun <T, K, V> List<T>.aggregateBy(
    keySelector: (T) -> K,
    valueSelector: (T) -> V,
    aggregator: (List<V>) -> V
): Map<K, V> {
    return this.groupBy(keySelector)
        .mapValues { (_, values) ->
            aggregator(values.map(valueSelector))
        }
}
```

Агрегация позволяет эффективно вычислять статистику по данным.

Этот файл содержит полное руководство по операциям над коллекциями в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, работы с типами, параллельной обработки, специализированных операций, работы с большими данными, предикатами, трансформациями и агрегацией.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Операции над коллекциями являются основой работы с данными в **Kotlin**. Понимание различных операций, от базовых фильтрации и трансформации до продвинутых техник агрегации, параллельной обработки и работы с предикатами, позволяет эффективно обрабатывать данные. Правильный выбор операций и их комбинирование помогает создавать читаемый и эффективный код.

Этот файл содержит полное руководство по операциям над коллекциями в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, работы с типами, параллельной обработки, специализированных операций, работы с большими данными, предикатами, трансформациями, агрегацией, заключение и дополнительные ресурсы.

## Практические примеры использования

### Обработка данных в цепочке

**Пример обработки данных с использованием цепочки операций:**

```kotlin
data class Product(val name: String, val price: Double, val category: String)

fun processProducts(products: List<Product>): Map<String, Double> {
    return products
        .filter { it.price > 100.0 }
        .groupBy { it.category }
        .mapValues { (_, products) ->
            products.map { it.price }.average()
        }
}
```

Цепочка операций делает код декларативным и читаемым.

### Трансформация и фильтрация

**Пример комбинирования трансформации и фильтрации:**

```kotlin
fun findExpensiveProducts(products: List<Product>, threshold: Double): List<String> {
    return products
        .filter { it.price > threshold }
        .map { it.name }
        .sorted()
}
```

Комбинирование операций позволяет эффективно обрабатывать данные.

### Работа с предикатами

**Пример создания и комбинирования предикатов:**

```kotlin
// Создание предикатов
fun <T> createPredicate(condition: (T) -> Boolean): (T) -> Boolean = condition

// Комбинирование предикатов
fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean {
    return { value -> this(value) && other(value) }
}

fun <T> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean {
    return { value -> this(value) || other(value) }
}

fun <T> ((T) -> Boolean).not(): (T) -> Boolean {
    return { value -> !this(value) }
}

// Использование
val isPositive = createPredicate<Int> { it > 0 }
val isEven = createPredicate<Int> { it % 2 == 0 }
val isPositiveAndEven = isPositive.and(isEven)
val isPositiveOrEven = isPositive.or(isEven)
val isNotPositive = isPositive.not()

val numbers = listOf(-2, -1, 0, 1, 2, 3, 4)
val filtered = numbers.filter(isPositiveAndEven)  // [2, 4]
```

Комбинирование предикатов позволяет создавать сложные условия фильтрации.

### Специализированные трансформации

**Пример специализированных трансформаций:**

```kotlin
// Трансформация с индексами
fun <T, R> List<T>.mapIndexedNotNull(transform: (Int, T) -> R?): List<R> {
    return this.mapIndexedNotNull { index, value -> transform(index, value) }
}

// Трансформация с накоплением
fun <T, R> List<T>.scan(initial: R, operation: (R, T) -> R): List<R> {
    val result = mutableListOf(initial)
    var accumulator = initial
    for (item in this) {
        accumulator = operation(accumulator, item)
        result.add(accumulator)
    }
    return result
}

// Использование
val numbers = listOf(1, 2, 3, 4, 5)
val withIndices = numbers.mapIndexedNotNull { index, value ->
    if (index % 2 == 0) value * 2 else null
}  // [2, 6, 10]
val runningSum = numbers.scan(0) { acc, value -> acc + value }  // [0, 1, 3, 6, 10, 15]
```

Специализированные трансформации предоставляют мощные инструменты для обработки данных.

### Практические примеры: Обработка больших наборов данных

```kotlin
// Обработка больших файлов через sequences
fun processLargeFile(filePath: String): Sequence<String> {
    return File(filePath)
        .bufferedReader()
        .lineSequence()
        .filter { it.isNotBlank() }
        .map { it.trim().uppercase() }
        .distinct()
}

// Параллельная обработка с chunking
suspend fun <T, R> List<T>.processInParallel(
    chunkSize: Int = 100,
    processor: suspend (T) -> R
): List<R> = coroutineScope {
    this@processInParallel
        .chunked(chunkSize)
        .flatMap { chunk ->
            chunk.map { item ->
                async { processor(item) }
            }
        }
        .awaitAll()
}
```

### Практические примеры: Фильтрация и трансформация данных

```kotlin
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean,
    val rating: Double
)

class ProductFilter(private val products: List<Product>) {
    // Поиск продуктов с фильтрами
    fun findProducts(
        minPrice: Double? = null,
        maxPrice: Double? = null,
        category: String? = null,
        minRating: Double? = null,
        inStockOnly: Boolean = false
    ): List<Product> {
        return products
            .filter { minPrice == null || it.price >= minPrice }
            .filter { maxPrice == null || it.price <= maxPrice }
            .filter { category == null || it.category == category }
            .filter { minRating == null || it.rating >= minRating }
            .filter { !inStockOnly || it.inStock }
    }
    
    // Сортировка с приоритетами
    fun sortByMultipleCriteria(): List<Product> {
        return products.sortedWith(
            compareBy<Product> { !it.inStock }
                .thenByDescending { it.rating }
                .thenBy { it.price }
        )
    }
    
    // Поиск похожих продуктов
    fun findSimilar(product: Product, limit: Int = 5): List<Product> {
        return products
            .filter { it.id != product.id && it.category == product.category }
            .sortedBy { 
                kotlin.math.abs(it.price - product.price) 
            }
            .take(limit)
    }
}
```

### Практические примеры: Агрегация данных

```kotlin
data class Transaction(
    val id: String,
    val amount: Double,
    val date: LocalDate,
    val type: TransactionType,
    val accountId: String
)

enum class TransactionType { DEBIT, CREDIT }

class TransactionAnalyzer(private val transactions: List<Transaction>) {
    // Общая сумма по типам
    fun totalByType(): Map<TransactionType, Double> {
        return transactions.groupingBy { it.type }
            .aggregate { _, accumulator: Double?, element, _ ->
                (accumulator ?: 0.0) + element.amount
            }
    }
    
    // Средняя сумма транзакций по месяцам
    fun averageByMonth(): Map<YearMonth, Double> {
        return transactions
            .groupBy { YearMonth.from(it.date) }
            .mapValues { (_, transactions) -> 
                transactions.map { it.amount }.average() 
            }
    }
    
    // Топ аккаунты по объему транзакций
    fun topAccounts(limit: Int = 10): List<Pair<String, Double>> {
        return transactions
            .groupBy { it.accountId }
            .mapValues { (_, transactions) -> 
                transactions.sumOf { it.amount } 
            }
            .toList()
            .sortedByDescending { it.second }
            .take(limit)
    }
    
    // Статистика по периодам
    fun statisticsByPeriod(period: Period): Map<String, TransactionStats> {
        return transactions
            .groupBy { transaction ->
                when (period) {
                    Period.DAY -> transaction.date.toString()
                    Period.WEEK -> "${transaction.date.year}-W${transaction.date.get(WeekFields.ISO.weekOfWeekBasedYear())}"
                    Period.MONTH -> YearMonth.from(transaction.date).toString()
                    Period.YEAR -> transaction.date.year.toString()
                }
            }
            .mapValues { (_, transactions) ->
                TransactionStats(
                    count = transactions.size,
                    total = transactions.sumOf { it.amount },
                    average = transactions.map { it.amount }.average(),
                    max = transactions.maxOf { it.amount },
                    min = transactions.minOf { it.amount }
                )
            }
    }
}

enum class Period { DAY, WEEK, MONTH, YEAR }

data class TransactionStats(
    val count: Int,
    val total: Double,
    val average: Double,
    val max: Double,
    val min: Double
)
```

Этот файл содержит полное руководство по операциям над коллекциями в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, работы с типами, параллельной обработки, специализированных операций, работы с большими данными, предикатами, трансформациями, агрегацией, практические примеры использования для обработки больших файлов, фильтрации продуктов, анализа транзакций, включая работу с предикатами и специализированные трансформации, заключение и дополнительные ресурсы.

## Заключение

Операции над коллекциями являются основой работы с данными в **Kotlin**. Понимание различных операций, от базовых фильтрации и трансформации до продвинутых техник агрегации, параллельной обработки и работы с предикатами, позволяет эффективно обрабатывать данные. Правильный выбор операций и их комбинирование помогает создавать читаемый и эффективный код. Использование специализированных операций для числовых коллекций, работа с большими данными через **sequence** и параллельная обработка критичны для создания производительных приложений.

## Дополнительные ресурсы

**Для дальнейшего изучения операций над коллекциями в **Kotlin** рекомендуется:**

- **Kotlin Collections Documentation**: **https**://**kotlinlang.org**/**docs**/**collections-overview.html**
- **Collection Operations**: **https**://**kotlinlang.org**/**docs**/**collection-operations.html**

