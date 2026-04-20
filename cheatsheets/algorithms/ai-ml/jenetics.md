---
title: "Руководство по Jenetics"
description: "Руководство по использованию библиотеки Jenetics для решения задач оптимизации с помощью эволюционных алгоритмов в Java."
tags:
  - algorithms
  - ai-ml
  - jenetics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Руководство по Jenetics

Руководство по использованию библиотеки **Jenetics** для решения задач оптимизации с помощью эволюционных алгоритмов в **Java**.

## Полезные ссылки

### Официальная документация
- [Jenetics Documentation](https://jenetics.io/)
- [Jenetics GitHub](https://github.com/jenetics/jenetics)

### См. также
- [[genetic-algorithms|Генетические алгоритмы]] — генетические алгоритмы
- [[multi-swarm|Multi-Swarm]]
- [[hill-climbing|Hill-Climbing]]

## Содержание

- [Обзор](#обзор)
- [Основные особенности](#основные-особенности)
- [Настройка проекта](#настройка-проекта)
- [Реализация на Java](#реализация-на-java)
  - [Простая бинарная задача (Java)](#простая-бинарная-задача-java)
- [Задача суммы подмножеств](#задача-суммы-подмножеств)
- [Задача о рюкзаке](#задача-о-рюкзаке)
- [Статистика эволюции](#статистика-эволюции)
- [Рекомендации по использованию](#рекомендации-по-использованию)
  - [Настройка параметров](#настройка-параметров)
- [Применение](#применение)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)
  - [Простая бинарная задача (Kotlin)](#простая-бинарная-задача-kotlin)
  - [Задача суммы подмножеств](#задача-суммы-подмножеств-1)

## Обзор

Цель этой серии — объяснить идею генетических алгоритмов и показать наиболее известные реализации.

В этом уроке мы опишем очень мощную **Java**-библиотеку **Jenetics**, которую можно использовать для решения различных задач оптимизации.

Согласно официальным документам, **Jenetics** — это библиотека, основанная на эволюционном алгоритме, написанном на **Java**. Эволюционные алгоритмы уходят своими корнями в биологию, поскольку они используют механизмы, вдохновленные биологической эволюцией, такие как размножение, мутация, рекомбинация и отбор.

**Jenetics** реализован с использованием интерфейса **Java Stream**, поэтому он без проблем работает с остальной частью **API Java Stream**.

## Основные особенности

1. **Frictionless minimization** — нет необходимости изменять или настраивать фитнес-функцию; мы можем просто изменить конфигурацию класса **Engine**, и мы готовы запустить наше первое приложение
2. **Dependency-free** — для использования **Jenetics** не требуются сторонние библиотеки времени выполнения
3. **Java 8 ready** — полная поддержка **Stream** и лямбда-выражений
4. **Multithreaded** — эволюционные шаги могут выполняться параллельно

## Настройка проекта

Зависимость Maven:

```xml
<!-- Jenetics: эволюционные алгоритмы для оптимизации -->
<dependency>
    <groupId>io.jenetics</groupId>
    <artifactId>jenetics</artifactId>
    <version>3.7.0</version>
</dependency>
```

## Реализация на Java

### Простая бинарная задача (Java)

Чтобы протестировать все возможности **Jenetics**, мы попробуем решить различные известные задачи оптимизации, начиная от простого бинарного алгоритма и заканчивая задачей о рюкзаке.

**Предположим, что нам нужно решить простейшую бинарную задачу, где нам нужно оптимизировать позиции битов 1 в хромосоме, состоящей из 0 и 1. Во-первых, нам нужно определить фабрику, подходящую для задачи:**

```java
// Jenetics: эволюционный движок с BitChromosome, фабрика генотипа и фитнес-функция для максимизации числа единиц.
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.Genotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;

public class SimpleGeneticAlgorithm {

    private static Integer eval(Genotype<BitGene> gt) {
        return gt.getChromosome().as(BitChromosome.class).bitCount();
    }

    public static void main(String[] args) {
        Factory<Genotype<BitGene>> gtf = Genotype.of(BitChromosome.of(10, 0.5));

        Engine<BitGene, Integer> engine = Engine.builder(
            SimpleGeneticAlgorithm::eval, gtf
        ).build();

        Genotype<BitGene> result = engine.stream()
            .limit(500)
            .collect(EvolutionResult.toBestGenotype());

        System.out.println("Before the evolution:");
        System.out.println("[00000010|11111100]");
        System.out.println("After the evolution:");
        System.out.println(result);
    }
}
```

Мы создали битхромосому длиной 10 и вероятностью наличия единиц в хромосоме, равной `0.5`.

**Окончательный результат будет выглядеть примерно так:**

**Before the evolution:**
```text
[00000010|11111100]
```

**After the evolution:**
```text
[00000000|11111111]
```

Нам удалось оптимизировать положение единиц в гене.

## Задача суммы подмножеств

Еще один пример использования **Jenetics** — решение проблемы суммы подмножеств. Короче говоря, задача оптимизации заключается в том, что для заданного набора целых чисел нам нужно найти непустое подмножество, сумма которого равна нулю.

**В **Jenetics** есть предопределенные интерфейсы для решения таких задач:**

```java
import io.jenetics.EnumGene;
import io.jenetics.engine.Codec;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Problem;
import io.jenetics.util.ISeq;
import io.jenetics.util.codecs;

public class SubsetSum implements Problem<ISeq<Integer>, EnumGene<Integer>, Integer> {

    private final ISeq<Integer> basicSet;
    private final int size;

    public SubsetSum(ISeq<Integer> basicSet, int size) {
        this.basicSet = basicSet;
        this.size = size;
    }

    @Override
    public Function<ISeq<Integer>, Integer> fitness() {
        return subset -> Math.abs(subset.stream()
            .mapToInt(Integer::intValue)
            .sum());
    }

    @Override
    public Codec<ISeq<Integer>, EnumGene<Integer>> codec() {
        return codecs.ofSubSet(basicSet, size);
    }

    public static void main(String[] args) {
        SubsetSum problem = new SubsetSum(
            ISeq.of(85, -76, 178, -197, 91, -106, -70, -243, -41, -98, 94, -213, 139, 238, 219),
            15
        );

        Engine<EnumGene<Integer>, Integer> engine = Engine.builder(problem)
            .minimizing()
            .maximalPhenotypeAge(5)
            .alterers(
                new PartiallyMatchedCrossover<>(0.4),
                new Mutator<>(0.3)
            )
            .build();

        Phenotype<EnumGene<Integer>, Integer> result = engine.stream()
            .limit(limit.bySteadyFitness(55))
            .collect(EvolutionResult.toBestPhenotype());

        System.out.println(result);
    }
}
```

**Как мы видим, мы реализуем `Problem<T, G, C>` с тремя параметрами:**

- `<T>` - тип аргумента функции пригодности задачи, в нашем случае неизменяемая, упорядоченная целочисленная последовательность фиксированного размера `ISeq<Integer>`
- `<G>` - тип гена, с которым работает эволюционный движок, в данном случае счетные целочисленные гены `EnumGene<Integer>`
- `<C>` - тип результата фитнес-функции; вот это целое число

Мы пытаемся минимизировать результат (оптимально результат будет 0), задавая возраст фенотипа и альтереры, используемые для изменения потомства.

**Если нам повезет, и есть решение для случайно созданного набора, мы увидим что-то похожее на это:**

```text
[85| -76|178| -197|91| -106| -70| -243| -41| -98|94| -213|139|238|219] -> 0
```

В противном случае сумма подмножества будет отличаться от 0.

## Задача о рюкзаке

Библиотека **Jenetics** позволяет решать еще более сложные задачи, например, задачу о рюкзаке. Короче говоря, в этой задаче у нас ограниченное пространство в нашем рюкзаке, и нам нужно решить, какие предметы положить внутрь.

**Начнем с определения размера сумки и количества предметов:**

```java
import io.jenetics.BitChromosome;
import io.jenetics.BitGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.EvolutionStatistics;
import io.jenetics.util.Factory;

public class KnapsackProblem {

    public static void main(String[] args) {
        int nItems = 15;
        double ksSize = nItems * 100.0 / 3.0;

        KnapsackFF ff = new KnapsackFF(
            Stream.generate(KnapsackItem::random)
                .limit(nItems)
                .toArray(KnapsackItem[]::new),
            ksSize
        );

        Engine<BitGene, Double> engine = Engine.builder(ff, BitChromosome.of(nItems, 0.5))
            .populationSize(500)
            .survivorsSelector(new TournamentSelector<>(5))
            .offspringSelector(new RouletteWheelSelector<>())
            .alterers(
                new Mutator<>(0.115),
                new SinglePointCrossover<>(0.16)
            )
            .build();

        EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();

        Phenotype<BitGene, Double> best = engine.stream()
            .limit(limit.bySteadyFitness(7))
            .limit(100)
            .peek(statistics)
            .collect(EvolutionResult.toBestPhenotype());

        System.out.println(statistics);
    }
}
```

**Здесь следует отметить несколько моментов:**

- **Численность населения** - `500` человек
- **Потомство** будет выбрано с помощью турниров и выбора колеса рулетки
- Как и в предыдущем подразделе, нам также нужно определить альтереры для вновь созданного потомка

## Статистика эволюции

**Также есть одна очень важная особенность **Jenetics**. Мы можем легко собрать всю статистику и информацию за все время моделирования. Мы собираемся сделать это с помощью класса `EvolutionStatistics`:**

```java
EvolutionStatistics<Double, ?> statistics = EvolutionStatistics.ofNumber();
```

**Обратите внимание, что мы обновляем статистику оценки после каждого поколения, которое ограничено 7 постоянными поколениями и максимум `100` поколениями в целом. Более подробно возможны два сценария:**

1. Мы достигаем 7 устойчивых поколений, затем симуляция останавливается
2. Мы не можем получить 7 устойчивых поколений менее чем за `100` поколений, поэтому симуляция останавливается из-за второго предела

Важно иметь максимальное ограничение на количество поколений, иначе симуляции могут не остановиться за разумное время.

**Конечный результат содержит много информации:**

```text
+-------------------------------------------------------------+
| Time statistics                                             |
+-------------------------------------------------------------+
| Selection: sum=0.039207931000 s; mean=0.003267327583 s     |
| Altering: sum=0.065145069000 s; mean=0.005428755750 s      |
| Fitness calculation: sum=0.029678433000 s; mean=0.002473202750 s |
| Overall execution: sum=0.111383965000 s; mean=0.009281997083 s |
+-------------------------------------------------------------+
| Evolution statistics                                       |
+-------------------------------------------------------------+
| Generations: 12                                            |
| Altered: sum=7 664; mean=638.666666667                     |
| Killed: sum=0; mean=0.000000000                            |
| Invalids: sum=0; mean=0.000000000                          |
+-------------------------------------------------------------+
| Population statistics                                       |
+-------------------------------------------------------------+
| Age: max=10; mean=1.792167; var=4.657748                   |
| Fitness:                                                    |
|   min = 0.000000000000                                      |
|   max = 716.684883338605                                    |
|   mean = 587.012666759785                                   |
|   var = 17309.892287851708                                  |
|   std = 131.567063841418                                    |
+-------------------------------------------------------------+
```

На этот раз мы смогли разместить предметы общей стоимостью `716.68` в лучшем случае. Мы также можем увидеть подробную статистику эволюции и времени.

## Рекомендации по использованию

Это довольно простой процесс — просто откройте основной файл, связанный с проблемой, и сначала запустите алгоритм. Как только у нас появится общее представление, мы можем начать играть с параметрами.

### Настройка параметров

- **Размер популяции**: Обычно 50-500, в зависимости от сложности задачи
- **Селекторы**: **TournamentSelector** для выживших, **RouletteWheelSelector** для потомства
- **Альтереры**: **Mutator** и **Crossover** с различными вероятностями
- **Ограничения**: **bySteadyFitness**() и максимальное количество поколений

## Применение

**Jenetics** используется для:**
- Оптимизации функций
- Планирования и расписания
- Задачи коммивояжера
- Задачи о рюкзаке
- Генетического программирования
- Машинного обучения

## Лучшие практики

В Engine.builder задавайте фабрику генотипа (Genotype.of(...)) и фитнес-функцию; для минимизации используйте инвертированную оценку. Jenetics поддерживает многопоточную эволюцию — настройте populationSize и alterers под задачу. Используйте EvolutionResult для доступа к лучшему генотипу, счётчику поколений и статистике. Для задач подмножеств и рюкзака применяйте Problem и Codec; подбирайте тип хромосомы (Bit, Integer, Permutation) под представление решения.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Эволюция быстро сходится к плохому решению | Слишком малая популяция или сильная мутация | Увеличить populationSize; уменьшить вероятность мутации; проверить фитнес |
| Решение не улучшается со временем | Слабая селекция или неподходящий генотип | Усилить отбор; проверить кодировку (Codec) и границы генов |
| Долгое время работы | Большая популяция или тяжёлая фитнес-функция | Уменьшить популяцию или поколения; распараллелить (Jenetics поддерживает); упростить фитнес |

## Частые вопросы

**Чем Jenetics отличается от своих реализаций генетического алгоритма?** Готовый API (Engine, Genotype, Problem, Codec), интеграция со Stream, многопоточность, статистика и тестируемость; удобно для быстрого прототипирования и встраивания в Java-приложения.

**Когда использовать BitChromosome, IntegerChromosome, PermutationChromosome?** Bit — бинарные решения и подмножества; Integer — целочисленные параметры с диапазоном; Permutation — порядок (TSP, расписания).

**Как задать минимизацию вместо максимизации?** Передать в фитнес-функцию отрицательное значение или обёртку (например, минус целевая функция); Engine по умолчанию максимизирует фитнес.

## Резюме

В этой статье мы рассмотрели возможности библиотеки **Jenetics** на основе реальных задач оптимизации.

Код доступен в виде проекта **Maven** на **GitHub**. Обратите внимание, что мы предоставили примеры кода для других задач оптимизации, таких как запись Спрингстина (да, она существует!) и задачи коммивояжера.

**Ключевые моменты:**
- **Jenetics** предоставляет простой **API** для эволюционных алгоритмов
- Полная поддержка **Java Stream API**
- Многопоточность из коробки
- Богатая статистика эволюции
- Гибкая настройка параметров

**Jenetics** — это инструмент для решения задач оптимизации в **Java**, который позволяет быстро создавать и тестировать эволюционные алгоритмы.

## Реализация на Kotlin

### Простая бинарная задача (Kotlin)

```kotlin
import io.jenetics.BitChromosome
import io.jenetics.BitGene
import io.jenetics.Genotype
import io.jenetics.engine.Engine
import io.jenetics.engine.EvolutionResult

object SimpleGeneticAlgorithmK {
    private fun eval(gt: Genotype<BitGene>): Int {
        return gt.chromosome().`as`(BitChromosome::class.java).bitCount()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val gtf = Genotype.of(BitChromosome.of(10, 0.5))

        val engine = Engine.builder(
            ::eval, gtf
        ).build()

        val result = engine.stream()
            .limit(500)
            .collect(EvolutionResult.toBestGenotype())

        println("Before the evolution:")
        println("[00000010|11111100]")
        println("After the evolution:")
        println(result)
    }
}
```

### Задача суммы подмножеств

```kotlin
import io.jenetics.*
import io.jenetics.engine.*
import io.jenetics.util.ISeq

class SubsetSumK(
    private val basicSet: ISeq<Int>,
    private val size: Int
) : Problem<ISeq<Int>, EnumGene<Int>, Int> {

    override fun fitness(): Function<ISeq<Int>, Int> {
        return Function { subset ->
            Math.abs(subset.stream()
                .mapToInt { it }
                .sum())
        }
    }

    override fun codec(): Codec<ISeq<Int>, EnumGene<Int>> {
        return codecs.ofSubSet(basicSet, size)
    }
}

fun main() {
    val problem = SubsetSumK(
        ISeq.of(85, -76, 178, -197, 91, -106, -70, -243, -41, -98, 94, -213, 139, 238, 219),
        15
    )

    val engine = Engine.builder(problem)
        .minimizing()
        .maximalPhenotypeAge(5)
        .alterers(
            PartiallyMatchedCrossover<Int, Int>(0.4),
            Mutator<EnumGene<Int>, Int>(0.3)
        )
        .build()

    val result = engine.stream()
        .limit(Limit.bySteadyFitness(55))
        .collect(EvolutionResult.toBestPhenotype())

    println(result)
}
```
