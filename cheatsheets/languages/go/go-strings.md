---
title: "Go: строки"
description: "Полное руководство по работе со строками в Go: strings package, манипуляции, поиск, замена, форматирование"
tags:
  - go
  - golang
  - strings
  - text-processing
  - formatting
type: "overview"
difficulty: "intermediate"
aliases:
  - "Go"
  - "строки"
  - "Go: строки"
  - "go strings"
prerequisites:
  - "[[go-basics]]"
next: []
updated: "2026-04-20"
---

# Go: строки

## Полезные ссылки

- [Go strings Package](https://pkg.go.dev/strings)
- [Go fmt Package](https://pkg.go.dev/fmt)

## Содержание

- [Введение в строки](#введение-в-строки)
  - [Основные концепции](#основные-концепции)
- [Базовые операции](#базовые-операции)
  - [Длина строки](#длина-строки)
  - [Сравнение строк](#сравнение-строк)
  - [Преобразование регистра](#преобразование-регистра)
- [Поиск и замена](#поиск-и-замена)
  - [Поиск подстроки](#поиск-подстроки)
  - [Замена](#замена)
  - [Префиксы и суффиксы](#префиксы-и-суффиксы)
- [Разделение и объединение](#разделение-и-объединение)
  - [Разделение строки](#разделение-строки)
  - [Объединение строк](#объединение-строк)
- [Форматирование](#форматирование)
  - [Trim операции](#trim-операции)
  - [Padding](#padding)
- [Конвертация](#конвертация)
  - [Конвертация в числа](#конвертация-в-числа)
  - [Конвертация из чисел](#конвертация-из-чисел)
  - [Практические примеры: Работа с рунами](#практические-примеры-работа-с-рунами)
  - [Практические примеры: Эффективная конкатенация](#практические-примеры-эффективная-конкатенация)
  - [Практические примеры: Парсинг и валидация](#практические-примеры-парсинг-и-валидация)
  - [Практические примеры: Текстовые преобразования](#практические-примеры-текстовые-преобразования)
  - [Практические примеры: Поиск и замена с контекстом](#практические-примеры-поиск-и-замена-с-контекстом)
  - [Практические примеры: Форматирование чисел в строках](#практические-примеры-форматирование-чисел-в-строках)
  - [Практические примеры: Работа с многострочным текстом](#практические-примеры-работа-с-многострочным-текстом)
  - [Практические примеры: Обработка текста](#практические-примеры-обработка-текста)
  - [Практические примеры: Нормализация строк](#практические-примеры-нормализация-строк)
  - [Практические примеры: Шаблонизация строк](#практические-примеры-шаблонизация-строк)
  - [Практические примеры: Эффективная работа со строками](#практические-примеры-эффективная-работа-со-строками)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в строки

Строки в Go — это неизменяемые последовательности байт. Пакет `strings` предоставляет множество функций для работы со строками.

### Основные концепции

1. **Неизменяемость** — строки нельзя изменить после создания
2. **UTF-8** — строки кодируются в **UTF-8**
3. **Индексация** — доступ к байтам, а не к символам
4. **Rune** — для работы с **Unicode** символами

## Базовые операции

### Длина строки

```go
import "strings"

func main() {
    s := "Hello, World!"

    // Длина в байтах
    fmt.Println(len(s))  // 13

    // Длина в рунах (символах)
    fmt.Println(len([]rune(s)))  // 13
}
```

### Сравнение строк

```go
import "strings"

func main() {
    s1 := "hello"
    s2 := "HELLO"

    // Сравнение
    fmt.Println(s1 == s2)  // false

    // Сравнение без учета регистра
    fmt.Println(strings.EqualFold(s1, s2))  // true

    // Сравнение (лексикографическое)
    fmt.Println(strings.Compare(s1, s2))  // -1 (s1 < s2)
}
```

### Преобразование регистра

```go
import "strings"

func main() {
    s := "Hello, World!"

    // Верхний регистр
    upper := strings.ToUpper(s)
    fmt.Println(upper)  // "HELLO, WORLD!"

    // Нижний регистр
    lower := strings.ToLower(s)
    fmt.Println(lower)  // "hello, world!"

    // Заглавная буква
    title := strings.Title(s)
    fmt.Println(title)  // "Hello, World!"
}
```

## Поиск и замена

### Поиск подстроки

```go
import "strings"

func main() {
    s := "Hello, World!"

    // Содержит ли подстроку
    fmt.Println(strings.Contains(s, "World"))  // true

    // Индекс подстроки
    fmt.Println(strings.Index(s, "World"))  // 7

    // Последний индекс
    fmt.Println(strings.LastIndex(s, "o"))  // 8

    // Количество вхождений
    fmt.Println(strings.Count(s, "l"))  // 3
}
```

### Замена

```go
import "strings"

func main() {
    s := "Hello, World!"

    // Замена всех вхождений
    replaced := strings.ReplaceAll(s, "World", "Go")
    fmt.Println(replaced)  // "Hello, Go!"

    // Замена с ограничением
    replaced = strings.Replace(s, "l", "L", 2)
    fmt.Println(replaced)  // "HeLLo, World!"
}
```

### Префиксы и суффиксы

```go
import "strings"

func main() {
    s := "Hello, World!"

    // Проверка префикса
    fmt.Println(strings.HasPrefix(s, "Hello"))  // true

    // Проверка суффикса
    fmt.Println(strings.HasSuffix(s, "!"))  // true
}
```

## Разделение и объединение

### Разделение строки

```go
import "strings"

func main() {
    s := "a,b,c,d"

    // Разделение по разделителю
    parts := strings.Split(s, ",")
    fmt.Println(parts)  // ["a", "b", "c", "d"]

    // Разделение с ограничением
    parts = strings.SplitN(s, ",", 2)
    fmt.Println(parts)  // ["a", "b,c,d"]

    // Разделение по пробелам
    parts = strings.Fields("a b c d")
    fmt.Println(parts)  // ["a", "b", "c", "d"]
}
```

### Объединение строк

```go
import "strings"

func main() {
    parts := []string{"a", "b", "c", "d"}

    // Объединение
    joined := strings.Join(parts, ",")
    fmt.Println(joined)  // "a,b,c,d"

    // Использование strings.Builder для эффективности
    var builder strings.Builder
    for i, part := range parts {
        if i > 0 {
            builder.WriteString(",")
        }
        builder.WriteString(part)
    }
    result := builder.String()
    fmt.Println(result)  // "a,b,c,d"
}
```

## Форматирование

### Trim операции

```go
import "strings"

func main() {
    s := "  Hello, World!  "

    // Удаление пробелов с обеих сторон
    trimmed := strings.TrimSpace(s)
    fmt.Println(trimmed)  // "Hello, World!"

    // Удаление символов
    trimmed = strings.Trim(s, " !")
    fmt.Println(trimmed)  // "Hello, World"

    // Удаление префикса
    trimmed = strings.TrimPrefix(s, "  ")
    fmt.Println(trimmed)  // "Hello, World!  "

    // Удаление суффикса
    trimmed = strings.TrimSuffix(s, "!  ")
    fmt.Println(trimmed)  // "  Hello, World"
}
```

### Padding

```go
import "fmt"

func main() {
    s := "Hello"

    // Padding справа
    padded := fmt.Sprintf("%-10s", s)
    fmt.Printf("'%s'\n", padded)  // 'Hello     '

    // Padding слева
    padded = fmt.Sprintf("%10s", s)
    fmt.Printf("'%s'\n", padded)  // '     Hello'
}
```

## Конвертация

### Конвертация в числа

```go
import (
    "strconv"
    "fmt"
)

func main() {
    // String to int
    i, err := strconv.Atoi("123")
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println(i)  // 123

    // String to int64
    i64, err := strconv.ParseInt("123", 10, 64)
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println(i64)  // 123

    // String to float64
    f, err := strconv.ParseFloat("3.14", 64)
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println(f)  // 3.14
}
```

### Конвертация из чисел

```go
import (
    "strconv"
    "fmt"
)

func main() {
    // Int to string
    s := strconv.Itoa(123)
    fmt.Println(s)  // "123"

    // Int64 to string
    s = strconv.FormatInt(123, 10)
    fmt.Println(s)  // "123"

    // Float64 to string
    s = strconv.FormatFloat(3.14, 'f', 2, 64)
    fmt.Println(s)  // "3.14"
}
```

### Практические примеры: Работа с рунами

```go
import (
    "strings"
    "unicode"
)

func processRunes(s string) {
    // Итерация по рунам
    for i, r := range s {
        fmt.Printf("Rune %d: %c (U+%04X)\n", i, r, r)
    }

    // Фильтрация рун
    filtered := strings.Map(func(r rune) rune {
        if unicode.IsLetter(r) || unicode.IsDigit(r) {
            return r
        }
        return -1  // Удалить
    }, s)

    fmt.Println("Filtered:", filtered)
}

func countRunes(s string) int {
    return len([]rune(s))
}

func reverseString(s string) string {
    runes := []rune(s)
    for i, j := 0, len(runes)-1; i < j; i, j = i+1, j-1 {
        runes[i], runes[j] = runes[j], runes[i]
    }
    return string(runes)
}
```

### Практические примеры: Эффективная конкатенация

```go
func concatenateStrings(parts []string) string {
    var builder strings.Builder

    // Предварительное выделение памяти
    totalLen := 0
    for _, part := range parts {
        totalLen += len(part)
    }
    builder.Grow(totalLen)

    for _, part := range parts {
        builder.WriteString(part)
    }

    return builder.String()
}

func concatenateWithSeparator(parts []string, sep string) string {
    if len(parts) == 0 {
        return ""
    }

    var builder strings.Builder
    builder.Grow(len(parts) * (len(parts[0]) + len(sep)))

    builder.WriteString(parts[0])
    for _, part := range parts[1:] {
        builder.WriteString(sep)
        builder.WriteString(part)
    }

    return builder.String()
}
```

### Практические примеры: Парсинг и валидация

```go
func validateEmail(email string) bool {
    parts := strings.Split(email, "@")
    if len(parts) != 2 {
        return false
    }

    local, domain := parts[0], parts[1]
    if len(local) == 0 || len(domain) == 0 {
        return false
    }

    if !strings.Contains(domain, ".") {
        return false
    }

    return true
}

func parseKeyValue(s string) (key, value string, ok bool) {
    idx := strings.Index(s, "=")
    if idx == -1 {
        return "", "", false
    }

    key = strings.TrimSpace(s[:idx])
    value = strings.TrimSpace(s[idx+1:])
    return key, value, true
}

func parseCSVLine(line string) []string {
    var fields []string
    var current strings.Builder
    inQuotes := false

    for _, r := range line {
        switch r {
        case '"':
            inQuotes = !inQuotes
        case ',':
            if !inQuotes {
                fields = append(fields, strings.TrimSpace(current.String()))
                current.Reset()
            } else {
                current.WriteRune(r)
            }
        default:
            current.WriteRune(r)
        }
    }

    fields = append(fields, strings.TrimSpace(current.String()))
    return fields
}
```

### Практические примеры: Текстовые преобразования

```go
func toSnakeCase(s string) string {
    var result strings.Builder
    result.Grow(len(s) + len(s)/3) // Предварительное выделение

    for i, r := range s {
        if unicode.IsUpper(r) {
            if i > 0 {
                result.WriteByte('_')
            }
            result.WriteRune(unicode.ToLower(r))
        } else {
            result.WriteRune(r)
        }
    }

    return result.String()
}

func toCamelCase(s string) string {
    words := strings.FieldsFunc(s, func(r rune) bool {
        return !unicode.IsLetter(r) && !unicode.IsDigit(r)
    })

    if len(words) == 0 {
        return ""
    }

    var result strings.Builder
    result.WriteString(strings.ToLower(words[0]))

    for _, word := range words[1:] {
        if len(word) > 0 {
            runes := []rune(word)
            runes[0] = unicode.ToUpper(runes[0])
            result.WriteString(string(runes))
        }
    }

    return result.String()
}

func truncateString(s string, maxLen int) string {
    if len(s) <= maxLen {
        return s
    }

    if maxLen <= 3 {
        return s[:maxLen]
    }

    return s[:maxLen-3] + "..."
}
```

### Практические примеры: Поиск и замена с контекстом

```go
func replaceWithContext(s, old, new string, maxReplacements int) string {
    if maxReplacements <= 0 {
        return strings.ReplaceAll(s, old, new)
    }

    var result strings.Builder
    result.Grow(len(s) + (len(new)-len(old))*maxReplacements)

    count := 0
    start := 0

    for {
        idx := strings.Index(s[start:], old)
        if idx == -1 || count >= maxReplacements {
            result.WriteString(s[start:])
            break
        }

        result.WriteString(s[start : start+idx])
        result.WriteString(new)
        start += idx + len(old)
        count++
    }

    return result.String()
}

func findAllOccurrences(s, substr string) []int {
    var indices []int
    start := 0

    for {
        idx := strings.Index(s[start:], substr)
        if idx == -1 {
            break
        }
        indices = append(indices, start+idx)
        start += idx + 1
    }

    return indices
}
```

### Практические примеры: Форматирование чисел в строках

```go
import (
    "strconv"
    "strings"
)

func formatNumberWithCommas(n int64) string {
    s := strconv.FormatInt(n, 10)

    if len(s) <= 3 {
        return s
    }

    var result strings.Builder
    result.Grow(len(s) + len(s)/3)

    for i, r := range s {
        if i > 0 && (len(s)-i)%3 == 0 {
            result.WriteByte(',')
        }
        result.WriteRune(r)
    }

    return result.String()
}

func parseNumberWithCommas(s string) (int64, error) {
    cleaned := strings.ReplaceAll(s, ",", "")
    return strconv.ParseInt(cleaned, 10, 64)
}
```

### Практические примеры: Работа с многострочным текстом

```go
func indentLines(s string, indent string) string {
    lines := strings.Split(s, "\n")
    var result strings.Builder
    result.Grow(len(s) + len(indent)*len(lines))

    for i, line := range lines {
        if i > 0 {
            result.WriteByte('\n')
        }
        result.WriteString(indent)
        result.WriteString(line)
    }

    return result.String()
}

func wrapText(s string, width int) []string {
    words := strings.Fields(s)
    if len(words) == 0 {
        return []string{}
    }

    var lines []string
    var current strings.Builder
    current.Grow(width)

    for _, word := range words {
        if current.Len() > 0 && current.Len()+len(word)+1 > width {
            lines = append(lines, current.String())
            current.Reset()
        }

        if current.Len() > 0 {
            current.WriteByte(' ')
        }
        current.WriteString(word)
    }

    if current.Len() > 0 {
        lines = append(lines, current.String())
    }

    return lines
}
```

### Практические примеры: Обработка текста

```go
// Удаление дубликатов слов
func RemoveDuplicateWords(text string) string {
    words := strings.Fields(text)
    seen := make(map[string]bool)
    unique := make([]string, 0)

    for _, word := range words {
        if !seen[word] {
            seen[word] = true
            unique = append(unique, word)
        }
    }

    return strings.Join(unique, " ")
}

// Подсчет слов
func CountWords(text string) map[string]int {
    words := strings.Fields(strings.ToLower(text))
    counts := make(map[string]int)

    for _, word := range words {
        // Удаление пунктуации
        word = strings.Trim(word, ".,!?;:")
        counts[word]++
    }

    return counts
}

// Извлечение email адресов
func ExtractEmails(text string) []string {
    emailRegex := regexp.MustCompile(`[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}`)
    return emailRegex.FindAllString(text, -1)
}
```

### Практические примеры: Нормализация строк

```go
import "unicode/utf8"

// Нормализация пробелов
func NormalizeWhitespace(s string) string {
    s = strings.TrimSpace(s)
    // Замена множественных пробелов одним
    return regexp.MustCompile(`\s+`).ReplaceAllString(s, " ")
}

// Удаление непечатаемых символов
func RemoveNonPrintable(s string) string {
    var builder strings.Builder
    builder.Grow(len(s))

    for _, r := range s {
        if unicode.IsPrint(r) {
            builder.WriteRune(r)
        }
    }

    return builder.String()
}

// Транслитерация
func Transliterate(s string) string {
    translitMap := map[rune]string{
        'а': "a", 'б': "b", 'в': "v", 'г': "g",
        'д': "d", 'е': "e", 'ё': "yo", 'ж': "zh",
        // ... больше символов
    }

    var builder strings.Builder
    for _, r := range s {
        if replacement, ok := translitMap[r]; ok {
            builder.WriteString(replacement)
        } else {
            builder.WriteRune(r)
        }
    }

    return builder.String()
}
```

### Практические примеры: Шаблонизация строк

```go
import "text/template"

func ProcessTemplate(tmpl string, data interface{}) (string, error) {
    t, err := template.New("tmpl").Parse(tmpl)
    if err != nil {
        return "", err
    }

    var buf strings.Builder
    if err := t.Execute(&buf, data); err != nil {
        return "", err
    }

    return buf.String(), nil
}

// Использование
tmpl := "Hello, {{.Name}}! Your age is {{.Age}}."
data := map[string]interface{}{
    "Name": "Alice",
    "Age":  30,
}
result, _ := ProcessTemplate(tmpl, data)
```

### Практические примеры: Эффективная работа со строками

```go
// Предварительное выделение памяти для Builder
func BuildLargeString(items []string) string {
    var builder strings.Builder
    builder.Grow(len(items) * 10) // Примерная оценка размера

    for i, item := range items {
        if i > 0 {
            builder.WriteString(", ")
        }
        builder.WriteString(item)
    }

    return builder.String()
}

// Обработка больших строк по частям
func ProcessLargeString(s string, chunkSize int, processor func(string)) {
    for i := 0; i < len(s); i += chunkSize {
        end := i + chunkSize
        if end > len(s) {
            end = len(s)
        }
        processor(s[i:end])
    }
}

// Построчная обработка
func ProcessLines(s string, processor func(string)) {
    scanner := bufio.NewScanner(strings.NewReader(s))
    for scanner.Scan() {
        processor(scanner.Text())
    }
}
```

## Лучшие практики

1. **Используйте strings.Builder** — для эффективной конкатенации строк
2. **Избегайте множественной конкатенации** — используйте **Join** или **Builder**
3. **Работайте с рунами** — для **Unicode** символов
4. **Используйте strings.Compare** — для лексикографического сравнения
5. **Проверяйте ошибки** — при конвертации строк в числа
6. **Предварительно выделяйте память** — используйте **Grow** для **Builder**
7. **Используйте strings.Fields** — для разделения по пробелам
8. **Избегайте создания лишних строк** — используйте эффективные методы
9. **Работайте с байтами когда возможно** — для лучшей производительности
10. **Валидируйте входные данные** — проверяйте строки перед обработкой
11. **Используйте регулярные выражения** — для сложных паттернов
12. **Нормализуйте строки** — перед обработкой и сравнением
13. **Используйте шаблоны** — для динамической генерации строк
14. **Обрабатывайте большие строки по частям** — для экономии памяти
15. **Используйте правильную кодировку** — учитывайте **UTF-8** особенности


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Работа со строками в Go предоставляет мощные инструменты для обработки текста. Понимание базовых операций, поиска, замены, разделения, объединения, форматирования, конвертации, нормализации, шаблонизации, работы с **Unicode** и практических применений критично для эффективной работы со строками в Go. Правильное использование строковых операций позволяет создавать эффективные, надежные и производительные текстовые обработчики, которые корректно работают с различными форматами данных и кодировками.

## Дополнительные ресурсы

- [Go strings Package](https://pkg.go.dev/strings)
- [Go fmt Package](https://pkg.go.dev/fmt)

## См. также

- [Go: продвинутые паттерны](go-advanced-patterns.md)
- [Go: основы](go-basics.md)
- [Go: бенчмаркинг](go-benchmarking.md)
- [Go: лучшие практики](go-best-practices.md)
- [Go: сборка и развертывание](go-build.md)
