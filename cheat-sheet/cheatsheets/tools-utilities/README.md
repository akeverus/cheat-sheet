# Мессенджинг и События

Комплексное руководство по системам обмена сообщениями: Apache Kafka, RabbitMQ, Redis Pub/Sub, ActiveMQ. Архитектуры, паттерны, интеграция и best practices.

## Apache Kafka

### Основные Компоненты
- **[Основы](../messaging/kafka.md)** - Producers, Consumers, Topics, Partitions
- **ZooKeeper** - Coordination service
- **Kafka Streams** - Stream processing
- **Kafka Connect** - Data integration
- **Schema Registry** - Schema management

**Ключевые особенности Kafka:**
- Высокая пропускная способность (millions of messages per second)
- Горизонтальное масштабирование
- Persistent message storage
- Built-in partitioning, replication, fault-tolerance

### Продвинутые Возможности
- **Exactly-once semantics** - Гарантия обработки ровно один раз
- **Compacted topics** - Log compaction для state storage
- **Tiered storage** - Cost-effective long-term storage
- **Idempotent producers** - Duplicate message prevention

## RabbitMQ

### Архитектура
- **[Основы](../messaging/rabbitmq.md)** - Exchanges, Queues, Bindings
- **Exchange Types** - Direct, Topic, Headers, Fanout
- **Message Acknowledgments** - Manual ACK, Auto ACK
- **Dead Letter Exchanges** - Error handling
- **Priority Queues** - Message prioritization

**Преимущества RabbitMQ:**
- Flexible routing с множеством типов exchanges
- Built-in clustering и high availability
- Management UI для мониторинга
- Поддержка множества протоколов (AMQP, MQTT, STOMP)

### Расширенные Функции
- **Federation** - Message distribution across brokers
- **Shovel** - Message migration between brokers
- **Alternate Exchanges** - Fallback routing
- **Message TTL** - Time-to-live settings

## Redis Pub/Sub

### Возможности
- **Publish/Subscribe Pattern** - Fire-and-forget messaging
- **Channels и Patterns** - Flexible subscriptions
- **Message Persistence** - RDB/AOF snapshots
- **Clustering** - Distributed deployment

**Когда использовать Redis Pub/Sub:**
- Простые pub/sub сценарии
- Кеширование с уведомлениями
- Real-time notifications
- Lightweight messaging

## ActiveMQ

### Classic и Artemis
- **Classic ActiveMQ** - Traditional message broker
- **ActiveMQ Artemis** - Next-generation broker
- **JMS Support** - Java Message Service
- **Protocol Support** - OpenWire, AMQP, MQTT, STOMP

**Особенности ActiveMQ:**
- Full JMS 2.0 compliance
- Embedded broker для testing
- Network of brokers для scaling
- Message transformation

## Сравнение Систем

### По Характеристикам

#### Throughput и Latency
```
Kafka        High throughput (~millions msg/sec), Higher latency (~10-100ms)
RabbitMQ     Medium throughput (~10k-100k msg/sec), Low latency (~1-10ms)
Redis Pub/Sub Very high throughput, Very low latency (~1ms)
ActiveMQ     Medium throughput, Medium latency
```

#### Persistence
```
Kafka        ✅ Persistent logs, configurable retention
RabbitMQ     ✅ Persistent queues, configurable policies
Redis Pub/Sub ❌ In-memory by default, optional RDB/AOF
ActiveMQ     ✅ Persistent messages, journal-based
```

#### Message Ordering
```
Kafka        ✅ Guaranteed within partition
RabbitMQ     ✅ Guaranteed within queue
Redis Pub/Sub ❌ No ordering guarantees
ActiveMQ     ✅ Guaranteed within queue
```

#### Scalability
```
Kafka        ✅ Excellent horizontal scaling
RabbitMQ     ✅ Good clustering
Redis Pub/Sub ✅ Clustering support
ActiveMQ     ✅ Network of brokers
```

### По Use Cases

#### Event Streaming
```java
// Kafka для event streaming
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", StringSerializer.class.getName());
props.put("value.serializer", StringSerializer.class.getName());

KafkaProducer<String, String> producer = new KafkaProducer<>(props);

// Publish events
producer.send(new ProducerRecord<>("user-events", "user-123", "UserLoggedIn"));
producer.send(new ProducerRecord<>("order-events", "order-456", "OrderPlaced"));
```

#### Task Queues
```java
// RabbitMQ для task processing
ConnectionFactory factory = new ConnectionFactory();
factory.setHost("localhost");

Connection connection = factory.newConnection();
Channel channel = connection.createChannel();

// Declare queue
channel.queueDeclare("task_queue", true, false, false, null);

// Send task
channel.basicPublish("", "task_queue", MessageProperties.PERSISTENT_TEXT_PLAIN,
    "Process heavy computation".getBytes());
```

#### Real-time Notifications
```java
// Redis Pub/Sub для notifications
Jedis jedis = new Jedis("localhost");

// Subscribe to channel
jedis.subscribe(new JedisPubSub() {
    @Override
    public void onMessage(String channel, String message) {
        System.out.println("Received: " + message);
    }
}, "notifications");

// Publish notification
jedis.publish("notifications", "System maintenance scheduled");
```

#### Enterprise Messaging
```java
// ActiveMQ с JMS
ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
Connection connection = factory.createConnection();
Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

// Create producer
Destination destination = session.createQueue("enterprise.queue");
MessageProducer producer = session.createProducer(destination);

// Send message
TextMessage message = session.createTextMessage("Enterprise message");
producer.send(message);
```

## Архитектурные Паттерны

### Event-Driven Architecture

#### Event Sourcing
```java
// Kafka-based event sourcing
public class EventStore {

    private final KafkaProducer<String, String> producer;

    public void saveEvent(String aggregateId, DomainEvent event) {
        ProducerRecord<String, String> record = new ProducerRecord<>(
            "events", aggregateId, event.toJson());
        producer.send(record);
    }

    public List<DomainEvent> getEvents(String aggregateId) {
        // Read from Kafka topic
        return kafkaConsumer.pollEvents(aggregateId);
    }
}
```

#### CQRS Pattern
```java
// Command side (RabbitMQ)
public class CommandHandler {

    @RabbitListener(queues = "user-commands")
    public void handleCreateUser(CreateUserCommand command) {
        User user = new User(command.getEmail(), command.getName());
        userRepository.save(user);

        // Publish event
        eventPublisher.publish(new UserCreatedEvent(user.getId()));
    }
}

// Query side (separate service)
public class UserQueryService {

    @QueryHandler
    public List<UserDto> handle(GetUsersQuery query) {
        return userReadRepository.findAll()
            .stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
}
```

### Message Routing Patterns

#### Content-Based Router
```java
// RabbitMQ topic exchange routing
public class MessageRouter {

    public void routeMessage(OrderMessage message) {
        String routingKey;

        if (message.getAmount() > 1000) {
            routingKey = "orders.high-value";
        } else if (message.getPriority() == Priority.URGENT) {
            routingKey = "orders.urgent";
        } else {
            routingKey = "orders.normal";
        }

        rabbitTemplate.convertAndSend("order-exchange", routingKey, message);
    }
}
```

#### Message Filter
```java
// Kafka Streams filtering
KStream<String, OrderEvent> orders = builder.stream("orders");

KStream<String, OrderEvent> highValueOrders = orders
    .filter((key, order) -> order.getTotal() > 500)
    .through("high-value-orders");

KStream<String, OrderEvent> internationalOrders = orders
    .filter((key, order) -> !order.getCountry().equals("US"))
    .through("international-orders");
```

## Надежность и Гарантии

### Message Delivery Guarantees

#### At Most Once
```java
// Redis Pub/Sub - at most once
jedis.publish("channel", message);
// Message may be lost if subscriber is down
```

#### At Least Once
```java
// RabbitMQ - at least once
channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message);

// Consumer with manual ACK
channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) {
        // Process message
        processMessage(body);

        // Manual acknowledgment
        channel.basicAck(envelope.getDeliveryTag(), false);
    }
});
```

#### Exactly Once
```java
// Kafka - exactly once
Properties props = new Properties();
props.put("enable.idempotence", "true");
props.put("acks", "all");
props.put("retries", 3);
props.put("max.inflight.requests.per.connection", 1);

KafkaProducer<String, String> producer = new KafkaProducer<>(props);
// Producer idempotency + transactional producer for exactly-once
```

### Error Handling

#### Dead Letter Queues
```java
// RabbitMQ DLQ setup
// Declare main queue
channel.queueDeclare("main-queue", true, false, false, null);

// Declare DLQ
Map<String, Object> dlqArgs = new HashMap<>();
dlqArgs.put("x-dead-letter-exchange", "");
dlqArgs.put("x-dead-letter-routing-key", "dlq");
channel.queueDeclare("dlq", true, false, false, null);

// Bind DLQ to main queue
Map<String, Object> mainArgs = new HashMap<>();
mainArgs.put("x-dead-letter-exchange", "");
mainArgs.put("x-dead-letter-routing-key", "dlq");
channel.queueDeclare("main-queue", true, false, false, mainArgs);
```

#### Retry Patterns
```java
// Exponential backoff retry
public class RetryableMessageConsumer {

    private final int maxRetries = 3;

    @RabbitListener(queues = "retry-queue")
    public void processWithRetry(Message message, Channel channel) throws IOException {
        try {
            processMessage(message);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            int retryCount = getRetryCount(message);
            if (retryCount < maxRetries) {
                // Republish with delay
                long delay = (long) Math.pow(2, retryCount) * 1000; // Exponential backoff
                republishWithDelay(message, delay);
            } else {
                // Send to DLQ
                sendToDeadLetterQueue(message);
            }
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
```

## Мониторинг и Observability

### Metrics Collection

#### Kafka Metrics
```java
// Producer metrics
KafkaProducer<String, String> producer = new KafkaProducer<>(props);

// Access metrics
Map<MetricName, ? extends Metric> metrics = producer.metrics();
for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
    System.out.println(entry.getKey().name() + ": " + entry.getValue().value());
}
```

#### RabbitMQ Monitoring
```java
// Management API
public class RabbitMQMonitor {

    public void getQueueInfo() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate
            .withBasicAuth("guest", "guest")
            .getForEntity("http://localhost:15672/api/queues/%2F/my-queue", String.class);

        // Parse queue statistics
        JsonNode queueInfo = objectMapper.readTree(response.getBody());
        int messageCount = queueInfo.get("messages").asInt();
        int consumerCount = queueInfo.get("consumers").asInt();
    }
}
```

### Distributed Tracing

#### Message Tracing
```java
// Spring Cloud Sleuth with messaging
@SpringBootApplication
public class MessagingApplication {

    @Bean
    public NewSpanParser newSpanParser() {
        return new ZipkinMessageSpanParser();
    }

    @Bean
    public SpanInjector<Message<?>> spanInjector() {
        return new ZipkinMessageSpanInjector();
    }
}

// Producer with tracing
@Service
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @NewSpan("send-message")
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend("my-exchange", "my-routing-key", message);
    }
}
```

## Производительность и Оптимизация

### Kafka Optimization

#### Producer Configuration
```properties
# High throughput producer
bootstrap.servers=localhost:9092
acks=1
compression.type=snappy
batch.size=32768
linger.ms=10
max.in.flight.requests.per.connection=5
enable.idempotence=true
```

#### Consumer Configuration
```properties
# High throughput consumer
bootstrap.servers=localhost:9092
group.id=my-consumer-group
auto.offset.reset=earliest
enable.auto.commit=false
max.poll.records=500
fetch.min.bytes=1024
fetch.max.wait.ms=500
```

### RabbitMQ Tuning

#### Connection Pooling
```java
// Connection pooling для high throughput
@Configuration
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setChannelCacheSize(10); // Pool size
        return factory;
    }
}
```

#### Message Batch Processing
```java
// Consumer batch processing
@RabbitListener(queues = "batch-queue")
public void processBatch(List<Message> messages, Channel channel) {
    // Process messages in batch
    for (Message message : messages) {
        processMessage(message);
    }

    // Manual ACK for entire batch
    channel.basicAck(messages.get(messages.size() - 1)
        .getMessageProperties().getDeliveryTag(), true);
}
```

## Безопасность

### Authentication и Authorization

#### Kafka Security
```properties
# SSL configuration
security.protocol=SSL
ssl.truststore.location=/path/to/truststore.jks
ssl.truststore.password=password
ssl.keystore.location=/path/to/keystore.jks
ssl.keystore.password=password

# SASL configuration
security.protocol=SASL_SSL
sasl.mechanism=PLAIN
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
  username="user" password="password";
```

#### RabbitMQ Security
```java
// SSL connection
ConnectionFactory factory = new ConnectionFactory();
factory.setHost("localhost");
factory.setPort(5671); // SSL port

factory.useSslProtocol(); // Default SSL
// or
factory.useSslProtocol(SSLContext.getDefault());
```

### Message Encryption

#### End-to-End Encryption
```java
public class EncryptedMessageProducer {

    private final SecretKey secretKey;

    public void sendEncryptedMessage(String message) throws Exception {
        // Encrypt message
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(message.getBytes());

        // Send encrypted message
        kafkaTemplate.send("secure-topic", encrypted);
    }
}

public class EncryptedMessageConsumer {

    private final SecretKey secretKey;

    @KafkaListener(topics = "secure-topic")
    public void consumeEncryptedMessage(byte[] encryptedMessage) throws Exception {
        // Decrypt message
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String decrypted = new String(cipher.doFinal(encryptedMessage));

        // Process decrypted message
        processMessage(decrypted);
    }
}
```

## Тестирование

### Unit Testing

#### Kafka Testing
```java
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class KafkaServiceTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    public void testMessageProcessing() {
        // Send test message
        kafkaTemplate.send("test-topic", "test-message");

        // Verify processing
        await().atMost(5, TimeUnit.SECONDS)
            .until(() -> messageProcessed.get());
    }
}
```

#### RabbitMQ Testing
```java
@Testcontainers
@SpringBootTest
public class RabbitMQIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3-management");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertySource registry) {
        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
    }

    @Test
    public void testMessageFlow() {
        // Test message publishing and consumption
        rabbitTemplate.convertAndSend("test-exchange", "test-key", "test-message");

        await().atMost(10, TimeUnit.SECONDS)
            .until(() -> receivedMessages.size() > 0);
    }
}
```

## Deployment Patterns

### Kubernetes Deployment

#### Kafka on Kubernetes
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: kafka
spec:
  serviceName: kafka
  replicas: 3
  template:
    spec:
      containers:
      - name: kafka
        image: confluentinc/cp-kafka:latest
        ports:
        - containerPort: 9092
        env:
        - name: KAFKA_BROKER_ID
          valueFrom:
            fieldRef:
              fieldPath: metadata.name
        - name: KAFKA_ZOOKEEPER_CONNECT
          value: zookeeper:2181
        volumeMounts:
        - name: kafka-data
          mountPath: /var/lib/kafka/data
  volumeClaimTemplates:
  - metadata:
      name: kafka-data
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 100Gi
```

#### RabbitMQ Cluster
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: rabbitmq
spec:
  serviceName: rabbitmq
  replicas: 3
  template:
    spec:
      containers:
      - name: rabbitmq
        image: rabbitmq:3-management
        ports:
        - containerPort: 5672
        - containerPort: 15672
        env:
        - name: RABBITMQ_ERLANG_COOKIE
          value: "SWQOKODSQALRPCLNMEQG"
        - name: RABBITMQ_DEFAULT_USER
          value: "admin"
        - name: RABBITMQ_DEFAULT_PASS
          value: "password"
        volumeMounts:
        - name: rabbitmq-data
          mountPath: /var/lib/rabbitmq
      serviceAccountName: rabbitmq
```

## Best Practices

### Message Design

#### Message Schema
```java
// Avro schema для Kafka
public class OrderMessage {
    private String orderId;
    private String customerId;
    private BigDecimal totalAmount;
    private List<OrderItem> items;
    private Instant createdAt;

    // Getters, setters, constructors
}

// Schema evolution
// - Backward compatible: add optional fields
// - Forward compatible: don't remove required fields
// - Full compatible: changes that work in both directions
```

#### Message Size Optimization
```java
// Compress large messages
Properties props = new Properties();
props.put("compression.type", "gzip");
props.put("max.request.size", "10485760"); // 10MB

// Split large messages
public List<MessageChunk> splitMessage(LargeMessage message, int chunkSize) {
    List<MessageChunk> chunks = new ArrayList<>();
    byte[] data = serialize(message);

    for (int i = 0; i < data.length; i += chunkSize) {
        byte[] chunk = Arrays.copyOfRange(data, i, Math.min(i + chunkSize, data.length));
        chunks.add(new MessageChunk(message.getId(), i / chunkSize, chunk));
    }

    return chunks;
}
```

### Consumer Lag Monitoring

#### Kafka Consumer Lag
```java
// Monitor consumer lag
public class ConsumerLagMonitor {

    private final AdminClient adminClient;

    public Map<TopicPartition, Long> getConsumerLag(String groupId) {
        Map<TopicPartition, Long> lags = new HashMap<>();

        // Get consumer group offsets
        ConsumerGroupDescription group = adminClient
            .describeConsumerGroups(Collections.singletonList(groupId))
            .all()
            .get(10, TimeUnit.SECONDS)
            .get(groupId);

        // Get latest offsets
        Map<TopicPartition, OffsetAndMetadata> consumerOffsets = group.members()
            .stream()
            .flatMap(member -> member.assignment().topicPartitions().stream())
            .collect(Collectors.toMap(tp -> tp, tp -> getConsumerOffset(tp, groupId)));

        // Calculate lag for each partition
        for (TopicPartition tp : consumerOffsets.keySet()) {
            long consumerOffset = consumerOffsets.get(tp).offset();
            long latestOffset = getLatestOffset(tp);
            lags.put(tp, latestOffset - consumerOffset);
        }

        return lags;
    }
}
```

### Capacity Planning

#### Throughput Estimation
```java
// Message throughput calculator
public class ThroughputCalculator {

    public ThroughputEstimate calculateCapacity(MessageProfile profile) {
        // Average message size
        int avgMessageSize = profile.getAvgMessageSize();

        // Target throughput
        int messagesPerSecond = profile.getMessagesPerSecond();

        // Calculate network bandwidth needed
        long networkBandwidthMbps = (avgMessageSize * messagesPerSecond * 8) / (1024 * 1024);

        // Calculate storage needed per day
        long dailyStorageBytes = avgMessageSize * messagesPerSecond * 24 * 60 * 60;

        return new ThroughputEstimate(networkBandwidthMbps, dailyStorageBytes);
    }
}
```

## Troubleshooting

### Common Issues

#### Kafka Issues
```bash
# Check broker logs
tail -f /var/log/kafka/server.log

# Check consumer lag
kafka-consumer-groups --bootstrap-server localhost:9092 --group my-group --describe

# Reset consumer offsets
kafka-consumer-groups --bootstrap-server localhost:9092 --group my-group --topic my-topic --reset-offsets --to-earliest --execute
```

#### RabbitMQ Issues
```bash
# Check queue status
rabbitmqctl list_queues name messages consumers

# Check connections
rabbitmqctl list_connections

# Check cluster status
rabbitmqctl cluster_status
```

### Performance Issues

#### High Latency
```java
// Optimize producer
Properties props = new Properties();
props.put("batch.size", "16384");
props.put("linger.ms", "5");
props.put("compression.type", "snappy");

// Optimize consumer
props.put("fetch.min.bytes", "1024");
props.put("fetch.max.wait.ms", "100");
props.put("max.poll.records", "100");
```

#### Memory Issues
```java
// RabbitMQ memory optimization
ConnectionFactory factory = new ConnectionFactory();
factory.setRequestedHeartbeat(60); // Heartbeat interval
factory.setConnectionTimeout(60000); // Connection timeout

// Consumer prefetch
channel.basicQos(10); // Prefetch 10 messages per consumer
```

---

**Категория:** Messaging
**Протоколы:** AMQP, Kafka Protocol, MQTT, STOMP
**Гарантии:** At-least-once, At-most-once, Exactly-once
**Шаблоны:** Pub/Sub, Request-Reply, Event Streaming
**Уровень:** Продвинутый
