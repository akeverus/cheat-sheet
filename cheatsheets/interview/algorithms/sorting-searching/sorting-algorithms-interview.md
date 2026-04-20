---
title: "Вопросы на собеседовании: Алгоритмы сортировки"
description: "Bubble, Insertion, Selection, Merge, Quick, Heap, Tim, Counting, Radix, Bucket sort. Stable vs unstable, in-place, comparison vs non-comparison, Java Arrays.sort внутри, 3-way partition"
tags:
  - interview
  - algorithms
  - sorting-algorithms-interview
aliases:
  - "Sorting algorithms interview"
  - "Алгоритмы сортировки собеседование"
  - "Quick sort interview"
  - "Merge sort interview"
  - "TimSort interview"
  - "Heap sort interview"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Алгоритмы сортировки`

Сортировка — классика интервью. Знают наизусть `O(n log n)` и `O(n²)` алгоритмы, отличия stable/unstable, in-place, и что Java `Arrays.sort()` использует Dual-Pivot Quicksort для примитивов и TimSort для объектов.

Дата последнего обновления: 2026-04-18

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Sorting Algorithms in Java — Baeldung](https://www.baeldung.com/java-sorting)
- [Quicksort — Baeldung](https://www.baeldung.com/java-quicksort)
- [Merge Sort — Baeldung](https://www.baeldung.com/java-merge-sort)
- [Heap Sort — Baeldung](https://www.baeldung.com/java-heap-sort)
- [TimSort Wikipedia](https://en.wikipedia.org/wiki/Timsort)
- [Counting Sort — Baeldung](https://www.baeldung.com/java-counting-sort)
- [Radix Sort — Baeldung](https://www.baeldung.com/java-radix-sort)
- [Sorting Visualizer](https://visualgo.net/en/sorting)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Какие свойства характеризуют алгоритм сортировки?](#q1--какие-свойства-характеризуют-алгоритм-сортировки)
- [Q2. (!) Чем stable отличается от unstable сортировки?](#q2--чем-stable-отличается-от-unstable-сортировки)
- [Q3. (!) Что такое in-place сортировка?](#q3--что-такое-in-place-сортировка)
- [Q4. (!) Сводная таблица сортировок?](#q4--сводная-таблица-сортировок)

**Comparison-based — O(n²)**
- [Q5. Bubble Sort?](#q5-bubble-sort)
- [Q6. Selection Sort?](#q6-selection-sort)
- [Q7. (!) Insertion Sort?](#q7--insertion-sort)

**Comparison-based — O(n log n)**
- [Q8. (!) Merge Sort?](#q8--merge-sort)
- [Q9. (!) Quick Sort?](#q9--quick-sort)
- [Q10. (!) Какие оптимизации Quick Sort?](#q10--какие-оптимизации-quick-sort)
- [Q11. (!) Heap Sort?](#q11--heap-sort)
- [Q12. (!) TimSort — что используется в Java?](#q12--timsort--что-используется-в-java)
- [Q13. Introsort — что используется в C++?](#q13-introsort--что-используется-в-c)
- [Q14. (!) Почему сравнительные сортировки не быстрее O(n log n)?](#q14--почему-сравнительные-сортировки-не-быстрее-on-log-n)

**Non-comparison — линейные**
- [Q15. (!) Counting Sort?](#q15--counting-sort)
- [Q16. (!) Radix Sort?](#q16--radix-sort)
- [Q17. Bucket Sort?](#q17-bucket-sort)

**Java internals**
- [Q18. (!) Что внутри Arrays.sort()?](#q18--что-внутри-arrayssort)
- [Q19. (!) Чем отличается Arrays.sort и Collections.sort?](#q19--чем-отличается-arrayssort-и-collectionssort)
- [Q20. Parallel sort?](#q20-parallel-sort)

**Специальные приёмы**
- [Q21. (!) Dutch National Flag — 3-way partition?](#q21--dutch-national-flag--3-way-partition)
- [Q22. Внешняя сортировка (external sort)?](#q22-внешняя-сортировка-external-sort)
- [Q23. (!) Sort almost-sorted array?](#q23--sort-almost-sorted-array)
- [Q24. Топологическая сортировка vs обычная?](#q24-топологическая-сортировка-vs-обычная)

**Custom Comparator**
- [Q25. (!) Как сортировать кастомным компаратором?](#q25--как-сортировать-кастомным-компаратором)
- [Q26. Стабильно ли Comparator.thenComparing?](#q26-стабильно-ли-comparatorthencomparing)
- [Q27. Что такое natural ordering?](#q27-что-такое-natural-ordering)

**Performance и подводные камни**
- [Q28. (!) Когда O(n²) сортировка выиграет у O(n log n)?](#q28--когда-on²-сортировка-выиграет-у-on-log-n)
- [Q29. (!) Что такое cache locality в контексте сортировки?](#q29--что-такое-cache-locality-в-контексте-сортировки)
- [Q30. (!) Как защититься от worst-case Quick Sort?](#q30--как-защититься-от-worst-case-quick-sort)
- [Q31. Hybrid sorting — почему всегда лучший выбор?](#q31-hybrid-sorting--почему-всегда-лучший-выбор)

## Q1. (!) Какие свойства характеризуют алгоритм сортировки?

1. **Сложность** — best/avg/worst time, space
2. **Stable** — сохраняет относительный порядок равных элементов
3. **In-place** — не использует доп. память больше `O(log n)`
4. **Adaptive** — быстрее на частично отсортированных данных
5. **Online** — может работать с потоком данных (получая по одному элементу)
6. **Comparison-based** vs **non-comparison-based**
7. **External** — работает с данными, не помещающимися в память

## Q2. (!) Чем stable отличается от unstable сортировки?

**Stable:** относительный порядок равных элементов сохраняется.

```
Вход:  (Alice, 30), (Bob, 25), (Carol, 30)   — сортируем по возрасту
Stable:   (Bob, 25), (Alice, 30), (Carol, 30)   — Alice раньше Carol
Unstable: (Bob, 25), (Carol, 30), (Alice, 30)   — порядок может поменяться
```

**Stable:** Insertion, Merge, Tim, Bubble, Counting (если правильно реализован).
**Unstable:** Quick, Heap, Selection (наивная), Shell.

**Зачем нужна:** при многоэтапной сортировке (сначала по фамилии, потом по возрасту) stable сохраняет порядок предыдущей сортировки.

## Q3. (!) Что такое in-place сортировка?

**In-place** — алгоритм использует **`O(1)` или `O(log n)`** дополнительной памяти (помимо входа).

| Алгоритм | In-place |
|----------|----------|
| Bubble, Selection, Insertion | Да (`O(1)`) |
| Quick Sort | Да (`O(log n)` стек) |
| Heap Sort | Да (`O(1)`) |
| Merge Sort | **Нет** (`O(n)`) |
| Counting Sort | Нет (`O(k)`) |
| Radix Sort | Нет |
| Tim Sort | **Нет** (`O(n)`) |

При работе с очень большими массивами или ограниченной памятью — in-place критичен.

## Q4. (!) Сводная таблица сортировок?

| Алгоритм | Best | Avg | Worst | Space | Stable | In-place |
|----------|------|-----|-------|-------|--------|----------|
| Bubble | `O(n)` | `O(n²)` | `O(n²)` | `O(1)` | Да | Да |
| Selection | `O(n²)` | `O(n²)` | `O(n²)` | `O(1)` | Нет | Да |
| Insertion | `O(n)` | `O(n²)` | `O(n²)` | `O(1)` | Да | Да |
| Merge | `O(n log n)` | `O(n log n)` | `O(n log n)` | `O(n)` | Да | Нет |
| Quick | `O(n log n)` | `O(n log n)` | `O(n²)` | `O(log n)` | Нет | Да |
| Heap | `O(n log n)` | `O(n log n)` | `O(n log n)` | `O(1)` | Нет | Да |
| Tim | `O(n)` | `O(n log n)` | `O(n log n)` | `O(n)` | Да | Нет |
| Counting | `O(n + k)` | `O(n + k)` | `O(n + k)` | `O(k)` | Да | Нет |
| Radix | `O(d(n + k))` | `O(d(n + k))` | `O(d(n + k))` | `O(n + k)` | Да | Нет |
| Bucket | `O(n + k)` | `O(n + k)` | `O(n²)` | `O(n + k)` | Да | Нет |

`k` — диапазон значений, `d` — число цифр в самом длинном числе.

## Q5. Bubble Sort?

**Bubble Sort** — соседние элементы сравниваются и меняются местами; повторяем, пока не будет ни одного обмена.

```java
void bubbleSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        boolean swapped = false;
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                int tmp = arr[j]; arr[j] = arr[j+1]; arr[j+1] = tmp;
                swapped = true;
            }
        }
        if (!swapped) break; // оптимизация: уже отсортировано
    }
}
```

`O(n²)` среднее, `O(n)` лучший. На практике не используется — Insertion Sort быстрее на тех же данных. Знают для понимания.

## Q6. Selection Sort?

**Selection Sort** — на каждом шаге ищем минимум в неотсортированной части и ставим на текущую позицию.

```java
void selectionSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
        int minIdx = i;
        for (int j = i + 1; j < n; j++) {
            if (arr[j] < arr[minIdx]) minIdx = j;
        }
        if (minIdx != i) {
            int tmp = arr[i]; arr[i] = arr[minIdx]; arr[minIdx] = tmp;
        }
    }
}
```

`O(n²)` всегда. **Преимущество:** минимальное число swap'ов (`O(n)`) — может быть полезно при дорогих swap (large records).

**Unstable** в наивной реализации — swap может «перепрыгнуть» равные элементы.

## Q7. (!) Insertion Sort?

**Insertion Sort** — для каждого элемента находим его место в отсортированной левой части и вставляем.

```java
void insertionSort(int[] arr) {
    for (int i = 1; i < arr.length; i++) {
        int key = arr[i];
        int j = i - 1;
        while (j >= 0 && arr[j] > key) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = key;
    }
}
```

`O(n²)` среднее, **`O(n)` для отсортированного**. Stable, in-place, **adaptive**.

**Когда используется:**
- Малые массивы (< 50 элементов) — быстрее `O(n log n)` из-за меньших констант
- Почти отсортированные данные
- **Внутри TimSort** — для коротких runs
- Online — можем добавлять элементы по одному

## Q8. (!) Merge Sort?

**Divide and Conquer:** делим массив пополам, рекурсивно сортируем, сливаем.

```java
void mergeSort(int[] arr, int left, int right) {
    if (left >= right) return;
    int mid = left + (right - left) / 2;
    mergeSort(arr, left, mid);
    mergeSort(arr, mid + 1, right);
    merge(arr, left, mid, right);
}

void merge(int[] arr, int left, int mid, int right) {
    int[] tmp = new int[right - left + 1];
    int i = left, j = mid + 1, k = 0;
    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) tmp[k++] = arr[i++]; // <= для stability
        else                   tmp[k++] = arr[j++];
    }
    while (i <= mid) tmp[k++] = arr[i++];
    while (j <= right) tmp[k++] = arr[j++];
    System.arraycopy(tmp, 0, arr, left, tmp.length);
}
```

`O(n log n)` всегда. Stable, **не in-place** (`O(n)` доп. памяти).

**Применения:** для linked lists (Quick Sort требует random access), внешняя сортировка (на диске), параллельная сортировка.

## Q9. (!) Quick Sort?

Выбираем pivot, делаем **partition** (меньшие — слева, большие — справа), рекурсивно сортируем половины.

```java
void quickSort(int[] arr, int left, int right) {
    if (left >= right) return;
    int pivotIdx = partition(arr, left, right);
    quickSort(arr, left, pivotIdx - 1);
    quickSort(arr, pivotIdx + 1, right);
}

int partition(int[] arr, int left, int right) {
    int pivot = arr[right]; // последний как pivot
    int i = left - 1;
    for (int j = left; j < right; j++) {
        if (arr[j] <= pivot) {
            i++;
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
        }
    }
    int tmp = arr[i + 1]; arr[i + 1] = arr[right]; arr[right] = tmp;
    return i + 1;
}
```

`O(n log n)` среднее, `O(n²)` худшее (отсортированный массив + плохой выбор pivot). In-place, **unstable**.

В среднем **в 2-3 раза быстрее Merge Sort** благодаря лучшей cache locality и in-place работе.

## Q10. (!) Какие оптимизации Quick Sort?

1. **Random pivot** — защита от worst case на отсортированных данных
2. **Median-of-three** — медиана из first, mid, last как pivot
3. **3-way partition (Dutch flag)** — разделяем на `<`, `=`, `>` — ускоряет на массивах с дубликатами
4. **Switch to Insertion Sort для малых subarrays** — меньше overhead рекурсии
5. **Tail call optimization** — рекурсия только в меньшую половину, итерация в большую — `O(log n)` стек гарантированно
6. **Dual-Pivot Quicksort** — два pivot делят массив на три части, обычно быстрее

```java
// Dual-Pivot (Java Arrays.sort для примитивов):
// Выбираем p1 < p2 → массив на 3 части: [<p1] [p1..p2] [>p2]
```

## Q11. (!) Heap Sort?

Строим max-heap → извлекаем максимум в конец массива.

```java
void heapSort(int[] arr) {
    int n = arr.length;
    // 1. Build max-heap — O(n)
    for (int i = n / 2 - 1; i >= 0; i--) siftDown(arr, n, i);
    // 2. Extract elements — O(n log n)
    for (int i = n - 1; i > 0; i--) {
        int tmp = arr[0]; arr[0] = arr[i]; arr[i] = tmp;
        siftDown(arr, i, 0);
    }
}

void siftDown(int[] arr, int size, int i) {
    while (true) {
        int l = 2*i+1, r = 2*i+2, largest = i;
        if (l < size && arr[l] > arr[largest]) largest = l;
        if (r < size && arr[r] > arr[largest]) largest = r;
        if (largest == i) break;
        int tmp = arr[i]; arr[i] = arr[largest]; arr[largest] = tmp;
        i = largest;
    }
}
```

`O(n log n)` всегда — главное преимущество (Quick Sort может скатиться в `O(n²)`). In-place, **unstable**. На практике медленнее Quick Sort из-за плохой cache locality.

**Используется в Introsort** для гарантий worst-case.

Подробнее — в [Кучи](../data-structures/heaps-interview.md).

## Q12. (!) TimSort — что используется в Java?

**TimSort** (Tim Peters, 2002) — гибрид Merge Sort и Insertion Sort. Используется в:
- Java `Arrays.sort()` для **объектов** (с Java 7+)
- Java `Collections.sort()`, `Stream.sorted()`
- Python `sorted()`, `list.sort()`
- Android, V8 JavaScript

**Идея:** ищем уже отсортированные **runs** (возрастающие или убывающие подпоследовательности), потом сливаем.

**Сложности:**
- Best: `O(n)` (уже отсортирован)
- Avg/Worst: `O(n log n)`
- Space: `O(n)`
- Stable: да

**Преимущества:**
- Adaptive — быстрее на реальных данных (где часто есть отсортированные участки)
- Stable — важно для многоэтапных сортировок
- Гарантия `O(n log n)` worst-case

## Q13. Introsort — что используется в C++?

**Introsort** (Musser, 1997) — гибрид Quick Sort, Heap Sort, Insertion Sort.

- Начинает с **Quick Sort**
- Если глубина рекурсии > `2 · log n` — переключается на **Heap Sort** (защита от worst-case `O(n²)`)
- Для малых subarrays — **Insertion Sort**

Используется в:
- C++ STL `std::sort` (GCC libstdc++)
- .NET `Array.Sort()`

**Сложности:** `O(n log n)` worst-case благодаря Heap Sort fallback.

## Q14. (!) Почему сравнительные сортировки не быстрее O(n log n)?

**Decision tree argument:**
- Любой алгоритм, основанный на сравнениях, можно представить **бинарным деревом решений**
- Каждая возможная перестановка `n` элементов — это лист (всего `n!` листьев)
- Высота дерева = минимальное число сравнений в худшем случае

По формуле Стирлинга: `log(n!) = Θ(n log n)`.

Поэтому **никакая** сортировка сравнениями не может работать быстрее `O(n log n)` в худшем случае.

**Не-сравнительные сортировки** (Counting, Radix, Bucket) могут быть `O(n)`, но требуют ограничений на тип данных.

## Q15. (!) Counting Sort?

Подходит для целых чисел в малом диапазоне. `O(n + k)`, где `k` — диапазон.

```java
void countingSort(int[] arr) {
    int min = Arrays.stream(arr).min().getAsInt();
    int max = Arrays.stream(arr).max().getAsInt();
    int range = max - min + 1;

    int[] count = new int[range];
    for (int x : arr) count[x - min]++;

    // Cumulative count для stability
    for (int i = 1; i < range; i++) count[i] += count[i - 1];

    int[] output = new int[arr.length];
    for (int i = arr.length - 1; i >= 0; i--) {
        output[count[arr[i] - min] - 1] = arr[i];
        count[arr[i] - min]--;
    }
    System.arraycopy(output, 0, arr, 0, arr.length);
}
```

`O(n + k)` время, `O(n + k)` память. Stable. **Только** для целых чисел в ограниченном диапазоне.

**Когда хорош:** k = O(n) (например, оценки 0-100, частоты слов с ограниченной длиной).
**Когда плох:** разреженные большие диапазоны (`int` от 0 до `10⁹`) — съедает терабайты памяти.

## Q16. (!) Radix Sort?

Сортирует числа по цифрам — от младшей к старшей (LSD) или наоборот (MSD). Каждая стадия — Counting Sort по одной цифре.

```java
void radixSort(int[] arr) {
    int max = Arrays.stream(arr).max().getAsInt();
    for (int exp = 1; max / exp > 0; exp *= 10) {
        countingSortByDigit(arr, exp);
    }
}

void countingSortByDigit(int[] arr, int exp) {
    int n = arr.length;
    int[] output = new int[n];
    int[] count = new int[10];

    for (int x : arr) count[(x / exp) % 10]++;
    for (int i = 1; i < 10; i++) count[i] += count[i - 1];

    for (int i = n - 1; i >= 0; i--) {
        int digit = (arr[i] / exp) % 10;
        output[count[digit] - 1] = arr[i];
        count[digit]--;
    }
    System.arraycopy(output, 0, arr, 0, n);
}
```

`O(d · (n + k))` ≈ `O(n)` если число цифр `d` константа. Stable.

**Применения:** сортировка строк фиксированной длины, IPv4 адресов, дат.

## Q17. Bucket Sort?

Распределяем элементы по `k` bucket'ам, сортируем каждый отдельно (обычно Insertion Sort).

```java
void bucketSort(double[] arr) {
    int n = arr.length;
    List<List<Double>> buckets = new ArrayList<>();
    for (int i = 0; i < n; i++) buckets.add(new ArrayList<>());

    for (double x : arr) {
        int idx = (int)(x * n); // assume 0 <= x < 1
        buckets.get(idx).add(x);
    }

    for (List<Double> b : buckets) Collections.sort(b);

    int k = 0;
    for (List<Double> b : buckets)
        for (double x : b) arr[k++] = x;
}
```

`O(n + k)` среднее (если равномерное распределение), `O(n²)` худшее. Stable, если внутренняя сортировка stable.

**Когда хорош:** равномерно распределённые real numbers в `[0, 1)`.

## Q18. (!) Что внутри Arrays.sort()?

В Java:

| Тип | Алгоритм |
|-----|----------|
| `int[]`, `long[]`, ... (примитивы) | **Dual-Pivot Quicksort** |
| `Object[]` | **TimSort** |

**Почему так:**
- Для примитивов **stability не нужна** (числа равны = одинаковы) → быстрый Quick Sort
- Для объектов **stability важна** (multi-key sort) → TimSort

```java
int[] arr = {3, 1, 4, 1, 5};
Arrays.sort(arr); // Dual-Pivot Quicksort

Integer[] objs = {3, 1, 4, 1, 5};
Arrays.sort(objs); // TimSort
```

**Parallel sort** доступен с Java 8: `Arrays.parallelSort(arr)` — использует Fork/Join framework, эффективен для **больших** массивов (>8K элементов).

## Q19. (!) Чем отличается Arrays.sort и Collections.sort?

`Collections.sort(list)` под капотом:
1. `list.toArray()`
2. `Arrays.sort(array)` — **TimSort** (объекты!)
3. `ListIterator.set()` для каждого элемента обратно

`Arrays.sort(int[])` — Dual-Pivot Quicksort.
`Arrays.sort(Integer[])` — TimSort.

С Java 8+ `List.sort(Comparator)` — лучше чем `Collections.sort` (нет лишних копирований).

```java
list.sort(null); // natural ordering (Comparable)
list.sort(Comparator.naturalOrder());
list.sort(Comparator.comparing(User::getName));
```

## Q20. Parallel sort?

`Arrays.parallelSort()` — работает на Fork/Join pool. Использует **divide-and-conquer** с параллельным merge.

```java
int[] arr = new int[1_000_000];
// ... fill ...
Arrays.parallelSort(arr); // на много-ядерных CPU быстрее
```

**Когда выигрывает:**
- Большие массивы (>8K — порог переключения)
- Доступно несколько CPU ядер
- Чтение/запись не bottleneck

**Когда проигрывает:**
- Малые массивы — overhead на распараллеливание больше
- Single-core или сильно загруженный CPU

В `Stream` параллельная сортировка через `parallelStream().sorted()` использует похожий механизм.

## Q21. (!) Dutch National Flag — 3-way partition?

Эдсгер Дейкстра. Сортируем массив с тремя значениями (`0`, `1`, `2`) за `O(n)` `O(1)`.

```java
void sortColors(int[] arr) {
    int low = 0, mid = 0, high = arr.length - 1;
    while (mid <= high) {
        if (arr[mid] == 0) {
            int tmp = arr[low]; arr[low++] = arr[mid]; arr[mid++] = tmp;
        } else if (arr[mid] == 1) {
            mid++;
        } else { // == 2
            int tmp = arr[mid]; arr[mid] = arr[high]; arr[high--] = tmp;
        }
    }
}
```

Применяется в **3-way Quick Sort** для эффективной обработки массивов с дубликатами.

## Q22. Внешняя сортировка (external sort)?

Когда данные **не помещаются в память** — нужно сортировать на диске.

**External merge sort:**
1. Разбиваем файл на куски, помещающиеся в RAM
2. Сортируем каждый кусок in-memory (например, TimSort)
3. Записываем отсортированные куски на диск
4. **Multi-way merge** — открываем все куски, читаем по одному элементу, выбираем минимум через PriorityQueue

```java
// Псевдокод
void externalSort(File input, File output, long memoryLimit) {
    List<File> chunks = splitAndSortChunks(input, memoryLimit);
    mergeChunks(chunks, output);
}
```

`O(n log n)` сравнений + `O(n log n)` I/O. Применяется в Hadoop MapReduce, реляционных БД (`ORDER BY` на больших таблицах).

## Q23. (!) Sort almost-sorted array?

Массив, где каждый элемент сдвинут не более чем на `k` позиций от своего места.

**Min-heap размера `k+1`:** `O(n log k)` — гораздо быстрее `O(n log n)`.

```java
int[] sortKSorted(int[] arr, int k) {
    PriorityQueue<Integer> heap = new PriorityQueue<>();
    for (int i = 0; i <= k && i < arr.length; i++) heap.offer(arr[i]);

    int[] result = new int[arr.length];
    int idx = 0;
    for (int i = k + 1; i < arr.length; i++) {
        result[idx++] = heap.poll();
        heap.offer(arr[i]);
    }
    while (!heap.isEmpty()) result[idx++] = heap.poll();
    return result;
}
```

`O(n log k)` время, `O(k)` память. Insertion Sort даёт `O(n · k)` — хуже для малых `k`, лучше для очень малых.

## Q24. Топологическая сортировка vs обычная?

**Обычная** — сортировка значений по порядку.
**Топологическая** — линейный порядок вершин **DAG**, при котором все зависимости приходят раньше.

Применяется для зависимостей: build systems, course schedules, Spring beans.

Подробнее — в [Графы](../data-structures/graphs-interview.md).

## Q25. (!) Как сортировать кастомным компаратором?

```java
List<User> users = ...;

// Java 8+ — Comparator.comparing
users.sort(Comparator.comparing(User::getName));

// Multiple criteria
users.sort(Comparator.comparing(User::getName)
                     .thenComparingInt(User::getAge)
                     .reversed());

// Compare с null safety
users.sort(Comparator.comparing(User::getName, Comparator.nullsLast(Comparator.naturalOrder())));

// Кастомная логика
users.sort((a, b) -> {
    if (a.isVip() != b.isVip()) return a.isVip() ? -1 : 1;
    return a.getName().compareTo(b.getName());
});
```

## Q26. Стабильно ли Comparator.thenComparing?

**Да** — TimSort (для объектов) stable. `thenComparing` гарантирует: если по первому критерию равны → используем второй; равные по обоим — порядок входа.

```java
users.sort(Comparator.comparing(User::getDepartment)
                     .thenComparing(User::getName));
// Сначала по department, внутри department — по name
```

## Q27. Что такое natural ordering?

**Natural ordering** — порядок, определённый методом `compareTo()` интерфейса `Comparable`. Для встроенных типов:
- `Integer`, `Double` — численный
- `String` — лексикографический
- `LocalDate` — хронологический

```java
class User implements Comparable<User> {
    private String name;

    @Override
    public int compareTo(User other) {
        return this.name.compareTo(other.name);
    }
}

// Используется по умолчанию:
Collections.sort(users); // natural ordering
TreeMap<User, Integer> map = new TreeMap<>(); // ключи по natural ordering
```

Подробнее — в [Java Core](../../programming-languages/java/java-core-interview.md).

## Q28. (!) Когда O(n²) сортировка выиграет у O(n log n)?

1. **Малые массивы** (< ~50 элементов) — Insertion Sort быстрее Merge/Quick из-за меньших констант. Поэтому **TimSort** использует Insertion для коротких runs (< 32-64 элемента).
2. **Почти отсортированные данные** — Insertion Sort `O(n)` на почти отсортированном.
3. **Online сортировка** — добавляем элементы по одному → Insertion идеален.
4. **Memory-constrained environments** — `O(1)` память Insertion vs `O(n)` Merge.

## Q29. (!) Что такое cache locality в контексте сортировки?

CPU читает данные блоками (cache lines, ~64 байта). Алгоритмы с **последовательным** доступом к памяти выигрывают за счёт меньших cache miss'ов.

| Алгоритм | Cache locality |
|----------|----------------|
| Insertion Sort | **Отличная** (последовательный доступ) |
| Merge Sort | Хорошая (последовательно по двум массивам) |
| Quick Sort | **Отличная** (in-place, partition по соседним) |
| Heap Sort | **Плохая** (siftDown прыгает по дереву) |
| Tim Sort | Отличная |

**Heap Sort асимптотически тот же `O(n log n)`, но на практике в 2-3 раза медленнее Quick Sort** именно из-за плохой cache locality.

## Q30. (!) Как защититься от worst-case Quick Sort?

1. **Random pivot** — случайный выбор: вероятность worst case → 0
2. **Median-of-three** — медиана first/mid/last как pivot
3. **Introsort** — переключение на Heap Sort при глубине > `2 log n`
4. **3-way partition** — для массивов с дубликатами
5. **Dual-Pivot** — два pivot делят на 3 части (Java Arrays.sort)

В production реализациях обычно используют комбинацию всех этих техник.

## Q31. Hybrid sorting — почему всегда лучший выбор?

**Hybrid sorting** комбинирует несколько алгоритмов для оптимизации разных сценариев:

- **TimSort:** Merge + Insertion (короткие runs)
- **Introsort:** Quick + Heap (worst-case) + Insertion (малые)
- **Pdqsort (Pattern-Defeating Quicksort):** Quick + Insertion + 3-way + smart pivot

**Преимущества:**
- Лучший случай каждого алгоритма используется
- Гарантия `O(n log n)` worst-case
- Adaptive — быстрее на реальных данных

В современном production **никто не использует «чистый» Quick Sort или Merge Sort** — всегда hybrid.

---

## See also

- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем
- [Алгоритмы поиска](searching-algorithms-interview.md) — Binary Search требует сортировки
- [Кучи](../data-structures/heaps-interview.md) — Heap Sort и PriorityQueue
- [Массивы и строки](../data-structures/arrays-strings-interview.md) — Dutch National Flag
- [Divide and Conquer](../algorithmic-paradigms/divide-and-conquer-interview.md) — Merge Sort, Quick Sort
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — почему сортировка не быстрее O(n log n)
- [Two Pointers](../algorithmic-paradigms/two-pointers-sliding-window-interview.md) — merge two sorted arrays
- [Связные списки](../data-structures/linked-lists-interview.md) — Merge Sort на linked list
- [Java Collections](../../programming-languages/java/java-collections-interview.md) — Arrays.sort vs Collections.sort
- [Java Stream API](../../programming-languages/java/java-stream-interview.md) — sorted() в стримах
- [Графы](../data-structures/graphs-interview.md) — топологическая сортировка

- [[searching-algorithms-interview|Алгоритмы поиска]]
- [[backtracking-interview|Backtracking]]
- [[divide-and-conquer-interview|Divide and Conquer]]
- [[dynamic-programming-interview|Динамическое программирование]]
- [[greedy-algorithms-interview|Жадные алгоритмы (Greedy)]]
- [[recursion-interview|Рекурсия]]
