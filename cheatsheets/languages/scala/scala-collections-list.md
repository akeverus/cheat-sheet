---
title: "Scala Collections - List"
description: "Полное руководство по работе со списками в Scala: List, ListBuffer, операции, производительность и лучшие практики"
tags: ["scala", "collections", "list", "functional-programming", "immutable"]
difficulty: "intermediate"
prerequisites: ["scala/scala-basics.md"]
next: ["scala/scala-collections-set.md", "scala/scala-collections-map.md"]
updated: "2026-02-06"
related: ["scala/scala-basics.md", "scala/scala-collections.md"]
---

# **Scala Collections** - **List**

Кратко: руководство по работе со списками в **Scala**: **List**, **ListBuffer**, операции и лучшие практики.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [Scala Collections Overview](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)
- [Scala List API](https://www.scala-lang.org/api/current/scala/collection/immutable/List.html)

### **Baeldung**
- [Scala Collections Guide](https://www.baeldung.com/scala/collections)

### См. также
- [Основы Scala](scala-basics.md)
- [Множества (Set)](scala-collections-set.md)
- [Словари (Map)](scala-collections-map.md)
- [Обзор коллекций](scala-collections.md)

## Содержание

- [**Scala Collections** - **List**](#scala-collections-list)
- [Введение в **List**](#введение-в-list)
  - [Основные характеристики](#основные-характеристики)
  - [**Immutable** vs **Mutable**](#immutable-vs-mutable)
- [Создание списков](#создание-списков)
  - [Базовое создание](#базовое-создание)
  - [Создание через **fill** и **tabulate**](#создание-через-fill-и-tabulate)
- [**Immutable List**](#immutable-list)
  - [Структура **List**](#структура-list)
  - [Основные операции](#основные-операции)
  - [Добавление элементов](#добавление-элементов)
- [**Mutable ListBuffer**](#mutable-listbuffer)
  - [**Map** - преобразование элементов](#map-преобразование-элементов)
  - [**Filter** - фильтрация элементов](#filter-фильтрация-элементов)
  - [**FlatMap** - преобразование и разворачивание](#flatmap-преобразование-и-разворачивание)
- [Поиск и фильтрация](#поиск-и-фильтрация)
  - [Поиск элементов](#поиск-элементов)
  - [**Take** и **Drop**](#take-и-drop)
- [Преобразование списков](#преобразование-списков)
  - [**Zip** - объединение списков](#zip-объединение-списков)
  - [**Slice** - получение подсписка](#slice-получение-подсписка)
- [Сортировка](#сортировка)
- [Группировка и агрегация](#группировка-и-агрегация)
  - [**GroupBy**](#groupby)
  - [**Fold** и **Reduce**](#fold-и-reduce)
  - [Агрегатные функции](#агрегатные-функции)
- [Работа с индексами](#работа-с-индексами)
- [**Pattern Matching** со списками](#pattern-matching-со-списками)
- [Производительность](#производительность)
  - [Временная сложность операций](#временная-сложность-операций)
  - [Рекомендации по производительности](#рекомендации-по-производительности)
- [Лучшие практики](#лучшие-практики)
  - [Предпочтение **immutable List**](#предпочтение-immutable-list)
  - [Использование **ListBuffer** для накопления](#использование-listbuffer-для-накопления)
  - [**Pattern Matching** вместо **head**/**tail**](#pattern-matching-вместо-headtail)
- [Практические примеры использования](#практические-примеры-использования)
  - [Реализация стека и очереди](#реализация-стека-и-очереди)
  - [Обработка больших списков](#обработка-больших-списков)
  - [Работа с вложенными списками](#работа-с-вложенными-списками)
  - [Рекурсивная обработка списков](#рекурсивная-обработка-списков)
- [Продвинутые техники работы с **List**](#продвинутые-техники-работы-с-list)
  - [Оптимизация производительности **List**](#оптимизация-производительности-list)
  - [Работа с большими **List**](#работа-с-большими-list)
  - [Сравнение **List** с другими структурами](#сравнение-list-с-другими-структурами)
  - [Практические примеры: Рекурсивная обработка **List**](#практические-примеры-рекурсивная-обработка-list)
  - [Практические примеры: Обработка вложенных **List**](#практические-примеры-обработка-вложенных-list)
  - [Практические примеры: Работа с большими **List**](#практические-примеры-работа-с-большими-list)
  - [Практические примеры: Работа с **zip** и **unzip**](#практические-примеры-работа-с-zip-и-unzip)
  - [Практические примеры: Работа с индексами](#практические-примеры-работа-с-индексами)
  - [Использование с различными операциями для производительности](#использование-с-различными-операциями-для-производительности)
  - [Использование с различными операциями для работы с индексами](#использование-с-различными-операциями-для-работы-с-индексами)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **List**

**List** в **Scala** - это неизменяемый связанный список, который является основой функционального программирования в **Scala**. **List** оптимизирован для операций в начале списка и является основным типом для работы с последовательностями данных в функциональном стиле.

### Основные характеристики

- **Неизменяемость**: **List** является неизменяемой структурой данных. Все операции создают новый список, не изменяя исходный.

- **Связанная структура**: **List** реализован как связанный список, что обеспечивает эффективное добавление элементов в начало (**O(1**)), но медленный доступ по индексу (**O(n**)).

- **Функциональный стиль**: **List** поддерживает функциональные операции, такие как **map**, **filter**, **fold**, что делает его идеальным для функционального программирования.

- **Pattern Matching**: **List** отлично работает с **pattern matching**, позволяя декомпозировать списки на **head** и **tail**.

### **Immutable** vs **Mutable**

**Scala** предоставляет два варианта списков:**

1. **List** (**immutable**) - неизменяемый список, рекомендуется по умолчанию
2. **ListBuffer** (**mutable**) - изменяемый список для случаев, когда нужна эффективная модификация

## Создание списков

### Базовое создание

```scala
// Пустой список
val empty: List[Int] = Nil
val empty2 = List.empty[Int]

// Список с элементами
val numbers = List(1, 2, 3, 4, 5)
val names = List("Alice", "Bob", "Charlie")

// Использование конструктора ::
val list1 = 1 :: 2 :: 3 :: Nil
val list2 = Nil.::(3).::(2).::(1)  // эквивалентно предыдущему

// Range в список
val range = (1 to 10).toList
val range2 = (1 until 10).toList
```

### Создание через **fill** и **tabulate**

```scala
// Создание списка с одинаковыми элементами
val zeros = List.fill(5)(0)  // List(0, 0, 0, 0, 0)
val repeated = List.fill(3)("Hello")  // List("Hello", "Hello", "Hello")

// Создание списка с вычисляемыми значениями
val squares = List.tabulate(5)(n => n * n)  // List(0, 1, 4, 9, 16)
val matrix = List.tabulate(3, 3)((i, j) => i * j)  // двумерный список
```

## **Immutable List**

### Структура **List**

**List** в **Scala** реализован как связанный список, где каждый элемент содержит значение и ссылку на следующий элемент. Это обеспечивает эффективное добавление в начало, но медленный доступ по индексу. Структура состоит из двух компонентов: **Nil** (**пустой список**) и :: (**cons, конструктор списка**).

```scala
// Структура List: sealed trait, Nil — пустой список, :: — голова и хвост
sealed trait List[+A]
case object Nil extends List[Nothing]
case class ::[+A](head: A, tail: List[A]) extends List[A]
```

Связанная структура означает, что для доступа к элементу по индексу n нужно пройти через n элементов от начала списка. Это делает **List** идеальным для операций в начале списка и для функциональных трансформаций, но неэффективным для произвольного доступа.

### Основные операции

```scala
val list = List(1, 2, 3, 4, 5)

// Доступ к элементам
list.head        // 1 - первый элемент
list.tail        // List(2, 3, 4, 5) - все кроме первого
list.last        // 5 - последний элемент
list.init        // List(1, 2, 3, 4) - все кроме последнего

// Проверки
list.isEmpty     // false
list.nonEmpty    // true
list.length      // 5
list.size        // 5 (синоним length)

// Доступ по индексу
list(0)          // 1
list(2)          // 3
list.apply(0)    // 1 (эквивалентно list(0))
```

### Добавление элементов

Операции добавления элементов в **List** создают новый список, не изменяя исходный. Это фундаментальное свойство неизменяемых структур данных.

```scala
val list = List(1, 2, 3)

// Добавление в начало (O(1))
// Операция :: (cons) эффективна, так как List - это связанный список.
// Новый элемент просто становится новым head, а старый список становится tail.
val prepended = 0 :: list  // List(0, 1, 2, 3)

// Добавление в конец (O(n) - создает новый список)
// Эта операция требует создания нового списка, так как нужно пройти по всем элементам.
// Для частых операций добавления в конец лучше использовать ListBuffer или Vector.
val appended = list :+ 4    // List(1, 2, 3, 4)

// Конкатенация списков
val combined = list ++ List(4, 5)  // List(1, 2, 3, 4, 5)
val combined2 = list ::: List(4, 5)  // то же самое
```

Важно помнить, что добавление в начало списка (::) эффективнее, чем добавление в конец (**:+**), так как **List** - это связанный список.

## **Mutable ListBuffer**

**ListBuffer** используется, когда нужна эффективная модификация списка:**

```scala
// ListBuffer — изменяемый буфер для эффективного накопления элементов
import scala.collection.mutable.ListBuffer

val buffer = ListBuffer(1, 2, 3)

// Добавление элементов
buffer += 4              // ListBuffer(1, 2, 3, 4)
buffer += (5, 6)         // ListBuffer(1, 2, 3, 4, 5, 6)
buffer ++= List(7, 8)    // ListBuffer(1, 2, 3, 4, 5, 6, 7, 8)

// Добавление в начало
0 +=: buffer             // ListBuffer(0, 1, 2, 3, 4, 5, 6, 7, 8)

// Удаление элементов
buffer -= 5              // удаляет первое вхождение 5
buffer --= List(6, 7)    // удаляет элементы 6 и 7

// Преобразование в immutable List
val immutableList = buffer.toList
```

**ListBuffer** полезен для накопления элементов перед преобразованием в **immutable List**.

## Основные операции

### **Map** - преобразование элементов

```scala
val numbers = List(1, 2, 3, 4, 5)

// Умножение каждого элемента на 2
val doubled = numbers.map(_ * 2)  // List(2, 4, 6, 8, 10)

// Преобразование типов
val strings = numbers.map(_.toString)  // List("1", "2", "3", "4", "5")

// С индексами
val withIndices = numbers.zipWithIndex.map { case (value, index) =>
  s"$index: $value"
}  // List("0: 1", "1: 2", "2: 3", "3: 4", "4: 5")
```

### **Filter** - фильтрация элементов

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Фильтрация четных чисел
val evens = numbers.filter(_ % 2 == 0)  // List(2, 4, 6, 8, 10)

// Фильтрация с отрицанием
val odds = numbers.filterNot(_ % 2 == 0)  // List(1, 3, 5, 7, 9)

// Разделение на две части
val (evens2, odds2) = numbers.partition(_ % 2 == 0)
```

### **FlatMap** - преобразование и разворачивание

```scala
val numbers = List(1, 2, 3)

// Разворачивание вложенных списков
val expanded = numbers.flatMap(n => List(n, n * 2))
// List(1, 2, 2, 4, 3, 6)

// Работа со строками
val words = List("hello", "world")
val chars = words.flatMap(_.toList)  // List(h, e, l, l, o, w, o, r, l, d)
```

## Поиск и фильтрация

### Поиск элементов

```scala
val numbers = List(1, 2, 3, 4, 5)

// Поиск первого элемента, удовлетворяющего условию
numbers.find(_ > 3)        // Some(4)
numbers.find(_ > 10)       // None

// Проверка существования
numbers.exists(_ > 3)      // true
numbers.forall(_ > 0)      // true (все элементы > 0)

// Подсчет
numbers.count(_ > 3)       // 2
```

### **Take** и **Drop**

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Взять первые n элементов
numbers.take(3)            // List(1, 2, 3)

// Пропустить первые n элементов
numbers.drop(3)            // List(4, 5, 6, 7, 8, 9, 10)

// Взять элементы, пока условие истинно
numbers.takeWhile(_ < 5)   // List(1, 2, 3, 4)

// Пропустить элементы, пока условие истинно
numbers.dropWhile(_ < 5)   // List(5, 6, 7, 8, 9, 10)

// Взять последние n элементов
numbers.takeRight(3)       // List(8, 9, 10)

// Пропустить последние n элементов
numbers.dropRight(3)       // List(1, 2, 3, 4, 5, 6, 7)
```

## Преобразование списков

### **Zip** - объединение списков

```scala
val numbers = List(1, 2, 3)
val letters = List('a', 'b', 'c')

// Объединение в пары
val zipped = numbers.zip(letters)  // List((1,a), (2,b), (3,c))

// С индексами
val withIndices = numbers.zipWithIndex  // List((1,0), (2,1), (3,2))

// Unzip - разделение на два списка
val (nums, lets) = zipped.unzip
```

### **Slice** - получение подсписка

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Получить подсписок с индекса from до to (не включая)
numbers.slice(2, 5)        // List(3, 4, 5)
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

## Группировка и агрегация

### **GroupBy**

```scala
val numbers = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Группировка по четности
val grouped = numbers.groupBy(_ % 2 == 0)
// Map(false -> List(1, 3, 5, 7, 9), true -> List(2, 4, 6, 8, 10))

// Группировка по первой букве
val words = List("apple", "banana", "apricot", "blueberry")
val byFirstLetter = words.groupBy(_.head)
// Map(a -> List(apple, apricot), b -> List(banana, blueberry))
```

### **Fold** и **Reduce**

```scala
val numbers = List(1, 2, 3, 4, 5)

// Reduce - свертка без начального значения
val sum = numbers.reduce(_ + _)  // 15

// Fold - свертка с начальным значением
val sum2 = numbers.fold(0)(_ + _)  // 15
val product = numbers.fold(1)(_ * _)  // 120

// FoldLeft - слева направо
val leftFold = numbers.foldLeft(0)(_ + _)  // 15

// FoldRight - справа налево
val rightFold = numbers.foldRight(0)(_ + _)  // 15
```

### Агрегатные функции

```scala
val numbers = List(1, 2, 3, 4, 5)

numbers.sum      // 15
numbers.product  // 120
numbers.min      // 1
numbers.max      // 5
numbers.minOption  // Some(1)
numbers.maxOption  // Some(5)
```

## Работа с индексами

```scala
val list = List("a", "b", "c", "d", "e")

// Доступ по индексу
list(0)          // "a"
list(2)          // "c"

// Безопасный доступ
list.lift(0)     // Some("a")
list.lift(10)    // None

// Поиск индекса
list.indexOf("c")      // 2
list.indexOf("x")      // -1 (не найдено)
list.indexWhere(_ == "c")  // 2

// Последний индекс
list.lastIndexOf("c")  // 2
```

## **Pattern Matching** со списками

**Pattern matching** - мощный инструмент для работы со списками:**

```scala
def describeList(list: List[Int]): String = list match {
  case Nil => "empty list"
  case head :: Nil => s"single element: $head"
  case head :: tail => s"head: $head, tail has ${tail.length} elements"
}

// Использование
describeList(Nil)              // "empty list"
describeList(List(1))          // "single element: 1"
describeList(List(1, 2, 3))   // "head: 1, tail has 2 elements"

// Более сложный пример
def sumList(list: List[Int]): Int = list match {
  case Nil => 0
  case head :: tail => head + sumList(tail)
}
```

## Производительность

### Временная сложность операций

- **Доступ по индексу**: `O(n)` - требуется проход от начала списка
- **Добавление в начало (::)**: `O(1)` - константное время
- **Добавление в конец (**:+**)** : `O(n)` - создается новый список
- **Поиск элемента**: `O(n)` - линейный поиск
- **Map/Filter**: `O(n)` - один проход по списку

### Рекомендации по производительности

1. **Используйте :: для добавления в начало** - это эффективная операция
2. **Избегайте :+ для больших списков** - используйте **ListBuffer** и преобразуйте в **List**
3. **Используйте `Vector` для частого доступа по индексу** - **Vector** обеспечивает `O(**log32(n**)`) доступ
4. **Используйте `ListBuffer` для накопления** - эффективнее, чем многократное использование :+

## Лучшие практики

### Предпочтение **immutable List**

```scala
// Хорошо - immutable
val numbers = List(1, 2, 3)
val doubled = numbers.map(_ * 2)

// Плохо - ненужная мутабельность
val buffer = ListBuffer(1, 2, 3)
buffer.map(_ * 2)  // создает новый ListBuffer, исходный не изменяется
```

### Использование **ListBuffer** для накопления

```scala
// Хорошо - эффективное накопление
val buffer = ListBuffer[Int]()
for (i <- 1 to 1000) {
  buffer += i
}
val result = buffer.toList

// Плохо - неэффективное накопление
var result = List[Int]()
for (i <- 1 to 1000) {
  result = result :+ i  // создает новый список каждый раз
}
```

### **Pattern Matching** вместо **head**/**tail**

```scala
// Хорошо - безопасный pattern matching
def processList(list: List[Int]): Int = list match {
  case Nil => 0
  case head :: tail => head + processList(tail)
}

// Плохо - небезопасный доступ
def processListBad(list: List[Int]): Int = {
  if (list.isEmpty) 0
  else list.head + processListBad(list.tail)  // может упасть на пустом списке
}
```

## Практические примеры использования

### Реализация стека и очереди

**Использование **List** для реализации стека и очереди:**

```scala
// Стек на основе List
class Stack[T] {
  private var items: List[T] = Nil
  
  def push(item: T): Unit = {
    items = item :: items
  }
  
  def pop(): Option[T] = items match {
    case Nil => None
    case head :: tail =>
      items = tail
      Some(head)
  }
  
  def peek: Option[T] = items.headOption
  def isEmpty: Boolean = items.isEmpty
}

// Очередь на основе List
class Queue[T] {
  private var items: List[T] = Nil
  
  def enqueue(item: T): Unit = {
    items = items :+ item
  }
  
  def dequeue(): Option[T] = items match {
    case Nil => None
    case head :: tail =>
      items = tail
      Some(head)
  }
  
  def peek: Option[T] = items.headOption
  def isEmpty: Boolean = items.isEmpty
}
```

**List** может использоваться для реализации различных структур данных, хотя для **production** кода лучше использовать специализированные структуры.

### Обработка больших списков

**Эффективная обработка больших списков:**

```scala
// Batch обработка больших списков
def processInBatches[A, B](list: List[A], batchSize: Int)(processor: List[A] => B): List[B] = {
  list.grouped(batchSize).map(processor).toList
}

// Использование
val largeList = (1 to 1000000).toList
val results = processInBatches(largeList, 1000) { batch =>
  batch.sum
}
```

**Batch** обработка позволяет эффективно работать с большими объемами данных, разбивая их на управляемые части.

### Работа с вложенными списками

```scala
// Разворачивание вложенных списков
val nested = List(List(1, 2), List(3, 4), List(5, 6))
val flattened = nested.flatten  // List(1, 2, 3, 4, 5, 6)

// Транспонирование матрицы
val matrix = List(List(1, 2, 3), List(4, 5, 6))
val transposed = matrix.transpose  // List(List(1, 4), List(2, 5), List(3, 6))
```

Работа с вложенными списками позволяет обрабатывать многомерные структуры данных.

### Обработка больших списков

**При работе с большими списками важно учитывать производительность:**

```scala
// Использование view для ленивой обработки
val largeList = (1 to 1000000).toList
val result = largeList.view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(10)
  .toList  // вычисления выполняются только здесь

// Использование iterator для потоковой обработки
val iterator = largeList.iterator
val processed = iterator
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(10)
  .toList
```

**View** и **Iterator** позволяют обрабатывать большие списки без создания промежуточных коллекций.

### Рекурсивная обработка списков

**Рекурсия является естественным способом обработки списков:**

```scala
// Хвостовая рекурсия для суммирования
@annotation.tailrec
def sum(list: List[Int], acc: Int = 0): Int = list match {
  case Nil => acc
  case head :: tail => sum(tail, acc + head)
}

// Рекурсивная фильтрация
def filterRecursive[A](list: List[A], predicate: A => Boolean): List[A] = list match {
  case Nil => Nil
  case head :: tail =>
    if (predicate(head)) head :: filterRecursive(tail, predicate)
    else filterRecursive(tail, predicate)
}
```

Рекурсивная обработка списков соответствует функциональному стилю программирования.

## Продвинутые техники работы с **List**

### Оптимизация производительности **List**

**List** оптимизирован для операций в начале списка.

```scala
// Эффективное добавление в начало
val list = 0 :: List(1, 2, 3)  // O(1) - очень быстро

// Эффективный доступ к голове
val head = list.head  // O(1) - очень быстро

// Эффективный доступ к хвосту
val tail = list.tail  // O(1) - очень быстро

// Неэффективное добавление в конец
val appended = list :+ 4  // O(n) - медленно для больших списков
```

### Работа с большими **List**

**List** эффективно работает с последовательными операциями.

```scala
// Создание большого List
val largeList = (1 to 1000000).toList

// Эффективная обработка с view
val processed = largeList.view
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toList

// Параллельная обработка
val parallelProcessed = largeList.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Сравнение **List** с другими структурами

**List** предоставляет оптимальную производительность для последовательных операций.

```scala
// List vs Vector
val list = List(1, 2, 3, 4, 5)
val vec = Vector(1, 2, 3, 4, 5)

// Доступ по индексу
list(2)  // O(n) - медленно
vec(2)   // O(log32(n)) - быстро

// Добавление в начало
0 :: list  // O(1) - быстро
0 +: vec   // O(log32(n)) - медленнее

// Pattern matching
list match {
  case head :: tail => // O(1) - эффективно
}
```

### Практические примеры: Рекурсивная обработка **List**

```scala
// Рекурсивное суммирование
def sumRecursive(list: List[Int]): Int = list match {
  case Nil => 0
  case head :: tail => head + sumRecursive(tail)
}

// Рекурсивная реверс функция
def reverseRecursive[A](list: List[A]): List[A] = list match {
  case Nil => Nil
  case head :: tail => reverseRecursive(tail) :+ head
}

// Рекурсивная фильтрация
def filterRecursive[A](list: List[A], predicate: A => Boolean): List[A] = list match {
  case Nil => Nil
  case head :: tail if predicate(head) => head :: filterRecursive(tail, predicate)
  case _ :: tail => filterRecursive(tail, predicate)
}
```

### Практические примеры: Обработка вложенных **List**

```scala
// Выравнивание вложенных списков
def flatten[A](list: List[List[A]]): List[A] = {
  list.foldLeft(List.empty[A])(_ ++ _)
}

// Map с вложенными структурами
def mapNested[A, B](list: List[List[A]], f: A => B): List[List[B]] = {
  list.map(_.map(f))
}

// Фильтрация вложенных структур
def filterNested[A](list: List[List[A]], predicate: A => Boolean): List[List[A]] = {
  list.map(_.filter(predicate)).filter(_.nonEmpty)
}
```

### Практические примеры: Работа с большими **List**

```scala
// Обработка больших списков по частям
def processInChunks[A, B](
  list: List[A],
  chunkSize: Int,
  processor: List[A] => B
): List[B] = {
  list.grouped(chunkSize).map(processor).toList
}

// Параллельная обработка
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def processParallel[A, B](list: List[A], processor: A => B): Future[List[B]] = {
  val futures = list.map(item => Future(processor(item)))
  Future.sequence(futures)
}
```

**List** является основой функционального программирования в **Scala**. Понимание его структуры, операций, особенностей производительности, оптимизации производительности, работы с большими **List**, сравнения с другими структурами данных, **pattern matching** для декомпозиции, рекурсивной обработки, обработки вложенных структур, параллельной обработки и практических применений позволяет эффективно работать с последовательностями данных. Использование **immutable List** по умолчанию, **ListBuffer** для накопления, **view** для ленивых вычислений, **pattern matching** для декомпозиции, рекурсивных функций для обработки, **chunking** для больших данных и параллельной обработки делает код более безопасным, выразительным и эффективным.

### Практические примеры: Работа с **zip** и **unzip**

```scala
// Zip для объединения двух списков
val list1 = List(1, 2, 3)
val list2 = List("a", "b", "c")
val zipped = list1.zip(list2)  // List((1, "a"), (2, "b"), (3, "c"))

// Unzip для разделения списка пар
val pairs = List((1, "a"), (2, "b"), (3, "c"))
val (numbers, letters) = pairs.unzip
// numbers: List(1, 2, 3)
// letters: List("a", "b", "c")
```

### Практические примеры: Работа с индексами

```scala
// Работа с индексами
val list = List("a", "b", "c", "d", "e")

// Zip с индексами
val withIndices = list.zipWithIndex
// List(("a", 0), ("b", 1), ("c", 2), ("d", 3), ("e", 4))

// Поиск индекса элемента
val index = list.indexWhere(_ == "c")  // 2

// Поиск всех индексов
val indices = list.zipWithIndex.filter(_._1 == "a").map(_._2)
```

### Использование с различными операциями для производительности

```scala
// Оптимизация операций над List
val largeList = (1 to 1000000).toList

// Эффективная обработка
val processed = largeList
  .view  // Ленивое представление
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .take(100)
  .toList  // Материализация только необходимых элементов

// Параллельная обработка
import scala.collection.parallel.CollectionConverters._
val parallelProcessed = largeList.par
  .filter(_ % 2 == 0)
  .map(_ * 2)
  .seq
```

### Использование с различными операциями для работы с индексами

```scala
// Работа с индексами
val list = List("a", "b", "c", "d", "e")

// Доступ по индексу
val value = list(2)  // O(n) - "c"

// Обновление по индексу
val updated = list.updated(2, "x")  // O(n)

// Получение диапазона
val slice = list.slice(1, 4)  // O(n) - List("b", "c", "d")

// Поиск индекса
val index = list.indexOf("c")  // O(n) - 2
```

## Дополнительные ресурсы

**Для дальнейшего изучения **List** в **Scala** рекомендуется:**

- [Scala List API Documentation](https://www.scala-lang.org/api/current/scala/collection/immutable/List.html)
- [Scala Collections Overview](https://docs.scala-lang.org/overviews/collections-2.13/overview.html)
- [Scala School — Collections](https://twitter.github.io/scala_school/collections.html)
