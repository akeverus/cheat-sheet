# Heap Sort

Кратко: сортировка кучей - это алгоритм сортировки, основанный на структуре данных "куча" (heap). Он использует бинарную кучу для сортировки элементов за время O(n*log(n)) с гарантированной производительностью.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Heap Sort](https://www.geeksforgeeks.org/heap-sort/)

### Визуализация
- [Visualgo: Heap Sort](https://visualgo.net/en/heapsort)

### См. также
- `./quick-sort.md` - алгоритм быстрой сортировки
- `./merge-sort.md` - алгоритм сортировки слиянием

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Структура данных "Куча"](#структура-данных-куча)
- [Операции с кучей](#операции-с-кучей)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Сортировка кучей основана на структуре данных кучи. Чтобы правильно понять сортировку кучей, мы сначала рассмотрим кучи и то, как они реализованы.

Куча - это специализированная древовидная структура данных. Поэтому он состоит из узлов. Мы присваиваем элементы узлам: каждый узел содержит ровно один элемент.

Кроме того, узлы могут иметь потомков. Если узел не имеет потомков, мы называем его листом.

## Структура данных "Куча"

Отличительной чертой Heap являются две вещи:

1. Значение каждого узла должно быть меньше или равно всем значениям, хранящимся в его дочерних элементах (для Min-Heap)
2. Это полное дерево, что означает, что оно имеет наименьшую возможную высоту

Из-за 1-го правила наименьший элемент всегда будет в корне дерева (для Min-Heap).

Кучи обычно используются для реализации приоритетных очередей, потому что куча - это очень эффективная реализация извлечения наименьшего (или наибольшего) элемента.

### Типы куч

- **Min-Heap:** родитель всегда меньше, чем все его дочерние элементы. Наименьший элемент в корневом узле
- **Max-Heap:** родитель всегда больше, чем его дочерние элементы. Наибольший элемент в корневом узле

### Полное бинарное дерево

Мы можем выбирать из многих реализаций дерева. Наиболее простым является бинарное дерево. В двоичном дереве каждый узел может иметь не более двух дочерних элементов. Мы называем их левым потомком и правым потомком.

Самый простой способ применить второе правило - использовать полное двоичное дерево. Полное двоичное дерево следует нескольким простым правилам:

- если узел имеет только одного дочернего элемента, это должен быть его левый дочерний элемент
- только самый правый узел на самом глубоком уровне может иметь ровно одного потомка
- листья могут быть только на самом глубоком уровне

В этом руководстве мы сосредоточимся на Min-Heap с реализацией двоичного дерева.

## Операции с кучей

### Вставка элемента

Мы можем вставить элемент со следующими шагами:

1. Создайте новый лист, который является самым правым доступным слотом на самом глубоком уровне, и сохраните элемент в этом узле
2. Если элемент меньше своего родителя, мы меняем их местами
3. Продолжайте с шага 2, пока элемент не станет меньше своего родителя или не станет новым корнем

Обратите внимание, что шаг 2 не нарушит правило кучи, потому что, если мы заменим значение узла на меньшее, оно все равно будет меньше, чем его дочерние элементы.

### Пример вставки

Давайте посмотрим пример! Мы хотим вставить 4 в эту кучу:

```
    2
   / \
  /   \
 3     6
/ \
5   7
```

Первым шагом является создание нового листа, который хранит 4:

```
    2
   / \
  /   \
 3     6
/ \   /
5   7 4
```

Так как 4 меньше, чем его родитель, 6, мы меняем их местами:

```
    2
   / \
  /   \
 3     4
/ \   /
5   7 6
```

Теперь мы проверяем, меньше ли 4 своего родителя или нет. Поскольку его родитель равен 2, мы останавливаемся. Куча все еще действительна, и мы вставили число 4.

### Удаление корневого элемента

Поскольку корень кучи всегда содержит наименьший элемент, идея сортировки кучи довольно проста: удалять корневой узел до тех пор, пока куча не станет пустой.

Единственное, что нам нужно, - это операция удаления, которая сохраняет кучу в согласованном состоянии. Мы должны убедиться, что не нарушаем структуру двоичного дерева или свойства кучи.

Чтобы сохранить структуру, мы не можем удалить ни один элемент, кроме самого правого листа. Итак, идея состоит в том, чтобы удалить элемент из корневого узла и сохранить крайний правый лист в корневом узле.

Но эта операция наверняка нарушит свойство кучи. Поэтому, если новый корень больше, чем любой из его дочерних узлов, мы меняем его местами с его наименьшим дочерним узлом. Поскольку наименьший дочерний узел меньше всех других дочерних узлов, он не нарушает свойство кучи.

Мы продолжаем менять местами до тех пор, пока элемент не станет листом или не станет меньше, чем все его дочерние элементы.

## Java Implementation

### Представление кучи массивом

Поскольку мы используем полное двоичное дерево, мы можем реализовать его с помощью массива: элемент в массиве будет узлом в дереве. Мы помечаем каждый узел индексами массива слева направо, сверху вниз следующим образом:

```
    0
   / \
  /   \
 1     2
/ \   /
3   4 5
```

Единственное, что нам нужно, это отслеживать, сколько элементов мы храним в дереве. Таким образом, индекс следующего элемента, который мы хотим вставить, будет размером массива.

Используя эту индексацию, мы можем вычислить индекс родительского и дочернего узлов:

- родитель: (индекс - 1) / 2
- левый ребенок: 2 * индекс + 1
- правый ребенок: 2 * индекс + 2

### Базовая структура

```java
class BinaryTree<E> {
    List<E> elements = new ArrayList<>();
    
    void add(E e) {
        elements.add(e);
    }
    
    boolean isEmpty() {
        return elements.isEmpty();
    }
    
    E elementAt(int index) {
        return elements.get(index);
    }
    
    int parentIndex(int index) {
        return (index - 1) / 2;
    }
    
    int leftChildIndex(int index) {
        return 2 * index + 1;
    }
    
    int rightChildIndex(int index) {
        return 2 * index + 2;
    }
}
```

### Реализация вставки

Приведенный выше код только добавляет новый элемент в конец дерева. Следовательно, нам нужно пройти новый элемент вверх, если это необходимо:

```java
class Heap<E extends Comparable<E>> {
    List<E> elements = new ArrayList<>();
    
    void add(E e) {
        elements.add(e);
        int elementIndex = elements.size() - 1;
        
        while (!isRoot(elementIndex) && !isCorrectChild(elementIndex)) {
            int parentIndex = parentIndex(elementIndex);
            swap(elementIndex, parentIndex);
            elementIndex = parentIndex;
        }
    }
    
    boolean isRoot(int index) {
        return index == 0;
    }
    
    boolean isCorrectChild(int index) {
        return isCorrect(parentIndex(index), index);
    }
    
    boolean isCorrect(int parentIndex, int childIndex) {
        if (!isValidIndex(parentIndex) || !isValidIndex(childIndex)) {
            return true;
        }
        return elementAt(parentIndex).compareTo(elementAt(childIndex)) < 0;
    }
    
    boolean isValidIndex(int index) {
        return index < elements.size();
    }
    
    void swap(int index1, int index2) {
        E element1 = elementAt(index1);
        E element2 = elementAt(index2);
        elements.set(index1, element2);
        elements.set(index2, element1);
    }
}
```

Обратите внимание, что поскольку нам нужно сравнивать элементы, для них необходимо реализовать `java.util.Comparable`.

### Реализация удаления

Удаление корневого элемента (pop):

```java
class Heap<E extends Comparable<E>> {
    E pop() {
        if (isEmpty()) {
            throw new IllegalStateException("You cannot pop from an empty heap");
        }
        
        E result = elementAt(0);
        int lastElementIndex = elements.size() - 1;
        swap(0, lastElementIndex);
        elements.remove(lastElementIndex);
        
        int elementIndex = 0;
        while (!isLeaf(elementIndex) && !isCorrectParent(elementIndex)) {
            int smallerChildIndex = smallerChildIndex(elementIndex);
            swap(elementIndex, smallerChildIndex);
            elementIndex = smallerChildIndex;
        }
        
        return result;
    }
    
    boolean isLeaf(int index) {
        return !isValidIndex(leftChildIndex(index));
    }
    
    boolean isCorrectParent(int index) {
        return isCorrect(index, leftChildIndex(index)) && 
               isCorrect(index, rightChildIndex(index));
    }
    
    int smallerChildIndex(int index) {
        int leftChildIndex = leftChildIndex(index);
        int rightChildIndex = rightChildIndex(index);
        
        if (!isValidIndex(rightChildIndex)) {
            return leftChildIndex;
        }
        
        if (elementAt(leftChildIndex).compareTo(elementAt(rightChildIndex)) < 0) {
            return leftChildIndex;
        }
        
        return rightChildIndex;
    }
}
```

### Реализация сортировки

Как мы уже говорили ранее, сортировка - это просто создание кучи и многократное удаление корня:

```java
class Heap<E extends Comparable<E>> {
    static <E extends Comparable<E>> List<E> sort(Iterable<E> elements) {
        Heap<E> heap = of(elements);
        List<E> result = new ArrayList<>();
        
        while (!heap.isEmpty()) {
            result.add(heap.pop());
        }
        
        return result;
    }
    
    static <E extends Comparable<E>> Heap<E> of(Iterable<E> elements) {
        Heap<E> result = new Heap<>();
        for (E element : elements) {
            result.add(element);
        }
        return result;
    }
}
```

Мы можем проверить его работу с помощью следующего теста:

```java
@Test
void givenNotEmptyIterable_whenSortCalled_thenItShouldReturnElementsInSortedList() {
    List<Integer> elements = Arrays.asList(3, 5, 1, 4, 2);
    List<Integer> sortedElements = Heap.sort(elements);
    assertThat(sortedElements).isEqualTo(Arrays.asList(1, 2, 3, 4, 5));
}
```

Обратите внимание, что мы могли бы предоставить реализацию, которая сортирует на месте, что означает, что мы предоставляем результат в том же массиве, в котором мы получили элементы. Кроме того, таким образом нам не нужно промежуточное выделение памяти. Однако эту реализацию будет немного сложнее понять.

## Kotlin Implementation

### Класс Heap

```kotlin
class HeapK<E : Comparable<E>> {
    private val elements = mutableListOf<E>()

    fun isEmpty(): Boolean = elements.isEmpty()

    fun add(element: E) {
        elements.add(element)
        var elementIndex = elements.size - 1
        while (!isRoot(elementIndex) && !isCorrectChild(elementIndex)) {
            val parentIndex = parentIndex(elementIndex)
            swap(elementIndex, parentIndex)
            elementIndex = parentIndex
        }
    }

    private fun isRoot(index: Int): Boolean = index == 0

    private fun elementAt(index: Int): E = elements[index]

    private fun parentIndex(index: Int): Int = (index - 1) / 2

    private fun leftChildIndex(index: Int): Int = 2 * index + 1

    private fun rightChildIndex(index: Int): Int = 2 * index + 2

    private fun isCorrectChild(index: Int): Boolean {
        return isCorrect(parentIndex(index), index)
    }

    private fun isCorrect(parentIndex: Int, childIndex: Int): Boolean {
        if (!isValidIndex(parentIndex) || !isValidIndex(childIndex)) {
            return true
        }
        return elementAt(parentIndex).compareTo(elementAt(childIndex)) < 0
    }

    private fun isValidIndex(index: Int): Boolean = index < elements.size

    private fun swap(index1: Int, index2: Int) {
        val element1 = elementAt(index1)
        val element2 = elementAt(index2)
        elements[index1] = element2
        elements[index2] = element1
    }
}
```

### Удаление элемента (pop)

```kotlin
fun pop(): E {
    if (isEmpty()) {
        throw IllegalStateException("You cannot pop from an empty heap")
    }

    val result = elementAt(0)
    val lastElementIndex = elements.size - 1
    swap(0, lastElementIndex)
    elements.removeAt(lastElementIndex)

    var elementIndex = 0
    while (!isLeaf(elementIndex) && !isCorrectParent(elementIndex)) {
        val smallerChildIndex = smallerChildIndex(elementIndex)
        swap(elementIndex, smallerChildIndex)
        elementIndex = smallerChildIndex
    }

    return result
}

private fun isLeaf(index: Int): Boolean = !isValidIndex(leftChildIndex(index))

private fun isCorrectParent(index: Int): Boolean {
    return isCorrect(index, leftChildIndex(index)) &&
           isCorrect(index, rightChildIndex(index))
}

private fun smallerChildIndex(index: Int): Int {
    val leftChildIndex = leftChildIndex(index)
    val rightChildIndex = rightChildIndex(index)

    if (!isValidIndex(rightChildIndex)) {
        return leftChildIndex
    }

    return if (elementAt(leftChildIndex).compareTo(elementAt(rightChildIndex)) < 0) {
        leftChildIndex
    } else {
        rightChildIndex
    }
}
```

### Сортировка

```kotlin
companion object {
    fun <E : Comparable<E>> sort(elements: Iterable<E>): List<E> {
        val heap = of(elements)
        val result = mutableListOf<E>()

        while (!heap.isEmpty()) {
            result.add(heap.pop())
        }

        return result
    }

    fun <E : Comparable<E>> of(elements: Iterable<E>): HeapK<E> {
        val result = HeapK<E>()
        for (element in elements) {
            result.add(element)
        }
        return result
    }
}
```

### In-place сортировка

```kotlin
fun heapSortInPlaceK(arr: IntArray) {
    val n = arr.size

    // Построение кучи (rearrange array)
    for (i in n / 2 - 1 downTo 0) {
        heapifyK(arr, n, i)
    }

    // Извлечение элементов из кучи по одному
    for (i in n - 1 downTo 1) {
        // Переместить текущий корень в конец
        val temp = arr[0]
        arr[0] = arr[i]
        arr[i] = temp

        // Вызвать heapify на уменьшенной куче
        heapifyK(arr, i, 0)
    }
}

private fun heapifyK(arr: IntArray, n: Int, i: Int) {
    var largest = i // Инициализировать наибольший как корень
    val left = 2 * i + 1
    val right = 2 * i + 2

    // Если левый дочерний элемент больше корня
    if (left < n && arr[left] > arr[largest]) {
        largest = left
    }

    // Если правый дочерний элемент больше, чем самый большой на данный момент
    if (right < n && arr[right] > arr[largest]) {
        largest = right
    }

    // Если самый большой не является корнем
    if (largest != i) {
        val swap = arr[i]
        arr[i] = arr[largest]
        arr[largest] = swap

        // Рекурсивно heapify затронутое поддерево
        heapifyK(arr, n, largest)
    }
}
```

### Пример использования

```kotlin
fun main() {
    // Использование класса Heap
    val elements = listOf(3, 5, 1, 4, 2)
    val sortedElements = HeapK.sort(elements)
    println(sortedElements) // [1, 2, 3, 4, 5]

    // In-place сортировка
    val arr = intArrayOf(3, 5, 1, 4, 2)
    heapSortInPlaceK(arr)
    println(arr.contentToString()) // [1, 2, 3, 4, 5]
}
```

## Сложность

Сортировка кучей состоит из двух ключевых шагов: вставки элемента и удаления корневого узла. Оба шага имеют сложность O(log(n)).

Поскольку мы повторяем оба шага n раз, общая сложность сортировки составляет O(n*log(n)).

Обратите внимание, что мы не упомянули стоимость перераспределения массива, но поскольку это O(n), это не влияет на общую сложность. Кроме того, как мы упоминали ранее, можно реализовать сортировку на месте, что означает, что перераспределение массива не требуется.

Также стоит отметить, что 50% элементов являются листьями, а 75% элементов находятся на двух нижних уровнях. Поэтому большинство операций вставки не требуют более двух шагов.

### Временная сложность

- **Лучший случай:** O(n*log(n))
- **Средний случай:** O(n*log(n))
- **Худший случай:** O(n*log(n))

Гарантированная производительность O(n*log(n)) во всех случаях - это одно из главных преимуществ Heap Sort.

### Пространственная сложность

- **С дополнительной памятью:** O(n) - для хранения кучи
- **In-place версия:** O(1) - сортировка на месте

## Особенности

- **Гарантированная производительность:** Всегда работает за O(n*log(n)), независимо от входных данных
- **Нестабильность:** Heap Sort не является стабильным алгоритмом сортировки
- **In-place:** Может быть реализован для сортировки на месте
- **Неадаптивность:** Производительность не зависит от порядка входных данных

## Применение

Heap Sort полезен в следующих случаях:

- Когда требуется гарантированная производительность O(n*log(n))
- В системах реального времени, где важна предсказуемость
- Когда память ограничена (in-place версия)
- Как часть более сложных алгоритмов

## Сравнение с другими алгоритмами

| Алгоритм | Лучший | Средний | Худший | Память | Стабильность |
|----------|--------|---------|--------|--------|--------------|
| Heap Sort | O(n*log(n)) | O(n*log(n)) | O(n*log(n)) | O(1) | Нет |
| Quick Sort | O(n*log(n)) | O(n*log(n)) | O(n²) | O(log(n)) | Нет |
| Merge Sort | O(n*log(n)) | O(n*log(n)) | O(n*log(n)) | O(n) | Да |

Обратите внимание, что на реальных данных быстрая сортировка обычно более эффективна, чем сортировка кучей. Положительным моментом является то, что сортировка кучей всегда имеет временную сложность O(n*log(n)) в наихудшем случае.

## Заключение

В этом уроке мы увидели реализацию двоичной кучи и сортировки кучи.

Несмотря на то, что временная сложность составляет O(n*log(n)), в большинстве случаев это не лучший алгоритм для реальных данных из-за плохой локальности ссылок и большего количества сравнений по сравнению с Quick Sort.
