---
title: "Поиск в ширину (BFS)"
description: "Обход графа или дерева по уровням: сначала все вершины на текущей глубине, затем на следующей. Используется очередь (FIFO); для графов с циклами — множество посещённых вершин. Реализации для дерева и графа в Java и Kotlin."
tags:
  - algorithms
  - graphs
  - bfs
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Поиск в ширину (`BFS`)

Обход графа или дерева по уровням: сначала все вершины на текущей глубине, затем на следующей. Используется очередь (FIFO); для графов с циклами — множество посещённых вершин. Реализации для дерева и графа в Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Breadth First Search — GeeksforGeeks](https://www.geeksforgeeks.org/breadth-first-search-or-bfs-for-a-graph/)

### Визуализация
- [`Visualgo`: `BFS`](https://visualgo.net/)

### См. также
- [[dijkstra|dijkstra.md]] — алгоритм Дейкстры
- [[jgrapht|jgrapht.md]] — библиотека JGraphT

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [BFS для деревьев](#bfs-для-деревьев)
- [BFS для графов](#bfs-для-графов)
- [Реализация на Java](#реализация-на-java)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Сравнение с DFS](#сравнение-с-dfs)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)


## Описание алгоритма

BFS обходит вершины по уровням: сначала все на расстоянии 0, затем 1, затем 2 и т.д. Порядок задаётся очередью (FIFO). Для дерева достаточно добавлять детей в очередь; для графа с циклами нужно хранить множество посещённых вершин и не добавлять их снова.

## BFS для деревьев

Очередь инициализируется корнем. На каждом шаге извлекаем узел из очереди; если он не искомый — добавляем всех его детей в конец очереди. Циклов в дереве нет, поэтому повторного посещения не будет.

## BFS для графов

В графе возможны циклы — без учёта посещённых вершин обход зациклится. Перед обработкой узла проверяем, не посещён ли он; после обработки добавляем его в множество посещённых и в очередь добавляем только непосещённых соседей (или добавляем всех, а при извлечении пропускаем уже посещённых).

## Реализация на Java

### Реализация для деревьев

```java
// Узел дерева: значение и список дочерних узлов для BFS-обхода
public class Tree<T> {
    private T value;
    private List<Tree<T>> children;
    
    private Tree(T value) {
        this.value = value;
        this.children = new ArrayList<>();
    }
    
    public static <T> Tree<T> of(T value) {
        return new Tree<>(value);
    }
    
    public Tree<T> addChild(T value) {
        Tree<T> newChild = new Tree<>(value);
        children.add(newChild);
        return newChild;
    }
    
    public T getValue() {
        return value;
    }
    
    public List<Tree<T>> getChildren() {
        return children;
    }
}
```

```java
// Поиск по дереву: очередь, извлекаем узел, сравниваем значение, добавляем детей
public static <T> Optional<Tree<T>> search(T value, Tree<T> root) {
    Queue<Tree<T>> queue = new ArrayDeque<>();
    queue.add(root);
    
    while(!queue.isEmpty()) {
        Tree<T> currentNode = queue.remove();
        
        if (currentNode.getValue().equals(value)) {
            return Optional.of(currentNode);
        } else {
            queue.addAll(currentNode.getChildren());
        }
    }
    
    return Optional.empty();
}
```

### Пример использования для деревьев

```java
Tree<Integer> root = Tree.of(10);
Tree<Integer> rootFirstChild = root.addChild(2);
Tree<Integer> depthMostChild = rootFirstChild.addChild(3);
Tree<Integer> rootSecondChild = root.addChild(4);
BreadthFirstSearchAlgorithm.search(4, root); // порядок обхода: 10, 2, 4
```

```text
[main] DEBUG - Visited node with value: 10
[main] DEBUG - Visited node with value: 2
[main] DEBUG - Visited node with value: 4
```

### Реализация для графов

Граф: узлы и множество соседей у каждого; связи двунаправленные. Чтобы не зациклиться, храним `alreadyVisited` и не обрабатываем узел повторно.

```java
// Узел графа: значение и множество соседей; connect — двусторонняя связь
public class Node<T> {
    private T value;
    private Set<Node<T>> neighbors;
    
    public Node(T value) {
        this.value = value;
        this.neighbors = new HashSet<>();
    }
    
    public void connect(Node<T> node) {
        if (this == node) {
            throw new IllegalArgumentException("Can't connect node to itself");
        }
        this.neighbors.add(node);
        node.neighbors.add(this);
    }
    
    public T getValue() {
        return value;
    }
    
    public Set<Node<T>> getNeighbors() {
        return neighbors;
    }
}

// BFS по графу: очередь + alreadyVisited; при извлечении проверяем значение, в посещённые добавляем после проверки, в очередь — соседей без посещённых
public static <T> Optional<Node<T>> search(T value, Node<T> start) {
    Queue<Node<T>> queue = new ArrayDeque<>();
    Set<Node<T>> alreadyVisited = new HashSet<>();
    
    queue.add(start);
    Node<T> currentNode;
    
    while (!queue.isEmpty()) {
        currentNode = queue.remove();
        
        LOGGER.debug("Visited node with value: {}", currentNode.getValue());
        
        if (currentNode.getValue().equals(value)) {
            return Optional.of(currentNode);
        } else {
            alreadyVisited.add(currentNode);
            queue.addAll(currentNode.getNeighbors());
            queue.removeAll(alreadyVisited);
        }
    }
    
    return Optional.empty();
}
```

### Пример использования для графов

```java
Node<Integer> start = new Node<>(10);
Node<Integer> firstNeighbor = new Node<>(2);
start.connect(firstNeighbor);

Node<Integer> firstNeighborNeighbor = new Node<>(3);
firstNeighbor.connect(firstNeighborNeighbor);
firstNeighborNeighbor.connect(start);

Node<Integer> secondNeighbor = new Node<>(4);
start.connect(secondNeighbor);
BreadthFirstSearchAlgorithm.search(4, firstNeighborNeighbor); // старт из firstNeighborNeighbor
```

```text
[main] DEBUG - Visited node with value: 3
[main] DEBUG - Visited node with value: 2
[main] DEBUG - Visited node with value: 10
[main] DEBUG - Visited node with value: 4
```

## Реализация на Kotlin

```kotlin
// Дерево: значение и список детей
class TreeK<T>(val value: T) {
    private val children = mutableListOf<TreeK<T>>()
    
    fun addChild(value: T): TreeK<T> {
        val newChild = TreeK(value)
        children.add(newChild)
        return newChild
    }
    
    fun getChildren(): List<TreeK<T>> = children
}
```

```kotlin
fun <T> searchK(value: T, root: TreeK<T>): TreeK<T>? {
    val queue = ArrayDeque<TreeK<T>>()
    queue.add(root)
    
    while (queue.isNotEmpty()) {
        val currentNode = queue.removeFirst()
        
        if (currentNode.value == value) {
            return currentNode
        } else {
            queue.addAll(currentNode.getChildren())
        }
    }
    
    return null
}
```

```kotlin
// Узел графа: соседи в mutableSetOf, connect — двусторонняя связь
class NodeK<T>(val value: T) {
    private val neighbors = mutableSetOf<NodeK<T>>()
    
    fun connect(node: NodeK<T>) {
        if (this == node) {
            throw IllegalArgumentException("Can't connect node to itself")
        }
        neighbors.add(node)
        node.neighbors.add(this)
    }
    
    fun getNeighbors(): Set<NodeK<T>> = neighbors
}
```

```kotlin
fun <T> searchK(value: T, start: NodeK<T>): NodeK<T>? {
    val queue = ArrayDeque<NodeK<T>>()
    val alreadyVisited = mutableSetOf<NodeK<T>>()
    
    queue.add(start)
    
    while (queue.isNotEmpty()) {
        val currentNode = queue.removeFirst()
        
        if (currentNode in alreadyVisited) {
            continue
        }
        
        if (currentNode.value == value) {
            return currentNode
        }
        
        alreadyVisited.add(currentNode)
        queue.addAll(currentNode.getNeighbors())
        queue.removeAll(alreadyVisited)
    }
    
    return null
}
```

### Пример использования

```kotlin
fun main() {
    // Для деревьев
    val root = TreeK(10)
    val rootFirstChild = root.addChild(2)
    rootFirstChild.addChild(3)
    val rootSecondChild = root.addChild(4)
    
    val result1 = searchK(4, root)
    println("Found: ${result1?.value}") // Found: 4
    
    // Для графов
    val start = NodeK(10)
    val firstNeighbor = NodeK(2)
    start.connect(firstNeighbor)
    
    val firstNeighborNeighbor = NodeK(3)
    firstNeighbor.connect(firstNeighborNeighbor)
    firstNeighborNeighbor.connect(start)
    
    val secondNeighbor = NodeK(4)
    start.connect(secondNeighbor)
    
    val result2 = searchK(4, firstNeighborNeighbor)
    println("Found: ${result2?.value}") // Found: 4
}
```

## Сложность

Дерево: время O(n), каждый узел в очереди не более одного раза. Граф: время O(n + c), где n — вершины, c — рёбра; узел посещается один раз, но операции с множеством и очередью дают вклад по рёбрам. Память: для дерева O(w) — ширина; для графа O(V) — очередь и посещённые.

## Особенности

BFS полный (найдёт решение в конечном графе, если оно есть) и в невзвешенном графе даёт кратчайший путь по числу рёбер. Памяти требует больше, чем DFS (хранит весь «фронт» уровня). Применяется для поиска кратчайшего пути, обхода по уровням, проверки связности.

## Применение

Кратчайший путь в невзвешенном графе, обход по уровням, проверка связности, поиск вершин на заданном расстоянии, соцсети (уровни друзей), игры (pathfinding), рассылка в сетях (broadcasting).

## Сравнение с DFS

| Характеристика | BFS | DFS |
|----------------|-----|-----|
| Структура данных | Очередь (FIFO) | Стек |
| Память | O(ширина) | O(глубина) |
| Кратчайший путь в невзвешенном | Да | Нет |
| Полнота (без циклов/с учётом посещённых) | Да | Зависит от реализации |
| Типичное применение | Кратчайший путь, уровни | Обход, поиск решения |

## Лучшие практики

Используйте очередь (в Java — `ArrayDeque` или `LinkedList`), не стек. Для графов с циклами обязательно храните множество посещённых и не добавляйте в очередь уже посещённые. Учитывайте пустой граф, одну вершину и несвязный граф: BFS из одной вершины обходит только одну компоненту; для полного обхода запускайте BFS из каждой непосещённой. Для восстановления кратчайшего пути сохраняйте родителя при добавлении вершины в очередь. Тестируйте на дереве (один узел, цепочка, разветвление), графе с циклом и с несколькими компонентами связности.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Бесконечный цикл при обходе графа | Не учитываются посещённые вершины | Хранить множество посещённых и не добавлять их в очередь (или пропускать при извлечении) |
| Не находит узел в несвязном графе | BFS стартует из одной компоненты | Запускать BFS из каждой непосещённой вершины для полного обхода |
| Неверный «кратчайший» путь | Граф взвешенный | BFS даёт кратчайший по числу рёбер; для весов использовать Дейкстру или A* |

## Частые вопросы

**Почему BFS даёт кратчайший путь в невзвешенном графе?** Вершины обрабатываются в порядке возрастания расстояния от старта (сначала расстояние 0, затем 1, затем 2…), поэтому при первом достижении цели пройденное число рёбер минимально.

**Когда предпочесть DFS?** Когда важна экономия памяти (глубина мала) или нужен полный обход/поиск с возвратом (backtracking); для кратчайшего пути в невзвешенном графе — BFS.

**Нужно ли помечать посещённые в дереве?** В дереве без циклов — нет; каждый узел входит в очередь один раз. Для графа — обязательно.
