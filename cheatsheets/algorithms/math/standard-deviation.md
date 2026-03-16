---
title: "Стандартное отклонение (Standard Deviation)"
description: "Стандартное отклонение (σ) — мера разброса данных вокруг среднего. В документе описаны расчёт по генеральной совокупности (деление на n), выборочное стандартное отклонение (n−1) и варианты на Java и Kotlin."
tags: ["algorithms", "math", "standard-deviation"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Стандартное отклонение (`Standard Deviation`)

**Дата последнего обновления:** 2026-02-06

Стандартное отклонение (σ) — мера разброса данных вокруг среднего. В документе описаны расчёт по генеральной совокупности (деление на n), выборочное стандартное отклонение (n−1) и варианты на `Java` и Kotlin.

## Полезные ссылки

### Официальная документация
- [Math.sqrt() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#sqrt-double-)
- [Math.pow() (Java SE 8)](https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html#pow-double-double-)

### См. также
- [Вычисление расстояния](distance-between-points.md) — расстояние между точками
- [Вычисление площади круга](circle-area-calculation.md) — площадь круга

## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Математическое определение](#математическое-определение)
  - [Примеры](#примеры)
- [Формула стандартного отклонения](#формула-стандартного-отклонения)
- [Реализация на Java](#реализация-на-java)
- [Варианты реализации](#варианты-реализации)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)


## Описание алгоритма

Стандартное отклонение (σ) — мера разброса данных вокруг среднего. Ниже — расчёт в `Java` и Kotlin.

### Математическое определение

Стандартное отклонение показывает, насколько значения в наборе данных отклоняются от среднего значения. Чем больше стандартное отклонение, тем больше разброс данных.

### Примеры

Для массива [25, 5, 45, 68, 61, 46, 24, 95]: среднее 46.125, стандартное отклонение ≈ 26.732.

## Формула стандартного отклонения

σ = √(∑(Xi − μ)² / N), где μ — среднее, N — число элементов. Шаги: вычислить среднее; для каждого элемента — квадрат разности с средним; усреднить эти квадраты (дисперсия); извлечь квадратный корень.

## Реализация на Java

Генеральная совокупность: два прохода — среднее, затем сумма квадратов разностей и `Math.sqrt`.

```java
// Генеральная совокупность: дисперсия = сумма (x_i - mean)² / n
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

Пример: для массива [25, 5, 45, 68, 61, 46, 24, 95] результат ≈ 26.732179.

## Варианты реализации

### Вариант 1: Вычисление среднего отдельно

Вынесение среднего в отдельный метод для повторного использования.

```java
// Среднее и затем дисперсия по формуле σ² = Σ(x-μ)²/n
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

### Вариант 2: Выборочное стандартное отклонение (n−1)

Для выборки из генеральной совокупности используют знаменатель (n−1) для несмещённой оценки.

```java
// Выборочная дисперсия: деление на (n-1)
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

Два прохода через потоки: среднее, затем дисперсия и корень.

```java
// Через Streams: average() и map + average()
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

Класс, возвращающий среднее, дисперсию и σ.

```java
// Статистика: mean, variance, standardDeviation
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

## Реализация на Kotlin

```kotlin
// Генеральная и выборочная дисперсия в Kotlin
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

Время O(n): один проход для среднего, второй для суммы квадратов разностей. Память O(1) без учёта входного массива.

## Особенности

Результаты подвержены обычным ошибкам округления при работе с `double`. Алгоритм с двумя проходами прост и эффективен; для выборки достаточно заменить знаменатель на (n−1).

## Применение

Стандартное отклонение применяется в статистике, анализе данных, машинном обучении, финансовом анализе, контроле качества и научных расчётах.

## Когда использовать

Используйте стандартное отклонение по генеральной совокупности (деление на n), когда данные представляют всю совокупность. Выборочное (n−1) — когда данные являются выборкой и нужна несмещённая оценка дисперсии.

## Лучшие практики

Для выборки используйте знаменатель (n−1). Проверяйте длину массива перед делением; для пустого массива определите контракт (исключение или NaN). При очень больших массивах можно рассмотреть однопроходный алгоритм Уэлфорда для численной стабильности. В тестах проверяйте один элемент, два элемента, одинаковые значения. Явно указывайте в документации, какая формула используется — генеральная или выборочная.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Деление на ноль / NaN | Пустой массив или length=0 | Проверять array.length > 0, иначе бросать исключение или возвращать NaN |
| Сильная погрешность при больших данных | Два прохода, накопление ошибки | Использовать однопроходный алгоритм Уэлфорда |
| Путаница с n и n−1 | Несогласованность формул | Документировать: генеральная (n) или выборочная (n−1) |

## Частые вопросы

**В чём разница между генеральным и выборочным стандартным отклонением?** Генеральное — деление на N (вся совокупность); выборочное — деление на (n−1) при оценке по выборке, чтобы оценка дисперсии была несмещённой.

**Что возвращать для массива из одного элемента?** Для одного элемента разброс не определён; часто возвращают 0 или документируют особый случай.

**Когда нужен алгоритм Уэлфорда?** При очень больших массивах и суммировании квадратов разностей может теряться точность; однопроходный Уэлфорд уменьшает погрешность.

## Заключение

В документе описано вычисление стандартного отклонения в `Java` и Kotlin: по генеральной совокупности (n) и по выборке (n−1). Алгоритм — два прохода: среднее, затем дисперсия и квадратный корень.
