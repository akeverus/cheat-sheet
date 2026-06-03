---
title: "Вопросы на собеседовании: Массивы и строки"
description: "Алгоритмы и приёмы работы с массивами и строками: реверс, циклический сдвиг, two-pointers, Kadane, dutch national flag, KMP, Rabin-Karp, immutable String и char[] в Java"
tags:
  - interview
  - algorithms
  - arrays-strings-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Массивы и строки"
  - "Arrays and strings interview"
  - "Массивы и строки собеседование"
prerequisites: []
next: []
updated: "2026-05-19"
---
# Вопросы на собеседовании: `Массивы и строки`

Массивы и строки — самые частые задачи на алгоритмических интервью. Большинство классических задач (`Two Sum`, `Reverse String`, `Maximum Subarray`, `Longest Substring Without Repeating`) — про правильный выбор техник: **two pointers**, **prefix sums**, **sliding window**, **hash maps**.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Arrays Tutorial — Oracle](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/arrays.html)
- [Common Java Array Algorithms — Baeldung](https://www.baeldung.com/java-common-array-operations)
- [String in Java — Baeldung](https://www.baeldung.com/java-string)
- [Reverse a String in Java — Baeldung](https://www.baeldung.com/java-reverse-string)
- [Kadane's Algorithm Wikipedia](https://en.wikipedia.org/wiki/Maximum_subarray_problem)
- [Rabin-Karp Algorithm — Baeldung](https://www.baeldung.com/cs/rabin-karp-algorithm)
- [KMP Algorithm — Baeldung](https://www.baeldung.com/cs/knuth-morris-pratt)
- [LeetCode Top Interview Questions](https://leetcode.com/explore/interview/card/top-interview-questions-easy/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые операции с массивами**
- [Q1. (!) Какие отличия между Array и ArrayList?](#q1--какие-отличия-между-array-и-arraylist)
- [Q2. Как реверсировать массив?](#q2-как-реверсировать-массив)
- [Q3. (!) Как реализовать циклический сдвиг массива?](#q3--как-реализовать-циклический-сдвиг-массива)
- [Q4. Как удалить дубликаты из отсортированного массива in-place?](#q4-как-удалить-дубликаты-из-отсортированного-массива-in-place)
- [Q5. (!) Как реализовать слияние двух отсортированных массивов?](#q5--как-реализовать-слияние-двух-отсортированных-массивов)

**Поиск и подсчёт**
- [Q6. (!) Как найти максимальную сумму подмассива (алгоритм Кадана)?](#q6--как-найти-максимальную-сумму-подмассива-алгоритм-кадана)
- [Q7. (!) Two Sum — поиск пары с заданной суммой?](#q7--two-sum--поиск-пары-с-заданной-суммой)
- [Q8. Three Sum — все тройки с нулевой суммой?](#q8-three-sum--все-тройки-с-нулевой-суммой)
- [Q9. Поиск элемента, встречающегося один раз?](#q9-поиск-элемента-встречающегося-один-раз)
- [Q10. (!) Поиск элемента большинства (>n/2)?](#q10--поиск-элемента-большинства-n2)
- [Q11. Найти недостающее число в массиве 0..n?](#q11-найти-недостающее-число-в-массиве-0n)

**Prefix sums и индексы**
- [Q12. (!) Что такое prefix sum?](#q12--что-такое-prefix-sum)
- [Q13. (!) Подсчёт подмассивов с заданной суммой?](#q13--подсчёт-подмассивов-с-заданной-суммой)
- [Q14. Найти equilibrium index?](#q14-найти-equilibrium-index)

**Двумерные массивы**
- [Q15. (!) Как повернуть матрицу на 90 градусов in-place?](#q15--как-повернуть-матрицу-на-90-градусов-in-place)
- [Q16. Spiral order — обход матрицы по спирали?](#q16-spiral-order--обход-матрицы-по-спирали)
- [Q17. Set zeroes — обнуление строк и столбцов?](#q17-set-zeroes--обнуление-строк-и-столбцов)

**Сортировка и перестановки**
- [Q18. (!) Dutch National Flag — сортировка трёх значений за один проход?](#q18--dutch-national-flag--сортировка-трёх-значений-за-один-проход)
- [Q19. Move Zeroes — переместить нули в конец, сохранив порядок ненулевых?](#q19-move-zeroes--переместить-нули-в-конец-сохранив-порядок-ненулевых)
- [Q20. Next Permutation — следующая лексикографическая перестановка?](#q20-next-permutation--следующая-лексикографическая-перестановка)

**Работа со строками**
- [Q21. (!) String — алгоритм реверса строки?](#q21--string--алгоритм-реверса-строки)
- [Q22. (!) Проверка палиндрома?](#q22--проверка-палиндрома)
- [Q23. (!) Проверка анаграммы?](#q23--проверка-анаграммы)
- [Q24. Группировка анаграмм?](#q24-группировка-анаграмм)
- [Q25. (!) Самая длинная подстрока без повторов?](#q25--самая-длинная-подстрока-без-повторов)
- [Q26. Самый длинный палиндром в строке?](#q26-самый-длинный-палиндром-в-строке)
- [Q27. (!) Поиск подстроки — алгоритм KMP?](#q27--поиск-подстроки--алгоритм-kmp)
- [Q28. (!) Алгоритм Рабина-Карпа?](#q28--алгоритм-рабина-карпа)
- [Q29. Сравнение двух строк с учётом backspace?](#q29-сравнение-двух-строк-с-учётом-backspace)

**Числа и битовые операции**
- [Q30. (!) Поменять два числа без временной переменной?](#q30--поменять-два-числа-без-временной-переменной)
- [Q31. Перевод числа в произвольную систему счисления?](#q31-перевод-числа-в-произвольную-систему-счисления)
- [Q32. Проверка степени двойки?](#q32-проверка-степени-двойки)

**Типичные ошибки и Java-специфика**
- [Q33. (!) Почему == для строк работает не как ожидается?](#q33--почему--для-строк-работает-не-как-ожидается)
- [Q34. Когда использовать char[] вместо String для алгоритмов?](#q34-когда-использовать-char-вместо-string-для-алгоритмов)
- [Q35. (!) StringBuilder vs String конкатенация — где разница?](#q35--stringbuilder-vs-string-конкатенация--где-разница)
- [Q36. Как избежать ArrayIndexOutOfBoundsException в two-pointers?](#q36-как-избежать-arrayindexoutofboundsexception-в-two-pointers)

## Q1. (!) Какие отличия между Array и ArrayList?

Коротко: `Array` — фиксированный размер и хранение примитивов напрямую; `ArrayList` — динамический размер и хранение только объектов (с боксингом примитивов). Доступ по индексу у обоих `O(1)`, потому что `ArrayList` внутри — тот же массив. Различия — в гибкости, расходе памяти и работе с generics.

| Характеристика | Array | ArrayList |
|---------------|-------|-----------|
| Размер | Фиксированный | Динамический (1.5x при росте) |
| Тип | Любые (включая примитивы `int[]`) | Только объекты (`Integer`, не `int`) |
| Доступ | `arr[i]` — `O(1)` | `list.get(i)` — `O(1)` |
| Вставка в конец | — | `O(1)` амортиз. |
| Память | Меньше (нет overhead) | Больше (объекты + capacity) |
| Generics | Нет (массивы reified) | Да (`ArrayList<T>` erasure) |

```java
// Массив примитивов — компактно, быстро
int[] arr = new int[1000];

// ArrayList — гибче, но boxing для примитивов
List<Integer> list = new ArrayList<>(1000); // initial capacity!

// Конвертация
Integer[] boxed = list.toArray(new Integer[0]);
List<Integer> back = Arrays.asList(boxed); // фиксированный размер!
```

**Подводный камень:** `Arrays.asList(int[])` вернёт `List<int[]>` размером 1, а не `List<Integer>`. Для примитивов — `Arrays.stream(arr).boxed().toList()`.


## Q2. Как реверсировать массив?

Эталонный приём — **two pointers** in-place: два индекса идут навстречу из концов и меняют элементы местами, пока не встретятся. Это `O(n)` по времени и `O(1)` по памяти, без аллокаций. На собеседовании ожидают именно этот вариант — он показывает владение указателями.

```java
// In-place через two pointers — O(n) время, O(1) память
void reverse(int[] arr) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int tmp = arr[left];
        arr[left++] = arr[right];
        arr[right--] = tmp;
    }
}

// Через стандартную библиотеку для объектов
Collections.reverse(list);

// Reverse Stream
int[] reversed = IntStream.rangeClosed(1, arr.length)
                          .map(i -> arr[arr.length - i])
                          .toArray();
```

**Подводный камень:** для примитивов в стандартной библиотеке нет `Arrays.reverse()`. Либо пишут цикл вручную (как выше), либо боксят в `List` и зовут `Collections.reverse(Arrays.asList(boxedArr))`. Stream-вариант создаёт новый массив — подходит, когда исходный нельзя мутировать.


## Q3. (!) Как реализовать циклический сдвиг массива?

Поворот массива на `k` позиций вправо оптимально делается **тройным реверсом** за `O(n)` времени и `O(1)` памяти. Наивный сдвиг по одному элементу `k` раз стоит `O(n·k)` — каждый из `k` сдвигов двигает все `n` элементов.

**Почему три реверса работают:** реверс всего массива переворачивает порядок целиком, а два локальных реверса возвращают правильный порядок внутри каждой из двух частей. Перед началом обязательно нормализуют `k = k % n` — иначе при `k >= n` границы реверсов выйдут за массив.

```java
// Метод тройного реверса — O(n) время, O(1) память
void rotate(int[] arr, int k) {
    int n = arr.length;
    k = k % n; // нормализуем k

    reverse(arr, 0, n - 1);       // [1,2,3,4,5] → [5,4,3,2,1]
    reverse(arr, 0, k - 1);       // → [4,5,3,2,1] для k=2
    reverse(arr, k, n - 1);       // → [4,5,1,2,3]
}

void reverse(int[] arr, int left, int right) {
    while (left < right) {
        int tmp = arr[left];
        arr[left++] = arr[right];
        arr[right--] = tmp;
    }
}
```

**Альтернативы:**
- Скопировать в новый массив со сдвигом — `O(n)` время и память; проще, но теряем in-place.
- Циклический алгоритм со счётчиком (cyclic replacements) — `O(n)` время, `O(1)` память, но заметно сложнее в реализации.


## Q4. Как удалить дубликаты из отсортированного массива in-place?

Ключевой факт: в **отсортированном** массиве дубликаты идут подряд, поэтому хватает одного прохода two pointers без хеш-структуры. `slow` держит границу уникальной части, `fast` сканирует массив; когда `fast` находит новое значение, его дописывают сразу за `slow`. Метод возвращает длину уникальной части `k` — сам массив в Java не усекается.

```java
// Two pointers: slow = граница уникальных, fast = идёт вперёд
int removeDuplicates(int[] arr) {
    if (arr.length == 0) return 0;

    int slow = 0;
    for (int fast = 1; fast < arr.length; fast++) {
        if (arr[fast] != arr[slow]) {
            arr[++slow] = arr[fast];
        }
    }
    return slow + 1; // длина уникальной части
}
```

После работы алгоритма первые `k` элементов содержат уникальные значения, остальное — неиспользуемый «хвост». Время `O(n)`, память `O(1)`.

**Граничный случай:** пустой массив нужно обработать отдельно (`return 0`), иначе обращение к `arr[slow]` упадёт.

Для **неотсортированного** массива этот приём не работает (равные значения разбросаны) — там нужен `HashSet` за `O(n)` памяти.


## Q5. (!) Как реализовать слияние двух отсортированных массивов?

Два индекса идут по массивам и на каждом шаге выбирают меньший элемент — `O(n+m)` за один проход. Это работает, потому что входы уже отсортированы (конкатенация + `Arrays.sort` отбросила бы это и стоила бы `O((n+m)·log(n+m))`).

Главный приём для **in-place** слияния в первый массив (LeetCode 88) — идти **с конца**: заполняем свободный хвост от наибольших элементов к меньшим, поэтому запись никогда не затирает ещё не прочитанные значения. Если бы шли с начала, первый же `a[0]=...` перезаписал бы данные, которые ещё нужны.

```java
// Out-of-place — O(n+m) время и память
int[] mergeSorted(int[] a, int[] b) {
    int[] result = new int[a.length + b.length];
    int i = 0, j = 0, k = 0;

    while (i < a.length && j < b.length) {
        if (a[i] <= b[j]) result[k++] = a[i++];
        else               result[k++] = b[j++];
    }
    while (i < a.length) result[k++] = a[i++];
    while (j < b.length) result[k++] = b[j++];

    return result;
}

// In-place в первый массив, если в нём есть место в конце (LeetCode 88)
// Идём с конца — чтобы не затирать ещё не обработанные элементы
void mergeInPlace(int[] a, int m, int[] b, int n) {
    int i = m - 1, j = n - 1, k = m + n - 1;
    while (i >= 0 && j >= 0) {
        a[k--] = (a[i] > b[j]) ? a[i--] : b[j--];
    }
    while (j >= 0) a[k--] = b[j--];
}
```

**Сценарий применения:** этот же паттерн — ядро `Merge Sort`. Для слияния `k` массивов берут `PriorityQueue` (min-heap) за `O(N log k)`, где `N` — суммарное число элементов; наивное попарное слияние медленнее, потому что перечитывает данные.


## Q6. (!) Как найти максимальную сумму подмассива (алгоритм Кадана)?

**Kadane's algorithm** — один проход за `O(n)`, `O(1)` памяти, вместо квадратичного перебора всех пар границ. Идея: на каждом индексе решаем — выгоднее продолжить текущий подмассив (`currentSum + arr[i]`) или начать новый с `arr[i]`. Это классический пример DP: состояние — «лучшая сумма подмассива, оканчивающегося в `i`», а `maxSum` хранит глобальный максимум.

```java
int maxSubarraySum(int[] arr) {
    int currentSum = arr[0];
    int maxSum = arr[0];

    for (int i = 1; i < arr.length; i++) {
        currentSum = Math.max(arr[i], currentSum + arr[i]);
        maxSum = Math.max(maxSum, currentSum);
    }
    return maxSum;
}

// Расширенная версия: возвращает индексы подмассива
int[] maxSubarrayWithIndices(int[] arr) {
    int currentSum = arr[0], maxSum = arr[0];
    int start = 0, end = 0, tempStart = 0;

    for (int i = 1; i < arr.length; i++) {
        if (arr[i] > currentSum + arr[i]) {
            currentSum = arr[i];
            tempStart = i;
        } else {
            currentSum += arr[i];
        }
        if (currentSum > maxSum) {
            maxSum = currentSum;
            start = tempStart;
            end = i;
        }
    }
    return new int[]{maxSum, start, end};
}
```

Пример: для `[-2, 1, -3, 4, -1, 2, 1, -5, 4]` ответ `6` (подмассив `[4, -1, 2, 1]`).

**Граничные случаи:**
- Все числа отрицательные — базовая версия (непустой подмассив) вернёт максимальный, то есть наименее отрицательный, элемент (для `[-3, -1, -2]` это `-1`).
- Если разрешён пустой подмассив с суммой `0` — нужна корректировка `currentSum = Math.max(0, ...)`, иначе ответ не учтёт «ничего не брать».


## Q7. (!) Two Sum — поиск пары с заданной суммой?

**Вариант 1 — отсортированный массив (two pointers):** `O(n)` время, `O(1)` память.

```java
int[] twoSumSorted(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int sum = arr[left] + arr[right];
        if (sum == target) return new int[]{left, right};
        if (sum < target) left++;
        else              right--;
    }
    return new int[]{-1, -1};
}
```

Two pointers корректны **только на отсортированном** массиве: сдвиг границы по знаку суммы опирается на монотонность. Для произвольного массива нужен другой приём.

**Вариант 2 — неотсортированный массив (HashMap):** `O(n)` время и память. За один проход для каждого `arr[i]` ищем уже виденный `complement = target - arr[i]`; проверку делают **до** вставки текущего элемента, поэтому хватает одного прохода.

```java
int[] twoSum(int[] arr, int target) {
    Map<Integer, Integer> seen = new HashMap<>();
    for (int i = 0; i < arr.length; i++) {
        int complement = target - arr[i];
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        seen.put(arr[i], i);
    }
    return new int[]{-1, -1};
}
```

**Компромисс:** если массив разрешено сортировать — sort + two pointers (`O(n log n)`, `O(1)` доп. памяти). Но при сортировке теряются исходные индексы, а в LeetCode Two Sum нужно вернуть именно их — поэтому там обычно выбирают HashMap-вариант.


## Q8. Three Sum — все тройки с нулевой суммой?

Идея: отсортировать массив, затем для каждого `i` зафиксировать `arr[i]` и решать `Two Sum` на оставшейся части через two pointers. Итог — `O(n²)` после сортировки `O(n log n)`. **Сортировка обязательна** не косметически: именно она даёт монотонность для движения `left`/`right` и позволяет дёшево пропускать дубликаты.

```java
List<List<Integer>> threeSum(int[] arr) {
    Arrays.sort(arr); // O(n log n)
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < arr.length - 2; i++) {
        if (i > 0 && arr[i] == arr[i - 1]) continue; // пропускаем дубликаты i
        int left = i + 1, right = arr.length - 1;

        while (left < right) {
            int sum = arr[i] + arr[left] + arr[right];
            if (sum == 0) {
                result.add(List.of(arr[i], arr[left], arr[right]));
                while (left < right && arr[left] == arr[left + 1]) left++;
                while (left < right && arr[right] == arr[right - 1]) right--;
                left++; right--;
            } else if (sum < 0) {
                left++;
            } else {
                right--;
            }
        }
    }
    return result;
}
```

Сложность `O(n²)`. **Подводный камень:** скип дубликатов на трёх уровнях (`i`, `left`, `right`) — критичная деталь. Без него алгоритм генерирует одинаковые тройки; убирать их потом через `Set<List<Integer>>` можно, но это лишняя память и работа, а не замена скипу. Внешний цикл идёт до `n-2`, чтобы хватило места на тройку.


## Q9. Поиск элемента, встречающегося один раз?

В массиве каждый элемент встречается **дважды**, кроме одного. Найти его за `O(n)` без доп. памяти.

```java
// Через XOR: a ^ a = 0, a ^ 0 = a
int singleNumber(int[] arr) {
    int result = 0;
    for (int x : arr) result ^= x;
    return result;
}
```

**Почему это работает:** XOR коммутативен и ассоциативен, поэтому порядок не важен; все парные числа взаимно сокращаются до `0` (`a ^ a = 0`), а оставшийся `0 ^ x = x` даёт одиночку. Память `O(1)`, время `O(n)` — сортировка для этого не нужна.

**Важная оговорка:** приём работает, только когда непарный элемент ровно один, а остальные строго по два. Если каждый встречается **трижды**, кроме одного, простой XOR ломается (три одинаковых XOR'ятся в само значение, а не в `0`) — там нужен подсчёт битов с состояниями `ones`/`twos`.


## Q10. (!) Поиск элемента большинства (>n/2)?

**Алгоритм Бойера-Мура (Boyer-Moore Voting):** `O(n)` время, `O(1)` память.

```java
int majorityElement(int[] arr) {
    int candidate = 0, count = 0;
    for (int x : arr) {
        if (count == 0) candidate = x;
        count += (x == candidate) ? 1 : -1;
    }
    return candidate; // гарантия — если majority существует
}
```

**Идея:** алгоритм ведёт «голосование». Каждый элемент-кандидат набирает голос за себя и теряет голос на любом отличном элементе; парные голоса за разных кандидатов гасят друг друга. Если элемент-большинство существует (>n/2 голосов), он переживёт все взаимные сокращения и останется кандидатом. Память `O(1)`, потому что хранятся всего две переменные, а не карта частот.

**Подводный камень:** алгоритм верен только при гарантированном существовании majority. Если такого элемента нет, кандидат окажется случайным. Поэтому в общем случае делают **второй проход** и проверяют, что кандидат действительно встречается >n/2 раз.


## Q11. Найти недостающее число в массиве 0..n?

В массиве из `n` чисел все значения от `0` до `n`, кроме одного. Найти пропущенное.

```java
// Метод 1: сумма арифметической прогрессии
int missingNumberSum(int[] arr) {
    int n = arr.length;
    int expected = n * (n + 1) / 2;
    int actual = 0;
    for (int x : arr) actual += x;
    return expected - actual;
}

// Метод 2: XOR — без переполнения
int missingNumberXor(int[] arr) {
    int xor = arr.length; // включаем n
    for (int i = 0; i < arr.length; i++) {
        xor ^= i ^ arr[i];
    }
    return xor;
}
```

Оба способа — `O(n)` время и `O(1)` память. **Компромисс:** сумма читается проще, но на больших `n` рискует переполнить `int` (тогда берут `long`). XOR не накапливает величину — это побитовая операция, поэтому переполнения не бывает в принципе; в нём присутствующие индексы и значения взаимно сокращаются, оставляя пропущенное число.


## Q12. (!) Что такое prefix sum?

**Prefix sum** — массив накопленных частичных сумм: `prefix[i]` хранит сумму всех элементов слева от `i`. Строится один раз за `O(n)`, после чего сумму любого подмассива берут за `O(1)` как разность двух префиксов. Это превращает повторные range-запросы из `O(n)` каждый в `O(1)`.

```java
int[] buildPrefix(int[] arr) {
    int[] prefix = new int[arr.length + 1];
    for (int i = 0; i < arr.length; i++) {
        prefix[i + 1] = prefix[i] + arr[i];
    }
    return prefix;
}

// Сумма arr[i..j] (включительно) — O(1)
int rangeSum(int[] prefix, int i, int j) {
    return prefix[j + 1] - prefix[i];
}
```

Удобный приём — делать `prefix` длиной `n+1` с `prefix[0] = 0`: это убирает спецслучай для подмассивов, начинающихся с индекса 0. Работает с любыми числами, включая отрицательные и нули — сортировка не требуется. На больших значениях возможно переполнение, тогда берут `long`.

**Сценарий применения:** многократные range-запросы суммы и поиск подмассива с заданной суммой — `O(1)` на запрос после `O(n)` препроцессинга.


## Q13. (!) Подсчёт подмассивов с заданной суммой?

**Идея:** prefix sums + HashMap частот префиксов. Подмассив `arr[i..j-1]` даёт сумму `k` тогда и только тогда, когда `prefix[j] - prefix[i] = k`, то есть `prefix[i] = prefix[j] - k`. Поэтому, идя по массиву, к ответу прибавляют, сколько раз уже встречался префикс `sum - k`, и только потом учитывают текущий `sum`. Один проход — `O(n)`.

```java
int subarraySum(int[] arr, int k) {
    Map<Integer, Integer> prefixCount = new HashMap<>();
    prefixCount.put(0, 1); // пустой префикс

    int sum = 0, count = 0;
    for (int x : arr) {
        sum += x;
        // Сколько раз встречали prefix sum = (sum - k)?
        count += prefixCount.getOrDefault(sum - k, 0);
        prefixCount.merge(sum, 1, Integer::sum);
    }
    return count;
}
```

Сложность `O(n)` по времени и памяти.

**Подводный камень:** инициализация `prefixCount.put(0, 1)` обязательна — она учитывает «пустой префикс» и засчитывает подмассивы, начинающиеся с индекса 0 (для них `sum - k = 0`). Без неё, например, `[3]` при `k=3` дал бы `0`. Sliding window здесь не годится: при отрицательных числах сумма не монотонна, и сжатие окна теряет ответы — поэтому именно prefix sum + HashMap.


## Q14. Найти equilibrium index?

**Equilibrium index** — индекс `i`, где сумма элементов слева от него равна сумме справа (сам `arr[i]` не входит ни в одну часть). Приём: посчитать общую сумму, затем в одном проходе поддерживать `leftSum` и выводить `rightSum = total - leftSum - arr[i]` за `O(1)` — не пересчитывая правую часть заново.

```java
int equilibriumIndex(int[] arr) {
    int total = 0;
    for (int x : arr) total += x;

    int leftSum = 0;
    for (int i = 0; i < arr.length; i++) {
        int rightSum = total - leftSum - arr[i];
        if (leftSum == rightSum) return i;
        leftSum += arr[i];
    }
    return -1;
}
```

Сложность `O(n)` время, `O(1)` память. Один проход после подсчёта общей суммы.


## Q15. (!) Как повернуть матрицу на 90 градусов in-place?

**Поворот по часовой стрелке** раскладывается на две простые in-place операции: **транспонирование** (отражение по главной диагонали) + **реверс каждой строки**. Обе делаются на месте, поэтому весь поворот не требует второй матрицы — `O(1)` доп. памяти.

```java
void rotate(int[][] matrix) {
    int n = matrix.length;

    // Транспонирование (отражение по главной диагонали)
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            int tmp = matrix[i][j];
            matrix[i][j] = matrix[j][i];
            matrix[j][i] = tmp;
        }
    }

    // Реверс каждой строки
    for (int[] row : matrix) {
        int left = 0, right = n - 1;
        while (left < right) {
            int tmp = row[left];
            row[left++] = row[right];
            row[right--] = tmp;
        }
    }
}
```

Сложность `O(n²)` (по числу элементов матрицы), память `O(1)`. **Поворот против часовой** получают зеркальным приёмом: транспонирование + реверс столбцов (либо реверс строк + транспонирование).


## Q16. Spiral order — обход матрицы по спирали?

Поддерживаем четыре границы — `top`, `bottom`, `left`, `right` — и обходим внешний контур по кругу (вправо → вниз → влево → вверх), после каждого направления сжимая соответствующую границу внутрь. Так спираль постепенно стягивается к центру. Сложность `O(m·n)`.

```java
List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> result = new ArrayList<>();
    if (matrix.length == 0) return result;

    int top = 0, bottom = matrix.length - 1;
    int left = 0, right = matrix[0].length - 1;

    while (top <= bottom && left <= right) {
        // → вправо
        for (int j = left; j <= right; j++) result.add(matrix[top][j]);
        top++;
        // ↓ вниз
        for (int i = top; i <= bottom; i++) result.add(matrix[i][right]);
        right--;
        if (top <= bottom) {
            // ← влево
            for (int j = right; j >= left; j--) result.add(matrix[bottom][j]);
            bottom--;
        }
        if (left <= right) {
            // ↑ вверх
            for (int i = bottom; i >= top; i--) result.add(matrix[i][left]);
            left++;
        }
    }
    return result;
}
```

**Подводный камень:** перед обратными проходами (влево и вверх) обязательны проверки `top <= bottom` и `left <= right`. В матрицах-полосках (одна строка или один столбец) без них последняя строка или столбец обойдутся дважды.


## Q17. Set zeroes — обнуление строк и столбцов?

Задача: если ячейка содержит `0`, обнулить всю её строку и столбец. **Подвох:** нельзя обнулять прямо во время обхода — свежие нули будут восприняты как исходные и обнулят лишние строки/столбцы. Решение `O(1)` памяти: первую строку и первый столбец используют как «доску маркеров», а их собственную обнуляемость запоминают в двух флагах.

```java
// O(1) дополнительной памяти — используем первую строку/столбец как маркеры
void setZeroes(int[][] matrix) {
    int rows = matrix.length, cols = matrix[0].length;
    boolean firstRowZero = false, firstColZero = false;

    // Проверяем первую строку и столбец
    for (int j = 0; j < cols; j++) if (matrix[0][j] == 0) firstRowZero = true;
    for (int i = 0; i < rows; i++) if (matrix[i][0] == 0) firstColZero = true;

    // Используем 1-ю строку/столбец как маркеры
    for (int i = 1; i < rows; i++)
        for (int j = 1; j < cols; j++)
            if (matrix[i][j] == 0) {
                matrix[i][0] = 0;
                matrix[0][j] = 0;
            }

    // Обнуляем по маркерам
    for (int i = 1; i < rows; i++)
        for (int j = 1; j < cols; j++)
            if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;

    // Обнуляем 1-ю строку/столбец, если нужно
    if (firstRowZero) for (int j = 0; j < cols; j++) matrix[0][j] = 0;
    if (firstColZero) for (int i = 0; i < rows; i++) matrix[i][0] = 0;
}
```

Сложность `O(m·n)` время, `O(1)` память. **Компромисс:** более простой для чтения вариант хранит два отдельных массива-маркера для строк и столбцов — это `O(m+n)` памяти, зато логика без хитростей с первой строкой/столбцом.


## Q18. (!) Dutch National Flag — сортировка трёх значений за один проход?

Задача Эдсгера Дейкстры: отсортировать массив из трёх значений (`0`, `1`, `2`) за один проход — `O(n)` время, `O(1)` память. Три указателя `low`, `mid`, `high` поддерживают инвариант разбиения массива на зоны и за один проход раскладывают элементы по местам, не пересортировывая.

```java
void sortColors(int[] arr) {
    int low = 0, mid = 0, high = arr.length - 1;

    while (mid <= high) {
        if (arr[mid] == 0) {
            swap(arr, low++, mid++);
        } else if (arr[mid] == 1) {
            mid++;
        } else { // arr[mid] == 2
            swap(arr, mid, high--);
            // mid не двигаем — пришедший элемент ещё не обработан
        }
    }
}

void swap(int[] arr, int i, int j) {
    int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
}
```

**Инвариант зон:** массив всегда поделён на четыре части — `[0..low)` нули, `[low..mid)` единицы, `[mid..high]` ещё не обработанные, `(high..end]` двойки. Тонкость: при свапе двойки в конец `mid` **не** двигают — пришедший из `high` элемент ещё не просмотрен.

**Сценарий применения:** 3-way partition в `Quick Sort` для массивов с большим числом дубликатов — тот же приём раскладки на «меньше / равно / больше».


## Q19. Move Zeroes — переместить нули в конец, сохранив порядок ненулевых?

Two pointers по той же схеме slow/fast, что и в удалении дубликатов: `fast` сканирует массив, и каждый ненулевой элемент пишется на позицию `slow`, после чего `slow` сдвигается. Когда `fast` дошёл до конца, хвост `[slow..n)` дозаполняют нулями. Порядок ненулевых сохраняется естественно. `O(n)` время, `O(1)` память.

```java
void moveZeroes(int[] arr) {
    int slow = 0;
    for (int fast = 0; fast < arr.length; fast++) {
        if (arr[fast] != 0) {
            arr[slow++] = arr[fast];
        }
    }
    while (slow < arr.length) arr[slow++] = 0;
}
```

**Компромисс:** альтернатива со swap (меняем нулевой и ненулевой местами) тоже `O(n)`, но делает больше записей; вариант с дозаписью нулей в конце обычно чище.


## Q20. Next Permutation — следующая лексикографическая перестановка?

Алгоритм из трёх шагов за `O(n)`, `O(1)`:
1. Идя справа, найти первый «пик» — индекс `i`, где `arr[i] < arr[i+1]`. Всё справа от него — убывающая (максимальная) последовательность, которую уже не увеличить.
2. Справа найти наименьший элемент, который всё ещё больше `arr[i]`, и поменять их местами — это минимально возможное увеличение позиции `i`.
3. Реверснуть хвост справа от `i`: он был убывающим, а нам нужна минимальная перестановка после увеличения — то есть возрастающая.

```java
void nextPermutation(int[] arr) {
    int n = arr.length;

    // 1. Найти первый i справа, где arr[i] < arr[i+1]
    int i = n - 2;
    while (i >= 0 && arr[i] >= arr[i + 1]) i--;

    if (i >= 0) {
        // 2. Найти первый j справа, где arr[j] > arr[i]
        int j = n - 1;
        while (arr[j] <= arr[i]) j--;
        // 3. Swap
        int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
    }

    // 4. Реверс правой части
    int left = i + 1, right = n - 1;
    while (left < right) {
        int tmp = arr[left];
        arr[left++] = arr[right];
        arr[right--] = tmp;
    }
}
```

**Граничный случай:** если «пик» не найден (`i < 0`), массив уже максимальная перестановка (полностью убывающий) — тогда шаг 4 реверсит его целиком, давая первую (отсортированную по возрастанию) перестановку. Это и есть «по кругу к началу».


## Q21. (!) String — алгоритм реверса строки?

`String` в Java **immutable**, поэтому перевернуть строку «на месте» нельзя — сначала получают изменяемое представление (`char[]` или `StringBuilder`). Три типовых подхода: `StringBuilder.reverse()` (короче всего), two pointers по `char[]` (показывает владение указателями — этого обычно и ждут) и рекурсия (наглядно, но `O(n)` стека — не рекомендуется).

```java
// 1. Через StringBuilder — O(n)
String reverse1(String s) {
    return new StringBuilder(s).reverse().toString();
}

// 2. Two pointers in-place в char[] — O(n)
String reverse2(String s) {
    char[] arr = s.toCharArray();
    int left = 0, right = arr.length - 1;
    while (left < right) {
        char tmp = arr[left];
        arr[left++] = arr[right];
        arr[right--] = tmp;
    }
    return new String(arr);
}

// 3. Рекурсия — O(n) по времени и стеку — НЕ рекомендуется
String reverse3(String s) {
    if (s.length() <= 1) return s;
    return reverse3(s.substring(1)) + s.charAt(0);
}
```

**Подводный камень — surrogate pairs.** Символы вне BMP (эмодзи, часть иероглифов) кодируются двумя `char`. Простой реверс `char[]` переставит половинки суррогатной пары и испортит символ. Корректно учитывает суррогаты `StringBuilder.reverse()` — он переворачивает по code points, а не по UTF-16 единицам.

Подробнее — в [Java String](../../programming-languages/java/java-string-interview.md).


## Q22. (!) Проверка палиндрома?

Two pointers навстречу: `left` от начала, `right` от конца сравнивают символы, пока не встретятся; первое же расхождение — не палиндром. `O(n)` время, `O(1)` память. Реверс с последующим сравнением тоже корректен, но тратит `O(n)` доп. памяти.

В LeetCode-варианте добавляются два правила: пропускать не-буквенно-цифровые символы и сравнивать без учёта регистра — для этого внутренними циклами проматывают «мусорные» символы и приводят к нижнему регистру.

```java
// Простой случай — без учёта регистра и спецсимволов
boolean isPalindrome(String s) {
    int left = 0, right = s.length() - 1;
    while (left < right) {
        if (s.charAt(left) != s.charAt(right)) return false;
        left++;
        right--;
    }
    return true;
}

// LeetCode-вариант: только alphanumeric, ignore case
boolean isPalindromeLeet(String s) {
    int left = 0, right = s.length() - 1;
    while (left < right) {
        while (left < right && !Character.isLetterOrDigit(s.charAt(left))) left++;
        while (left < right && !Character.isLetterOrDigit(s.charAt(right))) right--;
        if (Character.toLowerCase(s.charAt(left)) !=
            Character.toLowerCase(s.charAt(right))) return false;
        left++;
        right--;
    }
    return true;
}
```

Оба варианта — `O(n)` время, `O(1)` память.


## Q23. (!) Проверка анаграммы?

Две строки — анаграммы, если одна получается перестановкой символов другой (одинаковый мультимножество символов). Первый отсев — разная длина: тогда сразу `false`.

Три подхода с разным компромиссом:
- **Сортировка** обеих строк и сравнение — `O(n log n)`, просто, но медленнее.
- **Подсчёт частот** в `int[26]` — `O(n)`, `O(1)` памяти, но только для английского lowercase: один проход инкрементирует счётчики для первой строки и декрементирует для второй, в конце все должны быть нулём.
- **HashMap по code points** — `O(n)`, универсально для Unicode.

```java
// Метод 1: сортировка — O(n log n) время, O(n) память (toCharArray)
boolean isAnagramSort(String a, String b) {
    if (a.length() != b.length()) return false;
    char[] aArr = a.toCharArray();
    char[] bArr = b.toCharArray();
    Arrays.sort(aArr);
    Arrays.sort(bArr);
    return Arrays.equals(aArr, bArr);
}

// Метод 2: подсчёт частот — O(n) время, O(1) память для ASCII
boolean isAnagram(String a, String b) {
    if (a.length() != b.length()) return false;
    int[] count = new int[26]; // только английский lowercase
    for (int i = 0; i < a.length(); i++) {
        count[a.charAt(i) - 'a']++;
        count[b.charAt(i) - 'a']--;
    }
    for (int c : count) if (c != 0) return false;
    return true;
}

// Универсальный для unicode — HashMap
boolean isAnagramUnicode(String a, String b) {
    if (a.length() != b.length()) return false;
    Map<Integer, Integer> freq = new HashMap<>();
    a.codePoints().forEach(c -> freq.merge(c, 1, Integer::sum));
    b.codePoints().forEach(c -> freq.merge(c, -1, Integer::sum));
    return freq.values().stream().allMatch(v -> v == 0);
}
```


## Q24. Группировка анаграмм?

Идея: у анаграмм совпадает **канонический ключ**. Самый простой ключ — отсортированная строка: все анаграммы дают один и тот же `key`, поэтому их складывают в одну группу в `HashMap`.

```java
List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> groups = new HashMap<>();
    for (String s : strs) {
        char[] arr = s.toCharArray();
        Arrays.sort(arr);
        String key = new String(arr);
        groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(groups.values());
}
```

Сложность `O(n · k log k)`, где `n` — число строк, `k` — макс. длина (сортировка каждого ключа). **Компромисс:** альтернативный ключ — частотный массив (`count[26]`), сериализованный в строку: убирает сортировку и даёт `O(n · k)`, но ключи получаются длиннее и расходуют больше памяти.


## Q25. (!) Самая длинная подстрока без повторов?

**Sliding window** с `HashMap` последнего вхождения каждого символа. Правый край `right` расширяет окно; как только символ уже встречался **внутри текущего окна**, левый край `left` перепрыгивает за прошлую позицию этого символа. На каждом шаге обновляем максимум длины окна. Каждый символ обрабатывается один раз — `O(n)`.

```java
int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> lastSeen = new HashMap<>();
    int maxLen = 0, left = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
            left = lastSeen.get(c) + 1; // сдвигаем левую границу
        }
        lastSeen.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

Сложность `O(n)` время, память `O(min(n, размер_алфавита))`. **Тонкость:** проверка `lastSeen.get(c) >= left` обязательна — иначе `left` может «откатиться» назад на старое вхождение символа, оставшееся за пределами окна. Для ASCII вместо HashMap берут массив на 128 ячеек — `O(1)` память.


## Q26. Самый длинный палиндром в строке?

**Expand around center.** Палиндром симметричен относительно центра, поэтому перебираем все возможные центры и от каждого «расширяемся» наружу, пока символы совпадают. Центров `2n - 1`: `n` для нечётной длины (центр — символ) и `n - 1` для чётной (центр — между символами), поэтому проверяют обе функции `expand(i, i)` и `expand(i, i+1)`. Сложность `O(n²)`, память `O(1)`.

```java
String longestPalindrome(String s) {
    int start = 0, end = 0;

    for (int i = 0; i < s.length(); i++) {
        int len1 = expand(s, i, i);     // нечётная длина
        int len2 = expand(s, i, i + 1); // чётная длина
        int len = Math.max(len1, len2);

        if (len > end - start) {
            start = i - (len - 1) / 2;
            end = i + len / 2;
        }
    }
    return s.substring(start, end + 1);
}

int expand(String s, int left, int right) {
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        left--;
        right++;
    }
    return right - left - 1;
}
```

**Компромисс:** алгоритм Манакера решает задачу за `O(n)`, но заметно сложнее в реализации, поэтому на собеседовании обычно достаточно `expand around center`.


## Q27. (!) Поиск подстроки — алгоритм KMP?

**KMP (Knuth-Morris-Pratt)** — поиск подстроки длины `m` в тексте длины `n` за `O(n + m)` против `O(n·m)` у наивного. Главная идея: при несовпадении не откатывать указатель по тексту назад. Вместо этого предпосчитанная **префикс-функция** (`lps`) подсказывает, на сколько можно сдвинуть шаблон, переиспользовав уже совпавший префикс.

```java
int kmpSearch(String text, String pattern) {
    if (pattern.isEmpty()) return 0;

    int[] lps = computeLPS(pattern);
    int i = 0, j = 0;

    while (i < text.length()) {
        if (text.charAt(i) == pattern.charAt(j)) {
            i++; j++;
            if (j == pattern.length()) return i - j;
        } else if (j > 0) {
            j = lps[j - 1]; // используем префикс-функцию
        } else {
            i++;
        }
    }
    return -1;
}

int[] computeLPS(String pattern) {
    int[] lps = new int[pattern.length()];
    int len = 0, i = 1;

    while (i < pattern.length()) {
        if (pattern.charAt(i) == pattern.charAt(len)) {
            lps[i++] = ++len;
        } else if (len > 0) {
            len = lps[len - 1];
        } else {
            lps[i++] = 0;
        }
    }
    return lps;
}
```

`lps[i]` — длина наибольшего собственного префикса `pattern[0..i]`, который одновременно является и его суффиксом. Именно эту длину переиспользуют при несовпадении. Препроцессинг `lps` — `O(m)`, сам поиск — `O(n)`.

**Сценарий применения:** в стандартной библиотеке `String.indexOf()` использует наивный `O(n·m)` — для разовых поисков этого достаточно. KMP оправдан при частых поисках по большим текстам; для поиска множества образцов сразу — Aho-Corasick.


## Q28. (!) Алгоритм Рабина-Карпа?

**Rabin-Karp** — поиск подстроки через хеширование. Сравнивать строки посимвольно дорого, поэтому сравнивают их **хеши**, а посимвольную проверку делают только при совпадении хешей. Ключ к скорости — **rolling hash**: при сдвиге окна на один символ хеш пересчитывается за `O(1)` (вычитаем старший символ, домножаем, прибавляем новый), а не за `O(m)`. Среднее `O(n + m)`, худшее `O(n·m)` при множестве хеш-коллизий.

```java
int rabinKarp(String text, String pattern) {
    int n = text.length(), m = pattern.length();
    if (m > n) return -1;

    long base = 256, mod = 1_000_000_007L;
    long patternHash = 0, windowHash = 0, h = 1;

    for (int i = 0; i < m - 1; i++) h = (h * base) % mod;

    for (int i = 0; i < m; i++) {
        patternHash = (patternHash * base + pattern.charAt(i)) % mod;
        windowHash  = (windowHash  * base + text.charAt(i))    % mod;
    }

    for (int i = 0; i <= n - m; i++) {
        if (patternHash == windowHash && text.regionMatches(i, pattern, 0, m)) {
            return i; // hash match + verify
        }
        if (i < n - m) {
            windowHash = (base * (windowHash - text.charAt(i) * h)
                          + text.charAt(i + m)) % mod;
            if (windowHash < 0) windowHash += mod;
        }
    }
    return -1;
}
```

**Подводный камень:** совпадение хешей не гарантирует совпадения строк (коллизии), поэтому проверка `text.regionMatches(...)` обязательна. Берут большой простой `mod` и следят за знаком при пересчёте (`if (windowHash < 0) windowHash += mod`).

**Сценарий применения:** поиск сразу нескольких образцов одной длины (их хеши кладут в `HashSet`), плагиат-детекторы, поиск дубликатов фрагментов в файлах.


## Q29. Сравнение двух строк с учётом backspace?

`#` означает нажатие backspace (удаляет предыдущий символ). Нужно сравнить строки **после** применения всех backspace. Решение за `O(1)` памяти — идти **с конца**: с хвоста сразу видно, сколько символов «съест» накопленный backspace, тогда как при движении слева направо это неизвестно. Указатель `skip` считает, сколько ближайших реальных символов нужно пропустить.

```java
// O(n+m) время, O(1) память — идём с конца
boolean backspaceCompare(String s, String t) {
    int i = s.length() - 1, j = t.length() - 1;

    while (i >= 0 || j >= 0) {
        i = nextValidChar(s, i);
        j = nextValidChar(t, j);

        if (i < 0 && j < 0) return true;
        if (i < 0 || j < 0) return false;
        if (s.charAt(i) != t.charAt(j)) return false;
        i--; j--;
    }
    return true;
}

int nextValidChar(String s, int i) {
    int skip = 0;
    while (i >= 0) {
        if (s.charAt(i) == '#') { skip++; i--; }
        else if (skip > 0) { skip--; i--; }
        else return i;
    }
    return -1;
}
```

**Компромисс:** проще для чтения — построить итоговые строки через `StringBuilder` или стек и сравнить, но это `O(n+m)` памяти вместо `O(1)`.


## Q30. (!) Поменять два числа без временной переменной?

Два классических трюка: **XOR** (`a^=b; b^=a; a^=b`) и **арифметика** (`a+=b; b=a-b; a-=b`). XOR опирается на свойство `a^a=0`, не переполняется и работает для любых целых. Арифметический вариант рискует переполнением при больших значениях.

```java
// Через XOR — без переполнения, работает для целых
void swapXor(int[] arr, int i, int j) {
    if (i == j) return; // важно! XOR с собой даёт 0
    arr[i] = arr[i] ^ arr[j];
    arr[j] = arr[i] ^ arr[j];
    arr[i] = arr[i] ^ arr[j];
}

// Через арифметику — риск переполнения
void swapArith(int[] arr, int i, int j) {
    arr[i] = arr[i] + arr[j];
    arr[j] = arr[i] - arr[j];
    arr[i] = arr[i] - arr[j];
}
```

**Подводный камень:** XOR-swap по одному индексу/переменной (`i == j`) обнулит значение — `a^a=0`, поэтому в коде стоит проверка `if (i == j) return`. В реальном коде всё равно используют временную переменную — это читабельнее и без подвохов; XOR-трюк ценен лишь как демонстрация понимания побитовых операций. Подробнее — в [Java Types](../../programming-languages/java/java-types-interview.md).


## Q31. Перевод числа в произвольную систему счисления?

Алгоритм — повторное деление с остатком: на каждом шаге остаток `number % base` даёт очередную (младшую) цифру, а `number /= base` уменьшает число. Цифры получаются от младшей к старшей, поэтому в конце результат реверсят. Цифры > 9 кодируют буквами `A..Z`.

```java
String decimalToBase(int number, int base) {
    if (number == 0) return "0";
    StringBuilder sb = new StringBuilder();
    boolean negative = number < 0;
    number = Math.abs(number);

    while (number > 0) {
        int remainder = number % base;
        sb.append(remainder < 10 ? (char)('0' + remainder)
                                  : (char)('A' + remainder - 10));
        number /= base;
    }
    if (negative) sb.append('-');
    return sb.reverse().toString();
}

// Встроенные методы Java:
String binary = Integer.toString(42, 2);   // "101010"
String hex    = Integer.toString(42, 16);  // "2a"
int back      = Integer.parseInt("101010", 2); // 42
```

**Граничные случаи:** `number == 0` обрабатывают отдельно (цикл `while (number > 0)` иначе вернёт пустую строку), а отрицательные — через флаг знака. Минимальная база для `Integer.toString` — 2, максимальная — 36 (`Character.MAX_RADIX`): больше не хватит символов `0-9A-Z`.


## Q32. Проверка степени двойки?

Одной проверкой `n > 0 && (n & (n - 1)) == 0`. У степени двойки в двоичной записи ровно один установленный бит, и приём `n & (n - 1)` обнуляет младший единичный бит — для степеней двойки это даёт `0`.

```java
boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}
```

**Почему так:** `n - 1` инвертирует все биты после младшего единичного, поэтому `n & (n - 1)` гасит этот бит. У степени двойки бит один — результат `0`.

**Граничные случаи:** для `n = 0` выражение `n & (n - 1)` тоже даёт `0`, поэтому нужна явная проверка `n > 0`; она же отсекает отрицательные числа, для которых ответ всегда `false`.


## Q33. (!) Почему == для строк работает не как ожидается?

Потому что для объектов `==` сравнивает **ссылки**, а не содержимое. Для строк `==` случайно «срабатывает» только когда обе ссылки указывают на один объект — например, на интернированный литерал в string pool. Литералы с одинаковым значением Java переиспользует из пула, а `new String(...)` всегда создаёт новый объект в куче.

```java
String a = "hello";
String b = "hello";
a == b;  // true — обе ссылаются на pool

String c = new String("hello");
a == c;  // false — c в куче, не в pool

a.equals(c);  // true — содержимое одинаковое

c.intern() == a;  // true — intern возвращает ссылку из pool
```

**Рекомендация:** для сравнения строк по содержимому всегда `.equals()` или `Objects.equals()` (последний дополнительно защищает от NPE, когда один аргумент `null`). `==` для строк — почти всегда баг.

Подробнее — в [Java String](../../programming-languages/java/java-string-interview.md).


## Q34. Когда использовать char[] вместо String для алгоритмов?

Коротко — в трёх ситуациях, где immutability `String` мешает: нужна модификация на месте, требуется надёжно затереть данные из памяти или важна производительность горячего цикла.

1. **In-place модификация** — `String` immutable, `char[]` можно менять
2. **Безопасность** — пароли держат в `char[]`, чтобы можно было «затереть» (`Arrays.fill(arr, '\0')`); `String` остаётся в куче до GC
3. **Производительность** — для тяжёлых алгоритмов на строках `char[]` обходит `String.charAt()` (в Java 9+ String хранится как `byte[]` с compact strings)

```java
// Работа с паролем
char[] password = readPassword();
try {
    authenticate(password);
} finally {
    Arrays.fill(password, '\0'); // затираем
}
```

С Java 9+ строки хранятся как `byte[]` с флагом кодировки (LATIN1 или UTF-16) — `String.charAt()` для LATIN1 вернёт значение быстро, но для алгоритмов всё ещё может быть выгоднее `toCharArray()`.


## Q35. (!) StringBuilder vs String конкатенация — где разница?

Разница в сложности при конкатенации **в цикле**. `String` immutable, поэтому каждый `result += s` создаёт новую строку и копирует всё накопленное — для `n` итераций это `O(n²)`. `StringBuilder` пишет в один растущий буфер, давая `O(n)`. Для финальной сборки также удобны `String.join` и `Collectors.joining`.

```java
// O(n²) — каждая конкатенация создаёт новую строку
String result = "";
for (String s : list) result += s; // ужасно для большого list

// O(n) — StringBuilder
StringBuilder sb = new StringBuilder();
for (String s : list) sb.append(s);
String result = sb.toString();

// O(n) — String.join, лаконично
String result = String.join("", list);

// O(n) — Collectors.joining
String result = list.stream().collect(Collectors.joining(", "));
```

**Нюанс Java 9+:** для `+` компилятор подставляет `invokedynamic` + `StringConcatFactory`, и **простая** (однократная) конкатенация эффективна — отдельный `StringBuilder` тут вручную не нужен. Но в **цикле** `+=` всё равно проигрывает: компилятор не видит итерации целиком и не может слить их в одну операцию.

**Рекомендация:** для thread-safe сборки берут `StringBuffer` — он синхронизирован, но из-за этого медленнее `StringBuilder`.


## Q36. Как избежать ArrayIndexOutOfBoundsException в two-pointers?

Главное правило — проверять границы **до** обращения по индексу и пользоваться короткозамкнутым `&&`, чтобы проверка границы стояла раньше чтения элемента. Типичные источники ошибок:

1. **Условие в цикле перед чтением** — проверяй `left < arr.length`, `right >= 0`
2. **Учитывай Deque/PriorityQueue null** — `pollFirst()` возвращает `null`, не кидает исключение
3. **Используй `<=` vs `<`** аккуратно — частая ошибка в `binarySearch`
4. **При работе со строкой:** `s.charAt(i)` кидает `StringIndexOutOfBoundsException`, проверяй границы

```java
// Опасно — проверяй с короткого замыкания
while (i < n && j < m && arr[i] == brr[j]) { ... }

// Безопаснее: явный break
while (i < n) {
    if (j >= m || arr[i] != brr[j]) break;
    ...
}
```

При работе с `char` помни про **surrogate pairs**: `String.length()` считает UTF-16 кодовые единицы, а не code points.


---

## See also

- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — Big O для типичных операций
- [Two Pointers и Sliding Window](../algorithmic-paradigms/two-pointers-sliding-window-interview.md) — глубокий разбор паттернов
- [Алгоритмы сортировки](../sorting-searching/sorting-algorithms-interview.md) — Dutch National Flag и 3-way partition
- [Алгоритмы поиска](../sorting-searching/searching-algorithms-interview.md) — линейный, бинарный
- [Хеш-таблицы](hash-tables-interview.md) — для Two Sum, Group Anagrams
- [Динамическое программирование](../algorithmic-paradigms/dynamic-programming-interview.md) — Kadane как пример DP
- [Связные списки](linked-lists-interview.md) — фаст/слоу указатели для циклов
- [Java String](../../programming-languages/java/java-string-interview.md) — immutability, intern, surrogate pairs
- [Java Collections](../../programming-languages/java/java-collections-interview.md) — ArrayList vs LinkedList
- [Java Types](../../programming-languages/java/java-types-interview.md) — XOR, побитовые операции


- [Графы](graphs-interview.md)
- [Хеш-таблицы](hash-tables-interview.md)
- [Кучи (Heaps)](heaps-interview.md)
- [Связные списки](linked-lists-interview.md)
- [Стеки и очереди](stacks-queues-interview.md)
- [Деревья](trees-interview.md)
