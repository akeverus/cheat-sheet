---
title: "Вопросы на собеседовании: Apache Flink"
description: "Apache Flink: streaming-first архитектура, event time vs processing time, watermarks, state, checkpoints, exactly-once, savepoints, KeyedStream, windows, CEP, Flink SQL, отличие от Spark Streaming"
tags:
  - interview
  - data-engineering
  - apache-flink-interview
aliases:
  - "Apache Flink interview"
  - "Flink interview"
  - "Flink собеседование"
  - "Flink streaming interview"
  - "Flink vs Spark"
difficulty: "intermediate"
updated: "2026-04-18"
---
# Вопросы на собеседовании: `Apache Flink`

`Apache Flink` — distributed stream processing framework с **true streaming** архитектурой (не micro-batch как Spark). Создан в Berlin (TU Berlin), Apache top-level с 2014. Главные применения: **low-latency streaming** (миллисекунды), **stateful processing**, exactly-once гарантии, **CEP (complex event processing)**.

Дата последнего обновления: 2026-04-18

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Apache Flink Documentation](https://nightlies.apache.org/flink/flink-docs-stable/)
- [Flink Streaming Guide](https://nightlies.apache.org/flink/flink-docs-stable/docs/dev/datastream/overview/)
- [Flink State and Fault Tolerance](https://nightlies.apache.org/flink/flink-docs-stable/docs/concepts/stateful-stream-processing/)
- [Flink vs Spark — Baeldung](https://www.baeldung.com/apache-spark-vs-apache-flink)
- [Stream Processing with Apache Flink (book)](https://www.oreilly.com/library/view/stream-processing-with/9781491974285/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Apache Flink?](#q1--что-такое-apache-flink)
- [Q2. (!) Чем Flink отличается от Spark Streaming?](#q2--чем-flink-отличается-от-spark-streaming)
- [Q3. (!) Архитектура Flink — JobManager, TaskManager?](#q3--архитектура-flink--jobmanager-taskmanager)
- [Q4. Deployment режимы (Standalone, YARN, K8s)?](#q4-deployment-режимы-standalone-yarn-k8s)

**Streaming основы**
- [Q5. (!) DataStream API — основные операции?](#q5--datastream-api--основные-операции)
- [Q6. (!) KeyedStream — что это?](#q6--keyedstream--что-это)
- [Q7. Sources и Sinks?](#q7-sources-и-sinks)

**Time semantics**
- [Q8. (!) Event time vs Processing time vs Ingestion time?](#q8--event-time-vs-processing-time-vs-ingestion-time)
- [Q9. (!) Watermarks — как работают?](#q9--watermarks--как-работают)
- [Q10. (!) Late data — стратегии?](#q10--late-data--стратегии)

**Windows**
- [Q11. (!) Tumbling windows?](#q11--tumbling-windows)
- [Q12. (!) Sliding windows?](#q12--sliding-windows)
- [Q13. Session windows?](#q13-session-windows)
- [Q14. Global windows?](#q14-global-windows)

**State**
- [Q15. (!) Что такое state в Flink?](#q15--что-такое-state-в-flink)
- [Q16. (!) Keyed state vs Operator state?](#q16--keyed-state-vs-operator-state)
- [Q17. State backends — какие?](#q17-state-backends--какие)
- [Q18. (!) RocksDB state backend — особенности?](#q18--rocksdb-state-backend--особенности)

**Fault tolerance**
- [Q19. (!) Checkpointing — как работает?](#q19--checkpointing--как-работает)
- [Q20. (!) Exactly-once semantics?](#q20--exactly-once-semantics)
- [Q21. (!) Savepoints — отличие от checkpoints?](#q21--savepoints--отличие-от-checkpoints)
- [Q22. Barriers и алгоритм Chandy-Lamport?](#q22-barriers-и-алгоритм-chandy-lamport)

**APIs**
- [Q23. (!) DataStream vs Table vs SQL API?](#q23--datastream-vs-table-vs-sql-api)
- [Q24. ProcessFunction — что это?](#q24-processfunction--что-это)
- [Q25. (!) CEP (Complex Event Processing)?](#q25--cep-complex-event-processing)

**Производительность**
- [Q26. (!) Backpressure в Flink?](#q26--backpressure-в-flink)
- [Q27. Parallelism — как настраивать?](#q27-parallelism--как-настраивать)
- [Q28. Network buffering и chains?](#q28-network-buffering-и-chains)

**Применения**
- [Q29. (!) Когда выбирать Flink вместо Spark Streaming?](#q29--когда-выбирать-flink-вместо-spark-streaming)
- [Q30. (!) Где Flink в production?](#q30--где-flink-в-production)
- [Q31. Какие минусы Flink?](#q31-какие-минусы-flink)

## Q1. (!) Что такое Apache Flink?

`Apache Flink` — distributed stream processing engine. Создан в **TU Berlin** (2010), Apache top-level с 2014. Написан на **Scala/Java**, работает на JVM.

**Ключевое отличие от Spark:** **true streaming** — обрабатывает каждое событие **мгновенно**, не группирует в micro-batch.

**Применения:**
- Real-time analytics (clickstream, IoT, financial trading)
- Fraud detection
- Real-time recommendations
- ETL streaming (Kafka → DB / Data Lake)
- Complex Event Processing (CEP)

## Q2. (!) Чем Flink отличается от Spark Streaming?

| Критерий | Flink | Spark Structured Streaming |
|----------|-------|---------------------------|
| Modell | True streaming | Micro-batch (continuous mode experimental) |
| Latency | < 100ms (часто < 10ms) | 100ms - сек |
| Throughput | Высокий | Высокий |
| Time semantics | Event time first-class | Event time через watermark |
| State management | Богатый (keyed/operator) | Stateful operations есть, но проще |
| Exactly-once | Строго (Chandy-Lamport) | Строго (через WAL) |
| Batch | Да (батч = специальный streaming) | Да (batch + streaming) |
| Window types | Tumbling, sliding, session, global | Tumbling, sliding, session |
| Iterative algorithms | Native (для ML/graph) | Через DataFrame loop |

**Spark** — лучше для batch + streaming в одном стеке. **Flink** — лучше для **pure low-latency streaming**.

## Q3. (!) Архитектура Flink — JobManager, TaskManager?

```mermaid
graph TD
    Client --> JM[JobManager<br/>координация]
    JM --> TM1[TaskManager 1<br/>slots: 4]
    JM --> TM2[TaskManager 2<br/>slots: 4]
    JM --> TM3[TaskManager 3<br/>slots: 4]
    TM1 --> T1[Task]
    TM1 --> T2[Task]
    TM2 --> T3[Task]
```

**JobManager:**
- Принимает jobs от клиентов
- Координирует execution
- Управляет checkpointing
- Single-point-of-failure (можно настроить HA)

**TaskManager:**
- Worker process
- Имеет **task slots** (по умолчанию = #cores)
- Каждый slot — одна параллельная subtask

**Slot:** единица параллелизма (фиксированная память/CPU).

## Q4. Deployment режимы (Standalone, YARN, K8s)?

| Режим | Описание |
|-------|----------|
| **Standalone** | Built-in cluster manager |
| **YARN** | Hadoop ecosystem |
| **Kubernetes** | Cloud-native, рост популярности |
| **Mesos** | Apache Mesos (deprecated) |
| **Local** | Один процесс для тестов |

С Flink 1.12+ — first-class K8s integration через **Flink Kubernetes Operator**.

```bash
flink run -m yarn-cluster -p 4 -ys 2 myapp.jar
```

## Q5. (!) DataStream API — основные операции?

```scala
val env = StreamExecutionEnvironment.getExecutionEnvironment

val stream: DataStream[String] = env.socketTextStream("localhost", 9999)

val counts = stream
  .flatMap(_.split(" "))
  .map(word => (word, 1))
  .keyBy(_._1)
  .sum(1)

counts.print()

env.execute("Word Count")
```

**Operations:**
- `map`, `filter`, `flatMap` — element-wise
- `keyBy` — partitioning по key
- `reduce`, `sum`, `min`, `max` — aggregations
- `window` — окна
- `join`, `coGroup` — соединения
- `connect`, `union` — комбинирование streams

## Q6. (!) KeyedStream — что это?

`KeyedStream` — `DataStream`, разделённый по **key**. Все события с одним key обрабатываются **последовательно** одним subtask.

```scala
val keyed: KeyedStream[Event, String] = stream.keyBy(_.userId)

// Stateful operation per key
keyed.process(new MyKeyedProcessFunction())
```

**Зачем:**
- **Stateful operations per key** — counter per user, aggregations
- **Гарантия порядка** в пределах key
- **Параллелизация** — разные keys на разных subtasks

`KeyedStream` — основа для **windows**, **state**, **timers**.

## Q7. Sources и Sinks?

**Sources** — откуда данные:
- Apache Kafka (`flink-connector-kafka`)
- Files (FileSource)
- Sockets (для тестов)
- Custom

**Sinks** — куда:
- Kafka
- JDBC databases
- Elasticsearch
- File systems (S3, HDFS, Iceberg, Delta Lake)
- Custom

```scala
val stream = env.fromSource(
  KafkaSource.builder()
    .setBootstrapServers("kafka:9092")
    .setTopics("events")
    .setGroupId("flink-consumer")
    .setStartingOffsets(OffsetsInitializer.earliest())
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build(),
  WatermarkStrategy.noWatermarks(),
  "Kafka Source"
)

stream.sinkTo(
  KafkaSink.builder()
    .setBootstrapServers("kafka:9092")
    .setRecordSerializer(...)
    .build()
)
```

## Q8. (!) Event time vs Processing time vs Ingestion time?

| Time | Что значит |
|------|------------|
| **Event time** | Когда событие **произошло** в реальности (timestamp в данных) |
| **Processing time** | Когда событие **обрабатывается** в Flink |
| **Ingestion time** | Когда событие **поступило** в Flink |

**Пример:** clickstream
```
event_time = 14:00 (клик)
ingestion_time = 14:05 (Kafka получил)
processing_time = 14:07 (Flink обрабатывает)
```

**Event time** — единственный **корректный** для аналитики (детерминирован, повторяемый).
**Processing time** — для low-latency без точности.

```scala
WatermarkStrategy.forBoundedOutOfOrderness[Event](Duration.ofSeconds(20))
  .withTimestampAssigner((event, _) => event.timestamp)
```

## Q9. (!) Watermarks — как работают?

**Watermark** — заявление "до этого event_time все события **уже** обработаны".

```
events:    e1(t=10) e2(t=12) e3(t=11) e4(t=15) e5(t=13)
                                                      ↓
                                             watermark = 13 (событий с t<=13 больше не будет)
```

Используется для:
- **Closing windows** — когда watermark проходит конец окна, окно закрывается
- **Determining late data** — события с timestamp < watermark считаются late
- **State TTL** — освобождать state старше watermark

```scala
WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(5))
// allows up to 5s out-of-order events
```

## Q10. (!) Late data — стратегии?

```scala
val late = new OutputTag[Event]("late-events")

stream
  .keyBy(_.userId)
  .window(TumblingEventTimeWindows.of(Time.minutes(5)))
  .allowedLateness(Time.minutes(1))     // ждать ещё 1 мин после закрытия окна
  .sideOutputLateData(late)              // совсем late → side output
  .process(new MyWindowFunction())

stream.getSideOutput(late) // обрабатываем отдельно
```

**Стратегии:**
- **Drop** (default) — поздние события игнорируются
- **`allowedLateness`** — окно остаётся открытым ещё N времени, обновляет результат
- **`sideOutputLateData`** — поздние события идут в отдельный stream

Trade-off: больше lateness — больше state.

## Q11. (!) Tumbling windows?

**Tumbling window** — окна фиксированной длины, **не перекрываются**:

```
| 0-5 | 5-10 | 10-15 | 15-20 |
```

```scala
keyedStream
  .window(TumblingEventTimeWindows.of(Time.minutes(5)))
  .sum(1)
```

Каждое событие попадает **ровно в одно** окно.

Применение: статистика за минуту/час/день.

## Q12. (!) Sliding windows?

**Sliding window** — окна фиксированной длины, перекрываются с заданным шагом:

```
window 1: |0-5|
window 2:   |2-7|
window 3:     |4-9|
window 4:       |6-11|
```

```scala
keyedStream
  .window(SlidingEventTimeWindows.of(
    Time.minutes(5),     // длина окна
    Time.minutes(1)       // slide (как часто новое окно)
  ))
  .sum(1)
```

Каждое событие может попасть в **несколько** окон. Полезно для **moving averages**.

## Q13. Session windows?

**Session window** — окно объединяет события одной сессии (пользователь активен), разделённые **gap'ом неактивности**.

```
events: e1 e2 e3      e4 e5     e6
        |--session--|  |session| |session|
              gap     gap
```

```scala
keyedStream
  .window(EventTimeSessionWindows.withGap(Time.minutes(15)))
  .reduce(...)
```

Применение: пользовательские сессии в analytics, RP detection.

## Q14. Global windows?

**Global window** — все события в **одном** окне. Нужен **custom trigger** для emit'а.

```scala
keyedStream
  .window(GlobalWindows.create())
  .trigger(CountTrigger.of(100)) // emit когда 100 событий
  .reduce(...)
```

Используется редко — для специфичных случаев (count-based, custom triggers).

## Q15. (!) Что такое state в Flink?

**State** — данные, которые operator хранит между событиями. Flink — **stateful streaming engine**.

Примеры state:
- Counter per user (key)
- Window aggregations
- Last seen value
- Machine learning model

State **persistent** через checkpoints — переживает рестарты.

## Q16. (!) Keyed state vs Operator state?

| Тип | Привязан к | Использование |
|-----|------------|---------------|
| **Keyed state** | Key (KeyedStream) | Aggregations per user, sessions |
| **Operator state** | Параллельной subtask | Connector state (Kafka offsets) |

**Keyed state types:**
- `ValueState[T]` — одно значение
- `ListState[T]` — список
- `MapState[K, V]` — мап
- `ReducingState[T]` — reduce функция
- `AggregatingState[I, O]` — aggregate функция

```scala
class CounterFunction extends KeyedProcessFunction[String, Event, Long] {
  private var count: ValueState[Long] = _

  override def open(parameters: Configuration): Unit = {
    count = getRuntimeContext.getState(
      new ValueStateDescriptor("count", classOf[Long])
    )
  }

  override def processElement(value: Event, ctx: Context, out: Collector[Long]): Unit = {
    val current = Option(count.value()).getOrElse(0L)
    count.update(current + 1)
    out.collect(current + 1)
  }
}
```

## Q17. State backends — какие?

| Backend | Хранение state | Hot path | Размер |
|---------|----------------|----------|--------|
| **HashMapStateBackend** | JVM heap | Очень быстро | Ограничен heap |
| **EmbeddedRocksDBStateBackend** | RocksDB на диске | Медленнее (ser/deser) | Огромный (TBs) |

```scala
env.setStateBackend(new EmbeddedRocksDBStateBackend())
env.getCheckpointConfig.setCheckpointStorage("s3://bucket/checkpoints")
```

**HashMap:** для маленького state (< 1 GB), low latency.
**RocksDB:** для огромного state (терабайты), incremental checkpoints.

## Q18. (!) RocksDB state backend — особенности?

**RocksDB** — embedded LSM-tree key-value store от Facebook. В Flink — для огромного state.

**Преимущества:**
- State не ограничен RAM (на диске, до TBs)
- **Incremental checkpoints** — сохраняет только изменения
- Compression, predicate pushdown

**Недостатки:**
- Каждое чтение/запись — ser/deser (медленнее HashMap)
- Tuning RocksDB сложен (BlockCache, compaction levels)

В **production** для streaming с большим state — почти всегда RocksDB.

## Q19. (!) Checkpointing — как работает?

**Checkpoint** — snapshot state всех operators в определённый момент.

```scala
env.enableCheckpointing(60000) // каждые 60 секунд
env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
env.getCheckpointConfig.setCheckpointTimeout(600000)
```

**Алгоритм (Chandy-Lamport):**
1. JobManager инжектирует **barrier** в input streams
2. Barriers идут через operators
3. Когда operator получил barriers со всех inputs — снимает snapshot своего state
4. Snapshot пишется в **state backend** (HDFS, S3)
5. Когда все operators закончили — checkpoint complete

При **сбое** — рестарт с последнего checkpoint.

## Q20. (!) Exactly-once semantics?

Flink гарантирует **exactly-once** — каждое событие обрабатывается ровно один раз (с точки зрения state).

**Условия:**
- **Source** должен поддерживать replay (Kafka, Kinesis)
- **Sink** должен поддерживать **transactional writes** или **idempotent writes**

**End-to-end exactly-once:**
- Kafka → Flink → Kafka (через Kafka transactions)
- Flink → JDBC (через 2PC sink) — медленно, но строго

**At-least-once** — проще, быстрее, но возможны дубли.

## Q21. (!) Savepoints — отличие от checkpoints?

| Критерий | Checkpoint | Savepoint |
|----------|------------|-----------|
| Цель | Fault tolerance | Operations (upgrade, A/B) |
| Создание | Автоматически | Вручную (`flink savepoint`) |
| Удаление | Автоматически (по retention) | Вручную |
| Format | Optimized | Standard (можно восстанавливать после рефакторинга) |

```bash
flink savepoint <job-id> s3://bucket/savepoints/
flink run -s s3://bucket/savepoints/savepoint-... newapp.jar
```

**Savepoints** — для:
- **Версионных upgrades** Flink приложения
- **Migrations** state schema
- A/B тестирования

## Q22. Barriers и алгоритм Chandy-Lamport?

**Barrier** — специальный маркер, инжектируемый в stream для checkpoint coordination.

```mermaid
graph LR
    Source -->|e1, e2, BARRIER, e3, e4| Op1[Operator 1]
    Op1 -->|"e1, e2, BARRIER"| Op2[Operator 2]
    Op2 --> Sink
```

Когда operator получил barrier со ВСЕХ input streams → пишет snapshot. Это и есть **Chandy-Lamport algorithm** для distributed snapshots (1985).

В Flink barriers **выравниваются** (alignment) — operator ждёт barriers со всех inputs. Это даёт **exactly-once**, но может вызвать backpressure. Альтернатива — **unaligned checkpoints** (Flink 1.11+).

## Q23. (!) DataStream vs Table vs SQL API?

| API | Стиль | Audience |
|-----|-------|----------|
| **DataStream** | Imperative, Scala/Java | Developers |
| **Table** | Declarative, type-safe | Hybrid |
| **SQL** | Standard SQL | Analysts, BI |

```scala
// DataStream
stream.keyBy(_.userId).window(...).reduce(...)

// Table API
table.groupBy($"userId").select($"userId", $"amount".sum())

// SQL
tableEnv.sqlQuery("SELECT userId, SUM(amount) FROM events GROUP BY userId")
```

Все три **одинаковая performance** — компилируются в один Flink runtime.

С Flink 1.13+ — **Table API + SQL** активно развиваются для analytical use cases.

## Q24. ProcessFunction — что это?

`ProcessFunction` — **низкоуровневое** API для custom event processing с **state и timers**.

```scala
class MyFunction extends KeyedProcessFunction[String, Event, Result] {
  private var state: ValueState[Long] = _

  override def processElement(value: Event, ctx: Context, out: Collector[Result]): Unit = {
    // обработка, state, регистрация timer
    ctx.timerService().registerEventTimeTimer(value.timestamp + 60_000)
  }

  override def onTimer(timestamp: Long, ctx: OnTimerContext, out: Collector[Result]): Unit = {
    // выполнится через 60 сек event time
  }
}
```

Применение: custom logic, которую не выразить через стандартные операторы (CEP с custom правилами, fraud detection).

## Q25. (!) CEP (Complex Event Processing)?

`Flink CEP` — библиотека для поиска **сложных паттернов** в стриме.

```scala
import org.apache.flink.cep.scala.pattern.Pattern

val pattern = Pattern.begin[Event]("start")
  .where(_.amount > 100)
  .next("middle")
  .where(_.userId == "X")
  .followedBy("end")
  .where(_.action == "transfer")
  .within(Time.minutes(10))

CEP.pattern(stream, pattern).select(matches => alert(matches))
```

Применения:
- **Fraud detection** — последовательность подозрительных действий
- **Trading signals** — паттерны рыночных событий
- **System monitoring** — alerts по сложным условиям
- **Click stream analysis** — пользовательские sequences

CEP сложнее, чем simple aggregations, но даёт мощные возможности.

## Q26. (!) Backpressure в Flink?

**Backpressure** — медленный downstream operator замедляет upstream (через network buffers).

```mermaid
graph LR
    Source -->|fast| Op1[Op1: fast]
    Op1 -->|fast| Op2[Op2: SLOW]
    Op2 --> Sink
```

Когда `Op2` не успевает — buffers перед ним заполняются → `Op1` не может писать → замедляется.

Flink **не теряет данные** при backpressure — просто замедляется в целом.

**Detection:** Flink Web UI → Backpressure tab. Высокий — значит, узкое место.

**Решение:**
- Увеличить parallelism slow operator
- Оптимизировать его логику
- Увеличить ресурсы (CPU, memory)

## Q27. Parallelism — как настраивать?

```scala
env.setParallelism(8) // global default

stream
  .map(...)
  .setParallelism(4) // override для конкретного operator

env.setMaxParallelism(128) // verticale scaling
```

**Best practices:**
- Стартуй с `parallelism = #cores in cluster`
- Источник — обычно меньший parallelism (= partitions Kafka topic)
- Bottleneck operators — больше parallelism

`maxParallelism` — для будущего масштабирования, нельзя поменять после первого запуска без savepoint migration.

## Q28. Network buffering и chains?

**Operator chain** — Flink объединяет несколько operators в один task, если возможно (no shuffle между ними):

```
source → map → filter → keyBy → sum → sink
\__________ chain __________/  shuffle  \chain/
```

В chain — операции в одном thread, без сериализации. **Огромный прирост perf**.

Принудительно отключить:
```scala
stream.map(...).disableChaining()
```

**Network buffers** — буферы для передачи между operators. Настраиваются через `taskmanager.network.memory.*`.

## Q29. (!) Когда выбирать Flink вместо Spark Streaming?

**Выбирай Flink когда:**
- **Latency < 100ms** критична
- **Stateful processing** с big state
- **Exactly-once end-to-end** (Kafka → Flink → Kafka)
- **CEP** — сложные event patterns
- **True streaming** (не micro-batch)
- Уже есть team с Java/Scala expertise

**Выбирай Spark Streaming когда:**
- Один стек для batch + streaming
- Latency > 1 sec приемлема
- Уже используется Spark в компании
- Команда знакома со Spark

## Q30. (!) Где Flink в production?

**Известные пользователи:**
- **Alibaba** — Singles' Day, real-time analytics (биллион events/сек)
- **Netflix** — recommendation engine, fraud detection
- **Uber** — pricing, ETA estimation
- **Stripe** — fraud detection
- **Lyft, Pinterest, Twitter, Yelp**
- **ING Bank, Comcast**

Особенно сильна позиция в **финтехе** и крупных internet-компаниях.

## Q31. Какие минусы Flink?

1. **Steeper learning curve** — больше концепций (state, watermarks, checkpoints)
2. **Меньше community** по сравнению со Spark
3. **Меньше готовых connectors** (хотя основные есть)
4. **Сложность операционная** — checkpointing, savepoints, state migration
5. **SQL API менее зрелый** чем Spark SQL
6. **Меньше ML/AI** возможностей
7. **Сложнее в Python** (PyFlink) — не такой mature как PySpark

В **2024** Flink — лидер для **pure streaming**, но Spark всё ещё доминирует overall в data engineering.

---

## See also

- [Apache Spark](apache-spark-interview.md) — главный конкурент
- [Kafka Streams](kafka-streams-interview.md) — другая JVM streaming opция
- [Stream Processing](stream-processing-interview.md) — концепции
- [Apache Kafka](../messaging/kafka-interview.md) — главный source/sink Flink
- [Apache Airflow](apache-airflow-interview.md) — orchestration Flink jobs
- [Event-driven паттерны](../architecture/event-driven-patterns-interview.md) — концепции CEP
- [Scala](../programming-languages/scala/scala-interview.md) — родной API Flink
- [Микросервисы](../architecture/microservices-interview.md) — vs реактивный streaming
- [Распределённые системы](../architecture/distributed-systems-interview.md) — Chandy-Lamport
- [Performance Testing](../performance/performance-testing-interview.md) — Flink benchmarking
- [Memory Management](../performance/memory-management-interview.md) — RocksDB, off-heap
- [JVM](../jvm/jvm-interview.md) — Flink на JVM
