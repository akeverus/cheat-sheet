---
title: "Задача об обедающих философах (Dining Philosophers Problem)"
description: "Классическая задача синхронизации: пять философов, пять вилок, каждый нуждается в двух вилках. Взаимоблокировка при циклическом ожидании и способы её устранения — изменение порядка захвата вилок, семафоры, ReentrantLock. Java и Kotlin."
tags:
  - algorithms
  - problems
  - dining-philosophers-problem
type: "reference"
difficulty: "intermediate"
aliases:
  - "Задача об обедающих философах"
  - "Dining Philosophers Problem"
  - "dining philosophers"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Задача об обедающих философах (Dining Philosophers Problem)

Классическая задача синхронизации: пять философов, пять вилок, каждый нуждается в двух вилках. Взаимоблокировка при циклическом ожидании и способы её устранения — изменение порядка захвата вилок, семафоры, ReentrantLock. Java и Kotlin.

## Полезные ссылки

### Официальная документация
- [Baeldung: Dining Philosophers](https://www.baeldung.com/)

### См. также
- [Решение лабиринтов](maze-solver.md) — maze solver
- [Поиск пути A*](a-star-pathfinding.md) — A*

- [OptaPlanner](optaplanner.md)
- [Задача о рюкзаке (Knapsack Problem)](knapsack-problem.md)
- [Валидация банковских карт (Credit Card Validation)](credit-card-validation.md)
## Содержание

- [Описание алгоритма](#описание-алгоритма)
  - [Постановка задачи](#постановка-задачи)
- [Проблема взаимоблокировки](#проблема-взаимоблокировки)
- [Решение проблемы](#решение-проблемы)
- [Реализация на Java](#реализация-на-java)
- [Реализация на Kotlin](#реализация-на-kotlin)
- [Сложность](#сложность)
- [Особенности](#особенности)
- [Применение](#применение)
- [Варианты задачи](#варианты-задачи)
  - [Вариант 1: ReentrantLock](#вариант-1-reentrantlock)
  - [Вариант 2: Использование монитора](#вариант-2-использование-монитора)
- [Когда использовать](#когда-использовать)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Описание алгоритма

Задача **Dining Philosophers** — одна из классических задач, используемых для описания проблем синхронизации в многопоточной среде и иллюстрации методов их решения. Дейкстра впервые сформулировал эту проблему и представил ее относительно компьютеров, обращающихся к периферийным устройствам ленточных накопителей.

Настоящая формулировка была дана Тони Хоаром, который также известен изобретением алгоритма быстрой сортировки. В этой статье мы анализируем эту известную проблему и кодируем популярное решение.

### Постановка задачи

Пять молчаливых философов (P1 — P5) сидят за круглым столом, проводят свою жизнь за едой и размышлениями.

У них есть пять вилок, которыми они могут поделиться (1 — 5), и чтобы иметь возможность есть, философ должен иметь вилки в обеих руках. Поев, он кладет их обоих, а затем их может взять другой философ, который повторяет тот же цикл.

Цель состоит в том, чтобы придумать схему/протокол, который поможет философам достичь своей цели — есть и думать, не умирая от голода.

## Проблема взаимоблокировки

Наивный протокол: думать взять левую вилку взять правую есть положить обе. При одновременном захвате каждым левой вилки возникает циклическое ожидание и deadlock.

```text
while(true) {
    think();
    pick_up_left_fork();
    pick_up_right_fork();
    eat();
    put_down_right_fork();
    put_down_left_fork();
}
```

## Решение проблемы

Чтобы устранить deadlock, нужно нарушить циклическое ожидание. Простой приём: один философ (например, последний) сначала берёт правую вилку, затем левую; остальные — как раньше (сначала левая, затем правая). Альтернатива — семафор на (n−1) разрешений: одновременно есть могут не более n−1 философов.

## Реализация на Java

Каждый философ — поток (`Runnable`); вилки — объекты для `synchronized`. Захват обеих вилок в одном порядке у всех, кроме одного, устраняет цикл. В коде ниже последнему философу передаём вилки в порядке (right, left), чтобы он фактически брал их в обратном порядке.

```java
// Философ: поток с доступом к левой и правой вилке; порядок захвата предотвращает deadlock
public class Philosopher implements Runnable {
    private Object leftFork;
    private Object rightFork;

    public Philosopher(Object leftFork, Object rightFork) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    private void doAction(String action) throws InterruptedException {
        System.out.println(
            Thread.currentThread().getName() + " " + action
        );
        Thread.sleep(((int) (Math.random() * 100)));
    }

    @Override
    public void run() {
        try {
            while (true) {
                doAction(System.nanoTime() + ": Thinking");

                synchronized (leftFork) {
                    doAction(System.nanoTime() + ": Picked up left fork");

                    synchronized (rightFork) {
                        doAction(System.nanoTime() +
                                ": Picked up right fork - eating");
                        doAction(System.nanoTime() + ": Put down right fork");
                    }

                    doAction(System.nanoTime() +
                            ": Put down left fork. Back to thinking");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }
}
```


```java
public class DiningPhilosophers {
    public static void main(String[] args) throws Exception {
        final Philosopher[] philosophers = new Philosopher[5];
        Object[] forks = new Object[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % forks.length];

            // Исправление: последний философ берет вилки в обратном порядке
            if (i == philosophers.length - 1) {
                philosophers[i] = new Philosopher(rightFork, leftFork);
            } else {
                philosophers[i] = new Philosopher(leftFork, rightFork);
            }

            Thread t = new Thread(philosophers[i], "Philosopher " + (i + 1));
            t.start();
        }
    }
}
```

Альтернатива — семафор на (n−1): одновременно за столом не более n−1 философов, что гарантирует хотя бы одну свободную пару вилок.

```java
import java.util.concurrent.Semaphore;

public class DiningPhilosophersSemaphore {
    private static final int NUM_PHILOSOPHERS = 5;
    private static final Semaphore table = new Semaphore(NUM_PHILOSOPHERS - 1);

    public static void main(String[] args) {
        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
        Object[] forks = new Object[NUM_PHILOSOPHERS];

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            Object leftFork = forks[i];
            Object rightFork = forks[(i + 1) % NUM_PHILOSOPHERS];

            philosophers[i] = new PhilosopherWithSemaphore(
                leftFork, rightFork, table, i + 1
            );

            new Thread(philosophers[i], "Philosopher " + (i + 1)).start();
        }
    }
}

class PhilosopherWithSemaphore implements Runnable {
    private Object leftFork;
    private Object rightFork;
    private Semaphore table;
    private int id;

    public PhilosopherWithSemaphore(Object leftFork, Object rightFork,
                                    Semaphore table, int id) {
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.table = table;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();
                table.acquire(); // Получить разрешение на стол

                synchronized (leftFork) {
                    synchronized (rightFork) {
                        eat();
                    }
                }

                table.release(); // Освободить разрешение
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking");
        Thread.sleep((int) (Math.random() * 100));
    }

    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating");
        Thread.sleep((int) (Math.random() * 100));
    }
}
```

## Реализация на Kotlin

```kotlin
import java.util.concurrent.Semaphore

class PhilosopherK(
    private val leftFork: Any,
    private val rightFork: Any,
    private val table: Semaphore,
    private val id: Int
) : Runnable {
    override fun run() {
        try {
            while (true) {
                think()
                table.acquire() // Получить разрешение на стол

                synchronized(leftFork) {
                    synchronized(rightFork) {
                        eat()
                    }
                }

                table.release() // Освободить разрешение
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    private fun think() {
        println("Philosopher $id is thinking")
        Thread.sleep((Math.random() * 100).toLong())
    }

    private fun eat() {
        println("Philosopher $id is eating")
        Thread.sleep((Math.random() * 100).toLong())
    }
}
```

```kotlin
fun main() {
    val numPhilosophers = 5
    val philosophers = arrayOfNulls<PhilosopherK>(numPhilosophers)
    val forks = Array(numPhilosophers) { Any() }
    val table = Semaphore(numPhilosophers - 1)

    for (i in 0 until numPhilosophers) {
        val leftFork = forks[i]
        val rightFork = forks[(i + 1) % forks.size]

        // Исправление: последний философ берет вилки в обратном порядке
        if (i == numPhilosophers - 1) {
            philosophers[i] = PhilosopherK(rightFork, leftFork, table, i)
        } else {
            philosophers[i] = PhilosopherK(leftFork, rightFork, table, i)
        }
    }

    philosophers.forEach { Thread(it).start() }
}
```

Вариант с `ReentrantLock` и `tryLock()` — без блокирующего ожидания; при неудаче философ может повторить попытку позже.
```kotlin
import java.util.concurrent.locks.ReentrantLock

class DiningPhilosophersLockK(private val numPhilosophers: Int = 5) {
    private val forks = Array(numPhilosophers) { ReentrantLock() }

    fun eat(philosopherId: Int) {
        val leftFork = forks[philosopherId]
        val rightFork = forks[(philosopherId + 1) % numPhilosophers]

        if (leftFork.tryLock()) {
            try {
                if (rightFork.tryLock()) {
                    try {
                        println("Philosopher $philosopherId is eating")
                        Thread.sleep((Math.random() * 100).toLong())
                    } finally {
                        rightFork.unlock()
                    }
                }
            } finally {
                leftFork.unlock()
            }
        }
    }
}
```

## Сложность

Без исправления — риск бесконечной блокировки. С изменением порядка или семафором: захват вилки O(1). Память: O(n) на вилки и потоки.

## Особенности

Синхронизация через `synchronized` или `ReentrantLock`; устранение deadlock за счёт порядка захвата или ограничения числа одновременных едоков. Модель легко масштабируется на другое число философов.

## Применение

Обучение многопоточности, демонстрация синхронизации и взаимоблокировок, тестирование примитивов и протоколов параллельных вычислений.

## Варианты задачи

### Вариант 1: ReentrantLock

```java
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersLock {
    private static final int NUM_PHILOSOPHERS = 5;
    private ReentrantLock[] forks = new ReentrantLock[NUM_PHILOSOPHERS];

    public DiningPhilosophersLock() {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new ReentrantLock();
        }
    }

    public void eat(int philosopherId) {
        ReentrantLock leftFork = forks[philosopherId];
        ReentrantLock rightFork = forks[(philosopherId + 1) % NUM_PHILOSOPHERS];

        // Попытка захватить обе вилки с таймаутом
        if (leftFork.tryLock()) {
            try {
                if (rightFork.tryLock()) {
                    try {
                        // Есть
                    } finally {
                        rightFork.unlock();
                    }
                }
            } finally {
                leftFork.unlock();
            }
        }
    }
}
```

### Вариант 2: Использование монитора

```java
public class DiningPhilosophersMonitor {
    private enum State { THINKING, HUNGRY, EATING }
    private State[] states = new State[NUM_PHILOSOPHERS];
    private Object[] monitors = new Object[NUM_PHILOSOPHERS];

    public DiningPhilosophersMonitor() {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            states[i] = State.THINKING;
            monitors[i] = new Object();
        }
    }

    public void takeForks(int i) {
        states[i] = State.HUNGRY;
        test(i);
        if (states[i] != State.EATING) {
            synchronized (monitors[i]) {
                try {
                    monitors[i].wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public void putForks(int i) {
        states[i] = State.THINKING;
        test((i + 4) % 5);
        test((i + 1) % 5);
    }

    private void test(int i) {
        if (states[(i + 4) % 5] != State.EATING &&
            states[i] == State.HUNGRY &&
            states[(i + 1) % 5] != State.EATING) {
            states[i] = State.EATING;
            synchronized (monitors[i]) {
                monitors[i].notify();
            }
        }
    }
}
```

## Когда использовать

Изменение порядка захвата — когда нужно простое решение при небольшом числе философов. Семафор — когда важно ограничить число одновременно едящих или нужна гибкость; реализация сложнее. ReentrantLock с tryLock — когда нужны таймауты и явный контроль.

## Лучшие практики

Нарушайте циклическое ожидание: один философ в обратном порядке или глобальный порядок (всегда сначала вилка с меньшим индексом). Семафор на (n−1) устраняет deadlock; ReentrantLock даёт таймауты. Следите за голоданием: все философы должны периодически есть; при необходимости добавьте приоритеты или таймауты. Тестируйте длительной симуляцией; в коде явно укажите используемый протокол.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---------|-------------------|---------|
| Потоки зависают навсегда | Deadlock: все держат левую вилку | Ввести обратный порядок для одного философа или семафор на n−1 |
| Один философ не ест | Голодание (starvation) | Семафор или честная очередь; при tryLock — повторные попытки с задержкой |
| IllegalMonitorStateException при unlock | Вызов unlock не из того потока или лишний unlock | unlock только в finally и только тем потоком, который держит lock |

## Частые вопросы

**Достаточно ли изменить порядок для одного философа?** Да: цикл «каждый ждёт правую вилку соседа» разрывается, если хотя бы один берёт вилки в другом порядке.

**Чем семафор лучше порядка вилок?** Семафор ограничивает число едоков и не привязывает решение к конкретной нумерации; порядок вилок проще реализовать и объяснить.

**Когда использовать монитор (wait/notify)?** Когда нужен явный протокол «проверка условия — ожидание — уведомление соседей»; сложнее, но даёт полный контроль над очередностью.

## Заключение

Задача об обедающих философах иллюстрирует взаимоблокировку и способы её устранения: изменение порядка захвата вилок, семафор на (n−1), ReentrantLock с tryLock или монитор. Выбор зависит от требований к простоте и контролю над параллелизмом.
