---
title: "TimescaleDB"
description: "Точка входа в TimescaleDB: расширение PostgreSQL, превращающее его в мощную time-series СУБД без потери SQL и реляционных связей."
tags:
  - meta
  - index
  - databases
  - time-series
  - timescaledb
type: "index"
updated: "2026-04-17"
---
# TimescaleDB

TimescaleDB — расширение PostgreSQL, которое добавляет специализированные структуры для работы с временными рядами (hypertables с автоматическим партиционированием по времени, continuous aggregates, компрессия, retention policies), сохраняя при этом полный SQL, транзакции, JOIN-ы, индексы и всю экосистему PostgreSQL. Это идеальный выбор, когда time-series живут бок о бок с бизнес-данными.

Для кого: команды, которые уже используют PostgreSQL и не хотят заводить отдельный стек под метрики. Типичная ниша: продуктовая аналитика, IoT-платформы с реляционной моделью устройств, финансовые тики, observability с богатыми реляционными связями. Если метрики — это **только** devops-телеметрия без реляционного контекста, проще взять InfluxDB + Grafana. Если объёмы — миллиарды строк и нужна OLAP-скорость — ClickHouse.

## Полезные ссылки

### Основные документы
- [[timescaledb-basics|TimescaleDB: Основы]] — hypertables, SQL для time-series, интеграция

### Соседние разделы
- [[README|Time-series базы]]
- [[README|InfluxDB]]
- [[README|ClickHouse]]
- [PostgreSQL](../../relational/postgresql/)
- [SQL-справочники](../../sql/)

### Внешние ресурсы
- [TimescaleDB Documentation](https://docs.timescale.com/)
- [TimescaleDB GitHub](https://github.com/timescale/timescaledb)
- [Timescale Blog](https://www.timescale.com/blog/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## Содержание

- [Что внутри раздела](#что-внутри-раздела)
- [Когда брать TimescaleDB](#когда-брать-timescaledb)
- [TimescaleDB vs альтернативы](#timescaledb-vs-альтернативы)
- [Что стоит покрыть дополнительно](#что-стоит-покрыть-дополнительно)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри раздела

[[timescaledb-basics]] покрывает:

- Что такое расширение TimescaleDB и как оно живёт поверх PostgreSQL
- Установку через Docker
- Создание hypertable из обычной таблицы
- Запросы временных рядов: `time_bucket()`, агрегации, join с обычными таблицами
- Базовую интеграцию с Java (через стандартный PostgreSQL JDBC)

## Когда брать TimescaleDB

- Уже есть **PostgreSQL в стеке** — зачем заводить ещё одну БД?
- Time-series **связаны с бизнес-данными** (устройства, клиенты, заказы), нужны JOIN-ы.
- Нужен **полный SQL** и транзакции поверх time-series.
- Хочется **continuous aggregates** для преагрегации и compression для снижения затрат.
- Команда **знает PostgreSQL** — нулевой порог входа.

**Когда не брать:**
- Pure-telemetry без реляционного контекста — InfluxDB проще эксплуатировать.
- Десятки миллиардов строк с тяжёлой OLAP-нагрузкой — ClickHouse.
- Pull-based метрики для мониторинга инфраструктуры — Prometheus / VictoriaMetrics.

## TimescaleDB vs альтернативы

| Критерий | TimescaleDB | InfluxDB | ClickHouse | Обычный PostgreSQL |
|----------|-------------|----------|------------|-------------------|
| Основа | PostgreSQL + extension | Specialized TSDB | Columnar OLAP | Реляционная OLTP |
| Язык запросов | 100% PostgreSQL SQL | InfluxQL, Flux, SQL (3.x) | ClickHouse SQL | SQL |
| JOIN с реляционными данными | Полный | Нет | Ограниченный | Полный |
| Компрессия time-series | Да (native compression) | Да (TSM) | Очень высокая | Низкая |
| Continuous aggregates | Да | Continuous queries | Materialized views | Ручной cron |
| Кластеризация OSS | Multi-node TimescaleDB | Только Enterprise/Cloud | Встроенная | Требует расширений |
| Лучшее применение | TS + бизнес-данные в PG | Pure DevOps / IoT метрики | DWH / огромные TS | Универсальный OLTP |

## Что стоит покрыть дополнительно

Базовый файл короткий. Разумные расширения раздела:

- `timescaledb-hypertables.md` — chunks, partitioning, tablespaces, multi-dimensional.
- `timescaledb-continuous-aggregates.md` — CAGGs, real-time aggregation, refresh policies.
- `timescaledb-compression.md` — политики, segmentby/orderby, замеры экономии.
- `timescaledb-retention.md` — drop_chunks, retention policies, data tiering.
- `timescaledb-performance.md` — индексы, параллельное выполнение, chunk size tuning.

## Маршруты чтения

- **Быстрый старт (1 час):** `timescaledb-basics.md` → запустить в Docker → создать hypertable → INSERT + `time_bucket()`.
- **Миграция с InfluxDB:** basics → соответствие понятий (measurement → table, tag → column) → оценка объёмов.
- **Проектирование схемы:** таблицы бизнес-данных + hypertable для событий → индексы → continuous aggregates.

## Куда идти дальше

- Time-series в целом — [[README|databases/time-series/README.md]]
- PostgreSQL как основа — [databases/relational/postgresql/](../../relational/postgresql/)
- InfluxDB как альтернатива — [[README]]
- ClickHouse для гипер-объёмов — [[README]]
- Мониторинг приложений — [[README|monitoring/README.md]]
