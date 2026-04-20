---
title: "Kotlin Collections: Map"
description: "Кратко: руководство по работе со словарями в Kotlin: Map, MutableMap, HashMap, TreeMap, LinkedHashMap, операции и лучшие практики."
tags:
  - languages
  - kotlin
  - kotlin-collections-map
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Kotlin Collections: Map

Кратко: руководство по работе со словарями в **Kotlin**: **Map**, **MutableMap**, **HashMap**, **TreeMap**, **LinkedHashMap**, операции и лучшие практики.

## Полезные ссылки

### Официальная документация
- [Kotlin Collections Overview](https://kotlinlang.org/docs/collections-overview.html)
- [Kotlin Map API](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)

### Обучающие материалы
- [Kotlin Collections Guide](https://www.baeldung.com/kotlin/collections-api)

### См. также
- [[kotlin-basics|Основы Kotlin]]
- [[kotlin-collections-list|Списки (List)]]
- [[kotlin-collections-set|Множества (Set)]]
- [[kotlin-collections-operations|Операции над коллекциями]]
- [[java-collections-map|Java Collections Map]]

## Содержание

- [Введение в **Map**](#введение-в-map)
  - [Основные характеристики](#основные-характеристики)
  - [Интерфейсы](#интерфейсы)
  - [Реализации](#реализации)
- [Создание словарей](#создание-словарей)
  - [Пустые словари](#пустые-словари)
  - [Создание с элементами](#создание-с-элементами)
  - [Создание из других коллекций](#создание-из-других-коллекций)
  - [Создание с помощью **builder**](#создание-с-помощью-builder)
- [Неизменяемые и изменяемые словари](#неизменяемые-и-изменяемые-словари)
  - [Неизменяемые словари (**Map**)](#неизменяемые-словари-map)
  - [Изменяемые словари (**MutableMap**)](#изменяемые-словари-mutablemap)
  - [Преобразование между типами](#преобразование-между-типами)
- [**HashMap**](#hashmap)
  - [Характеристики](#характеристики)
  - [Создание **HashMap**](#создание-hashmap)
  - [Операции с **HashMap**](#операции-с-hashmap)
- [**TreeMap**](#treemap)
  - [Создание **TreeMap**](#создание-treemap)
  - [Операции с **TreeMap**](#операции-с-treemap)
- [**LinkedHashMap**](#linkedhashmap)
  - [Создание **LinkedHashMap**](#создание-linkedhashmap)
- [Основные операции](#основные-операции)
  - [Доступ к элементам](#доступ-к-элементам)
  - [Итерация](#итерация)
  - [Размер и пустота](#размер-и-пустота)
- [Работа с ключами и значениями](#работа-с-ключами-и-значениями)
  - [Получение ключей и значений](#получение-ключей-и-значений)
  - [Изменяемые ключи и значения](#изменяемые-ключи-и-значения)
- [Преобразование словарей](#преобразование-словарей)
  - [Map (трансформация)](#map-трансформация)
  - [Фильтрация](#фильтрация)
- [Фильтрация и поиск](#фильтрация-и-поиск)
  - [Поиск элементов](#поиск-элементов)
- [Группировка и агрегация](#группировка-и-агрегация)
  - [Группировка](#группировка)
  - [Агрегация](#агрегация)
  - [Подсчет](#подсчет)
- [Слияние словарей](#слияние-словарей)
  - [Объединение](#объединение)
  - [Обновление значений](#обновление-значений)
- [Производительность](#производительность)
  - [Сложность операций](#сложность-операций)
  - [Выбор реализации](#выбор-реализации)
- [Лучшие практики](#лучшие-практики)
  - [Выбор типа словаря](#выбор-типа-словаря)
  - [**Null safety**](#null-safety)
  - [Функциональный стиль](#функциональный-стиль)
  - [Идиоматичный **Kotlin**](#идиоматичный-kotlin)
  - [Работа с парами](#работа-с-парами)
- [Продвинутые техники работы с **Map**](#продвинутые-техники-работы-с-map)
  - [Операции с несколькими словарями](#операции-с-несколькими-словарями)
  - [Многоуровневые словари](#многоуровневые-словари)
  - [Словари как кэш](#словари-как-кэш)
- [Производительность **Map**](#производительность-map)
  - [Выбор правильной реализации](#выбор-правильной-реализации)
  - [Оптимизация операций **Map**](#оптимизация-операций-map)
- [Реальные примеры использования **Map**](#реальные-примеры-использования-map)
  - [**Map** для конфигураций](#map-для-конфигураций)
  - [**Map** для кэширования](#map-для-кэширования)
- [Продвинутые операции **Map**](#продвинутые-операции-map)
  - [Трансформация и фильтрация **Map**](#трансформация-и-фильтрация-map)
  - [Работа с вложенными **Map**](#работа-с-вложенными-map)
  - [Операции с несколькими **Map**](#операции-с-несколькими-map)
  - [Специализированные **Map** операции](#специализированные-map-операции)
- [Дополнительные техники **Map**](#дополнительные-техники-map)
  - [Работа с **default** значениями](#работа-с-default-значениями)
  - [Работа с мутабельными **Map**](#работа-с-мутабельными-map)
  - [Работа с биективными **Map**](#работа-с-биективными-map)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Практические примеры использования](#практические-примеры-использования)
  - [Реализация кэша](#реализация-кэша)
  - [Группировка данных](#группировка-данных)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Map**

**Map** в **Kotlin** — это коллекция пар ключ-значение, где каждый ключ уникален. Словари не наследуются от **Collection**, но являются частью **Collections Framework**.

### Основные характеристики

- **Уникальные ключи**: каждый ключ встречается только один раз
- **Пары ключ-значение**: хранит ассоциации между ключами и значениями
- **Порядок**: зависит от реализации (**HashMap — неупорядочен, `TreeMap` - отсортирован, `LinkedHashMap` - порядок вставки**)
- **Null safety**: может содержать **null** ключи и значения (**в зависимости от типа**)

### Интерфейсы

```kotlin
// Неизменяемый словарь
interface Map<K, out V>

// Изменяемый словарь
interface MutableMap<K, V>
```

### Реализации

- **HashMap**: неупорядоченный словарь на основе хеш-таблицы
- **TreeMap**: отсортированный словарь на основе красно-черного дерева
- **LinkedHashMap**: словарь с сохранением порядка вставки

## Создание словарей

### Пустые словари

```kotlin
// Неизменяемый пустой словарь
val emptyMap: Map<String, Int> = emptyMap()
val emptyMap2 = mapOf<String, Int>()

// Изменяемый пустой словарь
val mutableMap = mutableMapOf<String, Int>()
val hashMap = HashMap<String, Int>()
```

### Создание с элементами

```kotlin
// Неизменяемый словарь (mapOf)
val map = mapOf("a" to 1, "b" to 2, "c" to 3)
val map2 = mapOf(
    Pair("a", 1),
    Pair("b", 2),
    Pair("c", 3)
)

// Изменяемый словарь (mutableMapOf)
val mutableMap = mutableMapOf("a" to 1, "b" to 2)
mutableMap["c"] = 3

// HashMap напрямую
val hashMap = HashMap<String, Int>()
hashMap["a"] = 1
hashMap["b"] = 2

// Инициализация с начальной емкостью
val mapWithCapacity = HashMap<String, Int>(20)
```

### Создание из других коллекций

```kotlin
// Из списка пар
val pairs = listOf("a" to 1, "b" to 2, "c" to 3)
val mapFromPairs = pairs.toMap()

// Из двух списков (ключи и значения)
val keys = listOf("a", "b", "c")
val values = listOf(1, 2, 3)
val mapFromLists = keys.zip(values).toMap()

// Из списка с группировкой
val list = listOf("apple", "banana", "apricot")
val mapByLength = list.associateBy { it.length }
// {5: "apricot", 6: "banana", 9: "apricot"} (последнее значение выигрывает)

// С трансформацией значения
val mapWithTransform = list.associateWith { it.length }
// {"apple": 5, "banana": 6, "apricot": 7}
```

### Создание с помощью **builder**

```kotlin
// buildMap — создание неизменяемой Map через builder
val map = buildMap {
    put("a", 1)
    put("b", 2)
    put("c", 3)
}
```

## Неизменяемые и изменяемые словари

### Неизменяемые словари (**Map**)

```kotlin
val immutableMap = mapOf("a" to 1, "b" to 2, "c" to 3)

// Эти операции НЕ изменяют исходный словарь, а возвращают новый
val newMap = immutableMap + ("d" to 4)
val anotherMap = immutableMap - "b"

// immutableMap остается {"a": 1, "b": 2, "c": 3}
```

### Изменяемые словари (**MutableMap**)

```kotlin
val mutableMap = mutableMapOf("a" to 1, "b" to 2, "c" to 3)

// Эти операции изменяют исходный словарь
mutableMap["d"] = 4
mutableMap.put("e", 5)
mutableMap.remove("b")
mutableMap += "f" to 6
mutableMap -= "c"

// mutableMap теперь {"a": 1, "d": 4, "e": 5, "f": 6}
```

### Преобразование между типами

```kotlin
val mutable = mutableMapOf("a" to 1, "b" to 2)
val immutable: Map<String, Int> = mutable.toMap()

// Создание копии для безопасного изменения
val copy = mutable.toMutableMap()
copy["c"] = 3 // не влияет на mutable
```

## **HashMap**

**HashMap** — это реализация **MutableMap** на основе хеш-таблицы. Это наиболее распространенная реализация словаря в **Kotlin**, так как она обеспечивает оптимальную производительность для большинства операций со словарями при отсутствии требований к порядку элементов.

**HashMap** использует хеш-таблицу для хранения пар ключ-значение. Ключи хешируются, и хеш-код используется для быстрого поиска соответствующих значений. Это делает **HashMap** идеальным выбором для большинства случаев использования словарей.

### Характеристики

- **Уникальные ключи**: каждый ключ встречается только один раз. При попытке добавить пару с ключом, который уже существует в словаре, старое значение перезаписывается новым. Это фундаментальное свойство словарей — каждому ключу соответствует ровно одно значение.

- **Неупорядоченность**: порядок элементов не гарантируется. Элементы могут храниться в произвольном порядке, который может изменяться при добавлении или удалении элементов. Это связано с внутренней структурой хеш-таблицы и процессом рехеширования при расширении.

- **Быстрый доступ**: `O(1)` в среднем для добавления, удаления, поиска. Хеш-таблица позволяет находить значения по ключу за постоянное время в среднем случае. В худшем случае (**при большом количестве коллизий**) сложность может деградировать до `O(n)`, но на практике это редко происходит.

- **Null элементы**: может содержать один **null** ключ и множество **null** значений. Это особенность реализации **HashMap** в **Kotlin**/**Java**. **Null** ключ обрабатывается как специальное значение с хеш-кодом 0.

### Создание **HashMap**

```kotlin
// Пустой HashMap
val map1 = HashMap<String, Int>()

// С начальной емкостью
val map2 = HashMap<String, Int>(20)

// С начальной емкостью и load factor
val map3 = HashMap<String, Int>(16, 0.75f)

// Из другой коллекции
val map4 = HashMap(mapOf("a" to 1, "b" to 2))

// С помощью hashMapOf
val map5 = hashMapOf("a" to 1, "b" to 2, "c" to 3)
```

### Операции с **HashMap**

```kotlin
val map = hashMapOf("a" to 1, "b" to 2, "c" to 3)

// Добавление/изменение элементов
map["d"] = 4                    // добавление
map["a"] = 10                   // изменение существующего
map.put("e", 5)                 // то же самое
map.putAll(mapOf("f" to 6, "g" to 7))

// Удаление элементов
map.remove("b")                 // удаление по ключу
map.remove("a", 1)              // удаление только если значение совпадает
map.clear()                     // все элементы

// Получение значений
val value = map["a"]            // получение значения
val withDefault = map.getOrDefault("x", 0) // значение по умолчанию
```

## **TreeMap**

**TreeMap** — это реализация **MutableMap** на основе красно-черного дерева. Ключи автоматически сортируются.

### Характеристики

- **Сортировка**: ключи всегда отсортированы
- **Уникальные ключи**: гарантирует отсутствие дубликатов ключей
- **Логарифмический доступ**: `O(**log n**)` для добавления, удаления, поиска
- **Требует Comparable**: ключи должны быть **Comparable** или нужен **Comparator**

### Создание **TreeMap**

```kotlin
// TreeMap из Java — сортировка по ключам (красно-чёрное дерево)
import java.util.TreeMap

// Пустой TreeMap (натуральный порядок)
val map1 = TreeMap<String, Int>()

// С Comparator
val map2 = TreeMap<String, Int>(compareByDescending { it.length })

// Из другой коллекции
val map3 = TreeMap(mapOf("c" to 3, "a" to 1, "b" to 2))

// TreeMap автоматически сортирует ключи
val sorted = TreeMap(mapOf(5 to "five", 2 to "two", 8 to "eight"))
// Результат: {2: "two", 5: "five", 8: "eight"}
```

### Операции с **TreeMap**

```kotlin
val map = TreeMap<Int, String>()

map[5] = "five"
map[2] = "two"
map[8] = "eight"
// map = {2: "two", 5: "five", 8: "eight"}

// Навигационные операции
map.firstKey()                  // 2 (минимальный ключ)
map.lastKey()                   // 8 (максимальный ключ)
map.lowerKey(5)                 // 2 (меньше чем 5)
map.higherKey(5)                // 8 (больше чем 5)
map.floorKey(5)                  // 5 (меньше или равно 5)
map.ceilingKey(5)                // 5 (больше или равно 5)

// Подсловари
map.headMap(5)                   // {2: "two"} (ключи меньше 5)
map.tailMap(5)                   // {5: "five", 8: "eight"} (ключи >= 5)
map.subMap(2, 8)                 // {2: "two", 5: "five"} (от 2 включительно до 8 исключительно)
```

## **LinkedHashMap**

**LinkedHashMap** — это реализация **MutableMap**, которая сохраняет порядок вставки элементов.

### Характеристики

- **Порядок вставки**: сохраняет порядок, в котором элементы были добавлены
- **Уникальные ключи**: гарантирует отсутствие дубликатов ключей
- **Быстрый доступ**: `O(1)` в среднем для добавления, удаления, поиска
- **Больше памяти**: требует больше памяти, чем **HashMap**

### Создание **LinkedHashMap**

```kotlin
// LinkedHashMap — порядок итерации совпадает с порядком вставки
import java.util.LinkedHashMap

// Пустой LinkedHashMap
val map1 = LinkedHashMap<String, Int>()

// С начальной емкостью
val map2 = LinkedHashMap<String, Int>(20)

// Из другой коллекции
val map3 = LinkedHashMap(mapOf("a" to 1, "b" to 2))

// LinkedHashMap сохраняет порядок вставки
val ordered = LinkedHashMap<String, Int>()
ordered["c"] = 3
ordered["a"] = 1
ordered["b"] = 2
// Результат: {"c": 3, "a": 1, "b": 2} (порядок сохранен)
```

## Основные операции

Работа со словарями в **Kotlin** предоставляет множество способов доступа к данным, модификации значений и итерации по элементам. Понимание различных методов работы со словарями критично для эффективной работы с ассоциативными структурами данных.

### Доступ к элементам

Доступ к значениям в словаре осуществляется по ключу. **Kotlin** предоставляет несколько способов получения значений, каждый из которых имеет свои особенности в плане безопасности и удобства использования.

```kotlin
val map = mapOf("a" to 1, "b" to 2, "c" to 3)

// Получение значения по ключу
val value = map["a"]             // 1
val value2 = map.get("b")        // 2
```

Оператор `[]` является синтаксическим сахаром для метода `**get**()` и предоставляет наиболее удобный способ доступа к значениям. Оба метода возвращают **nullable** тип, так как ключ может отсутствовать в словаре. Если ключ отсутствует, возвращается `**null**`. Для безопасной работы с отсутствующими ключами используются методы `**getOrDefault**()` или `**getOrElse**()`, которые позволяют указать значение по умолчанию.

// Безопасный доступ
**val safe** = **map.`getOrDefault`(**"x", 0**)      // 0 (**если ключ отсутствует**)
**val orElse** = **map.`getOrElse`(**"x"**) { 0 }    // 0 (**с лямбдой**)
**val orNull** = **map**["x"]                    // **null** (**если тип nullable**)

// Проверка наличия ключа
**map.`containsKey`(**"a"**)            // **true**
"a" in **map**                       // **true**
"a" !in **map**                      // **false**

// Проверка наличия значения
**map.containsValue**(2)             // **true**
```text

### Итерация

```kotlin
val map = `mapOf`("a" `to 1`, "b" `to 2`, "c" `to 3`)

// Итерация по парам
for ((key, value) in map) {
    println("$key: $value")
}

// Итерация по ключам
for (key in `map.keys`) {
    println(key)
}

// Итерация по значениям
for (value in `map.values`) {
    println(value)
}

// `ForEach`
map.`forEach` { (key, value) ->
    println("$key: $value")
}

// Итератор
val `iterator` = `map.iterator`()
while (`iterator`.`hasNext()`) {
    val entry = `iterator.next`()
    println("${`entry.key`}: ${`entry.value`}")
}
```text

### Размер и пустота

```kotlin
val map = `mapOf`("a" `to 1`, "b" `to 2`, "c" `to 3`)

// Размер
`map.size`                         // 3
`map.count`()                      // 3

// Проверка пустоты
map.isEmpty()   // false
map.isNotEmpty() // true
```text

## Работа с ключами и значениями

### Получение ключей и значений

```kotlin
val map = `mapOf`("a" `to 1`, "b" `to 2`, "c" `to 3`)

// Ключи
val keys = `map.keys`              // Set {"a", "b", "c"}
val keysList = map.keys.toList() // List ["a", "b", "c"]

// Значения
val values = map.values   // Collection [1, 2, 3]
val valuesList = map.values.toList()   // List [1, 2, 3]

// Пары
val entries = map.entries   // Set<Map.Entry>
```text

### Изменяемые ключи и значения

```kotlin
val `mutableMap` = `mutableMapOf`("a" `to 1`, "b" `to 2`)

// Изменяемый набор ключей
val keys = mutableMap.keys   // MutableSet (изменения отражаются в map)
`keys.remove`("a")                 // также удалит из map

// Изменяемая коллекция значений
val values = mutableMap.values   // MutableCollection (изменения отражаются в map)
```text

## Преобразование словарей

Преобразование словарей позволяет трансформировать ключи, значения или пары ключ-значение. Это мощная операция, которая позволяет адаптировать структуру данных под различные нужды без изменения исходного словаря.

### Map (трансформация)

Операции преобразования словарей различаются в зависимости от того, что нужно трансформировать: только значения, только ключи, или и то, и другое. Каждая операция создает новый словарь, сохраняя структуру исходного.

```kotlin
val map = `mapOf`("a" `to 1`, "b" `to 2`, "c" `to 3`)

// Преобразование значений
val doubled = map.`mapValues` { `it.value` * 2 }    // {"a": 2, "b": 4, "c": 6}
```text

Метод `mapValues` применяет функцию преобразования к каждому значению словаря, создавая новый словарь с теми же ключами, но с преобразованными значениями. Это полезно, когда нужно изменить значения, сохранив структуру ключей. Функция получает доступ к паре через параметр `it`, где `it.key` - это ключ, а `it.value` - текущее значение.

// Преобразование ключей
val uppercased = map.mapKeys { it.key.uppercase() } // {"A": 1, "B": 2, "C": 3}

// Преобразование пар
val transformed = map.map { (key, value) ->
    key.uppercase() to value * 2
}.toMap()                                       // {"A": 2, "B": 4, "C": 6}
```

### Фильтрация

```kotlin
val map = mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4)

// Фильтр по ключу
val filteredByKey = map.filterKeys { it != "b" } // {"a": 1, "c": 3, "d": 4}

// Фильтр по значению
val filteredByValue = map.filterValues { it > 2 } // {"c": 3, "d": 4}

// Фильтр по паре
val filtered = map.filter { (key, value) ->
    key != "b" && value > 2
}                                               // {"c": 3, "d": 4}
```

## Фильтрация и поиск

### Поиск элементов

```kotlin
val map = mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4)

// Найти ключ по значению
val key = map.entries.find { it.value == 3 }?.key // "c"
val keys = map.filterValues { it > 2 }.keys        // {"c", "d"}

// Найти значение по условию
val value = map.values.find { it > 2 }            // 3

// Проверка условий
map.any { (_, value) -> value > 3 }              // true
map.all { (_, value) -> value > 0 }              // true
map.none { (_, value) -> value > 10 }            // true
```

## Группировка и агрегация

### Группировка

```kotlin
val list = listOf("apple", "banana", "apricot", "blueberry")

// Группировка по первому символу
val grouped = list.groupBy { it[0] }
// {'a': ["apple", "apricot"], 'b': ["banana", "blueberry"]}

// Группировка с трансформацией значения
val byLength = list.groupBy(
    keySelector = { it.length },
    valueTransform = { it.uppercase() }
)
```

### Агрегация

```kotlin
val map = mapOf("a" to 1, "b" to 2, "c" to 3, "d" to 4)

// Сумма значений
val sum = map.values.sum()                      // 10
val sumBy = map.values.sumOf { it * 2 }         // 20

// Среднее
val average = map.values.average()              // 2.5

// Минимум и максимум
val minKey = map.minByOrNull { it.value }?.key // "a"
val maxKey = map.maxByOrNull { it.value }?.key // "d"
val minValue = map.values.minOrNull()          // 1
val maxValue = map.values.maxOrNull()          // 4
```

### Подсчет

```kotlin
val map = mapOf("a" to 1, "b" to 2, "c" to 2, "d" to 3)

// Подсчет значений
val counts = map.values.groupingBy { it }.eachCount()
// {1: 1, 2: 2, 3: 1}

// Количество элементов по условию
val count = map.count { (_, value) -> value > 2 } // 1
```

## Слияние словарей

### Объединение

```kotlin
val map1 = mapOf("a" to 1, "b" to 2)
val map2 = mapOf("b" to 3, "c" to 4)

// Объединение (значения из map2 перезаписывают map1)
val merged = map1 + map2                        // {"a": 1, "b": 3, "c": 4}

// Слияние с функцией
val merged = map1.merge(map2) { old, new -> old + new }
// {"a": 1, "b": 5, "c": 4} (b: 2 + 3 = 5)
```

### Обновление значений

```kotlin
val mutableMap = mutableMapOf("a" to 1, "b" to 2)

// Обновление существующего значения
mutableMap["a"] = 10
mutableMap.put("a", 10)

// Обновление с функцией
mutableMap.compute("a") { _, value -> (value ?: 0) + 1 } // увеличивает на 1
mutableMap.computeIfPresent("b") { _, value -> value + 1 }
mutableMap.computeIfAbsent("c") { 0 }           // добавляет если отсутствует

// Обновление всех значений
mutableMap.replaceAll { _, value -> value * 2 }
```

## Производительность

### Сложность операций

| Операция | **HashMap** | **TreeMap** | **LinkedHashMap** |
|----------|---------|---------|---------------|
| Добавление | `O(1)` | `O(**log n**)` | `O(1)` |
| Удаление | `O(1)` | `O(**log n**)` | `O(1)` |
| Поиск | `O(1)` | `O(**log n**)` | `O(1)` |
| Итерация | `O(n)` | `O(n)` | `O(n)` |
| Минимум/Максимум ключа | `O(n)` | `O(1)` | `O(n)` |

### Выбор реализации

```kotlin
// Используйте HashMap когда:
// - Нужна уникальность ключей
// - Порядок не важен
// - Нужна максимальная производительность
val hashMap = hashMapOf("a" to 1, "b" to 2)

// Используйте TreeMap когда:
// - Нужна сортировка ключей
// - Нужны навигационные операции (lowerKey, higherKey, etc.)
// - Готовы пожертвовать скоростью ради порядка
val treeMap = TreeMap<String, Int>()
treeMap["c"] = 3
treeMap["a"] = 1
treeMap["b"] = 2

// Используйте LinkedHashMap когда:
// - Нужен порядок вставки
// - Нужна уникальность ключей
// - Готовы использовать немного больше памяти
val linkedMap = LinkedHashMap<String, Int>()
linkedMap["c"] = 3
linkedMap["a"] = 1
linkedMap["b"] = 2
```

## Лучшие практики

### Выбор типа словаря

```kotlin
// Используйте mapOf для неизменяемых словарей
val config = mapOf("host" to "localhost", "port" to 8080)

// Используйте mutableMapOf когда нужны изменения
val cache = mutableMapOf<String, Any>()

// Используйте HashMap с начальной емкостью для больших коллекций
val largeMap = HashMap<String, Data>(10_000)
```

### **Null safety**

```kotlin
// Словарь может содержать null значения
val nullableMap: Map<String, Int?> = mapOf("a" to 1, "b" to null)

// Безопасный доступ
val value = nullableMap["b"] ?: 0

// Фильтрация null
val nonNull = nullableMap.filterValues { it != null }
```

### Функциональный стиль

```kotlin
// Предпочитайте функциональные операции
val result = map
    .filter { (_, value) -> value > 0 }
    .mapValues { (_, value) -> value * 2 }
    .toMap()

// Вместо императивного стиля
val result = mutableMapOf<String, Int>()
for ((key, value) in map) {
    if (value > 0) {
        result[key] = value * 2
    }
}
```

### Производительность

```kotlin
// Используйте associateBy для группировки
val users = listOf(User("Alice", 25), User("Bob", 30))
val byName = users.associateBy { it.name }      // O(n)

// Избегайте множественных проходов
// Плохо:
val filtered = map.filter { it.value > 0 }
val doubled = filtered.mapValues { it.value * 2 }

// Хорошо:
val result = map.filter { it.value > 0 }
    .mapValues { it.value * 2 }
```

### Идиоматичный **Kotlin**

```kotlin
// Используйте деструктуризацию
for ((key, value) in map) {
    println("$key: $value")
}

// Используйте when для работы со словарями
when {
    map.isEmpty() -> println("Empty")
    map.size == 1 -> println("Single: ${map.values.first()}")
    else -> println("Multiple: ${map.size}")
}

// Используйте расширения
fun <K, V> Map<K, V>.getOrThrow(key: K): V {
    return get(key) ?: throw NoSuchElementException("Key $key not found")
}

val value = map.getOrThrow("a")
```

### Работа с парами

```kotlin
// Создание пар
val pair = "a" to 1
val pair2 = Pair("b", 2)

// Деструктуризация
val (key, value) = pair

// Преобразование в список пар
val pairs = map.toList()                        // List<Pair<K, V>>

// Создание словаря из пар
val mapFromPairs = listOf("a" to 1, "b" to 2).toMap()
```

## Продвинутые техники работы с **Map**

### Операции с несколькими словарями

**Работа с несколькими словарями одновременно:**

```kotlin
val map1 = mapOf("a" to 1, "b" to 2, "c" to 3)
val map2 = mapOf("c" to 30, "d" to 4, "e" to 5)

// Объединение словарей
val merged = map1 + map2  // {a=1, b=2, c=30, d=4, e=5}
val merged2 = map1.toMutableMap().apply {
    putAll(map2)
}

// Объединение с функцией для разрешения конфликтов
val mergedWithFunction = (map1.asSequence() + map2.asSequence())
    .groupBy({ it.key }, { it.value })
    .mapValues { (_, values) -> values.sum() }  // Суммирование значений

// Пересечение словарей
val intersection = map1.filterKeys { it in map2 }

// Разность словарей
val difference = map1.filterKeys { it !in map2 }
```

Операции с несколькими словарями позволяют создавать сложные преобразования данных и объединять информацию из различных источников.

### Многоуровневые словари

**Работа с вложенными словарями:**

```kotlin
// Создание многоуровневого словаря
val nested = mapOf(
    "users" to mapOf(
        "alice" to mapOf("age" to 25, "city" to "NYC"),
        "bob" to mapOf("age" to 30, "city" to "LA")
    )
)

// Доступ к вложенным значениям
val aliceAge = nested["users"]?.get("alice")?.get("age")

// Безопасный доступ с default значениями
val aliceAgeSafe = nested
    .getOrDefault("users", emptyMap())
    .getOrDefault("alice", emptyMap())
    .getOrDefault("age", 0)

// Использование extension функции для безопасного доступа
fun <K, V> Map<K, V>.nestedGet(vararg keys: K): V? {
    var current: Any? = this
    for (key in keys) {
        current = when (current) {
            is Map<*, *> -> current[key]
            else -> return null
        }
    }
    @Suppress("UNCHECKED_CAST")
    return current as? V
}

val aliceAgeNested = nested.nestedGet("users", "alice", "age")
```

Многоуровневые словари позволяют создавать иерархические структуры данных, что полезно для конфигураций и сложных данных.

### Словари как кэш

**Использование словарей для кэширования:**

```kotlin
// Простой кэш
class SimpleCache<K, V> {
    private val cache = mutableMapOf<K, V>()

    fun get(key: K, compute: () -> V): V {
        return cache.getOrPut(key, compute)
    }

    fun clear() {
        cache.clear()
    }
}

// Кэш с TTL (Time To Live)
class TTLCache<K, V>(private val ttlMillis: Long) {
    data class CacheEntry<V>(val value: V, val timestamp: Long)
    private val cache = mutableMapOf<K, CacheEntry<V>>()

    fun get(key: K, compute: () -> V): V {
        val now = System.currentTimeMillis()
        val entry = cache[key]

        return if (entry != null && now - entry.timestamp < ttlMillis) {
            entry.value
        } else {
            val value = compute()
            cache[key] = CacheEntry(value, now)
            value
        }
    }

    fun cleanup() {
        val now = System.currentTimeMillis()
        cache.entries.removeIf { now - it.value.timestamp >= ttlMillis }
    }
}
```

Словари идеально подходят для реализации кэшей различных типов, что улучшает производительность приложения.

## Производительность **Map**

### Выбор правильной реализации

**Различные реализации **Map** имеют разные характеристики производительности:**

```kotlin
// HashMap - O(1) для большинства операций, неупорядочен
val hashMap = HashMap<String, Int>()
hashMap["key"] = 1  // O(1)
hashMap["key"]  // O(1)

// LinkedHashMap - O(1) для большинства операций, сохраняет порядок вставки
val linkedHashMap = LinkedHashMap<String, Int>()
linkedHashMap["key"] = 1  // O(1), сохраняет порядок

// TreeMap - O(log n) для операций, отсортирован по ключам
val treeMap = TreeMap<String, Int>()
treeMap["key"] = 1  // O(log n), но отсортирован
```

Выбор правильной реализации **Map** зависит от требований к производительности и необходимости в упорядочивании.

### Оптимизация операций **Map**

**Оптимизация операций со словарями для лучшей производительности:**

```kotlin
// Использование начальной емкости
val largeMap = HashMap<String, Int>(10000)  // Избегает перераспределения

// Batch операции
fun <K, V> Map<K, V>.batchGet(keys: Collection<K>): Map<K, V> {
    return keys.mapNotNull { key ->
        get(key)?.let { key to it }
    }.toMap()
}

// Оптимизация итерации
val map = mapOf("a" to 1, "b" to 2, "c" to 3)
// Хорошо - прямое обращение к entrySet
for ((key, value) in map) {
    println("$key: $value")
}

// Плохо - множественный доступ
for (key in map.keys) {
    println("$key: ${map[key]}")  // Два обращения к словарю
}
```

Оптимизация операций **Map** улучшает производительность, особенно при работе с большими словарями или частыми операциями.

Этот файл содержит полное руководство по **Map** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник и оптимизации производительности.

## Реальные примеры использования **Map**

### **Map** для конфигураций

**Использование **Map** для управления конфигурациями:**

```kotlin
// Иерархическая конфигурация
class Configuration {
    private val config = mutableMapOf<String, Any>()

    fun set(key: String, value: Any) {
        config[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, default: T): T {
        return config[key] as? T ?: default
    }

    fun getNested(path: String, default: Any? = null): Any? {
        return path.split(".").fold(config as Any?) { current, segment ->
            when (current) {
                is Map<*, *> -> current[segment]
                else -> null
            }
        } ?: default
    }
}

// Использование
val config = Configuration()
config.set("database.host", "localhost")
config.set("database.port", 5432)
config.set("server.port", 8080)

val host = config.getNested("database.host")  // "localhost"
val port = config.getNested("database.port")  // 5432
```

**Map** идеально подходит для управления конфигурациями, где нужна гибкая структура данных с ключами.

### **Map** для кэширования

**Реализация различных типов кэшей с использованием **Map**:**

```kotlin
// LRU (Least Recently Used) кэш
class LRUCache<K, V>(private val capacity: Int) {
    private val cache = LinkedHashMap<K, V>(capacity, 0.75f, true)

    fun get(key: K): V? {
        return cache[key]  // Доступ перемещает элемент в конец
    }

    fun put(key: K, value: V) {
        if (cache.size >= capacity && key !in cache) {
            val firstKey = cache.keys.first()
            cache.remove(firstKey)
        }
        cache[key] = value
    }

    fun size(): Int = cache.size
}

// TTLCache (Time To Live) кэш
class TTLCache<K, V>(private val ttlMillis: Long) {
    data class CacheEntry<V>(val value: V, val timestamp: Long)
    private val cache = mutableMapOf<K, CacheEntry<V>>()

    fun get(key: K): V? {
        val entry = cache[key] ?: return null
        val now = System.currentTimeMillis()

        return if (now - entry.timestamp < ttlMillis) {
            entry.value
        } else {
            cache.remove(key)
            null
        }
    }

    fun put(key: K, value: V) {
        cache[key] = CacheEntry(value, System.currentTimeMillis())
    }

    fun cleanup() {
        val now = System.currentTimeMillis()
        cache.entries.removeIf { now - it.value.timestamp >= ttlMillis }
    }
}
```

**Map** позволяет реализовывать различные типы кэшей с разными стратегиями вытеснения.

## Продвинутые операции **Map**

### Трансформация и фильтрация **Map**

**Продвинутые операции трансформации и фильтрации **Map**:**

```kotlin
// Трансформация значений с сохранением структуры
fun <K, V, R> Map<K, V>.mapValues(
    transform: (K, V) -> R
): Map<K, R> {
    return mapValues { (key, value) -> transform(key, value) }
}

// Трансформация ключей с сохранением значений
fun <K, V, R> Map<K, V>.mapKeys(
    transform: (K, V) -> R
): Map<R, V> {
    return mapKeys { (key, value) -> transform(key, value) }
}

// Фильтрация по ключам и значениям
fun <K, V> Map<K, V>.filter(
    predicate: (K, V) -> Boolean
): Map<K, V> {
    return filter { (key, value) -> predicate(key, value) }
}

// Использование
val map = mapOf("a" to 1, "b" to 2, "c" to 3)

val doubled = map.mapValues { _, value -> value * 2 }  // {"a": 2, "b": 4, "c": 6}
val uppercase = map.mapKeys { key, _ -> key.uppercase() }  // {"A": 1, "B": 2, "C": 3}
val filtered = map.filter { key, value -> key.length == 1 && value > 1 }  // {"b": 2, "c": 3}
```

Трансформация и фильтрация **Map** позволяют эффективно преобразовывать данные без изменения исходной структуры.

### Работа с вложенными **Map**

**Операции над вложенными **Map** для создания сложных структур данных:**

```kotlin
// Flatten вложенных Map
fun <K1, K2, V> Map<K1, Map<K2, V>>.flatten(): Map<Pair<K1, K2>, V> {
    return flatMap { (k1, nested) ->
        nested.map { (k2, v) -> (k1 to k2) to v }
    }.toMap()
}

// Group Map обратно во вложенную структуру
fun <K1, K2, V> Map<Pair<K1, K2>, V>.group(): Map<K1, Map<K2, V>> {
    return groupBy({ it.key.first }, { it.key.second to it.value })
        .mapValues { (_, pairs) ->
            pairs.associate { it.first to it.second }
        }
}

// Объединение вложенных Map
fun <K1, K2, V> Map<K1, Map<K2, V>>.mergeNested(other: Map<K1, Map<K2, V>>): Map<K1, Map<K2, V>> {
    return (this.asSequence() + other.asSequence())
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, maps) ->
            maps.flatMap { it.entries }
                .groupBy({ it.key }, { it.value })
                .mapValues { it.value.first() }
        }
}

// Использование
val nested = mapOf(
    "users" to mapOf("alice" to 25, "bob" to 30),
    "admins" to mapOf("charlie" to 35)
)

val flattened = nested.flatten()
// {(users, alice): 25, (users, bob): 30, (admins, charlie): 35}

val grouped = flattened.group()
// Восстановление исходной структуры
```

Вложенные **Map** позволяют создавать иерархические структуры данных для сложных доменных моделей.

## Продвинутые техники работы с **Map**

### Операции с несколькими **Map**

**Работа с несколькими **Map** одновременно:**

```kotlin
// Объединение Map
val map1 = mapOf("a" to 1, "b" to 2)
val map2 = mapOf("c" to 3, "d" to 4)
val merged = map1 + map2  // {"a": 1, "b": 2, "c": 3, "d": 4}

// Объединение с обработкой конфликтов
val map3 = mapOf("a" to 10, "b" to 20)
val mergedWithConflict = map1 + map3  // {"a": 10, "b": 20} - последнее значение побеждает

// Объединение с кастомной логикой
val mergedCustom = (map1.asSequence() + map3.asSequence())
    .groupBy({ it.key }, { it.value })
    .mapValues { it.value.first() }  // Первое значение побеждает

// Пересечение Map
val intersection = map1.filterKeys { it in map3 }
val intersectionWithValues = map1.filter { (key, value) ->
    key in map3 && map3[key] == value
}

// Разность Map
val difference = map1.filterKeys { it !in map3 }

// Сравнение Map
val areEqual = map1 == map2  // false
val keysEqual = map1.keys == map2.keys  // false
val valuesEqual = map1.values.toSet() == map2.values.toSet()  // false
```

Работа с несколькими **Map** позволяет комбинировать данные из различных источников и выполнять сложные операции.

### Специализированные **Map** операции

**Специализированные операции для различных сценариев:**

```kotlin
// Группировка по значениям
val map = mapOf("a" to 1, "b" to 2, "c" to 1, "d" to 3)
val groupedByValue = map.entries.groupBy({ it.value }, { it.key })
// {1: ["a", "c"], 2: ["b"], 3: ["d"]}

// Инвертирование Map (ключи становятся значениями)
val inverted = map.entries.associate { it.value to it.key }
// {1: "c", 2: "b", 3: "d"} - последнее значение

// Инвертирование с сохранением всех значений
val invertedAll = map.entries.groupBy({ it.value }, { it.key })
    .mapValues { it.value.toList() }
// {1: ["a", "c"], 2: ["b"], 3: ["d"]}

// Сортировка Map
val sortedByKey = map.toList().sortedBy { it.first }.toMap()
val sortedByValue = map.toList().sortedBy { it.second }.toMap()
val sortedByKeyDesc = map.toList().sortedByDescending { it.first }.toMap()

// Преобразование Map в другие структуры
val asList = map.toList()  // [("a", 1), ("b", 2), ...]
val asSet = map.entries.toSet()
val keysList = map.keys.toList()
val valuesList = map.values.toList()

// Создание Map из пар
val pairs = listOf("a" to 1, "b" to 2, "c" to 3)
val mapFromPairs = pairs.toMap()
val mapFromPairsWithMerge = pairs.groupBy({ it.first }, { it.second })
    .mapValues { it.value.first() }
```

Специализированные операции **Map** позволяют эффективно работать с данными в различных форматах.

Этот файл содержит полное руководство по **Map** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы с конфигурациями и кэшами, трансформации и фильтрации, работы с вложенными **Map**, операций с несколькими **Map** и специализированных операций.

## Дополнительные техники **Map**

### Работа с **default** значениями

**Использование **default** значений в **Map**:**

```kotlin
// Map с default значениями
val mapWithDefault = mapOf("a" to 1, "b" to 2).withDefault { 0 }

// Использование
val valueA = mapWithDefault.getValue("a")  // 1
val valueC = mapWithDefault.getValue("c")  // 0 (default)

// Создание Map с default функцией
fun <K, V> createMapWithDefault(defaultValue: (K) -> V): Map<K, V> {
    val base = mutableMapOf<K, V>()
    return object : Map<K, V> by base {
        override fun get(key: K): V? {
            return base[key] ?: defaultValue(key).also { base[key] = it }
        }
    }
}

// Использование
val map = createMapWithDefault<String, Int> { it.length }
val value = map["hello"]  // 5 (длина строки)
```

**Default** значения позволяют обрабатывать отсутствующие ключи без явных проверок.

### Работа с мутабельными **Map**

**Продвинутые операции с мутабельными **Map**:**

```kotlin
// Безопасные операции с мутабельными Map
fun <K, V> MutableMap<K, V>.putIfAbsent(key: K, value: V): V? {
    return if (key !in this) {
        this[key] = value
        null
    } else {
        this[key]
    }
}

// Операции с compute
fun <K, V> MutableMap<K, V>.computeIfAbsent(key: K, mappingFunction: (K) -> V): V {
    return this.getOrPut(key) { mappingFunction(key) }
}

fun <K, V> MutableMap<K, V>.computeIfPresent(key: K, remappingFunction: (K, V) -> V?): V? {
    return this[key]?.let { value ->
        val newValue = remappingFunction(key, value)
        if (newValue != null) {
            this[key] = newValue
            newValue
        } else {
            this.remove(key)
            null
        }
    }
}

// Использование
val map = mutableMapOf<String, Int>()
map.computeIfAbsent("key") { it.length }  // Добавляет "key" -> 3
map.computeIfPresent("key") { k, v -> v * 2 }  // Обновляет до 6
```

Мутабельные **Map** операции позволяют эффективно обновлять значения.

Этот файл содержит полное руководство по **Map** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы с конфигурациями и кэшами, трансформации и фильтрации, работы с вложенными **Map**, операций с несколькими **Map**, специализированных операций, работы с **default** значениями и мутабельными **Map**.

## Дополнительные техники **Map**

### Работа с биективными **Map**

**Создание и работа с биективными **Map**:**

```kotlin
// Биективная Map (один к одному)
class BiMap<K, V> {
    private val forward = mutableMapOf<K, V>()
    private val reverse = mutableMapOf<V, K>()

    fun put(key: K, value: V) {
        forward[key]?.let { oldValue ->
            reverse.remove(oldValue)
        }
        reverse[value]?.let { oldKey ->
            forward.remove(oldKey)
        }
        forward[key] = value
        reverse[value] = key
    }

    fun get(key: K): V? = forward[key]
    fun getKey(value: V): K? = reverse[value]
    fun getValue(key: K): V? = forward[key]
    fun containsKey(key: K): Boolean = key in forward
    fun containsValue(value: V): Boolean = value in reverse
}

// Использование
val biMap = BiMap<String, Int>()
biMap.put("one", 1)
biMap.put("two", 2)
val value = biMap.get("one")  // 1
val key = biMap.getKey(2)  // "two"
```

Биективные **Map** позволяют эффективно получать ключи по значениям и наоборот.

Этот файл содержит полное руководство по **Map** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы с конфигурациями и кэшами, трансформации и фильтрации, работы с вложенными **Map**, операций с несколькими **Map**, специализированных операций, работы с **default** значениями, мутабельными **Map** и биективными **Map**.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Map** является важной структурой данных для хранения пар ключ-значение. Понимание различных операций над **Map**, от базовых добавления и получения значений до продвинутых техник работы с **default** значениями, мутабельными **Map**, биективными **Map** и операций с несколькими **Map**, позволяет эффективно работать с данными. Правильное использование **Map** помогает создавать эффективные структуры данных для различных задач.

Этот файл содержит полное руководство по **Map** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы с конфигурациями и кэшами, трансформации и фильтрации, работы с вложенными **Map**, операций с несколькими **Map**, специализированных операций, работы с **default** значениями, мутабельными **Map**, биективными **Map**, заключение и дополнительные ресурсы.

## Практические примеры использования

### Реализация кэша

**Пример реализации простого кэша с использованием **Map**:**

```kotlin
class SimpleCache<K, V>(private val maxSize: Int = 100) {
    private val cache = LinkedHashMap<K, V>(maxSize, 0.75f, true)

    fun get(key: K): V? {
        return cache[key]
    }

    fun put(key: K, value: V) {
        if (cache.size >= maxSize && key !in cache) {
            val firstKey = cache.keys.first()
            cache.remove(firstKey)
        }
        cache[key] = value
    }

    fun clear() {
        cache.clear()
    }
}
```

**LinkedHashMap** с **access order** позволяет реализовать **LRU** кэш.

### Группировка данных

**Пример группировки данных с использованием **Map**:**

```kotlin
data class Order(val customerId: Long, val amount: Double, val date: LocalDate)

fun groupOrdersByCustomer(orders: List<Order>): Map<Long, List<Order>> {
    return orders.groupBy { it.customerId }
}

fun calculateTotalByCustomer(orders: List<Order>): Map<Long, Double> {
    return orders.groupBy { it.customerId }
        .mapValues { (_, orders) -> orders.sumOf { it.amount } }
}
```

**Map** упрощает группировку и агрегацию данных.

### Работа с вложенными **Map**

**Пример работы с вложенными структурами:**

```kotlin
// Получение вложенного значения
fun <K1, K2, V> Map<K1, Map<K2, V>>.getNested(key1: K1, key2: K2): V? {
    return this[key1]?.get(key2)
}

// Обновление вложенного значения
fun <K1, K2, V> MutableMap<K1, MutableMap<K2, V>>.putNested(
    key1: K1,
    key2: K2,
    value: V
) {
    this.getOrPut(key1) { mutableMapOf() }[key2] = value
}

// Трансформация вложенной структуры
fun <K1, K2, V, R> Map<K1, Map<K2, V>>.mapNested(
    transform: (V) -> R
): Map<K1, Map<K2, R>> {
    return this.mapValues { (_, innerMap) ->
        innerMap.mapValues { (_, value) -> transform(value) }
    }
}

// Использование
val nested = mapOf(
    "users" to mapOf("john" to 25, "jane" to 30),
    "admins" to mapOf("admin1" to 40)
)
val age = nested.getNested("users", "john")  // 25
```

Работа с вложенными **Map** позволяет обрабатывать сложные иерархические структуры данных.

### Операции с несколькими **Map**

**Пример комбинирования нескольких **Map**:**

```kotlin
// Объединение Map с приоритетом
fun <K, V> Map<K, V>.mergeWith(
    other: Map<K, V>,
    conflictResolver: (V, V) -> V = { first, _ -> first }
): Map<K, V> {
    return (this.asSequence() + other.asSequence())
        .groupBy({ it.key }, { it.value })
        .mapValues { (_, values) ->
            values.reduce(conflictResolver)
        }
}

// Разность Map
fun <K, V> Map<K, V>.minusKeys(keys: Set<K>): Map<K, V> {
    return this.filterKeys { it !in keys }
}

// Использование
val map1 = mapOf("a" to 1, "b" to 2, "c" to 3)
val map2 = mapOf("b" to 20, "c" to 30, "d" to 4)
val merged = map1.mergeWith(map2) { first, second -> first + second }  // {"a"=1, "b"=22, "c"=33, "d"=4}
val diff = map1.minusKeys(setOf("b", "c"))  // {"a"=1}
```

Операции с несколькими **Map** позволяют комбинировать данные из различных источников.

Этот файл содержит полное руководство по **Map** в **Kotlin**, покрывающее все основные аспекты от базовых операций до продвинутых техник, оптимизации производительности, реальных примеров использования, работы с конфигурациями и кэшами, трансформации и фильтрации, работы с вложенными **Map**, операций с несколькими **Map**, специализированных операций, работы с **default** значениями, мутабельными **Map**, биективными **Map**, практические примеры использования, включая работу с вложенными **Map** и операции с несколькими **Map**, заключение и дополнительные ресурсы.

## Дополнительные ресурсы

**Для дальнейшего изучения **Map** в **Kotlin** рекомендуется:**

- **Kotlin Map Documentation**: **https**://**kotlinlang.org**/**api**/**latest**/**jvm**/**stdlib**/**kotlin.collections**/-**map**/
- **Map Operations**: **https**://**kotlinlang.org**/**docs**/**collection-operations.html**

