---
title: "Вопросы на собеседовании: Kafka Streams"
description: "Kafka Streams: stream processing library поверх Kafka, KStream/KTable/GlobalKTable, joins, windows, state stores (RocksDB), exactly-once, interactive queries, ksqlDB, отличие от Flink"
tags:
  - interview
  - data-engineering
  - kafka-streams-interview
aliases:
  - "Kafka Streams interview"
  - "Kafka Streams собеседование"
  - "KStream KTable interview"
  - "ksqlDB interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Kafka Streams`

`Kafka Streams` — **library** (не отдельный кластер!) для stream processing поверх Apache Kafka. Запускается как обычное Java/Scala приложение, использует Kafka topics как input/output и для **state**. Особенно удобен для **event-driven микросервисов** — без отдельного Spark/Flink кластера.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)
- [Kafka Streams Tutorials — Confluent](https://developer.confluent.io/learn-kafka/kafka-streams/get-started/)
- [Kafka Streams Examples GitHub](https://github.com/confluentinc/kafka-streams-examples)
- [Kafka Streams — Baeldung](https://www.baeldung.com/java-kafka-streams)
- [ksqlDB](https://ksqldb.io/)
- [Designing Event-Driven Systems (book)](https://www.confluent.io/designing-event-driven-systems/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое Kafka Streams?](#q1--что-такое-kafka-streams)
- [Q2. (!) Чем Kafka Streams отличается от Spark/Flink?](#q2--чем-kafka-streams-отличается-от-sparkflink)
- [Q3. (!) Application как отдельный процесс — почему?](#q3--application-как-отдельный-процесс--почему)

**Топология**
- [Q4. (!) Что такое topology в Kafka Streams?](#q4--что-такое-topology-в-kafka-streams)
- [Q5. (!) Streams DSL vs Processor API?](#q5--streams-dsl-vs-processor-api)

**KStream, KTable, GlobalKTable**
- [Q6. (!) Чем KStream отличается от KTable?](#q6--чем-kstream-отличается-от-ktable)
- [Q7. (!) GlobalKTable — когда использовать?](#q7--globalktable--когда-использовать)
- [Q8. (!) Конвертации KStream ↔ KTable?](#q8--конвертации-kstream--ktable)

**Операции**
- [Q9. (!) map, filter, flatMap, branch?](#q9--map-filter-flatmap-branch)
- [Q10. (!) groupByKey vs groupBy?](#q10--groupbykey-vs-groupby)
- [Q11. (!) Aggregate, Reduce, Count?](#q11--aggregate-reduce-count)
- [Q12. (!) Joins — типы и семантика?](#q12--joins--типы-и-семантика)

**Windows**
- [Q13. (!) Tumbling, Hopping, Session windows?](#q13--tumbling-hopping-session-windows)
- [Q14. Sliding windows?](#q14-sliding-windows)
- [Q15. (!) Grace period для late events?](#q15--grace-period-для-late-events)

**State**
- [Q16. (!) Что такое state store?](#q16--что-такое-state-store)
- [Q17. RocksDB как state backend?](#q17-rocksdb-как-state-backend)
- [Q18. (!) Changelog topic — для чего?](#q18--changelog-topic--для-чего)
- [Q19. (!) Interactive Queries?](#q19--interactive-queries)

**Time semantics**
- [Q20. (!) Event time vs processing time?](#q20--event-time-vs-processing-time)
- [Q21. TimestampExtractor?](#q21-timestampextractor)

**Exactly-once и fault tolerance**
- [Q22. (!) Exactly-once семантика в Kafka Streams?](#q22--exactly-once-семантика-в-kafka-streams)
- [Q23. Standby replicas?](#q23-standby-replicas)
- [Q24. (!) Что происходит при сбое instance?](#q24--что-происходит-при-сбое-instance)

**ksqlDB**
- [Q25. (!) Что такое ksqlDB?](#q25--что-такое-ksqldb)

**Сравнения и применение**
- [Q26. (!) Kafka Streams vs Flink — когда что?](#q26--kafka-streams-vs-flink--когда-что)
- [Q27. (!) Где Kafka Streams в production?](#q27--где-kafka-streams-в-production)
- [Q28. Какие минусы Kafka Streams?](#q28-какие-минусы-kafka-streams)

## Q1. (!) Что такое Kafka Streams?

`Kafka Streams` — **client library** для построения stream processing приложений на JVM. Часть Apache Kafka (с 0.10).

**Ключевые особенности:**
- **Library, не framework** — обычное Java/Scala приложение, не нужен отдельный кластер
- Хранит **state в Kafka topics** (changelog) — fault tolerance из коробки
- Использует Kafka topics как input/output
- Поддерживает **exactly-once** (с Kafka 0.11+)

**Применения:** event-driven микросервисы, real-time aggregations, joins streams, fraud detection в финтехе.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Чем Kafka Streams отличается от Spark/Flink?

| Критерий | Kafka Streams | Spark/Flink |
|----------|---------------|-------------|
| Архитектура | Library | Framework + cluster |
| Развёртывание | Как обычный Java app | Spark cluster, Flink JobManager |
| Source/Sink | Только Kafka | Kafka, files, DBs, ... |
| State | Kafka topics + RocksDB | Custom backends |
| Scaling | Через consumer groups | Через cluster manager |
| Operational complexity | Низкая | Высокая |
| Отдельный кластер | Не нужен | Нужен |
| Fault tolerance | Через Kafka | Через checkpoints |

**Главная идея Kafka Streams:** "deploy в Kubernetes как обычный микросервис, не нужен Hadoop кластер".


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Application как отдельный процесс — почему?

В Kafka Streams **нет JobManager/Driver** — каждый instance твоего приложения = standalone JVM процесс.

```
[ Pod 1 (Streams app) ] ┐
[ Pod 2 (Streams app) ] ─→ Kafka cluster
[ Pod 3 (Streams app) ] ┘
```

**Координация — через Kafka:**
- Topics как input/output
- Partition assignment через **consumer groups**
- State persistence через **changelog topics**

**Преимущества:**
- Простой deployment — это просто Java app
- Хорошо ложится на K8s
- Нет single point of failure (для координации)
- Авто-failover через consumer rebalance


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. (!) Что такое topology в Kafka Streams?

`Topology` — DAG операций (sources, processors, sinks).

```java
StreamsBuilder builder = new StreamsBuilder();

KStream<String, String> input = builder.stream("input-topic");

KStream<String, String> processed = input
    .filter((key, value) -> value != null)
    .mapValues(String::toUpperCase);

processed.to("output-topic");

Topology topology = builder.build();
KafkaStreams streams = new KafkaStreams(topology, config);
streams.start();
```

Topology может быть **визуализирована** через `topology.describe()`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) Streams DSL vs Processor API?

**Streams DSL** — высокоуровневый, с операциями `map/filter/groupBy/join`:

```java
KStream<String, Order> orders = builder.stream("orders");
orders.groupByKey()
      .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
      .aggregate(
          () -> 0L,
          (key, order, total) -> total + order.amount,
          Materialized.as("totals-store")
      )
      .toStream()
      .to("totals");
```

**Processor API** — низкоуровневый, ручное управление state и timers:

```java
public class MyProcessor implements Processor<String, String, String, String> {
    private KeyValueStore<String, Long> store;

    @Override
    public void init(ProcessorContext<String, String> context) {
        store = context.getStateStore("my-store");
    }

    @Override
    public void process(Record<String, String> record) {
        // custom логика
    }
}
```

В большинстве задач — DSL. Processor API — для специальных случаев.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Чем KStream отличается от KTable?

**KStream** — поток **независимых событий** (insert-only). Каждая запись — фактическое событие.

**KTable** — **таблица** (последнее значение per key). Записи с одинаковым key **обновляют** запись.

```
KStream events:
  (alice, +10)  (bob, +20)  (alice, +30)
  → 3 события

KTable balances:
  (alice, +10) (bob, +20) (alice, +30)
  → текущее состояние: {alice: +30, bob: +20}
```

**KStream** — для immutable facts (orders, clicks).
**KTable** — для current state (user profiles, prices).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) GlobalKTable — когда использовать?

| Тип | Partitioning | Replication |
|-----|-------------|-------------|
| `KTable` | По partitions Kafka topic | Каждый instance держит свой shard |
| `GlobalKTable` | Не разделена | Каждый instance имеет **полную копию** |

`GlobalKTable` — для **lookup тables** (маленькие, нужны на каждом instance):

```java
GlobalKTable<String, User> users = builder.globalTable("users");

orders.join(users,
    (orderId, order) -> order.userId,  // KStream key extractor
    (order, user) -> enrich(order, user)
)
```

**Преимущество:** join без repartitioning (любой instance имеет данные).
**Недостаток:** не масштабируется (если table большая → OOM).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Конвертации KStream ↔ KTable?

```java
// KStream → KTable
KTable<String, Long> counts = events
    .groupByKey()
    .count();

// KTable → KStream
KStream<String, Long> changeLog = counts.toStream();
```

`KTable.toStream()` — даёт стрим **изменений** (changelog). Полезно для отправки изменений в downstream.

```java
// Из KStream получить latest по key
KTable<String, Order> latest = orders
    .groupByKey()
    .reduce((oldVal, newVal) -> newVal);
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. (!) map, filter, flatMap, branch?

```java
KStream<String, String> stream = builder.stream("input");

// map — изменяет ключ или значение
stream.map((k, v) -> KeyValue.pair(k.toLowerCase(), v.toUpperCase()));

// mapValues — только значение (не вызывает repartition)
stream.mapValues(v -> v.toUpperCase());

// filter
stream.filter((k, v) -> v.length() > 5);

// flatMap — один → много
stream.flatMap((k, v) -> Arrays.asList(KeyValue.pair(k, v + "1"), KeyValue.pair(k, v + "2")));

// branch (split в 2.8+) — разделить на несколько стримов
Map<String, KStream<String, String>> branches = stream.split()
    .branch((k, v) -> v.startsWith("A"), Branched.as("a"))
    .branch((k, v) -> v.startsWith("B"), Branched.as("b"))
    .defaultBranch(Branched.as("other"));
```

**Подвох:** `map` (с изменением key) триггерит **repartitioning**. `mapValues` — нет.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) groupByKey vs groupBy?

```java
// groupByKey — текущий key, без repartition
events.groupByKey()

// groupBy — новый key, ТРИГЕРИТ repartitioning
events.groupBy((k, v) -> v.userId)
```

`groupBy` создаёт **internal repartition topic**. Дороже, но позволяет группировать по любому полю.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) Aggregate, Reduce, Count?

```java
// count — number of records per key
KTable<String, Long> counts = events.groupByKey().count();

// reduce — same type
KTable<String, Long> sums = events
    .groupByKey()
    .reduce((v1, v2) -> v1 + v2);

// aggregate — different output type
KTable<String, Stats> stats = events
    .groupByKey()
    .aggregate(
        () -> new Stats(0, 0),                          // initializer
        (key, event, agg) -> agg.add(event.amount),     // aggregator
        Materialized.with(Serdes.String(), statsSerde)  // serdes
    );
```

`aggregate` — самый мощный, может строить произвольный состояние per key.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Joins — типы и семантика?

```java
// KStream-KStream join (windowed!)
KStream<String, OrderShipment> joined = orders.join(
    shipments,
    (order, shipment) -> new OrderShipment(order, shipment),
    JoinWindows.of(Duration.ofMinutes(5))
);

// KStream-KTable join (non-windowed, lookup)
KStream<String, Enriched> enriched = events.join(
    userTable,
    (event, user) -> enrich(event, user)
);

// KTable-KTable join
KTable<String, Combined> combined = table1.join(table2, (v1, v2) -> ...);

// Left/Outer joins
events.leftJoin(table, ...)
events.outerJoin(table, ...)
```

| Join | Windowed | Repartition |
|------|----------|-------------|
| KStream-KStream | Да (обязательно) | Если разные partitions |
| KStream-KTable | Нет | Стрим должен co-partition с таблицей |
| KStream-GlobalKTable | Нет | Не нужен |
| KTable-KTable | Нет | Co-partition |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. (!) Tumbling, Hopping, Session windows?

```java
// Tumbling — фиксированные, не перекрываются
TimeWindows.of(Duration.ofMinutes(5))

// Hopping — фиксированные, перекрываются (= Sliding в Spark/Flink)
TimeWindows.of(Duration.ofMinutes(5)).advanceBy(Duration.ofMinutes(1))

// Session — gap-based
SessionWindows.with(Duration.ofMinutes(15))

events.groupByKey()
      .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
      .count();
```

В Kafka Streams "**hopping window**" — то, что в Flink/Spark называют sliding.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Sliding windows?

С Kafka Streams 2.7+ — **SlidingWindows** (отдельная концепция):

```java
SlidingWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5))
```

Создаёт window для **каждой пары** записей в пределах timeDifference. Полезно для pattern matching.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. (!) Grace period для late events?

```java
TimeWindows.of(Duration.ofMinutes(5))
           .grace(Duration.ofMinutes(1))
```

После закрытия окна Kafka Streams **ждёт grace period** для late events. После — окно становится "closed", state удаляется.

С Kafka Streams 2.5+ — **обязательно** указывать grace (default = `Long.MAX_VALUE`, но это плохая практика — grow state forever).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. (!) Что такое state store?

State store — **локальное** key-value хранилище для stateful operations (aggregations, joins).

По умолчанию — **RocksDB on local disk** (быстрее JVM heap для большого state).

```java
// Имплицитно создаётся через aggregate/count/etc
events.groupByKey()
      .aggregate(..., Materialized.as("my-store"));

// Явное создание (Processor API)
StoreBuilder<KeyValueStore<String, Long>> storeBuilder = Stores.keyValueStoreBuilder(
    Stores.persistentKeyValueStore("my-store"),
    Serdes.String(),
    Serdes.Long()
);
builder.addStateStore(storeBuilder);
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. RocksDB как state backend?

`RocksDB` — embedded LSM-tree key-value store от Facebook. По умолчанию в Kafka Streams.

**Преимущества:**
- State не ограничен RAM (диск)
- Хорошая производительность (LSM-tree оптимизирован для write-heavy)
- Встроенная компрессия

**Tuning:** через `RocksDBConfigSetter`:

```java
public class CustomRocksDBConfig implements RocksDBConfigSetter {
    @Override
    public void setConfig(String storeName, Options options, Map<String, Object> configs) {
        BlockBasedTableConfig tableConfig = new BlockBasedTableConfig();
        tableConfig.setBlockCacheSize(50 * 1024 * 1024L);
        options.setTableFormatConfig(tableConfig);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. (!) Changelog topic — для чего?

Каждый persistent state store имеет соответствующий **changelog topic** в Kafka.

```
my-app-store-changelog (compacted topic)
  - key1 → value1
  - key2 → value2
  - key1 → value3  (last write wins)
```

**Зачем:**
- При сбое instance → восстановление state из changelog
- При scale-up → новый instance читает свою partition из changelog

**Compacted topic** — только last value per key хранится. Не растёт бесконечно.

Чем больше state → больше storage в Kafka. Это **trade-off** упрощённости.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. (!) Interactive Queries?

`Interactive Queries` — **прямое чтение** state stores из приложения, без Kafka.

```java
KafkaStreams streams = ...;
streams.start();

// Получить store
ReadOnlyKeyValueStore<String, Long> store = streams.store(
    StoreQueryParameters.fromNameAndType("my-store", QueryableStoreTypes.keyValueStore())
);

Long value = store.get("alice");
```

**Distributed:** state разделён между instances. Чтобы найти где key — `streams.metadataForKey("my-store", key)`.

**Применение:** Kafka Streams app становится **читаемым store** (как in-memory DB), без отдельного DB.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Event time vs processing time?

Kafka Streams поддерживает оба:

- **Event time** — timestamp из записи (Kafka header или из payload)
- **Processing time** — wall-clock time

По умолчанию — **event time** через timestamp в Kafka record.

```java
Properties props = new Properties();
props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
    WallclockTimestampExtractor.class); // processing time
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. TimestampExtractor?

```java
public class CustomExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        Order order = (Order) record.value();
        return order.eventTime; // из payload, не из Kafka
    }
}
```

Полезно когда event time — внутри payload, не в Kafka headers.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. (!) Exactly-once семантика в Kafka Streams?

С Kafka Streams 0.11+ — **exactly-once** через **Kafka transactions**:

```java
props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
```

**Гарантия end-to-end:**
- Kafka source → Streams processing → Kafka sink — exactly-once
- Включает state stores (changelog updates тоже транзакционные)

**Стоимость:** ~5-15% throughput (overhead на transactions).

**При необходимости:** `at_least_once` (default) — быстрее, возможны дубли.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. Standby replicas?

```java
props.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1);
```

Standby instances держат **горячую копию** state stores. При failover — почти мгновенный takeover (не нужно читать changelog с нуля).

Trade-off: больше resources, но выше availability.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. (!) Что происходит при сбое instance?

1. Kafka **детектирует** dead consumer (по timeout)
2. **Rebalance** — partitions перераспределяются на живые instances
3. Новый owner partition **читает changelog** для восстановления state
4. После восстановления — продолжает обработку

Время recovery зависит от **размера state**. С standby replicas — быстро.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. (!) Что такое ksqlDB?

`ksqlDB` — SQL-like layer над Kafka Streams. Позволяет писать stream processing **на SQL** без Java-кода.

```sql
CREATE STREAM orders (id INT, amount DOUBLE, user_id VARCHAR)
  WITH (KAFKA_TOPIC='orders', VALUE_FORMAT='JSON');

CREATE TABLE total_per_user AS
  SELECT user_id, SUM(amount) AS total
  FROM orders
  WINDOW TUMBLING (SIZE 5 MINUTES)
  GROUP BY user_id;

SELECT * FROM total_per_user EMIT CHANGES;
```

**ksqlDB Server** — process, выполняющий ksql queries. Под капотом — Kafka Streams.

**Применения:** простые трансформации без Java-кода, BI/аналитики, low-code stream processing.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. (!) Kafka Streams vs Flink — когда что?

| Критерий | Kafka Streams | Flink |
|----------|---------------|-------|
| Source | Только Kafka | Любой |
| Deploy | Library (как K8s pod) | Cluster |
| Latency | Низкая (~10-100ms) | Очень низкая (<10ms) |
| Throughput | Высокий | Очень высокий |
| Stateful | Через RocksDB + changelog | Богатый state API |
| Operational | Простая (микросервис) | Сложнее (cluster ops) |
| Languages | Java, Scala | Java, Scala, Python (PyFlink) |
| Ecosystem | Confluent + open-source | Богатая |

**Kafka Streams когда:**
- Источник и sink — Kafka
- Хочется простоты deploy
- Микросервисная архитектура
- Не нужны самые низкие latencies

**Flink когда:**
- Сложные joins, CEP
- Очень низкая latency (<10ms)
- Источники/sinks — не только Kafka
- Огромный state, нужен fine-grained control


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. (!) Где Kafka Streams в production?

- **Confluent (создатели)** — внутренние сервисы
- **LinkedIn** — multiple use cases (Kafka родом из LinkedIn)
- **Uber** — fraud detection, real-time pricing
- **Pinterest, Airbnb, Slack** — event-driven services
- **Banks (Goldman Sachs, ING)** — risk, fraud
- **Walmart** — supply chain

В **банках и финтехе** Kafka Streams особенно популярен — простой deployment + exactly-once.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. Какие минусы Kafka Streams?

1. **Только Kafka** — нельзя читать из других sources напрямую
2. **State limited** by local disk — RocksDB растёт, нужно следить
3. **Rebalance** при scale up/down — пауза в обработке
4. **Сложно debug** — distributed processing
5. **Не такой богатый CEP** как Flink
6. **Ksql имеет ограничения** vs Java/Scala API
7. **Java-only** (Scala через JVM API, нет Python)
8. **Less mature** для очень больших scale (Flink выигрывает)

В **2024** Kafka Streams — отличный выбор для **event-driven микросервисов** на Kafka, не для general-purpose stream processing.

---

## See also

- [Apache Spark](apache-spark-interview.md) — другой stream processing
- [Apache Flink](apache-flink-interview.md) — главный конкурент для streaming
- [Apache Kafka](../messaging/kafka-interview.md) — основа Kafka Streams
- [Stream Processing](stream-processing-interview.md) — общие концепции
- [Event-driven паттерны](../architecture/event-driven-patterns-interview.md) — где Kafka Streams сильны
- [Микросервисы](../architecture/microservices-interview.md) — естественная среда Kafka Streams
- [Scala](../programming-languages/scala/scala-interview.md) — может быть использован для Kafka Streams
- [Spring Cloud](../frameworks/spring/spring-cloud-interview.md) — Spring Cloud Stream поверх Kafka Streams
- [Apache Airflow](apache-airflow-interview.md) — orchestration не-streaming
- [Data Warehousing](data-warehousing-interview.md) — обычно sink Kafka Streams
- [Saga Pattern](../architecture/saga-pattern-interview.md) — Kafka Streams для choreographed sagas


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [Apache Airflow](apache-airflow-interview.md)
- [Apache Flink](apache-flink-interview.md)
- [Apache Spark](apache-spark-interview.md)
- [Data Lake и Lakehouse](data-lake-lakehouse-interview.md)
- [Data Warehousing](data-warehousing-interview.md)
- [dbt](dbt-interview.md)
