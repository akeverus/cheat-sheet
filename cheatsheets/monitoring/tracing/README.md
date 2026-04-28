---
title: "Распределённый трейсинг"
description: "Точка входа в раздел tracing: концепции, OpenTelemetry, Jaeger, Zipkin и связка с метриками/логами."
tags:
  - meta
  - index
  - monitoring
  - tracing
type: "index"
aliases:
  - "Распределённый трейсинг"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Распределённый трейсинг

Распределённый трейсинг показывает путь запроса через множество сервисов: какие span-ы выполнялись, сколько заняли, где узкое место, где произошла ошибка. В связке с метриками и логами трейсы замыкают третий столп observability и незаменимы для диагностики производительности в микросервисной архитектуре.

Для кого: backend-инженеры и SRE в распределённых системах, архитекторы микросервисов, разработчики клиентских SDK. Документы дают базовые концепции (trace, span, context propagation), практические примеры инструментации и конфиги Jaeger/Zipkin для Spring Boot.

## Полезные ссылки

### Основные документы
- [Distributed Tracing (концепции)](distributed-tracing.md) — Span, Trace, Context Propagation, W3C Trace Context
- [opentelemetry](opentelemetry.md) — единый vendor-neutral стандарт SDK + collector
- [jaeger](jaeger.md) — CNCF-бэкенд для трейсов, UI, Spring Boot интеграция
- [zipkin](zipkin.md) — альтернативный бэкенд, Sleuth-интеграция

### Соседние разделы
- [Monitoring](../../basics/README.md)
- [Metrics](../../basics/README.md) — Prometheus + Grafana
- [Logging](../../basics/README.md) — корреляция `trace_id` с логами
- [Alerting](../../basics/README.md)
- [APM](../../basics/README.md)

### Внешние ресурсы
- [OpenTelemetry Project](https://opentelemetry.io/)
- [W3C Trace Context](https://www.w3.org/TR/trace-context/)
- [Jaeger](https://www.jaegertracing.io/)
- [Zipkin](https://zipkin.io/)
- [Google Dapper paper](https://research.google/pubs/pub36356/) — исходная идея distributed tracing

## Содержание

- [Карта инструментов](#карта-инструментов)
- [Когда что использовать](#когда-что-использовать)
- [Связки стека](#связки-стека)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта инструментов

```mermaid
flowchart LR
    S["SDK / инструментация<br/>OpenTelemetry SDK, Sleuth, Micrometer Tracing"] --> P["Context propagation<br/>W3C traceparent, B3, Jaeger format"]
    P --> C["Collector<br/>OpenTelemetry Collector, Jaeger agent"]
    C --> T["Storage / UI<br/>Jaeger, Zipkin, Tempo, APM-платформы"]
```

## Когда что использовать

| Задача | Инструмент |
|--------|-----------|
| Vendor-neutral SDK, на долгий срок | [opentelemetry](opentelemetry.md) |
| CNCF OSS backend с UI | [jaeger](jaeger.md) |
| Простой backend, Sleuth + Spring Cloud | [zipkin](zipkin.md) |
| Базовая теория, Span/Trace/Baggage | [Distributed Tracing](distributed-tracing.md) |
| Grafana Tempo (дешёвое хранилище) | связка OTel -> Tempo + [grafana](../metrics/grafana.md) |
| Платный all-in-one APM | [README](../../basics/README.md) (Datadog, New Relic) |

## Связки стека

- **OpenTelemetry SDK + OTel Collector + Jaeger/Zipkin/Tempo** — vendor-neutral путь.
- **Spring Boot + Micrometer Tracing + Zipkin/Jaeger** — стандарт для Spring с 3.x.
- **Prometheus + Jaeger + ELK** — три столпа observability, связка по `trace_id` во всех трёх.
- **Grafana** ([grafana](../metrics/grafana.md)) умеет визуализировать трейсы (Tempo) рядом с метриками и логами (Loki).
- **Kubernetes** — OTel Operator автоматически инструментирует поды через auto-instrumentation.
- **Alertmanager** ([alertmanager](../alerting/alertmanager.md)) работает на метриках, производных от трейсов (например, p99 latency из span-ов).

## Маршруты чтения

- **Концепции за час:** [distributed-tracing](distributed-tracing.md) -> W3C Trace Context (внешняя ссылка).
- **Быстрый старт Spring Boot:** [opentelemetry](opentelemetry.md) -> [jaeger](jaeger.md).
- **Миграция с Sleuth:** [zipkin](zipkin.md) -> Micrometer Tracing -> OpenTelemetry.

## Куда идти дальше

- Метрики — [README](../../basics/README.md)
- Логирование — [README](../../basics/README.md)
- Observability — [observability-guide](../observability-guide.md)
- APM-платформы — [README](../../basics/README.md)
