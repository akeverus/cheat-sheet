# Find Kth Largest Element

Кратко: поиск k-го по величине элемента в массиве. Рассматриваются три подхода: сортировка O(n*log(n)), QuickSelect O(n) в среднем, и рандомизированный QuickSelect.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: K'th Smallest/Largest Element in Unsorted Array](https://www.geeksforgeeks.org/kth-smallestlargest-element-unsorted-array/)

### См. также
- `./maximum-subarray.md` - проблема максимального подмассива
- `./k-largest-elements.md` - поиск k наибольших элементов
- `../sorting/quick-sort.md` - быстрая сортировка

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Подход 1: Сортировка](#подход-1-сортировка)
  - [Java Implementation](#java-implementation-1)
  - [Kotlin Implementation](#kotlin-implementation-1)
- [Подход 2: QuickSelect](#подход-2-quickselect)
  - [Java Implementation](#java-implementation-2)
  - [Kotlin Implementation](#kotlin-implementation-2)
- [Подход 3: Рандомизированный QuickSelect](#подход-3-рандомизированный-quickselect)
  - [Java Implementation](#java-implementation-3)
  - [Kotlin Implementation](#kotlin-implementation-3)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этой статье мы представим различные решения для нахождения k-го по величине элемента в последовательности уникальных чисел. Мы будем использовать массив целых чисел для наших примеров.

Мы также поговорим о средней и наихудшей временной сложности каждого алгоритма.

Теперь давайте рассмотрим несколько возможных решений - одно с использованием простой сортировки и два с использованием алгоритма быстрого выбора, полученного из быстрой сортировки.

## Подход 1: Сортировка

Когда мы думаем о проблеме, возможно, наиболее очевидным решением, которое приходит на ум, является сортировка массива.

### Определим необходимые шаги:

1. Отсортировать массив в порядке возрастания
2. Поскольку последний элемент массива будет самым большим элементом, k-й самый большой элемент будет иметь индекс x, где x = длина(массив) - k

Как видим, решение простое, но требует сортировки всего массива. Следовательно, временная сложность будет O(n*log(n)):

### Java Implementation

```java
public int findKthLargestBySorting(Integer[] arr, int k) {
    Arrays.sort(arr);
    int targetIndex = arr.length - k;
    return arr[targetIndex];
}
```

Альтернативный подход - отсортировать массив в порядке убывания и просто вернуть элемент по (k - 1)-му индексу:

```java
public int findKthLargestBySortingDesc(Integer[] arr, int k) {
    Arrays.sort(arr, Collections.reverseOrder());
    return arr[k - 1];
}
```

Пример использования:

```java
Integer[] arr = {3, 2, 1, 5, 6, 4};
int k = 2; // Найти 2-й по величине элемент
int result = findKthLargestBySorting(arr, k);
// result = 5
```

### Kotlin Implementation

```kotlin
fun findKthLargestBySorting(arr: IntArray, k: Int): Int {
    arr.sort()
    val targetIndex = arr.size - k
    return arr[targetIndex]
}
```

Альтернативный подход:

```kotlin
fun findKthLargestBySortingDesc(arr: IntArray, k: Int): Int {
    arr.sortDescending()
    return arr[k - 1]
}
```

Пример использования:

```kotlin
val arr = intArrayOf(3, 2, 1, 5, 6, 4)
val k = 2 // Найти 2-й по величине элемент
val result = findKthLargestBySorting(arr, k)
// result = 5
```

## Подход 2: QuickSelect

Это можно считать оптимизацией предыдущего подхода. Здесь мы выбираем QuickSort для сортировки. Анализируя постановку задачи, мы понимаем, что на самом деле нам не нужно сортировать весь массив - нам нужно только переставить его содержимое так, чтобы k-й элемент массива был k-м наибольшим или наименьшим.

В QuickSort мы выбираем опорный элемент и перемещаем его в правильное положение. Мы также разбиваем массив вокруг него. В QuickSelect идея состоит в том, чтобы остановиться в точке, где сама точка поворота является k-м по величине элементом.

Мы можем дополнительно оптимизировать алгоритм, если не будем повторяться как для левой, так и для правой сторон опорной точки. Нам нужно только повторить для одного из них в соответствии с положением точки опоры.

### Основные идеи алгоритма QuickSelect:

1. Выберите опорный элемент и разделите массив соответствующим образом
2. Выберите крайний правый элемент в качестве опорного
3. Перетасуйте массив так, чтобы опорный элемент располагался на своем законном месте - все элементы, меньшие опорного, будут иметь более низкие индексы, а элементы, большие опорного, будут помещены в более высокие индексы, чем опорный
4. Если стержень находится в k-м элементе массива, выйти из процесса, так как стержень - это k-й самый большой элемент
5. Если позиция поворота больше k, то продолжить процесс с левым подмассивом, в противном случае повторить процесс с правым подмассивом

Мы можем написать общую логику, которую также можно использовать для поиска k-го наименьшего элемента. Мы определим метод `findKthElementByQuickSelect()`, который будет возвращать k-й элемент в отсортированном массиве.

Если мы отсортируем массив в порядке возрастания, k-й элемент массива будет k-м наименьшим элементом. Чтобы найти k-й самый большой элемент, мы можем передать k = length(Array) - k.

### Java Implementation

Давайте реализуем это решение:

```java
public int findKthElementByQuickSelect(Integer[] arr, int left, int right, int k) {
    if (k >= 0 && k <= right - left + 1) {
        int pos = partition(arr, left, right);
        
        if (pos - left == k) {
            return arr[pos];
        }
        
        if (pos - left > k) {
            return findKthElementByQuickSelect(arr, left, pos - 1, k);
        }
        
        return findKthElementByQuickSelect(arr, pos + 1, right, k - pos + left - 1);
    }
    
    return 0;
}

public int partition(Integer[] arr, int left, int right) {
    int pivot = arr[right];
    int i = left;
    
    for (int j = left; j <= right - 1; j++) {
        if (arr[j] <= pivot) {
            swap(arr, i, j);
            i++;
        }
    }
    
    swap(arr, i, right);
    return i;
}

public void swap(Integer[] arr, int n1, int n2) {
    int temp = arr[n2];
    arr[n2] = arr[n1];
    arr[n1] = temp;
}
```

Это решение работает в среднем за время O(n). Однако в худшем случае временная сложность будет O(n²).

### Kotlin Implementation

```kotlin
fun findKthElementByQuickSelect(arr: IntArray, left: Int, right: Int, k: Int): Int {
    if (k >= 0 && k <= right - left + 1) {
        val pos = partition(arr, left, right)
        
        when {
            pos - left == k -> return arr[pos]
            pos - left > k -> return findKthElementByQuickSelect(arr, left, pos - 1, k)
            else -> return findKthElementByQuickSelect(arr, pos + 1, right, k - pos + left - 1)
        }
    }
    return 0
}

fun partition(arr: IntArray, left: Int, right: Int): Int {
    val pivot = arr[right]
    var i = left
    
    for (j in left until right) {
        if (arr[j] <= pivot) {
            arr.swap(i, j)
            i++
        }
    }
    
    arr.swap(i, right)
    return i
}

fun IntArray.swap(i: Int, j: Int) {
    val temp = this[i]
    this[i] = this[j]
    this[j] = temp
}
```

Пример использования:

```kotlin
val arr = intArrayOf(3, 2, 1, 5, 6, 4)
val k = 2
val result = findKthElementByQuickSelect(arr, 0, arr.size - 1, arr.size - k)
// result = 5
```

## Подход 3: Рандомизированный QuickSelect

Этот подход является небольшой модификацией предыдущего подхода. Если массив почти/полностью отсортирован и если мы выберем самый правый элемент в качестве опорного, разделение левого и правого подмассивов будет очень неравномерным.

Этот метод предлагает выбирать начальный опорный элемент случайным образом. Однако нам не нужно менять логику разбиения.

Вместо вызова partition мы вызываем метод randomPartition, который выбирает случайный элемент и заменяет его крайним правым элементом, прежде чем, наконец, вызвать метод partition.

### Java Implementation

Давайте реализуем метод randomPartition:

```java
public int randomPartition(Integer arr[], int left, int right) {
    int n = right - left + 1;
    int pivot = (int) (Math.random() * n);
    swap(arr, left + pivot, right);
    return partition(arr, left, right);
}

public int findKthElementByRandomizedQuickSelect(Integer[] arr, int left, int right, int k) {
    if (k >= 0 && k <= right - left + 1) {
        int pos = randomPartition(arr, left, right);
        
        if (pos - left == k) {
            return arr[pos];
        }
        
        if (pos - left > k) {
            return findKthElementByRandomizedQuickSelect(arr, left, pos - 1, k);
        }
        
        return findKthElementByRandomizedQuickSelect(arr, pos + 1, right, k - pos + left - 1);
    }
    
    return 0;
}
```

В большинстве случаев это решение работает лучше, чем предыдущее.

Ожидаемая временная сложность рандомизированного QuickSelect составляет O(n).

Однако наихудшая временная сложность по-прежнему остается O(n²).

### Kotlin Implementation

```kotlin
fun randomPartition(arr: IntArray, left: Int, right: Int): Int {
    val n = right - left + 1
    val pivot = (0 until n).random()
    arr.swap(left + pivot, right)
    return partition(arr, left, right)
}

fun findKthElementByRandomizedQuickSelect(arr: IntArray, left: Int, right: Int, k: Int): Int {
    if (k >= 0 && k <= right - left + 1) {
        val pos = randomPartition(arr, left, right)
        
        when {
            pos - left == k -> return arr[pos]
            pos - left > k -> return findKthElementByRandomizedQuickSelect(arr, left, pos - 1, k)
            else -> return findKthElementByRandomizedQuickSelect(arr, pos + 1, right, k - pos + left - 1)
        }
    }
    return 0
}
```

Пример использования:

```kotlin
val arr = intArrayOf(3, 2, 1, 5, 6, 4)
val k = 2
val result = findKthElementByRandomizedQuickSelect(arr, 0, arr.size - 1, arr.size - k)
// result = 5
```

## Сравнение подходов

| Характеристика | Сортировка | QuickSelect | Рандомизированный QuickSelect |
|----------------|------------|-------------|-------------------------------|
| Временная сложность (среднее) | O(n*log(n)) | O(n) | O(n) |
| Временная сложность (худшее) | O(n*log(n)) | O(n²) | O(n²) |
| Пространственная сложность | O(1) | O(log(n)) | O(log(n)) |
| Простота | Проще | Сложнее | Сложнее |
| Применение | Когда нужна сортировка | Когда нужен только k-й элемент | Когда нужна стабильность |

## Сложность

### Подход 1: Сортировка

- **Временная сложность:** O(n*log(n)) - сортировка всего массива
- **Пространственная сложность:** O(1) - in-place сортировка

### Подход 2: QuickSelect

- **Временная сложность (среднее):** O(n)
- **Временная сложность (худшее):** O(n²) - когда опорный всегда минимальный/максимальный
- **Пространственная сложность:** O(log(n)) - для стека рекурсии

### Подход 3: Рандомизированный QuickSelect

- **Временная сложность (среднее):** O(n)
- **Временная сложность (худшее):** O(n²) - крайне редко
- **Пространственная сложность:** O(log(n)) - для стека рекурсии

## Особенности

- **Эффективность:** QuickSelect оптимален для поиска одного элемента
- **Гибкость:** Можно найти k-й наибольший или наименьший элемент
- **Рандомизация:** Улучшает среднюю производительность

## Применение

Поиск k-го элемента используется в:

- Статистике (медиана, перцентили)
- Алгоритмах ранжирования
- Поиске топ-k элементов
- Анализе данных
- Машинном обучении

## Когда использовать

### Используйте сортировку, когда:

- Нужно найти несколько k-х элементов
- Массив уже отсортирован
- Нужна простота реализации

### Используйте QuickSelect, когда:

- Нужен только один k-й элемент
- Важна производительность
- Массив большой

### Используйте рандомизированный QuickSelect, когда:

- Нужна стабильная производительность
- Массив может быть частично отсортирован
- Важна надежность

## Заключение

В этой статье мы обсудили различные решения для поиска k-го по величине (или наименьшего) элемента в массиве уникальных чисел. Самое простое решение - отсортировать массив и вернуть k-й элемент. Это решение имеет временную сложность O(n*log(n)).

Мы также обсудили два варианта быстрого выбора. Этот алгоритм не является простым, но в среднем имеет временную сложность O(n).
