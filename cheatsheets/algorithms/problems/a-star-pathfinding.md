---
title: "Поиск пути A* (A* Pathfinding Algorithm)"
description: "Поиск оптимального пути в графе с эвристикой: открытое множество (приоритетная очередь по f = g + h), обход соседей, восстановление маршрута. Универсальная реализация и пример для графа станций (Haversine). Java и Kotlin."
tags:
  - algorithms
  - problems
  - a-star-pathfinding
type: "reference"
difficulty: "intermediate"
aliases:
  - "Поиск пути A*"
  - "A* Pathfinding Algorithm"
  - "a star pathfinding"
  - "A*"
prerequisites:
  - "[[dijkstra]]"
next: []
updated: "2026-04-20"
---
# Поиск пути A* (A* Pathfinding Algorithm)

Поиск оптимального пути в графе с эвристикой: открытое множество (приоритетная очередь по f = g + h), обход соседей, восстановление маршрута. Универсальная реализация и пример для графа станций (Haversine). Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Baeldung: A* Pathfinding](https://www.baeldung.com/)

### См. также
- [Задача коммивояжера](traveling-salesman-problem.md) — TSP
- [Решение лабиринтов](maze-solver.md) — maze solver
- [Алгоритм Дейкстры](../graphs/dijkstra.md) — Dijkstra

- [OptaPlanner](optaplanner.md)
- [Задача о рюкзаке (Knapsack Problem)](knapsack-problem.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Основные концепции](#основные-концепции)
- [Реализация на Java](#реализация-на-java)
- [Пример использования](#пример-использования)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: A* с различными эвристиками](#вариант-1-a-с-различными-эвристиками)
  - [Вариант 2: A* с весами](#вариант-2-a-с-весами)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Поиск пути в графе — нахождение последовательности рёбер от старта до цели; у рёбер может быть стоимость. A* (Харт, Нильссон, Рафаэль, 1968) — алгоритм с эвристикой: на каждом шаге выбирается узел с минимальной оценкой f = g + h (g — стоимость пути от старта, h — оценка до цели). Подходит, когда маршруты не предвычисляются и память не жёстко ограничена. Граф задаётся узлами и связями; каждая связь имеет стоимость (например, расстояние).

## Основные концепции

A* — вариант Дейкстры с эвристикой h: выбор следующего узла по минимальному f = g + h ускоряет сходимость. Открытое множество — узлы-кандидаты (обычно приоритетная очередь по f). Для каждого узла храним лучший достигнутый путь (g) и предыдущий узел. Два скора: стоимость перехода между соседями (g) и эвристика до цели (h); h должна быть допустимой (не переоценивать). В худшем случае время и память O(b^d) (b — ветвление, d — глубина решения); при хорошей эвристике — существенно меньше. На каждой итерации: извлечь узел с минимальным f; если это цель — построить путь назад по ссылкам previous; иначе добавить соседей в открытое множество и обновить их g/f при улучшении.

## Реализация на Java

Узел графа — интерфейс с идентификатором (`GraphNode`). Граф хранит узлы и связи между ними (`Graph`). Для A* нужны два скора: стоимость перехода между соседями и эвристика до цели (`Scorer`). Узел в маршруте (`RouteNode`) хранит текущий узел, предыдущий, g и f.

```java
// Узел графа с уникальным идентификатором для A*
public interface GraphNode {
    String getId();
}
```


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


```java
public interface Scorer<T extends GraphNode> {
    double computeCost(T from, T to);
}
```


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

`RouteFinder`: приоритетная очередь открытого множества по f, карта всех посещённых RouteNode. Старт с g=0, f=h(start, goal). Цикл: извлечь узел с минимальным f; если это цель — восстановить путь по previous; иначе для каждого соседа обновить g и f и добавить в очередь при улучшении.

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

Узлы — станции (`Station` с id, именем, координатами). Один `Scorer` для перехода и эвристики — формула Хаверсина (расстояние по прямой в км). Строим граф, создаём `RouteFinder` с этим скорером, вызываем `findRoute(from, to)`.

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

## Реализация на Kotlin

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

В худшем случае время и память O(b^d) (b — ветвление, d — глубина). При хорошей эвристике — существенно меньше; при идеальной h — порядок O(d).

## Особенности

При допустимой эвристике A* даёт оптимальный путь. Благодаря h обычно исследует меньше узлов, чем Дейкстра. Подходит для любых графов с неотрицательными весами и заданной эвристикой.

## Применение

Игры (NPC), навигация, робототехника, ИИ и планирование, сетевые алгоритмы.

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

A* уместен, когда нужен оптимальный путь и есть допустимая эвристика; граф не обязательно маленький, но при очень большом ветвлении рассмотрите приближённые или специализированные алгоритмы. Не используйте при отрицательных весах рёбер — оптимальность не гарантируется.

## Лучшие практики

Эвристика должна быть допустимой (не переоценивать); для сетки — манхэттенское или евклидово расстояние; для дорог — оценка по прямой. Открытое множество — приоритетная очередь по f (min-heap), O(log n) на шаг. Не обрабатывать один и тот же узел повторно с худшим g. Завершать при достижении цели или при пустом открытом множестве (путь не найден). Веса рёбер — неотрицательные.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Путь не оптимален | Эвристика переоценивает (недопустима) | Сделать h допустимой: h(n) ≤ реальная стоимость до цели |
| No route found при существующем пути | Цель недостижима из старта или ошибка в графе | Проверить связность; логировать открытое множество на последней итерации |
| Медленная работа | Слабая эвристика или много узлов | Улучшить h; рассмотреть weighted A* (f = g + ε·h) для приближённого быстрого пути |

## Частые вопросы

**Чем A* отличается от Дейкстры?** A* использует эвристику h и выбирает узел по f = g + h; Дейкстра — только по g. При допустимой h A* находит тот же оптимальный путь, но обычно быстрее.

**Нужно ли хранить закрытое множество?** Достаточно не добавлять в открытое множество узел с худшим g; можно обновлять запись в очереди при улучшении g. Явный закрытый набор упрощает «не обрабатывать повторно».

**Когда использовать weighted A*?** Когда допустимо приближённое решение: f = g + ε·h с ε > 1 даёт более быстрый, но не обязательно оптимальный путь; полезно в играх и реальном времени.

## Заключение

A* — поиск оптимального пути в графе с эвристикой; реализация через приоритетную очередь по f и обновление соседей. Широко применяется в играх, навигации и планировании при наличии допустимой эвристики.
