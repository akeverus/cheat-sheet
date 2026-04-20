---
title: "Go: Context"
description: "Полное руководство по Context в Go: отмена операций, таймауты, передача значений, best practices"
tags:
  - go
  - golang
  - context
  - cancellation
  - timeout
difficulty: "intermediate"
prerequisites: ["go/go-basics.md", "go/go-concurrency.md"]
updated: "2026-02-06"
---

# Go: **Context**

## Полезные ссылки

- [Go context Package](https://pkg.go.dev/context)
- [Go Context Blog Post](https://go.dev/blog/context)

## Содержание

- [Go: **Context**](#go-context)
- [Введение в **Context**](#введение-в-context)
  - [Основные случаи использования](#основные-случаи-использования)
- [Создание **Context**](#создание-context)
  - [**Background Context**](#background-context)
  - [**TODO Context**](#todo-context)
- [Отмена операций](#отмена-операций)
  - [**WithCancel**](#withcancel)
  - [Проверка отмены](#проверка-отмены)
- [Таймауты](#таймауты)
  - [**WithTimeout**](#withtimeout)
  - [**WithDeadline**](#withdeadline)
- [Передача значений](#передача-значений)
  - [**WithValue**](#withvalue)
  - [Типизированные ключи](#типизированные-ключи)
- [Практические примеры](#практические-примеры)
  - [**HTTP** запрос с таймаутом](#http-запрос-с-таймаутом)
  - [Параллельная обработка с отменой](#параллельная-обработка-с-отменой)
  - [**Database** запрос с таймаутом](#database-запрос-с-таймаутом)
  - [Практические примеры: **Context** с цепочкой операций](#практические-примеры-context-с-цепочкой-операций)
  - [Практические примеры: **Context** с приоритетной отменой](#практические-примеры-context-с-приоритетной-отменой)
  - [Практические примеры: **Context** с прогрессом](#практические-примеры-context-с-прогрессом)
  - [Практические примеры: **Context** с таймаутом на операцию](#практические-примеры-context-с-таймаутом-на-операцию)
  - [Практические примеры: **Context** с **retry**](#практические-примеры-context-с-retry)
  - [Практические примеры: **Context** с **deadline propagation**](#практические-примеры-context-с-deadline-propagation)
  - [Практические примеры: **Context** с **tracing**](#практические-примеры-context-с-tracing)
  - [Практические примеры: **Context** для запросов **HTTP**](#практические-примеры-context-для-запросов-http)
  - [Практические примеры: **Context** для базы данных](#практические-примеры-context-для-базы-данных)
  - [Практические примеры: **Context** для **gRPC**](#практические-примеры-context-для-grpc)
  - [Практические примеры: **Context** для каскадной отмены](#практические-примеры-context-для-каскадной-отмены)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Context**

Пакет `**context**` предоставляет механизм для передачи сигналов отмены, таймаутов и значений через границы **API** и между горутинами.

### Основные случаи использования

1. **Отмена операций** — передача сигнала отмены
2. **Таймауты** — установка временных ограничений
3. **Передача значений** — передача данных через границы **API**
4. **Управление жизненным циклом** — контроль выполнения операций

## Создание **Context**

### **Background Context**

```go
import "context"

// Создание базового context
ctx := context.Background()

// Использование в функциях
func process(ctx context.Context) error {
    // обработка
    return nil
}
```

### **TODO Context**

```go
// Создание TODO context (для временного использования)
ctx := context.TODO()
```

## Отмена операций

### **WithCancel**

```go
import "context"

func processWithCancel() {
    ctx, cancel := context.WithCancel(context.Background())
    defer cancel()  // Важно: всегда вызывайте cancel

    go func() {
        // Долгая операция
        time.Sleep(5 * time.Second)
        cancel()  // Отмена операции
    }()

    select {
    case <-ctx.Done():
        fmt.Println("Operation cancelled")
    case <-time.After(10 * time.Second):
        fmt.Println("Operation completed")
    }
}
```

### Проверка отмены

```go
func longOperation(ctx context.Context) error {
    for {
        // Проверка отмены
        select {
        case <-ctx.Done():
            return ctx.Err()
        default:
            // Продолжение работы
        }

        // Выполнение работы
        doWork()
    }
}
```

## Таймауты

### **WithTimeout**

```go
import (
    "context"
    "time"
)

func processWithTimeout() {
    // Context с таймаутом
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    // Выполнение операции
    err := doWork(ctx)
    if err != nil {
        if err == context.DeadlineExceeded {
            fmt.Println("Operation timed out")
        }
    }
}
```

### **WithDeadline**

```go
func processWithDeadline() {
    // Context с дедлайном
    deadline := time.Now().Add(5 * time.Second)
    ctx, cancel := context.WithDeadline(context.Background(), deadline)
    defer cancel()

    // Выполнение операции
    doWork(ctx)
}
```

## Передача значений

### **WithValue**

```go
import "context"

type key string

const userIDKey key = "userID"

func processWithValue() {
    // Context с значением
    ctx := context.WithValue(context.Background(), userIDKey, 123)

    // Получение значения
    userID := ctx.Value(userIDKey).(int)
    fmt.Println("User ID:", userID)
}
```

### Типизированные ключи

```go
type contextKey string

const (
    userIDKey contextKey = "userID"
    requestIDKey contextKey = "requestID"
)

func setUserID(ctx context.Context, userID int) context.Context {
    return context.WithValue(ctx, userIDKey, userID)
}

func getUserID(ctx context.Context) (int, bool) {
    userID, ok := ctx.Value(userIDKey).(int)
    return userID, ok
}
```

## Практические примеры

### **HTTP** запрос с таймаутом

```go
import (
    "context"
    "net/http"
    "time"
)

func makeRequest(ctx context.Context, url string) (*http.Response, error) {
    req, err := http.NewRequestWithContext(ctx, "GET", url, nil)
    if err != nil {
        return nil, err
    }

    client := &http.Client{
        Timeout: 10 * time.Second,
    }

    return client.Do(req)
}

func main() {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()

    resp, err := makeRequest(ctx, "https://api.example.com/data")
    if err != nil {
        if err == context.DeadlineExceeded {
            fmt.Println("Request timed out")
        }
        return
    }
    defer resp.Body.Close()
}
```

### Параллельная обработка с отменой

```go
func processItems(ctx context.Context, items []Item) error {
    errCh := make(chan error, len(items))

    for _, item := range items {
        go func(item Item) {
            select {
            case <-ctx.Done():
                errCh <- ctx.Err()
            default:
                errCh <- processItem(ctx, item)
            }
        }(item)
    }

    for i := 0; i < len(items); i++ {
        if err := <-errCh; err != nil {
            return err
        }
    }

    return nil
}
```

### **Database** запрос с таймаутом

```go
func queryDatabase(ctx context.Context, query string) ([]Row, error) {
    // Проверка отмены перед запросом
    select {
    case <-ctx.Done():
        return nil, ctx.Err()
    default:
    }

    // Выполнение запроса
    rows, err := db.QueryContext(ctx, query)
    if err != nil {
        return nil, err
    }
    defer rows.Close()

    var results []Row
    for rows.Next() {
        // Проверка отмены во время обработки
        select {
        case <-ctx.Done():
            return nil, ctx.Err()
        default:
        }

        var row Row
        if err := rows.Scan(&row); err != nil {
            return nil, err
        }
        results = append(results, row)
    }

    return results, nil
}
```

### Практические примеры: **Context** с цепочкой операций

```go
func processChain(ctx context.Context, steps []func(context.Context) error) error {
    for i, step := range steps {
        // Проверка отмены перед каждым шагом
        select {
        case <-ctx.Done():
            return fmt.Errorf("cancelled at step %d: %w", i, ctx.Err())
        default:
        }

        if err := step(ctx); err != nil {
            return fmt.Errorf("step %d failed: %w", i, err)
        }
    }
    return nil
}
```

### Практические примеры: **Context** с приоритетной отменой

```go
func processWithPriority(ctx context.Context, highPriority, lowPriority []Task) error {
    ctx, cancel := context.WithCancel(ctx)
    defer cancel()

    // Выполнение высокоприоритетных задач
    errCh := make(chan error, 1)
    go func() {
        errCh <- processTasks(ctx, highPriority)
    }()

    // Выполнение низкоприоритетных задач с возможностью отмены
    go func() {
        select {
        case <-ctx.Done():
            return
        default:
            processTasks(ctx, lowPriority)
        }
    }()

    return <-errCh
}
```

### Практические примеры: **Context** с прогрессом

```go
type ProgressContext struct {
    context.Context
    progress chan int
}

func NewProgressContext(ctx context.Context) (*ProgressContext, context.CancelFunc) {
    childCtx, cancel := context.WithCancel(ctx)
    return &ProgressContext{
        Context:  childCtx,
        progress: make(chan int, 1),
    }, cancel
}

func (pc *ProgressContext) ReportProgress(percent int) {
    select {
    case pc.progress <- percent:
    case <-pc.Done():
    default:
    }
}

func processWithProgress(ctx context.Context, items []Item) error {
    progCtx, cancel := NewProgressContext(ctx)
    defer cancel()

    go func() {
        for progress := range progCtx.progress {
            fmt.Printf("Progress: %d%%\n", progress)
        }
    }()

    for i, item := range items {
        if err := processItem(progCtx, item); err != nil {
            return err
        }
        progCtx.ReportProgress((i + 1) * 100 / len(items))
    }

    return nil
}
```

### Практические примеры: **Context** с таймаутом на операцию

```go
func processWithOperationTimeout(ctx context.Context, timeout time.Duration, fn func(context.Context) error) error {
    opCtx, cancel := context.WithTimeout(ctx, timeout)
    defer cancel()

    errCh := make(chan error, 1)
    go func() {
        errCh <- fn(opCtx)
    }()

    select {
    case err := <-errCh:
        return err
    case <-opCtx.Done():
        return fmt.Errorf("operation timeout: %w", opCtx.Err())
    }
}
```

### Практические примеры: **Context** с **retry**

```go
func retryWithContext(ctx context.Context, maxRetries int, fn func(context.Context) error) error {
    var lastErr error

    for i := 0; i < maxRetries; i++ {
        // Проверка отмены перед каждой попыткой
        select {
        case <-ctx.Done():
            return ctx.Err()
        default:
        }

        err := fn(ctx)
        if err == nil {
            return nil
        }

        lastErr = err

        // Ожидание перед следующей попыткой
        if i < maxRetries-1 {
            select {
            case <-ctx.Done():
                return ctx.Err()
            case <-time.After(time.Duration(i+1) * time.Second):
            }
        }
    }

    return fmt.Errorf("failed after %d retries: %w", maxRetries, lastErr)
}
```

### Практические примеры: **Context** с **deadline propagation**

```go
func propagateDeadline(ctx context.Context, operations []func(context.Context) error) error {
    // Вычисление оставшегося времени
    deadline, ok := ctx.Deadline()
    if !ok {
        deadline = time.Now().Add(30 * time.Second)
    }

    remaining := time.Until(deadline)
    perOpTimeout := remaining / time.Duration(len(operations))

    for i, op := range operations {
        opCtx, cancel := context.WithDeadline(ctx, deadline.Add(-time.Duration(len(operations)-i-1)*perOpTimeout))

        err := op(opCtx)
        cancel()

        if err != nil {
            return fmt.Errorf("operation %d failed: %w", i, err)
        }
    }

    return nil
}
```

### Практические примеры: **Context** с **tracing**

```go
type TraceContext struct {
    context.Context
    traceID string
    spanID  string
}

func NewTraceContext(ctx context.Context, traceID string) *TraceContext {
    return &TraceContext{
        Context: ctx,
        traceID: traceID,
        spanID:  generateSpanID(),
    }
}

func (tc *TraceContext) TraceID() string {
    return tc.traceID
}

func (tc *TraceContext) SpanID() string {
    return tc.spanID
}

func processWithTracing(ctx context.Context, operation string) error {
    traceCtx := NewTraceContext(ctx, generateTraceID())

    log.Printf("[%s] Starting %s", traceCtx.TraceID(), operation)
    defer log.Printf("[%s] Completed %s", traceCtx.TraceID(), operation)

    return doWork(traceCtx)
}
```

### Практические примеры: **Context** для запросов **HTTP**

```go
func HTTPHandler(w http.ResponseWriter, r *http.Request) {
    ctx := r.Context()

    // Добавление таймаута
    ctx, cancel := context.WithTimeout(ctx, 5*time.Second)
    defer cancel()

    // Передача context в бизнес-логику
    result, err := processRequest(ctx, r)
    if err != nil {
        if errors.Is(err, context.DeadlineExceeded) {
            http.Error(w, "Request timeout", http.StatusRequestTimeout)
            return
        }
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }

    json.NewEncoder(w).Encode(result)
}

func processRequest(ctx context.Context, r *http.Request) (interface{}, error) {
    // Проверка отмены перед долгой операцией
    select {
    case <-ctx.Done():
        return nil, ctx.Err()
    default:
    }

    // Долгая операция с проверкой context
    return longRunningOperation(ctx)
}
```

### Практические примеры: **Context** для базы данных

```go
func QueryWithContext(ctx context.Context, db *sql.DB, query string, args ...interface{}) (*sql.Rows, error) {
    // Создание context с таймаутом для запроса
    queryCtx, cancel := context.WithTimeout(ctx, 10*time.Second)
    defer cancel()

    return db.QueryContext(queryCtx, query, args...)
}

func TransactionWithContext(ctx context.Context, db *sql.DB, fn func(*sql.Tx) error) error {
    tx, err := db.BeginTx(ctx, nil)
    if err != nil {
        return err
    }

    defer func() {
        if err != nil {
            tx.Rollback()
        }
    }()

    if err := fn(tx); err != nil {
        return err
    }

    return tx.Commit()
}
```

### Практические примеры: **Context** для **gRPC**

```go
func (s *Server) GetUser(ctx context.Context, req *pb.GetUserRequest) (*pb.User, error) {
    // Проверка отмены
    if ctx.Err() != nil {
        return nil, ctx.Err()
    }

    // Извлечение метаданных
    md, ok := metadata.FromIncomingContext(ctx)
    if ok {
        userID := md.Get("user-id")
        // Использование userID
    }

    // Вызов с context
    user, err := s.repo.GetUser(ctx, req.Id)
    if err != nil {
        return nil, err
    }

    return user, nil
}
```

### Практические примеры: **Context** для каскадной отмены

```go
func ProcessWithCancellation(ctx context.Context, tasks []Task) error {
    ctx, cancel := context.WithCancel(ctx)
    defer cancel()

    errCh := make(chan error, len(tasks))

    for _, task := range tasks {
        go func(t Task) {
            if err := t.Execute(ctx); err != nil {
                errCh <- err
                cancel() // Отмена всех задач при ошибке
            }
        }(task)
    }

    // Ожидание завершения или отмены
    select {
    case <-ctx.Done():
        return ctx.Err()
    case err := <-errCh:
        return err
    }
}
```

## Лучшие практики

1. **Всегда передавайте context первым параметром** — конвенция Go
2. **Не храните context в структурах** — передавайте как параметр
3. **Всегда вызывайте cancel** — используйте **defer** для гарантированного вызова
4. **Используйте типизированные ключи** — для **WithValue**
5. **Проверяйте ctx.`Done()`** — в долгих операциях
6. **Не передавайте nil context** — используйте **context.Background**() или **context.TODO**()
7. **Используйте таймауты** — устанавливайте разумные таймауты
8. **Распространяйте context** — передавайте **context** через все уровни
9. **Обрабатывайте ошибки context** — проверяйте **context.DeadlineExceeded** и **context.Canceled**
10. **Используйте context для отмены** — не используйте каналы для отмены, используйте **context**
11. **Используйте context в HTTP** — передавайте **context** через **handlers**
12. **Используйте context в БД** — для отмены долгих запросов
13. **Используйте context в gRPC** — для управления жизненным циклом запросов
14. **Используйте каскадную отмену** — для отмены связанных операций
15. **Мониторьте таймауты** — логируйте превышения таймаутов


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Context** в Go предоставляет мощные инструменты для управления жизненным циклом операций, отмены, таймаутов, передачи значений и координации конкурентных операций. Понимание создания **context**, отмены операций, таймаутов, передачи значений, практических применений в различных контекстах и лучших практик критично для эффективного использования **context** в Go. Правильное использование **context** позволяет создавать отзывчивые, управляемые и надежные приложения, которые корректно обрабатывают отмены и таймауты.

## Дополнительные ресурсы

- [Go context Package](https://pkg.go.dev/context)
- [Go Context Blog Post](https://go.dev/blog/context)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
