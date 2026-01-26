# K Largest Elements

Кратко: поиск k самых больших элементов в массиве. Рассматриваются три подхода: грубая сила O(n*k), TreeSet O(n*log(n)), и PriorityQueue O(n*log(k)).

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Find k largest elements in an array](https://www.geeksforgeeks.org/k-largestor-smallest-elements-in-an-array/)

### См. также
- `./find-max-element.md` - поиск k-го по величине элемента
- `./top-n-frequent-elements.md` - поиск n самых частых элементов

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом руководстве мы реализуем различные решения проблемы поиска k самых больших элементов в массиве с помощью Java. Для описания временной сложности мы будем использовать нотацию Big-O.

## Java Implementation

### Подход 1: Грубая сила

Грубое решение этой проблемы состоит в том, чтобы перебрать заданный массив k раз. На каждой итерации мы будем находить наибольшее значение. Затем мы удалим это значение из массива и поместим в выходной список:

```java
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

Если мы предположим, что n - это размер данного массива, временная сложность этого решения равна O(n * k). Кроме того, это самое неэффективное решение.

### Подход 2: TreeSet

Однако существуют более эффективные решения этой проблемы. В этом разделе мы объясним два из них с использованием коллекций Java.

TreeSet имеет структуру данных Red-Black Tree в качестве основы. В результате добавление значения в этот набор стоит O(log(n)). TreeSet - это отсортированная коллекция. Следовательно, мы можем поместить все значения в TreeSet и извлечь первые k из них:

```java
public List<Integer> findTopK(List<Integer> input, int k) {
    Set<Integer> sortedSet = new TreeSet<>(Comparator.reverseOrder());
    sortedSet.addAll(input);
    return sortedSet.stream().limit(k).collect(Collectors.toList());
}
```

Временная сложность этого решения составляет O(n*log(n)). Прежде всего, предполагается, что это более эффективно, чем метод грубой силы, если k ≥ log(n).

### Ограничения

Важно помнить, что TreeSet не содержит дубликатов. В результате решение работает только для входного массива с различными значениями.

### Пример использования

```java
List<Integer> input = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
List<Integer> top3 = findTopK(input, 3);
// Результат: [9, 6, 5] (дубликаты удалены)
```

### Подход 3: PriorityQueue (Min Heap)

PriorityQueue - это структура данных Heap в Java. С его помощью мы можем добиться решения O(n*log(k)). Более того, это будет более быстрое решение, чем предыдущее. Из-за указанной проблемы k всегда меньше размера массива. Итак, это означает, что O(n*log(k)) ≤ O(n*log(n)).

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

1. Создаем минимальную кучу (min heap)
2. Для каждого элемента массива:
   - Добавляем элемент в кучу
   - Если размер кучи превышает k, удаляем минимальный элемент
3. После обработки всех элементов куча содержит k наибольших элементов
4. Преобразуем кучу в список и разворачиваем его для получения убывающего порядка

### Пример использования

```java
List<Integer> input = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
List<Integer> top3 = findTopK(input, 3);
// Результат: [9, 6, 5]
```

### Альтернативная реализация с Max Heap

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

## Kotlin Implementation

В Kotlin алгоритм поиска k наибольших элементов может быть реализован следующим образом:

### Подход 1: Грубая сила

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

### Подход 2: TreeSet

```kotlin
fun findTopKWithTreeSet(input: List<Int>, k: Int): List<Int> {
    val sortedSet = input.toSortedSet(compareByDescending { it })
    return sortedSet.take(k)
}
```

### Подход 3: PriorityQueue (Min Heap)

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

### Альтернативная реализация с Max Heap

```kotlin
fun findTopKWithMaxHeap(input: List<Int>, k: Int): List<Int> {
    val maxHeap = PriorityQueue<Int>(compareByDescending { it })
    maxHeap.addAll(input)

    return (0 until minOf(k, maxHeap.size)).map { maxHeap.poll() }
}
```

## Сравнение подходов

| Характеристика | Грубая сила | TreeSet | PriorityQueue |
|----------------|-------------|---------|---------------|
| Временная сложность | O(n*k) | O(n*log(n)) | O(n*log(k)) |
| Пространственная сложность | O(n) | O(n) | O(k) |
| Работает с дубликатами | Да | Нет | Да |
| Эффективность | Низкая | Средняя | Высокая |
| Применение | Малые массивы | Уникальные значения | Общий случай |

## Сложность

### Подход 1: Грубая сила

- **Временная сложность:** O(n*k) - k итераций по n элементам
- **Пространственная сложность:** O(n) - копия массива

### Подход 2: TreeSet

- **Временная сложность:** O(n*log(n)) - вставка n элементов
- **Пространственная сложность:** O(n) - хранение всех элементов

### Подход 3: PriorityQueue

- **Временная сложность:** O(n*log(k)) - n операций вставки/удаления в кучу размера k
- **Пространственная сложность:** O(k) - хранение только k элементов

## Особенности

- **Эффективность:** PriorityQueue оптимален для больших массивов
- **Память:** PriorityQueue использует только O(k) памяти
- **Гибкость:** Работает с дубликатами и любыми типами данных

## Применение

Поиск k наибольших элементов используется в:

- Ранжировании результатов поиска
- Рекомендательных системах
- Анализе данных (топ-k запросы)
- Статистике
- Машинном обучении

## Оптимизация

Существует множество подходов к решению данной проблемы. И, хотя это выходит за рамки этого руководства, использование подхода алгоритма выбора будет лучшим, поскольку он дает линейную временную сложность O(n).

### QuickSelect подход

```java
// Использование QuickSelect для поиска k-го элемента
// Затем проход по массиву для поиска всех элементов >= k-го
// Временная сложность: O(n) в среднем
```

## Когда использовать

### Используйте грубую силу, когда:

- Массив очень мал
- k очень мало (k = 1, 2)
- Нужна простота реализации

### Используйте TreeSet, когда:

- Нужны уникальные значения
- Массив среднего размера
- Нужна отсортированная коллекция

### Используйте PriorityQueue, когда:

- Массив большой
- k относительно мало
- Нужна максимальная эффективность
- Важна экономия памяти

## Заключение

В этом руководстве мы описали несколько решений для поиска k самых больших элементов в массиве. PriorityQueue с минимальной кучей является наиболее эффективным решением для большинства практических случаев, обеспечивая временную сложность O(n*log(k)) и пространственную сложность O(k).
