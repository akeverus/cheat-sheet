---
title: "Вопросы на собеседовании: Application Profiling"
description: "Практичные вопросы и ответы по profiling Java-приложений: CPU, memory, allocation, lock contention, flame graphs, JFR, async-profiler, heap/thread dumps, GC-анализ, APM, continuous profiling в production."
tags:
  - interview
  - performance
  - application-profiling-interview
  - profiling
  - jfr
  - async-profiler
  - flame-graph
  - apm
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Application Profiling"
  - "Java profiling собеседование"
  - "Профилирование приложений"
prerequisites: []
next: []
updated: "2026-05-14"
---
# Вопросы на собеседовании: `Application Profiling`

Практичные вопросы и ответы по profiling для `Senior Java Developer`: как находить узкие места по `CPU`, памяти, блокировкам и сетевым операциям в production. Покрывает `JFR`, `async-profiler`, `flame graphs`, `heap/thread dumps`, `GC`-анализ, `APM`-инструменты и `continuous profiling`.

## Полезные ссылки

### Официальная документация

- [Java Flight Recorder](https://docs.oracle.com/javase/9/troubleshoot/diagnostic-tools.htm#JSTGD356) — диагностика JVM через встроенный рекордер
- [JVM Tool Interface (JVMTI)](https://docs.oracle.com/javase/9/docs/specs/jvmti.html) — низкоуровневый API для инструментирования JVM
- [async-profiler GitHub](https://github.com/async-profiler/async-profiler) — низко-overhead профайлер для Linux/macOS
- [JDK Mission Control](https://www.oracle.com/java/technologies/jdk-mission-control.html) — GUI-анализатор JFR-записей
- [Eclipse MAT](https://eclipse.dev/mat/) — анализатор heap dump
- [Datadog Continuous Profiler](https://docs.datadoghq.com/profiler/) — APM с continuous profiling
- [Pyroscope](https://grafana.com/oss/pyroscope/) — open-source continuous profiling (Grafana)
- [Monitoring Java Applications with Flight Recorder](https://www.baeldung.com/java-flight-recorder-monitoring) — JFR для мониторинга Java-приложений
- [A Guide to Java Profilers](https://www.baeldung.com/java-profilers) — обзор профайлеров Java
- [Diagnosing a Running JVM](https://www.baeldung.com/running-jvm-diagnose) — диагностика работающей JVM
- [JFR View Command in Java](https://www.baeldung.com/java-flight-recorder-view) — использование команды jfr view

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы профилирования**
- [Q1. (!) Что такое profiling и чем он отличается от мониторинга?](#q1--что-такое-profiling-и-чем-он-отличается-от-мониторинга)
- [Q2. (!) Как выбрать между sampling и instrumentation?](#q2--как-выбрать-между-sampling-и-instrumentation)
- [Q3. Что такое safepoint bias и как он влияет на точность профилирования?](#q3-что-такое-safepoint-bias-и-как-он-влияет-на-точность-профилирования)
- [Q4. Какие метрики обязательно связать с профилем?](#q4-какие-метрики-обязательно-связать-с-профилем)

**Java Flight Recorder (JFR)**
- [Q5. (!) Что такое JFR и как он работает?](#q5--что-такое-jfr-и-как-он-работает)
- [Q6. Как запустить JFR и какие настройки использовать?](#q6-как-запустить-jfr-и-какие-настройки-использовать)
- [Q7. Как анализировать JFR-записи?](#q7-как-анализировать-jfr-записи)
- [Q8. Какие ключевые JFR-события нужно знать?](#q8-какие-ключевые-jfr-события-нужно-знать)

**async-profiler**
- [Q9. (!) Как работает async-profiler и чем он лучше стандартных инструментов?](#q9--как-работает-async-profiler-и-чем-он-лучше-стандартных-инструментов)
- [Q10. Как использовать async-profiler на практике?](#q10-как-использовать-async-profiler-на-практике)
- [Q11. Когда использовать JFR, а когда async-profiler?](#q11-когда-использовать-jfr-а-когда-async-profiler)

**Flame Graphs**
- [Q12. (!) Как читать flame graph корректно?](#q12--как-читать-flame-graph-корректно)
- [Q13. Какие типы flame graph существуют и когда каждый полезен?](#q13-какие-типы-flame-graph-существуют-и-когда-каждый-полезен)

**CPU-профилирование**
- [Q14. (!) Как профилировать CPU bottleneck?](#q14--как-профилировать-cpu-bottleneck)
- [Q15. Что такое on-CPU и off-CPU анализ, и зачем оба?](#q15-что-такое-on-cpu-и-off-cpu-анализ-и-зачем-оба)
- [Q16. Как интерпретировать профиль и не ошибиться с root cause?](#q16-как-интерпретировать-профиль-и-не-ошибиться-с-root-cause)

**Allocation Profiling и Memory**
- [Q17. (!) Как профилировать allocation и memory pressure?](#q17--как-профилировать-allocation-и-memory-pressure)
- [Q18. Как снять и проанализировать heap dump?](#q18-как-снять-и-проанализировать-heap-dump)
- [Q19. Как найти memory leak с помощью профилирования?](#q19-как-найти-memory-leak-с-помощью-профилирования)

**Thread Dumps и Lock Contention**
- [Q20. (!) Как снять и анализировать thread dump?](#q20--как-снять-и-анализировать-thread-dump)
- [Q21. Как находить lock contention через профилирование?](#q21-как-находить-lock-contention-через-профилирование)

**GC-анализ**
- [Q22. (!) Как анализировать GC и связать его с профилированием?](#q22--как-анализировать-gc-и-связать-его-с-профилированием)
- [Q23. Как профилировать GC pauses и их влияние на latency?](#q23-как-профилировать-gc-pauses-и-их-влияние-на-latency)

**APM и Continuous Profiling**
- [Q24. (!) Что такое APM и как он дополняет профилирование?](#q24--что-такое-apm-и-как-он-дополняет-профилирование)
- [Q25. (!) Что такое continuous profiling и зачем он нужен?](#q25--что-такое-continuous-profiling-и-зачем-он-нужен)
- [Q26. Как работает Datadog Continuous Profiler?](#q26-как-работает-datadog-continuous-profiler)

**Профилирование в Production**
- [Q27. (!) Как профилировать production безопасно?](#q27--как-профилировать-production-безопасно)
- [Q28. Как профилировать сервис в Kubernetes/Docker?](#q28-как-профилировать-сервис-в-kubernetesdocker)
- [Q29. Как связать profiling с Prometheus/Grafana и алертингом?](#q29-как-связать-profiling-с-prometheusgrafana-и-алертингом)

**Heap dump и flame graphs — практика**
- [Q33. (!) Как анализировать heap dump с Eclipse MAT: практический сценарий?](#q33--как-анализировать-heap-dump-с-eclipse-mat-практический-сценарий)
- [Q34. Как читать flame graph и находить проблемы?](#q34-как-читать-flame-graph-и-находить-проблемы)

**Продвинутые темы профилирования**
- [Q35. Async Profiler vs JFR — когда что выбирать, отличия](#q35-async-profiler-vs-jfr--когда-что-выбирать-отличия)
- [Q36. Profiling в production — low-overhead инструменты](#q36-profiling-в-production--low-overhead-инструменты)
- [Q37. Heap Dump анализ — MAT, VisualVM, утечки памяти](#q37-heap-dump-анализ--mat-visualvm-утечки-памяти)
- [Q38. Thread Dump анализ — deadlock detection, jstack](#q38-thread-dump-анализ--deadlock-detection-jstack)
- [Q39. CPU-профилирование: чем отличаются sampling и instrumentation?](#q39-cpu-профилирование-чем-отличаются-sampling-и-instrumentation)
- [Q40. Профилирование аллокаций: что такое TLAB и allocation rate?](#q40-профилирование-аллокаций-что-такое-tlab-и-allocation-rate)
- [Q41. Профилирование запросов к БД: slow query log и EXPLAIN](#q41-профилирование-запросов-к-бд-slow-query-log-и-explain)
- [Q42. Profiling реактивных приложений — особенности Project Reactor](#q42-profiling-реактивных-приложений--особенности-project-reactor)

**Практика и Anti-patterns**
- [Q30. Какие anti-patterns в profiling чаще всего встречаются?](#q30-какие-anti-patterns-в-profiling-чаще-всего-встречаются)
- [Q31. Как построить системный процесс профилирования в команде?](#q31-как-построить-системный-процесс-профилирования-в-команде)
- [Q32. (!) Как ответить про profiling на senior-раунде за 1 минуту?](#q32--как-ответить-про-profiling-на-senior-раунде-за-1-минуту)

---

## Q1. (!) Что такое profiling и чем он отличается от мониторинга?

`Profiling` измеряет реальное поведение приложения на уровне отдельных методов и стек-трейсов: где именно тратится CPU, как аллоцируется память, где потоки блокируются. Мониторинг отвечает на вопрос «есть ли проблема», профилирование — «где именно она внутри кода».

**Ключевое отличие от мониторинга:**

| Аспект | Мониторинг | Профилирование |
|--------|-----------|----------------|
| Гранулярность | Метрики на уровне сервиса (p99, CPU%) | Стек-трейсы на уровне методов |
| Когда | Постоянно (24/7) | По запросу или continuous |
| Overhead | Минимальный (<1%) | Низкий-средний (1-5%) |
| Цель | Обнаружение проблемы | Локализация root cause |
| Пример | "p99 latency вырос до 500ms" | "60% CPU тратится в `JsonParser.parse()`" |

Профилирование не заменяет метрики, а дополняет их: его включают **после** сигнала о деградации или при адресной оптимизации критичного сценария. Метрики дёшевы и работают всегда; профиль дороже, поэтому снимается прицельно.

```mermaid
graph LR
    A[Алерт по метрикам] --> B[Гипотеза]
    B --> C[Профилирование]
    C --> D[Root cause]
    D --> E[Изменение кода]
    E --> F[Re-profile]
    F -->|подтверждено| G[Deploy]
    F -->|не подтверждено| B
```

Ключевой принцип: **profile → hypothesis → change → re-profile**.

## Q2. (!) Как выбрать между sampling и instrumentation?

Это два фундаментально разных способа собрать данные: `sampling` периодически «фотографирует» стеки, а `instrumentation` вшивает замеры в каждый метод. Разница в том, **что мы жертвуем ради точности**.

**`Sampling` (выборочный)** — статистическая выборка. Профайлер каждые N мс снимает стек-трейсы всех потоков, и чем чаще метод попадает на «фото», тем дороже он по ресурсу.
- Низкий overhead (~1–5%) — пригоден для production.
- Картина вероятностная: очень короткие методы могут не попасть ни в один снимок.
- Инструменты: `async-profiler`, `JFR`, `perf`.

**`Instrumentation` (инструментальный)** — измерение каждого вызова. Профайлер вставляет код до и после метода, поэтому видит точный счётчик вызовов и время.
- Высокий overhead (10–50%+) искажает саму картину — это «observer effect»: измерение меняет измеряемое.
- Зато ловит даже редкие короткоживущие методы.
- Инструменты: `JProfiler` (instrumentation mode), `YourKit`, `BTrace`.

```java
// Instrumentation по сути добавляет такой код в каждый метод:
public void processOrder(Order order) {
    long start = System.nanoTime();
    try {
        // оригинальный код
    } finally {
        Profiler.record("processOrder", System.nanoTime() - start);
    }
}
```

**Рекомендация:** в production — только `sampling` (`JFR`, `async-profiler`). `Instrumentation` оставляйте для dev/test и только на конкретных классах, когда действительно нужен точный счётчик вызовов.

## Q3. Что такое safepoint bias и как он влияет на точность профилирования?

`Safepoint bias` — это систематическое искажение профиля у стандартных JVM-профайлеров: они «видят» не реальный горячий код, а ближайшие к нему `safepoint`-ы. `Safepoint` — это точки в коде, где состояние потока полностью определено и JVM может безопасно снять его стек (например, перед вызовом метода или на back-edge цикла).

**В чём проблема:** стандартный `JVM TI GetStackTrace()` снимает стек только в `safepoint`. Если поток выполняет длинный цикл без safepoint-а, профайлер вынужден дождаться выхода из цикла — и в итоге «припишет» время не тому коду:

```java
// Этот цикл с counted loop может НЕ содержать safepoint
// до JDK 17 (JEP 401 добавил safepoints в counted loops)
for (int i = 0; i < array.length; i++) {
    sum += array[i]; // safepoint bias — профайлер не покажет этот код
}
```

**Решение:** `async-profiler` использует `AsyncGetCallTrace` — недокументированный API, который снимает стек прямо из обработчика сигнала, не дожидаясь safepoint. Поэтому он показывает честную картину, включая код внутри циклов.

| Профайлер | Safepoint bias | Точность |
|-----------|---------------|----------|
| `VisualVM` (sampling) | Да | Низкая |
| `JProfiler` (sampling) | Да | Низкая |
| `JFR` (execution sample) | Минимальный | Высокая |
| `async-profiler` | Нет | Высокая |

## Q4. Какие метрики обязательно связать с профилем?

Профиль без контекста метрик легко толкует неверно, поэтому к нему всегда привязывают минимальный набор показателей. Они отвечают на вопрос «насколько вообще больно пользователю и под какой нагрузкой снят этот профиль»:

- **Latency** p50/p95/p99 — основной SLI: что чувствует пользователь.
- **Throughput** (RPS) — под какой нагрузкой снимался профиль.
- **CPU utilization** — общая загрузка и распределение по ядрам.
- **GC pauses** — частота и длительность пауз (см. [Memory Management](memory-management-interview.md)).
- **Error rate** — процент ошибок.
- **Heap usage** — утилизация памяти.

**Зачем это нужно:** «горячий» метод в профиле не обязательно и есть причина деградации. Классический пример — `GC` занимает 30% CPU, но если его паузы не влияют на p99, оптимизировать его бессмысленно: вы потратите время, а пользователь разницы не заметит. Метрики не дают принять симптом за корень.

## Q5. (!) Что такое JFR и как он работает?

`Java Flight Recorder (JFR)` — встроенный прямо в JVM механизм сбора событий с минимальным overhead (<1% в production-конфигурации). Его сила в том, что он не подключается извне, а пишет данные из самой виртуальной машины, поэтому почти ничего не стоит по производительности. С `JDK 11` бесплатен (раньше требовал коммерческой лицензии `Oracle JDK`).

**Архитектура JFR:**

```mermaid
graph TB
    subgraph JVM
        A[JFR Engine] --> B[Thread-local Buffers]
        B --> C[Global Buffer Pool]
        C --> D[Disk Repository]
    end
    D --> E[.jfr файл]
    E --> F[JDK Mission Control]
    E --> G[jfr CLI tool]
    E --> H[Programmatic API]
```

**Почему overhead такой низкий:**
- Каждый поток пишет в **свой** thread-local буфер — нет борьбы за общий lock.
- Буферы периодически сбрасываются в глобальный пул, а оттуда — на диск, не блокируя рабочие потоки.

**Ключевые особенности:**
- **Событийная модель** — JFR пишет не только стеки CPU, но и GC-события, I/O, monitor enter, exceptions и десятки других типов. Это даёт цельную картину «что вообще происходит в JVM», а не только CPU.
- **Два встроенных профиля:** `default` (~1% overhead) и `profile` (~2% overhead, больше деталей, включая allocation events).
- **Circular buffer** — может работать непрерывно, перезаписывая самые старые данные, поэтому годится для постоянной записи в production.

## Q6. Как запустить JFR и какие настройки использовать?

JFR запускают тремя способами: флагом при старте JVM, командой `jcmd` на уже работающем процессе или программно из кода. Выбор зависит от того, есть ли у вас доступ к строке запуска и можно ли перезапустить приложение.

**1. При старте JVM** (когда можно перезапустить):

```bash
# С default-профилем (production-safe)
java -XX:StartFlightRecording=duration=60s,filename=recording.jfr \
     -jar myapp.jar

# С расширенным профилем
java -XX:StartFlightRecording=settings=profile,duration=300s,\
     filename=detailed.jfr -jar myapp.jar

# Continuous recording с maxsize
java -XX:StartFlightRecording=disk=true,maxsize=500m,maxage=1h \
     -jar myapp.jar
```

**2. На работающем приложении через `jcmd`** (когда перезапуск недоступен — типичный production-кейс):

```bash
# Найти PID
jcmd | grep myapp

# Начать запись
jcmd <PID> JFR.start name=profile settings=profile duration=60s \
     filename=/tmp/recording.jfr

# Dump текущего continuous recording
jcmd <PID> JFR.dump name=default filename=/tmp/dump.jfr

# Остановить
jcmd <PID> JFR.stop name=profile
```

**3. Программно из кода (JDK 14+)** (когда нужно записывать профиль по бизнес-событию):

```java
try (Recording recording = new Recording(Configuration.getConfiguration("profile"))) {
    recording.setMaxAge(Duration.ofMinutes(5));
    recording.setDestination(Path.of("/tmp/recording.jfr"));
    recording.start();
    
    // ... выполнение нагрузки ...
    
    recording.stop();
}
```

## Q7. Как анализировать JFR-записи?

`.jfr`-файл — это бинарный поток событий. Анализировать его можно тремя способами: быстрый CLI для разовых проверок, GUI-инструмент JMC для глубокого разбора и программный API для автоматизации.

**1. CLI-инструмент `jfr` (JDK 17+)** — для быстрых проверок прямо в терминале:

```bash
# Вывод сводки
jfr summary recording.jfr

# Просмотр конкретных событий
jfr print --events jdk.ExecutionSample recording.jfr

# Фильтрация по времени
jfr print --events jdk.GCPhasePause --json recording.jfr | jq .

# Получение flame graph через jfr-datasource
jfr print --events jdk.ExecutionSample --stack-depth 64 recording.jfr
```

**2. JDK Mission Control (JMC)** — лучший инструмент для комплексного разбора:
- GUI с heat map, histogram, dependency view.
- Automated Analysis — автоматически прогоняет правила и подсвечивает подозрительные места (высокий allocation rate, длинные паузы, lock contention), так что не нужно знать заранее, что искать.

**3. Программный доступ (JDK 14+)** — когда анализ нужно автоматизировать (например, в CI):

```java
try (RecordingFile file = new RecordingFile(Path.of("recording.jfr"))) {
    while (file.hasMoreEvents()) {
        RecordedEvent event = file.readEvent();
        if (event.getEventType().getName().equals("jdk.ExecutionSample")) {
            RecordedStackTrace stack = event.getStackTrace();
            RecordedMethod topMethod = stack.getFrames().get(0).getMethod();
            System.out.println(topMethod.getType().getName() 
                + "." + topMethod.getName());
        }
    }
}
```

## Q8. Какие ключевые JFR-события нужно знать?

| Событие | Категория | Что показывает |
|---------|----------|----------------|
| `jdk.ExecutionSample` | CPU | Стек-трейсы активных потоков (CPU profiling) |
| `jdk.ObjectAllocationInNewTLAB` | Memory | Аллокации в новый TLAB |
| `jdk.ObjectAllocationOutsideTLAB` | Memory | Аллокации вне TLAB (медленный путь) |
| `jdk.GCPhasePause` | GC | Длительность GC-пауз |
| `jdk.GarbageCollection` | GC | Общая информация о GC-циклах |
| `jdk.JavaMonitorEnter` | Locks | Ожидание synchronized-блоков (>20ms) |
| `jdk.JavaMonitorWait` | Locks | `Object.wait()` вызовы |
| `jdk.ThreadPark` | Locks | `LockSupport.park()` — concurrent locks |
| `jdk.FileRead` / `jdk.FileWrite` | I/O | Файловые операции с длительностью |
| `jdk.SocketRead` / `jdk.SocketWrite` | I/O | Сетевые операции |
| `jdk.JavaExceptionThrow` | Errors | Выброшенные исключения со стеками |

Главная ценность этой таблицы — понимать, под какую проблему какое событие смотреть: CPU-боттлнек → `ExecutionSample`, давление на GC → `ObjectAllocation*`, зависания на блокировках → `JavaMonitorEnter` / `ThreadPark`, медленный I/O → `Socket*` / `File*`.

**Рекомендация:** в production держите `default`-профиль, но для диагностики аллокаций переключитесь на `profile` — только он включает `ObjectAllocationInNewTLAB`.

## Q9. (!) Как работает async-profiler и чем он лучше стандартных инструментов?

`async-profiler` — open-source профайлер для JVM на Linux и macOS, главное преимущество которого в том, что он не страдает от `safepoint bias` и показывает честную картину горячего кода. Достигается это двумя механизмами:

**1. `AsyncGetCallTrace`** — недокументированный API OpenJDK, который снимает Java-стек в произвольный момент по сигналу, не дожидаясь safepoint. Именно это устраняет искажение профиля.

**2. `perf_events`** (Linux) — ядерный механизм аппаратных счётчиков. Благодаря ему async-profiler сшивает native-стеки (JIT, GC, ядро) с Java-стеками в одном flame graph — видно даже то, что происходит ниже уровня байт-кода.

```mermaid
graph TB
    subgraph async-profiler
        A[perf_events / timer signal] -->|SIGPROF| B[Signal Handler]
        B --> C[AsyncGetCallTrace]
        C --> D[Java Stack Trace]
        B --> E[Native Unwinding]
        E --> F[Native Stack Trace]
        D --> G[Merged Flame Graph]
        F --> G
    end
```

**Преимущества:**
- Нет `safepoint bias` — видит код даже внутри counted loops, который стандартные профайлеры пропускают.
- Объединяет Java + native стеки в одном графе (видно JNI, GC, компилятор).
- Overhead ~1–2% в sampling mode — пригоден для production.
- Один инструмент закрывает несколько режимов: CPU, allocation, lock, wall-clock.
- Встроенная генерация `flame graph` в HTML/SVG — не нужны внешние конвертеры.

## Q10. Как использовать async-profiler на практике?

На практике всё сводится к выбору режима через флаг `-e` (событие, по которому семплируем) и длительности `-d`. Ключевая идея — под разную проблему берётся разное событие: `cpu` для процессора, `alloc` для памяти, `lock` для блокировок, `wall` для ожиданий.

**Базовые команды:**

```bash
# CPU profiling на 30 секунд с flame graph
./asprof -d 30 -f cpu-flamegraph.html <PID>

# Allocation profiling (показывает, где создаются объекты)
./asprof -d 30 -e alloc -f alloc-flamegraph.html <PID>

# Lock contention profiling
./asprof -d 30 -e lock -f lock-flamegraph.html <PID>

# Wall-clock profiling (on-CPU + off-CPU)
./asprof -d 30 -e wall -f wall-flamegraph.html <PID>

# С фильтрацией по потокам
./asprof -d 30 -e cpu -t -f cpu-per-thread.html <PID>

# Только конкретные потоки (по имени)
./asprof -d 30 -e cpu --filter "http-nio-*" -f http-threads.html <PID>
```

**Запуск как Java-agent:**

```bash
java -agentpath:/path/to/libasyncProfiler.so=start,event=cpu,\
     file=profile.html,duration=60 -jar myapp.jar
```

**Программный API (JDK):**

```java
// Через Attach API
AsyncProfiler profiler = AsyncProfiler.getInstance();
profiler.execute("start,event=cpu,file=profile.html");
// ... нагрузка ...
profiler.execute("stop");
```

## Q11. Когда использовать JFR, а когда async-profiler?

| Критерий | JFR | async-profiler |
|----------|-----|----------------|
| Встроенность | Часть JDK | Внешняя библиотека |
| ОС | Любая | Linux, macOS |
| CPU profiling | Хорошо (минимальный bias) | Отлично (без bias) |
| Allocation | Только TLAB events | Точный alloc sampling |
| Lock profiling | Monitor events | lock + wall-clock |
| Native стеки | Нет | Да (Java + native) |
| GC/IO events | Да (богатая модель) | Нет |
| Continuous mode | Да | Ограниченно |
| Flame graphs | Через конвертацию | Встроенные |

Короткий ответ: это не «или–или», а два инструмента в связке. `JFR` даёт широкий контекст (GC, I/O, exceptions, мониторы) и работает на любой ОС без доустановки; `async-profiler` даёт точную, без safepoint bias, детализацию CPU/allocation и видит native-стеки, но требует Linux/macOS и отдельного агента.

**Рекомендация:** сначала снимите `JFR` — понять, в чём вообще проблема (GC? I/O? CPU?). Затем, если узкое место в CPU или аллокациях, добейте его `async-profiler`-ом с честными стеками.

## Q12. (!) Как читать flame graph корректно?

`Flame graph` (изобретён Brendan Gregg) — это визуализация тысяч стек-трейсов, схлопнутых в одну картинку: чем шире блок, тем больше ресурса (CPU, аллокаций, ожиданий) на него приходится. Читается он не слева направо во времени, а «по ширине»: ищем самые широкие блоки. Правила чтения:

```mermaid
graph TB
    subgraph "Flame Graph — как читать"
        direction TB
        A["main() — ширина = 100% CPU"]
        A --> B["handleRequest() — 70%"]
        A --> C["healthCheck() — 30%"]
        B --> D["processData() — 40%"]
        B --> E["serializeResponse() — 30%"]
        D --> F["parseJson() — 25%"]
        D --> G["validate() — 15%"]
    end
```

**Ключевые принципы:**
- **Ширина блока** = доля ресурса (времени CPU / аллокаций / lock waits)
- **Высота** = глубина стека вызовов
- **Горизонтальный порядок** — алфавитный (НЕ хронологический!)
- Искать нужно **широкие "плато"** — методы, занимающие значительную долю

**Типичные ошибки:**
1. **Оптимизировать верхний helper-метод** вместо настоящей причины ниже по дереву (верх — это лишь то, где поток находился в момент снимка).
2. **Принимать ширину за абсолютное время** — это доля от общего ресурса, а не миллисекунды.
3. **Думать, что порядок слева направо = порядок выполнения** — он алфавитный, не временной.
4. **Игнорировать `[unknown]` фреймы** — за ними могут скрываться JIT stubs или native-код, и иногда именно там проблема.

**Рекомендация:** для сравнения «до/после» оптимизации используйте **differential flame graph** — он подсвечивает красным/зелёным изменения между двумя профилями, так что сразу видно, помогла правка или нет.

## Q13. Какие типы flame graph существуют и когда каждый полезен?

| Тип | Ось X | Когда использовать |
|-----|-------|--------------------|
| CPU flame graph | CPU time | Где тратится процессорное время |
| Allocation flame graph | Bytes allocated | Где создаются объекты (memory pressure) |
| Off-CPU flame graph | Wall-clock wait time | Где потоки ждут (I/O, locks) |
| Wall-clock flame graph | Wall time | Полная картина (on-CPU + off-CPU) |
| Differential flame graph | Разница двух профилей | Сравнение до/после оптимизации |
| Icicle graph | Инвертированная ось Y | Анализ bottom-up (от листьев к корню) |

Главное — тип flame graph задаётся **осью X**, то есть тем, что мы откладываем по ширине. CPU-граф меряет процессорное время, allocation — байты, off-CPU/wall — время ожидания. Один и тот же код на разных графах выглядит совершенно по-разному, поэтому под вопрос «где медленно» сначала выбирают правильный тип.

**Генерация flame graph:**

```bash
# async-profiler — встроенная генерация
./asprof -d 30 -f flamegraph.html <PID>

# Из JFR через конвертер
jfr print --events jdk.ExecutionSample --stack-depth 64 recording.jfr \
  | java -jar converter.jar > flamegraph.html

# Из perf через FlameGraph tools (Brendan Gregg)
perf script | stackcollapse-perf.pl | flamegraph.pl > perf-flamegraph.svg
```

## Q14. (!) Как профилировать CPU bottleneck?

CPU-bottleneck профилируют по чёткому циклу из четырёх шагов: зафиксировать окно деградации по метрикам → снять CPU-профиль → разобрать flame graph → исправить и перепроверить. Хаотичное «давайте просто попрофилируем» обычно ведёт к оптимизации не того места.

**1. Зафиксировать окно деградации** — связать с метриками (см. [метрики и трассировка](../monitoring/metrics-tracing-interview.md)):

```bash
# Быстрая проверка: что делает JVM прямо сейчас
jcmd <PID> Thread.print | head -100

# CPU utilization по процессу
top -Hp <PID>  # Linux: показать потоки
```

**2. Снять CPU profile:**

```bash
# async-profiler: 60 секунд CPU sampling
./asprof -d 60 -e cpu -f cpu.html <PID>

# JFR: с execution samples
jcmd <PID> JFR.start name=cpu settings=profile duration=60s \
     filename=/tmp/cpu.jfr
```

**3. Анализ flame graph:**

```bash
# Найти top-N горячих методов
./asprof -d 60 -e cpu -o flat <PID>

# Пример вывода:
#   --- Execution profile ---
#   Total samples: 12543
#       3214 (25.62%) com.example.JsonParser.parse
#       1987 (15.84%) java.util.HashMap.resize
#       1245 ( 9.93%) com.example.Validator.validate
```

**4. Оценить и исправить:**
- Алгоритмическая сложность (O(n²) → O(n log n))
- Лишние аллокации в горячем цикле
- Неэффективная сериализация
- Результат: "после оптимизации hot path CPU -22%, p99 -18%"

## Q15. Что такое on-CPU и off-CPU анализ, и зачем оба?

Поток в каждый момент либо что-то считает на процессоре (**on-CPU**), либо ждёт (**off-CPU**). Это два непересекающихся источника latency, и обычный CPU-профайлер видит только первый — поэтому нужны оба анализа.

**`on-CPU`** — поток реально выполняет код на процессоре (вычисления, парсинг, сериализация).

**`off-CPU`** — поток ничего не считает, а ждёт: lock, I/O, sleep, park, сетевой вызов. CPU при этом простаивает, но запрос всё равно тормозит.

```mermaid
graph LR
    subgraph "Жизнь потока"
        A[on-CPU: вычисления] --> B[off-CPU: ожидание I/O]
        B --> C[on-CPU: обработка]
        C --> D[off-CPU: lock wait]
        D --> E[on-CPU: вычисления]
    end
```

**Почему мало одного on-CPU:** самые частые причины медленных сервисов вообще не нагружают процессор, и CPU-профиль их «не видит»:
- Поток ждёт ответа от БД — off-CPU.
- Поток заблокирован на `synchronized` — off-CPU.
- Поток ждёт DNS resolution — off-CPU.

```bash
# Wall-clock profiling покажет ОБА типа
./asprof -d 30 -e wall -t -f wall.html <PID>

# Только off-CPU (async-profiler 3.0+)
./asprof -d 30 -e wall --wall-filter off-cpu -f offcpu.html <PID>
```

**Эмпирическое правило:** если приложение «медленное, но CPU низкий» — проблема почти всегда off-CPU, и искать её надо wall-clock-профилем, а не CPU.

## Q16. Как интерпретировать профиль и не ошибиться с root cause?

Главная ошибка — принять верхний горячий метод за root cause и сразу его чинить. Чтобы не оптимизировать симптом, профиль прогоняют через три проверки:

**1. Воспроизводимость** — картина повторяется в нескольких прогонах, а не была разовым артефактом?

**2. Корреляция с метриками** — горячий метод действительно двигает пользовательскую latency, а не просто «много CPU где-то в фоне»?

```bash
# Сопоставить timestamp профиля с метриками
# В Grafana: наложить window профилирования на график latency/CPU
```

**3. Подтверждение гипотезы** — после изменения кода метрика реально улучшилась? Если нет — вы лечили симптом.

**Подводные камни** (метод в top ≠ виновник):
- `Thread.sleep()` / `Object.wait()` в CPU-профиле — это ожидание, а не нагрузка на CPU.
- Горячий `HashMap.get()` — обычно проблема не в самом get, а в размере map или hash collision.
- `GC` в top — настоящая причина не в GC, а в аллокациях: смотрите allocation profile.
- `JIT compilation` в начале записи — это warm-up, а не steady-state; такой профиль нерепрезентативен.

Итог: если изменение кода не подтвердилось улучшением метрики — найденное было симптомом, а не root cause.

## Q17. (!) Как профилировать allocation и memory pressure?

Суть allocation profiling — найти, где код создаёт больше всего объектов, потому что именно темп аллокаций (а не размер heap) определяет давление на `GC`. Чем быстрее код «мусорит», тем чаще запускается сборщик, тем больше пауз и хуже latency. Подробнее о влиянии GC — в [Memory Management](memory-management-interview.md).

**Инструменты:**

```bash
# async-profiler: allocation profiling
./asprof -d 60 -e alloc -f alloc.html <PID>

# JFR: включить allocation events
jcmd <PID> JFR.start settings=profile duration=60s \
     filename=/tmp/alloc.jfr
# profile-настройки включают ObjectAllocationInNewTLAB
```

**Что искать в allocation flame graph:**
- **Allocation hotspots** — самые широкие блоки: где создаётся больше всего объектов по байтам.
- **Churn** — объекты, которые создаются и тут же умирают (temporary objects); их много, и они зря гоняют Young GC.
- **Promotion** — объекты, которые «дожили» до перевода в Old Gen; они опаснее всего, потому что провоцируют дорогие Full GC.

**Типичные находки и решения:**

```java
// ПРОБЛЕМА: аллокация строк в горячем цикле
for (Order order : orders) {
    String key = "order:" + order.getId() + ":status"; // новый String каждый раз
    cache.get(key);
}

// РЕШЕНИЕ: StringBuilder или предвычисленные ключи
StringBuilder sb = new StringBuilder(32);
for (Order order : orders) {
    sb.setLength(0);
    sb.append("order:").append(order.getId()).append(":status");
    cache.get(sb.toString());
}
```

```java
// ПРОБЛЕМА: autoboxing в горячем пути
Map<Long, Integer> scores = new HashMap<>(); // autoboxing Long, Integer
scores.put(userId, score); // 2 объекта на каждый put

// РЕШЕНИЕ: примитивные коллекции (Eclipse Collections, fastutil)
LongIntHashMap scores = new LongIntHashMap(); // нет autoboxing
scores.put(userId, score);
```

## Q18. Как снять и проанализировать heap dump?

`Heap dump` — это полный снимок содержимого Java Heap (формат HPROF): все живые объекты, их поля и ссылки на момент снятия. По нему отвечают на вопрос «что именно занимает память и кто это удерживает».

**Снятие heap dump:**

```bash
# Через jcmd (рекомендуется)
jcmd <PID> GC.heap_dump /tmp/heap.hprof

# Через jmap
jmap -dump:format=b,file=/tmp/heap.hprof <PID>

# Автоматически при OutOfMemoryError
java -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/tmp/dumps/ -jar myapp.jar
```

**Анализ в Eclipse MAT:**
1. Открыть dump → Leak Suspects Report (автоматический анализ)
2. Dominator Tree — кто удерживает больше всего памяти
3. Histogram — количество и размер объектов по классам
4. Path to GC Roots — цепочка ссылок от объекта до root

**Ключевые термины** (без них heap dump не прочитать):
- **Shallow size** — размер самого объекта без того, на что он ссылается. Часто обманчиво мал.
- **Retained size** — сколько памяти реально освободится, если удалить объект **и всё, что держится только через него**. Именно это число показывает «вес» утечки.
- **Dominator** — объект, через который проходят все пути от GC root к группе других объектов; удалив его, освобождаешь всю группу.

**Подводный камень:** снятие heap dump вызывает `STW`-паузу, пропорциональную размеру heap — на 8 GB это 5–10 секунд полной остановки. В production обязательно согласуйте окно с SRE.

## Q19. Как найти memory leak с помощью профилирования?

`Memory leak` в Java — это не «потерянная» память, как в C, а объекты, которые уже не нужны логически, но GC не может их собрать, потому что на них остаётся живая ссылка. Поэтому суть поиска утечки — найти **эту лишнюю ссылку**.

**Стратегия поиска** — сравнить два снимка во времени и посмотреть, что растёт:

```mermaid
graph TD
    A[Heap растёт после Full GC] --> B[Снять 2 heap dump]
    B --> C["Сравнить histogram (delta)"]
    C --> D[Найти растущие классы]
    D --> E[Path to GC Roots]
    E --> F[Найти нежелательную ссылку]
    F --> G[Fix + verify]
```

**Практические шаги:**

```bash
# 1. Снять baseline dump
jcmd <PID> GC.heap_dump /tmp/heap1.hprof

# 2. Подождать (или дать нагрузку)
sleep 300

# 3. Снять второй dump
jcmd <PID> GC.heap_dump /tmp/heap2.hprof

# 4. Сравнить в MAT: File → Compare Heap Dumps
```

**Типичные причины утечек** (всё это — «забытые» живые ссылки):
- `static` коллекции без eviction — кэши без `maxSize` / TTL растут бесконечно.
- Listeners/callbacks, которые подписали, но не отписали при закрытии.
- `ThreadLocal` без `remove()` в пуле потоков — поток живёт долго и держит значение вечно (классический MDC leak).
- `ClassLoader` leak — особенно при hot redeploy: старый класслоадер не выгружается.
- `InputStream` / `Connection` без закрытия — формально не leak в heap, но держат native-ресурсы и файловые дескрипторы.

## Q20. (!) Как снять и анализировать thread dump?

`Thread dump` — это мгновенный снимок всех потоков JVM с их стек-трейсами и состояниями. Он отвечает на вопрос «чем сейчас заняты потоки и не застряли ли они»: один dump показывает срез, а несколько dump-ов подряд — что зависло (повторяющийся стек) против того, что просто работает.

**Снятие** (любой способ даёт один и тот же текст):

```bash
# Через jcmd (рекомендуемый способ)
jcmd <PID> Thread.print > /tmp/threads.txt

# Через jstack
jstack <PID> > /tmp/threads.txt

# Через kill (Unix) — не убивает процесс
kill -3 <PID>   # вывод в stdout JVM

# Несколько снимков с интервалом (для анализа тренда)
for i in 1 2 3; do jcmd <PID> Thread.print > /tmp/td_$i.txt; sleep 5; done
```

**Состояния потоков:**

| Состояние | Значение | На что обратить внимание |
|-----------|---------|--------------------------|
| `RUNNABLE` | Выполняется | Горячий код или I/O (может быть и off-CPU) |
| `BLOCKED` | Ждёт монитора | Lock contention — кто держит lock? |
| `WAITING` | `Object.wait()` / `LockSupport.park()` | Ожидание условия |
| `TIMED_WAITING` | `sleep()` / `wait(timeout)` | Polling или timeout |

**Что искать** (по убыванию частоты на собеседовании):
1. **Много потоков в `BLOCKED` на одном мониторе** → lock contention; дальше ищем, кто этот монитор `locked`.
2. **Потоки застряли в одном методе** в нескольких dump-ах подряд → deadlock или бесконечный цикл.
3. **Все потоки пула заняты** → исчерпание пула; проверить его размер и долгие операции внутри.

```
// Пример: lock contention в thread dump
"http-nio-8080-exec-15" BLOCKED
    at com.example.CacheService.get(CacheService.java:42)
    - waiting to lock <0x00000007a4c8e8d0> (a java.util.HashMap)
    
"http-nio-8080-exec-3" RUNNABLE
    at com.example.CacheService.put(CacheService.java:58)
    - locked <0x00000007a4c8e8d0> (a java.util.HashMap)
```

## Q21. Как находить lock contention через профилирование?

Lock contention — это когда потоки бóльшую часть времени не работают, а стоят в очереди за общим lock-ом. Профилирование тут отвечает на два вопроса: **на каком именно lock-е** копится очередь и **кто его держит**.

**Сигналы lock contention:**
- Много потоков в `BLOCKED` / `WAITING` в thread dump.
- Рост времени в monitor/lock событиях.
- **Negative scalability** — добавили потоков, а throughput упал: верный признак, что упёрлись в общий lock.

**Инструменты:**

```bash
# async-profiler: lock profiling
./asprof -d 30 -e lock -f lock.html <PID>
# Показывает: на каких lock-ах потоки ждут и кто их держит

# JFR: monitor events
jcmd <PID> JFR.start settings=profile duration=60s \
     filename=/tmp/locks.jfr
# Анализ jdk.JavaMonitorEnter, jdk.ThreadPark событий
```

**Типичные решения** (общая идея — дробить или убирать общий lock):
- `synchronized HashMap` → `ConcurrentHashMap` (lock striping — блокировка не на всю map, а на сегмент).
- Один глобальный lock → lock per partition (разнести данные по независимым lock-ам).
- `synchronized` → `ReadWriteLock`, если чтений сильно больше записей.
- `Lock` → lock-free структуры (`AtomicReference`, `LongAdder`), где можно обойтись без блокировок вовсе.

```java
// ПРОБЛЕМА: один lock на все операции
public class MetricsCollector {
    private final Map<String, Long> counters = new HashMap<>();
    
    public synchronized void increment(String key) { // global lock
        counters.merge(key, 1L, Long::sum);
    }
}

// РЕШЕНИЕ: ConcurrentHashMap + LongAdder
public class MetricsCollector {
    private final ConcurrentHashMap<String, LongAdder> counters = 
        new ConcurrentHashMap<>();
    
    public void increment(String key) { // lock-free
        counters.computeIfAbsent(key, k -> new LongAdder()).increment();
    }
}
```

## Q22. (!) Как анализировать GC и связать его с профилированием?

Ключевая мысль: GC почти никогда не «причина» — это следствие высокого темпа аллокаций. Поэтому анализ GC связывают с allocation-профилем: сперва смотрят, как часто и надолго встают паузы, а затем профилем находят код, который их провоцирует. Детальная настройка GC — в [Memory Management](memory-management-interview.md) и [JVM Performance Tuning](jvm-performance-tuning-interview.md); здесь — связка с profiling.

**Включение GC-логов:**

```bash
# JDK 17+ unified logging
java -Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=5,filesize=50m \
     -jar myapp.jar
```

**Ключевые метрики GC для связи с профилем:**

```mermaid
graph LR
    A[Allocation Rate] -->|высокий| B[Частые Young GC]
    B -->|promotion| C[Рост Old Gen]
    C -->|full| D[Full GC / Long Pause]
    D -->|p99 spike| E[Пользовательская деградация]
    
    style D fill:#f66,color:#fff
```

**Анализ GC через JFR:**

```bash
# GC-события в JFR
jfr print --events "jdk.GarbageCollection,jdk.GCPhasePause" recording.jfr

# Пример вывода:
# jdk.GCPhasePause {
#   gcId = 142
#   name = "Pause Young (Normal)"
#   duration = 12.4 ms
# }
```

**Инструменты визуализации:**
- [GCEasy](https://gceasy.io) — онлайн-анализатор GC-логов
- [GCViewer](https://github.com/chewiebug/GCViewer) — десктопный анализатор
- JMC → GC tab — анализ GC из JFR-записи
- Grafana + `jmx_exporter` — realtime GC-дашборд

## Q23. Как профилировать GC pauses и их влияние на latency?

**Связь GC pauses с latency:**

GC-паузы (`STW` — Stop-The-World) добавляются к latency запроса напрямую: пока сборщик держит мир остановленным, запрос просто ждёт. Поэтому если p99 = 100ms, а самая длинная GC-пауза = 80ms — именно GC и есть главный виновник хвоста latency, а не код обработки.

```bash
# Быстрый скрипт: извлечь паузы >50ms из GC-лога
grep "pause" gc.log | awk '$NF > 50 {print}'
```

**Профилирование аллокаций → снижение GC pressure:**

```java
// ПРОБЛЕМА: 100MB/s allocation rate из-за temporary объектов
public List<OrderDTO> toDto(List<Order> orders) {
    return orders.stream()
        .map(o -> new OrderDTO(               // новый DTO
            o.getId(),
            formatDate(o.getCreated()),        // новый String
            formatMoney(o.getTotal()),         // новый String
            o.getItems().stream()              // новый Stream + Iterator
                .map(ItemDTO::from)            // новый DTO на каждый item
                .collect(toList())             // новый ArrayList
        ))
        .collect(toList());                    // новый ArrayList
}

// РЕШЕНИЕ: переиспользование, pre-sized collections
public List<OrderDTO> toDto(List<Order> orders) {
    List<OrderDTO> result = new ArrayList<>(orders.size()); // pre-sized
    for (Order o : orders) {
        result.add(orderDtoPool.acquire(o)); // object pool для hot path
    }
    return result;
}
```

**Выбор GC-коллектора** — это компромисс между паузой и throughput: чем короче целевая пауза, тем больше CPU коллектор тратит на фоновую работу.

| Коллектор | Цель по паузе | Когда использовать |
|-----------|--------------|-------------------|
| `G1GC` | <200ms | Универсальный (default с JDK 9) |
| `ZGC` | <1ms | Низкая latency, большой heap |
| `Shenandoah` | <10ms | Низкая latency, RedHat/OpenJDK |
| `Parallel GC` | throughput | Batch-задачи, не критична latency |

## Q24. (!) Что такое APM и как он дополняет профилирование?

`APM (Application Performance Management)` — это платформа, которая сводит в одном UI трассировку, метрики, профилирование и логи. Главная ценность для профилирования — она перекидывает мост от «какой запрос медленный» (distributed trace) к «какой код в нём медленный» (profile), чего по отдельности ни мониторинг, ни профайлер не дают.

**Популярные APM-решения:**

| APM | Профилирование | Трассировка | Особенности |
|-----|---------------|-------------|-------------|
| `Datadog` | Continuous Profiler | Distributed traces | Корреляция traces → profiles |
| `New Relic` | Thread Profiler | Distributed traces | Code-level visibility |
| `Dynatrace` | PurePath | Полная трассировка | AI-driven root cause |
| `Elastic APM` | Есть (JFR-based) | Distributed traces | Open source core |
| `Grafana` + `Pyroscope` | Continuous profiling | Tempo | Полностью open source |

**Ключевая интеграция APM с профилированием:**

```mermaid
graph TB
    A[Пользовательский запрос] --> B[Distributed Trace]
    B --> C[Медленный span]
    C --> D[Profile для этого span]
    D --> E[Горячий метод]
    E --> F[Оптимизация]
    
    style C fill:#ff9,color:#333
    style D fill:#9f9,color:#333
```

**Сценарий применения:** в `Datadog` кликаешь на медленный span в trace — и сразу видишь CPU profile именно этого запроса. Раньше пришлось бы вручную воспроизводить нагрузку и угадывать окно профилирования; APM делает связь «медленный запрос → горячий метод» мгновенной.

## Q25. (!) Что такое continuous profiling и зачем он нужен?

`Continuous profiling` — это постоянное (24/7) low-overhead профилирование, работающее в production всегда. Принципиальное отличие от ad-hoc профилирования: профиль не нужно успеть снять во время инцидента — он уже записан и доступен ретроспективно.

**Зачем нужен** (всё крутится вокруг «профиль был снят заранее»):
- Проблема случилась вчера в 3:00 и больше не повторяется — профиль того момента уже есть.
- Можно diff-ить профили между версиями и деплоями: что изменилось после релиза.
- **Regression detection** — ловит постепенные деградации, которые незаметны на одном снимке.
- **Cost attribution** — CPU flame graph показывает, например, что 20% процессора уходит в логирование, то есть в живые деньги на инфраструктуру.

**Архитектура:**

```mermaid
graph LR
    subgraph "Production pods"
        A1[Agent + profiler] --> B[Collector]
        A2[Agent + profiler] --> B
        A3[Agent + profiler] --> B
    end
    B --> C[Storage]
    C --> D[UI: flame graphs, diff, trends]
```

**Инструменты:**

| Инструмент | Тип | Overhead | Хранение |
|-----------|-----|---------|----------|
| `Datadog Continuous Profiler` | SaaS | ~1% | Cloud |
| `Pyroscope` (Grafana) | OSS/Cloud | ~1% | Local/Cloud |
| `Parca` | OSS | ~1% | Local |
| `JFR` + custom pipeline | DIY | ~1% | Custom |
| `async-profiler` + custom | DIY | ~1-2% | Custom |

**Пример: интеграция Pyroscope с Spring Boot:**

```java
// build.gradle
implementation 'io.pyroscope:agent:0.13.0'

// application.yml
pyroscope:
  application-name: order-service
  server-address: http://pyroscope:4040
  profiling-event: cpu,alloc,lock
  profiling-interval: 10ms
  upload-interval: 15s
```

## Q26. Как работает Datadog Continuous Profiler?

`Datadog Continuous Profiler` — промышленное SaaS-решение для постоянного профилирования в production. Подключается одним Java-agent-ом, после чего профиль собирается непрерывно и связывается с трейсами того же сервиса.

**Подключение:**

```bash
# Подключение Java-agent
java -javaagent:/opt/dd-java-agent.jar \
     -Ddd.profiling.enabled=true \
     -Ddd.profiling.allocation.enabled=true \
     -Ddd.service=order-service \
     -Ddd.env=production \
     -Ddd.version=1.2.3 \
     -jar myapp.jar
```

**Ключевые возможности:**
- **Trace → Profile** корреляция — из медленного span сразу открыть профиль конкретного запроса.
- **Endpoint profiling** — агрегированный flame graph по конкретному HTTP-эндпоинту, а не по всему сервису.
- **Version comparison** — diff flame graph между версиями (например, v1.2.2 и v1.2.3): видно, какой метод подорожал после релиза.
- **Cost attribution** — перевод CPU-времени метода в деньги: «этот метод стоит $X/месяц в cloud».
- **Timeline view** — CPU, allocation, lock profiling на единой временной оси.

**Типы профилей:**
- CPU time — где тратится CPU
- Wall time — общее время (on-CPU + off-CPU)
- Allocations — где создаются объекты
- Heap live objects — что сейчас живёт в heap
- Lock contention — ожидание на lock-ах
- Thrown exceptions — откуда летят исключения

## Q27. (!) Как профилировать production безопасно?

Безопасное production-профилирование держится на одном принципе: overhead должен быть незаметен для пользователя, а любое действие — обратимо. Отсюда конкретные правила:

1. **Короткие сессии** — 30–60 секунд: достаточно для статистики, мало для влияния на нагрузку.
2. **Sampling, а не instrumentation** — overhead держим <2%.
3. **Минимально достаточный набор событий** — не включать всё сразу, каждое событие стоит overhead.
4. **Чёткий rollback-план** — заранее знать команду stop, если пойдёт не так.
5. **Согласовать с SRE** — кто включает, кто анализирует, где хранятся артефакты.

**Чеклист перед production profiling:**

```markdown
- [ ] Проверить overhead инструмента на staging
- [ ] Убедиться, что есть место на диске для артефактов
- [ ] Согласовать окно с on-call SRE
- [ ] Подготовить команды stop/rollback
- [ ] Не профилировать во время пиковой нагрузки (если можно)
- [ ] Не снимать heap dump без крайней необходимости (STW пауза!)
```

**Безопасные инструменты для production:**

| Инструмент | Overhead | Безопасность |
|-----------|---------|-------------|
| `JFR` (default profile) | <1% | Максимально safe |
| `async-profiler` (cpu) | ~1% | Safe |
| `async-profiler` (alloc) | ~2% | Safe |
| `jcmd Thread.print` | Мгновенный | Safe |
| `jmap heap dump` | STW пауза | Осторожно! |
| Instrumentation agents | 10-50% | Не для production |

## Q28. Как профилировать сервис в Kubernetes/Docker?

Профилирование в контейнере отличается от профилирования на «голой» машине тремя вещами: изоляцией, эфемерностью pod-ов и cgroup-лимитами. Если их не учесть, либо не подключишься к процессу, либо потеряешь артефакт, либо сделаешь неверные выводы. Подробнее о `Kubernetes` — в [Kubernetes](../devops/kubernetes-interview.md).

**Проблемы:**
- **`cgroup` limits** — JVM может не видеть реальные лимиты CPU/memory и считать, что у неё все ядра хоста.
- **Ephemeral pods** — файл профиля исчезнет вместе с pod при рестарте, поэтому его надо успеть скопировать наружу.
- **Network isolation** — к процессу не всегда можно подключиться извне через `jcmd`; обычно работают через `kubectl exec`.

**Решения:**

```bash
# 1. Exec в pod и снять профиль
kubectl exec -it <pod> -- jcmd 1 JFR.start duration=60s \
    filename=/tmp/recording.jfr

# 2. Скопировать файл
kubectl cp <pod>:/tmp/recording.jfr ./recording.jfr

# 3. async-profiler в Docker (нужны capabilities)
# Dockerfile:
# COPY async-profiler/ /opt/async-profiler/
kubectl exec -it <pod> -- /opt/async-profiler/asprof -d 30 \
    -f /tmp/profile.html 1
```

**Docker security requirements для async-profiler:**

```yaml
# В Kubernetes: добавить securityContext
securityContext:
  capabilities:
    add:
      - SYS_PTRACE    # для attach к процессу
  # или для perf_events:
  # privileged: true  # крайний случай
```

**Учёт cgroup limits:**

```bash
# JDK 17+ автоматически определяет cgroup limits
# Проверить:
jcmd 1 VM.info | grep "container"
# Available CPUs: 2 (container limited)
# Memory Limit: 4096M (container limited)
```

**Подводный камень:** снять профиль локально (8 CPU, 32 GB RAM) и перенести выводы на pod (2 CPU, 4 GB RAM). При других лимитах меняется и поведение GC, и количество доступных потоков, и горячие места — профиль будет нерепрезентативным.

## Q29. Как связать profiling с Prometheus/Grafana и алертингом?

Идея в том, чтобы профилирование запускалось не вручную «когда вспомнили», а автоматически — по алерту от метрик. Prometheus детектит деградацию, по типу проблемы триггерится нужный профиль, результат прилетает в Grafana как flame graph (см. [метрики и трассировка](../monitoring/metrics-tracing-interview.md)):

```mermaid
graph TD
    A[Prometheus: метрики SLI] -->|алерт| B[Alert Manager]
    B --> C{Тип проблемы}
    C -->|CPU > 80%| D[Trigger CPU profile]
    C -->|p99 > SLO| E[Trigger wall profile]
    C -->|Heap > 80%| F[Trigger alloc profile]
    D --> G[Grafana: flame graph]
    E --> G
    F --> G
    G --> H[Root cause analysis]
    H --> I[Fix + Deploy]
    I -->|verify| A
```

**Автоматический trigger профилирования:**

```java
// Spring Boot Actuator + custom endpoint
@Component
public class ProfilingTrigger {
    
    @EventListener
    public void onHighCpu(HighCpuEvent event) {
        // Автоматически запустить JFR при CPU > 80%
        Runtime.getRuntime().exec(new String[]{
            "jcmd", "1", "JFR.start",
            "name=auto-cpu", "duration=60s",
            "filename=/tmp/auto-" + Instant.now() + ".jfr"
        });
    }
}
```

**Grafana + Pyroscope** для continuous profiling:
- Встроенный flame graph panel в Grafana
- Annotations на графиках — когда был деплой, когда снят профиль
- Exemplar links: из метрики → в trace → в profile

Так профилирование становится частью операционного цикла, а не "разовой магией".

## Q30. Какие anti-patterns в profiling чаще всего встречаются?

Почти все ошибки профилирования сводятся к одному: данные снимаются в нерепрезентативных условиях или выводы делаются без проверки. Ниже — самые частые из них.

| Anti-pattern | Почему плохо | Правильный подход |
|-------------|-------------|-------------------|
| Профилируют без репрезентативной нагрузки | Результат не отражает production | Использовать realistic load (replay, shadow traffic) |
| Оптимизируют micro-hotspot без бизнес-эффекта | Потраченное время без улучшения SLI | Сначала проверить влияние на p99/throughput |
| Делают выводы по одному профилю | Может быть артефакт / случайный всплеск | Минимум 3 прогона, сравнивать |
| Смешивают cold-start и steady-state | JIT-компиляция искажает картину | Дать warm-up перед снятием профиля |
| Игнорируют off-CPU | Пропускают I/O, lock waits, network | Снимать wall-clock профиль |
| Профилируют с heavy instrumentation в prod | Искажение поведения, деградация | Только sampling с low overhead |
| Анализируют локальный профиль для prod | Другое железо, другая нагрузка | Профилировать в production-like среде |
| Оптимизируют GC-флагами вместо кода | Лечат симптом, не причину | Сначала снизить allocation rate |

## Q31. Как построить системный процесс профилирования в команде?

Зрелость процесса профилирования проходит три уровня — от реактивного «тушим пожар» до встроенного в CI/CD. Цель — сдвинуть команду от ручного профилирования по факту инцидента к автоматическому обнаружению регрессий ещё до релиза.

```mermaid
graph TB
    subgraph "Level 1: Reactive"
        A[Инцидент] --> B[Ad-hoc profiling]
    end
    
    subgraph "Level 2: Proactive"
        C[Continuous profiling в prod] --> D[Regression detection]
        D --> E[Automatic alerts]
    end
    
    subgraph "Level 3: Integrated"
        F[Profile в CI/CD pipeline] --> G[Performance budgets]
        G --> H[Block merge если деградация]
    end
```

**Практические шаги:**

1. **Инструментарий** — выбрать и стандартизировать инструменты (`JFR` + `async-profiler` + APM)
2. **Runbook** — документировать: как снять профиль, где хранить, как интерпретировать
3. **Continuous profiling** — развернуть `Pyroscope` или подключить `Datadog Profiler`
4. **Baseline** — зафиксировать "нормальный" профиль для ключевых сценариев
5. **Performance tests** — интегрировать профилирование в нагрузочные тесты
6. **Review** — на post-mortem всегда включать анализ профиля

## Q32. (!) Как ответить про profiling на senior-раунде за 1 минуту?

Сильный ответ на senior-раунде укладывается в четыре такта и показывает не знание команд, а системный подход: симптом → диагностика → находка → измеримый результат. Шаблон:

**1. Симптом:** "p99 вырос после релиза X".

**2. Диагностика:** "По метрикам в [Grafana](../monitoring/observability-interview.md) нашли окно деградации, сняли `JFR` + `async-profiler`".

**3. Находка:** "Lock contention в connection pool + лишние аллокации в сериализации JSON".

**4. Решение/результат:** "Переделали синхронизацию на `ConcurrentHashMap`, закэшировали `ObjectMapper`, p99 −35%, CPU −15%".

**Ключевые моменты для senior:**
- Показать **системный подход**: метрики → гипотеза → профиль → подтверждение
- Упомянуть конкретные инструменты: `JFR`, `async-profiler`, `flame graphs`
- Знать разницу `sampling` vs `instrumentation`, `on-CPU` vs `off-CPU`
- Понимать `safepoint bias` и почему `async-profiler` точнее
- Говорить об **измеримом результате** — "CPU −X%, p99 −Y%"
- Показать, что profiling — часть операционного процесса, а не разовая акция

## Q33. (!) Как анализировать heap dump с Eclipse MAT: практический сценарий?

**Eclipse MAT** (`Memory Analyzer Tool`) — де-факто стандарт для анализа Java heap dump; тянет файлы от сотен MB до десятков GB. Практический сценарий — это конвейер из четырёх инструментов внутри MAT: Leak Suspects → Dominator Tree → OQL → Histogram. Каждый отвечает на свой вопрос, и идти по ним стоит именно в этом порядке.

**Получение heap dump:**
```bash
# 1. При OOM (автоматически с флагом)
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp/heapdump.hprof

# 2. Вручную без остановки процесса
jmap -dump:format=b,file=/tmp/heapdump.hprof <PID>

# 3. Через jcmd (предпочтительнее, live=true только живые объекты)
jcmd <PID> GC.heap_dump /tmp/heapdump.hprof

# 4. В Kubernetes (сначала exec в pod)
kubectl exec -it my-pod -- jcmd 1 GC.heap_dump /tmp/heapdump.hprof
kubectl cp my-pod:/tmp/heapdump.hprof ./heapdump.hprof
```

**Анализ в Eclipse MAT — шаги:**

**1. Leak Suspects Report** — первый шаг всегда
```
File → Open Heap Dump → Reports → Leak Suspects
```
MAT автоматически ищет объекты с аномально большим retained heap.

**2. Dominator Tree** — кто удерживает больше всего памяти
```
Window → Heap Dump Details → Dominator Tree
```
Показывает дерево: если удалить объект X, сколько памяти освободится.

```
# Пример вывода Dominator Tree:
Class                          Shallow Heap   Retained Heap
HashMap$Entry[]                    1.2 MB        287 MB  ← проблема!
  └─ SomeCache.cache                  32 B        287 MB
      └─ HashMap                      48 B        287 MB
```

**3. OQL (Object Query Language)** — SQL для heap
```sql
-- Найти все HashMap с размером > 10000 записей
SELECT * FROM java.util.HashMap h WHERE h.size > 10000

-- Найти все String, содержащие "password" (поиск утечки секретов)
SELECT s FROM java.lang.String s WHERE s.toString().contains("password")

-- Найти все экземпляры кастомного класса
SELECT * FROM com.company.app.service.OrderService
```

**4. Histogram** — сколько объектов каждого типа
```
Window → Heap Dump Details → Histogram

Наиболее опасные признаки:
- Много byte[] → буферы не освобождены (FileInputStream, ByteArrayOutputStream)
- Много char[] или String → строки накапливаются (кэши, логи в памяти)
- Много ThreadLocal → MDC leak через thread pools
```

**Типичный сценарий: ThreadLocal leak**
```java
// Причина: после задачи MDC не очищается
executor.submit(() -> {
    MDC.put("sessionId", session.getId());
    processRequest();
    // MDC.clear() забыли!
});

// В MAT: ищем ThreadLocalMap$Entry с большим retained heap
// OQL: SELECT * FROM java.lang.ThreadLocal$ThreadLocalMap$Entry
```

**Рекомендации:**
- Используйте `jcmd GC.heap_dump` вместо `jmap` — он безопаснее для production.
- Флаг `live=true` (в jcmd) снимает только живые объекты — dump меньше и анализируется быстрее.
- Качайте dump на свою машину и анализируйте локально: MAT прожорлив по памяти и нагрузит production-сервер.

## Q34. Как читать flame graph и находить проблемы?

**Flame graph** — визуализация тысяч стектрейсов, схлопнутых в одну фигуру: ширина блока = доля ресурса, высота = глубина стека. Цель чтения — найти самый широкий блок (где «горит» ресурс) и спуститься до конкретного метода-виновника.

**Правила чтения:**
```
┌──────────── processOrder (50% CPU) ──────────────┐  ← широкий = дорогой
│  ┌──── serializeJson (30%) ───┐  ┌─ dbQuery(20%)─┐ │
│  │ Jackson.write(30%)         │  │ HikariCP(20%) │ │
│  └────────────────────────────┘  └───────────────┘ │
└──────────────────────────────────────────────────────┘
         ↑ ось X = % времени (не хронологический порядок!)
         ↑ ось Y = глубина стека
```

**Что искать:**
1. **Широкие плоские вершины** — метод дорог сам по себе (а не из-за вызовов внутри него) → это bottleneck.
2. **Тонкие высокие башни** — глубокая рекурсия или длинная цепочка вызовов → кандидат на упрощение.
3. **Неожиданные методы наверху** — например, `ObjectMapper.writeValue` занимает 40% → значит, его пересоздают на каждый запрос → кэшировать ObjectMapper.

**Типы flame graphs в async-profiler:**
```bash
# CPU flame graph (on-CPU — что реально выполнялось)
./profiler.sh -e cpu -d 30 -f cpu.html <PID>

# Allocation flame graph (что выделяло память)
./profiler.sh -e alloc -d 30 -f alloc.html <PID>

# Lock contention (что ждало мьютексов)
./profiler.sh -e lock -d 30 -f lock.html <PID>

# Wall clock (on-CPU + off-CPU, видны блокировки IO)
./profiler.sh -e wall -d 30 -f wall.html <PID>
```

**Практический анализ: пример из production**
```
Симптом: p99 latency 2s при нагрузке 500 RPS

Flame graph показал:
  processOrder (100%)
  └─ getConnectionFromPool (65%)   ← занимает 65% времени!
      └─ HikariPool.getConnection (65%)
          └─ AbstractQueuedSynchronizer.acquireSharedInterruptibly (65%)

Root cause: пул соединений = 10, но 100 потоков ждут → deadlock-подобная ситуация

Решение:
hikari.maximum-pool-size: 50
hikari.connection-timeout: 3000

Результат: p99 200ms → 95ms
```

**Ключевые паттерны:**
- `GC` в верхушке flame graph → много allocation, нужен alloc-профиль
- `park` / `wait` / `sleep` в верхушке → off-CPU паузы → используй wall clock профиль
- `Unsafe.park` → потоки ждут Lock → используй lock-профиль

---

## Q35. Async Profiler vs JFR — когда что выбирать, отличия

Коротко: JFR берут за широту контекста и нулевой деплой, async-profiler — за точность CPU/allocation без safepoint bias. Это не конкуренты, а связка для разных задач.

**Java Flight Recorder (JFR)** — «штатный регистратор» JVM:
- Встроен в JDK (с Java 11 — бесплатно), не требует доустановки.
- Низкий overhead: ~1–2% для профиля по умолчанию.
- Собирает широкий контекст: GC, I/O, locks, JIT, network, allocation — а не только CPU.
- Sampling идёт через safepoints → есть **safepoint bias**: методы, редко доходящие до safepoint, недооцениваются.
- **Лучший выбор:** «что вообще происходит в JVM», непрерывная запись в production.

**async-profiler** — «точный CPU-микроскоп»:
- Работает через `perf_events` (Linux) и `AsyncGetCallTrace` — не привязан к safepoints, поэтому без bias.
- Видит «застрявшие» методы, которые JFR пропускает: native, системные вызовы, JNI.
- Поддерживает CPU, allocation, wall clock, lock, `perf`-события (cache misses и др.).
- Требует Linux для полного функционала, прав на perf_events или `CAP_SYS_ADMIN`, отдельного агента.
- **Лучший выбор:** точный CPU bottleneck, allocation hotspot, разбор нативного кода.

```bash
# JFR — запуск
java -XX:StartFlightRecording=duration=60s,filename=profile.jfr,settings=profile MyApp

# async-profiler — запуск
./profiler.sh -e cpu -d 60 -f profile.html <PID>         # CPU
./profiler.sh -e alloc -d 60 -f alloc.html <PID>          # Allocation
./profiler.sh -e wall -d 60 -f wall.html <PID>            # Wall clock (on+off CPU)
```

| Критерий | JFR | async-profiler |
|---|---|---|
| Safepoint bias | Есть | Нет |
| Overhead | ~1-2% | ~1-5% (зависит от rate) |
| Нативный код | Частично | Да (`perf_events`) |
| Контекст | Богатый (GC, IO, etc.) | CPU/alloc/lock |
| ОС | Любая | Linux (полный), macOS (CPU only) |
| Деплой | Встроен в JDK | Отдельный агент |

---

## Q36. Profiling в production — low-overhead инструменты

**Главное правило:** инструмент для production обязан укладываться в overhead < 3%, не блокировать рабочие потоки и не раздувать latency больше чем на ~5%. Под этот бюджет подходят только sampling-инструменты, спроектированные для непрерывной работы.

**Инструменты low-overhead profiling:**

**1. JFR (Continuous Recording):**
```bash
# Постоянная запись с rolling-окном 1 час
java -XX:StartFlightRecording=name=continuous,settings=profile,disk=true,\
  maxage=1h,maxsize=500m MyApp

# Скачать дамп по требованию (без остановки)
jcmd <PID> JFR.dump name=continuous filename=/tmp/dump.jfr
```

**2. async-profiler с низким rate:**
```bash
# Низкочастотный sampling (100 samples/sec вместо 1000)
./profiler.sh start -e cpu --interval 10000000 <PID>  # 10ms interval
```

**3. Datadog / Pyroscope (Continuous Profiling):**
```yaml
# Datadog Java Agent — включить профилирование
DD_PROFILING_ENABLED=true
DD_PROFILING_CPU_ENABLED=true
DD_PROFILING_ALLOCATION_ENABLED=true
# Overhead: ~2-5%
```

**4. Micrometer + JFR метрики:**
```java
// Экспортировать JFR события в Prometheus
@Bean
public JfrMeterRegistry jfrMeterRegistry(JfrConfig config) {
    return new JfrMeterRegistry(config, Clock.SYSTEM);
}
```

**Подводные камни (нельзя в production):**
- `jstack` в tight loop — каждый вызов это Stop-the-World, в цикле кладёт latency.
- instrumentation-профайлер (YourKit, JProfiler) с полным трассированием — overhead 50–200%.
- heap dump без предупреждения — остановка JVM на секунды/минуты.

**Что можно:**
- JFR profile recording (`settings=profile`, не `default` — чуть дороже, зато данных больше).
- async-profiler с низким rate на короткое время (30–60 секунд).
- Continuous profiling агенты (Datadog, Pyroscope) — изначально спроектированы под production.

---

## Q37. Heap Dump анализ — MAT, VisualVM, утечки памяти

**Heap dump** — снимок всего состояния памяти JVM в формате `.hprof`: все объекты, ссылки и классы на момент снятия. По нему ищут утечки, отвечая на вопрос «какие объекты накопились и кто мешает их собрать GC».

**Как получить heap dump:**
```bash
# 1. Автоматически при OutOfMemoryError
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof MyApp

# 2. По требованию (без остановки приложения — кратковременная пауза)
jcmd <PID> GC.heap_dump /tmp/heapdump.hprof
jmap -dump:format=b,file=/tmp/heapdump.hprof <PID>  # устаревший способ

# 3. Через JFR (менее полный, но менее инвазивный)
```

**Eclipse MAT (Memory Analyzer Tool) — основные операции:**

```
1. Open heap dump → MAT строит Object Graph
2. Leak Suspects Report → автоматически находит подозрительные accumulation points
3. Dominator Tree → объекты, удерживающие больше всего памяти
4. OQL (Object Query Language):
   SELECT * FROM java.util.HashMap$Entry WHERE key.toString().startsWith("SESSION_")
5. Retained Heap → сколько памяти освободится, если удалить объект
```

**Типичные утечки и как выглядят в MAT:**

```
Утечка 1: Static Map без ограничения размера
Dominator Tree: HashMap → удерживает 800MB → ключи — никогда не очищаемые Session ID

Утечка 2: ThreadLocal без remove()
Shallow heap ThreadLocalMap$Entry: много объектов с expired thread refs

Утечка 3: Listeners/Callbacks не отписаны
OutgoingReferences: EventBus → List<Listener> → 10k объектов → все живые
```

**Практический checklist** (порядок не случаен — от «что весит» к «почему не собирается»):
1. Открыть Dominator Tree → найти топ-10 по Retained Heap (кто держит больше всего).
2. Для подозреваемого посмотреть Path to GC Roots — почему объект не собирается GC.
3. Запустить Leak Suspects Report для автоматических подсказок.
4. Проверить Duplicate Strings — нередко 30–50% heap съедают дубликаты строк.

---

## Q38. Thread Dump анализ — deadlock detection, jstack

**Thread dump** — мгновенный снимок состояния всех потоков JVM. Его основное применение — найти deadlock (jstack помечает его сам) и понять, чем заняты потоки: считают, ждут lock или висят в пуле.

**Как получить:**
```bash
# jstack — классический способ
jstack <PID> > thread_dump.txt
jstack -l <PID>  # с блокировками (-l = long listing)

# jcmd — современный способ
jcmd <PID> Thread.print > thread_dump.txt

# kill -3 (SIGQUIT) — дамп в stdout приложения
kill -3 <PID>

# JFR — включить запись состояния потоков
```

**Состояния потоков:**
```
RUNNABLE      — выполняется или ожидает CPU
BLOCKED       — ждёт монитора (synchronized)
WAITING       — Object.wait(), LockSupport.park() без таймаута
TIMED_WAITING — Thread.sleep(), wait(timeout), park(timeout)
```

**Обнаружение deadlock:**
```
"Thread-1" waiting to lock <0x000...> held by "Thread-2"
"Thread-2" waiting to lock <0x000...> held by "Thread-1"
← jstack автоматически выводит "Found 1 deadlock"
```

**Анализ thread dump — паттерны:**
```
# Много потоков в состоянии BLOCKED на одном мониторе
→ Contention: один поток держит synchronized слишком долго

# Все потоки в WAITING на HikariPool
"at com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:213)"
→ Pool exhaustion: увеличить pool size или найти долгие транзакции

# Потоки в TIMED_WAITING на sleep()
→ Проверить, нет ли busy-wait антипаттерна

# Много потоков RUNNABLE, высокое CPU
→ CPU-intensive работа или tight loop без park/sleep
```

**Совет:** для тысяч потоков не читайте dump глазами — он группирует одинаковые стеки. Используйте анализаторы: fastThread.io (онлайн), встроенный в IntelliJ IDEA, IBM Thread and Monitor Dump Analyzer, VisualVM Threads view.

---

## Q39. CPU-профилирование: чем отличаются sampling и instrumentation?

Это два полюса по оси «точность vs накладные расходы»: sampling дёшев и пригоден для production, но статистичен; instrumentation точен, но неприемлемо дорог. Для собеседования важно объяснить именно этот компромисс.

**Sampling profiling** — периодические «снимки» стеков:
- С заданной частотой (например, 1000 раз/сек) прерывает каждый поток и записывает его стектрейс.
- Overhead зависит от частоты сэмплирования, а не от числа методов — поэтому он предсказуем.
- **Safepoint bias (у JVMTI-based):** прерывание ловится только в safepoint → код между safepoints невидим.
- **async-profiler** обходит это через `AsyncGetCallTrace`, работая вне safepoints.

```bash
# async-profiler — sampling CPU (10ms interval = 100 samples/sec)
./profiler.sh -e cpu --interval 10000000 -d 30 -f cpu.html <PID>

# JFR — sampling CPU
java -XX:StartFlightRecording=duration=30s,settings=profile,filename=cpu.jfr MyApp
# JFR по умолчанию: 10ms sampling rate
```

**Instrumentation profiling** — замер каждого вызова:
- В каждый метод вшивается байт-код для замера времени.
- 100% покрытие — видны все вызовы и точное время, даже у короткоживущих методов.
- Overhead 10–200× — неприемлемо для production.
- Применяют в разработке и тест-окружении, когда нужен точный анализ конкретного метода.

```java
// Ручная инструментация (micro-benchmark)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public void benchmarkJsonSerialization(Blackhole bh) {
    bh.consume(objectMapper.writeValueAsString(payload));
}
// JMH — Java Microbenchmark Harness — правильный способ измерять
```

**Когда что:**
| Ситуация | Инструмент |
|---|---|
| Production CPU hotspot | async-profiler sampling |
| Точное время метода в dev | JMH + instrumentation |
| Регрессия между версиями | JMH benchmark |
| Нативный код + Java | async-profiler + perf_events |

---

## Q40. Профилирование аллокаций: что такое TLAB и allocation rate?

Allocation profiling отвечает на вопрос «какой код создаёт больше всего объектов» — а это и есть главный драйвер нагрузки на GC. Чтобы понимать его метрики, нужны два понятия: TLAB и allocation rate.

**TLAB (Thread-Local Allocation Buffer)** — персональный буфер потока в young generation. Аллокация в нём — это просто сдвиг указателя, без синхронизации, поэтому она почти бесплатна. Именно по событиям заполнения TLAB профайлеры и считают аллокации.

**Allocation rate** — сколько байт/объектов выделяется в секунду. Цепочка проста: высокий allocation rate → частые minor GC → рост GC pressure → паузы и просадка latency.

**Как профилировать allocation:**

```bash
# async-profiler — allocation profiling (TLAB events)
./profiler.sh -e alloc -d 30 -f alloc.html <PID>
# Показывает: какие методы аллоцируют больше всего байт

# JFR — allocation sampling
java -XX:StartFlightRecording=duration=30s,\
  +jdk.ObjectAllocationInNewTLAB#enabled=true,\
  +jdk.ObjectAllocationOutsideTLAB#enabled=true,\
  filename=alloc.jfr MyApp
```

**Интерпретация allocation flame graph:**
```
processOrder (100% allocation)
├── JsonSerializer.serialize (60%)   ← много временных String объектов
│   └── String.format (40%)          ← заменить на StringBuilder
└── Hibernate.buildQuery (30%)       ← много QueryImpl объектов → query cache
```

**Типичные проблемы и решения:**
```java
// ПРОБЛЕМА: String.format в hot path → много String objects
log.debug("Processing order {} for customer {}", String.format("(%d, %s)", id, name));
// РЕШЕНИЕ: Lazy logging + прямая конкатенация
log.debug("Processing order ({}, {}) for customer", id, name);

// ПРОБЛЕМА: new ArrayList<>() внутри цикла
for (Order order : orders) {
    List<Item> items = new ArrayList<>();  // N аллокаций
    items.addAll(order.getItems());
}
// РЕШЕНИЕ: переиспользование или stream
orders.stream().flatMap(o -> o.getItems().stream()).collect(toList());

// Мониторинг allocation rate через JMX / Micrometer
Gauge.builder("jvm.gc.allocation.rate", ...)
```

**Эмпирическое правило:** для сервиса с умеренной нагрузкой allocation rate > 500 MB/s — повод присмотреться: почти наверняка где-то льются временные объекты в hot path.

---

## Q41. Профилирование запросов к БД: slow query log и EXPLAIN

Профилирование БД-запросов идёт в два этапа: сначала находим «кто медленный» (slow query log и `pg_stat_statements`), затем разбираем «почему медленный» (`EXPLAIN ANALYZE`). Лезть в `EXPLAIN` до того, как нашли проблемный запрос, — пустая трата времени.

**1. Найти медленные запросы — slow query log в PostgreSQL:**
```sql
-- postgresql.conf (или ALTER SYSTEM)
log_min_duration_statement = 100   -- логировать запросы дольше 100ms
log_duration = on
log_statement = 'all'              -- осторожно: очень много логов в production

-- pg_stat_statements — агрегированная статистика
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

SELECT query,
       calls,
       round(total_exec_time::numeric / calls, 2) AS avg_ms,
       round(total_exec_time::numeric, 2) AS total_ms,
       rows / calls AS avg_rows
FROM pg_stat_statements
ORDER BY avg_ms DESC
LIMIT 20;
```

**2. Понять причину — `EXPLAIN ANALYZE`:**
```sql
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT o.*, c.name
FROM orders o
JOIN customers c ON c.id = o.customer_id
WHERE o.status = 'PENDING' AND o.created_at > now() - interval '1 day';

-- Что искать:
-- Seq Scan на большой таблице → нет индекса → CREATE INDEX
-- Nested Loop с большим числом строк → рассмотреть Hash Join
-- "Rows Removed by Filter" большое → selectivity индекса низкая
-- "Buffers: shared hit=X read=Y" → read >> hit → много disk I/O
-- actual time >> estimated time → устаревшая статистика → ANALYZE
```

**Индексы — быстрый checklist:**
```sql
-- Найти таблицы без индексов или с редко используемыми индексами
SELECT relname, seq_scan, idx_scan,
       round(100.0 * idx_scan / NULLIF(seq_scan + idx_scan, 0), 1) AS idx_pct
FROM pg_stat_user_tables
WHERE seq_scan > 100
ORDER BY seq_scan DESC;

-- Найти неиспользуемые индексы
SELECT indexrelname, idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0 AND indexrelname NOT LIKE 'pg_%';
```

**Spring/Hibernate — включить логирование запросов:**
```yaml
# application.yml
spring.jpa.show-sql: false  # в production не включать!
logging.level.org.hibernate.SQL: DEBUG       # SQL
logging.level.org.hibernate.orm.jdbc.bind: TRACE  # параметры

# p6spy — профилирование с временем выполнения
# datasource-proxy — для детальных метрик
```

---

## Q42. Profiling реактивных приложений — особенности Project Reactor

Главная проблема профилирования реактивных приложений: классические инструменты привязаны к потоку, а в реактивной модели один поток (обычно по одному на CPU) обслуживает множество запросов, перескакивая между ними. Поэтому thread dump и CPU-профиль «по потокам» не отвечают на вопрос «к какому запросу относится этот стек».

**Проблемы стандартного profiling:**
```
Thread-1 (reactor-http-nio-1): RUNNABLE
  processOrder → flatMap → map → ...
← Непонятно, к какому запросу принадлежит стектрейс!
```

**Reactor Debugger Agent:**
```java
// Включить в dev/test — overhead до 100%, не для production!
ReactorDebugAgent.init();  // из io.projectreactor:reactor-tools

// Или через блокирующий debugger
Hooks.onOperatorDebug();  // аналогично, overhead высокий
```

**Reactor Context для трассировки:**
```java
// Micrometer Tracing + Reactor
// spring-boot-starter-actuator + reactor-core + micrometer-tracing
Flux<Order> orders = orderService.getOrders()
    .contextWrite(Context.of("traceId", traceId));
// Трейс пробрасывается через Context, виден в Zipkin/Jaeger
```

**Checkpoint — найти "где в цепочке проблема":**
```java
orderFlux
    .map(this::enrich)
    .checkpoint("after-enrich")   // имя появится в stacktrace при ошибке
    .flatMap(this::persist)
    .checkpoint("after-persist")
    .subscribe();
```

**async-profiler + wall clock для реактивных приложений:**
```bash
# Wall clock профиль — видит on-CPU И off-CPU (ожидание I/O)
./profiler.sh -e wall -d 30 --wall-sampling-interval 10ms -f wall.html <PID>
# Позволяет увидеть, где реально тратится время (в т.ч. ожидание БД)
```

**Метрики для реактивных приложений:**
```yaml
# application.yml
management.metrics.enable.reactor: true
# Экспортирует: reactor.flow.duration, reactor.flow.errors, pending requests
```

**Ключевые метрики реактивного сервиса** (на них смотрят в первую очередь): event loop utilization — главный индикатор, > 80% означает, что event loop стал bottleneck; pending count — длина очереди необработанных запросов; upstream latency через r2dbc/WebClient — где реально ждём внешние системы.

---

## See also

- [JVM Performance Tuning](jvm-performance-tuning-interview.md) — настройка JVM-параметров
- [Memory Management](memory-management-interview.md) — управление памятью и GC
- [JVM Fundamentals](../jvm/jvm-interview.md) — основы JVM
- [Метрики и трассировка](../monitoring/metrics-tracing-interview.md) — Prometheus, distributed tracing
- [Observability](../monitoring/observability-interview.md) — наблюдаемость систем
- [Kubernetes](../devops/kubernetes-interview.md) — оркестрация контейнеров
- [Caching Performance](caching-performance-interview.md) — стратегии кеширования
- [Database Performance](database-performance-interview.md) — оптимизация БД-запросов
- [Network Performance](network-performance-interview.md) — сетевая производительность
- [Performance Testing](performance-testing-interview.md) — нагрузочное тестирование
