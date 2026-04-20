---
title: "Scala Collections — Array"
description: "Полное руководство по Array в Scala: массивы, операции, производительность, взаимодействие с Java"
tags:
  - scala
  - collections
  - array
  - performance
  - java-interop
difficulty: "intermediate"
prerequisites: ["scala/scala-collections.md"]
next: []
updated: "2026-04-20"
related: ["scala/scala-collections-list.md", "scala/scala-interop-java.md"]
---

# Scala Collections — Array

Кратко: полное руководство по **Array** в **Scala**: массивы, операции, производительность, взаимодействие с **Java**.

## Полезные ссылки

### Официальная документация
- [Scala Array API](https://www.scala-lang.org/api/current/scala/Array.html)

### См. также
- [[scala-collections-list|Списки (List)]]
- [[scala-interop-java|Взаимодействие с Java]]

- [[scala-collections-vector|Scala Collections — Vector]]
- [[scala-collections-grouping|Scala Collections — Grouping and Aggregation]]
- [[scala-performance|Scala Performance]]
## Содержание

- [Введение в Array](#введение-в-array)
  - [Основные характеристики](#основные-характеристики)
- [Создание массивов](#создание-массивов)
- [Основные операции](#основные-операции)
- [Производительность](#производительность)
  - [Временная сложность операций](#временная-сложность-операций)
- [Взаимодействие с Java](#взаимодействие-с-java)
  - [Трансформации](#трансформации)
  - [Агрегация](#агрегация)
  - [Сортировка](#сортировка)
  - [Поиск элементов](#поиск-элементов)
  - [Работа с подмассивами](#работа-с-подмассивами)
  - [Группировка и разбиение](#группировка-и-разбиение)
  - [Многомерные массивы](#многомерные-массивы)
  - [Преобразование в другие коллекции](#преобразование-в-другие-коллекции)
  - [Практический пример: Обработка больших данных](#практический-пример-обработка-больших-данных)
  - [Практический пример: Математические операции](#практический-пример-математические-операции)
- [Лучшие практики](#лучшие-практики)
  - [Использование Array для производительности](#использование-array-для-производительности)
  - [Избегание ненужных преобразований](#избегание-ненужных-преобразований)
  - [Использование ArrayBuffer для динамических массивов](#использование-arraybuffer-для-динамических-массивов)
- [Продвинутые техники работы с Array](#продвинутые-техники-работы-с-array)
  - [Оптимизация производительности Array](#оптимизация-производительности-array)
  - [Работа с большими Array](#работа-с-большими-array)
  - [Интеграция с Java](#интеграция-с-java)
- [Заключение (расширенное)](#заключение-расширенное)
  - [Практические примеры: Работа с многомерными массивами](#практические-примеры-работа-с-многомерными-массивами)
  - [Практические примеры: Интеграция с Java библиотеками](#практические-примеры-интеграция-с-java-библиотеками)
  - [Практические примеры: Оптимизация производительности Array](#практические-примеры-оптимизация-производительности-array)
  - [Практические примеры: Работа с примитивными массивами](#практические-примеры-работа-с-примитивными-массивами)
  - [Практические примеры: Работа с многомерными массивами](#практические-примеры-работа-с-многомерными-массивами-1)
  - [Практические примеры: Оптимизация производительности Array](#практические-примеры-оптимизация-производительности-array-1)
  - [Практические примеры: Работа с ArrayBuffer](#практические-примеры-работа-с-arraybuffer)
  - [Практические примеры: Работа с Java Arrays](#практические-примеры-работа-с-java-arrays)
  - [Практические примеры: Работа с примитивными массивами](#практические-примеры-работа-с-примитивными-массивами-1)
  - [Практические примеры: Работа с многомерными массивами](#практические-примеры-работа-с-многомерными-массивами-2)
  - [Использование с различными операциями для производительности](#использование-с-различными-операциями-для-производительности)
  - [Использование с различными операциями для работы с индексами](#использование-с-различными-операциями-для-работы-с-индексами)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в Array

**Array** в **Scala** — это изменяемая структура данных фиксированного размера, которая соответствует **Java** массивам. **Array** обеспечивает максимальную производительность для произвольного доступа.

### Основные характеристики

- **Изменяемость**: **Array** является изменяемой структурой данных
- **Фиксированный размер**: размер массива задается при создании
- **Произвольный доступ**: `O(1)` доступ по индексу
- **Java совместимость**: **Array** соответствует **Java** массивам

## Создание массивов

```scala
// Пустой массив
val empty = new Array[Int](5)  // Array(0, 0, 0, 0, 0)

// Массив с элементами
val numbers = Array(1, 2, 3, 4, 5)
val names = Array("Alice", "Bob", "Charlie")

// Из Range
val range = (1 to 10).toArray

// Из List
val fromList = List(1, 2, 3).toArray
```

## Основные операции

```scala
val arr = Array(1, 2, 3, 4, 5)

// Доступ по индексу
arr(0)          // 1
arr(2)          // 3

// Изменение элементов
arr(0) = 10     // Array(10, 2, 3, 4, 5)

// Проверки
arr.length      // 5
arr.isEmpty     // false

// Итерация
arr.foreach(println)
arr.map(_ * 2)  // Array(2, 4, 6, 8, 10)
```

## Производительность

### Временная сложность операций

- **Доступ по индексу**: `O(1)` - константное время
- **Изменение элемента**: `O(1)` - константное время
- **Итерация**: `O(n)` - один проход по элементам

**Array** обеспечивает максимальную производительность для произвольного доступа и изменений.

## Взаимодействие с Java

**Array** полностью совместим с **Java** массивами:**

```scala
// Использование Java методов
val arr = Array(1, 2, 3, 4, 5)
java.util.Arrays.toString(arr)  // "[1, 2, 3, 4, 5]"

// Передача в Java методы
def javaMethod(arr: Array[Int]): Unit = {
  // Java код
}
```

### Трансформации

**Array** поддерживает стандартные операции трансформации:**

```scala
val arr = Array(1, 2, 3, 4, 5)

// Map - преобразование каждого элемента
arr.map(_ * 2)  // Array(2, 4, 6, 8, 10)

// Filter - фильтрация элементов
arr.filter(_ > 2)  // Array(3, 4, 5)

// FlatMap - преобразование и разворачивание
arr.flatMap(x => Array(x, x * 2))  // Array(1, 2, 2, 4, 3, 6, 4, 8, 5, 10)

// Collect - комбинация filter и map
arr.collect {
  case x if x % 2 == 0 => x * 2
}  // Array(4, 8)
```

### Агрегация

**Array** поддерживает операции агрегации:**

```scala
val arr = Array(1, 2, 3, 4, 5)

// Sum, product, min, max
arr.sum      // 15
arr.product  // 120
arr.min      // 1
arr.max      // 5

// Fold и reduce
arr.fold(0)(_ + _)     // 15
arr.reduce(_ + _)       // 15
arr.foldLeft(0)(_ + _)  // 15

// Count
arr.count(_ > 2)  // 3
```

### Сортировка

**Array** можно сортировать **in-place** или создавать отсортированную копию:**

```scala
val arr = Array(3, 1, 4, 1, 5, 9, 2, 6)

// Создание отсортированной копии
val sorted = arr.sorted  // Array(1, 1, 2, 3, 4, 5, 6, 9)

// Сортировка in-place
val mutable = Array(3, 1, 4, 1, 5, 9, 2, 6)
scala.util.Sorting.quickSort(mutable)
// mutable теперь Array(1, 1, 2, 3, 4, 5, 6, 9)

// Сортировка с кастомным компаратором
val strings = Array("banana", "apple", "cherry")
strings.sortWith(_ < _)  // Array("apple", "banana", "cherry")
```

### Поиск элементов

```scala
val arr = Array(1, 2, 3, 4, 5)

// Проверка наличия
arr.contains(3)  // true
arr.exists(_ > 4)  // true
arr.forall(_ > 0)  // true

// Поиск элементов
arr.find(_ > 3)  // Some(4)
arr.indexWhere(_ > 3)  // 3
arr.lastIndexWhere(_ > 3)  // 4
```

### Работа с подмассивами

```scala
val arr = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Slice - получение подмассива
arr.slice(2, 5)  // Array(3, 4, 5)

// Take и drop
arr.take(3)  // Array(1, 2, 3)
arr.drop(3)  // Array(4, 5, 6, 7, 8, 9, 10)
arr.takeWhile(_ < 5)  // Array(1, 2, 3, 4)
arr.dropWhile(_ < 5)  // Array(5, 6, 7, 8, 9, 10)

// SplitAt
val (left, right) = arr.splitAt(5)
// left: Array(1, 2, 3, 4, 5)
// right: Array(6, 7, 8, 9, 10)
```

### Группировка и разбиение

```scala
val arr = Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// GroupBy
arr.groupBy(_ % 2)
// Map(1 -> Array(1, 3, 5, 7, 9), 0 -> Array(2, 4, 6, 8, 10))

// Partition
val (even, odd) = arr.partition(_ % 2 == 0)
// even: Array(2, 4, 6, 8, 10)
// odd: Array(1, 3, 5, 7, 9)

// Grouped - разбиение на группы фиксированного размера
arr.grouped(3).toArray
// Array(Array(1, 2, 3), Array(4, 5, 6), Array(7, 8, 9), Array(10))

// Sliding - скользящее окно
arr.sliding(3).toArray
// Array(Array(1, 2, 3), Array(2, 3, 4), Array(3, 4, 5), ...)
```

### Многомерные массивы

```scala
// Создание двумерного массива
val matrix = Array.ofDim[Int](3, 3)
matrix(0)(0) = 1
matrix(0)(1) = 2
matrix(0)(2) = 3
matrix(1)(0) = 4
matrix(1)(1) = 5
matrix(1)(2) = 6
matrix(2)(0) = 7
matrix(2)(1) = 8
matrix(2)(2) = 9

// Или с начальными значениями
val matrix2 = Array(
  Array(1, 2, 3),
  Array(4, 5, 6),
  Array(7, 8, 9)
)

// Доступ к элементам
val value = matrix(1)(2)  // 6

// Итерация
for (i <- matrix.indices; j <- matrix(i).indices) {
  println(s"matrix($i)($j) = ${matrix(i)(j)}")
}
```

### Преобразование в другие коллекции

```scala
val arr = Array(1, 2, 3, 4, 5)

// В List
val list = arr.toList  // List(1, 2, 3, 4, 5)

// В Vector
val vector = arr.toVector  // Vector(1, 2, 3, 4, 5)

// В Set
val set = arr.toSet  // Set(1, 2, 3, 4, 5)

// В Map (из пар)
val pairs = Array(("a", 1), ("b", 2), ("c", 3))
val map = pairs.toMap  // Map("a" -> 1, "b" -> 2, "c" -> 3)
```

### Практический пример: Обработка больших данных

**Array** эффективен для обработки больших объемов данных:**

```scala
// Создание большого массива
val largeArray = new Array[Double](1000000)
for (i <- largeArray.indices) {
  largeArray(i) = math.random()
}

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallel = largeArray.par
val doubled = parallel.map(_ * 2).toArray

// Batch обработка
def processBatch[T](arr: Array[T], batchSize: Int)(f: Array[T] => Unit): Unit = {
  arr.grouped(batchSize).foreach(f)
}

processBatch(largeArray, 1000) { batch =>
  // обработка батча
  val sum = batch.sum
  println(s"Batch sum: $sum")
}
```

### Практический пример: Математические операции

**Array** удобен для математических вычислений:**

```scala
// Сложение массивов
def addArrays(a1: Array[Double], a2: Array[Double]): Array[Double] = {
  require(a1.length == a2.length, "Arrays must have the same length")
  (a1 zip a2).map { case (x, y) => x + y }
}

// Скалярное произведение
def dotProduct(a1: Array[Double], a2: Array[Double]): Double = {
  require(a1.length == a2.length, "Arrays must have the same length")
  (a1 zip a2).map { case (x, y) => x * y }.sum
}

// Нормализация
def normalize(arr: Array[Double]): Array[Double] = {
  val sum = arr.sum
  if (sum != 0) arr.map(_ / sum) else arr
}

// Использование
val a1 = Array(1.0, 2.0, 3.0)
val a2 = Array(4.0, 5.0, 6.0)
val sum = addArrays(a1, a2)  // Array(5.0, 7.0, 9.0)
val dot = dotProduct(a1, a2)  // 32.0
val normalized = normalize(a1)  // Array(0.166..., 0.333..., 0.5)
```

## Лучшие практики

### Использование Array для производительности

```scala
// Хорошо - использование Array для критичных по производительности участков
val arr = new Array[Int](1000000)
for (i <- arr.indices) {
  arr(i) = i * 2
}

// Плохо - использование List для частого доступа по индексу
val list = (1 to 1000000).toList
for (i <- list.indices) {
  val value = list(i)  // O(n) для каждого доступа
}
```

### Избегание ненужных преобразований

```scala
// Хорошо - работа напрямую с Array
val arr = Array(1, 2, 3, 4, 5)
val doubled = arr.map(_ * 2)

// Плохо - ненужное преобразование в List и обратно
val list = arr.toList.map(_ * 2).toArray
```

### Использование ArrayBuffer для динамических массивов

**Если нужен динамический размер, используйте **ArrayBuffer**:**

```scala
import scala.collection.mutable.ArrayBuffer

// Хорошо - ArrayBuffer для динамического размера
val buffer = ArrayBuffer[Int]()
buffer += 1
buffer += 2
buffer += 3
val arr = buffer.toArray

// Плохо - создание нового Array при каждом добавлении
var arr = Array(1)
arr = arr :+ 2  // создается новый массив
arr = arr :+ 3  // создается еще один новый массив
```

## Продвинутые техники работы с Array

### Оптимизация производительности Array

**Array** предоставляет максимальную производительность для произвольного доступа.

```scala
// Эффективный доступ по индексу
val arr = Array(1, 2, 3, 4, 5)
val element = arr(2)  // O(1) - очень быстро

// Эффективное обновление
arr(2) = 10  // O(1) - очень быстро

// Эффективная итерация
arr.foreach(println)  // O(n) - эффективно
```

### Работа с большими Array

**Array** эффективно работает с большими объемами данных.

```scala
// Создание большого Array
val largeArray = Array.range(1, 1000000)

// Эффективная обработка
val processed = largeArray
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)

// Параллельная обработка
val parallelProcessed = largeArray.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Интеграция с Java

**Array** полностью совместим с **Java** массивами.

```scala
// Использование Java методов
val arr = Array(1, 2, 3, 4, 5)
java.util.Arrays.sort(arr)  // Сортировка через Java API

// Преобразование в Java массив
val javaArray: Array[Int] = arr

// Использование Java коллекций
val javaList = java.util.Arrays.asList(arr: _*)
```

## Заключение (расширенное)

### Практические примеры: Работа с многомерными массивами

```scala
// Создание двумерного массива
val matrix = Array.ofDim[Int](3, 3)

// Инициализация
for (i <- 0 until 3; j <- 0 until 3) {
  matrix(i)(j) = i * 3 + j
}

// Доступ к элементам
val element = matrix(1)(2)  // 5

// Обход массива
def printMatrix[A](matrix: Array[Array[A]]): Unit = {
  for (row <- matrix) {
    println(row.mkString(", "))
  }
}

// Транспонирование матрицы
def transpose[A](matrix: Array[Array[A]]): Array[Array[A]] = {
  Array.tabulate(matrix(0).length, matrix.length) { (i, j) =>
    matrix(j)(i)
  }
}
```

### Практические примеры: Интеграция с Java библиотеками

```scala
// Использование Array для работы с Java библиотеками
import java.util.Arrays

val array = Array(5, 2, 8, 1, 9)

// Использование Java Arrays.sort
Arrays.sort(array)
// array: Array(1, 2, 5, 8, 9)

// Использование Java Arrays.binarySearch
val index = Arrays.binarySearch(array, 5)  // 2

// Использование Java Arrays.fill
Arrays.fill(array, 0)
// array: Array(0, 0, 0, 0, 0)
```

### Практические примеры: Оптимизация производительности Array

```scala
// In-place сортировка (быстро)
val array = Array(5, 2, 8, 1, 9)
java.util.Arrays.sort(array)  // Изменяет исходный массив

// Многопоточная обработка больших массивов
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def processArrayParallel[A, B](
  array: Array[A],
  numThreads: Int,
  processor: A => B
): Future[Array[B]] = {
  val chunkSize = array.length / numThreads
  val chunks = array.grouped(chunkSize).toArray
  val futures = chunks.map(chunk => Future(chunk.map(processor)))
  Future.sequence(futures).map(_.flatten.toArray)
}
```

**Array** является оптимальным выбором для сценариев, требующих максимальной производительности произвольного доступа и изменений. Понимание его характеристик, операций трансформации, агрегации, работы с многомерными массивами, взаимодействия с **Java**, оптимизации производительности, работы с большими **Array**, интеграции с **Java**, матричных операций, транспонирования матриц, использования **Java** библиотек, **in-place** сортировки, многопоточной обработки и практических применений позволяет эффективно использовать **Array** в **Scala** приложениях. **Array** особенно полезен для математических вычислений, обработки больших данных, интеграции с **Java** библиотеками, сценариев, где требуется максимальная производительность для произвольного доступа и изменений, работы с многомерными данными и параллельной обработки больших массивов.

### Практические примеры: Работа с примитивными массивами

```scala
// Создание массивов примитивных типов
val intArray = new Array[Int](10)
val doubleArray = new Array[Double](5)
val booleanArray = new Array[Boolean](3)

// Инициализация массивов
for (i <- intArray.indices) {
  intArray(i) = i * 2
}

// Использование Array.fill
val filled = Array.fill(10)(0)
val randomFilled = Array.fill(10)(math.random())
```

### Практические примеры: Работа с многомерными массивами

```scala
// Создание трехмерного массива
val cube = Array.ofDim[Int](3, 3, 3)

// Инициализация
for (i <- 0 until 3; j <- 0 until 3; k <- 0 until 3) {
  cube(i)(j)(k) = i * 9 + j * 3 + k
}

// Доступ к элементам
val value = cube(1)(2)(0)  // 15
```

### Практические примеры: Оптимизация производительности Array

```scala
// Использование Array для критичных по производительности участков
val largeArray = new Array[Double](1000000)

// Быстрая инициализация
var i = 0
while (i < largeArray.length) {
  largeArray(i) = math.random()
  i += 1
}

// Быстрая обработка
var sum = 0.0
i = 0
while (i < largeArray.length) {
  sum += largeArray(i)
  i += 1
}
```

### Практические примеры: Работа с ArrayBuffer

```scala
import scala.collection.mutable.ArrayBuffer

// ArrayBuffer для динамических массивов
val buffer = ArrayBuffer[Int]()

// Добавление элементов
buffer += 1
buffer += 2
buffer += 3

// Добавление нескольких элементов
buffer ++= Array(4, 5, 6)

// Преобразование в Array
val array = buffer.toArray
```

### Практические примеры: Работа с Java Arrays

```scala
import java.util.Arrays

val array = Array(5, 2, 8, 1, 9)

// Сортировка
Arrays.sort(array)  // Array(1, 2, 5, 8, 9)

// Бинарный поиск
val index = Arrays.binarySearch(array, 5)  // 2

// Заполнение
Arrays.fill(array, 0)  // Array(0, 0, 0, 0, 0)

// Копирование
val copy = Arrays.copyOf(array, array.length)

// Сравнение
val equals = Arrays.equals(array, copy)  // true
```

### Практические примеры: Работа с примитивными массивами

```scala
// Создание массивов примитивных типов
val intArray = new Array[Int](10)
val doubleArray = new Array[Double](5)
val booleanArray = new Array[Boolean](3)

// Инициализация массивов
for (i <- intArray.indices) {
  intArray(i) = i * 2
}

// Использование Array.fill
val filled = Array.fill(10)(0)
val randomFilled = Array.fill(10)(math.random())
```

### Практические примеры: Работа с многомерными массивами

```scala
// Создание трехмерного массива
val cube = Array.ofDim[Int](3, 3, 3)

// Инициализация
for (i <- 0 until 3; j <- 0 until 3; k <- 0 until 3) {
  cube(i)(j)(k) = i * 9 + j * 3 + k
}

// Доступ к элементам
val value = cube(1)(2)(0)  // 15
```

### Использование с различными операциями для производительности

```scala
// Оптимизация операций над Array
val array = Array.range(1, 1000000)

// Эффективная обработка
val processed = array
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallelProcessed = array.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Использование с различными операциями для работы с индексами

```scala
// Эффективная работа с индексами
val array = Array.range(1, 100)

// Доступ по индексу
val value = array(50)  // O(1)

// Обновление по индексу
array(50) = 999  // O(1) - мутация

// Получение диапазона
val slice = array.slice(10, 20)  // O(k)

// Поиск индекса
val index = array.indexOf(50)  // O(n)
```

## Дополнительные ресурсы

**Для дальнейшего изучения **Array** в **Scala** рекомендуется:**

- [Scala Array API Documentation](https://www.scala-lang.org/api/current/scala/Array.html)
- [Scala Collections Performance](https://docs.scala-lang.org/overviews/collections-2.13/performance-characteristics.html)
