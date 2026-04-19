---
title: "ClickHouse"
description: "Точка входа в ClickHouse: колоночная OLAP-СУБД для аналитических запросов по миллиардам строк с задержками в миллисекунды."
tags:
  - meta
  - index
  - databases
  - clickhouse
  - olap
type: "index"
updated: "2026-04-17"
---
# ClickHouse

ClickHouse — колоночная OLAP-СУБД, созданная для аналитических запросов по очень большим объёмам данных (миллиарды строк, терабайты) с низкой латентностью. Сильные стороны: высокая скорость сканирования, векторизованное исполнение, специализированные движки таблиц (MergeTree, ReplacingMergeTree, SummingMergeTree), встроенная репликация и шардирование, материализованные представления для преагрегации.

Для кого: команды, которые строят аналитику продукта, метрики, observability (логи, APM), DWH, real-time дашборды. ClickHouse не заменяет OLTP-СУБД: для точечных транзакционных обновлений берите PostgreSQL, для key-value — Redis, для документов — MongoDB. Для time-series выбор между ClickHouse, TimescaleDB и InfluxDB — см. таблицу ниже.

## Полезные ссылки

### Основные документы
- [ClickHouse: Основы](clickhouse-basics.md) — архитектура, установка, концепции
- [ClickHouse](clickhouse.md) — сводный обзор
- [Таблицы и движки](clickhouse-tables.md) — MergeTree семейство
- [Запросы](clickhouse-queries.md) — SQL-диалект ClickHouse
- [Индексы](clickhouse-indexes.md) — primary key, skip indexes
- [Материализованные представления](clickhouse-materialized-views.md) — преагрегация
- [Репликация](clickhouse-replication.md) — ReplicatedMergeTree, ZooKeeper/ClickHouse Keeper
- [Производительность](clickhouse-performance.md) — оптимизация запросов
- [Интеграция](clickhouse-integration.md) — Kafka, JDBC, драйверы

### Соседние разделы
- [NoSQL базы данных](../README.md)
- [TimescaleDB](../../time-series/timescaledb/README.md) — альтернатива для time-series
- [InfluxDB](../../time-series/influxdb/README.md) — специализированный time-series
- [PostgreSQL](../../relational/postgresql/)
- [Kafka](../../../development/messaging/)

### Внешние ресурсы
- [ClickHouse Documentation](https://clickhouse.com/docs)
- [ClickHouse GitHub](https://github.com/ClickHouse/ClickHouse)
- [Altinity Knowledge Base](https://kb.altinity.com/)
- [Baeldung: Introduction to ClickHouse](https://www.baeldung.com/clickhouse)

## Содержание

- [Карта тем](#карта-тем)
- [Когда брать ClickHouse](#когда-брать-clickhouse)
- [ClickHouse vs альтернативы](#clickhouse-vs-альтернативы)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта тем

| Тема | Файл |
|------|------|
| Архитектура и первые шаги | [clickhouse-basics.md](clickhouse-basics.md) |
| Движки таблиц MergeTree | [clickhouse-tables.md](clickhouse-tables.md) |
| SQL-запросы и функции | [clickhouse-queries.md](clickhouse-queries.md) |
| Первичные и skip-индексы | [clickhouse-indexes.md](clickhouse-indexes.md) |
| Materialized views и преагрегация | [clickhouse-materialized-views.md](clickhouse-materialized-views.md) |
| Репликация и шардирование | [clickhouse-replication.md](clickhouse-replication.md) |
| Настройка производительности | [clickhouse-performance.md](clickhouse-performance.md) |
| Интеграции (Kafka, JDBC) | [clickhouse-integration.md](clickhouse-integration.md) |

## Когда брать ClickHouse

- **Аналитика** с агрегациями по миллионам/миллиардам строк (SUM, COUNT, GROUP BY, window functions).
- **Append-only** нагрузка: логи, события, метрики, clickstream, финансовые тики.
- Нужна **низкая латентность** (секунды и меньше) на больших объёмах.
- Данные хорошо **сжимаются** (колоночная модель даёт 5-10x компрессию).

**Когда не брать:**
- Транзакционные обновления отдельных строк (UPDATE/DELETE точечные) — возьмите PostgreSQL.
- Нужен ACID и внешние ключи — реляционная БД.
- Малый объём данных (<10 GB) — PostgreSQL справится и без ClickHouse.
- Горячее key-value кэширование — Redis.

## ClickHouse vs альтернативы

| Критерий | ClickHouse | TimescaleDB | InfluxDB | PostgreSQL |
|----------|------------|-------------|----------|------------|
| Модель | Колоночная OLAP | Реляционная + hypertable | Specialized TSDB | Строковая OLTP |
| Язык | ClickHouse SQL | PostgreSQL SQL | InfluxQL, Flux, SQL (3.x) | SQL |
| Сжатие | Очень высокое (5-10x) | Высокое | Высокое | Низкое |
| Транзакции (ACID) | Слабые | Полные (через PG) | Нет | Полные |
| Лучше всего для | DWH, аналитика, observability | Метрики + реляционные данные рядом | Мониторинг, IoT метрики | OLTP, общая нагрузка |
| Кластеризация | Встроенная | Multi-node TimescaleDB | Enterprise / Cloud | Требует расширений |
| Экосистема SQL | Почти стандартный | 100% PostgreSQL | Частичная | Полный |

## Маршруты чтения

- **Быстрый старт (2 часа):** `clickhouse-basics.md` → `clickhouse-tables.md` → первый INSERT+SELECT в Docker.
- **Разработчик аналитического сервиса:** basics → tables → queries → materialized-views → integration.
- **Production-инженер:** replication → performance → integration (Kafka Engine) → monitoring.
- **Оптимизация существующего кластера:** performance → indexes → materialized-views.

## Куда идти дальше

- Обзор NoSQL — [databases/nosql/README.md](../README.md)
- Time-series специализированные — [databases/time-series/README.md](../../time-series/README.md)
- Очереди и стриминг (Kafka integration) — [development/messaging/](../../../development/messaging/)
- Мониторинг и логи — [monitoring/README.md](../../../monitoring/README.md)
- Интервью по БД — [interview/databases/](../../../interview/databases/database-architecture-interview.md)
