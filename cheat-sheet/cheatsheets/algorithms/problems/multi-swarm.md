# Multi-Swarm

Руководство по алгоритму оптимизации Multi-Swarm для решения задач оптимизации с использованием нескольких роев частиц.

**Последнее обновление:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Particle Swarm Optimization - Wikipedia](https://en.wikipedia.org/wiki/Particle_swarm_optimization)
- [Swarm Intelligence Algorithms](https://www.sciencedirect.com/topics/computer-science/swarm-intelligence)

### См. также
- [Hill-Climbing](./hill-climbing.md)
- [Генетические алгоритмы](../ai-collections/genetic-algorithms.md)
- [Алгоритмы оптимизации](./greedy-algorithms.md)

## Содержание

- [Обзор](#обзор)
- [Что такое Multi-Swarm?](#что-такое-multi-swarm)
- [Компоненты алгоритма](#компоненты-алгоритма)
- [Пример задачи](#пример-задачи)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Обновление скорости частиц](#обновление-скорости-частиц)
- [Полный пример](#полный-пример)

## Обзор

В этой статье мы рассмотрим алгоритм оптимизации Multi-Swarm. Как и другие алгоритмы того же класса, его цель - найти наилучшее решение проблемы путем максимизации или минимизации определенной функции, называемой фитнес-функцией.

## Что такое Multi-Swarm?

Multi-Swarm - это разновидность алгоритма Swarm. Как следует из названия, алгоритм Swarm решает задачу, моделируя движение группы объектов в пространстве возможных решений. В версии с несколькими роями используется несколько роев, а не один.

Основной компонент роя называется частицей. Частица определяется ее фактическим положением, которое также является возможным решением нашей проблемы, и ее скоростью, которая используется для вычисления следующей позиции.

Скорость частицы постоянно меняется, склоняясь к наилучшему положению, найденному среди всех частиц во всех роях, с определенной степенью случайности, чтобы увеличить количество охватываемого пространства.

Это в конечном итоге приводит большинство частиц к конечному набору точек, которые являются локальными минимумами или максимумами в функции пригодности, в зависимости от того, пытаемся ли мы минимизировать или максимизировать ее.

Хотя найденная точка всегда является локальным минимумом или максимумом функции, она не обязательно является глобальной, поскольку нет гарантии, что алгоритм полностью исследовал пространство решений.

По этой причине мульти-рой считается метаэвристикой - решения, которые он находит, являются одними из лучших, но они не могут быть абсолютно лучшими.

## Компоненты алгоритма

### Частица (Particle)

Частица представляет собой возможное решение задачи. Она содержит:
- Текущую позицию (решение)
- Скорость (направление движения)
- Лучшую найденную позицию
- Оценку пригодности

### Рой (Swarm)

Рой состоит из множества частиц, которые взаимодействуют друг с другом, обмениваясь информацией о лучших найденных решениях.

### Multi-Swarm

Multi-Swarm использует несколько роев, что позволяет исследовать разные области пространства решений одновременно.

## Пример задачи

В нашем примере мы попытаемся решить эту реальную проблему оптимизации, опубликованную на StackExchange:

В League of Legends эффективное здоровье игрока при защите от физического урона определяется как:

```
E = H(100 + A) / 100
```

где:
- H - здоровье
- A - броня

Здоровье стоит 2.5 золота за единицу, а броня стоит 18 золотых за единицу. У вас есть 3600 золотых, и вам нужно оптимизировать эффективность E вашего здоровья и брони, чтобы выжить как можно дольше против атак вражеской команды. Сколько каждого из них вы должны купить?

## Java Implementation

### Класс Particle

Мы начинаем с моделирования нашей базовой конструкции, частицы:

```java
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

Мы решили использовать длинные массивы для представления как скорости, так и позиции, потому что из постановки задачи можно сделать вывод, что мы не можем купить доли брони или здоровья, следовательно, решение должно быть в целочисленной области.

### Класс Swarm

Далее, давайте определим рой как набор частиц:

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

Рой также должен будет позаботиться об инициализации своих частиц, назначив каждой случайную начальную позицию и скорость.

### Класс Multiswarm

Наконец, давайте завершим нашу модель созданием класса Multiswarm:

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

### Интерфейс FitnessFunction

Давайте теперь реализуем фитнес-функцию. Чтобы отделить логику алгоритма от этой конкретной проблемы, мы представим интерфейс с одним методом:

```java
public interface FitnessFunction {
    double getFitness(long[] particlePosition);
}
```

### Реализация фитнес-функции для League of Legends

При условии, что найденный результат действителен в соответствии с ограничениями задачи, измерение пригодности - это всего лишь вопрос возврата вычисленного эффективного здоровья, которое мы хотим максимизировать.

Для нашей проблемы у нас есть следующие конкретные ограничения проверки:

1. Решения должны быть только положительными целыми числами
2. Решения должны быть осуществимы с предоставленным количеством золота

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

Основная программа будет перебирать все частицы во всех роях и делать следующее:

1. Вычислить приспособленность частиц
2. Если найдена новая лучшая позиция, обновить историю частиц, роя и мультироя
3. Вычислить новую позицию частицы, добавив текущую скорость к каждому измерению
4. Вычислить новую скорость частицы

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

Для частицы важно изменить свою скорость, поскольку именно так ей удается исследовать различные возможные решения.

Скорость частицы должна заставить частицу двигаться к лучшей позиции, найденной ею самой, ее роем и всеми роями, присваивая каждому из них определенный вес. Мы будем называть эти веса когнитивным весом, социальным весом и глобальным весом соответственно.

Чтобы внести некоторую вариативность, мы умножим каждый из этих весов на случайное число от 0 до 1. Мы также добавим в формулу коэффициент инерции, который побуждает частицу не замедляться слишком сильно:

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

Принятые значения инерционных, когнитивных, социальных и глобальных весов составляют 0.729, 1.49445, 1.49445 и 0.3645 соответственно.

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

## Преимущества Multi-Swarm

- **Исследование разных областей**: Несколько роев могут исследовать разные области пространства решений
- **Избежание локальных оптимумов**: Больше шансов найти глобальный оптимум
- **Параллелизация**: Рои могут работать параллельно
- **Гибкость**: Можно настроить количество роев и частиц

## Недостатки

- **Вычислительная сложность**: Требует больше вычислений, чем простой PSO
- **Параметры**: Требует настройки многих параметров
- **Нет гарантии**: Не гарантирует нахождение глобального оптимума

## Kotlin Implementation

### Класс Particle

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

### Класс Swarm

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

### Multi-Swarm Optimizer

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

Multi-Swarm - это мощный алгоритм оптимизации, который использует несколько роев частиц для исследования пространства решений.

Ключевые моменты:
- Использует несколько роев для лучшего исследования
- Каждая частица запоминает свою лучшую позицию
- Рои обмениваются информацией о глобальном лучшем решении
- Скорость частиц обновляется на основе когнитивного, социального и глобального компонентов
- Эффективен для задач с большим пространством решений

Алгоритм особенно полезен для задач оптимизации, где:
- Пространство решений велико и сложно
- Есть много локальных оптимумов
- Требуется баланс между исследованием и эксплуатацией
- Можно использовать параллельные вычисления
