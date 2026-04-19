---
title: "Логирование"
description: "Точка входа в раздел логирования: SLF4J, Logback, Log4j, структурированные логи, ELK, Fluentd, агрегация."
tags:
  - meta
  - index
  - monitoring
  - logging
type: "index"
updated: "2026-04-17"
---
# Логирование

Раздел покрывает весь путь логов в JVM-приложениях: выбор фасада и бэкенда, уровни и MDC, структурированные JSON-логи с correlation-id, централизация через ELK/Fluentd, агрегация потоков логов через Kafka/Flink и лучшие практики производительности и безопасности.

Для кого: backend-инженеры и SRE, которые отвечают за наблюдаемость сервисов. Документы дают готовые конфиги Logback/Log4j, шаблоны JSON-логов, примеры агрегации и рекомендации по ретеншну и маскированию PII.

## Полезные ссылки

### Основные документы
- [Основы логирования](logging-basics.md) — уровни, фреймворки, JUL vs SLF4J vs Logback
- [SLF4J](slf4j.md) — фасад, MDC, markers
- [Logback](logback.md) — конфигурация, appenders, фильтры
- [Log4j](log4j.md) — Apache Log4j 2, async logger, disruptor
- [Структурированное логирование](structured-logging.md) — JSON, correlation-id, ECS
- [Лучшие практики](logging-best-practices.md) — производительность, безопасность, PII
- [Централизованное логирование](centralized-logging.md) — сбор с множества нод
- [Агрегация логов](log-aggregation.md) — stream processing логов
- [ELK Stack](elk-stack.md) — Elasticsearch + Logstash + Kibana

### Подразделы
- [Fluentd](fluentd/README.md) — сборщик логов cross-source (CNCF)
- [Log Aggregation](log-aggregation/README.md) — решения по агрегации и обработке

### Соседние разделы
- [Monitoring](../README.md)
- [Metrics](../metrics/README.md)
- [Tracing](../tracing/README.md) — correlation trace-id <-> логи
- [Alerting](../alerting/README.md)
- [APM](../apm/README.md)

### Внешние ресурсы
- [12 Factor App — Logs](https://12factor.net/logs)
- [Elastic Common Schema (ECS)](https://www.elastic.co/guide/en/ecs/current/index.html)
- [OpenTelemetry Logs](https://opentelemetry.io/docs/specs/otel/logs/)
- [OWASP Logging Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html)

## Содержание

- [Карта инструментов](#карта-инструментов)
- [Когда что использовать](#когда-что-использовать)
- [Связки стека](#связки-стека)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта инструментов

```text
┌────────────────────────────────────────────────────────────┐
│                   Logging pipeline                          │
├──────────────────────┬─────────────────────────────────────┤
│ Facade               │ SLF4J                                │
├──────────────────────┼─────────────────────────────────────┤
│ Backend              │ Logback, Log4j 2, JUL                │
├──────────────────────┼─────────────────────────────────────┤
│ Format               │ JSON / Logstash encoder / ECS        │
├──────────────────────┼─────────────────────────────────────┤
│ Shipper              │ Fluentd, Filebeat, Logstash, Vector  │
├──────────────────────┼─────────────────────────────────────┤
│ Storage / Search     │ Elasticsearch, Loki, CloudWatch      │
├──────────────────────┼─────────────────────────────────────┤
│ UI                   │ Kibana, Grafana Loki                 │
└──────────────────────┴─────────────────────────────────────┘
```

## Когда что использовать

| Задача | Документ |
|--------|----------|
| Старт нового Java-сервиса | [SLF4J](slf4j.md) + [Logback](logback.md) |
| Высоконагруженное логирование (async) | [Log4j](log4j.md) + disruptor |
| JSON-логи с correlation-id | [Структурированное логирование](structured-logging.md) |
| Сбор логов из множества подов | [Fluentd](fluentd/README.md), Filebeat -> ELK |
| Централизация и поиск | [ELK Stack](elk-stack.md), [Централизованное логирование](centralized-logging.md) |
| Stream processing логов | [Агрегация](log-aggregation/README.md) |
| Руководство по безопасности/PII | [Лучшие практики](logging-best-practices.md) |

## Связки стека

- **Prometheus** ([../metrics/prometheus.md](../metrics/prometheus.md)) + **ELK** — метрики и логи в единой картине.
- **Jaeger/OpenTelemetry** ([../tracing/README.md](../tracing/README.md)) — `trace_id` в логах связывает запрос со span-ом.
- **Grafana** ([../metrics/grafana.md](../metrics/grafana.md)) — unified dashboards для метрик и Loki-логов.
- **Alertmanager** ([../alerting/alertmanager.md](../alerting/alertmanager.md)) — алерты по паттернам логов через ElastAlert или Logstash-правила.
- **Kubernetes** — stdout/stderr подов собирает sidecar или node-agent (Fluent Bit, Filebeat).

## Маршруты чтения

- **Минимум для нового сервиса (1 день):** `logging-basics` -> `slf4j` -> `logback` -> `structured-logging`.
- **Production-ready pipeline:** + `centralized-logging` -> `elk-stack` -> `fluentd/` -> `logging-best-practices`.
- **Поиск узких мест в логах:** `log4j` (async) -> `log-aggregation/` -> профайлинг в [../apm/README.md](../apm/README.md).

## Куда идти дальше

- Метрики — [../metrics/README.md](../metrics/README.md)
- Распределённый трейсинг — [../tracing/README.md](../tracing/README.md)
- Алерты на логи — [../alerting/README.md](../alerting/README.md)
- Observability в целом — [../observability-guide.md](../observability-guide.md)
