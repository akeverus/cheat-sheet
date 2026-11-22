---
title: "Вопросы на собеседовании: Стандартная библиотека Go"
description: "Стандартная библиотека Go: net/http, encoding/json и json/v2, io/ioutil, strings, time, context, log/slog, database/sql, embed, sort, errors"
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
updated: "2026-11-02"
---
# Вопросы на собеседовании: Стандартная библиотека Go

Стандартная библиотека Go покрывает 80% потребностей backend-разработки **без third-party**. На интервью спрашивают: устройство `net/http`, нюансы `encoding/json`, новый `log/slog`, `database/sql`, `time` пакет, `embed` (с Go 1.16), `errors` wrapping.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Standard Library](https://pkg.go.dev/std)
- [net/http package](https://pkg.go.dev/net/http)
- [encoding/json package](https://pkg.go.dev/encoding/json)
- [encoding/json/v2 и jsontext (Go 1.27)](https://go.dev/doc/go1.27#jsonv2)
- [database/sql tutorial](https://go.dev/doc/database/index)
- [Отмена операций БД](https://go.dev/doc/database/cancel-operations) и [пул соединений](https://go.dev/doc/database/manage-connections)
- [log/slog (Go 1.21+)](https://pkg.go.dev/log/slog)
- [embed package](https://pkg.go.dev/embed)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**net/http**
- [Q1. (!) HTTP-сервер на стандартном net/http?](#q1--http-сервер-на-стандартном-nethttp)
- [Q2. (!) ServeMux и роутинг?](#q2--servemux-и-роутинг)
- [Q3. (!) Middleware patterns в Go?](#q3--middleware-patterns-в-go)
- [Q4. (!) HTTP client: timeout и повторное использование соединений?](#q4--http-client-timeout-и-повторное-использование-соединений)
- [Q5. Graceful shutdown HTTP сервера?](#q5-graceful-shutdown-http-сервера)

**encoding/json**
- [Q6. (!) Marshal и Unmarshal?](#q6--marshal-и-unmarshal)
- [Q7. (!) Struct tags для JSON?](#q7--struct-tags-для-json)
- [Q8. (!) json.RawMessage — для отложенного парсинга?](#q8--jsonrawmessage--для-отложенного-парсинга)
- [Q9. Потоковый JSON через Decoder/Encoder — зачем?](#q9-потоковый-json-через-decoderencoder--зачем)
- [Q10. Своя сериализация: MarshalJSON / UnmarshalJSON?](#q10-своя-сериализация-marshaljson--unmarshaljson)

**Strings и Bytes**
- [Q11. (!) strings.Builder vs конкатенация?](#q11--stringsbuilder-vs-конкатенация)
- [Q12. Что умеет bytes.Buffer при чтении и записи?](#q12-что-умеет-bytesbuffer-при-чтении-и-записи)

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

**Go 1.27: JSON**
- [Q31. Когда выбрать encoding/json/v2 и как мигрировать с encoding/json?](#q31-когда-выбрать-encodingjsonv2-и-как-мигрировать-с-encodingjson)

## Q1. (!) HTTP-сервер на стандартном net/http?

```go
package main

import (
    "fmt"
    "log"
    "net/http"
    "time"
)

func helloHandler(w http.ResponseWriter, _ *http.Request) {
    fmt.Fprintln(w, "Hello!")
}

func main() {
    mux := http.NewServeMux()
    mux.HandleFunc("/hello", helloHandler)

    server := &http.Server{
        Addr:              ":8080",
        Handler:           mux,
        ReadHeaderTimeout: 5 * time.Second,
        WriteTimeout:      15 * time.Second,
        IdleTimeout:       60 * time.Second,
    }
    log.Fatal(server.ListenAndServe())
}
```

`net/http` даёт HTTP-сервер без обязательного фреймворка. Для рабочего сервиса нужны осознанные настройки сервера и окружения: таймауты, ограничения тела запроса, TLS или доверенный reverse proxy, а также остановка без потери активных запросов (Q5).

Минимальный сервер — это две вещи:

- **`http.Handler`** — интерфейс с методом `ServeHTTP(w http.ResponseWriter, r *http.Request)`; `HandleFunc` принимает функцию такой формы и превращает её в обработчик.
- **`Server.ListenAndServe`** — блокирующий вызов: открывает сокет и обслуживает запросы, пока не вернёт ошибку. Запускать его в отдельной горутине нужно только если текущая горутина должна продолжить работу, например ждать сигнал остановки (Q5).

Если хендлер не вызвал `WriteHeader`, первая запись в `ResponseWriter` отправит статус `200 OK`. Для других статусов вызовите `WriteHeader` до записи тела.

В примере `NewServeMux` создаёт отдельный роутер и передаётся через `Server.Handler`. В коротких примерах допустимы `http.HandleFunc(...)` и `http.ListenAndServe(":8080", nil)`: они используют глобальный `DefaultServeMux`. Отдельный mux делает набор маршрутов явным и не зависит от регистраций других пакетов.

`ReadHeaderTimeout` ограничивает чтение заголовков; `WriteTimeout` — время записи ответа, `IdleTimeout` — ожидание следующего запроса на соединении. Они не заменяют лимит размера тела и дедлайн работы хендлера. Для долгих потоковых ответов фиксированный `WriteTimeout` надо подбирать отдельно. Если затем вызывается `Shutdown`, `ListenAndServe` возвращает `http.ErrServerClosed`, который в коде остановки нужно отличать от сбоя (Q5). [net/http: Server](https://pkg.go.dev/net/http#Server), [ListenAndServe](https://pkg.go.dev/net/http#ListenAndServe).

## Q2. (!) ServeMux и роутинг?

```go
func routes() *http.ServeMux {
    mux := http.NewServeMux()
    mux.HandleFunc("GET /users", func(w http.ResponseWriter, _ *http.Request) {
        fmt.Fprintln(w, "users")
    })
    mux.HandleFunc("GET /users/{id}", func(w http.ResponseWriter, r *http.Request) {
        fmt.Fprintln(w, r.PathValue("id"))
    })
    return mux
}
```

С **Go 1.22** `ServeMux` понимает метод и сегмент пути в паттерне. `GET /users/{id}` принимает один сегмент после `/users/`, а `r.PathValue("id")` возвращает его значение. Паттерн `GET /users` подходит и для `HEAD`; паттерн без метода подходит для любого метода. Для отдельного `POST` зарегистрируйте `POST /users`.

Путь `/users` без завершающего `/` совпадает только с этим путём, а `/users/` охватывает поддерево. Если зарегистрирован только `/users/`, запрос на `/users` перенаправляется на `/users/`; отдельная регистрация `/users` отменяет это перенаправление. Для совпадения только с `/users/` используйте `/users/{$}`.

При нескольких совпадениях выбирается **более специфичный** паттерн: он покрывает строго меньше запросов. Это не просто сравнение длины строк. Если множества запросов пересекаются и ни одно не является строгим подмножеством другого, паттерны конфликтуют. По общему правилу `Handle` и `HandleFunc` паникуют при регистрации второго паттерна. Для совместимости есть исключение: при таком пересечении паттернов с хостом и без хоста приоритет получает паттерн с хостом. Например, `GET /users/{id}` и `api.example.com/users/me` можно зарегистрировать вместе. Для `GET /users/me` на `api.example.com` сработает второй обработчик, а на другом хосте — первый. До Go 1.22 метод и параметр пути проверяли в обработчике или использовали сторонний роутер. При миграции можно временно вернуть старые правила через `GODEBUG=httpmuxgo121=1`; тогда новые шаблоны не действуют. [Go 1.22 release notes](https://go.dev/doc/go1.22#enhanced_routing_patterns), [ServeMux API](https://pkg.go.dev/net/http#ServeMux), [Go blog](https://go.dev/blog/routing-enhancements).

## Q3. (!) Middleware patterns в Go?

```go
import (
    "log"
    "net/http"
    "time"
)

type Middleware func(http.Handler) http.Handler

func Logging(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        next.ServeHTTP(w, r)
        log.Printf("%s %s took %v", r.Method, r.URL.Path, time.Since(start))
    })
}

func RequireRequestID(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        if r.Header.Get("X-Request-ID") == "" {
            http.Error(w, "missing request id", http.StatusBadRequest)
            return
        }
        next.ServeHTTP(w, r)
    })
}

func withMiddleware(handler http.Handler) http.Handler {
    return Logging(RequireRequestID(handler))
}

// server.Handler = withMiddleware(mux) // mux из Q2
```

Middleware в Go — это **функция, которая оборачивает один handler в другой**: принимает `http.Handler` и возвращает новый `http.Handler`. Внутри она делает свою работу (логирование, проверка токена) и решает, звать ли `next.ServeHTTP` дальше по цепочке.

Как это работает:

- `http.HandlerFunc` превращает обычную функцию с аргументами `ResponseWriter` и `*Request` в `http.Handler`. Код до `next.ServeHTTP` выполняется на входе, код после — когда вложенный handler вернулся.
- Чтобы прервать запрос, `RequireRequestID` пишет `400` и возвращается без вызова `next`: маршрутизатор и конечный handler не запускаются.
- В `Logging(RequireRequestID(handler))` сначала входит `Logging`, затем `RequireRequestID`. Даже при отказе внутренней проверки управление вернётся в `Logging`, и его код после `next` запишет длительность. Если `next` паникует, обычный код после вызова не выполняется; для безусловного завершающего действия используйте `defer`.

Не запускайте `next.ServeHTTP(w, r)` в отдельной горутине и не возвращайтесь раньше неё: после завершения `ServeHTTP` использовать `ResponseWriter` нельзя. Оборачивать можно и отдельный handler, и весь mux; для теста цепочки передайте готовый `http.Handler` в `httptest.NewRecorder` (Go Testing Q14). [net/http: Handler](https://pkg.go.dev/net/http#Handler), [HandlerFunc](https://pkg.go.dev/net/http#HandlerFunc), [ResponseWriter](https://pkg.go.dev/net/http#ResponseWriter).

## Q4. (!) HTTP client: timeout и повторное использование соединений?

```go
import (
    "context"
    "fmt"
    "io"
    "net/http"
    "time"
)

// Создать один раз и передавать вызывающему коду.
var client = &http.Client{Timeout: 5 * time.Second}

func fetch(ctx context.Context, endpoint string) ([]byte, error) {
    req, err := http.NewRequestWithContext(ctx, http.MethodGet, endpoint, nil)
    if err != nil {
        return nil, err
    }
    resp, err := client.Do(req)
    if err != nil {
        return nil, err
    }
    defer resp.Body.Close()

    if resp.StatusCode != http.StatusOK {
        return nil, fmt.Errorf("GET %s: %s", endpoint, resp.Status)
    }
    const maxResponseBytes = 1 << 20
    body, err := io.ReadAll(io.LimitReader(resp.Body, maxResponseBytes+1))
    if err != nil {
        return nil, err
    }
    if len(body) > maxResponseBytes {
        return nil, fmt.Errorf("response exceeds %d bytes", maxResponseBytes)
    }
    return body, nil
}
```

`Client.Timeout` ограничивает весь запрос: получение соединения, редиректы и чтение `Response.Body`. Таймер продолжает работать после возврата `Do`; нулевое значение означает отсутствие **общего** срока. У `http.DefaultClient`, который используют пакетные `http.Get` и `http.Post`, `Timeout` равен нулю. При этом у его транспорта есть отдельные таймауты этапов: отсутствие общего срока не означает, что каждый этап бесконечен. `NewRequestWithContext` связывает запрос с отменой вызывающего кода; действует более раннее из двух ограничений.

**Сценарий с задержкой тела ответа.** Сервер сразу отправил заголовки с `200 OK`, а затем перестал передавать данные. `Transport.ResponseHeaderTimeout` отсчитывается после полной отправки запроса, включая его тело, и ограничивает ожидание заголовков. В этом случае `Do` может вернуть успешный ответ, а `resp.Body.Read` продолжит ждать данные. Для короткого запроса задайте общий срок через `Client.Timeout` или контекст запроса и обрабатывайте ошибку чтения. Для долгоживущего потока общий срок завершает чтение даже при регулярном поступлении данных. Если требуется обрывать только зависший поток, ограничивайте паузу между данными отдельно. [Go — Transport и Client](https://pkg.go.dev/net/http#Transport).

`Client` безопасен для одновременного использования. Пул соединений хранит **`Transport`**: если `Client.Transport == nil`, применяется общий `http.DefaultTransport`, который уже повторно использует соединения и поддерживает HTTP/2. Поэтому новый `Client` на каждый запрос сам по себе не уничтожает общий пул при `Transport == nil`, но обычный путь — переиспользовать настроенный клиент. Создавать новый `Transport` на каждый запрос нельзя, если нужен его пул. При необходимости настройки клонируйте `http.DefaultTransport.(*http.Transport).Clone()` один раз и меняйте поля до первого запроса. `MaxIdleConns` и `MaxIdleConnsPerHost` ограничивают **простаивающие**, а `MaxConnsPerHost` — все соединения с хостом, включая активные; `IdleConnTimeout` закрывает долго простаивающие. Для простого случая отдельный `Transport` не нужен.

После успешного `Do` закройте `resp.Body`; `Do` не считает HTTP-статусы 4xx/5xx ошибкой. Для повторного использования HTTP/1 соединения обычно нужно дочитать body до EOF и закрыть его. С **Go 1.27** `Transport` может асинхронно дочитать ограниченный остаток после `Close`, поэтому закрывать body по окончании работы достаточно; повторное использование всё равно не гарантировано. До Go 1.27 непрочитанное HTTP/1 body обычно мешало повторному использованию соединения: так бывает и в ветке ошибки статуса из примера, где body закрывается без чтения. Пример принимает не более 1 МиБ из `resp.Body`. Дополнительный байт позволяет обнаружить превышение и вернуть ошибку вместо усечённого ответа (см. Q26). При превышении `defer` закрывает тело, даже если в потоке остались данные. Лимит относится к байтам, которые читает приложение, в том числе после автоматической распаковки gzip стандартным транспортом. [`io.LimitReader`](https://pkg.go.dev/io#LimitReader). [net/http Client](https://pkg.go.dev/net/http#Client), [Transport](https://pkg.go.dev/net/http#Transport), [NewRequestWithContext](https://pkg.go.dev/net/http#NewRequestWithContext), [Go blog: Better HTTP/1 connection reuse](https://go.dev/blog/http-connection-reuse).

## Q5. Graceful shutdown HTTP сервера?

```go
import (
    "context"
    "errors"
    "fmt"
    "net/http"
    "os"
    "os/signal"
    "syscall"
    "time"
)

// handler — заранее собранный mux или другой http.Handler.
func serveUntilSignal(handler http.Handler) error {
    srv := &http.Server{Addr: ":8080", Handler: handler}
    signalCtx, stop := signal.NotifyContext(
        context.Background(), os.Interrupt, syscall.SIGTERM,
    )
    defer stop()

    serveErr := make(chan error, 1)
    go func() { serveErr <- srv.ListenAndServe() }()

    select {
    case err := <-serveErr:
        return fmt.Errorf("HTTP server: %w", err)
    case <-signalCtx.Done():
    }
    stop() // следующий сигнал снова получит обычную обработку ОС

    shutdownCtx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
    defer cancel()
    if err := srv.Shutdown(shutdownCtx); err != nil {
        return fmt.Errorf("HTTP shutdown: %w", err)
    }
    if err := <-serveErr; err != nil && !errors.Is(err, http.ErrServerClosed) {
        return fmt.Errorf("HTTP server: %w", err)
    }
    return nil
}
```

`Server.Shutdown(ctx)` закрывает listener, затем простаивающие соединения и ждёт, пока активные запросы закончатся. Если `ctx` истёк раньше, метод возвращает ошибку контекста, **но сам не обрывает** оставшиеся активные соединения. Для принудительного закрытия есть отдельный `Server.Close`; после выхода процесса соединения также оборвутся. `Shutdown` не ждёт захваченные соединения, например WebSocket: им нужна отдельная остановка.

**Пример с WebSocket.** После HTTP/1.1 upgrade библиотека может забрать соединение через `Hijack`. Держите реестр таких соединений на стороне приложения. При остановке запретите новые upgrade и согласуйте этот запрет с регистрацией сессий, чтобы позднее соединение не выпало из реестра. Через `RegisterOnShutdown` можно запустить закрытие по протоколу. Завершающая горутина отдельно ждёт опустошения реестра в пределах общего дедлайна и закрывает оставшиеся соединения по его истечении. Возврат `Shutdown` не означает завершения callback или сессий, а `Server.Close` тоже не закрывает захваченные соединения. Пример выше управляет только HTTP-частью. [`Server.RegisterOnShutdown`](https://pkg.go.dev/net/http#Server.RegisterOnShutdown), [`Server.Close`](https://pkg.go.dev/net/http#Server.Close).

В примере `ListenAndServe` работает в горутине, а вызывающая горутина ждёт либо сигнала, либо ошибки запуска. Обычная ошибка запуска возвращается вызывающему коду; `log.Fatal` внутри серверной горутины здесь не нужен. После вызова `Shutdown` метод `ListenAndServe` возвращает `http.ErrServerClosed`, даже пока активные запросы ещё завершаются. Поэтому приложение ждёт **возврата самого `Shutdown`**, а `ErrServerClosed` не считает ошибкой. Буферизированный канал позволяет серверной горутине передать результат, даже если завершение уже пошло по другой ветке.

`signal.NotifyContext` (Go 1.16+) отменяет контекст при `SIGINT` или `SIGTERM`. Вызов `stop` освобождает подписку и восстанавливает обычную обработку следующего сигнала, если других подписок нет. Для `Shutdown` создаётся **новый** контекст от `Background`: сигнальный контекст уже отменён и привёл бы к немедленному прекращению ожидания. Пример рассчитан на Unix-подобное окружение с `SIGTERM`.

В Kubernetes бюджет остановки должен оставлять место и для `preStop`, и для завершения HTTP-запросов: после истечения `terminationGracePeriodSeconds` оставшийся процесс может получить `SIGKILL`. Сам `Shutdown` не гарантирует отсутствие потерь при долгих запросах или позднем поступлении трафика. [net/http Server.Shutdown](https://pkg.go.dev/net/http#Server.Shutdown), [Server.Close](https://pkg.go.dev/net/http#Server.Close), [os/signal.NotifyContext](https://pkg.go.dev/os/signal#NotifyContext), [Kubernetes Pod termination](https://kubernetes.io/docs/concepts/workloads/pods/pod-lifecycle/#termination-of-pods).

## Q6. (!) Marshal и Unmarshal?

```go
import "encoding/json"

type User struct {
    Name string `json:"name"`
    Age  int    `json:"age"`
}

func roundTripUser() (User, error) {
    original := User{Name: "Alice", Age: 30}
    data, err := json.Marshal(original) // {"name":"Alice","age":30}
    if err != nil {
        return User{}, err
    }

    var decoded User
    if err := json.Unmarshal(data, &decoded); err != nil {
        return User{}, err
    }
    return decoded, nil
}
```

`encoding/json` конвертирует между Go-значениями и JSON в обе стороны:

- **`json.Marshal(v)`** — Go-значение → `[]byte` с JSON.
- **`json.Unmarshal(data, &v)`** — JSON → Go. Вторым аргументом нужен ненулевой указатель; передача значения или `nil` вместо него возвращает `InvalidUnmarshalError`. Проверяйте ошибку и не используйте частично заполненную цель как успешный результат.

Для обычной структуры пакет во время обработки использует её тип и `json`-теги. Имя поля в JSON берётся из тега `json:"..."`, а при его отсутствии — из имени экспортируемого поля. При чтении он предпочитает точное совпадение имени, но также принимает совпадение без учёта регистра. Неизвестные ключи по умолчанию игнорируются; если их нужно отвергать, используйте `Decoder.DisallowUnknownFields()` (Q9). Собственные методы сериализации меняют стандартное отображение (Q10).

**Главный подводный камень — неэкспортируемые поля.** При стандартном отображении структуры поля с маленькой буквы не попадают в JSON и не заполняются из него, даже если рядом есть тег. Ошибки только из-за их наличия не будет.

```go
type User struct {
    name string  // стандартный Marshal пропустит
    Name string  // экспортируемое поле попадёт в JSON
}
```

Если JSON должен содержать ключ `name`, сделайте поле `Name` экспортируемым и задайте ему тег `json:"name"`. При переходе на `encoding/json/v2` проверьте отличия сопоставления имён и других правил в Q31. [Официальный API encoding/json](https://pkg.go.dev/encoding/json).

## Q7. (!) Struct tags для JSON?

```go
type User struct {
    ID        int        `json:"id"`
    Name      string     `json:"name"`
    Email     string     `json:"email,omitempty"`     // пропустить пустую строку
    Password  string     `json:"-"`                    // не читать и не писать
    CreatedAt time.Time  `json:"created_at"`
    Roles     []string   `json:"roles,omitempty"`
}
```

Struct-теги — это управление сериализацией прямо в объявлении поля: первый элемент после `json:` — имя ключа в JSON, дальше через запятую идут опции.

**Опции:**

- `omitempty` в `encoding/json` пропускает `false`, `0`, `""`, `nil` и массив, срез или map нулевой длины. Пустая структура под это правило не подпадает; `omitzero` проверяет Go zero value.
- `-` исключает поле и при записи, и при чтении. Для `Password` это защита от случайной выдачи, но принимать пароль в тот же тип с таким тегом нельзя.
- `,string` записывает число как JSON-строку и требует строку при чтении. Это полезно, если клиент представляет JSON-числа через IEEE 754 `float64`: целые за пределами точного диапазона теряют разряды. В v1 опция также работает с `bool` и `string`; поведение v2 отличается (см. Q31).

```go
type Money struct {
    Amount int64 `json:"amount,string"` // {"amount": "12345"}
}
```

**Подводный камень `omitempty`:** он смотрит на пустое Go-значение по правилам v1, а не на «поле задано или нет». У обычного `bool` нельзя отличить «не пришло» от «пришло `false`»; при обратной записи оба случая дадут пропуск поля. Если различие нужно, используйте `*bool`: при декодировании в новый DTO отсутствующее поле и явный `null` дают `nil`, а `false` даёт указатель на `false`. Если API различает все три случая, фиксируйте присутствие ключа отдельно, например через `map[string]json.RawMessage`. [encoding/json: struct tags](https://pkg.go.dev/encoding/json#hdr-Marshal), [различия v1/v2](https://pkg.go.dev/encoding/json/v2).

## Q8. (!) json.RawMessage — для отложенного парсинга?

```go
type Event struct {
    Type    string          `json:"type"`
    Payload json.RawMessage `json:"payload"`
}

type UserCreatedPayload struct { UserID string `json:"user_id"` }
type OrderPayload struct { OrderID string `json:"order_id"` }

func decodeEvent(data []byte) (any, error) {
    var e Event
    if err := json.Unmarshal(data, &e); err != nil {
        return nil, err
    }
    switch e.Type {
    case "user_created":
        var p UserCreatedPayload
        if err := json.Unmarshal(e.Payload, &p); err != nil {
            return nil, err
        }
        return p, nil
    case "order_placed":
        var p OrderPayload
        if err := json.Unmarshal(e.Payload, &p); err != nil {
            return nil, err
        }
        return p, nil
    default:
        return nil, fmt.Errorf("unknown event type %q", e.Type)
    }
}
```

`json.RawMessage` хранит байты JSON-значения, не преобразуя вложенное содержимое в прикладную Go-структуру. Внешний `Unmarshal` всё же проверяет синтаксис JSON; `RawMessage` не проверяет, подходит ли payload выбранной позже схеме.

Зачем это нужно: в примере форма `Payload` зависит от `Type`. Распарсить его сразу нельзя — мы ещё не знаем, это `UserCreatedPayload` или `OrderPayload`. Поэтому сначала читаем `Type` и сырой `Payload`, а потом по `switch` доделываем второй проход в конкретный тип.

**Сценарии применения:**

- **Разнородные события/сообщения** — общий конверт, разное тело (как выше).
- **Прокси и роутеры** — сохранить вложенное значение без отображения его полей в Go-типы. Повторная запись всего конверта не гарантирует побайтово идентичный JSON.
- **Отложенная работа** — не декодировать вложенное значение в прикладную структуру, пока она не понадобится; размер и синтаксис входа всё равно нужно проверять.

Если сообщение не поддерживает заявленный `Type`, возвращайте ошибку, а не принимайте непроверенный `Payload`. Для бинарных данных обычный `[]byte` кодируется как base64-строка; отдельного «base64-тега» здесь нет. [encoding/json: RawMessage](https://pkg.go.dev/encoding/json#RawMessage).

## Q9. Потоковый JSON через Decoder/Encoder — зачем?

`Decoder`/`Encoder` работают с `io.Reader`/`io.Writer`, тогда как `Marshal`/`Unmarshal` принимают или возвращают готовый `[]byte`. Когда это важно:

- **Последовательность JSON-значений** — `Decode` читает по одному значению до `io.EOF`; при записи каждый `Encode` добавляет перевод строки, что подходит для NDJSON.
- **Большой JSON-массив** — вызов `Decode(&allItems)` всё равно создаст весь срез. Чтобы обрабатывать элементы по одному, используйте `Token` для `[`/`]`, `More` и `Decode` внутри массива.

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

Источник/приёмник — любой `io.Reader`/`io.Writer`: файл, тело HTTP-запроса, сетевое соединение. `Decoder` имеет внутренний буфер и может прочитать из Reader дальше запрошенного значения; если затем продолжать чтение через тот же Reader, учтите `Decoder.Buffered()`. Один успешный `Decode` из HTTP body сам по себе не доказывает, что после JSON нет второго значения: для такого контракта проверьте следующий `Decode` на `io.EOF` и ограничьте размер тела запроса.

**Если контракт требует NDJSON.** Обычный `Decode` разбирает последовательность JSON-значений независимо от границ строк. Например, он примет объект с переводом строки между полями, хотя такая запись нарушает формат NDJSON. Для проверки формата разделяйте вход на строки с явным лимитом размера записи и разбирайте каждую строку целиком. Политику обработки пустых строк задайте явно. Для записи NDJSON оставьте компактный вывод `Encoder`: `SetIndent` может разбить значение на несколько строк. [Спецификация NDJSON](https://github.com/ndjson/ndjson-spec#3-functional-specification).

**Один массив целых чисел.** В примере `process` сразу обрабатывает очередное значение.

```go
func readArray(r io.Reader, process func(int) error) error {
    dec := json.NewDecoder(r)
    opening, err := dec.Token()
    if err != nil {
        return err
    }
    if opening != json.Delim('[') {
        return fmt.Errorf("expected a JSON array")
    }

    for dec.More() {
        var n *int
        if err := dec.Decode(&n); err != nil {
            return err
        }
        if n == nil {
            return fmt.Errorf("expected an integer, got null")
        }
        if err := process(*n); err != nil {
            return err
        }
    }

    if _, err := dec.Token(); err != nil { // закрывающая ]
        return err
    }
    if _, err := dec.Token(); err != io.EOF {
        if err != nil {
            return err
        }
        return fmt.Errorf("unexpected data after the array")
    }
    return nil
}
```

`More()` только определяет, виден ли следующий элемент. Чтение закрывающего токена позволяет обнаружить обрыв массива, а проверка EOF отвергает лишние данные после него. Обработка идёт до проверки конца документа: если на третьем элементе найдена ошибка, первые два уже могли вызвать побочные эффекты. Для атомарного импорта выполняйте обработку в транзакции либо сначала складывайте данные в промежуточное хранилище. [Документация Decoder.More](https://pkg.go.dev/encoding/json#Decoder.More), [Decoder.Token](https://pkg.go.dev/encoding/json#Decoder.Token).

**Нюанс:** `Encoder.Encode` дописывает в конце каждого вызова перевод строки, но не строит JSON-массив из нескольких вызовов. [encoding/json: Decoder/Encoder](https://pkg.go.dev/encoding/json#Decoder).

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
    var s string
    if err := json.Unmarshal(data, &s); err != nil {
        return err
    }
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

**Важная деталь получателя:** в примере `MarshalJSON` объявлен на значении (`c Color`), а `UnmarshalJSON` — на указателе (`c *Color`), чтобы менять поля исходного `Color`. Это выбор для данного типа, а не обязательная форма интерфейсов: метод со значением-получателем входит и в method set `Color`, и в method set `*Color`. Если сделать `UnmarshalJSON` методом с получателем-значением и присваивать `c.R`/`c.G`/`c.B`, изменится копия, а не декодируемое значение. [Go spec: Method sets](https://go.dev/ref/spec#Method_sets), [encoding/json](https://pkg.go.dev/encoding/json).

В `UnmarshalJSON` нельзя просто снять крайние кавычки: JSON-строка может содержать escapes, а вместо строки могут прийти `null` или объект. В примере используется `encoding/json` v1. При разборе объекта в `string` пакет вернёт ошибку типа. При разборе `null` вызов вернёт `nil` и оставит свежую переменную `s` пустой. Поэтому `Color.UnmarshalJSON` отклонит `null` своей проверкой длины, прежде чем прочитает `s[0]`. Внутренний вызов декодирует `string`, поэтому повторного вызова метода `Color` нет. Исходный цвет меняется только после проверки формата и успешного разбора числа. [Контракт encoding/json.Unmarshal](https://pkg.go.dev/encoding/json#Unmarshal).

## Q11. (!) strings.Builder vs конкатенация?

```go
import "strings"

func repeatedConcat() string {
    var s string
    for i := 0; i < 1000; i++ {
        s += "hello"
    }
    return s
}

func repeatedBuilder() string {
    var b strings.Builder
    b.Grow(1000 * len("hello"))
    for i := 0; i < 1000; i++ {
        b.WriteString("hello")
    }
    return b.String()
}
```

Обе функции возвращают одинаковую строку длиной 5000 байт. В цикле `s += "hello"` на каждой итерации нужно получить новую строку с уже накопленным содержимым: для фиксированного фрагмента суммарный объём копирования растёт квадратично с числом итераций. Это аргумент о механизме, а не универсальное обещание выигрыша по времени для любого короткого ввода.

`strings.Builder` накапливает байты в растущем буфере и предоставляет `WriteString`, `Write`, `WriteByte`, `String`. Он реализует `io.Writer`, но не `io.Reader`. `Grow(n)` заранее обеспечивает место ещё для `n` **байт** без новой аллокации при записи; отрицательное `n` вызывает панику. Ненулевой `Builder` нельзя копировать: при дальнейшем использовании копии текущая реализация обнаруживает это и паникует.

В Go 1.27 `Builder.String()` реализован без копирования накопленного буфера, но публичный API обещает результат, а не способ хранения. Не полагайтесь на этот внутренний приём при работе со строкой.

Для нескольких известных частей обычный `+` остаётся простым выбором. Для множества частей в цикле используйте `Builder`; если известен суммарный объём в байтах, вызовите `Grow` до записи. Реальную разницу времени и аллокаций для конкретного кода проверяйте бенчмарком. [strings.Builder](https://pkg.go.dev/strings#Builder), [реализация Builder в Go 1.27](https://go.dev/src/strings/builder.go), [Go blog: go fix и конкатенация в цикле](https://go.dev/blog/gofix).

## Q12. Что умеет bytes.Buffer при чтении и записи?

```go
import (
    "bytes"
    "encoding/json"
)

func roundTripName(name string) (string, error) {
    var buf bytes.Buffer
    value := struct{ Name string `json:"name"` }{Name: name}
    if err := json.NewEncoder(&buf).Encode(value); err != nil {
        return "", err
    }
    var decoded struct{ Name string `json:"name"` }
    if err := json.NewDecoder(&buf).Decode(&decoded); err != nil {
        return "", err
    }
    return decoded.Name, nil
}
```

`bytes.Buffer` — растущий буфер байтов с методами чтения и записи. `*bytes.Buffer` реализует и `io.Writer`, и `io.Reader`: в примере `json.Encoder` пишет в него, затем `json.Decoder` читает оставшееся содержимое. После успешного `roundTripName("Ada")` результат — `"Ada"`; ошибку любого этапа функция возвращает вызывающему коду. В реальном коде промежуточный буфер нужен, только если содержимое требуется сохранить или обработать между этапами.

`Read` сдвигает позицию чтения; `Len`, `String` и `Bytes` показывают **непрочитанную** часть. `Bytes()` возвращает срез, разделяющий память с буфером: изменение элементов сразу влияет на последующие чтения, а после следующего изменения самого буфера срез нельзя считать действительным представлением его данных. Если нужны независимые байты, сделайте копию. `Reset()` очищает содержимое, **но сохраняет выделенную память** для повторной записи; он не служит способом освободить большой буфер.

Для сборки только строки обычно выбирают `strings.Builder`. Когда нужен буфер байтов, чтение после записи или передача в API, ожидающее `io.Reader`/`io.Writer`, подходит `bytes.Buffer`. Оба типа при общем доступе из нескольких горутин требуют внешней синхронизации. [bytes.Buffer](https://pkg.go.dev/bytes#Buffer), [Buffer.Bytes](https://pkg.go.dev/bytes#Buffer.Bytes), [Buffer.Reset](https://pkg.go.dev/bytes#Buffer.Reset), [strings.Builder](https://pkg.go.dev/strings#Builder).

## Q13. (!) Чем различаются time.Time и time.Duration?

```go
import (
    "fmt"
    "time"
)

func durationExample() {
    start := time.Date(2025, time.April, 18, 14, 30, 0, 0, time.UTC)
    later := start.Add(5 * time.Second)
    elapsed := later.Sub(start)

    fmt.Println(later.Format(time.RFC3339)) // 2025-04-18T14:30:05Z
    fmt.Println(elapsed, elapsed.Seconds())  // 5s 5
}
```

`time.Time` — конкретный момент; `time.Duration` — интервал между моментами, представленный отдельным типом на основе `int64` с единицей **наносекунда**. `Time.Add(Duration)` возвращает новый момент, а `Time.Sub(Time)` — длительность. В примере обе операции согласованы: разница равна `5s`.

Единицу указывают явно: `5*time.Second` и `2*time.Hour + 30*time.Minute` имеют тип `Duration`; «голое» значение `time.Duration(5)` означает **5 наносекунд**, не секунд или миллисекунд. `Seconds()` возвращает число секунд как `float64`; обычный `fmt.Println` напечатает целое значение `5`, а не обязательно `5.0`.

Для измерения прошедшего времени `start := time.Now(); elapsed := time.Since(start)` использует монотонную часть часов, если она есть у обоих значений. Время, созданное через `Date` или `Parse`, монотонной части не имеет; его `Sub` работает с календарным временем. Для сравнения одного момента в разных зонах и хранения даты см. Q15. [time.Duration](https://pkg.go.dev/time#Duration), [Time.Add](https://pkg.go.dev/time#Time.Add), [Time.Sub](https://pkg.go.dev/time#Time.Sub), [монотонные часы](https://pkg.go.dev/time#hdr-Monotonic_Clocks).

## Q14. Парсинг времени — особенности формата?

В `time.Parse` и `Time.Format` layout составляют из значений опорного момента **Mon Jan 2 15:04:05 MST 2006**. Для записи «год-месяц-день» нужен `"2006-01-02"`, а не плейсхолдеры `"YYYY-MM-DD"`. Время без указания зоны `time.Parse` трактует как UTC, а не как системный `time.Local`; если входная строка означает время в конкретной зоне, используйте `time.ParseInLocation`.

```go
import "time"

func parseUTC(value string) (string, error) {
    const layout = "2006-01-02 15:04:05"
    t, err := time.Parse(layout, value)
    if err != nil {
        return "", err
    }
    return t.Format(time.RFC3339), nil
}
```

Для `parseUTC("2025-04-18 14:30:00")` результат — `"2025-04-18T14:30:00Z"`; несовпадение строки с layout возвращает ошибку разбора. `15` означает час в 24-часовом формате, `03` — в 12-часовом; замена одного другим меняет смысл даже без ошибки компиляции. `time.RFC3339` содержит числовое смещение `Z07:00` и подходит для разбора и записи timestamp с явной зоной; `time.Time` в JSON сериализуется в формате RFC3339 с возможной дробной частью секунды.

Если строка `"2025-04-18 14:30:00"` описывает московское местное время, `Parse` с layout без зоны даст другой момент. `ParseInLocation(layout, value, loc)` интерпретирует такую строку в указанной `loc`; загрузку зоны и переходы между смещениями см. в Q15. Для внешних timestamp предпочитайте числовое смещение, поскольку сокращения зон вроде `MST` могут быть неоднозначны. [time.Parse](https://pkg.go.dev/time#Parse), [ParseInLocation](https://pkg.go.dev/time#ParseInLocation), [layout и RFC3339](https://pkg.go.dev/time#pkg-constants), [Time.MarshalJSON](https://pkg.go.dev/time#Time.MarshalJSON).

## Q15. Часовые пояса и UTC?

`time.Time` представляет момент времени и связанную с ним `Location`. Метод `In` меняет календарное представление момента, но не сам момент. Для сравнения моментов используйте `Equal`: оператор `==` дополнительно сравнивает `Location` и монотонную часть времени.

```go
func zoneExample() error {
    loc, err := time.LoadLocation("America/New_York")
    if err != nil {
        return err
    }

    utc := time.Date(2025, time.April, 18, 14, 30, 0, 0, time.UTC)
    local := utc.In(loc)
    fmt.Println(local.Format(time.RFC3339)) // 2025-04-18T10:30:00-04:00
    fmt.Println(utc.Equal(local))            // true
    fmt.Println(utc == local)                 // false: Location отличается
    return nil
}
```

`time.Now()` берёт текущий момент и возвращает его в системной `time.Local`; `UTC()` меняет представление этого же момента. Разный пояс серверов сам по себе не меняет момент, но меняет строковое представление. Для обмена моментами между сервисами и хранения timestamp удобно использовать UTC или формат с явным смещением. Если правило задано местным временем («каждый день в 09:00 в Нью-Йорке»), сохраните и IANA-зону: одного UTC-момента или фиксированного смещения недостаточно для будущих повторений.

`LoadLocation` возвращает ошибку, если данные зоны недоступны. Он ищет их в `ZONEINFO`, системной базе, `$GOROOT/lib/time/zoneinfo.zip` и импортированном `time/tzdata`; для контейнера без этих данных можно включить `time/tzdata`. На переходе летнего времени местные часы могут пропустить или повторить значение: `time.Date` не обещает выбрать конкретный момент для неоднозначного местного времени. При приёме такой записи определите правило выбора с источником данных. [time.Time](https://pkg.go.dev/time#Time), [Time.Equal](https://pkg.go.dev/time#Time.Equal), [LoadLocation](https://pkg.go.dev/time#LoadLocation), [time.Date](https://pkg.go.dev/time#Date).

## Q16. Таймеры: time.NewTimer, time.Tick, time.After — в чём разница?

`NewTimer` даёт одно событие и управление через `Stop`/`Reset`. `After` возвращает только канал одноразового таймера. `NewTicker` даёт повторяющиеся события и позволяет вызвать `Stop`; `Tick` возвращает только канал тикера.

```go
func waitOnce(done <-chan struct{}) {
    select {
    case <-time.After(5 * time.Second):
        fmt.Println("timeout")
    case <-done:
        return
    }
}

func waitWithTimer(done <-chan struct{}) {
    timer := time.NewTimer(5 * time.Second)
    defer timer.Stop()
    select {
    case <-timer.C:
        fmt.Println("timeout")
    case <-done:
        return
    }
}
```

Обе функции выше — альтернативы для одного локального ожидания. Для периодической работы используйте `ticker := time.NewTicker(d)` и `defer ticker.Stop()`, когда нужно остановить тики при выходе. `time.Tick(d)` подходит, если нужен только канал и отдельная остановка не требуется; при `d <= 0` он возвращает `nil`, тогда как `NewTicker` паникует. Тики могут быть пропущены, если получатель отстаёт. `Ticker.Stop` прекращает тики и оставляет канал открытым. Для выхода из цикла чтения используйте отдельный сигнал отмены.

Начиная с Go 1.23 недостижимые таймеры и тикеры могут собираться GC до срабатывания; `time.After` в цикле уже нельзя автоматически называть утечкой. Повторное создание таймеров в часто выполняемом цикле всё же может давать лишние выделения памяти. Переиспользуйте `NewTimer` с `Reset`, когда это оправдано измерениями или когда нужен явный `Stop`. Пока тикер **достижим** и активен, `Stop` нужен, чтобы прекратить события.

При синхронной семантике канал таймера не буферизован, поэтому после возврата `Stop` или `Reset` из `timer.C` не придёт устаревшее значение от прежней настройки. Безусловный `<-timer.C` после `Stop` может заблокироваться, поэтому в этом режиме канал оставляют как есть. Для старой семантики безопасный `Reset` требует остановить таймер и при необходимости удалить старое значение из канала.

В Go 1.23–1.26 синхронная семантика по умолчанию зависела от версии `go` главного модуля (1.23 и выше). Её можно было включить через `GODEBUG=asynctimerchan=0` или вернуть старое поведение через `GODEBUG=asynctimerchan=1`. С Go 1.27 переключатель удалён. Каналы таймеров и тикеров всегда синхронные, в том числе при сборке модуля со старой строкой `go`.

При миграции проверь явные настройки. В Go 1.27 команда `go` принимает `godebug asynctimerchan=0` в `go.mod` и `//go:debug asynctimerchan=0` в исходнике для совместимости. Значение `1` в этих директивах вызывает ошибку сборки. Если задать `GODEBUG=asynctimerchan=1` в окружении, программа завершится при старте. [Заметки к Go 1.27](https://go.dev/doc/go1.27) · [Настройки GODEBUG](https://go.dev/doc/godebug#go-127).

[time.After](https://pkg.go.dev/time#After), [Timer.Stop](https://pkg.go.dev/time#Timer.Stop), [Timer.Reset](https://pkg.go.dev/time#Timer.Reset), [time.Tick](https://pkg.go.dev/time#Tick), [Go 1.23 release notes](https://go.dev/doc/go1.23#timer).

## Q17. (!) context.Context — основные методы?

`context.Context` передаёт дедлайн, сигнал отмены и значения, относящиеся к запросу, через цепочку вызовов. Его методы — `Deadline()`, `Done()`, `Err()` и `Value(key)`. `Done()` возвращает канал, который закрывается при отмене; `Err()` после этого возвращает `context.Canceled` или `context.DeadlineExceeded`. У `Background()` канал `Done()` равен `nil`, потому что этот корневой контекст сам не отменяется.

```go
func waitForDone(parent context.Context, done <-chan struct{}) error {
    ctx, cancel := context.WithTimeout(parent, 5*time.Second)
    defer cancel()

    select {
    case <-ctx.Done():
        return ctx.Err()
    case <-done:
        return nil
    }
}
```

`Background()` подходит для корня программы и тестов; `TODO()` отмечает место, где нужный родительский контекст ещё не определён. Производные `WithCancel`, `WithTimeout` и `WithDeadline` создают дочерний контекст и возвращают `CancelFunc`. Родительская отмена распространяется вниз; дочерний дедлайн не может быть позже родительского. `WithValue` переносит только значения запроса между API: для ключа нужен собственный сравнимый тип, а обычные параметры операции передавайте явно.

Вызов `cancel()` отсоединяет дочерний контекст от родителя и останавливает связанный таймер. Его нужно вызвать по завершении работы на всех путях исполнения, даже если дедлайн когда-нибудь истечёт сам; типичный способ — `defer cancel()` сразу после создания. `go vet` проверяет потерянные функции отмены. При этом `cancel()` лишь подаёт сигнал: работа должна слушать `Done()` или передавать контекст API, которые умеют отменяться; функция отмены не ждёт остановки горутин. Пропуск `cancel()` может удерживать дочерний контекст и его потомков до отмены родителя, но сам по себе не доказывает утечку горутины. [context](https://pkg.go.dev/context), [CancelFunc](https://pkg.go.dev/context#CancelFunc), [go vet](https://pkg.go.dev/cmd/vet).

Сценарии распространения отмены разобраны также в [Go Concurrency Q23–Q25](go-concurrency-interview.md#q23--contextcontext--концепция).

## Q18. (!) Работа с ошибками: errors.New, errors.Is, errors.As?

`errors.New` создаёт новое значение ошибки. `fmt.Errorf` с `%w` добавляет контекст и сохраняет исходную ошибку для проверки через `errors.Is` и `errors.As`; `%v` вставляет только текст. `Is` ищет заданную ошибку в дереве причин, а `As` находит ошибку подходящего типа и записывает её в переданную переменную.

```go
var ErrNotFound = errors.New("not found")

type ValidationError struct {
    Field string
}

func (e *ValidationError) Error() string {
    return "invalid field: " + e.Field
}

func errorExample() {
    wrapped := fmt.Errorf("lookup: %w", ErrNotFound)
    fmt.Println(errors.Is(wrapped, ErrNotFound)) // true

    validation := fmt.Errorf("input: %w", &ValidationError{Field: "age"})
    var fieldErr *ValidationError
    if errors.As(validation, &fieldErr) {
        fmt.Println(fieldErr.Field) // age
    }
}
```

`errors.New` при каждом вызове возвращает отдельную ошибку, даже если текст одинаков. Поэтому известную ошибку объявляют один раз как sentinel. `errors.Is(err, target)` сравнивает узлы дерева с `target` и учитывает пользовательский метод `Is(error) bool`; это шире проверки «один и тот же указатель». `errors.As(err, &fieldErr)` ищет подходящий тип и записывает найденное значение в `fieldErr`; для указательного типа ошибки нужен адрес переменной (`**ValidationError`). Проверка одного текста ошибки ненадёжна.

Начиная с Go 1.26 для большинства таких проверок доступна типизированная `errors.AsType[*ValidationError](err)`, возвращающая значение и `bool` без отдельной целевой переменной. Для Go 1.13–1.25 используйте `errors.As`. Оборачивание через `%w` делает исходную причину частью публичного поведения API: вызывающий сможет проверять её через `Is`/`As`, поэтому не раскрывайте внутреннюю ошибку случайно. [errors.New/Is/As/AsType](https://pkg.go.dev/errors), [fmt.Errorf](https://pkg.go.dev/fmt#Errorf), [Go 1.13 errors](https://go.dev/blog/go1.13-errors).

## Q19. Объединение ошибок через errors.Join (Go 1.20+)?

`errors.Join` объединяет несколько независимых причин в одно значение ошибки. Он пропускает `nil` и возвращает `nil`, если ненулевых ошибок нет. Полученная ошибка реализует `Unwrap() []error`, поэтому `errors.Is` и `errors.As` обходят каждую ветвь дерева.

```go
func joinExample() {
    readErr := errors.New("read failed")
    closeErr := errors.New("close failed")
    joined := errors.Join(readErr, nil, closeErr)

    fmt.Println(errors.Is(joined, readErr), errors.Is(joined, closeErr)) // true true
    fmt.Println(errors.Unwrap(joined) == nil)                           // true
    fmt.Println(joined) // read failed, затем close failed с новой строки
}
```

Для `Join` обычная `errors.Unwrap(joined)` возвращает `nil`: эта функция раскрывает только одиночный `Unwrap() error`. Используйте `Is`/`As` для поиска внутри объединения. Например, накопленные ошибки валидации можно вернуть одной строкой `return errors.Join(errs...)`; пустой срез даст `nil`. `As` при нескольких подходящих причинах вернёт первую при обходе в глубину, поэтому порядок аргументов может влиять на выбранное значение.

С Go 1.20 несколько `%w` в `fmt.Errorf` тоже создают дерево причин, но текст ошибки задаётся строкой формата. `Join` соединяет сообщения переводом строки. Выбор зависит от того, нужен ли текст с контекстом или список самостоятельных проблем. [errors.Join/Unwrap](https://pkg.go.dev/errors#Join), [fmt.Errorf](https://pkg.go.dev/fmt#Errorf), [Go 1.20 release notes](https://go.dev/doc/go1.20).

## Q20. (!) log/slog (structured logging) — что это?

`log/slog` появился в Go 1.21. Он записывает сообщение, уровень и типизированные атрибуты с ключами: сборщик логов может фильтровать записи по полям, если выбран подходящий формат вывода.

Из чего он состоит:

- **Logger** принимает сообщение и атрибуты: парами `"user_id", 42` или через `slog.Int`, `slog.String` и другие конструкторы `Attr`. `With` создаёт логгер с постоянными атрибутами.
- **Handler** проверяет уровень и обрабатывает запись. `TextHandler` пишет пары `key=value`, `JSONHandler` — JSON-объект; оба получают `io.Writer`. `HandlerOptions` задаёт уровень и, при необходимости, исходный файл и строку (`AddSource`).
- `slog.Group` группирует атрибуты: `JSONHandler` выводит вложенный объект, `TextHandler` добавляет имя группы к ключам.

```go
package main

import (
    "context"
    "log/slog"
    "os"
)

func main() {
    logger := slog.New(slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{
        Level: slog.LevelInfo,
    }))
    ctx := context.Background()
    logger.InfoContext(ctx, "request complete",
        slog.String("route", "/users"),
        slog.Group("response", slog.Int("status", 200)),
    )
}
```

`InfoContext` передаёт `ctx` обработчику; **стандартные** `TextHandler` и `JSONHandler` сами не извлекают из него trace ID. Для этого нужен собственный обработчик либо явный атрибут из контекста. Отмена `ctx` не подавляет запись. Если код использует пакетные вызовы `slog.Info`, можно настроить `slog.SetDefault(logger)` при запуске приложения; это также перенаправит вызовы стандартного `log.Print` в обработчик нового логгера. Если логгер передают в компоненты явно, глобальная настройка не обязательна. [log/slog](https://pkg.go.dev/log/slog), [Go blog: Structured Logging with slog](https://go.dev/blog/slog).

## Q21. Чем выбрать логгер: log vs slog vs zap vs logrus?

| Пакет | Возможности и причина выбора |
|-------|-----------------------------|
| `log` | Простой стандартный вывод строк без встроенных уровней и атрибутов. Подходит, если этого хватает потребителям логов. |
| `log/slog` | Стандартный API структурированных записей с уровнями и обработчиками, доступный с Go 1.21. Удобная отправная точка для нового кода на этой версии Go. |
| `zap` | Сторонний логгер с типизированными полями (`Logger`) и более удобным `SugaredLogger`; его авторы оптимизируют горячий путь. |
| `zerolog` | Сторонний структурированный логгер с цепочкой вызовов и акцентом на малое число аллокаций. |
| `logrus` | Сторонний структурированный логгер. Проект официально находится в maintenance mode: поддерживаются исправления и совместимость, новые функции не планируются. |

Для выбора уточните формат, сборщик логов, существующие зависимости и требования команды. Если сервис уже использует `zap` или `logrus`, миграция сама по себе не даёт измеримой пользы: API и интеграции придётся менять. Если логирование действительно заметно в профиле нагрузки, сравните выбранные конфигурации на своих сообщениях, полях и `io.Writer`: опубликованные бенчмарки пакетов не задают универсальный рейтинг. `slog.Handler` даёт точку интеграции с API `slog`, но не делает все пакеты автоматически взаимозаменяемыми; отдельный интерфейс приложения нужен лишь при реальной границе между компонентами. [log](https://pkg.go.dev/log), [log/slog](https://pkg.go.dev/log/slog), [zap](https://github.com/uber-go/zap), [zerolog](https://github.com/rs/zerolog), [logrus](https://github.com/sirupsen/logrus).

## Q22. (!) Как работает database/sql?

`*sql.DB` — конкурентно безопасный handle к пулу соединений, а не одно соединение. `database/sql` управляет запросами, транзакциями и пулом; конкретный протокол и формат плейсхолдеров предоставляет драйвер. `sql.Open` может не открыть соединение сразу — готовность проверяют `PingContext`.

Для серверного запроса передавайте `ctx` от HTTP request (при необходимости с более коротким timeout) в `QueryContext`/`QueryRowContext`/`ExecContext`. Отмена поддерживается через контекст и драйвер; дедлайн HTTP не заменяет лимиты БД и контроль пула.

```go
func userNames(ctx context.Context, db *sql.DB) ([]string, error) {
    rows, err := db.QueryContext(ctx, "SELECT name FROM users WHERE active = $1", true)
    if err != nil { return nil, err }
    defer rows.Close()

    names := []string{}
    for rows.Next() {
        var name string
        if err := rows.Scan(&name); err != nil { return nil, err }
        names = append(names, name)
    }
    if err := rows.Err(); err != nil { return nil, err }
    return names, nil
}
```

`QueryRowContext` возвращает ненулевой `*sql.Row` без отдельного значения `error`. Проверяйте результат `Scan`: он возвращает ошибку запроса или чтения, в том числе `sql.ErrNoRows` при пустом результате. Начиная с Go 1.15 обёртка может проверить уже полученную ошибку запроса через `Row.Err()` до чтения. Даже если `Row.Err()` вернул `nil`, вызовите `Scan`, чтобы прочитать строку и узнать результат. Если найдено несколько строк, `Scan` читает первую и отбрасывает остальные. Например, для учётной записи, которую ищут по email, уникальность должна обеспечивать схема БД. [Go: Row.Err](https://pkg.go.dev/database/sql#Row.Err), [Go: Row.Scan](https://pkg.go.dev/database/sql#Row.Scan).

После цикла **обязательно проверяйте `Rows.Err()`**: ошибка может возникнуть во время итерации. Если `Rows.Next()` вернул `false` и следующих наборов результатов нет, строки закрываются автоматически. При этом `defer rows.Close()` освобождает ресурсы и при раннем выходе из-за `Scan` или другой ошибки. Для PostgreSQL при вставке ID обычно используют `INSERT ... RETURNING id` + `QueryRowContext(...).Scan(&id)`: `Result.LastInsertId()` поддерживают не все драйверы. Значения передавайте параметрами (`$1` у PostgreSQL, `?` у ряда других драйверов), не собирайте SQL конкатенацией. [Go database querying](https://go.dev/doc/database/querying), [cancel operations](https://go.dev/doc/database/cancel-operations).

Если запись найдена, но её поле `nickname` содержит SQL `NULL`, сканирование в `*string` (например, `Scan(&nickname)` для переменной `var nickname string`) вернёт ошибку преобразования. Начиная с Go 1.22 можно объявить `var nickname sql.Null[string]` и передать `&nickname` в `Scan`. После успешного чтения `Valid == false` означает SQL `NULL`, а `Valid == true` и `V == ""` означают сохранённую пустую строку. Если запрос не вернул ни одной строки, `Scan` вернёт `sql.ErrNoRows`. Для более ранних версий Go используйте `sql.NullString` с полями `Valid` и `String`. [Go: Null](https://pkg.go.dev/database/sql#Null), [Go: преобразования Scan](https://go.dev/src/database/sql/convert.go).
**Байты из строки запроса.** Сервис читает бинарные значения и собирает их для обработки после закрытия результата. Если передать в `Rows.Scan` указатель на `sql.RawBytes` и сложить срезы в список, список сохранит ссылки на временную память. После следующего `Next`, `Scan` или `Close` эти данные больше нельзя использовать. Сканируйте каждую строку в `var payload []byte` через `rows.Scan(&payload)`: копия принадлежит вызывающему коду и остаётся доступной после закрытия результата. Если обработка выполняется сразу, можно прочитать `RawBytes` до следующей операции с результатом. Для передачи в другую горутину сделайте копию до того, как продолжите обход. [Go: RawBytes](https://pkg.go.dev/database/sql#RawBytes), [Go: Rows.Scan](https://pkg.go.dev/database/sql#Rows.Scan).


## Q23. Connection pool в Go?

`*sql.DB` уже управляет пулом и безопасен для совместного использования goroutine. Создавайте handle на жизненный цикл приложения, а не при каждом запросе; затем настройте пределы с учётом числа реплик сервиса и лимита БД:

```go
db.SetMaxOpenConns(25)
db.SetMaxIdleConns(5)
db.SetConnMaxLifetime(5 * time.Minute)
db.SetConnMaxIdleTime(time.Minute)
```

`MaxOpenConns` ограничивает одновременно открытые соединения; если предел достигнут, новый запрос **ждёт** доступное соединение, поэтому контекст с дедлайном нужен и при ожидании пула. Малый лимит может превратить пул в узкое место, большой — перегрузить сервер БД. Ноль или отрицательное значение `SetMaxOpenConns` означает отсутствие лимита. `SetMaxIdleConns` задаёт число сохраняемых свободных соединений, но не создаёт их заранее и не увеличивает общий лимит: если оно выше положительного `MaxOpenConns`, эффективный idle-предел снижается до него. Значение не больше нуля отключает сохранение idle-соединений. Lifetime и idle time ограничивают переиспользование старых соединений; истёкшее соединение может закрываться перед следующим использованием, а не точно в момент достижения лимита.

Диагностика — `stats := db.Stats()`: смотрите `OpenConnections`, `InUse`, `Idle`, рост `WaitCount` и `WaitDuration` во времени. Большое ожидание при `InUse` около лимита — повод проверить длительные запросы, незакрытые `Rows`, незавершённые транзакции и размер пула. `InUse` и `Idle` показывают состояние на момент замера, а `WaitCount` и `WaitDuration` накапливаются за время жизни этого `*sql.DB`. Сравнивайте приращения за один интервал. `WaitCount` увеличивается при входе в очередь, а `WaitDuration` учитывает время при выходе из неё, включая отмену через контекст. Из-за ожиданий на границах интервала отношение приращений даёт лишь приближение и не показывает p95. [Go: DBStats](https://pkg.go.dev/database/sql#DBStats), [исходник database/sql](https://go.dev/src/database/sql/sql.go).

**Соединения часто закрываются, хотя очередь почти не растёт.** После каждой волны запросов сервис снова устанавливает много соединений. Сравните приращения трёх счётчиков за тот же интервал. `MaxIdleClosed` растёт, когда пул закрывает лишние свободные соединения из-за лимита их числа. `MaxIdleTimeClosed` учитывает закрытия по времени простоя, а `MaxLifetimeClosed` — по общему возрасту соединения. Если после коротких всплесков быстро растёт только `MaxIdleClosed`, проверьте, сколько соединений возвращается в пул и хватает ли idle-лимита для следующей волны. Измерьте частоту новых подключений и задержку при следующем всплеске. Эти три счётчика охватывают закрытия по настройкам пула. Закрытие соединения из-за ошибки драйвера может в них не отразиться. [Go: DBStats](https://pkg.go.dev/database/sql#DBStats), [реализация database/sql](https://go.dev/src/database/sql/sql.go).

**Когда пул повторяет операцию.** Допустим, сервер закрыл простаивающее соединение, а драйвер обнаружил это до отправки `UPDATE`. Драйвер может вернуть `driver.ErrBadConn`, и `database/sql` попробует выполнить вызов `db.ExecContext` на другом соединении. Если связь оборвалась после отправки и сервер мог выполнить запрос, контракт драйвера запрещает сигнализировать `ErrBadConn` для такого повтора: тот же `UPDATE` мог бы примениться дважды. Операции внутри `*sql.Tx` привязаны к одному соединению, поэтому пул не переносит начатую транзакцию на другое. Как обработать неизвестный исход `Commit`, разобрано в Q25. [Go: driver.ErrBadConn](https://pkg.go.dev/database/sql/driver#ErrBadConn), [реализация database/sql](https://go.dev/src/database/sql/sql.go).

**Сценарий диагностики.** Если под нагрузкой `InUse` держится у положительного `MaxOpenConnections`, SQL на сервере выполняется быстро, а время ожидания в пуле растёт, проверьте, когда приложение закрывает `Rows` и завершает транзакции. Например, обработчик может обращаться к внешнему сервису до закрытия строк и всё это время удерживать соединение. Вынесите внешний вызов за пределы чтения строк или транзакции, если это сохраняет семантику, и повторите замер при той же нагрузке. Рост ожидания также возможен при медленном SQL, поэтому сравнивайте обе метрики перед изменением лимита. Сценарий с одним соединением разобран в Q25. [Managing connections](https://go.dev/doc/database/manage-connections), [`DB.Stats`](https://pkg.go.dev/database/sql#DB.Stats).

**Когда нужно закрепить соединение.** Сервис создаёт временную таблицу PostgreSQL одним вызовом `db.ExecContext`, а следующим пытается её прочитать. Второй вызов может попасть в другую сессию и не увидеть таблицу. При прямом подключении к PostgreSQL можно взять `conn, err := db.Conn(ctx)` и после успешного получения выполнять обе операции через `conn`. Этот handle занимает место в пуле до `conn.Close()`. Закрытие handle возвращает соединение в пул и само по себе не гарантирует завершения сессии, поэтому временную таблицу нужно удалить до возврата соединения. Если все действия входят в одну транзакцию, используйте `*sql.Tx` и создайте таблицу с `ON COMMIT DROP`. [Go: DB.Conn](https://pkg.go.dev/database/sql#DB.Conn), [PostgreSQL 18: CREATE TABLE](https://www.postgresql.org/docs/18/sql-createtable.html).

## Q24. (!) Зачем нужны prepared statements?

`db.PrepareContext(ctx, query)` создаёт `*sql.Stmt` для повторного выполнения параметризованного запроса; закрывайте его, когда он больше не нужен. Если statement используется только внутри одной функции, подойдёт `defer stmt.Close()`. Если statement подготовлен на `*sql.DB`, он остаётся пригодным для работы с пулом и при необходимости готовится на новых underlying connections. Statement, подготовленный на `*sql.Tx` или `*sql.Conn`, привязан к их соединению.

Контекст в `PrepareContext` ограничивает **подготовку**, а не все будущие выполнения: для отдельного вызова передайте его в `stmt.ExecContext`/`QueryContext`/`QueryRowContext`. Если подготовленный на `db` statement нужен внутри транзакции, используйте `tx.StmtContext(ctx, stmt)` и выполняйте запрос через полученный statement; прямой вызов исходного `stmt` не станет частью `tx`. Транзакционный statement закрывается при `Commit` или `Rollback`, а исходный `db`-statement закрывайте отдельно, когда закончили им пользоваться.

**Сценарий с транзакцией.** Сервис заранее подготовил `updateStmt` через `db.PrepareContext`. При записи заказа вместе с аудитом он вызывает `tx.StmtContext(ctx, updateStmt).ExecContext(ctx, status, orderID)`. Если вторая запись завершится ошибкой, откат этой транзакции отменит и обновление заказа. При прямом вызове `updateStmt.ExecContext` обновление выполняется через пул вне `tx`, поэтому откат его не отменит. Если транзакция заняла единственное соединение, прямой вызов может вообще ждать освобождения пула, как в Q25.

При привязке через `tx.StmtContext` выражение может потребоваться подготовить на соединении транзакции. Переданный туда контекст относится к подготовке. Сам метод возвращает только `*sql.Stmt`, поэтому ошибку подготовки проверяют при выполнении через `ExecContext` или `QueryContext`. Для `QueryRowContext` её возвращает `Scan`. [Go: Tx.StmtContext](https://pkg.go.dev/database/sql#Tx.StmtContext), [исходник database/sql](https://go.dev/src/database/sql/sql.go).

Параметры передают отдельно от SQL-текста, поэтому **явный `Prepare` не требуется только ради защиты от injection**: `db.QueryContext(ctx, query, args...)` тоже принимает параметры. Не утверждайте, что каждый драйвер всегда сам кэширует/готовит statement или что СУБД непременно строит план ровно один раз: детали зависят от драйвера и базы. Явный statement полезен, когда один запрос многократно выполняется и измерения показывают выгоду; имена таблиц/колонок параметрами не заменяются, их выбирают из allowlist. [Prepared statements](https://go.dev/doc/database/prepared-statements), [`Stmt`](https://pkg.go.dev/database/sql#Stmt).

## Q25. Транзакции и идиома deferred Rollback?

`BeginTx` закрепляет одно соединение за транзакцией. При `nil` вместо `TxOptions` действуют настройки по умолчанию. В PostgreSQL уровень по умолчанию задаёт `default_transaction_isolation`, а пример перевода ниже явно запрашивает `READ COMMITTED`. Если драйвер не поддерживает заданный уровень, `BeginTx` возвращает ошибку. [Go: BeginTx](https://pkg.go.dev/database/sql#DB.BeginTx), [PostgreSQL 18: настройки транзакций](https://www.postgresql.org/docs/18/runtime-config-client.html#GUC-DEFAULT-TRANSACTION-ISOLATION). Все связанные запросы выполняйте через `tx`, иначе вызов `db.ExecContext` может попасть на другое соединение и пройти вне транзакции.

**Пример с исчерпанным пулом.** В тесте задано `db.SetMaxOpenConns(1)`. Обработчик начинает `tx` и вызывает функцию, которая читает счёт через `db.QueryRowContext(...).Scan(...)`. Транзакция уже держит единственное соединение, чтение ждёт выдачи соединения из пула, а обработчик ждёт чтения перед `Commit`. Если транзакция остаётся открытой, дедлайн контекста чтения завершит ожидание с ошибкой. При большем пуле вызов может пройти, но чтение всё равно будет отдельной операцией и в PostgreSQL не увидит незакоммиченные изменения этого `tx`. Передайте функции `*sql.Tx` и вызовите `tx.QueryRowContext(...).Scan(...)`: это использует закреплённое за транзакцией соединение и видит её собственные изменения. Дедлайн отдельного запроса по-прежнему задаёт переданный в него `ctx`. [Go: Managing connections](https://go.dev/doc/database/manage-connections), [Go: Executing transactions](https://go.dev/doc/database/execute-transactions).

В примере для PostgreSQL `accounts.id` — первичный ключ, `balance` — целое число денежных единиц минимального номинала с ограничениями `NOT NULL` и `CHECK (balance >= 0)`. Внутри функции проверяем сумму и оба результата обновления.

```go
if amount <= 0 || fromID == toID {
    return fmt.Errorf("invalid transfer")
}

tx, err := db.BeginTx(ctx, &sql.TxOptions{Isolation: sql.LevelReadCommitted})
if err != nil {
    return err
}
defer tx.Rollback()

var updatedID int64
if err := tx.QueryRowContext(ctx, `
    UPDATE accounts SET balance = balance - $1
    WHERE id = $2 AND balance >= $1
    RETURNING id`, amount, fromID).Scan(&updatedID); err != nil {
    return fmt.Errorf("debit source account: %w", err)
}

if err := tx.QueryRowContext(ctx, `
    UPDATE accounts SET balance = balance + $1
    WHERE id = $2
    RETURNING id`, amount, toID).Scan(&updatedID); err != nil {
    return fmt.Errorf("credit destination account: %w", err)
}
return tx.Commit()
```

При `READ COMMITTED` конкурентное обновление той же строки может заставить списание ждать. После фиксации конкурента PostgreSQL повторно проверит условие `balance >= $1` на обновлённой строке. Если счёт отправителя отсутствует или средств недостаточно, первое `Scan` вернёт `sql.ErrNoRows`. Если счёт получателя отсутствует, второе `Scan` вернёт ту же ошибку, а отложенный откат отменит уже выполненное списание. [PostgreSQL 18: UPDATE](https://www.postgresql.org/docs/18/sql-update.html), [уровни изоляции](https://www.postgresql.org/docs/18/transaction-iso.html), [Go: Row.Scan](https://pkg.go.dev/database/sql#Row.Scan).

`defer Rollback` закрывает ранние выходы; после успешного `Commit` отложенный вызов вернёт `sql.ErrTxDone`. **Ошибку `Commit` проверяют**: при ней результаты запросов внутри `tx` нельзя считать подтверждёнными. Встречные переводы могут взять блокировки счетов в противоположном порядке и столкнуться с deadlock. Если приложение повторяет прерванную транзакцию, оно начинает её заново через `BeginTx`. Если соединение оборвалось во время `Commit`, клиент может не узнать, зафиксировался ли перевод. Один из способов безопасного повтора — принимать стабильный ID операции и записывать его с ограничением уникальности в той же транзакции, что и изменение балансов. При повторе с тем же ID уже зафиксированная запись позволяет вернуть прежний результат, сверив реквизиты. Если первая транзакция откатилась, её запись об операции тоже откатится и перевод можно выполнить заново. Пример выше показывает только атомарность перевода. Для безопасного повтора ему нужна эта дополнительная запись. [Transactions](https://go.dev/doc/database/execute-transactions), [`BeginTx`](https://pkg.go.dev/database/sql#DB.BeginTx).

**Два бюджета времени.** Передайте в `BeginTx` контекст на всю транзакцию, а для отдельного запроса при необходимости создайте его дочерний контекст с более коротким дедлайном. Если истёк только дедлайн запроса, это само по себе не запускает автоматический откат `*sql.Tx` в `database/sql`. Состояние транзакции после ошибки зависит от СУБД и драйвера. В примере перевода следует вернуть ошибку и дать отложенному `Rollback` завершить транзакцию. Если же отменён контекст из `BeginTx` до начала фиксации, пакет инициирует откат, а попытка `Commit` завершится ошибкой. Отмена после начала драйверного `Commit` не подтверждает откат: интерфейс `driver.Tx.Commit()` не принимает контекст. [Go: BeginTx](https://pkg.go.dev/database/sql#DB.BeginTx), [исходник Tx.Commit](https://go.dev/src/database/sql/sql.go), [Go: driver.Tx](https://pkg.go.dev/database/sql/driver#Tx).

## Q26. (!) io.Reader, io.Writer интерфейсы?

```go
type Reader interface {
    Read(p []byte) (n int, err error)
}

type Writer interface {
    Write(p []byte) (n int, err error)
}
```

`io.Reader` и `io.Writer` — базовые интерфейсы чтения и записи байтов, по одному методу в каждом. Функция, принимающая эти интерфейсы, работает с файлом, сетью или буфером без привязки к их конкретным типам.

`Read(p)` возвращает число прочитанных байтов и ошибку. Он может заполнить только часть `p` без ошибки или вернуть данные вместе с `io.EOF`; сначала обработайте `p[:n]`, затем `err`. Если нужно ровно `len(p)` байтов, используйте `io.ReadFull`. `Write(p)` при записи меньше `len(p)` обязан вернуть ненулевую ошибку.

Один и тот же интерфейс реализуют совершенно разные вещи:

- `os.File` — оба (и читать, и писать).
- `bytes.Buffer` — оба (буфер в памяти).
- `strings.Reader` — Reader поверх строки.
- `http.Response.Body` — `io.ReadCloser`: тело ответа нужно прочитать и закрыть.
- `bufio.Reader`/`bufio.Writer` — буферизованные обёртки над любым Reader/Writer.

Благодаря единому интерфейсу работают универсальные хелперы — им всё равно, что конкретно за `r`/`w`:

```go
written, err := io.Copy(dst, src) // потоково копировать до EOF или ошибки
data, err := io.ReadAll(r)        // прочитать весь поток в []byte
reader, writer := io.Pipe()       // связать чтение и запись
```

**Композиция** — главный приём: Reader/Writer оборачивают друг друга, как трубы. `gzip.NewWriter(file)` — сжатие поверх файла; `io.TeeReader` — читать и одновременно копировать в сторону; `io.MultiWriter` — писать сразу в несколько мест. Эта «сборка из кубиков» и есть идиоматичный Go-I/O.

`io.Copy` возвращает число записанных байтов и первую ошибку; обычный `io.EOF` завершает копирование без ошибки. `io.ReadAll` держит **весь** результат в памяти, поэтому для больших или неограниченных тел выбирайте потоковую обработку, а при обязательном чтении в память ограничивайте объём, например через `io.LimitReader`. Проверяйте ошибки чтения, записи и `bufio.Writer.Flush()`. [Пакет `io`](https://pkg.go.dev/io), [`bufio.Writer`](https://pkg.go.dev/bufio#Writer).

**Завершение цепочки записи.** Экспорт пишет через `gzip.NewWriter(bufio.NewWriter(file))`. Успешный `io.Copy` подтверждает передачу данных в gzip-обёртку. Прежде чем сообщить об успехе, вызовите `Close()` у gzip writer, чтобы он дописал сжатые данные и завершающую часть потока. Затем вызовите `Flush()` у сохранённого `bufio.Writer` и закройте файл. Проверяйте ошибки каждого шага. Если сбросить буфер перед закрытием gzip writer и больше его не сбрасывать, завершающие байты могут остаться в буфере. Закрытие gzip writer само по себе не закрывает файл. [Go: gzip.Writer.Close](https://pkg.go.dev/compress/gzip#Writer.Close), [Go: bufio.Writer.Flush](https://pkg.go.dev/bufio#Writer.Flush).

**Ограничить чтение и проверить размер — разные задачи.** Если импорт принимает файл до 1 МиБ, `io.ReadAll(io.LimitReader(r, 1<<20))` вернёт и файл ровно в 1 МиБ, и первые 1 МиБ более длинного файла без ошибки. Для проверки читайте один дополнительный байт. Сначала проверьте ошибку чтения, затем длину. Результат длиннее лимита отклоните целиком до обработки.

```go
const maxBytes = 1 << 20
data, err := io.ReadAll(io.LimitReader(r, maxBytes+1))
if err != nil {
    return fmt.Errorf("read import: %w", err)
}
if len(data) > maxBytes {
    return fmt.Errorf("import exceeds %d bytes", maxBytes)
}
// Теперь data содержит весь принятый файл.
```

Для тела входящего HTTP-запроса `http.MaxBytesReader` возвращает `*http.MaxBytesError`, когда чтение обнаруживает превышение. Если парсер останавливается после первого значения, дочитайте до конца либо проверьте отсутствие хвоста по правилам формата, иначе превышение может остаться незамеченным. [`io.LimitReader`](https://pkg.go.dev/io#LimitReader), [`http.MaxBytesReader`](https://pkg.go.dev/net/http#MaxBytesReader).

## Q27. Почему ioutil устарел (Go 1.16+)?

С Go 1.16 пакет `io/ioutil` устарел: его функции распределены между `io` и `os`. Пакет сохранили для совместимости, а новый код использует функции из этих пакетов. Соответствие имён:

| Старое | Новое |
|--------|-------|
| `ioutil.ReadFile` | `os.ReadFile` |
| `ioutil.WriteFile` | `os.WriteFile` |
| `ioutil.ReadAll` | `io.ReadAll` |
| `ioutil.ReadDir` | `os.ReadDir` |
| `ioutil.TempFile` | `os.CreateTemp` |
| `ioutil.TempDir` | `os.MkdirTemp` |
| `ioutil.NopCloser` | `io.NopCloser` |
| `ioutil.Discard` | `io.Discard` |

Большинство замен сохраняет сигнатуру и поведение, например `ioutil.ReadAll` → `io.ReadAll` (обе принимают `io.Reader`) и `ioutil.ReadFile` → `os.ReadFile`. Для `ReadDir` замена требует проверки кода: старый вызов возвращает `[]fs.FileInfo`, а `os.ReadDir` — `[]fs.DirEntry`; если нужны метаданные, вызывайте `entry.Info()` и проверяйте его ошибку. Если чтение каталога прервалось, `os.ReadDir` может вернуть уже прочитанные записи вместе с ошибкой. Старый `ioutil.ReadDir` в этом случае возвращает только ошибку и пустой результат. Например, если список файлов нужен для полной сверки каталога, при ошибке остановите сверку, иначе частичный список может выглядеть как удаление файлов. `os.ReadFile` тоже читает весь файл в память. Устаревший код продолжает компилироваться, а предупреждение зависит от используемых инструментов. Замены доступны с Go 1.16; документация помечает `ioutil.TempFile` и `ioutil.TempDir` устаревшими с Go 1.17. [Go 1.16 release notes](https://go.dev/doc/go1.16#ioutil), [`io/ioutil`](https://pkg.go.dev/io/ioutil), [`os.ReadDir`](https://pkg.go.dev/os#ReadDir).

## Q28. (!) //go:embed для статических файлов?

Директива `//go:embed` (Go 1.16+) помещает содержимое локальных файлов в программу **при сборке**. Так можно поставлять шаблоны, миграции и статические ресурсы вместе с исполняемым файлом. Для меняющихся во время работы данных оставьте обычное чтение файла: встроенное содержимое обновится только после новой сборки.

```go
package main

import (
    "embed"
    "io/fs"
    "log"
    "net/http"
)

//go:embed config.yaml
var configData []byte

//go:embed static
var assets embed.FS

func main() {
    static, err := fs.Sub(assets, "static")
    if err != nil {
        log.Fatal(err)
    }
    http.Handle("/static/", http.StripPrefix("/static/", http.FileServer(http.FS(static))))
    log.Fatal(http.ListenAndServe(":8080", nil))
}
```

Пример предполагает `config.yaml` и каталог `static` рядом с исходным файлом пакета. `fs.Sub` ограничивает HTTP-обработчик только каталогом `static`; `http.StripPrefix` убирает префикс URL перед поиском файла. Не передавайте в файловый сервер `embed.FS`, содержащую конфиги или другие внутренние файлы, целиком.

Правила встраивания:

- Директива относится к одной переменной уровня пакета. Между ней и объявлением допустимы пустые строки и комментарии `//`; импорт `embed` обязателен, даже если используется только `string` или `[]byte` (тогда нужен `import _ "embed"`).
- `string` и `[]byte` получают содержимое **ровно одного файла**. Для дерева файлов или нескольких совпадений шаблона используйте `embed.FS`; он реализует `fs.FS` и подходит для `fs.ReadDir`, `template.ParseFS` и `http.FS`.
- Шаблоны задаются относительно каталога пакета. Выход через `../`, абсолютные пути и совпадения вне модуля запрещены; отсутствие подходящего файла завершает сборку ошибкой. При встраивании каталога файлы с именами на `.` или `_` обычно пропускаются; префикс `all:` включает их.

**Сценарии применения:**

- **Single-binary деплой** — один исполняемый файл, никаких внешних ассетов рядом.
- **Шаблоны, SQL-миграции, конфиги по умолчанию** — то, что должно ехать вместе с кодом.
- **Статика веб-приложения** (HTML/CSS/JS) внутри бинаря.

**Подводные камни:** `embed.FS` доступна только для чтения. Байты в `configData` из примера можно менять в памяти по обычным правилам слайсов. Такое изменение живёт в процессе и исчезнет после перезапуска. Большие встроенные файлы увеличивают размер артефакта. Публикация всей встроенной FS через `http.FileServer` может открыть внутренние ресурсы, поэтому выделяйте только предназначенное для HTTP поддерево. [Пакет `embed`](https://pkg.go.dev/embed), [`fs.Sub`](https://pkg.go.dev/io/fs#Sub), [`http.FS`](https://pkg.go.dev/net/http#FS), [Go 1.16 release notes](https://go.dev/doc/go1.16#embed).

## Q29. Чем отличается sort.Slice от sort.Sort?

Оба способа меняют данные **на месте** и по умолчанию не сохраняют порядок равных элементов. Разница — в контракте: `sort.Slice` принимает срез и функцию `less(i, j)`, которая сравнивает элементы по индексам; `sort.Sort` принимает значение с методами `Len`, `Less` и `Swap` (`sort.Interface`). Последний подходит и для собственной структуры данных, не обязательно для среза.

```go
package main

import "sort"

type Person struct{ Age int }
type byAge []Person

func (p byAge) Len() int           { return len(p) }
func (p byAge) Less(i, j int) bool { return p[i].Age < p[j].Age }
func (p byAge) Swap(i, j int)      { p[i], p[j] = p[j], p[i] }

func orderPeople(people []Person) {
    sort.Slice(people, func(i, j int) bool { return people[i].Age < people[j].Age })
    // Альтернативный способ с переиспользуемым типом:
    sort.Sort(byAge(people))
}

func main() {
    orderPeople([]Person{{Age: 30}, {Age: 20}})
}
```

Оба вызова в примере показаны для сравнения; в рабочем коде выбирают один. Для разовой сортировки среза `sort.Slice` короче; тип с `sort.Interface` полезен, если тот же порядок нужен в нескольких местах или уже является частью API. Начиная с Go 1.21 для типизированного среза часто удобнее `slices.SortFunc`: его компаратор получает **значения**, возвращает отрицательное число, ноль или положительное число, а не `bool` по индексам. Компаратор должен задавать строгий слабый порядок. Например, при сортировке измерений типа `float64` простое `a < b` приравняет `NaN` и к 1, и к 2 по правилам сортировки, хотя 1 и 2 различаются. Для числового поля используйте `cmp.Less(a, b)` с `sort.Slice` или `cmp.Compare(a, b)` с `slices.SortFunc`. Оба ставят `NaN` перед остальными числами при сортировке по возрастанию. Если по правилам отчёта пропуски идут в конце, задайте этот порядок явно. [Контракт `sort.Interface`](https://pkg.go.dev/sort#Interface), [пакет `cmp`](https://pkg.go.dev/cmp).

Если порядок равных элементов важен, выбирайте `sort.SliceStable`, `sort.Stable` или `slices.SortStableFunc`. Ни `sort.Slice`, ни `sort.Sort`, ни `slices.SortFunc` стабильность не гарантируют. Производительность конкретного способа измеряйте на своих данных: документация отмечает, что `slices.SortFunc` во многих сценариях удобнее и быстрее, но не обещает фиксированного преимущества `sort.Sort` над `sort.Slice`. [Пакет `sort`](https://pkg.go.dev/sort), [`slices.SortFunc`](https://pkg.go.dev/slices#SortFunc).

## Q30. (!) slices пакет в Go 1.21+?

Пакет `slices` появился в стандартной библиотеке Go 1.21 и предоставляет обобщённые операции со срезами. `Sort` сортирует срез элементов, удовлетворяющих `cmp.Ordered`; для структур и собственного порядка используйте `SortFunc` с компаратором по **значениям**. В отличие от `sort.Slice` с `less(i, j) bool` по индексам, `SortFunc` получает два элемента и возвращает `int`. Обе функции меняют исходный срез, а `SortFunc` не гарантирует стабильность.

```go
package main

import (
    "cmp"
    "slices"
)

type Person struct{ Age int }

func main() {
    original := []int{3, 1, 2}
    ordered := slices.Clone(original) // поверхностная копия
    slices.Sort(ordered)              // ordered: [1 2 3], original не меняется
    _ = slices.Contains(ordered, 2)   // true

    people := []Person{{Age: 30}, {Age: 20}}
    slices.SortFunc(people, func(a, b Person) int {
        return cmp.Compare(a.Age, b.Age)
    })
}
```

`SortFunc` ожидает отрицательный результат для `a < b`, ноль для равных и положительный для `a > b`; компаратор должен задавать согласованный порядок. `cmp.Compare` для числового поля безопаснее вычитания, которое может переполниться. Для сохранения порядка равных элементов есть `slices.SortStableFunc`. `slices.Equal` сравнивает элементы по порядку и считает `nil` и пустой срез равными; `Clone` копирует элементы поверхностно, а не клонирует вложенные объекты.

**Удаление меняет длину.** Если из очереди `pending []*Job` удалили первый элемент, присвойте результат обратно: `pending = slices.Delete(pending, 0, 1)`. Функция сдвигает оставшиеся элементы в том же массиве. Если отбросить результат, у переменной останется старая длина. Другой срез того же массива тоже увидит изменения элементов и сохранит свою длину. Если ему нужна прежняя последовательность, сначала сделайте поверхностную копию через `slices.Clone`. Начиная с Go 1.22 `Delete` обнуляет освободившийся хвост до старой длины, поэтому сам хвост больше не удерживает ссылки на объекты. [Контракт `slices.Delete`](https://pkg.go.dev/slices#Delete), [разбор изменения в Go 1.22](https://go.dev/blog/generic-slice-functions).

У функций разные границы версий: базовые `Sort`, `SortFunc`, `Contains`, `Clone` и `Equal` есть с Go 1.21; `Concat` добавлен в Go 1.22, а `Collect` для сборки итератора в срез — в Go 1.23. Соседний вопрос по [Go generics Q25](go-generics-interview.md#q25--как-generics-используются-в-стандартной-библиотеке) отдельно разбирает `maps.Keys` и итераторы. Выбирайте API по типу элементов и требованию стабильности, а скорость проверяйте измерением. [Пакет `slices`](https://pkg.go.dev/slices), [Go 1.21 release notes](https://go.dev/doc/go1.21#slices).

---

## Q31. Когда выбрать encoding/json/v2 и как мигрировать с encoding/json?

С **Go 1.27** `encoding/json/v2` — отдельный API для типизированного JSON с настраиваемыми `Options`; `encoding/json/jsontext` — нижний уровень для чтения и записи синтаксических `Token`/`Value`. Если обычный `encoding/json` уже соответствует контракту API, переписывать код ради новой версии не требуется: в Go 1.27 он работает поверх новой реализации с сохранением прежней семантики (текст ошибок может измениться). При проблеме совместимости со старым импортом Go 1.27 временно позволяет вернуть прежнюю реализацию флагом сборки `GOEXPERIMENT=nojsonv2`. Это способ локализовать регрессию при обновлении Go. При включённом opt-out сборка с прямым импортом `encoding/json/v2` завершится ошибкой. Переход на импорт `encoding/json/v2` меняет правила API и требует отдельной проверки.

```go
import (
    jsonv2 "encoding/json/v2"
    "io"
)

type Request struct {
    ID string `json:"id"`
}

func decodeRequest(r io.Reader) (Request, error) {
    var req Request
    err := jsonv2.UnmarshalRead(r, &req, jsonv2.RejectUnknownMembers(true))
    return req, err
}
```

`UnmarshalRead` читает **одно** JSON-значение и весь `io.Reader` до EOF; лишнее значение после первого вызовет ошибку. Это подходит для одного тела HTTP-запроса, но не для NDJSON или соединения, где EOF пока не наступил. Для последовательности значений создайте `jsontext.Decoder` и вызывайте `jsonv2.UnmarshalDecode` по одному значению; `MarshalEncode` аналогично пишет в `jsontext.Encoder`. `MarshalWrite` пишет одно значение в `io.Writer`, а для готовых `[]byte` есть `Marshal`/`Unmarshal`. Если нужно самостоятельно разбирать синтаксис по токенам, используйте `jsontext.Decoder`/`Encoder` напрямую. См. также Q9 о потоковом API v1.

**Проверка миграции:** у v2 по умолчанию имена полей сопоставляются с учётом регистра, дублирующиеся ключи и некорректный UTF-8 отклоняются. Совместимость с дублирующимися именами можно включить явно через `jsontext.AllowDuplicateNames(true)`, но тогда позднее значение заменит или дополнит раннее в зависимости от Go-типа. Неизвестные поля по умолчанию по-прежнему игнорируются: `RejectUnknownMembers(true)` включает отказ явно. При кодировании nil slice/map v2 выдаёт `[]`/`{}` вместо `null` (для `[]byte` действует отдельное правило строки); опции `FormatNilSliceAsNull` и `FormatNilMapAsNull` возвращают `null`. `omitempty` ориентируется на пустое **JSON-значение**, поэтому для числового `0` и `false` поведение может отличаться от v1. Если нужен Go zero value, рассмотрите `omitzero`. Проверьте реальные входные payload и golden JSON, особенно регистр ключей, дубликаты, nil и теги, прежде чем менять импорт. [Go 1.27 release notes](https://go.dev/doc/go1.27#jsonv2), [json/v2 package](https://pkg.go.dev/encoding/json/v2), [jsontext package](https://pkg.go.dev/encoding/json/jsontext).

**Поле с тегом `,string`.** Если старый клиент ожидает `{"active":"true"}` для поля `Active bool` с тегом `json:"active,string"`, при смене импорта на v2 вызов `Marshal` в Go 1.27 вернёт ошибку: тег `,string` допустим только для поля, которое без этой опции кодировалось бы JSON-числом. Удаление тега даст `{"active":true}` и изменит контракт. Для `int64` с таким тегом число по-прежнему передаётся строкой. Если строковый контракт для булева поля нужно сохранить, оставьте v1 для этого API либо явно задайте преобразование в транспортной модели. При поэтапном переходе можно также передать `json.StringifyWithLegacySemantics(true)` из `encoding/json` в вызовы `jsonv2.Marshal` и `jsonv2.Unmarshal`. Эта опция возвращает прежнее поведение тега для затронутых полей, а остальные настройки v2 остаются в силе. [Опция совместимости](https://pkg.go.dev/encoding/json#StringifyWithLegacySemantics). [Тег в v1](https://pkg.go.dev/encoding/json#Marshal), [тег в v2](https://pkg.go.dev/encoding/json/v2#StringifyNumbers).

**Если тест сравнивает байты.** В v1 ключи map сортируются. В v2 порядок обхода map по умолчанию не гарантирован, поэтому один и тот же объект может дать разные байты. Если golden-тест проверяет значения полей, сравнивайте разобранный JSON. Если тесту нужна повторяемая сериализация в одной версии программы, используйте `jsonv2.Marshal(value, jsonv2.Deterministic(true))` и проверьте новые эталоны. Для протокола, который требует канонический JSON между реализациями, применяйте согласованный стандарт, например JCS через `jsontext.Value.Canonicalize`. [Порядок в v1](https://pkg.go.dev/encoding/json#Marshal), [Deterministic в v2](https://pkg.go.dev/encoding/json/v2#Deterministic), [канонизация JCS](https://pkg.go.dev/encoding/json/jsontext#Value.Canonicalize).

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
