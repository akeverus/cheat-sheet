---
title: "Оптимизация муравьиной колонии"
description: "Алгоритм оптимизации муравьиной колонии (ACO) — это метаэвристический алгоритм, вдохновленный поведением муравьев в природе. В этом руководстве мы опишем концепцию ACO и приведем пример реализации для задачи коммивояжера."
tags:
  - algorithms
  - ai-ml
  - ant-colony-optimization
type: "reference"
difficulty: "intermediate"
aliases:
  - "Оптимизация муравьиной колонии"
  - "ant colony optimization"
  - "ACO"
prerequisites: []
next:
  - "[[genetic-algorithms]]"
updated: "2026-04-20"
---
# Оптимизация муравьиной колонии

Алгоритм оптимизации муравьиной колонии (ACO) — это метаэвристический алгоритм, вдохновленный поведением муравьев в природе. В этом руководстве мы опишем концепцию ACO и приведем пример реализации для задачи коммивояжера.

## Полезные ссылки

### Официальная документация
- [Ant Colony Optimization — Wikipedia](https://en.wikipedia.org/wiki/Ant_colony_optimization_algorithms)

### Обучающие материалы
- [Introduction to Ant Colony Optimization](https://www.baeldung.com/java-ant-colony-optimization)

### См. также
- [Генетические алгоритмы](genetic-algorithms.md)
- [Hill Climbing](../problems/hill-climbing.md)
- [Multi-Swarm Optimization](../problems/multi-swarm.md)

- [Логистическая регрессия](logistic-regression.md)
- [Реализация CNN с помощью Deeplearning4j](cnn-deeplearning4j.md)
## Содержание

- [Введение](#введение)
- [Основные понятия](#основные-понятия)
- [Параметры алгоритма](#параметры-алгоритма)
- [Реализация на Java](#реализация-на-java)
  - [Класс Ant (Java)](#класс-ant-java)
- [Инициализация](#инициализация)
- [Основной цикл алгоритма](#основной-цикл-алгоритма)
- [Движение муравьев](#движение-муравьев)
- [Выбор следующего города](#выбор-следующего-города)
- [Расчет вероятностей](#расчет-вероятностей)
- [Обновление следов](#обновление-следов)
- [Обновление лучшего решения](#обновление-лучшего-решения)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Класс Ant (Kotlin)](#класс-ant-kotlin)
  - [Ant Colony Optimization](#ant-colony-optimization)

## Введение

Цель этой серии — объяснить идею генетических алгоритмов и показать наиболее известные реализации.

В этом руководстве мы опишем концепцию оптимизации колонии муравьев (ACO), а затем приведем пример кода.

## Основные понятия

ACO — это генетический алгоритм, вдохновленный естественным поведением муравья. Чтобы полностью понять алгоритм ACO, нам необходимо ознакомиться с его основными понятиями:

1. Муравьи используют феромоны, чтобы найти кратчайший путь между домом и источником пищи
2. Феромоны быстро испаряются
3. Муравьи предпочитают использовать более короткие пути с более плотным феромоном

Давайте покажем простой пример ACO, используемый в задаче о коммивояжере. В следующем случае нам нужно найти кратчайший путь между всеми узлами в графе.

Следуя естественному поведению, муравьи начнут исследовать новые пути во время исследования. Более сильный синий цвет указывает на пути, которые используются чаще, чем другие, тогда как зеленый цвет указывает на текущий найденный кратчайший путь.

В результате мы получим кратчайший путь между всеми узлами.

## Параметры алгоритма

Обсудим основные параметры алгоритма ACO, объявленные в классе AntColonyOptimization:

```java
private double c = 1.0;
private double alpha = 1;
private double beta = 5;
private double evaporation = 0.5;
private double Q = 500;
private double antFactor = 0.8;
private double randomFactor = 0.01;
```

Параметр `c` указывает исходное количество следов в начале моделирования. Кроме того, `alpha` управляет важностью феромона, а `beta` управляет приоритетом расстояния. Как правило, для достижения наилучших результатов параметр `beta` должен быть больше, чем `alpha`.

Затем переменная `evaporation` показывает, сколько феромона испаряется в процентах за каждую итерацию, тогда как `Q` предоставляет информацию об общем количестве феромона, оставленного на следе каждым муравьем, а `antFactor` сообщает нам, сколько муравьев мы будем использовать в каждом городе.

Наконец, нам нужно немного случайности в наших симуляциях, и это покрывается `randomFactor`.

## Реализация на Java

### Класс Ant (Java)

Каждый Муравей сможет посетить конкретный город, запомнить все посещенные города и отслеживать длину маршрута:

```java
public void visitCity(int currentIndex, int city) {
    trail[currentIndex + 1] = city;
    visited[city] = true;
}

public boolean visited(int i) {
    return visited[i];
}

public double trailLength(double graph[][]) {
    double length = graph[trail[trailSize - 1]][trail[0]];
    for (int i = 0; i < trailSize - 1; i++) {
        length += graph[trail[i]][trail[i + 1]];
    }
    return length;
}
```

## Инициализация

В самом начале нам нужно инициализировать нашу реализацию кода ACO, предоставив матрицы следов и муравьев:

```java
graph = generateRandomMatrix(noOfCities);
numberOfCities = graph.length;
numberOfAnts = (int) (numberOfCities * antFactor);
trails = new double[numberOfCities][numberOfCities];
probabilities = new double[numberOfCities];
ants = new Ant[numberOfAnts];
IntStream.range(0, numberOfAnts).forEach(i -> ants.add(new Ant(numberOfCities)));
```

Затем нам нужно настроить матрицу муравьев, чтобы начать со случайного города:

```java
public void setupAnts() {
    IntStream.range(0, numberOfAnts)
        .forEach(i -> {
            ants.forEach(ant -> {
                ant.clear();
                ant.visitCity(-1, random.nextInt(numberOfCities));
            });
        });
    currentIndex = 0;
}
```

## Основной цикл алгоритма

Для каждой итерации цикла мы будем выполнять следующие операции:

```java
IntStream.range(0, maxIterations).forEach(i -> {
    moveAnts();
    updateTrails();
    updateBest();
});
```

## Движение муравьев

Начнем с метода `moveAnts()`. Нам нужно выбрать следующий город для всех муравьев, помня, что каждый муравей старается идти по следам других муравьев:

```java
public void moveAnts() {
    IntStream.range(currentIndex, numberOfCities - 1).forEach(i -> {
        ants.forEach(ant -> {
            ant.visitCity(currentIndex, selectNextCity(ant));
        });
        currentIndex++;
    });
}
```

## Выбор следующего города

Самая важная часть — правильно выбрать следующий город для посещения. Мы должны выбрать следующий город на основе вероятностной логики. Во-первых, мы можем проверить, должен ли Муравей посетить случайный город:

```java
int t = random.nextInt(numberOfCities - currentIndex);
if (random.nextDouble() < randomFactor) {
    OptionalInt cityIndex = IntStream.range(0, numberOfCities)
        .filter(i -> i == t && !ant.visited(i))
        .findFirst();
    if (cityIndex.isPresent()) {
        return cityIndex.getAsInt();
    }
}
```

## Расчет вероятностей

Если бы мы не выбрали ни одного случайного города, нам нужно рассчитать вероятности выбора следующего города, помня, что муравьи предпочитают следовать по более сильным и коротким тропам. Мы можем сделать это, сохранив вероятность переезда в каждый город в массиве:

```java
public void calculateProbabilities(Ant ant) {
    int i = ant.trail[currentIndex];
    double pheromone = 0.0;

    for (int l = 0; l < numberOfCities; l++) {
        if (!ant.visited(l)) {
            pheromone += Math.pow(trails[i][l], alpha) * Math.pow(1.0 / graph[i][l], beta);
        }
    }

    for (int j = 0; j < numberOfCities; j++) {
        if (ant.visited(j)) {
            probabilities[j] = 0.0;
        } else {
            double numerator = Math.pow(trails[i][j], alpha) * Math.pow(1.0 / graph[i][j], beta);
            probabilities[j] = numerator / pheromone;
        }
    }
}
```

После того, как мы вычислим вероятности, мы можем решить, в какой город идти, используя:

```java
double r = random.nextDouble();
double total = 0;
for (int i = 0; i < numberOfCities; i++) {
    total += probabilities[i];
    if (total >= r) {
        return i;
    }
}
```

## Обновление следов

На этом этапе мы должны обновить следы и левый феромон:

```java
public void updateTrails() {
    for (int i = 0; i < numberOfCities; i++) {
        for (int j = 0; j < numberOfCities; j++) {
            trails[i][j] *= evaporation;
        }
    }

    for (Ant a : ants) {
        double contribution = Q / a.trailLength(graph);
        for (int i = 0; i < numberOfCities - 1; i++) {
            trails[a.trail[i]][a.trail[i + 1]] += contribution;
        }
        trails[a.trail[numberOfCities - 1]][a.trail[0]] += contribution;
    }
}
```

## Обновление лучшего решения

Это последний шаг каждой итерации. Нам нужно обновить лучшее решение, чтобы сохранить ссылку на него:

```java
private void updateBest() {
    if (bestTourOrder == null) {
        bestTourOrder = ants[0].trail;
        bestTourLength = ants[0].trailLength(graph);
    }

    for (Ant a : ants) {
        if (a.trailLength(graph) < bestTourLength) {
            bestTourLength = a.trailLength(graph);
            bestTourOrder = a.trail.clone();
        }
    }
}
```

## Лучшие практики

Подбирайте параметры: alpha (важность феромона) и beta (важность расстояния) — обычно beta > alpha; evaporation (испарение) и Q (количество феромона на муравья) влияют на сходимость. Число муравьев (antFactor) и итераций задавайте с учётом размера задачи; для TSP с большим числом городов увеличивайте итерации. Добавляйте небольшой randomFactor для исследования и избежания застревания в локальном оптимуме.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Решение не улучшается / застревание | Слишком сильное испарение или малый randomFactor | Уменьшить evaporation; увеличить randomFactor; проверить alpha/beta |
| Слишком долгое выполнение | Много городов или итераций | Уменьшить число муравьев или итераций; упростить расчёт вероятностей |
| Нестабильные результаты | Случайность выбора пути | Запускать несколько раз и брать лучший результат; увеличить число итераций |

## Частые вопросы

**Чем ACO отличается от генетического алгоритма?** ACO использует феромоны и коллективное поведение муравьёв; решение строится пошагово с учётом следов. ГА оперирует популяцией особей, кроссовером и мутацией. ACO часто хорошо подходит для задач на графах (TSP, маршрутизация).

**Когда ACO предпочтительнее точных методов?** Для больших экземпляров TSP и подобных задач точные методы не масштабируются; ACO даёт приближённое решение за разумное время. Для маленьких графов можно использовать полный перебор или branch and bound.

**Как выбрать число муравьев?** Обычно порядка 0.5–1.0 от числа городов (antFactor); слишком много муравьев увеличивает время, слишком мало — ухудшает исследование пространства решений.

## Заключение

После всех итераций окончательный результат укажет лучший путь, найденный ACO. Обратите внимание, что при увеличении количества городов уменьшается вероятность нахождения кратчайшего пути.

В этом руководстве представлен алгоритм оптимизации муравьиной колонии. Вы можете узнать о генетических алгоритмах без каких-либо предварительных знаний в этой области, имея только базовые навыки компьютерного программирования.

## Реализация на Kotlin

### Класс Ant (Kotlin)

```kotlin
class AntK(private val trailSize: Int) {
    val trail = IntArray(trailSize)
    private val visited = BooleanArray(trailSize)

    fun visitCity(currentIndex: Int, city: Int) {
        trail[currentIndex + 1] = city
        visited[city] = true
    }

    fun visited(i: Int): Boolean = visited[i]

    fun trailLength(graph: Array<DoubleArray>): Double {
        var length = graph[trail[trailSize - 1]][trail[0]]
        for (i in 0 until trailSize - 1) {
            length += graph[trail[i]][trail[i + 1]]
        }
        return length
    }

    fun clear() {
        visited.fill(false)
    }
}
```

### Ant Colony Optimization

```kotlin
import kotlin.random.Random

class AntColonyOptimizationK(
    private val noOfCities: Int,
    private val maxIterations: Int
) {
    private var c = 1.0
    private var alpha = 1.0
    private var beta = 5.0
    private var evaporation = 0.5
    private var Q = 500.0
    private var antFactor = 0.8
    private var randomFactor = 0.01

    private lateinit var graph: Array<DoubleArray>
    private var numberOfAnts: Int = 0
    private lateinit var trails: Array<DoubleArray>
    private lateinit var probabilities: DoubleArray
    private lateinit var ants: Array<AntK>
    private var currentIndex: Int = 0

    private var bestTourOrder: IntArray? = null
    private var bestTourLength: Double = Double.MAX_VALUE

    fun solve(): IntArray? {
        graph = generateRandomMatrix(noOfCities)
        numberOfAnts = (noOfCities * antFactor).toInt()
        trails = Array(noOfCities) { DoubleArray(noOfCities) { c } }
        probabilities = DoubleArray(noOfCities)
        ants = Array(numberOfAnts) { AntK(noOfCities) }

        repeat(maxIterations) {
            setupAnts()
            moveAnts()
            updateTrails()
            updateBest()
        }

        return bestTourOrder
    }

    private fun setupAnts() {
        ants.forEach { ant ->
            ant.clear()
            ant.visitCity(-1, Random.nextInt(noOfCities))
        }
        currentIndex = 0
    }

    private fun moveAnts() {
        while (currentIndex < noOfCities - 1) {
            ants.forEach { ant ->
                ant.visitCity(currentIndex, selectNextCity(ant))
            }
            currentIndex++
        }
    }

    private fun selectNextCity(ant: AntK): Int {
        val t = Random.nextInt(noOfCities - currentIndex)
        if (Random.nextDouble() < randomFactor) {
            val cityIndex = (0 until noOfCities)
                .filter { !ant.visited(it) }
                .getOrNull(t)
            if (cityIndex != null) {
                return cityIndex
            }
        }

        calculateProbabilities(ant)
        val r = Random.nextDouble()
        var total = 0.0

        for (i in 0 until noOfCities) {
            total += probabilities[i]
            if (total >= r) {
                return i
            }
        }

        return 0
    }

    private fun calculateProbabilities(ant: AntK) {
        val i = ant.trail[currentIndex]
        var pheromone = 0.0

        for (l in 0 until noOfCities) {
            if (!ant.visited(l)) {
                pheromone += Math.pow(trails[i][l], alpha) *
                    Math.pow(1.0 / graph[i][l], beta)
            }
        }

        for (j in 0 until noOfCities) {
            if (ant.visited(j)) {
                probabilities[j] = 0.0
            } else {
                val numerator = Math.pow(trails[i][j], alpha) *
                    Math.pow(1.0 / graph[i][j], beta)
                probabilities[j] = numerator / pheromone
            }
        }
    }

    private fun updateTrails() {
        for (i in 0 until noOfCities) {
            for (j in 0 until noOfCities) {
                trails[i][j] *= evaporation
            }
        }

        ants.forEach { ant ->
            val contribution = Q / ant.trailLength(graph)
            for (i in 0 until noOfCities - 1) {
                trails[ant.trail[i]][ant.trail[i + 1]] += contribution
            }
            trails[ant.trail[noOfCities - 1]][ant.trail[0]] += contribution
        }
    }

    private fun updateBest() {
        ants.forEach { ant ->
            val trailLength = ant.trailLength(graph)
            if (trailLength < bestTourLength) {
                bestTourLength = trailLength
                bestTourOrder = ant.trail.clone()
            }
        }
    }

    private fun generateRandomMatrix(n: Int): Array<DoubleArray> {
        val matrix = Array(n) { DoubleArray(n) }
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (i == j) {
                    matrix[i][j] = 0.0
                } else {
                    matrix[i][j] = Random.nextDouble(1.0, 100.0)
                }
            }
        }
        return matrix
    }
}
```
