---
title: "Методы ветвей и границ (Branch and Bound)"
description: "Кратко: Комплексное руководство по алгоритмам ветвей и границ - от базовых концепций до практических применений с примерами на Java."
tags:
  - algorithms
  - algorithmic-paradigms
  - branch-and-bound
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Методы ветвей и границ (Branch and Bound)

Кратко: Комплексное руководство по алгоритмам ветвей и границ — от базовых концепций до практических применений с примерами на **Java**.

## Полезные ссылки

### Официальная документация
- [Branch and Bound — GeeksforGeeks](https://www.geeksforgeeks.org/branch-and-bound-set-1-introduction-and-general-procedure/) — руководство

### Обучающие материалы
- [Oracle Java Documentation](https://docs.oracle.com/en/java/) — **Java** API и руководства

### См. также
- [Решение задач](../../basics/README.md) — решение задач
- [Поиск с возвратом](backtracking.md) — backtracking
- [Динамическое программирование](dynamic-programming.md) — **DP**

- [Жадные алгоритмы (Greedy Algorithms)](../problems/greedy-algorithms.md)
- [Разделяй и властвуй (Divide and Conquer)](divide-and-conquer.md)
## Содержание

- [Введение в Branch and Bound](#введение-в-branch-and-bound)
  - [Характеристики Branch and Bound](#характеристики-branch-and-bound)
- [Основные принципы](#основные-принципы)
  - [Три основных компонента](#три-основных-компонента)
- [Компоненты алгоритма](#компоненты-алгоритма)
  - [Общий шаблон](#общий-шаблон)
- [Классические задачи](#классические-задачи)
  - [Задача коммивояжера (TSP)](#задача-коммивояжера-tsp)
  - [Задача о рюкзаке (0/1)](#задача-о-рюкзаке-01)
- [Оптимизация Branch and Bound](#оптимизация-branch-and-bound)
  - [Улучшение оценок](#улучшение-оценок)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
  - [Ключевые выводы](#ключевые-выводы)
  - [Рекомендации для практики](#рекомендации-для-практики)

## Введение в Branch and Bound

Методы ветвей и границ (Branch and Bound) — это алгоритмическая техника для решения задач оптимизации, которая комбинирует поиск с возвратом с оценкой стоимости решений для отсечения неперспективных ветвей.

### Характеристики Branch and Bound

```java
// Ветви и границы: ветвление пространства решений, оценка границ для отсечения неперспективных ветвей.
/*
 * Основные характеристики алгоритмов Branch and Bound
 */
public class BranchAndBoundCharacteristics {

    /*
     * Признак 1: Ветвление (Branching)
     * Разбиваем задачу на подзадачи
     */
    public void demonstrateBranching() {
        System.out.println("Пример: Задача коммивояжера");
        System.out.println("Разбиваем на подзадачи:");
        System.out.println("- Путь через город A");
        System.out.println("- Путь через город B");
        System.out.println("- Путь через город C");
    }

    /*
     * Признак 2: Ограничение (Bounding)
     * Используем оценки для отсечения неперспективных ветвей
     */
    public void demonstrateBounding() {
        System.out.println("Пример: Оценка минимальной стоимости");
        System.out.println("Если оценка текущей ветви больше лучшего решения,");
        System.out.println("отсекаем эту ветвь без дальнейшего исследования");
    }

    /*
     * Признак 3: Поиск оптимального решения
     * Находим решение с минимальной (или максимальной) стоимостью
     */
    public void demonstrateOptimization() {
        System.out.println("Пример: Минимальная стоимость пути");
        System.out.println("Отслеживаем лучшее найденное решение");
        System.out.println("и используем его для отсечения ветвей");
    }

    /*
     * Когда использовать Branch and Bound
     */
    public void whenToUse() {
        System.out.println("Используйте Branch and Bound если:");
        System.out.println("1. Задача оптимизации с ограничениями");
        System.out.println("2. Можно эффективно оценить нижнюю/верхнюю границу");
        System.out.println("3. Пространство решений можно эффективно отсекать");
        System.out.println("4. Нужно найти оптимальное решение (не просто любое)");
    }
}
```

## Основные принципы

### Три основных компонента

```java
/*
 * Три основных компонента алгоритма Branch and Bound
 */
public class BranchAndBoundComponents {

    /*
     * Компонент 1: Ветвление (Branching)
     * Разбиваем задачу на подзадачи
     */
    public void branch(int[] solution, int step) {
        /*/ Создаем подзадачи для каждого возможного выбора
        for (int option : getOptions(step)) {
            solution[step] = option;
            /*/ Рекурсивно решаем подзадачу
            solveSubproblem(solution, step + 1);
        }
    }

    /*
     * Компонент 2: Ограничение (Bounding)
     * Оцениваем стоимость текущего решения
     */
    public int bound(int[] solution, int step) {
        /*/ Вычисляем оценку стоимости текущего решения
        int currentCost = calculateCost(solution, step);
        int estimatedCost = estimateRemainingCost(solution, step);
        return currentCost + estimatedCost; // Общая оценка
    }

    /*
     * Компонент 3: Отсечение (Pruning)
     * Отсекаем неперспективные ветви
     */
    public boolean shouldPrune(int[] solution, int step, int bestCost) {
        int bound = bound(solution, step);
        /*/ Отсекаем, если оценка хуже лучшего решения
        return bound >= bestCost; // Для задачи минимизации
    }

    /*/ Вспомогательные методы
    private int[] getOptions(int step) { return new int[0]; }
    private void solveSubproblem(int[] solution, int step) {}
    private int calculateCost(int[] solution, int step) { return 0; }
    private int estimateRemainingCost(int[] solution, int step) { return 0; }
}
```

## Компоненты алгоритма

### Общий шаблон

```java
/*
 * Общий шаблон алгоритма Branch and Bound
 */
public class BranchAndBoundTemplate {

    private int bestCost = Integer.MAX_VALUE; // Лучшая найденная стоимость
    private int[] bestSolution;                /*/ Лучшее решение

    /*
     * Шаблонный метод для Branch and Bound алгоритма
     * @param solution текущее частичное решение
     * @param step текущий шаг
     * @param currentCost текущая стоимость решения
     */
    public void solve(int[] solution, int step, int currentCost) {
        /*/ Вычисляем оценку стоимости текущего решения
        int bound = calculateBound(solution, step, currentCost);

        /*/ Отсечение: если оценка хуже лучшего решения, прекращаем поиск
        if (bound >= bestCost) {
            return; // Отсекаем эту ветвь
        }

        /*/ Базовый случай: решение завершено
        if (isComplete(solution, step)) {
            if (currentCost < bestCost) {
                bestCost = currentCost;           /*/ Обновляем лучшую стоимость
                bestSolution = solution.clone();  // Сохраняем лучшее решение
            }
            return;
        }

        /*/ Ветвление: пробуем все возможные варианты
        for (int option : getOptions(step)) {
            solution[step] = option;
            int newCost = currentCost + getCost(solution, step);

            /*/ Рекурсивно решаем подзадачу
            solve(solution, step + 1, newCost);

            /*/ Возврат: отменяем выбор
            solution[step] = 0;
        }
    }

    /*
     * Вычисление оценки стоимости (bound)
     */
    private int calculateBound(int[] solution, int step, int currentCost) {
        /*/ Текущая стоимость + оценка оставшейся стоимости
        return currentCost + estimateRemainingCost(solution, step);
    }

    /*/ Вспомогательные методы
    private boolean isComplete(int[] solution, int step) { return false; }
    private int[] getOptions(int step) { return new int[0]; }
    private int getCost(int[] solution, int step) { return 0; }
    private int estimateRemainingCost(int[] solution, int step) { return 0; }
}
```

## Классические задачи

### Задача коммивояжера (TSP)

```java
/*
 * Решение задачи коммивояжера методом Branch and Bound
 * Найти кратчайший путь, проходящий через все города ровно один раз
 */
public class TSPBranchAndBound {

    private int[][] distances;      /*/ Матрица расстояний между городами
    private int n;                    /*/ Количество городов
    private int bestCost;             /*/ Лучшая найденная стоимость
    private int[] bestPath;           /*/ Лучший путь

    public TSPBranchAndBound(int[][] distances) {
        this.distances = distances;
        this.n = distances.length;
        this.bestCost = Integer.MAX_VALUE;
        this.bestPath = new int[n];
    }

    /*
     * Решение задачи коммивояжера
     * @return кратчайший путь
     */
    public int[] solve() {
        boolean[] visited = new boolean[n];
        int[] path = new int[n];
        path[0] = 0;        /*/ Начинаем с города 0
        visited[0] = true;   // Помечаем город 0 как посещенный

        /*/ Начинаем поиск с города 0
        solveTSP(path, visited, 1, 0);

        return bestPath;
    }

    /*
     * Рекурсивный метод решения TSP
     * @param path текущий путь
     * @param visited массив посещенных городов
     * @param step количество посещенных городов
     * @param currentCost текущая стоимость пути
     */
    private void solveTSP(int[] path, boolean[] visited, int step, int currentCost) {
        /*/ Базовый случай: все города посещены
        if (step == n) {
            /*/ Добавляем стоимость возврата в начальный город
            int totalCost = currentCost + distances[path[step - 1]][path[0]];

            /*/ Обновляем лучшее решение, если нашли лучшее
            if (totalCost < bestCost) {
                bestCost = totalCost;
                bestPath = path.clone();
            }
            return;
        }

        /*/ Вычисляем оценку стоимости (bound)
        int bound = calculateBound(path, visited, step, currentCost);

        /*/ Отсечение: если оценка хуже лучшего решения, прекращаем поиск
        if (bound >= bestCost) {
            return; // Отсекаем эту ветвь
        }

        /*/ Ветвление: пробуем посетить каждый непосещенный город
        for (int city = 0; city < n; city++) {
            if (!visited[city]) {
                /*/ Выбор: добавляем город в путь
                path[step] = city;
                visited[city] = true;
                int newCost = currentCost + distances[path[step - 1]][city];

                /*/ Рекурсивно решаем подзадачу
                solveTSP(path, visited, step + 1, newCost);

                /*/ Возврат: отменяем выбор
                visited[city] = false;
            }
        }
    }

    /*
     * Вычисление оценки стоимости (bound)
     * Используем минимальные стоимости для оставшихся городов
     */
    private int calculateBound(int[] path, boolean[] visited, int step, int currentCost) {
        int bound = currentCost;

        /*/ Для каждого непосещенного города добавляем минимальную стоимость
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                int minCost = Integer.MAX_VALUE;
                /*/ Находим минимальную стоимость до города i
                for (int j = 0; j < n; j++) {
                    if (i != j && distances[j][i] < minCost) {
                        minCost = distances[j][i];
                    }
                }
                bound += minCost;
            }
        }

        return bound;
    }

    /*
     * Демонстрация решения TSP
     */
    public static void demonstrateTSP() {
        /*/ Матрица расстояний между городами
        int[][] distances = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };

        System.out.println("=== Задача коммивояжера ===");
        TSPBranchAndBound solver = new TSPBranchAndBound(distances);
        int[] path = solver.solve();

        System.out.println("Кратчайший путь:");
        for (int city : path) {
            System.out.print(city + " -> ");
        }
        System.out.println(path[0]);
        System.out.println("Минимальная стоимость: " + solver.bestCost);
    }
}
```

### Задача о рюкзаке (0/1)

```java
/*
 * Решение задачи о рюкзаке методом Branch and Bound
 * Найти максимальную стоимость предметов, которые можно поместить в рюкзак
 */
public class KnapsackBranchAndBound {

    private int[] weights;  // Веса предметов
    private int[] values;    /*/ Стоимости предметов
    private int capacity;    /*/ Вместимость рюкзака
    private int n;           /*/ Количество предметов
    private int bestValue;   // Лучшая найденная стоимость
    private boolean[] bestItems; // Лучший набор предметов

    public KnapsackBranchAndBound(int[] weights, int[] values, int capacity) {
        this.weights = weights;
        this.values = values;
        this.capacity = capacity;
        this.n = weights.length;
        this.bestValue = 0;
        this.bestItems = new boolean[n];
    }

    /*
     * Решение задачи о рюкзаке
     * @return максимальная стоимость
     */
    public int solve() {
        boolean[] items = new boolean[n];
        solveKnapsack(items, 0, 0, 0);
        return bestValue;
    }

    /*
     * Рекурсивный метод решения задачи о рюкзаке
     * @param items текущий набор выбранных предметов
     * @param step текущий предмет
     * @param currentWeight текущий вес
     * @param currentValue текущая стоимость
     */
    private void solveKnapsack(boolean[] items, int step, int currentWeight, int currentValue) {
        /*/ Базовый случай: все предметы рассмотрены
        if (step == n) {
            if (currentValue > bestValue) {
                bestValue = currentValue;
                bestItems = items.clone();
            }
            return;
        }

        /*/ Вычисляем оценку стоимости (bound)
        int bound = calculateBound(items, step, currentWeight, currentValue);

        /*/ Отсечение: если оценка не лучше лучшего решения, прекращаем поиск
        if (bound <= bestValue) {
            return; // Отсекаем эту ветвь
        }

        /*/ Ветвление 1: Берем текущий предмет (если помещается)
        if (currentWeight + weights[step] <= capacity) {
            items[step] = true; // Выбор: берем предмет
            solveKnapsack(items, step + 1,
                         currentWeight + weights[step],
                         currentValue + values[step]);
            items[step] = false; // Возврат: отменяем выбор
        }

        /*/ Ветвление 2: Не берем текущий предмет
        items[step] = false; // Выбор: не берем предмет
        solveKnapsack(items, step + 1, currentWeight, currentValue);
    }

    /*
     * Вычисление оценки стоимости (bound)
     * Используем жадный алгоритм для оценки верхней границы
     */
    private int calculateBound(boolean[] items, int step, int currentWeight, int currentValue) {
        int bound = currentValue;
        int remainingWeight = capacity - currentWeight;

        /*/ Жадный алгоритм: берем предметы с максимальным отношением стоимость/вес
        for (int i = step; i < n && remainingWeight > 0; i++) {
            if (weights[i] <= remainingWeight) {
                /*/ Берем предмет полностью
                bound += values[i];
                remainingWeight -= weights[i];
            } else {
                /*/ Берем часть предмета (для оценки)
                bound += (values[i] * remainingWeight) / weights[i];
                remainingWeight = 0;
            }
        }

        return bound;
    }

    /*
     * Демонстрация решения задачи о рюкзаке
     */
    public static void demonstrateKnapsack() {
        int[] weights = {2, 3, 4, 5};
        int[] values = {3, 4, 5, 6};
        int capacity = 5;

        System.out.println("=== Задача о рюкзаке (Branch and Bound) ===");
        KnapsackBranchAndBound solver = new KnapsackBranchAndBound(weights, values, capacity);
        int maxValue = solver.solve();

        System.out.println("Максимальная стоимость: " + maxValue);
        System.out.println("Выбранные предметы:");
        for (int i = 0; i < solver.bestItems.length; i++) {
            if (solver.bestItems[i]) {
                System.out.println("  Предмет " + i + ": вес=" + weights[i] +
                                 ", стоимость=" + values[i]);
            }
        }
    }
}
```

## Оптимизация Branch and Bound

### Улучшение оценок

```java
/*
 * Оптимизация Branch and Bound с улучшенными оценками
 */
public class OptimizedBranchAndBound {

    /*
     * Улучшенная оценка для TSP с использованием минимального остовного дерева
     */
    public static class ImprovedTSPBound {
        /*
         * Вычисление оценки с использованием MST
         * MST дает хорошую нижнюю оценку для TSP
         */
        public int calculateMSTBound(int[][] distances, boolean[] visited) {
            /*/ Упрощенная реализация: используем минимальные стоимости
            int bound = 0;
            for (int i = 0; i < distances.length; i++) {
                if (!visited[i]) {
                    int minCost = Integer.MAX_VALUE;
                    for (int j = 0; j < distances.length; j++) {
                        if (distances[j][i] < minCost) {
                            minCost = distances[j][i];
                        }
                    }
                    bound += minCost;
                }
            }
            return bound;
        }
    }

    /*
     * Приоритетная очередь для лучшего порядка исследования ветвей
     */
    public static class PriorityQueueBranchAndBound {
        /*
         * Исследуем ветви с лучшими оценками первыми
         */
        public void solveWithPriority() {
            /*/ Используем PriorityQueue для хранения подзадач
            /*/ Исследуем подзадачи с лучшими оценками первыми
            /*/ Это может ускорить нахождение хорошего решения
        }
    }
}
```

## Лучшие практики

Качественные оценки (bounds) критичны: используйте точные оценки где возможно, балансируйте точность и скорость вычисления, при необходимости — эвристики. Исследуйте ветви с лучшими оценками первыми (например, приоритетная очередь). Раннее хорошее решение улучшает отсечение: жадный или эвристический старт, локальный поиск, обновление лучшего решения при первом же нахождении. Компактное представление состояния и итеративные версии при глубокой рекурсии снижают расход памяти.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Слишком много ветвей, долгое время | Слабые оценки, мало отсечений | Улучшить нижнюю/верхнюю границу, усилить отсечение по границе |
| Переполнение стека / OutOfMemory | Глубокая рекурсия или много активных узлов | Итеративная реализация с очередью, ограничение глубины или размера очереди |
| Решение не оптимально | Ошибка в оценке или порядке обхода | Проверить корректность bounds и порядок исследования ветвей |

## Частые вопросы

**Когда выбирать Branch and Bound вместо динамического программирования?** B&B удобен, когда подзадачи сложно свести к перекрывающимся подзадачам с простой рекуррентностью; B&B явно перебирает пространство решений с отсечением. DP — когда есть оптимальная подструктура и перекрывающиеся подзадачи.

**Как ускорить Branch and Bound?** Улучшать оценки (bounds), чтобы раньше отсекать ветви; получать хорошее начальное решение жадным/эвристическим методом; исследовать первыми ветви с лучшей оценкой.

**Чем отличается от backtracking?** Backtracking перебирает все варианты с возвратом; B&B добавляет оценку границ и отсекает ветви, заведомо не дающие лучшего решения.

## Заключение

Методы ветвей и границ — мощная техника для решения задач оптимизации. Понимание принципов **Branch and Bound** критически важно для эффективного решения сложных комбинаторных задач.

### Ключевые выводы

1. **Ветвление** — разбиваем задачу на подзадачи
2. **Ограничение** — используем оценки для отсечения ветвей
3. **Отсечение** — эффективные оценки уменьшают пространство поиска
4. **Оптимизация** — находим оптимальное решение, а не просто любое
5. **Практика** — ключ к пониманию эффективных оценок

### Рекомендации для практики

- Решайте классические задачи (TSP, `Knapsack`, Assignment)
- Экспериментируйте с различными методами оценки
- Анализируйте влияние качества оценок на производительность
- Сравнивайте с другими подходами (DP, Greedy)
- Практикуйте оптимизацию порядка исследования ветвей

Помните: качество оценок (bounds) критически важно для эффективности **Branch and Bound** алгоритмов!

