---
title: "Градиентный спуск (Gradient Descent)"
description: "Градиентный спуск — итеративный алгоритм поиска локального минимума функции. В документе описаны вариант с возвратом (backtracking) без явной производной и вариант с вычислением градиента в Java и Kotlin, а также стохастический и адаптивный варианты."
tags:
  - algorithms
  - math
  - gradient-descent
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Градиентный спуск (Gradient Descent)

Градиентный спуск — итеративный алгоритм поиска локального минимума функции. В документе описаны вариант с возвратом (backtracking) без явной производной и вариант с вычислением градиента в `Java` и Kotlin, а также стохастический и адаптивный варианты.

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Gradient Descent Algorithm](https://www.geeksforgeeks.org/gradient-descent-algorithm-and-its-variants/)

### См. также
- [[k-means-clustering-java|Кластеризация K-средних]] — K-means
- [[fibonacci-sequence|Ряд Фибоначчи]] — ряд Фибоначчи

- [[line-intersection|Пересечение прямых (Line Intersection)]]
- [[circle-area-calculation|Вычисление площади круга (Circle Area Calculation)]]
- [[coprime-numbers|Взаимно простые числа (Coprime Numbers)]]
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Основные понятия](#основные-понятия)
  - [Важные замечания](#важные-замечания)
- [Алгоритм с возвратом](#алгоритм-с-возвратом)
- [Реализация на Java](#реализация-на-java)
  - [Полная реализация](#полная-реализация)
  - [Версия с вычислением градиента (Java)](#версия-с-вычислением-градиента-java)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: С адаптивным размером шага](#вариант-1-с-адаптивным-размером-шага)
  - [Вариант 2: Стохастический градиентный спуск](#вариант-2-стохастический-градиентный-спуск)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Версия с вычислением градиента (Kotlin)](#версия-с-вычислением-градиента-kotlin)
  - [Адаптивный градиентный спуск](#адаптивный-градиентный-спуск)
  - [Стохастический градиентный спуск](#стохастический-градиентный-спуск)
  - [Пример использования](#пример-использования)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Градиентный спуск итеративно сдвигает точку в направлении убывания функции (вдоль антиградиента) и используется в машинном обучении для минимизации функции потерь. Находит локальный минимум; результат зависит от начальной точки.

### Основные понятия

Алгоритму нужны функция и начальная точка. Вариант с возвратом (backtracking): при увеличении значения функции разворачиваем направление и уменьшаем шаг. Реализация без явной производной работает и для недифференцируемых функций (по конечным разностям или эвристике).

### Важные замечания

Спуск находит локальный, а не обязательно глобальный минимум. Сходимость не гарантирована; ограничиваем число итераций.

## Алгоритм с возвратом

Параметры: точность остановки и коэффициент шага. Положительный шаг — увеличение x; если y вырос — меняем знак и уменьшаем шаг.

## Реализация на Java

Вход: функция `Function<Double, Double>` и начальная точка. Цикл: пока шаг больше точности и есть итерации — пересчитываем y; при росте y инвертируем направление и уменьшаем шаг (`stepCoefficient = -stepCoefficient / 2`).

### Полная реализация

```java
// Градиентный спуск с возвратом: без явной производной
public static double gradientDescent(Function<Double, Double> f, double initialX) {
    double precision = 0.000001;
    double stepCoefficient = 0.1;
    int iter = 100;

    double previousX = initialX;
    double previousY = f.apply(previousX);
    double currentX = initialX + stepCoefficient * previousY;
    double previousStep = 1.0;

    while (previousStep > precision && iter > 0) {
        iter--;
        double currentY = f.apply(currentX);

        if (currentY > previousY) {
            stepCoefficient = -stepCoefficient / 2;
        }

        previousX = currentX;
        currentX += stepCoefficient * previousY;
        previousY = currentY;
        previousStep = StrictMath.abs(currentX - previousX);
    }

    return currentX;
}
```

### Версия с вычислением градиента (Java)

Классический шаг: x_new = x — learningRate * gradient. Остановка по малости изменения x или по числу итераций.

```java
// x -= learningRate * derivative(x); остановка по precision или maxIterations
public static double gradientDescentWithDerivative(
    Function<Double, Double> f,
    Function<Double, Double> derivative,
    double initialX,
    double learningRate) {

    double precision = 0.000001;
    int maxIterations = 1000;
    double currentX = initialX;

    for (int i = 0; i < maxIterations; i++) {
        double gradient = derivative.apply(currentX);
        double newX = currentX - learningRate * gradient;

        if (Math.abs(newX - currentX) < precision) {
            break;
        }

        currentX = newX;
    }

    return currentX;
}
```

## Сложность

Время O(k), где k — число итераций (ограничено maxIterations). Память O(1).

## Особенности

Находит локальный минимум; сходимость зависит от функции и шага. Скорость сходимости зависит от learning rate и формы функции.

## Применение

Используется в машинном обучении (обучение моделей, минимизация потерь), нейронных сетях, регрессии и классификации.

## Варианты задачи

### Вариант 1: С адаптивным размером шага

Проверка соседей currentX ± stepSize; если ни один не лучше — уменьшаем шаг.

```java
public static double gradientDescentAdaptive(Function<Double, Double> f, double initialX) {
    double precision = 0.000001;
    double stepSize = 0.1;
    int maxIterations = 1000;
    double currentX = initialX;

    for (int i = 0; i < maxIterations; i++) {
        double currentY = f.apply(currentX);
        double nextX1 = currentX + stepSize;
        double nextX2 = currentX - stepSize;
        double y1 = f.apply(nextX1);
        double y2 = f.apply(nextX2);

        if (y1 < currentY) {
            currentX = nextX1;
        } else if (y2 < currentY) {
            currentX = nextX2;
        } else {
            stepSize /= 2;
        }

        if (stepSize < precision) {
            break;
        }
    }

    return currentX;
}
```

### Вариант 2: Стохастический градиентный спуск

Обновление по одному элементу данных (или по мини-батчу); перемешивание каждой эпохи.

```java
public static double stochasticGradientDescent(
    List<Double> dataPoints,
    Function<Double, Double> lossFunction,
    double initialX,
    double learningRate) {

    double currentX = initialX;
    Random random = new Random();

    for (int epoch = 0; epoch < 100; epoch++) {
        Collections.shuffle(dataPoints);
        for (Double point : dataPoints) {
            double gradient = lossFunction.apply(point - currentX);
            currentX -= learningRate * gradient;
        }
    }

    return currentX;
}
```

## Реализация на Kotlin

```kotlin
// С возвратом, с производной, адаптивный и стохастический варианты
fun gradientDescentK(f: (Double) -> Double, initialX: Double): Double {
    val precision = 0.000001
    var stepCoefficient = 0.1
    var iter = 100

    var previousX = initialX
    var previousY = f(previousX)
    var currentX = initialX + stepCoefficient * previousY
    var previousStep = 1.0

    while (previousStep > precision && iter > 0) {
        iter--
        val currentY = f(currentX)

        if (currentY > previousY) {
            stepCoefficient = -stepCoefficient / 2
        }

        previousX = currentX
        currentX += stepCoefficient * previousY
        previousY = currentY
        previousStep = Math.abs(currentX - previousX)
    }

    return currentX
}
```

### Версия с вычислением градиента (Kotlin)

```kotlin
fun gradientDescentWithDerivativeK(
    f: (Double) -> Double,
    derivative: (Double) -> Double,
    initialX: Double,
    learningRate: Double
): Double {
    val precision = 0.000001
    val maxIterations = 1000
    var currentX = initialX

    for (i in 0 until maxIterations) {
        val gradient = derivative(currentX)
        currentX -= learningRate * gradient

        if (Math.abs(gradient) < precision) {
            break
        }
    }

    return currentX
}
```

### Адаптивный градиентный спуск

```kotlin
fun gradientDescentAdaptiveK(f: (Double) -> Double, initialX: Double): Double {
    val precision = 0.000001
    var stepSize = 0.1
    val maxIterations = 1000
    var currentX = initialX

    for (i in 0 until maxIterations) {
        val currentY = f(currentX)
        val nextX1 = currentX + stepSize
        val nextX2 = currentX - stepSize
        val y1 = f(nextX1)
        val y2 = f(nextX2)

        currentX = when {
            y1 < currentY -> nextX1
            y2 < currentY -> nextX2
            else -> {
                stepSize /= 2
                currentX
            }
        }

        if (stepSize < precision) {
            break
        }
    }

    return currentX
}
```

### Стохастический градиентный спуск

```kotlin
fun stochasticGradientDescentK(
    dataPoints: List<Double>,
    lossFunction: (Double) -> Double,
    initialX: Double,
    learningRate: Double
): Double {
    var currentX = initialX

    for (epoch in 0 until 100) {
        val shuffled = dataPoints.shuffled()
        for (point in shuffled) {
            val gradient = lossFunction(point - currentX)
            currentX -= learningRate * gradient
        }
    }

    return currentX
}
```

### Пример использования

```kotlin
fun main() {
    // Минимизация функции f(x) = x^2
    val f: (Double) -> Double = { it * it }
    val derivative: (Double) -> Double = { 2 * it }

    val minimum1 = gradientDescentK(f, 5.0)
    println("Minimum: $minimum1") // ≈ 0.0

    val minimum2 = gradientDescentWithDerivativeK(f, derivative, 5.0, 0.1)
    println("Minimum with derivative: $minimum2") // ≈ 0.0
}
```

## Когда использовать

Градиентный спуск уместен, когда функцию можно дифференцировать (или аппроксимировать градиент) и нужна минимизация. Для быстрой сходимости при «хорошей» функции — метод Ньютона; для поиска глобального минимума — симуляция отжига или генетические алгоритмы.

## Лучшие практики

Начинайте с небольшого шага; используйте backtracking или убывающий schedule. Задайте критерий остановки (порог по изменению x или по норме градиента) и ограничение по числу итераций. Учитывайте, что результат — локальный минимум; при необходимости запускайте из нескольких начальных точек. В ML нормализуйте признаки. Фиксируйте seed и сохраняйте гиперпараметры для воспроизводимости.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Расходимость, NaN | Слишком большой learning rate | Уменьшить шаг, использовать backtracking или schedule |
| Застревание далеко от минимума | Локальный минимум или плоская зона | Другие начальные точки; проверить learning rate |
| Слишком много итераций | Малый шаг или высокая точность | Увеличить шаг в разумных пределах; ослабить precision |

## Частые вопросы

**Чем градиентный спуск с возвратом отличается от варианта с производной?** С возвратом направление и шаг подбираются по изменению значения функции (без явной производной); с производной шаг делается по градиенту. Вариант с производной обычно сходится быстрее при гладкой функции.

**Почему находит локальный, а не глобальный минимум?** Алгоритм движется в направлении убывания из текущей точки; он не «видит» всю функцию. Для глобального минимума нужны методы глобальной оптимизации или несколько запусков из разных точек.

**Когда использовать стохастический градиентный спуск?** Когда данных много и полный проход по выборке дорог; обновление по одному элементу или мини-батчу даёт быстрые итерации и часто достаточную сходимость.

## Заключение

В документе описаны градиентный спуск с возвратом и с явным градиентом в `Java` и Kotlin, а также адаптивный и стохастический варианты. Алгоритм итеративно приближается к локальному минимуму; выбор шага и критерия остановки влияет на сходимость.
