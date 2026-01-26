# Pairs with Given Sum

Кратко: поиск всех пар чисел в массиве, сумма которых равна заданному числу. Рассматриваются два подхода: поиск всех пар и поиск только уникальных пар.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Find pairs with given sum](https://www.geeksforgeeks.org/find-pairs-given-sum-array/)

### См. также
- `./diagonal-array-traversal.md` - перебор массива по диагонали
- `./maximum-subarray.md` - проблема максимального подмассива

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом кратком руководстве мы покажем, как реализовать алгоритм поиска всех пар чисел в массиве, сумма которых равна заданному числу. Мы сосредоточимся на двух подходах к проблеме.

При первом подходе мы найдем все такие пары независимо от их уникальности. Во втором мы найдем только уникальные комбинации чисел, удалив лишние пары.

Для каждого подхода мы представим две реализации - традиционную реализацию с использованием циклов for и вторую с использованием Java 8 Stream API.

Для наших демонстраций мы будем искать все пары чисел, сумма которых равна 6, используя следующий входной массив:

```java
int[] input = { 2, 4, 3, 3 };
```

## Java Implementation

### Подход 1: Поиск всех пар

Мы будем перебирать массив целых чисел, находя все пары (i и j), которые в сумме дают заданное число (sum), используя подход грубой силы с вложенным циклом. Этот алгоритм будет иметь сложность времени выполнения O(n²).

При таком подходе наш алгоритм должен возвращать:

```
{2,4}, {4,2}, {3,3}, {3,3}
```

В каждом из алгоритмов, когда мы находим целевую пару чисел, сумма которых равна целевому числу, мы собираем пару с помощью служебного метода `addPairs(i, j)`.

### Реализация с циклами for

Первый способ, которым мы могли бы подумать, чтобы реализовать решение, - это использовать традиционный цикл for:

```java
public List<int[]> findAllPairs(int[] input, int sum) {
    List<int[]> pairs = new ArrayList<>();
    
    for (int i = 0; i < input.length; i++) {
        for (int j = 0; j < input.length; j++) {
            if (j != i && (input[i] + input[j]) == sum) {
                pairs.add(new int[]{input[i], input[j]});
            }
        }
    }
    
    return pairs;
}
```

### Реализация с Java 8 Stream API

Здесь мы используем метод `IntStream.range` для создания последовательного потока чисел. Затем мы фильтруем их по нашему условию: число 1 + число 2 = сумма:

```java
public List<int[]> findAllPairsStream(int[] input, int sum) {
    List<int[]> pairs = new ArrayList<>();
    
    IntStream.range(0, input.length)
        .forEach(i -> IntStream.range(0, input.length)
            .filter(j -> i != j && input[i] + input[j] == sum)
            .forEach(j -> pairs.add(new int[]{input[i], input[j]}))
        );
    
    return pairs;
}
```

## Подход 2: Поиск уникальных пар

Для этого примера нам придется разработать более умный алгоритм, который возвращает только уникальные комбинации чисел, опуская избыточные пары.

Для этого мы добавим каждый элемент в хеш-карту (без сортировки), предварительно проверив, была ли уже показана пара. Если нет, мы получим и пометим его, как показано (установите поле значения как null).

Соответственно, используя тот же входной массив, что и раньше, и целевую сумму 6, наш алгоритм должен возвращать только различные комбинации чисел:

```
{2,4}, {3,3}
```

### Реализация с циклами for

Если мы используем традиционный цикл for, у нас будет:

```java
public List<int[]> findUniquePairs(int[] input, int sum) {
    List<int[]> pairs = new ArrayList<>();
    Map<Integer, Integer> pairsMap = new HashMap<>();
    
    for (int i : input) {
        if (pairsMap.containsKey(i)) {
            if (pairsMap.get(i) != null) {
                pairs.add(new int[]{i, sum - i});
            }
            pairsMap.put(sum - i, null);
        } else if (!pairsMap.containsValue(i)) {
            pairsMap.put(sum - i, i);
        }
    }
    
    return pairs;
}
```

Обратите внимание, что эта реализация улучшает предыдущую сложность, поскольку мы используем только один цикл for, поэтому у нас будет O(n).

### Реализация с Java 8 Stream API

Теперь решим задачу с помощью Java 8 и Stream API:

```java
public List<int[]> findUniquePairsStream(int[] input, int sum) {
    List<int[]> pairs = new ArrayList<>();
    Map<Integer, Integer> pairsMap = new HashMap<>();
    
    IntStream.range(0, input.length).forEach(i -> {
        if (pairsMap.containsKey(input[i])) {
            if (pairsMap.get(input[i]) != null) {
                pairs.add(new int[]{input[i], sum - input[i]});
            }
            pairsMap.put(sum - input[i], null);
        } else if (!pairsMap.containsValue(input[i])) {
            pairsMap.put(sum - input[i], input[i]);
        }
    });
    
    return pairs;
}
```

### Тестирование

```java
@Test
void givenArray_whenFindUniquePairs_thenReturnUniquePairs() {
    int[] input = { 2, 4, 3, 3 };
    int sum = 6;
    List<int[]> pairs = findUniquePairs(input, sum);
    
    assertEquals(2, pairs.size());
    assertArrayEquals(new int[]{2, 4}, pairs.get(0));
    assertArrayEquals(new int[]{3, 3}, pairs.get(1));
}
```

## Kotlin Implementation

### Подход 1: Поиск всех пар

```kotlin
fun findAllPairsK(input: IntArray, sum: Int): List<IntArray> {
    val pairs = mutableListOf<IntArray>()
    
    for (i in input.indices) {
        for (j in input.indices) {
            if (j != i && input[i] + input[j] == sum) {
                pairs.add(intArrayOf(input[i], input[j]))
            }
        }
    }
    
    return pairs
}

fun findAllPairsFunctionalK(input: IntArray, sum: Int): List<IntArray> {
    return input.indices.flatMap { i ->
        input.indices
            .filter { j -> i != j && input[i] + input[j] == sum }
            .map { j -> intArrayOf(input[i], input[j]) }
    }
}
```

### Подход 2: Поиск уникальных пар

```kotlin
fun findUniquePairsK(input: IntArray, sum: Int): List<IntArray> {
    val pairs = mutableListOf<IntArray>()
    val pairsMap = mutableMapOf<Int, Int?>()
    
    for (i in input) {
        if (pairsMap.containsKey(i)) {
            if (pairsMap[i] != null) {
                pairs.add(intArrayOf(i, sum - i))
            }
            pairsMap[sum - i] = null
        } else if (!pairsMap.containsValue(i)) {
            pairsMap[sum - i] = i
        }
    }
    
    return pairs
}

fun findUniquePairsFunctionalK(input: IntArray, sum: Int): List<IntArray> {
    val pairs = mutableListOf<IntArray>()
    val pairsMap = mutableMapOf<Int, Int?>()
    
    input.forEach { i ->
        when {
            pairsMap.containsKey(i) && pairsMap[i] != null -> {
                pairs.add(intArrayOf(i, sum - i))
                pairsMap[sum - i] = null
            }
            !pairsMap.containsValue(i) -> {
                pairsMap[sum - i] = i
            }
        }
    }
    
    return pairs
}
```

### Оптимизированный подход с HashMap

```kotlin
fun findPairsOptimizedK(input: IntArray, sum: Int): List<Pair<Int, Int>> {
    val pairs = mutableListOf<Pair<Int, Int>>()
    val seen = mutableSetOf<Int>()
    
    for (num in input) {
        val complement = sum - num
        if (seen.contains(complement)) {
            pairs.add(Pair(minOf(num, complement), maxOf(num, complement)))
        }
        seen.add(num)
    }
    
    return pairs.distinct()
}
```

### Пример использования

```kotlin
fun main() {
    val input = intArrayOf(2, 4, 3, 3)
    val sum = 6
    
    val allPairs = findAllPairsK(input, sum)
    println("All pairs: ${allPairs.size}") // 4
    
    val uniquePairs = findUniquePairsK(input, sum)
    println("Unique pairs: ${uniquePairs.size}") // 2
}
```

## Сравнение подходов

| Характеристика | Подход 1 (все пары) | Подход 2 (уникальные пары) |
|----------------|---------------------|----------------------------|
| Временная сложность | O(n²) | O(n) |
| Пространственная сложность | O(n²) | O(n) |
| Результат | Все пары, включая дубликаты | Только уникальные пары |
| Применение | Когда нужны все комбинации | Когда нужны только уникальные |

## Сложность

### Подход 1: Поиск всех пар

- **Временная сложность:** O(n²) - два вложенных цикла
- **Пространственная сложность:** O(n²) - для хранения всех пар

### Подход 2: Поиск уникальных пар

- **Временная сложность:** O(n) - один проход по массиву
- **Пространственная сложность:** O(n) - для хранения HashMap

## Особенности

- **Два подхода:** Разные решения для разных требований
- **Оптимизация:** Второй подход значительно быстрее
- **Гибкость:** Можно использовать циклы или Stream API

## Применение

Поиск пар с заданной суммой используется в:

- Задачах на собеседованиях
- Алгоритмах двух указателей
- Поиске подмассивов с заданной суммой
- Оптимизации запросов
- Анализе данных

## Альтернативные решения

### Использование HashSet

```java
public List<int[]> findPairsWithHashSet(int[] input, int sum) {
    List<int[]> pairs = new ArrayList<>();
    Set<Integer> seen = new HashSet<>();
    
    for (int num : input) {
        int complement = sum - num;
        if (seen.contains(complement)) {
            pairs.add(new int[]{num, complement});
        }
        seen.add(num);
    }
    
    return pairs;
}
```

### Сортировка и два указателя

```java
public List<int[]> findPairsWithTwoPointers(int[] input, int sum) {
    Arrays.sort(input);
    List<int[]> pairs = new ArrayList<>();
    int left = 0, right = input.length - 1;
    
    while (left < right) {
        int currentSum = input[left] + input[right];
        if (currentSum == sum) {
            pairs.add(new int[]{input[left], input[right]});
            left++;
            right--;
        } else if (currentSum < sum) {
            left++;
        } else {
            right--;
        }
    }
    
    return pairs;
}
```

## Заключение

В этой статье мы объяснили несколько различных способов найти все пары, которые суммируют заданное число в Java. Мы видели два разных решения, каждое из которых использует два основных метода Java. Выбор подхода зависит от конкретных требований задачи.
