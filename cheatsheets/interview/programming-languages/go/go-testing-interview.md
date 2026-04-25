---
title: "Вопросы на собеседовании: Go Testing"
description: "Стандартный testing пакет, table-driven tests, subtests, t.Helper, benchmarks, fuzzing (Go 1.18+), httptest, testify, моки, интеграционные тесты, coverage"
tags:
  - interview
  - programming-languages
  - go-testing-interview
aliases:
  - "Go testing interview"
  - "Go test interview"
  - "Go fuzzing interview"
  - "Go benchmarks interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Go Testing`

Стандартный пакет `testing` — простой, но мощный: table-driven tests, subtests, benchmarks, fuzzing (с Go 1.18). На интервью спрашивают разницу с JUnit/RSpec, моки в Go (interfaces), `httptest` для HTTP, coverage, integration tests.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [testing package](https://pkg.go.dev/testing)
- [Go test command](https://go.dev/cmd/go/#hdr-Test_packages)
- [Go fuzzing tutorial](https://go.dev/doc/tutorial/fuzz)
- [Subtests and Sub-benchmarks](https://go.dev/blog/subtests)
- [testify library](https://github.com/stretchr/testify)
- [gomock](https://github.com/uber-go/mock)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Как написать unit test в Go?](#q1--как-написать-unit-test-в-go)
- [Q2. (!) Где располагать тесты?](#q2--где-располагать-тесты)
- [Q3. (!) Как запускать тесты?](#q3--как-запускать-тесты)
- [Q4. testing.T — основные методы?](#q4-testingt--основные-методы)

**Style и patterns**
- [Q5. (!) Table-driven tests?](#q5--table-driven-tests)
- [Q6. (!) Subtests через t.Run?](#q6--subtests-через-trun)
- [Q7. (!) t.Helper() — что делает?](#q7--thelper--что-делает)
- [Q8. setup/teardown в Go (нет JUnit @Before)?](#q8-setupteardown-в-go-нет-junit-before)
- [Q9. (!) Параллельные тесты — t.Parallel?](#q9--параллельные-тесты--tparallel)

**Assertions и моки**
- [Q10. (!) Почему в стандарте нет assertions?](#q10--почему-в-стандарте-нет-assertions)
- [Q11. (!) testify — assert и require?](#q11--testify--assert-и-require)
- [Q12. (!) Mocking в Go — общие подходы?](#q12--mocking-в-go--общие-подходы)
- [Q13. gomock vs mockery vs ручные моки?](#q13-gomock-vs-mockery-vs-ручные-моки)

**HTTP testing**
- [Q14. (!) httptest для HTTP handlers?](#q14--httptest-для-http-handlers)
- [Q15. httptest.NewServer для тестирования клиентов?](#q15-httptestnewserver-для-тестирования-клиентов)

**Benchmarks**
- [Q16. (!) Как писать benchmarks?](#q16--как-писать-benchmarks)
- [Q17. (!) b.ResetTimer, b.ReportAllocs?](#q17--bresettimer-breportallocs)
- [Q18. Sub-benchmarks?](#q18-sub-benchmarks)
- [Q19. benchstat — сравнение результатов?](#q19-benchstat--сравнение-результатов)

**Fuzzing**
- [Q20. (!) Что такое fuzzing в Go (1.18+)?](#q20--что-такое-fuzzing-в-go-118)
- [Q21. Когда применять fuzzing?](#q21-когда-применять-fuzzing)

**Coverage**
- [Q22. (!) Как считать coverage?](#q22--как-считать-coverage)
- [Q23. Coverage profile — как читать?](#q23-coverage-profile--как-читать)

**Интеграционные тесты**
- [Q24. (!) Build tags для разделения unit и integration?](#q24--build-tags-для-разделения-unit-и-integration)
- [Q25. Testcontainers для Go?](#q25-testcontainers-для-go)

**Best practices**
- [Q26. (!) Где запускать тесты — рядом с кодом или в отдельной директории?](#q26--где-запускать-тесты--рядом-с-кодом-или-в-отдельной-директории)
- [Q27. (!) testing.M — TestMain для setup всего пакета?](#q27--testingm--testmain-для-setup-всего-пакета)
- [Q28. (!) Race detector в тестах?](#q28--race-detector-в-тестах)

## Q1. (!) Как написать unit test в Go?

Test = функция в файле `*_test.go` с сигнатурой `Test*`:

```go
// math.go
package mathx

func Add(a, b int) int { return a + b }

// math_test.go
package mathx

import "testing"

func TestAdd(t *testing.T) {
    result := Add(2, 3)
    if result != 5 {
        t.Errorf("Add(2, 3) = %d; want 5", result)
    }
}
```

Запуск: `go test`. Без аннотаций, без фреймворков.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Где располагать тесты? Частая ошибка в реальном коде.

**Рядом с кодом**, в том же пакете:

```
mathx/
  ├── math.go
  ├── math_test.go      ← package mathx (white-box)
  └── math_ext_test.go  ← package mathx_test (black-box)
```

**White-box** (`package mathx`) — доступ к unexported.
**Black-box** (`package mathx_test`) — только public API. Лучшая практика для library кода.

Не выносят в отдельную `tests/` директорию (как в Java/Python) — `go test` ожидает их рядом.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Как запускать тесты? Частая ошибка в реальном коде.

```bash
go test                        # текущий пакет
go test ./...                  # все пакеты
go test -v ./...               # verbose
go test -run TestAdd            # один тест по regex
go test -run "TestAdd|TestSub" # несколько по regex
go test -timeout 30s            # timeout
go test -race ./...             # с race detector
go test -count=1 ./...          # отключить кеш
go test -short ./...            # пропустить долгие тесты (testing.Short())
go test -count=10 ./...         # повторить 10 раз (для нестабильных)
```

Тесты **кешируются** Go — если код и `*_test.go` не менялись, повторный запуск даёт мгновенный "ok (cached)". Отключить — `-count=1`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. testing.T — основные методы? Частая ошибка в реальном коде.

```go
func TestSomething(t *testing.T) {
    t.Log("info")               // вывод (только в -v или при failure)
    t.Logf("count=%d", n)

    t.Error("oops")              // mark failed, продолжить
    t.Errorf("got %v, want %v", got, want)

    t.Fatal("stop")              // mark failed + остановить test
    t.Fatalf("got %v, want %v", got, want)

    t.Skip("not implemented")    // пропустить
    t.SkipNow()

    if testing.Short() { t.Skip() } // если -short

    t.Parallel()                 // параллельный запуск
    t.Helper()                   // см. Q7

    t.Cleanup(func() {
        // выполнится при завершении теста
    })

    name := t.Name()             // имя теста
}
```

`Error` vs `Fatal`: `Fatal` останавливает текущий тест немедленно. `Error` — отмечает failed, но продолжает.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) Table-driven tests? Частая ошибка в реальном коде.

Идиоматичный паттерн в Go:

```go
func TestAdd(t *testing.T) {
    tests := []struct {
        name     string
        a, b     int
        expected int
    }{
        {"positives", 2, 3, 5},
        {"negatives", -1, -2, -3},
        {"zero", 0, 5, 5},
        {"overflow boundary", math.MaxInt32, 1, math.MaxInt32 + 1},
    }

    for _, tc := range tests {
        t.Run(tc.name, func(t *testing.T) {
            if got := Add(tc.a, tc.b); got != tc.expected {
                t.Errorf("Add(%d, %d) = %d; want %d", tc.a, tc.b, got, tc.expected)
            }
        })
    }
}
```

**Преимущества:**
- Лаконичность
- Легко добавить новый кейс
- Отдельные subtests видны в отчёте

Используется в стандартной библиотеке Go повсеместно.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Subtests через t.Run? Частая ошибка в реальном коде.

```go
func TestUser(t *testing.T) {
    t.Run("create", func(t *testing.T) {
        // ...
    })
    t.Run("update", func(t *testing.T) {
        // ...
    })
    t.Run("delete", func(t *testing.T) {
        // ...
    })
}
```

Subtests дают:
- Иерархию в отчётах
- Отдельный запуск: `go test -run TestUser/create`
- Отдельный `t.Parallel()` для каждого subtest
- Изолированные failures


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) t.Helper() — что делает? Частая ошибка в реальном коде.

Помечает функцию как **helper** — при failure trace показывает caller, не helper.

```go
func assertEqual(t *testing.T, got, want int) {
    t.Helper() // важно!
    if got != want {
        t.Errorf("got %d, want %d", got, want)
    }
}

func TestSomething(t *testing.T) {
    assertEqual(t, Add(1, 2), 4) // failure → строка с этим вызовом
    // Без t.Helper() → строка внутри assertEqual
}
```

Без `t.Helper()` сложно понять, **какой** вызов assertEqual упал.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. setup/teardown в Go (нет JUnit @Before)? Частая ошибка в реальном коде.

В Go нет аннотаций. Используют:

**Setup перед каждым тестом** — функция:

```go
func setupTest(t *testing.T) (*App, func()) {
    app := NewApp()
    cleanup := func() { app.Close() }
    return app, cleanup
}

func TestSomething(t *testing.T) {
    app, cleanup := setupTest(t)
    defer cleanup()
    // ... test using app
}

// Лучше — t.Cleanup
func setupTest(t *testing.T) *App {
    app := NewApp()
    t.Cleanup(func() { app.Close() })
    return app
}

func TestSomething(t *testing.T) {
    app := setupTest(t) // cleanup автоматический
}
```

`t.Cleanup` — вызывается даже при `t.Fatal`. Лучше чем `defer`, особенно для setup-функций.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Параллельные тесты — t.Parallel? Частая ошибка в реальном коде.

```go
func TestA(t *testing.T) {
    t.Parallel()
    // ...
}

func TestB(t *testing.T) {
    t.Parallel()
    // ...
}
```

Тесты с `t.Parallel()` идут параллельно. **Сначала** все sequential тесты, **потом** все parallel.

В **table-driven** с subtests — нужно скопировать переменную:

```go
for _, tc := range tests {
    tc := tc // capture range variable (до Go 1.22)
    t.Run(tc.name, func(t *testing.T) {
        t.Parallel()
        // используем tc
    })
}
```

С **Go 1.22** — переменная цикла per-iteration → копирование не нужно.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Почему в стандарте нет assertions? Частая ошибка в реальном коде.

Философия Go: **минимум magic**, всё явно.

Стандарт даёт `t.Error/Fatal` + ручную проверку. Преимущества:
- Чёткие сообщения об ошибках
- Простой код
- Нет dependencies

Недостатки:
- Многословно (`if got != want { t.Errorf(...) }`)
- Нет красивых diff'ов

Многие проекты используют `testify` (`assert`/`require`) для удобства.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) testify — assert и require? Частая ошибка в реальном коде.

```go
import (
    "github.com/stretchr/testify/assert"
    "github.com/stretchr/testify/require"
)

func TestSomething(t *testing.T) {
    user, err := getUser(1)
    require.NoError(t, err)        // failed → t.Fatal
    require.NotNil(t, user)

    assert.Equal(t, "Alice", user.Name)  // failed → t.Error (продолжает)
    assert.Equal(t, 30, user.Age)

    // Diff diff diff
    assert.ElementsMatch(t, []int{1, 2, 3}, []int{3, 1, 2})
    assert.JSONEq(t, `{"a":1}`, `{"a":1}`)
    assert.WithinDuration(t, time.Now(), got, time.Second)
}
```

| Подход | Эффект |
|--------|--------|
| `assert.X` | `t.Error` — продолжает |
| `require.X` | `t.Fatal` — останавливает |

`testify/mock` — фреймворк mock'ов, `testify/suite` — TestSuite (как JUnit class).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Mocking в Go — общие подходы? Это антипаттерн или неправильный выбор в production.

В Go нет аннотаций `@Mock`. Идиома — **interfaces**:

```go
// production
type UserRepo interface {
    FindByID(id int) (*User, error)
}

type UserService struct {
    repo UserRepo
}

func (s *UserService) GetUser(id int) (*User, error) {
    return s.repo.FindByID(id)
}

// test — ручной mock
type mockRepo struct {
    user *User
    err  error
}

func (m *mockRepo) FindByID(id int) (*User, error) {
    return m.user, m.err
}

func TestGetUser(t *testing.T) {
    s := &UserService{repo: &mockRepo{user: &User{Name: "Alice"}}}
    u, err := s.GetUser(1)
    assert.NoError(t, err)
    assert.Equal(t, "Alice", u.Name)
}
```

Часто mock пишут вручную или генерируют (gomock, mockery).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. gomock vs mockery vs ручные моки? Это антипаттерн или неправильный выбор в production.

| Подход | Плюсы | Минусы |
|--------|-------|--------|
| Ручные | Простые, понятные | Boilerplate для каждой interface |
| **gomock** (Uber) | Mock с expectations | Generated code, нужен тулинг |
| **mockery** | Generated mocks с testify-like API | Зависимость на mockery CLI |
| **testify/mock** | Часть testify | Verbose API |

```bash
# gomock
mockgen -source=repo.go -destination=mock_repo.go

# mockery
mockery --name=UserRepo
```

```go
// gomock
mockRepo := NewMockUserRepo(ctrl)
mockRepo.EXPECT().FindByID(1).Return(&User{Name: "Alice"}, nil)
```

В большинстве проектов — **ручные моки** для простых случаев + **mockery/gomock** для сложных.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. (!) httptest для HTTP handlers? Частая ошибка в реальном коде.

```go
import "net/http/httptest"

func TestHandler(t *testing.T) {
    req := httptest.NewRequest("GET", "/users/1", nil)
    w := httptest.NewRecorder()

    UserHandler(w, req) // вызываем handler напрямую

    resp := w.Result()
    body, _ := io.ReadAll(resp.Body)

    assert.Equal(t, http.StatusOK, resp.StatusCode)
    assert.Contains(t, string(body), "Alice")
}
```

`httptest.NewRecorder` записывает response без реального сервера — быстро.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. httptest.NewServer для тестирования клиентов? Частая ошибка в реальном коде.

```go
func TestUserClient(t *testing.T) {
    server := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        if r.URL.Path == "/users/1" {
            json.NewEncoder(w).Encode(User{Name: "Alice"})
            return
        }
        http.NotFound(w, r)
    }))
    defer server.Close()

    client := NewUserClient(server.URL)
    user, err := client.GetUser(1)
    assert.NoError(t, err)
    assert.Equal(t, "Alice", user.Name)
}
```

`httptest.NewServer` — реальный HTTP сервер на random порте. Используется для **integration-style** тестов клиентов.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. (!) Как писать benchmarks? Частая ошибка в реальном коде.

```go
func BenchmarkAdd(b *testing.B) {
    for i := 0; i < b.N; i++ {
        Add(2, 3)
    }
}
```

Запуск:

```bash
go test -bench=. -benchmem
BenchmarkAdd-8   1000000000   0.30 ns/op   0 B/op   0 allocs/op
```

`b.N` — runtime подбирает, чтобы общее время было ~1 сек. Чем быстрее код, тем больше итераций.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) b.ResetTimer, b.ReportAllocs? Частая ошибка в реальном коде.

```go
func BenchmarkSomething(b *testing.B) {
    // Setup — не считаем
    data := makeBigData()
    b.ResetTimer()
    b.ReportAllocs()

    for i := 0; i < b.N; i++ {
        process(data)
    }

    b.StopTimer()
    cleanup()
}
```

- `b.ResetTimer()` — сбросить таймер (после setup)
- `b.StopTimer()` — остановить (на teardown)
- `b.ReportAllocs()` — показать allocs/op

С Go 1.20+ есть `b.Loop()`:

```go
func BenchmarkSomething(b *testing.B) {
    setup()
    for b.Loop() { // вместо for i := 0; i < b.N; i++
        process()
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Sub-benchmarks? Частая ошибка в реальном коде.

```go
func BenchmarkAdd(b *testing.B) {
    sizes := []int{10, 100, 1000, 10000}
    for _, size := range sizes {
        b.Run(fmt.Sprintf("size_%d", size), func(b *testing.B) {
            data := make([]int, size)
            b.ResetTimer()
            for i := 0; i < b.N; i++ {
                process(data)
            }
        })
    }
}
```

Output:
```
BenchmarkAdd/size_10-8       100000000   12 ns/op
BenchmarkAdd/size_100-8       10000000  120 ns/op
BenchmarkAdd/size_1000-8       1000000 1200 ns/op
```

Полезно для проверки масштабирования (ожидаем ли O(n)?).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. benchstat — сравнение результатов? Частая ошибка в реальном коде.

```bash
go install golang.org/x/perf/cmd/benchstat@latest

# Бейзлайн
go test -bench=. -count=10 > old.txt

# После изменений
go test -bench=. -count=10 > new.txt

benchstat old.txt new.txt
```

Output:
```
name              old time/op    new time/op    delta
Process-8           1.20µs         0.85µs       -29.17% (p=0.000)
```

`p-value` показывает статистическую значимость. Без `-count=10+` — нельзя доверять микро-отличиям.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Что такое fuzzing в Go (1.18+)? Частая ошибка в реальном коде.

`Fuzzing` — автоматическая генерация **случайных** входов для поиска edge cases (panic, неожиданное поведение).

```go
func FuzzReverse(f *testing.F) {
    // Seed corpus — стартовые примеры
    f.Add("hello")
    f.Add("foo")
    f.Add("")

    f.Fuzz(func(t *testing.T, s string) {
        rev := Reverse(s)
        doubleRev := Reverse(rev)

        if s != doubleRev {
            t.Errorf("Reverse(Reverse(%q)) = %q; want %q", s, doubleRev, s)
        }

        if !utf8.ValidString(rev) {
            t.Errorf("Reverse produced invalid UTF-8: %q", rev)
        }
    })
}
```

Запуск:

```bash
go test -fuzz=FuzzReverse -fuzztime=10s
```

Найдённые "плохие" inputs автоматически добавляются в `testdata/fuzz/` для регрессионных тестов.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. Когда применять fuzzing? Частая ошибка в реальном коде.

**Кандидаты для fuzzing:**
- Парсеры (JSON, XML, custom protocols)
- Декодеры/кодировщики (URL, Base64)
- Crypto (но осторожно — slow)
- Public API с user input
- Functions, где `f(g(x)) == x` (round-trip property)

**Не для:**
- Бизнес-логика (нет очевидной property)
- Тесты integration (медленно)

Go fuzzing — **coverage-guided** (как AFL): двигается в сторону новых code paths.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. (!) Как считать coverage? Это антипаттерн или неправильный выбор в production.

```bash
go test -cover ./...
go test -coverprofile=cover.out ./...
go tool cover -html=cover.out  # web view
go tool cover -func=cover.out  # текстовый отчёт
```

Output:
```
PASS
coverage: 87.5% of statements
ok      example.com/myapp  0.005s
```

**Build mode:**
- `-covermode=set` (default) — есть/нет coverage
- `-covermode=count` — счётчик вызовов
- `-covermode=atomic` — для concurrent кода

С Go 1.20+ — `go test -cover -test.gocoverdir=...` для интеграционных тестов.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. Coverage profile — как читать? Это антипаттерн или неправильный выбор в production.

```
mode: set
example.com/myapp/math.go:5.18,8.2 2 1
```

Формат: `file:startLine.startCol,endLine.endCol numStatements count`.

`go tool cover -html` — визуализация (зелёный = covered, красный = not covered).

**Best practice:** не **гонись** за 100% coverage. Фокусируйся на critical paths и edge cases.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. (!) Build tags для разделения unit и integration? Частая ошибка в реальном коде.

```go
//go:build integration

package mypackage_test

func TestIntegrationDB(t *testing.T) { ... }
```

```bash
# Только unit
go test ./...

# Только integration
go test -tags=integration ./...

# И то и то
go test -tags=integration ./...
```

Build tags позволяют **управлять**, какие файлы компилировать. Полезно для медленных тестов или зависимых от env (DB, S3, ...).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Testcontainers для Go? Это антипаттерн или неправильный выбор в production.

```go
import (
    "github.com/testcontainers/testcontainers-go"
    "github.com/testcontainers/testcontainers-go/modules/postgres"
)

func TestRepo(t *testing.T) {
    ctx := context.Background()
    container, err := postgres.Run(ctx,
        "postgres:15-alpine",
        postgres.WithDatabase("mydb"),
        postgres.WithUsername("user"),
        postgres.WithPassword("pass"),
    )
    require.NoError(t, err)
    t.Cleanup(func() { container.Terminate(ctx) })

    connStr, _ := container.ConnectionString(ctx)
    db, _ := sql.Open("postgres", connStr)

    // тестируем с реальным postgres
}
```

Аналог Java testcontainers — поднимает Docker-контейнер на время теста. Slow, но **реальный** integration test.

Подробнее — в [Testcontainers](../../testing/testcontainers-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. (!) Где запускать тесты — рядом с кодом или в отдельной директории? Частая ошибка в реальном коде.

**Рядом с кодом** — стандарт Go.

Преимущества:
- Доступ к unexported (white-box тесты)
- Тесты ходят с кодом при рефакторинге
- IDE автоматически показывает coverage

Недостатки:
- Видно тесты при просмотре кода (но IDE может скрывать `*_test.go`)

В отдельную директорию — **только** для очень больших test suites (например, e2e).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. (!) testing.M — TestMain для setup всего пакета? Частая ошибка в реальном коде.

```go
func TestMain(m *testing.M) {
    // Setup
    setupDatabase()

    code := m.Run()

    // Teardown
    teardownDatabase()

    os.Exit(code)
}
```

`TestMain` — entry point всего test пакета. Запускает все тесты через `m.Run()`.

Используется для:
- Setup БД
- Запуск Docker containers
- Init logger / config


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. (!) Race detector в тестах? Частая ошибка в реальном коде.

**Всегда** запускай тесты с `-race` в CI:

```bash
go test -race ./...
```

Race detector найдёт data races, которые проявляются только при определённом scheduling. Без него — race может быть **зеленым 100 раз, потом упасть в production**.

Race detector замедляет тесты в 5-10 раз. Включай в **CI**, но локально опционально.

---

## See also

- [Go (базовый)](go-interview.md) — основы
- [Go Concurrency](go-concurrency-interview.md) — race conditions
- [Go Standard Library](go-stdlib-interview.md) — testing пакет
- [Go Modules](go-modules-interview.md) — управление зависимостями (testify)
- [Unit Testing](../../testing/unit-testing-interview.md) — общие принципы
- [Integration Testing](../../testing/integration-testing-interview.md) — testcontainers
- [Mockito](../../testing/mockito-interview.md) — для сравнения с Java mocks
- [Testcontainers](../../testing/testcontainers-interview.md) — Docker в тестах
- [Performance Testing](../../performance/performance-testing-interview.md) — benchmarks


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Go Concurrency](go-concurrency-interview.md) Частая ошибка в реальном коде.
- [Go Generics](go-generics-interview.md)
- [Go](go-interview.md)
- [Go Memory и GC](go-memory-gc-interview.md)
- [Go Modules](go-modules-interview.md)
- [Go Standard Library](go-stdlib-interview.md)
- [Шпаргалка: Go: тестирование](../../../languages/go/go-testing.md) — теория
