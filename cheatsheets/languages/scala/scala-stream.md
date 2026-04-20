---
title: "Scala Stream"
description: "Полное руководство по Stream в Scala: ленивые списки, бесконечные последовательности, отложенные вычисления"
tags:
  - scala
  - stream
  - lazy-evaluation
  - functional-programming
difficulty: "intermediate"
prerequisites: ["scala/scala-collections-list.md"]
next:
  - scala-implicit
  - scala-http4s
  - scala-fp-advanced
updated: "2026-04-20"
related: ["scala/scala-collections-list.md", "scala/scala-collections.md"]
---

# Scala Stream

Кратко: полное руководство по **Stream** в **Scala**: ленивые списки, бесконечные последовательности, отложенные вычисления.

## Полезные ссылки

### Официальная документация
- [Scala Stream API](https://www.scala-lang.org/api/current/scala/collection/immutable/Stream.html)

### См. также
- [Списки (List)](scala-collections-list.md)
- [Обзор коллекций](scala-collections.md)

- [Scala Collections — Grouping and Aggregation](scala-collections-grouping.md)
- [Scala Collections — Vector](scala-collections-vector.md)
- [Scala Functional Programming — Advanced](scala-fp-advanced.md)
## Содержание

- [Введение в Stream](#введение-в-stream)
  - [Основные характеристики](#основные-характеристики)
- [Создание Stream](#создание-stream)
- [Ленивые вычисления](#ленивые-вычисления)
- [Бесконечные последовательности](#бесконечные-последовательности)
- [Операции с Stream](#операции-с-stream)
  - [Трансформации Stream](#трансформации-stream)
  - [Агрегация Stream](#агрегация-stream)
  - [Практический пример: Генерация последовательностей](#практический-пример-генерация-последовательностей)
  - [Практический пример: Обработка больших файлов](#практический-пример-обработка-больших-файлов)
  - [Мемоизация и производительность](#мемоизация-и-производительность)
  - [LazyList в Scala 2.13+](#lazylist-в-scala-213)
  - [Практический пример: Генерация тестовых данных](#практический-пример-генерация-тестовых-данных)
  - [Практический пример: Пайплайн обработки данных](#практический-пример-пайплайн-обработки-данных)
  - [Сравнение Stream и LazyList](#сравнение-stream-и-lazylist)
- [Лучшие практики](#лучшие-практики)
  - [Использование Stream для больших данных](#использование-stream-для-больших-данных)
  - [Избегание мемоизации больших Stream](#избегание-мемоизации-больших-stream)
  - [Использование take для ограничения размера](#использование-take-для-ограничения-размера)
  - [Использование view для ленивых коллекций](#использование-view-для-ленивых-коллекций)
  - [Практические примеры: Генерация последовательностей с Stream](#практические-примеры-генерация-последовательностей-с-stream)
  - [Практические примеры: Обработка больших файлов](#практические-примеры-обработка-больших-файлов)
  - [Практические примеры: Использование LazyList (Scala 2.13+)](#практические-примеры-использование-lazylist-scala-213)
  - [Практические примеры: Работа с деревьями через Stream](#практические-примеры-работа-с-деревьями-через-stream)
  - [Практические примеры: Генерация комбинаций](#практические-примеры-генерация-комбинаций)
  - [Практические примеры: Парсинг через Stream](#практические-примеры-парсинг-через-stream)
  - [Практические примеры: Фильтрация через Stream](#практические-примеры-фильтрация-через-stream)
  - [Практические примеры: Обработка с группировкой](#практические-примеры-обработка-с-группировкой)
  - [Практические примеры: Слияние Stream](#практические-примеры-слияние-stream)
  - [Практические примеры: Работа с окнами](#практические-примеры-работа-с-окнами)
  - [Практические примеры: Разделение Stream](#практические-примеры-разделение-stream)
  - [Практические примеры: Работа с Zip](#практические-примеры-работа-с-zip)
  - [Практические примеры: Работа с индексами](#практические-примеры-работа-с-индексами)
  - [Практические примеры: Работа с дубликатами](#практические-примеры-работа-с-дубликатами)
  - [Практические примеры: Работа с моноидами](#практические-примеры-работа-с-моноидами)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Использование с различными операциями для производительности](#использование-с-различными-операциями-для-производительности)
  - [Использование с различными операциями для работы с бесконечными последовательностями](#использование-с-различными-операциями-для-работы-с-бесконечными-последовательностями)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в Stream

**Stream** — это ленивый список, который вычисляет элементы по требованию. **Stream** позволяет работать с бесконечными последовательностями и откладывать вычисления до момента использования.

### Основные характеристики

- **Ленивость**: элементы вычисляются только при обращении
- **Мемоизация**: вычисленные элементы кэшируются
- **Бесконечность**: может представлять бесконечные последовательности
- **Функциональность**: поддерживает все операции коллекций

## Создание Stream

```scala
// Пустой Stream
val empty: Stream[Int] = Stream.empty

// Stream с элементами
val stream = Stream(1, 2, 3, 4, 5)

// Stream из Range
val range = (1 to 10).toStream

// Stream с помощью оператора #
val stream2 = 1 #:: 2 #:: 3 #:: Stream.empty

// Бесконечный Stream
val infinite = Stream.from(1)  // Stream(1, 2, 3, ...)
```

## Ленивые вычисления

**Stream** вычисляет элементы только при обращении:**

```scala
// Создание Stream с побочным эффектом
val stream = Stream.range(1, 10).map { x =>
  println(s"Computing $x")
  x * 2
}

// Элементы еще не вычислены
println("Stream created")

// Вычисление происходит при обращении
val first = stream.head  // "Computing 1"
val second = stream(1)   // "Computing 2" (если еще не вычислен)
```

Ленивость позволяет создавать эффективные пайплайны обработки данных.

## Бесконечные последовательности

**Stream** может представлять бесконечные последовательности:**

```scala
// Бесконечная последовательность чисел
val numbers = Stream.from(1)

// Первые 10 элементов
val first10 = numbers.take(10).toList  // List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Бесконечная последовательность четных чисел
val evens = Stream.from(2, 2)  // Stream(2, 4, 6, 8, ...)

// Последовательность Фибоначчи
val fib: Stream[BigInt] = BigInt(0) #:: BigInt(1) #::
  fib.zip(fib.tail).map { case (a, b) => a + b }
val first10Fib = fib.take(10).toList  // List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
```

Бесконечные **Stream** позволяют работать с последовательностями, размер которых неизвестен заранее.

## Операции с Stream

```scala
val stream = Stream.from(1)

// Фильтрация
val evens = stream.filter(_ % 2 == 0).take(10).toList

// Преобразование
val doubled = stream.map(_ * 2).take(10).toList

// Комбинация операций
val result = stream
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(5)
  .toList
```

**Stream** поддерживает все стандартные операции коллекций, выполняя их лениво.

### Трансформации Stream

**Stream** поддерживает стандартные операции трансформации:**

```scala
val stream = Stream.from(1)

// Map - преобразование каждого элемента
val doubled = stream.map(_ * 2).take(10).toList

// Filter - фильтрация элементов
val evens = stream.filter(_ % 2 == 0).take(10).toList

// FlatMap - преобразование и разворачивание
val expanded = stream.flatMap(x => Stream(x, x * 2)).take(10).toList

// Collect - комбинация filter и map
val collected = stream.collect {
  case x if x % 2 == 0 => x * 2
}.take(10).toList
```

### Агрегация Stream

**Stream** поддерживает операции агрегации:**

```scala
val stream = Stream.from(1)

// Sum, product, min, max (для конечных Stream)
val finite = Stream(1, 2, 3, 4, 5)
finite.sum      // 15
finite.product  // 120
finite.min      // 1
finite.max      // 5

// Fold и reduce
finite.fold(0)(_ + _)     // 15
finite.reduce(_ + _)       // 15
```

### Практический пример: Генерация последовательностей

```scala
// Последовательность простых чисел
def primes: Stream[Int] = {
  def isPrime(n: Int): Boolean = {
    (2 to math.sqrt(n).toInt).forall(n % _ != 0)
  }
  Stream.from(2).filter(isPrime)
}

val first10Primes = primes.take(10).toList
// List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)

// Последовательность степеней двойки
val powersOfTwo: Stream[BigInt] = {
  def next(power: Int): Stream[BigInt] = {
    BigInt(2).pow(power) #:: next(power + 1)
  }
  next(0)
}

val first10Powers = powersOfTwo.take(10).toList
// List(1, 2, 4, 8, 16, 32, 64, 128, 256, 512)
```

### Практический пример: Обработка больших файлов

```scala
import scala.io.Source

// Чтение большого файла построчно через Stream
def readLines(file: String): Stream[String] = {
  val source = Source.fromFile(file)
  source.getLines().toStream
}

// Обработка только первых N строк без загрузки всего файла
val lines = readLines("large-file.txt")
val processed = lines
  .filter(_.nonEmpty)
  .map(_.toUpperCase)
  .take(100)
  .toList
```

### Мемоизация и производительность

**Stream** мемоизирует вычисленные элементы:**

```scala
var computationCount = 0

val stream = Stream.from(1).map { x =>
  computationCount += 1
  println(s"Computing $x")
  x * 2
}

// Первое обращение - вычисление
val first = stream(0)  // "Computing 1", computationCount = 1

// Второе обращение - использование кэша
val firstAgain = stream(0)  // без "Computing 1", computationCount = 1

// Новое обращение - вычисление
val second = stream(1)  // "Computing 2", computationCount = 2
```

Мемоизация может быть полезной для избежания повторных вычислений, но может привести к проблемам с памятью для очень больших **Stream**.

### LazyList в Scala 2.13+

**В **Scala** 2.13+ **Stream** заменен на **LazyList**:**

```scala
// LazyList - новый тип для ленивых списков
val lazyList = LazyList.from(1)
val first10 = lazyList.take(10).toList

// LazyList не мемоизирует элементы по умолчанию
// Это решает проблемы с памятью Stream
```

### Практический пример: Генерация тестовых данных

```scala
// Генерация бесконечной последовательности тестовых пользователей
case class TestUser(id: Long, name: String, email: String)

def generateTestUsers: Stream[TestUser] = {
  def next(id: Long): Stream[TestUser] = {
    TestUser(id, s"User$id", s"user$id@example.com") #:: next(id + 1)
  }
  next(1)
}

val testUsers = generateTestUsers.take(1000).toList
```

### Практический пример: Пайплайн обработки данных

```scala
// Ленивый пайплайн обработки данных
val dataStream = Stream.from(1)

val processed = dataStream
  .filter(_ % 2 == 0)      // только четные
  .map(_ * 3)              // умножение на 3
  .filter(_ > 10)          // больше 10
  .take(20)                // первые 20
  .toList                  // материализация

// Вычисления выполняются только при вызове toList
```

### Сравнение Stream и LazyList

```scala
// Stream (Scala 2.12 и ранее)
val stream = Stream.from(1)
// Мемоизирует элементы
// Может привести к проблемам с памятью

// LazyList (Scala 2.13+)
val lazyList = LazyList.from(1)
// Не мемоизирует элементы по умолчанию
// Более эффективное использование памяти
```

## Лучшие практики

### Использование Stream для больших данных

```scala
// Хорошо - использование Stream для обработки больших данных
val largeStream = Stream.from(1)
val result = largeStream
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toList

// Плохо - создание большого List
val largeList = (1 to 1000000).toList
val result2 = largeList.filter(_ % 2 == 0).map(_ * 2).take(100)
```

### Избегание мемоизации больших Stream

```scala
// Stream мемоизирует вычисленные элементы
val stream = Stream.from(1).map(_ * 2)
val first = stream(0)  // вычисляется и кэшируется
val second = stream(1)  // вычисляется и кэшируется

// Для очень больших Stream это может привести к проблемам с памятью
// Используйте LazyList в Scala 2.13+ для избежания мемоизации
```

### Использование take для ограничения размера

```scala
// Хорошо - использование take для ограничения
val result = infiniteStream
  .filter(condition)
  .map(transform)
  .take(100)
  .toList

// Плохо - попытка материализовать бесконечный Stream
val result2 = infiniteStream.toList  // может привести к OutOfMemoryError
```

### Использование view для ленивых коллекций

**Для обычных коллекций используйте **view** вместо **Stream**:**

```scala
// Хорошо - использование view для ленивых операций
val result = (1 to 1000000).view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toList

// View не мемоизирует промежуточные результаты
```

### Практические примеры: Генерация последовательностей с Stream

```scala
// Генерация последовательности Фибоначчи
def fibonacci: Stream[BigInt] = {
  def fib(a: BigInt, b: BigInt): Stream[BigInt] =
    a #:: fib(b, a + b)
  fib(0, 1)
}

// Первые 10 чисел Фибоначчи
val fibs = fibonacci.take(10).toList
// List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
```

### Практические примеры: Обработка больших файлов

```scala
import scala.io.Source

def processLargeFile(filePath: String): Stream[String] = {
  val source = Source.fromFile(filePath)
  source.getLines().toStream
    .filter(_.nonEmpty)
    .map(_.trim)
    .map(_.toUpperCase)
}

// Обработка файла построчно без загрузки всего в память
val lines = processLargeFile("large-file.txt")
val result = lines.take(1000).toList
```

### Практические примеры: Использование LazyList (Scala 2.13+)

```scala
// LazyList - современная замена Stream
def naturalNumbers: LazyList[Int] = LazyList.from(1)

def primes: LazyList[Int] = {
  def isPrime(n: Int): Boolean =
    !(2 until n).exists(n % _ == 0)

  naturalNumbers.filter(isPrime)
}

// Первые 10 простых чисел
val firstPrimes = primes.take(10).toList
// List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
```

### Практические примеры: Работа с деревьями через Stream

```scala
sealed trait Tree[A]
case class Leaf[A](value: A) extends Tree[A]
case class Node[A](left: Tree[A], value: A, right: Tree[A]) extends Tree[A]

def treeToStream[A](tree: Tree[A]): Stream[A] = tree match {
  case Leaf(value) => Stream(value)
  case Node(left, value, right) =>
    treeToStream(left) #::: Stream(value) #::: treeToStream(right)
}

val tree = Node(
  Node(Leaf(1), 2, Leaf(3)),
  4,
  Node(Leaf(5), 6, Leaf(7))
)

val stream = treeToStream(tree)
val result = stream.toList  // List(1, 2, 3, 4, 5, 6, 7)
```

### Практические примеры: Генерация комбинаций

```scala
// Генерация всех комбинаций элементов
def combinations[A](list: List[A], n: Int): Stream[List[A]] = {
  if (n == 0) Stream(Nil)
  else if (list.isEmpty) Stream.empty
  else {
    val head = list.head
    val tail = list.tail
    combinations(tail, n - 1).map(head :: _) #:::
    combinations(tail, n)
  }
}

val list = List(1, 2, 3, 4)
val combos = combinations(list, 2).take(6).toList
// List(List(1, 2), List(1, 3), List(1, 4), List(2, 3), List(2, 4), List(3, 4))
```

### Практические примеры: Парсинг через Stream

```scala
// Ленивый парсер для потоков данных
def parseNumbers(input: Stream[Char]): Stream[Int] = {
  def parseDigit(stream: Stream[Char]): Option[(Int, Stream[Char])] = {
    stream.headOption.flatMap { ch =>
      if (ch.isDigit) {
        val number = ch.toString.toInt
        parseDigit(stream.tail).map { case (n, rest) =>
          (number * 10 + n, rest)
        }.orElse(Some((number, stream.tail)))
      } else None
    }
  }

  parseDigit(input).map { case (num, rest) =>
    Stream(num) #::: parseNumbers(rest.dropWhile(!_.isDigit))
  }.getOrElse(Stream.empty)
}

val input = Stream('1', '2', ' ', '3', '4', ' ', '5', '6', '7')
val numbers = parseNumbers(input).toList  // List(12, 34, 567)
```

### Практические примеры: Фильтрация через Stream

```scala
// Фильтрация с использованием Stream
def sieve(stream: Stream[Int]): Stream[Int] = {
  stream.headOption match {
    case Some(head) =>
      head #:: sieve(stream.tail.filter(_ % head != 0))
    case None => Stream.empty
  }
}

val primes = sieve(Stream.from(2))
val first10Primes = primes.take(10).toList
// List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
```

### Практические примеры: Обработка с группировкой

```scala
// Группировка элементов через Stream
def groupBy[A, K](stream: Stream[A])(key: A => K): Stream[(K, List[A])] = {
  def group(remaining: Stream[A], groups: Map[K, List[A]]): Stream[(K, List[A])] = {
    remaining.headOption match {
      case Some(head) =>
        val k = key(head)
        val updatedGroups = groups + (k -> (head :: groups.getOrElse(k, Nil)))
        group(remaining.tail, updatedGroups)
      case None =>
        groups.toStream.map { case (k, list) => (k, list.reverse) }
    }
  }

  group(stream, Map.empty)
}

val stream = Stream(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
val grouped = groupBy(stream)(_ % 2)
val result = grouped.take(2).toList
// List((1, List(1, 3, 5, 7, 9)), (0, List(2, 4, 6, 8, 10)))
```

### Практические примеры: Слияние Stream

```scala
// Слияние двух упорядоченных Stream
def merge[A: Ordering](s1: Stream[A], s2: Stream[A]): Stream[A] = {
  (s1.headOption, s2.headOption) match {
    case (Some(h1), Some(h2)) =>
      if (Ordering[A].lt(h1, h2)) h1 #:: merge(s1.tail, s2)
      else h2 #:: merge(s1, s2.tail)
    case (Some(h1), None) => s1
    case (None, Some(h2)) => s2
    case (None, None) => Stream.empty
  }
}

val stream1 = Stream(1, 3, 5, 7, 9)
val stream2 = Stream(2, 4, 6, 8, 10)
val merged = merge(stream1, stream2).toList
// List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
```

### Практические примеры: Работа с окнами

```scala
// Создание скользящих окон
def sliding[A](stream: Stream[A], windowSize: Int): Stream[List[A]] = {
  if (stream.length < windowSize) Stream.empty
  else {
    val window = stream.take(windowSize).toList
    window #:: sliding(stream.tail, windowSize)
  }
}

val stream = Stream.from(1)
val windows = sliding(stream, 3).take(5).toList
// List(List(1, 2, 3), List(2, 3, 4), List(3, 4, 5), List(4, 5, 6), List(5, 6, 7))
```

### Практические примеры: Разделение Stream

```scala
// Разделение Stream на два по условию
def partition[A](stream: Stream[A])(pred: A => Boolean): (Stream[A], Stream[A]) = {
  def partitionInternal(remaining: Stream[A], left: List[A], right: List[A]): (Stream[A], Stream[A]) = {
    remaining.headOption match {
      case Some(head) =>
        if (pred(head)) partitionInternal(remaining.tail, head :: left, right)
        else partitionInternal(remaining.tail, left, head :: right)
      case None =>
        (left.reverse.toStream, right.reverse.toStream)
    }
  }

  partitionInternal(stream, Nil, Nil)
}

val stream = Stream(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
val (evens, odds) = partition(stream)(_ % 2 == 0)
val evenList = evens.take(5).toList  // List(2, 4, 6, 8, 10)
val oddList = odds.take(5).toList    // List(1, 3, 5, 7, 9)
```

### Практические примеры: Работа с Zip

```scala
// Объединение двух Stream через zip
val stream1 = Stream(1, 2, 3, 4, 5)
val stream2 = Stream("a", "b", "c", "d", "e")
val zipped = stream1.zip(stream2).toList
// List((1, "a"), (2, "b"), (3, "c"), (4, "d"), (5, "e"))

// ZipWith для применения функции
val zippedWith = stream1.zip(stream2).map { case (a, b) => s"$a$b" }.toList
// List("1a", "2b", "3c", "4d", "5e")
```

### Практические примеры: Работа с индексами

```scala
// Добавление индексов к элементам
val stream = Stream("a", "b", "c", "d", "e")
val indexed = stream.zipWithIndex.map { case (value, index) =>
  (index, value)
}.toList
// List((0, "a"), (1, "b"), (2, "c"), (3, "d"), (4, "e"))
```

### Практические примеры: Работа с дубликатами

```scala
// Удаление дубликатов
def distinct[A](stream: Stream[A]): Stream[A] = {
  def distinctInternal(remaining: Stream[A], seen: Set[A]): Stream[A] = {
    remaining.headOption match {
      case Some(head) if !seen.contains(head) =>
        head #:: distinctInternal(remaining.tail, seen + head)
      case Some(_) => distinctInternal(remaining.tail, seen)
      case None => Stream.empty
    }
  }

  distinctInternal(stream, Set.empty)
}

val stream = Stream(1, 2, 2, 3, 3, 3, 4, 4, 5)
val unique = distinct(stream).toList  // List(1, 2, 3, 4, 5)
```

### Практические примеры: Работа с моноидами

```scala
// Агрегация через Monoid
trait Monoid[A] {
  def empty: A
  def combine(x: A, y: A): A
}

implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
  def empty: Int = 0
  def combine(x: Int, y: Int): Int = x + y
}

def fold[A: Monoid](stream: Stream[A]): A = {
  val m = implicitly[Monoid[A]]
  stream.foldLeft(m.empty)(m.combine)
}

val stream = Stream(1, 2, 3, 4, 5)
val sum = fold(stream)  // 15
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Stream** предоставляет мощные возможности для работы с ленивыми и бесконечными последовательностями. Понимание ленивых вычислений, мемоизации, работы с бесконечными последовательностями, трансформаций, агрегации, генерации последовательностей, обработки больших файлов и практических применений позволяет эффективно использовать **Stream** в различных сценариях. В **Scala** 2.13+ рекомендуется использовать **LazyList** вместо **Stream** для более эффективного использования памяти. **Stream** особенно полезен для генерации последовательностей, обработки больших файлов, создания ленивых пайплайнов обработки данных и работы с бесконечными последовательностями без потребления всех ресурсов памяти.

**Stream** предоставляет мощные инструменты для работы с деревьями, генерации комбинаций, парсинга, фильтрации, группировки, слияния, работы с окнами, разделения, **zip**, индексации, удаления дубликатов и агрегации через моноиды. Понимание этих техник позволяет создавать сложные, эффективные программы обработки данных.

### Использование с различными операциями для производительности

```scala
// Оптимизация операций над Stream
val stream = Stream.range(1, 1000000)

// Ленивая обработка
val processed = stream
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)  // Вычисляется только необходимое

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallelProcessed = stream.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Использование с различными операциями для работы с бесконечными последовательностями

```scala
// Работа с бесконечными последовательностями
val infiniteStream = Stream.from(1)

// Генерация бесконечной последовательности
val fibonacci: Stream[BigInt] = {
  def fib(a: BigInt, b: BigInt): Stream[BigInt] = a #:: fib(b, a + b)
  fib(0, 1)
}

// Взятие первых N элементов
val first10 = fibonacci.take(10).toList
// List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
```

## Дополнительные ресурсы

**Для дальнейшего изучения **Stream** в **Scala** рекомендуется:**

- [Scala Stream API Documentation](https://www.scala-lang.org/api/current/scala/collection/immutable/Stream.html)
- [Scala LazyList Documentation](https://www.scala-lang.org/api/current/scala/collection/immutable/LazyList.html)
- [Scala Collections Performance](https://docs.scala-lang.org/overviews/collections-2.13/performance-characteristics.html)
