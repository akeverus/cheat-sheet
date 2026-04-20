---
title: "Java Virtual Threads — Interview"
description: "Вопросы на собеседовании по Java Virtual Threads (Project Loom): carrier threads, pinning, Structured Concurrency, Spring Boot."
tags:
  - interview
  - java
  - virtual-threads
  - concurrency
  - java21
difficulty: "advanced"
updated: "2026-04-20"
---
# Java Virtual Threads — Interview

## Q1. Что такое виртуальные потоки (Virtual Threads) и зачем они нужны?

**Virtual Thread** — лёгкий поток, управляемый JVM (а не ОС). Один OS-поток (carrier thread) может обслуживать тысячи виртуальных потоков.

**Зачем:** традиционные платформенные потоки (Platform Thread) один-в-один соответствуют OS-потокам. ОС ограничивает их число ~10 000, каждый потребляет ~1 МБ стека. При высоком I/O-bound параллелизме (HTTP, DB) большинство потоков просто блокировались.

Virtual Threads позволяют писать **blocking-style код** с **non-blocking производительностью**:
- Один carrier thread при блокировке виртуального потока переключается на другой VT.
- Можно создавать миллионы VT без OOM.

## Q2. Как создать виртуальный поток?

```java
// Thread factory
Thread vt = Thread.ofVirtual().name("vt-1").start(() -> {
    System.out.println("Virtual: " + Thread.currentThread().isVirtual());
});

// Executor для массового создания
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 100_000; i++) {
        executor.submit(() -> {
            Thread.sleep(Duration.ofMillis(100));
            return "done";
        });
    }
}

// Явная проверка
Thread t = Thread.ofVirtual().start(() -> {});
System.out.println(t.isVirtual()); // true
```

## Q3. Что такое carrier thread и как виртуальный поток с ним связан?

**Carrier thread** — платформенный поток, который физически выполняет виртуальный поток. JVM монтирует VT на carrier при выполнении, демонтирует при блокировке.

- При вызове блокирующей операции (I/O, `Thread.sleep`, `LockSupport.park`) JVM демонтирует VT с carrier.
- Carrier thread переключается на другой готовый VT.
- Когда блокировка снимается — VT ставится в очередь планировщика.

Carrier threads — это ForkJoinPool (по умолчанию `parallelism = Runtime.availableProcessors()`).

## Q4. Что такое pinning и как его избежать?

**Pinning** — виртуальный поток «прикреплён» к carrier thread и не может быть демонтирован при блокировке. Это сводит на нет преимущества VT.

Pinning возникает при:
1. **`synchronized`** блоках/методах — JVM не умеет демонтировать VT внутри монитора.
2. **Native-методах** (JNI).

```java
// Проблема — pinning
synchronized (lock) {
    Thread.sleep(1000);  // carrier заблокирован!
}

// Решение — ReentrantLock
private final ReentrantLock lock = new ReentrantLock();

lock.lock();
try {
    Thread.sleep(1000);  // carrier свободен
} finally {
    lock.unlock();
}
```

**Диагностика:** JVM флаг `-Djdk.tracePinnedThreads=full` выводит стек при pinning.

## Q5. Для каких задач Virtual Threads подходят, а для каких нет?

**Подходят:**
- I/O-bound задачи: HTTP-запросы к внешним API, чтение из БД, файловые операции.
- Серверы с высоким параллелизмом и blocking I/O (один поток на запрос).
- Замена ThreadPoolExecutor с большим числом потоков.

**Не подходят:**
- CPU-bound задачи — нет преимущества над ForkJoinPool/ParallelStream.
- Код с активным использованием `synchronized` — pinning нивелирует эффект.
- Реактивное программирование (WebFlux) — там уже non-blocking I/O, VT избыточны.

## Q6. Как включить Virtual Threads в Spring Boot?

```yaml
# application.yml — Spring Boot 3.2+
spring:
  threads:
    virtual:
      enabled: true
```

Spring Boot автоматически переключает Tomcat, `@Async`, планировщик (`@Scheduled`) на виртуальные потоки.

```java
// Ручная настройка (до Spring Boot 3.2)
@Bean
public TomcatProtocolHandlerCustomizer<?> virtualThreadsForTomcat() {
    return handler -> handler.setExecutor(
        Executors.newVirtualThreadPerTaskExecutor());
}

@Bean
public AsyncTaskExecutor applicationTaskExecutor() {
    return new TaskExecutorAdapter(
        Executors.newVirtualThreadPerTaskExecutor());
}
```

## Q7. Влияет ли использование ThreadLocal на Virtual Threads?

`ThreadLocal` работает с VT, но есть нюансы:
- При миллионах VT миллионы `ThreadLocal` значений потребляют много памяти.
- Нельзя использовать пул потоков с VT и `ThreadLocal` (пул нет смысла, VT и так дёшевы) — но если используется, значения из `ThreadLocal` предыдущих задач могут "утечь".

**Java 20+**: Scoped Values (`ScopedValue`) — замена `ThreadLocal` для VT, безопаснее и эффективнее:

```java
ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();

ScopedValue.where(CURRENT_USER, user)
    .run(() -> processRequest());

// Внутри processRequest():
User user = CURRENT_USER.get();
```

## Q8. Что такое Structured Concurrency?

Structured Concurrency (Java 21, preview → Java 23, standard) — подход к параллельным задачам как к структурированному блоку: все дочерние задачи завершаются до выхода из блока.

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<User> user = scope.fork(() -> fetchUser(userId));
    Future<List<Order>> orders = scope.fork(() -> fetchOrders(userId));

    scope.join()           // ждём завершения всех
         .throwIfFailed(); // пробрасываем исключение если было

    return new UserProfile(user.resultNow(), orders.resultNow());
}
// При выходе из блока все задачи гарантированно завершены
```

Режимы:
- `ShutdownOnFailure` — при ошибке одной задачи отменяет остальные.
- `ShutdownOnSuccess` — при успехе одной задачи отменяет остальные (race pattern).

## Q9. Чем Virtual Threads отличаются от корутин Kotlin?

| Критерий | Virtual Threads | Kotlin Coroutines |
|----------|----------------|-------------------|
| Механизм | JVM-управляемые потоки | Suspend-функции + continuations |
| Синтаксис | Обычный blocking Java | `suspend`, `async`, `launch` |
| Совместимость | Все библиотеки Java | Нужны coroutine-aware библиотеки |
| Производительность | Чуть выше накладных расходов | Очень низкие overhead |
| Отладка | Обычный thread dump | Специальные инструменты |
| Реактивный стек | Не нужен | Заменяет |

VT позволяют писать Java в blocking-стиле без изучения reactive API.

## Q10. Как виртуальные потоки взаимодействуют с JDBC?

JDBC-драйверы делают blocking I/O — именно то, для чего VT предназначены. При использовании пула соединений (HikariCP) нужно убедиться, что пул не ограничивает параллелизм:

```yaml
# HikariCP с Virtual Threads — оптимальный размер пула
spring:
  datasource:
    hikari:
      maximum-pool-size: 20  # VT создают много потоков, но соединений с БД обычно немного
```

`r2dbc` (реактивный JDBC) с VT не нужен — обычный JDBC + VT даёт сопоставимую производительность.

## Q11. Как отлаживать Virtual Threads?

```bash
# Включить трассировку pinning
java -Djdk.tracePinnedThreads=full -jar app.jar

# Thread dump (jstack, VisualVM) — показывает VT
jstack <pid>

# Просмотр всех виртуальных потоков
Thread.getAllStackTraces().keySet().stream()
    .filter(Thread::isVirtual)
    .forEach(t -> System.out.println(t.getName()));
```

В JDK 21+ `jcmd <pid> Thread.dump_to_file -format=json file.json` создаёт полный дамп с поддержкой VT.

## Q12. Каков накладной расход создания виртуального потока?

Создание VT: ~1 мкс (vs ~100 мкс для OS-потока). Стек: несколько КБ, grows/shrinks динамически (vs фиксированные ~512 КБ–8 МБ для OS-потока).

```java
// Benchmark: создание 1 000 000 VT за ~1 секунду — нормально
var futures = new ArrayList<Future<?>>();
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 1_000_000; i++) {
        futures.add(executor.submit(() -> "work"));
    }
}
```

## Q13. Почему не нужен пул виртуальных потоков?

Классический пул потоков (`ThreadPoolExecutor`) нужен для:
1. Ограничения числа потоков (дорогих OS-потоков).
2. Переиспользования потоков (чтобы не создавать/уничтожать).

С VT обе причины отпадают: создание дёшево, ограничение числа VT убивает преимущество. Правильно использовать `newVirtualThreadPerTaskExecutor()`.

Ограничивать нужно не потоки, а ресурсы (DB-соединения, семафоры для rate limiting):

```java
Semaphore semaphore = new Semaphore(100);
// каждый VT берёт разрешение перед запросом к БД
semaphore.acquire();
try { db.query(...); } finally { semaphore.release(); }
```

## Q14. Как виртуальные потоки соотносятся с Reactive Streams (WebFlux)?

VT и Reactive — две разных модели решения одной проблемы (высокое I/O-параллелизм):

- **Reactive/WebFlux**: non-blocking I/O + callback/operator chains. Сложнее в написании, но максимальная эффективность.
- **Virtual Threads**: blocking-style код, JVM управляет non-blocking под капотом. Проще, но чуть более накладно.

Для **нового кода** Spring Boot + VT — отличный выбор. WebFlux стоит использовать при очень высоких требованиях к throughput или при работе с reactive-only библиотеками.

## Q15. Что такое Thread.sleep() в контексте VT и чем отличается от Platform Thread?

`Thread.sleep()` в VT **не блокирует carrier thread** — JVM демонтирует VT и carrier переключается на другой.

```java
// C VT — carrier thread НЕ заблокирован
Thread.ofVirtual().start(() -> {
    Thread.sleep(Duration.ofSeconds(5)); // carrier свободен
});

// С Platform Thread — OS-поток заблокирован
Thread.ofPlatform().start(() -> {
    Thread.sleep(Duration.ofSeconds(5)); // OS-поток занят
});
```

Это же касается `Object.wait()`, `LockSupport.park()`, blocking I/O — все они корректно демонтируют VT.

## See also

- [[java-virtual-threads|Java Virtual Threads]] — полный cheatsheet
- [[java-concurrency-interview|Java Concurrency Interview]] — основы многопоточности
- [[java-17-21-interview|Java 17–21 Interview]] — все новшества Java 21
- [[spring-boot-interview|Spring Boot Interview]] — Spring Boot + Virtual Threads
- [[reactive-patterns-interview|Reactive Patterns Interview]] — альтернативный подход
