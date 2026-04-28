---
title: "Kafka Advanced"
description: "Apache Kafka - это распределенная платформа для потоковой обработки данных в реальном времени. Этот документ охватывает продвинутые концепции, архитектурные паттерны, производительность и enterprise-grade практики работы с Kafka."
tags:
  - development
  - messaging
  - kafka-advanced
type: "overview"
difficulty: "intermediate"
aliases:
  - "Kafka Advanced"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kafka Advanced

**Apache Kafka** — это распределенная платформа для потоковой обработки данных в реальном времени. Этот документ охватывает продвинутые концепции, архитектурные паттерны, производительность и **enterprise-grade** практики работы с **Kafka**.

## Полезные ссылки
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Kafka Streams](https://kafka.apache.org/documentation/streams/)
- [Kafka Connect](https://kafka.apache.org/documentation/#connect)
- [Schema Registry (Confluent)](https://docs.confluent.io/platform/current/schema-registry/index.html)
- [Kafka Security](https://kafka.apache.org/documentation/#security)


### См. также
- [Apache ActiveMQ](../activemq/activemq.md)
## Содержание

- [Продвинутая архитектура](#продвинутая-архитектура)
  - [Multi-region deployment](#multi-region-deployment)
  - [Tiered Storage](#tiered-storage)
- [Продвинутые продюсеры](#продвинутые-продюсеры)
  - [Idempotent и transactional producers](#idempotent-и-transactional-producers)
  - [Custom partitioning](#custom-partitioning)
- [Продвинутые консьюмеры](#продвинутые-консьюмеры)
  - [Consumer Groups и rebalancing](#consumer-groups-и-rebalancing)
  - [Consumer lag monitoring](#consumer-lag-monitoring)
- [Kafka Streams](#kafka-streams)
  - [Stream processing application](#stream-processing-application)
  - [KTable operations](#ktable-operations)
- [Kafka Connect](#kafka-connect)
  - [Custom connector](#custom-connector)
  - [Connector configuration](#connector-configuration)
- [Schema Registry](#schema-registry)
  - [Schema evolution](#schema-evolution)
  - [Schema validation](#schema-validation)
- [Security](#security)
  - [SASL/SSL authentication](#saslssl-authentication)
  - [ACL management](#acl-management)
- [Monitoring и Observability](#monitoring-и-observability)
  - [JMX metrics](#jmx-metrics)
  - [Custom monitoring](#custom-monitoring)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Broker optimization](#broker-optimization)
  - [Client optimization](#client-optimization)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
  - [Performance debugging](#performance-debugging)
- [Лучшие практики](#лучшие-практики)
- [См. также](#см-также-1)

## Продвинутая архитектура

### Multi-region deployment
```yaml
# MirrorMaker 2.0 — репликация топиков между кластерами (source/target, offset sync)
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaMirrorMaker2
metadata:
  name: mirror-maker-2
spec:
  version: 3.4.0
  replicas: 3
  connectCluster: "target-cluster"
  clusters:
  - alias: "source-cluster"
    bootstrapServers: "source-cluster-kafka-bootstrap:9092"
  - alias: "target-cluster"
    bootstrapServers: "target-cluster-kafka-bootstrap:9092"
  mirrors:
  - sourceCluster: "source-cluster"
    targetCluster: "target-cluster"
    sourceConnector:
      config:
        replication.factor: 3
        offset-syncs.topic.replication.factor: 3
        sync.group.offsets.enabled: true
        sync.group.offsets.interval.seconds: 60
        refresh.topics.interval.seconds: 60
        topics: ".*"
        topics.exclude: ".*\\.internal\\..*"
    heartbeatConnector:
      config:
        heartbeats.topic.replication.factor: 3
        heartbeats.topic.replication.factor: 3
    checkpointConnector:
      config:
        checkpoints.topic.replication.factor: 3
        group.id: "mirror-maker-2"
        topics: ".*"
        topics.exclude: ".*\\.internal\\..*"
```

### Tiered Storage
```properties
# server.properties для tiered storage
# Включение tiered storage (Kafka 3.6+)
kafka.config.server.ConfluentTieredStorageConfig=true

# Конфигурация S3 для remote storage
confluent.tiered.storage.s3.bucket=kafka-tiered-storage
confluent.tiered.storage.s3.region=us-east-1
confluent.tiered.storage.s3.endpoint=https://s3.us-east-1.amazonaws.com

# Credentials
confluent.tiered.storage.s3.access.key=${AWS_ACCESS_KEY}
confluent.tiered.storage.s3.secret.key=${AWS_SECRET_KEY}

# Tiering policy
confluent.tiered.storage.enable=true
confluent.tiered.storage.rollover.segment.ms=3600000  # 1 hour
confluent.tiered.storage.rollover.segment.bytes=1073741824  # 1GB
confluent.tiered.storage.deletion.retention.ms=604800000  # 7 days
```

## Продвинутые продюсеры

### Idempotent и transactional producers
```java
// Конфигурация producer'а: acks, retries, idempotence, сериализаторы
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Idempotent producer
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        // Transactional producer
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "order-producer-tx");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

```java
// Отправка заказов в топик с ключом и заголовками
@Service
public class OrderService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void processOrder(Order order) {
        try {
            // Init transaction
            kafkaTemplate.executeInTransaction(kt -> {
                // Send order event
                kt.send("orders", order.getId(), order);

                // Send inventory update
                kt.send("inventory-updates", order.getId(),
                    new InventoryUpdate(order.getProductId(), -order.getQuantity()));

                // Send payment request
                kt.send("payment-requests", order.getId(),
                    new PaymentRequest(order.getId(), order.getTotal()));

                return true;
            });
        } catch (Exception e) {
            // Transaction will be aborted automatically
            throw new RuntimeException("Failed to process order", e);
        }
    }
}
```

### Custom partitioning
```java
// Кастомный партиционер: распределение по типу заказа
public class OrderPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes,
                        Object value, byte[] valueBytes, Cluster cluster) {

        if (!(value instanceof Order)) {
            return 0; // Default partition
        }

        Order order = (Order) value;

        // Partition by customer region for better locality
        String region = order.getCustomerRegion();
        if ("US-EAST".equals(region)) {
            return 0;
        } else if ("US-WEST".equals(region)) {
            return 1;
        } else if ("EU".equals(region)) {
            return 2;
        } else {
            return 3; // Other regions
        }
    }

    @Override
    public void close() {
        // Cleanup if needed
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // Configuration if needed
    }
}
```

```java
// Регистрация кастомного партиционера в конфигурации
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, Order> orderProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, OrderPartitioner.class.getName());

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Order> orderKafkaTemplate() {
        return new KafkaTemplate<>(orderProducerFactory());
    }
}
```

## Продвинутые консьюмеры

### Consumer Groups и rebalancing
```java
// Конфигурация consumer'а: группа, offset reset, десериализаторы
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processing-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // Advanced consumer settings
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);

        // Rebalancing settings
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
            Arrays.asList(RoundRobinAssignor.class, RangeAssignor.class));

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        // Manual acknowledgment
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Concurrency settings
        factory.setConcurrency(3);

        return factory;
    }
}
```

```java
// Обработка заказов из топика с ручным commit offset
@Service
public class OrderConsumer {

    @KafkaListener(topics = "orders", groupId = "order-processing-group",
                   containerFactory = "kafkaListenerContainerFactory")
    public void processOrder(@Payload Order order,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                           @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                           @Header(KafkaHeaders.OFFSET) long offset,
                           Acknowledgment acknowledgment) {

        try {
            // Process order
            processOrderBusinessLogic(order);

            // Manual acknowledgment
            acknowledgment.acknowledge();

            // Store offset for monitoring
            metrics.recordProcessedOrder(offset);

        } catch (Exception e) {
            // Log error and potentially send to dead letter queue
            logger.error("Failed to process order: {}", order.getId(), e);

            // Send to dead letter topic
            deadLetterProducer.send("orders-dlq", order);

            // Still acknowledge to avoid infinite retries
            acknowledgment.acknowledge();
        }
    }
}
```

### Consumer lag monitoring
```java
// Мониторинг отставания consumer group от конца партиций
@Component
public class KafkaLagMonitor {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Autowired
    private MeterRegistry meterRegistry;

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void monitorConsumerLag() {
        try {
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());

            // Get consumer groups
            ListConsumerGroupsResult groupsResult = adminClient.listConsumerGroups();
            Collection<ConsumerGroupListing> groups = groupsResult.all().get();

            for (ConsumerGroupListing group : groups) {
                // Get group offsets
                DescribeConsumerGroupsResult groupResult =
                    adminClient.describeConsumerGroups(Collections.singletonList(group.groupId()));

                ConsumerGroupDescription groupDesc = groupResult.all().get().get(group.groupId());

                // Get lag for each topic partition
                for (MemberDescription member : groupDesc.members()) {
                    for (TopicPartition tp : member.assignment().topicPartitions()) {
                        try {
                            // Get consumer offset
                            OffsetAndMetadata committed =
                                adminClient.listConsumerGroupOffsets(
                                    Collections.singletonMap(new TopicPartition(tp.topic(), tp.partition()),
                                    OffsetSpec.latest())).partitions().get(tp);

                            // Get latest offset
                            Map<TopicPartition, OffsetSpec> offsetSpecs = new HashMap<>();
                            offsetSpecs.put(tp, OffsetSpec.latest());

                            Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestOffsets =
                                adminClient.listOffsets(offsetSpecs).all().get();

                            long consumerOffset = committed != null ? committed.offset() : 0;
                            long latestOffset = latestOffsets.get(tp).offset();
                            long lag = latestOffset - consumerOffset;

                            // Record metric
                            meterRegistry.gauge("kafka_consumer_lag",
                                Tags.of(
                                    "group", group.groupId(),
                                    "topic", tp.topic(),
                                    "partition", String.valueOf(tp.partition())
                                ),
                                lag);

                        } catch (Exception e) {
                            logger.warn("Failed to get lag for topic {} partition {}",
                                      tp.topic(), tp.partition(), e);
                        }
                    }
                }
            }

            adminClient.close();

        } catch (Exception e) {
            logger.error("Failed to monitor consumer lag", e);
        }
    }
}
```

## Kafka Streams

### Stream processing application
```java
@Configuration
public class KafkaStreamsConfig {

    @Bean
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-processing-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Advanced settings
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10485760); // 10MB
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");

        // Exactly-once processing
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);

        // Monitoring
        props.put(StreamsConfig.METRICS_RECORDING_LEVEL_CONFIG, "INFO");

        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public KStream<String, Order> processOrders(KafkaStreamsConfiguration config) {
        StreamsBuilder builder = new StreamsBuilder();

        // Input stream
        KStream<String, Order> orders = builder.stream("orders",
            Consumed.with(Serdes.String(), new OrderSerde()));

        // Branch orders by type
        KStream<String, Order>[] branches = orders.branch(
            (key, order) -> "premium".equals(order.getCustomerType()),
            (key, order) -> "standard".equals(order.getCustomerType()),
            (key, order) -> true // default branch
        );

        // Process premium orders
        branches[0]
            .mapValues(order -> processPremiumOrder(order))
            .to("premium-orders-processed", Produced.with(Serdes.String(), new OrderSerde()));

        // Process standard orders
        branches[1]
            .mapValues(order -> processStandardOrder(order))
            .to("standard-orders-processed", Produced.with(Serdes.String(), new OrderSerde()));

        // Handle unknown order types
        branches[2]
            .mapValues(order -> handleUnknownOrder(order))
            .to("unknown-orders", Produced.with(Serdes.String(), new OrderSerde()));

        return orders;
    }
}
```

### KTable operations
```java
// KTable: материализованное представление из топика
@Configuration
public class InventoryStreamsConfig {

    @Bean
    public KTable<String, Inventory> inventoryTable(StreamsBuilder builder) {
        // Create KTable from inventory topic
        KTable<String, Inventory> inventory = builder.table(
            "inventory",
            Consumed.with(Serdes.String(), new InventorySerde()),
            Materialized.<String, Inventory, KeyValueStore<Bytes, byte[]>>as("inventory-store")
                .withKeySerde(Serdes.String())
                .withValueSerde(new InventorySerde())
        );

        // Stream of inventory updates
        KStream<String, InventoryUpdate> updates = builder.stream(
            "inventory-updates",
            Consumed.with(Serdes.String(), new InventoryUpdateSerde())
        );

        // Update inventory based on order updates
        updates
            .groupByKey(Grouped.with(Serdes.String(), new InventoryUpdateSerde()))
            .reduce(
                (oldUpdate, newUpdate) -> newUpdate, // Keep latest update
                Materialized.<String, InventoryUpdate, KeyValueStore<Bytes, byte[]>>as("inventory-updates-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new InventoryUpdateSerde())
            )
            .join(
                inventory,
                (update, currentInventory) -> {
                    // Apply inventory update
                    return new Inventory(
                        currentInventory.getProductId(),
                        currentInventory.getQuantity() + update.getQuantityChange(),
                        currentInventory.getLastUpdated()
                    );
                },
                Materialized.<String, Inventory, KeyValueStore<Bytes, byte[]>>as("updated-inventory")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new InventorySerde())
            )
            .toStream()
            .to("inventory-updated", Produced.with(Serdes.String(), new InventorySerde()));

        return inventory;
    }
}
```

## Kafka Connect

### Custom connector
```java
// Кастомный source-коннектор: конфигурация и список задач
public class CustomSourceConnector extends SourceConnector {

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public void start(Map<String, String> props) {
        // Initialize connector
        this.config = new CustomSourceConfig(props);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return CustomSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new ArrayList<>();

        // Create task configurations
        for (int i = 0; i < maxTasks; i++) {
            Map<String, String> taskConfig = new HashMap<>(config.originalsStrings());
            taskConfig.put("task.id", String.valueOf(i));
            configs.add(taskConfig);
        }

        return configs;
    }

    @Override
    public void stop() {
        // Cleanup resources
    }

    @Override
    public ConfigDef config() {
        return CustomSourceConfig.CONFIG_DEF;
    }
}
```

```java
// Задача коннектора: опрос источника и отправка записей в Kafka
public class CustomSourceTask extends SourceTask {

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public void start(Map<String, String> props) {
        // Initialize task
        this.taskConfig = new CustomSourceTaskConfig(props);
        this.reader = new DataReader(taskConfig.getConnectionString());
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        List<SourceRecord> records = new ArrayList<>();

        // Read data from source
        List<DataRecord> dataRecords = reader.readRecords();

        for (DataRecord record : dataRecords) {
            // Create source record
            SourceRecord sourceRecord = new SourceRecord(
                getSourcePartition(record),
                getSourceOffset(record),
                taskConfig.getTopic(),
                null,  // partition
                Schema.STRING_SCHEMA,
                record.getKey(),
                getValueSchema(),
                convertToStruct(record)
            );

            records.add(sourceRecord);
        }

        return records;
    }

    @Override
    public void stop() {
        if (reader != null) {
            reader.close();
        }
    }

    private Map<String, ?> getSourcePartition(DataRecord record) {
        return Collections.singletonMap("table", record.getTableName());
    }

    private Map<String, ?> getSourceOffset(DataRecord record) {
        return Collections.singletonMap("position", record.getPosition());
    }
}
```

### Connector configuration
```properties
# Source connector configuration
{
  "name": "custom-source-connector",
  "config": {
    "connector.class": "com.example.CustomSourceConnector",
    "tasks.max": "3",
    "topic.prefix": "custom-data-",
    "connection.url": "jdbc:postgresql://db:5432/mydb",
    "connection.user": "user",
    "connection.password": "password",
    "table.whitelist": "users,orders,products",
    "mode": "incrementing",
    "incrementing.column.name": "id",
    "timestamp.column.name": "updated_at",
    "poll.interval.ms": "5000",
    "batch.max.rows": "100",
    "transforms": "unwrap",
    "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
    "transforms.unwrap.drop.tombstones": "false"
  }
}

# Sink connector configuration
{
  "name": "elasticsearch-sink-connector",
  "config": {
    "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
    "tasks.max": "2",
    "topics": "orders,products,users",
    "connection.url": "http://elasticsearch:9200",
    "type.name": "_doc",
    "key.ignore": "false",
    "schema.ignore": "true",
    "behavior.on.malformed.documents": "warn",
    "drop.invalid.message": "true",
    "compact.map.entries": "true",
    "write.method": "upsert",
    "read.timeout.ms": "10000",
    "linger.ms": "1",
    "batch.size": "1000",
    "max.retries": "5",
    "retry.backoff.ms": "1000"
  }
}
```

## Schema Registry

### Schema evolution
```java
@Configuration
public class KafkaSchemaConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        // Schema Registry configuration
        configProps.put("schema.registry.url", "http://localhost:8081");
        configProps.put("auto.register.schemas", "false");
        configProps.put("use.latest.version", "true");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "order-consumer");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);

        // Schema Registry configuration
        configProps.put("schema.registry.url", "http://localhost:8081");
        configProps.put("specific.avro.reader", "true");

        return new DefaultKafkaConsumerFactory<>(configProps);
    }
}
```

### Schema validation
```java
@Service
public class SchemaValidationService {

    @Autowired
    private CachedSchemaRegistryClient schemaRegistry;

    public void validateSchema(String topic, String schemaString) throws Exception {
        // Parse schema
        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(schemaString);

        // Check compatibility
        SchemaMetadata metadata = schemaRegistry.getLatestSchemaMetadata(topic + "-value");

        SchemaCompatibility compatibility = SchemaCompatibility.checkReaderWriterCompatibility(
            schema, new Schema.Parser().parse(metadata.getSchema()));

        if (!compatibility.getType().equals(SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE)) {
            throw new SchemaValidationException("Schema is not backward compatible: " + compatibility.getDescription());
        }

        // Register new schema
        schemaRegistry.register(topic + "-value", schema);
    }
}
```

## Security

### SASL/SSL authentication
```properties
# Producer configuration with SASL/SSL
bootstrap.servers=kafka1:9093,kafka2:9093,kafka3:9093
security.protocol=SASL_SSL
sasl.mechanism=PLAIN
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required \
  username="producer" \
  password="producer-secret";

ssl.truststore.location=/etc/kafka/secrets/kafka.truststore.jks
ssl.truststore.password=truststore-password
ssl.keystore.location=/etc/kafka/secrets/kafka.keystore.jks
ssl.keystore.password=keystore-password
ssl.key.password=key-password

# ACL configuration
authorizer.class.name=kafka.security.auth.SimpleAclAuthorizer
allow.everyone.if.no.acl.found=false
```

### ACL management
```bash
# Create ACLs
kafka-acls --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:order-producer \
  --operation Write \
  --topic orders

kafka-acls --bootstrap-server localhost:9092 \
  --add \
  --allow-principal User:order-consumer \
  --operation Read \
  --topic orders \
  --group order-processing-group

# List ACLs
kafka-acls --bootstrap-server localhost:9092 \
  --list \
  --topic orders

# Remove ACLs
kafka-acls --bootstrap-server localhost:9092 \
  --remove \
  --allow-principal User:old-producer \
  --operation Write \
  --topic orders
```

## Monitoring и Observability

### JMX metrics
```java
@Configuration
public class KafkaMetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        MeterRegistry registry = new SimpleMeterRegistry();

        // Kafka producer metrics
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

            // Producer metrics
            ObjectName producerMetrics = new ObjectName("kafka.producer:type=producer-metrics,*");
            Set<ObjectName> producerInstances = mbeanServer.queryNames(producerMetrics, null);

            for (ObjectName producerInstance : producerInstances) {
                String clientId = mbeanServer.getAttribute(producerInstance, "client-id").toString();

                // Record request rate
                Gauge.builder("kafka_producer_request_rate", () ->
                    (Double) mbeanServer.getAttribute(producerInstance, "request-rate"))
                    .tags("client.id", clientId)
                    .register(registry);

                // Record error rate
                Gauge.builder("kafka_producer_error_rate", () ->
                    (Double) mbeanServer.getAttribute(producerInstance, "error-rate"))
                    .tags("client.id", clientId)
                    .register(registry);
            }

        } catch (Exception e) {
            logger.warn("Failed to register Kafka JMX metrics", e);
        }

        return registry;
    }
}
```

### Custom monitoring
```java
@Component
public class KafkaHealthIndicator implements HealthIndicator {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    @Override
    public Health health() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            // Check cluster connectivity
            DescribeClusterResult clusterResult = adminClient.describeCluster();
            Collection<Node> nodes = clusterResult.nodes().get();

            // Check topics
            ListTopicsResult topicsResult = adminClient.listTopics();
            Set<String> topics = topicsResult.names().get();

            // Check consumer groups
            ListConsumerGroupsResult groupsResult = adminClient.listConsumerGroups();
            Collection<ConsumerGroupListing> groups = groupsResult.all().get();

            return Health.up()
                .withDetail("clusterId", clusterResult.clusterId().get())
                .withDetail("nodes", nodes.size())
                .withDetail("topics", topics.size())
                .withDetail("consumerGroups", groups.size())
                .build();

        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
}
```

## Производительность и оптимизация

### Broker optimization
```properties
# server.properties optimizations
############################# Server Basics #############################
broker.id=1
num.network.threads=9
num.io.threads=16
num.replica.fetchers=2

############################# Log Basics #############################
num.partitions=8
default.replication.factor=3
min.insync.replicas=2

############################# Log Retention #############################
log.retention.hours=168
log.retention.bytes=1073741824
log.segment.bytes=1073741824
log.cleanup.policy=delete

############################# Zookeeper #############################
zookeeper.connection.timeout.ms=6000
zookeeper.session.timeout.ms=6000

############################# Performance #############################
socket.send.buffer.bytes=1048576
socket.receive.buffer.bytes=1048576
socket.request.max.bytes=104857600
max.connections.per.ip=10
num.recovery.threads.per.data.dir=2

# JVM settings
KAFKA_HEAP_OPTS="-Xmx8G -Xms8G"
KAFKA_JVM_PERFORMANCE_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35"
```

### Client optimization
```java
@Configuration
public class OptimizedKafkaConfig {

    @Bean
    public ProducerFactory<String, Object> optimizedProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Connection settings
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9092,kafka2:9092,kafka3:9092");
        configProps.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 600000);

        // Compression
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 32768);

        // Buffering
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864); // 64MB
        configProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 60000);

        // Reliability
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 10);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        // Performance
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
        configProps.put(ProducerConfig.SEND_BUFFER_CONFIG, 131072);
        configProps.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, 32768);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public ConsumerFactory<String, Object> optimizedConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Connection settings
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9092,kafka2:9092,kafka3:9092");
        configProps.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 600000);

        // Consumption settings
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "optimized-consumer-group");
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);

        // Performance
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);
        configProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 52428800); // 50MB
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        configProps.put(ConsumerConfig.SEND_BUFFER_CONFIG, 131072);
        configProps.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, 32768);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }
}
```

## Решение проблем

### Распространенные проблемы
```bash
# Check broker logs
tail -f /var/log/kafka/server.log

# Check consumer lag
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group order-processing-group \
  --describe

# Check topic details
kafka-topics --bootstrap-server localhost:9092 \
  --topic orders \
  --describe

# Check partition distribution
kafka-topics --bootstrap-server localhost:9092 \
  --topic orders \
  --describe \
  --under-replicated-partitions

# Check broker metrics
kafka-run-class kafka.tools.JmxTool \
  --jmx-url service:jmx:rmi:///jndi/rmi://localhost:9999/jmxrmi \
  --object-name kafka.server:type=ReplicaManager,name=PartitionCount \
  --attributes Value

# Reset consumer offsets
kafka-consumer-groups --bootstrap-server localhost:9092 \
  --group order-processing-group \
  --topic orders \
  --reset-offsets --to-earliest \
  --execute

# Check connectivity
kafka-broker-api-versions --bootstrap-server localhost:9092

# Validate configuration
kafka-configs --bootstrap-server localhost:9092 \
  --entity-type topics \
  --entity-name orders \
  --describe
```

### Performance debugging
```java
@Component
public class KafkaPerformanceMonitor {

    @Autowired
    private MeterRegistry meterRegistry;

    @KafkaListener(topics = "performance-metrics")
    public void monitorPerformance(ConsumerRecord<String, String> record) {
        try {
            JsonNode metrics = new ObjectMapper().readTree(record.value());

            // Record producer metrics
            if (metrics.has("producer")) {
                JsonNode producer = metrics.get("producer");
                meterRegistry.gauge("kafka_producer_request_latency",
                    producer.get("request-latency-avg").asDouble(),
                    Tags.of("client", record.key()));

                meterRegistry.counter("kafka_producer_requests_total",
                    Tags.of("client", record.key()))
                    .increment(producer.get("request-total").asDouble());
            }

            // Record consumer metrics
            if (metrics.has("consumer")) {
                JsonNode consumer = metrics.get("consumer");
                meterRegistry.gauge("kafka_consumer_lag",
                    consumer.get("records-lag-avg").asDouble(),
                    Tags.of("group", record.key()));

                meterRegistry.gauge("kafka_consumer_fetch_rate",
                    consumer.get("fetch-rate").asDouble(),
                    Tags.of("group", record.key()));
            }

        } catch (Exception e) {
            logger.error("Failed to parse performance metrics", e);
        }
    }
}
```

## Лучшие практики

- **Партиционирование и ключи:** используйте осмысленные ключи для упорядоченности и сбалансированности партиций; избегайте **hot partitions** (один ключ доминирует).
- **Репликация и durability:** `acks=all` и `min.insync.replicas=2` для критичных данных; достаточный `replication.factor` (≥2 в prod); мониторинг **under-replicated** партиций.
- **Консьюмеры:** используйте **consumer groups** для масштабирования; обрабатывайте сообщения идемпотентно где возможно; настраивайте `max.`poll.interval`.ms` и **commit policy** под латентность обработки.
- **Schema `Registry`:** используйте **Schema Registry** для эволюции схем; совместимость (backward/forward) при изменении схем; не храните сырые байты без версии схемы.
- **Безопасность и мониторинг:** включайте **SSL**/**TLS** и **SASL** в **prod**; мониторьте **lag**, **throughput**, ошибки; настройте алерты на отставание **consumer groups** и сбои брокеров.
## См. также
- [RabbitMQ](../rabbitmq/rabbitmq.md) — альтернативная система сообщений
- [Spring Kafka](../../../frameworks/java-frameworks/spring/spring-kafka.md) — **Spring** интеграция
- [Kafka (основы и Connect)](kafka.md) — **ETL** для **Kafka**
- [Prometheus](../../../monitoring/metrics/prometheus.md) — мониторинг инфраструктуры
