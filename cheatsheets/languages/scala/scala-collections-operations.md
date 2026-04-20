---
title: "Scala Collections - Operations"
description: "Полное руководство по операциям над коллекциями в Scala: трансформации, фильтрация, агрегация, группировка"
tags:
  - scala
  - collections
  - operations
  - functional-programming
difficulty: "intermediate"
prerequisites: ["scala/scala-collections.md"]
next: ["scala/scala-collections-grouping.md"]
updated: "2026-04-20"
related: ["scala/scala-collections.md", "scala/scala-collections-list.md"]
---

# Scala Collections - Operations

Кратко: полное руководство по операциям над коллекциями в **Scala**: трансформации, фильтрация, агрегация, группировка и другие операции.

## Полезные ссылки

### Официальная документация
- [Scala Collections Operations](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)

### См. также
- [[scala-collections|Обзор коллекций]]
- [[scala-collections-list|Списки]]
- [[scala-collections-grouping|Группировка]]

## Содержание

- [Трансформации](#трансформации)
  - [Map](#map)
  - [FlatMap](#flatmap)
  - [Collect](#collect)
- [Фильтрация](#фильтрация)
- [Агрегация](#агрегация)
  - [Fold и Reduce](#fold-и-reduce)
  - [Агрегатные функции](#агрегатные-функции)
- [Поиск и выборка](#поиск-и-выборка)
- [Сортировка](#сортировка)
- [Объединение и разделение](#объединение-и-разделение)
- [Лучшие практики](#лучшие-практики)
  - [Композиция операций](#композиция-операций)
  - [Использование view для ленивых вычислений](#использование-view-для-ленивых-вычислений)
  - [Zip и Unzip](#zip-и-unzip)
  - [Sliding и Grouped](#sliding-и-grouped)
  - [Scan](#scan)
  - [Практический пример: Обработка данных](#практический-пример-обработка-данных)
  - [Практический пример: Трансформация данных](#практический-пример-трансформация-данных)
  - [Практический пример: Обработка временных рядов](#практический-пример-обработка-временных-рядов)
  - [Комбинирование операций](#комбинирование-операций)
  - [Параллельные операции](#параллельные-операции)
  - [Операции с индексами](#операции-с-индексами)
- [Лучшие практики](#лучшие-практики-1)
  - [Композиция операций](#композиция-операций-1)
  - [Использование view для ленивых вычислений](#использование-view-для-ленивых-вычислений-1)
  - [Использование параллельных коллекций для больших данных](#использование-параллельных-коллекций-для-больших-данных)
  - [Избегание ненужных операций](#избегание-ненужных-операций)
- [Продвинутые техники работы с операциями](#продвинутые-техники-работы-с-операциями)
  - [Композиция операций](#композиция-операций-2)
  - [Оптимизация операций](#оптимизация-операций)
  - [Параллельные операции](#параллельные-операции-1)
  - [Практические примеры: Обработка больших наборов данных](#практические-примеры-обработка-больших-наборов-данных)
  - [Практические примеры: Фильтрация и трансформация данных](#практические-примеры-фильтрация-и-трансформация-данных)
- [Заключение (расширенное)](#заключение-расширенное)
  - [Практические примеры: Работа с временными рядами](#практические-примеры-работа-с-временными-рядами)
  - [Практические примеры: Обработка больших файлов](#практические-примеры-обработка-больших-файлов)
  - [Использование с различными техниками для оптимизации](#использование-с-различными-техниками-для-оптимизации)
  - [Использование с различными техниками для обработки больших файлов](#использование-с-различными-техниками-для-обработки-больших-файлов)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Трансформации

Трансформации позволяют преобразовывать элементы коллекции в другие значения или типы. Это одна из самых распространенных операций при работе с коллекциями в функциональном стиле.

### Map

Операция `map` применяет функцию преобразования к каждому элементу коллекции, создавая новую коллекцию с преобразованными значениями. **Map** является одной из самых фундаментальных операций в функциональном программировании и позволяет выразительно трансформировать данные без явных циклов. Операция **map** сохраняет структуру коллекции и ее размер, изменяя только значения элементов.

**Map** особенно полезен для преобразования данных из одного типа в другой, применения вычислений к каждому элементу и создания новых представлений данных. Операция выполняется лениво для некоторых коллекций (например, View), но для большинства коллекций создается новая коллекция немедленно.

**Операция `map` применяет функцию преобразования к каждому элементу коллекции:**

```scala
val list = List(1, 2, 3, 4, 5)

// Простое преобразование
// Функция _ * 2 применяется к каждому элементу
// Результирующая коллекция имеет тот же размер, что и исходная
val doubled = list.map(_ * 2)  // List(2, 4, 6, 8, 10)

// Преобразование типа
// toString преобразует числа в строки
// Map может изменять тип элементов коллекции
val strings = list.map(_.toString)  // List("1", "2", "3", "4", "5")

// С индексом
// zipWithIndex создает пары (элемент, индекс)
// Затем map преобразует пары в строки
val indexed = list.zipWithIndex.map { case (value, index) =>
  s"$index: $value"
}  // List("0: 1", "1: 2", "2: 3", "3: 4", "4: 5")
```

Операция `map` создает новую коллекцию того же размера, что и исходная. Каждый элемент исходной коллекции преобразуется в один элемент результирующей коллекции. Это гарантирует, что структура данных сохраняется, а трансформация применяется к каждому элементу независимо. **Map** является чистой функцией, не изменяющей исходную коллекцию, что делает его безопасным для использования в многопоточных программах.

### FlatMap

Операция `flatMap` применяет функцию, которая возвращает коллекцию, к каждому элементу, а затем разворачивает все результирующие коллекции в одну плоскую коллекцию. В отличие от `map`, который создает коллекцию коллекций, `flatMap` создает одну плоскую коллекцию, объединяя все результаты. Это особенно полезно для работы с вложенными структурами данных и для комбинирования операций фильтрации и трансформации.

**FlatMap** является основой для многих операций в функциональном программировании, включая **for-comprehensions**, которые компилируются в комбинацию **flatMap** и **map**. Операция **flatMap** позволяет создавать цепочки вычислений, где каждое вычисление может производить несколько результатов или не производить результатов вообще.

**Операция `flatMap` применяет функцию, которая возвращает коллекцию, к каждому элементу, а затем разворачивает все результирующие коллекции:**

```scala
val list = List(1, 2, 3)

// FlatMap - преобразует и разворачивает
// Функция n => List(n, n * 2) возвращает коллекцию для каждого элемента
// flatMap разворачивает все коллекции в одну плоскую структуру
// Каждый элемент преобразуется в два элемента, которые затем объединяются
val flatMapped = list.flatMap(n => List(n, n * 2))  // List(1, 2, 2, 4, 3, 6)
// Элемент 1 -> List(1, 2)
// Элемент 2 -> List(2, 4)
// Элемент 3 -> List(3, 6)
// Все списки объединяются в один: List(1, 2, 2, 4, 3, 6)

// Flatten - просто разворачивает
// Flatten разворачивает коллекцию коллекций в одну плоскую коллекцию
// Это эквивалентно flatMap с тождественной функцией
val nested = List(List(1, 2), List(3, 4))
val flat = nested.flatten  // List(1, 2, 3, 4)
// flatten эквивалентно flatMap(identity)
```

В отличие от `map`, который создает коллекцию коллекций, `flatMap` создает одну плоскую коллекцию. Это делает **flatMap** особенно полезным для работы с вложенными структурами данных, где нужно развернуть вложенность, или для операций, где каждый элемент может производить несколько результатов или не производить результатов вообще (например, при работе с `Option` или Try).

### Collect

**`collect` комбинирует фильтрацию и преобразование:**

```scala
val list = List(1, 2, 3, 4, 5)

// Collect с pattern matching
val evens = list.collect {
  case x if x % 2 == 0 => x * 2
}  // List(4, 8)
```

## Фильтрация

**Фильтрация позволяет выбирать элементы, удовлетворяющие определенному условию:**

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Filter - оставляет элементы, удовлетворяющие условию
val evens = numbers.filter(_ % 2 == 0)  // List(2, 4, 6, 8, 10)

// FilterNot - оставляет элементы, не удовлетворяющие условию
val odds = numbers.filterNot(_ % 2 == 0)  // List(1, 3, 5, 7, 9)

// Partition - разделяет на две части
val (evens2, odds2) = numbers.partition(_ % 2 == 0)
// evens2: List(2, 4, 6, 8, 10)
// odds2: List(1, 3, 5, 7, 9)
```

## Агрегация

Агрегация позволяет вычислять одно значение из коллекции, комбинируя все элементы с помощью функции. Операции агрегации являются основой для многих вычислений над коллекциями, таких как суммирование, умножение, поиск минимума или максимума. Агрегация позволяет выразить сложные вычисления декларативно, без явных циклов и накопления результатов в переменных.

Агрегация особенно полезна для вычисления статистики, накопления значений и преобразования коллекций в одно значение. Операции **fold** и **reduce** предоставляют общий механизм для различных видов агрегации, позволяя определять, как элементы комбинируются.

**Агрегация позволяет вычислять одно значение из коллекции:**

### Fold и Reduce

**Fold** и **reduce** являются основными операциями агрегации в **Scala**. **Reduce** не требует начального значения и использует первый элемент коллекции как начальное значение, что делает его удобным для непустых коллекций. **Fold** требует явного начального значения, что делает его более безопасным и подходящим для пустых коллекций.

```scala
val numbers = List(1, 2, 3, 4, 5)

// Reduce - свертка без начального значения
// Reduce использует первый элемент как начальное значение
// Для пустой коллекции reduce выбросит исключение
// Функция применяется последовательно: ((1 + 2) + 3) + 4) + 5
val sum = numbers.reduce(_ + _)  // 15

// Fold - свертка с начальным значением
// Fold безопасен для пустых коллекций, так как имеет начальное значение
// Начальное значение используется, если коллекция пуста
val sum2 = numbers.fold(0)(_ + _)  // 15
// 0 + 1 + 2 + 3 + 4 + 5 = 15

// Fold с другим начальным значением
// Начальное значение 1 используется для умножения
val product = numbers.fold(1)(_ * _)  // 120
// 1 * 1 * 2 * 3 * 4 * 5 = 120

// FoldLeft - слева направо
// FoldLeft гарантирует порядок обработки слева направо
// Это важно для некоммутативных операций
val leftFold = numbers.foldLeft(0)(_ + _)  // 15
// ((((0 + 1) + 2) + 3) + 4) + 5

// FoldRight - справа налево
// FoldRight обрабатывает элементы справа налево
// Может быть менее эффективным для списков из-за необходимости обхода с конца
val rightFold = numbers.foldRight(0)(_ + _)  // 15
// 1 + (2 + (3 + (4 + (5 + 0))))
```

**Fold** и **reduce** позволяют выразить различные виды агрегации единообразным способом. Выбор между **fold** и **reduce** зависит от того, нужно ли начальное значение и может ли коллекция быть пустой. **FoldLeft** и **foldRight** различаются порядком обработки элементов, что важно для некоммутативных операций.

### Агрегатные функции

```scala
val numbers = List(1, 2, 3, 4, 5)

numbers.sum      // 15
numbers.product  // 120
numbers.min      // 1
numbers.max      // 5
numbers.minOption  // Some(1)
numbers.maxOption  // Some(5)
numbers.average  // 3.0 (для числовых коллекций)
```

## Поиск и выборка

```scala
val numbers = List(1, 2, 3, 4, 5)

// Поиск первого элемента
numbers.find(_ > 3)  // Some(4)
numbers.find(_ > 10)  // None

// Проверка существования
numbers.exists(_ > 3)  // true
numbers.forall(_ > 0)  // true

// Подсчет
numbers.count(_ > 3)  // 2

// Take и Drop
numbers.take(3)  // List(1, 2, 3)
numbers.drop(3)  // List(4, 5)
numbers.takeWhile(_ < 4)  // List(1, 2, 3)
numbers.dropWhile(_ < 3)  // List(3, 4, 5)
```

## Сортировка

```scala
val numbers = List(5, 2, 8, 1, 9, 3)

// Сортировка по возрастанию
val sorted = numbers.sorted  // List(1, 2, 3, 5, 8, 9)

// Сортировка по убыванию
val reversed = numbers.sorted.reverse  // List(9, 8, 5, 3, 2, 1)
val sortedDesc = numbers.sortWith(_ > _)  // то же самое

// Сортировка по ключу
case class Person(name: String, age: Int)
val people = List(Person("Alice", 30), Person("Bob", 25), Person("Charlie", 35))
val sortedByAge = people.sortBy(_.age)  // по возрасту
val sortedByName = people.sortBy(_.name)  // по имени
```

## Объединение и разделение

```scala
val list1 = List(1, 2, 3)
val list2 = List(4, 5, 6)

// Объединение
val combined = list1 ++ list2  // List(1, 2, 3, 4, 5, 6)

// Zip - объединение в пары
val zipped = list1.zip(list2)  // List((1, 4), (2, 5), (3, 6))

// Unzip - разделение на два списка
val (nums, lets) = zipped.unzip

// Slice - получение подсписка
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
numbers.slice(2, 5)  // List(3, 4, 5)
```

## Лучшие практики

### Композиция операций

```scala
// Хорошо - композиция операций
val result = numbers
  .filter(_ > 0)
  .map(_ * 2)
  .filter(_ < 100)
  .sum

// Плохо - множественные проходы
val filtered = numbers.filter(_ > 0)
val mapped = filtered.map(_ * 2)
val filtered2 = mapped.filter(_ < 100)
val result = filtered2.sum
```

### Использование view для ленивых вычислений

```scala
// View создает ленивую коллекцию
val view = (1 to 1000000).view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(10)
  .toList  // вычисления выполняются только здесь
```

### Zip и Unzip

```scala
val numbers = List(1, 2, 3)
val letters = List('a', 'b', 'c')

// Zip - объединение в пары
val zipped = numbers.zip(letters)  // List((1, 'a'), (2, 'b'), (3, 'c'))

// Zip с индексом
val withIndex = numbers.zipWithIndex  // List((1, 0), (2, 1), (3, 2))

// ZipAll - с заполнением недостающих элементов
val zippedAll = numbers.zipAll(List('a', 'b'), 0, 'x')
// List((1, 'a'), (2, 'b'), (3, 'x'))

// Unzip - разделение на два списка
val (nums, lets) = zipped.unzip
// nums: List(1, 2, 3)
// lets: List('a', 'b', 'c')
```

### Sliding и Grouped

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Grouped - разбиение на группы фиксированного размера
val grouped = numbers.grouped(3).toList
// List(List(1, 2, 3), List(4, 5, 6), List(7, 8, 9), List(10))

// Sliding - скользящее окно
val sliding = numbers.sliding(3).toList
// List(List(1, 2, 3), List(2, 3, 4), List(3, 4, 5), ...)

// Sliding с размером шага
val slidingStep = numbers.sliding(3, 2).toList
// List(List(1, 2, 3), List(3, 4, 5), List(5, 6, 7), ...)
```

### Scan

**`scan` похож на `fold`, но возвращает все промежуточные результаты:**

```scala
val numbers = List(1, 2, 3, 4, 5)

// Scan - все промежуточные результаты
val scanned = numbers.scan(0)(_ + _)
// List(0, 1, 3, 6, 10, 15)

// ScanLeft
val scannedLeft = numbers.scanLeft(0)(_ + _)
// List(0, 1, 3, 6, 10, 15)

// ScanRight
val scannedRight = numbers.scanRight(0)(_ + _)
// List(15, 14, 12, 9, 5, 0)
```

### Практический пример: Обработка данных

```scala
case class Sale(product: String, amount: Double, date: String)

val sales = List(
  Sale("Product1", 100.0, "2023-01-01"),
  Sale("Product2", 200.0, "2023-01-02"),
  Sale("Product3", 150.0, "2023-01-03")
)

// Композиция операций
val result = sales
  .filter(_.amount > 100)
  .map(_.amount)
  .sum
// 350.0

// Группировка и агрегация
val byProduct = sales
  .groupBy(_.product)
  .mapValues(_.map(_.amount).sum)
// Map("Product1" -> 100.0, "Product2" -> 200.0, "Product3" -> 150.0)
```

### Практический пример: Трансформация данных

```scala
case class User(id: Long, name: String, age: Int, city: String)

val users = List(
  User(1, "Alice", 30, "New York"),
  User(2, "Bob", 25, "London"),
  User(3, "Charlie", 35, "New York")
)

// Фильтрация и трансформация
val activeUsers = users
  .filter(_.age >= 30)
  .map(u => (u.name, u.city))
// List(("Alice", "New York"), ("Charlie", "New York"))

// Группировка и подсчет
val byCity = users
  .groupBy(_.city)
  .mapValues(_.length)
// Map("New York" -> 2, "London" -> 1)
```

### Практический пример: Обработка временных рядов

```scala
case class Event(timestamp: Long, value: Double)

val events = List(
  Event(1000, 10.0),
  Event(2000, 20.0),
  Event(3000, 15.0),
  Event(4000, 25.0)
)

// Скользящее среднее
def movingAverage(events: List[Event], windowSize: Int): List[Double] = {
  events
    .sliding(windowSize)
    .map(window => window.map(_.value).sum / window.size)
    .toList
}

val averages = movingAverage(events, 2)
// List(15.0, 17.5, 20.0)
```

### Комбинирование операций

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Цепочка операций
val result = numbers
  .filter(_ % 2 == 0)      // List(2, 4, 6, 8, 10)
  .map(_ * 2)              // List(4, 8, 12, 16, 20)
  .filter(_ > 10)          // List(12, 16, 20)
  .sum                     // 48

// С использованием view для ленивых вычислений
val lazyResult = numbers.view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .filter(_ > 10)
  .take(2)
  .toList
// List(12, 16) - вычисления выполняются только при toList
```

### Параллельные операции

```scala
import scala.collection.parallel.CollectionConverters._

val largeList = (1 to 1000000).toList

// Параллельная обработка
val parallel = largeList.par
val result = parallel
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .sum

// Параллельные операции автоматически распределяются по ядрам процессора
```

### Операции с индексами

```scala
val list = List("a", "b", "c", "d", "e")

// Zip с индексом
val withIndex = list.zipWithIndex
// List(("a", 0), ("b", 1), ("c", 2), ("d", 3), ("e", 4))

// Индексы элементов, удовлетворяющих условию
val indices = list.zipWithIndex
  .filter(_._1 == "c")
  .map(_._2)
// List(2)

// Обновление элементов по индексу
val updated = list.zipWithIndex.map {
  case (value, index) if index % 2 == 0 => value.toUpperCase
  case (value, _) => value
}
// List("A", "b", "C", "d", "E")
```

## Лучшие практики

### Композиция операций

```scala
// Хорошо - композиция операций
val result = numbers
  .filter(_ > 0)
  .map(_ * 2)
  .filter(_ < 100)
  .sum

// Плохо - множественные проходы
val filtered = numbers.filter(_ > 0)
val mapped = filtered.map(_ * 2)
val filtered2 = mapped.filter(_ < 100)
val result = filtered2.sum
```

### Использование view для ленивых вычислений

```scala
// Хорошо - использование view для больших коллекций
val view = (1 to 1000000).view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(10)
  .toList  // вычисления выполняются только здесь

// Плохо - создание промежуточных коллекций
val result = (1 to 1000000)
  .filter(_ % 2 == 0)  // создается промежуточная коллекция
  .map(_ * 2)          // создается еще одна промежуточная коллекция
  .take(10)
  .toList
```

### Использование параллельных коллекций для больших данных

```scala
// Хорошо - параллельная обработка для больших коллекций
val largeList = (1 to 1000000).toList
val result = largeList.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .sum

// Плохо - последовательная обработка больших коллекций
val result2 = largeList
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .sum
```

### Избегание ненужных операций

```scala
// Хорошо - только необходимые операции
val result = numbers
  .filter(_ > 0)
  .sum

// Плохо - ненужные операции
val result2 = numbers
  .filter(_ > 0)
  .map(identity)  // ненужная операция
  .sum
```

## Продвинутые техники работы с операциями

### Композиция операций

Композиция операций позволяет создавать сложные трансформации данных.

```scala
// Создание переиспользуемых цепочек операций
def processNumbers(numbers: List[Int]): List[Int] = {
  numbers
    .filter(_ > 0)
    .map(_ * 2)
    .distinct
    .sorted
}

// Композиция с различными типами данных
def processUsers(users: List[User]): List[String] = {
  users
    .filter(_.age >= 18)
    .map(_.name)
    .filter(_.nonEmpty)
    .sorted
}
```

### Оптимизация операций

Оптимизация операций критична для производительности.

```scala
// Использование view для ленивых вычислений
val largeList = (1 to 1000000).toList
val result = largeList.view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toList  // Вычисляется только необходимое

// Использование Iterator для потоковой обработки
val iterator = (1 to 1000000).iterator
val processed = iterator
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toList
```

### Параллельные операции

Параллельные операции позволяют обрабатывать большие объемы данных эффективно.

```scala
// Параллельная обработка
val largeList = (1 to 1000000).toList
val parallelResult = largeList.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq  // Преобразование обратно в последовательную коллекцию
```

### Практические примеры: Обработка больших наборов данных

```scala
import scala.io.Source

// Обработка больших файлов через Iterator
def processLargeFile(filePath: String): Iterator[String] = {
  Source.fromFile(filePath)
    .getLines()
    .filter(_.nonEmpty)
    .map(_.trim.toUpperCase)
}

// Параллельная обработка с chunking
def processInParallel[A, B](
  items: List[A],
  chunkSize: Int = 100,
  processor: A => B
)(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[List[B]] = {
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  val chunks = items.grouped(chunkSize).toList
  val futures = chunks.map(chunk => Future(chunk.map(processor)))
  Future.sequence(futures).map(_.flatten)
}
```

### Практические примеры: Фильтрация и трансформация данных

```scala
case class Product(
  id: String,
  name: String,
  price: Double,
  category: String,
  inStock: Boolean,
  rating: Double
)

class ProductFilter(products: List[Product]) {
  // Поиск продуктов с фильтрами
  def findProducts(
    minPrice: Option[Double] = None,
    maxPrice: Option[Double] = None,
    category: Option[String] = None,
    minRating: Option[Double] = None,
    inStockOnly: Boolean = false
  ): List[Product] = {
    products
      .filter(p => minPrice.forall(p.price >= _))
      .filter(p => maxPrice.forall(p.price <= _))
      .filter(p => category.forall(_ == p.category))
      .filter(p => minRating.forall(p.rating >= _))
      .filter(p => !inStockOnly || p.inStock)
  }

  // Сортировка с приоритетами
  def sortByMultipleCriteria: List[Product] = {
    products.sortBy(p => (
      !p.inStock,      // inStock первыми
      -p.rating,       // высокий рейтинг первым
      p.price          // низкая цена первым
    ))
  }

  // Поиск похожих продуктов
  def findSimilar(product: Product, limit: Int = 5): List[Product] = {
    products
      .filter(p => p.id != product.id && p.category == product.category)
      .sortBy(p => Math.abs(p.price - product.price))
      .take(limit)
  }
}
```

## Заключение (расширенное)

Операции над коллекциями являются основой работы с данными в **Scala**. Понимание различных операций, от базовых трансформаций (map, `flatMap`, collect) и фильтрации (filter, partition) до продвинутых техник агрегации (fold, reduce, scan), группировки (zip, sliding, grouped), композиции операций, оптимизации операций, параллельных операций, обработки больших файлов, фильтрации продуктов и практических применений (обработка данных, временные ряды) позволяет эффективно обрабатывать данные. Правильный выбор операций, их комбинирование, использование **view** для ленивых вычислений, **Iterator** для потоковой обработки, параллельных коллекций для больших данных, обработка больших файлов и фильтрация продуктов помогает создавать читаемый, эффективный и масштабируемый код. Операции над коллекциями особенно важны для создания приложений, которые должны обрабатывать большие объемы данных, выполнять сложные трансформации, и обеспечивать высокую производительность.

### Практические примеры: Работа с временными рядами

```scala
// Обработка временных рядов
case class DataPoint(timestamp: Long, value: Double)

val timeSeries = List(
  DataPoint(1000, 10.0),
  DataPoint(2000, 15.0),
  DataPoint(3000, 12.0),
  DataPoint(4000, 18.0)
)

// Скользящее среднее
def movingAverage(series: List[DataPoint], windowSize: Int): List[Double] = {
  series.sliding(windowSize).map(window =>
    window.map(_.value).sum / window.size
  ).toList
}

val averages = movingAverage(timeSeries, 2)
// List(12.5, 13.5, 15.0)
```

### Практические примеры: Обработка больших файлов

```scala
import scala.io.Source

// Обработка больших файлов построчно
def processLargeFile(filePath: String): Iterator[String] = {
  Source.fromFile(filePath).getLines()
    .filter(_.nonEmpty)
    .map(_.trim)
    .map(_.toUpperCase)
}

// Обработка без загрузки всего файла в память
val lines = processLargeFile("large-file.txt")
val result = lines.take(1000).toList
```

### Использование с различными техниками для оптимизации

```scala
// Оптимизация операций над коллекциями
val largeList = (1 to 1000000).toList

// Использование view для ленивых вычислений
val processed = largeList.view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toList

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallelProcessed = largeList.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Использование с различными техниками для обработки больших файлов

```scala
import scala.io.Source

// Обработка больших файлов построчно
def processLargeFile(filePath: String): Iterator[String] = {
  Source.fromFile(filePath).getLines()
    .filter(_.nonEmpty)
    .map(_.trim.toUpperCase)
}

// Обработка без загрузки всего файла в память
val lines = processLargeFile("large-file.txt")
val result = lines.take(1000).toList
```

## Дополнительные ресурсы

**Для дальнейшего изучения операций над коллекциями в **Scala** рекомендуется:**

- [Scala Collections Documentation](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)
- [Scala Collections Performance](https://docs.scala-lang.org/overviews/collections-2.13/performance-characteristics.html)
- [Scala Collections Operations](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)
