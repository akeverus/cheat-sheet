---
title: "Go: тестирование"
description: "Полное руководство по тестированию в Go: unit тесты, table-driven tests, benchmarks, примеры, моки, интеграционные тесты"
tags:
  - go
  - golang
  - testing
  - unit-tests
  - benchmarks
  - mocks
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-04-20"
---

# Go: тестирование

## Полезные ссылки

- [Go Testing Documentation](https://pkg.go.dev/testing)
- [Go Testing Best Practices](https://go.dev/doc/tutorial/add-a-test)
- [Table-Driven Tests](https://go.dev/wiki/TableDrivenTests)

## Содержание

- [Введение в тестирование](#введение-в-тестирование)
  - [Основные концепции](#основные-концепции)
  - [Структура тестовых файлов](#структура-тестовых-файлов)
- [Unit тесты](#unit-тесты)
  - [Базовый тест](#базовый-тест)
  - [Запуск тестов](#запуск-тестов)
  - [Использование t.Helper()](#использование-thelper)
  - [Подтесты (Subtests)](#подтесты-subtests)
- [Table-Driven Tests](#table-driven-tests)
  - [Базовый пример](#базовый-пример)
  - [Тестирование с ошибками](#тестирование-с-ошибками)
- [Benchmarks](#benchmarks)
  - [Базовый benchmark](#базовый-benchmark)
  - [Запуск benchmarks](#запуск-benchmarks)
  - [Сравнение производительности](#сравнение-производительности)
  - [Benchmark с подготовкой данных](#benchmark-с-подготовкой-данных)
- [Примеры (Examples)](#примеры-examples)
  - [Базовый пример](#базовый-пример-1)
  - [Пример с функцией](#пример-с-функцией)
  - [Пример с типом](#пример-с-типом)
- [Покрытие кода](#покрытие-кода)
  - [Измерение покрытия](#измерение-покрытия)
  - [Целевое покрытие](#целевое-покрытие)
- [Моки и стабы](#моки-и-стабы)
  - [Использование интерфейсов](#использование-интерфейсов)
  - [Использование библиотек для моков](#использование-библиотек-для-моков)
- [Интеграционные тесты](#интеграционные-тесты)
  - [Тестирование с базой данных](#тестирование-с-базой-данных)
  - [Тестирование HTTP handlers](#тестирование-http-handlers)
  - [Property-Based Testing](#property-based-testing)
  - [Тестирование с использованием testify](#тестирование-с-использованием-testify)
  - [Тестирование с использованием gomock](#тестирование-с-использованием-gomock)
  - [Тестирование конкурентного кода](#тестирование-конкурентного-кода)
  - [Тестирование с использованием httptest](#тестирование-с-использованием-httptest)
  - [Тестирование с использованием testcontainers](#тестирование-с-использованием-testcontainers)
  - [Тестирование с использованием golden files](#тестирование-с-использованием-golden-files)
  - [Тестирование с использованием table-driven tests для ошибок](#тестирование-с-использованием-table-driven-tests-для-ошибок)
  - [Тестирование с использованием параллельных тестов](#тестирование-с-использованием-параллельных-тестов)
  - [Тестирование с использованием cleanup](#тестирование-с-использованием-cleanup)
  - [Тестирование с использованием skip](#тестирование-с-использованием-skip)
  - [Тестирование с использованием fatal](#тестирование-с-использованием-fatal)
  - [Практические примеры: тестирование сервисов](#практические-примеры-тестирование-сервисов)
  - [Практические примеры: тестирование middleware](#практические-примеры-тестирование-middleware)
  - [Практические примеры: тестирование с временем](#практические-примеры-тестирование-с-временем)
  - [Практические примеры: Тестирование HTTP handlers](#практические-примеры-тестирование-http-handlers)
  - [Практические примеры: Тестирование конкурентного кода](#практические-примеры-тестирование-конкурентного-кода)
  - [Практические примеры: Тестирование с cleanup](#практические-примеры-тестирование-с-cleanup)
  - [Практические примеры: Тестирование с проверкой ошибок](#практические-примеры-тестирование-с-проверкой-ошибок)
  - [Практические примеры: Интеграционные тесты с testcontainers](#практические-примеры-интеграционные-тесты-с-testcontainers)
  - [Практические примеры: Property-based тестирование](#практические-примеры-property-based-тестирование)
  - [Практические примеры: Моки интерфейсов](#практические-примеры-моки-интерфейсов)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: Тестирование с использованием testify](#практические-примеры-тестирование-с-использованием-testify)
  - [Практические примеры: Golden файлы для тестирования](#практические-примеры-golden-файлы-для-тестирования)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в тестирование

Go предоставляет встроенную поддержку тестирования через пакет `testing`. Тестирование в Go следует простым правилам и конвенциям, что делает написание тестов простым и эффективным.

### Основные концепции

1. **Unit тесты** — тестирование отдельных функций и методов
2. **Table-`Driven` Tests** — тестирование с использованием таблиц данных
3. **Benchmarks** — измерение производительности кода
4. **Examples** — примеры использования с проверкой вывода

### Структура тестовых файлов

**Тестовые файлы в Go должны:**
- Иметь суффикс `_test.go`
- Находиться в том же пакете, что и тестируемый код
- Содержать функции с префиксом `Test`, `Benchmark` или `Example`

## Unit тесты

**Unit** тесты проверяют корректность работы отдельных функций и методов.

### Базовый тест

```go
// math.go
package math

func Add(a, b int) int {
    return a + b
}

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
```

### Запуск тестов

```bash
# Запуск всех тестов в пакете
go test

# Запуск конкретного теста
go test -run TestAdd

# Запуск с подробным выводом
go test -v

# Запуск тестов во всех пакетах
go test ./...
```

### Использование t.Helper()

```go
func TestAdd(t *testing.T) {
    t.Helper()  // Помечает функцию как вспомогательную

    result := Add(2, 3)
    if result != 5 {
        t.Errorf("Expected 5, got %d", result)
    }
}
```

### Подтесты (Subtests)

```go
func TestAdd(t *testing.T) {
    tests := []struct {
        name     string
        a        int
        b        int
        expected int
    }{
        {"positive numbers", 2, 3, 5},
        {"negative numbers", -2, -3, -5},
        {"zero", 0, 0, 0},
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            result := Add(tt.a, tt.b)
            if result != tt.expected {
                t.Errorf("Add(%d, %d) = %d; expected %d",
                    tt.a, tt.b, result, tt.expected)
            }
        })
    }
}
```

## Table-Driven Tests

**Table-Driven Tests** — это популярный паттерн в Go для тестирования множественных сценариев.

### Базовый пример

```go
func TestMultiply(t *testing.T) {
    tests := []struct {
        name     string
        a        int
        b        int
        expected int
    }{
        {
            name:     "positive numbers",
            a:        2,
            b:        3,
            expected: 6,
        },
        {
            name:     "negative numbers",
            a:        -2,
            b:        -3,
            expected: 6,
        },
        {
            name:     "zero",
            a:        0,
            b:        5,
            expected: 0,
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            result := Multiply(tt.a, tt.b)
            if result != tt.expected {
                t.Errorf("Multiply(%d, %d) = %d; expected %d",
                    tt.a, tt.b, result, tt.expected)
            }
        })
    }
}
```

### Тестирование с ошибками

```go
func TestDivide(t *testing.T) {
    tests := []struct {
        name      string
        a         int
        b         int
        expected  int
        expectErr bool
    }{
        {
            name:      "normal division",
            a:         10,
            b:         2,
            expected:  5,
            expectErr: false,
        },
        {
            name:      "division by zero",
            a:         10,
            b:         0,
            expected:  0,
            expectErr: true,
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            result, err := Divide(tt.a, tt.b)

            if tt.expectErr {
                if err == nil {
                    t.Error("Expected error, got nil")
                }
                return
            }

            if err != nil {
                t.Errorf("Unexpected error: %v", err)
                return
            }

            if result != tt.expected {
                t.Errorf("Divide(%d, %d) = %d; expected %d",
                    tt.a, tt.b, result, tt.expected)
            }
        })
    }
}
```

## Benchmarks

**Benchmarks** позволяют измерять производительность кода.

### Базовый benchmark

```go
func BenchmarkAdd(b *testing.B) {
    for i := 0; i < b.N; i++ {
        Add(2, 3)
    }
}
```

### Запуск benchmarks

```bash
# Запуск всех benchmarks
go test -bench=.

# Запуск конкретного benchmark
go test -bench=BenchmarkAdd

# Запуск с профилированием памяти
go test -bench=. -benchmem

# Запуск с указанием времени
go test -bench=. -benchtime=10s
```

### Сравнение производительности

```go
func BenchmarkAdd(b *testing.B) {
    b.Run("small numbers", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            Add(1, 2)
        }
    })

    b.Run("large numbers", func(b *testing.B) {
        for i := 0; i < b.N; i++ {
            Add(1000000, 2000000)
        }
    })
}
```

### Benchmark с подготовкой данных

```go
func BenchmarkProcess(b *testing.B) {
    data := make([]int, 1000)
    for i := range data {
        data[i] = i
    }

    b.ResetTimer()  // Сброс таймера после подготовки

    for i := 0; i < b.N; i++ {
        Process(data)
    }
}
```

## Примеры (Examples)

**Examples** — это специальные функции, которые служат как документация и тесты одновременно.

### Базовый пример

```go
func ExampleAdd() {
    result := Add(2, 3)
    fmt.Println(result)
    // Output: 5
}
```

### Пример с функцией

```go
func ExampleMultiply() {
    result := Multiply(4, 5)
    fmt.Println(result)
    // Output: 20
}
```

### Пример с типом

```go
func ExampleCalculator() {
    calc := NewCalculator()
    result := calc.Add(10, 20)
    fmt.Println(result)
    // Output: 30
}
```

## Покрытие кода

Go предоставляет инструменты для измерения покрытия кода тестами.

### Измерение покрытия

```bash
# Генерация отчета о покрытии
go test -cover

# Детальный отчет о покрытии
go test -coverprofile=coverage.out
go tool cover -html=coverage.out

# Покрытие для всех пакетов
go test -cover ./...
```

### Целевое покрытие

```bash
# Проверка минимального покрытия
go test -cover -coverpkg=./... -coverprofile=coverage.out
go tool cover -func=coverage.out | grep total
```

## Моки и стабы

Для создания моков и стабов в Go используются интерфейсы и внедрение зависимостей.

### Использование интерфейсов

```go
// Определение интерфейса
type UserRepository interface {
    GetUser(id int) (*User, error)
}

// Реализация для тестов
type mockUserRepository struct {
    users map[int]*User
}

func (m *mockUserRepository) GetUser(id int) (*User, error) {
    user, ok := m.users[id]
    if !ok {
        return nil, errors.New("user not found")
    }
    return user, nil
}

// Тест с моком
func TestGetUser(t *testing.T) {
    mockRepo := &mockUserRepository{
        users: map[int]*User{
            1: {ID: 1, Name: "Alice"},
        },
    }

    service := NewUserService(mockRepo)
    user, err := service.GetUser(1)

    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }

    if user.Name != "Alice" {
        t.Errorf("Expected Alice, got %s", user.Name)
    }
}
```

### Использование библиотек для моков

```go
// Использование testify/mock
import "github.com/stretchr/testify/mock"

type MockRepository struct {
    mock.Mock
}

func (m *MockRepository) GetUser(id int) (*User, error) {
    args := m.Called(id)
    return args.Get(0).(*User), args.Error(1)
}

func TestGetUser(t *testing.T) {
    mockRepo := new(MockRepository)
    mockRepo.On("GetUser", 1).Return(&User{ID: 1, Name: "Alice"}, nil)

    service := NewUserService(mockRepo)
    user, err := service.GetUser(1)

    assert.NoError(t, err)
    assert.Equal(t, "Alice", user.Name)
    mockRepo.AssertExpectations(t)
}
```

## Интеграционные тесты

Интеграционные тесты проверяют взаимодействие между компонентами системы.

### Тестирование с базой данных

```go
func TestUserService_Integration(t *testing.T) {
    if testing.Short() {
        t.Skip("Skipping integration test")
    }

    db := setupTestDB(t)
    defer db.Close()

    repo := NewUserRepository(db)
    service := NewUserService(repo)

    user, err := service.CreateUser("Alice")
    if err != nil {
        t.Fatalf("Failed to create user: %v", err)
    }

    if user.Name != "Alice" {
        t.Errorf("Expected Alice, got %s", user.Name)
    }
}
```

### Тестирование HTTP handlers

```go
func TestUserHandler(t *testing.T) {
    handler := setupTestHandler(t)

    req := httptest.NewRequest("GET", "/users/1", nil)
    w := httptest.NewRecorder()

    handler.ServeHTTP(w, req)

    if w.Code != http.StatusOK {
        t.Errorf("Expected status 200, got %d", w.Code)
    }

    var user User
    json.Unmarshal(w.Body.Bytes(), &user)

    if user.ID != 1 {
        t.Errorf("Expected user ID 1, got %d", user.ID)
    }
}
```

### Property-Based Testing

**Property-based testing** позволяет тестировать свойства функций вместо конкретных примеров.

```go
import "github.com/leanovate/gopter"

func TestAddCommutative(t *testing.T) {
    properties := gopter.NewProperties(nil)

    properties.Property("addition is commutative", prop.ForAll(
        func(a, b int) bool {
            return Add(a, b) == Add(b, a)
        },
        gen.Int(),
        gen.Int(),
    ))

    properties.TestingRun(t)
}
```

### Тестирование с использованием testify

**Testify** предоставляет дополнительные утилиты для тестирования.

```go
import (
    "github.com/stretchr/testify/assert"
    "github.com/stretchr/testify/require"
)

func TestWithTestify(t *testing.T) {
    result := Add(2, 3)

    assert.Equal(t, 5, result)
    assert.NotNil(t, result)

    require.Equal(t, 5, result)  // Останавливает тест при ошибке
}
```

### Тестирование с использованием gomock

**Gomock** позволяет генерировать моки на основе интерфейсов.

```go
//go:generate mockgen -source=repository.go -destination=mock_repository.go

type MockRepository struct {
    ctrl     *gomock.Controller
    recorder *MockRepositoryMockRecorder
}

func TestWithGomock(t *testing.T) {
    ctrl := gomock.NewController(t)
    defer ctrl.Finish()

    mockRepo := NewMockRepository(ctrl)
    mockRepo.EXPECT().GetUser(1).Return(&User{ID: 1, Name: "Alice"}, nil)

    service := NewUserService(mockRepo)
    user, err := service.GetUser(1)

    assert.NoError(t, err)
    assert.Equal(t, "Alice", user.Name)
}
```

### Тестирование конкурентного кода

```go
func TestConcurrentAccess(t *testing.T) {
    counter := &Counter{}
    var wg sync.WaitGroup
    numGoroutines := 100
    iterations := 1000

    for i := 0; i < numGoroutines; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for j := 0; j < iterations; j++ {
                counter.Increment()
            }
        }()
    }

    wg.Wait()
    expected := numGoroutines * iterations
    if counter.Value() != expected {
        t.Errorf("Expected %d, got %d", expected, counter.Value())
    }
}
```

### Тестирование с использованием httptest

```go
func TestHTTPHandler(t *testing.T) {
    handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.WriteHeader(http.StatusOK)
        w.Write([]byte("OK"))
    })

    req := httptest.NewRequest("GET", "/", nil)
    w := httptest.NewRecorder()

    handler.ServeHTTP(w, req)

    assert.Equal(t, http.StatusOK, w.Code)
    assert.Equal(t, "OK", w.Body.String())
}
```

### Тестирование с использованием testcontainers

**Testcontainers** позволяет использовать реальные контейнеры в тестах.

```go
import "github.com/testcontainers/testcontainers-go"

func TestWithPostgres(t *testing.T) {
    ctx := context.Background()

    postgresContainer, err := postgres.RunContainer(ctx,
        testcontainers.WithImage("postgres:15-alpine"),
        postgres.WithDatabase("testdb"),
        postgres.WithUsername("testuser"),
        postgres.WithPassword("testpass"),
    )
    require.NoError(t, err)
    defer postgresContainer.Terminate(ctx)

    connStr, err := postgresContainer.ConnectionString(ctx)
    require.NoError(t, err)

    db, err := sql.Open("postgres", connStr)
    require.NoError(t, err)
    defer db.Close()

    // Тестирование с реальной БД
}
```

### Тестирование с использованием golden files

**Golden files** позволяют сохранять ожидаемые результаты тестов.

```go
func TestWithGoldenFile(t *testing.T) {
    result := processData("input.txt")

    golden := filepath.Join("testdata", "expected.golden")
    if *update {
        os.WriteFile(golden, []byte(result), 0644)
    }

    expected, err := os.ReadFile(golden)
    require.NoError(t, err)

    assert.Equal(t, string(expected), result)
}
```

### Тестирование с использованием table-driven tests для ошибок

```go
func TestErrorCases(t *testing.T) {
    tests := []struct {
        name      string
        input     string
        wantErr   bool
        errString string
    }{
        {
            name:      "empty string",
            input:     "",
            wantErr:   true,
            errString: "input cannot be empty",
        },
        {
            name:      "invalid format",
            input:     "invalid",
            wantErr:   true,
            errString: "invalid format",
        },
        {
            name:    "valid input",
            input:   "valid",
            wantErr: false,
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            err := validate(tt.input)
            if tt.wantErr {
                assert.Error(t, err)
                assert.Contains(t, err.Error(), tt.errString)
            } else {
                assert.NoError(t, err)
            }
        })
    }
}
```

### Тестирование с использованием параллельных тестов

```go
func TestParallel(t *testing.T) {
    t.Parallel()  // Тест может выполняться параллельно

    // Тест код
}

func TestSequential(t *testing.T) {
    // Тест выполняется последовательно
    // Используется для тестов, которые не могут выполняться параллельно
}
```

### Тестирование с использованием cleanup

```go
func TestWithCleanup(t *testing.T) {
    // Настройка
    tempFile := createTempFile(t)

    // Cleanup выполнится после теста
    t.Cleanup(func() {
        os.Remove(tempFile)
    })

    // Тест код
}
```

### Тестирование с использованием skip

```go
func TestSkipOnCondition(t *testing.T) {
    if runtime.GOOS == "windows" {
        t.Skip("Skipping on Windows")
    }

    // Тест код
}
```

### Тестирование с использованием fatal

```go
func TestWithFatal(t *testing.T) {
    result, err := criticalOperation()
    if err != nil {
        t.Fatalf("Critical operation failed: %v", err)
        // Код после Fatal не выполнится
    }

    // Продолжение теста
}
```

### Практические примеры: тестирование сервисов

```go
type UserService struct {
    repo UserRepository
}

func TestUserService_CreateUser(t *testing.T) {
    mockRepo := &MockUserRepository{}
    service := NewUserService(mockRepo)

    mockRepo.On("Create", mock.Anything).Return(&User{ID: 1, Name: "Alice"}, nil)

    user, err := service.CreateUser("Alice")

    assert.NoError(t, err)
    assert.Equal(t, "Alice", user.Name)
    mockRepo.AssertExpectations(t)
}
```

### Практические примеры: тестирование middleware

```go
func TestAuthMiddleware(t *testing.T) {
    handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.WriteHeader(http.StatusOK)
    })

    middleware := AuthMiddleware(handler)

    tests := []struct {
        name           string
        authHeader     string
        expectedStatus int
    }{
        {
            name:           "valid token",
            authHeader:     "Bearer valid-token",
            expectedStatus: http.StatusOK,
        },
        {
            name:           "invalid token",
            authHeader:     "Bearer invalid-token",
            expectedStatus: http.StatusUnauthorized,
        },
        {
            name:           "no token",
            authHeader:     "",
            expectedStatus: http.StatusUnauthorized,
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            req := httptest.NewRequest("GET", "/", nil)
            if tt.authHeader != "" {
                req.Header.Set("Authorization", tt.authHeader)
            }
            w := httptest.NewRecorder()

            middleware.ServeHTTP(w, req)

            assert.Equal(t, tt.expectedStatus, w.Code)
        })
    }
}
```

### Практические примеры: тестирование с временем

```go
func TestWithTime(t *testing.T) {
    // Использование фиксированного времени для тестов
    fixedTime := time.Date(2025, 1, 1, 12, 0, 0, 0, time.UTC)

    // Тест с фиксированным временем
    result := processWithTime(fixedTime)

    assert.Equal(t, expectedResult, result)
}
```

### Практические примеры: Тестирование HTTP handlers

```go
func TestHTTPHandler(t *testing.T) {
    handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.WriteHeader(http.StatusOK)
        w.Write([]byte("OK"))
    })

    req := httptest.NewRequest("GET", "/test", nil)
    w := httptest.NewRecorder()

    handler.ServeHTTP(w, req)

    if w.Code != http.StatusOK {
        t.Errorf("expected status %d, got %d", http.StatusOK, w.Code)
    }

    if w.Body.String() != "OK" {
        t.Errorf("expected body %q, got %q", "OK", w.Body.String())
    }
}

func TestHTTPHandlerWithJSON(t *testing.T) {
    handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.Header().Set("Content-Type", "application/json")
        json.NewEncoder(w).Encode(map[string]string{"status": "ok"})
    })

    req := httptest.NewRequest("GET", "/test", nil)
    w := httptest.NewRecorder()

    handler.ServeHTTP(w, req)

    var result map[string]string
    if err := json.Unmarshal(w.Body.Bytes(), &result); err != nil {
        t.Fatalf("failed to unmarshal response: %v", err)
    }

    if result["status"] != "ok" {
        t.Errorf("expected status 'ok', got %q", result["status"])
    }
}
```

### Практические примеры: Тестирование конкурентного кода

```go
func TestConcurrentAccess(t *testing.T) {
    var counter int64
    var wg sync.WaitGroup

    numGoroutines := 100
    iterations := 1000

    for i := 0; i < numGoroutines; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for j := 0; j < iterations; j++ {
                atomic.AddInt64(&counter, 1)
            }
        }()
    }

    wg.Wait()

    expected := int64(numGoroutines * iterations)
    if counter != expected {
        t.Errorf("expected counter %d, got %d", expected, counter)
    }
}

func TestRaceCondition(t *testing.T) {
    var counter int
    var mu sync.Mutex

    numGoroutines := 10
    var wg sync.WaitGroup

    for i := 0; i < numGoroutines; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            for j := 0; j < 100; j++ {
                mu.Lock()
                counter++
                mu.Unlock()
            }
        }()
    }

    wg.Wait()

    expected := numGoroutines * 100
    if counter != expected {
        t.Errorf("expected counter %d, got %d", expected, counter)
    }
}
```

### Практические примеры: Тестирование с cleanup

```go
func TestWithCleanup(t *testing.T) {
    tempFile, err := os.CreateTemp("", "test-*.txt")
    if err != nil {
        t.Fatalf("failed to create temp file: %v", err)
    }
    defer os.Remove(tempFile.Name())
    defer tempFile.Close()

    // Использование t.Cleanup (Go 1.14+)
    t.Cleanup(func() {
        os.Remove(tempFile.Name())
        tempFile.Close()
    })

    // Тест с использованием tempFile
    _, err = tempFile.WriteString("test data")
    if err != nil {
        t.Fatalf("failed to write: %v", err)
    }
}
```

### Практические примеры: Тестирование с проверкой ошибок

```go
func TestErrorHandling(t *testing.T) {
    tests := []struct {
        name          string
        input         string
        expectedErr   error
        expectedValue string
    }{
        {
            name:          "valid input",
            input:         "valid",
            expectedErr:   nil,
            expectedValue: "processed: valid",
        },
        {
            name:          "empty input",
            input:         "",
            expectedErr:   ErrEmptyInput,
            expectedValue: "",
        },
        {
            name:          "invalid input",
            input:         "invalid",
            expectedErr:   ErrInvalidInput,
            expectedValue: "",
        },
    }

    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            result, err := processInput(tt.input)

            if !errors.Is(err, tt.expectedErr) {
                t.Errorf("expected error %v, got %v", tt.expectedErr, err)
            }

            if result != tt.expectedValue {
                t.Errorf("expected value %q, got %q", tt.expectedValue, result)
            }
        })
    }
}
```

### Практические примеры: Интеграционные тесты с testcontainers

```go
func TestWithDatabase(t *testing.T) {
    ctx := context.Background()

    req := testcontainers.ContainerRequest{
        Image:        "postgres:13",
        ExposedPorts: []string{"5432/tcp"},
        Env: map[string]string{
            "POSTGRES_PASSWORD": "postgres",
            "POSTGRES_USER":     "postgres",
            "POSTGRES_DB":       "testdb",
        },
        WaitingFor: wait.ForLog("database system is ready to accept connections"),
    }

    postgresC, err := testcontainers.GenericContainer(ctx, testcontainers.GenericContainerRequest{
        ContainerRequest: req,
        Started:          true,
    })
    if err != nil {
        t.Fatalf("failed to start container: %v", err)
    }
    defer postgresC.Terminate(ctx)

    host, _ := postgresC.Host(ctx)
    port, _ := postgresC.MappedPort(ctx, "5432")

    dsn := fmt.Sprintf("postgres://postgres:postgres@%s:%s/testdb?sslmode=disable",
        host, port.Port())

    db, err := sql.Open("postgres", dsn)
    if err != nil {
        t.Fatalf("failed to connect: %v", err)
    }
    defer db.Close()

    // Тесты с использованием базы данных
    _, err = db.Exec("CREATE TABLE users (id SERIAL PRIMARY KEY, name TEXT)")
    if err != nil {
        t.Fatalf("failed to create table: %v", err)
    }

    // Дальнейшие тесты...
}
```

### Практические примеры: Property-based тестирование

```go
import (
    "testing"
    "testing/quick"
)

func TestReverse(t *testing.T) {
    f := func(s string) bool {
        reversed := reverseString(s)
        doubleReversed := reverseString(reversed)
        return s == doubleReversed
    }

    if err := quick.Check(f, nil); err != nil {
        t.Error(err)
    }
}

func TestAddCommutative(t *testing.T) {
    f := func(a, b int) bool {
        return add(a, b) == add(b, a)
    }

    if err := quick.Check(f, nil); err != nil {
        t.Error(err)
    }
}
```

### Практические примеры: Моки интерфейсов

```go
type UserService interface {
    GetUser(id int) (*User, error)
    CreateUser(user *User) error
}

type MockUserService struct {
    GetUserFunc    func(id int) (*User, error)
    CreateUserFunc func(user *User) error
}

func (m *MockUserService) GetUser(id int) (*User, error) {
    if m.GetUserFunc != nil {
        return m.GetUserFunc(id)
    }
    return nil, fmt.Errorf("not implemented")
}

func (m *MockUserService) CreateUser(user *User) error {
    if m.CreateUserFunc != nil {
        return m.CreateUserFunc(user)
    }
    return fmt.Errorf("not implemented")
}

func TestWithMock(t *testing.T) {
    mockService := &MockUserService{
        GetUserFunc: func(id int) (*User, error) {
            return &User{ID: id, Name: "Test"}, nil
        },
    }

    user, err := mockService.GetUser(1)
    if err != nil {
        t.Fatalf("unexpected error: %v", err)
    }

    if user.ID != 1 {
        t.Errorf("expected ID 1, got %d", user.ID)
    }
}
```

## Лучшие практики

1. **Используйте `table-driven` tests** — для тестирования множественных сценариев
2. **Именуйте тесты описательно** — имена тестов должны описывать что тестируется
3. **Используйте t.`Helper()`** — для вспомогательных функций
4. **Тестируйте граничные случаи** — включая ошибки и крайние значения
5. **Избегайте тестовых зависимостей** — тесты должны быть независимыми
6. **Используйте подтесты** — для организации связанных тестов
7. **Измеряйте покрытие** — следите за покрытием кода тестами
8. **Используйте моки осторожно** — предпочитайте реальные зависимости когда возможно
9. **Тестируйте поведение, а не реализацию** — тесты должны проверять что делает код, а не как
10. **Держите тесты быстрыми** — медленные тесты замедляют разработку
11. **Используйте cleanup** — правильно очищайте ресурсы после тестов
12. **Тестируйте конкурентный код** — проверяйте **race conditions**
13. **Используйте `property-based` тестирование** — для проверки свойств
14. **Используйте интеграционные тесты** — для тестирования всего стека
15. **Документируйте тесты** — объясняйте что тестируется

### Практические примеры: Тестирование с использованием testify

```go
import (
    "github.com/stretchr/testify/assert"
    "github.com/stretchr/testify/require"
    "github.com/stretchr/testify/suite"
)

type UserSuite struct {
    suite.Suite
    users []User
}

func (s *UserSuite) SetupTest() {
    s.users = []User{
        {ID: 1, Name: "Alice"},
        {ID: 2, Name: "Bob"},
    }
}

func (s *UserSuite) TestFindUser() {
    user := findUser(s.users, 1)
    assert.NotNil(s.T(), user)
    assert.Equal(s.T(), "Alice", user.Name)
}

func (s *UserSuite) TestUserNotFound() {
    user := findUser(s.users, 999)
    assert.Nil(s.T(), user)
}

func TestUserSuite(t *testing.T) {
    suite.Run(t, new(UserSuite))
}
```

### Практические примеры: Golden файлы для тестирования

```go
func TestWithGoldenFiles(t *testing.T) {
    result := processData(input)

    goldenFile := filepath.Join("testdata", "expected_output.golden")
    if *update {
        os.WriteFile(goldenFile, result, 0644)
        return
    }

    expected, err := os.ReadFile(goldenFile)
    require.NoError(t, err)

    assert.Equal(t, string(expected), string(result))
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Тестирование в Go предоставляет мощные инструменты для обеспечения качества кода. Понимание **unit** тестов, **table-driven tests**, **benchmarks**, примеров, моков, интеграционных тестов, **property-based** тестирования, использования библиотек **testify**, **golden** файлов и практических техник критично для создания надежных приложений. Правильное использование инструментов тестирования позволяет создавать качественный, проверенный код, который легко поддерживать, развивать и уверенно рефакторить.

## Дополнительные ресурсы

- [Go Testing Documentation](https://pkg.go.dev/testing)
- [Go Testing Best Practices](https://go.dev/doc/tutorial/add-a-test)
- [Table-Driven Tests](https://go.dev/wiki/TableDrivenTests)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
