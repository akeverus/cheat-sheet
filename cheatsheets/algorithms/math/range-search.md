---
title: "Поиск по области (Range Search)"
description: "Поиск точек в заданной двумерной области с помощью структуры данных QuadTree (дерево квадрантов). Описаны классы Point, Region, QuadTree в Java и Kotlin, поиск по диапазону и варианты: k ближайших соседей, удаление точки, подсчёт в области."
tags:
  - algorithms
  - math
  - range-search
type: "reference"
difficulty: "intermediate"
aliases:
  - "Поиск по области"
  - "Range Search"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Поиск по области (Range Search)

Поиск точек в заданной двумерной области с помощью структуры данных QuadTree (дерево квадрантов). Описаны классы `Point`, `Region`, `QuadTree` в Java и Kotlin, поиск по диапазону и варианты: k ближайших соседей, удаление точки, подсчёт в области.

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: QuadTree](https://www.geeksforgeeks.org/quad-tree/)

### См. также
- [Вычисление расстояния](distance-between-points.md) — расстояние между точками
- [Проверка перекрытия двух прямоугольников](rectangle-overlap.md) — перекрытие прямоугольников
- [Бинарное дерево](../trees/) — бинарное дерево

- [Пересечение прямых (Line Intersection)](line-intersection.md)
- [Вычисление площади круга (Circle Area Calculation)](circle-area-calculation.md)
- [Взаимно простые числа (Coprime Numbers)](coprime-numbers.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Структура данных QuadTree](#структура-данных-quadtree)
  - [Пример работы](#пример-работы)
- [Реализация на Java](#реализация-на-java)
  - [Класс Point](#класс-point)
  - [Класс Region](#класс-region)
  - [Класс QuadTree](#класс-quadtree)
- [Пример использования](#пример-использования)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: Поиск k ближайших соседей](#вариант-1-поиск-k-ближайших-соседей)
  - [Вариант 2: Удаление точки](#вариант-2-удаление-точки)
  - [Вариант 3: Подсчет точек в области](#вариант-3-подсчет-точек-в-области)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Поиск соседей в 2D: элементы — точки с координатами XY. Бинарный поиск не подходит, так как сравнивает по одной оси; для «ближайших n точек» нужна пространственная структура. QuadTree позволяет эффективно искать точки в заданной прямоугольной области.

## Структура данных QuadTree

QuadTree — дерево, у каждого узла до четырех потомков. Узел хранит точки (до порога) или четыре поддерева-квадранта. Область (`Region`) задаёт границы узла; точка попадает в узел, если лежит внутри его области. При переполнении узел делится на четыре квадранта, точки распределяются по ним.

### Пример работы

Пример 10 точек: первые три попадают в корень; при четвёртой корень делится на четыре квадранта, точки распределяются рекурсивно. При скоплении в одном квадранте он снова подразделяется.

```text
(21, 25), (55, 53), (70, 318), (98, 302), (49, 229),
(135, 229), (224, 292), (206, 321), (197, 258), (245, 238)
```

## Реализация на Java

### Класс Point

```java
// Точка в 2D (x, y) для хранения в QuadTree и поиска по области
public class Point {
    private float x;
    private float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
```

### Класс Region

```java
// Границы прямоугольной области квадранта; проверка вхождения точки и перекрытия с другой областью
public class Region {
    private float x1;
    private float y1;
    private float x2;
    private float y2;

    public Region(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public float getX1() {
        return x1;
    }

    public float getY1() {
        return y1;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public boolean containsPoint(Point point) {
        return point.getX() >= this.x1
            && point.getX() < this.x2
            && point.getY() >= this.y1
            && point.getY() < this.y2;
    }

    public boolean doesOverlap(Region testRegion) {
        if (testRegion.getX2() < this.getX1()) {
            return false;
        }
        if (testRegion.getX1() > this.getX2()) {
            return false;
        }
        if (testRegion.getY1() > this.getY2()) {
            return false;
        }
        if (testRegion.getY2() < this.getY1()) {
            return false;
        }
        return true;
    }

    public Region getQuadrant(int quadrantIndex) {
        float quadrantWidth = (this.x2 - this.x1) / 2;
        float quadrantHeight = (this.y2 - this.y1) / 2;

        switch (quadrantIndex) {
            case 0:
                return new Region(x1, y1, x1 + quadrantWidth, y1 + quadrantHeight);
            case 1:
                return new Region(x1, y1 + quadrantHeight, x1 + quadrantWidth, y2);
            case 2:
                return new Region(x1 + quadrantWidth, y1 + quadrantHeight, x2, y2);
            case 3:
                return new Region(x1 + quadrantWidth, y1, x2, y1 + quadrantHeight);
        }
        return null;
    }
}
```

### Класс QuadTree

```java
import java.util.ArrayList;
import java.util.List;

// QuadTree: при переполнении (MAX_POINTS) создаём 4 поддерева и распределяем точки; поиск — обход пересекающихся с searchRegion узлов
public class QuadTree {
    private static final int MAX_POINTS = 3;
    private Region area;
    private List<Point> points = new ArrayList<>();
    private List<QuadTree> quadTrees = new ArrayList<>();

    public QuadTree(Region area) {
        this.area = area;
    }

    public boolean addPoint(Point point) {
        if (this.area.containsPoint(point)) {
            if (this.points.size() < MAX_POINTS) {
                this.points.add(point);
                return true;
            } else {
                if (this.quadTrees.size() == 0) {
                    createQuadrants();
                }
                return addPointToOneQuadrant(point);
            }
        }
        return false;
    }

    private boolean addPointToOneQuadrant(Point point) {
        boolean isPointAdded;
        for (int i = 0; i < 4; i++) {
            isPointAdded = this.quadTrees.get(i).addPoint(point);
            if (isPointAdded) {
                return true;
            }
        }
        return false;
    }

    private void createQuadrants() {
        Region region;
        for (int i = 0; i < 4; i++) {
            region = this.area.getQuadrant(i);
            quadTrees.add(new QuadTree(region));
        }
    }

    public List<Point> search(Region searchRegion, List<Point> matches) {
        if (matches == null) {
            matches = new ArrayList<Point>();
        }

        if (!this.area.doesOverlap(searchRegion)) {
            return matches;
        } else {
            for (Point point : points) {
                if (searchRegion.containsPoint(point)) {
                    matches.add(point);
                }
            }

            if (this.quadTrees.size() > 0) {
                for (int i = 0; i < 4; i++) {
                    quadTrees.get(i).search(searchRegion, matches);
                }
            }
        }

        return matches;
    }
}
```

## Пример использования

Пример: создаём область и дерево, добавляем точки, ищем в прямоугольнике.

```java
// Область 0..400 x 0..400, те же 10 точек
Region area = new Region(0, 0, 400, 400);
QuadTree quadTree = new QuadTree(area);

float[][] points = new float[][] {
    {21, 25}, {55, 53}, {70, 318}, {98, 302},
    {49, 229}, {135, 229}, {224, 292}, {206, 321},
    {197, 258}, {245, 238}
};

for (int i = 0; i < points.length; i++) {
    Point point = new Point(points[i][0], points[i][1]);
    quadTree.addPoint(point);
}
```

```java
// Поиск в [200,200]–[250,250]: одна точка [245, 238]
Region searchArea = new Region(200, 200, 250, 250);
List<Point> result = quadTree.search(searchArea, null);

// Поиск в [0,0]–[100,100]: две точки
searchArea = new Region(0, 0, 100, 100);
result = quadTree.search(searchArea, null);
```

Для k ближайших соседей задают область с центром в запросе, вызывают поиск по области, затем сортируют по евклидову расстоянию и берут k первых.

## Сложность

Вставка: в среднем O(log n), в худшем O(n). Поиск по области: в худшем O(n) (область покрывает всё дерево), при равномерном распределении лучше. Память O(n) для точек и узлов.

## Особенности

QuadTree эффективен при разреженных данных и масштабируется на большие наборы; размер и форма областей настраиваются.

## Применение

Используется в компьютерной графике, играх (коллизии, поиск объектов в области), GIS, обработке изображений и физических симуляциях.

## Варианты задачи

### Вариант 1: Поиск k ближайших соседей

Расширяем область поиска вокруг запроса, пока не наберём не меньше k точек; сортируем по расстоянию и возвращаем k ближайших.

```java
public List<Point> findKNearestNeighbors(Point queryPoint, int k) {
    // Расширяем searchRadius, пока в области не наберётся >= k точек
    float searchRadius = 10.0f;
    List<Point> neighbors = new ArrayList<>();

    while (neighbors.size() < k) {
        Region searchArea = new Region(
            queryPoint.getX() - searchRadius,
            queryPoint.getY() - searchRadius,
            queryPoint.getX() + searchRadius,
            queryPoint.getY() + searchRadius
        );

        neighbors = search(searchArea, null);

        if (neighbors.size() >= k) {
            // Сортируем по расстоянию и берем k ближайших
            neighbors.sort((p1, p2) -> {
                double dist1 = euclideanDistance(queryPoint, p1);
                double dist2 = euclideanDistance(queryPoint, p2);
                return Double.compare(dist1, dist2);
            });
            return neighbors.subList(0, k);
        }

        searchRadius *= 2;  // Увеличиваем радиус поиска
    }

    return neighbors;
}

private double euclideanDistance(Point p1, Point p2) {
    double dx = p1.getX() - p2.getX();
    double dy = p1.getY() - p2.getY();
    return Math.sqrt(dx * dx + dy * dy);
}
```

### Вариант 2: Удаление точки

Удаление в листе — из списка точек; иначе рекурсивно в том поддереве, чья область содержит точку.

```java
public boolean removePoint(Point point) {
    if (!this.area.containsPoint(point)) {
        return false;
    }

    if (this.points.remove(point)) {
        return true;
    }

    if (this.quadTrees.size() > 0) {
        for (QuadTree quadTree : quadTrees) {
            if (quadTree.removePoint(point)) {
                return true;
            }
        }
    }

    return false;
}
```

### Вариант 3: Подсчет точек в области

Рекурсивный обход узлов, пересекающихся с областью; подсчёт точек, попавших в `searchRegion`.

```java
public int countPointsInRegion(Region searchRegion) {
    if (!this.area.doesOverlap(searchRegion)) {
        return 0;
    }

    int count = 0;

    for (Point point : points) {
        if (searchRegion.containsPoint(point)) {
            count++;
        }
    }

    if (this.quadTrees.size() > 0) {
        for (QuadTree quadTree : quadTrees) {
            count += quadTree.countPointsInRegion(searchRegion);
        }
    }

    return count;
}
```

## Реализация на Kotlin

```kotlin
// Точка 2D
data class PointK(val x: Float, val y: Float) {
    override fun toString(): String = "[$x, $y]"
}
```

```kotlin
// Область и проверки containsPoint / doesOverlap / getQuadrant
data class RegionK(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float
) {
    fun containsPoint(point: PointK): Boolean {
        return point.x >= this.x1
            && point.x < this.x2
            && point.y >= this.y1
            && point.y < this.y2
    }

    fun doesOverlap(testRegion: RegionK): Boolean {
        return !(testRegion.x2 < this.x1
            || testRegion.x1 > this.x2
            || testRegion.y1 > this.y2
            || testRegion.y2 < this.y1)
    }

    fun getQuadrant(quadrantIndex: Int): RegionK {
        val quadrantWidth = (this.x2 - this.x1) / 2
        val quadrantHeight = (this.y2 - this.y1) / 2

        return when (quadrantIndex) {
            0 -> RegionK(x1, y1, x1 + quadrantWidth, y1 + quadrantHeight)
            1 -> RegionK(x1 + quadrantWidth, y1, x2, y1 + quadrantHeight)
            2 -> RegionK(x1, y1 + quadrantHeight, x1 + quadrantWidth, y2)
            3 -> RegionK(x1 + quadrantWidth, y1 + quadrantHeight, x2, y2)
            else -> throw IllegalArgumentException("Invalid quadrant index")
        }
    }
}
```

```kotlin
// QuadTree: вставка, поиск по области, удаление
class QuadTreeK(
    private val area: RegionK,
    private val bucketCapacity: Int = 3
) {
    private val points = mutableListOf<PointK>()
    private val quadTrees = mutableListOf<QuadTreeK>()

    fun insert(point: PointK): Boolean {
        if (!this.area.containsPoint(point)) {
            return false
        }

        if (this.points.size < bucketCapacity) {
            this.points.add(point)
            return true
        }

        if (this.quadTrees.isEmpty()) {
            createQuadrants()
        }

        return quadTrees.any { it.insert(point) }
    }

    private fun createQuadrants() {
        for (i in 0 until 4) {
            quadTrees.add(QuadTreeK(area.getQuadrant(i), bucketCapacity))
        }
    }

    fun search(searchArea: RegionK, foundPoints: MutableList<PointK>? = null): List<PointK> {
        val pointsInRange = foundPoints ?: mutableListOf()

        if (!this.area.doesOverlap(searchArea)) {
            return pointsInRange
        }

        for (point in points) {
            if (searchArea.containsPoint(point)) {
                pointsInRange.add(point)
            }
        }

        if (quadTrees.isNotEmpty()) {
            for (quadTree in quadTrees) {
                quadTree.search(searchArea, pointsInRange)
            }
        }

        return pointsInRange
    }

    fun removePoint(point: PointK): Boolean {
        if (!this.area.containsPoint(point)) {
            return false
        }

        if (points.remove(point)) {
            return true
        }

        return quadTrees.any { it.removePoint(point) }
    }
}
```

```kotlin
// Расширение области до >= k точек, сортировка по расстоянию, take(k)
fun QuadTreeK.findKNearestNeighborsK(queryPoint: PointK, k: Int): List<PointK> {
    var searchRadius = 10.0f
    var neighbors = mutableListOf<PointK>()

    while (neighbors.size < k) {
        val searchArea = RegionK(
            queryPoint.x - searchRadius,
            queryPoint.y - searchRadius,
            queryPoint.x + searchRadius,
            queryPoint.y + searchRadius
        )

        neighbors = search(searchArea).toMutableList()

        if (neighbors.size >= k) {
            neighbors.sortBy { p ->
                val dx = p.x - queryPoint.x
                val dy = p.y - queryPoint.y
                dx * dx + dy * dy
            }
            return neighbors.take(k)
        }

        searchRadius *= 2
    }

    return neighbors
}

private fun euclideanDistanceK(p1: PointK, p2: PointK): Double {
    val dx = p1.x - p2.x
    val dy = p1.y - p2.y
    return Math.sqrt((dx * dx + dy * dy).toDouble())
}
```

```kotlin
fun main() {
    val quadTree = QuadTreeK(RegionK(0f, 0f, 400f, 400f))

    quadTree.insert(PointK(21f, 25f))
    quadTree.insert(PointK(55f, 53f))
    quadTree.insert(PointK(70f, 318f))

    val searchArea = RegionK(0f, 0f, 100f, 100f)
    val result = quadTree.search(searchArea)
    println(result) // [[21.0, 25.0], [55.0, 53.0]]
}
```

## Когда использовать

QuadTree уместен при поиске в 2D по диапазону, при разреженных данных и больших объёмах. Не стоит использовать при очень плотных данных, в 1D (достаточно бинарного дерева) или когда область поиска всегда покрывает все точки — тогда выгоднее линейный проход.

## Лучшие практики

Ограничьте максимальную глубину дерева, чтобы избежать бесконечного дробления при скоплении точек; при лимите храните список в листе. Порог разделения (например, 3–4 точки на узел) выбирайте разумно: слишком малый — лишняя глубина, слишком большой — слабое отсечение при поиске. Все точки и области задавайте в одной системе координат. Область корня должна покрывать все возможные точки или расширяться при вставке за границы. Точки лучше делать неизменяемыми.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Точка не находится при поиске по области | Точка вне области корня или неверные границы | Задать корневую область с запасом или расширять её при вставке |
| Медленный поиск | Очень большая область поиска или плотные данные | Уменьшить область запроса; при плотных данных рассмотреть другую структуру |
| Глубокое дерево, много узлов | Много точек в одной зоне, малый порог | Ограничить глубину; увеличить порог точек на узел в разумных пределах |

## Частые вопросы

**Чем QuadTree лучше линейного перебора?** При разреженных данных и небольших областях поиска QuadTree отсекает целые квадранты, не пересекающиеся с областью, и даёт в среднем сублинейное время. При области на всё дерево выигрыша нет.

**Когда использовать k-d дерево вместо QuadTree?** K-d дерево часто предпочтительнее в высоких размерностях и при чередовании осей; QuadTree проще реализовать и удобен для 2D и равномерного разбиения по площади.

**Нужно ли перебалансировать QuadTree?** Обычно нет; структура статична по области. При частых удалениях можно предусмотреть слияние пустых узлов для экономии памяти.

## Заключение

Поиск по области в худшем случае O(n); при равномерном распределении и умеренных областях QuadTree даёт ускорение за счёт отсечения. В документе описаны `Point`, `Region`, `QuadTree` в Java и Kotlin, поиск по диапазону и варианты: k ближайших соседей, удаление, подсчёт в области.
