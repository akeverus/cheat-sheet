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
| [[README|algorithmic-paradigms/]] | Divide & Conquer, DP, Greedy, Backtracking, Branch & Bound |
| [[README|algorithms/]] | Обзор классических алгоритмов |
| [[README|data-structures/]] | Массивы, списки, стеки, очереди, хэш-таблицы, heap, Union-Find, Bloom, LRU |
| [[README|sorting/]] | Quick/Merge/Heap/Radix/Bucket, стабильность, in-place |
| [[README|searching/]] | Линейный, бинарный, троичный, поиск в деревьях и графах |
| [[README|graphs/]] | BFS/DFS, Dijkstra, Bellman-Ford, Floyd-Warshall, MST, топо-сорт, SCC |
| [[README|trees/]] | BST, AVL, Red-Black, B/B+-деревья, Trie, Segment Tree, Fenwick |
| [[README|strings/]] | KMP, Rabin-Karp, Boyer-Moore, Z-функция, суффиксные массивы |
| [[README|math/]] | Арифметика, теория чисел, комбинаторика, теория вероятностей |
| [[README|problem-solving/]] | Подходы к решению задач: паттерны, декомпозиция, шаблоны |
| [[README|problems/]] | Коллекции задач по категориям |
| [[README|ai-ml/]] | Базовые алгоритмы ML, метрики, оптимизация |
| [[README|ai-collections/]] | Подборки задач и промптов для AI-ассистированной подготовки |

### Соседние разделы
- [[README|Computer Science]]
- [[README|Basics / programming]]
- [[README|Design Patterns]]
- [[README|Java]]
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

- Паттерны проектирования (как применять алгоритмы в коде) — [[README|patterns/]]
- Системный дизайн и масштабирование — [[README|architecture/system-design/]]
- Java Collections и их устройство — [[README|languages/java/]]
- Подготовка к Q&A — [interview/](../interview/)
