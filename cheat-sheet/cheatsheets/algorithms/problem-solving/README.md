# Решение задач

Кратко: Систематический подход к решению алгоритмических задач - от анализа проблемы до оптимизации решения с практическими примерами на Java.

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [LeetCode Problem Solving](https://leetcode.com/problemset/) - Платформа для практики алгоритмов

### Baeldung
- [Problem Solving Techniques in Java](https://www.baeldung.com/java-problem-solving) - Техники решения задач

### См. также
- `../algorithms/README.md` - Обзор алгоритмов
- `../data-structures/README.md` - Структуры данных
- `../algorithmic-paradigms/README.md` - Алгоритмические парадигмы

## Содержание

- [Методология решения задач](#методология-решения-задач)
- [Анализ сложности](#анализ-сложности)
- [Стратегии решения](#стратегии-решения)
- [Распространенные паттерны](#распространенные-паттерны)
- [Оптимизация решений](#оптимизация-решений)
- [Тестирование и отладка](#тестирование-и-отладка)
- [Практические примеры](#практические-примеры)

## Методология решения задач

Систематический подход к решению алгоритмических задач гарантирует качественное решение.

### Шаги решения задачи

```java
/**
 * Структурированный подход к решению алгоритмических задач
 */
public class ProblemSolvingMethodology {

    /**
     * Шаг 1: Понимание задачи
     * - Прочитать условие несколько раз
     * - Выделить входные данные и ожидаемый результат
     * - Определить ограничения и крайние случаи
     */
    public void understandProblem(String problemStatement) {
        // 1. Какие данные поступают на вход?
        // 2. Что должно быть на выходе?
        // 3. Какие ограничения заданы?
        // 4. Какие крайние случаи нужно рассмотреть?
    }

    /**
     * Шаг 2: Разработка примеров
     * - Создать простые примеры для понимания
     * - Разработать сложные примеры для проверки
     * - Убедиться в корректности примеров
     */
    public void developExamples() {
        // Пример 1: Простой случай
        // Вход: [1, 2, 3]
        // Выход: 6

        // Пример 2: Крайний случай
        // Вход: []
        // Выход: 0

        // Пример 3: Большой массив
        // Вход: [1, 2, ..., 1000000]
        // Выход: 500000500000
    }

    /**
     * Шаг 3: Формулировка решения
     * - Описать алгоритм словами
     * - Определить необходимые структуры данных
     * - Оценить временную и пространственную сложность
     */
    public void formulateSolution() {
        // Алгоритм:
        // 1. Инициализировать переменную суммы
        // 2. Для каждого элемента массива добавить его к сумме
        // 3. Вернуть итоговую сумму

        // Структуры данных: массив, переменная для суммы
        // Сложность: O(n) время, O(1) память
    }

    /**
     * Шаг 4: Имплементация
     * - Написать код решения
     * - Использовать понятные имена переменных
     * - Добавить комментарии для сложных частей
     */
    public int implementSolution(int[] array) {
        int sum = 0;  // Инициализируем сумму

        // Проходим по всем элементам массива
        for (int i = 0; i < array.length; i++) {
            sum += array[i];  // Добавляем текущий элемент к сумме
        }

        return sum;  // Возвращаем результат
    }

    /**
     * Шаг 5: Тестирование
     * - Протестировать на разработанных примерах
     * - Проверить крайние случаи
     * - Убедиться в корректности решения
     */
    public void testSolution() {
        // Тест 1: Простой случай
        assert implementSolution(new int[]{1, 2, 3}) == 6;

        // Тест 2: Пустой массив
        assert implementSolution(new int[]{}) == 0;

        // Тест 3: Отрицательные числа
        assert implementSolution(new int[]{-1, 2, -3}) == -2;

        System.out.println("Все тесты пройдены!");
    }
}
```

### Типичные ошибки при решении задач

```java
public class CommonMistakes {

    /**
     * ОШИБКА: Неправильное понимание задачи
     */
    public int wrongUnderstanding(int[] array) {
        // Задача: найти сумму элементов
        // Неправильное решение: вернуть длину массива
        return array.length;  // ОШИБКА!
    }

    /**
     * ОШИБКА: Игнорирование крайних случаев
     */
    public int ignoreEdgeCases(int[] array) {
        if (array.length == 0) {
            return 0;  // Хорошо
        }

        int sum = 0;
        for (int num : array) {
            sum += num;
        }
        return sum;

        // А что если array == null?
        // Будет NullPointerException!
    }

    /**
     * ПРАВИЛЬНО: Обработка всех крайних случаев
     */
    public int handleAllCases(int[] array) {
        // Проверяем на null
        if (array == null) {
            throw new IllegalArgumentException("Массив не может быть null");
        }

        // Проверяем на пустоту
        if (array.length == 0) {
            return 0;
        }

        int sum = 0;
        for (int num : array) {
            sum += num;
        }
        return sum;
    }

    /**
     * ОШИБКА: Неэффективное решение
     */
    public boolean inefficientSolution(int[] array, int target) {
        // Задача: проверить, есть ли два числа, дающие сумму target
        // Неэффективное решение: O(n²)

        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] + array[j] == target) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ПРАВИЛЬНО: Эффективное решение
     */
    public boolean efficientSolution(int[] array, int target) {
        // Эффективное решение: O(n) с использованием HashSet

        Set<Integer> seen = new HashSet<>();

        for (int num : array) {
            int complement = target - num;
            if (seen.contains(complement)) {
                return true;
            }
            seen.add(num);
        }

        return false;
    }
}
```

## Анализ сложности

Понимание временной и пространственной сложности критически важно для оценки эффективности решения.

### Временная сложность

```java
public class TimeComplexityAnalysis {

    /**
     * O(1) - Константная сложность
     * Время выполнения не зависит от размера входных данных
     */
    public int constantTime(int[] array) {
        // Доступ к элементу массива по индексу
        return array.length > 0 ? array[0] : 0;
    }

    /**
     * O(n) - Линейная сложность
     * Время выполнения пропорционально размеру входных данных
     */
    public int linearTime(int[] array) {
        int sum = 0;
        for (int num : array) {  // Один проход по массиву
            sum += num;
        }
        return sum;
    }

    /**
     * O(n²) - Квадратичная сложность
     * Два вложенных цикла по размеру входных данных
     */
    public void quadraticTime(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                // Какие-то операции над парами элементов
                System.out.println(array[i] + ", " + array[j]);
            }
        }
    }

    /**
     * O(log n) - Логарифмическая сложность
     * Типична для алгоритмов, которые делят задачу пополам
     */
    public int logarithmicTime(int n) {
        int count = 0;
        while (n > 0) {
            n = n / 2;  // Каждый шаг уменьшаем в 2 раза
            count++;
        }
        return count;
    }

    /**
     * O(n log n) - Линейно-логарифмическая сложность
     * Типична для эффективных алгоритмов сортировки
     */
    public void linearithmicTime(int[] array) {
        Arrays.sort(array);  // Большинство сортировок - O(n log n)
    }

    /**
     * Анализ сложности распространенных операций
     */
    public void analyzeCommonOperations() {
        List<String> operations = Arrays.asList(
            "ArrayList.add() - O(1) амортизированная",
            "ArrayList.get(index) - O(1)",
            "LinkedList.addFirst() - O(1)",
            "LinkedList.get(index) - O(n)",
            "HashMap.put() - O(1) средняя",
            "HashMap.get() - O(1) средняя",
            "TreeMap.put() - O(log n)",
            "Arrays.sort() - O(n log n)",
            "Arrays.binarySearch() - O(log n)"
        );

        System.out.println("Сложность распространенных операций:");
        operations.forEach(System.out::println);
    }
}
```

### Пространственная сложность

```java
public class SpaceComplexityAnalysis {

    /**
     * O(1) - Константная память
     * Используется фиксированное количество памяти
     */
    public int constantSpace(int[] array) {
        int result = 0;

        // Используем только несколько переменных
        for (int num : array) {
            result = Math.max(result, num);
        }

        return result;
    }

    /**
     * O(n) - Линейная память
     * Память пропорциональна размеру входных данных
     */
    public int[] linearSpace(int[] array) {
        int[] copy = new int[array.length];  // Создаем копию массива

        for (int i = 0; i < array.length; i++) {
            copy[i] = array[i] * 2;  // Модифицируем копию
        }

        return copy;
    }

    /**
     * O(n²) - Квадратичная память
     * Создание матрицы размером n x n
     */
    public int[][] quadraticSpace(int n) {
        int[][] matrix = new int[n][n];  // n x n элементов

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = i * j;
            }
        }

        return matrix;
    }

    /**
     * Анализ пространственной сложности алгоритмов
     */
    public void analyzeSpaceComplexity() {
        System.out.println("Пространственная сложность алгоритмов:");
        System.out.println("Bubble Sort: O(1) - сортировка на месте");
        System.out.println("Merge Sort: O(n) - дополнительный массив для слияния");
        System.out.println("Quick Sort: O(log n) - стек рекурсии");
        System.out.println("DFS: O(h) - высота дерева/графа");
        System.out.println("BFS: O(w) - максимальная ширина дерева/графа");
    }

    /**
     * Оптимизация памяти: in-place алгоритмы
     */
    public void inPlaceAlgorithms() {
        System.out.println("In-place алгоритмы (O(1) дополнительной памяти):");
        System.out.println("- Перестановка элементов массива");
        System.out.println("- Реверс массива");
        System.out.println("- Циклическая перестановка");
        System.out.println("- Большинство алгоритмов сортировки на месте");
    }
}
```

### Правила анализа сложности

```java
public class ComplexityRules {

    /**
     * Правила оценки временной сложности
     */
    public void timeComplexityRules() {
        System.out.println("Правила оценки временной сложности:");
        System.out.println("1. Игнорировать константы: O(2n) = O(n)");
        System.out.println("2. Игнорировать младшие члены: O(n² + n) = O(n²)");
        System.out.println("3. Логарифмы с разными основаниями эквивалентны: O(log₂ n) = O(log₁₀ n)");
        System.out.println("4. Учитывать худший случай: O(n) означает O(n) в худшем случае");
    }

    /**
     * Лучший, средний и худший случаи
     */
    public int[] complexityCases() {
        return new int[] {
            0,  // Лучший случай: элемент найден первым
            5,  // Средний случай: элемент найден в середине
            9   // Худший случай: элемент найден последним или не найден
        };
    }

    /**
     * Амортизированная сложность
     */
    public void amortizedComplexity() {
        List<Integer> list = new ArrayList<>();

        // ArrayList.add() - амортизированная O(1)
        // Большинство операций O(1), но иногда O(n) при расширении массива
        for (int i = 0; i < 100; i++) {
            list.add(i);  // В среднем O(1) на операцию
        }
    }
}
```

## Стратегии решения

### 1. Разделяй и властвуй (Divide and Conquer)

```java
public class DivideAndConquerStrategies {

    /**
     * Поиск максимального элемента (разделяй и властвуй)
     */
    public int findMaxDivideAndConquer(int[] array, int left, int right) {
        // Базовый случай
        if (left == right) {
            return array[left];
        }

        // Разделяем на две половины
        int mid = left + (right - left) / 2;

        // Рекурсивно находим максимум в каждой половине
        int leftMax = findMaxDivideAndConquer(array, left, mid);
        int rightMax = findMaxDivideAndConquer(array, mid + 1, right);

        // Комбинируем результаты
        return Math.max(leftMax, rightMax);
    }

    /**
     * Сортировка слиянием
     */
    public void mergeSort(int[] array) {
        if (array.length < 2) return;

        int mid = array.length / 2;
        int[] left = Arrays.copyOfRange(array, 0, mid);
        int[] right = Arrays.copyOfRange(array, mid, array.length);

        mergeSort(left);
        mergeSort(right);

        merge(array, left, right);
    }

    private void merge(int[] result, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;

        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                result[k++] = left[i++];
            } else {
                result[k++] = right[k] = right[j++];
            }
        }

        while (i < left.length) result[k++] = left[i++];
        while (j < right.length) result[k++] = right[j++];
    }
}
```

### 2. Динамическое программирование

```java
public class DynamicProgrammingStrategies {

    /**
     * Задача о рюкзаке 0/1
     */
    public int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 1; w <= capacity; w++) {
                if (weights[i-1] <= w) {
                    // Выбираем максимум: взять предмет или нет
                    dp[i][w] = Math.max(
                        dp[i-1][w],  // Не берем
                        values[i-1] + dp[i-1][w - weights[i-1]]  // Берем
                    );
                } else {
                    dp[i][w] = dp[i-1][w];  // Не можем взять
                }
            }
        }

        return dp[n][capacity];
    }

    /**
     * Наибольшая общая подпоследовательность
     */
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i-1) == text2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1] + 1;  // Символы совпадают
                } else {
                    dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);  // Берем максимум
                }
            }
        }

        return dp[m][n];
    }

    /**
     * Числа Фибоначчи с мемоизацией
     */
    public int fibonacci(int n) {
        int[] memo = new int[n + 1];
        Arrays.fill(memo, -1);
        return fibonacciMemo(n, memo);
    }

    private int fibonacciMemo(int n, int[] memo) {
        if (n <= 1) return n;
        if (memo[n] != -1) return memo[n];

        memo[n] = fibonacciMemo(n-1, memo) + fibonacciMemo(n-2, memo);
        return memo[n];
    }
}
```

### 3. Жадные алгоритмы

```java
public class GreedyStrategies {

    /**
     * Задача о размене монет (жадный алгоритм)
     */
    public List<Integer> coinChangeGreedy(int amount, int[] coins) {
        List<Integer> result = new ArrayList<>();
        Arrays.sort(coins);  // Сортируем по убыванию

        for (int i = coins.length - 1; i >= 0; i--) {
            int coin = coins[i];
            while (amount >= coin) {
                result.add(coin);
                amount -= coin;
            }
        }

        return amount == 0 ? result : new ArrayList<>();
    }

    /**
     * Жадный выбор задач (Activity Selection)
     */
    public static class Activity {
        int start, finish;

        Activity(int start, int finish) {
            this.start = start;
            this.finish = finish;
        }
    }

    public List<Activity> selectActivities(Activity[] activities) {
        // Сортируем по времени окончания
        Arrays.sort(activities, Comparator.comparingInt(a -> a.finish));

        List<Activity> selected = new ArrayList<>();

        // Выбираем первую задачу
        selected.add(activities[0]);
        int lastFinishTime = activities[0].finish;

        // Выбираем остальные непересекающиеся задачи
        for (int i = 1; i < activities.length; i++) {
            if (activities[i].start >= lastFinishTime) {
                selected.add(activities[i]);
                lastFinishTime = activities[i].finish;
            }
        }

        return selected;
    }
}
```

### 4. Поиск с возвратом (Backtracking)

```java
public class BacktrackingStrategies {

    /**
     * Генерация всех подмножеств
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSubsets(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrackSubsets(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        // Добавляем текущее подмножество в результат
        result.add(new ArrayList<>(current));

        // Пробуем добавить каждый следующий элемент
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);  // Добавляем элемент
            backtrackSubsets(nums, i + 1, current, result);  // Рекурсия
            current.remove(current.size() - 1);  // Убираем элемент (backtrack)
        }
    }

    /**
     * Задача о 8 ферзях
     */
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> solutions = new ArrayList<>();
        int[] queens = new int[n];  // queens[i] = позиция ферзя в строке i
        Arrays.fill(queens, -1);

        solveNQueensBacktrack(queens, 0, solutions);
        return solutions;
    }

    private void solveNQueensBacktrack(int[] queens, int row, List<List<String>> solutions) {
        if (row == queens.length) {
            // Найдено решение
            solutions.add(generateBoard(queens));
            return;
        }

        // Пробуем разместить ферзя в каждой колонке текущей строки
        for (int col = 0; col < queens.length; col++) {
            if (isSafe(queens, row, col)) {
                queens[row] = col;  // Размещаем ферзя
                solveNQueensBacktrack(queens, row + 1, solutions);  // Следующая строка
                queens[row] = -1;   // Убираем ферзя (backtrack)
            }
        }
    }

    private boolean isSafe(int[] queens, int row, int col) {
        for (int i = 0; i < row; i++) {
            // Проверяем вертикаль и диагонали
            if (queens[i] == col || Math.abs(queens[i] - col) == Math.abs(i - row)) {
                return false;
            }
        }
        return true;
    }

    private List<String> generateBoard(int[] queens) {
        List<String> board = new ArrayList<>();
        for (int i = 0; i < queens.length; i++) {
            char[] row = new char[queens.length];
            Arrays.fill(row, '.');
            row[queens[i]] = 'Q';
            board.add(new String(row));
        }
        return board;
    }
}
```

## Распространенные паттерны

### Паттерн "Два указателя" (Two Pointers)

```java
public class TwoPointersPattern {

    /**
     * Проверка палиндрома
     */
    public boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;

        while (left < right) {
            // Пропускаем не-буквенно-цифровые символы
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            // Сравниваем символы в нижнем регистре
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    /**
     * Сумма двух чисел в отсортированном массиве
     */
    public int[] twoSumSorted(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];

            if (sum == target) {
                return new int[]{left + 1, right + 1};  // 1-indexed
            } else if (sum < target) {
                left++;  // Увеличиваем сумму
            } else {
                right--; // Уменьшаем сумму
            }
        }

        return new int[]{-1, -1};  // Не найдено
    }

    /**
     * Контейнер с наибольшим количеством воды
     */
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1;
        int maxArea = 0;

        while (left < right) {
            // Площадь = min(высота) * расстояние
            int area = Math.min(height[left], height[right]) * (right - left);
            maxArea = Math.max(maxArea, area);

            // Двигаем указатель с меньшей высотой
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxArea;
    }
}
```

### Паттерн "Скользящее окно" (Sliding Window)

```java
public class SlidingWindowPattern {

    /**
     * Максимальная сумма подмассива фиксированной длины
     */
    public int maxSumSubarray(int[] nums, int k) {
        if (nums.length < k) return 0;

        int maxSum = 0;

        // Вычисляем сумму первого окна
        for (int i = 0; i < k; i++) {
            maxSum += nums[i];
        }

        int currentSum = maxSum;

        // Сдвигаем окно
        for (int i = k; i < nums.length; i++) {
            currentSum = currentSum - nums[i - k] + nums[i];  // Убираем старый, добавляем новый
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    /**
     * Минимальная длина подмассива с суммой >= target
     */
    public int minSubArrayLen(int target, int[] nums) {
        int minLength = Integer.MAX_VALUE;
        int currentSum = 0;
        int left = 0;

        for (int right = 0; right < nums.length; right++) {
            currentSum += nums[right];  // Расширяем окно справа

            // Сжимаем окно слева, пока сумма >= target
            while (currentSum >= target && left <= right) {
                minLength = Math.min(minLength, right - left + 1);
                currentSum -= nums[left];  // Убираем элемент слева
                left++;
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    /**
     * Самая длинная подстрока без повторяющихся символов
     */
    public int lengthOfLongestSubstring(String s) {
        Set<Character> chars = new HashSet<>();
        int left = 0, maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            // Если символ уже есть в окне, сжимаем слева
            while (chars.contains(current)) {
                chars.remove(s.charAt(left));
                left++;
            }

            chars.add(current);
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }
}
```

### Паттерн "Быстрый и медленный указатели" (Fast and Slow Pointers)

```java
public class FastSlowPointersPattern {

    /**
     * Определение цикла в связном списке (Floyd's Cycle Detection)
     */
    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }

        ListNode slow = head;
        ListNode fast = head.next;

        while (fast != null && fast.next != null) {
            if (slow == fast) {  // Указатели встретились - цикл есть
                return true;
            }

            slow = slow.next;          // Медленный: один шаг
            fast = fast.next.next;     // Быстрый: два шага
        }

        return false;
    }

    /**
     * Нахождение середины связного списка
     */
    public ListNode findMiddle(ListNode head) {
        if (head == null) return null;

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;          // Один шаг
            fast = fast.next.next;     // Два шага
        }

        return slow;  // Когда быстрый дойдет до конца, медленный будет в середине
    }

    /**
     * Нахождение начала цикла в связном списке
     */
    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }

        ListNode slow = head;
        ListNode fast = head;

        // Находим место встречи
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                // Цикл найден, находим начало
                slow = head;

                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next;
                }

                return slow;  // Начало цикла
            }
        }

        return null;  // Цикла нет
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
            this.next = null;
        }
    }
}
```

## Оптимизация решений

### Техники оптимизации

```java
public class OptimizationTechniques {

    /**
     * Мемоизация - кэширование результатов
     */
    public class FibonacciMemo {
        private Map<Integer, Long> cache = new HashMap<>();

        public long fibonacci(int n) {
            if (n <= 1) return n;
            if (cache.containsKey(n)) return cache.get(n);

            long result = fibonacci(n - 1) + fibonacci(n - 2);
            cache.put(n, result);
            return result;
        }
    }

    /**
     * Табуляция - заполнение таблицы снизу вверх
     */
    public long fibonacciTabulation(int n) {
        if (n <= 1) return n;

        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }

    /**
     * Пространственная оптимизация: O(1) памяти для Fibonacci
     */
    public long fibonacciOptimized(int n) {
        if (n <= 1) return n;

        long prev2 = 0, prev1 = 1;

        for (int i = 2; i <= n; i++) {
            long current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    /**
     * Преобразование к оптимальному типу данных
     */
    public void dataTypeOptimization() {
        // Для больших массивов используйте примитивы вместо объектов
        int[] primitiveArray = new int[1000000];      // Эффективно
        List<Integer> wrapperList = new ArrayList<>(); // Менее эффективно

        // Для множеств используйте EnumSet для enum'ов
        enum Color { RED, GREEN, BLUE }
        EnumSet<Color> colors = EnumSet.allOf(Color.class);  // Оптимально

        // Для строк используйте StringBuilder вместо конкатенации
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append(i).append(", ");
        }
        String result = sb.toString();
    }

    /**
     * Оптимизация алгоритмов сортировки
     */
    public void sortingOptimization() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6};

        // Для почти отсортированных массивов - Insertion Sort (O(n))
        // Для случайных данных - Quick Sort или Merge Sort (O(n log n))
        // Для маленьких массивов - любые алгоритмы подойдут

        Arrays.sort(array);  // Java использует оптимизированный Quick Sort

        // Для параллельной сортировки больших массивов
        Arrays.parallelSort(array);
    }
}
```

### Профилирование и измерение производительности

```java
public class PerformanceMeasurement {

    /**
     * Измерение времени выполнения
     */
    public static long measureTime(Runnable task) {
        long startTime = System.nanoTime();
        task.run();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    /**
     * Сравнение производительности алгоритмов
     */
    public void compareAlgorithms() {
        int[] sizes = {1000, 10000, 100000};

        for (int size : sizes) {
            int[] array = generateRandomArray(size);

            // Алгоритм 1: Пузырьковая сортировка
            long bubbleTime = measureTime(() -> {
                int[] copy = Arrays.copyOf(array, array.length);
                bubbleSort(copy);
            });

            // Алгоритм 2: Быстрая сортировка
            long quickTime = measureTime(() -> {
                int[] copy = Arrays.copyOf(array, array.length);
                Arrays.sort(copy);
            });

            System.out.printf("Размер: %d, Пузырьковая: %d ns, Быстрая: %d ns%n",
                            size, bubbleTime, quickTime);
        }
    }

    private int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1000);
        }
        return array;
    }

    private void bubbleSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Анализ использования памяти
     */
    public void memoryAnalysis() {
        Runtime runtime = Runtime.getRuntime();

        System.out.println("Всего памяти: " + runtime.totalMemory() / 1024 / 1024 + " MB");
        System.out.println("Свободной памяти: " + runtime.freeMemory() / 1024 / 1024 + " MB");
        System.out.println("Используемой памяти: " +
            (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + " MB");
        System.out.println("Максимальной памяти: " + runtime.maxMemory() / 1024 / 1024 + " MB");
    }
}
```

## Тестирование и отладка

### Стратегии тестирования

```java
public class TestingStrategies {

    /**
     * Тестирование на крайних случаях
     */
    public void testEdgeCases() {
        testTwoSum(new int[]{}, 0);           // Пустой массив
        testTwoSum(new int[]{1}, 1);          // Один элемент
        testTwoSum(new int[]{3, 3}, 6);       // Дубликаты
        testTwoSum(new int[]{-1, -2, -3}, -5); // Отрицательные числа
        testTwoSum(new int[]{1, 2, 3, 4}, 10); // Нет решения
    }

    /**
     * Генерация больших тестовых данных
     */
    public int[] generateLargeTestData(int size) {
        Random random = new Random(42);  // Фиксированное зерно для воспроизводимости
        int[] data = new int[size];

        for (int i = 0; i < size; i++) {
            data[i] = random.nextInt(2000000) - 1000000;  // От -1M до 1M
        }

        return data;
    }

    /**
     * Параметризованное тестирование
     */
    public void parameterizedTesting() {
        // Тестовые случаи для различных алгоритмов
        Object[][] testCases = {
            // {вход, ожидаемый_выход, описание}
            {new int[]{1, 2, 3}, 6, "Простой случай"},
            {new int[]{}, 0, "Пустой массив"},
            {new int[]{-1, 2, -3}, -2, "Отрицательные числа"},
            {new int[]{1000000, 2000000, 3000000}, 6000000, "Большие числа"}
        };

        for (Object[] testCase : testCases) {
            int[] input = (int[]) testCase[0];
            int expected = (int) testCase[1];
            String description = (String) testCase[2];

            int actual = sumArray(input);
            assert actual == expected : "Ошибка в тесте: " + description +
                                       ", ожидалось: " + expected + ", получено: " + actual;
        }
    }

    private int sumArray(int[] array) {
        int sum = 0;
        for (int num : array) {
            sum += num;
        }
        return sum;
    }

    private void testTwoSum(int[] nums, int target) {
        // Заглушка для тестирования two sum
        System.out.println("Тестирование two sum с target=" + target);
    }

    /**
     * Отладка алгоритмов
     */
    public void debuggingTechniques() {
        System.out.println("Техники отладки:");
        System.out.println("1. Добавляйте отладочный вывод для ключевых переменных");
        System.out.println("2. Используйте assertions для проверки инвариантов");
        System.out.println("3. Тестируйте на маленьких входных данных");
        System.out.println("4. Используйте debugger для пошагового выполнения");
        System.out.println("5. Проверяйте边界 условия");
        System.out.println("6. Визуализируйте алгоритм на бумаге");
        System.out.println("7. Ищите похожие решенные задачи");
    }
}
```

## Практические примеры

### Пример 1: Задача Two Sum

```java
public class TwoSumSolution {

    /**
     * Решение задачи Two Sum с использованием HashMap
     * Сложность: O(n) время, O(n) память
     */
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> numToIndex = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            // Проверяем, есть ли дополнение в map
            if (numToIndex.containsKey(complement)) {
                return new int[]{numToIndex.get(complement), i};
            }

            // Сохраняем текущий элемент
            numToIndex.put(nums[i], i);
        }

        throw new IllegalArgumentException("Решение не найдено");
    }

    /**
     * Тестирование решения
     */
    public void testTwoSum() {
        TwoSumSolution solution = new TwoSumSolution();

        // Тест 1: Простой случай
        int[] result1 = solution.twoSum(new int[]{2, 7, 11, 15}, 9);
        assert Arrays.equals(result1, new int[]{0, 1});

        // Тест 2: Отрицательные числа
        int[] result2 = solution.twoSum(new int[]{-1, -2, -3, -4}, -5);
        assert Arrays.equals(result2, new int[]{0, 2});

        System.out.println("Все тесты Two Sum пройдены!");
    }
}
```

### Пример 2: Поиск медианы в потоке данных

```java
public class MedianFinder {

    private PriorityQueue<Integer> maxHeap;  // Левая половина (меньшие числа)
    private PriorityQueue<Integer> minHeap;  // Правая половина (большие числа)

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder()); // Max-heap
        minHeap = new PriorityQueue<>();                           // Min-heap
    }

    /**
     * Добавление числа в структуру данных
     */
    public void addNum(int num) {
        // Добавляем в max-heap (левую половину)
        maxHeap.offer(num);

        // Балансируем: переносим максимум левой половины в правую
        minHeap.offer(maxHeap.poll());

        // Поддерживаем баланс размеров
        if (maxHeap.size() < minHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    /**
     * Получение медианы
     */
    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();  // Нечетное количество - максимум левой половины
        } else {
            // Четное количество - среднее двух центральных элементов
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }

    /**
     * Тестирование MedianFinder
     */
    public void testMedianFinder() {
        MedianFinder mf = new MedianFinder();

        // Добавляем числа
        mf.addNum(1);
        System.out.println("Медиана после [1]: " + mf.findMedian()); // 1.0

        mf.addNum(2);
        System.out.println("Медиана после [1,2]: " + mf.findMedian()); // 1.5

        mf.addNum(3);
        System.out.println("Медиана после [1,2,3]: " + mf.findMedian()); // 2.0

        mf.addNum(4);
        System.out.println("Медиана после [1,2,3,4]: " + mf.findMedian()); // 2.5
    }
}
```

### Пример 3: Сжатие строки

```java
public class StringCompression {

    /**
     * Сжатие строки подсчетом повторяющихся символов
     * Пример: "aabcccccaaa" -> "a2b1c5a3"
     */
    public String compress(String str) {
        if (str == null || str.length() <= 1) {
            return str;
        }

        StringBuilder compressed = new StringBuilder();
        int count = 1;

        for (int i = 1; i < str.length(); i++) {
            if (str.charAt(i) == str.charAt(i - 1)) {
                count++;  // Увеличиваем счетчик
            } else {
                // Добавляем предыдущий символ и его счетчик
                compressed.append(str.charAt(i - 1)).append(count);
                count = 1;  // Сбрасываем счетчик
            }
        }

        // Добавляем последний символ
        compressed.append(str.charAt(str.length() - 1)).append(count);

        // Возвращаем более короткую строку
        return compressed.length() < str.length() ? compressed.toString() : str;
    }

    /**
     * Альтернативное решение с использованием массива символов
     */
    public String compressArray(String str) {
        if (str == null || str.length() <= 1) {
            return str;
        }

        char[] chars = str.toCharArray();
        StringBuilder compressed = new StringBuilder();
        int count = 1;

        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == chars[i - 1]) {
                count++;
            } else {
                compressed.append(chars[i - 1]).append(count);
                count = 1;
            }
        }

        compressed.append(chars[chars.length - 1]).append(count);

        return compressed.length() < str.length() ? compressed.toString() : str;
    }

    /**
     * Тестирование сжатия строк
     */
    public void testCompression() {
        StringCompression compressor = new StringCompression();

        String[] testCases = {
            "aabcccccaaa",    // "a2b1c5a3"
            "abc",           // "abc" (не сжимается)
            "aaa",           // "a3"
            "",              // ""
            "a",             // "a"
            "aaabbbccc"      // "a3b3c3"
        };

        for (String test : testCases) {
            String compressed = compressor.compress(test);
            System.out.println("\"" + test + "\" -> \"" + compressed + "\"");
        }
    }
}
```

## Заключение

Решение алгоритмических задач - это навык, который развивается с практикой. Систематический подход с анализом сложности, выбором правильных структур данных и стратегий решения гарантирует качественные и эффективные решения.

### Ключевые принципы

1. **Понимание задачи** - внимательно читайте условие и ограничения
2. **Анализ сложности** - оценивайте время и память до написания кода
3. **Выбор подхода** - подбирайте подходящую парадигму или паттерн
4. **Тестирование** - проверяйте на крайних случаях и больших данных
5. **Оптимизация** - улучшайте решение после получения рабочего варианта

### Рекомендации по практике

- Решайте задачи ежедневно на платформах типа LeetCode, HackerRank
- Начинайте с простых задач, постепенно увеличивая сложность
- Анализируйте чужие решения после самостоятельного решения
- Ведите записи решенных задач с анализом подходов
- Участвуйте в соревнованиях по программированию
- Читайте книги по алгоритмам (CLRS, "Грокаем алгоритмы")

Помните: качество решений важнее количества решенных задач!
