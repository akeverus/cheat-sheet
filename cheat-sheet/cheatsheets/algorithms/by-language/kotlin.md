---
title: "Bubble Sort - Kotlin"
description: "Реализация алгоритма пузырьковой сортировки на Kotlin с использованием современных возможностей языка"
tags: ["algorithm", "sorting", "bubble-sort", "kotlin"]
difficulty: "beginner"
prerequisites: ["bubble-sort/java.md"]
next: ["bubble-sort/scala.md"]
updated: "2025-01-11"
---

# Bubble Sort - Kotlin

## Базовая реализация

```kotlin
/**
 * Пузырьковая сортировка массива
 */
fun bubbleSort(arr: IntArray) {
    val n = arr.size
    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (arr[j] > arr[j + 1]) {
                // Меняем элементы местами
                val temp = arr[j]
                arr[j] = arr[j + 1]
                arr[j + 1] = temp
            }
        }
    }
}

/**
 * Пузырьковая сортировка для MutableList
 */
fun <T : Comparable<T>> bubbleSort(list: MutableList<T>) {
    val n = list.size
    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (list[j] > list[j + 1]) {
                // Меняем элементы местами
                val temp = list[j]
                list[j] = list[j + 1]
                list[j + 1] = temp
            }
        }
    }
}
```

## Оптимизированная версия

```kotlin
/**
 * Оптимизированная пузырьковая сортировка
 * Прекращает работу, если в проходе не было обменов
 */
fun optimizedBubbleSort(arr: IntArray) {
    val n = arr.size
    var swapped: Boolean

    for (i in 0 until n - 1) {
        swapped = false
        for (j in 0 until n - i - 1) {
            if (arr[j] > arr[j + 1]) {
                // Меняем элементы местами
                val temp = arr[j]
                arr[j] = arr[j + 1]
                arr[j + 1] = temp
                swapped = true
            }
        }

        // Если не было обменов, массив уже отсортирован
        if (!swapped) {
            break
        }
    }
}

/**
 * Оптимизированная версия для MutableList
 */
fun <T : Comparable<T>> optimizedBubbleSort(list: MutableList<T>) {
    val n = list.size
    var swapped: Boolean

    for (i in 0 until n - 1) {
        swapped = false
        for (j in 0 until n - i - 1) {
            if (list[j] > list[j + 1]) {
                // Меняем элементы местами
                val temp = list[j]
                list[j] = list[j + 1]
                list[j + 1] = temp
                swapped = true
            }
        }

        // Если не было обменов, массив уже отсортирован
        if (!swapped) {
            break
        }
    }
}
```

## Функциональная версия с Kotlin

```kotlin
/**
 * Функциональная версия пузырьковой сортировки
 */
fun functionalBubbleSort(arr: IntArray): IntArray {
    val result = arr.copyOf()
    val n = result.size

    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (result[j] > result[j + 1]) {
                // Меняем элементы местами
                val temp = result[j]
                result[j] = result[j + 1]
                result[j + 1] = temp
            }
        }
    }

    return result
}

/**
 * Функциональная версия с использованием sequence
 */
fun sequenceBubbleSort(arr: IntArray): IntArray {
    val result = arr.copyOf()
    val n = result.size

    (0 until n - 1).forEach { i ->
        (0 until n - i - 1).forEach { j ->
            if (result[j] > result[j + 1]) {
                val temp = result[j]
                result[j] = result[j + 1]
                result[j + 1] = temp
            }
        }
    }

    return result
}
```

## Extension функции

```kotlin
/**
 * Extension функция для сортировки массива пузырьковой сортировкой
 */
fun IntArray.bubbleSorted(): IntArray {
    val result = this.copyOf()
    result.bubbleSort()
    return result
}

/**
 * Extension функция для сортировки MutableList
 */
fun <T : Comparable<T>> MutableList<T>.bubbleSorted(): List<T> {
    val result = this.toMutableList()
    result.bubbleSort()
    return result
}

/**
 * In-place сортировка для IntArray
 */
fun IntArray.bubbleSort() {
    val n = this.size
    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (this[j] > this[j + 1]) {
                val temp = this[j]
                this[j] = this[j + 1]
                this[j + 1] = temp
            }
        }
    }
}

/**
 * In-place сортировка для MutableList
 */
fun <T : Comparable<T>> MutableList<T>.bubbleSort() {
    val n = this.size
    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (this[j] > this[j + 1]) {
                val temp = this[j]
                this[j] = this[j + 1]
                this[j + 1] = temp
            }
        }
    }
}
```

## Класс BubbleSort с дополнительными возможностями

```kotlin
class BubbleSort {

    /**
     * Универсальная пузырьковая сортировка с компаратором
     */
    fun <T> sort(arr: Array<T>, comparator: Comparator<T>) {
        val n = arr.size
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                if (comparator.compare(arr[j], arr[j + 1]) > 0) {
                    // Меняем элементы местами
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                }
            }
        }
    }

    /**
     * Сортировка с подсчетом операций
     */
    fun sortWithStats(arr: IntArray): SortStats {
        val n = arr.size
        var comparisons = 0
        var swaps = 0

        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                comparisons++
                if (arr[j] > arr[j + 1]) {
                    // Меняем элементы местами
                    val temp = arr[j]
                    arr[j] = arr[j + 1]
                    arr[j + 1] = temp
                    swaps++
                }
            }
        }

        return SortStats(comparisons, swaps)
    }

    /**
     * Статистика сортировки
     */
    data class SortStats(
        val comparisons: Int,
        val swaps: Int
    )
}
```

## Модульные тесты

```kotlin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class BubbleSortTest {

    @Test
    fun `test bubble sort with IntArray`() {
        val arr = intArrayOf(64, 34, 25, 12, 22, 11, 90)
        val expected = intArrayOf(11, 12, 22, 25, 34, 64, 90)

        arr.bubbleSort()

        assertArrayEquals(expected, arr)
    }

    @Test
    fun `test bubble sort with MutableList`() {
        val list = mutableListOf(64, 34, 25, 12, 22, 11, 90)
        val expected = listOf(11, 12, 22, 25, 34, 64, 90)

        list.bubbleSort()

        assertEquals(expected, list)
    }

    @Test
    fun `test functional bubble sort`() {
        val arr = intArrayOf(64, 34, 25, 12, 22, 11, 90)
        val expected = intArrayOf(11, 12, 22, 25, 34, 64, 90)

        val result = functionalBubbleSort(arr)

        assertArrayEquals(expected, result)
        // Проверяем, что оригинальный массив не изменился
        assertArrayEquals(intArrayOf(64, 34, 25, 12, 22, 11, 90), arr)
    }

    @Test
    fun `test extension function bubbleSorted()`() {
        val arr = intArrayOf(64, 34, 25, 12, 22, 11, 90)
        val expected = intArrayOf(11, 12, 22, 25, 34, 64, 90)

        val result = arr.bubbleSorted()

        assertArrayEquals(expected, result)
        // Проверяем, что оригинальный массив не изменился
        assertArrayEquals(intArrayOf(64, 34, 25, 12, 22, 11, 90), arr)
    }

    @Test
    fun `test optimized bubble sort on already sorted array`() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        val expected = intArrayOf(1, 2, 3, 4, 5)

        val stats = BubbleSort().sortWithStats(arr)

        assertArrayEquals(expected, arr)
        // Для уже отсортированного массива количество обменов должно быть 0
        assertEquals(0, stats.swaps)
        assertEquals(10, stats.comparisons) // 4 + 3 + 2 + 1 = 10
    }

    @Test
    fun `test bubble sort with custom comparator`() {
        val arr = arrayOf("apple", "Banana", "cherry", "Date")
        val expected = arrayOf("apple", "Banana", "cherry", "Date") // без учета регистра

        BubbleSort().sort(arr) { a, b -> a.compareTo(b, ignoreCase = true) }

        assertArrayEquals(expected, arr)
    }

    @Test
    fun `test bubble sort with empty array`() {
        val arr = intArrayOf()
        val expected = intArrayOf()

        arr.bubbleSort()

        assertArrayEquals(expected, arr)
    }

    @Test
    fun `test bubble sort with single element`() {
        val arr = intArrayOf(42)
        val expected = intArrayOf(42)

        arr.bubbleSort()

        assertArrayEquals(expected, arr)
    }
}
```

## Сравнение с Java реализацией

### Kotlin преимущества:

1. **Null safety**: Kotlin защищает от NullPointerException
2. **Extension функции**: Позволяют добавлять методы к существующим классам
3. **Data classes**: Для создания простых структур данных
4. **Оператор `?.`**: Для безопасного вызова методов
5. **Inline функции**: Для оптимизации производительности

### Kotlin особенности в реализации:

```kotlin
// Kotlin позволяет использовать ranges
for (i in 0 until n - 1) {
    // ...
}

// Destructuring declarations
val (comparisons, swaps) = BubbleSort().sortWithStats(arr)

// Elvis operator для значений по умолчанию
val result = optionalValue ?: defaultValue
```

## Производительность

Kotlin компилируется в JVM байткод, поэтому производительность аналогична Java. Однако Kotlin предлагает дополнительные возможности для оптимизации:

```kotlin
// Inline функции для снижения накладных расходов
inline fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
}
```

## Заключение

Kotlin предлагает более выразительный и безопасный способ реализации алгоритмов сортировки по сравнению с Java:

- **Безопасность**: Null safety и неизменяемые коллекции
- **Выразительность**: Extension функции и операторы
- **Современный синтаксис**: Ranges, destructuring, Elvis operator
- **Функциональное программирование**: Поддержка функциональных концепций

Пузырьковая сортировка на Kotlin демонстрирует все преимущества языка при работе с алгоритмами.
