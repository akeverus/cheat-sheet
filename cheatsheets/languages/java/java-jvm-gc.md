---
title: "JVM: архитектура и сборка мусора"
description: "Практическое руководство по JVM: области памяти, жизненный цикл объектов, сборщики мусора (Serial, Parallel, G1, ZGC, Shenandoah), настройка и диагностика."
tags:
  - java
  - jvm
  - gc
  - memory
  - performance
difficulty: "intermediate"
prerequisites: ["java-basics.md"]
next: ["java-memory-model.md", "java-concurrency-basics.md"]
updated: "2026-04-11"
---

# JVM: архитектура и сборка мусора

Практическое руководство по устройству JVM, управлению памятью и выбору/настройке сборщика мусора.

## Полезные ссылки

- [JVM Specification](https://docs.oracle.com/javase/specs/jvms/se21/html/)
- [HotSpot GC Tuning Guide](https://docs.oracle.com/en/java/javase/21/gctuning/)
- [JEP 439: Generational ZGC](https://openjdk.org/jeps/439)

### См. также

- [[java-memory-model|Java Memory Model]] — happens-before, volatile, synchronized
- [[java-concurrency-basics|Java Concurrency]] — потоки, пулы, синхронизация

## Содержание

- [Архитектура JVM](#архитектура-jvm)
- [Области памяти](#области-памяти)
- [Жизненный цикл объекта](#жизненный-цикл-объекта)
- [Сборщики мусора](#сборщики-мусора)
- [Выбор GC](#выбор-gc)
- [Ключевые JVM-флаги](#ключевые-jvm-флаги)
- [Диагностика и мониторинг](#диагностика-и-мониторинг)
- [Типичные проблемы и решения](#типичные-проблемы-и-решения)
- [Антипаттерны](#антипаттерны)

## Архитектура JVM

```text
┌─────────────────────────────────────────────┐
│                   JVM                       │
│  ┌──────────┐  ┌──────────┐  ┌───────────┐ │
│  │ClassLoader│  │ Execution│  │  Runtime   │ │
│  │ Subsystem │  │  Engine  │  │   Data    │ │
│  │           │  │          │  │   Areas   │ │
│  │ Bootstrap │  │ Interpr. │  │ Heap      │ │
│  │ Extension │  │ JIT (C1) │  │ Stack     │ │
│  │ App       │  │ JIT (C2) │  │ Metaspace │ │
│  └──────────┘  │ GC       │  │ PC Reg    │ │
│                └──────────┘  │ Native St. │ │
│                              └───────────┘ │
│  ┌─────────────────────────────────────┐   │
│  │    Native Method Interface (JNI)    │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
```

**ClassLoader** загружает `.class` файлы (Bootstrap → Extension → Application).

**Execution Engine**:
- **Interpreter** — построчное выполнение байткода (медленно, но быстрый старт)
- **C1 JIT** — быстрая компиляция для «тёплых» методов
- **C2 JIT** — агрессивная оптимизация для «горячих» методов (inlining, escape analysis, loop unrolling)

## Области памяти

| Область | Содержимое | Общая/per-thread | Настройка |
|---------|-----------|-------------------|-----------|
| **Heap** | Объекты и массивы | Общая | `-Xms`, `-Xmx` |
| **Metaspace** | Метаданные классов, constant pool | Общая | `-XX:MaxMetaspaceSize` |
| **Thread Stack** | Фреймы вызовов, локальные переменные | Per-thread | `-Xss` |
| **PC Register** | Адрес текущей инструкции байткода | Per-thread | — |
| **Native Stack** | Вызовы native-методов (JNI) | Per-thread | — |
| **Code Cache** | JIT-скомпилированный машинный код | Общая | `-XX:ReservedCodeCacheSize` |
| **Direct Memory** | NIO DirectByteBuffer (off-heap) | Общая | `-XX:MaxDirectMemorySize` |

### Структура Heap (поколенческая модель)

```text
Heap
├── Young Generation
│   ├── Eden          — новые объекты создаются здесь
│   ├── Survivor S0   — объекты, пережившие 1+ Minor GC
│   └── Survivor S1   — копия при очередном Minor GC
└── Old Generation    — долгоживущие объекты (age ≥ threshold)
```

- **Minor GC** — собирает Young Gen (быстро, STW-пауза обычно < 10ms)
- **Major/Full GC** — собирает Old Gen + Young Gen (медленно, STW может быть секунды)

## Жизненный цикл объекта

1. Объект создаётся в **Eden**
2. При Minor GC живые объекты копируются в **Survivor** (S0 ↔ S1)
3. При каждом GC `age++`; при `age ≥ MaxTenuringThreshold` объект переходит в **Old Gen**
4. Когда Old Gen заполнен — **Major GC**
5. Если памяти не хватает даже после Full GC — `OutOfMemoryError`

**Определение «живой» объект:** достижим из GC Roots (стеки потоков, статические поля, JNI-ссылки).

## Сборщики мусора

### Serial GC (`-XX:+UseSerialGC`)

- Однопоточный, STW для всех фаз
- Минимальный overhead, подходит для маленьких heap (< 100MB) и single-core
- Не для production-сервисов

### Parallel GC (`-XX:+UseParallelGC`)

- Многопоточный STW (throughput-oriented)
- По умолчанию в Java 8
- Хорош для batch-задач, где латентность не критична
- Паузы могут достигать секунд на больших heap

### G1 GC (`-XX:+UseG1GC`)

- По умолчанию с Java 9+
- Heap делится на **регионы** (~2048 регионов)
- Concurrent marking + incremental compaction
- Целевая пауза: `-XX:MaxGCPauseMillis=200` (по умолчанию)
- Подходит для большинства серверных приложений (heap 4-64 GB)

```text
┌──────────────────────────────────────┐
│  E │ E │ S │ O │ O │ H │ E │ O │ F  │  G1 Region Layout
│eden│eden│sur│old│old│hum│eden│old│free│
└──────────────────────────────────────┘
E = Eden, S = Survivor, O = Old, H = Humongous, F = Free
```

**Humongous regions** — для объектов > 50% размера региона. Собираются отдельно.

### ZGC (`-XX:+UseZGC`)

- Паузы **< 1 ms** (не зависят от размера heap)
- Concurrent: marking, relocation, remapping — всё параллельно с приложением
- Colored pointers + load barriers
- С Java 21: Generational ZGC (`-XX:+UseZGC -XX:+ZGenerational`) — по умолчанию с Java 23
- Подходит для heap 8 GB — 16 TB

### Shenandoah GC (`-XX:+UseShenandoahGC`)

- Аналогичные ZGC цели: sub-millisecond паузы
- Brooks pointers (forwarding pointer в каждом объекте)
- Доступен в OpenJDK (не в Oracle JDK)
- Concurrent compaction

## Выбор GC

| Сценарий | Рекомендация | Почему |
|----------|-------------|--------|
| Типичный микросервис, heap 2-8 GB | **G1** | Баланс throughput/latency, минимальная настройка |
| Критичная латентность (p99 < 5ms) | **ZGC** | Sub-millisecond паузы |
| Большой heap (> 32 GB) | **ZGC** или **Shenandoah** | Паузы G1 растут с heap |
| Batch-обработка, throughput важнее | **Parallel** | Максимальная пропускная способность |
| Embedded / маленький heap | **Serial** | Минимальный overhead |

**Практический совет:** начинайте с G1 (по умолчанию). Переходите на ZGC только если G1 не выдерживает SLO по латентности.

## Ключевые JVM-флаги

### Память

```bash
# Размер heap
-Xms4g -Xmx4g              # Min = Max → предсказуемое поведение

# Metaspace
-XX:MaxMetaspaceSize=256m   # Ограничить (по умолчанию — без лимита)

# Стек потока
-Xss512k                    # Размер стека (по умолчанию ~1MB)

# Direct memory
-XX:MaxDirectMemorySize=512m
```

### GC

```bash
# G1
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:G1HeapRegionSize=8m     # Размер региона (1-32 MB, степень двойки)

# ZGC
-XX:+UseZGC
-XX:+ZGenerational           # Generational ZGC (Java 21+)

# GC Logging (Java 9+)
-Xlog:gc*:file=gc.log:time,uptime,level,tags:filecount=5,filesize=10M
```

### Диагностика

```bash
# Heap dump при OOM
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/dumps/

# Остановка при OOM (для перезапуска контейнера)
-XX:+ExitOnOutOfMemoryError

# NMT (Native Memory Tracking)
-XX:NativeMemoryTracking=summary
```

## Диагностика и мониторинг

### Команды

```bash
# Heap summary
jmap -heap <pid>

# Histogram живых объектов
jmap -histo:live <pid> | head -20

# Heap dump
jmap -dump:live,format=b,file=heap.hprof <pid>

# Thread dump
jstack <pid> > threads.txt

# Текущие флаги JVM
jinfo -flags <pid>

# GC статистика (JMX)
jstat -gcutil <pid> 1000 10

# Native Memory Tracking
jcmd <pid> VM.native_memory summary
```

### Метрики для мониторинга

| Метрика | Что смотреть | Алерт |
|---------|-------------|-------|
| `jvm.memory.used` / `jvm.memory.max` | Утилизация heap | > 80% постоянно |
| `jvm.gc.pause` | Длительность STW-пауз | p99 > SLO |
| `jvm.gc.pause.count` | Частота GC | Рост тренда |
| `jvm.memory.pool.used` (Old Gen) | Утечки памяти | Монотонный рост |
| `jvm.threads.live` | Количество потоков | Рост без стабилизации |
| `process.cpu.usage` | CPU на GC vs приложение | GC > 5% CPU |

## Типичные проблемы и решения

| Проблема | Симптомы | Диагностика | Решение |
|----------|---------|-------------|---------|
| Memory Leak | Old Gen монотонно растёт, Full GC не освобождает | Heap dump → MAT/VisualVM → dominator tree | Найти удерживающие ссылки (кеши, listeners, static collections) |
| GC Thrashing | CPU 100% на GC, приложение не отвечает | `jstat -gcutil`, GC log | Увеличить heap, проверить allocation rate, оптимизировать объекты |
| Long GC Pauses | Latency spikes | GC log, `-Xlog:gc*` | Перейти на ZGC, уменьшить Old Gen pressure |
| Metaspace OOM | `OutOfMemoryError: Metaspace` | `jcmd VM.native_memory` | Утечка classloader-ов (hot deploy, groovy scripts), увеличить лимит |
| Direct Memory OOM | `OutOfMemoryError: Direct buffer memory` | NMT | Утечка NIO-буферов, явный `Cleaner`, увеличить `-XX:MaxDirectMemorySize` |
| StackOverflowError | Рекурсия или глубокие call chains | Thread dump | Исправить рекурсию или увеличить `-Xss` |

## Антипаттерны

- **`System.gc()`** — не вызывайте явно. Используйте `-XX:+DisableExplicitGc` для защиты.
- **`finalize()`** — deprecated с Java 9. Используйте `Cleaner` или try-with-resources.
- **Object pooling для мелких объектов** — GC справляется лучше. Пулы оправданы только для тяжёлых ресурсов (DB connections, threads).
- **-Xms ≠ -Xmx в контейнерах** — heap будет resize'иться, вызывая лишние GC. Устанавливайте одинаковые значения.
- **Не логировать GC в production** — GC-логи почти бесплатны и критичны для диагностики. Всегда включайте `-Xlog:gc*`.
- **Игнорировать container memory limits** — JVM видит cgroup-лимит с Java 10+. Используйте `-XX:MaxRAMPercentage=75` вместо фиксированного `-Xmx` в контейнерах.
