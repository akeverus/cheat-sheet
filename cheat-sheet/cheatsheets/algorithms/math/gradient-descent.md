# Gradient Descent

Кратко: градиентный спуск - это алгоритм оптимизации, используемый для поиска локального минимума заданной функции. Рассматривается реализация с возвратом (backtracking) для минимизации функций потерь.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [GeeksforGeeks: Gradient Descent Algorithm](https://www.geeksforgeeks.org/gradient-descent-algorithm-and-its-variants/)

### См. также
- `./k-means-clustering-java.md` - кластеризация K-средних
- `./fibonacci-sequence.md` - ряд Фибоначчи

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Алгоритм с возвратом](#алгоритм-с-возвратом)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

В этом уроке мы узнаем об алгоритме градиентного спуска. Мы реализуем алгоритм на Java и проиллюстрируем его шаг за шагом.

Градиентный спуск - это алгоритм оптимизации, используемый для поиска локального минимума заданной функции. Он широко используется в высокоуровневых алгоритмах машинного обучения для минимизации функций потерь.

Градиент - это еще одно слово для обозначения уклона, а спуск означает спуск. Как следует из названия, градиентный спуск идет вниз по склону функции, пока не достигнет конца.

### Основные понятия

Градиентный спуск находит локальный минимум, который может отличаться от глобального минимума. Начальная локальная точка задается в качестве параметра алгоритма.

Это итеративный алгоритм, и на каждом этапе он пытается двигаться вниз по склону и приближаться к локальному минимуму.

На практике алгоритм работает с возвратом. В этом уроке мы проиллюстрируем и реализуем градиентный спуск с возвратом.

### Процесс работы

Градиентному спуску нужна функция и отправная точка в качестве входных данных.

1. На первом этапе Gradient Descent спускается по склону с заранее заданным размером шага
2. Далее он идет дальше с тем же размером шага. Однако на этот раз он заканчивается на большем y, чем на последнем шаге
3. Это указывает на то, что алгоритм прошел локальный минимум, поэтому он возвращается с уменьшенным размером шага
4. Впоследствии, всякий раз, когда текущий y больше, чем предыдущий y, размер шага уменьшается и инвертируется. Итерация продолжается до тех пор, пока не будет достигнута желаемая точность

### Важные замечания

Как мы видим, Gradient Descent нашел здесь локальный минимум, но это не глобальный минимум. Если мы начнем с x = -1 вместо x = 1, будет найден глобальный минимум.

Существует несколько способов реализации градиентного спуска. Здесь мы не вычисляем производную функции, чтобы найти направление наклона, поэтому наша реализация работает и для недифференцируемых функций.

## Алгоритм с возвратом

Давайте определим точность и stepCoefficient и присвоим им начальные значения:

```java
double precision = 0.000001;
double stepCoefficient = 0.1;
```

На первом этапе у нас нет предыдущего y для сравнения. Мы можем либо увеличить, либо уменьшить значение x, чтобы увидеть, снижается или повышается y. Положительный stepCoefficient означает, что мы увеличиваем значение x.

## Java Implementation

### Реализация

Теперь давайте выполним первый шаг:

```java
double previousX = initialX;
double previousY = f.apply(previousX);
double currentX = initialX + stepCoefficient * previousY;
double previousStep = 1.0;
```

В приведенном выше коде f - это `Function<Double, Double>`, а initialX - это double, оба значения предоставляются в качестве входных данных.

Еще один ключевой момент, который следует учитывать, заключается в том, что сходимость градиентного спуска не гарантируется. Чтобы не застрять в цикле, давайте ограничим количество итераций:

```java
int iter = 100;
```

Позже мы будем уменьшать iter на единицу на каждой итерации. Следовательно, мы выйдем из цикла максимум через 100 итераций.

Теперь, когда у нас есть предыдущий X, мы можем настроить наш цикл:

```java
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
```

На каждой итерации мы вычисляем новый y и сравниваем его с предыдущим y. Если currentY больше, чем previousY, мы меняем наше направление и уменьшаем размер шага.

Цикл продолжается до тех пор, пока размер нашего шага не станет меньше желаемой точности. Наконец, мы можем вернуть currentX как локальный минимум.

### Полная реализация

```java
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

### Версия с вычислением градиента

```java
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

### Временная сложность

- **С возвратом:** O(k), где k - количество итераций (до maxIterations)
- **С градиентом:** O(k), где k - количество итераций

### Пространственная сложность

- **Все версии:** O(1) - только константная память

## Особенности

- **Локальный минимум:** Находит локальный, а не глобальный минимум
- **Сходимость:** Не гарантируется для всех функций
- **Скорость:** Зависит от размера шага и функции

## Применение

Градиентный спуск используется в:

- Машинном обучении
- Нейронных сетях
- Оптимизации
- Регрессии
- Классификации

## Варианты задачи

### Вариант 1: С адаптивным размером шага

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

## Kotlin Implementation

### Основная реализация с возвратом

```kotlin
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

### Версия с вычислением градиента

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

### Используйте градиентный спуск, когда:

- Функция дифференцируема (или можно аппроксимировать)
- Нужна оптимизация
- Работаете с большими данными

### Альтернативы:

- **Ньютон-Рафсон:** Для быстрой сходимости
- **Симуляция отжига:** Для глобального минимума
- **Генетические алгоритмы:** Для сложных функций

## Заключение

В этом уроке мы рассмотрели алгоритм градиентного спуска с возвратом. Алгоритм итеративно движется к локальному минимуму функции, корректируя размер шага при необходимости.
