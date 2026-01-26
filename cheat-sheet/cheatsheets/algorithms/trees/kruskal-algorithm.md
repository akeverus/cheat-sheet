# Kruskal's Algorithm

Кратко: алгоритм Крускала - это жадный алгоритм для нахождения минимального остовного дерева (MST) взвешенного неориентированного графа. Использует Union-Find для обнаружения циклов. Временная сложность O(E*log(E)) или O(E*log(V)).

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Kruskal's Minimum Spanning Tree Algorithm](https://www.geeksforgeeks.org/kruskals-minimum-spanning-tree-algorithm-greedy-algo-2/)

### Визуализация
- [Visualgo: Minimum Spanning Tree](https://visualgo.net/en/mst)

### См. также
- `./prim-algorithm.md` - алгоритм Прима
- `./boruvka-algorithm.md` - алгоритм Борувки
- `../graphs/dijkstra.md` - алгоритм Дейкстры

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Принцип работы](#принцип-работы)
- [Union-Find структура данных](#union-find-структура-данных)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В предыдущей статье мы представили алгоритм Прима для поиска минимальных остовных деревьев. В этой статье мы будем использовать другой подход, алгоритм Крускала, для решения задач минимального и максимального остовных деревьев.

Остовное дерево неориентированного графа - это связный подграф, покрывающий все вершины графа с минимально возможным числом ребер. В общем случае граф может иметь более одного остовного дерева.

Если граф взвешен по ребрам, мы можем определить вес остовного дерева как сумму весов всех его ребер. Минимальное остовное дерево - это остовное дерево, вес которого наименьший среди всех возможных остовных деревьев.

Точно так же максимальное остовное дерево имеет наибольший вес среди всех остовных деревьев.

## Принцип работы

Имея граф, мы можем использовать алгоритм Крускала, чтобы найти его минимальное остовное дерево. Если количество узлов в графе равно V, то каждое из его остовных деревьев должно иметь (V - 1) ребер и не содержать циклов.

### Псевдокод

```
Initialize an empty edge set T.
Sort all graph edges by the ascending order of their weight values.
foreach edge in the sorted edge list
    Check whether it will create a cycle with the edges inside T.
    If the edge doesn't introduce any cycles, add it into T.
    If T has (V - 1) edges, exit the loop.
return T
```

### Пошаговый пример

Давайте шаг за шагом запустим алгоритм Крускала для минимального остовного дерева на нашем образце графа:

1. Во-первых, мы выбираем ребро (0, 2), потому что оно имеет наименьший вес
2. Затем мы можем добавить ребра (3, 4) и (0, 1), так как они не создают циклов
3. Теперь следующим кандидатом является ребро (1, 2) с весом 9. Однако, если мы включим это ребро, мы получим цикл (0, 1, 2). Поэтому мы отбрасываем это ребро и продолжаем выбирать следующее наименьшее
4. Наконец, алгоритм завершается добавлением ребра (2, 4) веса 10

Чтобы вычислить максимальное остовное дерево, мы можем изменить порядок сортировки на убывающий. Остальные шаги остаются прежними.

## Union-Find структура данных

В алгоритме Крускала ключевой частью является проверка того, создаст ли ребро цикл, если мы добавим его к существующему набору ребер. Есть несколько алгоритмов обнаружения циклов графа, которые мы можем использовать. Например, мы можем использовать алгоритм поиска в глубину (DFS), чтобы пройти по графу и определить, есть ли цикл.

Однако нам нужно выполнять обнаружение циклов на существующих ребрах каждый раз, когда мы тестируем новое ребро. Более быстрым решением является использование алгоритма Union-Find с непересекающейся структурой данных, поскольку он также использует подход с пошаговым добавлением ребер для обнаружения циклов.

### Принцип работы Union-Find

Во-первых, мы рассматриваем каждый узел графа как отдельное множество, содержащее только один узел. Затем каждый раз, когда мы вводим ребро, мы проверяем, принадлежат ли два его узла одному и тому же множеству. Если ответ да, то он создаст цикл. В противном случае мы объединяем два непересекающихся множества в одно множество и включаем ребро для остовного дерева.

Например, в приведенной выше конструкции минимального связующего дерева у нас сначала есть 5 наборов узлов: {0}, {1}, {2}, {3}, {4}. Когда мы проверяем первое ребро (0, 2), два его узла находятся в разных наборах узлов. Следовательно, мы можем включить это ребро и объединить {0} и {2} в один набор {0, 2}.

Мы можем сделать аналогичные операции для ребер (3, 4) и (0, 1). Затем наборы узлов становятся {0, 1, 2} и {3, 4}. Когда мы проверяем следующее ребро (1, 2), мы видим, что оба узла этого ребра находятся в одном множестве. Поэтому отбрасываем это ребро и продолжаем проверять следующее. Наконец, ребро (2, 4) удовлетворяет нашему условию, и мы можем включить его в минимальное остовное дерево.

## Java Implementation

### Реализация

Мы можем использовать древовидную структуру для представления непересекающегося множества. Каждый узел имеет родительский указатель для ссылки на его родительский узел. В каждом наборе есть уникальный корневой узел, представляющий этот набор. Корневой узел имеет родительский указатель, ссылающийся на себя.

### Класс DisjointSetInfo

Давайте используем класс Java для определения информации о непересекающихся множествах:

```java
public class DisjointSetInfo {
    private Integer parentNode;
    private int rank;
    
    DisjointSetInfo(Integer parent) {
        setParentNode(parent);
        setRank(0);
    }
    
    // Геттеры и сеттеры
    public Integer getParentNode() {
        return parentNode;
    }
    
    public void setParentNode(Integer parentNode) {
        this.parentNode = parentNode;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
}
```

### Инициализация

Давайте пометим каждый узел графа целым числом, начиная с 0. Мы можем использовать структуру данных списка `List<DisjointSetInfo> nodes` для хранения информации о непересекающихся множествах графа. Вначале каждый узел является представителем своего собственного набора:

```java
void initDisjointSets(int totalNodes) {
    nodes = new ArrayList<>(totalNodes);
    for (int i = 0; i < totalNodes; i++) {
        nodes.add(new DisjointSetInfo(i));
    }
}
```

### Операция Find

Чтобы найти набор, которому принадлежит узел, мы можем следовать родительской цепочке узла вверх, пока не достигнем корневого узла:

```java
Integer find(Integer node) {
    Integer parent = nodes.get(node).getParentNode();
    if (parent.equals(node)) {
        return node;
    } else {
        return find(parent);
    }
}
```

### Сжатие пути (Path Compression)

Для непересекающегося множества может быть сильно несбалансированная древовидная структура. Мы можем улучшить операцию поиска, используя метод сжатия пути.

Поскольку каждый узел, который мы посещаем на пути к корневому узлу, является частью одного и того же набора, мы можем напрямую присоединить корневой узел к его родительской ссылке. В следующий раз, когда мы посетим этот узел, нам понадобится один путь поиска, чтобы получить корневой узел:

```java
Integer pathCompressionFind(Integer node) {
    DisjointSetInfo setInfo = nodes.get(node);
    Integer parent = setInfo.getParentNode();
    
    if (parent.equals(node)) {
        return node;
    } else {
        Integer parentNode = find(parent);
        setInfo.setParentNode(parentNode);
        return parentNode;
    }
}
```

### Операция Union

Если два узла ребра находятся в разных наборах, мы объединим эти два набора в один. Мы можем выполнить эту операцию объединения, установив корень одного репрезентативного узла в другой репрезентативный узел:

```java
void union(Integer rootU, Integer rootV) {
    DisjointSetInfo setInfoU = nodes.get(rootU);
    setInfoU.setParentNode(rootV);
}
```

### Union по рангу

Эта простая операция объединения может привести к сильно несбалансированному дереву, поскольку мы выбрали случайный корневой узел для объединенного множества. Мы можем улучшить производительность, используя метод объединения по рангу.

Поскольку именно глубина дерева влияет на время выполнения операции поиска, мы присоединяем набор с более коротким деревом к набору с более длинным деревом. Этот метод увеличивает глубину объединенного дерева только в том случае, если исходные два дерева имеют одинаковую глубину.

Вначале один непересекающийся узел имеет ранг 0. При объединении двух множеств корневым узлом объединенного множества становится корневой узел с более высоким рангом. Мы увеличиваем ранг нового корневого узла на единицу, только если исходные два ранга совпадают:

```java
void unionByRank(int rootU, int rootV) {
    DisjointSetInfo setInfoU = nodes.get(rootU);
    DisjointSetInfo setInfoV = nodes.get(rootV);
    int rankU = setInfoU.getRank();
    int rankV = setInfoV.getRank();
    
    if (rankU < rankV) {
        setInfoU.setParentNode(rootV);
    } else {
        setInfoV.setParentNode(rootU);
        if (rankU == rankV) {
            setInfoU.setRank(rankU + 1);
        }
    }
}
```

### Обнаружение циклов

Мы можем определить, находятся ли два узла в одном и том же непересекающемся множестве, сравнив результаты двух операций поиска. Если у них один и тот же репрезентативный корневой узел, значит, мы обнаружили цикл. В противном случае мы объединяем два непересекающихся множества с помощью операции объединения:

```java
boolean detectCycle(Integer u, Integer v) {
    Integer rootU = pathCompressionFind(u);
    Integer rootV = pathCompressionFind(v);
    
    if (rootU.equals(rootV)) {
        return true;
    }
    
    unionByRank(rootU, rootV);
    return false;
}
```

Обнаружение цикла, только с методом объединения по рангу, имеет время выполнения O(logV). Мы можем добиться лучшей производительности как при сжатии пути, так и при объединении по рангу. Время выполнения равно O(α(V)), где α(V) - обратная функция Аккермана от общего числа узлов. Это небольшая константа, которая меньше 5 в наших реальных вычислениях.

### Полная реализация с Guava

Мы можем использовать структуру данных ValueGraph в Google Guava для представления графа, взвешенного по ребрам.

Чтобы использовать ValueGraph, нам сначала нужно добавить зависимость Guava в файл pom.xml нашего проекта:

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

Мы можем обернуть описанные выше методы обнаружения цикла в класс CycleDetector и использовать его в алгоритме Крускала. Поскольку алгоритмы построения минимального и максимального остовного дерева имеют лишь небольшую разницу, мы можем использовать одну общую функцию для достижения обеих конструкций:

```java
ValueGraph<Integer, Double> spanningTree(ValueGraph<Integer, Double> graph, boolean minSpanningTree) {
    Set<EndpointPair> edges = graph.edges();
    List<EndpointPair> edgeList = new ArrayList<>(edges);
    
    if (minSpanningTree) {
        edgeList.sort(Comparator.comparing(e -> graph.edgeValue(e).get()));
    } else {
        edgeList.sort(Collections.reverseOrder(Comparator.comparing(e -> graph.edgeValue(e).get())));
    }
    
    int totalNodes = graph.nodes().size();
    CycleDetector cycleDetector = new CycleDetector(totalNodes);
    int edgeCount = 0;
    MutableValueGraph<Integer, Double> spanningTree = ValueGraphBuilder.undirected().build();
    
    for (EndpointPair edge : edgeList) {
        if (cycleDetector.detectCycle(edge.nodeU(), edge.nodeV())) {
            continue;
        }
        
        spanningTree.putEdgeValue(edge.nodeU(), edge.nodeV(), graph.edgeValue(edge).get());
        edgeCount++;
        
        if (edgeCount == totalNodes - 1) {
            break;
        }
    }
    
    return spanningTree;
}
```

## Kotlin Implementation

### Класс DisjointSetInfo

```kotlin
data class DisjointSetInfoK(
    var parentNode: Int,
    var rank: Int = 0
)
```

### Union-Find операции

```kotlin
class UnionFindK(private val totalNodes: Int) {
    private val nodes = mutableListOf<DisjointSetInfoK>()
    
    init {
        for (i in 0 until totalNodes) {
            nodes.add(DisjointSetInfoK(i))
        }
    }
    
    fun find(node: Int): Int {
        val parent = nodes[node].parentNode
        return if (parent == node) {
            node
        } else {
            val parentNode = find(parent)
            nodes[node].parentNode = parentNode
            parentNode
        }
    }
    
    fun unionByRank(rootU: Int, rootV: Int) {
        val setInfoU = nodes[rootU]
        val setInfoV = nodes[rootV]
        val rankU = setInfoU.rank
        val rankV = setInfoV.rank
        
        when {
            rankU < rankV -> setInfoU.parentNode = rootV
            rankU > rankV -> setInfoV.parentNode = rootU
            else -> {
                setInfoV.parentNode = rootU
                setInfoU.rank = rankU + 1
            }
        }
    }
}
```

### Алгоритм Крускала

```kotlin
data class EdgeK(val u: Int, val v: Int, val weight: Int)

fun kruskalMSTK(edges: List<EdgeK>, totalNodes: Int): List<EdgeK> {
    val sortedEdges = edges.sortedBy { it.weight }
    val unionFind = UnionFindK(totalNodes)
    val spanningTree = mutableListOf<EdgeK>()
    var edgeCount = 0
    
    for (edge in sortedEdges) {
        val rootU = unionFind.find(edge.u)
        val rootV = unionFind.find(edge.v)
        
        if (rootU != rootV) {
            unionFind.unionByRank(rootU, rootV)
            spanningTree.add(edge)
            edgeCount++
            
            if (edgeCount == totalNodes - 1) {
                break
            }
        }
    }
    
    return spanningTree
}
```

### Пример использования

```kotlin
fun main() {
    val edges = listOf(
        EdgeK(0, 1, 7),
        EdgeK(0, 2, 8),
        EdgeK(1, 2, 3),
        EdgeK(1, 3, 6),
        EdgeK(2, 3, 4),
        EdgeK(2, 4, 3),
        EdgeK(3, 4, 2),
        EdgeK(3, 5, 5),
        EdgeK(4, 5, 2)
    )
    
    val mst = kruskalMSTK(edges, 6)
    println("MST edges: ${mst.size}") // 5
}
```

## Сложность

### Временная сложность

В алгоритме Крускала мы сначала сортируем все ребра графа по их весам. Эта операция занимает время O(E*log(E)), где E - общее количество ребер.

Затем мы используем цикл для просмотра отсортированного списка ребер. На каждой итерации мы проверяем, будет ли сформирован цикл, добавляя ребро в текущий набор ребер связующего дерева. Этот цикл с обнаружением цикла занимает не более O(E*log(V)) времени.

Таким образом, общее время работы составляет O(E*log(E) + E*log(V)). Поскольку значение E находится в масштабе O(V²), временная сложность алгоритма Крускала составляет O(E*log(E)) или O(E*log(V)).

- **Сортировка:** O(E*log(E))
- **Union-Find операции:** O(E*α(V)), где α - обратная функция Аккермана
- **Общая:** O(E*log(E)) или O(E*log(V))

### Пространственная сложность

- **Все случаи:** O(V + E) - для хранения графа и Union-Find структуры

## Особенности

- **Жадный алгоритм:** На каждом шаге выбирает ребро с минимальным весом
- **Union-Find:** Эффективное обнаружение циклов
- **Гибкость:** Работает как для минимального, так и для максимального MST

## Применение

Алгоритм Крускала используется в:

- Проектировании сетей
- Кластеризации данных
- Анализе изображений
- Планировании маршрутов
- Оптимизации инфраструктуры

## Сравнение с другими алгоритмами MST

| Алгоритм | Временная сложность | Применение |
|----------|---------------------|------------|
| Крускала | O(E*log(E)) | Разреженные графы |
| Прима | O(E*log(V)) | Плотные графы |
| Борувки | O(E*log(V)) | Параллельные вычисления |

## Когда использовать

### Используйте алгоритм Крускала, когда:

- Граф разреженный (мало ребер)
- Нужна простая реализация
- Важна производительность на разреженных графах

### Альтернативы:

- **Прима:** Для плотных графов
- **Борувки:** Для параллельных вычислений

## Заключение

В этой статье мы рассмотрели алгоритм Крускала для нахождения минимального остовного дерева. Алгоритм работает жадным образом, сортируя все ребра по весу и добавляя их в MST, если они не создают циклов. Использование Union-Find структуры данных делает обнаружение циклов эффективным, что приводит к общей временной сложности O(E*log(E)).
