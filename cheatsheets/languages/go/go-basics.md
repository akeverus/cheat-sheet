---
title: "Go: основы"
description: "Полное руководство по языку Go: базовый синтаксис, типы данных, функции, структуры, интерфейсы, конкурентность, HTTP, JSON, тестирование, best practices"
tags:
  - go
  - golang
  - language
  - concurrency
  - web
  - backend
difficulty: "beginner"
prerequisites: []
next: ["go/go-advanced-patterns.md"]
updated: "2026-02-11"
---

# Go: основы

**Go** (или **Golang**) — это компилируемый, статически типизированный язык программирования с открытым исходным кодом, разработанный в **Google**. Go сочетает простоту синтаксиса, высокую производительность и встроенную поддержку конкурентности, что делает его идеальным для создания надежных и масштабируемых приложений.

## Полезные ссылки

### Официальная документация

- [Go Documentation](https://go.dev/doc/)
- [Go Packages](https://pkg.go.dev/std)
- [A Tour of Go](https://go.dev/tour/)
- [Effective Go](https://go.dev/doc/effective_go)

### См. также

- [[go-modules|`go-modules.md`]] — практики модулей и dependency management
- [[go-concurrency|`go-concurrency.md`]] — конкурентность в production
- [[go-testing|`go-testing.md`]] — unit/integration/load тестирование
- [[go-observability|`go-observability.md`]] — метрики, логи и трассировка

### Практика миграции Go 1.19 -> 1.22+

Короткий production-чеклист:
1. Обновить toolchain в CI/CD и зафиксировать версию Go в окружениях.
2. Прогнать `go test ./...` и smoke-набор на целевых сервисах.
3. Перепроверить `go.mod`/`go.sum`, удалить неиспользуемые зависимости.
4. Сверить behavior performance-критичных участков (`allocs/op`, latency, GC).
5. Проверить race-сценарии (`go test -race`) и корректность graceful shutdown.

Anti-pattern: обновить только локальную версию Go у разработчиков, но оставить старый CI toolchain.

## Содержание

- [Введение в Go](#введение-в-go)
  - [Основные характеристики Go:](#основные-характеристики-go)
  - [Преимущества Go:](#преимущества-go)
- [Установка и настройка](#установка-и-настройка)
  - [Установка Go](#установка-go)
  - [Проверка установки](#проверка-установки)
  - [Настройка окружения](#настройка-окружения)
  - [Создание первого проекта](#создание-первого-проекта)
- [Базовый синтаксис](#базовый-синтаксис)
  - [Структура программы](#структура-программы)
  - [Комментарии](#комментарии)
  - [Форматирование кода](#форматирование-кода)
- [Типы данных](#типы-данных)
  - [Базовые типы](#базовые-типы)
  - [Составные типы](#составные-типы)
- [Переменные и константы](#переменные-и-константы)
  - [Объявление переменных](#объявление-переменных)
  - [Нулевые значения](#нулевые-значения)
  - [Константы](#константы)
- [Функции](#функции)
  - [Определение функций](#определение-функций)
  - [Примеры функций](#примеры-функций)
  - [Анонимные функции и замыкания](#анонимные-функции-и-замыкания)
- [Структуры (Structs)](#структуры-structs)
  - [Вложенные структуры](#вложенные-структуры)
  - [Анонимные структуры](#анонимные-структуры)
- [Интерфейсы (Interfaces)](#интерфейсы-interfaces)
  - [Пустой интерфейс](#пустой-интерфейс)
- [Пакеты (Packages)](#пакеты-packages)
  - [Экспорт](#экспорт)
- [Модули (Modules)](#модули-modules)
  - [Создание модуля](#создание-модуля)
  - [Добавление зависимостей](#добавление-зависимостей)
  - [Версионирование](#версионирование)
- [Указатели (Pointers)](#указатели-pointers)
  - [Указатели в функциях](#указатели-в-функциях)
  - [Указатели на структуры](#указатели-на-структуры)
- [Методы (Methods)](#методы-methods)
  - [Когда использовать pointer receiver:](#когда-использовать-pointer-receiver)
- [Управление ошибками](#управление-ошибками)
  - [Кастомные ошибки](#кастомные-ошибки)
  - [Проверка типов ошибок](#проверка-типов-ошибок)
- [Горутины (Goroutines)](#горутины-goroutines)
  - [Основные концепции горутин:](#основные-концепции-горутин)
  - [Запуск горутин:](#запуск-горутин)
  - [Ожидание завершения горутин:](#ожидание-завершения-горутин)
- [Каналы (Channels)](#каналы-channels)
  - [Типы каналов:](#типы-каналов)
  - [Select statement:](#select-statement)
  - [Паттерны работы с каналами:](#паттерны-работы-с-каналами)
- [Синхронизация](#синхронизация)
  - [Mutex:](#mutex)
  - [RWMutex:](#rwmutex)
  - [WaitGroup:](#waitgroup)
  - [Once:](#once)
  - [Cond:](#cond)
- [Context](#context)
  - [Типы контекстов:](#типы-контекстов)
- [JSON](#json)
  - [Теги JSON:](#теги-json)
  - [Кастомный маршалинг:](#кастомный-маршалинг)
- [Работа с файлами](#работа-с-файлами)
  - [Чтение файлов:](#чтение-файлов)
  - [Запись в файлы:](#запись-в-файлы)
  - [Работа с директориями:](#работа-с-директориями)
- [Базы данных](#базы-данных)
  - [PostgreSQL:](#postgresql)
  - [Connection pooling:](#connection-pooling)
- [HTTP клиент и сервер](#http-клиент-и-сервер)
  - [HTTP сервер:](#http-сервер)
  - [HTTP клиент:](#http-клиент)
  - [REST API с Gin:](#rest-api-с-gin)
- [Generics (Go 1.18+)](#generics-go-118)
  - [Сложные constraints:](#сложные-constraints)
- [Рефлексия (Reflection)](#рефлексия-reflection)
  - [Практическое применение рефлексии:](#практическое-применение-рефлексии)
- [Unsafe операции](#unsafe-операции)
- [Тестирование](#тестирование)
  - [Запуск тестов:](#запуск-тестов)
  - [Тесты с зависимостями:](#тесты-с-зависимостями)
  - [HTTP тестирование:](#http-тестирование)
- [Benchmarking](#benchmarking)
  - [Анализ результатов:](#анализ-результатов)
- [Профилирование](#профилирование)
  - [Анализ профилей:](#анализ-профилей)
- [Отладка](#отладка)
  - [Delve debugger:](#delve-debugger)
  - [Логирование:](#логирование)
- [Лучшие практики](#лучшие-практики)
  - [Стиль кода:](#стиль-кода)
  - [Производительность:](#производительность)
- [Распространенные паттерны](#распространенные-паттерны)
  - [Singleton:](#singleton)
  - [Factory:](#factory)
  - [Decorator:](#decorator)
  - [Worker Pool:](#worker-pool)
  - [Dependency Injection:](#dependency-injection)
- [Производительность](#производительность)
  - [Оптимизация памяти:](#оптимизация-памяти)
  - [Оптимизация CPU:](#оптимизация-cpu)
  - [Профилирование производительности:](#профилирование-производительности)
- [Безопасность](#безопасность)
  - [Валидация ввода:](#валидация-ввода)
  - [Безопасность горутин:](#безопасность-горутин)
  - [Защита от race conditions:](#защита-от-race-conditions)
  - [HTTPS и TLS:](#https-и-tls)
- [Дополнительные темы](#дополнительные-темы)
  - [Практический навигатор](#практический-навигатор)
  - [Что изучать в этом файле, а что вынести в профильные документы](#что-изучать-в-этом-файле-а-что-вынести-в-профильные-документы)
  - [Расширенная справка (legacy)](#расширенная-справка-legacy)
- [Продвинутые техники и паттерны](#продвинутые-техники-и-паттерны)
  - [Каналы для координации](#каналы-для-координации)
  - [Оптимизация производительности](#оптимизация-производительности)
  - [Проектирование API](#проектирование-api)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Введение в Go

**Go** (также известный как **Golang**) — это язык программирования с открытым исходным кодом, разработанный в **Google**. Go сочетает простоту синтаксиса с производительностью компилируемого языка.

### Основные характеристики Go:

1. **Статическая типизация**: Типы проверяются во время компиляции
2. **Компилируемый язык**: Код компилируется в нативный машинный код
3. **Сборка мусора**: Автоматическое управление памятью
4. **Параллелизм**: Встроенная поддержка горутин (goroutines) и каналов (channels)
5. **Простота**: Минималистичный синтаксис, быстрая компиляция
6. **Быстрая компиляция**: Компиляция происходит очень быстро

### Преимущества Go:

- **Производительность**: Скомпилированный код работает быстро
- **Простота**: Легко изучать и поддерживать
- **Параллелизм**: Удобная работа с параллельными задачами
- **Стандартная библиотека**: Богатая стандартная библиотека
- **Статическая линковка**: Один исполняемый файл, нет зависимостей

## Установка и настройка

### Установка Go

**Go можно установить несколькими способами:**

1. **С официального сайта**: [go.dev/dl](https://go.dev/dl/)
2. **Через пакетный менеджер** (Homebrew, apt, yum и др.)
3. **Использование версионных менеджеров** (g, gvm)

### Проверка установки

```bash
# Проверка версии Go
go version
```

### Настройка окружения

**Переменные окружения Go:**

- `GOROOT` — путь к установке Go (обычно устанавливается автоматически)
- `GOPATH` — рабочее пространство для кода (устарело в `Go 1.11+`)
- `GOBIN` — директория для исполняемых файлов

### Создание первого проекта

```bash
# Создание проекта и инициализация модуля
mkdir hello-world
cd hello-world
go mod init hello-world
```

**Создайте файл `main.go`:**

```go
// Точка входа: package main и функция main
package main

import "fmt"

func main() {
    fmt.Println("Hello, World!")
}
```

**Запуск:**

```bash
# Запуск без сборки бинарника
go run main.go
```

**Или компиляция:**

```bash
# Сборка бинарника и запуск
go build
./hello-world  # или hello-world.exe на Windows
```

## Базовый синтаксис

### Структура программы

**Каждая программа на Go начинается с объявления пакета:**

```go
package main  // пакет main - точка входа программы

import "fmt"  // импорт стандартной библиотеки

func main() {  // функция main - точка входа
    fmt.Println("Hello, World!")
}
```

### Комментарии

```go
// Однострочный комментарий

/*
 Многострочный
 комментарий
*/

// Документирующий комментарий (начинается с имени)
// PrintHello выводит приветствие на экран
func PrintHello() {
    fmt.Println("Hello")
}
```

### Форматирование кода

**Go имеет встроенный инструмент форматирования:**

```bash
go fmt ./...  # форматирует весь код в проекте
```

**Или используйте `gofmt`:**

```bash
gofmt -w main.go  # форматирует и сохраняет файл
```

## Типы данных

### Базовые типы

#### Числовые типы

**Целочисленные:**
- `int`, `int8`, `int16`, `int32`, `int64` — знаковые целые
- `uint`, `uint8`, `uint16`, `uint32`, `uint64`, `uintptr` — беззнаковые целые
- `byte` — псевдоним для `uint8`
- `rune` — псевдоним для `int32` (Unicode кодпоинт)

**С плавающей точкой:**
- `float32`, `float64` — числа с плавающей точкой

**Комплексные:**
- `complex64`, `complex128` — комплексные числа

```go
// Объявление переменных с явным типом
var i int = 42
var f float64 = 3.14
var c complex128 = 1 + 2i
```

#### Строки

```go
// Строки и raw-строки (обратные кавычки)
var s string = "Hello, Go!"
var multiline = `Многострочная
строка
в обратных кавычках`
```

#### Булевы значения

```go
// Логический тип bool
var b bool = true
var isActive bool = false
```

### Составные типы

#### Массивы (Arrays)

**Массив — это последовательность элементов фиксированной длины:**

```go
var arr [5]int                    // массив из 5 целых чисел
var arr2 [3]string = [3]string{"a", "b", "c"}  // инициализация
arr3 := [3]int{1, 2, 3}          // короткая запись
arr4 := [...]int{1, 2, 3}        // длина определяется автоматически
```

#### Срезы (Slices)

**Срез — это динамический массив:**

```go
var s []int                       // nil срез
s := []int{1, 2, 3}              // инициализация
s := make([]int, 5)              // создание среза длиной 5
s := make([]int, 5, 10)          // длина 5, емкость 10

// Операции со срезами
s = append(s, 4)                 // добавление элемента
s = append(s, 5, 6, 7)           // добавление нескольких элементов
sub := s[1:3]                    // подсрез (индексы 1, 2)
```

#### Карты (Maps)

**Карта — это ассоциативный массив (хэш-таблица):**

```go
var m map[string]int              // nil карта
m := make(map[string]int)         // создание карты
m := map[string]int{              // инициализация
    "one": 1,
    "two": 2,
}

// Операции с картами
m["three"] = 3                    // добавление/изменение
value := m["one"]                 // получение значения
value, ok := m["four"]            // проверка существования
delete(m, "two")                  // удаление элемента
```

#### Каналы (Channels)

**Каналы используются для связи между горутинами:**

```go
ch := make(chan int)              // небуферизованный канал
ch := make(chan int, 10)          // буферизованный канал (емкость 10)

ch <- 42                          // отправка значения
value := <-ch                     // получение значения
close(ch)                         // закрытие канала
```

## Переменные и константы

### Объявление переменных

```go
// Полная форма
var name string = "Go"

// С выводом типа
var name = "Go"

// Короткая запись (только внутри функций)
name := "Go"

// Множественное объявление
var (
    name string = "Go"
    age  int    = 10
)

// Множественное присваивание
x, y := 1, 2
x, y = y, x  // обмен значений
```

### Нулевые значения

**В Go все переменные инициализируются нулевыми значениями:**

- Числа: `0`
- Строки: `""`
- Булевы: `false`
- Указатели, срезы, карты, каналы, функции, интерфейсы: `nil`

```go
var i int      // 0
var s string   // ""
var b bool     // false
var p *int     // nil
```

### Константы

```go
const Pi = 3.14159
const (
    StatusOK    = 200
    StatusError = 500
)

// iota - автоматическая нумерация
const (
    Monday = iota  // 0
    Tuesday        // 1
    Wednesday      // 2
)
```

## Функции

### Определение функций

```go
// Синтаксис объявления функции: имя, параметры, возвращаемый тип
func functionName(param1 type1, param2 type2) returnType {
    // тело функции
    return value
}
```

### Примеры функций

```go
// Простая функция
func add(a int, b int) int {
    return a + b
}

// Сокращенная форма параметров одного типа
func multiply(a, b int) int {
    return a * b
}

// Несколько возвращаемых значений
func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, fmt.Errorf("division by zero")
    }
    return a / b, nil
}

// Именованные возвращаемые значения
func swap(a, b int) (x, y int) {
    x = b
    y = a
    return  // автоматически возвращает x, y
}

// Вариативные функции
func sum(numbers ...int) int {
    total := 0
    for _, num := range numbers {
        total += num
    }
    return total
}
```

### Анонимные функции и замыкания

```go
// Анонимная функция
func() {
    fmt.Println("Anonymous function")
}()

// Присваивание функции переменной
add := func(a, b int) int {
    return a + b
}

result := add(3, 4)

// Замыкание
func counter() func() int {
    count := 0
    return func() int {
        count++
        return count
    }
}

c := counter()
fmt.Println(c())  // 1
fmt.Println(c())  // 2
```

## Структуры (Structs)

**Структура — это тип данных, который группирует связанные поля:**

```go
type Person struct {
    Name string
    Age  int
    City string
}

// Создание структуры
p1 := Person{"Alice", 30, "New York"}           // позиционная инициализация
p2 := Person{Name: "Bob", Age: 25, City: "LA"}  // именованная инициализация
p3 := Person{Name: "Charlie"}                   // остальные поля - нулевые значения

// Доступ к полям
fmt.Println(p1.Name)  // Alice
p1.Age = 31
```

### Вложенные структуры

```go
type Address struct {
    Street string
    City   string
}

type Person struct {
    Name    string
    Address Address
}

p := Person{
    Name: "Alice",
    Address: Address{
        Street: "123 Main St",
        City:   "New York",
    },
}
```

### Анонимные структуры

```go
person := struct {
    Name string
    Age  int
}{
    Name: "Alice",
    Age:  30,
}
```

## Интерфейсы (Interfaces)

**Интерфейс определяет набор методов, которые должен реализовать тип:**

```go
type Shape interface {
    Area() float64
    Perimeter() float64
}

type Rectangle struct {
    Width  float64
    Height float64
}

func (r Rectangle) Area() float64 {
    return r.Width * r.Height
}

func (r Rectangle) Perimeter() float64 {
    return 2 * (r.Width + r.Height)
}

// Rectangle реализует интерфейс Shape
var s Shape = Rectangle{Width: 10, Height: 5}
fmt.Println(s.Area())  // 50
```

### Пустой интерфейс

**Пустой интерфейс `**interface**{}` может представлять любой тип:**

```go
// Пустой интерфейс interface{} принимает любое значение
var i interface{}
i = 42
i = "hello"
i = []int{1, 2, 3}

// Проверка типа (type assertion)
if str, ok := i.(string); ok {
    fmt.Println(str)
}

// Type switch
switch v := i.(type) {
case int:
    fmt.Printf("Integer: %d\n", v)
case string:
    fmt.Printf("String: %s\n", v)
default:
    fmt.Printf("Unknown type\n")
}
```

## Пакеты (Packages)

**Пакет — это способ организации кода. Каждый файл Go принадлежит пакету:**

```go
package mypackage  // объявление пакета

import (
    "fmt"           // стандартная библиотека
    "math"

    "example.com/mypackage/subpackage"  // внешний пакет
    alias "example.com/otherpackage"    // импорт с алиасом
    _ "example.com/unusedpackage"       // импорт только для side effects
)
```

### Экспорт

**Имена, начинающиеся с заглавной буквы, экспортируются (публичные):**

```go
package mypackage

var PublicVar int      // экспортируется
var privateVar int     // не экспортируется

func PublicFunction() {}   // экспортируется
func privateFunction() {}  // не экспортируется
```

## Модули (Modules)

**Модули появились в `Go 1.11` и заменяют старую систему `GOPATH`:**

### Создание модуля

```bash
go mod init example.com/myproject
```

**Это создает файл `go.mod`:**

```text
module example.com/myproject

go 1.21
```

### Добавление зависимостей

```bash
go get github.com/gin-gonic/gin
go get github.com/gorilla/mux@v1.8.0  # конкретная версия
go mod tidy  # очистка неиспользуемых зависимостей
```

### Версионирование

**Go использует семантическое версионирование:**

- `v1.2.3` — стабильная версия
- `v1.2.3-beta.1` — предварительная версия
- `v0.1.0` — нестабильная версия

## Указатели (Pointers)

**Указатель хранит адрес памяти переменной:**

```go
var x int = 42
var p *int = &x      // p содержит адрес x

fmt.Println(*p)      // 42 - разыменование указателя
*p = 100             // изменение значения через указатель
fmt.Println(x)       // 100
```

### Указатели в функциях

```go
func increment(x *int) {
    *x++
}

value := 10
increment(&value)
fmt.Println(value)  // 11
```

### Указатели на структуры

```go
type Person struct {
    Name string
    Age  int
}

p := &Person{Name: "Alice", Age: 30}
p.Age = 31  // автоматическое разыменование

// или явно
(*p).Age = 31
```

## Методы (Methods)

**Метод — это функция с получателем (receiver):**

```go
type Rectangle struct {
    Width  float64
    Height float64
}

// Метод со значением (value receiver)
func (r Rectangle) Area() float64 {
    return r.Width * r.Height
}

// Метод с указателем (pointer receiver)
func (r *Rectangle) Scale(factor float64) {
    r.Width *= factor
    r.Height *= factor
}

rect := Rectangle{Width: 10, Height: 5}
fmt.Println(rect.Area())  // 50
rect.Scale(2)
fmt.Println(rect.Area())  // 200
```

### Когда использовать pointer receiver:

- Когда нужно изменить получателя
- Когда структура большая (избегаем копирования)
- Для консистентности (если есть методы, меняющие состояние)

## Управление ошибками

**В Go ошибки обрабатываются явно:**

```go
import "errors"

func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}

result, err := divide(10, 2)
if err != nil {
    fmt.Printf("Error: %v\n", err)
    return
}
fmt.Printf("Result: %f\n", result)
```

### Кастомные ошибки

```go
type DivisionError struct {
    Dividend float64
    Divisor  float64
}

func (e *DivisionError) Error() string {
    return fmt.Sprintf("cannot divide %.2f by %.2f", e.Dividend, e.Divisor)
}

func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, &DivisionError{Dividend: a, Divisor: b}
    }
    return a / b, nil
}
```

### Проверка типов ошибок

```go
result, err := divide(10, 0)
var divErr *DivisionError
if errors.As(err, &divErr) {
    fmt.Printf("Division error: %v\n", divErr)
}
```

## Горутины (Goroutines)

**Горутины — это легковесные потоки выполнения в Go. Они позволяют выполнять функции конкурентно:**

```go
package main

import (
    "fmt"
    "time"
)

func say(s string) {
    for i := 0; i < 5; i++ {
        time.Sleep(100 * time.Millisecond)
        fmt.Println(s)
    }
}

func main() {
    go say("world")  // запуск горутины
    say("hello")     // выполнение в основной горутине
}
```

### Основные концепции горутин:

1. **Легковесность**: Горутины используют минимальный стек (2KB) и могут масштабироваться до тысяч
2. **Планирование**: Go **runtime** автоматически планирует выполнение горутин на доступных **CPU**
3. **Связь через каналы**: Для безопасной коммуникации между горутинами используются каналы

### Запуск горутин:

```go
// Анонимная горутина
go func() {
    fmt.Println("Anonymous goroutine")
}()

// Горутина с параметрами
go func(msg string) {
    fmt.Println(msg)
}("Hello from goroutine")

// Горутина с замыканием
data := "closure data"
go func() {
    fmt.Println(data)
}()
```

### Ожидание завершения горутин:

```go
package main

import (
    "fmt"
    "sync"
)

func worker(id int, wg *sync.WaitGroup) {
    defer wg.Done()  // уменьшаем счетчик при завершении
    fmt.Printf("Worker %d starting\n", id)
    // работа...
    fmt.Printf("Worker %d done\n", id)
}

func main() {
    var wg sync.WaitGroup

    for i := 1; i <= 5; i++ {
        wg.Add(1)  // увеличиваем счетчик
        go worker(i, &wg)
    }

    wg.Wait()  // ждем завершения всех горутин
    fmt.Println("All workers completed")
}
```

## Каналы (Channels)

**Каналы — это типизированные конвейеры для передачи данных между горутинами:**

```go
// Создание каналов
ch := make(chan int)           // небуферизованный канал
ch := make(chan int, 10)       // буферизованный канал емкостью 10

// Отправка данных
ch <- 42

// Получение данных
value := <-ch

// Закрытие канала
close(ch)
```

### Типы каналов:

#### Небуферизованные каналы:

```go
func main() {
    ch := make(chan string)

    go func() {
        ch <- "ping"  // блокируется до получения
    }()

    msg := <-ch  // разблокирует отправителя
    fmt.Println(msg)
}
```

#### Буферизованные каналы:

```go
func main() {
    ch := make(chan string, 2)

    ch <- "buffered"  // не блокируется
    ch <- "channel"   // не блокируется

    fmt.Println(<-ch)  // buffered
    fmt.Println(<-ch)  // channel
}
```

### Select statement:

```go
func main() {
    ch1 := make(chan string)
    ch2 := make(chan string)

    go func() {
        time.Sleep(1 * time.Second)
        ch1 <- "one"
    }()

    go func() {
        time.Sleep(2 * time.Second)
        ch2 <- "two"
    }()

    for i := 0; i < 2; i++ {
        select {
        case msg1 := <-ch1:
            fmt.Println("Received", msg1)
        case msg2 := <-ch2:
            fmt.Println("Received", msg2)
        case <-time.After(3 * time.Second):
            fmt.Println("Timeout")
        }
    }
}
```

### Паттерны работы с каналами:

#### Generator pattern:

```go
func fibonacci(n int, c chan int) {
    x, y := 0, 1
    for i := 0; i < n; i++ {
        c <- x
        x, y = y, x+y
    }
    close(c)
}

func main() {
    c := make(chan int, 10)
    go fibonacci(cap(c), c)  // cap(c) возвращает емкость канала
    for i := range c {
        fmt.Println(i)
    }
}
```

#### Fan-in pattern:

```go
func fanIn(input1, input2 <-chan string) <-chan string {
    c := make(chan string)
    go func() { for { c <- <-input1 } }()
    go func() { for { c <- <-input2 } }()
    return c
}
```

## Синхронизация

**Пакет `sync` предоставляет примитивы синхронизации:**

### Mutex:

```go
package main

import (
    "fmt"
    "sync"
)

type SafeCounter struct {
    mu sync.Mutex
    v  map[string]int
}

func (c *SafeCounter) Inc(key string) {
    c.mu.Lock()
    c.v[key]++
    c.mu.Unlock()
}

func (c *SafeCounter) Value(key string) int {
    c.mu.Lock()
    defer c.mu.Unlock()
    return c.v[key]
}

func main() {
    c := SafeCounter{v: make(map[string]int)}
    for i := 0; i < 1000; i++ {
        go c.Inc("somekey")
    }
    fmt.Println(c.Value("somekey"))
}
```

### RWMutex:

```go
type SafeMap struct {
    mu   sync.RWMutex
    data map[string]int
}

func (m *SafeMap) Get(key string) int {
    m.mu.RLock()
    defer m.mu.RUnlock()
    return m.data[key]
}

func (m *SafeMap) Set(key string, value int) {
    m.mu.Lock()
    m.data[key] = value
    m.mu.Unlock()
}
```

### WaitGroup:

```go
func main() {
    var wg sync.WaitGroup

    for i := 0; i < 5; i++ {
        wg.Add(1)
        go func(id int) {
            defer wg.Done()
            fmt.Printf("Goroutine %d\n", id)
        }(i)
    }

    wg.Wait()
    fmt.Println("All goroutines completed")
}
```

### Once:

```go
var once sync.Once
var config *Config

func loadConfig() *Config {
    once.Do(func() {
        // инициализация выполнится только один раз
        config = &Config{ /* ... */ }
    })
    return config
}
```

### Cond:

```go
var cond = sync.NewCond(&sync.Mutex{})
var ready = false

func waitForReady() {
    cond.L.Lock()
    for !ready {
        cond.Wait()  // разблокирует мьютекс и ждет сигнала
    }
    cond.L.Unlock()
    fmt.Println("Ready!")
}

func makeReady() {
    cond.L.Lock()
    ready = true
    cond.L.Unlock()
    cond.Broadcast()  // будит все ожидающие горутины
}
```

## Context

**Пакет `context` используется для управления жизненным циклом операций:**

```go
package main

import (
    "context"
    "fmt"
    "time"
)

func worker(ctx context.Context, id int) {
    for {
        select {
        case <-ctx.Done():
            fmt.Printf("Worker %d cancelled: %v\n", id, ctx.Err())
            return
        default:
            fmt.Printf("Worker %d working\n", id)
            time.Sleep(500 * time.Millisecond)
        }
    }
}

func main() {
    ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
    defer cancel()

    for i := 1; i <= 3; i++ {
        go worker(ctx, i)
    }

    time.Sleep(3 * time.Second)
}
```

### Типы контекстов:

```go
// WithCancel - для явной отмены
ctx, cancel := context.WithCancel(parentCtx)

// WithTimeout - автоматическая отмена по таймауту
ctx, cancel := context.WithTimeout(parentCtx, time.Minute)

// WithDeadline - отмена в указанное время
ctx, cancel := context.WithDeadline(parentCtx, time.Now().Add(time.Hour))

// WithValue - передача значений
ctx := context.WithValue(parentCtx, "user_id", 123)
```

## JSON

**Go имеет встроенную поддержку **JSON**:**

```go
package main

import (
    "encoding/json"
    "fmt"
)

type Person struct {
    Name    string   `json:"name"`
    Age     int      `json:"age"`
    Hobbies []string `json:"hobbies,omitempty"`
    Address *Address `json:"address,omitempty"`
}

type Address struct {
    Street string `json:"street"`
    City   string `json:"city"`
}

func main() {
    // Маршализация (Go -> JSON)
    p := Person{
        Name:    "Alice",
        Age:     30,
        Hobbies: []string{"reading", "coding"},
        Address: &Address{"123 Main St", "NYC"},
    }

    jsonData, err := json.Marshal(p)
    if err != nil {
        panic(err)
    }
    fmt.Println(string(jsonData))

    // Демаршализация (JSON -> Go)
    jsonStr := `{"name":"Bob","age":25,"hobbies":["gaming","music"]}`
    var p2 Person
    err = json.Unmarshal([]byte(jsonStr), &p2)
    if err != nil {
        panic(err)
    }
    fmt.Printf("%+v\n", p2)
}
```

### Теги JSON:

```go
type User struct {
    ID       int    `json:"id"`
    Username string `json:"username"`
    Password string `json:"-"`        // игнорировать поле
    Email    string `json:"email,omitempty"` // опустить если пустое
    Created  time.Time `json:"created_at"`
}
```

### Кастомный маршалинг:

```go
func (p Person) MarshalJSON() ([]byte, error) {
    type Alias Person  // предотвращает рекурсию
    return json.Marshal(&struct {
        Alias
        FullName string `json:"full_name"`
    }{
        Alias:    (Alias)(p),
        FullName: p.Name + " Smith",
    })
}
```

## Работа с файлами

### Чтение файлов:

```go
package main

import (
    "bufio"
    "fmt"
    "io"
    "os"
)

func main() {
    // Чтение всего файла
    data, err := os.ReadFile("file.txt")
    if err != nil {
        panic(err)
    }
    fmt.Println(string(data))

    // Чтение по строкам
    file, err := os.Open("file.txt")
    if err != nil {
        panic(err)
    }
    defer file.Close()

    scanner := bufio.NewScanner(file)
    for scanner.Scan() {
        fmt.Println(scanner.Text())
    }

    // Чтение с буфером
    buffer := make([]byte, 1024)
    for {
        n, err := file.Read(buffer)
        if err == io.EOF {
            break
        }
        if err != nil {
            panic(err)
        }
        fmt.Print(string(buffer[:n]))
    }
}
```

### Запись в файлы:

```go
// Запись всего файла
err := os.WriteFile("output.txt", []byte("Hello, World!"), 0644)

// Создание и запись
file, err := os.Create("output.txt")
if err != nil {
    panic(err)
}
defer file.Close()

// Запись байтов
file.Write([]byte("Hello"))
file.WriteString(" World!")

// Буферизованная запись
writer := bufio.NewWriter(file)
writer.WriteString("Buffered write")
writer.Flush()  // обязательно вызвать Flush

// Форматированная запись
fmt.Fprintf(file, "Number: %d, String: %s\n", 42, "test")
```

### Работа с директориями:

```go
// Создание директории
os.Mkdir("newdir", 0755)
os.MkdirAll("path/to/dir", 0755)

// Чтение содержимого директории
files, err := os.ReadDir(".")
for _, file := range files {
    fmt.Println(file.Name(), file.IsDir())
}

// Рекурсивный обход
filepath.WalkDir(".", func(path string, d fs.DirEntry, err error) error {
    if err != nil {
        return err
    }
    fmt.Println(path)
    return nil
})
```

## Базы данных

**Go имеет драйверы для большинства популярных БД:**

### PostgreSQL:

```go
package main

import (
    "database/sql"
    "fmt"
    "log"

    _ "github.com/lib/pq"  // драйвер PostgreSQL
)

type User struct {
    ID    int
    Name  string
    Email string
}

func main() {
    // Подключение
    db, err := sql.Open("postgres", "user=postgres password=secret dbname=mydb sslmode=disable")
    if err != nil {
        log.Fatal(err)
    }
    defer db.Close()

    // Создание таблицы
    _, err = db.Exec(`
        CREATE TABLE IF NOT EXISTS users (
            id SERIAL PRIMARY KEY,
            name VARCHAR(100),
            email VARCHAR(100) UNIQUE
        )
    `)

    // Вставка данных
    result, err := db.Exec("INSERT INTO users (name, email) VALUES ($1, $2)", "Alice", "alice@example.com")
    if err != nil {
        log.Fatal(err)
    }

    id, _ := result.LastInsertId()
    fmt.Printf("Inserted user with ID: %d\n", id)

    // Запрос данных
    rows, err := db.Query("SELECT id, name, email FROM users")
    if err != nil {
        log.Fatal(err)
    }
    defer rows.Close()

    for rows.Next() {
        var user User
        err := rows.Scan(&user.ID, &user.Name, &user.Email)
        if err != nil {
            log.Fatal(err)
        }
        fmt.Printf("User: %+v\n", user)
    }

    // Подготовленный запрос
    stmt, err := db.Prepare("SELECT name FROM users WHERE id = $1")
    if err != nil {
        log.Fatal(err)
    }
    defer stmt.Close()

    var name string
    err = stmt.QueryRow(1).Scan(&name)
    fmt.Printf("User name: %s\n", name)

    // Транзакция
    tx, err := db.Begin()
    if err != nil {
        log.Fatal(err)
    }

    _, err = tx.Exec("UPDATE users SET name = $1 WHERE id = $2", "Bob", 1)
    if err != nil {
        tx.Rollback()
        log.Fatal(err)
    }

    err = tx.Commit()
    if err != nil {
        log.Fatal(err)
    }
}
```

### Connection pooling:

```go
db.SetMaxOpenConns(25)                 // максимум открытых соединений
db.SetMaxIdleConns(25)                 // максимум простаивающих соединений
db.SetConnMaxLifetime(5 * time.Minute) // максимальное время жизни соединения
```

## HTTP клиент и сервер

### HTTP сервер:

```go
package main

import (
    "fmt"
    "net/http"
    "time"
)

func handler(w http.ResponseWriter, r *http.Request) {
    fmt.Fprintf(w, "Hello, %s!", r.URL.Path[1:])
}

func timeHandler(w http.ResponseWriter, r *http.Request) {
    fmt.Fprintf(w, "Current time: %s", time.Now().Format(time.RFC3339))
}

func main() {
    http.HandleFunc("/", handler)
    http.HandleFunc("/time", timeHandler)

    // Статические файлы
    fs := http.FileServer(http.Dir("static/"))
    http.Handle("/static/", http.StripPrefix("/static/", fs))

    // Middleware
    http.HandleFunc("/api/", loggingMiddleware(apiHandler))

    fmt.Println("Server starting on :8080")
    log.Fatal(http.ListenAndServe(":8080", nil))
}

func loggingMiddleware(next http.HandlerFunc) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        next(w, r)
        fmt.Printf("%s %s %v\n", r.Method, r.URL.Path, time.Since(start))
    }
}

func apiHandler(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "application/json")
    fmt.Fprintf(w, `{"message": "Hello from API"}`)
}
```

### HTTP клиент:

```go
package main

import (
    "bytes"
    "encoding/json"
    "fmt"
    "io"
    "net/http"
    "time"
)

type User struct {
    ID   int    `json:"id"`
    Name string `json:"name"`
}

func main() {
    client := &http.Client{
        Timeout: 10 * time.Second,
    }

    // GET запрос
    resp, err := client.Get("https://jsonplaceholder.typicode.com/users/1")
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()

    body, err := io.ReadAll(resp.Body)
    fmt.Printf("Status: %s\n", resp.Status)
    fmt.Printf("Body: %s\n", string(body))

    // POST запрос
    user := User{Name: "John Doe"}
    jsonData, err := json.Marshal(user)
    if err != nil {
        panic(err)
    }

    resp, err = client.Post("https://jsonplaceholder.typicode.com/users",
        "application/json", bytes.NewBuffer(jsonData))
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()

    var createdUser User
    json.NewDecoder(resp.Body).Decode(&createdUser)
    fmt.Printf("Created user: %+v\n", createdUser)

    // Кастомный запрос
    req, err := http.NewRequest("PUT", "https://jsonplaceholder.typicode.com/users/1", bytes.NewBuffer(jsonData))
    if err != nil {
        panic(err)
    }

    req.Header.Set("Content-Type", "application/json")
    req.Header.Set("Authorization", "Bearer token123")

    resp, err = client.Do(req)
    if err != nil {
        panic(err)
    }
    defer resp.Body.Close()

    fmt.Printf("PUT Status: %s\n", resp.Status)
}
```

### REST API с Gin:

```go
package main

import (
    "net/http"
    "strconv"

    "github.com/gin-gonic/gin"
)

type Album struct {
    ID     int     `json:"id"`
    Title  string  `json:"title"`
    Artist string  `json:"artist"`
    Price  float64 `json:"price"`
}

var albums = []Album{
    {ID: 1, Title: "Blue Train", Artist: "John Coltrane", Price: 56.99},
    {ID: 2, Title: "Jeru", Artist: "Gerry Mulligan", Price: 17.99},
}

func main() {
    router := gin.Default()

    router.GET("/albums", getAlbums)
    router.GET("/albums/:id", getAlbumByID)
    router.POST("/albums", postAlbums)

    router.Run("localhost:8080")
}

func getAlbums(c *gin.Context) {
    c.IndentedJSON(http.StatusOK, albums)
}

func getAlbumByID(c *gin.Context) {
    id := c.Param("id")

    for _, a := range albums {
        if strconv.Itoa(a.ID) == id {
            c.IndentedJSON(http.StatusOK, a)
            return
        }
    }
    c.IndentedJSON(http.StatusNotFound, gin.H{"message": "album not found"})
}

func postAlbums(c *gin.Context) {
    var newAlbum Album

    if err := c.BindJSON(&newAlbum); err != nil {
        return
    }

    albums = append(albums, newAlbum)
    c.IndentedJSON(http.StatusCreated, newAlbum)
}
```

## Generics (Go 1.18+)

**Generics** позволяют писать обобщенный код:**

```go
package main

import "fmt"

// Обобщенная функция
func PrintSlice[T any](s []T) {
    for _, v := range s {
        fmt.Print(v, " ")
    }
    fmt.Println()
}

// Обобщенная функция с ограничениями
func Max[T Number](a, b T) T {
    if a > b {
        return a
    }
    return b
}

// Тип constraint
type Number interface {
    ~int | ~int8 | ~int16 | ~int32 | ~int64 |
    ~uint | ~uint8 | ~uint16 | ~uint32 | ~uint64 | ~uintptr |
    ~float32 | ~float64
}

// Обобщенный тип
type Stack[T any] struct {
    items []T
}

func (s *Stack[T]) Push(item T) {
    s.items = append(s.items, item)
}

func (s *Stack[T]) Pop() T {
    item := s.items[len(s.items)-1]
    s.items = s.items[:len(s.items)-1]
    return item
}

func main() {
    // Использование обобщенной функции
    PrintSlice([]int{1, 2, 3})
    PrintSlice([]string{"a", "b", "c"})

    // Использование с ограничениями
    fmt.Println(Max(10, 20))        // 20
    fmt.Println(Max(10.5, 5.2))     // 10.5

    // Использование обобщенного типа
    intStack := Stack[int]{}
    intStack.Push(1)
    intStack.Push(2)
    fmt.Println(intStack.Pop())  // 2

    stringStack := Stack[string]{}
    stringStack.Push("hello")
    stringStack.Push("world")
    fmt.Println(stringStack.Pop())  // world
}
```

### Сложные constraints:

```go
// Constraint для типов с методом String()
type Stringer interface {
    String() string
}

// Constraint для типов с операторами сравнения
type Ordered interface {
    ~int | ~int8 | ~int16 | ~int32 | ~int64 |
    ~uint | ~uint8 | ~uint16 | ~uint32 | ~uint64 | ~uintptr |
    ~float32 | ~float64 |
    ~string
}

// Обобщенная сортировка
func Sort[T Ordered](slice []T) {
    // Пузырьковая сортировка для примера
    for i := 0; i < len(slice); i++ {
        for j := 0; j < len(slice)-1-i; j++ {
            if slice[j] > slice[j+1] {
                slice[j], slice[j+1] = slice[j+1], slice[j]
            }
        }
    }
}

// Constraint для структур с определенными полями
type Person interface {
    GetName() string
    GetAge() int
}

func PrintPersonInfo[T Person](p T) {
    fmt.Printf("Name: %s, Age: %d\n", p.GetName(), p.GetAge())
}
```

## Рефлексия (Reflection)

**Рефлексия позволяет инспектировать и модифицировать переменные во время выполнения:**

```go
package main

import (
    "fmt"
    "reflect"
)

type User struct {
    Name string `json:"name"`
    Age  int    `json:"age"`
}

func main() {
    user := User{Name: "Alice", Age: 30}

    // Получение типа
    t := reflect.TypeOf(user)
    fmt.Printf("Type: %s, Kind: %s\n", t.Name(), t.Kind())

    // Получение значения
    v := reflect.ValueOf(user)

    // Итерация по полям
    for i := 0; i < t.NumField(); i++ {
        field := t.Field(i)
        value := v.Field(i)

        fmt.Printf("Field %s = %v (tag: %s)\n",
            field.Name, value.Interface(), field.Tag.Get("json"))
    }

    // Вызов метода по имени
    method := v.MethodByName("String")
    if method.IsValid() {
        result := method.Call(nil)
        fmt.Printf("String(): %s\n", result[0].String())
    }

    // Создание нового значения
    newUserType := reflect.TypeOf(User{})
    newUserValue := reflect.New(newUserType).Elem()

    newUserValue.FieldByName("Name").SetString("Bob")
    newUserValue.FieldByName("Age").SetInt(25)

    newUser := newUserValue.Interface().(User)
    fmt.Printf("New user: %+v\n", newUser)
}

// Метод для демонстрации
func (u User) String() string {
    return fmt.Sprintf("User{Name: %s, Age: %d}", u.Name, u.Age)
}
```

### Практическое применение рефлексии:

```go
// JSON сериализация с рефлексией
func ToMap(obj interface{}) map[string]interface{} {
    result := make(map[string]interface{})

    v := reflect.ValueOf(obj)
    if v.Kind() == reflect.Ptr {
        v = v.Elem()
    }

    t := v.Type()

    for i := 0; i < v.NumField(); i++ {
        field := t.Field(i)
        value := v.Field(i)

        // Пропускаем неэкспортированные поля
        if !field.IsExported() {
            continue
        }

        // Используем JSON tag или имя поля
        key := field.Tag.Get("json")
        if key == "" {
            key = field.Name
        }

        result[key] = value.Interface()
    }

    return result
}

// Валидация структур
type Validator interface {
    Validate() error
}

func ValidateStruct(obj interface{}) error {
    v := reflect.ValueOf(obj)

    // Если указатель, получаем значение
    if v.Kind() == reflect.Ptr {
        v = v.Elem()
    }

    // Проверяем, реализует ли тип Validator
    if validator, ok := obj.(Validator); ok {
        return validator.Validate()
    }

    // Проверяем теги валидации
    t := v.Type()
    for i := 0; i < v.NumField(); i++ {
        field := t.Field(i)
        value := v.Field(i)

        validateTag := field.Tag.Get("validate")
        if validateTag == "required" && value.IsZero() {
            return fmt.Errorf("field %s is required", field.Name)
        }

        if validateTag == "min=1" {
            if num, ok := value.Interface().(int); ok && num < 1 {
                return fmt.Errorf("field %s must be at least 1", field.Name)
            }
        }
    }

    return nil
}
```

## Unsafe операции

**Пакет `unsafe` позволяет обходить систему типов Go для низкоуровневых операций:**

```go
package main

import (
    "fmt"
    "unsafe"
)

type User struct {
    Name string
    Age  int
}

func main() {
    user := User{Name: "Alice", Age: 30}

    // Получение размера структуры
    size := unsafe.Sizeof(user)
    fmt.Printf("Size of User: %d bytes\n", size)

    // Преобразование указателя
    namePtr := (*string)(unsafe.Pointer(&user.Name))
    *namePtr = "Bob"

    fmt.Printf("Modified user: %+v\n", user)

    // Работа с сырыми байтами
    data := []byte("Hello, World!")
    ptr := unsafe.Pointer(&data[0])

    // Преобразование в указатель на int32
    intPtr := (*int32)(ptr)
    fmt.Printf("First 4 bytes as int32: %d\n", *intPtr)

    // Вычисление смещений полей
    nameOffset := unsafe.Offsetof(user.Name)
    ageOffset := unsafe.Offsetof(user.Age)

    fmt.Printf("Name offset: %d, Age offset: %d\n", nameOffset, ageOffset)

    // Доступ к полям через смещения
    userPtr := unsafe.Pointer(&user)
    agePtr := (*int)(unsafe.Pointer(uintptr(userPtr) + ageOffset))
    *agePtr = 31

    fmt.Printf("Final user: %+v\n", user)
}
```

⚠️ **Предупреждение**: Использование `unsafe` может привести к неопределенному поведению и нарушению безопасности памяти. Используйте только при необходимости и с осторожностью.

## Тестирование

**Go имеет встроенную поддержку тестирования:**

```go
// math_test.go
package math

import "testing"

func TestAdd(t *testing.T) {
    result := Add(2, 3)
    expected := 5

    if result != expected {
        t.Errorf("Add(2, 3) = %d; expected %d", result, expected)
    }
}

func TestAddTable(t *testing.T) {
    tests := []struct {
        name     string
        a, b     int
        expected int
    }{
        {"positive numbers", 2, 3, 5},
        {"negative numbers", -1, -2, -3},
        {"zero", 0, 5, 5},
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            result := Add(tt.a, tt.b)
            if result != tt.expected {
                t.Errorf("Add(%d, %d) = %d; expected %d", tt.a, tt.b, result, tt.expected)
            }
        })
    }
}

func BenchmarkAdd(b *testing.B) {
    for i := 0; i < b.N; i++ {
        Add(2, 3)
    }
}
```

### Запуск тестов:

```bash
go test                    # запуск всех тестов
go test -v                 # подробный вывод
go test -run TestAdd       # запуск конкретного теста
go test -bench=.           # запуск бенчмарков
go test -cover             # покрытие кода
go test -race              # проверка на race conditions
```

### Тесты с зависимостями:

```go
type MockDB struct{}

func (m *MockDB) GetUser(id int) (*User, error) {
    if id == 1 {
        return &User{ID: 1, Name: "Alice"}, nil
    }
    return nil, errors.New("user not found")
}

func TestUserService_GetUser(t *testing.T) {
    mockDB := &MockDB{}
    service := &UserService{db: mockDB}

    user, err := service.GetUser(1)
    if err != nil {
        t.Fatalf("Expected no error, got %v", err)
    }

    if user.Name != "Alice" {
        t.Errorf("Expected name 'Alice', got '%s'", user.Name)
    }
}
```

### HTTP тестирование:

```go
func TestGetUserHandler(t *testing.T) {
    // Создание тестового сервера
    router := setupRouter()
    ts := httptest.NewServer(router)
    defer ts.Close()

    // Выполнение запроса
    resp, err := http.Get(ts.URL + "/users/1")
    if err != nil {
        t.Fatalf("Expected no error, got %v", err)
    }
    defer resp.Body.Close()

    // Проверка статуса
    if resp.StatusCode != http.StatusOK {
        t.Errorf("Expected status 200, got %d", resp.StatusCode)
    }

    // Проверка тела ответа
    var user User
    if err := json.NewDecoder(resp.Body).Decode(&user); err != nil {
        t.Fatalf("Failed to decode response: %v", err)
    }

    if user.ID != 1 {
        t.Errorf("Expected user ID 1, got %d", user.ID)
    }
}
```

## Benchmarking

```go
func BenchmarkStringConcat(b *testing.B) {
    // Сброс таймера
    b.ResetTimer()

    for i := 0; i < b.N; i++ {
        var s string
        for j := 0; j < 100; j++ {
            s += "test"
        }
    }
}

func BenchmarkStringBuilder(b *testing.B) {
    for i := 0; i < b.N; i++ {
        var sb strings.Builder
        for j := 0; j < 100; j++ {
            sb.WriteString("test")
        }
        _ = sb.String()
    }
}

func BenchmarkMapAccess(b *testing.B) {
    m := make(map[int]int)
    for i := 0; i < 1000; i++ {
        m[i] = i
    }

    b.ResetTimer()

    for i := 0; i < b.N; i++ {
        _ = m[i%1000]
    }
}
```

### Анализ результатов:

```bash
go test -bench=. -benchmem
# BenchmarkStringConcat-8    100000    12345 ns/op    1234 B/op    100 allocs/op
# BenchmarkStringBuilder-8   200000     6789 ns/op     234 B/op      2 allocs/op
```

## Профилирование

**Go имеет встроенные инструменты профилирования:**

```go
package main

import (
    "log"
    "os"
    "runtime/pprof"
)

func main() {
    // CPU профилирование
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
    doWork()

    // Memory профилирование
    memFile, err := os.Create("mem.prof")
    if err != nil {
        log.Fatal(err)
    }
    defer memFile.Close()

    runtime.GC() // Получить актуальные данные
    if err := pprof.WriteHeapProfile(memFile); err != nil {
        log.Fatal(err)
    }
}
```

### Анализ профилей:

```bash
go tool pprof cpu.prof
# В интерактивном режиме:
# top      - топ функций
# list main.doWork - детальный анализ функции
# web      - графическое представление

# Анализ памяти
go tool pprof mem.prof
```

## Отладка

### Delve debugger:

```bash
# Установка
go install github.com/go-delve/delve/cmd/dlv@latest

# Запуск программы под отладчиком
dlv debug main.go

# Основные команды в отладчике:
# break main.main - установить breakpoint
# continue - продолжить выполнение
# next - шаг с заходом
# step - шаг без захода
# print variable - вывести переменную
# goroutines - показать горутины
```

### Логирование:

```go
package main

import (
    "log"
    "os"
)

func init() {
    // Настройка логирования
    log.SetFlags(log.LstdFlags | log.Lshortfile)
    log.SetOutput(os.Stdout)
}

func main() {
    log.Println("Starting application")

    // Структурированное логирование с slog (Go 1.21+)
    logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{
        Level: slog.LevelDebug,
    }))

    logger.Info("User logged in",
        "user_id", 123,
        "ip", "192.168.1.1",
    )

    logger.Error("Database connection failed",
        "error", err,
        "retry_count", 3,
    )
}
```

## Лучшие практики

### Стиль кода:

```go
// Правильное именование
type UserService struct{}  // публичный тип
type userRepository struct{}  // приватный тип

func (s *UserService) GetUser(id int) (*User, error) {  // публичный метод
    return s.repo.getUser(id)  // приватный метод
}

// Обработка ошибок
func processFile(filename string) error {
    file, err := os.Open(filename)
    if err != nil {
        return fmt.Errorf("failed to open file %s: %w", filename, err)
    }
    defer file.Close()

    // обработка файла
    return nil
}

// Использование контекстов
func handleRequest(ctx context.Context, req *Request) error {
    // Проверка отмены
    select {
    case <-ctx.Done():
        return ctx.Err()
    default:
    }

    // Таймаут для операций
    dbCtx, cancel := context.WithTimeout(ctx, 5*time.Second)
    defer cancel()

    return db.QueryRowContext(dbCtx, "SELECT * FROM users WHERE id = $1", req.UserID).Scan(...)
}
```

### Производительность:

```go
// Буферы для строк
func concatStrings(strings []string) string {
    var sb strings.Builder
    sb.Grow(len(strings) * 10)  // предварительное выделение

    for _, s := range strings {
        sb.WriteString(s)
    }
    return sb.String()
}

// Пул объектов
var bufferPool = sync.Pool{
    New: func() interface{} {
        return make([]byte, 4096)
    },
}

func processData(data []byte) {
    buffer := bufferPool.Get().([]byte)
    defer bufferPool.Put(buffer)

    // использование buffer
    copy(buffer, data)
}

// Оптимизация JSON
type User struct {
    ID       int       `json:"id"`
    Name     string    `json:"name"`
    Created  time.Time `json:"created"`
}

// Кастомный маршалинг для оптимизации
func (u User) MarshalJSON() ([]byte, error) {
    return json.Marshal(struct {
        ID      int    `json:"id"`
        Name    string `json:"name"`
        Created int64  `json:"created"`
    }{
        ID:      u.ID,
        Name:    u.Name,
        Created: u.Created.Unix(),
    })
}
```

## Распространенные паттерны

### Singleton:

```go
type singleton struct{}

var instance *singleton
var once sync.Once

func GetInstance() *singleton {
    once.Do(func() {
        instance = &singleton{}
    })
    return instance
}
```

### Factory:

```go
type Database interface {
    Connect() error
    Query(query string) ([]Result, error)
}

type PostgresDB struct{}

func (p *PostgresDB) Connect() error { /* ... */ }
func (p *PostgresDB) Query(query string) ([]Result, error) { /* ... */ }

type MongoDB struct{}

func (m *MongoDB) Connect() error { /* ... */ }
func (m *MongoDB) Query(query string) ([]Result, error) { /* ... */ }

func CreateDatabase(dbType string) Database {
    switch dbType {
    case "postgres":
        return &PostgresDB{}
    case "mongo":
        return &MongoDB{}
    default:
        return nil
    }
}
```

### Decorator:

```go
type HttpHandler func(http.ResponseWriter, *http.Request)

func LoggingMiddleware(next HttpHandler) HttpHandler {
    return func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        next(w, r)
        log.Printf("%s %s took %v", r.Method, r.URL.Path, time.Since(start))
    }
}

func AuthMiddleware(next HttpHandler) HttpHandler {
    return func(w http.ResponseWriter, r *http.Request) {
        if !isAuthenticated(r) {
            http.Error(w, "Unauthorized", http.StatusUnauthorized)
            return
        }
        next(w, r)
    }
}

func handler(w http.ResponseWriter, r *http.Request) {
    fmt.Fprintf(w, "Hello, authenticated user!")
}

func main() {
    http.HandleFunc("/api", LoggingMiddleware(AuthMiddleware(handler)))
    http.ListenAndServe(":8080", nil)
}
```

### Worker Pool:

```go
type WorkerPool struct {
    jobs    chan Job
    results chan Result
    workers int
}

type Job struct {
    ID   int
    Data string
}

type Result struct {
    JobID   int
    Output  string
    Success bool
}

func NewWorkerPool(workers int) *WorkerPool {
    wp := &WorkerPool{
        jobs:    make(chan Job, 100),
        results: make(chan Result, 100),
        workers: workers,
    }

    for i := 0; i < workers; i++ {
        go wp.worker(i)
    }

    return wp
}

func (wp *WorkerPool) worker(id int) {
    for job := range wp.jobs {
        // Обработка задания
        result := Result{
            JobID:   job.ID,
            Output:  strings.ToUpper(job.Data),
            Success: true,
        }
        wp.results <- result
    }
}

func (wp *WorkerPool) Submit(job Job) {
    wp.jobs <- job
}

func (wp *WorkerPool) Results() <-chan Result {
    return wp.results
}

func (wp *WorkerPool) Shutdown() {
    close(wp.jobs)
}
```

### Dependency Injection:

```go
type UserService struct {
    repo UserRepository
    cache Cache
    logger Logger
}

type UserRepository interface {
    GetByID(id int) (*User, error)
    Save(user *User) error
}

type Cache interface {
    Get(key string) (interface{}, bool)
    Set(key string, value interface{})
}

type Logger interface {
    Info(msg string, args ...interface{})
    Error(msg string, args ...interface{})
}

// Конструктор с dependency injection
func NewUserService(repo UserRepository, cache Cache, logger Logger) *UserService {
    return &UserService{
        repo:   repo,
        cache:  cache,
        logger: logger,
    }
}

func (s *UserService) GetUser(id int) (*User, error) {
    cacheKey := fmt.Sprintf("user:%d", id)

    // Проверка кэша
    if cached, found := s.cache.Get(cacheKey); found {
        s.logger.Info("User found in cache", "user_id", id)
        return cached.(*User), nil
    }

    // Получение из репозитория
    user, err := s.repo.GetByID(id)
    if err != nil {
        s.logger.Error("Failed to get user", "error", err, "user_id", id)
        return nil, err
    }

    // Сохранение в кэш
    s.cache.Set(cacheKey, user)

    return user, nil
}
```

## Производительность

### Оптимизация памяти:

```go
// Избегайте лишних аллокаций
func processStrings(strings []string) []string {
    // Плохо - создает промежуточные срезы
    result := make([]string, 0, len(strings))
    for _, s := range strings {
        if len(s) > 5 {
            result = append(result, strings.ToUpper(s))
        }
    }
    return result
}

// Хорошо - переиспользуем память
func processStringsOptimized(strings []string) []string {
    result := make([]string, 0, len(strings))
    for _, s := range strings {
        if len(s) > 5 {
            result = append(result, strings.ToUpper(s))
        }
    }
    return result[:len(result):len(result)]  // предотвращаем future appends от использования лишней памяти
}

// Использование object pooling
var userPool = sync.Pool{
    New: func() interface{} {
        return &User{}
    },
}

func getUser() *User {
    return userPool.Get().(*User)
}

func putUser(u *User) {
    // Сброс состояния
    u.ID = 0
    u.Name = ""
    userPool.Put(u)
}
```

### Оптимизация CPU:

```go
// SIMD операции (если поддерживается)
import "golang.org/x/sys/cpu"

func addVectors(a, b []float64) []float64 {
    if cpu.X86.HasAVX2 {
        // Использовать AVX2 инструкции для векторных операций
        return addVectorsAVX2(a, b)
    }

    // Fallback для обычных процессоров
    result := make([]float64, len(a))
    for i := range a {
        result[i] = a[i] + b[i]
    }
    return result
}

// Кэширование результатов
var fibCache = make(map[int]int)

func fibonacci(n int) int {
    if n <= 1 {
        return n
    }

    if val, ok := fibCache[n]; ok {
        return val
    }

    result := fibonacci(n-1) + fibonacci(n-2)
    fibCache[n] = result
    return result
}

// Оптимизация строковых операций
func isPalindrome(s string) bool {
    runes := []rune(s)  // для поддержки Unicode
    for i, j := 0, len(runes)-1; i < j; i, j = i+1, j-1 {
        if runes[i] != runes[j] {
            return false
        }
    }
    return true
}
```

### Профилирование производительности:

```go
import (
    "runtime"
    "time"
)

func monitorGoroutines() {
    for {
        time.Sleep(10 * time.Second)
        fmt.Printf("Active goroutines: %d\n", runtime.NumGoroutine())
    }
}

func monitorGC() {
    var stats runtime.MemStats
    for {
        time.Sleep(30 * time.Second)
        runtime.ReadMemStats(&stats)
        fmt.Printf("GC cycles: %d, Pause total: %v\n",
            stats.NumGC, time.Duration(stats.PauseTotalNs))
    }
}
```

## Безопасность

### Валидация ввода:

```go
import (
    "regexp"
    "strings"
)

func sanitizeInput(input string) string {
    // Удаление потенциально опасных символов
    input = strings.ReplaceAll(input, "<", "&lt;")
    input = strings.ReplaceAll(input, ">", "&gt;")
    input = strings.ReplaceAll(input, "&", "&amp;")
    input = strings.ReplaceAll(input, "\"", "&quot;")

    return input
}

func validateEmail(email string) bool {
    emailRegex := regexp.MustCompile(`^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$`)
    return emailRegex.MatchString(email)
}

func validatePassword(password string) error {
    if len(password) < 8 {
        return errors.New("password must be at least 8 characters")
    }

    hasUpper := regexp.MustCompile(`[A-Z]`).MatchString(password)
    hasLower := regexp.MustCompile(`[a-z]`).MatchString(password)
    hasNumber := regexp.MustCompile(`[0-9]`).MatchString(password)

    if !hasUpper || !hasLower || !hasNumber {
        return errors.New("password must contain uppercase, lowercase, and number")
    }

    return nil
}
```

### Безопасность горутин:

```go
// Recovery от panic в горутинах
func safeGoroutine(fn func()) {
    defer func() {
        if r := recover(); r != nil {
            log.Printf("Goroutine panicked: %v", r)
            // Логирование stack trace
            // Отправка алерта
        }
    }()
    fn()
}

// Запуск безопасной горутины
go safeGoroutine(func() {
    // потенциально опасный код
    riskyOperation()
})
```

### Защита от race conditions:

```go
type SafeCounter struct {
    mu    sync.RWMutex
    count int
}

func (c *SafeCounter) Increment() {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.count++
}

func (c *SafeCounter) Value() int {
    c.mu.RLock()
    defer c.mu.RUnlock()
    return c.count
}

// Или использование atomic
import "sync/atomic"

type AtomicCounter struct {
    count int64
}

func (c *AtomicCounter) Increment() {
    atomic.AddInt64(&c.count, 1)
}

func (c *AtomicCounter) Value() int64 {
    return atomic.LoadInt64(&c.count)
}
```

### HTTPS и TLS:

```go
// HTTP сервер с TLS
func main() {
    router := http.NewServeMux()
    router.HandleFunc("/", handler)

    // Генерация самоподписанного сертификата для разработки
    cert, err := tls.X509KeyPair(certPEM, keyPEM)
    if err != nil {
        log.Fatal(err)
    }

    tlsConfig := &tls.Config{
        Certificates: []tls.Certificate{cert},
        MinVersion:   tls.VersionTLS12,
    }

    server := &http.Server{
        Addr:      ":8443",
        Handler:   router,
        TLSConfig: tlsConfig,
    }

    log.Fatal(server.ListenAndServeTLS("", ""))
}

// HTTP клиент с кастомным TLS
func createSecureClient() *http.Client {
    tlsConfig := &tls.Config{
        MinVersion:         tls.VersionTLS12,
        InsecureSkipVerify: false,  // для продакшена всегда false
        Certificates:       []tls.Certificate{clientCert},
    }

    transport := &http.Transport{
        TLSClientConfig: tlsConfig,
    }

    return &http.Client{
        Transport: transport,
        Timeout:   30 * time.Second,
    }
}
```

## Дополнительные темы

### Практический навигатор

Этот блок оставлен как расширенная справка. Для production-работы удобнее идти по профильным документам:

- [[go-configuration]] — конфигурация и работа с окружением
- [[go-command-line]] — CLI, флаги и аргументы
- [[go-time]] — время, таймеры, интервалы
- [[go-regexp]] — регулярные выражения
- [[go-database]] — БД, миграции, транзакции
- [[go-concurrency]] — каналы, worker pool, pipeline
- [[go-observability]] — метрики, логи, tracing
- [[go-best-practices]] — архитектура и эксплуатационные практики

### Что изучать в этом файле, а что вынести в профильные документы

- В этом файле: базовые синтаксические и платформенные концепции Go.
- В профильных файлах: инфраструктурные и production-паттерны (retry, circuit breaker, CQRS, outbox, кэш/очереди, миграции и т.д.).

### Расширенная справка (legacy)

### Работа с окружением

**Go предоставляет удобные инструменты для работы с переменными окружения:**

```go
import "os"

// Получение переменной окружения
func getEnv(key, defaultValue string) string {
    if value := os.Getenv(key); value != "" {
        return value
    }
    return defaultValue
}

// Установка переменной окружения
os.Setenv("MY_VAR", "value")

// Получение всех переменных окружения
env := os.Environ()
for _, e := range env {
    fmt.Println(e)
}

// Расширение переменных в строке
path := os.ExpandEnv("$HOME/go/bin")
```

### Работа с аргументами командной строки

```go
import (
    "flag"
    "os"
)

func main() {
    // Простые флаги
    name := flag.String("name", "World", "Name to greet")
    age := flag.Int("age", 0, "Age")
    verbose := flag.Bool("verbose", false, "Verbose output")

    flag.Parse()

    fmt.Printf("Hello, %s! Age: %d\n", *name, *age)
    if *verbose {
        fmt.Println("Verbose mode enabled")
    }

    // Аргументы без флагов
    args := flag.Args()
    fmt.Println("Arguments:", args)
}
```

### Работа с временем и датами

```go
import "time"

// Текущее время
now := time.Now()
fmt.Println(now)

// Создание конкретного времени
t := time.Date(2025, 1, 11, 12, 30, 0, 0, time.UTC)

// Парсинг времени
t, err := time.Parse("2006-01-02 15:04:05", "2025-01-11 12:30:00")
if err != nil {
    log.Fatal(err)
}

// Форматирование времени
formatted := now.Format("2006-01-02 15:04:05")
fmt.Println(formatted)

// Операции со временем
future := now.Add(24 * time.Hour)
past := now.Add(-24 * time.Hour)
diff := future.Sub(past)

// Таймеры
timer := time.NewTimer(2 * time.Second)
<-timer.C
fmt.Println("Timer expired")

// Ticker
ticker := time.NewTicker(1 * time.Second)
defer ticker.Stop()
for t := range ticker.C {
    fmt.Println("Tick at", t)
}
```

### Работа с регулярными выражениями

```go
import "regexp"

// Компиляция регулярного выражения
re := regexp.MustCompile(`\d+`)

// Поиск совпадений
matches := re.FindAllString("abc123def456", -1)
fmt.Println(matches) // ["123", "456"]

// Замена
result := re.ReplaceAllString("abc123def456", "X")
fmt.Println(result) // "abcXdefX"

// Проверка соответствия
matched, _ := regexp.MatchString(`\d+`, "123")
fmt.Println(matched) // true

// Группы захвата
emailRe := regexp.MustCompile(`(\w+)@(\w+\.\w+)`)
matches := emailRe.FindStringSubmatch("user@example.com")
if len(matches) > 0 {
    fmt.Println("User:", matches[1])
    fmt.Println("Domain:", matches[2])
}
```

### Работа с путями

```go
import (
    "path/filepath"
    "os"
)

// Объединение путей
path := filepath.Join("dir", "subdir", "file.txt")

// Получение компонентов пути
dir := filepath.Dir(path)
file := filepath.Base(path)
ext := filepath.Ext(path)

// Абсолютный путь
absPath, _ := filepath.Abs("relative/path")

// Относительный путь
relPath, _ := filepath.Rel("/usr", "/usr/local/bin")

// Обход директории
filepath.Walk(".", func(path string, info os.FileInfo, err error) error {
    if err != nil {
        return err
    }
    fmt.Println(path)
    return nil
})
```

### Работа с URL

```go
import (
    "net/url"
    "fmt"
)

// Парсинг URL
u, err := url.Parse("https://example.com/path?key=value")
if err != nil {
    log.Fatal(err)
}

fmt.Println("Scheme:", u.Scheme)
fmt.Println("Host:", u.Host)
fmt.Println("Path:", u.Path)
fmt.Println("Query:", u.Query())

// Создание URL
u := &url.URL{
    Scheme:   "https",
    Host:     "example.com",
    Path:     "/path",
    RawQuery: "key=value",
}
fmt.Println(u.String())

// Кодирование/декодирование
encoded := url.QueryEscape("hello world")
decoded, _ := url.QueryDecode(encoded)
```

### Работа с процессами

```go
import (
    "os"
    "os/exec"
)

// Запуск команды
cmd := exec.Command("ls", "-la")
output, err := cmd.Output()
if err != nil {
    log.Fatal(err)
}
fmt.Println(string(output))

// Запуск с контекстом
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()

cmd := exec.CommandContext(ctx, "sleep", "10")
err := cmd.Run()

// Переменные окружения для команды
cmd := exec.Command("env")
cmd.Env = append(os.Environ(), "MY_VAR=value")
output, _ := cmd.Output()

// Получение PID текущего процесса
pid := os.Getpid()
fmt.Println("PID:", pid)
```

### Работа с сигналами

```go
import (
    "os"
    "os/signal"
    "syscall"
)

func main() {
    // Канал для сигналов
    sigChan := make(chan os.Signal, 1)
    signal.Notify(sigChan, os.Interrupt, syscall.SIGTERM)

    // Ожидание сигнала
    sig := <-sigChan
    fmt.Println("Received signal:", sig)

    // Graceful shutdown
    cleanup()
    os.Exit(0)
}
```

### Работа с буферами

```go
import (
    "bytes"
    "fmt"
)

// Создание буфера
var buf bytes.Buffer

// Запись в буфер
buf.WriteString("Hello")
buf.WriteByte(' ')
buf.WriteString("World")

// Чтение из буфера
data := buf.Bytes()
fmt.Println(string(data))

// Сброс буфера
buf.Reset()

// Преобразование в строку
str := buf.String()
```

### Работа с кодированием

```go
import (
    "encoding/base64"
    "encoding/hex"
)

// Base64 кодирование
data := []byte("Hello, World!")
encoded := base64.StdEncoding.EncodeToString(data)
decoded, _ := base64.StdEncoding.DecodeString(encoded)

// Hex кодирование
hexEncoded := hex.EncodeToString(data)
hexDecoded, _ := hex.DecodeString(hexEncoded)

// URL-safe Base64
urlEncoded := base64.URLEncoding.EncodeToString(data)
```

### Работа с архивами

```go
import (
    "archive/tar"
    "archive/zip"
    "compress/gzip"
    "io"
    "os"
)

// Создание ZIP архива
func createZip(filename string, files []string) error {
    zipFile, err := os.Create(filename)
    if err != nil {
        return err
    }
    defer zipFile.Close()

    zipWriter := zip.NewWriter(zipFile)
    defer zipWriter.Close()

    for _, file := range files {
        f, err := os.Open(file)
        if err != nil {
            return err
        }

        w, err := zipWriter.Create(file)
        if err != nil {
            f.Close()
            return err
        }

        io.Copy(w, f)
        f.Close()
    }

    return nil
}

// Распаковка ZIP
func extractZip(zipFile, destDir string) error {
    r, err := zip.OpenReader(zipFile)
    if err != nil {
        return err
    }
    defer r.Close()

    for _, f := range r.File {
        rc, err := f.Open()
        if err != nil {
            return err
        }

        path := filepath.Join(destDir, f.Name)
        if f.FileInfo().IsDir() {
            os.MkdirAll(path, f.FileInfo().Mode())
        } else {
            os.MkdirAll(filepath.Dir(path), 0755)
            out, err := os.Create(path)
            if err != nil {
                rc.Close()
                return err
            }

            io.Copy(out, rc)
            out.Close()
        }
        rc.Close()
    }

    return nil
}
```

### Работа с конфигурацией

```go
import (
    "encoding/json"
    "os"
)

type Config struct {
    Host     string `json:"host"`
    Port     int    `json:"port"`
    Database string `json:"database"`
}

func loadConfig(filename string) (*Config, error) {
    data, err := os.ReadFile(filename)
    if err != nil {
        return nil, err
    }

    var config Config
    if err := json.Unmarshal(data, &config); err != nil {
        return nil, err
    }

    return &config, nil
}

func saveConfig(filename string, config *Config) error {
    data, err := json.MarshalIndent(config, "", "  ")
    if err != nil {
        return err
    }

    return os.WriteFile(filename, data, 0644)
}
```

### Работа с логами

```go
import (
    "log"
    "os"
)

// Базовое логирование
log.Print("Info message")
log.Printf("Formatted: %s", "value")
log.Println("Message with newline")

// Уровни логирования
log.SetPrefix("[INFO] ")
log.SetFlags(log.Ldate | log.Ltime | log.Lshortfile)

// Логирование в файл
file, err := os.OpenFile("app.log", os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0666)
if err != nil {
    log.Fatal(err)
}
defer file.Close()

log.SetOutput(file)
log.Println("Log to file")

// Кастомный логгер
logger := log.New(os.Stdout, "[CUSTOM] ", log.LstdFlags)
logger.Println("Custom log message")
```

### Работа с метриками

```go
import (
    "sync"
    "time"
)

type Metrics struct {
    mu          sync.RWMutex
    requestCount int64
    errorCount   int64
    totalTime    time.Duration
}

func (m *Metrics) RecordRequest(duration time.Duration, err error) {
    m.mu.Lock()
    defer m.mu.Unlock()

    m.requestCount++
    m.totalTime += duration
    if err != nil {
        m.errorCount++
    }
}

func (m *Metrics) GetStats() (int64, int64, time.Duration) {
    m.mu.RLock()
    defer m.mu.RUnlock()

    return m.requestCount, m.errorCount, m.totalTime
}
```

### Работа с кэшем

```go
import (
    "sync"
    "time"
)

type CacheItem struct {
    Value      interface{}
    Expiration time.Time
}

type Cache struct {
    mu    sync.RWMutex
    items map[string]*CacheItem
}

func NewCache() *Cache {
    c := &Cache{
        items: make(map[string]*CacheItem),
    }
    go c.cleanup()
    return c
}

func (c *Cache) Set(key string, value interface{}, ttl time.Duration) {
    c.mu.Lock()
    defer c.mu.Unlock()

    c.items[key] = &CacheItem{
        Value:      value,
        Expiration: time.Now().Add(ttl),
    }
}

func (c *Cache) Get(key string) (interface{}, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()

    item, ok := c.items[key]
    if !ok {
        return nil, false
    }

    if time.Now().After(item.Expiration) {
        return nil, false
    }

    return item.Value, true
}

func (c *Cache) cleanup() {
    ticker := time.NewTicker(1 * time.Minute)
    defer ticker.Stop()

    for range ticker.C {
        c.mu.Lock()
        now := time.Now()
        for key, item := range c.items {
            if now.After(item.Expiration) {
                delete(c.items, key)
            }
        }
        c.mu.Unlock()
    }
}
```

### Работа с очередями

```go
import "container/list"

type Queue struct {
    items *list.List
    mu    sync.Mutex
}

func NewQueue() *Queue {
    return &Queue{
        items: list.New(),
    }
}

func (q *Queue) Enqueue(item interface{}) {
    q.mu.Lock()
    defer q.mu.Unlock()
    q.items.PushBack(item)
}

func (q *Queue) Dequeue() (interface{}, bool) {
    q.mu.Lock()
    defer q.mu.Unlock()

    if q.items.Len() == 0 {
        return nil, false
    }

    front := q.items.Front()
    q.items.Remove(front)
    return front.Value, true
}

func (q *Queue) Size() int {
    q.mu.Lock()
    defer q.mu.Unlock()
    return q.items.Len()
}
```

### Работа с пулами

```go
type Pool struct {
    items chan interface{}
    new   func() interface{}
}

func NewPool(size int, new func() interface{}) *Pool {
    p := &Pool{
        items: make(chan interface{}, size),
        new:   new,
    }

    for i := 0; i < size; i++ {
        p.items <- new()
    }

    return p
}

func (p *Pool) Get() interface{} {
    select {
    case item := <-p.items:
        return item
    default:
        return p.new()
    }
}

func (p *Pool) Put(item interface{}) {
    select {
    case p.items <- item:
    default:
        // Pool is full, discard
    }
}
```

### Работа с валидацией

```go
import (
    "errors"
    "strings"
    "unicode"
)

type Validator struct{}

func (v *Validator) ValidateEmail(email string) error {
    if !strings.Contains(email, "@") {
        return errors.New("invalid email format")
    }
    parts := strings.Split(email, "@")
    if len(parts) != 2 {
        return errors.New("invalid email format")
    }
    return nil
}

func (v *Validator) ValidatePassword(password string) error {
    if len(password) < 8 {
        return errors.New("password must be at least 8 characters")
    }

    hasUpper := false
    hasLower := false
    hasDigit := false

    for _, r := range password {
        if unicode.IsUpper(r) {
            hasUpper = true
        }
        if unicode.IsLower(r) {
            hasLower = true
        }
        if unicode.IsDigit(r) {
            hasDigit = true
        }
    }

    if !hasUpper || !hasLower || !hasDigit {
        return errors.New("password must contain uppercase, lowercase and digit")
    }

    return nil
}
```

### Работа с шаблонами

```go
import (
    "html/template"
    "os"
)

func renderTemplate(templateStr string, data interface{}) error {
    tmpl, err := template.New("test").Parse(templateStr)
    if err != nil {
        return err
    }

    return tmpl.Execute(os.Stdout, data)
}

// Использование
const templateStr = `
Hello, {{.Name}}!
You have {{.Count}} messages.
`

data := struct {
    Name  string
    Count int
}{
    Name:  "Alice",
    Count: 5,
}

renderTemplate(templateStr, data)
```

### Работа с метаданными

```go
import "reflect"

func getStructTags(v interface{}) map[string]map[string]string {
    result := make(map[string]map[string]string)
    typ := reflect.TypeOf(v)

    if typ.Kind() == reflect.Ptr {
        typ = typ.Elem()
    }

    for i := 0; i < typ.NumField(); i++ {
        field := typ.Field(i)
        tags := make(map[string]string)

        for _, tagName := range []string{"json", "xml", "db"} {
            if tagValue := field.Tag.Get(tagName); tagValue != "" {
                tags[tagName] = tagValue
            }
        }

        result[field.Name] = tags
    }

    return result
}
```

### Работа с контекстом запросов

```go
import (
    "context"
    "net/http"
    "time"
)

// Создание контекста с таймаутом
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()

// Передача контекста в запрос
req, _ := http.NewRequestWithContext(ctx, "GET", "https://api.example.com", nil)
client := &http.Client{}
resp, err := client.Do(req)

// Проверка отмены контекста
select {
case <-ctx.Done():
    fmt.Println("Request cancelled:", ctx.Err())
default:
    // Продолжение работы
}

// Контекст с дедлайном
deadline := time.Now().Add(10 * time.Second)
ctx, cancel = context.WithDeadline(context.Background(), deadline)
defer cancel()

// Контекст с отменой
ctx, cancel = context.WithCancel(context.Background())
go func() {
    time.Sleep(5 * time.Second)
    cancel() // Отмена через 5 секунд
}()
```

### Работа с middleware

```go
import "net/http"

// Middleware для логирования
func loggingMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()

        next.ServeHTTP(w, r)

        duration := time.Since(start)
        log.Printf("%s %s %v", r.Method, r.URL.Path, duration)
    })
}

// Middleware для аутентификации
func authMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        token := r.Header.Get("Authorization")
        if token == "" {
            http.Error(w, "Unauthorized", http.StatusUnauthorized)
            return
        }

        // Проверка токена
        if !isValidToken(token) {
            http.Error(w, "Invalid token", http.StatusUnauthorized)
            return
        }

        next.ServeHTTP(w, r)
    })
}

// Применение middleware
func setupRoutes() {
    mux := http.NewServeMux()
    mux.HandleFunc("/api", apiHandler)

    handler := loggingMiddleware(authMiddleware(mux))
    http.ListenAndServe(":8080", handler)
}
```

### Работа с каналами и селектами

```go
// Множественный выбор из каналов
select {
case msg1 := <-ch1:
    fmt.Println("Received from ch1:", msg1)
case msg2 := <-ch2:
    fmt.Println("Received from ch2:", msg2)
case <-time.After(1 * time.Second):
    fmt.Println("Timeout")
default:
    fmt.Println("No message ready")
}

// Отправка в канал с таймаутом
select {
case ch <- value:
    fmt.Println("Sent successfully")
case <-time.After(1 * time.Second):
    fmt.Println("Send timeout")
}

// Ожидание нескольких операций
done1 := make(chan bool)
done2 := make(chan bool)

go func() {
    // Работа 1
    done1 <- true
}()

go func() {
    // Работа 2
    done2 <- true
}()

// Ожидание обеих операций
<-done1
<-done2
fmt.Println("Both operations completed")
```

### Работа с пулами горутин

```go
func processWithPool(items []Item, numWorkers int) []Result {
    jobs := make(chan Item, len(items))
    results := make(chan Result, len(items))

    // Запуск воркеров
    var wg sync.WaitGroup
    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for item := range jobs {
                result := processItem(item)
                results <- result
            }
        }()
    }

    // Отправка заданий
    for _, item := range items {
        jobs <- item
    }
    close(jobs)

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

### Работа с фанаут/фанин паттерном

```go
// Fan-out: распределение работы между воркерами
func fanOut(input <-chan int, numWorkers int) []<-chan int {
    outputs := make([]<-chan int, numWorkers)

    for i := 0; i < numWorkers; i++ {
        output := make(chan int)
        outputs[i] = output

        go func(out chan<- int) {
            defer close(out)
            for n := range input {
                out <- process(n)
            }
        }(output)
    }

    return outputs
}

// Fan-in: объединение результатов
func fanIn(inputs ...<-chan int) <-chan int {
    output := make(chan int)
    var wg sync.WaitGroup

    for _, input := range inputs {
        wg.Add(1)
        go func(in <-chan int) {
            defer wg.Done()
            for n := range in {
                output <- n
            }
        }(input)
    }

    go func() {
        wg.Wait()
        close(output)
    }()

    return output
}
```

### Работа с pipeline

```go
// Pipeline для обработки данных
func pipeline(input <-chan int) <-chan int {
    // Stage 1: Умножение
    stage1 := make(chan int)
    go func() {
        defer close(stage1)
        for n := range input {
            stage1 <- n * 2
        }
    }()

    // Stage 2: Фильтрация
    stage2 := make(chan int)
    go func() {
        defer close(stage2)
        for n := range stage1 {
            if n > 10 {
                stage2 <- n
            }
        }
    }()

    return stage2
}

// Использование pipeline
input := make(chan int)
go func() {
    defer close(input)
    for i := 0; i < 10; i++ {
        input <- i
    }
}()

output := pipeline(input)
for result := range output {
    fmt.Println(result)
}
```

### Работа с rate limiting

```go
import "golang.org/x/time/rate"

// Создание rate limiter
limiter := rate.NewLimiter(rate.Limit(10), 20) // 10 запросов в секунду, burst 20

// Проверка лимита
if limiter.Allow() {
    // Выполнение запроса
    makeRequest()
} else {
    // Превышен лимит
    fmt.Println("Rate limit exceeded")
}

// Ожидание с учетом лимита
ctx := context.Background()
if err := limiter.Wait(ctx); err != nil {
    return err
}
makeRequest()
```

### Работа с circuit breaker

```go
type CircuitBreaker struct {
    maxFailures int
    timeout     time.Duration
    failures    int
    lastFailure time.Time
    state       string // "closed", "open", "half-open"
    mu          sync.Mutex
}

func NewCircuitBreaker(maxFailures int, timeout time.Duration) *CircuitBreaker {
    return &CircuitBreaker{
        maxFailures: maxFailures,
        timeout:     timeout,
        state:       "closed",
    }
}

func (cb *CircuitBreaker) Call(fn func() error) error {
    cb.mu.Lock()

    if cb.state == "open" {
        if time.Since(cb.lastFailure) > cb.timeout {
            cb.state = "half-open"
        } else {
            cb.mu.Unlock()
            return fmt.Errorf("circuit breaker is open")
        }
    }

    cb.mu.Unlock()

    err := fn()

    cb.mu.Lock()
    defer cb.mu.Unlock()

    if err != nil {
        cb.failures++
        cb.lastFailure = time.Now()
        if cb.failures >= cb.maxFailures {
            cb.state = "open"
        }
        return err
    }

    if cb.state == "half-open" {
        cb.state = "closed"
    }
    cb.failures = 0

    return nil
}
```

### Работа с retry механизмом

```go
func retry(attempts int, delay time.Duration, fn func() error) error {
    var lastErr error

    for i := 0; i < attempts; i++ {
        if err := fn(); err == nil {
            return nil
        }

        lastErr = err
        if i < attempts-1 {
            time.Sleep(delay)
            delay *= 2 // Exponential backoff
        }
    }

    return fmt.Errorf("failed after %d attempts: %w", attempts, lastErr)
}

// Использование
err := retry(3, time.Second, func() error {
    return makeRequest()
})
```

### Работа с observability

```go
import (
    "github.com/prometheus/client_golang/prometheus"
    "github.com/prometheus/client_golang/prometheus/promhttp"
)

var (
    requestsTotal = prometheus.NewCounterVec(
        prometheus.CounterOpts{
            Name: "http_requests_total",
            Help: "Total number of HTTP requests",
        },
        []string{"method", "status"},
    )

    requestDuration = prometheus.NewHistogramVec(
        prometheus.HistogramOpts{
            Name: "http_request_duration_seconds",
            Help: "HTTP request duration",
        },
        []string{"method"},
    )
)

func init() {
    prometheus.MustRegister(requestsTotal)
    prometheus.MustRegister(requestDuration)
}

func metricsMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()

        next.ServeHTTP(w, r)

        duration := time.Since(start).Seconds()
        requestsTotal.WithLabelValues(r.Method, "200").Inc()
        requestDuration.WithLabelValues(r.Method).Observe(duration)
    })
}

// Экспорт метрик
func setupMetrics() {
    http.Handle("/metrics", promhttp.Handler())
}
```

### Работа с graceful shutdown

```go
func gracefulShutdown(server *http.Server) {
    sigChan := make(chan os.Signal, 1)
    signal.Notify(sigChan, os.Interrupt, syscall.SIGTERM)

    <-sigChan
    log.Println("Shutting down server...")

    ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
    defer cancel()

    if err := server.Shutdown(ctx); err != nil {
        log.Fatal("Server forced to shutdown:", err)
    }

    log.Println("Server exited")
}
```

### Работа с валидацией данных

```go
import "github.com/go-playground/validator/v10"

type User struct {
    Email    string `validate:"required,email"`
    Age      int    `validate:"required,min=18,max=100"`
    Password string `validate:"required,min=8"`
}

func validateUser(user User) error {
    validate := validator.New()
    return validate.Struct(user)
}

// Кастомная валидация
func validateCustom(fl validator.FieldLevel) bool {
    value := fl.Field().String()
    return len(value) > 5
}

validate.RegisterValidation("custom", validateCustom)
```

### Работа с миграциями базы данных

```go
type Migration struct {
    Version int
    Up      func(*sql.DB) error
    Down    func(*sql.DB) error
}

func runMigrations(db *sql.DB, migrations []Migration) error {
    // Создание таблицы миграций
    db.Exec(`CREATE TABLE IF NOT EXISTS migrations (
        version INTEGER PRIMARY KEY
    )`)

    for _, migration := range migrations {
        var exists bool
        err := db.QueryRow("SELECT EXISTS(SELECT 1 FROM migrations WHERE version = $1)",
            migration.Version).Scan(&exists)
        if err != nil {
            return err
        }

        if !exists {
            if err := migration.Up(db); err != nil {
                return err
            }

            db.Exec("INSERT INTO migrations (version) VALUES ($1)", migration.Version)
        }
    }

    return nil
}
```

### Работа с транзакциями

```go
func transferMoney(db *sql.DB, from, to string, amount float64) error {
    tx, err := db.Begin()
    if err != nil {
        return err
    }
    defer tx.Rollback()

    // Снятие средств
    _, err = tx.Exec("UPDATE accounts SET balance = balance - $1 WHERE id = $2",
        amount, from)
    if err != nil {
        return err
    }

    // Пополнение счета
    _, err = tx.Exec("UPDATE accounts SET balance = balance + $1 WHERE id = $2",
        amount, to)
    if err != nil {
        return err
    }

    return tx.Commit()
}
```

### Работа с конфигурацией через переменные окружения

```go
import "github.com/spf13/viper"

func loadConfig() (*Config, error) {
    viper.SetConfigName("config")
    viper.SetConfigType("yaml")
    viper.AddConfigPath(".")

    viper.AutomaticEnv()
    viper.SetEnvPrefix("APP")

    if err := viper.ReadInConfig(); err != nil {
        if _, ok := err.(viper.ConfigFileNotFoundError); !ok {
            return nil, err
        }
    }

    var config Config
    if err := viper.Unmarshal(&config); err != nil {
        return nil, err
    }

    return &config, nil
}
```

### Работа с версионированием API

```go
func versionMiddleware(version string) func(http.Handler) http.Handler {
    return func(next http.Handler) http.Handler {
        return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
            w.Header().Set("API-Version", version)
            next.ServeHTTP(w, r)
        })
    }
}

func apiV1Handler(w http.ResponseWriter, r *http.Request) {
    // API v1 логика
}

func apiV2Handler(w http.ResponseWriter, r *http.Request) {
    // API v2 логика
}

func setupVersionedRoutes() {
    mux := http.NewServeMux()

    v1 := http.NewServeMux()
    v1.HandleFunc("/users", apiV1Handler)
    mux.Handle("/v1/", http.StripPrefix("/v1", v1))

    v2 := http.NewServeMux()
    v2.HandleFunc("/users", apiV2Handler)
    mux.Handle("/v2/", http.StripPrefix("/v2", v2))
}
```

### Работа с кэшированием HTTP ответов

```go
type CacheMiddleware struct {
    cache map[string]CacheEntry
    mu    sync.RWMutex
    ttl   time.Duration
}

type CacheEntry struct {
    Data      []byte
    ExpiresAt time.Time
}

func (cm *CacheMiddleware) Middleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        if r.Method != "GET" {
            next.ServeHTTP(w, r)
            return
        }

        cm.mu.RLock()
        entry, ok := cm.cache[r.URL.Path]
        cm.mu.RUnlock()

        if ok && time.Now().Before(entry.ExpiresAt) {
            w.Header().Set("X-Cache", "HIT")
            w.Write(entry.Data)
            return
        }

        recorder := &responseRecorder{
            ResponseWriter: w,
            body:           &bytes.Buffer{},
        }

        next.ServeHTTP(recorder, r)

        if recorder.statusCode == 200 {
            cm.mu.Lock()
            cm.cache[r.URL.Path] = CacheEntry{
                Data:      recorder.body.Bytes(),
                ExpiresAt: time.Now().Add(cm.ttl),
            }
            cm.mu.Unlock()
        }
    })
}
```

### Работа с health checks

```go
func healthCheckHandler(w http.ResponseWriter, r *http.Request) {
    checks := map[string]bool{
        "database": checkDatabase(),
        "cache":    checkCache(),
        "external": checkExternalAPI(),
    }

    allHealthy := true
    for name, healthy := range checks {
        if !healthy {
            allHealthy = false
            log.Printf("Health check failed: %s", name)
        }
    }

    if allHealthy {
        w.WriteHeader(http.StatusOK)
        json.NewEncoder(w).Encode(map[string]interface{}{
            "status": "healthy",
            "checks": checks,
        })
    } else {
        w.WriteHeader(http.StatusServiceUnavailable)
        json.NewEncoder(w).Encode(map[string]interface{}{
            "status": "unhealthy",
            "checks": checks,
        })
    }
}

func checkDatabase() bool {
    // Проверка подключения к БД
    ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
    defer cancel()

    err := db.PingContext(ctx)
    return err == nil
}
```

### Работа с feature flags

```go
type FeatureFlags struct {
    flags map[string]bool
    mu    sync.RWMutex
}

func NewFeatureFlags() *FeatureFlags {
    return &FeatureFlags{
        flags: make(map[string]bool),
    }
}

func (ff *FeatureFlags) IsEnabled(flag string) bool {
    ff.mu.RLock()
    defer ff.mu.RUnlock()
    return ff.flags[flag]
}

func (ff *FeatureFlags) Set(flag string, enabled bool) {
    ff.mu.Lock()
    defer ff.mu.Unlock()
    ff.flags[flag] = enabled
}

// Использование
flags := NewFeatureFlags()
flags.Set("new_feature", true)

if flags.IsEnabled("new_feature") {
    // Новая функциональность
} else {
    // Старая функциональность
}
```

### Работа с сервисной архитектурой

```go
// Базовый сервис
type Service interface {
    Start(context.Context) error
    Stop(context.Context) error
    Health() error
}

// Реализация сервиса
type UserService struct {
    db     *sql.DB
    cache  *Cache
    stopCh chan struct{}
}

func NewUserService(db *sql.DB, cache *Cache) *UserService {
    return &UserService{
        db:     db,
        cache:  cache,
        stopCh: make(chan struct{}),
    }
}

func (s *UserService) Start(ctx context.Context) error {
    // Инициализация сервиса
    go s.backgroundWorker(ctx)
    return nil
}

func (s *UserService) Stop(ctx context.Context) error {
    close(s.stopCh)
    return nil
}

func (s *UserService) Health() error {
    return s.db.Ping()
}

func (s *UserService) backgroundWorker(ctx context.Context) {
    ticker := time.NewTicker(1 * time.Minute)
    defer ticker.Stop()

    for {
        select {
        case <-ctx.Done():
            return
        case <-s.stopCh:
            return
        case <-ticker.C:
            s.performPeriodicTask()
        }
    }
}

// Менеджер сервисов
type ServiceManager struct {
    services []Service
    mu       sync.Mutex
}

func (sm *ServiceManager) Register(service Service) {
    sm.mu.Lock()
    defer sm.mu.Unlock()
    sm.services = append(sm.services, service)
}

func (sm *ServiceManager) StartAll(ctx context.Context) error {
    for _, service := range sm.services {
        if err := service.Start(ctx); err != nil {
            return err
        }
    }
    return nil
}

func (sm *ServiceManager) StopAll(ctx context.Context) error {
    for _, service := range sm.services {
        if err := service.Stop(ctx); err != nil {
            return err
        }
    }
    return nil
}
```

### Работа с dependency injection

```go
// Контейнер зависимостей
type Container struct {
    services map[string]interface{}
    mu       sync.RWMutex
}

func NewContainer() *Container {
    return &Container{
        services: make(map[string]interface{}),
    }
}

func (c *Container) Register(name string, service interface{}) {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.services[name] = service
}

func (c *Container) Get(name string) (interface{}, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    service, ok := c.services[name]
    return service, ok
}

func (c *Container) MustGet(name string) interface{} {
    service, ok := c.Get(name)
    if !ok {
        panic(fmt.Sprintf("service %s not found", name))
    }
    return service
}

// Использование
container := NewContainer()
container.Register("db", db)
container.Register("cache", cache)
container.Register("logger", logger)

db := container.MustGet("db").(*sql.DB)
```

### Работа с event-driven архитектурой

```go
type Event interface {
    Type() string
    Payload() interface{}
}

type EventHandler func(Event) error

type EventBus struct {
    handlers map[string][]EventHandler
    mu       sync.RWMutex
}

func NewEventBus() *EventBus {
    return &EventBus{
        handlers: make(map[string][]EventHandler),
    }
}

func (eb *EventBus) Subscribe(eventType string, handler EventHandler) {
    eb.mu.Lock()
    defer eb.mu.Unlock()
    eb.handlers[eventType] = append(eb.handlers[eventType], handler)
}

func (eb *EventBus) Publish(event Event) error {
    eb.mu.RLock()
    handlers := eb.handlers[event.Type()]
    eb.mu.RUnlock()

    for _, handler := range handlers {
        if err := handler(event); err != nil {
            return err
        }
    }
    return nil
}

// Пример события
type UserCreatedEvent struct {
    UserID int
    Email  string
}

func (e UserCreatedEvent) Type() string {
    return "user.created"
}

func (e UserCreatedEvent) Payload() interface{} {
    return e
}

// Использование
eventBus := NewEventBus()
eventBus.Subscribe("user.created", func(e Event) error {
    event := e.(UserCreatedEvent)
    log.Printf("User created: %d", event.UserID)
    return nil
})

eventBus.Publish(UserCreatedEvent{
    UserID: 123,
    Email:  "user@example.com",
})
```

### Работа с CQRS паттерном

```go
// Command
type Command interface {
    Type() string
}

type CreateUserCommand struct {
    Email string
    Name  string
}

func (c CreateUserCommand) Type() string {
    return "create_user"
}

// Query
type Query interface {
    Type() string
}

type GetUserQuery struct {
    UserID int
}

func (q GetUserQuery) Type() string {
    return "get_user"
}

// Command Handler
type CommandHandler interface {
    Handle(Command) error
}

type CreateUserHandler struct {
    db *sql.DB
}

func (h *CreateUserHandler) Handle(cmd Command) error {
    createCmd := cmd.(CreateUserCommand)
    _, err := h.db.Exec("INSERT INTO users (email, name) VALUES ($1, $2)",
        createCmd.Email, createCmd.Name)
    return err
}

// Query Handler
type QueryHandler interface {
    Handle(Query) (interface{}, error)
}

type GetUserHandler struct {
    db *sql.DB
}

func (h *GetUserHandler) Handle(q Query) (interface{}, error) {
    query := q.(GetUserQuery)
    var user User
    err := h.db.QueryRow("SELECT id, email, name FROM users WHERE id = $1",
        query.UserID).Scan(&user.ID, &user.Email, &user.Name)
    return user, err
}
```

### Работа с repository паттерном

```go
type UserRepository interface {
    Create(ctx context.Context, user *User) error
    GetByID(ctx context.Context, id int) (*User, error)
    GetByEmail(ctx context.Context, email string) (*User, error)
    Update(ctx context.Context, user *User) error
    Delete(ctx context.Context, id int) error
}

type userRepository struct {
    db *sql.DB
}

func NewUserRepository(db *sql.DB) UserRepository {
    return &userRepository{db: db}
}

func (r *userRepository) Create(ctx context.Context, user *User) error {
    query := `INSERT INTO users (email, name) VALUES ($1, $2) RETURNING id`
    return r.db.QueryRowContext(ctx, query, user.Email, user.Name).Scan(&user.ID)
}

func (r *userRepository) GetByID(ctx context.Context, id int) (*User, error) {
    var user User
    query := `SELECT id, email, name FROM users WHERE id = $1`
    err := r.db.QueryRowContext(ctx, query, id).Scan(&user.ID, &user.Email, &user.Name)
    if err != nil {
        return nil, err
    }
    return &user, nil
}

func (r *userRepository) GetByEmail(ctx context.Context, email string) (*User, error) {
    var user User
    query := `SELECT id, email, name FROM users WHERE email = $1`
    err := r.db.QueryRowContext(ctx, query, email).Scan(&user.ID, &user.Email, &user.Name)
    if err != nil {
        return nil, err
    }
    return &user, nil
}

func (r *userRepository) Update(ctx context.Context, user *User) error {
    query := `UPDATE users SET email = $1, name = $2 WHERE id = $3`
    _, err := r.db.ExecContext(ctx, query, user.Email, user.Name, user.ID)
    return err
}

func (r *userRepository) Delete(ctx context.Context, id int) error {
    query := `DELETE FROM users WHERE id = $1`
    _, err := r.db.ExecContext(ctx, query, id)
    return err
}
```

### Работа с unit of work паттерном

```go
type UnitOfWork interface {
    Users() UserRepository
    Orders() OrderRepository
    Commit() error
    Rollback() error
}

type unitOfWork struct {
    db    *sql.DB
    tx    *sql.Tx
    users UserRepository
}

func NewUnitOfWork(db *sql.DB) (UnitOfWork, error) {
    tx, err := db.Begin()
    if err != nil {
        return nil, err
    }

    return &unitOfWork{
        db:    db,
        tx:    tx,
        users: NewUserRepository(db),
    }, nil
}

func (uow *unitOfWork) Users() UserRepository {
    return uow.users
}

func (uow *unitOfWork) Commit() error {
    return uow.tx.Commit()
}

func (uow *unitOfWork) Rollback() error {
    return uow.tx.Rollback()
}

// Использование
func createUserWithOrder(db *sql.DB, user *User, order *Order) error {
    uow, err := NewUnitOfWork(db)
    if err != nil {
        return err
    }
    defer uow.Rollback()

    if err := uow.Users().Create(context.Background(), user); err != nil {
        return err
    }

    if err := uow.Orders().Create(context.Background(), order); err != nil {
        return err
    }

    return uow.Commit()
}
```

### Работа с factory паттерном

```go
type DatabaseFactory interface {
    Create() (*sql.DB, error)
}

type PostgreSQLFactory struct {
    connectionString string
}

func NewPostgreSQLFactory(connectionString string) *PostgreSQLFactory {
    return &PostgreSQLFactory{
        connectionString: connectionString,
    }
}

func (f *PostgreSQLFactory) Create() (*sql.DB, error) {
    return sql.Open("postgres", f.connectionString)
}

type MySQLFactory struct {
    connectionString string
}

func NewMySQLFactory(connectionString string) *MySQLFactory {
    return &MySQLFactory{
        connectionString: connectionString,
    }
}

func (f *MySQLFactory) Create() (*sql.DB, error) {
    return sql.Open("mysql", f.connectionString)
}

// Фабрика фабрик
func NewDatabaseFactory(dbType, connectionString string) (DatabaseFactory, error) {
    switch dbType {
    case "postgres":
        return NewPostgreSQLFactory(connectionString), nil
    case "mysql":
        return NewMySQLFactory(connectionString), nil
    default:
        return nil, fmt.Errorf("unsupported database type: %s", dbType)
    }
}
```

### Работа с strategy паттерном

```go
type PaymentStrategy interface {
    Pay(amount float64) error
}

type CreditCardStrategy struct {
    cardNumber string
}

func (s *CreditCardStrategy) Pay(amount float64) error {
    // Логика оплаты картой
    log.Printf("Paying %.2f with credit card", amount)
    return nil
}

type PayPalStrategy struct {
    email string
}

func (s *PayPalStrategy) Pay(amount float64) error {
    // Логика оплаты через PayPal
    log.Printf("Paying %.2f with PayPal", amount)
    return nil
}

type PaymentProcessor struct {
    strategy PaymentStrategy
}

func NewPaymentProcessor(strategy PaymentStrategy) *PaymentProcessor {
    return &PaymentProcessor{strategy: strategy}
}

func (p *PaymentProcessor) ProcessPayment(amount float64) error {
    return p.strategy.Pay(amount)
}

// Использование
processor := NewPaymentProcessor(&CreditCardStrategy{cardNumber: "1234"})
processor.ProcessPayment(100.0)
```

### Работа с observer паттерном

```go
type Observer interface {
    Update(event string, data interface{})
}

type Subject struct {
    observers []Observer
    mu        sync.RWMutex
}

func NewSubject() *Subject {
    return &Subject{
        observers: make([]Observer, 0),
    }
}

func (s *Subject) Attach(observer Observer) {
    s.mu.Lock()
    defer s.mu.Unlock()
    s.observers = append(s.observers, observer)
}

func (s *Subject) Detach(observer Observer) {
    s.mu.Lock()
    defer s.mu.Unlock()
    for i, obs := range s.observers {
        if obs == observer {
            s.observers = append(s.observers[:i], s.observers[i+1:]...)
            break
        }
    }
}

func (s *Subject) Notify(event string, data interface{}) {
    s.mu.RLock()
    observers := make([]Observer, len(s.observers))
    copy(observers, s.observers)
    s.mu.RUnlock()

    for _, observer := range observers {
        observer.Update(event, data)
    }
}

// Реализация наблюдателя
type LogObserver struct{}

func (o *LogObserver) Update(event string, data interface{}) {
    log.Printf("Event: %s, Data: %v", event, data)
}
```

### Работа с builder паттерном

```go
type QueryBuilder struct {
    table   string
    selects []string
    wheres  []string
    args    []interface{}
}

func NewQueryBuilder(table string) *QueryBuilder {
    return &QueryBuilder{
        table:   table,
        selects: []string{"*"},
        wheres:  []string{},
        args:    []interface{}{},
    }
}

func (qb *QueryBuilder) Select(columns ...string) *QueryBuilder {
    qb.selects = columns
    return qb
}

func (qb *QueryBuilder) Where(condition string, value interface{}) *QueryBuilder {
    qb.wheres = append(qb.wheres, condition)
    qb.args = append(qb.args, value)
    return qb
}

func (qb *QueryBuilder) Build() (string, []interface{}) {
    query := fmt.Sprintf("SELECT %s FROM %s",
        strings.Join(qb.selects, ", "), qb.table)

    if len(qb.wheres) > 0 {
        query += " WHERE " + strings.Join(qb.wheres, " AND ")
    }

    return query, qb.args
}

// Использование
builder := NewQueryBuilder("users").
    Select("id", "email", "name").
    Where("age > ?", 18).
    Where("status = ?", "active")

query, args := builder.Build()
```

### Работа с chain of responsibility

```go
type Handler interface {
    Handle(request interface{}) error
    SetNext(Handler)
}

type BaseHandler struct {
    next Handler
}

func (h *BaseHandler) SetNext(next Handler) {
    h.next = next
}

func (h *BaseHandler) Handle(request interface{}) error {
    if h.next != nil {
        return h.next.Handle(request)
    }
    return nil
}

type AuthHandler struct {
    BaseHandler
}

func (h *AuthHandler) Handle(request interface{}) error {
    // Проверка аутентификации
    if !isAuthenticated(request) {
        return fmt.Errorf("unauthorized")
    }
    return h.BaseHandler.Handle(request)
}

type ValidationHandler struct {
    BaseHandler
}

func (h *ValidationHandler) Handle(request interface{}) error {
    // Валидация
    if err := validate(request); err != nil {
        return err
    }
    return h.BaseHandler.Handle(request)
}

type LoggingHandler struct {
    BaseHandler
}

func (h *LoggingHandler) Handle(request interface{}) error {
    log.Printf("Handling request: %v", request)
    err := h.BaseHandler.Handle(request)
    if err != nil {
        log.Printf("Error handling request: %v", err)
    }
    return err
}

// Использование
chain := &AuthHandler{}
chain.SetNext(&ValidationHandler{})
chain.SetNext(&LoggingHandler{})

err := chain.Handle(request)
```

### Работа с adapter паттерном

```go
// Старый интерфейс
type OldService interface {
    DoSomething(data string) string
}

// Новый интерфейс
type NewService interface {
    Process(input []byte) ([]byte, error)
}

// Адаптер
type ServiceAdapter struct {
    oldService OldService
}

func NewServiceAdapter(oldService OldService) NewService {
    return &ServiceAdapter{oldService: oldService}
}

func (a *ServiceAdapter) Process(input []byte) ([]byte, error) {
    result := a.oldService.DoSomething(string(input))
    return []byte(result), nil
}

// Использование
oldSvc := &OldServiceImpl{}
newSvc := NewServiceAdapter(oldSvc)
output, _ := newSvc.Process([]byte("input"))
```

### Работа с decorator паттерном

```go
type Component interface {
    Operation() string
}

type ConcreteComponent struct{}

func (c *ConcreteComponent) Operation() string {
    return "ConcreteComponent"
}

type Decorator struct {
    component Component
}

func (d *Decorator) Operation() string {
    return d.component.Operation()
}

type LoggingDecorator struct {
    Decorator
}

func (d *LoggingDecorator) Operation() string {
    log.Println("Before operation")
    result := d.Decorator.Operation()
    log.Println("After operation:", result)
    return result
}

type CachingDecorator struct {
    Decorator
    cache map[string]string
}

func (d *CachingDecorator) Operation() string {
    key := "operation"
    if cached, ok := d.cache[key]; ok {
        return cached
    }

    result := d.Decorator.Operation()
    d.cache[key] = result
    return result
}

// Использование
component := &ConcreteComponent{}
decorated := &LoggingDecorator{
    Decorator: Decorator{component: component},
}
cached := &CachingDecorator{
    Decorator: Decorator{component: decorated},
    cache:     make(map[string]string),
}

result := cached.Operation()
```

### Работа с middleware цепочкой

```go
type Middleware func(http.Handler) http.Handler

func Chain(middlewares ...Middleware) Middleware {
    return func(next http.Handler) http.Handler {
        for i := len(middlewares) - 1; i >= 0; i-- {
            next = middlewares[i](next)
        }
        return next
    }
}

func authMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        token := r.Header.Get("Authorization")
        if token == "" {
            http.Error(w, "Unauthorized", http.StatusUnauthorized)
            return
        }
        next.ServeHTTP(w, r)
    })
}

func loggingMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        next.ServeHTTP(w, r)
        log.Printf("%s %s %v", r.Method, r.URL.Path, time.Since(start))
    })
}

func corsMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.Header().Set("Access-Control-Allow-Origin", "*")
        next.ServeHTTP(w, r)
    })
}

// Использование
handler := Chain(
    loggingMiddleware,
    corsMiddleware,
    authMiddleware,
)(myHandler)
```

### Работа с валидацией запросов

```go
type RequestValidator struct {
    rules map[string]func(interface{}) error
}

func NewRequestValidator() *RequestValidator {
    return &RequestValidator{
        rules: make(map[string]func(interface{}) error),
    }
}

func (v *RequestValidator) AddRule(field string, rule func(interface{}) error) {
    v.rules[field] = rule
}

func (v *RequestValidator) Validate(data map[string]interface{}) error {
    for field, rule := range v.rules {
        value, ok := data[field]
        if !ok {
            continue
        }
        if err := rule(value); err != nil {
            return fmt.Errorf("field %s: %w", field, err)
        }
    }
    return nil
}

// Примеры правил
func required(value interface{}) error {
    if value == nil {
        return fmt.Errorf("required")
    }
    if str, ok := value.(string); ok && str == "" {
        return fmt.Errorf("required")
    }
    return nil
}

func minLength(min int) func(interface{}) error {
    return func(value interface{}) error {
        str, ok := value.(string)
        if !ok {
            return fmt.Errorf("must be string")
        }
        if len(str) < min {
            return fmt.Errorf("must be at least %d characters", min)
        }
        return nil
    }
}

// Использование
validator := NewRequestValidator()
validator.AddRule("email", required)
validator.AddRule("email", func(v interface{}) error {
    email := v.(string)
    if !strings.Contains(email, "@") {
        return fmt.Errorf("invalid email format")
    }
    return nil
})
validator.AddRule("password", required)
validator.AddRule("password", minLength(8))

err := validator.Validate(map[string]interface{}{
    "email":    "user@example.com",
    "password": "secret123",
})
```

### Работа с пагинацией

```go
type Paginator struct {
    Page     int
    PageSize int
    Total    int
}

func NewPaginator(page, pageSize int) *Paginator {
    if page < 1 {
        page = 1
    }
    if pageSize < 1 {
        pageSize = 10
    }
    return &Paginator{
        Page:     page,
        PageSize: pageSize,
    }
}

func (p *Paginator) Offset() int {
    return (p.Page - 1) * p.PageSize
}

func (p *Paginator) Limit() int {
    return p.PageSize
}

func (p *Paginator) TotalPages() int {
    if p.Total == 0 {
        return 0
    }
    return (p.Total + p.PageSize - 1) / p.PageSize
}

func (p *Paginator) HasNext() bool {
    return p.Page < p.TotalPages()
}

func (p *Paginator) HasPrev() bool {
    return p.Page > 1
}

func (p *Paginator) GetItems(db *sql.DB, query string) ([]interface{}, error) {
    offset := p.Offset()
    limit := p.Limit()

    paginatedQuery := fmt.Sprintf("%s LIMIT %d OFFSET %d", query, limit, offset)
    rows, err := db.Query(paginatedQuery)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var items []interface{}
    for rows.Next() {
        var item interface{}
        if err := rows.Scan(&item); err != nil {
            return nil, err
        }
        items = append(items, item)
    }

    return items, nil
}
```

### Работа с сортировкой

```go
type SortOrder string

const (
    SortASC  SortOrder = "ASC"
    SortDESC SortOrder = "DESC"
)

type Sorter struct {
    Field string
    Order SortOrder
}

func NewSorter(field string, order SortOrder) *Sorter {
    return &Sorter{
        Field: field,
        Order: order,
    }
}

func (s *Sorter) Apply(query string) string {
    allowedFields := map[string]bool{
        "id":    true,
        "name":  true,
        "email": true,
        "created_at": true,
    }

    if !allowedFields[s.Field] {
        return query
    }

    return fmt.Sprintf("%s ORDER BY %s %s", query, s.Field, s.Order)
}
```

### Работа с фильтрацией

```go
type Filter struct {
    Field    string
    Operator string
    Value    interface{}
}

type FilterBuilder struct {
    filters []Filter
    args    []interface{}
}

func NewFilterBuilder() *FilterBuilder {
    return &FilterBuilder{
        filters: make([]Filter, 0),
        args:    make([]interface{}, 0),
    }
}

func (fb *FilterBuilder) AddFilter(field, operator string, value interface{}) *FilterBuilder {
    fb.filters = append(fb.filters, Filter{
        Field:    field,
        Operator: operator,
        Value:    value,
    })
    return fb
}

func (fb *FilterBuilder) Build() (string, []interface{}) {
    if len(fb.filters) == 0 {
        return "", nil
    }

    conditions := make([]string, 0, len(fb.filters))
    args := make([]interface{}, 0)

    for _, filter := range fb.filters {
        condition := fmt.Sprintf("%s %s ?", filter.Field, filter.Operator)
        conditions = append(conditions, condition)
        args = append(args, filter.Value)
    }

    whereClause := "WHERE " + strings.Join(conditions, " AND ")
    return whereClause, args
}

// Использование
builder := NewFilterBuilder()
builder.AddFilter("age", ">=", 18).
    AddFilter("status", "=", "active")

where, args := builder.Build()
```

### Работа с batch обработкой

```go
func ProcessBatch[T any](items []T, batchSize int, processor func([]T) error) error {
    for i := 0; i < len(items); i += batchSize {
        end := i + batchSize
        if end > len(items) {
            end = len(items)
        }

        batch := items[i:end]
        if err := processor(batch); err != nil {
            return fmt.Errorf("batch %d: %w", i/batchSize, err)
        }
    }
    return nil
}

// Использование
users := []User{...}
err := ProcessBatch(users, 100, func(batch []User) error {
    // Обработка батча
    return nil
})
```

### Работа с graceful degradation

```go
type Service struct {
    primary   Service
    fallback  Service
    available bool
}

func NewServiceWithFallback(primary, fallback Service) *Service {
    return &Service{
        primary:   primary,
        fallback:  fallback,
        available: true,
    }
}

func (s *Service) Process(ctx context.Context, data interface{}) (interface{}, error) {
    if s.available {
        result, err := s.primary.Process(ctx, data)
        if err != nil {
            s.available = false
            // Пробуем fallback
            return s.fallback.Process(ctx, data)
        }
        return result, nil
    }

    return s.fallback.Process(ctx, data)
}

func (s *Service) HealthCheck(ctx context.Context) error {
    err := s.primary.HealthCheck(ctx)
    if err == nil {
        s.available = true
    }
    return err
}
```

### Работа с retry с exponential backoff

```go
type RetryConfig struct {
    MaxAttempts int
    InitialDelay time.Duration
    MaxDelay     time.Duration
    Multiplier   float64
}

func DefaultRetryConfig() *RetryConfig {
    return &RetryConfig{
        MaxAttempts: 3,
        InitialDelay: 100 * time.Millisecond,
        MaxDelay:     5 * time.Second,
        Multiplier:   2.0,
    }
}

func RetryWithBackoff(ctx context.Context, config *RetryConfig, fn func() error) error {
    delay := config.InitialDelay
    var lastErr error

    for attempt := 0; attempt < config.MaxAttempts; attempt++ {
        if err := ctx.Err(); err != nil {
            return err
        }

        err := fn()
        if err == nil {
            return nil
        }

        lastErr = err

        if attempt < config.MaxAttempts-1 {
            select {
            case <-ctx.Done():
                return ctx.Err()
            case <-time.After(delay):
                delay = time.Duration(float64(delay) * config.Multiplier)
                if delay > config.MaxDelay {
                    delay = config.MaxDelay
                }
            }
        }
    }

    return fmt.Errorf("failed after %d attempts: %w", config.MaxAttempts, lastErr)
}
```

### Работа с rate limiter per user

```go
type UserRateLimiter struct {
    limiters map[string]*rate.Limiter
    mu       sync.RWMutex
    rate     rate.Limit
    burst    int
}

func NewUserRateLimiter(rps float64, burst int) *UserRateLimiter {
    return &UserRateLimiter{
        limiters: make(map[string]*rate.Limiter),
        rate:     rate.Limit(rps),
        burst:    burst,
    }
}

func (url *UserRateLimiter) getLimiter(userID string) *rate.Limiter {
    url.mu.RLock()
    limiter, ok := url.limiters[userID]
    url.mu.RUnlock()

    if !ok {
        url.mu.Lock()
        limiter, ok = url.limiters[userID]
        if !ok {
            limiter = rate.NewLimiter(url.rate, url.burst)
            url.limiters[userID] = limiter
        }
        url.mu.Unlock()
    }

    return limiter
}

func (url *UserRateLimiter) Allow(userID string) bool {
    return url.getLimiter(userID).Allow()
}
```

### Работа с distributed tracing

```go
type Trace struct {
    TraceID   string
    SpanID    string
    ParentID  string
    Operation string
    StartTime time.Time
    EndTime   time.Time
    Tags      map[string]string
}

type Tracer struct {
    traces map[string]*Trace
    mu     sync.RWMutex
}

func NewTracer() *Tracer {
    return &Tracer{
        traces: make(map[string]*Trace),
    }
}

func (t *Tracer) StartSpan(operation, parentSpanID string) *Trace {
    spanID := generateID()
    traceID := spanID
    if parentSpanID != "" {
        t.mu.RLock()
        if parent, ok := t.traces[parentSpanID]; ok {
            traceID = parent.TraceID
        }
        t.mu.RUnlock()
    }

    trace := &Trace{
        TraceID:   traceID,
        SpanID:    spanID,
        ParentID:  parentSpanID,
        Operation: operation,
        StartTime: time.Now(),
        Tags:      make(map[string]string),
    }

    t.mu.Lock()
    t.traces[spanID] = trace
    t.mu.Unlock()

    return trace
}

func (t *Tracer) FinishSpan(spanID string) {
    t.mu.Lock()
    defer t.mu.Unlock()

    if trace, ok := t.traces[spanID]; ok {
        trace.EndTime = time.Now()
    }
}

func generateID() string {
    b := make([]byte, 16)
    rand.Read(b)
    return hex.EncodeToString(b)
}
```

### Работа с structured logging

```go
type LogLevel int

const (
    LogLevelDebug LogLevel = iota
    LogLevelInfo
    LogLevelWarn
    LogLevelError
)

type Logger struct {
    level  LogLevel
    fields map[string]interface{}
    mu     sync.Mutex
}

func NewLogger(level LogLevel) *Logger {
    return &Logger{
        level:  level,
        fields: make(map[string]interface{}),
    }
}

func (l *Logger) WithField(key string, value interface{}) *Logger {
    l.mu.Lock()
    defer l.mu.Unlock()

    newFields := make(map[string]interface{})
    for k, v := range l.fields {
        newFields[k] = v
    }
    newFields[key] = value

    return &Logger{
        level:  l.level,
        fields: newFields,
    }
}

func (l *Logger) log(level LogLevel, msg string) {
    if level < l.level {
        return
    }

    entry := map[string]interface{}{
        "level":   level.String(),
        "message": msg,
        "time":    time.Now().Format(time.RFC3339),
    }

    for k, v := range l.fields {
        entry[k] = v
    }

    data, _ := json.Marshal(entry)
    log.Println(string(data))
}

func (l *Logger) Debug(msg string) {
    l.log(LogLevelDebug, msg)
}

func (l *Logger) Info(msg string) {
    l.log(LogLevelInfo, msg)
}

func (l *Logger) Warn(msg string) {
    l.log(LogLevelWarn, msg)
}

func (l *Logger) Error(msg string) {
    l.log(LogLevelError, msg)
}

func (l LogLevel) String() string {
    switch l {
    case LogLevelDebug:
        return "DEBUG"
    case LogLevelInfo:
        return "INFO"
    case LogLevelWarn:
        return "WARN"
    case LogLevelError:
        return "ERROR"
    default:
        return "UNKNOWN"
    }
}
```

### Работа с configuration management

```go
type Config struct {
    mu         sync.RWMutex
    data       map[string]interface{}
    watchers   []func(map[string]interface{})
}

func NewConfig() *Config {
    return &Config{
        data:     make(map[string]interface{}),
        watchers: make([]func(map[string]interface{}), 0),
    }
}

func (c *Config) Set(key string, value interface{}) {
    c.mu.Lock()
    defer c.mu.Unlock()

    c.data[key] = value

    // Уведомление наблюдателей
    for _, watcher := range c.watchers {
        watcher(c.data)
    }
}

func (c *Config) Get(key string) (interface{}, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    value, ok := c.data[key]
    return value, ok
}

func (c *Config) GetString(key string) (string, bool) {
    value, ok := c.Get(key)
    if !ok {
        return "", false
    }
    str, ok := value.(string)
    return str, ok
}

func (c *Config) Watch(callback func(map[string]interface{})) {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.watchers = append(c.watchers, callback)
}

// Загрузка конфигурации из файла
func (c *Config) LoadFromFile(filename string) error {
    data, err := os.ReadFile(filename)
    if err != nil {
        return err
    }

    var config map[string]interface{}
    if err := json.Unmarshal(data, &config); err != nil {
        return err
    }

    c.mu.Lock()
    defer c.mu.Unlock()

    for k, v := range config {
        c.data[k] = v
    }

    for _, watcher := range c.watchers {
        watcher(c.data)
    }

    return nil
}
```

### Работа с worker pool

```go
type WorkerPool struct {
    workers  int
    jobs     chan Job
    results  chan Result
    wg       sync.WaitGroup
    ctx      context.Context
    cancel   context.CancelFunc
}

type Job interface {
    Process() Result
}

type Result interface{}

func NewWorkerPool(workers int) *WorkerPool {
    ctx, cancel := context.WithCancel(context.Background())
    return &WorkerPool{
        workers: workers,
        jobs:    make(chan Job, workers*2),
        results: make(chan Result, workers*2),
        ctx:     ctx,
        cancel:  cancel,
    }
}

func (wp *WorkerPool) Start() {
    for i := 0; i < wp.workers; i++ {
        wp.wg.Add(1)
        go wp.worker()
    }
}

func (wp *WorkerPool) worker() {
    defer wp.wg.Done()

    for {
        select {
        case <-wp.ctx.Done():
            return
        case job, ok := <-wp.jobs:
            if !ok {
                return
            }
            result := job.Process()
            select {
            case wp.results <- result:
            case <-wp.ctx.Done():
                return
            }
        }
    }
}

func (wp *WorkerPool) Submit(job Job) error {
    select {
    case wp.jobs <- job:
        return nil
    case <-wp.ctx.Done():
        return wp.ctx.Err()
    }
}

func (wp *WorkerPool) Stop() {
    close(wp.jobs)
    wp.cancel()
    wp.wg.Wait()
    close(wp.results)
}
```

### Работа с message queue

```go
type MessageQueue struct {
    messages chan Message
    handlers map[string][]func(Message) error
    mu       sync.RWMutex
}

type Message struct {
    Type    string
    Payload interface{}
}

func NewMessageQueue(bufferSize int) *MessageQueue {
    mq := &MessageQueue{
        messages: make(chan Message, bufferSize),
        handlers: make(map[string][]func(Message) error),
    }
    go mq.process()
    return mq
}

func (mq *MessageQueue) Subscribe(messageType string, handler func(Message) error) {
    mq.mu.Lock()
    defer mq.mu.Unlock()
    mq.handlers[messageType] = append(mq.handlers[messageType], handler)
}

func (mq *MessageQueue) Publish(message Message) error {
    select {
    case mq.messages <- message:
        return nil
    default:
        return fmt.Errorf("queue is full")
    }
}

func (mq *MessageQueue) process() {
    for message := range mq.messages {
        mq.mu.RLock()
        handlers := mq.handlers[message.Type]
        mq.mu.RUnlock()

        for _, handler := range handlers {
            if err := handler(message); err != nil {
                log.Printf("Error handling message: %v", err)
            }
        }
    }
}
```

### Работа с saga паттерном

```go
type SagaStep struct {
    Name      string
    Execute   func() error
    Compensate func() error
}

type Saga struct {
    steps []SagaStep
}

func NewSaga() *Saga {
    return &Saga{
        steps: make([]SagaStep, 0),
    }
}

func (s *Saga) AddStep(step SagaStep) {
    s.steps = append(s.steps, step)
}

func (s *Saga) Execute() error {
    executed := make([]SagaStep, 0)

    for _, step := range s.steps {
        if err := step.Execute(); err != nil {
            // Компенсация выполненных шагов
            for i := len(executed) - 1; i >= 0; i-- {
                if err := executed[i].Compensate(); err != nil {
                    log.Printf("Compensation failed for step %s: %v", executed[i].Name, err)
                }
            }
            return fmt.Errorf("saga step %s failed: %w", step.Name, err)
        }
        executed = append(executed, step)
    }

    return nil
}
```

### Работа с distributed locks

```go
type DistributedLock struct {
    key       string
    value     string
    ttl       time.Duration
    renewCh   chan struct{}
    stopCh    chan struct{}
    lockFunc  func(string, string, time.Duration) (bool, error)
    unlockFunc func(string, string) error
}

func NewDistributedLock(key string, ttl time.Duration,
    lockFunc func(string, string, time.Duration) (bool, error),
    unlockFunc func(string, string) error) *DistributedLock {
    return &DistributedLock{
        key:       key,
        value:     generateID(),
        ttl:       ttl,
        renewCh:   make(chan struct{}),
        stopCh:    make(chan struct{}),
        lockFunc:  lockFunc,
        unlockFunc: unlockFunc,
    }
}

func (dl *DistributedLock) Acquire(ctx context.Context) error {
    acquired, err := dl.lockFunc(dl.key, dl.value, dl.ttl)
    if err != nil {
        return err
    }
    if !acquired {
        return fmt.Errorf("failed to acquire lock")
    }

    // Запуск обновления блокировки
    go dl.renew(ctx)
    return nil
}

func (dl *DistributedLock) renew(ctx context.Context) {
    ticker := time.NewTicker(dl.ttl / 2)
    defer ticker.Stop()

    for {
        select {
        case <-ctx.Done():
            return
        case <-dl.stopCh:
            return
        case <-ticker.C:
            dl.lockFunc(dl.key, dl.value, dl.ttl)
        }
    }
}

func (dl *DistributedLock) Release() error {
    close(dl.stopCh)
    return dl.unlockFunc(dl.key, dl.value)
}
```

### Работа с event sourcing

```go
type Event interface {
    Type() string
    AggregateID() string
    Timestamp() time.Time
}

type EventStore interface {
    Append(events []Event) error
    GetEvents(aggregateID string) ([]Event, error)
}

type InMemoryEventStore struct {
    events map[string][]Event
    mu     sync.RWMutex
}

func NewInMemoryEventStore() *InMemoryEventStore {
    return &InMemoryEventStore{
        events: make(map[string][]Event),
    }
}

func (es *InMemoryEventStore) Append(events []Event) error {
    es.mu.Lock()
    defer es.mu.Unlock()

    for _, event := range events {
        aggregateID := event.AggregateID()
        es.events[aggregateID] = append(es.events[aggregateID], event)
    }
    return nil
}

func (es *InMemoryEventStore) GetEvents(aggregateID string) ([]Event, error) {
    es.mu.RLock()
    defer es.mu.RUnlock()

    events, ok := es.events[aggregateID]
    if !ok {
        return []Event{}, nil
    }
    return events, nil
}

type Aggregate interface {
    Apply(event Event)
    GetID() string
}

type EventSourcedAggregate struct {
    id      string
    version int
    events  []Event
}

func (a *EventSourcedAggregate) Apply(event Event) {
    a.events = append(a.events, event)
    a.version++
}
```

### Работа с CQRS и event sourcing

```go
type Command interface {
    AggregateID() string
}

type Query interface {
    Type() string
}

type CommandHandler interface {
    Handle(Command) error
}

type QueryHandler interface {
    Handle(Query) (interface{}, error)
}

type CommandBus struct {
    handlers map[string]CommandHandler
    mu       sync.RWMutex
}

func NewCommandBus() *CommandBus {
    return &CommandBus{
        handlers: make(map[string]CommandHandler),
    }
}

func (cb *CommandBus) Register(commandType string, handler CommandHandler) {
    cb.mu.Lock()
    defer cb.mu.Unlock()
    cb.handlers[commandType] = handler
}

func (cb *CommandBus) Handle(cmd Command) error {
    cb.mu.RLock()
    handler, ok := cb.handlers[reflect.TypeOf(cmd).Name()]
    cb.mu.RUnlock()

    if !ok {
        return fmt.Errorf("handler not found for command type")
    }

    return handler.Handle(cmd)
}
```

### Работа с domain events

```go
type DomainEvent interface {
    AggregateID() string
    EventType() string
    OccurredAt() time.Time
}

type DomainEventHandler interface {
    Handle(DomainEvent) error
}

type EventDispatcher struct {
    handlers map[string][]DomainEventHandler
    mu       sync.RWMutex
}

func NewEventDispatcher() *EventDispatcher {
    return &EventDispatcher{
        handlers: make(map[string][]DomainEventHandler),
    }
}

func (ed *EventDispatcher) Register(eventType string, handler DomainEventHandler) {
    ed.mu.Lock()
    defer ed.mu.Unlock()
    ed.handlers[eventType] = append(ed.handlers[eventType], handler)
}

func (ed *EventDispatcher) Dispatch(event DomainEvent) error {
    ed.mu.RLock()
    handlers := ed.handlers[event.EventType()]
    ed.mu.RUnlock()

    for _, handler := range handlers {
        if err := handler.Handle(event); err != nil {
            return err
        }
    }
    return nil
}
```

### Работа с specification pattern

```go
type Specification interface {
    IsSatisfiedBy(candidate interface{}) bool
    And(other Specification) Specification
    Or(other Specification) Specification
    Not() Specification
}

type BaseSpecification struct{}

func (bs *BaseSpecification) And(other Specification) Specification {
    return &AndSpecification{
        left:  bs,
        right: other,
    }
}

func (bs *BaseSpecification) Or(other Specification) Specification {
    return &OrSpecification{
        left:  bs,
        right: other,
    }
}

func (bs *BaseSpecification) Not() Specification {
    return &NotSpecification{
        spec: bs,
    }
}

type AndSpecification struct {
    left, right Specification
}

func (as *AndSpecification) IsSatisfiedBy(candidate interface{}) bool {
    return as.left.IsSatisfiedBy(candidate) && as.right.IsSatisfiedBy(candidate)
}

type OrSpecification struct {
    left, right Specification
}

func (os *OrSpecification) IsSatisfiedBy(candidate interface{}) bool {
    return os.left.IsSatisfiedBy(candidate) || os.right.IsSatisfiedBy(candidate)
}

type NotSpecification struct {
    spec Specification
}

func (ns *NotSpecification) IsSatisfiedBy(candidate interface{}) bool {
    return !ns.spec.IsSatisfiedBy(candidate)
}

// Пример спецификации
type AgeSpecification struct {
    BaseSpecification
    minAge int
}

func NewAgeSpecification(minAge int) *AgeSpecification {
    return &AgeSpecification{minAge: minAge}
}

func (as *AgeSpecification) IsSatisfiedBy(candidate interface{}) bool {
    user, ok := candidate.(*User)
    if !ok {
        return false
    }
    return user.Age >= as.minAge
}
```

### Работа с value objects

```go
type ValueObject interface {
    Equals(other ValueObject) bool
}

type Email struct {
    value string
}

func NewEmail(value string) (*Email, error) {
    if !isValidEmail(value) {
        return nil, fmt.Errorf("invalid email")
    }
    return &Email{value: value}, nil
}

func (e *Email) Value() string {
    return e.value
}

func (e *Email) Equals(other ValueObject) bool {
    otherEmail, ok := other.(*Email)
    if !ok {
        return false
    }
    return e.value == otherEmail.value
}

type Money struct {
    amount   float64
    currency string
}

func NewMoney(amount float64, currency string) (*Money, error) {
    if amount < 0 {
        return nil, fmt.Errorf("amount cannot be negative")
    }
    if !isValidCurrency(currency) {
        return nil, fmt.Errorf("invalid currency")
    }
    return &Money{amount: amount, currency: currency}, nil
}

func (m *Money) Add(other *Money) (*Money, error) {
    if m.currency != other.currency {
        return nil, fmt.Errorf("currencies must match")
    }
    return NewMoney(m.amount+other.amount, m.currency)
}

func (m *Money) Equals(other ValueObject) bool {
    otherMoney, ok := other.(*Money)
    if !ok {
        return false
    }
    return m.amount == otherMoney.amount && m.currency == otherMoney.currency
}
```

### Работа с aggregate roots

```go
type AggregateRoot interface {
    GetID() string
    GetVersion() int
    GetUncommittedEvents() []DomainEvent
    MarkEventsAsCommitted()
}

type BaseAggregateRoot struct {
    id                string
    version           int
    uncommittedEvents []DomainEvent
    mu                sync.Mutex
}

func (ar *BaseAggregateRoot) GetID() string {
    return ar.id
}

func (ar *BaseAggregateRoot) GetVersion() int {
    return ar.version
}

func (ar *BaseAggregateRoot) GetUncommittedEvents() []DomainEvent {
    ar.mu.Lock()
    defer ar.mu.Unlock()
    events := make([]DomainEvent, len(ar.uncommittedEvents))
    copy(events, ar.uncommittedEvents)
    return events
}

func (ar *BaseAggregateRoot) MarkEventsAsCommitted() {
    ar.mu.Lock()
    defer ar.mu.Unlock()
    ar.uncommittedEvents = nil
}

func (ar *BaseAggregateRoot) AddEvent(event DomainEvent) {
    ar.mu.Lock()
    defer ar.mu.Unlock()
    ar.uncommittedEvents = append(ar.uncommittedEvents, event)
    ar.version++
}
```

### Работа с projection

```go
type Projection interface {
    Handle(event DomainEvent) error
}

type ReadModelProjection struct {
    db *sql.DB
}

func NewReadModelProjection(db *sql.DB) *ReadModelProjection {
    return &ReadModelProjection{db: db}
}

func (p *ReadModelProjection) Handle(event DomainEvent) error {
    switch e := event.(type) {
    case *UserCreatedEvent:
        return p.handleUserCreated(e)
    case *UserUpdatedEvent:
        return p.handleUserUpdated(e)
    default:
        return nil
    }
}

func (p *ReadModelProjection) handleUserCreated(event *UserCreatedEvent) error {
    _, err := p.db.Exec(
        "INSERT INTO user_read_model (id, email, name, created_at) VALUES ($1, $2, $3, $4)",
        event.UserID, event.Email, event.Name, event.OccurredAt(),
    )
    return err
}

func (p *ReadModelProjection) handleUserUpdated(event *UserUpdatedEvent) error {
    _, err := p.db.Exec(
        "UPDATE user_read_model SET email = $1, name = $2 WHERE id = $3",
        event.Email, event.Name, event.UserID,
    )
    return err
}
```

### Работа с snapshot

```go
type Snapshot interface {
    AggregateID() string
    Version() int
    Data() interface{}
}

type SnapshotStore interface {
    Save(snapshot Snapshot) error
    Load(aggregateID string) (Snapshot, error)
}

type InMemorySnapshotStore struct {
    snapshots map[string]Snapshot
    mu        sync.RWMutex
}

func NewInMemorySnapshotStore() *InMemorySnapshotStore {
    return &InMemorySnapshotStore{
        snapshots: make(map[string]Snapshot),
    }
}

func (ss *InMemorySnapshotStore) Save(snapshot Snapshot) error {
    ss.mu.Lock()
    defer ss.mu.Unlock()
    ss.snapshots[snapshot.AggregateID()] = snapshot
    return nil
}

func (ss *InMemorySnapshotStore) Load(aggregateID string) (Snapshot, error) {
    ss.mu.RLock()
    defer ss.mu.RUnlock()
    snapshot, ok := ss.snapshots[aggregateID]
    if !ok {
        return nil, fmt.Errorf("snapshot not found")
    }
    return snapshot, nil
}

func RehydrateFromSnapshot(aggregate Aggregate, snapshot Snapshot, eventStore EventStore) error {
    // Загрузка снимка
    applySnapshot(aggregate, snapshot)

    // Загрузка событий после снимка
    events, err := eventStore.GetEvents(aggregate.GetID())
    if err != nil {
        return err
    }

    // Применение событий после снимка
    for _, event := range events {
        if event.Timestamp().After(snapshot.Timestamp()) {
            aggregate.Apply(event)
        }
    }

    return nil
}
```

### Работа с eventual consistency

```go
type EventualConsistency struct {
    eventStore  EventStore
    projections []Projection
    dispatcher  *EventDispatcher
}

func NewEventualConsistency(eventStore EventStore, projections []Projection) *EventualConsistency {
    ec := &EventualConsistency{
        eventStore:  eventStore,
        projections: projections,
        dispatcher:  NewEventDispatcher(),
    }

    // Регистрация проекций как обработчиков событий
    for _, projection := range projections {
        ec.dispatcher.Register("", projection)
    }

    return ec
}

func (ec *EventualConsistency) ProcessEvents(aggregateID string) error {
    events, err := ec.eventStore.GetEvents(aggregateID)
    if err != nil {
        return err
    }

    for _, event := range events {
        if err := ec.dispatcher.Dispatch(event); err != nil {
            return err
        }
    }

    return nil
}
```

### Работа с saga orchestration

```go
type SagaOrchestrator struct {
    steps []SagaStep
    state map[string]interface{}
}

func NewSagaOrchestrator() *SagaOrchestrator {
    return &SagaOrchestrator{
        steps: make([]SagaStep, 0),
        state: make(map[string]interface{}),
    }
}

func (so *SagaOrchestrator) AddStep(step SagaStep) {
    so.steps = append(so.steps, step)
}

func (so *SagaOrchestrator) Execute(ctx context.Context) error {
    executed := make([]SagaStep, 0)

    for _, step := range so.steps {
        if err := ctx.Err(); err != nil {
            // Компенсация при отмене
            so.compensate(executed)
            return err
        }

        if err := step.Execute(); err != nil {
            // Компенсация при ошибке
            so.compensate(executed)
            return err
        }

        executed = append(executed, step)
    }

    return nil
}

func (so *SagaOrchestrator) compensate(executed []SagaStep) {
    for i := len(executed) - 1; i >= 0; i-- {
        if err := executed[i].Compensate(); err != nil {
            log.Printf("Compensation failed for step %s: %v", executed[i].Name, err)
        }
    }
}
```

### Работа с idempotency

```go
type IdempotencyKey string

type IdempotencyStore interface {
    Store(key IdempotencyKey, result interface{}, ttl time.Duration) error
    Get(key IdempotencyKey) (interface{}, bool)
}

type InMemoryIdempotencyStore struct {
    store map[IdempotencyKey]interface{}
    mu    sync.RWMutex
}

func NewInMemoryIdempotencyStore() *InMemoryIdempotencyStore {
    return &InMemoryIdempotencyStore{
        store: make(map[IdempotencyKey]interface{}),
    }
}

func (s *InMemoryIdempotencyStore) Store(key IdempotencyKey, result interface{}, ttl time.Duration) error {
    s.mu.Lock()
    defer s.mu.Unlock()
    s.store[key] = result

    go func() {
        time.Sleep(ttl)
        s.mu.Lock()
        delete(s.store, key)
        s.mu.Unlock()
    }()

    return nil
}

func (s *InMemoryIdempotencyStore) Get(key IdempotencyKey) (interface{}, bool) {
    s.mu.RLock()
    defer s.mu.RUnlock()
    result, ok := s.store[key]
    return result, ok
}

func IdempotentHandler(
    handler func() (interface{}, error),
    idempotencyStore IdempotencyStore,
) func(IdempotencyKey) (interface{}, error) {
    return func(key IdempotencyKey) (interface{}, error) {
        if result, ok := idempotencyStore.Get(key); ok {
            return result, nil
        }

        result, err := handler()
        if err != nil {
            return nil, err
        }

        idempotencyStore.Store(key, result, 1*time.Hour)
        return result, nil
    }
}
```

### Работа с outbox pattern

```go
type OutboxEvent struct {
    ID          string
    AggregateID string
    EventType   string
    Payload     []byte
    CreatedAt   time.Time
    Processed   bool
}

type OutboxStore interface {
    Save(events []OutboxEvent) error
    GetUnprocessed(limit int) ([]OutboxEvent, error)
    MarkAsProcessed(ids []string) error
}

type OutboxProcessor struct {
    store      OutboxStore
    dispatcher *EventDispatcher
    interval   time.Duration
    stopCh     chan struct{}
}

func NewOutboxProcessor(store OutboxStore, dispatcher *EventDispatcher, interval time.Duration) *OutboxProcessor {
    return &OutboxProcessor{
        store:      store,
        dispatcher: dispatcher,
        interval:   interval,
        stopCh:     make(chan struct{}),
    }
}

func (op *OutboxProcessor) Start() {
    ticker := time.NewTicker(op.interval)
    defer ticker.Stop()

    for {
        select {
        case <-op.stopCh:
            return
        case <-ticker.C:
            op.process()
        }
    }
}

func (op *OutboxProcessor) process() {
    events, err := op.store.GetUnprocessed(100)
    if err != nil {
        log.Printf("Error getting unprocessed events: %v", err)
        return
    }

    processedIDs := make([]string, 0)
    for _, event := range events {
        if err := op.dispatcher.Dispatch(event); err != nil {
            log.Printf("Error dispatching event: %v", err)
            continue
        }
        processedIDs = append(processedIDs, event.ID)
    }

    if len(processedIDs) > 0 {
        op.store.MarkAsProcessed(processedIDs)
    }
}

func (op *OutboxProcessor) Stop() {
    close(op.stopCh)
}
```

### Работа с bulk operations

```go
type BulkOperation struct {
    batchSize int
    processor func([]interface{}) error
}

func NewBulkOperation(batchSize int, processor func([]interface{}) error) *BulkOperation {
    return &BulkOperation{
        batchSize: batchSize,
        processor: processor,
    }
}

func (bo *BulkOperation) Process(items []interface{}) error {
    for i := 0; i < len(items); i += bo.batchSize {
        end := i + bo.batchSize
        if end > len(items) {
            end = len(items)
        }

        batch := items[i:end]
        if err := bo.processor(batch); err != nil {
            return fmt.Errorf("batch %d failed: %w", i/bo.batchSize, err)
        }
    }
    return nil
}

// Использование для базы данных
func bulkInsert(db *sql.DB, table string, records []map[string]interface{}) error {
    if len(records) == 0 {
        return nil
    }

    keys := make([]string, 0, len(records[0]))
    for k := range records[0] {
        keys = append(keys, k)
    }

    query := fmt.Sprintf("INSERT INTO %s (%s) VALUES ", table, strings.Join(keys, ", "))
    values := make([]string, 0, len(records))
    args := make([]interface{}, 0)

    argIndex := 1
    for _, record := range records {
        placeholders := make([]string, 0, len(keys))
        for _, key := range keys {
            placeholders = append(placeholders, fmt.Sprintf("$%d", argIndex))
            args = append(args, record[key])
            argIndex++
        }
        values = append(values, "("+strings.Join(placeholders, ", ")+")")
    }

    query += strings.Join(values, ", ")
    _, err := db.Exec(query, args...)
    return err
}
```

### Работа с optimistic locking

```go
type OptimisticLock struct {
    version int
    mu      sync.RWMutex
}

func (ol *OptimisticLock) GetVersion() int {
    ol.mu.RLock()
    defer ol.mu.RUnlock()
    return ol.version
}

func (ol *OptimisticLock) IncrementVersion() {
    ol.mu.Lock()
    defer ol.mu.Unlock()
    ol.version++
}

type OptimisticEntity struct {
    ID      string
    Data    interface{}
    Version int
}

func UpdateWithOptimisticLock(db *sql.DB, entity *OptimisticEntity, update func(*OptimisticEntity) error) error {
    tx, err := db.Begin()
    if err != nil {
        return err
    }
    defer tx.Rollback()

    var currentVersion int
    err = tx.QueryRow("SELECT version FROM entities WHERE id = $1", entity.ID).Scan(&currentVersion)
    if err != nil {
        return err
    }

    if currentVersion != entity.Version {
        return fmt.Errorf("optimistic lock conflict: expected version %d, got %d", entity.Version, currentVersion)
    }

    if err := update(entity); err != nil {
        return err
    }

    entity.Version++
    _, err = tx.Exec("UPDATE entities SET data = $1, version = $2 WHERE id = $3 AND version = $4",
        entity.Data, entity.Version, entity.ID, currentVersion)
    if err != nil {
        return err
    }

    affected, _ := result.RowsAffected()
    if affected == 0 {
        return fmt.Errorf("optimistic lock conflict: no rows updated")
    }

    return tx.Commit()
}
```

### Работа с soft delete

```go
type SoftDeletable struct {
    DeletedAt *time.Time
    mu        sync.RWMutex
}

func (sd *SoftDeletable) IsDeleted() bool {
    sd.mu.RLock()
    defer sd.mu.RUnlock()
    return sd.DeletedAt != nil
}

func (sd *SoftDeletable) SoftDelete() {
    sd.mu.Lock()
    defer sd.mu.Unlock()
    now := time.Now()
    sd.DeletedAt = &now
}

func (sd *SoftDeletable) Restore() {
    sd.mu.Lock()
    defer sd.mu.Unlock()
    sd.DeletedAt = nil
}

func SoftDelete(db *sql.DB, table string, id string) error {
    query := fmt.Sprintf("UPDATE %s SET deleted_at = $1 WHERE id = $2", table)
    _, err := db.Exec(query, time.Now(), id)
    return err
}

func Restore(db *sql.DB, table string, id string) error {
    query := fmt.Sprintf("UPDATE %s SET deleted_at = NULL WHERE id = $1", table)
    _, err := db.Exec(query, id)
    return err
}

func FindWithSoftDelete(db *sql.DB, table string, id string) (*sql.Row, error) {
    query := fmt.Sprintf("SELECT * FROM %s WHERE id = $1 AND deleted_at IS NULL", table)
    return db.QueryRow(query, id), nil
}
```

### Работа с auditing

```go
type Auditable struct {
    CreatedAt time.Time
    UpdatedAt time.Time
    CreatedBy string
    UpdatedBy string
}

func (a *Auditable) BeforeCreate(userID string) {
    now := time.Now()
    a.CreatedAt = now
    a.UpdatedAt = now
    a.CreatedBy = userID
    a.UpdatedBy = userID
}

func (a *Auditable) BeforeUpdate(userID string) {
    a.UpdatedAt = time.Now()
    a.UpdatedBy = userID
}

type AuditLog struct {
    ID        string
    EntityType string
    EntityID   string
    Action     string
    Changes    map[string]interface{}
    UserID     string
    Timestamp  time.Time
}

type AuditLogger struct {
    logs []AuditLog
    mu   sync.Mutex
}

func NewAuditLogger() *AuditLogger {
    return &AuditLogger{
        logs: make([]AuditLog, 0),
    }
}

func (al *AuditLogger) Log(entityType, entityID, action string, changes map[string]interface{}, userID string) {
    al.mu.Lock()
    defer al.mu.Unlock()

    log := AuditLog{
        ID:         generateID(),
        EntityType: entityType,
        EntityID:   entityID,
        Action:     action,
        Changes:    changes,
        UserID:     userID,
        Timestamp:  time.Now(),
    }

    al.logs = append(al.logs, log)
}

func (al *AuditLogger) GetLogs(entityType, entityID string) []AuditLog {
    al.mu.RLock()
    defer al.mu.RUnlock()

    var result []AuditLog
    for _, log := range al.logs {
        if log.EntityType == entityType && log.EntityID == entityID {
            result = append(result, log)
        }
    }
    return result
}
```

### Работа с multi-tenancy

```go
type Tenant struct {
    ID   string
    Name string
}

type TenantContext struct {
    TenantID string
}

type TenantKey string

const TenantContextKey TenantKey = "tenant"

func WithTenant(ctx context.Context, tenantID string) context.Context {
    return context.WithValue(ctx, TenantContextKey, tenantID)
}

func GetTenantFromContext(ctx context.Context) (string, bool) {
    tenantID, ok := ctx.Value(TenantContextKey).(string)
    return tenantID, ok
}

type TenantRepository struct {
    db *sql.DB
}

func (tr *TenantRepository) FindByTenant(ctx context.Context, table string, tenantID string, id string) error {
    query := fmt.Sprintf("SELECT * FROM %s WHERE tenant_id = $1 AND id = $2", table)
    return tr.db.QueryRowContext(ctx, query, tenantID, id).Scan(...)
}

func (tr *TenantRepository) FindAllByTenant(ctx context.Context, table string, tenantID string) (*sql.Rows, error) {
    query := fmt.Sprintf("SELECT * FROM %s WHERE tenant_id = $1", table)
    return tr.db.QueryContext(ctx, query, tenantID)
}

func TenantMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        tenantID := r.Header.Get("X-Tenant-ID")
        if tenantID == "" {
            http.Error(w, "Tenant ID required", http.StatusBadRequest)
            return
        }

        ctx := WithTenant(r.Context(), tenantID)
        next.ServeHTTP(w, r.WithContext(ctx))
    })
}
```

### Работа с versioning данных

```go
type VersionedEntity struct {
    ID        string
    Version   int
    Data      interface{}
    CreatedAt time.Time
    UpdatedAt time.Time
}

type VersionRepository struct {
    db *sql.DB
}

func (vr *VersionRepository) CreateVersion(entityID string, version int, data interface{}) error {
    query := `INSERT INTO entity_versions (entity_id, version, data, created_at)
              VALUES ($1, $2, $3, $4)`
    _, err := vr.db.Exec(query, entityID, version, data, time.Now())
    return err
}

func (vr *VersionRepository) GetVersion(entityID string, version int) (*VersionedEntity, error) {
    var entity VersionedEntity
    query := `SELECT entity_id, version, data, created_at
              FROM entity_versions
              WHERE entity_id = $1 AND version = $2`
    err := vr.db.QueryRow(query, entityID, version).Scan(
        &entity.ID, &entity.Version, &entity.Data, &entity.CreatedAt)
    if err != nil {
        return nil, err
    }
    return &entity, nil
}

func (vr *VersionRepository) GetVersions(entityID string) ([]VersionedEntity, error) {
    query := `SELECT entity_id, version, data, created_at
              FROM entity_versions
              WHERE entity_id = $1
              ORDER BY version DESC`
    rows, err := vr.db.Query(query, entityID)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var entities []VersionedEntity
    for rows.Next() {
        var entity VersionedEntity
        if err := rows.Scan(&entity.ID, &entity.Version, &entity.Data, &entity.CreatedAt); err != nil {
            return nil, err
        }
        entities = append(entities, entity)
    }
    return entities, nil
}

func (vr *VersionRepository) RestoreVersion(entityID string, version int) error {
    entity, err := vr.GetVersion(entityID, version)
    if err != nil {
        return err
    }

    tx, err := vr.db.Begin()
    if err != nil {
        return err
    }
    defer tx.Rollback()

    // Обновление текущей версии
    _, err = tx.Exec("UPDATE entities SET data = $1, version = $2 WHERE id = $3",
        entity.Data, entity.Version, entityID)
    if err != nil {
        return err
    }

    // Создание новой версии
    currentVersion := entity.Version + 1
    _, err = tx.Exec("INSERT INTO entity_versions (entity_id, version, data, created_at) VALUES ($1, $2, $3, $4)",
        entityID, currentVersion, entity.Data, time.Now())
    if err != nil {
        return err
    }

    return tx.Commit()
}
```

### Работа с materialized views

```go
type MaterializedView struct {
    name    string
    query   string
    refreshInterval time.Duration
    lastRefresh time.Time
    mu      sync.RWMutex
}

func NewMaterializedView(name, query string, refreshInterval time.Duration) *MaterializedView {
    return &MaterializedView{
        name:           name,
        query:          query,
        refreshInterval: refreshInterval,
    }
}

func (mv *MaterializedView) Refresh(db *sql.DB) error {
    mv.mu.Lock()
    defer mv.mu.Unlock()

    refreshQuery := fmt.Sprintf("REFRESH MATERIALIZED VIEW CONCURRENTLY %s", mv.name)
    _, err := db.Exec(refreshQuery)
    if err != nil {
        return err
    }

    mv.lastRefresh = time.Now()
    return nil
}

func (mv *MaterializedView) ShouldRefresh() bool {
    mv.mu.RLock()
    defer mv.mu.RUnlock()
    return time.Since(mv.lastRefresh) >= mv.refreshInterval
}

func (mv *MaterializedView) StartAutoRefresh(db *sql.DB) {
    ticker := time.NewTicker(mv.refreshInterval)
    go func() {
        for range ticker.C {
            if mv.ShouldRefresh() {
                if err := mv.Refresh(db); err != nil {
                    log.Printf("Error refreshing materialized view: %v", err)
                }
            }
        }
    }()
}
```

### Работа с read replicas

```go
type DatabasePool struct {
    primary *sql.DB
    replicas []*sql.DB
    currentReplica int
    mu       sync.Mutex
}

func NewDatabasePool(primary *sql.DB, replicas []*sql.DB) *DatabasePool {
    return &DatabasePool{
        primary: primary,
        replicas: replicas,
    }
}

func (dp *DatabasePool) GetReadDB() *sql.DB {
    if len(dp.replicas) == 0 {
        return dp.primary
    }

    dp.mu.Lock()
    defer dp.mu.Unlock()

    db := dp.replicas[dp.currentReplica]
    dp.currentReplica = (dp.currentReplica + 1) % len(dp.replicas)
    return db
}

func (dp *DatabasePool) GetWriteDB() *sql.DB {
    return dp.primary
}

type ReadWriteRepository struct {
    pool *DatabasePool
}

func (rwr *ReadWriteRepository) FindByID(ctx context.Context, id string) (*Entity, error) {
    db := rwr.pool.GetReadDB()
    // Использование read replica для чтения
    var entity Entity
    err := db.QueryRowContext(ctx, "SELECT * FROM entities WHERE id = $1", id).Scan(&entity)
    return &entity, err
}

func (rwr *ReadWriteRepository) Save(ctx context.Context, entity *Entity) error {
    db := rwr.pool.GetWriteDB()
    // Использование primary для записи
    _, err := db.ExecContext(ctx, "INSERT INTO entities ...", entity)
    return err
}
```

### Работа с connection pooling

```go
type ConnectionPool struct {
    pool     *sql.DB
    maxConns int
    idleConns int
    maxIdleTime time.Duration
}

func NewConnectionPool(dsn string, maxConns, idleConns int, maxIdleTime time.Duration) (*ConnectionPool, error) {
    db, err := sql.Open("postgres", dsn)
    if err != nil {
        return nil, err
    }

    db.SetMaxOpenConns(maxConns)
    db.SetMaxIdleConns(idleConns)
    db.SetConnMaxIdleTime(maxIdleTime)
    db.SetConnMaxLifetime(1 * time.Hour)

    if err := db.Ping(); err != nil {
        return nil, err
    }

    return &ConnectionPool{
        pool:        db,
        maxConns:    maxConns,
        idleConns:   idleConns,
        maxIdleTime: maxIdleTime,
    }, nil
}

func (cp *ConnectionPool) GetDB() *sql.DB {
    return cp.pool
}

func (cp *ConnectionPool) Health() error {
    return cp.pool.Ping()
}

func (cp *ConnectionPool) Stats() sql.DBStats {
    return cp.pool.Stats()
}

func (cp *ConnectionPool) Close() error {
    return cp.pool.Close()
}
```

### Работа с query builder

```go
type QueryBuilder struct {
    table    string
    selects  []string
    joins    []string
    wheres   []string
    groups   []string
    orders   []string
    having   []string
    limitVal *int
    offsetVal *int
    args     []interface{}
}

func NewQueryBuilder(table string) *QueryBuilder {
    return &QueryBuilder{
        table:   table,
        selects: []string{"*"},
        joins:   []string{},
        wheres:  []string{},
        groups:  []string{},
        orders:  []string{},
        having:  []string{},
        args:    []interface{}{},
    }
}

func (qb *QueryBuilder) Select(columns ...string) *QueryBuilder {
    qb.selects = columns
    return qb
}

func (qb *QueryBuilder) Join(table, condition string) *QueryBuilder {
    qb.joins = append(qb.joins, fmt.Sprintf("JOIN %s ON %s", table, condition))
    return qb
}

func (qb *QueryBuilder) Where(condition string, args ...interface{}) *QueryBuilder {
    qb.wheres = append(qb.wheres, condition)
    qb.args = append(qb.args, args...)
    return qb
}

func (qb *QueryBuilder) GroupBy(columns ...string) *QueryBuilder {
    qb.groups = columns
    return qb
}

func (qb *QueryBuilder) OrderBy(column string, direction string) *QueryBuilder {
    qb.orders = append(qb.orders, fmt.Sprintf("%s %s", column, direction))
    return qb
}

func (qb *QueryBuilder) Having(condition string, args ...interface{}) *QueryBuilder {
    qb.having = append(qb.having, condition)
    qb.args = append(qb.args, args...)
    return qb
}

func (qb *QueryBuilder) Limit(limit int) *QueryBuilder {
    qb.limitVal = &limit
    return qb
}

func (qb *QueryBuilder) Offset(offset int) *QueryBuilder {
    qb.offsetVal = &offset
    return qb
}

func (qb *QueryBuilder) Build() (string, []interface{}) {
    query := fmt.Sprintf("SELECT %s FROM %s", strings.Join(qb.selects, ", "), qb.table)

    if len(qb.joins) > 0 {
        query += " " + strings.Join(qb.joins, " ")
    }

    if len(qb.wheres) > 0 {
        query += " WHERE " + strings.Join(qb.wheres, " AND ")
    }

    if len(qb.groups) > 0 {
        query += " GROUP BY " + strings.Join(qb.groups, ", ")
    }

    if len(qb.having) > 0 {
        query += " HAVING " + strings.Join(qb.having, " AND ")
    }

    if len(qb.orders) > 0 {
        query += " ORDER BY " + strings.Join(qb.orders, ", ")
    }

    if qb.limitVal != nil {
        query += fmt.Sprintf(" LIMIT %d", *qb.limitVal)
    }

    if qb.offsetVal != nil {
        query += fmt.Sprintf(" OFFSET %d", *qb.offsetVal)
    }

    return query, qb.args
}

// Использование
builder := NewQueryBuilder("users").
    Select("id", "email", "name").
    Join("profiles", "profiles.user_id = users.id").
    Where("users.active = ?", true).
    Where("users.created_at > ?", time.Now().AddDate(0, -1, 0)).
    OrderBy("users.created_at", "DESC").
    Limit(10).
    Offset(0)

query, args := builder.Build()
```

### Работа с транзакциями с retry

```go
func ExecuteWithRetry(ctx context.Context, db *sql.DB, fn func(*sql.Tx) error, maxRetries int) error {
    var lastErr error

    for attempt := 0; attempt < maxRetries; attempt++ {
        tx, err := db.BeginTx(ctx, nil)
        if err != nil {
            return err
        }

        if err := fn(tx); err != nil {
            tx.Rollback()
            lastErr = err

            if isRetryableError(err) && attempt < maxRetries-1 {
                time.Sleep(time.Duration(attempt+1) * 100 * time.Millisecond)
                continue
            }
            return err
        }

        if err := tx.Commit(); err != nil {
            tx.Rollback()
            lastErr = err

            if isRetryableError(err) && attempt < maxRetries-1 {
                time.Sleep(time.Duration(attempt+1) * 100 * time.Millisecond)
                continue
            }
            return err
        }

        return nil
    }

    return fmt.Errorf("transaction failed after %d attempts: %w", maxRetries, lastErr)
}

func isRetryableError(err error) bool {
    // Проверка на retryable ошибки (например, deadlock, timeout)
    return strings.Contains(err.Error(), "deadlock") ||
           strings.Contains(err.Error(), "timeout")
}
```

### Работа с prepared statements

```go
type PreparedStatementCache struct {
    statements map[string]*sql.Stmt
    mu         sync.RWMutex
    db         *sql.DB
}

func NewPreparedStatementCache(db *sql.DB) *PreparedStatementCache {
    return &PreparedStatementCache{
        statements: make(map[string]*sql.Stmt),
        db:         db,
    }
}

func (psc *PreparedStatementCache) Get(key string, query string) (*sql.Stmt, error) {
    psc.mu.RLock()
    stmt, ok := psc.statements[key]
    psc.mu.RUnlock()

    if ok {
        return stmt, nil
    }

    psc.mu.Lock()
    defer psc.mu.Unlock()

    // Double-check
    stmt, ok = psc.statements[key]
    if ok {
        return stmt, nil
    }

    preparedStmt, err := psc.db.Prepare(query)
    if err != nil {
        return nil, err
    }

    psc.statements[key] = preparedStmt
    return preparedStmt, nil
}

func (psc *PreparedStatementCache) Clear() {
    psc.mu.Lock()
    defer psc.mu.Unlock()

    for _, stmt := range psc.statements {
        stmt.Close()
    }
    psc.statements = make(map[string]*sql.Stmt)
}
```

### Работа с batch insert

```go
func BatchInsert(db *sql.DB, table string, columns []string, rows [][]interface{}) error {
    if len(rows) == 0 {
        return nil
    }

    placeholders := make([]string, len(rows))
    args := make([]interface{}, 0, len(rows)*len(columns))

    argIndex := 1
    for i, row := range rows {
        rowPlaceholders := make([]string, len(columns))
        for j := range columns {
            rowPlaceholders[j] = fmt.Sprintf("$%d", argIndex)
            args = append(args, row[j])
            argIndex++
        }
        placeholders[i] = "(" + strings.Join(rowPlaceholders, ", ") + ")"
    }

    query := fmt.Sprintf("INSERT INTO %s (%s) VALUES %s",
        table, strings.Join(columns, ", "), strings.Join(placeholders, ", "))

    _, err := db.Exec(query, args...)
    return err
}

func BatchInsertWithTransaction(db *sql.DB, table string, columns []string, rows [][]interface{}, batchSize int) error {
    tx, err := db.Begin()
    if err != nil {
        return err
    }
    defer tx.Rollback()

    for i := 0; i < len(rows); i += batchSize {
        end := i + batchSize
        if end > len(rows) {
            end = len(rows)
        }

        batch := rows[i:end]
        if err := BatchInsertTx(tx, table, columns, batch); err != nil {
            return err
        }
    }

    return tx.Commit()
}
```

### Работа с upsert (INSERT `ON` CONFLICT)

```go
func Upsert(db *sql.DB, table string, data map[string]interface{}, conflictColumns []string, updateColumns []string) error {
    keys := make([]string, 0, len(data))
    values := make([]interface{}, 0, len(data))
    placeholders := make([]string, 0, len(data))

    argIndex := 1
    for k, v := range data {
        keys = append(keys, k)
        values = append(values, v)
        placeholders = append(placeholders, fmt.Sprintf("$%d", argIndex))
        argIndex++
    }

    conflictClause := "(" + strings.Join(conflictColumns, ", ") + ")"

    updates := make([]string, 0, len(updateColumns))
    for _, col := range updateColumns {
        updates = append(updates, fmt.Sprintf("%s = EXCLUDED.%s", col, col))
    }

    query := fmt.Sprintf(`INSERT INTO %s (%s) VALUES (%s)
                          ON CONFLICT %s DO UPDATE SET %s`,
        table, strings.Join(keys, ", "), strings.Join(placeholders, ", "),
        conflictClause, strings.Join(updates, ", "))

    _, err := db.Exec(query, values...)
    return err
}
```

### Работа с JSON в базе данных

```go
type JSONField struct {
    Data map[string]interface{}
}

func (jf *JSONField) Value() (driver.Value, error) {
    return json.Marshal(jf.Data)
}

func (jf *JSONField) Scan(value interface{}) error {
    if value == nil {
        jf.Data = nil
        return nil
    }

    bytes, ok := value.([]byte)
    if !ok {
        return fmt.Errorf("cannot scan %T into JSONField", value)
    }

    return json.Unmarshal(bytes, &jf.Data)
}

func InsertWithJSON(db *sql.DB, table string, id string, jsonData map[string]interface{}) error {
    jsonField := &JSONField{Data: jsonData}
    _, err := db.Exec("INSERT INTO "+table+" (id, data) VALUES ($1, $2)", id, jsonField)
    return err
}

func QueryJSON(db *sql.DB, table string, id string) (map[string]interface{}, error) {
    var jsonField JSONField
    err := db.QueryRow("SELECT data FROM "+table+" WHERE id = $1", id).Scan(&jsonField)
    if err != nil {
        return nil, err
    }
    return jsonField.Data, nil
}
```

### Работа с полнотекстовым поиском

```go
type FullTextSearch struct {
    db *sql.DB
}

func NewFullTextSearch(db *sql.DB) *FullTextSearch {
    return &FullTextSearch{db: db}
}

func (fts *FullTextSearch) Search(table, searchColumn, query string, limit int) ([]interface{}, error) {
    searchQuery := fmt.Sprintf(
        "SELECT * FROM %s WHERE to_tsvector('english', %s) @@ plainto_tsquery('english', $1) LIMIT $2",
        table, searchColumn)

    rows, err := fts.db.Query(searchQuery, query, limit)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var results []interface{}
    for rows.Next() {
        // Scan results
        var item interface{}
        if err := rows.Scan(&item); err != nil {
            return nil, err
        }
        results = append(results, item)
    }

    return results, nil
}

func (fts *FullTextSearch) CreateIndex(table, column string) error {
    indexName := fmt.Sprintf("idx_%s_%s_fts", table, column)
    query := fmt.Sprintf(
        "CREATE INDEX IF NOT EXISTS %s ON %s USING GIN (to_tsvector('english', %s))",
        indexName, table, column)
    _, err := fts.db.Exec(query)
    return err
}
```

### Работа с геоданными

```go
type Point struct {
    Longitude float64
    Latitude  float64
}

func (p *Point) Value() (driver.Value, error) {
    return fmt.Sprintf("POINT(%f %f)", p.Longitude, p.Latitude), nil
}

func (p *Point) Scan(value interface{}) error {
    str, ok := value.(string)
    if !ok {
        return fmt.Errorf("cannot scan %T into Point", value)
    }

    var lon, lat float64
    _, err := fmt.Sscanf(str, "POINT(%f %f)", &lon, &lat)
    if err != nil {
        return err
    }

    p.Longitude = lon
    p.Latitude = lat
    return nil
}

func FindNearby(db *sql.DB, table string, point Point, radiusKm float64) ([]interface{}, error) {
    query := fmt.Sprintf(
        `SELECT *,
         ST_Distance(location, ST_MakePoint($1, $2)::geography) / 1000 AS distance_km
         FROM %s
         WHERE ST_DWithin(location::geography, ST_MakePoint($1, $2)::geography, $3)
         ORDER BY distance_km`,
        table)

    radiusMeters := radiusKm * 1000
    rows, err := db.Query(query, point.Longitude, point.Latitude, radiusMeters)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var results []interface{}
    for rows.Next() {
        var item interface{}
        if err := rows.Scan(&item); err != nil {
            return nil, err
        }
        results = append(results, item)
    }

    return results, nil
}
```

### Работа с индексами

```go
type IndexManager struct {
    db *sql.DB
}

func NewIndexManager(db *sql.DB) *IndexManager {
    return &IndexManager{db: db}
}

func (im *IndexManager) CreateIndex(table, indexName string, columns []string, unique bool) error {
    uniqueClause := ""
    if unique {
        uniqueClause = "UNIQUE"
    }

    query := fmt.Sprintf(
        "CREATE %s INDEX IF NOT EXISTS %s ON %s (%s)",
        uniqueClause, indexName, table, strings.Join(columns, ", "))

    _, err := im.db.Exec(query)
    return err
}

func (im *IndexManager) CreatePartialIndex(table, indexName string, columns []string, condition string) error {
    query := fmt.Sprintf(
        "CREATE INDEX IF NOT EXISTS %s ON %s (%s) WHERE %s",
        indexName, table, strings.Join(columns, ", "), condition)

    _, err := im.db.Exec(query)
    return err
}

func (im *IndexManager) CreateCompositeIndex(table, indexName string, columns []string) error {
    return im.CreateIndex(table, indexName, columns, false)
}

func (im *IndexManager) DropIndex(indexName string) error {
    query := fmt.Sprintf("DROP INDEX IF EXISTS %s", indexName)
    _, err := im.db.Exec(query)
    return err
}

func (im *IndexManager) Reindex(table string) error {
    query := fmt.Sprintf("REINDEX TABLE %s", table)
    _, err := im.db.Exec(query)
    return err
}
```

### Работа с миграциями схемы

```go
type Migration struct {
    Version     int
    Name        string
    Up          func(*sql.DB) error
    Down        func(*sql.DB) error
}

type MigrationManager struct {
    db         *sql.DB
    migrations []Migration
}

func NewMigrationManager(db *sql.DB) *MigrationManager {
    mm := &MigrationManager{
        db:         db,
        migrations: make([]Migration, 0),
    }
    mm.initialize()
    return mm
}

func (mm *MigrationManager) initialize() error {
    query := `CREATE TABLE IF NOT EXISTS schema_migrations (
        version INTEGER PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )`
    _, err := mm.db.Exec(query)
    return err
}

func (mm *MigrationManager) AddMigration(migration Migration) {
    mm.migrations = append(mm.migrations, migration)
    sort.Slice(mm.migrations, func(i, j int) bool {
        return mm.migrations[i].Version < mm.migrations[j].Version
    })
}

func (mm *MigrationManager) GetAppliedMigrations() (map[int]bool, error) {
    rows, err := mm.db.Query("SELECT version FROM schema_migrations")
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    applied := make(map[int]bool)
    for rows.Next() {
        var version int
        if err := rows.Scan(&version); err != nil {
            return nil, err
        }
        applied[version] = true
    }

    return applied, nil
}

func (mm *MigrationManager) Up() error {
    applied, err := mm.GetAppliedMigrations()
    if err != nil {
        return err
    }

    for _, migration := range mm.migrations {
        if applied[migration.Version] {
            continue
        }

        if err := migration.Up(mm.db); err != nil {
            return fmt.Errorf("migration %d (%s) failed: %w", migration.Version, migration.Name, err)
        }

        _, err := mm.db.Exec("INSERT INTO schema_migrations (version, name) VALUES ($1, $2)",
            migration.Version, migration.Name)
        if err != nil {
            return err
        }
    }

    return nil
}

func (mm *MigrationManager) Down(targetVersion int) error {
    applied, err := mm.GetAppliedMigrations()
    if err != nil {
        return err
    }

    for i := len(mm.migrations) - 1; i >= 0; i-- {
        migration := mm.migrations[i]
        if !applied[migration.Version] || migration.Version <= targetVersion {
            continue
        }

        if migration.Down != nil {
            if err := migration.Down(mm.db); err != nil {
                return fmt.Errorf("rollback migration %d (%s) failed: %w", migration.Version, migration.Name, err)
            }
        }

        _, err := mm.db.Exec("DELETE FROM schema_migrations WHERE version = $1", migration.Version)
        if err != nil {
            return err
        }
    }

    return nil
}
```

### Работа с database views

```go
type ViewManager struct {
    db *sql.DB
}

func NewViewManager(db *sql.DB) *ViewManager {
    return &ViewManager{db: db}
}

func (vm *ViewManager) CreateView(name, query string, replace bool) error {
    replaceClause := ""
    if replace {
        replaceClause = "OR REPLACE"
    }

    createQuery := fmt.Sprintf("CREATE %s VIEW %s AS %s", replaceClause, name, query)
    _, err := vm.db.Exec(createQuery)
    return err
}

func (vm *ViewManager) DropView(name string) error {
    query := fmt.Sprintf("DROP VIEW IF EXISTS %s", name)
    _, err := vm.db.Exec(query)
    return err
}

func (vm *ViewManager) CreateMaterializedView(name, query string) error {
    createQuery := fmt.Sprintf("CREATE MATERIALIZED VIEW %s AS %s", name, query)
    _, err := vm.db.Exec(createQuery)
    return err
}

func (vm *ViewManager) RefreshMaterializedView(name string, concurrently bool) error {
    concurrentClause := ""
    if concurrently {
        concurrentClause = "CONCURRENTLY"
    }

    query := fmt.Sprintf("REFRESH MATERIALIZED VIEW %s %s", concurrentClause, name)
    _, err := vm.db.Exec(query)
    return err
}
```

### Работа с database triggers

```go
type TriggerManager struct {
    db *sql.DB
}

func NewTriggerManager(db *sql.DB) *TriggerManager {
    return &TriggerManager{db: db}
}

func (tm *TriggerManager) CreateTrigger(name, table, timing, event string, function string) error {
    query := fmt.Sprintf(
        `CREATE TRIGGER %s
         %s %s ON %s
         FOR EACH ROW
         EXECUTE FUNCTION %s()`,
        name, timing, event, table, function)

    _, err := tm.db.Exec(query)
    return err
}

func (tm *TriggerManager) DropTrigger(name, table string) error {
    query := fmt.Sprintf("DROP TRIGGER IF EXISTS %s ON %s", name, table)
    _, err := tm.db.Exec(query)
    return err
}

// Пример создания функции для триггера
func (tm *TriggerManager) CreateTriggerFunction(name, body string) error {
    query := fmt.Sprintf(
        `CREATE OR REPLACE FUNCTION %s()
         RETURNS TRIGGER AS $$
         BEGIN
             %s
             RETURN NEW;
         END;
         $$ LANGUAGE plpgsql;`,
        name, body)

    _, err := tm.db.Exec(query)
    return err
}
```

### Работа с stored procedures

```go
type ProcedureManager struct {
    db *sql.DB
}

func NewProcedureManager(db *sql.DB) *ProcedureManager {
    return &ProcedureManager{db: db}
}

func (pm *ProcedureManager) CreateProcedure(name, parameters, body string) error {
    query := fmt.Sprintf(
        `CREATE OR REPLACE FUNCTION %s(%s)
         RETURNS void AS $$
         BEGIN
             %s
         END;
         $$ LANGUAGE plpgsql;`,
        name, parameters, body)

    _, err := pm.db.Exec(query)
    return err
}

func (pm *ProcedureManager) CallProcedure(name string, args ...interface{}) error {
    placeholders := make([]string, len(args))
    for i := range placeholders {
        placeholders[i] = fmt.Sprintf("$%d", i+1)
    }

    query := fmt.Sprintf("CALL %s(%s)", name, strings.Join(placeholders, ", "))
    _, err := pm.db.Exec(query, args...)
    return err
}
```

### Работа с database functions

```go
func CreateFunction(db *sql.DB, name, returnType, body string) error {
    query := fmt.Sprintf(
        `CREATE OR REPLACE FUNCTION %s()
         RETURNS %s AS $$
         BEGIN
             %s
         END;
         $$ LANGUAGE plpgsql;`,
        name, returnType, body)

    _, err := db.Exec(query)
    return err
}

func CallFunction(db *sql.DB, name string, args ...interface{}) (interface{}, error) {
    placeholders := make([]string, len(args))
    for i := range placeholders {
        placeholders[i] = fmt.Sprintf("$%d", i+1)
    }

    query := fmt.Sprintf("SELECT %s(%s)", name, strings.Join(placeholders, ", "))
    var result interface{}
    err := db.QueryRow(query, args...).Scan(&result)
    return result, err
}
```

### Работа с database extensions

```go
type ExtensionManager struct {
    db *sql.DB
}

func NewExtensionManager(db *sql.DB) *ExtensionManager {
    return &ExtensionManager{db: db}
}

func (em *ExtensionManager) InstallExtension(name string) error {
    query := fmt.Sprintf("CREATE EXTENSION IF NOT EXISTS %s", name)
    _, err := em.db.Exec(query)
    return err
}

func (em *ExtensionManager) UninstallExtension(name string) error {
    query := fmt.Sprintf("DROP EXTENSION IF EXISTS %s", name)
    _, err := em.db.Exec(query)
    return err
}

func (em *ExtensionManager) ListExtensions() ([]string, error) {
    rows, err := em.db.Query("SELECT extname FROM pg_extension")
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var extensions []string
    for rows.Next() {
        var name string
        if err := rows.Scan(&name); err != nil {
            return nil, err
        }
        extensions = append(extensions, name)
    }

    return extensions, nil
}
```

### Работа с database constraints

```go
type ConstraintManager struct {
    db *sql.DB
}

func NewConstraintManager(db *sql.DB) *ConstraintManager {
    return &ConstraintManager{db: db}
}

func (cm *ConstraintManager) AddPrimaryKey(table, column string) error {
    constraintName := fmt.Sprintf("pk_%s_%s", table, column)
    query := fmt.Sprintf("ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)",
        table, constraintName, column)
    _, err := cm.db.Exec(query)
    return err
}

func (cm *ConstraintManager) AddForeignKey(table, column, refTable, refColumn, constraintName string) error {
    query := fmt.Sprintf(
        `ALTER TABLE %s
         ADD CONSTRAINT %s
         FOREIGN KEY (%s) REFERENCES %s(%s)`,
        table, constraintName, column, refTable, refColumn)
    _, err := cm.db.Exec(query)
    return err
}

func (cm *ConstraintManager) AddUniqueConstraint(table, column, constraintName string) error {
    query := fmt.Sprintf(
        "ALTER TABLE %s ADD CONSTRAINT %s UNIQUE (%s)",
        table, constraintName, column)
    _, err := cm.db.Exec(query)
    return err
}

func (cm *ConstraintManager) AddCheckConstraint(table, condition, constraintName string) error {
    query := fmt.Sprintf(
        "ALTER TABLE %s ADD CONSTRAINT %s CHECK (%s)",
        table, constraintName, condition)
    _, err := cm.db.Exec(query)
    return err
}

func (cm *ConstraintManager) DropConstraint(table, constraintName string) error {
    query := fmt.Sprintf("ALTER TABLE %s DROP CONSTRAINT %s", table, constraintName)
    _, err := cm.db.Exec(query)
    return err
}
```

### Работа с database sequences

```go
type SequenceManager struct {
    db *sql.DB
}

func NewSequenceManager(db *sql.DB) *SequenceManager {
    return &SequenceManager{db: db}
}

func (sm *SequenceManager) CreateSequence(name string, start, increment int64) error {
    query := fmt.Sprintf(
        "CREATE SEQUENCE IF NOT EXISTS %s START %d INCREMENT %d",
        name, start, increment)
    _, err := sm.db.Exec(query)
    return err
}

func (sm *SequenceManager) NextValue(sequenceName string) (int64, error) {
    var value int64
    query := fmt.Sprintf("SELECT nextval('%s')", sequenceName)
    err := sm.db.QueryRow(query).Scan(&value)
    return value, err
}

func (sm *SequenceManager) CurrentValue(sequenceName string) (int64, error) {
    var value int64
    query := fmt.Sprintf("SELECT currval('%s')", sequenceName)
    err := sm.db.QueryRow(query).Scan(&value)
    return value, err
}

func (sm *SequenceManager) SetValue(sequenceName string, value int64) error {
    query := fmt.Sprintf("SELECT setval('%s', %d)", sequenceName, value)
    _, err := sm.db.Exec(query)
    return err
}

func (sm *SequenceManager) DropSequence(name string) error {
    query := fmt.Sprintf("DROP SEQUENCE IF EXISTS %s", name)
    _, err := sm.db.Exec(query)
    return err
}
```

### Работа с database partitioning

```go
type PartitionManager struct {
    db *sql.DB
}

func NewPartitionManager(db *sql.DB) *PartitionManager {
    return &PartitionManager{db: db}
}

func (pm *PartitionManager) CreatePartitionedTable(name, column string, partitionType string) error {
    var query string
    switch partitionType {
    case "range":
        query = fmt.Sprintf(
            `CREATE TABLE %s (
                id SERIAL,
                %s DATE,
                PRIMARY KEY (id, %s)
            ) PARTITION BY RANGE (%s)`,
            name, column, column, column)
    case "list":
        query = fmt.Sprintf(
            `CREATE TABLE %s (
                id SERIAL,
                %s VARCHAR,
                PRIMARY KEY (id, %s)
            ) PARTITION BY LIST (%s)`,
            name, column, column, column)
    case "hash":
        query = fmt.Sprintf(
            `CREATE TABLE %s (
                id SERIAL,
                %s INT,
                PRIMARY KEY (id)
            ) PARTITION BY HASH (%s)`,
            name, column, column)
    default:
        return fmt.Errorf("unsupported partition type: %s", partitionType)
    }

    _, err := pm.db.Exec(query)
    return err
}

func (pm *PartitionManager) CreateRangePartition(table, partitionName, start, end string) error {
    query := fmt.Sprintf(
        `CREATE TABLE %s PARTITION OF %s
         FOR VALUES FROM ('%s') TO ('%s')`,
        partitionName, table, start, end)
    _, err := pm.db.Exec(query)
    return err
}

func (pm *PartitionManager) CreateListPartition(table, partitionName string, values []string) error {
    valuesStr := "'" + strings.Join(values, "', '") + "'"
    query := fmt.Sprintf(
        `CREATE TABLE %s PARTITION OF %s
         FOR VALUES IN (%s)`,
        partitionName, table, valuesStr)
    _, err := pm.db.Exec(query)
    return err
}
```

### Работа с database replication

```go
type ReplicationManager struct {
    db *sql.DB
}

func NewReplicationManager(db *sql.DB) *ReplicationManager {
    return &ReplicationManager{db: db}
}

func (rm *ReplicationManager) CreateReplicationSlot(slotName string) error {
    query := fmt.Sprintf("SELECT pg_create_physical_replication_slot('%s')", slotName)
    _, err := rm.db.Exec(query)
    return err
}

func (rm *ReplicationManager) DropReplicationSlot(slotName string) error {
    query := fmt.Sprintf("SELECT pg_drop_replication_slot('%s')", slotName)
    _, err := rm.db.Exec(query)
    return err
}

func (rm *ReplicationManager) ListReplicationSlots() ([]map[string]interface{}, error) {
    query := `SELECT slot_name, slot_type, active, restart_lsn
              FROM pg_replication_slots`
    rows, err := rm.db.Query(query)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var slots []map[string]interface{}
    for rows.Next() {
        var slotName, slotType, restartLSN string
        var active bool

        if err := rows.Scan(&slotName, &slotType, &active, &restartLSN); err != nil {
            return nil, err
        }

        slots = append(slots, map[string]interface{}{
            "slot_name":   slotName,
            "slot_type":   slotType,
            "active":      active,
            "restart_lsn": restartLSN,
        })
    }

    return slots, nil
}
```

### Работа с database backups

```go
type BackupManager struct {
    dbHost     string
    dbPort     int
    dbName     string
    dbUser     string
    dbPassword string
}

func NewBackupManager(host string, port int, name, user, password string) *BackupManager {
    return &BackupManager{
        dbHost:     host,
        dbPort:     port,
        dbName:     name,
        dbUser:     user,
        dbPassword: password,
    }
}

func (bm *BackupManager) CreateBackup(outputPath string) error {
    cmd := exec.Command("pg_dump",
        "-h", bm.dbHost,
        "-p", strconv.Itoa(bm.dbPort),
        "-U", bm.dbUser,
        "-d", bm.dbName,
        "-F", "c",
        "-f", outputPath)

    cmd.Env = append(os.Environ(), fmt.Sprintf("PGPASSWORD=%s", bm.dbPassword))

    return cmd.Run()
}

func (bm *BackupManager) RestoreBackup(backupPath string) error {
    cmd := exec.Command("pg_restore",
        "-h", bm.dbHost,
        "-p", strconv.Itoa(bm.dbPort),
        "-U", bm.dbUser,
        "-d", bm.dbName,
        "-c",
        backupPath)

    cmd.Env = append(os.Environ(), fmt.Sprintf("PGPASSWORD=%s", bm.dbPassword))

    return cmd.Run()
}

func (bm *BackupManager) CreateSQLBackup(outputPath string) error {
    cmd := exec.Command("pg_dump",
        "-h", bm.dbHost,
        "-p", strconv.Itoa(bm.dbPort),
        "-U", bm.dbUser,
        "-d", bm.dbName,
        "-F", "p",
        "-f", outputPath)

    cmd.Env = append(os.Environ(), fmt.Sprintf("PGPASSWORD=%s", bm.dbPassword))

    return cmd.Run()
}
```

### Работа с database statistics

```go
type StatisticsManager struct {
    db *sql.DB
}

func NewStatisticsManager(db *sql.DB) *StatisticsManager {
    return &StatisticsManager{db: db}
}

func (sm *StatisticsManager) GetTableStats(table string) (map[string]interface{}, error) {
    query := `
        SELECT
            schemaname,
            tablename,
            n_tup_ins as inserts,
            n_tup_upd as updates,
            n_tup_del as deletes,
            n_live_tup as live_tuples,
            n_dead_tup as dead_tuples,
            last_vacuum,
            last_autovacuum,
            last_analyze,
            last_autoanalyze
        FROM pg_stat_user_tables
        WHERE tablename = $1`

    var stats map[string]interface{}
    err := sm.db.QueryRow(query, table).Scan(
        &stats["schema"], &stats["table"],
        &stats["inserts"], &stats["updates"], &stats["deletes"],
        &stats["live_tuples"], &stats["dead_tuples"],
        &stats["last_vacuum"], &stats["last_autovacuum"],
        &stats["last_analyze"], &stats["last_autoanalyze"])

    return stats, err
}

func (sm *StatisticsManager) GetIndexStats(index string) (map[string]interface{}, error) {
    query := `
        SELECT
            schemaname,
            indexrelname,
            idx_scan as index_scans,
            idx_tup_read as tuples_read,
            idx_tup_fetch as tuples_fetched
        FROM pg_stat_user_indexes
        WHERE indexrelname = $1`

    var stats map[string]interface{}
    err := sm.db.QueryRow(query, index).Scan(
        &stats["schema"], &stats["index"],
        &stats["scans"], &stats["tuples_read"], &stats["tuples_fetched"])

    return stats, err
}

func (sm *StatisticsManager) AnalyzeTable(table string) error {
    query := fmt.Sprintf("ANALYZE %s", table)
    _, err := sm.db.Exec(query)
    return err
}

func (sm *StatisticsManager) VacuumTable(table string, full bool) error {
    query := fmt.Sprintf("VACUUM")
    if full {
        query += " FULL"
    }
    query += fmt.Sprintf(" %s", table)
    _, err := sm.db.Exec(query)
    return err
}
```

## Продвинутые техники и паттерны

### Каналы для координации

Каналы в Go — это не только средство коммуникации, но и инструмент координации горутин. Используя каналы, можно реализовать различные паттерны синхронизации и координации.

```go
// Паттерн: Токен для ограничения параллелизма
func WorkerPool(workers int, jobs <-chan Job, results chan<- Result) {
    tokens := make(chan struct{}, workers)

    for i := 0; i < workers; i++ {
        tokens <- struct{}{}
    }

    for job := range jobs {
        token := <-tokens
        go func(j Job) {
            defer func() { tokens <- token }()
            results <- processJob(j)
        }(job)
    }
}
```

### Оптимизация производительности

Go предоставляет множество возможностей для оптимизации производительности. Понимание работы **runtime**, сборщика мусора и инструментов профилирования критично для создания высокопроизводительных приложений.

```go
// Предварительное выделение памяти для слайсов
func OptimizedSlice(size int) []int {
    return make([]int, 0, size) // capacity предварительно установлен
}

// Использование sync.Pool для переиспользования объектов
var bufferPool = sync.Pool{
    New: func() interface{} {
        return new(bytes.Buffer)
    },
}

func GetBuffer() *bytes.Buffer {
    buf := bufferPool.Get().(*bytes.Buffer)
    buf.Reset()
    return buf
}

func PutBuffer(buf *bytes.Buffer) {
    bufferPool.Put(buf)
}
```

### Проектирование API

Создание хороших **API** требует понимания принципов проектирования, обработки ошибок, версионирования и документации.

```go
// Типизированные ошибки для API
type APIError struct {
    Code    int    `json:"code"`
    Message string `json:"message"`
    Details string `json:"details,omitempty"`
}

func (e *APIError) Error() string {
    return e.Message
}

var (
    ErrNotFound     = &APIError{Code: 404, Message: "Resource not found"}
    ErrUnauthorized = &APIError{Code: 401, Message: "Unauthorized"}
    ErrValidation   = &APIError{Code: 400, Message: "Validation error"}
)
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Go — это мощный, эффективный и простой язык программирования, который идеально подходит для создания современного программного обеспечения. Понимание основ Go, конкурентности, типов, интерфейсов, пакетов, тестирования и продвинутых техник позволяет создавать надежные, производительные и масштабируемые приложения.

**Ключевые принципы Go:**
- Простота и читаемость кода
- Эффективная конкурентность через горутины и каналы
- Быстрая компиляция и выполнение
- Встроенная поддержка тестирования и профилирования
- Богатая стандартная библиотека
- Отличная поддержка работы с сетью и веб-разработки

Продолжайте изучать Go, экспериментируйте с различными паттернами и техниками, и вы сможете создавать высококачественное программное обеспечение.

> **Примечание**: Это полное руководство по языку Go. Для более глубокого изучения отдельных тем рекомендуется изучать официальную документацию и специализированные ресурсы.
