---
title: "Алгоритмы и структуры данных"
description: "Точка входа в раздел алгоритмов: парадигмы, структуры данных, сортировка, поиск, графы, деревья, строки, математика, подборки задач и AI/ML."
tags:
  - meta
  - index
  - algorithms
type: "index"
updated: "2026-04-17"
---
# Алгоритмы и структуры данных

Раздел покрывает всю фундаментальную алгоритмику: классические структуры данных (массивы, списки, деревья, графы, хэш-таблицы, heap, disjoint set), алгоритмические парадигмы (DP, greedy, divide-and-conquer, backtracking), сортировку и поиск, работу со строками и графами, математику и подборки задач для практики и интервью.

Для кого: готовящиеся к интервью, инженеры, работающие с нагруженными системами и перф-оптимизацией, а также те, кто углубляет понимание сложности и design decisions.

## Полезные ссылки

### Подразделы

| Подраздел | О чём |
|-----------|------|
| [algorithmic-paradigms/](algorithmic-paradigms/README.md) | Divide & Conquer, DP, Greedy, Backtracking, Branch & Bound |
| [algorithms/](algorithms/README.md) | Обзор классических алгоритмов |
| [data-structures/](data-structures/README.md) | Массивы, списки, стеки, очереди, хэш-таблицы, heap, Union-Find, Bloom, LRU |
| [sorting/](sorting/README.md) | Quick/Merge/Heap/Radix/Bucket, стабильность, in-place |
| [searching/](searching/README.md) | Линейный, бинарный, троичный, поиск в деревьях и графах |
| [graphs/](graphs/README.md) | BFS/DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, MST, топо-сорт, SCC |
| [trees/](trees/README.md) | BST, AVL, Red-Black, B/B+-деревья, Trie, Segment Tree, Fenwick |
| [strings/](strings/README.md) | KMP, Rabin-Karp, Boyer-Moore, Z-функция, суффиксные массивы |
| [math/](math/README.md) | Арифметика, теория чисел, комбинаторика, теория вероятностей |
| [problem-solving/](problem-solving/README.md) | Подходы к решению задач: паттерны, декомпозиция, шаблоны |
| [problems/](problems/README.md) | Коллекции задач по категориям |
| [ai-ml/](ai-ml/README.md) | Базовые алгоритмы ML, метрики, оптимизация |
| [ai-collections/](ai-collections/README.md) | Подборки задач и промптов для AI-ассистированной подготовки |

### Соседние разделы
- [Computer Science](../basics/computer-science/README.md)
- [Basics / programming](../basics/programming-basics/README.md)
- [Design Patterns](../patterns/README.md)
- [Java](../languages/java/README.md)
- [Interview](../interview/)

### Внешние ресурсы
- [Introduction to Algorithms (CLRS)](https://mitpress.mit.edu/books/introduction-algorithms-third-edition) — канонический учебник
- [Algorithms (Sedgewick)](https://algs4.cs.princeton.edu/home/)
- [LeetCode](https://leetcode.com/) — практика
- [CP-Algorithms](https://cp-algorithms.com/) — конкурсное программирование
- [Big-O Cheat Sheet](https://www.bigocheatsheet.com/)
- [Visualgo](https://visualgo.net/) — визуализация алгоритмов

## Содержание

- [Базовые сложности](#базовые-сложности)
- [Карта «задача → алгоритм/структура»](#карта-задача--алгоритмструктура)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Базовые сложности

| Операция | Массив | Связный список | Hash Map | BST | Сбалансированное BST (AVL/RB) | Heap |
|----------|--------|----------------|----------|-----|-------------------------------|------|
| Доступ по индексу | O(1) | O(n) | n/a | O(log n)* | O(log n) | O(1) для min/max |
| Поиск | O(n) | O(n) | O(1) avg | O(log n)* | O(log n) | O(n) |
| Вставка | O(n) | O(1) | O(1) avg | O(log n)* | O(log n) | O(log n) |
| Удаление | O(n) | O(1) | O(1) avg | O(log n)* | O(log n) | O(log n) |

*для несбалансированного BST — O(n) worst case.

| Алгоритм | Лучшее | Среднее | Худшее | Память |
|----------|--------|---------|--------|--------|
| Quicksort | O(n log n) | O(n log n) | O(n²) | O(log n) |
| Mergesort | O(n log n) | O(n log n) | O(n log n) | O(n) |
| Heapsort | O(n log n) | O(n log n) | O(n log n) | O(1) |
| Timsort | O(n) | O(n log n) | O(n log n) | O(n) |
| Radix | O(nk) | O(nk) | O(nk) | O(n+k) |

## Карта «задача → алгоритм/структура»

| Задача | Инструмент |
|--------|-----------|
| Кратчайший путь в графе с неотрицательными весами | Dijkstra (min-heap) |
| Кратчайший путь с отрицательными весами | Bellman-Ford |
| Все пары кратчайших путей | Floyd-Warshall |
| Минимальное остовное дерево | Kruskal (Union-Find) / Prim |
| Поиск подстроки | KMP, Rabin-Karp, Boyer-Moore |
| Автодополнение, префиксы | Trie |
| Range sum/min query с обновлениями | Segment Tree, Fenwick |
| Уникальность + частота | HashMap / HashSet |
| Top-K | min-heap размером K |
| Медиана в потоке | две кучи (max-heap + min-heap) |
| LRU-кэш | HashMap + Doubly linked list |
| Компоненты связности | DFS/BFS, Union-Find |
| Детекция циклов в графе | DFS с цветами / топологический сорт |
| Подсчёт уникальных элементов (примерно) | HyperLogLog |
| Проверка «элемент точно не в множестве» | Bloom Filter |
| Interval scheduling / activity selection | Greedy |
| Knapsack, LCS, edit distance | DP |
| Permutations, combinations, N-Queens | Backtracking |
| Chess-like / game theory | Minimax + alpha-beta |
| Стабильная сортировка с гарантией O(n log n) | Mergesort / Timsort |

## Маршруты чтения

- **Интервью junior (1 неделя):** `data-structures` → `sorting` → `searching` → `problems/easy`.
- **Интервью mid/senior (2-3 недели):** весь раздел + `algorithmic-paradigms` (DP, Graph), `problems/medium+hard`.
- **Углубление систем:** `data-structures` (Bloom, HLL, consistent hashing) → `architecture/system-design/`.
- **Конкурсное программирование:** `math` + `strings` (суффиксные структуры) + `graphs` (Tarjan, SCC, flow).

## Куда идти дальше

- Паттерны проектирования (как применять алгоритмы в коде) — [patterns/](../patterns/README.md)
- Системный дизайн и масштабирование — [architecture/system-design/](../architecture/system-design/README.md)
- Java Collections и их устройство — [languages/java/](../languages/java/README.md)
- Подготовка к Q&A — [interview/](../interview/)
