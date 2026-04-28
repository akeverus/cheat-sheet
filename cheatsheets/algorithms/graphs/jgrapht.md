---
title: "JGraphT — библиотека для работы с графами (JGraphT)"
description: "Библиотека Java с открытым исходным кодом: типы графов (простые, ориентированные, взвешенные, мультиграфы), обход (BFS, DFS), кратчайшие пути (Дейкстра, Беллман-Форд), сильно связные компоненты, Эйлеровы и гамильтоновы циклы, визуализация."
tags:
  - algorithms
  - graphs
  - jgrapht
type: "reference"
difficulty: "intermediate"
aliases:
  - "JGraphT"
prerequisites: []
next: []
updated: "2026-04-20"
---
# JGraphT — библиотека для работы с графами (JGraphT)

Библиотека Java с открытым исходным кодом: типы графов (простые, ориентированные, взвешенные, мультиграфы), обход (BFS, DFS), кратчайшие пути (Дейкстра, Беллман-Форд), сильно связные компоненты, Эйлеровы и гамильтоновы циклы, визуализация.

## Полезные ссылки

### Официальная документация
- [JGraphT Documentation](https://jgrapht.org/guide/UserOverview)
- [JGraphT GitHub](https://github.com/jgrapht/jgrapht)

### См. также
- [dijkstra.md](dijkstra.md) — алгоритм Дейкстры
- [bfs.md](bfs.md) — поиск в ширину

- [Конвертация римских и арабских чисел (Roman-Arabic Numeral Conversion)](../strings/roman-arabic-numeral-conversion.md)
- [Баланс скобок (Balanced Parentheses)](../strings/balanced-parentheses.md)
- [Поиск по суффиксному дереву (Suffix Tree Pattern Matching)](../strings/suffix-tree-pattern-matching.md)
## Содержание

- [Введение](#введение)
- [Установка](#установка)
- [Типы графов](#типы-графов)
- [Реализация на Java](#реализация-на-java)
  - [Создание графов](#создание-графов)
- [Обход графов](#обход-графов)
- [Алгоритмы поиска пути](#алгоритмы-поиска-пути)
- [Сильно связные компоненты](#сильно-связные-компоненты)
- [Эйлеровы и гамильтоновы циклы](#эйлеровы-и-гамильтоновы-циклы)
  - [Эйлеров цикл](#эйлеров-цикл)
  - [Гамильтонов цикл](#гамильтонов-цикл)
  - [Обнаружение циклов](#обнаружение-циклов)
- [Визуализация графов](#визуализация-графов)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Обход графов](#обход-графов-1)
  - [Алгоритмы поиска пути](#алгоритмы-поиска-пути-1)
  - [Пример использования](#пример-использования)
- [Лучшие практики](#лучшие-практики)
- [Основные возможности](#основные-возможности)
- [Применение](#применение)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение

JGraphT — Java-библиотека с открытым исходным кодом: типы графов и готовые алгоритмы (обход, кратчайшие пути, компоненты связности, циклы). Ниже — установка, создание графов, обход, алгоритмы пути и визуализация.

## Установка

Зависимость Maven:

```xml
<dependency>
    <groupId>org.jgrapht</groupId>
    <artifactId>jgrapht-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

Для экспорта в изображения и DOT — `jgrapht-ext`:

```xml
<dependency>
    <groupId>org.jgrapht</groupId>
    <artifactId>jgrapht-ext</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Типы графов

Поддерживаются: простые графы (без петель и кратных рёбер), ориентированные и неориентированные, взвешенные и невзвешенные, мультиграфы (несколько рёбер между парой вершин), немодифицируемые и прослушиваемые графы.

## Реализация на Java

### Создание графов

```java
// Создание простого неориентированного графа: вершины и рёбра через JGraphT API.
Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
g.addVertex("v1");
g.addVertex("v2");
g.addEdge("v1", "v2");
```


```java
// Ориентированный граф: DefaultDirectedGraph
DirectedGraph<String, DefaultEdge> directedGraph =
    new DefaultDirectedGraph<>(DefaultEdge.class);

directedGraph.addVertex("v1");
directedGraph.addVertex("v2");
directedGraph.addVertex("v3");
directedGraph.addEdge("v1", "v2");
```

```java
// Полный граф заданного размера через CompleteGraphGenerator и VertexFactory
public void createCompleteGraph() {
    completeGraph = new SimpleWeightedGraph<>(DefaultEdge.class);
    CompleteGraphGenerator<String, DefaultEdge> completeGenerator =
        new CompleteGraphGenerator<>(size);

    VertexFactory<String> vFactory = new VertexFactory<String>() {
        private int id = 0;

        public String createVertex() {
            return "v" + id++;
        }
    };

    completeGenerator.generateGraph(completeGraph, vFactory, null);
}
```

```java
// Мультиграф: несколько рёбер между v1 и v2 с разными весами
public void createMultiGraphWithWeightedEdges() {
    multiGraph = new Multigraph<>(DefaultWeightedEdge.class);

    multiGraph.addVertex("v1");
    multiGraph.addVertex("v2");

    DefaultWeightedEdge edge1 = multiGraph.addEdge("v1", "v2");
    multiGraph.setEdgeWeight(edge1, 5);

    DefaultWeightedEdge edge2 = multiGraph.addEdge("v1", "v2");
    multiGraph.setEdgeWeight(edge2, 3);
}
```

## Обход графов

Итераторы: `BreadthFirstIterator`, `DepthFirstIterator`, `ClosestFirstIterator`, `RandomWalkIterator`. Создаём итератор от графа, обходим через `hasNext()`/`next()`.

```java
DepthFirstIterator<String, DefaultEdge> depthFirstIterator =
    new DepthFirstIterator<>(directedGraph);

BreadthFirstIterator<String, DefaultEdge> breadthFirstIterator =
    new BreadthFirstIterator<>(directedGraph);
```

```java
while (breadthFirstIterator.hasNext()) {
    String vertex = breadthFirstIterator.next();
    // Обработка вершины
}
```

## Алгоритмы поиска пути

Пакет `org.jgrapht.alg.shortestpath`: Дейкстра, Беллман-Форд, A*, Флойд-Уоршелл. Пример — Дейкстра:

```java
@Test
void whenGetDijkstraShortestPath_thenGetNotNullPath() {
    DijkstraShortestPath<String, DefaultEdge> dijkstraShortestPath =
        new DijkstraShortestPath<>(directedGraph);

    GraphPath<String, DefaultEdge> path =
        dijkstraShortestPath.getPath("v1", "v4");

    List<String> shortestPath = path.getVertexList();
    assertNotNull(shortestPath);
}
```

Беллман-Форд (поддержка отрицательных весов):

```java
@Test
void whenGetBellmanFordShortestPath_thenGetNotNullPath() {
    BellmanFordShortestPath<String, DefaultEdge> bellmanFordShortestPath =
        new BellmanFordShortestPath<>(directedGraph);

    GraphPath<String, DefaultEdge> path =
        bellmanFordShortestPath.getPath("v1", "v4");

    List<String> shortestPath = path.getVertexList();
    assertNotNull(shortestPath);
}
```

## Сильно связные компоненты

Сильно связный подграф — между любой парой вершин есть путь в обе стороны. Перечисление: `KosarajuStrongConnectivityInspector`, метод `stronglyConnectedSubgraphs()`.

```java
@Test
void whenGetStronglyConnectedSubgraphs_thenPathExists() {
    StrongConnectivityAlgorithm<String, DefaultEdge> scAlg =
        new KosarajuStrongConnectivityInspector<>(directedGraph);

    List<DirectedSubgraph<String, DefaultEdge>> stronglyConnectedSubgraphs =
        scAlg.stronglyConnectedSubgraphs();

    List<String> stronglyConnectedVertices = new ArrayList<>(
        stronglyConnectedSubgraphs.get(3).vertexSet());

    String randomVertex1 = stronglyConnectedVertices.get(0);
    String randomVertex2 = stronglyConnectedVertices.get(3);

    AllDirectedPaths<String, DefaultEdge> allDirectedPaths =
        new AllDirectedPaths<>(directedGraph);

    List<GraphPath<String, DefaultEdge>> possiblePathList =
        allDirectedPaths.getAllPaths(
            randomVertex1, randomVertex2, false,
            stronglyConnectedVertices.size());

    assertTrue(possiblePathList.size() > 0);
}
```

## Эйлеровы и гамильтоновы циклы

### Эйлеров цикл

Эйлерова цепь проходит по каждому ребру ровно один раз. Проверка и поиск: `HierholzerEulerianCycle.isEulerian()`, `getEulerianCycle()`.

```java
public void createGraphWithEulerianCircuit() {
    SimpleWeightedGraph<String, DefaultEdge> simpleGraph =
        new SimpleWeightedGraph<>(DefaultEdge.class);

    IntStream.range(1, 5)
        .forEach(i -> simpleGraph.addVertex("v" + i));

    IntStream.range(1, 5)
        .forEach(i -> {
            int endVertexNo = (i + 1) > 5 ? 1 : i + 1;
            simpleGraph.addEdge("v" + i, "v" + endVertexNo);
        });
}
```

```java
@Test
void givenGraph_whenCheckEulerianCycle_thenGetResult() {
    HierholzerEulerianCycle<String, DefaultEdge> eulerianCycle =
        new HierholzerEulerianCycle<>();

    assertTrue(eulerianCycle.isEulerian(simpleGraph));
}

@Test
void whenGetEulerianCycle_thenGetGraphPath() {
    HierholzerEulerianCycle<String, DefaultEdge> eulerianCycle =
        new HierholzerEulerianCycle<>();

    GraphPath<String, DefaultEdge> path =
        eulerianCycle.getEulerianCycle(simpleGraph);

    assertTrue(path.getEdgeList().containsAll(simpleGraph.edgeSet()));
}
```

### Гамильтонов цикл

Гамильтонов путь посещает каждую вершину ровно один раз; цикл — путь с ребром от последней к первой. Для полного графа приближённый минимальный тур: `HamiltonianCycle.getApproximateOptimalForCompleteGraph()` (NP-полная задача, полиномиальное приближение).

```java
void whenGetHamiltonianCyclePath_thenGetVerticeSequence() {
    List<String> verticeList = HamiltonianCycle
        .getApproximateOptimalForCompleteGraph(completeGraph);

    assertEquals(verticeList.size(), completeGraph.vertexSet().size());
}
```

### Обнаружение циклов

`CycleDetector` — только для ориентированных графов; `detectCycles()`, `findCycles()`.

```java
@Test
void whenCheckCycles_thenDetectCycles() {
    CycleDetector<String, DefaultEdge> cycleDetector =
        new CycleDetector<>(directedGraph);

    assertTrue(cycleDetector.detectCycles());

    Set<String> cycleVertices = cycleDetector.findCycles();
    assertTrue(cycleVertices.size() > 0);
}
```

## Визуализация графов

С зависимостью `jgrapht-ext`: адаптер `JGraphXAdapter`, раскладка (например `mxCircleLayout`), `mxCellRenderer.createBufferedImage()`, сохранение через ImageIO.

```java
@BeforeEach
public void createGraph() {
    File imgFile = new File("src/test/resources/graph.png");
    imgFile.createNewFile();

    DefaultDirectedGraph<String, DefaultEdge> g =
        new DefaultDirectedGraph<>(DefaultEdge.class);

    String x1 = "x1";
    String x2 = "x2";
    String x3 = "x3";

    g.addVertex(x1);
    g.addVertex(x2);
    g.addVertex(x3);

    g.addEdge(x1, x2);
    g.addEdge(x2, x3);
    g.addEdge(x3, x1);
}
```

```java
@Test
void givenAdaptedGraph_whenWriteBufferedImage_thenFileShouldExist()
    throws IOException {

    JGraphXAdapter<String, DefaultEdge> graphAdapter =
        new JGraphXAdapter<>(g);

    mxIGraphLayout layout = new mxCircleLayout(graphAdapter);
    layout.execute(graphAdapter.getDefaultParent());

    BufferedImage image =
        mxCellRenderer.createBufferedImage(
            graphAdapter, null, 2, Color.WHITE, true, null);

    File imgFile = new File("src/test/resources/graph.png");
    ImageIO.write(image, "PNG", imgFile);

    assertTrue(imgFile.exists());
}
```

## Реализация на Kotlin

```kotlin
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import org.jgrapht.graph.SimpleWeightedGraph
import org.jgrapht.graph.Multigraph
import org.jgrapht.graph.DefaultWeightedEdge

// Простой граф
fun createSimpleGraphK(): Graph<String, DefaultEdge> {
    val g = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)
    g.addVertex("v1")
    g.addVertex("v2")
    g.addEdge("v1", "v2")
    return g
}

// Ориентированный граф
fun createDirectedGraphK(): DefaultDirectedGraph<String, DefaultEdge> {
    val directedGraph = DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge::class.java)
    directedGraph.addVertex("v1")
    directedGraph.addVertex("v2")
    directedGraph.addVertex("v3")
    directedGraph.addEdge("v1", "v2")
    return directedGraph
}

// Мультиграф со взвешенными ребрами
fun createMultiGraphWithWeightedEdgesK(): Multigraph<String, DefaultWeightedEdge> {
    val multiGraph = Multigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
    multiGraph.addVertex("v1")
    multiGraph.addVertex("v2")

    val edge1 = multiGraph.addEdge("v1", "v2")
    multiGraph.setEdgeWeight(edge1, 5.0)

    val edge2 = multiGraph.addEdge("v1", "v2")
    multiGraph.setEdgeWeight(edge2, 3.0)

    return multiGraph
}
```

### Обход графов

```kotlin
import org.jgrapht.traverse.DepthFirstIterator
import org.jgrapht.traverse.BreadthFirstIterator

fun traverseGraphK(directedGraph: DefaultDirectedGraph<String, DefaultEdge>) {
    // DFS
    val depthFirstIterator = DepthFirstIterator(directedGraph)
    while (depthFirstIterator.hasNext()) {
        val vertex = depthFirstIterator.next()
        println("DFS: $vertex")
    }

    // BFS
    val breadthFirstIterator = BreadthFirstIterator(directedGraph)
    while (breadthFirstIterator.hasNext()) {
        val vertex = breadthFirstIterator.next()
        println("BFS: $vertex")
    }
}
```

### Алгоритмы поиска пути

```kotlin
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath
import org.jgrapht.GraphPath

fun findShortestPathK(
    graph: DefaultDirectedGraph<String, DefaultEdge>,
    source: String,
    target: String
): List<String>? {
    val dijkstraShortestPath = DijkstraShortestPath(graph)
    val path: GraphPath<String, DefaultEdge>? = dijkstraShortestPath.getPath(source, target)
    return path?.vertexList
}

fun findShortestPathBellmanFordK(
    graph: DefaultDirectedGraph<String, DefaultEdge>,
    source: String,
    target: String
): List<String>? {
    val bellmanFordShortestPath = BellmanFordShortestPath(graph)
    val path: GraphPath<String, DefaultEdge>? = bellmanFordShortestPath.getPath(source, target)
    return path?.vertexList
}
```

### Пример использования

```kotlin
fun main() {
    val graph = createDirectedGraphK()
    graph.addVertex("v4")
    graph.addEdge("v2", "v4")
    graph.addEdge("v3", "v4")

    val path = findShortestPathK(graph, "v1", "v4")
    println("Shortest path: $path") // [v1, v2, v4]

    traverseGraphK(graph)
}
```

## Лучшие практики

Выбирайте тип графа по задаче: `SimpleGraph` для неориентированных без петель, `DirectedGraph` для ориентированных, взвешенные графы при нужных весах. Подключайте `jgrapht-core` для алгоритмов, `jgrapht-ext` для экспорта в DOT и изображений. Для кратчайших путей: неотрицательные веса — Дейкстра, отрицательные — Беллман-Форд, эвристика — A*. На больших графах используйте итераторы обхода, не загружая все вершины в память.

## Основные возможности

Типы графов (простые, ориентированные, взвешенные, мультиграфы), обход (BFS, DFS и др.), кратчайшие пути (Дейкстра, Беллман-Форд, A*, Флойд-Уоршелл), анализ (циклы, сильно связные компоненты), Эйлеровы и гамильтоновы циклы, визуализация.

## Применение

Моделирование сетей, маршрутизация, анализ соцсетей, транспортная оптимизация, исследование алгоритмов, визуализация структур. Подробнее — в [официальной документации JGraphT](https://jgrapht.org/guide/UserOverview).

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Ошибка при визуализации / экспорте | Нет зависимости jgrapht-ext | Добавить jgrapht-ext в pom |
| Неверный путь при отрицательных весах | Использована Дейкстра | Использовать BellmanFordShortestPath |
| CycleDetector не работает | Граф неориентированный | CycleDetector только для ориентированных графов |

## Частые вопросы

**Какой тип графа выбрать для неориентированного без петель?** `SimpleGraph` (или `SimpleWeightedGraph` при нужных весах).

**Нужна ли jgrapht-ext для расчёта кратчайших путей?** Нет; достаточно `jgrapht-core`. Расширение нужно для экспорта в DOT и рендера в изображения.

**Где взять актуальную версию?** [Maven Central](https://search.maven.org/artifact/org.jgrapht/jgrapht-core) и [GitHub JGraphT](https://github.com/jgrapht/jgrapht).
