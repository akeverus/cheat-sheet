---
title: "Quarkus: Kafka - Reactive Messaging и Event Streaming"
description: "Полное руководство по работе с Kafka в Quarkus: reactive messaging, producers, consumers, serialization, error handling и best practices"
tags:
  - quarkus
  - kafka
  - reactive-messaging
  - event-streaming
  - messaging
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-reactive.md"]
next: ["quarkus-reactive.md", "quarkus-messaging.md"]
updated: "2026-02-11"
related: ["quarkus-reactive.md", "quarkus-messaging.md"]
---

# Quarkus: Kafka - Reactive Messaging и Event Streaming



## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Quarkus: Kafka - Reactive Messaging и Event Streaming](#quarkus-kafka-reactive-messaging-и-event-streaming)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Конфигурация Kafka](#конфигурация-kafka)
  - [Basic Configuration](#basic-configuration)
- [Kafka broker](#kafka-broker)
- [Consumer configuration](#consumer-configuration)
- [Producer configuration](#producer-configuration)
  - [Advanced Configuration](#advanced-configuration)
- [Consumer settings](#consumer-settings)
- [Producer settings](#producer-settings)
- [Message Producer](#message-producer)
  - [Basic Producer](#basic-producer)
  - [Producer с Payload](#producer-с-payload)
  - [Producer с Metadata](#producer-с-metadata)
- [Message Consumer](#message-consumer)
  - [Basic Consumer](#basic-consumer)
  - [Reactive Consumer](#reactive-consumer)
  - [Consumer с Message](#consumer-с-message)
- [Serialization](#serialization)
  - [JSON Serialization](#json-serialization)
- [application.properties](#applicationproperties)
  - [Custom Serializer](#custom-serializer)
  - [Custom Deserializer](#custom-deserializer)
- [Error Handling](#error-handling)
  - [Dead Letter Queue](#dead-letter-queue)
  - [Retry Strategy](#retry-strategy)
  - [Custom Error Handler](#custom-error-handler)
- [Partitioning](#partitioning)
  - [Partition Assignment](#partition-assignment)
  - [Key-based Partitioning](#key-based-partitioning)
- [Testing](#testing)
  - [Testing Producers](#testing-producers)
  - [Testing Consumers](#testing-consumers)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте reactive messaging](#1-используйте-reactive-messaging)
  - [2. Обрабатывайте ошибки](#2-обрабатывайте-ошибки)
- [✅ Хорошо](#хорошо)
  - [3. Настраивайте serialization](#3-настраивайте-serialization)
  - [4. Используйте ключи для партиционирования](#4-используйте-ключи-для-партиционирования)
  - [5. Настраивайте retry и timeout](#5-настраивайте-retry-и-timeout)
- [Advanced Kafka Patterns](#advanced-kafka-patterns)
  - [Exactly-Once Semantics](#exactly-once-semantics)
  - [Transactional Producers](#transactional-producers)
  - [Consumer Groups](#consumer-groups)
  - [Offset Management](#offset-management)
- [Kafka Streams](#kafka-streams)
  - [Stream Processing](#stream-processing)
- [Schema Registry](#schema-registry)
  - [Avro Serialization](#avro-serialization)
  - [JSON Schema](#json-schema)
- [Monitoring и Metrics](#monitoring-и-metrics)
  - [Kafka Metrics](#kafka-metrics)
  - [Custom Metrics](#custom-metrics)
- [Error Recovery](#error-recovery)
  - [Retry with Exponential Backoff](#retry-with-exponential-backoff)
  - [Circuit Breaker](#circuit-breaker)
  - [6. Используйте transactions для exactly-once](#6-используйте-transactions-для-exactly-once)
  - [7. Мониторьте производительность](#7-мониторьте-производительность)
  - [Event Sourcing](#event-sourcing)
  - [CQRS Pattern](#cqrs-pattern)
  - [Saga Pattern](#saga-pattern)
- [Kafka Performance Tuning](#kafka-performance-tuning)
  - [Producer Performance](#producer-performance)
- [Batch settings](#batch-settings)
- [Compression](#compression)
- [Buffer memory](#buffer-memory)
  - [Consumer Performance](#consumer-performance)
- [Fetch settings](#fetch-settings)
- [Max poll records](#max-poll-records)
- [Session timeout](#session-timeout)
  - [Partitioning Strategy](#partitioning-strategy)
- [Kafka Monitoring](#kafka-monitoring)
  - [Consumer Lag Monitoring](#consumer-lag-monitoring)
  - [Throughput Monitoring](#throughput-monitoring)
- [Error Recovery Patterns](#error-recovery-patterns)
  - [Circuit Breaker Pattern](#circuit-breaker-pattern)
  - [Dead Letter Queue Pattern](#dead-letter-queue-pattern)
- [DLQ configuration](#dlq-configuration)
- [Kafka Streams Integration](#kafka-streams-integration)
  - [Windowed Aggregations](#windowed-aggregations)
- [Kafka Schema Evolution](#kafka-schema-evolution)
  - [Schema Compatibility](#schema-compatibility)
  - [Schema Versioning](#schema-versioning)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Quarkus** предоставляет мощную интеграцию с **Apache Kafka** через **SmallRye Reactive Messaging**. Это позволяет создавать **event-driven** приложения с поддержкой **reactive streams** и неблокирующей обработки сообщений.

### Основные возможности

- **Reactive Messaging**: Асинхронная обработка сообщений
- **Producers и Consumers**: Отправка и получение сообщений
- **Serialization**: Поддержка различных форматов
- **Error Handling**: Обработка ошибок и **retry**
- **Partitioning**: Управление партициями

## Конфигурация **Kafka**

### **Basic Configuration**

**application.properties:**

```properties
# Kafka broker
kafka.bootstrap.servers=localhost:9092

# Consumer configuration
mp.messaging.incoming.events.connector=smallrye-kafka
mp.messaging.incoming.events.topic=events
mp.messaging.incoming.events.group.id=my-group

# Producer configuration
mp.messaging.outgoing.events.connector=smallrye-kafka
mp.messaging.outgoing.events.topic=events
```

### **Advanced Configuration**

```properties
# Consumer settings
mp.messaging.incoming.events.auto.offset.reset=earliest
mp.messaging.incoming.events.enable.auto.commit=true
mp.messaging.incoming.events.max.poll.records=500

# Producer settings
mp.messaging.outgoing.events.acks=all
mp.messaging.outgoing.events.retries=3
mp.messaging.outgoing.events.max.in.flight.requests.per.connection=5
```

## Message Producer

### **Basic Producer**

```java
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventProducer {
    
    @Outgoing("events")
    public Multi<String> produceEvents() {
        return Multi.createFrom().items(
            "Event 1",
            "Event 2",
            "Event 3"
        );
    }
}
```

### **Producer** с **Payload**

```java
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.messaging.Message;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserEventProducer {
    
    @Outgoing("user-events")
    public Multi<Message<User>> produceUserEvents() {
        return Multi.createFrom().items(
            Message.of(new User("John", "john@example.com")),
            Message.of(new User("Jane", "jane@example.com"))
        );
    }
}
```

### **Producer** с **Metadata**

```java
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.messaging.Message;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class MetadataProducer {
    
    @Outgoing("events")
    public Multi<Message<String>> produceWithMetadata() {
        return Multi.createFrom().items(
            Message.of("Event 1")
                .addMetadata(OutgoingKafkaRecordMetadata.builder()
                    .withKey("key1")
                    .withPartition(0)
                    .withHeaders(Map.of("header1", "value1"))
                    .build())
        );
    }
}
```

## Message Consumer

### **Basic Consumer**

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventConsumer {
    
    @Incoming("events")
    public void consumeEvent(String event) {
        System.out.println("Received event: " + event);
        processEvent(event);
    }
    
    private void processEvent(String event) {
        // Обработка события
    }
}
```

### **Reactive Consumer**

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReactiveEventConsumer {
    
    @Incoming("events")
    public Uni<Void> consumeEventReactive(String event) {
        return Uni.createFrom().item(event)
            .onItem().transform(this::processEvent)
            .onFailure().retry().withBackOff(Duration.ofSeconds(1))
            .replaceWithVoid();
    }
}
```

### **Consumer** с **Message**

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageConsumer {
    
    @Incoming("events")
    public CompletionStage<Void> consumeMessage(Message<String> message) {
        String payload = message.getPayload();
        IncomingKafkaRecordMetadata<String, String> metadata = 
            message.getMetadata(IncomingKafkaRecordMetadata.class)
                .orElseThrow();
        
        System.out.println("Topic: " + metadata.getTopic());
        System.out.println("Partition: " + metadata.getPartition());
        System.out.println("Offset: " + metadata.getOffset());
        
        return message.ack();
    }
}
```

## Serialization

### **JSON Serialization**

```properties
# application.properties
mp.messaging.incoming.events.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
mp.messaging.outgoing.events.value.serializer=org.apache.kafka.common.serialization.StringSerializer
```

### **Custom Serializer**

```java
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserSerializer implements Serializer<User> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public byte[] serialize(String topic, User user) {
        try {
            return objectMapper.writeValueAsBytes(user);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing User", e);
        }
    }
}
```

### **Custom Deserializer**

```java
import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserDeserializer implements Deserializer<User> {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public User deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, User.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing User", e);
        }
    }
}
```

## Error Handling

### **Dead Letter Queue**

```properties
# application.properties
mp.messaging.incoming.events.failure-strategy=dead-letter-queue
mp.messaging.incoming.events.dead-letter-queue.topic=events-dlq
```

### **Retry Strategy**

```properties
# application.properties
mp.messaging.incoming.events.failure-strategy=retry
mp.messaging.incoming.events.retry.attempts=3
mp.messaging.incoming.events.retry.delay=1s
```

### **Custom Error Handler**

```java
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ErrorHandlingConsumer {
    
    @Incoming("events")
    public CompletionStage<Void> consumeWithErrorHandling(Message<String> message) {
        try {
            processMessage(message.getPayload());
            return message.ack();
        } catch (Exception e) {
            logError(e, message);
            return message.nack(e);
        }
    }
}
```

## Partitioning

### **Partition Assignment**

```properties
# application.properties
mp.messaging.incoming.events.partition=0
```

### **Key-based Partitioning**

```java
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.messaging.Message;
import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class PartitionedProducer {
    
    @Outgoing("events")
    public Multi<Message<String>> produceWithPartitioning() {
        return Multi.createFrom().items(
            Message.of("Event 1")
                .addMetadata(OutgoingKafkaRecordMetadata.builder()
                    .withKey("key1")
                    .build()),
            Message.of("Event 2")
                .addMetadata(OutgoingKafkaRecordMetadata.builder()
                    .withKey("key2")
                    .build())
        );
    }
}
```

## Testing

### **Testing Producers**

```java
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;

@QuarkusTest
public class ProducerTest {
    
    @Inject
    @Any
    InMemoryConnector connector;
    
    @Test
    void testProducer() {
        // Тестирование producer
        InMemorySource<String> source = connector.source("events");
        source.send("Test event");
    }
}
```

### **Testing Consumers**

```java
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import io.smallrye.reactive.messaging.providers.connectors.InMemoryConnector;

@QuarkusTest
public class ConsumerTest {
    
    @Inject
    @Any
    InMemoryConnector connector;
    
    @Test
    void testConsumer() {
        InMemorySink<String> sink = connector.sink("events");
        sink.send("Test event");
        
        // Проверка обработки
        assertThat(sink.received()).hasSize(1);
    }
}
```

## Лучшие практики

### 1. Используйте **reactive messaging**

```java
// ✅ Хорошо
@Incoming("events")
public Uni<Void> consumeReactive(String event) {
    return processAsync(event).replaceWithVoid();
}
```

### 2. Обрабатывайте ошибки

```properties
# ✅ Хорошо
mp.messaging.incoming.events.failure-strategy=dead-letter-queue
```

### 3. Настраивайте **serialization**

```properties
# ✅ Хорошо
mp.messaging.incoming.events.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

### 4. Используйте ключи для партиционирования

```java
// ✅ Хорошо
Message.of(event)
    .addMetadata(OutgoingKafkaRecordMetadata.builder()
        .withKey(key)
        .build())
```

### 5. Настраивайте **retry** и **timeout**

```properties
# ✅ Хорошо
mp.messaging.incoming.events.retry.attempts=3
mp.messaging.incoming.events.retry.delay=1s
```

## Advanced Kafka Patterns

### **Exactly-Once Semantics**

**Обеспечение **exactly-once** семантики:**

```properties
# application.properties
mp.messaging.outgoing.events.enable.idempotence=true
mp.messaging.outgoing.events.transactional.id=my-transaction-id
```

### **Transactional Producers**

**Использование транзакционных **producers**:**

```java
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.messaging.Message;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionalProducer {
    
    @Outgoing("events")
    public Multi<Message<String>> produceTransactional() {
        return Multi.createFrom().items(
            Message.of("Event 1")
                .addMetadata(OutgoingKafkaRecordMetadata.builder()
                    .withKey("key1")
                    .build())
        );
    }
}
```

### **Consumer Groups**

**Настройка **consumer groups**:**

```properties
# application.properties
mp.messaging.incoming.events.group.id=my-consumer-group
mp.messaging.incoming.events.partition=0
```

### **Offset Management**

**Управление **offset**:**

```properties
# application.properties
mp.messaging.incoming.events.auto.offset.reset=earliest
mp.messaging.incoming.events.enable.auto.commit=false
mp.messaging.incoming.events.commit-strategy=latest
```

## Kafka Streams

### **Stream Processing**

**Обработка потоков данных:**

```java
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class StreamProcessor {
    
    @Produces
    public KStream<String, String> processStream(StreamsBuilder builder) {
        KStream<String, String> source = builder.stream("input-topic");
        
        return source
            .filter((key, value) -> value != null)
            .mapValues(value -> value.toUpperCase())
            .to("output-topic");
    }
}
```

## Schema Registry

### **Avro Serialization**

**Использование **Avro** с **Schema Registry**:**

```properties
# application.properties
mp.messaging.outgoing.events.value.serializer=io.confluent.kafka.serializers.KafkaAvroSerializer
mp.messaging.outgoing.events.schema.registry.url=http://localhost:8081
```

### **JSON Schema**

**Использование **JSON Schema**:**

```properties
# application.properties
mp.messaging.outgoing.events.value.serializer=io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer
mp.messaging.outgoing.events.schema.registry.url=http://localhost:8081
```

## Monitoring и Metrics

### **Kafka Metrics**

**Настройка метрик:**

```properties
# application.properties
quarkus.micrometer.enabled=true
quarkus.micrometer.export.prometheus.enabled=true
```

### **Custom Metrics**

**Создание кастомных метрик:**

```java
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KafkaMetrics {
    
    @Inject
    MeterRegistry registry;
    
    public void recordMessageProcessed(String topic) {
        registry.counter("kafka.messages.processed", "topic", topic).increment();
    }
}
```

## Error Recovery

### **Retry with Exponential Backoff**

**Retry** с экспоненциальной задержкой:**

```properties
# application.properties
mp.messaging.incoming.events.failure-strategy=retry
mp.messaging.incoming.events.retry.attempts=5
mp.messaging.incoming.events.retry.delay=1s
mp.messaging.incoming.events.retry.max-delay=30s
mp.messaging.incoming.events.retry.multiplier=2
```

### **Circuit Breaker**

**Использование **circuit breaker**:**

```java
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CircuitBreakerConsumer {
    
    @Incoming("events")
    @CircuitBreaker(
        requestVolumeThreshold = 10,
        failureRatio = 0.5,
        delay = 5000
    )
    public void consumeWithCircuitBreaker(String event) {
        processEvent(event);
    }
}
```

## Advanced Kafka Patterns

### **Event Sourcing**

**Реализация **Event Sourcing**:**

```java
@ApplicationScoped
public class EventSourcingService {
    
    @Channel("events")
    Emitter<Event> eventEmitter;
    
    public void publishEvent(Event event) {
        eventEmitter.send(Message.of(event)
            .addMetadata(OutgoingKafkaRecordMetadata.builder()
                .withKey(event.getAggregateId())
                .build()));
    }
}
```

### **CQRS Pattern**

**Реализация **CQRS**:**

```java
@ApplicationScoped
public class CommandHandler {
    
    @Channel("commands")
    Emitter<Command> commandEmitter;
    
    public void handleCommand(Command command) {
        commandEmitter.send(Message.of(command));
    }
}

@ApplicationScoped
public class QueryHandler {
    
    @Incoming("events")
    public void handleEvent(Event event) {
        // Обновление read model
        readModelService.update(event);
    }
}
```

### **Saga Pattern**

**Реализация **Saga pattern**:**

```java
@ApplicationScoped
public class SagaOrchestrator {
    
    @Channel("saga-events")
    Emitter<SagaEvent> sagaEventEmitter;
    
    public void startSaga(Saga saga) {
        sagaEventEmitter.send(Message.of(new SagaStartedEvent(saga)));
    }
    
    @Incoming("saga-events")
    public void handleSagaEvent(SagaEvent event) {
        if (event instanceof SagaStepCompleted) {
            processNextStep(event.getSagaId());
        } else if (event instanceof SagaStepFailed) {
            compensate(event.getSagaId());
        }
    }
}
```

## Kafka Performance Tuning

### **Producer Performance**

**Оптимизация производительности **producer**:**

```properties
# Batch settings
mp.messaging.outgoing.events.batch.size=16384
mp.messaging.outgoing.events.linger.ms=10

# Compression
mp.messaging.outgoing.events.compression.type=lz4

# Buffer memory
mp.messaging.outgoing.events.buffer.memory=33554432
```

### **Consumer Performance**

**Оптимизация производительности **consumer**:**

```properties
# Fetch settings
mp.messaging.incoming.events.fetch.min.bytes=1
mp.messaging.incoming.events.fetch.max.wait.ms=500

# Max poll records
mp.messaging.incoming.events.max.poll.records=500

# Session timeout
mp.messaging.incoming.events.session.timeout.ms=30000
```

### **Partitioning Strategy**

**Стратегия партиционирования:**

```java
@ApplicationScoped
public class PartitioningService {
    
    public String getPartitionKey(Event event) {
        // Использование агрегатного ID для партиционирования
        return event.getAggregateId();
    }
    
    public int getPartition(String key, int totalPartitions) {
        return Math.abs(key.hashCode()) % totalPartitions;
    }
}
```

## Kafka Monitoring

### **Consumer Lag Monitoring**

**Мониторинг **lag** потребителей:**

```java
@ApplicationScoped
public class LagMonitor {
    
    @Inject
    KafkaAdmin kafkaAdmin;
    
    public Uni<Long> getConsumerLag(String groupId, String topic) {
        return kafkaAdmin.describeConsumerGroups(List.of(groupId))
            .onItem().transform(groups -> {
                // Вычисление lag
                return calculateLag(groups, topic);
            });
    }
}
```

### **Throughput Monitoring**

**Мониторинг пропускной способности:**

```java
@ApplicationScoped
public class ThroughputMonitor {
    
    @Inject
    MeterRegistry registry;
    
    private final Counter messagesProcessed;
    
    public ThroughputMonitor(MeterRegistry registry) {
        this.registry = registry;
        this.messagesProcessed = Counter.builder("kafka.messages.processed")
            .description("Number of messages processed")
            .register(registry);
    }
    
    public void recordMessage() {
        messagesProcessed.increment();
    }
}
```

## Error Recovery Patterns

### **Circuit Breaker Pattern**

**Реализация **Circuit Breaker**:**

```java
@ApplicationScoped
public class CircuitBreakerService {
    
    @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5)
    public Uni<String> processWithCircuitBreaker(String message) {
        return processMessage(message);
    }
}
```

### **Dead Letter Queue Pattern**

**Использование **Dead Letter Queue**:**

```properties
# DLQ configuration
mp.messaging.incoming.events.failure-strategy=dead-letter-queue
mp.messaging.incoming.events.dead-letter-queue.topic=events-dlq
```

### **Retry with Exponential Backoff**

**Retry** с экспоненциальной задержкой:**

```properties
mp.messaging.incoming.events.retry.attempts=5
mp.messaging.incoming.events.retry.max-delay=30s
mp.messaging.incoming.events.retry.delay=1s
mp.messaging.incoming.events.retry.multiplier=2
```

## Kafka Streams Integration

### **Stream Processing**

**Обработка потоков:**

```java
@ApplicationScoped
public class StreamProcessor {
    
    @Incoming("input-stream")
    @Outgoing("output-stream")
    public Message<ProcessedEvent> process(Message<Event> message) {
        Event event = message.getPayload();
        ProcessedEvent processed = processEvent(event);
        return message.withPayload(processed);
    }
}
```

### **Windowed Aggregations**

**Оконные агрегации:**

```java
@ApplicationScoped
public class WindowedAggregation {
    
    @Incoming("events")
    @Outgoing("aggregated")
    public Multi<AggregatedResult> aggregate(Multi<Event> events) {
        return events
            .group().by(Event::getKey)
            .onItem().transform(group -> {
                return group
                    .collect().asList()
                    .map(this::aggregateEvents);
            })
            .merge();
    }
}
```

## Kafka Schema Evolution

### **Schema Compatibility**

**Совместимость схем:**

```properties
quarkus.kafka.schema.registry.url=http://localhost:8081
quarkus.kafka.schema.compatibility=BACKWARD
```

### **Schema Versioning**

**Версионирование схем:**

```java
@ApplicationScoped
public class SchemaVersioningService {
    
    public void publishWithSchema(Event event, int schemaVersion) {
        Message<Event> message = Message.of(event)
            .addMetadata(OutgoingKafkaRecordMetadata.builder()
                .withHeaders(Map.of("schema-version", String.valueOf(schemaVersion)))
                .build());
        eventEmitter.send(message);
    }
}
```


## Заключение

**Quarkus Kafka** предоставляет мощные инструменты для работы с **Apache Kafka**. Поддержка **reactive messaging**, **producers**, **consumers**, **serialization**, **error handling**, **partitioning**, **transactions**, **streams**, **schema registry**, **monitoring** и других продвинутых возможностей позволяет создавать эффективные **event-driven** приложения. Правильное использование **Kafka** паттернов, обработка ошибок, мониторинг и оптимизация производительности являются ключевыми аспектами создания надежных систем обработки событий.

## Дополнительные ресурсы

- [**Quarkus Kafka** Guide](https://quarkus.io/guides/kafka)
- [**SmallRye Reactive Messaging**](https://smallrye.io/docs/smallrye-reactive-messaging/)
- [**Apache Kafka** Documentation](https://kafka.apache.org/documentation/)
- [**Kafka Streams** Documentation](https://kafka.apache.org/documentation/streams/)
- [Confluent **Schema Registry**](https://docs.confluent.io/platform/current/schema-registry/index.html)
