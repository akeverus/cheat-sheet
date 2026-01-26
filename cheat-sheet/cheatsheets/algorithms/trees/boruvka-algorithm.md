# Borůvka's Algorithm

Кратко: алгоритм Борувки - это алгоритм для нахождения минимального остовного дерева (MST) графа, взвешенного по ребрам. Предшествует алгоритмам Прима и Крускала. Временная сложность O(E*log(V)).

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Borůvka's algorithm](https://www.geeksforgeeks.org/boruvkas-algorithm-minimum-spanning-tree/)

### См. также
- `./prim-algorithm.md` - алгоритм Прима
- `./kruskal-algorithm.md` - алгоритм Крускала
- `../graphs/dijkstra.md` - алгоритм Дейкстры

## Содержание

- [История алгоритма](#история-алгоритма)
- [Описание алгоритма](#описание-алгоритма)
- [Принцип работы](#принцип-работы)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## История алгоритма

В этом уроке мы рассмотрим Java-реализацию алгоритма Борувки для нахождения минимального остовного дерева (MST) графа, взвешенного по ребрам.

Он предшествует алгоритмам Прима и Крускала, но все же может считаться чем-то средним между ними.

Способ нахождения MST данного графа был впервые сформулирован Отакаром Борувкой в 1926 году. Это было задолго до того, как появились компьютеры, и фактически он был смоделирован для разработки эффективной системы распределения электроэнергии.

Жорж Соллен заново открыл его в 1965 году и использовал в параллельных вычислениях.

## Описание алгоритма

Основная идея алгоритма состоит в том, чтобы начать с набора деревьев, каждая вершина которого представляет собой изолированное дерево. Затем нам нужно продолжать добавлять ребра, чтобы уменьшить количество изолированных деревьев, пока у нас не получится одно связанное дерево.

## Принцип работы

Давайте рассмотрим это по шагам на примере графика:

1. **Шаг 0:** создайте график
2. **Шаг 1:** начните с набора несвязанных деревьев (количество деревьев = количеству вершин)
3. **Шаг 2:** пока есть несвязанные деревья, для каждого несвязанного дерева:
   - найти его ребро с меньшим весом
   - добавьте это ребро, чтобы соединить другое дерево

### Пример работы

```
Исходный граф:
    0---8---1
   /|      /|
  5 |     9 |
 /  |    /  |
2   |   3   |
 \  |    \  |
  15|    11 |
   \|      \|
    3---7---4
```

На каждой итерации:
- Каждое дерево находит свое минимальное ребро
- Добавляются найденные ребра
- Деревья объединяются
- Процесс повторяется до получения одного дерева

## Java Implementation

### Реализация

Теперь давайте посмотрим, как мы можем реализовать это на Java.

### Класс UnionFind

Для начала нам нужна структура данных для хранения родителей и рангов наших вершин.

Для этой цели определим класс UnionFind с двумя методами: union и find:

```java
public class UnionFind {
    private int[] parents;
    private int[] ranks;
    
    public UnionFind(int n) {
        parents = new int[n];
        ranks = new int[n];
        for (int i = 0; i < n; i++) {
            parents[i] = i;
            ranks[i] = 0;
        }
    }
    
    public int find(int u) {
        while (u != parents[u]) {
            u = parents[u];
        }
        return u;
    }
    
    public void union(int u, int v) {
        int uParent = find(u);
        int vParent = find(v);
        
        if (uParent == vParent) {
            return;
        }
        
        if (ranks[uParent] < ranks[vParent]) {
            parents[uParent] = vParent;
        } else if (ranks[uParent] > ranks[vParent]) {
            parents[vParent] = uParent;
        } else {
            parents[vParent] = uParent;
            ranks[uParent]++;
        }
    }
}
```

Мы можем думать об этом классе как о вспомогательной структуре для поддержания отношений между нашими вершинами и постепенного построения нашего MST.

Чтобы узнать, принадлежат ли две вершины u и v одному и тому же дереву, мы смотрим, возвращает ли find(u) того же родителя, что и find(v). Метод объединения используется для объединения деревьев.

### Инициализация графа

Теперь нам нужен способ получить вершины и ребра графа от пользователя и сопоставить их с объектами, которые мы можем использовать в нашем алгоритме во время выполнения.

Так как мы будем использовать JUnit для проверки нашего алгоритма, эта часть выполняется в методе @Before:

```java
@BeforeEach
public void setup() {
    graph = ValueGraphBuilder.undirected().build();
    graph.putEdgeValue(0, 1, 8);
    graph.putEdgeValue(0, 2, 5);
    graph.putEdgeValue(1, 2, 9);
    graph.putEdgeValue(1, 3, 11);
    graph.putEdgeValue(2, 3, 15);
    graph.putEdgeValue(2, 4, 10);
    graph.putEdgeValue(3, 4, 7);
}
```

Здесь мы использовали `MutableValueGraph<Integer, Integer>` от Guava для хранения нашего графика. Затем мы использовали ValueGraphBuilder для построения неориентированного взвешенного графа.

Метод putEdgeValue принимает три аргумента: два Integer для вершин и третий Integer для веса, как указано в объявлении универсального типа MutableValueGraph.

### Класс BoruvkaMST

Наконец, мы подошли к сути дела, реализации алгоритма.

Мы сделаем это в классе, который назовем BoruvkaMST. Во-первых, давайте объявим пару переменных экземпляра:

```java
public class BoruvkaMST {
    private static MutableValueGraph<Integer, Integer> mst = ValueGraphBuilder.undirected().build();
    private static int totalWeight;
    
    public MutableValueGraph<Integer, Integer> getMST() {
        return mst;
    }
    
    public int getTotalWeight() {
        return totalWeight;
    }
}
```

Как мы видим, здесь мы используем `MutableValueGraph<Integer, Integer>` для представления MST.

### Конструктор с алгоритмом

Во-вторых, мы определим конструктор, в котором происходит вся магия. Он принимает один аргумент - граф, который мы построили ранее.

Первое, что он делает, это инициализирует UnionFind вершин входного графа. Изначально все вершины являются своими родителями, ранг каждой равен 0:

```java
public BoruvkaMST(MutableValueGraph<Integer, Integer> graph) {
    int size = graph.nodes().size();
    UnionFind uf = new UnionFind(size);
    totalWeight = 0;
    
    // Основной цикл алгоритма
    for (int t = 1; t < size && mst.edges().size() < size - 1; t = t + t) {
        EndpointPair<Integer>[] closestEdgeArray = new EndpointPair[size];
        
        // Находим минимальное ребро для каждого дерева
        for (EndpointPair<Integer> edge : graph.edges()) {
            int u = edge.nodeU();
            int v = edge.nodeV();
            int uParent = uf.find(u);
            int vParent = uf.find(v);
            
            if (uParent == vParent) {
                continue;
            }
            
            int weight = graph.edgeValueOrDefault(u, v, 0);
            
            if (closestEdgeArray[uParent] == null) {
                closestEdgeArray[uParent] = edge;
            }
            
            if (closestEdgeArray[vParent] == null) {
                closestEdgeArray[vParent] = edge;
            }
            
            int uParentWeight = graph.edgeValueOrDefault(
                closestEdgeArray[uParent].nodeU(),
                closestEdgeArray[uParent].nodeV(), 0);
            
            int vParentWeight = graph.edgeValueOrDefault(
                closestEdgeArray[vParent].nodeU(),
                closestEdgeArray[vParent].nodeV(), 0);
            
            if (weight < uParentWeight) {
                closestEdgeArray[uParent] = edge;
            }
            
            if (weight < vParentWeight) {
                closestEdgeArray[vParent] = edge;
            }
        }
        
        // Добавляем найденные ребра в MST
        for (int i = 0; i < size; i++) {
            EndpointPair<Integer> edge = closestEdgeArray[i];
            if (edge != null) {
                int u = edge.nodeU();
                int v = edge.nodeV();
                int weight = graph.edgeValueOrDefault(u, v, 0);
                
                if (uf.find(u) != uf.find(v)) {
                    mst.putEdgeValue(u, v, weight);
                    totalWeight += weight;
                    uf.union(u, v);
                }
            }
        }
    }
}
```

Далее мы создадим цикл, определяющий количество итераций, необходимых для создания MST - не более log V раз или до тех пор, пока у нас не будет ребер V - 1, где V - количество вершин.

Здесь мы также инициализируем массив ребер, `closestEdgeArray` - для хранения ближайших ребер с меньшим весом.

После этого мы определим внутренний цикл for для перебора всех ребер графа, чтобы заполнить наш ближайший EdgeArray.

Если родители двух вершин одинаковы, это одно и то же дерево, и мы не добавляем его в массив. В противном случае мы сравниваем вес текущего ребра с весом ребер его родительских вершин. Если меньше, то добавляем в ближайший EdgeArray.

Затем мы определим второй внутренний цикл для создания дерева. Мы добавим к этому дереву ребра из предыдущего шага, не добавляя одно и то же ребро дважды. Кроме того, мы выполним объединение нашего UnionFind, чтобы получить и сохранить родителей и ранги вершин вновь созданных деревьев.

После повторения этих шагов не более log V раз или до тех пор, пока у нас не будет ребер V - 1, результирующее дерево будет нашим MST.

### Тестирование

Наконец, давайте посмотрим на простой JUnit для проверки нашей реализации:

```java
@Test
void givenInputGraph_whenBoruvkaPerformed_thenMinimumSpanningTree() {
    BoruvkaMST boruvkaMST = new BoruvkaMST(graph);
    MutableValueGraph<Integer, Integer> mst = boruvkaMST.getMST();
    
    assertEquals(30, boruvkaMST.getTotalWeight());
    assertEquals(4, mst.getEdgeCount());
}
```

Как мы видим, мы получили MST с весом 30 и 4 ребра, как и в наглядном примере.

## Kotlin Implementation

### Класс UnionFind

```kotlin
class UnionFindK(private val n: Int) {
    private val parents = IntArray(n) { it }
    private val ranks = IntArray(n) { 0 }
    
    fun find(u: Int): Int {
        var current = u
        while (current != parents[current]) {
            current = parents[current]
        }
        return current
    }
    
    fun union(u: Int, v: Int) {
        val uParent = find(u)
        val vParent = find(v)
        
        if (uParent == vParent) return
        
        when {
            ranks[uParent] < ranks[vParent] -> parents[uParent] = vParent
            ranks[uParent] > ranks[vParent] -> parents[vParent] = uParent
            else -> {
                parents[vParent] = uParent
                ranks[uParent]++
            }
        }
    }
}
```

### Алгоритм Борувки

```kotlin
data class EdgeK(val u: Int, val v: Int, val weight: Int)

class BoruvkaMSTK {
    private val mst = mutableListOf<EdgeK>()
    private var totalWeight = 0
    
    fun getMST(graph: Map<Int, List<Pair<Int, Int>>>, vertexCount: Int): List<EdgeK> {
        val unionFind = UnionFindK(vertexCount)
        var treeCount = vertexCount
        
        while (treeCount > 1) {
            val cheapest = IntArray(vertexCount) { -1 }
            
            for ((u, neighbors) in graph) {
                val uRoot = unionFind.find(u)
                
                for ((v, weight) in neighbors) {
                    val vRoot = unionFind.find(v)
                    
                    if (uRoot != vRoot) {
                        if (cheapest[uRoot] == -1 || 
                            weight < graph[u]!![cheapest[uRoot]].second) {
                            cheapest[uRoot] = neighbors.indexOfFirst { it.first == v }
                        }
                    }
                }
            }
            
            for (u in 0 until vertexCount) {
                if (cheapest[u] != -1) {
                    val (v, weight) = graph[u]!![cheapest[u]]
                    val uRoot = unionFind.find(u)
                    val vRoot = unionFind.find(v)
                    
                    if (uRoot != vRoot) {
                        mst.add(EdgeK(u, v, weight))
                        totalWeight += weight
                        unionFind.union(uRoot, vRoot)
                        treeCount--
                    }
                }
            }
        }
        
        return mst
    }
    
    fun getTotalWeight(): Int = totalWeight
}
```

### Пример использования

```kotlin
fun main() {
    val graph = mapOf(
        0 to listOf(Pair(1, 8), Pair(2, 5)),
        1 to listOf(Pair(0, 8), Pair(2, 9), Pair(3, 11)),
        2 to listOf(Pair(0, 5), Pair(1, 9), Pair(3, 15), Pair(4, 10)),
        3 to listOf(Pair(1, 11), Pair(2, 15), Pair(4, 7)),
        4 to listOf(Pair(2, 10), Pair(3, 7))
    )
    
    val boruvka = BoruvkaMSTK()
    val mst = boruvka.getMST(graph, 5)
    
    println("Total weight: ${boruvka.getTotalWeight()}") // 30
    println("Edges: ${mst.size}") // 4
}
```

## Сложность

### Временная сложность

- **Все случаи:** O(E*log(V)), где E - количество ребер, а V - количество вершин

Алгоритм выполняет не более log(V) итераций, и на каждой итерации обрабатывает все E ребер.

### Пространственная сложность

- **Все случаи:** O(V + E) - для хранения графа, MST и UnionFind структуры

## Особенности

- **Параллелизация:** Алгоритм хорошо поддается параллелизации
- **Жадный подход:** На каждой итерации выбирает минимальные ребра для каждого дерева
- **Эффективность:** Работает за O(E*log(V)) время

## Применение

Алгоритм Борувки используется в:

- Параллельных вычислениях
- Распределенных системах
- Проектировании сетей
- Кластеризации данных

## Сравнение с другими алгоритмами MST

| Алгоритм | Временная сложность | Параллелизация | Применение |
|----------|---------------------|----------------|------------|
| Борувки | O(E*log(V)) | Отлично | Параллельные вычисления |
| Прима | O(E*log(V)) | Сложно | Плотные графы |
| Крускала | O(E*log(E)) | Хорошо | Разреженные графы |

## Когда использовать

### Используйте алгоритм Борувки, когда:

- Нужна параллельная реализация
- Граф большой и распределен
- Важна эффективность на параллельных системах

### Альтернативы:

- **Прима:** Для последовательных вычислений на плотных графах
- **Крускала:** Для последовательных вычислений на разреженных графах

## Преимущества и недостатки

### Преимущества

- Хорошо поддается параллелизации
- Простая логика на каждой итерации
- Эффективен для больших графов

### Недостатки

- Может быть медленнее, чем Прима или Крускала на последовательных системах
- Требует больше памяти для хранения промежуточных результатов

## Заключение

В этом уроке мы увидели реализацию алгоритма Борувки на Java. Его временная сложность равна O(E*log(V)), где E - количество ребер, а V - количество вершин. Алгоритм особенно полезен для параллельных вычислений и больших распределенных графов.
