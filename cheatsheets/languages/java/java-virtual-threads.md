---
title: "Java Virtual Threads (Project Loom)"
description: "Практическое руководство по виртуальным потокам Java 21: создание, ограничения, pinning, отладка, Spring Boot, Structured Concurrency, Scoped Values."
tags:
  - java
  - virtual-threads
  - concurrency
  - loom
  - java21
  - spring-boot
difficulty: "intermediate"
updated: "2026-04-20"
---

# Java Virtual Threads (Project Loom)

Project Loom — это переосмысление конкурентности в Java. Виртуальные потоки дают масштаб сопрограмм при сохранении привычной модели `Thread`.

## Полезные ссылки

### Официальная документация

- [JEP 444: Virtual Threads (Java 21)](https://openjdk.org/jeps/444) — официальный JEP с описанием virtual threads
- [Project Loom (OpenJDK)](https://openjdk.org/projects/loom/) — страница проекта Loom

### Обучающие материалы

- [Virtual Threads in Java 21 (Baeldung)](https://www.baeldung.com/java-virtual-thread-vs-thread) — сравнение virtual и platform threads

### См. также

- [Java Concurrency Advanced](java-concurrency-advanced.md) — ExecutorService, CompletableFuture, Lock
- [Java Memory Model](java-memory-model.md) — happens-before, volatile
- [Java 17/21 Interview](../../interview/programming-languages/java/java-17-21-interview.md) — вопросы на собеседовании
- [Spring WebFlux](../../frameworks/java-frameworks/spring/spring-webflux.md) — реактивная модель vs virtual threads
- [Spring Boot](../../frameworks/spring/spring-boot.md) — конфигурация `spring.threads.virtual.enabled`
- [Вопросы на собеседовании](../../interview/programming-languages/java/java-virtual-threads-interview.md) — подготовка к интервью

## Содержание

- [Virtual Threads vs Platform Threads](#virtual-threads-vs-platform-threads)
- [Создание виртуальных потоков](#создание-виртуальных-потоков)
  - [Thread.ofVirtual()](#threadofvirtual)
  - [Executors.newVirtualThreadPerTaskExecutor()](#executorsnewvirtualthreadpertaskexecutor)
  - [ThreadFactory](#threadfactory)
  - [Thread.startVirtualThread() — самый короткий вариант](#threadstartvirtualthread-самый-короткий-вариант)
- [Ограничения: pinning](#ограничения-pinning)
- [Когда НЕ использовать](#когда-не-использовать)
- [Отладка и мониторинг](#отладка-и-мониторинг)
  - [Трассировка pinning](#трассировка-pinning)
  - [jcmd](#jcmd)
  - [JFR (Java Flight Recorder)](#jfr-java-flight-recorder)
  - [Метрики JVM (Micrometer / Actuator)](#метрики-jvm-micrometer-actuator)
- [Spring Boot + Virtual Threads](#spring-boot-virtual-threads)
  - [Включение одной строкой](#включение-одной-строкой)
  - [Что происходит внутри](#что-происходит-внутри)
  - [WebMVC + Virtual Threads](#webmvc-virtual-threads)
  - [WebFlux vs Virtual Threads](#webflux-vs-virtual-threads)
- [Structured Concurrency](#structured-concurrency)
- [Scoped Values](#scoped-values)
- [Benchmark: когда и сколько выигрыш](#benchmark-когда-и-сколько-выигрыш)
  - [I/O-bound: HTTP-запросы к внешнему сервису](#io-bound-http-запросы-к-внешнему-сервису)
  - [CPU-bound: никакого выигрыша](#cpu-bound-никакого-выигрыша)
- [Типичные ошибки](#типичные-ошибки)

## Virtual Threads vs Platform Threads

**Platform Thread** (обычный поток):
- 1:1 маппинг на OS-поток
- Стек ~1 MB, создание ~1 мс
- ОС ограничивает: ~10k потоков реально, ~1k эффективно

**Virtual Thread** (виртуальный поток):
- M:N маппинг: много виртуальных потоков на несколько OS-потоков
- Стек начинается с ~кБ, растёт по необходимости (heap-allocated)
- Создание дешевле на порядки, миллионы потоков — норма

**Как это работает:**

```text
Virtual Thread 1 ──┐
Virtual Thread 2 ──┤  Scheduler (ForkJoinPool)
Virtual Thread N ──┘       │
                     Carrier Thread 1  (OS Thread)
                     Carrier Thread 2  (OS Thread)
                       ...
```

- **Carrier thread** — platform thread, на котором выполняется virtual thread
- **Continuation** — снапшот стека виртуального потока. При блокировке continuation снимается с carrier thread и паркуется в heap; carrier освобождается для другого виртуального потока
- Блокирующие вызовы (`Thread.sleep`, `InputStream.read`, JDBC) — не блокируют carrier thread, а «unmount» виртуальный поток

**Когда использовать:**
- I/O-bound задачи: HTTP-клиенты, JDBC, Kafka, файловый ввод-вывод
- Высокая конкурентность (тысячи одновременных запросов)
- Миграция thread-per-request приложений без переписывания на реактив

## Создание виртуальных потоков

### Thread.ofVirtual()

```java
// Создать и запустить
Thread vt = Thread.ofVirtual()
    .name("my-vthread")
    .start(() -> System.out.println("Hello from virtual thread"));

// Создать без запуска
Thread vt2 = Thread.ofVirtual()
    .name("worker-", 0)       // авто-нумерация: worker-0, worker-1 ...
    .unstarted(() -> doWork());
vt2.start();

// Проверка
System.out.println(vt.isVirtual()); // true
```

### Executors.newVirtualThreadPerTaskExecutor()

```java
// Один виртуальный поток на каждую задачу
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 100_000; i++) {
        executor.submit(() -> {
            // каждый запрос в своём virtual thread
            return fetchFromDatabase(id);
        });
    }
} // auto-close ждёт завершения всех задач
```

### ThreadFactory

```java
// Для интеграции с существующими API (Spring, Tomcat)
ThreadFactory factory = Thread.ofVirtual()
    .name("http-handler-", 0)
    .factory();

// Использование с пулом (не типичная практика, но допустимо)
ExecutorService exec = Executors.newThreadPerTaskExecutor(factory);
```

### Thread.startVirtualThread() — самый короткий вариант

```java
Thread t = Thread.startVirtualThread(() -> processRequest(req));
t.join(); // дождаться завершения
```

## Ограничения: pinning

**Pinning** — ситуация, когда виртуальный поток не может «unmount» с carrier thread. Carrier блокируется вместе с виртуальным потоком, лишая других virtual threads возможности на него сесть.

**Причины pinning:**

1. **`synchronized` блок/метод** — самая частая причина

```java
// ПРОБЛЕМА: синхронизированный блок вызывает pinning
synchronized (lock) {
    Thread.sleep(Duration.ofSeconds(1)); // carrier заблокирован!
}

// РЕШЕНИЕ: заменить на ReentrantLock
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    Thread.sleep(Duration.ofSeconds(1)); // OK, unmount работает
} finally {
    lock.unlock();
}
```

2. **Native frames** — вызовы JNI/native методов внутри стека

```java
// Любой стековый фрейм с native-кодом удерживает carrier
nativeMethod(); // вызывает JNI — carrier pinned до возврата
```

**Важно:** pinning не является ошибкой — код работает корректно. Проблема только в throughput: carrier thread занят, не может обслуживать других. JVM автоматически добавляет carrier threads при нехватке (до некоторого предела).

**Как проверить, есть ли pinning в библиотеках:**

```java
// Популярные библиотеки и статус pinning:
// JDBC драйверы: PostgreSQL (42.7+) — исправлен, MySQL — исправляется
// Lettuce (Redis): поддерживает virtual threads с 6.3+
// Hibernate: pinning в некоторых путях — проверяйте версию
```

## Когда НЕ использовать

| Сценарий | Почему не подходит | Альтернатива |
|----------|-------------------|--------------|
| CPU-intensive задачи (матрицы, шифрование, компрессия) | Virtual thread = 1 задача на 1 CPU, переключение бесполезно | `ForkJoinPool`, параллельные стримы |
| Код с тяжёлым `synchronized` и pinning | Carrier threads исчерпываются | Мигрировать на `ReentrantLock` или принять ограничение |
| Очень короткие задачи (< мкс) | Overhead создания потока может превысить пользу | `ForkJoinPool.commonPool()`, batch-обработка |
| Системы с жёстким контролем concurrency | Virtual threads не pooling — один поток на задачу, не переиспользуются | Semaphore поверх virtual threads |

**Semaphore для ограничения параллелизма:**

```java
Semaphore semaphore = new Semaphore(100); // не более 100 одновременных обращений к БД

try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (Request req : requests) {
        executor.submit(() -> {
            semaphore.acquire();
            try {
                return db.query(req);
            } finally {
                semaphore.release();
            }
        });
    }
}
```

## Отладка и мониторинг

### Трассировка pinning

```bash
# JVM-флаг: вывод в stderr при каждом pinning
-Djdk.tracePinnedThreads=full    # полный стектрейс
-Djdk.tracePinnedThreads=short   # только верхние фреймы
```

Вывод выглядит так:
```text
Thread[#24,ForkJoinPool-1-worker-1,5,CarrierThreads]
    com.example.SomeService.syncMethod(SomeService.java:42) <-- monitors:1>
```

### jcmd

```bash
# Список всех virtual threads (Java 21)
jcmd <pid> Thread.dump_to_file -format=json threads.json

# Статус виртуальных потоков
jcmd <pid> Thread.print | grep "virtual"
```

### JFR (Java Flight Recorder)

```bash
# Запись JFR с событиями virtual threads
jcmd <pid> JFR.start name=vthreads settings=profile duration=60s filename=vt.jfr

# Полезные события в JFR:
# jdk.VirtualThreadStart / jdk.VirtualThreadEnd
# jdk.VirtualThreadPinned   — pinning event
# jdk.VirtualThreadSubmitFailed
```

```java
// Программная запись JFR в тестах
Configuration config = Configuration.getConfiguration("profile");
try (Recording rec = new Recording(config)) {
    rec.start();
    // ... код под тестом ...
    rec.stop();
    rec.dump(Path.of("vt.jfr"));
}
```

### Метрики JVM (Micrometer / Actuator)

```text
# В Spring Boot Actuator при включённых virtual threads:
jvm.threads.live          — включает virtual threads (может быть миллионы)
jvm.threads.daemon        — виртуальные потоки daemon по умолчанию
```

**Совет:** не алертить на `jvm.threads.live > 1000` если включены virtual threads — это норма.

## Spring Boot + Virtual Threads

### Включение одной строкой

```yaml
# application.yml
spring:
  threads:
    virtual:
      enabled: true
```

Это включает virtual threads для:
- **Tomcat** (веб-запросы) — каждый запрос в виртуальном потоке
- **@Async** методы — используют `VirtualThreadTaskExecutor`
- **Spring Scheduler** (`@Scheduled`)

### Что происходит внутри

```java
// Spring Boot AutoConfiguration (упрощённо):
@Bean
@ConditionalOnProperty("spring.threads.virtual.enabled")
public TomcatProtocolHandlerCustomizer<?> virtualThreadsProtocolHandlerCustomizer() {
    return handler -> handler.setExecutor(
        Executors.newVirtualThreadPerTaskExecutor()
    );
}
```

### WebMVC + Virtual Threads

```java
@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        // Выполняется в virtual thread
        // Thread.sleep, JDBC, HTTP — не блокируют carrier
        User user = userRepository.findById(id); // JDBC в virtual thread — OK
        return user;
    }
}
```

**Не нужно**: `@Async`, `CompletableFuture`, `Mono`/`Flux` — для I/O-bound MVC кода. Простой блокирующий стиль масштабируется так же хорошо.

### WebFlux vs Virtual Threads

| Критерий | WebFlux (Reactor) | Virtual Threads |
|----------|------------------|-----------------|
| Модель программирования | Реактивная (`Mono`/`Flux`) | Блокирующая (обычный Java) |
| Кривая обучения | Высокая | Минимальная |
| Throughput при I/O | Сопоставимый | Сопоставимый |
| Backpressure | Встроенный | Нет (нужен Semaphore) |
| Streaming | Нативный | Сложнее |
| Отладка (стектрейсы) | Реактивный стектрейс, сложный | Обычный стектрейс |
| CPU-bound смешан с I/O | Хорошо | Нужен отдельный пул |

**Вывод:** для новых проектов на WebMVC — включайте virtual threads. WebFlux оправдан для streaming, backpressure и экосистемы R2DBC.

## Structured Concurrency

**Structured Concurrency** (preview в Java 21, second preview в Java 22) — способ запускать параллельные задачи так, чтобы их жизненный цикл был явно ограничен блоком кода.

```java
// Импорт (preview API)
import java.util.concurrent.StructuredTaskScope;

// ShutdownOnFailure: завершить всё, если одна задача упала
String fetchUserAndOrder(long userId) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Subtask<User> userTask    = scope.fork(() -> fetchUser(userId));
        Subtask<Order> orderTask  = scope.fork(() -> fetchLastOrder(userId));

        scope.join()           // ждём обе задачи
             .throwIfFailed(); // если хоть одна упала — пробрасываем исключение

        // Обе успешно завершились
        return userTask.get().name() + " -> " + orderTask.get().id();
    }
    // scope.close() отменяет незавершённые задачи
}
```

```java
// ShutdownOnSuccess: вернуть первый успешный результат
String fetchFromFastestReplica(String query) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
        scope.fork(() -> replica1.query(query));
        scope.fork(() -> replica2.query(query));
        scope.fork(() -> replica3.query(query));

        scope.join();
        return scope.result(); // результат первого успешного
    }
}
```

**Преимущества Structured Concurrency:**
- Задачи не могут «убежать» за пределы блока — нет утечек потоков
- Отмена дочерних задач при выходе из scope — автоматически
- Стектрейс включает родительскую задачу — debuggability
- Легко рассуждать о конкурентности: вошёл в scope → запустил → вышел из scope

**Включение preview API:**

```bash
# Компиляция
javac --enable-preview --source 21 MyApp.java

# Запуск
java --enable-preview MyApp
```

```gradle
// build.gradle (Kotlin DSL)
tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--enable-preview", "--source", "21"))
}
tasks.withType<Test> {
    jvmArgs("--enable-preview")
}
```

## Scoped Values

**Scoped Values** (preview в Java 21) — замена `ThreadLocal` для virtual threads. Неизменяемые значения, автоматически видимые дочерним потокам в рамках scope.

**Проблемы ThreadLocal с virtual threads:**
- `ThreadLocal` работает корректно, но миллионы virtual threads = миллионы `ThreadLocal`-записей → давление на GC
- `InheritableThreadLocal` не масштабируется: каждый дочерний поток копирует Map родителя

```java
// ScopedValue — неизменяемый, передаётся только вниз по стеку
public static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
public static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

// Устанавливаем значение — доступно только внутри where().run()
void handleRequest(HttpRequest req) {
    ScopedValue.where(REQUEST_ID, req.id())
               .where(CURRENT_USER, authenticateUser(req))
               .run(() -> processRequest(req));
}

// Читаем где угодно в call stack внутри where().run()
void processRequest(HttpRequest req) {
    String reqId = REQUEST_ID.get();   // всегда доступен
    User user    = CURRENT_USER.get(); // всегда доступен
    auditLog(reqId, user, "processing started");
}
```

**ScopedValue vs ThreadLocal:**

| Характеристика | ThreadLocal | ScopedValue |
|---------------|-------------|-------------|
| Изменяемость | Изменяемый (`set()`) | Неизменяемый (rebind через `where()`) |
| Передача детям | `InheritableThreadLocal` | Автоматически в `StructuredTaskScope.fork()` |
| Очистка | Нужно явно `remove()` | Автоматически при выходе из scope |
| Масштабирование с VT | Проблемы при миллионах потоков | Оптимизировано |
| API | `get()`, `set()`, `remove()` | `get()`, `where().run()` |

## Benchmark: когда и сколько выигрыш

### I/O-bound: HTTP-запросы к внешнему сервису

| Конфигурация | Параллельных запросов | RPS (условно) | RAM (потоки) |
|-------------|----------------------|--------------|--------------|
| Platform threads (пул 200) | 200 | 1x (baseline) | ~200 MB |
| Virtual threads | 10 000 | ~10-50x | ~50 MB |
| WebFlux (Reactor) | 10 000 | ~10-50x | ~50 MB |

**Реальные цифры (TechEmpower, Spring Boot 3.2):**
- Virtual threads vs Tomcat thread pool: **+40-60% RPS** на I/O-bound нагрузке при нехватке потоков
- При достаточном пуле платформ. потоков: разница < 5%

### CPU-bound: никакого выигрыша

```java
// CPU-intensive: virtual threads не помогают
// Число реально работающих потоков = числу CPU-ядер в обоих случаях
IntStream.range(0, 1_000_000)
         .parallel()  // лучше ForkJoinPool
         .forEach(i -> heavyComputation(i));
```

**Правило:** выигрыш от virtual threads пропорционален времени ожидания I/O. Если поток 90% времени ждёт — выигрыш 10x. Если 10% ждёт — выигрыш 10%.

## Типичные ошибки

| Ошибка | Проблема | Правильно |
|--------|---------|-----------|
| `Executors.newFixedThreadPool(N)` с virtual threads | Pooling virtual threads бессмысленен — они дёшевы | `Executors.newVirtualThreadPerTaskExecutor()` |
| `synchronized` вместо `ReentrantLock` в hot path | Pinning carrier thread, потеря throughput | `ReentrantLock` / `StampedLock` |
| `ThreadLocal` для передачи контекста | Утечки при миллионах VT, ручная очистка | `ScopedValue` (Java 21 preview) |
| Алёрт на `jvm.threads.live > 1000` | Virtual threads видны как обычные потоки в JMX | Разделить мониторинг platform / virtual |
| `Thread.sleep()` считают блокирующим | `Thread.sleep()` в virtual thread корректно «unmounts» carrier | Можно использовать спокойно |
| Оборачивать каждый `submit()` в try-catch | В `StructuredTaskScope` ошибки агрегируются | Использовать `scope.throwIfFailed()` |
| Создавать `newVirtualThreadPerTaskExecutor()` без try-with-resources | ExecutorService не закроется, потоки зависнут | Всегда `try (var exec = ...)` |
| Смешивать CPU-bound и I/O-bound задачи в одном пуле VT | CPU-задачи монополизируют carrier threads | Выделить отдельный `ForkJoinPool` для CPU |
