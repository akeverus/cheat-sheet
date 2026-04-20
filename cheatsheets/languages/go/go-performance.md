---
title: "Go: производительность"
description: "Полное руководство по оптимизации производительности в Go: профилирование, оптимизация памяти, сборка мусора, best practices"
tags:
  - go
  - golang
  - performance
  - profiling
  - optimization
  - memory
difficulty: "advanced"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: производительность

## Полезные ссылки

- [Go Performance Tips](https://dave.cheney.net/high-performance-go-workshop/dotgo-paris.html)
- [Go Profiling](https://go.dev/blog/pprof)
- [Go Memory Model](https://go.dev/ref/mem)

## Содержание

- [Go: производительность](#go-производительность)
- [Введение в производительность](#введение-в-производительность)
  - [Основные аспекты производительности](#основные-аспекты-производительности)
- [Профилирование](#профилирование)
  - [CPU профилирование](#cpu-профилирование)
  - [Memory профилирование](#memory-профилирование)
  - [Использование pprof](#использование-pprof)
- [HTTP профилирование](#http-профилирование)
- [Оптимизация памяти](#оптимизация-памяти)
  - [Предварительное выделение памяти](#предварительное-выделение-памяти)
  - [Избегание утечек памяти](#избегание-утечек-памяти)
  - [Оптимизация структур](#оптимизация-структур)
- [Сборка мусора](#сборка-мусора)
  - [Настройка GC](#настройка-gc)
- [Установка переменных окружения для GC](#установка-переменных-окружения-для-gc)
  - [Минимизация аллокаций](#минимизация-аллокаций)
  - [Использование sync.Pool](#использование-syncpool)
- [Оптимизация кода](#оптимизация-кода)
  - [Избегание ненужных копирований](#избегание-ненужных-копирований)
  - [Оптимизация строк](#оптимизация-строк)
  - [Оптимизация циклов](#оптимизация-циклов)
  - [Анализ CPU профиля](#анализ-cpu-профиля)
- [Интерактивный режим](#интерактивный-режим)
- [Топ функций по CPU](#топ-функций-по-cpu)
- [Граф вызовов](#граф-вызовов)
- [Список функций](#список-функций)
  - [Анализ Memory профиля](#анализ-memory-профиля)
- [Топ по памяти](#топ-по-памяти)
- [Граф аллокаций](#граф-аллокаций)
- [Детали функции](#детали-функции)
  - [Trace профилирование](#trace-профилирование)
  - [Оптимизация срезов: предварительное выделение](#оптимизация-срезов-предварительное-выделение)
  - [Оптимизация карт: предварительное выделение](#оптимизация-карт-предварительное-выделение)
  - [Оптимизация строк: использование strings.Builder](#оптимизация-строк-использование-stringsbuilder)
  - [Оптимизация структур: выравнивание полей](#оптимизация-структур-выравнивание-полей)
  - [Оптимизация структур: использование указателей](#оптимизация-структур-использование-указателей)
  - [Оптимизация циклов: кэширование значений](#оптимизация-циклов-кэширование-значений)
  - [Оптимизация функций: inline функции](#оптимизация-функций-inline-функции)
  - [Оптимизация функций: избегание defer в горячих путях](#оптимизация-функций-избегание-defer-в-горячих-путях)
  - [Оптимизация интерфейсов: избегание лишних интерфейсов](#оптимизация-интерфейсов-избегание-лишних-интерфейсов)
  - [Оптимизация памяти: использование sync.Pool](#оптимизация-памяти-использование-syncpool)
  - [Оптимизация памяти: переиспользование слайсов](#оптимизация-памяти-переиспользование-слайсов)
  - [Оптимизация GC: минимизация аллокаций](#оптимизация-gc-минимизация-аллокаций)
  - [Оптимизация GC: настройка GOGC](#оптимизация-gc-настройка-gogc)
- [Уменьшение частоты сборки мусора (больше памяти, меньше GC)](#уменьшение-частоты-сборки-мусора-больше-памяти-меньше-gc)
- [Увеличение частоты сборки мусора (меньше памяти, больше GC)](#увеличение-частоты-сборки-мусора-меньше-памяти-больше-gc)
- [Отключение GC (только для тестирования!)](#отключение-gc-только-для-тестирования)
  - [Оптимизация GC: анализ GC](#оптимизация-gc-анализ-gc)
- [Включение трассировки GC](#включение-трассировки-gc)
- [Запуск приложения](#запуск-приложения)
- [Вывод будет содержать информацию о GC:](#вывод-будет-содержать-информацию-о-gc)
- [gc 1 @0.001s 2%: 0.010+0.20+0.003 ms clock, 0.040+0.20/0.20/0+0.012 ms cpu, 4->4->0 MB, 5 MB goal, 4 P](#gc-1-0001s-2-00100200003-ms-clock-004002002000012-ms-cpu-4-4-0-mb-5-mb-goal-4-p)
  - [Оптимизация конкурентности: правильное количество горутин](#оптимизация-конкурентности-правильное-количество-горутин)
  - [Оптимизация конкурентности: worker pools](#оптимизация-конкурентности-worker-pools)
  - [Оптимизация I/O: буферизация](#оптимизация-io-буферизация)
  - [Оптимизация I/O: параллельное чтение](#оптимизация-io-параллельное-чтение)
  - [Практические примеры: оптимизация HTTP сервера](#практические-примеры-оптимизация-http-сервера)
  - [Практические примеры: оптимизация HTTP клиента](#практические-примеры-оптимизация-http-клиента)
  - [Практические примеры: оптимизация JSON](#практические-примеры-оптимизация-json)
  - [Практические примеры: оптимизация регулярных выражений](#практические-примеры-оптимизация-регулярных-выражений)
  - [Практические примеры: оптимизация работы с базами данных](#практические-примеры-оптимизация-работы-с-базами-данных)
  - [Практические примеры: оптимизация кэширования](#практические-примеры-оптимизация-кэширования)
  - [Измерение производительности: использование benchstat](#измерение-производительности-использование-benchstat)
- [Запуск benchmarks несколько раз](#запуск-benchmarks-несколько-раз)
- [После изменений](#после-изменений)
- [Сравнение результатов](#сравнение-результатов)
  - [Измерение производительности: использование go-torch](#измерение-производительности-использование-go-torch)
- [Установка](#установка)
- [Генерация flame graph](#генерация-flame-graph)
  - [Практические примеры: Оптимизация строковых операций](#практические-примеры-оптимизация-строковых-операций)
  - [Практические примеры: Оптимизация слайсов](#практические-примеры-оптимизация-слайсов)
  - [Практические примеры: Оптимизация map](#практические-примеры-оптимизация-map)
  - [Практические примеры: Оптимизация с использованием sync.Pool](#практические-примеры-оптимизация-с-использованием-syncpool)
  - [Практические примеры: Оптимизация циклов](#практические-примеры-оптимизация-циклов)
  - [Практические примеры: Оптимизация вызовов функций](#практические-примеры-оптимизация-вызовов-функций)
  - [Практические примеры: Оптимизация памяти через выравнивание](#практические-примеры-оптимизация-памяти-через-выравнивание)
  - [Практические примеры: Оптимизация с помощью escape analysis](#практические-примеры-оптимизация-с-помощью-escape-analysis)
  - [Практические примеры: Оптимизация с помощью compiler flags](#практические-примеры-оптимизация-с-помощью-compiler-flags)
- [Оптимизация размера бинарника](#оптимизация-размера-бинарника)
- [Отключение проверок](#отключение-проверок)
- [Оптимизация для конкретной архитектуры](#оптимизация-для-конкретной-архитектуры)
- [Оптимизация с PGO (Profile Guided Optimization)](#оптимизация-с-pgo-profile-guided-optimization)
  - [Практические примеры: Мониторинг производительности в runtime](#практические-примеры-мониторинг-производительности-в-runtime)
  - [Практические примеры: Оптимизация горутин](#практические-примеры-оптимизация-горутин)
  - [Практические примеры: Оптимизация через бенчмаркинг](#практические-примеры-оптимизация-через-бенчмаркинг)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: Оптимизация через escape analysis](#практические-примеры-оптимизация-через-escape-analysis)
  - [Практические примеры: Оптимизация через inlining](#практические-примеры-оптимизация-через-inlining)
  - [Практические примеры: Оптимизация через компилятор](#практические-примеры-оптимизация-через-компилятор)
- [Оптимизация с использованием PGO (Profile Guided Optimization)](#оптимизация-с-использованием-pgo-profile-guided-optimization)
- [Анализ оптимизаций компилятора](#анализ-оптимизаций-компилятора)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в производительность

Оптимизация производительности в Go требует понимания работы **runtime**, сборщика мусора, и инструментов профилирования. Правильная оптимизация позволяет создавать высокопроизводительные приложения.

### Основные аспекты производительности

1. **CPU производительность** — скорость выполнения кода
2. **Память** — использование и управление памятью
3. **Сборка мусора** — влияние `GC` на производительность
4. **Конкурентность** — эффективное использование горутин

## Профилирование

Профилирование позволяет выявить узкие места в коде и оптимизировать их.

### CPU профилирование

```go
import (
    "os"
    "runtime/pprof"
)

func cpuProfile() {
    f, err := os.Create("cpu.prof")
    if err != nil {
        log.Fatal(err)
    }
    defer f.Close()

    if err := pprof.StartCPUProfile(f); err != nil {
        log.Fatal(err)
    }
    defer pprof.StopCPUProfile()

    // Ваш код здесь
}
```

### Memory профилирование

```go
import (
    "os"
    "runtime/pprof"
)

func memoryProfile() {
    f, err := os.Create("mem.prof")
    if err != nil {
        log.Fatal(err)
    }
    defer f.Close()

    // Ваш код здесь

    runtime.GC()
    if err := pprof.WriteHeapProfile(f); err != nil {
        log.Fatal(err)
    }
}
```

### Использование pprof

```bash
# CPU профилирование
go test -cpuprofile=cpu.prof
go tool pprof cpu.prof

# Memory профилирование
go test -memprofile=mem.prof
go tool pprof mem.prof

# HTTP профилирование
import _ "net/http/pprof"
go tool pprof http://localhost:6060/debug/pprof/heap
```

## Оптимизация памяти

Правильное управление памятью критично для производительности.

### Предварительное выделение памяти

```go
// Плохо: множественные перераспределения
var slice []int
for i := 0; i < 1000; i++ {
    slice = append(slice, i)
}

// Хорошо: предварительное выделение
slice := make([]int, 0, 1000)
for i := 0; i < 1000; i++ {
    slice = append(slice, i)
}
```

### Избегание утечек памяти

```go
// Утечка памяти через горутину
func leak() {
    ch := make(chan int)
    go func() {
        for {
            <-ch  // Горутина никогда не завершится
        }
    }()
}

// Исправление: использование context для отмены
func fixed(ctx context.Context) {
    ch := make(chan int)
    go func() {
        for {
            select {
            case <-ctx.Done():
                return
            case <-ch:
                // обработка
            }
        }
    }()
}
```

### Оптимизация структур

```go
// Плохо: неоптимальное выравнивание
type Bad struct {
    a bool    // 1 байт + 7 байт padding
    b int64   // 8 байт
    c bool    // 1 байт + 7 байт padding
}  // Итого: 24 байта

// Хорошо: оптимальное выравнивание
type Good struct {
    b int64   // 8 байт
    a bool    // 1 байт
    c bool    // 1 байт + 6 байт padding
}  // Итого: 16 байт
```

## Сборка мусора

Понимание работы сборщика мусора помогает оптимизировать производительность.

### Настройка `GC`

```bash
# Установка переменных окружения для GC
export GOGC=100  # процент роста перед сборкой мусора
export GODEBUG=gctrace=1  # вывод трассировки GC
```

### Минимизация аллокаций

```go
// Плохо: создание новых объектов в цикле
func process(items []Item) {
    for _, item := range items {
        result := processItem(item)  // новая аллокация
        // использование result
    }
}

// Хорошо: переиспользование объектов
func process(items []Item) {
    var result Result  // переиспользование
    for _, item := range items {
        result.Reset()
        result.Process(item)
        // использование result
    }
}
```

### Использование sync.Pool

```go
import "sync"

var pool = sync.Pool{
    New: func() interface{} {
        return make([]byte, 1024)
    },
}

func process() {
    buf := pool.Get().([]byte)
    defer pool.Put(buf)

    // использование buf
}
```

## Оптимизация кода

### Избегание ненужных копирований

```go
// Плохо: копирование больших структур
func process(user User) {
    // user копируется
}

// Хорошо: передача по указателю
func process(user *User) {
    // передается только указатель
}
```

### Оптимизация строк

```go
import "strings"

// Плохо: конкатенация строк в цикле
var result string
for _, s := range strings {
    result += s  // создает новые строки
}

// Хорошо: использование strings.Builder
var builder strings.Builder
for _, s := range strings {
    builder.WriteString(s)
}
result := builder.String()
```

### Оптимизация циклов

```go
// Плохо: вызов функции в условии цикла
for i := 0; i < len(slice); i++ {
    // len(slice) вызывается на каждой итерации
}

// Хорошо: сохранение длины
length := len(slice)
for i := 0; i < length; i++ {
    // длина вычисляется один раз
}
```

### HTTP профилирование

```go
import _ "net/http/pprof"

func main() {
    // Запуск HTTP сервера для профилирования
    go func() {
        log.Println(http.ListenAndServe("localhost:6060", nil))
    }()

    // Ваш код здесь
}

// Доступ к профилям:
// http://localhost:6060/debug/pprof/
// http://localhost:6060/debug/pprof/heap
// http://localhost:6060/debug/pprof/profile?seconds=30
```

### Анализ CPU профиля

```bash
# Интерактивный режим
go tool pprof http://localhost:6060/debug/pprof/profile?seconds=30

# Топ функций по CPU
(pprof) top

# Граф вызовов
(pprof) web

# Список функций
(pprof) list functionName
```

### Анализ Memory профиля

```bash
# Интерактивный режим
go tool pprof http://localhost:6060/debug/pprof/heap

# Топ по памяти
(pprof) top

# Граф аллокаций
(pprof) web

# Детали функции
(pprof) list functionName
```

### Trace профилирование

```go
import (
    "os"
    "runtime/trace"
)

func traceProfile() {
    f, err := os.Create("trace.out")
    if err != nil {
        log.Fatal(err)
    }
    defer f.Close()

    trace.Start(f)
    defer trace.Stop()

    // Ваш код здесь
}

// Анализ trace
// go tool trace trace.out
```

### Оптимизация срезов: предварительное выделение

```go
// Плохо: множественные перераспределения
func processItems(items []Item) []Result {
    var results []Result
    for _, item := range items {
        result := processItem(item)
        results = append(results, result)  // Перераспределение памяти
    }
    return results
}

// Хорошо: предварительное выделение
func processItems(items []Item) []Result {
    results := make([]Result, 0, len(items))
    for _, item := range items {
        result := processItem(item)
        results = append(results, result)  // Без перераспределения
    }
    return results
}
```

### Оптимизация карт: предварительное выделение

```go
// Плохо: множественные перераспределения
func buildMap(items []Item) map[string]Item {
    m := make(map[string]Item)
    for _, item := range items {
        m[item.Key] = item  // Перераспределение памяти
    }
    return m
}

// Хорошо: предварительное выделение
func buildMap(items []Item) map[string]Item {
    m := make(map[string]Item, len(items))
    for _, item := range items {
        m[item.Key] = item  // Меньше перераспределений
    }
    return m
}
```

### Оптимизация строк: использование strings.Builder

```go
import "strings"

// Плохо: множественные аллокации
func concatenateStrings(strs []string) string {
    var result string
    for _, s := range strs {
        result += s  // Создает новую строку каждый раз
    }
    return result
}

// Хорошо: использование Builder
func concatenateStrings(strs []string) string {
    var builder strings.Builder
    builder.Grow(len(strs) * 10)  // Предварительное выделение

    for _, s := range strs {
        builder.WriteString(s)
    }
    return builder.String()
}
```

### Оптимизация структур: выравнивание полей

```go
// Плохо: неоптимальное выравнивание (24 байта)
type Bad struct {
    a bool    // 1 байт + 7 байт padding
    b int64   // 8 байт
    c bool    // 1 байт + 7 байт padding
}

// Хорошо: оптимальное выравнивание (16 байт)
type Good struct {
    b int64   // 8 байт
    a bool    // 1 байт
    c bool    // 1 байт + 6 байт padding
}

// Проверка размера
fmt.Println(unsafe.Sizeof(Bad{}))   // 24
fmt.Println(unsafe.Sizeof(Good{}))  // 16
```

### Оптимизация структур: использование указателей

```go
// Плохо: копирование больших структур
type LargeStruct struct {
    Data [1000]int
}

func processBad(s LargeStruct) {
    // s копируется (8000 байт)
}

// Хорошо: использование указателей
func processGood(s *LargeStruct) {
    // Передается только указатель (8 байт)
}
```

### Оптимизация циклов: кэширование значений

```go
// Плохо: повторные вычисления
func processBad(slice []int) {
    for i := 0; i < len(slice); i++ {
        // len(slice) вызывается на каждой итерации
        process(slice[i])
    }
}

// Хорошо: кэширование длины
func processGood(slice []int) {
    length := len(slice)
    for i := 0; i < length; i++ {
        process(slice[i])
    }
}

// Еще лучше: использование range
func processBest(slice []int) {
    for _, value := range slice {
        process(value)
    }
}
```

### Оптимизация функций: inline функции

```go
//go:inline
func smallFunction(a, b int) int {
    return a + b
}

// Компилятор может встроить функцию для оптимизации
```

### Оптимизация функций: избегание defer в горячих путях

```go
// Плохо: defer в горячем пути
func processHot(items []Item) {
    for _, item := range items {
        mu.Lock()
        defer mu.Unlock()  // defer создает overhead
        process(item)
    }
}

// Хорошо: явное управление
func processHot(items []Item) {
    for _, item := range items {
        mu.Lock()
        process(item)
        mu.Unlock()  // Нет overhead от defer
    }
}
```

### Оптимизация интерфейсов: избегание лишних интерфейсов

```go
// Плохо: лишние интерфейсы в горячем пути
func processBad(items []interface{}) {
    for _, item := range items {
        process(item)  // Динамическая диспетчеризация
    }
}

// Хорошо: конкретные типы
func processGood(items []Item) {
    for _, item := range items {
        process(item)  // Статическая диспетчеризация
    }
}
```

### Оптимизация памяти: использование sync.Pool

```go
import "sync"

var bufferPool = sync.Pool{
    New: func() interface{} {
        return make([]byte, 0, 1024)
    },
}

func processWithPool(data []byte) {
    buf := bufferPool.Get().([]byte)
    defer bufferPool.Put(buf)

    // Использование буфера
    buf = append(buf, data...)
    process(buf)

    // Буфер возвращается в пул
}
```

### Оптимизация памяти: переиспользование слайсов

```go
func processItems(items []Item) {
    // Переиспользование слайса
    temp := make([]int, 0, 100)

    for _, item := range items {
        temp = temp[:0]  // Очистка без перераспределения
        temp = processItem(item, temp)
        // Использование temp
    }
}
```

### Оптимизация `GC`: минимизация аллокаций

```go
// Плохо: множество аллокаций
func processBad(items []Item) []Result {
    var results []Result
    for _, item := range items {
        result := Result{  // Новая аллокация
            ID:   item.ID,
            Data: processData(item.Data),
        }
        results = append(results, result)
    }
    return results
}

// Хорошо: переиспользование структуры
func processGood(items []Item) []Result {
    results := make([]Result, 0, len(items))
    var result Result  // Переиспользование

    for _, item := range items {
        result.ID = item.ID
        result.Data = processData(item.Data)
        results = append(results, result)
    }
    return results
}
```

### Оптимизация `GC`: настройка GOGC

```bash
# Уменьшение частоты сборки мусора (больше памяти, меньше GC)
export GOGC=200

# Увеличение частоты сборки мусора (меньше памяти, больше GC)
export GOGC=50

# Отключение GC (только для тестирования!)
export GOGC=off
```

### Оптимизация `GC`: анализ `GC`

```bash
# Включение трассировки GC
export GODEBUG=gctrace=1

# Запуск приложения
./myapp

# Вывод будет содержать информацию о GC:
# gc 1 @0.001s 2%: 0.010+0.20+0.003 ms clock, 0.040+0.20/0.20/0+0.012 ms cpu, 4->4->0 MB, 5 MB goal, 4 P
```

### Оптимизация конкурентности: правильное количество горутин

```go
// Плохо: слишком много горутин
func processBad(items []Item) {
    for _, item := range items {
        go processItem(item)  // Может создать тысячи горутин
    }
}

// Хорошо: ограничение количества горутин
func processGood(items []Item) {
    sem := make(chan struct{}, 10)  // Максимум 10 горутин
    var wg sync.WaitGroup

    for _, item := range items {
        wg.Add(1)
        sem <- struct{}{}  // Acquire

        go func(it Item) {
            defer func() {
                <-sem  // Release
                wg.Done()
            }()
            processItem(it)
        }(item)
    }

    wg.Wait()
}
```

### Оптимизация конкурентности: worker pools

```go
func processWithWorkers(items []Item, numWorkers int) []Result {
    jobs := make(chan Item, len(items))
    results := make(chan Result, len(items))

    // Заполнение jobs
    for _, item := range items {
        jobs <- item
    }
    close(jobs)

    // Запуск воркеров
    var wg sync.WaitGroup
    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for item := range jobs {
                results <- processItem(item)
            }
        }()
    }

    // Ожидание завершения
    go func() {
        wg.Wait()
        close(results)
    }()

    // Сбор результатов
    var allResults []Result
    for result := range results {
        allResults = append(allResults, result)
    }

    return allResults
}
```

### Оптимизация I/O: буферизация

```go
import "bufio"

// Плохо: небуферизованное чтение
func readBad(file *os.File) error {
    data := make([]byte, 1)
    for {
        _, err := file.Read(data)
        if err != nil {
            return err
        }
        process(data)
    }
}

// Хорошо: буферизованное чтение
func readGood(file *os.File) error {
    reader := bufio.NewReader(file)
    buffer := make([]byte, 4096)

    for {
        n, err := reader.Read(buffer)
        if err != nil {
            return err
        }
        process(buffer[:n])
    }
}
```

### Оптимизация I/O: параллельное чтение

```go
func readParallel(files []string) error {
    var wg sync.WaitGroup
    errCh := make(chan error, len(files))

    for _, filename := range files {
        wg.Add(1)
        go func(fn string) {
            defer wg.Done()
            if err := readFile(fn); err != nil {
                errCh <- err
            }
        }(filename)
    }

    wg.Wait()
    close(errCh)

    for err := range errCh {
        if err != nil {
            return err
        }
    }

    return nil
}
```

### Практические примеры: оптимизация HTTP сервера

```go
func optimizedHTTPServer() *http.Server {
    return &http.Server{
        Addr:         ":8080",
        ReadTimeout:  15 * time.Second,
        WriteTimeout: 15 * time.Second,
        IdleTimeout:  60 * time.Second,
        MaxHeaderBytes: 1 << 20,  // 1 MB
    }
}
```

### Практические примеры: оптимизация HTTP клиента

```go
func optimizedHTTPClient() *http.Client {
    transport := &http.Transport{
        MaxIdleConns:        100,
        MaxIdleConnsPerHost: 10,
        IdleConnTimeout:     90 * time.Second,
        TLSHandshakeTimeout: 10 * time.Second,
        DisableCompression:  false,
    }

    return &http.Client{
        Timeout:   10 * time.Second,
        Transport: transport,
    }
}
```

### Практические примеры: оптимизация JSON

```go
import "encoding/json"

// Использование json.Encoder для потоковой записи
func writeJSONStream(w io.Writer, items []Item) error {
    encoder := json.NewEncoder(w)
    for _, item := range items {
        if err := encoder.Encode(item); err != nil {
            return err
        }
    }
    return nil
}

// Использование json.Decoder для потокового чтения
func readJSONStream(r io.Reader) ([]Item, error) {
    decoder := json.NewDecoder(r)
    var items []Item

    for decoder.More() {
        var item Item
        if err := decoder.Decode(&item); err != nil {
            return nil, err
        }
        items = append(items, item)
    }

    return items, nil
}
```

### Практические примеры: оптимизация регулярных выражений

```go
import "regexp"

// Плохо: компиляция на каждой итерации
func processBad(texts []string) {
    for _, text := range texts {
        matched, _ := regexp.MatchString(`\d+`, text)  // Компиляция каждый раз
        if matched {
            process(text)
        }
    }
}

// Хорошо: предварительная компиляция
func processGood(texts []string) {
    re := regexp.MustCompile(`\d+`)  // Компиляция один раз

    for _, text := range texts {
        if re.MatchString(text) {
            process(text)
        }
    }
}
```

### Практические примеры: оптимизация работы с базами данных

```go
// Использование prepared statements
func optimizedQuery(db *sql.DB) error {
    stmt, err := db.Prepare("SELECT * FROM users WHERE id = $1")
    if err != nil {
        return err
    }
    defer stmt.Close()

    // Переиспользование prepared statement
    for i := 1; i <= 1000; i++ {
        var user User
        err := stmt.QueryRow(i).Scan(&user.ID, &user.Name)
        if err != nil {
            return err
        }
    }

    return nil
}
```

### Практические примеры: оптимизация кэширования

```go
type Cache struct {
    data map[string]CacheEntry
    mu   sync.RWMutex
}

type CacheEntry struct {
    Value     interface{}
    ExpiresAt time.Time
}

func (c *Cache) Get(key string) (interface{}, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()

    entry, ok := c.data[key]
    if !ok || time.Now().After(entry.ExpiresAt) {
        return nil, false
    }

    return entry.Value, true
}

func (c *Cache) Set(key string, value interface{}, ttl time.Duration) {
    c.mu.Lock()
    defer c.mu.Unlock()

    c.data[key] = CacheEntry{
        Value:     value,
        ExpiresAt: time.Now().Add(ttl),
    }
}
```

### Измерение производительности: использование benchstat

```bash
# Запуск benchmarks несколько раз
go test -bench=. -count=5 > old.txt

# После изменений
go test -bench=. -count=5 > new.txt

# Сравнение результатов
benchstat old.txt new.txt
```

### Измерение производительности: использование go-torch

```bash
# Установка
go get github.com/uber/go-torch

# Генерация flame graph
go-torch -u http://localhost:6060 -p
```

### Практические примеры: Оптимизация строковых операций

```go
// Плохо: множественная конкатенация
func buildStringBad(items []string) string {
    result := ""
    for _, item := range items {
        result += item + ","
    }
    return result
}

// Хорошо: использование strings.Builder
func buildStringGood(items []string) string {
    var builder strings.Builder
    builder.Grow(len(items) * 10) // Предварительное выделение

    for i, item := range items {
        if i > 0 {
            builder.WriteString(",")
        }
        builder.WriteString(item)
    }
    return builder.String()
}

// Еще лучше: использование strings.Join
func buildStringBest(items []string) string {
    return strings.Join(items, ",")
}
```

### Практические примеры: Оптимизация слайсов

```go
// Плохо: неизвестный размер
func processItemsBad(items []Item) []Result {
    var results []Result
    for _, item := range items {
        results = append(results, processItem(item))
    }
    return results
}

// Хорошо: предварительное выделение
func processItemsGood(items []Item) []Result {
    results := make([]Result, 0, len(items))
    for _, item := range items {
        results = append(results, processItem(item))
    }
    return results
}

// Если размер точно известен
func processItemsBest(items []Item) []Result {
    results := make([]Result, len(items))
    for i, item := range items {
        results[i] = processItem(item)
    }
    return results
}
```

### Практические примеры: Оптимизация map

```go
// Предварительное выделение для map
func buildMapGood(keys []string, values []string) map[string]string {
    result := make(map[string]string, len(keys))
    for i, key := range keys {
        result[key] = values[i]
    }
    return result
}

// Проверка существования перед записью
func updateMapSafe(m map[string]int, key string, value int) {
    if _, exists := m[key]; exists {
        m[key] = value
    } else {
        m[key] = value
    }
}
```

### Практические примеры: Оптимизация с использованием sync.Pool

```go
var bufferPool = sync.Pool{
    New: func() interface{} {
        return make([]byte, 0, 1024)
    },
}

func processWithPool(data []byte) []byte {
    buffer := bufferPool.Get().([]byte)
    defer bufferPool.Put(buffer[:0]) // Сброс для переиспользования

    buffer = append(buffer, data...)
    // Обработка...

    result := make([]byte, len(buffer))
    copy(result, buffer)
    return result
}

var requestPool = sync.Pool{
    New: func() interface{} {
        return &Request{
            Headers: make(map[string]string),
            Body:    make([]byte, 0, 512),
        }
    },
}

func handleRequest(data []byte) {
    req := requestPool.Get().(*Request)
    defer func() {
        req.Reset()
        requestPool.Put(req)
    }()

    // Использование запроса
    req.Parse(data)
    process(req)
}
```

### Практические примеры: Оптимизация циклов

```go
// Плохо: вызов len() в каждой итерации
func sumBad(items []int) int {
    total := 0
    for i := 0; i < len(items); i++ {
        total += items[i]
    }
    return total
}

// Хорошо: кэширование длины
func sumGood(items []int) int {
    total := 0
    n := len(items)
    for i := 0; i < n; i++ {
        total += items[i]
    }
    return total
}

// Еще лучше: range для простоты
func sumBest(items []int) int {
    total := 0
    for _, item := range items {
        total += item
    }
    return total
}

// Для оптимизации: unroll циклов для маленьких размеров
func sumOptimized(items []int) int {
    total := 0
    i := 0
    n := len(items)

    // Unroll для первых n-4 элементов
    for i < n-4 {
        total += items[i] + items[i+1] + items[i+2] + items[i+3]
        i += 4
    }

    // Обработка оставшихся
    for i < n {
        total += items[i]
        i++
    }

    return total
}
```

### Практические примеры: Оптимизация вызовов функций

```go
// Избегание лишних вызовов в циклах
func processItems(items []Item) {
    // Кэширование результатов функций
    processor := getProcessor()

    for _, item := range items {
        processor.Process(item) // Вместо getProcessor().Process(item)
    }
}

// Inline маленьких функций
// Вместо:
func add(a, b int) int {
    return a + b
}

// Используйте напрямую:
result := a + b

// Использование указателей для больших структур
func processLargeStruct(s *LargeStruct) {
    // Изменение вместо копирования
    s.Field = newValue
}
```

### Практические примеры: Оптимизация памяти через выравнивание

```go
// Плохо: неоптимальное выравнивание (24 байта)
type Bad struct {
    a bool    // 1 байт + 7 байт padding
    b int64   // 8 байт
    c bool    // 1 байт + 7 байт padding
}

// Хорошо: оптимизированное выравнивание (16 байт)
type Good struct {
    b int64   // 8 байт
    a bool    // 1 байт
    c bool    // 1 байт + 6 байт padding
}

// Проверка размера структуры
func checkStructSize() {
    var bad Bad
    var good Good
    fmt.Printf("Bad size: %d\n", unsafe.Sizeof(bad))
    fmt.Printf("Good size: %d\n", unsafe.Sizeof(good))
}
```

### Практические примеры: Оптимизация с помощью escape analysis

```go
// Структуры, которые не escape на heap
func processLocal() {
    data := make([]byte, 1024) // Может быть на stack
    // Использование data
}

// Избегание escape на heap
func processWithPointer(data []byte) {
    // Использование указателя может заставить escape
    // Используйте значения когда возможно
}

// Оптимизация через inline функций
//go:noinline
func noInlineFunction() {
    // Предотвращает inline для тестирования
}

//go:inline
func inlineFunction() {
    // Подсказка компилятору для inline
}
```

### Практические примеры: Оптимизация с помощью compiler flags

```bash
# Оптимизация размера бинарника
go build -ldflags="-s -w" main.go

# Отключение проверок
go build -gcflags="-N -l" main.go

# Оптимизация для конкретной архитектуры
GOARCH=amd64 GOOS=linux go build main.go

# Оптимизация с PGO (Profile Guided Optimization)
go build -pgo=default.pgo main.go
```

### Практические примеры: Мониторинг производительности в runtime

```go
import "runtime"

func monitorPerformance() {
    var m runtime.MemStats
    runtime.ReadMemStats(&m)

    fmt.Printf("Alloc = %v MiB", bToMb(m.Alloc))
    fmt.Printf("\tTotalAlloc = %v MiB", bToMb(m.TotalAlloc))
    fmt.Printf("\tSys = %v MiB", bToMb(m.Sys))
    fmt.Printf("\tNumGC = %v\n", m.NumGC)

    fmt.Printf("HeapAlloc = %v MiB", bToMb(m.HeapAlloc))
    fmt.Printf("\tHeapSys = %v MiB", bToMb(m.HeapSys))
    fmt.Printf("\tHeapInuse = %v MiB", bToMb(m.HeapInuse))
}

func bToMb(b uint64) uint64 {
    return b / 1024 / 1024
}

// Принудительная сборка мусора (только для тестирования)
func forceGC() {
    runtime.GC()
    runtime.GC() // Дважды для полной очистки
}
```

### Практические примеры: Оптимизация горутин

```go
// Ограничение количества горутин
func processWithLimiter(items []Item, maxConcurrency int) error {
    sem := make(chan struct{}, maxConcurrency)
    errCh := make(chan error, len(items))

    for _, item := range items {
        sem <- struct{}{} // Acquire

        go func(it Item) {
            defer func() { <-sem }() // Release
            errCh <- processItem(it)
        }(item)
    }

    for i := 0; i < len(items); i++ {
        if err := <-errCh; err != nil {
            return err
        }
    }

    return nil
}

// Переиспользование горутин через worker pool
func processWithWorkerPool(items []Item, numWorkers int) error {
    jobs := make(chan Item, len(items))
    errCh := make(chan error, len(items))

    // Запуск воркеров
    for i := 0; i < numWorkers; i++ {
        go func() {
            for item := range jobs {
                errCh <- processItem(item)
            }
        }()
    }

    // Отправка заданий
    for _, item := range items {
        jobs <- item
    }
    close(jobs)

    // Сбор результатов
    for i := 0; i < len(items); i++ {
        if err := <-errCh; err != nil {
            return err
        }
    }

    return nil
}
```

### Практические примеры: Оптимизация через бенчмаркинг

```go
func BenchmarkStringConcat(b *testing.B) {
    items := []string{"a", "b", "c", "d", "e"}

    b.Run("concat", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            result := ""
            for _, item := range items {
                result += item
            }
            _ = result
        }
    })

    b.Run("builder", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            var builder strings.Builder
            for _, item := range items {
                builder.WriteString(item)
            }
            _ = builder.String()
        }
    })

    b.Run("join", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            _ = strings.Join(items, "")
        }
    })
}

func BenchmarkMemoryAllocation(b *testing.B) {
    b.ReportAllocs()

    b.Run("no preallocate", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            var slice []int
            for j := 0; j < 1000; j++ {
                slice = append(slice, j)
            }
        }
    })

    b.Run("preallocate", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            slice := make([]int, 0, 1000)
            for j := 0; j < 1000; j++ {
                slice = append(slice, j)
            }
        }
    })
}
```

## Лучшие практики

1. **Измеряйте перед оптимизацией** — используйте профилирование для выявления узких мест
2. **Предварительно выделяйте память** — используйте **make** с указанием емкости
3. **Избегайте утечек памяти** — правильно управляйте жизненным циклом объектов
4. **Оптимизируйте структуры** — учитывайте выравнивание полей
5. **Используйте sync.Pool** — для переиспользования объектов
6. **Минимизируйте аллокации** — переиспользуйте объекты где возможно
7. **Оптимизируйте строки** — используйте **strings.Builder** для конкатенации
8. **Избегайте defer в горячих путях** — используйте явное управление
9. **Используйте конкретные типы** — избегайте лишних интерфейсов в горячих путях
10. **Настраивайте GC** — используйте **GOGC** для баланса памяти и производительности
11. **Используйте бенчмарки** — измеряйте производительность изменений
12. **Мониторьте память** — отслеживайте использование памяти
13. **Оптимизируйте циклы** — кэшируйте значения и используйте **range**
14. **Избегайте лишних копий** — используйте указатели для больших структур
15. **Используйте worker pools** — ограничивайте количество горутин

### Практические примеры: Оптимизация через escape analysis

```go
// Проверка escape analysis
//go:noinline
func NoEscapeExample() {
    data := make([]byte, 1024)
    // data не escape на heap
    processLocal(data)
}

//go:noinline
func EscapeExample() {
    data := make([]byte, 1024)
    // data escape на heap из-за передачи в функцию
    processPointer(&data)
}

func processLocal(data []byte) {
    // Локальная обработка
}

func processPointer(data *[]byte) {
    // Обработка через указатель
}

// Анализ escape analysis
// go build -gcflags="-m" main.go
```

### Практические примеры: Оптимизация через inlining

```go
// Подсказка компилятору для inline
//go:inline
func InlineFunction(a, b int) int {
    return a + b
}

// Предотвращение inline для тестирования
//go:noinline
func NoInlineFunction(a, b int) int {
    return a * b
}

// Проверка inlining
// go build -gcflags="-m -m" main.go
```

### Практические примеры: Оптимизация через компилятор

```bash
# Оптимизация размера бинарника
go build -ldflags="-s -w" -o app .

# Оптимизация для конкретной архитектуры
GOARCH=amd64 GOOS=linux go build -o app .

# Оптимизация с использованием PGO (Profile Guided Optimization)
go build -pgo=default.pgo -o app .

# Анализ оптимизаций компилятора
go build -gcflags="-S" main.go
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Оптимизация производительности в Go требует понимания работы **runtime**, сборщика мусора, инструментов профилирования, **escape analysis**, **inlining**, компиляторных оптимизаций и практических техник оптимизации. Правильное использование профилирования, оптимизация памяти, понимание работы `GC`, оптимизация кода, анализ **escape** и систематический подход критичны для создания высокопроизводительных приложений. Правильные практики оптимизации позволяют создавать приложения, которые эффективно используют ресурсы, минимизируют аллокации, максимизируют использование **stack** памяти и обеспечивают высокую производительность даже под нагрузкой.

## Дополнительные ресурсы

- [Go Performance Tips](https://dave.cheney.net/high-performance-go-workshop/dotgo-paris.html)
- [Go Profiling](https://go.dev/blog/pprof)
- [Go Memory Model](https://go.dev/ref/mem)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
