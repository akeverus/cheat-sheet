---
title: "Go: стандартная библиотека - JSON"
description: "Полное руководство по работе с JSON в Go: marshaling, unmarshaling, custom marshaling, JSON tags, streaming"
tags:
  - go
  - golang
  - json
  - serialization
  - encoding
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: стандартная библиотека — JSON

## Полезные ссылки

- [Go encoding/json Documentation](https://pkg.go.dev/encoding/json)
- [Go JSON Blog Post](https://go.dev/blog/json)

## Содержание

- [Go: стандартная библиотека — **JSON**](#go-стандартная-библиотека-json)
- [Введение в **JSON**](#введение-в-json)
  - [Основные операции](#основные-операции)
- [**Marshaling** (Сериализация)](#marshaling-сериализация)
  - [Базовый **marshaling**](#базовый-marshaling)
  - [Форматированный **JSON**](#форматированный-json)
  - [**Marshaling** в **Writer**](#marshaling-в-writer)
- [**Unmarshaling** (Десериализация)](#unmarshaling-десериализация)
  - [Базовый **unmarshaling**](#базовый-unmarshaling)
  - [**Unmarshaling** из **Reader**](#unmarshaling-из-reader)
  - [Частичный **unmarshaling**](#частичный-unmarshaling)
- [**JSON Tags**](#json-tags)
  - [Базовые теги](#базовые-теги)
  - [Теги для управления сериализацией](#теги-для-управления-сериализацией)
- [**Custom Marshaling**](#custom-marshaling)
  - [Реализация **json.Marshaler**](#реализация-jsonmarshaler)
  - [Использование **custom marshaling**](#использование-custom-marshaling)
- [**Streaming JSON**](#streaming-json)
  - [**Streaming encoder**](#streaming-encoder)
  - [**Streaming decoder**](#streaming-decoder)
  - [**Marshaling** вложенных структур](#marshaling-вложенных-структур)
  - [**Marshaling** массивов и слайсов](#marshaling-массивов-и-слайсов)
  - [**Marshaling** карт](#marshaling-карт)
  - [**Marshaling** указателей](#marshaling-указателей)
  - [**Unmarshaling** в карты](#unmarshaling-в-карты)
  - [**Unmarshaling** с валидацией](#unmarshaling-с-валидацией)
  - [**Unmarshaling** частичных данных](#unmarshaling-частичных-данных)
  - [**Custom Unmarshaler** для валидации](#custom-unmarshaler-для-валидации)
  - [Работа с числами как строками](#работа-с-числами-как-строками)
  - [Работа с **raw JSON**](#работа-с-raw-json)
  - [Практические примеры: **API** сериализация](#практические-примеры-api-сериализация)
  - [Практические примеры: Парсинг **JSON** конфигурации](#практические-примеры-парсинг-json-конфигурации)
  - [Практические примеры: **JSON** патчинг](#практические-примеры-json-патчинг)
  - [Практические примеры: **JSON** трансформация](#практические-примеры-json-трансформация)
  - [Практические примеры: **JSON** валидация схемы](#практические-примеры-json-валидация-схемы)
  - [Практические примеры: **JSON streaming** для больших данных](#практические-примеры-json-streaming-для-больших-данных)
  - [Практические примеры: **JSON** инкрементальный парсинг](#практические-примеры-json-инкрементальный-парсинг)
  - [Практические примеры: **JSON** сжатие](#практические-примеры-json-сжатие)
  - [Практические примеры: **JSON** кэширование](#практические-примеры-json-кэширование)
  - [Практические примеры: Валидация **JSON**](#практические-примеры-валидация-json)
  - [Практические примеры: **JSON Patch**](#практические-примеры-json-patch)
  - [Практические примеры: **JSON Schema** валидация](#практические-примеры-json-schema-валидация)
  - [Практические примеры: **JSON** с индентацией](#практические-примеры-json-с-индентацией)
  - [Практические примеры: **JSON streaming** с обработкой ошибок](#практические-примеры-json-streaming-с-обработкой-ошибок)
  - [Практические примеры: **JSON** с контекстом](#практические-примеры-json-с-контекстом)
  - [Практические примеры: **JSON** с компрессией](#практические-примеры-json-с-компрессией)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в JSON

Пакет `encoding/json` предоставляет полную поддержку работы с **JSON** в Go. Понимание работы с **JSON** критично для создания **API** и работы с внешними сервисами.

### Основные операции

1. **Marshaling** — преобразование Go структур в **JSON**
2. **Unmarshaling** — преобразование **JSON** в Go структуры
3. **Custom Marshaling** — кастомная логика сериализации/десериализации

## Marshaling (Сериализация)

**Marshaling** преобразует Go структуры в **JSON** формат.

### Базовый marshaling

```go
import (
    "encoding/json"
    "fmt"
)

type User struct {
    ID   int    `json:"id"`
    Name string `json:"name"`
    Age  int    `json:"age"`
}

func main() {
    user := User{ID: 1, Name: "Alice", Age: 30}

    jsonData, err := json.Marshal(user)
    if err != nil {
        log.Fatal(err)
    }

    fmt.Println(string(jsonData))
    // Output: {"id":1,"name":"Alice","age":30}
}
```

### Форматированный JSON

```go
import "encoding/json"

func main() {
    user := User{ID: 1, Name: "Alice", Age: 30}

    jsonData, err := json.MarshalIndent(user, "", "  ")
    if err != nil {
        log.Fatal(err)
    }

    fmt.Println(string(jsonData))
    // Output:
    // {
    //   "id": 1,
    //   "name": "Alice",
    //   "age": 30
    // }
}
```

### Marshaling в Writer

```go
import (
    "encoding/json"
    "os"
)

func main() {
    user := User{ID: 1, Name: "Alice", Age: 30}

    encoder := json.NewEncoder(os.Stdout)
    encoder.SetIndent("", "  ")
    encoder.Encode(user)
}
```

## Unmarshaling (Десериализация)

**Unmarshaling** преобразует **JSON** в Go структуры.

### Базовый unmarshaling

```go
import "encoding/json"

func main() {
    jsonData := `{"id":1,"name":"Alice","age":30}`

    var user User
    err := json.Unmarshal([]byte(jsonData), &user)
    if err != nil {
        log.Fatal(err)
    }

    fmt.Printf("User: %+v\n", user)
    // Output: User: {ID:1 Name:Alice Age:30}
}
```

### Unmarshaling из Reader

```go
import (
    "encoding/json"
    "strings"
)

func main() {
    jsonData := `{"id":1,"name":"Alice","age":30}`
    reader := strings.NewReader(jsonData)

    var user User
    decoder := json.NewDecoder(reader)
    err := decoder.Decode(&user)
    if err != nil {
        log.Fatal(err)
    }
}
```

### Частичный unmarshaling

```go
import "encoding/json"

func main() {
    jsonData := `{"id":1,"name":"Alice","age":30,"email":"alice@example.com"}`

    // Unmarshaling только в поля, которые есть в структуре
    var user User
    json.Unmarshal([]byte(jsonData), &user)
    // email будет проигнорирован, т.к. его нет в структуре User
}
```

## JSON Tags

**JSON tags** позволяют контролировать сериализацию полей структуры.

### Базовые теги

```go
type User struct {
    ID       int    `json:"id"`
    Name     string `json:"name"`
    Age      int    `json:"age"`
    Email    string `json:"email,omitempty"`  // пропуск, если пустое
    Password string `json:"-"`                // игнорирование поля
}
```

### Теги для управления сериализацией

```go
type User struct {
    ID       int    `json:"id"`
    Name     string `json:"name"`
    Age      int    `json:"age,string"`      // сериализация как строка
    Email    string `json:"email,omitempty"`  // пропуск, если пустое
    Password string `json:"-"`                // игнорирование
    Metadata string `json:"metadata,omitempty"`
}
```

## Custom Marshaling

**Custom marshaling** позволяет определять собственную логику сериализации.

### Реализация json.Marshaler

```go
import "encoding/json"

type CustomDate struct {
    time.Time
}

func (d CustomDate) MarshalJSON() ([]byte, error) {
    return json.Marshal(d.Time.Format("2006-01-02"))
}

func (d *CustomDate) UnmarshalJSON(data []byte) error {
    var str string
    if err := json.Unmarshal(data, &str); err != nil {
        return err
    }

    t, err := time.Parse("2006-01-02", str)
    if err != nil {
        return err
    }

    d.Time = t
    return nil
}
```

### Использование custom marshaling

```go
type User struct {
    ID   int        `json:"id"`
    Name string     `json:"name"`
    DOB  CustomDate `json:"dob"`
}

func main() {
    user := User{
        ID:   1,
        Name: "Alice",
        DOB:  CustomDate{Time: time.Now()},
    }

    jsonData, _ := json.Marshal(user)
    fmt.Println(string(jsonData))
    // Output: {"id":1,"name":"Alice","dob":"2025-01-11"}
}
```

## Streaming JSON

**Streaming JSON** позволяет обрабатывать большие **JSON** файлы без загрузки всего в память.

### Streaming encoder

```go
import (
    "encoding/json"
    "os"
)

func main() {
    encoder := json.NewEncoder(os.Stdout)

    users := []User{
        {ID: 1, Name: "Alice"},
        {ID: 2, Name: "Bob"},
    }

    for _, user := range users {
        encoder.Encode(user)
    }
}
```

### Streaming decoder

```go
import (
    "encoding/json"
    "io"
)

func main() {
    decoder := json.NewDecoder(reader)

    for {
        var user User
        if err := decoder.Decode(&user); err == io.EOF {
            break
        } else if err != nil {
            log.Fatal(err)
        }

        // Обработка user
        processUser(user)
    }
}
```

### Marshaling вложенных структур

```go
type Address struct {
    Street  string `json:"street"`
    City    string `json:"city"`
    Country string `json:"country"`
}

type User struct {
    ID      int     `json:"id"`
    Name    string  `json:"name"`
    Address Address `json:"address"`
}

func main() {
    user := User{
        ID:   1,
        Name: "Alice",
        Address: Address{
            Street:  "123 Main St",
            City:    "New York",
            Country: "USA",
        },
    }

    jsonData, _ := json.Marshal(user)
    fmt.Println(string(jsonData))
    // {"id":1,"name":"Alice","address":{"street":"123 Main St","city":"New York","country":"USA"}}
}
```

### Marshaling массивов и слайсов

```go
type User struct {
    ID    int      `json:"id"`
    Name  string   `json:"name"`
    Tags  []string `json:"tags"`
}

func main() {
    user := User{
        ID:   1,
        Name: "Alice",
        Tags: []string{"admin", "user", "developer"},
    }

    jsonData, _ := json.Marshal(user)
    fmt.Println(string(jsonData))
    // {"id":1,"name":"Alice","tags":["admin","user","developer"]}
}
```

### Marshaling карт

```go
func main() {
    data := map[string]interface{}{
        "name":  "Alice",
        "age":   30,
        "email": "alice@example.com",
    }

    jsonData, _ := json.Marshal(data)
    fmt.Println(string(jsonData))
    // {"age":30,"email":"alice@example.com","name":"Alice"}
}
```

### Marshaling указателей

```go
type User struct {
    ID   *int    `json:"id,omitempty"`
    Name string  `json:"name"`
    Age  *int    `json:"age,omitempty"`
}

func main() {
    id := 1
    age := 30

    user := User{
        ID:   &id,
        Name: "Alice",
        Age:  &age,
    }

    jsonData, _ := json.Marshal(user)
    fmt.Println(string(jsonData))
    // {"id":1,"name":"Alice","age":30}

    // Если указатель nil, поле будет пропущено с omitempty
    user2 := User{
        Name: "Bob",
        // ID и Age будут nil, поэтому пропущены
    }
    jsonData2, _ := json.Marshal(user2)
    fmt.Println(string(jsonData2))
    // {"name":"Bob"}
}
```

### Unmarshaling в карты

```go
func main() {
    jsonData := `{"name":"Alice","age":30,"email":"alice@example.com"}`

    var data map[string]interface{}
    json.Unmarshal([]byte(jsonData), &data)

    fmt.Println(data["name"])  // Alice
    fmt.Println(data["age"])    // 30
}
```

### Unmarshaling с валидацией

```go
type User struct {
    ID    int    `json:"id"`
    Name  string `json:"name"`
    Email string `json:"email"`
}

func unmarshalWithValidation(jsonData []byte) (*User, error) {
    var user User
    if err := json.Unmarshal(jsonData, &user); err != nil {
        return nil, err
    }

    // Валидация
    if user.Name == "" {
        return nil, fmt.Errorf("name is required")
    }

    if user.Email == "" {
        return nil, fmt.Errorf("email is required")
    }

    return &user, nil
}
```

### Unmarshaling частичных данных

```go
type PartialUser struct {
    ID   int    `json:"id"`
    Name string `json:"name"`
}

func main() {
    // Полный JSON
    jsonData := `{"id":1,"name":"Alice","age":30,"email":"alice@example.com"}`

    // Unmarshaling только в нужные поля
    var user PartialUser
    json.Unmarshal([]byte(jsonData), &user)
    // age и email будут проигнорированы
}
```

### Custom Unmarshaler для валидации

```go
type Email string

func (e *Email) UnmarshalJSON(data []byte) error {
    var s string
    if err := json.Unmarshal(data, &s); err != nil {
        return err
    }

    // Валидация email
    if !strings.Contains(s, "@") {
        return fmt.Errorf("invalid email format")
    }

    *e = Email(s)
    return nil
}

type User struct {
    ID    int    `json:"id"`
    Name  string `json:"name"`
    Email Email  `json:"email"`
}
```

### Работа с числами как строками

```go
type User struct {
    ID   int    `json:"id,string"`  // Сериализация как строка
    Name string `json:"name"`
}

func main() {
    user := User{ID: 1, Name: "Alice"}
    jsonData, _ := json.Marshal(user)
    fmt.Println(string(jsonData))
    // {"id":"1","name":"Alice"}

    // Unmarshaling обратно
    jsonData2 := `{"id":"1","name":"Alice"}`
    var user2 User
    json.Unmarshal([]byte(jsonData2), &user2)
    fmt.Println(user2.ID)  // 1
}
```

### Работа с raw JSON

```go
import "encoding/json"

type User struct {
    ID       int             `json:"id"`
    Name     string          `json:"name"`
    Metadata json.RawMessage `json:"metadata"`  // Сохранение как raw JSON
}

func main() {
    jsonData := `{"id":1,"name":"Alice","metadata":{"key":"value","nested":{"a":1}}}`

    var user User
    json.Unmarshal([]byte(jsonData), &user)

    // Metadata остается как raw JSON
    fmt.Println(string(user.Metadata))
    // {"key":"value","nested":{"a":1}}
}
```

### Практические примеры: API сериализация

```go
type APIResponse struct {
    Success bool        `json:"success"`
    Data    interface{} `json:"data,omitempty"`
    Error   string      `json:"error,omitempty"`
}

func successResponse(data interface{}) []byte {
    response := APIResponse{
        Success: true,
        Data:    data,
    }
    jsonData, _ := json.Marshal(response)
    return jsonData
}

func errorResponse(err error) []byte {
    response := APIResponse{
        Success: false,
        Error:   err.Error(),
    }
    jsonData, _ := json.Marshal(response)
    return jsonData
}
```

### Практические примеры: Парсинг JSON конфигурации

```go
type Config struct {
    Server   ServerConfig   `json:"server"`
    Database DatabaseConfig `json:"database"`
    Cache    CacheConfig    `json:"cache"`
}

type ServerConfig struct {
    Host string `json:"host"`
    Port int    `json:"port"`
}

type DatabaseConfig struct {
    Host     string `json:"host"`
    Port     int    `json:"port"`
    Database string `json:"database"`
    Username string `json:"username"`
    Password string `json:"password"`
}

type CacheConfig struct {
    Type     string `json:"type"`
    Host     string `json:"host"`
    Port     int    `json:"port"`
    TTL      int    `json:"ttl"`
}

func loadConfig(filename string) (*Config, error) {
    data, err := os.ReadFile(filename)
    if err != nil {
        return nil, err
    }

    var config Config
    if err := json.Unmarshal(data, &config); err != nil {
        return nil, err
    }

    return &config, nil
}
```

### Практические примеры: JSON патчинг

```go
func patchUser(userID int, patch map[string]interface{}) error {
    // Получение текущего пользователя
    var user User
    err := db.QueryRow("SELECT * FROM users WHERE id = $1", userID).
        Scan(&user.ID, &user.Name, &user.Email)
    if err != nil {
        return err
    }

    // Сериализация в JSON
    userJSON, _ := json.Marshal(user)

    // Применение патча
    var userMap map[string]interface{}
    json.Unmarshal(userJSON, &userMap)

    for key, value := range patch {
        userMap[key] = value
    }

    // Обратная сериализация
    patchedJSON, _ := json.Marshal(userMap)
    json.Unmarshal(patchedJSON, &user)

    // Сохранение
    _, err = db.Exec(
        "UPDATE users SET name = $1, email = $2 WHERE id = $3",
        user.Name, user.Email, user.ID,
    )
    return err
}
```

### Практические примеры: JSON трансформация

```go
func transformJSON(input []byte, transformer func(map[string]interface{}) map[string]interface{}) ([]byte, error) {
    var data map[string]interface{}
    if err := json.Unmarshal(input, &data); err != nil {
        return nil, err
    }

    transformed := transformer(data)
    return json.Marshal(transformed)
}

// Использование
func addTimestamp(data map[string]interface{}) map[string]interface{} {
    data["timestamp"] = time.Now().Unix()
    return data
}
```

### Практические примеры: JSON валидация схемы

```go
func validateJSONSchema(data []byte, schema map[string]interface{}) error {
    var jsonData map[string]interface{}
    if err := json.Unmarshal(data, &jsonData); err != nil {
        return err
    }

    // Простая валидация (в реальности используйте библиотеку)
    requiredFields, ok := schema["required"].([]interface{})
    if !ok {
        return nil
    }

    for _, field := range requiredFields {
        fieldName := field.(string)
        if _, exists := jsonData[fieldName]; !exists {
            return fmt.Errorf("required field %s is missing", fieldName)
        }
    }

    return nil
}
```

### Практические примеры: JSON streaming для больших данных

```go
func streamLargeJSON(w io.Writer, items []Item) error {
    encoder := json.NewEncoder(w)

    // Начало массива
    w.Write([]byte("["))

    for i, item := range items {
        if i > 0 {
            w.Write([]byte(","))
        }

        if err := encoder.Encode(item); err != nil {
            return err
        }
    }

    // Конец массива
    w.Write([]byte("]"))

    return nil
}
```

### Практические примеры: JSON инкрементальный парсинг

```go
func parseJSONStream(reader io.Reader, callback func(map[string]interface{}) error) error {
    decoder := json.NewDecoder(reader)

    // Пропуск начального токена
    token, err := decoder.Token()
    if err != nil {
        return err
    }

    if delim, ok := token.(json.Delim); !ok || delim != '[' {
        return fmt.Errorf("expected array")
    }

    // Парсинг элементов
    for decoder.More() {
        var item map[string]interface{}
        if err := decoder.Decode(&item); err != nil {
            return err
        }

        if err := callback(item); err != nil {
            return err
        }
    }

    // Пропуск конечного токена
    token, err = decoder.Token()
    if err != nil {
        return err
    }

    return nil
}
```

### Практические примеры: JSON сжатие

```go
import "compress/gzip"

func compressJSON(data []byte) ([]byte, error) {
    var buf bytes.Buffer
    writer := gzip.NewWriter(&buf)

    if _, err := writer.Write(data); err != nil {
        return nil, err
    }

    if err := writer.Close(); err != nil {
        return nil, err
    }

    return buf.Bytes(), nil
}

func decompressJSON(compressed []byte) ([]byte, error) {
    reader, err := gzip.NewReader(bytes.NewReader(compressed))
    if err != nil {
        return nil, err
    }
    defer reader.Close()

    return io.ReadAll(reader)
}
```

### Практические примеры: JSON кэширование

```go
type JSONCache struct {
    cache map[string][]byte
    mu    sync.RWMutex
}

func (c *JSONCache) Get(key string, target interface{}) error {
    c.mu.RLock()
    data, ok := c.cache[key]
    c.mu.RUnlock()

    if !ok {
        return fmt.Errorf("cache miss")
    }

    return json.Unmarshal(data, target)
}

func (c *JSONCache) Set(key string, value interface{}) error {
    data, err := json.Marshal(value)
    if err != nil {
        return err
    }

    c.mu.Lock()
    c.cache[key] = data
    c.mu.Unlock()

    return nil
}
```

### Практические примеры: Валидация JSON

```go
import "github.com/go-playground/validator/v10"

type User struct {
    Email string `json:"email" validate:"required,email"`
    Age   int    `json:"age" validate:"required,min=18,max=120"`
    Name  string `json:"name" validate:"required,min=2,max=50"`
}

func ValidateJSON(data []byte, v interface{}) error {
    if err := json.Unmarshal(data, v); err != nil {
        return fmt.Errorf("unmarshal error: %w", err)
    }

    validate := validator.New()
    if err := validate.Struct(v); err != nil {
        return fmt.Errorf("validation error: %w", err)
    }

    return nil
}
```

### Практические примеры: JSON Patch

```go
import "github.com/evanphx/json-patch/v5"

func ApplyJSONPatch(original []byte, patch []byte) ([]byte, error) {
    patched, err := jsonpatch.MergePatch(original, patch)
    if err != nil {
        return nil, err
    }
    return patched, nil
}

// Использование
original := []byte(`{"name":"Alice","age":30}`)
patch := []byte(`{"age":31,"city":"New York"}`)
patched, _ := ApplyJSONPatch(original, patch)
```

### Практические примеры: JSON Schema валидация

```go
import "github.com/xeipuuv/gojsonschema"

func ValidateJSONSchema(data []byte, schema []byte) error {
    documentLoader := gojsonschema.NewBytesLoader(data)
    schemaLoader := gojsonschema.NewBytesLoader(schema)

    result, err := gojsonschema.Validate(schemaLoader, documentLoader)
    if err != nil {
        return err
    }

    if !result.Valid() {
        var errors []string
        for _, desc := range result.Errors() {
            errors = append(errors, desc.String())
        }
        return fmt.Errorf("validation errors: %v", errors)
    }

    return nil
}
```

### Практические примеры: JSON с индентацией

```go
func PrettyJSON(data interface{}) ([]byte, error) {
    return json.MarshalIndent(data, "", "  ")
}

func PrettyJSONString(data interface{}) (string, error) {
    b, err := PrettyJSON(data)
    if err != nil {
        return "", err
    }
    return string(b), nil
}
```

### Практические примеры: JSON streaming с обработкой ошибок

```go
func StreamJSONArray(reader io.Reader, processor func(interface{}) error) error {
    decoder := json.NewDecoder(reader)

    // Читаем открывающую скобку
    token, err := decoder.Token()
    if err != nil {
        return err
    }
    if delim, ok := token.(json.Delim); !ok || delim != '[' {
        return fmt.Errorf("expected array start")
    }

    // Читаем элементы массива
    for decoder.More() {
        var item interface{}
        if err := decoder.Decode(&item); err != nil {
            return fmt.Errorf("decode error: %w", err)
        }

        if err := processor(item); err != nil {
            return fmt.Errorf("processor error: %w", err)
        }
    }

    // Читаем закрывающую скобку
    token, err = decoder.Token()
    if err != nil {
        return err
    }
    if delim, ok := token.(json.Delim); !ok || delim != ']' {
        return fmt.Errorf("expected array end")
    }

    return nil
}
```

### Практические примеры: JSON с контекстом

```go
func MarshalWithContext(ctx context.Context, v interface{}) ([]byte, error) {
    type contextValue struct {
        RequestID string `json:"request_id,omitempty"`
        UserID    string `json:"user_id,omitempty"`
    }

    var cv contextValue
    if reqID := ctx.Value("request_id"); reqID != nil {
        cv.RequestID = reqID.(string)
    }
    if userID := ctx.Value("user_id"); userID != nil {
        cv.UserID = userID.(string)
    }

    data := map[string]interface{}{
        "data":    v,
        "context": cv,
    }

    return json.Marshal(data)
}
```

### Практические примеры: JSON с компрессией

```go
import "compress/gzip"

func CompressJSON(data interface{}) ([]byte, error) {
    jsonData, err := json.Marshal(data)
    if err != nil {
        return nil, err
    }

    var buf bytes.Buffer
    writer := gzip.NewWriter(&buf)

    if _, err := writer.Write(jsonData); err != nil {
        writer.Close()
        return nil, err
    }

    if err := writer.Close(); err != nil {
        return nil, err
    }

    return buf.Bytes(), nil
}

func DecompressJSON(data []byte, v interface{}) error {
    reader, err := gzip.NewReader(bytes.NewReader(data))
    if err != nil {
        return err
    }
    defer reader.Close()

    decoder := json.NewDecoder(reader)
    return decoder.Decode(v)
}
```

## Лучшие практики

1. **Используйте `JSON` tags** — для контроля сериализации полей
2. **Обрабатывайте ошибки** — всегда проверяйте ошибки при **marshaling**/**unmarshaling**
3. **Используйте omitempty** — для пропуска пустых полей
4. **Используйте streaming** — для больших **JSON** файлов
5. **Валидируйте данные** — проверяйте данные после **unmarshaling**
6. **Используйте custom marshaling** — для сложных типов данных
7. **Используйте json.RawMessage** — для сохранения **raw JSON**
8. **Избегайте interface{}** — используйте конкретные типы когда возможно
9. **Используйте streaming для больших данных** — для экономии памяти
10. **Кэшируйте скомпилированные JSON** — для часто используемых данных
11. **Используйте валидацию** — проверяйте структуру данных
12. **Используйте `JSON` Schema** — для строгой валидации
13. **Используйте компрессию** — для уменьшения размера
14. **Обрабатывайте контекст** — передавайте метаданные в **JSON**
15. **Используйте pretty printing** — для отладки и логирования

### Практические примеры: JSON streaming для больших данных

```go
func StreamJSONArray(w io.Writer, items []interface{}) error {
    encoder := json.NewEncoder(w)

    w.Write([]byte("["))
    for i, item := range items {
        if i > 0 {
            w.Write([]byte(","))
        }
        if err := encoder.Encode(item); err != nil {
            return err
        }
    }
    w.Write([]byte("]"))

    return nil
}

func StreamJSONObjects(reader io.Reader, processor func(map[string]interface{}) error) error {
    decoder := json.NewDecoder(reader)

    // Пропуск открывающей скобки
    token, err := decoder.Token()
    if err != nil {
        return err
    }
    if delim, ok := token.(json.Delim); !ok || delim != '[' {
        return fmt.Errorf("expected array start")
    }

    for decoder.More() {
        var obj map[string]interface{}
        if err := decoder.Decode(&obj); err != nil {
            return err
        }
        if err := processor(obj); err != nil {
            return err
        }
    }

    return nil
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Работа с **JSON** в Go предоставляет мощные инструменты для сериализации и десериализации данных. Понимание **marshaling**, **unmarshaling**, **JSON tags**, **custom marshaling**, **streaming**, валидации, **streaming** для больших данных и практических техник критично для создания эффективных приложений, работающих с **JSON**. Правильное использование этих инструментов позволяет создавать масштабируемые, производительные приложения, которые эффективно обрабатывают **JSON** данные любого размера, обеспечивают валидность данных и оптимальное использование ресурсов.

## Дополнительные ресурсы

- [Go encoding/json Documentation](https://pkg.go.dev/encoding/json)
- [Go JSON Blog Post](https://go.dev/blog/json)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
