---
title: "Перекрытие прямоугольников (Rectangle Overlap)"
description: "Проверка наличия общей точки у двух прямоугольников по координатам левого нижнего и правого верхнего углов. В документе описаны алгоритм проверки, площадь перекрытия, прямоугольник пересечения и варианты на Java и Kotlin."
tags:
  - algorithms
  - math
  - rectangle-overlap
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Перекрытие прямоугольников (`Rectangle Overlap`)

Проверка наличия общей точки у двух прямоугольников по координатам левого нижнего и правого верхнего углов. В документе описаны алгоритм проверки, площадь перекрытия, прямоугольник пересечения и варианты на `Java` и Kotlin.

## Полезные ссылки

### Официальная документация
- [Point (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/awt/Point.html)

### См. также
- [Поиск пересечения двух линий](line-intersection.md) — пересечение линий
- [Вычисление расстояния](distance-between-points.md) — расстояние между точками

## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Определение проблемы](#определение-проблемы)
  - [Примеры перекрытия](#примеры-перекрытия)
- [Представление прямоугольника](#представление-прямоугольника)
- [Алгоритм проверки перекрытия](#алгоритм-проверки-перекрытия)
- [Реализация на Java](#реализация-на-java)
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

Два прямоугольника перекрываются, если существует хотя бы одна общая точка. Варианты: полное перекрытие (один внутри другого), частичное пересечение, касание по границе, отсутствие перекрытия.

## Представление прямоугольника

Прямоугольник задаётся левым нижним и правым верхним углами (bottomLeft, topRight). Класс `Point` хранит (x, y).

```java
// Прямоугольник: левый нижний и правый верхний угол
public class Rectangle {
    private Point bottomLeft;
    private Point topRight;
    
    public Rectangle(Point bottomLeft, Point topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }
    
    public Point getBottomLeft() {
        return bottomLeft;
    }
    
    public Point getTopRight() {
        return topRight;
    }
}
```

Точка (x, y):

```java
// Точка с целочисленными координатами
public class Point {
    private int x;
    private int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}
```

## Алгоритм проверки перекрытия

Прямоугольники не перекрываются, если один строго выше другого (y2 < y3 или y1 > y4) или один строго левее другого (x2 < x3 или x1 > x4). Иначе перекрытие есть. Для r1: bottomLeft(x1,y1), topRight(x2,y2); r2: bottomLeft(x3,y3), topRight(x4,y4). Не перекрытие: y2 < y3, или y1 > y4, или x2 < x3, или x1 > x4.

## Реализация на Java

Проверка по осям Y и X: если интервалы по обеим осям пересекаются — прямоугольники перекрываются.

```java
// false, если один выше/ниже или левее/правее другого
public boolean isOverlapping(Rectangle other) {
    // Проверка по оси Y: один прямоугольник выше другого
    if (this.topRight.getY() < other.bottomLeft.getY() 
        || this.bottomLeft.getY() > other.topRight.getY()) {
        return false;
    }
    
    // Проверка по оси X: один прямоугольник левее другого
    if (this.topRight.getX() < other.bottomLeft.getX() 
        || this.bottomLeft.getX() > other.topRight.getX()) {
        return false;
    }
    
    return true;
}
```

### Полный пример класса

```java
// Класс Rectangle с isOverlapping и геттерами
public class Rectangle {
    private Point bottomLeft;
    private Point topRight;
    
    public Rectangle(Point bottomLeft, Point topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }
    
    public boolean isOverlapping(Rectangle other) {
        if (this.topRight.getY() < other.bottomLeft.getY() 
            || this.bottomLeft.getY() > other.topRight.getY()) {
            return false;
        }
        
        if (this.topRight.getX() < other.bottomLeft.getX() 
            || this.bottomLeft.getX() > other.topRight.getX()) {
            return false;
        }
        
        return true;
    }
    
    // Геттеры
    public Point getBottomLeft() {
        return bottomLeft;
    }
    
    public Point getTopRight() {
        return topRight;
    }
}
```

### Пример использования (Java)

```java
// Прямоугольник 1: от (0, 0) до (5, 5)
Rectangle r1 = new Rectangle(new Point(0, 0), new Point(5, 5));

// Прямоугольник 2: от (3, 3) до (8, 8) - перекрывается
Rectangle r2 = new Rectangle(new Point(3, 3), new Point(8, 8));

// Прямоугольник 3: от (10, 10) до (15, 15) - не перекрывается
Rectangle r3 = new Rectangle(new Point(10, 10), new Point(15, 15));

System.out.println(r1.isOverlapping(r2)); // true
System.out.println(r1.isOverlapping(r3)); // false
```

## Варианты задачи

### Вариант 1: Вычисление площади перекрытия

Пересечение интервалов по X и Y; ширина × высота перекрытия.

```java
// Площадь перекрытия: max левых — min правых по осям
public int getOverlapArea(Rectangle other) {
    if (!isOverlapping(other)) {
        return 0;
    }
    
    int overlapX1 = Math.max(this.bottomLeft.getX(), other.bottomLeft.getX());
    int overlapY1 = Math.max(this.bottomLeft.getY(), other.bottomLeft.getY());
    int overlapX2 = Math.min(this.topRight.getX(), other.topRight.getX());
    int overlapY2 = Math.min(this.topRight.getY(), other.topRight.getY());
    
    int width = overlapX2 - overlapX1;
    int height = overlapY2 - overlapY1;
    
    return width * height;
}
```

### Вариант 2: Получение прямоугольника перекрытия

Прямоугольник пересечения: общие границы по осям.

```java
public Optional<Rectangle> getOverlapRectangle(Rectangle other) {
    if (!isOverlapping(other)) {
        return Optional.empty();
    }
    
    int overlapX1 = Math.max(this.bottomLeft.getX(), other.bottomLeft.getX());
    int overlapY1 = Math.max(this.bottomLeft.getY(), other.bottomLeft.getY());
    int overlapX2 = Math.min(this.topRight.getX(), other.topRight.getX());
    int overlapY2 = Math.min(this.topRight.getY(), other.topRight.getY());
    
    Rectangle overlap = new Rectangle(
        new Point(overlapX1, overlapY1),
        new Point(overlapX2, overlapY2)
    );
    
    return Optional.of(overlap);
}
```

### Вариант 3: Проверка полного содержания

Один прямоугольник полностью внутри другого: все границы второго внутри первого.

```java
public boolean contains(Rectangle other) {
    return this.bottomLeft.getX() <= other.bottomLeft.getX()
        && this.bottomLeft.getY() <= other.bottomLeft.getY()
        && this.topRight.getX() >= other.topRight.getX()
        && this.topRight.getY() >= other.topRight.getY();
}
```

### Вариант 4: Проверка касания границ

Касание без перекрытия: совпадение одной из границ по X или Y.

```java
public boolean isTouching(Rectangle other) {
    // Проверка касания по вертикали
    boolean touchingVertically = 
        this.topRight.getY() == other.bottomLeft.getY() 
        || this.bottomLeft.getY() == other.topRight.getY();
    
    // Проверка касания по горизонтали
    boolean touchingHorizontally = 
        this.topRight.getX() == other.bottomLeft.getX() 
        || this.bottomLeft.getX() == other.topRight.getX();
    
    return touchingVertically || touchingHorizontally;
}
```

## Реализация на Kotlin

```kotlin
// data-классы Point и Rectangle с isOverlapping, getOverlapArea, isTouching
data class PointK(val x: Int, val y: Int)

data class RectangleK(val bottomLeft: PointK, val topRight: PointK) {
    fun isOverlappingK(other: RectangleK): Boolean {
        // Проверка по оси Y: один прямоугольник выше другого
        if (this.topRight.y < other.bottomLeft.y 
            || this.bottomLeft.y > other.topRight.y) {
            return false
        }
        
        // Проверка по оси X: один прямоугольник левее другого
        if (this.topRight.x < other.bottomLeft.x 
            || this.bottomLeft.x > other.topRight.x) {
            return false
        }
        
        return true
    }
    
    fun getAreaK(): Int {
        val width = topRight.x - bottomLeft.x
        val height = topRight.y - bottomLeft.y
        return width * height
    }
    
    fun getOverlapAreaK(other: RectangleK): Int {
        if (!isOverlappingK(other)) {
            return 0
        }
        
        val xOverlap = maxOf(0, 
            minOf(this.topRight.x, other.topRight.x) - 
            maxOf(this.bottomLeft.x, other.bottomLeft.x)
        )
        
        val yOverlap = maxOf(0,
            minOf(this.topRight.y, other.topRight.y) - 
            maxOf(this.bottomLeft.y, other.bottomLeft.y)
        )
        
        return xOverlap * yOverlap
    }
    
    fun isTouchingK(other: RectangleK): Boolean {
        val touchingVertically = 
            this.topRight.y == other.bottomLeft.y 
            || this.bottomLeft.y == other.topRight.y
        
        val touchingHorizontally = 
            this.topRight.x == other.bottomLeft.x 
            || this.bottomLeft.x == other.topRight.x
        
        return touchingVertically || touchingHorizontally
    }
}
```

### Пример использования (Kotlin)

```kotlin
fun main() {
    val r1 = RectangleK(PointK(0, 0), PointK(5, 5))
    val r2 = RectangleK(PointK(3, 3), PointK(8, 8))
    
    println(r1.isOverlappingK(r2)) // true
    
    val r3 = RectangleK(PointK(10, 10), PointK(15, 15))
    println(r1.isOverlappingK(r3)) // false
    
    val overlapArea = r1.getOverlapAreaK(r2)
    println("Overlap area: $overlapArea") // 4
}
```

## Сложность

Проверка перекрытия — O(1) по времени и памяти (сравнение координат). Хранение прямоугольника — два объекта `Point`.

## Особенности

Алгоритм прост: достаточно проверить разделение по осям. Работает за константное время; касание по границе при строгих неравенствах перекрытием не считается.

## Применение

Используется в графике, играх (коллизии), GIS, UI/UX, обработке изображений и системах управления окнами.

## Когда использовать

Применяйте при проверке коллизий, работе с GUI-элементами, обработке геометрических данных и когда нужна быстрая проверка пересечения по осям.

## Лучшие практики

Зафиксируйте представление прямоугольника (левый нижний + правый верхний или левый верхний + размеры) и придерживайтесь его везде. Явно определите, считается ли касание по границе перекрытием (обычно — нет, строгое неравенство). Гарантируйте инвариант left ≤ right, bottom ≤ top. Для `float`/`double` учитывайте погрешность при сравнении границ.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Перекрытие там, где его нет (или наоборот) | Разная система координат (Y вверх/вниз) или перепутаны left/right | Единое соглашение: bottomLeft.y < topRight.y и т.д.; проверить знаки |
| Отрицательная площадь перекрытия | Не проверено isOverlapping перед расчётом | Сначала isOverlapping; при false возвращать 0 или пустой Optional |
| Касание считается перекрытием | Использовано ≤ вместо < | Для «без касания» использовать строгие неравенства |

## Частые вопросы

**Считается ли касание по ребру перекрытием?** Зависит от постановки. При строгих неравенствах (один строго выше/левее) касание не считается перекрытием; при нестрогих — может считаться. Лучше явно описать в контракте.

**Какая система координат предполагается?** В примере: bottomLeft — левый нижний, topRight — правый верхний (Y растёт вверх). Если в вашей системе Y вниз, инвертируйте сравнения по Y.

**Как вычислить площадь перекрытия?** Пересечение интервалов по X и по Y: overlapX = max(0, min(x2,x4) - max(x1,x3)); то же для Y; площадь = overlapX * overlapY.

## Заключение

В документе описан алгоритм проверки перекрытия двух прямоугольников в `Java` и Kotlin: прямоугольники не перекрываются, если один строго выше, ниже, левее или правее другого. Приведены варианты: площадь перекрытия, прямоугольник пересечения, проверка содержания и касания.
