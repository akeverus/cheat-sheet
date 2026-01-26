---
title: "Bubble Sort - Go"
description: "Эффективная реализация алгоритма пузырьковой сортировки на Go с использованием slices и конкурентности"
tags: ["algorithm", "sorting", "bubble-sort", "go", "golang", "concurrency"]
difficulty: "beginner"
prerequisites: ["bubble-sort/scala.md"]
next: []
updated: "2025-01-11"
---

# Bubble Sort - Go

## Базовая реализация

```go
package bubblesort

// BubbleSort сортирует массив целых чисел пузырьковой сортировкой
func BubbleSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        for j := 0; j < n-i-1; j++ {
            if arr[j] > arr[j+1] {
                // Меняем элементы местами
                arr[j], arr[j+1] = arr[j+1], arr[j]
            }
        }
    }
}

// BubbleSortStrings сортирует массив строк
func BubbleSortStrings(arr []string) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        for j := 0; j < n-i-1; j++ {
            if arr[j] > arr[j+1] {
                // Меняем элементы местами
                arr[j], arr[j+1] = arr[j+1], arr[j]
            }
        }
    }
}
```

## Оптимизированная версия

```go
// OptimizedBubbleSort оптимизированная пузырьковая сортировка
// Прекращает работу, если в проходе не было обменов
func OptimizedBubbleSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-i-1; j++ {
            if arr[j] > arr[j+1] {
                // Меняем элементы местами
                arr[j], arr[j+1] = arr[j+1], arr[j]
                swapped = true
            }
        }

        // Если не было обменов, массив уже отсортирован
        if !swapped {
            break
        }
    }
}
```

## Обобщенная реализация

```go
package bubblesort

import "golang.org/x/exp/constraints"

// BubbleSortGeneric обобщенная пузырьковая сортировка
func BubbleSortGeneric[T constraints.Ordered](arr []T) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        for j := 0; j < n-i-1; j++ {
            if arr[j] > arr[j+1] {
                // Меняем элементы местами
                arr[j], arr[j+1] = arr[j+1], arr[j]
            }
        }
    }
}

// BubbleSortFunc сортировка с пользовательской функцией сравнения
func BubbleSortFunc[T any](arr []T, less func(a, b T) bool) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        for j := 0; j < n-i-1; j++ {
            if less(arr[j+1], arr[j]) {
                // Меняем элементы местами
                arr[j], arr[j+1] = arr[j+1], arr[j]
            }
        }
    }
}
```

## Реализация с возвратом нового слайса

```go
// BubbleSortNew возвращает новый отсортированный слайс
func BubbleSortNew(arr []int) []int {
    // Создаем копию массива
    result := make([]int, len(arr))
    copy(result, arr)

    BubbleSort(result)
    return result
}

// BubbleSortNewGeneric обобщенная версия с возвратом нового слайса
func BubbleSortNewGeneric[T constraints.Ordered](arr []T) []T {
    // Создаем копию массива
    result := make([]T, len(arr))
    copy(result, arr)

    BubbleSortGeneric(result)
    return result
}
```

## Реализация с сортировкой структур

```go
import "time"

// Person структура для демонстрации сортировки
type Person struct {
    Name string
    Age  int
}

// ByAge тип для сортировки по возрасту
type ByAge []Person

func (a ByAge) Len() int           { return len(a) }
func (a ByAge) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a ByAge) Less(i, j int) bool { return a[i].Age < a[j].Age }

// ByName тип для сортировки по имени
type ByName []Person

func (a ByName) Len() int           { return len(a) }
func (a ByName) Swap(i, j int)      { a[i], a[j] = a[i], a[j] }
func (a ByName) Less(i, j int) bool { return a[i].Name < a[j].Name }

// BubbleSortPersons сортировка массива Person пузырьковой сортировкой
func BubbleSortPersons(persons []Person, less func(a, b Person) bool) {
    n := len(persons)
    for i := 0; i < n-1; i++ {
        for j := 0; j < n-i-1; j++ {
            if less(persons[j+1], persons[j]) {
                // Меняем элементы местами
                persons[j], persons[j+1] = persons[j+1], persons[j]
            }
        }
    }
}
```

## Конкурентная реализация

```go
import (
    "runtime"
    "sync"
)

// ConcurrentBubbleSort конкурентная пузырьковая сортировка
func ConcurrentBubbleSort(arr []int) {
    n := len(arr)
    numGoroutines := runtime.NumCPU()

    for i := 0; i < n-1; i++ {
        swapped := false
        var wg sync.WaitGroup

        // Разделяем работу между горутинами
        chunkSize := (n - i) / numGoroutines
        if chunkSize < 2 {
            chunkSize = 2 // Минимум 2 элемента для сортировки
        }

        for start := 0; start < n-i-1; start += chunkSize {
            end := start + chunkSize
            if end > n-i-1 {
                end = n - i - 1
            }

            wg.Add(1)
            go func(start, end int) {
                defer wg.Done()
                for j := start; j < end; j++ {
                    if arr[j] > arr[j+1] {
                        arr[j], arr[j+1] = arr[j+1], arr[j]
                        swapped = true
                    }
                }
            }(start, end)
        }

        wg.Wait()

        // Если не было обменов, массив уже отсортирован
        if !swapped {
            break
        }
    }
}
```

## Реализация с метриками производительности

```go
import "time"

// SortMetrics метрики сортировки
type SortMetrics struct {
    Duration      time.Duration
    Comparisons   int64
    Swaps         int64
    Passes        int
}

// BubbleSortWithMetrics сортировка с сбором метрик
func BubbleSortWithMetrics(arr []int) SortMetrics {
    start := time.Now()
    n := len(arr)

    var comparisons, swaps int64
    passes := 0

    for i := 0; i < n-1; i++ {
        swapped := false
        passes++

        for j := 0; j < n-i-1; j++ {
            comparisons++
            if arr[j] > arr[j+1] {
                // Меняем элементы местами
                arr[j], arr[j+1] = arr[j+1], arr[j]
                swaps++
                swapped = true
            }
        }

        // Если не было обменов, массив уже отсортирован
        if !swapped {
            break
        }
    }

    duration := time.Since(start)

    return SortMetrics{
        Duration:    duration,
        Comparisons: comparisons,
        Swaps:       swaps,
        Passes:      passes,
    }
}
```

## Модульные тесты

```go
package bubblesort

import (
    "reflect"
    "testing"
    "sort"
)

func TestBubbleSort(t *testing.T) {
    tests := []struct {
        name     string
        input    []int
        expected []int
    }{
        {
            name:     "normal case",
            input:    []int{64, 34, 25, 12, 22, 11, 90},
            expected: []int{11, 12, 22, 25, 34, 64, 90},
        },
        {
            name:     "already sorted",
            input:    []int{1, 2, 3, 4, 5},
            expected: []int{1, 2, 3, 4, 5},
        },
        {
            name:     "reverse sorted",
            input:    []int{5, 4, 3, 2, 1},
            expected: []int{1, 2, 3, 4, 5},
        },
        {
            name:     "empty array",
            input:    []int{},
            expected: []int{},
        },
        {
            name:     "single element",
            input:    []int{42},
            expected: []int{42},
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            arr := make([]int, len(tt.input))
            copy(arr, tt.input)

            BubbleSort(arr)

            if !reflect.DeepEqual(arr, tt.expected) {
                t.Errorf("BubbleSort() = %v, want %v", arr, tt.expected)
            }
        })
    }
}

func TestOptimizedBubbleSort(t *testing.T) {
    arr := []int{1, 2, 3, 4, 5}
    expected := []int{1, 2, 3, 4, 5}

    OptimizedBubbleSort(arr)

    if !reflect.DeepEqual(arr, expected) {
        t.Errorf("OptimizedBubbleSort() = %v, want %v", arr, expected)
    }
}

func TestBubbleSortGeneric(t *testing.T) {
    // Тест для строк
    strings := []string{"zebra", "apple", "banana"}
    expectedStrings := []string{"apple", "banana", "zebra"}

    BubbleSortGeneric(strings)

    if !reflect.DeepEqual(strings, expectedStrings) {
        t.Errorf("BubbleSortGeneric() strings = %v, want %v", strings, expectedStrings)
    }

    // Тест для float64
    floats := []float64{3.14, 1.41, 2.71}
    expectedFloats := []float64{1.41, 2.71, 3.14}

    BubbleSortGeneric(floats)

    if !reflect.DeepEqual(floats, expectedFloats) {
        t.Errorf("BubbleSortGeneric() floats = %v, want %v", floats, expectedFloats)
    }
}

func TestBubbleSortNew(t *testing.T) {
    original := []int{3, 1, 4, 1, 5}
    expected := []int{1, 1, 3, 4, 5}

    result := BubbleSortNew(original)

    // Проверяем, что оригинальный массив не изменился
    if !reflect.DeepEqual(original, []int{3, 1, 4, 1, 5}) {
        t.Errorf("Original array was modified: %v", original)
    }

    // Проверяем результат сортировки
    if !reflect.DeepEqual(result, expected) {
        t.Errorf("BubbleSortNew() = %v, want %v", result, expected)
    }
}

func TestBubbleSortPersons(t *testing.T) {
    persons := []Person{
        {"Alice", 30},
        {"Bob", 25},
        {"Charlie", 35},
    }

    // Сортировка по возрасту
    BubbleSortPersons(persons, func(a, b Person) bool {
        return a.Age < b.Age
    })

    expected := []Person{
        {"Bob", 25},
        {"Alice", 30},
        {"Charlie", 35},
    }

    if !reflect.DeepEqual(persons, expected) {
        t.Errorf("BubbleSortPersons() = %v, want %v", persons, expected)
    }
}

func TestBubbleSortWithMetrics(t *testing.T) {
    arr := []int{3, 1, 4, 1, 5}

    metrics := BubbleSortWithMetrics(arr)

    // Проверяем, что сортировка работает
    if !sort.IntsAreSorted(arr) {
        t.Errorf("Array is not sorted: %v", arr)
    }

    // Проверяем метрики
    if metrics.Comparisons <= 0 {
        t.Errorf("Comparisons should be > 0, got %d", metrics.Comparisons)
    }

    if metrics.Swaps < 0 {
        t.Errorf("Swaps should be >= 0, got %d", metrics.Swaps)
    }

    if metrics.Passes <= 0 {
        t.Errorf("Passes should be > 0, got %d", metrics.Passes)
    }

    if metrics.Duration <= 0 {
        t.Errorf("Duration should be > 0, got %v", metrics.Duration)
    }
}

func BenchmarkBubbleSort(b *testing.B) {
    arr := []int{64, 34, 25, 12, 22, 11, 90}

    for i := 0; i < b.N; i++ {
        arrCopy := make([]int, len(arr))
        copy(arrCopy, arr)
        BubbleSort(arrCopy)
    }
}

func BenchmarkOptimizedBubbleSort(b *testing.B) {
    arr := []int{64, 34, 25, 12, 22, 11, 90}

    for i := 0; i < b.N; i++ {
        arrCopy := make([]int, len(arr))
        copy(arrCopy, arr)
        OptimizedBubbleSort(arrCopy)
    }
}
```

## Особенности Go реализации

### Преимущества Go:

1. **Простота**: Четкий и понятный синтаксис
2. **Эффективность**: Компиляция в машинный код, низкие накладные расходы
3. **Конкурентность**: Встроенная поддержка горутин и каналов
4. **Безопасность**: Автоматическое управление памятью, отсутствие указателей на nil
5. **Стандартная библиотека**: Богатый набор функций для работы с данными

### Специфические возможности Go:

```go
// Множественное присваивание для обмена значениями
arr[j], arr[j+1] = arr[j+1], arr[j]

// Slices вместо массивов фиксированного размера
func BubbleSort(arr []int) // Принимает slice, а не массив

// Empty interface для обобщенного программирования (до Go 1.18)
func BubbleSortInterface(arr []interface{}, less func(a, b interface{}) bool)

// Generics в Go 1.18+
func BubbleSortGeneric[T constraints.Ordered](arr []T)
```

## Производительность

### Сравнение реализаций

```go
package main

import (
    "fmt"
    "math/rand"
    "time"
)

func benchmarkSort(name string, sortFunc func([]int), size int) {
    arr := make([]int, size)
    for i := range arr {
        arr[i] = rand.Intn(1000)
    }

    start := time.Now()
    sortFunc(arr)
    duration := time.Since(start)

    fmt.Printf("%s (size %d): %v\n", name, size, duration)
}

func main() {
    rand.Seed(time.Now().UnixNano())

    sizes := []int{100, 1000, 10000}

    for _, size := range sizes {
        fmt.Printf("\nРазмер массива: %d\n", size)

        benchmarkSort("BubbleSort", BubbleSort, size)
        benchmarkSort("OptimizedBubbleSort", OptimizedBubbleSort, size)

        if size <= 1000 {
            benchmarkSort("ConcurrentBubbleSort", ConcurrentBubbleSort, size)
        }
    }
}
```

## Интеграция со стандартной библиотекой

```go
import "sort"

// Использование стандартной сортировки Go
func StandardSort(arr []int) {
    sort.Ints(arr)
}

// Сортировка с кастомным компаратором
type Person struct {
    Name string
    Age  int
}

func SortPersonsByAge(persons []Person) {
    sort.Slice(persons, func(i, j int) bool {
        return persons[i].Age < persons[j].Age
    })
}

// Сортировка с сохранением стабильности
func StableSort(arr []int) {
    sort.Stable(sort.IntSlice(arr))
}
```

## Заключение

Go предлагает эффективный и простой подход к реализации алгоритмов сортировки:

- **Производительность**: Компиляция в машинный код обеспечивает высокую скорость
- **Конкурентность**: Легко добавить параллельную обработку с помощью горутин
- **Память**: Автоматическое управление памятью без сборщика мусора в режиме реального времени
- **Простота**: Четкий синтаксис и минималистичный подход

Пузырьковая сортировка на Go демонстрирует баланс между простотой реализации и производительностью, что делает язык отличным выбором для системного программирования и микросервисов.
