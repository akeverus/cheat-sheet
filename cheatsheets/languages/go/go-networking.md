---
title: "Go: сетевые операции"
description: "Полное руководство по сетевым операциям в Go: TCP, UDP, HTTP клиент, сокеты, WebSocket"
tags:
  - go
  - golang
  - networking
  - tcp
  - udp
  - http
  - websocket
difficulty: "intermediate"
prerequisites: ["go/go-basics.md", "go/go-stdlib-http.md"]
updated: "2026-02-06"
---

# Go: сетевые операции

## Полезные ссылки

- [Go net Package](https://pkg.go.dev/net)
- [Go net/http](https://pkg.go.dev/net/http)

## Содержание

- [Go: сетевые операции](#go-сетевые-операции)
- [Введение в сетевые операции](#введение-в-сетевые-операции)
  - [Основные протоколы](#основные-протоколы)
- [**TCP** соединения](#tcp-соединения)
  - [**TCP** сервер](#tcp-сервер)
  - [**TCP** клиент](#tcp-клиент)
- [**UDP** соединения](#udp-соединения)
  - [**UDP** сервер](#udp-сервер)
  - [**UDP** клиент](#udp-клиент)
- [**HTTP** клиент](#http-клиент)
  - [Базовый **HTTP** клиент](#базовый-http-клиент)
  - [**HTTP** клиент с настройками](#http-клиент-с-настройками)
- [**WebSocket**](#websocket)
  - [**WebSocket** сервер](#websocket-сервер)
  - [**WebSocket** клиент](#websocket-клиент)
  - [Практические примеры: **TCP** сервер с пулом соединений](#практические-примеры-tcp-сервер-с-пулом-соединений)
  - [Практические примеры: **TCP** сервер с таймаутами](#практические-примеры-tcp-сервер-с-таймаутами)
  - [Практические примеры: **UDP multicast**](#практические-примеры-udp-multicast)
  - [Практические примеры: **HTTP** клиент с **retry**](#практические-примеры-http-клиент-с-retry)
  - [Практические примеры: **HTTP** клиент с **middleware**](#практические-примеры-http-клиент-с-middleware)
  - [Практические примеры: **WebSocket** с **heartbeat**](#практические-примеры-websocket-с-heartbeat)
  - [Практические примеры: **HTTP** клиент с **connection pooling**](#практические-примеры-http-клиент-с-connection-pooling)
  - [Практические примеры: **TCP proxy**](#практические-примеры-tcp-proxy)
  - [Практические примеры: **HTTP** клиент с **circuit breaker**](#практические-примеры-http-клиент-с-circuit-breaker)
  - [Практические примеры: **DNS resolver**](#практические-примеры-dns-resolver)
  - [Практические примеры: **HTTP** клиент с **rate limiting**](#практические-примеры-http-клиент-с-rate-limiting)
  - [Практические примеры: **Connection pooling**](#практические-примеры-connection-pooling)
  - [Практические примеры: **WebSocket** сервер](#практические-примеры-websocket-сервер)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в сетевые операции

Go предоставляет мощные инструменты для сетевых операций через пакеты `net` и `net/http`. Понимание сетевых операций критично для создания распределенных приложений.

### Основные протоколы

1. **TCP** — надежная передача данных
2. **UDP** — быстрая передача данных
3. **HTTP** — веб-протокол
4. **WebSocket** — двусторонняя связь

## TCP соединения

### TCP сервер

```go
import (
    "net"
    "fmt"
)

func tcpServer() error {
    listener, err := net.Listen("tcp", ":8080")
    if err != nil {
        return err
    }
    defer listener.Close()

    for {
        conn, err := listener.Accept()
        if err != nil {
            continue
        }

        go handleConnection(conn)
    }
}

func handleConnection(conn net.Conn) {
    defer conn.Close()

    buf := make([]byte, 1024)
    n, err := conn.Read(buf)
    if err != nil {
        return
    }

    response := fmt.Sprintf("Echo: %s", string(buf[:n]))
    conn.Write([]byte(response))
}
```

### TCP клиент

```go
import "net"

func tcpClient(message string) error {
    conn, err := net.Dial("tcp", "localhost:8080")
    if err != nil {
        return err
    }
    defer conn.Close()

    _, err = conn.Write([]byte(message))
    if err != nil {
        return err
    }

    buf := make([]byte, 1024)
    n, err := conn.Read(buf)
    if err != nil {
        return err
    }

    fmt.Println(string(buf[:n]))
    return nil
}
```

## UDP соединения

### UDP сервер

```go
import "net"

func udpServer() error {
    addr, err := net.ResolveUDPAddr("udp", ":8080")
    if err != nil {
        return err
    }

    conn, err := net.ListenUDP("udp", addr)
    if err != nil {
        return err
    }
    defer conn.Close()

    buf := make([]byte, 1024)
    for {
        n, clientAddr, err := conn.ReadFromUDP(buf)
        if err != nil {
            continue
        }

        response := fmt.Sprintf("Echo: %s", string(buf[:n]))
        conn.WriteToUDP([]byte(response), clientAddr)
    }
}
```

### UDP клиент

```go
import "net"

func udpClient(message string) error {
    serverAddr, err := net.ResolveUDPAddr("udp", "localhost:8080")
    if err != nil {
        return err
    }

    conn, err := net.DialUDP("udp", nil, serverAddr)
    if err != nil {
        return err
    }
    defer conn.Close()

    _, err = conn.Write([]byte(message))
    if err != nil {
        return err
    }

    buf := make([]byte, 1024)
    n, err := conn.Read(buf)
    if err != nil {
        return err
    }

    fmt.Println(string(buf[:n]))
    return nil
}
```

## HTTP клиент

### Базовый HTTP клиент

```go
import (
    "net/http"
    "io"
)

func httpGet(url string) error {
    resp, err := http.Get(url)
    if err != nil {
        return err
    }
    defer resp.Body.Close()

    body, err := io.ReadAll(resp.Body)
    if err != nil {
        return err
    }

    fmt.Println(string(body))
    return nil
}
```

### HTTP клиент с настройками

```go
import (
    "net/http"
    "time"
)

func httpClientWithConfig() *http.Client {
    return &http.Client{
        Timeout: 10 * time.Second,
        Transport: &http.Transport{
            MaxIdleConns:        100,
            IdleConnTimeout:     90 * time.Second,
            TLSHandshakeTimeout: 10 * time.Second,
        },
    }
}
```

## WebSocket

### WebSocket сервер

```go
import (
    "github.com/gorilla/websocket"
    "net/http"
)

var upgrader = websocket.Upgrader{
    CheckOrigin: func(r *http.Request) bool {
        return true
    },
}

func websocketHandler(w http.ResponseWriter, r *http.Request) {
    conn, err := upgrader.Upgrade(w, r, nil)
    if err != nil {
        return
    }
    defer conn.Close()

    for {
        messageType, message, err := conn.ReadMessage()
        if err != nil {
            break
        }

        response := fmt.Sprintf("Echo: %s", string(message))
        conn.WriteMessage(messageType, []byte(response))
    }
}
```

### WebSocket клиент

```go
import "github.com/gorilla/websocket"

func websocketClient(url string) error {
    conn, _, err := websocket.DefaultDialer.Dial(url, nil)
    if err != nil {
        return err
    }
    defer conn.Close()

    // Отправка сообщения
    err = conn.WriteMessage(websocket.TextMessage, []byte("Hello"))
    if err != nil {
        return err
    }

    // Чтение сообщения
    _, message, err := conn.ReadMessage()
    if err != nil {
        return err
    }

    fmt.Println(string(message))
    return nil
}
```

### Практические примеры: TCP сервер с пулом соединений

```go
type ConnectionPool struct {
    connections chan net.Conn
    maxConns    int
    mu          sync.Mutex
}

func NewConnectionPool(maxConns int) *ConnectionPool {
    return &ConnectionPool{
        connections: make(chan net.Conn, maxConns),
        maxConns:    maxConns,
    }
}

func (cp *ConnectionPool) Get() (net.Conn, error) {
    select {
    case conn := <-cp.connections:
        return conn, nil
    default:
        return nil, errors.New("pool exhausted")
    }
}

func (cp *ConnectionPool) Put(conn net.Conn) {
    select {
    case cp.connections <- conn:
    default:
        conn.Close()
    }
}
```

### Практические примеры: TCP сервер с таймаутами

```go
func tcpServerWithTimeout() error {
    listener, err := net.Listen("tcp", ":8080")
    if err != nil {
        return err
    }
    defer listener.Close()

    for {
        conn, err := listener.Accept()
        if err != nil {
            continue
        }

        // Установка таймаутов
        conn.SetReadDeadline(time.Now().Add(30 * time.Second))
        conn.SetWriteDeadline(time.Now().Add(30 * time.Second))

        go handleConnectionWithTimeout(conn)
    }
}

func handleConnectionWithTimeout(conn net.Conn) {
    defer conn.Close()

    buf := make([]byte, 1024)
    for {
        conn.SetReadDeadline(time.Now().Add(30 * time.Second))
        n, err := conn.Read(buf)
        if err != nil {
            if netErr, ok := err.(net.Error); ok && netErr.Timeout() {
                fmt.Println("Read timeout")
            }
            return
        }

        conn.SetWriteDeadline(time.Now().Add(30 * time.Second))
        _, err = conn.Write(buf[:n])
        if err != nil {
            return
        }
    }
}
```

### Практические примеры: UDP multicast

```go
func udpMulticastServer(group string, port int) error {
    addr, err := net.ResolveUDPAddr("udp", fmt.Sprintf("%s:%d", group, port))
    if err != nil {
        return err
    }

    conn, err := net.ListenMulticastUDP("udp", nil, addr)
    if err != nil {
        return err
    }
    defer conn.Close()

    buf := make([]byte, 1024)
    for {
        n, src, err := conn.ReadFromUDP(buf)
        if err != nil {
            continue
        }

        fmt.Printf("Received from %s: %s\n", src, string(buf[:n]))
    }
}

func udpMulticastClient(group string, port int, message string) error {
    addr, err := net.ResolveUDPAddr("udp", fmt.Sprintf("%s:%d", group, port))
    if err != nil {
        return err
    }

    conn, err := net.DialUDP("udp", nil, addr)
    if err != nil {
        return err
    }
    defer conn.Close()

    _, err = conn.Write([]byte(message))
    return err
}
```

### Практические примеры: HTTP клиент с retry

```go
type RetryClient struct {
    client  *http.Client
    maxRetries int
}

func NewRetryClient(maxRetries int) *RetryClient {
    return &RetryClient{
        client: &http.Client{
            Timeout: 10 * time.Second,
        },
        maxRetries: maxRetries,
    }
}

func (rc *RetryClient) Do(req *http.Request) (*http.Response, error) {
    var lastErr error

    for i := 0; i < rc.maxRetries; i++ {
        resp, err := rc.client.Do(req)
        if err == nil {
            return resp, nil
        }

        lastErr = err
        time.Sleep(time.Duration(i+1) * time.Second)
    }

    return nil, lastErr
}
```

### Практические примеры: HTTP клиент с middleware

```go
type Middleware func(http.RoundTripper) http.RoundTripper

func LoggingMiddleware(next http.RoundTripper) http.RoundTripper {
    return &loggingTransport{next: next}
}

type loggingTransport struct {
    next http.RoundTripper
}

func (t *loggingTransport) RoundTrip(req *http.Request) (*http.Response, error) {
    start := time.Now()
    resp, err := t.next.RoundTrip(req)
    duration := time.Since(start)

    fmt.Printf("%s %s - %v - %v\n",
        req.Method, req.URL, duration, err)

    return resp, err
}

func AuthMiddleware(token string) Middleware {
    return func(next http.RoundTripper) http.RoundTripper {
        return &authTransport{
            token: token,
            next:  next,
        }
    }
}

type authTransport struct {
    token string
    next  http.RoundTripper
}

func (t *authTransport) RoundTrip(req *http.Request) (*http.Response, error) {
    req.Header.Set("Authorization", "Bearer "+t.token)
    return t.next.RoundTrip(req)
}
```

### Практические примеры: WebSocket с heartbeat

```go
func websocketHandlerWithHeartbeat(w http.ResponseWriter, r *http.Request) {
    conn, err := upgrader.Upgrade(w, r, nil)
    if err != nil {
        return
    }
    defer conn.Close()

    // Heartbeat
    conn.SetReadDeadline(time.Now().Add(60 * time.Second))
    conn.SetPongHandler(func(string) error {
        conn.SetReadDeadline(time.Now().Add(60 * time.Second))
        return nil
    })

    // Отправка ping
    ticker := time.NewTicker(30 * time.Second)
    defer ticker.Stop()

    done := make(chan struct{})

    go func() {
        defer close(done)
        for {
            conn.SetWriteDeadline(time.Now().Add(10 * time.Second))
            if err := conn.WriteMessage(websocket.PingMessage, nil); err != nil {
                return
            }
            time.Sleep(30 * time.Second)
        }
    }()

    for {
        select {
        case <-done:
            return
        case <-ticker.C:
            conn.SetWriteDeadline(time.Now().Add(10 * time.Second))
            if err := conn.WriteMessage(websocket.PingMessage, nil); err != nil {
                return
            }
        default:
            messageType, message, err := conn.ReadMessage()
            if err != nil {
                return
            }

            conn.SetWriteDeadline(time.Now().Add(10 * time.Second))
            if err := conn.WriteMessage(messageType, message); err != nil {
                return
            }
        }
    }
}
```

### Практические примеры: HTTP клиент с connection pooling

```go
func httpClientWithPool() *http.Client {
    transport := &http.Transport{
        MaxIdleConns:        100,
        MaxIdleConnsPerHost: 10,
        IdleConnTimeout:      90 * time.Second,
        DisableKeepAlives:   false,
    }

    return &http.Client{
        Transport: transport,
        Timeout:   30 * time.Second,
    }
}
```

### Практические примеры: TCP proxy

```go
func tcpProxy(listenAddr, targetAddr string) error {
    listener, err := net.Listen("tcp", listenAddr)
    if err != nil {
        return err
    }
    defer listener.Close()

    for {
        clientConn, err := listener.Accept()
        if err != nil {
            continue
        }

        go func() {
            defer clientConn.Close()

            targetConn, err := net.Dial("tcp", targetAddr)
            if err != nil {
                return
            }
            defer targetConn.Close()

            // Копирование данных в обе стороны
            var wg sync.WaitGroup
            wg.Add(2)

            go func() {
                defer wg.Done()
                io.Copy(targetConn, clientConn)
                targetConn.Close()
            }()

            go func() {
                defer wg.Done()
                io.Copy(clientConn, targetConn)
                clientConn.Close()
            }()

            wg.Wait()
        }()
    }
}
```

### Практические примеры: HTTP клиент с circuit breaker

```go
type CircuitBreakerClient struct {
    client        *http.Client
    maxFailures   int
    timeout       time.Duration
    failures      int
    lastFailure   time.Time
    state         string // "closed", "open", "half-open"
    mu            sync.RWMutex
}

func NewCircuitBreakerClient(maxFailures int, timeout time.Duration) *CircuitBreakerClient {
    return &CircuitBreakerClient{
        client:      &http.Client{Timeout: 10 * time.Second},
        maxFailures: maxFailures,
        timeout:     timeout,
        state:       "closed",
    }
}

func (cbc *CircuitBreakerClient) Do(req *http.Request) (*http.Response, error) {
    cbc.mu.RLock()
    state := cbc.state
    cbc.mu.RUnlock()

    if state == "open" {
        cbc.mu.Lock()
        if time.Since(cbc.lastFailure) > cbc.timeout {
            cbc.state = "half-open"
        } else {
            cbc.mu.Unlock()
            return nil, errors.New("circuit breaker is open")
        }
        cbc.mu.Unlock()
    }

    resp, err := cbc.client.Do(req)

    cbc.mu.Lock()
    defer cbc.mu.Unlock()

    if err != nil {
        cbc.failures++
        cbc.lastFailure = time.Now()
        if cbc.failures >= cbc.maxFailures {
            cbc.state = "open"
        }
        return nil, err
    }

    if resp.StatusCode >= 500 {
        cbc.failures++
        cbc.lastFailure = time.Now()
        if cbc.failures >= cbc.maxFailures {
            cbc.state = "open"
        }
    } else {
        if cbc.state == "half-open" {
            cbc.state = "closed"
        }
        cbc.failures = 0
    }

    return resp, nil
}
```

### Практические примеры: DNS resolver

```go
func resolveDNS(hostname string) ([]string, error) {
    addrs, err := net.LookupHost(hostname)
    if err != nil {
        return nil, err
    }
    return addrs, nil
}

func resolveDNSWithTimeout(hostname string, timeout time.Duration) ([]string, error) {
    resolver := &net.Resolver{
        PreferGo: true,
        Dial: func(ctx context.Context, network, address string) (net.Conn, error) {
            d := net.Dialer{
                Timeout: timeout,
            }
            return d.DialContext(ctx, network, address)
        },
    }

    ctx, cancel := context.WithTimeout(context.Background(), timeout)
    defer cancel()

    addrs, err := resolver.LookupHost(ctx, hostname)
    if err != nil {
        return nil, err
    }

    return addrs, nil
}
```

### Практические примеры: HTTP клиент с rate limiting

```go
type RateLimitedClient struct {
    client  *http.Client
    limiter *rate.Limiter
}

func NewRateLimitedClient(rps int) *RateLimitedClient {
    return &RateLimitedClient{
        client:  &http.Client{Timeout: 10 * time.Second},
        limiter: rate.NewLimiter(rate.Limit(rps), rps),
    }
}

func (rlc *RateLimitedClient) Do(req *http.Request) (*http.Response, error) {
    ctx := req.Context()
    if err := rlc.limiter.Wait(ctx); err != nil {
        return nil, err
    }
    return rlc.client.Do(req)
}
```

### Практические примеры: HTTP клиент с retry

```go
type RetryClient struct {
    client     *http.Client
    maxRetries int
    backoff    time.Duration
}

func NewRetryClient(maxRetries int, backoff time.Duration) *RetryClient {
    return &RetryClient{
        client:     &http.Client{Timeout: 30 * time.Second},
        maxRetries: maxRetries,
        backoff:    backoff,
    }
}

func (rc *RetryClient) Do(req *http.Request) (*http.Response, error) {
    var lastErr error

    for i := 0; i < rc.maxRetries; i++ {
        resp, err := rc.client.Do(req)
        if err == nil && resp.StatusCode < 500 {
            return resp, nil
        }

        if resp != nil {
            resp.Body.Close()
        }

        lastErr = err
        if i < rc.maxRetries-1 {
            time.Sleep(rc.backoff * time.Duration(i+1))
        }
    }

    return nil, fmt.Errorf("max retries exceeded: %w", lastErr)
}
```

### Практические примеры: Connection pooling

```go
type ConnectionPool struct {
    connections chan net.Conn
    factory     func() (net.Conn, error)
    maxSize     int
    timeout     time.Duration
}

func NewConnectionPool(factory func() (net.Conn, error), maxSize int, timeout time.Duration) *ConnectionPool {
    return &ConnectionPool{
        connections: make(chan net.Conn, maxSize),
        factory:     factory,
        maxSize:     maxSize,
        timeout:     timeout,
    }
}

func (cp *ConnectionPool) Get() (net.Conn, error) {
    select {
    case conn := <-cp.connections:
        return conn, nil
    default:
        return cp.factory()
    }
}

func (cp *ConnectionPool) Put(conn net.Conn) {
    select {
    case cp.connections <- conn:
    default:
        conn.Close()
    }
}
```

### Практические примеры: WebSocket сервер

```go
import "github.com/gorilla/websocket"

var upgrader = websocket.Upgrader{
    CheckOrigin: func(r *http.Request) bool {
        return true // В production проверяйте origin
    },
}

func WebSocketHandler(w http.ResponseWriter, r *http.Request) {
    conn, err := upgrader.Upgrade(w, r, nil)
    if err != nil {
        return
    }
    defer conn.Close()

    for {
        messageType, message, err := conn.ReadMessage()
        if err != nil {
            break
        }

        // Эхо сообщения
        if err := conn.WriteMessage(messageType, message); err != nil {
            break
        }
    }
}
```

## Лучшие практики

1. **Используйте таймауты** — устанавливайте таймауты для сетевых операций
2. **Обрабатывайте ошибки** — всегда проверяйте ошибки сетевых операций
3. **Закрывайте соединения** — используйте **defer** для закрытия соединений
4. **Используйте пулы соединений** — для переиспользования соединений
5. **Обрабатывайте разрывы соединений** — корректно обрабатывайте сетевые ошибки
6. **Используйте context** — для отмены операций
7. **Используйте circuit breaker** — для защиты от каскадных отказов
8. **Используйте retry** — для обработки временных ошибок
9. **Используйте rate limiting** — для ограничения частоты запросов
10. **Мониторьте соединения** — отслеживайте состояние соединений
11. **Используйте retry механизмы** — для устойчивости к временным сбоям
12. **Используйте connection pooling** — для эффективного использования соединений
13. **Используйте WebSocket** — для двунаправленной коммуникации
14. **Оптимизируйте сетевые операции** — минимизируйте **latency**
15. **Мониторьте производительность** — отслеживайте сетевые метрики


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Сетевые операции в Go предоставляют мощные инструменты для создания распределенных приложений. Понимание **TCP**, **UDP**, **HTTP** клиента, **WebSocket**, **retry** механизмов, **connection pooling** и лучших практик критично для эффективных сетевых операций в Go. Правильное использование сетевых инструментов позволяет создавать надежные, масштабируемые, устойчивые и эффективные сетевые приложения, которые корректно обрабатывают ошибки и обеспечивают высокую доступность.

## Дополнительные ресурсы

- [Go net Package](https://pkg.go.dev/net)
- [Go net/http](https://pkg.go.dev/net/http)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
