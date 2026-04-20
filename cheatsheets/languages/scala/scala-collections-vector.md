---
title: "Scala Collections — Vector"
description: "Полное руководство по Vector в Scala: эффективная структура данных для произвольного доступа, операции, производительность"
tags:
  - scala
  - collections
  - vector
  - functional-programming
  - performance
difficulty: "intermediate"
prerequisites: ["scala/scala-collections.md"]
next: []
updated: "2026-04-20"
related: ["scala/scala-collections-list.md", "scala/scala-collections.md"]
---

# Scala Collections — Vector

Кратко: полное руководство по **Vector** в **Scala**: эффективная структура данных для произвольного доступа, операции, производительность.

## Полезные ссылки

### Официальная документация
- [Scala Vector API](https://www.scala-lang.org/api/current/scala/collection/immutable/Vector.html)

### См. также
- [[scala-collections-list|Списки (List)]]
- [[scala-collections|Обзор коллекций]]

- [[scala-collections-grouping|Scala Collections — Grouping and Aggregation]]
- [[scala-collections-array|Scala Collections — Array]]
- [[scala-collections-set|Scala Collections — Set]]
## Содержание

- [Введение в Vector](#введение-в-vector)
  - [Основные характеристики](#основные-характеристики)
- [Создание Vector](#создание-vector)
- [Основные операции](#основные-операции)
- [Производительность](#производительность)
  - [Временная сложность операций](#временная-сложность-операций)
- [Сравнение с List](#сравнение-с-list)
  - [Трансформации](#трансформации)
  - [Агрегация](#агрегация)
  - [Поиск и проверки](#поиск-и-проверки)
  - [Работа с подвекторами](#работа-с-подвекторами)
  - [Группировка и разбиение](#группировка-и-разбиение)
  - [Сортировка](#сортировка)
  - [Объединение векторов](#объединение-векторов)
  - [Практический пример: Матричные операции](#практический-пример-матричные-операции)
  - [Практический пример: Обработка больших данных](#практический-пример-обработка-больших-данных)
  - [Внутренняя структура Vector](#внутренняя-структура-vector)
- [Лучшие практики](#лучшие-практики)
  - [Использование Vector для произвольного доступа](#использование-vector-для-произвольного-доступа)
  - [Использование Vector для частых обновлений](#использование-vector-для-частых-обновлений)
  - [Выбор между Vector и List](#выбор-между-vector-и-list)
- [Продвинутые техники работы с Vector](#продвинутые-техники-работы-с-vector)
  - [Оптимизация производительности Vector](#оптимизация-производительности-vector)
  - [Работа с большими Vector](#работа-с-большими-vector)
  - [Сравнение Vector с другими структурами](#сравнение-vector-с-другими-структурами)
- [Заключение (расширенное)](#заключение-расширенное)
  - [Практические примеры: Матричные операции с Vector](#практические-примеры-матричные-операции-с-vector)
  - [Практические примеры: Эффективный произвольный доступ](#практические-примеры-эффективный-произвольный-доступ)
  - [Практические примеры: Обработка больших Vector](#практические-примеры-обработка-больших-vector)
  - [Практические примеры: Работа с индексами и обновлениями](#практические-примеры-работа-с-индексами-и-обновлениями)
  - [Практические примеры: Работа с большими Vector](#практические-примеры-работа-с-большими-vector)
  - [Практические примеры: Работа с вложенными Vector](#практические-примеры-работа-с-вложенными-vector)
  - [Практические примеры: Оптимизация производительности Vector](#практические-примеры-оптимизация-производительности-vector)
  - [Практические примеры: Работа с окнами и слайсами](#практические-примеры-работа-с-окнами-и-слайсами)
  - [Практические примеры: Работа с индексами и обновлениями](#практические-примеры-работа-с-индексами-и-обновлениями-1)
  - [Практические примеры: Работа с большими Vector](#практические-примеры-работа-с-большими-vector-1)
  - [Использование с различными операциями для производительности](#использование-с-различными-операциями-для-производительности)
  - [Использование с различными операциями для работы с индексами](#использование-с-различными-операциями-для-работы-с-индексами)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в Vector

**Vector** — это неизменяемая структура данных, оптимизированная для произвольного доступа. **Vector** обеспечивает эффективный доступ по индексу и хорошую производительность для большинства операций.

### Основные характеристики

- **Произвольный доступ**: `O(log32(n)`) доступ по индексу, что практически константно для большинства случаев
- **Неизменяемость**: **Vector** является неизменяемой структурой данных
- **Сбалансированная производительность**: хорошая производительность для большинства операций
- **Persistent структура**: эффективное создание новых версий при модификации

## Создание Vector

```scala
// Пустой Vector
val empty: Vector[Int] = Vector.empty
val empty2 = Vector.empty[Int]

// Vector с элементами
val numbers = Vector(1, 2, 3, 4, 5)
val names = Vector("Alice", "Bob", "Charlie")

// Из Range
val range = (1 to 10).toVector

// Из List
val fromList = List(1, 2, 3).toVector
```

## Основные операции

```scala
val vec = Vector(1, 2, 3, 4, 5)

// Доступ по индексу
vec(0)          // 1
vec(2)          // 3
vec.apply(0)    // 1

// Проверки
vec.isEmpty     // false
vec.nonEmpty    // true
vec.length      // 5
vec.size        // 5

// Добавление элементов
val appended = vec :+ 6        // Vector(1, 2, 3, 4, 5, 6)
val prepended = 0 +: vec       // Vector(0, 1, 2, 3, 4, 5)

// Обновление элементов
val updated = vec.updated(2, 10)  // Vector(1, 2, 10, 4, 5)

// Удаление элементов
val removed = vec.patch(2, Nil, 1)  // Vector(1, 2, 4, 5)
```

## Производительность

### Временная сложность операций

- **Доступ по индексу**: `O(log32(n)`) — практически константное время
- **Добавление в конец**: `O(log32(n)`) — эффективно
- **Добавление в начало**: `O(log32(n)`) — эффективно
- **Обновление элемента**: `O(log32(n)`) — эффективно
- **Итерация**: `O(n)` - один проход по элементам

**Vector** обеспечивает сбалансированную производительность для большинства операций, что делает его хорошим выбором для многих сценариев.

## Сравнение с List

```scala
// List - эффективен для операций в начале
val list = 0 :: List(1, 2, 3)  // O(1)

// Vector - эффективен для произвольного доступа
val vec = Vector(1, 2, 3)
vec(1)  // O(log32(n)) - быстрее, чем List для больших коллекций
```

**Vector** предпочтительнее **List**, когда нужен частый доступ по индексу или операции в середине коллекции.

### Трансформации

**Vector** поддерживает стандартные операции трансформации:**

```scala
val vec = Vector(1, 2, 3, 4, 5)

// Map - преобразование каждого элемента
vec.map(_ * 2)  // Vector(2, 4, 6, 8, 10)

// Filter - фильтрация элементов
vec.filter(_ > 2)  // Vector(3, 4, 5)

// FlatMap - преобразование и разворачивание
vec.flatMap(x => Vector(x, x * 2))  // Vector(1, 2, 2, 4, 3, 6, 4, 8, 5, 10)

// Collect - комбинация filter и map
vec.collect {
  case x if x % 2 == 0 => x * 2
}  // Vector(4, 8)
```

### Агрегация

**Vector** поддерживает операции агрегации:**

```scala
val vec = Vector(1, 2, 3, 4, 5)

// Sum, product, min, max
vec.sum      // 15
vec.product  // 120
vec.min      // 1
vec.max      // 5

// Fold и reduce
vec.fold(0)(_ + _)     // 15
vec.reduce(_ + _)       // 15
vec.foldLeft(0)(_ + _)  // 15

// Count
vec.count(_ > 2)  // 3
```

### Поиск и проверки

```scala
val vec = Vector(1, 2, 3, 4, 5)

// Проверка наличия
vec.contains(3)  // true
vec.exists(_ > 4)  // true
vec.forall(_ > 0)  // true

// Поиск элементов
vec.find(_ > 3)  // Some(4)
vec.indexWhere(_ > 3)  // 3
vec.lastIndexWhere(_ > 3)  // 4
```

### Работа с подвекторами

```scala
val vec = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Slice - получение подвектора
vec.slice(2, 5)  // Vector(3, 4, 5)

// Take и drop
vec.take(3)  // Vector(1, 2, 3)
vec.drop(3)  // Vector(4, 5, 6, 7, 8, 9, 10)
vec.takeWhile(_ < 5)  // Vector(1, 2, 3, 4)
vec.dropWhile(_ < 5)  // Vector(5, 6, 7, 8, 9, 10)

// SplitAt
val (left, right) = vec.splitAt(5)
// left: Vector(1, 2, 3, 4, 5)
// right: Vector(6, 7, 8, 9, 10)
```

### Группировка и разбиение

```scala
val vec = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// GroupBy
vec.groupBy(_ % 2)
// Map(1 -> Vector(1, 3, 5, 7, 9), 0 -> Vector(2, 4, 6, 8, 10))

// Partition
val (even, odd) = vec.partition(_ % 2 == 0)
// even: Vector(2, 4, 6, 8, 10)
// odd: Vector(1, 3, 5, 7, 9)

// Grouped - разбиение на группы фиксированного размера
vec.grouped(3).toVector
// Vector(Vector(1, 2, 3), Vector(4, 5, 6), Vector(7, 8, 9), Vector(10))

// Sliding - скользящее окно
vec.sliding(3).toVector
// Vector(Vector(1, 2, 3), Vector(2, 3, 4), Vector(3, 4, 5), ...)
```

### Сортировка

```scala
val vec = Vector(3, 1, 4, 1, 5, 9, 2, 6)

// Сортировка
val sorted = vec.sorted  // Vector(1, 1, 2, 3, 4, 5, 6, 9)

// Сортировка с кастомным компаратором
val strings = Vector("banana", "apple", "cherry")
strings.sortWith(_ < _)  // Vector("apple", "banana", "cherry")

// Сортировка по ключу
case class Person(name: String, age: Int)
val people = Vector(Person("Alice", 30), Person("Bob", 25), Person("Charlie", 35))
val sortedByAge = people.sortBy(_.age)  // Vector(Person("Bob", 25), Person("Alice", 30), Person("Charlie", 35))
```

### Объединение векторов

```scala
val vec1 = Vector(1, 2, 3)
val vec2 = Vector(4, 5, 6)

// Конкатенация
val combined = vec1 ++ vec2  // Vector(1, 2, 3, 4, 5, 6)

// Добавление одного элемента
val withElement = vec1 :+ 4  // Vector(1, 2, 3, 4)
val prepended = 0 +: vec1     // Vector(0, 1, 2, 3)
```

### Практический пример: Матричные операции

**Vector** эффективен для матричных операций:**

```scala
// Представление матрицы как Vector of Vectors
type Matrix = Vector[Vector[Double]]

def matrixAdd(m1: Matrix, m2: Matrix): Matrix = {
  require(m1.length == m2.length && m1.head.length == m2.head.length,
    "Matrices must have the same dimensions")
  (m1 zip m2).map { case (row1, row2) =>
    (row1 zip row2).map { case (a, b) => a + b }
  }
}

def matrixMultiply(m1: Matrix, m2: Matrix): Matrix = {
  require(m1.head.length == m2.length, "Invalid dimensions for multiplication")
  m1.map { row1 =>
    (0 until m2.head.length).map { j =>
      (row1 zip m2.map(_(j))).map { case (a, b) => a * b }.sum
    }.toVector
  }
}

// Использование
val m1 = Vector(Vector(1.0, 2.0), Vector(3.0, 4.0))
val m2 = Vector(Vector(5.0, 6.0), Vector(7.0, 8.0))
val sum = matrixAdd(m1, m2)  // Vector(Vector(6.0, 8.0), Vector(10.0, 12.0))
```

### Практический пример: Обработка больших данных

**Vector** эффективен для обработки больших объемов данных:**

```scala
// Создание большого вектора
val largeVector = (1 to 1000000).toVector

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallel = largeVector.par
val doubled = parallel.map(_ * 2).toVector

// Batch обработка
def processBatch[T](vec: Vector[T], batchSize: Int)(f: Vector[T] => Unit): Unit = {
  vec.grouped(batchSize).foreach(f)
}

processBatch(largeVector, 1000) { batch =>
  val sum = batch.sum
  println(s"Batch sum: $sum")
}
```

### Внутренняя структура Vector

**Vector** использует **trie** структуру (32-way tree) для эффективного доступа:**

```scala
// Vector использует trie структуру с branching factor 32
// Это обеспечивает O(log32(n)) доступ, что практически константно
// для большинства практических размеров

// Для Vector размером до 32 элементов: O(1)
// Для Vector размером до 1024 элементов: O(2)
// Для Vector размером до 32768 элементов: O(3)
// И так далее
```

Эта структура обеспечивает эффективный доступ и обновление элементов.

## Лучшие практики

### Использование Vector для произвольного доступа

```scala
// Хорошо - использование Vector для доступа по индексу
val vec = Vector(1, 2, 3, 4, 5)
val element = vec(2)  // эффективно

// Плохо - использование List для доступа по индексу
val list = List(1, 2, 3, 4, 5)
val element = list(2)  // O(n) - медленно для больших списков
```

### Использование Vector для частых обновлений

```scala
// Vector эффективен для обновлений
val vec = Vector(1, 2, 3, 4, 5)
val updated = vec.updated(2, 10)  // O(log32(n))
```

### Выбор между Vector и List

```scala
// Используйте List для:
// - Частых операций в начале списка
// - Функционального стиля с pattern matching
val list = 0 :: List(1, 2, 3)  // O(1)

// Используйте Vector для:
// - Частого доступа по индексу
// - Операций в середине коллекции
// - Сбалансированной производительности
val vec = Vector(1, 2, 3)
val element = vec(1)  // O(log32(n)) - эффективно
```

## Продвинутые техники работы с Vector

### Оптимизация производительности Vector

**Vector** оптимизирован для различных операций.

```scala
// Эффективный доступ по индексу
val vec = Vector(1, 2, 3, 4, 5)
val element = vec(2)  // O(log32(n)) - эффективно

// Эффективное обновление
val updated = vec.updated(2, 10)  // O(log32(n))

// Эффективное добавление в конец
val appended = vec :+ 6  // O(log32(n))

// Эффективное добавление в начало
val prepended = 0 +: vec  // O(log32(n))
```

### Работа с большими Vector

**Vector** эффективно работает с большими объемами данных.

```scala
// Создание большого Vector
val largeVector = Vector.range(1, 1000000)

// Эффективная обработка
val processed = largeVector
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)

// Параллельная обработка
val parallelProcessed = largeVector.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Сравнение Vector с другими структурами

**Vector** предоставляет сбалансированную производительность.

```scala
// Vector vs List
val list = List(1, 2, 3, 4, 5)
val vec = Vector(1, 2, 3, 4, 5)

// Доступ по индексу
list(2)  // O(n) - медленно
vec(2)   // O(log32(n)) - быстро

// Добавление в начало
0 :: list  // O(1) - быстро
0 +: vec   // O(log32(n)) - медленнее, но приемлемо

// Добавление в конец
list :+ 6  // O(n) - медленно
vec :+ 6   // O(log32(n)) - быстро
```

## Заключение (расширенное)

### Практические примеры: Матричные операции с Vector

```scala
// Создание матрицы
type Matrix = Vector[Vector[Int]]

def createMatrix(rows: Int, cols: Int): Matrix = {
  Vector.fill(rows)(Vector.fill(cols)(0))
}

// Сложение матриц
def addMatrices(m1: Matrix, m2: Matrix): Matrix = {
  m1.zip(m2).map { case (row1, row2) =>
    row1.zip(row2).map { case (a, b) => a + b }
  }
}

// Умножение матриц
def multiplyMatrices(m1: Matrix, m2: Matrix): Matrix = {
  val cols = m2.head.length
  Vector.tabulate(m1.length, cols) { (i, j) =>
    (0 until m1.head.length).map(k => m1(i)(k) * m2(k)(j)).sum
  }
}
```

### Практические примеры: Эффективный произвольный доступ

```scala
// Быстрый доступ к элементам
val vector = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Доступ по индексу - O(log32(n))
val element = vector(5)  // 6

// Обновление элемента - O(log32(n))
val updated = vector.updated(5, 99)  // Vector(1, 2, 3, 4, 5, 99, 7, 8, 9, 10)

// Батч обновления
val multipleUpdates = vector
  .updated(0, 10)
  .updated(5, 60)
  .updated(9, 100)
```

### Практические примеры: Обработка больших Vector

```scala
// Параллельная обработка больших Vector
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def processVectorParallel[A, B](
  vector: Vector[A],
  chunkSize: Int,
  processor: A => B
): Future[Vector[B]] = {
  val chunks = vector.grouped(chunkSize).toVector
  val futures = chunks.map(chunk => Future(chunk.map(processor)))
  Future.sequence(futures).map(_.flatten.toVector)
}

// Слайсинг и windowing
val largeVector = (1 to 1000000).toVector
val window = largeVector.slice(100, 200)  // Эффективно
val sliding = largeVector.sliding(10).toVector  // Создает окна
```

**Vector** является эффективной структурой данных для сценариев, требующих произвольного доступа и сбалансированной производительности. Понимание его характеристик производительности, внутренней структуры (trie), операций трансформации, агрегации, оптимизации производительности, работы с большими **Vector**, сравнения с другими структурами данных, сравнения с **List**, матричных операций, эффективного произвольного доступа, параллельной обработки и практических применений позволяет выбирать правильную структуру данных для конкретных задач. **Vector** особенно полезен для матричных операций, обработки больших данных, сценариев, где нужен эффективный доступ по индексу, случаев, когда требуется сбалансированная производительность для различных операций, математических вычислений и параллельной обработки данных.

### Практические примеры: Работа с индексами и обновлениями

```scala
// Эффективное обновление элементов по индексу
val vector = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Обновление одного элемента
val updated = vector.updated(5, 99)  // Vector(1, 2, 3, 4, 5, 99, 7, 8, 9, 10)

// Обновление нескольких элементов
val multipleUpdates = vector
  .updated(0, 10)
  .updated(5, 60)
  .updated(9, 100)

// Использование patch для замены диапазона
val patched = vector.patch(2, Vector(30, 40, 50), 3)
// Заменяет элементы с индекса 2 на 3 новых элемента
```

### Практические примеры: Работа с большими Vector

```scala
// Создание больших Vector
val largeVector = Vector.range(1, 1000000)

// Эффективная обработка больших Vector
val processed = largeVector
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallelProcessed = largeVector.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Практические примеры: Работа с вложенными Vector

```scala
// Вложенные Vector для представления матриц
type Matrix = Vector[Vector[Int]]

def createMatrix(rows: Int, cols: Int, value: Int = 0): Matrix = {
  Vector.fill(rows)(Vector.fill(cols)(value))
}

// Транспонирование матрицы
def transpose[A](matrix: Matrix): Matrix = {
  if (matrix.isEmpty) matrix
  else Vector.tabulate(matrix.head.length, matrix.length) { (i, j) =>
    matrix(j)(i)
  }
}

// Умножение матриц
def multiplyMatrices(m1: Matrix, m2: Matrix): Matrix = {
  require(m1.head.length == m2.length, "Invalid dimensions")
  Vector.tabulate(m1.length, m2.head.length) { (i, j) =>
    (0 until m1.head.length).map(k => m1(i)(k) * m2(k)(j)).sum
  }
}
```

### Практические примеры: Оптимизация производительности Vector

```scala
// Использование Vector для эффективного произвольного доступа
val vector = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Быстрый доступ по индексу - O(log32(n))
val element = vector(5)  // 6

// Быстрое обновление - O(log32(n))
val updated = vector.updated(5, 99)

// Эффективная конкатенация
val vec1 = Vector(1, 2, 3)
val vec2 = Vector(4, 5, 6)
val combined = vec1 ++ vec2  // O(log32(n))
```

### Практические примеры: Работа с окнами и слайсами

```scala
val vector = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Скользящие окна
val windows = vector.sliding(3).toVector
// Vector(Vector(1, 2, 3), Vector(2, 3, 4), Vector(3, 4, 5), ...)

// Группировка
val grouped = vector.grouped(3).toVector
// Vector(Vector(1, 2, 3), Vector(4, 5, 6), Vector(7, 8, 9), Vector(10))

// Разбиение на части
val (left, right) = vector.splitAt(5)
// left: Vector(1, 2, 3, 4, 5)
// right: Vector(6, 7, 8, 9, 10)
```

### Практические примеры: Работа с индексами и обновлениями

```scala
// Эффективное обновление элементов по индексу
val vector = Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Обновление одного элемента
val updated = vector.updated(5, 99)  // Vector(1, 2, 3, 4, 5, 99, 7, 8, 9, 10)

// Обновление нескольких элементов
val multipleUpdates = vector
  .updated(0, 10)
  .updated(5, 60)
  .updated(9, 100)

// Использование patch для замены диапазона
val patched = vector.patch(2, Vector(30, 40, 50), 3)
// Заменяет элементы с индекса 2 на 3 новых элемента
```

### Практические примеры: Работа с большими Vector

```scala
// Создание больших Vector
val largeVector = Vector.range(1, 1000000)

// Эффективная обработка больших Vector
val processed = largeVector
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallelProcessed = largeVector.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Использование с различными операциями для производительности

```scala
// Оптимизация операций над Vector
val vector = Vector.range(1, 1000000)

// Эффективная фильтрация и трансформация
val processed = vector
  .view  // Ленивое представление
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toVector  // Материализация только необходимых элементов

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallelProcessed = vector.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Использование с различными операциями для работы с индексами

```scala
// Эффективная работа с индексами
val vector = Vector.range(1, 100)

// Доступ по индексу
val value = vector(50)  // O(log32(n)) - практически O(1)

// Обновление по индексу
val updated = vector.updated(50, 999)  // O(log32(n))

// Получение диапазона
val slice = vector.slice(10, 20)  // O(log32(n) + k)

// Поиск индекса
val index = vector.indexOf(50)  // O(n)
```

## Дополнительные ресурсы

**Для дальнейшего изучения **Vector** в **Scala** рекомендуется:**

- [Scala Vector API Documentation](https://www.scala-lang.org/api/current/scala/collection/immutable/Vector.html)
- [Scala Collections Performance](https://docs.scala-lang.org/overviews/collections-2.13/performance-characteristics.html)
