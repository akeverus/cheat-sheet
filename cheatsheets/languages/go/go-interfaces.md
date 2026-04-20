---
title: "Go: интерфейсы"
description: "Полное руководство по интерфейсам в Go: определение интерфейсов, реализация, type assertions, type switches, empty interface"
tags:
  - go
  - golang
  - interfaces
  - polymorphism
  - type-assertions
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-04-20"
---

# Go: интерфейсы

## Полезные ссылки

- [Go Interfaces](https://go.dev/doc/effective_go#interfaces)
- [Effective Go — Interfaces](https://go.dev/doc/effective_go#interfaces)

## Содержание

- [Введение в интерфейсы](#введение-в-интерфейсы)
  - [Основные концепции](#основные-концепции)
- [Определение интерфейсов](#определение-интерфейсов)
  - [Базовый интерфейс](#базовый-интерфейс)
  - [Интерфейс с несколькими методами](#интерфейс-с-несколькими-методами)
  - [Встраивание интерфейсов](#встраивание-интерфейсов)
- [Реализация интерфейсов](#реализация-интерфейсов)
  - [Реализация интерфейса](#реализация-интерфейса)
  - [Использование интерфейсов](#использование-интерфейсов)
  - [Множественная реализация](#множественная-реализация)
- [Type Assertions](#type-assertions)
  - [Базовое использование](#базовое-использование)
  - [Type Assertion с интерфейсами](#type-assertion-с-интерфейсами)
- [Type Switches](#type-switches)
  - [Базовый type switch](#базовый-type-switch)
  - [Type switch с интерфейсами](#type-switch-с-интерфейсами)
- [Empty Interface](#empty-interface)
  - [Использование empty interface](#использование-empty-interface)
  - [Слайсы с empty interface](#слайсы-с-empty-interface)
  - [Практические примеры: Интерфейсы для работы с данными](#практические-примеры-интерфейсы-для-работы-с-данными)
  - [Практические примеры: Интерфейсы для логирования](#практические-примеры-интерфейсы-для-логирования)
  - [Практические примеры: Интерфейсы для кэширования](#практические-примеры-интерфейсы-для-кэширования)
  - [Практические примеры: Интерфейсы для HTTP клиентов](#практические-примеры-интерфейсы-для-http-клиентов)
  - [Практические примеры: Интерфейсы для валидации](#практические-примеры-интерфейсы-для-валидации)
  - [Практические примеры: Интерфейсы для сериализации](#практические-примеры-интерфейсы-для-сериализации)
  - [Практические примеры: Интерфейсы для работы с файлами](#практические-примеры-интерфейсы-для-работы-с-файлами)
  - [Практические примеры: Интерфейсы для работы с базами данных](#практические-примеры-интерфейсы-для-работы-с-базами-данных)
  - [Практические примеры: Интерфейсы для работы с очередями](#практические-примеры-интерфейсы-для-работы-с-очередями)
  - [Практические примеры: Интерфейсы для работы с конфигурацией](#практические-примеры-интерфейсы-для-работы-с-конфигурацией)
  - [Практические примеры: Интерфейсы для работы с уведомлениями](#практические-примеры-интерфейсы-для-работы-с-уведомлениями)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: Интерфейсы для плагинов](#практические-примеры-интерфейсы-для-плагинов)
  - [Практические примеры: Интерфейсы для middleware](#практические-примеры-интерфейсы-для-middleware)
  - [Практические примеры: Интерфейсы для стратегий](#практические-примеры-интерфейсы-для-стратегий)
  - [Практические примеры: Интерфейсы для валидации](#практические-примеры-интерфейсы-для-валидации-1)
  - [Практические примеры: Интерфейсы для адаптеров](#практические-примеры-интерфейсы-для-адаптеров)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в интерфейсы

Интерфейсы в Go определяют набор методов, которые тип должен реализовать. Интерфейсы обеспечивают полиморфизм и позволяют писать более гибкий код.

### Основные концепции

1. **Неявная реализация** — типы автоматически реализуют интерфейсы
2. **Декларация методов** — интерфейсы определяют только сигнатуры методов
3. **Полиморфизм** — один интерфейс может быть реализован разными типами

## Определение интерфейсов

### Базовый интерфейс

```go
type Writer interface {
    Write([]byte) (int, error)
}

type Reader interface {
    Read([]byte) (int, error)
}

type ReadWriter interface {
    Reader
    Writer
}
```

### Интерфейс с несколькими методами

```go
type Shape interface {
    Area() float64
    Perimeter() float64
}
```

### Встраивание интерфейсов

```go
type ReadWriter interface {
    Reader
    Writer
}

// Эквивалентно
type ReadWriter interface {
    Read([]byte) (int, error)
    Write([]byte) (int, error)
}
```

## Реализация интерфейсов

Типы автоматически реализуют интерфейсы, если они содержат все необходимые методы.

### Реализация интерфейса

```go
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

// Rectangle автоматически реализует интерфейс Shape
```

### Использование интерфейсов

```go
func printArea(s Shape) {
    fmt.Printf("Area: %.2f\n", s.Area())
}

func main() {
    rect := Rectangle{Width: 10, Height: 5}
    printArea(rect)
}
```

### Множественная реализация

```go
type Circle struct {
    Radius float64
}

func (c Circle) Area() float64 {
    return math.Pi * c.Radius * c.Radius
}

func (c Circle) Perimeter() float64 {
    return 2 * math.Pi * c.Radius
}

// И Rectangle, и Circle реализуют интерфейс Shape
func main() {
    shapes := []Shape{
        Rectangle{Width: 10, Height: 5},
        Circle{Radius: 3},
    }

    for _, shape := range shapes {
        fmt.Printf("Area: %.2f\n", shape.Area())
    }
}
```

## Type Assertions

**Type Assertions** позволяют проверять и извлекать конкретные типы из интерфейсов.

### Базовое использование

```go
var i interface{} = "hello"

s := i.(string)
fmt.Println(s)  // "hello"

s, ok := i.(string)
if ok {
    fmt.Println(s)
}
```

### Type Assertion с интерфейсами

```go
func processShape(s Shape) {
    // Проверка конкретного типа
    if rect, ok := s.(Rectangle); ok {
        fmt.Printf("Rectangle: width=%.2f, height=%.2f\n",
            rect.Width, rect.Height)
    } else if circle, ok := s.(Circle); ok {
        fmt.Printf("Circle: radius=%.2f\n", circle.Radius)
    }
}
```

## Type Switches

**Type Switches** позволяют проверять тип значения в интерфейсе.

### Базовый type switch

```go
func processValue(v interface{}) {
    switch val := v.(type) {
    case int:
        fmt.Printf("Integer: %d\n", val)
    case string:
        fmt.Printf("String: %s\n", val)
    case bool:
        fmt.Printf("Boolean: %t\n", val)
    default:
        fmt.Printf("Unknown type: %T\n", val)
    }
}
```

### Type switch с интерфейсами

```go
func processShape(s Shape) {
    switch shape := s.(type) {
    case Rectangle:
        fmt.Printf("Rectangle: width=%.2f, height=%.2f\n",
            shape.Width, shape.Height)
    case Circle:
        fmt.Printf("Circle: radius=%.2f\n", shape.Radius)
    default:
        fmt.Println("Unknown shape")
    }
}
```

## Empty Interface

**Empty Interface** (`interface{}`) может содержать значения любого типа.

### Использование empty interface

```go
func printValue(v interface{}) {
    fmt.Println(v)
}

func main() {
    printValue(42)        // int
    printValue("hello")   // string
    printValue(3.14)      // float64
}
```

### Слайсы с empty interface

```go
var values []interface{} = []interface{}{
    42,
    "hello",
    3.14,
    true,
}

for _, v := range values {
    fmt.Printf("Type: %T, Value: %v\n", v, v)
}
```

### Практические примеры: Интерфейсы для работы с данными

```go
type Repository interface {
    FindByID(id int) (interface{}, error)
    Save(entity interface{}) error
    Delete(id int) error
}

type UserRepository struct {
    db *sql.DB
}

func (r *UserRepository) FindByID(id int) (interface{}, error) {
    var user User
    err := r.db.QueryRow("SELECT * FROM users WHERE id = $1", id).Scan(&user.ID, &user.Name)
    return user, err
}

func (r *UserRepository) Save(entity interface{}) error {
    user := entity.(User)
    _, err := r.db.Exec("INSERT INTO users (name) VALUES ($1)", user.Name)
    return err
}

func (r *UserRepository) Delete(id int) error {
    _, err := r.db.Exec("DELETE FROM users WHERE id = $1", id)
    return err
}
```

### Практические примеры: Интерфейсы для логирования

```go
type Logger interface {
    Debug(message string, args ...interface{})
    Info(message string, args ...interface{})
    Warn(message string, args ...interface{})
    Error(message string, args ...interface{})
}

type ConsoleLogger struct{}

func (l *ConsoleLogger) Debug(message string, args ...interface{}) {
    fmt.Printf("[DEBUG] "+message+"\n", args...)
}

func (l *ConsoleLogger) Info(message string, args ...interface{}) {
    fmt.Printf("[INFO] "+message+"\n", args...)
}

func (l *ConsoleLogger) Warn(message string, args ...interface{}) {
    fmt.Printf("[WARN] "+message+"\n", args...)
}

func (l *ConsoleLogger) Error(message string, args ...interface{}) {
    fmt.Printf("[ERROR] "+message+"\n", args...)
}

type FileLogger struct {
    file *os.File
}

func (l *FileLogger) Debug(message string, args ...interface{}) {
    fmt.Fprintf(l.file, "[DEBUG] "+message+"\n", args...)
}

func (l *FileLogger) Info(message string, args ...interface{}) {
    fmt.Fprintf(l.file, "[INFO] "+message+"\n", args...)
}

func (l *FileLogger) Warn(message string, args ...interface{}) {
    fmt.Fprintf(l.file, "[WARN] "+message+"\n", args...)
}

func (l *FileLogger) Error(message string, args ...interface{}) {
    fmt.Fprintf(l.file, "[ERROR] "+message+"\n", args...)
}
```

### Практические примеры: Интерфейсы для кэширования

```go
type Cache interface {
    Get(key string) (interface{}, bool)
    Set(key string, value interface{}, ttl time.Duration) error
    Delete(key string) error
    Clear() error
}

type MemoryCache struct {
    data  map[string]interface{}
    times map[string]time.Time
    mu    sync.RWMutex
}

func NewMemoryCache() *MemoryCache {
    return &MemoryCache{
        data:  make(map[string]interface{}),
        times: make(map[string]time.Time),
    }
}

func (c *MemoryCache) Get(key string) (interface{}, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()

    value, exists := c.data[key]
    if !exists {
        return nil, false
    }

    if time.Since(c.times[key]) > 0 {
        delete(c.data, key)
        delete(c.times, key)
        return nil, false
    }

    return value, true
}

func (c *MemoryCache) Set(key string, value interface{}, ttl time.Duration) error {
    c.mu.Lock()
    defer c.mu.Unlock()

    c.data[key] = value
    c.times[key] = time.Now().Add(ttl)
    return nil
}

func (c *MemoryCache) Delete(key string) error {
    c.mu.Lock()
    defer c.mu.Unlock()

    delete(c.data, key)
    delete(c.times, key)
    return nil
}

func (c *MemoryCache) Clear() error {
    c.mu.Lock()
    defer c.mu.Unlock()

    c.data = make(map[string]interface{})
    c.times = make(map[string]time.Time)
    return nil
}
```

### Практические примеры: Интерфейсы для HTTP клиентов

```go
type HTTPClient interface {
    Get(url string) (*http.Response, error)
    Post(url string, body []byte) (*http.Response, error)
    Put(url string, body []byte) (*http.Response, error)
    Delete(url string) (*http.Response, error)
}

type StandardHTTPClient struct {
    client *http.Client
}

func NewStandardHTTPClient() *StandardHTTPClient {
    return &StandardHTTPClient{
        client: &http.Client{
            Timeout: 10 * time.Second,
        },
    }
}

func (c *StandardHTTPClient) Get(url string) (*http.Response, error) {
    return c.client.Get(url)
}

func (c *StandardHTTPClient) Post(url string, body []byte) (*http.Response, error) {
    return c.client.Post(url, "application/json", bytes.NewReader(body))
}

func (c *StandardHTTPClient) Put(url string, body []byte) (*http.Response, error) {
    req, err := http.NewRequest("PUT", url, bytes.NewReader(body))
    if err != nil {
        return nil, err
    }
    req.Header.Set("Content-Type", "application/json")
    return c.client.Do(req)
}

func (c *StandardHTTPClient) Delete(url string) (*http.Response, error) {
    req, err := http.NewRequest("DELETE", url, nil)
    if err != nil {
        return nil, err
    }
    return c.client.Do(req)
}
```

### Практические примеры: Интерфейсы для валидации

```go
type Validator interface {
    Validate(value interface{}) error
}

type StringValidator struct {
    MinLength int
    MaxLength int
}

func (v *StringValidator) Validate(value interface{}) error {
    str, ok := value.(string)
    if !ok {
        return fmt.Errorf("value is not a string")
    }

    if len(str) < v.MinLength {
        return fmt.Errorf("string is too short (min: %d)", v.MinLength)
    }

    if len(str) > v.MaxLength {
        return fmt.Errorf("string is too long (max: %d)", v.MaxLength)
    }

    return nil
}

type NumberValidator struct {
    Min float64
    Max float64
}

func (v *NumberValidator) Validate(value interface{}) error {
    num, ok := value.(float64)
    if !ok {
        return fmt.Errorf("value is not a number")
    }

    if num < v.Min {
        return fmt.Errorf("number is too small (min: %f)", v.Min)
    }

    if num > v.Max {
        return fmt.Errorf("number is too large (max: %f)", v.Max)
    }

    return nil
}
```

### Практические примеры: Интерфейсы для сериализации

```go
type Serializer interface {
    Serialize(value interface{}) ([]byte, error)
    Deserialize(data []byte, target interface{}) error
}

type JSONSerializer struct{}

func (s *JSONSerializer) Serialize(value interface{}) ([]byte, error) {
    return json.Marshal(value)
}

func (s *JSONSerializer) Deserialize(data []byte, target interface{}) error {
    return json.Unmarshal(data, target)
}

type XMLSerializer struct{}

func (s *XMLSerializer) Serialize(value interface{}) ([]byte, error) {
    return xml.Marshal(value)
}

func (s *XMLSerializer) Deserialize(data []byte, target interface{}) error {
    return xml.Unmarshal(data, target)
}
```

### Практические примеры: Интерфейсы для работы с файлами

```go
type FileStorage interface {
    Read(filename string) ([]byte, error)
    Write(filename string, data []byte) error
    Delete(filename string) error
    Exists(filename string) bool
}

type LocalFileStorage struct {
    basePath string
}

func NewLocalFileStorage(basePath string) *LocalFileStorage {
    return &LocalFileStorage{basePath: basePath}
}

func (s *LocalFileStorage) Read(filename string) ([]byte, error) {
    path := filepath.Join(s.basePath, filename)
    return os.ReadFile(path)
}

func (s *LocalFileStorage) Write(filename string, data []byte) error {
    path := filepath.Join(s.basePath, filename)
    return os.WriteFile(path, data, 0644)
}

func (s *LocalFileStorage) Delete(filename string) error {
    path := filepath.Join(s.basePath, filename)
    return os.Remove(path)
}

func (s *LocalFileStorage) Exists(filename string) bool {
    path := filepath.Join(s.basePath, filename)
    _, err := os.Stat(path)
    return err == nil
}
```

### Практические примеры: Интерфейсы для работы с базами данных

```go
type Database interface {
    Query(query string, args ...interface{}) (Rows, error)
    Exec(query string, args ...interface{}) (Result, error)
    Begin() (Transaction, error)
    Close() error
}

type Rows interface {
    Next() bool
    Scan(dest ...interface{}) error
    Close() error
}

type Result interface {
    LastInsertId() (int64, error)
    RowsAffected() (int64, error)
}

type Transaction interface {
    Commit() error
    Rollback() error
    Query(query string, args ...interface{}) (Rows, error)
    Exec(query string, args ...interface{}) (Result, error)
}
```

### Практические примеры: Интерфейсы для работы с очередями

```go
type Queue interface {
    Enqueue(item interface{}) error
    Dequeue() (interface{}, error)
    Size() int
    IsEmpty() bool
}

type InMemoryQueue struct {
    items []interface{}
    mu    sync.Mutex
}

func NewInMemoryQueue() *InMemoryQueue {
    return &InMemoryQueue{items: make([]interface{}, 0)}
}

func (q *InMemoryQueue) Enqueue(item interface{}) error {
    q.mu.Lock()
    defer q.mu.Unlock()

    q.items = append(q.items, item)
    return nil
}

func (q *InMemoryQueue) Dequeue() (interface{}, error) {
    q.mu.Lock()
    defer q.mu.Unlock()

    if len(q.items) == 0 {
        return nil, fmt.Errorf("queue is empty")
    }

    item := q.items[0]
    q.items = q.items[1:]
    return item, nil
}

func (q *InMemoryQueue) Size() int {
    q.mu.Lock()
    defer q.mu.Unlock()

    return len(q.items)
}

func (q *InMemoryQueue) IsEmpty() bool {
    return q.Size() == 0
}
```

### Практические примеры: Интерфейсы для работы с конфигурацией

```go
type Config interface {
    GetString(key string) (string, error)
    GetInt(key string) (int, error)
    GetBool(key string) (bool, error)
    GetFloat(key string) (float64, error)
    Set(key string, value interface{}) error
}

type MapConfig struct {
    data map[string]interface{}
    mu   sync.RWMutex
}

func NewMapConfig() *MapConfig {
    return &MapConfig{data: make(map[string]interface{})}
}

func (c *MapConfig) GetString(key string) (string, error) {
    c.mu.RLock()
    defer c.mu.RUnlock()

    value, exists := c.data[key]
    if !exists {
        return "", fmt.Errorf("key %s not found", key)
    }

    str, ok := value.(string)
    if !ok {
        return "", fmt.Errorf("value for key %s is not a string", key)
    }

    return str, nil
}

func (c *MapConfig) GetInt(key string) (int, error) {
    c.mu.RLock()
    defer c.mu.RUnlock()

    value, exists := c.data[key]
    if !exists {
        return 0, fmt.Errorf("key %s not found", key)
    }

    num, ok := value.(int)
    if !ok {
        return 0, fmt.Errorf("value for key %s is not an int", key)
    }

    return num, nil
}

func (c *MapConfig) GetBool(key string) (bool, error) {
    c.mu.RLock()
    defer c.mu.RUnlock()

    value, exists := c.data[key]
    if !exists {
        return false, fmt.Errorf("key %s not found", key)
    }

    b, ok := value.(bool)
    if !ok {
        return false, fmt.Errorf("value for key %s is not a bool", key)
    }

    return b, nil
}

func (c *MapConfig) GetFloat(key string) (float64, error) {
    c.mu.RLock()
    defer c.mu.RUnlock()

    value, exists := c.data[key]
    if !exists {
        return 0, fmt.Errorf("key %s not found", key)
    }

    f, ok := value.(float64)
    if !ok {
        return 0, fmt.Errorf("value for key %s is not a float64", key)
    }

    return f, nil
}

func (c *MapConfig) Set(key string, value interface{}) error {
    c.mu.Lock()
    defer c.mu.Unlock()

    c.data[key] = value
    return nil
}
```

### Практические примеры: Интерфейсы для работы с уведомлениями

```go
type Notifier interface {
    Notify(message string) error
}

type EmailNotifier struct {
    smtpServer string
    from       string
}

func (n *EmailNotifier) Notify(message string) error {
    // Реализация отправки email
    return nil
}

type SMSNotifier struct {
    apiKey string
}

func (n *SMSNotifier) Notify(message string) error {
    // Реализация отправки SMS
    return nil
}

type SlackNotifier struct {
    webhookURL string
}

func (n *SlackNotifier) Notify(message string) error {
    // Реализация отправки в Slack
    return nil
}

// Композитный notifier
type CompositeNotifier struct {
    notifiers []Notifier
}

func NewCompositeNotifier(notifiers ...Notifier) *CompositeNotifier {
    return &CompositeNotifier{notifiers: notifiers}
}

func (n *CompositeNotifier) Notify(message string) error {
    for _, notifier := range n.notifiers {
        if err := notifier.Notify(message); err != nil {
            return err
        }
    }
    return nil
}
```

## Лучшие практики

1. **Используйте маленькие интерфейсы** — предпочитайте интерфейсы с небольшим количеством методов
2. **Принимайте интерфейсы, возвращайте структуры** — принимайте интерфейсы в функциях, возвращайте конкретные типы
3. **Избегайте empty interface** — используйте типизированные интерфейсы когда возможно
4. **Документируйте интерфейсы** — объясняйте назначение интерфейсов
5. **Используйте встраивание** — комбинируйте интерфейсы через встраивание
6. **Используйте интерфейсы для тестирования** — упрощайте мокирование зависимостей
7. **Избегайте интерфейсов для внутренних типов** — используйте интерфейсы для публичного **API**
8. **Используйте интерфейсы для абстракции** — скрывайте детали реализации
9. **Тестируйте реализацию интерфейсов** — проверяйте соответствие интерфейсам
10. **Используйте интерфейсы для расширяемости** — позволяйте добавлять новые реализации

### Практические примеры: Интерфейсы для плагинов

```go
type Plugin interface {
    Name() string
    Execute(ctx context.Context, input interface{}) (interface{}, error)
    Shutdown() error
}

type PluginRegistry struct {
    plugins map[string]Plugin
    mu      sync.RWMutex
}

func NewPluginRegistry() *PluginRegistry {
    return &PluginRegistry{
        plugins: make(map[string]Plugin),
    }
}

func (r *PluginRegistry) Register(plugin Plugin) {
    r.mu.Lock()
    defer r.mu.Unlock()
    r.plugins[plugin.Name()] = plugin
}

func (r *PluginRegistry) Get(name string) (Plugin, bool) {
    r.mu.RLock()
    defer r.mu.RUnlock()
    plugin, ok := r.plugins[name]
    return plugin, ok
}

func (r *PluginRegistry) ExecuteAll(ctx context.Context, input interface{}) error {
    r.mu.RLock()
    plugins := make([]Plugin, 0, len(r.plugins))
    for _, plugin := range r.plugins {
        plugins = append(plugins, plugin)
    }
    r.mu.RUnlock()

    for _, plugin := range plugins {
        if _, err := plugin.Execute(ctx, input); err != nil {
            return fmt.Errorf("plugin %s failed: %w", plugin.Name(), err)
        }
    }

    return nil
}
```

### Практические примеры: Интерфейсы для middleware

```go
type Middleware interface {
    Handle(next http.Handler) http.Handler
}

type AuthMiddleware struct {
    token string
}

func (m *AuthMiddleware) Handle(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        if r.Header.Get("Authorization") != "Bearer "+m.token {
            http.Error(w, "Unauthorized", http.StatusUnauthorized)
            return
        }
        next.ServeHTTP(w, r)
    })
}

type LoggingMiddleware struct {
    logger *log.Logger
}

func (m *LoggingMiddleware) Handle(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        next.ServeHTTP(w, r)
        m.logger.Printf("%s %s took %v", r.Method, r.URL.Path, time.Since(start))
    })
}

func ApplyMiddleware(h http.Handler, middlewares ...Middleware) http.Handler {
    for i := len(middlewares) - 1; i >= 0; i-- {
        h = middlewares[i].Handle(h)
    }
    return h
}
```

### Практические примеры: Интерфейсы для стратегий

```go
type PaymentStrategy interface {
    Pay(amount float64) error
}

type CreditCardStrategy struct {
    cardNumber string
}

func (s *CreditCardStrategy) Pay(amount float64) error {
    // Реализация оплаты кредитной картой
    return nil
}

type PayPalStrategy struct {
    email string
}

func (s *PayPalStrategy) Pay(amount float64) error {
    // Реализация оплаты через PayPal
    return nil
}

type PaymentProcessor struct {
    strategy PaymentStrategy
}

func (p *PaymentProcessor) SetStrategy(strategy PaymentStrategy) {
    p.strategy = strategy
}

func (p *PaymentProcessor) ProcessPayment(amount float64) error {
    return p.strategy.Pay(amount)
}
```

### Практические примеры: Интерфейсы для валидации

```go
type Validator interface {
    Validate(value interface{}) error
}

type StringValidator struct {
    MinLength int
    MaxLength int
}

func (v *StringValidator) Validate(value interface{}) error {
    str, ok := value.(string)
    if !ok {
        return fmt.Errorf("expected string, got %T", value)
    }

    if len(str) < v.MinLength {
        return fmt.Errorf("string too short: minimum %d characters", v.MinLength)
    }

    if len(str) > v.MaxLength {
        return fmt.Errorf("string too long: maximum %d characters", v.MaxLength)
    }

    return nil
}

type NumberValidator struct {
    Min float64
    Max float64
}

func (v *NumberValidator) Validate(value interface{}) error {
    num, ok := value.(float64)
    if !ok {
        return fmt.Errorf("expected number, got %T", value)
    }

    if num < v.Min {
        return fmt.Errorf("number too small: minimum %f", v.Min)
    }

    if num > v.Max {
        return fmt.Errorf("number too large: maximum %f", v.Max)
    }

    return nil
}
```

### Практические примеры: Интерфейсы для адаптеров

```go
type DataStore interface {
    Save(key string, value interface{}) error
    Get(key string) (interface{}, error)
}

type RedisAdapter struct {
    client *redis.Client
}

func (a *RedisAdapter) Save(key string, value interface{}) error {
    data, err := json.Marshal(value)
    if err != nil {
        return err
    }
    return a.client.Set(context.Background(), key, data, 0).Err()
}

func (a *RedisAdapter) Get(key string) (interface{}, error) {
    data, err := a.client.Get(context.Background(), key).Bytes()
    if err != nil {
        return nil, err
    }

    var value interface{}
    if err := json.Unmarshal(data, &value); err != nil {
        return nil, err
    }

    return value, nil
}

type MemoryAdapter struct {
    data map[string]interface{}
    mu   sync.RWMutex
}

func (a *MemoryAdapter) Save(key string, value interface{}) error {
    a.mu.Lock()
    defer a.mu.Unlock()
    a.data[key] = value
    return nil
}

func (a *MemoryAdapter) Get(key string) (interface{}, error) {
    a.mu.RLock()
    defer a.mu.RUnlock()
    value, ok := a.data[key]
    if !ok {
        return nil, fmt.Errorf("key not found: %s", key)
    }
    return value, nil
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Интерфейсы в Go предоставляют мощные инструменты для создания гибкого и полиморфного кода. Понимание определения интерфейсов, их реализации, **type assertions**, **type switches**, **empty interface**, паттернов использования и практических применений критично для эффективного использования интерфейсов в Go. Правильное использование интерфейсов позволяет создавать расширяемый, тестируемый, модульный код, который легко поддерживать, модифицировать и адаптировать к различным требованиям.

## Дополнительные ресурсы

- [Go Interfaces](https://go.dev/doc/effective_go#interfaces)
- [Effective Go — Interfaces](https://go.dev/doc/effective_go#interfaces)

## См. также

- [Go: продвинутые паттерны](go-advanced-patterns.md)
- [Go: основы](go-basics.md)
- [Go: бенчмаркинг](go-benchmarking.md)
- [Go: лучшие практики](go-best-practices.md)
- [Go: сборка и развертывание](go-build.md)
