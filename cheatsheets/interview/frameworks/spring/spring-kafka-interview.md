---
title: "Вопросы на собеседовании: Spring Kafka"
description: "Spring Kafka для работы с Apache Kafka: KafkaTemplate, @KafkaListener, Consumer Groups, DLT, транзакции, тестирование с EmbeddedKafka"
tags:
  - interview
  - spring
  - spring-kafka-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Kafka"
  - "Spring Kafka interview"
  - "Spring Kafka собеседование"
prerequisites:
  - "[[spring-kafka]]"
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring Kafka`

`Spring Kafka` — интеграция Apache Kafka в экосистему Spring. Предоставляет `KafkaTemplate` для отправки, `@KafkaListener` для потребления, поддержку транзакций и Dead Letter Topics. Часто задаётся в интервью рядом с основами Kafka.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Kafka Docs](https://docs.spring.io/spring-kafka/docs/current/reference/html/) — официальная документация
- [Baeldung: Spring Kafka](https://www.baeldung.com/spring-kafka) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Spring Kafka и ключевые абстракции?](#q1-что-такое-spring-kafka-и-ключевые-абстракции)
- [Q2. Как настроить KafkaTemplate и отправить сообщение?](#q2-как-настроить-kafkatemplate-и-отправить-сообщение)
- [Q3. Как написать Kafka Consumer с @KafkaListener?](#q3-как-написать-kafka-consumer-с-kafkalistener)
- [Q4. (!) Что такое Consumer Group?](#q4-что-такое-consumer-group)

**Обработка ошибок**
- [Q5. (!) Как обрабатывать ошибки в @KafkaListener?](#q5-как-обрабатывать-ошибки-в-kafkalistener)
- [Q6. Что такое Dead Letter Topic (DLT)?](#q6-что-такое-dead-letter-topic-dlt)

**Конфигурация и особенности**
- [Q7. Как работают транзакции в Spring Kafka?](#q7-как-работают-транзакции-в-spring-kafka)
- [Q8. Как настроить конкурентное чтение?](#q8-как-настроить-конкурентное-чтение)
- [Q9. Как управлять offset коммитами?](#q9-как-управлять-offset-коммитами)
- [Q10. Как работает сериализация/десериализация?](#q10-как-работает-сериализациядесериализация)

**Тестирование и специальные случаи**
- [Q11. Как тестировать Spring Kafka без реального брокера?](#q11-как-тестировать-spring-kafka-без-реального-брокера)
- [Q12. Что такое Kafka Streams в контексте Spring?](#q12-что-такое-kafka-streams-в-контексте-spring)
- [Q13. Какие стратегии обеспечения порядка сообщений?](#q13-какие-стратегии-обеспечения-порядка-сообщений)
- [Q14. Как настроить idempotent producer?](#q14-как-настроить-idempotent-producer)
- [Q15. Как работает Pause/Resume для @KafkaListener?](#q15-как-работает-pauseresume-для-kafkalistener)

## Q1. Что такое Spring Kafka и какие ключевые абстракции он предоставляет?

Spring Kafka — интеграция Apache Kafka в экосистему Spring. Ключевые абстракции:

- **`KafkaTemplate`** — отправка сообщений (Producer API).
- **`@KafkaListener`** — декларативная подписка на топик (Consumer API).
- **`KafkaListenerContainerFactory`** — конфигурация контейнера слушателей.
- **`ConsumerRecord` / `ProducerRecord`** — обёртки над сырым Kafka API.
- **`KafkaTransactionManager`** — управление транзакциями Kafka.


> [!mcq]
>
> **Вопрос:** Чем `Spring Kafka` отличается от прямого использования `kafka-clients`, и какая абстракция стоит в центре всей библиотеки?
>
> ---
>
> #### A) `Spring Kafka` — это тонкая обёртка над `kafka-clients` только для красивых аннотаций; runtime-поведение и threading модель идентичны нативному API — ❌ Неверно
>
> **Что на самом деле:** `Spring Kafka` приносит **собственную threading-модель** через `MessageListenerContainer` (poll-loop в выделенном потоке), интеграцию с `TransactionManager`, retry/DLT через `DefaultErrorHandler`, lifecycle бинов (`SmartLifecycle`), и Spring-style конфигурацию через `application.yml`. Это не косметика — без контейнера пришлось бы вручную писать `while(true) { consumer.poll(); }` в `@PostConstruct`.
>
> **Откуда путаница:** `KafkaTemplate.send()` действительно делегирует в `KafkaProducer.send()` — здесь обёртка тонкая. Но Consumer-сторона — это полноценный фреймворк с listener container.
>
> **Если бы это было правдой:** не было бы централизованной обработки ошибок (`DefaultErrorHandler`), не работали бы транзакции Kafka+JPA (`ChainedKafkaTransactionManager`), и `@KafkaListener` сводился бы к ручному `consumer.poll()`.
>
> ---
>
> #### B) Центральная абстракция — `ConsumerRecord<K, V>`; всё остальное (`KafkaTemplate`, `@KafkaListener`) — вспомогательное — ❌ Неверно
>
> **Что на самом деле:** `ConsumerRecord` — это **DTO**, обёртка над сырой записью из брокера (key, value, partition, offset, headers, timestamp). Это **данные**, а не абстракция инфраструктуры. Центральные абстракции — `KafkaTemplate` (producer), `MessageListenerContainer` (consumer runtime), `KafkaListenerContainerFactory` (фабрика контейнеров).
>
> **Откуда путаница:** `ConsumerRecord` часто фигурирует в сигнатурах `@KafkaListener` методов как параметр — отсюда ощущение «важной» абстракции.
>
> **Если бы это было правдой:** не было бы concurrent listeners, transactional support, retries — потому что DTO ничего из этого делать не умеет. Это просто структура данных.
>
> ---
>
> #### C) Spring Kafka работает поверх `Spring AMQP` и реализует тот же API только с Kafka-протоколом — ❌ Неверно
>
> **Что на самом деле:** `Spring AMQP` — это **отдельная** библиотека для RabbitMQ/AMQP-брокеров (`RabbitTemplate`, `@RabbitListener`). `Spring Kafka` не зависит от `Spring AMQP`. API похожи **по дизайну** (Template + Listener), но это **параллельные** проекты, не наследники.
>
> **Откуда путаница:** в `Spring Integration` есть общий слой `MessageChannel`, который абстрагирует и Kafka, и RabbitMQ. Но `Spring Kafka` сам по себе не использует AMQP внутри.
>
> **Если бы это было правдой:** Kafka transactions (через `transaction.id` и `idempotence`) не работали бы — AMQP не имеет такого протокола; producer-стороне пришлось бы эмулировать exactly-once через ack-механизм RabbitMQ.
>
> ---
>
> #### D) `Spring Kafka` поверх `kafka-clients` добавляет: `KafkaTemplate` (producer + transactions), `MessageListenerContainer` (poll-loop runtime), `@KafkaListener` (декларативная подписка), `DefaultErrorHandler` (retry/DLT), `KafkaTransactionManager`; всё это интегрируется с Spring DI/lifecycle — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Архитектура: `KafkaTemplate<K,V>` оборачивает `Producer<K,V>` и добавляет ProducerListener callbacks, transactional API (`executeInTransaction`), header-mapping, `@SendTo` поддержку. На consumer-стороне `MessageListenerContainer` запускает свой пул потоков, в каждом — `Consumer.poll()` в цикле, и диспатчит записи в `@KafkaListener` метод. Контейнер берёт на себя: commit offset (по AckMode), error handler invocation, rebalance listener, pause/resume, lifecycle (`start/stop` через `SmartLifecycle`).
>
> `KafkaListenerContainerFactory` (обычно `ConcurrentKafkaListenerContainerFactory`) — фабрика, которая по `@KafkaListener` параметрам строит контейнер с `ConsumerFactory`, `ErrorHandler`, `RecordFilterStrategy`, `MessageConverter`. Это точка расширения для всех настроек.
>
> **Пример:**
> ```java
> @Configuration
> @EnableKafka
> public class KafkaConfig {
>
>     @Bean
>     public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory(
>             ConsumerFactory<String, OrderEvent> cf,
>             DefaultErrorHandler errorHandler) {
>         var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderEvent>();
>         factory.setConsumerFactory(cf);
>         factory.setConcurrency(3);                                    // 3 consumer threads
>         factory.setCommonErrorHandler(errorHandler);                  // retry + DLT
>         factory.getContainerProperties().setAckMode(AckMode.MANUAL);
>         return factory;
>     }
> }
>
> @Component
> public class OrderConsumer {
>     @KafkaListener(topics = "orders", groupId = "order-service")
>     public void onOrder(OrderEvent event, Acknowledgment ack) {
>         processOrder(event);
>         ack.acknowledge();
>     }
> }
> ```
>
> **Когда применять:**
> - **Любой Spring Boot микросервис на Kafka** (LinkedIn, Confluent demos, Yandex Lavka, Wolt) — `kafka-clients` напрямую используют только в performance-critical библиотечных интеграциях.
> - **Транзакции через несколько брокеров/БД**: `ChainedKafkaTransactionManager` для outbox-pattern без EventualConsistency-гарантий.
> - **Multi-tenant consumer**: один Spring Boot инстанс читает 10+ топиков с разной конфигурацией через `@KafkaListener(containerFactory = "...")`.
>
> **Подводные камни:**
> - `@EnableKafka` обязателен — без него `@KafkaListener` будет проигнорирован, ошибки в логах не будет.
> - Версия `spring-kafka` тесно связана с версией `kafka-clients` — нельзя апгрейдить broker до новых semantics без апгрейда client.
> - `MessageListenerContainer.start()` блокирует поток до `partition assignment` — это влияет на startup time и health-check.
>
> **Связанные вопросы:** [[Q2]] — `KafkaTemplate` details; [[Q3]] — `@KafkaListener` варианты; [[Q8]] — `ConcurrentKafkaListenerContainerFactory`.

## Q2. Как настроить KafkaTemplate и отправить сообщение?

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

```java
@Service
@RequiredArgsConstructor
public class OrderProducer {
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrder(OrderEvent event) {
        // Синхронная отправка (ожидаем подтверждения)
        ListenableFuture<SendResult<String, OrderEvent>> future =
            kafkaTemplate.send("orders", event.orderId(), event);

        future.addCallback(
            result -> log.info("Sent offset: {}", result.getRecordMetadata().offset()),
            ex -> log.error("Failed to send: {}", event.orderId(), ex)
        );
    }

    // Асинхронная (Java 8+ CompletableFuture API)
    public CompletableFuture<SendResult<String, OrderEvent>> sendAsync(OrderEvent event) {
        return kafkaTemplate.send("orders", event.orderId(), event)
            .completable();
    }
}
```


> [!mcq]
>
> **Вопрос:** Что вернёт `kafkaTemplate.send("topic", key, value)` в Spring Kafka 3.x, и почему `.get()` сразу после `.send()` — типичный анти-паттерн в production?
>
> ---
>
> #### A) `send()` возвращает `void` — это fire-and-forget; для подтверждения нужен отдельный `ProducerListener` бин — ❌ Неверно
>
> **Что на самом деле:** `KafkaTemplate.send()` возвращает `CompletableFuture<SendResult<K, V>>` (в Spring Kafka 3.x; в 2.x был `ListenableFuture`). Future завершается когда broker отдаст ack (или ошибку). `ProducerListener` — это **дополнительный** хук, не замена future.
>
> **Откуда путаница:** в нативном `kafka-clients` `Producer.send(record, Callback)` принимает callback и возвращает `Future<RecordMetadata>` — оттуда впечатление «нужен callback».
>
> **Если бы это было правдой:** невозможно было бы написать `kafkaTemplate.send(...).thenAccept(result -> ...)` или `await` отправки в тестах. На практике это базовая идиома.
>
> ---
>
> #### B) `send()` возвращает `CompletableFuture<SendResult<K, V>>`, который **асинхронно** завершается при получении ack от брокера; вызов `.get()` сразу после `.send()` блокирует поток и нивелирует batching producer-а — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Под капотом `kafka-clients` Producer работает так: `send()` кладёт запись в in-memory **`RecordAccumulator`** (буфер по партициям), фоновый **`Sender` thread** периодически отправляет batches брокеру (`linger.ms`, `batch.size`). Future завершается только когда ack от broker получен (`acks=1` — leader, `acks=all` — все ISR).
>
> Если сразу после `send()` вызвать `.get()`, поток блокируется до round-trip к брокеру (~5-50ms) — приложение теряет throughput. Правильно: `.thenAccept(...)` для логирования, `.exceptionally(...)` для DLQ-публикации, либо `.thenCompose(...)` для chaining. В транзакции (`executeInTransaction`) `.get()` оправдан — нужна синхронность.
>
> Метрика для production: `kafka.producer.record.send.total`, `kafka.producer.record.error.total` (Micrometer); batching эффективность видна по `kafka.producer.batch.size.avg`.
>
> **Пример:**
> ```java
> @Service
> @RequiredArgsConstructor
> @Slf4j
> public class OrderProducer {
>     private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
>
>     public CompletableFuture<SendResult<String, OrderEvent>> sendOrder(OrderEvent event) {
>         return kafkaTemplate.send("orders", event.orderId(), event)
>             .whenComplete((result, ex) -> {
>                 if (ex != null) {
>                     log.error("Failed to send order={}", event.orderId(), ex);
>                     deadLetterService.persist(event, ex);
>                 } else {
>                     log.info("Sent partition={}, offset={}",
>                         result.getRecordMetadata().partition(),
>                         result.getRecordMetadata().offset());
>                 }
>             });
>     }
> }
> ```
>
> **Когда применять:**
> - **High-throughput pipelines** (Yandex Lavka ordering, Wolt courier dispatch) — `linger.ms=10, batch.size=32KB` даёт 10x throughput vs `.get()` после каждого send.
> - **Outbox pattern**: `.thenAccept()` для marking outbox-row как «sent».
> - **Saga-step**: возврат CompletableFuture позволяет встроить producer в reactive стек (`Mono.fromFuture(...)`).
>
> **Подводные камни:**
> - `producer.flush()` или `producer.close()` нужны на shutdown — иначе in-flight записи в RecordAccumulator теряются.
> - При `acks=0` future завершается сразу — но это **at-most-once**, broker ack не дожидается. Опасно для финансовых событий.
> - `transaction.id` + `enable.idempotence=true` меняют семантику: `.get()` бросает `ProducerFencedException` если другой producer перехватил `transactional.id`.
>
> **Связанные вопросы:** [[Q7]] — Kafka transactions, `executeInTransaction`; [[Q14]] — idempotent producer; [[Q13]] — ordering и partition key.
>
> ---
>
> #### C) `send()` возвращает `Mono<SendResult>` — это полностью reactive API, интегрированный с WebFlux — ❌ Неверно
>
> **Что на самом деле:** `KafkaTemplate.send()` возвращает `CompletableFuture` (JDK-тип), **не** `Mono`. Reactive вариант — это **другой** класс `ReactiveKafkaProducerTemplate` из `reactor-kafka`, и его API: `send(SenderRecord<K, V, T>)` → `Flux<SenderResult<T>>`.
>
> **Откуда путаница:** в Spring WebFlux вокруг почти всё `Mono`/`Flux`, но `KafkaTemplate` — наследие Spring MVC мира, остался на `CompletableFuture` для backward compatibility.
>
> **Если бы это было правдой:** не нужен был бы отдельный `reactor-kafka` artifact в зависимостях reactive-проектов. На практике для WebFlux добавляют `spring-kafka` + `reactor-kafka` отдельно.
>
> ---
>
> #### D) `send()` блокирует поток до ack от брокера — потому что Spring Kafka не поддерживает асинхронную отправку — ❌ Неверно
>
> **Что на самом деле:** `send()` **не блокирует** — он только кладёт запись в `RecordAccumulator` и возвращает future. Фактическая отправка происходит в `Sender` thread производителя. Блокировка возможна только если буфер переполнен (`max.block.ms`).
>
> **Откуда путаница:** при `linger.ms=0, batch.size=1` и `acks=all` отправка визуально кажется синхронной — низкий throughput. Но потоковая модель остаётся async.
>
> **Если бы это было правдой:** producer не мог бы достигать миллионов сообщений в секунду — каждый send занимал бы network round-trip. На практике один producer thread выдаёт сотни тысяч msg/sec за счёт batching.

## Q3. Как написать Kafka Consumer с @KafkaListener?

```java
@Component
@Slf4j
public class OrderConsumer {

    @KafkaListener(topics = "orders", groupId = "order-service")
    public void handle(OrderEvent event) {
        log.info("Received: {}", event.orderId());
        processOrder(event);
    }

    // С доступом к метаданным записи
    @KafkaListener(topics = "orders", groupId = "order-service")
    public void handleWithMeta(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("topic={}, partition={}, offset={}: {}", topic, partition, offset, event);
    }

    // Batch listener
    @KafkaListener(topics = "orders", containerFactory = "batchFactory")
    public void handleBatch(List<OrderEvent> events) {
        log.info("Received batch of {}", events.size());
    }
}
```


> [!mcq]
>
> **Вопрос:** В каком потоке выполняется метод `@KafkaListener`, и можно ли иметь несколько `@KafkaListener` методов в одном бине, обрабатывающих **разные типы payload** одного топика?
>
> ---
>
> #### A) `@KafkaListener` метод выполняется в HTTP-thread из Tomcat connector pool — это позволяет переиспользовать `@RequestScope` бины — ❌ Неверно
>
> **Что на самом деле:** `@KafkaListener` методы выполняются в **выделенных consumer-threads** `MessageListenerContainer` (`KafkaConsumerThread-N`). Tomcat вообще не задействован — Spring Kafka работает в чистой Spring-среде без web-stack. `@RequestScope` бины **не доступны** (нет HTTP-request).
>
> **Откуда путаница:** в Spring MVC контроллеры выполняются в Tomcat threads — отсюда ассоциация. Но Kafka consumer — это отдельная инфраструктура.
>
> **Если бы это было правдой:** Kafka-only микросервисы (без HTTP) не могли бы работать — Tomcat был бы обязателен. На практике консьюмеры часто запускаются с `spring.main.web-application-type=none`.
>
> ---
>
> #### B) Несколько `@KafkaListener` методов в одном классе невозможны — Spring выбирает первый попавшийся метод, остальные игнорируются — ❌ Неверно
>
> **Что на самом деле:** Можно иметь **много** `@KafkaListener` методов в одном бине — каждый создаёт **отдельный** `MessageListenerContainer`. Каждый метод привязан к своей конфигурации (topics, groupId, containerFactory). Для **диспатча разных типов** в одном топике используется `@KafkaHandler` + `@KafkaListener` на классе (multi-method dispatch).
>
> **Откуда путаница:** в `@RabbitListener` Spring AMQP действительно требует один listener на бин для multi-method (`@RabbitHandler`). В Spring Kafka семантика аналогичная, но через `@KafkaHandler`.
>
> **Если бы это было правдой:** нельзя было бы организовать paginated consumer/admin endpoints в одном `@Component`. На практике это нормальная архитектура.
>
> ---
>
> #### C) Метод `@KafkaListener` выполняется в выделенном consumer-thread контейнера; для нескольких типов payload в одном топике используется `@KafkaListener` на **классе** + `@KafkaHandler` на методах — multi-method dispatch по типу `@Payload` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Threading: `ConcurrentKafkaListenerContainerFactory` создаёт N контейнеров (`concurrency=N`), каждый со своим thread (`KafkaConsumerThread`). Этот thread выполняет `poll()` в цикле и вызывает listener метод **синхронно** — то есть метод блокирует thread до возврата. Это критично: **`@KafkaListener` метод не должен делать долгие операции** (например, sync HTTP к downstream), иначе следующий `poll()` задержится → `max.poll.interval.ms` exceeded → consumer выпадает из группы.
>
> Multi-method dispatch: `@KafkaListener` ставится на класс, `@KafkaHandler` — на методы с разной сигнатурой `@Payload`. Spring выбирает метод по типу message via `MessageConverter` (обычно Jackson). Это решает паттерн «одна тема — несколько типов событий».
>
> **Пример:**
> ```java
> @Component
> @KafkaListener(topics = "order-events", groupId = "order-handler",
>                containerFactory = "kafkaListenerContainerFactory")
> public class OrderEventDispatcher {
>
>     @KafkaHandler
>     public void onCreated(@Payload OrderCreated event,
>                           @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
>         log.info("Created: {} at {}", event.orderId(), ts);
>         processCreate(event);
>     }
>
>     @KafkaHandler
>     public void onCancelled(@Payload OrderCancelled event) {
>         log.info("Cancelled: {}", event.orderId());
>         processCancel(event);
>     }
>
>     @KafkaHandler(isDefault = true)   // fallback для unknown types
>     public void onUnknown(Object payload) {
>         log.warn("Unknown event type: {}", payload.getClass());
>     }
> }
> ```
> Для корректной работы продюсер должен **писать заголовок типа** (`JsonSerializer` пишет `__TypeId__` по умолчанию), либо consumer должен использовать `JsonDeserializer` с `useTypeHeaders=false` и явным mapping.
>
> **Когда применять:**
> - **Event-sourcing топик** в DDD-системах (LinkedIn, Yandex Realty) — единый поток событий aggregate-а с разными типами.
> - **Saga-coordinator**: один топик команд, dispatched по типу команды.
> - **Avro/Protobuf со schema registry** — multi-method dispatch естественно работает с polymorphic schema.
>
> **Подводные камни:**
> - **Long-running listener блокирует группу**: если метод обрабатывает >`max.poll.interval.ms` (default 5 min), consumer выкинут из группы. Решение: `@Async` (но осторожно с offset commit) или batch listener с manual ack.
> - **`@KafkaHandler` требует Jackson type info** — без `__TypeId__` header dispatcher не различит типы.
> - **Exception в любом `@KafkaHandler` методе** прерывает обработку записи — стандартная `DefaultErrorHandler` retry/DLT работает на уровне всего dispatcher-класса, не индивидуальных handlers.
>
> **Связанные вопросы:** [[Q4]] — consumer groups и partition assignment; [[Q5]] — error handling в listener; [[Q10]] — `JsonDeserializer` type headers.
>
> ---
>
> #### D) `@KafkaListener` создаёт WebFlux-style reactive stream — каждое сообщение приходит в `Flux<ConsumerRecord>`, обработка идёт на event-loop — ❌ Неверно
>
> **Что на самом деле:** Классический `@KafkaListener` — **блокирующий**, синхронный. Reactive вариант — `@EnableKafkaStreams`+ `KStream` или `reactor-kafka` с `ReactiveKafkaConsumerTemplate.receive()` → `Flux<ReceiverRecord>`. Это **отдельные** API, не сам `@KafkaListener`.
>
> **Откуда путаница:** Spring 5+ активно push reactive, и Kafka reactive-обёртки существуют — но `@KafkaListener` остался блокирующим для backward compatibility.
>
> **Если бы это было правдой:** не нужен был бы `reactor-kafka` artifact. Текущее состояние: для reactive стека добавляют отдельный `ReactiveKafkaConsumerTemplate` сверху.

## Q4. Что такое Consumer Group и почему это важно?

Consumer Group — группа потребителей, совместно читающих из топика. Каждая партиция назначается ровно одному потребителю в группе.

```
Topic "orders" (3 partitions)
  Partition 0 → Consumer A (group "order-service")
  Partition 1 → Consumer B (group "order-service")
  Partition 2 → Consumer C (group "order-service")
```

- **Горизонтальное масштабирование**: добавить экземпляр приложения = добавить потребителя в группу.
- **Параллелизм ограничен числом партиций**: больше потребителей, чем партиций → лишние простаивают.
- **Разные группы** получают одни и те же сообщения независимо (fan-out без дублирования).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q5. Как обрабатывать ошибки в @KafkaListener? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Опции обработки ошибок:**

```java
// 1. DefaultErrorHandler — retry + DLT
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<?, ?> template) {
    // 3 попытки с экспоненциальным backoff
    ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
    backOff.setInitialInterval(1000L);
    backOff.setMultiplier(2.0);

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
        (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
    handler.addNotRetryableExceptions(ValidationException.class);
    return handler;
}

@Bean
public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
        ConsumerFactory<?, ?> cf, DefaultErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cf);
    factory.setCommonErrorHandler(errorHandler);
    return factory;
}
```

```java
// 2. @KafkaListener с ErrorHandler
@KafkaListener(topics = "orders", errorHandler = "myErrorHandler")
public void handle(OrderEvent event) { ... }
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q6. Что такое Dead Letter Topic (DLT) и как его использовать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

DLT — топик, куда отправляются сообщения, которые не удалось обработать после всех повторных попыток. Предотвращает блокировку партиции.

```java
// Автоматически создаётся суффикс "-dlt" при DeadLetterPublishingRecoverer
// orders → orders-dlt

// Потребитель DLT для разбора проблем
@KafkaListener(topics = "orders-dlt", groupId = "orders-dlt-handler")
public void handleDlt(
        @Payload OrderEvent event,
        @Header(KafkaHeaders.DLT_EXCEPTION_CAUSE_FQCN) String causeClass,
        @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic) {
    log.error("DLT event from topic={}, cause={}: {}", originalTopic, causeClass, event);
    alertingService.notify(event, causeClass);
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Как работают транзакции в Spring Kafka? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Kafka транзакции гарантируют **exactly-once semantics** при записи в несколько топиков или при совместном использовании с БД.

```yaml
spring:
  kafka:
    producer:
      transaction-id-prefix: tx-orders-
```

```java
@Bean
public KafkaTransactionManager<String, Object> kafkaTransactionManager(
        ProducerFactory<String, Object> pf) {
    return new KafkaTransactionManager<>(pf);
}

// Транзакция Kafka + JPA в одной транзакции (ChainedKafkaTransactionManager)
@Transactional("chainedKafkaTxManager")
public void processAndPublish(OrderCommand cmd) {
    Order order = orderRepository.save(new Order(cmd));  // JPA
    kafkaTemplate.send("orders", order.getId(), new OrderCreated(order)); // Kafka
    // При откате JPA транзакции — откатится и Kafka
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. Как настроить Kafka Listener для конкурентного чтения? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
@Bean
public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory(
        ConsumerFactory<String, OrderEvent> cf) {
    ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(cf);
    factory.setConcurrency(3); // 3 потока = 3 consumer, по одному на партицию
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    return factory;
}

@KafkaListener(topics = "orders", concurrency = "3")
public void handle(OrderEvent event, Acknowledgment ack) {
    try {
        processOrder(event);
        ack.acknowledge(); // ручной коммит offset
    } catch (Exception e) {
        log.error("Processing failed", e);
        // не вызываем ack → сообщение будет перечитано
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Как управлять offset коммитами? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| AckMode | Поведение |
|---------|-----------|
| `BATCH` (default) | Коммит после обработки пачки записей |
| `RECORD` | Коммит после каждой записи |
| `MANUAL` | Ручной вызов `ack.acknowledge()` |
| `MANUAL_IMMEDIATE` | Немедленный коммит при вызове `ack.acknowledge()` |
| `TIME` | Периодический коммит по таймеру |
| `COUNT` | Коммит после N записей |

```yaml
spring:
  kafka:
    consumer:
      enable-auto-commit: false
      auto-offset-reset: earliest  # или latest
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. Как работает сериализация/десериализация в Spring Kafka? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```yaml
spring:
  kafka:
    producer:
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "com.example.*"
        spring.json.value.default.type: com.example.OrderEvent
```

```java
// Кастомный десериализатор для polymorphic types
@Bean
public ConsumerFactory<String, Object> consumerFactory() {
    JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
    deserializer.addTrustedPackages("com.example.*");
    deserializer.setUseTypeHeaders(false);
    deserializer.setRemoveTypeHeaders(true);
    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как тестировать Spring Kafka без реального брокера? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```xml
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka-test</artifactId>
  <scope>test</scope>
</dependency>
```

```java
@EmbeddedKafka(
    partitions = 1,
    topics = {"orders", "orders-dlt"},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092"}
)
@SpringBootTest
class OrderConsumerTest {

    @Autowired
    private KafkaTemplate<String, OrderEvent> template;

    @Autowired
    private OrderService orderService;

    @Test
    void shouldProcessOrderEvent() throws Exception {
        OrderEvent event = new OrderEvent(UUID.randomUUID(), "PENDING");

        template.send("orders", event.orderId().toString(), event).get();

        // Ждём обработки
        await().atMost(5, SECONDS).until(() ->
            orderService.findById(event.orderId()).isPresent());
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Что такое Kafka Streams в контексте Spring? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Spring Kafka поддерживает Kafka Streams через `StreamsBuilderFactoryBean`:

```java
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration streamsConfig() {
        return new KafkaStreamsConfiguration(Map.of(
            StreamsConfig.APPLICATION_ID_CONFIG, "orders-processor",
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
        ));
    }

    @Bean
    public KStream<String, OrderEvent> orderStream(StreamsBuilder builder) {
        KStream<String, OrderEvent> stream = builder.stream("orders");

        stream.filter((key, value) -> "COMPLETED".equals(value.status()))
              .to("completed-orders");

        stream.groupByKey()
              .count()
              .toStream()
              .to("order-counts");

        return stream;
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. Какие стратегии обеспечения порядка сообщений есть в Kafka? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
// 1. Один partition на топик (low throughput, high ordering)
@KafkaListener(topics = "critical-orders", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))

// 2. Одинаковый ключ для связанных сообщений (partition per key)
kafkaTemplate.send("orders", order.getCustomerId(), event);  // все события одного клиента в одну партицию

// 3. Заголовки для отслеживания порядка
ProducerRecord<String, OrderEvent> record = new ProducerRecord<>("orders", event.orderId(), event);
record.headers().add(new RecordHeader("sequence", ByteBuffer.allocate(8).putLong(sequence).array()));
```


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Как настроить idempotent producer? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```yaml
spring:
  kafka:
    producer:
      properties:
        enable.idempotence: true   # acks=all, retries>0 автоматически
        max.in.flight.requests.per.connection: 5  # max=5 для idempotence
```

Idempotent producer присваивает каждому сообщению sequence number; брокер отбрасывает дубли при повторных отправках.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Как работает Pause/Resume для @KafkaListener? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

```java
@Autowired
private KafkaListenerEndpointRegistry registry;

// Пауза при перегрузке
public void pauseConsumer() {
    registry.getListenerContainer("ordersContainer")
        .pause();
}

// Возобновление
public void resumeConsumer() {
    registry.getListenerContainer("ordersContainer")
        .resume();
}

// Пауза на уровне конкретной партиции
@KafkaListener(id = "ordersContainer", topics = "orders")
public void handle(ConsumerRecord<String, OrderEvent> record,
                   Consumer<String, OrderEvent> consumer) {
    if (isBackpressure()) {
        consumer.pause(consumer.assignment());
    }
    // ...
}
```

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka: partitions, offsets, consumer groups, delivery semantics
- [Spring Boot](spring-boot-interview.md) — auto-configuration, Spring Boot starters
- [Spring @Transactional](spring-transaction-interview.md) — транзакции Kafka + JPA через ChainedKafkaTransactionManager
- [Spring Retry](spring-retry-interview.md) — retry в Kafka listeners через DefaultErrorHandler
- [Spring Modulith](spring-modulith-interview.md) — ApplicationEvents как альтернатива Kafka в монолите
- [Spring WebFlux](spring-webflux-interview.md) — реактивный стек с ReactiveKafkaConsumerTemplate
- [Spring Testing](spring-testing-interview.md) — @EmbeddedKafka для интеграционных тестов
- [Message Brokers](../../messaging/message-brokers-comparison-interview.md) — сравнение Kafka, RabbitMQ, Pulsar
- [Spring Events](spring-events-interview.md) — ApplicationEventPublisher как лёгкая альтернатива
- [Resilience4j](resilience4j-interview.md) — circuit breaker для Kafka producers
