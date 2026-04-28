---
title: "Мульти-рой (Multi-Swarm)"
description: "Метаэвристика оптимизации: несколько роёв частиц, каждая частица — позиция (решение) и скорость; обновление скорости по инерции, личному лучшему, лучшему роя и глобальному лучшему. Пример: оптимизация эффективного здоровья в League of Legends при ограничении по золоту. Java и Kot"
tags:
  - algorithms
  - problems
  - multi-swarm
type: "reference"
difficulty: "intermediate"
aliases:
  - "Мульти-рой"
  - "Multi-Swarm"
  - "Мульти-рой (Multi-Swarm)"
  - "multi swarm"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Мульти-рой (Multi-Swarm)

Метаэвристика оптимизации: несколько роёв частиц, каждая частица — позиция (решение) и скорость; обновление скорости по инерции, личному лучшему, лучшему роя и глобальному лучшему. Пример: оптимизация эффективного здоровья в League of Legends при ограничении по золоту. Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Particle Swarm Optimization (Wikipedia)](https://en.wikipedia.org/wiki/Particle_swarm_optimization)
- [Swarm Intelligence (Wikipedia)](https://en.wikipedia.org/wiki/Swarm_intelligence)

### См. также
- [Hill-Climbing](hill-climbing.md)
- [Генетические алгоритмы](../ai-ml/genetic-algorithms.md) — генетические алгоритмы
- [Алгоритмы оптимизации](../algorithmic-paradigms/) — парадигмы

- [OptaPlanner](optaplanner.md)
- [Задача о рюкзаке (Knapsack Problem)](knapsack-problem.md)
- [Валидация банковских карт (Credit Card Validation)](credit-card-validation.md)
## Содержание

- [Обзор](#обзор)
- [Компоненты алгоритма](#компоненты-алгоритма)
- [Пример задачи](#пример-задачи)
- [Реализация на Java](#реализация-на-java)
- [Обновление скорости частиц](#обновление-скорости-частиц)
- [Полный пример](#полный-пример)
- [Преимущества и недостатки](#преимущества-и-недостатки)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Резюме](#резюме)

## Обзор

Multi-Swarm — вариант алгоритма роя частиц (PSO): несколько роёв вместо одного, цель — максимизация или минимизация фитнес-функции. Частицы движутся по пространству решений; скорость обновляется с учётом личного лучшего, лучшего роя и глобального лучшего, что даёт баланс исследования и сходимости. Результат — локальный оптимум; глобальный не гарантирован (метаэвристика).

## Компоненты алгоритма

Частица хранит текущую позицию (решение), скорость, лучшую найденную позицию и её фитнес. Рой — набор частиц с общим лучшим решением роя. Multi-Swarm — несколько роёв; глобальный лучший берётся по всем роям. Позиция обновляется как position += speed; скорость — взвешенная сумма инерции, когнитивной компоненты (к личному лучшему), социальной (к лучшему роя) и глобальной (к лучшему всех роёв), с рандомизацией.

## Пример задачи

League of Legends: эффективное здоровье E = H(100+A)/100 (H — здоровье, A — броня). Стоимость: 2.5 за единицу здоровья, 18 за единицу брони; бюджет 3600. Найти целочисленные H, A в рамках бюджета, максимизирующие E.

## Реализация на Java

Класс Particle: позиция и скорость (long[]), фитнес, bestPosition, bestFitness. Swarm: массив частиц, лучшая позиция роя; инициализация случайными позициями и скоростями в границах. Multiswarm: массив роёв, глобальный лучший, FitnessFunction. Фитнес для LoL: проверка неотрицательности и бюджета, иначе штраф; иначе E = health*(100+armor)/100.

```java
// Частица роя: позиция, скорость, лучшее найденное положение и значение фитнес-функции.
public class Particle {
    private long[] position;
    private long[] speed;
    private double fitness;
    private long[] bestPosition;
    private double bestFitness = Double.NEGATIVE_INFINITY;

    public Particle(long[] position, long[] speed) {
        this.position = position.clone();
        this.speed = speed.clone();
        this.bestPosition = position.clone();
    }

    // Геттеры и сеттеры
    public long[] getPosition() {
        return position.clone();
    }

    public void setPosition(long[] position) {
        this.position = position.clone();
    }

    public long[] getSpeed() {
        return speed.clone();
    }

    public void setSpeed(long[] speed) {
        this.speed = speed.clone();
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public long[] getBestPosition() {
        return bestPosition.clone();
    }

    public void setBestPosition(long[] bestPosition) {
        this.bestPosition = bestPosition.clone();
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }
}
```


```java
import java.util.Random;

public class Swarm {
    private Particle[] particles;
    private long[] bestPosition;
    private double bestFitness = Double.NEGATIVE_INFINITY;
    private static final Random random = new Random();
    private static final int PARTICLE_UPPER_BOUND = 2000;

    public Swarm(int numParticles) {
        particles = new Particle[numParticles];

        for (int i = 0; i < numParticles; i++) {
            long[] initialParticlePosition = {
                random.nextInt(PARTICLE_UPPER_BOUND),
                random.nextInt(PARTICLE_UPPER_BOUND)
            };

            long[] initialParticleSpeed = {
                random.nextInt(PARTICLE_UPPER_BOUND),
                random.nextInt(PARTICLE_UPPER_BOUND)
            };

            particles[i] = new Particle(initialParticlePosition, initialParticleSpeed);
        }
    }

    public Particle[] getParticles() {
        return particles;
    }

    public long[] getBestPosition() {
        return bestPosition != null ? bestPosition.clone() : null;
    }

    public void setBestPosition(long[] bestPosition) {
        this.bestPosition = bestPosition.clone();
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }
}
```


```java
public class Multiswarm {
    private Swarm[] swarms;
    private long[] bestPosition;
    private double bestFitness = Double.NEGATIVE_INFINITY;
    private FitnessFunction fitnessFunction;

    public Multiswarm(int numSwarms, int particlesPerSwarm,
                     FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
        this.swarms = new Swarm[numSwarms];

        for (int i = 0; i < numSwarms; i++) {
            swarms[i] = new Swarm(particlesPerSwarm);
        }
    }

    public Swarm[] getSwarms() {
        return swarms;
    }

    public long[] getBestPosition() {
        return bestPosition != null ? bestPosition.clone() : null;
    }

    public double getBestFitness() {
        return bestFitness;
    }
}
```


```java
public interface FitnessFunction {
    double getFitness(long[] particlePosition);
}
```

Реализация для LoL: проверка неотрицательности и стоимости ≤ 3600; при нарушении — штраф (отрицательный фитнес), иначе E = health*(100+armor)/100.

```java
public class LolFitnessFunction implements FitnessFunction {
    private static final double HEALTH_COST = 2.5;
    private static final double ARMOR_COST = 18.0;
    private static final double TOTAL_GOLD = 3600.0;

    @Override
    public double getFitness(long[] particlePosition) {
        long health = particlePosition[0];
        long armor = particlePosition[1];

        // Проверка на отрицательные значения
        if (health < 0 && armor < 0) {
            return -(health * armor);
        } else if (health < 0) {
            return health;
        } else if (armor < 0) {
            return armor;
        }

        // Проверка стоимости
        double cost = (health * HEALTH_COST) + (armor * ARMOR_COST);

        if (cost > TOTAL_GOLD) {
            return TOTAL_GOLD - cost; // Штраф за превышение бюджета
        } else {
            // Эффективное здоровье
            long fitness = (health * (100 + armor)) / 100;
            return fitness;
        }
    }
}
```

## Обновление скорости частиц

Основной цикл: для каждой частицы вычислить фитнес; при улучшении обновить best частицы, роя и глобальный лучший; обновить позицию (position += speed); обновить скорость по формуле: инерция × текущая_скорость + случайность × (когнитивный × (bestPosition − position) + социальный × (swarmBest − position) + глобальный × (globalBest − position)). Типичные веса: инерция 0.729, когнитивный и социальный 1.49445, глобальный 0.3645.

```java
public void mainLoop() {
    for (Swarm swarm : swarms) {
        for (Particle particle : swarm.getParticles()) {
            long[] particleOldPosition = particle.getPosition();

            particle.setFitness(fitnessFunction.getFitness(particleOldPosition));

            if (particle.getFitness() > particle.getBestFitness()) {
                particle.setBestFitness(particle.getFitness());
                particle.setBestPosition(particleOldPosition);

                if (particle.getFitness() > swarm.getBestFitness()) {
                    swarm.setBestFitness(particle.getFitness());
                    swarm.setBestPosition(particleOldPosition);

                    if (swarm.getBestFitness() > bestFitness) {
                        bestFitness = swarm.getBestFitness();
                        bestPosition = swarm.getBestPosition().clone();
                    }
                }
            }

            // Обновление позиции
            long[] position = particle.getPosition();
            long[] speed = particle.getSpeed();
            position[0] += speed[0];
            position[1] += speed[1];

            // Обновление скорости
            speed[0] = getNewParticleSpeedForIndex(particle, swarm, 0);
            speed[1] = getNewParticleSpeedForIndex(particle, swarm, 1);
        }
    }
}
```


```java
private static final double INERTIA_FACTOR = 0.729;
private static final double COGNITIVE_WEIGHT = 1.49445;
private static final double SOCIAL_WEIGHT = 1.49445;
private static final double GLOBAL_WEIGHT = 0.3645;
private static final Random random = new Random();

private long getNewParticleSpeedForIndex(Particle particle, Swarm swarm, int index) {
    long[] particleSpeed = particle.getSpeed();
    long[] particlePosition = particle.getPosition();
    long[] particleBestPosition = particle.getBestPosition();
    long[] swarmBestPosition = swarm.getBestPosition();

    double inertia = INERTIA_FACTOR * particleSpeed[index];
    double cognitive = random.nextDouble() * COGNITIVE_WEIGHT *
                      (particleBestPosition[index] - particlePosition[index]);
    double social = random.nextDouble() * SOCIAL_WEIGHT *
                   (swarmBestPosition[index] - particlePosition[index]);
    double global = random.nextDouble() * GLOBAL_WEIGHT *
                   (bestPosition[index] - particlePosition[index]);

    return (long) (inertia + cognitive + social + global);
}

private double randomizePercentage(double value) {
    return random.nextDouble() * value;
}
```

## Полный пример

```java
public class MultiSwarmExample {
    public static void main(String[] args) {
        FitnessFunction fitnessFunction = new LolFitnessFunction();
        Multiswarm multiswarm = new Multiswarm(5, 20, fitnessFunction);

        // Выполняем итерации
        for (int i = 0; i < 100; i++) {
            multiswarm.mainLoop();

            if (i % 10 == 0) {
                System.out.println("Итерация " + i +
                    ": Лучшая пригодность = " + multiswarm.getBestFitness());
                long[] bestPos = multiswarm.getBestPosition();
                if (bestPos != null) {
                    System.out.println("  Здоровье: " + bestPos[0] +
                        ", Броня: " + bestPos[1]);
                }
            }
        }

        // Финальный результат
        System.out.println("\nФинальный результат:");
        System.out.println("Лучшая пригодность: " + multiswarm.getBestFitness());
        long[] finalBest = multiswarm.getBestPosition();
        if (finalBest != null) {
            System.out.println("Здоровье: " + finalBest[0]);
            System.out.println("Броня: " + finalBest[1]);
            double cost = (finalBest[0] * 2.5) + (finalBest[1] * 18);
            System.out.println("Стоимость: " + cost + " золота");
        }
    }
}
```

## Преимущества и недостатки

Плюсы: несколько роёв исследуют разные области, меньше застревание в локальных оптимумах, возможна параллелизация и гибкая настройка числа роёв и частиц. Минусы: выше вычислительные затраты, чем у одного роя; много параметров для настройки; глобальный оптимум не гарантирован.

## Лучшие практики

Число роёв и частиц подбирать под размер пространства решений. Использовать проверенные веса (инерция 0.729, когнитивный и социальный 1.49445, глобальный 0.3645) и при необходимости тюнить под задачу. Задавать границы позиций и скоростей. Ограничивать итерации или останавливаться по сходимости (изменение лучшего фитнеса ниже порога).

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Частицы уходят в недопустимую область | Нет ограничения позиций/скоростей | Задать min/max для каждой размерности; при выходе за границы обрезать или отскакивать |
| Решение не улучшается | Застревание в локальном оптимуме или слабая исследование | Увеличить число роёв или частиц; повысить инерцию или случайность |
| Нестабильные результаты | Слишком мало итераций или частиц | Увеличить maxIterations; проверить инициализацию (Random seed при отладке) |

## Частые вопросы

**Чем Multi-Swarm лучше одного роя?** Несколько роёв одновременно исследуют разные области пространства, что снижает риск застревания в одном локальном оптимуме; обмен глобальным лучшим даёт сходимость к хорошему решению.

**Когда остановить алгоритм?** По фиксированному числу итераций или при малом изменении лучшего фитнеса за последние N итераций; можно комбинировать с лимитом по времени.

**Как задать ограничения (например, бюджет)?** В фитнес-функции возвращать штраф (очень плохой фитнес) для недопустимых решений; либо после обновления позиции обрезать координаты до допустимой области.

## Реализация на Kotlin

```kotlin
data class ParticleK(
    var position: LongArray,
    var speed: LongArray,
    var fitness: Double = 0.0,
    var bestPosition: LongArray,
    var bestFitness: Double = Double.NEGATIVE_INFINITY
) {
    init {
        this.bestPosition = position.clone()
    }
}
```


```kotlin
import kotlin.random.Random

class SwarmK(private val numParticles: Int) {
    val particles: Array<ParticleK>
    var bestPosition: LongArray? = null
    var bestFitness: Double = Double.NEGATIVE_INFINITY

    companion object {
        private const val PARTICLE_UPPER_BOUND = 2000
    }

    init {
        particles = Array(numParticles) {
            val initialPosition = longArrayOf(
                Random.nextInt(PARTICLE_UPPER_BOUND).toLong(),
                Random.nextInt(PARTICLE_UPPER_BOUND).toLong()
            )
            val initialSpeed = longArrayOf(
                Random.nextInt(PARTICLE_UPPER_BOUND).toLong(),
                Random.nextInt(PARTICLE_UPPER_BOUND).toLong()
            )
            ParticleK(initialPosition, initialSpeed)
        }
    }
}
```


```kotlin
class MultiSwarmOptimizerK(
    private val numSwarms: Int,
    private val particlesPerSwarm: Int
) {
    private val swarms = Array(numSwarms) { SwarmK(particlesPerSwarm) }
    private var globalBestPosition: LongArray? = null
    private var globalBestFitness: Double = Double.NEGATIVE_INFINITY

    fun optimize(maxIterations: Int) {
        for (iteration in 0 until maxIterations) {
            for (swarm in swarms) {
                updateSwarm(swarm)
            }
            updateGlobalBest()
        }
    }

    private fun updateSwarm(swarm: SwarmK) {
        // Обновление частиц в рое
        for (particle in swarm.particles) {
            updateParticleSpeed(particle, swarm)
            updateParticlePosition(particle)
            evaluateFitness(particle)
        }
    }

    private fun updateParticleSpeed(particle: ParticleK, swarm: SwarmK) {
        // Логика обновления скорости
    }

    private fun updateParticlePosition(particle: ParticleK) {
        // Обновление позиции частицы
    }

    private fun evaluateFitness(particle: ParticleK) {
        // Вычисление фитнес-функции
    }

    private fun updateGlobalBest() {
        // Обновление глобального лучшего решения
    }
}
```

## Резюме

Multi-Swarm — метаэвристика на основе нескольких роёв частиц; каждая частица обновляет позицию и скорость с учётом личного, роевого и глобального лучшего. Подходит для задач с большим пространством решений и множеством локальных оптимумов; параллелизация по роям возможна.
