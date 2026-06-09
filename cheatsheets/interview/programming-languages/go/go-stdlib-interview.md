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
- [Q9. Потоковый JSON через Decoder/Encoder — зачем?](#q9-потоковый-json-через-decoderencoder--зачем)
- [Q10. Своя сериализация: MarshalJSON / UnmarshalJSON?](#q10-своя-сериализация-marshaljson--unmarshaljson)

**Strings и Bytes**
- [Q11. (!) strings.Builder vs конкатенация?](#q11--stringsbuilder-vs-конкатенация)
- [Q12. bytes.Buffer для эффективной работы с byte?](#q12-bytesbuffer-для-эффективной-работы-с-byte)

**Time**
- [Q13. (!) Чем различаются time.Time и time.Duration?](#q13--чем-различаются-timetime-и-timeduration)
- [Q14. Парсинг времени — особенности формата?](#q14-парсинг-времени--особенности-формата)
- [Q15. Часовые пояса и UTC?](#q15-часовые-пояса-и-utc)
- [Q16. Таймеры: time.NewTimer, time.Tick, time.After — в чём разница?](#q16-таймеры-timenewtimer-timetick-timeafter--в-чём-разница)

**Context**
- [Q17. (!) context.Context — основные методы?](#q17--contextcontext--основные-методы)

**Errors**
- [Q18. (!) Работа с ошибками: errors.New, errors.Is, errors.As?](#q18--работа-с-ошибками-errorsnew-errorsis-errorsas)
- [Q19. Объединение ошибок через errors.Join (Go 1.20+)?](#q19-объединение-ошибок-через-errorsjoin-go-120)

**Logging**
- [Q20. (!) log/slog (structured logging) — что это?](#q20--logslog-structured-logging--что-это)
- [Q21. Чем выбрать логгер: log vs slog vs zap vs logrus?](#q21-чем-выбрать-логгер-log-vs-slog-vs-zap-vs-logrus)

**database/sql**
- [Q22. (!) Как работает database/sql?](#q22--как-работает-databasesql)
- [Q23. Connection pool в Go?](#q23-connection-pool-в-go)
- [Q24. (!) Зачем нужны prepared statements?](#q24--зачем-нужны-prepared-statements)
- [Q25. Транзакции и идиома deferred Rollback?](#q25-транзакции-и-идиома-deferred-rollback)

**I/O**
- [Q26. (!) io.Reader, io.Writer интерфейсы?](#q26--ioreader-iowriter-интерфейсы)
- [Q27. Почему ioutil устарел (Go 1.16+)?](#q27-почему-ioutil-устарел-go-116)

**embed (Go 1.16+)**
- [Q28. (!) //go:embed для статических файлов?](#q28--goembed-для-статических-файлов)

**Сортировка**
- [Q29. Чем отличается sort.Slice от sort.Sort?](#q29-чем-отличается-sortslice-от-sortsort)
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

`net/http` из коробки даёт production-ready HTTP-сервер — без сторонних фреймворков. На нём построены Docker, Kubernetes и масса боевых сервисов.

Минимальный сервер — это две вещи:

- **Handler** — функция `func(w http.ResponseWriter, r *http.Request)`. Пишет ответ в `w`, читает запрос из `r`.
- **`ListenAndServe`** — блокирующий вызов: открывает сокет на адресе и обслуживает запросы, пока не получит ошибку.

`http.HandleFunc("/", ...)` регистрирует handler в **глобальном `DefaultServeMux`**, а `nil` вторым аргументом `ListenAndServe` означает «используй этот глобальный mux».

**Подводный камень:** в этой минимальной форме нет таймаутов чтения/записи — медленный клиент может занять соединение надолго (Slowloris). Для боевого кода создавай `&http.Server{}` явно и задавай `ReadTimeout`/`WriteTimeout` (см. Q5).

## Q2. (!) ServeMux и роутинг?

```go
mux := http.NewServeMux()
mux.HandleFunc("/users", listUsers)
mux.HandleFunc("/users/", getUser) // trailing slash важен

http.ListenAndServe(":8080", mux)
```

`http.ServeMux` — стандартный роутер (мультиплексор): сопоставляет путь запроса с зарегистрированным handler. Правила матчинга:

- Путь **без** завершающего слэша (`/users`) — точное совпадение.
- Путь **с** завершающим слэшем (`/users/`) — префикс: матчит `/users/`, `/users/42`, `/users/42/posts`. Поэтому `getUser` выше ловит всё под `/users/`.
- При коллизии выигрывает **самый длинный** совпавший паттерн.

С Go 1.22+ роутер стал заметно мощнее — появились метод и path-параметры прямо в паттерне:

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

Значение `{id}` читается внутри handler через `r.PathValue("id")`. Метод в начале паттерна (`GET`, `POST`) фильтрует по HTTP-методу — раньше это приходилось проверять вручную через `if r.Method != ...`.

**Итог:** до Go 1.22 для метод-роутинга и path-параметров брали сторонние роутеры (Chi, Gorilla mux, gin). Теперь для большинства REST-API хватает стандартного `ServeMux` — сторонние библиотеки нужны только ради middleware-цепочек, групп маршрутов и регулярок в пути.

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

Middleware в Go — это **функция, которая оборачивает один handler в другой**: принимает `http.Handler` и возвращает новый `http.Handler`. Внутри она делает свою работу (логирование, проверка токена) и решает, звать ли `next.ServeHTTP` дальше по цепочке.

Как это работает:

- Каждый middleware возвращает `http.HandlerFunc`, который выполняет код **до** и/или **после** вызова `next.ServeHTTP(w, r)`.
- Чтобы прервать запрос (как `Auth` при невалидном токене), middleware пишет ответ и делает `return`, **не вызывая** `next` — следующий handler просто не запускается.
- Композиция `Logging(Auth(handler))` строит «луковицу»: запрос проходит сквозь слои снаружи внутрь, ответ — изнутри наружу. Здесь `Logging` снаружи, поэтому замеряет полное время, включая работу `Auth`.

Никаких аннотаций и магии DI, как в Java/Spring, — всё на обычных функциях и интерфейсе. Цена за явность: длинную цепочку приходится собирать руками (или маленьким хелпером `Chain(mw...)`).

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

У `http.Client` таймаут **не задан по умолчанию**, и это главная ловушка. `client.Timeout` ограничивает время всего запроса целиком — от установки соединения до чтения тела ответа.

**Главный подводный камень:** `http.DefaultClient` (его использует `http.Get`/`http.Post`) идёт **без таймаута**. Если сервер на той стороне завис и держит соединение открытым, твой вызов `Do` повиснет навсегда, а вместе с ним — горутина, занимающая память и, возможно, слот в пуле. В проде всегда создавай `&http.Client{Timeout: ...}` вместо `DefaultClient`.

Зачем настраивать `Transport`:

- `MaxIdleConns` / `MaxIdleConnsPerHost` — сколько простаивающих TCP-соединений держать для переиспользования (keep-alive). Без этого под нагрузкой на один хост каждый запрос открывает новое соединение.
- `IdleConnTimeout` — через сколько закрывать простаивающее соединение.

**Рекомендация:** `http.Client` потокобезопасен и внутри держит пул соединений — создавай **один** экземпляр на приложение и переиспользуй. Новый клиент на каждый запрос убивает keep-alive и плодит соединения.

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

Graceful shutdown — это корректная остановка: перестать принимать новые запросы, но дать **уже выполняющимся** дойти до конца, а не рвать их посередине. Без этого деплой/масштабирование роняли бы запросы пользователей с ошибкой.

Как устроен код:

- Сервер запускается в **отдельной горутине**, потому что `ListenAndServe` блокирует. После штатного `Shutdown` он возвращает `http.ErrServerClosed` — это **не ошибка**, поэтому её отдельно отфильтровывают, чтобы не словить ложный `log.Fatal`.
- Главная горутина блокируется на `<-quit` и ждёт сигнала ОС. `signal.Notify` подписывается на `SIGINT` (Ctrl+C) и `SIGTERM` (его шлёт Kubernetes/Docker при остановке пода). Канал буферизованный (`make(..., 1)`), чтобы сигнал не потерялся, если он придёт до того, как мы начнём читать.
- `srv.Shutdown(ctx)` перестаёт принимать новые соединения и ждёт завершения активных запросов — но не дольше, чем разрешает `ctx` (здесь 10 секунд). Если запросы не успели — `Shutdown` вернёт ошибку, и можно завершиться принудительно.

**Эмпирическое правило:** таймаут shutdown должен укладываться в `terminationGracePeriodSeconds` пода, иначе оркестратор убьёт процесс раньше, чем тот успеет красиво закрыться.

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

`encoding/json` конвертирует между Go-значениями и JSON в обе стороны:

- **`json.Marshal(v)`** — Go-значение → `[]byte` с JSON.
- **`json.Unmarshal(data, &v)`** — JSON → Go. Принимает **указатель** на цель: иначе функции некуда писать результат, и данные не появятся.

Пакет работает через **рефлексию**: на старте читает теги и типы полей. Имя поля в JSON берётся из тега `json:"..."`, а при его отсутствии — из имени поля как есть.

**Главный подводный камень — приватные поля.** Marshal/Unmarshal видят только **экспортируемые** поля (с большой буквы). Поле с маленькой буквы недоступно через рефлексию извне пакета, поэтому в JSON оно молча не попадёт и из JSON не заполнится — без всякой ошибки.

```go
type User struct {
    name string  // НЕ marshal'ит — lowercase
    Name string  // marshal'ит
}
```

Типичная ловушка новичка: распарсил JSON в структуру с lowercase-полями, получил все нули и долго ищет, почему «json не работает».

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

Struct-теги — это управление сериализацией прямо в объявлении поля: первый элемент после `json:` — имя ключа в JSON, дальше через запятую идут опции.

**Опции:**

- `omitempty` — не включать поле, если значение равно zero value (`false`, `0`, `""`, `nil`, пустой slice/map). Так из ответа убирают пустые поля.
- `-` — поле никогда не сериализуется. Классика для `Password`: чтобы случайно не утёк в JSON-ответ.
- `,string` — обернуть число в строку. Нужно для JavaScript-клиентов: там number — это float64, и `int64` больше 2^53 теряет точность. Передача его строкой сохраняет все разряды.

```go
type Money struct {
    Amount int64 `json:"amount,string"` // {"amount": "12345"}
}
```

**Подводный камень `omitempty`:** он смотрит на zero value, а не на «поле задано или нет». У `bool` нельзя отличить «не пришло» от «пришло `false`» — оба дают пропуск поля. Если эта разница важна, бери указатель (`*bool`): тогда «не задано» — это `nil`, а `false` — реальное значение.

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

`json.RawMessage` — это `[]byte`, который при Unmarshal **не парсится**, а сохраняется как сырой кусок JSON. Декодирование откладывается до момента, когда станет понятно, во что именно парсить.

Зачем это нужно: в примере форма `Payload` зависит от `Type`. Распарсить его сразу нельзя — мы ещё не знаем, это `UserCreatedPayload` или `OrderPayload`. Поэтому сначала читаем `Type` и сырой `Payload`, а потом по `switch` доделываем второй проход в конкретный тип.

**Сценарии применения:**

- **Разнородные события/сообщения** — общий конверт, разное тело (как выше).
- **Прокси и роутеры** — пробросить payload дальше как есть, не зная и не трогая его структуру.
- **Производительность** — пропустить парсинг ветки, которая в этом запросе не понадобится.

## Q9. Потоковый JSON через Decoder/Encoder — зачем?

`Decoder`/`Encoder` обрабатывают JSON **потоком**, в отличие от `Marshal`/`Unmarshal`, которые держат весь документ в памяти как `[]byte`. Когда это важно:

- **Большие данные** — не грузить в память файл/ответ целиком, а читать по одному элементу.
- **NDJSON / поток сообщений** — в цикле `dec.Decode(&item)` вычитывает по одному JSON-объекту за раз, пока не вернёт `io.EOF`.

Ключевое отличие — они работают не с готовым байтовым буфером, а напрямую с `io.Reader`/`io.Writer`:

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

Источник/приёмник — любой `io.Reader`/`io.Writer`: файл, тело HTTP-запроса, сетевое соединение. Поэтому в веб-хендлере декодируют прямо из `r.Body` и кодируют прямо в `w`, минуя промежуточный `[]byte`.

**Нюанс:** `Encoder.Encode` дописывает в конце каждого вызова перевод строки — это удобно для NDJSON, но если ждёшь ровно один компактный объект, помни про этот завершающий `\n`.

## Q10. Своя сериализация: MarshalJSON / UnmarshalJSON?

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

Реализовав интерфейсы `json.Marshaler` (`MarshalJSON`) и `json.Unmarshaler` (`UnmarshalJSON`), тип сам решает, как выглядеть в JSON. Когда `encoding/json` встречает такой тип, он вызывает эти методы вместо стандартной рефлексии по полям.

Зачем это нужно — стандартное представление часто не то, что хочет API:

- В примере `Color{R,G,B}` сериализуется не в объект `{"R":..,"G":..,"B":..}`, а в привычную строку `"#rrggbb"`, и обратно парсится из неё.
- Те же сценарии: даты в нестандартном формате, IP-адреса как строки, enum как текстовая метка, деньги как строка.

**Важная деталь сигнатур:** `MarshalJSON` объявлен на **значении** (`c Color`), а `UnmarshalJSON` — обязательно на **указателе** (`c *Color`), потому что должен изменить поля разбираемого значения. Если объявить `UnmarshalJSON` на значении, метод не попадёт в method set указателя для нужного интерфейса и просто не будет вызван при декодировании.

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

Строки в Go **неизменяемы**, поэтому `s += "hello"` каждый раз выделяет **новую** строку и копирует туда всё накопленное. На N итераций это даёт O(N²) копирований и кучу мусора — отсюда тормоза на больших объёмах.

`strings.Builder` решает проблему: внутри он держит растущий `[]byte` (как `append` у слайса) и расширяет его с **амортизированной** стоимостью — буфер удваивается, а не перевыделяется на каждый `WriteString`. Поэтому сборка строки из множества частей становится O(N).

`sb.String()` в конце отдаёт результат **без копирования** буфера (через `unsafe` внутри пакета) — ещё одна экономия по сравнению с конкатенацией.

**Рекомендация:** для пары конкатенаций обычный `+` нормален и читабельнее. `strings.Builder` оправдан, когда частей много или их число неизвестно (цикл, генерация). Если знаешь итоговый размер заранее — вызови `sb.Grow(n)`, чтобы избежать промежуточных перевыделений.

## Q12. bytes.Buffer для эффективной работы с byte?

```go
var buf bytes.Buffer
buf.WriteString("hello")
buf.WriteByte(' ')
buf.WriteString("world")

fmt.Println(buf.String()) // "hello world"
fmt.Println(buf.Bytes())  // []byte("hello world")
```

`bytes.Buffer` — растущий буфер байтов, который умеет и накапливать данные, и отдавать их. Его суперсила в том, что он реализует сразу **`io.Reader` и `io.Writer`**: значит, его можно подсунуть в любую функцию стандартной библиотеки, ожидающую эти интерфейсы, — и собрать результат в памяти вместо файла или сети.

```go
// Запись JSON в buffer вместо файла
var buf bytes.Buffer
json.NewEncoder(&buf).Encode(myStruct)
log.Printf("encoded: %s", buf.String())
```

Здесь `json.NewEncoder` думает, что пишет в обычный `io.Writer`, а на деле всё оседает в `buf` — удобно для тестов, логирования, in-memory сборки ответа.

**Когда что:** `strings.Builder` — когда строишь **строку** (только запись, результат `string`). `bytes.Buffer` — когда нужны **байты** и/или двунаправленная работа (и читать, и писать), либо передача как `io.Writer`/`io.Reader`.

## Q13. (!) Чем различаются time.Time и time.Duration?

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

В `time` есть два разных по смыслу типа, и их важно не путать:

- **`time.Time`** — момент во времени (точка): «18 апреля 2025, 14:30». Получаем через `time.Now()`, сдвигаем через `Add`.
- **`time.Duration`** — длительность (интервал): «5 секунд». Под капотом это `int64` наносекунд, а не «дата». Разность двух `Time` (`later.Sub(now)`) даёт именно `Duration`.

Поскольку `Duration` — обычное число наносекунд, длительности **складываются и умножаются** как числа: `5 * time.Second`, `2*time.Hour + 30*time.Minute`. Константы `time.Second`, `time.Minute` и т.д. — это просто заранее посчитанные значения в наносекундах.

**Удобство:** `Duration` сам красиво печатается (`5s`, `2h30m0s`) и отдаёт компоненты методами `Seconds()`, `Minutes()` (как `float64`). Поэтому в коде почти всегда оперируют `Duration`, а не «голыми» миллисекундами в `int`.

## Q14. Парсинг времени — особенности формата?

Парсинг и форматирование времени в Go устроены не так, как везде. Вместо плейсхолдеров `YYYY-MM-DD` ты записываешь **образец** того, как должна выглядеть конкретная опорная дата:

**Mon Jan 2 15:04:05 MST 2006** — её легко запомнить как последовательность 1 2 3 4 5 6 (месяц=01, день=02, час=03 PM=15, минута=04, секунда=05, год=06).

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

Идея в том, что layout — это **пример нужного формата**, а не строка спецсимволов: глядя на `"2006-01-02 15:04:05"`, сразу видно, как будет выглядеть результат. Та же строка работает в обе стороны — `time.Parse(layout, str)` разбирает, `t.Format(layout)` печатает.

**Подводные камни:**

- Цифры в layout **значимы**: перепутал `15` (24-часовой час) и `03` (12-часовой) — и получишь неверное время без ошибки компиляции.
- Не выдумывай свой образец, когда есть готовые константы. `time.RFC3339` — стандарт для API и JSON; используй его, а не ручной `"2006-01-02T15:04:05Z07:00"`.

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

`time.Time` хранит не просто «дату-время», а момент **плюс часовой пояс** (`*time.Location`). Это разводит два понятия:

- **Точка во времени** — один и тот же физический момент, выраженный в разных поясах. `In(loc)` меняет только пояс отображения, сам момент остаётся прежним.
- **Сравнение.** Используй `t1.Equal(t2)`, а не `t1 == t2`: `Equal` сравнивает именно момент и вернёт `true` для одной точки в разных поясах. Оператор `==` сравнивает в том числе внутреннее представление `Location` и может дать `false` для одинакового момента — это распространённая ловушка.

`LoadLocation("America/New_York")` тянет правила пояса (включая переходы на летнее время) из системной базы IANA tz; на «голых» контейнерах её может не быть — тогда подкладывают пакет `time/tzdata` или ставят tzdata в образ.

**Рекомендация:** храни и считай всё в **UTC** (`time.Now().UTC()`), а в локальный пояс переводи только при показе пользователю. Так уходят баги с переходом на летнее время и расхождением серверов в разных зонах.

## Q16. Таймеры: time.NewTimer, time.Tick, time.After — в чём разница?

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

Все три инструмента дают канал `C`, в который приходит «сигнал времени», — разница в том, сколько раз и удобно ли отменять:

- **`time.NewTimer(d)`** — сработает **один раз** через `d`: в канал `C` придёт одно значение. Если ждать больше не нужно — `timer.Stop()` отменяет и освобождает таймер.
- **`time.NewTicker(d)`** — шлёт в `C` значение **каждые** `d`, пока его не остановят. Обязательно `defer ticker.Stop()`, иначе тикер продолжит тикать и течь после выхода из функции.
- **`time.After(d)`** — обёртка-однострочник: создаёт одноразовый таймер и сразу возвращает его канал. Заточена под `select` как ветка таймаута, чтобы не заводить переменную-таймер вручную.

**Подводный камень `time.After`:** под капотом он создаёт `Timer`, который **нельзя остановить** (ссылки на него у вас нет), и он живёт до самого срабатывания. В цикле или горячем коде — например, `for { select { case <-time.After(...) ...` — это плодит таймеры и течёт по памяти. В таких местах создавай один `NewTimer` заранее и переиспользуй его с `Reset`/`Stop`.

(В Go 1.23+ сборщик умеет собирать незавершённые таймеры от `After` агрессивнее, но идиома «`NewTimer` в горячем цикле» по-прежнему надёжнее и явнее.)

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

`context.Context` — стандартный способ нести через цепочку вызовов **сигнал отмены, дедлайн и request-scoped значения**. Главная задача — вовремя свернуть работу: отменили запрос или вышел таймаут — все горутины, которым передали ctx, узнают об этом и прекращают работать, не тратя ресурсы впустую.

С чего начинается контекст:

- **`context.Background()`** — корень дерева, точка старта (в `main`, в обработчике запроса).
- **`context.TODO()`** — заглушка, когда контекст ещё неоткуда взять; сигнал «здесь нужно протянуть настоящий ctx».

Контексты **наследуются**: каждый `WithXxx` оборачивает родителя и возвращает потомка. Отмена/дедлайн родителя автоматически распространяется на всех потомков.

- **`WithCancel`** — ручная отмена через вызов `cancel()`.
- **`WithTimeout`** — отмена через заданный интервал.
- **`WithDeadline`** — отмена к конкретному моменту времени.
- **`WithValue`** — протащить request-scoped данные (traceID, userID). Не для опциональных параметров функции — только для сквозных метаданных.

Читают отмену через **`<-ctx.Done()`**: этот канал закрывается при отмене/таймауте, а `ctx.Err()` объясняет причину — `context.Canceled` (позвали `cancel`) или `context.DeadlineExceeded` (вышло время).

**Важно про `cancel`:** `defer cancel()` обязателен даже для `WithTimeout`/`WithDeadline`. Иначе утекут внутренние ресурсы контекста (таймер и горутина), пока дедлайн не наступит сам. `go vet` ругается, если `cancel` потерян.

Подробнее — в [Go Concurrency](go-concurrency-interview.md).

## Q18. (!) Работа с ошибками: errors.New, errors.Is, errors.As?

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

В Go ошибка — обычное значение (интерфейс `error`), и пакет `errors` даёт инструменты её создавать, оборачивать и разбирать.

**Создание:**

- `errors.New("...")` — простая ошибка с текстом.
- `fmt.Errorf("... %s", x)` — ошибка с форматированием.

**Оборачивание (wrapping, с Go 1.13).** Глагол **`%w`** в `fmt.Errorf` вкладывает исходную ошибку внутрь новой, сохраняя цепочку причин. Так на каждом уровне добавляют контекст («processing failed»), не теряя первопричину. Это ключ к понятным логам: видна вся история «откуда взялась ошибка».

Чтобы по этой цепочке что-то узнать, есть две функции — и разница между ними частый вопрос на собесе:

- **`errors.Is(err, target)`** — отвечает на «**это** ли ошибка?». Идёт по всей цепочке `%w` и сравнивает с конкретным значением — обычно с **sentinel-ошибкой** (`ErrNotFound`). Заменяет хрупкое `err == ErrNotFound`, которое ломается, как только ошибку обернули.
- **`errors.As(err, &target)`** — отвечает на «есть ли в цепочке ошибка **этого типа**?». Находит в цепочке ошибку нужного типа и записывает её в `target`, давая доступ к её полям (например, `verr.Field`). Это безопасный аналог type assertion, работающий сквозь обёртки.

**Эмпирическое правило:** сравниваешь с известным экземпляром — `Is`; нужен доступ к полям/типу ошибки — `As`. Никогда не сравнивай ошибки по тексту (`err.Error() == "..."`) — это самый ломкий вариант.

## Q19. Объединение ошибок через errors.Join (Go 1.20+)?

```go
err1 := errors.New("first error")
err2 := errors.New("second error")

joined := errors.Join(err1, err2)
// Output: "first error\nsecond error"

errors.Is(joined, err1) // true
errors.Is(joined, err2) // true
```

`errors.Join` (Go 1.20+) объединяет **несколько ошибок в одну**. Раньше для этого писали свои типы или склеивали строки; теперь это в стандарте.

Что важно: объединённая ошибка остаётся «прозрачной» для `errors.Is`/`errors.As` — проверка пройдёт по **каждой** вложенной ошибке. То есть `errors.Is(joined, err1)` и `errors.Is(joined, err2)` оба вернут `true`. Текстом она печатается как ошибки через перевод строки.

**Сценарий применения** — когда нужно собрать сразу все проблемы, а не падать на первой: валидация формы (вернуть все невалидные поля разом), batch-операции (что именно из пачки не прошло). `nil`-ошибки `Join` отбрасывает, а если все аргументы `nil` — вернёт `nil`, поэтому накопитель ниже безопасен.

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

## Q20. (!) log/slog (structured logging) — что это?

`log/slog` (Go 1.21+) — встроенный **структурированный логгер**. «Структурированный» значит, что лог пишется не сплошной строкой, а как набор пар ключ-значение (`user_id=42`), которые легко парсить, фильтровать и искать в Kibana/Loki/Grafana. Раньше за этим шли в zap/zerolog/logrus — теперь базовый кейс закрыт стандартом.

Из чего он состоит:

- **Logger** — фасад: `slog.Info(msg, key, value, ...)`. Атрибуты передают парами либо типизированно (`slog.Int`, `slog.String`).
- **Handler** — определяет, **куда и в каком формате** писать. `NewTextHandler` — человекочитаемый `key=value`, `NewJSONHandler` — JSON для машинного сбора логов. Свой Handler можно написать под любой бэкенд.
- `slog.Group(...)` вкладывает атрибуты в подобъект — в JSON это вложенный `{...}`.

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

Возможности:

- **Группы атрибутов** — вкладывать связанные поля под общий ключ.
- **Сменные handler'ы** — text, JSON или свой.
- **Уровни** — Debug, Info, Warn, Error (порог настраивается через `HandlerOptions.Level`).
- **Источник** — файл и строку вызова (`AddSource`).

**Рекомендация:** в начале программы один раз настрой нужный Handler и сделай `slog.SetDefault(logger)` — после этого пакетные вызовы `slog.Info(...)` по всему коду пойдут через него. Версия с контекстом (`slog.InfoContext`) позволяет Handler'у вытаскивать из ctx сквозные поля вроде traceID.

## Q21. Чем выбрать логгер: log vs slog vs zap vs logrus?

| Library | Стиль | Performance | Стандарт |
|---------|-------|-------------|----------|
| `log` | Plain text | Норм | Да (с 2009) |
| `log/slog` | Structured | Хороший | **Да** (Go 1.21+) |
| `zap` (Uber) | Structured | **Лучший** | Нет |
| `logrus` | Structured | Хороший | Нет |
| `zerolog` | Structured (zero allocations) | Очень хороший | Нет |

Ключевые различия:

- **`log`** — самый старый, plain-text, без структуры и уровней. Годится для CLI и набросков, но не для прод-сервисов, где логи парсят машинами.
- **`log/slog`** — структурный логгер из стандарта (Go 1.21+). Главный плюс — он встроенный и расширяемый через Handler, поэтому не тащит зависимостей.
- **`zap` (Uber)** и **`zerolog`** — сторонние, но **самые быстрые**: спроектированы вокруг минимума аллокаций (zerolog — практически zero-allocation на горячем пути). Берут там, где логирование на критичном пути и важна каждая аллокация.
- **`logrus`** — исторически популярный structured-логгер, сейчас в режиме поддержки; для нового кода обычно выбирают slog или zap.

**Эмпирическое правило:** новый сервис → начинай с `log/slog`, его достаточно для подавляющего большинства приложений. Упёрся в производительность логирования под высокой нагрузкой → переходи на `zap`/`zerolog`. Сменить логгер позже несложно, если в коде ходить через тонкий интерфейс, а не звать конкретный пакет напрямую.

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

`database/sql` — это **обобщённый интерфейс** к SQL-базам: пул соединений, транзакции, плейсхолдеры. Сам он не умеет говорить с конкретной СУБД — за это отвечает **драйвер** (`lib/pq` для PostgreSQL, `go-sql-driver/mysql`, `mattn/go-sqlite3`). Драйвер подключают анонимным импортом `_ "..."`: пакет не используется по имени, но в `init()` регистрирует себя, чтобы `sql.Open("postgres", ...)` нашёл реализацию по имени.

Три способа выполнить запрос:

- **`QueryRow`** — ожидаешь **одну** строку. Результат сразу разбираешь в переменные через `.Scan(&dst)`.
- **`Query`** — **много** строк. Идёшь по ним циклом `rows.Next()` + `rows.Scan(...)`. Обязательно `defer rows.Close()`, иначе соединение не вернётся в пул (см. Q23).
- **`Exec`** — запрос **без выборки** (INSERT/UPDATE/DELETE). Возвращает `Result` с `RowsAffected()` и (где поддерживается) `LastInsertId()`.

**Важно про безопасность:** значения подставляй **только** через плейсхолдеры (`$1` у Postgres, `?` у MySQL), а не конкатенацией строк. Драйвер передаёт их отдельно от текста запроса — это и есть защита от SQL-инъекций.

## Q23. Connection pool в Go?

У `database/sql` пул соединений **встроен** — отдельную библиотеку для пулинга подключать не нужно. Это и объясняет ключевой момент: `*sql.DB` — это **не одно соединение, а handle к пулу**. Поэтому его создают **один раз** на всё приложение (а не на запрос), он потокобезопасен, и под каждый запрос соединение автоматически берётся из пула и возвращается обратно.

Что настраивают и зачем:

```go
db.SetMaxOpenConns(25)            // макс открытых connections
db.SetMaxIdleConns(5)             // idle connections в пуле
db.SetConnMaxLifetime(5 * time.Minute) // время жизни connection
db.SetConnMaxIdleTime(time.Minute)     // макс idle время
```

Смысл параметров:

- `SetMaxOpenConns` — потолок одновременных соединений к БД. Защищает базу от перегрузки; согласуй с её лимитом `max_connections` (особенно при нескольких инстансах сервиса).
- `SetMaxIdleConns` — сколько простаивающих соединений держать наготове для переиспользования. Слишком мало — частые переоткрытия; должно быть не больше `MaxOpenConns`.
- `SetConnMaxLifetime` — максимальный возраст соединения. Помогает равномерно перераспределять нагрузку при масштабировании БД и обходить серверные таймауты простоя/балансировщик.
- `SetConnMaxIdleTime` — закрывать соединение, провисевшее без дела дольше указанного.

**Главный подводный камень — утечка соединений.** Если не закрыть `Rows` (`defer rows.Close()`) или не завершить транзакцию, соединение **не вернётся в пул**. Накопится столько таких — упрёшься в `MaxOpenConns`, и новые запросы начнут висеть, ожидая свободного соединения (вплоть до дедлайна контекста). Это классическая причина «зависшего» под нагрузкой сервиса.

## Q24. (!) Зачем нужны prepared statements?

```go
stmt, err := db.Prepare("SELECT name FROM users WHERE id = $1")
if err != nil { log.Fatal(err) }
defer stmt.Close()

for _, id := range []int{1, 2, 3} {
    var name string
    stmt.QueryRow(id).Scan(&name)
}
```

Prepared statement — это запрос, который БД **разбирает и компилирует один раз**, а потом ты много раз выполняешь его с разными параметрами. `db.Prepare` возвращает `*sql.Stmt`, привязанный к соединению; его обязательно закрывают через `defer stmt.Close()`.

**Что это даёт:**

- **Защита от SQL-инъекций** — параметры всегда передаются отдельно от текста запроса, их невозможно «подмешать» в SQL.
- **Однократная компиляция** — план запроса строится один раз, а не на каждый вызов.
- **Скорость на повторяющихся запросах** — в цикле (как выше с тремя `id`) экономится парсинг и планирование.

**Ключевой нюанс для собеса:** в Go ради безопасности `Prepare` руками звать почти не нужно. Когда ты передаёшь параметры в `db.Query`/`db.Exec`, драйвер **сам** под капотом готовит statement, выполняет и закрывает его. То есть `?`/`$1` уже защищают от инъекций без явного `Prepare`. Явный `Prepare` оправдан только когда **один и тот же** запрос гоняется много раз подряд и хочется переиспользовать скомпилированный statement.

## Q25. Транзакции и идиома deferred Rollback?

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

Транзакция (`tx`) группирует несколько запросов в одну атомарную единицу: либо применяются все, либо ни один. Важно, что после `Begin` все запросы идут через `tx.Exec`/`tx.Query`, а **не** через `db` — иначе они выполнятся вне транзакции, на другом соединении из пула.

**Идиома `defer tx.Rollback()`** — главное, что здесь спрашивают. Ставим откат в `defer` сразу после `Begin` как страховку: при любом раннем `return` из-за ошибки транзакция гарантированно откатится, и не нужно вызывать `Rollback` вручную в каждой ветке. Это работает, потому что:

- если выполнение дошло до `tx.Commit()` и он прошёл — последующий `Rollback` уже **ничего не делает** (вернёт `ErrTxDone`, который игнорируют);
- если случилась ошибка и мы вышли раньше — `defer` откатит изменения.

Так одна строка закрывает все пути выхода из функции и устраняет классическую утечку: незакрытую транзакцию, держащую соединение и блокировки в БД.

**Рекомендация:** используй `BeginTx(ctx, ...)`, чтобы транзакция уважала отмену/таймаут контекста, и проверяй ошибку `Commit` — именно в ней всплывают конфликты сериализации и нарушения ограничений.

## Q26. (!) io.Reader, io.Writer интерфейсы?

```go
type Reader interface {
    Read(p []byte) (n int, err error)
}

type Writer interface {
    Write(p []byte) (n int, err error)
}
```

`io.Reader` и `io.Writer` — два **самых важных** интерфейса стандартной библиотеки, по одному методу в каждом. Их сила в малости: любой источник байтов реализует `Read`, любой приёмник — `Write`, поэтому код пишется против **абстракции потока байтов**, не зная, что за ней — файл, сеть, память или сжатие.

Один и тот же интерфейс реализуют совершенно разные вещи:

- `os.File` — оба (и читать, и писать).
- `bytes.Buffer` — оба (буфер в памяти).
- `strings.Reader` — Reader поверх строки.
- `http.Response.Body` — Reader (тело ответа).
- `bufio.Reader`/`bufio.Writer` — буферизованные обёртки над любым Reader/Writer.

Благодаря единому интерфейсу работают универсальные хелперы — им всё равно, что конкретно за `r`/`w`:

```go
io.Copy(dst, src)    // copy всё
io.ReadAll(r)         // прочесть всё в []byte
io.Pipe()             // create pipe
```

**Композиция** — главный приём: Reader/Writer оборачивают друг друга, как трубы. `gzip.NewWriter(file)` — сжатие поверх файла; `io.TeeReader` — читать и одновременно копировать в сторону; `io.MultiWriter` — писать сразу в несколько мест. Эта «сборка из кубиков» и есть идиоматичный Go-I/O.

**Подводный камень:** `io.ReadAll` грузит **весь** поток в память — для больших/неограниченных тел (загрузка файла, бесконечный стрим) это риск OOM. Там, где размер велик или неизвестен, используй `io.Copy` или потоковую обработку чанками.

## Q27. Почему ioutil устарел (Go 1.16+)?

Пакет `io/ioutil` помечен **deprecated** с Go 1.16. Причина — он был свалкой разнородных хелперов; их разнесли по логичным местам: файловые операции ушли в `os`, потоковые — в `io`. Новых функций туда не добавляют. Соответствие старого и нового:

| Старое | Новое |
|--------|-------|
| `ioutil.ReadFile` | `os.ReadFile` |
| `ioutil.WriteFile` | `os.WriteFile` |
| `ioutil.ReadAll` | `io.ReadAll` |
| `ioutil.TempFile` | `os.CreateTemp` |
| `ioutil.TempDir` | `os.MkdirTemp` |
| `ioutil.NopCloser` | `io.NopCloser` |

Старые функции по-прежнему работают (они просто проксируют в новые), но линтеры выдают warning. В новом коде используй замены из правой колонки; старый код мигрируется механически — `gofmt`/`gopls` умеют предлагать автозамену.

## Q28. (!) //go:embed для статических файлов?

Директива `//go:embed` (Go 1.16+) **вшивает файлы прямо в скомпилированный бинарник** на этапе компиляции. Раньше шаблоны, миграции и конфиги приходилось класть рядом с бинарём и не забывать копировать при деплое — теперь они часть исполняемого файла.

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

Как это работает на практике:

- Комментарий `//go:embed <шаблон>` должен стоять **вплотную** над переменной (без пустой строки), а в файле должен быть `import "embed"`.
- Тип переменной задаёт форму данных: `embed.FS` — целая виртуальная файловая система (несколько файлов, каталоги, glob `*.html`); `[]byte` — содержимое одного файла как байты; `string` — как строка.
- `embed.FS` реализует интерфейс `fs.FS`, поэтому встроенные файлы прозрачно работают с `fs.ReadDir`, `template.ParseFS`, а через `http.FS` — отдаются как статика обычным `http.FileServer`.

**Сценарии применения:**

- **Single-binary деплой** — один исполняемый файл, никаких внешних ассетов рядом.
- **Шаблоны, SQL-миграции, конфиги по умолчанию** — то, что должно ехать вместе с кодом.
- **Статика веб-приложения** (HTML/CSS/JS) внутри бинаря.

**Подводные камни:** встроенные файлы **read-only** и фиксируются на момент сборки — чтобы обновить, нужно пересобрать бинарь. Пути в `//go:embed` относительны пакету и **не могут** выходить выше (`../`); большие ассеты раздувают размер бинарника.

## Q29. Чем отличается sort.Slice от sort.Sort?

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

Оба сортируют слайс на месте, разница — в том, как ты задаёшь порядок:

- **`sort.Slice`** — передаёшь слайс и **одну closure** `less(i, j)`, сравнивающую элементы по индексам. Коротко, пишется по месту, не нужен отдельный тип. Подходит для 99% прикладного кода.
- **`sort.Sort`** — требует тип, реализующий `sort.Interface` (три метода: `Len`, `Swap`, `Less`). Многословно, зато порядок переиспользуем и привязан к типу (как `ByAge`).

Почему `sort.Slice` чуть медленнее: сравнение и перестановка идут через closure и рефлексию над слайсом, тогда как у `sort.Sort` методы вызываются напрямую. На практике разница заметна лишь на очень больших и горячих сортировках.

**Что выбрать:** по умолчанию — `sort.Slice` (или `slices.SortFunc` из Q30, он generic и ещё быстрее). `sort.Sort` оправдан, когда один и тот же порядок нужен в разных местах или важна максимальная скорость. Нужна **стабильная** сортировка (сохранять порядок равных) — есть `sort.SliceStable`/`sort.Stable`.

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

`slices` (Go 1.21+) — пакет **типобезопасных дженерик-функций** для слайсов: сортировка, поиск, сравнение, копирование. До него такие операции писали руками в каждом проекте или гоняли через рефлексию (`sort.Slice`, `reflect`).

Главное преимущество — **дженерики вместо рефлексии**:

- **Типобезопасность** — `slices.Contains([]int, "str")` не скомпилируется, ошибку ловит компилятор, а не рантайм.
- **Скорость** — нет накладных расходов рефлексии; `slices.Sort` обычно быстрее `sort.Slice`.

Полезные функции из примера: `Sort` (для упорядочиваемых типов), `Contains`/`Index` (поиск), `Min`/`Max`, `Equal` (поэлементное сравнение, в отличие от `==`, которое к слайсам неприменимо), `Clone` (поверхностная копия). `SortFunc` сортирует по своему компаратору, который возвращает `int` (`<0`, `0`, `>0`) — обычно через `cmp.Compare`.

Парный пакет **`maps`** даёт то же для map'ов:

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

