---
title: "Вопросы на собеседовании: Жадные алгоритмы (Greedy)"
description: "Greedy choice property, optimal substructure, классические задачи (Activity Selection, Huffman, Dijkstra, Kruskal, Fractional Knapsack), почему greedy не всегда работает"
tags:
  - interview
  - algorithms
  - greedy-algorithms-interview
aliases:
  - "Greedy algorithms interview"
  - "Жадные алгоритмы собеседование"
  - "Activity selection interview"
  - "Huffman coding interview"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Жадные алгоритмы (Greedy)`

Жадные алгоритмы делают **локально оптимальный выбор** на каждом шаге, надеясь на глобальный оптимум. Когда работают — изящны и быстры (`O(n log n)` обычно). Когда нет — нужен DP. На интервью важно отличать «когда greedy работает» и **доказывать** корректность через **exchange argument**.

Дата последнего обновления: 2026-04-18

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Greedy Algorithms — Baeldung](https://www.baeldung.com/cs/greedy-approach-vs-dynamic-programming)
- [Activity Selection — Baeldung](https://www.baeldung.com/cs/activity-selection-problem)
- [Huffman Coding — Baeldung](https://www.baeldung.com/cs/huffman-coding)
- [Fractional Knapsack — Baeldung](https://www.baeldung.com/cs/fractional-knapsack-problem)
- [Greedy Algorithms — GeeksforGeeks](https://www.geeksforgeeks.org/greedy-algorithms/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое жадный алгоритм?](#q1--что-такое-жадный-алгоритм)
- [Q2. (!) Что такое greedy choice property?](#q2--что-такое-greedy-choice-property)
- [Q3. (!) Чем Greedy отличается от DP?](#q3--чем-greedy-отличается-от-dp)
- [Q4. Как доказать корректность жадного алгоритма?](#q4-как-доказать-корректность-жадного-алгоритма)

**Классические задачи**
- [Q5. (!) Activity Selection?](#q5--activity-selection)
- [Q6. (!) Fractional Knapsack — почему greedy работает?](#q6--fractional-knapsack--почему-greedy-работает)
- [Q7. (!) 0/1 Knapsack — почему greedy НЕ работает?](#q7--01-knapsack--почему-greedy-не-работает)
- [Q8. (!) Coin Change — когда greedy работает?](#q8--coin-change--когда-greedy-работает)
- [Q9. (!) Huffman Coding?](#q9--huffman-coding)

**Графы**
- [Q10. (!) Алгоритм Дейкстры — это greedy?](#q10--алгоритм-дейкстры--это-greedy)
- [Q11. (!) Алгоритмы Краскала и Прима для MST?](#q11--алгоритмы-краскала-и-прима-для-mst)

**Расписания**
- [Q12. (!) Job Sequencing с deadlines?](#q12--job-sequencing-с-deadlines)
- [Q13. Minimum Meeting Rooms?](#q13-minimum-meeting-rooms)
- [Q14. Task Scheduler с cooldown?](#q14-task-scheduler-с-cooldown)
- [Q15. Reorganize String?](#q15-reorganize-string)

**Интервалы**
- [Q16. (!) Merge Intervals?](#q16--merge-intervals)
- [Q17. (!) Insert Interval?](#q17--insert-interval)
- [Q18. Non-overlapping Intervals?](#q18-non-overlapping-intervals)
- [Q19. Minimum Number of Arrows to Burst Balloons?](#q19-minimum-number-of-arrows-to-burst-balloons)

**Прочие задачи**
- [Q20. (!) Jump Game?](#q20--jump-game)
- [Q21. Jump Game II — минимум прыжков?](#q21-jump-game-ii--минимум-прыжков)
- [Q22. (!) Gas Station — может ли робот объехать круг?](#q22--gas-station--может-ли-робот-объехать-круг)
- [Q23. Candy — минимум конфет детям?](#q23-candy--минимум-конфет-детям)
- [Q24. Partition Labels?](#q24-partition-labels)
- [Q25. (!) Best Time to Buy and Sell Stock II?](#q25--best-time-to-buy-and-sell-stock-ii)

**Мета-вопросы**
- [Q26. (!) Как распознать жадную задачу?](#q26--как-распознать-жадную-задачу)
- [Q27. Какие типичные ошибки в greedy?](#q27-какие-типичные-ошибки-в-greedy)
- [Q28. (!) Где жадные алгоритмы в production?](#q28--где-жадные-алгоритмы-в-production)

## Q1. (!) Что такое жадный алгоритм?

**Greedy algorithm** делает **локально оптимальный** выбор на каждом шаге, не пересматривая его потом, надеясь получить глобально оптимальное решение.

**Признаки:**
- Сортируем по какому-то критерию (часто)
- Перебираем элементы, делаем «жадный» выбор
- Не возвращаемся к предыдущим решениям

**Структура жадного решения:**
1. Сформулировать **жадный выбор** (что брать первым)
2. Доказать, что он не противоречит оптимальному
3. После выбора — задача сводится к меньшей подзадаче того же типа

## Q2. (!) Что такое greedy choice property?

**Greedy choice property** — глобальный оптимум можно построить через **локально оптимальные** выборы. Если на каждом шаге берём «лучший» — получаем оптимум целиком.

Это **необходимое условие** для применимости greedy. В DP оно не требуется — DP перебирает варианты.

**Примеры с greedy choice property:**
- Activity Selection (по концу)
- Huffman (минимальная пара частот)
- Fractional Knapsack (по value/weight ratio)
- Dijkstra (минимальная необработанная вершина)
- Kruskal (минимальное безопасное ребро)

**Без greedy choice property:**
- 0/1 Knapsack — нужен DP
- Longest Common Subsequence — нужен DP
- TSP — нужен DP с битмаской

## Q3. (!) Чем Greedy отличается от DP?

| Критерий | Greedy | DP |
|----------|--------|-----|
| Выбор | Локально оптимальный | Перебор всех вариантов |
| Оптимальность | Не всегда гарантирует | Всегда оптимум (если применим) |
| Сложность | `O(n log n)` или `O(n)` обычно | Полиномиальная (часто `O(n²)` или больше) |
| Память | `O(1)` или `O(n)` | `O(n)` или `O(n·k)` |
| Trade-off | Скорость vs гарантия |  Гарантия vs скорость |
| Применимость | Greedy choice property | Optimal substructure + overlapping |

**Когда выбирать greedy:**
- Доказали корректность (или известная задача)
- Нужна высокая скорость
- DP не помещается в память

## Q4. Как доказать корректность жадного алгоритма?

**Exchange argument** — самый частый метод:
1. Предположим, оптимум `OPT` отличается от greedy `G`
2. Найдём место, где они впервые расходятся
3. Покажем, что замена выбора OPT на выбор G не ухудшает решение
4. Тогда G тоже оптимум

**Пример (Activity Selection):**
- Greedy выбирает активность с минимальным end-time
- Если OPT выбирает другую — её можно заменить на greedy без ухудшения (greedy завершается раньше или одновременно)

**Альтернатива — induction**: после каждого шага greedy + остаток = оптимум.

## Q5. (!) Activity Selection?

`n` активностей с временами `[start, end]`. Найти максимум **неперекрывающихся**.

**Жадная стратегия:** сортируем по `end`, выбираем активности по порядку, если не пересекаются.

```java
record Activity(int start, int end) {}

List<Activity> selectActivities(List<Activity> activities) {
    activities.sort(Comparator.comparingInt(Activity::end));

    List<Activity> selected = new ArrayList<>();
    int lastEnd = Integer.MIN_VALUE;

    for (Activity a : activities) {
        if (a.start() >= lastEnd) {
            selected.add(a);
            lastEnd = a.end();
        }
    }
    return selected;
}
```

`O(n log n)` — доминирует сортировка.

**Доказательство:** активность с минимальным end оставляет максимум места для остальных. Любой OPT можно начать с неё (или поменять).

## Q6. (!) Fractional Knapsack — почему greedy работает?

В **дробной** задаче можно брать **долю** предмета. Жадный по value/weight ratio даёт оптимум.

```java
record Item(double weight, double value) {
    double ratio() { return value / weight; }
}

double fractionalKnapsack(Item[] items, double capacity) {
    Arrays.sort(items, (a, b) -> Double.compare(b.ratio(), a.ratio()));

    double totalValue = 0, remaining = capacity;
    for (Item item : items) {
        if (remaining <= 0) break;
        if (item.weight() <= remaining) {
            totalValue += item.value();
            remaining -= item.weight();
        } else {
            totalValue += item.ratio() * remaining;
            remaining = 0;
        }
    }
    return totalValue;
}
```

**Почему работает:** каждая единица веса самого «выгодного» предмета даёт максимум value. Дробление снимает ограничение «целиком или нет».

`O(n log n)`.

## Q7. (!) 0/1 Knapsack — почему greedy НЕ работает?

В **целочисленной** задаче нельзя брать долю. Жадный по value/weight **не даёт оптимум**:

```
Items: A (weight=10, value=60),  B (weight=20, value=100), C (weight=30, value=120)
Capacity: 50

Greedy (по ratio):
  A ratio = 6, B ratio = 5, C ratio = 4
  Берём A → 10, B → 30, C не помещается → total = 60 + 100 = 160

Optimum (DP):
  Берём B + C → 20 + 30 = 50, value = 100 + 120 = 220
```

**Greedy choice property не выполняется** — оптимум может НЕ содержать самый «выгодный» предмет. Поэтому нужен **DP** — см. [Динамическое программирование](dynamic-programming-interview.md).

## Q8. (!) Coin Change — когда greedy работает?

**Greedy работает только для канонических систем монет** (где номиналы кратны и упорядочены особым образом).

```
Канонические (greedy работает):
  USD: [1, 5, 10, 25] — для 30: 25 + 5 = 2 монеты
  EUR: [1, 2, 5, 10, 20, 50, 100, 200]

Неканонические (greedy НЕ работает):
  [1, 3, 4] — для 6: greedy = 4 + 1 + 1 = 3 монеты, optimum = 3 + 3 = 2 монеты
```

**Теорема Pearson** даёт точный критерий. Для `[1, 5, 10, 25]` — да. Для произвольных — нужен DP.

```java
// Greedy (работает для US coins)
int coinChangeGreedy(int amount) {
    int[] coins = {25, 10, 5, 1};
    int count = 0;
    for (int c : coins) {
        count += amount / c;
        amount %= c;
    }
    return count;
}
```

## Q9. (!) Huffman Coding?

Алгоритм для **lossless compression**. Строит **префиксный код** (никакое слово не префикс другого) с минимальной средней длиной.

**Жадная стратегия:** берём две **наименее частые** буквы, объединяем в узел дерева. Повторяем.

```java
record Node(char ch, int freq, Node left, Node right) {}

Node buildHuffman(Map<Character, Integer> freqs) {
    PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::freq));
    freqs.forEach((ch, f) -> pq.offer(new Node(ch, f, null, null)));

    while (pq.size() > 1) {
        Node a = pq.poll();
        Node b = pq.poll();
        pq.offer(new Node('\0', a.freq + b.freq, a, b));
    }
    return pq.poll();
}

// Кодируем: левое = 0, правое = 1
Map<Character, String> buildCodes(Node root) {
    Map<Character, String> codes = new HashMap<>();
    dfs(root, "", codes);
    return codes;
}

void dfs(Node node, String code, Map<Character, String> codes) {
    if (node == null) return;
    if (node.ch != '\0') codes.put(node.ch, code);
    dfs(node.left, code + "0", codes);
    dfs(node.right, code + "1", codes);
}
```

`O(n log n)`. Применяется в **gzip, JPEG, MP3, Brotli**.

## Q10. (!) Алгоритм Дейкстры — это greedy?

**Да.** На каждом шаге выбираем непосещённую вершину с **минимальным** известным расстоянием — жадный выбор.

**Почему работает:** для неотрицательных весов, выбранная вершина гарантированно имеет финальное расстояние (никакой обходной путь не короче).

**Почему НЕ работает с отрицательными весами:** жадный выбор может «зафиксировать» расстояние, которое позже окажется неоптимальным из-за отрицательного ребра.

Подробнее — в [Графы](../data-structures/graphs-interview.md).

## Q11. (!) Алгоритмы Краскала и Прима для MST?

**Kruskal:** сортируем рёбра, добавляем минимальное безопасное (без цикла).
**Prim:** растим дерево, добавляя минимальное ребро из текущего дерева.

Оба greedy, оба дают MST. Доказательство — **cut property**: для любого разреза минимальное ребро через разрез принадлежит MST.

Подробнее — в [Графы](../data-structures/graphs-interview.md).

## Q12. (!) Job Sequencing с deadlines?

`n` задач, каждая с deadline и profit. Каждая задача занимает 1 единицу времени. Максимизировать profit.

```java
record Job(int id, int deadline, int profit) {}

int maxProfit(Job[] jobs) {
    Arrays.sort(jobs, (a, b) -> b.profit() - a.profit()); // по убыванию profit

    int maxDeadline = Arrays.stream(jobs).mapToInt(Job::deadline).max().getAsInt();
    boolean[] slot = new boolean[maxDeadline + 1];
    int totalProfit = 0;

    for (Job j : jobs) {
        for (int t = j.deadline(); t > 0; t--) { // ищем свободный слот <= deadline
            if (!slot[t]) {
                slot[t] = true;
                totalProfit += j.profit();
                break;
            }
        }
    }
    return totalProfit;
}
```

`O(n²)`. Можно ускорить через Union-Find до `O(n log n)`.

## Q13. Minimum Meeting Rooms?

`n` встреч с `[start, end]`. Минимум комнат для одновременного проведения.

```java
int minMeetingRooms(int[][] intervals) {
    int n = intervals.length;
    int[] starts = new int[n], ends = new int[n];
    for (int i = 0; i < n; i++) {
        starts[i] = intervals[i][0];
        ends[i] = intervals[i][1];
    }
    Arrays.sort(starts);
    Arrays.sort(ends);

    int rooms = 0, endIdx = 0;
    for (int i = 0; i < n; i++) {
        if (starts[i] < ends[endIdx]) rooms++;
        else                           endIdx++;
    }
    return rooms;
}
```

`O(n log n)`. Альтернатива через **min-heap** освобождающихся встреч.

## Q14. Task Scheduler с cooldown?

`n` задач, между одинаковыми — cooldown `n`. Минимальное время.

См. [Кучи](../data-structures/heaps-interview.md) — задача решается жадно через max-heap.

## Q15. Reorganize String?

Расположить буквы строки так, чтобы не было двух одинаковых подряд.

См. [Кучи](../data-structures/heaps-interview.md) — жадный с max-heap.

## Q16. (!) Merge Intervals?

```java
int[][] merge(int[][] intervals) {
    if (intervals.length == 0) return intervals;
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

    List<int[]> result = new ArrayList<>();
    int[] current = intervals[0];
    result.add(current);

    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] <= current[1]) {
            current[1] = Math.max(current[1], intervals[i][1]); // merge
        } else {
            current = intervals[i];
            result.add(current);
        }
    }
    return result.toArray(new int[result.size()][]);
}
```

`O(n log n)` — доминирует сортировка.

## Q17. (!) Insert Interval?

```java
int[][] insert(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0;

    // 1. До newInterval
    while (i < intervals.length && intervals[i][1] < newInterval[0]) {
        result.add(intervals[i++]);
    }
    // 2. Слияние с newInterval
    while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    result.add(newInterval);
    // 3. После
    while (i < intervals.length) result.add(intervals[i++]);

    return result.toArray(new int[result.size()][]);
}
```

`O(n)` — без сортировки, intervals уже отсортированы.

## Q18. Non-overlapping Intervals?

Минимальное число интервалов для удаления, чтобы остальные не пересекались.

```java
int eraseOverlapIntervals(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[1] - b[1]); // по концу

    int end = Integer.MIN_VALUE, removed = 0;
    for (int[] i : intervals) {
        if (i[0] >= end) end = i[1];
        else              removed++;
    }
    return removed;
}
```

Activity Selection в обратной формулировке. `O(n log n)`.

## Q19. Minimum Number of Arrows to Burst Balloons?

Каждый шарик — `[xstart, xend]`. Стрела вертикально лопает все шарики, пересекающие её x-координату. Минимум стрел.

```java
int findMinArrowShots(int[][] points) {
    Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1])); // по концу

    int arrows = 1, end = points[0][1];
    for (int i = 1; i < points.length; i++) {
        if (points[i][0] > end) {
            arrows++;
            end = points[i][1];
        }
    }
    return arrows;
}
```

Та же идея greedy на интервалах: сортируем по концу, минимизируем число «точек разреза».

## Q20. (!) Jump Game?

Из массива `nums`, где `nums[i]` — макс. шаг. Можно ли достичь конца?

```java
boolean canJump(int[] nums) {
    int maxReach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > maxReach) return false;
        maxReach = Math.max(maxReach, i + nums[i]);
    }
    return true;
}
```

`O(n)`. **Жадный:** на каждом шаге обновляем `maxReach`. Если текущий `i > maxReach` — недостижим.

## Q21. Jump Game II — минимум прыжков?

```java
int jump(int[] nums) {
    int jumps = 0, currentEnd = 0, farthest = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        farthest = Math.max(farthest, i + nums[i]);
        if (i == currentEnd) {
            jumps++;
            currentEnd = farthest;
        }
    }
    return jumps;
}
```

`O(n)`. Идея: поддерживаем «текущий максимально достижимый» и «следующий, если мы прыгнем».

## Q22. (!) Gas Station — может ли робот объехать круг?

Кольцо станций с запасом газа `gas[i]` и стоимостью `cost[i]` до следующей. С какой начать?

```java
int canCompleteCircuit(int[] gas, int[] cost) {
    int totalTank = 0, currTank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) {
        int diff = gas[i] - cost[i];
        totalTank += diff;
        currTank += diff;
        if (currTank < 0) {
            start = i + 1; // не можем доехать — пробуем со следующей
            currTank = 0;
        }
    }
    return totalTank >= 0 ? start : -1;
}
```

`O(n)`. **Идея:** если общий бак положительный — решение существует. Стартуем после последнего «провала».

## Q23. Candy — минимум конфет детям?

Каждый ребёнок имеет рейтинг. Условия:
- Каждому минимум 1 конфета
- Если рейтинг выше соседа — конфет должно быть больше

```java
int candy(int[] ratings) {
    int n = ratings.length;
    int[] candies = new int[n];
    Arrays.fill(candies, 1);

    // Слева направо
    for (int i = 1; i < n; i++) {
        if (ratings[i] > ratings[i - 1]) candies[i] = candies[i - 1] + 1;
    }
    // Справа налево
    for (int i = n - 2; i >= 0; i--) {
        if (ratings[i] > ratings[i + 1]) candies[i] = Math.max(candies[i], candies[i + 1] + 1);
    }

    return Arrays.stream(candies).sum();
}
```

`O(n)` — два прохода. Можно за `O(1)` память через анализ «холмов».

## Q24. Partition Labels?

Разделить строку на максимум частей так, чтобы каждая буква встречалась только в одной части.

```java
List<Integer> partitionLabels(String s) {
    int[] lastIndex = new int[26];
    for (int i = 0; i < s.length(); i++) {
        lastIndex[s.charAt(i) - 'a'] = i;
    }

    List<Integer> result = new ArrayList<>();
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, lastIndex[s.charAt(i) - 'a']);
        if (i == end) {
            result.add(end - start + 1);
            start = i + 1;
        }
    }
    return result;
}
```

`O(n)`. Идея: расширяем текущий блок до последнего вхождения любой буквы внутри.

## Q25. (!) Best Time to Buy and Sell Stock II?

Можно делать неограниченное число сделок. Максимизировать profit.

**Greedy:** суммируем все положительные разности.

```java
int maxProfit(int[] prices) {
    int profit = 0;
    for (int i = 1; i < prices.length; i++) {
        if (prices[i] > prices[i - 1]) profit += prices[i] - prices[i - 1];
    }
    return profit;
}
```

`O(n)`. Идея: каждый локальный рост = одна сделка (купил вчера — продал сегодня).

## Q26. (!) Как распознать жадную задачу?

**Признаки:**
1. Задача про **оптимизацию** (max/min)
2. На каждом шаге есть очевидный «локально лучший» выбор
3. Можно **отсортировать** по какому-то критерию и обрабатывать в порядке
4. Подзадачи **не перекрываются** (иначе DP)
5. Часто работает на интервалах, расписаниях, графах

**Алгоритм:**
1. Сформулируй жадный выбор
2. Проверь на простых тестах
3. Попробуй найти контрпример
4. Если нашёл — переходи на DP
5. Если нет — попробуй доказать через exchange argument

## Q27. Какие типичные ошибки в greedy?

1. **Применение там, где не работает** — `0/1 Knapsack` greedy ≠ optimum
2. **Неправильный критерий сортировки** — например, по value, а не по value/weight
3. **Не учитывают edge cases** — пустой вход, один элемент, все равные
4. **Жадный выбор** локально оптимален, но **глобально** хуже — не доказали
5. **Пытаются «поправить» решение задним числом** — это не greedy, а pseudo-DP

## Q28. (!) Где жадные алгоритмы в production?

1. **Compression** — Huffman в gzip/zlib/Brotli
2. **Routing** — Dijkstra в OSPF, IS-IS
3. **Networking** — Token bucket для rate limiting
4. **Scheduling** — Earliest Deadline First (EDF), task scheduling в OS
5. **MST** — telecom network design, electrical grids
6. **Approximation algorithms для NP-hard** — Set Cover, Vertex Cover
7. **Caching** — LRU/LFU eviction
8. **Bin Packing** — First Fit, Best Fit (приближённые алгоритмы)
9. **Fractional Knapsack** — load balancing с весами
10. **Game theory** — minimax pruning

---

## See also

- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем
- [Динамическое программирование](dynamic-programming-interview.md) — альтернатива greedy
- [Divide and Conquer](divide-and-conquer-interview.md) — другая парадигма
- [Графы](../data-structures/graphs-interview.md) — Dijkstra, Kruskal, Prim — все greedy
- [Кучи](../data-structures/heaps-interview.md) — основа для жадных задач (Task Scheduler, Reorganize)
- [Массивы и строки](../data-structures/arrays-strings-interview.md) — Jump Game, Best Time to Buy
- [Алгоритмы сортировки](../sorting-searching/sorting-algorithms-interview.md) — обычно начало greedy
- [Two Pointers](two-pointers-sliding-window-interview.md) — родственная техника
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — почему greedy быстрее DP

- [[backtracking-interview|Backtracking]]
- [[divide-and-conquer-interview|Divide and Conquer]]
- [[dynamic-programming-interview|Динамическое программирование]]
- [[recursion-interview|Рекурсия]]
- [[two-pointers-sliding-window-interview|Two Pointers и Sliding Window]]
- [[algorithms-interview|Алгоритмы (обзор)]]
