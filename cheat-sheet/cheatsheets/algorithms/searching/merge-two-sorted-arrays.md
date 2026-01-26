# Merge Two Sorted Arrays

Кратко: объединение двух отсортированных массивов - это классическая операция, которая создает один отсортированный массив из двух отсортированных массивов за время O(n+m) с использованием двух указателей.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Merge Two Sorted Arrays](https://www.geeksforgeeks.org/merge-two-sorted-arrays/)

### См. также
- `./binary-search.md` - бинарный поиск
- `./maximum-subarray.md` - максимальный подмассив
- `../sorting/merge-sort.md` - алгоритм сортировки слиянием

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Идея алгоритма](#идея-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Объединение двух отсортированных массивов — это классическая операция из merge sort. Это фундаментальная операция, которая используется во многих алгоритмах сортировки и обработки данных.

## Идея алгоритма

Используем два указателя и на каждом шаге выбираем меньший элемент:

- Имеем массивы `foo` и `bar`
- Создаём `merged` длиной `foo.length + bar.length`
- Двигаем `fooPosition` и `barPosition`, добавляя меньший элемент
- Когда один массив закончился — копируем остаток второго

### Пример работы

```
foo = [3, 7]
bar = [4, 8, 11]

Шаг 1: Сравниваем 3 и 4 → добавляем 3
merged = [3]

Шаг 2: Сравниваем 7 и 4 → добавляем 4
merged = [3, 4]

Шаг 3: Сравниваем 7 и 8 → добавляем 7
merged = [3, 4, 7]

Шаг 4: foo закончился → копируем остаток bar
merged = [3, 4, 7, 8, 11]
```

## Java Implementation

### Реализация

```java
public static int[] merge(int[] foo, int[] bar) {
    int fooLength = foo.length;
    int barLength = bar.length;
    int[] merged = new int[fooLength + barLength];
    
    int fooPosition = 0;
    int barPosition = 0;
    int mergedPosition = 0;
    
    while (fooPosition < fooLength && barPosition < barLength) {
        if (foo[fooPosition] < bar[barPosition]) {
            merged[mergedPosition++] = foo[fooPosition++];
        } else {
            merged[mergedPosition++] = bar[barPosition++];
        }
    }
    
    while (fooPosition < fooLength) {
        merged[mergedPosition++] = foo[fooPosition++];
    }
    
    while (barPosition < barLength) {
        merged[mergedPosition++] = bar[barPosition++];
    }
    
    return merged;
}
```

### Объяснение реализации

1. **Инициализация:** Создаем результирующий массив и три указателя
2. **Основной цикл:** Пока оба массива не закончились, сравниваем элементы и добавляем меньший
3. **Копирование остатков:** После основного цикла копируем оставшиеся элементы из того массива, который не закончился

### Тест

```java
@Test
void givenTwoSortedArrays_whenMerged_thenReturnMergedSortedArray() {
    int[] foo = { 3, 7 };
    int[] bar = { 4, 8, 11 };
    int[] merged = { 3, 4, 7, 8, 11 };
    
    assertArrayEquals(merged, SortedArrays.merge(foo, bar));
}
```

### Альтернативная реализация (in-place для одного массива)

Если нужно объединить две части одного массива:

```java
public static void mergeInPlace(int[] arr, int left, int mid, int right) {
    int[] temp = new int[right - left + 1];
    int i = left, j = mid + 1, k = 0;
    
    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) {
            temp[k++] = arr[i++];
        } else {
            temp[k++] = arr[j++];
        }
    }
    
    while (i <= mid) {
        temp[k++] = arr[i++];
    }
    
    while (j <= right) {
        temp[k++] = arr[j++];
    }
    
    System.arraycopy(temp, 0, arr, left, temp.length);
}
```

## Kotlin Implementation

### Основная реализация

```kotlin
fun mergeK(foo: IntArray, bar: IntArray): IntArray {
    val fooLength = foo.size
    val barLength = bar.size
    val merged = IntArray(fooLength + barLength)
    
    var fooPosition = 0
    var barPosition = 0
    var mergedPosition = 0
    
    while (fooPosition < fooLength && barPosition < barLength) {
        if (foo[fooPosition] < bar[barPosition]) {
            merged[mergedPosition++] = foo[fooPosition++]
        } else {
            merged[mergedPosition++] = bar[barPosition++]
        }
    }
    
    while (fooPosition < fooLength) {
        merged[mergedPosition++] = foo[fooPosition++]
    }
    
    while (barPosition < barLength) {
        merged[mergedPosition++] = bar[barPosition++]
    }
    
    return merged
}
```

### In-place версия

```kotlin
fun mergeInPlaceK(arr: IntArray, left: Int, mid: Int, right: Int) {
    val temp = IntArray(right - left + 1)
    var i = left
    var j = mid + 1
    var k = 0
    
    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) {
            temp[k++] = arr[i++]
        } else {
            temp[k++] = arr[j++]
        }
    }
    
    while (i <= mid) {
        temp[k++] = arr[i++]
    }
    
    while (j <= right) {
        temp[k++] = arr[j++]
    }
    
    temp.copyInto(arr, left, 0, temp.size)
}
```

### Объединение без дубликатов

```kotlin
fun mergeWithoutDuplicatesK(foo: IntArray, bar: IntArray): IntArray {
    val result = mutableListOf<Int>()
    var i = 0
    var j = 0
    
    while (i < foo.size && j < bar.size) {
        when {
            foo[i] < bar[j] -> {
                if (result.isEmpty() || result.last() != foo[i]) {
                    result.add(foo[i])
                }
                i++
            }
            foo[i] > bar[j] -> {
                if (result.isEmpty() || result.last() != bar[j]) {
                    result.add(bar[j])
                }
                j++
            }
            else -> {
                if (result.isEmpty() || result.last() != foo[i]) {
                    result.add(foo[i])
                }
                i++
                j++
            }
        }
    }
    
    while (i < foo.size) {
        if (result.isEmpty() || result.last() != foo[i]) {
            result.add(foo[i])
        }
        i++
    }
    
    while (j < bar.size) {
        if (result.isEmpty() || result.last() != bar[j]) {
            result.add(bar[j])
        }
        j++
    }
    
    return result.toIntArray()
}
```

### Пример использования

```kotlin
fun main() {
    val foo = intArrayOf(3, 7)
    val bar = intArrayOf(4, 8, 11)
    val merged = mergeK(foo, bar)
    println(merged.contentToString()) // [3, 4, 7, 8, 11]
}
```

## Сложность

### Временная сложность

- **Все случаи:** O(n + m), где n и m - длины двух массивов

Каждый элемент из обоих массивов посещается ровно один раз.

### Пространственная сложность

- **С дополнительным массивом:** O(n + m) - для результирующего массива
- **In-place версия:** O(n + m) - для временного массива

## Особенности

- **Стабильность:** Алгоритм стабилен (сохраняет относительный порядок равных элементов)
- **Эффективность:** Линейная временная сложность
- **Простота:** Легко понять и реализовать
- **Применение:** Основа для Merge Sort

## Применение

Объединение отсортированных массивов используется в:

- **Merge Sort** - основной шаг алгоритма
- Объединении результатов из разных источников
- Слиянии отсортированных списков
- Операциях с базами данных (UNION с сортировкой)
- Обработке больших данных (MapReduce)

## Варианты задачи

### Вариант 1: Объединение без дубликатов

```java
public static int[] mergeWithoutDuplicates(int[] foo, int[] bar) {
    List<Integer> result = new ArrayList<>();
    int i = 0, j = 0;
    
    while (i < foo.length && j < bar.length) {
        if (foo[i] < bar[j]) {
            if (result.isEmpty() || result.get(result.size() - 1) != foo[i]) {
                result.add(foo[i]);
            }
            i++;
        } else if (foo[i] > bar[j]) {
            if (result.isEmpty() || result.get(result.size() - 1) != bar[j]) {
                result.add(bar[j]);
            }
            j++;
        } else {
            if (result.isEmpty() || result.get(result.size() - 1) != foo[i]) {
                result.add(foo[i]);
            }
            i++;
            j++;
        }
    }
    
    // Копируем остатки...
    return result.stream().mapToInt(x -> x).toArray();
}
```

### Вариант 2: Объединение k отсортированных массивов

Для объединения k массивов можно использовать:

1. Последовательно объединять по два массива: O(k * n)
2. Использовать приоритетную очередь (min-heap): O(n * log(k))

## Сравнение с другими операциями

| Операция | Временная сложность | Применение |
|----------|---------------------|------------|
| Объединение двух массивов | O(n + m) | Merge Sort, объединение результатов |
| Объединение k массивов | O(n * log(k)) | Слияние множества источников |
| Сортировка массива | O(n*log(n)) | Общая сортировка |

## Оптимизации

- **Проверка на пустые массивы:** Если один массив пуст, просто возвращаем другой
- **Проверка на уже отсортированный результат:** Если все элементы одного массива меньше всех элементов другого
- **Использование System.arraycopy:** Для копирования остатков вместо циклов

## Заключение

Объединение двух отсортированных массивов - это фундаментальная операция, которая лежит в основе многих алгоритмов. Понимание этого алгоритма важно для изучения более сложных алгоритмов, таких как Merge Sort и обработки больших данных.
