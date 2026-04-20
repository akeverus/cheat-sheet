---
title: "InfluxDB: Основы временных рядов"
description: "Комплексное руководство по использованию InfluxDB — высокопроизводительной базы данных временных рядов."
tags:
  - databases
  - time-series
  - influxdb-basics
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# InfluxDB: Основы временных рядов

**Комплексное руководство по использованию `InfluxDB` — высокопроизводительной базы данных временных рядов.**

## Полезные ссылки

### Официальная документация
- [InfluxDB Documentation](https://docs.influxdata.com/influxdb/) — официальная документация **InfluxDB**
- [InfluxDB GitHub](https://github.com/influxdata/influxdb) — репозиторий проекта

### См. также
- [TimescaleDB](../timescaledb/timescaledb-basics.md) — **TimescaleDB**
- [NoSQL](../../nosql/) — **NoSQL** базы данных

- [Hibernate: JPQL, HQL и Criteria API](../../orm/hibernate-jpql-criteria.md)
- [Hibernate: кэширование (L1, L2, Query Cache)](../../orm/hibernate-caching.md)
- [ORM: Object-Relational Mapping](../../orm/orm-basics.md)
- [Hibernate: связи между сущностями](../../orm/hibernate-relationships.md)
## Содержание

- [Введение в InfluxDB](#введение-в-influxdb)
  - [Почему InfluxDB?](#почему-influxdb)
  - [Архитектура InfluxDB](#архитектура-influxdb)
- [Установка и настройка](#установка-и-настройка)
  - [Docker установка](#docker-установка)
- [Основы работы с данными](#основы-работы-с-данными)
  - [Запись данных](#запись-данных)
  - [Чтение данных](#чтение-данных)
- [InfluxQL запросы](#influxql-запросы)
  - [SELECT запросы](#select-запросы)
  - [Агрегация](#агрегация)
- [Интеграция с Java](#интеграция-с-java)
  - [InfluxDB Java Client](#influxdb-java-client)
- [Лучшие практики](#лучшие-практики)

## Введение в InfluxDB

**InfluxDB** — специализированная база данных временных рядов, оптимизированная для хранения и анализа данных с временными метками (метрики, логи, события IoT).

### Почему InfluxDB?

**InfluxDB** предлагает преимущества для временных рядов:

1. **Высокая производительность записи** — Оптимизирована для потоковой записи
2. **Эффективное сжатие** — Автоматическое сжатие данных
3. **Retention policies** — Автоматическое удаление старых данных
4. **Continuous queries** — Автоматическое агрегирование
5. **InfluxQL** — **SQL**-подобный язык запросов

### Архитектура InfluxDB

**InfluxDB** использует:
- **TSM (Time-Structured Merge Tree)** — Структура хранения данных
- **Shards** — Партиции данных по времени
- **Retention Policies** — Политики хранения данных

## Установка и настройка

### Docker установка

```bash
# Запуск InfluxDB в Docker
docker run -d --name influxdb \
  -p 8086:8086 \
  -v influxdb-data:/var/lib/influxdb2 \
  -v influxdb-config:/etc/influxdb2 \
  -e DOCKER_INFLUXDB_INIT_MODE=setup \
  -e DOCKER_INFLUXDB_INIT_USERNAME=admin \
  -e DOCKER_INFLUXDB_INIT_PASSWORD=password123 \
  -e DOCKER_INFLUXDB_INIT_ORG=myorg \
  -e DOCKER_INFLUXDB_INIT_BUCKET=mybucket \
  influxdb:latest
```

## Основы работы с данными

### Запись данных

```java
/*
 * Запись данных в InfluxDB через Java Client
 */
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxDBWriter {

    /*
     * Создание InfluxDB клиента
     */
    public static InfluxDBClient createClient() {
        // Создание клиента для подключения к InfluxDB
        // url - адрес InfluxDB сервера
        // token - токен аутентификации (создается при настройке InfluxDB)
        // org - организация в InfluxDB
        // bucket - bucket (база данных) для записи данных
        return InfluxDBClientFactory.create(
            "http://localhost:8086",           // URL InfluxDB сервера
            "my-token".toCharArray(),          // Токен аутентификации
            "myorg",                           // Название организации
            "mybucket"                         // Название bucket (база данных)
        );
    }

    /*
     * Запись точки данных (measurement)
     */
    public static void writePoint() {
        InfluxDBClient client = createClient();

        try (WriteApi writeApi = client.getWriteApi()) {
            // Создание точки данных
            // Point представляет одно измерение (measurement) с тегами, полями и временной меткой
            Point point = Point.measurement("temperature")  // Название measurement (таблица)
                .addTag("location", "server-room")         // Тег для группировки (индексируется)
                .addTag("sensor", "sensor1")              // Еще один тег
                .addField("value", 23.5)                   // Поле со значением (не индексируется)
                .time(System.currentTimeMillis(), WritePrecision.MS);  // Временная метка

            // Запись точки в InfluxDB
            writeApi.writePoint(point);
            // InfluxDB автоматически создаст measurement если его нет
        }

        client.close();
    }

    /*
     * Запись нескольких точек
     */
    public static void writeMultiplePoints() {
        InfluxDBClient client = createClient();

        try (WriteApi writeApi = client.getWriteApi()) {
            // Запись нескольких точек за раз (batch запись)
            for (int i = 0; i < 10; i++) {
                Point point = Point.measurement("cpu_usage")
                    .addTag("host", "server1")
                    .addTag("cpu", "cpu0")
                    .addField("usage", 50.0 + Math.random() * 50)
                    .time(System.currentTimeMillis() + i * 1000, WritePrecision.MS);

                writeApi.writePoint(point);
            }
            // Все точки будут записаны в одном batch для производительности
        }

        client.close();
    }
}
```

### Чтение данных

```java
/*
 * Чтение данных из InfluxDB
 */
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import com.influxdb.query.FluxRecord;

public class InfluxDBReader {

    /*
     * Чтение данных через Flux запросы
     */
    public static void readData() {
        InfluxDBClient client = InfluxDBWriter.createClient();
        QueryApi queryApi = client.getQueryApi();

        // Flux запрос (язык запросов InfluxDB 2.0+)
        String flux = """
            from(bucket: "mybucket")
              |> range(start: -1h)
              |> filter(fn: (r) => r._measurement == "temperature")
              |> filter(fn: (r) => r.location == "server-room")
        """;

        // Выполнение запроса
        List<FluxTable> tables = queryApi.query(flux);

        // Обработка результатов
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                // Получение значений из записи
                Object value = record.getValue();           // Значение поля
                String measurement = record.getMeasurement();  // Название measurement
                Instant time = record.getTime();           // Временная метка
                String field = record.getField();          // Название поля

                System.out.printf("Time: %s, Measurement: %s, Field: %s, Value: %s%n",
                                time, measurement, field, value);
            }
        }

        client.close();
    }

    /*
     * Чтение данных через InfluxQL (InfluxDB 1.x)
     */
    public static void readWithInfluxQL() {
        // InfluxQL используется в InfluxDB 1.x
        // В InfluxDB 2.0+ рекомендуется использовать Flux
        String query = "SELECT * FROM temperature WHERE time > now() - 1h";
        // Выполнение через соответствующий API
    }
}
```

## InfluxQL запросы

### SELECT запросы

```sql
/*
 * InfluxQL запросы (InfluxDB 1.x)
 * InfluxQL - SQL-подобный язык для InfluxDB
 */

-- Выбор всех данных из measurement
SELECT * FROM temperature;

-- Выбор с условиями по времени
SELECT * FROM temperature WHERE time > now() - 1h;

-- Группировка по тегам
SELECT mean(value) FROM temperature
WHERE time > now() - 1h
GROUP BY location;

-- Агрегация по временным интервалам
SELECT mean(value) FROM temperature
WHERE time > now() - 24h
GROUP BY time(1h);
```

### Агрегация

```sql
/*
 * Агрегатные функции в InfluxQL
 */

-- Среднее значение
SELECT mean(value) FROM temperature;

-- Минимум и максимум
SELECT min(value), max(value) FROM temperature;

-- Сумма
SELECT sum(value) FROM temperature;

-- Количество записей
SELECT count(value) FROM temperature;

-- Статистика
SELECT mean(value), stddev(value) FROM temperature;
```

## Интеграция с Java

### InfluxDB Java Client

```java
/*
 * Полная интеграция InfluxDB с Java приложением
 */
@Configuration
public class InfluxDBConfiguration {

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(
            "http://localhost:8086",
            System.getenv("INFLUXDB_TOKEN").toCharArray(),
            "myorg",
            "mybucket"
        );
    }
}

/
 * Сервис для работы с метриками
 */
@Service
public class MetricsService {

    private final InfluxDBClient influxDBClient;
    private final WriteApi writeApi;

    public MetricsService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
        this.writeApi = influxDBClient.getWriteApi();
    }

    /*
     * Запись метрики CPU
     */
    public void recordCpuUsage(String host, String cpu, double usage) {
        Point point = Point.measurement("cpu_usage")
            .addTag("host", host)
            .addTag("cpu", cpu)
            .addField("usage", usage)
            .time(Instant.now(), WritePrecision.NS);

        writeApi.writePoint(point);
    }

    /*
     * Запись метрики памяти
     */
    public void recordMemoryUsage(String host, double used, double total) {
        Point point = Point.measurement("memory")
            .addTag("host", host)
            .addField("used", used)
            .addField("total", total)
            .addField("usage_percent", (used / total) * 100)
            .time(Instant.now(), WritePrecision.NS);

        writeApi.writePoint(point);
    }

    /*
     * Получение метрик за период
     */
    public List<FluxRecord> getMetrics(String measurement, Duration duration) {
        String flux = String.format("""
            from(bucket: "mybucket")
              |> range(start: -%s)
              |> filter(fn: (r) => r._measurement == "%s")
        """, duration, measurement);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux);

        return tables.stream()
            .flatMap(table -> table.getRecords().stream())
            .collect(Collectors.toList());
    }
}
```

## Лучшие практики

1. **Использование тегов** для индексируемых полей
2. **Пакетная запись** для производительности
3. **Retention policies** для управления данными
4. **Continuous queries** для предварительной агрегации


