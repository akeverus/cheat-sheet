---
title: "Вопросы на собеседовании: Kafka Streams"
description: "Kafka Streams: stream processing library поверх Kafka, KStream/KTable/GlobalKTable, joins, windows, state stores (RocksDB), exactly-once, interactive queries, ksqlDB, отличие от Flink"
tags:
  - interview
  - data-engineering
  - kafka-streams-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Kafka Streams"
  - "Kafka Streams interview"
  - "Kafka Streams собеседование"
prerequisites:
  - "[[kafka]]"
next: []
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

(!) Что такое Kafka Streams?

`Kafka Streams` — **клиентская библиотека** для построения приложений потоковой обработки на JVM. Часть Apache Kafka (начиная с 0.10).

**Ключевые особенности:**
- **Библиотека, а не фреймворк** — обычное Java/Scala-приложение, отдельный кластер не нужен
- Хранит **state в Kafka topics** (changelog) — отказоустойчивость из коробки
- Использует Kafka topics как вход и выход
- Поддерживает **exactly-once** (начиная с Kafka 0.11+)

**Применения:** event-driven микросервисы, агрегации в реальном времени, соединение потоков, выявление мошенничества в финтехе.

## Q2. (!) Чем Kafka Streams отличается от Spark/Flink?

| Критерий | Kafka Streams | Spark/Flink |
|----------|---------------|-------------|
| Архитектура | Library | Framework + кластер |
| Развёртывание | Как обычное Java-приложение | Spark-кластер, Flink JobManager |
| Source/Sink | Только Kafka | Kafka, файлы, БД, ... |
| State | Kafka topics + RocksDB | Произвольные backends |
| Масштабирование | Через consumer groups | Через cluster manager |
| Операционная сложность | Низкая | Высокая |
| Отдельный кластер | Не нужен | Нужен |
| Fault tolerance | Через Kafka | Через checkpoints |

**Главная идея Kafka Streams:** «деплоим в Kubernetes как обычный микросервис, отдельный Hadoop-кластер не нужен».

## Q3. (!) Application как отдельный процесс — почему?

В Kafka Streams **нет JobManager/Driver** — каждый instance твоего приложения = самостоятельный JVM-процесс.

```
[ Pod 1 (Streams app) ] ┐
[ Pod 2 (Streams app) ] ─→ Kafka cluster
[ Pod 3 (Streams app) ] ┘
```

**Координация — через Kafka:**
- Topics как вход и выход
- Распределение partitions через **consumer groups**
- Сохранение state через **changelog topics**

**Преимущества:**
- Простое развёртывание — это обычное Java-приложение
- Хорошо ложится на K8s
- Нет единой точки отказа (для координации)
- Авто-failover через consumer rebalance

## Q4. (!) Что такое topology в Kafka Streams?

`Topology` — это DAG операций (sources, processors, sinks).

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

Топологию можно **визуализировать** через `topology.describe()`.

## Q5. (!) Streams DSL vs Processor API?

**Streams DSL** — высокоуровневый API, с операциями `map/filter/groupBy/join`:

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

**Processor API** — низкоуровневый, с ручным управлением state и таймерами:

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

В большинстве задач используют DSL. Processor API — для особых случаев.

## Q6. (!) Чем KStream отличается от KTable?

**KStream** — поток **независимых событий** (insert-only). Каждая запись — отдельное свершившееся событие.

**KTable** — **таблица** (последнее значение по ключу). Записи с одинаковым ключом **перезаписывают** предыдущее значение.

```
KStream events:
  (alice, +10)  (bob, +20)  (alice, +30)
  → 3 события

KTable balances:
  (alice, +10) (bob, +20) (alice, +30)
  → текущее состояние: {alice: +30, bob: +20}
```

**KStream** — для неизменяемых фактов (orders, clicks).
**KTable** — для текущего состояния (user profiles, prices).

## Q7. (!) GlobalKTable — когда использовать?

| Тип | Партиционирование | Репликация |
|-----|-------------|-------------|
| `KTable` | По partitions Kafka topic | Каждый instance держит свой shard |
| `GlobalKTable` | Не партиционирована | Каждый instance держит **полную копию** |

`GlobalKTable` — для **lookup-таблиц** (маленькие, нужны на каждом instance):

```java
GlobalKTable<String, User> users = builder.globalTable("users");

orders.join(users,
    (orderId, order) -> order.userId,  // KStream key extractor
    (order, user) -> enrich(order, user)
)
```

**Преимущество:** join без repartitioning (данные есть на любом instance).
**Недостаток:** не масштабируется (если таблица большая → OOM).

## Q8. (!) Конвертации KStream ↔ KTable?

```java
// KStream → KTable
KTable<String, Long> counts = events
    .groupByKey()
    .count();

// KTable → KStream
KStream<String, Long> changeLog = counts.toStream();
```

`KTable.toStream()` — даёт поток **изменений** (changelog). Полезно для отправки этих изменений дальше по конвейеру (downstream).

```java
// Из KStream получить latest по key
KTable<String, Order> latest = orders
    .groupByKey()
    .reduce((oldVal, newVal) -> newVal);
```

## Q9. (!) map, filter, flatMap, branch?

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

**Подвох:** `map` (с изменением ключа) запускает **repartitioning**. `mapValues` — нет.

## Q10. (!) groupByKey vs groupBy?

```java
// groupByKey — текущий key, без repartition
events.groupByKey()

// groupBy — новый key, ТРИГЕРИТ repartitioning
events.groupBy((k, v) -> v.userId)
```

`groupBy` создаёт **внутренний repartition-топик**. Дороже, но позволяет группировать по любому полю.

## Q11. (!) Aggregate, Reduce, Count?

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

`aggregate` — самый мощный, может строить произвольное состояние per key.

## Q12. (!) Joins — типы и семантика?

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

| Join | Оконный | Repartition |
|------|----------|-------------|
| KStream-KStream | Да (обязательно) | Если partitions различаются |
| KStream-KTable | Нет | Стрим должен быть co-partition с таблицей |
| KStream-GlobalKTable | Нет | Не нужен |
| KTable-KTable | Нет | Co-partition |

## Q13. (!) Tumbling, Hopping, Session windows?

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

В Kafka Streams «**hopping window**» — это то, что в Flink/Spark называют скользящим (sliding) окном.

## Q14. Sliding windows?

С Kafka Streams 2.7+ появились **SlidingWindows** (отдельная концепция):

```java
SlidingWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5))
```

Создаёт окно для **каждой пары** записей в пределах timeDifference. Полезно для pattern matching.

## Q15. (!) Grace period для late events?

```java
TimeWindows.of(Duration.ofMinutes(5))
           .grace(Duration.ofMinutes(1))
```

После закрытия окна Kafka Streams **ждёт grace period** для запоздавших событий. По истечении — окно становится «closed», state удаляется.

С Kafka Streams 2.5+ — **обязательно** указывать grace (default = `Long.MAX_VALUE`, но это плохая практика — состояние будет расти бесконечно).

## Q16. (!) Что такое state store?

State store — **локальное** key-value хранилище для операций с состоянием (агрегации, joins).

По умолчанию — **RocksDB на локальном диске** (для большого state быстрее, чем JVM heap).

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

## Q17. RocksDB как state backend?

`RocksDB` — встраиваемое LSM-tree key-value хранилище от Facebook. Используется в Kafka Streams по умолчанию.

**Преимущества:**
- State не ограничен объёмом RAM (хранится на диске)
- Хорошая производительность (LSM-tree оптимизирован под write-heavy нагрузку)
- Встроенное сжатие

**Тюнинг:** через `RocksDBConfigSetter`:

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

## Q18. (!) Changelog topic — для чего?

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

**Compacted topic** — хранит только последнее значение для каждого ключа. Не растёт бесконечно.

Чем больше state → тем больше места занимает в Kafka. Это **trade-off** за простоту.

## Q19. (!) Interactive Queries?

`Interactive Queries` — **прямое чтение** state stores из приложения, минуя Kafka.

```java
KafkaStreams streams = ...;
streams.start();

// Получить store
ReadOnlyKeyValueStore<String, Long> store = streams.store(
    StoreQueryParameters.fromNameAndType("my-store", QueryableStoreTypes.keyValueStore())
);

Long value = store.get("alice");
```

**Распределённость:** state разделён между instances. Чтобы найти, где находится ключ — `streams.metadataForKey("my-store", key)`.

**Применение:** Kafka Streams app становится **читаемым store** (как in-memory БД), без отдельной БД.

## Q20. (!) Event time vs processing time?

Kafka Streams поддерживает оба варианта:

- **Event time** — timestamp из самой записи (Kafka header или payload)
- **Processing time** — wall-clock, время обработки

По умолчанию — **event time** через timestamp в Kafka record.

```java
Properties props = new Properties();
props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
    WallclockTimestampExtractor.class); // processing time
```

## Q21. TimestampExtractor?

```java
public class CustomExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        Order order = (Order) record.value();
        return order.eventTime; // из payload, не из Kafka
    }
}
```

Полезно, когда event time лежит внутри payload, а не в Kafka headers.

## Q22. (!) Exactly-once семантика в Kafka Streams?

С Kafka Streams 0.11+ — **exactly-once** реализуется через **Kafka transactions**:

```java
props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
```

**Гарантия end-to-end:**
- Kafka source → Streams processing → Kafka sink — exactly-once
- Распространяется и на state stores (обновления changelog тоже транзакционные)

**Стоимость:** ~5-15% throughput (накладные расходы на транзакции).

**Если нужно иначе:** `at_least_once` (default) — быстрее, но возможны дубли.

## Q23. Standby replicas?

```java
props.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1);
```

Standby-инстансы держат **горячую копию** state stores. При failover — почти мгновенный takeover (не нужно читать changelog с нуля).

Trade-off: больше потребляемых ресурсов, но выше доступность.

## Q24. (!) Что происходит при сбое instance?

1. Kafka **обнаруживает** мёртвого consumer (по timeout)
2. **Rebalance** — partitions перераспределяются на живые instances
3. Новый владелец partition **читает changelog**, чтобы восстановить state
4. После восстановления — продолжает обработку

Время восстановления зависит от **размера state**. Со standby replicas — быстро.

## Q25. (!) Что такое ksqlDB?

`ksqlDB` — SQL-подобный слой поверх Kafka Streams. Позволяет писать stream processing **на SQL** без Java-кода.

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

**ksqlDB Server** — процесс, выполняющий ksql-запросы. Под капотом — Kafka Streams.

**Применения:** простые трансформации без Java-кода, BI/аналитика, low-code stream processing.

## Q26. (!) Kafka Streams vs Flink — когда что?

| Критерий | Kafka Streams | Flink |
|----------|---------------|-------|
| Source | Только Kafka | Любой |
| Развёртывание | Library (как K8s pod) | Cluster |
| Latency | Низкая (~10-100ms) | Очень низкая (<10ms) |
| Throughput | Высокий | Очень высокий |
| Stateful | Через RocksDB + changelog | Богатый state API |
| Эксплуатация | Простая (микросервис) | Сложнее (cluster ops) |
| Языки | Java, Scala | Java, Scala, Python (PyFlink) |
| Экосистема | Confluent + open-source | Богатая |

**Kafka Streams — когда:**
- Источник и sink — Kafka
- Нужна простота развёртывания
- Микросервисная архитектура
- Не требуются предельно низкие latencies

**Flink — когда:**
- Сложные joins, CEP
- Очень низкая latency (<10ms)
- Источники/sinks — не только Kafka
- Огромный state, нужен fine-grained-контроль

## Q27. (!) Где Kafka Streams в production?

- **Confluent (создатели)** — внутренние сервисы
- **LinkedIn** — множество сценариев (Kafka родом из LinkedIn)
- **Uber** — fraud detection, real-time pricing
- **Pinterest, Airbnb, Slack** — event-driven сервисы
- **Банки (Goldman Sachs, ING)** — риски, fraud
- **Walmart** — supply chain

В **банках и финтехе** Kafka Streams особенно популярен — простота развёртывания + exactly-once.

## Q28. Какие минусы Kafka Streams?

1. **Только Kafka** — нельзя читать из других источников напрямую
2. **State ограничен** локальным диском — RocksDB растёт, нужно следить
3. **Rebalance** при scale up/down — пауза в обработке
4. **Сложно дебажить** — распределённая обработка
5. **CEP не такой богатый**, как у Flink
6. **У ksql есть ограничения** по сравнению с Java/Scala API
7. **Только Java** (Scala через JVM API, Python нет)
8. **Менее зрелый** для очень больших масштабов (тут выигрывает Flink)

В **2024** Kafka Streams — отличный выбор для **event-driven микросервисов** на Kafka, но не для general-purpose stream processing.

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

- [Apache Flink](apache-flink-interview.md)
- [Apache Spark](apache-spark-interview.md)
- [Data Lake и Lakehouse](data-lake-lakehouse-interview.md)
- [Data Warehousing](data-warehousing-interview.md)
- [dbt](dbt-interview.md)
