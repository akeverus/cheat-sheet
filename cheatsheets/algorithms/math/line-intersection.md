---
title: "Пересечение прямых (Line Intersection)"
description: "Поиск точки пересечения двух прямых, заданных в форме y = mx + b. В документе описаны расчёт по наклону и смещению, обработка параллельных линий, пересечение отрезков и варианты на Java и Kotlin."
tags:
  - algorithms
  - math
  - line-intersection
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Пересечение прямых (`Line Intersection`)

Поиск точки пересечения двух прямых, заданных в форме y = mx + b. В документе описаны расчёт по наклону и смещению, обработка параллельных линий, пересечение отрезков и варианты на `Java` и Kotlin.

## Полезные ссылки

### Официальная документация
- [Optional (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)
- [Point (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/awt/Point.html)

### См. также
- [[rectangle-overlap|Проверка перекрытия двух прямоугольников]] — перекрытие прямоугольников
- [[distance-between-points|Вычисление расстояния]] — расстояние между точками

## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Форма уравнения прямой](#форма-уравнения-прямой)
  - [Примеры](#примеры)
- [Математическая формулировка](#математическая-формулировка)
- [Реализация на Java](#реализация-на-java)
- [Обработка параллельных линий](#обработка-параллельных-линий)
- [Варианты задачи](#варианты-задачи)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

В этом кратком руководстве мы покажем, как найти точку пересечения двух линий, определенных линейными функциями в форме пересечения наклона.

### Форма уравнения прямой

Прямая (кроме вертикальной): y = mx + b, где m — наклон (slope), b — смещение по оси y (y-intercept). Вертикальная линия не задаётся этой формой. Параллельные прямые имеют одинаковый m.

Примеры: y = x − 1 (m=1, b=−1), y = 0 (горизонталь), y = 2x + 3.

## Математическая формулировка

Из m1x + b1 = m2x + b2 получаем x = (b2 − b1)/(m1 − m2). При m1 = m2 знаменатель нулевой — прямые параллельны. y = m1*x + b1 (или m2*x + b2).

## Реализация на Java

Точка пересечения по (m1,b1) и (m2,b2). При m1 == m2 возвращаем `Optional.empty()`.

```java
// x = (b2-b1)/(m1-m2); y = m1*x+b1; при параллельных — empty
import java.awt.Point;
import java.util.Optional;

public Optional<Point> calculateIntersectionPoint(
    double m1, double b1,
    double m2, double b2
) {
    if (m1 == m2) {
        return Optional.empty();
    }

    double x = (b2 - b1) / (m1 - m2);
    double y = m1 * x + b1;

    Point point = new Point();
    point.setLocation(x, y);

    return Optional.of(point);
}
```

Пример: y = 0 и y = x − 1 пересекаются в (1, 0).

```java
@Test
public void givenNotParallelLines_whenCalculatePoint_thenPresent() {
    double m1 = 0;
    double b1 = 0;
    double m2 = 1;
    double b2 = -1;

    Optional<Point> point = calculateIntersectionPoint(m1, b1, m2, b2);

    assertTrue(point.isPresent());
    assertEquals(1.0, point.get().getX(), 0.001);
    assertEquals(0.0, point.get().getY(), 0.001);
}
```

## Обработка параллельных линий

При m1 = m2 прямые параллельны — пересечения нет. Для сравнения double используйте epsilon.

```java
@Test
public void givenParallelLines_whenCalculatePoint_thenEmpty() {
    double m1 = 1;
    double b1 = 0;
    double m2 = 1;
    double b2 = -1;

    Optional<Point> point = calculateIntersectionPoint(m1, b1, m2, b2);

    assertFalse(point.isPresent());
}
```

### Улучшенная проверка параллельности

Сравнение наклонов с допуском epsilon; при совпадении прямых (b1≈b2) можно вернуть любую точку на прямой.

```java
// Проверка |m1-m2| < epsilon; при совпадении линий — одна точка
public Optional<Point> calculateIntersectionPointImproved(
    double m1, double b1,
    double m2, double b2
) {
    // Проверка на параллельность с учетом погрешности
    double epsilon = 1e-10;
    if (Math.abs(m1 - m2) < epsilon) {
        // Если линии параллельны, проверяем, совпадают ли они
        if (Math.abs(b1 - b2) < epsilon) {
            // Линии совпадают - можно вернуть любую точку
            return Optional.of(new Point(0, (int)b1));
        }
        return Optional.empty();
    }

    double x = (b2 - b1) / (m1 - m2);
    double y = m1 * x + b1;

    return Optional.of(new Point((int)Math.round(x), (int)Math.round(y)));
}
```

## Варианты задачи

### Вариант 1: Пересечение через две точки

По двум точкам прямой: m = (y2−y1)/(x2−x1), b = y1 − m*x1. Вертикальная прямая (x2=x1) обрабатывается отдельно.

```java
public Optional<Point> calculateIntersectionFromPoints(
    Point p1, Point p2,  // Первая линия
    Point p3, Point p4   // Вторая линия
) {
    // Вычисляем наклон и пересечение для первой линии
    double m1 = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
    double b1 = p1.getY() - m1 * p1.getX();

    // Вычисляем наклон и пересечение для второй линии
    double m2 = (p4.getY() - p3.getY()) / (p4.getX() - p3.getX());
    double b2 = p3.getY() - m2 * p3.getX();

    return calculateIntersectionPoint(m1, b1, m2, b2);
}
```

### Вариант 2: Пересечение отрезков

Сначала точка пересечения прямых; затем проверка, что точка лежит на обоих отрезках.

```java
public Optional<Point> calculateSegmentIntersection(
    Point p1, Point p2,  // Первый отрезок
    Point p3, Point p4   // Второй отрезок
) {
    Optional<Point> intersection = calculateIntersectionFromPoints(p1, p2, p3, p4);

    if (!intersection.isPresent()) {
        return Optional.empty();
    }

    Point point = intersection.get();

    // Проверяем, находится ли точка пересечения в пределах обоих отрезков
    boolean onSegment1 = isPointOnSegment(point, p1, p2);
    boolean onSegment2 = isPointOnSegment(point, p3, p4);

    if (onSegment1 && onSegment2) {
        return Optional.of(point);
    }

    return Optional.empty();
}

private boolean isPointOnSegment(Point point, Point segmentStart, Point segmentEnd) {
    double minX = Math.min(segmentStart.getX(), segmentEnd.getX());
    double maxX = Math.max(segmentStart.getX(), segmentEnd.getX());
    double minY = Math.min(segmentStart.getY(), segmentEnd.getY());
    double maxY = Math.max(segmentStart.getY(), segmentEnd.getY());

    return point.getX() >= minX && point.getX() <= maxX
        && point.getY() >= minY && point.getY() <= maxY;
}
```

### Вариант 3: Пересечение вертикальной и горизонтальной линий

Вертикаль x = a и горизонталь y = b пересекаются в (a, b).

```java
public Optional<Point> calculateVerticalHorizontalIntersection(
    double verticalX,      // x-координата вертикальной линии
    double horizontalY     // y-координата горизонтальной линии
) {
    Point point = new Point((int)verticalX, (int)horizontalY);
    return Optional.of(point);
}
```

## Реализация на Kotlin

```kotlin
// Точка пересечения; при параллельных — null
import java.awt.Point

fun calculateIntersectionPointK(
    m1: Double, b1: Double,
    m2: Double, b2: Double
): Point? {
    if (m1 == m2) {
        return null
    }

    val x = (b2 - b1) / (m1 - m2)
    val y = m1 * x + b1

    return Point(x.toInt(), y.toInt())
}
```

### Улучшенная версия с epsilon

```kotlin
fun calculateIntersectionPointImprovedK(
    m1: Double, b1: Double,
    m2: Double, b2: Double
): Point? {
    val epsilon = 1e-10

    if (Math.abs(m1 - m2) < epsilon) {
        // Если линии параллельны, проверяем, совпадают ли они
        if (Math.abs(b1 - b2) < epsilon) {
            // Линии совпадают - можно вернуть любую точку
            return Point(0, b1.toInt())
        }
        return null
    }

    val x = (b2 - b1) / (m1 - m2)
    val y = m1 * x + b1

    return Point(Math.round(x).toInt(), Math.round(y).toInt())
}
```

### Пересечение через две точки

```kotlin
data class PointK(val x: Double, val y: Double)

fun calculateIntersectionFromPointsK(
    p1: PointK, p2: PointK,
    p3: PointK, p4: PointK
): PointK? {
    val m1 = if (p2.x != p1.x) (p2.y - p1.y) / (p2.x - p1.x) else Double.POSITIVE_INFINITY
    val b1 = if (m1 != Double.POSITIVE_INFINITY) p1.y - m1 * p1.x else Double.NaN

    val m2 = if (p4.x != p3.x) (p4.y - p3.y) / (p4.x - p3.x) else Double.POSITIVE_INFINITY
    val b2 = if (m2 != Double.POSITIVE_INFINITY) p3.y - m2 * p3.x else Double.NaN

    // Обработка вертикальных линий
    if (m1 == Double.POSITIVE_INFINITY && m2 == Double.POSITIVE_INFINITY) {
        return null // Обе линии вертикальны
    }

    if (m1 == Double.POSITIVE_INFINITY) {
        val x = p1.x
        val y = m2 * x + b2
        return PointK(x, y)
    }

    if (m2 == Double.POSITIVE_INFINITY) {
        val x = p3.x
        val y = m1 * x + b1
        return PointK(x, y)
    }

    if (Math.abs(m1 - m2) < 1e-10) {
        return null
    }

    val x = (b2 - b1) / (m1 - m2)
    val y = m1 * x + b1

    return PointK(x, y)
}
```

### Пример использования

```kotlin
fun main() {
    val point1 = calculateIntersectionPointK(0.0, 0.0, 1.0, -1.0)
    println(point1) // Point(1, 0)

    val point2 = calculateIntersectionPointK(1.0, 0.0, 1.0, -1.0)
    println(point2) // null (параллельные линии)
}
```

## Сложность

Вычисление точки пересечения — O(1) по времени и памяти.

## Особенности

Алгоритм прост. При работе с `double` для сравнения наклонов используйте epsilon. Вертикальные прямые (бесконечный наклон) обрабатывайте отдельно.

## Применение

Используется в графике, геометрических алгоритмах, играх (коллизии, трассировка лучей), обработке изображений, CAD и навигации.

## Когда использовать

Применяйте при поиске пересечения двух прямых, заданных в форме y = mx + b, при работе с отрезками (с дополнительной проверкой принадлежности точки отрезку) и при необходимости поддержки вертикальных линий.

## Лучшие практики

Проверяйте параллельность (m1 == m2 или |m1−m2| < epsilon) до деления; возвращайте `Optional.empty()` или null. Для double используйте epsilon вместо ==. Вертикальные линии обрабатывайте отдельно (бесконечный наклон). В тестах покрывайте параллельные, пересекающиеся и совпадающие прямые. Явно документируйте формат (y = mx + b) и поведение в особых случаях.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Деление на ноль | Параллельные линии (m1 = m2) | Проверять m1 == m2 до вычисления x; возвращать empty |
| Неверная точка при близких наклонах | Ошибки округления double | Сравнивать наклоны с epsilon: Math.abs(m1 — m2) < 1e-10 |
| Не работает для вертикальной прямой | Форма y=mx+b не задаёт x=const | Отдельная ветка: вертикаль x=a, вторая линия y=m2*a+b2 |

## Частые вопросы

**Что возвращать для совпадающих прямых?** Бесконечно много точек. Часто возвращают пустой Optional или документируют особый контракт (например, точку на прямой). В улучшенной версии с epsilon при b1≈b2 можно вернуть одну точку.

**Как обработать вертикальный отрезок?** Прямая x = x1; точка пересечения (x1, m2*x1+b2). Проверить, что y в пределах y-границ отрезка.

**Почему использовать epsilon для double?** Из-за представления чисел с плавающей точкой m1 и m2 могут быть не равны побитово даже при математически параллельных прямых; сравнение с допуском устойчивее.

## Заключение

В документе описано вычисление точки пересечения двух прямых (y = mx + b) в `Java` и Kotlin. При параллельных прямых возвращается `Optional.empty()` или null. Рассмотрены пересечение отрезков, пересечение по двум точкам и случай вертикальной/горизонтальной прямой.
