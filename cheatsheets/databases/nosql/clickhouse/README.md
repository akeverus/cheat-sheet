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
updated: "2026-04-20"
---
# ClickHouse

ClickHouse — колоночная OLAP-СУБД, созданная для аналитических запросов по очень большим объёмам данных (миллиарды строк, терабайты) с низкой латентностью. Сильные стороны: высокая скорость сканирования, векторизованное исполнение, специализированные движки таблиц (MergeTree, ReplacingMergeTree, SummingMergeTree), встроенная репликация и шардирование, материализованные представления для преагрегации.

Для кого: команды, которые строят аналитику продукта, метрики, observability (логи, APM), DWH, real-time дашборды. ClickHouse не заменяет OLTP-СУБД: для точечных транзакционных обновлений берите PostgreSQL, для key-value — Redis, для документов — MongoDB. Для time-series выбор между ClickHouse, TimescaleDB и InfluxDB — см. таблицу ниже.

## Полезные ссылки

### Основные документы
- [[clickhouse-basics|ClickHouse: Основы]] — архитектура, установка, концепции
- [[clickhouse]] — сводный обзор
- [[clickhouse-tables|Таблицы и движки]] — MergeTree семейство
- [[clickhouse-queries|Запросы]] — SQL-диалект ClickHouse
- [[clickhouse-indexes|Индексы]] — primary key, skip indexes
- [[clickhouse-materialized-views|Материализованные представления]] — преагрегация
- [[clickhouse-replication|Репликация]] — ReplicatedMergeTree, ZooKeeper/ClickHouse Keeper
- [[clickhouse-performance|Производительность]] — оптимизация запросов
- [[clickhouse-integration|Интеграция]] — Kafka, JDBC, драйверы

### Соседние разделы
- [[README|NoSQL базы данных]]
- [[README|TimescaleDB]] — альтернатива для time-series
- [[README|InfluxDB]] — специализированный time-series
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
| Архитектура и первые шаги | [[clickhouse-basics]] |
| Движки таблиц MergeTree | [[clickhouse-tables]] |
| SQL-запросы и функции | [[clickhouse-queries]] |
| Первичные и skip-индексы | [[clickhouse-indexes]] |
| Materialized views и преагрегация | [[clickhouse-materialized-views]] |
| Репликация и шардирование | [[clickhouse-replication]] |
| Настройка производительности | [[clickhouse-performance]] |
| Интеграции (Kafka, JDBC) | [[clickhouse-integration]] |

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

- **Быстрый старт (2 часа):** `clickhouse-basics.md` `clickhouse-tables.md` первый INSERT+SELECT в Docker.
- **Разработчик аналитического сервиса:** basics tables queries materialized-views integration.
- **Production-инженер:** replication performance integration (Kafka Engine) monitoring.
- **Оптимизация существующего кластера:** performance indexes materialized-views.

## Куда идти дальше

- Обзор NoSQL — [[README|databases/nosql/README.md]]
- Time-series специализированные — [[README|databases/time-series/README.md]]
- Очереди и стриминг (Kafka integration) — [development/messaging/](../../../development/messaging/)
- Мониторинг и логи — [[README|monitoring/README.md]]
- Интервью по БД — [[database-architecture-interview|interview/databases/]]
