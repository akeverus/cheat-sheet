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

`Kafka Streams` — **библиотека** (а не отдельный кластер!) для потоковой обработки поверх Apache Kafka. Запускается как обычное Java/Scala-приложение, использует Kafka topics и на вход/выход, и для хранения **состояния**. Особенно удобен для **event-driven микросервисов** — без отдельного кластера Spark или Flink.

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
- [Q5. (!) Чем Streams DSL отличается от Processor API?](#q5--чем-streams-dsl-отличается-от-processor-api)

**KStream, KTable, GlobalKTable**
- [Q6. (!) Чем KStream отличается от KTable?](#q6--чем-kstream-отличается-от-ktable)
- [Q7. (!) GlobalKTable — когда использовать?](#q7--globalktable--когда-использовать)
- [Q8. (!) Конвертации KStream ↔ KTable?](#q8--конвертации-kstream--ktable)

**Операции**
- [Q9. (!) Операции преобразования потока: `map`, `filter`, `flatMap`, `branch`?](#q9--операции-преобразования-потока-map-filter-flatmap-branch)
- [Q10. (!) Чем `groupByKey` отличается от `groupBy`?](#q10--чем-groupbykey-отличается-от-groupby)
- [Q11. (!) Чем различаются `aggregate`, `reduce` и `count`?](#q11--чем-различаются-aggregate-reduce-и-count)
- [Q12. (!) Joins — типы и семантика?](#q12--joins--типы-и-семантика)

**Windows**
- [Q13. (!) Окна Tumbling, Hopping и Session — в чём разница?](#q13--окна-tumbling-hopping-и-session--в-чём-разница)
- [Q14. Что такое Sliding windows?](#q14-что-такое-sliding-windows)
- [Q15. (!) Grace period для late events?](#q15--grace-period-для-late-events)

**State**
- [Q16. (!) Что такое state store?](#q16--что-такое-state-store)
- [Q17. RocksDB как state backend?](#q17-rocksdb-как-state-backend)
- [Q18. (!) Changelog topic — для чего?](#q18--changelog-topic--для-чего)
- [Q19. (!) Что такое Interactive Queries?](#q19--что-такое-interactive-queries)

**Time semantics**
- [Q20. (!) Чем event time отличается от processing time?](#q20--чем-event-time-отличается-от-processing-time)
- [Q21. Зачем нужен `TimestampExtractor`?](#q21-зачем-нужен-timestampextractor)

**Exactly-once и fault tolerance**
- [Q22. (!) Exactly-once семантика в Kafka Streams?](#q22--exactly-once-семантика-в-kafka-streams)
- [Q23. Зачем нужны Standby replicas?](#q23-зачем-нужны-standby-replicas)
- [Q24. (!) Что происходит при сбое instance?](#q24--что-происходит-при-сбое-instance)

**ksqlDB**
- [Q25. (!) Что такое ksqlDB?](#q25--что-такое-ksqldb)

**Сравнения и применение**
- [Q26. (!) Kafka Streams vs Flink — когда что?](#q26--kafka-streams-vs-flink--когда-что)
- [Q27. (!) Где Kafka Streams в production?](#q27--где-kafka-streams-в-production)
- [Q28. Какие минусы Kafka Streams?](#q28-какие-минусы-kafka-streams)

## Q1. (!) Что такое Kafka Streams?

`Kafka Streams` — клиентская библиотека для потоковой обработки на JVM, входящая в состав Apache Kafka (с версии 0.10). Ключевое отличие от Spark и Flink: это не отдельная платформа с собственным кластером, а просто зависимость, которую вы добавляете в обычное Java/Scala-приложение.

Главная идея: вы пишете код, который читает из Kafka-топиков, преобразует и агрегирует данные и пишет результат обратно в Kafka. Состояние (счётчики, окна, joins) тоже живёт в Kafka — в специальных changelog-топиках, поэтому отказоустойчивость работает без внешних систем.

**Что важно подчеркнуть на собеседовании:**
- **Библиотека, а не фреймворк** — нет JobManager, Driver или отдельного кластера; деплоится как обычный микросервис.
- **State в Kafka topics** (changelog) — состояние реплицируется в Kafka, поэтому при падении инстанса оно восстанавливается из топика.
- **Kafka — и источник, и приёмник** — на входе и на выходе всегда топики.
- **Exactly-once** (с Kafka 0.11+) — сквозная гарантия «прочитал → обработал → записал ровно один раз» через транзакции Kafka.

**Сценарии применения:** event-driven микросервисы, агрегации в реальном времени, обогащение и соединение потоков, выявление мошенничества в финтехе.

## Q2. (!) Чем Kafka Streams отличается от Spark/Flink?

Коротко: Spark и Flink — это **платформы**, которым нужен собственный кластер и менеджер задач, а Kafka Streams — **библиотека**, которая встраивается в ваш сервис. Из этого вытекают все остальные различия.

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

Практический смысл различий: с Kafka Streams вы масштабируетесь, просто запуская больше инстансов приложения (партиции перераспределит consumer group), а не настраивая отдельный cluster manager. Платой за эту простоту становится привязка к Kafka как единственному источнику и приёмнику данных.

**Главная идея Kafka Streams:** «деплоим в Kubernetes как обычный микросервис, отдельный Hadoop-кластер не нужен».

## Q3. (!) Application как отдельный процесс — почему?

Потому что в Kafka Streams **нет центрального координатора** (JobManager или Driver, как в Flink/Spark): каждый инстанс приложения — это самостоятельный JVM-процесс, который ни от кого не зависит. Всю координацию между инстансами берёт на себя сам Kafka.

```
[ Pod 1 (Streams app) ] ┐
[ Pod 2 (Streams app) ] ─→ Kafka cluster
[ Pod 3 (Streams app) ] ┘
```

**Координация — целиком через Kafka:**
- Topics — вход и выход данных.
- Распределение partitions между инстансами — через механизм **consumer groups** (тот же, что у обычных Kafka-консьюмеров).
- Сохранение и восстановление state — через **changelog topics**.

**Что это даёт:**
- Простое развёртывание — это обычное Java-приложение, без отдельной инфраструктуры.
- Хорошо ложится на Kubernetes: один pod = один инстанс.
- Нет единой точки отказа в координации — если падает инстанс, остальные продолжают работать.
- Авто-failover «из коробки»: при падении инстанса срабатывает consumer rebalance и его партиции подхватывают живые инстансы.

## Q4. (!) Что такое topology в Kafka Streams?

`Topology` — это граф обработки данных (DAG), описывающий, как событие проходит через приложение: от **source** (чтение из топика) через **processors** (преобразования) к **sink** (запись в топик). Когда вы вызываете `builder.stream(...).filter(...).to(...)`, вы как раз и собираете эту топологию.

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

Топологию можно **распечатать** через `topology.describe()` — это полезно при отладке, чтобы увидеть реальную цепочку узлов и понять, где Kafka Streams вставит repartition-топики.

## Q5. (!) Чем Streams DSL отличается от Processor API?

В Kafka Streams два уровня API, и выбор между ними — частый вопрос на собеседовании.

**Streams DSL** — высокоуровневый API с готовыми операциями `map/filter/groupBy/join`. Лаконичен, читается как функциональный пайплайн, сам управляет состоянием и repartition-топиками:

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

**Processor API** — низкоуровневый: вы сами управляете state stores, таймерами (`punctuate`) и тем, как именно обрабатывается каждая запись. Многословнее, но даёт полный контроль:

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

**Эмпирическое правило:** в большинстве задач хватает DSL. К Processor API спускаются, когда нужны кастомные таймеры, нестандартная логика поверх state store или поведение, которого нет в DSL. Их можно и комбинировать: вставить `process()`-узел в DSL-топологию.

## Q6. (!) Чем KStream отличается от KTable?

Это фундаментальная двойственность Kafka Streams: один и тот же топик можно интерпретировать либо как поток событий, либо как таблицу состояний.

**KStream** — поток **независимых событий** (insert-only). Каждая запись — самостоятельный свершившийся факт; новая запись с тем же ключом не отменяет предыдущую, а просто добавляется.

**KTable** — **таблица** последних значений по ключу. Запись с уже существующим ключом **перезаписывает** прежнее значение (как `UPSERT` в БД), а `null`-значение трактуется как удаление (tombstone).

```
KStream events:
  (alice, +10)  (bob, +20)  (alice, +30)
  → 3 события

KTable balances:
  (alice, +10) (bob, +20) (alice, +30)
  → текущее состояние: {alice: +30, bob: +20}
```

**Когда что:**
- **KStream** — для неизменяемых фактов: orders, clicks, транзакции. Важна каждая запись.
- **KTable** — для текущего состояния сущности: профили пользователей, цены, остатки. Важно только последнее значение.

## Q7. (!) GlobalKTable — когда использовать?

Разница между `KTable` и `GlobalKTable` — в том, как данные распределяются по инстансам. Обычная `KTable` шардирована: каждый инстанс хранит только те партиции, что ему достались. `GlobalKTable` реплицируется целиком — каждый инстанс держит полную копию таблицы.

| Тип | Партиционирование | Репликация |
|-----|-------------|-------------|
| `KTable` | По partitions Kafka topic | Каждый instance держит свой shard |
| `GlobalKTable` | Не партиционирована | Каждый instance держит **полную копию** |

`GlobalKTable` нужна для **lookup-таблиц** — небольших справочников, к которым нужен доступ на каждом инстансе независимо от ключа события:

```java
GlobalKTable<String, User> users = builder.globalTable("users");

orders.join(users,
    (orderId, order) -> order.userId,  // KStream key extractor
    (order, user) -> enrich(order, user)
)
```

**Плюс:** join обходится без repartitioning — раз полная копия таблицы есть на каждом инстансе, искать нужный ключ удалённо не приходится. Стрим тоже не обязан быть co-partition с таблицей.
**Минус:** не масштабируется — таблица целиком лежит в памяти/RocksDB каждого инстанса, поэтому большой справочник приведёт к OOM. Подходит только для небольших и относительно статичных данных.

## Q8. (!) Конвертации KStream ↔ KTable?

Поскольку поток и таблица — две стороны одной медали, между ними можно переходить в обе стороны.

**KStream → KTable** делается через агрегацию по ключу (`count`, `reduce`, `aggregate`): поток событий «схлопывается» в таблицу текущих значений.

```java
// KStream → KTable
KTable<String, Long> counts = events
    .groupByKey()
    .count();

// KTable → KStream
KStream<String, Long> changeLog = counts.toStream();
```

**KTable → KStream** через `toStream()` даёт поток **изменений** (changelog): каждое обновление таблицы превращается в событие. Это нужно, чтобы отправить изменения дальше по конвейеру (downstream) — например, записать в выходной топик.

Частый приём — получить из потока событий таблицу с последним значением по ключу: группируем по ключу и в `reduce` всегда возвращаем новое значение.

```java
// Из KStream получить latest по key
KTable<String, Order> latest = orders
    .groupByKey()
    .reduce((oldVal, newVal) -> newVal);
```

## Q9. (!) Операции преобразования потока: `map`, `filter`, `flatMap`, `branch`?

Это базовые stateless-операции DSL для преобразования потока. Главное, что нужно про них знать — какие из них меняют ключ и потому приводят к repartition (см. ниже).

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

**Подводный камень:** `map` меняет ключ, а значит Kafka Streams вынужден перераспределить данные по партициям — запускается **repartitioning** (запись во внутренний топик и перечитывание). `mapValues` трогает только значение, ключ остаётся прежним → repartition не нужен. Поэтому, если меняете лишь значение, всегда предпочитайте `mapValues`/`filter` вместо `map`.

## Q10. (!) Чем `groupByKey` отличается от `groupBy`?

Обе операции группируют поток перед агрегацией, но различаются ценой: меняют они ключ или нет.

```java
// groupByKey — текущий key, без repartition
events.groupByKey()

// groupBy — новый key, ТРИГЕРИТ repartitioning
events.groupBy((k, v) -> v.userId)
```

`groupByKey` группирует по уже существующему ключу — данные перемещать не нужно, repartition не происходит. `groupBy` задаёт новый ключ, поэтому Kafka Streams создаёт **внутренний repartition-топик** и переливает данные через него: это дороже (лишняя запись/чтение в Kafka), но позволяет группировать по любому полю значения.

**Эмпирическое правило:** если данные уже сгруппированы по нужному ключу — используйте `groupByKey`; `groupBy` берите только когда ключ группировки реально другой.

## Q11. (!) Чем различаются `aggregate`, `reduce` и `count`?

Три stateful-операции, которые превращают сгруппированный поток в `KTable`. Различаются по мощности: `count` — частный случай, `reduce` — общее свёртывание без смены типа, `aggregate` — самое гибкое.

- **count** — считает число записей по ключу. Результат всегда `Long`.
- **reduce** — сворачивает значения попарно; тип результата совпадает с типом входа (`(v1, v2) -> ...`).
- **aggregate** — позволяет накапливать произвольное состояние, тип которого отличается от входного (например, поток событий → объект со статистикой).

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

**Итог:** `aggregate` — самый мощный, он включает в себя возможности двух остальных; `count` и `reduce` стоит брать как более короткую запись, когда их хватает.

## Q12. (!) Joins — типы и семантика?

Joins в Kafka Streams различаются по тому, **что** соединяется (поток или таблица) и **нужно ли окно**. Главное правило: соединение двух потоков всегда оконное (нужно ограничить, какие события считать «одновременными»), а соединение с таблицей — это lookup без окна.

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

**Про co-partitioning:** для всех join, кроме `GlobalKTable`, обе стороны должны быть **co-partition** — одинаковое число партиций и один и тот же ключ. Если это не так, Kafka Streams либо потребует repartition, либо просто не сможет соединить данные, потому что связанные записи окажутся в разных партициях на разных инстансах. Именно поэтому `GlobalKTable` так удобна для lookup: полная копия на каждом инстансе снимает требование co-partition.

## Q13. (!) Окна Tumbling, Hopping и Session — в чём разница?

Окна нужны, чтобы агрегировать события не за всё время, а по интервалам. Три основных типа различаются тем, как они нарезают время:

- **Tumbling** — непересекающиеся окна фиксированного размера, идущие встык. Каждое событие попадает ровно в одно окно. Пример: «количество заказов за каждые 5 минут».
- **Hopping** — окна фиксированного размера, которые накладываются друг на друга и сдвигаются с шагом `advanceBy`. Одно событие может попасть сразу в несколько окон. Пример: «сумма за последние 5 минут, пересчитываемая каждую минуту».
- **Session** — окна переменной длины, привязанные к активности: новое событие продлевает сессию, а пауза дольше заданного gap её закрывает. Пример: «сессия активности пользователя».

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

**Терминологическая ловушка:** то, что в Kafka Streams называется «**hopping window**», в Flink и Spark называют скользящим (sliding) окном. А `SlidingWindows` в самой Kafka Streams (см. ниже) — это уже отдельная, третья концепция. Об этом легко запутаться на собеседовании.

## Q14. Что такое Sliding windows?

С Kafka Streams 2.7+ появились **SlidingWindows** — отдельная концепция, не путать с hopping-окнами:

```java
SlidingWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5))
```

В отличие от tumbling/hopping (где границы окон фиксированы заранее), sliding-окно создаётся для **каждой пары** записей, оказавшихся в пределах `timeDifference` друг от друга. То есть границы окна «привязаны» к самим событиям. Это полезно, когда важны отношения между близкими по времени событиями — например, для pattern matching.

## Q15. (!) Grace period для late events?

Grace period — это интервал, в течение которого окно после своего «конца» по event time всё ещё принимает запоздавшие события. Он решает классическую проблему стриминга: событие произошло внутри окна, но дошло до приложения с опозданием.

```java
TimeWindows.of(Duration.ofMinutes(5))
           .grace(Duration.ofMinutes(1))
```

Как это работает: окно закрывается по времени, но Kafka Streams **держит его state ещё на grace period** и обновляет агрегат, если за это время придут опоздавшие события этого окна. Как только grace истёк, окно становится «closed», его state удаляется, а более поздние события для него уже отбрасываются.

**Подводный камень:** с Kafka Streams 2.5+ grace желательно задавать явно. Старое поведение по умолчанию (`Long.MAX_VALUE`) означает, что окна не закрываются никогда — state копится бесконечно и приложение со временем упирается в диск. Поэтому grace выбирают как **компромисс** между терпимостью к опозданиям и ростом состояния.

## Q16. (!) Что такое state store?

State store — это **локальное** key-value хранилище, в котором Kafka Streams держит состояние stateful-операций: промежуточные результаты агрегаций, окна, буферы для join. Без него count или aggregate было бы негде накапливать.

По умолчанию это **RocksDB на локальном диске** каждого инстанса. Диск выбран намеренно: состояние может не помещаться в JVM heap, а RocksDB позволяет хранить его вне кучи и при этом быстро читать/писать. При этом store остаётся отказоустойчивым — он зеркалируется в Kafka через changelog-топик (см. Q18).

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

`RocksDB` — встраиваемое key-value хранилище от Facebook на основе LSM-tree, которое Kafka Streams использует по умолчанию для persistent state stores. Оно работает прямо внутри JVM-процесса (это библиотека, а не отдельный сервис), но хранит данные на диске вне heap.

**Почему именно оно:**
- **State не ограничен объёмом RAM** — данные лежат на диске, поэтому можно держать состояние больше доступной памяти.
- **Высокая производительность на запись** — LSM-tree оптимизирован под write-heavy нагрузку, типичную для стриминга.
- **Встроенное сжатие** — экономит место на диске.

**Тюнинг** выполняют через `RocksDBConfigSetter` — например, чтобы ограничить размер block cache, если RocksDB занимает слишком много памяти:

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

Changelog-топик — это механизм, который делает локальный (и потенциально теряемый при падении инстанса) state store отказоустойчивым. Каждый persistent state store зеркалируется в свой **changelog topic** в Kafka: любое изменение в store сначала пишется в этот топик.

```
my-app-store-changelog (compacted topic)
  - key1 → value1
  - key2 → value2
  - key1 → value3  (last write wins)
```

**Зачем это нужно:**
- **При сбое инстанса** — новый владелец партиции восстанавливает state, перечитывая changelog с начала.
- **При scale-up** — новый инстанс читает свою партицию changelog, чтобы поднять локальный store.

**Почему changelog — compacted topic:** log compaction хранит только последнее значение для каждого ключа, поэтому топик не растёт бесконечно (старые версии ключа удаляются) и восстановление не нужно проигрывать всю историю изменений.

**Компромисс:** чем больше state, тем больше места он занимает в Kafka и тем дольше восстановление при failover. Это цена за то, что отказоустойчивость работает «из коробки», без внешнего хранилища.

## Q19. (!) Что такое Interactive Queries?

`Interactive Queries` — это возможность **читать содержимое state stores напрямую** из своего приложения, минуя Kafka и не отправляя результат в выходной топик. То есть состояние, которое Kafka Streams и так держит для агрегаций, становится доступным как обычное key-value хранилище для запросов извне (например, из REST-эндпоинта).

```java
KafkaStreams streams = ...;
streams.start();

// Получить store
ReadOnlyKeyValueStore<String, Long> store = streams.store(
    StoreQueryParameters.fromNameAndType("my-store", QueryableStoreTypes.keyValueStore())
);

Long value = store.get("alice");
```

**Подводный камень — распределённость:** state шардирован между инстансами, и на конкретном инстансе лежит только часть ключей. Поэтому нужный ключ может оказаться на соседнем инстансе. Найти, где он находится, помогает `streams.metadataForKey("my-store", key)` — дальше запрос проксируют на нужный инстанс (обычно по HTTP).

**Сценарий применения:** само Kafka Streams приложение становится **читаемым хранилищем** (как in-memory БД) — можно отдавать актуальные агрегаты по REST без отдельной базы данных под результаты.

## Q20. (!) Чем event time отличается от processing time?

Это вопрос о том, **какое время** использовать для оконных операций — момент, когда событие реально произошло, или момент, когда приложение его обрабатывает. Выбор напрямую влияет на корректность агрегаций при задержках и пересортировке событий.

- **Event time** — timestamp самого события (из Kafka record или из payload). Окна нарезаются по реальному времени события, поэтому результат не зависит от того, когда и в каком порядке события дошли до приложения.
- **Processing time** — wall-clock, текущее время обработки. Проще, но результат становится недетерминированным: при задержке или повторной обработке события попадут в другие окна.

По умолчанию Kafka Streams работает в **event time** — берёт timestamp из Kafka record. Чтобы перейти на processing time, подменяют timestamp extractor на `WallclockTimestampExtractor`:

```java
Properties props = new Properties();
props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG,
    WallclockTimestampExtractor.class); // processing time
```

## Q21. Зачем нужен `TimestampExtractor`?

`TimestampExtractor` — интерфейс, который определяет, **откуда Kafka Streams берёт timestamp каждой записи** для оконных операций. По умолчанию используется timestamp самой Kafka-записи, но его можно переопределить своей реализацией.

```java
public class CustomExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        Order order = (Order) record.value();
        return order.eventTime; // из payload, не из Kafka
    }
}
```

**Сценарий применения:** кастомный extractor нужен, когда настоящий event time лежит внутри payload (поле `eventTime` в объекте), а не в метаданных Kafka-записи. Так окна нарезаются по бизнес-времени события, а не по времени его публикации в Kafka.

## Q22. (!) Exactly-once семантика в Kafka Streams?

Exactly-once гарантирует, что каждое событие повлияет на результат **ровно один раз**, даже при сбоях и повторной обработке — без дублей и без потерь. В Kafka Streams (с 0.11+) это достигается за счёт **транзакций Kafka**: чтение offset-а, обновление state и запись в выходной топик происходят атомарно — либо всё фиксируется вместе, либо ничего.

```java
props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
```

**Почему гарантия именно сквозная (end-to-end):**
- Цепочка Kafka source → обработка → Kafka sink целиком exactly-once: при откате транзакции откатываются и записи в выходной топик, и сдвиг offset-а.
- Гарантия распространяется и на state stores — обновления changelog тоже входят в транзакцию, поэтому состояние не разъезжается с выходными данными.

**Компромисс:** транзакции стоят примерно 5–15% throughput. Альтернатива — `at_least_once` (режим по умолчанию): быстрее, но при повторной обработке возможны дубликаты, которые downstream должен уметь переварить (идемпотентность).

## Q23. Зачем нужны Standby replicas?

Standby-реплики ускоряют восстановление после сбоя. Проблема, которую они решают: без них новый владелец партиции при failover должен заново вычитать весь changelog, и при большом state это занимает минуты простоя.

```java
props.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1);
```

С включёнными standby другой инстанс заранее держит **горячую копию** state store, постоянно подтягивая changelog в фоне. Поэтому при падении основного владельца takeover происходит почти мгновенно — читать changelog с нуля не нужно.

**Компромисс:** выше доступность и быстрее failover, но больше потребляемых ресурсов (диск и трафик на поддержание копий).

## Q24. (!) Что происходит при сбое instance?

При падении инстанса срабатывает тот же механизм, что и у обычных Kafka-консьюмеров, плюс восстановление состояния:

1. Kafka **обнаруживает** мёртвого консьюмера — он перестаёт слать heartbeat, и срабатывает session timeout.
2. **Rebalance** — партиции упавшего инстанса перераспределяются между живыми.
3. Новый владелец партиции **поднимает state**, перечитывая changelog-топик этого store.
4. Восстановив состояние, инстанс **продолжает обработку** с того места, где остановился предыдущий.

**Главный фактор времени восстановления — размер state:** чем больше состояние, тем дольше проигрывается changelog. Именно поэтому существуют standby replicas (Q23) — они держат состояние уже готовым и сводят простой к минимуму.

## Q25. (!) Что такое ksqlDB?

`ksqlDB` — это SQL-слой поверх Kafka Streams: вы описываете потоковую обработку **декларативными SQL-запросами**, а ksqlDB транслирует их в топологию Kafka Streams и выполняет. Java-код писать не нужно — то же самое, что вы делали бы DSL-операциями, выражается через `CREATE STREAM`/`CREATE TABLE` и `SELECT`.

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

**ksqlDB Server** — это процесс, который принимает и выполняет ksql-запросы. Под капотом каждый такой запрос превращается в обычное Kafka Streams приложение, поэтому ksqlDB наследует все его свойства (state stores, exactly-once, масштабирование через партиции).

**Сценарии применения:** простые трансформации и агрегации без написания Java, BI и аналитика поверх потоков, low-code stream processing для тех, кто знает SQL, но не хочет погружаться в DSL.

## Q26. (!) Kafka Streams vs Flink — когда что?

Оба инструмента решают stateful stream processing, но в разных весовых категориях. Грубое правило: Kafka Streams выбирают за **простоту эксплуатации**, когда вся обработка крутится вокруг Kafka; Flink — за **мощность** (низкая latency, богатый state, любые источники), когда сложность кластера оправдана.

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

Реальные примеры использования (полезно назвать пару на собеседовании):

- **Confluent (создатели)** — внутренние сервисы.
- **LinkedIn** — множество сценариев (Kafka родом именно из LinkedIn).
- **Uber** — fraud detection, real-time pricing.
- **Pinterest, Airbnb, Slack** — event-driven сервисы.
- **Банки (Goldman Sachs, ING)** — оценка рисков, fraud.
- **Walmart** — управление supply chain.

Закономерность: Kafka Streams особенно прижился в **банках и финтехе**, и причина в сочетании двух свойств — простота развёртывания (обычный микросервис вместо отдельного кластера) и exactly-once для денежных операций, где дубли недопустимы.

## Q28. Какие минусы Kafka Streams?

Большинство ограничений — обратная сторона его главного достоинства: «всё через Kafka и без отдельного кластера».

1. **Только Kafka** — нельзя напрямую читать из других источников (БД, файлы); всё должно сначала попасть в топик.
2. **State привязан к локальному диску** — RocksDB растёт вместе с состоянием, за местом нужно следить.
3. **Rebalance при scale up/down** — при изменении числа инстансов обработка на время приостанавливается.
4. **Сложнее дебажить** — обработка распределена по инстансам, единого места с полной картиной нет.
5. **CEP беднее, чем у Flink** — сложный complex event processing выразить тяжелее.
6. **ksqlDB ограниченнее** Java/Scala API — не всё, что можно в DSL, доступно в SQL.
7. **По сути только JVM** — Java и Scala (через JVM API); Python-клиента нет.
8. **Менее зрел для очень больших масштабов** — на экстремальных нагрузках обычно выигрывает Flink.

**Вывод:** Kafka Streams — отличный выбор для **event-driven микросервисов** поверх Kafka, но не универсальный инструмент general-purpose stream processing на любые источники и нагрузки.

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
