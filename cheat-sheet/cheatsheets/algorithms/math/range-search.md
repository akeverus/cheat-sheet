# Range Search

Кратко: поиск соседей в двумерном пространстве с использованием структуры данных QuadTree (дерево квадрантов). Рассматривается реализация для эффективного поиска точек в заданной области.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: QuadTree](https://www.geeksforgeeks.org/quad-tree/)

### См. также
- `./distance-between-points.md` - вычисление расстояния
- `./rectangle-overlap.md` - проверка перекрытия двух прямоугольников
- `../trees/binary-tree.md` - бинарное дерево

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Структура данных QuadTree](#структура-данных-quadtree)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы рассмотрим концепцию поиска соседей в двумерном пространстве. Затем мы рассмотрим его реализацию на Java.

Мы знаем, что бинарный поиск - это эффективный алгоритм поиска точного совпадения в списке элементов с использованием подхода «разделяй и властвуй».

Давайте теперь рассмотрим двумерную область, где каждый элемент представлен координатами XY (точками) на плоскости.

Однако предположим, что вместо точного совпадения мы хотим найти соседей данной точки на плоскости. Понятно, что если нам нужны ближайшие n совпадений, то бинарный поиск не сработает. Это связано с тем, что бинарный поиск может сравнивать два элемента только по одной оси, тогда как нам нужно иметь возможность сравнивать их по двум осям.

## Структура данных QuadTree

Дерево квадрантов - это пространственная древовидная структура данных, в которой каждый узел имеет ровно четыре дочерних элемента. Каждый дочерний элемент может быть либо точкой, либо списком, содержащим четыре поддерева квадрантов.

Точка хранит данные - например, координаты XY. Область представляет собой замкнутую границу, в пределах которой может быть сохранена точка. Он используется для определения области досягаемости дерева квадрантов.

### Пример работы

Давайте лучше разберемся в этом на примере 10 координат в произвольном порядке:

```
(21, 25), (55, 53), (70, 318), (98, 302), (49, 229), 
(135, 229), (224, 292), (206, 321), (197, 258), (245, 238)
```

1. **Первые три значения** будут сохранены в виде точек под корневым узлом.

2. **Корневой узел не может вместить новые точки**, так как он достиг своей емкости в три точки. Поэтому разделим область корневого узла на четыре равных квадранта.

3. **Каждый из этих квадрантов** может хранить три точки и дополнительно содержать четыре квадранта в пределах своей границы. Это можно сделать рекурсивно, в результате чего получится дерево квадрантов.

4. **Один квадрант снова подразделяется**, чтобы разместить больше точек в этой области, в то время как другие квадранты все еще могут принимать новые точки.

## Java Implementation

### Класс Point

Во-первых, мы создадим класс Point для хранения координат XY:

```java
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

Во-вторых, давайте создадим класс Region для определения границ квадранта:

```java
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

Наконец, давайте создадим класс QuadTree для хранения данных в виде экземпляров Point и дочерних элементов в виде классов QuadTree:

```java
import java.util.ArrayList;
import java.util.List;

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

Теперь, когда у нас есть наш алгоритм, давайте проверим его.

Во-первых, давайте заполним дерево квадрантов теми же 10 координатами, которые мы использовали ранее:

```java
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

Далее выполним поиск диапазона в области, ограниченной координатой нижней границы (200, 200) и координатой верхней границы (250, 250):

```java
Region searchArea = new Region(200, 200, 250, 250);
List<Point> result = quadTree.search(searchArea, null);
// Результат: [[245.0, 238.0]]
```

Попробуем другую область поиска между координатами (0, 0) и (100, 100):

```java
Region searchArea = new Region(0, 0, 100, 100);
List<Point> result = quadTree.search(searchArea, null);
// Результат: [[21.0, 25.0], [55.0, 53.0]]
```

Заметим, что в зависимости от размера области поиска мы получаем ноль, одну или много точек. Итак, если нам дали точку и попросили найти n ближайших соседей, мы могли бы определить подходящую область поиска, в которой данная точка находится в центре.

Затем по всем полученным точкам операции поиска мы можем вычислить евклидовы расстояния между заданными точками и отсортировать их для получения ближайших соседей.

## Сложность

### Временная сложность

- **Добавление точки:** O(log n) - в среднем случае, O(n) - в худшем случае
- **Поиск диапазона:** O(n) - в худшем случае, когда область поиска равна или больше, чем населенная область
- **В лучшем случае:** O(log n) - когда точки равномерно распределены

### Пространственная сложность

- **Хранение точек:** O(n) - где n - количество точек
- **Хранение структуры:** O(n) - для всех узлов дерева

## Особенности

- **Эффективность:** QuadTree эффективен для разреженных данных
- **Масштабируемость:** Хорошо работает с большими наборами данных
- **Гибкость:** Легко адаптировать для различных размеров областей

## Применение

Поиск диапазона с QuadTree используется в:

- Компьютерной графике
- Играх (коллизии, поиск объектов)
- Географических информационных системах (GIS)
- Обработке изображений
- Физических симуляциях

## Варианты задачи

### Вариант 1: Поиск k ближайших соседей

```java
public List<Point> findKNearestNeighbors(Point queryPoint, int k) {
    // Начинаем с небольшой области поиска
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

## Kotlin Implementation

### Класс Point

```kotlin
data class PointK(val x: Float, val y: Float) {
    override fun toString(): String = "[$x, $y]"
}
```

### Класс Region

```kotlin
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

### Класс QuadTree

```kotlin
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

### Поиск k ближайших соседей

```kotlin
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

### Пример использования

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

### Используйте QuadTree, когда:

- Нужен эффективный поиск в 2D пространстве
- Данные разреженные (не все области заполнены)
- Нужен поиск по диапазону
- Работаете с большими наборами данных

### Не используйте QuadTree, когда:

- Данные очень плотные
- Нужен поиск в 1D пространстве (используйте бинарное дерево)
- Область поиска всегда покрывает все данные

## Заключение

Временная сложность запроса диапазона просто O(n). Причина в том, что в худшем случае он должен пройти через каждый элемент, если указанная область поиска равна или больше, чем населенная область. В этой статье мы впервые поняли концепцию дерева квадрантов, сравнив его с бинарным деревом. Затем мы увидели, как его можно эффективно использовать для хранения данных, разбросанных по двумерному пространству.
