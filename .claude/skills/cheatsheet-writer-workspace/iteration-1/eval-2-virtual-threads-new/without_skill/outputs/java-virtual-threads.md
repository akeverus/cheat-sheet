---
title: "Java Virtual Threads (Project Loom)"
description: "Шпаргалка по виртуальным потокам Java: API, Structured Concurrency, отличия от platform threads, подводные камни, best practices."
tags: ["languages", "java", "virtual-threads", "project-loom", "concurrency", "structured-concurrency"]
difficulty: "advanced"
prerequisites: ["java-concurrency-basics.md", "java-concurrency-advanced.md"]
next: []
updated: "2026-04-11"
---

# Java Virtual Threads (Project Loom)

Virtual Threads (JEP 444, Java 21) — легковесные потоки, управляемые JVM, а не ОС. Позволяют писать блокирующий код с производительностью асинхронного.

## Полезные ссылки

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 453: Structured Concurrency (Preview)](https://openjdk.org/jeps/453)
- [JEP 462: Structured Concurrency (Second Preview, Java 22)](https://openjdk.org/jeps/462)
- [JEP 464: Scoped Values (Second Preview, Java 22)](https://openjdk.org/jeps/464)
- [Oracle: Virtual Threads Guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)
- [Inside Java: Loom articles](https://inside.java/tag/loom)

## Содержание

- [Что такое Virtual Threads](#что-такое-virtual-threads)
  - [Мотивация](#мотивация)
  - [Как это работает (архитектура)](#как-это-работает-архитектура)
- [Platform Threads vs Virtual Threads](#platform-threads-vs-virtual-threads)
- [API создания Virtual Threads](#api-создания-virtual-threads)
  - [Thread.ofVirtual()](#threadofvirtual)
  - [Executors.newVirtualThreadPerTaskExecutor()](#executorsnewvirtualthreadpertaskexecutor)
  - [Thread.startVirtualThread()](#threadstartvirtualthread)
- [Structured Concurrency](#structured-concurrency)
  - [StructuredTaskScope](#structuredtaskscope)
  - [ShutdownOnFailure](#shutdownonfailure)
  - [ShutdownOnSuccess](#shutdownonsuccess)
  - [Собственные политики](#собственные-политики)
- [Scoped Values](#scoped-values)
- [Подводные камни (Pitfalls)](#подводные-камни-pitfalls)
  - [Pinning](#pinning)
  - [ThreadLocal и утечки памяти](#threadlocal-и-утечки-памяти)
  - [Pooling виртуальных потоков — антипаттерн](#pooling-виртуальных-потоков--антипаттерн)
  - [CPU-bound задачи](#cpu-bound-задачи)
  - [synchronized и native-фреймы](#synchronized-и-native-фреймы)
- [Best Practices](#best-practices)
- [Интеграция с фреймворками](#интеграция-с-фреймворками)
- [Диагностика и отладка](#диагностика-и-отладка)
- [Типичные ошибки](#типичные-ошибки)

## Что такое Virtual Threads

### Мотивация

Классическая модель «один запрос = один OS-поток» не масштабируется:

| Проблема | Описание |
|----------|----------|
| **Лимит OS-потоков** | Каждый platform thread ~1 MB стека + ресурсы ОС. 10 000 потоков = 10 GB RAM только на стеки |
| **Контекст-свитчинг** | Переключение OS-потоков дорогое (~1-10 мкс), растёт с количеством потоков |
| **Reactive-сложность** | Реактивные фреймворки (WebFlux, RxJava) решают масштабируемость ценой читаемости и отладки |

Virtual Threads решают эту проблему: **миллионы** легковесных потоков при сохранении простого блокирующего стиля кода.

### Как это работает (архитектура)

```
┌──────────────────────────────────────────────────┐
│                    JVM                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ VThread 1│ │ VThread 2│ │VThread N │  ...      │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘          │
│       │             │            │                │
│  ┌────▼─────────────▼────────────▼──────────┐     │
│  │         ForkJoinPool (carrier threads)     │    │
│  │  ┌────────┐ ┌────────┐ ┌────────┐         │    │
│  │  │Carrier1│ │Carrier2│ │CarrierM│         │    │
│  │  └────────┘ └────────┘ └────────┘         │    │
│  └──────────────────────────────────────────┘     │
└──────────────────────────────────────────────────┘
                      │
              ┌───────▼───────┐
              │  OS Threads   │
              └───────────────┘
```

- **Virtual Thread** — объект `java.lang.Thread`, управляемый JVM
- **Carrier Thread** — platform thread из `ForkJoinPool`, на котором исполняется виртуальный поток
- При блокирующей операции (I/O, `sleep`, `lock`) виртуальный поток **отмонтируется** (unmount) от carrier-а, освобождая его для других задач
- При завершении блокировки — **монтируется** обратно (возможно, на другой carrier)

## Platform Threads vs Virtual Threads

| Характеристика | Platform Thread | Virtual Thread |
|---------------|----------------|----------------|
| **Управляется** | ОС | JVM (поверх carrier threads) |
| **Стек** | ~1 MB (фиксирован) | Начинается с ~нескольких КБ, растёт по необходимости |
| **Создание** | Дорогое (~1 мс) | Дешёвое (~1 мкс) |
| **Количество** | Тысячи | Миллионы |
| **Планировщик** | ОС | JVM `ForkJoinPool` |
| **Подходит для** | CPU-bound, долгоживущие задачи | I/O-bound, короткоживущие задачи |
| **ThreadLocal** | Привычно, безопасно | Работает, но может привести к утечкам (см. Scoped Values) |
| **Daemon** | По умолчанию `false` | Всегда `true` |
| **Приоритет** | Настраиваемый | Всегда `Thread.NORM_PRIORITY` (игнорируется) |
| **Thread group** | Настраиваемый | Фиксированный `VirtualThreads` |

## API создания Virtual Threads

### Thread.ofVirtual()

```java
// Базовое создание и запуск
Thread vt = Thread.ofVirtual()
    .name("my-vthread")
    .start(() -> {
        System.out.println("Привет из виртуального потока! " + Thread.currentThread());
    });
vt.join();

// С фабрикой имён (удобно для отладки)
Thread.Builder builder = Thread.ofVirtual().name("worker-", 0); // worker-0, worker-1, ...
Thread t1 = builder.start(() -> doTask(1)); // worker-0
Thread t2 = builder.start(() -> doTask(2)); // worker-1

// Проверка типа потока
Thread.currentThread().isVirtual(); // true для виртуальных
```

### Executors.newVirtualThreadPerTaskExecutor()

Самый практичный способ — через `ExecutorService`:

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Каждая задача — отдельный виртуальный поток
    List<Future<String>> futures = new ArrayList<>();

    for (int i = 0; i < 100_000; i++) {
        final int taskId = i;
        futures.add(executor.submit(() -> {
            // Блокирующий I/O — это нормально!
            String response = httpClient.send(request, BodyHandlers.ofString()).body();
            return "Task " + taskId + ": " + response;
        }));
    }

    // Собираем результаты
    for (var future : futures) {
        System.out.println(future.get());
    }
} // AutoCloseable — ждёт завершения всех задач
```

### Thread.startVirtualThread()

Быстрый способ для одноразовых задач:

```java
Thread.startVirtualThread(() -> {
    System.out.println("Быстрый виртуальный поток");
});
```

## Structured Concurrency

> **Preview API** (Java 21-24). Требует `--enable-preview`.

Structured Concurrency гарантирует, что время жизни параллельных подзадач ограничено родительской задачей — аналогия с блоком `try` для потоков.

### StructuredTaskScope

```java
// Базовый паттерн
try (var scope = new StructuredTaskScope<String>()) {
    // Форкаем подзадачи
    Subtask<String> user  = scope.fork(() -> fetchUser(userId));
    Subtask<String> order = scope.fork(() -> fetchOrder(orderId));

    // Ждём завершения ВСЕХ подзадач
    scope.join();

    // Получаем результаты
    String userData  = user.get();   // бросает исключение если подзадача упала
    String orderData = order.get();

    return new UserOrder(userData, orderData);
}
```

### ShutdownOnFailure

Если **любая** подзадача падает — отменяет остальные:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<User>    user    = scope.fork(() -> findUser(id));
    Subtask<Account> account = scope.fork(() -> findAccount(id));

    scope.join()            // ждём
         .throwIfFailed();  // бросает первое исключение

    // Сюда попадём только если ОБЕ задачи успешны
    return new UserAccount(user.get(), account.get());
}
```

### ShutdownOnSuccess

Возвращает результат **первой** успешной подзадачи (гонка):

```java
try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
    scope.fork(() -> fetchFromMirror1(url));
    scope.fork(() -> fetchFromMirror2(url));
    scope.fork(() -> fetchFromMirror3(url));

    scope.join();

    // Результат самого быстрого, остальные отменены
    return scope.result();
}
```

### Собственные политики

Можно наследовать `StructuredTaskScope` и переопределить `handleComplete()`:

```java
class CollectingScope<T> extends StructuredTaskScope<T> {
    private final Queue<T> results = new ConcurrentLinkedQueue<>();
    private final Queue<Throwable> errors = new ConcurrentLinkedQueue<>();

    @Override
    protected void handleComplete(Subtask<? extends T> subtask) {
        switch (subtask.state()) {
            case SUCCESS   -> results.add(subtask.get());
            case FAILED    -> errors.add(subtask.exception());
            case UNAVAILABLE -> { } // отменена
        }
    }

    public List<T> results() { return List.copyOf(results); }
    public List<Throwable> errors() { return List.copyOf(errors); }
}
```

## Scoped Values

> **Preview API** (Java 21-24). Замена `ThreadLocal` для виртуальных потоков.

```java
// Объявляем scoped value
private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

// Привязываем значение на время выполнения блока
ScopedValue.runWhere(CURRENT_USER, authenticatedUser, () -> {
    // Здесь и во всех вызываемых методах доступно:
    User user = CURRENT_USER.get();
    processRequest(user);
});

// Вне блока — CURRENT_USER.isBound() == false

// С возвратом значения
String result = ScopedValue.callWhere(CURRENT_USER, admin, () -> {
    return performAdminAction();
});
```

**Преимущества над ThreadLocal:**

| ThreadLocal | ScopedValue |
|-------------|-------------|
| Мутабельный | Иммутабельный (re-bind через вложенный `runWhere`) |
| Неограниченное время жизни | Чётко ограничен блоком `runWhere` |
| Наследуется дочерними потоками (InheritableThreadLocal) | Автоматически наследуется в `StructuredTaskScope.fork()` |
| Утечки памяти при пулинге | Нет утечек — значение привязано к scope |
| O(1) чтение из хеш-таблицы | O(1) чтение, ещё быстрее (stack walk) |

## Подводные камни (Pitfalls)

### Pinning

**Pinning** — виртуальный поток «прибивается» к carrier thread и не может отмонтироваться при блокировке.

**Причины pinning:**

1. **`synchronized` блок/метод** — если внутри происходит блокирующая операция:

```java
// ПЛОХО: pinning — carrier заблокирован на время I/O
synchronized (lock) {
    var data = restClient.get(url); // блокирующий вызов внутри synchronized
}

// ХОРОШО: используем ReentrantLock
private final ReentrantLock lock = new ReentrantLock();

lock.lock();
try {
    var data = restClient.get(url); // виртуальный поток отмонтируется при I/O
} finally {
    lock.unlock();
}
```

2. **Native-фреймы в стеке** — JNI вызовы

**Диагностика pinning:**

```bash
# JVM флаг для логирования pinning-событий
-Djdk.tracePinnedThreads=full   # полный стектрейс
-Djdk.tracePinnedThreads=short  # краткий формат
```

### ThreadLocal и утечки памяти

```java
// ПРОБЛЕМА: каждый из миллиона виртуальных потоков создаёт свою копию
private static final ThreadLocal<ExpensiveObject> cache = ThreadLocal.withInitial(ExpensiveObject::new);

// РЕШЕНИЕ 1: ScopedValue (предпочтительно)
private static final ScopedValue<ExpensiveObject> CACHE = ScopedValue.newInstance();

// РЕШЕНИЕ 2: если нужен мутабельный контекст — передавайте явно через параметры
```

### Pooling виртуальных потоков -- антипаттерн

```java
// АНТИПАТТЕРН: не нужно создавать пул виртуальных потоков!
ExecutorService pool = Executors.newFixedThreadPool(100, Thread.ofVirtual().factory());

// ПРАВИЛЬНО: один поток на задачу
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
```

Виртуальные потоки **дешёвые** — их не нужно переиспользовать. Пулинг ограничивает параллелизм без причины.

> Если нужно ограничить параллелизм (например, к БД не больше 50 соединений), используйте `Semaphore`:

```java
private final Semaphore dbPermits = new Semaphore(50);

void handleRequest() {
    dbPermits.acquire();
    try {
        queryDatabase();
    } finally {
        dbPermits.release();
    }
}
```

### CPU-bound задачи

Виртуальные потоки **не дают преимуществ** для CPU-bound вычислений:

```java
// Нет смысла — CPU-bound задача не отдаёт carrier thread
Thread.startVirtualThread(() -> {
    // Чистые вычисления без I/O — виртуальный поток не отмонтируется
    BigInteger result = factorial(100_000);
});

// Для CPU-bound используйте platform threads с ForkJoinPool
```

### synchronized и native-фреймы

Многие библиотеки внутри используют `synchronized`. Проверяйте совместимость:

| Библиотека / компонент | Статус (Java 21+) |
|------------------------|-------------------|
| **JDBC драйверы** | Большинство обновлены (PostgreSQL, MySQL 8.2+) |
| **java.io** streams | Могут вызывать pinning (`FileInputStream.read()`) |
| **java.net.Socket** | Переписан на виртуальные потоки в JDK 21 |
| **HttpClient** (JDK) | Полная поддержка |
| **Netty** | Собственная модель, виртуальные потоки не нужны |

## Best Practices

### 1. Используйте для I/O-bound задач

```java
// Идеальный сценарий: веб-сервер с блокирующим I/O
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    serverSocket.accept(); // на каждое соединение — виртуальный поток
    executor.submit(() -> handleConnection(socket));
}
```

### 2. Не пулите виртуальные потоки

Создавайте новый виртуальный поток на каждую задачу. Если нужно ограничить ресурс — используйте `Semaphore`.

### 3. Замените synchronized на ReentrantLock

Где есть блокирующий I/O внутри критической секции:

```java
// До
synchronized (this) { blockingCall(); }

// После
private final ReentrantLock lock = new ReentrantLock();
lock.lock();
try { blockingCall(); } finally { lock.unlock(); }
```

### 4. Предпочитайте ScopedValue вместо ThreadLocal

Особенно для контекста запроса (User, Trace ID, Locale).

### 5. Используйте Structured Concurrency

Вместо ручного управления `Future` — `StructuredTaskScope` гарантирует:
- Все подзадачи завершены до выхода из scope
- Отмена каскадная — при ошибке одной отменяются остальные
- Стектрейсы читаемые — видна иерархия parent-child

### 6. Ограничивайте доступ к внешним ресурсам

```java
// Семафор для ограничения одновременных HTTP-запросов
private static final Semaphore HTTP_PERMITS = new Semaphore(200);

String fetch(String url) throws Exception {
    HTTP_PERMITS.acquire();
    try {
        return httpClient.send(request, BodyHandlers.ofString()).body();
    } finally {
        HTTP_PERMITS.release();
    }
}
```

### 7. Давайте потокам имена для отладки

```java
Thread.ofVirtual()
    .name("order-processor-", 0)  // order-processor-0, order-processor-1, ...
    .factory();
```

## Интеграция с фреймворками

### Spring Boot 3.2+

```yaml
# application.yml — включает виртуальные потоки для Tomcat/Jetty
spring:
  threads:
    virtual:
      enabled: true
```

Это автоматически настраивает:
- Tomcat / Jetty / Undertow — каждый запрос в виртуальном потоке
- `@Async` методы — исполняются в виртуальных потоках
- Spring MVC — блокирующий стиль с масштабируемостью реактивного

### Quarkus 3.x

```properties
# application.properties
quarkus.virtual-threads.enabled=true
```

Аннотация `@RunOnVirtualThread` на REST-эндпоинтах.

### Micronaut 4.x

```yaml
# application.yml
micronaut:
  server:
    thread-selection: AUTO
  executors:
    io:
      type: VIRTUAL
```

## Диагностика и отладка

### JFR-события

```bash
# Запись Flight Recorder событий
java -XX:StartFlightRecording=filename=vthreads.jfr,settings=profile ...

# Virtual Thread события в JFR:
# jdk.VirtualThreadStart
# jdk.VirtualThreadEnd
# jdk.VirtualThreadPinned
# jdk.VirtualThreadSubmitFailed
```

### Thread dump

```bash
# Thread dump теперь в JSON формате, показывает иерархию
jcmd <pid> Thread.dump_to_file -format=json threads.json

# Или через kill
kill -QUIT <pid>
```

### Полезные JVM-флаги

| Флаг | Описание |
|------|----------|
| `-Djdk.tracePinnedThreads=full` | Логирует события pinning с полным стектрейсом |
| `-Djdk.tracePinnedThreads=short` | Краткий формат pinning-логов |
| `-Djdk.virtualThreadScheduler.parallelism=N` | Количество carrier threads (по умолчанию = числу ядер) |
| `-Djdk.virtualThreadScheduler.maxPoolSize=N` | Максимум carrier threads |

## Типичные ошибки

### 1. Пул виртуальных потоков с фиксированным размером

```java
// ОШИБКА: убивает весь смысл виртуальных потоков
var pool = Executors.newFixedThreadPool(10, Thread.ofVirtual().factory());

// ПРАВИЛЬНО
var executor = Executors.newVirtualThreadPerTaskExecutor();
```

### 2. Блокирующий I/O внутри synchronized

```java
// ОШИБКА: pinning carrier thread
synchronized (this) {
    return new URL(url).openStream().readAllBytes();
}

// ПРАВИЛЬНО: ReentrantLock
lock.lock();
try {
    return new URL(url).openStream().readAllBytes();
} finally {
    lock.unlock();
}
```

### 3. ThreadLocal с тяжёлыми объектами

```java
// ОШИБКА: миллион виртуальных потоков = миллион копий буфера
private static final ThreadLocal<byte[]> BUFFER =
    ThreadLocal.withInitial(() -> new byte[1024 * 1024]);

// ПРАВИЛЬНО: ScopedValue или явная передача через параметр
```

### 4. Ожидание ускорения CPU-bound задач

```java
// ОШИБКА: нет блокирующих операций — виртуальные потоки не помогут
try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 1000; i++) {
        exec.submit(() -> computeHash(data)); // CPU-bound
    }
}

// ПРАВИЛЬНО: ForkJoinPool для CPU-bound
ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
```

### 5. Забыли обработать InterruptedException

```java
// ОШИБКА: глотание прерывания ломает Structured Concurrency
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // пусто — scope.shutdown() не сможет остановить эту задачу!
}

// ПРАВИЛЬНО: пробрасываем или восстанавливаем флаг
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    throw new RuntimeException(e);
}
```
