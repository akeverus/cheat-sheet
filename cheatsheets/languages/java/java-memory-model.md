---
title: "Java Memory Model (JMM)"
description: "Шпаргалка по Java Memory Model: happens-before, volatile, synchronized, final, атомарность, видимость, упорядоченность. Практические примеры гонок и их исправления."
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
updated: "2026-04-20"
---

# Java Memory Model (JMM)

JMM (JSR-133, Java 5+) определяет правила видимости записей в память между потоками. Без понимания JMM невозможно писать корректный многопоточный код.

## Полезные ссылки

- [JLS §17.4 Memory Model](https://docs.oracle.com/javase/specs/jls/se17/html/jls-17.html#jls-17.4)
- [JSR-133 FAQ (Brian Goetz)](https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html)
- [Java Concurrency in Practice — Brian Goetz](https://jcip.net/)

## Содержание

- [Три проблемы многопоточности](#три-проблемы-многопоточности)
- [Happens-Before](#happens-before)
  - [Правила happens-before](#правила-happens-before)
  - [Транзитивность](#транзитивность)
- [volatile](#volatile)
  - [Что гарантирует volatile](#что-гарантирует-volatile)
  - [Чего volatile НЕ гарантирует](#чего-volatile-не-гарантирует)
  - [Типичное применение volatile](#типичное-применение-volatile)
- [synchronized](#synchronized)
  - [Гарантии synchronized](#гарантии-synchronized)
  - [Intrinsic lock (monitor)](#intrinsic-lock-monitor)
- [final поля](#final-поля)
- [Атомарные операции](#атомарные-операции)
- [Переупорядочивание (reordering)](#переупорядочивание-reordering)
  - [Пример опасного реордеринга](#пример-опасного-реордеринга)
- [Double-Checked Locking](#double-checked-locking)
- [Практические паттерны](#практические-паттерны)
- [Типичные ошибки](#типичные-ошибки)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также)

## Три проблемы многопоточности

| Проблема | Суть | Решение в JMM |
|----------|------|----------------|
| **Видимость** (visibility) | Поток A записал значение, поток B видит старое | `volatile`, `synchronized`, `final` |
| **Атомарность** (atomicity) | Операция не завершилась целиком, другой поток видит промежуточное состояние | `synchronized`, `Atomic*` классы |
| **Упорядоченность** (ordering) | Компилятор/CPU переставляют инструкции | happens-before правила |

Без явной синхронизации JVM **не обязана** показывать записи одного потока другому.

```java
// ГОНКА: поток B может навсегда зациклиться (JIT может закэшировать running в регистре)
class Broken {
    boolean running = true; // НЕ volatile!

    void stop() { running = false; }       // Поток A
    void run()  { while (running) { } }    // Поток B — может не увидеть false
}
```

## Happens-Before

**Happens-before** — ключевое отношение JMM. Если действие A happens-before действия B, то все записи A **гарантированно видны** в B.

### Правила happens-before

| # | Правило | Пояснение |
|---|---------|-----------|
| 1 | **Program order** | В рамках одного потока каждое действие hb следующего |
| 2 | **Monitor lock** | `unlock()` монитора hb последующего `lock()` того же монитора |
| 3 | **volatile** | Запись в volatile-переменную hb последующего чтения той же переменной |
| 4 | **Thread.start()** | Вызов `t.start()` hb любого действия в потоке `t` |
| 5 | **Thread.join()** | Любое действие в потоке `t` hb возврата из `t.join()` |
| 6 | **Interruption** | Вызов `t.interrupt()` hb обнаружения прерывания в `t` |
| 7 | **Finalizer** | Конец конструктора hb начала `finalize()` |
| 8 | **Транзитивность** | Если A hb B и B hb C, то A hb C |

### Транзитивность

Транзитивность — инструмент. Записав флаг в volatile, вы «публикуете» все предшествующие записи:

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
            // потому что (1) hb (2) hb (3), транзитивно (1) hb (3)
            System.out.println(data);
        }
    }
}
```

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
```

## synchronized

### Гарантии synchronized

1. **Атомарность** — только один поток выполняет блок
2. **Видимость** — при выходе из synchronized все записи сбрасываются в main memory; при входе — кэш инвалидируется
3. **Happens-before** — unlock hb последующего lock того же монитора

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
synchronized (this)        { }  // монитор текущего объекта
synchronized (MyClass.class) { }  // монитор объекта Class
synchronized (lockObject)  { }  // монитор произвольного объекта

// Метод-уровень эквивалентен:
synchronized void method() { }
// ↔ void method() { synchronized(this) { ... } }
```

## final поля

`final` поля имеют специальную семантику в JMM:

- Если объект **корректно опубликован** (ссылка не утекла из конструктора), все `final` поля гарантированно видны другим потокам **без дополнительной синхронизации**.
- Это основа безопасности immutable-объектов.

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

**Ограничение:** гарантия распространяется только на граф объектов, достижимый через `final` поля. Если `final` поле ссылается на мутабельный объект, содержимое этого объекта НЕ защищено.

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

## Переупорядочивание (reordering)

JIT-компилятор и CPU могут менять порядок инструкций для оптимизации, если это не меняет результат **в рамках одного потока** (as-if-serial semantics).

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
1. Выделить память
2. Вызвать конструктор
3. Присвоить ссылку в `instance`

CPU может переставить (2) и (3): другой поток увидит не-null ссылку на не до конца сконструированный объект.

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

## Практические паттерны

| Паттерн | Механизм | Когда применять |
|---------|----------|-----------------|
| Флаг остановки | `volatile boolean` | Сигнализация между потоками |
| Счётчик | `AtomicInteger` / `LongAdder` | Конкурентные инкременты |
| Безопасная публикация | `volatile` ссылка или `final` поле | Передача объекта между потоками |
| Мьютекс | `synchronized` / `ReentrantLock` | Защита составных операций |
| Immutable object | `final` поля + корректная публикация | Разделяемое состояние без синхронизации |
| Copy-on-write | `volatile` + клонирование | Редкие записи, частые чтения |

## Типичные ошибки

**1. Отсутствие синхронизации — «у меня работает»**
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

## Лучшие практики

1. **Используйте высокоуровневые абстракции** — `java.util.concurrent` (ConcurrentHashMap, BlockingQueue, ExecutorService) вместо ручной синхронизации
2. **Prefer immutability** — immutable-объекты потокобезопасны по определению (records в Java 16+)
3. **Минимизируйте разделяемое состояние** — чем меньше shared mutable state, тем проще
4. **Документируйте thread-safety** — `@ThreadSafe`, `@NotThreadSafe`, `@GuardedBy("lock")`
5. **Не полагайтесь на тесты** — гонки не всегда воспроизводимы; используйте формальное рассуждение о happens-before
6. **volatile + immutable object** — самый дешёвый способ безопасной публикации
7. **Один монитор — одна группа данных** — не защищайте разные данные одним lock (ложная конкуренция)

## См. также

- [[java-annotations-reflection|Java Annotations и Reflection]]
- [[java-basics|Java: основы]]
- [[java-collections-converting|Java Collections: конвертирование]]
- [[java-collections-list|Java Collections: List]]
- [[java-collections-map|Java Collections: Map]]
