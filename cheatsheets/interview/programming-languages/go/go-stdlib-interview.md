---
title: "Вопросы на собеседовании: Go Standard Library"
description: "Стандартная библиотека Go: net/http, encoding/json, io/ioutil, strings, time, context, log/slog, database/sql, embed, sort, errors"
tags:
  - interview
  - programming-languages
  - go-stdlib-interview
aliases:
  - "Go stdlib interview"
  - "Go standard library interview"
  - "net/http Go interview"
  - "encoding/json Go interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Go Standard Library`

Стандартная библиотека Go покрывает 80% потребностей backend-разработки **без third-party**. На интервью спрашивают: устройство `net/http`, нюансы `encoding/json`, новый `log/slog`, `database/sql`, `time` пакет, `embed` (с Go 1.16), `errors` wrapping.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Standard Library](https://pkg.go.dev/std)
- [net/http package](https://pkg.go.dev/net/http)
- [encoding/json package](https://pkg.go.dev/encoding/json)
- [database/sql tutorial](https://go.dev/doc/database/index)
- [log/slog (Go 1.21+)](https://pkg.go.dev/log/slog)
- [embed package](https://pkg.go.dev/embed)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**net/http**
- [Q1. (!) HTTP-сервер на стандартном net/http?](#q1--http-сервер-на-стандартном-nethttp)
- [Q2. (!) ServeMux и роутинг?](#q2--servemux-и-роутинг)
- [Q3. (!) Middleware patterns в Go?](#q3--middleware-patterns-в-go)
- [Q4. (!) HTTP client с timeout?](#q4--http-client-с-timeout)
- [Q5. Graceful shutdown HTTP сервера?](#q5-graceful-shutdown-http-сервера)

**encoding/json**
- [Q6. (!) Marshal и Unmarshal?](#q6--marshal-и-unmarshal)
- [Q7. (!) Struct tags для JSON?](#q7--struct-tags-для-json)
- [Q8. (!) json.RawMessage — для отложенного парсинга?](#q8--jsonrawmessage--для-отложенного-парсинга)
- [Q9. Streaming JSON (Decoder/Encoder)?](#q9-streaming-json-decoderencoder)
- [Q10. Custom MarshalJSON / UnmarshalJSON?](#q10-custom-marshaljson--unmarshaljson)

**Strings и Bytes**
- [Q11. (!) strings.Builder vs конкатенация?](#q11--stringsbuilder-vs-конкатенация)
- [Q12. bytes.Buffer для эффективной работы с byte?](#q12-bytesbuffer-для-эффективной-работы-с-byte)

**Time**
- [Q13. (!) time.Time, time.Duration?](#q13--timetime-timeduration)
- [Q14. Парсинг времени — особенности формата?](#q14-парсинг-времени--особенности-формата)
- [Q15. Часовые пояса и UTC?](#q15-часовые-пояса-и-utc)
- [Q16. time.NewTimer, time.Tick, time.After?](#q16-timenewtimer-timetick-timeafter)

**Context**
- [Q17. (!) context.Context — основные методы?](#q17--contextcontext--основные-методы)

**Errors**
- [Q18. (!) errors.New, errors.Is, errors.As?](#q18--errorsnew-errorsis-errorsas)
- [Q19. errors.Join (Go 1.20+)?](#q19-errorsjoin-go-120)

**Logging**
- [Q20. (!) log/slog (structured logging) — что это?](#q20--logslog-structured-logging--что-это)
- [Q21. log vs slog vs zap vs logrus?](#q21-log-vs-slog-vs-zap-vs-logrus)

**database/sql**
- [Q22. (!) Как работает database/sql?](#q22--как-работает-databasesql)
- [Q23. Connection pool в Go?](#q23-connection-pool-в-go)
- [Q24. (!) Prepared statements?](#q24--prepared-statements)
- [Q25. Transactions, deferred Rollback?](#q25-transactions-deferred-rollback)

**I/O**
- [Q26. (!) io.Reader, io.Writer интерфейсы?](#q26--ioreader-iowriter-интерфейсы)
- [Q27. ioutil deprecation (Go 1.16+)?](#q27-ioutil-deprecation-go-116)

**embed (Go 1.16+)**
- [Q28. (!) //go:embed для статических файлов?](#q28--goembed-для-статических-файлов)

**Сортировка**
- [Q29. sort.Slice vs sort.Sort?](#q29-sortslice-vs-sortsort)
- [Q30. (!) slices пакет в Go 1.21+?](#q30--slices-пакет-в-go-121)

## Q1. (!) HTTP-сервер на стандартном net/http?

```go
package main

import (
    "fmt"
    "net/http"
)

func helloHandler(w http.ResponseWriter, r *http.Request) {
    fmt.Fprintf(w, "Hello, %s!", r.URL.Path[1:])
}

func main() {
    http.HandleFunc("/", helloHandler)
    http.ListenAndServe(":8080", nil)
}
```

`net/http` — **production-ready** HTTP server. Используется в Docker, Kubernetes, многих production сервисах **без фреймворка**.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) ServeMux и роутинг? Частая ошибка в реальном коде.

```go
mux := http.NewServeMux()
mux.HandleFunc("/users", listUsers)
mux.HandleFunc("/users/", getUser) // trailing slash важен

http.ListenAndServe(":8080", mux)
```

С Go 1.22+ — **улучшенный routing** в стандарте:

```go
mux := http.NewServeMux()

// Path patterns
mux.HandleFunc("GET /users", listUsers)
mux.HandleFunc("POST /users", createUser)
mux.HandleFunc("GET /users/{id}", getUser)
mux.HandleFunc("DELETE /users/{id}", deleteUser)

// Wildcards
mux.HandleFunc("GET /static/", serveStatic)
```

До Go 1.22 — нужны были третьесторонние роутеры (Chi, Gorilla, Gin). Сейчас стандарт достаточно мощный.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Middleware patterns в Go? Частая ошибка в реальном коде.

```go
type Middleware func(http.Handler) http.Handler

func Logging(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        next.ServeHTTP(w, r)
        log.Printf("%s %s took %v", r.Method, r.URL.Path, time.Since(start))
    })
}

func Auth(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        token := r.Header.Get("Authorization")
        if !validToken(token) {
            http.Error(w, "Unauthorized", http.StatusUnauthorized)
            return
        }
        next.ServeHTTP(w, r)
    })
}

// Композиция
handler := Logging(Auth(http.HandlerFunc(myHandler)))
http.Handle("/api", handler)
```

Middleware — обычные функции `func(http.Handler) http.Handler`. Никаких аннотаций, всё явно.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) HTTP client с timeout? Частая ошибка в реальном коде.

```go
client := &http.Client{
    Timeout: 5 * time.Second,
    Transport: &http.Transport{
        MaxIdleConns:        100,
        MaxIdleConnsPerHost: 10,
        IdleConnTimeout:     90 * time.Second,
    },
}

req, _ := http.NewRequestWithContext(ctx, "GET", url, nil)
resp, err := client.Do(req)
if err != nil {
    return err
}
defer resp.Body.Close()

body, err := io.ReadAll(resp.Body)
```

**Подвох:** `http.DefaultClient` — **без timeout**. Это означает, что зависший сервер может зависнуть твой клиент **навсегда**. **Никогда** не используй DefaultClient в production.

`http.Client` thread-safe — переиспользуй один экземпляр.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Graceful shutdown HTTP сервера? Частая ошибка в реальном коде.

```go
srv := &http.Server{
    Addr:    ":8080",
    Handler: mux,
}

go func() {
    if err := srv.ListenAndServe(); err != http.ErrServerClosed {
        log.Fatal(err)
    }
}()

// Wait for interrupt
quit := make(chan os.Signal, 1)
signal.Notify(quit, os.Interrupt, syscall.SIGTERM)
<-quit

// Graceful shutdown с timeout
ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
defer cancel()
if err := srv.Shutdown(ctx); err != nil {
    log.Fatal("Server forced to shutdown:", err)
}
```

`Shutdown` ждёт завершения **активных запросов** до timeout. Не принимает новые соединения.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Marshal и Unmarshal? Частая ошибка в реальном коде.

```go
import "encoding/json"

type User struct {
    Name string `json:"name"`
    Age  int    `json:"age"`
}

// Marshal: Go → JSON
u := User{Name: "Alice", Age: 30}
data, err := json.Marshal(u)
// data: {"name":"Alice","age":30}

// Unmarshal: JSON → Go
var u User
err := json.Unmarshal([]byte(`{"name":"Bob","age":25}`), &u)
```

**Подвох с малой/большой буквой:** только **exported поля** (с большой буквы) могут быть marshal/unmarshal'ed.

```go
type User struct {
    name string  // НЕ marshal'ит — lowercase
    Name string  // marshal'ит
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) Struct tags для JSON? Частая ошибка в реальном коде.

```go
type User struct {
    ID        int        `json:"id"`
    Name      string     `json:"name"`
    Email     string     `json:"email,omitempty"`     // omit если пустой
    Password  string     `json:"-"`                    // НЕ включать
    CreatedAt time.Time  `json:"created_at"`
    Roles     []string   `json:"roles,omitempty"`
}
```

**Особенности:**
- `omitempty` — не включает zero value (false, 0, "", nil, empty slice)
- `-` — никогда не сериализовать
- `,string` — конвертирует число в строку (для JS, где int64 теряет точность)

```go
type Money struct {
    Amount int64 `json:"amount,string"` // {"amount": "12345"}
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) json.RawMessage — для отложенного парсинга? Частая ошибка в реальном коде.

```go
type Event struct {
    Type    string          `json:"type"`
    Payload json.RawMessage `json:"payload"`
}

var e Event
json.Unmarshal(data, &e)

switch e.Type {
case "user_created":
    var p UserCreatedPayload
    json.Unmarshal(e.Payload, &p)
case "order_placed":
    var p OrderPayload
    json.Unmarshal(e.Payload, &p)
}
```

`json.RawMessage` — отложенный декодинг. Полезно для:
- Heterogeneous events
- Прокси (просто перебросить как есть)
- Performance (если payload не нужен сразу)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Streaming JSON (Decoder/Encoder)? Частая ошибка в реальном коде.

Для **больших** данных или streaming:

```go
// Streaming decode
dec := json.NewDecoder(resp.Body)
for {
    var item Item
    if err := dec.Decode(&item); err == io.EOF {
        break
    } else if err != nil {
        log.Fatal(err)
    }
    process(item)
}

// Streaming encode
enc := json.NewEncoder(w)
for item := range items {
    if err := enc.Encode(item); err != nil {
        log.Fatal(err)
    }
}
```

`Decoder/Encoder` работает с `io.Reader`/`io.Writer` — может быть файл, HTTP body, network.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Custom MarshalJSON / UnmarshalJSON? Частая ошибка в реальном коде.

```go
type Color struct {
    R, G, B uint8
}

// Сериализация в "#RRGGBB"
func (c Color) MarshalJSON() ([]byte, error) {
    s := fmt.Sprintf(`"#%02x%02x%02x"`, c.R, c.G, c.B)
    return []byte(s), nil
}

func (c *Color) UnmarshalJSON(data []byte) error {
    s := strings.Trim(string(data), `"`)
    if len(s) != 7 || s[0] != '#' {
        return fmt.Errorf("invalid color: %s", s)
    }
    n, err := strconv.ParseUint(s[1:], 16, 32)
    if err != nil {
        return err
    }
    c.R = uint8(n >> 16)
    c.G = uint8(n >> 8)
    c.B = uint8(n)
    return nil
}
```

Используется для нестандартных типов (даты, IP-адреса, custom структуры).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) strings.Builder vs конкатенация? Частая ошибка в реальном коде.

```go
// Naive — O(n²) из-за перевыделения
s := ""
for i := 0; i < 1000; i++ {
    s += "hello" // создаёт новую строку каждый раз
}

// strings.Builder — O(n)
var sb strings.Builder
for i := 0; i < 1000; i++ {
    sb.WriteString("hello")
}
result := sb.String()
```

`strings.Builder` — оптимальный способ строить строку из множества частей. Внутри использует `[]byte` с амортизированными аллокациями.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. bytes.Buffer для эффективной работы с byte? Частая ошибка в реальном коде.

```go
var buf bytes.Buffer
buf.WriteString("hello")
buf.WriteByte(' ')
buf.WriteString("world")

fmt.Println(buf.String()) // "hello world"
fmt.Println(buf.Bytes())  // []byte("hello world")
```

`bytes.Buffer` реализует `io.Reader` и `io.Writer` — можно передавать в любые функции, ожидающие эти интерфейсы.

```go
// Запись JSON в buffer вместо файла
var buf bytes.Buffer
json.NewEncoder(&buf).Encode(myStruct)
log.Printf("encoded: %s", buf.String())
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. (!) time.Time, time.Duration? Частая ошибка в реальном коде.

```go
now := time.Now()
later := now.Add(5 * time.Second)
diff := later.Sub(now) // time.Duration

fmt.Println(diff)          // 5s
fmt.Println(diff.Seconds()) // 5.0

// Constants
const (
    Nanosecond  Duration = 1
    Microsecond          = 1000 * Nanosecond
    Millisecond          = 1000 * Microsecond
    Second               = 1000 * Millisecond
    Minute               = 60 * Second
    Hour                 = 60 * Minute
)

duration := 2*time.Hour + 30*time.Minute
```

`time.Duration` — `int64` число наносекунд. Не "datetime", а **интервал**.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Парсинг времени — особенности формата? Частая ошибка в реальном коде.

Go использует **уникальный** reference time: **Mon Jan 2 15:04:05 MST 2006** (memorize: 1 2 3 4 5 6).

```go
// Layout = "Mon Jan 2 15:04:05 MST 2006"
t, err := time.Parse("2006-01-02 15:04:05", "2025-04-18 14:30:00")

// Готовые форматы
time.RFC3339          // "2006-01-02T15:04:05Z07:00"
time.RFC1123          // "Mon, 02 Jan 2006 15:04:05 MST"

// Format
fmt.Println(t.Format(time.RFC3339)) // "2025-04-18T14:30:00Z"
fmt.Println(t.Format("2006/01/02"))  // "2025/04/18"
```

Это **уникально для Go** — другие языки используют `YYYY-MM-DD`. Go-команда хотела **самодокументируемый** формат.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Часовые пояса и UTC? Частая ошибка в реальном коде.

```go
loc, _ := time.LoadLocation("America/New_York")
t := time.Now().In(loc)

// Всегда работай в UTC
t := time.Now().UTC()

// Сравнение независимо от tz
t1 := time.Now().In(loc1)
t2 := time.Now().In(loc2)
fmt.Println(t1.Equal(t2)) // true — одна точка во времени
```

**Best practice:** хранить и обрабатывать в **UTC**, конвертировать в local только при отображении.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. time.NewTimer, time.Tick, time.After? Частая ошибка в реальном коде.

```go
// Timer — одноразовый
timer := time.NewTimer(5 * time.Second)
<-timer.C
fmt.Println("5 seconds passed")

timer.Stop() // отменить, если не нужно

// Ticker — повторяющийся
ticker := time.NewTicker(1 * time.Second)
defer ticker.Stop()
for t := range ticker.C {
    fmt.Println("tick at", t)
}

// time.After — синтаксический сахар (для select)
select {
case <-time.After(5 * time.Second):
    fmt.Println("timeout")
case <-done:
    fmt.Println("done")
}
```

**Подвох с time.After:** создаёт новый Timer, который не освобождается до истечения. В горячем коде → memory leak. Используй `NewTimer` с явным `Stop`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) context.Context — основные методы? Частая ошибка в реальном коде.

```go
import "context"

ctx := context.Background()  // root context
ctx := context.TODO()         // если не уверен какой использовать

// С отменой
ctx, cancel := context.WithCancel(ctx)
defer cancel()

// С таймаутом
ctx, cancel := context.WithTimeout(ctx, 5*time.Second)
defer cancel()

// С deadline
deadline := time.Now().Add(10*time.Second)
ctx, cancel := context.WithDeadline(ctx, deadline)

// С value (request-scoped)
ctx := context.WithValue(ctx, key, value)
val := ctx.Value(key)

// Использование
select {
case <-ctx.Done():
    return ctx.Err() // context.Canceled или context.DeadlineExceeded
case result := <-doWork():
    return result
}
```

Подробнее — в [Go Concurrency](go-concurrency-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. (!) errors.New, errors.Is, errors.As? Частая ошибка в реальном коде.

```go
import "errors"

// Создание ошибки
err1 := errors.New("not found")
err2 := fmt.Errorf("user %s not found", username)

// Wrapping (с Go 1.13)
err3 := fmt.Errorf("processing failed: %w", err1)

// Sentinel errors
var ErrNotFound = errors.New("not found")

if errors.Is(err, ErrNotFound) { /* ... */ }

// Custom error type
type ValidationError struct {
    Field string
}

func (v *ValidationError) Error() string {
    return fmt.Sprintf("invalid: %s", v.Field)
}

var verr *ValidationError
if errors.As(err, &verr) {
    fmt.Println(verr.Field)
}
```

`errors.Is` — сравнение по identity. `errors.As` — type assertion для error wrapping chain.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. errors.Join (Go 1.20+)? Частая ошибка в реальном коде.

```go
err1 := errors.New("first error")
err2 := errors.New("second error")

joined := errors.Join(err1, err2)
// Output: "first error\nsecond error"

errors.Is(joined, err1) // true
errors.Is(joined, err2) // true
```

Полезно для **множественных ошибок** (валидация, batch operations).

```go
var errs []error
for _, item := range items {
    if err := validate(item); err != nil {
        errs = append(errs, err)
    }
}
if len(errs) > 0 {
    return errors.Join(errs...)
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) log/slog (structured logging) — что это? Частая ошибка в реальном коде.

С Go 1.21 — встроенный **structured logger** в стандарте.

```go
import "log/slog"

slog.Info("user logged in", "user_id", 42, "ip", "1.2.3.4")
// Output: time=... level=INFO msg="user logged in" user_id=42 ip=1.2.3.4

// JSON handler
logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))
slog.SetDefault(logger)
slog.Info("event", slog.Group("user", slog.Int("id", 42), slog.String("name", "Alice")))
// {"time":"...","level":"INFO","msg":"event","user":{"id":42,"name":"Alice"}}

// С контекстом
slog.InfoContext(ctx, "query executed", "duration", time.Since(start))
```

Поддерживает:
- Attribute groups
- Custom handlers (text, JSON, custom)
- Levels (Debug, Info, Warn, Error)
- Source info (file, line)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. log vs slog vs zap vs logrus? Частая ошибка в реальном коде.

| Library | Стиль | Performance | Стандарт |
|---------|-------|-------------|----------|
| `log` | Plain text | Норм | Да (с 2009) |
| `log/slog` | Structured | Хороший | **Да** (Go 1.21+) |
| `zap` (Uber) | Structured | **Лучший** | Нет |
| `logrus` | Structured | Хороший | Нет |
| `zerolog` | Structured (zero allocations) | Очень хороший | Нет |

`log/slog` — **новый стандарт**. Для большинства apps достаточно. Если нужна максимальная perf — `zap` или `zerolog`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. (!) Как работает database/sql? Частая ошибка в реальном коде.

```go
import (
    "database/sql"
    _ "github.com/lib/pq" // PostgreSQL driver
)

db, err := sql.Open("postgres", "host=localhost user=...")
if err != nil { log.Fatal(err) }
defer db.Close()

// Один результат
var name string
err = db.QueryRow("SELECT name FROM users WHERE id = $1", 1).Scan(&name)

// Множество
rows, err := db.Query("SELECT id, name FROM users")
defer rows.Close()
for rows.Next() {
    var id int
    var name string
    rows.Scan(&id, &name)
    fmt.Println(id, name)
}

// Insert/Update
result, err := db.Exec("INSERT INTO users (name) VALUES ($1)", "Alice")
id, _ := result.LastInsertId()
n, _ := result.RowsAffected()
```

`database/sql` — **абстракция**. Driver (lib/pq, mysql, sqlite3) — реализация.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. Connection pool в Go? Частая ошибка в реальном коде.

`database/sql` имеет встроенный pool:

```go
db.SetMaxOpenConns(25)            // макс открытых connections
db.SetMaxIdleConns(5)             // idle connections в пуле
db.SetConnMaxLifetime(5 * time.Minute) // время жизни connection
db.SetConnMaxIdleTime(time.Minute)     // макс idle время
```

`db` — это **handle**, не connection. Connections автоматически берутся из пула.

**Подводный камень:** не закрывать `Rows` → connection не возвращается в pool → exhaustion.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. (!) Prepared statements? Частая ошибка в реальном коде.

```go
stmt, err := db.Prepare("SELECT name FROM users WHERE id = $1")
if err != nil { log.Fatal(err) }
defer stmt.Close()

for _, id := range []int{1, 2, 3} {
    var name string
    stmt.QueryRow(id).Scan(&name)
}
```

**Преимущества:**
- Защита от SQL injection
- Однократная компиляция запроса
- Быстрее для повторяющихся запросов

В `db.Query()`/`db.Exec()` с параметрами Go **сам** использует prepared statements внутренне. Так что обычно явный `Prepare` не нужен.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Transactions, deferred Rollback? Частая ошибка в реальном коде.

```go
tx, err := db.BeginTx(ctx, nil)
if err != nil { return err }

defer tx.Rollback() // safety net — если уже Commit, Rollback no-op

_, err = tx.Exec("INSERT INTO ...")
if err != nil { return err }

_, err = tx.Exec("UPDATE ...")
if err != nil { return err }

return tx.Commit()
```

**Идиома:** `defer tx.Rollback()` сразу после `Begin`. Если Commit прошёл — Rollback ничего не сделает. Если return из-за ошибки — Rollback откатит.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. (!) io.Reader, io.Writer интерфейсы? Частая ошибка в реальном коде.

```go
type Reader interface {
    Read(p []byte) (n int, err error)
}

type Writer interface {
    Write(p []byte) (n int, err error)
}
```

**Самые важные** интерфейсы в стандартной библиотеке. Реализованы:
- `os.File` — оба
- `bytes.Buffer` — оба
- `strings.Reader`
- `http.Response.Body` — Reader
- `bufio.Reader/Writer` — буферизованные

```go
io.Copy(dst, src)    // copy всё
io.ReadAll(r)         // прочесть всё в []byte
io.Pipe()             // create pipe
```

Composability — можно соединять `Reader` и `Writer` цепочкой (gzip, tee, multi).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. ioutil deprecation (Go 1.16+)? Частая ошибка в реальном коде.

`io/ioutil` deprecated с Go 1.16. Замены:

| Старое | Новое |
|--------|-------|
| `ioutil.ReadFile` | `os.ReadFile` |
| `ioutil.WriteFile` | `os.WriteFile` |
| `ioutil.ReadAll` | `io.ReadAll` |
| `ioutil.TempFile` | `os.CreateTemp` |
| `ioutil.TempDir` | `os.MkdirTemp` |
| `ioutil.NopCloser` | `io.NopCloser` |

Старые работают, но deprecation warning. В новом коде — использовать новое.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. (!) //go:embed для статических файлов? Частая ошибка в реальном коде.

С Go 1.16 — встраивание файлов в binary:

```go
import "embed"

//go:embed templates/*.html
var templates embed.FS

//go:embed config.yaml
var configData []byte

//go:embed static/index.html
var indexHTML string

func main() {
    // FS интерфейс
    files, _ := fs.ReadDir(templates, "templates")
    for _, f := range files {
        fmt.Println(f.Name())
    }

    // Сразу как HTTP handler
    http.Handle("/static/", http.FileServer(http.FS(staticFS)))
}
```

Полезно для:
- Single-binary деплои (нет нужды в external файлах)
- Templates, миграций, конфигов
- HTTP static files в одном исполняемом


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q29. sort.Slice vs sort.Sort? Частая ошибка в реальном коде.

```go
// sort.Slice — короткий, использует closure
people := []Person{{...}, {...}}
sort.Slice(people, func(i, j int) bool {
    return people[i].Age < people[j].Age
})

// sort.Sort — нужен sort.Interface (3 метода)
type ByAge []Person
func (a ByAge) Len() int           { return len(a) }
func (a ByAge) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a ByAge) Less(i, j int) bool { return a[i].Age < a[j].Age }

sort.Sort(ByAge(people))
```

`sort.Slice` — проще, но чуть медленнее (overhead closure). `sort.Sort` — старый, для library types.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q30. (!) slices пакет в Go 1.21+? Частая ошибка в реальном коде.

```go
import "slices"

slices.Sort([]int{3, 1, 2})              // в порядке возрастания
slices.Reverse([]int{1, 2, 3})            // [3, 2, 1]
slices.Contains([]string{"a", "b"}, "a") // true
slices.Index([]int{10, 20, 30}, 20)      // 1
slices.Min([]int{3, 1, 4, 1, 5})         // 1
slices.Max(...)
slices.Concat(s1, s2)
slices.Equal(s1, s2)
slices.Clone(s)

// Сортировка с custom функцией
slices.SortFunc(people, func(a, b Person) int {
    return cmp.Compare(a.Age, b.Age)
})
```

`slices` — **generic** пакет. Тип-безопасно, без рефлексии.

Аналогично `maps`:

```go
import "maps"
maps.Keys(m)        // возвращает iter.Seq (с Go 1.23)
maps.Values(m)
maps.Equal(m1, m2)
maps.Clone(m)
```

---

## See also

- [Go (базовый)](go-interview.md) — основы языка
- [Go Concurrency](go-concurrency-interview.md) — context, sync
- [Go Memory & GC](go-memory-gc-interview.md) — pprof
- [Go Generics](go-generics-interview.md) — slices/maps пакеты
- [Go Testing](go-testing-interview.md) — testing пакет
- [HTTP & REST](../../api/http-rest-interview.md) — net/http
- [PostgreSQL](../../databases/postgresql-interview.md) — database/sql + lib/pq
- [Logging](../../logging/logging-interview.md) — slog vs zap vs zerolog
- [Микросервисы](../../architecture/microservices-interview.md) — Go использует stdlib для большинства задач


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Go Concurrency](go-concurrency-interview.md) Частая ошибка в реальном коде.
- [Go Generics](go-generics-interview.md)
- [Go](go-interview.md)
- [Go Memory и GC](go-memory-gc-interview.md)
- [Go Modules](go-modules-interview.md)
- [Go Testing](go-testing-interview.md)
