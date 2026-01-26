# Bucket Sort

Кратко: сортировка блочная (bucket sort) - это алгоритм сортировки, который распределяет элементы по нескольким "ведрам" (buckets), сортирует каждое ведро отдельно, а затем объединяет результаты в отсортированный массив.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Bucket Sort](https://www.geeksforgeeks.org/bucket-sort-2/)

### Визуализация
- [Visualgo: Bucket Sort](https://visualgo.net/en/sorting)

### См. также
- `./counting-sort.md` - сортировка подсчетом
- `./radix-sort.md` - сортировка поразрядная

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Принцип работы](#принцип-работы)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Bucket sort, иногда называемая сортировкой контейнеров, представляет собой особый алгоритм сортировки. Сортировка работает путем распределения элементов, которые мы хотим отсортировать, по нескольким индивидуально отсортированным сегментам. Делая это, мы можем уменьшить количество сравнений между элементами и сократить время сортировки.

## Принцип работы

Давайте кратко рассмотрим шаги, необходимые для выполнения сортировки ведра:

1. Настраиваем массив наших изначально пустых bucket
2. Распределяем наши элементы по соответствующим bucket
3. Сортировка каждого bucket
4. Объедините отсортированные сегменты вместе, чтобы воссоздать полный список

Хотя этот алгоритм не зависит от языка, мы будем реализовывать сортировку в Java. Давайте шаг за шагом пройдемся по приведенному выше списку и напишем код для сортировки списка целых чисел.

## Java Implementation

### Шаг 1: Определение функции хеширования

Во-первых, нам нужно определить алгоритм хеширования, чтобы решить, какие из наших элементов помещаются в какое ведро:

```java
private int hash(int i, int max, int numberOfBuckets) {
    return (int) ((double) i / max * (numberOfBuckets - 1));
}
```

Эта функция распределяет элементы по ведрам на основе их значения относительно максимального значения.

### Шаг 2: Создание ведер

Определив наш хэш-метод, мы теперь можем указать количество бинов как квадратный корень из размера входного списка:

```java
final int numberOfBuckets = (int) Math.sqrt(initialList.size());
List<List<Integer>> buckets = new ArrayList<>(numberOfBuckets);

for(int i = 0; i < numberOfBuckets; i++) {
    buckets.add(new ArrayList<>());
}
```

### Шаг 3: Поиск максимального значения

Наконец, нам нужен короткий метод для определения максимального целого числа в нашем списке ввода:

```java
private int findMax(List<Integer> input) {
    int m = Integer.MIN_VALUE;
    for (int i : input) {
        m = Math.max(i, m);
    }
    return m;
}
```

### Шаг 4: Распределение элементов

Теперь, когда мы определили наши корзины, мы можем распределить каждый элемент нашего входного списка в соответствующие корзины, используя метод хеширования:

```java
int max = findMax(initialList);
for (int i : initialList) {
    buckets.get(hash(i, max, numberOfBuckets)).add(i);
}
```

### Шаг 5: Сортировка каждого ведра

Определив наши корзины и заполнив их целыми числами, давайте воспользуемся компаратором для их сортировки:

```java
Comparator<Integer> comparator = Comparator.naturalOrder();
for(List<Integer> bucket : buckets) {
    bucket.sort(comparator);
}
```

### Шаг 6: Объединение результатов

Наконец, нам нужно собрать наши корзины вместе, чтобы воссоздать единый список. Поскольку наши корзины отсортированы, нам нужно только один раз пройтись по каждой корзине и добавить элементы в основной список:

```java
List<Integer> sortedArray = new LinkedList<>();
for(List<Integer> bucket : buckets) {
    sortedArray.addAll(bucket);
}
return sortedArray;
```

### Полная реализация

```java
public class BucketSort {
    public static List<Integer> sort(List<Integer> initialList) {
        final int numberOfBuckets = (int) Math.sqrt(initialList.size());
        List<List<Integer>> buckets = new ArrayList<>(numberOfBuckets);
        
        for(int i = 0; i < numberOfBuckets; i++) {
            buckets.add(new ArrayList<>());
        }
        
        int max = findMax(initialList);
        
        for (int i : initialList) {
            buckets.get(hash(i, max, numberOfBuckets)).add(i);
        }
        
        Comparator<Integer> comparator = Comparator.naturalOrder();
        for(List<Integer> bucket : buckets) {
            bucket.sort(comparator);
        }
        
        List<Integer> sortedArray = new LinkedList<>();
        for(List<Integer> bucket : buckets) {
            sortedArray.addAll(bucket);
        }
        
        return sortedArray;
    }
    
    private static int hash(int i, int max, int numberOfBuckets) {
        return (int) ((double) i / max * (numberOfBuckets - 1));
    }
    
    private static int findMax(List<Integer> input) {
        int m = Integer.MIN_VALUE;
        for (int i : input) {
            m = Math.max(i, m);
        }
        return m;
    }
}
```

### Тестирование

Когда наша реализация завершена, давайте напишем быстрый модульный тест, чтобы убедиться, что он работает должным образом:

```java
BucketSorter sorter = new IntegerBucketSorter();
List<Integer> unsorted = Arrays.asList(80, 50, 60, 30, 20, 10, 70, 0, 40, 500, 600, 602, 200, 15);
List<Integer> expected = Arrays.asList(0, 10, 15, 20, 30, 40, 50, 60, 70, 80, 200, 500, 600, 602);
List<Integer> sorted = sorter.sort(unsorted);
assertEquals(expected, sorted);
```

## Kotlin Implementation

### Полная реализация

```kotlin
fun bucketSortK(initialList: List<Int>): List<Int> {
    val numberOfBuckets = Math.sqrt(initialList.size.toDouble()).toInt()
    val buckets = MutableList(numberOfBuckets) { mutableListOf<Int>() }
    
    val max = findMaxK(initialList)
    
    // Распределение элементов по ведрам
    for (i in initialList) {
        buckets[hashK(i, max, numberOfBuckets)].add(i)
    }
    
    // Сортировка каждого ведра
    buckets.forEach { it.sort() }
    
    // Объединение результатов
    return buckets.flatten()
}

private fun hashK(i: Int, max: Int, numberOfBuckets: Int): Int {
    return (i.toDouble() / max * (numberOfBuckets - 1)).toInt()
}

private fun findMaxK(input: List<Int>): Int {
    return input.maxOrNull() ?: Int.MIN_VALUE
}
```

### Функциональный стиль

```kotlin
fun bucketSortFunctionalK(initialList: List<Int>): List<Int> {
    if (initialList.isEmpty()) return emptyList()
    
    val numberOfBuckets = Math.sqrt(initialList.size.toDouble()).toInt()
    val max = initialList.maxOrNull() ?: return initialList
    
    return initialList
        .groupBy { hashK(it, max, numberOfBuckets) }
        .values
        .map { it.sorted() }
        .flatten()
}
```

### In-place версия для массивов

```kotlin
fun bucketSortArrayK(arr: IntArray) {
    if (arr.isEmpty()) return
    
    val numberOfBuckets = Math.sqrt(arr.size.toDouble()).toInt()
    val buckets = Array(numberOfBuckets) { mutableListOf<Int>() }
    val max = arr.maxOrNull() ?: return
    
    // Распределение
    arr.forEach { element ->
        buckets[hashK(element, max, numberOfBuckets)].add(element)
    }
    
    // Сортировка каждого ведра
    buckets.forEach { it.sort() }
    
    // Объединение обратно в массив
    var index = 0
    buckets.forEach { bucket ->
        bucket.forEach { element ->
            arr[index++] = element
        }
    }
}
```

### Пример использования

```kotlin
fun main() {
    val unsorted = listOf(80, 50, 60, 30, 20, 10, 70, 0, 40, 500, 600, 602, 200, 15)
    
    // Базовая версия
    val sorted = bucketSortK(unsorted)
    println(sorted) 
    // [0, 10, 15, 20, 30, 40, 50, 60, 70, 80, 200, 500, 600, 602]
    
    // Функциональный стиль
    val sorted2 = bucketSortFunctionalK(unsorted)
    println(sorted2)
    
    // In-place для массива
    val arr = intArrayOf(80, 50, 60, 30, 20, 10, 70, 0, 40)
    bucketSortArrayK(arr)
    println(arr.contentToString())
}
```

## Сложность

Давайте кратко рассмотрим временную сложность выполнения сортировки ведром.

### Временная сложность

- **Лучший случай:** O(n) - когда элементы равномерно распределены по ведрам
- **Средний случай:** O(n) - при равномерном распределении
- **Худший случай:** O(n²) - когда все элементы попадают в одно ведро

В нашем худшем сценарии мы найдем все наши элементы в одном и том же сегменте и в обратном порядке. Когда происходит этот случай, мы сокращаем нашу сортировку ведра до простой сортировки, в которой каждый элемент сравнивается с каждым другим элементом, что дает временную сложность O(n²).

В нашем среднем случае мы обнаруживаем, что элементы относительно равномерно распределены между нашими входными сегментами. Поскольку каждый из наших шагов требует только одной итерации по нашим входным сегментам, мы обнаруживаем, что наша сортировка по сегментам завершается за время O(n).

### Пространственная сложность

- **Все случаи:** O(n) - для хранения ведер и элементов

## Особенности

- **Распределительная сортировка:** Bucket Sort является распределительным алгоритмом сортировки
- **Зависит от распределения:** Производительность сильно зависит от распределения входных данных
- **Стабильность:** Может быть стабильным, если используемый алгоритм сортировки внутри ведер стабилен
- **Применимость:** Лучше всего работает, когда входные данные равномерно распределены по диапазону

## Применение

Bucket Sort особенно полезен в следующих случаях:

- Когда входные данные равномерно распределены по диапазону
- Для сортировки чисел с плавающей точкой в диапазоне [0, 1)
- Когда нужно отсортировать данные, которые можно разделить на "ведра"
- В комбинации с другими алгоритмами сортировки

## Ограничения

- Требует знания диапазона входных данных
- Может быть неэффективным, если элементы неравномерно распределены
- В худшем случае деградирует до O(n²)
- Требует дополнительной памяти для ведер

## Сравнение с другими алгоритмами

| Алгоритм | Лучший | Средний | Худший | Память | Применение |
|----------|--------|---------|--------|--------|------------|
| Bucket Sort | O(n) | O(n) | O(n²) | O(n) | Равномерное распределение |
| Counting Sort | O(n+k) | O(n+k) | O(n+k) | O(k) | Ограниченный диапазон |
| Radix Sort | O(d*(n+k)) | O(d*(n+k)) | O(d*(n+k)) | O(n+k) | Целые числа |

## Преимущества и недостатки

### Преимущества

- Очень быстрый в среднем случае (O(n))
- Хорошо работает с равномерно распределенными данными
- Может быть параллелизован

### Недостатки

- В худшем случае O(n²)
- Требует дополнительной памяти
- Зависит от распределения данных
- Не подходит для неравномерно распределенных данных
