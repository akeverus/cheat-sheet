---
title: "Расчёт високосного года (Leap Year Calculation)"
description: "В документе описано определение високосных лет в Java с использованием разных подходов: GregorianCalendar, Java 8 Date-Time API и Joda-Time. Приводятся правила григорианского календаря и примеры на Java и Kotlin."
tags:
  - algorithms
  - problems
  - leap-year-calculation
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Расчёт високосного года (`Leap Year Calculation`)

В документе описано определение високосных лет в `Java` с использованием разных подходов: `GregorianCalendar`, `Java` 8 Date-Time API и `Joda-Time`. Приводятся правила григорианского календаря и примеры на `Java` и `Kotlin`.

## Полезные ссылки

### Официальная документация
- [Java GregorianCalendar Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/GregorianCalendar.html)
- [Java Year Documentation](https://docs.oracle.com/javase/8/docs/api/java/time/Year.html)

### См. также
- [[age-calculation|Вычисление возраста]] — вычисление возраста
- [[distance-between-points|Расстояние между точками]] — вычисление расстояния

## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Правила определения високосного года](#правила-определения-високосного-года)
- [Реализация на Java](#реализация-на-java)
  - [Правила високосных лет](#правила-високосных-лет)
    - [Примеры](#примеры)
  - [Подход 1: GregorianCalendar (Java)](#подход-1-gregoriancalendar-java)
  - [Пример использования (GregorianCalendar)](#пример-использования-gregoriancalendar)
  - [Подход 2: Java 8 Date-Time API (Java)](#подход-2-java-8-date-time-api-java)
  - [Пример использования (Date-Time API)](#пример-использования-date-time-api)
  - [Подход 3: Joda-Time](#подход-3-joda-time)
  - [Пример использования (Joda-Time)](#пример-использования-joda-time)
  - [Подход 4: Собственная реализация](#подход-4-собственная-реализация)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Подход 3: Собственная реализация](#подход-3-собственная-реализация)
  - [Подход 4: Использование LocalDate](#подход-4-использование-localdate)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)
  - [Временная сложность](#временная-сложность)
  - [Пространственная сложность](#пространственная-сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Подсчет високосных лет в диапазоне](#вариант-1-подсчет-високосных-лет-в-диапазоне)
  - [Вариант 2: Список високосных лет в диапазоне](#вариант-2-список-високосных-лет-в-диапазоне)
  - [Вариант 3: Проверка с использованием LocalDate](#вариант-3-проверка-с-использованием-localdate)
- [Когда использовать](#когда-использовать)
  - [Используйте GregorianCalendar, когда:](#используйте-gregoriancalendar-когда)
  - [Используйте Java 8 Date-Time API, когда:](#используйте-java-8-date-time-api-когда)
  - [Используйте Joda-Time, когда:](#используйте-joda-time-когда)
  - [Используйте собственную реализацию, когда:](#используйте-собственную-реализацию-когда)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

В этом руководстве показано несколько способов определить, является ли год високосным в `Java`. Високосный год в григорианском календаре — это год, который делится на 4 без остатка, с двумя исключениями: год, делящийся на 100, не считается високосным, если только он не делится также на 400. Поэтому 2000 и 2004 — високосные, 1900 — нет, 2001 — нет.

### Правила определения високосного года

Год считается високосным, если выполняется одно из условий: он делится на 4 и не делится на 100, либо делится на 400. Иначе говоря, если год делится на 100, он должен делиться на 400, чтобы быть високосным. Это полная формулировка правил григорианского календаря.

## Реализация на Java

### Правила високосных лет

#### Примеры

2000 — високосный (делится на 400). 2004 — високосный (делится на 4, но не на 100). 1900 — не високосный (делится на 100, но не на 400). 2001 — не високосный (не делится на 4).

### Подход 1: GregorianCalendar (Java)

Начиная с `Java` 1.1 класс `GregorianCalendar` позволяет проверять, является ли год високосным, без ручной реализации правил.

```java
// Проверка високосного года по правилам григорианского календаря (GregorianCalendar)
import java.util.GregorianCalendar;

public class LeapYearChecker {
    public static boolean isLeapYear(int year) {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.isLeapYear(year);
    }
}
```

Метод возвращает `true`, если год високосный, и `false` в противном случае. Годы до нашей эры в `GregorianCalendar` задаются отрицательными числами по формуле 1 − год (например, 3 год до н.э. задаётся как −2).

### Пример использования (GregorianCalendar)

```java
// Проверка ключевых годов: 2000 и 2004 — високосные, 1900 и 2001 — нет
System.out.println(isLeapYear(2000)); // true
System.out.println(isLeapYear(2004)); // true
System.out.println(isLeapYear(1900)); // false
System.out.println(isLeapYear(2001)); // false
```

### Подход 2: Java 8 Date-Time API (Java)

В `Java` 8 появился пакет `java.time` с удобным API для даты и времени. Класс `Year` из `java.time` предоставляет статический метод проверки високосного года.

```java
// Статический метод Year.isLeap и метод экземпляра isLeap()
import java.time.Year;

public class LeapYearCheckerJava8 {
    public static boolean isLeapYear(long year) {
        return Year.isLeap(year);
    }

    public static boolean isLeapYearInstance(int year) {
        Year yearObj = Year.of(year);
        return yearObj.isLeap();
    }
}
```

Проверку можно выполнить и через экземпляр: `Year.of(year).isLeap()`.

```java
// Проверка через экземпляр Year
Year year = Year.of(2000);
boolean isLeap = year.isLeap(); // true
```

### Пример использования (Date-Time API)

```java
System.out.println(Year.isLeap(2000)); // true
System.out.println(Year.isLeap(2004)); // true
System.out.println(Year.isLeap(1900)); // false
```

### Подход 3: Joda-Time

`Joda-Time` — популярная сторонняя библиотека для работы с датой и временем в `Java`. С появлением `Java` 8 она переведена в режим поддержки; для новых проектов предпочтительнее `java.time`. Готового метода «является ли год високосным» в `Joda-Time` нет, но проверку можно выполнить через `LocalDate` и `Days`: по числу дней между 31 января данного года и следующего (в високосном году их 366).

```java
// Число дней между 31.01 года N и 31.01 года N+1: 366 только для високосного
import org.joda.time.LocalDate;
import org.joda.time.Days;

public class LeapYearCheckerJoda {
    public static boolean isLeapYear(int year) {
        LocalDate localDate = new LocalDate(year, 1, 31);
        int numberOfDays = Days.daysBetween(
            localDate,
            localDate.plusYears(1)
        ).getDays();
        return numberOfDays > 365;
    }
}
```

### Пример использования (Joda-Time)

```java
System.out.println(isLeapYear(2000)); // true
System.out.println(isLeapYear(2004)); // true
System.out.println(isLeapYear(1900)); // false
```

### Подход 4: Собственная реализация

Логику можно реализовать вручную по правилам: сначала проверка делимости на 4, затем исключение для 100 и исключение из исключения для 400.

```java
// Собственная реализация по правилам григорианского календаря
public class LeapYearCheckerCustom {
    public static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 100 != 0) {
            return true;
        } else {
            return year % 400 == 0;
        }
    }
}
```

Компактный вариант в одно выражение:

```java
// Одной строкой: (делится на 4 и не на 100) или делится на 400
public static boolean isLeapYearCompact(int year) {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
}
```

## Реализация на Kotlin

### Подход 1: GregorianCalendar (Kotlin)

```kotlin
// Проверка через GregorianCalendar (аналог Java-подхода)
import java.util.GregorianCalendar

object LeapYearCheckerK {
    fun isLeapYear(year: Int): Boolean {
        val calendar = GregorianCalendar()
        return calendar.isLeapYear(year)
    }
}
```

### Подход 2: Java 8 Date-Time API (Kotlin)

```kotlin
// Использование Year из java.time
import java.time.Year

object LeapYearCheckerJava8K {
    fun isLeapYear(year: Long): Boolean = Year.isLeap(year)

    fun isLeapYearInstance(year: Int): Boolean {
        val yearObj = Year.of(year)
        return yearObj.isLeap
    }
}
```

### Подход 3: Собственная реализация

```kotlin
// Та же логика: делимость на 4/100/400
object LeapYearCheckerCustomK {
    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}
```

### Подход 4: Использование LocalDate

```kotlin
// Проверка: если 29 февраля для данного года допустимо — год високосный
import java.time.LocalDate

fun isLeapYearLocalDateK(year: Int): Boolean {
    return try {
        val date = LocalDate.of(year, 2, 29)
        date.dayOfMonth == 29
    } catch (e: Exception) {
        false
    }
}
```

### Пример использования (Kotlin)

```kotlin
fun main() {
    println(LeapYearCheckerK.isLeapYear(2000)) // true
    println(LeapYearCheckerK.isLeapYear(2004)) // true
    println(LeapYearCheckerK.isLeapYear(1900)) // false
    println(LeapYearCheckerK.isLeapYear(2001)) // false

    println(Year.isLeap(2000)) // true
    println(LeapYearCheckerCustomK.isLeapYear(2000)) // true
}
```

## Сравнение подходов

| Подход | Простота | Производительность | Зависимости | Рекомендуется |
|--------|----------|-------------------|-------------|---------------|
| `GregorianCalendar` | Средняя | Средняя | Нет | Для старых версий `Java` |
| `Java` 8 Date-Time | Высокая | Высокая | Нет | Рекомендуется для новых проектов |
| `Joda-Time` | Средняя | Средняя | Да | Если проект уже использует `Joda-Time` |
| Собственная реализация | Высокая | Высокая | Нет | Простые сценарии, минимум зависимостей |

## Сложность

Все описанные подходы имеют константную временную сложность `O(1)` и константное потребление памяти `O(1)`: проверка сводится к нескольким арифметическим операциям или вызову одного метода API.

## Особенности

Подходы просты в использовании, выполняются за константное время и дают одинаковый результат по правилам григорианского календаря. Выбор между ними определяется версией `Java`, наличием зависимостей и требованиями к производительности.

## Применение

Определение високосного года нужно в календарных приложениях, при вычислении и валидации дат, в финансовых и научных расчётах, а также везде, где требуется корректная работа с количеством дней в году или с датами 29 февраля.

## Варианты задачи

### Вариант 1: Подсчет високосных лет в диапазоне

```java
public static int countLeapYears(int startYear, int endYear) {
    int count = 0;
    for (int year = startYear; year <= endYear; year++) {
        if (isLeapYear(year)) {
            count++;
        }
    }
    return count;
}
```

### Вариант 2: Список високосных лет в диапазоне

```java
public static List<Integer> getLeapYears(int startYear, int endYear) {
    return IntStream.rangeClosed(startYear, endYear)
        .filter(LeapYearChecker::isLeapYear)
        .boxed()
        .collect(Collectors.toList());
}
```

### Вариант 3: Проверка с использованием LocalDate

```java
import java.time.LocalDate;

public static boolean isLeapYearLocalDate(int year) {
    try {
        LocalDate date = LocalDate.of(year, 2, 29);
        return date.getDayOfMonth() == 29;
    } catch (Exception e) {
        return false;
    }
}
```

## Когда использовать

Использовать `GregorianCalendar` имеет смысл при работе с `Java` до 8 или при необходимости совместимости со старым кодом, уже использующим этот класс. Для проектов на `Java` 8+ предпочтительнее Date-Time API и метод `Year.isLeap(year)`: он современный, без лишних зависимостей и рекомендуется для новых разработок. `Joda-Time` уместен, если проект уже опирается на эту библиотеку и нужны её дополнительные возможности. Собственная реализация (делимость на 4, 100, 400) подходит, когда важны простота и отсутствие зависимостей или максимальная производительность в горячем коде.

## Лучшие практики

Правила високосного года стоит держать в одном месте: год високосный, если делится на 4, с исключением для делящихся на 100 (они не високосные), и исключением из исключения для делящихся на 400 (они високосные). В коде для `Java` 8+ лучше использовать `Year.isLeap(year)`; для старых проектов — `GregorianCalendar.isLeapYear(year)`, не дублируя логику. Годы до н.э. в `GregorianCalendar` задаются отрицательными (1 − year); в `Year` тоже допустимы отрицательные значения — при необходимости стоит свериться с документацией. В тестах нужно покрыть примеры 2000 (да), 2004 (да), 1900 (нет), 2001 (нет), граничные значения и при необходимости отрицательные годы. Там, где критична производительность, собственная проверка по остаткам от деления быстрее создания календарных объектов.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| 1900 считается високосным | Используется только проверка «делится на 4» | Учесть исключения: деление на 100 и 400 |
| Исключение при `LocalDate.of(year, 2, 29)` | Год невисокосный, 29 февраля недопустимо | Обрабатывать исключение и возвращать `false` |
| Неверный результат для годов до н.э. | Неверное представление года до н.э. | В `GregorianCalendar` использовать 1 − year (например, −2 для 3 года до н.э.) |
| Нет класса `Year` | Версия `Java` ниже 8 | Подключить `java.time` через desugar или использовать `GregorianCalendar` / собственную реализацию |

## Частые вопросы

**Почему 1900 год не високосный?** По григорианскому календарю год, делящийся на 100, не считается високосным, если не делится также на 400. 1900 делится на 100, но не на 400, поэтому не високосный.

**Какой способ выбрать для нового проекта на Java 8+?** Рекомендуется `Year.isLeap(year)` из `java.time`: он стандартный, без зависимостей и явно выражает намерение.

**Нужно ли учитывать годы до н.э.?** Зависит от задачи. В `GregorianCalendar` и `Year` поддерживаются отрицательные годы; при собственной реализации при необходимости добавьте обработку по той же формуле, что и в API.

**Чем собственная реализация лучше вызова API?** Меньше выделений объектов и вызовов — удобно в горячих циклах при массовой проверке лет. Для разовых проверок разница обычно несущественна.

## Заключение

В документе рассмотрены способы определения високосного года в `Java`: `GregorianCalendar`, `Java` 8 Date-Time API (`Year`), `Joda-Time` и собственная реализация по правилам григорианского календаря. Для новых проектов предпочтительнее `Year.isLeap(year)` из `java.time`.
