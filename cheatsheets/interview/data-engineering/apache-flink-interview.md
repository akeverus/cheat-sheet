---
title: "Вопросы на собеседовании: Apache Flink"
description: "Apache Flink: streaming-first архитектура, event time vs processing time, watermarks, state, checkpoints, exactly-once, savepoints, KeyedStream, windows, CEP, Flink SQL, отличие от Spark Streaming"
tags:
  - interview
  - data-engineering
  - apache-flink-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Apache Flink"
  - "Apache Flink interview"
  - "Flink interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Apache Flink`

`Apache Flink` — distributed stream processing framework с **true streaming** архитектурой (не micro-batch как Spark). Создан в Berlin (TU Berlin), Apache top-level с 2014. Главные применения: **low-latency streaming** (миллисекунды), **stateful processing**, exactly-once гарантии, **CEP (complex event processing)**.

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
- [Q8. (!) Чем отличаются Event time, Processing time и Ingestion time?](#q8--чем-отличаются-event-time-processing-time-и-ingestion-time)
- [Q9. (!) Watermarks — как работают?](#q9--watermarks--как-работают)
- [Q10. (!) Late data — стратегии?](#q10--late-data--стратегии)

**Windows**
- [Q11. (!) Что такое Tumbling windows?](#q11--что-такое-tumbling-windows)
- [Q12. (!) Что такое Sliding windows?](#q12--что-такое-sliding-windows)
- [Q13. Что такое Session windows?](#q13-что-такое-session-windows)
- [Q14. Что такое Global windows?](#q14-что-такое-global-windows)

**State**
- [Q15. (!) Что такое state в Flink?](#q15--что-такое-state-в-flink)
- [Q16. (!) Чем Keyed state отличается от Operator state?](#q16--чем-keyed-state-отличается-от-operator-state)
- [Q17. State backends — какие?](#q17-state-backends--какие)
- [Q18. (!) RocksDB state backend — особенности?](#q18--rocksdb-state-backend--особенности)

**Fault tolerance**
- [Q19. (!) Checkpointing — как работает?](#q19--checkpointing--как-работает)
- [Q20. (!) Как работает семантика Exactly-once?](#q20--как-работает-семантика-exactly-once)
- [Q21. (!) Savepoints — отличие от checkpoints?](#q21--savepoints--отличие-от-checkpoints)
- [Q22. Barriers и алгоритм Chandy-Lamport?](#q22-barriers-и-алгоритм-chandy-lamport)

**APIs**
- [Q23. (!) Чем различаются DataStream, Table и SQL API?](#q23--чем-различаются-datastream-table-и-sql-api)
- [Q24. ProcessFunction — что это?](#q24-processfunction--что-это)
- [Q25. (!) Что такое CEP (Complex Event Processing)?](#q25--что-такое-cep-complex-event-processing)

**Производительность**
- [Q26. (!) Backpressure в Flink?](#q26--backpressure-в-flink)
- [Q27. Parallelism — как настраивать?](#q27-parallelism--как-настраивать)
- [Q28. Network buffering и chains?](#q28-network-buffering-и-chains)

**Применения**
- [Q29. (!) Когда выбирать Flink вместо Spark Streaming?](#q29--когда-выбирать-flink-вместо-spark-streaming)
- [Q30. (!) Где Flink в production?](#q30--где-flink-в-production)
- [Q31. Какие минусы Flink?](#q31-какие-минусы-flink)

## Q1. (!) Что такое Apache Flink?

`Apache Flink` — распределённый движок потоковой обработки данных (distributed stream processing engine). Создан в **TU Berlin** (2010), стал Apache top-level project в 2014. Написан на **Scala/Java**, работает на JVM.

**Главная идея:** Flink — это **настоящий streaming** (true streaming). Каждое событие обрабатывается сразу, как только пришло, а не накапливается в пачку. Это и отличает его от Spark, где поток режется на микро-батчи. Отсюда задержки в миллисекунды и естественная stateful-обработка: оператор помнит, что видел раньше, и Flink сам сохраняет это состояние через checkpoints.

**Что Flink даёт из коробки:**
- обработку по **event time** с watermarks (корректная аналитика даже при неупорядоченных и опоздавших событиях);
- **exactly-once**-гарантии end-to-end;
- богатое управление состоянием (keyed/operator state, RocksDB для терабайтных объёмов).

**Где применяют:**
- Аналитика в реальном времени (clickstream, IoT, финансовый трейдинг)
- Обнаружение мошенничества (fraud detection)
- Рекомендации в реальном времени
- Потоковый ETL (Kafka → БД / Data Lake)
- Обработка сложных событий (CEP)

## Q2. (!) Чем Flink отличается от Spark Streaming?

Коренное различие — **модель обработки**. Flink обрабатывает каждое событие по отдельности и непрерывно (true streaming), а Spark Structured Streaming копит события в короткие микро-батчи и запускает batch-задачу для каждого. Из этого вытекает почти всё остальное: у Flink ниже задержка и нативная stateful/event-time-семантика; у Spark — единый стек batch + streaming и более зрелый SQL.

| Критерий | Flink | Spark Structured Streaming |
|----------|-------|---------------------------|
| Модель | Настоящий streaming | Micro-batch (continuous mode экспериментальный) |
| Задержка | < 100 мс (часто < 10 мс) | 100 мс — секунды |
| Пропускная способность | Высокая | Высокая |
| Семантика времени | Event time — гражданин первого класса | Event time через watermark |
| Управление состоянием | Богатое (keyed/operator) | Stateful-операции есть, но проще |
| Exactly-once | Строго (Chandy-Lamport) | Строго (через WAL) |
| Batch | Да (батч = частный случай streaming) | Да (batch + streaming) |
| Типы окон | Tumbling, sliding, session, global | Tumbling, sliding, session |
| Итеративные алгоритмы | Нативно (для ML/графов) | Через DataFrame-цикл |

**Итог:** **Spark** выигрывает, когда нужен один стек для batch + streaming. **Flink** — когда важен **чистый low-latency streaming** с большим состоянием и строгим event time.

## Q3. (!) Архитектура Flink — JobManager, TaskManager?

Топология кластера по уровням:

- `Client` отправляет job в `JobManager` (координация);
- `JobManager` управляет несколькими TaskManager'ами — например, `TaskManager 1`, `TaskManager 2`, `TaskManager 3`, у каждого по 4 слота (slots: 4);
- внутри TaskManager'ов слоты исполняют конкретные задачи (Task): на `TaskManager 1` идут две задачи, на `TaskManager 2` — одна, и так далее.

Flink — классическая master-worker-система. **JobManager** дирижирует, **TaskManager** считают.

**JobManager** (master, координатор):
- принимает jobs от клиентов и строит план выполнения;
- распределяет задачи по слотам и следит за их жизненным циклом;
- управляет checkpointing (вставляет barrier, фиксирует завершённые снимки);
- единая точка отказа — в production включают HA (несколько JobManager, лидер выбирается через ZooKeeper/Kubernetes).

**TaskManager** (worker, исполнитель):
- запускает реальные подзадачи (subtasks) и обменивается данными с соседями;
- делит свои ресурсы на **task slots** (по умолчанию число слотов = числу ядер);
- каждый slot держит одну параллельную subtask каждого оператора.

**Slot** — единица параллелизма с фиксированной долей памяти/CPU. Параллелизм job ограничен суммарным числом слотов в кластере: чтобы запустить job с parallelism = 12, нужно как минимум 12 слотов.

## Q4. Deployment режимы (Standalone, YARN, K8s)?

Flink сам по себе не управляет ресурсами — он работает поверх менеджера кластера, который выделяет ему JobManager и TaskManager. Выбор режима определяется тем, что уже есть в инфраструктуре.

| Режим | Когда выбирают |
|-------|----------|
| **Standalone** | Встроенный менеджер кластера — проще всего поднять, но без авто-выделения ресурсов |
| **YARN** | Уже есть экосистема Hadoop — Flink делит ресурсы с другими YARN-приложениями |
| **Kubernetes** | Cloud-native подход — растущий стандарт, авто-масштабирование и self-healing |
| **Mesos** | Apache Mesos (устарел, поддержка свёрнута) |
| **Local** | Один процесс на машине разработчика — для тестов и отладки |

Современный выбор по умолчанию — **Kubernetes**: с Flink 1.12+ есть полноценный **Flink Kubernetes Operator**, который описывает job как Kubernetes-ресурс и сам управляет деплоем, апгрейдами через savepoint и восстановлением.

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

`DataStream API` — основной императивный API Flink. Программа описывается как **граф трансформаций**: каждый оператор берёт `DataStream` и возвращает новый. Выполнение **ленивое** — реальный запуск происходит только на `env.execute()`.

**Группы операций:**
- `map`, `filter`, `flatMap` — поэлементные трансформации (без перетасовки данных);
- `keyBy` — партиционирование по ключу: разводит события по subtask так, чтобы одинаковые ключи попадали в один и обязательно перед stateful-операциями и окнами;
- `reduce`, `sum`, `min`, `max` — агрегации (применяются на `KeyedStream`);
- `window` — нарезка потока на окна для агрегаций по времени/количеству;
- `join`, `coGroup` — соединения двух потоков по ключу в окне;
- `connect`, `union` — комбинирование потоков (`union` — однотипные, `connect` — разнотипные с общим состоянием).

## Q6. (!) KeyedStream — что это?

`KeyedStream` — это `DataStream`, логически разбитый по **ключу** через `keyBy`. Все события с одинаковым ключом гарантированно идут в одну subtask и обрабатываются ею **последовательно**. По сути это потоковый аналог `GROUP BY`: данные перетасовываются (shuffle) так, чтобы один ключ всегда оказывался у одного исполнителя.

```scala
val keyed: KeyedStream[Event, String] = stream.keyBy(_.userId)

// Stateful operation per key
keyed.process(new MyKeyedProcessFunction())
```

**Зачем это нужно:**
- **Изолированное состояние на ключ** — Flink хранит отдельный keyed state для каждого ключа (счётчик на пользователя, последняя цена по инструменту), и оператор автоматически видит состояние именно текущего ключа;
- **Гарантия порядка** в пределах ключа — события одного пользователя не «обгоняют» друг друга;
- **Параллелизация** — разные ключи распределены по разным subtasks, поэтому нагрузка масштабируется горизонтально.

`KeyedStream` — обязательная основа для **keyed state**, **окон** и **таймеров**: всё это работает «per key».

**Подводный камень:** перекос ключей (key skew). Если по одному ключу проходит непропорционально много событий, его subtask становится узким местом, а остальные простаивают.

## Q7. Sources и Sinks?

Source и sink — это границы pipeline: **source** читает данные снаружи и подаёт их в поток, **sink** записывает результат во внешнюю систему. Именно от их свойств зависят гарантии доставки: source должен уметь перечитать данные после сбоя (replay), а sink — записывать транзакционно или идемпотентно, иначе end-to-end exactly-once не получить.

**Sources** — откуда данные:
- Apache Kafka (`flink-connector-kafka`) — основной источник, поддерживает replay по offset
- Файлы (FileSource)
- Сокеты (для тестов)
- Собственные (custom)

**Sinks** — куда:
- Kafka
- JDBC-базы данных
- Elasticsearch
- Файловые системы (S3, HDFS, Iceberg, Delta Lake)
- Собственные (custom)

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

## Q8. (!) Чем отличаются Event time, Processing time и Ingestion time?

Это три разных «часов», по которым Flink может определять время события. Различие важно потому, что данные приходят с задержкой и не по порядку: одно и то же событие имеет разные временные метки на этих трёх шкалах.

| Time | Что значит |
|------|------------|
| **Event time** | Когда событие **произошло** в реальности (timestamp в самих данных) |
| **Processing time** | Когда событие **обрабатывается** оператором в Flink (системные часы воркера) |
| **Ingestion time** | Когда событие **поступило** в Flink (метка ставится на source) |

**Пример:** clickstream
```
event_time = 14:00 (клик)
ingestion_time = 14:05 (Kafka получил)
processing_time = 14:07 (Flink обрабатывает)
```

**Что выбрать:**
- **Event time** — единственный корректный выбор для аналитики. Результат детерминирован и воспроизводим: пересчёт того же исторического потока даст тот же ответ, потому что окна привязаны к меткам в данных, а не к моменту обработки. Цена — нужны watermarks, чтобы понимать, когда окно можно закрывать.
- **Processing time** — для low-latency, когда точность не критична. Быстро и просто, но результат недетерминирован: зависит от скорости обработки и сетевых задержек.
- **Ingestion time** — компромисс: стабильнее processing time, но не отражает реального времени события.

```scala
WatermarkStrategy.forBoundedOutOfOrderness[Event](Duration.ofSeconds(20))
  .withTimestampAssigner((event, _) => event.timestamp)
```

## Q9. (!) Watermarks — как работают?

**Watermark** — это движущаяся метка времени в потоке, означающая утверждение: «событий с event_time меньше этой метки больше не будет». Watermark решает фундаментальную проблему event time: события приходят неупорядоченно и с опозданием, поэтому Flink нужен сигнал, когда уже можно считать окно завершённым и выдать результат. Watermark — это и есть оценка прогресса по времени.

```
events:    e1(t=10) e2(t=12) e3(t=11) e4(t=15) e5(t=13)
                                                      ↓
                                             watermark = 13 (событий с t<=13 больше не будет)
```

**Зачем нужны watermarks:**
- **Закрытие окон** — как только watermark проходит конец окна, окно считается полным и выдаёт результат;
- **Определение опоздавших данных** — события с timestamp < текущего watermark считаются late;
- **Срабатывание event-time таймеров** и продвижение времени в `ProcessFunction`.

**Компромисс:** watermark задаёт допуск на неупорядоченность. `forBoundedOutOfOrderness(5s)` ставит watermark на 5 секунд позади максимального увиденного timestamp — это значит, что Flink ждёт опоздавшие события до 5 секунд, прежде чем закрыть окно. Больший допуск — меньше потерянных данных, но выше задержка результата; меньший — наоборот.

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

**Late data** — события, чей event_time меньше текущего watermark, то есть пришедшие после того, как Flink уже «закрыл» их период времени. По умолчанию они теряются, и это частая причина расхождений в данных. Flink предлагает три стратегии — от самой простой к самой надёжной:

- **Отбрасывание** (по умолчанию) — поздние события молча игнорируются. Быстро, но данные теряются;
- **`allowedLateness(N)`** — окно после закрытия держится ещё N времени и при каждом опоздавшем событии пересчитывает и переотправляет результат. Downstream должен уметь обрабатывать обновления (upsert);
- **`sideOutputLateData`** — совсем поздние события не теряются, а уходят в отдельный поток (side output), где их можно записать в «корзину» или обработать иначе.

**Компромисс:** чем больше `allowedLateness`, тем дольше Flink держит окно в состоянии — растёт потребление памяти state backend.

## Q11. (!) Что такое Tumbling windows?

**Tumbling window** (кувыркающееся окно) — окна фиксированной длины, идущие встык и **не перекрывающиеся**. Самый частый тип окна: каждое событие попадает ровно в одно окно, окна не делят данные между собой.

```
| 0-5 | 5-10 | 10-15 | 15-20 |
```

```scala
keyedStream
  .window(TumblingEventTimeWindows.of(Time.minutes(5)))
  .sum(1)
```

Поскольку окна не перекрываются, каждое событие учитывается **ровно один раз** — состояние компактное, агрегаты не дублируются.

**Сценарий применения:** периодическая статистика без перекрытия — счётчики за минуту/час/день, биллинг по часам, метрики дашборда.


## Q12. (!) Что такое Sliding windows?

**Sliding window** (скользящее окно) — окна фиксированной длины, которые перекрываются и сдвигаются с заданным шагом (slide). В отличие от tumbling, новое окно открывается не когда закрылось предыдущее, а каждые `slide` единиц времени:

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

Поскольку окна перекрываются, **одно событие попадает сразу в несколько окон** (примерно `длина / slide` штук). Это даёт плавный, часто обновляемый результат, но и расход состояния выше — событие хранится в каждом окне, куда попало.

**Сценарий применения:** скользящие средние и непрерывный мониторинг — например, «среднее число запросов за последние 5 минут, обновляемое каждую минуту».

## Q13. Что такое Session windows?

**Session window** — окно с **динамической** длиной: оно группирует события, идущие плотно друг за другом, и закрывается, когда между событиями возникает пауза дольше заданного **gap** (период неактивности). В отличие от tumbling/sliding, границы окна не фиксированы заранее — они «прорастают» из самих данных под каждый ключ.

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

**Сценарий применения:** пользовательские сессии в аналитике (визит на сайте, серия действий между паузами), определение последовательностей активности.

## Q14. Что такое Global windows?

**Global window** складывает все события одного ключа в **одно бесконечное окно**, которое само по себе никогда не закрывается. Поэтому к нему обязателен **собственный триггер (custom trigger)** — иначе результат не будет выдан никогда. Времени тут нет: момент выдачи определяет триггер, например по количеству накопленных событий.

```scala
keyedStream
  .window(GlobalWindows.create())
  .trigger(CountTrigger.of(100)) // emit когда 100 событий
  .reduce(...)
```

**Сценарий применения:** используется редко — когда окна нужно нарезать не по времени, а по количеству событий или собственному условию (count-based триггеры, нестандартная логика).

## Q15. (!) Что такое state в Flink?

**State** — это данные, которые оператор запоминает между событиями. Stateless-операция (`map`, `filter`) смотрит только на текущее событие; stateful-операция помнит контекст: сколько раз видела пользователя, какова была предыдущая цена, что накопилось в окне. Flink — **stateful streaming-движок**, и именно управление состоянием — его главная сила.

**Примеры состояния:**
- счётчик на пользователя (ключ);
- оконные агрегации (накопленная сумма за окно);
- последнее увиденное значение для сравнения с текущим;
- модель машинного обучения в памяти оператора.

Ключевая особенность: **состояние персистентно**. Flink периодически снимает с него snapshot в checkpoints, поэтому после сбоя или рестарта job продолжает с того же места, не теряя накопленных данных. Это то, что отличает Flink-state от обычной переменной в памяти.

## Q16. (!) Чем Keyed state отличается от Operator state?

Flink различает два вида состояния по тому, к чему оно привязано. Это важно для масштабирования: при изменении параллелизма Flink по-разному перераспределяет эти два типа.

| Тип | Привязан к | Использование |
|-----|------------|---------------|
| **Keyed state** | Ключу (на `KeyedStream`) | Агрегации на пользователя, сессии |
| **Operator state** | Параллельной subtask целиком | Состояние коннектора (Kafka offsets) |

**Keyed state** — самый частый: доступен только после `keyBy`, своё значение на каждый ключ, оператор автоматически видит состояние текущего ключа. При перемасштабировании Flink перераспределяет ключи по subtask через key groups.

**Operator state** — привязан к экземпляру оператора, а не к ключу; типичный пример — Kafka-source, хранящий offset'ы своих партиций. При изменении параллелизма перераспределяется по subtask (round-robin / union redistribution).

**Типы keyed state:**
- `ValueState[T]` — одно значение на ключ;
- `ListState[T]` — список значений;
- `MapState[K, V]` — словарь (можно читать/писать по ключу, не загружая всё целиком);
- `ReducingState[T]` — агрегат, сворачиваемый reduce-функцией на лету;
- `AggregatingState[I, O]` — агрегат с разными типами входа и выхода.

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

**State backend** определяет, **где** живёт состояние во время работы job и как оно сериализуется в checkpoint. Это ключевой выбор: он напрямую задаёт компромисс между скоростью доступа и максимальным объёмом состояния.

| Backend | Где живёт состояние | Скорость доступа | Предел размера |
|---------|----------------|----------|--------|
| **HashMapStateBackend** | JVM heap (объекты в памяти) | Очень быстро (прямой доступ к объектам) | Ограничен heap |
| **EmbeddedRocksDBStateBackend** | RocksDB на локальном диске | Медленнее (сериализация/десериализация на каждое чтение) | Огромный — терабайты |

```scala
env.setStateBackend(new EmbeddedRocksDBStateBackend())
env.getCheckpointConfig.setCheckpointStorage("s3://bucket/checkpoints")
```

**Как выбирать:**
- **HashMap** — небольшое состояние (< 1 ГБ), которое умещается в heap; самая низкая задержка, но при росте состояния упирается в память и риск OOM;
- **RocksDB** — огромное состояние (десятки ГБ — терабайты); поддерживает **инкрементальные checkpoints** (сохраняются только изменения), но каждое обращение к state проходит через (де)сериализацию, поэтому медленнее.

Обратите внимание: **место хранения checkpoint** (S3, HDFS) настраивается отдельно через `setCheckpointStorage` и не зависит от выбора backend.

## Q18. (!) RocksDB state backend — особенности?

**RocksDB** — встраиваемое key-value-хранилище на LSM-tree от Facebook. В Flink оно встраивается прямо в TaskManager и хранит состояние на локальном диске, а не в JVM heap. Это снимает главное ограничение HashMap-backend: состояние больше не упирается в память.

**Плюсы:**
- состояние **не ограничено RAM** — хранится на диске, до терабайтов на job;
- **инкрементальные checkpoints** — в S3/HDFS уходят только изменившиеся SST-файлы, а не весь снимок; критично для большого состояния;
- состояние вне JVM heap → меньше нагрузка на garbage collector, стабильнее latency.

**Минусы:**
- каждое чтение/запись проходит через **сериализацию/десериализацию** — заметно медленнее прямого доступа к объектам в HashMap;
- **тюнинг RocksDB сложен** — BlockCache, write buffers, уровни compaction; неправильные настройки бьют по производительности.

**Эмпирическое правило:** в production для streaming с большим или растущим состоянием выбирают почти всегда RocksDB — стабильность и масштаб важнее, чем выигрыш HashMap в задержке.

## Q19. (!) Checkpointing — как работает?

**Checkpoint** — это согласованный snapshot состояния всех операторов на один и тот же логический момент потока. Это механизм отказоустойчивости Flink: имея checkpoint, после сбоя можно откатить всю job к этому моменту и продолжить с него, не теряя и не дублируя данные. Снимаются checkpoints автоматически и периодически.

```scala
env.enableCheckpointing(60000) // каждые 60 секунд
env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)
env.getCheckpointConfig.setCheckpointTimeout(600000)
```

**Алгоритм (вариант Chandy-Lamport):**
1. JobManager вставляет в источники специальный маркер — **barrier** — и пускает его по потоку вместе с данными;
2. barrier проходит через операторы, разделяя поток на «до» и «после» снимка;
3. когда оператор получил barrier со **всех** своих входов, он снимает snapshot своего состояния (alignment гарантирует согласованность);
4. snapshot асинхронно пишется в надёжное хранилище (S3, HDFS);
5. когда все операторы подтвердили снимок, JobManager помечает checkpoint завершённым.

Ключевое: barrier движется вместе с данными, поэтому снимок **не останавливает обработку** — pipeline продолжает течь. При **сбое** Flink перезапускает job, восстанавливает состояние из последнего успешного checkpoint и перематывает источники (Kafka offset) на соответствующую позицию.

## Q20. (!) Как работает семантика Exactly-once?

**Exactly-once** означает, что каждое событие влияет на результат ровно один раз — без потерь и без двойного учёта. Важное уточнение: при сбое Flink может **физически перечитать** событие повторно, но за счёт отката состояния к checkpoint и перемотки источника **эффект** остаётся однократным. То есть гарантия даётся на уровне согласованности состояния, а не «событие никогда не читается дважды».

**Условия, без которых exactly-once невозможен:**
- **Source** должен поддерживать повторное чтение (replay) с заданной позиции — Kafka, Kinesis (по offset);
- **Sink** должен быть либо **транзакционным**, либо **идемпотентным** — иначе при перезаписи после отката во внешней системе появятся дубли.

**Сквозной (end-to-end) exactly-once** — самое сложное, потому что требует согласовать checkpoint Flink с фиксацией во внешней системе:
- **Kafka → Flink → Kafka** — через транзакции Kafka: sink публикует записи в транзакции и коммитит её только когда checkpoint завершён (two-phase commit);
- **Flink → JDBC** — через 2PC-sink: строго, но медленно из-за двухфазного коммита.

**Компромисс:** **at-least-once** проще и быстрее (нет alignment, нет транзакций), но при сбое возможны дубли. Выбирают, когда downstream идемпотентен или дубли допустимы.

## Q21. (!) Savepoints — отличие от checkpoints?

Технически и checkpoint, и savepoint — это снимки состояния job. Разница в **назначении и владельце**: checkpoint Flink делает сам для автоматического восстановления после сбоя, а savepoint создаёт оператор вручную для управляемых операций — апгрейда кода, миграции, A/B. Грубо: checkpoint — «страховка от падения», savepoint — «пауза для осмысленного действия».

| Критерий | Checkpoint | Savepoint |
|----------|------------|-----------|
| Цель | Отказоустойчивость | Эксплуатация (upgrade, A/B) |
| Создание | Автоматически | Вручную (`flink savepoint`) |
| Удаление | Автоматически (по retention) | Вручную (живёт, пока не удалят) |
| Формат | Оптимизированный под скорость | Стандартный, переносимый (можно восстановить после рефакторинга кода) |

```bash
flink savepoint <job-id> s3://bucket/savepoints/
flink run -s s3://bucket/savepoints/savepoint-... newapp.jar
```

**Сценарии применения savepoint:**
- **выкатка новой версии** Flink-приложения без потери состояния (остановить по savepoint → запустить новый код из него);
- **миграция схемы состояния** при изменении модели данных;
- **A/B-тестирование** и масштабирование (старт с другим parallelism).

## Q22. Barriers и алгоритм Chandy-Lamport?

**Barrier** — специальный маркер, который JobManager вставляет в поток данных для координации checkpoint. Barrier течёт по pipeline вместе с обычными событиями и проводит «линию отреза»: всё, что прошло до него, входит в текущий снимок, всё после — уже в следующий. За счёт этого Flink снимает согласованный снимок распределённого состояния, **не останавливая** поток.

Как barrier течёт по pipeline:

- из `Source` поток идёт в `Operator 1` с содержимым `e1, e2, BARRIER, e3, e4` — barrier стоит между событиями `e2` и `e3`;
- из `Operator 1` в `Operator 2` уходит уже `e1, e2, BARRIER` — события до barrier'а вместе с самим маркером;
- из `Operator 2` поток идёт в `Sink`.

Когда оператор получил barrier со **всех** входных потоков, он пишет snapshot своего состояния. Это и есть **алгоритм Chandy-Lamport** для распределённых снимков (1985), адаптированный Flink под потоковую обработку.

**Alignment (выравнивание):** при exactly-once оператор, получив barrier по одному входу, приостанавливает этот вход и ждёт barrier по остальным. Так гарантируется, что снимок согласован — но если один вход медленный, ожидание создаёт **backpressure** и удлиняет checkpoint.

**Unaligned checkpoints (Flink 1.11+):** альтернатива для систем под нагрузкой — barrier «обгоняет» данные в буферах, а они сохраняются как часть снимка. Checkpoint завершается быстрее даже при backpressure ценой большего объёма снимка.

## Q23. (!) Чем различаются DataStream, Table и SQL API?

Flink даёт три уровня абстракции над одним и тем же движком — от низкоуровневого императивного контроля до декларативного SQL. Выбор — это компромисс между гибкостью и простотой: чем выше уровень, тем меньше кода, но и меньше контроля над состоянием и таймерами.

| API | Стиль | Аудитория |
|-----|-------|----------|
| **DataStream** | Императивный, Scala/Java; полный контроль над state и таймерами | Разработчики |
| **Table** | Декларативный, типобезопасный; реляционные операции на API | Гибридная |
| **SQL** | Стандартный ANSI SQL | Аналитики, BI |

```scala
// DataStream
stream.keyBy(_.userId).window(...).reduce(...)

// Table API
table.groupBy($"userId").select($"userId", $"amount".sum())

// SQL
tableEnv.sqlQuery("SELECT userId, SUM(amount) FROM events GROUP BY userId")
```

Важно: у всех трёх **одинаковая производительность** — Table/SQL компилируются в тот же DataStream-runtime через единый оптимизатор. Поэтому SQL не «медленнее» — выбор между API диктуется удобством, а не скоростью.

**Эмпирическое правило:** берите SQL/Table для стандартной аналитики (фильтры, join, агрегаты), а DataStream — когда нужны тонкая работа с состоянием, таймеры или нестандартная логика (`ProcessFunction`, CEP). С Flink 1.13+ Table API + SQL активно развиваются и покрывают всё больше аналитических сценариев.

## Q24. ProcessFunction — что это?

`ProcessFunction` — самый **низкоуровневый** строительный блок DataStream API. Он даёт прямой доступ к трём вещам сразу: каждому событию, **состоянию** (keyed state) и **таймерам** (event-time и processing-time). Именно сочетание состояния и таймеров делает его мощнее обычных операторов: можно реализовать логику, которую нельзя выразить через `map`/`window` — например, «если за 60 секунд не пришло подтверждение, отправить алерт».

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

**Сценарий применения:** кастомная логика, которую не выразить стандартными операторами — таймауты и тайм-ауты ожидания, собственные правила CEP, fraud detection, машины состояний на потоке.

## Q25. (!) Что такое CEP (Complex Event Processing)?

`Flink CEP` — библиотека для поиска **сложных паттернов** в потоке: не отдельных событий, а их **последовательностей** во времени. Вместо того чтобы вручную писать машину состояний в `ProcessFunction`, паттерн описывается декларативно — «событие A, затем в течение N минут событие B, затем C» — а CEP сам отслеживает совпадения и выдаёт результат.

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

**Сценарии применения:**
- **обнаружение мошенничества** — характерная последовательность подозрительных действий за короткое время;
- **торговые сигналы** — паттерны рыночных событий;
- **мониторинг систем** — алерты по сложным условиям (несколько ошибок подряд за окно);
- **анализ clickstream** — типовые пользовательские последовательности.

**Компромисс:** CEP сложнее в освоении и в эксплуатации, чем простые агрегации (паттерны хранят промежуточные совпадения в состоянии), но даёт выразительность, которой иначе пришлось бы добиваться большим объёмом ручного кода.

## Q26. (!) Backpressure в Flink?

**Backpressure** (обратное давление) — это естественный механизм саморегуляции: когда downstream-оператор не успевает обрабатывать данные, он автоматически замедляет upstream, и торможение распространяется вверх по pipeline вплоть до источника. Работает это через network buffers: получатель не освобождает буферы, пока не обработает данные, поэтому отправитель не может в них писать и вынужден притормозить.

Когда `Op2` не успевает — буферы перед ним заполняются → `Op1` не может писать → замедляется → давление доходит до источника, и тот замедляет чтение из Kafka.

Главное: backpressure — это **не ошибка, а защита**. Flink **не теряет данные** — система просто работает на скорости самого медленного звена. Но устойчивый высокий backpressure означает узкое место, которое надо устранять, иначе растёт лаг.

**Как обнаружить:** Flink Web UI → вкладка Backpressure (или метрики). Красный/высокий статус указывает на оператор-узкое-место.

**Как лечить:**
- увеличить **параллелизм** медленного оператора (раздать нагрузку на больше subtask);
- **оптимизировать его логику** (медленный sink, тяжёлый вызов, неэффективный доступ к state);
- добавить **ресурсов** (CPU, память) или ускорить внешнюю систему-приёмник.

## Q27. Parallelism — как настраивать?

```scala
env.setParallelism(8) // global default

stream
  .map(...)
  .setParallelism(4) // override для конкретного operator

env.setMaxParallelism(128) // verticale scaling
```

**Parallelism** — это число параллельных экземпляров (subtask) оператора. Задаётся на трёх уровнях с убыванием приоритета: на отдельный оператор → глобально для job → дефолт кластера. Более конкретный уровень переопределяет общий.

**Рекомендации по настройке:**
- начинайте с `parallelism = суммарное число ядер в кластере` — разумная отправная точка;
- у **источника** параллелизм обычно ограничен числом партиций Kafka-топика: больше subtask, чем партиций, не дадут выигрыша — лишние будут простаивать;
- у **операторов-узких мест** (выявленных по backpressure) — повышайте параллелизм точечно.

**Подводный камень:** `maxParallelism` задаёт верхний предел масштабирования и определяет число key groups. Его **нельзя изменить** после первого запуска без миграции через savepoint, поэтому закладывайте запас сразу.

## Q28. Network buffering и chains?

**Operator chaining** — оптимизация, при которой Flink склеивает несколько подряд идущих операторов в один task и выполняет их в одном потоке. Условие — между ними нет перетасовки данных (shuffle), то есть связь один-к-одному (`map`, `filter` подряд). Если же есть `keyBy` или смена параллелизма, цепочка рвётся, потому что данные нужно перераспределить по сети.

```
source → map → filter → keyBy → sum → sink
\__________ chain __________/  shuffle  \chain/
```

Почему это важно: внутри chain события передаются прямым вызовом метода в одном потоке — **без сериализации, копирования и сетевого обмена**. Отсюда заметный прирост производительности и снижение задержки, поэтому Flink включает chaining по умолчанию.

Иногда цепочку полезно разорвать вручную — например, чтобы изолировать «тяжёлый» оператор на отдельный поток или для отладки:
```scala
stream.map(...).disableChaining()
```

**Network buffers** — пул буферов для передачи данных между task там, где chaining невозможен (через сеть). Их объём настраивается через `taskmanager.network.memory.*` и напрямую связан с backpressure: именно заполнение этих буферов тормозит upstream.

## Q29. (!) Когда выбирать Flink вместо Spark Streaming?

Короткий критерий: **Flink — если streaming первичен**, а низкая задержка и работа с состоянием критичны; **Spark — если streaming это дополнение к batch** в едином стеке. Ниже разворот по ситуациям.

**Выбирай Flink когда:**
- Критична **задержка < 100 мс**
- **Stateful-обработка** с большим состоянием
- **Сквозной exactly-once** (Kafka → Flink → Kafka)
- **CEP** — сложные паттерны событий
- **Настоящий streaming** (не micro-batch)
- Уже есть команда с экспертизой по Java/Scala

**Выбирай Spark Streaming когда:**
- Один стек для batch + streaming
- Приемлема задержка > 1 секунды
- В компании уже используется Spark
- Команда знакома со Spark

## Q30. (!) Где Flink в production?

Flink выбирают там, где потоки огромны, а задержка и корректность критичны. Показательно, что многие кейсы — это именно low-latency stateful-обработка в реальном времени, где micro-batch проигрывает.

**Известные пользователи:**
- **Alibaba** — Singles' Day, аналитика в реальном времени (миллиарды событий/сек); Alibaba — один из главных контрибьюторов Flink (Blink);
- **Netflix** — рекомендательный движок, обнаружение мошенничества;
- **Uber** — динамическое ценообразование, оценка ETA;
- **Stripe** — обнаружение мошенничества;
- **Lyft, Pinterest, Twitter, Yelp**;
- **ING Bank, Comcast**.

**Закономерность:** особенно сильна позиция Flink в **финтехе** (fraud, трейдинг, банки) и крупных интернет-компаниях — там, где stateful streaming с строгими гарантиями приносит прямую бизнес-ценность.

## Q31. Какие минусы Flink?

Сила Flink — глубокая stateful streaming-модель — оборачивается её же главным минусом: чтобы пользоваться ей правильно, надо разобраться в большом числе концепций и грамотно эксплуатировать состояние. Минусы группируются вокруг этого.

**Сложность входа и эксплуатации:**
1. **более крутая кривая обучения** — нужно держать в голове состояние, watermarks, event time, checkpoints;
2. **операционная сложность** — настройка checkpointing/savepoints, миграция и эволюция схемы состояния, тюнинг RocksDB.

**Зрелость экосистемы (в сравнении со Spark):**
3. **меньше сообщество** и материалов;
4. **меньше готовых коннекторов** (хотя ключевые есть);
5. **SQL API менее зрелый**, чем Spark SQL;
6. **меньше возможностей для ML/AI**;
7. **PyFlink** заметно менее зрелый, чем PySpark — для Python-команд это барьер.

**Итог:** Flink — лидер для **чистого low-latency streaming**, но в data engineering в целом Spark всё ещё доминирует за счёт зрелости экосистемы и единого batch+streaming-стека.

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

- [Apache Spark](apache-spark-interview.md)
- [Data Lake и Lakehouse](data-lake-lakehouse-interview.md)
- [Data Warehousing](data-warehousing-interview.md)
- [dbt](dbt-interview.md)
- [Kafka Streams](kafka-streams-interview.md)
