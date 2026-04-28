---
title: "Circular Buffer"
description: "A guide to implementing a circular buffer (ring buffer) in Java for efficient data buffering between threads."
tags:
  - algorithms
  - problems
  - circular-buffer
type: "reference"
difficulty: "intermediate"
aliases:
  - "Circular Buffer"
  - "ring buffer"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Circular Buffer

A **guide** to **implementing** a **circular buffer** (ring buffer) in **Java for efficient data buffering between threads**.

## Полезные ссылки

### Официальная документация
- [Java Concurrency Utilities](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html)
- [Disruptor Pattern](https://lmax-exchange.github.io/disruptor/)

### См. также
- [Задачи и алгоритмы](../../basics/README.md)
- [Thread-Safe Data Structures](../data-structures/collections-lock-free.md)
- [Структуры данных](../data-structures/)

- [OptaPlanner](optaplanner.md)
- [Задача о рюкзаке (Knapsack Problem)](knapsack-problem.md)
- [Валидация банковских карт (Credit Card Validation)](credit-card-validation.md)
## Содержание

- [Обзор](#обзор)
- [Что такое кольцевой буфер?](#что-такое-кольцевой-буфер)
- [Implementation Approach](#implementation-approach)
- [Basic Operations](#basic-operations)
  - [Inserting an Element](#inserting-an-element)
  - [Consuming an Element](#consuming-an-element)
  - [Buffer State](#buffer-state)
- [Java Implementation](#java-implementation)
  - [Constructor](#constructor)
  - [Offer Operation](#offer-operation)
  - [Poll Operation](#poll-operation)
  - [Size Method](#size-method)
  - [Test Example](#test-example)
- [Kotlin Implementation](#kotlin-implementation)
  - [Basic Circular Buffer](#basic-circular-buffer)
  - [Thread-Safe Implementation](#thread-safe-implementation)
  - [Producer-Consumer Example](#producer-consumer-example)
- [Thread-Safe Implementation](#thread-safe-implementation-1)
- [Producer-Consumer Example](#producer-consumer-example-1)
- [Complexity Analysis](#complexity-analysis)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Обзор

A **circular buffer** (or ring buffer) is a **bounded circular data structure used for buffering data between two** or **more threads**. `As we` **continue writing** to **the circular buffer**, it **wraps around when** it **reaches the end**.

**The circular buffer** is **implemented using** a **fixed-size array that wraps around boundaries**.

## Что такое кольцевой буфер?

A **circular buffer** is an **efficient FIFO buffer**. It **uses** a **fixed-size array that can** be **pre-allocated** in **advance and provides** an **efficient memory access pattern**. **All buffer operations are performed** in **constant time** `O(1)`, **including consuming** an **element**, as **this doesn**'t **require shifting elements**.

**Besides the array**, it **tracks three things**:**

1. **The next available slot** in **the buffer for inserting** an **element**
2. **The next unread element** in **the buffer**
3. **The end** of **the array** — **the point** at **which the buffer wraps** to **the beginning** of **the array**

**The mechanics** of **how** a **circular buffer handles these requirements depends** on **the implementation**. **For example**, **the Wikipedia entry** on **this topic shows** a **method using four pointers**.

## Implementation Approach

We'll **borrow the approach from the Disruptor**'s **circular buffer implementation using sequences**.

**The first thing** we **need** to **know** is **the capacity** — **the fixed maximum size** of **the buffer**. **Next**, we'll **use two monotonically increasing sequences**:**

1. **Write sequence: Starting** at -1, **increases** `by 1` **when inserting** an **element**
2. **Read sequence: Starting** `at 0`, **increases** `by 1` as an **element** is **consumed**

**We **can map** a **sequence** to an **index** in **the array using the mod operation**:**

```java
arrayIndex = sequence % capacity
```

**The mod operation wraps the sequence around the boundaries** to **get** a **slot** in **the buffer**.

## Basic Operations

### Inserting an Element

**Let**'s **see how** we'll **insert** an **element**:**

```java
buffer[++writeSequence % capacity] = element
```

We **pre-increment the sequence before inserting the element**.

### Consuming an Element

**To **consume** an **element**, we do a **post-increment**:**

```java
element = buffer[readSequence++ % capacity]
```

In **this case**, we **perform** a **post-increment** of **the sequence**. **Consuming** an **element doesn**'t **remove** it **from the buffer** — it **just remains** in **the array until** it's **overwritten**.

### Buffer State

`As we` **wrap around the array**, we **start overwriting data** in **the buffer**. If **the buffer** is **full**, we **can either overwrite the oldest data regardless** of **whether the reader has consumed** it, or **prevent overwriting data that hasn**'t **been read**.

If **the reader can afford** to **skip intermediate** or **old values** (such as a stock price ticker), we **can overwrite data without waiting for** it to be **consumed**. On **the other hand**, if **the reader must consume all values** (as in `e-commerce` transactions), we **must wait** (blocking wait/busy wait) **until** a **free slot appears** in **the buffer**.

**The buffer** is **full** if **the buffer size equals its capacity**, **where its size equals the number** of **unread elements**:**

```java
size = (writeSequence - readSequence) + 1
isFull = (size == capacity)
```

**If **the write sequence lags behind the read sequence**, **the buffer** is **empty**:**

```java
isEmpty = writeSequence < readSequence
```

**The buffer returns null** if it's **empty**.

## Java Implementation

**Now that** we **understand how** a **circular buffer works**, **let**'s **implement** it in **Java**.

### Constructor

**First**, **let**'s **define** a **constructor that initializes the buffer with** a **predefined capacity**:**

```java
// Кольцевой буфер фиксированной ёмкости на основе последовательностей (write/read)
public class CircularBuffer<E> {
    private final int capacity;
    private final E[] data;
    private int readSequence = 0;
    private int writeSequence = -1;
    private static final int DEFAULT_CAPACITY = 8;

    @SuppressWarnings("unchecked")
    public CircularBuffer(int capacity) {
        this.capacity = (capacity < 1) ? DEFAULT_CAPACITY : capacity;
        this.data = (E[]) new Object[this.capacity];
        this.readSequence = 0;
        this.writeSequence = -1;
    }
}
```

**This creates** an **empty buffer and initializes the sequence fields** as **discussed** in **the previous section**.

### Offer Operation

**Next**, we **implement the offer operation**, **which inserts** an **element into the buffer** at **the next available slot and returns true** on **success**. **Returns false** if **the buffer can**'t **find** an **empty slot**, **i.e**., we **can**'t **overwrite unread values**.

```java
public boolean offer(E element) {
    boolean isFull = (writeSequence - readSequence) + 1 == capacity;

    if (!isFull) {
        int nextWriteSeq = writeSequence + 1;
        data[nextWriteSeq % capacity] = element;
        writeSequence++;
        return true;
    }

    return false;
}
```

`So we` **increment the write sequence and calculate the index** in **the array for the next available slot**. **Then** we **write the data** to **the buffer and save the updated write sequence**.

### Poll Operation

**Finally**, we **implement the poll operation**, **which retrieves and removes the next unread element**. **The poll operation doesn**'t **remove the element**, **but increments the read sequence**.

```java
public E poll() {
    boolean isEmpty = writeSequence < readSequence;

    if (!isEmpty) {
        E nextValue = data[readSequence % capacity];
        readSequence++;
        return nextValue;
    }

    return null;
}
```

**Here** we **read the data** at **the current read sequence**, **calculating the index** in **the array**. **Then** we **increment the sequence and return the value** if **the buffer** is **not empty**.

### Size Method

```java
public int size() {
    if (writeSequence < readSequence) {
        return 0;
    }
    return (writeSequence - readSequence) + 1;
}
```

### Test Example

```java
@Test
public void givenCircularBuffer_whenAnElementIsEnqueued_thenSizeIsOne() {
    CircularBuffer<String> buffer = new CircularBuffer<>(8);
    assertTrue(buffer.offer("Square"));
    assertEquals(1, buffer.size());
}

@Test
public void givenCircularBuffer_whenAnElementIsDequeued_thenElementMatchesEnqueuedElement() {
    CircularBuffer<String> buffer = new CircularBuffer<>(8);
    buffer.offer("Triangle");
    String shape = buffer.poll();
    assertEquals("Triangle", shape);
}
```

## Kotlin Implementation

### Basic Circular Buffer

```kotlin
class CircularBufferK<E>(capacity: Int) {
    private val capacity: Int = if (capacity < 1) DEFAULT_CAPACITY else capacity
    private val data: Array<Any?> = arrayOfNulls(this.capacity)
    private var readSequence = 0
    private var writeSequence = -1

    companion object {
        private const val DEFAULT_CAPACITY = 8
    }

    fun offer(element: E): Boolean {
        val isFull = (writeSequence - readSequence) + 1 == capacity

        if (!isFull) {
            val nextWriteSeq = writeSequence + 1
            data[nextWriteSeq % capacity] = element
            writeSequence++
            return true
        }

        return false
    }

    @Suppress("UNCHECKED_CAST")
    fun poll(): E? {
        if (isEmpty()) {
            return null
        }

        val nextReadSeq = readSequence % capacity
        val nextValue = data[nextReadSeq] as E
        data[nextReadSeq] = null
        readSequence++
        return nextValue
    }

    fun size(): Int {
        return (writeSequence - readSequence) + 1
    }

    fun isEmpty(): Boolean {
        return writeSequence < readSequence
    }

    fun capacity(): Int = capacity
}
```

### Thread-Safe Implementation

```kotlin
class ThreadSafeCircularBufferK<E>(capacity: Int) {
    private val capacity: Int = if (capacity < 1) DEFAULT_CAPACITY else capacity
    private val data: Array<Any?> = arrayOfNulls(this.capacity)

    @Volatile
    private var readSequence = 0

    @Volatile
    private var writeSequence = -1

    companion object {
        private const val DEFAULT_CAPACITY = 8
    }

    fun offer(element: E): Boolean {
        val isFull = (writeSequence - readSequence) + 1 == capacity

        if (!isFull) {
            val nextWriteSeq = writeSequence + 1
            data[nextWriteSeq % capacity] = element
            writeSequence = nextWriteSeq
            return true
        }

        return false
    }

    @Suppress("UNCHECKED_CAST")
    fun poll(): E? {
        if (isEmpty()) {
            return null
        }

        val nextReadSeq = readSequence % capacity
        val nextValue = data[nextReadSeq] as E
        data[nextReadSeq] = null
        readSequence = nextReadSeq + 1
        return nextValue
    }

    fun isEmpty(): Boolean {
        return writeSequence < readSequence
    }
}
```

### Producer-Consumer Example

```kotlin
import java.util.concurrent.Executors

class ProducerK<T>(private val buffer: CircularBufferK<T>, private val items: Array<T>) : Runnable {
    override fun run() {
        var i = 0
        while (i < items.size) {
            if (buffer.offer(items[i])) {
                println("Produced: ${items[i]}")
                i++
            }
        }
    }
}

class ConsumerK<T>(private val buffer: CircularBufferK<T>, private val expectedCount: Int) : Callable<Array<T?>> {
    override fun call(): Array<T?> {
        val items = arrayOfNulls<T>(expectedCount)
        var i = 0
        while (i < items.size) {
            val item = buffer.poll()
            if (item != null) {
                items[i++] = item
                println("Consumed: $item")
            }
        }
        return items
    }
}

fun main() {
    val executorService = Executors.newFixedThreadPool(2)
    val buffer = CircularBufferK<String>(8)

    val items = arrayOf("Circle", "Triangle", "Rectangle", "Square",
        "Rhombus", "Trapezoid", "Pentagon", "Pentagram",
        "Hexagon", "Hexagram")

    executorService.submit(ProducerK(buffer, items))
    executorService.submit(ConsumerK(buffer, items.size))

    executorService.shutdown()
}
```

## Thread-Safe Implementation

We **talked about using** a **circular buffer for exchanging data between two** or **more threads**, **which** is an **example** of a **synchronization problem called the producer-consumer problem**. In **Java**, we **can solve the producer-consumer problem** in **various ways using semaphores**, **bounded queues**, **circular buffers**, **etc**.

**Let**'s **implement** a **circular buffer-based solution**.

**Our circular buffer implementation** is **not thread-safe**. **Let**'s **make** it **thread-safe for the simple case** of a **single producer and single consumer**.

**The producer writes data** to **the buffer and increments writeSequence**, **while the consumer only reads from the buffer and increments readSequence**. **Thus**, **the backing array doesn**'t **conflict**, **and** we **can get away without synchronization**.

**But** we **still need** to **ensure that the consumer can see the latest value** of **the writeSequence field** (visibility) **and that writeSequence** is **not updated until the data** is **actually available** in **the buffer** (ordering).

**In **this case**, we **can make the circular buffer concurrent and lock-free** by **making the sequence fields volatile**:**

```java
private volatile int writeSequence = -1, readSequence = 0;
```

In **the offer method**, **writing** to **the volatile writeSequence field ensures that writing** to **the buffer happens before updating the sequence**. At **the same time**, **the volatile visibility guarantee ensures that the consumer will always see the latest value** of **writeSequence**.

## Producer-Consumer Example

**Let**'s **implement** a **simple producer Runnable that writes** to **the circular buffer**:**

```java
public class Producer<T> implements Runnable {
    private final CircularBuffer<T> buffer;
    private final T[] items;

    public Producer(CircularBuffer<T> buffer, T[] items) {
        this.buffer = buffer;
        this.items = items;
    }

    @Override
    public void run() {
        for (int i = 0; i < items.length;) {
            if (buffer.offer(items[i])) {
                System.out.println("Produced: " + items[i]);
                i++;
            }
        }
    }
}
```

**The producer thread will wait for** an **empty slot** to **appear** in a **loop** (busy wait).

**We **implement** a **Callable consumer that reads from the buffer**:**

```java
public class Consumer<T> implements Callable<T[]> {
    private final CircularBuffer<T> buffer;
    private final int expectedCount;

    public Consumer(CircularBuffer<T> buffer, int expectedCount) {
        this.buffer = buffer;
        this.expectedCount = expectedCount;
    }

    @Override
    public T[] call() {
        @SuppressWarnings("unchecked")
        T[] items = (T[]) new Object[expectedCount];
        for (int i = 0; i < items.length;) {
            T item = buffer.poll();
            if (item != null) {
                items[i++] = item;
                System.out.println("Consumed: " + item);
            }
        }
        return items;
    }
}
```

**The consumer thread continues without printing** if it **gets** a **null value from the buffer**.

**Driver code**:**

```java
ExecutorService executorService = Executors.newFixedThreadPool(2);
CircularBuffer<String> buffer = new CircularBuffer<>(8);

String[] items = {"Circle", "Triangle", "Rectangle", "Square",
                  "Rhombus", "Trapezoid", "Pentagon", "Pentagram",
                  "Hexagon", "Hexagram"};

executorService.submit(new Thread(new Producer<>(buffer, items)));
executorService.submit(new Thread(new Consumer<>(buffer, items.length)));

executorService.shutdown();
```

**Execution** of **our producer-consumer program results** in **output like**:**

```text
Produced: Circle
Produced: Triangle
Consumed: Circle
Produced: Rectangle
Consumed: Triangle
Consumed: Rectangle
Produced: Square
Produced: Rhombus
Consumed: Square
Produced: Trapezoid
Consumed: Rhombus
Consumed: Trapezoid
Produced: Pentagon
Produced: Pentagram
Produced: Hexagon
Consumed: Pentagon
Consumed: Pentagram
Produced: Hexagram
Consumed: Hexagon
Consumed: Hexagram
```

## Complexity Analysis

- **Time `Complexity`: All operations** (offer, poll, size) **are** `O(1)`
- **Space `Complexity`:** `O(n)` **where** n is **the capacity** of **the buffer**

## Лучшие практики

Выбирайте размер буфера под пиковую нагрузку; слишком маленький буфер приведёт к блокировкам записи или потере данных в режиме overwrite. При overwrite старые непрочитанные данные теряются (подходит для тикеров, сенсоров); при необходимости сохранять все элементы используйте блокирующие offer/poll или отказ при переполнении. Для многопоточности используйте атомарные последовательности (например, в стиле Disruptor) или блокировки; документируйте, потокобезопасен ли экземпляр. Отслеживайте размер очереди (writeSequence — readSequence) и долю переполнений — это поможет подобрать capacity и выявить медленных потребителей. В тестах покройте случаи: пустой/полный буфер, обход по кольцу (write/read после конца массива), один производитель — один потребитель.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Потеря данных при пиковой нагрузке | Буфер переполняется (overwrite) или блокировка не обработана | Увеличить capacity; или использовать блокирующий offer и обрабатывать отказ |
| Блокировка записи надолго | Медленный потребитель, маленький буфер | Увеличить размер буфера; мониторить разницу write-read; оптимизировать потребителя |
| Race condition в многопоточной среде | Буфер не потокобезопасен | Использовать потокобезопасную реализацию (атомарные индексы или блокировки); документировать контракт |

## Частые вопросы

**Overwrite или блокировка при переполнении?** Overwrite — когда важны только последние данные (тикеры, сенсоры). Блокировка или отказ — когда нужно сохранить каждое сообщение; тогда потребитель не должен отставать надолго.

**Как выбрать размер буфера?** Ориентируйтесь на пиковую нагрузку и скорость потребителя; мониторинг (writeSequence — readSequence) и доля переполнений подскажут, нужно ли увеличить capacity.

**Нужна ли потокобезопасность?** Если буфер используется из нескольких потоков — да; используйте атомарные счётчики или блокировки и явно документируйте это в API.

## Заключение

Выбор размера циклического буфера критически важен: при недостаточном размере операции записи могут блокироваться на длительное время, если чтение не успевает. Динамическое изменение размера возможно, но требует перемещения данных, что нивелирует главное преимущество кольцевого буфера — `O(1)` операции без аллокаций.
