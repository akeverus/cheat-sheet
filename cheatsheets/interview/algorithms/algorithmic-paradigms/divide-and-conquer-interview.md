---
title: "Вопросы на собеседовании: Divide and Conquer"
description: "Парадигма разделяй-и-властвуй, master theorem, Merge Sort, Quick Sort, Binary Search, fast power, Strassen, closest pair, поиск максимума, FFT, Karatsuba"
tags:
  - interview
  - algorithms
  - divide-and-conquer-interview
aliases:
  - "Divide and conquer interview"
  - "Разделяй и властвуй собеседование"
  - "D&C interview"
  - "Master theorem interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Divide and Conquer`

D&C — фундаментальная парадигма: **разделить → решить рекурсивно → объединить**. На ней построены Merge Sort, Quick Sort, Binary Search, FFT, и многие задачи на массивах. Знают master theorem и могут анализировать сложность через дерево рекурсии.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Divide and Conquer — Baeldung](https://www.baeldung.com/cs/divide-and-conquer-strategy)
- [Master Theorem — Baeldung](https://www.baeldung.com/cs/master-theorem-asymptotic-analysis)
- [Merge Sort — Baeldung](https://www.baeldung.com/java-merge-sort)
- [Quick Sort — Baeldung](https://www.baeldung.com/java-quicksort)
- [Strassen Algorithm — Baeldung](https://www.baeldung.com/cs/strassen-multiplication)
- [FFT Wikipedia](https://en.wikipedia.org/wiki/Fast_Fourier_transform)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Divide and Conquer?](#q1--что-такое-divide-and-conquer)
- [Q2. (!) Три шага D&C?](#q2--три-шага-dc)
- [Q3. (!) Чем D&C отличается от DP?](#q3--чем-dc-отличается-от-dp)
- [Q4. Чем D&C отличается от рекурсии в общем?](#q4-чем-dc-отличается-от-рекурсии-в-общем)

**Анализ сложности**
- [Q5. (!) Master Theorem?](#q5--master-theorem)
- [Q6. (!) Как анализировать через дерево рекурсии?](#q6--как-анализировать-через-дерево-рекурсии)
- [Q7. Когда master theorem не применим?](#q7-когда-master-theorem-не-применим)

**Классические алгоритмы**
- [Q8. (!) Merge Sort как D&C?](#q8--merge-sort-как-dc)
- [Q9. (!) Quick Sort как D&C?](#q9--quick-sort-как-dc)
- [Q10. (!) Binary Search как D&C?](#q10--binary-search-как-dc)
- [Q11. (!) Fast Power — возведение в степень за O(log n)?](#q11--fast-power--возведение-в-степень-за-olog-n)

**Числовые алгоритмы**
- [Q12. Karatsuba — быстрое умножение чисел?](#q12-karatsuba--быстрое-умножение-чисел)
- [Q13. (!) Strassen — умножение матриц?](#q13--strassen--умножение-матриц)
- [Q14. FFT — быстрое преобразование Фурье?](#q14-fft--быстрое-преобразование-фурье)

**Геометрия и поиск**
- [Q15. Closest Pair of Points за O(n log n)?](#q15-closest-pair-of-points-за-on-log-n)
- [Q16. (!) Найти максимум массива через D&C?](#q16--найти-максимум-массива-через-dc)
- [Q17. (!) Inversion count в массиве?](#q17--inversion-count-в-массиве)
- [Q18. Maximum Subarray через D&C?](#q18-maximum-subarray-через-dc)

**Деревья**
- [Q19. (!) Высота дерева через D&C?](#q19--высота-дерева-через-dc)
- [Q20. (!) Diameter of Binary Tree?](#q20--diameter-of-binary-tree)
- [Q21. Same Tree, Symmetric Tree?](#q21-same-tree-symmetric-tree)

**Специальные D&C**
- [Q22. (!) Median of Two Sorted Arrays?](#q22--median-of-two-sorted-arrays)
- [Q23. Skyline Problem?](#q23-skyline-problem)
- [Q24. Count of Smaller Numbers After Self?](#q24-count-of-smaller-numbers-after-self)

**Подводные камни**
- [Q25. (!) Когда D&C проигрывает итеративному подходу?](#q25--когда-dc-проигрывает-итеративному-подходу)
- [Q26. Risk of stack overflow?](#q26-risk-of-stack-overflow)
- [Q27. (!) Когда D&C можно распараллелить?](#q27--когда-dc-можно-распараллелить)

## Q1. (!) Что такое Divide and Conquer?

**Divide and Conquer (D&C)** — парадигма решения задач:

1. **Divide:** разбить задачу на подзадачи меньшего размера
2. **Conquer:** рекурсивно решить подзадачи (если они достаточно малы — решить напрямую)
3. **Combine:** объединить решения подзадач в решение исходной

**Примеры:**
- Merge Sort
- Quick Sort
- Binary Search (упрощённый D&C — только одна подзадача)
- Strassen multiplication
- FFT
- Closest Pair of Points


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Три шага D&C? Частая ошибка в реальном коде.

```mermaid
graph TD
    A["Задача размера n"] --> B["Divide: подзадача n/2"]
    A --> C["Divide: подзадача n/2"]
    B --> D["Conquer: рекурсия"]
    C --> E["Conquer: рекурсия"]
    D --> F["Combine"]
    E --> F
    F --> G["Решение"]
```

**Псевдокод:**

```java
Result solve(Problem p) {
    if (isBaseCase(p)) return solveDirectly(p);

    List<Problem> subproblems = divide(p);
    List<Result> subresults = subproblems.stream()
                                          .map(this::solve)
                                          .toList();
    return combine(subresults);
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Чем D&C отличается от DP? Частая ошибка в реальном коде.

| Критерий | D&C | DP |
|----------|-----|-----|
| Подзадачи перекрываются | **Нет** (или редко) | **Да** |
| Memoization | Не нужно | Обязательно |
| Подход | Top-down рекурсия | Top-down или bottom-up |
| Примеры | Merge Sort, Binary Search | Fibonacci, Knapsack |

**Mерge Sort** — D&C: подзадачи `mergeSort(left)` и `mergeSort(right)` независимы.
**Naive Fibonacci** — D&C, но с перекрытием → нужно DP/мемоизация.

Если переопределить с мемоизацией — D&C превращается в DP.

Подробнее — в [Динамическое программирование](dynamic-programming-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Чем D&C отличается от рекурсии в общем? Частая ошибка в реальном коде.

**Рекурсия** — общий механизм: функция вызывает себя.
**D&C** — частный случай рекурсии с **тремя обязательными шагами** (divide, conquer, combine).

```java
// Рекурсия — НЕ D&C (нет divide на меньшие части)
int factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);
}

// D&C
int sumDC(int[] arr, int lo, int hi) {
    if (lo == hi) return arr[lo];
    int mid = lo + (hi - lo) / 2;
    return sumDC(arr, lo, mid) + sumDC(arr, mid + 1, hi); // divide на две части
}
```

D&C обычно даёт лучшие сложности (`O(log n)` или `O(n log n)`), потому что разбивает задачу на **сравнимые по размеру** подзадачи.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) Master Theorem? Частая ошибка в реальном коде.

Для рекуррентностей вида `T(n) = a · T(n/b) + f(n)` (где `a ≥ 1`, `b > 1`):

Сравниваем `f(n)` с `n^(log_b a)`:

| Случай | Условие | Результат |
|--------|---------|-----------|
| **1** | `f(n) = O(n^(log_b a − ε))` для некоторого ε > 0 | `T(n) = Θ(n^(log_b a))` |
| **2** | `f(n) = Θ(n^(log_b a))` | `T(n) = Θ(n^(log_b a) · log n)` |
| **3** | `f(n) = Ω(n^(log_b a + ε))` и regularity | `T(n) = Θ(f(n))` |

**Примеры:**

| Рекуррентность | Алгоритм | Решение |
|----------------|----------|---------|
| `T(n) = 2T(n/2) + O(n)` | Merge Sort | `Θ(n log n)` (case 2) |
| `T(n) = T(n/2) + O(1)` | Binary Search | `Θ(log n)` (case 2) |
| `T(n) = 7T(n/2) + O(n²)` | Strassen | `Θ(n^log₂7) ≈ Θ(n^2.81)` (case 1) |
| `T(n) = 4T(n/2) + O(n)` | — | `Θ(n²)` (case 1) |
| `T(n) = 2T(n/2) + O(n²)` | — | `Θ(n²)` (case 3) |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Как анализировать через дерево рекурсии? Частая ошибка в реальном коде.

1. Нарисовать дерево вызовов
2. Подсчитать работу на каждом уровне
3. Сложить по всем уровням

**Пример: `T(n) = 2T(n/2) + n` (Merge Sort)**

```
Уровень 0: 1 узел    × n         = n
Уровень 1: 2 узла    × n/2       = n
Уровень 2: 4 узла    × n/4       = n
...
Уровень k: 2^k узлов × n/2^k     = n

Глубина = log n уровней.
Сумма = n × log n = O(n log n).
```

**Пример: `T(n) = T(n/2) + n`**

```
Уровень 0: n
Уровень 1: n/2
Уровень 2: n/4
...
Сумма = n + n/2 + n/4 + ... = O(n)
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Когда master theorem не применим? Частая ошибка в реальном коде.

1. **Подзадачи разного размера:** `T(n) = T(n/3) + T(2n/3) + n` — нужно дерево или Akra-Bazzi
2. **Не полиномиальное `f(n)`:** `T(n) = 2T(n/2) + n log n` — между cases 2 и 3
3. **Отрицательное `f(n)`** или странное
4. **Не строго `n/b`** — округления

В таких случаях — рекурсивное дерево или **substitution method**.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Merge Sort как D&C? Частая ошибка в реальном коде.

```java
void mergeSort(int[] arr, int left, int right) {
    if (left >= right) return; // base case

    int mid = left + (right - left) / 2;

    mergeSort(arr, left, mid);      // divide + conquer left
    mergeSort(arr, mid + 1, right); // divide + conquer right
    merge(arr, left, mid, right);   // combine
}

void merge(int[] arr, int left, int mid, int right) {
    int[] tmp = new int[right - left + 1];
    int i = left, j = mid + 1, k = 0;
    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) tmp[k++] = arr[i++];
        else                   tmp[k++] = arr[j++];
    }
    while (i <= mid)   tmp[k++] = arr[i++];
    while (j <= right) tmp[k++] = arr[j++];
    System.arraycopy(tmp, 0, arr, left, tmp.length);
}
```

**Recurrence:** `T(n) = 2T(n/2) + O(n)` → `O(n log n)`.

**Combine — самая работа.** В `Quick Sort` наоборот: divide дорогой, combine ничего не делает.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) Quick Sort как D&C? Частая ошибка в реальном коде.

```java
void quickSort(int[] arr, int left, int right) {
    if (left >= right) return;

    int pivotIdx = partition(arr, left, right); // divide
    quickSort(arr, left, pivotIdx - 1);          // conquer left
    quickSort(arr, pivotIdx + 1, right);         // conquer right
    // combine — ничего не делаем (in-place)
}
```

**Recurrence (среднее):** `T(n) = 2T(n/2) + O(n)` → `O(n log n)`.
**Худшее:** `T(n) = T(n-1) + O(n)` → `O(n²)`.

Подробнее — в [Sorting](../sorting-searching/sorting-algorithms-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Binary Search как D&C? Частая ошибка в реальном коде.

```java
int binarySearch(int[] arr, int target, int lo, int hi) {
    if (lo > hi) return -1; // base
    int mid = lo + (hi - lo) / 2;
    if (arr[mid] == target) return mid;
    if (arr[mid] < target) return binarySearch(arr, target, mid + 1, hi);
    return binarySearch(arr, target, lo, mid - 1);
}
```

Упрощённый D&C: только **одна** подзадача после divide. Combine — тривиально.

**Recurrence:** `T(n) = T(n/2) + O(1)` → `O(log n)`.

Подробнее — в [Searching](../sorting-searching/searching-algorithms-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) Fast Power — возведение в степень за O(log n)? Частая ошибка в реальном коде.

Идея: `x^n = (x^(n/2))²` при чётном `n`, `x^n = x · x^(n-1)` при нечётном.

```java
double power(double x, int n) {
    if (n == 0) return 1;
    if (n < 0) return 1.0 / power(x, -n);

    if (n % 2 == 0) {
        double half = power(x, n / 2);
        return half * half;
    }
    return x * power(x, n - 1);
}

// Итеративный вариант (binary exponentiation)
double powerIter(double x, int n) {
    long exp = n; // long чтобы обработать Integer.MIN_VALUE
    if (exp < 0) { x = 1.0 / x; exp = -exp; }
    double result = 1;
    while (exp > 0) {
        if ((exp & 1) == 1) result *= x;
        x *= x;
        exp >>= 1;
    }
    return result;
}
```

`O(log n)` вместо наивного `O(n)`.

**Применения:** криптография (RSA), вычисление больших степеней матриц (быстрый Фибоначчи через матричное умножение за `O(log n)`).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Karatsuba — быстрое умножение чисел? Частая ошибка в реальном коде.

Наивное умножение `n`-цифровых чисел — `O(n²)`. **Karatsuba (1960):** `O(n^log₂3) ≈ O(n^1.585)`.

```
x = x1 · 10^(n/2) + x0
y = y1 · 10^(n/2) + y0

x · y = x1·y1 · 10^n + (x1·y0 + x0·y1) · 10^(n/2) + x0·y0

Трюк: считаем три произведения вместо четырёх:
  z2 = x1 · y1
  z0 = x0 · y0
  z1 = (x1 + x0)(y1 + y0) - z2 - z0
```

**Recurrence:** `T(n) = 3T(n/2) + O(n)` → `Θ(n^log₂3)`.

Применяется в **BigInteger.multiply()** для очень больших чисел (`> 80` цифр).

Toom-Cook — обобщение, ещё быстрее. **Schönhage-Strassen** — `O(n log n log log n)` через FFT.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. (!) Strassen — умножение матриц? Частая ошибка в реальном коде.

Наивное умножение матриц `n×n` — `O(n³)`. **Strassen (1969):** `O(n^log₂7) ≈ O(n^2.81)`.

Идея: каждая матрица делится на 4 подматрицы `n/2 × n/2`. Strassen вычисляет результат через **7** перемножений (вместо 8), используя умные комбинации:

```
M1 = (A11 + A22)(B11 + B22)
M2 = (A21 + A22)·B11
M3 = A11·(B12 - B22)
M4 = A22·(B21 - B11)
M5 = (A11 + A12)·B22
M6 = (A21 - A11)(B11 + B12)
M7 = (A12 - A22)(B21 + B22)

C11 = M1 + M4 - M5 + M7
C12 = M3 + M5
C21 = M2 + M4
C22 = M1 - M2 + M3 + M6
```

**Recurrence:** `T(n) = 7T(n/2) + O(n²)` → `Θ(n^log₂7)`.

На практике выигрывает только для **очень больших** матриц (`n > 100-1000`) из-за больших констант.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. FFT — быстрое преобразование Фурье? Частая ошибка в реальном коде.

**Fast Fourier Transform** — D&C алгоритм для дискретного преобразования Фурье за `O(n log n)` (вместо `O(n²)`).

Идея Cooley-Tukey: разделить вход на чётные и нечётные индексы, рекурсивно вычислить DFT каждой половины, объединить через complex roots of unity.

**Применения:**
- Сигналы (audio/image processing)
- Полиномиальное умножение за `O(n log n)`
- Алгоритм Schönhage-Strassen для умножения чисел
- JPEG compression

Реализация в Java — Apache Commons Math `FastFourierTransformer`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Closest Pair of Points за O(n log n)? Частая ошибка в реальном коде.

Найти ближайшую пару точек в плоскости. Brute force — `O(n²)`. D&C — `O(n log n)`.

**Алгоритм:**
1. Сортировать точки по `x`
2. Разделить на две половины линией `x = mid`
3. Рекурсивно найти ближайшие пары в каждой половине → `d_left`, `d_right`
4. `d = min(d_left, d_right)`
5. **Merge step:** проверить пары точек в полосе шириной `2d` вокруг линии — каждая точка проверяет максимум 7 соседей по `y`

**Recurrence:** `T(n) = 2T(n/2) + O(n)` → `O(n log n)`.

Применяется в computational geometry, mesh generation.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. (!) Найти максимум массива через D&C? Частая ошибка в реальном коде.

```java
int findMax(int[] arr, int lo, int hi) {
    if (lo == hi) return arr[lo];
    int mid = lo + (hi - lo) / 2;
    int leftMax = findMax(arr, lo, mid);
    int rightMax = findMax(arr, mid + 1, hi);
    return Math.max(leftMax, rightMax);
}
```

`O(n)` — то же, что итеративно. Полезно как **учебный пример** D&C, и для **параллелизации** на больших массивах (`Fork/Join` framework).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) Inversion count в массиве? Частая ошибка в реальном коде.

**Inversion** — пара `(i, j)`, где `i < j` и `arr[i] > arr[j]`. Количество отражает «несортированность».

```java
long countInversions(int[] arr) {
    return mergeAndCount(arr, 0, arr.length - 1);
}

long mergeAndCount(int[] arr, int lo, int hi) {
    if (lo >= hi) return 0;
    int mid = lo + (hi - lo) / 2;
    long count = mergeAndCount(arr, lo, mid)
               + mergeAndCount(arr, mid + 1, hi)
               + merge(arr, lo, mid, hi);
    return count;
}

long merge(int[] arr, int lo, int mid, int hi) {
    int[] tmp = new int[hi - lo + 1];
    int i = lo, j = mid + 1, k = 0;
    long count = 0;
    while (i <= mid && j <= hi) {
        if (arr[i] <= arr[j]) tmp[k++] = arr[i++];
        else {
            tmp[k++] = arr[j++];
            count += (mid - i + 1); // все оставшиеся слева — инверсии
        }
    }
    while (i <= mid) tmp[k++] = arr[i++];
    while (j <= hi)  tmp[k++] = arr[j++];
    System.arraycopy(tmp, 0, arr, lo, tmp.length);
    return count;
}
```

`O(n log n)`. Brute force — `O(n²)`. Применяется в коллаборативной фильтрации, similarity ranking.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Maximum Subarray через D&C? Частая ошибка в реальном коде.

```java
int maxSubArray(int[] arr) {
    return helper(arr, 0, arr.length - 1);
}

int helper(int[] arr, int lo, int hi) {
    if (lo == hi) return arr[lo];
    int mid = lo + (hi - lo) / 2;
    int leftMax = helper(arr, lo, mid);
    int rightMax = helper(arr, mid + 1, hi);
    int crossMax = maxCrossing(arr, lo, mid, hi);
    return Math.max(crossMax, Math.max(leftMax, rightMax));
}

int maxCrossing(int[] arr, int lo, int mid, int hi) {
    int leftSum = Integer.MIN_VALUE, sum = 0;
    for (int i = mid; i >= lo; i--) {
        sum += arr[i];
        leftSum = Math.max(leftSum, sum);
    }
    int rightSum = Integer.MIN_VALUE; sum = 0;
    for (int i = mid + 1; i <= hi; i++) {
        sum += arr[i];
        rightSum = Math.max(rightSum, sum);
    }
    return leftSum + rightSum;
}
```

`O(n log n)`. **Kadane** даёт `O(n)` и проще — D&C тут учебный.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. (!) Высота дерева через D&C? Частая ошибка в реальном коде.

```java
int height(TreeNode root) {
    if (root == null) return 0;
    int leftHeight = height(root.left);
    int rightHeight = height(root.right);
    return 1 + Math.max(leftHeight, rightHeight);
}
```

Чистый D&C: divide на левое и правое поддеревья, conquer рекурсивно, combine через max + 1.

`O(n)`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Diameter of Binary Tree? Частая ошибка в реальном коде.

```java
class Solution {
    int diameter = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        depth(root);
        return diameter;
    }

    int depth(TreeNode node) {
        if (node == null) return 0;
        int left = depth(node.left);
        int right = depth(node.right);
        diameter = Math.max(diameter, left + right);
        return 1 + Math.max(left, right);
    }
}
```

`O(n)`. На каждом узле комбинируем глубины поддеревьев. Подробнее — в [Деревья](../data-structures/trees-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. Same Tree, Symmetric Tree? Частая ошибка в реальном коде.

```java
boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    return p.val == q.val
        && isSameTree(p.left, q.left)
        && isSameTree(p.right, q.right);
}

boolean isSymmetric(TreeNode root) {
    return root == null || isMirror(root.left, root.right);
}

boolean isMirror(TreeNode a, TreeNode b) {
    if (a == null && b == null) return true;
    if (a == null || b == null) return false;
    return a.val == b.val
        && isMirror(a.left, b.right)
        && isMirror(a.right, b.left);
}
```

Классические D&C на деревьях. `O(n)` обе.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. (!) Median of Two Sorted Arrays? Частая ошибка в реальном коде.

`O(log(min(m, n)))` через бинарный поиск + D&C логика.

Подробнее — в [Searching](../sorting-searching/searching-algorithms-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. Skyline Problem? Частая ошибка в реальном коде.

Дан набор зданий `[left, right, height]`. Построить силуэт.

**D&C решение:** делим здания пополам, рекурсивно строим силуэты, **сливаем** два силуэта в один (как `merge` в Merge Sort).

`O(n log n)`. Альтернатива через PriorityQueue + sweep line.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. Count of Smaller Numbers After Self? Частая ошибка в реальном коде.

Для каждого `arr[i]` — сколько элементов справа меньше него. `O(n²)` brute force, `O(n log n)` через D&C (modified Merge Sort, как inversion count).

```java
int[] countSmaller(int[] nums) {
    int n = nums.length;
    int[] count = new int[n];
    int[] indices = new int[n];
    for (int i = 0; i < n; i++) indices[i] = i;

    mergeSort(nums, indices, count, 0, n - 1);
    return count;
}

void mergeSort(int[] nums, int[] indices, int[] count, int lo, int hi) {
    if (lo >= hi) return;
    int mid = lo + (hi - lo) / 2;
    mergeSort(nums, indices, count, lo, mid);
    mergeSort(nums, indices, count, mid + 1, hi);
    merge(nums, indices, count, lo, mid, hi);
}
// merge увеличивает count[indices[i]] на число элементов справа,
// которые меньше nums[indices[i]]
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. (!) Когда D&C проигрывает итеративному подходу? Частая ошибка в реальном коде.

1. **Простые задачи** — overhead рекурсии хуже, чем простой цикл (`max array`)
2. **Глубокие рекурсии** — risk of StackOverflow в Java (нет TCO)
3. **Тривиальный combine** — преимущество D&C в том, что combine эффективен; иначе D&C может быть хуже
4. **Cache-unfriendly** — рекурсия может прыгать по памяти

```java
// Итеративно — быстрее
int sumIter(int[] arr) {
    int sum = 0;
    for (int x : arr) sum += x;
    return sum;
}

// D&C — медленнее (overhead вызовов)
int sumDC(int[] arr, int lo, int hi) {
    if (lo == hi) return arr[lo];
    int mid = lo + (hi - lo) / 2;
    return sumDC(arr, lo, mid) + sumDC(arr, mid + 1, hi);
}
```

D&C оправдан для **параллелизации** или когда даёт лучшую асимптотику.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. Risk of stack overflow? Частая ошибка в реальном коде.

JVM стек ограничен (~512KB). Глубокая рекурсия → `StackOverflowError`.

**Quick Sort** при плохом pivot — глубина `O(n)`, может упасть на массиве `10⁵-10⁶`.

**Защита:**
1. **Tail call optimization (TCO)** — JVM не поддерживает!
2. **Рекурсия только в меньшую часть, итерация в большую** — гарантирует `O(log n)` стек:

```java
void quickSortIter(int[] arr, int lo, int hi) {
    while (lo < hi) {
        int p = partition(arr, lo, hi);
        if (p - lo < hi - p) {
            quickSortIter(arr, lo, p - 1); // меньшая
            lo = p + 1;
        } else {
            quickSortIter(arr, p + 1, hi);
            hi = p - 1;
        }
    }
}
```

3. **Увеличить стек:** `-Xss4m` (4MB вместо 512K)
4. **Итеративный D&C через explicit stack**


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. (!) Когда D&C можно распараллелить? Частая ошибка в реальном коде.

D&C **естественно** параллелизуется, потому что подзадачи **независимы**.

```java
// Java Fork/Join
class MergeSortTask extends RecursiveAction {
    int[] arr;
    int lo, hi;

    @Override
    protected void compute() {
        if (hi - lo < THRESHOLD) {
            sequentialSort(arr, lo, hi);
        } else {
            int mid = lo + (hi - lo) / 2;
            MergeSortTask left = new MergeSortTask(arr, lo, mid);
            MergeSortTask right = new MergeSortTask(arr, mid + 1, hi);
            invokeAll(left, right); // параллельно
            merge(arr, lo, mid, hi);
        }
    }
}
```

`Arrays.parallelSort()` использует именно такой подход.

**Хорошо параллелизуются:**
- Merge Sort, Quick Sort
- Matrix multiplication
- Reduce-операции (sum, max, count)

**Плохо:**
- Sequential combine (Skyline merge)
- Когда подзадачи делят общий ресурс

Подробнее — в [Java Concurrency](../../programming-languages/java/java-concurrency-interview.md).

---

## See also

- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем
- [Рекурсия](recursion-interview.md) — D&C — частный случай
- [Динамическое программирование](dynamic-programming-interview.md) — D&C + memoization
- [Greedy](greedy-algorithms-interview.md) — другая парадигма
- [Sorting](../sorting-searching/sorting-algorithms-interview.md) — Merge Sort, Quick Sort
- [Searching](../sorting-searching/searching-algorithms-interview.md) — Binary Search
- [Деревья](../data-structures/trees-interview.md) — естественный D&C
- [Массивы и строки](../data-structures/arrays-strings-interview.md) — Inversion count, Maximum subarray
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — Master theorem
- [Кучи](../data-structures/heaps-interview.md) — Heap Sort, build-heap
- [Java Concurrency](../../programming-languages/java/java-concurrency-interview.md) — Fork/Join framework


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Backtracking](backtracking-interview.md) Частая ошибка в реальном коде.
- [Динамическое программирование](dynamic-programming-interview.md)
- [Жадные алгоритмы (Greedy)](greedy-algorithms-interview.md)
- [Рекурсия](recursion-interview.md)
- [Two Pointers и Sliding Window](two-pointers-sliding-window-interview.md)
- [Алгоритмы (обзор)](../algorithms-interview.md)
- [Шпаргалка: Разделяй и властвуй (Divide and Conquer)](../../../algorithms/algorithmic-paradigms/divide-and-conquer.md) — теория
