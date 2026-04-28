---
title: "Go: лучшие практики"
description: "Полное руководство по лучшим практикам в Go: идиоматический код, стиль, производительность, безопасность"
tags:
  - go
  - golang
  - best-practices
  - idiomatic
  - style
type: "overview"
difficulty: "intermediate"
aliases:
  - "Go"
  - "лучшие практики"
  - "Go: лучшие практики"
  - "go best practices"
prerequisites:
  - "[[go-basics]]"
next: []
updated: "2026-04-20"
---

# Go: лучшие практики

## Полезные ссылки

- [Effective Go](https://go.dev/doc/effective_go)
- [Go Code Review Comments](https://go.dev/wiki/CodeReviewComments)

## Содержание

- [Введение в лучшие практики](#введение-в-лучшие-практики)
  - [Основные принципы](#основные-принципы)
- [Идиоматический Go](#идиоматический-go)
  - [Именование](#именование)
  - [Возврат ошибок](#возврат-ошибок)
  - [Использование интерфейсов](#использование-интерфейсов)
- [Стиль кода](#стиль-кода)
  - [Форматирование](#форматирование)
  - [Комментарии](#комментарии)
  - [Группировка](#группировка)
- [Обработка ошибок](#обработка-ошибок)
  - [Всегда проверяйте ошибки](#всегда-проверяйте-ошибки)
  - [Добавляйте контекст к ошибкам](#добавляйте-контекст-к-ошибкам)
  - [Используйте именованные возвращаемые значения осторожно](#используйте-именованные-возвращаемые-значения-осторожно)
- [Производительность](#производительность)
  - [Предварительное выделение памяти](#предварительное-выделение-памяти)
  - [Использование strings.Builder](#использование-stringsbuilder)
  - [Избегайте ненужных аллокаций](#избегайте-ненужных-аллокаций)
- [Безопасность](#безопасность)
  - [Валидация входных данных](#валидация-входных-данных)
  - [Избегайте SQL инъекций](#избегайте-sql-инъекций)
  - [Безопасная работа с паролями](#безопасная-работа-с-паролями)
  - [Практические примеры: Организация кода](#практические-примеры-организация-кода)
  - [Практические примеры: Dependency Injection](#практические-примеры-dependency-injection)
  - [Практические примеры: Использование defer](#практические-примеры-использование-defer)
  - [Практические примеры: Идиоматическая работа с каналами](#практические-примеры-идиоматическая-работа-с-каналами)
  - [Практические примеры: Работа с контекстом](#практические-примеры-работа-с-контекстом)
  - [Практические примеры: Валидация входных данных](#практические-примеры-валидация-входных-данных)
  - [Практические примеры: Безопасная работа с паролями](#практические-примеры-безопасная-работа-с-паролями)
  - [Практические примеры: Защита от SQL инъекций](#практические-примеры-защита-от-sql-инъекций)
  - [Практические примеры: Защита от XSS](#практические-примеры-защита-от-xss)
  - [Практические примеры: Защита от CSRF](#практические-примеры-защита-от-csrf)
  - [Практические примеры: Rate limiting](#практические-примеры-rate-limiting)
  - [Практические примеры: Логирование для безопасности](#практические-примеры-логирование-для-безопасности)
  - [Практические примеры: Обработка секретов](#практические-примеры-обработка-секретов)
  - [Практические примеры: Тестирование](#практические-примеры-тестирование)
  - [Практические примеры: Документирование кода](#практические-примеры-документирование-кода)
  - [Практические примеры: Использование интерфейсов](#практические-примеры-использование-интерфейсов)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: Организация кода и пакетов](#практические-примеры-организация-кода-и-пакетов)
  - [Практические примеры: Dependency Injection](#практические-примеры-dependency-injection-1)
  - [Практические примеры: Конфигурация](#практические-примеры-конфигурация)
  - [Практические примеры: Валидация входных данных](#практические-примеры-валидация-входных-данных-1)
  - [Практические примеры: Обработка ошибок](#практические-примеры-обработка-ошибок)
  - [Практические примеры: Логирование](#практические-примеры-логирование)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в лучшие практики

Лучшие практики Go помогают писать читаемый, поддерживаемый и эффективный код. Следование идиомам Go делает код более понятным для других разработчиков.

### Основные принципы

1. **Простота** — предпочитайте простое решение сложному
2. **Читаемость** — код должен быть понятным
3. **Производительность** — но не в ущерб читаемости
4. **Безопасность** — правильная обработка ошибок и граничных случаев

## Идиоматический Go

### Именование

```go
// Плохо
func GetUserData(id int) UserData {
    // ...
}

// Хорошо
func User(id int) User {
    // ...
}
```

### Возврат ошибок

```go
// Плохо
func Process(data []byte) ([]byte, bool) {
    // ...
}

// Хорошо
func Process(data []byte) ([]byte, error) {
    // ...
}
```

### Использование интерфейсов

```go
// Плохо - интерфейс слишком большой
type Processor interface {
    Process()
    Validate()
    Transform()
    Save()
}

// Хорошо - маленькие интерфейсы
type Processor interface {
    Process() error
}

type Validator interface {
    Validate() error
}
```

## Стиль кода

### Форматирование

```go
// Используйте gofmt для форматирования
// Всегда используйте табы для отступов
// Максимальная длина строки - 80-100 символов
```

### Комментарии

```go
// Package math provides basic mathematical functions.
package math

// Add returns the sum of two integers.
func Add(a, b int) int {
    return a + b
}
```

### Группировка

```go
import (
    "fmt"
    "log"
    "os"

    "github.com/example/package"
)
```

## Обработка ошибок

### Всегда проверяйте ошибки

```go
// Плохо
data, _ := readFile("file.txt")

// Хорошо
data, err := readFile("file.txt")
if err != nil {
    return err
}
```

### Добавляйте контекст к ошибкам

```go
// Плохо
if err != nil {
    return err
}

// Хорошо
if err != nil {
    return fmt.Errorf("failed to read file %s: %w", filename, err)
}
```

### Используйте именованные возвращаемые значения осторожно

```go
// Плохо - может скрыть ошибки
func Process() (result string, err error) {
    result = "value"
    return  // err будет nil
}

// Хорошо
func Process() (string, error) {
    result := "value"
    return result, nil
}
```

## Производительность

### Предварительное выделение памяти

```go
// Плохо
var slice []int
for i := 0; i < 1000; i++ {
    slice = append(slice, i)
}

// Хорошо
slice := make([]int, 0, 1000)
for i := 0; i < 1000; i++ {
    slice = append(slice, i)
}
```

### Использование strings.Builder

```go
// Плохо
var result string
for _, s := range strings {
    result += s
}

// Хорошо
var builder strings.Builder
for _, s := range strings {
    builder.WriteString(s)
}
result := builder.String()
```

### Избегайте ненужных аллокаций

```go
// Плохо
func Process(user User) {
    // user копируется
}

// Хорошо
func Process(user *User) {
    // передается только указатель
}
```

## Безопасность

### Валидация входных данных

```go
func ProcessUser(user User) error {
    if user.ID <= 0 {
        return errors.New("invalid user ID")
    }
    if user.Email == "" {
        return errors.New("email is required")
    }
    // ...
}
```

### Избегайте SQL инъекций

```go
// Плохо
query := fmt.Sprintf("SELECT * FROM users WHERE id = %d", userID)

// Хорошо
query := "SELECT * FROM users WHERE id = $1"
rows, err := db.Query(query, userID)
```

### Безопасная работа с паролями

```go
import "golang.org/x/crypto/bcrypt"

func HashPassword(password string) (string, error) {
    hash, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
    if err != nil {
        return "", err
    }
    return string(hash), nil
}
```

### Практические примеры: Организация кода

```go
// Плохо - все в одном файле
package main

func main() {
    // 1000 строк кода
}

// Хорошо - разделение на пакеты
package main

import (
    "myapp/api"
    "myapp/database"
    "myapp/service"
)

func main() {
    db := database.New()
    svc := service.New(db)
    api.Start(svc)
}
```

### Практические примеры: Dependency Injection

```go
// Плохо - глобальные зависимости
var db *sql.DB

func init() {
    db = connectDB()
}

func GetUser(id int) (*User, error) {
    return db.QueryRow("SELECT * FROM users WHERE id = $1", id)
}

// Хорошо - dependency injection
type UserRepository struct {
    db *sql.DB
}

func NewUserRepository(db *sql.DB) *UserRepository {
    return &UserRepository{db: db}
}

func (r *UserRepository) GetUser(id int) (*User, error) {
    return r.db.QueryRow("SELECT * FROM users WHERE id = $1", id)
}
```

### Практические примеры: Использование defer

```go
// Всегда используйте defer для очистки
func processFile(filename string) error {
    file, err := os.Open(filename)
    if err != nil {
        return err
    }
    defer file.Close()  // Всегда закрывается

    // Обработка файла
    return process(file)
}

// defer для разблокировки мьютекса
func (s *Service) Process() {
    s.mu.Lock()
    defer s.mu.Unlock()

    // Критическая секция
}

// defer для восстановления после паники
func safeOperation() {
    defer func() {
        if r := recover(); r != nil {
            log.Printf("Recovered from panic: %v", r)
        }
    }()

    // Операция, которая может вызвать панику
}
```

### Практические примеры: Идиоматическая работа с каналами

```go
// Плохо - закрытие канала отправителем
func badPattern() {
    ch := make(chan int)
    go func() {
        for i := 0; i < 10; i++ {
            ch <- i
        }
        close(ch)  // Отправитель закрывает
    }()

    for val := range ch {
        fmt.Println(val)
    }
}

// Хорошо - закрытие канала отправителем (правильно для одного отправителя)
func goodPattern() {
    ch := make(chan int)
    go func() {
        defer close(ch)  // Используйте defer
        for i := 0; i < 10; i++ {
            ch <- i
        }
    }()

    for val := range ch {
        fmt.Println(val)
    }
}
```

### Практические примеры: Работа с контекстом

```go
// Всегда передавайте context первым параметром
func processWithContext(ctx context.Context, data []byte) error {
    // Проверка отмены
    if err := ctx.Err(); err != nil {
        return err
    }

    // Использование context в операциях
    req, _ := http.NewRequestWithContext(ctx, "GET", "http://example.com", nil)
    client.Do(req)

    return nil
}

// Использование context в горутинах
func processConcurrently(ctx context.Context, items []Item) error {
    var wg sync.WaitGroup
    errCh := make(chan error, len(items))

    for _, item := range items {
        wg.Add(1)
        go func(it Item) {
            defer wg.Done()

            if err := processItem(ctx, it); err != nil {
                select {
                case errCh <- err:
                case <-ctx.Done():
                }
            }
        }(item)
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

### Практические примеры: Валидация входных данных

```go
type User struct {
    ID    int
    Name  string
    Email string
    Age   int
}

func (u *User) Validate() error {
    if u.ID <= 0 {
        return fmt.Errorf("invalid user ID: %d", u.ID)
    }

    if u.Name == "" {
        return fmt.Errorf("name is required")
    }

    if len(u.Name) < 2 || len(u.Name) > 50 {
        return fmt.Errorf("name must be between 2 and 50 characters")
    }

    if u.Email == "" {
        return fmt.Errorf("email is required")
    }

    emailRegex := regexp.MustCompile(`^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$`)
    if !emailRegex.MatchString(u.Email) {
        return fmt.Errorf("invalid email format: %s", u.Email)
    }

    if u.Age < 0 || u.Age > 150 {
        return fmt.Errorf("age must be between 0 and 150")
    }

    return nil
}
```

### Практические примеры: Безопасная работа с паролями

```go
import "golang.org/x/crypto/bcrypt"

// Хеширование пароля
func HashPassword(password string) (string, error) {
    hash, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
    if err != nil {
        return "", err
    }
    return string(hash), nil
}

// Проверка пароля
func CheckPassword(password, hash string) bool {
    err := bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
    return err == nil
}

// Использование
func createUser(username, password string) (*User, error) {
    hashedPassword, err := HashPassword(password)
    if err != nil {
        return nil, err
    }

    user := &User{
        Username: username,
        PasswordHash: hashedPassword,
    }

    return user, nil
}
```

### Практические примеры: Защита от SQL инъекций

```go
// Плохо - уязвимо к SQL инъекциям
func badQuery(userID string) {
    query := fmt.Sprintf("SELECT * FROM users WHERE id = %s", userID)
    db.Query(query)
}

// Хорошо - использование параметров
func goodQuery(userID int) (*User, error) {
    query := "SELECT * FROM users WHERE id = $1"
    row := db.QueryRow(query, userID)

    var user User
    err := row.Scan(&user.ID, &user.Name, &user.Email)
    return &user, err
}

// Использование prepared statements
func preparedQuery(userID int) (*User, error) {
    stmt, err := db.Prepare("SELECT * FROM users WHERE id = $1")
    if err != nil {
        return nil, err
    }
    defer stmt.Close()

    row := stmt.QueryRow(userID)
    var user User
    err = row.Scan(&user.ID, &user.Name, &user.Email)
    return &user, err
}
```

### Практические примеры: Защита от XSS

```go
import "html/template"

// Экранирование HTML
func safeHTML(input string) string {
    return template.HTMLEscapeString(input)
}

// Использование в шаблонах
func renderTemplate(w http.ResponseWriter, data map[string]interface{}) {
    tmpl := template.Must(template.New("page").Parse(`
        <h1>{{.Title}}</h1>
        <p>{{.Content}}</p>
    `))

    // Автоматическое экранирование
    tmpl.Execute(w, data)
}
```

### Практические примеры: Защита от CSRF

```go
import "github.com/gorilla/csrf"

func setupCSRF() http.Handler {
    CSRF := csrf.Protect(
        []byte("32-byte-long-auth-key"),
        csrf.Secure(false), // true для HTTPS
    )

    mux := http.NewServeMux()
    mux.HandleFunc("/form", formHandler)

    return CSRF(mux)
}

func formHandler(w http.ResponseWriter, r *http.Request) {
    token := csrf.Token(r)
    // Использование token в форме
    fmt.Fprintf(w, `<form method="POST">
        <input type="hidden" name="%s" value="%s">
        <!-- остальная форма -->
    </form>`, csrf.TemplateField(r), token)
}
```

### Практические примеры: Rate limiting

```go
import "golang.org/x/time/rate"

func rateLimitMiddleware(limiter *rate.Limiter) func(http.Handler) http.Handler {
    return func(next http.Handler) http.Handler {
        return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
            if !limiter.Allow() {
                http.Error(w, "Rate limit exceeded", http.StatusTooManyRequests)
                return
            }
            next.ServeHTTP(w, r)
        })
    }
}

// Использование
func main() {
    limiter := rate.NewLimiter(10, 1) // 10 запросов в секунду

    mux := http.NewServeMux()
    mux.HandleFunc("/api", apiHandler)

    handler := rateLimitMiddleware(limiter)(mux)
    http.ListenAndServe(":8080", handler)
}
```

### Практические примеры: Логирование для безопасности

```go
func logSecurityEvent(event string, details map[string]interface{}) {
    logger.Info("Security event",
        "event", event,
        "timestamp", time.Now().UTC(),
        "details", details,
    )
}

func loginHandler(w http.ResponseWriter, r *http.Request) {
    username := r.FormValue("username")

    // Логирование попытки входа
    logSecurityEvent("login_attempt", map[string]interface{}{
        "username": username,
        "ip":       r.RemoteAddr,
    })

    // Проверка учетных данных
    if !checkCredentials(username, password) {
        logSecurityEvent("login_failed", map[string]interface{}{
            "username": username,
            "ip":       r.RemoteAddr,
        })
        http.Error(w, "Invalid credentials", http.StatusUnauthorized)
        return
    }

    logSecurityEvent("login_success", map[string]interface{}{
        "username": username,
        "ip":       r.RemoteAddr,
    })
}
```

### Практические примеры: Обработка секретов

```go
// Плохо - секреты в коде
const APIKey = "secret-key-12345"

// Хорошо - секреты из переменных окружения
func getAPIKey() (string, error) {
    key := os.Getenv("API_KEY")
    if key == "" {
        return "", fmt.Errorf("API_KEY environment variable is not set")
    }
    return key, nil
}

// Использование secret manager
type SecretManager interface {
    GetSecret(key string) (string, error)
}

func getSecretFromManager(sm SecretManager, key string) (string, error) {
    return sm.GetSecret(key)
}
```

### Практические примеры: Тестирование

```go
// Покрытие тестами критичного функционала
func TestUserValidation(t *testing.T) {
    tests := []struct {
        name    string
        user    User
        wantErr bool
    }{
        {
            name: "valid user",
            user: User{
                ID:    1,
                Name:  "Alice",
                Email: "alice@example.com",
                Age:   30,
            },
            wantErr: false,
        },
        {
            name: "invalid email",
            user: User{
                ID:    1,
                Name:  "Bob",
                Email: "invalid-email",
                Age:   30,
            },
            wantErr: true,
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            err := tt.user.Validate()
            if (err != nil) != tt.wantErr {
                t.Errorf("Validate() error = %v, wantErr %v", err, tt.wantErr)
            }
        })
    }
}
```

### Практические примеры: Документирование кода

```go
// Package user provides functionality for user management.
package user

// User represents a user in the system.
// It contains basic user information and is used throughout the application.
type User struct {
    // ID is the unique identifier for the user.
    ID int

    // Name is the user's full name.
    // It must be between 2 and 50 characters.
    Name string

    // Email is the user's email address.
    // It must be a valid email format.
    Email string
}

// NewUser creates a new user with the given name and email.
// It validates the input and returns an error if validation fails.
//
// Example:
//
//	user, err := NewUser("Alice", "alice@example.com")
//	if err != nil {
//	    log.Fatal(err)
//	}
func NewUser(name, email string) (*User, error) {
    // implementation
}
```

### Практические примеры: Использование интерфейсов

```go
// Маленькие интерфейсы лучше больших
type Reader interface {
    Read([]byte) (int, error)
}

type Writer interface {
    Write([]byte) (int, error)
}

type ReadWriter interface {
    Reader
    Writer
}

// Использование интерфейсов для тестирования
type UserRepository interface {
    GetUser(id int) (*User, error)
    SaveUser(user *User) error
}

type UserService struct {
    repo UserRepository
}

func NewUserService(repo UserRepository) *UserService {
    return &UserService{repo: repo}
}

// Легко мокировать для тестов
type MockUserRepository struct {
    users map[int]*User
}

func (m *MockUserRepository) GetUser(id int) (*User, error) {
    return m.users[id], nil
}

func (m *MockUserRepository) SaveUser(user *User) error {
    m.users[user.ID] = user
    return nil
}
```

## Лучшие практики

1. **Используйте gofmt** — для форматирования кода
2. **Следуйте конвенциям именования** — экспортируемые с большой буквы
3. **Документируйте публичный API** — используйте комментарии
4. **Обрабатывайте ошибки явно** — не игнорируйте ошибки
5. **Используйте defer** — для очистки ресурсов
6. **Тестируйте код** — пишите тесты для критичного функционала
7. **Избегайте глобальных переменных** — используйте **dependency injection**
8. **Используйте интерфейсы** — для абстракции и тестирования
9. **Валидируйте входные данные** — всегда проверяйте данные от клиентов
10. **Используйте context** — для отмены и таймаутов
11. **Защищайте от атак** — **SQL** инъекции, **XSS**, **CSRF**
12. **Используйте rate limiting** — для защиты от злоупотреблений
13. **Логируйте безопасность** — отслеживайте подозрительную активность
14. **Храните секреты безопасно** — используйте **secret managers**
15. **Используйте идиоматический Go** — следуйте конвенциям языка

### Практические примеры: Организация кода и пакетов

```go
// Плохая организация: все в одном файле
package main

// Хорошая организация: разделение по ответственности
// handlers/user_handler.go
package handlers

type UserHandler struct {
    service UserService
}

// services/user_service.go
package services

type UserService interface {
    GetUser(id int) (*User, error)
}

// repositories/user_repository.go
package repositories

type UserRepository interface {
    FindByID(id int) (*User, error)
}

// models/user.go
package models

type User struct {
    ID   int
    Name string
}
```

### Практические примеры: Dependency Injection

```go
// Плохо: жесткая зависимость
type Service struct {
    repo *Repository
}

func NewService() *Service {
    return &Service{
        repo: NewRepository(), // Прямая зависимость
    }
}

// Хорошо: dependency injection
type Service struct {
    repo Repository // Интерфейс
}

func NewService(repo Repository) *Service {
    return &Service{
        repo: repo, // Внедренная зависимость
    }
}
```

### Практические примеры: Конфигурация

```go
// Плохо: глобальные переменные конфигурации
var Config = struct {
    DBHost string
    DBPort int
}{}

// Хорошо: структура конфигурации
type Config struct {
    DB DBConfig
}

type DBConfig struct {
    Host     string
    Port     int
    User     string
    Password string
}

func LoadConfig() (*Config, error) {
    // Загрузка из файла или переменных окружения
    return &Config{
        DB: DBConfig{
            Host: os.Getenv("DB_HOST"),
            Port: 5432,
        },
    }, nil
}
```

### Практические примеры: Валидация входных данных

```go
type Validator interface {
    Validate() error
}

type UserInput struct {
    Email string
    Name  string
    Age   int
}

func (u *UserInput) Validate() error {
    if u.Email == "" {
        return fmt.Errorf("email is required")
    }

    if !strings.Contains(u.Email, "@") {
        return fmt.Errorf("invalid email format")
    }

    if u.Name == "" {
        return fmt.Errorf("name is required")
    }

    if len(u.Name) < 2 {
        return fmt.Errorf("name too short")
    }

    if u.Age < 0 || u.Age > 120 {
        return fmt.Errorf("invalid age")
    }

    return nil
}
```

### Практические примеры: Обработка ошибок

```go
// Плохо: игнорирование ошибок
func processData(data []byte) {
    result, _ := parseData(data) // Игнорирование ошибки
    save(result)
}

// Хорошо: явная обработка ошибок
func processData(data []byte) error {
    result, err := parseData(data)
    if err != nil {
        return fmt.Errorf("parse data: %w", err)
    }

    if err := save(result); err != nil {
        return fmt.Errorf("save: %w", err)
    }

    return nil
}

// Использование errors.Is и errors.As
if errors.Is(err, ErrNotFound) {
    // Обработка специфичной ошибки
}

var validationErr *ValidationError
if errors.As(err, &validationErr) {
    // Обработка типа ошибки
}
```

### Практические примеры: Логирование

```go
import "log/slog"

// Структурированное логирование
logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))

logger.Info("user login",
    "user_id", userID,
    "ip", ipAddress,
    "timestamp", time.Now(),
)

logger.Error("database error",
    "error", err,
    "query", query,
)

// Уровни логирования
logger.Debug("debug message")
logger.Info("info message")
logger.Warn("warning message")
logger.Error("error message")
```


## Решение проблем

При медленной компиляции крупных проектов проверьте кэш сборки и использование `go build -i` для кэширования пакетов. При «undefined: X» в импортах убедитесь, что пакет установлен (`go mod tidy`) и путь импорта корректен. При панических ошибках используйте `recover()` в отложенных вызовах и логируйте stack trace для диагностики.

## Частые вопросы

**Когда использовать указатели?** — Для больших структур и когда нужно изменять значение внутри функции. Для примитивов и мелких структур передача по значению эффективнее.

**Как организовать проект?** — Корневой пакет — `main`, подпакеты по доменной логике. Один пакет — одна директория, имена в lowercase.

## Заключение

Лучшие практики Go помогают создавать качественный, поддерживаемый и эффективный код. Понимание идиоматического Go, стиля кода, обработки ошибок, производительности, безопасности, организации кода, **dependency injection** и практических паттернов критично для написания хорошего кода на Go. Следование лучшим практикам позволяет создавать надежные, безопасные, масштабируемые и легко поддерживаемые приложения.

## Дополнительные ресурсы

- [Effective Go](https://go.dev/doc/effective_go)
- [Go Code Review Comments](https://go.dev/wiki/CodeReviewComments)

## См. также

- [Go: продвинутые паттерны](go-advanced-patterns.md)
- [Go: основы](go-basics.md)
- [Go: бенчмаркинг](go-benchmarking.md)
- [Go: сборка и развертывание](go-build.md)
- [Go: коллекции](go-collections.md)
