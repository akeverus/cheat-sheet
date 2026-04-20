---
title: "Go: продвинутые паттерны"
description: "Полное руководство по продвинутым паттернам в Go: worker pools, pipelines, fan-out/fan-in, rate limiting"
tags:
  - go
  - golang
  - patterns
  - concurrency
  - design-patterns
difficulty: "advanced"
prerequisites: ["go/go-basics.md", "go/go-concurrency.md"]
updated: "2026-02-06"
---

# Go: продвинутые паттерны

## Полезные ссылки

- [Go Concurrency Patterns](https://go.dev/blog/pipelines)
- [Advanced Go Concurrency Patterns](https://go.dev/blog/advanced-go-concurrency-patterns)

## Содержание

- [Go: продвинутые паттерны](#go-продвинутые-паттерны)
- [Введение в паттерны](#введение-в-паттерны)
  - [Основные паттерны](#основные-паттерны)
- [**Worker Pools**](#worker-pools)
  - [Базовый **worker pool**](#базовый-worker-pool)
  - [**Worker pool** с ограничением](#worker-pool-с-ограничением)
- [**Pipelines**](#pipelines)
  - [Базовый **pipeline**](#базовый-pipeline)
  - [**Pipeline** с обработкой ошибок](#pipeline-с-обработкой-ошибок)
- [**Fan-out**/**Fan-in**](#fan-outfan-in)
  - [**Fan-out**](#fan-out)
  - [**Fan-in**](#fan-in)
- [**Rate Limiting**](#rate-limiting)
  - [**Token bucket**](#token-bucket)
  - [**Sliding window**](#sliding-window)
  - [Практические примеры: Улучшенный **Worker Pool**](#практические-примеры-улучшенный-worker-pool)
  - [Практические примеры: **Pipeline** с обработкой ошибок](#практические-примеры-pipeline-с-обработкой-ошибок)
  - [Практические примеры: **Circuit Breaker**](#практические-примеры-circuit-breaker)
  - [Практические примеры: **Retry Pattern**](#практические-примеры-retry-pattern)
  - [Практические примеры: **Backpressure Pattern**](#практические-примеры-backpressure-pattern)
  - [Практические примеры: **Semaphore Pattern**](#практические-примеры-semaphore-pattern)
  - [Практические примеры: **Producer-Consumer Pattern**](#практические-примеры-producer-consumer-pattern)
  - [Практические примеры: **Barrier Pattern**](#практические-примеры-barrier-pattern)
  - [Практические примеры: **Future Pattern**](#практические-примеры-future-pattern)
  - [Практические примеры: **Observer Pattern**](#практические-примеры-observer-pattern)
  - [Практические примеры: **Throttle Pattern**](#практические-примеры-throttle-pattern)
  - [Практические примеры: **Debounce Pattern**](#практические-примеры-debounce-pattern)
  - [Практические примеры: **Circuit Breaker** паттерн](#практические-примеры-circuit-breaker-паттерн)
  - [Практические примеры: **Retry** паттерн с экспоненциальной задержкой](#практические-примеры-retry-паттерн-с-экспоненциальной-задержкой)
  - [Практические примеры: **Bulkhead** паттерн](#практические-примеры-bulkhead-паттерн)
  - [Практические примеры: **Timeout** паттерн](#практические-примеры-timeout-паттерн)
  - [Практические примеры: **Timeout** и **Retry** комбинированный паттерн](#практические-примеры-timeout-и-retry-комбинированный-паттерн)
  - [Практические примеры: **Graceful shutdown** паттерн](#практические-примеры-graceful-shutdown-паттерн)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: **Event-driven** паттерн](#практические-примеры-event-driven-паттерн)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в паттерны

Продвинутые паттерны в Go помогают создавать эффективные и масштабируемые конкурентные программы. Понимание этих паттернов критично для создания сложных приложений.

### Основные паттерны

1. **Worker Pools** — пул воркеров для обработки задач
2. **Pipelines** — цепочки обработки данных
3. **Fan-out/Fan-in** — распределение и сбор результатов
4. **Rate Limiting** — ограничение скорости обработки

## Worker Pools

### Базовый worker pool

```go
import (
    "sync"
    "context"
)

func workerPool(ctx context.Context, jobs <-chan Job, results chan<- Result) {
    var wg sync.WaitGroup
    numWorkers := 10

    for i := 0; i < numWorkers; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for {
                select {
                case <-ctx.Done():
                    return
                case job, ok := <-jobs:
                    if !ok {
                        return
                    }
                    result := processJob(job)
                    results <- result
                }
            }
        }()
    }

    wg.Wait()
    close(results)
}
```

### Worker pool с ограничением

```go
func workerPoolWithLimit(jobs <-chan Job, results chan<- Result, limit int) {
    sem := make(chan struct{}, limit)
    var wg sync.WaitGroup

    for job := range jobs {
        wg.Add(1)
        sem <- struct{}{}  // Acquire

        go func(j Job) {
            defer func() {
                <-sem  // Release
                wg.Done()
            }()

            result := processJob(j)
            results <- result
        }(job)
    }

    wg.Wait()
    close(results)
}
```

## Pipelines

### Базовый pipeline

```go
func pipeline(input <-chan int) <-chan int {
    // Stage 1: Multiply
    stage1 := make(chan int)
    go func() {
        defer close(stage1)
        for n := range input {
            stage1 <- n * 2
        }
    }()

    // Stage 2: Add
    stage2 := make(chan int)
    go func() {
        defer close(stage2)
        for n := range stage1 {
            stage2 <- n + 1
        }
    }()

    return stage2
}
```

### Pipeline с обработкой ошибок

```go
type Result struct {
    Value int
    Error error
}

func pipelineWithErrors(input <-chan int) <-chan Result {
    results := make(chan Result)

    go func() {
        defer close(results)
        for n := range input {
            value, err := process(n)
            results <- Result{Value: value, Error: err}
        }
    }()

    return results
}
```

## Fan-out/Fan-in

### Fan-out

```go
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
```

### Fan-in

```go
func fanIn(inputs []<-chan int) <-chan int {
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

## Rate Limiting

### Token bucket

```go
import "golang.org/x/time/rate"

func rateLimitedHandler(limiter *rate.Limiter) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        if !limiter.Allow() {
            http.Error(w, "Rate limit exceeded", http.StatusTooManyRequests)
            return
        }

        // Обработка запроса
        processRequest(w, r)
    }
}
```

### Sliding window

```go
type SlidingWindow struct {
    requests chan time.Time
    limit    int
    window   time.Duration
}

func NewSlidingWindow(limit int, window time.Duration) *SlidingWindow {
    return &SlidingWindow{
        requests: make(chan time.Time, limit),
        limit:    limit,
        window:   window,
    }
}

func (sw *SlidingWindow) Allow() bool {
    now := time.Now()

    // Удаление старых запросов
    for {
        select {
        case req := <-sw.requests:
            if now.Sub(req) < sw.window {
                sw.requests <- req
                break
            }
        default:
            break
        }
    }

    // Проверка лимита
    if len(sw.requests) >= sw.limit {
        return false
    }

    sw.requests <- now
    return true
}
```

### Практические примеры: Улучшенный Worker Pool

```go
type WorkerPool struct {
    workers    int
    jobs       chan Job
    results    chan Result
    wg         sync.WaitGroup
    ctx        context.Context
    cancel     context.CancelFunc
}

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
        go wp.worker(i)
    }
}

func (wp *WorkerPool) worker(id int) {
    defer wp.wg.Done()

    for {
        select {
        case <-wp.ctx.Done():
            return
        case job, ok := <-wp.jobs:
            if !ok {
                return
            }

            result := wp.processJob(job)

            select {
            case wp.results <- result:
            case <-wp.ctx.Done():
                return
            }
        }
    }
}

func (wp *WorkerPool) processJob(job Job) Result {
    // Обработка задачи
    return Result{Job: job, Output: job.Process()}
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

### Практические примеры: Pipeline с обработкой ошибок

```go
type PipelineStage func(<-chan int) (<-chan int, <-chan error)

func multiplyStage(factor int) PipelineStage {
    return func(input <-chan int) (<-chan int, <-chan error) {
        output := make(chan int)
        errors := make(chan error)

        go func() {
            defer close(output)
            defer close(errors)

            for n := range input {
                if n < 0 {
                    errors <- fmt.Errorf("negative number: %d", n)
                    continue
                }
                output <- n * factor
            }
        }()

        return output, errors
    }
}

func addStage(value int) PipelineStage {
    return func(input <-chan int) (<-chan int, <-chan error) {
        output := make(chan int)
        errors := make(chan error)

        go func() {
            defer close(output)
            defer close(errors)

            for n := range input {
                result := n + value
                if result > 1000 {
                    errors <- fmt.Errorf("result too large: %d", result)
                    continue
                }
                output <- result
            }
        }()

        return output, errors
    }
}

func RunPipeline(input <-chan int, stages ...PipelineStage) (<-chan int, <-chan error) {
    current := input
    allErrors := make(chan error)

    for _, stage := range stages {
        var stageErrors <-chan error
        current, stageErrors = stage(current)

        go func(errCh <-chan error) {
            for err := range errCh {
                allErrors <- err
            }
        }(stageErrors)
    }

    return current, allErrors
}
```

### Практические примеры: Circuit Breaker

```go
type CircuitBreaker struct {
    maxFailures int
    timeout     time.Duration
    failures    int
    lastFailure time.Time
    state       string // "closed", "open", "half-open"
    mu          sync.RWMutex
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
    defer cb.mu.Unlock()

    // Проверка состояния
    if cb.state == "open" {
        if time.Since(cb.lastFailure) > cb.timeout {
            cb.state = "half-open"
        } else {
            return fmt.Errorf("circuit breaker is open")
        }
    }

    // Выполнение функции
    err := fn()

    if err != nil {
        cb.failures++
        cb.lastFailure = time.Now()

        if cb.failures >= cb.maxFailures {
            cb.state = "open"
        }

        return err
    }

    // Успешное выполнение
    if cb.state == "half-open" {
        cb.state = "closed"
    }
    cb.failures = 0

    return nil
}
```

### Практические примеры: Retry Pattern

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

func Retry(ctx context.Context, config *RetryConfig, fn func() error) error {
    var lastErr error
    delay := config.InitialDelay

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

### Практические примеры: Backpressure Pattern

```go
type Backpressure struct {
    capacity int
    queue    chan interface{}
    mu       sync.Mutex
    dropped  int64
}

func NewBackpressure(capacity int) *Backpressure {
    return &Backpressure{
        capacity: capacity,
        queue:    make(chan interface{}, capacity),
    }
}

func (bp *Backpressure) Push(item interface{}) bool {
    select {
    case bp.queue <- item:
        return true
    default:
        bp.mu.Lock()
        bp.dropped++
        bp.mu.Unlock()
        return false
    }
}

func (bp *Backpressure) Pop() (interface{}, bool) {
    select {
    case item := <-bp.queue:
        return item, true
    default:
        return nil, false
    }
}

func (bp *Backpressure) Dropped() int64 {
    bp.mu.Lock()
    defer bp.mu.Unlock()
    return bp.dropped
}
```

### Практические примеры: Semaphore Pattern

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

### Практические примеры: Producer-Consumer Pattern

```go
type ProducerConsumer struct {
    buffer chan Item
    done   chan struct{}
}

func NewProducerConsumer(bufferSize int) *ProducerConsumer {
    return &ProducerConsumer{
        buffer: make(chan Item, bufferSize),
        done:   make(chan struct{}),
    }
}

func (pc *ProducerConsumer) Produce(item Item) error {
    select {
    case pc.buffer <- item:
        return nil
    case <-pc.done:
        return fmt.Errorf("producer stopped")
    }
}

func (pc *ProducerConsumer) Consume() (Item, error) {
    select {
    case item := <-pc.buffer:
        return item, nil
    case <-pc.done:
        return Item{}, fmt.Errorf("consumer stopped")
    }
}

func (pc *ProducerConsumer) Stop() {
    close(pc.done)
}
```

### Практические примеры: Barrier Pattern

```go
type Barrier struct {
    count    int
    current  int
    waiters  chan struct{}
    mu       sync.Mutex
}

func NewBarrier(count int) *Barrier {
    return &Barrier{
        count:   count,
        waiters: make(chan struct{}),
    }
}

func (b *Barrier) Wait() {
    b.mu.Lock()
    b.current++

    if b.current == b.count {
        // Все достигли барьера
        close(b.waiters)
        b.current = 0
        b.waiters = make(chan struct{})
        b.mu.Unlock()
    } else {
        waiters := b.waiters
        b.mu.Unlock()
        <-waiters
    }
}
```

### Практические примеры: Future Pattern

```go
type Future struct {
    result chan interface{}
    err    chan error
    done   chan struct{}
}

func NewFuture(fn func() (interface{}, error)) *Future {
    f := &Future{
        result: make(chan interface{}, 1),
        err:    make(chan error, 1),
        done:   make(chan struct{}),
    }

    go func() {
        defer close(f.done)
        result, err := fn()
        if err != nil {
            f.err <- err
        } else {
            f.result <- result
        }
    }()

    return f
}

func (f *Future) Get() (interface{}, error) {
    select {
    case result := <-f.result:
        return result, nil
    case err := <-f.err:
        return nil, err
    }
}

func (f *Future) GetWithTimeout(timeout time.Duration) (interface{}, error) {
    select {
    case result := <-f.result:
        return result, nil
    case err := <-f.err:
        return nil, err
    case <-time.After(timeout):
        return nil, fmt.Errorf("future timeout")
    }
}
```

### Практические примеры: Observer Pattern

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

func (s *Subject) Subscribe(observer Observer) {
    s.mu.Lock()
    defer s.mu.Unlock()
    s.observers = append(s.observers, observer)
}

func (s *Subject) Unsubscribe(observer Observer) {
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
        go observer.Update(event, data)
    }
}
```

### Практические примеры: Throttle Pattern

```go
type Throttler struct {
    interval time.Duration
    lastCall time.Time
    mu       sync.Mutex
}

func NewThrottler(interval time.Duration) *Throttler {
    return &Throttler{
        interval: interval,
        lastCall: time.Time{},
    }
}

func (t *Throttler) Throttle(fn func()) {
    t.mu.Lock()
    defer t.mu.Unlock()

    now := time.Now()
    if now.Sub(t.lastCall) >= t.interval {
        fn()
        t.lastCall = now
    }
}
```

### Практические примеры: Debounce Pattern

```go
type Debouncer struct {
    delay   time.Duration
    timer   *time.Timer
    mu      sync.Mutex
    pending func()
}

func NewDebouncer(delay time.Duration) *Debouncer {
    return &Debouncer{
        delay: delay,
    }
}

func (d *Debouncer) Debounce(fn func()) {
    d.mu.Lock()
    defer d.mu.Unlock()

    if d.timer != nil {
        d.timer.Stop()
    }

    d.pending = fn
    d.timer = time.AfterFunc(d.delay, func() {
        d.mu.Lock()
        defer d.mu.Unlock()
        if d.pending != nil {
            d.pending()
            d.pending = nil
        }
    })
}
```

### Практические примеры: Circuit Breaker паттерн

```go
type CircuitState int

const (
    StateClosed CircuitState = iota
    StateOpen
    StateHalfOpen
)

type CircuitBreaker struct {
    maxFailures   int
    timeout       time.Duration
    state         CircuitState
    failures      int
    lastFailTime  time.Time
    mu            sync.RWMutex
}

func NewCircuitBreaker(maxFailures int, timeout time.Duration) *CircuitBreaker {
    return &CircuitBreaker{
        maxFailures: maxFailures,
        timeout:     timeout,
        state:       StateClosed,
    }
}

func (cb *CircuitBreaker) Call(fn func() error) error {
    cb.mu.RLock()
    state := cb.state
    cb.mu.RUnlock()

    if state == StateOpen {
        if time.Since(cb.lastFailTime) > cb.timeout {
            cb.mu.Lock()
            cb.state = StateHalfOpen
            cb.mu.Unlock()
        } else {
            return fmt.Errorf("circuit breaker is open")
        }
    }

    err := fn()

    cb.mu.Lock()
    defer cb.mu.Unlock()

    if err != nil {
        cb.failures++
        cb.lastFailTime = time.Now()

        if cb.failures >= cb.maxFailures {
            cb.state = StateOpen
        }
        return err
    }

    cb.failures = 0
    cb.state = StateClosed
    return nil
}
```

### Практические примеры: Retry паттерн с экспоненциальной задержкой

```go
type RetryConfig struct {
    MaxAttempts int
    InitialDelay time.Duration
    MaxDelay     time.Duration
    Multiplier   float64
    Retryable    func(error) bool
}

type Retrier struct {
    config RetryConfig
}

func NewRetrier(config RetryConfig) *Retrier {
    return &Retrier{config: config}
}

func (r *Retrier) Do(ctx context.Context, fn func() error) error {
    delay := r.config.InitialDelay

    for attempt := 0; attempt < r.config.MaxAttempts; attempt++ {
        err := fn()
        if err == nil {
            return nil
        }

        if !r.config.Retryable(err) {
            return err
        }

        if attempt == r.config.MaxAttempts-1 {
            return fmt.Errorf("max attempts reached: %w", err)
        }

        select {
        case <-ctx.Done():
            return ctx.Err()
        case <-time.After(delay):
            delay = time.Duration(float64(delay) * r.config.Multiplier)
            if delay > r.config.MaxDelay {
                delay = r.config.MaxDelay
            }
        }
    }

    return fmt.Errorf("retry failed")
}
```

### Практические примеры: Bulkhead паттерн

```go
type Bulkhead struct {
    maxConcurrency int
    semaphore      chan struct{}
    timeout        time.Duration
}

func NewBulkhead(maxConcurrency int, timeout time.Duration) *Bulkhead {
    return &Bulkhead{
        maxConcurrency: maxConcurrency,
        semaphore:      make(chan struct{}, maxConcurrency),
        timeout:        timeout,
    }
}

func (b *Bulkhead) Execute(ctx context.Context, fn func() error) error {
    ctx, cancel := context.WithTimeout(ctx, b.timeout)
    defer cancel()

    select {
    case b.semaphore <- struct{}{}:
        defer func() { <-b.semaphore }()
        return fn()
    case <-ctx.Done():
        return fmt.Errorf("bulkhead timeout: %w", ctx.Err())
    }
}
```

### Практические примеры: Timeout паттерн

```go
func WithTimeout(ctx context.Context, timeout time.Duration, fn func(context.Context) error) error {
    ctx, cancel := context.WithTimeout(ctx, timeout)
    defer cancel()

    done := make(chan error, 1)
    go func() {
        done <- fn(ctx)
    }()

    select {
    case err := <-done:
        return err
    case <-ctx.Done():
        return fmt.Errorf("operation timeout: %w", ctx.Err())
    }
}

func WithDeadline(ctx context.Context, deadline time.Time, fn func(context.Context) error) error {
    ctx, cancel := context.WithDeadline(ctx, deadline)
    defer cancel()

    done := make(chan error, 1)
    go func() {
        done <- fn(ctx)
    }()

    select {
    case err := <-done:
        return err
    case <-ctx.Done():
        return fmt.Errorf("operation deadline exceeded: %w", ctx.Err())
    }
}
```

### Практические примеры: Timeout и Retry комбинированный паттерн

```go
type TimeoutRetryConfig struct {
    Timeout      time.Duration
    MaxRetries   int
    RetryDelay   time.Duration
}

func ExecuteWithTimeoutAndRetry(
    ctx context.Context,
    config TimeoutRetryConfig,
    fn func(context.Context) error,
) error {
    var lastErr error

    for attempt := 0; attempt < config.MaxRetries; attempt++ {
        timeoutCtx, cancel := context.WithTimeout(ctx, config.Timeout)

        err := fn(timeoutCtx)
        cancel()

        if err == nil {
            return nil
        }

        lastErr = err

        if attempt < config.MaxRetries-1 {
            select {
            case <-ctx.Done():
                return ctx.Err()
            case <-time.After(config.RetryDelay):
            }
        }
    }

    return fmt.Errorf("operation failed after %d retries: %w",
        config.MaxRetries, lastErr)
}
```

### Практические примеры: Graceful shutdown паттерн

```go
type ShutdownManager struct {
    services []Shutdownable
    timeout  time.Duration
    mu       sync.Mutex
}

type Shutdownable interface {
    Shutdown(ctx context.Context) error
}

func NewShutdownManager(timeout time.Duration) *ShutdownManager {
    return &ShutdownManager{
        services: make([]Shutdownable, 0),
        timeout:  timeout,
    }
}

func (sm *ShutdownManager) Register(service Shutdownable) {
    sm.mu.Lock()
    defer sm.mu.Unlock()
    sm.services = append(sm.services, service)
}

func (sm *ShutdownManager) Shutdown(ctx context.Context) error {
    ctx, cancel := context.WithTimeout(ctx, sm.timeout)
    defer cancel()

    errCh := make(chan error, len(sm.services))
    var wg sync.WaitGroup

    sm.mu.Lock()
    services := sm.services
    sm.mu.Unlock()

    for _, service := range services {
        wg.Add(1)
        go func(svc Shutdownable) {
            defer wg.Done()
            if err := svc.Shutdown(ctx); err != nil {
                errCh <- err
            }
        }(service)
    }

    go func() {
        wg.Wait()
        close(errCh)
    }()

    var errs []error
    for err := range errCh {
        errs = append(errs, err)
    }

    if len(errs) > 0 {
        return fmt.Errorf("shutdown errors: %v", errs)
    }

    return nil
}
```

## Лучшие практики

1. **Используйте context** — для отмены операций
2. **Закрывайте каналы** — для предотвращения утечек
3. **Используйте WaitGroup** — для синхронизации горутин
4. **Ограничивайте ресурсы** — используйте **semaphores** для ограничения
5. **Обрабатывайте ошибки** — правильно обрабатывайте ошибки в паттернах
6. **Используйте circuit breaker** — для защиты от каскадных отказов
7. **Используйте retry** — для обработки временных ошибок
8. **Используйте backpressure** — для управления нагрузкой
9. **Используйте throttling** — для ограничения частоты вызовов
10. **Используйте debouncing** — для группировки событий
11. **Используйте timeout** — для предотвращения зависаний
12. **Используйте graceful shutdown** — для корректного завершения
13. **Комбинируйте паттерны** — используйте несколько паттернов вместе
14. **Тестируйте паттерны** — проверяйте поведение паттернов
15. **Мониторьте паттерны** — отслеживайте использование паттернов

### Практические примеры: Event-driven паттерн

```go
type Event struct {
    Type string
    Data interface{}
}

type EventBus struct {
    subscribers map[string][]chan Event
    mu          sync.RWMutex
}

func NewEventBus() *EventBus {
    return &EventBus{
        subscribers: make(map[string][]chan Event),
    }
}

func (eb *EventBus) Subscribe(eventType string) <-chan Event {
    eb.mu.Lock()
    defer eb.mu.Unlock()

    ch := make(chan Event, 10)
    eb.subscribers[eventType] = append(eb.subscribers[eventType], ch)

    return ch
}

func (eb *EventBus) Publish(event Event) {
    eb.mu.RLock()
    defer eb.mu.RUnlock()

    for _, ch := range eb.subscribers[event.Type] {
        select {
        case ch <- event:
        default:
            // Канал переполнен, пропускаем
        }
    }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Продвинутые паттерны в Go предоставляют мощные инструменты для создания эффективных конкурентных программ. Понимание **worker pools**, **pipelines**, **fan-out**/**fan-in**, **rate limiting**, **circuit breaker**, **retry**, **timeout**, **event-driven** и других паттернов критично для создания сложных приложений в Go. Правильное использование этих паттернов позволяет создавать масштабируемые, надежные, отказоустойчивые, гибкие и эффективные приложения, которые корректно обрабатывают ошибки, управляют ресурсами и реагируют на события.

## Дополнительные ресурсы

- [Go Concurrency Patterns](https://go.dev/blog/pipelines)
- [Advanced Go Concurrency Patterns](https://go.dev/blog/advanced-go-concurrency-patterns)

## См. также

- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
- [[go-collections|Go: коллекции]]
