---
title: "Расстояние между точками (Distance Between Points)"
description: "Вычисление расстояния между двумя точками на плоскости по теореме Пифагора: формула √((x2−x1)²+(y2−y1)²), реализация через прямую формулу, Math.hypot() и Point2D.distance(). Рассмотрены 3D, метрики Манхэттена и Чебышёва."
tags:
  - algorithms
  - math
  - distance-between-points
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Расстояние между точками (Distance Between Points)

Вычисление расстояния между двумя точками на плоскости по теореме Пифагора: формула √((x2−x1)²+(y2−y1)²), реализация через прямую формулу, `Math.hypot()` и `Point2D.distance()`. Рассмотрены 3D, метрики Манхэттена и Чебышёва.

## Полезные ссылки

### Официальная документация
- [Math.hypot() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#hypot-double-double-)
- [Point2D.distance() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/awt/geom/Point2D.html)

### См. также
- [[circle-area-calculation|Вычисление площади круга]] — площадь круга
- [[line-intersection|Пересечение линий]] — пересечение линий

## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Теорема Пифагора](#теорема-пифагора)
- [Реализация на Java](#реализация-на-java)
  - [Подход 1: Прямая формула (Java)](#подход-1-прямая-формула-java)
  - [Улучшенная версия с Math.pow()](#улучшенная-версия-с-mathpow)
- [Подход 2: Math.hypot()](#подход-2-mathhypot)
  - [Преимущества Math.hypot()](#преимущества-mathhypot)
- [Подход 3: Point2D.distance()](#подход-3-point2ddistance)
  - [Использование объектов Point2D](#использование-объектов-point2d)
- [Расстояние в 3D](#расстояние-в-3d)
  - [Использование Math.hypot() для 3D](#использование-mathhypot-для-3d)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Расстояние Манхэттена](#вариант-1-расстояние-манхэттена)
  - [Вариант 2: Расстояние Чебышева](#вариант-2-расстояние-чебышева)
  - [Вариант 3: Расстояние для массива точек](#вариант-3-расстояние-для-массива-точек)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Подход 1: Прямая формула (Kotlin)](#подход-1-прямая-формула-kotlin)
  - [Подход 2: Math.hypot()](#подход-2-mathhypot-1)
  - [Подход 3: Point2D.distance()](#подход-3-point2ddistance-1)
  - [Расстояние в 3D](#расстояние-в-3d-1)
  - [Дополнительные метрики расстояния](#дополнительные-метрики-расстояния)
  - [Пример использования](#пример-использования)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

В этом кратком руководстве мы покажем, как рассчитать расстояние между двумя точками в **Java**. Допустим, у нас есть две точки на плоскости: первая точка A имеет координаты (x1, y1), а вторая точка B имеет координаты (x2, y2). Мы хотим вычислить `AB`, расстояние между точками.

### Теорема Пифагора

**Сначала построим прямоугольный треугольник с гипотенузой АВ:**

По теореме Пифагора сумма квадратов длин катетов треугольника равна квадрату длины гипотенузы треугольника: AB² = AC² + CB².

Во-вторых, посчитаем `AC` и `CB`.

**Очевидно:**
- `AC` = **y2** — **y1**

**Сходным образом:**
- `BC` = **x2** — **x1**

**Подставим части уравнения:**

```text
distance² = (y2 - y1)² + (x2 - x1)²
```

**Наконец, из приведенного выше уравнения мы можем рассчитать расстояние между точками:**

```text
distance = √((y2 - y1)² + (x2 - x1)²)
```

## Реализация на Java

### Подход 1: Прямая формула (Java)

Теперь перейдем к части реализации.

**Хотя пакеты **java.lang.Math** и **java.`awt.geom`.Point2D** предоставляют готовые решения, давайте сначала реализуем приведенную выше формулу как есть:**

```java
// Расстояние между точками: √((x2−x1)² + (y2−y1)²)
public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
    return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
}
```

**Для проверки решения возьмем треугольник с катетами 3 и 4 (как показано на рисунке выше). Понятно, что в качестве значения гипотенузы подходит число 5:**

```text
3² + 4² = 5²
```

**Проверим решение:**

```java
@Test
public void givenTwoPoints_whenCalculateDistanceByFormula_thenCorrect() {
    double x1 = 3;
    double y1 = 4;
    double x2 = 7;
    double y2 = 1;

    double distance = service.calculateDistanceBetweenPoints(x1, y1, x2, y2);
    assertEquals(distance, 5, 0.001);
}
```

### Улучшенная версия с Math.pow()

```java
public double calculateDistanceWithPow(double x1, double y1, double x2, double y2) {
    return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
}
```

## Подход 2: Math.hypot()

`Math.hypot(ac, cb)` вычисляет √(ac²+cb²) без промежуточного переполнения. Разницы по модулю можно не брать: для расстояния квадраты устраняют знак.

```java
// Безопасно для больших разностей: нет переполнения при возведении в квадрат
public double calculateDistanceBetweenPointsWithHypot(double x1, double y1, double x2, double y2) {
    double ac = Math.abs(y2 - y1);
    double cb = Math.abs(x2 - x1);
    return Math.hypot(ac, cb);
}
```

**Возьмем те же точки, что и раньше, и проверим, что расстояние такое же:**

```java
@Test
public void givenTwoPoints_whenCalculateDistanceWithHypot_thenCorrect() {
    double x1 = 3;
    double y1 = 4;
    double x2 = 7;
    double y2 = 1;

    double distance = service.calculateDistanceBetweenPointsWithHypot(x1, y1, x2, y2);
    assertEquals(distance, 5, 0.001);
}
```

### Преимущества Math.hypot()

`Math.hypot()` уменьшает риск переполнения и потери значимости при больших разностях координат и даёт более точный результат, чем ручное возведение в квадрат и извлечение корня.

## Подход 3: Point2D.distance()

**Наконец, давайте рассчитаем расстояние с помощью метода **Point2D.distance**():**

```java
import java.awt.geom.Point2D;

public double calculateDistanceBetweenPointsWithPoint2D(double x1, double y1, double x2, double y2) {
    return Point2D.distance(x1, y1, x2, y2);
}
```

**Теперь давайте проверим метод таким же образом:**

```java
@Test
public void givenTwoPoints_whenCalculateDistanceWithPoint2D_thenCorrect() {
    double x1 = 3;
    double y1 = 4;
    double x2 = 7;
    double y2 = 1;

    double distance = service.calculateDistanceBetweenPointsWithPoint2D(x1, y1, x2, y2);
    assertEquals(distance, 5, 0.001);
}
```

### Использование объектов Point2D

```java
public double calculateDistanceWithPoint2DObjects(Point2D point1, Point2D point2) {
    return point1.distance(point2);
}
```

## Расстояние в 3D

**Для вычисления расстояния между двумя точками в трехмерном пространстве:**

```java
public double calculateDistance3D(double x1, double y1, double z1,
                                   double x2, double y2, double z2) {
    return Math.sqrt(Math.pow(x2 - x1, 2) +
                     Math.pow(y2 - y1, 2) +
                     Math.pow(z2 - z1, 2));
}
```

### Использование Math.hypot() для 3D

```java
public double calculateDistance3DWithHypot(double x1, double y1, double z1,
                                           double x2, double y2, double z2) {
    double dx = x2 - x1;
    double dy = y2 - y1;
    double dz = z2 - z1;
    return Math.hypot(Math.hypot(dx, dy), dz);
}
```

## Сравнение подходов

| Подход | Точность | Производительность | Переполнение | Когда использовать |
|--------|----------|-------------------|--------------|-------------------|
| Прямая формула | Средняя | Высокая | Возможно | Малые числа |
| `Math.hypot()` | Высокая | Средняя | Нет | Рекомендуется |
| `Point2D.distance()` | Высокая | Средняя | Нет | Работа с `Point2D` |

## Сложность

Все подходы имеют константную временную и пространственную сложность `O(1)`.

## Особенности

`Math.hypot()` и `Point2D.distance()` дают лучшую точность и не приводят к переполнению при больших разностях координат. Прямая формула быстрее на малых числах, но при больших значениях возможны переполнение и потеря точности.

## Применение

Расстояние между точками используется в компьютерной графике, ГИС, играх, навигации и машинном обучении — везде, где нужна евклидова метрика или её варианты (Манхэттен, Чебышёв).

## Варианты задачи

### Вариант 1: Расстояние Манхэттена

```java
public double manhattanDistance(double x1, double y1, double x2, double y2) {
    return Math.abs(x2 - x1) + Math.abs(y2 - y1);
}
```

### Вариант 2: Расстояние Чебышева

```java
public double chebyshevDistance(double x1, double y1, double x2, double y2) {
    return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
}
```

### Вариант 3: Расстояние для массива точек

```java
public double totalDistance(double[][] points) {
    double total = 0.0;
    for (int i = 0; i < points.length - 1; i++) {
        total += calculateDistanceBetweenPoints(
            points[i][0], points[i][1],
            points[i+1][0], points[i+1][1]
        );
    }
    return total;
}
```

## Реализация на Kotlin

### Подход 1: Прямая формула (Kotlin)

```kotlin
fun calculateDistanceBetweenPointsK(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1))
}

fun calculateDistanceWithPowK(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return Math.sqrt(Math.pow(y2 - y1, 2.0) + Math.pow(x2 - x1, 2.0))
}
```

### Подход 2: Math.hypot()

```kotlin
fun calculateDistanceWithHypotK(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return Math.hypot(x2 - x1, y2 - y1)
}
```

### Подход 3: Point2D.distance()

```kotlin
import java.awt.geom.Point2D

fun calculateDistanceWithPoint2DK(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return Point2D.distance(x1, y1, x2, y2)
}

fun calculateDistanceWithPoint2DObjectsK(point1: Point2D, point2: Point2D): Double {
    return point1.distance(point2)
}
```

### Расстояние в 3D

```kotlin
fun calculateDistance3DK(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
    return Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0) + Math.pow(z2 - z1, 2.0))
}

fun calculateDistance3DWithHypotK(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): Double {
    val dx = x2 - x1
    val dy = y2 - y1
    val dz = z2 - z1
    return Math.hypot(Math.hypot(dx, dy), dz)
}
```

### Дополнительные метрики расстояния

```kotlin
fun manhattanDistanceK(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return Math.abs(x2 - x1) + Math.abs(y2 - y1)
}

fun chebyshevDistanceK(x1: Double, y1: Double, x2: Double, y2: Double): Double {
    return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1))
}

fun totalDistanceK(points: Array<DoubleArray>): Double {
    var total = 0.0
    for (i in 0 until points.size - 1) {
        total += calculateDistanceBetweenPointsK(
            points[i][0], points[i][1],
            points[i + 1][0], points[i + 1][1]
        )
    }
    return total
}
```

### Пример использования

```kotlin
fun main() {
    val x1 = 3.0
    val y1 = 4.0
    val x2 = 7.0
    val y2 = 1.0

    val distance1 = calculateDistanceBetweenPointsK(x1, y1, x2, y2)
    println("Distance (formula): $distance1") // 5.0

    val distance2 = calculateDistanceWithHypotK(x1, y1, x2, y2)
    println("Distance (hypot): $distance2") // 5.0

    val distance3 = calculateDistanceWithPoint2DK(x1, y1, x2, y2)
    println("Distance (Point2D): $distance3") // 5.0

    // 3D расстояние
    val distance3D = calculateDistance3DK(0.0, 0.0, 0.0, 3.0, 4.0, 5.0)
    println("3D Distance: $distance3D") // ~7.07
}
```

## Когда использовать

Прямую формулу уместно применять при малых координатах и когда переполнение исключено. Для больших разностей и когда важна точность предпочтительнее `Math.hypot(x2-x1, y2-y1)`. `Point2D.distance()` удобен, если уже используются объекты `Point2D` или нужна явная работа с точками из AWT.

## Лучшие практики

Для больших разностей координат используйте `Math.hypot(x2-x1, y2-y1)` вместо ручного возведения в квадрат и `sqrt` — так избегают переполнения и потери точности. В тестах покрывайте совпадающие точки, точки на оси и классический треугольник 3-4-5. Параметры лучше именовать явно (x1, y1, x2, y2) или использовать типы вроде `Point2D`.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Неверный результат при больших координатах | Переполнение при возведении в квадрат | Использовать `Math.hypot(dx, dy)` |
| NaN или Infinity | Очень большие или некорректные значения | Проверить входы; для 3D — `Math.hypot(Math.hypot(dx, dy), dz)` |
| Отрицательное «расстояние» | Ошибка в формуле или порядке аргументов | Формула использует разности (x2−x1), (y2−y1); порядок не меняет результат |

## Частые вопросы

**Чем Math.hypot лучше ручной формулы?** `Math.hypot` вычисляет √(a²+b²) без промежуточного переполнения при больших a, b: не возводит сразу в квадрат, а масштабирует вычисление. Для малых чисел разница несущественна.

**Когда использовать расстояние Манхэттена?** Когда движение возможно только по осям (сетка, городские кварталы): |x2−x1| + |y2−y1|. В машинном обучении часто используется как L1-метрика.

**Как считать расстояние в 3D?** Аналогично 2D: √((x2−x1)²+(y2−y1)²+(z2−z1)²). Удобно через `Math.hypot(Math.hypot(dx, dy), dz)`.

## Заключение

В документе рассмотрены способы вычисления расстояния между двумя точками в `Java`: прямая формула, `Math.hypot()` (рекомендуется для большинства случаев) и `Point2D.distance()`. Для больших координат предпочтителен `Math.hypot()` из-за устойчивости к переполнению.
