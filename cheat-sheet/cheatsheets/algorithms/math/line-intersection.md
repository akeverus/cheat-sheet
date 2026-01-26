# Line Intersection

Кратко: поиск точки пересечения двух линий, определенных линейными функциями в форме пересечения наклона. Рассматривается алгоритм для параллельных и непараллельных линий.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Optional Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)
- [Java Point Documentation](https://docs.oracle.com/javase/8/docs/api/java/awt/Point.html)

### См. также
- `./rectangle-overlap.md` - проверка перекрытия двух прямоугольников
- `./distance-between-points.md` - вычисление расстояния

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Математическая формулировка](#математическая-формулировка)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы покажем, как найти точку пересечения двух линий, определенных линейными функциями в форме пересечения наклона.

### Форма уравнения прямой

Любая прямая линия (кроме вертикальной) на плоскости может быть задана линейной функцией:

```
y = mx + b
```

где:
- **m** - наклон (slope)
- **b** - точка пересечения с осью y (y-intercept)

Для вертикальной линии m будет равно бесконечности, поэтому мы его исключаем. Если две прямые параллельны, они имеют одинаковый наклон, то есть одно и то же значение m.

### Примеры

- Линия с наклоном 1 и пересечением -1: `y = x - 1`
- Горизонтальная линия: `y = 0` (наклон 0)
- Линия с наклоном 2 и пересечением 3: `y = 2x + 3`

## Математическая формулировка

Допустим, у нас есть две линии. Первая функция определяет первую строку:

```
y = m1x + b1
```

И вторая функция определяет вторую строку:

```
y = m2x + b2
```

Мы хотим найти точку пересечения этих линий. Очевидно, что для точки пересечения верно равенство:

```
y1 = y2
```

Заменим переменные y:

```
m1x + b1 = m2x + b2
```

Из приведенного выше уравнения мы можем найти координату x:

```
x(m1 - m2) = b2 - b1
```

```
x = (b2 - b1) / (m1 - m2)
```

Наконец, мы можем найти y-координату точки пересечения:

```
y = m1x + b1
```

или

```
y = m2x + b2
```

## Java Implementation

### Реализация

Во-первых, у нас есть четыре входных переменных - m1, b1 для первой строки и m2, b2 для второй строки.

Во-вторых, мы преобразуем рассчитанную точку пересечения в объект типа `java.awt.Point`.

Наконец, линии могут быть параллельными, поэтому давайте сделаем возвращаемое значение `Optional<Point>`:

```java
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

### Пример использования

Теперь давайте выберем некоторые значения и протестируем метод для параллельных и непараллельных линий.

Например, возьмем ось x (y = 0) в качестве первой линии, а линию, определяемую y = x - 1, в качестве второй линии.

Для второй линии наклон m равен 1, что означает 45 градусов, а точка пересечения y равна -1, что означает, что линия пересекает ось y в точке (0, -1).

Интуитивно понятно, что точка пересечения второй линии с осью x должна быть (1, 0):

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

Наконец, давайте возьмем две параллельные строки и убедимся, что возвращаемое значение пусто:

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

```java
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

```java
public Optional<Point> calculateVerticalHorizontalIntersection(
    double verticalX,      // x-координата вертикальной линии
    double horizontalY     // y-координата горизонтальной линии
) {
    Point point = new Point((int)verticalX, (int)horizontalY);
    return Optional.of(point);
}
```

## Kotlin Implementation

### Основная реализация

```kotlin
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

### Временная сложность

- **Вычисление точки пересечения:** O(1) - константное время, так как это простые арифметические операции

### Пространственная сложность

- **Вычисление точки пересечения:** O(1) - только константная память для хранения результата

## Особенности

- **Простота:** Алгоритм очень прост и понятен
- **Эффективность:** Выполняется за константное время
- **Точность:** Может иметь проблемы с точностью при работе с числами с плавающей точкой

## Применение

Поиск пересечения линий используется в:

- Компьютерной графике
- Геометрических алгоритмах
- Играх (коллизии, трассировка лучей)
- Обработке изображений
- CAD системах
- Навигации

## Когда использовать

### Используйте этот алгоритм, когда:

- Нужно найти точку пересечения двух прямых
- Работаете с линейными функциями
- Решаете геометрические задачи
- Нужна быстрая проверка пересечения

## Заключение

В этом уроке мы показали, как вычислить точку пересечения двух линий. Алгоритм основан на решении системы линейных уравнений и обрабатывает случай параллельных линий, возвращая пустой Optional.
