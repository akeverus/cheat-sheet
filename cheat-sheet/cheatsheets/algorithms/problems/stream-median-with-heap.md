# Stream Median with Heap

Кратко: вычисление медианы потока целых чисел с использованием двух куч (min-heap и max-heap). Рассматривается эффективный алгоритм для поддержания медианы в потоке данных.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java PriorityQueue Documentation](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)
- [Baeldung: Stream Median](https://www.baeldung.com/java-stream-integers-median-using-heap)

### См. также
- `../trees/binary-tree.md` - бинарное дерево
- `../math/standard-deviation.md` - стандартное отклонение

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Определение медианы](#определение-медианы)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы узнаем, как вычислить медиану потока целых чисел.

Мы продолжим постановку проблемы с примерами, затем проанализируем проблему и, наконец, реализуем несколько решений на Java.

## Определение медианы

Медиана - это среднее значение упорядоченного набора данных. Для набора целых чисел существует столько же элементов меньше медианы, сколько больше.

В заказанном наборе:

1. **Нечетное количество целых чисел:** средний элемент является медианой - в упорядоченном наборе {5, 7, 10} медиана равна 7
2. **Четное количество целых чисел:** среднего элемента нет; медиана вычисляется как среднее двух средних элементов - в упорядоченном наборе {5, 7, 8, 10} медиана равна (7 + 8) / 2 = 7.5

Теперь предположим, что вместо конечного набора мы считываем целые числа из потока данных. Мы можем определить медиану потока целых чисел как медиану набора целых чисел, прочитанных до сих пор.

### Формулировка задачи

Получив входной поток целых чисел, мы должны разработать класс, который выполняет следующие две задачи для каждого целого числа, которое мы читаем:

1. Добавьте целое число к множеству целых чисел
2. Найдите медиану целых чисел, прочитанных до сих пор

### Примеры

```
add 5  // sorted-set = { 5 }, size = 1
get median -> 5

add 7  // sorted-set = { 5, 7 }, size = 2
get median -> (5 + 7) / 2 = 6

add 10 // sorted-set = { 5, 7, 10 }, size = 3
get median -> 7

add 8  // sorted-set = { 5, 7, 8, 10 }, size = 4
get median -> (7 + 8) / 2 = 7.5
```

Хотя поток не является конечным, мы можем предположить, что можем хранить в памяти сразу все элементы потока.

Мы можем представить наши задачи в виде следующих операций в коде:

```java
void add(int num);
double getMedian();
```

## Java Implementation

### Подход 1: С отсортированным списком

Давайте начнем с простой идеи - мы можем вычислить медиану отсортированного списка целых чисел, обратившись к среднему элементу или двум средним элементам списка по индексу. Временная сложность операции getMedian составляет O(1).

При добавлении нового целого числа мы должны определить его правильное положение в списке, чтобы список оставался отсортированным. Эта операция может быть выполнена за время O(n), где n - размер списка. Таким образом, общая стоимость добавления нового элемента в список и вычисления новой медианы составляет O(n).

Операция добавления выполняется за линейное время, что не является оптимальным.

## Подход 2: С двумя кучами

Мы можем разделить список на два отсортированных списка - меньшая половина целых чисел отсортирована в порядке убывания, а большая половина целых чисел - в порядке возрастания.

Давайте заменим списки в нашем наивном подходе двумя кучами:

1. **Минимальная куча**, содержащая большую половину элементов, с минимальным элементом в корне
2. **Максимальная куча**, содержащая меньшую половину элементов, с максимальным элементом в корне

Теперь мы можем добавить входящее целое число к соответствующей половине, сравнив его с корнем минимальной кучи. Затем, если после вставки размер одной кучи отличается от размера другой кучи более чем на 1, мы можем перебалансировать кучи, таким образом поддерживая разницу в размерах не более 1.

### Реализация

Мы будем использовать класс PriorityQueue для представления кучи. Свойство кучи PriorityQueue по умолчанию - min-heap. Мы можем создать максимальную кучу, используя `Comparator.reverseOrder()`:

```java
import java.util.*;

class MedianOfIntegerStream {
    private Queue<Integer> minHeap, maxHeap;
    
    MedianOfIntegerStream() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    }
    
    void add(int num) {
        if (!minHeap.isEmpty() && num < minHeap.peek()) {
            maxHeap.offer(num);
            if (maxHeap.size() > minHeap.size() + 1) {
                minHeap.offer(maxHeap.poll());
            }
        } else {
            minHeap.offer(num);
            if (minHeap.size() > maxHeap.size() + 1) {
                maxHeap.offer(minHeap.poll());
            }
        }
    }
    
    double getMedian() {
        int median;
        if (minHeap.size() < maxHeap.size()) {
            median = maxHeap.peek();
        } else if (minHeap.size() > maxHeap.size()) {
            median = minHeap.peek();
        } else {
            median = (minHeap.peek() + maxHeap.peek()) / 2;
        }
        return median;
    }
}
```

## Подход 3: Улучшенная версия с кучами

В нашем предыдущем подходе мы сравнивали каждый новый элемент с корневыми элементами кучи. Давайте рассмотрим другой подход с использованием кучи, в котором мы можем использовать свойство кучи для добавления нового элемента в соответствующую половину.

Как и в предыдущем решении, мы начинаем с двух куч - минимальной кучи и максимальной кучи. Далее введем условие: размер max-heap всегда должен быть (n/2), а размер min-heap может быть либо (n/2), либо (n/2) + 1, в зависимости от общего количества элементов в двух кучах. Другими словами, мы можем позволить только минимальной куче иметь дополнительный элемент, когда общее количество элементов нечетно.

С нашим неизменным размером кучи мы можем вычислить медиану как среднее значение корневых элементов обеих куч, если размеры обеих куч равны (n / 2). В противном случае корневым элементом минимальной кучи является медиана.

### Реализация

```java
class MedianOfIntegerStream {
    private Queue<Integer> minHeap, maxHeap;
    
    MedianOfIntegerStream() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    }
    
    void add(int num) {
        if (minHeap.size() == maxHeap.size()) {
            maxHeap.offer(num);
            minHeap.offer(maxHeap.poll());
        } else {
            minHeap.offer(num);
            maxHeap.offer(minHeap.poll());
        }
    }
    
    double getMedian() {
        int median;
        if (minHeap.size() > maxHeap.size()) {
            median = minHeap.peek();
        } else {
            median = (minHeap.peek() + maxHeap.peek()) / 2;
        }
        return median;
    }
}
```

Временные сложности наших операций остаются неизменными: getMedian стоит O(1) времени, а add выполняется за время O(log(n)) с точно таким же количеством операций.

## Сравнение подходов

| Подход | add() | getMedian() | Пространство |
|--------|-------|-------------|--------------|
| Отсортированный список | O(n) | O(1) | O(n) |
| Две кучи (подход 1) | O(log n) | O(1) | O(n) |
| Две кучи (подход 2) | O(log n) | O(1) | O(n) |

### Временная сложность операций с кучей

- **find-min/find-max:** O(1)
- **delete-min/delete-max:** O(log(n))
- **insert:** O(log(n))

Таким образом, операция getMedian может быть выполнена за время O(1), поскольку для нее требуются только функции find-min и find-max. Временная сложность операции добавления составляет O(log(n)) - три вызова вставки/удаления, каждый из которых требует O(log(n)) времени.

## Kotlin Implementation

### Подход с двумя кучами

```kotlin
import java.util.*

class MedianOfIntegerStreamK {
    private val minHeap = PriorityQueue<Int>()
    private val maxHeap = PriorityQueue<Int>(compareByDescending { it })
    
    fun add(num: Int) {
        if (minHeap.size == maxHeap.size) {
            maxHeap.offer(num)
            minHeap.offer(maxHeap.poll())
        } else {
            minHeap.offer(num)
            maxHeap.offer(minHeap.poll())
        }
    }
    
    fun getMedian(): Double {
        return if (minHeap.size > maxHeap.size) {
            minHeap.peek().toDouble()
        } else {
            (minHeap.peek() + maxHeap.peek()) / 2.0
        }
    }
}
```

### Улучшенная версия

```kotlin
class MedianOfIntegerStreamImprovedK {
    private val minHeap = PriorityQueue<Int>()
    private val maxHeap = PriorityQueue<Int>(compareByDescending { it })
    
    fun add(num: Int) {
        if (!minHeap.isEmpty() && num < minHeap.peek()) {
            maxHeap.offer(num)
            if (maxHeap.size > minHeap.size + 1) {
                minHeap.offer(maxHeap.poll())
            }
        } else {
            minHeap.offer(num)
            if (minHeap.size > maxHeap.size + 1) {
                maxHeap.offer(minHeap.poll())
            }
        }
    }
    
    fun getMedian(): Double {
        return when {
            minHeap.size < maxHeap.size -> maxHeap.peek().toDouble()
            minHeap.size > maxHeap.size -> minHeap.peek().toDouble()
            else -> (minHeap.peek() + maxHeap.peek()) / 2.0
        }
    }
}
```

### Пример использования

```kotlin
fun main() {
    val medianStream = MedianOfIntegerStreamK()
    
    medianStream.add(5)
    println("Median: ${medianStream.getMedian()}") // 5.0
    
    medianStream.add(7)
    println("Median: ${medianStream.getMedian()}") // 6.0
    
    medianStream.add(10)
    println("Median: ${medianStream.getMedian()}") // 7.0
    
    medianStream.add(8)
    println("Median: ${medianStream.getMedian()}") // 7.5
}
```

## Сложность

### Временная сложность

- **add():** O(log n) - вставка в кучу и возможная перебалансировка
- **getMedian():** O(1) - доступ к корневым элементам куч

### Пространственная сложность

- **Хранение элементов:** O(n) - где n - количество элементов в потоке

## Особенности

- **Эффективность:** Операции выполняются за O(log n) и O(1)
- **Простота:** Легко понять и реализовать
- **Гибкость:** Легко адаптировать для различных типов данных

## Применение

Вычисление медианы потока используется в:

- Статистическом анализе
- Обработке данных в реальном времени
- Мониторинге систем
- Финансовых расчетах
- Медицинских приложениях

## Варианты задачи

### Вариант 1: Медиана с удалением

```java
public class MedianStreamWithRemoval extends MedianOfIntegerStream {
    private Map<Integer, Integer> count = new HashMap<>();
    
    public void remove(int num) {
        count.put(num, count.getOrDefault(num, 0) + 1);
        // Ленивое удаление при получении медианы
    }
    
    @Override
    public double getMedian() {
        // Удаляем помеченные элементы перед вычислением
        cleanup();
        return super.getMedian();
    }
    
    private void cleanup() {
        // Удаляем элементы, помеченные для удаления
    }
}
```

### Вариант 2: Медиана с весами

```java
public class WeightedMedianStream {
    private Queue<WeightedElement> minHeap, maxHeap;
    
    public void add(int num, double weight) {
        // Добавление элемента с весом
    }
    
    public double getWeightedMedian() {
        // Вычисление взвешенной медианы
    }
}
```

## Когда использовать

### Используйте подход с кучами, когда:

- Нужна эффективность (O(log n) для add)
- Работаете с большими потоками данных
- Важна производительность

### Используйте отсортированный список, когда:

- Поток данных маленький
- Нужна простота реализации
- Производительность не критична

## Заключение

Оба решения на основе кучи предлагают схожие сложности с пространством и временем. Хотя второе решение умно и имеет более чистую реализацию, этот подход не интуитивно понятен. С другой стороны, первое решение естественно следует нашей интуиции, и легче рассуждать о правильности его операции сложения.

В этом уроке мы узнали, как вычислить медиану потока целых чисел. Мы оценили несколько подходов и реализовали несколько различных решений на Java с помощью PriorityQueue.
