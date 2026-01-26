---
title: "Micronaut: Kafka Integration - Producers, Consumers и Streams"
description: "Полное руководство по интеграции с Apache Kafka в Micronaut: producers, consumers, streams, transactions и best practices"
tags: ["micronaut", "kafka", "messaging", "streams", "producers", "consumers", "java", "kotlin"]
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-reactive.md"]
next: ["micronaut-reactive.md", "micronaut-cloud.md"]
updated: "2025-01-16"
related: ["micronaut-reactive.md", "micronaut-messaging.md"]
---

# Micronaut: Kafka Integration - Producers, Consumers и Streams

## Введение

Micronaut предоставляет отличную поддержку Apache Kafka через Micronaut Kafka. Это позволяет создавать высокопроизводительные приложения для обработки потоков данных с поддержкой producers, consumers, streams и транзакций.

### Основные возможности

- **Kafka Producers**: Отправка сообщений в Kafka
- **Kafka Consumers**: Получение сообщений из Kafka
- **Kafka Streams**: Обработка потоков данных
- **Transactions**: Транзакционная обработка
- **Error Handling**: Обработка ошибок
- **Serialization**: Сериализация/десериализация сообщений

## Настройка Kafka

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.kafka:micronaut-kafka")
    implementation("io.micronaut.kafka:micronaut-kafka-streams")
}
```

### Конфигурация

**application.yml:**

```yaml
kafka:
  bootstrap:
    servers: localhost:9092
  producer:
    key-serializer: org.apache.kafka.common.serialization.StringSerializer
    value-serializer: org.apache.kafka.common.serialization.StringSerializer
  consumer:
    group-id: my-group
    key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    auto-offset-reset: earliest
```

## Kafka Producers

### Basic Producer

```java
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageBody;
import io.micronaut.messaging.annotation.MessageHeader;

@KafkaClient
public interface UserProducer {
    
    @Topic("users")
    void sendUser(@MessageBody User user);
    
    @Topic("users")
    void sendUserWithKey(String key, @MessageBody User user);
    
    @Topic("users")
    void sendUserWithHeaders(
        @MessageBody User user,
        @MessageHeader("X-Custom-Header") String customHeader
    );
}
```

### Producer с Callback

```java
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;

@KafkaClient
public interface UserProducer {
    
    @Topic("users")
    void sendUser(@MessageBody User user, Callback callback);
}
```

### Использование Producer

```java
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private final UserProducer userProducer;
    
    public UserService(UserProducer userProducer) {
        this.userProducer = userProducer;
    }
    
    public void createUser(User user) {
        userRepository.save(user);
        userProducer.sendUser(user);
    }
}
```

## Kafka Consumers

### Basic Consumer

```java
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.messaging.annotation.MessageBody;
import io.micronaut.messaging.annotation.MessageHeader;

@KafkaListener(groupId = "user-consumer-group")
public class UserConsumer {
    
    @Topic("users")
    public void receiveUser(@MessageBody User user) {
        System.out.println("Received user: " + user.getName());
        // Обработка пользователя
    }
    
    @Topic("users")
    public void receiveUserWithKey(
        String key,
        @MessageBody User user,
        @MessageHeader("X-Custom-Header") String customHeader
    ) {
        System.out.println("Received user with key: " + key);
        // Обработка пользователя
    }
}
```

### Batch Consumer

```java
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.configuration.kafka.annotation.Batchable;

@KafkaListener(groupId = "batch-consumer-group")
public class BatchUserConsumer {
    
    @Topic("users")
    @Batchable
    public void receiveUsers(List<User> users) {
        System.out.println("Received " + users.size() + " users");
        // Batch обработка пользователей
    }
}
```

## Kafka Streams

### Stream Processing

```java
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

@Factory
public class UserStreamFactory {
    
    @Singleton
    public KStream<String, User> userStream(ConfiguredStreamBuilder streamBuilder) {
        KStream<String, User> stream = streamBuilder.stream("users");
        
        stream
            .filter((key, user) -> user.getAge() >= 18)
            .mapValues(user -> {
                user.setProcessed(true);
                return user;
            })
            .to("processed-users");
        
        return stream;
    }
}
```

### Stream Configuration

**application.yml:**

```yaml
kafka:
  streams:
    default:
      application-id: my-stream-app
      bootstrap:
        servers: localhost:9092
      default:
        key-serde: org.apache.kafka.common.serialization.Serdes$StringSerde
        value-serde: io.micronaut.configuration.kafka.serde.JsonSerde
```

## Transactions

### Transactional Producer

```java
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient(transactionalId = "user-producer")
public interface TransactionalUserProducer {
    
    @Topic("users")
    void sendUser(@MessageBody User user);
}
```

### Transactional Consumer

```java
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.transaction.annotation.Transactional;

@KafkaListener(groupId = "transactional-consumer-group")
public class TransactionalUserConsumer {
    
    @Topic("users")
    @Transactional
    public void receiveUser(@MessageBody User user) {
        userRepository.save(user);
        // Если произойдет ошибка, транзакция откатится
    }
}
```

## Error Handling

### Error Handler

```java
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.configuration.kafka.exceptions.DefaultKafkaListenerExceptionHandler;

@KafkaListener(
    groupId = "error-handler-group",
    exceptionHandler = DefaultKafkaListenerExceptionHandler.class
)
public class ErrorHandlingConsumer {
    
    @Topic("users")
    public void receiveUser(@MessageBody User user) {
        try {
            // Обработка пользователя
        } catch (Exception e) {
            // Обработка ошибки
            errorService.handleError(user, e);
        }
    }
}
```

### Dead Letter Queue

```java
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaListener(groupId = "dlq-consumer-group")
public class DeadLetterQueueConsumer {
    
    @Topic("users-dlq")
    public void handleDeadLetter(@MessageBody User user) {
        // Обработка сообщений из dead letter queue
        log.error("Failed to process user: {}", user);
    }
}
```

## Serialization

### Custom Serializer

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
            throw new RuntimeException("Error serializing user", e);
        }
    }
}
```

### Custom Deserializer

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
            throw new RuntimeException("Error deserializing user", e);
        }
    }
}
```

## Best Practices

### 1. Используйте правильные сериализаторы

```yaml
# ✅ Хорошо
kafka:
  producer:
    value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

### 2. Настраивайте consumer groups правильно

```yaml
# ✅ Хорошо
kafka:
  consumer:
    group-id: my-unique-group-id
```

### 3. Обрабатывайте ошибки

```java
// ✅ Хорошо
@KafkaListener(exceptionHandler = CustomExceptionHandler.class)
public class Consumer {
    // ...
}
```

### 4. Используйте batch processing для больших объемов

```java
// ✅ Хорошо
@Batchable
public void receiveUsers(List<User> users) {
    // Batch обработка
}
```

### 5. Настраивайте transactions для критических операций

```java
// ✅ Хорошо
@KafkaClient(transactionalId = "producer-id")
public interface Producer {
    // ...
}
```

## Kafka Streams Advanced

### Stream Processing with State

```java
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

@Factory
public class AdvancedStreamFactory {
    
    @Singleton
    public KStream<String, User> userStreamWithState(ConfiguredStreamBuilder streamBuilder) {
        KStream<String, User> stream = streamBuilder.stream("users");
        
        // Создание KTable для состояния
        KTable<String, User> userTable = streamBuilder.table("users",
            Materialized.as("user-store"));
        
        // Объединение stream и table
        stream.join(userTable, (streamUser, tableUser) -> {
            // Логика объединения
            return streamUser;
        });
        
        return stream;
    }
}
```

### Windowed Aggregations

```java
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

@Factory
public class WindowedStreamFactory {
    
    @Singleton
    public KStream<Windowed<String>, Long> windowedAggregation(
            ConfiguredStreamBuilder streamBuilder) {
        KStream<String, User> stream = streamBuilder.stream("users");
        
        return stream
            .groupByKey()
            .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
            .count()
            .toStream();
    }
}
```

## Kafka Connect Integration

### Kafka Connect Configuration

```yaml
kafka:
  connect:
    enabled: true
    url: http://localhost:8083
```

### Custom Connector

```java
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.source.SourceTask;

public class CustomSourceConnector extends SourceConnector {
    @Override
    public void start(Map<String, String> props) {
        // Инициализация коннектора
    }
    
    @Override
    public Class<? extends SourceTask> taskClass() {
        return CustomSourceTask.class;
    }
    
    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        // Конфигурация задач
        return Collections.emptyList();
    }
}
```

## Kafka Metrics

### Producer Metrics

```java
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import jakarta.inject.Singleton;

@Singleton
public class KafkaMetricsService {
    private final Counter messagesProduced;
    private final Counter messagesConsumed;
    
    public KafkaMetricsService(MeterRegistry meterRegistry) {
        this.messagesProduced = Counter.builder("kafka.messages.produced")
            .description("Number of messages produced")
            .register(meterRegistry);
        this.messagesConsumed = Counter.builder("kafka.messages.consumed")
            .description("Number of messages consumed")
            .register(meterRegistry);
    }
    
    public void recordProduced() {
        messagesProduced.increment();
    }
    
    public void recordConsumed() {
        messagesConsumed.increment();
    }
}
```

## Kafka Monitoring

### Consumer Lag Monitoring

```java
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import jakarta.inject.Singleton;

@Singleton
public class KafkaLagMonitor {
    private final Consumer<String, String> consumer;
    
    public KafkaLagMonitor(Consumer<String, String> consumer) {
        this.consumer = consumer;
    }
    
    public Map<TopicPartition, Long> getConsumerLag() {
        Map<TopicPartition, Long> lag = new HashMap<>();
        // Вычисление lag для каждого partition
        return lag;
    }
}
```

## Kafka Admin

### Topic Management

```java
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import jakarta.inject.Singleton;

@Singleton
public class KafkaAdminService {
    private final AdminClient adminClient;
    
    public KafkaAdminService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }
    
    public void createTopic(String topicName, int partitions, short replicationFactor) {
        NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
        adminClient.createTopics(Collections.singletonList(newTopic));
    }
    
    public void deleteTopic(String topicName) {
        adminClient.deleteTopics(Collections.singletonList(topicName));
    }
}
```

## Kafka Partitions

### Partition Assignment

```java
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import org.apache.kafka.common.TopicPartition;

@KafkaListener(groupId = "partitioned-consumer-group")
public class PartitionedConsumer {
    
    @Topic("users")
    public void receiveUser(@MessageBody User user, @Partition int partition) {
        System.out.println("Received user from partition: " + partition);
        // Обработка пользователя
    }
}
```

## Заключение

Micronaut Kafka предоставляет мощные инструменты для интеграции с Apache Kafka. Поддержка producers, consumers, streams, transactions, error handling, serialization, advanced stream processing, windowed aggregations, Kafka Connect, metrics, monitoring, admin operations, partitions и других продвинутых возможностей позволяет создавать высокопроизводительные приложения для обработки потоков данных.

## Дополнительные ресурсы

- [Micronaut Kafka Documentation](https://micronaut-projects.github.io/micronaut-kafka/latest/guide/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)
- [Kafka Connect Documentation](https://kafka.apache.org/documentation/#connect)
- [Kafka Monitoring](https://kafka.apache.org/documentation/#monitoring)

