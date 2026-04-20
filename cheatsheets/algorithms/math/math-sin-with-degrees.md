---
title: "Синус в градусах (Math.sin with Degrees)"
description: "В Java тригонометрические функции Math работают в радианах. В документе описаны преобразование градусов в радианы (Math.toRadians), обратное (Math.toDegrees) и вычисление синуса (и других функций) от угла в градусах."
tags:
  - algorithms
  - math
  - math-sin-with-degrees
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Синус в градусах (Math.sin with Degrees)

В `Java` тригонометрические функции `Math` работают в радианах. В документе описаны преобразование градусов в радианы (`Math.toRadians`), обратное (`Math.toDegrees`) и вычисление синуса (и других функций) от угла в градусах.

## Полезные ссылки

### Официальная документация
- [Math.sin() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#sin-double-)
- [Math.toRadians() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#toRadians-double-)

### См. также
- [Вычисление площади круга](circle-area-calculation.md) — площадь круга
- [Вычисление расстояния](distance-between-points.md) — расстояние между точками

- [Пересечение прямых (Line Intersection)](line-intersection.md)
- [Взаимно простые числа (Coprime Numbers)](coprime-numbers.md)
- [Преобразование широты и долготы в 2D-точку (Lat/Lon to 2D Point)](lat-lon-to-2d-point.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Радианы и градусы](#радианы-и-градусы)
- [Реализация на Java](#реализация-на-java)
  - [Преобразование градусов в радианы](#преобразование-градусов-в-радианы)
  - [Преобразование радиан в градусы](#преобразование-радиан-в-градусы)
- [Вычисление синуса](#вычисление-синуса)
  - [Примеры вычисления синуса](#примеры-вычисления-синуса)
- [Другие тригонометрические функции](#другие-тригонометрические-функции)
  - [Косинус](#косинус)
  - [Тангенс](#тангенс)
  - [Обратные функции](#обратные-функции)
  - [Примеры использования](#примеры-использования)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Вычисление всех тригонометрических функций](#вариант-1-вычисление-всех-тригонометрических-функций)
  - [Вариант 2: Преобразование углов](#вариант-2-преобразование-углов)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Вычисление синуса](#вычисление-синуса-1)
  - [Другие тригонометрические функции](#другие-тригонометрические-функции-1)
  - [Преобразование и нормализация углов](#преобразование-и-нормализация-углов)
  - [Пример использования](#пример-использования)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

В `Java` методы `Math.sin`, `Math.cos`, `Math.tan` принимают угол в радианах. Формулы: радианы = градусы × π/180; градусы = радианы × 180/π. В API есть `Math.toRadians(degrees)` и `Math.toDegrees(radians)`.

### Радианы и градусы

Примеры: 0° = 0 рад, 90° = π/2 ≈ 1.5708, 180° = π ≈ 3.1416, 360° = 2π ≈ 6.2832.

```java
// Преобразование градусов ↔ радианы
double inRadians = Math.toRadians(inDegrees);
double inDegrees = Math.toDegrees(inRadians);
```

## Реализация на Java

### Преобразование градусов в радианы

Сигнатура: `Math.sin(double a)` — аргумент в радианах. Перед вызовом переводим градусы в радианы через `Math.toRadians`.

```java
// Примеры: 30°→≈0.5236, 45°→≈0.7854, 90°→≈1.5708
double degrees30 = 30;
double radians30 = Math.toRadians(degrees30);  // ≈ 0.5236

double degrees45 = 45;
double radians45 = Math.toRadians(degrees45);  // ≈ 0.7854

double degrees90 = 90;
double radians90 = Math.toRadians(degrees90);  // ≈ 1.5708
```

### Преобразование радиан в градусы

```java
// Преобразование радиан в градусы
double piRadians = Math.PI;
double degrees = Math.toDegrees(piRadians);  // 180.0

double halfPi = Math.PI / 2;
double degrees90 = Math.toDegrees(halfPi);  // 90.0
```

## Вычисление синуса

Угол в градусах `Math.toRadians(degrees)` `Math.sin(radians)`. Если угол уже в радианах, вызываем `Math.sin` напрямую.

```java
// Проверка: sin(30°) через toRadians и через явные радианы дают один результат
@Test
public void givenAnAngleInDegrees_whenUsingToRadians_thenResultIsInRadians() {
    double angleInDegrees = 30;
    double sinForDegrees = Math.sin(Math.toRadians(angleInDegrees));

    double thirtyDegreesInRadians = 1.0/6 * Math.PI;
    double sinForRadians = Math.sin(thirtyDegreesInRadians);

    assertTrue(sinForDegrees == sinForRadians);
}
```

### Примеры вычисления синуса

```java
// sin(0°)=0, sin(30°)=0.5, sin(45°)≈0.707, sin(90°)=1, sin(180°)=0
double sin0 = Math.sin(Math.toRadians(0));      // 0.0

// Синус 30 градусов
double sin30 = Math.sin(Math.toRadians(30));    // 0.5

// Синус 45 градусов
double sin45 = Math.sin(Math.toRadians(45));    // ≈ 0.7071

// Синус 90 градусов
double sin90 = Math.sin(Math.toRadians(90));    // 1.0

// Синус 180 градусов
double sin180 = Math.sin(Math.toRadians(180));  // 0.0
```

Вспомогательный метод: синус от градусов в одну строку.

```java
// Удобная обёртка: градусы → радианы → sin
public static double sinDegrees(double degrees) {
    return Math.sin(Math.toRadians(degrees));
}
```

## Другие тригонометрические функции

### Косинус

```java
public static double cosDegrees(double degrees) {
    return Math.cos(Math.toRadians(degrees));
}
```

### Тангенс

```java
public static double tanDegrees(double degrees) {
    return Math.tan(Math.toRadians(degrees));
}
```

### Обратные функции

```java
// Арксинус (возвращает радианы)
public static double asinDegrees(double value) {
    return Math.toDegrees(Math.asin(value));
}

// Арккосинус (возвращает радианы)
public static double acosDegrees(double value) {
    return Math.toDegrees(Math.acos(value));
}

// Арктангенс (возвращает радианы)
public static double atanDegrees(double value) {
    return Math.toDegrees(Math.atan(value));
}
```

### Примеры использования

```java
// Косинус 60 градусов
double cos60 = Math.cos(Math.toRadians(60));    // 0.5

// Тангенс 45 градусов
double tan45 = Math.tan(Math.toRadians(45));    // 1.0

// Арксинус 0.5 (в градусах)
double asin05 = Math.toDegrees(Math.asin(0.5)); // 30.0
```

## Сложность

Все операции O(1) по времени и памяти: `Math.sin`/`cos`/`tan` — нативная аппроксимация, `toRadians`/`toDegrees` — умножение на константу.

## Особенности

Возможны небольшие ошибки округления при работе с `double`. `Math.sin` принимает любой аргумент (в том числе отрицательный и больше 2π). Функции оптимизированы в JVM.

## Применение

Тригонометрия в градусах используется в графике, физике, играх, навигации и обработке сигналов.

## Варианты задачи

### Вариант 1: Вычисление всех тригонометрических функций

sin, cos, tan и обратные (sec, csc, cot) от угла в градусах.

```java
// Все функции от градусов через toRadians
public class Trigonometry {
    public static double sinDegrees(double degrees) {
        return Math.sin(Math.toRadians(degrees));
    }

    public static double cosDegrees(double degrees) {
        return Math.cos(Math.toRadians(degrees));
    }

    public static double tanDegrees(double degrees) {
        return Math.tan(Math.toRadians(degrees));
    }

    public static double cotDegrees(double degrees) {
        return 1.0 / tanDegrees(degrees);
    }

    public static double secDegrees(double degrees) {
        return 1.0 / cosDegrees(degrees);
    }

    public static double cscDegrees(double degrees) {
        return 1.0 / sinDegrees(degrees);
    }
}
```

### Вариант 2: Преобразование углов

Конвертер градусы радианы и нормализация угла в [0, 360).

```java
public class AngleConverter {
    public static double degreesToRadians(double degrees) {
        return Math.toRadians(degrees);
    }

    public static double radiansToDegrees(double radians) {
        return Math.toDegrees(radians);
    }

    public static double normalizeAngle(double degrees) {
        degrees = degrees % 360;
        if (degrees < 0) {
            degrees += 360;
        }
        return degrees;
    }
}
```

## Реализация на Kotlin

```kotlin
// toRadians / toDegrees и sin от градусов
fun degreesToRadiansK(degrees: Double): Double {
    return Math.toRadians(degrees)
}

fun radiansToDegreesK(radians: Double): Double {
    return Math.toDegrees(radians)
}

// Примеры преобразования
fun main() {
    val degrees30 = 30.0
    val radians30 = Math.toRadians(degrees30) // ≈ 0.5236

    val piRadians = Math.PI
    val degrees = Math.toDegrees(piRadians) // 180.0
}
```

### Вычисление синуса

```kotlin
fun sinDegreesK(degrees: Double): Double {
    return Math.sin(Math.toRadians(degrees))
}

// Примеры вычисления синуса
fun sinExamplesK() {
    val sin0 = Math.sin(Math.toRadians(0.0))      // 0.0
    val sin30 = Math.sin(Math.toRadians(30.0))    // 0.5
    val sin45 = Math.sin(Math.toRadians(45.0))    // ≈ 0.7071
    val sin90 = Math.sin(Math.toRadians(90.0))    // 1.0
    val sin180 = Math.sin(Math.toRadians(180.0))  // 0.0
}
```

### Другие тригонометрические функции

```kotlin
object TrigonometryK {
    fun sinDegrees(degrees: Double): Double {
        return Math.sin(Math.toRadians(degrees))
    }

    fun cosDegrees(degrees: Double): Double {
        return Math.cos(Math.toRadians(degrees))
    }

    fun tanDegrees(degrees: Double): Double {
        return Math.tan(Math.toRadians(degrees))
    }

    fun cotDegrees(degrees: Double): Double {
        return 1.0 / tanDegrees(degrees)
    }

    fun secDegrees(degrees: Double): Double {
        return 1.0 / cosDegrees(degrees)
    }

    fun cscDegrees(degrees: Double): Double {
        return 1.0 / sinDegrees(degrees)
    }
}
```

### Преобразование и нормализация углов

```kotlin
object AngleConverterK {
    fun degreesToRadians(degrees: Double): Double {
        return Math.toRadians(degrees)
    }

    fun radiansToDegrees(radians: Double): Double {
        return Math.toDegrees(radians)
    }

    fun normalizeAngle(degrees: Double): Double {
        var angle = degrees % 360
        if (angle < 0) {
            angle += 360
        }
        return angle
    }
}
```

### Пример использования

```kotlin
fun main() {
    val angle = 30.0
    val sinValue = TrigonometryK.sinDegrees(angle)
    println("sin($angle°) = $sinValue") // sin(30.0°) = 0.5

    val normalized = AngleConverterK.normalizeAngle(-45.0)
    println("Normalized angle: $normalized") // 315.0
}
```

## Когда использовать

Используйте `Math.toRadians()` когда угол задан в градусах и нужно вызвать `Math.sin`/`cos`/`tan`. Если угол уже в радианах (например, из формул с π), вызывайте тригонометрические функции напрямую.

## Лучшие практики

Явно указывайте в именах или комментариях единицы угла (градусы или радианы). Используйте `Math.PI` и `Math.toRadians(180)` вместо магических чисел. При пользовательском вводе нормализуйте угол в [0, 360) или [-180, 180). Для сравнения результатов используйте допуск (epsilon), а не проверку на равенство. В горячем цикле конвертируйте градусы в радианы один раз при вводе.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Неверное значение sin/cos | Угол передан в градусах, а метод ждёт радианы | Обернуть в Math.toRadians(degrees) перед вызовом |
| assertEqual не срабатывает | Сравнение double без допуска | Сравнивать с epsilon: Math.abs(a — b) < 1e-9 |
| Угол вне [0,360) | Не нормализован пользовательский ввод | Привести: angle = angle % 360; if (angle < 0) angle += 360 |

## Частые вопросы

**Почему в Java углы в радианах?** Математические библиотеки и анализ обычно оперируют радианами; градусы конвертируются одной операцией умножения.

**Как получить sin(90°) без toRadians?** Можно вызвать Math.sin(Math.PI / 2), но для читаемости лучше sinDegrees(90) с обёрткой через toRadians.

**Нужно ли нормализовать угол перед sin?** Math.sin принимает любой double (в том числе отрицательный и больше 2π), но для логики приложения часто удобно привести угол к [0, 360) или [-180, 180).

## Заключение

В документе описаны преобразование градусов и радиан в `Java` (`Math.toRadians`, `Math.toDegrees`) и вычисление синуса (и других тригонометрических функций) от угла в градусах. Важно явно учитывать единицы измерения при вызове методов `Math`.
