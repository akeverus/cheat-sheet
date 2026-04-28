---
title: "k наибольших элементов (K Largest Elements)"
description: "Кратко: поиск k самых больших элементов в массиве. Рассматриваются три подхода: грубая сила O(nk), TreeSet O(nlog(n)), и PriorityQueue O(nlog(k))."
tags:
  - algorithms
  - searching
  - k-largest-elements
type: "overview"
difficulty: "intermediate"
aliases:
  - "k наибольших элементов"
  - "K Largest Elements"
  - "k наибольших"
prerequisites:
  - "[[heap-sort]]"
next:
  - "[[top-n-frequent-elements]]"
updated: "2026-04-20"
---
# k наибольших элементов (K Largest Elements)

Кратко: поиск k самых больших элементов в массиве. Рассматриваются три подхода: грубая сила `O(nk)`, `TreeSet` `O(nlog(n)`), и `PriorityQueue` `O(nlog(k)`).

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Find k largest elements](https://www.geeksforgeeks.org/k-largestor-smallest-elements-in-an-array/)

### См. также
- [Поиск k-го по величине элемента](kth-smallest-in-two-sorted-arrays.md) — k-th smallest
- [N самых частых элементов](top-n-frequent-elements.md) — top N frequent

- [Максимальный подмассив (Maximum Subarray Problem)](maximum-subarray.md)
- [Интерполяционный поиск (Interpolation Search)](interpolation-search.md)
- [Поиск k-го по величине элемента (Find Kth Largest Element)](find-max-element.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Реализация на Java](#реализация-на-java)
  - [Подход 1: Грубая сила (Java)](#подход-1-грубая-сила-java)
  - [Подход 2: TreeSet (Java)](#подход-2-treeset-java)
  - [Ограничения](#ограничения)
  - [Пример использования](#пример-использования)
  - [Подход 3: PriorityQueue (Min Heap) (Java)](#подход-3-priorityqueue-min-heap-java)
  - [Объяснение алгоритма](#объяснение-алгоритма)
  - [Альтернативная реализация с Max Heap (Java)](#альтернативная-реализация-с-max-heap-java)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Подход 1: Грубая сила (Kotlin)](#подход-1-грубая-сила-kotlin)
  - [Подход 2: TreeSet (Kotlin)](#подход-2-treeset-kotlin)
  - [Подход 3: PriorityQueue (Min Heap) (Kotlin)](#подход-3-priorityqueue-min-heap-kotlin)
  - [Альтернативная реализация с Max Heap (Kotlin)](#альтернативная-реализация-с-max-heap-kotlin)
- [Альтернативы и оптимизации](#альтернативы-и-оптимизации)
  - [QuickSelect](#quickselect)
  - [Сортировка и частичный срез](#сортировка-и-частичный-срез)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Проверка входа и контракты](#проверка-входа-и-контракты)
- [Профилирование и масштабирование](#профилирование-и-масштабирование)
- [Тестирование](#тестирование)
  - [Юнит-тесты на JUnit](#юнит-тесты-на-junit)
  - [Kotlin-тесты](#kotlin-тесты)
  - [Property-based тест](#property-based-тест)
  - [Перфоманс‑скетч с JMH](#перфомансскетч-с-jmh)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
- [Дополнительные примеры использования](#дополнительные-примеры-использования)
  - [Пример 1: Потоки и Comparator](#пример-1-потоки-и-comparator)
  - [Пример 2: Поддержка стабильного порядка](#пример-2-поддержка-стабильного-порядка)
  - [Пример 3: Фильтрация дубликатов](#пример-3-фильтрация-дубликатов)
  - [Пример 4: Несколько отсортированных массивов](#пример-4-несколько-отсортированных-массивов)
  - [Пример 5: Counting для ограниченного диапазона](#пример-5-counting-для-ограниченного-диапазона)
  - [Пример 6: SQL window top-k](#пример-6-sql-window-top-k)
- [Заключение](#заключение)

## Описание алгоритма

В этом руководстве мы реализуем различные решения проблемы поиска k самых больших элементов в массиве с помощью `Java`. Для описания временной сложности мы будем использовать нотацию `Big-O`.

## Реализация на Java

### Подход 1: Грубая сила (Java)

Грубое решение этой проблемы состоит в том, чтобы перебрать заданный массив k раз. На каждой итерации мы будем находить наибольшее значение. Затем мы удалим это значение из массива и поместим в выходной список:

```java
// Грубая сила: k проходов, каждый раз ищем максимум и удаляем (O(n*k))
public List<Integer> findTopK(List<Integer> input, int k) {
    List<Integer> array = new ArrayList<>(input);
    List<Integer> topKList = new ArrayList<>();

    for (int i = 0; i < k; i++) {
        int maxIndex = 0;

        for (int j = 1; j < array.size(); j++) {
            if (array.get(j) > array.get(maxIndex)) {
                maxIndex = j;
            }
        }

        topKList.add(array.remove(maxIndex));
    }

    return topKList;
}
```

Если мы предположим, что n — это размер данного массива, временная сложность этого решения равна `O(n * k)`. Кроме того, это самое неэффективное решение.

### Подход 2: TreeSet (Java)

Однако существуют более эффективные решения этой проблемы. В этом разделе мы объясним два из них с использованием коллекций `Java`.

`TreeSet` имеет структуру данных `Red-Black Tree` в качестве основы. В результате добавление значения в этот набор стоит `O(log(n))`). `TreeSet` - это отсортированная коллекция. Следовательно, мы можем поместить все значения в `TreeSet` и извлечь первые k из них:

```java
public List<Integer> findTopK(List<Integer> input, int k) {
    Set<Integer> sortedSet = new TreeSet<>(Comparator.reverseOrder());
    sortedSet.addAll(input);
    return sortedSet.stream().limit(k).collect(Collectors.toList());
}
```

Временная сложность этого решения составляет `O(nlog(n)`). Прежде всего, предполагается, что это более эффективно, чем метод грубой силы, если k ≥ log(n).

### Ограничения

Важно помнить, что `TreeSet` не содержит дубликатов. В результате решение работает только для входного массива с различными значениями.

### Пример использования

```java
List<Integer> input = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
List<Integer> top3 = findTopK(input, 3);
// Результат: [9, 6, 5] (дубликаты удалены)
```

### Подход 3: PriorityQueue (Min Heap) (Java)

`PriorityQueue` - это структура данных `Heap` в `Java`. С его помощью мы можем добиться решения `O(nlog(k)`). Более того, это будет более быстрое решение, чем предыдущее. Из-за указанной проблемы k всегда меньше размера массива. Итак, это означает, что `O(nlog(k)`) ≤ `O(nlog(n)`).

Алгоритм проходит один раз по заданному массиву. На каждой итерации мы будем добавлять новый элемент в кучу. Кроме того, мы сохраним размер кучи меньше или равным k. Итак, нам придется удалить лишние элементы из кучи и добавить новые. В результате после перебора массива куча будет содержать k самых больших значений:

```java
public List<Integer> findTopK(List<Integer> input, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    input.forEach(number -> {
        minHeap.add(number);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    });

    List<Integer> topKList = new ArrayList<>(minHeap);
    Collections.reverse(topKList);
    return topKList;
}
```

### Объяснение алгоритма

Ключевой инвариант — в куче всегда лежат не более `k` элементов, и это текущие k максимальных. Мы последовательно добавляем каждый элемент входа, а при превышении размера удаляем минимум. Так мы гарантированно «отсекаем» мелкие значения, которые не попадут в итоговый ответ. Одного линейного прохода достаточно: после него кучу можно преобразовать в список и, при необходимости, развернуть в убывающий порядок.

### Альтернативная реализация с Max Heap (Java)

Можно также использовать максимальную кучу, но это менее эффективно:

```java
public List<Integer> findTopKWithMaxHeap(List<Integer> input, int k) {
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    maxHeap.addAll(input);

    List<Integer> topKList = new ArrayList<>();
    for (int i = 0; i < k && !maxHeap.isEmpty(); i++) {
        topKList.add(maxHeap.poll());
    }

    return topKList;
}
```

## Реализация на Kotlin

В `Kotlin` алгоритм поиска k наибольших элементов может быть реализован следующим образом:

### Подход 1: Грубая сила (Kotlin)

```kotlin
fun findTopK(input: List<Int>, k: Int): List<Int> {
    val array = input.toMutableList()
    val topKList = mutableListOf<Int>()

    repeat(k) {
        var maxIndex = 0
        for (j in 1 until array.size) {
            if (array[j] > array[maxIndex]) {
                maxIndex = j
            }
        }
        topKList.add(array.removeAt(maxIndex))
    }

    return topKList
}
```

### Подход 2: TreeSet (Kotlin)

```kotlin
fun findTopKWithTreeSet(input: List<Int>, k: Int): List<Int> {
    val sortedSet = input.toSortedSet(compareByDescending { it })
    return sortedSet.take(k)
}
```

### Подход 3: PriorityQueue (Min Heap) (Kotlin)

```kotlin
import java.util.*

fun findTopKWithPriorityQueue(input: List<Int>, k: Int): List<Int> {
    val minHeap = PriorityQueue<Int>()

    input.forEach { number ->
        minHeap.offer(number)
        if (minHeap.size > k) {
            minHeap.poll()
        }
    }

    return minHeap.toList().reversed()
}
```

Использование:

```kotlin
fun main() {
    val input = listOf(3, 1, 4, 1, 5, 9, 2, 6)
    val top3 = findTopKWithPriorityQueue(input, 3)
    println(top3) // [9, 6, 5]
}
```

### Альтернативная реализация с Max Heap (Kotlin)

```kotlin
fun findTopKWithMaxHeap(input: List<Int>, k: Int): List<Int> {
    val maxHeap = PriorityQueue<Int>(compareByDescending { it })
    maxHeap.addAll(input)

    return (0 until minOf(k, maxHeap.size)).map { maxHeap.poll() }
}
```

## Альтернативы и оптимизации

### QuickSelect

`QuickSelect` позволяет в среднем за линейное время найти k-й по величине элемент, после чего можно отфильтровать все элементы, не меньшие его. Алгоритм использует разбиение массива вокруг опорного элемента (`pivot`) и рекурсивно спускается только в нужную часть. В худшем случае сложность `O(n²)`, но на практике с рандомизацией опоры почти всегда работает за `O(n)`.

```java
static int quickSelect(int[] arr, int k, int left, int right) {
    int pivotIndex = ThreadLocalRandom.current().nextInt(left, right + 1);
    int pivot = arr[pivotIndex];
    int i = left, j = right;
    while (i <= j) {
        while (arr[i] > pivot) i++;   // ищем элементы меньше опоры справа
        while (arr[j] < pivot) j--;   // ищем элементы больше опоры слева
        if (i <= j) {
            int tmp = arr[i]; arr[i] = arr[j]; arr[j] = tmp;
            i++; j--;
        }
    }
    int leftSize = j - left + 1; // сколько элементов > pivot слева
    if (k <= leftSize) {
        return quickSelect(arr, k, left, j);
    } else if (k <= i - left) {
        return pivot; // pivot — k-й по величине
    } else {
        return quickSelect(arr, k - (i - left), i, right);
    }
}

// Получить top-k после QuickSelect:
List<Integer> topKWithQuickSelect(int[] arr, int k) {
    int threshold = quickSelect(arr, k, 0, arr.length - 1);
    return Arrays.stream(arr)
        .filter(v -> v >= threshold)
        .boxed()
        .sorted(Comparator.reverseOrder())
        .limit(k)
        .toList();
}
```

```kotlin
fun quickSelect(arr: IntArray, k: Int, left: Int, right: Int): Int {
    var l = left
    var r = right
    val pivot = arr[(l + r) / 2]
    while (l <= r) {
        while (arr[l] > pivot) l++
        while (arr[r] < pivot) r--
        if (l <= r) {
            val tmp = arr[l]; arr[l] = arr[r]; arr[r] = tmp
            l++; r--
        }
    }
    val leftSize = r - left + 1
    return when {
        k <= leftSize -> quickSelect(arr, k, left, r)
        k <= l - left -> pivot
        else -> quickSelect(arr, k - (l - left), l, right)
    }
}

fun topKWithQuickSelect(arr: IntArray, k: Int): List<Int> {
    val threshold = quickSelect(arr, k, 0, arr.lastIndex)
    return arr.filter { it >= threshold }
        .sortedDescending()
        .take(k)
}
```

### Сортировка и частичный срез

Простой подход: отсортировать массив по убыванию и взять первые `k` элементов. Это `O(n log n)` по времени и `O(n)` по памяти, но код минимален и подходит для небольших наборов данных или когда простота важнее производительности.

## Сравнение подходов

| Характеристика | Грубая сила | `TreeSet` | `PriorityQueue` |
|----------------|-------------|---------|---------------|
| Временная сложность | `O(n · k)` | `O(n · log n)` | `O(n · log k)` |
| Пространственная сложность | `O(n)` | `O(n)` | `O(k)` |
| Работает с дубликатами | Да | Нет | Да |
| Эффективность | Низкая | Средняя | Высокая |
| Применение | Малые массивы | Уникальные значения | Общий случай |

## Сложность

Подход грубой силы требует `O(n · k)` времени, потому что для каждого из `k` шагов ищется максимум среди оставшихся элементов, и `O(n)` памяти при копировании входа. Решение с `TreeSet` вставляет все элементы с `log n` на операцию, давая `O(n · log n)` времени и `O(n)` памяти, но теряет дубликаты. Алгоритм на `PriorityQueue` ограничивает размер кучи `k`, работает за `O(n · log k)` и занимает `O(k)` памяти, так как хранит только верхушку значений.

## Особенности

Подход с `PriorityQueue` хорошо масштабируется: время растёт логарифмически от `k`, а память ограничена размером кучи. `TreeSet` даёт естественную сортировку, но «съедает» дубликаты и занимает память на весь вход. Грубая сила проста, но плохо переносится на большие `n`. Все подходы опираются на доступ к элементам за `O(1)`; на потоковых источниках min-heap остаётся предпочтительным.

## Применение

Поиск `top-k` нужен в ранжировании результатов поиска, рекомендательных системах, аналитических отчётах (топ популярных значений), статистике, задачах машинного обучения, где выбирают k лучших признаков или кандидатов. В потоковых сценариях min-heap позволяет поддерживать «скользящую» верхушку без пересортировки данных.

## Когда использовать

Грубая сила уместна для крошечных массивов и очень маленьких `k` (например, `k = 1–2`), когда важна предельная простота и читаемость. `TreeSet` выбирайте, если нужны уникальные значения и гарантированно отсортированный результат «на лету» для массивов среднего размера. `PriorityQueue` — вариант по умолчанию для больших массивов и потоковых данных: `k` обычно существенно меньше `n`, а память и время остаются ограниченными.

## Лучшие практики

Для большинства задач выбирайте min-heap (`PriorityQueue`) фиксированного размера `k`: время `O(n · log k)` и память `O(k)` дают лучший баланс. Если требуется строго уникальный набор и сразу отсортированный результат, применяйте `TreeSet`, но заранее зафиксируйте, что дубликаты будут отброшены. Поддерживайте инвариант размера кучи: при вставке лишнего элемента сразу удаляйте минимум, иначе итог не будет корректным. В контракте явно оговорите поведение при `k > n`, при нулевом или отрицательном `k`, а также для пустого массива. Тесты должны покрывать `k = 1`, `k = n`, случаи с дубликатами и равными элементами, а при необходимости — проверять порядок вывода.

## Проверка входа и контракты

Перед вычислениями убедитесь, что `k` лежит в диапазоне `1..n`. Если `k` больше размера массива, решите, возвращаете ли весь массив или кидаете исключение — это должно быть задокументировано. Для потоковых данных проверьте, что источники не бесконечны без ограничений, иначе хранение даже `k` элементов может быть проблемой. Для обобщённых типов всегда задавайте явный `Comparator`, чтобы избежать неоднозначного сравнения и `ClassCastException`.

## Профилирование и масштабирование

При росте `n` профилирование покажет, что доминирует количество операций сравнения. Для min-heap это `n · log k`, поэтому уменьшение `k` даёт наибольший выигрыш. Если данные уже отсортированы или почти отсортированы, частичная сортировка может быть быстрее кучи из‑за кэш‑локальности. На больших массивах и малых `k` min-heap почти всегда обгоняет сортировку. Используйте `JMH` или аналогичные инструменты, чтобы померить реальные цифры на ваших данных: распределение значений, количество дубликатов и форма входа заметно влияют на выбор.

Память и GC: min-heap с маленьким `k` снижает давление на кучу JVM, но не забывайте про боковые структуры (копии массива при сортировке, временные списки при конвертациях потоков). В горячих путях избегайте лишних автобоксингов: работайте с `int[]` и специализированными кучами, если профилирование показывает узкие места.

Для потоковой обработки полезно ограничивать ширину окна: поддерживать top-k на скользящем окне требует удаления устаревших элементов; для этого добавляют счётчики частот или используют структурированные окна (например, в `Flink` или `Kafka Streams`), где min-heap встроен в оператор.

## Тестирование

### Юнит-тесты на `JUnit`

```java
@Test
void topK_handlesSmallK() {
    List<Integer> input = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
    assertThat(findTopK(input, 1)).containsExactly(9);
    assertThat(findTopKWithPriorityQueue(input, 3)).containsExactlyInAnyOrder(9, 6, 5);
}

@Test
void topK_respectsDuplicatesWithPriorityQueue() {
    List<Integer> input = Arrays.asList(5, 5, 4, 4, 3);
    assertThat(findTopKWithPriorityQueue(input, 3)).containsExactlyInAnyOrder(5, 5, 4);
}

@Test
void topK_treesetDropsDuplicates() {
    List<Integer> input = Arrays.asList(5, 5, 4, 4, 3);
    assertThat(findTopKWithTreeSet(input, 3)).containsExactly(5, 4, 3);
}
```

### Kotlin-тесты

```kotlin
class TopKTest {
    @Test
    fun `priority queue keeps k largest`() {
        val input = listOf(3, 1, 4, 1, 5, 9, 2, 6)
        val result = findTopKWithPriorityQueue(input, 3)
        assertThat(result).containsExactlyInAnyOrder(9, 6, 5)
    }

    @Test
    fun `treeset removes duplicates`() {
        val input = listOf(5, 5, 4, 3)
        val result = findTopKWithTreeSet(input, 3)
        assertThat(result).containsExactly(5, 4, 3)
    }
}
```

### Property-based тест

```kotlin
@Property
fun `priority queue matches sorting`(@ForAll @Size(min = 1, max = 200) data: List<Int>,
                                    @ForAll @IntRange(min = 1, max = 200) kRaw: Int) {
    val k = kRaw.coerceAtMost(data.size)
    val heapResult = findTopKWithPriorityQueue(data, k).sortedDescending()
    val sorted = data.sortedDescending().take(k)
    assertThat(heapResult).containsExactlyElementsOf(sorted)
}
```

### Перфоманс‑скетч с `JMH`

```java
@State(Scope.Benchmark)
public class TopKBench {
    int[] arr;
    int k;

    @Setup
    public void setup() {
        arr = ThreadLocalRandom.current()
            .ints(1_000_000, 0, 10_000_000)
            .toArray();
        k = 100;
    }

    @Benchmark
    public List<Integer> pq() {
        return findTopKWithPriorityQueue(Arrays.stream(arr).boxed().toList(), k);
    }

    @Benchmark
    public List<Integer> quickSelect() {
        int[] copy = Arrays.copyOf(arr, arr.length);
        return topKWithQuickSelect(copy, k);
    }
}
```

Такой бенчмарк показывает, где min-heap начинает обгонять сортировку и насколько QuickSelect выигрывает на больших объёмах.

## Решение проблем

- Результат короче `k`. Проверьте, не отбросил ли `TreeSet` дубликаты; если дубликаты нужны, используйте `PriorityQueue` или сортировку.
- Неверный порядок вывода. Помните, что `PriorityQueue` не гарантирует порядок при преобразовании в список; для убывающего порядка разверните результат или используйте сортировку полученного списка.
- `k` больше размера массива. Явно опишите поведение: вернуть все элементы, выбросить исключение или обрезать `k`. Несогласованный контракт ведёт к ошибкам в проде.
- Производительность низкая. Убедитесь, что не сортируете весь массив при больших `n` и маленьком `k`. Мин-heap с ограничением размера `k` даст логарифмический рост.
- Потеря дубликатов. Это нормальное поведение `TreeSet`; переключитесь на `PriorityQueue`, если счёт повторов критичен.
- Слишком большое выделение памяти. Проверяйте, не копируете ли массивы лишний раз; при необходимости работайте с потоками и мин-heap, чтобы не хранить всё сразу.
- QuickSelect портит вход. Алгоритм меняет порядок элементов; сделайте копию массива, если исходные данные должны сохраниться.

## Частые вопросы

**Нужно ли сортировать весь массив, если k мало?** Нет, min-heap на `k` элементов даст то же значение быстрее, чем `O(n log n)` сортировка.

**Можно ли обойтись без доп.памяти?** Почти нет: даже min-heap требует `O(k)` памяти. Для крайне ограниченной памяти можно использовать выбор без хранения (QuickSelect), но он изменяет массив.

**Что делать с потоковыми данными?** Держите min-heap фиксированного размера и обновляйте его по мере прихода элементов; это естественный способ поддерживать top-k на стриме.

**Как работать с пользовательским порядком?** Передавайте `Comparator` в `PriorityQueue` или используйте `Stream.sorted(comparator).limit(k)`, если важен порядок по полю объекта.

**Почему `TreeSet` не возвращает дубликаты?** Это свойство множества: ключи уникальны. Если нужно учитывать все вхождения, используйте кучу или сортировку.

**Можно ли смешивать разные типы?** Нет, для гетерогенных коллекций без общего сравнения получите `ClassCastException`. Всегда задавайте `Comparator` и используйте обобщённые типы.

**Что выбрать для небольших k и частых вызовов?** Min-heap предпочтителен: она кэширует только `k` элементов и быстро обновляется; сортировка будет дороже на каждом вызове.

**Нужно ли стабильное упорядочивание равных элементов?** Сортировка и сравнение с учётом индексов (см. пример со стабильностью) обеспечат предсказуемый порядок; `PriorityQueue` не гарантирует стабильность.

**Как обрабатывать отрицательные числа или `null`?** Для чисел логика не меняется; для `null` используйте `Comparator.nullsLast` или фильтруйте вход. В кучу нельзя добавлять `null` — получите `NullPointerException`.

**Что делать, если k динамически меняется?** Пересоздайте кучу под новое `k` или поддерживайте сортированный контейнер (`TreeSet`) и берите разные префиксы — это дороже по памяти, но гибче для изменяющихся запросов.

## Глоссарий

`k-th order statistics` — k-я порядковая статистика, элемент, стоящий на k-й позиции в отсортированной последовательности.
`Min Heap` — двоичная куча, в которой минимальный элемент находится в корне; удобно для удержания верхних значений с отсечением минимума.
`Max Heap` — куча с максимумом в корне; для top-k обычно менее эффективна, чем min-heap фиксированного размера.
`Comparator` — функция сравнения, определяющая порядок элементов в структурах данных.
`Deduplication` — удаление дубликатов; для `TreeSet` происходит автоматически.
`Streaming top-k` — поддержание k наибольших элементов в потоке данных без полной сортировки.

## Дополнительные примеры использования

### Пример 1: Потоки и Comparator

```java
record Person(String name, int score) {}

List<Person> topByScore(List<Person> people, int k) {
    return people.stream()
        .sorted(Comparator.comparingInt(Person::score).reversed())
        .limit(k)
        .toList();
}
```

### Пример 2: Поддержка стабильного порядка

```java
List<Integer> topKStable(List<Integer> input, int k) {
    return IntStream.range(0, input.size())
        .boxed()
        .sorted(Comparator
            .<Integer>comparingInt(input::get).reversed()
            .thenComparingInt(i -> i)) // стабильность по индексу
        .limit(k)
        .map(input::get)
        .toList();
}
```

### Пример 3: Фильтрация дубликатов

```java
List<Integer> topKDistinct(List<Integer> input, int k) {
    return input.stream()
        .collect(Collectors.toCollection(
            () -> new TreeSet<>(Comparator.reverseOrder())))
        .stream()
        .limit(k)
        .toList();
}
```

### Пример 4: Несколько отсортированных массивов

```java
List<Integer> topKFromSorted(List<int[]> inputs, int k) {
    record Entry(int value, int arrIdx, int pos) {}
    PriorityQueue<Entry> heap = new PriorityQueue<>(Comparator.comparingInt(e -> e.value));
    for (int i = 0; i < inputs.size(); i++) {
        int[] arr = inputs.get(i);
        if (arr.length > 0) {
            heap.add(new Entry(arr[arr.length - 1], i, arr.length - 1));
        }
    }
    List<Integer> result = new ArrayList<>();
    while (!heap.isEmpty() && result.size() < k) {
        Entry e = heap.poll();
        result.add(e.value);
        if (e.pos > 0) {
            int[] arr = inputs.get(e.arrIdx);
            heap.add(new Entry(arr[e.pos - 1], e.arrIdx, e.pos - 1));
        }
    }
    return result;
}
```

Такой подход полезен, если исходные массивы уже отсортированы по возрастанию: двигаемся с конца, используя max-heap/мин-heap с инверсией, и собираем top-k без полной слияния.

### Пример 5: Counting для ограниченного диапазона

Если значения лежат в узком диапазоне (например, баллы 0–100), можно использовать counting‑массив и пройти диапазон в обратном порядке:

```java
List<Integer> topKCounting(int[] arr, int k) {
    int max = 100; // известный максимум
    int[] freq = new int[max + 1];
    for (int v : arr) freq[v]++;
    List<Integer> result = new ArrayList<>();
    for (int v = max; v >= 0 && result.size() < k; v--) {
        while (freq[v]-- > 0 && result.size() < k) {
            result.add(v);
        }
    }
    return result;
}
```

Сложность `O(n + R)`, где `R` — ширина диапазона, и память `O(R)`. При малом `R` это быстрее сортировки и кучи.

### Пример 6: SQL window top-k

```sql
SELECT value
FROM (
  SELECT value,
         ROW_NUMBER() OVER (ORDER BY value DESC) AS rn
  FROM measurements
) t
WHERE rn <= :k;
```

Оконные функции позволяют получить top-k прямо в базе без выгрузки всех данных в приложение; полезно, когда объём данных велик, а сети — узкое место.

## Заключение

Мы рассмотрели несколько подходов к поиску k наибольших элементов: от простого частичного сортирования до min-heap, `TreeSet` и `QuickSelect`. Для большинства практических задач оптимален min-heap на `k` элементов: он сочетает линейный проход с логарифмической вставкой и ограниченной памятью. `TreeSet` полезен, если нужны уникальные значения и мгновенная сортировка, а `QuickSelect` — когда важна линейная средняя сложность и допустима модификация массива. Чёткий контракт на дубликаты, порядок вывода и поведение при граничных значениях `k` делает решение предсказуемым и устойчивым в продакшене.

При внедрении в продовые сервисы держите под рукой чек-лист: контракт на вход и `k`, выбор структуры данных под распределение значений, тесты на граничные случаи и бенчмарки на реальных данных. Если данные приходят потоками, добавьте метрики по размеру кучи и времени вставки, чтобы отслеживать регрессии.

Для дальнейшего чтения: изучите `Selection Algorithms`, `Top-K Frequent Elements`, «Streaming algorithms» (например, `Count-Min Sketch`) и структуры типа `BoundedPriorityQueue`. Они пригодятся, если потребуется масштабировать вычисления на распределённые системы или оптимизировать потребление памяти ещё сильнее.

Поддерживайте этот файл актуальным: обновляйте дату, когда меняется код или рекомендации, и фиксируйте контракт по дубликатам и порядку — это сократит число регрессий в будущих аудитах.
