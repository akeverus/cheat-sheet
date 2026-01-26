# Top N Frequent Elements

Кратко: поиск n самых часто встречающихся элементов в массиве. Рассматриваются три подхода: HashMap + PriorityQueue, Stream API, и TreeMap.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Find k most frequent in array](https://www.geeksforgeeks.org/find-k-most-frequent-in-given-array/)

### См. также
- `./k-largest-elements.md` - поиск k наибольших элементов
- `./find-max-element.md` - поиск k-го по величине элемента

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Подход 1: HashMap + PriorityQueue](#подход-1-hashmap--priorityqueue)
- [Подход 2: Stream API](#подход-2-stream-api)
- [Подход 3: TreeMap](#подход-3-treemap)
- [Сравнение подходов](#сравнение-подходов)
- [Сложность](#сложность)

## Описание алгоритма

В этом коротком руководстве мы увидим, как найти n наиболее часто встречающихся элементов в массиве Java.

Мы можем использовать HashMap для подсчета вхождений каждого элемента и PriorityQueue для определения приоритетов элементов на основе их количества.

## Подход 1: HashMap + PriorityQueue

Это позволяет нам эффективно находить n наиболее часто встречающихся элементов в массиве:

```java
public static List<Integer> findByHashMapAndPriorityQueue(Integer[] array, int n) {
    Map<Integer, Integer> countMap = new HashMap<>();
    
    for (Integer i : array) {
        countMap.put(i, countMap.getOrDefault(i, 0) + 1);
    }
    
    PriorityQueue<Integer> heap = new PriorityQueue<>((a, b) -> countMap.get(b) - countMap.get(a));
    heap.addAll(countMap.keySet());
    
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < n && !heap.isEmpty(); i++) {
        result.add(heap.poll());
    }
    
    return result;
}
```

### Объяснение алгоритма

1. **Подсчет частоты:** Используем HashMap для подсчета вхождений каждого элемента
2. **Создание кучи:** Создаем PriorityQueue с компаратором, который сравнивает элементы по частоте (убывание)
3. **Извлечение топ-n:** Извлекаем n элементов с наибольшей частотой

### Тестирование

Проверим наши методы:

```java
Integer[] inputArray = {1, 2, 3, 2, 2, 1, 4, 5, 6, 1, 2, 3};
Integer[] outputArray = {2, 1, 3};

assertThat(findByHashMapAndPriorityQueue(inputArray, 3)).containsExactly(outputArray);
```

## Подход 2: Stream API

Мы также можем использовать Stream API для создания карты для подсчета вхождений каждого элемента, сортировки записей на карте по частоте в порядке убывания, а затем извлечения n наиболее часто встречающихся элементов:

```java
public static List<Integer> findByStream(Integer[] arr, int n) {
    return Arrays.stream(arr)
        .collect(Collectors.groupingBy(i -> i, Collectors.counting()))
        .entrySet()
        .stream()
        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
        .map(Map.Entry::getKey)
        .limit(n)
        .collect(Collectors.toList());
}
```

### Объяснение алгоритма

1. **Группировка:** `groupingBy` группирует элементы и подсчитывает их частоту
2. **Сортировка:** Сортируем записи по значению (частоте) в убывающем порядке
3. **Извлечение:** Берем ключи (элементы) и ограничиваем результат n элементами

### Пример использования

```java
Integer[] arr = {1, 2, 3, 2, 2, 1, 4, 5, 6, 1, 2, 3};
List<Integer> top3 = findByStream(arr, 3);
// Результат: [2, 1, 3]
```

## Подход 3: TreeMap

В качестве альтернативы мы можем использовать TreeMap и создать собственный компаратор, который сравнивает значения в объектах Map.Entry на карте по частоте в порядке убывания:

```java
public static List<Integer> findByTreeMap(Integer[] arr, int n) {
    Map<Integer, Integer> countMap = new TreeMap<>(Collections.reverseOrder());
    
    for (int i : arr) {
        countMap.put(i, countMap.getOrDefault(i, 0) + 1);
    }
    
    List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(countMap.entrySet());
    sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
    
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < n && i < sortedEntries.size(); i++) {
        result.add(sortedEntries.get(i).getKey());
    }
    
    return result;
}
```

### Объяснение алгоритма

1. **Подсчет:** Используем TreeMap для подсчета частоты
2. **Сортировка:** Сортируем записи по частоте в убывающем порядке
3. **Извлечение:** Берем первые n элементов

### Примечание

Обратите внимание, что TreeMap в данном случае сортирует по ключам, а не по значениям. Поэтому нам все равно нужно дополнительная сортировка по значениям.

## Сравнение подходов

| Характеристика | HashMap + PriorityQueue | Stream API | TreeMap |
|----------------|-------------------------|------------|---------|
| Временная сложность | O(n*log(m)) | O(m*log(m)) | O(m*log(m)) |
| Пространственная сложность | O(m) | O(m) | O(m) |
| Читаемость | Средняя | Высокая | Средняя |
| Производительность | Высокая | Средняя | Средняя |
| Применение | Общий случай | Функциональный стиль | Когда нужна сортировка |

где:
- n - количество уникальных элементов
- m - размер входного массива

## Kotlin Implementation

### Подход 1: HashMap + PriorityQueue

```kotlin
fun findByHashMapAndPriorityQueueK(array: Array<Int>, n: Int): List<Int> {
    val countMap = array.groupingBy { it }.eachCount()
    
    val heap = PriorityQueue<Int> { a, b -> 
        (countMap[b] ?: 0).compareTo(countMap[a] ?: 0)
    }
    heap.addAll(countMap.keys)
    
    return generateSequence { if (heap.isNotEmpty()) heap.poll() else null }
        .take(n)
        .toList()
}
```

### Подход 2: Stream API

```kotlin
fun findByStreamK(arr: Array<Int>, n: Int): List<Int> {
    return arr.groupingBy { it }.eachCount()
        .entries
        .sortedByDescending { it.value }
        .take(n)
        .map { it.key }
}
```

### Подход 3: TreeMap

```kotlin
fun findByTreeMapK(arr: Array<Int>, n: Int): List<Int> {
    val countMap = arr.groupingBy { it }.eachCount().toMutableMap()
    
    val sortedEntries = countMap.entries.sortedByDescending { it.value }
    
    return sortedEntries.take(n).map { it.key }
}
```

### Пример использования

```kotlin
fun main() {
    val inputArray = arrayOf(1, 2, 3, 2, 2, 1, 4, 5, 6, 1, 2, 3)
    
    val top3a = findByHashMapAndPriorityQueueK(inputArray, 3)
    println("Top 3 (PriorityQueue): $top3a") // [2, 1, 3]
    
    val top3b = findByStreamK(inputArray, 3)
    println("Top 3 (Stream): $top3b") // [2, 1, 3]
}
```

## Сложность

### Подход 1: HashMap + PriorityQueue

- **Временная сложность:** O(m + n*log(n)), где m - размер массива, n - количество уникальных элементов
  - Подсчет частоты: O(m)
  - Построение кучи: O(n*log(n))
  - Извлечение топ-n: O(n*log(n))
- **Пространственная сложность:** O(n) - для HashMap и PriorityQueue

### Подход 2: Stream API

- **Временная сложность:** O(m + n*log(n))
  - Группировка: O(m)
  - Сортировка: O(n*log(n))
- **Пространственная сложность:** O(n) - для Map и промежуточных коллекций

### Подход 3: TreeMap

- **Временная сложность:** O(m + n*log(n))
  - Подсчет: O(m*log(n))
  - Сортировка: O(n*log(n))
- **Пространственная сложность:** O(n) - для TreeMap и списка

## Особенности

- **Гибкость:** Все подходы работают с любыми типами данных
- **Эффективность:** PriorityQueue оптимален для больших массивов
- **Читаемость:** Stream API наиболее читаем

## Применение

Поиск n самых частых элементов используется в:

- Анализе данных
- Обработке текста (топ слов)
- Рекомендательных системах
- Логировании (топ ошибок)
- Аналитике веб-сайтов

## Оптимизация

### Минимальная куча для больших n

Если n близко к количеству уникальных элементов, можно использовать минимальную кучу:

```java
public static List<Integer> findByMinHeap(Integer[] arr, int n) {
    Map<Integer, Integer> countMap = new HashMap<>();
    for (int i : arr) {
        countMap.put(i, countMap.getOrDefault(i, 0) + 1);
    }
    
    PriorityQueue<Map.Entry<Integer, Integer>> minHeap = 
        new PriorityQueue<>((a, b) -> a.getValue() - b.getValue());
    
    for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
        minHeap.offer(entry);
        if (minHeap.size() > n) {
            minHeap.poll();
        }
    }
    
    return minHeap.stream()
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
}
```

## Когда использовать

### Используйте HashMap + PriorityQueue, когда:

- Нужна максимальная производительность
- Массив большой
- n относительно мало

### Используйте Stream API, когда:

- Нужна читаемость кода
- Работаете в функциональном стиле
- Массив среднего размера

### Используйте TreeMap, когда:

- Нужна дополнительная сортировка по ключам
- Работаете с отсортированными данными

## Заключение

В этом руководстве мы рассмотрели три подхода к поиску n самых частых элементов в массиве. HashMap + PriorityQueue является наиболее эффективным решением для большинства случаев, обеспечивая хороший баланс между производительностью и простотой реализации.
