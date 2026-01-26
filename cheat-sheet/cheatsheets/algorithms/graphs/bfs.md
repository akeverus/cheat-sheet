# Поиск в ширину (BFS)

Кратко: алгоритм поиска в ширину (BFS) - это алгоритм обхода графа, который исследует все узлы на текущем уровне перед переходом на следующий уровень, используя очередь для управления порядком обхода.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Breadth First Search](https://www.geeksforgeeks.org/breadth-first-search-or-bfs-for-a-graph/)

### Визуализация
- [Visualgo: BFS](https://visualgo.net/en/bfsdfs)

### См. также
- `./dijkstra.md` - алгоритм Дейкстры для поиска кратчайшего пути
- `./jgrapht.md` - библиотека JGraphT для работы с графами

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [BFS для деревьев](#bfs-для-деревьев)
- [BFS для графов](#bfs-для-графов)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Основной подход алгоритма поиска в ширину (BFS) заключается в поиске узла в структуре дерева или графа путем изучения соседей до потомков.

Во-первых, мы увидим, как этот алгоритм работает для деревьев. После этого мы адаптируем его к графам, которые имеют определенное ограничение, заключающееся в том, что иногда они содержат циклы. Наконец, мы обсудим производительность этого алгоритма.

## BFS для деревьев

Идея алгоритма BFS для деревьев состоит в том, чтобы поддерживать очередь узлов, которая обеспечит порядок обхода. В начале алгоритма очередь содержит только корневой узел. Мы будем повторять эти шаги, пока очередь содержит один или несколько узлов:

1. Вытолкнуть первый узел из очереди
2. Если это тот узел, который мы ищем, то поиск окончен
3. В противном случае добавьте дочерние элементы этого узла в конец очереди и повторите шаги

Прекращение выполнения обеспечивается отсутствием циклов. Мы увидим, как управлять циклами в следующем разделе.

## BFS для графов

В случае графов мы должны думать о возможных циклах в структуре. Если мы просто применим предыдущий алгоритм к графу с циклом, он зациклится навсегда. Поэтому нам нужно сохранить коллекцию посещенных узлов и убедиться, что мы не посещаем их дважды:

1. Вытолкнуть первый узел из очереди
2. Проверьте, был ли узел уже посещен, если да, пропустите его
3. Если это тот узел, который мы ищем, то поиск окончен
4. В противном случае добавьте его в посещенные узлы
5. Добавьте дочерние элементы этого узла в очередь и повторите эти шаги

## Java Implementation

### Реализация для деревьев

Во-первых, мы реализуем алгоритм дерева. Давайте создадим наш класс Tree, который состоит из значения и дочерних элементов, представленных списком других Tree:

```java
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

Чтобы избежать создания циклов, дочерние элементы создаются самим классом на основе заданного значения.

После этого давайте обеспечим метод search():

```java
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

Как мы упоминали ранее, алгоритм BFS использует очередь для обхода узлов. Прежде всего, мы добавляем нашу корневую ноду в эту очередь, затем мы должны зациклиться, пока очередь не пуста, и каждый раз, когда мы извлекаем узел из очереди, проверяем, является ли он искомым узлом. Если нет, добавляем его дочерние элементы в очередь.

### Пример использования для деревьев

Давайте теперь представим пример древовидной структуры:

```java
Tree<Integer> root = Tree.of(10);
Tree<Integer> rootFirstChild = root.addChild(2);
Tree<Integer> depthMostChild = rootFirstChild.addChild(3);
Tree<Integer> rootSecondChild = root.addChild(4);
```

Затем, если мы ищем значение 4, мы ожидаем, что алгоритм будет проходить узлы со значениями 10, 2 и 4 в следующем порядке:

```java
BreadthFirstSearchAlgorithm.search(4, root);
```

Мы можем убедиться в этом, зарегистрировав значение посещенных узлов:

```
[main] DEBUG - Visited node with value: 10
[main] DEBUG - Visited node with value: 2
[main] DEBUG - Visited node with value: 4
```

### Реализация для графов

На этом история с деревьями заканчивается. Давайте теперь посмотрим, как работать с графиками. В отличие от деревьев графы могут содержать циклы. Это означает, как мы видели в предыдущем разделе, мы должны помнить узлы, которые мы посетили, чтобы избежать бесконечного цикла.

Сначала давайте определим структуру нашего графа:

```java
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
```

Теперь мы видим, что, в отличие от деревьев, мы можем свободно соединять узел с другим, что дает нам возможность создавать циклы. Единственным исключением является то, что узел не может соединиться сам с собой.

Также стоит отметить, что в этом представлении нет корневого узла. Это не проблема, так как мы также сделали соединения между узлами двунаправленными. Это означает, что мы сможем выполнять поиск по графу, начиная с любого узла.

Прежде всего, давайте повторно используем алгоритм выше, адаптированный к новой структуре:

```java
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

Мы не можем запустить алгоритм без проверки посещенных узлов, иначе любой цикл заставит его работать вечно. Итак, мы должны добавить инструкции, чтобы позаботиться об уже посещенных узлах.

Как мы видим, мы сначала инициализируем набор, который будет содержать посещенные узлы. Затем, когда сравнение значений не удается, мы добавляем узел в посещенные. Наконец, после добавления соседей узла в очередь, мы удаляем из нее уже посещенные узлы (это альтернативный способ проверки присутствия текущего узла в этом наборе).

Делая это, мы гарантируем, что алгоритм не попадет в бесконечный цикл.

### Пример использования для графов

Давайте посмотрим, как это работает на примере. Прежде всего, мы определим граф с циклом:

```java
Node<Integer> start = new Node<>(10);
Node<Integer> firstNeighbor = new Node<>(2);
start.connect(firstNeighbor);

Node<Integer> firstNeighborNeighbor = new Node<>(3);
firstNeighbor.connect(firstNeighborNeighbor);
firstNeighborNeighbor.connect(start);

Node<Integer> secondNeighbor = new Node<>(4);
start.connect(secondNeighbor);
```

Давайте снова скажем, что мы хотим найти значение 4. Поскольку корневого узла нет, мы можем начать поиск с любого узла, который захотим, и мы выберем firstNeighborNeighbor:

```java
BreadthFirstSearchAlgorithm.search(4, firstNeighborNeighbor);
```

Опять же, мы добавим журнал, чтобы увидеть, какие узлы посещены, и мы ожидаем, что их будет 3, 2, 10 и 4, только один раз каждый в этом порядке:

```
[main] DEBUG - Visited node with value: 3
[main] DEBUG - Visited node with value: 2
[main] DEBUG - Visited node with value: 10
[main] DEBUG - Visited node with value: 4
```

## Kotlin Implementation

### Класс Tree

```kotlin
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

### BFS для деревьев

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

### Класс Node для графов

```kotlin
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

### BFS для графов

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

Теперь, когда мы рассмотрели оба алгоритма в Java, давайте поговорим об их временной сложности. Мы будем использовать нотацию Big-O, чтобы выразить их.

### Временная сложность для деревьев

Начнем с алгоритма дерева. Он добавляет узел в очередь не более одного раза, поэтому также посещает его не более одного раза. Таким образом, если n - количество узлов в дереве, временная сложность алгоритма будет **O(n)**.

### Временная сложность для графов

Теперь с алгоритмом графа все немного сложнее. Мы пройдемся по каждому узлу не более одного раза, но для этого мы будем использовать операции линейной сложности, такие как `addAll()` и `removeAll()`.

Возьмем n количество узлов и c количество соединений графа. Затем, в худшем случае (если узел не найден), мы можем использовать методы `addAll()` и `removeAll()` для добавления и удаления узлов вплоть до количества соединений, что дает нам сложность O(c) для этих операций. Итак, при условии, что c > n, сложность всего алгоритма будет **O(c)**. В противном случае это будет **O(n)**. Обычно это **O(n + c)**, что можно интерпретировать как сложность, зависящую от наибольшего числа между n и c.

Почему у нас не было этой проблемы для поиска по дереву? Потому что количество соединений в дереве ограничено количеством узлов. Количество связей в дереве из n узлов равно n - 1.

### Пространственная сложность

- **Для деревьев:** O(w), где w - максимальная ширина дерева (максимальное количество узлов на одном уровне)
- **Для графов:** O(V), где V - количество вершин (для хранения посещенных узлов и очереди)

## Особенности

- **Полнота:** BFS гарантированно найдет решение, если оно существует (для конечных графов)
- **Оптимальность:** BFS находит кратчайший путь в невзвешенном графе
- **Использование памяти:** BFS требует больше памяти, чем DFS, так как хранит все узлы текущего уровня
- **Применение:** Поиск кратчайшего пути, обход графа по уровням, проверка связности

## Применение

BFS широко используется в:

- Поиске кратчайшего пути в невзвешенном графе
- Обходе графа по уровням
- Проверке связности графа
- Поиске всех узлов на определенном расстоянии
- Социальных сетях (поиск друзей на определенной дистанции)
- Игровой разработке (pathfinding)
- Сетевых протоколах (broadcasting)

## Сравнение с DFS

| Характеристика | BFS | DFS |
|----------------|-----|-----|
| Структура данных | Очередь | Стек |
| Память | O(ширина) | O(глубина) |
| Кратчайший путь | Да (в невзвешенном графе) | Нет |
| Полнота | Да | Нет (может зациклиться) |
| Применение | Поиск кратчайшего пути | Поиск решения, обход |
