# A* Pathfinding Algorithm

Кратко: реализация алгоритма поиска пути A* для навигации по графам. Рассматривается универсальная реализация с примерами использования для навигации в метро.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: A* Pathfinding](https://www.baeldung.com/java-a-star-pathfinding)

### См. также
- `./traveling-salesman-problem.md` - задача коммивояжера
- `./maze-solver.md` - решение лабиринтов
- `../graphs/dijkstra-algorithm.md` - алгоритм Дейкстры

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Основные концепции](#основные-концепции)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Пример использования](#пример-использования)
- [Сложность](#сложность)

## Описание алгоритма

Алгоритмы поиска пути - это методы навигации по картам, позволяющие нам найти маршрут между двумя разными точками. Разные алгоритмы имеют разные плюсы и минусы, часто с точки зрения эффективности алгоритма и эффективности генерируемого им маршрута.

Алгоритм поиска пути - это метод преобразования графа, состоящего из узлов и ребер, в маршрут через граф. Этот граф может быть чем угодно, что требует обхода.

### Особенности графа

В нем много интересных компонентов:

1. У нас может быть, а может и не быть прямого маршрута между начальной и конечной точками.
2. Каждый шаг имеет определенную стоимость. В нашем случае это расстояние между станциями.
3. Каждая остановка связана только с небольшим подмножеством других остановок.
4. Все алгоритмы поиска пути принимают на вход совокупность всех узлов и соединений между ними, а также желаемые начальные и конечные точки. Результатом обычно является набор узлов, которые проведут нас от начала до конца в том порядке, в котором нам нужно пройти.

A* - это один конкретный алгоритм поиска пути, впервые опубликованный в 1968 году Питером Хартом, Нильсом Нильссоном и Бертрамом Рафаэлем. Обычно считается, что это лучший алгоритм для использования, когда нет возможности предварительно вычислить маршруты и нет ограничений на использование памяти.

## Основные концепции

Сложность как памяти, так и производительности может быть O(b^d) в худшем случае, поэтому, хотя всегда будет работать наиболее эффективный маршрут, это не всегда самый эффективный способ сделать это.

A* на самом деле является разновидностью алгоритма Дейкстры, где предоставляется дополнительная информация, помогающая выбрать следующий узел для использования. Эта дополнительная информация не обязательно должна быть идеальной - если у нас уже есть идеальная информация, поиск пути не имеет смысла. Но чем она лучше, тем лучше будет конечный результат.

### Механизм работы

Алгоритм A* работает путем итеративного выбора наилучшего маршрута на данный момент и попытки определить наилучший следующий шаг.

При работе с этим алгоритмом у нас есть несколько фрагментов данных, которые нам нужно отслеживать:

- **Открытое множество** - это все узлы, которые мы сейчас рассматриваем. Это не каждый узел в системе, а каждый узел, из которого мы можем сделать следующий шаг.
- **Текущий лучший результат** - предполагаемый общий балл и текущий лучший предыдущий узел для каждого узла в системе.

В рамках этого нам нужно иметь возможность рассчитать два разных балла:

1. **Оценка перехода** - от одного узла к другому
2. **Эвристика** - позволяющая оценить стоимость пути от любого узла до пункта назначения

Эта оценка не обязательно должна быть точной, но большая точность даст лучшие результаты. Единственное требование состоит в том, чтобы обе оценки были согласованы друг с другом, т.е. они были в одних и тех же единицах измерения.

### Алгоритм

На каждой итерации мы будем:

1. Выбрать узел из нашего открытого набора с наименьшим оценочным общим баллом.
2. Удалить этот узел из открытого набора
3. Добавить в открытый набор все узлы, до которых мы можем добраться из него
4. Когда мы делаем это, мы также обрабатываем новую оценку от этого узла к каждому новому, чтобы увидеть, является ли это улучшением того, что у нас есть до сих пор, и если это так, мы обновляем то, что мы знаем об этом узле.

Затем это повторяется до тех пор, пока узел в нашем открытом наборе с наименьшим оценочным общим баллом не станет нашим пунктом назначения, и в этой точке мы получим наш маршрут.

## Java Implementation

### Интерфейсы и классы

Во-первых, нам нужно иметь возможность представить наш граф, который мы хотим пройти. Он состоит из двух классов - отдельных узлов и графа в целом.

Мы будем представлять наши отдельные узлы с помощью интерфейса GraphNode:

```java
public interface GraphNode {
    String getId();
}
```

Каждый из наших узлов должен иметь идентификатор. Все остальное специфично для этого конкретного графа и не требуется для общего решения.

Затем наш общий граф представляется классом, называемым просто Graph:

```java
import java.util.*;
import java.util.stream.Collectors;

public class Graph<T extends GraphNode> {
    private final Set<T> nodes;
    private final Map<String, Set<String>> connections;
    
    public Graph() {
        this.nodes = new HashSet<>();
        this.connections = new HashMap<>();
    }
    
    public void addNode(T node) {
        nodes.add(node);
        connections.putIfAbsent(node.getId(), new HashSet<>());
    }
    
    public void addConnection(T from, T to) {
        connections.get(from.getId()).add(to.getId());
    }
    
    public T getNode(String id) {
        return nodes.stream()
            .filter(node -> node.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No node found with ID"));
    }
    
    public Set<T> getConnections(T node) {
        return connections.get(node.getId()).stream()
            .map(this::getNode)
            .collect(Collectors.toSet());
    }
}
```

Он хранит все узлы в нашем графе и знает, какие узлы к каким подключаются. Затем мы можем получить любой узел по идентификатору или все узлы, подключенные к данному узлу.

Следующее, что нам нужно, это наш механизм поиска маршрутов через граф.

Первая часть - это какой-то способ сгенерировать оценку между любыми двумя узлами. Мы создадим интерфейс Scorer как для оценки следующего узла, так и для оценки места назначения:

```java
public interface Scorer<T extends GraphNode> {
    double computeCost(T from, T to);
}
```

Имея начальный и конечный узлы, мы затем получаем оценку за перемещение между ними.

Нам также нужна оболочка вокруг наших узлов, несущая некоторую дополнительную информацию. Вместо того, чтобы быть GraphNode, это RouteNode - потому что это узел в нашем вычисляемом маршруте, а не во всем графе:

```java
class RouteNode<T extends GraphNode> implements Comparable<RouteNode> {
    private final T current;
    private T previous;
    private double routeScore;
    private double estimatedScore;
    
    RouteNode(T current) {
        this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    RouteNode(T current, T previous, double routeScore, double estimatedScore) {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }
    
    @Override
    public int compareTo(RouteNode other) {
        if (this.estimatedScore > other.estimatedScore) {
            return 1;
        } else if (this.estimatedScore < other.estimatedScore) {
            return -1;
        } else {
            return 0;
        }
    }
    
    // Геттеры и сеттеры
    public T getCurrent() { return current; }
    public T getPrevious() { return previous; }
    public void setPrevious(T previous) { this.previous = previous; }
    public double getRouteScore() { return routeScore; }
    public void setRouteScore(double routeScore) { this.routeScore = routeScore; }
    public double getEstimatedScore() { return estimatedScore; }
    public void setEstimatedScore(double estimatedScore) { this.estimatedScore = estimatedScore; }
}
```

### Основной алгоритм

Теперь мы можем фактически генерировать наши маршруты по нашему графу. Это будет класс RouteFinder:

```java
import java.util.*;

public class RouteFinder<T extends GraphNode> {
    private final Graph<T> graph;
    private final Scorer<T> nextNodeScorer;
    private final Scorer<T> targetScorer;
    
    public RouteFinder(Graph<T> graph, Scorer<T> nextNodeScorer, Scorer<T> targetScorer) {
        this.graph = graph;
        this.nextNodeScorer = nextNodeScorer;
        this.targetScorer = targetScorer;
    }
    
    public List<T> findRoute(T from, T to) {
        Queue<RouteNode<T>> openSet = new PriorityQueue<>();
        Map<T, RouteNode<T>> allNodes = new HashMap<>();
        
        RouteNode<T> start = new RouteNode<>(
            from, 
            null, 
            0d, 
            targetScorer.computeCost(from, to)
        );
        openSet.add(start);
        allNodes.put(from, start);
        
        while (!openSet.isEmpty()) {
            RouteNode<T> next = openSet.poll();
            
            if (next.getCurrent().equals(to)) {
                List<T> route = new ArrayList<>();
                RouteNode<T> current = next;
                
                do {
                    route.add(0, current.getCurrent());
                    current = allNodes.get(current.getPrevious());
                } while (current != null);
                
                return route;
            }
            
            graph.getConnections(next.getCurrent()).forEach(connection -> {
                RouteNode<T> nextNode = allNodes.getOrDefault(
                    connection, 
                    new RouteNode<>(connection)
                );
                allNodes.put(connection, nextNode);
                
                double newScore = next.getRouteScore() + 
                    nextNodeScorer.computeCost(next.getCurrent(), connection);
                
                if (newScore < nextNode.getRouteScore()) {
                    nextNode.setPrevious(next.getCurrent());
                    nextNode.setRouteScore(newScore);
                    nextNode.setEstimatedScore(
                        newScore + targetScorer.computeCost(connection, to)
                    );
                    openSet.add(nextNode);
                }
            });
        }
        
        throw new IllegalStateException("No route found");
    }
}
```

## Пример использования

Наши узлы - это станции метро, и мы будем моделировать их с помощью класса Station:

```java
public class Station implements GraphNode {
    private final String id;
    private final String name;
    private final double latitude;
    private final double longitude;
    
    public Station(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
}
```

В этом сценарии нам нужна только одна реализация Scorer. Мы собираемся использовать для этого формулу Хаверсина, чтобы вычислить расстояние по прямой между двумя парами широта/долгота:

```java
public class HaversineScorer implements Scorer<Station> {
    private static final double R = 6372.8; // Радиус Земли в километрах
    
    @Override
    public double computeCost(Station from, Station to) {
        double dLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double dLon = Math.toRadians(to.getLongitude() - from.getLongitude());
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());
        
        double a = Math.pow(Math.sin(dLat / 2), 2) +
            Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        
        return R * c;
    }
}
```

Теперь мы можем использовать его для составления маршрута:

```java
Graph<Station> underground = new Graph<>();
// Добавляем станции и соединения

RouteFinder<Station> routeFinder = new RouteFinder<>(
    underground,
    new HaversineScorer(),
    new HaversineScorer()
);

List<Station> route = routeFinder.findRoute(
    underground.getNode("74"), 
    underground.getNode("7")
);

System.out.println(route.stream()
    .map(Station::getName)
    .collect(Collectors.toList()));
```

## Kotlin Implementation

### Интерфейсы и классы

```kotlin
interface GraphNodeK {
    fun getId(): String
}

class GraphK<T : GraphNodeK> {
    private val nodes = mutableSetOf<T>()
    private val connections = mutableMapOf<String, MutableSet<String>>()
    
    fun addNode(node: T) {
        nodes.add(node)
        connections.putIfAbsent(node.getId(), mutableSetOf())
    }
    
    fun addConnection(from: T, to: T) {
        connections[from.getId()]?.add(to.getId())
    }
    
    fun getNode(id: String): T {
        return nodes.firstOrNull { it.getId() == id }
            ?: throw IllegalArgumentException("No node found with ID")
    }
    
    fun getConnections(node: T): Set<T> {
        return connections[node.getId()]?.map { getNode(it) }?.toSet() ?: emptySet()
    }
}

interface ScorerK<T : GraphNodeK> {
    fun computeCost(from: T, to: T): Double
}

data class RouteNodeK<T : GraphNodeK>(
    val current: T,
    var previous: T? = null,
    var routeScore: Double = Double.POSITIVE_INFINITY,
    var estimatedScore: Double = Double.POSITIVE_INFINITY
) : Comparable<RouteNodeK<T>> {
    override fun compareTo(other: RouteNodeK<T>): Int {
        return estimatedScore.compareTo(other.estimatedScore)
    }
}
```

### Основной алгоритм

```kotlin
import java.util.*

class RouteFinderK<T : GraphNodeK>(
    private val graph: GraphK<T>,
    private val nextNodeScorer: ScorerK<T>,
    private val targetScorer: ScorerK<T>
) {
    fun findRoute(from: T, to: T): List<T> {
        val open = PriorityQueue<RouteNodeK<T>>()
        val allNodes = mutableMapOf<String, RouteNodeK<T>>()
        
        val start = RouteNodeK(from)
        start.routeScore = 0.0
        start.estimatedScore = targetScorer.computeCost(from, to)
        
        open.add(start)
        allNodes[from.getId()] = start
        
        while (!open.isEmpty()) {
            val next = open.poll()
            
            if (next.current.getId() == to.getId()) {
                val route = mutableListOf<T>()
                var current: RouteNodeK<T>? = next
                
                while (current != null) {
                    route.add(0, current.current)
                    current = allNodes[current.previous?.getId()]
                }
                
                return route
            }
            
            graph.getConnections(next.current).forEach { connection ->
                val newRouteScore = next.routeScore + nextNodeScorer.computeCost(next.current, connection)
                val routeNode = allNodes.getOrDefault(
                    connection.getId(),
                    RouteNodeK(connection)
                )
                allNodes[connection.getId()] = routeNode
                
                if (newRouteScore < routeNode.routeScore) {
                    routeNode.previous = next.current
                    routeNode.routeScore = newRouteScore
                    routeNode.estimatedScore = newRouteScore + targetScorer.computeCost(connection, to)
                    open.add(routeNode)
                }
            }
        }
        
        throw IllegalStateException("No route found")
    }
}
```

### Пример использования

```kotlin
data class StationK(val name: String) : GraphNodeK {
    override fun getId(): String = name
}

fun main() {
    val graph = GraphK<StationK>()
    val station1 = StationK("Station1")
    val station2 = StationK("Station2")
    val station3 = StationK("Station3")
    
    graph.addNode(station1)
    graph.addNode(station2)
    graph.addNode(station3)
    graph.addConnection(station1, station2)
    graph.addConnection(station2, station3)
    
    val routeFinder = RouteFinderK(
        graph,
        object : ScorerK<StationK> {
            override fun computeCost(from: StationK, to: StationK): Double = 1.0
        },
        object : ScorerK<StationK> {
            override fun computeCost(from: StationK, to: StationK): Double = 1.0
        }
    )
    
    val route = routeFinder.findRoute(station1, station3)
    println(route.map { it.name }) // [Station1, Station2, Station3]
}
```

## Сложность

### Временная сложность

- **В худшем случае:** O(b^d) - где b - коэффициент ветвления, d - глубина решения
- **В лучшем случае:** O(d) - когда эвристика идеальна

### Пространственная сложность

- **В худшем случае:** O(b^d) - для хранения всех узлов в открытом множестве
- **В лучшем случае:** O(d) - для хранения пути решения

## Особенности

- **Оптимальность:** A* гарантирует нахождение оптимального пути, если эвристика допустима
- **Эффективность:** Более эффективен, чем Дейкстра, благодаря эвристике
- **Гибкость:** Легко адаптировать для различных типов графов

## Применение

Алгоритм A* используется в:

- Играх (поиск пути NPC)
- Навигационных системах
- Робототехнике
- ИИ и планировании
- Сетевых алгоритмах

## Варианты задачи

### Вариант 1: A* с различными эвристиками

```java
// Манхэттенское расстояние для сетки
public class ManhattanDistance implements Scorer<GridNode> {
    @Override
    public double computeCost(GridNode from, GridNode to) {
        return Math.abs(from.getX() - to.getX()) + 
               Math.abs(from.getY() - to.getY());
    }
}

// Евклидово расстояние
public class EuclideanDistance implements Scorer<GridNode> {
    @Override
    public double computeCost(GridNode from, GridNode to) {
        double dx = from.getX() - to.getX();
        double dy = from.getY() - to.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
```

### Вариант 2: A* с весами

```java
public class WeightedAStar<T extends GraphNode> extends RouteFinder<T> {
    private final double weight;
    
    public WeightedAStar(
        Graph<T> graph, 
        Scorer<T> nextNodeScorer, 
        Scorer<T> targetScorer,
        double weight
    ) {
        super(graph, nextNodeScorer, targetScorer);
        this.weight = weight;
    }
    
    // Переопределить оценку с учетом веса
    // f(n) = g(n) + weight * h(n)
}
```

## Когда использовать

### Используйте A*, когда:

- Нужен оптимальный путь
- Есть хорошая эвристика
- Граф не слишком большой
- Важна эффективность

### Не используйте A*, когда:

- Нужен только приблизительный путь
- Эвристика плохая или отсутствует
- Граф очень большой (рассмотрите другие алгоритмы)

## Заключение

В этой статье мы увидели, что такое алгоритм A*, как он работает и как его реализовать в наших собственных проектах. Алгоритм A* является мощным инструментом для поиска оптимальных путей в графах и широко используется в различных областях.
