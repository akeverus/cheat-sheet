---
title: "Go: отладка"
description: "Полное руководство по отладке в Go: GDB, Delve, логирование, трассировка, профилирование"
tags:
  - go
  - golang
  - debugging
  - delve
  - gdb
  - tracing
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-04-20"
---

# Go: отладка

## Полезные ссылки

- [Delve Documentation](https://github.com/go-delve/delve)
- [Go Debugging](https://go.dev/doc/diagnostics)

## Содержание

- [Введение в отладку](#введение-в-отладку)
  - [Основные инструменты](#основные-инструменты)
- [Delve](#delve)
  - [Установка](#установка)
  - [Базовое использование](#базовое-использование)
  - [Команды Delve](#команды-delve)
- [GDB](#gdb)
  - [Использование GDB](#использование-gdb)
- [Логирование для отладки](#логирование-для-отладки)
  - [Добавление логов](#добавление-логов)
  - [Условное логирование](#условное-логирование)
- [Трассировка](#трассировка)
  - [Использование runtime/trace](#использование-runtimetrace)
  - [Анализ трассировки](#анализ-трассировки)
  - [Детальные команды Delve](#детальные-команды-delve)
  - [Установка breakpoints](#установка-breakpoints)
  - [Просмотр и изменение переменных](#просмотр-и-изменение-переменных)
  - [Условная отладка](#условная-отладка)
  - [Remote debugging с Delve](#remote-debugging-с-delve)
  - [Практические примеры: Отладка горутин](#практические-примеры-отладка-горутин)
  - [Практические примеры: Отладка race conditions](#практические-примеры-отладка-race-conditions)
  - [Практические примеры: Отладка паники](#практические-примеры-отладка-паники)
  - [Практические примеры: Логирование для отладки](#практические-примеры-логирование-для-отладки)
  - [Практические примеры: Структурированное логирование для отладки](#практические-примеры-структурированное-логирование-для-отладки)
  - [Практические примеры: Трассировка выполнения](#практические-примеры-трассировка-выполнения)
  - [Практические примеры: Профилирование для отладки](#практические-примеры-профилирование-для-отладки)
  - [Практические примеры: Отладка через HTTP endpoint](#практические-примеры-отладка-через-http-endpoint)
  - [Практические примеры: Отладка с использованием GDB](#практические-примеры-отладка-с-использованием-gdb)
  - [Практические примеры: Отладка с использованием core dumps](#практические-примеры-отладка-с-использованием-core-dumps)
  - [Практические примеры: Отладка memory leaks](#практические-примеры-отладка-memory-leaks)
  - [Практические примеры: Отладка с использованием тестов](#практические-примеры-отладка-с-использованием-тестов)
  - [Практические примеры: Отладка с использованием assertions](#практические-примеры-отладка-с-использованием-assertions)
  - [Практические примеры: Отладка network connections](#практические-примеры-отладка-network-connections)
  - [Практические примеры: Отладка горутин](#практические-примеры-отладка-горутин-1)
  - [Практические примеры: Отладка утечек памяти](#практические-примеры-отладка-утечек-памяти)
  - [Практические примеры: Отладка с условными breakpoints](#практические-примеры-отладка-с-условными-breakpoints)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в отладку

Отладка — это процесс поиска и исправления ошибок в программе. Go предоставляет несколько инструментов для отладки.

### Основные инструменты

1. **Delve** — современный отладчик для Go
2. **GDB** — классический отладчик
3. **Логирование** — добавление логов для отладки
4. **Трассировка** — отслеживание выполнения программы

## Delve

**Delve** — это отладчик, специально разработанный для Go.

### Установка

```bash
go install github.com/go-delve/delve/cmd/dlv@latest
```

### Базовое использование

```bash
# Запуск программы под отладчиком
dlv debug main.go

# Установка breakpoint
(dlv) break main.go:10

# Запуск программы
(dlv) continue

# Пошаговое выполнение
(dlv) next
(dlv) step

# Просмотр переменных
(dlv) print variable

# Просмотр стека
(dlv) stack
```

### Команды Delve

```bash
# Список breakpoints
(dlv) breakpoints

# Удаление breakpoint
(dlv) clear 1

# Продолжение выполнения
(dlv) continue

# Выход
(dlv) exit
```

## GDB

**GDB** — классический отладчик, который также работает с Go.

### Использование GDB

```bash
# Компиляция с отладочной информацией
go build -gcflags="-N -l" main.go

# Запуск под GDB
gdb ./main

# Установка breakpoint
(gdb) break main.main

# Запуск программы
(gdb) run

# Пошаговое выполнение
(gdb) next
(gdb) step

# Просмотр переменных
(gdb) print variable
```

## Логирование для отладки

### Добавление логов

```go
import "log"

func processData(data []byte) error {
    log.Printf("Processing %d bytes", len(data))

    // Обработка данных
    result := process(data)

    log.Printf("Result: %v", result)
    return nil
}
```

### Условное логирование

```go
var debug = false

func debugLog(format string, args ...interface{}) {
    if debug {
        log.Printf("[DEBUG] "+format, args...)
    }
}

func main() {
    debug = true
    debugLog("Starting processing")
    // ...
}
```

## Трассировка

### Использование runtime/trace

```go
import (
    "os"
    "runtime/trace"
)

func main() {
    f, err := os.Create("trace.out")
    if err != nil {
        log.Fatal(err)
    }
    defer f.Close()

    trace.Start(f)
    defer trace.Stop()

    // Ваш код здесь
}
```

### Анализ трассировки

```bash
go tool trace trace.out
```

### Детальные команды Delve

```bash
# Запуск программы
(dlv) continue
(dlv) c

# Пошаговое выполнение (step into)
(dlv) step
(dlv) s

# Следующая строка (step over)
(dlv) next
(dlv) n

# Выход из функции (step out)
(dlv) stepout
(dlv) so

# Просмотр переменных
(dlv) print variable
(dlv) p variable

# Просмотр переменной в формате
(dlv) print -v variable
(dlv) p -v variable

# Просмотр типа переменной
(dlv) whatis variable

# Просмотр стека вызовов
(dlv) stack
(dlv) bt

# Просмотр локальных переменных
(dlv) locals

# Просмотр аргументов функции
(dlv) args

# Просмотр исходного кода
(dlv) list
(dlv) l

# Просмотр определенной строки
(dlv) list 10

# Просмотр функции
(dlv) list main.main
```

### Установка breakpoints

```bash
# Breakpoint на строке
(dlv) break main.go:10
(dlv) b main.go:10

# Breakpoint на функции
(dlv) break main.processData
(dlv) b main.processData

# Breakpoint с условием
(dlv) break main.go:10 if i > 10
(dlv) b main.go:10 if i > 10

# Breakpoint на регулярном выражении
(dlv) break -r "process.*"

# Список breakpoints
(dlv) breakpoints
(dlv) bp

# Удаление breakpoint
(dlv) clear 1
(dlv) clear main.go:10

# Удаление всех breakpoints
(dlv) clearall

# Включение/выключение breakpoint
(dlv) toggle 1
```

### Просмотр и изменение переменных

```bash
# Просмотр переменной
(dlv) print x
(dlv) p x

# Просмотр структуры
(dlv) print -v user
(dlv) p -v user

# Просмотр массива/слайса
(dlv) print slice[0:5]

# Изменение переменной
(dlv) set x = 10
(dlv) set user.Name = "Alice"

# Просмотр выражения
(dlv) print x + y
(dlv) p len(slice)
```

### Условная отладка

```bash
# Breakpoint с условием
(dlv) break main.go:20 if count > 100

# Условное выполнение
(dlv) condition 1 count > 100

# Команда при остановке
(dlv) trace main.go:20 print count
```

### Remote debugging с Delve

```bash
# Запуск Delve в headless режиме
dlv debug --headless --listen=:2345 --api-version=2

# Подключение к удаленному отладчику
dlv connect localhost:2345
```

### Практические примеры: Отладка горутин

```go
package main

import (
    "fmt"
    "sync"
    "time"
)

func worker(id int, wg *sync.WaitGroup, ch chan int) {
    defer wg.Done()
    for val := range ch {
        fmt.Printf("Worker %d processing %d\n", id, val)
        time.Sleep(100 * time.Millisecond)
    }
}

func main() {
    var wg sync.WaitGroup
    ch := make(chan int, 10)

    for i := 0; i < 3; i++ {
        wg.Add(1)
        go worker(i, &wg, ch)
    }

    for i := 0; i < 20; i++ {
        ch <- i
    }
    close(ch)

    wg.Wait()
}
```

```bash
# Отладка с просмотром горутин
(dlv) goroutines
(dlv) goroutine
(dlv) goroutine 1
(dlv) goroutine 1 stack
```

### Практические примеры: Отладка race conditions

```bash
# Компиляция с race detector
go build -race -o myapp

# Запуск с race detector
./myapp

# Race detector покажет место race condition
```

### Практические примеры: Отладка паники

```go
package main

import (
    "fmt"
    "runtime/debug"
)

func recoverPanic() {
    if r := recover(); r != nil {
        fmt.Printf("Panic recovered: %v\n", r)
        fmt.Printf("Stack trace:\n%s\n", debug.Stack())
    }
}

func main() {
    defer recoverPanic()

    panic("something went wrong")
}
```

### Практические примеры: Логирование для отладки

```go
package main

import (
    "log"
    "os"
)

var debugLogger *log.Logger

func init() {
    if os.Getenv("DEBUG") == "true" {
        debugLogger = log.New(os.Stdout, "[DEBUG] ", log.LstdFlags|log.Lshortfile)
    }
}

func debugLog(format string, args ...interface{}) {
    if debugLogger != nil {
        debugLogger.Printf(format, args...)
    }
}

func processData(data []byte) error {
    debugLog("Processing %d bytes", len(data))

    // Обработка данных
    result := process(data)

    debugLog("Result: %v", result)
    return nil
}
```

### Практические примеры: Структурированное логирование для отладки

```go
package main

import (
    "encoding/json"
    "log"
    "os"
)

type DebugLogger struct {
    enabled bool
    logger  *log.Logger
}

func NewDebugLogger(enabled bool) *DebugLogger {
    return &DebugLogger{
        enabled: enabled,
        logger:  log.New(os.Stdout, "[DEBUG] ", log.LstdFlags),
    }
}

func (l *DebugLogger) Log(event string, data map[string]interface{}) {
    if !l.enabled {
        return
    }

    logData := map[string]interface{}{
        "event": event,
        "data":  data,
    }

    jsonData, _ := json.Marshal(logData)
    l.logger.Println(string(jsonData))
}

func main() {
    logger := NewDebugLogger(os.Getenv("DEBUG") == "true")

    logger.Log("processing_started", map[string]interface{}{
        "user_id": 123,
        "action":  "process_data",
    })

    // Обработка

    logger.Log("processing_completed", map[string]interface{}{
        "user_id": 123,
        "result":  "success",
    })
}
```

### Практические примеры: Трассировка выполнения

```go
package main

import (
    "context"
    "fmt"
    "runtime/trace"
    "time"
)

func processWithTrace(ctx context.Context) {
    defer trace.StartRegion(ctx, "process").End()

    // Обработка данных
    time.Sleep(100 * time.Millisecond)
}

func main() {
    f, _ := os.Create("trace.out")
    defer f.Close()

    trace.Start(f)
    defer trace.Stop()

    ctx := context.Background()

    for i := 0; i < 10; i++ {
        processWithTrace(ctx)
    }
}
```

### Практические примеры: Профилирование для отладки

```go
package main

import (
    "os"
    "runtime/pprof"
)

func debugWithProfiling() {
    // CPU профилирование
    cpuFile, _ := os.Create("cpu.prof")
    defer cpuFile.Close()
    pprof.StartCPUProfile(cpuFile)
    defer pprof.StopCPUProfile()

    // Memory профилирование
    memFile, _ := os.Create("mem.prof")
    defer memFile.Close()

    // Ваш код здесь

    pprof.WriteHeapProfile(memFile)
}
```

### Практические примеры: Отладка через HTTP endpoint

```go
package main

import (
    _ "net/http/pprof"
    "net/http"
)

func main() {
    // Запуск HTTP сервера для профилирования
    go func() {
        log.Println(http.ListenAndServe("localhost:6060", nil))
    }()

    // Ваш код здесь

    // Доступ к профилям:
    // http://localhost:6060/debug/pprof/
    // http://localhost:6060/debug/pprof/heap
    // http://localhost:6060/debug/pprof/profile?seconds=30
}
```

### Практические примеры: Отладка с использованием GDB

```bash
# Компиляция с отладочной информацией
go build -gcflags="-N -l" -o myapp

# Запуск под GDB
gdb ./myapp

# Команды GDB
(gdb) break main.main
(gdb) run
(gdb) next
(gdb) step
(gdb) print variable
(gdb) info locals
(gdb) info args
(gdb) backtrace
(gdb) frame 1
(gdb) continue
(gdb) quit
```

### Практические примеры: Отладка с использованием core dumps

```bash
# Включение core dumps
ulimit -c unlimited

# Запуск программы
./myapp

# Анализ core dump
gdb ./myapp core

# Просмотр стека
(gdb) backtrace

# Просмотр переменных
(gdb) frame 0
(gdb) info locals
```

### Практические примеры: Отладка memory leaks

```go
package main

import (
    "runtime"
    "time"
)

func checkMemory() {
    var m runtime.MemStats
    runtime.ReadMemStats(&m)

    fmt.Printf("Alloc: %d KB\n", m.Alloc/1024)
    fmt.Printf("TotalAlloc: %d KB\n", m.TotalAlloc/1024)
    fmt.Printf("Sys: %d KB\n", m.Sys/1024)
    fmt.Printf("NumGC: %d\n", m.NumGC)
}

func main() {
    go func() {
        for {
            checkMemory()
            time.Sleep(5 * time.Second)
        }
    }()

    // Ваш код здесь
}
```

### Практические примеры: Отладка с использованием тестов

```go
package main

import (
    "testing"
    "runtime/debug"
)

func TestDebugFunction(t *testing.T) {
    // Включение детального вывода
    t.Log("Starting test")

    // Выполнение функции
    result, err := processData(testData)

    if err != nil {
        t.Logf("Error: %v\nStack: %s", err, debug.Stack())
        t.Fail()
    }

    t.Logf("Result: %v", result)
}
```

### Практические примеры: Отладка с использованием assertions

```go
package main

import (
    "fmt"
    "runtime"
)

func assert(condition bool, message string) {
    if !condition {
        _, file, line, _ := runtime.Caller(1)
        panic(fmt.Sprintf("%s:%d: Assertion failed: %s", file, line, message))
    }
}

func main() {
    x := 10
    assert(x > 0, "x must be positive")
    assert(x < 5, "x must be less than 5") // Паника здесь
}
```

### Практические примеры: Отладка network connections

```go
package main

import (
    "net"
    "net/http/httptrace"
)

func debugHTTPRequest() {
    trace := &httptrace.ClientTrace{
        DNSStart: func(info httptrace.DNSStartInfo) {
            fmt.Printf("DNS Start: %v\n", info)
        },
        DNSDone: func(info httptrace.DNSDoneInfo) {
            fmt.Printf("DNS Done: %v\n", info)
        },
        ConnectStart: func(network, addr string) {
            fmt.Printf("Connect Start: %s %s\n", network, addr)
        },
        ConnectDone: func(network, addr string, err error) {
            fmt.Printf("Connect Done: %s %s, err: %v\n", network, addr, err)
        },
    }

    ctx := httptrace.WithClientTrace(context.Background(), trace)
    req, _ := http.NewRequestWithContext(ctx, "GET", "https://example.com", nil)

    client := &http.Client{}
    client.Do(req)
}
```

### Практические примеры: Отладка горутин

```go
import "runtime/debug"

func DebugGoroutines() {
    // Печать стека всех горутин
    debug.PrintStack()

    // Получение информации о горутинах
    buf := make([]byte, 1<<20)
    stackSize := runtime.Stack(buf, true)
    fmt.Printf("Stack trace:\n%s\n", buf[:stackSize])
}

func LogGoroutineID() {
    buf := make([]byte, 64)
    n := runtime.Stack(buf, false)
    idField := strings.Fields(strings.TrimPrefix(string(buf[:n]), "goroutine "))[0]
    id, _ := strconv.Atoi(idField)
    log.Printf("Goroutine ID: %d", id)
}
```

### Практические примеры: Отладка утечек памяти

```go
import "runtime"

func CheckMemory() {
    var m runtime.MemStats
    runtime.ReadMemStats(&m)

    fmt.Printf("Alloc = %v KiB", bToKiB(m.Alloc))
    fmt.Printf("\tTotalAlloc = %v KiB", bToKiB(m.TotalAlloc))
    fmt.Printf("\tSys = %v KiB", bToKiB(m.Sys))
    fmt.Printf("\tNumGC = %v\n", m.NumGC)
    fmt.Printf("HeapAlloc = %v KiB", bToKiB(m.HeapAlloc))
    fmt.Printf("\tHeapSys = %v KiB", bToKiB(m.HeapSys))
    fmt.Printf("\tHeapInuse = %v KiB", bToKiB(m.HeapInuse))
}

func bToKiB(b uint64) uint64 {
    return b / 1024
}

func ForceGC() {
    runtime.GC()
    runtime.GC() // Дважды для полной очистки
}
```

### Практические примеры: Отладка с условными breakpoints

```go
// Условное логирование для отладки
var debugMode = os.Getenv("DEBUG") == "true"

func debugLog(format string, args ...interface{}) {
    if debugMode {
        log.Printf("[DEBUG] "+format, args...)
    }
}

// Отладочные структуры
type DebugContext struct {
    enabled bool
    trace   []string
}

func (dc *DebugContext) Trace(msg string) {
    if dc.enabled {
        dc.trace = append(dc.trace, msg)
    }
}

func (dc *DebugContext) Dump() {
    if dc.enabled {
        for _, msg := range dc.trace {
            fmt.Println(msg)
        }
    }
}
```

## Лучшие практики

1. **Используйте Delve** — для отладки Go программ
2. **Добавляйте логи** — для понимания выполнения программы
3. **Используйте breakpoints** — для остановки в нужных местах
4. **Проверяйте переменные** — используйте **print** для просмотра значений
5. **Используйте трассировку** — для анализа производительности
6. **Используйте race detector** — для поиска **race conditions**
7. **Используйте профилирование** — для анализа производительности
8. **Используйте структурированное логирование** — для лучшей отладки
9. **Используйте тесты для отладки** — для изоляции проблем
10. **Документируйте известные проблемы** — для будущей отладки
11. **Отлаживайте горутины** — используйте **stack traces** для горутин
12. **Отлаживайте память** — отслеживайте использование памяти
13. **Используйте условную отладку** — включайте/выключайте отладочный код
14. **Используйте трассировку запросов** — для отладки распределенных систем
15. **Документируйте процесс отладки** — описывайте найденные проблемы


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Отладка в Go предоставляет мощные инструменты для поиска и исправления ошибок. Понимание **Delve**, **GDB**, логирования, трассировки, отладки горутин, памяти, условной отладки и практических техник критично для эффективной отладки Go программ. Правильное использование инструментов отладки позволяет быстро находить, изолировать и исправлять проблемы в приложениях, обеспечивая стабильность и надежность кода.

## Дополнительные ресурсы

- [Delve Documentation](https://github.com/go-delve/delve)
- [Go Debugging](https://go.dev/doc/diagnostics)

## См. также

- [Go: продвинутые паттерны](go-advanced-patterns.md)
- [Go: основы](go-basics.md)
- [Go: бенчмаркинг](go-benchmarking.md)
- [Go: лучшие практики](go-best-practices.md)
- [Go: сборка и развертывание](go-build.md)
