---
title: "Слияние отсортированных последовательностей (Merge Sorted Sequences)"
description: "Эффективное объединение нескольких отсортированных массивов в один отсортированный результат выполняется с помощью минимальной кучи (min-heap) за время O(K log N), где K — общее число элементов во всех массивах, N — количество массивов. Алгоритм применяется во внешней сортировке,"
tags:
  - algorithms
  - searching
  - merge-sorted-sequences
type: "reference"
difficulty: "intermediate"
aliases:
  - "Merge Sorted Sequences"
prerequisites:
  - "[[merge-sort]]"
next:
  - "[[merge-two-sorted-arrays]]"
updated: "2026-04-20"
---
# Слияние отсортированных последовательностей (Merge Sorted Sequences)

Эффективное объединение нескольких отсортированных массивов в один отсортированный результат выполняется с помощью минимальной кучи (min-heap) за время `O(K log N)`, где K — общее число элементов во всех массивах, N — количество массивов. Алгоритм применяется во внешней сортировке, в `Merge Sort` при слиянии k подмассивов, в потоковой обработке и при объединении результатов из нескольких источников. В документе рассмотрены реализация на Java с кастомной кучей и `PriorityQueue`, реализация на Kotlin, сравнение подходов и практические рекомендации.

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Merge K sorted arrays](https://www.geeksforgeeks.org/merge-k-sorted-arrays/)

### См. также
- [Объединение двух отсортированных массивов](merge-two-sorted-arrays.md) — merge two
- [K-й элемент в двух отсортированных массивах](kth-smallest-in-two-sorted-arrays.md) — k-th smallest
- [Сортировка слиянием](../sorting/merge-sort.md) — merge sort

- [Максимальный подмассив (Maximum Subarray Problem)](maximum-subarray.md)
- [k наибольших элементов (K Largest Elements)](k-largest-elements.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Принцип работы](#принцип-работы)
  - [Визуализация min-heap](#визуализация-min-heap)
  - [Пример](#пример)
- [Реализация](#реализация)
  - [Класс HeapNode](#класс-heapnode)
  - [Класс MinHeap](#класс-minheap)
  - [Метод heapify](#метод-heapify)
  - [Метод merge](#метод-merge)
  - [Полная реализация](#полная-реализация)
  - [Тестирование](#тестирование)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Класс HeapNode (Kotlin)](#класс-heapnode-kotlin)
  - [Класс MinHeap (Kotlin)](#класс-minheap-kotlin)
  - [Пример использования](#пример-использования)
- [Сложность](#сложность)
  - [Временная сложность](#временная-сложность)
  - [Пространственная сложность](#пространственная-сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Альтернативные подходы](#альтернативные-подходы)
  - [Использование PriorityQueue](#использование-priorityqueue)
- [Сравнение подходов](#сравнение-подходов)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий терминов](#глоссарий-терминов)
- [Дополнительные примеры использования](#дополнительные-примеры-использования)
- [Когда использовать](#когда-использовать)
- [Заключение](#заключение)

## Описание алгоритма

В этом коротком руководстве мы увидим, как можно эффективно объединить отсортированные массивы с помощью кучи.

Min-heap обычно реализуется массивом: родитель и потомки вычисляются по индексу (см. ниже).

## Принцип работы

Для массива `A[]` и элемента с индексом `i`: родитель — `A[(i-1)/2]`, левый потомок — `A[2*i+1]`, правый — `A[2*i+2]`.

### Визуализация min-heap

```text
        1
      /   \
     3     2
    / \   / \
   5   4 6   7

Массив: [1, 3, 2, 5, 4, 6, 7]
```

Алгоритм объединения набора отсортированных массивов выглядит так. Создаётся массив результатов размером, равным сумме длин всех входных массивов, и массив узлов кучи размером по количеству массивов, заполненный первыми элементами каждого массива. Этот массив узлов преобразуется в min-heap (вызов `heapify` от родителя последнего листа до корня). Далее повторяем: извлекаем корень кучи и записываем его в результат; заменяем корень следующим элементом из того же массива (или «бесконечностью», если массив исчерпан); вызываем `heapify(0)`, чтобы восстановить свойство кучи. Каждый элемент обрабатывается один раз, операция с кучей — `O(log n)`, итого временная сложность `O(K log N)`, где K — общее число элементов, N — количество массивов.

### Пример

Для входных массивов:

```text
{ { 0, 6 }, { 1, 5, 10, 100 }, { 2, 4, 200, 650 } }
```

алгоритм возвращает один отсортированный массив:

```text
{ 0, 1, 2, 4, 5, 6, 10, 100, 200, 650 }
```

## Реализация

Ниже приведена реализация на Java: класс для узла кучи (`HeapNode`) и класс для min-heap с методом слияния.

### Класс HeapNode

Узел кучи хранит текущий элемент, индекс массива, из которого он взят, и индекс следующего элемента в этом массиве (`nextElementIndex`). Изначально `nextElementIndex = 1`; после переноса корня в результат корень заменяется следующим элементом того же массива, и индекс увеличивается.

```java
// Узел min-heap для слияния k отсортированных последовательностей:
// element — текущий элемент, arrayIndex — номер массива, nextElementIndex — следующий индекс в нём
public class HeapNode {
    int element;
    int arrayIndex;
    int nextElementIndex = 1;

    public HeapNode(int element, int arrayIndex) {
        this.element = element;
        this.arrayIndex = arrayIndex;
    }
}
```

### Класс MinHeap

```java
// Min-heap из узлов HeapNode: индексация родителя/потомков, getRootNode возвращает минимум
public class MinHeap {
    HeapNode[] heapNodes;

    public MinHeap(HeapNode heapNodes[]) {
        this.heapNodes = heapNodes;
        heapifyFromLastLeafsParent();
    }

    int getParentNodeIndex(int index) {
        return (index - 1) / 2;
    }

    int getLeftNodeIndex(int index) {
        return (2 * index + 1);
    }

    int getRightNodeIndex(int index) {
        return (2 * index + 2);
    }

    HeapNode getRootNode() {
        return heapNodes[0];
    }
}
```

### Метод heapify

Метод восстанавливает свойство min-heap для поддерева с корнем в заданном индексе: сравнивает узел с левым и правым потомками, при необходимости меняет местами с меньшим из них и рекурсивно вызывает себя для поддерева.

```java
// Восстановление свойства min-heap для поддерева с корнем index; рекурсия при обмене
void heapify(int index) {
    int leftNodeIndex = getLeftNodeIndex(index);
    int rightNodeIndex = getRightNodeIndex(index);
    int smallestElementIndex = index;

    if (leftNodeIndex < heapNodes.length
            && heapNodes[leftNodeIndex].element < heapNodes[index].element) {
        smallestElementIndex = leftNodeIndex;
    }

    if (rightNodeIndex < heapNodes.length
            && heapNodes[rightNodeIndex].element < heapNodes[smallestElementIndex].element) {
        smallestElementIndex = rightNodeIndex;
    }

    if (smallestElementIndex != index) {
        swap(index, smallestElementIndex);
        heapify(smallestElementIndex);
    }
}

void swap(int i, int j) {
    HeapNode temp = heapNodes[i];
    heapNodes[i] = heapNodes[j];
    heapNodes[j] = temp;
}
```

При представлении кучи массивом последний лист в конце; строим кучу, вызывая `heapify` от родителя последнего листа до корня:

```java
void heapifyFromLastLeafsParent() {
    int lastLeafsParentIndex = getParentNodeIndex(heapNodes.length);

    while (lastLeafsParentIndex >= 0) {
        heapify(lastLeafsParentIndex);
        lastLeafsParentIndex--;
    }
}
```

### Метод merge

Метод сначала создаёт массив узлов кучи из первых элементов каждого входного массива и вычисляет суммарный размер результата. Затем в цикле извлекает корень кучи, записывает его в результат, заменяет корень следующим элементом того же массива (или `Integer.MAX_VALUE`, если массив исчерпан) и вызывает `heapify(0)`.

```java
int[] merge(int[][] array) {
    HeapNode[] heapNodes = new HeapNode[array.length];
    int resultingArraySize = 0;

    for (int i = 0; i < array.length; i++) {
        HeapNode node = new HeapNode(array[i][0], i);
        heapNodes[i] = node;
        resultingArraySize += array[i].length;
    }
```

Цикл заполнения результата:

```java
    MinHeap minHeap = new MinHeap(heapNodes);
    int[] resultingArray = new int[resultingArraySize];

    for (int i = 0; i < resultingArraySize; i++) {
        HeapNode root = minHeap.getRootNode();
        resultingArray[i] = root.element;

        if (root.nextElementIndex < array[root.arrayIndex].length) {
            root.element = array[root.arrayIndex][root.nextElementIndex++];
        } else {
            root.element = Integer.MAX_VALUE;
        }

        minHeap.heapify(0);
    }

    return resultingArray;
}
```

### Полная реализация

```java
public class MinHeap {
    HeapNode[] heapNodes;

    public MinHeap(HeapNode heapNodes[]) {
        this.heapNodes = heapNodes;
        heapifyFromLastLeafsParent();
    }

    // ... методы getParentNodeIndex, getLeftNodeIndex, getRightNodeIndex, getRootNode ...

    void heapify(int index) {
        // ... реализация heapify ...
    }

    void heapifyFromLastLeafsParent() {
        // ... реализация heapifyFromLastLeafsParent ...
    }

    public static int[] merge(int[][] array) {
        // ... полная реализация merge ...
    }
}
```

### Тестирование

Проверка на тех же входных данных:

```java
int[][] inputArray = { { 0, 6 }, { 1, 5, 10, 100 }, { 2, 4, 200, 650 } };
int[] expectedArray = { 0, 1, 2, 4, 5, 6, 10, 100, 200, 650 };

int[] resultArray = MinHeap.merge(inputArray);

assertThat(resultArray.length, is(equalTo(10)));
assertThat(resultArray, is(equalTo(expectedArray)));
```

## Реализация на Kotlin

### Класс HeapNode (Kotlin)

```kotlin
// Узел кучи: элемент, индекс массива, индекс следующего элемента в нём
data class HeapNodeK(
    var element: Int,
    val arrayIndex: Int,
    var nextElementIndex: Int = 1
)
```

### Класс MinHeap (Kotlin)

```kotlin
// Min-heap для слияния k отсортированных массивов: индексация кучи и heapify
class MinHeapK(private val heapNodes: Array<HeapNodeK>) {
    init {
        heapifyFromLastLeafsParent()
    }

    private fun getParentNodeIndex(index: Int): Int = (index - 1) / 2
    private fun getLeftNodeIndex(index: Int): Int = 2 * index + 1
    private fun getRightNodeIndex(index: Int): Int = 2 * index + 2

    fun getRootNode(): HeapNodeK = heapNodes[0]

    private fun heapify(index: Int) {
        val leftNodeIndex = getLeftNodeIndex(index)
        val rightNodeIndex = getRightNodeIndex(index)
        var smallestElementIndex = index

        if (leftNodeIndex < heapNodes.size &&
            heapNodes[leftNodeIndex].element < heapNodes[index].element) {
            smallestElementIndex = leftNodeIndex
        }

        if (rightNodeIndex < heapNodes.size &&
            heapNodes[rightNodeIndex].element < heapNodes[smallestElementIndex].element) {
            smallestElementIndex = rightNodeIndex
        }

        if (smallestElementIndex != index) {
            swap(index, smallestElementIndex)
            heapify(smallestElementIndex)
        }
    }

    private fun swap(index1: Int, index2: Int) {
        val temp = heapNodes[index1]
        heapNodes[index1] = heapNodes[index2]
        heapNodes[index2] = temp
    }

    private fun heapifyFromLastLeafsParent() {
        val lastLeafsParentIndex = (heapNodes.size - 2) / 2
        for (index in lastLeafsParentIndex downTo 0) {
            heapify(index)
        }
    }

    fun replaceRoot(newRoot: HeapNodeK) {
        heapNodes[0] = newRoot
        heapify(0)
    }

    companion object {
        fun merge(inputArray: Array<IntArray>): IntArray {
            val totalSize = inputArray.sumOf { it.size }
            val result = IntArray(totalSize)

            val heapNodes = Array(inputArray.size) { i ->
                HeapNodeK(inputArray[i][0], i, 1)
            }

            val minHeap = MinHeapK(heapNodes)
            var resultIndex = 0

            while (resultIndex < totalSize) {
                val root = minHeap.getRootNode()
                result[resultIndex++] = root.element

                if (root.nextElementIndex < inputArray[root.arrayIndex].size) {
                    root.element = inputArray[root.arrayIndex][root.nextElementIndex]
                    root.nextElementIndex++
                    minHeap.replaceRoot(root)
                } else {
                    root.element = Int.MAX_VALUE
                    minHeap.replaceRoot(root)
                }
            }

            return result
        }
    }
}
```

### Пример использования

```kotlin
// Пример: слияние трёх отсортированных массивов в один
fun main() {
    val inputArray = arrayOf(
        intArrayOf(0, 6),
        intArrayOf(1, 5, 10, 100),
        intArrayOf(2, 4, 200, 650)
    )

    val resultArray = MinHeapK.merge(inputArray)
    println(resultArray.contentToString())
    // [0, 1, 2, 4, 5, 6, 10, 100, 200, 650]
}
```

## Сложность

### Временная сложность

Во всех случаях временная сложность составляет `O(K log N)`, где K — общее количество элементов во всех массивах, N — количество отсортированных массивов. Каждый элемент обрабатывается один раз; для каждого выполняется извлечение минимума из кучи и возможная вставка следующего элемента — операции с кучей размера N занимают `O(log N)`.

### Пространственная сложность

Требуется `O(N)` дополнительной памяти для хранения кучи узлов — по одному узлу на каждый массив.

## Особенности

Алгоритм даёт оптимальную временную сложность `O(K log N)` для объединения N отсортированных массивов с общим числом элементов K. Дополнительная память — только `O(N)` для кучи узлов. Подход работает при любом количестве массивов и не требует предварительного знания длин.

## Применение

Слияние отсортированных последовательностей используется в сортировке слиянием (`Merge Sort`) при объединении отсортированных подмассивов; во внешней сортировке (`External Sorting`) при слиянии отсортированных чанков файлов; в `MapReduce` при объединении результатов с разных узлов; в потоковой обработке (`Stream Processing`) при слиянии отсортированных потоков; в операциях с БД при объединении результатов нескольких отсортированных запросов.

## Альтернативные подходы

### Использование PriorityQueue

Более простая реализация на стандартной `PriorityQueue` с компаратором по полю `element`:

```java
// Слияние k массивов через стандартную PriorityQueue с компаратором по element
public static int[] mergeWithPriorityQueue(int[][] arrays) {
    PriorityQueue<HeapNode> minHeap = new PriorityQueue<>(
        (a, b) -> Integer.compare(a.element, b.element)
    );

    int totalSize = 0;
    for (int i = 0; i < arrays.length; i++) {
        if (arrays[i].length > 0) {
            minHeap.offer(new HeapNode(arrays[i][0], i));
            totalSize += arrays[i].length;
        }
    }

    int[] result = new int[totalSize];
    int index = 0;

    while (!minHeap.isEmpty()) {
        HeapNode node = minHeap.poll();
        result[index++] = node.element;

        if (node.nextElementIndex < arrays[node.arrayIndex].length) {
            node.element = arrays[node.arrayIndex][node.nextElementIndex++];
            minHeap.offer(node);
        }
    }

    return result;
}
```

## Сравнение подходов

| Характеристика | Кастомная MinHeap | PriorityQueue |
|----------------|-------------------|---------------|
| Временная сложность | `O(K log N)` | `O(K log N)` |
| Пространственная сложность | `O(N)` | `O(N)` |
| Простота реализации | Сложнее | Проще |
| Контроль | Полный | Ограниченный |

## Лучшие практики

Для объединения N отсортированных последовательностей используйте min-heap (или `PriorityQueue`) — сложность `O(K log N)` вместо последовательного слияния по два, которое даёт худшую асимптотику. Пустые массивы отфильтровывайте до построения кучи. В узле кучи храните индекс массива и индекс следующего элемента, чтобы не копировать данные. Для большинства задач достаточно `PriorityQueue`; кастомная min-heap нужна при особых требованиях к памяти или к порядку операций.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Результат не отсортирован | Неверное восстановление кучи после замены корня | После замены корня вызывать `heapify(0)`; проверять индексы `nextElementIndex` и `arrayIndex` |
| ArrayIndexOutOfBoundsException | Выход за границу массива при доступе к следующему элементу | Проверять `nextElementIndex < array[arrayIndex].length` перед доступом; для исчерпанного массива подставлять `Integer.MAX_VALUE` |
| NullPointerException | Пустой или null массив среди входных | До построения кучи отфильтровать пустые массивы; проверять `arrays[i].length > 0` |
| Неверный размер результата | Размер результата не равен сумме длин массивов | Вычислять `resultingArraySize` как сумму `array[i].length` по всем массивам |

## Частые вопросы

**Когда использовать min-heap, а когда слияние по два?** При N массивах слияние по два даёт сложность порядка `O(K*N)`; min-heap даёт `O(K log N)` и выгоден уже при N > 2. Для двух массивов достаточно двух указателей без кучи.

**Нужно ли копировать элементы в узлы кучи?** Достаточно хранить в узле текущее значение, индекс массива и позицию следующего элемента; при замене корня просто обновляйте значение и индекс, не копируя весь массив.

**Как обрабатывать пустые массивы?** Не добавлять их в кучу: при инициализации учитывать только массивы с `length > 0`, иначе размер кучи и индексы собьются.

**Подходит ли алгоритм для потоков (streams)?** Да, если из каждого потока можно получать следующий элемент по требованию — тогда в кучи хранятся текущие «головы» потоков, как и для массивов.

## Глоссарий терминов

- **Min-heap (минимальная куча)** — бинарное дерево (часто в виде массива), в котором значение каждого узла не больше значений его потомков; минимум всегда в корне.
- **Heapify** — процедура восстановления свойства кучи для поддерева с заданным корнем; при нарушении порядка узел меняется местами с меньшим потомком и процедура вызывается рекурсивно.
- **PriorityQueue** — очередь с приоритетом; в Java реализация обычно на основе кучи, подходит для min-heap при задании компаратора по элементу.
- **K-way merge** — слияние K отсортированных последовательностей в одну; при использовании min-heap сложность `O(K log N)` по времени, где N — число последовательностей.

## Дополнительные примеры использования

Объединение логов с нескольких серверов, отсортированных по времени. Слияние отсортированных результатов полнотекстового поиска из нескольких индексов. Финальная фаза внешней сортировки: слияние отсортированных чанков файлов. Объединение событий из нескольких источников в один поток по временной метке.

## Когда использовать

Используйте слияние k отсортированных последовательностей через min-heap, когда массивов или потоков больше двух и нужен один отсортированный результат за время `O(K log N)`. Для двух массивов достаточно алгоритма с двумя указателями без кучи. Не используйте кучу для неотсортированных данных — сначала сортировка, затем слияние.

## Заключение

В документе рассмотрено эффективное объединение нескольких отсортированных массивов с помощью min-heap. Алгоритм даёт временную сложность `O(K log N)` и пространственную `O(N)`, что оптимально для задачи k-way merge. Приведены реализации на Java (кастомная куча и `PriorityQueue`) и на Kotlin, сравнение подходов, обработка граничных случаев и рекомендации по выбору структуры данных. Понимание алгоритма полезно для внешней сортировки, потоковой обработки и объединения результатов из нескольких источников.
