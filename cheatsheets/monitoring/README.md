---
title: "Мониторинг и Observability"
description: "Раздел охватывает инструменты и практики мониторинга, логирования, трейсинга, алертинга и APM."
tags:
  - meta
  - index
type: "index"
updated: "2026-04-20"
---
# Мониторинг и Observability

Раздел охватывает инструменты и практики мониторинга, логирования, трейсинга, алертинга и APM.

## Полезные ссылки

[Logging](logging/)
[Tracing](tracing/)

## Обзорные документы

- [Руководство по Observability](observability-guide.md) — три столпа: метрики, логи, трейсы
- [Лучшие практики мониторинга](monitoring-best-practices.md) — золотые сигналы, алертинг, организация
- [Мониторинг инфраструктуры](infrastructure-monitoring.md) — серверы, контейнеры, сеть (Node Exporter, cAdvisor)

## Метрики (`metrics/`)

- [prometheus](metrics/prometheus.md) — сбор метрик, PromQL, алертинг, Java Client, Micrometer
- [grafana](metrics/grafana.md) — визуализация, дашборды, панели, Spring Boot интеграция
- [micrometer](metrics/micrometer.md) — vendor-neutral API метрик для JVM
- [statsd](metrics/statsd.md) — лёгкий демон для приёма метрик по UDP

## Логирование (`logging/`)

- [Основы логирования](logging/logging-basics.md) — уровни, фреймворки (JUL, SLF4J, Logback)
- [slf4j](logging/slf4j.md) — фасад логирования, MDC, Markers
- [logback](logging/logback.md) — конфигурация, appenders, фильтры
- [log4j](logging/log4j.md) — Apache Log4j 2, async logging
- [Структурированное логирование](logging/structured-logging.md) — JSON-логи, correlation ID
- [Лучшие практики логирования](logging/logging-best-practices.md) — принципы, производительность, безопасность
- [Централизованное логирование](logging/centralized-logging.md) — ELK, Fluentd, CloudWatch
- [Агрегация логов](logging/log-aggregation.md) — обработка, фильтрация, трансформация
- [ELK Stack](logging/elk-stack.md) — Elasticsearch, Logstash, Kibana

## Трейсинг (`tracing/`)

- [Distributed Tracing](tracing/distributed-tracing.md) — концепции, Span, Trace, Context Propagation
- [opentelemetry](tracing/opentelemetry.md) — единый стандарт телеметрии
- [jaeger](tracing/jaeger.md) — распределённый трейсинг для Java
- [zipkin](tracing/zipkin.md) — система трейсинга, интеграция с Spring Boot

## Алертинг (`alerting/`)

- [Alerting (обзор)](alerting/alerting.md) — системы алертинга, каналы уведомлений
- [alertmanager](alerting/alertmanager.md) — маршрутизация, группировка, inhibition
- [pagerduty](alerting/pagerduty.md) — инцидент-менеджмент и on-call
- [Slack Alerting](alerting/slack-alerting.md) — уведомления в Slack

## APM (`apm/`)

- [datadog](apm/datadog.md) — унифицированная платформа мониторинга
- [Elastic APM](apm/elastic-apm.md) — APM на Elastic Stack
- [New Relic](apm/new-relic.md) — мониторинг производительности приложений
