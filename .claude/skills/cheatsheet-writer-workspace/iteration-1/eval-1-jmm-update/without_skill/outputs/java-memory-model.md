---
title: "Java Memory Model (JMM)"
description: "Полная шпаргалка по Java Memory Model: happens-before, volatile, synchronized, Lock, final fields semantics, memory barriers, атомарность, видимость, упорядоченность. Практические примеры гонок и их исправления."
tags: ["languages", "java", "jmm", "memory-model", "concurrency", "happens-before", "memory-barriers", "volatile", "synchronized"]
difficulty: "advanced"
prerequisites: ["java-concurrency-basics.md"]
next: ["java-concurrency-advanced.md"]
updated: "2026-04-11"
---

# Java Memory Model (JMM)

JMM (JSR-133, Java 5+) определяет правила видимости записей в память между потоками. Без понимания JMM невозможно писать корректный многопоточный код.

## Полезные ссылки

- [JLS §17.4 Memory Model](https://docs.oracle.com/javase/specs/jls/se17/html/jls-17.html#jls-17.4)
- [JSR-133 FAQ (Brian Goetz)](https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html)
- [Java Concurrency in Practice — Brian Goetz](https://jcip.net/)
- [The JSR-133 Cookbook for Compiler Writers (Doug Lea)](https://gee.cs.oswego.edu/dl/jmm/cookbook.html)
- [Close Encounters of The Java Memory Model Kind (Shipilёv)](https://shipilev.net/blog/2016/close-encounters-of-jmm-kind/)

## Содержание

- [Три проблемы многопоточности](#три-проблемы-многопоточности)
- [Аппаратные предпосылки: почему нужна JMM](#аппаратные-предпосылки-почему-нужна-jmm)
- [Happens-Before](#happens-before)
  - [Правила happens-before](#правила-happens-before)
  - [Транзитивность](#транзитивность)
  - [Happens-before и причинность](#happens-before-и-причинность)
- [volatile](#volatile)
  - [Что гарантирует volatile](#что-гарантирует-volatile)
  - [Чего volatile НЕ гарантирует](#чего-volatile-не-гарантирует)
  - [Типичное применение volatile](#типичное-применение-volatile)
  - [volatile и memory barriers](#volatile-и-memory-barriers)
- [synchronized](#synchronized)
  - [Гарантии synchronized](#гарантии-synchronized)
  - [Intrinsic lock (monitor)](#intrinsic-lock-monitor)
  - [Memory-эффекты synchronized](#memory-эффекты-synchronized)
- [java.util.concurrent.locks — ReentrantLock и другие](#javautilconcurrentlocks--reentrantlock-и-другие)
  - [JMM-гарантии Lock](#jmm-гарантии-lock)
  - [ReadWriteLock и память](#readwritelock-и-память)
  - [StampedLock — оптимистичное чтение](#stampedlock--оптимистичное-чтение)
- [final поля — полная семантика](#final-поля--полная-семантика)
  - [Гарантии final fields](#гарантии-final-fields)
  - [Freeze action и store-store barrier](#freeze-action-и-store-store-barrier)
  - [Ограничения final](#ограничения-final)
  - [final и сериализация](#final-и-сериализация)
- [Memory Barriers (барьеры памяти)](#memory-barriers-барьеры-памяти)
  - [Типы барьеров](#типы-барьеров)
  - [Маппинг JMM-конструкций на барьеры](#маппинг-jmm-конструкций-на-барьеры)
  - [VarHandle и явные fence-операции](#varhandle-и-явные-fence-операции)
- [Атомарные операции](#атомарные-операции)
- [Переупорядочивание (reordering)](#переупорядочивание-reordering)
  - [Какие реордеринги разрешены](#какие-реордеринги-разрешены)
  - [Пример опасного реордеринга](#пример-опасного-реордеринга)
- [JMM-гарантии java.util.concurrent](#jmm-гарантии-javautilconcurrent)
- [Double-Checked Locking](#double-checked-locking)
- [Безопасная публикация объектов](#безопасная-публикация-объектов)
- [Практические паттерны](#практические-паттерны)
- [Типичные ошибки](#типичные-ошибки)
- [Лучшие практики](#лучшие-практики)

## Три проблемы многопоточности

| Проблема | Суть | Решение в JMM |
|----------|------|----------------|
| **Видимость** (visibility) | Поток A записал значение, поток B видит старое | `volatile`, `synchronized`, `final`, `Lock` |
| **Атомарность** (atomicity) | Операция не завершилась целиком, другой поток видит промежуточное состояние | `synchronized`, `Lock`, `Atomic*` классы |
| **Упорядоченность** (ordering) | Компилятор/CPU переставляют инструкции | happens-before правила, memory barriers |

Без явной синхронизации JVM **не обязана** показывать записи одного потока другому.

```java
// ГОНКА: поток B может навсегда зациклиться (JIT может закэшировать running в регистре)
class Broken {
    boolean running = true; // НЕ volatile!

    void stop() { running = false; }       // Поток A
    void run()  { while (running) { } }    // Поток B — может не увидеть false
}
```

## Аппаратные предпосылки: почему нужна JMM

Современные CPU имеют многоуровневую иерархию памяти, которая создает проблемы видимости:

```
┌─────────┐   ┌─────────┐   ┌─────────┐
│  CPU 0  │   │  CPU 1  │   │  CPU 2  │
│┌───────┐│   │┌───────┐│   │┌───────┐│
││  L1   ││   ││  L1   ││   ││  L1   ││
│└───┬───┘│   │└───┬───┘│   │└───┬───┘│
│┌───┴───┐│   │┌───┴───┐│   │┌───┴───┐│
││  L2   ││   ││  L2   ││   ││  L2   ││
│└───┬───┘│   │└───┬───┘│   │└───┬───┘│
└────┼────┘   └────┼────┘   └────┼────┘
     └──────┬──────┴──────┬──────┘
        ┌───┴───┐     ┌───┴───┐
        │  L3 (shared) │   │ Store │
        │              │   │ Buffer│
        └──────┬───────┘   └───┬───┘
           ┌───┴───────────────┴───┐
           │     Main Memory       │
           └───────────────────────┘
```

**Три источника переупорядочивания:**

1. **Компилятор (javac / JIT C2)** — переставляет инструкции для лучшей загрузки pipeline
2. **Store buffer** — запись сначала попадает в локальный буфер CPU, а не в кэш/память
3. **Инвалидация кэша** — протокол MESI/MOESI может задерживать обновление кэш-линий

JMM абстрагирует эти аппаратные детали в единый набор правил, работающий на **любой** архитектуре (x86, ARM, POWER, RISC-V).

> **x86 vs ARM**: x86 имеет относительно строгую модель памяти (TSO — Total Store Order), поэтому многие баги не проявляются. ARM/POWER имеют слабую модель — баги, незаметные на x86, ломают код на ARM. JMM защищает от этого.

## Happens-Before

**Happens-before** — ключевое отношение JMM. Если действие A happens-before действия B, то все записи A **гарантированно видны** в B.

Важно: happens-before — это **не** про реальный порядок выполнения во времени. Это про **гарантии видимости**. Два действия могут физически выполниться в любом порядке, но если между ними есть happens-before, результат записи будет виден.

### Правила happens-before

| # | Правило | Пояснение |
|---|---------|-----------|
| 1 | **Program order** | В рамках одного потока каждое действие hb следующего |
| 2 | **Monitor lock** | `unlock()` монитора hb последующего `lock()` **того же** монитора |
| 3 | **volatile** | Запись в volatile-переменную hb последующего чтения **той же** переменной |
| 4 | **Thread.start()** | Вызов `t.start()` hb любого действия в потоке `t` |
| 5 | **Thread.join()** | Любое действие в потоке `t` hb возврата из `t.join()` |
| 6 | **Thread.interrupt()** | Вызов `t.interrupt()` hb обнаружения прерывания в `t` |
| 7 | **Finalizer** | Конец конструктора hb начала `finalize()` |
| 8 | **Транзитивность** | Если A hb B и B hb C, то A hb C |
| 9 | **Default values** | Запись значения по умолчанию (0, null, false) hb первого действия в любом потоке |
| 10 | **`Lock.unlock()`** | `lock.unlock()` hb последующего `lock.lock()` **того же** lock-объекта |
| 11 | **`Executor`** | Отправка `Runnable` в `Executor` hb начала его выполнения |
| 12 | **`Future`** | Действия в задаче hb возврата из `Future.get()` в другом потоке |
| 13 | **`CountDownLatch`** | `countDown()` hb возврата из `await()` |
| 14 | **`Phaser/CyclicBarrier`** | Действия до `arrive/await` hb действий после `await` в другом потоке |
| 15 | **`ConcurrentMap.put`** | `put(k, v)` hb успешного `get(k)`, возвращающего `v` |

### Транзитивность

Транзитивность — мощный инструмент. Записав флаг в volatile, вы "публикуете" все предшествующие записи:

```java
class TransitivityExample {
    int data;             // обычное поле
    volatile boolean ready; // volatile флаг

    // Поток A
    void writer() {
        data = 42;        // (1)
        ready = true;     // (2) volatile write — публикует (1)
    }

    // Поток B
    void reader() {
        if (ready) {      // (3) volatile read
            // data гарантированно == 42
            // потому что (1) hb (2) [program order]
            //            (2) hb (3) [volatile]
            //            (1) hb (3) [транзитивность]
            System.out.println(data);
        }
    }
}
```

### Happens-before и причинность

Happens-before **не означает**, что A физически произошло раньше B. Это гарантия видимости, а не временного порядка.

```java
// Допустимый результат: r1 == 0, r2 == 0
// Несмотря на то, что по "здравому смыслу" хотя бы один должен увидеть 1
int x = 0, y = 0; // обычные переменные

// Поток A            // Поток B
x = 1;               y = 1;
r1 = y;              r2 = x;

// Нет happens-before между потоками → JMM разрешает r1 == 0 && r2 == 0
```

Это пример так называемого **causality loop** — JMM допускает результаты, которые кажутся нарушающими причинность, потому что без синхронизации нет гарантий порядка.

## volatile

### Что гарантирует volatile

1. **Видимость** — запись немедленно видна всем потокам
2. **Запрет реордеринга** — компилятор и CPU не переставляют операции через volatile read/write
3. **64-bit атомарность** — чтение/запись `volatile long` и `volatile double` атомарны (без volatile — нет!)

### Чего volatile НЕ гарантирует

**Атомарность составных операций:**

```java
volatile int counter = 0;

// НЕ потокобезопасно! read-modify-write — три операции
counter++;  // read counter → increment → write counter

// Решение: AtomicInteger или synchronized
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet(); // атомарный CAS
```

### Типичное применение volatile

```java
// 1. Флаг остановки потока
volatile boolean shutdown = false;

// 2. Одноразовая публикация immutable-объекта
volatile Config config;

// 3. Double-checked locking (вместе с synchronized)
volatile Singleton instance;

// 4. Публикация результата ленивой инициализации
volatile List<String> cachedResult;
```

### volatile и memory barriers

volatile-операции транслируются в конкретные memory barriers:

| Операция | Барьеры | Эффект |
|----------|---------|--------|
| **volatile read** | После: LoadLoad + LoadStore | Никакие последующие чтения/записи не будут переупорядочены до volatile read |
| **volatile write** | До: StoreStore + LoadStore. После: StoreLoad | Никакие предшествующие чтения/записи не будут переупорядочены после volatile write |

```
volatile read v
─── LoadLoad ───    // все последующие load-ы увидят v и всё до записи в v
─── LoadStore ───   // все последующие store-ы будут после volatile read
... обычные операции ...

... обычные операции ...
─── StoreStore ───  // все предыдущие store-ы завершатся до volatile write
─── LoadStore ───   // все предыдущие load-ы завершатся до volatile write
volatile write v
─── StoreLoad ───   // запись в v станет видна до любых последующих load-ов
```

> **StoreLoad** — самый дорогой барьер. На x86 транслируется в `MFENCE` или `lock addl $0, (%rsp)`. Именно поэтому volatile write дороже volatile read.

## synchronized

### Гарантии synchronized

1. **Атомарность** — только один поток выполняет блок
2. **Видимость** — при выходе из synchronized все записи сбрасываются в main memory; при входе — кэш инвалидируется
3. **Happens-before** — unlock hb последующего lock того же монитора
4. **Запрет реордеринга** — операции не "вытекают" из synchronized-блока (хотя могут "втекать" внутрь)

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

Каждый объект в Java имеет встроенный монитор:

```java
synchronized (this)          { }  // монитор текущего объекта
synchronized (MyClass.class) { }  // монитор объекта Class
synchronized (lockObject)    { }  // монитор произвольного объекта

// Метод-уровень эквивалентен:
synchronized void method() { }
// ↔ void method() { synchronized(this) { ... } }

// static synchronized — монитор объекта Class:
static synchronized void method() { }
// ↔ static void method() { synchronized(MyClass.class) { ... } }
```

### Memory-эффекты synchronized

```
monitorenter (lock acquire)
─── LoadLoad ───
─── LoadStore ───
    // все чтения внутри блока видят актуальные данные
    // (нельзя поднять чтения ПЕРЕД monitorenter)

    ... тело synchronized-блока ...

─── StoreStore ───
─── LoadStore ───
monitorexit (lock release)
─── StoreLoad ───     // все записи становятся видны
    // (нельзя опустить записи ПОСЛЕ monitorexit)
```

**Важно**: операции могут "втекать" внутрь synchronized-блока (roach motel ordering), но не могут "вытекать" наружу.

```java
int a = x;            // (1) может быть перенесено ВНУТРЬ блока
synchronized (lock) {
    int b = y;        // (2)
    z = 3;            // (3)
}
int c = w;            // (4) НЕ может быть перенесено внутрь блока
// Компилятор вправе выполнить (1) после monitorenter,
// но (2) и (3) никогда не окажутся за пределами блока
```

## java.util.concurrent.locks — ReentrantLock и другие

### JMM-гарантии Lock

`java.util.concurrent.locks.Lock` предоставляет **те же** JMM-гарантии, что и `synchronized`:

```java
class SafeCounterWithLock {
    private final Lock lock = new ReentrantLock();
    private int count = 0;

    void increment() {
        lock.lock();       // ≡ monitorenter — acquire barrier
        try {
            count++;
        } finally {
            lock.unlock(); // ≡ monitorexit — release barrier
        }
    }

    int get() {
        lock.lock();
        try {
            return count;  // видит все предыдущие increment()
        } finally {
            lock.unlock();
        }
    }
}
```

**Правило happens-before для Lock:** `lock.unlock()` happens-before последующего `lock.lock()` того же lock-объекта. Это аналогично правилу для monitor lock.

**Дополнительные возможности Lock по сравнению с synchronized:**

| Возможность | synchronized | ReentrantLock |
|-------------|:---:|:---:|
| Автоматическое освобождение | Да | Нет (finally!) |
| tryLock (неблокирующий захват) | Нет | Да |
| lockInterruptibly | Нет | Да |
| Fairness (честная очередь) | Нет | Да |
| Condition (несколько условий) | Нет (один wait/notify) | Да |
| JMM-гарантии | Одинаковые | Одинаковые |

### ReadWriteLock и память

`ReentrantReadWriteLock` предоставляет раздельные read/write-блокировки с JMM-гарантиями:

```java
class CachedData {
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private Object data;
    private volatile boolean cacheValid;

    void update(Object newData) {
        rwl.writeLock().lock();    // write-acquire
        try {
            data = newData;
            cacheValid = true;
        } finally {
            rwl.writeLock().unlock(); // write-release → hb read-acquire
        }
    }

    Object read() {
        rwl.readLock().lock();     // read-acquire: видит всё, что было до write-release
        try {
            return data;           // гарантированно видит актуальные данные
        } finally {
            rwl.readLock().unlock();
        }
    }
}
```

**Правила happens-before для ReadWriteLock:**
- `writeLock().unlock()` hb последующего `readLock().lock()` или `writeLock().lock()`
- `readLock().unlock()` hb последующего `writeLock().lock()`
- Множественные `readLock().lock()` могут выполняться параллельно — между ними нет happens-before

### StampedLock — оптимистичное чтение

`StampedLock` (Java 8+) предлагает оптимистичное чтение без блокировки:

```java
class Point {
    private final StampedLock sl = new StampedLock();
    private double x, y;

    void move(double deltaX, double deltaY) {
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    double distanceFromOrigin() {
        // Оптимистичное чтение — НЕ блокирует
        long stamp = sl.tryOptimisticRead();
        double currentX = x, currentY = y;

        // Проверяем, не было ли записи с момента получения stamp
        if (!sl.validate(stamp)) {
            // Кто-то писал — откат к пессимистичному чтению
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
}
```

**JMM-нюанс**: `tryOptimisticRead()` **не создает happens-before**. Данные, прочитанные между `tryOptimisticRead()` и `validate()`, могут быть неконсистентны. Только успешный `validate()` гарантирует, что прочитанные значения были консистентны. Если `validate()` вернул `false`, данные нужно перечитать под блокировкой.

## final поля — полная семантика

### Гарантии final fields

`final` поля имеют специальную семантику в JMM, обеспечивающую потокобезопасность immutable-объектов **без синхронизации**:

```java
class ImmutablePoint {
    final int x;
    final int y;

    ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
        // НЕЛЬЗЯ: передавать this куда-то до завершения конструктора
    }
}

// Безопасно читать из любого потока без synchronized/volatile
ImmutablePoint p = sharedRef; // если sharedRef корректно опубликован
System.out.println(p.x); // гарантированно видит значение из конструктора
```

### Freeze action и store-store barrier

При завершении конструктора объекта с `final`-полями JMM выполняет **freeze action**:

```
┌──────────────────────────────────────────┐
│             Конструктор                  │
│  this.x = 10;    // запись final-поля    │
│  this.y = 20;    // запись final-поля    │
│                                          │
│  ═══ freeze action ═══                   │
│  (StoreStore barrier перед публикацией    │
│   ссылки на объект)                      │
└──────────────────────────────────────────┘
│
▼  Только после freeze ссылка на объект
   может стать видна другим потокам

ref = new ImmutablePoint(10, 20);
// Любой поток, видящий ref != null,
// гарантированно видит x == 10, y == 20
```

Freeze гарантирует, что:
1. Записи в `final`-поля завершаются **до** публикации ссылки на объект
2. Другие потоки, видящие ссылку, видят полностью инициализированные `final`-поля
3. Гарантия распространяется на весь **граф объектов**, достижимый через `final`-поля в момент завершения конструктора

### Ограничения final

**1. Гарантия только для графа, достижимого при завершении конструктора:**

```java
class FinalFieldExample {
    final List<String> items;

    FinalFieldExample() {
        items = new ArrayList<>();
        items.add("a"); // видимо — добавлено ДО завершения конструктора
    }

    // ПОСЛЕ конструктора:
    // items.add("b"); — НЕ защищено гарантиями final!
    // Другие потоки могут не увидеть "b"
}
```

**2. Утечка this из конструктора ломает гарантии:**

```java
class Broken {
    final int value;

    Broken() {
        // БАГ: this утекает ДО завершения конструктора
        global = this;  // другой поток может прочитать value == 0!
        value = 42;
    }
}
```

**3. Рефлексия может нарушить гарантии:**

```java
// Изменение final-поля через рефлексию:
Field f = String.class.getDeclaredField("value");
f.setAccessible(true);
f.set(someString, newValue);
// JMM НЕ гарантирует видимость этого изменения другим потокам!
// Формально — undefined behavior с точки зрения JMM
```

### final и сериализация

При десериализации (обычная Java-сериализация) конструктор **не вызывается**. Это означает, что freeze action не происходит, и гарантии `final`-полей **не действуют** для десериализованных объектов без дополнительной синхронизации.

```java
// Безопасно: readResolve() публикует объект корректно
class SafeSingleton implements Serializable {
    private final String name;

    private Object readResolve() {
        return INSTANCE; // INSTANCE создан через конструктор — гарантии работают
    }
}
```

## Memory Barriers (барьеры памяти)

Memory barriers (memory fences) — инструкции процессора, которые ограничивают переупорядочивание операций с памятью. JMM использует их для реализации happens-before.

### Типы барьеров

| Барьер | Запрещает реордеринг | Пояснение |
|--------|---------------------|-----------|
| **LoadLoad** | Load₁; LoadLoad; Load₂ | Load₂ не будет выполнен до завершения Load₁ |
| **StoreStore** | Store₁; StoreStore; Store₂ | Store₂ не будет выполнен до того, как Store₁ станет видим |
| **LoadStore** | Load₁; LoadStore; Store₂ | Store₂ не будет выполнен до завершения Load₁ |
| **StoreLoad** | Store₁; StoreLoad; Load₂ | Load₂ не увидит значение до того, как Store₁ станет виден всем CPU |

**StoreLoad** — самый "тяжелый" барьер, на x86 это единственный, который требует явной инструкции (`MFENCE` / `lock`-префикс). Остальные барьеры на x86 выполняются автоматически благодаря TSO.

### Маппинг JMM-конструкций на барьеры

| JMM-конструкция | Генерируемые барьеры (абстрактно) | Реализация на x86 |
|-----------------|-----------------------------------|--------------------|
| **volatile read** | LoadLoad + LoadStore (после) | Нет инструкции (TSO достаточно) |
| **volatile write** | StoreStore + LoadStore (до) + StoreLoad (после) | `MFENCE` или `lock addl` |
| **monitor enter** | LoadLoad + LoadStore (после) | `lock cmpxchg` (CAS) |
| **monitor exit** | StoreStore + LoadStore (до) + StoreLoad (после) | `lock` на xchg/cmpxchg |
| **final field** | StoreStore (после записи, до публикации) | Нет инструкции на x86 |
| **CAS (compareAndSet)** | Full fence | `lock cmpxchg` |

> На **ARM** и **POWER** почти все барьеры требуют явных инструкций (`dmb`, `dsb`, `lwsync`, `sync`), поэтому синхронизация на этих архитектурах дороже.

### VarHandle и явные fence-операции

Java 9+ предоставляет `VarHandle` с разными режимами доступа к памяти и явными fence-операциями:

```java
import java.lang.invoke.VarHandle;

// Явные fence-операции
VarHandle.fullFence();          // StoreLoad + LoadLoad + StoreStore + LoadStore
VarHandle.acquireFence();       // LoadLoad + LoadStore (как volatile read)
VarHandle.releaseFence();       // StoreStore + LoadStore (как volatile write без StoreLoad)
VarHandle.loadLoadFence();      // LoadLoad
VarHandle.storeStoreFence();    // StoreStore
```

**Режимы доступа VarHandle:**

| Режим | Гарантии | Аналог |
|-------|----------|--------|
| **plain** | Никаких | Обычное поле |
| **opaque** | Атомарность + progress guarantee, без ordering | — |
| **acquire/release** | acquire-чтение / release-запись | volatile read / volatile write без StoreLoad |
| **volatile** | Полные volatile-гарантии | `volatile` поле |

```java
// Пример: acquire/release — дешевле volatile, но достаточно для многих паттернов
class AcquireReleaseExample {
    private int data;
    private static final VarHandle READY;
    private boolean ready;

    static {
        try {
            READY = MethodHandles.lookup()
                .findVarHandle(AcquireReleaseExample.class, "ready", boolean.class);
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }

    void writer() {
        data = 42;
        READY.setRelease(this, true); // release-запись: StoreStore + LoadStore
    }

    void reader() {
        if ((boolean) READY.getAcquire(this)) { // acquire-чтение: LoadLoad + LoadStore
            assert data == 42; // гарантированно
        }
    }
}
```

## Атомарные операции

Без volatile/synchronized атомарны только:
- Чтение/запись `int`, `char`, `byte`, `short`, `boolean`, ссылок
- НЕ атомарны: `long`, `double` (64 бита — два 32-битных слова)

```java
// ГОНКА: поток может прочитать «половину» long
long sharedLong = 0; // НЕ volatile
// Поток A: sharedLong = 0xFFFFFFFF_00000000L;
// Поток B может увидеть: 0xFFFFFFFF_FFFFFFFFL (верхние биты от A, нижние от старого значения)

// Исправление:
volatile long sharedLong = 0; // атомарное 64-bit чтение/запись
```

**Классы Atomic* и их JMM-гарантии:**

```java
// AtomicInteger — volatile read/write + CAS
AtomicInteger ai = new AtomicInteger(0);
ai.get();                // ≡ volatile read
ai.set(42);              // ≡ volatile write
ai.compareAndSet(0, 1);  // ≡ volatile read + volatile write (full fence)
ai.getAndIncrement();    // ≡ CAS в цикле (full fence)

// lazySet — только release-семантика (дешевле set)
ai.lazySet(42);          // ≡ release write (StoreStore, без StoreLoad)
// Использовать когда видимость может быть отложена
// (например, обнуление ссылки в конкурентной очереди)

// Java 9+ VarHandle-based методы
ai.getAcquire();         // acquire read
ai.setRelease(42);       // release write
ai.getOpaque();          // opaque read (атомарность без ordering)
ai.setOpaque(42);        // opaque write
```

## Переупорядочивание (reordering)

JIT-компилятор и CPU могут менять порядок инструкций для оптимизации, если это не меняет результат **в рамках одного потока** (as-if-serial semantics).

### Какие реордеринги разрешены

Таблица разрешенных переупорядочиваний (без синхронизации):

| | Normal Load | Normal Store | volatile Load | volatile Store | monitor enter | monitor exit |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| **Normal Load** | Да | Да | Да | **Нет** | Да | **Нет** |
| **Normal Store** | Да | Да | Да | **Нет** | Да | **Нет** |
| **volatile Load** | **Нет** | **Нет** | **Нет** | **Нет** | **Нет** | **Нет** |
| **volatile Store** | Да | Да | **Нет** | **Нет** | **Нет** | **Нет** |
| **monitor enter** | **Нет** | **Нет** | **Нет** | **Нет** | **Нет** | **Нет** |
| **monitor exit** | Да | Да | **Нет** | **Нет** | Да | **Нет** |

"Нет" = переупорядочивание запрещено. Строка — первая операция, столбец — вторая.

### Пример опасного реордеринга

```java
class Reordering {
    int a = 0;
    boolean flag = false; // НЕ volatile

    // Поток A
    void writer() {
        a = 1;          // (1)
        flag = true;    // (2) — может быть переставлено ДО (1)!
    }

    // Поток B
    void reader() {
        if (flag) {     // может увидеть true...
            assert a == 1; // ...но a может быть ещё 0!
        }
    }
}
```

**Исправление:** сделать `flag` volatile — запретит реордеринг (1) и (2).

## JMM-гарантии java.util.concurrent

Классы из `j.u.c` имеют строго определенные happens-before-гарантии:

| Класс / операция | Happens-before правило |
|-----------------|----------------------|
| **ConcurrentHashMap** | `put(k,v)` hb `get(k)`, возвращающего `v` |
| **BlockingQueue** | `put()/offer()` hb `take()/poll()`, возвращающего элемент |
| **Exchanger** | `exchange()` в потоке A hb `exchange()` в потоке B (взаимно) |
| **CountDownLatch** | `countDown()` hb `await()`, возвращающего true |
| **Semaphore** | `release()` hb `acquire()` |
| **CyclicBarrier** | действия до `await()` hb действий после `await()` |
| **Phaser** | `arrive*()` hb `awaitAdvance()` |
| **ExecutorService** | `submit(task)` hb начала выполнения `task` |
| **Future** | действия в задаче hb `get()` |
| **CompletableFuture** | `complete(v)` hb `thenApply/get()` |
| **Collections.synchronized*()** | по правилу monitor lock для внутреннего mutex |

## Double-Checked Locking

Классический паттерн — корректен **только** с volatile:

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
3. Присвоить ссылку в `instance` (`astore`)

CPU может переставить (2) и (3): другой поток увидит не-null ссылку на не до конца сконструированный объект. Volatile write после конструктора вставляет StoreStore-барьер, гарантируя что конструктор завершится до публикации ссылки.

**Альтернатива без volatile:** holder idiom (ленивая инициализация через вложенный класс):

```java
class Singleton {
    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }

    static Singleton getInstance() {
        return Holder.INSTANCE; // класс загрузится лениво, потокобезопасно
    }
}
```

> Holder idiom работает благодаря гарантиям class loading: JVM гарантирует, что статическая инициализация класса выполняется ровно один раз и полностью до первого использования.

## Безопасная публикация объектов

Публикация — это передача ссылки на объект от одного потока к другому. Безопасная публикация гарантирует, что объект виден в полностью сконструированном состоянии.

**Способы безопасной публикации:**

| Способ | Когда использовать |
|--------|-------------------|
| `static final` поле (статический инициализатор) | Синглтоны, константы |
| `volatile` ссылка | Объект может меняться |
| Через `final` поле (если объект immutable) | Неизменяемые объекты |
| Через `synchronized` / `Lock` | Мутабельные объекты |
| Через `AtomicReference` | Lock-free публикация |
| Через `j.u.c` коллекцию | ConcurrentHashMap, BlockingQueue и т.д. |

**Небезопасная публикация:**

```java
// ОПАСНО: другой поток может увидеть holder != null,
// но holder.value == 0 (не 42)
class UnsafePublication {
    Holder holder;

    void publish() {
        holder = new Holder(42); // без volatile/synchronized
    }
}
```

**Безопасные варианты:**

```java
// Вариант 1: volatile
volatile Holder holder;
void publish() { holder = new Holder(42); }

// Вариант 2: final (если Holder immutable с final-полями)
class Holder { final int value; Holder(int v) { value = v; } }
// Любая публикация ссылки безопасна

// Вариант 3: через j.u.c
ConcurrentHashMap<String, Holder> map = new ConcurrentHashMap<>();
map.put("key", new Holder(42)); // hb для get("key")
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
| Read-write lock | `ReentrantReadWriteLock` | Много читателей, редкие писатели |
| Оптимистичное чтение | `StampedLock` | Очень частые чтения, минимум записей |
| Acquire-release | `VarHandle` acquire/release | Дешевле volatile, достаточно для publish-subscribe |
| Lazy init holder | Вложенный static класс | Ленивый thread-safe синглтон без volatile |

## Типичные ошибки

**1. Отсутствие синхронизации — "у меня работает"**
```java
// Код может работать годами и сломаться после обновления JVM или на другом CPU
int shared = 0; // без volatile
// Поток A: shared = 42;
// Поток B: print(shared); // может напечатать 0 или 42 — зависит от фазы луны
```

**2. synchronized на разных мониторах**
```java
// ГОНКА: два разных объекта-монитора не создают happens-before между собой
synchronized (lockA) { x = 1; }   // Поток A
synchronized (lockB) { print(x); } // Поток B — может не увидеть 1
```

**3. Утечка this из конструктора**
```java
class Broken {
    final int value;

    Broken() {
        // БАГ: регистрация до завершения конструктора
        EventBus.register(this); // другой поток может увидеть value == 0
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

**5. Чтение volatile один раз — проверка + использование**
```java
// БАГ: между двумя чтениями volatile значение может измениться
volatile String name;

if (name != null) {          // volatile read #1
    System.out.println(name.length()); // volatile read #2 — может быть null!
}

// Исправление: локальная копия
String local = name;         // volatile read — один раз
if (local != null) {
    System.out.println(local.length()); // читаем из стека — безопасно
}
```

**6. Lock без finally**
```java
// БАГ: при исключении lock никогда не освобождается → deadlock
lock.lock();
doSomething(); // может бросить исключение
lock.unlock(); // никогда не выполнится

// Исправление: ВСЕГДА try-finally
lock.lock();
try {
    doSomething();
} finally {
    lock.unlock();
}
```

**7. Публикация mutable-объекта через final-поле**
```java
class Container {
    final List<String> items;
    Container() {
        items = new ArrayList<>();
        items.add("init"); // защищено final-семантикой
    }
}
// После конструктора:
container.items.add("oops"); // НЕ защищено! Гонка данных
// Решение: Collections.unmodifiableList() или List.of()
```

## Лучшие практики

1. **Используйте высокоуровневые абстракции** — `java.util.concurrent` (ConcurrentHashMap, BlockingQueue, ExecutorService) вместо ручной синхронизации
2. **Предпочитайте immutability** — immutable-объекты потокобезопасны по определению (records в Java 16+, `List.of()`, `Map.of()`)
3. **Минимизируйте разделяемое состояние** — чем меньше shared mutable state, тем проще
4. **Документируйте thread-safety** — `@ThreadSafe`, `@NotThreadSafe`, `@GuardedBy("lock")`
5. **Не полагайтесь на тесты** — гонки не всегда воспроизводимы; используйте формальное рассуждение о happens-before
6. **volatile + immutable object** — самый дешёвый способ безопасной публикации
7. **Один монитор — одна группа данных** — не защищайте разные данные одним lock (ложная конкуренция) и не защищайте одни данные разными lock-ами
8. **Lock.lock() всегда с try-finally** — без исключений
9. **Локальная копия volatile** — прочитать volatile в локальную переменную, если нужно несколько обращений
10. **jcstress для проверки** — используйте [jcstress](https://github.com/openjdk/jcstress) для тестирования JMM-гарантий вашего кода, обычные тесты не находят гонки
11. **Не тестируйте на x86 и деплойте на ARM** — x86 скрывает многие гонки из-за строгой модели памяти (TSO)
