---
title: "Go: стандартная библиотека - I/O"
description: "Полное руководство по работе с I/O в Go: io.Reader, io.Writer, bufio, os, path/filepath"
tags:
  - go
  - golang
  - io
  - files
  - streaming
  - buffering
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-02-06"
---

# Go: стандартная библиотека — I/O

## Полезные ссылки

- [Go io Documentation](https://pkg.go.dev/io)
- [Go os Documentation](https://pkg.go.dev/os)
- [Go bufio Documentation](https://pkg.go.dev/bufio)

## Содержание

- [Go: стандартная библиотека — I/O](#go-стандартная-библиотека-io)
- [Введение в I/O](#введение-в-io)
  - [Основные интерфейсы](#основные-интерфейсы)
- [io.Reader и io.Writer](#ioreader-и-iowriter)
  - [io.Reader](#ioreader)
  - [io.Writer](#iowriter)
  - [Чтение до конца](#чтение-до-конца)
  - [Копирование данных](#копирование-данных)
- [Работа с файлами](#работа-с-файлами)
  - [Открытие файлов](#открытие-файлов)
  - [Чтение файлов](#чтение-файлов)
  - [Запись в файлы](#запись-в-файлы)
  - [Работа с директориями](#работа-с-директориями)
- [Buffered I/O](#buffered-io)
  - [Буферизованное чтение](#буферизованное-чтение)
  - [Буферизованная запись](#буферизованная-запись)
  - [Scanner для чтения строк](#scanner-для-чтения-строк)
- [Работа с путями](#работа-с-путями)
  - [Манипуляции с путями](#манипуляции-с-путями)
  - [MultiWriter](#multiwriter)
  - [MultiReader](#multireader)
  - [TeeReader](#teereader)
  - [LimitReader](#limitreader)
  - [SectionReader](#sectionreader)
  - [Pipe](#pipe)
  - [Практические примеры: Чтение конфигурационных файлов](#практические-примеры-чтение-конфигурационных-файлов)
  - [Практические примеры: Запись логов в файл](#практические-примеры-запись-логов-в-файл)
  - [Практические примеры: Ротация логов](#практические-примеры-ротация-логов)
  - [Практические примеры: Параллельное чтение файлов](#практические-примеры-параллельное-чтение-файлов)
  - [Практические примеры: Синхронная запись в несколько файлов](#практические-примеры-синхронная-запись-в-несколько-файлов)
  - [Практические примеры: Чтение больших файлов по частям](#практические-примеры-чтение-больших-файлов-по-частям)
  - [Практические примеры: Поиск в файле](#практические-примеры-поиск-в-файле)
  - [Практические примеры: Замена в файле](#практические-примеры-замена-в-файле)
  - [Практические примеры: Сравнение файлов](#практические-примеры-сравнение-файлов)
  - [Практические примеры: Копирование с прогрессом](#практические-примеры-копирование-с-прогрессом)
  - [Практические примеры: Работа с временными файлами](#практические-примеры-работа-с-временными-файлами)
  - [Практические примеры: Работа с временными директориями](#практические-примеры-работа-с-временными-директориями)
  - [Практические примеры: Мониторинг изменений файлов](#практические-примеры-мониторинг-изменений-файлов)
  - [Практические примеры: Асинхронная запись](#практические-примеры-асинхронная-запись)
  - [Практические примеры: Потоковое чтение больших файлов](#практические-примеры-потоковое-чтение-больших-файлов)
  - [Практические примеры: MultiWriter для записи в несколько мест](#практические-примеры-multiwriter-для-записи-в-несколько-мест)
  - [Практические примеры: Чтение с прогрессом](#практические-примеры-чтение-с-прогрессом)
  - [Практические примеры: Телескопирование reader/writer](#практические-примеры-телескопирование-readerwriter)
  - [Практические примеры: Ограничение скорости чтения/записи](#практические-примеры-ограничение-скорости-чтениязаписи)
  - [Практические примеры: Чтение с таймаутом](#практические-примеры-чтение-с-таймаутом)
  - [Практические примеры: Чтение/запись с контекстом](#практические-примеры-чтениезапись-с-контекстом)
  - [Практические примеры: Чтение с ограничением размера](#практические-примеры-чтение-с-ограничением-размера)
  - [Практические примеры: Запись с буферизацией и flush](#практические-примеры-запись-с-буферизацией-и-flush)
- [Лучшие практики](#лучшие-практики)
  - [Практические примеры: Асинхронное чтение и запись](#практические-примеры-асинхронное-чтение-и-запись)
  - [Практические примеры: Pipeline обработки данных](#практические-примеры-pipeline-обработки-данных)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в I/O

Пакеты `io`, `os` и `bufio` предоставляют мощные инструменты для работы с вводом-выводом в Go. Понимание работы с I/O критично для создания приложений, работающих с файлами и потоками данных.

### Основные интерфейсы

1. **io.Reader** — чтение данных из источника
2. **io.Writer** — запись данных в приемник
3. **io.Closer** — закрытие ресурсов
4. **io.ReadWriter** — комбинация **Reader** и **Writer**

## io.Reader и io.Writer

Интерфейсы `io.Reader` и `io.Writer` являются основой работы с I/O в Go.

### io.Reader

```go
import "io"

type Reader interface {
    Read(p []byte) (n int, err error)
}

// Пример использования
func readData(r io.Reader) ([]byte, error) {
    data := make([]byte, 1024)
    n, err := r.Read(data)
    if err != nil && err != io.EOF {
        return nil, err
    }
    return data[:n], nil
}
```

### io.Writer

```go
import "io"

type Writer interface {
    Write(p []byte) (n int, err error)
}

// Пример использования
func writeData(w io.Writer, data []byte) error {
    _, err := w.Write(data)
    return err
}
```

### Чтение до конца

```go
import "io"

func readAll(r io.Reader) ([]byte, error) {
    return io.ReadAll(r)
}

// Или вручную
func readAllManual(r io.Reader) ([]byte, error) {
    var result []byte
    buf := make([]byte, 4096)

    for {
        n, err := r.Read(buf)
        if n > 0 {
            result = append(result, buf[:n]...)
        }
        if err == io.EOF {
            break
        }
        if err != nil {
            return nil, err
        }
    }

    return result, nil
}
```

### Копирование данных

```go
import "io"

func copyData(dst io.Writer, src io.Reader) (int64, error) {
    return io.Copy(dst, src)
}

// Копирование с ограничением
func copyN(dst io.Writer, src io.Reader, n int64) (int64, error) {
    return io.CopyN(dst, src, n)
}
```

## Работа с файлами

Пакет `os` предоставляет функции для работы с файлами.

### Открытие файлов

```go
import "os"

// Открытие файла для чтения
file, err := os.Open("data.txt")
if err != nil {
    log.Fatal(err)
}
defer file.Close()

// Открытие файла для записи
file, err := os.Create("output.txt")
if err != nil {
    log.Fatal(err)
}
defer file.Close()

// Открытие с флагами
file, err := os.OpenFile("data.txt", os.O_RDWR|os.O_CREATE, 0644)
if err != nil {
    log.Fatal(err)
}
defer file.Close()
```

### Чтение файлов

```go
import (
    "io"
    "os"
)

// Чтение всего файла
data, err := os.ReadFile("data.txt")
if err != nil {
    log.Fatal(err)
}

// Чтение по частям
file, err := os.Open("data.txt")
if err != nil {
    log.Fatal(err)
}
defer file.Close()

buf := make([]byte, 1024)
for {
    n, err := file.Read(buf)
    if err == io.EOF {
        break
    }
    if err != nil {
        log.Fatal(err)
    }
    // Обработка данных
    processData(buf[:n])
}
```

### Запись в файлы

```go
import "os"

// Запись всего файла
data := []byte("Hello, World!")
err := os.WriteFile("output.txt", data, 0644)
if err != nil {
    log.Fatal(err)
}

// Запись по частям
file, err := os.Create("output.txt")
if err != nil {
    log.Fatal(err)
}
defer file.Close()

data := []byte("Hello, World!")
_, err = file.Write(data)
if err != nil {
    log.Fatal(err)
}
```

### Работа с директориями

```go
import (
    "os"
    "path/filepath"
)

// Создание директории
err := os.Mkdir("mydir", 0755)
if err != nil {
    log.Fatal(err)
}

// Создание директорий рекурсивно
err := os.MkdirAll("path/to/dir", 0755)
if err != nil {
    log.Fatal(err)
}

// Чтение директории
entries, err := os.ReadDir(".")
if err != nil {
    log.Fatal(err)
}

for _, entry := range entries {
    fmt.Println(entry.Name())
}

// Обход директории рекурсивно
filepath.Walk(".", func(path string, info os.FileInfo, err error) error {
    if err != nil {
        return err
    }
    fmt.Println(path)
    return nil
})
```

## Buffered I/O

Пакет `bufio` предоставляет буферизованный I/O для повышения производительности.

### Буферизованное чтение

```go
import (
    "bufio"
    "os"
)

file, err := os.Open("data.txt")
if err != nil {
    log.Fatal(err)
}
defer file.Close()

reader := bufio.NewReader(file)

// Чтение строки
line, err := reader.ReadString('\n')
if err != nil {
    log.Fatal(err)
}

// Чтение байтов
buf := make([]byte, 1024)
n, err := reader.Read(buf)
if err != nil {
    log.Fatal(err)
}

// Чтение до разделителя
data, err := reader.ReadBytes('\n')
if err != nil {
    log.Fatal(err)
}
```

### Буферизованная запись

```go
import (
    "bufio"
    "os"
)

file, err := os.Create("output.txt")
if err != nil {
    log.Fatal(err)
}
defer file.Close()

writer := bufio.NewWriter(file)
defer writer.Flush()  // Важно: сброс буфера перед закрытием

writer.WriteString("Hello, World!\n")
writer.Write([]byte("Data\n"))
```

### Scanner для чтения строк

```go
import (
    "bufio"
    "os"
)

file, err := os.Open("data.txt")
if err != nil {
    log.Fatal(err)
}
defer file.Close()

scanner := bufio.NewScanner(file)
for scanner.Scan() {
    line := scanner.Text()
    // Обработка строки
    processLine(line)
}

if err := scanner.Err(); err != nil {
    log.Fatal(err)
}
```

## Работа с путями

Пакет `path/filepath` предоставляет функции для работы с путями файлов.

### Манипуляции с путями

```go
import "path/filepath"

// Объединение путей
path := filepath.Join("dir", "subdir", "file.txt")

// Получение директории
dir := filepath.Dir(path)

// Получение имени файла
filename := filepath.Base(path)

// Получение расширения
ext := filepath.Ext(path)

// Получение пути без расширения
name := filepath.Base(path[:len(path)-len(ext)])

// Абсолютный путь
absPath, err := filepath.Abs("relative/path")
if err != nil {
    log.Fatal(err)
}

// Относительный путь
relPath, err := filepath.Rel("/base", "/base/dir/file.txt")
if err != nil {
    log.Fatal(err)
}
```

### Работа с путями

```go
import "path/filepath"

// Проверка существования
exists := filepath.Exists("file.txt")

// Разделение пути
dir, file := filepath.Split("dir/file.txt")

// Очистка пути
cleanPath := filepath.Clean("../dir/./file.txt")

// Сопоставление с шаблоном
matched, err := filepath.Match("*.go", "main.go")
if err != nil {
    log.Fatal(err)
}
```

### MultiWriter

```go
import "io"

func writeToMultiple(writers ...io.Writer) io.Writer {
    return io.MultiWriter(writers...)
}

func main() {
    file1, _ := os.Create("file1.txt")
    file2, _ := os.Create("file2.txt")
    defer file1.Close()
    defer file2.Close()

    writer := io.MultiWriter(file1, file2, os.Stdout)
    writer.Write([]byte("Hello, World!"))
    // Запись в оба файла и stdout
}
```

### MultiReader

```go
import "io"

func readFromMultiple(readers ...io.Reader) io.Reader {
    return io.MultiReader(readers...)
}

func main() {
    reader1 := strings.NewReader("Hello, ")
    reader2 := strings.NewReader("World!")

    reader := io.MultiReader(reader1, reader2)
    data, _ := io.ReadAll(reader)
    fmt.Println(string(data))  // "Hello, World!"
}
```

### TeeReader

```go
import "io"

func teeReaderExample() {
    reader := strings.NewReader("Hello, World!")
    var buf bytes.Buffer

    tee := io.TeeReader(reader, &buf)

    data, _ := io.ReadAll(tee)
    fmt.Println(string(data))  // "Hello, World!"
    fmt.Println(buf.String())  // "Hello, World!" (копия)
}
```

### LimitReader

```go
import "io"

func limitReaderExample() {
    reader := strings.NewReader("Hello, World!")
    limited := io.LimitReader(reader, 5)  // Ограничение до 5 байт

    data, _ := io.ReadAll(limited)
    fmt.Println(string(data))  // "Hello"
}
```

### SectionReader

```go
import "io"

func sectionReaderExample() {
    file, _ := os.Open("data.txt")
    defer file.Close()

    // Чтение секции файла (с байта 10, длиной 20)
    section := io.NewSectionReader(file, 10, 20)
    data, _ := io.ReadAll(section)
    fmt.Println(string(data))
}
```

### Pipe

```go
import "io"

func pipeExample() {
    reader, writer := io.Pipe()

    // Запись в горутине
    go func() {
        defer writer.Close()
        writer.Write([]byte("Hello, World!"))
    }()

    // Чтение
    data, _ := io.ReadAll(reader)
    fmt.Println(string(data))  // "Hello, World!"
}
```

### Практические примеры: Чтение конфигурационных файлов

```go
func readConfigFile(filename string) (map[string]interface{}, error) {
    file, err := os.Open(filename)
    if err != nil {
        return nil, err
    }
    defer file.Close()

    var config map[string]interface{}
    decoder := json.NewDecoder(file)
    if err := decoder.Decode(&config); err != nil {
        return nil, err
    }

    return config, nil
}
```

### Практические примеры: Запись логов в файл

```go
type FileLogger struct {
    file   *os.File
    writer *bufio.Writer
    mu     sync.Mutex
}

func NewFileLogger(filename string) (*FileLogger, error) {
    file, err := os.OpenFile(filename, os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0644)
    if err != nil {
        return nil, err
    }

    return &FileLogger{
        file:   file,
        writer: bufio.NewWriter(file),
    }, nil
}

func (l *FileLogger) Log(message string) {
    l.mu.Lock()
    defer l.mu.Unlock()

    l.writer.WriteString(time.Now().Format("2006-01-02 15:04:05") + " " + message + "\n")
    l.writer.Flush()
}

func (l *FileLogger) Close() error {
    l.writer.Flush()
    return l.file.Close()
}
```

### Практические примеры: Ротация логов

```go
type RotatingLogger struct {
    filename   string
    maxSize    int64
    currentFile *os.File
    writer     *bufio.Writer
    mu         sync.Mutex
}

func (l *RotatingLogger) rotate() error {
    if l.currentFile != nil {
        l.writer.Flush()
        l.currentFile.Close()
    }

    timestamp := time.Now().Format("20060102-150405")
    newFilename := fmt.Sprintf("%s.%s", l.filename, timestamp)
    os.Rename(l.filename, newFilename)

    file, err := os.Create(l.filename)
    if err != nil {
        return err
    }

    l.currentFile = file
    l.writer = bufio.NewWriter(file)
    return nil
}

func (l *RotatingLogger) Log(message string) {
    l.mu.Lock()
    defer l.mu.Unlock()

    // Проверка размера файла
    if info, err := l.currentFile.Stat(); err == nil {
        if info.Size() > l.maxSize {
            l.rotate()
        }
    }

    l.writer.WriteString(message + "\n")
    l.writer.Flush()
}
```

### Практические примеры: Параллельное чтение файлов

```go
func readFilesParallel(filenames []string) (map[string][]byte, error) {
    results := make(map[string][]byte)
    var mu sync.Mutex
    var wg sync.WaitGroup
    errCh := make(chan error, len(filenames))

    for _, filename := range filenames {
        wg.Add(1)
        go func(fn string) {
            defer wg.Done()

            data, err := os.ReadFile(fn)
            if err != nil {
                errCh <- err
                return
            }

            mu.Lock()
            results[fn] = data
            mu.Unlock()
        }(filename)
    }

    wg.Wait()
    close(errCh)

    for err := range errCh {
        if err != nil {
            return nil, err
        }
    }

    return results, nil
}
```

### Практические примеры: Синхронная запись в несколько файлов

```go
func writeToMultipleFiles(data []byte, filenames []string) error {
    files := make([]*os.File, len(filenames))
    writers := make([]io.Writer, len(filenames))

    // Открытие всех файлов
    for i, filename := range filenames {
        file, err := os.Create(filename)
        if err != nil {
            // Закрытие уже открытых файлов
            for j := 0; j < i; j++ {
                files[j].Close()
            }
            return err
        }
        files[i] = file
        writers[i] = file
    }

    // Запись во все файлы одновременно
    writer := io.MultiWriter(writers...)
    _, err := writer.Write(data)

    // Закрытие всех файлов
    for _, file := range files {
        file.Close()
    }

    return err
}
```

### Практические примеры: Чтение больших файлов по частям

```go
func readLargeFileInChunks(filename string, chunkSize int, processor func([]byte) error) error {
    file, err := os.Open(filename)
    if err != nil {
        return err
    }
    defer file.Close()

    reader := bufio.NewReader(file)
    buffer := make([]byte, chunkSize)

    for {
        n, err := reader.Read(buffer)
        if n > 0 {
            if err := processor(buffer[:n]); err != nil {
                return err
            }
        }

        if err == io.EOF {
            break
        }
        if err != nil {
            return err
        }
    }

    return nil
}
```

### Практические примеры: Поиск в файле

```go
func searchInFile(filename string, searchTerm string) ([]int, error) {
    file, err := os.Open(filename)
    if err != nil {
        return nil, err
    }
    defer file.Close()

    scanner := bufio.NewScanner(file)
    var matches []int
    lineNum := 1

    for scanner.Scan() {
        line := scanner.Text()
        if strings.Contains(line, searchTerm) {
            matches = append(matches, lineNum)
        }
        lineNum++
    }

    return matches, scanner.Err()
}
```

### Практические примеры: Замена в файле

```go
func replaceInFile(filename string, old, new string) error {
    // Чтение файла
    data, err := os.ReadFile(filename)
    if err != nil {
        return err
    }

    // Замена
    content := strings.ReplaceAll(string(data), old, new)

    // Запись обратно
    return os.WriteFile(filename, []byte(content), 0644)
}
```

### Практические примеры: Сравнение файлов

```go
func compareFiles(file1, file2 string) (bool, error) {
    data1, err := os.ReadFile(file1)
    if err != nil {
        return false, err
    }

    data2, err := os.ReadFile(file2)
    if err != nil {
        return false, err
    }

    return bytes.Equal(data1, data2), nil
}
```

### Практические примеры: Копирование с прогрессом

```go
type ProgressWriter struct {
    writer   io.Writer
    total    int64
    written  int64
    callback func(int64, int64)
}

func (pw *ProgressWriter) Write(p []byte) (int, error) {
    n, err := pw.writer.Write(p)
    pw.written += int64(n)

    if pw.callback != nil {
        pw.callback(pw.written, pw.total)
    }

    return n, err
}

func copyWithProgress(src, dst string, callback func(int64, int64)) error {
    source, err := os.Open(src)
    if err != nil {
        return err
    }
    defer source.Close()

    info, err := source.Stat()
    if err != nil {
        return err
    }

    destination, err := os.Create(dst)
    if err != nil {
        return err
    }
    defer destination.Close()

    pw := &ProgressWriter{
        writer:   destination,
        total:    info.Size(),
        callback: callback,
    }

    _, err = io.Copy(pw, source)
    return err
}
```

### Практические примеры: Работа с временными файлами

```go
func workWithTempFile() error {
    // Создание временного файла
    tmpfile, err := os.CreateTemp("", "example-*.txt")
    if err != nil {
        return err
    }
    defer os.Remove(tmpfile.Name())  // Удаление при выходе
    defer tmpfile.Close()

    // Запись данных
    tmpfile.WriteString("Temporary data")
    tmpfile.Sync()

    // Чтение данных
    data, err := os.ReadFile(tmpfile.Name())
    if err != nil {
        return err
    }

    fmt.Println(string(data))
    return nil
}
```

### Практические примеры: Работа с временными директориями

```go
func workWithTempDir() error {
    // Создание временной директории
    tmpdir, err := os.MkdirTemp("", "example-*")
    if err != nil {
        return err
    }
    defer os.RemoveAll(tmpdir)  // Удаление при выходе

    // Создание файлов в временной директории
    file1 := filepath.Join(tmpdir, "file1.txt")
    file2 := filepath.Join(tmpdir, "file2.txt")

    os.WriteFile(file1, []byte("Data 1"), 0644)
    os.WriteFile(file2, []byte("Data 2"), 0644)

    // Работа с файлами
    // ...

    return nil
}
```

### Практические примеры: Мониторинг изменений файлов

```go
import "github.com/fsnotify/fsnotify"

func watchFile(filename string, callback func()) error {
    watcher, err := fsnotify.NewWatcher()
    if err != nil {
        return err
    }
    defer watcher.Close()

    err = watcher.Add(filename)
    if err != nil {
        return err
    }

    for {
        select {
        case event := <-watcher.Events:
            if event.Op&fsnotify.Write == fsnotify.Write {
                callback()
            }
        case err := <-watcher.Errors:
            return err
        }
    }
}
```

### Практические примеры: Асинхронная запись

```go
type AsyncWriter struct {
    writer io.Writer
    queue  chan []byte
    done   chan struct{}
}

func NewAsyncWriter(writer io.Writer) *AsyncWriter {
    aw := &AsyncWriter{
        writer: writer,
        queue:  make(chan []byte, 100),
        done:   make(chan struct{}),
    }

    go aw.process()
    return aw
}

func (aw *AsyncWriter) Write(data []byte) (int, error) {
    dataCopy := make([]byte, len(data))
    copy(dataCopy, data)

    select {
    case aw.queue <- dataCopy:
        return len(data), nil
    default:
        return 0, fmt.Errorf("queue full")
    }
}

func (aw *AsyncWriter) process() {
    for {
        select {
        case data := <-aw.queue:
            aw.writer.Write(data)
        case <-aw.done:
            return
        }
    }
}

func (aw *AsyncWriter) Close() error {
    close(aw.done)
    return nil
}
```

### Практические примеры: Потоковое чтение больших файлов

```go
func StreamLargeFile(filename string, processor func([]byte) error) error {
    file, err := os.Open(filename)
    if err != nil {
        return err
    }
    defer file.Close()

    buffer := make([]byte, 64*1024) // 64KB буфер

    for {
        n, err := file.Read(buffer)
        if n > 0 {
            if err := processor(buffer[:n]); err != nil {
                return err
            }
        }
        if err == io.EOF {
            break
        }
        if err != nil {
            return err
        }
    }

    return nil
}

func StreamLineByLine(filename string, processor func(string) error) error {
    file, err := os.Open(filename)
    if err != nil {
        return err
    }
    defer file.Close()

    scanner := bufio.NewScanner(file)
    scanner.Buffer(make([]byte, 1024*1024), 10*1024*1024) // Увеличенный буфер

    for scanner.Scan() {
        if err := processor(scanner.Text()); err != nil {
            return err
        }
    }

    return scanner.Err()
}
```

### Практические примеры: Параллельное чтение файлов

```go
func ReadFilesParallel(filenames []string) (map[string][]byte, error) {
    type result struct {
        filename string
        data     []byte
        err      error
    }

    resultCh := make(chan result, len(filenames))
    var wg sync.WaitGroup

    for _, filename := range filenames {
        wg.Add(1)
        go func(name string) {
            defer wg.Done()
            data, err := os.ReadFile(name)
            resultCh <- result{
                filename: name,
                data:     data,
                err:      err,
            }
        }(filename)
    }

    go func() {
        wg.Wait()
        close(resultCh)
    }()

    results := make(map[string][]byte)
    for res := range resultCh {
        if res.err != nil {
            return nil, fmt.Errorf("error reading %s: %w", res.filename, res.err)
        }
        results[res.filename] = res.data
    }

    return results, nil
}
```

### Практические примеры: MultiWriter для записи в несколько мест

```go
func WriteToMultiple(writers ...io.Writer) io.Writer {
    return io.MultiWriter(writers...)
}

func LogToFileAndStdout(filename string) (io.Writer, error) {
    file, err := os.OpenFile(filename, os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0666)
    if err != nil {
        return nil, err
    }

    return io.MultiWriter(os.Stdout, file), nil
}

// Использование
writer, _ := LogToFileAndStdout("app.log")
fmt.Fprintf(writer, "Log message\n")
```

### Практические примеры: Чтение с прогрессом

```go
type ProgressReader struct {
    reader   io.Reader
    total    int64
    read     int64
    callback func(current, total int64)
    mu       sync.Mutex
}

func NewProgressReader(reader io.Reader, total int64, callback func(int64, int64)) *ProgressReader {
    return &ProgressReader{
        reader:   reader,
        total:    total,
        callback: callback,
    }
}

func (pr *ProgressReader) Read(p []byte) (int, error) {
    n, err := pr.reader.Read(p)

    pr.mu.Lock()
    pr.read += int64(n)
    current := pr.read
    pr.mu.Unlock()

    if pr.callback != nil {
        pr.callback(current, pr.total)
    }

    return n, err
}

func ReadFileWithProgress(filename string) error {
    file, err := os.Open(filename)
    if err != nil {
        return err
    }
    defer file.Close()

    info, err := file.Stat()
    if err != nil {
        return err
    }

    progressReader := NewProgressReader(file, info.Size(), func(current, total int64) {
        percent := float64(current) / float64(total) * 100
        fmt.Printf("\rProgress: %.2f%%", percent)
    })

    _, err = io.Copy(io.Discard, progressReader)
    fmt.Println()
    return err
}
```

### Практические примеры: Телескопирование reader/writer

```go
type TeeReader struct {
    reader io.Reader
    writer io.Writer
}

func NewTeeReader(reader io.Reader, writer io.Writer) *TeeReader {
    return &TeeReader{
        reader: reader,
        writer: writer,
    }
}

func (tr *TeeReader) Read(p []byte) (int, error) {
    n, err := tr.reader.Read(p)
    if n > 0 {
        tr.writer.Write(p[:n])
    }
    return n, err
}

// Использование для логирования чтения
func ReadAndLog(reader io.Reader) io.Reader {
    logWriter := os.Stdout
    return NewTeeReader(reader, logWriter)
}
```

### Практические примеры: Ограничение скорости чтения/записи

```go
import "golang.org/x/time/rate"

type RateLimitedReader struct {
    reader io.Reader
    limiter *rate.Limiter
}

func NewRateLimitedReader(reader io.Reader, bytesPerSecond int) *RateLimitedReader {
    return &RateLimitedReader{
        reader:  reader,
        limiter: rate.NewLimiter(rate.Limit(bytesPerSecond), bytesPerSecond),
    }
}

func (rlr *RateLimitedReader) Read(p []byte) (int, error) {
    n, err := rlr.reader.Read(p)
    if n > 0 {
        rlr.limiter.WaitN(context.Background(), n)
    }
    return n, err
}

type RateLimitedWriter struct {
    writer  io.Writer
    limiter *rate.Limiter
}

func NewRateLimitedWriter(writer io.Writer, bytesPerSecond int) *RateLimitedWriter {
    return &RateLimitedWriter{
        writer:  writer,
        limiter: rate.NewLimiter(rate.Limit(bytesPerSecond), bytesPerSecond),
    }
}

func (rlw *RateLimitedWriter) Write(p []byte) (int, error) {
    if err := rlw.limiter.WaitN(context.Background(), len(p)); err != nil {
        return 0, err
    }
    return rlw.writer.Write(p)
}
```

### Практические примеры: Чтение с таймаутом

```go
func ReadWithTimeout(reader io.Reader, timeout time.Duration) ([]byte, error) {
    resultCh := make(chan []byte, 1)
    errCh := make(chan error, 1)

    go func() {
        data, err := io.ReadAll(reader)
        if err != nil {
            errCh <- err
            return
        }
        resultCh <- data
    }()

    select {
    case data := <-resultCh:
        return data, nil
    case err := <-errCh:
        return nil, err
    case <-time.After(timeout):
        return nil, fmt.Errorf("read timeout after %v", timeout)
    }
}
```

### Практические примеры: Чтение/запись с контекстом

```go
type ContextReader struct {
    reader io.Reader
    ctx    context.Context
}

func NewContextReader(ctx context.Context, reader io.Reader) *ContextReader {
    return &ContextReader{
        reader: reader,
        ctx:    ctx,
    }
}

func (cr *ContextReader) Read(p []byte) (int, error) {
    select {
    case <-cr.ctx.Done():
        return 0, cr.ctx.Err()
    default:
    }

    type result struct {
        n   int
        err error
    }

    resultCh := make(chan result, 1)
    go func() {
        n, err := cr.reader.Read(p)
        resultCh <- result{n: n, err: err}
    }()

    select {
    case <-cr.ctx.Done():
        return 0, cr.ctx.Err()
    case res := <-resultCh:
        return res.n, res.err
    }
}
```

### Практические примеры: Чтение с ограничением размера

```go
type LimitedReader struct {
    reader io.Reader
    limit  int64
    read   int64
}

func NewLimitedReader(reader io.Reader, limit int64) *LimitedReader {
    return &LimitedReader{
        reader: reader,
        limit:  limit,
    }
}

func (lr *LimitedReader) Read(p []byte) (int, error) {
    if lr.read >= lr.limit {
        return 0, io.EOF
    }

    remaining := lr.limit - lr.read
    if int64(len(p)) > remaining {
        p = p[:remaining]
    }

    n, err := lr.reader.Read(p)
    lr.read += int64(n)

    if lr.read >= lr.limit {
        return n, io.EOF
    }

    return n, err
}

func ReadFileWithLimit(filename string, maxSize int64) ([]byte, error) {
    file, err := os.Open(filename)
    if err != nil {
        return nil, err
    }
    defer file.Close()

    limitedReader := NewLimitedReader(file, maxSize)
    return io.ReadAll(limitedReader)
}
```

### Практические примеры: Запись с буферизацией и flush

```go
type BufferedWriter struct {
    writer  io.Writer
    buffer  []byte
    bufSize int
    mu      sync.Mutex
}

func NewBufferedWriter(writer io.Writer, bufSize int) *BufferedWriter {
    return &BufferedWriter{
        writer:  writer,
        buffer:  make([]byte, 0, bufSize),
        bufSize: bufSize,
    }
}

func (bw *BufferedWriter) Write(p []byte) (int, error) {
    bw.mu.Lock()
    defer bw.mu.Unlock()

    written := 0
    for len(p) > 0 {
        space := bw.bufSize - len(bw.buffer)
        if space == 0 {
            if err := bw.flush(); err != nil {
                return written, err
            }
            space = bw.bufSize
        }

        n := space
        if n > len(p) {
            n = len(p)
        }

        bw.buffer = append(bw.buffer, p[:n]...)
        p = p[n:]
        written += n
    }

    return written, nil
}

func (bw *BufferedWriter) Flush() error {
    bw.mu.Lock()
    defer bw.mu.Unlock()
    return bw.flush()
}

func (bw *BufferedWriter) flush() error {
    if len(bw.buffer) == 0 {
        return nil
    }

    _, err := bw.writer.Write(bw.buffer)
    bw.buffer = bw.buffer[:0]
    return err
}

func (bw *BufferedWriter) Close() error {
    return bw.Flush()
}
```

## Лучшие практики

1. **Всегда закрывайте файлы** — используйте **defer** для закрытия файлов
2. **Обрабатывайте ошибки** — всегда проверяйте ошибки при работе с I/O
3. **Используйте буферизацию** — используйте **bufio** для повышения производительности
4. **Используйте io.Copy** — для копирования данных между потоками
5. **Используйте filepath** — для кроссплатформенной работы с путями
6. **Обрабатывайте EOF** — правильно обрабатывайте конец файла
7. **Используйте временные файлы** — для промежуточных данных
8. **Используйте streaming** — для больших файлов
9. **Используйте параллельное чтение** — для множественных файлов
10. **Мониторьте производительность** — отслеживайте операции I/O
11. **Используйте контекст** — для отмены долгих операций
12. **Используйте rate limiting** — для контроля скорости I/O
13. **Используйте прогресс** — информируйте пользователя о ходе выполнения
14. **Ограничивайте размер** — защищайтесь от больших файлов
15. **Используйте таймауты** — для предотвращения зависаний

### Практические примеры: Асинхронное чтение и запись

```go
type AsyncIO struct {
    reader io.Reader
    writer io.Writer
    buffer chan []byte
    errCh  chan error
}

func NewAsyncIO(reader io.Reader, writer io.Writer, bufferSize int) *AsyncIO {
    return &AsyncIO{
        reader: reader,
        writer: writer,
        buffer: make(chan []byte, bufferSize),
        errCh:  make(chan error, 1),
    }
}

func (aio *AsyncIO) Start(ctx context.Context) error {
    go func() {
        defer close(aio.buffer)
        buffer := make([]byte, 4096)
        for {
            n, err := aio.reader.Read(buffer)
            if n > 0 {
                select {
                case aio.buffer <- buffer[:n]:
                case <-ctx.Done():
                    return
                }
            }
            if err != nil {
                if err != io.EOF {
                    aio.errCh <- err
                }
                return
            }
        }
    }()

    go func() {
        for data := range aio.buffer {
            if _, err := aio.writer.Write(data); err != nil {
                aio.errCh <- err
                return
            }
        }
        close(aio.errCh)
    }()

    return <-aio.errCh
}
```

### Практические примеры: Pipeline обработки данных

```go
func ProcessPipeline(input io.Reader, processors []func([]byte) []byte, output io.Writer) error {
    scanner := bufio.NewScanner(input)

    for scanner.Scan() {
        data := scanner.Bytes()

        for _, processor := range processors {
            data = processor(data)
        }

        if _, err := output.Write(data); err != nil {
            return err
        }
        if _, err := output.Write([]byte("\n")); err != nil {
            return err
        }
    }

    return scanner.Err()
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Работа с I/O в Go предоставляет мощные инструменты для работы с файлами и потоками данных. Понимание **io.Reader**, **io.Writer**, работы с файлами, буферизации, путей, асинхронной обработки, **pipeline** и практических паттернов критично для создания эффективных приложений, работающих с данными. Правильное использование этих инструментов позволяет создавать масштабируемые, производительные приложения, которые эффективно обрабатывают данные и обеспечивают хорошую производительность даже при работе с большими объемами данных.

## Дополнительные ресурсы

- [Go io Documentation](https://pkg.go.dev/io)
- [Go os Documentation](https://pkg.go.dev/os)
- [Go bufio Documentation](https://pkg.go.dev/bufio)

## См. также

- [[go-advanced-patterns|Go: продвинутые паттерны]]
- [[go-basics|Go: основы]]
- [[go-benchmarking|Go: бенчмаркинг]]
- [[go-best-practices|Go: лучшие практики]]
- [[go-build|Go: сборка и развертывание]]
