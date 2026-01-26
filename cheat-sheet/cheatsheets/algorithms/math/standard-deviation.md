# Standard Deviation

Кратко: расчет стандартного отклонения (σ) - меры разброса данных вокруг среднего значения. Рассматривается реализация с использованием Java Math.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Java Math.sqrt() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#sqrt-double-)
- [Java Math.pow() Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#pow-double-double-)

### См. также
- `./distance-between-points.md` - вычисление расстояния
- `./circle-area-calculation.md` - вычисление площади круга

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Формула стандартного отклонения](#формула-стандартного-отклонения)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Стандартное отклонение (символ сигма - σ) является мерой разброса данных вокруг среднего значения.

В этом кратком руководстве мы увидим, как рассчитать стандартное отклонение в Java.

### Математическое определение

Стандартное отклонение показывает, насколько значения в наборе данных отклоняются от среднего значения. Чем больше стандартное отклонение, тем больше разброс данных.

### Примеры

Для массива `[25, 5, 45, 68, 61, 46, 24, 95]`:
- Среднее значение: 46.125
- Стандартное отклонение: ≈ 26.732

## Формула стандартного отклонения

Стандартное отклонение вычисляется по формуле квадратного корня из (∑(Xi - ų)²)/N, где:

1. ∑ - сумма каждого элемента
2. Xi - каждый элемент массива
3. ų - среднее значение элементов массива
4. N - количество элементов

### Шаги вычисления

1. Вычислить среднее значение всех элементов
2. Для каждого элемента вычислить квадрат разности между элементом и средним значением
3. Найти среднее значение этих квадратов разностей
4. Извлечь квадратный корень из результата

## Java Implementation

### Реализация

Мы можем легко рассчитать стандартное отклонение с помощью класса Java Math:

```java
public static double calculateStandardDeviation(double[] array) {
    double sum = 0.0;
    
    for (double i : array) {
        sum += i;
    }
    
    int length = array.length;
    double mean = sum / length;
    
    double standardDeviation = 0.0;
    
    for (double num : array) {
        standardDeviation += Math.pow(num - mean, 2);
    }
    
    return Math.sqrt(standardDeviation / length);
}
```

### Пример использования

```java
double[] array = {25, 5, 45, 68, 61, 46, 24, 95};

System.out.println("List of elements: " + Arrays.toString(array));

double standardDeviation = calculateStandardDeviation(array);

System.out.format("Standard Deviation = %.6f", standardDeviation);
```

Результат будет выглядеть так:

```
List of elements: [25.0, 5.0, 45.0, 68.0, 61.0, 46.0, 24.0, 95.0]
Standard Deviation = 26.732179
```

## Варианты реализации

### Вариант 1: Вычисление среднего отдельно

```java
public static double calculateMean(double[] array) {
    double sum = 0.0;
    for (double value : array) {
        sum += value;
    }
    return sum / array.length;
}

public static double calculateStandardDeviationSeparated(double[] array) {
    double mean = calculateMean(array);
    double sumSquaredDifferences = 0.0;
    
    for (double value : array) {
        double difference = value - mean;
        sumSquaredDifferences += difference * difference;
    }
    
    double variance = sumSquaredDifferences / array.length;
    return Math.sqrt(variance);
}
```

### Вариант 2: Выборочное стандартное отклонение (n-1)

```java
public static double calculateSampleStandardDeviation(double[] array) {
    if (array.length <= 1) {
        return 0.0;
    }
    
    double mean = calculateMean(array);
    double sumSquaredDifferences = 0.0;
    
    for (double value : array) {
        double difference = value - mean;
        sumSquaredDifferences += difference * difference;
    }
    
    // Используем (n-1) для выборочного стандартного отклонения
    double variance = sumSquaredDifferences / (array.length - 1);
    return Math.sqrt(variance);
}
```

### Вариант 3: Использование Java Streams

```java
public static double calculateStandardDeviationStreams(double[] array) {
    double mean = Arrays.stream(array)
        .average()
        .orElse(0.0);
    
    double variance = Arrays.stream(array)
        .map(x -> Math.pow(x - mean, 2))
        .average()
        .orElse(0.0);
    
    return Math.sqrt(variance);
}
```

### Вариант 4: Вычисление дисперсии и стандартного отклонения

```java
public static class Statistics {
    private final double mean;
    private final double variance;
    private final double standardDeviation;
    
    public Statistics(double[] array) {
        this.mean = calculateMean(array);
        this.variance = calculateVariance(array, mean);
        this.standardDeviation = Math.sqrt(variance);
    }
    
    private double calculateVariance(double[] array, double mean) {
        double sumSquaredDifferences = 0.0;
        for (double value : array) {
            sumSquaredDifferences += Math.pow(value - mean, 2);
        }
        return sumSquaredDifferences / array.length;
    }
    
    public double getMean() {
        return mean;
    }
    
    public double getVariance() {
        return variance;
    }
    
    public double getStandardDeviation() {
        return standardDeviation;
    }
}
```

## Kotlin Implementation

### Основная реализация

```kotlin
fun calculateStandardDeviationK(array: DoubleArray): Double {
    val sum = array.sum()
    val length = array.size
    val mean = sum / length
    
    val standardDeviation = array.sumOf { Math.pow(it - mean, 2.0) }
    return Math.sqrt(standardDeviation / length)
}

fun calculateMeanK(array: DoubleArray): Double {
    return array.average()
}

fun calculateSampleStandardDeviationK(array: DoubleArray): Double {
    if (array.size <= 1) {
        return 0.0
    }
    
    val mean = calculateMeanK(array)
    val sumSquaredDifferences = array.sumOf { (it - mean) * (it - mean) }
    val variance = sumSquaredDifferences / (array.size - 1)
    return Math.sqrt(variance)
}
```

### Пример использования

```kotlin
fun main() {
    val array = doubleArrayOf(25.0, 5.0, 45.0, 68.0, 61.0, 46.0, 24.0, 95.0)
    val stdDev = calculateStandardDeviationK(array)
    println("Standard Deviation = %.6f".format(stdDev))
}
```

## Сложность

### Временная сложность

- **Все подходы:** O(n) - где n - количество элементов в массиве
  - Первый проход: O(n) для вычисления среднего
  - Второй проход: O(n) для вычисления квадратов разностей

### Пространственная сложность

- **Все подходы:** O(1) - только константная память (не считая входного массива)

## Особенности

- **Точность:** Результаты могут иметь небольшие ошибки округления из-за операций с плавающей точкой
- **Производительность:** Линейная сложность делает алгоритм эффективным
- **Гибкость:** Легко адаптировать для выборочного стандартного отклонения

## Применение

Стандартное отклонение используется в:

- Статистике
- Анализе данных
- Машинном обучении
- Финансовом анализе
- Контроле качества
- Научных исследованиях

## Когда использовать

### Используйте стандартное отклонение, когда:

- Нужно измерить разброс данных
- Важна статистическая валидность
- Анализируете распределение данных

### Используйте выборочное стандартное отклонение, когда:

- Работаете с выборкой из генеральной совокупности
- Нужна несмещенная оценка
- Размер выборки мал

## Заключение

В этом кратком руководстве мы узнали, как рассчитать стандартное отклонение в Java. Алгоритм использует два прохода по массиву: первый для вычисления среднего значения, второй для вычисления квадратов разностей и извлечения квадратного корня.
