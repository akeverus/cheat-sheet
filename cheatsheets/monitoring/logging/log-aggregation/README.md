---
title: "Log Aggregation"
description: "Подраздел об агрегации и потоковой обработке логов: Kafka, Flink, Spark Streaming, Disruptor и интеграция с ELK/Grafana."
tags:
  - meta
  - index
  - monitoring
  - logging
  - log-aggregation
type: "index"
updated: "2026-04-20"
---
# Log Aggregation

Агрегация логов — это не просто сбор в хранилище, а обработка потоков: фильтрация, обогащение, парсинг, дедупликация, корреляция событий, построение метрик из логов, детект аномалий. Раздел раскрывает, когда нужна тяжёлая stream-processing поверх обычного ELK/Loki, и какие инструменты JVM-экосистемы для этого используются.

Для кого: инженеры, работающие с большими объёмами логов (сотни тысяч событий в секунду), командами security/fraud, и все, кому мало "просто положить JSON в Elasticsearch". Подраздел служит точкой входа: детальные материалы собраны в соседних файлах каталога logging/.

## Полезные ссылки

### Документы репозитория по теме
- [Агрегация логов](../log-aggregation.md) — основной документ раздела (Kafka, Flink, Spark, Disruptor, RxJava/Reactor)
- [Централизованное логирование](../centralized-logging.md) — доставка логов в центральное хранилище
- [Структурированное логирование](../structured-logging.md) — формат для последующей агрегации
- [ELK Stack](../elk-stack.md) — хранение и поиск после агрегации
- [Fluentd](../../../basics/README.md) — shipper-слой перед агрегатором

### Соседние разделы
- [Logging (корень)](../../../basics/README.md)
- [Monitoring](../../../basics/README.md)
- [Metrics](../../../basics/README.md) — derived metrics из логов
- [Messaging](../../../basics/README.md) — Kafka/RabbitMQ

### Внешние ресурсы
- [Apache Kafka](https://kafka.apache.org/)
- [Apache Flink](https://flink.apache.org/)
- [Apache Spark Streaming](https://spark.apache.org/streaming/)
- [LMAX Disruptor](https://lmax-exchange.github.io/disruptor/)
- [Elastic ECS](https://www.elastic.co/guide/en/ecs/current/index.html)

## Содержание

- [Когда нужна агрегация](#когда-нужна-агрегация)
- [Карта инструментов](#карта-инструментов)
- [Типичные связки стека](#типичные-связки-стека)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Когда нужна агрегация

- Объём логов > возможностей ES-индекса за разумные деньги.
- Требуется real-time обнаружение паттернов (fraud, security events, SLA breach).
- Нужны derived metrics (RPS, error rate) прямо из логов, без дублирующей инструментации.
- Корреляция событий между сервисами (multi-stream join).
- Обогащение логов данными из БД/справочников до записи в хранилище.

## Карта инструментов

```mermaid
flowchart LR
    T["Transport buffer<br/>Kafka, RabbitMQ, Kinesis"] --> S["Stream processor<br/>Flink, Spark Streaming, Kafka Streams"]
    S --> P["In-process pipeline<br/>Disruptor, RxJava, Reactor"]
    P --> K["Sink<br/>Elasticsearch, ClickHouse, S3, Loki"]
```

## Типичные связки стека

- **App -> Fluent Bit -> Kafka -> Flink -> Elasticsearch** — классическая схема с буфером и enrichment.
- **App -> Logback Kafka appender -> Kafka Streams -> Grafana Loki** ([grafana](../../metrics/grafana.md)) — лёгкий стек.
- **App -> Disruptor -> async Logback -> файл** — in-process агрегация для минимизации влияния на hot-path.
- **Kafka -> Spark Streaming -> ClickHouse** — cost-effective аналитика по большим объёмам.
- Алерты по потоку строятся на выходе stream processor либо в [alertmanager](../../alerting/alertmanager.md) на derived-метриках.

## Маршруты чтения

- **Начало:** [log-aggregation](../log-aggregation.md) — основной Java-ориентированный документ.
- **Stream-подход:** Kafka Streams / Flink + [../../messaging/README.md](../../../basics/README.md).
- **Оптимизация in-process:** Disruptor -> [log4j](../log4j.md) async logger.

## Куда идти дальше

- Hot-path логирование — [log4j](../log4j.md), [logback](../logback.md)
- Fluentd-pipeline — [README](../../../basics/README.md)
- ELK — [elk-stack](../elk-stack.md)
- Метрики из логов -> Prometheus — [prometheus](../../metrics/prometheus.md)
