---
title: "Вычисление логарифма (Logarithm Calculation)"
description: "В документе описано вычисление логарифмов в Java: десятичный (Math.log10), натуральный (Math.log) и логарифм по произвольному основанию по формуле смены основания."
tags:
  - algorithms
  - math
  - logarithm-calculation
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Вычисление логарифма (Logarithm Calculation)

В документе описано вычисление логарифмов в `Java`: десятичный (`Math.log10`), натуральный (`Math.log`) и логарифм по произвольному основанию по формуле смены основания.

## Полезные ссылки

### Официальная документация
- [Math.log() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#log-double-)
- [Math.log10() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#log10-double-)

### См. также
- [Вычисление факториала](factorial-calculation.md) — факториал
- [Ряд Фибоначчи](fibonacci-sequence.md) — ряд Фибоначчи

- [Пересечение прямых (Line Intersection)](line-intersection.md)
- [Вычисление площади круга (Circle Area Calculation)](circle-area-calculation.md)
- [Взаимно простые числа (Coprime Numbers)](coprime-numbers.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Математическое определение](#математическое-определение)
- [Реализация на Java](#реализация-на-java)
  - [Десятичный логарифм](#десятичный-логарифм)
  - [Обработка особых случаев](#обработка-особых-случаев)
- [Натуральный логарифм](#натуральный-логарифм)
  - [Связь с экспонентой](#связь-с-экспонентой)
- [Логарифм с пользовательским основанием](#логарифм-с-пользовательским-основанием)
  - [Реализация](#реализация)
  - [Альтернативная реализация с использованием log10](#альтернативная-реализация-с-использованием-log10)
  - [Улучшенная версия с проверками](#улучшенная-версия-с-проверками)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Логарифм по основанию 2](#вариант-1-логарифм-по-основанию-2)
  - [Вариант 2: Дискретный логарифм (целочисленный)](#вариант-2-дискретный-логарифм-целочисленный)
  - [Вариант 3: Логарифмическая шкала](#вариант-3-логарифмическая-шкала)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Десятичный и натуральный логарифм](#десятичный-и-натуральный-логарифм)
  - [Натуральный логарифм](#натуральный-логарифм-1)
  - [Логарифм с пользовательским основанием](#логарифм-с-пользовательским-основанием-1)
  - [Логарифм по основанию 2](#логарифм-по-основанию-2)
  - [Пример использования](#пример-использования)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Логарифм по основанию b от x — это степень y, в которую нужно возвести b, чтобы получить x: b^y = x. В `Java` для основания 10 используется `Math.log10()`, для натурального (e) — `Math.log()`.

### Математическое определение

log_b(x) = y, где b^y = x. Здесь b — основание, x — аргумент, y — результат.

## Реализация на Java

### Десятичный логарифм

Десятичный логарифм: основание 10. Для неположительных аргументов логарифм не определён — нужна проверка или исключение.

```java
// Math.log10(100)==2, Math.log10(1000)==3; для x<=0 — исключение
@Test
public void givenLog10_shouldReturnValidResults() {
    assertEquals(Math.log10(100), 2, 0.001);
    assertEquals(Math.log10(1000), 3, 0.001);
    assertEquals(Math.log10(1), 0, 0.001);
}
```

### Обработка особых случаев

Безопасная обёртка: при x ≤ 0 бросаем исключение.

```java
// Проверка аргумента перед вызовом Math.log10
public static double log10Safe(double x) {
    if (x <= 0) {
        throw new IllegalArgumentException("Logarithm is not defined for non-positive numbers");
    }
    return Math.log10(x);
}
```

## Натуральный логарифм

Натуральный логарифм — по основанию e (число Эйлера). В `Java`: `Math.log(x)`.

```java
// Math.log(Math.E)==1; для x<=0 результат -Infinity или NaN
@Test
public void givenNaturalLog_shouldReturnValidResults() {
    assertEquals(Math.log(Math.E), 1, 0.001);
    assertEquals(Math.log(10), 2.30258, 0.001);
}
```

### Связь с экспонентой

`exp(ln(x)) = x` при x > 0 — обратные функции.

## Логарифм с пользовательским основанием

Формула смены основания: log_b(x) = log(x) / log(b). Можно использовать `Math.log` (натуральный) или `Math.log10`.

### Реализация

```java
// log_b(x) = ln(x)/ln(b); основание и аргумент должны быть > 0, base != 1
@Test
public void givenCustomLog_shouldReturnValidResults() {
    assertEquals(customLog(2, 256), 8, 0.001);
    assertEquals(customLog(10, 100), 2, 0.001);
    assertEquals(customLog(3, 81), 4, 0.001);
}

private static double customLog(double base, double logNumber) {
    return Math.log(logNumber) / Math.log(base);
}
```

### Альтернативная реализация с использованием log10

Тот же результат через десятичный логарифм: log_b(x) = log10(x)/log10(b).

```java
private static double customLogWithLog10(double base, double logNumber) {
    return Math.log10(logNumber) / Math.log10(base);
}
```

### Улучшенная версия с проверками

Основание: положительное и не равное 1; аргумент — положительный.

```java
// Валидация base и logNumber перед расчётом
public static double customLogSafe(double base, double logNumber) {
    if (base <= 0 || base == 1) {
        throw new IllegalArgumentException("Base must be positive and not equal to 1");
    }
    if (logNumber <= 0) {
        throw new IllegalArgumentException("Logarithm is not defined for non-positive numbers");
    }
    return Math.log(logNumber) / Math.log(base);
}
```

## Сложность

Все рассмотренные методы работают за константное время O(1) и используют O(1) памяти (результаты аппроксимируются нативными функциями).

## Особенности

Результаты имеют типичные для `double` ошибки округления. Особые случаи: log(0) даёт -∞, log(1)=0; для отрицательных — NaN. `Math.log()` и `Math.log10()` оптимизированы в JVM.

## Применение

Логарифмы применяются в научных расчётах, при анализе сложности алгоритмов (бинарный поиск, деревья), в финансах, обработке сигналов и машинном обучении.

## Варианты задачи

### Вариант 1: Логарифм по основанию 2

log2(x) = ln(x)/ln(2).

```java
public static double log2(double x) {
    return Math.log(x) / Math.log(2);
}
```

### Вариант 2: Дискретный логарифм (целочисленный)

Целочисленный log2 через количество ведущих нулей (для положительного int).

```java
// floor(log2(x)) для int через numberOfLeadingZeros
public static int discreteLog2(int x) {
    if (x <= 0) {
        throw new IllegalArgumentException("x must be positive");
    }
    return 31 - Integer.numberOfLeadingZeros(x);
}
```

### Вариант 3: Логарифмическая шкала

Перевод в логарифмическую шкалу и обратно по заданному основанию.

```java
public static double toLogScale(double value, double base) {
    return Math.log(value) / Math.log(base);
}

public static double fromLogScale(double logValue, double base) {
    return Math.pow(base, logValue);
}
```

## Реализация на Kotlin

### Десятичный и натуральный логарифм

```kotlin
// Обёртки над Math.log10 / Math.log с проверкой аргумента
fun log10K(x: Double): Double {
    return Math.log10(x)
}

fun log10SafeK(x: Double): Double {
    if (x <= 0) {
        throw IllegalArgumentException("Logarithm is not defined for non-positive numbers")
    }
    return Math.log10(x)
}
```

### Натуральный логарифм

```kotlin
fun naturalLogK(x: Double): Double {
    return Math.log(x)
}

fun naturalLogE(): Double {
    return Math.log(Math.E) // 1.0
}
```

### Логарифм с пользовательским основанием

```kotlin
fun customLogK(base: Double, logNumber: Double): Double {
    return Math.log(logNumber) / Math.log(base)
}

fun customLogWithLog10K(base: Double, logNumber: Double): Double {
    return Math.log10(logNumber) / Math.log10(base)
}

fun customLogSafeK(base: Double, logNumber: Double): Double {
    if (base <= 0 || base == 1.0) {
        throw IllegalArgumentException("Base must be positive and not equal to 1")
    }
    if (logNumber <= 0) {
        throw IllegalArgumentException("LogNumber must be positive")
    }
    return Math.log(logNumber) / Math.log(base)
}
```

### Логарифм по основанию 2

```kotlin
fun log2K(x: Double): Double {
    return Math.log(x) / Math.log(2.0)
}

fun log2IntegerK(x: Int): Int {
    if (x <= 0) {
        throw IllegalArgumentException("x must be positive")
    }
    return 31 - Integer.numberOfLeadingZeros(x)
}
```

### Пример использования

```kotlin
fun main() {
    println(Math.log10(100.0)) // 2.0
    println(Math.log10(1000.0)) // 3.0

    println(Math.log(Math.E)) // 1.0
    println(Math.log(10.0)) // ≈ 2.302585

    println(customLogK(2.0, 256.0)) // 8.0
    println(customLogK(10.0, 100.0)) // 2.0

    println(log2K(8.0)) // 3.0
    println(log2IntegerK(8)) // 3
}
```

## Когда использовать

Используйте `Math.log10()` для основания 10 (десятичный логарифм), `Math.log()` для натурального (основание e). Для произвольного основания — формула customLog: log(x)/log(base). Для целочисленного log2 по битам — дискретный вариант через `numberOfLeadingZeros`.

## Лучшие практики

Для неположительных аргументов логарифм не определён: бросайте `IllegalArgumentException` в обёртках или явно документируйте контракт (NaN). Основание должно быть положительным и не равным 1. Для большинства задач достаточно `double`; при необходимости повышенной точности — `BigDecimal` и ряды. В тестах проверяйте 1, 0, отрицательные (ожидание исключения), большие числа.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| NaN или -Infinity | Аргумент ≤ 0 или основание ≤ 0 / = 1 | Валидировать входы, использовать log*Safe или документировать контракт |
| Неточный результат при больших x | Ограниченная точность double | Для критичной точности использовать BigDecimal и разложение в ряд |
| IllegalArgumentException в log10Safe | Передан 0 или отрицательное число | Проверять x > 0 до вызова или обрабатывать исключение |

## Частые вопросы

**Почему log(0) даёт -Infinity?** По математическому пределу при x0+ значение log(x) стремится к -∞; в IEEE 754 это представлено как отрицательная бесконечность.

**Когда использовать log10, а когда log?** log10 — для десятичных логарифмов (порядки величин, pH, децибелы); log — для натурального (теория, производные, ряды). Для произвольного основания оба дают один результат по формуле смены основания.

**Как получить целочисленный log2?** Для положительного int: 31 — Integer.numberOfLeadingZeros(x) даёт floor(log2(x)); для long — 63 — Long.numberOfLeadingZeros(x).

## Заключение

В документе описано вычисление логарифмов в `Java`: десятичный (`Math.log10`), натуральный (`Math.log`) и по произвольному основанию (формула log(x)/log(base)). Для безопасного использования добавлены проверки аргументов; рассмотрены варианты log2 и дискретный целочисленный log2.
