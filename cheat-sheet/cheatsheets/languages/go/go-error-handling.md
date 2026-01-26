---
title: "Go: обработка ошибок"
description: "Полное руководство по обработке ошибок в Go: error interface, error wrapping, errors.Is, errors.As, best practices"
tags: ["go", "golang", "errors", "error-handling", "wrapping"]
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2025-01-11"
---

# Go: обработка ошибок

**Дата последнего обновления:** 2025-01-11

## Полезные ссылки

- [Go Error Handling](https://go.dev/blog/error-handling-and-go)
- [Go errors Package](https://pkg.go.dev/errors)
- [Go Error Wrapping](https://go.dev/blog/go1.13-errors)

## Содержание

- [Введение в обработку ошибок](#введение-в-обработку-ошибок)
- [Error Interface](#error-interface)
- [Создание ошибок](#создание-ошибок)
- [Error Wrapping](#error-wrapping)
- [Проверка ошибок](#проверка-ошибок)
- [Кастомные ошибки](#кастомные-ошибки)
- [Лучшие практики](#лучшие-практики)

## Введение в обработку ошибок

Go использует явную обработку ошибок через возврат значений ошибок из функций. Понимание работы с ошибками критично для создания надежных приложений.

### Принципы обработки ошибок в Go

1. **Явная обработка** - ошибки возвращаются как значения
2. **Проверка ошибок** - всегда проверяйте возвращаемые ошибки
3. **Контекст ошибок** - добавляйте контекст к ошибкам
4. **Типизация ошибок** - используйте типизированные ошибки для проверки

## Error Interface

Интерфейс `error` является основой обработки ошибок в Go.

### Определение error

```go
type error interface {
    Error() string
}
```

### Базовое использование

```go
func divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}

// Использование
result, err := divide(10, 2)
if err != nil {
    log.Fatal(err)
}
fmt.Println(result)
```

## Создание ошибок

### errors.New

```go
import "errors"

err := errors.New("something went wrong")
```

### fmt.Errorf

```go
import "fmt"

err := fmt.Errorf("failed to process user %d: %v", userID, cause)
```

### Кастомные ошибки

```go
type ValidationError struct {
    Field   string
    Message string
}

func (e *ValidationError) Error() string {
    return fmt.Sprintf("validation error on field %s: %s", e.Field, e.Message)
}
```

## Error Wrapping

Error Wrapping позволяет добавлять контекст к ошибкам, сохраняя исходную ошибку.

### fmt.Errorf с %w

```go
import "fmt"

func processUser(id int) error {
    user, err := getUser(id)
    if err != nil {
        return fmt.Errorf("failed to process user %d: %w", id, err)
    }
    // обработка пользователя
    return nil
}
```

### errors.Unwrap

```go
import "errors"

func unwrapError(err error) error {
    return errors.Unwrap(err)
}
```

### errors.Is

```go
import "errors"

var ErrNotFound = errors.New("not found")

func checkError(err error) bool {
    return errors.Is(err, ErrNotFound)
}
```

### errors.As

```go
import "errors"

func checkCustomError(err error) {
    var validationErr *ValidationError
    if errors.As(err, &validationErr) {
        fmt.Printf("Field: %s, Message: %s\n", 
            validationErr.Field, validationErr.Message)
    }
}
```

## Проверка ошибок

### Базовая проверка

```go
result, err := someFunction()
if err != nil {
    // обработка ошибки
    return err
}
```

### Проверка конкретных ошибок

```go
import "errors"

var ErrNotFound = errors.New("not found")

result, err := getItem(id)
if errors.Is(err, ErrNotFound) {
    // обработка ошибки "not found"
    return nil, nil
}
if err != nil {
    // обработка других ошибок
    return nil, err
}
```

### Проверка типизированных ошибок

```go
result, err := validateUser(user)
var validationErr *ValidationError
if errors.As(err, &validationErr) {
    // обработка ошибки валидации
    fmt.Printf("Validation error: %s\n", validationErr.Message)
}
```

## Кастомные ошибки

### Структурированные ошибки

```go
type APIError struct {
    Code    int
    Message string
    Cause   error
}

func (e *APIError) Error() string {
    return fmt.Sprintf("API error %d: %s", e.Code, e.Message)
}

func (e *APIError) Unwrap() error {
    return e.Cause
}
```

### Использование кастомных ошибок

```go
func makeRequest(url string) error {
    resp, err := http.Get(url)
    if err != nil {
        return &APIError{
            Code:    500,
            Message: "failed to make request",
            Cause:   err,
        }
    }
    defer resp.Body.Close()
    
    if resp.StatusCode != http.StatusOK {
        return &APIError{
            Code:    resp.StatusCode,
            Message: "unexpected status code",
        }
    }
    
    return nil
}
```

### Практические примеры: Обработка ошибок в HTTP handlers

```go
func handleGetUser(w http.ResponseWriter, r *http.Request) {
    id, err := strconv.Atoi(r.URL.Query().Get("id"))
    if err != nil {
        http.Error(w, "invalid user ID", http.StatusBadRequest)
        return
    }
    
    user, err := getUser(id)
    if err != nil {
        if errors.Is(err, ErrNotFound) {
            http.Error(w, "user not found", http.StatusNotFound)
            return
        }
        http.Error(w, "internal server error", http.StatusInternalServerError)
        return
    }
    
    json.NewEncoder(w).Encode(user)
}
```

### Практические примеры: Обработка ошибок в database операциях

```go
func createUser(db *sql.DB, user User) error {
    query := "INSERT INTO users (name, email) VALUES ($1, $2) RETURNING id"
    
    err := db.QueryRow(query, user.Name, user.Email).Scan(&user.ID)
    if err != nil {
        if errors.Is(err, sql.ErrNoRows) {
            return fmt.Errorf("failed to create user: %w", err)
        }
        
        // Проверка на constraint violation
        var pgErr *pq.Error
        if errors.As(err, &pgErr) {
            if pgErr.Code == "23505" { // unique_violation
                return fmt.Errorf("user with email %s already exists: %w", 
                    user.Email, err)
            }
        }
        
        return fmt.Errorf("database error: %w", err)
    }
    
    return nil
}
```

### Практические примеры: Обработка ошибок в file operations

```go
func readConfigFile(filename string) (*Config, error) {
    file, err := os.Open(filename)
    if err != nil {
        if os.IsNotExist(err) {
            return nil, fmt.Errorf("config file %s not found: %w", filename, err)
        }
        if os.IsPermission(err) {
            return nil, fmt.Errorf("permission denied for %s: %w", filename, err)
        }
        return nil, fmt.Errorf("failed to open config file: %w", err)
    }
    defer file.Close()
    
    var config Config
    decoder := json.NewDecoder(file)
    if err := decoder.Decode(&config); err != nil {
        return nil, fmt.Errorf("failed to decode config file: %w", err)
    }
    
    return &config, nil
}
```

### Практические примеры: Обработка ошибок в network operations

```go
func makeHTTPRequest(url string) (*http.Response, error) {
    client := &http.Client{
        Timeout: 10 * time.Second,
    }
    
    resp, err := client.Get(url)
    if err != nil {
        // Проверка на timeout
        if netErr, ok := err.(net.Error); ok && netErr.Timeout() {
            return nil, fmt.Errorf("request to %s timed out: %w", url, err)
        }
        
        // Проверка на DNS error
        if dnsErr, ok := err.(*net.DNSError); ok {
            return nil, fmt.Errorf("DNS error for %s: %w", url, dnsErr)
        }
        
        return nil, fmt.Errorf("failed to make request to %s: %w", url, err)
    }
    
    if resp.StatusCode != http.StatusOK {
        resp.Body.Close()
        return nil, &HTTPError{
            StatusCode: resp.StatusCode,
            URL:        url,
            Message:    fmt.Sprintf("unexpected status code: %d", resp.StatusCode),
        }
    }
    
    return resp, nil
}

type HTTPError struct {
    StatusCode int
    URL        string
    Message    string
}

func (e *HTTPError) Error() string {
    return fmt.Sprintf("HTTP error %d for %s: %s", e.StatusCode, e.URL, e.Message)
}
```

### Практические примеры: Обработка ошибок в concurrent operations

```go
func processItemsConcurrently(items []Item) error {
    var wg sync.WaitGroup
    errCh := make(chan error, len(items))
    
    for _, item := range items {
        wg.Add(1)
        go func(it Item) {
            defer wg.Done()
            
            if err := processItem(it); err != nil {
                errCh <- fmt.Errorf("failed to process item %d: %w", it.ID, err)
            }
        }(item)
    }
    
    wg.Wait()
    close(errCh)
    
    var errors []error
    for err := range errCh {
        errors = append(errors, err)
    }
    
    if len(errors) > 0 {
        return fmt.Errorf("failed to process %d items: %v", len(errors), errors)
    }
    
    return nil
}
```

### Практические примеры: Обработка ошибок с retry

```go
func retryOperation(operation func() error, maxRetries int, delay time.Duration) error {
    var lastErr error
    
    for i := 0; i < maxRetries; i++ {
        err := operation()
        if err == nil {
            return nil
        }
        
        lastErr = err
        
        // Проверка на временные ошибки
        if !isTemporaryError(err) {
            return fmt.Errorf("non-retryable error: %w", err)
        }
        
        if i < maxRetries-1 {
            time.Sleep(delay)
            delay *= 2 // Exponential backoff
        }
    }
    
    return fmt.Errorf("operation failed after %d retries: %w", maxRetries, lastErr)
}

func isTemporaryError(err error) bool {
    if netErr, ok := err.(net.Error); ok {
        return netErr.Temporary() || netErr.Timeout()
    }
    return false
}
```

### Практические примеры: Обработка ошибок с context

```go
func processWithContext(ctx context.Context, data []byte) error {
    // Проверка на отмену контекста
    if err := ctx.Err(); err != nil {
        return fmt.Errorf("context cancelled: %w", err)
    }
    
    // Обработка данных
    result, err := processData(data)
    if err != nil {
        return fmt.Errorf("failed to process data: %w", err)
    }
    
    // Проверка на timeout
    select {
    case <-ctx.Done():
        return fmt.Errorf("operation timed out: %w", ctx.Err())
    default:
        // Продолжение обработки
    }
    
    return saveResult(result)
}
```

### Практические примеры: Обработка ошибок валидации

```go
type ValidationErrors struct {
    Errors []ValidationError
}

func (e *ValidationErrors) Error() string {
    var messages []string
    for _, err := range e.Errors {
        messages = append(messages, err.Error())
    }
    return strings.Join(messages, "; ")
}

func (e *ValidationErrors) Add(field, message string) {
    e.Errors = append(e.Errors, ValidationError{
        Field:   field,
        Message: message,
    })
}

func validateUser(user User) error {
    var errors ValidationErrors
    
    if user.Name == "" {
        errors.Add("name", "name is required")
    }
    
    if user.Email == "" {
        errors.Add("email", "email is required")
    } else if !strings.Contains(user.Email, "@") {
        errors.Add("email", "email must be valid")
    }
    
    if len(errors.Errors) > 0 {
        return &errors
    }
    
    return nil
}
```

### Практические примеры: Обработка ошибок с logging

```go
func handleErrorWithLogging(err error, context map[string]interface{}) {
    logger := log.New(os.Stdout, "", log.LstdFlags)
    
    // Логирование с контекстом
    var fields []string
    for key, value := range context {
        fields = append(fields, fmt.Sprintf("%s=%v", key, value))
    }
    
    logger.Printf("Error: %v | Context: %s", err, strings.Join(fields, ", "))
    
    // Логирование цепочки ошибок
    current := err
    depth := 0
    for current != nil && depth < 10 {
        logger.Printf("  [%d] %v", depth, current)
        current = errors.Unwrap(current)
        depth++
    }
}
```

### Практические примеры: Обработка ошибок с metrics

```go
type ErrorMetrics struct {
    errorCounts map[string]int64
    mu          sync.RWMutex
}

func NewErrorMetrics() *ErrorMetrics {
    return &ErrorMetrics{
        errorCounts: make(map[string]int64),
    }
}

func (m *ErrorMetrics) RecordError(err error) {
    m.mu.Lock()
    defer m.mu.Unlock()
    
    errorType := reflect.TypeOf(err).String()
    m.errorCounts[errorType]++
}

func (m *ErrorMetrics) GetErrorCount(errorType string) int64 {
    m.mu.RLock()
    defer m.mu.RUnlock()
    
    return m.errorCounts[errorType]
}
```

### Практические примеры: Обработка ошибок с recovery

```go
func recoverFromPanic() {
    if r := recover(); r != nil {
        err, ok := r.(error)
        if !ok {
            err = fmt.Errorf("panic: %v", r)
        }
        
        // Логирование паники
        log.Printf("Recovered from panic: %v", err)
        
        // Отправка уведомления
        notifyPanic(err)
    }
}

func safeOperation() {
    defer recoverFromPanic()
    
    // Операция, которая может вызвать панику
    riskyOperation()
}
```

### Практические примеры: Обработка ошибок в middleware

```go
func ErrorMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        defer func() {
            if err := recover(); err != nil {
                log.Printf("Panic recovered: %v", err)
                http.Error(w, "Internal Server Error", http.StatusInternalServerError)
            }
        }()
        
        ww := &responseWriter{ResponseWriter: w}
        next.ServeHTTP(ww, r)
        
        if ww.statusCode >= 400 {
            log.Printf("Error: %d %s", ww.statusCode, ww.body)
        }
    })
}

type responseWriter struct {
    http.ResponseWriter
    statusCode int
    body       []byte
}

func (rw *responseWriter) WriteHeader(code int) {
    rw.statusCode = code
    rw.ResponseWriter.WriteHeader(code)
}

func (rw *responseWriter) Write(b []byte) (int, error) {
    rw.body = b
    return rw.ResponseWriter.Write(b)
}
```

### Практические примеры: Обработка ошибок в горутинах

```go
func ProcessWithErrorHandling(items []Item) []error {
    errCh := make(chan error, len(items))
    var wg sync.WaitGroup
    
    for _, item := range items {
        wg.Add(1)
        go func(it Item) {
            defer wg.Done()
            defer func() {
                if err := recover(); err != nil {
                    errCh <- fmt.Errorf("panic processing item %v: %w", it, err.(error))
                }
            }()
            
            if err := processItem(it); err != nil {
                errCh <- fmt.Errorf("error processing item %v: %w", it, err)
            }
        }(item)
    }
    
    go func() {
        wg.Wait()
        close(errCh)
    }()
    
    var errors []error
    for err := range errCh {
        errors = append(errors, err)
    }
    
    return errors
}
```

### Практические примеры: Типизированные ошибки для API

```go
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

func HandleAPIError(err error) (int, interface{}) {
    var apiErr *APIError
    if errors.As(err, &apiErr) {
        return apiErr.Code, apiErr
    }
    
    return http.StatusInternalServerError, map[string]string{
        "error": "Internal server error",
    }
}
```

### Практические примеры: Обработка ошибок в транзакциях

```go
func ExecuteTransaction(ctx context.Context, db *sql.DB, fn func(*sql.Tx) error) error {
    tx, err := db.BeginTx(ctx, nil)
    if err != nil {
        return fmt.Errorf("begin transaction: %w", err)
    }
    
    defer func() {
        if p := recover(); p != nil {
            tx.Rollback()
            panic(p)
        } else if err != nil {
            if rollbackErr := tx.Rollback(); rollbackErr != nil {
                err = fmt.Errorf("rollback error: %w, original error: %w", rollbackErr, err)
            }
        } else {
            err = tx.Commit()
        }
    }()
    
    err = fn(tx)
    return err
}
```

## Лучшие практики

1. **Всегда проверяйте ошибки** - не игнорируйте возвращаемые ошибки
2. **Добавляйте контекст** - используйте error wrapping для добавления контекста
3. **Используйте типизированные ошибки** - для проверки конкретных типов ошибок
4. **Документируйте ошибки** - указывайте, какие ошибки может возвращать функция
5. **Обрабатывайте ошибки на нужном уровне** - обрабатывайте ошибки там, где есть контекст для их обработки
6. **Используйте errors.Is и errors.As** - для проверки ошибок в цепочке
7. **Избегайте паники** - используйте возврат ошибок вместо паники
8. **Логируйте ошибки** - добавляйте контекст при логировании
9. **Используйте retry для временных ошибок** - обрабатывайте временные ошибки с повторами
10. **Тестируйте обработку ошибок** - проверяйте все пути обработки ошибок
11. **Обрабатывайте паники** - используйте recover для критических секций
12. **Используйте middleware** - для централизованной обработки ошибок
13. **Создавайте типизированные ошибки** - для разных типов ошибок API
14. **Обрабатывайте ошибки в горутинах** - не позволяйте ошибкам пропадать
15. **Откатывайте транзакции** - при ошибках в транзакциях

## Заключение

Обработка ошибок в Go предоставляет мощные инструменты для создания надежных приложений. Понимание error interface, создания ошибок, error wrapping, проверки ошибок, кастомных ошибок, обработки паник, middleware и практических паттернов критично для эффективной обработки ошибок в Go. Правильная обработка ошибок позволяет создавать надежные, устойчивые приложения, которые корректно обрабатывают все возможные сценарии ошибок и обеспечивают хороший пользовательский опыт.

## Дополнительные ресурсы

- [Go Error Handling](https://go.dev/blog/error-handling-and-go)
- [Go errors Package](https://pkg.go.dev/errors)
- [Go Error Wrapping](https://go.dev/blog/go1.13-errors)

