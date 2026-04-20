---
title: "Scala Collections - Map"
description: "Полное руководство по работе со словарями в Scala: Map, HashMap, SortedMap, операции и лучшие практики"
tags:
  - scala
  - collections
  - map
  - functional-programming
  - immutable
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: ["scala/scala-collections-list.md", "scala/scala-collections-set.md"]
updated: "2026-02-06"
related: ["scala/scala-basics.md", "scala/scala-collections.md"]
---

# Scala Collections — Map

Кратко: руководство по работе со словарями в **Scala**: **Map**, **HashMap**, **SortedMap**, операции и лучшие практики.

## Полезные ссылки

### Официальная документация
- [Scala Map API](https://www.scala-lang.org/api/current/scala/collection/immutable/Map.html)
- [Scala Collections Overview](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)

### См. также
- [[scala-basics|Основы Scala]]
- [[scala-collections-list|Списки (List)]]
- [[scala-collections-set|Множества (Set)]]
- [[scala-collections|Обзор коллекций]]

## Содержание

- [**Scala Collections** — **Map**](#scala-collections-map)
- [Введение в **Map**](#введение-в-map)
  - [Основные характеристики](#основные-характеристики)
  - [**Immutable** vs **Mutable**](#immutable-vs-mutable)
- [Создание словарей](#создание-словарей)
  - [Базовое создание](#базовое-создание)
  - [Создание из коллекций](#создание-из-коллекций)
- [**Immutable Map**](#immutable-map)
  - [Основные операции](#основные-операции)
  - [Обновление значений](#обновление-значений)
- [**Mutable Map**](#mutable-map)
- [**HashMap**](#hashmap)
- [**SortedMap**](#sortedmap)
  - [**Map** — преобразование значений](#map-преобразование-значений)
  - [**Filter** — фильтрация элементов](#filter-фильтрация-элементов)
  - [**FlatMap** — преобразование и разворачивание](#flatmap-преобразование-и-разворачивание)
- [Работа с ключами и значениями](#работа-с-ключами-и-значениями)
  - [Ключи](#ключи)
  - [Значения](#значения)
  - [Итерация](#итерация)
- [Преобразование словарей](#преобразование-словарей)
  - [В другие коллекции](#в-другие-коллекции)
  - [Транспонирование](#транспонирование)
- [Фильтрация и поиск](#фильтрация-и-поиск)
- [Группировка и агрегация](#группировка-и-агрегация)
  - [**GroupBy**](#groupby)
  - [**Fold** и **Reduce**](#fold-и-reduce)
- [Слияние словарей](#слияние-словарей)
- [Производительность](#производительность)
  - [Временная сложность операций](#временная-сложность-операций)
  - [Рекомендации по производительности](#рекомендации-по-производительности)
- [Лучшие практики](#лучшие-практики)
  - [Предпочтение **immutable Map**](#предпочтение-immutable-map)
  - [Безопасный доступ к значениям](#безопасный-доступ-к-значениям)
  - [Использование **Map** для кэширования](#использование-map-для-кэширования)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Практические примеры использования](#практические-примеры-использования)
  - [Реализация кэша](#реализация-кэша)
  - [Группировка данных](#группировка-данных)
  - [Работа с вложенными **Map**](#работа-с-вложенными-map)
  - [Трансформация значений **Map**](#трансформация-значений-map)
  - [Обновление значений с функциями](#обновление-значений-с-функциями)
- [Продвинутые техники работы с **Map**](#продвинутые-техники-работы-с-map)
  - [Оптимизация производительности **Map**](#оптимизация-производительности-map)
  - [Работа с большими **Map**](#работа-с-большими-map)
  - [Сравнение **Map** с другими структурами](#сравнение-map-с-другими-структурами)
  - [Практические примеры: Группировка данных с **Map**](#практические-примеры-группировка-данных-с-map)
  - [Практические примеры: Агрегация данных с **Map**](#практические-примеры-агрегация-данных-с-map)
  - [Практические примеры: Обновление **Map** с дефолтными значениями](#практические-примеры-обновление-map-с-дефолтными-значениями)
  - [Практические примеры: Работа с **SortedMap**](#практические-примеры-работа-с-sortedmap)
  - [Практические примеры: Работа с **ListMap**](#практические-примеры-работа-с-listmap)
  - [Использование с различными операциями для производительности](#использование-с-различными-операциями-для-производительности)
  - [Использование с различными операциями для группировки данных](#использование-с-различными-операциями-для-группировки-данных)
  - [Использование с различными операциями для агрегации данных](#использование-с-различными-операциями-для-агрегации-данных)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в Map

**Map** в **Scala** — это коллекция пар ключ-значение, где каждый ключ уникален. **Map** оптимизирован для быстрого доступа к значениям по ключу и является основным типом для работы с ассоциативными данными. **Map** реализован на основе хеш-таблиц, что обеспечивает эффективный доступ к значениям по ключу и проверку принадлежности ключей.

**Map** особенно полезен для задач, где нужно быстро находить значения по ключам, группировать данные по ключам или создавать индексы. В отличие от **List**, **Map** не поддерживает доступ по индексу, но обеспечивает константное среднее время для операций доступа по ключу.

### Основные характеристики

- **Уникальные ключи**: каждый ключ встречается только один раз. При добавлении элемента с существующим ключом, старое значение заменяется. Это свойство делает **Map** идеальным для создания словарей, индексов и кэшей, где каждому ключу соответствует одно значение.

- **Пары ключ-значение**: хранит ассоциации между ключами и значениями, обеспечивая быстрый доступ к значениям по ключу. Ключи должны быть сравнимыми и иметь корректную реализацию **hashCode** для эффективной работы хеш-таблицы.

- **Порядок**: зависит от реализации. **HashMap** не гарантирует порядок элементов, что обеспечивает максимальную производительность. **SortedMap** поддерживает отсортированный порядок по ключам, но с дополнительными накладными расходами на поддержание порядка. **LinkedHashMap** сохраняет порядок вставки элементов.

- **Быстрый доступ**: **Map** оптимизирован для операций **get** и **contains**, обеспечивая `O(1)` среднее время для **HashMap**. Это делает **Map** идеальным выбором для задач, где требуется частый доступ к значениям по ключам, таких как кэширование, индексация данных или создание **lookup**-таблиц.

### Immutable vs Mutable

**Scala** предоставляет два варианта словарей:**

1. **Map** (immutable) — неизменяемый словарь, рекомендуется по умолчанию
2. **mutable.Map** — изменяемый словарь для случаев, когда нужна эффективная модификация

## Создание словарей

### Базовое создание

```scala
// Пустой словарь
val empty: Map[String, Int] = Map.empty
val empty2 = Map.empty[String, Int]

// Словарь с элементами
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)
val ages2 = Map(
  ("Alice", 30),
  ("Bob", 25),
  ("Charlie", 35)
)

// Использование синтаксиса ->
val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
```

### Создание из коллекций

```scala
// Из списка пар
val pairs = List(("a", 1), ("b", 2), ("c", 3))
val map = pairs.toMap

// Из двух списков
val keys = List("a", "b", "c")
val values = List(1, 2, 3)
val map2 = (keys zip values).toMap
```

## Immutable Map

### Основные операции

```scala
// Неизменяемый Map: ключ -> значение
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Доступ к значениям
ages("Alice")              // 30
ages.get("Alice")          // Some(30)
ages.get("David")          // None
ages.getOrElse("David", 0) // 0 - значение по умолчанию

// Проверки
ages.contains("Alice")     // true
ages.isEmpty               // false
ages.size                  // 3

// Добавление элементов (создает новый словарь)
val newMap = ages + ("David" -> 40)  // Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35, "David" -> 40)
val newMap2 = ages + (("David", 40), ("Eve", 28))  // добавление нескольких

// Удаление элементов
val removed = ages - "Bob"  // Map("Alice" -> 30, "Charlie" -> 35)
val removed2 = ages -- List("Bob", "Charlie")  // удаление нескольких
```

### Обновление значений

```scala
val ages = Map("Alice" -> 30, "Bob" -> 25)

// Обновление с использованием функции
val updated = ages.updatedWith("Alice") {
  case Some(age) => Some(age + 1)
  case None => Some(31)
}

// Простое обновление
val updated2 = ages.updated("Alice", 31)  // Map("Alice" -> 31, "Bob" -> 25)
```

## Mutable Map

**Mutable Map** используется, когда нужна эффективная модификация:**

```scala
// Mutable Map для изменяемого словаря
import scala.collection.mutable

val map = mutable.Map("Alice" -> 30, "Bob" -> 25)

// Добавление и обновление элементов
map("Charlie") = 35              // добавление
map("Alice") = 31                // обновление
map += ("David" -> 40)           // добавление
map += (("Eve", 28), ("Frank", 32))  // добавление нескольких
map ++= Map("Grace" -> 27, "Henry" -> 29)  // добавление из другого Map

// Удаление элементов
map -= "Bob"                     // удаление
map --= List("Charlie", "David")  // удаление нескольких

// Преобразование в immutable Map
val immutableMap = map.toMap
```

## HashMap

**HashMap** — это реализация **Map** на основе хеш-таблицы:**

```scala
// HashMap — неизменяемая реализация с хеш-таблицей
import scala.collection.immutable.HashMap

val hashMap = HashMap("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Операции те же, что и у обычного Map
hashMap("Alice")  // 30
hashMap + ("David" -> 40)  // HashMap("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35, "David" -> 40)
```

**HashMap** обеспечивает `O(1)` среднее время для операций **get**, **put** и **remove**.

## SortedMap

**SortedMap** поддерживает отсортированный порядок по ключам:**

```scala
// SortedMap — ключи отсортированы в естественном порядке
import scala.collection.immutable.SortedMap

val sortedMap = SortedMap("Charlie" -> 35, "Alice" -> 30, "Bob" -> 25)
// SortedMap("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35) - автоматически отсортирован

// Ключи всегда в отсортированном порядке
sortedMap.firstKey      // "Alice"
sortedMap.lastKey       // "Charlie"

// Диапазоны
val subset = sortedMap.range("Alice", "Charlie")  // SortedMap("Alice" -> 30, "Bob" -> 25)
val from = sortedMap.from("Bob")                   // SortedMap("Bob" -> 25, "Charlie" -> 35)
val until = sortedMap.until("Charlie")             // SortedMap("Alice" -> 30, "Bob" -> 25)
```

**SortedMap** требует, чтобы ключи были сравнимыми (имели Ordering).

## Основные операции

### Map — преобразование значений

```scala
// Неизменяемый Map: ключ -> значение
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Преобразование значений
val doubled = ages.map { case (name, age) => (name, age * 2) }
// Map("Alice" -> 60, "Bob" -> 50, "Charlie" -> 70)

// Преобразование только значений
val agesAsStrings = ages.mapValues(_.toString)
// Map("Alice" -> "30", "Bob" -> "25", "Charlie" -> "35")
```

### Filter — фильтрация элементов

```scala
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35, "David" -> 20)

// Фильтрация по значению
val adults = ages.filter(_._2 >= 25)
// Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Фильтрация по ключу
val aNames = ages.filter(_._1.startsWith("A"))
// Map("Alice" -> 30)
```

### FlatMap — преобразование и разворачивание

```scala
val map = Map("a" -> List(1, 2), "b" -> List(3, 4))

// Разворачивание вложенных коллекций
val flattened = map.flatMap { case (key, values) =>
  values.map(value => (key, value))
}
// Map("a" -> 1, "a" -> 2, "b" -> 3, "b" -> 4) - но это не Map, так как ключи не уникальны
// Правильнее будет List или другой тип коллекции
```

## Работа с ключами и значениями

### Ключи

```scala
// Неизменяемый Map: ключ -> значение
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Получение всех ключей
val keys = ages.keys        // Set("Alice", "Bob", "Charlie")
val keysList = ages.keys.toList  // List("Alice", "Bob", "Charlie")

// Проверка наличия ключа
ages.contains("Alice")     // true
ages.isDefinedAt("Alice")  // true
ages.isDefinedAt("David")  // false
```

### Значения

```scala
// Неизменяемый Map: ключ -> значение
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Получение всех значений
val values = ages.values    // Iterable(30, 25, 35)
val valuesList = ages.values.toList  // List(30, 25, 35)
```

### Итерация

```scala
// Неизменяемый Map: ключ -> значение
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Итерация по парам
ages.foreach { case (name, age) =>
  println(s"$name is $age years old")
}

// Итерация с использованием map
ages.map { case (name, age) =>
  s"$name: $age"
}
```

## Преобразование словарей

### В другие коллекции

```scala
// Неизменяемый Map: ключ -> значение
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Преобразование в список пар
val pairs = ages.toList
// List(("Alice", 30), ("Bob", 25), ("Charlie", 35))

// Преобразование в список ключей
val keys = ages.keys.toList

// Преобразование в список значений
val values = ages.values.toList

// Преобразование в Set
val keySet = ages.keySet
```

### Транспонирование

```scala
// Создание обратного Map (значение -> ключ)
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)
val reversed = ages.map(_.swap)
// Map(30 -> "Alice", 25 -> "Bob", 35 -> "Charlie")
```

## Фильтрация и поиск

```scala
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35, "David" -> 20)

// Поиск элементов
ages.find(_._2 > 30)       // Some(("Charlie", 35))
ages.find(_._2 > 50)       // None

// Проверка существования
ages.exists(_._2 > 30)     // true
ages.forall(_._2 > 18)     // true (все значения > 18)

// Подсчет
ages.count(_._2 > 25)      // 2
```

## Группировка и агрегация

### GroupBy

```scala
val people = List(
  ("Alice", 30, "Engineer"),
  ("Bob", 25, "Engineer"),
  ("Charlie", 35, "Manager"),
  ("David", 28, "Engineer")
)

// Группировка по профессии
val byProfession = people.groupBy(_._3)
// Map("Engineer" -> List(("Alice", 30, "Engineer"), ("Bob", 25, "Engineer"), ...), ...)

// Преобразование в Map с агрегацией
val avgAgeByProfession = people
  .groupBy(_._3)
  .mapValues(people => people.map(_._2).sum / people.length)
```

### Fold и Reduce

```scala
// Неизменяемый Map: ключ -> значение
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Сумма всех значений
val totalAge = ages.values.sum  // 90

// Средний возраст
val avgAge = ages.values.sum / ages.size  // 30

// Максимальное значение
val maxAge = ages.values.max  // 35
val maxEntry = ages.maxBy(_._2)  // ("Charlie", 35)
```

## Слияние словарей

```scala
val map1 = Map("a" -> 1, "b" -> 2, "c" -> 3)
val map2 = Map("c" -> 30, "d" -> 4, "e" -> 5)

// Объединение (значения из map2 имеют приоритет)
val merged = map1 ++ map2
// Map("a" -> 1, "b" -> 2, "c" -> 30, "d" -> 4, "e" -> 5)

// Объединение с функцией для разрешения конфликтов
val merged2 = map1.foldLeft(map2) { case (acc, (k, v)) =>
  acc.updatedWith(k) {
    case Some(existing) => Some(existing + v)
    case None => Some(v)
  }
}
```

## Производительность

### Временная сложность операций

- **get/contains**: `O(1)` среднее время для **HashMap**, `O(log n)` для **SortedMap**
- **put/update**: `O(1)` среднее время для **HashMap**, `O(log n)` для **SortedMap**
- **remove**: `O(1)` среднее время для **HashMap**, `O(log n)` для **SortedMap**
- **iteration**: `O(n)` где n — количество элементов

### Рекомендации по производительности

1. **Используйте `HashMap` для быстрого доступа** — обеспечивает `O(1)` среднее время
2. **Используйте `SortedMap` для отсортированных данных** — обеспечивает `O(log n)` доступ и автоматическую сортировку
3. **Избегайте частых преобразований** — преобразование в другие коллекции создает копии
4. **Используйте mutable.Map для частых модификаций** — эффективнее, чем создание новых **immutable Map**

## Лучшие практики

### Предпочтение immutable Map

```scala
// Хорошо - immutable
val map = Map("a" -> 1, "b" -> 2)
val doubled = map.mapValues(_ * 2)

// Плохо - ненужная мутабельность
val mutableMap = mutable.Map("a" -> 1, "b" -> 2)
mutableMap.mapValues(_ * 2)  // создает новый Map, исходный не изменяется
```

### Безопасный доступ к значениям

```scala
// Хорошо - безопасный доступ с getOrElse
val age = ages.getOrElse("David", 0)

// Хорошо - использование Option
ages.get("David") match {
  case Some(age) => println(s"Age: $age")
  case None => println("Not found")
}

// Плохо - небезопасный доступ (может упасть)
val age = ages("David")  // NoSuchElementException если ключ не существует
```

### Использование Map для кэширования

```scala
// Хорошо - использование Map как кэша
def expensiveComputation(key: String): Int = {
  // дорогая операция
  key.length * 1000
}

val cache = mutable.Map[String, Int]()

def getCached(key: String): Int = {
  cache.getOrElseUpdate(key, expensiveComputation(key))
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

## Практические примеры использования

### Реализация кэша

**Пример реализации простого кэша с использованием **Map**:**

```scala
// Mutable Map для изменяемого словаря
import scala.collection.mutable

class SimpleCache[K, V](maxSize: Int = 100) {
  private val cache = mutable.LinkedHashMap[K, V]()

  def get(key: K): Option[V] = {
    cache.get(key).map { value =>
      // Переместить в конец (LRU)
      cache -= key
      cache += (key -> value)
      value
    }
  }

  def put(key: K, value: V): Unit = {
    if (cache.size >= maxSize && !cache.contains(key)) {
      val firstKey = cache.head._1
      cache -= firstKey
    }
    cache += (key -> value)
  }

  def clear(): Unit = {
    cache.clear()
  }
}
```

**LinkedHashMap** с доступом по порядку позволяет реализовать **LRU** кэш для эффективного кэширования данных.

### Группировка данных

**Пример группировки данных с использованием **Map**:**

```scala
case class Order(customerId: Long, amount: Double, date: java.time.LocalDate)

// Группировка по клиенту
def groupOrdersByCustomer(orders: List[Order]): Map[Long, List[Order]] = {
  orders.groupBy(_.customerId)
}

// Вычисление общей суммы по клиенту
def calculateTotalByCustomer(orders: List[Order]): Map[Long, Double] = {
  orders.groupBy(_.customerId).mapValues(_.map(_.amount).sum)
}
```

**Map** упрощает группировку и агрегацию данных, позволяя эффективно обрабатывать структурированную информацию.

### Работа с вложенными Map

**Пример работы с вложенными структурами:**

```scala
// Получение вложенного значения
def getNested[K1, K2, V](map: Map[K1, Map[K2, V]], key1: K1, key2: K2): Option[V] = {
  map.get(key1).flatMap(_.get(key2))
}

// Обновление вложенного значения
def putNested[K1, K2, V](
  map: mutable.Map[K1, mutable.Map[K2, V]],
  key1: K1,
  key2: K2,
  value: V
): Unit = {
  map.getOrElseUpdate(key1, mutable.Map.empty) += (key2 -> value)
}

// Трансформация вложенной структуры
def mapNested[K1, K2, V, R](map: Map[K1, Map[K2, V]])(f: V => R): Map[K1, Map[K2, R]] = {
  map.mapValues(_.mapValues(f))
}
```

Работа с вложенными **Map** позволяет обрабатывать сложные иерархические структуры данных, такие как конфигурации или многомерные индексы.

### Трансформация значений Map

**Map** предоставляет множество способов трансформации значений:**

```scala
// Неизменяемый Map: ключ -> значение
val ages = Map("Alice" -> 30, "Bob" -> 25, "Charlie" -> 35)

// Трансформация всех значений
val agesPlusOne = ages.mapValues(_ + 1)  // Map("Alice" -> 31, "Bob" -> 26, "Charlie" -> 36)

// Трансформация с изменением ключей
val swapped = ages.map(_.swap)  // Map(30 -> "Alice", 25 -> "Bob", 35 -> "Charlie")

// Фильтрация по ключам
val filteredKeys = ages.filterKeys(_.startsWith("A"))  // Map("Alice" -> 30)

// Фильтрация по значениям
val filteredValues = ages.filter(_._2 > 28)  // Map("Alice" -> 30, "Charlie" -> 35)
```

Трансформация **Map** позволяет эффективно обрабатывать ассоциативные данные.

### Обновление значений с функциями

**Map** поддерживает сложные операции обновления:**

```scala
val counts = Map("a" -> 1, "b" -> 2, "c" -> 3)

// Обновление с функцией
val incremented = counts.updatedWith("a") {
  case Some(value) => Some(value + 1)
  case None => Some(1)
}

// Обновление всех значений
val doubled = counts.mapValues(_ * 2)

// Обновление с условием
val conditional = counts.map {
  case (k, v) if v > 2 => (k, v * 2)
  case (k, v) => (k, v)
}
```

Эти операции позволяют создавать сложные трансформации данных.

## Продвинутые техники работы с Map

### Оптимизация производительности Map

**Map** оптимизирован для доступа по ключу и операций над словарями.

```scala
// Эффективный доступ по ключу
val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
val value = map("a")  // O(1) - очень быстро

// Эффективное добавление
val added = map + ("d" -> 4)  // O(1) - очень быстро

// Эффективное удаление
val removed = map - "a"  // O(1) - очень быстро
```

### Работа с большими Map

**Map** эффективно работает с большими объемами данных.

```scala
// Создание большого Map
val largeMap = (1 to 1000000).map(i => (i, i * 2)).toMap

// Эффективный доступ по ключу
val value = largeMap(500000)  // O(1) - быстро

// Эффективная фильтрация
val filtered = largeMap.filter(_._2 > 1000000)  // O(n) - эффективно

// Эффективная трансформация
val transformed = largeMap.mapValues(_ * 2)  // O(n) - эффективно
```

### Сравнение Map с другими структурами

**Map** предоставляет оптимальную производительность для ассоциативных данных.

```scala
// Map vs List of Tuples
val map = Map("a" -> 1, "b" -> 2, "c" -> 3)
val list = List(("a", 1), ("b", 2), ("c", 3))

// Доступ по ключу
map("a")  // O(1) - быстро
list.find(_._1 == "a").map(_._2)  // O(n) - медленно

// Обновление значения
val updated = map.updated("a", 10)  // O(1) - быстро
val updatedList = list.map {
  case (k, v) if k == "a" => (k, 10)
  case other => other
}  // O(n) - медленно
```

### Практические примеры: Группировка данных с Map

```scala
// Группировка списка по ключу
case class Person(name: String, age: Int, city: String)

val people = List(
  Person("Alice", 30, "New York"),
  Person("Bob", 25, "London"),
  Person("Charlie", 30, "New York")
)

// Группировка по городу
val byCity: Map[String, List[Person]] = people.groupBy(_.city)
// Map("New York" -> List(Alice, Charlie), "London" -> List(Bob))

// Группировка по возрасту
val byAge: Map[Int, List[Person]] = people.groupBy(_.age)
// Map(30 -> List(Alice, Charlie), 25 -> List(Bob))
```

### Практические примеры: Агрегация данных с Map

```scala
// Подсчет частоты элементов
def frequency[A](list: List[A]): Map[A, Int] = {
  list.groupMapReduce(identity)(_ => 1)(_ + _)
}

val words = List("apple", "banana", "apple", "orange", "banana", "apple")
val wordFreq = frequency(words)
// Map("apple" -> 3, "banana" -> 2, "orange" -> 1)

// Агрегация с вычислением суммы
case class Sale(product: String, amount: Double)

val sales = List(
  Sale("product1", 100.0),
  Sale("product2", 200.0),
  Sale("product1", 150.0)
)

val totalByProduct = sales.groupMapReduce(_.product)(_.amount)(_ + _)
// Map("product1" -> 250.0, "product2" -> 200.0)
```

### Практические примеры: Обновление Map с дефолтными значениями

```scala
// Обновление с дефолтным значением
val map = Map("a" -> 1, "b" -> 2)

def increment(key: String, map: Map[String, Int]): Map[String, Int] = {
  map.updated(key, map.getOrElse(key, 0) + 1)
}

val updated = increment("a", map)  // Map("a" -> 2, "b" -> 2)
val newKey = increment("c", map)   // Map("a" -> 1, "b" -> 2, "c" -> 1)

// Использование withDefaultValue
val mapWithDefault = Map("a" -> 1, "b" -> 2).withDefaultValue(0)
val value = mapWithDefault("c")  // 0 (дефолтное значение)
```

**Map** является важной структурой данных для работы с ассоциативными данными. Понимание различных реализаций (HashMap, SortedMap), их особенностей производительности, оптимизации производительности, работы с большими **Map**, сравнения с другими структурами данных, операций над словарями, группировки данных, агрегации данных, обновления **Map** с дефолтными значениями и практических применений позволяет эффективно использовать **Map** в различных сценариях. Использование **immutable Map** по умолчанию, **mutable.Map** для частых модификаций, правильный выбор реализации (HashMap для быстрого доступа, `SortedMap` для упорядоченных данных), группировка данных по ключам, агрегация данных с **groupMapReduce** и использование **withDefaultValue** для дефолтных значений делает код более безопасным, эффективным и выразительным.

### Практические примеры: Работа с SortedMap

```scala
import scala.collection.immutable.SortedMap

// SortedMap для упорядоченных ключей
val sortedMap = SortedMap("z" -> 3, "a" -> 1, "m" -> 2)
// SortedMap("a" -> 1, "m" -> 2, "z" -> 3)

// С кастомным порядком
val customSorted = SortedMap("z" -> 3, "a" -> 1, "m" -> 2)(Ordering.String.reverse)
// SortedMap("z" -> 3, "m" -> 2, "a" -> 1)
```

### Практические примеры: Работа с ListMap

```scala
import scala.collection.immutable.ListMap

// ListMap сохраняет порядок вставки
val listMap = ListMap("first" -> 1, "second" -> 2, "third" -> 3)
// Порядок элементов сохраняется
```

### Использование с различными операциями для производительности

```scala
// Оптимизация операций над Map
val largeMap = (1 to 1000000).map(i => (s"key$i", i)).toMap

// Быстрый доступ
val value = largeMap.get("key500000")  // O(1)

// Фильтрация
val filtered = largeMap.filter(_._2 % 2 == 0)  // O(n)

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallelProcessed = largeMap.par
  .filter(_._2 % 2 == 0)
  .mapValues(_ * 2)
  .seq
```

### Использование с различными операциями для группировки данных

```scala
// Группировка данных
val users = List(
  ("Alice", "admin"),
  ("Bob", "user"),
  ("Charlie", "admin"),
  ("David", "user")
)

// Группировка по роли
val groupedByRole = users.groupBy(_._2)
// Map("admin" -> List(("Alice", "admin"), ("Charlie", "admin")),
//     "user" -> List(("Bob", "user"), ("David", "user")))

// Группировка с преобразованием
val groupedNames = users.groupMap(_._2)(_._1)
// Map("admin" -> List("Alice", "Charlie"),
//     "user" -> List("Bob", "David"))
```

### Использование с различными операциями для агрегации данных

```scala
// Агрегация данных
val sales = List(
  ("Alice", 100),
  ("Bob", 200),
  ("Alice", 150),
  ("Bob", 300)
)

// Группировка и агрегация
val totalSales = sales.groupMapReduce(_._1)(_._2)(_ + _)
// Map("Alice" -> 250, "Bob" -> 500)
```

## Дополнительные ресурсы

**Для дальнейшего изучения **Map** в **Scala** рекомендуется:**

- [Scala Map API Documentation](https://www.scala-lang.org/api/current/scala/collection/immutable/Map.html)
- [Scala Collections Overview](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)
