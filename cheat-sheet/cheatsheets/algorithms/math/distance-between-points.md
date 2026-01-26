# Distance Between Points

Кратко: вычисление расстояния между двумя точками на плоскости с использованием формулы расстояния (теорема Пифагора). Рассматриваются различные реализации: прямая формула, Math.hypot() и Point2D.distance().

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Math.hypot() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#hypot-double-double-)
- [Java Point2D.distance() Documentation](https://docs.oracle.com/javase/8/docs/api/java/awt/geom/Point2D.html#distance-double-double-)

### См. также
- `./circle-area-calculation.md` - вычисление площади круга
- `./line-intersection.md` - пересечение линий

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы покажем, как рассчитать расстояние между двумя точками в Java. Допустим, у нас есть две точки на плоскости: первая точка A имеет координаты (x1, y1), а вторая точка B имеет координаты (x2, y2). Мы хотим вычислить AB, расстояние между точками.

### Теорема Пифагора

Сначала построим прямоугольный треугольник с гипотенузой АВ:

По теореме Пифагора сумма квадратов длин катетов треугольника равна квадрату длины гипотенузы треугольника: AB² = AC² + CB².

Во-вторых, посчитаем AC и CB.

Очевидно:
- AC = y2 - y1

Сходным образом:
- BC = x2 - x1

Подставим части уравнения:

```
distance² = (y2 - y1)² + (x2 - x1)²
```

Наконец, из приведенного выше уравнения мы можем рассчитать расстояние между точками:

```
distance = √((y2 - y1)² + (x2 - x1)²)
```

## Java Implementation

### Подход 1: Прямая формула

Теперь перейдем к части реализации.

Хотя пакеты java.lang.Math и java.awt.geom.Point2D предоставляют готовые решения, давайте сначала реализуем приведенную выше формулу как есть:

```java
public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
    return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
}
```

Для проверки решения возьмем треугольник с катетами 3 и 4 (как показано на рисунке выше). Понятно, что в качестве значения гипотенузы подходит число 5:

```
3² + 4² = 5²
```

Проверим решение:

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

Если результат умножения в методе calculateDistanceBetweenPoints() слишком велик, может произойти переполнение. В отличие от этого, метод Math.hypot() предотвращает промежуточное переполнение или потерю значимости:

```java
public double calculateDistanceBetweenPointsWithHypot(double x1, double y1, double x2, double y2) {
    double ac = Math.abs(y2 - y1);
    double cb = Math.abs(x2 - x1);
    return Math.hypot(ac, cb);
}
```

Возьмем те же точки, что и раньше, и проверим, что расстояние такое же:

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

- Предотвращает переполнение
- Предотвращает потерю значимости
- Более точные результаты для больших чисел

## Подход 3: Point2D.distance()

Наконец, давайте рассчитаем расстояние с помощью метода Point2D.distance():

```java
import java.awt.geom.Point2D;

public double calculateDistanceBetweenPointsWithPoint2D(double x1, double y1, double x2, double y2) {
    return Point2D.distance(x1, y1, x2, y2);
}
```

Теперь давайте проверим метод таким же образом:

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

Для вычисления расстояния между двумя точками в трехмерном пространстве:

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
| Math.hypot() | Высокая | Средняя | Нет | Рекомендуется |
| Point2D.distance() | Высокая | Средняя | Нет | Работа с Point2D |

## Сложность

### Временная сложность

- **Все подходы:** O(1) - константное время

### Пространственная сложность

- **Все подходы:** O(1) - только константная память

## Особенности

- **Точность:** Math.hypot() и Point2D.distance() более точны
- **Переполнение:** Прямая формула может вызвать переполнение
- **Производительность:** Прямая формула быстрее для малых чисел

## Применение

Вычисление расстояния между точками используется в:

- Компьютерной графике
- Географических информационных системах (GIS)
- Играх
- Навигации
- Машинном обучении

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

## Kotlin Implementation

### Подход 1: Прямая формула

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

### Используйте прямую формулу, когда:

- Работаете с малыми числами
- Нужна максимальная производительность
- Переполнение не проблема

### Используйте Math.hypot(), когда:

- Работаете с большими числами
- Нужна точность
- Важно избежать переполнения

### Используйте Point2D.distance(), когда:

- Работаете с объектами Point2D
- Нужна читаемость кода
- Используете Java AWT

## Заключение

В этом руководстве мы показали несколько способов вычисления расстояния между двумя точками в Java. Math.hypot() рекомендуется для большинства случаев, так как он предотвращает переполнение и обеспечивает высокую точность.
