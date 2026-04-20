---
title: "Алгоритм Дейкстры (Dijkstra's Algorithm)"
description: "Поиск кратчайших путей от одной вершины до всех остальных во взвешенном графе с неотрицательными весами. Жадный выбор вершины с минимальным текущим расстоянием, релаксация рёбер. Реализация в Java и Kotlin."
tags:
  - algorithms
  - graphs
  - dijkstra
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Алгоритм Дейкстры (`Dijkstra's Algorithm`)

Поиск кратчайших путей от одной вершины до всех остальных во взвешенном графе с неотрицательными весами. Жадный выбор вершины с минимальным текущим расстоянием, релаксация рёбер. Реализация в Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Dijkstra's Algorithm — GeeksforGeeks](https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-greedy-algo-7/)

### Визуализация
- [`Visualgo`: `Dijkstra`](https://visualgo.net/)

### См. также
- [[bfs|bfs.md]] — поиск в ширину
- [[jgrapht|jgrapht.md]] — библиотека JGraphT

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Принцип работы](#принцип-работы)
- [Реализация на Java](#реализация-на-java)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Ограничения](#ограничения)
- [Применение](#применение)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)


## Описание алгоритма

Задача кратчайшего пути (SPP): найти кратчайшие пути от одной вершины до всех остальных. Алгоритм Дейкстры решает её для графов с неотрицательными весами рёбер: на каждом шаге выбирается вершина с минимальным текущим расстоянием, релаксируются исходящие рёбра, вершина помечается как обработанная.

## Принцип работы

Два множества: установленные (минимальное расстояние уже найдено) и неустановленные вершины. Инициализация: расстояние до источника 0, до остальных — бесконечность; источник в неустановленных. Цикл: из неустановленных выбираем вершину с минимальным расстоянием; для каждого соседа проверяем, не улучшится ли путь (расстояние до текущей + вес ребра); при улучшении обновляем расстояние и предшественника; соседа добавляем в неустановленные; текущую вершину переносим в установленные. Повторяем, пока неустановленные не пусты.

### Таблица итераций

| Iteration | Unsettled | Settled | EvaluationNode | A | B | C | D | E | F |
|-----------|-----------|---------|-----------------|---|---|---|---|---|---|
| 1 | A | - | A | 0 | A — 10 | A — 15 | X - ∞ | X - ∞ | X - ∞ |
| 2 | B, C | A | B | 0 | A — 10 | A — 15 | B — 22 | X - ∞ | B — 25 |
| 3 | C, F, D | A, B | C | 0 | A — 10 | A — 15 | B — 22 | C — 25 | B — 25 |
| 4 | D, E, F | A, B, C | D | 0 | A — 10 | A — 15 | B — 22 | D — 24 | D — 23 |
| 5 | E, F | A, B, C, D | F | 0 | A — 10 | A — 15 | B — 22 | D — 24 | D — 23 |
| 6 | E | A, B, C, D, F | E | 0 | A — 10 | A — 15 | B — 22 | D — 24 | D — 23 |
| Final | - | ALL | NONE | 0 | A — 10 | A — 15 | B — 22 | D — 24 | D — 23 |

Обозначение B — 22: предшественник B, суммарное расстояние от A равно 22. Итоговые пути: B 10, C 15, D 22 (A→B→D), E 24 (A→B→D→E), F 23 (A→B→D→F).

## Реализация на Java

Граф — множество узлов; у узла: имя, список смежности (сосед → вес), текущее расстояние и цепочка предшественников для восстановления пути.

```java
// Граф — множество узлов; у каждого узла список смежности и расстояние до источника
public class Graph {
    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }
}

// Узел: имя, shortestPath (цепочка до источника), distance, adjacentNodes (сосед → вес ребра)
public class Node {
    private String name;
    private List<Node> shortestPath = new LinkedList<>();
    private Integer distance = Integer.MAX_VALUE;
    Map<Node, Integer> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, int distance) {
        adjacentNodes.put(destination, distance);
    }

    public Node(String name) {
        this.name = name;
    }
}

// Источник: distance=0. Цикл: выбираем вершину с мин. расстоянием из unsettled, релаксируем рёбра к соседям, переносим вершину в settled
public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
    source.setDistance(0);

    Set<Node> settledNodes = new HashSet<>();
    Set<Node> unsettledNodes = new HashSet<>();

    unsettledNodes.add(source);

    while (unsettledNodes.size() != 0) {
        Node currentNode = getLowestDistanceNode(unsettledNodes);
        unsettledNodes.remove(currentNode);

        for (Entry<Node, Integer> adjacencyPair :
             currentNode.getAdjacentNodes().entrySet()) {
            Node adjacentNode = adjacencyPair.getKey();
            Integer edgeWeight = adjacencyPair.getValue();

            if (!settledNodes.contains(adjacentNode)) {
                calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                unsettledNodes.add(adjacentNode);
            }
        }

        settledNodes.add(currentNode);
    }

    return graph;
}
```

**Метод `**getLowestDistanceNode**()` возвращает узел с наименьшим расстоянием от набора неустановленных узлов, а метод `**calculateMinimumDistance**()` сравнивает фактическое расстояние с вновь рассчитанным при следовании по вновь исследованному пути:**

```java
private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
    Node lowestDistanceNode = null;
    int lowestDistance = Integer.MAX_VALUE;

    for (Node node : unsettledNodes) {
        int nodeDistance = node.getDistance();
        if (nodeDistance < lowestDistance) {
            lowestDistance = nodeDistance;
            lowestDistanceNode = node;
        }
    }

    return lowestDistanceNode;
}

private static void calculateMinimumDistance(Node evaluationNode,
                                             Integer edgeWeight,
                                             Node sourceNode) {
    Integer sourceDistance = sourceNode.getDistance();

    if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
        evaluationNode.setDistance(sourceDistance + edgeWeight);
        LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
        shortestPath.add(sourceNode);
        evaluationNode.setShortestPath(shortestPath);
    }
}
```

### Пример использования (Java)

```java
Node nodeA = new Node("A");
Node nodeB = new Node("B");
Node nodeC = new Node("C");
Node nodeD = new Node("D");
Node nodeE = new Node("E");
Node nodeF = new Node("F");

nodeA.addDestination(nodeB, 10);
nodeA.addDestination(nodeC, 15);
nodeB.addDestination(nodeD, 12);
nodeB.addDestination(nodeF, 15);
nodeC.addDestination(nodeE, 10);
nodeD.addDestination(nodeE, 2);
nodeD.addDestination(nodeF, 1);
nodeF.addDestination(nodeE, 5);

Graph graph = new Graph();
graph.addNode(nodeA);
graph.addNode(nodeB);
graph.addNode(nodeC);
graph.addNode(nodeD);
graph.addNode(nodeE);
graph.addNode(nodeF);

graph = Dijkstra.calculateShortestPathFromSource(graph, nodeA);
```

После вызова у каждого узла заполнены `shortestPath` и `distance`.

## Реализация на Kotlin

```kotlin
class NodeK(val name: String) {
    val shortestPath = mutableListOf<NodeK>()
    var distance: Int = Int.MAX_VALUE
    val adjacentNodes = mutableMapOf<NodeK, Int>()

    fun addDestination(destination: NodeK, distance: Int) {
        adjacentNodes[destination] = distance
    }
}

class GraphK {
    val nodes = mutableSetOf<NodeK>()

    fun addNode(node: NodeK) {
        nodes.add(node)
    }
}
```

### Алгоритм Дейкстры

```kotlin
object DijkstraK {
    fun calculateShortestPathFromSource(graph: GraphK, source: NodeK): GraphK {
        source.distance = 0

        val settledNodes = mutableSetOf<NodeK>()
        val unsettledNodes = mutableSetOf<NodeK>()

        unsettledNodes.add(source)

        while (unsettledNodes.isNotEmpty()) {
            val currentNode = getLowestDistanceNode(unsettledNodes)
            unsettledNodes.remove(currentNode)

            for ((adjacentNode, edgeWeight) in currentNode.adjacentNodes) {
                if (adjacentNode !in settledNodes) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode)
                    unsettledNodes.add(adjacentNode)
                }
            }

            settledNodes.add(currentNode)
        }

        return graph
    }

    private fun getLowestDistanceNode(unsettledNodes: Set<NodeK>): NodeK {
        var lowestDistanceNode: NodeK? = null
        var lowestDistance = Int.MAX_VALUE

        for (node in unsettledNodes) {
            val nodeDistance = node.distance
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance
                lowestDistanceNode = node
            }
        }

        return lowestDistanceNode!!
    }

    private fun calculateMinimumDistance(
        evaluationNode: NodeK,
        edgeWeight: Int,
        sourceNode: NodeK
    ) {
        val sourceDistance = sourceNode.distance

        if (sourceDistance + edgeWeight < evaluationNode.distance) {
            evaluationNode.distance = sourceDistance + edgeWeight
            val shortestPath = mutableListOf<NodeK>()
            shortestPath.addAll(sourceNode.shortestPath)
            shortestPath.add(sourceNode)
            evaluationNode.shortestPath.clear()
            evaluationNode.shortestPath.addAll(shortestPath)
        }
    }
}
```

### Пример использования (Kotlin)

```kotlin
fun main() {
    val nodeA = NodeK("A")
    val nodeB = NodeK("B")
    val nodeC = NodeK("C")
    val nodeD = NodeK("D")
    val nodeE = NodeK("E")
    val nodeF = NodeK("F")

    nodeA.addDestination(nodeB, 10)
    nodeA.addDestination(nodeC, 15)
    nodeB.addDestination(nodeD, 12)
    nodeB.addDestination(nodeF, 15)
    nodeC.addDestination(nodeE, 10)
    nodeD.addDestination(nodeE, 2)
    nodeD.addDestination(nodeF, 1)
    nodeF.addDestination(nodeE, 5)

    val graph = GraphK()
    graph.addNode(nodeA)
    graph.addNode(nodeB)
    graph.addNode(nodeC)
    graph.addNode(nodeD)
    graph.addNode(nodeE)
    graph.addNode(nodeF)

    DijkstraK.calculateShortestPathFromSource(graph, nodeA)

    println("Distance to B: ${nodeB.distance}") // 10
    println("Distance to D: ${nodeD.distance}") // 22
}
```

## Сложность

Простая реализация (перебор неустановленных для выбора минимума): время O(V²). С приоритетной очередью (min-heap): O((V+E) log V). Память O(V) для расстояний и предшественников.

## Особенности

Жадный алгоритм: на каждом шаге выбирается вершина с минимальным текущим расстоянием. Работает только при неотрицательных весах. За один запуск находит пути от одной вершины до всех остальных. При неотрицательных весах результат оптимален.

## Ограничения

При отрицательных весах рёбер Дейкстра может дать неверный результат; для таких графов — Беллман-Форд. Для кратчайших путей между всеми парами вершин — Флойд-Уоршелл.

## Применение

Маршрутизация в сетях (OSPF, IS-IS), GPS-навигация, соцсети, игры (pathfinding), логистика.

## Лучшие практики

Используйте только при неотрицательных весах; при отрицательных — Беллман-Форд. Для ускорения — приоритетная очередь по расстоянию (O((V+E) log V)); простая реализация O(V²). Инициализация: источник 0, остальные — бесконечность (при риске переполнения — long или BigInteger). Восстановление пути: хранить предшественника при обновлении расстояния. Тестировать на одном узле, цепочке, несвязном графе (недостижимые остаются с бесконечным расстоянием).

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Неверные расстояния при отрицательных рёбрах | Дейкстра не поддерживает отрицательные веса | Использовать Беллмана-Форда |
| Медленная работа на больших графах | Линейный поиск минимума в unsettled | Приоритетная очередь (PriorityQueue) по distance |
| Переполнение при больших весах | Integer overflow при суммировании | Использовать long или BigInteger для расстояний |

## Частые вопросы

**Почему Дейкстра не работает с отрицательными весами?** Жадный выбор вершины с минимальным расстоянием предполагает, что это расстояние уже окончательное; при отрицательных рёбрах позже может появиться более короткий путь через другую вершину.

**Когда использовать приоритетную очередь?** Всегда для плотных или больших графов: выбор минимума за O(log V) вместо O(V) сильно ускоряет алгоритм.

**Как восстановить путь до конкретной вершины?** Хранить для каждой вершины предшественника (parent); путь собирается от цели к источнику по цепочке предшественников.
