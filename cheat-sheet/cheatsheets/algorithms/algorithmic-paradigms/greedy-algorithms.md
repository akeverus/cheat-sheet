# Жадные алгоритмы (Greedy Algorithms)

Кратко: Комплексное руководство по жадным алгоритмам - от базовых концепций до практических применений с примерами на Java.

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Greedy Algorithm - GeeksforGeeks](https://www.geeksforgeeks.org/greedy-algorithms/) - Подробное руководство по жадным алгоритмам

### Baeldung
- [Greedy Algorithms in Java](https://www.baeldung.com/java-greedy-algorithms) - Практические примеры на Java

### См. также
- `../problem-solving/README.md` - Решение задач
- `../algorithms/README.md` - Обзор алгоритмов
- `dynamic-programming.md` - Динамическое программирование

## Содержание

- [Введение в жадные алгоритмы](#введение-в-жадные-алгоритмы)
- [Основные принципы](#основные-принципы)
- [Когда использовать жадные алгоритмы](#когда-использовать-жадные-алгоритмы)
- [Классические задачи](#классические-задачи)
  - [Задача о размене монет](#задача-о-размене-монет)
  - [Задача о рюкзаке (жадный вариант)](#задача-о-рюкзаке-жадный-вариант)
  - [Алгоритм Дейкстры](#алгоритм-дейкстры)
  - [Минимальное остовное дерево](#минимальное-остовное-дерево)
- [Оптимизация жадных алгоритмов](#оптимизация-жадных-алгоритмов)
- [Best Practices](#best-practices)

## Введение в жадные алгоритмы

Жадные алгоритмы - это класс алгоритмов, которые принимают локально оптимальные решения на каждом шаге в надежде найти глобально оптимальное решение.

### Характеристики жадных алгоритмов

```java
/**
 * Основные характеристики жадных алгоритмов
 */
public class GreedyAlgorithmCharacteristics {

    /**
     * Признак 1: Жадный выбор
     * На каждом шаге выбирается локально оптимальное решение
     */
    public void demonstrateGreedyChoice() {
        System.out.println("Пример: Задача о размене монет");
        System.out.println("На каждом шаге выбираем самую большую монету,");
        System.out.println("которая не превышает оставшуюся сумму");
    }

    /**
     * Признак 2: Оптимальная подструктура
     * Оптимальное решение задачи содержит оптимальные решения подзадач
     */
    public void demonstrateOptimalSubstructure() {
        System.out.println("Пример: Минимальное остовное дерево");
        System.out.println("Если мы выбрали оптимальное ребро на шаге k,");
        System.out.println("то решение для оставшихся шагов также оптимально");
    }

    /**
     * Когда жадный алгоритм работает
     */
    public void whenGreedyWorks() {
        System.out.println("Жадный алгоритм работает, если:");
        System.out.println("1. Есть жадное свойство выбора");
        System.out.println("2. Есть оптимальная подструктура");
        System.out.println("3. Нет необходимости пересматривать предыдущие решения");
    }
}
```

## Основные принципы

### Жадное свойство выбора

```java
/**
 * Демонстрация жадного свойства выбора
 */
public class GreedyChoiceProperty {

    /**
     * Пример: Выбор активности с максимальной продолжительностью
     * Жадный выбор: всегда выбираем активность, которая заканчивается раньше
     */
    public static class Activity {
        int start;   // Время начала активности
        int end;     // Время окончания активности
        String name; // Название активности

        public Activity(int start, int end, String name) {
            this.start = start;
            this.end = end;
            this.name = name;
        }

        /**
         * Проверка совместимости двух активностей
         * Активности совместимы, если они не перекрываются по времени
         */
        public boolean isCompatible(Activity other) {
            return this.end <= other.start || other.end <= this.start;
        }
    }

    /**
     * Жадный алгоритм выбора максимального количества совместимых активностей
     * Жадный выбор: выбираем активность с наименьшим временем окончания
     */
    public static List<Activity> selectActivities(List<Activity> activities) {
        // Сортируем активности по времени окончания (жадный выбор)
        activities.sort(Comparator.comparingInt(a -> a.end));

        List<Activity> selected = new ArrayList<>();
        int lastEndTime = 0; // Время окончания последней выбранной активности

        // Проходим по отсортированным активностям
        for (Activity activity : activities) {
            // Если активность совместима с уже выбранными (жадный выбор)
            if (activity.start >= lastEndTime) {
                selected.add(activity);
                lastEndTime = activity.end; // Обновляем время окончания
            }
        }

        return selected;
    }

    /**
     * Демонстрация жадного выбора активностей
     */
    public static void demonstrateActivitySelection() {
        List<Activity> activities = Arrays.asList(
            new Activity(1, 4, "A"),
            new Activity(3, 5, "B"),
            new Activity(0, 6, "C"),
            new Activity(5, 7, "D"),
            new Activity(8, 9, "E"),
            new Activity(5, 9, "F")
        );

        List<Activity> selected = selectActivities(activities);
        System.out.println("Выбранные активности:");
        selected.forEach(a -> System.out.println(a.name + ": " + a.start + "-" + a.end));
    }
}
```

## Когда использовать жадные алгоритмы

### Критерии применимости

```java
/**
 * Критерии для применения жадных алгоритмов
 */
public class GreedyApplicability {

    /**
     * Критерий 1: Жадное свойство выбора
     * Локально оптимальный выбор приводит к глобально оптимальному решению
     */
    public void greedyChoiceProperty() {
        System.out.println("Примеры задач с жадным свойством:");
        System.out.println("- Задача о размене монет (для некоторых систем монет)");
        System.out.println("- Задача о рюкзаке (для дробного варианта)");
        System.out.println("- Минимальное остовное дерево");
        System.out.println("- Кратчайший путь (алгоритм Дейкстры)");
    }

    /**
     * Критерий 2: Оптимальная подструктура
     * Оптимальное решение содержит оптимальные решения подзадач
     */
    public void optimalSubstructure() {
        System.out.println("Если мы выбрали оптимальное ребро для MST,");
        System.out.println("то решение для оставшихся ребер также оптимально");
    }

    /**
     * Когда НЕ использовать жадные алгоритмы
     */
    public void whenNotToUseGreedy() {
        System.out.println("Не используйте жадные алгоритмы если:");
        System.out.println("1. Нет жадного свойства выбора");
        System.out.println("2. Нужно пересматривать предыдущие решения");
        System.out.println("3. Локально оптимальные решения не приводят к глобальному оптимуму");
        System.out.println("Пример: Задача коммивояжера (TSP)");
    }
}
```

## Классические задачи

### Задача о размене монет

```java
/**
 * Задача о размене монет (жадный алгоритм)
 * Найти минимальное количество монет для размена заданной суммы
 */
public class CoinChangeGreedy {

    /**
     * Жадный алгоритм размена монет
     * @param amount сумма для размена
     * @param coins массив доступных монет (должен быть отсортирован по убыванию)
     * @return список монет для размена
     */
    public static List<Integer> greedyCoinChange(int amount, int[] coins) {
        List<Integer> result = new ArrayList<>();
        int remaining = amount; // Оставшаяся сумма для размена

        // Проходим по монетам от больших к меньшим (жадный выбор)
        for (int coin : coins) {
            // Пока текущая монета помещается в оставшуюся сумму
            while (remaining >= coin) {
                result.add(coin);      // Добавляем монету в результат
                remaining -= coin;     // Уменьшаем оставшуюся сумму
            }

            // Если сумма полностью разменяна, выходим
            if (remaining == 0) {
                break;
            }
        }

        // Проверяем, удалось ли разменять всю сумму
        if (remaining > 0) {
            throw new IllegalArgumentException(
                "Невозможно разменять сумму " + amount + " данными монетами"
            );
        }

        return result;
    }

    /**
     * Оптимизированная версия с подсчетом количества монет
     * @param amount сумма для размена
     * @param coins массив доступных монет
     * @return минимальное количество монет
     */
    public static int minCoinsGreedy(int amount, int[] coins) {
        int count = 0; // Счетчик количества монет
        int remaining = amount;

        // Проходим по монетам от больших к меньшим
        for (int coin : coins) {
            if (remaining >= coin) {
                // Вычисляем количество монет данного номинала
                int numCoins = remaining / coin;
                count += numCoins;
                remaining %= coin; // Остаток от деления

                if (remaining == 0) {
                    break;
                }
            }
        }

        if (remaining > 0) {
            throw new IllegalArgumentException("Невозможно разменять сумму");
        }

        return count;
    }

    /**
     * Демонстрация жадного алгоритма размена монет
     */
    public static void demonstrateCoinChange() {
        // Система монет, для которой жадный алгоритм работает оптимально
        int[] coins = {25, 10, 5, 1}; // Американская система монет
        int amount = 67;

        System.out.println("=== Размен монет (жадный алгоритм) ===");
        System.out.println("Сумма: " + amount);
        System.out.println("Монеты: " + Arrays.toString(coins));

        List<Integer> result = greedyCoinChange(amount, coins);
        System.out.println("Результат: " + result);
        System.out.println("Количество монет: " + result.size());

        int minCoins = minCoinsGreedy(amount, coins);
        System.out.println("Минимальное количество монет: " + minCoins);
    }

    /**
     * Пример системы монет, для которой жадный алгоритм НЕ работает
     */
    public static void demonstrateNonGreedyCase() {
        // Система монет, для которой жадный алгоритм не оптимален
        int[] coins = {1, 3, 4}; // Пример неканонической системы
        int amount = 6;

        System.out.println("\n=== Пример неоптимальности жадного алгоритма ===");
        System.out.println("Сумма: " + amount);
        System.out.println("Монеты: " + Arrays.toString(coins));

        // Жадный алгоритм выберет: 4 + 1 + 1 = 3 монеты
        List<Integer> greedyResult = greedyCoinChange(amount, coins);
        System.out.println("Жадный алгоритм: " + greedyResult + " (" + greedyResult.size() + " монет)");

        // Оптимальное решение: 3 + 3 = 2 монеты
        System.out.println("Оптимальное решение: [3, 3] (2 монеты)");
        System.out.println("Жадный алгоритм не оптимален для этой системы монет!");
    }
}
```

### Задача о рюкзаке (жадный вариант)

```java
/**
 * Задача о рюкзаке (дробный вариант - жадный алгоритм)
 * Можно брать части предметов
 */
public class FractionalKnapsackGreedy {

    /**
     * Класс предмета для задачи о рюкзаке
     */
    static class Item {
        int weight;    // Вес предмета
        int value;     // Стоимость предмета
        double ratio;  // Отношение стоимости к весу (для жадного выбора)

        public Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
            this.ratio = (double) value / weight; // Вычисляем отношение
        }
    }

    /**
     * Жадный алгоритм для дробного рюкзака
     * Жадный выбор: выбираем предметы с максимальным отношением стоимость/вес
     * @param items массив предметов
     * @param capacity вместимость рюкзака
     * @return максимальная стоимость
     */
    public static double fractionalKnapsack(Item[] items, int capacity) {
        // Сортируем предметы по убыванию отношения стоимость/вес (жадный выбор)
        Arrays.sort(items, (a, b) -> Double.compare(b.ratio, a.ratio));

        double totalValue = 0.0; // Общая стоимость
        int remainingCapacity = capacity; // Оставшаяся вместимость

        // Проходим по отсортированным предметам
        for (Item item : items) {
            if (remainingCapacity == 0) {
                break; // Рюкзак полон
            }

            if (item.weight <= remainingCapacity) {
                // Берем предмет полностью
                totalValue += item.value;
                remainingCapacity -= item.weight;
            } else {
                // Берем часть предмета (дробный вариант)
                double fraction = (double) remainingCapacity / item.weight;
                totalValue += item.value * fraction;
                remainingCapacity = 0; // Рюкзак полностью заполнен
            }
        }

        return totalValue;
    }

    /**
     * Демонстрация жадного алгоритма для дробного рюкзака
     */
    public static void demonstrateFractionalKnapsack() {
        Item[] items = {
            new Item(10, 60),  // Предмет 1: вес=10, стоимость=60, отношение=6.0
            new Item(20, 100), // Предмет 2: вес=20, стоимость=100, отношение=5.0
            new Item(30, 120)  // Предмет 3: вес=30, стоимость=120, отношение=4.0
        };
        int capacity = 50;

        System.out.println("=== Дробный рюкзак (жадный алгоритм) ===");
        System.out.println("Вместимость рюкзака: " + capacity);
        System.out.println("Предметы:");
        for (Item item : items) {
            System.out.println("  Вес=" + item.weight + ", Стоимость=" + item.value +
                            ", Отношение=" + item.ratio);
        }

        double maxValue = fractionalKnapsack(items, capacity);
        System.out.println("Максимальная стоимость: " + maxValue);
    }
}
```

### Алгоритм Дейкстры

```java
/**
 * Алгоритм Дейкстры для поиска кратчайшего пути
 * Жадный алгоритм: на каждом шаге выбираем вершину с минимальным расстоянием
 */
public class DijkstraAlgorithm {

    /**
     * Реализация алгоритма Дейкстры
     * @param graph граф в виде матрицы смежности
     * @param start начальная вершина
     * @return массив кратчайших расстояний от start до всех вершин
     */
    public static int[] dijkstra(int[][] graph, int start) {
        int n = graph.length; // Количество вершин
        int[] distances = new int[n]; // Кратчайшие расстояния
        boolean[] visited = new boolean[n]; // Посещенные вершины

        // Инициализация: все расстояния бесконечны, кроме начальной вершины
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[start] = 0; // Расстояние до начальной вершины = 0

        // Обрабатываем все вершины
        for (int count = 0; count < n - 1; count++) {
            // Жадный выбор: выбираем вершину с минимальным расстоянием
            int u = minDistance(distances, visited);

            // Помечаем вершину как посещенную
            visited[u] = true;

            // Обновляем расстояния до соседних вершин
            for (int v = 0; v < n; v++) {
                // Если вершина не посещена, есть ребро, и найден более короткий путь
                if (!visited[v] && graph[u][v] != 0 &&
                    distances[u] != Integer.MAX_VALUE &&
                    distances[u] + graph[u][v] < distances[v]) {
                    distances[v] = distances[u] + graph[u][v];
                }
            }
        }

        return distances;
    }

    /**
     * Вспомогательная функция для выбора вершины с минимальным расстоянием
     * Жадный выбор на каждом шаге
     */
    private static int minDistance(int[] distances, boolean[] visited) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        // Ищем непосещенную вершину с минимальным расстоянием
        for (int v = 0; v < distances.length; v++) {
            if (!visited[v] && distances[v] <= min) {
                min = distances[v];
                minIndex = v;
            }
        }

        return minIndex;
    }

    /**
     * Демонстрация алгоритма Дейкстры
     */
    public static void demonstrateDijkstra() {
        // Граф в виде матрицы смежности
        // 0 означает отсутствие ребра
        int[][] graph = {
            {0, 4, 0, 0, 0, 0, 0, 8, 0},  // Вершина 0
            {4, 0, 8, 0, 0, 0, 0, 11, 0}, // Вершина 1
            {0, 8, 0, 7, 0, 4, 0, 0, 2},  // Вершина 2
            {0, 0, 7, 0, 9, 14, 0, 0, 0}, // Вершина 3
            {0, 0, 0, 9, 0, 10, 0, 0, 0}, // Вершина 4
            {0, 0, 4, 14, 10, 0, 2, 0, 0}, // Вершина 5
            {0, 0, 0, 0, 0, 2, 0, 1, 6},  // Вершина 6
            {8, 11, 0, 0, 0, 0, 1, 0, 7}, // Вершина 7
            {0, 0, 2, 0, 0, 0, 6, 7, 0}   // Вершина 8
        };

        int start = 0; // Начальная вершина

        System.out.println("=== Алгоритм Дейкстры ===");
        System.out.println("Начальная вершина: " + start);

        int[] distances = dijkstra(graph, start);

        System.out.println("Кратчайшие расстояния от вершины " + start + ":");
        for (int i = 0; i < distances.length; i++) {
            System.out.println("  До вершины " + i + ": " + distances[i]);
        }
    }
}
```

### Минимальное остовное дерево

```java
/**
 * Алгоритм Крускала для построения минимального остовного дерева
 * Жадный алгоритм: на каждом шаге выбираем ребро с минимальным весом
 */
public class KruskalMST {

    /**
     * Класс для представления ребра графа
     */
    static class Edge implements Comparable<Edge> {
        int src;    // Исходная вершина
        int dest;   // Конечная вершина
        int weight; // Вес ребра

        public Edge(int src, int dest, int weight) {
            this.src = src;
            this.dest = dest;
            this.weight = weight;
        }

        /**
         * Сравнение ребер по весу для сортировки (жадный выбор)
         */
        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }

    /**
     * Структура данных Union-Find для проверки циклов
     */
    static class UnionFind {
        int[] parent; // Родительская вершина для каждой вершины
        int[] rank;   // Ранг для оптимизации

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            // Инициализация: каждая вершина - своя компонента
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        /**
         * Нахождение корня компоненты с path compression
         */
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        /**
         * Объединение двух компонент
         */
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                // Union by rank для оптимизации
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }

    /**
     * Алгоритм Крускала для построения MST
     * Жадный выбор: выбираем ребра в порядке возрастания веса
     */
    public static List<Edge> kruskalMST(List<Edge> edges, int vertices) {
        // Сортируем ребра по весу (жадный выбор)
        Collections.sort(edges);

        List<Edge> mst = new ArrayList<>(); // Минимальное остовное дерево
        UnionFind uf = new UnionFind(vertices); // Для проверки циклов

        // Проходим по отсортированным ребрам
        for (Edge edge : edges) {
            int rootSrc = uf.find(edge.src);
            int rootDest = uf.find(edge.dest);

            // Если ребро не создает цикл, добавляем его в MST
            if (rootSrc != rootDest) {
                mst.add(edge);
                uf.union(rootSrc, rootDest);
            }

            // Если построили дерево (n-1 ребро), выходим
            if (mst.size() == vertices - 1) {
                break;
            }
        }

        return mst;
    }

    /**
     * Демонстрация алгоритма Крускала
     */
    public static void demonstrateKruskal() {
        int vertices = 4;
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 10)); // Ребро между вершинами 0 и 1 с весом 10
        edges.add(new Edge(0, 2, 6));  // Ребро между вершинами 0 и 2 с весом 6
        edges.add(new Edge(0, 3, 5)); // Ребро между вершинами 0 и 3 с весом 5
        edges.add(new Edge(1, 3, 15)); // Ребро между вершинами 1 и 3 с весом 15
        edges.add(new Edge(2, 3, 4));  // Ребро между вершинами 2 и 3 с весом 4

        System.out.println("=== Алгоритм Крускала (MST) ===");
        System.out.println("Количество вершин: " + vertices);
        System.out.println("Ребра графа:");
        edges.forEach(e -> System.out.println(
            "  " + e.src + " -> " + e.dest + " (вес: " + e.weight + ")"
        ));

        List<Edge> mst = kruskalMST(edges, vertices);

        System.out.println("\nРебра минимального остовного дерева:");
        int totalWeight = 0;
        for (Edge edge : mst) {
            System.out.println("  " + edge.src + " -> " + edge.dest + " (вес: " + edge.weight + ")");
            totalWeight += edge.weight;
        }
        System.out.println("Общий вес MST: " + totalWeight);
    }
}
```

## Оптимизация жадных алгоритмов

### Эффективные структуры данных

```java
/**
 * Оптимизация жадных алгоритмов с использованием приоритетных очередей
 */
public class OptimizedGreedyAlgorithms {

    /**
     * Оптимизированный алгоритм Дейкстры с PriorityQueue
     * Использует приоритетную очередь для эффективного выбора вершины
     */
    public static class DijkstraOptimized {
        static class Node implements Comparable<Node> {
            int vertex;    // Номер вершины
            int distance;  // Расстояние до вершины

            public Node(int vertex, int distance) {
                this.vertex = vertex;
                this.distance = distance;
            }

            @Override
            public int compareTo(Node other) {
                return Integer.compare(this.distance, other.distance);
            }
        }

        /**
         * Оптимизированная версия алгоритма Дейкстры
         * Использует PriorityQueue вместо линейного поиска
         */
        public static int[] dijkstraOptimized(List<List<Node>> graph, int start) {
            int n = graph.size();
            int[] distances = new int[n];
            boolean[] visited = new boolean[n];
            Arrays.fill(distances, Integer.MAX_VALUE);
            distances[start] = 0;

            // Приоритетная очередь для эффективного выбора вершины
            PriorityQueue<Node> pq = new PriorityQueue<>();
            pq.offer(new Node(start, 0));

            while (!pq.isEmpty()) {
                // Извлекаем вершину с минимальным расстоянием (жадный выбор)
                Node current = pq.poll();
                int u = current.vertex;

                if (visited[u]) {
                    continue; // Уже обработали эту вершину
                }

                visited[u] = true;

                // Обновляем расстояния до соседних вершин
                for (Node neighbor : graph.get(u)) {
                    int v = neighbor.vertex;
                    int weight = neighbor.distance;

                    if (!visited[v] && distances[u] + weight < distances[v]) {
                        distances[v] = distances[u] + weight;
                        pq.offer(new Node(v, distances[v]));
                    }
                }
            }

            return distances;
        }
    }

    /**
     * Демонстрация оптимизированного алгоритма Дейкстры
     */
    public static void demonstrateOptimizedDijkstra() {
        int n = 5;
        List<List<Node>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        // Добавляем ребра графа
        graph.get(0).add(new Node(1, 4));
        graph.get(0).add(new Node(2, 1));
        graph.get(1).add(new Node(3, 1));
        graph.get(2).add(new Node(1, 2));
        graph.get(2).add(new Node(3, 5));
        graph.get(3).add(new Node(4, 3));

        int[] distances = DijkstraOptimized.dijkstraOptimized(graph, 0);

        System.out.println("=== Оптимизированный алгоритм Дейкстры ===");
        System.out.println("Кратчайшие расстояния:");
        for (int i = 0; i < distances.length; i++) {
            System.out.println("  До вершины " + i + ": " + distances[i]);
        }
    }
}
```

## Best Practices

### Рекомендации по использованию жадных алгоритмов

```java
/**
 * Best practices для жадных алгоритмов
 */
public class GreedyBestPractices {

    /**
     * 1. Проверка жадного свойства
     * Всегда проверяйте, что жадный выбор приводит к оптимальному решению
     */
    public void verifyGreedyProperty() {
        System.out.println("Советы по проверке жадного свойства:");
        System.out.println("1. Докажите, что локально оптимальный выбор глобально оптимален");
        System.out.println("2. Проверьте на контрпримерах");
        System.out.println("3. Убедитесь в наличии оптимальной подструктуры");
    }

    /**
     * 2. Выбор критерия жадности
     * Определите правильный критерий для жадного выбора
     */
    public void chooseGreedyCriterion() {
        System.out.println("Примеры критериев жадного выбора:");
        System.out.println("- Максимальное отношение стоимость/вес (рюкзак)");
        System.out.println("- Минимальное время окончания (активности)");
        System.out.println("- Минимальный вес ребра (MST)");
        System.out.println("- Минимальное расстояние (Дейкстра)");
    }

    /**
     * 3. Оптимизация производительности
     */
    public void performanceOptimization() {
        System.out.println("Техники оптимизации:");
        System.out.println("1. Используйте эффективные структуры данных (PriorityQueue, Union-Find)");
        System.out.println("2. Избегайте повторных вычислений");
        System.out.println("3. Используйте мемоизацию при необходимости");
        System.out.println("4. Оптимизируйте сортировку (если нужна)");
    }

    /**
     * 4. Обработка граничных случаев
     */
    public void handleEdgeCases() {
        System.out.println("Граничные случаи для жадных алгоритмов:");
        System.out.println("1. Пустой вход");
        System.out.println("2. Невозможность найти решение");
        System.out.println("3. Несколько оптимальных решений");
        System.out.println("4. Очень большие входные данные");
    }

    /**
     * 5. Тестирование жадных алгоритмов
     */
    public void testingGreedyAlgorithms() {
        System.out.println("Стратегии тестирования:");
        System.out.println("1. Тестируйте на известных примерах");
        System.out.println("2. Проверяйте граничные случаи");
        System.out.println("3. Сравнивайте с оптимальными решениями (если известны)");
        System.out.println("4. Тестируйте на больших входных данных");
    }
}
```

## Заключение

Жадные алгоритмы - мощный инструмент для решения оптимизационных задач, когда локально оптимальные решения приводят к глобально оптимальному результату.

### Ключевые выводы

1. **Жадное свойство выбора** - основа для применения жадных алгоритмов
2. **Оптимальная подструктура** - необходимое условие для корректности
3. **Эффективные структуры данных** - ключ к производительности
4. **Проверка корректности** - критически важно для жадных алгоритмов
5. **Практика** - ключ к пониманию, когда использовать жадные алгоритмы

### Рекомендации для практики

- Решайте классические задачи жадных алгоритмов
- Анализируйте, почему жадный алгоритм работает (или не работает)
- Сравнивайте жадные решения с решениями других подходов
- Практикуйте оптимизацию жадных алгоритмов
- Изучайте доказательства корректности жадных алгоритмов

Помните: не все задачи можно решить жадными алгоритмами, но когда они работают, они часто дают простые и эффективные решения!

