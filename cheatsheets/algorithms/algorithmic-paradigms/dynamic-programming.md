---
title: "Динамическое программирование"
description: "Кратко: Комплексное руководство по динамическому программированию - от базовых концепций до продвинутых техник оптимизации с практическими примерами на Java."
tags:
  - algorithms
  - algorithmic-paradigms
  - dynamic-programming
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Динамическое программирование

Кратко: Комплексное руководство по динамическому программированию - от базовых концепций до продвинутых техник оптимизации с практическими примерами на **Java**.

## Полезные ссылки

### Официальная документация
- [Dynamic Programming — GeeksforGeeks](https://www.geeksforgeeks.org/dynamic-programming/) — руководство по **DP**

### Baeldung
- [Oracle Java Documentation](https://docs.oracle.com/en/java/) — **Java** API и руководства

### См. также
- [Решение задач](../problem-solving/README.md) — решение задач
- [Обзор алгоритмов](../algorithms/README.md) — алгоритмы
- [Структуры данных](../data-structures/README.md) — структуры данных

## Содержание

- [Введение в динамическое программирование](#введение-в-динамическое-программирование)
  - [Когда использовать DP?](#когда-использовать-dp)
- [Основные принципы DP](#основные-принципы-dp)
  - [Оптимальная подструктура](#оптимальная-подструктура)
  - [Перекрывающиеся подзадачи](#перекрывающиеся-подзадачи)
- [Методы реализации DP](#методы-реализации-dp)
  - [Мемоизация (Top-Down)](#мемоизация-top-down)
  - [Табуляция (Bottom-Up)](#табуляция-bottom-up)
- [Классические задачи DP](#классические-задачи-dp)
  - [Числа Фибоначчи](#числа-фибоначчи)
  - [Задача о рюкзаке](#задача-о-рюкзаке)
  - [Наибольшая общая подпоследовательность](#наибольшая-общая-подпоследовательность)
  - [Редакционное расстояние](#редакционное-расстояние)
- [Оптимизация пространства](#оптимизация-пространства)
- [Лучшие практики](#лучшие-практики)
  - [Рекомендации по использованию DP](#рекомендации-по-использованию-dp)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Ключевые выводы](#ключевые-выводы)
  - [Рекомендации для практики](#рекомендации-для-практики)


## Введение в динамическое программирование

Динамическое программирование (**DP**) - это метод решения сложных задач путем разбиения их на более простые подзадачи и запоминания результатов для избежания повторных вычислений.

### Когда использовать `DP`?

```java
// Критерии DP: оптимальная подструктура и перекрывающиеся подзадачи; мемоизация или табуляция.
/*
 * Критерии для применения динамического программирования
 */
public class DPCriteria {

    /*
     * Признак 1: Оптимальная подструктура
     * Оптимальное решение задачи содержит оптимальные решения подзадач
     */
    public void demonstrateOptimalSubstructure() {
        System.out.println("Пример: Задача о рюкзаке");
        System.out.println("Оптимальное решение для рюкзака вместимостью W");
        System.out.println("содержит оптимальные решения для рюкзаков меньшей вместимости");
    }

    /*
     * Признак 2: Перекрывающиеся подзадачи
     * Одна и та же подзадача решается многократно
     */
    public void demonstrateOverlappingSubproblems() {
        System.out.println("Пример: Числа Фибоначчи");
        System.out.println("F(5) = F(4) + F(3)");
        System.out.println("F(4) = F(3) + F(2)");
        System.out.println("F(3) вычисляется дважды - это перекрывающаяся подзадача!");
    }

    /*
     * Когда НЕ использовать DP
     */
    public void whenNotToUseDP() {
        System.out.println("Не используйте DP если:");
        System.out.println("1. Нет перекрывающихся подзадач");
        System.out.println("2. Нет оптимальной подструктуры");
        System.out.println("3. Пространство подзадач слишком велико");
        System.out.println("4. Есть более простые решения (жадные алгоритмы)");
    }
}
```

## Основные принципы `DP`

### Оптимальная подструктура

```java
/*
 * Демонстрация оптимальной подструктуры
 */
public class OptimalSubstructure {

    /*
     * Пример: Поиск кратчайшего пути
     * Если кратчайший путь от A до C проходит через B,
     * то путь от A до B и от B до C также должны быть кратчайшими
     */
    public static class ShortestPath {
        private int[][] graph;

        public ShortestPath(int[][] graph) {
            this.graph = graph;
        }

        /*
         * Нахождение кратчайшего пути с использованием DP
         * Алгоритм Флойда-Уоршелла
         */
        public int[][] findAllPairsShortestPath() {
            int n = graph.length;
            int[][] dist = new int[n][n];

            /*/ Инициализация: копируем исходный граф
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    dist[i][j] = graph[i][j];
                }
            }

            /*/ DP: для каждой промежуточной вершины k
            for (int k = 0; k < n; k++) {
                /*/ Для каждой пары вершин (i, j)
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        /*/ Обновляем кратчайший путь через k
                        if (dist[i][k] != Integer.MAX_VALUE &&
                            dist[k][j] != Integer.MAX_VALUE) {
                            dist[i][j] = Math.min(dist[i][j],
                                dist[i][k] + dist[k][j]);
                        }
                    }
                }
            }

            return dist;
        }
    }
}
```

### Перекрывающиеся подзадачи

```java
/*
 * Демонстрация перекрывающихся подзадач
 */
public class OverlappingSubproblems {

    /*
     * НЕЭФФЕКТИВНО: Рекурсия без мемоизации
     * Временная сложность: O(2^n)
     */
    public static int fibonacciNaive(int n) {
        /*/ Базовые случаи
        if (n <= 1) {
            return n;
        }

        /*/ Рекурсивные вызовы - много повторных вычислений!
        return fibonacciNaive(n - 1) + fibonacciNaive(n - 2);
    }

    /*
     * ЭФФЕКТИВНО: С мемоизацией
     * Временная сложность: O(n)
     */
    public static int fibonacciMemoized(int n) {
        /*/ Массив для хранения вычисленных значений
        int[] memo = new int[n + 1];
        Arrays.fill(memo, -1);  // Инициализация значением "не вычислено"

        return fibonacciMemoizedHelper(n, memo);
    }

    /*
     * Вспомогательная функция с мемоизацией
     */
    private static int fibonacciMemoizedHelper(int n, int[] memo) {
        /*/ Базовые случаи
        if (n <= 1) {
            return n;
        }

        /*/ Если значение уже вычислено, возвращаем его
        if (memo[n] != -1) {
            return memo[n];
        }

        /*/ Вычисляем и сохраняем результат
        memo[n] = fibonacciMemoizedHelper(n - 1, memo) +
                  fibonacciMemoizedHelper(n - 2, memo);

        return memo[n];
    }

    /*
     * Демонстрация разницы в производительности
     */
    public static void demonstrateOverlapping() {
        int n = 40;

        System.out.println("Вычисление F(" + n + ") без мемоизации:");
        long startTime = System.nanoTime();
        int naiveResult = fibonacciNaive(n);
        long naiveTime = System.nanoTime() - startTime;
        System.out.println("Результат: " + naiveResult);
        System.out.println("Время: " + naiveTime / 1_000_000 + " мс");

        System.out.println("\nВычисление F(" + n + ") с мемоизацией:");
        startTime = System.nanoTime();
        int memoizedResult = fibonacciMemoized(n);
        long memoizedTime = System.nanoTime() - startTime;
        System.out.println("Результат: " + memoizedResult);
        System.out.println("Время: " + memoizedTime / 1_000_000 + " мс");

        System.out.println("\nУскорение: " + (naiveTime / memoizedTime) + "x");
    }
}
```

## Методы реализации `DP`

### Мемоизация (Top-Down)

Мемоизация - это техника, при которой результаты вычислений сохраняются для повторного использования.

```java
/*
 * Реализация DP с мемоизацией (рекурсивный подход)
 */
public class MemoizationDP {

    /*
     * Задача о рюкзаке с мемоизацией
     */
    public static class KnapsackMemoized {
        private int[] weights;
        private int[] values;
        private int capacity;
        private Map<String, Integer> memo;  // Кэш для результатов

        public KnapsackMemoized(int[] weights, int[] values, int capacity) {
            this.weights = weights;
            this.values = values;
            this.capacity = capacity;
            this.memo = new HashMap<>();
        }

        /*
         * Рекурсивное решение с мемоизацией
         * @param index текущий индекс предмета
         * @param remainingCapacity оставшаяся вместимость рюкзака
         * @return максимальная стоимость
         */
        public int solve(int index, int remainingCapacity) {
            /*/ Базовый случай: нет предметов или рюкзак полон
            if (index < 0 || remainingCapacity <= 0) {
                return 0;
            }

            /*/ Ключ для мемоизации
            String key = index + "," + remainingCapacity;

            /*/ Проверяем, не вычисляли ли мы уже это значение
            if (memo.containsKey(key)) {
                return memo.get(key);
            }

            /*/ Вариант 1: Не берем текущий предмет
            int withoutCurrent = solve(index - 1, remainingCapacity);

            /*/ Вариант 2: Берем текущий предмет (если помещается)
            int withCurrent = 0;
            if (weights[index] <= remainingCapacity) {
                withCurrent = values[index] +
                    solve(index - 1, remainingCapacity - weights[index]);
            }

            /*/ Выбираем максимум и сохраняем в кэш
            int result = Math.max(withoutCurrent, withCurrent);
            memo.put(key, result);

            return result;
        }

        /*
         * Получение максимальной стоимости
         */
        public int getMaxValue() {
            return solve(weights.length - 1, capacity);
        }

        /*
         * Получение статистики использования мемоизации
         */
        public int getCacheSize() {
            return memo.size();
        }
    }

    /*
     * Демонстрация мемоизации
     */
    public static void demonstrateMemoization() {
        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 6};
        int capacity = 5;

        KnapsackMemoized knapsack = new KnapsackMemoized(weights, values, capacity);
        int maxValue = knapsack.getMaxValue();

        System.out.println("Максимальная стоимость: " + maxValue);
        System.out.println("Размер кэша: " + knapsack.getCacheSize());
    }
}
```

### Табуляция (Bottom-Up)

Табуляция - это итеративный подход, при котором таблица заполняется снизу вверх.

```java
/*
 * Реализация DP с табуляцией (итеративный подход)
 */
public class TabulationDP {

    /*
     * Задача о рюкзаке с табуляцией
     */
    public static class KnapsackTabulated {
        private int[] weights;
        private int[] values;
        private int capacity;

        public KnapsackTabulated(int[] weights, int[] values, int capacity) {
            this.weights = weights;
            this.values = values;
            this.capacity = capacity;
        }

        /*
         * Итеративное решение с табуляцией
         * @return максимальная стоимость
         */
        public int solve() {
            int n = weights.length;

            /*/ Создаем таблицу DP: dp[i][w] = максимальная стоимость
            /*/ для первых i предметов и вместимости w
            int[][] dp = new int[n + 1][capacity + 1];

            /*/ Заполняем таблицу снизу вверх
            for (int i = 1; i <= n; i++) {
                for (int w = 1; w <= capacity; w++) {
                    /*/ Вариант 1: Не берем текущий предмет
                    dp[i][w] = dp[i - 1][w];

                    /*/ Вариант 2: Берем текущий предмет (если помещается)
                    if (weights[i - 1] <= w) {
                        dp[i][w] = Math.max(
                            dp[i][w],  // Не берем
                            values[i - 1] + dp[i - 1][w - weights[i - 1]]  // Берем
                        );
                    }
                }
            }

            return dp[n][capacity];
        }

        /*
         * Получение выбранных предметов
         */
        public List<Integer> getSelectedItems() {
            int n = weights.length;
            int[][] dp = new int[n + 1][capacity + 1];

            /*/ Заполняем таблицу
            for (int i = 1; i <= n; i++) {
                for (int w = 1; w <= capacity; w++) {
                    dp[i][w] = dp[i - 1][w];
                    if (weights[i - 1] <= w) {
                        dp[i][w] = Math.max(
                            dp[i][w],
                            values[i - 1] + dp[i - 1][w - weights[i - 1]]
                        );
                    }
                }
            }

            /*/ Восстанавливаем решение
            List<Integer> selected = new ArrayList<>();
            int w = capacity;

            for (int i = n; i > 0; i--) {
                if (dp[i][w] != dp[i - 1][w]) {
                    /*/ Предмет i-1 был выбран
                    selected.add(i - 1);
                    w -= weights[i - 1];
                }
            }

            Collections.reverse(selected);
            return selected;
        }
    }

    /*
     * Демонстрация табуляции
     */
    public static void demonstrateTabulation() {
        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 6};
        int capacity = 5;

        KnapsackTabulated knapsack = new KnapsackTabulated(weights, values, capacity);
        int maxValue = knapsack.solve();
        List<Integer> selected = knapsack.getSelectedItems();

        System.out.println("Максимальная стоимость: " + maxValue);
        System.out.println("Выбранные предметы: " + selected);
    }
}
```

## Классические задачи `DP`

### Числа Фибоначчи

```java
/*
 * Различные подходы к вычислению чисел Фибоначчи
 */
public class FibonacciDP {

    /*
     * Подход 1: Рекурсия без оптимизации
     * Временная сложность: O(2^n)
     * Пространственная сложность: O(n)
     */
    public static long fibonacciRecursive(int n) {
        /*/ Базовые случаи
        if (n <= 1) {
            return n;
        }

        /*/ Рекурсивные вызовы
        return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
    }

    /*
     * Подход 2: Мемоизация (Top-Down)
     * Временная сложность: O(n)
     * Пространственная сложность: O(n)
     */
    public static long fibonacciMemoized(int n) {
        /*/ Массив для хранения результатов
        long[] memo = new long[n + 1];
        Arrays.fill(memo, -1);

        return fibonacciMemoizedHelper(n, memo);
    }

    private static long fibonacciMemoizedHelper(int n, long[] memo) {
        /*/ Базовые случаи
        if (n <= 1) {
            return n;
        }

        /*/ Если значение уже вычислено, возвращаем его
        if (memo[n] != -1) {
            return memo[n];
        }

        /*/ Вычисляем и сохраняем
        memo[n] = fibonacciMemoizedHelper(n - 1, memo) +
                  fibonacciMemoizedHelper(n - 2, memo);

        return memo[n];
    }

    /*
     * Подход 3: Табуляция (Bottom-Up)
     * Временная сложность: O(n)
     * Пространственная сложность: O(n)
     */
    public static long fibonacciTabulated(int n) {
        if (n <= 1) {
            return n;
        }

        /*/ Таблица для хранения результатов
        long[] dp = new long[n + 1];
        dp[0] = 0;  // Базовый случай
        dp[1] = 1;  // Базовый случай

        /*/ Заполняем таблицу снизу вверх
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }

    /*
     * Подход 4: Оптимизация пространства
     * Временная сложность: O(n)
     * Пространственная сложность: O(1)
     */
    public static long fibonacciOptimized(int n) {
        if (n <= 1) {
            return n;
        }

        /*/ Храним только два предыдущих значения
        long prev2 = 0;  // F(0)
        long prev1 = 1;  // F(1)

        /*/ Вычисляем последовательно
        for (int i = 2; i <= n; i++) {
            long current = prev1 + prev2;
            prev2 = prev1;  // Сдвигаем значения
            prev1 = current;
        }

        return prev1;
    }

    /*
     * Демонстрация всех подходов
     */
    public static void demonstrateFibonacci() {
        int n = 40;

        System.out.println("Вычисление F(" + n + "):");

        /*/ Рекурсивный подход (медленный)
        long startTime = System.nanoTime();
        long recursiveResult = fibonacciRecursive(n);
        long recursiveTime = System.nanoTime() - startTime;
        System.out.println("Рекурсия: " + recursiveResult + " (" +
                          recursiveTime / 1_000_000 + " мс)");

        /*/ Мемоизация
        startTime = System.nanoTime();
        long memoizedResult = fibonacciMemoized(n);
        long memoizedTime = System.nanoTime() - startTime;
        System.out.println("Мемоизация: " + memoizedResult + " (" +
                          memoizedTime / 1_000_000 + " мс)");

        /*/ Табуляция
        startTime = System.nanoTime();
        long tabulatedResult = fibonacciTabulated(n);
        long tabulatedTime = System.nanoTime() - startTime;
        System.out.println("Табуляция: " + tabulatedResult + " (" +
                          tabulatedTime / 1_000_000 + " мс)");

        /*/ Оптимизированный
        startTime = System.nanoTime();
        long optimizedResult = fibonacciOptimized(n);
        long optimizedTime = System.nanoTime() - startTime;
        System.out.println("Оптимизированный: " + optimizedResult + " (" +
                          optimizedTime / 1_000_000 + " мс)");
    }
}
```

### Задача о рюкзаке

```java
/*
 * Задача о рюкзаке 0/1
 * Дано: n предметов с весами и стоимостями, рюкзак вместимостью W
 * Найти: максимальную стоимость предметов, которые можно поместить в рюкзак
 */
public class KnapsackProblem {

    /*
     * Решение с табуляцией
     */
    public static class ZeroOneKnapsack {
        private int[] weights;
        private int[] values;
        private int capacity;

        public ZeroOneKnapsack(int[] weights, int[] values, int capacity) {
            this.weights = weights;
            this.values = values;
            this.capacity = capacity;
        }

        /*
         * Нахождение максимальной стоимости
         */
        public int solve() {
            int n = weights.length;
            int[][] dp = new int[n + 1][capacity + 1];

            /*/ Заполняем таблицу
            for (int i = 1; i <= n; i++) {
                for (int w = 1; w <= capacity; w++) {
                    /*/ Не берем текущий предмет
                    dp[i][w] = dp[i - 1][w];

                    /*/ Берем текущий предмет, если помещается
                    if (weights[i - 1] <= w) {
                        dp[i][w] = Math.max(
                            dp[i][w],
                            values[i - 1] + dp[i - 1][w - weights[i - 1]]
                        );
                    }
                }
            }

            return dp[n][capacity];
        }

        /*
         * Восстановление решения (какие предметы были выбраны)
         */
        public List<Integer> getSelectedItems() {
            int n = weights.length;
            int[][] dp = new int[n + 1][capacity + 1];

            /*/ Заполняем таблицу
            for (int i = 1; i <= n; i++) {
                for (int w = 1; w <= capacity; w++) {
                    dp[i][w] = dp[i - 1][w];
                    if (weights[i - 1] <= w) {
                        dp[i][w] = Math.max(
                            dp[i][w],
                            values[i - 1] + dp[i - 1][w - weights[i - 1]]
                        );
                    }
                }
            }

            /*/ Восстанавливаем решение
            List<Integer> selected = new ArrayList<>();
            int w = capacity;

            for (int i = n; i > 0; i--) {
                if (dp[i][w] != dp[i - 1][w]) {
                    /*/ Предмет i-1 был выбран
                    selected.add(i - 1);
                    w -= weights[i - 1];
                }
            }

            Collections.reverse(selected);
            return selected;
        }
    }

    /*
     * Оптимизация пространства: O(W) вместо O(n*W)
     */
    public static class OptimizedKnapsack {
        private int[] weights;
        private int[] values;
        private int capacity;

        public OptimizedKnapsack(int[] weights, int[] values, int capacity) {
            this.weights = weights;
            this.values = values;
            this.capacity = capacity;
        }

        /*
         * Используем только один массив вместо двумерной таблицы
         */
        public int solve() {
            int n = weights.length;
            int[] dp = new int[capacity + 1];

            /*/ Обрабатываем каждый предмет
            for (int i = 0; i < n; i++) {
                /*/ Идем справа налево, чтобы не перезаписывать нужные значения
                for (int w = capacity; w >= weights[i]; w--) {
                    dp[w] = Math.max(
                        dp[w],  // Не берем предмет
                        values[i] + dp[w - weights[i]]  // Берем предмет
                    );
                }
            }

            return dp[capacity];
        }
    }

    /*
     * Демонстрация задачи о рюкзаке
     */
    public static void demonstrateKnapsack() {
        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 6};
        int capacity = 5;

        System.out.println("=== Задача о рюкзаке ===");
        System.out.println("Предметы:");
        for (int i = 0; i < weights.length; i++) {
            System.out.println("  Предмет " + i + ": вес=" + weights[i] +
                            ", стоимость=" + values[i]);
        }
        System.out.println("Вместимость рюкзака: " + capacity);

        /*/ Решение с табуляцией
        ZeroOneKnapsack knapsack = new ZeroOneKnapsack(weights, values, capacity);
        int maxValue = knapsack.solve();
        List<Integer> selected = knapsack.getSelectedItems();

        System.out.println("\nМаксимальная стоимость: " + maxValue);
        System.out.println("Выбранные предметы: " + selected);

        /*/ Оптимизированное решение
        OptimizedKnapsack optimized = new OptimizedKnapsack(weights, values, capacity);
        int optimizedValue = optimized.solve();
        System.out.println("Оптимизированное решение: " + optimizedValue);
    }
}
```

### Наибольшая общая подпоследовательность

```java
/*
 * Наибольшая общая подпоследовательность (LCS)
 * Найти длину самой длинной общей подпоследовательности двух строк
 */
public class LongestCommonSubsequence {

    /*
     * Решение с табуляцией
     */
    public static int lcs(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();

        /*/ dp[i][j] = длина LCS для text1[0..i-1] и text2[0..j-1]
        int[][] dp = new int[m + 1][n + 1];

        /*/ Заполняем таблицу
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    /*/ Символы совпадают - увеличиваем длину LCS
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    /*/ Символы не совпадают - берем максимум
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    /*
     * Восстановление самой подпоследовательности
     */
    public static String getLCS(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        /*/ Заполняем таблицу
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        /*/ Восстанавливаем подпоследовательность
        StringBuilder lcs = new StringBuilder();
        int i = m, j = n;

        while (i > 0 && j > 0) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                /*/ Символы совпадают - добавляем в результат
                lcs.append(text1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                /*/ Двигаемся вверх
                i--;
            } else {
                /*/ Двигаемся влево
                j--;
            }
        }

        return lcs.reverse().toString();
    }

    /*
     * Оптимизация пространства: O(min(m, n))
     */
    public static int lcsOptimized(String text1, String text2) {
        /*/ Используем меньшую строку для экономии памяти
        if (text1.length() < text2.length()) {
            String temp = text1;
            text1 = text2;
            text2 = temp;
        }

        int m = text1.length();
        int n = text2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }

            /*/ Обмениваем массивы
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }

    /*
     * Демонстрация LCS
     */
    public static void demonstrateLCS() {
        String text1 = "ABCDGH";
        String text2 = "AEDFHR";

        System.out.println("=== Наибольшая общая подпоследовательность ===");
        System.out.println("Строка 1: " + text1);
        System.out.println("Строка 2: " + text2);

        int length = lcs(text1, text2);
        String subsequence = getLCS(text1, text2);

        System.out.println("Длина LCS: " + length);
        System.out.println("LCS: " + subsequence);

        int optimizedLength = lcsOptimized(text1, text2);
        System.out.println("Оптимизированное решение: " + optimizedLength);
    }
}
```

### Редакционное расстояние

```java
/*
 * Редакционное расстояние (расстояние Левенштейна)
 * Минимальное количество операций для преобразования одной строки в другую
 */
public class EditDistance {

    /*
     * Решение с табуляцией
     * Операции: вставка, удаление, замена
     */
    public static int editDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        /*/ dp[i][j] = минимальное расстояние между word1[0..i-1] и word2[0..j-1]
        int[][] dp = new int[m + 1][n + 1];

        /*/ Инициализация: преобразование пустой строки
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;  // Удаление всех символов из word1
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;  // Вставка всех символов в word1
        }

        /*/ Заполняем таблицу
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    /*/ Символы совпадают - операций не требуется
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    /*/ Выбираем минимум из трех операций:
                    /*/ 1. Удаление: dp[i-1][j] + 1
                    /*/ 2. Вставка: dp[i][j-1] + 1
                    /*/ 3. Замена: dp[i-1][j-1] + 1
                    dp[i][j] = 1 + Math.min(
                        Math.min(dp[i - 1][j], dp[i][j - 1]),
                        dp[i - 1][j - 1]
                    );
                }
            }
        }

        return dp[m][n];
    }

    /*
     * Восстановление последовательности операций
     */
    public static List<String> getEditOperations(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];

        /*/ Заполняем таблицу
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        Math.min(dp[i - 1][j], dp[i][j - 1]),
                        dp[i - 1][j - 1]
                    );
                }
            }
        }

        /*/ Восстанавливаем операции
        List<String> operations = new ArrayList<>();
        int i = m, j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && word1.charAt(i - 1) == word2.charAt(j - 1)) {
                /*/ Символы совпадают - без изменений
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                /*/ Замена
                operations.add("Заменить '" + word1.charAt(i - 1) +
                             "' на '" + word2.charAt(j - 1) + "'");
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                /*/ Удаление
                operations.add("Удалить '" + word1.charAt(i - 1) + "'");
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                /*/ Вставка
                operations.add("Вставить '" + word2.charAt(j - 1) + "'");
                j--;
            }
        }

        Collections.reverse(operations);
        return operations;
    }

    /*
     * Оптимизация пространства
     */
    public static int editDistanceOptimized(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        if (m < n) {
            String temp = word1;
            word1 = word2;
            word2 = temp;
            int tempLen = m;
            m = n;
            n = tempLen;
        }

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int j = 0; j <= n; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= m; i++) {
            curr[0] = i;
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(
                        Math.min(prev[j], curr[j - 1]),
                        prev[j - 1]
                    );
                }
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }

    /*
     * Демонстрация редакционного расстояния
     */
    public static void demonstrateEditDistance() {
        String word1 = "kitten";
        String word2 = "sitting";

        System.out.println("=== Редакционное расстояние ===");
        System.out.println("Слово 1: " + word1);
        System.out.println("Слово 2: " + word2);

        int distance = editDistance(word1, word2);
        List<String> operations = getEditOperations(word1, word2);

        System.out.println("Редакционное расстояние: " + distance);
        System.out.println("Операции:");
        for (String op : operations) {
            System.out.println("  " + op);
        }

        int optimizedDistance = editDistanceOptimized(word1, word2);
        System.out.println("Оптимизированное решение: " + optimizedDistance);
    }
}
```

## Оптимизация пространства

Многие задачи `DP` можно оптимизировать по пространству, используя только необходимое количество памяти.

```java
/*
 * Техники оптимизации пространства в DP
 */
public class SpaceOptimization {

    /*
     * Пример: Оптимизация задачи о рюкзаке
     * С O(n*W) до O(W) пространства
     */
    public static class SpaceOptimizedKnapsack {
        public static int solve(int[] weights, int[] values, int capacity) {
            int n = weights.length;
            int[] dp = new int[capacity + 1];

            /*/ Обрабатываем каждый предмет
            for (int i = 0; i < n; i++) {
                /*/ Идем справа налево для сохранения предыдущих значений
                for (int w = capacity; w >= weights[i]; w--) {
                    dp[w] = Math.max(
                        dp[w],  // Не берем
                        values[i] + dp[w - weights[i]]  // Берем
                    );
                }
            }

            return dp[capacity];
        }
    }

    /*
     * Пример: Оптимизация чисел Фибоначчи
     * С O(n) до O(1) пространства
     */
    public static long fibonacciSpaceOptimized(int n) {
        if (n <= 1) return n;

        long prev2 = 0, prev1 = 1;

        for (int i = 2; i <= n; i++) {
            long current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    /*
     * Пример: Оптимизация LCS
     * С O(m*n) до O(min(m, n)) пространства
     */
    public static int lcsSpaceOptimized(String text1, String text2) {
        if (text1.length() < text2.length()) {
            String temp = text1;
            text1 = text2;
            text2 = temp;
        }

        int m = text1.length();
        int n = text2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }

            /*/ Обмен массивов
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }

    /*
     * Демонстрация оптимизации пространства
     */
    public static void demonstrateSpaceOptimization() {
        System.out.println("=== Оптимизация пространства ===");

        /*/ Фибоначчи
        System.out.println("Фибоначчи F(50):");
        System.out.println("С оптимизацией: " + fibonacciSpaceOptimized(50));

        /*/ Рюкзак
        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 6};
        int capacity = 5;
        System.out.println("\nРюкзак:");
        System.out.println("Максимальная стоимость: " +
                          SpaceOptimizedKnapsack.solve(weights, values, capacity));

        /*/ LCS
        String text1 = "ABCDGH";
        String text2 = "AEDFHR";
        System.out.println("\nLCS:");
        System.out.println("Длина: " + lcsSpaceOptimized(text1, text2));
    }
}
```

## Лучшие практики

### Рекомендации по использованию DP

Чётко определите состояние: например, `dp[i]` — результат для первых `i` элементов, `dp[i][j]` — для подзадачи (i, j), `dp[mask]` — для подмножества (битовая маска). Формула перехода должна однозначно связывать состояние с предыдущими: `dp[i] = f(dp[i-1], ...)` или `dp[i][j] = f(dp[i-1][j], dp[i][j-1], dp[i-1][j-1])`. Мемоизация (top-down) проще в реализации и считает только нужные подзадачи, но даёт накладные расходы на рекурсию и риск переполнения стека; табуляция (bottom-up) избегает рекурсии и проще для оптимизации по памяти, но может считать лишние подзадачи. Для оптимизации пространства используйте только нужные измерения, переиспользуйте массивы, битовые маски для подмножеств, при редких состояниях — `HashMap`. При отладке выводите таблицу DP на малых входах, проверяйте базовые случаи и границы индексов, корректность перехода и покрывайте решение unit-тестами. Избегайте типичных ошибок: неверная инициализация базовых случаев, off-by-one в индексах, неверный порядок заполнения, применение DP там, где достаточно жадного алгоритма.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Неверный ответ на части тестов | Неправильная инициализация или формула перехода | Проверить базовые случаи (пустой ввод, один элемент); вывести таблицу DP на малых примерах; сверить индексы |
| StackOverflowError при мемоизации | Слишком глубокая рекурсия | Перейти на табуляцию (bottom-up) или увеличить лимит стека; проверить, что базовый случай достигается |
| Превышение лимита по памяти | Двумерная/трёхмерная таблица при больших n | Оптимизировать: хранить только текущий слой/две строки; заменить массив на HashMap при разреженных состояниях |
| TLE (time limit exceeded) | Лишние подзадачи или неоптимальная формула | Убедиться, что каждая подзадача считается один раз; упростить переход; рассмотреть жадный или другой метод |

## Частые вопросы

**Когда использовать мемоизацию, а когда табуляцию?** Мемоизация удобна, когда подзадач мало и рекурсия естественна; табуляция — когда нужна оптимизация по памяти или стек ограничен. Для соревнований и продакшена часто предпочитают bottom-up из-за предсказуемой памяти.

**Как понять, что задачу можно решить DP?** Нужны оптимальная подструктура (оптимум задачи выражается через оптимумы подзадач) и перекрывающиеся подзадачи (одни и те же подзадачи считаются много раз). Если подзадачи не перекрываются, достаточно рекурсии без запоминания.

**Почему решение даёт не тот ответ?** Чаще всего: неверные базовые случаи (например, `dp[0]` или `dp[0][j]`), ошибка в индексах (off-by-one), неправильный порядок циклов в табуляции. Стоит распечатать таблицу для маленького примера и пройти формулу перехода вручную.

## Заключение

Динамическое программирование - мощный метод решения оптимизационных задач. Понимание принципов `DP` и умение применять различные техники критически важно для эффективного решения сложных алгоритмических задач.

### Ключевые выводы

1. **Оптимальная подструктура** - основа для применения `DP`
2. **Перекрывающиеся подзадачи** - признак необходимости мемоизации
3. **Мемоизация vs Табуляция** - выбор зависит от задачи
4. **Оптимизация пространства** - важна для больших задач
5. **Практика** - ключ к освоению `DP`

### Рекомендации для практики

- Решайте классические задачи `DP` на платформах типа **LeetCode**
- Анализируйте временную и пространственную сложность
- Практикуйте оптимизацию пространства
- Изучайте различные техники восстановления решений
- Комбинируйте `DP` с другими алгоритмическими техниками

Помните: `DP` - это не просто техника, это способ мышления о решении задач!

