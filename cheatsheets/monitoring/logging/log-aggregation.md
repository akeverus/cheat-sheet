---
title: "Агрегация логов для Java"
description: "Комплексное руководство по агрегации логов в Java: обработка, фильтрация, трансформация и анализ больших объемов логов в реальном времени и batch режиме."
tags: ["monitoring", "logging", "log-aggregation"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Агрегация логов для Java

Комплексное руководство по агрегации логов в Java: обработка, фильтрация, трансформация и анализ больших объемов логов в реальном времени и batch режиме.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Фреймворки агрегации
- [Apache Kafka](https://kafka.apache.org/)
- [Apache Flink](https://flink.apache.org/)
- [Apache Spark](https://spark.apache.org/)
- [Apache Storm](https://storm.apache.org/)

### Java библиотеки
- [Logback Async Appender](https://logback.qos.ch/manual/appenders.html#AsyncAppender)
- [Disruptor](https://lmax-exchange.github.io/disruptor/)
- [RxJava](https://github.com/ReactiveX/RxJava)
- [Reactor](https://projectreactor.io/)

### См. также
- [Основы логирования](logging-basics.md)
- [Централизованное логирование](centralized-logging.md)
- [Структурированное логирование](structured-logging.md)

## Содержание

- [Введение в агрегацию логов](#введение-в-агрегацию-логов)
- [Архитектура агрегации](#архитектура-агрегации)
- [Kafka для логирования](#kafka-для-логирования)
- [Stream processing](#stream-processing)
- [Batch processing](#batch-processing)
- [Real-time и windowing](#real-time-и-windowing)
- [Error aggregation](#error-aggregation)
- [Performance metrics](#performance-metrics)
- [Data enrichment](#data-enrichment)
- [Storage optimization](#storage-optimization)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Введение в агрегацию логов

Агрегация логов — это процесс сбора, обработки и анализа лог-сообщений из различных источников для извлечения информации, выявления паттернов и принятия решений (streaming или batch).

### Почему важна агрегация логов?

1. Pattern Recognition — выявление повторяющихся проблем и аномалий
2. Performance Analysis — анализ производительности систем
3. Business Intelligence — извлечение бизнес-метрик из логов
4. Security Monitoring — обнаружение угроз
5. Root Cause Analysis — быстрая диагностика
6. Real-time Alerting — оповещения о критических событиях

### Типы агрегации

- По времени: фиксированные окна (1 мин, 5 мин, 1 час), скользящие окна, сессионные окна
- По содержимому: ошибки, метрики производительности, бизнес-события
- По структуре: count, sum, average

## Архитектура агрегации

**Streaming:** источники логов → ingestion (Filebeat, Fluent Bit, Logstash) → очередь (Kafka, RabbitMQ, Kinesis) → обработка (Flink, Spark Streaming, Kafka Streams) → хранилище (Elasticsearch, S3, Redshift).

**Batch:** сырые логи (файлы, БД, S3) → MapReduce/Spark/Hive → отчёты, дашборды, хранилище аналитики.

## Kafka для логирования

### Топики и партиционирование

Именование: `{domain}.{entity}.{action}`, например `logs.application.info`, `logs.application.error`, `events.user.login`, `audit.security.login`.

Партиционирование по ключу (correlationId или service) обеспечивает порядок и равномерную нагрузку:

```java
public class LogPartitioner implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        if (key == null)
            return Math.abs(ThreadLocalRandom.current().nextInt()) % cluster.partitionCountForTopic(topic);
        if (key instanceof String)
            return Math.abs(((String) key).hashCode()) % cluster.partitionCountForTopic(topic);
        return 0;
    }
}
```

### Producer (высокий throughput)

```java
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 65536);
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5);
configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864);
        configProps.put(ProducerConfig.ACKS_CONFIG, "1");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
```

Асинхронная отправка: сериализация лога в JSON, выбор топика по level/category, ключ — correlationId, отправка через KafkaTemplate с обработкой ошибок в callback.

```java
    public void shipLogAsync(LogEntry logEntry) {
        CompletableFuture.runAsync(() -> {
            try {
                String json = objectMapper.writeValueAsString(logEntry);
            String topic = "ERROR".equals(logEntry.getLevel()) ? "logs.application.error" : "logs.application.info";
            kafkaTemplate.send(topic, logEntry.getCorrelationId(), json)
                .whenComplete((r, ex) -> { if (ex != null) logger.error("Failed to ship log", ex); });
        } catch (JsonProcessingException e) { logger.error("Serialize error", e); }
    });
}
```

## Stream processing

### Kafka Streams — агрегация ошибок по сервису

```java
        StreamsBuilder builder = new StreamsBuilder();
KStream<String, String> errorLogs = builder.stream("logs.application.error");
        
        KTable<Windowed<String>, Long> errorCounts = errorLogs
            .mapValues(json -> parseServiceName(json))
            .groupBy((key, service) -> service)
            .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
            .count();
        
errorCounts.toStream()
    .map((windowedKey, count) -> KeyValue.pair(windowedKey.key(),
        String.format("{\"service\":\"%s\",\"windowStart\":%d,\"errorCount\":%d}",
            windowedKey.key(), windowedKey.window().start(), count)))
    .to("logs.aggregated.errors");
        
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
```

### Apache Flink (схема)

KafkaSource с WatermarkStrategy forBoundedOutOfOrderness → map(parseLogEntry) → filter(level == ERROR) → keyBy(LogEntry::getService) → window(TumblingEventTimeWindows.of(5 min)) → aggregate(ErrorCountAggregator) → addSink(ElasticsearchSink). В AggregateFunction: createAccumulator (ErrorStats), add (инкремент счётчика, сбор типов ошибок), getResult, merge для слияния при checkpoint.

## Batch processing

### Apache Spark

Чтение JSON из S3/локальных путей, парсинг @timestamp, фильтрация по level/category. Группировка по date + service или operation, agg: count, collect_set(errorType), avg/max/min(duration). Сохранение в Parquet/другой формат для отчётов и дашбордов.

```java
Dataset<Row> logs = spark.read().json("s3://logs-bucket/application-logs/");
logs = logs.withColumn("timestamp", to_timestamp(col("@timestamp"), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        Dataset<Row> errors = logs.filter(col("level").equalTo("ERROR"));
Dataset<Row> errorSummary = errors.groupBy(to_date(col("timestamp")), col("service"))
    .agg(count("*").as("error_count"), collect_set(col("errorType")).as("error_types"));
errorSummary.write().mode("overwrite").parquet("s3://analytics-bucket/error-summary/");

// Метрики производительности по операции
Dataset<Row> perfLogs = logs.filter(col("category").equalTo("performance"));
Dataset<Row> perfSummary = perfLogs.groupBy(to_date(col("timestamp")), col("operation"))
    .agg(avg(col("duration")).as("avg_duration"), max(col("duration")).as("max_duration"), count("*").as("count"));
perfSummary.write().mode("overwrite").parquet("s3://analytics-bucket/performance-summary/");
```

## Real-time и windowing

Tumbling windows — фиксированные неперекрывающиеся интервалы (например 5 минут). Sliding windows — перекрывающиеся (окно 5 мин, шаг 1 мин). Session windows — по активности пользователя с таймаутом неактивности.

Пример: хранить счётчики по сервису и windowStart в Map; по таймеру обрабатывать завершённые окна (алерт при превышении порога ошибок, запись агрегата в хранилище) и удалять старые окна.

### In-memory tumbling (идея)

```java
// windowStart = (timestamp / windowSizeMs) * windowSizeMs
// addError(service, ts): serviceErrorCounts.computeIfAbsent(service, k -> new HashMap<>()).merge(windowStart, 1L, Long::sum)
// По schedule (каждую минуту): для windowStart < now - 10min — проверить порог, записать агрегат, удалить окно из map
```

## Error aggregation

Группировка ошибок по паттерну (например, по верхним кадрам stack trace). Подсчёт частоты по паттерну; алерт при росте частоты за короткий интервал. Error rate monitoring: подсчёт ошибок в скользящем окне по сервису, сравнение с адаптивным порогом (baseline из истории).

### Stack trace clustering (идея)

Из stack trace извлекать ключевые кадры (строки "at ...", исключая java.lang/sun.), объединять в ключ паттерна. Хранить по паттерну: sample stack trace, список вхождений, firstSeen/lastSeen. Алерт при числе вхождений за последние 5 мин выше порога (например ≥3).

## Performance metrics

Response time: накопление значений в окне, расчёт перцентилей (p50, p95, p99). Алерт при деградации (например, p95 > 5× p50 или > 10s). Throughput: подсчёт запросов в единицу времени по endpoint в скользящем окне.

### Перцентили (идея)

По операции хранить список responseTimes (с периодической очисткой или ограничением размера). getP95: sort, index = ceil(0.95 * size) - 1. isDegraded: при достаточном объёме данных (например ≥100) проверять p95 > max(p50 * 5, 10000 ms).

## Data enrichment

Добавление контекста к логам: lookup пользователя (userId → email, role), GeoIP по clientIP, hostname/region сервера. Для бизнес-логов — обогащение по orderId/customerId (статус заказа, тип клиента). Выполнять в фильтре Logstash или в потоковом/батч-процессоре перед записью в хранилище.

### Пример обогащения в коде

```java
    public Map<String, Object> enrichLogEntry(Map<String, Object> logEntry) {
        Map<String, Object> enriched = new HashMap<>(logEntry);
        String userId = (String) logEntry.get("userId");
        if (userId != null) {
        User user = userService.getUserById(userId);
        enriched.put("userEmail", user.getEmail());
        enriched.put("userRole", user.getRole());
    }
    String ip = (String) logEntry.get("clientIP");
    if (ip != null) {
        GeoLocation geo = geoService.lookup(ip);
        enriched.put("country", geo.getCountry());
        enriched.put("city", geo.getCity());
    }
        enriched.put("serverHostname", getServerHostname());
        return enriched;
}
```

## Storage optimization

Elasticsearch: индекс для агрегатов с полями @timestamp, aggregationWindow, service, errorCount, avgResponseTime, p95ResponseTime; refresh_interval 30s, best_compression. Алиасы для удобных запросов. Data lifecycle: удаление старых сырых логов по политике (например 30 дней), архивирование агрегатов старше N дней, forcemerge для оптимизации поиска.

### Индекс для агрегатов (ES)

```bash
curl -X PUT "localhost:9200/logs-aggregated" -H 'Content-Type: application/json' -d '{
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "refresh_interval": "30s",
      "index.codec": "best_compression"
    },
    "mappings": {
      "properties": {
      "@timestamp": { "type": "date" },
      "aggregationWindow": { "type": "date" },
      "service": { "type": "keyword" },
      "errorCount": { "type": "integer" },
      "avgResponseTime": { "type": "double" },
      "p95ResponseTime": { "type": "double" },
      "totalRequests": { "type": "long" },
      "errorRate": { "type": "double" }
    }
  }
}'
# Алиас: POST _aliases — add index logs-aggregated-* -> alias logs-aggregated
```

## Лучшие практики

1. Единый формат логов (JSON) и согласованные имена полей для стриминга и batch.
2. Партиционирование в Kafka по ключу (correlationId/service) для порядка и баланса.
3. Ограничение памяти: bounded buffers, выгрузка на диск при переполнении, периодическая очистка старых данных в окнах.
4. Обработка сбоев: dead letter queue для неудачных записей, circuit breaker при вызовах внешних сервисов, повторная обработка DLQ по расписанию.

### Fault tolerance (схема)

При обработке каждого лога: try processLogEntry(log), при исключении — deadLetterQueue.send(log). Обёртка в CircuitBreaker: при CallNotPermittedException отправлять весь батч в DLQ. По расписанию (например каждые 5 мин): забирать из DLQ батч (до 100 записей), повторять обработку; при ошибке возвращать в очередь или в постоянный failure store.

### Метрики пайплайна

Сбор метрик (Kafka Streams metrics, размеры очередей агрегаторов, throughput по типам) в MeterRegistry; дашборд по commit lag, poll rate, размеру DLQ. Алерт при росте lag или падении throughput ниже порога.

## Решение проблем

| Симптом | Причина | Действие |
|--------|---------|----------|
| Сильный перекос нагрузки по партициям/ключам (data skew) | Один ключ (сервис/пользователь) даёт основной объём | Партиционировать по составному ключу (service + hash(id)), ограничивать размер партиции и сбрасывать при достижении лимита; при batch — репартиционирование или сэмплирование |
| Высокая задержка в стриминговой агрегации | Большие окна, частые коммиты, медленный state store | Уменьшить commit interval, оптимизировать размер окна и grace; проверить cache max bytes buffering и дисковый state store |
| Рост потребления памяти агрегатором | Неограниченные буферы по ключам, долгое хранение в окнах | Ограничить размер буфера на ключ, вытеснять старые записи или сбрасывать на диск; сократить retention окон и очищать завершённые окна |

## Частые вопросы

**Когда выбирать streaming, а когда batch?** Streaming — для алертинга, мониторинга в реальном времени и невысокой латентности. Batch — для тяжёлых отчётов, исторического анализа и ETL в хранилище данных, когда задержка в часах допустима.

**Как избежать потери логов при сбое процессора?** Использовать Kafka с достаточным retention и acknowledgment; потребители с commit только после успешной обработки; dead letter queue для записей, которые не удалось обработать, с последующей повторной обработкой.

**Kafka Streams или Flink для агрегации логов?** Kafka Streams проще интегрировать в Java-приложение и достаточно для многих сценариев (окна, агрегаты, join). Flink даёт более богатую семантику (точная обработка событий, сложные окна, CEP) и масштабирование независимо от Kafka.
