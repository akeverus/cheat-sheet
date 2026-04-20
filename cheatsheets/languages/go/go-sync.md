---
title: "Go: синхронизация"
description: "Полное руководство по синхронизации в Go: Mutex, RWMutex, WaitGroup, Once, Cond, Atomic операции"
tags:
  - go
  - golang
  - sync
  - mutex
  - synchronization
  - concurrency
difficulty: "intermediate"
prerequisites: ["go/go-basics.md", "go/go-concurrency.md"]
updated: "2026-02-06"
---

# Go: синхронизация

## Полезные ссылки

- [Go sync Package](https://pkg.go.dev/sync)
- [Go sync/atomic](https://pkg.go.dev/sync/atomic)

## Содержание

- [Go: синхронизация](#go-синхронизация)
- [Введение в синхронизацию](#введение-в-синхронизацию)
  - [Основные примитивы](#основные-примитивы)
- [**Mutex**](#mutex)
  - [Базовое использование](#базовое-использование)
  - [**TryLock** (**Go 1.18+**)](#trylock-go-118)
- [**RWMutex**](#rwmutex)
- [**WaitGroup**](#waitgroup)
  - [Использование с результатами](#использование-с-результатами)
- [**Once**](#once)
  - [**Singleton** паттерн](#singleton-паттерн)
- [**Cond**](#cond)
  - [**Broadcast**](#broadcast)
- [**Atomic** операции](#atomic-операции)
  - [Базовые операции](#базовые-операции)
  - [**CompareAndSwap**](#compareandswap)
  - [**Store** и **Load**](#store-и-load)
  - [Практические примеры: **Thread-safe map**](#практические-примеры-thread-safe-map)
  - [Практические примеры: **Thread-safe** слайс](#практические-примеры-thread-safe-слайс)
  - [Практические примеры: **Rate limiter** с **Mutex**](#практические-примеры-rate-limiter-с-mutex)
  - [Практические примеры: **Pool** с **WaitGroup**](#практические-примеры-pool-с-waitgroup)
  - [Практические примеры: **Lazy initialization** с **Once**](#практические-примеры-lazy-initialization-с-once)
  - [Практические примеры: **Barrier** с **WaitGroup**](#практические-примеры-barrier-с-waitgroup)
  - [Практические примеры: **Semaphore** с **Cond**](#практические-примеры-semaphore-с-cond)
  - [Практические примеры: **Atomic** операции для счетчиков](#практические-примеры-atomic-операции-для-счетчиков)
  - [Практические примеры: **Thread-safe** очередь](#практические-примеры-thread-safe-очередь)
  - [Практические примеры: Потокобезопасный пул объектов](#практические-примеры-потокобезопасный-пул-объектов)
  - [Практические примеры: Потокобезопасный счетчик с метриками](#практические-примеры-потокобезопасный-счетчик-с-метриками)
  - [Практические примеры: Барьер для синхронизации](#практические-примеры-барьер-для-синхронизации)
  - [Практические примеры: **Read-Write Lock** для кэша](#практические-примеры-read-write-lock-для-кэша)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в синхронизацию

Пакет `**sync**` предоставляет примитивы синхронизации для безопасного доступа к общим ресурсам из множества горутин. Понимание синхронизации критично для создания безопасных конкурентных программ.

### Основные примитивы

1. **Mutex** — взаимное исключение для защиты критических секций
2. **RWMutex** — читатель-писатель мьютекс для оптимизации чтения
3. **WaitGroup** — ожидание завершения группы горутин
4. **Once** — выполнение функции один раз
5. **Cond** — условные переменные для координации

## **Mutex**

**Mutex** обеспечивает взаимное исключение для защиты критических секций.

### Базовое использование

```go
import (
    "sync"
    "fmt"
)

type Counter struct {
    mu    sync.Mutex
    value int
}

func (c *Counter) Increment() {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.value++
}

func (c *Counter) Value() int {
    c.mu.Lock()
    defer c.mu.Unlock()
    return c.value
}

func main() {
    counter := &Counter{}

    var wg sync.WaitGroup
    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            counter.Increment()
        }()
    }

    wg.Wait()
    fmt.Println(counter.Value())  // 1000
}
```

### **TryLock** (**Go 1.18+**)

```go
func (c *Counter) TryIncrement() bool {
    if c.mu.TryLock() {
        defer c.mu.Unlock()
        c.value++
        return true
    }
    return false
}
```

## **RWMutex**

**RWMutex** позволяет множественным читателям или одному писателю.

### Базовое использование

```go
import "sync"

type SafeMap struct {
    mu   sync.RWMutex
    data map[string]int
}

func NewSafeMap() *SafeMap {
    return &SafeMap{
        data: make(map[string]int),
    }
}

func (m *SafeMap) Get(key string) (int, bool) {
    m.mu.RLock()
    defer m.mu.RUnlock()
    value, ok := m.data[key]
    return value, ok
}

func (m *SafeMap) Set(key string, value int) {
    m.mu.Lock()
    defer m.mu.Unlock()
    m.data[key] = value
}
```

## **WaitGroup**

**WaitGroup** позволяет ожидать завершения группы горутин.

### Базовое использование

```go
import (
    "sync"
    "fmt"
)

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

### Использование с результатами

```go
func processItems(items []Item) []Result {
    var wg sync.WaitGroup
    results := make([]Result, len(items))

    for i, item := range items {
        wg.Add(1)
        go func(index int, it Item) {
            defer wg.Done()
            results[index] = processItem(it)
        }(i, item)
    }

    wg.Wait()
    return results
}
```

## **Once**

**Once** гарантирует выполнение функции только один раз.

### Базовое использование

```go
import (
    "sync"
    "fmt"
)

var once sync.Once

func initialize() {
    fmt.Println("Initializing...")
}

func main() {
    // Вызовется только один раз
    once.Do(initialize)
    once.Do(initialize)
    once.Do(initialize)
    // Output: Initializing... (один раз)
}
```

### **Singleton** паттерн

```go
type Singleton struct {
    value int
}

var (
    instance *Singleton
    once     sync.Once
)

func GetInstance() *Singleton {
    once.Do(func() {
        instance = &Singleton{value: 42}
    })
    return instance
}
```

## **Cond**

**Cond** предоставляет условные переменные для координации горутин.

### Базовое использование

```go
import (
    "sync"
    "fmt"
)

func main() {
    var mu sync.Mutex
    cond := sync.NewCond(&mu)
    ready := false

    // Горутина-потребитель
    go func() {
        mu.Lock()
        for !ready {
            cond.Wait()
        }
        fmt.Println("Consumer: ready!")
        mu.Unlock()
    }()

    // Горутина-производитель
    go func() {
        mu.Lock()
        ready = true
        cond.Signal()
        mu.Unlock()
    }()

    time.Sleep(100 * time.Millisecond)
}
```

### **Broadcast**

```go
func main() {
    var mu sync.Mutex
    cond := sync.NewCond(&mu)
    ready := false

    // Множественные потребители
    for i := 0; i < 3; i++ {
        go func(id int) {
            mu.Lock()
            for !ready {
                cond.Wait()
            }
            fmt.Printf("Consumer %d: ready!\n", id)
            mu.Unlock()
        }(i)
    }

    // Производитель
    go func() {
        time.Sleep(100 * time.Millisecond)
        mu.Lock()
        ready = true
        cond.Broadcast()  // Уведомление всех
        mu.Unlock()
    }()

    time.Sleep(200 * time.Millisecond)
}
```

## **Atomic** операции

**Atomic** операции обеспечивают атомарные операции над переменными.

### Базовые операции

```go
import (
    "sync/atomic"
    "sync"
)

type AtomicCounter struct {
    value int64
}

func (c *AtomicCounter) Increment() {
    atomic.AddInt64(&c.value, 1)
}

func (c *AtomicCounter) Value() int64 {
    return atomic.LoadInt64(&c.value)
}

func main() {
    counter := &AtomicCounter{}

    var wg sync.WaitGroup
    for i := 0; i < 1000; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            counter.Increment()
        }()
    }

    wg.Wait()
    fmt.Println(counter.Value())  // 1000
}
```

### **CompareAndSwap**

```go
func (c *AtomicCounter) CompareAndSwap(old, new int64) bool {
    return atomic.CompareAndSwapInt64(&c.value, old, new)
}
```

### **Store** и **Load**

```go
func main() {
    var value int64

    // Сохранение значения
    atomic.StoreInt64(&value, 42)

    // Загрузка значения
    loaded := atomic.LoadInt64(&value)
    fmt.Println(loaded)  // 42
}
```

### Практические примеры: **Thread-safe map**

```go
type SafeMap struct {
    mu   sync.RWMutex
    data map[string]interface{}
}

func NewSafeMap() *SafeMap {
    return &SafeMap{
        data: make(map[string]interface{}),
    }
}

func (m *SafeMap) Get(key string) (interface{}, bool) {
    m.mu.RLock()
    defer m.mu.RUnlock()
    value, ok := m.data[key]
    return value, ok
}

func (m *SafeMap) Set(key string, value interface{}) {
    m.mu.Lock()
    defer m.mu.Unlock()
    m.data[key] = value
}

func (m *SafeMap) Delete(key string) {
    m.mu.Lock()
    defer m.mu.Unlock()
    delete(m.data, key)
}

func (m *SafeMap) Keys() []string {
    m.mu.RLock()
    defer m.mu.RUnlock()

    keys := make([]string, 0, len(m.data))
    for k := range m.data {
        keys = append(keys, k)
    }
    return keys
}

func (m *SafeMap) Size() int {
    m.mu.RLock()
    defer m.mu.RUnlock()
    return len(m.data)
}
```

### Практические примеры: **Thread-safe** слайс

```go
type SafeSlice struct {
    mu    sync.RWMutex
    items []interface{}
}

func NewSafeSlice() *SafeSlice {
    return &SafeSlice{
        items: make([]interface{}, 0),
    }
}

func (s *SafeSlice) Append(item interface{}) {
    s.mu.Lock()
    defer s.mu.Unlock()
    s.items = append(s.items, item)
}

func (s *SafeSlice) Get(index int) (interface{}, bool) {
    s.mu.RLock()
    defer s.mu.RUnlock()

    if index < 0 || index >= len(s.items) {
        return nil, false
    }
    return s.items[index], true
}

func (s *SafeSlice) Len() int {
    s.mu.RLock()
    defer s.mu.RUnlock()
    return len(s.items)
}

func (s *SafeSlice) Range(fn func(interface{}) bool) {
    s.mu.RLock()
    items := make([]interface{}, len(s.items))
    copy(items, s.items)
    s.mu.RUnlock()

    for _, item := range items {
        if !fn(item) {
            break
        }
    }
}
```

### Практические примеры: **Rate limiter** с **Mutex**

```go
type RateLimiter struct {
    mu       sync.Mutex
    tokens   int
    maxTokens int
    refillRate time.Duration
    lastRefill time.Time
}

func NewRateLimiter(maxTokens int, refillRate time.Duration) *RateLimiter {
    return &RateLimiter{
        tokens:     maxTokens,
        maxTokens:  maxTokens,
        refillRate: refillRate,
        lastRefill: time.Now(),
    }
}

func (rl *RateLimiter) Allow() bool {
    rl.mu.Lock()
    defer rl.mu.Unlock()

    // Пополнение токенов
    now := time.Now()
    elapsed := now.Sub(rl.lastRefill)
    if elapsed >= rl.refillRate {
        refills := int(elapsed / rl.refillRate)
        rl.tokens = min(rl.tokens+refills, rl.maxTokens)
        rl.lastRefill = now
    }

    if rl.tokens > 0 {
        rl.tokens--
        return true
    }

    return false
}
```

### Практические примеры: **Pool** с **WaitGroup**

```go
type WorkerPool struct {
    workers  int
    jobs     chan Job
    results  chan Result
    wg       sync.WaitGroup
}

func NewWorkerPool(workers int) *WorkerPool {
    return &WorkerPool{
        workers: workers,
        jobs:    make(chan Job, workers*2),
        results: make(chan Result, workers*2),
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

    for job := range wp.jobs {
        result := processJob(job)
        wp.results <- result
    }
}

func (wp *WorkerPool) Wait() {
    close(wp.jobs)
    wp.wg.Wait()
    close(wp.results)
}
```

### Практические примеры: **Lazy initialization** с **Once**

```go
type LazyValue struct {
    once  sync.Once
    value interface{}
    fn    func() interface{}
}

func NewLazyValue(fn func() interface{}) *LazyValue {
    return &LazyValue{fn: fn}
}

func (lv *LazyValue) Get() interface{} {
    lv.once.Do(func() {
        lv.value = lv.fn()
    })
    return lv.value
}
```

### Практические примеры: **Barrier** с **WaitGroup**

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
        // Последний достиг барьера
        b.waiters.Done()
        b.waiters.Wait()
        b.waiters.Add(b.count)
        atomic.StoreInt64(&b.current, 0)
    } else {
        b.waiters.Wait()
    }
}
```

### Практические примеры: **Semaphore** с **Cond**

```go
type Semaphore struct {
    count int
    cond  *sync.Cond
    mu    sync.Mutex
}

func NewSemaphore(count int) *Semaphore {
    s := &Semaphore{count: count}
    s.cond = sync.NewCond(&s.mu)
    return s
}

func (s *Semaphore) Acquire() {
    s.mu.Lock()
    defer s.mu.Unlock()

    for s.count == 0 {
        s.cond.Wait()
    }
    s.count--
}

func (s *Semaphore) Release() {
    s.mu.Lock()
    defer s.mu.Unlock()

    s.count++
    s.cond.Signal()
}
```

### Практические примеры: **Atomic** операции для счетчиков

```go
type AtomicCounter struct {
    value int64
}

func NewAtomicCounter() *AtomicCounter {
    return &AtomicCounter{}
}

func (c *AtomicCounter) Increment() int64 {
    return atomic.AddInt64(&c.value, 1)
}

func (c *AtomicCounter) Decrement() int64 {
    return atomic.AddInt64(&c.value, -1)
}

func (c *AtomicCounter) Add(delta int64) int64 {
    return atomic.AddInt64(&c.value, delta)
}

func (c *AtomicCounter) Value() int64 {
    return atomic.LoadInt64(&c.value)
}

func (c *AtomicCounter) Reset() {
    atomic.StoreInt64(&c.value, 0)
}

func (c *AtomicCounter) CompareAndSwap(old, new int64) bool {
    return atomic.CompareAndSwapInt64(&c.value, old, new)
}
```

### Практические примеры: **Thread-safe** очередь

```go
type SafeQueue struct {
    mu    sync.Mutex
    items []interface{}
    cond  *sync.Cond
}

func NewSafeQueue() *SafeQueue {
    sq := &SafeQueue{
        items: make([]interface{}, 0),
    }
    sq.cond = sync.NewCond(&sq.mu)
    return sq
}

func (sq *SafeQueue) Enqueue(item interface{}) {
    sq.mu.Lock()
    defer sq.mu.Unlock()

    sq.items = append(sq.items, item)
    sq.cond.Signal()
}

func (sq *SafeQueue) Dequeue() (interface{}, bool) {
    sq.mu.Lock()
    defer sq.mu.Unlock()

    for len(sq.items) == 0 {
        sq.cond.Wait()
    }

    item := sq.items[0]
    sq.items = sq.items[1:]
    return item, true
}

func (sq *SafeQueue) Size() int {
    sq.mu.Lock()
    defer sq.mu.Unlock()
    return len(sq.items)
}
```

### Практические примеры: Потокобезопасный пул объектов

```go
type ObjectPool struct {
    pool sync.Pool
}

func NewObjectPool(newFunc func() interface{}) *ObjectPool {
    return &ObjectPool{
        pool: sync.Pool{
            New: newFunc,
        },
    }
}

func (p *ObjectPool) Get() interface{} {
    return p.pool.Get()
}

func (p *ObjectPool) Put(obj interface{}) {
    p.pool.Put(obj)
}

// Использование
var bufferPool = NewObjectPool(func() interface{} {
    return new(bytes.Buffer)
})

func getBuffer() *bytes.Buffer {
    return bufferPool.Get().(*bytes.Buffer)
}

func putBuffer(buf *bytes.Buffer) {
    buf.Reset()
    bufferPool.Put(buf)
}
```

### Практические примеры: Потокобезопасный счетчик с метриками

```go
type SafeCounter struct {
    mu    sync.RWMutex
    count int64
    total int64
    min   int64
    max   int64
}

func (sc *SafeCounter) Increment() {
    sc.mu.Lock()
    defer sc.mu.Unlock()
    sc.count++
    sc.total++
}

func (sc *SafeCounter) Add(n int64) {
    sc.mu.Lock()
    defer sc.mu.Unlock()
    sc.count += n
    sc.total += n

    if n < sc.min || sc.min == 0 {
        sc.min = n
    }
    if n > sc.max {
        sc.max = n
    }
}

func (sc *SafeCounter) Value() int64 {
    sc.mu.RLock()
    defer sc.mu.RUnlock()
    return sc.count
}

func (sc *SafeCounter) Stats() (int64, int64, int64, int64) {
    sc.mu.RLock()
    defer sc.mu.RUnlock()
    return sc.count, sc.total, sc.min, sc.max
}
```

### Практические примеры: Барьер для синхронизации

```go
type Barrier struct {
    count    int
    current  int64
    waiters  sync.WaitGroup
    mu       sync.Mutex
    cond     *sync.Cond
}

func NewBarrier(count int) *Barrier {
    b := &Barrier{count: count}
    b.cond = sync.NewCond(&b.mu)
    b.waiters.Add(count)
    return b
}

func (b *Barrier) Wait() {
    b.mu.Lock()
    current := atomic.AddInt64(&b.current, 1)

    if current == int64(b.count) {
        b.current = 0
        b.cond.Broadcast()
    } else {
        b.cond.Wait()
    }
    b.mu.Unlock()
}
```

### Практические примеры: **Read-Write Lock** для кэша

```go
type Cache struct {
    mu    sync.RWMutex
    data  map[string]interface{}
}

func NewCache() *Cache {
    return &Cache{
        data: make(map[string]interface{}),
    }
}

func (c *Cache) Get(key string) (interface{}, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    value, ok := c.data[key]
    return value, ok
}

func (c *Cache) Set(key string, value interface{}) {
    c.mu.Lock()
    defer c.mu.Unlock()
    c.data[key] = value
}

func (c *Cache) Delete(key string) {
    c.mu.Lock()
    defer c.mu.Unlock()
    delete(c.data, key)
}

func (c *Cache) Size() int {
    c.mu.RLock()
    defer c.mu.RUnlock()
    return len(c.data)
}
```

## Лучшие практики

1. **Всегда используйте defer для Unlock** — гарантирует освобождение блокировки
2. **Минимизируйте время блокировки** — держите блокировки как можно меньше
3. **Используйте RWMutex для чтения** — когда много читателей и мало писателей
4. **Используйте atomic для простых операций** — для счетчиков и флагов
5. **Избегайте вложенных блокировок** — может привести к **deadlock**
6. **Используйте `WaitGroup` правильно** — **Add** перед запуском, **Done** в **defer**
7. **Используйте `Once` для инициализации** — гарантирует однократное выполнение
8. **Используйте `Cond` для координации** — для условной синхронизации
9. **Избегайте гонок данных** — используйте правильные примитивы синхронизации
10. **Тестируйте конкурентность** — используйте **race detector**
11. **Используйте sync.Pool** — для переиспользования объектов
12. **Используйте барьеры** — для синхронизации групп горутин
13. **Используйте RWMutex для кэшей** — когда много читателей
14. **Оптимизируйте блокировки** — минимизируйте критическую секцию
15. **Документируйте блокировки** — объясняйте, почему используется синхронизация


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Синхронизация в Go предоставляет мощные инструменты для создания безопасных конкурентных программ. Понимание **Mutex**, **RWMutex**, **WaitGroup**, **Once**, **Cond**, **atomic** операций, **sync.Pool**, барьеров и практических применений критично для эффективной синхронизации в Go. Правильное использование синхронизации позволяет создавать безопасные, эффективные, масштабируемые конкурентные приложения, которые корректно работают в многопоточной среде.

## Дополнительные ресурсы

- [Go sync Package](https://pkg.go.dev/sync)
- [Go sync/atomic](https://pkg.go.dev/sync/atomic)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
