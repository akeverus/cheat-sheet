# JGraphT - Библиотека для работы с графами

Кратко: JGraphT - это библиотека классов Java с открытым исходным кодом, которая предоставляет различные типы графов и множество полезных алгоритмов для решения наиболее часто встречающихся проблем с графами.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [JGraphT Documentation](https://jgrapht.org/)
- [JGraphT GitHub](https://github.com/jgrapht/jgrapht)

### См. также
- `./dijkstra.md` - алгоритм Дейкстры для поиска кратчайшего пути
- `./bfs.md` - поиск в ширину

## Содержание

- [Введение](#введение)
- [Установка](#установка)
- [Типы графов](#типы-графов)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Визуализация графов](#визуализация-графов)

## Введение

В большинстве случаев, когда мы реализуем алгоритмы на основе графов, нам также необходимо реализовать некоторые служебные функции.

JGraphT - это библиотека классов Java с открытым исходным кодом, которая не только предоставляет нам различные типы графиков, но и множество полезных алгоритмов для решения наиболее часто встречающихся проблем с графами.

В этой статье мы увидим, как создавать разные типы графиков и насколько удобно пользоваться предоставленными утилитами.

## Установка

Давайте начнем с добавления зависимости в наш проект Maven:

```xml
<dependency>
    <groupId>org.jgrapht</groupId>
    <artifactId>jgrapht-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

Для визуализации графов также можно добавить расширение:

```xml
<dependency>
    <groupId>org.jgrapht</groupId>
    <artifactId>jgrapht-ext</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Типы графов

JGraphT поддерживает различные типы графиков:

- **Простые графы** - графы без петель и кратных ребер
- **Ориентированные/неориентированные графы**
- **Взвешенные/невзвешенные графы**
- **Мультиграфы** - графы с несколькими путями между двумя вершинами
- **Немодифицируемые графы** - только для чтения
- **Прослушиваемые графы** - позволяют внешним слушателям отслеживать модификации

## Java Implementation

### Создание графов

#### Простой граф

Для начала создадим простой граф с вершиной типа String:

```java
Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
g.addVertex("v1");
g.addVertex("v2");
g.addEdge("v1", "v2");
```

### Ориентированный граф

В нашем примере мы создадим ориентированный граф и будем использовать его для демонстрации других полезных функций и алгоритмов:

```java
DirectedGraph<String, DefaultEdge> directedGraph = 
    new DefaultDirectedGraph<>(DefaultEdge.class);

directedGraph.addVertex("v1");
directedGraph.addVertex("v2");
directedGraph.addVertex("v3");
directedGraph.addEdge("v1", "v2");
```

### Полный граф

Точно так же мы можем сгенерировать полный граф:

```java
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

### Мультиграф со взвешенными ребрами

Помимо простых графов API также предоставляет нам мультиграфы (графы с несколькими путями между двумя вершинами).

Кроме того, у нас могут быть взвешенные/невзвешенные или определяемые пользователем ребра в любом графе.

Создадим мультиграф со взвешенными ребрами:

```java
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

В дополнение к этому у нас могут быть немодифицируемые (только для чтения) и прослушиваемые (позволяет внешним слушателям отслеживать модификации) графы, а также подграфы. Также мы всегда можем создать все композиции этих графиков.

## Обход графов

Теперь, когда у нас есть полноценные графовые объекты, давайте рассмотрим некоторые распространенные проблемы и их решения.

Мы можем перемещаться по графу, используя различные итераторы, такие как `BreadthFirstIterator`, `DepthFirstIterator`, `ClosestFirstIterator`, `RandomWalkIterator` в соответствии с требованиями.

Нам просто нужно создать экземпляр соответствующих итераторов, передав объекты графа:

```java
DepthFirstIterator<String, DefaultEdge> depthFirstIterator = 
    new DepthFirstIterator<>(directedGraph);

BreadthFirstIterator<String, DefaultEdge> breadthFirstIterator = 
    new BreadthFirstIterator<>(directedGraph);
```

Как только мы получим объекты итератора, мы можем выполнить итерацию, используя методы `hasNext()` и `next()`:

```java
while (breadthFirstIterator.hasNext()) {
    String vertex = breadthFirstIterator.next();
    // Обработка вершины
}
```

## Алгоритмы поиска пути

Он предоставляет реализации различных алгоритмов, таких как Dijkstra, Bellman-Ford, A* и FloydWarshall, в пакете `org.jgrapht.alg.shortestpath`.

### Алгоритм Дейкстры

Найдем кратчайший путь по алгоритму Дейкстры:

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

### Алгоритм Беллмана-Форда

Точно так же, чтобы получить кратчайший путь с помощью алгоритма Беллмана-Форда:

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

Прежде чем мы перейдем к реализации, давайте кратко рассмотрим, что означают сильно связанные подграфы. Подграф называется сильно связным, только если существует путь между каждой парой его вершин.

В нашем примере {v1, v2, v3, v4} можно считать сильно связным подграфом, если мы можем перейти к любой вершине, независимо от того, какая вершина является текущей.

Реализация для перечисления всех сильно связанных подграфов:

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

Эйлерова цепь в графе G - это цепь, включающая все вершины и ребра графа G. Граф, в котором он есть, является эйлеровым графом.

Давайте посмотрим на график:

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

Теперь мы можем проверить, содержит ли граф эйлеров цикл, используя API:

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

GraphPath, который посещает каждую вершину ровно один раз, называется гамильтоновым путем.

Гамильтонов цикл (или гамильтонова цепь) - это гамильтонов путь такой, что существует ребро (в графе) от последней вершины до первой вершины пути.

Мы можем найти оптимальный гамильтонов цикл для полного графа с помощью метода `HamiltonianCycle.getApproximateOptimalForCompleteGraph()`.

Этот метод вернет приблизительный минимальный тур коммивояжера (гамильтоновский цикл). Оптимальное решение является NP-полным, так что это хорошее приближение, работающее за полиномиальное время:

```java
void whenGetHamiltonianCyclePath_thenGetVerticeSequence() {
    List<String> verticeList = HamiltonianCycle
        .getApproximateOptimalForCompleteGraph(completeGraph);
    
    assertEquals(verticeList.size(), completeGraph.vertexSet().size());
}
```

### Обнаружение циклов

Мы также можем проверить, есть ли циклы в графе. В настоящее время CycleDetector поддерживает только ориентированные графы:

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

JGraphT позволяет нам генерировать визуализации графиков и сохранять их как изображения, сначала добавив зависимость расширения jgrapht-ext (см. раздел Установка).

Далее создадим простой ориентированный граф с 3 вершинами и 3 ребрами:

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

Теперь мы можем визуализировать этот график:

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

Здесь мы создали `JGraphXAdapter`, который получает наш график в качестве аргумента конструктора, и мы применили к нему `mxCircleLayout`. Это выстраивает визуализацию по кругу.

Кроме того, мы используем `mxCellRenderer` для создания `BufferedImage`, а затем записываем визуализацию в файл png.

## Kotlin Implementation

### Создание графов

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

## Основные возможности

JGraphT предоставляет:

- **Разнообразные типы графов:** простые, ориентированные, взвешенные, мультиграфы
- **Алгоритмы обхода:** BFS, DFS, и другие итераторы
- **Алгоритмы поиска пути:** Dijkstra, Bellman-Ford, A*, Floyd-Warshall
- **Анализ графов:** обнаружение циклов, сильно связные компоненты
- **Специальные алгоритмы:** Эйлеровы и гамильтоновы циклы
- **Визуализация:** создание изображений графов

## Применение

JGraphT полезен для:

- Моделирования сетей и связей
- Решения задач маршрутизации
- Анализа социальных сетей
- Оптимизации транспортных маршрутов
- Исследования алгоритмов на графах
- Визуализации сложных структур данных

Более подробную информацию мы можем найти в официальной документации JGraphT.
