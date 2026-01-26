# Агрегация логов для Java

Комплексное руководство по агрегации логов в Java: обработка, фильтрация, трансформация и анализ больших объемов логов в реальном времени и batch режиме.

**Дата последнего обновления:** 2026-01-21

## Полезные ссылки

### Фреймворки агрегации
- [Apache Kafka](https://kafka.apache.org/) - Distributed streaming platform
- [Apache Flink](https://flink.apache.org/) - Stream processing framework
- [Apache Spark](https://spark.apache.org/) - Big data processing
- [Apache Storm](https://storm.apache.org/) - Real-time computation

### Java библиотеки
- [Logback Async Appender](https://logback.qos.ch/manual/appenders.html)
- [Disruptor](https://lmax-exchange.github.io/disruptor/) - High-performance messaging
- [RxJava](https://github.com/ReactiveX/RxJava) - Reactive programming
- [Reactor](https://projectreactor.io/) - Reactive streams

### Статьи и туториалы
- [Log Aggregation Patterns](https://microservices.io/patterns/observability/application-logging.html)
- [Streaming Log Analysis](https://www.confluent.io/blog/real-time-log-streaming-pipeline/)
- [Kafka Log Aggregation](https://kafka.apache.org/documentation/streams/)

### См. также
- `logging-basics.md` - Основы логирования
- `centralized-logging.md` - Централизованное логирование
- `structured-logging.md` - Структурированное логирование

## Содержание

- [Введение в агрегацию логов](#введение-в-агрегацию-логов)
- [Архитектура агрегации](#архитектура-агрегации)
- [Kafka для логирования](#kafka-для-логирования)
- [Stream processing](#stream-processing)
- [Batch processing](#batch-processing)
- [Real-time aggregation](#real-time-aggregation)
- [Complex event processing](#complex-event-processing)
- [Windowing и time-based aggregation](#windowing-и-time-based-aggregation)
- [Error aggregation](#error-aggregation)
- [Performance metrics](#performance-metrics)
- [Data enrichment](#data-enrichment)
- [Storage optimization](#storage-optimization)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Заключение](#заключение)

## Введение в агрегацию логов

**Агрегация логов** — это процесс сбора, обработки и анализа лог-сообщений из различных источников для извлечения полезной информации, выявления паттернов и принятия решений. Агрегация может происходить в реальном времени (streaming) или пакетном режиме (batch).

### Почему важна агрегация логов?

Агрегация логов решает ключевые задачи анализа больших данных:

1. **Pattern Recognition** — выявление повторяющихся проблем и аномалий
2. **Performance Analysis** — анализ производительности систем
3. **Business Intelligence** — извлечение бизнес-метрик из логов
4. **Security Monitoring** — обнаружение security threats
5. **Capacity Planning** — планирование ресурсов на основе трендов
6. **Root Cause Analysis** — быстрая диагностика проблем
7. **Compliance Reporting** — автоматизированные отчеты
8. **Real-time Alerting** — мгновенные оповещения о критических событиях

### Типы агрегации

#### По времени (Time-based)
- **Fixed Windows** — агрегация за фиксированные интервалы (1 мин, 5 мин, 1 час)
- **Sliding Windows** — перекрывающиеся окна для smooth анализа
- **Session Windows** — группировка по пользовательским сессиям

#### По содержимому (Content-based)
- **Error Aggregation** — группировка ошибок по типам и источникам
- **Performance Metrics** — сбор метрик производительности
- **Business Events** — агрегация бизнес-событий

#### По структуре (Structure-based)
- **Count Aggregation** — подсчет количества событий
- **Sum Aggregation** — суммирование числовых значений
- **Average Aggregation** — расчет средних значений

## Архитектура агрегации

### Streaming architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Log Sources                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Application │ Database │ Infrastructure │ External APIs │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Ingestion Layer                               │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Filebeat │ Fluent Bit │ Logstash │ Custom Collectors   │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Message Queue                                 │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Kafka │ RabbitMQ │ Redis Stream │ Kinesis             │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Processing Layer                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Flink │ Spark Streaming │ Kafka Streams │ Storm        │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Storage Layer                                 │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Elasticsearch │ Cassandra │ S3 │ Redshift               │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### Batch architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Raw Logs                                     │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Files │ Databases │ Archives │ Cloud Storage            │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Batch Processing                             │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ MapReduce │ Spark Batch │ Hive │ Pig                    │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Aggregation Results                           │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Reports │ Dashboards │ Data Warehouse │ Analytics       │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

## Kafka для логирования

### Kafka topics design

#### Topic naming strategy
```bash
# Topic naming conventions
# Format: {domain}.{entity}.{action}

# Application logs
logs.application.info
logs.application.error
logs.application.debug

# Business events
events.user.registration
events.user.login
events.order.created
events.payment.processed

# Infrastructure logs
logs.infrastructure.cpu
logs.infrastructure.memory
logs.infrastructure.disk

# Audit logs
audit.security.login
audit.security.permission
audit.data.access
```

#### Partitioning strategy
```java
public class LogPartitioner implements Partitioner {
    
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, 
                        Object value, byte[] valueBytes, Cluster cluster) {
        
        if (key == null) {
            // Round-robin for null keys
            return Math.abs(ThreadLocalRandom.current().nextInt()) % 
                   cluster.partitionCountForTopic(topic);
        }
        
        // Hash-based partitioning for correlation
        if (key instanceof String) {
            String correlationId = (String) key;
            return Math.abs(correlationId.hashCode()) % 
                   cluster.partitionCountForTopic(topic);
        }
        
        return 0;
    }
}
```

### Producer configuration

#### High-throughput producer
```java
@Configuration
public class KafkaLogProducerConfig {
    
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Bootstrap servers
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Serializer configuration
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        // Performance tuning for logs
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 65536); // 64KB
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864); // 64MB
        
        // Reliability
        configProps.put(ProducerConfig.ACKS_CONFIG, "1");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

#### Async log shipping
```java
@Service
public class AsyncLogShipper {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public AsyncLogShipper(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }
    
    public void shipLogAsync(LogEntry logEntry) {
        CompletableFuture.runAsync(() -> {
            try {
                String json = objectMapper.writeValueAsString(logEntry);
                String topic = determineTopic(logEntry);
                String key = logEntry.getCorrelationId();
                
                kafkaTemplate.send(topic, key, json)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            logger.error("Failed to ship log to Kafka", ex);
                        }
                    });
                
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize log entry", e);
            }
        });
    }
    
    private String determineTopic(LogEntry logEntry) {
        String level = logEntry.getLevel();
        String category = logEntry.getCategory();
        
        if ("ERROR".equals(level) || "WARN".equals(level)) {
            return "logs.application.error";
        } else if ("business".equals(category)) {
            return "events.business";
        } else {
            return "logs.application.info";
        }
    }
}
```

## Stream processing

### Apache Flink

#### Flink log processing
```java
public class FlinkLogProcessor {
    
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        // Kafka source
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
            .setBootstrapServers("localhost:9092")
            .setTopics(Arrays.asList("logs.application.*"))
            .setGroupId("log-processor")
            .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(StringDeserializer.class))
            .build();
        
        // Process stream
        DataStream<LogEntry> logStream = env.fromSource(kafkaSource, 
            WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                .withTimestampAssigner((event, timestamp) -> extractTimestamp(event)),
            "Kafka Source")
            .map(json -> parseLogEntry(json))
            .filter(log -> log.getLevel().equals("ERROR"))
            .keyBy(LogEntry::getService)
            .window(TumblingEventTimeWindows.of(Time.minutes(5)))
            .aggregate(new ErrorCountAggregator());
        
        // Sink results
        logStream.addSink(new ElasticsearchSink.Builder<ErrorStats, ErrorStats>()
            .setBulkFlushMaxActions(100)
            .setHosts(new HttpHost("localhost", 9200, "http"))
            .setEmitter((element, context, indexer) -> 
                indexer.add(createIndexRequest(element)))
            .build());
        
        env.execute("Log Aggregation Job");
    }
    
    private static long extractTimestamp(String json) {
        try {
            ObjectNode node = (ObjectNode) new ObjectMapper().readTree(json);
            return node.get("@timestamp").asLong();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
    
    private static LogEntry parseLogEntry(String json) throws IOException {
        return new ObjectMapper().readValue(json, LogEntry.class);
    }
    
    static class ErrorCountAggregator implements AggregateFunction<LogEntry, ErrorStats, ErrorStats> {
        
        @Override
        public ErrorStats createAccumulator() {
            return new ErrorStats();
        }
        
        @Override
        public ErrorStats add(LogEntry log, ErrorStats accumulator) {
            accumulator.setService(log.getService());
            accumulator.setErrorCount(accumulator.getErrorCount() + 1);
            accumulator.addErrorType(log.getErrorType());
            return accumulator;
        }
        
        @Override
        public ErrorStats getResult(ErrorStats accumulator) {
            return accumulator;
        }
        
        @Override
        public ErrorStats merge(ErrorStats a, ErrorStats b) {
            a.setErrorCount(a.getErrorCount() + b.getErrorCount());
            a.getErrorTypes().addAll(b.getErrorTypes());
            return a;
        }
    }
}
```

### Kafka Streams

#### Log aggregation with Kafka Streams
```java
public class KafkaStreamsLogAggregator {
    
    private static final String INPUT_TOPIC = "logs.application.error";
    private static final String OUTPUT_TOPIC = "logs.aggregated.errors";
    
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "log-aggregator");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        StreamsBuilder builder = new StreamsBuilder();
        
        // Source stream
        KStream<String, String> errorLogs = builder.stream(INPUT_TOPIC);
        
        // Parse and aggregate
        KTable<Windowed<String>, Long> errorCounts = errorLogs
            .mapValues(json -> parseServiceName(json))
            .groupBy((key, service) -> service)
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
            .count();
        
        // Convert to output format
        KStream<String, String> aggregatedErrors = errorCounts
            .toStream()
            .map((windowedKey, count) -> {
                String service = windowedKey.key();
                long windowStart = windowedKey.window().start();
                long windowEnd = windowedKey.window().end();
                
                String output = String.format(
                    "{\"service\":\"%s\",\"windowStart\":%d,\"windowEnd\":%d,\"errorCount\":%d}",
                    service, windowStart, windowEnd, count);
                
                return KeyValue.pair(service, output);
            });
        
        // Sink to output topic
        aggregatedErrors.to(OUTPUT_TOPIC);
        
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        
        // Graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
    
    private static String parseServiceName(String json) {
        try {
            ObjectNode node = (ObjectNode) new ObjectMapper().readTree(json);
            return node.get("service").asText("unknown");
        } catch (Exception e) {
            return "unknown";
        }
    }
}
```

## Batch processing

### Apache Spark

#### Spark log analysis
```java
public class SparkLogAnalyzer {
    
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder()
            .appName("Log Analysis")
            .config("spark.sql.adaptive.enabled", "true")
            .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
            .getOrCreate();
        
        // Read log files
        Dataset<Row> logs = spark.read()
            .option("header", "false")
            .option("inferSchema", "true")
            .json("s3://logs-bucket/application-logs/")
            .cache();
        
        // Parse timestamps
        logs = logs.withColumn("timestamp", 
            to_timestamp(col("@timestamp"), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"))
            .withColumn("date", to_date(col("timestamp")));
        
        // Error analysis
        Dataset<Row> errors = logs.filter(col("level").equalTo("ERROR"));
        
        Dataset<Row> errorSummary = errors.groupBy(col("date"), col("service"))
            .agg(
                count("*").as("error_count"),
                collect_set(col("errorType")).as("error_types"),
                first(col("message")).as("sample_message")
            )
            .orderBy(col("date").desc(), col("error_count").desc());
        
        // Performance analysis
        Dataset<Row> performanceLogs = logs.filter(col("category").equalTo("performance"));
        
        Dataset<Row> perfSummary = performanceLogs.groupBy(col("date"), col("operation"))
            .agg(
                avg(col("duration")).as("avg_duration"),
                max(col("duration")).as("max_duration"),
                min(col("duration")).as("min_duration"),
                count("*").as("operation_count")
            );
        
        // Business metrics
        Dataset<Row> businessEvents = logs.filter(col("category").equalTo("business"));
        
        Dataset<Row> businessSummary = businessEvents.groupBy(col("date"), col("eventType"))
            .agg(count("*").as("event_count"))
            .orderBy(col("date").desc(), col("event_count").desc());
        
        // Save results
        errorSummary.write()
            .mode("overwrite")
            .parquet("s3://analytics-bucket/error-summary/");
        
        perfSummary.write()
            .mode("overwrite")
            .parquet("s3://analytics-bucket/performance-summary/");
        
        businessSummary.write()
            .mode("overwrite")
            .parquet("s3://analytics-bucket/business-summary/");
        
        spark.stop();
    }
}
```

### Custom aggregations

#### User-defined aggregation functions
```java
public class PercentileAggregator extends Aggregator<Long, PercentileAccumulator, Double> {
    
    private final double percentile;
    
    public PercentileAggregator(double percentile) {
        this.percentile = percentile;
    }
    
    @Override
    public PercentileAccumulator zero() {
        return new PercentileAccumulator();
    }
    
    @Override
    public PercentileAccumulator reduce(PercentileAccumulator accumulator, Long value) {
        accumulator.addValue(value);
        return accumulator;
    }
    
    @Override
    public PercentileAccumulator merge(PercentileAccumulator a, PercentileAccumulator b) {
        a.merge(b);
        return a;
    }
    
    @Override
    public Double finish(PercentileAccumulator accumulator) {
        return accumulator.getPercentile(percentile);
    }
    
    @Override
    public Encoder<PercentileAccumulator> bufferEncoder() {
        return Encoders.bean(PercentileAccumulator.class);
    }
    
    @Override
    public Encoder<Double> outputEncoder() {
        return Encoders.DOUBLE();
    }
    
    static class PercentileAccumulator {
        private final List<Long> values = new ArrayList<>();
        
        public void addValue(long value) {
            values.add(value);
        }
        
        public void merge(PercentileAccumulator other) {
            values.addAll(other.values);
        }
        
        public double getPercentile(double percentile) {
            if (values.isEmpty()) return 0.0;
            
            Collections.sort(values);
            int index = (int) Math.ceil(percentile / 100.0 * values.size()) - 1;
            return values.get(Math.max(0, index));
        }
    }
}

// Usage
PercentileAggregator p95Aggregator = new PercentileAggregator(95.0);
spark.udf().register("percentile95", udaf(p95Aggregator));

Dataset<Row> perfAnalysis = logs.groupBy(col("operation"))
    .agg(
        count("*").as("count"),
        callUDF("percentile95", col("duration")).as("p95_duration")
    );
```

## Real-time aggregation

### Sliding window aggregation

#### Tumbling windows
```java
public class TumblingWindowAggregator {
    
    private final Map<String, Map<Long, Long>> serviceErrorCounts = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    
    public void startAggregation() {
        scheduler.scheduleAtFixedRate(this::processWindows, 0, 1, TimeUnit.MINUTES);
    }
    
    public void addError(String service, long timestamp) {
        long windowStart = getWindowStart(timestamp, TimeUnit.MINUTES.toMillis(5));
        
        serviceErrorCounts.computeIfAbsent(service, k -> new ConcurrentHashMap<>())
                          .merge(windowStart, 1L, Long::sum);
    }
    
    private void processWindows() {
        long currentTime = System.currentTimeMillis();
        long cutoffTime = currentTime - TimeUnit.MINUTES.toMillis(10); // Keep 2 windows
        
        for (Map.Entry<String, Map<Long, Long>> serviceEntry : serviceErrorCounts.entrySet()) {
            String service = serviceEntry.getKey();
            Map<Long, Long> windows = serviceEntry.getValue();
            
            // Process completed windows
            windows.entrySet().removeIf(window -> {
                if (window.getKey() < cutoffTime) {
                    long errorCount = window.getValue();
                    
                    // Alert if error rate is high
                    if (errorCount > getErrorThreshold(service)) {
                        alertHighErrorRate(service, errorCount, window.getKey());
                    }
                    
                    // Store aggregated data
                    storeAggregatedData(service, window.getKey(), errorCount);
                    
                    return true; // Remove processed window
                }
                return false;
            });
        }
    }
    
    private long getWindowStart(long timestamp, long windowSize) {
        return (timestamp / windowSize) * windowSize;
    }
    
    private int getErrorThreshold(String service) {
        // Service-specific thresholds
        return switch (service) {
            case "critical-service" -> 10;
            case "background-service" -> 100;
            default -> 50;
        };
    }
}
```

#### Sliding windows
```java
public class SlidingWindowAggregator {
    
    private final Map<String, Deque<TimeValue>> serviceErrors = new ConcurrentHashMap<>();
    private final long windowSize = TimeUnit.MINUTES.toMillis(5);
    private final long slideInterval = TimeUnit.MINUTES.toMillis(1);
    
    public void addError(String service, long timestamp) {
        Deque<TimeValue> errors = serviceErrors.computeIfAbsent(service, 
            k -> new ConcurrentLinkedDeque<>());
        
        errors.addLast(new TimeValue(timestamp, 1L));
        
        // Remove old entries outside the window
        long cutoffTime = timestamp - windowSize;
        while (!errors.isEmpty() && errors.peekFirst().timestamp < cutoffTime) {
            errors.removeFirst();
        }
    }
    
    public long getErrorCountInWindow(String service) {
        Deque<TimeValue> errors = serviceErrors.get(service);
        if (errors == null) return 0;
        
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - windowSize;
        
        return errors.stream()
            .filter(tv -> tv.timestamp >= windowStart)
            .mapToLong(tv -> tv.value)
            .sum();
    }
    
    public void processSlidingWindows() {
        for (Map.Entry<String, Deque<TimeValue>> entry : serviceErrors.entrySet()) {
            String service = entry.getKey();
            Deque<TimeValue> errors = entry.getValue();
            
            long currentCount = getErrorCountInWindow(service);
            long previousCount = getPreviousWindowCount(service);
            
            // Detect sudden increase
            if (currentCount > previousCount * 2 && currentCount > 10) {
                alertErrorSpike(service, currentCount, previousCount);
            }
            
            // Update metrics
            updateMetrics(service, currentCount);
        }
    }
    
    private long getPreviousWindowCount(String service) {
        // Implementation to get count from previous window
        return 0;
    }
    
    static class TimeValue {
        final long timestamp;
        final long value;
        
        TimeValue(long timestamp, long value) {
            this.timestamp = timestamp;
            this.value = value;
        }
    }
}
```

## Complex event processing

### Pattern detection

#### Sequence patterns
```java
public class LogPatternDetector {
    
    private final Map<String, LogSequence> activeSequences = new ConcurrentHashMap<>();
    
    public void processLog(LogEntry log) {
        String correlationId = log.getCorrelationId();
        if (correlationId == null) return;
        
        LogSequence sequence = activeSequences.computeIfAbsent(correlationId, 
            k -> new LogSequence());
        
        sequence.addLog(log);
        
        // Check for patterns
        checkForErrorSequence(sequence);
        checkForSlowOperationSequence(sequence);
        
        // Clean up old sequences
        if (sequence.isExpired()) {
            activeSequences.remove(correlationId);
        }
    }
    
    private void checkForErrorSequence(LogSequence sequence) {
        List<LogEntry> logs = sequence.getLogs();
        
        // Pattern: INFO -> WARN -> ERROR within 5 minutes
        boolean hasInfo = logs.stream().anyMatch(l -> "INFO".equals(l.getLevel()));
        boolean hasWarn = logs.stream().anyMatch(l -> "WARN".equals(l.getLevel()));
        boolean hasError = logs.stream().anyMatch(l -> "ERROR".equals(l.getLevel()));
        
        if (hasInfo && hasWarn && hasError && sequence.getDuration() < 300000) {
            alertEscalatingError(sequence.getCorrelationId(), logs);
        }
    }
    
    private void checkForSlowOperationSequence(LogSequence sequence) {
        List<LogEntry> logs = sequence.getLogs();
        
        // Pattern: Multiple slow operations for same user
        long slowOpsCount = logs.stream()
            .filter(l -> l.getMessage().contains("slow") || 
                        (l.getDuration() != null && l.getDuration() > 5000))
            .count();
        
        if (slowOpsCount >= 3) {
            alertPerformanceIssue(sequence.getCorrelationId(), slowOpsCount);
        }
    }
    
    static class LogSequence {
        private final List<LogEntry> logs = new ArrayList<>();
        private long startTime = System.currentTimeMillis();
        
        public void addLog(LogEntry log) {
            logs.add(log);
            if (logs.size() == 1) {
                startTime = log.getTimestamp();
            }
        }
        
        public List<LogEntry> getLogs() {
            return new ArrayList<>(logs);
        }
        
        public long getDuration() {
            if (logs.isEmpty()) return 0;
            long endTime = logs.get(logs.size() - 1).getTimestamp();
            return endTime - startTime;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > TimeUnit.HOURS.toMillis(1);
        }
        
        public String getCorrelationId() {
            return logs.isEmpty() ? null : logs.get(0).getCorrelationId();
        }
    }
}
```

### Anomaly detection

#### Statistical anomaly detection
```java
public class AnomalyDetector {
    
    private final Map<String, MetricStats> metricStats = new ConcurrentHashMap<>();
    
    public void updateMetric(String metricName, double value, long timestamp) {
        MetricStats stats = metricStats.computeIfAbsent(metricName, k -> new MetricStats());
        stats.addValue(value, timestamp);
        
        // Check for anomalies
        if (stats.isAnomaly(value)) {
            alertAnomaly(metricName, value, stats.getMean(), stats.getStdDev());
        }
    }
    
    public void analyzeLogPatterns(List<LogEntry> logs) {
        // Count error patterns
        Map<String, Integer> errorPatterns = new HashMap<>();
        
        for (LogEntry log : logs) {
            if ("ERROR".equals(log.getLevel())) {
                String pattern = extractErrorPattern(log.getMessage());
                errorPatterns.merge(pattern, 1, Integer::sum);
            }
        }
        
        // Detect unusual error patterns
        for (Map.Entry<String, Integer> entry : errorPatterns.entrySet()) {
            String pattern = entry.getKey();
            int count = entry.getValue();
            
            if (isUnusualErrorPattern(pattern, count)) {
                alertUnusualErrorPattern(pattern, count);
            }
        }
    }
    
    private String extractErrorPattern(String message) {
        // Extract common error patterns
        if (message.contains("NullPointerException")) {
            return "NullPointerException";
        } else if (message.contains("SQLException")) {
            return "SQLException";
        } else if (message.contains("TimeoutException")) {
            return "TimeoutException";
        }
        return "Other";
    }
    
    private boolean isUnusualErrorPattern(String pattern, int count) {
        // Simple threshold-based detection
        return count > 10; // More than 10 occurrences
    }
    
    static class MetricStats {
        private final List<Double> values = new ArrayList<>();
        private final List<Long> timestamps = new ArrayList<>();
        private double mean = 0.0;
        private double variance = 0.0;
        
        public synchronized void addValue(double value, long timestamp) {
            values.add(value);
            timestamps.add(timestamp);
            
            // Keep only recent values (last hour)
            long cutoffTime = timestamp - TimeUnit.HOURS.toMillis(1);
            while (!timestamps.isEmpty() && timestamps.get(0) < cutoffTime) {
                timestamps.remove(0);
                values.remove(0);
            }
            
            // Recalculate statistics
            if (!values.isEmpty()) {
                mean = values.stream().mapToDouble(v -> v).average().orElse(0.0);
                variance = values.stream()
                    .mapToDouble(v -> Math.pow(v - mean, 2))
                    .average()
                    .orElse(0.0);
            }
        }
        
        public boolean isAnomaly(double value) {
            if (values.size() < 10) return false; // Not enough data
            
            double stdDev = Math.sqrt(variance);
            double zScore = Math.abs(value - mean) / stdDev;
            
            return zScore > 3.0; // 3 standard deviations
        }
        
        public double getMean() { return mean; }
        public double getStdDev() { return Math.sqrt(variance); }
    }
}
```

## Windowing и time-based aggregation

### Session windows

#### User session aggregation
```java
public class SessionWindowAggregator {
    
    private final Map<String, UserSession> activeSessions = new ConcurrentHashMap<>();
    private final long sessionTimeout = TimeUnit.MINUTES.toMillis(30);
    
    public void processLog(LogEntry log) {
        String userId = log.getUserId();
        if (userId == null) return;
        
        UserSession session = activeSessions.computeIfAbsent(userId, 
            k -> new UserSession(userId));
        
        session.addLog(log);
        
        // Check for session end
        if (isSessionEndEvent(log)) {
            completeSession(session);
            activeSessions.remove(userId);
        }
    }
    
    public void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        
        activeSessions.entrySet().removeIf(entry -> {
            UserSession session = entry.getValue();
            if (currentTime - session.getLastActivity() > sessionTimeout) {
                completeSession(session);
                return true;
            }
            return false;
        });
    }
    
    private boolean isSessionEndEvent(LogEntry log) {
        return "LOGOUT".equals(log.getEventType()) || 
               "SESSION_EXPIRED".equals(log.getEventType());
    }
    
    private void completeSession(UserSession session) {
        // Calculate session metrics
        long duration = session.getDuration();
        int actionCount = session.getActionCount();
        int errorCount = session.getErrorCount();
        
        // Store session summary
        Map<String, Object> sessionSummary = Map.of(
            "userId", session.getUserId(),
            "startTime", session.getStartTime(),
            "endTime", session.getLastActivity(),
            "duration", duration,
            "actionCount", actionCount,
            "errorCount", errorCount,
            "avgActionTime", duration / Math.max(actionCount, 1)
        );
        
        storeSessionSummary(sessionSummary);
        
        // Check for anomalies
        if (errorCount > actionCount * 0.5) { // More than 50% errors
            alertProblematicSession(session.getUserId(), errorCount, actionCount);
        }
    }
    
    static class UserSession {
        private final String userId;
        private final long startTime;
        private long lastActivity;
        private int actionCount = 0;
        private int errorCount = 0;
        
        public UserSession(String userId) {
            this.userId = userId;
            this.startTime = System.currentTimeMillis();
            this.lastActivity = startTime;
        }
        
        public void addLog(LogEntry log) {
            lastActivity = log.getTimestamp();
            actionCount++;
            
            if ("ERROR".equals(log.getLevel())) {
                errorCount++;
            }
        }
        
        public long getDuration() {
            return lastActivity - startTime;
        }
        
        // Getters
        public String getUserId() { return userId; }
        public long getStartTime() { return startTime; }
        public long getLastActivity() { return lastActivity; }
        public int getActionCount() { return actionCount; }
        public int getErrorCount() { return errorCount; }
    }
}
```

### Custom time windows

#### Business hours aggregation
```java
public class BusinessHoursAggregator {
    
    private final Map<String, BusinessDayStats> dailyStats = new ConcurrentHashMap<>();
    
    public void processLog(LogEntry log) {
        ZonedDateTime logTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(log.getTimestamp()), 
            ZoneId.systemDefault());
        
        // Only process business hours (9 AM - 6 PM, Monday-Friday)
        if (isBusinessHours(logTime)) {
            String dateKey = logTime.toLocalDate().toString();
            
            BusinessDayStats stats = dailyStats.computeIfAbsent(dateKey, 
                k -> new BusinessDayStats(dateKey));
            
            stats.addLog(log);
        }
    }
    
    private boolean isBusinessHours(ZonedDateTime time) {
        DayOfWeek day = time.getDayOfWeek();
        int hour = time.getHour();
        
        return day != DayOfWeek.SATURDAY && 
               day != DayOfWeek.SUNDAY && 
               hour >= 9 && hour < 18;
    }
    
    public void generateBusinessDayReport(String date) {
        BusinessDayStats stats = dailyStats.get(date);
        if (stats == null) return;
        
        Map<String, Object> report = Map.of(
            "date", date,
            "totalLogs", stats.getTotalLogs(),
            "errorCount", stats.getErrorCount(),
            "avgResponseTime", stats.getAvgResponseTime(),
            "topErrors", stats.getTopErrors(),
            "peakHour", stats.getPeakHour()
        );
        
        generateReport(report);
    }
    
    static class BusinessDayStats {
        private final String date;
        private int totalLogs = 0;
        private int errorCount = 0;
        private long totalResponseTime = 0;
        private int responseTimeCount = 0;
        private final Map<String, Integer> errorTypes = new ConcurrentHashMap<>();
        private final int[] hourlyLogs = new int[24];
        
        public BusinessDayStats(String date) {
            this.date = date;
        }
        
        public synchronized void addLog(LogEntry log) {
            totalLogs++;
            
            if ("ERROR".equals(log.getLevel())) {
                errorCount++;
                errorTypes.merge(log.getErrorType(), 1, Integer::sum);
            }
            
            if (log.getDuration() != null) {
                totalResponseTime += log.getDuration();
                responseTimeCount++;
            }
            
            // Record hourly activity
            ZonedDateTime logTime = ZonedDateTime.ofInstant(
                Instant.ofEpochMilli(log.getTimestamp()), ZoneId.systemDefault());
            int hour = logTime.getHour();
            hourlyLogs[hour]++;
        }
        
        // Getters with calculations
        public int getTotalLogs() { return totalLogs; }
        public int getErrorCount() { return errorCount; }
        public double getAvgResponseTime() { 
            return responseTimeCount > 0 ? (double) totalResponseTime / responseTimeCount : 0;
        }
        
        public Map<String, Integer> getTopErrors() {
            return errorTypes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        
        public int getPeakHour() {
            int peakHour = 0;
            int maxLogs = 0;
            
            for (int hour = 0; hour < 24; hour++) {
                if (hourlyLogs[hour] > maxLogs) {
                    maxLogs = hourlyLogs[hour];
                    peakHour = hour;
                }
            }
            
            return peakHour;
        }
    }
}
```

## Error aggregation

### Error pattern analysis

#### Stack trace clustering
```java
public class ErrorAggregator {
    
    private final Map<String, ErrorPattern> errorPatterns = new ConcurrentHashMap<>();
    
    public void processError(LogEntry errorLog) {
        if (!"ERROR".equals(errorLog.getLevel())) return;
        
        String stackTrace = errorLog.getStackTrace();
        if (stackTrace == null) return;
        
        String patternKey = extractErrorPattern(stackTrace);
        
        ErrorPattern pattern = errorPatterns.computeIfAbsent(patternKey, 
            k -> new ErrorPattern(patternKey, stackTrace));
        
        pattern.addOccurrence(errorLog);
        
        // Check for increasing frequency
        if (pattern.shouldAlert()) {
            alertErrorSpike(pattern);
        }
    }
    
    private String extractErrorPattern(String stackTrace) {
        // Extract key frames from stack trace
        String[] lines = stackTrace.split("\n");
        List<String> keyFrames = new ArrayList<>();
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("at ") && !line.contains("java.lang") && !line.contains("sun.")) {
                keyFrames.add(line);
                if (keyFrames.size() >= 5) break; // Top 5 frames
            }
        }
        
        return String.join("|", keyFrames);
    }
    
    static class ErrorPattern {
        private final String patternKey;
        private final String sampleStackTrace;
        private final List<LogEntry> occurrences = new ArrayList<>();
        private final long firstSeen = System.currentTimeMillis();
        
        public ErrorPattern(String patternKey, String sampleStackTrace) {
            this.patternKey = patternKey;
            this.sampleStackTrace = sampleStackTrace;
        }
        
        public synchronized void addOccurrence(LogEntry errorLog) {
            occurrences.add(errorLog);
        }
        
        public synchronized boolean shouldAlert() {
            if (occurrences.size() < 5) return false;
            
            // Check if errors are increasing
            long recentErrors = occurrences.stream()
                .filter(log -> log.getTimestamp() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5))
                .count();
            
            return recentErrors >= 3;
        }
        
        public synchronized Map<String, Object> getSummary() {
            return Map.of(
                "pattern", patternKey,
                "totalOccurrences", occurrences.size(),
                "firstSeen", firstSeen,
                "lastSeen", occurrences.get(occurrences.size() - 1).getTimestamp(),
                "affectedServices", getAffectedServices(),
                "sampleStackTrace", sampleStackTrace
            );
        }
        
        private Set<String> getAffectedServices() {
            return occurrences.stream()
                .map(LogEntry::getService)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        }
    }
}
```

### Error rate monitoring

#### Adaptive thresholding
```java
public class AdaptiveErrorMonitor {
    
    private final Map<String, ErrorRateStats> serviceStats = new ConcurrentHashMap<>();
    
    public void recordError(String service, long timestamp) {
        ErrorRateStats stats = serviceStats.computeIfAbsent(service, 
            k -> new ErrorRateStats(service));
        
        stats.recordError(timestamp);
        
        if (stats.isAboveThreshold()) {
            alertHighErrorRate(service, stats.getCurrentRate(), stats.getThreshold());
        }
    }
    
    public void updateBaselines() {
        // Update adaptive baselines based on historical data
        for (ErrorRateStats stats : serviceStats.values()) {
            stats.updateBaseline();
        }
    }
    
    static class ErrorRateStats {
        private final String service;
        private final Deque<Long> errorTimestamps = new ConcurrentLinkedDeque<>();
        private double baselineRate = 0.1; // 10% error rate baseline
        private final long windowSize = TimeUnit.MINUTES.toMillis(5);
        
        public ErrorRateStats(String service) {
            this.service = service;
        }
        
        public synchronized void recordError(long timestamp) {
            errorTimestamps.addLast(timestamp);
            
            // Remove old errors outside the window
            long cutoffTime = timestamp - windowSize;
            while (!errorTimestamps.isEmpty() && errorTimestamps.peekFirst() < cutoffTime) {
                errorTimestamps.removeFirst();
            }
        }
        
        public synchronized double getCurrentRate() {
            if (errorTimestamps.isEmpty()) return 0.0;
            
            long timeSpan = errorTimestamps.peekLast() - errorTimestamps.peekFirst();
            if (timeSpan == 0) return 0.0;
            
            return (double) errorTimestamps.size() / (timeSpan / 1000.0); // errors per second
        }
        
        public synchronized double getThreshold() {
            // Adaptive threshold: 3x baseline or minimum 1 error per minute
            return Math.max(baselineRate * 3, 1.0 / 60.0);
        }
        
        public synchronized boolean isAboveThreshold() {
            return getCurrentRate() > getThreshold();
        }
        
        public synchronized void updateBaseline() {
            // Update baseline based on historical data
            // This is a simplified implementation
            double currentRate = getCurrentRate();
            if (currentRate > 0) {
                baselineRate = (baselineRate + currentRate) / 2.0; // Moving average
            }
        }
    }
}
```

## Performance metrics

### Response time aggregation

#### Percentile calculations
```java
public class PerformanceAggregator {
    
    private final Map<String, ResponseTimeStats> operationStats = new ConcurrentHashMap<>();
    
    public void recordResponseTime(String operation, long responseTime, long timestamp) {
        ResponseTimeStats stats = operationStats.computeIfAbsent(operation, 
            k -> new ResponseTimeStats(operation));
        
        stats.addResponseTime(responseTime, timestamp);
        
        // Check for performance degradation
        if (stats.isPerformanceDegraded()) {
            alertPerformanceDegradation(operation, stats.getP95ResponseTime());
        }
    }
    
    static class ResponseTimeStats {
        private final String operation;
        private final List<Long> responseTimes = new ArrayList<>();
        private long lastCleanup = System.currentTimeMillis();
        private final long cleanupInterval = TimeUnit.MINUTES.toMillis(5);
        
        public ResponseTimeStats(String operation) {
            this.operation = operation;
        }
        
        public synchronized void addResponseTime(long responseTime, long timestamp) {
            responseTimes.add(responseTime);
            
            // Periodic cleanup to prevent memory growth
            if (timestamp - lastCleanup > cleanupInterval) {
                cleanup();
                lastCleanup = timestamp;
            }
        }
        
        public synchronized long getP95ResponseTime() {
            if (responseTimes.isEmpty()) return 0;
            
            Collections.sort(responseTimes);
            int index = (int) Math.ceil(0.95 * responseTimes.size()) - 1;
            return responseTimes.get(Math.max(0, index));
        }
        
        public synchronized boolean isPerformanceDegraded() {
            if (responseTimes.size() < 100) return false; // Not enough data
            
            long p95 = getP95ResponseTime();
            long p50 = getPercentile(0.5);
            
            // Alert if P95 is more than 5x P50 or > 10 seconds
            return p95 > Math.max(p50 * 5, 10000);
        }
        
        private long getPercentile(double percentile) {
            if (responseTimes.isEmpty()) return 0;
            
            Collections.sort(responseTimes);
            int index = (int) Math.ceil(percentile * responseTimes.size()) - 1;
            return responseTimes.get(Math.max(0, index));
        }
        
        private void cleanup() {
            // Keep only recent data (last 1000 measurements)
            if (responseTimes.size() > 1000) {
                responseTimes.subList(0, responseTimes.size() - 1000).clear();
            }
        }
    }
}
```

### Throughput monitoring

#### Request rate aggregation
```java
public class ThroughputMonitor {
    
    private final Map<String, ThroughputStats> endpointStats = new ConcurrentHashMap<>();
    
    public void recordRequest(String endpoint, long timestamp) {
        ThroughputStats stats = endpointStats.computeIfAbsent(endpoint, 
            k -> new ThroughputStats(endpoint));
        
        stats.recordRequest(timestamp);
    }
    
    public Map<String, Double> getCurrentThroughput() {
        Map<String, Double> throughput = new HashMap<>();
        
        for (Map.Entry<String, ThroughputStats> entry : endpointStats.entrySet()) {
            throughput.put(entry.getKey(), entry.getValue().getRequestsPerSecond());
        }
        
        return throughput;
    }
    
    static class ThroughputStats {
        private final String endpoint;
        private final Deque<Long> requestTimestamps = new ConcurrentLinkedDeque<>();
        private final long windowSize = TimeUnit.MINUTES.toMillis(1);
        
        public ThroughputStats(String endpoint) {
            this.endpoint = endpoint;
        }
        
        public synchronized void recordRequest(long timestamp) {
            requestTimestamps.addLast(timestamp);
            
            // Remove old requests outside the window
            long cutoffTime = timestamp - windowSize;
            while (!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() < cutoffTime) {
                requestTimestamps.removeFirst();
            }
        }
        
        public synchronized double getRequestsPerSecond() {
            if (requestTimestamps.isEmpty()) return 0.0;
            
            long timeSpan = requestTimestamps.peekLast() - requestTimestamps.peekFirst();
            if (timeSpan == 0) return 0.0;
            
            return (double) requestTimestamps.size() / (timeSpan / 1000.0);
        }
    }
}
```

## Data enrichment

### Log context enrichment

#### External data lookup
```java
@Service
public class LogEnrichmentService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private GeoLocationService geoService;
    
    public Map<String, Object> enrichLogEntry(Map<String, Object> logEntry) {
        Map<String, Object> enriched = new HashMap<>(logEntry);
        
        // Enrich user information
        String userId = (String) logEntry.get("userId");
        if (userId != null) {
            enriched.putAll(enrichUserInfo(userId));
        }
        
        // Enrich geo information
        String ipAddress = (String) logEntry.get("clientIP");
        if (ipAddress != null) {
            enriched.putAll(enrichGeoInfo(ipAddress));
        }
        
        // Add system context
        enriched.put("serverHostname", getServerHostname());
        enriched.put("serverRegion", getServerRegion());
        enriched.put("enrichmentTimestamp", System.currentTimeMillis());
        
        return enriched;
    }
    
    private Map<String, Object> enrichUserInfo(String userId) {
        try {
            User user = userService.getUserById(userId);
            return Map.of(
                "userEmail", user.getEmail(),
                "userRole", user.getRole(),
                "userRegistrationDate", user.getRegistrationDate(),
                "userLastLogin", user.getLastLoginDate()
            );
        } catch (Exception e) {
            return Map.of("userEnrichmentError", e.getMessage());
        }
    }
    
    private Map<String, Object> enrichGeoInfo(String ipAddress) {
        try {
            GeoLocation geo = geoService.lookup(ipAddress);
            return Map.of(
                "country", geo.getCountry(),
                "city", geo.getCity(),
                "timezone", geo.getTimezone(),
                "isp", geo.getIsp()
            );
        } catch (Exception e) {
            return Map.of("geoEnrichmentError", e.getMessage());
        }
    }
    
    private String getServerHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
    
    private String getServerRegion() {
        return System.getProperty("server.region", "unknown");
    }
}
```

### Business context enrichment

#### Domain-specific enrichment
```java
public class BusinessContextEnricher {
    
    public Map<String, Object> enrichOrderLog(Map<String, Object> logEntry) {
        Map<String, Object> enriched = new HashMap<>(logEntry);
        
        String orderId = (String) logEntry.get("orderId");
        if (orderId != null) {
            enriched.putAll(enrichOrderInfo(orderId));
        }
        
        String customerId = (String) logEntry.get("customerId");
        if (customerId != null) {
            enriched.putAll(enrichCustomerInfo(customerId));
        }
        
        // Add business metrics
        enriched.put("businessHour", isBusinessHour());
        enriched.put("peakSeason", isPeakSeason());
        
        return enriched;
    }
    
    public Map<String, Object> enrichPaymentLog(Map<String, Object> logEntry) {
        Map<String, Object> enriched = new HashMap<>(logEntry);
        
        String paymentMethod = (String) logEntry.get("paymentMethod");
        if (paymentMethod != null) {
            enriched.put("paymentType", categorizePaymentMethod(paymentMethod));
            enriched.put("paymentRisk", assessPaymentRisk(paymentMethod));
        }
        
        BigDecimal amount = (BigDecimal) logEntry.get("amount");
        if (amount != null) {
            enriched.put("amountCategory", categorizeAmount(amount));
            enriched.put("currency", "USD"); // Assume default
        }
        
        return enriched;
    }
    
    private Map<String, Object> enrichOrderInfo(String orderId) {
        // Simulate order lookup
        return Map.of(
            "orderStatus", "CONFIRMED",
            "orderTotal", 299.99,
            "orderItems", 3,
            "shippingMethod", "STANDARD"
        );
    }
    
    private Map<String, Object> enrichCustomerInfo(String customerId) {
        // Simulate customer lookup
        return Map.of(
            "customerType", "PREMIUM",
            "customerSince", "2020-01-15",
            "totalOrders", 45,
            "loyaltyPoints", 1250
        );
    }
    
    private boolean isBusinessHour() {
        ZonedDateTime now = ZonedDateTime.now();
        int hour = now.getHour();
        DayOfWeek day = now.getDayOfWeek();
        
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY && 
               hour >= 9 && hour <= 17;
    }
    
    private boolean isPeakSeason() {
        Month currentMonth = ZonedDateTime.now().getMonth();
        return currentMonth == Month.NOVEMBER || currentMonth == Month.DECEMBER;
    }
    
    private String categorizePaymentMethod(String method) {
        if (method.contains("CREDIT") || method.contains("DEBIT")) {
            return "CARD";
        } else if (method.contains("PAYPAL")) {
            return "DIGITAL_WALLET";
        } else if (method.contains("BANK")) {
            return "BANK_TRANSFER";
        }
        return "OTHER";
    }
    
    private String assessPaymentRisk(String method) {
        // Simple risk assessment
        if (method.contains("INTERNATIONAL")) {
            return "HIGH";
        } else if (method.contains("PREPAID")) {
            return "MEDIUM";
        }
        return "LOW";
    }
    
    private String categorizeAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.valueOf(100)) < 0) {
            return "SMALL";
        } else if (amount.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return "MEDIUM";
        }
        return "LARGE";
    }
}
```

## Storage optimization

### Index optimization

#### Elasticsearch index optimization
```bash
# Create optimized index for log aggregation
curl -X PUT "localhost:9200/logs-aggregated" \
  -H 'Content-Type: application/json' \
  -d '{
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "refresh_interval": "30s",
      "index.codec": "best_compression"
    },
    "mappings": {
      "properties": {
        "@timestamp": {
          "type": "date",
          "format": "strict_date_optional_time"
        },
        "aggregationWindow": {
          "type": "date",
          "format": "yyyy-MM-dd'\''T'\''HH:mm:ss.SSSZ"
        },
        "service": {
          "type": "keyword"
        },
        "errorCount": {
          "type": "integer"
        },
        "avgResponseTime": {
          "type": "double"
        },
        "p95ResponseTime": {
          "type": "double"
        },
        "totalRequests": {
          "type": "long"
        },
        "errorRate": {
          "type": "double"
        }
      }
    }
  }'

# Create alias for easy querying
curl -X POST "localhost:9200/_aliases" \
  -H 'Content-Type: application/json' \
  -d '{
    "actions": [
      {
        "add": {
          "index": "logs-aggregated-*",
          "alias": "logs-aggregated"
        }
      }
    ]
  }'
```

### Data lifecycle management

#### Automated cleanup
```java
@Service
public class DataLifecycleManager {
    
    @Autowired
    private ElasticsearchClient esClient;
    
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void manageDataLifecycle() {
        try {
            // Delete old raw logs (keep 30 days)
            deleteOldIndices("logs-", 30);
            
            // Archive aggregated data older than 90 days
            archiveOldAggregatedData(90);
            
            // Optimize remaining indices
            optimizeIndices("logs-aggregated-");
            
        } catch (Exception e) {
            logger.error("Failed to manage data lifecycle", e);
        }
    }
    
    private void deleteOldIndices(String prefix, int daysToKeep) throws IOException {
        String indexPattern = prefix + "*";
        long cutoffDate = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysToKeep);
        
        SearchRequest searchRequest = new SearchRequest(indexPattern);
        searchRequest.source(new SearchSourceBuilder()
            .query(QueryBuilders.rangeQuery("@timestamp").lt(cutoffDate))
            .size(0));
        
        SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);
        
        // Delete indices with old data
        for (String index : getIndicesWithOldData(response)) {
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest(index);
            esClient.indices().delete(deleteRequest, RequestOptions.DEFAULT);
            logger.info("Deleted old index: {}", index);
        }
    }
    
    private void archiveOldAggregatedData(int daysToKeep) throws IOException {
        // Move old aggregated data to cheaper storage
        String archiveIndex = "logs-archived-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        
        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices("logs-aggregated-*");
        reindexRequest.setDestIndex(archiveIndex);
        reindexRequest.setSourceQuery(QueryBuilders.rangeQuery("@timestamp")
            .lt(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(daysToKeep)));
        
        BulkByScrollResponse response = esClient.reindex(reindexRequest, RequestOptions.DEFAULT);
        logger.info("Archived {} documents to {}", response.getTotal(), archiveIndex);
    }
    
    private void optimizeIndices(String indexPattern) throws IOException {
        // Force merge for better query performance
        ForceMergeRequest mergeRequest = new ForceMergeRequest(indexPattern);
        mergeRequest.maxNumSegments(1);
        
        esClient.indices().forcemerge(mergeRequest, RequestOptions.DEFAULT);
        logger.info("Optimized indices: {}", indexPattern);
    }
}
```

## Best Practices

### 1. Architecture design

#### Scalable aggregation pipeline
```java
@Configuration
public class AggregationPipelineConfig {
    
    @Bean
    public KafkaStreamsConfiguration kafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "log-aggregation-service");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        
        // Performance tuning
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.POLL_MS_CONFIG, 100);
        props.put(StreamsConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        
        return new KafkaStreamsConfiguration(props);
    }
    
    @Bean
    public KStream<String, String> logAggregationStream(KafkaStreamsConfiguration config) {
        StreamsBuilder builder = new StreamsBuilder();
        
        // Source stream
        KStream<String, String> logs = builder.stream("logs.raw");
        
        // Parse and enrich
        KStream<String, LogEntry> parsedLogs = logs
            .mapValues(this::parseLogEntry)
            .filter((key, log) -> log != null);
        
        // Error aggregation
        parsedLogs
            .filter((key, log) -> "ERROR".equals(log.getLevel()))
            .groupBy((key, log) -> log.getService())
            .windowedBy(TimeWindows.of(Duration.ofMinutes(5)))
            .count()
            .toStream()
            .map((windowedKey, count) -> KeyValue.pair(
                windowedKey.key(),
                createErrorSummary(windowedKey, count)))
            .to("logs.errors.aggregated");
        
        // Performance aggregation
        parsedLogs
            .filter((key, log) -> log.getResponseTime() != null)
            .groupBy((key, log) -> log.getOperation())
            .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
            .aggregate(
                () -> new PerformanceStats(),
                (key, log, stats) -> stats.addResponseTime(log.getResponseTime()),
                Materialized.with(Serdes.String(), new PerformanceStatsSerde()))
            .toStream()
            .map((windowedKey, stats) -> KeyValue.pair(
                windowedKey.key(),
                createPerformanceSummary(windowedKey, stats)))
            .to("logs.performance.aggregated");
        
        return parsedLogs;
    }
}
```

### 2. Performance optimization

#### Parallel processing
```java
@Service
public class ParallelAggregationService {
    
    @Autowired
    private ExecutorService aggregationExecutor;
    
    public CompletableFuture<Map<String, Object>> aggregateLogsAsync(List<LogEntry> logs) {
        return CompletableFuture.supplyAsync(() -> {
            // Split logs by service
            Map<String, List<LogEntry>> logsByService = logs.stream()
                .collect(Collectors.groupingBy(LogEntry::getService));
            
            // Process each service in parallel
            Map<String, CompletableFuture<ServiceStats>> serviceFutures = logsByService.entrySet()
                .stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> CompletableFuture.supplyAsync(
                        () -> aggregateServiceLogs(entry.getValue()),
                        aggregationExecutor
                    )
                ));
            
            // Combine results
            return serviceFutures.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        try {
                            return entry.getValue().get(30, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            logger.error("Failed to aggregate service logs", e);
                            return new ServiceStats();
                        }
                    }
                ));
        }, aggregationExecutor);
    }
    
    private ServiceStats aggregateServiceLogs(List<LogEntry> logs) {
        ServiceStats stats = new ServiceStats();
        
        for (LogEntry log : logs) {
            stats.addLog(log);
        }
        
        return stats;
    }
    
    static class ServiceStats {
        private int totalLogs = 0;
        private int errorCount = 0;
        private long totalResponseTime = 0;
        private int responseTimeCount = 0;
        
        public void addLog(LogEntry log) {
            totalLogs++;
            
            if ("ERROR".equals(log.getLevel())) {
                errorCount++;
            }
            
            if (log.getResponseTime() != null) {
                totalResponseTime += log.getResponseTime();
                responseTimeCount++;
            }
        }
        
        // Getters with calculations
        public int getTotalLogs() { return totalLogs; }
        public int getErrorCount() { return errorCount; }
        public double getAvgResponseTime() {
            return responseTimeCount > 0 ? (double) totalResponseTime / responseTimeCount : 0;
        }
    }
}
```

### 3. Error handling

#### Fault-tolerant aggregation
```java
@Service
public class FaultTolerantAggregator {
    
    @Autowired
    private DeadLetterQueue deadLetterQueue;
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    public void aggregateWithFaultTolerance(List<LogEntry> logs) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("aggregation");
        
        try {
            circuitBreaker.executeCallable(() -> {
                for (LogEntry log : logs) {
                    try {
                        processLogEntry(log);
                    } catch (Exception e) {
                        logger.warn("Failed to process log entry: {}", log.getId(), e);
                        deadLetterQueue.send(log);
                    }
                }
                return null;
            });
            
        } catch (CallNotPermittedException e) {
            // Circuit breaker is open
            logger.warn("Aggregation circuit breaker is open, queuing logs for later processing");
            deadLetterQueue.sendAll(logs);
            
        } catch (Exception e) {
            logger.error("Failed to execute log aggregation", e);
            deadLetterQueue.sendAll(logs);
        }
    }
    
    private void processLogEntry(LogEntry log) {
        // Aggregation logic with potential failures
        if (log.getService() == null) {
            throw new IllegalArgumentException("Service is required");
        }
        
        // Simulate external service call that might fail
        callExternalAggregationService(log);
    }
    
    private void callExternalAggregationService(LogEntry log) {
        // Simulate network call
        if (Math.random() < 0.1) { // 10% failure rate
            throw new RuntimeException("External service unavailable");
        }
    }
    
    static class DeadLetterQueue {
        
        private final Queue<LogEntry> queue = new ConcurrentLinkedQueue<>();
        
        public void send(LogEntry log) {
            queue.add(log);
            logger.info("Added log to dead letter queue: {}", log.getId());
        }
        
        public void sendAll(List<LogEntry> logs) {
            queue.addAll(logs);
            logger.info("Added {} logs to dead letter queue", logs.size());
        }
        
        @Scheduled(fixedRate = 300000) // Every 5 minutes
        public void processDeadLetters() {
            List<LogEntry> batch = new ArrayList<>();
            
            LogEntry log;
            while ((log = queue.poll()) != null && batch.size() < 100) {
                batch.add(log);
            }
            
            if (!batch.isEmpty()) {
                try {
                    // Retry processing
                    aggregateBatch(batch);
                    logger.info("Successfully processed {} dead letter logs", batch.size());
                    
                } catch (Exception e) {
                    logger.error("Failed to process dead letter batch", e);
                    // Put back in queue or send to permanent failure queue
                    queue.addAll(batch);
                }
            }
        }
        
        private void aggregateBatch(List<LogEntry> logs) {
            // Retry aggregation logic
            logs.forEach(this::processLogEntry);
        }
    }
}
```

### 4. Monitoring and alerting

#### Aggregation metrics
```java
@Service
public class AggregationMetricsCollector {
    
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> aggregationCounters = new ConcurrentHashMap<>();
    private final Map<String, Timer> aggregationTimers = new ConcurrentHashMap<>();
    
    @Autowired
    public AggregationMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public <T> T measureAggregation(String aggregationType, Supplier<T> aggregationFunction) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            T result = aggregationFunction.get();
            
            sample.stop(Timer.builder("log.aggregation.duration")
                .tag("type", aggregationType)
                .register(meterRegistry));
            
            // Count successful aggregations
            Counter.builder("log.aggregation.count")
                .tag("type", aggregationType)
                .tag("status", "success")
                .register(meterRegistry)
                .increment();
            
            return result;
            
        } catch (Exception e) {
            sample.stop(Timer.builder("log.aggregation.duration")
                .tag("type", aggregationType)
                .tag("status", "error")
                .register(meterRegistry));
            
            // Count failed aggregations
            Counter.builder("log.aggregation.count")
                .tag("type", aggregationType)
                .tag("status", "error")
                .tag("error_type", e.getClass().getSimpleName())
                .register(meterRegistry)
                .increment();
            
            throw e;
        }
    }
    
    public void recordAggregationSize(String aggregationType, int size) {
        Gauge.builder("log.aggregation.size")
            .tag("type", aggregationType)
            .register(meterRegistry)
            .set(size);
    }
    
    public void alertOnAggregationFailure(String aggregationType, Exception error) {
        logger.error("Aggregation failure for type: {}", aggregationType, error);
        
        // Send alert if needed
        if (isCriticalAggregation(aggregationType)) {
            alertService.sendAlert(
                "Critical Aggregation Failure",
                String.format("Aggregation failed for %s: %s", aggregationType, error.getMessage()),
                AlertSeverity.CRITICAL
            );
        }
    }
    
    private boolean isCriticalAggregation(String type) {
        return "errors".equals(type) || "security".equals(type);
    }
}
```

## Troubleshooting

### Распространенные проблемы

#### Data skew in aggregation
```java
// Problem: Some aggregation keys have much more data than others
// Solution: Implement key partitioning or sampling

public class BalancedAggregator {
    
    private final Map<String, List<LogEntry>> partitionedData = new ConcurrentHashMap<>();
    private final int maxPartitionSize = 10000;
    
    public void addLogBalanced(LogEntry log) {
        String key = getAggregationKey(log);
        
        partitionedData.compute(key, (k, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }
            
            // If partition is too large, process it immediately
            if (list.size() >= maxPartitionSize) {
                processPartitionAsync(k, list);
                list = new ArrayList<>();
            }
            
            list.add(log);
            return list;
        });
    }
    
    public void flushAllPartitions() {
        for (Map.Entry<String, List<LogEntry>> entry : partitionedData.entrySet()) {
            processPartitionAsync(entry.getKey(), entry.getValue());
        }
        partitionedData.clear();
    }
    
    private void processPartitionAsync(String key, List<LogEntry> logs) {
        CompletableFuture.runAsync(() -> {
            try {
                processPartition(key, logs);
            } catch (Exception e) {
                logger.error("Failed to process partition: {}", key, e);
            }
        });
    }
    
    private void processPartition(String key, List<LogEntry> logs) {
        // Aggregation logic for this partition
        Map<String, Object> result = aggregateLogs(logs);
        storeResult(key, result);
    }
    
    private String getAggregationKey(LogEntry log) {
        // Use compound key to distribute load
        return log.getService() + ":" + (Math.abs(log.getId().hashCode()) % 10);
    }
}
```

#### Latency issues in streaming
```java
// Problem: High latency in streaming aggregation
// Solution: Optimize windowing and state management

@Configuration
public class OptimizedStreamingConfig {
    
    @Bean
    public StreamsBuilderFactoryBean streamsBuilderFactoryBean() {
        Map<String, Object> props = new HashMap<>();
        
        // Performance optimizations
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        props.put(StreamsConfig.POLL_MS_CONFIG, 100);
        props.put(StreamsConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        
        // State store optimizations
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10485760); // 10MB
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        
        // Windowing optimizations
        props.put(StreamsConfig.WINDOW_STORE_CHANGE_LOG_ADDITIONAL_RETENTION_MS_CONFIG, 86400000); // 24 hours
        
        StreamsBuilderFactoryBean factoryBean = new StreamsBuilderFactoryBean();
        factoryBean.setStreamsConfiguration(props);
        
        return factoryBean;
    }
    
    @Bean
    public KStream<String, String> optimizedAggregationStream(StreamsBuilder builder) {
        // Use compact topics for better performance
        KStream<String, String> logs = builder.stream("logs.raw", 
            Consumed.with(Serdes.String(), Serdes.String())
                .withTimestampExtractor(new LogTimestampExtractor()));
        
        // Optimize windowing
        TimeWindows window = TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5))
            .advanceBy(Duration.ofMinutes(1)); // Sliding window
        
        logs.groupByKey()
            .windowedBy(window)
            .count()
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
            .toStream()
            .map((windowedKey, count) -> {
                // Emit only when window closes to reduce latency
                return KeyValue.pair(windowedKey.key(), count.toString());
            })
            .to("logs.aggregated");
        
        return logs;
    }
    
    static class LogTimestampExtractor implements TimestampExtractor {
        
        @Override
        public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
            // Extract timestamp from log JSON
            try {
                String json = (String) record.value();
                ObjectNode node = (ObjectNode) new ObjectMapper().readTree(json);
                return node.get("@timestamp").asLong();
            } catch (Exception e) {
                return partitionTime;
            }
        }
    }
}
```

#### Memory pressure
```java
// Problem: High memory usage in aggregation
// Solution: Implement memory-bounded aggregation

public class MemoryBoundedAggregator {
    
    private final Map<String, Deque<LogEntry>> boundedBuffers = new ConcurrentHashMap<>();
    private final int maxBufferSize = 1000;
    private final int maxTotalMemory = 100 * 1024 * 1024; // 100MB
    
    public void addLogMemoryBounded(LogEntry log) {
        String key = getAggregationKey(log);
        
        Deque<LogEntry> buffer = boundedBuffers.computeIfAbsent(key, 
            k -> new ConcurrentLinkedDeque<>());
        
        // Check memory bounds
        if (getTotalMemoryUsage() > maxTotalMemory) {
            // Spill to disk or process immediately
            spillToDisk(buffer);
            buffer.clear();
        }
        
        // Add to buffer
        buffer.addLast(log);
        
        // Evict old entries if buffer is full
        if (buffer.size() > maxBufferSize) {
            LogEntry evicted = buffer.removeFirst();
            processEvictedEntry(evicted);
        }
    }
    
    public void processAllBuffers() {
        for (Map.Entry<String, Deque<LogEntry>> entry : boundedBuffers.entrySet()) {
            processBuffer(entry.getKey(), entry.getValue());
        }
        boundedBuffers.clear();
    }
    
    private void spillToDisk(Deque<LogEntry> buffer) {
        // Write buffer to temporary file
        try {
            Path tempFile = Files.createTempFile("aggregation-spill-", ".json");
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                for (LogEntry log : buffer) {
                    writer.write(new ObjectMapper().writeValueAsString(log));
                    writer.newLine();
                }
            }
            
            // Schedule processing of spilled data
            scheduleSpilledDataProcessing(tempFile);
            
        } catch (IOException e) {
            logger.error("Failed to spill buffer to disk", e);
        }
    }
    
    private long getTotalMemoryUsage() {
        // Estimate memory usage (simplified)
        return boundedBuffers.values().stream()
            .mapToLong(buffer -> buffer.size() * 1024L) // Rough estimate per entry
            .sum();
    }
    
    private void processEvictedEntry(LogEntry log) {
        // Process immediately or add to low-priority queue
        processSingleLog(log);
    }
    
    private void scheduleSpilledDataProcessing(Path file) {
        // Schedule background processing
        CompletableFuture.runAsync(() -> processSpilledFile(file));
    }
    
    private void processSpilledFile(Path file) {
        try (Stream<String> lines = Files.lines(file)) {
            List<LogEntry> logs = lines
                .map(line -> {
                    try {
                        return new ObjectMapper().readValue(line, LogEntry.class);
                    } catch (JsonProcessingException e) {
                        logger.warn("Failed to parse spilled log entry", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            processBatch(logs);
            Files.delete(file);
            
        } catch (IOException e) {
            logger.error("Failed to process spilled file", e);
        }
    }
}
```

### Debug techniques

#### Aggregation pipeline monitoring
```java
@Service
public class AggregationPipelineMonitor {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Autowired
    private KafkaStreams kafkaStreams;
    
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void monitorAggregationPipeline() {
        // Kafka Streams metrics
        if (kafkaStreams != null) {
            Map<MetricName, ? extends Metric> metrics = kafkaStreams.metrics();
            
            for (Map.Entry<MetricName, ? extends Metric> entry : metrics.entrySet()) {
                MetricName name = entry.getKey();
                Metric metric = entry.getValue();
                
                if (name.name().contains("commit") || name.name().contains("poll")) {
                    Gauge.builder("kafka.streams.operation")
                        .tag("operation", name.name())
                        .register(meterRegistry)
                        .set(metric.metricValue().doubleValue());
                }
            }
        }
        
        // Aggregation-specific metrics
        updateAggregationMetrics();
    }
    
    private void updateAggregationMetrics() {
        // Custom aggregation metrics
        Map<String, Integer> queueSizes = getAggregationQueueSizes();
        
        for (Map.Entry<String, Integer> entry : queueSizes.entrySet()) {
            Gauge.builder("aggregation.queue.size")
                .tag("queue", entry.getKey())
                .register(meterRegistry)
                .set(entry.getValue());
        }
        
        // Processing rates
        Map<String, Double> processingRates = getProcessingRates();
        
        for (Map.Entry<String, Double> entry : processingRates.entrySet()) {
            Gauge.builder("aggregation.processing.rate")
                .tag("aggregator", entry.getKey())
                .register(meterRegistry)
                .set(entry.getValue());
        }
    }
    
    private Map<String, Integer> getAggregationQueueSizes() {
        // Implementation to get queue sizes from aggregation components
        return Map.of(
            "error-aggregator", 150,
            "performance-aggregator", 75,
            "business-aggregator", 200
        );
    }
    
    private Map<String, Double> getProcessingRates() {
        // Implementation to calculate processing rates
        return Map.of(
            "error-aggregator", 1250.5,
            "performance-aggregator", 890.2,
            "business-aggregator", 2100.8
        );
    }
    
    public void logPipelineStatus() {
        logger.info("Aggregation pipeline status: queues={}, rates={}", 
                   getAggregationQueueSizes(), getProcessingRates());
    }
}
```

#### Aggregation result validation
```java
public class AggregationValidator {
    
    public void validateAggregationResults(Map<String, Object> results, List<LogEntry> sourceLogs) {
        ValidationResult validation = new ValidationResult();
        
        // Check counts
        int expectedTotalLogs = sourceLogs.size();
        int actualTotalLogs = results.values().stream()
            .filter(value -> value instanceof Number)
            .mapToInt(value -> ((Number) value).intValue())
            .sum();
        
        if (expectedTotalLogs != actualTotalLogs) {
            validation.addError("Total log count mismatch: expected %d, got %d", 
                              expectedTotalLogs, actualTotalLogs);
        }
        
        // Check error rates
        long expectedErrors = sourceLogs.stream()
            .mapToLong(log -> "ERROR".equals(log.getLevel()) ? 1 : 0)
            .sum();
        
        Object errorCount = results.get("totalErrors");
        if (errorCount instanceof Number && ((Number) errorCount).longValue() != expectedErrors) {
            validation.addError("Error count mismatch: expected %d, got %d", 
                              expectedErrors, ((Number) errorCount).longValue());
        }
        
        // Check time ranges
        Instant expectedStartTime = sourceLogs.stream()
            .map(LogEntry::getTimestamp)
            .min(Instant::compareTo)
            .orElse(Instant.now());
        
        Instant expectedEndTime = sourceLogs.stream()
            .map(LogEntry::getTimestamp)
            .max(Instant::compareTo)
            .orElse(Instant.now());
        
        Object startTime = results.get("windowStart");
        Object endTime = results.get("windowEnd");
        
        if (startTime instanceof Instant && endTime instanceof Instant) {
            Instant actualStart = (Instant) startTime;
            Instant actualEnd = (Instant) endTime;
            
            if (!actualStart.equals(expectedStartTime) || !actualEnd.equals(expectedEndTime)) {
                validation.addWarning("Time range mismatch: expected [%s, %s], got [%s, %s]",
                                    expectedStartTime, expectedEndTime, actualStart, actualEnd);
            }
        }
        
        // Log validation results
        if (validation.hasErrors()) {
            logger.error("Aggregation validation failed: {}", validation.getErrors());
        } else if (validation.hasWarnings()) {
            logger.warn("Aggregation validation warnings: {}", validation.getWarnings());
        } else {
            logger.info("Aggregation validation passed");
        }
    }
    
    static class ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();
        
        public void addError(String message, Object... args) {
            errors.add(String.format(message, args));
        }
        
        public void addWarning(String message, Object... args) {
            warnings.add(String.format(message, args));
        }
        
        public boolean hasErrors() { return !errors.isEmpty(); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }
    }
}
```

## Заключение

**Агрегация логов** — это критически важная функция для анализа больших объемов данных в распределенных системах. Она позволяет извлекать ценную информацию из логов, выявлять паттерны и принимать обоснованные решения.

### Ключевые возможности:

1. **Real-time Aggregation** — потоковая обработка логов с низкой латентностью
2. **Batch Processing** — пакетная обработка для комплексного анализа
3. **Time-based Windows** — агрегация по временным интервалам
4. **Complex Event Processing** — выявление сложных паттернов
5. **Error Aggregation** — анализ и группировка ошибок
6. **Performance Metrics** — сбор метрик производительности
7. **Kafka Integration** — использование Kafka для надежной доставки
8. **Flink/Spark Processing** — мощные фреймворки для обработки

### Архитектурные преимущества:

#### Scalability:
- **Horizontal Scaling** — распределение нагрузки между узлами
- **Partitioning** — разделение данных для параллельной обработки
- **Load Balancing** — автоматическое распределение работы
- **Fault Tolerance** — устойчивость к отказам компонентов

#### Performance:
- **Streaming Processing** — обработка в реальном времени
- **Memory Optimization** — эффективное использование памяти
- **Parallel Execution** — параллельная обработка данных
- **Caching** — кеширование для улучшения производительности

### Когда использовать агрегацию логов:

✅ **High-Volume Logging** — системы с большим количеством логов
✅ **Real-time Analytics** — необходимость оперативного анализа
✅ **Distributed Systems** — микросервисы и распределенные приложения
✅ **Business Intelligence** — извлечение бизнес-метрик из логов
✅ **Performance Monitoring** — анализ производительности системы
✅ **Security Monitoring** — обнаружение security threats
✅ **Compliance** — аудит и соответствие требованиям
✅ **Capacity Planning** — планирование ресурсов

### Когда НЕ использовать:

❌ **Simple Applications** — для небольших приложений с низким трафиком
❌ **Batch-only Analysis** — если не нужна real-time обработка
❌ **Low Data Volume** — если объем данных не оправдывает сложность
❌ **Human-only Analysis** — если логи анализируются только вручную
❌ **Resource Constraints** — ограниченные вычислительные ресурсы
❌ **Legacy Systems** — сложная интеграция со старыми системами

### Best practices:

1. **Streaming-first Architecture** — приоритизация real-time обработки
2. **Time Window Optimization** — правильный выбор размеров окон
3. **Fault Tolerance** — обработка отказов и recovery
4. **Performance Monitoring** — отслеживание метрик производительности
5. **Data Quality** — валидация и очистка данных
6. **Scalable Storage** — выбор подходящего хранилища
7. **Security** — защита чувствительных данных
8. **Cost Optimization** — оптимизация расходов на инфраструктуру

Агрегация логов является фундаментом для observable и управляемых систем. Правильная реализация позволяет превращать сырые логи в actionable insights, обеспечивая надежность и эффективность работы приложений. 🚀
