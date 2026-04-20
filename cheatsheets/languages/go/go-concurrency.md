---
title: "Go: конкурентность"
description: "Полное руководство по конкурентности в Go: горутины, каналы, синхронизация, context, паттерны"
tags:
  - go
  - golang
  - concurrency
  - goroutines
  - channels
  - sync
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-04-20"
---

# Go: конкурентность


### См. также
- [[go-concurrency-interview|Вопросы на собеседовании]] — подготовка к интервью

## Полезные ссылки

- [Go Concurrency Patterns](https://go.dev/blog/pipelines)
- [Effective Go — Concurrency](https://go.dev/doc/effective_go#concurrency)
- [Go Memory Model](https://go.dev/doc/mem)

## Содержание

- [Введение в конкурентность](#введение-в-конкурентность)
  - [Основные концепции](#основные-концепции)
  - [Принципы конкурентности в Go](#принципы-конкурентности-в-go)
- [Goroutines (Горутины)](#goroutines-горутины)
  - [Создание горутин](#создание-горутин)
  - [Передача параметров](#передача-параметров)
  - [Ожидание завершения горутин](#ожидание-завершения-горутин)
  - [Множественные горутины](#множественные-горутины)
- [Channels (Каналы)](#channels-каналы)
  - [Создание каналов](#создание-каналов)
  - [Отправка и получение данных](#отправка-и-получение-данных)
  - [Буферизованные каналы](#буферизованные-каналы)
  - [Закрытие каналов](#закрытие-каналов)
  - [Range по каналу](#range-по-каналу)
- [Select Statement](#select-statement)
  - [Базовое использование](#базовое-использование)
  - [Select с default](#select-с-default)
  - [Select с таймаутом](#select-с-таймаутом)
- [Синхронизация](#синхронизация)
  - [Mutex](#mutex)
  - [RWMutex](#rwmutex)
  - [WaitGroup](#waitgroup)
  - [Once](#once)
- [Context](#context)
  - [Создание Context](#создание-context)
  - [Использование Context](#использование-context)
  - [Context с значениями](#context-с-значениями)
- [Паттерны конкурентности](#паттерны-конкурентности)
  - [Pipeline](#pipeline)
  - [Fan-out / Fan-in](#fan-out-fan-in)
  - [Worker Pool](#worker-pool)
  - [Rate Limiting](#rate-limiting)
  - [Semaphore](#semaphore)
  - [Timeout Pattern](#timeout-pattern)
  - [Graceful Shutdown](#graceful-shutdown)
  - [Producer-Consumer Pattern](#producer-consumer-pattern)
  - [Barrier Pattern](#barrier-pattern)
  - [Future Pattern](#future-pattern)
  - [Pub-Sub Pattern](#pub-sub-pattern)
  - [Circuit Breaker Pattern](#circuit-breaker-pattern)
  - [Практические примеры: параллельная обработка файлов](#практические-примеры-параллельная-обработка-файлов)
  - [Практические примеры: параллельные HTTP запросы](#практические-примеры-параллельные-http-запросы)
  - [Практические примеры: параллельная обработка данных](#практические-примеры-параллельная-обработка-данных)
  - [Отладка конкурентных программ](#отладка-конкурентных-программ)
  - [Измерение производительности](#измерение-производительности)
  - [Практические примеры: Worker pool с приоритетами](#практические-примеры-worker-pool-с-приоритетами)
  - [Практические примеры: Таймаут для горутин](#практические-примеры-таймаут-для-горутин)
  - [Практические примеры: Барьер для синхронизации](#практические-примеры-барьер-для-синхронизации)
  - [Практические примеры: Конкурентная map с шардированием](#практические-примеры-конкурентная-map-с-шардированием)
  - [Практические примеры: Семафор для ограничения конкурентности](#практические-примеры-семафор-для-ограничения-конкурентности)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: Мониторинг горутин](#практические-примеры-мониторинг-горутин)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в конкурентность

Go предоставляет мощные инструменты для конкурентного программирования: горутины (goroutines) для параллельного выполнения и каналы (channels) для коммуникации между горутинами. Понимание этих концепций критично для создания эффективных параллельных приложений.

### Основные концепции

1. **Goroutines** — легковесные потоки выполнения
2. **Channels** — типизированные каналы для коммуникации
3. **Select** — выбор из нескольких каналов
4. **Sync Package** — примитивы синхронизации

### Принципы конкурентности в Go

Go следует принципу: «Не общайтесь через общую память; вместо этого делите память через общение» (Don't communicate by sharing memory; share memory by communicating).

## Goroutines (Горутины)

Горутины — это легковесные потоки выполнения, управляемые **runtime** Go. Они намного легче обычных потоков операционной системы.

### Создание горутин

```go
// Запуск горутины
go func() {
    fmt.Println("Hello from goroutine")
}()

// Горутина с функцией
func sayHello() {
    fmt.Println("Hello")
}

go sayHello()
```

### Передача параметров

```go
// Передача параметров в горутину
go func(name string) {
    fmt.Printf("Hello, %s\n", name)
}("World")

// Захват переменных из внешней области видимости
name := "Go"
go func() {
    fmt.Printf("Hello, %s\n", name)
}()
```

### Ожидание завершения горутин

```go
// Пакет sync: WaitGroup, Mutex и другие примитивы синхронизации
import "sync"

var wg sync.WaitGroup

// Добавление горутины в группу ожидания
wg.Add(1)
go func() {
    defer wg.Done()  // Уменьшение счетчика при завершении
    fmt.Println("Goroutine finished")
}()

// Ожидание завершения всех горутин
wg.Wait()
```

### Множественные горутины

```go
// WaitGroup для ожидания завершения группы горутин
var wg sync.WaitGroup

for i := 0; i < 10; i++ {
    wg.Add(1)
    go func(id int) {
        defer wg.Done()
        fmt.Printf("Goroutine %d\n", id)
    }(i)
}

wg.Wait()
```

## Channels (Каналы)

Каналы — это типизированные каналы для коммуникации между горутинами. Они обеспечивают безопасную передачу данных.

### Создание каналов

```go
// Небуферизованный канал
ch := make(chan int)

// Буферизованный канал
ch := make(chan int, 10)

// Канал только для чтения
var readOnly <-chan int

// Канал только для записи
var writeOnly chan<- int
```

### Отправка и получение данных

```go
ch := make(chan int)

// Отправка данных в горутине
go func() {
    ch <- 42
}()

// Получение данных
value := <-ch
fmt.Println(value)  // 42
```

### Буферизованные каналы

```go
// Буферизованный канал с емкостью 3
ch := make(chan int, 3)

// Отправка данных (не блокируется, пока буфер не заполнен)
ch <- 1
ch <- 2
ch <- 3

// Получение данных
fmt.Println(<-ch)  // 1
fmt.Println(<-ch)  // 2
fmt.Println(<-ch)  // 3
```

### Закрытие каналов

```go
ch := make(chan int)

// Закрытие канала
close(ch)

// Проверка на закрытие
value, ok := <-ch
if !ok {
    fmt.Println("Channel is closed")
}
```

### Range по каналу

```go
ch := make(chan int)

// Отправка данных
go func() {
    for i := 0; i < 5; i++ {
        ch <- i
    }
    close(ch)
}()

// Итерация по каналу
for value := range ch {
    fmt.Println(value)
}
```

## Select Statement

**Select** позволяет выбирать из нескольких каналов, что полезно для обработки множественных каналов одновременно.

### Базовое использование

```go
ch1 := make(chan string)
ch2 := make(chan string)

go func() {
    ch1 <- "from ch1"
}()

go func() {
    ch2 <- "from ch2"
}()

select {
case msg1 := <-ch1:
    fmt.Println(msg1)
case msg2 := <-ch2:
    fmt.Println(msg2)
}
```

### Select с default

```go
ch := make(chan int)

select {
case value := <-ch:
    fmt.Println(value)
default:
    fmt.Println("No value received")
}
```

### Select с таймаутом

```go
ch := make(chan int)

select {
case value := <-ch:
    fmt.Println(value)
case <-time.After(1 * time.Second):
    fmt.Println("Timeout")
}
```

## Синхронизация

Пакет `sync` предоставляет примитивы синхронизации для координации горутин.

### Mutex

```go
// Пакет sync: WaitGroup, Mutex и другие примитивы синхронизации
import "sync"

var mu sync.Mutex
var counter int

func increment() {
    mu.Lock()
    defer mu.Unlock()
    counter++
}
```

### RWMutex

```go
// Пакет sync: WaitGroup, Mutex и другие примитивы синхронизации
import "sync"

var mu sync.RWMutex
var data map[string]int

// Чтение (множественные читатели)
func read(key string) int {
    mu.RLock()
    defer mu.RUnlock()
    return data[key]
}

// Запись (один писатель)
func write(key string, value int) {
    mu.Lock()
    defer mu.Unlock()
    data[key] = value
}
```

### WaitGroup

```go
// Пакет sync: WaitGroup, Mutex и другие примитивы синхронизации
import "sync"

var wg sync.WaitGroup

for i := 0; i < 10; i++ {
    wg.Add(1)
    go func(id int) {
        defer wg.Done()
        // Работа горутины
    }(i)
}

wg.Wait()
```

### Once

```go
// Пакет sync: WaitGroup, Mutex и другие примитивы синхронизации
import "sync"

var once sync.Once

func initialize() {
    once.Do(func() {
        // Инициализация выполнится только один раз
    })
}
```

## Context

Пакет `context` предоставляет механизм для отмены операций и передачи значений между горутинами.

### Создание Context

```go
// Контекст для отмены и таймаутов в горутинах
import "context"

// Создание базового контекста
ctx := context.Background()

// Создание контекста с отменой
ctx, cancel := context.WithCancel(context.Background())
defer cancel()

// Контекст с таймаутом
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()

// Контекст с дедлайном
ctx, cancel := context.WithDeadline(context.Background(), time.Now().Add(5*time.Second))
defer cancel()
```

### Использование Context

```go
func worker(ctx context.Context) {
    for {
        select {
        case <-ctx.Done():
            fmt.Println("Worker cancelled")
            return
        default:
            // Работа
        }
    }
}
```

### Context с значениями

```go
// Создание контекста со значением
ctx := context.WithValue(context.Background(), "userID", 123)

// Получение значения
userID := ctx.Value("userID")
```

## Паттерны конкурентности

### Pipeline

```go
// Генерация чисел
func generate(nums ...int) <-chan int {
    out := make(chan int)
    go func() {
        for _, n := range nums {
            out <- n
        }
        close(out)
    }()
    return out
}

// Удвоение чисел
func double(in <-chan int) <-chan int {
    out := make(chan int)
    go func() {
        for n := range in {
            out <- n * 2
        }
        close(out)
    }()
    return out
}

// Использование
numbers := generate(1, 2, 3, 4, 5)
doubled := double(numbers)

for n := range doubled {
    fmt.Println(n)
}
```

### Fan-out / Fan-in

```go
// Fan-out: распределение работы между несколькими горутинами
func fanOut(in <-chan int, workers int) []<-chan int {
    outputs := make([]<-chan int, workers)
    for i := 0; i < workers; i++ {
        out := make(chan int)
        go func() {
            for n := range in {
                out <- process(n)
            }
            close(out)
        }()
        outputs[i] = out
    }
    return outputs
}

// Fan-in: объединение результатов
func fanIn(inputs ...<-chan int) <-chan int {
    out := make(chan int)
    var wg sync.WaitGroup

    for _, in := range inputs {
        wg.Add(1)
        go func(ch <-chan int) {
            defer wg.Done()
            for n := range ch {
                out <- n
            }
        }(in)
    }

    go func() {
        wg.Wait()
        close(out)
    }()

    return out
}
```

### Worker Pool

```go
func workerPool(jobs <-chan int, results chan<- int, workers int) {
    var wg sync.WaitGroup

    for i := 0; i < workers; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for job := range jobs {
                results <- process(job)
            }
        }()
    }

    go func() {
        wg.Wait()
        close(results)
    }()
}
```

### Rate Limiting

**Rate limiting** позволяет ограничивать скорость обработки запросов или операций.

```go
import "golang.org/x/time/rate"

// Создание rate limiter (10 запросов в секунду)
limiter := rate.NewLimiter(10, 1)

func handleRequest() {
    if !limiter.Allow() {
        // Превышен лимит
        return
    }
    // Обработка запроса
}
```

### Semaphore

**Semaphore** позволяет ограничивать количество одновременно выполняющихся операций.

```go
type Semaphore struct {
    ch chan struct{}
}

func NewSemaphore(n int) *Semaphore {
    return &Semaphore{
        ch: make(chan struct{}, n),
    }
}

func (s *Semaphore) Acquire() {
    s.ch <- struct{}{}
}

func (s *Semaphore) Release() {
    <-s.ch
}

// Использование
sem := NewSemaphore(5)  // Максимум 5 одновременных операций

for i := 0; i < 100; i++ {
    go func(id int) {
        sem.Acquire()
        defer sem.Release()

        // Выполнение операции
        process(id)
    }(i)
}
```

### Timeout Pattern

Паттерн таймаута позволяет ограничивать время выполнения операции.

```go
func withTimeout(fn func(), timeout time.Duration) error {
    done := make(chan struct{})
    errCh := make(chan error)

    go func() {
        defer close(done)
        fn()
    }()

    select {
    case <-done:
        return nil
    case <-time.After(timeout):
        return fmt.Errorf("operation timed out")
    case err := <-errCh:
        return err
    }
}
```

### Graceful Shutdown

**Graceful shutdown** позволяет корректно завершать работу приложения.

```go
func gracefulShutdown(server *http.Server) {
    sigChan := make(chan os.Signal, 1)
    signal.Notify(sigChan, os.Interrupt, syscall.SIGTERM)

    <-sigChan
    fmt.Println("Shutting down...")

    ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
    defer cancel()

    if err := server.Shutdown(ctx); err != nil {
        fmt.Printf("Server forced to shutdown: %v\n", err)
    }

    fmt.Println("Server stopped")
}
```

### Producer-Consumer Pattern

Паттерн **producer-consumer** разделяет производство и потребление данных.

```go
func producer(items []int) <-chan int {
    out := make(chan int)
    go func() {
        defer close(out)
        for _, item := range items {
            out <- item
        }
    }()
    return out
}

func consumer(in <-chan int) {
    for item := range in {
        process(item)
    }
}

// Использование
items := []int{1, 2, 3, 4, 5}
ch := producer(items)
consumer(ch)
```

### Barrier Pattern

**Barrier** позволяет синхронизировать выполнение множественных горутин в определенной точке.

```go
type Barrier struct {
    count    int
    current  int
    mutex    sync.Mutex
    cond     *sync.Cond
}

func NewBarrier(count int) *Barrier {
    b := &Barrier{count: count}
    b.cond = sync.NewCond(&b.mutex)
    return b
}

func (b *Barrier) Wait() {
    b.mutex.Lock()
    b.current++
    if b.current < b.count {
        b.cond.Wait()
    } else {
        b.current = 0
        b.cond.Broadcast()
    }
    b.mutex.Unlock()
}
```

### Future Pattern

**Future** позволяет получить результат асинхронной операции позже.

```go
type Future struct {
    result chan interface{}
}

func NewFuture(fn func() interface{}) *Future {
    f := &Future{
        result: make(chan interface{}, 1),
    }
    go func() {
        f.result <- fn()
    }()
    return f
}

func (f *Future) Get() interface{} {
    return <-f.result
}

// Использование
future := NewFuture(func() interface{} {
    return expensiveOperation()
})

// Делаем другую работу
doOtherWork()

// Получаем результат
result := future.Get()
```

### Pub-Sub Pattern

**Pub-Sub** позволяет множественным подписчикам получать сообщения от издателя.

```go
type PubSub struct {
    subscribers map[string][]chan string
    mu          sync.RWMutex
}

func NewPubSub() *PubSub {
    return &PubSub{
        subscribers: make(map[string][]chan string),
    }
}

func (ps *PubSub) Subscribe(topic string) <-chan string {
    ps.mu.Lock()
    defer ps.mu.Unlock()

    ch := make(chan string, 1)
    ps.subscribers[topic] = append(ps.subscribers[topic], ch)
    return ch
}

func (ps *PubSub) Publish(topic string, message string) {
    ps.mu.RLock()
    defer ps.mu.RUnlock()

    for _, ch := range ps.subscribers[topic] {
        select {
        case ch <- message:
        default:
            // Подписчик не готов, пропускаем
        }
    }
}
```

### Circuit Breaker Pattern

**Circuit breaker** предотвращает каскадные сбои, временно блокируя вызовы при ошибках.

```go
type CircuitBreaker struct {
    maxFailures int
    timeout     time.Duration
    failures    int
    lastFailure time.Time
    mu          sync.Mutex
}

func NewCircuitBreaker(maxFailures int, timeout time.Duration) *CircuitBreaker {
    return &CircuitBreaker{
        maxFailures: maxFailures,
        timeout:     timeout,
    }
}

func (cb *CircuitBreaker) Call(fn func() error) error {
    cb.mu.Lock()

    if cb.failures >= cb.maxFailures {
        if time.Since(cb.lastFailure) < cb.timeout {
            cb.mu.Unlock()
            return fmt.Errorf("circuit breaker is open")
        }
        // Сброс счетчика после таймаута
        cb.failures = 0
    }

    cb.mu.Unlock()

    err := fn()

    cb.mu.Lock()
    if err != nil {
        cb.failures++
        cb.lastFailure = time.Now()
    } else {
        cb.failures = 0
    }
    cb.mu.Unlock()

    return err
}
```

### Практические примеры: параллельная обработка файлов

```go
func processFiles(files []string) error {
    jobs := make(chan string, len(files))
    results := make(chan error, len(files))

    // Заполнение jobs
    for _, file := range files {
        jobs <- file
    }
    close(jobs)

    // Запуск воркеров
    var wg sync.WaitGroup
    numWorkers := 10

    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for file := range jobs {
                err := processFile(file)
                results <- err
            }
        }()
    }

    // Ожидание завершения
    go func() {
        wg.Wait()
        close(results)
    }()

    // Сбор результатов
    for err := range results {
        if err != nil {
            return err
        }
    }

    return nil
}
```

### Практические примеры: параллельные HTTP запросы

```go
func fetchURLs(urls []string) ([]string, error) {
    type result struct {
        url   string
        body  string
        error error
    }

    results := make(chan result, len(urls))

    for _, url := range urls {
        go func(u string) {
            resp, err := http.Get(u)
            if err != nil {
                results <- result{url: u, error: err}
                return
            }
            defer resp.Body.Close()

            body, err := io.ReadAll(resp.Body)
            if err != nil {
                results <- result{url: u, error: err}
                return
            }

            results <- result{url: u, body: string(body)}
        }(url)
    }

    var responses []string
    for i := 0; i < len(urls); i++ {
        res := <-results
        if res.error != nil {
            return nil, res.error
        }
        responses = append(responses, res.body)
    }

    return responses, nil
}
```

### Практические примеры: параллельная обработка данных

```go
func processDataParallel(data []int, numWorkers int) []int {
    chunkSize := len(data) / numWorkers
    if chunkSize == 0 {
        chunkSize = 1
    }

    results := make([]int, len(data))
    var wg sync.WaitGroup

    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        start := i * chunkSize
        end := start + chunkSize
        if i == numWorkers-1 {
            end = len(data)
        }

        go func(start, end int) {
            defer wg.Done()
            for j := start; j < end; j++ {
                results[j] = data[j] * 2
            }
        }(start, end)
    }

    wg.Wait()
    return results
}
```

### Отладка конкурентных программ

```go
// Использование race detector
// Запуск с флагом -race
// go run -race main.go

// Логирование для отладки
func debugLog(goroutineID int, message string) {
    fmt.Printf("[Goroutine %d] %s\n", goroutineID, message)
}

// Использование
go func(id int) {
    debugLog(id, "Started")
    // Работа
    debugLog(id, "Finished")
}(1)
```

### Измерение производительности

```go
func benchmarkConcurrency() {
    start := time.Now()

    var wg sync.WaitGroup
    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            // Работа
        }()
    }

    wg.Wait()
    duration := time.Since(start)
    fmt.Printf("Took %v\n", duration)
}
```

### Практические примеры: Worker pool с приоритетами

```go
type Priority int

const (
    PriorityLow Priority = iota
    PriorityMedium
    PriorityHigh
)

type PriorityJob struct {
    Priority Priority
    Task     func() error
}

type PriorityWorkerPool struct {
    workers  int
    jobs     chan PriorityJob
    done     chan struct{}
    wg       sync.WaitGroup
}

func NewPriorityWorkerPool(workers int) *PriorityWorkerPool {
    return &PriorityWorkerPool{
        workers: workers,
        jobs:    make(chan PriorityJob, workers*2),
        done:    make(chan struct{}),
    }
}

func (pwp *PriorityWorkerPool) Start() {
    for i := 0; i < pwp.workers; i++ {
        pwp.wg.Add(1)
        go pwp.worker()
    }
}

func (pwp *PriorityWorkerPool) worker() {
    defer pwp.wg.Done()

    // Сортировка по приоритету
    jobs := make([]PriorityJob, 0)

    for {
        select {
        case <-pwp.done:
            // Обработка оставшихся задач
            for _, job := range jobs {
                job.Task()
            }
            return
        case job, ok := <-pwp.jobs:
            if !ok {
                return
            }
            jobs = append(jobs, job)

            // Сортировка по приоритету
            sort.Slice(jobs, func(i, j int) bool {
                return jobs[i].Priority > jobs[j].Priority
            })

            // Обработка задачи с наивысшим приоритетом
            if len(jobs) > 0 {
                job := jobs[0]
                jobs = jobs[1:]
                job.Task()
            }
        }
    }
}

func (pwp *PriorityWorkerPool) Submit(job PriorityJob) error {
    select {
    case pwp.jobs <- job:
        return nil
    case <-pwp.done:
        return fmt.Errorf("worker pool stopped")
    }
}

func (pwp *PriorityWorkerPool) Stop() {
    close(pwp.done)
    close(pwp.jobs)
    pwp.wg.Wait()
}
```

### Практические примеры: Таймаут для горутин

```go
func RunWithTimeout(fn func() error, timeout time.Duration) error {
    done := make(chan error, 1)

    go func() {
        done <- fn()
    }()

    select {
    case err := <-done:
        return err
    case <-time.After(timeout):
        return fmt.Errorf("operation timeout after %v", timeout)
    }
}

func RunWithContext(ctx context.Context, fn func(context.Context) error) error {
    done := make(chan error, 1)

    go func() {
        done <- fn(ctx)
    }()

    select {
    case err := <-done:
        return err
    case <-ctx.Done():
        return ctx.Err()
    }
}
```

### Практические примеры: Барьер для синхронизации

```go
type Barrier struct {
    count    int
    current  int64
    waiters  sync.WaitGroup
    mu       sync.Mutex
}

func NewBarrier(count int) *Barrier {
    b := &Barrier{count: count}
    b.waiters.Add(count)
    return b
}

func (b *Barrier) Wait() {
    b.mu.Lock()
    current := atomic.AddInt64(&b.current, 1)
    b.mu.Unlock()

    if current == int64(b.count) {
        b.waiters.Done()
        b.waiters.Wait()
        b.waiters.Add(b.count)
        atomic.StoreInt64(&b.current, 0)
    } else {
        b.waiters.Wait()
    }
}
```

### Практические примеры: Конкурентная map с шардированием

```go
type ShardedMap struct {
    shards []*Shard
    count  int
}

type Shard struct {
    data map[string]interface{}
    mu   sync.RWMutex
}

func NewShardedMap(shardCount int) *ShardedMap {
    shards := make([]*Shard, shardCount)
    for i := range shards {
        shards[i] = &Shard{
            data: make(map[string]interface{}),
        }
    }
    return &ShardedMap{
        shards: shards,
        count:  shardCount,
    }
}

func (sm *ShardedMap) getShard(key string) *Shard {
    h := fnv.New32a()
    h.Write([]byte(key))
    index := h.Sum32() % uint32(sm.count)
    return sm.shards[index]
}

func (sm *ShardedMap) Get(key string) (interface{}, bool) {
    shard := sm.getShard(key)
    shard.mu.RLock()
    defer shard.mu.RUnlock()
    value, ok := shard.data[key]
    return value, ok
}

func (sm *ShardedMap) Set(key string, value interface{}) {
    shard := sm.getShard(key)
    shard.mu.Lock()
    defer shard.mu.Unlock()
    shard.data[key] = value
}

func (sm *ShardedMap) Delete(key string) {
    shard := sm.getShard(key)
    shard.mu.Lock()
    defer shard.mu.Unlock()
    delete(shard.data, key)
}
```

### Практические примеры: Семафор для ограничения конкурентности

```go
type Semaphore struct {
    ch chan struct{}
}

func NewSemaphore(capacity int) *Semaphore {
    return &Semaphore{
        ch: make(chan struct{}, capacity),
    }
}

func (s *Semaphore) Acquire() {
    s.ch <- struct{}{}
}

func (s *Semaphore) Release() {
    <-s.ch
}

func (s *Semaphore) TryAcquire() bool {
    select {
    case s.ch <- struct{}{}:
        return true
    default:
        return false
    }
}

func (s *Semaphore) AcquireWithContext(ctx context.Context) error {
    select {
    case s.ch <- struct{}{}:
        return nil
    case <-ctx.Done():
        return ctx.Err()
    }
}
```

## Лучшие практики

1. **Используйте каналы для коммуникации** — предпочитайте каналы мьютексам
2. **Закрывайте каналы** — всегда закрывайте каналы, когда закончили отправку данных
3. **Используйте `Context` для отмены** — используйте **context** для управления жизненным циклом горутин
4. **Избегайте утечек горутин** — убедитесь, что все горутины могут завершиться
5. **Используйте `WaitGroup` для ожидания** — используйте **WaitGroup** для координации множественных горутин
6. **Ограничивайте количество горутин** — используйте **worker pools** и **semaphores**
7. **Обрабатывайте ошибки** — правильно обрабатывайте ошибки в горутинах
8. **Используйте буферизованные каналы осторожно** — они могут скрыть проблемы синхронизации
9. **Избегайте гонок данных** — используйте **race detector** для проверки
10. **Документируйте конкурентное поведение** — объясняйте, как функции работают в конкурентном контексте
11. **Используйте шардирование** — для уменьшения **contention**
12. **Мониторьте количество горутин** — отслеживайте использование горутин
13. **Используйте приоритеты** — для обработки важных задач первыми
14. **Тестируйте конкурентность** — используйте специальные тесты для проверки **race conditions**
15. **Используйте таймауты** — для предотвращения зависаний

### Практические примеры: Мониторинг горутин

```go
import "runtime"

func MonitorGoroutines() {
    go func() {
        for {
            numGoroutines := runtime.NumGoroutine()
            log.Printf("Active goroutines: %d", numGoroutines)
            time.Sleep(5 * time.Second)
        }
    }()
}

func GetGoroutineStats() map[string]interface{} {
    var m runtime.MemStats
    runtime.ReadMemStats(&m)

    return map[string]interface{}{
        "goroutines":     runtime.NumGoroutine(),
        "cpu_count":      runtime.NumCPU(),
        "heap_alloc":     m.HeapAlloc,
        "heap_sys":       m.HeapSys,
        "num_gc":         m.NumGC,
    }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Конкурентность в Go предоставляет мощные инструменты для создания параллельных приложений. Понимание горутин, каналов, синхронизации, паттернов конкурентности, мониторинга и практических техник критично для создания эффективных и безопасных параллельных программ. Правильное использование этих инструментов позволяет создавать масштабируемые, наблюдаемые приложения, которые эффективно используют многоядерные процессоры, обеспечивают высокую производительность и корректную работу в конкурентной среде.

## Дополнительные ресурсы

- [Go Concurrency Patterns](https://go.dev/blog/pipelines)
- [Effective Go — Concurrency](https://go.dev/doc/effective_go#concurrency)
- [Go Memory Model](https://go.dev/doc/mem)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
