# Алгоритм Дейкстры

Кратко: алгоритм Дейкстры - алгоритм поиска кратчайшего пути от начальной вершины до всех остальных вершин во взвешенном графе с неотрицательными весами ребер.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Dijkstra's Algorithm](https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-greedy-algo-7/)

### Визуализация
- [Visualgo: Dijkstra](https://visualgo.net/en/sssp)

### См. также
- `./bfs.md` - поиск в ширину
- `./jgrapht.md` - библиотека JGraphT для работы с графами

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Принцип работы](#принцип-работы)
- [Инициализация](#инициализация)
- [Процесс оценки](#процесс-оценки)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Акцент в этой статье делается на задаче о кратчайшем пути (SPP), являющейся одной из фундаментальных теоретических проблем, известных в теории графов, и на том, как алгоритм Дейкстры может быть использован для ее решения.

Основная цель алгоритма - определить кратчайший путь между начальным узлом и остальной частью графа. Учитывая положительно взвешенный граф и начальный узел (A), Дейкстра определяет кратчайший путь и расстояние от источника до всех пунктов назначения в графе.

Основная идея алгоритма Дейкстры состоит в том, чтобы постоянно исключать более длинные пути между начальным узлом и всеми возможными пунктами назначения.

## Принцип работы

Чтобы отслеживать процесс, нам нужно иметь два различных набора узлов: установленные и неустановленные.

**Установленные узлы** - это узлы с известным минимальным расстоянием от источника. Набор неустановленных узлов собирает узлы, до которых мы можем добраться из источника, но мы не знаем минимального расстояния от начального узла.

Вот список шагов, которые нужно выполнить, чтобы решить SPP с Дейкстрой:

1. Установите расстояние до startNode равным нулю
2. Установите для всех остальных расстояний бесконечное значение
3. Мы добавляем startNode в набор неустановленных узлов
4. Пока набор неустановленных узлов не пуст, мы:
   - Выберите узел оценки из набора неустановленных узлов, узел оценки должен быть с наименьшим расстоянием от источника
   - Рассчитайте новые расстояния до прямых соседей, сохраняя наименьшее расстояние при каждой оценке
   - Добавьте соседей, которые еще не установлены, в набор неустановленных узлов

Эти шаги можно объединить в два этапа: инициализацию и оценку.

## Инициализация

Прежде чем мы начнем исследовать все пути в графе, нам сначала нужно инициализировать все узлы с бесконечным расстоянием и неизвестным предшественником, кроме источника.

В рамках процесса инициализации нам нужно присвоить значение 0 узлу A (мы знаем, что расстояние от узла A до узла A равно 0).

Таким образом, каждый узел в остальной части графа будет отличаться предшественником и расстоянием.

Чтобы завершить процесс инициализации, нам нужно добавить узел A к неустановленным узлам, чтобы он был выбран первым на этапе оценки. Имейте в виду, набор установленных узлов все еще пуст.

## Процесс оценки

Теперь, когда мы инициализировали наш граф, мы выбираем узел с наименьшим расстоянием из неустановленного набора, затем мы оцениваем все соседние узлы, которые не находятся в установленных узлах.

Идея состоит в том, чтобы добавить вес ребра к расстоянию до узла оценки, а затем сравнить его с расстоянием до пункта назначения. Например, для узла B 0+10 меньше, чем БЕСКОНЕЧНОСТЬ, поэтому новое расстояние для узла B равно 10, а новым предшественником является A, то же самое относится к узлу C.

Затем узел А перемещается из набора неустановленных узлов в установленные узлы.

Узлы B и C добавляются к неустановленным узлам, поскольку до них можно добраться, но их необходимо оценить.

Теперь, когда у нас есть два узла в неустановленном наборе, мы выбираем узел с наименьшим расстоянием (узел B), затем повторяем, пока не установим все узлы в графе.

### Таблица итераций

Вот таблица, в которой обобщаются итерации, выполненные на этапах оценки:

| Iteration | Unsettled | Settled | EvaluationNode | A | B | C | D | E | F |
|-----------|-----------|---------|-----------------|---|---|---|---|---|---|
| 1 | A | - | A | 0 | A - 10 | A - 15 | X - ∞ | X - ∞ | X - ∞ |
| 2 | B, C | A | B | 0 | A - 10 | A - 15 | B - 22 | X - ∞ | B - 25 |
| 3 | C, F, D | A, B | C | 0 | A - 10 | A - 15 | B - 22 | C - 25 | B - 25 |
| 4 | D, E, F | A, B, C | D | 0 | A - 10 | A - 15 | B - 22 | D - 24 | D - 23 |
| 5 | E, F | A, B, C, D | F | 0 | A - 10 | A - 15 | B - 22 | D - 24 | D - 23 |
| 6 | E | A, B, C, D, F | E | 0 | A - 10 | A - 15 | B - 22 | D - 24 | D - 23 |
| Final | - | ALL | NONE | 0 | A - 10 | A - 15 | B - 22 | D - 24 | D - 23 |

Обозначение B - 22, например, означает, что узел B является непосредственным предшественником с общим расстоянием 22 от узла A.

### Результаты

Наконец, мы можем вычислить кратчайшие пути из узла A следующим образом:

- Узел B: A → B (общее расстояние = 10)
- Узел C: A → C (общее расстояние = 15)
- Узел D: A → B → D (общее расстояние = 22)
- Узел E: A → B → D → E (общее расстояние = 24)
- Узел F: A → B → D → F (общее расстояние = 23)

## Java Implementation

### Реализация

В этой простой реализации мы будем представлять граф как набор узлов:

```java
public class Graph {
    private Set<Node> nodes = new HashSet<>();
    
    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }
}
```

Узел может быть описан с помощью имени, LinkedList относительно кратчайшего пути, расстояния от источника и списка смежности с именем смежных узлов:

```java
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
```

Атрибут смежных узлов используется для связывания непосредственных соседей с длиной ребра. Это упрощенная реализация списка смежности, которая больше подходит для алгоритма Дейкстры, чем для матрицы смежности.

Что касается атрибута shortestPath, то это список узлов, описывающий кратчайший путь, рассчитанный от начального узла.

По умолчанию все расстояния между узлами инициализируются с помощью `Integer.MAX_VALUE` для имитации бесконечного расстояния, как описано на этапе инициализации.

Теперь давайте реализуем алгоритм Дейкстры:

```java
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

Метод `getLowestDistanceNode()` возвращает узел с наименьшим расстоянием от набора неустановленных узлов, а метод `calculateMinimumDistance()` сравнивает фактическое расстояние с вновь рассчитанным при следовании по вновь исследованному пути:

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

### Пример использования

Теперь, когда все необходимое готово, давайте применим алгоритм Дейкстры к образцу графа:

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

После расчета атрибуты `shortestPath` и `Distance` устанавливаются для каждого узла в графе, мы можем выполнить итерацию по ним, чтобы убедиться, что результаты точно соответствуют тому, что было найдено в предыдущем разделе.

## Kotlin Implementation

### Класс Node

```kotlin
class NodeK(val name: String) {
    val shortestPath = mutableListOf<NodeK>()
    var distance: Int = Int.MAX_VALUE
    val adjacentNodes = mutableMapOf<NodeK, Int>()
    
    fun addDestination(destination: NodeK, distance: Int) {
        adjacentNodes[destination] = distance
    }
}
```

### Класс Graph

```kotlin
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

### Пример использования

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

### Временная сложность

- **С простой реализацией:** O(V²), где V - количество вершин
- **С приоритетной очередью (кучей):** O((V+E) log V), где E - количество ребер

В простой реализации для каждой вершины мы проходим по всем неустановленным вершинам, чтобы найти минимальное расстояние, что дает O(V²).

С использованием приоритетной очереди (например, Fibonacci heap) сложность улучшается до O((V+E) log V).

### Пространственная сложность

- **Все случаи:** O(V) - для хранения расстояний и предшественников для каждой вершины

## Особенности

- **Жадный алгоритм:** Алгоритм Дейкстры является жадным алгоритмом - на каждом шаге выбирает локально оптимальное решение
- **Только неотрицательные веса:** Алгоритм работает только для графов с неотрицательными весами ребер
- **Однократный запуск:** Находит кратчайшие пути от одной вершины до всех остальных за один запуск
- **Оптимальность:** Гарантирует нахождение кратчайшего пути при неотрицательных весах

## Ограничения

- **Не работает с отрицательными весами:** Если в графе есть ребра с отрицательными весами, алгоритм Дейкстры может дать неверный результат
- **Для отрицательных весов:** Используйте алгоритм Беллмана-Форда
- **Для всех пар вершин:** Используйте алгоритм Флойда-Уоршелла

## Применение

Алгоритм Дейкстры широко используется в:

- Маршрутизации в сетях (протоколы OSPF, IS-IS)
- GPS навигации
- Социальных сетях (поиск кратчайших связей)
- Игровой разработке (pathfinding)
- Логистике и планировании маршрутов
