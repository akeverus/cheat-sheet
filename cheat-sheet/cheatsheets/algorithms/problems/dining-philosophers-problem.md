# Dining Philosophers Problem

Кратко: решение классической задачи синхронизации "Проблема обедающих философов" с использованием потоков Java. Рассматривается проблема взаимоблокировки и способы ее предотвращения.

**Дата последнего обновления:** 2025-01-15

## Полезные ссылки

### Официальная документация
- [Baeldung: Dining Philosophers](https://www.baeldung.com/java-dining-philosophers)

### См. также
- `./maze-solver.md` - решение лабиринтов
- `./a-star-pathfinding.md` - поиск пути A*

## Содержание

- [Описание алгоритма](#описание-алгоритма)
- [Проблема взаимоблокировки](#проблема-взаимоблокировки)
- [Решение проблемы](#решение-проблемы)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [Сложность](#сложность)

## Описание алгоритма

Задача Dining Philosophers - одна из классических задач, используемых для описания проблем синхронизации в многопоточной среде и иллюстрации методов их решения. Дейкстра впервые сформулировал эту проблему и представил ее относительно компьютеров, обращающихся к периферийным устройствам ленточных накопителей.

Настоящая формулировка была дана Тони Хоаром, который также известен изобретением алгоритма быстрой сортировки. В этой статье мы анализируем эту известную проблему и кодируем популярное решение.

### Постановка задачи

Пять молчаливых философов (P1 - P5) сидят за круглым столом, проводят свою жизнь за едой и размышлениями.

У них есть пять вилок, которыми они могут поделиться (1 - 5), и чтобы иметь возможность есть, философ должен иметь вилки в обеих руках. Поев, он кладет их обоих, а затем их может взять другой философ, который повторяет тот же цикл.

Цель состоит в том, чтобы придумать схему/протокол, который поможет философам достичь своей цели - есть и думать, не умирая от голода.

## Проблема взаимоблокировки

Первоначальным решением было бы заставить каждого из философов следовать следующему протоколу:

```java
while(true) {
    think();
    pick_up_left_fork();
    pick_up_right_fork();
    eat();
    put_down_right_fork();
    put_down_left_fork();
}
```

Как описывает приведенный выше псевдокод, каждый философ изначально думает. Через определенное время философ проголодается и захочет есть.

В этот момент он тянется к вилкам с обеих сторон и, получив их обе, приступает к еде. После еды философ кладет вилки так, чтобы они были доступны для соседа.

### Проблема

Хотя кажется, что приведенное выше решение правильное, возникает проблема взаимоблокировки.

Взаимная блокировка - это ситуация, когда работа системы останавливается, поскольку каждый процесс ожидает получения ресурса, удерживаемого каким-либо другим процессом.

В этой ситуации каждый из Философов приобрел свою левую вилку, но не может приобрести правую вилку, потому что ее уже приобрел его сосед. Эта ситуация широко известна как циклическое ожидание и является одним из условий, которые приводят к взаимоблокировке и препятствуют работе системы.

## Решение проблемы

Как мы видели выше, основной причиной взаимоблокировки является циклическое ожидание, когда каждый процесс ожидает ресурса, удерживаемого другим процессом. Следовательно, чтобы избежать тупиковой ситуации, нам нужно убедиться, что условие циклического ожидания нарушено.

### Решение: Изменить порядок захвата вилок

Самый простой способ - все Философы первыми тянутся к своей левой вилке, кроме одного, который первым тянется к своей правой вилке.

## Java Implementation

### Реализация

Мы моделируем каждого из наших философов как классы, реализующие интерфейс Runnable, чтобы мы могли запускать их как отдельные потоки. Каждый Философ имеет доступ к двум развилкам слева и справа:

```java
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

Как показано в приведенном выше коде, каждое действие моделируется путем приостановки вызывающего потока на случайное время, чтобы порядок выполнения не зависел только от времени.

Чтобы имитировать получение форка, нам нужно заблокировать его, чтобы никакие два потока Philosopher не могли получить его одновременно.

Для этого мы используем ключевое слово synchronized, чтобы получить внутренний монитор объекта fork и предотвратить то же самое от других потоков.

### Клиентский код с исправлением

Чтобы запустить весь процесс, мы пишем клиент, который создает 5 Философов как потоки и запускает их всех:

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

Изменение происходит в строках, где мы вводим условие, которое заставляет последнего философа сначала тянуться к своей правой вилке, а не к левой. Это нарушает условие циклического ожидания, и мы можем предотвратить взаимоблокировку.

### Альтернативное решение: Семафоры

Другое решение - использовать семафоры для ограничения количества философов, которые могут есть одновременно:

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

## Kotlin Implementation

### Класс Philosopher

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

### Решение с изменением порядка

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

### Решение с ReentrantLock

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

### Временная сложность

- **Без исправления:** Может привести к взаимоблокировке (бесконечное ожидание)
- **С исправлением:** O(1) - для каждой операции захвата вилки

### Пространственная сложность

- **Хранение вилок:** O(n) - где n - количество философов
- **Потоки:** O(n) - по одному потоку на философа

## Особенности

- **Синхронизация:** Использует synchronized для блокировки вилок
- **Предотвращение взаимоблокировки:** Изменение порядка захвата вилок
- **Гибкость:** Легко адаптировать для различного количества философов

## Применение

Проблема обедающих философов используется в:

- Обучении многопоточности
- Демонстрации проблем синхронизации
- Тестировании алгоритмов синхронизации
- Исследованиях в области параллельных вычислений

## Варианты задачи

### Вариант 1: Использование ReentrantLock

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

### Используйте изменение порядка, когда:

- Нужно простое решение
- Количество философов небольшое
- Важна простота реализации

### Используйте семафоры, когда:

- Нужно ограничить количество одновременно едящих
- Важна гибкость
- Готовы к более сложной реализации

## Заключение

В этой статье мы рассмотрели классическую проблему синхронизации "Проблема обедающих философов" и показали, как избежать взаимоблокировки, изменив порядок захвата вилок. Мы также рассмотрели альтернативные решения с использованием семафоров и других механизмов синхронизации.
