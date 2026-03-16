---
title: "Go: логирование"
description: "Полное руководство по логированию в Go: log package, structured logging, уровни логирования, best practices"
tags: ["go", "golang", "logging", "log", "structured-logging"]
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: логирование

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

- [Go log Package](https://pkg.go.dev/log)
- [Go log/slog](https://pkg.go.dev/log/slog)

## Содержание

- [Go: логирование](#go-логирование)
- [Введение в логирование](#введение-в-логирование)
  - [Основные концепции](#основные-концепции)
- [Стандартный **log** пакет](#стандартный-log-пакет)
  - [Базовое логирование](#базовое-логирование)
  - [Уровни логирования](#уровни-логирования)
  - [Настройка логгера](#настройка-логгера)
  - [Логирование в файл](#логирование-в-файл)
- [**Structured Logging**](#structured-logging)
  - [**log**/**slog** (**Go 1.21+**)](#logslog-go-121)
  - [**JSON** логирование](#json-логирование)
  - [Настройка уровней](#настройка-уровней)
  - [Определение уровней](#определение-уровней)
- [Кастомные логгеры](#кастомные-логгеры)
  - [Логгер с контекстом](#логгер-с-контекстом)
  - [Асинхронное логирование](#асинхронное-логирование)
  - [Практические примеры: Логгер с ротацией](#практические-примеры-логгер-с-ротацией)
  - [Практические примеры: Логгер с уровнями и фильтрацией](#практические-примеры-логгер-с-уровнями-и-фильтрацией)
  - [Практические примеры: **Structured logger** с контекстом](#практические-примеры-structured-logger-с-контекстом)
  - [Практические примеры: Логгер с метриками](#практические-примеры-логгер-с-метриками)
  - [Практические примеры: Логгер с маскировкой чувствительных данных](#практические-примеры-логгер-с-маскировкой-чувствительных-данных)
  - [Практические примеры: Логгер с буферизацией](#практические-примеры-логгер-с-буферизацией)
  - [Практические примеры: Логирование с ротацией](#практические-примеры-логирование-с-ротацией)
  - [Практические примеры: Контекстное логирование](#практические-примеры-контекстное-логирование)
  - [Практические примеры: Логирование с уровнями](#практические-примеры-логирование-с-уровнями)
  - [Практические примеры: Логирование производительности](#практические-примеры-логирование-производительности)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в логирование

Логирование критично для отладки, мониторинга и аудита приложений. Go предоставляет несколько способов логирования.

### Основные концепции

1. **Уровни логирования** - **DEBUG**, **INFO**, **WARN**, **ERROR**
2. **Structured Logging** - структурированные логи для анализа
3. **Контекст** - добавление контекста к логам
4. **Производительность** - эффективное логирование

## Стандартный **log** пакет

### Базовое логирование

```go
import "log"

func main() {
    log.Print("This is a log message")
    log.Printf("Formatted log: %s", "value")
    log.Println("Log with newline")
}
```

### Уровни логирования

```go
import "log"

func main() {
    log.Print("Info message")
    log.Fatal("Fatal error - exits program")
    log.Panic("Panic - exits program with stack trace")
}
```

### Настройка логгера

```go
import (
    "log"
    "os"
)

func main() {
    // Настройка префикса
    log.SetPrefix("APP: ")
    
    // Настройка флагов
    log.SetFlags(log.Ldate | log.Ltime | log.Lshortfile)
    
    // Настройка вывода
    log.SetOutput(os.Stdout)
    
    log.Println("Custom formatted log")
}
```

### Логирование в файл

```go
import (
    "log"
    "os"
)

func main() {
    file, err := os.OpenFile("app.log", os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0666)
    if err != nil {
        log.Fatal(err)
    }
    defer file.Close()
    
    log.SetOutput(file)
    log.Println("Log to file")
}
```

## **Structured Logging**

### **log**/**slog** (**Go 1.21+**)

```go
import (
    "log/slog"
    "os"
)

func main() {
    logger := slog.New(slog.NewTextHandler(os.Stdout, nil))
    
    logger.Info("User logged in",
        "user_id", 123,
        "ip", "192.168.1.1",
    )
    
    logger.Error("Failed to process request",
        "error", "connection timeout",
        "request_id", "abc123",
    )
}
```

### **JSON** логирование

```go
import (
    "log/slog"
    "os"
)

func main() {
    logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))
    
    logger.Info("User action",
        "user_id", 123,
        "action", "purchase",
        "amount", 99.99,
    )
}
```

### Настройка уровней

```go
import (
    "log/slog"
    "os"
)

func main() {
    opts := &slog.HandlerOptions{
        Level: slog.LevelDebug,
    }
    
    logger := slog.New(slog.NewTextHandler(os.Stdout, opts))
    
    logger.Debug("Debug message")
    logger.Info("Info message")
    logger.Warn("Warning message")
    logger.Error("Error message")
}
```

## Уровни логирования

### Определение уровней

```go
import "log"

const (
    LevelDebug = iota
    LevelInfo
    LevelWarn
    LevelError
)

type Logger struct {
    level int
}

func (l *Logger) Debug(msg string) {
    if l.level <= LevelDebug {
        log.Printf("[DEBUG] %s", msg)
    }
}

func (l *Logger) Info(msg string) {
    if l.level <= LevelInfo {
        log.Printf("[INFO] %s", msg)
    }
}

func (l *Logger) Warn(msg string) {
    if l.level <= LevelWarn {
        log.Printf("[WARN] %s", msg)
    }
}

func (l *Logger) Error(msg string) {
    if l.level <= LevelError {
        log.Printf("[ERROR] %s", msg)
    }
}
```

## Кастомные логгеры

### Логгер с контекстом

```go
type ContextLogger struct {
    fields map[string]interface{}
    logger *log.Logger
}

func NewContextLogger() *ContextLogger {
    return &ContextLogger{
        fields: make(map[string]interface{}),
        logger: log.New(os.Stdout, "", log.LstdFlags),
    }
}

func (l *ContextLogger) WithField(key string, value interface{}) *ContextLogger {
    newLogger := &ContextLogger{
        fields: make(map[string]interface{}),
        logger: l.logger,
    }
    for k, v := range l.fields {
        newLogger.fields[k] = v
    }
    newLogger.fields[key] = value
    return newLogger
}

func (l *ContextLogger) Info(msg string) {
    l.logger.Printf("[INFO] %s %v", msg, l.fields)
}
```

### Асинхронное логирование

```go
type AsyncLogger struct {
    logCh chan string
    done  chan bool
}

func NewAsyncLogger() *AsyncLogger {
    logger := &AsyncLogger{
        logCh: make(chan string, 100),
        done:  make(chan bool),
    }
    
    go logger.process()
    return logger
}

func (l *AsyncLogger) process() {
    for {
        select {
        case msg := <-l.logCh:
            log.Println(msg)
        case <-l.done:
            return
        }
    }
}

func (l *AsyncLogger) Log(msg string) {
    select {
    case l.logCh <- msg:
    default:
        // Буфер переполнен, пропускаем
    }
}

func (l *AsyncLogger) Close() {
    close(l.done)
}
```

### Практические примеры: Логгер с ротацией

```go
import (
    "log"
    "os"
    "time"
)

type RotatingLogger struct {
    file     *os.File
    logger   *log.Logger
    maxSize  int64
    filePath string
    mu       sync.Mutex
}

func NewRotatingLogger(filePath string, maxSize int64) (*RotatingLogger, error) {
    file, err := os.OpenFile(filePath, os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0666)
    if err != nil {
        return nil, err
    }
    
    return &RotatingLogger{
        file:     file,
        logger:   log.New(file, "", log.LstdFlags),
        maxSize:  maxSize,
        filePath: filePath,
    }, nil
}

func (rl *RotatingLogger) Write(p []byte) (n int, err error) {
    rl.mu.Lock()
    defer rl.mu.Unlock()
    
    // Проверка размера файла
    info, err := rl.file.Stat()
    if err != nil {
        return 0, err
    }
    
    if info.Size() >= rl.maxSize {
        rl.file.Close()
        
        // Ротация: переименование старого файла
        backupPath := rl.filePath + "." + time.Now().Format("20060102-150405")
        os.Rename(rl.filePath, backupPath)
        
        // Создание нового файла
        rl.file, err = os.OpenFile(rl.filePath, os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0666)
        if err != nil {
            return 0, err
        }
        rl.logger.SetOutput(rl.file)
    }
    
    return rl.file.Write(p)
}
```

### Практические примеры: Логгер с уровнями и фильтрацией

```go
type Level int

const (
    LevelDebug Level = iota
    LevelInfo
    LevelWarn
    LevelError
)

type LeveledLogger struct {
    level  Level
    logger *log.Logger
    mu     sync.Mutex
}

func NewLeveledLogger(level Level) *LeveledLogger {
    return &LeveledLogger{
        level:  level,
        logger: log.New(os.Stdout, "", log.LstdFlags|log.Lshortfile),
    }
}

func (ll *LeveledLogger) SetLevel(level Level) {
    ll.mu.Lock()
    defer ll.mu.Unlock()
    ll.level = level
}

func (ll *LeveledLogger) Debug(format string, v ...interface{}) {
    if ll.level <= LevelDebug {
        ll.logger.Printf("[DEBUG] "+format, v...)
    }
}

func (ll *LeveledLogger) Info(format string, v ...interface{}) {
    if ll.level <= LevelInfo {
        ll.logger.Printf("[INFO] "+format, v...)
    }
}

func (ll *LeveledLogger) Warn(format string, v ...interface{}) {
    if ll.level <= LevelWarn {
        ll.logger.Printf("[WARN] "+format, v...)
    }
}

func (ll *LeveledLogger) Error(format string, v ...interface{}) {
    if ll.level <= LevelError {
        ll.logger.Printf("[ERROR] "+format, v...)
    }
}
```

### Практические примеры: **Structured logger** с контекстом

```go
type StructuredLogger struct {
    handler slog.Handler
    logger  *slog.Logger
}

func NewStructuredLogger(level slog.Level) *StructuredLogger {
    opts := &slog.HandlerOptions{
        Level: level,
        AddSource: true,
    }
    
    handler := slog.NewJSONHandler(os.Stdout, opts)
    logger := slog.New(handler)
    
    return &StructuredLogger{
        handler: handler,
        logger:  logger,
    }
}

func (sl *StructuredLogger) WithContext(ctx context.Context) *slog.Logger {
    // Извлечение значений из context
    if userID, ok := ctx.Value("userID").(int); ok {
        return sl.logger.With("user_id", userID)
    }
    return sl.logger
}

func (sl *StructuredLogger) LogRequest(ctx context.Context, method, path string, statusCode int, duration time.Duration) {
    logger := sl.WithContext(ctx)
    logger.Info("HTTP request",
        "method", method,
        "path", path,
        "status", statusCode,
        "duration_ms", duration.Milliseconds(),
    )
}
```

### Практические примеры: Логгер с метриками

```go
type MetricsLogger struct {
    logger  *slog.Logger
    metrics map[string]int64
    mu      sync.RWMutex
}

func NewMetricsLogger() *MetricsLogger {
    logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))
    
    return &MetricsLogger{
        logger:  logger,
        metrics: make(map[string]int64),
    }
}

func (ml *MetricsLogger) LogWithMetrics(level slog.Level, msg string, attrs ...interface{}) {
    ml.logger.Log(context.Background(), level, msg, attrs...)
    
    // Обновление метрик
    ml.mu.Lock()
    ml.metrics[msg]++
    ml.mu.Unlock()
}

func (ml *MetricsLogger) GetMetrics() map[string]int64 {
    ml.mu.RLock()
    defer ml.mu.RUnlock()
    
    result := make(map[string]int64)
    for k, v := range ml.metrics {
        result[k] = v
    }
    return result
}
```

### Практические примеры: Логгер с маскировкой чувствительных данных

```go
type SafeLogger struct {
    logger *slog.Logger
    sensitiveFields []string
}

func NewSafeLogger(sensitiveFields []string) *SafeLogger {
    return &SafeLogger{
        logger:          slog.New(slog.NewJSONHandler(os.Stdout, nil)),
        sensitiveFields: sensitiveFields,
    }
}

func (sl *SafeLogger) maskSensitive(data map[string]interface{}) map[string]interface{}) {
    masked := make(map[string]interface{})
    for k, v := range data {
        for _, field := range sl.sensitiveFields {
            if k == field {
                masked[k] = "*MASKED*"
                continue
            }
        }
        masked[k] = v
    }
    return masked
}

func (sl *SafeLogger) LogSafe(level slog.Level, msg string, attrs ...interface{}) {
    // Преобразование attrs в map
    data := make(map[string]interface{})
    for i := 0; i < len(attrs); i += 2 {
        if i+1 < len(attrs) {
            key, ok := attrs[i].(string)
            if ok {
                data[key] = attrs[i+1]
            }
        }
    }
    
    // Маскировка чувствительных данных
    masked := sl.maskSensitive(data)
    
    // Преобразование обратно в attrs
    safeAttrs := make([]interface{}, 0, len(masked)*2)
    for k, v := range masked {
        safeAttrs = append(safeAttrs, k, v)
    }
    
    sl.logger.Log(context.Background(), level, msg, safeAttrs...)
}
```

### Практические примеры: Логгер с буферизацией

```go
type BufferedLogger struct {
    buffer  []string
    logger  *log.Logger
    mu      sync.Mutex
    maxSize int
}

func NewBufferedLogger(maxSize int) *BufferedLogger {
    return &BufferedLogger{
        buffer:  make([]string, 0, maxSize),
        logger:  log.New(os.Stdout, "", log.LstdFlags),
        maxSize: maxSize,
    }
}

func (bl *BufferedLogger) Log(msg string) {
    bl.mu.Lock()
    defer bl.mu.Unlock()
    
    bl.buffer = append(bl.buffer, msg)
    
    if len(bl.buffer) >= bl.maxSize {
        bl.Flush()
    }
}

func (bl *BufferedLogger) Flush() {
    bl.mu.Lock()
    defer bl.mu.Unlock()
    
    for _, msg := range bl.buffer {
        bl.logger.Println(msg)
    }
    bl.buffer = bl.buffer[:0]
}
```

### Практические примеры: Логирование с ротацией

```go
import (
    "gopkg.in/natefinch/lumberjack.v2"
)

func SetupRotatingLogger(logPath string) *log.Logger {
    writer := &lumberjack.Logger{
        Filename:   logPath,
        MaxSize:    100, // MB
        MaxBackups: 3,
        MaxAge:     28, // days
        Compress:   true,
    }
    
    return log.New(writer, "", log.LstdFlags)
}

func SetupSlogRotating(logPath string) *slog.Logger {
    writer := &lumberjack.Logger{
        Filename:   logPath,
        MaxSize:    100,
        MaxBackups: 3,
        MaxAge:     28,
        Compress:   true,
    }
    
    handler := slog.NewJSONHandler(writer, nil)
    return slog.New(handler)
}
```

### Практические примеры: Контекстное логирование

```go
type ContextLogger struct {
    logger *slog.Logger
    ctx    context.Context
}

func NewContextLogger(logger *slog.Logger, ctx context.Context) *ContextLogger {
    return &ContextLogger{
        logger: logger,
        ctx:    ctx,
    }
}

func (cl *ContextLogger) WithFields(fields map[string]interface{}) *ContextLogger {
    attrs := make([]slog.Attr, 0, len(fields))
    for k, v := range fields {
        attrs = append(attrs, slog.Any(k, v))
    }
    
    // Добавление контекстных значений
    if reqID := cl.ctx.Value("request_id"); reqID != nil {
        attrs = append(attrs, slog.String("request_id", reqID.(string)))
    }
    
    return &ContextLogger{
        logger: cl.logger.With(attrs...),
        ctx:    cl.ctx,
    }
}

func (cl *ContextLogger) Info(msg string, fields ...interface{}) {
    cl.logger.Info(msg, fields...)
}

func (cl *ContextLogger) Error(msg string, err error, fields ...interface{}) {
    allFields := append([]interface{}{"error", err}, fields...)
    cl.logger.Error(msg, allFields...)
}
```

### Практические примеры: Логирование с уровнями

```go
type LogLevel int

const (
    LevelDebug LogLevel = iota
    LevelInfo
    LevelWarn
    LevelError
)

type LeveledLogger struct {
    level  LogLevel
    logger *log.Logger
}

func NewLeveledLogger(level LogLevel) *LeveledLogger {
    return &LeveledLogger{
        level:  level,
        logger: log.New(os.Stdout, "", log.LstdFlags),
    }
}

func (ll *LeveledLogger) Debug(msg string, v ...interface{}) {
    if ll.level <= LevelDebug {
        ll.logger.Printf("[DEBUG] "+msg, v...)
    }
}

func (ll *LeveledLogger) Info(msg string, v ...interface{}) {
    if ll.level <= LevelInfo {
        ll.logger.Printf("[INFO] "+msg, v...)
    }
}

func (ll *LeveledLogger) Warn(msg string, v ...interface{}) {
    if ll.level <= LevelWarn {
        ll.logger.Printf("[WARN] "+msg, v...)
    }
}

func (ll *LeveledLogger) Error(msg string, v ...interface{}) {
    if ll.level <= LevelError {
        ll.logger.Printf("[ERROR] "+msg, v...)
    }
}
```

### Практические примеры: Логирование производительности

```go
func LogDuration(logger *slog.Logger, operation string, fn func()) {
    start := time.Now()
    defer func() {
        duration := time.Since(start)
        logger.Info("operation completed",
            "operation", operation,
            "duration", duration,
            "duration_ms", duration.Milliseconds(),
        )
    }()
    
    fn()
}

func LogSlowOperation(logger *slog.Logger, threshold time.Duration, operation string, fn func()) {
    start := time.Now()
    defer func() {
        duration := time.Since(start)
        if duration > threshold {
            logger.Warn("slow operation detected",
                "operation", operation,
                "duration", duration,
                "threshold", threshold,
            )
        }
    }()
    
    fn()
}
```

## Лучшие практики

1. **Используйте structured logging** - для лучшего анализа логов
2. **Добавляйте контекст** - включайте релевантную информацию в логи
3. **Используйте уровни правильно** - **DEBUG** для отладки, **ERROR** для ошибок
4. **Не логируйте чувствительные данные** - пароли, токены, персональные данные
5. **Используйте асинхронное логирование** - для высокой производительности
6. **Ротация логов** - управляйте размером лог-файлов
7. **Используйте контекст** - передавайте **context** для логирования
8. **Мониторьте производительность** - логирование не должно замедлять приложение
9. **Используйте метрики** - отслеживайте частоту логов
10. **Тестируйте логирование** - убедитесь, что логи записываются правильно
11. **Используйте ротацию** - управляйте размером лог-файлов
12. **Используйте контекстное логирование** - добавляйте контекст запросов
13. **Используйте уровни** - правильно устанавливайте уровни логирования
14. **Логируйте производительность** - отслеживайте время операций
15. **Используйте structured logging** - для интеграции с системами анализа


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Логирование в Go предоставляет мощные инструменты для отладки, мониторинга и анализа приложений. Понимание стандартного **log** пакета, **structured logging**, уровней логирования, кастомных логгеров, ротации, контекстного логирования, логирования производительности и лучших практик критично для эффективного логирования в Go. Правильное использование логирования позволяет создавать надежные, отлаживаемые, мониторируемые приложения с хорошей наблюдаемостью.

## Дополнительные ресурсы

- [Go log Package](https://pkg.go.dev/log)
- [Go log/slog](https://pkg.go.dev/log/slog)
