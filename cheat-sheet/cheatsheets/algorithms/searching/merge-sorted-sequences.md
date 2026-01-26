# Merge Sorted Sequences

Кратко: эффективное объединение нескольких отсортированных массивов с помощью минимальной кучи (min-heap). Временная сложность O(k*log(n)), где k - общее количество элементов, n - количество массивов.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Merge K sorted arrays](https://www.geeksforgeeks.org/merge-k-sorted-arrays/)

### См. также
- `./merge-two-sorted-arrays.md` - объединение двух отсортированных массивов
- `./kth-smallest-in-two-sorted-arrays.md` - k-й элемент в двух отсортированных массивах
- `../sorting/merge-sort.md` - алгоритм сортировки слиянием

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Принцип работы](#принцип-работы)
- [Реализация](#реализация)
- [Сложность](#сложность)

## Описание алгоритма

В этом коротком руководстве мы увидим, как можно эффективно объединить отсортированные массивы с помощью кучи.

Поскольку наша постановка задачи заключается в использовании кучи для объединения массивов, мы будем использовать мини-кучу для решения нашей проблемы. Минимальная куча - это не что иное, как бинарное дерево, в котором значение каждого узла меньше значений его дочерних узлов.

Обычно min-heap реализуется с использованием массива, в котором массив удовлетворяет определенным правилам, когда речь идет о поиске родителя и потомка узла.

## Принцип работы

Для массива A[] и элемента с индексом i:

1. `A[(i - 1)/2]` вернет своего родителя
2. `A[(2*i)+1]` вернет левого дочернего элемента
3. `A[(2*i)+2]` вернет правильный дочерний элемент

### Визуализация min-heap

```
        1
      /   \
     3     2
    / \   / \
   5   4 6   7

Массив: [1, 3, 2, 5, 4, 6, 7]
```

Давайте теперь создадим наш алгоритм, который объединяет набор отсортированных массивов:

1. Создайте массив для хранения результатов, размер которого определяется путем сложения длины всех входных массивов
2. Создайте второй массив размером, равным количеству входных массивов, и заполните его первыми элементами всех входных массивов
3. Преобразуйте ранее созданный массив в мини-кучу, применив правила минимальной кучи ко всем узлам и их дочерним элементам
4. Повторяйте следующие шаги, пока массив результатов не будет полностью заполнен:
   - Получите корневой элемент из минимальной кучи и сохраните его в массиве результатов
   - Замените корневой элемент следующим элементом из массива, в котором находится текущий корень
   - Снова примените правило минимальной кучи к нашему массиву минимальной кучи

В нашем алгоритме есть рекурсивный поток для создания минимальной кучи, и мы должны посетить все элементы входных массивов.

Временная сложность этого алгоритма равна O(k*log(n)), где k - общее количество элементов во всех входных массивах, а n - общее количество отсортированных массивов.

### Пример

Давайте теперь посмотрим пример ввода и ожидаемый результат после запуска алгоритма, чтобы мы могли лучше понять проблему. Итак, для этих массивов:

```java
{ { 0, 6 }, { 1, 5, 10, 100 }, { 2, 4, 200, 650 } }
```

Алгоритм должен вернуть массив результатов:

```java
{ 0, 1, 2, 4, 5, 6, 10, 100, 200, 650 }
```

## Реализация

Теперь, когда у нас есть общее представление о том, что такое мини-куча и как работает алгоритм слияния, давайте посмотрим на реализацию Java. Мы будем использовать два класса - один для представления узлов кучи, а другой для реализации алгоритма слияния.

### Класс HeapNode

Перед реализацией самого алгоритма создадим класс, представляющий узел кучи. Это сохранит значение узла и два вспомогательных поля:

```java
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

Обратите внимание, что мы намеренно опустили здесь геттеры и сеттеры, чтобы упростить задачу. Мы будем использовать свойство `arrayIndex` для хранения индекса массива, в котором берется текущий элемент узла кучи. И мы будем использовать свойство `nextElementIndex` для хранения индекса элемента, который мы возьмем после перемещения корневого узла в результирующий массив.

Изначально значение `nextElementIndex` будет равно 1. Мы будем увеличивать его значение после замены корневого узла min-heap.

### Класс MinHeap

Наш следующий класс должен представлять саму мини-кучу и реализовывать алгоритм слияния:

```java
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

Теперь, когда мы создали наш класс min-heap, давайте добавим метод, который будет увеличивать поддерево, где корневой узел поддерева находится в заданном индексе массива:

```java
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

Когда мы используем массив для представления минимальной кучи, последний листовой узел всегда будет в конце массива. Таким образом, при преобразовании массива в мини-кучу путем итеративного вызова метода `heapify()` нам нужно только начать итерацию с родительского узла последнего листа:

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

Наш следующий метод будет выполнять фактическую реализацию нашего алгоритма. Для лучшего понимания давайте разделим метод на две части и посмотрим, как он работает:

Первая часть преобразует входные массивы в массив узлов кучи, который содержит все элементы первого массива, и находит размер результирующего массива:

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

И следующая часть заполняет массив результатов, реализуя шаги 4, 5, 6 и 7 нашего алгоритма:

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

Давайте теперь протестируем наш алгоритм с теми же входными данными, которые мы упоминали ранее:

```java
int[][] inputArray = { { 0, 6 }, { 1, 5, 10, 100 }, { 2, 4, 200, 650 } };
int[] expectedArray = { 0, 1, 2, 4, 5, 6, 10, 100, 200, 650 };

int[] resultArray = MinHeap.merge(inputArray);

assertThat(resultArray.length, is(equalTo(10)));
assertThat(resultArray, is(equalTo(expectedArray)));
```

## Kotlin Implementation

### Класс HeapNode

```kotlin
data class HeapNodeK(
    var element: Int,
    val arrayIndex: Int,
    var nextElementIndex: Int = 1
)
```

### Класс MinHeap

```kotlin
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

- **Все случаи:** O(k*log(n)), где:
  - k - общее количество элементов во всех массивах
  - n - количество отсортированных массивов

Каждый элемент обрабатывается один раз, и для каждого элемента выполняется операция вставки/удаления в кучу размера n, что занимает O(log(n)).

### Пространственная сложность

- **Все случаи:** O(n) - для хранения кучи узлов

Куча содержит максимум n узлов (по одному на каждый массив).

## Особенности

- **Эффективность:** Оптимальное решение для объединения k отсортированных массивов
- **Память:** Использует только O(n) дополнительной памяти
- **Гибкость:** Работает с любым количеством массивов

## Применение

Объединение отсортированных последовательностей используется в:

- **Merge Sort** - объединение отсортированных подмассивов
- **External Sorting** - сортировка больших файлов
- **MapReduce** - объединение результатов из разных узлов
- **Stream Processing** - объединение отсортированных потоков данных
- **Database Operations** - объединение результатов запросов

## Альтернативные подходы

### Использование PriorityQueue

Более простая реализация с использованием стандартной PriorityQueue:

```java
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
| Временная сложность | O(k*log(n)) | O(k*log(n)) |
| Пространственная сложность | O(n) | O(n) |
| Простота реализации | Сложнее | Проще |
| Контроль | Полный | Ограниченный |

## Заключение

В этом уроке мы узнали, как эффективно объединять отсортированные массивы с помощью min-heap. Алгоритм обеспечивает оптимальную временную сложность O(k*log(n)) и эффективное использование памяти O(n), что делает его идеальным решением для объединения множества отсортированных последовательностей.
