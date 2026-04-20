---
title: "ClickHouse: Интеграции и экосистема - Подключение внешних систем и инструментов"
description: "Комплексное руководство по интеграциям ClickHouse: Kafka, Spark, Airflow, BI инструменты, языки программирования и облачные сервисы"
tags:
  - clickhouse
  - integration
  - kafka
  - spark
  - airflow
  - bi-tools
  - programming-languages
  - cloud
difficulty: "intermediate"
prerequisites: ["databases/clickhouse-performance.md"]
updated: "2026-02-06"
related: ["databases/clickhouse-performance.md", "databases/clickhouse-replication.md"]
---

## Полезные ссылки

### Официальная документация
- [ClickHouse Integrations](https://clickhouse.com/docs/en/integrations/)
- [ClickHouse Documentation](https://clickhouse.com/docs/)

### См. также
- [[clickhouse-performance|clickhouse-performance.md]] — оптимизация и производительность
- [[clickhouse-replication|clickhouse-replication.md]] — кластеры и репликация

## Содержание

- [Введение в интеграции](#введение-в-интеграции)
  - [Категории интеграций](#категории-интеграций)
  - [Подходы к интеграции](#подходы-к-интеграции)
    - [Native интеграции](#native-интеграции)
    - [Third-party инструменты](#third-party-инструменты)
- [Интеграция с Apache Kafka](#интеграция-с-apache-kafka)
  - [Kafka Engine в ClickHouse](#kafka-engine-в-clickhouse)
  - [Расширенная конфигурация Kafka](#расширенная-конфигурация-kafka)
  - [Мониторинг Kafka интеграции](#мониторинг-kafka-интеграции)
- [Интеграция с Apache Spark](#интеграция-с-apache-spark)
  - [Spark-ClickHouse Connector](#spark-clickhouse-connector)
  - [JDBC Connector для Spark](#jdbc-connector-для-spark)
  - [Spark Structured Streaming](#spark-structured-streaming)
- [Интеграция с Apache Airflow](#интеграция-с-apache-airflow)
  - [ClickHouse Hook для Airflow](#clickhouse-hook-для-airflow)
  - [Go](#go)
  - [Node.js](#nodejs)
- [Облачные сервисы](#облачные-сервисы)
  - [AWS](#aws)
  - [Google Cloud Platform](#google-cloud-platform)
  - [Azure](#azure)
- [ETL и потоковая обработка](#etl-и-потоковая-обработка)
  - [Apache NiFi](#apache-nifi)
  - [Apache Flink](#apache-flink)
  - [dbt (Data Build Tool)](#dbt-data-build-tool)
- [dbt profiles.yml](#dbt-profilesyml)
- [Мониторинг и observability](#мониторинг-и-observability)
  - [Prometheus](#prometheus)
- [prometheus.yml](#prometheusyml)
- [Запросы в Grafana](#запросы-в-grafana)
- [ClickHouse queries per second](#clickhouse-queries-per-second)
- [Memory usage](#memory-usage)
- [Disk usage](#disk-usage)
  - [ELK Stack](#elk-stack)
  - [Jaeger/OpenTelemetry](#jaegeropentelemetry)
- [Best Practices](#лучшие-практики)
  - [Проектирование интеграций](#проектирование-интеграций)
  - [Безопасность интеграций](#безопасность-интеграций)
  - [Производительность интеграций](#производительность-интеграций)

## Введение в интеграции

**ClickHouse** имеет богатую экосистему интеграций с различными инструментами и платформами. Это позволяет строить комплексные аналитические системы и **ETL** пайплайны.

### Категории интеграций

1. **Data Ingestion**: **Kafka**, **Spark**, **Flink**
2. **Orchestration**: **Airflow**, **Prefect**, **Dagster**
3. **BI & Analytics**: **Tableau**, **PowerBI**, **Grafana**
4. **Programming**: **Python**, **Java**, Go, **Node.js**
5. **Cloud Platforms**: **AWS**, **GCP**, **Azure**
6. **Monitoring**: **Prometheus**, **Grafana**, **ELK Stack**

### Подходы к интеграции

#### Native интеграции
- Встроенные в **ClickHouse** возможности
- Максимальная производительность
- Минимальная задержка

#### Third-party инструменты
- Внешние коннекторы и драйверы
- Гибкость в выборе технологий
- Дополнительная настройка

## Интеграция с Apache Kafka

**Kafka** — основная система для потоковой передачи данных в реальном времени.

### Kafka Engine в ClickHouse

```sql
-- Создание Kafka таблицы
CREATE TABLE kafka_events (
    timestamp DateTime,
    user_id UInt64,
    event_type String,
    data String
) ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'kafka-1:9092,kafka-2:9092,kafka-3:9092',
    kafka_topic_list = 'user-events',
    kafka_group_name = 'clickhouse-consumer',
    kafka_format = 'JSONEachRow',
    kafka_num_consumers = 3,
    kafka_skip_broken_messages = 1;

-- Создание материализованной таблицы для хранения
CREATE TABLE events (
    timestamp DateTime,
    user_id UInt64,
    event_type String,
    data String,
    processed_at DateTime DEFAULT now()
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(timestamp)
ORDER BY (user_id, timestamp);

-- Материализованное представление для автоматической загрузки
CREATE MATERIALIZED VIEW events_mv TO events
AS SELECT
    timestamp,
    user_id,
    event_type,
    data
FROM kafka_events;
```

### Расширенная конфигурация Kafka

```sql
-- Kafka с авторизацией
CREATE TABLE secure_kafka (
    message String
) ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'kafka-1:9092',
    kafka_topic_list = 'secure-topic',
    kafka_group_name = 'clickhouse-group',
    kafka_format = 'RawBLOB',
    kafka_security_protocol = 'SASL_SSL',
    kafka_sasl_mechanism = 'PLAIN',
    kafka_sasl_username = 'clickhouse-user',
    kafka_sasl_password = 'secure-password';

-- Kafka с кастомным потребителем
CREATE TABLE custom_kafka (
    key String,
    value String,
    headers Map(String, String),
    timestamp DateTime64(3),
    offset UInt64,
    partition UInt32
) ENGINE = Kafka()
SETTINGS
    kafka_broker_list = 'localhost:9092',
    kafka_topic_list = 'custom-topic',
    kafka_group_name = 'custom-group',
    kafka_format = 'JSONEachRow',
    kafka_row_delimiter = '\n',
    kafka_schema = '',
    kafka_thread_per_consumer = 1;
```

### Мониторинг Kafka интеграции

```sql
-- Статистика потребителей Kafka
SELECT
    database,
    table,
    num_consumers,
    last_exception,
    total_reads,
    total_writes
FROM system.kafka_consumers;

-- Логи ошибок Kafka
SELECT
    event_time,
    level,
    message
FROM system.text_log
WHERE message LIKE '%kafka%'
ORDER BY event_time DESC
LIMIT 10;

-- Проверка состояния топиков
SELECT * FROM system.kafka_tables;
```

## Интеграция с Apache Spark

**Spark** — фреймворк для распределенной обработки больших данных.

### Spark-ClickHouse Connector

```java
<!-- Maven dependency -->
<dependency>
    <groupId>com.github.housepower</groupId>
    <artifactId>spark-clickhouse-connector</artifactId>
    <version>0.3.0</version>
</dependency>

@Service
public class SparkClickHouseService {

    // Настройка Spark Session
    public SparkSession createSparkSession() {
        return SparkSession.builder()
            .appName("ClickHouse Integration")
            .config("spark.clickhouse.host", "clickhouse-host")
            .config("spark.clickhouse.port", "8123")
            .config("spark.clickhouse.database", "default")
            .getOrCreate();
    }

    // Чтение из ClickHouse
    public Dataset<Row> readFromClickHouse(SparkSession spark) {
        return spark.read()
            .format("clickhouse")
            .option("url", "clickhouse://clickhouse-host:8123/default")
            .option("query", "SELECT * FROM events WHERE date >= '2024-01-01'")
            .load();
    }

    // Запись в ClickHouse
    public void writeToClickHouse(Dataset<Row> df, SparkSession spark) {
        df.write()
            .format("clickhouse")
            .option("url", "clickhouse://clickhouse-host:8123/default")
            .option("table", "processed_events")
            .mode("append")
            .save();
    }

    // Комплексный пример обработки
    public void processEventsWithSpark() {
        SparkSession spark = createSparkSession();

        try {
            // Чтение данных
            Dataset<Row> eventsDF = readFromClickHouse(spark);

            // Обработка данных
            Dataset<Row> processedDF = eventsDF
                .filter("event_type IS NOT NULL")
                .groupBy("user_id", "event_type")
                .count()
                .orderBy(org.apache.spark.sql.functions.desc("count"));

            // Сохранение результатов
            writeToClickHouse(processedDF, spark);

        } finally {
            spark.close();
        }
    }
}
```

### JDBC Connector для Spark

```xml
<!-- Maven dependency for ClickHouse JDBC -->
<dependency>
    <groupId>com.clickhouse</groupId>
    <artifactId>clickhouse-jdbc</artifactId>
    <version>0.4.6</version>
</dependency>
```

```java
@Service
public class SparkJDBCService {

    // Использование JDBC с Spark
    public Dataset<Row> readFromClickHouseJDBC(SparkSession spark) {
        String jdbcUrl = "jdbc:clickhouse://clickhouse-host:8123/default";

        return spark.read()
            .format("jdbc")
            .option("url", jdbcUrl)
            .option("dbtable", "events")
            .option("user", "default")
            .option("password", "")
            .load();
    }

    // Запись через JDBC
    public void writeToClickHouseJDBC(Dataset<Row> df, SparkSession spark) {
        String jdbcUrl = "jdbc:clickhouse://clickhouse-host:8123/default";

        df.write()
            .format("jdbc")
            .option("url", jdbcUrl)
            .option("dbtable", "processed_data")
            .mode("overwrite")
            .save();
    }

    // Продвинутая конфигурация JDBC
    public Dataset<Row> readWithAdvancedConfig(SparkSession spark) {
        String jdbcUrl = "jdbc:clickhouse://clickhouse-host:8123/default";

        // Дополнительные параметры подключения
        Map<String, String> options = new HashMap<>();
        options.put("url", jdbcUrl);
        options.put("dbtable", "events");
        options.put("user", "default");
        options.put("password", "");
        options.put("driver", "com.clickhouse.jdbc.ClickHouseDriver");
        options.put("fetchSize", "10000"); // Оптимизация производительности
        options.put("partitionColumn", "id");
        options.put("lowerBound", "1");
        options.put("upperBound", "1000000");
        options.put("numPartitions", "10");

        return spark.read()
            .format("jdbc")
            .options(options)
            .load();
    }
}
```

### Spark Structured Streaming

```java
import org.apache.spark.sql.streaming.Trigger;
import static org.apache.spark.sql.functions.*;

@Service
public class SparkStreamingService {

    // Потоковая обработка с ClickHouse
    public StreamingQuery startClickHouseStreaming(SparkSession spark) {
        // Определение схемы для JSON данных
        StructType schema = new StructType()
            .add("user_id", DataTypes.StringType)
            .add("timestamp", DataTypes.TimestampType)
            .add("event_type", DataTypes.StringType)
            .add("value", DataTypes.DoubleType);

        // Чтение из Kafka
        Dataset<Row> streamDF = spark.readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "kafka-host:9092")
            .option("subscribe", "events")
            .load()
            .selectExpr("CAST(value AS STRING) as json")
            .select(from_json(col("json"), schema).as("data"))
            .select("data.*");

        // Агрегация в окнах
        Dataset<Row> aggregatedDF = streamDF
            .groupBy(
                window(col("timestamp"), "1 hour"),
                col("user_id")
            )
            .count();

        // Запись в ClickHouse
        return aggregatedDF.writeStream()
            .format("clickhouse")
            .option("url", "clickhouse://clickhouse-host:8123/default")
            .option("table", "hourly_user_stats")
            .option("checkpointLocation", "/tmp/spark-checkpoint")
            .trigger(Trigger.ProcessingTime("10 seconds"))
            .start();
    }

    // Более сложный пример с несколькими трансформациями
    public StreamingQuery startAdvancedStreaming(SparkSession spark) {
        StructType eventSchema = new StructType()
            .add("user_id", DataTypes.StringType)
            .add("timestamp", DataTypes.TimestampType)
            .add("event_type", DataTypes.StringType)
            .add("value", DataTypes.DoubleType)
            .add("metadata", DataTypes.StringType);

        Dataset<Row> streamDF = spark.readStream()
            .format("kafka")
            .option("kafka.bootstrap.servers", "kafka-host:9092")
            .option("subscribe", "events")
            .load()
            .select(from_json(col("value").cast("string"), eventSchema).as("event"))
            .select("event.*");

        // Множественные агрегации
        Dataset<Row> userStats = streamDF
            .withWatermark("timestamp", "10 minutes")
            .groupBy(
                window(col("timestamp"), "1 hour", "30 minutes"),
                col("user_id"),
                col("event_type")
            )
            .agg(
                count("*").as("event_count"),
                sum("value").as("total_value"),
                avg("value").as("avg_value"),
                min("timestamp").as("first_event"),
                max("timestamp").as("last_event")
            );

        // Запись результатов
        return userStats.writeStream()
            .format("clickhouse")
            .option("url", "clickhouse://clickhouse-host:8123/default")
            .option("table", "user_event_analytics")
            .option("checkpointLocation", "/tmp/spark-analytics-checkpoint")
            .trigger(Trigger.ProcessingTime("30 seconds"))
            .start();
    }
}
```

## Интеграция с Apache Airflow

**Airflow** — платформа для оркестрации и планирования **ETL** задач.

### ClickHouse Hook для Airflow

```java
// ClickHouse Python example replaced with Java Spring
``````java
// `JDBC` драйвер
<dependency>
    <`groupId`>com.clickhouse</groupId>
    <`artifactId`>clickhouse-jdbc</artifactId>
    <version>0.4.6</version>
</dependency>

// Подключение
`String url` = "jdbc:clickhouse://localhost:8123/default";
`Connection conn` = `DriverManager`.`getConnection`(url);

// Выполнение запроса
`Statement stmt` = conn.`createStatement`();
`ResultSet` rs = stmt.`executeQuery`("`SELECT` * `FROM` events `LIMIT 10`");

while (`rs.next`()) {
    `System`.`out.println`(rs.`getString`("`event_type`"));
}

// Пакетная вставка
`PreparedStatement` pstmt = conn.`prepareStatement`(
    "`INSERT INTO` events (`user_id`, timestamp, `event_type`) `VALUES` (?, ?, ?)"
);

for (`Event event` : events) {
    pstmt.`setLong`(1, event.`getUserId`());
    pstmt.`setTimestamp`(2, `Timestamp`.`valueOf`(event.`getTimestamp`()));
    pstmt.`setString`(3, event.`getType`());
    pstmt.`addBatch`();
}

pstmt.`executeBatch`();
```text

### Go

```java
<!-- `Maven dependency for ClickHouse Java client` -->
<dependency>
    <`groupId`>com.clickhouse</groupId>
    <`artifactId`>clickhouse-jdbc</artifactId>
    <version>0.4.6</version>
</dependency>

`@Service`
public class `ClickHouseGoStyleService` {

    `@Autowired`
    private `DataSource dataSource`;

    // Подключение (аналогично Go стилю)
    public void `executeQuery`() throws SQLException {
        try (`Connection conn` = `dataSource`.`getConnection`()) {

            // Выполнение запроса
            `String query` = "`SELECT user_id`, count() `FROM` events `GROUP BY user_id`";
            try (`PreparedStatement` stmt = conn.`prepareStatement`(query);
                 `ResultSet` rs = stmt.`executeQuery`()) {

                while (`rs.next`()) {
                    int `userId` = rs.`getInt`(1);
                    long count = rs.`getLong`(2);
                    `System`.`out.printf`("`User` %d: %d events%n", `userId`, count);
                }
            }
        }
    }

    // Вставка данных с транзакцией
    public void `insertData`() throws SQLException {
        try (`Connection conn` = `dataSource`.`getConnection`()) {
            conn.`setAutoCommit`(`false`);

            try (`PreparedStatement` stmt = conn.`prepareStatement`(
                    "`INSERT INTO` events (`user_id`, timestamp, `event_type`) `VALUES` (?, ?, ?)")) {

                // Вставка одной записи (аналогично Go)
                stmt.`setInt`(1, 1);
                stmt.`setString`(2, "2024-01-01 12:00:00");
                stmt.`setString`(3, "login");
                stmt.`executeUpdate`();

                // Можно добавить больше вставок в цикле
                // for (`Event event` : events) {
                //     stmt.`setInt`(1, event.`getUserId`());
                //     stmt.`setString`(2, event.`getTimestamp`().`toString`());
                //     stmt.`setString`(3, event.`getType`());
                //     stmt.`addBatch`();
                // }
                // stmt.`executeBatch`();

                `conn.commit`();

            } catch (SQLException e) {
                `conn.rollback`();
                throw e;
            }
        }
    }

    // Более полный пример с обработкой ошибок
    public void `processEvents`(`List`<`Event`> events) throws SQLException {
        try (`Connection conn` = `dataSource`.`getConnection`()) {
            conn.`setAutoCommit`(`false`);

            try (`PreparedStatement` stmt = conn.`prepareStatement`(
                    "`INSERT INTO` events (`user_id`, timestamp, `event_type`, value) `VALUES` (?, ?, ?, ?)")) {

                for (`Event event` : events) {
                    stmt.`setLong`(1, event.`getUserId`());
                    stmt.`setTimestamp`(2, `Timestamp`.`valueOf`(event.`getTimestamp`()));
                    stmt.`setString`(3, event.`getType`());
                    stmt.`setDouble`(4, event.`getValue`());
                    stmt.`addBatch`();
                }

                stmt.`executeBatch`();
                `conn.commit`();

            } catch (SQLException e) {
                `conn.rollback`();
                throw new `RuntimeException`("`Failed to insert events`", e);
            }
        }
    }

    // Аналог `Go defer` с `try-with-resources`
    public void `safeQueryExecution`() {
        `String sql` = "`SELECT user_id`, `event_type`, count() `FROM` events `GROUP BY user_id`, `event_type`";

        try (`Connection conn` = `dataSource`.`getConnection`();
             `PreparedStatement` stmt = conn.`prepareStatement`(sql);
             `ResultSet` rs = stmt.`executeQuery`()) {

            while (`rs.next`()) {
                long `userId` = rs.`getLong`("`user_id`");
                `String eventType` = rs.`getString`("`event_type`");
                long count = rs.`getLong`(3); // count() column

                `System`.`out.printf`("`User` %d, `Event` %s: %d occurrences%n",
                    `userId`, `eventType`, count);
            }

        } catch (SQLException e) {
            throw new `RuntimeException`("`Query execution failed`", e);
        }
    }
}
```text

### Node.js

```java
`@Configuration`
public class `ClickHouseConfig` {

    `@Bean`
    public `ClickHouseClient clickHouseClient`() {
        return `ClickHouseClient`.`builder`()
            .`endpoint`("http://localhost:8123")
            .build();
    }
}

`@Service`
public class `ClickHouseService` {

    `@Autowired`
    private `ClickHouseClient clickHouseClient`;

    // Выполнение запроса
    public `List`<Map<`String`, `Object`>> `runQuery`() throws `Exception` {
        `String query` = "`SELECT user_id`, count() as events `FROM` events `GROUP BY user_id`";

        try (`ClickHouseResponse` response = `clickHouseClient`.read(query).execute()) {
            return `response.records`().`stream`()
                .map(record -> {
                    Map<`String`, `Object`> result = new `HashMap`<>();
                    `result.put`("`user_id`", record.`getValue`(0).`asLong`());
                    `result.put`("events", record.`getValue`(1).`asLong`());
                    return result;
                })
                .collect(`Collectors`.`toList`());
        }
    }

    // Вставка данных
    public void `insertData`() throws `Exception` {
        `String insertQuery` = """
            `INSERT INTO` events (`user_id`, timestamp, `event_type`) `VALUES`
            (1, '2024-01-01 12:00:00', 'login'),
            (2, '2024-01-01 12:05:00', 'purchase')
            """;

        `clickHouseClient`.write(`insertQuery`).execute();
    }

    // Комплексный пример с `Spring Data JDBC`
    `@Repository`
    public interface `EventRepository` extends `CrudRepository<Event, Long>` {

        `@Query("SELECT user_id, count() as events FROM events GROUP BY user_id")`
        `List<UserEventCount> getUserEventCounts();`

        `@Query("SELECT user_id, event_type, count() as count FROM events " +
               "WHERE timestamp >= :startDate GROUP BY user_id, event_type")`
        `List<UserEventStats> getUserStatsByDate(@Param("startDate") LocalDateTime startDate);`
    }

    // `DTO` классы
    public static class `UserEventCount` {
        private `Long userId`;
        private `Long events`;
        // getters and setters
    }

    public static class `UserEventStats` {
        private `Long userId`;
        private `String eventType`;
        private `Long count`;
        // getters and setters
    }
}
```text

## Облачные сервисы

### AWS

```sql
-- `ClickHouse` в `AWS`
-- Использование `S3` для бэкапов
`CREATE TABLE events_backup` (
    timestamp `DateTime`,
    data `String`
) ENGINE = S3('my-bucket.s3.amazonaws.com/events.parquet', 'AWS_ACCESS_KEY_ID', 'AWS_SECRET_ACCESS_KEY', 'Parquet');

-- Интеграция с `Kinesis`
`CREATE TABLE kinesis_events` (
    timestamp `DateTime`,
    `user_id UInt64`,
    `event_data String`
) `ENGINE` = `ExternalStream`(
    'kinesis',
    '`my-stream`',
    '`aws_region`=`us-east-1`,`aws_access_key_id`=`KEY`,`aws_secret_access_key`=`SECRET`'
);

-- Использование `AWS Glue Catalog`
`SELECT` * `FROM` awsglue('`my-database`.`my-table`', '`us-east-1`');
```text

### Google Cloud Platform

```sql
-- `BigQuery` federation
`SELECT` *
`FROM` bigquery('`my-project`.`my-dataset`.`my-table`', '`service-account-key`.json');

-- `Google Cloud Storage`
`CREATE TABLE gcs_backup` (
    date `Date`,
    data `String`
) `ENGINE` = `URL`(
    'https://storage.googleapis.com/my-bucket/backup.tsv',
    '`TSV`'
);

-- `Cloud Logging` интеграция
`CREATE TABLE gcp_logs` (
    timestamp `DateTime64`(3),
    severity `String`,
    message `String`,
    resource Map(`String`, `String`)
) `ENGINE` = `ExternalStream`(
    '`gcp_logging`',
    'projects/my-project/my-log',
    'credentials_path/service-account.json'
);
```text

### Azure

```sql
-- `Azure Blob Storage`
`CREATE TABLE azure_backup` (
    id `UInt64`,
    data `String`
) `ENGINE` = `AzureBlobStorage`(
    'https://myaccount.blob.core.windows.net/backup.parquet',
    '`account_name`',
    '`account_key`',
    '`Parquet`'
);

-- `Azure Event Hubs`
`CREATE TABLE eventhubs_events` (
    timestamp `DateTime`,
    `event_type String`,
    payload `String`
) `ENGINE` = `ExternalStream`(
    'eventhubs',
    '`my-event-hub`',
    '`connection_string`=`Endpoint`=sb://...'
);
```text

## ETL и потоковая обработка

### Apache NiFi

```xml
<!-- `NiFi` процессор для `ClickHouse` -->
<processor>
    <id>`clickhouse-processor`</id>
    <name>`PutClickHouse`</name>
    <class>`org.`apache.nifi.processors`.clickhouse`.`PutClickHouse`</class>

    <property name="Database Connection URL">clickhouse-host:8123/default</property>
    <property name="`Table Name`">events</property>
    <property name="`Batch Size`">1000</property>

    <relationships>
        <relationship name="success">success</relationship>
        <relationship name="failure">failure</relationship>
    </relationships>
</processor>
```text

### Apache Flink

```java
// `Flink ClickHouse` sink
`DataStream`<`Event`> events = ...;

events.`addSink`(new `ClickHouseSink`<>(
    "jdbc:clickhouse://`clickhouse-host`:8123/default",
    "`INSERT INTO` events (`user_id`, timestamp, `event_type`) `VALUES` (?, ?, ?)",
    (ps, event) -> {
        ps.`setLong`(1, event.`getUserId`());
        ps.`setTimestamp`(2, `Timestamp`.`valueOf`(event.`getTimestamp`()));
        ps.`setString`(3, event.`getType`());
    }
));
```text

### dbt (Data Build Tool)

```yaml
# dbt `profiles.yml`
clickhouse:
  target: dev
  outputs:
    dev:
      type: clickhouse
      host: localhost
      port: `8123`
      user: default
      password: ""
      database: default
      schema: default
```text

```sql
-- dbt модель
{{ config(
    materialized='table',
    engine='`MergeTree`()',
    `order_by`='`user_id`'
) }}

`SELECT`
    `user_id`,
    count() as `total_events`,
    max(timestamp) as `last_event`,
    sum(amount) as `total_amount`
`FROM` {{ ref('events') }}
`WHERE` timestamp >= '2024-01-01'
`GROUP BY user_id`
```text

## Мониторинг и observability

### Prometheus

```yaml
# `prometheus.yml`
`scrape_configs`:
  - `job_name`: 'clickhouse'
    `static_configs`:
      - targets: ['`clickhouse-host`:9363']  # `ClickHouse` metrics `endpoint`
    `scrape_interval`: 15s

# Запросы в `Grafana`
# `ClickHouse` queries per second
rate(`clickhouse_query_total`[5m])

# `Memory usage`
`clickhouse_memory_tracked_bytes` / `clickhouse_memory_total_bytes` * 100

# `Disk usage`
`clickhouse_disk_free_bytes` / `clickhouse_disk_total_bytes` * 100
```text

### ELK Stack

```json
// `Logstash` конфигурация
input {
  clickhouse {
    host => "`clickhouse-host`"
    port => `8123`
    query => "`SELECT` timestamp, level, message `FROM` system.`text_log WHERE event_time` > :`sql_last_value`"
    schedule => "* * * * *"
    `tracking_column` => "timestamp"
  }
}

output {
  elasticsearch {
    hosts => ["elasticsearch:9200"]
    index => "`clickhouse-logs`-%{+`YYYY`.`MM`.dd}"
  }
}
```text

### Jaeger/OpenTelemetry

```java
// `OpenTelemetry` интеграция
`ClickHouseSpanExporter` exporter = new `ClickHouseSpanExporter`(
    "jdbc:clickhouse://`clickhouse-host`:8123/default"
);

`SpanProcessor` processor = `BatchSpanProcessor`.`builder`(exporter)
    .`setScheduleDelay`(`Duration`.`ofSeconds`(5))
    .build();

`OpenTelemetrySdk`.`builder`()
    .`setTracerProvider`(
        `SdkTracerProvider`.`builder`()
            .`addSpanProcessor`(processor)
            .build()
    )
    .`buildAndRegisterGlobal`();
```text

## Лучшие практики

### Проектирование интеграций

1. Выберите подходящий инструмент
   - Kafka: Для потоковых данных в реальном времени
   - Airflow: Для оркестрации ETL пайплайнов
   - Spark: Для сложной обработки больших данных
   - Grafana: Для визуализации и мониторинга

2. Оптимизируйте конвейеры данных
   ```sql
   — Используйте буферные таблицы
   `CREATE TABLE events_queue` (
       timestamp `DateTime`,
       data `String`
   ) `ENGINE` = `Kafka`('kafka:9092', '`events-topic`', 'JSONEachRow');

   `CREATE TABLE events_processed` (
       timestamp `DateTime`,
       `user_id UInt64`,
       `event_type String`
   ) `ENGINE` = `MergeTree`()
   `ORDER BY` (`user_id`, timestamp);

   `CREATE MATERIALIZED VIEW events_mv TO events_processed`
   `AS SELECT`
       timestamp,
       JSONExtractUInt(data, '`user_id`') as `user_id`,
       JSONExtractString(data, '`event_type`') as `event_type`
   `FROM events_queue`;
   ```text

3. Обеспечьте отказоустойчивость
   - Используйте репликацию ClickHouse
   - Настройте retry логику в приложениях
   - Мониторьте состояние конвейеров

### Безопасность интеграций

1. Аутентификация и авторизация
   ```sql
   — Создание пользователей для интеграций
   `CREATE USER etl_user IDENTIFIED BY` '`secure_password`';
   `GRANT SELECT`, `INSERT ON` events `TO etl_user`;

   `CREATE USER bi_user IDENTIFIED BY` '`bi_password`';
   `GRANT SELECT ON` events, `user_metrics TO bi_user`;
   ```text

2. Шифрование данных
   ```xml
   <!-- `TLS` для внешних подключений -->
   <https_port>8123</https_port>
   <certificateFile>server.crt</certificateFile>
   <privateKeyFile>server.key</privateKeyFile>
   ```text

3. Ограничения ресурсов
   ```sql
   — Ограничения для пользователей
   `CREATE USER api_user IDENTIFIED BY` 'password'
   `SETTINGS`
       `max_memory_usage` = `100000000`,  — 100MB
       `max_execution_time` = 30,        — 30 секунд
       `max_threads` = 2;               — 2 потока
   ```text

### Производительность интеграций

1. Batch операции
   ```java
// `ClickHouse Python example replaced with Java Spring`
```text

```
