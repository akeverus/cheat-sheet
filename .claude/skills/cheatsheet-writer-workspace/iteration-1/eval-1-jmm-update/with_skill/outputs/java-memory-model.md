---
title: "Java Memory Model (JMM)"
description: "Исчерпывающая шпаргалка по Java Memory Model: happens-before, volatile, final fields semantics, memory barriers, гарантии synchronized и Lock, VarHandle memory modes. Практические примеры гонок и их исправления."
tags:
  - languages
  - java
  - jmm
  - memory-model
  - concurrency
  - happens-before
difficulty: "advanced"
prerequisites: ["java-concurrency-basics.md"]
next: ["java-concurrency-advanced.md"]
updated: "2026-04-11"
related: ["java-concurrency-basics.md", "java-concurrency-advanced.md"]
---

# Java Memory Model (JMM)

JMM (JSR-133, Java 5+) — формальная спецификация, определяющая правила видимости записей в память между потоками. JMM отвечает на вопрос: «какие значения может увидеть поток при чтении переменной?». Без понимания JMM невозможно писать корректный многопоточный код — программа может работать на одной JVM/архитектуре и ломаться на другой.

JMM — это компромисс между двумя целями:
- **Корректность** — программист должен иметь предсказуемые гарантии
- **Производительность** — компилятор и CPU должны иметь свободу для оптимизаций (реордеринг, кэширование)

## Полезные ссылки

### Официальная документация
- [JLS §17.4 Memory Model](https://docs.oracle.com/javase/specs/jls/se17/html/jls-17.html#jls-17.4) — формальная спецификация
- [JSR-133 FAQ (Brian Goetz)](https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html) — объяснение от автора спецификации
- [JSR-133 Cookbook for Compiler Writers (Doug Lea)](https://gee.cs.oswego.edu/dl/jmm/cookbook.html) — реализация барьеров
- [JDK 9 Memory Order Modes (Doug Lea)](https://gee.cs.oswego.edu/dl/html/j9mm.html) — `VarHandle` и режимы памяти

### Рекомендуемая литература
- [Java Concurrency in Practice — Brian Goetz](https://jcip.net/) — библия многопоточности в Java
- [Close Encounters of The JMM Kind (Shipilev)](https://shipilev.net/blog/2016/close-encounters-of-jmm-kind/) — практические кейсы от инженера JVM
- [All Fields Are Final (Shipilev)](https://shipilev.net/blog/2014/all-fields-are-final/) — глубокий разбор `final` семантики

### См. также
- [[java-concurrency-basics]] — основы многопоточности, `ExecutorService`, синхронизаторы
- [[java-concurrency-advanced]] — `ReentrantLock`, `StampedLock`, `CompletableFuture`, `Fork/Join`

## Содержание

- [Три проблемы многопоточности](#три-проблемы-многопоточности)
- [Модель памяти: абстракция](#модель-памяти-абстракция)
- [Happens-Before](#happens-before)
  - [Полный список правил happens-before](#полный-список-правил-happens-before)
  - [Транзитивность и цепочки happens-before](#транзитивность-и-цепочки-happens-before)
  - [Чего happens-before НЕ означает](#чего-happens-before-не-означает)
- [volatile](#volatile)
  - [Гарантии volatile](#гарантии-volatile)
  - [Чего volatile НЕ гарантирует](#чего-volatile-не-гарантирует)
  - [volatile как механизм публикации](#volatile-как-механизм-публикации)
  - [Типичное применение volatile](#типичное-применение-volatile)
- [synchronized и мониторы](#synchronized-и-мониторы)
  - [Гарантии synchronized](#гарантии-synchronized)
  - [Intrinsic lock (monitor)](#intrinsic-lock-monitor)
  - [Важные нюансы synchronized](#важные-нюансы-synchronized)
- [java.util.concurrent.locks и JMM](#javautilconcurrentlocks-и-jmm)
  - [Гарантии Lock](#гарантии-lock)
  - [ReentrantLock vs synchronized — гарантии памяти](#reentrantlock-vs-synchronized--гарантии-памяти)
  - [ReadWriteLock и видимость](#readwritelock-и-видимость)
  - [StampedLock и оптимистичное чтение](#stampedlock-и-оптимистичное-чтение)
- [final поля — семантика безопасной публикации](#final-поля--семантика-безопасной-публикации)
  - [Freeze action](#freeze-action)
  - [Dereference chain](#dereference-chain)
  - [Ограничения final-семантики](#ограничения-final-семантики)
  - [final и immutable-объекты](#final-и-immutable-объекты)
- [Атомарность операций](#атомарность-операций)
  - [Атомарные чтение и запись](#атомарные-чтение-и-запись)
  - [Atomic-классы и CAS](#atomic-классы-и-cas)
- [Переупорядочивание (reordering)](#переупорядочивание-reordering)
  - [Кто переупорядочивает](#кто-переупорядочивает)
  - [Правила переупорядочивания](#правила-переупорядочивания)
  - [Пример опасного реордеринга](#пример-опасного-реордеринга)
- [Memory Barriers (барьеры памяти)](#memory-barriers-барьеры-памяти)
  - [Четыре типа барьеров](#четыре-типа-барьеров)
  - [Какие барьеры вставляет JVM](#какие-барьеры-вставляет-jvm)
  - [Реализация на x86 и ARM](#реализация-на-x86-и-arm)
- [VarHandle и режимы памяти (Java 9+)](#varhandle-и-режимы-памяти-java-9)
  - [Четыре режима доступа](#четыре-режима-доступа)
  - [Сравнительная таблица режимов](#сравнительная-таблица-режимов)
  - [Когда использовать какой режим](#когда-использовать-какой-режим)
- [Double-Checked Locking](#double-checked-locking)
- [Safe Publication (безопасная публикация)](#safe-publication-безопасная-публикация)
- [Практические паттерны](#практические-паттерны)
- [Типичные ошибки](#типичные-ошибки)
- [Лучшие практики](#лучшие-практики)

## Три проблемы многопоточности

| Проблема | Суть | Механизмы JMM |
|----------|------|----------------|
| **Видимость** (visibility) | Поток A записал значение, поток B видит старое | `volatile`, `synchronized`, `final`, `Lock` |
| **Атомарность** (atomicity) | Операция не завершилась целиком, другой поток видит промежуточное состояние | `synchronized`, `Lock`, `Atomic*` классы |
| **Упорядоченность** (ordering) | Компилятор/CPU переставляют инструкции | happens-before правила, memory barriers |

Без явной синхронизации JVM **не обязана** показывать записи одного потока другому.

```java
// ГОНКА: поток B может навсегда зациклиться
// JIT может закэшировать running в регистре CPU
class Broken {
    boolean running = true; // НЕ volatile!

    void stop() { running = false; }       // Поток A
    void run()  { while (running) { } }    // Поток B — может не увидеть false
}
```

## Модель памяти: абстракция

JMM оперирует абстрактной моделью, а не реальным железом:

```text
┌─────────────┐     ┌─────────────┐
│   Thread 1  │     │   Thread 2  │
│ ┌─────────┐ │     │ ┌─────────┐ │
│ │ Working  │ │     │ │ Working  │ │
│ │ Memory   │ │     │ │ Memory   │ │
│ └────┬─────┘ │     │ └────┬─────┘ │
└──────┼───────┘     └──────┼───────┘
       │                    │
       ▼                    ▼
┌──────────────────────────────────┐
│         Main Memory              │
│  (heap: поля объектов, статики)  │
└──────────────────────────────────┘
```

- **Working Memory** — абстракция кэшей, регистров, store buffers CPU
- **Main Memory** — абстракция общей памяти (heap)
- JMM определяет, **когда** записи из working memory становятся видны в main memory (и наоборот)
- Без happens-before поток может **бесконечно** читать устаревшее значение из своего рабочего кэша

## Happens-Before

**Happens-before (hb)** — ключевое отношение JMM. Если действие A happens-before действия B, то:
1. Все записи, выполненные действием A (и всеми предшествующими действиями), **гарантированно видны** в B
2. Действие A **упорядочено** перед B

### Полный список правил happens-before

| # | Правило | Описание |
|---|---------|----------|
| 1 | **Program order** | В рамках одного потока каждое действие hb следующего по порядку в исходном коде |
| 2 | **Monitor lock** | `unlock()` монитора hb последующего `lock()` **того же** монитора |
| 3 | **volatile** | Запись в `volatile`-переменную hb последующего чтения **той же** переменной |
| 4 | **Thread.start()** | Вызов `thread.start()` hb любого действия в запущенном потоке |
| 5 | **Thread.join()** | Любое действие в потоке hb успешного возврата из `thread.join()` |
| 6 | **Thread.interrupt()** | Вызов `thread.interrupt()` hb обнаружения прерывания (через `InterruptedException` или `Thread.interrupted()`) |
| 7 | **Finalizer** | Конец конструктора объекта hb начала `finalize()` этого объекта |
| 8 | **Транзитивность** | Если A hb B и B hb C, то A hb C |
| 9 | **Default values** | Запись значения по умолчанию (`0`, `null`, `false`) hb первого действия в любом потоке |
| 10 | **Concurrent utilities** | Действия в коллекциях `j.u.c` (submit в Executor hb начала task; завершение task hb `Future.get()`) |

**Правило 10 детально** — happens-before гарантии `java.util.concurrent`:

| Операция A | hb | Операция B |
|------------|----|------------|
| `executor.submit(task)` | → | Начало выполнения `task` |
| Завершение `Callable/Runnable` | → | `future.get()` возвращает результат |
| `countDownLatch.countDown()` | → | `countDownLatch.await()` возвращает управление |
| `semaphore.release()` | → | `semaphore.acquire()` успешно завершается |
| `phaser.arrive()` / `arriveAndDeregister()` | → | `phaser.awaitAdvance()` возвращает управление |
| Действия в потоке перед помещением в `BlockingQueue` | → | Действия после извлечения из `BlockingQueue` в другом потоке |
| Действия перед `CyclicBarrier.await()` | → | Действия после `await()` в других потоках (барьерный action) |

### Транзитивность и цепочки happens-before

Транзитивность — самый мощный инструмент JMM. Записав значение в `volatile`-переменную, вы «публикуете» **все** предшествующие записи:

```java
class TransitivityExample {
    int x = 0;                  // обычное поле
    int y = 0;                  // обычное поле
    volatile boolean ready;     // volatile флаг

    // Поток A
    void writer() {
        x = 1;            // (1)
        y = 2;            // (2)
        ready = true;     // (3) volatile write — «публикует» (1) и (2)
    }

    // Поток B
    void reader() {
        if (ready) {      // (4) volatile read — видит (3)
            // Гарантировано: x == 1, y == 2
            // Цепочка: (1) hb (2) hb (3) hb (4) → транзитивно (1) hb (4)
            System.out.println(x + y); // всегда 3
        }
    }
}
```

Это работает потому что:
1. (1) hb (3) — program order в потоке A
2. (3) hb (4) — volatile write hb volatile read
3. Транзитивно: (1) hb (4) — **все** записи до volatile write видны после volatile read

### Чего happens-before НЕ означает

- **Не означает хронологический порядок.** Если A hb B, это не значит, что A физически выполнится раньше B на CPU. JVM может переставить их, если итоговый результат эквивалентен
- **Без hb нет гарантий.** Если между двумя действиями в разных потоках нет цепочки hb, JMM не гарантирует ничего — это **data race**
- **hb — не запрет реордеринга.** Это гарантия видимости. Реордеринг разрешён, пока не нарушает hb-гарантии

## volatile

### Гарантии volatile

`volatile` — самый лёгкий механизм синхронизации в JMM. Обеспечивает:

1. **Видимость** — запись в `volatile` немедленно сбрасывается из рабочей памяти потока в main memory; чтение всегда идёт из main memory
2. **Happens-before** — запись hb последующего чтения той же переменной (правило 3)
3. **Запрет реордеринга** — компилятор и CPU не переставляют операции через `volatile` read/write (подробности в разделе [Memory Barriers](#memory-barriers-барьеры-памяти))
4. **64-bit атомарность** — чтение/запись `volatile long` и `volatile double` атомарны (без `volatile` — не гарантируется!)

### Чего volatile НЕ гарантирует

**Атомарность составных операций (read-modify-write):**

```java
volatile int counter = 0;

// НЕ потокобезопасно! Это три операции: read → modify → write
counter++;  // Два потока могут прочитать одно значение и оба записать +1

// Решения:
// 1. AtomicInteger (lock-free CAS)
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();

// 2. synchronized
synchronized (lock) { counter++; }

// 3. LongAdder (для high-contention счётчиков, Java 8+)
LongAdder adder = new LongAdder();
adder.increment();
```

**Check-then-act тоже не атомарен:**

```java
volatile boolean available = false;

// ГОНКА: между проверкой и действием другой поток может изменить значение
if (!available) {
    available = true;  // два потока могут оба войти сюда
}
```

### volatile как механизм публикации

`volatile` — основной механизм **piggybacking**: через `volatile` write/read можно «протащить» видимость обычных переменных:

```java
class VolatilePublisher {
    private int a, b, c;       // обычные поля
    private volatile int flag;  // volatile «забор»

    // Поток-писатель
    void publish() {
        a = 1;       // обычная запись
        b = 2;       // обычная запись
        c = 3;       // обычная запись
        flag = 1;    // volatile write — «публикует» a, b, c
    }

    // Поток-читатель
    void consume() {
        if (flag == 1) {      // volatile read — «подтягивает» a, b, c
            // a == 1, b == 2, c == 3 — гарантировано
        }
    }
}
```

### Типичное применение volatile

```java
// 1. Флаг остановки потока
private volatile boolean shutdown = false;

public void stop() { shutdown = true; }
public void run() { while (!shutdown) { doWork(); } }

// 2. Одноразовая публикация immutable-объекта
private volatile Config config;

public void updateConfig(Config newConfig) {
    config = newConfig;  // безопасно, если Config immutable
}

// 3. Состояние конечного автомата (enum)
private volatile State state = State.IDLE;

// 4. Double-checked locking (обязателен volatile!)
private static volatile Singleton instance;

// 5. Индикатор прогресса (один писатель, много читателей)
private volatile int progress = 0;
```

## synchronized и мониторы

### Гарантии synchronized

`synchronized` — самый строгий встроенный механизм синхронизации. Обеспечивает **все три** гарантии:

1. **Взаимное исключение (mutex)** — только один поток выполняет блок в каждый момент времени
2. **Видимость** — при выходе из `synchronized` (unlock) все записи сбрасываются в main memory; при входе (lock) рабочая память инвалидируется
3. **Happens-before** — `unlock` hb последующего `lock` **того же** монитора

```java
class SafeCounter {
    private int count = 0;

    synchronized void increment() {
        count++; // атомарно + видимо другим потокам после unlock
    }

    synchronized int get() {
        return count; // видит все предыдущие increment()
    }
}
```

### Intrinsic lock (monitor)

Каждый объект в Java имеет встроенный монитор (intrinsic lock):

```java
// Монитор текущего экземпляра
synchronized (this) { ... }

// Монитор объекта Class (для статических методов)
synchronized (MyClass.class) { ... }

// Монитор произвольного объекта (рекомендуемый подход)
private final Object lock = new Object();
synchronized (lock) { ... }

// Метод-уровень: эквивалент synchronized(this)
synchronized void method() { ... }
// ↔ void method() { synchronized(this) { ... } }

// Статический метод: эквивалент synchronized(MyClass.class)
static synchronized void staticMethod() { ... }
```

### Важные нюансы synchronized

**Реентерабельность (reentrancy):**

```java
class Reentrant {
    synchronized void outer() {
        inner(); // НЕ deadlock — тот же поток уже владеет монитором
    }
    synchronized void inner() { ... }
}
```

**Happens-before работает только для одного монитора:**

```java
// ГОНКА: lockA и lockB — разные мониторы, hb не связаны!
synchronized (lockA) { x = 1; }   // Поток A
synchronized (lockB) { print(x); } // Поток B — может увидеть 0
```

**synchronized не гарантирует порядок захвата** — потоки могут получать монитор в любом порядке (нет fairness).

## java.util.concurrent.locks и JMM

### Гарантии Lock

Все реализации `java.util.concurrent.locks.Lock` **обязаны** предоставлять те же гарантии памяти, что и `synchronized`:

> *«All Lock implementations must enforce the same memory synchronization semantics as provided by the built-in monitor lock»* — Javadoc `Lock`

Это означает:
- `lock.unlock()` hb последующего `lock.lock()` того же `Lock`-объекта
- Все записи до `unlock()` видны потоку, вызвавшему `lock()`

### ReentrantLock vs synchronized — гарантии памяти

| Аспект | `synchronized` | `ReentrantLock` |
|--------|---------------|-----------------|
| Happens-before | unlock hb lock | unlock hb lock |
| Видимость | Полная (flush + invalidate) | Полная (flush + invalidate) |
| Атомарность | Да | Да |
| Fairness | Нет (по умолчанию) | Настраивается (`new ReentrantLock(true)`) |
| Interruptible lock | Нет | `lockInterruptibly()` |
| Try-lock | Нет | `tryLock()` / `tryLock(timeout)` |
| Multiple conditions | Только один `wait/notify` | Несколько `Condition` объектов |

```java
class LockExample {
    private final ReentrantLock lock = new ReentrantLock();
    private int sharedState = 0;

    void update() {
        lock.lock();
        try {
            sharedState++;   // видимо после unlock
        } finally {
            lock.unlock();   // hb последующего lock() в другом потоке
        }
    }
}
```

### ReadWriteLock и видимость

`ReadWriteLock` разделяет чтение и запись:

```java
private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
private Map<String, String> cache = new HashMap<>();

// Запись — эксклюзивный доступ
void put(String key, String value) {
    rwLock.writeLock().lock();
    try {
        cache.put(key, value);
    } finally {
        rwLock.writeLock().unlock();  // hb последующего readLock/writeLock
    }
}

// Чтение — разделяемый доступ
String get(String key) {
    rwLock.readLock().lock();
    try {
        return cache.get(key);  // видит все записи до writeLock.unlock()
    } finally {
        rwLock.readLock().unlock();
    }
}
```

Гарантии памяти `ReadWriteLock`:
- `writeLock.unlock()` hb последующего `readLock.lock()` или `writeLock.lock()`
- `readLock.unlock()` hb последующего `writeLock.lock()`
- Между двумя `readLock` нет hb (они параллельны, и это нормально)

### StampedLock и оптимистичное чтение

`StampedLock` (Java 8+) добавляет оптимистичное чтение **без** блокировки:

```java
private final StampedLock sl = new StampedLock();
private double x, y;

// Оптимистичное чтение — без барьеров памяти!
double distanceFromOrigin() {
    long stamp = sl.tryOptimisticRead(); // не блокирует
    double currentX = x, currentY = y;  // могут быть неконсистентны
    if (!sl.validate(stamp)) {          // проверяем: была ли запись?
        // Была запись — откатываемся к пессимистичному чтению
        stamp = sl.readLock();
        try {
            currentX = x;
            currentY = y;
        } finally {
            sl.unlockRead(stamp);
        }
    }
    return Math.sqrt(currentX * currentX + currentY * currentY);
}
```

**Важно:** `tryOptimisticRead()` **не создаёт** happens-before. Гарантии появляются только после успешного `validate()` или при эскалации к `readLock()`.

## final поля — семантика безопасной публикации

### Freeze action

JMM определяет специальное действие — **freeze** — для `final` полей:

1. **Freeze action** происходит в момент завершения конструктора, в котором записано `final` поле
2. Если объект **корректно опубликован** (ссылка не утекла из конструктора), все `final` поля гарантированно видны другим потокам **без дополнительной синхронизации**

```java
class ImmutablePoint {
    final int x;
    final int y;

    ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
        // ← freeze action здесь (конец конструктора)
    }
}

// Любой поток может безопасно читать x и y без synchronized/volatile
ImmutablePoint p = sharedRef;
System.out.println(p.x); // гарантированно видит значение из конструктора
```

На уровне реализации JVM вставляет **StoreStore** барьер между записью `final` поля и присвоением ссылки на объект.

### Dereference chain

Гарантия `final` полей работает через **dereference chain** (цепочку разыменования):

```java
class FinalExample {
    final int[] data;

    FinalExample() {
        data = new int[]{1, 2, 3};
        // Гарантия: другой поток увидит data != null
        // И содержимое массива {1, 2, 3}
        // Потому что массив достижим через final-поле (dereference chain)
    }
}
```

Для гарантии необходима непрерывная цепочка: `объект → final поле → вложенный объект → ...`. Если цепочка разрывается (например, ссылка на вложенный объект получена не через `final` поле), гарантия теряется.

### Ограничения final-семантики

**1. Ссылка не должна утекать из конструктора:**

```java
class Broken {
    final int value;

    Broken() {
        // БАГ: this утекает до завершения конструктора
        EventBus.register(this);  // другой поток может увидеть value == 0
        value = 42;               // freeze ещё не произошёл!
    }
}
```

**2. Мутабельное содержимое НЕ защищено:**

```java
class Holder {
    final List<String> items;

    Holder() {
        items = new ArrayList<>();
        items.add("initial");
        // freeze гарантирует: items != null, items.get(0) == "initial"
    }
}

// НО: items.add("later") из другого потока — data race!
// final защищает ССЫЛКУ и начальное состояние, не последующие мутации
```

**3. Запись через reflection ломает гарантии:**

```java
// Технически возможно, но ломает final-семантику JMM
Field f = String.class.getDeclaredField("value");
f.setAccessible(true);
f.set(str, newValue); // Undefined behavior с точки зрения JMM
```

### final и immutable-объекты

Комбинация `final` полей + отсутствие мутаций = **потокобезопасность без синхронизации**:

```java
// Java record (16+) — все поля final по определению
record Config(String host, int port, Duration timeout) {}

// Безопасная публикация через обычную (не volatile) ссылку — 
// но только если ссылка записана в конструкторе другого объекта
// или через volatile/synchronized
```

**Важно:** `final` поля + корректная публикация — это самый дешёвый способ обеспечить потокобезопасность. Нет барьеров на стороне читателя, только StoreStore при конструировании.

## Атомарность операций

### Атомарные чтение и запись

JLS §17.7 гарантирует атомарность чтения/записи для:

| Тип | Атомарен без volatile? | С volatile? |
|-----|----------------------|-------------|
| `int`, `char`, `short`, `byte`, `boolean` | Да | Да |
| Ссылки (references) | Да | Да |
| `float` | Да | Да |
| `long` | **Нет** (два 32-bit слова) | Да |
| `double` | **Нет** (два 32-bit слова) | Да |

```java
// ГОНКА: поток может прочитать «половину» long (word tearing)
long sharedLong = 0; // НЕ volatile
// Поток A: sharedLong = 0xFFFFFFFF_00000000L;
// Поток B может увидеть: 0xFFFFFFFF_FFFFFFFFL
//   (верхние 32 бита от A, нижние от предыдущего значения)

// Исправление:
volatile long sharedLong = 0; // атомарное 64-bit чтение/запись
```

**Примечание:** на современных 64-битных JVM word tearing для `long`/`double` практически не встречается, но **спецификация его не запрещает**. Всегда используйте `volatile` или `Atomic*` для разделяемых `long`/`double`.

### Atomic-классы и CAS

`java.util.concurrent.atomic` предоставляет атомарные операции на основе CAS (Compare-And-Swap):

```java
AtomicInteger counter = new AtomicInteger(0);

// Все операции атомарны + обеспечивают volatile-семантику
counter.incrementAndGet();       // атомарный ++counter
counter.compareAndSet(0, 1);     // CAS: если == 0, установить 1
counter.getAndUpdate(x -> x * 2); // атомарная функция

// LongAdder — для high-contention счётчиков (Java 8+)
// Внутри: массив ячеек, каждый поток пишет в свою → нет contention
LongAdder adder = new LongAdder();
adder.increment(); // быстрее AtomicLong при множестве потоков
adder.sum();       // итоговое значение (eventual consistency)
```

Happens-before для `Atomic*`: успешная запись (включая CAS) hb последующего чтения того же `Atomic*` объекта. Это следует из того, что `Atomic*` внутри использует `volatile` семантику.

## Переупорядочивание (reordering)

### Кто переупорядочивает

Реордеринг происходит на нескольких уровнях:

1. **javac** — компилятор Java → байткод (минимальный реордеринг)
2. **JIT (C1/C2/Graal)** — байткод → машинный код (основной источник: инлайнинг, loop unrolling, dead code elimination)
3. **CPU** — out-of-order execution, store buffers, speculative execution
4. **Кэш** — иерархия L1/L2/L3 + cache coherence protocols (MESI)

### Правила переупорядочивания

JMM определяет, какие пары операций **можно** переставить:

| 1-я операция ↓ / 2-я операция → | Normal Load/Store | Volatile Load / MonitorEnter | Volatile Store / MonitorExit |
|----------------------------------|:-:|:-:|:-:|
| **Normal Load/Store** | ✅ | ❌ | ❌ |
| **Volatile Load / MonitorEnter** | ❌ | ❌ | ❌ |
| **Volatile Store / MonitorExit** | ✅ | ❌ | ❌ |

Где ✅ = реордеринг разрешён, ❌ = запрещён.

**Ключевое:** volatile load и monitor enter запрещают реордеринг **всех** последующих операций. Volatile store и monitor exit запрещают реордеринг **всех** предшествующих операций.

### Пример опасного реордеринга

```java
class Reordering {
    int a = 0;
    boolean flag = false; // НЕ volatile

    // Поток A
    void writer() {
        a = 1;          // (1) — может быть переставлено ПОСЛЕ (2)!
        flag = true;    // (2)
    }

    // Поток B
    void reader() {
        if (flag) {     // может увидеть true...
            assert a == 1; // ...но a может быть ещё 0!
        }
    }
}

// Исправление: volatile запрещает реордеринг (1) и (2)
volatile boolean flag = false;
```

**Ещё один классический пример — IRIW (Independent Reads of Independent Writes):**

```java
// Начальные значения: x = 0, y = 0
// Поток 1: x = 1;
// Поток 2: y = 1;
// Поток 3: r1 = x; r2 = y;  // может увидеть r1=1, r2=0
// Поток 4: r3 = y; r4 = x;  // может увидеть r3=1, r4=0
// Без volatile оба результата возможны одновременно!
// Это значит: потоки 3 и 4 видят записи x,y в разном порядке
```

## Memory Barriers (барьеры памяти)

Memory barriers (memory fences) — инструкции процессора, ограничивающие переупорядочивание. JVM вставляет их для обеспечения гарантий JMM.

### Четыре типа барьеров

| Барьер | Гарантия |
|--------|----------|
| **LoadLoad** | Все загрузки до барьера завершатся до любой загрузки после барьера |
| **StoreStore** | Все записи до барьера станут видны (flush) до любой записи после барьера |
| **LoadStore** | Все загрузки до барьера завершатся до любой записи после барьера |
| **StoreLoad** | Все записи до барьера станут видны до любой загрузки после барьера. **Самый дорогой** барьер — единственный, который требует сброса store buffer |

### Какие барьеры вставляет JVM

**Volatile read:**

```text
[volatile read]
LoadLoad    ← запрещает реордеринг последующих загрузок до volatile read
LoadStore   ← запрещает реордеринг последующих записей до volatile read
```

**Volatile write:**

```text
StoreStore  ← запрещает реордеринг предшествующих записей после volatile write
[volatile write]
StoreLoad   ← запрещает реордеринг последующих загрузок до volatile write
```

**Monitor enter (synchronized вход):**

```text
[lock acquire]
LoadLoad    ← аналогично volatile read
LoadStore
```

**Monitor exit (synchronized выход):**

```text
StoreStore  ← аналогично volatile write
LoadStore
[lock release]
StoreLoad
```

**final field write (в конструкторе):**

```text
[write to final field]
StoreStore  ← гарантирует видимость final поля до публикации ссылки
```

### Реализация на x86 и ARM

| Барьер | x86 (TSO) | ARM |
|--------|-----------|-----|
| LoadLoad | **no-op** (TSO гарантирует) | `dmb ishld` |
| StoreStore | **no-op** (TSO гарантирует) | `dmb ishst` |
| LoadStore | **no-op** (TSO гарантирует) | `dmb ish` |
| StoreLoad | `mfence` или `lock addl $0,(%rsp)` | `dmb ish` |

**x86 (Total Store Order):** единственный разрешённый реордеринг — StoreLoad (запись может быть задержана в store buffer, а следующая загрузка произойдёт раньше). Поэтому только `StoreLoad` требует явного барьера. Это делает `volatile` на x86 дешёвым для чтения и умеренно дорогим для записи.

**ARM (Weakly Ordered):** разрешены все виды реордеринга. Каждый барьер требует реальной инструкции `dmb` (Data Memory Barrier). `volatile` на ARM значительно дороже, чем на x86.

## VarHandle и режимы памяти (Java 9+)

`VarHandle` (Java 9+) предоставляет гранулярный контроль над режимами доступа к памяти, расширяя бинарный выбор «обычный доступ vs volatile».

### Четыре режима доступа

**1. Plain (обычный доступ)**
- Аналог обычного чтения/записи поля
- Никаких межпоточных гарантий упорядоченности
- `long`/`double` не гарантированно атомарны
- Методы: `get()`, `set()`

**2. Opaque (непрозрачный)**
- Гарантия: **per-variable coherence** — записи в одну переменную видны в порядке их выполнения
- Гарантия: записи **eventually visible** — рано или поздно станут видны другим потокам
- Гарантия: **атомарность** для всех типов (включая `long`/`double`)
- **Нет** межпеременного упорядочивания
- Методы: `getOpaque()`, `setOpaque()`

**3. Release/Acquire (освобождение/захват)**
- Всё от Opaque +
- **Causal ordering**: все записи до `setRelease()` видны потоку, вызвавшему `getAcquire()` для той же переменной
- Аналог C++ `memory_order_release` / `memory_order_acquire`
- Методы: `getAcquire()`, `setRelease()`, `compareAndExchangeAcquire()`, `compareAndExchangeRelease()`

**4. Volatile (полный порядок)**
- Всё от Release/Acquire +
- **Total order**: все volatile-операции над всеми переменными упорядочены глобально
- Аналог C++ `memory_order_seq_cst`
- Методы: `getVolatile()`, `setVolatile()`, `compareAndSet()`, `getAndAdd()`

### Сравнительная таблица режимов

| Свойство | Plain | Opaque | Release/Acquire | Volatile |
|----------|:-----:|:------:|:---------------:|:--------:|
| Атомарность `long`/`double` | ❌ | ✅ | ✅ | ✅ |
| Per-variable coherence | ❌ | ✅ | ✅ | ✅ |
| Eventual visibility | ❌ | ✅ | ✅ | ✅ |
| Causal ordering (межпеременное) | ❌ | ❌ | ✅ | ✅ |
| Total order (все volatile) | ❌ | ❌ | ❌ | ✅ |
| Стоимость на x86 | — | — | ≈ no-op | `mfence` |
| Стоимость на ARM | — | `dmb` | `dmb` | `dmb` |

### Когда использовать какой режим

| Задача | Режим |
|--------|-------|
| Индикатор прогресса (один писатель) | Opaque |
| Флаг cancellation | Release/Acquire |
| Публикация данных (producer → consumer) | Release/Acquire |
| Координация нескольких потоков (Dekker, Peterson) | Volatile |
| Счётчики статистики (eventual accuracy) | Opaque |
| Lock-free структуры данных | Volatile (CAS) |

```java
// Пример: VarHandle с Release/Acquire для producer-consumer
class RingBuffer {
    private final Object[] buffer;
    private static final VarHandle HEAD;
    private static final VarHandle TAIL;
    // ... static initializer ...

    void produce(Object item) {
        int t = (int) TAIL.getOpaque(this);
        buffer[t % buffer.length] = item;
        TAIL.setRelease(this, t + 1);  // публикует item
    }

    Object consume() {
        int h = (int) HEAD.getOpaque(this);
        int t = (int) TAIL.getAcquire(this);  // видит item
        if (h < t) {
            Object item = buffer[h % buffer.length];
            HEAD.setRelease(this, h + 1);
            return item;
        }
        return null;
    }
}
```

## Double-Checked Locking

Классический паттерн ленивой инициализации — корректен **только** с `volatile`:

```java
class Singleton {
    // volatile ОБЯЗАТЕЛЕН — без него другой поток может увидеть
    // частично сконструированный объект
    private static volatile Singleton instance;

    static Singleton getInstance() {
        if (instance == null) {                    // 1-я проверка (без lock)
            synchronized (Singleton.class) {
                if (instance == null) {            // 2-я проверка (с lock)
                    instance = new Singleton();    // без volatile: может быть reorder
                }
            }
        }
        return instance;
    }
}
```

**Почему без volatile сломано?** Инструкция `instance = new Singleton()` — это три шага:
1. Выделить память (`allocate`)
2. Вызвать конструктор (`<init>`)
3. Присвоить ссылку в `instance`

Без `volatile` CPU/JIT может переставить (2) и (3). Другой поток увидит ненулевую ссылку на объект с незавершённым конструктором.

С `volatile` запись в `instance` — это volatile store. StoreStore барьер перед ней гарантирует, что конструктор завершён до публикации ссылки.

**Альтернативы без volatile:**

```java
// 1. Holder idiom (initialization-on-demand) — рекомендуемый подход
class Singleton {
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }
    static Singleton getInstance() {
        return Holder.INSTANCE; // класс загрузится лениво, потокобезопасно
    }
}

// 2. enum singleton
enum Singleton {
    INSTANCE;
    // JVM гарантирует единственность и потокобезопасную инициализацию
}
```

## Safe Publication (безопасная публикация)

Безопасная публикация объекта — передача ссылки так, чтобы другой поток видел полностью сконструированный объект.

**Способы безопасной публикации:**

| Способ | Гарантия |
|--------|----------|
| `volatile` ссылка | Видимость + ordering |
| Через `synchronized` / `Lock` | Видимость + ordering + mutex |
| `final` поле (корректная конструкция) | Видимость final-графа без синхронизации |
| `AtomicReference` | volatile-семантика |
| Помещение в concurrent-коллекцию (`ConcurrentHashMap`, `BlockingQueue`) | Гарантии j.u.c |
| `static final` (инициализация класса) | JVM гарантирует потокобезопасность |

**Небезопасная публикация:**

```java
// ГОНКА: другой поток может увидеть holder.value == 0
class Holder {
    int value;
    Holder(int v) { this.value = v; }
}

// Обычное присвоение — НЕ безопасная публикация
Holder holder; // не volatile, не final
holder = new Holder(42); // поток B может увидеть holder != null, но value == 0
```

## Практические паттерны

| Паттерн | Механизм | Когда применять |
|---------|----------|-----------------|
| Флаг остановки | `volatile boolean` | Сигнализация между потоками |
| Счётчик | `AtomicInteger` / `LongAdder` | Конкурентные инкременты |
| Безопасная публикация | `volatile` ссылка или `final` поле | Передача объекта между потоками |
| Мьютекс | `synchronized` / `ReentrantLock` | Защита составных операций |
| Immutable object | `final` поля + корректная публикация | Разделяемое состояние без синхронизации |
| Copy-on-write | `volatile` + клонирование | Редкие записи, частые чтения |
| Producer-consumer | `Release/Acquire` через `VarHandle` | Lock-free очереди |
| Lazy init | Holder idiom или DCL с volatile | Отложенное создание тяжёлых объектов |
| Spin-wait | `Thread.onSpinWait()` (Java 9+) + volatile read | Busy-wait с подсказкой CPU |

## Типичные ошибки

**1. Отсутствие синхронизации — «у меня работает»**

```java
// Код может работать годами и сломаться после обновления JVM,
// смены CPU (x86 → ARM), или включения агрессивных оптимизаций JIT
int shared = 0; // без volatile
// Поток A: shared = 42;
// Поток B: print(shared); // может напечатать 0 или 42
```

**2. synchronized на разных мониторах**

```java
// ГОНКА: два разных монитора не создают happens-before
synchronized (lockA) { x = 1; }   // Поток A
synchronized (lockB) { print(x); } // Поток B — может не увидеть 1
```

**3. Утечка this из конструктора**

```java
class Broken {
    final int value;
    Broken() {
        EventBus.register(this); // this утекает — final-гарантия сломана
        value = 42;
    }
}
```

**4. volatile для составных операций**

```java
volatile Map<String, String> map = new HashMap<>();
// Потокобезопасно: map = newMap; (замена ссылки)
// НЕ потокобезопасно: map.put("key", "value"); (мутация содержимого)
```

**5. Полагаться на атомарность записи long без volatile**

```java
long timestamp = 0; // без volatile
// Поток A: timestamp = System.nanoTime(); // 64-bit запись — не атомарна по JMM
// Поток B: может прочитать «мусор» (half-write)
```

**6. Забыть volatile в DCL**

```java
// СЛОМАНО: без volatile instance может быть не-null, но не сконструирован
private static Singleton instance; // забыли volatile!
```

**7. Использовать synchronized(new Object()) или synchronized на мутабельной ссылке**

```java
// БЕСПОЛЕЗНО: каждый вызов — новый монитор
synchronized (new Object()) { ... }

// ГОНКА: ссылка может измениться, потоки захватят разные мониторы
synchronized (mutableRef) { ... }
```

## Лучшие практики

1. **Используйте высокоуровневые абстракции** — `java.util.concurrent` (`ConcurrentHashMap`, `BlockingQueue`, `ExecutorService`) вместо ручной синхронизации на `volatile`/`synchronized`

2. **Prefer immutability** — immutable-объекты потокобезопасны по определению. Java 16+ `record` — идеальный вариант

3. **Минимизируйте shared mutable state** — чем меньше разделяемого мутабельного состояния, тем проще доказать корректность

4. **Документируйте thread-safety** — `@ThreadSafe`, `@NotThreadSafe`, `@GuardedBy("lock")` (JCIP Annotations)

5. **Не полагайтесь на тесты** — гонки не воспроизводимы детерминированно. Используйте формальное рассуждение о happens-before + инструменты: jcstress, ThreadSanitizer

6. **Один lock — одна группа данных** — не защищайте несвязанные данные одним монитором (ложная конкуренция), но и не используйте разные мониторы для связанных данных

7. **Выбирайте минимально достаточный уровень синхронизации:**
   - Immutable + `final` → нет синхронизации
   - Один флаг/ссылка → `volatile`
   - Атомарный счётчик → `AtomicInteger` / `LongAdder`
   - Составные операции → `synchronized` / `Lock`
   - High-performance → `VarHandle` с Release/Acquire

8. **Используйте `private final Object lock = new Object()`** вместо `synchronized(this)` — защита от случайного захвата монитора извне

9. **Всегда освобождайте Lock в finally** — иначе deadlock при исключении:

```java
lock.lock();
try {
    // ...
} finally {
    lock.unlock(); // ВСЕГДА в finally
}
```

10. **Тестируйте на слабых моделях памяти** — баги, невидимые на x86, проявляются на ARM (Apple Silicon, серверы на Graviton). Используйте [jcstress](https://github.com/openjdk/jcstress) для стресс-тестирования JMM-контрактов
