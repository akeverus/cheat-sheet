---
title: "Java Virtual Threads (Project Loom)"
description: "Шпаргалка по виртуальным потокам Java: API создания, планировщик, structured concurrency, scoped values, отличия от platform threads, pinning, подводные камни и лучшие практики."
tags:
  - languages
  - java
  - virtual-threads
  - project-loom
  - concurrency
  - structured-concurrency
difficulty: "advanced"
prerequisites: ["java-concurrency-basics.md", "java-concurrency-advanced.md"]
next: ["java-memory-model.md"]
updated: "2026-04-11"
related: ["java-reactive-project-reactor.md", "java-io-nio.md"]
---

# Java Virtual Threads (Project Loom)

Virtual threads (виртуальные потоки) — легковесные потоки, управляемые JVM, а не операционной системой. Появились как preview в JDK 19 (JEP 425), стали production-ready в JDK 21 (JEP 444). Являются ключевой частью Project Loom, который также включает structured concurrency и scoped values.

Главная идея: **один поток на одну задачу** (thread-per-request) снова становится жизнеспособной моделью. Виртуальные потоки настолько дёшевы, что можно создавать миллионы одновременно — без пулов, без реактивных фреймворков, без callback hell.

## Полезные ссылки

### Официальная документация
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444) — финальная спецификация virtual threads
- [JEP 491: Synchronize Virtual Threads without Pinning](https://openjdk.org/jeps/491) — устранение pinning в JDK 24
- [JEP 505: Structured Concurrency (Fifth Preview)](https://openjdk.org/jeps/505) — structured concurrency в JDK 25
- [JEP 506: Scoped Values](https://openjdk.org/jeps/506) — scoped values (финализировано в JDK 25)
- [Oracle: Virtual Threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html) — руководство Oracle

### См. также
- [[java-concurrency-basics]] — основы многопоточности, `ExecutorService`, `synchronized`
- [[java-concurrency-advanced]] — `Lock`, `Phaser`, `StampedLock`, fork/join
- [[java-memory-model]] — happens-before, volatile, видимость между потоками
- [[java-reactive-project-reactor]] — реактивный подход (альтернатива virtual threads)
- [[java-io-nio]] — блокирующий и неблокирующий I/O

## Содержание

- [Зачем нужны виртуальные потоки](#зачем-нужны-виртуальные-потоки)
  - [Проблема platform threads](#проблема-platform-threads)
  - [Как виртуальные потоки решают проблему](#как-виртуальные-потоки-решают-проблему)
- [Архитектура: как это работает](#архитектура-как-это-работает)
  - [Carrier threads и планировщик](#carrier-threads-и-планировщик)
  - [Continuation и yield](#continuation-и-yield)
  - [Жизненный цикл виртуального потока](#жизненный-цикл-виртуального-потока)
- [API создания виртуальных потоков](#api-создания-виртуальных-потоков)
  - [Thread.ofVirtual()](#threadofvirtual)
  - [Thread.startVirtualThread()](#threadstartvirtualthread)
  - [Executors.newVirtualThreadPerTaskExecutor()](#executorsnewvirtualthreadpertaskexecutor)
  - [Thread.Builder и фабрики](#threadbuilder-и-фабрики)
- [Отличия от platform threads](#отличия-от-platform-threads)
  - [Сравнительная таблица](#сравнительная-таблица)
  - [Что осталось прежним](#что-осталось-прежним)
  - [Что изменилось](#что-изменилось)
- [Structured Concurrency](#structured-concurrency)
  - [Мотивация](#мотивация)
  - [StructuredTaskScope](#structuredtaskscope)
  - [Joiner: стратегии объединения результатов](#joiner-стратегии-объединения-результатов)
  - [Обработка ошибок](#обработка-ошибок)
  - [Таймауты](#таймауты)
- [Scoped Values](#scoped-values)
  - [Зачем нужны scoped values](#зачем-нужны-scoped-values)
  - [API ScopedValue](#api-scopedvalue)
  - [Наследование в дочерних потоках](#наследование-в-дочерних-потоках)
  - [ScopedValue vs ThreadLocal](#scopedvalue-vs-threadlocal)
- [Pinning: блокировка carrier thread](#pinning-блокировка-carrier-thread)
  - [Что такое pinning](#что-такое-pinning)
  - [Сценарии pinning](#сценарии-pinning)
  - [Диагностика pinning](#диагностика-pinning)
  - [JDK 24: synchronized без pinning](#jdk-24-synchronized-без-pinning)
- [Подводные камни](#подводные-камни)
  - [ThreadLocal и утечки памяти](#threadlocal-и-утечки-памяти)
  - [CPU-bound задачи](#cpu-bound-задачи)
  - [Пулирование виртуальных потоков](#пулирование-виртуальных-потоков)
  - [Synchronized в JDK < 24](#synchronized-в-jdk--24)
  - [Мониторинг и отладка](#мониторинг-и-отладка)
- [Миграция на виртуальные потоки](#миграция-на-виртуальные-потоки)
  - [Пошаговая стратегия](#пошаговая-стратегия)
  - [Spring Boot и виртуальные потоки](#spring-boot-и-виртуальные-потоки)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)

## Зачем нужны виртуальные потоки

### Проблема platform threads

Platform thread (обычный `Thread`) — это тонкая обёртка над потоком ОС. Каждый такой поток:

- Выделяет **~1 MB стека** (по умолчанию)
- Требует **системный вызов** для создания и уничтожения
- Управляется **планировщиком ОС** (context switch ~1-10 мкс)
- Ограничен: типичный сервер выдерживает **тысячи**, но не миллионы потоков

В модели thread-per-request каждый HTTP-запрос занимает поток. Если запрос ждёт ответа от БД 50 мс — поток простаивает, но ресурс занят. При 10 000 одновременных запросов нужно 10 000 потоков, что уже на пределе.

```java
// Классический подход: пул потоков ограничивает throughput
ExecutorService pool = Executors.newFixedThreadPool(200); // потолок — 200 одновременных запросов

for (int i = 0; i < 10_000; i++) {
    pool.submit(() -> {
        String result = httpClient.send(request); // 50 мс ожидания — поток заблокирован
        process(result);
    });
}
// 10 000 задач, но только 200 выполняются параллельно
```

### Как виртуальные потоки решают проблему

Виртуальный поток **не привязан** к потоку ОС. Когда виртуальный поток блокируется на I/O, JVM **отсоединяет** его от carrier thread и назначает carrier другому виртуальному потоку. Блокирующая операция не расходует поток ОС.

```java
// С виртуальными потоками: каждая задача — свой поток, без ограничений пула
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10_000; i++) {
        executor.submit(() -> {
            String result = httpClient.send(request); // поток «паркуется», carrier свободен
            process(result);
        });
    }
}
// 10 000 виртуальных потоков — все работают «одновременно»
```

## Архитектура: как это работает

### Carrier threads и планировщик

Виртуальные потоки выполняются поверх пула **carrier threads** — обычных platform threads. Планировщик — это `ForkJoinPool` в режиме FIFO (по умолчанию, количество carrier threads = количеству ядер CPU).

```text
┌──────────────────────────────────────────────────┐
│                    JVM                            │
│                                                   │
│  Virtual Thread 1  ──┐                            │
│  Virtual Thread 2  ──┼──▶ Carrier Thread 1 (OS)  │
│  Virtual Thread 3  ──┘                            │
│                                                   │
│  Virtual Thread 4  ──┐                            │
│  Virtual Thread 5  ──┼──▶ Carrier Thread 2 (OS)  │
│  Virtual Thread 6  ──┘                            │
│                                                   │
│  ... (миллионы VT) ──▶ ... (десятки carrier)     │
└──────────────────────────────────────────────────┘
```

Настройка планировщика через системные свойства:

| Свойство | По умолчанию | Описание |
|----------|--------------|----------|
| `jdk.virtualThreadScheduler.parallelism` | `Runtime.availableProcessors()` | Количество carrier threads |
| `jdk.virtualThreadScheduler.maxPoolSize` | 256 | Максимум carrier threads |
| `jdk.virtualThreadScheduler.minRunnable` | 1 | Минимум runnable threads перед созданием нового carrier |

### Continuation и yield

Внутренне виртуальный поток — это **continuation** (приостанавливаемое вычисление). Когда виртуальный поток выполняет блокирующую операцию:

1. JVM сохраняет стек виртуального потока в heap
2. Carrier thread освобождается для других виртуальных потоков
3. Когда I/O завершается, виртуальный поток возвращается в очередь планировщика
4. Любой свободный carrier thread подхватывает его и продолжает выполнение

Yield происходит автоматически при вызове:
- `Thread.sleep()`
- `BlockingQueue.take()` / `put()`
- `Socket` I/O (read/write)
- `Lock.lock()` / `Condition.await()`
- `CompletableFuture.get()` / `join()`
- Операции с `java.nio.channels`

### Жизненный цикл виртуального потока

```text
NEW → STARTED → RUNNING → [PARKED ↔ RUNNING] → TERMINATED
                              ↑
                    (блокирующая операция / unpark)
```

Состояния идентичны platform thread (`Thread.State`), но переходы RUNNING → PARKED и обратно происходят **без context switch ОС** — это чисто JVM-операция.

## API создания виртуальных потоков

### Thread.ofVirtual()

Основной builder API для создания виртуальных потоков:

```java
// Создать и запустить
Thread vt = Thread.ofVirtual()
    .name("worker-", 0)    // имя с автоинкрементом: worker-0, worker-1, ...
    .start(() -> {
        System.out.println(Thread.currentThread());
    });
vt.join();

// Создать без запуска
Thread vt = Thread.ofVirtual()
    .name("my-vt")
    .unstarted(() -> doWork());
vt.start();
```

### Thread.startVirtualThread()

Удобный shortcut для быстрого запуска:

```java
// Самый простой способ
Thread vt = Thread.startVirtualThread(() -> {
    System.out.println("Hello from virtual thread!");
});
vt.join();
```

### Executors.newVirtualThreadPerTaskExecutor()

Рекомендуемый подход для серверных приложений — совместим с существующим кодом через `ExecutorService`:

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Каждый submit() создаёт новый виртуальный поток
    List<Future<String>> futures = new ArrayList<>();

    for (String url : urls) {
        futures.add(executor.submit(() -> fetchUrl(url)));
    }

    // Собираем результаты
    for (Future<String> future : futures) {
        String result = future.get(); // блокировка — но виртуальный поток просто паркуется
        process(result);
    }
} // close() ждёт завершения всех задач
```

### Thread.Builder и фабрики

`ThreadFactory` для интеграции с библиотеками:

```java
// ThreadFactory для виртуальных потоков
ThreadFactory factory = Thread.ofVirtual()
    .name("http-handler-", 0)
    .factory();

// Использование с ExecutorService
ExecutorService executor = Executors.newThreadPerTaskExecutor(factory);

// Использование с ScheduledExecutorService (JDK 21+)
// ScheduledExecutorService НЕ поддерживает virtual threads напрямую.
// Workaround: использовать scheduled executor для планирования,
// а выполнение делегировать виртуальным потокам
ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
scheduler.scheduleAtFixedRate(() -> {
    Thread.startVirtualThread(() -> doPeriodicWork());
}, 0, 1, TimeUnit.SECONDS);
```

Проверка типа потока:

```java
Thread current = Thread.currentThread();
boolean isVirtual = current.isVirtual(); // true для виртуальных потоков

// threadId() — уникальный long ID (не совпадает с OS thread ID)
long id = current.threadId();
```

## Отличия от platform threads

### Сравнительная таблица

| Характеристика | Platform Thread | Virtual Thread |
|----------------|-----------------|----------------|
| Управляется | ОС | JVM (ForkJoinPool) |
| Стоимость создания | ~1 мс, ~1 MB стека | ~1 мкс, ~кБ стека (растёт по необходимости) |
| Максимальное количество | Тысячи | Миллионы |
| Стек | Фиксированный (или `-Xss`) | Динамический, хранится в heap |
| Планировщик | ОС (preemptive) | JVM ForkJoinPool (cooperative при I/O) |
| `Thread.sleep()` | Блокирует OS thread | Паркует VT, освобождает carrier |
| `synchronized` (JDK < 24) | Без проблем | Pinning carrier thread |
| `synchronized` (JDK 24+) | Без проблем | Без проблем (JEP 491) |
| `ThreadLocal` | Работает | Работает, но не рекомендуется |
| `Thread.setPriority()` | Работает | Игнорируется |
| `Thread.setDaemon()` | Настраивается | Всегда daemon |
| Thread groups | Поддержка | Помещается в фиктивную группу `VirtualThreads` |
| `stop()`, `suspend()`, `resume()` | Deprecated, но работают | Бросают `UnsupportedOperationException` |

### Что осталось прежним

Виртуальные потоки — это полноценные `Thread`. Всё существующее API потоков работает:

```java
Thread vt = Thread.startVirtualThread(() -> {
    // Thread.currentThread() — работает
    // Thread.sleep() — работает (паркует VT)
    // synchronized — работает (с оговорками до JDK 24)
    // try-catch — работает
    // ThreadLocal — работает (но лучше ScopedValue)
    // InterruptedException — работает
});

vt.join();           // работает
vt.interrupt();      // работает
vt.isAlive();        // работает
vt.getState();       // работает
vt.threadId();       // работает
vt.getName();        // работает
```

### Что изменилось

```java
Thread vt = Thread.startVirtualThread(() -> { /* ... */ });

vt.isDaemon();        // всегда true
vt.getPriority();     // всегда Thread.NORM_PRIORITY (5), setPriority() игнорируется
vt.getThreadGroup();  // фиктивная группа "VirtualThreads"

// Мониторинг через JFR (Java Flight Recorder), а не через ThreadMXBean
// ThreadMXBean по умолчанию не учитывает виртуальные потоки
```

## Structured Concurrency

> **Статус:** preview API. На момент JDK 25 — пятый preview (JEP 505). API может измениться до финализации.

### Мотивация

Без structured concurrency параллельные задачи живут «сами по себе»:

```java
// Проблема: если fetchUser() бросил исключение, fetchOrder() продолжает работать впустую
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
Future<User> userFuture = executor.submit(() -> fetchUser(userId));
Future<Order> orderFuture = executor.submit(() -> fetchOrder(orderId));

User user = userFuture.get();     // если тут исключение...
Order order = orderFuture.get();  // ...эта задача зря работает
```

Structured concurrency связывает время жизни дочерних задач с родительским scope: если родитель отменяется или падает, все дочерние задачи автоматически отменяются.

### StructuredTaskScope

Основной класс API — `StructuredTaskScope`. Начиная с JDK 25, создаётся через статические фабричные методы:

```java
// Базовый паттерн: fork-join
try (var scope = StructuredTaskScope.open()) {
    // fork запускает задачу в новом виртуальном потоке
    StructuredTaskScope.Subtask<User> userTask = scope.fork(() -> fetchUser(userId));
    StructuredTaskScope.Subtask<Order> orderTask = scope.fork(() -> fetchOrder(orderId));

    // join() ждёт завершения ВСЕХ задач
    scope.join();

    // Результаты доступны через get()
    User user = userTask.get();     // бросит исключение, если задача упала
    Order order = orderTask.get();
    return new UserOrder(user, order);
} // close() гарантирует, что все дочерние потоки завершены
```

### Joiner: стратегии объединения результатов

`Joiner` определяет, когда scope завершает ожидание:

```java
// ShutdownOnFailure — при первой ошибке отменить остальные
try (var scope = StructuredTaskScope.open(Joiner.awaitAllSuccessfulOrThrow())) {
    scope.fork(() -> fetchUser(userId));
    scope.fork(() -> fetchOrder(orderId));

    scope.join(); // бросит FailedException, если хотя бы одна задача упала
                  // все остальные задачи будут прерваны
}

// ShutdownOnSuccess — вернуть первый успешный результат, остальные отменить
try (var scope = StructuredTaskScope.open(Joiner.anySuccessfulResultOrThrow())) {
    scope.fork(() -> queryPrimaryDb(query));
    scope.fork(() -> queryReplicaDb(query));

    var result = scope.join(); // вернёт первый успешный результат
}

// AllSuccessful — собрать все результаты
try (var scope = StructuredTaskScope.open(Joiner.allSuccessfulOrThrow())) {
    scope.fork(() -> fetchPrice("USD"));
    scope.fork(() -> fetchPrice("EUR"));
    scope.fork(() -> fetchPrice("GBP"));

    List<Object> results = scope.join(); // список всех результатов
}
```

### Обработка ошибок

```java
try (var scope = StructuredTaskScope.open(Joiner.awaitAllSuccessfulOrThrow())) {
    var userTask = scope.fork(() -> fetchUser(userId));
    var orderTask = scope.fork(() -> fetchOrder(orderId));

    try {
        scope.join();
    } catch (FailedException e) {
        // Одна из задач бросила исключение
        Throwable cause = e.getCause(); // оригинальное исключение
        throw new ServiceException("Failed to fetch data", cause);
    }

    return new UserOrder(userTask.get(), orderTask.get());
}
```

### Таймауты

```java
try (var scope = StructuredTaskScope.open(Joiner.awaitAllSuccessfulOrThrow())) {
    scope.fork(() -> fetchUser(userId));
    scope.fork(() -> fetchOrder(orderId));

    try {
        scope.joinUntil(Instant.now().plusSeconds(5)); // таймаут 5 секунд
    } catch (TimeoutException e) {
        // Не все задачи завершились за 5 секунд
        // scope.close() отменит незавершённые задачи
    }
}
```

## Scoped Values

> **Статус:** финализированы в JDK 25 (JEP 506). До этого — preview.

### Зачем нужны scoped values

`ThreadLocal` имеет проблемы с виртуальными потоками:
- **Мутабельность** — любой код может вызвать `set()` и изменить значение
- **Неограниченное время жизни** — значение живёт, пока жив поток (или пока не вызван `remove()`)
- **Стоимость наследования** — `InheritableThreadLocal` копирует значения при создании дочернего потока; при миллионах виртуальных потоков это дорого
- **Утечки памяти** — забытый `remove()` при пуле потоков приводит к утечкам

`ScopedValue` решает все эти проблемы: значение неизменяемо, имеет ограниченное время жизни (scope) и эффективно наследуется.

### API ScopedValue

```java
// Объявление — всегда static final
private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

// Привязка значения к scope
void handleRequest(Request request) {
    User user = authenticate(request);

    ScopedValue.where(CURRENT_USER, user).run(() -> {
        // Внутри этого блока CURRENT_USER привязан к user
        processRequest(request);
    });
    // За пределами блока CURRENT_USER снова не привязан
}

// Чтение значения
void processRequest(Request request) {
    User user = CURRENT_USER.get(); // возвращает привязанное значение
    // Или безопасно:
    User user = CURRENT_USER.orElse(null);
    boolean bound = CURRENT_USER.isBound();
}

// С возвратом результата
String result = ScopedValue.where(CURRENT_USER, user).call(() -> {
    return computeResult();
});
```

### Наследование в дочерних потоках

`ScopedValue` автоматически наследуется виртуальными потоками, созданными внутри `StructuredTaskScope`:

```java
private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

void handleRequest(Request request) {
    User user = authenticate(request);

    ScopedValue.where(CURRENT_USER, user).run(() -> {
        try (var scope = StructuredTaskScope.open()) {
            // Оба дочерних потока видят CURRENT_USER
            scope.fork(() -> {
                User u = CURRENT_USER.get(); // тот же user
                return fetchOrders(u);
            });
            scope.fork(() -> {
                User u = CURRENT_USER.get(); // тот же user
                return fetchRecommendations(u);
            });
            scope.join();
        }
    });
}
```

### ScopedValue vs ThreadLocal

| Характеристика | `ThreadLocal` | `ScopedValue` |
|----------------|---------------|---------------|
| Мутабельность | `set()` доступен всем | Неизменяемый в пределах scope |
| Время жизни | До `remove()` или смерти потока | Автоматически ограничен блоком `run()`/`call()` |
| Наследование | Копирование (дорого) | Zero-copy через structured concurrency |
| Утечки памяти | Возможны без `remove()` | Исключены по дизайну |
| Перепривязка | `set()` в любой момент | Rebinding через вложенный `where().run()` |
| Производительность | O(1) через `ThreadLocalMap` | O(1) через snapshot наследования |
| Рекомендация | Legacy, пулы platform threads | Виртуальные потоки, structured concurrency |

## Pinning: блокировка carrier thread

### Что такое pinning

**Pinning** — ситуация, когда виртуальный поток не может отсоединиться от carrier thread при блокировке. Carrier thread оказывается заблокирован вместе с виртуальным — теряется главное преимущество virtual threads.

### Сценарии pinning

**До JDK 24:**
1. **`synchronized` блоки и методы** — виртуальный поток, владеющий монитором, пиннит carrier
2. **Native-методы (JNI)** — вызов native-кода пиннит carrier
3. **Foreign Function & Memory API** — вызов foreign-функций пиннит carrier

**JDK 24+ (JEP 491):**
1. ~~`synchronized`~~ — **больше не вызывает pinning**
2. **Native-методы (JNI)** — всё ещё пиннят
3. **Foreign Function & Memory API** — всё ещё пиннят

### Диагностика pinning

```bash
# JVM-флаг для обнаружения pinning (JDK 21-23)
-Djdk.tracePinnedThreads=full   # полный стектрейс при pinning
-Djdk.tracePinnedThreads=short  # краткий стектрейс

# Java Flight Recorder (JFR) — рекомендуемый подход
# Событие: jdk.VirtualThreadPinned
java -XX:StartFlightRecording=filename=recording.jfr,settings=profile MyApp
```

```java
// Программная проверка через JFR API
// Фильтр события jdk.VirtualThreadPinned в записи JFR
```

### JDK 24: synchronized без pinning

Начиная с JDK 24 (JEP 491), виртуальные потоки могут захватывать, удерживать и освобождать мониторы **независимо от carrier thread**. Это означает:

```java
// JDK 24+: безопасно для виртуальных потоков
synchronized (lock) {
    // Если здесь произойдёт блокирующая операция (I/O, sleep),
    // виртуальный поток будет отмонтирован от carrier — pinning НЕ произойдёт
    Thread.sleep(1000);
    socket.read();
}
```

Если вы на JDK < 24, замените `synchronized` на `ReentrantLock` в горячих путях:

```java
// До JDK 24: замена synchronized на ReentrantLock для избежания pinning
private final ReentrantLock lock = new ReentrantLock();

void process() {
    lock.lock();
    try {
        // I/O операции здесь безопасны — виртуальный поток может отмонтироваться
        var data = socket.read();
        database.save(data);
    } finally {
        lock.unlock();
    }
}
```

## Подводные камни

### ThreadLocal и утечки памяти

Каждый виртуальный поток получает собственную копию `ThreadLocal`. При миллионах потоков это миллионы копий:

```java
// ПЛОХО: ThreadLocal с тяжёлым объектом при виртуальных потоках
private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[1024 * 1024]);

// При 1 000 000 виртуальных потоков = 1 TB памяти только на буферы!

// ХОРОШО: используйте ScopedValue или передавайте через параметры
private static final ScopedValue<RequestContext> CTX = ScopedValue.newInstance();

// Или: локальная переменная внутри метода
void handle() {
    byte[] buffer = new byte[8192]; // живёт на стеке виртуального потока
    // ...
}
```

### CPU-bound задачи

Виртуальные потоки оптимизированы для **I/O-bound** задач. CPU-bound задачи **не дают yield**, поэтому carrier thread остаётся занят:

```java
// ПЛОХО: CPU-bound задача в виртуальном потоке
Thread.startVirtualThread(() -> {
    // Этот цикл никогда не отпустит carrier thread
    long sum = 0;
    for (long i = 0; i < 1_000_000_000; i++) {
        sum += i;
    }
});

// ХОРОШО: CPU-bound задачи — в platform thread pool
ExecutorService cpuPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
cpuPool.submit(() -> heavyComputation());
```

Практическое правило: если задача тратит >1 мс на CPU без I/O-пауз, рассмотрите platform threads.

### Пулирование виртуальных потоков

Виртуальные потоки **не нужно пулировать**. Они дешёвые — создавайте новый на каждую задачу:

```java
// ПЛОХО: пул виртуальных потоков (бессмысленно и вредно)
ExecutorService pool = Executors.newFixedThreadPool(100, Thread.ofVirtual().factory());

// ХОРОШО: один поток на задачу
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(task1);
    executor.submit(task2);
}
```

Если нужно ограничить concurrency (например, max 50 одновременных запросов к БД), используйте `Semaphore`:

```java
private final Semaphore dbPermits = new Semaphore(50);

void queryDb() throws InterruptedException {
    dbPermits.acquire();
    try {
        // максимум 50 одновременных запросов
        return database.query(sql);
    } finally {
        dbPermits.release();
    }
}
```

### Synchronized в JDK < 24

До JDK 24 любой `synchronized` блок пиннит carrier thread. Особенно опасно, когда внутри `synchronized` выполняется I/O:

```java
// ОПАСНО на JDK < 24: synchronized + I/O = pinned carrier
synchronized (this) {
    var response = httpClient.send(request); // carrier заблокирован на всё время запроса!
}

// БЕЗОПАСНО: ReentrantLock позволяет виртуальному потоку yield
private final ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    var response = httpClient.send(request); // виртуальный поток может yield
} finally {
    lock.unlock();
}
```

### Мониторинг и отладка

Виртуальные потоки не отображаются в `ThreadMXBean` по умолчанию. Для мониторинга:

```java
// Thread dump включает виртуальные потоки (JDK 21+)
// jcmd <pid> Thread.dump_to_file -format=json threads.json

// JFR события для виртуальных потоков:
// jdk.VirtualThreadStart
// jdk.VirtualThreadEnd
// jdk.VirtualThreadPinned
// jdk.VirtualThreadSubmitFailed
```

## Миграция на виртуальные потоки

### Пошаговая стратегия

**Шаг 1: Аудит ThreadLocal**
- Найдите все `ThreadLocal` — замените на `ScopedValue` где возможно
- Убедитесь, что `ThreadLocal` не хранит тяжёлые объекты

**Шаг 2: Аудит synchronized (JDK < 24)**
- Найдите `synchronized` блоки с I/O внутри
- Замените на `ReentrantLock`

**Шаг 3: Замена ExecutorService**
```java
// Было
ExecutorService pool = Executors.newFixedThreadPool(200);

// Стало
ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
```

**Шаг 4: Убрать пулирование**
- Удалите ограничения размера пула (они бессмысленны с VT)
- Для ограничения concurrency используйте `Semaphore`

**Шаг 5: Тестирование и мониторинг**
- Включите JFR-записи для `jdk.VirtualThreadPinned`
- Нагрузочное тестирование с акцентом на throughput

### Spring Boot и виртуальные потоки

Spring Boot 3.2+ поддерживает виртуальные потоки из коробки:

```yaml
# application.yml — одна строка для переключения
spring:
  threads:
    virtual:
      enabled: true
```

Это переключает Tomcat/Jetty/Undertow на обработку запросов в виртуальных потоках. Работает для:
- HTTP-обработчики (`@Controller`, `@RestController`)
- `@Async` методы
- Spring MVC async support
- `@Scheduled` задачи

```java
// Spring Boot автоматически создаёт бин:
@Bean
public AsyncTaskExecutor applicationTaskExecutor() {
    return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
}
```

## Лучшие практики

1. **Используйте virtual threads для I/O-bound задач** — HTTP-запросы, обращения к БД, файловый I/O, вызовы сервисов. Для CPU-bound оставьте platform threads

2. **Не пулируйте виртуальные потоки** — создавайте новый поток на каждую задачу. `newVirtualThreadPerTaskExecutor()` — стандартный подход

3. **Ограничивайте concurrency через `Semaphore`** — вместо размера пула потоков. Это отделяет ограничение ресурса (подключения к БД) от ограничения потоков

4. **Замените `ThreadLocal` на `ScopedValue`** — особенно для контекстов запроса, пользовательских данных, MDC-логирования

5. **Используйте `ReentrantLock` вместо `synchronized`** — если вы на JDK < 24 и внутри блока есть I/O

6. **Включите JFR-мониторинг** — отслеживайте события `jdk.VirtualThreadPinned` для обнаружения проблем с pinning

7. **Не привязывайте логику к `Thread` identity** — виртуальный поток может выполняться на разных carrier threads. Не используйте `Thread.currentThread()` как ключ

8. **Обновите зависимости** — JDBC-драйверы, HTTP-клиенты, ORM должны быть совместимы с virtual threads (современные версии PostgreSQL JDBC, HikariCP, Apache HttpClient 5 уже совместимы)

9. **Используйте structured concurrency для параллельных задач** — `StructuredTaskScope` обеспечивает автоматическую отмену и чистый error handling

10. **Не смешивайте модели** — в одном сервисе используйте либо virtual threads, либо реактивный подход, но не оба одновременно

## Решение проблем

| Симптом | Причина | Решение |
|---------|---------|---------|
| Throughput не вырос после перехода на VT | Pinning из-за `synchronized` (JDK < 24) | Замените на `ReentrantLock` или обновитесь до JDK 24+ |
| `OutOfMemoryError` при миллионах VT | `ThreadLocal` с тяжёлыми объектами | Замените на `ScopedValue` или локальные переменные |
| Carrier threads исчерпаны | CPU-bound задачи в виртуальных потоках | Выделите CPU-bound задачи в отдельный platform thread pool |
| `jdk.VirtualThreadSubmitFailed` в JFR | Carrier thread pool отклоняет задачи | Увеличьте `jdk.virtualThreadScheduler.maxPoolSize` |
| Медленный thread dump | Миллионы виртуальных потоков | Используйте `jcmd` с `-format=json`, анализируйте фильтрацией |
| `ThreadLocal` пустой в виртуальном потоке | VT не наследует `ThreadLocal` от parent (если не `InheritableThreadLocal`) | Используйте `ScopedValue` + structured concurrency |
| Deadlock с `synchronized` и VT (JDK < 24) | Все carrier threads запиннены | Замените `synchronized` на `ReentrantLock` |
| Spring `@Async` не работает с VT | Не включена поддержка virtual threads | Добавьте `spring.threads.virtual.enabled=true` |
