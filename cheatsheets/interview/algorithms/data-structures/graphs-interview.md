---
title: "Вопросы на собеседовании: Графы"
description: "Представление графов (матрица/список смежности), BFS, DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, Kruskal, Prim, топологическая сортировка, SCC, графы в реальной жизни"
tags:
  - interview
  - algorithms
  - graphs-interview
aliases:
  - "Graphs interview"
  - "Графы собеседование"
  - "Graph algorithms interview"
  - "BFS DFS interview"
  - "Dijkstra interview"
  - "Topological sort interview"
difficulty: "intermediate"
updated: "2026-04-18"
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

## Q15. (!) Что такое MST?

**Minimum Spanning Tree (MST)** — подмножество рёбер, образующее **дерево** на всех вершинах с минимальной суммой весов.

Применения:
- Прокладка кабельной сети
- Организация коммуникаций (telecom)
- Кластеризация
- Approximation algorithms (TSP)

Существует два классических алгоритма: **Краскала** и **Прима**.

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

## Q24. (!) Топологическая сортировка — что и зачем?

**Топологическая сортировка** — линейный порядок вершин **DAG** (Directed Acyclic Graph), при котором для каждого ребра `u → v` вершина `u` стоит **перед** `v`.

**Применения:**
- Сборка зависимостей (Maven/Gradle)
- Порядок задач с предусловиями
- Spring bean dependency graph
- Парсинг конфигов с зависимостями
- Course scheduling

Существует, **только если граф DAG** (без циклов).

## Q25. (!) Алгоритм Кана (BFS-подход)?

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

## Q26. Топологическая сортировка через DFS?

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

## Q27. (!) Number of Islands — задача с матрицей?

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

## Q28. (!) Course Schedule — обнаружение цикла в зависимостях?

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

## Q29. Word Ladder — кратчайшая трансформация слов?

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

## Q30. Network Delay Time — Dijkstra-задача?

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

## Q31. (!) Clone Graph — глубокое копирование?

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

## Q32. (!) Где графы встречаются в реальной разработке?

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

## Q33. Что такое graph database? Когда выбирать?

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

## Q34. (!) PageRank — как работает?

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

- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем
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
