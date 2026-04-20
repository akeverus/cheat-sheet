---
title: "Go: регулярные выражения"
description: "Полное руководство по регулярным выражениям в Go: regexp package, паттерны, поиск, замена, группы"
tags:
  - go
  - golang
  - regexp
  - regex
  - pattern-matching
difficulty: "intermediate"
prerequisites: ["go/go-basics.md"]
updated: "2026-04-20"
---

# Go: регулярные выражения

## Полезные ссылки

- [Go regexp Package](https://pkg.go.dev/regexp)
- [RE2 Syntax](https://github.com/google/re2/wiki/Syntax)

## Содержание

- [Введение в регулярные выражения](#введение-в-регулярные-выражения)
  - [Основные концепции](#основные-концепции)
- [Компиляция паттернов](#компиляция-паттернов)
  - [Compile](#compile)
  - [MustCompile](#mustcompile)
  - [CompilePOSIX](#compileposix)
- [Поиск совпадений](#поиск-совпадений)
  - [MatchString](#matchstring)
  - [FindString](#findstring)
  - [FindAllString](#findallstring)
  - [FindStringIndex](#findstringindex)
- [Замена текста](#замена-текста)
  - [ReplaceAllString](#replaceallstring)
  - [ReplaceAllStringFunc](#replaceallstringfunc)
  - [ReplaceAllLiteralString](#replaceallliteralstring)
- [Группы захвата](#группы-захвата)
  - [FindStringSubmatch](#findstringsubmatch)
  - [FindAllStringSubmatch](#findallstringsubmatch)
  - [Именованные группы](#именованные-группы)
  - [Практические примеры: Валидация email](#практические-примеры-валидация-email)
  - [Практические примеры: Валидация URL](#практические-примеры-валидация-url)
  - [Практические примеры: Парсинг дат](#практические-примеры-парсинг-дат)
  - [Практические примеры: Извлечение чисел](#практические-примеры-извлечение-чисел)
  - [Практические примеры: Поиск и замена с контекстом](#практические-примеры-поиск-и-замена-с-контекстом)
  - [Практические примеры: Парсинг логов](#практические-примеры-парсинг-логов)
  - [Практические примеры: Валидация паролей](#практические-примеры-валидация-паролей)
  - [Практические примеры: Парсинг CSV с regex](#практические-примеры-парсинг-csv-с-regex)
  - [Практические примеры: Поиск IP адресов](#практические-примеры-поиск-ip-адресов)
  - [Практические примеры: Кэширование регулярных выражений](#практические-примеры-кэширование-регулярных-выражений)
  - [Практические примеры: Поиск с ограничением](#практические-примеры-поиск-с-ограничением)
  - [Практические примеры: Замена с обратными ссылками](#практические-примеры-замена-с-обратными-ссылками)
  - [Практические примеры: Многострочный поиск](#практические-примеры-многострочный-поиск)
  - [Практические примеры: Поиск с анкорами](#практические-примеры-поиск-с-анкорами)
  - [Практические примеры: Экранирование специальных символов](#практические-примеры-экранирование-специальных-символов)
  - [Практические примеры: Кэширование регулярных выражений](#практические-примеры-кэширование-регулярных-выражений-1)
  - [Практические примеры: Валидация данных](#практические-примеры-валидация-данных)
  - [Практические примеры: Парсинг структурированных данных](#практические-примеры-парсинг-структурированных-данных)
  - [Практические примеры: Поиск и замена с функциями](#практические-примеры-поиск-и-замена-с-функциями)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в регулярные выражения

Пакет `regexp` предоставляет функциональность для работы с регулярными выражениями. Go использует **RE2 engine**, который обеспечивает линейную производительность.

### Основные концепции

1. **Паттерны** — строки, описывающие шаблоны текста
2. **Компиляция** — преобразование паттерна в регулярное выражение
3. **Совпадения** — поиск текста, соответствующего паттерну
4. **Группы** — захват частей совпадения

## Компиляция паттернов

### Compile

```go
import "regexp"

func main() {
    // Компиляция паттерна
    re, err := regexp.Compile(`\d+`)
    if err != nil {
        log.Fatal(err)
    }

    // Использование
    matched := re.MatchString("123")
    fmt.Println(matched)  // true
}
```

### MustCompile

```go
import "regexp"

func main() {
    // Компиляция с паникой при ошибке
    re := regexp.MustCompile(`\d+`)

    matched := re.MatchString("123")
    fmt.Println(matched)  // true
}
```

### CompilePOSIX

```go
import "regexp"

func main() {
    // POSIX совместимая компиляция
    re, err := regexp.CompilePOSIX(`[0-9]+`)
    if err != nil {
        log.Fatal(err)
    }

    matched := re.MatchString("123")
    fmt.Println(matched)  // true
}
```

## Поиск совпадений

### MatchString

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`hello`)

    matched := re.MatchString("hello world")
    fmt.Println(matched)  // true
}
```

### FindString

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`\d+`)

    // Поиск первого совпадения
    match := re.FindString("abc 123 def 456")
    fmt.Println(match)  // "123"
}
```

### FindAllString

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`\d+`)

    // Поиск всех совпадений
    matches := re.FindAllString("abc 123 def 456", -1)
    fmt.Println(matches)  // ["123", "456"]

    // Ограничение количества
    matches = re.FindAllString("abc 123 def 456", 1)
    fmt.Println(matches)  // ["123"]
}
```

### FindStringIndex

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`\d+`)

    // Поиск индексов
    indices := re.FindStringIndex("abc 123 def")
    fmt.Println(indices)  // [4 7]
}
```

## Замена текста

### ReplaceAllString

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`\d+`)

    // Замена всех совпадений
    result := re.ReplaceAllString("abc 123 def 456", "XXX")
    fmt.Println(result)  // "abc XXX def XXX"
}
```

### ReplaceAllStringFunc

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`\d+`)

    // Замена с функцией
    result := re.ReplaceAllStringFunc("abc 123 def 456", func(s string) string {
        return "[" + s + "]"
    })
    fmt.Println(result)  // "abc [123] def [456]"
}
```

### ReplaceAllLiteralString

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`\d+`)

    // Буквальная замена (без интерпретации)
    result := re.ReplaceAllLiteralString("abc 123 def", "$1")
    fmt.Println(result)  // "abc $1 def"
}
```

## Группы захвата

### FindStringSubmatch

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`(\d+)-(\d+)`)

    // Поиск с группами
    matches := re.FindStringSubmatch("123-456")
    fmt.Println(matches)  // ["123-456", "123", "456"]

    // matches[0] - полное совпадение
    // matches[1] - первая группа
    // matches[2] - вторая группа
}
```

### FindAllStringSubmatch

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`(\d+)-(\d+)`)

    // Поиск всех совпадений с группами
    matches := re.FindAllStringSubmatch("123-456 789-012", -1)
    for _, match := range matches {
        fmt.Printf("Full: %s, Group1: %s, Group2: %s\n",
            match[0], match[1], match[2])
    }
}
```

### Именованные группы

```go
import "regexp"

func main() {
    re := regexp.MustCompile(`(?P<year>\d{4})-(?P<month>\d{2})-(?P<day>\d{2})`)

    matches := re.FindStringSubmatch("2025-01-11")
    if len(matches) > 0 {
        // Получение именованных групп через индексы
        yearIndex := re.SubexpIndex("year")
        monthIndex := re.SubexpIndex("month")
        dayIndex := re.SubexpIndex("day")

        if yearIndex >= 0 {
            fmt.Println("Year:", matches[yearIndex])
        }
        if monthIndex >= 0 {
            fmt.Println("Month:", matches[monthIndex])
        }
        if dayIndex >= 0 {
            fmt.Println("Day:", matches[dayIndex])
        }
    }
}
```

### Практические примеры: Валидация email

```go
var emailRegex = regexp.MustCompile(`^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$`)

func ValidateEmail(email string) bool {
    return emailRegex.MatchString(email)
}

// Более строгая валидация
var strictEmailRegex = regexp.MustCompile(`^[a-zA-Z0-9]([a-zA-Z0-9._-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9.-]*[a-zA-Z0-9])?\.[a-zA-Z]{2,}$`)

func ValidateEmailStrict(email string) bool {
    return strictEmailRegex.MatchString(email)
}
```

### Практические примеры: Валидация URL

```go
var urlRegex = regexp.MustCompile(`^https?://[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}(/.*)?$`)

func ValidateURL(url string) bool {
    return urlRegex.MatchString(url)
}

// Более детальная валидация
var detailedURLRegex = regexp.MustCompile(`^https?://([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,}(:[0-9]+)?(/.*)?$`)

func ValidateURLDetailed(url string) bool {
    return detailedURLRegex.MatchString(url)
}
```

### Практические примеры: Парсинг дат

```go
var dateRegex = regexp.MustCompile(`(\d{4})-(\d{2})-(\d{2})`)

func ParseDate(dateStr string) (year, month, day string, ok bool) {
    matches := dateRegex.FindStringSubmatch(dateStr)
    if len(matches) != 4 {
        return "", "", "", false
    }
    return matches[1], matches[2], matches[3], true
}

// Именованные группы
var namedDateRegex = regexp.MustCompile(`(?P<year>\d{4})-(?P<month>\d{2})-(?P<day>\d{2})`)

func ParseDateNamed(dateStr string) (map[string]string, bool) {
    matches := namedDateRegex.FindStringSubmatch(dateStr)
    if len(matches) == 0 {
        return nil, false
    }

    result := make(map[string]string)
    for i, name := range namedDateRegex.SubexpNames() {
        if i > 0 && name != "" {
            result[name] = matches[i]
        }
    }

    return result, true
}
```

### Практические примеры: Извлечение чисел

```go
var numberRegex = regexp.MustCompile(`-?\d+\.?\d*`)

func ExtractNumbers(text string) []string {
    return numberRegex.FindAllString(text, -1)
}

func ExtractFloats(text string) []float64 {
    matches := numberRegex.FindAllString(text, -1)
    floats := make([]float64, 0, len(matches))

    for _, match := range matches {
        if f, err := strconv.ParseFloat(match, 64); err == nil {
            floats = append(floats, f)
        }
    }

    return floats
}
```

### Практические примеры: Поиск и замена с контекстом

```go
var wordRegex = regexp.MustCompile(`\b\w+\b`)

func HighlightWords(text string, words []string) string {
    wordMap := make(map[string]bool)
    for _, word := range words {
        wordMap[strings.ToLower(word)] = true
    }

    return wordRegex.ReplaceAllStringFunc(text, func(match string) string {
        if wordMap[strings.ToLower(match)] {
            return fmt.Sprintf("<mark>%s</mark>", match)
        }
        return match
    })
}
```

### Практические примеры: Парсинг логов

```go
var logRegex = regexp.MustCompile(`(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) \[(\w+)\] (.+)`)

type LogEntry struct {
    Timestamp time.Time
    Level     string
    Message   string
}

func ParseLogLine(line string) (*LogEntry, error) {
    matches := logRegex.FindStringSubmatch(line)
    if len(matches) != 4 {
        return nil, fmt.Errorf("invalid log format")
    }

    timestamp, err := time.Parse("2006-01-02 15:04:05", matches[1])
    if err != nil {
        return nil, err
    }

    return &LogEntry{
        Timestamp: timestamp,
        Level:     matches[2],
        Message:   matches[3],
    }, nil
}
```

### Практические примеры: Валидация паролей

```go
var (
    hasUpper   = regexp.MustCompile(`[A-Z]`)
    hasLower   = regexp.MustCompile(`[a-z]`)
    hasNumber  = regexp.MustCompile(`[0-9]`)
    hasSpecial = regexp.MustCompile(`[!@#$%^&*]`)
    minLength  = 8
)

func ValidatePassword(password string) []string {
    var errors []string

    if len(password) < minLength {
        errors = append(errors, fmt.Sprintf("password must be at least %d characters", minLength))
    }

    if !hasUpper.MatchString(password) {
        errors = append(errors, "password must contain at least one uppercase letter")
    }

    if !hasLower.MatchString(password) {
        errors = append(errors, "password must contain at least one lowercase letter")
    }

    if !hasNumber.MatchString(password) {
        errors = append(errors, "password must contain at least one number")
    }

    if !hasSpecial.MatchString(password) {
        errors = append(errors, "password must contain at least one special character")
    }

    return errors
}
```

### Практические примеры: Парсинг CSV с regex

```go
var csvFieldRegex = regexp.MustCompile(`"([^"]*)"|([^,]+)`)

func ParseCSVLine(line string) []string {
    matches := csvFieldRegex.FindAllStringSubmatch(line, -1)
    fields := make([]string, 0, len(matches))

    for _, match := range matches {
        if match[1] != "" {
            fields = append(fields, match[1]) // Quoted field
        } else {
            fields = append(fields, strings.TrimSpace(match[2])) // Unquoted field
        }
    }

    return fields
}
```

### Практические примеры: Поиск `IP` адресов

```go
var ipRegex = regexp.MustCompile(`\b(?:\d{1,3}\.){3}\d{1,3}\b`)

func ExtractIPs(text string) []string {
    return ipRegex.FindAllString(text, -1)
}

// Валидация IP адреса
func ValidateIP(ip string) bool {
    parts := strings.Split(ip, ".")
    if len(parts) != 4 {
        return false
    }

    for _, part := range parts {
        num, err := strconv.Atoi(part)
        if err != nil || num < 0 || num > 255 {
            return false
        }
    }

    return true
}
```

### Практические примеры: Кэширование регулярных выражений

```go
type RegexCache struct {
    cache map[string]*regexp.Regexp
    mu    sync.RWMutex
}

func NewRegexCache() *RegexCache {
    return &RegexCache{
        cache: make(map[string]*regexp.Regexp),
    }
}

func (c *RegexCache) Get(pattern string) (*regexp.Regexp, error) {
    c.mu.RLock()
    re, exists := c.cache[pattern]
    c.mu.RUnlock()

    if exists {
        return re, nil
    }

    // Компиляция
    re, err := regexp.Compile(pattern)
    if err != nil {
        return nil, err
    }

    c.mu.Lock()
    c.cache[pattern] = re
    c.mu.Unlock()

    return re, nil
}

func (c *RegexCache) MustGet(pattern string) *regexp.Regexp {
    re, err := c.Get(pattern)
    if err != nil {
        panic(err)
    }
    return re
}
```

### Практические примеры: Поиск с ограничением

```go
func FindMatchesWithLimit(text string, pattern string, limit int) []string {
    re := regexp.MustCompile(pattern)
    matches := re.FindAllString(text, limit)
    return matches
}

func FindMatchesInRange(text string, pattern string, start, end int) []string {
    if start < 0 || end > len(text) || start > end {
        return nil
    }

    re := regexp.MustCompile(pattern)
    matches := re.FindAllString(text[start:end], -1)
    return matches
}
```

### Практические примеры: Замена с обратными ссылками

```go
var phoneRegex = regexp.MustCompile(`(\d{3})-(\d{3})-(\d{4})`)

func FormatPhoneNumber(phone string) string {
    // Замена с использованием групп
    return phoneRegex.ReplaceAllString(phone, "($1) $2-$3")
}

// Пример: "123-456-7890" -> "(123) 456-7890"
```

### Практические примеры: Многострочный поиск

```go
var multiLineRegex = regexp.MustCompile(`(?m)^\s*(\w+):\s*(.+)$`)

func ParseKeyValuePairs(text string) map[string]string {
    matches := multiLineRegex.FindAllStringSubmatch(text, -1)
    result := make(map[string]string)

    for _, match := range matches {
        if len(match) == 3 {
            result[match[1]] = match[2]
        }
    }

    return result
}
```

### Практические примеры: Поиск с анкорами

```go
// Поиск в начале строки
var startRegex = regexp.MustCompile(`^hello`)

// Поиск в конце строки
var endRegex = regexp.MustCompile(`world$`)

// Поиск целого слова
var wordBoundaryRegex = regexp.MustCompile(`\bhello\b`)

func FindAtStart(text string) bool {
    return startRegex.MatchString(text)
}

func FindAtEnd(text string) bool {
    return endRegex.MatchString(text)
}

func FindWholeWord(text string) bool {
    return wordBoundaryRegex.MatchString(text)
}
```

### Практические примеры: Экранирование специальных символов

```go
import "regexp"

func EscapeRegex(pattern string) string {
    return regexp.QuoteMeta(pattern)
}

// Использование
func SearchLiteral(text, literal string) bool {
    escaped := regexp.QuoteMeta(literal)
    re := regexp.MustCompile(escaped)
    return re.MatchString(text)
}
```

### Практические примеры: Кэширование регулярных выражений

```go
type RegexCache struct {
    cache map[string]*regexp.Regexp
    mu    sync.RWMutex
}

func NewRegexCache() *RegexCache {
    return &RegexCache{
        cache: make(map[string]*regexp.Regexp),
    }
}

func (rc *RegexCache) Get(pattern string) (*regexp.Regexp, error) {
    rc.mu.RLock()
    if re, ok := rc.cache[pattern]; ok {
        rc.mu.RUnlock()
        return re, nil
    }
    rc.mu.RUnlock()

    rc.mu.Lock()
    defer rc.mu.Unlock()

    // Двойная проверка
    if re, ok := rc.cache[pattern]; ok {
        return re, nil
    }

    re, err := regexp.Compile(pattern)
    if err != nil {
        return nil, err
    }

    rc.cache[pattern] = re
    return re, nil
}
```

### Практические примеры: Валидация данных

```go
var (
    EmailRegex    = regexp.MustCompile(`^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$`)
    PhoneRegex    = regexp.MustCompile(`^\+?[1-9]\d{1,14}$`)
    URLRegex      = regexp.MustCompile(`^https?://[^\s/$.?#].[^\s]*$`)
    IPRegex       = regexp.MustCompile(`^(\d{1,3}\.){3}\d{1,3}$`)
    UUIDRegex     = regexp.MustCompile(`^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$`)
)

func ValidateEmail(email string) bool {
    return EmailRegex.MatchString(email)
}

func ValidatePhone(phone string) bool {
    return PhoneRegex.MatchString(phone)
}

func ValidateURL(url string) bool {
    return URLRegex.MatchString(url)
}
```

### Практические примеры: Парсинг структурированных данных

```go
// Парсинг логов
type LogEntry struct {
    Timestamp time.Time
    Level     string
    Message   string
}

var LogRegex = regexp.MustCompile(`^(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}) \[(\w+)\] (.+)$`)

func ParseLogLine(line string) (*LogEntry, error) {
    matches := LogRegex.FindStringSubmatch(line)
    if len(matches) != 4 {
        return nil, fmt.Errorf("invalid log format")
    }

    timestamp, err := time.Parse("2006-01-02 15:04:05", matches[1])
    if err != nil {
        return nil, err
    }

    return &LogEntry{
        Timestamp: timestamp,
        Level:     matches[2],
        Message:   matches[3],
    }, nil
}

// Парсинг CSV с кавычками
func ParseCSVLine(line string) []string {
    var fields []string
    var current strings.Builder
    inQuotes := false

    for i := 0; i < len(line); i++ {
        char := line[i]
        switch {
        case char == '"':
            inQuotes = !inQuotes
        case char == ',' && !inQuotes:
            fields = append(fields, current.String())
            current.Reset()
        default:
            current.WriteByte(char)
        }
    }
    fields = append(fields, current.String())

    return fields
}
```

### Практические примеры: Поиск и замена с функциями

```go
func ReplaceFunc(re *regexp.Regexp, text string, fn func(string) string) string {
    return re.ReplaceAllStringFunc(text, fn)
}

// Пример: замена чисел их квадратами
func SquareNumbers(text string) string {
    numRegex := regexp.MustCompile(`\d+`)
    return numRegex.ReplaceAllStringFunc(text, func(match string) string {
        num, _ := strconv.Atoi(match)
        return strconv.Itoa(num * num)
    })
}

// Пример: маскирование чувствительных данных
func MaskSensitiveData(text string) string {
    emailRegex := regexp.MustCompile(`([a-zA-Z0-9._%+\-]+)@([a-zA-Z0-9.\-]+\.[a-zA-Z]{2,})`)
    return emailRegex.ReplaceAllStringFunc(text, func(match string) string {
        parts := strings.Split(match, "@")
        username := parts[0]
        domain := parts[1]
        if len(username) > 3 {
            username = username[:2] + "*" + username[len(username)-1:]
        }
        return username + "@" + domain
    })
}
```

## Лучшие практики

1. **Компилируйте регулярные выражения заранее** — для повторного использования
2. **Используйте MustCompile** — если паттерн известен на этапе компиляции
3. **Обрабатывайте ошибки** — при компиляции паттернов
4. **Избегайте сложных паттернов** — для лучшей производительности
5. **Кэшируйте скомпилированные выражения** — для повторного использования
6. **Используйте именованные группы** — для лучшей читаемости
7. **Экранируйте специальные символы** — при поиске литералов
8. **Тестируйте паттерны** — проверяйте на различных входных данных
9. **Используйте анкоры** — для точного поиска
10. **Ограничивайте количество совпадений** — для производительности
11. **Используйте для валидации** — проверяйте форматы данных
12. **Парсите структурированные данные** — извлекайте данные из текста
13. **Используйте функции замены** — для сложных преобразований
14. **Оптимизируйте паттерны** — избегайте **backtracking**
15. **Документируйте паттерны** — объясняйте сложные регулярные выражения


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Регулярные выражения в Go предоставляют мощные инструменты для работы с текстом. Понимание компиляции паттернов, поиска совпадений, замены текста, групп захвата, кэширования, валидации, парсинга и практических применений критично для эффективного использования регулярных выражений в Go. Правильное использование регулярных выражений позволяет создавать эффективные, надежные инструменты для обработки текста, валидации данных и извлечения информации из структурированных форматов.

## Дополнительные ресурсы

- [Go regexp Package](https://pkg.go.dev/regexp)
- [RE2 Syntax](https://github.com/google/re2/wiki/Syntax)

## См. также

- [Go: продвинутые паттерны](go-advanced-patterns.md)
- [Go: основы](go-basics.md)
- [Go: бенчмаркинг](go-benchmarking.md)
- [Go: лучшие практики](go-best-practices.md)
- [Go: сборка и развертывание](go-build.md)
