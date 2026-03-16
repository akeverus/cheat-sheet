---
title: "Go: операции с файлами"
description: "Полное руководство по работе с файлами в Go: чтение, запись, копирование, удаление, информация о файлах"
tags: ["go", "golang", "files", "io", "file-operations"]
difficulty: "intermediate"
prerequisites: ["go/go-basics.md", "go/go-stdlib-io.md"]
updated: "2026-02-06"
---

# Go: операции с файлами

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

- [Go os Package](https://pkg.go.dev/os)
- [Go path/filepath](https://pkg.go.dev/path/filepath)

## Содержание

- [Go: операции с файлами](#go-операции-с-файлами)
- [Введение в операции с файлами](#введение-в-операции-с-файлами)
  - [Основные операции](#основные-операции)
- [Чтение файлов](#чтение-файлов)
  - [Чтение всего файла](#чтение-всего-файла)
  - [Чтение по частям](#чтение-по-частям)
  - [Чтение строк](#чтение-строк)
- [Запись в файлы](#запись-в-файлы)
  - [Запись всего файла](#запись-всего-файла)
  - [Запись по частям](#запись-по-частям)
  - [Запись строк](#запись-строк)
- [Копирование файлов](#копирование-файлов)
  - [Копирование с **io.Copy**](#копирование-с-iocopy)
  - [Копирование с буферизацией](#копирование-с-буферизацией)
- [Удаление файлов](#удаление-файлов)
  - [Удаление файла](#удаление-файла)
  - [Удаление директории](#удаление-директории)
- [Информация о файлах](#информация-о-файлах)
  - [Получение информации](#получение-информации)
  - [Проверка существования](#проверка-существования)
- [Работа с директориями](#работа-с-директориями)
  - [Создание директории](#создание-директории)
  - [Чтение директории](#чтение-директории)
  - [Обход директории](#обход-директории)
  - [Практические примеры: Асинхронное чтение файла](#практические-примеры-асинхронное-чтение-файла)
  - [Практические примеры: Чтение файла с таймаутом](#практические-примеры-чтение-файла-с-таймаутом)
  - [Практические примеры: Построчное чтение с обработкой](#практические-примеры-построчное-чтение-с-обработкой)
  - [Практические примеры: Запись с буферизацией и **flush**](#практические-примеры-запись-с-буферизацией-и-flush)
  - [Практические примеры: Копирование с прогрессом](#практические-примеры-копирование-с-прогрессом)
  - [Практические примеры: Рекурсивное копирование директории](#практические-примеры-рекурсивное-копирование-директории)
  - [Практические примеры: Поиск файлов](#практические-примеры-поиск-файлов)
  - [Практические примеры: Мониторинг изменений файла](#практические-примеры-мониторинг-изменений-файла)
  - [Практические примеры: Временные файлы](#практические-примеры-временные-файлы)
  - [Практические примеры: Безопасная запись файла](#практические-примеры-безопасная-запись-файла)
  - [Практические примеры: Чтение конфигурационных файлов](#практические-примеры-чтение-конфигурационных-файлов)
  - [Практические примеры: Мониторинг изменений файлов](#практические-примеры-мониторинг-изменений-файлов)
  - [Практические примеры: Атомарная запись файлов](#практические-примеры-атомарная-запись-файлов)
  - [Практические примеры: Рекурсивное копирование директорий](#практические-примеры-рекурсивное-копирование-директорий)
- [Лучшие практики](#лучшие-практики)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в операции с файлами

Go предоставляет мощные инструменты для работы с файлами через пакеты `os`, `io` и `path/filepath`. Понимание работы с файлами критично для создания приложений, работающих с файловой системой.

### Основные операции

1. **Чтение** - чтение данных из файлов
2. **Запись** - запись данных в файлы
3. **Копирование** - копирование файлов
4. **Удаление** - удаление файлов и директорий
5. **Информация** - получение информации о файлах

## Чтение файлов

### Чтение всего файла

```go
import (
    "os"
    "fmt"
)

func readFile(filename string) ([]byte, error) {
    data, err := os.ReadFile(filename)
    if err != nil {
        return nil, err
    }
    return data, nil
}
```

### Чтение по частям

```go
import (
    "os"
    "io"
)

func readFileChunks(filename string, chunkSize int) error {
    file, err := os.Open(filename)
    if err != nil {
        return err
    }
    defer file.Close()
    
    buf := make([]byte, chunkSize)
    for {
        n, err := file.Read(buf)
        if err == io.EOF {
            break
        }
        if err != nil {
            return err
        }
        // Обработка данных
        processChunk(buf[:n])
    }
    return nil
}
```

### Чтение строк

```go
import (
    "bufio"
    "os"
)

func readFileLines(filename string) ([]string, error) {
    file, err := os.Open(filename)
    if err != nil {
        return nil, err
    }
    defer file.Close()
    
    var lines []string
    scanner := bufio.NewScanner(file)
    for scanner.Scan() {
        lines = append(lines, scanner.Text())
    }
    
    if err := scanner.Err(); err != nil {
        return nil, err
    }
    
    return lines, nil
}
```

## Запись в файлы

### Запись всего файла

```go
import "os"

func writeFile(filename string, data []byte) error {
    return os.WriteFile(filename, data, 0644)
}
```

### Запись по частям

```go
import (
    "os"
    "io"
)

func writeFileChunks(filename string, data []byte) error {
    file, err := os.Create(filename)
    if err != nil {
        return err
    }
    defer file.Close()
    
    _, err = file.Write(data)
    return err
}
```

### Запись строк

```go
import (
    "bufio"
    "os"
)

func writeFileLines(filename string, lines []string) error {
    file, err := os.Create(filename)
    if err != nil {
        return err
    }
    defer file.Close()
    
    writer := bufio.NewWriter(file)
    defer writer.Flush()
    
    for _, line := range lines {
        _, err := writer.WriteString(line + "\n")
        if err != nil {
            return err
        }
    }
    
    return nil
}
```

## Копирование файлов

### Копирование с **io.Copy**

```go
import (
    "io"
    "os"
)

func copyFile(src, dst string) error {
    source, err := os.Open(src)
    if err != nil {
        return err
    }
    defer source.Close()
    
    destination, err := os.Create(dst)
    if err != nil {
        return err
    }
    defer destination.Close()
    
    _, err = io.Copy(destination, source)
    return err
}
```

### Копирование с буферизацией

```go
import (
    "bufio"
    "io"
    "os"
)

func copyFileBuffered(src, dst string) error {
    source, err := os.Open(src)
    if err != nil {
        return err
    }
    defer source.Close()
    
    destination, err := os.Create(dst)
    if err != nil {
        return err
    }
    defer destination.Close()
    
    reader := bufio.NewReader(source)
    writer := bufio.NewWriter(destination)
    defer writer.Flush()
    
    _, err = io.Copy(writer, reader)
    return err
}
```

## Удаление файлов

### Удаление файла

```go
import "os"

func deleteFile(filename string) error {
    return os.Remove(filename)
}
```

### Удаление директории

```go
import "os"

func deleteDirectory(dirname string) error {
    return os.RemoveAll(dirname)
}
```

## Информация о файлах

### Получение информации

```go
import (
    "os"
    "fmt"
)

func fileInfo(filename string) error {
    info, err := os.Stat(filename)
    if err != nil {
        return err
    }
    
    fmt.Printf("Name: %s\n", info.Name())
    fmt.Printf("Size: %d bytes\n", info.Size())
    fmt.Printf("Mode: %s\n", info.Mode())
    fmt.Printf("ModTime: %s\n", info.ModTime())
    fmt.Printf("IsDir: %t\n", info.IsDir())
    
    return nil
}
```

### Проверка существования

```go
import (
    "os"
    "errors"
)

func fileExists(filename string) (bool, error) {
    _, err := os.Stat(filename)
    if err == nil {
        return true, nil
    }
    if errors.Is(err, os.ErrNotExist) {
        return false, nil
    }
    return false, err
}
```

## Работа с директориями

### Создание директории

```go
import "os"

func createDirectory(dirname string) error {
    return os.Mkdir(dirname, 0755)
}

func createDirectories(dirname string) error {
    return os.MkdirAll(dirname, 0755)
}
```

### Чтение директории

```go
import (
    "os"
    "fmt"
)

func listDirectory(dirname string) error {
    entries, err := os.ReadDir(dirname)
    if err != nil {
        return err
    }
    
    for _, entry := range entries {
        fmt.Println(entry.Name())
        if entry.IsDir() {
            fmt.Println("  (directory)")
        }
    }
    
    return nil
}
```

### Обход директории

```go
import (
    "path/filepath"
    "fmt"
)

func walkDirectory(root string) error {
    return filepath.Walk(root, func(path string, info os.FileInfo, err error) error {
        if err != nil {
            return err
        }
        fmt.Println(path)
        return nil
    })
}
```

### Практические примеры: Асинхронное чтение файла

```go
func readFileAsync(filename string) <-chan []byte {
    result := make(chan []byte, 1)
    
    go func() {
        defer close(result)
        data, err := os.ReadFile(filename)
        if err != nil {
            return
        }
        result <- data
    }()
    
    return result
}
```

### Практические примеры: Чтение файла с таймаутом

```go
func readFileWithTimeout(ctx context.Context, filename string) ([]byte, error) {
    type result struct {
        data []byte
        err  error
    }
    
    resultCh := make(chan result, 1)
    
    go func() {
        data, err := os.ReadFile(filename)
        resultCh <- result{data: data, err: err}
    }()
    
    select {
    case res := <-resultCh:
        return res.data, res.err
    case <-ctx.Done():
        return nil, ctx.Err()
    }
}
```

### Практические примеры: Построчное чтение с обработкой

```go
func processFileLines(filename string, processor func(string) error) error {
    file, err := os.Open(filename)
    if err != nil {
        return err
    }
    defer file.Close()
    
    scanner := bufio.NewScanner(file)
    lineNum := 0
    
    for scanner.Scan() {
        lineNum++
        line := scanner.Text()
        
        if err := processor(line); err != nil {
            return fmt.Errorf("error processing line %d: %w", lineNum, err)
        }
    }
    
    return scanner.Err()
}
```

### Практические примеры: Запись с буферизацией и **flush**

```go
type BufferedFileWriter struct {
    file   *os.File
    writer *bufio.Writer
    mu     sync.Mutex
}

func NewBufferedFileWriter(filename string, bufferSize int) (*BufferedFileWriter, error) {
    file, err := os.Create(filename)
    if err != nil {
        return nil, err
    }
    
    return &BufferedFileWriter{
        file:   file,
        writer: bufio.NewWriterSize(file, bufferSize),
    }, nil
}

func (bfw *BufferedFileWriter) Write(data []byte) error {
    bfw.mu.Lock()
    defer bfw.mu.Unlock()
    
    _, err := bfw.writer.Write(data)
    return err
}

func (bfw *BufferedFileWriter) Flush() error {
    bfw.mu.Lock()
    defer bfw.mu.Unlock()
    return bfw.writer.Flush()
}

func (bfw *BufferedFileWriter) Close() error {
    if err := bfw.Flush(); err != nil {
        return err
    }
    return bfw.file.Close()
}
```

### Практические примеры: Копирование с прогрессом

```go
type ProgressWriter struct {
    writer  io.Writer
    written int64
    total   int64
    mu      sync.Mutex
}

func NewProgressWriter(writer io.Writer, total int64) *ProgressWriter {
    return &ProgressWriter{
        writer: writer,
        total:  total,
    }
}

func (pw *ProgressWriter) Write(p []byte) (int, error) {
    n, err := pw.writer.Write(p)
    
    pw.mu.Lock()
    pw.written += int64(n)
    progress := float64(pw.written) / float64(pw.total) * 100
    pw.mu.Unlock()
    
    fmt.Printf("\rProgress: %.2f%%", progress)
    
    return n, err
}

func copyFileWithProgress(src, dst string) error {
    source, err := os.Open(src)
    if err != nil {
        return err
    }
    defer source.Close()
    
    sourceInfo, err := source.Stat()
    if err != nil {
        return err
    }
    
    destination, err := os.Create(dst)
    if err != nil {
        return err
    }
    defer destination.Close()
    
    progressWriter := NewProgressWriter(destination, sourceInfo.Size())
    _, err = io.Copy(progressWriter, source)
    fmt.Println()
    
    return err
}
```

### Практические примеры: Рекурсивное копирование директории

```go
func copyDirectory(src, dst string) error {
    return filepath.Walk(src, func(path string, info os.FileInfo, err error) error {
        if err != nil {
            return err
        }
        
        relPath, err := filepath.Rel(src, path)
        if err != nil {
            return err
        }
        
        dstPath := filepath.Join(dst, relPath)
        
        if info.IsDir() {
            return os.MkdirAll(dstPath, info.Mode())
        }
        
        return copyFile(path, dstPath)
    })
}
```

### Практические примеры: Поиск файлов

```go
func findFiles(root string, predicate func(os.FileInfo) bool) ([]string, error) {
    var files []string
    
    err := filepath.Walk(root, func(path string, info os.FileInfo, err error) error {
        if err != nil {
            return err
        }
        
        if !info.IsDir() && predicate(info) {
            files = append(files, path)
        }
        
        return nil
    })
    
    return files, err
}

func findFilesByExtension(root, ext string) ([]string, error) {
    return findFiles(root, func(info os.FileInfo) bool {
        return strings.HasSuffix(strings.ToLower(info.Name()), strings.ToLower(ext))
    })
}

func findLargeFiles(root string, minSize int64) ([]string, error) {
    return findFiles(root, func(info os.FileInfo) bool {
        return info.Size() >= minSize
    })
}
```

### Практические примеры: Мониторинг изменений файла

```go
type FileWatcher struct {
    filename string
    interval time.Duration
    lastMod  time.Time
    callback func()
    stop     chan struct{}
}

func NewFileWatcher(filename string, interval time.Duration, callback func()) *FileWatcher {
    return &FileWatcher{
        filename: filename,
        interval: interval,
        callback: callback,
        stop:     make(chan struct{}),
    }
}

func (fw *FileWatcher) Start() {
    info, err := os.Stat(fw.filename)
    if err == nil {
        fw.lastMod = info.ModTime()
    }
    
    ticker := time.NewTicker(fw.interval)
    defer ticker.Stop()
    
    for {
        select {
        case <-fw.stop:
            return
        case <-ticker.C:
            info, err := os.Stat(fw.filename)
            if err != nil {
                continue
            }
            
            if info.ModTime().After(fw.lastMod) {
                fw.lastMod = info.ModTime()
                fw.callback()
            }
        }
    }
}

func (fw *FileWatcher) Stop() {
    close(fw.stop)
}
```

### Практические примеры: Временные файлы

```go
func createTempFile(pattern string, data []byte) (string, error) {
    tmpfile, err := os.CreateTemp("", pattern)
    if err != nil {
        return "", err
    }
    
    if _, err := tmpfile.Write(data); err != nil {
        tmpfile.Close()
        os.Remove(tmpfile.Name())
        return "", err
    }
    
    if err := tmpfile.Close(); err != nil {
        os.Remove(tmpfile.Name())
        return "", err
    }
    
    return tmpfile.Name(), nil
}

func withTempFile(pattern string, data []byte, fn func(string) error) error {
    tmpfile, err := createTempFile(pattern, data)
    if err != nil {
        return err
    }
    defer os.Remove(tmpfile)
    
    return fn(tmpfile)
}
```

### Практические примеры: Безопасная запись файла

```go
func writeFileSafe(filename string, data []byte) error {
    // Запись во временный файл
    tmpfile := filename + ".tmp"
    
    if err := os.WriteFile(tmpfile, data, 0644); err != nil {
        return err
    }
    
    // Атомарная замена
    if err := os.Rename(tmpfile, filename); err != nil {
        os.Remove(tmpfile)
        return err
    }
    
    return nil
}
```

### Практические примеры: Чтение конфигурационных файлов

```go
func readConfigFile(filename string) (map[string]string, error) {
    config := make(map[string]string)
    
    file, err := os.Open(filename)
    if err != nil {
        return nil, err
    }
    defer file.Close()
    
    scanner := bufio.NewScanner(file)
    for scanner.Scan() {
        line := strings.TrimSpace(scanner.Text())
        
        // Пропуск комментариев и пустых строк
        if len(line) == 0 || strings.HasPrefix(line, "#") {
            continue
        }
        
        // Парсинг key=value
        parts := strings.SplitN(line, "=", 2)
        if len(parts) == 2 {
            key := strings.TrimSpace(parts[0])
            value := strings.TrimSpace(parts[1])
            config[key] = value
        }
    }
    
    return config, scanner.Err()
}
```

### Практические примеры: Мониторинг изменений файлов

```go
import "github.com/fsnotify/fsnotify"

func WatchFile(path string, callback func()) error {
    watcher, err := fsnotify.NewWatcher()
    if err != nil {
        return err
    }
    defer watcher.Close()
    
    if err := watcher.Add(path); err != nil {
        return err
    }
    
    go func() {
        for {
            select {
            case event, ok := <-watcher.Events:
                if !ok {
                    return
                }
                if event.Op&fsnotify.Write == fsnotify.Write {
                    callback()
                }
            case err, ok := <-watcher.Errors:
                if !ok {
                    return
                }
                log.Printf("Watcher error: %v", err)
            }
        }
    }()
    
    return nil
}
```

### Практические примеры: Атомарная запись файлов

```go
func AtomicWrite(filename string, data []byte) error {
    // Создание временного файла в той же директории
    dir := filepath.Dir(filename)
    tmpfile, err := os.CreateTemp(dir, filepath.Base(filename)+".tmp")
    if err != nil {
        return err
    }
    defer os.Remove(tmpfile.Name())
    
    // Запись данных
    if _, err := tmpfile.Write(data); err != nil {
        tmpfile.Close()
        return err
    }
    
    // Синхронизация на диск
    if err := tmpfile.Sync(); err != nil {
        tmpfile.Close()
        return err
    }
    
    if err := tmpfile.Close(); err != nil {
        return err
    }
    
    // Атомарная замена
    return os.Rename(tmpfile.Name(), filename)
}
```

### Практические примеры: Рекурсивное копирование директорий

```go
func CopyDir(src, dst string) error {
    info, err := os.Stat(src)
    if err != nil {
        return err
    }
    
    if err := os.MkdirAll(dst, info.Mode()); err != nil {
        return err
    }
    
    entries, err := os.ReadDir(src)
    if err != nil {
        return err
    }
    
    for _, entry := range entries {
        srcPath := filepath.Join(src, entry.Name())
        dstPath := filepath.Join(dst, entry.Name())
        
        if entry.IsDir() {
            if err := CopyDir(srcPath, dstPath); err != nil {
                return err
            }
        } else {
            if err := CopyFile(srcPath, dstPath); err != nil {
                return err
            }
        }
    }
    
    return nil
}

func CopyFile(src, dst string) error {
    source, err := os.Open(src)
    if err != nil {
        return err
    }
    defer source.Close()
    
    destination, err := os.Create(dst)
    if err != nil {
        return err
    }
    defer destination.Close()
    
    _, err = io.Copy(destination, source)
    return err
}
```

### Практические примеры: Поиск файлов

```go
func FindFiles(root string, pattern string) ([]string, error) {
    var matches []string
    err := filepath.Walk(root, func(path string, info os.FileInfo, err error) error {
        if err != nil {
            return err
        }
        
        matched, err := filepath.Match(pattern, info.Name())
        if err != nil {
            return err
        }
        
        if matched {
            matches = append(matches, path)
        }
        
        return nil
    })
    
    return matches, err
}

func FindFilesByExtension(root, ext string) ([]string, error) {
    var matches []string
    err := filepath.Walk(root, func(path string, info os.FileInfo, err error) error {
        if err != nil {
            return err
        }
        
        if !info.IsDir() && filepath.Ext(path) == ext {
            matches = append(matches, path)
        }
        
        return nil
    })
    
    return matches, err
}
```

## Лучшие практики

1. **Всегда закрывайте файлы** - используйте **defer** для закрытия
2. **Обрабатывайте ошибки** - всегда проверяйте ошибки при работе с файлами
3. **Используйте буферизацию** - для больших файлов
4. **Проверяйте существование** - перед операциями с файлами
5. **Используйте правильные права доступа** - устанавливайте корректные **permissions**
6. **Используйте временные файлы** - для безопасной записи
7. **Мониторьте изменения** - для отслеживания изменений файлов
8. **Используйте контекст** - для отмены операций с файлами
9. **Обрабатывайте большие файлы** - читайте по частям
10. **Используйте filepath** - для кроссплатформенных путей
11. **Используйте атомарные операции** - для безопасной записи
12. **Рекурсивно обрабатывайте директории** - для работы с деревом файлов
13. **Ищите файлы эффективно** - используйте **filepath.Walk**
14. **Используйте мониторинг** - отслеживайте изменения файлов
15. **Оптимизируйте операции** - используйте буферизацию и **streaming**


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Операции с файлами в Go предоставляют мощные инструменты для работы с файловой системой. Понимание чтения, записи, копирования, удаления файлов, работы с директориями, мониторинга, атомарных операций, рекурсивной обработки и практических применений критично для эффективной работы с файлами в Go. Правильное использование файловых операций позволяет создавать надежные, эффективные, безопасные приложения, которые корректно работают с файловой системой и обеспечивают целостность данных.

## Дополнительные ресурсы

- [Go os Package](https://pkg.go.dev/os)
- [Go path/filepath](https://pkg.go.dev/path/filepath)
