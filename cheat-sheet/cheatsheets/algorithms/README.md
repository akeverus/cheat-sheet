# Алгоритмы и структуры данных

Полное руководство по алгоритмам и структурам данных: сортировка, поиск, деревья, графы, строки, ИИ и математические алгоритмы.

**Дата последнего обновления:** 2025-01-15

## Содержание

- [Структура](#�-�-�-�-к�-�-�-а)
  - [🔄 [Sorting Algorithms](sorting/README.md) - Алгоритмы сортировки](#sorting-algorithms-sorting-readme-md-�-лго�-и�-м�-�-о�-�-и�-овки)
  - [🔍 [Searching Algorithms](searching/README.md) - Алгоритмы поиска](#searching-algorithms-searching-readme-md-�-лго�-и�-м�-пои�-ка)
  - [🌳 [Tree Algorithms](trees/README.md) - Алгоритмы на деревьях](#�-tree-algorithms-trees-readme-md-�-лго�-и�-м�-на-де�-ев�-я�)
  - [🕸️ [Graph Algorithms](graphs/README.md) - Алгоритмы на графах](#�-��-graph-algorithms-graphs-readme-md-�-лго�-и�-м�-на-г�-а�-а�)
  - [🤖 [AI & Collections](ai-collections/README.md) - ИИ и коллекции](#ai-collections-ai-collections-readme-md-�-�-и-коллек�-ии)
  - [📝 [String Algorithms](strings/README.md) - Алгоритмы обработки строк](#string-algorithms-strings-readme-md-�-лго�-и�-м�-об�-або�-ки-�-�-�-ок)
  - [🔢 [Math Algorithms](math/README.md) - Математические алгоритмы](#math-algorithms-math-readme-md-�-а�-ема�-и�-е�-кие-алго�-и�-м�)
  - [🧩 [Algorithmic Problems](problems/README.md) - Задачи и решения](#algorithmic-problems-problems-readme-md-�-ада�-и-и-�-е�-ения)
- [Классификация алгоритмов](#�-ла�-�-и�-ика�-ия-алго�-и�-мов)
  - [По сложности времени (Big O)](#�-о-�-ложно�-�-и-в�-емени-big-o)
  - [По типу](#�-о-�-ип�)
- [Примечания](#�-�-име�-ания)
- [Полезные ссылки](#�-олезн�-е-�-�-�-лки)
  - [Теория алгоритмов](#�-ео�-ия-алго�-и�-мов)
  - [Практика](#�-�-ак�-ика)
  - [Визуализация](#�-из�-ализа�-ия)



## Структура

### 🔄 [Sorting Algorithms](sorting/README.md) - Алгоритмы сортировки
- Comparison-based sorts: Bubble, Selection, Insertion, Merge, Quick, Heap, Shell
- Non-comparison sorts: Counting, Bucket, Radix, Bead
- Special sorts: In-place sorting

### 🔍 [Searching Algorithms](searching/README.md) - Алгоритмы поиска
- Binary Search, Interpolation Search
- Array operations: merging, finding pairs, max subarray
- Frequency analysis, K-largest elements

### 🌳 [Tree Algorithms](trees/README.md) - Алгоритмы на деревьях
- AVL Tree, Binary Tree, traversals
- Monte Carlo Tree Search
- Minimum Spanning Tree: Prim, Kruskal, Boruvka

### 🕸️ [Graph Algorithms](graphs/README.md) - Алгоритмы на графах
- Shortest paths: Dijkstra
- Graph libraries: JGraphT
- Search algorithms: BFS

### 🤖 [AI & Collections](ai-collections/README.md) - ИИ и коллекции
- Machine Learning: Logistic Regression, Genetic Algorithms
- Neural Networks: CNN with Deeplearning4j
- Big Data: Spark MLlib, HyperLogLog
- Collections: Big O notation, non-blocking structures

### 📝 [String Algorithms](strings/README.md) - Алгоритмы обработки строк
- Pattern matching, suffix trees
- String metrics: Levenshtein distance
- Text processing: regex, permutations, palindrome checks

### 🔢 [Math Algorithms](math/README.md) - Математические алгоритмы
- Computational mathematics
- Number theory algorithms
- Mathematical computations for programmers

### 🧩 [Algorithmic Problems](problems/README.md) - Задачи и решения
- Classic coding problems
- Interview questions
- Competitive programming challenges

## Классификация алгоритмов

### По сложности времени (Big O)

| Алгоритм | Лучший | Средний | Худший | Память |
|----------|--------|---------|--------|--------|
| Bubble Sort | O(n) | O(n²) | O(n²) | O(1) |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) |
| Binary Search | O(1) | O(log n) | O(log n) | O(1) |
| Dijkstra | O(V²) | O((V+E) log V) | O((V+E) log V) | O(V) |

### По типу

- Sorting: Bubble, Quick, Merge, Heap, Radix
- Searching: Binary, Interpolation, Hash-based
- Graph: BFS, DFS, Dijkstra, MST algorithms
- String: KMP, Suffix trees, Levenshtein
- Dynamic: Knapsack, Longest subsequence
- Greedy: Activity selection, Huffman coding
- Divide & Conquer: Quick sort, Merge sort

## Примечания

- Языки: Алгоритмы реализованы на Java, Kotlin, Go, Scala
- Сложность: Все алгоритмы содержат анализ временной и пространственной сложности
- Примеры: Каждый алгоритм содержит код и разбор примеров
- Тестирование: Unit тесты для валидации корректности

## Полезные ссылки

### Теория алгоритмов
- [MIT Introduction to Algorithms](https://ocw.mit.edu/courses/6-006-introduction-to-algorithms-fall-2011/)
- [Coursera: Algorithms by Princeton](https://www.coursera.org/learn/algorithms-part1)
- [GeeksforGeeks Algorithms](https://www.geeksforgeeks.org/fundamentals-of-algorithms/)

### Практика
- [LeetCode](https://leetcode.com/) - Практика алгоритмов
- [HackerRank](https://www.hackerrank.com/) - Соревнования
- [CodeChef](https://www.codechef.com/) - Контесты

### Визуализация
- [Visualgo](https://visualgo.net/en) - Визуализация алгоритмов
- [Algorithm Visualizer](https://algorithm-visualizer.org/) - Интерактивные демо
