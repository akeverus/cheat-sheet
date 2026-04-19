---
title: "InfluxDB"
description: "Точка входа в InfluxDB: специализированная СУБД для временных рядов — метрики, IoT-датчики, мониторинг, real-time аналитика."
tags:
  - meta
  - index
  - databases
  - time-series
  - influxdb
type: "index"
updated: "2026-04-17"
---
# InfluxDB

InfluxDB — специализированная база данных для временных рядов. Оптимизирована под высокоскоростную запись измерений (метрики, события IoT, трейсы, показатели приложений) и под временно-ориентированные запросы: downsampling, retention policies, агрегации в временных окнах. Использует язык запросов InfluxQL и Flux (в 2.x), в 3.x добавлена поддержка SQL поверх Apache Arrow / DataFusion.

Для кого: DevOps, SRE, IoT-инженеры, разработчики мониторинга. Типичный стек — Telegraf (сбор) → InfluxDB (хранение) → Grafana (визуализация). Если нужны реляционные связи и рядовые бизнес-данные помимо метрик — возьмите TimescaleDB (расширение PostgreSQL). Если нужны тяжёлые OLAP-запросы по миллиардам точек без привязки к «чистому time-series» — ClickHouse.

## Полезные ссылки

### Основные документы
- [InfluxDB: Основы](influxdb-basics.md) — архитектура, установка, запись/чтение, InfluxQL

### Соседние разделы
- [Time-series базы](../README.md)
- [TimescaleDB](../timescaledb/README.md) — time-series поверх PostgreSQL
- [ClickHouse](../../nosql/clickhouse/README.md) — OLAP для огромных time-series
- [Мониторинг](../../../monitoring/)
- [Prometheus / Grafana / мониторинг](../../../monitoring/)

### Внешние ресурсы
- [InfluxDB Documentation](https://docs.influxdata.com/influxdb/)
- [Telegraf](https://docs.influxdata.com/telegraf/) — сбор метрик
- [Flux Language](https://docs.influxdata.com/flux/)
- [InfluxDB GitHub](https://github.com/influxdata/influxdb)

## Содержание

- [Что внутри раздела](#что-внутри-раздела)
- [Когда брать InfluxDB](#когда-брать-influxdb)
- [InfluxDB vs альтернативы](#influxdb-vs-альтернативы)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри раздела

[influxdb-basics.md](influxdb-basics.md) покрывает:

- Архитектуру InfluxDB: measurements, tags, fields, buckets, retention policies
- Установку через Docker
- Запись данных: line protocol, HTTP API, Telegraf
- Чтение: InfluxQL (SELECT, GROUP BY time, aggregations)
- Интеграцию с мониторинг-стэком

## Когда брать InfluxDB

- Основной сценарий — **метрики и события со временной меткой**: CPU/memory, IoT-датчики, бизнес-метрики.
- Нужен **downsampling и retention policies** из коробки.
- Уже используется стек **Telegraf → Grafana**, и хочется минимизировать интеграцию.
- Объём — средний; один нод (open-source) справится.

**Когда не брать:**
- Нужно хранить рядом бизнес-данные и делать JOIN с метриками — TimescaleDB.
- Терабайты time-series + аналитика с окнами и JOIN-ами — ClickHouse.
- Требуется кластерная репликация в open-source — в InfluxDB OSS её нет (только Enterprise/Cloud).
- Prometheus-совместимый pull-based мониторинг — VictoriaMetrics / Prometheus.

## InfluxDB vs альтернативы

| Критерий | InfluxDB | TimescaleDB | ClickHouse | Prometheus |
|----------|----------|-------------|------------|------------|
| Модель | Tag/field, TSM engine | Реляционная (hypertable) | Колоночная OLAP | Multi-dimensional TSDB (pull) |
| Язык запросов | InfluxQL, Flux, SQL (3.x) | PostgreSQL SQL | ClickHouse SQL | PromQL |
| Кластеризация OSS | Нет (только Enterprise) | Multi-node TimescaleDB | Встроенная | Federation, но не шардирование |
| Интеграция с Grafana | Нативная | Через PostgreSQL datasource | Через ClickHouse datasource | Нативная |
| JOIN с реляционными данными | Нет | Да | Ограниченный | Нет |
| Retention / downsampling | Встроены | Continuous aggregates, compression | Materialized views + TTL | Recording rules |
| Лучшее применение | IoT, DevOps метрики, app monitoring | Метрики + бизнес-данные | Огромные time-series + аналитика | Инфраструктурный мониторинг |

## Маршруты чтения

- **Быстрый старт (1 час):** `influxdb-basics.md` → поднять контейнер → отправить line protocol → построить график в Grafana.
- **Мониторинг приложения:** basics → Telegraf конфиг → Spring Boot Micrometer → Influx → Grafana dashboards.
- **Выбор time-series БД:** этот README → раздел сравнения → соседние README (TimescaleDB, ClickHouse).

## Куда идти дальше

- Time-series в целом — [databases/time-series/README.md](../README.md)
- TimescaleDB — [../timescaledb/README.md](../timescaledb/README.md)
- ClickHouse для OLAP — [../../nosql/clickhouse/README.md](../../nosql/clickhouse/README.md)
- Мониторинг и метрики — [monitoring/README.md](../../../monitoring/README.md)
