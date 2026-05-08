---
title: "Вопросы на собеседовании: Графы"
description: "Представление графов (матрица/список смежности), BFS, DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, Kruskal, Prim, топологическая сортировка, SCC, графы в реальной жизни"
tags:
  - interview
  - algorithms
  - graphs-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Графы"
  - "Graphs interview"
  - "Графы собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Графы`

Графы — основа социальных сетей, маршрутизации, зависимостей сборки, рекомендаций. На интервью спрашивают BFS/DFS, Dijkstra, топологическую сортировку, MST, обнаружение циклов и компонент связности. Всё это — `O(V + E)` или `O(E log V)`, и важно понимать когда какой подход применять.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Graph Data Structure in Java — Baeldung](https://www.baeldung.com/java-graphs)
- [Dijkstra's Algorithm in Java — Baeldung](https://www.baeldung.com/java-dijkstra)
- [Bellman-Ford Algorithm — Baeldung](https://www.baeldung.com/cs/bellman-ford)
- [Floyd-Warshall — Baeldung](https://www.baeldung.com/cs/floyd-warshall-shortest-path)
- [Kruskal vs Prim — Baeldung](https://www.baeldung.com/cs/prim-vs-kruskal)
- [Topological Sort — Baeldung](https://www.baeldung.com/cs/topological-sort)
- [JGraphT Library](https://jgrapht.org/) — Java graph library
- [Visualgo Graph Algorithms](https://visualgo.net/en/graphds)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое граф и какие виды бывают?](#q1--что-такое-граф-и-какие-виды-бывают)
- [Q2. (!) Как представляют графы в коде?](#q2--как-представляют-графы-в-коде)
- [Q3. Когда матрица, а когда список смежности?](#q3-когда-матрица-а-когда-список-смежности)
- [Q4. Что такое плотный и разреженный граф?](#q4-что-такое-плотный-и-разреженный-граф)

**Обходы**
- [Q5. (!) BFS — Breadth First Search?](#q5--bfs--breadth-first-search)
- [Q6. (!) DFS — Depth First Search?](#q6--dfs--depth-first-search)
- [Q7. (!) Чем BFS отличается от DFS?](#q7--чем-bfs-отличается-от-dfs)
- [Q8. (!) Кратчайший путь в невзвешенном графе?](#q8--кратчайший-путь-в-невзвешенном-графе)
- [Q9. Bidirectional BFS — двунаправленный поиск?](#q9-bidirectional-bfs--двунаправленный-поиск)

**Кратчайшие пути в взвешенных графах**
- [Q10. (!) Алгоритм Дейкстры?](#q10--алгоритм-дейкстры)
- [Q11. (!) Алгоритм Беллмана-Форда?](#q11--алгоритм-беллмана-форда)
- [Q12. Когда Dijkstra не работает?](#q12-когда-dijkstra-не-работает)
- [Q13. (!) Floyd-Warshall — все пары?](#q13--floyd-warshall--все-пары)
- [Q14. A* поиск — что это?](#q14-a-поиск--что-это)

**Минимальное остовное дерево (MST)**
- [Q15. (!) Что такое MST?](#q15--что-такое-mst)
- [Q16. (!) Алгоритм Краскала?](#q16--алгоритм-краскала)
- [Q17. (!) Алгоритм Прима?](#q17--алгоритм-прима)
- [Q18. Чем Краскал отличается от Прима?](#q18-чем-краскал-отличается-от-прима)

**Циклы и компоненты**
- [Q19. (!) Как обнаружить цикл в неориентированном графе?](#q19--как-обнаружить-цикл-в-неориентированном-графе)
- [Q20. (!) Как обнаружить цикл в ориентированном графе?](#q20--как-обнаружить-цикл-в-ориентированном-графе)
- [Q21. Найти компоненты связности?](#q21-найти-компоненты-связности)
- [Q22. (!) Найти strongly connected components (SCC)?](#q22--найти-strongly-connected-components-scc)
- [Q23. Bipartite graph — проверка двудольности?](#q23-bipartite-graph--проверка-двудольности)

**Топологическая сортировка**
- [Q24. (!) Топологическая сортировка — что и зачем?](#q24--топологическая-сортировка--что-и-зачем)
- [Q25. (!) Алгоритм Кана (BFS-подход)?](#q25--алгоритм-кана-bfs-подход)
- [Q26. Топологическая сортировка через DFS?](#q26-топологическая-сортировка-через-dfs)

**Прикладные задачи**
- [Q27. (!) Number of Islands — задача с матрицей?](#q27--number-of-islands--задача-с-матрицей)
- [Q28. (!) Course Schedule — обнаружение цикла в зависимостях?](#q28--course-schedule--обнаружение-цикла-в-зависимостях)
- [Q29. Word Ladder — кратчайшая трансформация слов?](#q29-word-ladder--кратчайшая-трансформация-слов)
- [Q30. Network Delay Time — Dijkstra-задача?](#q30-network-delay-time--dijkstra-задача)
- [Q31. (!) Clone Graph — глубокое копирование?](#q31--clone-graph--глубокое-копирование)

**Real-world применения**
- [Q32. (!) Где графы встречаются в реальной разработке?](#q32--где-графы-встречаются-в-реальной-разработке)
- [Q33. Что такое graph database? Когда выбирать?](#q33-что-такое-graph-database-когда-выбирать)
- [Q34. (!) PageRank — как работает?](#q34--pagerank--как-работает)

## Q1. (!) Что такое граф и какие виды бывают?

**Граф** `G = (V, E)` — множество **вершин** (V) и **рёбер** (E), соединяющих пары вершин.

**Виды:**

| Тип | Описание |
|-----|----------|
| **Ориентированный (directed)** | Рёбра имеют направление |
| **Неориентированный (undirected)** | Рёбра двунаправленные |
| **Взвешенный (weighted)** | Каждое ребро имеет вес |
| **Невзвешенный** | Все рёбра одинаковы |
| **Циклический** | Содержит хотя бы один цикл |
| **Ациклический (DAG)** | Нет циклов, ориентированный |
| **Связный** | Между любыми двумя вершинами есть путь |
| **Полный** | Все вершины попарно соединены |
| **Bipartite (двудольный)** | Вершины разбиваются на 2 группы, рёбра только между группами |
| **Tree** | Связный, ациклический, с `V - 1` рёбрами |

**Применения:**
- Социальные сети (друзья, подписки)
- Маршрутизация (карты, интернет)
- Зависимости (Maven/Gradle, Spring beans)
- Рекомендации (e-commerce)


> [!mcq]
> - [ ] DAG — граф без рёбер (empty graph) | ❌ ПОСЛЕДСТВИЕ: DAG = Directed Acyclic Graph = ориентированный без циклов; рёбра есть, просто нет цикла
> - [ ] Tree — связный граф с V+1 рёбрами | ❌ ПОСЛЕДСТВИЕ: Tree = V-1 рёбра; V+1 рёбер означает есть цикл → не дерево по определению
> - [x] G=(V,E): directed/undirected, weighted, DAG (ориентированный ациклический), bipartite (рёбра только между двумя группами), tree (connected+acyclic+V-1 рёбер) | ✓ ПРИМЕНЯТЬ: классификация → назвать все 6 видов с примерами 📋 ПРАВИЛО: DAG=no-cycle-directed; Tree=connected+acyclic+V-1edges; Bipartite=no-odd-cycle 🔗 См. Q2
> - [ ] Bipartite граф допускает нечётные циклы | ❌ ПОСЛЕДСТВИЕ: bipartite ⟺ НЕТ нечётных циклов; нечётный цикл → граф НЕ двудольный (теорема König)

## Q2. (!) Как представляют графы в коде?

**1. Матрица смежности — `boolean[V][V]` или `int[V][V]`:**

```java
int[][] matrix = new int[V][V];
matrix[u][v] = weight; // ребро u → v с весом
matrix[v][u] = weight; // для неориентированного
```

**2. Список смежности — `List<List<Integer>>` или `Map<V, List<V>>`:**

```java
List<List<int[]>> adj = new ArrayList<>(); // список пар {to, weight}
for (int i = 0; i < V; i++) adj.add(new ArrayList<>());
adj.get(u).add(new int[]{v, weight});
```

**3. Список рёбер — `List<Edge>`:**

```java
record Edge(int from, int to, int weight) {}
List<Edge> edges = new ArrayList<>();
```

В Java для production рекомендуется библиотека **JGraphT**.


> [!mcq]
> - [ ] Матрица смежности — всегда лучший выбор: O(1) проверка ребра | ❌ ПОСЛЕДСТВИЕ: для разреженного графа O(V²) памяти вместо O(V+E); перебор соседей O(V) вместо O(degree)
> - [x] Список смежности O(V+E) — стандарт для разреженных графов; матрица O(V²) — когда E ≈ V² и нужна O(1) проверка ребра | ✓ ПРИМЕНЯТЬ: разреженный граф → список; плотный + много проверок ребер → матрица 📋 ПРАВИЛО: список = память O(V+E) = почти всегда лучше 🔗 См. Q3
> - [ ] Edge list — лучшее представление для обхода BFS/DFS | ❌ ПОСЛЕДСТВИЕ: для BFS нужны соседи вершины O(1); edge list требует O(E) scan для каждой вершины
> - [ ] Список смежности и матрица идентичны по памяти для любого графа | ❌ ПОСЛЕДСТВИЕ: для sparse (E ≈ V) матрица тратит O(V²), список O(V+E) ≈ O(V); разница может быть гигабайты

## Q3. Когда матрица, а когда список смежности?

| Критерий | Матрица | Список смежности |
|----------|---------|-------------------|
| Память | `O(V²)` | `O(V + E)` |
| Проверка ребра `(u, v)` | `O(1)` | `O(degree(u))` |
| Перебор соседей `u` | `O(V)` | `O(degree(u))` |
| Добавление ребра | `O(1)` | `O(1)` |
| Удаление ребра | `O(1)` | `O(degree(u))` |
| Лучше для | Плотных графов | Разреженных графов |

**Правило:** если `E ≈ V²` — матрица, если `E ≈ V` — список.

В соцсетях (миллионы пользователей, средний фолловинг ~100) — список. В картах (~10 связей на перекрёсток) — список.


> [!mcq]
> - [ ] Матрица всегда лучше для алгоритмов типа Dijkstra — O(1) доступ к ребру | ❌ ПОСЛЕДСТВИЕ: для sparse графа Dijkstra с heap + список O(V log V); матрица + O(1) доступ даёт O(V²) — хуже на sparse
> - [ ] Список смежности всегда быстрее матрицы — матрицу не использовать | ❌ ПОСЛЕДСТВИЕ: для dense E ≈ V² матрица Dijkstra O(V²) vs heap O(V² log V); матрица может быть лучше
> - [x] E ≈ V² → матрица (dense, O(1) edge lookup выгоден); E << V² → список (sparse, экономия памяти O(V+E)) | ✓ ПРИМЕНЯТЬ: routing tables (dense) → матрица; соцсети (sparse) → список 📋 ПРАВИЛО: E/V² > 0.5 → матрица; иначе список 🔗 См. Q4
> - [ ] Выбор зависит только от числа вершин V, не от числа рёбер | ❌ ПОСЛЕДСТВИЕ: матрица O(V²) независимо от E; при 1M вершин и 2M рёбер матрица — 1TB памяти, список — 16MB

## Q4. Что такое плотный и разреженный граф?

- **Плотный (dense):** `E ≈ V²` (близко к максимуму `V·(V-1)/2`)
- **Разреженный (sparse):** `E << V²` (часто `E = O(V)`)

Большинство реальных графов **разреженные** (соцсети, дорожные карты, граф зависимостей).

| Алгоритм | Sparse `E ≈ V` | Dense `E ≈ V²` |
|----------|----------------|-----------------|
| BFS/DFS | `O(V)` | `O(V²)` |
| Dijkstra (с heap) | `O(V log V)` | `O(V² log V)` |
| Dijkstra (с массивом) | `O(V²)` | `O(V²)` |
| Floyd-Warshall | `O(V³)` | `O(V³)` |

Для плотных графов матрица + Dijkstra на массиве может оказаться быстрее.


> [!mcq]
> - [ ] Разреженный граф = менее 100 рёбер; плотный = более 100 | ❌ ПОСЛЕДСТВИЕ: разреженность относительна: граф с 1000V и 1000E sparse (E ≈ V); с 1000V и 500000E dense (E ≈ V²)
> - [ ] Соцсети — плотные графы (каждый со всеми связан) | ❌ ПОСЛЕДСТВИЕ: Facebook с 3B пользователей и средним ~150 друзьями крайне sparse: E ≈ 150·V << V² ≈ 9·10¹⁸
> - [ ] Floyd-Warshall быстрее Dijkstra на разреженных графах | ❌ ПОСЛЕДСТВИЕ: Floyd-Warshall O(V³) одинаково для любого графа; Dijkstra с heap O((V+E)log V) = O(V log V) на sparse — намного быстрее
> - [x] Sparse: E = O(V) << V²; dense: E ≈ V²; большинство реальных графов sparse (соцсети, роуты, зависимости) | ✓ ПРИМЕНЯТЬ: классификация для выбора алгоритма и структуры данных 📋 ПРАВИЛО: sparse = E << V²; dense = E ≈ V(V-1)/2 🔗 См. Q3

## Q5. (!) BFS — Breadth First Search?

Обход графа **по уровням**: посещаем все вершины на расстоянии 1, потом 2, и т.д. Использует **очередь**.

```java
void bfs(List<List<Integer>> adj, int start) {
    boolean[] visited = new boolean[adj.size()];
    Queue<Integer> queue = new ArrayDeque<>();
    queue.offer(start);
    visited[start] = true;

    while (!queue.isEmpty()) {
        int u = queue.poll();
        process(u);
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                visited[v] = true;
                queue.offer(v);
            }
        }
    }
}
```

`O(V + E)` время, `O(V)` память.

**Применения:**
- Кратчайший путь в невзвешенном графе
- Найти все вершины на расстоянии K
- Двудольная проверка
- Web crawling, social network friends-of-friends


> [!mcq]
> - [ ] BFS использует стек, DFS использует очередь | ❌ ПОСЛЕДСТВИЕ: наоборот: BFS = Queue (FIFO для level-by-level); DFS = Stack/recursion (LIFO для глубины); перепутать = получить неправильный обход
> - [ ] BFS находит кратчайший путь в взвешенных графах | ❌ ПОСЛЕДСТВИЕ: BFS находит кратчайший путь только в невзвешенных; для взвешенных нужен Dijkstra/Bellman-Ford
> - [x] BFS = Queue, обход по уровням, кратчайший путь в невзвешенном; O(V+E) время, O(w) память (w = ширина уровня) | ✓ ПРИМЕНЯТЬ: shortest path невзвешенный, level-order, bipartite check 📋 ПРАВИЛО: BFS = очередь = уровни = shortest hops 🔗 См. Q7
> - [ ] BFS не находит все вершины, связанные с источником | ❌ ПОСЛЕДСТВИЕ: BFS гарантированно обходит все достижимые вершины; невидимые — только disconnected компоненты

## Q6. (!) DFS — Depth First Search?

Идём «вглубь» сколько можем, потом возвращаемся. Использует **стек** (рекурсия или явный).

```java
// Рекурсивный
void dfs(List<List<Integer>> adj, int u, boolean[] visited) {
    visited[u] = true;
    process(u);
    for (int v : adj.get(u)) {
        if (!visited[v]) dfs(adj, v, visited);
    }
}

// Итеративный через стек
void dfsIterative(List<List<Integer>> adj, int start) {
    boolean[] visited = new boolean[adj.size()];
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(start);
    while (!stack.isEmpty()) {
        int u = stack.pop();
        if (visited[u]) continue;
        visited[u] = true;
        process(u);
        for (int v : adj.get(u)) {
            if (!visited[v]) stack.push(v);
        }
    }
}
```

`O(V + E)` время, `O(V)` память (стек/visited). Стек вызовов до `O(V)` — для больших графов используй итеративный.

**Применения:**
- Топологическая сортировка
- Обнаружение циклов
- Поиск SCC (Tarjan, Kosaraju)
- Backtracking, генерация перестановок
- Maze solving


> [!mcq]
> - [ ] DFS находит кратчайший путь лучше BFS — идёт глубже | ❌ ПОСЛЕДСТВИЕ: DFS не находит кратчайший путь в невзвешенном графе; он может найти длинный путь первым; только BFS гарантирует shortest hops
> - [ ] DFS не может обнаружить циклы в графе | ❌ ПОСЛЕДСТВИЕ: DFS с цветами (white/gray/black) — стандартный алгоритм обнаружения циклов; back edge в DFS tree = цикл
> - [x] DFS = Stack/рекурсия, идёт в глубину; O(V+E) время, O(h) память (h = глубина); применяется для topological sort, SCC, backtracking | ✓ ПРИМЕНЯТЬ: topological sort, cycle detection, SCC, maze solving 📋 ПРАВИЛО: DFS = стек = глубина = структура графа 🔗 См. Q7
> - [ ] DFS всегда быстрее BFS — меньше памяти O(h) vs O(w) | ❌ ПОСЛЕДСТВИЕ: оба O(V+E) по времени; память зависит от формы графа: для дерева O(h) vs O(w) — разная но не всегда лучше DFS

## Q7. (!) Чем BFS отличается от DFS?

| Критерий | BFS | DFS |
|----------|-----|-----|
| Структура данных | Queue (FIFO) | Stack (LIFO) или рекурсия |
| Память | `O(w)`, w = ширина уровня | `O(h)`, h = глубина |
| Кратчайший путь | Невзвешенный — да | Не находит |
| Топологическая сортировка | Да (Кан) | Да |
| Обнаружение цикла | Да (с осторожностью) | Естественно |
| Поиск SCC | Не подходит | Tarjan/Kosaraju |
| Память для очень глубоких | Хуже (широкий слой) | Хуже (deep recursion) |
| Память для очень широких | Хуже | Лучше |

**BFS** — для shortest path, level-based.
**DFS** — для топологии, циклов, backtracking.


> [!mcq]
> - [ ] BFS и DFS одинаково подходят для shortest path — выбор произвольный | ❌ ПОСЛЕДСТВИЕ: DFS может найти длинный путь первым и остановиться; только BFS гарантирует минимальное количество рёбер
> - [x] BFS: shortest path в невзвешенном; DFS: topological sort, cycle detection, SCC; оба O(V+E) по времени | ✓ ПРИМЕНЯТЬ: shortest hops → BFS; структура графа (cycles, order) → DFS 📋 ПРАВИЛО: BFS = уровни = hops; DFS = глубина = структура 🔗 См. Q8
> - [ ] DFS лучше BFS для нахождения пути в labyrinths — всегда использовать DFS | ❌ ПОСЛЕДСТВИЕ: DFS находит путь но не минимальный; BFS найдёт кратчайший; A* ещё лучше для навигации с эвристикой
> - [ ] BFS подходит только для деревьев, а не для произвольных графов | ❌ ПОСЛЕДСТВИЕ: BFS работает на любом графе с visited[]; для деревьев — просто особый случай (нет циклов)

## Q8. (!) Кратчайший путь в невзвешенном графе?

**BFS** даёт кратчайший путь в невзвешенном графе:

```java
int bfsShortestPath(List<List<Integer>> adj, int start, int end) {
    boolean[] visited = new boolean[adj.size()];
    Queue<int[]> queue = new ArrayDeque<>(); // {вершина, расстояние}
    queue.offer(new int[]{start, 0});
    visited[start] = true;

    while (!queue.isEmpty()) {
        int[] curr = queue.poll();
        int u = curr[0], dist = curr[1];
        if (u == end) return dist;

        for (int v : adj.get(u)) {
            if (!visited[v]) {
                visited[v] = true;
                queue.offer(new int[]{v, dist + 1});
            }
        }
    }
    return -1;
}
```

Чтобы восстановить **сам путь** — храни `parent[v] = u` при посещении и пройдись от end до start.

`O(V + E)` время, `O(V)` память.


> [!mcq]
> - [ ] BFS восстанавливает путь автоматически — не нужно хранить parent[] | ❌ ПОСЛЕДСТВИЕ: BFS находит расстояние, но путь нужно восстанавливать через parent[v]=u; без него только длина пути
> - [x] BFS находит кратчайший путь в невзвешенном графе за O(V+E); путь восстанавливается через parent[] массив в обратном направлении | ✓ ПРИМЕНЯТЬ: shortest hops, word ladder, social network degrees 📋 ПРАВИЛО: BFS = гарантия кратчайшего пути в невзвешенном 🔗 См. Q5
> - [ ] BFS находит кратчайший путь только если граф ациклический | ❌ ПОСЛЕДСТВИЕ: BFS работает на любом графе с visited[]; visited предотвращает бесконечные циклы
> - [ ] Для взвешенного графа BFS даёт минимальный вес пути | ❌ ПОСЛЕДСТВИЕ: BFS минимизирует число рёбер, не сумму весов; для взвешенных нужен Dijkstra

## Q9. Bidirectional BFS — двунаправленный поиск?

**Bidirectional BFS** — запускаем BFS одновременно от start и end, останавливаемся когда фронты встретятся.

```java
int bidirBFS(List<List<Integer>> adj, int start, int end) {
    if (start == end) return 0;

    Set<Integer> visitedFromStart = new HashSet<>(Set.of(start));
    Set<Integer> visitedFromEnd = new HashSet<>(Set.of(end));
    int distance = 0;

    while (!visitedFromStart.isEmpty() && !visitedFromEnd.isEmpty()) {
        // Расширяем меньший фронт
        if (visitedFromStart.size() > visitedFromEnd.size()) {
            Set<Integer> tmp = visitedFromStart;
            visitedFromStart = visitedFromEnd;
            visitedFromEnd = tmp;
        }

        Set<Integer> next = new HashSet<>();
        for (int u : visitedFromStart) {
            for (int v : adj.get(u)) {
                if (visitedFromEnd.contains(v)) return distance + 1;
                next.add(v);
            }
        }
        visitedFromStart = next;
        distance++;
    }
    return -1;
}
```

**Сложность:** в среднем `O(b^(d/2))` вместо `O(b^d)` — где `b` — branching factor, `d` — расстояние. На practice экспоненциально быстрее обычного BFS.


> [!mcq]
> - [ ] Bidirectional BFS всегда быстрее обычного BFS на любом графе | ❌ ПОСЛЕДСТВИЕ: выигрыш только при высоком branching factor; на линейном графе оба O(V)
> - [x] Два BFS-фронта от start и end; стоп при пересечении; O(b^(d/2)) vs O(b^d) — экспоненциально быстрее | ✓ ПРИМЕНЯТЬ: social network degrees-of-separation; Google Maps между двумя точками 📋 ПРАВИЛО: biBFS ≈ sqrt(обычного BFS) по исследованным вершинам 🔗 См. Q5
> - [ ] Bidirectional BFS работает только на невзвешенных ориентированных графах | ❌ ПОСЛЕДСТВИЕ: biBFS работает на любом графе где есть обратные рёбра (undirected или reversible)
> - [ ] Bidirectional BFS не даёт кратчайший путь — нужен обычный BFS для точности | ❌ ПОСЛЕДСТВИЕ: при правильной реализации biBFS даёт точный кратчайший путь; ошибки только при неправильной проверке пересечения

## Q10. (!) Алгоритм Дейкстры?

Кратчайшие пути от одной вершины до всех в графе с **неотрицательными весами**.

```java
int[] dijkstra(List<List<int[]>> graph, int start) {
    int n = graph.size();
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;

    // {distance, vertex}
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    pq.offer(new int[]{0, start});

    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int d = curr[0], u = curr[1];
        if (d > dist[u]) continue; // устаревшая запись

        for (int[] edge : graph.get(u)) {
            int v = edge[0], w = edge[1];
            int newDist = dist[u] + w;
            if (newDist < dist[v]) {
                dist[v] = newDist;
                pq.offer(new int[]{newDist, v});
            }
        }
    }
    return dist;
}
```

| Реализация | Сложность |
|------------|-----------|
| С массивом | `O(V²)` |
| С бинарной кучей | `O((V + E) log V)` |
| С Fibonacci heap | `O(V log V + E)` |

**Применение:** GPS-навигация, IP-маршрутизация (OSPF), сетевая задержка.


> [!mcq]
> - [ ] Dijkstra работает с отрицательными весами рёбер — просто берёт минимальное | ❌ ПОСЛЕДСТВИЕ: Dijkstra с отрицательными весами даёт неверные результаты; greedy relaxation ломается когда вершина уже "посещена"
> - [ ] Dijkstra с отрицательными весами работает если граф ациклический | ❌ ПОСЛЕДСТВИЕ: для ациклических графов с отрицательными весами нужен topological sort + relaxation; Dijkstra всё равно не гарантирует корректность
> - [x] Dijkstra: PriorityQueue + relaxation, O((V+E)log V), только неотрицательные веса; для GPS, OSPF | ✓ ПРИМЕНЯТЬ: взвешенный граф без отрицательных весов; Dijkstra всегда быстрее Bellman-Ford 📋 ПРАВИЛО: Dijkstra = жадный + heap = O(E log V) но NO neg weights 🔗 См. Q11
> - [ ] Dijkstra требует V² памяти для PriorityQueue | ❌ ПОСЛЕДСТВИЕ: PriorityQueue хранит O(E) записей в худшем случае, не V²; с lazy deletion O(E log V) и O(E) памяти

## Q11. (!) Алгоритм Беллмана-Форда?

Кратчайшие пути от одной вершины **с отрицательными весами**. Может обнаружить **отрицательный цикл**.

```java
int[] bellmanFord(int n, List<int[]> edges, int start) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[start] = 0;

    // V-1 итераций релаксации всех рёбер
    for (int i = 0; i < n - 1; i++) {
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], w = edge[2];
            if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
            }
        }
    }

    // Проверка отрицательного цикла
    for (int[] edge : edges) {
        int u = edge[0], v = edge[1], w = edge[2];
        if (dist[u] != Integer.MAX_VALUE && dist[u] + w < dist[v]) {
            throw new IllegalStateException("Negative cycle detected!");
        }
    }
    return dist;
}
```

`O(V · E)` время. Медленнее Dijkstra, но универсальнее. Применение: forex arbitrage detection, общая маршрутизация с возможными «доплатами».


> [!mcq]
> - [ ] Bellman-Ford медленнее Dijkstra и не имеет преимуществ | ❌ ПОСЛЕДСТВИЕ: Bellman-Ford поддерживает отрицательные веса и обнаруживает отрицательные циклы — то чего Dijkstra не умеет; незаменим для forex arbitrage detection
> - [x] Bellman-Ford: V-1 итераций релаксации всех рёбер; O(VE); поддерживает отрицательные веса + обнаружение отрицательных циклов | ✓ ПРИМЕНЯТЬ: отрицательные веса, currency arbitrage, RIP routing protocol 📋 ПРАВИЛО: Bellman-Ford = V-1 passes = slow but neg-weight safe 🔗 См. Q12
> - [ ] Bellman-Ford требует сортировки рёбер по весу перед запуском | ❌ ПОСЛЕДСТВИЕ: никакой сортировки не нужно; рёбра обрабатываются в любом порядке V-1 раз; сортировка = Kruskal MST, не Bellman-Ford
> - [ ] Bellman-Ford работает только на DAG (ациклических) графах | ❌ ПОСЛЕДСТВИЕ: на DAG используют topological sort + relaxation O(V+E); Bellman-Ford работает на любом графе, включая cyclic

## Q12. Когда Dijkstra не работает?

**Dijkstra не работает с отрицательными весами.** Жадный выбор минимума может «зафиксировать» вершину с дистанцией, которая позже окажется ошибочной из-за отрицательного ребра.

Пример:
```
A → B: 5
A → C: 4
C → B: -2
```
От A: правильно `dist[B] = 4 + (-2) = 2`. Dijkstra зафиксирует `dist[C] = 4` первым (минимум), потом проверит B через C → 2. Это работает только если **граф не имеет negative cycles** и Dijkstra **не зафиксирует** вершину рано.

На практике — для отрицательных весов используем **Bellman-Ford** или **SPFA**.

Также **Dijkstra медленнее** на плотных графах с heap → лучше использовать массивную реализацию.


> [!mcq]
> - [ ] Dijkstra не работает с отрицательными рёбрами только при наличии отрицательного цикла | ❌ ПОСЛЕДСТВИЕ: Dijkstra даёт неверный результат с любыми отрицательными весами, даже без цикла: жадная фиксация вершины может произойти до оптимальной релаксации через отрицательное ребро
> - [x] Dijkstra не работает с отрицательными весами (greedy fix ломается); используй Bellman-Ford O(VE) или SPFA | ✓ ПРИМЕНЯТЬ: отрицательные веса → Bellman-Ford; DAG с neg edges → topological sort 📋 ПРАВИЛО: Dijkstra = только неотрицательные; neg weights = Bellman-Ford 🔗 См. Q11
> - [ ] Dijkstra не работает на направленных графах (digraph) | ❌ ПОСЛЕДСТВИЕ: Dijkstra прекрасно работает на directed и undirected; edge direction не влияет на алгоритм, только веса
> - [ ] Dijkstra не работает если в графе более 10K вершин | ❌ ПОСЛЕДСТВИЕ: Dijkstra масштабируется до миллионов вершин (GPS системы); сложность O((V+E)log V) позволяет обрабатывать большие графы

## Q13. (!) Floyd-Warshall — все пары?

Кратчайшие пути **между всеми парами** вершин. Работает с любыми весами (без отрицательных циклов).

```java
int[][] floydWarshall(int[][] graph) {
    int n = graph.length;
    int[][] dist = new int[n][n];
    for (int i = 0; i < n; i++) dist[i] = graph[i].clone();

    for (int k = 0; k < n; k++) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (dist[i][k] != Integer.MAX_VALUE
                    && dist[k][j] != Integer.MAX_VALUE
                    && dist[i][k] + dist[k][j] < dist[i][j]) {
                    dist[i][j] = dist[i][k] + dist[k][j];
                }
            }
        }
    }
    return dist;
}
```

`O(V³)` время, `O(V²)` память. Идеален для **малых плотных графов** (до 500 вершин). Для большего — все пары через Dijkstra/Bellman-Ford от каждой вершины.


> [!mcq]
> - [ ] Floyd-Warshall работает только для поиска пути между двумя конкретными вершинами | ❌ ПОСЛЕДСТВИЕ: Floyd-Warshall вычисляет ВСЕ пары вершин одновременно в O(V³); для одной пары лучше Dijkstra O((V+E)log V)
> - [ ] Floyd-Warshall быстрее Dijkstra для разреженных графов | ❌ ПОСЛЕДСТВИЕ: Floyd-Warshall O(V³) всегда; для sparse графа запуск Dijkstra от каждой вершины O(V·E·log V) < O(V³) при малом E
> - [x] Floyd-Warshall: O(V³) время, O(V²) память; все пары кратчайших путей; поддерживает отрицательные веса (без отрицательных циклов) | ✓ ПРИМЕНЯТЬ: плотный граф, все пары нужны, V ≤ 500 📋 ПРАВИЛО: Floyd = 3 вложенных цикла = all-pairs shortest paths 🔗 См. Q10
> - [ ] Floyd-Warshall не работает с отрицательными весами рёбер | ❌ ПОСЛЕДСТВИЕ: Floyd-Warshall поддерживает отрицательные веса; не работает только при наличии отрицательного ЦИКЛА (бесконечно уменьшающийся путь)

## Q14. A* поиск — что это?

**A* (A-star)** — расширение Dijkstra с **эвристикой** для ускорения.

```
f(n) = g(n) + h(n)
g(n) — фактическое расстояние от start до n
h(n) — эвристическая оценка от n до end (admissible — не переоценивает)
```

Если `h(n) ≡ 0` — это Dijkstra. Если `h` точная — A* идёт прямо к цели.

**Применения:** игровая навигация (NPC patfinding), GPS, robotics.

```java
// Эвристика для grid: Manhattan / Euclidean distance
int manhattan(int[] a, int[] b) {
    return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
}
```


> [!mcq]
> - [ ] A* с эвристикой h(n)=0 быстрее Dijkstra — не вычисляет лишнего | ❌ ПОСЛЕДСТВИЕ: A* с h=0 идентичен Dijkstra; нет прироста производительности; цель A* — направить поиск к цели через ненулевую эвристику
> - [ ] A* с admissible эвристикой может не найти оптимальный путь | ❌ ПОСЛЕДСТВИЕ: admissible эвристика (никогда не переоценивает) гарантирует оптимальность A*; только inadmissible может дать suboptimal результат
> - [x] A* = Dijkstra + эвристика h(n); f(n) = g(n) + h(n); admissible h гарантирует оптимальность; быстрее Dijkstra для точечного поиска | ✓ ПРИМЕНЯТЬ: grid pathfinding (Manhattan/Euclidean h), game NPC, GPS с географической эвристикой 📋 ПРАВИЛО: A* = Dijkstra + direction hint = меньше вершин посещается 🔗 См. Q10
> - [ ] A* не работает для 3D-пространства, только для 2D grid | ❌ ПОСЛЕДСТВИЕ: A* работает в любом пространстве с подходящей эвристикой; 3D robotics используют Euclidean distance как h

## Q15. (!) Что такое MST?

**Minimum Spanning Tree (MST)** — подмножество рёбер, образующее **дерево** на всех вершинах с минимальной суммой весов.

Применения:
- Прокладка кабельной сети
- Организация коммуникаций (telecom)
- Кластеризация
- Approximation algorithms (TSP)

Существует два классических алгоритма: **Краскала** и **Прима**.


> [!mcq]
> - [ ] MST находит кратчайший путь между двумя вершинами | ❌ ПОСЛЕДСТВИЕ: MST — дерево, покрывающее все вершины с минимальной суммой весов; кратчайший путь — Dijkstra/BFS; это разные задачи
> - [ ] MST единственный — существует только один MST для любого графа | ❌ ПОСЛЕДСТВИЕ: MST уникален только если все веса рёбер различны; при равных весах может быть несколько различных MST с одинаковой суммой
> - [x] MST = подмножество V-1 рёбер, покрывающее все V вершин с минимальной суммой весов; алгоритмы: Краскала (sort+UF) и Прима (PQ) | ✓ ПРИМЕНЯТЬ: кабельная сеть, telecom, кластеризация, TSP approximation 📋 ПРАВИЛО: MST = дерево на всех вершинах = V-1 рёбер = min total weight 🔗 См. Q16
> - [ ] MST применяется только к невзвешенным графам | ❌ ПОСЛЕДСТВИЕ: MST по определению для взвешенных графов; в невзвешенном все MST — обычные spanning trees (любое BFS/DFS дерево)

## Q16. (!) Алгоритм Краскала?

**Идея:** сортируем рёбра по весу, добавляем по одному, если не образуют цикл (используем **Union-Find**).

```java
int kruskalMST(int n, List<int[]> edges) {
    edges.sort(Comparator.comparingInt(e -> e[2])); // по весу
    UnionFind uf = new UnionFind(n);
    int totalWeight = 0, edgesUsed = 0;

    for (int[] e : edges) {
        int u = e[0], v = e[1], w = e[2];
        if (uf.union(u, v)) {
            totalWeight += w;
            if (++edgesUsed == n - 1) break;
        }
    }
    return totalWeight;
}
```

`O(E log E)` — доминирует сортировка. С Union-Find и path compression — почти `O(E α(V))` для остальной работы.

**Лучше для разреженных графов** (когда `E` маленькое).


> [!mcq]
> - [ ] Краскала лучше Прима во всех случаях — всегда его выбирать | ❌ ПОСЛЕДСТВИЕ: Краскала O(E log E) лучше для sparse (E маленькое); Прима O(E log V) с heap лучше для dense (E большое и нужно добавлять ребра инкрементально)
> - [x] Краскала: sort edges O(E log E) + Union-Find для cycle check; O(E log E); лучше для sparse графов | ✓ ПРИМЕНЯТЬ: sparse граф (cable routing, telecom), когда рёбра уже sorted 📋 ПРАВИЛО: Kruskal = sort + UF = жадный по рёбрам 🔗 См. Q15
> - [ ] Краскала требует сохранить весь граф в матрице смежности | ❌ ПОСЛЕДСТВИЕ: Краскала работает с edge list; матрица не нужна; edge list достаточно для сортировки и Union-Find
> - [ ] Краскала не работает для несвязных графов | ❌ ПОСЛЕДСТВИЕ: Краскала строит MST для каждой connected компоненты; результат — minimum spanning forest для несвязных графов

## Q17. (!) Алгоритм Прима?

**Идея:** растим MST из одной вершины, добавляя минимальное ребро, выходящее из текущего дерева.

```java
int primMST(List<List<int[]>> adj) {
    int n = adj.size();
    boolean[] inMST = new boolean[n];
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{0, 0}); // {vertex, weight}
    int totalWeight = 0, edgesUsed = 0;

    while (!pq.isEmpty() && edgesUsed < n) {
        int[] curr = pq.poll();
        int u = curr[0], w = curr[1];
        if (inMST[u]) continue;
        inMST[u] = true;
        totalWeight += w;
        edgesUsed++;
        for (int[] edge : adj.get(u)) {
            if (!inMST[edge[0]]) pq.offer(new int[]{edge[0], edge[1]});
        }
    }
    return totalWeight;
}
```

`O(E log V)` с binary heap. **Лучше для плотных графов**.


> [!mcq]
> - [ ] Прима не работает для несвязных графов — нужно использовать только Краскала | ❌ ПОСЛЕДСТВИЕ: Прима требует связного графа, но можно запускать от каждой компоненты; Краскала естественно обрабатывает несвязные (возвращает forest)
> - [ ] Прима и Краскала дают всегда разные MST деревья (но одинаковый вес) | ❌ ПОСЛЕДСТВИЕ: при уникальных весах MST единственный; оба алгоритма вернут одно и то же дерево
> - [ ] Прима медленнее Краскала на sparse графах | ❌ ПОСЛЕДСТВИЕ: оба O(E log V) ≈ O(E log E) для sparse; на dense Прима с матрицей O(V²) быстрее Краскала O(E log E) = O(V² log V)
> - [x] Прима: PQ + растить MST из вершины; O(E log V); лучше для dense; Краскала: sort rёбер + UF; O(E log E); лучше для sparse | ✓ ПРИМЕНЯТЬ: dense → Прима; sparse → Краскала 📋 ПРАВИЛО: Прима = растём вершины; Краскала = жадные рёбра 🔗 См. Q16

## Q18. Чем Краскал отличается от Прима?

| Критерий | Kruskal | Prim |
|----------|---------|------|
| Структура | Сортировка + Union-Find | Priority queue |
| Сложность | `O(E log E)` | `O(E log V)` |
| Подход | Глобальный (по всем рёбрам) | Локальный (растёт из вершины) |
| Лучше для | Разреженных | Плотных |
| Параллелизация | Сложно | Легко |
| Связан ли граф | Не требуется | Требуется (или возвращает MST компоненты) |

Оба дают одинаковый MST по весу (если веса уникальны — и идентичные деревья).


> [!mcq]
> - [ ] Прима не работает для несвязных графов; Краскала работает для обоих | ❌ ПОСЛЕДСТВИЕ: Прима требует связности но при запуске от каждой компоненты тоже строит forest; основное различие — подход и производительность на dense/sparse
> - [x] Краскала: sort edges + UF, O(E log E), лучше sparse; Прима: PQ + grow from vertex, O(E log V), лучше dense; оба дают одинаковый MST по весу | ✓ ПРИМЕНЯТЬ: sparse network → Краскала; dense complete graph → Прима 📋 ПРАВИЛО: Краскала = глобальные рёбра; Прима = локальный рост 🔗 См. Q15
> - [ ] Прима медленнее при наличии отрицательных рёбер | ❌ ПОСЛЕДСТВИЕ: ни Прима ни Краскала не чувствительны к знаку весов; оба выбирают минимальное ребро — отрицательное ещё лучше
> - [ ] Краскала всегда быстрее Прима из-за отсутствия PriorityQueue | ❌ ПОСЛЕДСТВИЕ: для dense E ≈ V² Краскала O(V² log V) vs Прима с матрицей O(V²); Прима быстрее на dense графах

## Q19. (!) Как обнаружить цикл в неориентированном графе?

**DFS:** если встретили посещённую вершину, не являющуюся родителем в DFS-дереве — цикл.

```java
boolean hasCycle(List<List<Integer>> adj) {
    boolean[] visited = new boolean[adj.size()];
    for (int i = 0; i < adj.size(); i++) {
        if (!visited[i] && dfsCycle(adj, i, -1, visited)) return true;
    }
    return false;
}

boolean dfsCycle(List<List<Integer>> adj, int u, int parent, boolean[] visited) {
    visited[u] = true;
    for (int v : adj.get(u)) {
        if (!visited[v]) {
            if (dfsCycle(adj, v, u, visited)) return true;
        } else if (v != parent) {
            return true; // back edge — цикл!
        }
    }
    return false;
}
```

Альтернатива — **Union-Find**: для каждого ребра проверяем, не в одной ли группе уже его концы.

`O(V + E)`.


> [!mcq]
> - [ ] Для неориентированного графа можно использовать visited[] как boolean (true/false) для cycle detection | ❌ ПОСЛЕДСТВИЕ: для undirected нужно передавать parent и проверять visited[v] && v != parent; иначе будет false positive на каждом ребре (back edge к parent)
> - [ ] Union-Find не может обнаружить цикл в неориентированном графе | ❌ ПОСЛЕДСТВИЕ: Union-Find — стандартный метод: union(u,v) вернёт false если u и v уже в одном компоненте = цикл обнаружен
> - [ ] DFS cycle detection в неориентированном и ориентированном графе работает одинаково | ❌ ПОСЛЕДСТВИЕ: undirected нужен parent[], чтобы не считать ребро к parent циклом; directed нужны 3 цвета (WHITE/GRAY/BLACK) для back edge detection
> - [x] DFS с parent[]: если встретили visited вершину ≠ parent → цикл; или Union-Find: union(u,v) = false → цикл; O(V+E) | ✓ ПРИМЕНЯТЬ: dependency graph, network topology validation 📋 ПРАВИЛО: undirected cycle = visited ≠ parent → back edge 🔗 См. Q20

## Q20. (!) Как обнаружить цикл в ориентированном графе?

**DFS с тремя состояниями:**
- WHITE — не посещена
- GRAY — в текущем DFS-стеке
- BLACK — полностью обработана

Если из GRAY попадаем в GRAY — цикл (back edge).

```java
boolean hasCycleDirected(List<List<Integer>> adj) {
    int n = adj.size();
    int[] color = new int[n]; // 0=WHITE, 1=GRAY, 2=BLACK
    for (int i = 0; i < n; i++) {
        if (color[i] == 0 && dfsDirected(adj, i, color)) return true;
    }
    return false;
}

boolean dfsDirected(List<List<Integer>> adj, int u, int[] color) {
    color[u] = 1; // GRAY
    for (int v : adj.get(u)) {
        if (color[v] == 1) return true; // back edge
        if (color[v] == 0 && dfsDirected(adj, v, color)) return true;
    }
    color[u] = 2; // BLACK
    return false;
}
```

`O(V + E)`. Альтернатива — **алгоритм Кана**: если после топологической сортировки осталось < V вершин в результате — цикл.


> [!mcq]
> - [ ] Union-Find не работает для cycle detection в directed graph | ❌ ПОСЛЕДСТВИЕ: Union-Find не различает направление рёбер; для directed cycle detection нужен DFS с WHITE/GRAY/BLACK; для undirected — Union-Find подходит
> - [x] DFS с 3 цветами: WHITE→GRAY при входе, GRAY→BLACK при выходе; back edge = GRAY→GRAY = цикл; O(V+E) | ✓ ПРИМЕНЯТЬ: CI/CD dependency cycle detection, module circular deps 📋 ПРАВИЛО: directed cycle = DFS GRAY→GRAY = back edge 🔗 См. Q21
> - [ ] Для directed cycle достаточно visited[] как в undirected DFS | ❌ ПОСЛЕДСТВИЕ: visited[] без parent не различает back/forward/cross edges; cross edge ведёт к уже посещённой вершине без цикла — будет false positive
> - [ ] Алгоритм Кана (topological sort) не может обнаружить циклы | ❌ ПОСЛЕДСТВИЕ: алгоритм Кана: если после topo sort обработано < V вершин — остались вершины с ненулевым in-degree = цикл

## Q21. Найти компоненты связности?

В неориентированном графе. Запускаем DFS/BFS от каждой непосещённой вершины — это и есть отдельный компонент.

```java
int countComponents(int n, List<List<Integer>> adj) {
    boolean[] visited = new boolean[n];
    int count = 0;
    for (int i = 0; i < n; i++) {
        if (!visited[i]) {
            dfs(adj, i, visited);
            count++;
        }
    }
    return count;
}
```

Альтернатива — **Union-Find**: для каждого ребра делаем union, потом считаем число уникальных корней.

`O(V + E)`.


> [!mcq]
> - [ ] Компоненты связности можно найти только алгоритмом Union-Find — DFS не подходит | ❌ ПОСЛЕДСТВИЕ: DFS/BFS одинаково эффективны O(V+E); запуск от каждой непосещённой вершины = одна компонента; Union-Find — альтернатива, не единственный метод
> - [ ] Компоненты связности существуют только в неориентированных графах | ❌ ПОСЛЕДСТВИЕ: в directed существуют strongly connected components (SCC) — Tarjan/Kosaraju; weakly connected — если ignore направления
> - [x] DFS/BFS от каждой непосещённой вершины = одна компонента; O(V+E); альтернатива — Union-Find с union по всем рёбрам | ✓ ПРИМЕНЯТЬ: network partitioning, image segmentation, social communities 📋 ПРАВИЛО: connected components = DFS от каждой непосещённой 🔗 См. Q22
> - [ ] Компоненты связности требуют O(V²) памяти для adjacency matrix | ❌ ПОСЛЕДСТВИЕ: алгоритм работает с adjacency list O(V+E) памяти; adjacency matrix не нужна

## Q22. (!) Найти strongly connected components (SCC)?

В **ориентированном** графе. SCC — максимальные подграфы, где между любыми двумя вершинами есть путь в обе стороны.

**Алгоритм Косараджу (Kosaraju):**
1. DFS на исходном графе, записываем порядок завершения (postorder)
2. Транспонируем граф
3. DFS на транспонированном в обратном порядке завершения — каждое DFS-дерево = SCC

```java
List<List<Integer>> kosaraju(List<List<Integer>> adj) {
    int n = adj.size();
    boolean[] visited = new boolean[n];
    Deque<Integer> stack = new ArrayDeque<>();

    // Шаг 1: DFS, заполняем стек по postorder
    for (int i = 0; i < n; i++) {
        if (!visited[i]) fillOrder(adj, i, visited, stack);
    }

    // Шаг 2: транспонировать
    List<List<Integer>> trans = transpose(adj, n);

    // Шаг 3: DFS на транспонированном
    Arrays.fill(visited, false);
    List<List<Integer>> sccs = new ArrayList<>();
    while (!stack.isEmpty()) {
        int u = stack.pop();
        if (!visited[u]) {
            List<Integer> scc = new ArrayList<>();
            dfsCollect(trans, u, visited, scc);
            sccs.add(scc);
        }
    }
    return sccs;
}
```

**Алгоритм Тарьяна** делает то же за один DFS, используя low-link значения. Сложнее в реализации, но эффективнее на практике.

`O(V + E)` оба.


> [!mcq]
> - [ ] SCC можно найти через обычный BFS без реверса графа | ❌ ПОСЛЕДСТВИЕ: Kosaraju требует реверса рёбер; простой BFS/DFS не находит SCC — он находит только weakly connected components
> - [ ] Алгоритм Тарьяна требует двух DFS-проходов как Косарайю | ❌ ПОСЛЕДСТВИЕ: Тарьян — один DFS проход с low-link values; Косарайю — два DFS прохода; Тарьян сложнее в реализации но требует меньше памяти
> - [x] Kosaraju: 2 DFS (прямой граф + реверсированный); Tarjan: 1 DFS с low-link; оба O(V+E); SCC = множество вершин с взаимной достижимостью | ✓ ПРИМЕНЯТЬ: circular deps в модулях, deadlock detection, compiler optimization 📋 ПРАВИЛО: SCC = все вершины достигают друг друга = Kosaraju/Tarjan 🔗 См. Q20
> - [ ] SCC существуют только в undirected графах | ❌ ПОСЛЕДСТВИЕ: SCC только для directed; в undirected — обычные connected components; SCC учитывает направление рёбер

## Q23. Bipartite graph — проверка двудольности?

**BFS с двумя цветами:** красим start — 0, соседей — 1, их соседей — 0, и т.д. Если встретим ребро между одинаково окрашенными — НЕ двудольный.

```java
boolean isBipartite(List<List<Integer>> adj) {
    int n = adj.size();
    int[] color = new int[n];
    Arrays.fill(color, -1);

    for (int i = 0; i < n; i++) {
        if (color[i] == -1) {
            Queue<Integer> queue = new ArrayDeque<>();
            queue.offer(i);
            color[i] = 0;
            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (int v : adj.get(u)) {
                    if (color[v] == -1) {
                        color[v] = 1 - color[u];
                        queue.offer(v);
                    } else if (color[v] == color[u]) {
                        return false;
                    }
                }
            }
        }
    }
    return true;
}
```

`O(V + E)`. Применения: matching, scheduling.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. (!) Топологическая сортировка — что и зачем? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Топологическая сортировка** — линейный порядок вершин **DAG** (Directed Acyclic Graph), при котором для каждого ребра `u → v` вершина `u` стоит **перед** `v`.

**Применения:**
- Сборка зависимостей (Maven/Gradle)
- Порядок задач с предусловиями
- Spring bean dependency graph
- Парсинг конфигов с зависимостями
- Course scheduling

Существует, **только если граф DAG** (без циклов).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. (!) Алгоритм Кана (BFS-подход)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
List<Integer> kahnTopologicalSort(int n, List<List<Integer>> adj) {
    int[] inDegree = new int[n];
    for (int u = 0; u < n; u++)
        for (int v : adj.get(u)) inDegree[v]++;

    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < n; i++)
        if (inDegree[i] == 0) queue.offer(i);

    List<Integer> order = new ArrayList<>();
    while (!queue.isEmpty()) {
        int u = queue.poll();
        order.add(u);
        for (int v : adj.get(u)) {
            if (--inDegree[v] == 0) queue.offer(v);
        }
    }

    if (order.size() != n) throw new IllegalStateException("Cycle!");
    return order;
}
```

`O(V + E)`. Алгоритм Кана **обнаруживает цикл**: если `order.size() < n` — есть цикл. Естественно поддерживает обработку рёбер по слоям.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. Топологическая сортировка через DFS? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
List<Integer> topologicalSortDFS(int n, List<List<Integer>> adj) {
    boolean[] visited = new boolean[n];
    Deque<Integer> stack = new ArrayDeque<>();

    for (int i = 0; i < n; i++) {
        if (!visited[i]) dfs(i, adj, visited, stack);
    }

    List<Integer> order = new ArrayList<>();
    while (!stack.isEmpty()) order.add(stack.pop());
    return order;
}

void dfs(int u, List<List<Integer>> adj, boolean[] visited, Deque<Integer> stack) {
    visited[u] = true;
    for (int v : adj.get(u))
        if (!visited[v]) dfs(v, adj, visited, stack);
    stack.push(u); // post-order
}
```

`O(V + E)`. Не обнаруживает циклы напрямую — нужно дополнить трёхцветной схемой.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. (!) Number of Islands — задача с матрицей? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Дана grid из `1` (земля) и `0` (вода). Найти количество островов (4-связные регионы из `1`).

```java
int numIslands(char[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    int count = 0;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == '1') {
                count++;
                dfsIsland(grid, r, c);
            }
        }
    }
    return count;
}

void dfsIsland(char[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length
        || grid[r][c] != '1') return;
    grid[r][c] = '#'; // mark visited
    dfsIsland(grid, r + 1, c);
    dfsIsland(grid, r - 1, c);
    dfsIsland(grid, r, c + 1);
    dfsIsland(grid, r, c - 1);
}
```

`O(R · C)` время. Альтернатива — Union-Find (полезно если нужно динамически добавлять землю).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. (!) Course Schedule — обнаружение цикла в зависимостях? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Дано `numCourses` и список prerequisites `[[a, b], ...]` (b → a). Можно ли пройти все курсы?

```java
boolean canFinish(int numCourses, int[][] prerequisites) {
    List<List<Integer>> adj = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
    int[] inDegree = new int[numCourses];

    for (int[] p : prerequisites) {
        adj.get(p[1]).add(p[0]);
        inDegree[p[0]]++;
    }

    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++)
        if (inDegree[i] == 0) queue.offer(i);

    int taken = 0;
    while (!queue.isEmpty()) {
        int u = queue.poll();
        taken++;
        for (int v : adj.get(u)) {
            if (--inDegree[v] == 0) queue.offer(v);
        }
    }
    return taken == numCourses;
}
```

Если можно завершить — топологический порядок существует (нет циклов). `O(V + E)`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q29. Word Ladder — кратчайшая трансформация слов? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Из `beginWord` в `endWord`, изменяя по 1 букве, через слова из словаря. Минимальная длина пути.

```java
int ladderLength(String begin, String end, List<String> wordList) {
    Set<String> dict = new HashSet<>(wordList);
    if (!dict.contains(end)) return 0;

    Queue<String> queue = new ArrayDeque<>();
    queue.offer(begin);
    int level = 1;

    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            String word = queue.poll();
            if (word.equals(end)) return level;
            for (int j = 0; j < word.length(); j++) {
                char[] arr = word.toCharArray();
                for (char c = 'a'; c <= 'z'; c++) {
                    arr[j] = c;
                    String next = new String(arr);
                    if (dict.contains(next)) {
                        queue.offer(next);
                        dict.remove(next); // mark visited
                    }
                }
            }
        }
        level++;
    }
    return 0;
}
```

`O(N · L · 26)`, где N — слов, L — длина слова. Bidirectional BFS даёт ещё ускорение.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q30. Network Delay Time — Dijkstra-задача? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Дано `times = [[u, v, w], ...]`, n узлов. Найти время, за которое сигнал достигнет всех узлов из `k`.

```java
int networkDelayTime(int[][] times, int n, int k) {
    List<List<int[]>> adj = new ArrayList<>();
    for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
    for (int[] t : times) adj.get(t[0]).add(new int[]{t[1], t[2]});

    int[] dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[k] = 0;

    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
    pq.offer(new int[]{0, k});

    while (!pq.isEmpty()) {
        int[] curr = pq.poll();
        int d = curr[0], u = curr[1];
        if (d > dist[u]) continue;
        for (int[] e : adj.get(u)) {
            int v = e[0], w = e[1];
            if (dist[u] + w < dist[v]) {
                dist[v] = dist[u] + w;
                pq.offer(new int[]{dist[v], v});
            }
        }
    }

    int max = 0;
    for (int i = 1; i <= n; i++) {
        if (dist[i] == Integer.MAX_VALUE) return -1;
        max = Math.max(max, dist[i]);
    }
    return max;
}
```

Классический Dijkstra. Ответ — максимальная дистанция (последний достигнутый узел).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q31. (!) Clone Graph — глубокое копирование? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Глубокое копирование графа с задаными вершинами и рёбрами.

```java
class Node { int val; List<Node> neighbors; }

Node cloneGraph(Node node) {
    if (node == null) return null;
    Map<Node, Node> visited = new HashMap<>();
    return dfs(node, visited);
}

Node dfs(Node node, Map<Node, Node> visited) {
    if (visited.containsKey(node)) return visited.get(node);
    Node copy = new Node();
    copy.val = node.val;
    copy.neighbors = new ArrayList<>();
    visited.put(node, copy);
    for (Node n : node.neighbors) {
        copy.neighbors.add(dfs(n, visited));
    }
    return copy;
}
```

`O(V + E)`. HashMap нужен, чтобы не клонировать одну вершину дважды.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q32. (!) Где графы встречаются в реальной разработке? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **Spring DI** — bean dependency graph (тополог. сорт. для определения порядка инициализации)
2. **Maven/Gradle** — модули и их зависимости
3. **Compilers** — control flow graph (CFG), data flow analysis
4. **Социальные сети** — friends, followers, recommendations
5. **Routing protocols** — OSPF (Dijkstra), BGP
6. **Knowledge graphs** — Google Knowledge Graph, Wikipedia
7. **Code analysis** — call graphs, import graphs (детектор циклов)
8. **Recommender systems** — collaborative filtering
9. **Network topology** — datacenter design, mesh networks
10. **Workflow orchestration** — Airflow DAGs, Argo Workflows


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q33. Что такое graph database? Когда выбирать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Graph database** — БД с нативной моделью узлов и рёбер, оптимизированная для traversal-запросов.

Примеры: **Neo4j**, ArangoDB, Amazon Neptune, JanusGraph.

**Когда выбирать:**
- Запросы с **множественными join'ами** (соц. сети — «друзья друзей друзей»)
- Граф знаний и онтологии
- Fraud detection (поиск подозрительных паттернов)
- Recommendation engines
- Network topology analysis

**Когда НЕ выбирать:**
- Простые CRUD-операции — relational БД проще
- Аналитика по агрегатам — column store (ClickHouse)

Запросы пишут на **Cypher** (Neo4j), Gremlin, SPARQL.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q34. (!) PageRank — как работает? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**PageRank** (Page-Brin, 1998) — алгоритм оценки важности веб-страницы по ссылкам.

**Идея:** «случайный сёрфер» с вероятностью `d` (=0.85) переходит по ссылке, с вероятностью `1-d` — на случайную страницу.

```
PR(p) = (1-d)/N + d · Σ (PR(q) / L(q))   для q → p
```

Итеративный алгоритм:
1. Инициализировать PR всех страниц = 1/N
2. Обновлять PR по формуле
3. Повторять до сходимости

```java
double[] pageRank(int n, List<List<Integer>> adj, int iterations) {
    double[] pr = new double[n];
    Arrays.fill(pr, 1.0 / n);
    double damp = 0.85;

    for (int iter = 0; iter < iterations; iter++) {
        double[] next = new double[n];
        for (int u = 0; u < n; u++) {
            int outDegree = adj.get(u).size();
            if (outDegree == 0) continue;
            double share = damp * pr[u] / outDegree;
            for (int v : adj.get(u)) next[v] += share;
        }
        for (int i = 0; i < n; i++) next[i] += (1 - damp) / n;
        pr = next;
    }
    return pr;
}
```

Применения: Google search ranking, citation networks, social influence detection.

---

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Деревья](trees-interview.md) — частный случай графа
- [Хеш-таблицы](hash-tables-interview.md) — visited sets, adj as Map
- [Кучи](heaps-interview.md) — Dijkstra, Prim используют PriorityQueue
- [Стеки и очереди](stacks-queues-interview.md) — BFS использует очередь, DFS — стек
- [Рекурсия](../algorithmic-paradigms/recursion-interview.md) — DFS обычно рекурсивный
- [Динамическое программирование](../algorithmic-paradigms/dynamic-programming-interview.md) — Bellman-Ford = DP
- [Divide and Conquer](../algorithmic-paradigms/divide-and-conquer-interview.md) — некоторые graph алгоритмы
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — O(V+E) vs O(E log V)
- [Backtracking](../algorithmic-paradigms/backtracking-interview.md) — DFS с возвратом
- [System Design](../../system-design/system-design-interview.md) — графы в архитектуре
- [Распределённые системы](../../architecture/distributed-systems-interview.md) — graph algorithms in distributed
- [Микросервисы](../../architecture/microservices-interview.md) — service dependency graph
