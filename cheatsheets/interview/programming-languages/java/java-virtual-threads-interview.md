---
title: "Вопросы на собеседовании: Java Virtual Threads"
description: "Java Virtual Threads (Project Loom): carrier threads, pinning, Structured Concurrency, ScopedValue, интеграция с Spring Boot и JDBC"
tags:
  - interview
  - java
  - java-virtual-threads-interview
type: "interview"
difficulty: "advanced"
aliases:
  - "Вопросы на собеседовании"
  - "Java Virtual Threads"
  - "Virtual Threads interview"
  - "Virtual Threads собеседование"
prerequisites:
  - "[[java-virtual-threads]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Java Virtual Threads`

`Virtual Threads` (Project Loom) — лёгкие потоки JVM, ставшие стандартом в Java 21. Позволяют писать blocking-стиль код с non-blocking производительностью — миллионы потоков вместо тысяч, без перехода на reactive.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444) — JEP, описывающий Virtual Threads
- [Spring Boot: Virtual Threads](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.task-execution-and-scheduling) — интеграция в Spring Boot 3.2+
- [Baeldung: Virtual Threads](https://www.baeldung.com/java-virtual-thread-vs-thread) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое виртуальные потоки и зачем они нужны?](#q1-что-такое-виртуальные-потоки-virtual-threads-и-зачем-они-нужны)
- [Q2. Как создать виртуальный поток?](#q2-как-создать-виртуальный-поток)
- [Q3. (!) Что такое carrier thread?](#q3-что-такое-carrier-thread-и-как-виртуальный-поток-с-ним-связан)
- [Q4. (!) Что такое pinning и как его избежать?](#q4-что-такое-pinning-и-как-его-избежать)

**Применение**
- [Q5. Для каких задач VT подходят, а для каких нет?](#q5-для-каких-задач-virtual-threads-подходят-а-для-каких-нет)
- [Q6. Как включить Virtual Threads в Spring Boot?](#q6-как-включить-virtual-threads-в-spring-boot)
- [Q7. Влияет ли ThreadLocal на Virtual Threads?](#q7-влияет-ли-использование-threadlocal-на-virtual-threads)

**Structured Concurrency**
- [Q8. Что такое Structured Concurrency?](#q8-что-такое-structured-concurrency)

**Сравнения**
- [Q9. Чем VT отличаются от корутин Kotlin?](#q9-чем-virtual-threads-отличаются-от-корутин-kotlin)
- [Q10. Как VT взаимодействуют с JDBC?](#q10-как-виртуальные-потоки-взаимодействуют-с-jdbc)
- [Q14. Как VT соотносятся с Reactive Streams?](#q14-как-виртуальные-потоки-соотносятся-с-reactive-streams-webflux)

**Диагностика и производительность**
- [Q11. Как отлаживать Virtual Threads?](#q11-как-отлаживать-virtual-threads)
- [Q12. Каков накладной расход создания VT?](#q12-каков-накладной-расход-создания-виртуального-потока)
- [Q13. Почему не нужен пул виртуальных потоков?](#q13-почему-не-нужен-пул-виртуальных-потоков)
- [Q15. Thread.sleep() в VT vs Platform Thread?](#q15-что-такое-threadsleep-в-контексте-vt-и-чем-отличается-от-platform-thread)

## Q1. Что такое виртуальные потоки (Virtual Threads) и зачем они нужны?

**Virtual Thread** — лёгкий поток, управляемый JVM (а не ОС). Один OS-поток (carrier thread) может обслуживать тысячи виртуальных потоков.

**Зачем:** традиционные платформенные потоки (Platform Thread) один-в-один соответствуют OS-потокам. ОС ограничивает их число ~10 000, каждый потребляет ~1 МБ стека. При высоком I/O-bound параллелизме (HTTP, DB) большинство потоков просто блокировались.

Virtual Threads позволяют писать **blocking-style код** с **non-blocking производительностью**:
- Один carrier thread при блокировке виртуального потока переключается на другой VT.
- Можно создавать миллионы VT без OOM.


> [!mcq]
>
> **Вопрос:** В чём фундаментальное отличие Virtual Thread от Platform Thread в JVM и какую проблему это решает?
>
> ---
>
> #### A) Virtual Thread — это просто платформенный поток с уменьшенным размером стека через флаг `-Xss64k` — ❌ Неверно
>
> **Что на самом деле:** Virtual Thread — это совершенно отдельная абстракция, управляемая JVM, а не ОС. Стек VT хранится в heap (а не в нативной памяти), грузится/выгружается динамически, и один Platform Thread (carrier) обслуживает много VT. Это M:N модель, а не 1:1.
>
> **Откуда путаница:** middle часто думает, что Loom — это «тюнинг» существующих потоков. На самом деле в JDK добавили новый kind потока с другим scheduler-ом (ForkJoinPool) и mounting/unmounting механикой.
>
> **Если бы это было правдой:** ОС-предел потоков (`/proc/sys/kernel/threads-max`, ~10K) никуда бы не делся, и при попытке создать миллион потоков — `OutOfMemoryError: unable to create native thread`. Реальный benchmark из JEP 444 показывает 1M VT за ~1.5 сек на ноутбуке.
>
> ---
>
> #### B) Virtual Thread — лёгкий поток, управляемый JVM поверх пула carrier threads (Platform Threads из ForkJoinPool); при блокирующем вызове JVM отмонтирует VT с carrier и тот берёт следующий VT из очереди — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Virtual Thread реализует M:N scheduling. M виртуальных потоков мультиплексируются на N carrier threads (по умолчанию N = `Runtime.availableProcessors()`). При вызове блокирующей операции (`Thread.sleep`, blocking I/O, `LockSupport.park`) JVM сохраняет состояние VT в heap (continuation), отмонтирует его с carrier-а, и carrier немедленно берёт другой готовый VT. Когда блокировка снимается — VT помещается в очередь scheduler-а и в дальнейшем монтируется на любой свободный carrier.
>
> Это позволяет писать blocking-style код (`var user = httpClient.get(url)`), но получать non-blocking throughput, потому что carrier не простаивает во время I/O.
>
> **Пример:**
> ```java
> // 10 000 параллельных HTTP-запросов через VT — никаких CompletableFuture
> try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
>     List<Future<String>> futures = IntStream.range(0, 10_000)
>         .mapToObj(i -> executor.submit(() -> {
>             var client = HttpClient.newHttpClient();
>             return client.send(
>                 HttpRequest.newBuilder(URI.create("https://api/" + i)).build(),
>                 BodyHandlers.ofString()
>             ).body();
>         }))
>         .toList();
>     // Под капотом: ~CPU cores carrier threads обслуживают 10K VT
> }
> ```
>
> **Когда применять:**
> - Spring Boot 3.2+ HTTP API с blocking JDBC (HikariCP) — включить `spring.threads.virtual.enabled=true`.
> - Микросервис-агрегатор, делающий 5-10 параллельных вызовов к downstream-сервисам на каждый входящий запрос (фан-аут).
> - Замена больших `ThreadPoolExecutor(500)` при I/O-bound нагрузке — у Netflix и LinkedIn доклады о замене Reactor на VT в части сервисов.
> - Background-задачи с blocking-вызовами (импорт CSV, скрапинг), где нужны тысячи параллельных контекстов.
>
> **Подводные камни:**
> - VT — НЕ ускорение CPU-bound: с GIL-стилем нет, но carrier-ы всё равно ограничены ядрами; на CPU-задачах VT равноценен ParallelStream/ForkJoinPool.
> - Старые библиотеки с `synchronized` блокировками вокруг I/O вызывают pinning (carrier застревает) — см. [[java-virtual-threads-interview#Q4]].
> - Не пулить VT через `ThreadPoolExecutor(N)` — это убивает преимущество (см. [[java-virtual-threads-interview#Q13]]).
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q3]] — детали carrier thread и mounting; [[java-virtual-threads-interview#Q4]] — pinning как срыв этой модели; [[java-virtual-threads-interview#Q12]] — измеримый overhead создания.
>
> ---
>
> #### C) Virtual Thread выполняется отдельным OS-потоком, но JVM группирует их по NUMA-узлам для cache locality — ❌ Неверно
>
> **Что на самом деле:** VT не имеет соответствующего OS-потока. Он живёт исключительно внутри JVM как continuation. NUMA-awareness существует в ForkJoinPool, но это про placement carrier threads, а не VT.
>
> **Откуда путаница:** в low-latency JVM-системах (LMAX Disruptor, Aeron) действительно делают thread affinity и NUMA pinning. Можно ошибочно перенести эти идеи на Loom.
>
> **Если бы это было правдой:** один OS-поток на VT — это вернуло бы предел ОС (~10K) и стоимость ~1MB на стек. Создать миллион VT было бы невозможно.
>
> ---
>
> #### D) Virtual Thread — синтаксический сахар над `CompletableFuture`, компилятор переписывает блокирующие вызовы в цепочки `.thenApply()` — ❌ Неверно
>
> **Что на самом деле:** Loom — это runtime-механизм, а не bytecode transformation. Никаких CPS-преобразований компилятором, никакой codegen-магии. Метод `Thread.sleep` внутри определяет, в каком потоке он выполнен, и вызывает `VirtualThreadContinuation.yield()` для cooperative unmounting.
>
> **Откуда путаница:** Kotlin coroutines действительно используют CPS-трансформацию (suspend → continuation). Если переносить эту модель на Java, можно ожидать compiler magic.
>
> **Если бы это было правдой:** существующие jar-ы (jdbc-драйвера, HTTP-клиенты) не работали бы с VT без перекомпиляции. На деле Hikari, Apache HTTP Client, Jetty Servlet работают «из коробки» — это и есть киллер-фича Loom против coroutines.

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


> [!mcq]
>
> **Вопрос:** Какой API в JDK 21 используется для массового создания виртуальных потоков и в чём отличие от Thread.ofVirtual()?
>
> ---
>
> #### A) `Executors.newCachedThreadPool()` — JDK 21 переключает его на VT автоматически если установлен `-Dvirtual.threads=true` — ❌ Неверно
>
> **Что на самом деле:** `newCachedThreadPool()` остаётся пулом Platform Threads без изменений. Нет глобального флага, превращающего его в VT. Нужен явный `Executors.newVirtualThreadPerTaskExecutor()` или собственный `ThreadFactory` через `Thread.ofVirtual().factory()`.
>
> **Откуда путаница:** Spring Boot имеет похожий флаг `spring.threads.virtual.enabled=true`, который меняет executor-ы Spring (Tomcat, @Async). Это Spring-уровень, не JDK.
>
> **Если бы это было правдой:** существующий код с `newCachedThreadPool` сразу получил бы VT, что сломало бы legacy с `synchronized + I/O` (pinning) — было бы много CVE и regression. Поэтому JDK сознательно требует opt-in.
>
> ---
>
> #### B) `Executors.newFixedThreadPool(Integer.MAX_VALUE)` — ❌ Неверно
>
> **Что на самом деле:** `newFixedThreadPool` создаёт `ThreadPoolExecutor` с **Platform Threads**. Передача `Integer.MAX_VALUE` приведёт к попытке создать миллионы OS-потоков — `OutOfMemoryError: unable to create native thread` при ~10K. VT нужно использовать через `newVirtualThreadPerTaskExecutor()`.
>
> **Откуда путаница:** в pre-Loom коде «много потоков» — это большой fixed pool. Привычка переносится. Plus параметр — `int`, и `MAX_VALUE` выглядит как «без лимита».
>
> **Если бы это было правдой:** проблема ОС-потоков (1MB стек × N) исчезла бы. По факту 5000 платформенных потоков уже жрут ~5 GB и упираются в `nproc`.
>
> ---
>
> #### C) `Executors.newVirtualThreadPerTaskExecutor()` создаёт новый VT на каждую submit-задачу; `Thread.ofVirtual().start(r)` создаёт ровно один VT — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> JDK 21 предоставляет два главных API для VT:
> 1. **`Thread.ofVirtual()`** — `Thread.Builder.OfVirtual`, fluent API для создания одного VT (`name`, `inheritInheritableThreadLocals`, `uncaughtExceptionHandler`). Метод `.start(r)` стартует VT, `.unstarted(r)` — создаёт без запуска, `.factory()` — возвращает `ThreadFactory`.
> 2. **`Executors.newVirtualThreadPerTaskExecutor()`** — не пул, а executor, который на каждый `submit/execute` создаёт **новый** VT. Имеет AutoCloseable семантику для try-with-resources с `close()` = `shutdown() + awaitTermination(Long.MAX_VALUE)`.
>
> Ключевая разница с привычными executor-ами: «per task» — никакого переиспользования, каждая задача в своём свежем VT. Это безопасно для ThreadLocal и предсказуемо.
>
> **Пример:**
> ```java
> // Способ 1: одиночный VT через builder
> Thread vt = Thread.ofVirtual()
>     .name("worker-", 1)              // префикс + start counter
>     .uncaughtExceptionHandler((t, e) -> log.error("VT crashed", e))
>     .start(() -> processOrder(orderId));
>
> // Способ 2: массовый запуск через executor (правильный паттерн)
> try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
>     List<Future<Order>> futures = orderIds.stream()
>         .map(id -> executor.submit(() -> orderService.fetch(id)))
>         .toList();
>     // close() в конце try-with-resources дождётся ВСЕХ задач
> }
>
> // Способ 3: VT как ThreadFactory для совместимости со старым кодом
> ThreadFactory vtFactory = Thread.ofVirtual().name("vt-", 0).factory();
> var scheduler = Executors.newScheduledThreadPool(1, vtFactory); // ВНИМАНИЕ: scheduler хранит потоки, не делай так — см. подводные камни
> ```
>
> **Когда применять:**
> - Web-controller у Spring Boot 3.2+: каждый HTTP-запрос — отдельный VT, через `newVirtualThreadPerTaskExecutor()` на уровне Tomcat.
> - Параллельный фан-аут к downstream-сервисам (10-100 параллельных вызовов) — Netflix паттерн.
> - Batch-обработка: пройти 1M записей с blocking I/O на каждой — миллион VT за пару секунд.
> - Тесты с большим числом параллельных сценариев — Testcontainers + VT для нагрузочного теста изнутри JVM.
>
> **Подводные камни:**
> - `newVirtualThreadPerTaskExecutor()` НЕ кэширует — переиспользования нет, и это правильно (см. [[java-virtual-threads-interview#Q13]]).
> - НЕ оборачивать в `ScheduledThreadPoolExecutor` со своей VT-factory: scheduler удерживает потоки, и теряется dynamism. Используй `Thread.ofVirtual().start(() -> Thread.sleep(...))` напрямую.
> - При закрытии executor через try-with-resources идёт ожидание всех задач — если задача висит, `close()` тоже зависнет. Тайм-аут — через явный `shutdown()` + `awaitTermination(timeout)`.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — модель VT vs Platform Thread; [[java-virtual-threads-interview#Q13]] — почему не нужен пул VT; [[java-virtual-threads-interview#Q6]] — Spring Boot интеграция.
>
> ---
>
> #### D) `new VirtualThread(runnable).start()` — прямой конструктор класса `VirtualThread` — ❌ Неверно
>
> **Что на самом деле:** Класс `java.lang.VirtualThread` существует, но является `package-private` (или sealed final внутри `java.lang`) — нет публичного конструктора. JDK сознательно скрывает реализацию, оставляя только factory API (`Thread.ofVirtual()`, `Executors.newVirtualThreadPerTaskExecutor()`).
>
> **Откуда путаница:** во всех JDK классах поток создавался через `new Thread(r)`. Логично ожидать `new VirtualThread(r)`.
>
> **Если бы это было правдой:** была бы публичная зависимость от внутренней реализации, и JDK не мог бы менять continuation-модель без breakage. По JEP 444 авторы намеренно закрыли класс.

## Q3. Что такое carrier thread и как виртуальный поток с ним связан?

**Carrier thread** — платформенный поток, который физически выполняет виртуальный поток. JVM монтирует VT на carrier при выполнении, демонтирует при блокировке.

- При вызове блокирующей операции (I/O, `Thread.sleep`, `LockSupport.park`) JVM демонтирует VT с carrier.
- Carrier thread переключается на другой готовый VT.
- Когда блокировка снимается — VT ставится в очередь планировщика.

Carrier threads — это ForkJoinPool (по умолчанию `parallelism = Runtime.availableProcessors()`).


> [!mcq]
>
> **Вопрос:** По умолчанию scheduler для carrier threads в JDK 21 — что это и сколько потоков создаётся?
>
> ---
>
> #### A) Это специальный `ForkJoinPool` (поле `VirtualThread.DEFAULT_SCHEDULER`) с параллелизмом `Runtime.availableProcessors()`; его можно переопределить системным свойством `jdk.virtualThreadScheduler.parallelism` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Под капотом VT-scheduler — это инстанс `ForkJoinPool`, отличный от common pool. По умолчанию parallelism = число CPU (через `Runtime.availableProcessors()`), но это можно изменить через системные свойства до старта JVM:
> - `jdk.virtualThreadScheduler.parallelism` — целевое число активных carrier-ов.
> - `jdk.virtualThreadScheduler.maxPoolSize` — максимум carrier-ов (по умолчанию 256). При pinning или managed-blocker carrier-ы могут расти до этого предела.
> - `jdk.virtualThreadScheduler.minRunnable` — нижняя граница активных carrier-ов перед добавлением новых.
>
> Carrier-ы — это обычные платформенные потоки с именем `ForkJoinPool-1-worker-N`. Они «носят» VT: монтаж VT (`mount`) — это сохранение Thread.currentThread() pointer-а, демонтаж (`unmount`) — обратное.
>
> **Пример:**
> ```java
> // Старт JVM с увеличенным параллелизмом для контейнера с CPU limit но многими VT
> // java -Djdk.virtualThreadScheduler.parallelism=16 -jar app.jar
>
> // Диагностика carrier-ов
> Thread.ofVirtual().start(() -> {
>     // Внутри VT: получить «текущий» carrier через reflection или JFR
>     System.out.println(Thread.currentThread()); // VirtualThread[#23]/runnable@ForkJoinPool-1-worker-3
>     blockingIO();                               // unmount → carrier берёт другой VT
>     System.out.println(Thread.currentThread()); // тот же VT, но carrier может быть другой
> }).join();
>
> // Кастомный scheduler через рефлексию (внутреннее API, осторожно)
> // ExecutorService customScheduler = Executors.newFixedThreadPool(8);
> // Thread.Builder.OfVirtual via reflection scheduler field
> ```
>
> **Когда применять:**
> - В Kubernetes-pod с CPU limit = 2 и 100K VT: установить `jdk.virtualThreadScheduler.parallelism=2` чтобы избежать context-switch overhead carrier-ов поверх ядер.
> - При диагностике pinning через `-Djdk.tracePinnedThreads=full` carrier-ы видны в stack trace.
> - Для JFR events `jdk.VirtualThreadPinned`, `jdk.VirtualThreadSubmitFailed` — мониторить health scheduler-а.
> - В докладах Heinz Kabutz / Brian Goetz — рекомендация не трогать scheduler defaults без проблем.
>
> **Подводные камни:**
> - НЕ передавать произвольный `Executors.newFixedThreadPool` как scheduler — он не понимает `ForkJoinTask`, и VT не смогут yield/unmount эффективно.
> - При большом `parallelism` (>2× CPU) растёт contention в work-stealing queue, throughput может упасть.
> - `maxPoolSize=256` — при pinning от 256 синхронизированных VT новый VT может НЕ получить carrier и зависнуть до освобождения. Симптом: `jstack` показывает 256 carrier-ов в `synchronized + I/O` и горы VT в очереди scheduler-а.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — что такое VT и как mount/unmount работает; [[java-virtual-threads-interview#Q4]] — pinning, который убивает carrier-pool; [[java-virtual-threads-interview#Q13]] — почему НЕ нужен дополнительный пул поверх VT.
>
> ---
>
> #### B) Это `Executors.newCachedThreadPool` без лимита потоков — JVM создаёт carrier по требованию — ❌ Неверно
>
> **Что на самом деле:** VT-scheduler — НЕ `newCachedThreadPool`. Это специализированный `ForkJoinPool` с work-stealing очередями, что важно для миграции VT между carrier-ами (steal). `CachedThreadPool` имеет одну общую очередь — это не масштабируется.
>
> **Откуда путаница:** в pre-Loom blocking-серверах действительно часто использовали `newCachedThreadPool` для I/O-bound — он рос динамически. На Loom выбор пал на FJP из-за work-stealing.
>
> **Если бы это было правдой:** при 10K активных VT carrier-ы росли бы до 10K, упёршись в `nproc`. На деле благодаря mount/unmount carrier-ов нужно ~ CPU cores штук.
>
> ---
>
> #### C) Это common ForkJoinPool, тот же что обслуживает `parallelStream()` и `CompletableFuture` — ❌ Неверно
>
> **Что на самом деле:** VT-scheduler — это **отдельный** инстанс `ForkJoinPool`, **не** common pool. Это специально сделано, чтобы pinning или медленный VT не убил `parallelStream()` и наоборот.
>
> **Откуда путаница:** оба используют `ForkJoinPool`, и в раннем превью Loom (Java 19) обсуждался reuse common pool. В финальном JEP 444 решили изолировать.
>
> **Если бы это было правдой:** один зависший VT с pinning убивал бы все `parallelStream` приложения. Был бы единый bottleneck для CPU и I/O нагрузки, что противоречит принципу separation.
>
> ---
>
> #### D) Это `Executors.newSingleThreadExecutor` — один carrier на JVM, остальные VT ждут — ❌ Неверно
>
> **Что на самом деле:** Параллелизм carrier-ов = CPU cores по умолчанию. Single-thread на JVM с 16 ядрами означал бы, что VT не масштабируются по CPU — что противоречит JEP 444 (миллион VT, и при этом загрузка всех ядер).
>
> **Откуда путаница:** некоторые scheduler-ы (Netty event loop) действительно single-threaded на запрос, и можно перенести идею.
>
> **Если бы это было правдой:** throughput не зависел бы от ядер. Netflix benchmark в 2023: 16-core machine, blocking JDBC + VT — 30K rps; single-thread бы дал 1-2K rps.

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


> [!mcq]
>
> **Вопрос:** В коде ниже что нужно изменить, чтобы устранить pinning, не теряя thread-safety?
>
> ```java
> private final Object cacheLock = new Object();
> public Response fetch(String key) {
>     synchronized (cacheLock) {
>         if (cache.containsKey(key)) return cache.get(key);
>         var resp = httpClient.send(req, BodyHandlers.ofString()); // блокирующий I/O
>         cache.put(key, resp.body());
>         return cache.get(key);
>     }
> }
> ```
>
> ---
>
> #### A) Добавить JVM-флаг `-Djdk.virtualThreadScheduler.preferPinning=false` — JVM сама обойдёт pinning — ❌ Неверно
>
> **Что на самом деле:** Такого флага не существует. JVM физически не может unmount VT внутри `synchronized` блока в текущей реализации (JEP 444), потому что monitor entry хранится в нативной фрейм-структуре, которая не сериализуется в continuation. Нет «волшебного флага», который это обойдёт.
>
> **Откуда путаница:** в Java исторически много фич включается флагами (`-XX:+UseG1GC`). Кажется, что и pinning fix имеет флаг.
>
> **Если бы это было правдой:** не было бы рекомендаций мигрировать с `synchronized` на `ReentrantLock` в JEP 444. JEP 491 («Synchronize Virtual Threads without Pinning», targeted Java 24) сделает это автоматически — но это будущее.
>
> ---
>
> #### B) Заменить `synchronized` на `volatile` для поля `cache` — ❌ Неверно
>
> **Что на самом деле:** `volatile` обеспечивает только visibility памяти, не atomicity составных операций (`containsKey` + `put`). Под нагрузкой два VT параллельно вызовут `httpClient.send` для одного key, и `cache.put` затрёт друг друга — race condition, нарушение «cache loaded once».
>
> **Откуда путаница:** `volatile` часто упоминают как «лёгкую альтернативу synchronized». Применимо для одиночных read/write, не для compound.
>
> **Если бы это было правдой:** в production видели бы дубликаты HTTP-запросов под нагрузкой, в логах — два «fetched key=X» подряд. И всё ещё было бы pinning, если бы где-то ещё остался `synchronized`.
>
> ---
>
> #### C) Поставить `Thread.yield()` перед I/O — это даст JVM возможность unmount — ❌ Неверно
>
> **Что на самом деле:** `Thread.yield()` — это hint для scheduler-а, который для VT внутри `synchronized` тоже не unmount-ит. И thread-safety проблема `httpClient.send + cache.put` остаётся.
>
> **Откуда путаница:** `Thread.yield()` исторически использовался для cooperative scheduling. Но Loom не cooperative по yield — unmount привязан к специфичным блокирующим методам JDK.
>
> **Если бы это было правдой:** добавление `Thread.yield()` в горячий путь — typical micro-optimization. Реально измерить — нет эффекта, в `-Djdk.tracePinnedThreads=full` всё то же.
>
> ---
>
> #### D) Заменить `synchronized (cacheLock)` на `ReentrantLock`, и/или использовать `ConcurrentHashMap.computeIfAbsent` чтобы избежать ручной блокировки вокруг I/O — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Pinning возникает потому что `synchronized` использует native monitor (`monitorenter`/`monitorexit`), который не интегрирован с continuations. `ReentrantLock`, наоборот, реализован на `AbstractQueuedSynchronizer` + `LockSupport.park()` — а `park()` корректно unmount-ит VT с carrier-а. Pinning исчезает.
>
> Ещё лучше — убрать ручную блокировку: `ConcurrentHashMap.computeIfAbsent` гарантирует, что value-loader будет вызван **один раз** на ключ, и не держит lock во время вычисления (per-bucket locking + CAS, а не глобальный mutex).
>
> Заметка про JEP 491 (Java 24): команда Loom планирует unmount внутри `synchronized` через рефакторинг monitor representation. Когда это придёт — `synchronized` перестанет быть проблемой. Но в Java 21 LTS и большую часть Java 25 эта миграция обязательна.
>
> **Пример:**
> ```java
> // Вариант 1: ReentrantLock — минимальная правка, сохраняет существующую логику
> private final ReentrantLock cacheLock = new ReentrantLock();
> public Response fetch(String key) {
>     cacheLock.lock();
>     try {
>         if (cache.containsKey(key)) return cache.get(key);
>         var resp = httpClient.send(req, BodyHandlers.ofString());
>         cache.put(key, resp.body());
>         return cache.get(key);
>     } finally {
>         cacheLock.unlock();
>     }
> }
>
> // Вариант 2: ConcurrentHashMap.computeIfAbsent — лучше, нет глобального lock
> private final ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
> public String fetch(String key) {
>     return cache.computeIfAbsent(key, k -> {
>         try {
>             return httpClient.send(req, BodyHandlers.ofString()).body();
>         } catch (IOException | InterruptedException e) {
>             throw new RuntimeException(e);
>         }
>     });
> }
>
> // Диагностика pinning при старте JVM
> // java -Djdk.tracePinnedThreads=full -jar app.jar
> // → стандартный вывод покажет stack trace всех pinned VT
> ```
>
> **Когда применять:**
> - Аудит legacy-кода при включении `spring.threads.virtual.enabled=true` — Spring Framework 6.1 сам не делает миграцию, нужно вручную пройтись по `synchronized`.
> - При импорте библиотек: проверить `jdeps` или JFR profile, есть ли `synchronized + I/O` в зависимостях (старый Apache HttpClient 4.x, jackson при синхронной записи).
> - JFR events `jdk.VirtualThreadPinned` в Mission Control — production-мониторинг pinning без перезапуска с флагом.
>
> **Подводные камни:**
> - `ReentrantLock` не reentrant между разными классами — рефакторинг shared mutex требует осторожности с try/finally (lock без unlock = вечно заблокировано).
> - `ConcurrentHashMap.computeIfAbsent` не должен делать модификацию того же ключа изнутри лямбды — `IllegalStateException` или deadlock.
> - `static synchronized` методы pin-ят на class monitor — это особенно коварно, потому что lock «невидимый».
> - JEP 491 (target Java 24/25) убирает pinning для `synchronized`, но native-методы и `Object.wait()` под мониторами остаются — миграция не 100% устаревает.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q3]] — carrier thread, который и заклинивается при pinning; [[java-virtual-threads-interview#Q5]] — почему `synchronized` в списке «не подходит для VT»; [[java-virtual-threads-interview#Q11]] — как диагностировать pinning через JFR/tracePinnedThreads.

## Q5. Для каких задач Virtual Threads подходят, а для каких нет?

**Подходят:**
- I/O-bound задачи: HTTP-запросы к внешним API, чтение из БД, файловые операции.
- Серверы с высоким параллелизмом и blocking I/O (один поток на запрос).
- Замена ThreadPoolExecutor с большим числом потоков.

**Не подходят:**
- CPU-bound задачи — нет преимущества над ForkJoinPool/ParallelStream.
- Код с активным использованием `synchronized` — pinning нивелирует эффект.
- Реактивное программирование (WebFlux) — там уже non-blocking I/O, VT избыточны.


> [!mcq]
>
> **Вопрос:** У вас сервис парсит изображения (resize + JPEG-кодирование) на ~50K запросов в минуту. Стоит ли мигрировать на Virtual Threads с текущего `ThreadPoolExecutor(corePool=200)`?
>
> ---
>
> #### A) Да, заменить на `newVirtualThreadPerTaskExecutor()` — миллион VT параллельно — изображения обработаются быстрее за счёт scheduler-а — ❌ Неверно
>
> **Что на самом деле:** Image resize и JPEG-кодирование — это **CPU-bound** задачи (libjpeg, ImageIO внутри JDK). VT помогают только если поток *блокируется* (I/O, sleep, lock wait). Если поток крутит CPU 100ms, увеличение числа VT не даст ничего: carrier-ы всё равно ограничены ядрами CPU.
>
> **Откуда путаница:** маркетинг Loom часто звучит как «миллион потоков параллельно». Это правда для I/O-bound, но создаёт неверный посыл «VT всегда быстрее».
>
> **Если бы это было правдой:** на 16-core машине миллион VT крутили бы CPU параллельно. Закон Амдала + физика: ядро может выполнять одну инструкцию за раз. Реальный benchmark показал бы тот же throughput при context-switch overhead.
>
> ---
>
> #### B) Нет, оставить Platform Threads с пулом `~CPU cores × 2`; VT не дают преимуществ на CPU-bound, и создание миллионов VT при CPU-нагрузке только увеличит context-switching — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Virtual Threads решают одну конкретную проблему: blocking I/O в большом числе параллельных контекстов. Carrier threads — это `ForkJoinPool` с параллелизмом = CPU cores. Если ваш код CPU-bound, carrier-ы постоянно заняты, у вас нет блокировок → VT не имеет шанса unmount → выгоды нет.
>
> Правило большого пальца: посчитать **процент времени в blocking-вызовах** на запрос. Если >60% времени блокировка (HTTP, JDBC, file I/O, Kafka send) — VT даст значительный буст. Если <20% (CPU-числодробилка, ML inference, image codec) — оставить Platform Thread pool ≈ CPU × 1.5..2.
>
> Для смешанной нагрузки правильный паттерн — разделить executor-ы: CPU-task pool отдельно (Platform), I/O-task pool отдельно (Virtual).
>
> **Пример:**
> ```java
> @Configuration
> public class ExecutorConfig {
>
>     // CPU-bound: парсинг изображений, ML, шифрование
>     @Bean
>     public ExecutorService cpuBoundExecutor() {
>         int cores = Runtime.getRuntime().availableProcessors();
>         return new ThreadPoolExecutor(
>             cores, cores * 2,                          // size = CPU bound
>             60L, TimeUnit.SECONDS,
>             new LinkedBlockingQueue<>(1000),           // backpressure через очередь
>             Thread.ofPlatform().name("cpu-", 0).factory(),
>             new ThreadPoolExecutor.CallerRunsPolicy() // backpressure: при переполнении вызывающий поток сам делает работу
>         );
>     }
>
>     // I/O-bound: HTTP-вызовы, JDBC, Kafka
>     @Bean
>     public ExecutorService ioBoundExecutor() {
>         return Executors.newVirtualThreadPerTaskExecutor();
>     }
> }
>
> // Использование
> public Image processOrder(OrderId id) {
>     // Шаг 1: I/O — fetch metadata из БД (VT)
>     var metadata = CompletableFuture.supplyAsync(
>         () -> repo.findMetadata(id), ioBoundExecutor);
>     // Шаг 2: CPU — resize + JPEG encode (Platform)
>     return metadata.thenApplyAsync(this::resizeAndEncode, cpuBoundExecutor).join();
> }
> ```
>
> **Когда применять:**
> - Видеотранскодер (Twitch, YouTube ingest) — FFmpeg-калькуляция, Platform Threads с CPU-affinity.
> - ML inference сервис (Triton, TF Serving) — Platform с фиксированным пулом ≈ GPU count.
> - Криптография/JWT signing с RSA — CPU-bound, Platform Threads.
> - Сжатие/распаковка (gzip, zstd) — Platform Threads, иначе carrier-ы стоят.
>
> **Подводные камни:**
> - «Слегка blocking» CPU-задачи (Drop SQLite query на 5ms внутри 100ms CPU-work) могут с VT работать лучше — измеряй, не угадывай.
> - Кэширование результата (Caffeine) внутри CPU-задачи делает её «горячую» часть CPU и «холодную» I/O — может потребоваться разделение фаз.
> - JIT-предупреждение: до C2 компиляции (тысячи итераций) ваш image-resize в 5× медленнее. CPU-bound в начале — это interpreted CPU, не помещение Loom.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q4]] — pinning как ещё одно «не подходит» (synchronized + I/O); [[java-virtual-threads-interview#Q13]] — почему VT не нужен пул, и противоположно тому, что Platform нужен; [[java-virtual-threads-interview#Q14]] — сравнение с WebFlux (тоже не помогает CPU-bound).
>
> ---
>
> #### C) Да, потому что VT дешевле в создании (~1 мкс) — overhead исчезнет — ❌ Неверно
>
> **Что на самом деле:** Stoимость **создания** потока — это разовая сумма. Если задача занимает 100ms CPU-работы, разница между «start платформенного потока за 100мкс» vs «start VT за 1мкс» — 0.1% от общего времени. Bottleneck не в создании, а в CPU-bound вычислении.
>
> **Откуда путаница:** часто цитируется «VT в 100× дешевле в создании» из JEP 444. Это правда, но статистически не значимо, когда задача длинная.
>
> **Если бы это было правдой:** все ML-фреймворки уже мигрировали бы на VT. На деле TensorFlow Java, Tribuo используют Platform Threads с affinity к ядрам.
>
> ---
>
> #### D) Зависит от настройки `-Djdk.virtualThreadScheduler.parallelism`, если поставить >100, VT обгонят Platform — ❌ Неверно
>
> **Что на самом деле:** parallelism scheduler-а ограничивает **carrier-ов** — Platform Threads, которые носят VT. Установка parallelism=100 на 16-core машине создаст 100 carrier-ов поверх 16 ядер, что приведёт к context-switch storm и деградации throughput на CPU-bound нагрузке.
>
> **Откуда путаница:** существует флаг, и кажется логичным «больше параллелизм = быстрее». На I/O-bound это иногда помогает (если pinning заклинивает carrier-ов), на CPU-bound — нет.
>
> **Если бы это было правдой:** все JVM приложения тюнили бы parallelism вверх. JDK документация прямо рекомендует НЕ менять без замера: «start with the default and only increase if you observe carrier starvation under load».

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


> [!mcq]
>
> **Вопрос:** В Spring Boot 3.2+ установка `spring.threads.virtual.enabled=true` — какие компоненты переключаются на Virtual Threads автоматически, а какие нет?
>
> ---
>
> #### A) Все ExecutorService в context, включая ваши кастомные `@Bean ExecutorService` — ❌ Неверно
>
> **Что на самом деле:** Spring переключает только свои инфраструктурные executor-ы: embedded Tomcat (`TomcatProtocolHandler`), `taskExecutor` (для `@Async` без явного qualifier), `taskScheduler` (для `@Scheduled`). Ваши кастомные `@Bean ExecutorService myPool` остаются как объявлены — Spring не патчит произвольные beans.
>
> **Откуда путаница:** `spring.threads.virtual.enabled` звучит «глобально». На деле это специфичный autoconfiguration, проверяющий каждое место Spring точечно.
>
> **Если бы это было правдой:** существующий `@Bean newFixedThreadPool(50)` неожиданно стал бы `newVirtualThreadPerTaskExecutor()` — изменилась бы семантика backpressure (нет лимита), что сломало бы production. Spring сознательно ограничивает scope.
>
> ---
>
> #### B) Только Tomcat HTTP worker — а `@Async` и `@Scheduled` нужно отдельно настраивать через `@Configuration` — ❌ Неверно
>
> **Что на самом деле:** Spring Boot 3.2+ переключает Tomcat **и** автоконфигурируемые `applicationTaskExecutor` / `taskScheduler`, которые лежат под `@Async` и `@Scheduled` соответственно. Отдельной настройки не нужно.
>
> **Откуда путаница:** в Spring Boot 3.1 и ранее (до GA Virtual Threads) `@Async` действительно требовал ручного `AsyncTaskExecutor` bean.
>
> **Если бы это было правдой:** недокументированная асимметрия, требующая дополнительной конфигурации. Spring Boot release notes 3.2 явно перечисляют все три места.
>
> ---
>
> #### C) Tomcat HTTP worker (один VT на запрос), `applicationTaskExecutor` (для `@Async`), `taskScheduler` (для `@Scheduled`). Reactive (WebFlux), Kafka listener container threads и кастомные пулы — не трогаются автоматически — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Boot 3.2 (на базе Spring Framework 6.1) добавил `VirtualThreadTaskExecutor` и автоконфигурацию `ThreadsVirtualAutoConfiguration` (под `@ConditionalOnProperty(name = "spring.threads.virtual.enabled")` + Java 21+). Эта конфигурация:
> 1. Меняет Tomcat protocol handler executor на `VirtualThreadTaskExecutor` — каждый HTTP запрос обрабатывается своим VT.
> 2. Регистрирует `applicationTaskExecutor` как `VirtualThreadTaskExecutor` — используется `@Async` методами и `AsyncConfigurer`.
> 3. Регистрирует `taskScheduler` как `SimpleAsyncTaskScheduler` с virtual=true — `@Scheduled` запускается в VT.
>
> Что НЕ переключается автоматически:
> - **WebFlux/Reactor**: бессмысленно, у него Netty event loop, не blocking — VT там не нужны.
> - **Kafka listener container threads**: `ConcurrentMessageListenerContainer` имеет свой scheduler. Нужно `setConsumerTaskExecutor(...)` ручками.
> - **Любой кастомный `@Bean ExecutorService`** — Spring не патчит произвольные beans.
> - **`@Async("customExecutor")`** с явным qualifier — использует ваш bean «customExecutor», не трогается.
> - **Embedded Jetty/Undertow**: для Tomcat есть прямая интеграция, для других — нужны их customizer-ы (Jetty: `Server.setThreadPool` с virtual factory).
>
> **Пример:**
> ```yaml
> # application.yml
> spring:
>   threads:
>     virtual:
>       enabled: true   # JDK 21 required, иначе ConditionFailedException
> ```
>
> ```java
> @Service
> public class OrderService {
>
>     @Async                                    // → VT (applicationTaskExecutor)
>     public CompletableFuture<Order> fetchAsync(OrderId id) { ... }
>
>     @Async("customExecutor")                  // → НЕ VT, ваш bean
>     public void slowCleanup() { ... }
>
>     @Scheduled(fixedDelay = 5000)             // → VT (taskScheduler)
>     public void healthCheck() { ... }
> }
>
> @Configuration
> public class KafkaConfig {
>     // Spring НЕ переключает Kafka listener сам — нужно вручную
>     @Bean
>     public ConcurrentKafkaListenerContainerFactory<String, String> factory(...) {
>         var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
>         factory.getContainerProperties().setListenerTaskExecutor(
>             new VirtualThreadTaskExecutor("kafka-vt-")
>         );
>         return factory;
>     }
> }
> ```
>
> **Когда применять:**
> - Любой Spring Boot 3.2+ REST API с blocking JDBC (Hikari) и/или blocking HTTP downstream — почти всегда win.
> - Микросервис-агрегатор: входящий запрос -> 10 параллельных вызовов через `RestClient`/`RestTemplate` — каждый в своём VT.
> - Замена `WebClient.block()` антипаттернов в legacy сервисах.
> - Старые синхронные приложения, которым нужен буст throughput без переписывания на reactive.
>
> **Подводные камни:**
> - Включение `spring.threads.virtual.enabled` на Java 17/20 приведёт к startup-ошибке (нет API VT). Build конфигурация должна гарантировать Java 21.
> - Tomcat keep-alive thread (для idle connections) остаётся platform — это маленький поток-смотритель.
> - `@Transactional` с PROPAGATION_REQUIRES_NEW в `@Async` методе на VT — транзакция корректна, но связка с ThreadLocal-based транзакционным контекстом значит, что миллионы VT держат миллионы `TransactionSynchronizationManager` записей — память.
> - Hikari `maximum-pool-size` не нужно увеличивать с включением VT — bottleneck не в потоках, а в коннекциях.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — модель VT, которую Spring и оборачивает; [[java-virtual-threads-interview#Q5]] — почему нет смысла включать на CPU-bound сервисах; [[java-virtual-threads-interview#Q10]] — взаимодействие с HikariCP при включённом VT.
>
> ---
>
> #### D) Перезаписывает `spring.task.execution.pool.core-size` на 0 и Tomcat на VT — нужна перезагрузка JVM — ❌ Неверно
>
> **Что на самом деле:** Конфигурационные свойства `spring.task.execution.*` относятся к `ThreadPoolTaskExecutor`. При включении `spring.threads.virtual.enabled=true` Spring **игнорирует** эти свойства и собирает `VirtualThreadTaskExecutor`, у которого нет понятия pool-size. Перезагрузка JVM нужна (это application.yml), но связь с свойствами размера — иллюзия.
>
> **Откуда путаница:** Spring задокументированы оба сразу в `application.yml`, и кажется, что они взаимодействуют.
>
> **Если бы это было правдой:** `spring.task.execution.pool.queue-capacity=0` отбрасывал бы задачи через `RejectedExecutionException`. На деле включение VT убирает понятие queue вообще.

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


> [!mcq]
>
> **Вопрос:** Что предпочтительнее использовать для request-scoped контекста (userId, traceId) в коде на Virtual Threads и почему?
>
> ---
>
> #### A) `ScopedValue` — immutable, лексически ограничен областью `where(...).run(...)`, не наследуется в дочерние потоки автоматически (только через `StructuredTaskScope`); эффективнее ThreadLocal на миллионах VT — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `ScopedValue<T>` (JEP 446, preview Java 21, целевой standard Java 23-25) — это новая абстракция для одно-узловой передачи неизменяемых данных. Отличия от `ThreadLocal`:
>
> 1. **Immutability**: значение установлено один раз через `ScopedValue.where(KEY, value).run(body)` и нельзя поменять внутри body. Это устраняет class бага «значение неожиданно изменилось».
> 2. **Лексический scope**: значение доступно только пока выполняется `body`. После выхода из `run(...)` — `ScopedValue.get()` бросает `NoSuchElementException`. У `ThreadLocal` значение «навсегда» в потоке, нужен ручной `remove()`.
> 3. **Память**: ScopedValue хранится в stack-frame (через bindings chain), не в `Thread.threadLocals`. На миллион VT — значительная экономия (нет N×M массивов `ThreadLocalMap`).
> 4. **Inheritance**: ScopedValue НЕ наследуется автоматически дочерним VT (как `InheritableThreadLocal`). Но `StructuredTaskScope.fork()` передаёт bindings явно — это design choice, чтобы избежать незаметного утекания.
>
> **Пример:**
> ```java
> public final class RequestContext {
>     public static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();
>     public static final ScopedValue<UserId> USER = ScopedValue.newInstance();
> }
>
> // Servlet filter — обёртывает запрос
> public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
>     var traceId = req.getHeader("X-Trace-Id");
>     var user = authenticate(req);
>     ScopedValue
>         .where(RequestContext.TRACE_ID, traceId)
>         .where(RequestContext.USER, user)
>         .run(() -> chain.doFilter(req, resp));
>     // После run(): get() уже бросит NoSuchElementException — никаких ThreadLocal leak
> }
>
> // Глубоко в стеке — доступ без передачи параметра
> @Repository
> public class OrderRepository {
>     public Order findById(OrderId id) {
>         log.info("[trace={}] fetching order {}", RequestContext.TRACE_ID.get(), id);
>         return jdbcTemplate.queryForObject(...);
>     }
> }
>
> // Параллельные подзадачи через StructuredTaskScope — bindings наследуются
> try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
>     scope.fork(() -> repo1.fetch());  // имеет доступ к TRACE_ID, USER
>     scope.fork(() -> repo2.fetch());  // тоже имеет доступ
>     scope.join().throwIfFailed();
> }
> ```
>
> **Когда применять:**
> - Spring Boot 3.2+ с VT — заменить `RequestContextHolder` на ScopedValue для traceId/userId.
> - MDC-логирование без `ThreadLocal` (Logback-jdk21 имеет ScopedValue-aware MDC adapter).
> - Security context в Spring Security — `SecurityContextHolder.STRATEGY_VIRTUAL_THREAD` (Spring 6.2+) уже использует ScopedValue.
> - Любой код, где раньше использовался `InheritableThreadLocal` — он переезжает в ScopedValue + StructuredTaskScope.
>
> **Подводные камни:**
> - ScopedValue в Java 21 — preview API; нужен флаг `--enable-preview`. В Java 23+ стабилизирован.
> - НЕ работает с `Executors.newVirtualThreadPerTaskExecutor().submit(...)` — bindings НЕ наследуются. Только `StructuredTaskScope.fork(...)` наследует. Это сознательное ограничение для безопасности.
> - Существующие библиотеки (старая Hikari, Micrometer Tracing 1.x) всё ещё используют ThreadLocal — миграция не моментальная.
> - `ScopedValue.get()` без active binding — `NoSuchElementException`, не `null`. Это другая семантика, чем у ThreadLocal.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — модель VT, где ThreadLocal становится дорогим; [[java-virtual-threads-interview#Q8]] — StructuredTaskScope, который наследует ScopedValue bindings; [[java-virtual-threads-interview#Q11]] — диагностика thread state с ScopedValue.
>
> ---
>
> #### B) `ThreadLocal` — он по-прежнему оптимальный выбор, проверенный с 2003 года; в VT работает без изменений — ❌ Неверно
>
> **Что на самом делe:** `ThreadLocal` функционально работает с VT, но имеет конкретные проблемы при миллионах VT: каждый VT держит свой `ThreadLocalMap` (внутренний массив), что суммарно может занять ГБ памяти. Plus нет lexical scope: после завершения работы VT нужен `ThreadLocal.remove()`, иначе утекает в reference из ScopedValue → memory pressure.
>
> **Откуда путаница:** ThreadLocal работает, JEP 444 явно не запрещает. Документация подсказывает «избегайте на VT», но не блокирует.
>
> **Если бы это было правдой:** не было бы JEP 446 (ScopedValue). Авторы Loom потратили годы на новое API именно потому что ThreadLocal не масштабируется на VT.
>
> ---
>
> #### C) `InheritableThreadLocal` — он сам передаёт значение в дочерние VT, лучшая совместимость — ❌ Неверно
>
> **Что на самом деле:** `InheritableThreadLocal` имеет ту же проблему памяти. Plus в VT-моделе наследование происходит ЛЕНИВО — при создании VT копируется reference к родительскому Map (через `Thread.inheritedAccessControlContext`). На миллионах VT это создаёт глубокие цепочки, GC долго ходит.
>
> **Откуда путаница:** `InheritableThreadLocal` исторически — единственный способ передать контекст в новые потоки, и кажется идеальным для fan-out.
>
> **Если бы это было правдой:** Spring Security не двигалась бы на ScopedValue strategy. JDK документация прямо предупреждает: «InheritableThreadLocal can cause memory issues with large numbers of virtual threads».
>
> ---
>
> #### D) Передавать через явный параметр метода (`UserId user`) — это единственно правильный способ — ❌ Неверно
>
> **Что на самом деле:** Явная передача параметром — допустимый стиль, но для cross-cutting concerns (traceId, security, locale) она требует менять сигнатуры тысячи методов. Это не «единственно правильный», а трудоёмкий способ.
>
> **Откуда путаница:** в FP-стиле (Haskell Reader monad, Scala implicit) принято передавать контекст явно. Можно ошибочно перенести на Java.
>
> **Если бы это было правдой:** Spring `@Transactional`, Micrometer `@Timed`, Spring Security всё бы развалилось — они все используют thread-local-style propagation. Это очень глубоко в экосистеме Java.

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


> [!mcq]
>
> **Вопрос:** Какое главное преимущество Structured Concurrency (`StructuredTaskScope`, JEP 453) над использованием `CompletableFuture.allOf(...)` для параллельных задач?
>
> ---
>
> #### A) `StructuredTaskScope` работает быстрее `CompletableFuture` благодаря отсутствию executor — ❌ Неверно
>
> **Что на самом деле:** `StructuredTaskScope` тоже использует executor (по умолчанию — VT factory). Производительность сопоставима — главное отличие НЕ в скорости, а в **lifecycle гарантиях**. `CompletableFuture` ещё после ~10 лет оптимизаций — один из самых быстрых async API в JDK.
>
> **Откуда путаница:** новое API часто маркетингово позиционируется как «быстрее». На деле Loom об упрощении модели, не о скорости.
>
> **Если бы это было правдой:** JEP 453 спецификация фокусировалась бы на perf. Реально она про «error propagation, cancellation, observability».
>
> ---
>
> #### B) `StructuredTaskScope` автоматически использует Project Reactor под капотом для backpressure — ❌ Неверно
>
> **Что на самом деле:** `StructuredTaskScope` — часть JDK, никакой зависимости от Reactor. Это родной API на VT. Backpressure для параллельных задач решается семафорами или ограничителями concurrency, а не Reactor-у.
>
> **Откуда путаница:** в reactive экосистеме Reactor — синоним «правильной» обработки async. Можно ошибочно ожидать, что новое API использует его.
>
> **Если бы это было правдой:** Spring Boot тащил бы Reactor как dependency для использования `StructuredTaskScope`. Никакой такой зависимости нет.
>
> ---
>
> #### C) `StructuredTaskScope` позволяет создавать task-graphs с зависимостями (`thenCompose`-style) на VT — ❌ Неверно
>
> **Что на самом деле:** `StructuredTaskScope` рассчитан на **независимые параллельные подзадачи в одной точке fan-out**. Цепочки зависимостей (B зависит от A) делаются обычным sequential кодом в VT (просто блокирующий вызов A, затем B). Никаких `thenCompose`-операторов нет в API.
>
> **Откуда путаница:** `CompletableFuture` имеет богатый composition API (`thenApply`, `thenCompose`, `thenCombine`), хочется ожидать его и здесь.
>
> **Если бы это было правдой:** API содержал бы методы типа `scope.fork(taskA).then(taskB)`. Реально только `scope.fork(...)` для параллельных задач + обычный JDK `Future.get()` для последовательных.
>
> ---
>
> #### D) Чёткий lifecycle: все дочерние задачи гарантированно завершаются до выхода из try-with-resources блока; при ошибке в одной — остальные автоматически отменяются (`ShutdownOnFailure`); stack trace дочерних задач связан с родительским (parent-child observability) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `CompletableFuture.allOf(f1, f2, f3)` имеет **unstructured** lifecycle: если task f1 упал, f2/f3 продолжают выполняться (тратя ресурсы), и без явного cancel остаются висеть. Если родительский поток умер по InterruptedException — futures продолжают работать «оторванно». Stack trace дочерних tasks — отдельный от родительского, debug-сложно.
>
> `StructuredTaskScope` (JEP 453, preview Java 21, target Java 25 standard) даёт **гарантии**:
>
> 1. **Subtask leakage невозможен**: при выходе из `try-with-resources` блок `close()` ждёт ВСЕ subtasks. Если код сделал `return` или бросил exception — все subtasks отменяются и блок дожидается их termination перед возвратом.
> 2. **Error propagation**: `ShutdownOnFailure.throwIfFailed()` бросает исключение первой упавшей подзадачи, отменяя остальные. `ShutdownOnSuccess` — наоборот, при первом успехе отменяет остальные (race pattern для fetch-from-mirrors).
> 3. **Cancellation cascade**: если родительский VT прерван (`Thread.interrupt()`), scope автоматически прерывает все subtasks через `Thread.interrupt()` на каждом fork-нутом VT.
> 4. **Observability**: `jcmd Thread.dump_to_file -format=json` показывает parent-child связи между VT — видно, какие задачи запущены из какого scope. У `CompletableFuture` этого нет.
>
> **Пример:**
> ```java
> // Anti-pattern: CompletableFuture leak при exception
> CompletableFuture<User> userF = CompletableFuture.supplyAsync(() -> fetchUser(id));
> CompletableFuture<List<Order>> ordersF = CompletableFuture.supplyAsync(() -> fetchOrders(id));
> // Если в этой точке выкинуть exception — userF и ordersF продолжат работать orphaned!
> validate(id);  // ← throws, futures висят
> return CompletableFuture.allOf(userF, ordersF).thenApply(_ -> ...);
>
> // Structured: всё корректно
> try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
>     StructuredTaskScope.Subtask<User> user = scope.fork(() -> fetchUser(id));
>     StructuredTaskScope.Subtask<List<Order>> orders = scope.fork(() -> fetchOrders(id));
>
>     scope.join()              // ждём обе
>          .throwIfFailed();    // первая упавшая → exception, вторая cancelled
>
>     return new UserProfile(user.get(), orders.get());
> } // close() здесь гарантирует: НИ ОДНА subtask не утекла за пределы блока
>
> // Race pattern: запросить из 3 mirrors, использовать первый ответ
> try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
>     scope.fork(() -> fetchFromMirror("us-east"));
>     scope.fork(() -> fetchFromMirror("eu-west"));
>     scope.fork(() -> fetchFromMirror("ap-south"));
>
>     return scope.join().result();  // первый успех; остальные two cancel
> }
> ```
>
> **Когда применять:**
> - Backend-агрегатор: на каждый запрос делает fan-out к 5-20 микросервисам и ждёт все — Netflix, Uber pattern.
> - Race pattern для CDN/mirror fallback (как в примере выше).
> - GraphQL resolvers с параллельным fetch dependent resources.
> - Replace existing `CompletableFuture.allOf(...).join()` блоки — миграция почти 1:1.
>
> **Подводные камни:**
> - В Java 21 — preview, нужен `--enable-preview` и `--add-modules jdk.incubator.concurrent` (зависит от версии JEP). В Java 25 — стандарт.
> - `scope.join(Duration.ofSeconds(5))` для overall timeout — если забыть, по умолчанию ждёт неограниченно. Для production обязательно ставить timeout.
> - `fork(...)` не наследует `ThreadLocal` (как и любые child VT). Использовать `ScopedValue` (см. [[java-virtual-threads-interview#Q7]]).
> - Один scope — одни ошибки policy: для разных подзадач с разной обработкой нужны разные scopes (можно вложенные).
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — VT, на которых работает Structured Concurrency; [[java-virtual-threads-interview#Q7]] — ScopedValue, который наследуется через `fork`; [[java-virtual-threads-interview#Q9]] — сравнение с `coroutineScope` в Kotlin (та же идея).

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


> [!mcq]
>
> **Вопрос:** Какое ключевое отличие на уровне реализации между Virtual Threads и Kotlin Coroutines, влияющее на совместимость с существующими Java-библиотеками?
>
> ---
>
> #### A) VT и coroutines одинаково внутри устроены — обе используют CPS-трансформацию байткода — ❌ Неверно
>
> **Что на самом деле:** Это два совершенно разных подхода. VT — runtime механизм (continuation на уровне JVM), без преобразования байткода. Kotlin coroutines — compiler transformation: `suspend fun` функции компилируются в state machine с continuation-passing style (CPS). Разные слои абстракции.
>
> **Откуда путаница:** обе механики «приостанавливают и возобновляют» поток выполнения, и используют слово continuation. Можно предположить общую реализацию.
>
> **Если бы это было правдой:** kotlinx.coroutines можно было бы использовать с Java-only кодом без особого runtime. На деле нужен `kotlin-stdlib` для continuation runtime.
>
> ---
>
> #### B) VT — это JVM-feature на уровне runtime, работающая с ЛЮБЫМ jar-ом без перекомпиляции; Kotlin coroutines — compile-time CPS-трансформация, требующая `suspend` keyword на каждой блокирующей функции и kotlinx-aware библиотек — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Это самое фундаментальное различие, определяющее экосистемные последствия:
>
> **Virtual Threads (JEP 444, runtime):**
> - Существующие jar-ы (Hikari, jdbc-драйвера, Apache HttpClient, Jetty servlet, Spring MVC) работают **из коробки**. Нужно только Java 21+ JVM.
> - Метод `Thread.sleep(...)`, `Socket.read()`, `LockSupport.park()` внутри определяют контекст исполнения — JVM знает, что текущий поток виртуальный, и unmount-ит его.
> - Стек VT хранится в heap как `Continuation` объект. При unmount: сохранили в heap, освободили carrier. При mount: ресторнули frames на carrier.
> - Нет «функционального окрашивания» (function coloring) — обычная Java функция работает на VT.
>
> **Kotlin Coroutines (kotlinx.coroutines, compile-time):**
> - Функция должна быть помечена `suspend`. Без этого нельзя вызвать `delay()`, `Channel.receive()` и пр.
> - Компилятор переписывает `suspend fun` в state machine: каждая точка возможного suspend становится case в switch, plus extra параметр `Continuation<T>`.
> - Для совместимости с blocking Java API нужны wrappers (`Dispatchers.IO`, `withContext`), которые delegateся пулу платформенных потоков.
> - **Function coloring**: обычная функция не может вызвать suspend без `runBlocking{...}` (что тащит весь стек платформенным потоком). Это вирусное свойство — попало в один уровень, ползёт вверх по стеку.
>
> **Пример:**
> ```kotlin
> // Kotlin: suspend "красит" функцию
> suspend fun fetchUser(id: Long): User {
>     delay(100)                            // suspend point
>     return httpClient.get("/users/$id")   // НЕ suspend (если client не coroutine-aware)
> }
>
> // Чтобы вызвать suspend из обычной Java — runBlocking { ... }, что блокирует поток
> public class JavaCaller {
>     public User load(long id) {
>         return BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
>             (scope, cont) -> fetchUser(id, cont));  // блокирует JVM поток
>     }
> }
> ```
>
> ```java
> // Java VT: никаких suspend, обычный код
> public User fetchUser(long id) {
>     Thread.sleep(100);                          // unmount happens here
>     return httpClient.send(req, ofString());    // unmount happens here too
> }
>
> // Вызвать из любого Java кода — ZERO ceremony
> try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
>     var future = executor.submit(() -> fetchUser(42));
> }
> ```
>
> **Когда применять:**
> - Greenfield Spring Boot 3.2+ на Java 21: VT — однозначно проще миграция, чем перевод на kotlinx.coroutines.
> - Большие legacy Java codebases (банки, телеком): VT — drop-in replacement, coroutines требуют переписывания.
> - В KMP (Kotlin Multiplatform) или Android — coroutines стандарт, VT недоступны (Android Dalvik/ART не имеет Loom).
> - Mixed Kotlin/Java сервис: coroutines на Kotlin-сторонe, VT на Java-стороне, через `runBlocking(Dispatchers.IO)` interop.
>
> **Подводные камни:**
> - VT всё ещё страдают от `synchronized` pinning ([[java-virtual-threads-interview#Q4]]); coroutines от этого иммунны (нет JNI/monitor проблемы).
> - kotlinx.coroutines имеет уже структурированный concurrency через `coroutineScope`/`supervisorScope` много лет — Java получила это только в JEP 453 (preview).
> - Coroutines дают cancellation cooperatively (через `CancellationException` на suspend points). VT — через `Thread.interrupt()`, что не все JDK методы корректно обрабатывают.
> - Performance: на чистом I/O-bound коде производительность сравнима. Coroutines чуть выигрывают в overhead на CPU-bound тестах из-за отсутствия mount/unmount цикла carrier-ов.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — модель VT и mount/unmount; [[java-virtual-threads-interview#Q4]] — pinning, которого нет в coroutines; [[java-virtual-threads-interview#Q14]] — сравнение с reactive (третий подход в этом пространстве).
>
> ---
>
> #### C) Coroutines работают только в Android, а VT — только на JVM 21+ — ❌ Неверно
>
> **Что на самом деле:** Kotlin coroutines работают везде где есть Kotlin runtime: JVM (любая версия с Kotlin), Native (Kotlin/Native), JS (Kotlin/JS), Android. VT — только JVM 21+. Утверждение про Android-only для coroutines просто неверно — Ktor сервера на JVM Kotlin активно используют coroutines.
>
> **Откуда путаница:** Kotlin coroutines очень популярны в Android (стандартный async API через Room, Retrofit). Но это не ограничение runtime.
>
> **Если бы это было правдой:** kotlinx.coroutines в Spring Boot применений не существовало. По факту есть `spring-webflux + Kotlin coroutines extension`, и Ktor.
>
> ---
>
> #### D) VT поддерживают `async/await` синтаксис, идентичный coroutines — ❌ Неверно
>
> **Что на самом деле:** В Java НЕТ keywords `async`/`await`. VT работают через обычные методы и блокирующий стиль — это и есть преимущество, нет нового синтаксиса. Это сознательное design-решение JEP 444: «избежать function coloring».
>
> **Откуда путаница:** C#, JavaScript, TypeScript, Kotlin — все имеют async/await. Java иногда обсуждался как «получит async/await». Финально пошли через VT.
>
> **Если бы это было правдой:** JEP 444 имел бы syntactic changes — но он чисто runtime. Любой существующий Java код компилируется как раньше.

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


> [!mcq]
>
> **Вопрос:** Сервис с включёнными VT (Spring Boot 3.2+) уперся в latency при ~500 параллельных запросах. Все запросы делают SELECT через JDBC. `maximum-pool-size` HikariCP = 20. Что произошло и как починить?
>
> ---
>
> #### A) JDBC-драйвер не поддерживает VT, нужен переход на R2DBC — ❌ Неверно
>
> **Что на самом деле:** Современные JDBC-драйверы (PostgreSQL JDBC 42.x, MySQL Connector/J 8.x, Oracle JDBC 21c+) корректно работают с VT — внутри они делают `Socket.read()`, который умеет unmount VT в JDK 21. R2DBC — это альтернативный reactive API, и он НЕ нужен при VT.
>
> **Откуда путаница:** «Reactive стек» (R2DBC + WebFlux) активно продвигался Spring 5.x как «решение для масштабирования». На VT эта проблема решается без перехода.
>
> **Если бы это было правдой:** Spring Boot 3.2 не предлагал бы VT для JDBC-стека. Реально пользователи получают тот же throughput с JDBC+VT, что и с R2DBC+WebFlux, но без сложности reactive API.
>
> ---
>
> #### B) Hikari pool slow — нужно установить `pool-name=HikariCP-VT` и `connection-init-sql` для warmup — ❌ Неверно
>
> **Что на самом деле:** Эти property не влияют на throughput. Bottleneck — `maximum-pool-size=20`. При 500 параллельных запросов первые 20 берут connection, остальные 480 VT ждут в `HikariCP.getConnection()` (с `LockSupport.park()` под капотом — VT корректно unmount, не блокируют carrier, но всё равно ЖДУТ). p99 latency = время ожидания свободной коннекции.
>
> **Откуда путаница:** Hikari имеет много настроек, и кажется, что одна из них магическая. На деле тюнинг pool-size решает 90% проблем.
>
> **Если бы это было правдой:** Hikari документация рекомендовала бы эти настройки в Loom-секции. Реально pool-size — единственное, что обсуждается.
>
> ---
>
> #### C) Pool слишком маленький — 500 VT конкурируют за 20 коннекций. Это анти-паттерн «VT много, ресурсов мало» — VT корректно unmount при ожидании коннекции, но всё равно блокируются логически. Решение: увеличить pool до уровня, выдерживаемого БД (50-100 для одной replica), и/или добавить external rate limiting через Semaphore — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Включение VT НЕ убирает физические ограничения downstream ресурсов. Если БД может обслужить 100 параллельных запросов (по своим CPU/IOPS), а VT в приложении 1000 — 900 VT будут просто стоять в очереди Hikari, что выглядит как deadlock или latency spike.
>
> Hikari использует `Semaphore`-подобную семантику: при `getConnection()` VT встаёт в очередь FIFO. Внутри `acquire()` вызывает `LockSupport.parkNanos()` — VT корректно unmount-ится с carrier (carrier берёт другой VT), но логически блокируется. p99 latency для запроса = (waiters в очереди / pool-size) × средний execution time.
>
> Правильная архитектура с VT — **pool-size = ёмкость downstream**:
> 1. Узнать у DBA максимум одновременных запросов к replica (обычно 50-200 для PostgreSQL).
> 2. Установить `maximum-pool-size` на этом уровне.
> 3. Перед каждым batch-fan-out — explicit semaphore на 30-50, чтобы не утопить downstream service.
> 4. Метрика `hikaricp_connections_pending` — количество VT в очереди. Если > 5 постоянно, надо или поднимать pool, или сжимать concurrency сверху.
>
> **Пример:**
> ```yaml
> # Правильная конфигурация HikariCP с VT
> spring:
>   datasource:
>     hikari:
>       maximum-pool-size: 50           # под ёмкость БД
>       minimum-idle: 10
>       connection-timeout: 5000        # критично: VT иначе ждут вечно
>       leak-detection-threshold: 10000 # ловить VT, забывшие release
> ```
>
> ```java
> // External rate limiting на критичный endpoint
> @Service
> public class CatalogService {
>     // Глобальный лимит — не пускаем больше 30 одновременных запросов к downstream
>     private final Semaphore downstreamLimit = new Semaphore(30);
>
>     public Product fetchProduct(ProductId id) throws InterruptedException {
>         downstreamLimit.acquire();
>         try {
>             return externalClient.get("/products/" + id);  // blocking HTTP
>         } finally {
>             downstreamLimit.release();
>         }
>     }
> }
>
> // Метрика для observability
> @Bean
> public MeterBinder hikariMetrics(DataSource ds) {
>     return registry -> {
>         var hikari = (HikariDataSource) ds;
>         registry.gauge("hikari.connections.pending",
>             hikari, h -> h.getHikariPoolMXBean().getThreadsAwaitingConnection());
>     };
> }
> ```
>
> **Когда применять:**
> - Любой VT-enabled Spring Boot сервис с JDBC: ОБЯЗАТЕЛЬНО посчитать pool-size под ёмкость БД.
> - Микросервис, делающий fan-out к 10+ downstream API — Semaphore на каждый downstream отдельно.
> - Замена `WebClient.block()` антипаттернов: вместо «всё на reactive» — оставить blocking, но грамотно лимитировать через Semaphore.
> - Доклад Marcus Hellberg (Vaadin) на JFokus 2024: «VT — это про равномерное распределение блокировок, не про их устранение».
>
> **Подводные камни:**
> - Не путать `maximum-pool-size` с числом VT. VT-у можно создать сколько угодно, но коннекций столько, сколько в пуле.
> - При `leak-detection-threshold` слишком маленьком — ложные срабатывания на медленных запросах. Ставить >2× p99 expected.
> - JDBC `Statement.executeQuery` может pin-нуть VT если внутри есть `synchronized` (в старых драйверах PostgreSQL 42.5- было). Обновляться до latest.
> - Если БД ушла в timeout, VT всё равно ждут — нужен `connection-timeout` и `socket-timeout` на JDBC URL.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q4]] — pinning, отдельный риск в JDBC-драйверах; [[java-virtual-threads-interview#Q5]] — VT не для CPU-bound, но JDBC blocking — идеальный кейс; [[java-virtual-threads-interview#Q13]] — Semaphore вместо thread pool как ограничитель.
>
> ---
>
> #### D) Carrier threads default параллелизм 4 — VT всё равно крутятся на 4 CPU и упираются в CPU — ❌ Неверно
>
> **Что на самом деле:** Bottleneck не CPU, а pool коннекций. JDBC-операция занимает ~10-50 мс на БД-стороне и < 1ms CPU в приложении на marshalling. 500 параллельных JDBC-запросов на 4-ядерной машине **должны** работать с VT (пул коннекций — единственное ограничение).
>
> **Откуда путаница:** Существует carrier parallelism, и звучит как ограничение. На I/O-bound нагрузке carrier-ы успевают обработать тысячи unmount/mount в секунду.
>
> **Если бы это было правдой:** на 4-ядерной машине было бы limited 4 параллельных запроса. Реально Spring Boot + VT обслуживает 30K rps на 4 ядрах при I/O-bound.

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


> [!mcq]
>
> **Вопрос:** На проде сервиса с миллионом VT (`jcmd <pid> Thread.print` зависает на 30 минут). Какой способ собрать актуальный thread dump для анализа?
>
> ---
>
> #### A) `jcmd <pid> Thread.dump_to_file -format=json /tmp/dump.json` — специально оптимизированная команда JDK 21 для дампов с миллионами VT в формате JSON — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> JDK 21 добавил `Thread.dump_to_file` именно из-за проблемы с `Thread.print` на большом числе VT. Ключевые отличия:
>
> 1. **Файловый вывод**: пишет в файл, не в stdout — нет проблем с буфером терминала/Argo/kubectl logs.
> 2. **JSON-формат**: каждый VT представлен как структура `{"tid": "...", "name": "...", "stack": [...]}`, парсится `jq`, `pandas`, любым tool. Plain text dump на миллион VT — 500MB+ нечитаемой простыни.
> 3. **Группировка по контейнерам**: dump группирует VT по `ThreadContainer` (StructuredTaskScope, Executor) — видно parent-child связи.
> 4. **Performance**: оптимизировано для большого числа потоков, выполняется за секунды (не минуты).
>
> Использование:
> - `-format=plain` (default) — текстовый thread dump в стиле jstack.
> - `-format=json` — JSON, лучший выбор для миллионов VT.
> - Файл указывается абсолютным путём; если файл существует — ошибка (защита от перезаписи).
>
> **Пример:**
> ```bash
> # 1. Получить PID JVM
> pid=$(jps | grep MyApp | awk '{print $1}')
>
> # 2. Сделать дамп в JSON
> jcmd $pid Thread.dump_to_file -format=json /tmp/dump-$(date +%s).json
>
> # 3. Анализ через jq — найти все VT, заблокированные на одном lock
> jq '.threadDump.threads[]
>     | select(.state == "WAITING")
>     | .stack[0:3]' /tmp/dump-*.json
>
> # 4. Топ-10 stack-traces по частоте — найти hot pattern
> jq -r '.threadDump.threads[]
>        | .stack[0:5]
>        | tostring' /tmp/dump-*.json \
>     | sort | uniq -c | sort -rn | head -10
>
> # 5. Pinned VT (через JFR события вместо thread dump)
> jcmd $pid JFR.start name=loom-debug \
>     settings=profile \
>     filename=/tmp/loom.jfr \
>     duration=60s
> # Внутри recording: события jdk.VirtualThreadPinned, jdk.VirtualThreadStart
>
> # 6. JIT-warm-up: после старта запросить дамп без подсчёта runtime
> # jstack PID > /dev/null 2>&1  # старый стиль — НЕ использовать на VT
> ```
>
> **Когда применять:**
> - Production troubleshooting Spring Boot с VT: подозрение на deadlock, leak (миллион VT в ожидании одного семафора).
> - Profile во время load-test: понять, что происходит при пике RPS — какой downstream/lock держит VT.
> - Анализ pinning: совместить с `-Djdk.tracePinnedThreads=full` JVM-флагом — у VT с pinned в дампе stack начинается с `synchronized`/native.
> - JFR `jdk.VirtualThreadPinned`, `jdk.VirtualThreadSubmitFailed`, `jdk.VirtualThreadStart/End` events — production-friendly, низкий overhead.
>
> **Подводные камни:**
> - `jstack <pid>` на JVM 21 с миллионом VT — нагрузка на JVM (стоп-зе-волд на dumping), p99 latency растёт пока dump идёт. Использовать JFR в production вместо ad-hoc jstack.
> - JSON-дамп файл может быть несколько ГБ — учитывать disk space в контейнере (`/tmp` часто 1-10 GB).
> - Visual VM, Mission Control умеют визуализировать VT, но при миллионах потоков GUI зависнет — анализировать только через CLI.
> - Старые APM-агенты (AppDynamics pre-23, New Relic pre-9) не понимают VT — показывают пустые stack или crash.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q4]] — pinning, диагностируемый через те же tools; [[java-virtual-threads-interview#Q12]] — расход создания миллионов VT (часто корень проблемы); [[java-virtual-threads-interview#Q1]] — VT модель, обуславливающая новые подходы к диагностике.
>
> ---
>
> #### B) `kill -3 <pid>` — SIGQUIT всегда работает для thread dump — ❌ Неверно
>
> **Что на самом деле:** `kill -3` запускает `Thread.print` в JVM, который и зависает на миллионе VT. Не решает корневую проблему — наоборот, выводит ту же простыню в STDOUT процесса (часто перенаправлен в Argo logs, забивает их и слепит просмотр).
>
> **Откуда путаница:** SIGQUIT — традиционный способ thread dump с 1990-х. На pre-Loom потоках всегда работал.
>
> **Если бы это было правдой:** не было бы JEP 425 о новом `dump_to_file` команде. JDK команда специально добавила альтернативу из-за `kill -3` проблем.
>
> ---
>
> #### C) Подключить YourKit/JProfiler через JMX и сделать снапшот — ❌ Неверно
>
> **Что на самом деле:** YourKit/JProfiler работают через JVMTI, и при миллионе VT интроспекция всех frames вешает JVM на минуты. Plus профайлер с GUI рисует список потоков — UI с миллионом строк зависает.
>
> **Откуда путаница:** Профайлеры — стандартный tool для troubleshooting. Но они оптимизированы под сотни-тысячи потоков, не миллионы.
>
> **Если бы это было правдой:** не было бы CFP докладов в 2024 «Profiling Loom in production» — реально все используют JFR + jq, не GUI-профайлеры.
>
> ---
>
> #### D) `jstack -l <pid>` — флаг `-l` оптимизирован для VT — ❌ Неверно
>
> **Что на самом деле:** Флаг `-l` в `jstack` показывает информацию о locks (long form). Не имеет специальной оптимизации для VT. На миллионе VT работает так же медленно/плохо, как `jstack` без флага.
>
> **Откуда путаница:** в `jcmd Thread.print` есть похожий флаг `-l`. Но он про locks, а не про VT-оптимизацию.
>
> **Если бы это было правдой:** в JEP 425 указали бы. Реально документация рекомендует `Thread.dump_to_file` именно как замену `jstack` для Loom.

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


> [!mcq]
>
> **Вопрос:** Какие реалистичные числа описывают memory footprint и cost создания Virtual Thread в JDK 21?
>
> ---
>
> #### A) ~1 MB на VT (как Platform Thread), но мульти-плексирование делает их «бесплатными» — ❌ Неверно
>
> **Что на самом деле:** VT занимают **существенно меньше** памяти, чем Platform Thread. Platform Thread имеет фиксированный native стек ~512 KB - 8 MB (зависит от `-Xss`). VT хранит свой стек как `Continuation` объект в JVM heap, обычно 256-1024 байт при простой задаче, до нескольких KB на сложной call-chain.
>
> **Откуда путаница:** «Поток ~1 MB» — заученный факт про OS-потоки. Переносится на VT по аналогии.
>
> **Если бы это было правдой:** 1M VT × 1MB = 1 TB heap, что невозможно. Реально 1M VT занимают 5-20 GB heap (зависит от глубины стека), что укладывается в типичный production JVM.
>
> ---
>
> #### B) Создание VT занимает 100 мс — поэтому пул всё равно нужен для warm-up — ❌ Неверно
>
> **Что на самом деле:** Создание VT занимает ~1 микросекунду (5-10× быстрее `new Object()` при learnt allocation paths). Это в 100-1000 раз быстрее, чем Platform Thread. 1M VT за 1-2 секунды — задокументированный benchmark.
>
> **Откуда путаница:** Platform Thread создание действительно стоит ~100 мкс (нужно syscall к ОС, аллокация стека). Если перепутать единицы (мкс vs мс) и забыть про разницу с VT — получится «слишком долго».
>
> **Если бы это было правдой:** один из главных аргументов Loom («миллионы потоков») отвалился бы — нельзя было бы быстро их создавать.
>
> ---
>
> #### C) VT — это `WeakReference` объекты, GC их освобождает автоматически — ❌ Неверно
>
> **Что на самом деле:** Активный VT — это сильная ссылка из scheduler-а (FJP). GC не может их собрать пока они не завершились. Завершившийся VT, на который никто не держит reference, действительно GC-собирается, но это обычная Java семантика, не специальный мечанизм.
>
> **Откуда путаница:** «Дешёвые потоки» воспринимаются как «эфемерные/managed». Реально это обычные Java объекты, просто маленькие.
>
> **Если бы это было правдой:** «утечка VT» (зависший VT, который никогда не завершится) не была бы реальной проблемой. На деле висящие VT держат свои Continuation объекты и память — типичный memory leak.
>
> ---
>
> #### D) Создание VT занимает ~1 мкс, стек хранится в heap (256B - несколько KB, динамически растёт/уменьшается), 1 миллион VT занимает 5-20 GB heap при средней глубине стека ~50 frames — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Главное архитектурное решение Loom — стек VT хранится в JVM heap как `Continuation` объект, а не в native памяти.
>
> **Стоимость создания:**
> - VT: ~1 мкс (allocate Continuation object + register в scheduler queue).
> - Platform Thread: ~100 мкс - 1 мс (syscall `pthread_create` + allocate native stack).
> - Разница в 100-1000×.
>
> **Стоимость памяти:**
> - VT при создании: ~256-512 байт (минимальный stack + объект Thread).
> - VT при выполнении глубокого вызова: stack растёт chunk-ами по 4KB; типичная глубина 50 frames × ~64 байт/frame = ~3 KB.
> - VT при unmount: только сохранённый stack в heap, без carrier.
> - Platform Thread: фиксированный stack 512KB-8MB native (вне heap), плюс ~1KB overhead в Java.
>
> **Бенчмарк JEP 444:**
> - 1 миллион VT, выполняющих `Thread.sleep(1 hour)` → ~5 GB heap, ~1.5 секунды на создание.
> - 1 миллион Platform Threads — невозможно (OS-предел ~10K).
>
> **Пример:**
> ```java
> // Создаём 1M VT и измеряем
> long start = System.nanoTime();
> var counter = new AtomicLong();
> try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
>     for (int i = 0; i < 1_000_000; i++) {
>         executor.submit(() -> {
>             counter.incrementAndGet();
>             try { Thread.sleep(Duration.ofSeconds(10)); }
>             catch (InterruptedException e) { Thread.currentThread().interrupt(); }
>         });
>     }
>     // На этом моменте в JVM 1M VT в состоянии WAITING
>     System.out.println("Created in " + (System.nanoTime() - start) / 1_000_000 + " ms");
>     // Проверка памяти
>     System.out.println("Heap used: "
>         + ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024 / 1024 + " MB");
> }
> // Typical: "Created in 1500 ms"; "Heap used: 5800 MB"
> ```
>
> **Когда применять:**
> - Batch processing: 1M записей с blocking IO на каждой — VT per-record даёт concurrency без OOM.
> - Long-polling server (Slack, Discord, IRC bot) — каждое соединение клиента = свой VT, держим тысячи параллельно.
> - High-fan-out aggregator: входящий запрос → 100 параллельных backend-вызовов — каждый в своём VT.
> - Тесты под нагрузкой: имитировать 100K активных пользователей внутри одной JVM.
>
> **Подводные камни:**
> - Стек ChunkSize - 4KB. Глубокая рекурсия в VT (1000+ frames) занимает MB на VT — следить за hot recursion.
> - 1M VT в OOM не обязательно — Heap может вырасти до `-Xmx`, и потом GC. Под нагрузкой нужно `-Xmx >> ожидаемый working set`.
> - Если код держит сильные ссылки на VT (ThreadLocal с большим объектом, ScopedValue с map), память растёт линейно с числом VT. Тестировать heap дамп на N VT.
> - JFR `jdk.VirtualThreadStart` events при миллионе VT генерируют гигабайты — sample 1% или фильтровать.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — модель VT, объясняющая дешевизну; [[java-virtual-threads-interview#Q13]] — отсутствие необходимости в пуле как следствие дешевизны; [[java-virtual-threads-interview#Q11]] — diagnostic при миллионах VT.

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


> [!mcq]
>
> **Вопрос:** Существует ли валидный сценарий поместить Virtual Threads в `ThreadPoolExecutor` (т.е. сделать пул VT)?
>
> ---
>
> #### A) Да, для warm-up при cold start Spring Boot — pre-aware VT снижают p99 latency первых запросов — ❌ Неверно
>
> **Что на самом деле:** Cold start у VT отсутствует как явление — создание дешёвое. Pre-aware «холодных» VT не имеет смысла, в отличие от Platform Thread pool, где avoiding `pthread_create` помогает. Spring Boot warm-up идёт через JIT компиляцию hot paths и connection pool init — VT тут ни при чём.
>
> **Откуда путаница:** Pre-warming — общая практика для платформенных пулов и connection pools. Переносится на VT по аналогии.
>
> **Если бы это было правдой:** Spring Boot имел бы свойство `spring.threads.virtual.pre-warm-count`. Реально такого нет — потому что нечего pre-warm-ить.
>
> ---
>
> #### B) НЕТ. Пул убивает преимущество VT (переиспользование становится bottleneck). Правильно: использовать `newVirtualThreadPerTaskExecutor()` (создаёт новый VT на каждую задачу) и ограничивать ресурсы через `Semaphore`/connection pool, а не через число потоков — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Идея пула возникла в Platform Thread эпоху по двум причинам:
> 1. **Cost amortization**: создать поток дорого (100мкс + native stack), нужно переиспользовать.
> 2. **Resource limiting**: ОС не может держать миллион потоков, нужен лимит.
>
> Для VT обе причины исчезают:
> 1. Создание ~1мкс, переиспользование не даёт значимой экономии.
> 2. JVM держит миллионы VT без проблем.
>
> Что хуже — пулинг VT **активно вредит**:
> - `ThreadPoolExecutor(100)` с VT factory: при 1000 параллельных задач 900 ждут в `workQueue` — теряется параллелизм. Concurrent throughput равен 100, а не «миллион».
> - `ThreadLocal` накапливает мусор между задачами на одном VT (нет logical separation per request).
> - Сам ThreadPoolExecutor внутри использует `synchronized` блоки — может pin VT.
>
> Правильный подход — **ограничивать ресурсы**, не потоки:
> - Hikari `maximum-pool-size=50` — лимит подключений к БД.
> - `Semaphore(30)` — лимит параллельных HTTP-запросов к downstream.
> - `RateLimiter` (Guava, Resilience4j) — token bucket для rate limit.
> - VT остаются «бесплатными и многочисленными», но синхронизируются на ресурсе.
>
> **Пример:**
> ```java
> // АНТИПАТТЕРН: пул VT
> ExecutorService antiPattern = new ThreadPoolExecutor(
>     100, 100, 0, TimeUnit.SECONDS,
>     new LinkedBlockingQueue<>(),
>     Thread.ofVirtual().factory()   // ← VT в pool — bottleneck на 100
> );
>
> // ПРАВИЛЬНО: unbounded VT executor + Semaphore для ресурса
> ExecutorService vtExecutor = Executors.newVirtualThreadPerTaskExecutor();
> Semaphore downstreamLimit = new Semaphore(30);  // не более 30 одновременных HTTP
>
> for (Order order : ordersToProcess) {
>     vtExecutor.submit(() -> {
>         downstreamLimit.acquire();
>         try {
>             return httpClient.send(buildRequest(order), ofString());
>         } finally {
>             downstreamLimit.release();
>         }
>     });
> }
> // Concurrency: 30 для HTTP downstream, миллионы VT в очереди acquire — всё ок
> ```
>
> **Когда применять:**
> - Любой Java 21+ Spring Boot — заменить `ThreadPoolExecutor(N)` на `newVirtualThreadPerTaskExecutor()` + Semaphore.
> - В Kafka consumer: вместо `concurrency=10` для Listener — VT executor + Semaphore на downstream calls (если throttling нужен).
> - Batch processing: миллион задач — миллион VT, никаких пулов и ConcurrentLinkedQueue прокладок.
> - Замена `CompletableFuture.runAsync(_, threadPool)` на `Thread.ofVirtual().start(...)` — миграционный паттерн.
>
> **Подводные камни:**
> - Legacy framework (старый Quartz Scheduler) может требовать `ThreadPoolExecutor` как dependency type. Иногда можно подсунуть `newFixedThreadPool(Integer.MAX_VALUE, vtFactory)` — но это hack, не лучше.
> - `CompletableFuture.runAsync(r)` без executor использует ForkJoinPool.commonPool (Platform). Передавать `vtExecutor` явно: `runAsync(r, vtExecutor)`.
> - Если используете `Executors.newScheduledThreadPool(N, vtFactory)`: scheduler держит фиксированное число потоков для таймеров — это OK, не bottleneck (число scheduled tasks ≠ число executions).
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — модель VT, объясняющая «дешёвые потоки»; [[java-virtual-threads-interview#Q5]] — VT не для CPU-bound, и пул не помогает; [[java-virtual-threads-interview#Q10]] — ограничение на ресурсе (Hikari) — правильный паттерн.
>
> ---
>
> #### C) Да, если у вас Spring Batch с `JobLauncher` — он требует `TaskExecutor` с лимитом — ❌ Неверно
>
> **Что на самом деле:** Spring Batch 5.x корректно работает с `VirtualThreadTaskExecutor` без `corePoolSize`/`maxPoolSize`. Параллелизм step-ов регулируется через `step.tasklet().taskExecutor(...).throttleLimit(50)` — это semaphore-style ограничение, не thread pool.
>
> **Откуда путаница:** Spring Batch традиционно требовал `ThreadPoolTaskExecutor`, и кажется, что миграция на VT требует обёртки.
>
> **Если бы это было правдой:** Spring Batch docs для версии 5+ не упоминали бы `VirtualThreadTaskExecutor`. Реально документация это рекомендует.
>
> ---
>
> #### D) Да, чтобы избежать DDoS на ваш собственный сервис при большом наплыве клиентов — ❌ Неверно
>
> **Что на самом деле:** Защита от DDoS делается на уровне Ingress/Gateway (rate limiting, throttling), не пулом потоков. Если упереться в VT pool size — клиенты получат queue wait, не отказ. Реальный DDoS-protection — это `RateLimiter`/`Bulkhead` (Resilience4j), который ограничивает по input rate, не по threads.
>
> **Откуда путаница:** Pool size исторически использовался как «защита от наплыва» — но это плохой механизм, потому что задачи копятся в очереди и память течёт.
>
> **Если бы это было правдой:** Resilience4j и Spring Cloud Gateway были бы не нужны. Реально они доминируют как rate-limiting слой.

## Q14. Как виртуальные потоки соотносятся с Reactive Streams (WebFlux)?

VT и Reactive — две разных модели решения одной проблемы (высокое I/O-параллелизм):

- **Reactive/WebFlux**: non-blocking I/O + callback/operator chains. Сложнее в написании, но максимальная эффективность.
- **Virtual Threads**: blocking-style код, JVM управляет non-blocking под капотом. Проще, но чуть более накладно.

Для **нового кода** Spring Boot + VT — отличный выбор. WebFlux стоит использовать при очень высоких требованиях к throughput или при работе с reactive-only библиотеками.


> [!mcq]
>
> **Вопрос:** Когда WebFlux/Reactor остаётся технически более выгодным выбором, чем Spring MVC + Virtual Threads в Java 21+?
>
> ---
>
> #### A) Всегда — reactive throughput выше при любом сценарии — ❌ Неверно
>
> **Что на самом деле:** На стандартных blocking I/O сценариях (JDBC, RestClient) Spring MVC + VT даёт сопоставимый или лучший throughput, чем WebFlux. Конкретный benchmark (Heinz Kabutz, 2024): blocking JDBC + VT — 30K rps; WebFlux + R2DBC — 28K rps; разница в пределах шума. Простота VT-кода доминирует.
>
> **Откуда путаница:** Маркетинг Reactive 2017-2020 утверждал «reactive всегда быстрее». Это правда было до Loom, не после.
>
> **Если бы это было правдой:** не было бы статей «We migrated from WebFlux to MVC+VT and gained simplicity without losing throughput» (LinkedIn, JetBrains).
>
> ---
>
> #### B) Только в greenfield проектах — миграция с WebFlux на VT слишком дорогая — ❌ Неверно
>
> **Что на самом деле:** Миграция с WebFlux на MVC+VT возможна, но НЕ автоматическая (нужно переписать `Mono`/`Flux` цепочки на блокирующие вызовы). Однако «greenfield only» — слишком сильное утверждение: команды реально мигрировали (LinkedIn opt-in часть сервисов).
>
> **Откуда путаница:** Миграция reactive → blocking требует «расплести» цепочки операторов — это работа, но не невозможна.
>
> **Если бы это было правдой:** не было бы доклада на Spring I/O 2024 о migration пути с примерами кода.
>
> ---
>
> #### C) При работе с reactive-only библиотеками без blocking API (R2DBC mongo-reactive, kafka-streams reactive-binding, событийные потоки SSE/WebSocket в стиле `Flux`), при very high streaming throughput (десятки миллионов событий/сек на одну JVM), и при необходимости backpressure семантики (push-source с механизмом regulation потока сверху) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Reactor/WebFlux остаются предпочтительными в трёх специфичных сценариях:
>
> **1. Reactive-only библиотеки (отсутствует blocking API):**
> - MongoDB reactive driver, Cassandra reactive driver, RSocket клиенты — все возвращают `Mono`/`Flux`, без blocking варианта. Использовать их в VT через `.block()` — антипаттерн (теряется bp).
> - Server-sent events (SSE), WebSocket streaming: `Flux<ServerSentEvent>` — нативная семантика reactive, в MVC требует callback-стиля.
> - Kafka Streams с reactive binding — для event-driven архитектур (lakhouse, real-time analytics).
>
> **2. Очень высокий streaming throughput:**
> - При обработке миллионов событий/сек на одну JVM (real-time bidding, sensor data, market data feed), накладные расходы VT mount/unmount становятся заметны. Reactor с zero-allocation operators (Reactor 3.5+) даёт чуть лучший throughput.
> - Пример: Netflix Mantis (real-time stream processing) — Reactor + Akka, не VT.
>
> **3. Backpressure (regulation push-источника):**
> - Reactive Streams спецификация формализует backpressure: subscriber говорит publisher-у `request(N)` для regulation. У VT нет built-in backpressure — нужно вручную через Semaphore.
> - Когда источник push-style (Kafka consumer, WebSocket, sensor) и потребитель slow — нужна backpressure семантика; reactive естественно её даёт.
>
> **Пример:**
> ```java
> // Случай 1: SSE-стрим из reactive-only источника
> @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
> public Flux<ServerSentEvent<String>> stream() {
>     return reactiveMongo.collection("events")
>         .find(Filters.gt("ts", lastSeen()))
>         .map(doc -> ServerSentEvent.builder(doc.toJson()).build())
>         .delayElements(Duration.ofMillis(100));  // backpressure-respecting
> }
>
> // Случай 2: Аналитический pipeline с backpressure
> Flux<MarketTick> ticks = kafkaReceiver.receive()
>     .map(this::parse)
>     .buffer(Duration.ofMillis(100))       // micro-batch для downstream
>     .flatMap(batch -> writeToTimeSeriesDB(batch),
>              /* concurrency */ 10);        // backpressure: max 10 параллельных writes
> ticks.subscribe();
>
> // Анти-пример: VT с reactive-only Mongo — теряется backpressure
> // List<Doc> docs = reactiveMongo.find(...).collectList().block();  // — антипаттерн
> ```
>
> **Когда применять:**
> - Стриминг real-time данных (BidSwitch, Reuters market feed, Twitch chat) — Reactor проверен на гигантских throughput.
> - Geo-distributed CRDT синхронизация (Cassandra reactive) — события natural reactive.
> - Event-driven архитектура с Kafka + WebSocket fan-out — backpressure обязателен.
> - Mixed Spring Cloud Gateway: его API на Reactor — для routing/filter chain VT не подходит.
>
> **Подводные камни:**
> - НЕ использовать `.block()` внутри VT в reactive chain — это antipattern, теряется backpressure и могут pinning при больших цепочках операторов.
> - WebFlux + Reactor имеет крутую кривую обучения; даже senior-developers делают ошибки с `subscribeOn`/`publishOn`.
> - Smart мульти-парадигмальная архитектура (часть сервисов на MVC+VT для простых REST, часть на WebFlux для streaming) — хороший подход у LinkedIn.
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q5]] — общие сценарии когда VT не помогает; [[java-virtual-threads-interview#Q1]] — модель VT, отличающаяся от reactive; [[java-virtual-threads-interview#Q9]] — сравнение моделей VT/coroutines/reactive.
>
> ---
>
> #### D) Только когда у вас несколько хранилищ — Reactor имеет лучшую multi-datasource поддержку — ❌ Неверно
>
> **Что на самом деле:** Multi-datasource (multi-tenancy с разными БД) корректно работает в Spring MVC через `AbstractRoutingDataSource` + `@Transactional` — никаких преимуществ Reactor нет. И MVC, и WebFlux одинаково умеют переключать соединения по контексту запроса.
>
> **Откуда путаница:** Reactor имеет богатый набор operators, может ощущаться как «более flexible». Multi-datasource — это не про reactive.
>
> **Если бы это было правдой:** Spring Cloud Tenant поддерживал бы только Reactor — реально есть и MVC, и WebFlux версии.

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


> [!mcq]
>
> **Вопрос:** Что делает `Thread.sleep(Duration.ofSeconds(5))` внутри `Thread.ofVirtual().start(r)`, и можно ли использовать тот же подход с `Thread.ofPlatform().start(r)`?
>
> ---
>
> #### A) В VT — отмонтирует поток с carrier, carrier берёт другой VT (физический поток ОС не занят); в Platform Thread — блокирует OS-поток на 5 секунд, занимая физический ресурс — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `Thread.sleep` — главный пример того, как одни и те же JDK API ведут себя по-разному в VT и Platform Thread:
>
> **В VT:**
> - `Thread.sleep(...)` распознаёт, что `Thread.currentThread().isVirtual()` = true.
> - Вызывает `VirtualThread.parkNanos(timeout)` — это hint scheduler-у, что VT надо снять с carrier до timeout.
> - VT-стек сохраняется в heap как Continuation, carrier освобождается и берёт другой VT.
> - По истечении timeout JVM-таймер (один на всю JVM, не на каждый VT) ставит VT обратно в scheduler queue. Любой свободный carrier подхватит.
>
> **В Platform Thread:**
> - `Thread.sleep(...)` вызывает syscall `nanosleep()` / `pthread_cond_timedwait` (зависит от ОС).
> - OS-поток уходит в kernel-state «WAITING» — он не занимает CPU, но занимает место в process table и его native стек.
> - Создать миллион Platform Threads с sleep — невозможно (ОС-лимит).
>
> **Тот же принцип применим к:**
> - `Object.wait(long)` — VT отмонтируется, Platform блокирует.
> - `LockSupport.parkNanos(long)` — то же самое.
> - `Socket.read()`, `Files.newInputStream(...).read()` — blocking I/O, JDK 21 переписал NIO для unmount VT.
> - `Selector.select(long)` — также корректно работает.
>
> **НЕ работает (pinning):**
> - `synchronized` блок + `Thread.sleep` внутри — VT пинируется на carrier.
> - JNI-метод, делающий блокировку — pinning.
> - `Object.wait()` без timeout внутри `synchronized` — pinning ([[java-virtual-threads-interview#Q4]]).
>
> **Пример:**
> ```java
> // Демонстрация: миллион VT спят 60 сек — нет проблем с ресурсами
> try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
>     IntStream.range(0, 1_000_000).forEach(i ->
>         executor.submit(() -> {
>             Thread.sleep(Duration.ofSeconds(60));  // VT отмонтирован, не занимает carrier
>             return null;
>         })
>     );
>     // На этом этапе все 1M VT в WAITING состоянии,
>     // carrier-ов всего ~CPU cores, остальные доступны для других задач
> }
>
> // НЕ ДЕЛАТЬ — миллион Platform Threads:
> // Executors.newFixedThreadPool(1_000_000) → OutOfMemoryError unable to create native thread
>
> // Диагностика: посмотреть, что VT действительно unmount
> Thread.ofVirtual().start(() -> {
>     System.out.println("Before sleep: " + Thread.currentThread());
>     // VirtualThread[#23]/runnable@ForkJoinPool-1-worker-3
>     try { Thread.sleep(100); }
>     catch (InterruptedException e) { Thread.currentThread().interrupt(); }
>     System.out.println("After sleep: " + Thread.currentThread());
>     // VirtualThread[#23]/runnable@ForkJoinPool-1-worker-7  ← carrier СМЕНИЛСЯ
> }).join();
> ```
>
> **Когда применять:**
> - Long-polling эндпоинты (Slack `connect`, Discord gateway) — клиенты ждут push events секунды/минуты в `Thread.sleep`/`Object.wait`. VT позволяет держать миллион таких подключений.
> - Scheduled retry в VT — `Thread.sleep(backoff)` внутри retry-цикла безопасен, не занимает carrier.
> - Тесты: имитировать «медленные клиенты» — миллион VT с `Thread.sleep(rand)` параллельно.
> - Cron-like задачи через VT + `Thread.sleep` вместо `ScheduledThreadPoolExecutor` — простее.
>
> **Подводные камни:**
> - `Thread.sleep(0)` или `Thread.sleep(1)` — короткие sleep всё равно unmount/mount, добавляя overhead. Для tight loops лучше `Thread.onSpinWait()` или вообще не спать.
> - `Object.wait(timeout)` внутри `synchronized (lock)` — pinning, не unmount, даже несмотря на timeout. Использовать `Condition.await(timeout)` с `ReentrantLock`.
> - В тестах JUnit `@Timeout` + VT — таймер JUnit может срабатывать раньше из-за scheduler delay (carrier лазер не свободен). Использовать generous timeouts.
> - `InterruptedException` от `Thread.sleep` в VT работает так же, как в Platform — нужно правильно обрабатывать (restore interrupt status).
>
> **Связанные вопросы:** [[java-virtual-threads-interview#Q1]] — модель VT, объясняющая mount/unmount; [[java-virtual-threads-interview#Q4]] — pinning, когда unmount не работает; [[java-virtual-threads-interview#Q3]] — carrier thread, на который VT мониpyется.
>
> ---
>
> #### B) В обоих случаях идентичное поведение — `Thread.sleep` всегда вызывает syscall — ❌ Неверно
>
> **Что на самом деле:** Поведение `Thread.sleep` ЯВНО зависит от типа потока. В VT идёт JVM-level park, без syscall. В Platform Thread — syscall в ОС. Это документировано в JEP 444.
>
> **Откуда путаница:** API сигнатура одинаковая — `Thread.sleep(long)`. Можно ожидать одинаковую семантику.
>
> **Если бы это было правдой:** не было бы преимущества VT — миллион «sleeping» VT упирался бы в OS-лимит, как Platform Threads.
>
> ---
>
> #### C) В VT — busy-waiting (carrier крутит while-loop с проверкой timer) — ❌ Неверно
>
> **Что на самом деле:** Busy-waiting тратил бы CPU. Реально JVM использует один центральный timer (`Timer thread` в HotSpot), который через priority queue tracking VT-ов с pending sleep. По истечении — VT ставится в scheduler queue, carrier его подхватывает в обычной work-stealing манере.
>
> **Откуда путаница:** Concept «scheduler» иногда воспринимается как «постоянно крутит». На деле scheduler работает event-driven, не polling.
>
> **Если бы это было правдой:** CPU usage в JVM с миллионом sleeping VT был бы 100% на N ядрах carrier-ов. Реально — практически 0%.
>
> ---
>
> #### D) В VT `Thread.sleep` бросает `IllegalThreadStateException`, нужно использовать `LockSupport.parkNanos` — ❌ Неверно
>
> **Что на самом деле:** `Thread.sleep` работает в VT корректно, без исключений. JEP 444 specifically обновил `Thread.sleep` для VT-aware behavior. `LockSupport.parkNanos` — альтернативный API, тоже работает, но не обязателен.
>
> **Откуда путаница:** в ранних preview-релизах Loom (Java 17 incubator) были API restrictions. В Java 21 GA всё стандартизировано.
>
> **Если бы это было правдой:** существующий код с `Thread.sleep` сломался бы при включении VT. Реально миграция on-VT — это zero changes в большинстве случаев.

---

## See also

- [Java Concurrency](java-concurrency-interview.md) — основы многопоточности, synchronized, locks, Executor
- [Java 17-21](java-17-21-interview.md) — все новшества Java 17-21, records, sealed classes
- [Java CompletableFuture](java-completable-future-interview.md) — async composition на любых потоках (включая VT)
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — включение Virtual Threads через spring.threads.virtual.enabled
- [Spring @Async](../../frameworks/spring/spring-async-interview.md) — @Async работает с Virtual Threads в Spring Boot 3.2+
- [Spring WebFlux](../../frameworks/spring/spring-webflux-interview.md) — альтернативный реактивный подход (не-блокирующий)
- [Kotlin Coroutines](../kotlin/kotlin-coroutines-interview.md) — сравнение с корутинами Kotlin
- [Reactive Patterns](../../reactive/reactive-patterns-interview.md) — reactive streams как альтернатива VT
- [JVM Performance Tuning](../../performance/jvm-performance-tuning-interview.md) — настройка JVM при использовании VT
- [Thread Pools](java-concurrency-interview.md) — ThreadPoolExecutor концепции (почему не нужны для VT)
