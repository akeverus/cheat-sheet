---
title: "Вопросы на собеседовании: Алгоритмы поиска"
description: "Linear, Binary, Exponential, Interpolation, Jump, Ternary search. Поиск в отсортированном/повёрнутом массиве. BFS/DFS как поиск. Подводные камни overflow"
tags:
  - interview
  - algorithms
  - searching-algorithms-interview
aliases:
  - "Searching algorithms interview"
  - "Алгоритмы поиска собеседование"
  - "Binary search interview"
  - "Linear search interview"
  - "Exponential search interview"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Алгоритмы поиска`

Поиск — фундамент алгоритмических задач. Binary search — самый популярный (и обманчиво коварный) — на нём строятся LeetCode-задачи «Search in Rotated Sorted Array», «Find First and Last Position», «Median of Two Sorted Arrays». Знают подводные камни: overflow, `<` vs `<=`, граничные условия.

Дата последнего обновления: 2026-04-18

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Binary Search in Java — Baeldung](https://www.baeldung.com/java-binary-search)
- [Linear Search vs Binary Search — Baeldung](https://www.baeldung.com/cs/linear-search-vs-binary-search)
- [Exponential Search — Baeldung](https://www.baeldung.com/cs/exponential-search)
- [Interpolation Search — Baeldung](https://www.baeldung.com/cs/interpolation-search)
- [Arrays.binarySearch (Java) — Oracle Docs](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Arrays.html#binarySearch(int%5B%5D,int))

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое поисковый алгоритм?](#q1--что-такое-поисковый-алгоритм)
- [Q2. (!) Сводная таблица поисковых алгоритмов?](#q2--сводная-таблица-поисковых-алгоритмов)

**Linear Search**
- [Q3. (!) Linear Search?](#q3--linear-search)
- [Q4. Sentinel Linear Search — оптимизация?](#q4-sentinel-linear-search--оптимизация)

**Binary Search**
- [Q5. (!) Binary Search — итеративный?](#q5--binary-search--итеративный)
- [Q6. Binary Search — рекурсивный?](#q6-binary-search--рекурсивный)
- [Q7. (!) Какие подводные камни Binary Search?](#q7--какие-подводные-камни-binary-search)
- [Q8. (!) Find First and Last Position?](#q8--find-first-and-last-position)
- [Q9. (!) Search in Rotated Sorted Array?](#q9--search-in-rotated-sorted-array)
- [Q10. (!) Search Insert Position?](#q10--search-insert-position)
- [Q11. (!) Find Peak Element?](#q11--find-peak-element)
- [Q12. (!) Median of Two Sorted Arrays?](#q12--median-of-two-sorted-arrays)
- [Q13. (!) Можно ли применять Binary Search к LinkedList?](#q13--можно-ли-применять-binary-search-к-linkedlist)

**Расширенные алгоритмы**
- [Q14. Exponential Search?](#q14-exponential-search)
- [Q15. Interpolation Search?](#q15-interpolation-search)
- [Q16. Jump Search?](#q16-jump-search)
- [Q17. Ternary Search?](#q17-ternary-search)

**Binary Search на ответе**
- [Q18. (!) Что значит «binary search на ответе»?](#q18--что-значит-binary-search-на-ответе)
- [Q19. (!) Koko Eating Bananas?](#q19--koko-eating-bananas)
- [Q20. Capacity to Ship Packages within D Days?](#q20-capacity-to-ship-packages-within-d-days)
- [Q21. Split Array Largest Sum?](#q21-split-array-largest-sum)

**Поиск в графе и других структурах**
- [Q22. (!) BFS как алгоритм поиска?](#q22--bfs-как-алгоритм-поиска)
- [Q23. (!) DFS как алгоритм поиска?](#q23--dfs-как-алгоритм-поиска)
- [Q24. Поиск в HashMap — действительно O(1)?](#q24-поиск-в-hashmap--действительно-o1)
- [Q25. Поиск в TreeMap — O(log n)?](#q25-поиск-в-treemap--olog-n)

**Java стандартная библиотека**
- [Q26. (!) Arrays.binarySearch и Collections.binarySearch?](#q26--arraysbinarysearch-и-collectionsbinarysearch)
- [Q27. (!) Что возвращает binarySearch при отсутствии элемента?](#q27--что-возвращает-binarysearch-при-отсутствии-элемента)
- [Q28. List.indexOf — какая сложность?](#q28-listindexof--какая-сложность)

**Подводные камни**
- [Q29. (!) Почему mid = (low + high) / 2 опасно?](#q29--почему-mid--low--high--2-опасно)
- [Q30. Когда Binary Search работает неправильно?](#q30-когда-binary-search-работает-неправильно)
- [Q31. (!) Как протестировать корректность Binary Search?](#q31--как-протестировать-корректность-binary-search)

## Q1. (!) Что такое поисковый алгоритм?

Алгоритм, который находит элемент или решение в структуре данных или пространстве состояний. Бывают:

- **Точечный** — найти конкретный элемент (Binary Search)
- **Range** — найти все элементы в диапазоне
- **Граничный** — найти первый/последний удовлетворяющий условию
- **Минимум/максимум** — поиск экстремума
- **Граф/дерево** — BFS, DFS, A*

## Q2. (!) Сводная таблица поисковых алгоритмов?

| Алгоритм | Где работает | Сложность | Требования |
|----------|-------------|-----------|------------|
| Linear Search | Любой массив/список | `O(n)` | Нет |
| Binary Search | Отсортированный массив | `O(log n)` | Сортировка |
| Exponential Search | Отсортированный массив (unbounded) | `O(log n)` | Сортировка |
| Interpolation Search | Равномерно распределённый | `O(log log n)` среднее | Равномерность + сортировка |
| Jump Search | Отсортированный массив | `O(√n)` | Сортировка |
| Ternary Search | Унимодальный массив | `O(log₃ n)` | Унимодальность |
| BFS | Графы, деревья | `O(V + E)` | Нет |
| DFS | Графы, деревья | `O(V + E)` | Нет |
| Dijkstra | Взвешенные графы | `O((V+E) log V)` | Неотрицательные веса |
| A* | Графы с эвристикой | Зависит от h | Допустимая эвристика |
| Hash lookup | HashMap/HashSet | `O(1)` среднее | Хорошая хеш-функция |

## Q3. (!) Linear Search?

Последовательное сравнение каждого элемента с искомым.

```java
int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) return i;
    }
    return -1;
}

// Generic вариант с Predicate
<T> int linearSearch(T[] arr, Predicate<T> condition) {
    for (int i = 0; i < arr.length; i++) {
        if (condition.test(arr[i])) return i;
    }
    return -1;
}
```

Сложность: `O(n)` среднее и худшее, `O(1)` лучший.

**Когда использовать:**
- Малые массивы (< 50-100) — кеш-friendly, может быть быстрее Binary Search
- Неотсортированные данные
- Поиск нескольких вхождений (всё равно нужно пройти)

## Q4. Sentinel Linear Search — оптимизация?

В обычном линейном поиске на каждой итерации делаются **два** сравнения: индекс < length и element == target.

**Sentinel** — добавляем искомый элемент в конец массива, тогда всегда найдём:

```java
int sentinelSearch(int[] arr, int target) {
    int n = arr.length;
    int last = arr[n - 1];
    arr[n - 1] = target;

    int i = 0;
    while (arr[i] != target) i++;

    arr[n - 1] = last; // restore
    if (i < n - 1 || last == target) return i;
    return -1;
}
```

Уменьшает число сравнений вдвое. На практике эффект минимален из-за branch prediction CPU.

## Q5. (!) Binary Search — итеративный?

```java
int binarySearch(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2; // избегаем переполнения!
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) low = mid + 1;
        else                    high = mid - 1;
    }
    return -1;
}
```

`O(log n)` время, `O(1)` память. **Требует** сортированного массива.

**Ключевые детали:**
- `low <= high` — включая равенство
- `low + (high - low) / 2` — защита от overflow при больших индексах
- `mid + 1` и `mid - 1` — избегаем infinite loop

## Q6. Binary Search — рекурсивный?

```java
int binarySearchRec(int[] arr, int target, int low, int high) {
    if (low > high) return -1;
    int mid = low + (high - low) / 2;
    if (arr[mid] == target) return mid;
    if (arr[mid] < target) return binarySearchRec(arr, target, mid + 1, high);
    return binarySearchRec(arr, target, low, mid - 1);
}
```

`O(log n)` время, `O(log n)` стек. Итеративный предпочтительнее в Java (нет TCO, экономия стека).

## Q7. (!) Какие подводные камни Binary Search?

1. **Integer overflow:** `mid = (low + high) / 2` может переполниться при `low + high > Integer.MAX_VALUE`. Решение: `mid = low + (high - low) / 2`.
2. **Infinite loop:** забыли `mid + 1` или `mid - 1`. Например `low = mid` зациклит.
3. **`<` vs `<=`:** `while (low < high)` пропустит проверку последнего mid.
4. **Off-by-one:** `high = arr.length` vs `arr.length - 1`. Зависит от инварианта.
5. **Дубликаты:** обычный Binary Search вернёт **любое** вхождение. Для первого/последнего — модификация.
6. **Empty array:** не забыть `if (arr.length == 0) return -1`.

## Q8. (!) Find First and Last Position?

```java
int[] searchRange(int[] arr, int target) {
    return new int[]{firstPosition(arr, target), lastPosition(arr, target)};
}

int firstPosition(int[] arr, int target) {
    int low = 0, high = arr.length - 1, result = -1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) {
            result = mid;
            high = mid - 1; // ищем дальше слева
        } else if (arr[mid] < target) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }
    return result;
}

int lastPosition(int[] arr, int target) {
    int low = 0, high = arr.length - 1, result = -1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) {
            result = mid;
            low = mid + 1; // ищем дальше справа
        } else if (arr[mid] < target) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }
    return result;
}
```

`O(log n)` каждая операция. Идея: при найденном target продолжаем искать в нужную сторону.

## Q9. (!) Search in Rotated Sorted Array?

Массив отсортирован, потом повёрнут вокруг неизвестной точки. Найти элемент за `O(log n)`.

```java
int searchRotated(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) return mid;

        // Какая половина отсортирована?
        if (arr[low] <= arr[mid]) {
            // Левая половина отсортирована
            if (arr[low] <= target && target < arr[mid]) {
                high = mid - 1; // ищем слева
            } else {
                low = mid + 1; // ищем справа
            }
        } else {
            // Правая половина отсортирована
            if (arr[mid] < target && target <= arr[high]) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
    }
    return -1;
}
```

`O(log n)`. Идея: одна из половин всегда отсортирована — определяем какая, и проверяем, попадает ли target в её диапазон.

## Q10. (!) Search Insert Position?

Найти индекс, куда нужно вставить target, чтобы массив остался отсортированным.

```java
int searchInsert(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    while (low <= high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] == target) return mid;
        if (arr[mid] < target) low = mid + 1;
        else                    high = mid - 1;
    }
    return low; // позиция вставки
}
```

`O(log n)`. Когда цикл заканчивается, `low` — позиция, куда нужно вставить (может быть `arr.length`).

## Q11. (!) Find Peak Element?

**Peak** — элемент, больший своих соседей. Может быть несколько — найти любой за `O(log n)`.

```java
int findPeakElement(int[] arr) {
    int low = 0, high = arr.length - 1;
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (arr[mid] > arr[mid + 1]) {
            high = mid; // peak слева или mid
        } else {
            low = mid + 1; // peak справа
        }
    }
    return low;
}
```

`O(log n)`. Идея: если `arr[mid] > arr[mid+1]` — справа идём вниз, peak слева/в mid. Иначе — справа идём вверх, peak справа.

## Q12. (!) Median of Two Sorted Arrays?

Найти медиану двух отсортированных массивов за `O(log(min(m, n)))`.

```java
double findMedianSortedArrays(int[] a, int[] b) {
    if (a.length > b.length) return findMedianSortedArrays(b, a);
    int m = a.length, n = b.length, total = m + n;
    int half = (total + 1) / 2;

    int low = 0, high = m;
    while (low <= high) {
        int i = low + (high - low) / 2;
        int j = half - i;

        int aLeft = (i == 0) ? Integer.MIN_VALUE : a[i - 1];
        int aRight = (i == m) ? Integer.MAX_VALUE : a[i];
        int bLeft = (j == 0) ? Integer.MIN_VALUE : b[j - 1];
        int bRight = (j == n) ? Integer.MAX_VALUE : b[j];

        if (aLeft <= bRight && bLeft <= aRight) {
            if (total % 2 == 1) return Math.max(aLeft, bLeft);
            return (Math.max(aLeft, bLeft) + Math.min(aRight, bRight)) / 2.0;
        } else if (aLeft > bRight) {
            high = i - 1;
        } else {
            low = i + 1;
        }
    }
    throw new IllegalStateException();
}
```

Одна из самых сложных задач на Binary Search. Идея: разделить оба массива так, чтобы все элементы слева были меньше всех элементов справа.

## Q13. (!) Можно ли применять Binary Search к LinkedList?

**Формально — да**, но **неэффективно**. Доступ к `mid` требует `O(n/2)` времени → итого `O(n log n)` — хуже линейного `O(n)`.

```java
// Это работает, но медленно для длинных списков
int idx = Collections.binarySearch(linkedList, key);
```

`Collections.binarySearch(LinkedList, key)` внутри использует итератор и `O(n)` доступ к середине.

**Альтернативы:**
- **Skip list** — `O(log n)` поиск
- Скопировать в массив + Binary Search — `O(n) + O(log n)`
- TreeMap/TreeSet

Подробнее — в [Связные списки](../data-structures/linked-lists-interview.md).

## Q14. Exponential Search?

Полезен для **unbounded** массивов (не знаем длину) или когда target близок к началу.

```java
int exponentialSearch(int[] arr, int target) {
    if (arr[0] == target) return 0;
    int i = 1;
    while (i < arr.length && arr[i] < target) i *= 2;
    return Arrays.binarySearch(arr, i / 2, Math.min(i + 1, arr.length), target);
}
```

`O(log n)` — первое удвоение находит range, потом обычный Binary Search в нём.

**Применения:** поиск в потоке, infinite array problem.

## Q15. Interpolation Search?

Для **равномерно распределённых** отсортированных данных. Вместо середины — оценивает позицию пропорционально:

```
pos = low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low])
```

```java
int interpolationSearch(int[] arr, int target) {
    int low = 0, high = arr.length - 1;
    while (low <= high && target >= arr[low] && target <= arr[high]) {
        if (low == high) {
            if (arr[low] == target) return low;
            return -1;
        }
        int pos = low + ((target - arr[low]) * (high - low)) / (arr[high] - arr[low]);
        if (arr[pos] == target) return pos;
        if (arr[pos] < target) low = pos + 1;
        else                    high = pos - 1;
    }
    return -1;
}
```

`O(log log n)` среднее на равномерных данных, `O(n)` худшее. Применяется в БД для индексных поисков.

## Q16. Jump Search?

Прыжки фиксированного размера `√n`, потом Linear Search в найденном блоке.

```java
int jumpSearch(int[] arr, int target) {
    int n = arr.length;
    int step = (int) Math.sqrt(n);
    int prev = 0;

    while (arr[Math.min(step, n) - 1] < target) {
        prev = step;
        step += (int) Math.sqrt(n);
        if (prev >= n) return -1;
    }

    while (arr[prev] < target) {
        prev++;
        if (prev == Math.min(step, n)) return -1;
    }
    return arr[prev] == target ? prev : -1;
}
```

`O(√n)`. Полезен когда **доступ к произвольному элементу дорогой**, но возможен (например, диск с linear access).

## Q17. Ternary Search?

Делит массив на **три** части. Применяется для **унимодальных** функций (один максимум/минимум).

```java
double ternarySearchMax(double low, double high, Function<Double, Double> f) {
    while (high - low > 1e-9) {
        double m1 = low + (high - low) / 3;
        double m2 = high - (high - low) / 3;
        if (f.apply(m1) < f.apply(m2)) low = m1;
        else                            high = m2;
    }
    return (low + high) / 2;
}
```

`O(log₃ n)`. Применяется в численной оптимизации, поиске экстремумов функций.

## Q18. (!) Что значит «binary search на ответе»?

**Binary search on answer** — техника, когда ищем не элемент в массиве, а значение в **диапазоне возможных ответов**.

Применяется когда:
1. Можно проверить **«может ли ответ быть X?»** за полиномиальное время
2. Свойство монотонно: если `X` подходит, то и любое `Y ≥ X` (или `≤ X`) тоже

```java
int binarySearchAnswer(int low, int high, Predicate<Integer> canDo) {
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (canDo.test(mid)) high = mid;
        else                  low = mid + 1;
    }
    return low;
}
```

**Применения:** Koko Eating Bananas, Split Array, Capacity to Ship, Aggressive Cows.

## Q19. (!) Koko Eating Bananas?

Коко ест бананы. `piles[i]` — куча бананов. За час ест `k` бананов из одной кучи (если в куче меньше — доедает). Найти минимальное `k`, чтобы успеть за `h` часов.

```java
int minEatingSpeed(int[] piles, int h) {
    int low = 1, high = Arrays.stream(piles).max().getAsInt();
    while (low < high) {
        int mid = low + (high - low) / 2;
        long hours = 0;
        for (int p : piles) hours += (p + mid - 1) / mid; // ceil(p/mid)
        if (hours <= h) high = mid;
        else            low = mid + 1;
    }
    return low;
}
```

`O(n log m)`, где `m = max(piles)`. **Идея:** перебираем `k` через Binary Search; для каждого `k` проверяем, успеваем ли за `h` часов.

## Q20. Capacity to Ship Packages within D Days?

Дано `weights[]` и `D` дней. Найти минимальную грузоподъёмность корабля.

```java
int shipWithinDays(int[] weights, int D) {
    int low = Arrays.stream(weights).max().getAsInt();
    int high = Arrays.stream(weights).sum();
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (canShip(weights, D, mid)) high = mid;
        else                           low = mid + 1;
    }
    return low;
}

boolean canShip(int[] weights, int D, int capacity) {
    int days = 1, current = 0;
    for (int w : weights) {
        if (current + w > capacity) {
            days++;
            current = 0;
        }
        current += w;
    }
    return days <= D;
}
```

Аналогично Koko: ищем минимальную capacity через Binary Search.

## Q21. Split Array Largest Sum?

Дано `arr[]` и `k`. Разделить на `k` непрерывных подмассивов так, чтобы максимальная сумма подмассива была минимальной.

```java
int splitArray(int[] arr, int k) {
    int low = Arrays.stream(arr).max().getAsInt();
    int high = Arrays.stream(arr).sum();
    while (low < high) {
        int mid = low + (high - low) / 2;
        if (canSplit(arr, k, mid)) high = mid;
        else                        low = mid + 1;
    }
    return low;
}

boolean canSplit(int[] arr, int k, int maxSum) {
    int parts = 1, current = 0;
    for (int x : arr) {
        if (current + x > maxSum) {
            parts++;
            current = 0;
        }
        current += x;
    }
    return parts <= k;
}
```

Та же идея. Альтернатива через DP — `O(n² · k)`. Binary search — `O(n log(sum))` — намного быстрее.

## Q22. (!) BFS как алгоритм поиска?

**Breadth-First Search** — поиск кратчайшего пути в **невзвешенном** графе.

```java
int bfsShortestPath(List<List<Integer>> adj, int start, int end) {
    boolean[] visited = new boolean[adj.size()];
    Queue<int[]> queue = new ArrayDeque<>();
    queue.offer(new int[]{start, 0});
    visited[start] = true;

    while (!queue.isEmpty()) {
        int[] curr = queue.poll();
        if (curr[0] == end) return curr[1];
        for (int v : adj.get(curr[0])) {
            if (!visited[v]) {
                visited[v] = true;
                queue.offer(new int[]{v, curr[1] + 1});
            }
        }
    }
    return -1;
}
```

`O(V + E)`. Подробнее — в [Графы](../data-structures/graphs-interview.md).

## Q23. (!) DFS как алгоритм поиска?

**Depth-First Search** — поиск любого пути, цикла, компонент связности.

```java
boolean dfs(List<List<Integer>> adj, int u, int target, boolean[] visited) {
    if (u == target) return true;
    visited[u] = true;
    for (int v : adj.get(u)) {
        if (!visited[v] && dfs(adj, v, target, visited)) return true;
    }
    return false;
}
```

`O(V + E)`. Не находит кратчайший путь в общем случае, но проще для backtracking задач.

## Q24. Поиск в HashMap — действительно O(1)?

**Среднее** — `O(1)`. **Худшее** — `O(log n)` (с Java 8+ благодаря treeify) или `O(n)` (до Java 8 или плохой hashCode).

Условия для `O(1)`:
- Хорошая хеш-функция (равномерное распределение)
- Load factor разумный (не > 1)
- `equals()` работает быстро

При плохом hashCode `HashMap` деградирует. Подробнее — в [Хеш-таблицы](../data-structures/hash-tables-interview.md).

## Q25. Поиск в TreeMap — O(log n)?

**Да** — `TreeMap` основан на красно-чёрном дереве. Все операции `get/put/remove` — `O(log n)` гарантированно.

```java
TreeMap<String, Integer> map = new TreeMap<>();
map.get("key");      // O(log n)
map.floorKey("k");   // O(log n) — наибольший ключ ≤ "k"
map.ceilingKey("k"); // O(log n) — наименьший ключ ≥ "k"
```

`floorKey`/`ceilingKey`/`headMap`/`subMap` — те операции, которых нет в `HashMap` (нет порядка).

## Q26. (!) Arrays.binarySearch и Collections.binarySearch?

```java
// Arrays.binarySearch — для массивов
int[] arr = {1, 3, 5, 7, 9};
int idx = Arrays.binarySearch(arr, 5);   // 2
int notFound = Arrays.binarySearch(arr, 6); // -4 (= -(insertionPoint+1))

// Collections.binarySearch — для List
List<Integer> list = List.of(1, 3, 5, 7, 9);
int idx2 = Collections.binarySearch(list, 5); // 2
```

**Подвох:** `Collections.binarySearch(linkedList, key)` работает за `O(n log n)` — линейный доступ к середине. Используй только для `RandomAccess` коллекций (`ArrayList`).

## Q27. (!) Что возвращает binarySearch при отсутствии элемента?

`-(insertionPoint + 1)`, где `insertionPoint` — индекс, куда нужно вставить элемент, чтобы массив остался сортированным.

```java
int[] arr = {1, 3, 5, 7, 9};
int result = Arrays.binarySearch(arr, 4);
// result = -3 → insertionPoint = -result - 1 = 2
// 4 нужно вставить на позицию 2: [1, 3, 4, 5, 7, 9]

if (result < 0) {
    int insertAt = -result - 1;
    // используй insertAt
}
```

Это позволяет за один вызов узнать «нашли или куда вставить». Удобно для maintain sorted list.

## Q28. List.indexOf — какая сложность?

`List.indexOf(o)` — `O(n)`, **линейный поиск через equals()**. Не требует сортировки. Возвращает первое вхождение или `-1`.

```java
List<String> list = List.of("apple", "banana", "apple");
list.indexOf("apple");      // 0
list.lastIndexOf("apple");  // 2
list.contains("cherry");    // false (тоже O(n))
```

**Для частого contains:** конвертируй в `HashSet` — `O(1)` lookup.

## Q29. (!) Почему mid = (low + high) / 2 опасно?

При больших `low + high` — **integer overflow**:

```java
int low = Integer.MAX_VALUE - 100;
int high = Integer.MAX_VALUE - 50;
int mid = (low + high) / 2; // overflow! результат отрицательный
```

**Правильно:**

```java
int mid = low + (high - low) / 2; // safe
// или
int mid = (low + high) >>> 1; // unsigned right shift — Joshua Bloch's trick
```

В 2006 году эта ошибка нашлась в `Arrays.binarySearch` JDK — была там 9 лет!

## Q30. Когда Binary Search работает неправильно?

1. **Неотсортированный массив** — undefined behavior
2. **Дубликаты** — может вернуть любое из них (нужна модификация для first/last)
3. **Floating point comparison** — `arr[mid] == target` плохо для double; используй `Math.abs(diff) < epsilon`
4. **Mutable array во время поиска** — другой поток меняет массив → результат непредсказуем
5. **NaN в double[]** — NaN ломает сравнения

## Q31. (!) Как протестировать корректность Binary Search?

1. **Empty array** — должен вернуть `-1`
2. **Single element** — найти и не найти
3. **Target в начале/конце** — граничные случаи
4. **Target между двумя элементами** — должен вернуть отрицательное
5. **Все одинаковые элементы** — должен найти
6. **Большой массив** — проверка на overflow
7. **Null array** — должен бросить NPE

```java
@Test
void testBinarySearchEdgeCases() {
    assertEquals(-1, search(new int[]{}, 5));
    assertEquals(0, search(new int[]{5}, 5));
    assertEquals(-1, search(new int[]{5}, 6));
    assertEquals(0, search(new int[]{1, 2, 3}, 1));
    assertEquals(2, search(new int[]{1, 2, 3}, 3));
    // и т.д.
}
```

Подробнее о тестировании — в [Unit Testing](../../testing/unit-testing-interview.md).

---

## See also

- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем
- [Алгоритмы сортировки](sorting-algorithms-interview.md) — Binary Search требует сортировки
- [Массивы и строки](../data-structures/arrays-strings-interview.md) — поиск в массиве
- [Связные списки](../data-structures/linked-lists-interview.md) — почему Binary Search неэффективен на linked list
- [Деревья](../data-structures/trees-interview.md) — поиск в BST
- [Хеш-таблицы](../data-structures/hash-tables-interview.md) — O(1) lookup
- [Графы](../data-structures/graphs-interview.md) — BFS, DFS как поиск
- [Divide and Conquer](../algorithmic-paradigms/divide-and-conquer-interview.md) — Binary Search как D&C
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — почему O(log n)
- [Two Pointers](../algorithmic-paradigms/two-pointers-sliding-window-interview.md) — альтернатива Binary Search
- [Java Collections](../../programming-languages/java/java-collections-interview.md) — Arrays.binarySearch, Collections.binarySearch
- [Unit Testing](../../testing/unit-testing-interview.md) — testing edge cases
