---
title: "Go: работа со временем"
description: "Полное руководство по работе со временем в Go: time package, парсинг, форматирование, таймеры, таймауты"
tags:
  - go
  - golang
  - time
  - timers
  - timeouts
  - dates
type: "overview"
difficulty: "intermediate"
aliases:
  - "Go"
  - "работа со временем"
  - "Go: работа со временем"
  - "go time"
prerequisites:
  - "[[go-basics]]"
next: []
updated: "2026-04-20"
---

# Go: работа со временем

## Полезные ссылки

- [Go time Package](https://pkg.go.dev/time)
- [Go Time Formatting](https://go.dev/blog/formatting)

## Содержание

- [Введение в работу со временем](#введение-в-работу-со-временем)
  - [Основные типы](#основные-типы)
- [Создание времени](#создание-времени)
  - [Текущее время](#текущее-время)
  - [Создание конкретного времени](#создание-конкретного-времени)
  - [Парсинг времени](#парсинг-времени)
- [Парсинг и форматирование](#парсинг-и-форматирование)
  - [Форматирование времени](#форматирование-времени)
  - [Строковое представление](#строковое-представление)
- [Операции со временем](#операции-со-временем)
  - [Добавление и вычитание](#добавление-и-вычитание)
  - [Сравнение времени](#сравнение-времени)
  - [Извлечение компонентов](#извлечение-компонентов)
- [Таймеры и таймауты](#таймеры-и-таймауты)
  - [Таймер](#таймер)
  - [Ticker](#ticker)
  - [Таймаут с select](#таймаут-с-select)
  - [Sleep](#sleep)
- [Часовые пояса](#часовые-пояса)
  - [Работа с часовыми поясами](#работа-с-часовыми-поясами)
  - [Конвертация часовых поясов](#конвертация-часовых-поясов)
  - [Практические примеры: Форматирование с локализацией](#практические-примеры-форматирование-с-локализацией)
  - [Практические примеры: Вычисление разницы во времени](#практические-примеры-вычисление-разницы-во-времени)
  - [Практические примеры: Проверка рабочего времени](#практические-примеры-проверка-рабочего-времени)
  - [Практические примеры: Таймер с переиспользованием](#практические-примеры-таймер-с-переиспользованием)
  - [Практические примеры: Парсинг различных форматов](#практические-примеры-парсинг-различных-форматов)
  - [Практические примеры: Вычисление возраста](#практические-примеры-вычисление-возраста)
  - [Практические примеры: Форматирование относительного времени](#практические-примеры-форматирование-относительного-времени)
  - [Практические примеры: Таймер с callback](#практические-примеры-таймер-с-callback)
  - [Практические примеры: Периодическое выполнение с остановкой](#практические-примеры-периодическое-выполнение-с-остановкой)
  - [Практические примеры: Debounce для операций](#практические-примеры-debounce-для-операций)
  - [Практические примеры: Парсинг различных форматов времени](#практические-примеры-парсинг-различных-форматов-времени)
  - [Практические примеры: Работа с временными интервалами](#практические-примеры-работа-с-временными-интервалами)
  - [Практические примеры: Таймеры и таймауты](#практические-примеры-таймеры-и-таймауты)
  - [Практические примеры: Форматирование времени для разных локалей](#практические-примеры-форматирование-времени-для-разных-локалей)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение в работу со временем

Пакет `time` предоставляет функциональность для работы со временем, датами и таймерами. Понимание работы со временем критично для создания приложений, работающих с временными данными.

### Основные типы

1. **time.Time** — представляет момент времени
2. **time.Duration** — представляет продолжительность времени
3. **time.Location** — представляет часовой пояс

## Создание времени

### Текущее время

```go
import "time"

func main() {
    // Текущее время
    now := time.Now()
    fmt.Println(now)

    // UTC время
    utc := time.Now().UTC()
    fmt.Println(utc)
}
```

### Создание конкретного времени

```go
import "time"

func main() {
    // Создание времени
    t := time.Date(2025, 1, 11, 12, 30, 0, 0, time.UTC)
    fmt.Println(t)

    // Unix timestamp
    unixTime := time.Unix(1704978000, 0)
    fmt.Println(unixTime)
}
```

### Парсинг времени

```go
import "time"

func main() {
    // Парсинг из строки
    t, err := time.Parse("2006-01-02 15:04:05", "2025-01-11 12:30:00")
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println(t)

    // Парсинг RFC3339
    t, err = time.Parse(time.RFC3339, "2025-01-11T12:30:00Z")
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println(t)
}
```

## Парсинг и форматирование

### Форматирование времени

```go
import "time"

func main() {
    now := time.Now()

    // Стандартные форматы
    fmt.Println(now.Format(time.RFC3339))
    fmt.Println(now.Format(time.RFC822))
    fmt.Println(now.Format(time.RFC1123))

    // Кастомный формат
    fmt.Println(now.Format("2006-01-02 15:04:05"))
    fmt.Println(now.Format("02/01/2006"))
    fmt.Println(now.Format("15:04:05"))
}
```

### Строковое представление

```go
import "time"

func main() {
    t := time.Now()

    // String() метод
    fmt.Println(t.String())

    // Unix timestamp
    fmt.Println(t.Unix())
    fmt.Println(t.UnixNano())
}
```

## Операции со временем

### Добавление и вычитание

```go
import "time"

func main() {
    now := time.Now()

    // Добавление
    future := now.Add(24 * time.Hour)
    fmt.Println(future)

    // Вычитание
    past := now.Add(-24 * time.Hour)
    fmt.Println(past)

    // Добавление компонентов
    future = now.AddDate(0, 1, 0)  // +1 месяц
    fmt.Println(future)
}
```

### Сравнение времени

```go
import "time"

func main() {
    t1 := time.Now()
    t2 := t1.Add(1 * time.Hour)

    // Сравнение
    fmt.Println(t1.Before(t2))  // true
    fmt.Println(t1.After(t2))   // false
    fmt.Println(t1.Equal(t2))   // false

    // Разница
    diff := t2.Sub(t1)
    fmt.Println(diff)  // 1h0m0s
}
```

### Извлечение компонентов

```go
import "time"

func main() {
    t := time.Now()

    fmt.Println(t.Year())
    fmt.Println(t.Month())
    fmt.Println(t.Day())
    fmt.Println(t.Hour())
    fmt.Println(t.Minute())
    fmt.Println(t.Second())
    fmt.Println(t.Weekday())
}
```

## Таймеры и таймауты

### Таймер

```go
import "time"

func main() {
    // Создание таймера
    timer := time.NewTimer(2 * time.Second)

    <-timer.C
    fmt.Println("Timer expired")
}
```

### Ticker

```go
import "time"

func main() {
    // Создание ticker
    ticker := time.NewTicker(1 * time.Second)
    defer ticker.Stop()

    for t := range ticker.C {
        fmt.Println("Tick at", t)
    }
}
```

### Таймаут с select

```go
import "time"

func main() {
    ch := make(chan string)

    go func() {
        time.Sleep(2 * time.Second)
        ch <- "result"
    }()

    select {
    case res := <-ch:
        fmt.Println("Received:", res)
    case <-time.After(1 * time.Second):
        fmt.Println("Timeout")
    }
}
```

### Sleep

```go
import "time"

func main() {
    // Пауза выполнения
    time.Sleep(2 * time.Second)
    fmt.Println("After sleep")
}
```

## Часовые пояса

### Работа с часовыми поясами

```go
import "time"

func main() {
    // UTC время
    utc := time.Now().UTC()
    fmt.Println("UTC:", utc)

    // Локальное время
    local := time.Now().Local()
    fmt.Println("Local:", local)

    // Конкретный часовой пояс
    loc, err := time.LoadLocation("America/New_York")
    if err != nil {
        log.Fatal(err)
    }
    nyTime := time.Now().In(loc)
    fmt.Println("NY Time:", nyTime)
}
```

### Конвертация часовых поясов

```go
import "time"

func main() {
    utc := time.Now().UTC()

    // Конвертация в другой часовой пояс
    loc, _ := time.LoadLocation("Asia/Tokyo")
    tokyoTime := utc.In(loc)
    fmt.Println("Tokyo Time:", tokyoTime)
}
```

### Практические примеры: Форматирование с локализацией

```go
func formatLocalized(t time.Time, locale string) string {
    loc, err := time.LoadLocation(locale)
    if err != nil {
        return t.Format(time.RFC3339)
    }

    return t.In(loc).Format("2006-01-02 15:04:05")
}
```

### Практические примеры: Вычисление разницы во времени

```go
func timeDifference(t1, t2 time.Time) string {
    diff := t2.Sub(t1)

    if diff < 0 {
        diff = -diff
    }

    days := int(diff.Hours() / 24)
    hours := int(diff.Hours()) % 24
    minutes := int(diff.Minutes()) % 60
    seconds := int(diff.Seconds()) % 60

    return fmt.Sprintf("%d days, %d hours, %d minutes, %d seconds",
        days, hours, minutes, seconds)
}
```

### Практические примеры: Проверка рабочего времени

```go
func isBusinessHours(t time.Time, startHour, endHour int) bool {
    hour := t.Hour()
    weekday := t.Weekday()

    // Выходные
    if weekday == time.Saturday || weekday == time.Sunday {
        return false
    }

    return hour >= startHour && hour < endHour
}

func nextBusinessHour(t time.Time, startHour, endHour int) time.Time {
    result := t

    for {
        if isBusinessHours(result, startHour, endHour) {
            return result
        }

        // Если выходной, переходим к следующему понедельнику
        if result.Weekday() == time.Saturday {
            daysUntilMonday := 2
            result = result.AddDate(0, 0, daysUntilMonday)
            result = time.Date(result.Year(), result.Month(), result.Day(),
                startHour, 0, 0, 0, result.Location())
        } else if result.Weekday() == time.Sunday {
            daysUntilMonday := 1
            result = result.AddDate(0, 0, daysUntilMonday)
            result = time.Date(result.Year(), result.Month(), result.Day(),
                startHour, 0, 0, 0, result.Location())
        } else if result.Hour() < startHour {
            // До начала рабочего дня
            result = time.Date(result.Year(), result.Month(), result.Day(),
                startHour, 0, 0, 0, result.Location())
        } else if result.Hour() >= endHour {
            // После окончания рабочего дня
            result = result.AddDate(0, 0, 1)
            result = time.Date(result.Year(), result.Month(), result.Day(),
                startHour, 0, 0, 0, result.Location())
        }
    }
}
```

### Практические примеры: Таймер с переиспользованием

```go
type ReusableTimer struct {
    timer *time.Timer
    mu    sync.Mutex
}

func NewReusableTimer() *ReusableTimer {
    return &ReusableTimer{
        timer: time.NewTimer(0),
    }
}

func (rt *ReusableTimer) Reset(duration time.Duration) {
    rt.mu.Lock()
    defer rt.mu.Unlock()

    if !rt.timer.Stop() {
        <-rt.timer.C
    }
    rt.timer.Reset(duration)
}

func (rt *ReusableTimer) C() <-chan time.Time {
    return rt.timer.C
}

func (rt *ReusableTimer) Stop() bool {
    rt.mu.Lock()
    defer rt.mu.Unlock()
    return rt.timer.Stop()
}
```

### Практические примеры: Парсинг различных форматов

```go
func parseTimeFlexible(timeStr string) (time.Time, error) {
    formats := []string{
        time.RFC3339,
        time.RFC3339Nano,
        "2006-01-02 15:04:05",
        "2006-01-02T15:04:05",
        "2006-01-02",
        "15:04:05",
        time.RFC822,
        time.RFC822Z,
        time.RFC1123,
        time.RFC1123Z,
    }

    for _, format := range formats {
        if t, err := time.Parse(format, timeStr); err == nil {
            return t, nil
        }
    }

    return time.Time{}, fmt.Errorf("unable to parse time: %s", timeStr)
}
```

### Практические примеры: Вычисление возраста

```go
func calculateAge(birthDate time.Time) (years, months, days int) {
    now := time.Now()

    years = now.Year() - birthDate.Year()
    months = int(now.Month()) - int(birthDate.Month())
    days = now.Day() - birthDate.Day()

    if days < 0 {
        months--
        days += time.Date(now.Year(), now.Month(), 0, 0, 0, 0, 0, time.UTC).Day()
    }

    if months < 0 {
        years--
        months += 12
    }

    return years, months, days
}
```

### Практические примеры: Форматирование относительного времени

```go
func formatRelativeTime(t time.Time) string {
    now := time.Now()
    diff := now.Sub(t)

    if diff < time.Minute {
        return "just now"
    } else if diff < time.Hour {
        minutes := int(diff.Minutes())
        return fmt.Sprintf("%d minute(s) ago", minutes)
    } else if diff < 24*time.Hour {
        hours := int(diff.Hours())
        return fmt.Sprintf("%d hour(s) ago", hours)
    } else if diff < 7*24*time.Hour {
        days := int(diff.Hours() / 24)
        return fmt.Sprintf("%d day(s) ago", days)
    } else if diff < 30*24*time.Hour {
        weeks := int(diff.Hours() / (7 * 24))
        return fmt.Sprintf("%d week(s) ago", weeks)
    } else if diff < 365*24*time.Hour {
        months := int(diff.Hours() / (30 * 24))
        return fmt.Sprintf("%d month(s) ago", months)
    } else {
        years := int(diff.Hours() / (365 * 24))
        return fmt.Sprintf("%d year(s) ago", years)
    }
}
```

### Практические примеры: Таймер с callback

```go
type CallbackTimer struct {
    duration time.Duration
    callback func()
    timer    *time.Timer
    mu       sync.Mutex
}

func NewCallbackTimer(duration time.Duration, callback func()) *CallbackTimer {
    return &CallbackTimer{
        duration: duration,
        callback: callback,
    }
}

func (ct *CallbackTimer) Start() {
    ct.mu.Lock()
    defer ct.mu.Unlock()

    if ct.timer != nil {
        ct.timer.Stop()
    }

    ct.timer = time.AfterFunc(ct.duration, ct.callback)
}

func (ct *CallbackTimer) Stop() {
    ct.mu.Lock()
    defer ct.mu.Unlock()

    if ct.timer != nil {
        ct.timer.Stop()
        ct.timer = nil
    }
}
```

### Практические примеры: Периодическое выполнение с остановкой

```go
func runPeriodically(ctx context.Context, interval time.Duration, fn func() error) error {
    ticker := time.NewTicker(interval)
    defer ticker.Stop()

    for {
        select {
        case <-ctx.Done():
            return ctx.Err()
        case <-ticker.C:
            if err := fn(); err != nil {
                return err
            }
        }
    }
}
```

### Практические примеры: Debounce для операций

```go
type Debouncer struct {
    duration time.Duration
    timer    *time.Timer
    mu       sync.Mutex
    fn       func()
}

func NewDebouncer(duration time.Duration, fn func()) *Debouncer {
    return &Debouncer{
        duration: duration,
        fn:       fn,
    }
}

func (d *Debouncer) Trigger() {
    d.mu.Lock()
    defer d.mu.Unlock()

    if d.timer != nil {
        d.timer.Stop()
    }

    d.timer = time.AfterFunc(d.duration, d.fn)
}
```

### Практические примеры: Парсинг различных форматов времени

```go
func ParseMultipleFormats(timeStr string) (time.Time, error) {
    formats := []string{
        time.RFC3339,
        time.RFC3339Nano,
        "2006-01-02 15:04:05",
        "2006-01-02T15:04:05Z07:00",
        "2006-01-02",
        "15:04:05",
    }

    for _, format := range formats {
        if t, err := time.Parse(format, timeStr); err == nil {
            return t, nil
        }
    }

    return time.Time{}, fmt.Errorf("unable to parse time: %s", timeStr)
}

func ParseWithLocation(timeStr string, loc *time.Location) (time.Time, error) {
    t, err := time.ParseInLocation("2006-01-02 15:04:05", timeStr, loc)
    if err != nil {
        return time.Time{}, err
    }
    return t, nil
}
```

### Практические примеры: Работа с временными интервалами

```go
func IsBusinessHours(t time.Time, timezone string) bool {
    loc, err := time.LoadLocation(timezone)
    if err != nil {
        return false
    }

    localTime := t.In(loc)
    hour := localTime.Hour()
    weekday := localTime.Weekday()

    // Понедельник - Пятница, 9:00 - 17:00
    if weekday >= time.Monday && weekday <= time.Friday {
        return hour >= 9 && hour < 17
    }

    return false
}

func NextBusinessDay(t time.Time) time.Time {
    next := t.AddDate(0, 0, 1)

    for next.Weekday() == time.Saturday || next.Weekday() == time.Sunday {
        next = next.AddDate(0, 0, 1)
    }

    return next
}
```

### Практические примеры: Таймеры и таймауты

```go
func ExecuteWithTimeout(fn func() error, timeout time.Duration) error {
    done := make(chan error, 1)

    go func() {
        done <- fn()
    }()

    select {
    case err := <-done:
        return err
    case <-time.After(timeout):
        return fmt.Errorf("operation timeout after %v", timeout)
    }
}

func ExecuteWithDeadline(fn func() error, deadline time.Time) error {
    now := time.Now()
    if deadline.Before(now) {
        return fmt.Errorf("deadline already passed")
    }

    timeout := deadline.Sub(now)
    return ExecuteWithTimeout(fn, timeout)
}

func PeriodicTask(interval time.Duration, fn func()) *time.Ticker {
    ticker := time.NewTicker(interval)

    go func() {
        for range ticker.C {
            fn()
        }
    }()

    return ticker
}
```

### Практические примеры: Форматирование времени для разных локалей

```go
func FormatForLocale(t time.Time, locale string) string {
    loc, err := time.LoadLocation(locale)
    if err != nil {
        loc = time.UTC
    }

    localTime := t.In(loc)

    // Различные форматы для разных локалей
    formats := map[string]string{
        "en_US": "January 2, 2006 3:04 PM",
        "de_DE": "2. January 2006 15:04",
        "fr_FR": "2 janvier 2006 15:04",
        "ru_RU": "2 января 2006 15:04",
    }

    format := formats[locale]
    if format == "" {
        format = time.RFC3339
    }

    return localTime.Format(format)
}
```

## Лучшие практики

1. **Используйте `UTC` для хранения** — конвертируйте в локальное время только для отображения
2. **Используйте time.Duration** — для представления продолжительности
3. **Проверяйте ошибки** — при парсинге времени
4. **Используйте константы времени** — **time.Second**, **time.Minute**, **time.Hour**
5. **Учитывайте часовые пояса** — при работе с датами и временем
6. **Используйте таймеры правильно** — останавливайте таймеры после использования
7. **Избегайте time.`Sleep` в циклах** — используйте таймеры или **tickers**
8. **Проверяйте таймауты** — используйте **context** для таймаутов
9. **Форматируйте правильно** — используйте правильные форматы для парсинга
10. **Учитывайте летнее время** — при работе с часовыми поясами
11. **Парсите множественные форматы** — обрабатывайте различные форматы времени
12. **Используйте бизнес-логику** — проверяйте рабочие часы и дни
13. **Используйте таймауты** — для ограничения времени операций
14. **Используйте deadline** — для абсолютных временных ограничений
15. **Форматируйте для локалей** — учитывайте локализацию времени


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Работа со временем в Go предоставляет мощные инструменты для работы с датами, временем, таймерами, таймаутами и часовыми поясами. Понимание создания времени, парсинга, форматирования, операций со временем, таймеров, **tickers**, часовых поясов, бизнес-логики и практических применений критично для эффективной работы со временем в Go. Правильное использование времени позволяет создавать надежные, точные, локализованные и эффективные временные операции.

## Дополнительные ресурсы

- [Go time Package](https://pkg.go.dev/time)
- [Go Time Formatting](https://go.dev/blog/formatting)

## См. также

- [Go: продвинутые паттерны](go-advanced-patterns.md)
- [Go: основы](go-basics.md)
- [Go: бенчмаркинг](go-benchmarking.md)
- [Go: лучшие практики](go-best-practices.md)
- [Go: сборка и развертывание](go-build.md)
