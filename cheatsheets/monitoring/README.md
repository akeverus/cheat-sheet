---
title: "Мониторинг и Observability"
description: "Раздел охватывает инструменты и практики мониторинга, логирования, трейсинга, алертинга и APM."
tags:
  - meta
  - index
type: "index"
updated: "2026-02-11"
---
# Мониторинг и Observability

Раздел охватывает инструменты и практики мониторинга, логирования, трейсинга, алертинга и APM.

## Полезные ссылки

[Logging](logging/)
[Tracing](tracing/)

## Обзорные документы

- [[observability-guide|Руководство по Observability]] — три столпа: метрики, логи, трейсы
- [[monitoring-best-practices|Лучшие практики мониторинга]] — золотые сигналы, алертинг, организация
- [[infrastructure-monitoring|Мониторинг инфраструктуры]] — серверы, контейнеры, сеть (Node Exporter, cAdvisor)

## Метрики (`metrics/`)

- [[prometheus]] — сбор метрик, PromQL, алертинг, Java Client, Micrometer
- [[grafana]] — визуализация, дашборды, панели, Spring Boot интеграция
- [[micrometer]] — vendor-neutral API метрик для JVM
- [[statsd]] — лёгкий демон для приёма метрик по UDP

## Логирование (`logging/`)

- [[logging-basics|Основы логирования]] — уровни, фреймворки (JUL, SLF4J, Logback)
- [[slf4j]] — фасад логирования, MDC, Markers
- [[logback]] — конфигурация, appenders, фильтры
- [[log4j]] — Apache Log4j 2, async logging
- [[structured-logging|Структурированное логирование]] — JSON-логи, correlation ID
- [[logging-best-practices|Лучшие практики логирования]] — принципы, производительность, безопасность
- [[centralized-logging|Централизованное логирование]] — ELK, Fluentd, CloudWatch
- [[log-aggregation|Агрегация логов]] — обработка, фильтрация, трансформация
- [[elk-stack|ELK Stack]] — Elasticsearch, Logstash, Kibana

## Трейсинг (`tracing/`)

- [[distributed-tracing|Distributed Tracing]] — концепции, Span, Trace, Context Propagation
- [[opentelemetry]] — единый стандарт телеметрии
- [[jaeger]] — распределённый трейсинг для Java
- [[zipkin]] — система трейсинга, интеграция с Spring Boot

## Алертинг (`alerting/`)

- [[alerting|Alerting (обзор)]] — системы алертинга, каналы уведомлений
- [[alertmanager]] — маршрутизация, группировка, inhibition
- [[pagerduty]] — инцидент-менеджмент и on-call
- [[slack-alerting|Slack Alerting]] — уведомления в Slack

## APM (`apm/`)

- [[datadog]] — унифицированная платформа мониторинга
- [[elastic-apm|Elastic APM]] — APM на Elastic Stack
- [[new-relic|New Relic]] — мониторинг производительности приложений
