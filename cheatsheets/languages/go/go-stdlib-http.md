---
title: "Go: стандартная библиотека - HTTP"
description: "Полное руководство по работе с HTTP в Go: клиент, сервер, handlers, middleware, routing, templates"
tags: ["go", "golang", "http", "web", "server", "client"]
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: стандартная библиотека - **HTTP**

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

- [Go net/http Documentation](https://pkg.go.dev/net/http)
- [Go HTTP Server Tutorial](https://go.dev/doc/articles/wiki/)
- [Go HTTP Client Tutorial](https://go.dev/doc/tutorial/web-service-gin)

## Содержание

- [Go: стандартная библиотека - **HTTP**](#go-стандартная-библиотека-http)
- [Введение в **HTTP**](#введение-в-http)
  - [Основные компоненты](#основные-компоненты)
- [**HTTP Server**](#http-server)
  - [Базовый **HTTP** сервер](#базовый-http-сервер)
  - [Сервер с настройками](#сервер-с-настройками)
  - [**HTTPS** сервер](#https-сервер)
- [**HTTP Handlers**](#http-handlers)
  - [**Handler** функция](#handler-функция)
  - [**Handler** интерфейс](#handler-интерфейс)
  - [Обработка различных методов](#обработка-различных-методов)
  - [Работа с параметрами запроса](#работа-с-параметрами-запроса)
  - [Работа с **JSON**](#работа-с-json)
- [**HTTP Client**](#http-client)
  - [Базовый **GET** запрос](#базовый-get-запрос)
  - [**POST** запрос](#post-запрос)
  - [Настройка клиента](#настройка-клиента)
  - [Создание запросов](#создание-запросов)
- [**Middleware**](#middleware)
  - [Базовый **middleware**](#базовый-middleware)
  - [Цепочка **middleware**](#цепочка-middleware)
- [**Routing**](#routing)
  - [Базовый роутинг](#базовый-роутинг)
  - [Роутинг с префиксами](#роутинг-с-префиксами)
- [**Templates**](#templates)
  - [Базовый шаблон](#базовый-шаблон)
  - [Шаблоны с функциями](#шаблоны-с-функциями)
  - [Аутентификация **middleware**](#аутентификация-middleware)
  - [**CORS middleware**](#cors-middleware)
  - [**Rate limiting middleware**](#rate-limiting-middleware)
  - [**Recovery middleware**](#recovery-middleware)
  - [**Request** ID **middleware**](#request-id-middleware)
  - [**Compression middleware**](#compression-middleware)
  - [Практические примеры: **REST API**](#практические-примеры-rest-api)
  - [Практические примеры: **File Server**](#практические-примеры-file-server)
  - [Практические примеры: **WebSocket**](#практические-примеры-websocket)
  - [Практические примеры: **Server-Sent Events**](#практические-примеры-server-sent-events)
  - [Практические примеры: **Multipart Form**](#практические-примеры-multipart-form)
  - [Практические примеры: **Cookie Management**](#практические-примеры-cookie-management)
  - [Практические примеры: **Session Management**](#практические-примеры-session-management)
  - [Практические примеры: **HTTP**/2 **Server Push**](#практические-примеры-http2-server-push)
  - [Практические примеры: **HTTP Client** с **Retry**](#практические-примеры-http-client-с-retry)
  - [Практические примеры: **HTTP Client** с **Circuit Breaker**](#практические-примеры-http-client-с-circuit-breaker)
  - [Практические примеры: **HTTP Client Pool**](#практические-примеры-http-client-pool)
  - [Практические примеры: **HTTP Proxy**](#практические-примеры-http-proxy)
  - [Практические примеры: **Graceful Shutdown**](#практические-примеры-graceful-shutdown)
  - [Практические примеры: **Health Check Endpoint**](#практические-примеры-health-check-endpoint)
  - [Практические примеры: **Request Validation**](#практические-примеры-request-validation)
  - [Практические примеры: **Response Caching**](#практические-примеры-response-caching)
  - [Практические примеры: **HTTP** клиент с **retry**](#практические-примеры-http-клиент-с-retry)
  - [Практические примеры: **HTTP** клиент с **circuit breaker**](#практические-примеры-http-клиент-с-circuit-breaker)
  - [Практические примеры: **HTTP** сервер с **metrics**](#практические-примеры-http-сервер-с-metrics)
  - [Практические примеры: **HTTP** сервер с **health checks**](#практические-примеры-http-сервер-с-health-checks)
  - [Практические примеры: **HTTP** сервер с **request** ID](#практические-примеры-http-сервер-с-request-id)
  - [Практические примеры: **HTTP** сервер с **compression**](#практические-примеры-http-сервер-с-compression)
  - [Практические примеры: **HTTP** клиент с прокси](#практические-примеры-http-клиент-с-прокси)
  - [Практические примеры: **HTTP** сервер с **file serving**](#практические-примеры-http-сервер-с-file-serving)
  - [Практические примеры: **HTTP** клиент с **cookies**](#практические-примеры-http-клиент-с-cookies)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: **Graceful shutdown HTTP** сервера](#практические-примеры-graceful-shutdown-http-сервера)
  - [Практические примеры: **HTTP** клиент с **connection pooling**](#практические-примеры-http-клиент-с-connection-pooling)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **HTTP**

Пакет `net/http` предоставляет полную поддержку **HTTP** клиента и сервера. Понимание работы с **HTTP** критично для создания веб-приложений и **API**.

### Основные компоненты

1. **HTTP Server** - создание **HTTP** серверов
2. **HTTP Handlers** - обработка **HTTP** запросов
3. **HTTP Client** - выполнение **HTTP** запросов
4. **Middleware** - промежуточная обработка запросов

## **HTTP Server**

**HTTP** сервер в Go создается с помощью пакета `net/http`.

### Базовый **HTTP** сервер

```go
package main

import (
    "fmt"
    "net/http"
)

func main() {
    http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
        fmt.Fprintf(w, "Hello, World!")
    })
    
    http.ListenAndServe(":8080", nil)
}
```

### Сервер с настройками

```go
import (
    "net/http"
    "time"
)

func main() {
    server := &http.Server{
        Addr:         ":8080",
        Handler:      nil,
        ReadTimeout:  15 * time.Second,
        WriteTimeout: 15 * time.Second,
        IdleTimeout:  60 * time.Second,
    }
    
    server.ListenAndServe()
}
```

### **HTTPS** сервер

```go
import (
    "net/http"
    "crypto/tls"
)

func main() {
    server := &http.Server{
        Addr:      ":8443",
        TLSConfig: &tls.Config{
            MinVersion: tls.VersionTLS12,
        },
    }
    
    server.ListenAndServeTLS("cert.pem", "key.pem")
}
```

## **HTTP Handlers**

**Handlers** обрабатывают **HTTP** запросы и формируют ответы.

### **Handler** функция

```go
func helloHandler(w http.ResponseWriter, r *http.Request) {
    w.WriteHeader(http.StatusOK)
    w.Write([]byte("Hello, World!"))
}

func main() {
    http.HandleFunc("/hello", helloHandler)
    http.ListenAndServe(":8080", nil)
}
```

### **Handler** интерфейс

```go
type Handler interface {
    ServeHTTP(ResponseWriter, *Request)
}

type MyHandler struct{}

func (h *MyHandler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
    w.WriteHeader(http.StatusOK)
    w.Write([]byte("Hello from Handler"))
}

func main() {
    handler := &MyHandler{}
    http.Handle("/", handler)
    http.ListenAndServe(":8080", nil)
}
```

### Обработка различных методов

```go
func userHandler(w http.ResponseWriter, r *http.Request) {
    switch r.Method {
    case http.MethodGet:
        // Обработка GET запроса
        getUser(w, r)
    case http.MethodPost:
        // Обработка POST запроса
        createUser(w, r)
    case http.MethodPut:
        // Обработка PUT запроса
        updateUser(w, r)
    case http.MethodDelete:
        // Обработка DELETE запроса
        deleteUser(w, r)
    default:
        http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
    }
}
```

### Работа с параметрами запроса

```go
func searchHandler(w http.ResponseWriter, r *http.Request) {
    // Получение query параметров
    query := r.URL.Query().Get("q")
    
    // Получение всех значений параметра
    tags := r.URL.Query()["tag"]
    
    // Парсинг формы
    r.ParseForm()
    name := r.Form.Get("name")
    
    fmt.Fprintf(w, "Query: %s, Tags: %v, Name: %s", query, tags, name)
}
```

### Работа с **JSON**

```go
import (
    "encoding/json"
    "net/http"
)

type User struct {
    ID   int    `json:"id"`
    Name string `json:"name"`
}

func getUserHandler(w http.ResponseWriter, r *http.Request) {
    user := User{ID: 1, Name: "Alice"}
    
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(user)
}

func createUserHandler(w http.ResponseWriter, r *http.Request) {
    var user User
    if err := json.NewDecoder(r.Body).Decode(&user); err != nil {
        http.Error(w, err.Error(), http.StatusBadRequest)
        return
    }
    
    // Обработка пользователя
    w.WriteHeader(http.StatusCreated)
    json.NewEncoder(w).Encode(user)
}
```

## **HTTP Client**

**HTTP** клиент позволяет выполнять **HTTP** запросы к внешним сервисам.

### Базовый **GET** запрос

```go
import (
    "io"
    "net/http"
)

func main() {
    resp, err := http.Get("https://api.example.com/users")
    if err != nil {
        log.Fatal(err)
    }
    defer resp.Body.Close()
    
    body, err := io.ReadAll(resp.Body)
    if err != nil {
        log.Fatal(err)
    }
    
    fmt.Println(string(body))
}
```

### **POST** запрос

```go
import (
    "bytes"
    "encoding/json"
    "net/http"
)

func main() {
    data := map[string]string{
        "name": "Alice",
    }
    
    jsonData, _ := json.Marshal(data)
    
    resp, err := http.Post(
        "https://api.example.com/users",
        "application/json",
        bytes.NewBuffer(jsonData),
    )
    if err != nil {
        log.Fatal(err)
    }
    defer resp.Body.Close()
}
```

### Настройка клиента

```go
import (
    "net/http"
    "time"
)

func main() {
    client := &http.Client{
        Timeout: 10 * time.Second,
        Transport: &http.Transport{
            MaxIdleConns:        100,
            IdleConnTimeout:     90 * time.Second,
            TLSHandshakeTimeout: 10 * time.Second,
        },
    }
    
    resp, err := client.Get("https://api.example.com/users")
    if err != nil {
        log.Fatal(err)
    }
    defer resp.Body.Close()
}
```

### Создание запросов

```go
import "net/http"

func main() {
    req, err := http.NewRequest("GET", "https://api.example.com/users", nil)
    if err != nil {
        log.Fatal(err)
    }
    
    // Добавление заголовков
    req.Header.Set("Authorization", "Bearer token")
    req.Header.Set("Content-Type", "application/json")
    
    client := &http.Client{}
    resp, err := client.Do(req)
    if err != nil {
        log.Fatal(err)
    }
    defer resp.Body.Close()
}
```

## **Middleware**

**Middleware** позволяет добавлять промежуточную обработку запросов.

### Базовый **middleware**

```go
func loggingMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        
        next.ServeHTTP(w, r)
        
        duration := time.Since(start)
        log.Printf("%s %s took %v", r.Method, r.URL.Path, duration)
    })
}

func main() {
    handler := http.HandlerFunc(helloHandler)
    http.Handle("/", loggingMiddleware(handler))
    http.ListenAndServe(":8080", nil)
}
```

### Цепочка **middleware**

```go
func chainMiddleware(handler http.Handler, middlewares ...func(http.Handler) http.Handler) http.Handler {
    for i := len(middlewares) - 1; i >= 0; i-- {
        handler = middlewares[i](handler)
    }
    return handler
}

func main() {
    handler := http.HandlerFunc(helloHandler)
    
    finalHandler := chainMiddleware(
        handler,
        loggingMiddleware,
        authMiddleware,
        corsMiddleware,
    )
    
    http.Handle("/", finalHandler)
    http.ListenAndServe(":8080", nil)
}
```

## **Routing**

Роутинг позволяет направлять запросы к соответствующим обработчикам.

### Базовый роутинг

```go
func main() {
    http.HandleFunc("/", homeHandler)
    http.HandleFunc("/users", usersHandler)
    http.HandleFunc("/users/", userHandler)
    http.HandleFunc("/posts", postsHandler)
    
    http.ListenAndServe(":8080", nil)
}
```

### Роутинг с префиксами

```go
func main() {
    mux := http.NewServeMux()
    
    mux.HandleFunc("/api/v1/users", usersHandler)
    mux.HandleFunc("/api/v1/posts", postsHandler)
    mux.HandleFunc("/api/v2/users", usersV2Handler)
    
    http.ListenAndServe(":8080", mux)
}
```

## **Templates**

Шаблоны позволяют генерировать динамический **HTML** контент.

### Базовый шаблон

```go
import (
    "html/template"
    "net/http"
)

func homeHandler(w http.ResponseWriter, r *http.Request) {
    tmpl := template.Must(template.ParseFiles("templates/home.html"))
    
    data := struct {
        Title string
        Name  string
    }{
        Title: "Home",
        Name:  "Alice",
    }
    
    tmpl.Execute(w, data)
}
```

### Шаблоны с функциями

```go
func homeHandler(w http.ResponseWriter, r *http.Request) {
    funcMap := template.FuncMap{
        "upper": strings.ToUpper,
        "lower": strings.ToLower,
    }
    
    tmpl := template.Must(
        template.New("home.html").
            Funcs(funcMap).
            ParseFiles("templates/home.html"),
    )
    
    tmpl.Execute(w, data)
}
```

### Аутентификация **middleware**

```go
func authMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        token := r.Header.Get("Authorization")
        if token == "" {
            http.Error(w, "Unauthorized", http.StatusUnauthorized)
            return
        }
        
        // Проверка токена
        if !isValidToken(token) {
            http.Error(w, "Invalid token", http.StatusUnauthorized)
            return
        }
        
        // Добавление информации о пользователе в контекст
        ctx := context.WithValue(r.Context(), "userID", getUserID(token))
        next.ServeHTTP(w, r.WithContext(ctx))
    })
}
```

### **CORS middleware**

```go
func corsMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.Header().Set("Access-Control-Allow-Origin", "*")
        w.Header().Set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")
        
        if r.Method == "OPTIONS" {
            w.WriteHeader(http.StatusOK)
            return
        }
        
        next.ServeHTTP(w, r)
    })
}
```

### **Rate limiting middleware**

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
```

### **Recovery middleware**

```go
func recoveryMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        defer func() {
            if err := recover(); err != nil {
                log.Printf("Panic recovered: %v", err)
                http.Error(w, "Internal Server Error", http.StatusInternalServerError)
            }
        }()
        next.ServeHTTP(w, r)
    })
}
```

### **Request** `ID` **middleware**

```go
import "github.com/google/uuid"

func requestIDMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        requestID := r.Header.Get("X-Request-ID")
        if requestID == "" {
            requestID = uuid.New().String()
        }
        
        w.Header().Set("X-Request-ID", requestID)
        ctx := context.WithValue(r.Context(), "requestID", requestID)
        next.ServeHTTP(w, r.WithContext(ctx))
    })
}
```

### **Compression middleware**

```go
import "compress/gzip"

func gzipMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        if !strings.Contains(r.Header.Get("Accept-Encoding"), "gzip") {
            next.ServeHTTP(w, r)
            return
        }
        
        w.Header().Set("Content-Encoding", "gzip")
        gz := gzip.NewWriter(w)
        defer gz.Close()
        
        gzw := &gzipResponseWriter{Writer: gz, ResponseWriter: w}
        next.ServeHTTP(gzw, r)
    })
}

type gzipResponseWriter struct {
    io.Writer
    http.ResponseWriter
}

func (w *gzipResponseWriter) Write(b []byte) (int, error) {
    return w.Writer.Write(b)
}
```

### Практические примеры: **REST API**

```go
type UserAPI struct {
    service UserService
}

func (api *UserAPI) GetUser(w http.ResponseWriter, r *http.Request) {
    idStr := r.URL.Path[len("/users/"):]
    id, err := strconv.Atoi(idStr)
    if err != nil {
        http.Error(w, "Invalid user ID", http.StatusBadRequest)
        return
    }
    
    user, err := api.service.GetUser(id)
    if err != nil {
        http.Error(w, err.Error(), http.StatusNotFound)
        return
    }
    
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(user)
}

func (api *UserAPI) CreateUser(w http.ResponseWriter, r *http.Request) {
    var user User
    if err := json.NewDecoder(r.Body).Decode(&user); err != nil {
        http.Error(w, err.Error(), http.StatusBadRequest)
        return
    }
    
    created, err := api.service.CreateUser(user)
    if err != nil {
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }
    
    w.Header().Set("Content-Type", "application/json")
    w.WriteHeader(http.StatusCreated)
    json.NewEncoder(w).Encode(created)
}
```

### Практические примеры: **File Server**

```go
// Статические файлы
func staticFileServer() {
    fs := http.FileServer(http.Dir("./static"))
    http.Handle("/static/", http.StripPrefix("/static/", fs))
}

// Файлы с кэшированием
func cachedFileServer() {
    fs := http.FileServer(http.Dir("./static"))
    handler := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        w.Header().Set("Cache-Control", "public, max-age=3600")
        fs.ServeHTTP(w, r)
    })
    http.Handle("/static/", http.StripPrefix("/static/", handler))
}
```

### Практические примеры: **WebSocket**

```go
import "github.com/gorilla/websocket"

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
        
        // Эхо ответ
        if err := conn.WriteMessage(messageType, message); err != nil {
            break
        }
    }
}
```

### Практические примеры: **Server-Sent Events**

```go
func sseHandler(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "text/event-stream")
    w.Header().Set("Cache-Control", "no-cache")
    w.Header().Set("Connection", "keep-alive")
    
    flusher, ok := w.(http.Flusher)
    if !ok {
        http.Error(w, "Streaming not supported", http.StatusInternalServerError)
        return
    }
    
    for i := 0; i < 10; i++ {
        fmt.Fprintf(w, "data: Message %d\n\n", i)
        flusher.Flush()
        time.Sleep(1 * time.Second)
    }
}
```

### Практические примеры: **Multipart Form**

```go
func uploadHandler(w http.ResponseWriter, r *http.Request) {
    if r.Method != http.MethodPost {
        http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
        return
    }
    
    err := r.ParseMultipartForm(10 << 20) // 10 MB
    if err != nil {
        http.Error(w, err.Error(), http.StatusBadRequest)
        return
    }
    
    file, handler, err := r.FormFile("file")
    if err != nil {
        http.Error(w, err.Error(), http.StatusBadRequest)
        return
    }
    defer file.Close()
    
    // Сохранение файла
    dst, err := os.Create(handler.Filename)
    if err != nil {
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }
    defer dst.Close()
    
    io.Copy(dst, file)
    w.WriteHeader(http.StatusOK)
}
```

### Практические примеры: **Cookie Management**

```go
func setCookieHandler(w http.ResponseWriter, r *http.Request) {
    cookie := &http.Cookie{
        Name:     "session",
        Value:    "abc123",
        Path:     "/",
        MaxAge:   3600,
        HttpOnly: true,
        Secure:   true,
        SameSite: http.SameSiteStrictMode,
    }
    http.SetCookie(w, cookie)
}

func getCookieHandler(w http.ResponseWriter, r *http.Request) {
    cookie, err := r.Cookie("session")
    if err != nil {
        http.Error(w, "Cookie not found", http.StatusBadRequest)
        return
    }
    fmt.Fprintf(w, "Cookie value: %s", cookie.Value)
}
```

### Практические примеры: **Session Management**

```go
type SessionStore struct {
    sessions map[string]*Session
    mu       sync.RWMutex
}

type Session struct {
    ID        string
    Data      map[string]interface{}
    ExpiresAt time.Time
}

func (s *SessionStore) GetSession(id string) (*Session, bool) {
    s.mu.RLock()
    defer s.mu.RUnlock()
    session, ok := s.sessions[id]
    if !ok || time.Now().After(session.ExpiresAt) {
        return nil, false
    }
    return session, true
}

func (s *SessionStore) CreateSession() *Session {
    session := &Session{
        ID:        uuid.New().String(),
        Data:      make(map[string]interface{}),
        ExpiresAt: time.Now().Add(24 * time.Hour),
    }
    s.mu.Lock()
    s.sessions[session.ID] = session
    s.mu.Unlock()
    return session
}
```

### Практические примеры: **HTTP**/2 **Server Push**

```go
func pushHandler(w http.ResponseWriter, r *http.Request) {
    if pusher, ok := w.(http.Pusher); ok {
        if err := pusher.Push("/static/style.css", nil); err != nil {
            log.Printf("Failed to push: %v", err)
        }
    }
    
    // Обычный ответ
    w.Write([]byte("<html><head><link rel='stylesheet' href='/static/style.css'></head><body>Hello</body></html>"))
}
```

### Практические примеры: **HTTP Client** с **Retry**

```go
func httpClientWithRetry(url string, maxRetries int) (*http.Response, error) {
    client := &http.Client{
        Timeout: 10 * time.Second,
    }
    
    var lastErr error
    for i := 0; i < maxRetries; i++ {
        resp, err := client.Get(url)
        if err == nil {
            return resp, nil
        }
        lastErr = err
        time.Sleep(time.Duration(i+1) * time.Second)
    }
    
    return nil, lastErr
}
```

### Практические примеры: **HTTP Client** с **Circuit Breaker**

```go
type CircuitBreakerClient struct {
    client       *http.Client
    maxFailures  int
    timeout      time.Duration
    failures     int
    lastFailure  time.Time
    mu           sync.Mutex
}

func (c *CircuitBreakerClient) Do(req *http.Request) (*http.Response, error) {
    c.mu.Lock()
    if c.failures >= c.maxFailures {
        if time.Since(c.lastFailure) < c.timeout {
            c.mu.Unlock()
            return nil, fmt.Errorf("circuit breaker is open")
        }
        c.failures = 0
    }
    c.mu.Unlock()
    
    resp, err := c.client.Do(req)
    
    c.mu.Lock()
    if err != nil {
        c.failures++
        c.lastFailure = time.Now()
    } else {
        c.failures = 0
    }
    c.mu.Unlock()
    
    return resp, err
}
```

### Практические примеры: **HTTP Client Pool**

```go
type ClientPool struct {
    clients chan *http.Client
    factory func() *http.Client
}

func NewClientPool(size int, factory func() *http.Client) *ClientPool {
    pool := &ClientPool{
        clients: make(chan *http.Client, size),
        factory: factory,
    }
    
    for i := 0; i < size; i++ {
        pool.clients <- factory()
    }
    
    return pool
}

func (p *ClientPool) Get() *http.Client {
    return <-p.clients
}

func (p *ClientPool) Put(client *http.Client) {
    select {
    case p.clients <- client:
    default:
        // Pool is full, discard client
    }
}
```

### Практические примеры: **HTTP Proxy**

```go
func proxyHandler(targetURL string) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        target, err := url.Parse(targetURL)
        if err != nil {
            http.Error(w, err.Error(), http.StatusInternalServerError)
            return
        }
        
        r.URL.Scheme = target.Scheme
        r.URL.Host = target.Host
        r.URL.Path = target.Path + r.URL.Path
        r.Host = target.Host
        
        resp, err := http.DefaultTransport.RoundTrip(r)
        if err != nil {
            http.Error(w, err.Error(), http.StatusBadGateway)
            return
        }
        defer resp.Body.Close()
        
        for k, v := range resp.Header {
            w.Header()[k] = v
        }
        w.WriteHeader(resp.StatusCode)
        io.Copy(w, resp.Body)
    }
}
```

### Практические примеры: **Graceful Shutdown**

```go
func gracefulShutdown(server *http.Server) {
    sigChan := make(chan os.Signal, 1)
    signal.Notify(sigChan, os.Interrupt, syscall.SIGTERM)
    
    <-sigChan
    fmt.Println("Shutting down server...")
    
    ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
    defer cancel()
    
    if err := server.Shutdown(ctx); err != nil {
        fmt.Printf("Server forced to shutdown: %v\n", err)
    }
    
    fmt.Println("Server stopped")
}
```

### Практические примеры: **Health Check Endpoint**

```go
func healthCheckHandler(w http.ResponseWriter, r *http.Request) {
    checks := map[string]string{
        "status":  "ok",
        "database": checkDatabase(),
        "cache":    checkCache(),
    }
    
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(checks)
}

func readinessHandler(w http.ResponseWriter, r *http.Request) {
    if isReady() {
        w.WriteHeader(http.StatusOK)
        w.Write([]byte("Ready"))
    } else {
        w.WriteHeader(http.StatusServiceUnavailable)
        w.Write([]byte("Not Ready"))
    }
}

func livenessHandler(w http.ResponseWriter, r *http.Request) {
    w.WriteHeader(http.StatusOK)
    w.Write([]byte("Alive"))
}
```

### Практические примеры: **Request Validation**

```go
func validateRequest(r *http.Request) error {
    if r.ContentLength > 10*1024*1024 { // 10 MB
        return fmt.Errorf("request too large")
    }
    
    contentType := r.Header.Get("Content-Type")
    if !strings.HasPrefix(contentType, "application/json") {
        return fmt.Errorf("invalid content type")
    }
    
    return nil
}

func validatedHandler(w http.ResponseWriter, r *http.Request) {
    if err := validateRequest(r); err != nil {
        http.Error(w, err.Error(), http.StatusBadRequest)
        return
    }
    
    // Обработка запроса
}
```

### Практические примеры: **Response Caching**

```go
type CacheEntry struct {
    Data      []byte
    ExpiresAt time.Time
}

type ResponseCache struct {
    entries map[string]*CacheEntry
    mu      sync.RWMutex
}

func (c *ResponseCache) Get(key string) ([]byte, bool) {
    c.mu.RLock()
    defer c.mu.RUnlock()
    
    entry, ok := c.entries[key]
    if !ok || time.Now().After(entry.ExpiresAt) {
        return nil, false
    }
    
    return entry.Data, true
}

func (c *ResponseCache) Set(key string, data []byte, ttl time.Duration) {
    c.mu.Lock()
    defer c.mu.Unlock()
    
    c.entries[key] = &CacheEntry{
        Data:      data,
        ExpiresAt: time.Now().Add(ttl),
    }
}

func cachedHandler(cache *ResponseCache) http.HandlerFunc {
    return func(w http.ResponseWriter, r *http.Request) {
        cacheKey := r.URL.Path
        
        if data, ok := cache.Get(cacheKey); ok {
            w.Header().Set("X-Cache", "HIT")
            w.Write(data)
            return
        }
        
        // Генерация ответа
        data := generateResponse(r)
        cache.Set(cacheKey, data, 5*time.Minute)
        
        w.Header().Set("X-Cache", "MISS")
        w.Write(data)
    }
}
```

### Практические примеры: **HTTP** клиент с **retry**

```go
type RetryClient struct {
    client      *http.Client
    maxRetries  int
    retryDelay  time.Duration
    retryFunc   func(*http.Response, error) bool
}

func NewRetryClient(maxRetries int, retryDelay time.Duration) *RetryClient {
    return &RetryClient{
        client:     &http.Client{Timeout: 30 * time.Second},
        maxRetries: maxRetries,
        retryDelay: retryDelay,
        retryFunc: func(resp *http.Response, err error) bool {
            if err != nil {
                return true
            }
            return resp.StatusCode >= 500
        },
    }
}

func (rc *RetryClient) Do(req *http.Request) (*http.Response, error) {
    var lastResp *http.Response
    var lastErr error
    
    for attempt := 0; attempt < rc.maxRetries; attempt++ {
        resp, err := rc.client.Do(req)
        lastResp = resp
        lastErr = err
        
        if !rc.retryFunc(resp, err) {
            return resp, err
        }
        
        if resp != nil {
            resp.Body.Close()
        }
        
        if attempt < rc.maxRetries-1 {
            time.Sleep(rc.retryDelay * time.Duration(attempt+1))
        }
    }
    
    return lastResp, lastErr
}
```

### Практические примеры: **HTTP** клиент с **circuit breaker**

```go
type CircuitBreakerClient struct {
    client      *http.Client
    maxFailures int
    timeout     time.Duration
    failures    int
    lastFailure time.Time
    state       string // "closed", "open", "half-open"
    mu          sync.RWMutex
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
            return nil, fmt.Errorf("circuit breaker is open")
        }
        cbc.mu.Unlock()
    }
    
    resp, err := cbc.client.Do(req)
    
    cbc.mu.Lock()
    defer cbc.mu.Unlock()
    
    if err != nil || (resp != nil && resp.StatusCode >= 500) {
        cbc.failures++
        cbc.lastFailure = time.Now()
        if cbc.failures >= cbc.maxFailures {
            cbc.state = "open"
        }
        return resp, err
    }
    
    if cbc.state == "half-open" {
        cbc.state = "closed"
    }
    cbc.failures = 0
    
    return resp, nil
}
```

### Практические примеры: **HTTP** сервер с **metrics**

```go
type MetricsHandler struct {
    requestsTotal    *prometheus.CounterVec
    requestDuration  *prometheus.HistogramVec
    requestSize      *prometheus.HistogramVec
    responseSize     *prometheus.HistogramVec
}

func NewMetricsHandler() *MetricsHandler {
    return &MetricsHandler{
        requestsTotal: prometheus.NewCounterVec(
            prometheus.CounterOpts{
                Name: "http_requests_total",
                Help: "Total number of HTTP requests",
            },
            []string{"method", "endpoint", "status"},
        ),
        requestDuration: prometheus.NewHistogramVec(
            prometheus.HistogramOpts{
                Name: "http_request_duration_seconds",
                Help: "HTTP request duration",
            },
            []string{"method", "endpoint"},
        ),
        requestSize: prometheus.NewHistogramVec(
            prometheus.HistogramOpts{
                Name: "http_request_size_bytes",
                Help: "HTTP request size",
            },
            []string{"method", "endpoint"},
        ),
        responseSize: prometheus.NewHistogramVec(
            prometheus.HistogramOpts{
                Name: "http_response_size_bytes",
                Help: "HTTP response size",
            },
            []string{"method", "endpoint"},
        ),
    }
}

func (mh *MetricsHandler) Middleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        
        // Запись размера запроса
        requestSize := r.ContentLength
        if requestSize < 0 {
            requestSize = 0
        }
        
        // Оборачивание ResponseWriter для записи размера ответа
        rw := &responseWriter{ResponseWriter: w, statusCode: http.StatusOK}
        
        next.ServeHTTP(rw, r)
        
        duration := time.Since(start)
        endpoint := r.URL.Path
        method := r.Method
        status := strconv.Itoa(rw.statusCode)
        
        mh.requestsTotal.WithLabelValues(method, endpoint, status).Inc()
        mh.requestDuration.WithLabelValues(method, endpoint).Observe(duration.Seconds())
        mh.requestSize.WithLabelValues(method, endpoint).Observe(float64(requestSize))
        mh.responseSize.WithLabelValues(method, endpoint).Observe(float64(rw.size))
    })
}

type responseWriter struct {
    http.ResponseWriter
    statusCode int
    size       int64
}

func (rw *responseWriter) WriteHeader(code int) {
    rw.statusCode = code
    rw.ResponseWriter.WriteHeader(code)
}

func (rw *responseWriter) Write(b []byte) (int, error) {
    n, err := rw.ResponseWriter.Write(b)
    rw.size += int64(n)
    return n, err
}
```

### Практические примеры: **HTTP** сервер с **health checks**

```go
type HealthChecker interface {
    Check(ctx context.Context) error
}

type HealthCheckServer struct {
    checkers map[string]HealthChecker
    mu       sync.RWMutex
}

func NewHealthCheckServer() *HealthCheckServer {
    return &HealthCheckServer{
        checkers: make(map[string]HealthChecker),
    }
}

func (hcs *HealthCheckServer) Register(name string, checker HealthChecker) {
    hcs.mu.Lock()
    defer hcs.mu.Unlock()
    hcs.checkers[name] = checker
}

func (hcs *HealthCheckServer) HandleHealth(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "application/json")
    w.WriteHeader(http.StatusOK)
    json.NewEncoder(w).Encode(map[string]string{"status": "ok"})
}

func (hcs *HealthCheckServer) HandleReady(w http.ResponseWriter, r *http.Request) {
    ctx, cancel := context.WithTimeout(r.Context(), 5*time.Second)
    defer cancel()
    
    results := make(map[string]string)
    allHealthy := true
    
    hcs.mu.RLock()
    checkers := make(map[string]HealthChecker)
    for k, v := range hcs.checkers {
        checkers[k] = v
    }
    hcs.mu.RUnlock()
    
    for name, checker := range checkers {
        if err := checker.Check(ctx); err != nil {
            results[name] = "unhealthy: " + err.Error()
            allHealthy = false
        } else {
            results[name] = "healthy"
        }
    }
    
    w.Header().Set("Content-Type", "application/json")
    if allHealthy {
        w.WriteHeader(http.StatusOK)
    } else {
        w.WriteHeader(http.StatusServiceUnavailable)
    }
    
    json.NewEncoder(w).Encode(map[string]interface{}{
        "status": map[bool]string{true: "ready", false: "not ready"}[allHealthy],
        "checks": results,
    })
}

func (hcs *HealthCheckServer) HandleLive(w http.ResponseWriter, r *http.Request) {
    w.Header().Set("Content-Type", "application/json")
    w.WriteHeader(http.StatusOK)
    json.NewEncoder(w).Encode(map[string]string{"status": "alive"})
}

func setupHealthCheck(mux *http.ServeMux, hcs *HealthCheckServer) {
    mux.HandleFunc("/health", hcs.HandleHealth)
    mux.HandleFunc("/ready", hcs.HandleReady)
    mux.HandleFunc("/live", hcs.HandleLive)
}
```

### Практические примеры: **HTTP** сервер с **request** `ID`

```go
type RequestIDGenerator func() string

func DefaultRequestIDGenerator() string {
    b := make([]byte, 16)
    rand.Read(b)
    return hex.EncodeToString(b)
}

func RequestIDMiddleware(generator RequestIDGenerator) func(http.Handler) http.Handler {
    if generator == nil {
        generator = DefaultRequestIDGenerator
    }
    
    return func(next http.Handler) http.Handler {
        return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
            requestID := r.Header.Get("X-Request-ID")
            if requestID == "" {
                requestID = generator()
            }
            
            w.Header().Set("X-Request-ID", requestID)
            ctx := context.WithValue(r.Context(), "requestID", requestID)
            next.ServeHTTP(w, r.WithContext(ctx))
        })
    }
}

func GetRequestID(r *http.Request) string {
    if id, ok := r.Context().Value("requestID").(string); ok {
        return id
    }
    return ""
}
```

### Практические примеры: **HTTP** сервер с **compression**

```go
import "github.com/klauspost/compress/gzip"

type gzipResponseWriter struct {
    http.ResponseWriter
    writer *gzip.Writer
}

func newGzipResponseWriter(w http.ResponseWriter) *gzipResponseWriter {
    return &gzipResponseWriter{
        ResponseWriter: w,
        writer:         gzip.NewWriter(w),
    }
}

func (grw *gzipResponseWriter) Write(b []byte) (int, error) {
    return grw.writer.Write(b)
}

func (grw *gzipResponseWriter) Close() error {
    return grw.writer.Close()
}

func CompressionMiddleware(next http.Handler) http.Handler {
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        if !strings.Contains(r.Header.Get("Accept-Encoding"), "gzip") {
            next.ServeHTTP(w, r)
            return
        }
        
        w.Header().Set("Content-Encoding", "gzip")
        grw := newGzipResponseWriter(w)
        defer grw.Close()
        
        next.ServeHTTP(grw, r)
    })
}
```

### Практические примеры: **HTTP** клиент с прокси

```go
func NewProxyClient(proxyURL string) (*http.Client, error) {
    proxy, err := url.Parse(proxyURL)
    if err != nil {
        return nil, err
    }
    
    transport := &http.Transport{
        Proxy: http.ProxyURL(proxy),
    }
    
    return &http.Client{
        Transport: transport,
        Timeout:   30 * time.Second,
    }, nil
}

func NewSOCKS5Client(proxyAddr string) (*http.Client, error) {
    dialer, err := proxy.SOCKS5("tcp", proxyAddr, nil, proxy.Direct)
    if err != nil {
        return nil, err
    }
    
    transport := &http.Transport{
        Dial: dialer.Dial,
    }
    
    return &http.Client{
        Transport: transport,
        Timeout:   30 * time.Second,
    }, nil
}
```

### Практические примеры: **HTTP** сервер с **file serving**

```go
func FileServerMiddleware(root string, stripPrefix string) http.Handler {
    fs := http.Dir(root)
    fileServer := http.FileServer(fs)
    
    if stripPrefix != "" {
        fileServer = http.StripPrefix(stripPrefix, fileServer)
    }
    
    return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
        // Проверка на попытку выйти за пределы директории
        if strings.Contains(r.URL.Path, "..") {
            http.Error(w, "Forbidden", http.StatusForbidden)
            return
        }
        
        fileServer.ServeHTTP(w, r)
    })
}

func setupFileServer(mux *http.ServeMux, path string, directory string) {
    mux.Handle(path, FileServerMiddleware(directory, path))
}
```

### Практические примеры: **HTTP** клиент с **cookies**

```go
type CookieJar struct {
    cookies map[string]map[string]*http.Cookie
    mu      sync.RWMutex
}

func NewCookieJar() *CookieJar {
    return &CookieJar{
        cookies: make(map[string]map[string]*http.Cookie),
    }
}

func (cj *CookieJar) SetCookies(u *url.URL, cookies []*http.Cookie) {
    cj.mu.Lock()
    defer cj.mu.Unlock()
    
    if cj.cookies[u.Host] == nil {
        cj.cookies[u.Host] = make(map[string]*http.Cookie)
    }
    
    for _, cookie := range cookies {
        cj.cookies[u.Host][cookie.Name] = cookie
    }
}

func (cj *CookieJar) Cookies(u *url.URL) []*http.Cookie {
    cj.mu.RLock()
    defer cj.mu.RUnlock()
    
    cookies := make([]*http.Cookie, 0)
    if hostCookies, ok := cj.cookies[u.Host]; ok {
        for _, cookie := range hostCookies {
            cookies = append(cookies, cookie)
        }
    }
    
    return cookies
}

func NewClientWithCookies() *http.Client {
    return &http.Client{
        Jar:     NewCookieJar(),
        Timeout: 30 * time.Second,
    }
}
```

## Лучшие практики

1. **Используйте context** - передавайте **context** в запросы для отмены
2. **Обрабатывайте ошибки** - всегда проверяйте ошибки при работе с **HTTP**
3. **Закрывайте тела ответов** - используйте **defer** для закрытия **resp.Body**
4. **Используйте middleware** - для общей логики (**логирование, аутентификация**)
5. **Настраивайте таймауты** - устанавливайте таймауты для клиентов и серверов
6. **Используйте пулы соединений** - настраивайте **Transport** для переиспользования соединений
7. **Используйте graceful shutdown** - корректно завершайте работу сервера
8. **Валидируйте входные данные** - проверяйте все данные от клиентов
9. **Используйте HTTPS** - для защиты данных в транзите
10. **Мониторьте производительность** - отслеживайте метрики **HTTP** запросов
11. **Используйте compression** - для уменьшения размера ответов
12. **Используйте request ID** - для трейсинга запросов
13. **Реализуйте health checks** - для мониторинга состояния приложения
14. **Используйте circuit breaker** - для защиты от каскадных отказов
15. **Реализуйте retry логику** - для обработки временных ошибок

### Практические примеры: **Graceful shutdown HTTP** сервера

```go
func GracefulShutdown(server *http.Server, timeout time.Duration) error {
    quit := make(chan os.Signal, 1)
    signal.Notify(quit, os.Interrupt, syscall.SIGTERM)
    
    <-quit
    
    log.Println("Shutting down server...")
    
    ctx, cancel := context.WithTimeout(context.Background(), timeout)
    defer cancel()
    
    if err := server.Shutdown(ctx); err != nil {
        log.Fatal("Server forced to shutdown:", err)
        return err
    }
    
    log.Println("Server exited")
    return nil
}

// Использование
func main() {
    server := &http.Server{
        Addr:    ":8080",
        Handler: setupRouter(),
    }
    
    go func() {
        if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
            log.Fatalf("Server failed: %v", err)
        }
    }()
    
    if err := GracefulShutdown(server, 5*time.Second); err != nil {
        log.Fatal(err)
    }
}
```

### Практические примеры: **HTTP** клиент с **connection pooling**

```go
func NewPooledClient(maxIdleConns, maxConnsPerHost int) *http.Client {
    transport := &http.Transport{
        MaxIdleConns:        maxIdleConns,
        MaxConnsPerHost:     maxConnsPerHost,
        IdleConnTimeout:     90 * time.Second,
        DisableCompression:  false,
    }
    
    return &http.Client{
        Transport: transport,
        Timeout:   30 * time.Second,
    }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Работа с **HTTP** в Go предоставляет мощные инструменты для создания веб-приложений и **API**. Понимание **HTTP** сервера, **handlers**, клиента, **middleware**, роутинга, шаблонов, метрик, **health checks**, **graceful shutdown**, **connection pooling** и практических паттернов критично для создания эффективных веб-приложений. Правильное использование этих инструментов позволяет создавать масштабируемые, безопасные, производительные веб-приложения с хорошей наблюдаемостью, отказоустойчивостью и корректным управлением жизненным циклом.

## Дополнительные ресурсы

- [Go net/http Documentation](https://pkg.go.dev/net/http)
- [Go HTTP Server Tutorial](https://go.dev/doc/articles/wiki/)
- [Go HTTP Client Tutorial](https://go.dev/doc/tutorial/web-service-gin)
