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
- [[logging-basics|Основы логирования]] — уровни, фреймворки, JUL vs SLF4J vs Logback
- [[slf4j]] — фасад, MDC, markers
- [[logback]] — конфигурация, appenders, фильтры
- [[log4j]] — Apache Log4j 2, async logger, disruptor
- [[structured-logging|Структурированное логирование]] — JSON, correlation-id, ECS
- [[logging-best-practices|Лучшие практики]] — производительность, безопасность, PII
- [[centralized-logging|Централизованное логирование]] — сбор с множества нод
- [[log-aggregation|Агрегация логов]] — stream processing логов
- [[elk-stack|ELK Stack]] — Elasticsearch + Logstash + Kibana

### Подразделы
- [[README|Fluentd]] — сборщик логов cross-source (CNCF)
- [[README|Log Aggregation]] — решения по агрегации и обработке

### Соседние разделы
- [[README|Monitoring]]
- [[README|Metrics]]
- [[README|Tracing]] — correlation trace-id <-> логи
- [[README|Alerting]]
- [[README|APM]]

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
| Старт нового Java-сервиса | [[slf4j]] + [[logback]] |
| Высоконагруженное логирование (async) | [[log4j]] + disruptor |
| JSON-логи с correlation-id | [[structured-logging|Структурированное логирование]] |
| Сбор логов из множества подов | [[README|Fluentd]], Filebeat -> ELK |
| Централизация и поиск | [[elk-stack|ELK Stack]], [[centralized-logging|Централизованное логирование]] |
| Stream processing логов | [[README|Агрегация]] |
| Руководство по безопасности/PII | [[logging-best-practices|Лучшие практики]] |

## Связки стека

- **Prometheus** ([[prometheus]]) + **ELK** — метрики и логи в единой картине.
- **Jaeger/OpenTelemetry** ([[README]]) — `trace_id` в логах связывает запрос со span-ом.
- **Grafana** ([[grafana]]) — unified dashboards для метрик и Loki-логов.
- **Alertmanager** ([[alertmanager]]) — алерты по паттернам логов через ElastAlert или Logstash-правила.
- **Kubernetes** — stdout/stderr подов собирает sidecar или node-agent (Fluent Bit, Filebeat).

## Маршруты чтения

- **Минимум для нового сервиса (1 день):** `logging-basics` -> `slf4j` -> `logback` -> `structured-logging`.
- **Production-ready pipeline:** + `centralized-logging` -> `elk-stack` -> `fluentd/` -> `logging-best-practices`.
- **Поиск узких мест в логах:** `log4j` (async) -> `log-aggregation/` -> профайлинг в [[README]].

## Куда идти дальше

- Метрики — [[README]]
- Распределённый трейсинг — [[README]]
- Алерты на логи — [[README]]
- Observability в целом — [[observability-guide]]
