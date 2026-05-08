---
title: "Вопросы на собеседовании: Go Standard Library"
description: "Стандартная библиотека Go: net/http, encoding/json, io/ioutil, strings, time, context, log/slog, database/sql, embed, sort, errors"
tags:
  - interview
  - programming-languages
  - go-stdlib-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Go Standard Library"
  - "Go stdlib interview"
  - "Go standard library interview"
prerequisites: []
next: []
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
> - [x] net/http достаточно для production без фреймворка; http.HandleFunc регистрирует handler; ListenAndServe запускает сервер | ✓ ПРИМЕНЯТЬ: simple services без сложного роутинга 📋 ПРАВИЛО: net/http = production-ready stdlib; handler=(w ResponseWriter, r *Request) 🔗 См. Q2
> - [ ] Для production нужен фреймворк (Gin, Fiber); net/http слишком низкоуровневый | ❌ ПОСЛЕДСТВИЕ: Docker, Kubernetes, многие крупные Go сервисы используют net/http без фреймворка; ненужная зависимость от третьесторонних пакетов
> - [ ] http.ListenAndServe блокирует goroutine; нужно запускать в go goroutine() | ❌ ПОСЛЕДСТВИЕ: ListenAndServe блокирует main goroutine — это правильное поведение для main(); запуск в goroutine без WaitGroup завершит main немедленно
> - [ ] net/http handler должен явно вызывать w.WriteHeader(200); иначе клиент получит 500 | ❌ ПОСЛЕДСТВИЕ: первый вызов w.Write() автоматически вызывает WriteHeader(200) если не было явного WriteHeader; явный 200 не нужен → лишний код

## Q2. (!) ServeMux и роутинг?

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
> - [ ] http.DefaultServeMux регистрируется через http.HandleFunc без создания нового mux; лучше использовать в production | ❌ ПОСЛЕДСТВИЕ: DefaultServeMux — глобальный; любой импортированный пакет может в него добавить handler (включая pprof) → случайные route conflicts в production
> - [ ] ServeMux различает пути по HTTP методам; "/users" с GET и POST — разные routes | ❌ ПОСЛЕДСТВИЕ: до Go 1.22 ServeMux не различал HTTP методы; один и тот же handler обрабатывал все методы; разграничение нужно вручную → неожиданный POST на read-only endpoint
> - [x] NewServeMux создаёт изолированный router; с Go 1.22 поддерживает метод+путь ("GET /users/{id}"); до 1.22 нужны сторонние роутеры | ✓ ПРИМЕНЯТЬ: новый проект на Go 1.22+ — stdlib mux достаточен 📋 ПРАВИЛО: NewServeMux вместо DefaultServeMux для безопасности; trailing slash — subtree pattern 🔗 См. Q3
> - [ ] Trailing slash в "/users/" и "/users" — одинаковые паттерны | ❌ ПОСЛЕДСТВИЕ: "/users" матчит только точный путь; "/users/" матчит всё что начинается с "/users/"; путаница → GET /users/123 не матчит "/users" pattern

## Q3. (!) Middleware patterns в Go?

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
> - [x] Middleware = func(http.Handler) http.Handler; обёртывает handler добавляя cross-cutting concerns; композиция через вложенные вызовы | ✓ ПРИМЕНЯТЬ: logging, auth, CORS, rate limiting как middleware chain 📋 ПРАВИЛО: middleware = decorator pattern; порядок wrapping = порядок выполнения outside-in 🔗 См. Q2
> - [ ] Middleware в Go реализуется через аннотации/декораторы на функции | ❌ ПОСЛЕДСТВИЕ: Go не имеет аннотаций как Java/Spring; middleware — обычные функции высшего порядка; ожидание @Auth → неправильный подход к cross-cutting concerns
> - [ ] next.ServeHTTP должен вызываться в начале middleware; после — cleanup | ❌ ПОСЛЕДСТВИЕ: порядок next.ServeHTTP определяет логику; до next — pre-processing (auth check, logging start); после — post-processing (response transform, timing); вызов только в начале нарушит response logging
> - [ ] Каждый middleware должен создавать отдельную goroutine для параллельного выполнения | ❌ ПОСЛЕДСТВИЕ: middleware выполняется последовательно в той же goroutine что и handler; параллелизм в middleware не нужен и опасен (w http.ResponseWriter не thread-safe)

## Q4. (!) HTTP client с timeout?

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
> - [ ] http.DefaultClient безопасен для production с дефолтным 30-секундным timeout | ❌ ПОСЛЕДСТВИЕ: DefaultClient не имеет timeout; зависший сервер → goroutine leak навсегда; в production это приведёт к исчерпанию goroutines под нагрузкой
> - [ ] Каждый HTTP запрос должен создавать новый http.Client для изоляции | ❌ ПОСЛЕДСТВИЕ: http.Client thread-safe; создание нового Client на каждый запрос → потеря connection pool (MaxIdleConns) → много повторных TCP handshakes → деградация latency
> - [x] http.DefaultClient не имеет timeout → goroutine leak при зависшем сервере; создавать http.Client с Timeout и настроенным Transport один раз и переиспользовать | ✓ ПРИМЕНЯТЬ: один client.Client как пакетная переменная; NewRequestWithContext с ctx 📋 ПРАВИЛО: no DefaultClient in prod; Timeout + Transport pool = правильный client 🔗 См. Q5
> - [ ] resp.Body.Close() не нужен если io.ReadAll прочитал всё тело | ❌ ПОСЛЕДСТВИЕ: Body.Close() обязателен всегда; без него connection не возвращается в pool → exhaustion; ReadAll читает данные, но не закрывает Body

## Q5. Graceful shutdown HTTP сервера?

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
> - [x] srv.Shutdown(ctx) останавливает приём новых соединений и ждёт завершения active requests до timeout; сигнал через signal.Notify → graceful в Kubernetes | ✓ ПРИМЕНЯТЬ: SIGTERM handler для Kubernetes SIGTERM grace period 📋 ПРАВИЛО: ListenAndServe в goroutine; Shutdown в main после SIGTERM; timeout context 🔗 См. Q4
> - [ ] os.Exit(0) после закрытия HTTP listener — достаточный graceful shutdown | ❌ ПОСЛЕДСТВИЕ: os.Exit немедленно завершает процесс; active requests обрываются; клиенты получают connection reset вместо нормального ответа → data loss и 502 у клиентов
> - [ ] srv.Close() и srv.Shutdown() эквивалентны | ❌ ПОСЛЕДСТВИЕ: Close немедленно закрывает все connections без ожидания; Shutdown ждёт active requests до завершения; в production для zero-downtime deploy — только Shutdown
> - [ ] SIGTERM обрабатывается Go runtime автоматически без signal.Notify | ❌ ПОСЛЕДСТВИЕ: без signal.Notify SIGTERM завершает процесс немедленно через OS default behavior; graceful shutdown требует явной регистрации через signal.Notify

## Q6. (!) Marshal и Unmarshal?

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
> - [ ] json.Unmarshal возвращает ошибку при наличии неизвестных JSON-полей (strict mode) | ❌ ПОСЛЕДСТВИЕ: Go по умолчанию игнорирует неизвестные поля; для strict — decoder.DisallowUnknownFields(); ожидание строгого режима → пропущенные опечатки в JSON ключах
> - [ ] Lowercase поля struct сериализуются с lowercase именем в JSON | ❌ ПОСЛЕДСТВИЕ: lowercase поля вообще не сериализуются (unexported); они невидимы для encoding/json → silent data loss без ошибки компиляции или runtime
> - [x] Только exported (uppercase) поля сериализуются; Unmarshal принимает pointer; неизвестные JSON поля игнорируются по умолчанию | ✓ ПРИМЕНЯТЬ: все JSON-поля struct делать uppercase; json tags для mapping 📋 ПРАВИЛО: lowercase = invisible to json; pointer for Unmarshal; tags for key mapping 🔗 См. Q7
> - [ ] json.Marshal автоматически добавляет все поля включая nil pointer | ❌ ПОСЛЕДСТВИЕ: nil pointer на struct → null в JSON (не ошибка); поля типа string со значением "" → "" (не omitted); omitempty нужен для пропуска zero values

## Q7. (!) Struct tags для JSON?

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
> - [x] omitempty пропускает zero values (0, false, "", nil, empty slice); `-` исключает поле полностью; `,string` конвертирует число в JSON-строку для JS-совместимости | ✓ ПРИМЕНЯТЬ: Password `json:"-"` обязательно; omitempty для nullable fields 📋 ПРАВИЛО: `-` = never serialize; omitempty = skip zero; `,string` = quote numbers for JS 🔗 См. Q6
> - [ ] omitempty пропускает только nil pointer; 0 и false всё равно сериализуются | ❌ ПОСЛЕДСТВИЕ: omitempty пропускает все zero values включая 0, false, ""; ожидание только nil-skip → неправильное поведение для numeric/bool полей
> - [ ] `json:"-"` делает поле write-only — Unmarshal читает, Marshal не включает | ❌ ПОСЛЕДСТВИЕ: `json:"-"` исключает поле из ОБОИХ направлений (marshal и unmarshal); для write-only нужны кастомные методы MarshalJSON/UnmarshalJSON
> - [ ] Struct tags проверяются компилятором; неправильный тег вызывает compile error | ❌ ПОСЛЕДСТВИЕ: struct tags — строки без статической проверки компилятором; опечатки обнаруживаются только в runtime; инструменты типа go vet могут помочь, но не компилятор

## Q8. (!) json.RawMessage — для отложенного парсинга?

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
> - [ ] json.RawMessage — для binary данных (base64 encoded bytes) внутри JSON | ❌ ПОСЛЕДСТВИЕ: RawMessage хранит raw JSON фрагмент (не base64); для binary данных — []byte с base64 тегом; использование RawMessage для base64 → double-encoding
> - [x] json.RawMessage сохраняет raw JSON без парсинга; полезно для polymorphic events с type-based dispatch | ✓ ПРИМЕНЯТЬ: event routing по type полю; передача JSON payload дальше без трансформации 📋 ПРАВИЛО: RawMessage = defer parsing; полезен для discriminated unions и proxy-сценариев 🔗 См. Q9
> - [ ] json.RawMessage автоматически определяет конкретный тип и десериализует | ❌ ПОСЛЕДСТВИЕ: RawMessage — просто []byte с raw JSON; Go не угадывает тип; dispatch нужно делать вручную (switch по type полю или reflection)
> - [ ] json.RawMessage безопаснее чем map[string]interface{} для unknown JSON | ❌ ПОСЛЕДСТВИЕ: оба подхода небезопасны без валидации; RawMessage эффективнее для дальнейшей передачи, map[string]interface{} — для доступа к произвольным полям; "безопаснее" не корректное сравнение

## Q9. Streaming JSON (Decoder/Encoder)?

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
> - [x] json.NewDecoder/Encoder работает streaming через io.Reader/Writer; эффективен для больших данных без буферизации всего в memory | ✓ ПРИМЕНЯТЬ: HTTP response body большого размера; NDJSON (newline-delimited JSON) 📋 ПРАВИЛО: Decoder = streaming read; Marshal = all-in-memory; выбор зависит от size 🔗 См. Q6
> - [ ] json.Decoder/Encoder быстрее json.Marshal/Unmarshal для любых данных | ❌ ПОСЛЕДСТВИЕ: для малых данных Marshal/Unmarshal проще и сопоставим по скорости; Decoder выигрывает только при больших/streaming данных; ненужная сложность для small payloads
> - [ ] json.NewDecoder читает весь io.Reader в память до начала декодирования | ❌ ПОСЛЕДСТВИЕ: Decoder читает по мере необходимости (streaming); буферизация не вся-в-память; это ключевое преимущество над json.Unmarshal(io.ReadAll(...))
> - [ ] json.Encoder.Encode добавляет запятую между объектами для valid JSON array | ❌ ПОСЛЕДСТВИЕ: Encode добавляет только newline после каждого объекта; для JSON array нужно ручное форматирование с "[", "]" и запятыми; по умолчанию производит NDJSON формат

## Q10. Custom MarshalJSON / UnmarshalJSON?

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
> - [x] MarshalJSON() ([]byte, error) на value receiver; UnmarshalJSON([]byte) error на pointer receiver; кастомный формат сериализации | ✓ ПРИМЕНЯТЬ: нестандартные форматы дат, IP, UUID; type-safe serialization 📋 ПРАВИЛО: MarshalJSON=value receiver; UnmarshalJSON=pointer receiver (нужна мутация) 🔗 См. Q7
> - [ ] Custom serialization требует реализации полного Marshaler интерфейса из encoding пакета | ❌ ПОСЛЕДСТВИЕ: encoding/json ищет конкретно MarshalJSON/UnmarshalJSON методы; нет отдельного "Marshaler интерфейса" который нужно явно реализовывать → правильные методы работают автоматически
> - [ ] UnmarshalJSON на value receiver тоже работает для изменения struct | ❌ ПОСЛЕДСТВИЕ: value receiver получает копию → изменения не видны вызывающему; UnmarshalJSON ДОЛЖЕН быть pointer receiver; иначе результаты парсинга теряются
> - [ ] MarshalJSON вызывается рекурсивно для всех вложенных полей автоматически | ❌ ПОСЛЕДСТВИЕ: при кастомном MarshalJSON он полностью контролирует сериализацию; вложенные поля сериализуются только если явно вызвать json.Marshal(field) внутри

## Q11. (!) strings.Builder vs конкатенация?

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
> - [ ] strings.Builder и += конкатенация имеют одинаковую производительность для коротких строк | ❌ ПОСЛЕДСТВИЕ: даже для 10 итераций += создаёт N new strings; Builder с Grow(N*len) — одна аллокация; в tight loop разница ощутима
> - [x] += конкатенация O(n²) — каждый раз новая string; strings.Builder O(n) с амортизированными аллокациями через внутренний []byte | ✓ ПРИМЕНЯТЬ: strings.Builder для любого loop с конкатенацией; Grow(expected) для pre-alloc 📋 ПРАВИЛО: += in loop = O(n²) allocations; Builder = O(n); для fixed parts — fmt.Sprintf 🔗 См. Q12
> - [ ] fmt.Sprintf всегда быстрее strings.Builder из-за оптимизаций компилятора | ❌ ПОСЛЕДСТВИЕ: fmt.Sprintf медленнее для чистой конкатенации — reflection overhead; Builder быстрее для строк без форматирования; Sprintf лучше когда нужен форматированный вывод
> - [ ] bytes.Buffer всегда лучше strings.Builder для строк | ❌ ПОСЛЕДСТВИЕ: strings.Builder специализирован для строк и немного быстрее bytes.Buffer (нет Write([]byte) overhead); для mixed string/bytes работы — bytes.Buffer

## Q12. bytes.Buffer для эффективной работы с byte?

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
> - [x] bytes.Buffer реализует io.Reader и io.Writer; удобен как промежуточный буфер для передачи в функции принимающие интерфейсы | ✓ ПРИМЕНЯТЬ: тест-mock для io.Writer; промежуточный буфер для json/gzip/тд 📋 ПРАВИЛО: Buffer = dual Reader+Writer; подходит для pipeline через io interfaces 🔗 См. Q26
> - [ ] bytes.Buffer thread-safe; можно использовать из нескольких goroutines без mutex | ❌ ПОСЛЕДСТВИЕ: bytes.Buffer НЕ thread-safe; concurrent writes → data race; нужен sync.Mutex или bytes.Buffer per goroutine
> - [ ] bytes.Buffer автоматически освобождает память после Read | ❌ ПОСЛЕДСТВИЕ: bytes.Buffer накапливает данные; Read продвигает read offset но не освобождает память; для освобождения нужен Reset(); без Reset memory растёт
> - [ ] strings.Builder и bytes.Buffer — одинаковые структуры с разными именами | ❌ ПОСЛЕДСТВИЕ: strings.Builder — write-only (нет Read); bytes.Buffer — read+write; Builder.String() не перемещает offset; оба используют []byte внутри, но разные use cases

## Q13. (!) time.Time, time.Duration?

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
> - [ ] time.Duration — число миллисекунд типа int64 | ❌ ПОСЛЕДСТВИЕ: Duration — наносекунды; умножение duration := 5 * 1000 даёт 5 микросекунд не 5 секунд; всегда использовать time.Second, time.Millisecond константы
> - [ ] time.Time сравнивается через == для равенства | ❌ ПОСЛЕДСТВИЕ: time.Time содержит location; два Time могут представлять один момент в разных timezone → == false; использовать t1.Equal(t2) для корректного сравнения
> - [x] time.Duration = int64 наносекунд (интервал); time.Time = момент времени; Add(duration) и Sub возвращают duration | ✓ ПРИМЕНЯТЬ: 5*time.Second, 200*time.Millisecond для Duration; Equal() для сравнения 📋 ПРАВИЛО: Duration=nanoseconds; Time.Equal() не ==; Add=shift; Sub=diff 🔗 См. Q14
> - [ ] time.Now().UnixMilli() возвращает duration в миллисекундах | ❌ ПОСЛЕДСТВИЕ: UnixMilli возвращает Unix timestamp в ms (int64), не duration; Duration.Milliseconds() возвращает duration в ms; путаница timestamp/duration

## Q14. Парсинг времени — особенности формата?

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
> - [ ] Go использует "YYYY-MM-DD HH:MM:SS" формат как большинство языков | ❌ ПОСЛЕДСТВИЕ: YYYY/MM/DD не работает в Go; reference time 2006-01-02 15:04:05; использование Y/M/D → parse ошибка или неправильный результат
> - [ ] 2006 в Go layout означает 4-значный год; 01 — месяц; 02 — день (мнемоника необязательна) | ❌ ПОСЛЕДСТВИЕ: частично верно, но мнемоника критична: 1 2 3 4 5 6 = month day hour min sec year; без мнемоники 15 (3pm) vs 03 (3am) путаются → неправильный формат часов
> - [x] Go использует reference time 01/02 03:04:05PM '06 -0700 (Jan 2 15:04:05 2006) как шаблон; не YYYY/MM/DD | ✓ ПРИМЕНЯТЬ: time.RFC3339 для API; кастомный layout через reference time 📋 ПРАВИЛО: layout = конкретные значения reference time; 2006=year, 01=month, 02=day, 15=hour24 🔗 См. Q15
> - [ ] time.Parse("2006-01-02", s) автоматически определяет timezone из строки | ❌ ПОСЛЕДСТВИЕ: без timezone в layout → UTC; с timezone нужен layout "2006-01-02T15:04:05Z07:00"; неправильный timezone → off-by-hours ошибки

## Q15. Часовые пояса и UTC?

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
> - [ ] time.Now() всегда возвращает UTC время | ❌ ПОСЛЕДСТВИЕ: time.Now() возвращает локальное время сервера; нужен time.Now().UTC() для UTC; при деплое на сервер с другим timezone → несогласованные timestamps
> - [ ] Сравнение двух time.Time через == корректно работает для разных timezone | ❌ ПОСЛЕДСТВИЕ: == сравнивает wall clock + ext + loc; два момента одного времени в разных tz → == false; Equal(t) сравнивает сам момент времени независимо от tz
> - [x] Хранить в UTC (time.Now().UTC()); конвертировать в local только при отображении; Equal() для сравнения независимо от timezone | ✓ ПРИМЕНЯТЬ: DB timestamps в UTC; .In(userLocation) только для UI 📋 ПРАВИЛО: store=UTC; display=local; compare=Equal() not == 🔗 См. Q13
> - [ ] time.LoadLocation блокирующая операция; нужно кешировать location в sync.Once | ❌ ПОСЛЕДСТВИЕ: LoadLocation читает timezone database из файла; при нормальных условиях — не blocking; кеширование уместно, но "блокирующая" характеристика преувеличена

## Q16. time.NewTimer, time.Tick, time.After?

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
> - [x] NewTimer — одноразовый, Stop для отмены; NewTicker — повторяющийся, defer Stop(); time.After — сахар для select, утечка памяти в loop | ✓ ПРИМЕНЯТЬ: NewTicker с defer Stop() для periodic tasks; NewTimer вместо time.After в hot paths 📋 ПРАВИЛО: time.After in loop = goroutine leak; NewTimer+Stop = safe 🔗 См. Q4
> - [ ] time.Tick безопасно использовать в long-running goroutines без Stop | ❌ ПОСЛЕДСТВИЕ: time.Tick создаёт Ticker который нельзя Stop; goroutine с time.Tick живёт вечно → goroutine leak; использовать NewTicker с defer Stop()
> - [ ] Timer.Stop() возвращает channel из которого можно читать сигнал остановки | ❌ ПОСЛЕДСТВИЕ: Stop() возвращает bool (остановлен/уже fired); после Stop channel может содержать pending value; if !timer.Stop() { <-timer.C } для drain
> - [ ] time.NewTimer и time.After имеют одинаковую производительность | ❌ ПОСЛЕДСТВИЕ: оба создают один Timer; но time.After в loop создаёт N таймеров не освобождая GC пока не fire; NewTimer можно Reset+Stop → существенная разница в memory при высокочастотном использовании

## Q17. (!) context.Context — основные методы?

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
> - [ ] context.TODO() и context.Background() идентичны; выбирать любой | ❌ ПОСЛЕДСТВИЕ: семантически разные; Background() — root для всей программы (main, init); TODO() — placeholder где context ещё не определён; статический анализ может выявлять TODO() для review
> - [ ] context.WithValue лучше использовать для передачи бизнес-данных (userID, orderID) | ❌ ПОСЛЕДСТВИЕ: context.Value — для request-scoped infrastructure данных (traceID, authToken); бизнес-данные в параметрах функции; ctx.Value требует type assertion и теряет типизацию
> - [x] context имеет Done() channel, Err() для причины отмены; WithCancel/Timeout/Deadline для создания; всегда defer cancel() | ✓ ПРИМЕНЯТЬ: select{case <-ctx.Done()} в любой долгой операции 📋 ПРАВИЛО: Done()=cancellation channel; Err()=reason; cancel() всегда через defer 🔗 См. Q4
> - [ ] ctx.Done() блокирует goroutine до отмены | ❌ ПОСЛЕДСТВИЕ: <-ctx.Done() — блокирующее чтение; ctx.Done() — возвращает channel; в select с другими case — не блокирует; неправильное использование Done() без select → потеря результатов операции

## Q18. (!) errors.New, errors.Is, errors.As?

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
> - [x] errors.Is проверяет identity в wrapping chain (%w); errors.As находит конкретный error type в chain; fmt.Errorf с %w создаёт wrapping | ✓ ПРИМЕНЯТЬ: sentinel errors + errors.Is; custom types + errors.As 📋 ПРАВИЛО: Is=value match through chain; As=type match through chain; %w=wrap for chain 🔗 См. Q19
> - [ ] err == ErrNotFound и errors.Is(err, ErrNotFound) — эквивалентны | ❌ ПОСЛЕДСТВИЕ: == сравнивает только верхний уровень; errors.Is() проходит весь unwrap chain; fmt.Errorf("wrap: %w", ErrNotFound) → == false, errors.Is → true
> - [ ] errors.As принимает interface{} и делает type cast | ❌ ПОСЛЕДСТВИЕ: errors.As принимает pointer to target type (**ValidationError); возвращает bool и заполняет target; простой type assertion.(type) не работает на wrapped errors
> - [ ] fmt.Errorf без %w создаёт wrapping | ❌ ПОСЛЕДСТВИЕ: fmt.Errorf без %w — новая ошибка без wrapping; с %w — wrapping сохраняет original в chain; errors.Is на non-wrapped → false даже для одинакового Error() string

## Q19. errors.Join (Go 1.20+)?

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
> - [x] errors.Join (Go 1.20+) объединяет несколько ошибок в одну; errors.Is проверяет каждую из joined | ✓ ПРИМЕНЯТЬ: batch validation с коллекцией всех ошибок 📋 ПРАВИЛО: Join=multi-error; Is/As работают на каждой из joined ошибок 🔗 См. Q18
> - [ ] errors.Join доступна с Go 1.13 как часть errors wrapping | ❌ ПОСЛЕДСТВИЕ: errors.Join добавлена в Go 1.20; до этого использовали multierr (Uber) или кастомные реализации; использование в коде с go 1.13 → compile error
> - [ ] errors.Join(err1, err2) — эквивалентно fmt.Errorf("%w: %w", err1, err2) | ❌ ПОСЛЕДСТВИЕ: fmt.Errorf с несколькими %w доступен с Go 1.20; Join и double-wrap имеют разную семантику и Error() output; Join разделяет через \n, Errorf через строку формата
> - [ ] errors.Is(errors.Join(err1, err2), err1) возвращает false | ❌ ПОСЛЕДСТВИЕ: errors.Is на joined error проверяет каждую из joined errors через Unwrap() []error; возвращает true для err1; ожидание false → пропуск конкретных ошибок в joined batch

## Q20. (!) log/slog (structured logging) — что это?

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
> - [ ] log/slog доступна с Go 1.13 как часть стандартного логгера | ❌ ПОСЛЕДСТВИЕ: slog добавлена в Go 1.21; до этого — только log пакет с plain text; использование slog в Go 1.13 → compile error
> - [ ] slog автоматически форматирует в JSON всегда | ❌ ПОСЛЕДСТВИЕ: default handler — text format; для JSON нужен slog.NewJSONHandler(os.Stdout, nil); без явной настройки → text output не пригодный для log aggregation
> - [x] log/slog (Go 1.21+) — стандартный structured logger; key-value attributes; JSON/text handlers; InfoContext для trace correlation | ✓ ПРИМЕНЯТЬ: новые проекты на Go 1.21+ — slog вместо logrus/zap; InfoContext для distributed tracing 📋 ПРАВИЛО: slog=stdlib structured logging; handler определяет format; slog.SetDefault для глобального 🔗 См. Q21
> - [ ] slog.Info принимает только строки как атрибуты; числа нужно конвертировать | ❌ ПОСЛЕДСТВИЕ: slog принимает любые значения как key-value pairs; slog.Int, slog.String, slog.Duration хелперы для типизированных attrs; автоматическая конвертация через fmt.Sprint

## Q21. log vs slog vs zap vs logrus?

| Library | Стиль | Performance | Стандарт |
|---------|-------|-------------|----------|
| `log` | Plain text | Норм | Да (с 2009) |
| `log/slog` | Structured | Хороший | **Да** (Go 1.21+) |
| `zap` (Uber) | Structured | **Лучший** | Нет |
| `logrus` | Structured | Хороший | Нет |
| `zerolog` | Structured (zero allocations) | Очень хороший | Нет |

`log/slog` — **новый стандарт**. Для большинства apps достаточно. Если нужна максимальная perf — `zap` или `zerolog`.


> [!mcq]
> - [ ] logrus — рекомендуемый стандарт для новых Go проектов | ❌ ПОСЛЕДСТВИЕ: logrus находится в maintenance mode (не активная разработка); для новых проектов рекомендуется slog (stdlib) или zap; ненужная зависимость от outdated library
> - [x] log/slog — новый stdlib стандарт (Go 1.21+); zap/zerolog быстрее при high-throughput; logrus в maintenance mode | ✓ ПРИМЕНЯТЬ: новый проект = slog; perf-critical logging = zap/zerolog 📋 ПРАВИЛО: slog=stdlib standard; zap=performance; logrus=legacy; zerolog=zero-alloc 🔗 См. Q20
> - [ ] Все structured loggers совместимы через общий интерфейс Logger | ❌ ПОСЛЕДСТВИЕ: нет стандартного Logger интерфейса в Go stdlib до slog; каждый logger имеет свой API; миграция между loggers требует изменений кода; slog.Handler — шаг к стандартизации
> - [ ] zap быстрее slog в 100 раз | ❌ ПОСЛЕДСТВИЕ: zap быстрее slog в 2-5x для zero-allocation API (zap.String vs "key", value); не 100x; для большинства apps разница незначительна относительно I/O latency

## Q22. (!) Как работает database/sql?

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
> - [ ] sql.Open немедленно создаёт connection к базе данных | ❌ ПОСЛЕДСТВИЕ: sql.Open не создаёт connection; только проверяет driver регистрацию; реальное connection — при первом запросе; для проверки connectivity — db.PingContext(ctx)
> - [x] database/sql — абстракция; driver импортируется blank (_); Open не коннектится сразу; rows.Close() обязателен для возврата connection в pool | ✓ ПРИМЕНЯТЬ: defer rows.Close() после Query; db.PingContext для healthcheck 📋 ПРАВИЛО: sql=interface; driver=impl via blank import; rows.Close=return connection 🔗 См. Q23
> - [ ] QueryRow возвращает ошибку только при отсутствии строк | ❌ ПОСЛЕДСТВИЕ: QueryRow возвращает *Row; ошибка доступна через Scan(); sql.ErrNoRows если строк нет; другие DB errors тоже возможны → нужно проверять err после Scan()
> - [ ] Параметры запроса нужно экранировать вручную для предотвращения SQL injection | ❌ ПОСЛЕДСТВИЕ: database/sql с параметрами ($1, ?) автоматически предотвращает injection через prepared statements; ручное экранирование — anti-pattern; fmt.Sprintf в query → SQL injection

## Q23. Connection pool в Go?

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
> - [x] database/sql имеет встроенный pool; db — handle не connection; незакрытые Rows держат connection → pool exhaustion | ✓ ПРИМЕНЯТЬ: defer rows.Close(); SetMaxOpenConns под нагрузку; мониторинг db.Stats() 📋 ПРАВИЛО: db=pool; Open≠connect; rows.Close=return to pool; MaxOpenConns limit 🔗 См. Q22
> - [ ] SetMaxOpenConns(0) означает отсутствие connections (отключённый pool) | ❌ ПОСЛЕДСТВИЕ: 0 означает unlimited connections; без лимита → DB может получить слишком много connections → postgres max_connections exceeded → connection refused
> - [ ] Каждый вызов db.Query создаёт новое connection; pool только для Transactions | ❌ ПОСЛЕДСТВИЕ: pool используется для всех операций (Query, Exec, QueryRow); не только транзакций; без pool каждый запрос создавал бы новое TCP соединение → огромный overhead
> - [ ] SetMaxIdleConns больше SetMaxOpenConns — допустимо | ❌ ПОСЛЕДСТВИЕ: MaxIdleConns автоматически ограничивается MaxOpenConns; установка MaxIdle > MaxOpen избыточна но не crash; реальный idle limit = min(MaxIdle, MaxOpen)

## Q24. (!) Prepared statements?

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
> - [ ] Явный Prepare обязателен для защиты от SQL injection | ❌ ПОСЛЕДСТВИЕ: db.Query($1, param) использует prepared statements внутренне; явный Prepare не нужен для injection protection; избыточный Prepare создаёт лишний round-trip к DB
> - [x] Prepared statements: однократная компиляция для многократного выполнения; db.Query с параметрами использует их автоматически; явный Prepare для hot queries | ✓ ПРИМЕНЯТЬ: явный stmt.Close() через defer; для не-repeated queries достаточно db.Query с params 📋 ПРАВИЛО: params=auto prepared internally; explicit Prepare=optimization for repeated queries 🔗 См. Q22
> - [ ] Prepared statements несовместимы с connection pool — stmt привязан к конкретному connection | ❌ ПОСЛЕДСТВИЕ: database/sql прозрачно re-prepares statement на другом connection при необходимости; это managed автоматически; явная привязка к connection не нужна
> - [ ] stmt.QueryRow автоматически освобождает statement после выполнения | ❌ ПОСЛЕДСТВИЕ: stmt.Close() нужно вызывать явно (через defer); без Close → prepared statement остаётся на сервере; при многих stmt без Close → server-side resource exhaustion

## Q25. Transactions, deferred Rollback?

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
> - [ ] tx.Rollback() после успешного Commit вызывает ошибку | ❌ ПОСЛЕДСТВИЕ: Rollback после Commit — no-op (возвращает sql.ErrTxDone); это намеренная идиома с defer tx.Rollback(); ожидание ошибки → избыточный if !committed check
> - [x] defer tx.Rollback() сразу после Begin — идиома; если Commit выполнен — Rollback no-op; иначе откатит при любом return | ✓ ПРИМЕНЯТЬ: BeginTx с ctx для timeout; defer Rollback как safety net; явный Commit в конце 📋 ПРАВИЛО: Begin+defer Rollback+Commit = safe transaction; Rollback after Commit=no-op 🔗 См. Q22
> - [ ] db.Exec в транзакции автоматически откатится при panic | ❌ ПОСЛЕДСТВИЕ: Go runtime не откатывает транзакции при panic; defer Rollback() откатит если defer выполнится перед panic propagation; для надёжности нужен recover в defer
> - [ ] BeginTx(ctx, nil) создаёт read-only транзакцию по умолчанию | ❌ ПОСЛЕДСТВИЕ: nil options = default (read-write, isolation level по умолчанию DB); для read-only — &sql.TxOptions{ReadOnly: true}; неправильный default → неожиданный отказ в write операции

## Q26. (!) io.Reader, io.Writer интерфейсы?

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
> - [x] io.Reader (Read) и io.Writer (Write) — fundamental interfaces; реализованы файлами, буферами, HTTP body; позволяют composable pipeline через io.Copy | ✓ ПРИМЕНЯТЬ: принимать io.Reader в функцию вместо []byte для streaming; io.TeeReader для logging 📋 ПРАВИЛО: Reader+Writer=composable pipeline; io.Copy=zero-alloc streaming; bufio для buffering 🔗 См. Q12
> - [ ] io.Reader гарантирует чтение ровно len(p) байт | ❌ ПОСЛЕДСТВИЕ: Read может вернуть n < len(p) без ошибки (partial read); нужно цикличное чтение или io.ReadFull; ожидание полного чтения → truncated data
> - [ ] http.Response.Body реализует io.ReadWriter | ❌ ПОСЛЕДСТВИЕ: Response.Body — только io.ReadCloser (Reader + Closer); записать в Body нельзя; для request body — использовать strings.NewReader или bytes.Buffer
> - [ ] bufio.Writer немедленно пишет в underlying Writer при каждом Write вызове | ❌ ПОСЛЕДСТВИЕ: bufio.Writer буферизует и пишет батчами; нужен явный Flush() для финального сброса; без Flush → потеря последних байт при завершении

## Q27. ioutil deprecation (Go 1.16+)?

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
> - [ ] ioutil удалён в Go 1.18; использование вызовет compile error | ❌ ПОСЛЕДСТВИЕ: ioutil deprecated но не удалён; существующий код продолжает компилироваться; удаление пакета нарушит обратную совместимость; Go придерживается совместимости
> - [x] io/ioutil deprecated с Go 1.16; замены в os и io пакетах; старый код работает но deprecated | ✓ ПРИМЕНЯТЬ: os.ReadFile, io.ReadAll, os.CreateTemp в новом коде 📋 ПРАВИЛО: ioutil→os/io; deprecated=works but avoid; go vet предупреждает 🔗 См. Q26
> - [ ] io.ReadAll и ioutil.ReadAll имеют разные сигнатуры и поведение | ❌ ПОСЛЕДСТВИЕ: io.ReadAll — прямое переименование ioutil.ReadAll с идентичной сигнатурой (r io.Reader) ([]byte, error); прямая замена без изменений логики
> - [ ] Deprecation ioutil не влияет на производительность; использование любого варианта одинаково | ❌ ПОСЛЕДСТВИЕ: функционально идентичны; но deprecated код — tech debt; IDE/linter предупреждения; накопление deprecated кода затрудняет будущие миграции

## Q28. (!) //go:embed для статических файлов?

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
> - [x] //go:embed встраивает файлы в binary при компиляции; embed.FS для директорий, []byte/string для файлов; single-binary deploy без внешних файлов | ✓ ПРИМЕНЯТЬ: SQL migrations, templates, frontend static files в binary 📋 ПРАВИЛО: directive ДО переменной; работает только с var на package level; паттерны поддерживают globbing 🔗 См. Q1
> - [ ] //go:embed обновляет embedded файлы при runtime без перекомпиляции | ❌ ПОСЛЕДСТВИЕ: embed встраивает при compile time; runtime изменения файлов НЕ отражаются; для hot-reload файлов нужно os.ReadFile; ожидание runtime update → stale content после деплоя
> - [ ] //go:embed поддерживает удалённые URL для загрузки при запуске | ❌ ПОСЛЕДСТВИЕ: embed работает только с локальными файлами относительно .go файла; никаких URL; для remote resources нужен HTTP client при старте
> - [ ] //go:embed увеличивает время запуска binary пропорционально размеру файлов | ❌ ПОСЛЕДСТВИЕ: embedded файлы загружаются при ссылке на переменную (lazy); сам бинарь больше, но startup time не увеличивается пропорционально; файлы в памяти сразу без I/O

## Q29. sort.Slice vs sort.Sort?

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
> - [ ] sort.Slice гарантирует stable sort (одинаковые элементы сохраняют порядок) | ❌ ПОСЛЕДСТВИЕ: sort.Slice — нестабильная сортировка; для stable нужен sort.SliceStable; использование sort.Slice когда нужен stable → непредсказуемый порядок равных элементов
> - [x] sort.Slice — closure-based, проще; sort.Sort — interface-based, нужен Len/Swap/Less; в Go 1.21+ предпочесть slices.Sort | ✓ ПРИМЕНЯТЬ: sort.Slice для одноразовой сортировки; slices.SortFunc для generic типизированного кода 📋 ПРАВИЛО: sort.Slice=simple; sort.Sort=reusable type; slices.SortFunc=modern generic 🔗 См. Q30
> - [ ] sort.Sort быстрее sort.Slice; всегда предпочитать sort.Sort | ❌ ПОСЛЕДСТВИЕ: sort.Sort может быть чуть быстрее из-за отсутствия closure overhead, но разница минимальна; sort.Slice — идиоматичнее для ad-hoc сортировок; premature optimization
> - [ ] sort.Slice не изменяет оригинальный slice; возвращает новый | ❌ ПОСЛЕДСТВИЕ: sort.Slice сортирует slice IN PLACE (изменяет оригинал); нет возврата нового slice; если нужна копия — clone перед sort

## Q30. (!) slices пакет в Go 1.21+?

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
> - [x] slices пакет (Go 1.21+) — generic, type-safe операции (Sort, Contains, Min, Max, Clone); заменяет sort.Slice для большинства случаев | ✓ ПРИМЕНЯТЬ: slices.Sort вместо sort.Slice; slices.Contains вместо range loop 📋 ПРАВИЛО: slices=generic stdlib; SortFunc с cmp.Compare; Go 1.21+ preferred API 🔗 См. Q29
> - [ ] slices.Sort работает для всех типов включая struct без компаратора | ❌ ПОСЛЕДСТВИЕ: slices.Sort работает только для ordered types (int, string, float); для struct нужен slices.SortFunc с кастомным компаратором → compile error на struct без constraints
> - [ ] slices пакет устарел с Go 1.22; заменён на go/slices | ❌ ПОСЛЕДСТВИЕ: нет пакета go/slices; slices — стандартный пакет Go 1.21+; актуален; deprecated утверждение неверно
> - [ ] maps.Keys возвращает []K срез ключей | ❌ ПОСЛЕДСТВИЕ: с Go 1.23 maps.Keys возвращает iter.Seq[K] (iterator, не slice); для среза нужен slices.Collect(maps.Keys(m)); ожидание []K → compile error с iter.Seq
