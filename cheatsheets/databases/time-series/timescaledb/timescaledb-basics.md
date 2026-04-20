---
title: "TimescaleDB: PostgreSQL для временных рядов"
description: "TimescaleDB — расширение PostgreSQL для временных рядов: hypertable, time_bucket, continuous aggregates, compression, retention."
tags:
  - databases
  - time-series
  - timescaledb
  - postgresql
difficulty: "intermediate"
updated: "2026-04-20"
---
# TimescaleDB: PostgreSQL для временных рядов

TimescaleDB — расширение PostgreSQL для хранения и обработки временных рядов. Сохраняет полную совместимость с PostgreSQL и добавляет автоматическое партиционирование, сжатие и функции агрегации.

## Полезные ссылки

### Официальная документация
- [TimescaleDB Documentation](https://docs.timescale.com/) — официальная документация

### См. также
- [[influxdb-basics|InfluxDB]] — time-series БД без SQL
- [[postgres-basics|PostgreSQL]] — основы PostgreSQL
- [[postgres-queries|PostgreSQL: запросы]] — оконные функции, CTE
- [[monitoring-best-practices|Мониторинг]] — использование time-series БД для метрик

## Содержание

- [Запуск в Docker](#запуск-в-docker)
- [Установка расширения](#установка-расширения)
- [Hypertable — автоматическое партиционирование](#hypertable-автоматическое-партиционирование)
- [Вставка данных](#вставка-данных)
- [Запросы временных рядов](#запросы-временных-рядов)
- [Continuous Aggregates](#continuous-aggregates)
- [Сжатие (Compression)](#сжатие-compression)
- [Retention Policy](#retention-policy)
- [Индексы](#индексы)
- [Spring Boot + JPA интеграция](#spring-boot-jpa-интеграция)
- [TimescaleDB vs InfluxDB](#timescaledb-vs-influxdb)
- [Типичные проблемы](#типичные-проблемы)

## Запуск в Docker

```bash
docker run -d --name timescaledb \
  -p 5432:5432 \
  -e POSTGRES_PASSWORD=password \
  timescale/timescaledb:latest-pg16

# Подключение
psql -h localhost -U postgres -d postgres
```

## Установка расширения

```sql
-- Один раз в БД
CREATE EXTENSION IF NOT EXISTS timescaledb;
SELECT timescaledb_pre_restore();   -- при восстановлении из pg_dump
```

## Hypertable — автоматическое партиционирование

TimescaleDB разбивает hypertable на **chunks** по времени (и опционально по пространству).

```sql
-- Создание таблицы с временным рядом
CREATE TABLE sensor_data (
    time        TIMESTAMPTZ NOT NULL,
    sensor_id   INTEGER     NOT NULL,
    location    TEXT,
    temperature DOUBLE PRECISION,
    humidity    DOUBLE PRECISION
);

-- Преобразование в hypertable (partition по time, chunks по 7 дней)
SELECT create_hypertable('sensor_data', 'time',
    chunk_time_interval => INTERVAL '7 days');

-- Смотреть chunks
SELECT * FROM timescaledb_information.chunks
WHERE hypertable_name = 'sensor_data';
```

## Вставка данных

```sql
-- Обычный INSERT — работает точно как в PostgreSQL
INSERT INTO sensor_data (time, sensor_id, location, temperature, humidity)
VALUES
  (NOW(), 1, 'room-1', 22.5, 60.0),
  (NOW() - INTERVAL '1 hour', 1, 'room-1', 21.8, 58.5);

-- Массовая вставка через COPY
COPY sensor_data FROM '/data/sensors.csv' CSV HEADER;
```

## Запросы временных рядов

```sql
-- time_bucket — группировка по временному окну
SELECT
    time_bucket('1 hour', time) AS bucket,
    sensor_id,
    AVG(temperature)  AS avg_temp,
    MAX(temperature)  AS max_temp,
    MIN(temperature)  AS min_temp
FROM sensor_data
WHERE time > NOW() - INTERVAL '24 hours'
GROUP BY bucket, sensor_id
ORDER BY bucket;

-- time_bucket_gapfill — заполнить пустые периоды
SELECT
    time_bucket_gapfill('1 hour', time, NOW() - INTERVAL '24 hours', NOW()) AS bucket,
    sensor_id,
    interpolate(AVG(temperature)) AS temp
FROM sensor_data
WHERE time > NOW() - INTERVAL '24 hours'
GROUP BY bucket, sensor_id
ORDER BY bucket;

-- first/last — первое/последнее значение в окне
SELECT
    sensor_id,
    first(temperature, time) AS first_reading,
    last(temperature, time)  AS last_reading
FROM sensor_data
WHERE time > NOW() - INTERVAL '1 day'
GROUP BY sensor_id;

-- Скользящее среднее за 1 час
SELECT time, sensor_id,
    AVG(temperature) OVER (
        PARTITION BY sensor_id
        ORDER BY time
        RANGE BETWEEN INTERVAL '30 minutes' PRECEDING AND INTERVAL '30 minutes' FOLLOWING
    ) AS rolling_avg
FROM sensor_data
WHERE time > NOW() - INTERVAL '6 hours';
```

## Continuous Aggregates

Материализованные представления с автоматическим обновлением — для быстрых запросов агрегатов.

```sql
-- Создание continuous aggregate (ежечасные средние)
CREATE MATERIALIZED VIEW hourly_sensor_stats
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', time) AS bucket,
    sensor_id,
    AVG(temperature) AS avg_temp,
    MAX(temperature) AS max_temp,
    COUNT(*)         AS reading_count
FROM sensor_data
GROUP BY bucket, sensor_id;

-- Политика автообновления (обновлять раз в 30 мин данные старше 1 часа)
SELECT add_continuous_aggregate_policy('hourly_sensor_stats',
    start_offset => INTERVAL '3 hours',
    end_offset   => INTERVAL '1 hour',
    schedule_interval => INTERVAL '30 minutes');

-- Ручное обновление
CALL refresh_continuous_aggregate('hourly_sensor_stats',
    NOW() - INTERVAL '3 hours', NOW());

-- Запрос к агрегату (быстрее сырой таблицы)
SELECT * FROM hourly_sensor_stats
WHERE bucket > NOW() - INTERVAL '7 days'
ORDER BY bucket;
```

## Сжатие (Compression)

```sql
-- Включить сжатие для hypertable
ALTER TABLE sensor_data SET (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'sensor_id',
    timescaledb.compress_orderby = 'time DESC'
);

-- Политика: сжимать данные старше 7 дней
SELECT add_compression_policy('sensor_data', INTERVAL '7 days');

-- Ручное сжатие конкретного chunk
SELECT compress_chunk(c.schema_name || '.' || c.table_name)
FROM timescaledb_information.chunks c
WHERE c.hypertable_name = 'sensor_data'
  AND c.range_end < NOW() - INTERVAL '7 days'
  AND NOT c.is_compressed;

-- Статистика сжатия
SELECT * FROM timescaledb_information.compressed_chunk_stats
WHERE hypertable_name = 'sensor_data';
```

Типичная степень сжатия: 10–20x для метрик с повторяющимися значениями.

## Retention Policy

```sql
-- Удалять данные старше 30 дней
SELECT add_retention_policy('sensor_data', INTERVAL '30 days');

-- Просмотр политик
SELECT * FROM timescaledb_information.jobs
WHERE proc_name = 'policy_retention';

-- Ручное удаление старых chunks
SELECT drop_chunks('sensor_data', NOW() - INTERVAL '30 days');
```

## Индексы

```sql
-- TimescaleDB создаёт индекс по time автоматически
-- Добавить индекс по sensor_id для фильтрации
CREATE INDEX ON sensor_data (sensor_id, time DESC);

-- Покрывающий индекс
CREATE INDEX ON sensor_data (sensor_id, time DESC) INCLUDE (temperature);
```

## Spring Boot + JPA интеграция

TimescaleDB — это PostgreSQL, поэтому стандартный PostgreSQL JDBC драйвер и Spring Data JPA работают без изменений.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

```java
@Entity
@Table(name = "sensor_data")
public class SensorData {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Instant time;

    @Column(name = "sensor_id")
    private Integer sensorId;

    private Double temperature;
    private Double humidity;
}

// Репозиторий
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {

    @Query(value = """
        SELECT time_bucket('1 hour', time) AS bucket,
               AVG(temperature) AS avg_temp
        FROM sensor_data
        WHERE sensor_id = :sensorId
          AND time > NOW() - INTERVAL '24 hours'
        GROUP BY bucket
        ORDER BY bucket
        """, nativeQuery = true)
    List<Object[]> findHourlyAvg(@Param("sensorId") int sensorId);
}
```

## TimescaleDB vs InfluxDB

| Критерий | TimescaleDB | InfluxDB |
|----------|-------------|----------|
| Основа | PostgreSQL (расширение) | Собственный движок |
| SQL | Полный PostgreSQL SQL | Flux / InfluxQL |
| JOINs | Да (любые PostgreSQL JOIN) | Ограниченно |
| Транзакции | ACID | Нет (eventual) |
| Гибкие схемы | Нет (строгая) | Да (schemaless) |
| Экосистема | PostgreSQL + TimescaleDB | Специализированная |

## Типичные проблемы

| Симптом | Причина | Решение |
|---------|---------|---------|
| Медленная вставка | Запись в сжатый chunk | Данные в сжатые chunks декомпрессируются при вставке; сжимать только старые данные |
| Большой размер БД | Нет retention policy или compression | Добавить `add_retention_policy` и `add_compression_policy` |
| Медленный `time_bucket` | Нет индекса по time | TimescaleDB создаёт автоматически; проверить `\d sensor_data` |
| Continuous aggregate не обновляется | Не добавлена политика | `add_continuous_aggregate_policy` или ручной `CALL refresh_continuous_aggregate` |

