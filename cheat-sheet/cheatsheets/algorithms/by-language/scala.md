---
title: "Bubble Sort - Scala"
description: "Функциональная реализация алгоритма пузырьковой сортировки на Scala с использованием неизменяемых структур данных"
tags: ["algorithm", "sorting", "bubble-sort", "scala", "functional-programming"]
difficulty: "intermediate"
prerequisites: ["bubble-sort/kotlin.md"]
next: ["bubble-sort/go.md"]
updated: "2025-01-11"
---

# Bubble Sort - Scala

## Императивная реализация

```scala
object BubbleSort {

  /**
   * Императивная пузырьковая сортировка массива
   */
  def bubbleSort(arr: Array[Int]): Unit = {
    val n = arr.length
    for (i <- 0 until n - 1) {
      for (j <- 0 until n - i - 1) {
        if (arr(j) > arr(j + 1)) {
          // Меняем элементы местами
          val temp = arr(j)
          arr(j) = arr(j + 1)
          arr(j + 1) = temp
        }
      }
    }
  }

  /**
   * Императивная пузырьковая сортировка для ArrayBuffer (изменяемой коллекции)
   */
  def bubbleSortMutable(arr: scala.collection.mutable.ArrayBuffer[Int]): Unit = {
    val n = arr.length
    for (i <- 0 until n - 1) {
      for (j <- 0 until n - i - 1) {
        if (arr(j) > arr(j + 1)) {
          // Меняем элементы местами
          val temp = arr(j)
          arr(j) = arr(j + 1)
          arr(j + 1) = temp
        }
      }
    }
  }
}
```

## Функциональная реализация

```scala
object FunctionalBubbleSort {

  /**
   * Функциональная пузырьковая сортировка с использованием рекурсии
   */
  def bubbleSortFunctional(arr: List[Int]): List[Int] = {
    def sort(remaining: List[Int], passes: Int): List[Int] = {
      if (passes == 0 || remaining.isEmpty) remaining
      else {
        val (sorted, _) = bubblePass(remaining)
        sort(sorted, passes - 1)
      }
    }

    sort(arr, arr.length - 1)
  }

  /**
   * Один проход пузырьковой сортировки
   */
  private def bubblePass(arr: List[Int]): (List[Int], Boolean) = arr match {
    case Nil => (Nil, false)
    case x :: Nil => (List(x), false)
    case x :: y :: rest =>
      if (x > y) {
        val (sortedRest, swapped) = bubblePass(y :: rest)
        (x :: sortedRest, true)
      } else {
        val (sortedRest, swapped) = bubblePass(y :: rest)
        (y :: sortedRest, swapped)
      }
  }

  /**
   * Функциональная сортировка с использованием foldLeft
   */
  def bubbleSortFold(arr: List[Int]): List[Int] = {
    (0 until arr.length - 1).foldLeft(arr) { (currentArr, _) =>
      bubblePassFold(currentArr)
    }
  }

  private def bubblePassFold(arr: List[Int]): List[Int] = arr match {
    case Nil => Nil
    case x :: Nil => List(x)
    case x :: y :: rest =>
      if (x > y) x :: bubblePassFold(y :: rest)
      else y :: bubblePassFold(x :: rest)
  }
}
```

## Обобщенная реализация

```scala
object GenericBubbleSort {

  /**
   * Обобщенная пузырьковая сортировка
   */
  def bubbleSort[T](arr: Array[T])(implicit ord: Ordering[T]): Unit = {
    val n = arr.length
    for (i <- 0 until n - 1) {
      for (j <- 0 until n - i - 1) {
        if (ord.gt(arr(j), arr(j + 1))) {
          // Меняем элементы местами
          val temp = arr(j)
          arr(j) = arr(j + 1)
          arr(j + 1) = temp
        }
      }
    }
  }

  /**
   * Обобщенная функциональная сортировка
   */
  def bubbleSortFunctional[T: Ordering](list: List[T]): List[T] = {
    import Ordered._
    def sort(remaining: List[T], passes: Int): List[T] = {
      if (passes == 0 || remaining.isEmpty) remaining
      else {
        val sorted = bubblePass(remaining)
        sort(sorted, passes - 1)
      }
    }

    sort(list, list.length - 1)
  }

  private def bubblePass[T: Ordering](list: List[T]): List[T] = list match {
    case Nil => Nil
    case x :: Nil => List(x)
    case x :: y :: rest =>
      if (x > y) x :: bubblePass(y :: rest)
      else y :: bubblePass(x :: rest)
  }
}
```

## Реализация с тайпклассами и монадами

```scala
import scala.language.higherKinds

object AdvancedBubbleSort {

  /**
   * Сортировка с использованием тайпклассов
   */
  trait Sortable[T] {
    def compare(a: T, b: T): Int
  }

  object Sortable {
    implicit val intSortable: Sortable[Int] = (a, b) => a.compare(b)
    implicit val stringSortable: Sortable[String] = (a, b) => a.compare(b)

    implicit def optionSortable[T](implicit s: Sortable[T]): Sortable[Option[T]] = {
      case (Some(a), Some(b)) => s.compare(a, b)
      case (Some(_), None) => 1
      case (None, Some(_)) => -1
      case (None, None) => 0
    }
  }

  def bubbleSort[T: Sortable](arr: Array[T]): Unit = {
    val sortable = implicitly[Sortable[T]]
    val n = arr.length

    for (i <- 0 until n - 1) {
      for (j <- 0 until n - i - 1) {
        if (sortable.compare(arr(j), arr(j + 1)) > 0) {
          val temp = arr(j)
          arr(j) = arr(j + 1)
          arr(j + 1) = temp
        }
      }
    }
  }

  /**
   * Монадическая сортировка с использованием Either для обработки ошибок
   */
  def safeBubbleSort[T: Ordering](list: List[T]): Either[String, List[T]] = {
    if (list.length > 1000) {
      Left("Список слишком большой для пузырьковой сортировки")
    } else {
      Right(GenericBubbleSort.bubbleSortFunctional(list))
    }
  }

  /**
   * Сортировка с логированием с использованием Writer монады
   */
  import scalaz.Writer
  import scalaz.std.list._
  import scalaz.syntax.writer._

  type Logged[A] = Writer[List[String], A]

  def bubbleSortWithLogging(arr: Array[Int]): Logged[Unit] = {
    val n = arr.length
    var swaps = 0
    var comparisons = 0

    for (i <- 0 until n - 1) {
      for (j <- 0 until n - i - 1) {
        comparisons += 1
        if (arr(j) > arr(j + 1)) {
          val temp = arr(j)
          arr(j) = arr(j + 1)
          arr(j + 1) = temp
          swaps += 1

          // Логируем обмен
          s"Обмен: ${arr(j)} <-> ${arr(j + 1)} на позициях $j и ${j + 1}".tell
        }
      }
    }

    s"Всего сравнений: $comparisons, обменов: $swaps".tell
  }
}
```

## Тесты

```scala
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BubbleSortSpec extends AnyFlatSpec with Matchers {

  "BubbleSort.bubbleSort" should "sort an array correctly" in {
    val arr = Array(64, 34, 25, 12, 22, 11, 90)
    val expected = Array(11, 12, 22, 25, 34, 64, 90)

    BubbleSort.bubbleSort(arr)

    arr should equal(expected)
  }

  "FunctionalBubbleSort.bubbleSortFunctional" should "sort a list functionally" in {
    val list = List(64, 34, 25, 12, 22, 11, 90)
    val expected = List(11, 12, 22, 25, 34, 64, 90)

    val result = FunctionalBubbleSort.bubbleSortFunctional(list)

    result should equal(expected)
    list should equal(List(64, 34, 25, 12, 22, 11, 90)) // Оригинальный список не изменился
  }

  "GenericBubbleSort.bubbleSort" should "sort arrays of different types" in {
    val intArr = Array(3, 1, 4, 1, 5)
    val stringArr = Array("zebra", "apple", "banana")

    GenericBubbleSort.bubbleSort(intArr)
    GenericBubbleSort.bubbleSort(stringArr)

    intArr should equal(Array(1, 1, 3, 4, 5))
    stringArr should equal(Array("apple", "banana", "zebra"))
  }

  "AdvancedBubbleSort.safeBubbleSort" should "handle large lists safely" in {
    val largeList = (1 to 1001).toList

    val result = AdvancedBubbleSort.safeBubbleSort(largeList)

    result should be a Symbol("left")
    result.left.get should include("слишком большой")
  }

  "FunctionalBubbleSort.bubbleSortFold" should "sort using fold" in {
    val list = List(5, 3, 8, 1, 9, 2)
    val expected = List(1, 2, 3, 5, 8, 9)

    val result = FunctionalBubbleSort.bubbleSortFold(list)

    result should equal(expected)
  }

  it should "handle edge cases" in {
    // Пустой список
    FunctionalBubbleSort.bubbleSortFunctional(List.empty[Int]) should equal(List.empty[Int])

    // Список из одного элемента
    FunctionalBubbleSort.bubbleSortFunctional(List(42)) should equal(List(42))

    // Уже отсортированный список
    val sorted = List(1, 2, 3, 4, 5)
    FunctionalBubbleSort.bubbleSortFunctional(sorted) should equal(sorted)
  }
}
```

## Производительность и оптимизации

### Сравнение реализаций

```scala
object PerformanceComparison {

  import scala.collection.mutable.ArrayBuffer

  def time[R](block: => R): (R, Long) = {
    val start = System.nanoTime()
    val result = block
    val end = System.nanoTime()
    (result, end - start)
  }

  def benchmarkSorts(): Unit = {
    val sizes = List(100, 500, 1000, 2000)

    println("Сравнение производительности пузырьковой сортировки")
    println("Размер массива\tИмперативная (мс)\tФункциональная (мс)")

    for (size <- sizes) {
      val arr = Array.fill(size)(scala.util.Random.nextInt(1000))
      val list = arr.toList

      // Императивная сортировка
      val arrCopy = arr.clone()
      val (imperativeResult, imperativeTime) = time {
        BubbleSort.bubbleSort(arrCopy)
      }

      // Функциональная сортировка
      val (functionalResult, functionalTime) = time {
        FunctionalBubbleSort.bubbleSortFunctional(list)
      }

      println(f"$size%d\t\t${imperativeTime / 1_000_000}%d\t\t\t${functionalTime / 1_000_000}%d")
    }
  }
}
```

## Особенности Scala реализации

### Преимущества функционального подхода:

1. **Неизменяемость**: Функциональная версия не изменяет входные данные
2. **Рекурсия**: Чисто функциональная реализация использует рекурсию
3. **Pattern Matching**: Мощный механизм для работы с алгебраическими типами данных
4. **Вывод типов**: Scala может автоматически выводить типы
5. **Коллекции**: Богатый API для работы с неизменяемыми коллекциями

### Недостатки:

1. **Производительность**: Функциональная версия создает много промежуточных объектов
2. **Стек**: Глубокая рекурсия может вызвать переполнение стека
3. **Сложность**: Функциональный код может быть сложнее для понимания

## Применение в реальных проектах

```scala
// Пример использования в веб-приложении с Akka HTTP
object SortService {
  import akka.http.scaladsl.server.Directives._
  import akka.http.scaladsl.model.StatusCodes
  import scala.concurrent.ExecutionContext.Implicits.global

  val route =
    path("sort" / "bubble") {
      post {
        entity(as[List[Int]]) { numbers =>
          if (numbers.length > 1000) {
            complete(StatusCodes.BadRequest, "Слишком большой массив")
          } else {
            val sorted = FunctionalBubbleSort.bubbleSortFunctional(numbers)
            complete(sorted)
          }
        }
      }
    }
}
```

## Заключение

Scala предлагает уникальный подход к реализации алгоритмов, сочетая объектно-ориентированное и функциональное программирование:

- **Функциональная парадигма**: Неизменяемость, рекурсия, высшие функции
- **Pattern Matching**: Мощный инструмент для работы с данными
- **Type System**: Сильная статическая типизация с выводом типов
- **Коллекции**: Богатый API для функционального программирования
- **Акторы**: Для конкурентного программирования

Пузырьковая сортировка на Scala демонстрирует все возможности языка для создания выразительного и безопасного кода.
