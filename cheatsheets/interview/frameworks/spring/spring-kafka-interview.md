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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Как настроить KafkaTemplate и отправить сообщение? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Как написать Kafka Consumer с @KafkaListener? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Что такое Consumer Group и почему это важно? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Как обрабатывать ошибки в @KafkaListener? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Что такое Dead Letter Topic (DLT) и как его использовать? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Как работают транзакции в Spring Kafka? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Как настроить Kafka Listener для конкурентного чтения? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Как управлять offset коммитами? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Как работает сериализация/десериализация в Spring Kafka? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как тестировать Spring Kafka без реального брокера? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Что такое Kafka Streams в контексте Spring? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Какие стратегии обеспечения порядка сообщений есть в Kafka? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Как настроить idempotent producer? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Как работает Pause/Resume для @KafkaListener? Частая ошибка в реальном коде.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Apache Kafka](../../messaging/kafka-interview.md) — основы Kafka: partitions, offsets, consumer groups, delivery semantics
- [Spring Boot](spring-boot-interview.md) — auto-configuration, Spring Boot starters
- [Spring @Transactional](spring-transaction-interview.md) — транзакции Kafka + JPA через ChainedKafkaTransactionManager
- [Spring Retry](spring-retry-interview.md) — retry в Kafka listeners через DefaultErrorHandler
- [Spring Modulith](spring-modulith-interview.md) — ApplicationEvents как альтернатива Kafka в монолите
- [Spring WebFlux](spring-webflux-interview.md) — реактивный стек с ReactiveKafkaConsumerTemplate
- [Spring Testing](spring-testing-interview.md) — @EmbeddedKafka для интеграционных тестов
- [Message Brokers](../../messaging/message-brokers-comparison-interview.md) — сравнение Kafka, RabbitMQ, Pulsar
- [Spring Events](spring-events-interview.md) — ApplicationEventPublisher как лёгкая альтернатива
- [Resilience4j](resilience4j-interview.md) — circuit breaker для Kafka producers
