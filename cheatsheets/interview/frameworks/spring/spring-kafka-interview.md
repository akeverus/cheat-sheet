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
updated: "2026-05-15"
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
> **Связанные вопросы:** [[spring-kafka-interview#Q2]] — `KafkaTemplate` details; [[spring-kafka-interview#Q3]] — `@KafkaListener` варианты; [[spring-kafka-interview#Q8]] — `ConcurrentKafkaListenerContainerFactory`.

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
> **Связанные вопросы:** [[spring-kafka-interview#Q7]] — Kafka transactions, `executeInTransaction`; [[spring-kafka-interview#Q14]] — idempotent producer; [[spring-kafka-interview#Q13]] — ordering и partition key.
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
> **Связанные вопросы:** [[spring-kafka-interview#Q4]] — consumer groups и partition assignment; [[spring-kafka-interview#Q5]] — error handling в listener; [[spring-kafka-interview#Q10]] — `JsonDeserializer` type headers.
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
>
> **Вопрос:** Что произойдёт, если у топика 3 партиции, а в Consumer Group 5 потребителей с одинаковым `groupId`?
>
> ---
>
> #### A) 3 потребителя получат по одной партиции, 2 будут **idle** в состоянии STABLE без сообщений; добавление шестого потребителя ничего не изменит — параллелизм ограничен числом партиций — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Kafka гарантирует: **одна партиция = один consumer внутри группы** в каждый момент времени (для ordering и offset-tracking). Если в группе больше потребителей, чем партиций — лишние сидят без работы, держат heartbeat, но не получают записей. Это базовое ограничение масштабирования.
>
> **Practical implication:** при планировании capacity сначала выбирают **число партиций** (это hard upper bound на параллелизм consumer), потом — `concurrency` listener. Изменить число партиций в работающем топике можно только **в большую сторону** (`kafka-topics.sh --alter --partitions N`), но это **сломает порядок** для существующих ключей (rebalancing rehash). Поэтому для критичных топиков партиции overprovisioned at design time (например, 50 партиций на старте даже для текущих 5 consumers).
>
> Rebalancing protocol: когда consumer добавляется/удаляется, **GroupCoordinator** (broker) триггерит rebalance — все consumers коротко останавливают обработку, переdistribution партиций по `PartitionAssignor` (default: `RangeAssignor`, в Kafka 2.4+ — `CooperativeStickyAssignor` для меньшего движения).
>
> **Пример:**
> ```yaml
> # 3 partition topic + 3 instance Spring Boot app:
> # - каждый instance имеет 1 thread (concurrency=1)
> # - каждый instance получит 1 partition
> # - результат: 3-way parallelism
>
> # Если concurrency=3 на одном instance с 3 partitions:
> # - 1 instance × 3 threads = 3 consumer
> # - также 3-way parallelism, но без HA (один pod падает — всё стоит)
> ```
>
> ```bash
> # Diagnose: кто сколько партиций имеет
> kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
>   --describe --group order-service
> # GROUP         TOPIC   PARTITION  CURRENT-OFFSET  CONSUMER-ID
> # order-service orders  0          12345           consumer-1-abc...
> # order-service orders  1          12300           consumer-2-def...
> # order-service orders  2          12400           consumer-3-ghi...
> # consumer-4-* и consumer-5-* — без PARTITION (idle)
> ```
>
> **Когда применять:**
> - **HA pattern**: 3 partitions + 3 replicas Spring Boot pods + concurrency=1 → каждый pod держит одну партицию, на pod restart другой подхватывает.
> - **Hot-key scenario** (LinkedIn-style): если партиции выбираются по user_id и есть «знаменитость» — её партиция перегружена, остальные idle. Решение: salt key (например, `user_id + random(0..N)`), но теряем ordering.
> - **Multi-tenant**: разные `groupId` для каждого consumer-приложения дают независимое чтение одного топика (fan-out).
>
> **Подводные камни:**
> - **Rebalancing pause**: classic protocol останавливает ВСЮ группу на время rebalance (stop-the-world). `CooperativeStickyAssignor` (default with Spring Kafka 3.x) — incremental rebalance, движение только нужных партиций.
> - **`session.timeout.ms` < `max.poll.interval.ms`**: если listener долго обрабатывает запись, истекает poll interval → consumer kicked out → rebalance. Симптом в логах: `Member ... has failed, removing it from the group`.
> - **`groupId` collision**: два разных приложения с одним `groupId` будут «съедать» сообщения друг у друга — баг типа «у меня только половина сообщений приходит».
>
> **Связанные вопросы:** [[spring-kafka-interview#Q3]] — `concurrency` параметр; [[spring-kafka-interview#Q8]] — `ConcurrentKafkaListenerContainerFactory`; [[spring-kafka-interview#Q9]] — offset commits в group context.
>
> ---
>
> #### B) Все 5 потребителей будут получать **одинаковые** копии сообщений — это broadcast паттерн Consumer Group — ❌ Неверно
>
> **Что на самом деле:** Внутри одной Consumer Group **нет** broadcast. Каждое сообщение доставляется **ровно одному** consumer группы. Broadcast достигается **разными** `groupId` — каждая группа независимо читает все сообщения.
>
> **Откуда путаница:** в JMS Topic broadcast действительно работает на subscribers одного topic. Kafka реализует и queue, и pub/sub через combination Consumer Group + topic.
>
> **Если бы это было правдой:** offset был бы per-consumer, а не per-group — но Kafka хранит offset в `__consumer_offsets` именно по `(groupId, topic, partition)`. На практике broadcasting через consumer group не работает.
>
> ---
>
> #### C) Kafka автоматически создаст 2 дополнительные партиции для балансировки — rebalance scales topic — ❌ Неверно
>
> **Что на самом деле:** Kafka **никогда** не создаёт партиции автоматически. Изменение partition count — ручное администраторское действие (`kafka-topics.sh --alter`). Rebalance перераспределяет существующие партиции между consumers, не создаёт новые.
>
> **Откуда путаница:** в managed-Kafka (Confluent Cloud) есть auto-scaling **тиров**, но не auto-creation партиций. Партиции — топологическое решение, и их число влияет на disk I/O всех брокеров.
>
> **Если бы это было правдой:** топологии Kafka деградировали бы со временем — каждое подключение consumer добавляло бы партиции, увеличивая нагрузку на диск brokers. Реальность: партиции стабильны и planning требует prediction.
>
> ---
>
> #### D) Запуск шестого потребителя выкинет один из существующих по принципу LRU — group size hard-capped размером партиций — ❌ Неверно
>
> **Что на самом деле:** Kafka **не выкидывает** consumers из группы по достижению лимита. Все 5 (или 50) могут быть в группе — лишние просто idle (без assignment). Выкидывание происходит только по `session.timeout` или `max.poll.interval` exceeded.
>
> **Откуда путаница:** некоторые реализации thread-pool делают eviction при превышении capacity — отсюда ассоциация. Kafka group membership — это soft state, не bounded.
>
> **Если бы это было правдой:** ручной shutdown extra-consumers пропадал бы непредсказуемо — нельзя было бы предсказать, какой pod выживет. На практике лишние instance идут в idle, что прозрачно для оператора.

## Q5. Как обрабатывать ошибки в @KafkaListener?

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
>
> **Вопрос:** Что делает `DefaultErrorHandler` по умолчанию при exception в `@KafkaListener`, и почему «просто catch внутри метода» — недостаточно?
>
> ---
>
> #### A) Сразу коммитит offset проблемной записи и продолжает с следующей — без retries — ❌ Неверно
>
> **Что на самом деле:** `DefaultErrorHandler` (default в Spring Kafka 2.8+) делает `FixedBackOff(0L, 9L)` — **10 attempts** (1 initial + 9 retries) с zero delay, потом log error и seek **на следующий offset** (`seekToCurrentErrorHandler` для batch варианта — другая семантика). Простой commit без retries не его поведение.
>
> **Откуда путаница:** в Spring Kafka 2.4 был `LoggingErrorHandler`, который действительно только логировал. `DefaultErrorHandler` (новый) — другая дефолтная политика.
>
> **Если бы это было правдой:** transient ошибки (network blip, БД temporary unavailable) приводили бы к потере данных. На практике default retries спасают от 90% таких ошибок.
>
> ---
>
> #### B) Перебрасывает exception вызывающему — в `MessageListenerContainer.stop()` — что останавливает весь listener — ❌ Неверно
>
> **Что на самом деле:** `DefaultErrorHandler` **не останавливает** контейнер. Старая семантика `SeekToCurrentErrorHandler` (deprecated) могла seek без commit (запись перечитывается бесконечно), но контейнер продолжал работать. Stop контейнера — это отдельный action через `KafkaListenerEndpointRegistry`.
>
> **Откуда путаница:** в Spring Batch необработанный exception останавливает Job. Spring Kafka — другая семантика: контейнер устойчив к ошибкам обработки.
>
> **Если бы это было правдой:** одна kafka запись с битым payload убивала бы весь потребитель — production был бы хрупким. Реальность: error handler **сам решает**, что делать (retry/skip/DLT/stop).
>
> ---
>
> #### C) Откатывает Kafka offset на начало топика (`auto.offset.reset=earliest`) и перечитывает все записи — ❌ Неверно
>
> **Что на самом деле:** `auto.offset.reset` срабатывает **только** когда у consumer **нет** committed offset для партиции (первое подключение, новый groupId, или удалили offset). На уже работающей группе ошибка обработки не триггерит реset на начало.
>
> **Откуда путаница:** в DLT-pipeline можно вручную reset offset через `kafka-consumer-groups.sh --reset-offsets --to-earliest` для replay — но это администраторская команда, не auto-behavior.
>
> **Если бы это было правдой:** баг в одной записи приводил бы к replay миллионов сообщений — экономически невозможно для high-throughput систем.
>
> ---
>
> #### D) Делает **retries с backoff** (default `FixedBackOff(0L, 9L)` — 10 попыток без задержки), при exhaust вызывает **recoverer** (по умолчанию — log + skip; с `DeadLetterPublishingRecoverer` — публикация в `<topic>.DLT`); offset коммитится **только после** recoverer; `addNotRetryableExceptions` исключает «детерминированные» ошибки из retries — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Try/catch внутри listener метода ловит exception, но **не интегрируется** с infrastructure: нет правил «retry/skip/DLT» на уровне фреймворка, нет метрик retry attempt, нет stack-trace headers в DLT, нет правильного offset управления. После `catch + return` offset коммитится **как если бы запись была успешно обработана** — это data loss.
>
> Правильно: бросать exception из метода → `DefaultErrorHandler` применяет backoff → если backoff exhausted, вызывается `ConsumerRecordRecoverer.accept(record, exception)` — обычно `DeadLetterPublishingRecoverer`, который пишет в `<topic>.DLT` с headers `kafka_dlt-exception-fqcn`, `kafka_dlt-exception-message`, `kafka_dlt-exception-stacktrace`, `kafka_dlt-original-topic`. Это даёт **полный контекст** для downstream диагностики.
>
> `addNotRetryableExceptions(ValidationException.class)` — критический паттерн: невалидный payload не должен ретраиться 10 раз (всё равно failed), отправить сразу в DLT.
>
> **Пример:**
> ```java
> @Configuration
> public class KafkaErrorConfig {
>
>     @Bean
>     public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
>         var backoff = new ExponentialBackOffWithMaxRetries(5);
>         backoff.setInitialInterval(1_000L);                          // 1s
>         backoff.setMultiplier(2.0);                                  // 2s, 4s, 8s, 16s
>         backoff.setMaxInterval(30_000L);                             // cap 30s
>
>         var recoverer = new DeadLetterPublishingRecoverer(template,
>             (rec, ex) -> new TopicPartition(rec.topic() + ".DLT", rec.partition()));
>
>         var handler = new DefaultErrorHandler(recoverer, backoff);
>         handler.addNotRetryableExceptions(
>             ValidationException.class,                               // плохой payload
>             ConstraintViolationException.class,
>             IllegalArgumentException.class);
>         handler.setRetryListeners((rec, ex, attempt) ->
>             log.warn("Retry {} for offset={}", attempt, rec.offset(), ex));
>         return handler;
>     }
> }
>
> @Component
> @RequiredArgsConstructor
> public class OrderConsumer {
>     private final OrderProcessor processor;
>
>     @KafkaListener(topics = "orders", groupId = "order-service")
>     public void handle(OrderEvent event) {
>         processor.process(event);              // exception bubbles up — handler решит
>     }
> }
> ```
>
> **Когда применять:**
> - **Любой production listener** (Wolt order processing, Yandex courier dispatch) — без DLT записи теряются после retries.
> - **Idempotent downstream**: можно retry агрессивно, downstream handles duplicates.
> - **Saga compensations**: если retry не помогает — DLT в outbox + manual operator decision.
>
> **Подводные камни:**
> - **Recoverer тоже может упасть** (broker down во время write в DLT) — настройте `DeadLetterPublishingRecovererFactory` с retries publishing, либо `kafkaTemplate` с idempotent producer.
> - **Headers DLT не сериализуемые в Avro**: при Avro консьюмере DLT — добавьте `JsonSerializer` для DLT topic отдельно.
> - **Order break после retry**: если запись retries 10s, последующие записи partition ждут — для critical-path задайте `addRetryableExceptions` только сетевым.
> - **`@RetryableTopic`** (Spring Kafka 2.7+) — альтернатива: создаёт отдельные retry-topics для async retry, не блокирует основной.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q6]] — DLT в детали; [[spring-kafka-interview#Q9]] — offset commit semantic; [[spring-kafka-interview#Q15]] — pause/resume в backpressure.

## Q6. Что такое Dead Letter Topic (DLT) и как его использовать?

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
>
> **Вопрос:** Какой Kafka-механизм лежит в основе Dead Letter Topic, и как `DeadLetterPublishingRecoverer` сохраняет контекст ошибки для DLT-consumer?
>
> ---
>
> #### A) DLT — это специальный broker-side feature Kafka: при ack-failure broker сам перенаправляет запись в `<topic>.DLT` через internal механизм — ❌ Неверно
>
> **Что на самом деле:** Kafka broker **ничего не знает** о DLT. Это **client-side convention**: `DeadLetterPublishingRecoverer` — обычный Spring Kafka recoverer, который вызывает `kafkaTemplate.send("<topic>.DLT", record)` через стандартный Producer API. Broker видит это как обычную publish-операцию.
>
> **Откуда путаница:** в AWS SQS DLQ — это broker-side feature (auto-redrive после maxReceiveCount). В Kafka DLT — паттерн, реализованный в клиенте.
>
> **Если бы это было правдой:** не нужно было бы конфигурировать `DeadLetterPublishingRecoverer` — broker автоматом всё бы делал. Реальность: без recoverer-а DLT не работает, не существует.
>
> ---
>
> #### B) DLT — **обычный** Kafka топик (часто `<original>.DLT`), куда `DeadLetterPublishingRecoverer` после exhaust retries публикует запись с **headers**: `kafka_dlt-original-topic`, `kafka_dlt-original-partition`, `kafka_dlt-original-offset`, `kafka_dlt-exception-fqcn`, `kafka_dlt-exception-message`, `kafka_dlt-exception-stacktrace` — это даёт операторам полный контекст для диагностики и replay — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Внутри `DeadLetterPublishingRecoverer.accept(record, ex)` логика:
> 1. Сериализует key/value исходной записи (или преобразует через `headerEnricher`).
> 2. Копирует **все** original headers.
> 3. Добавляет headers с префиксом `kafka_dlt-*` (или `KafkaHeaders.DLT_*` constants).
> 4. Вызывает `kafkaTemplate.send(new ProducerRecord<>("<topic>.DLT", ...))`.
> 5. Возвращает `void` — но при transactional setup это в той же transaction, что и offset commit (atomicity).
>
> Operations team имеет три типичных workflow с DLT:
> - **Alerting**: `@KafkaListener(topics = ".*\\.DLT$", topicPattern = ...)` шлёт alert в Slack.
> - **Investigation**: kafka-console-consumer + headers display показывают exception type.
> - **Replay**: после фикса бага через `kafka-console-producer` (или kcat) скопировать записи назад в original topic, либо запустить временный «retry» consumer.
>
> **Пример:**
> ```java
> @Bean
> public DeadLetterPublishingRecoverer dltRecoverer(KafkaTemplate<String, Object> template) {
>     return new DeadLetterPublishingRecoverer(template,
>         (rec, ex) -> {
>             // Кастомный routing: разные DLT для разных типов exceptions
>             if (ex.getCause() instanceof ValidationException) {
>                 return new TopicPartition(rec.topic() + ".validation.DLT", -1);
>             }
>             return new TopicPartition(rec.topic() + ".DLT", rec.partition());
>         });
> }
>
> @KafkaListener(topics = "orders.DLT", groupId = "dlt-monitor")
> public void monitorDlt(
>         @Payload OrderEvent event,
>         @Header(KafkaHeaders.DLT_EXCEPTION_FQCN) String exceptionType,
>         @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String exceptionMsg,
>         @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic,
>         @Header(KafkaHeaders.DLT_ORIGINAL_OFFSET) long originalOffset) {
>     alertingService.notify(event, exceptionType, exceptionMsg, originalTopic, originalOffset);
>     dltMetricsService.recordDltEvent(originalTopic, exceptionType);
> }
> ```
>
> **Когда применять:**
> - **Любой Spring Boot Kafka сервис в production** (Yandex, Wolt, LinkedIn) — без DLT баги обработки теряют данные.
> - **Multi-stage retry**: основной retry в memory (10 attempts), DLT + manual replay для уровня операторов.
> - **Compliance**: DLT с retention 30+ дней даёт audit trail для регуляторов (например, финтех).
>
> **Подводные камни:**
> - **DLT partition count** обычно равен original (чтобы preserved ordering при replay) — но это создаёт `topic.partitions × 2` storage cost.
> - **DLT loop**: если consumer DLT тоже падает, можно создать «вторичный DLT» — но обычно проще log + alert.
> - **Schema evolution**: при изменении schema в original topic, old DLT записи могут не десериализоваться — используйте `ErrorHandlingDeserializer` для DLT consumer.
> - **DLT producer transactions**: если main consumer transactional, recoverer publishing должен быть в том же `KafkaTemplate` — иначе DLT публикация может потеряться при rollback.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q5]] — error handler retries leading to DLT; [[spring-kafka-interview#Q7]] — transactional DLT publishing; [[spring-kafka-interview#Q13]] — ordering preservation in DLT replay.
>
> ---
>
> #### C) DLT — это специальный consumer group, который получает все failed messages из любого топика автоматически — ❌ Неверно
>
> **Что на самом деле:** Consumer group — это группа **потребителей**, не топик. DLT — топик. Они ортогональны: DLT топик может иметь свою consumer group (`dlt-monitor`), но это **обычная** группа.
>
> **Откуда путаница:** smешение двух конcept — possible если думать «DLT = special consumer». Реально DLT — это куда **пишут**, consumer group — кто **читает**.
>
> **Если бы это было правдой:** все приложения автоматом получали бы failed messages из всех топиков — это нарушение isolation. На практике каждое приложение конфигурирует свой DLT explicitly.
>
> ---
>
> #### D) DLT работает только при `acks=all` и `enable.idempotence=true`, иначе сообщения теряются — это broker-enforced — ❌ Неверно
>
> **Что на самом деле:** DLT **не требует** `acks=all` или `idempotence`. Это просто публикация в Kafka — работает с любыми producer settings. Тем не менее, **рекомендуется** `acks=all` (чтобы DLT publish не потерялось при broker failure) и idempotence (чтобы избежать дубликатов DLT при producer retry).
>
> **Откуда путаница:** `idempotence` и `acks=all` — обязательны для **transactional** producer, и часто DLT pipeline transactional. Но это не requirement DLT как такового.
>
> **Если бы это было правдой:** нельзя было бы делать DLT в high-throughput pipelines с `acks=0` (которые exist в metrics-streaming). Реальность: DLT — flexible feature, не привязан к durability settings.

## Q7. Как работают транзакции в Spring Kafka?

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
>
> **Вопрос:** Что обеспечивают Kafka транзакции (`transaction.id` + `KafkaTransactionManager`), и почему `@Transactional` поверх Kafka template **не** даёт «exactly-once» гарантию с произвольной внешней БД?
>
> ---
>
> #### A) Транзакции в Kafka — это просто `producer.beginTransaction()` + `producer.commitTransaction()`, аналог JDBC: либо все записи в одном топике вошли, либо ни одна — ❌ Неверно
>
> **Что на самом деле:** Kafka транзакции **шире** простого «все или ничего» в одном топике: они охватывают записи в **несколько топиков и партиций атомарно**, а также **offset commits** для consumer-side (для exactly-once stream processing — read → transform → write). Это не JDBC-аналог.
>
> **Откуда путаница:** API повторяет JDBC (`begin/commit/abort`), но семантика — distributed atomic broadcast.
>
> **Если бы это было правдой:** Kafka Streams не могли бы делать `read → process → write + commit offset` атомарно — но именно это и есть exactly-once semantics. На практике transactions дают cross-topic atomicity + offset.
>
> ---
>
> #### B) Transactional producer гарантирует exactly-once между Kafka и любой другой системой (БД, Redis, S3) автоматически — broker отслеживает внешние commits — ❌ Неверно
>
> **Что на самом деле:** Kafka transactions работают **только** внутри Kafka cluster (broker-side). Никакой broker не «знает» про внешнюю БД. Распределённая атомарность Kafka+JPA требует **two-phase commit** (XA) или **outbox pattern** — Kafka transactions сами по себе этого не дают.
>
> **Откуда путаница:** `ChainedKafkaTransactionManager` создаёт **видимость** общей транзакции — но это **best-effort** chain (commits в последовательности), а не XA. Между commit JPA и commit Kafka возможен сбой → inconsistency.
>
> **Если бы это было правдой:** не нужны были бы Debezium/outbox/Kafka Connect для CDC — приложение само писало бы в БД и Kafka atomically. Реальность: outbox-pattern — индустриальный стандарт именно из-за этого.
>
> ---
>
> #### C) Transactional producer (с `transaction.id`) обеспечивает **атомарное** write в несколько Kafka-топиков + offset commit как часть транзакции; consumer должен использовать `isolation.level=read_committed` для невидимости uncommitted записей. **Exactly-once с внешней БД** требует outbox pattern (БД пишет в outbox-таблицу → Debezium публикует в Kafka) или 2PC; `ChainedKafkaTransactionManager` даёт **best-effort chained commits**, не настоящую atomicity — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Внутренняя механика: каждый transactional producer регистрируется с уникальным `transaction.id` на `TransactionCoordinator` broker. Coordinator поддерживает state machine (`Ongoing` → `PrepareCommit` → `CompleteCommit`), пишет markers в `__transaction_state` топик и в каждую партицию, куда были writes (commit markers — `0x01`, abort markers — `0x00`). Consumer с `isolation.level=read_committed` пропускает все записи с abort marker и uncommitted записи.
>
> Exactly-once stream processing (Kafka Streams) использует transactions: `consumer.poll()` → `producer.send()` → `producer.sendOffsetsToTransaction(offsets, consumerGroupMetadata)` → `producer.commitTransaction()`. Если crash в любой точке — abort → consumer перечитывает с last committed offset.
>
> ChainedKafkaTransactionManager: `@Transactional("chainedKafkaTxManager")` запускает Kafka tx + JPA tx → выполняет body → commit JPA → commit Kafka. Между commit JPA и commit Kafka может быть сбой (JVM crash, network partition) → JPA committed, Kafka **не** committed → data inconsistency. Это **не atomic**, это «hopeful chain».
>
> **Пример (outbox pattern — правильный):**
> ```java
> @Service
> @RequiredArgsConstructor
> public class OrderService {
>     private final OrderRepository orderRepo;
>     private final OutboxRepository outboxRepo;
>
>     @Transactional   // только JPA-транзакция, никакой Kafka
>     public void createOrder(OrderCommand cmd) {
>         Order order = orderRepo.save(new Order(cmd));
>         outboxRepo.save(new OutboxEvent(
>             "orders",
>             order.getId().toString(),
>             objectMapper.writeValueAsString(new OrderCreated(order))
>         ));   // atomic с order: одна JPA tx, одна БД
>     }
> }
>
> // Debezium читает CDC из outbox-таблицы → публикует в Kafka
> // Atomicity гарантирована БД (single transaction), Kafka получает с задержкой
> ```
>
> **Когда применять:**
> - **Kafka Streams** (Confluent, Yandex.Reklama): exactly-once `processing.guarantee=exactly_once_v2` — встроенные transactions.
> - **Outbox pattern** (Stripe, Wolt): для надёжной публикации событий из микросервиса с БД.
> - **Saga compensation**: transactional producer для атомарного «send command + commit consumer offset» в saga step.
>
> **Подводные камни:**
> - **`transaction.id` коллизии**: если два инстанса используют один `transaction.id`, новый «fencit» старый (`ProducerFencedException`). При scale-out нужны уникальные ID — обычно `<service>-<podName>-<index>`.
> - **Transaction timeout** (`transaction.timeout.ms`, default 60s): если transaction висит дольше — broker abort. Long-running listener в транзакции может surprisingly abort.
> - **`read_committed` lag**: consumer ждёт commit/abort marker — latency растёт на размер `transaction.timeout.ms` worst-case.
> - **Кросс-кластерные transactions невозможны**: MirrorMaker copy не сохраняет transactional semantics.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q14]] — idempotent producer (обязательно для transactional); [[spring-kafka-interview#Q9]] — `sendOffsetsToTransaction`; [[spring-kafka-interview#Q12]] — Kafka Streams exactly-once.
>
> ---
>
> #### D) Транзакции работают только при `acks=0` — broker не должен ждать подтверждения для atomic commit — ❌ Неверно
>
> **Что на самом деле:** Transactional producer **обязательно** требует `acks=all` (default при enable transactions). `acks=0` (fire-and-forget) несовместим — broker не может garantee atomic commit без подтверждений.
>
> **Откуда путаница:** обратная ассоциация «низкие acks → меньше блокировки → лучше для tx». На деле transactions требуют **максимальной** durability.
>
> **Если бы это было правдой:** transactional pipeline не давал бы exactly-once — half of writes терялись бы в transit. Реальность: `acks=all + min.insync.replicas=2+` mandatory.

## Q8. Как настроить Kafka Listener для конкурентного чтения?

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
>
> **Вопрос:** Что точно делает `factory.setConcurrency(3)` в `ConcurrentKafkaListenerContainerFactory`, и почему `concurrency > partitions` бесполезно?
>
> ---
>
> #### A) `setConcurrency(3)` создаёт **3 sub-контейнера** внутри одного listener, каждый со своим `KafkaConsumer` и dedicated thread; партиции **топика** распределяются между этими consumers (внутри ОДНОГО Spring Boot инстанса). Если у топика 2 партиции, при `concurrency=3` третий consumer будет idle — параллелизм ограничен `min(concurrency, partitions)` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Архитектурно `ConcurrentMessageListenerContainer` — это **группа** из N `KafkaMessageListenerContainer`. Каждый sub-container:
> 1. Создаёт **собственный** `KafkaConsumer` через `consumerFactory`.
> 2. Запускает **свой** thread (`KafkaConsumerThread-<n>`).
> 3. Делает свой `poll()` цикл и диспатчит в listener method.
>
> При rebalance внутри одного Consumer Group партиции распределяются по всем consumers группы (включая консьюмеров из других инстансов). Если у топика 2 партиции, а у тебя 1 instance × concurrency=3 → 3 consumer регистрируются в группе → 2 получат по партиции, 1 idle. Аналогично, если 3 instance × concurrency=3 на топик с 5 partition → 9 consumers, 5 с партициями, 4 idle.
>
> Главная проблема: **idle consumers расходуют ресурсы** (heap, threads, network sockets) и **участвуют в rebalance** (замедляют его). Поэтому `concurrency` должен **точно** соответствовать planned partition count: `concurrency × replicas = partition_count`.
>
> Тredding model: каждый sub-container thread обрабатывает свою партицию **последовательно** — это гарантирует ordering внутри партиции. Внутри thread можно делать `@Async`, но тогда теряется ordering и offset commit становится сложным.
>
> **Пример:**
> ```java
> @Configuration
> public class KafkaConcurrencyConfig {
>
>     // Топик с 6 partitions, 2 instance с concurrency=3 → 6 consumers, всё работает
>     @Bean
>     public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> orderListenerFactory(
>             ConsumerFactory<String, OrderEvent> cf) {
>         var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderEvent>();
>         factory.setConsumerFactory(cf);
>         factory.setConcurrency(3);
>         factory.getContainerProperties().setAckMode(AckMode.MANUAL);
>         factory.getContainerProperties().setPollTimeout(3_000);
>         return factory;
>     }
> }
>
> // Или через annotation
> @KafkaListener(topics = "orders", concurrency = "3", containerFactory = "orderListenerFactory")
> public void handle(OrderEvent event, Acknowledgment ack) {
>     processOrder(event);
>     ack.acknowledge();
> }
> ```
>
> **Diagnose**: `kafka-consumer-groups.sh --describe --group <group>` покажет PARTITION column пустой для idle consumers — это red flag «слишком много concurrency».
>
> **Когда применять:**
> - **Topic-bound parallelism**: 1 instance × concurrency=N для batch-обработки в outbox publisher.
> - **HA по партициям**: replicas × concurrency=1 — каждый pod держит одну партицию, на pod restart другой подхватывает (Wolt courier dispatch).
> - **Saturated CPU listener** (compression, ML inference): concurrency = CPU cores, partitions tuned to match.
>
> **Подводные камни:**
> - **`concurrency > partitions`**: idle consumers, замедляют rebalance, нагружают `__consumer_offsets` heartbeat traffic.
> - **`concurrency` менять hot не получается**: требует restart listener container; для dynamic scaling использовать `KafkaListenerEndpointRegistry.getListenerContainer().stop()` + reconfig.
> - **`ack.acknowledge()` thread-safety**: вызывается в consumer thread; нельзя acknowledge из `@Async` без careful coordination.
> - **Rebalance взаимодействие**: при добавлении/удалении consumer вся группа коротко останавливается (или incrementally с `CooperativeStickyAssignor`).
>
> **Связанные вопросы:** [[spring-kafka-interview#Q4]] — Consumer Group + partitions; [[spring-kafka-interview#Q9]] — AckMode для concurrent контейнера; [[spring-kafka-interview#Q3]] — listener thread model.
>
> ---
>
> #### B) `setConcurrency(3)` запускает 3 **поток** внутри **одного** `KafkaConsumer` для parallel processing записей одной партиции — ❌ Неверно
>
> **Что на самом деле:** Один `KafkaConsumer` instance **не thread-safe** и не может быть обработан несколькими threads. `concurrency=3` создаёт **3 отдельных** consumer-а, каждый со своим thread. Внутри одной партиции записи обрабатываются строго последовательно (для ordering).
>
> **Откуда путаница:** общая ассоциация «concurrency = parallel processing one task». В Kafka concurrency = «сколько consumers создать», не «сколько threads на партицию».
>
> **Если бы это было правдой:** ordering внутри партиции потерялся бы — параллельная обработка не гарантирует order. На практике Kafka даёт ordering именно потому, что одна партиция — один thread.
>
> ---
>
> #### C) `setConcurrency(3)` означает, что Kafka cluster добавит 3 brokers для load balancing запросов — ❌ Неверно
>
> **Что на самом деле:** `concurrency` — это **client-side** конфигурация Spring Kafka. Broker-side ничего об этом не знает. Number of brokers и concurrency listener — независимые концепты.
>
> **Откуда путаница:** Смешение client/server параметров. Cluster scale — это broker operations team, listener concurrency — application configuration.
>
> **Если бы это было правдой:** добавление concurrency в app code триггерило бы infrastructure changes — невозможно. Реальность: `concurrency` влияет только на JVM consumers.
>
> ---
>
> #### D) `setConcurrency(3)` создаёт thread pool из 3 threads для **асинхронного** dispatch listener метода — основной thread остаётся свободен — ❌ Неверно
>
> **Что на самом деле:** `concurrency` создаёт **отдельных** consumers с **своими** poll loops. Это не async dispatch — каждый sub-container последовательно poll + processes. Async dispatch требует `@Async` на listener метод (с большими caveats для offset commit).
>
> **Откуда путаница:** `@Async` + thread pool — паттерн из Spring core. В Kafka concurrency — другая семантика: несколько independent consumers, не один shared task queue.
>
> **Если бы это было правдой:** offset commit стал бы непредсказуемым — нельзя commit пока тред не закончил async обработку. Реальность: dedicated consumer per thread решает это естественно.

## Q9. Как управлять offset коммитами?

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
>
> **Вопрос:** В чём разница между `AckMode.MANUAL` и `AckMode.MANUAL_IMMEDIATE`, и почему default `BATCH` опасен с long-running listeners?
>
> ---
>
> #### A) Различий нет — это просто алиасы одного режима для разных Spring версий — ❌ Неверно
>
> **Что на самом deal:** Два разных режима с разной семантикой. `MANUAL` — accumulate ack calls в pending list, commit при следующем `poll()`. `MANUAL_IMMEDIATE` — commit немедленно через `consumer.commitSync()` (или async).
>
> **Откуда путаница:** имена похожи. Но они дают разный performance vs durability trade-off.
>
> **Если бы это было правдой:** не было бы reason иметь два режима в API. Spring сохраняет оба именно потому что разница важна.
>
> ---
>
> #### B) `MANUAL_IMMEDIATE` асинхронный, `MANUAL` — синхронный — `MANUAL` блокирует listener до broker ack — ❌ Неверно
>
> **Что на самом деле:** Наоборот: `MANUAL_IMMEDIATE` делает `commitSync()` или `commitAsync()` сразу при `ack.acknowledge()` (блокирующий call к broker если sync). `MANUAL` — defer до следующего `poll()`, что даёт **batching** ack-ов и **меньше** broker round-trips.
>
> **Откуда путаница:** «immediate» звучит как «не ждёт». Но «immediate» здесь означает «без отлагательства до poll», что фактически вызывает MORE network calls.
>
> **Если бы это было правдой:** `MANUAL_IMMEDIATE` был бы лучше всегда — но он медленнее именно потому, что commits сразу.
>
> ---
>
> #### C) Default `BATCH` коммитит после каждой записи — производительность ниже, чем `MANUAL` — ❌ Неверно
>
> **Что на самом деле:** Default `BATCH` коммитит **после обработки batch-а из poll()** (одного `consumer.poll()` вызова), не после каждой записи. `RECORD` — это «после каждой записи». Поэтому `BATCH` (default) — высокий throughput, но при crash потеря batch unfinished записей.
>
> **Откуда путаница:** имя «BATCH» можно прочитать как «commit batch-ом» или «commit для batch» — путаница.
>
> **Если бы это было правдой:** default Spring Kafka был бы slowest mode — что не имело бы смысла как default.
>
> ---
>
> #### D) `MANUAL` — `ack.acknowledge()` добавляет offset в **pending list**, commit отложен до следующего `poll()` (batched ack — better throughput); `MANUAL_IMMEDIATE` — **немедленный** sync/async commit при каждом ack. Default `BATCH` коммитит **весь poll batch** после обработки последней записи в нём — при long-running listener crash посередине теряется уже сделанная работа всех предыдущих записей в batch (at-least-once) — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Все 7 AckMode мapping:
> | Mode | Когда commit | Trade-off |
> |------|-------------|-----------|
> | `RECORD` | После каждой записи | Slowest, lowest data loss |
> | `BATCH` (default) | После poll batch | Fast, batch loss на crash |
> | `TIME` | По таймеру (`ackTime`) | Predictable latency |
> | `COUNT` | После N записей | Tuned for throughput |
> | `COUNT_TIME` | По count ИЛИ time | Hybrid |
> | `MANUAL` | `ack.acknowledge()` + следующий poll | Programmatic, batched |
> | `MANUAL_IMMEDIATE` | `ack.acknowledge()` → commit сразу | Programmatic, immediate |
>
> Долгий listener при default `BATCH`: представь `poll()` вернул 50 записей, обработка каждой 1 сек. После записи #30 JVM crash → offset **не** committed (commit только после #50). После restart consumer перечитает все 50 — записи 1-30 обработаны дважды (at-least-once). Idempotent downstream спасает, но если processing — non-idempotent (например, charge card), это duplicate billing.
>
> Решение: `MANUAL` + ack после каждой successful записи → точечный commit, на crash потеряется максимум одна запись в обработке.
>
> **Пример:**
> ```java
> @Configuration
> public class AckModeConfig {
>     @Bean
>     public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> safeFactory(
>             ConsumerFactory<String, OrderEvent> cf) {
>         var factory = new ConcurrentKafkaListenerContainerFactory<String, OrderEvent>();
>         factory.setConsumerFactory(cf);
>         factory.getContainerProperties().setAckMode(AckMode.MANUAL);  // не IMMEDIATE — больше throughput
>         factory.getContainerProperties().setSyncCommits(true);        // sync commit (safer)
>         return factory;
>     }
> }
>
> @KafkaListener(topics = "orders", containerFactory = "safeFactory")
> public void handle(OrderEvent event, Acknowledgment ack) {
>     try {
>         processOrder(event);                  // не-идемпотентная операция
>         ack.acknowledge();                    // commit только после успеха
>     } catch (Exception e) {
>         log.error("Processing failed, will retry", e);
>         // не ack — DefaultErrorHandler сделает retry
>     }
> }
> ```
>
> **Когда применять:**
> - **`MANUAL`** (best default): non-idempotent processing с manual control — финансовые операции, charges, sends.
> - **`MANUAL_IMMEDIATE`**: критичные записи где crash после ack недопустим (например, single-event compliance audit).
> - **`BATCH`**: idempotent processing с high throughput — например, метрики aggregation.
> - **`TIME`/`COUNT`**: balance между latency и crash window — backend для analytics.
>
> **Подводные камни:**
> - **`AckMode.MANUAL` без ack никогда не коммитит** — bug «consumer перечитывает одни и те же записи бесконечно» обычно отсюда (`return` без ack).
> - **`enable-auto-commit=true` несовместим с MANUAL**: Spring выкинет exception при старте — явно выставить `enable-auto-commit=false` при MANUAL.
> - **`MANUAL_IMMEDIATE` + sync = blocking listener thread**: при медленном broker round-trip throughput падает 10x. Используй async или `MANUAL`.
> - **Out-of-order ack**: при concurrent processing записей одной партиции (`@Async`) ack может прийти не в порядке offset — Spring сохраняет highest, но window между ack-ами может быть data loss.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q3]] — `@KafkaListener` параметры; [[spring-kafka-interview#Q5]] — error handler vs ack interaction; [[spring-kafka-interview#Q7]] — `sendOffsetsToTransaction` в transactional context.

## Q10. Как работает сериализация/десериализация в Spring Kafka?

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
>
> **Вопрос:** Какая ключевая security-проблема с дефолтным `JsonDeserializer`, и какой параметр критичен в production?
>
> ---
>
> #### A) `JsonDeserializer` медленный — нужно использовать Kryo для скорости — ❌ Неверно
>
> **Что на самом деле:** скорость не главная проблема. **Безопасность** — критичнее. По умолчанию Jackson десериализует **любой** класс из payload, если есть type info в headers (`__TypeId__`). Атакующий может отправить malicious payload с `__TypeId__: org.apache.commons.collections.functors.InvokerTransformer` (CVE-2015-7501 series) → RCE при десериализации.
>
> Параметр `spring.json.trusted.packages` — whitelist разрешённых пакетов. Без него = security risk.
>
> **Откуда путаница:** «JSON медленный vs Protobuf» — частое сравнение. Но в Spring Kafka context security важнее performance для типичной нагрузки.
>
> **Если бы это было правдой:** все Spring Kafka apps использовали бы Avro/Protobuf по default. Реально JSON остаётся default — Spring добавил security guard вместо смены формата.
>
> ---
>
> #### B) `spring.json.trusted.packages` — whitelist пакетов разрешённых для deserialization; без него Jackson доверяет всем классам в payload, что = RCE-risk (gadget chain attacks); production setting: `com.mycompany.events.*`, никогда `*` — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring Kafka JsonDeserializer использует Jackson, который по умолчанию читает `__TypeId__` header (или resolved class из default type). При untrusted source (Kafka topic из external system, multi-tenant) это RCE-вектор:
>
> 1. Producer (compromised или malicious) шлёт payload с `__TypeId__: <gadget class>`.
> 2. Jackson инстанцирует класс — gadget chain вызывает `Runtime.exec()`.
> 3. На consumer'е выполняется arbitrary command.
>
> CVE post-mortems: Equifax 2017 (Apache Struts Java deserialization), Spring4Shell 2022.
>
> Защита:
> 1. **`trusted.packages`** — whitelist специфичных пакетов.
> 2. **`useTypeHeaders=false`** — игнорировать `__TypeId__` из headers, использовать known type (`value.default.type`).
> 3. **Avro/Protobuf** с Schema Registry — schema enforcement on broker level.
>
> **Пример:**
> ```yaml
> spring:
>   kafka:
>     consumer:
>       value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
>       properties:
>         spring.json.trusted.packages: "com.mycompany.events.*"  # whitelist
>         spring.json.use.type.headers: false                       # ignore __TypeId__
>         spring.json.value.default.type: com.mycompany.events.OrderEvent
> ```
>
> ```java
> // Programmatic для error-tolerant deserialization
> @Bean
> public ConsumerFactory<String, Object> consumerFactory() {
>     JsonDeserializer<Object> deserializer = new JsonDeserializer<>();
>     deserializer.addTrustedPackages("com.mycompany.events.*");
>     deserializer.setUseTypeHeaders(false);              // КРИТИЧНО для security
>     deserializer.setRemoveTypeHeaders(true);             // не пробрасываем дальше
>
>     // ErrorHandlingDeserializer — не убивает consumer на bad payload
>     ErrorHandlingDeserializer<Object> errorHandling =
>         new ErrorHandlingDeserializer<>(deserializer);
>     return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandling);
> }
> ```
>
> **Когда применять:** ВСЕГДА в production. Multi-tenant Kafka (где разные команды публикуют в общий cluster) — must.
>
> **Подводные камни:**
> - **`trusted.packages: "*"`** — отключает защиту. Никогда не делать в prod.
> - **Polymorphic types** (`@JsonTypeInfo`) ломаются если `useTypeHeaders=false` — нужен custom subtypes registration.
> - **`ErrorHandlingDeserializer` обёртка** обязательна, иначе bad payload убивает consumer thread → infinite restart loop.
> - **Schema evolution**: добавление optional field — OK; удаление required field ломает consumers. Confluent Schema Registry + backward/forward compat checks.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q3]] — @KafkaListener basics; [[spring-kafka-interview#Q5]] — DeserializationException handling; [[spring-kafka-interview#Q11]] — тестирование с EmbeddedKafka.
>
> ---
>
> #### C) JsonSerializer не подходит для production — нужен только Avro — ❌ Неверно
>
> **Что на самом деле:** JSON отлично работает в production многих компаний (Avito, Yandex, Wolt). Avro даёт преимущества: schema enforcement, compact binary, evolution checks — но требует Schema Registry infra. JSON проще, дебажится eyeballing, sufficient для internal events с rev'd contract.
>
> **Откуда путаница:** «Kafka best practice = Avro» — Confluent marketing. На практике выбор зависит от scale и compliance requirements.
>
> **Если бы это было правдой:** все Spring Kafka tutorials использовали бы Avro. Реально JSON — default в туториалах и работает.
>
> ---
>
> #### D) `JsonDeserializer` не работает с Spring Boot 3 — нужен `Jackson2JsonMessageConverter` — ❌ Неверно
>
> **Что на самом деле:** `JsonDeserializer` — primary path в Spring Kafka 3.x. `Jackson2JsonMessageConverter` — отдельный механизм для `KafkaTemplate` / `MessageHeaders` integration, не deserialization layer.
>
> **Откуда путаница:** оба связаны с Jackson + Spring Kafka. Но это разные слои API.
>
> **Если бы это было правдой:** breaking change documented в Spring Boot 3 release notes. Реально JsonDeserializer работает unchanged.

## Q11. Как тестировать Spring Kafka без реального брокера?

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
>
> **Вопрос:** EmbeddedKafka vs Testcontainers Kafka — что лучше для production-grade integration тестов и почему?
>
> ---
>
> #### A) EmbeddedKafka — быстрее (in-JVM), нет docker, лучше для CI — ✓ Верно (но с нюансом — нужен баланс)
>
> **Развёрнутое объяснение:**
>
> Оба инструмента для тестирования Kafka без production cluster. Trade-offs:
>
> | Аспект | EmbeddedKafka | Testcontainers Kafka |
> |---|---|---|
> | Startup time | ~3-5s | ~10-15s |
> | Docker required | Нет | Да |
> | Behavior fidelity | ~95% (in-JVM) | 100% (real Kafka) |
> | Memory usage | ~200MB (JVM heap) | ~500MB (container) |
> | Cluster scenarios | Single-broker | Multi-broker (compose) |
> | Network failures sim | Limited | Yes (toxiproxy) |
>
> **EmbeddedKafka подходит когда:**
> - Unit-ish integration tests (один consumer, простая логика)
> - CI с ограниченными ресурсами (нет Docker)
> - Тысячи тестов где speed критичен
>
> **Testcontainers Kafka подходит когда:**
> - End-to-end tests with multi-component setup (Schema Registry + Kafka + app)
> - Тестирование partition rebalancing, network partitions
> - Validation что app работает с **той же** Kafka version что в prod
>
> Production-grade подход — mix: EmbeddedKafka для unit-level (быстро, много тестов), Testcontainers для integration (medium count) + dedicated staging cluster для smoke tests.
>
> **Пример (EmbeddedKafka):**
> ```java
> @EmbeddedKafka(
>     partitions = 3,                          // multi-partition для testing ordering
>     topics = {"orders", "orders-dlt"},
>     brokerProperties = {
>         "log.cleaner.enable=false",           // disable log cleaner — speed
>         "auto.create.topics.enable=false"
>     }
> )
> @SpringBootTest
> class OrderConsumerTest {
>     @Autowired KafkaTemplate<String, OrderEvent> template;
>     @Autowired OrderService orderService;
>
>     @Test
>     void shouldProcessOrderEvent() throws Exception {
>         OrderEvent event = new OrderEvent(UUID.randomUUID(), "PENDING");
>         template.send("orders", event.orderId().toString(), event).get();
>
>         await().atMost(5, SECONDS)
>             .untilAsserted(() ->
>                 assertThat(orderService.findById(event.orderId())).isPresent());
>     }
> }
> ```
>
> **Когда применять:**
> - **Avito/Booking**: EmbeddedKafka для unit-level, Testcontainers для integration, staging cluster для E2E.
> - **CI optimization**: EmbeddedKafka в parallel test runs (по 100+ tests одновременно).
> - **Local dev**: Testcontainers — same Kafka version что в prod, no version drift bugs.
>
> **Подводные камни:**
> - **EmbeddedKafka не cleanup автоматически между тестами**: используй `@DirtiesContext` или `kafka.cleanup()` иначе state leaks между тестами.
> - **Random port conflicts**: `EmbeddedKafkaBroker` randomizes, но Spring Boot Test cache reuses context — `@DirtiesContext.AFTER_CLASS` обязателен для isolation.
> - **`await()` без timeout** — flaky tests. Всегда atMost(5-10s) с meaningful assertions.
> - **Kraft mode (KIP-500)**: новые версии Kafka работают без Zookeeper. EmbeddedKafka поддерживает с Spring Kafka 3.1+.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q3]] — @KafkaListener config; [[spring-kafka-interview#Q9]] — AckMode тестирование; [[spring-kafka-interview#Q13]] — ordering guarantees test.
>
> ---
>
> #### B) EmbeddedKafka не существует в Spring Kafka 3.x — удалено в favor of Testcontainers — ❌ Неверно
>
> **Что на самом деле:** EmbeddedKafka **активно поддерживается** в Spring Kafka 3.x. С Kraft mode (без Zookeeper) стало ещё быстрее. Удаление не планируется.
>
> **Откуда путаница:** Kafka 3.x deprecated Zookeeper — кажется что embedded Kafka тоже становится legacy. На деле просто Kraft под капотом.
>
> **Если бы это было правдой:** Spring Kafka docs убрали бы примеры EmbeddedKafka. Реально они актуальны.
>
> ---
>
> #### C) `@EmbeddedKafka` требует обязательно `@DirtiesContext` для каждого теста — ❌ Неверно (но рекомендуется)
>
> **Что на самом деле:** `@DirtiesContext` — recommended для test isolation, но не обязателен. Без него tests могут проходить, если осторожно cleanup'ить топики/groups. Но это fragile — типично используют `@DirtiesContext` или per-test unique topic names.
>
> **Откуда путаница:** многие туториалы показывают `@DirtiesContext` как обязательный — это best practice, не requirement.
>
> **Если бы это было правдой:** test suite размером 100+ tests был бы непрактичен (каждый тест = 5s startup × 100 = 500s test suite).
>
> ---
>
> #### D) MockProducer/MockConsumer достаточно — EmbeddedKafka избыточен — ❌ Неверно (в большинстве случаев)
>
> **Что на самом деле:** MockProducer/MockConsumer (Kafka Mock APIs) тестируют **логику обработки**, не **integration**. Без real broker нет:
> - Real serialization/deserialization (можно тестировать на mocks но не fidelity)
> - Partition rebalancing
> - Consumer group coordination
> - Spring Kafka container lifecycle (StartListener, AckMode, error handling)
>
> Mocks для unit tests (business logic). EmbeddedKafka для integration tests (Spring Kafka container + real Kafka behavior).
>
> **Откуда путаница:** mocks быстрее. Но они тестируют другой уровень — выбор инструмента зависит от scope теста.
>
> **Если бы это было правдой:** не было бы EmbeddedKafka в spring-kafka-test artifact.

## Q12. Что такое Kafka Streams в контексте Spring?

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
>
> **Вопрос:** В чём ключевое отличие Kafka Streams от обычного `@KafkaListener` consumer'а, и когда нужен Kafka Streams вместо Spring Kafka?
>
> ---
>
> #### A) Kafka Streams = `@KafkaListener` + автоматическая обработка в parallel threads — просто performance optimization — ❌ Неверно
>
> **Что на самом деле:** Kafka Streams — это **stateful stream processing framework**, не оптимизация `@KafkaListener`. Главные отличия:
> - **Local state stores** (RocksDB) — окна (windowed aggregations), join'ы между потоками — невозможно делать на consumer'е без external state.
> - **Exactly-once semantics** (EOS) — atomic write-back в output topics с read transaction.
> - **Topology DSL** (`KStream`/`KTable`/`GlobalKTable`) — declarative processing graph.
> - **Repartitioning** (через repartition topics) для join по non-key fields.
>
> @KafkaListener — простой consumer для message handling, не stream processing.
>
> **Откуда путаница:** оба читают Kafka topics. Но scope разный: consumer = single message handler, Streams = pipeline с state.
>
> **Если бы это было правдой:** Confluent не делал бы Streams отдельным продуктом. Spring Cloud Stream не имел бы separate `kafka-streams` binder.
>
> ---
>
> #### B) Kafka Streams для stateful обработки (windows, joins, aggregations) с local state stores (RocksDB) и exactly-once semantics; @KafkaListener — stateless message handler. Streams лучше для real-time analytics, ETL, CEP; @KafkaListener — для трансляции событий в commands — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> **@KafkaListener use cases:**
> - Прочитать message → вызвать method → ack
> - Простая трансляция event → command (например, OrderPlaced → SendEmail)
> - Stateless transformation (mapping/filtering без joins)
>
> **Kafka Streams use cases:**
> - **Aggregations**: count orders per customer last hour (windowed).
> - **Joins**: enrich OrderEvent с UserData via KTable join.
> - **Stateful transformations**: detect fraud patterns по последовательности событий.
> - **ETL pipelines**: real-time data warehouse populated from event streams.
>
> Spring integration через `@EnableKafkaStreams` + `StreamsBuilder`:
>
> ```java
> @Configuration
> @EnableKafkaStreams
> public class StreamConfig {
>
>     @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
>     public KafkaStreamsConfiguration streamsConfig() {
>         return new KafkaStreamsConfiguration(Map.of(
>             StreamsConfig.APPLICATION_ID_CONFIG, "orders-processor",
>             StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
>             StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once_v2",
>             StreamsConfig.STATE_DIR_CONFIG, "/var/kafka-streams"
>         ));
>     }
>
>     @Bean
>     public KStream<String, OrderEvent> topology(StreamsBuilder builder) {
>         // KStream — поток событий
>         KStream<String, OrderEvent> orders = builder.stream("orders");
>
>         // KTable — обновляющаяся таблица (compacted topic)
>         KTable<String, Customer> customers = builder.table("customers");
>
>         // Stream-Table join: обогащаем event данными о клиенте
>         orders
>             .join(customers,
>                   (order, customer) -> new EnrichedOrder(order, customer))
>             .filter((k, v) -> v.customer().tier() == VIP)
>             .to("vip-orders");
>
>         // Windowed aggregation: count orders per customer 1h window
>         orders
>             .groupBy((k, v) -> v.customerId())
>             .windowedBy(TimeWindows.of(Duration.ofHours(1)))
>             .count()
>             .toStream()
>             .to("orders-per-hour");
>
>         return orders;
>     }
> }
> ```
>
> **Когда применять:**
> - **Real-time analytics**: Yandex Music — count plays per artist, Netflix — recommendations updated by viewing events.
> - **Fraud detection**: Wolt — sequence detection (multiple orders from same IP в коротком окне).
> - **ETL**: Avito — events → real-time data warehouse update.
> - **CEP (Complex Event Processing)**: detection patterns на streams (transaction A followed by transaction B within 5 min = suspicious).
>
> **Подводные камни:**
> - **RocksDB state size** — на больших aggregations может вырасти до GB. State sized как `keys × windows`.
> - **Rebalancing pause** — при scale-out partition reassignment вызывает app pause (до минут на больших state).
> - **State restoration** — при restart Streams читает changelog topics для восстановления state. Большой state = slow startup.
> - **`exactly_once_v2`** требует Kafka 2.5+ broker.
> - **Spring Cloud Stream binder** — declarative альтернатива, но meno гибкая чем raw Streams DSL.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q3]] — @KafkaListener basics; [[spring-kafka-interview#Q14]] — idempotent producer; [[spring-kafka-interview#Q13]] — ordering для stateful processing.
>
> ---
>
> #### C) Kafka Streams не работает в Spring Boot — нужен Confluent Platform отдельно — ❌ Неверно
>
> **Что на самом деле:** Spring Kafka имеет first-class integration через `@EnableKafkaStreams` и `StreamsBuilder`. Не нужен Confluent Platform — стандартный Kafka broker достаточен.
>
> **Откуда путаница:** Confluent — vendor для Kafka, многие enterprise features (Schema Registry, KSQL) require Confluent. Но Kafka Streams — open source часть Apache Kafka.
>
> **Если бы это было правдой:** Spring Cloud Stream не имел бы kafka-streams binder.
>
> ---
>
> #### D) Streams — устаревшая технология, замещается Apache Flink — ❌ Неверно
>
> **Что на самом деле:** Kafka Streams и Apache Flink — комплементарные tools, не конкуренты:
> - **Streams**: библиотека внутри app, deployed as standalone JVM. Tightly coupled с Kafka.
> - **Flink**: separate cluster, supports multiple sources/sinks (Kafka, Kinesis, files), more advanced windowing.
>
> Streams лучше для Kafka-only pipelines с simpler topology. Flink — для cross-system orchestration с complex CEP.
>
> Оба активно развиваются и используются в production у LinkedIn, Uber, Yelp.
>
> **Откуда путаница:** Flink хайповее в последние годы. Но Streams не устарел.
>
> **Если бы это было правдой:** Confluent перестал бы инвестировать в Streams. Реально регулярные releases (Streams 3.x в 2024).

## Q13. Какие стратегии обеспечения порядка сообщений есть в Kafka?

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
>
> **Вопрос:** Какая стратегия ordering в Kafka — самая практичная для production, и какие trade-offs она имеет?
>
> ---
>
> #### A) Single partition per topic — простой способ обеспечить strict ordering — ❌ Неверно (для production)
>
> **Что на самом деле:** один partition действительно даёт strict ordering, но **жертвует throughput**. Кафка масштабируется через partitions: один partition = один consumer instance (в одной group), throughput limited single-thread. Для большинства production workloads это unacceptable: 1000 RPS — потолок одного partition.
>
> Подходит только для **low-volume strict-ordered** scenarios (audit log, ledger без partitioning ключа).
>
> **Откуда путаница:** «strict ordering» звучит как best practice. На деле это business requirement, а не tech default.
>
> **Если бы это было правдой:** все Kafka topics имели бы 1 partition. Реально prod-топики имеют 6-100+ partitions.
>
> ---
>
> #### B) Partition by key (e.g., customer_id) — даёт ordering per key, parallelism между keys; production-стандарт для большинства событийных систем — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Kafka гарантирует ordering **per partition**, не globally. Если все события для одного entity попадают в одну partition (через consistent hash key), они обрабатываются по порядку. Между разными entities — parallelism.
>
> Это **partition by key** strategy:
> ```java
> // Producer: explicit key
> kafkaTemplate.send("orders", order.getCustomerId(), orderEvent);
> //                  topic      key (определяет partition)  value
> ```
>
> Hash(customer_id) % partitions определяет target partition. Все события customer_id=42 идут в одну partition → strict ordering для этого customer'а. Разные customers распределяются по partitions → throughput linearly scales с partition count.
>
> **Пример (Spring Kafka):**
> ```java
> // Producer side
> @Service
> public class OrderEventPublisher {
>     @Autowired KafkaTemplate<String, OrderEvent> template;
>
>     public void publish(OrderEvent event) {
>         template.send("orders",
>             event.customerId(),     // KEY: customerId
>             event);                 // ordering preserved per customer
>     }
> }
>
> // Consumer side — concurrency настраивается по partition count
> @KafkaListener(
>     topics = "orders",
>     concurrency = "10"               // 10 consumer threads
> )
> public void handle(OrderEvent event) {
>     // Spring assigns partitions to threads;
>     // events of one customer всегда на одном thread.
> }
> ```
>
> **Когда применять:**
> - **E-commerce**: события одного order/customer должны обрабатываться по порядку (placed → paid → shipped), но между customers — parallel.
> - **Banking**: транзакции одного аккаунта по порядку.
> - **IoT**: события одного устройства по порядку, но устройств — миллионы.
> - **Wolt/Avito**: order lifecycle events keyed by order_id.
>
> **Подводные камни:**
> - **Hot keys**: если 80% событий — один customer (например, b2b client с миллионами orders), partition становится hot, throughput limited.
> - **Repartitioning**: при увеличении partition count hash mapping меняется — старые ключи могут попасть в другие partitions, ordering breaks. Использовать sticky partitioner или явный partition mapping.
> - **`max.in.flight.requests.per.connection > 1` + retries** ломает ordering при failures (out-of-order retry). С `enable.idempotence=true` Kafka сохраняет ordering автоматически.
> - **Cross-partition ordering**: НЕ гарантировано. Если бизнес-логика требует global ordering, partition by key не работает — нужен single partition или event sourcing с aggregator.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q12]] — Kafka Streams reuse partition strategy; [[spring-kafka-interview#Q14]] — idempotent producer для ordering safety; [[spring-kafka-interview#Q5]] — error handler не должен ломать ordering.
>
> ---
>
> #### C) Sequence numbers в headers — клиент сам сортирует на consumer side — ❌ Неверно (антипаттерн)
>
> **Что на самом деле:** consumer-side sorting через sequence numbers возможен, но создаёт сложности:
> - **Buffer overhead**: нужно держать out-of-order messages в памяти до закрытия gap.
> - **Stuck consumers**: если message #5 lost (или ну delayed), всё после него blocked.
> - **State complexity**: cross-batch state, restart recovery.
>
> На практике это **last-resort** когда partition by key невозможен (например, no natural key). Простой partition by key решает 95% случаев без этого complexity.
>
> **Откуда путаница:** Cassandra и другие eventually-consistent systems используют sequence numbers. Но Kafka built-in primitive — partition ordering, не cross-partition reconciliation.
>
> **Если бы это было правдой:** ordering был бы distributed problem, Kafka not different from generic message queue.
>
> ---
>
> #### D) Acks=all + retries=Integer.MAX_VALUE гарантируют ordering — ❌ Неверно
>
> **Что на самом деле:** `acks=all` + retries — это про **durability** (записано на N replicas), не **ordering**. Retries сами по себе ЛОМАЮТ ordering: message #1 fails, retried, message #2 succeeds first → arrived out of order on broker.
>
> Для сохранения ordering с retries нужен `enable.idempotence=true` (с Kafka 0.11+), который добавляет sequence numbers per producer на broker level.
>
> **Откуда путаница:** durability и ordering часто упоминаются вместе как «надёжность». Семантически они разные.
>
> **Если бы это было правдой:** не было бы документации Confluent про `enable.idempotence` и его требований (max.in.flight ≤ 5, retries > 0, acks=all).

## Q14. Как настроить idempotent producer?

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
>
> **Вопрос:** Что **не** гарантирует idempotent producer (`enable.idempotence=true`), и в чём отличие от Exactly-Once Semantics (EOS)?
>
> ---
>
> #### A) Idempotent producer = Exactly-Once delivery end-to-end — синонимы — ❌ Неверно
>
> **Что на самом деле:** idempotent producer гарантирует **at-most-once на broker level** — broker не примет дубль с тем же `(producer_id, sequence_number)`. НО:
> - Это **per-producer, per-session** — при restart producer'а ID меняется, дубли возможны.
> - Это **per-partition** — гарантия в рамках одной partition, не cross-partition.
> - Это **не покрывает consumer side** — consumer может прочитать одно сообщение несколько раз (например, после rebalance).
>
> Exactly-Once Semantics (EOS) — это **end-to-end** гарантия через transactions: producer atomically пишет в multiple partitions + commits consumer offsets. Это шире idempotent.
>
> **Откуда путаница:** оба связаны с avoiding duplicates. Но idempotent — частный случай (broker-side dedup), EOS — полный pipeline.
>
> **Если бы это было правдой:** не было бы separate `transactional.id` setting и `KafkaTransactionManager`.
>
> ---
>
> #### B) Idempotent producer гарантирует только broker-side dedup в пределах одной сессии producer'а; для full EOS нужны transactions (transactional.id, beginTransaction, sendOffsetsToTransaction); consumer-side обработка требует separate idempotency key — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Три уровня delivery semantics в Kafka:
>
> 1. **At-least-once** (default): возможны дубли.
>    ```yaml
>    enable.idempotence: false
>    acks: all
>    retries: 3
>    ```
>
> 2. **Idempotent producer**: dedup per-producer-session.
>    ```yaml
>    enable.idempotence: true   # auto: acks=all, retries=Integer.MAX_VALUE, max.in.flight≤5
>    ```
>    - Broker tracks (producer_id, sequence_number) per partition.
>    - Дубли при retry внутри сессии — отброшены.
>    - **Ограничение**: при producer restart новый producer_id → дубли возможны.
>
> 3. **Exactly-Once Semantics (EOS)**: end-to-end через transactions.
>    ```yaml
>    transactional.id: orders-producer-tx-1
>    isolation.level: read_committed   # consumer side
>    ```
>    - Producer atomically commits в multiple partitions.
>    - `sendOffsetsToTransaction` — atomically commits offsets вместе с output messages.
>    - Consumer с `read_committed` видит только committed messages.
>
> **Пример (EOS pattern — read-process-write):**
> ```java
> @Bean
> public KafkaTransactionManager<String, Object> ktm(ProducerFactory<String, Object> pf) {
>     return new KafkaTransactionManager<>(pf);
> }
>
> @Service
> public class OrderProcessor {
>     @Autowired KafkaTemplate<String, OrderEvent> template;
>     @Autowired KafkaTemplate<String, NotificationEvent> notifTemplate;
>
>     @KafkaListener(topics = "orders", containerFactory = "txFactory")
>     @Transactional("ktm")                        // ChainedKafkaTransactionManager
>     public void process(OrderEvent order) {
>         // 1. Read from "orders"
>         // 2. Process
>         OrderResult result = service.process(order);
>         // 3. Write to "results" (atomically with offset commit)
>         template.send("results", result);
>         // 4. Write to "notifications" (atomically)
>         notifTemplate.send("notifications", buildNotif(result));
>         // Все 3 step'a — atomically. Crash → ничего не commit'итс.
>     }
> }
> ```
>
> **Когда применять:**
> - **Idempotent producer**: ДОЛЖЕН быть включён в любом production Kafka producer'е (нет downside).
> - **Transactions + EOS**: read-process-write pipelines (Kafka Streams делает это автоматически), financial systems, exactly-once requirements.
> - **At-least-once + idempotent consumer logic**: most pragmatic — idempotency на app level (например, idempotency key в DB unique constraint).
>
> **Подводные камни:**
> - **`enable.idempotence=true` + `acks=1`** — exception при старте. Idempotence требует `acks=all`.
> - **`max.in.flight.requests > 5`** — exception. Idempotence требует ≤ 5 для maintaining ordering.
> - **Transactions hurt throughput** — 5-10x latency overhead. Используй только когда EOS критичен.
> - **Producer restart with same `transactional.id`** — fences старого producer'а. Если ID не уникален между instances — split-brain.
> - **Consumer `read_committed`** добавляет latency (ждёт commit для visibility).
>
> **Связанные вопросы:** [[spring-kafka-interview#Q8]] — KafkaTransactionManager basics; [[spring-kafka-interview#Q9]] — AckMode interplay с transactions; [[spring-kafka-interview#Q13]] — ordering с idempotent producer.
>
> ---
>
> #### C) Idempotence не нужна — современные Kafka brokers сами dedup'ят — ❌ Неверно
>
> **Что на самом деле:** broker dedup работает **только при enabled idempotence** (sequence numbers required). Без него broker не имеет способа distinguish original message от retry — оба valid sends.
>
> **Откуда путаница:** «broker умный» — желаемое, не реальность. Без protocol-level dedup (sequence numbers) задача невозможна.
>
> **Если бы это было правдой:** `enable.idempotence` был бы default `true` с Kafka 0.11+. Реально стал default только с Kafka 3.0+.
>
> ---
>
> #### D) Idempotence работает только с Avro/Protobuf, не с JSON — ❌ Неверно
>
> **Что на самом деле:** idempotence — это **wire protocol level** (producer_id + sequence_number в записываемых batches). Сериализация payload (JSON/Avro/Protobuf) — orthogonal. Idempotence работает с любой сериализацией.
>
> **Откуда путаница:** Avro/Protobuf часто упоминаются как «production-grade» — экстраполяция «production-grade features only with them».
>
> **Если бы это было правдой:** компании использующие JSON Kafka topics не могли бы получить idempotence. На практике все используют.

## Q15. Как работает Pause/Resume для @KafkaListener?

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

> [!mcq]
>
> **Вопрос:** Зачем нужен pause/resume `@KafkaListener` в production, и какие нюансы поведения важны?
>
> ---
>
> #### A) Pause/resume — это альтернатива rate limiting, лучше чем @RateLimit — ❌ Неверно
>
> **Что на самом деле:** pause/resume — **explicit consumer control** для backpressure scenarios, не rate limiting. Rate limiter (Bucket4j, Resilience4j) ограничивает throughput на app level. Pause полностью **останавливает** consumption — нет сообщений из Kafka до resume.
>
> Они комплементарны: rate limiter для smooth throttling, pause/resume для backoff на overload или maintenance.
>
> **Откуда путаница:** оба «replace flow». Но pause = stop, rate limit = slow.
>
> **Если бы это было правдой:** @RateLimit был бы избыточен в Spring. Реально оба сосуществуют.
>
> ---
>
> #### B) Pause/resume для: 1) backpressure при downstream деградации (БД медленнее), 2) graceful maintenance (drain in-flight), 3) circuit breaker logic. Pause **не теряет** записей (broker hold), но во время паузы heartbeats продолжаются → нет rebalance — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Behaviour деталей:
>
> 1. **`container.pause()`** — флаг для consumer'а. После текущего poll() consumer не делает следующий poll, но **heartbeats продолжаются** (через background thread в Kafka client) → consumer остаётся в group, нет rebalance.
>
> 2. **Сообщения не теряются** — broker держит их в topic (TTL = retention policy). Resume → consumer продолжает с last committed offset.
>
> 3. **In-flight messages** обрабатываются до конца — pause не interrupts текущий handler.
>
> 4. **`consumer.pause(partitions)`** (raw API) — pause specific partitions, polynomial consumer keeps polling other partitions.
>
> **Пример (production circuit breaker pattern):**
> ```java
> @Service
> public class OrderConsumerControl {
>     @Autowired KafkaListenerEndpointRegistry registry;
>     @Autowired DatabaseHealthMonitor dbHealth;
>
>     @Scheduled(fixedDelay = 10_000)
>     public void checkDownstreamHealth() {
>         MessageListenerContainer container =
>             registry.getListenerContainer("ordersContainer");
>
>         if (dbHealth.isUnhealthy() && container.isRunning() && !container.isPauseRequested()) {
>             log.warn("DB unhealthy, pausing Kafka consumer");
>             container.pause();
>         } else if (dbHealth.isHealthy() && container.isPauseRequested()) {
>             log.info("DB recovered, resuming Kafka consumer");
>             container.resume();
>         }
>     }
> }
>
> // Альтернатива — Resilience4j Circuit Breaker
> @KafkaListener(id = "ordersContainer", topics = "orders", containerFactory = "txFactory")
> public void handle(OrderEvent event) {
>     try {
>         circuitBreaker.executeRunnable(() -> orderService.save(event));
>     } catch (CallNotPermittedException e) {
>         // CB open → pause consumer, wait for recovery
>         registry.getListenerContainer("ordersContainer").pause();
>         throw e;     // Spring retry/DLQ обработает
>     }
> }
> ```
>
> **Когда применять:**
> - **Downstream degradation**: БД медленнее обычного, downstream HTTP API throttles 503 — pause до recovery.
> - **Graceful shutdown**: при rolling deployment — pause перед SIGTERM, drain in-flight, потом shutdown.
> - **Maintenance windows**: запланированный downtime downstream → pause перед окном, resume после.
> - **Batch processing throttle**: при memory pressure → pause до GC settling.
> - **Avito/Wolt**: pause consumers при degraded payments service вместо генерации тысячи failed orders.
>
> **Подводные камни:**
> - **Heartbeat timeout**: если pause longer than `session.timeout.ms` (default 45s, но depends on heartbeat-interval), consumer удаляется из group → rebalance при resume. Use `max.poll.interval.ms` > pause duration.
> - **Lag accumulation**: paused consumer накапливает lag в Prometheus метрик. Alert thresholds должны учитывать planned pauses.
> - **`pause()` is async**: takes effect после текущего poll(). На coarse-grained processing это может быть 1-2s latency.
> - **`partition.pause(...)` vs `container.pause()`**: container — все partitions, raw — selective.
> - **Не путать с `setAutoStartup(false)`**: эта property останавливает container полностью, requires `start()` через registry для resumption.
>
> **Связанные вопросы:** [[spring-kafka-interview#Q5]] — error handler без pause создаёт infinite retries; [[spring-kafka-interview#Q3]] — @KafkaListener lifecycle; [[spring-kafka-interview#Q9]] — AckMode interplay с pause.
>
> ---
>
> #### C) Pause/resume — deprecated в Kafka 3.x, заменён concurrent consumers — ❌ Неверно
>
> **Что на самом деле:** pause/resume — **core feature** Kafka client API, не deprecated. Concurrent consumers (через `concurrency=N`) — orthogonal механизм для parallel processing, не replacement для pause.
>
> **Откуда путаница:** concurrency хайповее обсуждается как «scaling». Pause/resume — про operational control, разные purposes.
>
> **Если бы это было правдой:** Spring Kafka API не имело бы методов pause/resume в `MessageListenerContainer`.
>
> ---
>
> #### D) Pause теряет сообщения — broker удаляет их после timeout — ❌ Неверно
>
> **Что на самом деле:** Kafka retention policy (default 7 days) определяет когда **broker** удаляет сообщения, не pause. Pause просто останавливает **consumer**, сообщения ждут на broker до resume.
>
> Единственный риск: если pause длится дольше retention (например, paused for 8 days с 7-day retention) — broker может удалить старые offset'ы, consumer пропустит at resume.
>
> **Откуда путаница:** «consumer не работает = сообщения теряются» — naive consumer/queue mental model. Kafka durable log не делает этого.
>
> **Если бы это было правдой:** pause был бы бессмысленным для production — нельзя было бы безопасно использовать.

## See also

- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka: partitions, offsets, consumer groups, delivery semantics
- [Spring Boot](spring-boot-interview.md) — auto-configuration, Spring Boot starters
- [Spring @Transactional](spring-transaction-interview.md) — транзакции Kafka + JPA через ChainedKafkaTransactionManager
- [Spring Retry](spring-retry-interview.md) — retry в Kafka listeners через DefaultErrorHandler
- [Spring Modulith](spring-modulith-interview.md) — ApplicationEvents как альтернатива Kafka в монолите
- [Spring WebFlux](spring-webflux-interview.md) — реактивный стек с ReactiveKafkaConsumerTemplate
- [Spring Testing](spring-testing-interview.md) — @EmbeddedKafka для интеграционных тестов
- [Message Brokers](../../messaging/message-brokers-comparison-interview.md) — сравнение Kafka, RabbitMQ, Pulsar
- [Spring Events](spring-events-interview.md) — ApplicationEventPublisher как лёгкая альтернатива
- [Resilience4j](resilience4j-interview.md) — circuit breaker для Kafka producers
