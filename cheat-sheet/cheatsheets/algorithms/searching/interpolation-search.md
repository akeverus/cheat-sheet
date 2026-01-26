# Interpolation Search

Кратко: интерполяционный поиск - это улучшение бинарного поиска для равномерно распределенных данных. Алгоритм оценивает позицию искомого элемента по формуле, а не всегда берёт середину массива.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Interpolation Search](https://www.geeksforgeeks.org/interpolation-search/)

### См. также
- `./binary-search.md` - бинарный поиск
- `./maximum-subarray.md` - максимальный подмассив

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Когда использовать](#когда-использовать)
- [Идея и формула](#идея-и-формула)
- [Пошаговый пример](#пошаговый-пример)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Интерполяционный поиск — это улучшение бинарного поиска для равномерно распределенных данных. Алгоритм оценивает позицию искомого элемента по формуле, а не всегда берёт середину массива.

## Когда использовать

- Массив отсортирован по возрастанию
- Данные распределены примерно равномерно
- Нужна скорость быстрее бинарного поиска в среднем

## Идея и формула

Вместо середины диапазона мы вычисляем «зонд» (probe) по формуле:

```
probe = low + (high - low) * (item - data[low]) / (data[high] - data[low])
```

Где:

- `low` — левый индекс диапазона поиска
- `high` — правый индекс диапазона поиска
- `data` — отсортированный массив
- `item` — искомый элемент

Если `data[high] == data[low]`, алгоритм должен остановиться (деление на ноль).

## Пошаговый пример

Ищем `84` в отсортированном массиве длины 8:

- `low = 0`, `high = 7`
- формула даёт `probe = 5`
- элемент `data[5] = 73`, значит смещаем `low` вправо (`low = probe + 1`)
- следующий `probe = 6`, элемент `data[6] = 84` — найден

### Визуализация

```
Массив: [10, 20, 30, 40, 50, 60, 70, 80, 90, 100]
Ищем: 70

Итерация 1:
low = 0, high = 9
probe = 0 + (9 - 0) * (70 - 10) / (100 - 10) = 0 + 9 * 60 / 90 = 6
data[6] = 70 ✓ Найден!
```

## Java Implementation

Инициализация границ:

```java
int highEnd = data.length - 1;
int lowEnd = 0;
```

Шаг цикла:

```java
while (item >= data[lowEnd] && item <= data[highEnd] && lowEnd <= highEnd) {
    int probe = lowEnd + (highEnd - lowEnd) * (item - data[lowEnd])
            / (data[highEnd] - data[lowEnd]);
}
```

Полная реализация:

```java
public int interpolationSearch(int[] data, int item) {
    int highEnd = data.length - 1;
    int lowEnd = 0;
    
    while (item >= data[lowEnd] && item <= data[highEnd] && lowEnd <= highEnd) {
        if (data[highEnd] == data[lowEnd]) {
            return data[lowEnd] == item ? lowEnd : -1;
        }
        
        int probe = lowEnd + (highEnd - lowEnd) * (item - data[lowEnd])
                / (data[highEnd] - data[lowEnd]);
        
        if (data[probe] == item) {
            return probe;
        }
        
        if (data[probe] < item) {
            lowEnd = probe + 1;
        } else {
            highEnd = probe - 1;
        }
    }
    
    return -1;
}
```

### Тестирование

```java
@Test
void givenUniformlyDistributedArray_whenInterpolationSearch_thenFindElement() {
    int[] data = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
    int index = interpolationSearch(data, 70);
    assertEquals(6, index);
}
```

## Kotlin Implementation

### Базовая реализация

```kotlin
fun interpolationSearchK(data: IntArray, item: Int): Int {
    var highEnd = data.size - 1
    var lowEnd = 0
    
    while (item >= data[lowEnd] && item <= data[highEnd] && lowEnd <= highEnd) {
        if (data[highEnd] == data[lowEnd]) {
            return if (data[lowEnd] == item) lowEnd else -1
        }
        
        val probe = lowEnd + (highEnd - lowEnd) * (item - data[lowEnd]) 
            / (data[highEnd] - data[lowEnd])
        
        when {
            data[probe] == item -> return probe
            data[probe] < item -> lowEnd = probe + 1
            else -> highEnd = probe - 1
        }
    }
    
    return -1
}
```

### Функциональный стиль (рекурсивный)

```kotlin
fun interpolationSearchRecursiveK(
    data: IntArray, 
    item: Int, 
    lowEnd: Int = 0, 
    highEnd: Int = data.size - 1
): Int {
    if (lowEnd > highEnd || item < data[lowEnd] || item > data[highEnd]) {
        return -1
    }
    
    if (data[highEnd] == data[lowEnd]) {
        return if (data[lowEnd] == item) lowEnd else -1
    }
    
    val probe = lowEnd + (highEnd - lowEnd) * (item - data[lowEnd]) 
        / (data[highEnd] - data[lowEnd])
    
    return when {
        data[probe] == item -> probe
        data[probe] < item -> interpolationSearchRecursiveK(data, item, probe + 1, highEnd)
        else -> interpolationSearchRecursiveK(data, item, lowEnd, probe - 1)
    }
}
```

### Пример использования

```kotlin
fun main() {
    val data = intArrayOf(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)
    
    val index = interpolationSearchK(data, 70)
    println("Found at index: $index") // Found at index: 6
    
    val index2 = interpolationSearchRecursiveK(data, 50)
    println("Found at index: $index2") // Found at index: 4
}
```

## Сложность

### Временная сложность

- **Лучший случай:** O(1) - элемент находится точно в вычисленной позиции
- **Средний случай:** O(log(log(n))) - при равномерном распределении
- **Худший случай:** O(n) - при сильной неравномерности распределения

### Пространственная сложность

- **Все случаи:** O(1) - только константная дополнительная память

## Особенности

- **Быстрее бинарного поиска:** В среднем работает быстрее для равномерно распределенных данных
- **Требует равномерное распределение:** Эффективность сильно зависит от распределения данных
- **Чувствителен к неравномерности:** Может деградировать до O(n) при неравномерном распределении

## Сравнение с бинарным поиском

| Характеристика | Бинарный поиск | Интерполяционный поиск |
|----------------|----------------|------------------------|
| Временная сложность (среднее) | O(log(n)) | O(log(log(n))) |
| Временная сложность (худшее) | O(log(n)) | O(n) |
| Требования | Отсортированный массив | Отсортированный + равномерное распределение |
| Надежность | Стабильная | Зависит от распределения |

## Применение

Интерполяционный поиск особенно полезен в следующих случаях:

- Равномерно распределенные данные (например, телефонные номера, индексы)
- Большие отсортированные массивы с равномерным распределением
- Когда нужна максимальная скорость поиска
- В базах данных с равномерно распределенными ключами

## Ограничения

- Требует равномерное распределение данных
- Может быть медленнее бинарного поиска при неравномерном распределении
- Более сложная реализация
- Чувствителен к выбросам в данных

## Когда использовать

### Используйте интерполяционный поиск, когда:

- Данные равномерно распределены
- Нужна максимальная скорость поиска
- Массив большой и отсортирован
- Распределение данных известно и равномерно

### Используйте бинарный поиск, когда:

- Распределение данных неизвестно
- Нужна стабильная производительность
- Данные могут быть неравномерно распределены
- Требуется простота реализации
