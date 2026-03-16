---
title: "Мониторинг и Observability"
description: "Раздел охватывает инструменты и практики мониторинга, логирования, трейсинга, алертинга и APM."
tags: ["meta", "index"]
type: "index"
updated: "2026-02-11"
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

- [Prometheus](metrics/prometheus.md) — сбор метрик, PromQL, алертинг, Java Client, Micrometer
- [Grafana](metrics/grafana.md) — визуализация, дашборды, панели, Spring Boot интеграция
- [Micrometer](metrics/micrometer.md) — vendor-neutral API метрик для JVM
- [StatsD](metrics/statsd.md) — лёгкий демон для приёма метрик по UDP

## Логирование (`logging/`)

- [Основы логирования](logging/logging-basics.md) — уровни, фреймворки (JUL, SLF4J, Logback)
- [SLF4J](logging/slf4j.md) — фасад логирования, MDC, Markers
- [Logback](logging/logback.md) — конфигурация, appenders, фильтры
- [Log4j](logging/log4j.md) — Apache Log4j 2, async logging
- [Структурированное логирование](logging/structured-logging.md) — JSON-логи, correlation ID
- [Лучшие практики логирования](logging/logging-best-practices.md) — принципы, производительность, безопасность
- [Централизованное логирование](logging/centralized-logging.md) — ELK, Fluentd, CloudWatch
- [Агрегация логов](logging/log-aggregation.md) — обработка, фильтрация, трансформация
- [ELK Stack](logging/elk-stack.md) — Elasticsearch, Logstash, Kibana

## Трейсинг (`tracing/`)

- [Distributed Tracing](tracing/distributed-tracing.md) — концепции, Span, Trace, Context Propagation
- [OpenTelemetry](tracing/opentelemetry.md) — единый стандарт телеметрии
- [Jaeger](tracing/jaeger.md) — распределённый трейсинг для Java
- [Zipkin](tracing/zipkin.md) — система трейсинга, интеграция с Spring Boot

## Алертинг (`alerting/`)

- [Alerting (обзор)](alerting/alerting.md) — системы алертинга, каналы уведомлений
- [Alertmanager](alerting/alertmanager.md) — маршрутизация, группировка, inhibition
- [PagerDuty](alerting/pagerduty.md) — инцидент-менеджмент и on-call
- [Slack Alerting](alerting/slack-alerting.md) — уведомления в Slack

## APM (`apm/`)

- [Datadog](apm/datadog.md) — унифицированная платформа мониторинга
- [Elastic APM](apm/elastic-apm.md) — APM на Elastic Stack
- [New Relic](apm/new-relic.md) — мониторинг производительности приложений
