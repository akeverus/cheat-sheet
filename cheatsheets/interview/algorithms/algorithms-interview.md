---
title: "Вопросы на собеседовании: Алгоритмы (обзор)"
description: "Карта раздела `algorithms/`: обзор тем, шаблон ответа на алгоритмические вопросы, выбор Java Collections, ссылки на детальные шпаргалки по структурам данных, парадигмам, сортировкам и поиску"
tags:
  - interview
  - algorithms
  - algorithms-interview
  - overview
aliases:
  - "Algorithms interview overview"
  - "Алгоритмы обзор"
  - "Алгоритмы карта раздела"
  - "Algorithms map"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Алгоритмы (обзор)`

Этот файл — **обзорная карта** раздела `algorithms/`. Содержит общие принципы, шаблоны ответов на алгоритмические вопросы и **навигацию** к специализированным шпаргалкам по конкретным темам.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Java Collections Framework (Oracle)](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/package-summary.html)
- [Introduction to Algorithms (Cormen, CLRS)](https://mitpress.mit.edu/9780262046305/introduction-to-algorithms/)
- [Time Complexity of Java Collections — Baeldung](https://www.baeldung.com/java-collections-complexity)
- [LeetCode](https://leetcode.com/) — практика
- [Visualgo](https://visualgo.net/) — визуализация
- [Big O Cheat Sheet](https://www.bigocheatsheet.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые принципы**
- [Q1. (!) Как отвечать на алгоритмические вопросы (шаблон)?](#q1--как-отвечать-на-алгоритмические-вопросы-шаблон)
- [Q2. (!) С чего начать, услышав задачу?](#q2--с-чего-начать-услышав-задачу)
- [Q3. (!) Какие парадигмы решения существуют?](#q3--какие-парадигмы-решения-существуют)
- [Q4. (!) Какие структуры данных используют чаще всего?](#q4--какие-структуры-данных-используют-чаще-всего)

**Сложности на одном экране**
- [Q5. (!) Сложности базовых операций структур данных?](#q5--сложности-базовых-операций-структур-данных)
- [Q6. (!) Какова шпаргалка выбора Java Collection под задачу?](#q6--какова-шпаргалка-выбора-java-collection-под-задачу)
- [Q7. Какие классы сложности встречаются чаще всего?](#q7-какие-классы-сложности-встречаются-чаще-всего)

**Карта раздела**
- [Q8. (!) Структура раздела `algorithms/`?](#q8--структура-раздела-algorithms)
- [Q9. С чего начать изучать тему алгоритмов?](#q9-с-чего-начать-изучать-тему-алгоритмов)

**Навигация по шпаргалкам**
- [Q10. (!) Какие темы есть в подразделе `complexity/`?](#q10--какие-темы-есть-в-подразделе-complexity)
- [Q11. (!) Какие структуры данных в `data-structures/`?](#q11--какие-структуры-данных-в-data-structures)
- [Q12. (!) Какие алгоритмы в `sorting-searching/`?](#q12--какие-алгоритмы-в-sorting-searching)
- [Q13. (!) Какие парадигмы в `algorithmic-paradigms/`?](#q13--какие-парадигмы-в-algorithmic-paradigms)

**Метатемы**
- [Q14. (!) Чем отличаются Greedy, DP, D&C, Backtracking?](#q14--чем-отличаются-greedy-dp-dc-backtracking)
- [Q15. Как выбрать алгоритм для конкретной задачи?](#q15-как-выбрать-алгоритм-для-конкретной-задачи)
- [Q16. (!) Как готовиться к алгоритмическому интервью?](#q16--как-готовиться-к-алгоритмическому-интервью)

## Q1. (!) Как отвечать на алгоритмические вопросы (шаблон)?

На интервью почти всегда ждут один и тот же каркас:

1. **Уточни задачу** — границы входа, типы данных, edge cases (пустой вход, дубликаты, переполнение)
2. **Объясни почему этот подход** — какая структура входных данных и ограничения
3. **Предложи brute force** — даже плохой работающий алгоритм лучше молчания
4. **Оптимизируй** — переходи к лучшему через known patterns (Two Pointers, Sliding Window, DP, ...)
5. **Сложность** — время и память в худшем/среднем случае
6. **Реализуй** — пиши чистый код с осмысленными именами
7. **Тестируй** — пройди по edge cases (пустой вход, один элемент, очень большой n)

## Q2. (!) С чего начать, услышав задачу?

**Чек-лист первых 30 секунд:**

| Вопрос | Зачем |
|--------|-------|
| Каков размер входа? | Понять допустимую сложность |
| Может ли вход быть пустым? | Edge case |
| Дубликаты возможны? | Меняет логику |
| Можно ли модифицировать вход? | In-place vs out-of-place |
| Отсортирован ли? | Открывает Binary Search, Two Pointers |
| Тип данных? | int, long, float, объекты с custom equals |
| Один или много ответов? | Returning all permutations vs first |
| Worst case или average? | Меняет выбор алгоритма (Quick vs Heap Sort) |

## Q3. (!) Какие парадигмы решения существуют?

| Парадигма | Когда применять | См. |
|-----------|-----------------|------|
| **Brute force** | Старт для понимания | — |
| **Two Pointers / Sliding Window** | Подмассивы, пары | [[two-pointers-sliding-window-interview|Two Pointers]] |
| **Binary Search** | Отсортированное или монотонное | [[searching-algorithms-interview|Searching]] |
| **DFS / BFS** | Графы, деревья | [[graphs-interview|Графы]], [[trees-interview|Деревья]] |
| **Recursion / Backtracking** | Перебор вариантов | [[recursion-interview|Рекурсия]], [[backtracking-interview|Backtracking]] |
| **Divide and Conquer** | Разбиение на подзадачи | [[divide-and-conquer-interview|D&C]] |
| **Dynamic Programming** | Перекрывающиеся подзадачи + оптимум | [[dynamic-programming-interview|DP]] |
| **Greedy** | Локально оптимальный выбор | [[greedy-algorithms-interview|Greedy]] |
| **Hashing** | Быстрый lookup | [[hash-tables-interview|Хеш-таблицы]] |
| **Heap / PriorityQueue** | Top-K, scheduling | [[heaps-interview|Кучи]] |

## Q4. (!) Какие структуры данных используют чаще всего?

```mermaid
graph TD
    DS[Структуры данных] --> Linear[Линейные]
    DS --> NonLinear[Нелинейные]

    Linear --> Arr[Array / ArrayList]
    Linear --> LL[LinkedList]
    Linear --> St[Stack]
    Linear --> Qu[Queue / Deque]

    NonLinear --> Tree[Tree]
    NonLinear --> Gr[Graph]
    NonLinear --> HT[HashTable]

    Tree --> BST[BST]
    Tree --> Heap[Heap]
    Tree --> Trie[Trie]
```

В Java из стандартной библиотеки: `ArrayList`, `LinkedList`, `ArrayDeque`, `HashMap`, `TreeMap`, `PriorityQueue`. Подробнее — в [[java-collections-interview|Java Collections]].

## Q5. (!) Сложности базовых операций структур данных?

| Структура | Доступ | Поиск | Вставка | Удаление | Память |
|-----------|--------|-------|---------|----------|--------|
| `Array` | `O(1)` | `O(n)` | `O(n)` | `O(n)` | `O(n)` |
| `ArrayList` | `O(1)` | `O(n)` | `O(1)` амортиз. в конец | `O(n)` | `O(n)` |
| `LinkedList` | `O(n)` | `O(n)` | `O(1)` (с ссылкой) | `O(1)` (с ссылкой) | `O(n)` |
| `ArrayDeque` | — | — | `O(1)` амортиз. | `O(1)` | `O(n)` |
| `HashMap` | — | `O(1)` | `O(1)` амортиз. | `O(1)` | `O(n)` |
| `TreeMap` | — | `O(log n)` | `O(log n)` | `O(log n)` | `O(n)` |
| `PriorityQueue` | — | `O(n)` | `O(log n)` | `O(log n)` | `O(n)` |
| `Trie` | — | `O(L)` | `O(L)` | `O(L)` | `O(N · L)` |
| `Union-Find` (DSU) | — | `O(α(n))` | — | — | `O(n)` |

Подробнее — в [[complexity-analysis-interview|Анализ сложности]].

## Q6. (!) Какова шпаргалка выбора Java Collection под задачу?

```mermaid
graph TD
    Q["Что нужно?"] --> S["Стек / LIFO"]
    Q --> QU["Очередь / FIFO"]
    Q --> PQ["Приоритет / мин-макс"]
    Q --> M["Ключ → значение"]
    Q --> SET["Уникальные элементы"]
    Q --> LIST["Список с индексами"]
    Q --> SORTED["Упорядоченные данные"]

    S --> AD1["ArrayDeque (push/pop)"]
    QU --> AD2["ArrayDeque (offer/poll)"]
    PQ --> PQ2["PriorityQueue (min-heap)"]
    M --> HM["HashMap O(1)"]
    SET --> HS["HashSet O(1)"]
    LIST --> AL["ArrayList"]
    SORTED --> TM["TreeMap / TreeSet"]
```

| Задача | Коллекция | Причина |
|--------|-----------|---------|
| BFS | `ArrayDeque` как Queue | `O(1)` offer/poll |
| DFS итеративный | `ArrayDeque` как Stack | `O(1)` push/pop |
| Dijkstra, Top-K | `PriorityQueue` | min-heap |
| Подсчёт частот | `HashMap<T, Integer>` | `O(1)` |
| Уникальные элементы | `HashSet` | `O(1)` |
| Sliding window max | `ArrayDeque` как Monotonic Deque | `O(1)` оба конца |
| LRU Cache | `LinkedHashMap(cap, .75f, true)` | accessOrder |
| Range queries (floor, ceiling) | `TreeMap` | `O(log n)` |

**Частые ошибки:**
- `Stack` — устаревший класс, синхронизирован, используй `ArrayDeque`
- `LinkedList` как Queue — обычно медленнее `ArrayDeque`
- `HashMap` не гарантирует порядок — для стабильного итерации используй `LinkedHashMap`

## Q7. Какие классы сложности встречаются чаще всего?

| Сложность | Название | Пример | n=1000 операций |
|-----------|----------|--------|------------------|
| `O(1)` | Константная | `HashMap.get()`, `arr[i]` | 1 |
| `O(log n)` | Логарифмическая | `Binary Search`, `TreeMap.get()` | 10 |
| `O(n)` | Линейная | Один проход | 1000 |
| `O(n log n)` | Линейно-логарифмическая | `Merge Sort`, `Quick Sort` | 10 000 |
| `O(n²)` | Квадратичная | Вложенные циклы | 1 000 000 |
| `O(2ⁿ)` | Экспоненциальная | Naive Fibonacci, subsets | 10³⁰¹ |
| `O(n!)` | Факториальная | Permutations, TSP | непригодно |

**Правило:** алгоритм должен укладываться в `~10⁸` операций за 1 секунду на современном CPU.

Подробнее — в [[complexity-analysis-interview|Анализ сложности]].

## Q8. (!) Структура раздела `algorithms/`?

```
algorithms/
├── algorithms-interview.md          ← этот файл (обзор)
├── complexity/
│   └── complexity-analysis-interview.md
├── data-structures/
│   ├── arrays-strings-interview.md
│   ├── linked-lists-interview.md
│   ├── stacks-queues-interview.md
│   ├── trees-interview.md
│   ├── heaps-interview.md
│   ├── hash-tables-interview.md
│   ├── graphs-interview.md
│   └── tries-interview.md
├── sorting-searching/
│   ├── sorting-algorithms-interview.md
│   └── searching-algorithms-interview.md
└── algorithmic-paradigms/
    ├── recursion-interview.md
    ├── divide-and-conquer-interview.md
    ├── dynamic-programming-interview.md
    ├── greedy-algorithms-interview.md
    ├── backtracking-interview.md
    └── two-pointers-sliding-window-interview.md
```

Каждый файл — глубокий разбор темы (30-50 вопросов с кодом и mermaid диаграммами).

## Q9. С чего начать изучать тему алгоритмов?

**Рекомендуемый порядок:**

1. **[[complexity-analysis-interview|Анализ сложности]]** — без понимания Big O всё остальное бесполезно
2. **[[arrays-strings-interview|Массивы и строки]]** — основа большинства задач
3. **[[hash-tables-interview|Хеш-таблицы]]** — самый используемый трюк
4. **[[two-pointers-sliding-window-interview|Two Pointers и Sliding Window]]** — снижение `O(n²) → O(n)`
5. **[[searching-algorithms-interview|Поиск]]** + **[[sorting-algorithms-interview|Сортировка]]** — Binary Search
6. **[[recursion-interview|Рекурсия]]** — фундамент DFS, DP, Backtracking
7. **[[trees-interview|Деревья]]** + **[[graphs-interview|Графы]]** — обходы
8. **[[stacks-queues-interview|Стеки и очереди]]** + **[[heaps-interview|Кучи]]** — поддерживают обходы
9. **[[divide-and-conquer-interview|Divide and Conquer]]** + **[[dynamic-programming-interview|DP]]** — оптимизация
10. **[[greedy-algorithms-interview|Greedy]]** + **[[backtracking-interview|Backtracking]]** — выбор стратегии
11. **[[tries-interview|Trie и Union-Find]]** — специальные структуры

## Q10. (!) Какие темы есть в подразделе `complexity/`?

- **[[complexity-analysis-interview|Анализ сложности алгоритмов]]** — Big O, Omega, Theta, амортизированная и пространственная сложность, master theorem, дерево рекурсии, сложности Java Collections, HashMap.get O(log n) с Java 8+, complexity attacks (HashDoS).

## Q11. (!) Какие структуры данных в `data-structures/`?

| Файл | Темы |
|------|------|
| **[[arrays-strings-interview|Массивы и строки]]** | Реверс, сдвиг, Kadane, Two Sum, Three Sum, prefix sum, матрица rotate, Dutch National Flag, Move Zeroes, Next Permutation, KMP, Rabin-Karp, palindrome, anagram, immutable String |
| **[[linked-lists-interview|Связные списки]]** | Singly/doubly, dummy node, реверс (итер./рек.), Floyd cycle detection, middle node, K-group, merge K sorted, LRU Cache, Skip List, ConcurrentLinkedQueue |
| **[[stacks-queues-interview|Стеки и очереди]]** | Stack/Queue/Deque, циклический массив, очередь через 2 стека, MinStack, MaxQueue, monotonic stack/queue, parentheses, RPN, BlockingQueue, ConcurrentLinkedQueue |
| **[[trees-interview|Деревья]]** | Binary tree, BST, AVL, Red-Black, B-Tree/B+Tree, обходы (preorder/inorder/postorder/level-order), Morris traversal, LCA, diameter, validate BST, serialize, segment tree, Fenwick |
| **[[heaps-interview|Кучи]]** | Min/max-heap, siftUp/siftDown, buildHeap O(n), PriorityQueue, top-K, median in stream, merge K lists, heap sort, Fibonacci heap |
| **[[hash-tables-interview|Хеш-таблицы]]** | HashMap внутри, hashCode/equals, load factor, rehashing, treeify, chaining vs open addressing, ConcurrentHashMap, LinkedHashMap, WeakHashMap, consistent hashing, Bloom filter, HashDoS |
| **[[graphs-interview|Графы]]** | Adjacency matrix/list, BFS, DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, Kruskal, Prim, topological sort, SCC, bipartite, Number of Islands, PageRank |
| **[[tries-interview|Префиксные деревья и Union-Find]]** | Trie, Compressed trie (Radix tree), Suffix tree/array, Aho-Corasick, autocomplete, Word Search II, Union-Find (DSU), path compression, union by rank, Number of Islands через UF |

## Q12. (!) Какие алгоритмы в `sorting-searching/`?

| Файл | Темы |
|------|------|
| **[[sorting-algorithms-interview|Алгоритмы сортировки]]** | Bubble, Insertion, Selection, Merge, Quick, Heap, Tim, Counting, Radix, Bucket, Dual-Pivot Quicksort, Introsort, stability, in-place, Java Arrays.sort внутри, parallel sort, external sort |
| **[[searching-algorithms-interview|Алгоритмы поиска]]** | Linear, Binary, Exponential, Interpolation, Jump, Ternary, поиск в отсортированном/повёрнутом массиве, Median of Two Sorted Arrays, Binary Search на ответе (Koko, Capacity), BFS/DFS как поиск, подводные камни overflow |

## Q13. (!) Какие парадигмы в `algorithmic-paradigms/`?

| Файл | Темы |
|------|------|
| **[[recursion-interview|Рекурсия]]** | Base case + recursive step, call stack, tail recursion, отсутствие TCO в JVM, factorial, Fibonacci, Hanoi, fast power, обходы деревьев |
| **[[divide-and-conquer-interview|Divide and Conquer]]** | Three-step pattern, master theorem, recursion tree, Merge Sort, Quick Sort, Binary Search, Strassen, Karatsuba, Closest Pair, Inversion count |
| **[[dynamic-programming-interview|DP]]** | Optimal substructure, overlapping subproblems, memoization vs tabulation, 0/1 Knapsack, LCS, LIS, Edit Distance, Coin Change, Stock series, bitmask DP, state compression |
| **[[greedy-algorithms-interview|Greedy]]** | Greedy choice property, exchange argument, Activity Selection, Fractional Knapsack, Huffman, Dijkstra, Kruskal, Job Sequencing, intervals, Jump Game, Gas Station |
| **[[backtracking-interview|Backtracking]]** | DFS с откатом, шаблон, subsets, permutations, combinations, N-Queens, Sudoku, Word Search, Generate Parentheses, Palindrome Partitioning, pruning, Branch and Bound |
| **[[two-pointers-sliding-window-interview|Two Pointers и Sliding Window]]** | Встречные/fast-slow/на двух массивах, Container with Most Water, Trapping Rain Water, Floyd cycle, Longest Substring Without Repeating, Minimum Window Substring, monotonic deque |

## Q14. (!) Чем отличаются Greedy, DP, D&C, Backtracking?

| Парадигма | Подзадачи перекрываются | Memoization | Возвращает | Сложность |
|-----------|-------------------------|-------------|------------|-----------|
| **Greedy** | Нет | Нет | Локально оптимальный | `O(n log n)` обычно |
| **DP** | Да | Да | Глобальный оптимум | Полиномиальная |
| **D&C** | Нет | Нет | Объединение подрешений | По master theorem |
| **Backtracking** | Иногда | Опционально | Все/один валидный путь | Экспоненциальная |

**Когда какую выбирать:**
- Перебираем все варианты с возвратом → **Backtracking**
- Оптимум + перекрытия → **DP**
- Оптимум без перекрытий + локальный выбор работает → **Greedy**
- Делим на меньшие задачи без перекрытий → **D&C**

## Q15. Как выбрать алгоритм для конкретной задачи?

**По типу задачи:**

| Задача | Алгоритм |
|--------|----------|
| Поиск в отсортированном | Binary Search |
| Подмассив с условием | Sliding Window |
| Пара/тройка с условием | Two Pointers (sorted) или HashMap |
| Подсчёт пар, инверсий | Merge Sort modified |
| Top K | Heap |
| Кратчайший путь невзвешенный | BFS |
| Кратчайший путь взвешенный | Dijkstra (если ≥ 0), иначе Bellman-Ford |
| Все пары | Floyd-Warshall |
| MST | Kruskal или Prim |
| Топологический порядок | Kahn (BFS) или DFS |
| Все перестановки/подмножества | Backtracking |
| Оптимум + перекрытия | DP |
| Поиск с префиксом | Trie |

## Q16. (!) Как готовиться к алгоритмическому интервью?

**Стратегия:**

1. **Изучи [[complexity-analysis-interview|Анализ сложности]]** — без него Big O становится магией
2. **Сделай ~150-200 задач LeetCode** — Top Interview Questions
3. **Сосредоточься на паттернах**, а не на конкретных задачах:
   - Two Pointers, Sliding Window, Binary Search
   - DFS, BFS, Dijkstra
   - DP по 1D, 2D, на строках, на интервалах
   - Backtracking subsets/permutations/combinations
4. **Решай вслух** (mock interviews) — главное на интервью
5. **Учи [[java-collections-interview|Java Collections]]** — какую структуру когда брать
6. **Пиши без IDE** — на собеседовании часто whiteboard или простой редактор
7. **Тестируй edge cases** — пустой вход, 1 элемент, дубликаты, переполнение

Подробнее — в [[interview-preparation|Подготовка к собеседованию]].

---

## See also

**Подразделы (детальные шпаргалки):**

- [[complexity-analysis-interview|Анализ сложности]] — Big O, master theorem, амортизация
- [[arrays-strings-interview|Массивы и строки]] — Two Sum, Kadane, KMP
- [[linked-lists-interview|Связные списки]] — Floyd, LRU, K-group
- [[stacks-queues-interview|Стеки и очереди]] — monotonic, BlockingQueue
- [[trees-interview|Деревья]] — BST, AVL, RB, B-Tree, обходы
- [[heaps-interview|Кучи]] — PriorityQueue, top-K, median stream
- [[hash-tables-interview|Хеш-таблицы]] — HashMap внутри, ConcurrentHashMap
- [[graphs-interview|Графы]] — BFS, DFS, Dijkstra, MST, topological
- [[tries-interview|Trie и Union-Find]] — autocomplete, Number of Islands
- [[sorting-algorithms-interview|Алгоритмы сортировки]] — TimSort, Dual-Pivot
- [[searching-algorithms-interview|Алгоритмы поиска]] — Binary Search, Search Rotated
- [[recursion-interview|Рекурсия]] — call stack, tail recursion, TCO
- [[divide-and-conquer-interview|Divide and Conquer]] — master theorem, Strassen
- [[dynamic-programming-interview|DP]] — Knapsack, LCS, LIS, Edit Distance
- [[greedy-algorithms-interview|Greedy]] — Activity Selection, Huffman, Dijkstra
- [[backtracking-interview|Backtracking]] — N-Queens, Sudoku, Word Search
- [[two-pointers-sliding-window-interview|Two Pointers / Sliding Window]] — все паттерны окон

**Связанные разделы:**

- [[java-collections-interview|Java Collections]] — структуры данных в Java
- [[java-stream-interview|Java Stream API]] — функциональные операции
- [[java-concurrency-interview|Java Concurrency]] — concurrent collections
- [[jvm-interview|JVM]] — стек, GC, JIT
- [[design-patterns-interview|Паттерны проектирования]] — связь с алгоритмами
- [[system-design-interview|System Design]] — алгоритмы в проектировании
- [[interview-preparation|Подготовка к собеседованию]] — стратегия
- [[postgresql-interview|PostgreSQL]] — B-Tree индексы
- [[redis-interview|Redis]] — hash table, skip list
- [[application-security-interview|Application Security]] — HashDoS, ReDoS

- [[ai-agents-interview|AI Agents]]
- [[embeddings-interview|Embeddings]]
- [[llm-basics-interview|LLM Basics]]
- [[llm-integration-patterns-interview|LLM Integration Patterns]]
- [[mlops-interview|MLOps]]
- [[model-serving-interview|Model Serving]]
