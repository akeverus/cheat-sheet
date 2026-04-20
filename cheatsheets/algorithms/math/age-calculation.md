---
title: "Вычисление возраста (Age Calculation)"
description: "Описание расчёта возраста в годах (и в днях/месяцах) по дате рождения и текущей дате. Рассмотрены подходы на Java 8 (Period, LocalDate), Joda-Time и Java 7 (Calendar, DateFormat)."
tags:
  - algorithms
  - math
  - age-calculation
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Вычисление возраста (Age Calculation)

Описание расчёта возраста в годах (и в днях/месяцах) по дате рождения и текущей дате. Рассмотрены подходы на `Java` 8 (`Period`, `LocalDate`), `Joda-Time` и `Java` 7 (`Calendar`, `DateFormat`).

## Полезные ссылки

### Официальная документация
- [Period (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html)
- [Joda-Time (joda.org)](https://www.joda.org/joda-time/)

### См. также
- [[standard-deviation|Стандартное отклонение]] — стандартное отклонение
- [[fibonacci-sequence|Ряд Фибоначчи]] — ряд Фибоначчи

- [[line-intersection|Пересечение прямых (Line Intersection)]]
- [[circle-area-calculation|Вычисление площади круга (Circle Area Calculation)]]
- [[coprime-numbers|Взаимно простые числа (Coprime Numbers)]]
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Примеры](#примеры)
- [Реализация на Java](#реализация-на-java)
  - [Подход 1: Java 8 (Period) (Java)](#подход-1-java-8-period-java)
  - [Пример использования (Java)](#пример-использования-java)
  - [Получение более точного возраста](#получение-более-точного-возраста)
  - [Получение возраста в месяцах и днях](#получение-возраста-в-месяцах-и-днях)
- [Подход 2: Joda-Time](#подход-2-joda-time)
- [Подход 3: Java 7](#подход-3-java-7)
  - [Улучшенная версия для Java 7](#улучшенная-версия-для-java-7)
  - [Версия с учетом месяца и дня](#версия-с-учетом-месяца-и-дня)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Подход 1: Java 8 (Period) (Kotlin)](#подход-1-java-8-period-kotlin)
  - [Пример использования (Kotlin)](#пример-использования-kotlin)
  - [Проверка совершеннолетия](#проверка-совершеннолетия)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Проверка совершеннолетия](#вариант-1-проверка-совершеннолетия)
  - [Вариант 2: Возраст в днях](#вариант-2-возраст-в-днях)
  - [Вариант 3: Возраст в месяцах](#вариант-3-возраст-в-месяцах)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Возраст в годах вычисляется по дате рождения и текущей дате. Например: дата рождения 1990-01-15, текущая дата 2025-01-15 35 лет; 2000-06-20 и 2025-01-15 24 года. Ниже рассмотрены реализации на `Java` 8, `Joda-Time` и `Java` 7.

### Примеры

Дата рождения 1990-01-15, текущая дата 2025-01-15 возраст 35 лет. Дата рождения 2000-06-20, текущая дата 2025-01-15 возраст 24 года.

## Реализация на Java

### Подход 1: Java 8 (Period) (Java)

В `Java` 8 для работы с датами без времени используется `LocalDate`, для разницы в годах/месяцах/днях — `Period`. Метод `Period.between(birthDate, currentDate).getYears()` даёт возраст в полных годах. `LocalDate.now()` возвращает текущую дату в системной таймзоне.

```java
// Вычисление возраста в годах через Period.between (Java 8)
import java.time.LocalDate;
import java.time.Period;

public int calculateAge(LocalDate birthDate, LocalDate currentDate) {
    return Period.between(birthDate, currentDate).getYears();
}
```

### Пример использования (Java)

```java
// Пример вызова: дата рождения и текущая дата
LocalDate birthDate = LocalDate.of(1990, 1, 15);
LocalDate currentDate = LocalDate.now();
int age = calculateAge(birthDate, currentDate);
```

### Получение более точного возраста

Для возраста в секундах используют `LocalDateTime` и `Duration.between`; результат — `long` секунд.

```java
// Возраст в секундах через Duration (для даты-времени)
import java.time.Duration;
import java.time.LocalDateTime;

public long calculateAgeInSeconds(LocalDateTime birthDateTime, LocalDateTime currentDateTime) {
    Duration duration = Duration.between(birthDateTime, currentDateTime);
    return duration.getSeconds();
}
```

### Получение возраста в месяцах и днях

`Period` даёт годы, месяцы и дни по отдельности — удобно для вывода «N лет, M месяцев, K дней».

```java
// Детальный возраст: годы, месяцы, дни
public String calculateDetailedAge(LocalDate birthDate, LocalDate currentDate) {
    Period period = Period.between(birthDate, currentDate);
    return String.format("%d years, %d months, %d days",
        period.getYears(), period.getMonths(), period.getDays());
}
```

## Подход 2: Joda-Time

Если проект на `Java` 7 или без `java.time`, подойдёт `Joda-Time`. Добавьте зависимость в `pom.xml`:

```xml
<!-- Зависимость Joda-Time для проектов на Java 7 -->
<dependency>
    <groupId>joda-time</groupId>
    <artifactId>joda-time</artifactId>
    <version>2.10</version>
</dependency>
```

Метод расчёта возраста через `Years.yearsBetween`:

```java
// Joda-Time: Years.yearsBetween для возраста в годах
import org.joda.time.LocalDate;
import org.joda.time.Years;

public int calculateAgeWithJodaTime(LocalDate birthDate, LocalDate currentDate) {
    Years age = Years.yearsBetween(birthDate, currentDate);
    return age.getYears();
}
```

## Подход 3: Java 7

В `Java` 7 нет `Period` и `LocalDate`, поэтому возраст считают через `Calendar` или преобразование дат в числа. Ниже — вариант через `DateFormat` (формат yyyyMMdd) и разницу лет по целым числам; точнее — вариант с `Calendar` и учётом месяца и дня.

```java
// Java 7: преобразование в yyyyMMdd и разница в годах (грубая оценка)
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public int calculateAgeWithJava7(Date birthDate, Date currentDate) {
    DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    int d1 = Integer.parseInt(formatter.format(birthDate));
    int d2 = Integer.parseInt(formatter.format(currentDate));
    int age = (d2 - d1) / 10000;
    return age;
}
```

### Улучшенная версия для Java 7

```java
// Точный возраст по году и дню года (учитывает, что день рождения в этом году ещё не наступил)
import java.util.Calendar;

public int calculateAgeWithCalendar(Date birthDate, Date currentDate) {
    Calendar birth = Calendar.getInstance();
    birth.setTime(birthDate);
    Calendar current = Calendar.getInstance();
    current.setTime(currentDate);

    int age = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

    if (current.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
        age--;
    }

    return age;
}
```

### Версия с учетом месяца и дня

```java
// Учёт месяца и дня: уменьшаем возраст, если текущий день ещё не достиг дня рождения
public int calculateAgeAccurate(Date birthDate, Date currentDate) {
    Calendar birth = Calendar.getInstance();
    birth.setTime(birthDate);
    Calendar current = Calendar.getInstance();
    current.setTime(currentDate);

    int age = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

    int monthDiff = current.get(Calendar.MONTH) - birth.get(Calendar.MONTH);
    if (monthDiff < 0 || (monthDiff == 0 &&
        current.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
        age--;
    }

    return age;
}
```

## Реализация на Kotlin

### Подход 1: Java 8 (Period) (Kotlin)

```kotlin
// Аналоги Java 8: Period, ChronoUnit для дней/месяцев/секунд
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

fun calculateAgeK(birthDate: LocalDate, currentDate: LocalDate): Int {
    return Period.between(birthDate, currentDate).years
}

fun calculateAgeInSecondsK(birthDateTime: java.time.LocalDateTime, currentDateTime: java.time.LocalDateTime): Long {
    return ChronoUnit.SECONDS.between(birthDateTime, currentDateTime)
}

fun calculateDetailedAgeK(birthDate: LocalDate, currentDate: LocalDate): String {
    val period = Period.between(birthDate, currentDate)
    return "${period.years} years, ${period.months} months, ${period.days} days"
}
```

### Пример использования (Kotlin)

```kotlin
fun main() {
    val birthDate = LocalDate.of(1990, 1, 15)
    val currentDate = LocalDate.now()
    val age = calculateAgeK(birthDate, currentDate)
    println("Age: $age years")

    val detailedAge = calculateDetailedAgeK(birthDate, currentDate)
    println("Detailed age: $detailedAge")
}
```

### Проверка совершеннолетия

```kotlin
fun isAdultK(birthDate: LocalDate, currentDate: LocalDate): Boolean {
    return calculateAgeK(birthDate, currentDate) >= 18
}

fun calculateAgeInDaysK(birthDate: LocalDate, currentDate: LocalDate): Long {
    return ChronoUnit.DAYS.between(birthDate, currentDate)
}

fun calculateAgeInMonthsK(birthDate: LocalDate, currentDate: LocalDate): Long {
    return ChronoUnit.MONTHS.between(birthDate, currentDate)
}
```

## Сравнение подходов

| Подход | Простота | Точность | Производительность | Когда использовать |
|--------|----------|----------|-------------------|-------------------|
| `Java` 8 `Period` | Высокая | Высокая | Высокая | `Java` 8+ |
| `Joda-Time` | Высокая | Высокая | Высокая | `Java` 7 и ниже |
| `Java` 7 `Calendar` | Средняя | Высокая | Средняя | `Java` 7 без библиотек |
| `Java` 7 `DateFormat` | Низкая | Низкая | Низкая | Не рекомендуется |

## Сложность

Все подходы имеют константную временную и пространственную сложность `O(1)`: несколько операций с датами и создание объектов периода.

## Особенности

Наиболее точны и просты в использовании `Period` в `Java` 8 и `Years` в `Joda-Time`. Для старых проектов без `java.time` подходит `Joda-Time` или аккуратная реализация на `Calendar` с учётом месяца и дня.

## Применение

Вычисление возраста нужно при валидации данных и в формах регистрации, в демографических и финансовых приложениях, в медицинских системах и везде, где требуется проверка совершеннолетия или отображение возраста в годах или днях.

## Варианты задачи

### Вариант 1: Проверка совершеннолетия

```java
public boolean isAdult(LocalDate birthDate, LocalDate currentDate) {
    return calculateAge(birthDate, currentDate) >= 18;
}
```

### Вариант 2: Возраст в днях

```java
public long calculateAgeInDays(LocalDate birthDate, LocalDate currentDate) {
    return ChronoUnit.DAYS.between(birthDate, currentDate);
}
```

### Вариант 3: Возраст в месяцах

```java
public long calculateAgeInMonths(LocalDate birthDate, LocalDate currentDate) {
    return ChronoUnit.MONTHS.between(birthDate, currentDate);
}
```

## Когда использовать

Для проектов на `Java` 8+ предпочтительнее `Period.between` и `LocalDate`: просто и точно. Для `Java` 7 или при невозможности перехода на `java.time` подойдёт `Joda-Time` с `Years.yearsBetween`. Чистый `Calendar` в `Java` 7 используют, когда внешние библиотеки недоступны; при этом нужно учитывать месяц и день для корректного возраста.

## Лучшие практики

Для возраста «в годах» достаточно `LocalDate` без времени и таймзоны; при учёте времени суток — `ZonedDateTime` и единая таймзона. Валидируйте входные данные: дата рождения не в будущем, текущая дата не раньше даты рождения; при ошибках — `IllegalArgumentException` или опциональный результат. В тестах покрывайте граничные случаи: день рождения сегодня, високосные годы, разные месяцы. При массовых расчётах можно переиспользовать текущую дату.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Возраст на 1 год больше | Не учтён факт «день рождения в этом году ещё не наступил» | Уменьшать возраст на 1, если текущий день года меньше дня рождения (или сравнить месяц/день) |
| Отрицательный возраст | Дата рождения в будущем или перепутаны аргументы | Валидировать: birthDate ≤ currentDate; при ошибке бросать исключение |
| Неверный возраст для 29 февраля | Разная обработка високосных в Calendar | Использовать `LocalDate`/`Period` или проверять в тестах 29.02 |
| Разные результаты в разных таймзонах | Использование даты-времени с таймзоной | Для возраста в годах использовать только дату без времени (`LocalDate`) |

## Частые вопросы

**Нужно ли учитывать часовой пояс для возраста в годах?** Нет. Возраст в полных годах считается по календарным датам (день рождения и «сегодня»). Достаточно `LocalDate` в одной таймзоне (например, локальная или UTC).

**Чем Period отличается от разницы в днях, делённой на 365?** `Period` считает полные календарные годы с учётом месяцев и дней (например, 1 января 2000 — 31 декабря 2000 это 0 лет по `Period`, но почти 365 дней). Деление дней на 365 даёт приближение и не совпадает с «возрастом в годах».

**Как проверить совершеннолетие?** Вычислить возраст в годах и проверить `age >= 18`. Для `LocalDate`: `Period.between(birthDate, currentDate).getYears() >= 18`.

## Заключение

В документе рассмотрены способы вычисления возраста в `Java`: `Java` 8 с `Period` и `LocalDate` (рекомендуется для новых проектов), `Joda-Time` для `Java` 7 и реализация на `Calendar` без внешних зависимостей.
