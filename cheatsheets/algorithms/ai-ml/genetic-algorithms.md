---
title: "Разработка генетического алгоритма"
description: "Руководство по разработке и реализации генетических алгоритмов в Java для решения задач оптимизации с использованием эволюционных вычислений."
tags:
  - algorithms
  - ai-ml
  - genetic-algorithms
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Разработка генетического алгоритма

Руководство по разработке и реализации генетических алгоритмов в **Java** для решения задач оптимизации с использованием эволюционных вычислений.

## Полезные ссылки

### Официальная документация
- [Genetic algorithm (Wikipedia)](https://en.wikipedia.org/wiki/Genetic_algorithm)
- [Jenetics (GitHub)](https://github.com/jenetics/jenetics) — генетические алгоритмы

### См. также
- [Multi-Swarm](../problems/multi-swarm.md)
- [Hill-Climbing](../problems/hill-climbing.md)
- [Jenetics](jenetics.md)

- [Логистическая регрессия](logistic-regression.md)
- [Реализация CNN с помощью Deeplearning4j](cnn-deeplearning4j.md)
## Содержание

- [Обзор](#обзор)
- [Что такое генетические алгоритмы?](#что-такое-генетические-алгоритмы)
- [Основные компоненты](#основные-компоненты)
  - [Индивидуум (Individual)](#индивидуум-individual)
  - [Популяция (Population)](#популяция-population)
  - [Фитнес-функция (Fitness Function)](#фитнес-функция-fitness-function)
- [Процесс генетического алгоритма](#процесс-генетического-алгоритма)
  - [1. Инициализация](#1-инициализация)
  - [2. Основной цикл](#2-основной-цикл)
- [Реализация на Java](#реализация-на-java)
  - [Класс Individual (Java)](#класс-individual-java)
  - [Фитнес-функция (Java)](#фитнес-функция-java)
  - [Класс Population (Java)](#класс-population-java)
  - [Эволюция популяции (Java)](#эволюция-популяции-java)
  - [Турнирный отбор](#турнирный-отбор)
  - [Кроссовер (Crossover)](#кроссовер-crossover)
  - [Мутация (Mutation)](#мутация-mutation)
- [Настройка параметров](#настройка-параметров)
- [Полный пример](#полный-пример)
- [Преимущества и недостатки](#преимущества-и-недостатки)
  - [Преимущества](#преимущества)
  - [Недостатки](#недостатки)
- [Применение](#применение)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Класс Individual (Kotlin)](#класс-individual-kotlin)
  - [Фитнес-функция (Kotlin)](#фитнес-функция-kotlin)
  - [Класс Population (Kotlin)](#класс-population-kotlin)
  - [Эволюция популяции (Kotlin)](#эволюция-популяции-kotlin)

## Обзор

Целью этой серии статей является объяснение идеи генетических алгоритмов.

Генетические алгоритмы предназначены для решения проблем с использованием тех же процессов, что и в природе — они используют комбинацию отбора, рекомбинации и мутации для развития решения проблемы.

Давайте начнем с объяснения концепции этих алгоритмов на примере простейшего бинарного генетического алгоритма.

Генетические алгоритмы являются частью эволюционных вычислений, которые представляют собой быстрорастущую область искусственного интеллекта.

## Что такое генетические алгоритмы?

Алгоритм начинается с набора решений (представленных индивидуумами), называемого населением. Берутся решения из одной популяции и используются для формирования новой популяции, так как есть шанс, что новая популяция будет лучше старой.

Особи, отобранные для образования новых растворов (потомков), отбираются в соответствии с их приспособленностью — чем они более приспособлены, тем больше у них шансов на размножение.

## Основные компоненты

### Индивидуум (Individual)

**Индивидуум представляет одно возможное решение задачи. Он содержит:**
- Гены (кодировку решения)
- Значение приспособленности (fitness)

### Популяция (Population)

Популяция — это набор индивидуумов, которые эволюционируют вместе.

### Фитнес-функция (Fitness Function)

Функция, которая оценивает качество решения, представленного индивидуумом.

## Процесс генетического алгоритма

Давайте взглянем на основной процесс простого генетического алгоритма.

### 1. Инициализация

**На этапе инициализации мы генерируем случайную популяцию, которая служит первым решением. Во-первых, нам нужно решить, насколько большим будет **Population** и какое окончательное решение мы ожидаем:**

```java
SimpleGeneticAlgorithm.runAlgorithm(50,
    "1011000100000100010000100000100111001000000100000100000000001111");
```

В приведенном выше примере размер населения равен 50, а правильное решение представлено двоичной битовой строкой, которую мы можем изменить в любое время.

**На следующем шаге мы сохраним желаемое решение и создадим случайную популяцию:**

```java
setSolution(solution);
Population myPop = new Population(populationSize, true);
```

### 2. Основной цикл

Теперь мы готовы запустить основной цикл программы.

**В основном цикле программы мы будем оценивать каждого Индивидуума по фитнес-функции (проще говоря, чем лучше Индивидуум, тем большее значение фитнес-функции он получает):**

```java
while (myPop.getFittest().getFitness() < getMaxFitness()) {
    System.out.println("Generation: " + generationCount
        + " Correct genes found: " + myPop.getFittest().getFitness());

    myPop = evolvePopulation(myPop);
    generationCount++;
}
```

## Реализация на Java

### Класс Individual (Java)

```java
// Индивидуум генетического алгоритма: массив генов (биты) и значение приспособленности (fitness).
public class Individual {
    private byte[] genes;
    private int fitness = 0;
    private static int defaultGeneLength = 64;

    public Individual() {
        genes = new byte[defaultGeneLength];
    }

    public void generateIndividual() {
        Random random = new Random();
        for (int i = 0; i < size(); i++) {
            byte gene = (byte) Math.round(Math.random());
            genes[i] = gene;
        }
    }

    public byte getSingleGene(int index) {
        return genes[index];
    }

    public void setSingleGene(int index, byte value) {
        genes[index] = value;
        fitness = 0;
    }

    public int size() {
        return genes.length;
    }

    public int getFitness() {
        if (fitness == 0) {
            fitness = FitnessCalc.getFitness(this);
        }
        return fitness;
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += getSingleGene(i);
        }
        return geneString;
    }
}
```

### Фитнес-функция (Java)

**Давайте начнем с объяснения того, как мы получаем наиболее приспособленного человека:**

```java
public class FitnessCalc {
    static byte[] solution = new byte[64];

    public static void setSolution(byte[] newSolution) {
        solution = newSolution;
    }

    public static void setSolution(String newSolution) {
        solution = new byte[newSolution.length()];
        for (int i = 0; i < newSolution.length(); i++) {
            String character = newSolution.substring(i, i + 1);
            if (character.contains("0") || character.contains("1")) {
                solution[i] = Byte.parseByte(character);
            } else {
                solution[i] = 0;
            }
        }
    }

    public static int getFitness(Individual individual) {
        int fitness = 0;
        for (int i = 0; i < individual.size() && i < solution.length; i++) {
            if (individual.getSingleGene(i) == solution[i]) {
                fitness++;
            }
        }
        return fitness;
    }

    public static int getMaxFitness() {
        int maxFitness = solution.length;
        return maxFitness;
    }
}
```

Как мы видим, мы сравниваем два отдельных объекта по крупицам. Если мы не можем найти идеальное решение, нам нужно перейти к следующему шагу, который представляет собой эволюцию населения.

### Класс Population (Java)

```java
public class Population {
    Individual[] individuals;

    public Population(int populationSize, boolean initialise) {
        individuals = new Individual[populationSize];
        if (initialise) {
            for (int i = 0; i < size(); i++) {
                Individual newIndividual = new Individual();
                newIndividual.generateIndividual();
                saveIndividual(i, newIndividual);
            }
        }
    }

    public Individual getIndividual(int index) {
        return individuals[index];
    }

    public Individual getFittest() {
        Individual fittest = individuals[0];
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness() <= getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    public int size() {
        return individuals.length;
    }

    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }
}
```

### Эволюция популяции (Java)

**На этом шаге нам нужно создать новый **Population**. Во-первых, нам нужно выбрать два родительских отдельных объекта из совокупности в соответствии с их пригодностью. Пожалуйста, обратите внимание, что лучше позволить лучшему Индивидууму из текущего поколения перейти в следующее поколение без изменений. Эта стратегия называется элитизмом:**

```java
public Population evolvePopulation(Population pop) {
    Population newPopulation = new Population(pop.size(), false);

    int elitismOffset = 0;
    if (elitism) {
        newPopulation.saveIndividual(0, pop.getFittest());
        elitismOffset = 1;
    }

    for (int i = elitismOffset; i < pop.size(); i++) {
        Individual indiv1 = tournamentSelection(pop);
        Individual indiv2 = tournamentSelection(pop);
        Individual newIndiv = crossover(indiv1, indiv2);
        newPopulation.saveIndividual(i, newIndiv);
    }

    for (int i = elitismOffset; i < newPopulation.size(); i++) {
        mutate(newPopulation.getIndividual(i));
    }

    return newPopulation;
}
```

### Турнирный отбор

**Чтобы выбрать два лучших индивидуальных объекта, мы собираемся применить турнирную стратегию отбора:**

```java
private Individual tournamentSelection(Population pop) {
    Population tournament = new Population(tournamentSize, false);
    for (int i = 0; i < tournamentSize; i++) {
        int randomId = (int) (Math.random() * pop.size());
        tournament.saveIndividual(i, pop.getIndividual(randomId));
    }
    Individual fittest = tournament.getFittest();
    return fittest;
}
```

Победитель каждого турнира (тот, у кого лучшая физическая форма) выбирается для следующего этапа, который называется Кроссовер.

### Кроссовер (Crossover)

**В кроссовере мы меняем местами биты от каждого выбранного индивидуума в случайно выбранном месте:**

```java
private Individual crossover(Individual indiv1, Individual indiv2) {
    Individual newSol = new Individual();
    for (int i = 0; i < newSol.size(); i++) {
        if (Math.random() <= uniformRate) {
            newSol.setSingleGene(i, indiv1.getSingleGene(i));
        } else {
            newSol.setSingleGene(i, indiv2.getSingleGene(i));
        }
    }
    return newSol;
}
```

### Мутация (Mutation)

**Наконец, мы можем выполнить мутацию. Мутация используется для поддержания генетического разнообразия от одного поколения популяции к другому. Мы использовали тип мутации битовой инверсии, где случайные биты просто инвертируются:**

```java
private void mutate(Individual indiv) {
    for (int i = 0; i < indiv.size(); i++) {
        if (Math.random() <= mutationRate) {
            byte gene = (byte) Math.round(Math.random());
            indiv.setSingleGene(i, gene);
        }
    }
}
```

Все типы Мутации и Кроссовера прекрасно описаны в этом уроке.

Затем мы повторяем шаги из подразделов `3.2` и `3.3`, пока не достигнем условия завершения, например, наилучшего решения.

## Настройка параметров

**Чтобы реализовать эффективный генетический алгоритм, нам нужно настроить набор параметров. Этот раздел должен дать вам несколько основных рекомендаций, как начать с наиболее импортируемых параметров:**

1. **Уровень кроссовера** — он должен быть высоким, около 80% - 95%
2. **Скорость мутации** — она должна быть очень низкой, около 0.5% - 1%
3. **Размер популяции** — хороший размер популяции составляет около 20 — 30, однако для некоторых задач лучше использовать размеры 50 - `100`
4. **Выбор** — базовый выбор колеса рулетки можно использовать с концепцией элитарности
5. **Кроссовер и тип мутации** — зависит от кодировки и проблемы

Обратите внимание, что рекомендации по настройке часто являются результатом эмпирических исследований генетических алгоритмов и могут варьироваться в зависимости от предлагаемых задач.

## Полный пример

```java
public class SimpleGeneticAlgorithm {
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;

    public static void runAlgorithm(int populationSize, String solution) {
        FitnessCalc.setSolution(solution);

        Population myPop = new Population(populationSize, true);
        int generationCount = 0;

        while (myPop.getFittest().getFitness() < FitnessCalc.getMaxFitness()) {
            generationCount++;
            System.out.println("Generation: " + generationCount
                + " Fittest: " + myPop.getFittest().getFitness());
            myPop = evolvePopulation(myPop);
        }

        System.out.println("Solution found!");
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        System.out.println(myPop.getFittest());
    }

    public static void main(String[] args) {
        runAlgorithm(50,
            "1011000100000100010000100000100111001000000100000100000000001111");
    }
}
```

## Преимущества и недостатки

### Преимущества

- Может найти хорошие решения для сложных задач
- Не требует градиента функции
- Работает с дискретными и непрерывными переменными
- Параллелизуем

### Недостатки

- Не гарантирует оптимальное решение
- Может быть медленным для больших популяций
- Требует настройки параметров
- Может застрять в локальных оптимумах

## Применение

**Генетические алгоритмы используются для:**
- Оптимизации функций
- Планирования и расписания
- Машинного обучения
- Игрового ИИ
- Робототехники
- Биоинформатики

## Лучшие практики

Подбирайте размер популяции (обычно 50–200) и вероятность мутации (0.01–0.1) под задачу; слишком малая мутация ведёт к застреванию в локальном оптимуме. Кодировка: бинарная для булевых/целочисленных решений, порядковая для комбинаторики (TSP), вещественная для непрерывной оптимизации. Ограничивайте критерий остановки числом поколений или порогом по фитнесу. Элитизм — сохранение лучших особей в следующее поколение — обеспечивает монотонное улучшение.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Популяция сходится к плохому решению | Преждевременная сходимость, малая мутация | Увеличить вероятность мутации; увеличить популяцию; проверить разнообразие (diversity) |
| Фитнес не улучшается | Неподходящая кодировка или фитнес-функция | Проверить кодировку и границы генов; убедиться, что фитнес корректно отражает цель |
| Слишком долгое выполнение | Большая популяция или тяжёлая фитнес-функция | Уменьшить размер популяции или поколений; кэшировать фитнес; распараллелить оценку |

## Частые вопросы

**Чем генетический алгоритм отличается от градиентной оптимизации?** ГА не требует градиентов и подходит для дискретных и негладких задач; выдает приближённое решение за счёт эволюции популяции. Градиентные методы эффективнее, когда целевая функция гладкая и дифференцируема.

**Когда использовать элитизм?** Почти всегда: перенос лучших особей в следующее поколение гарантирует, что найденный лучший результат не потеряется и часто ускоряет сходимость.

**Как выбрать тип кроссовера?** Одноточечный/двуточечный — для бинарных и порядковых хромосом; для вещественных — blend или арифметический кроссовер. Зависит от кодировки и задачи.

## Резюме

Этот учебник знакомит с основами генетических алгоритмов. Вы можете узнать о генетических алгоритмах без каких-либо предварительных знаний в этой области, имея только базовые навыки компьютерного программирования.

**Ключевые моменты:**
- Генетические алгоритмы имитируют процесс естественной эволюции
- Используют отбор, кроссовер и мутацию для улучшения решений
- Требуют настройки параметров для эффективной работы
- Подходят для задач оптимизации без градиента
- Могут найти хорошие, но не обязательно оптимальные решения

## Реализация на Kotlin

### Класс Individual (Kotlin)

```kotlin
class IndividualK(private val defaultGeneLength: Int = 64) {
    private val genes = ByteArray(defaultGeneLength)
    private var fitness: Int = 0

    fun generateIndividual() {
        for (i in genes.indices) {
            genes[i] = (Math.random().roundToInt()).toByte()
        }
    }

    fun getSingleGene(index: Int): Byte {
        return genes[index]
    }

    fun setSingleGene(index: Int, value: Byte) {
        genes[index] = value
        fitness = 0
    }

    fun size(): Int = genes.size

    fun getFitness(): Int {
        if (fitness == 0) {
            fitness = FitnessCalcK.getFitness(this)
        }
        return fitness
    }

    override fun toString(): String {
        return genes.joinToString("")
    }
}
```

### Фитнес-функция (Kotlin)

```kotlin
object FitnessCalcK {
    private var solution: ByteArray = ByteArray(64)

    fun setSolution(newSolution: ByteArray) {
        solution = newSolution
    }

    fun setSolution(newSolution: String) {
        solution = ByteArray(newSolution.length)
        for (i in newSolution.indices) {
            val character = newSolution.substring(i, i + 1)
            solution[i] = if (character == "0" || character == "1") {
                character.toByte()
            } else {
                0
            }
        }
    }

    fun getFitness(individual: IndividualK): Int {
        var fitness = 0
        for (i in 0 until minOf(individual.size(), solution.size)) {
            if (individual.getSingleGene(i) == solution[i]) {
                fitness++
            }
        }
        return fitness
    }

    fun getMaxFitness(): Int = solution.size
}
```

### Класс Population (Kotlin)

```kotlin
class PopulationK(private val populationSize: Int, initialise: Boolean) {
    val individuals: Array<IndividualK> = Array(populationSize) { IndividualK() }

    init {
        if (initialise) {
            for (i in individuals.indices) {
                val newIndividual = IndividualK()
                newIndividual.generateIndividual()
                individuals[i] = newIndividual
            }
        }
    }

    fun getFittest(): IndividualK {
        var fittest = individuals[0]
        for (i in 1 until individuals.size) {
            if (individuals[i].getFitness() > fittest.getFitness()) {
                fittest = individuals[i]
            }
        }
        return fittest
    }
}
```

### Эволюция популяции (Kotlin)

```kotlin
object SimpleGeneticAlgorithmK {
    private const val uniformRate = 0.5
    private const val mutationRate = 0.015
    private const val tournamentSize = 5
    private const val elitism = true

    fun evolvePopulation(pop: PopulationK): PopulationK {
        val newPopulation = PopulationK(pop.individuals.size, false)
        var offset = 0

        if (elitism) {
            newPopulation.individuals[0] = pop.getFittest()
            offset = 1
        }

        for (i in offset until newPopulation.individuals.size) {
            val indiv1 = tournamentSelection(pop)
            val indiv2 = tournamentSelection(pop)
            val newIndiv = crossover(indiv1, indiv2)
            newPopulation.individuals[i] = newIndiv
        }

        for (i in offset until newPopulation.individuals.size) {
            mutate(newPopulation.individuals[i])
        }

        return newPopulation
    }

    private fun crossover(indiv1: IndividualK, indiv2: IndividualK): IndividualK {
        val newSol = IndividualK()
        for (i in 0 until newSol.size()) {
            if (Math.random() <= uniformRate) {
                newSol.setSingleGene(i, indiv1.getSingleGene(i))
            } else {
                newSol.setSingleGene(i, indiv2.getSingleGene(i))
            }
        }
        return newSol
    }

    private fun mutate(indiv: IndividualK) {
        for (i in 0 until indiv.size()) {
            if (Math.random() <= mutationRate) {
                val gene = (Math.random().roundToInt()).toByte()
                indiv.setSingleGene(i, gene)
            }
        }
    }

    private fun tournamentSelection(pop: PopulationK): IndividualK {
        val tournament = PopulationK(tournamentSize, false)
        for (i in 0 until tournamentSize) {
            val randomId = (Math.random() * pop.individuals.size).toInt()
            tournament.individuals[i] = pop.individuals[randomId]
        }
        return tournament.getFittest()
    }
}
```
