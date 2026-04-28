---
title: "APM"
description: "Точка входа в раздел Application Performance Monitoring: Datadog, Elastic APM, New Relic и связки с метриками/трейсингом."
tags:
  - meta
  - index
  - monitoring
  - apm
type: "index"
aliases:
  - "APM"
prerequisites: []
next: []
updated: "2026-04-20"
---
# APM

APM (Application Performance Monitoring) — класс инструментов, которые собирают данные о производительности приложений: транзакции, зависимости, ошибки, медленные запросы, JVM-метрики, профили. В отличие от «чистого» Prometheus, APM-платформы обычно объединяют метрики, трейсы, логи и профили в едином UI с готовыми дашбордами.

Для кого: backend-инженеры, которые расследуют деградации производительности, SRE, которые хотят быстрее локализовать узкие места, и архитекторы, выбирающие observability-стек. Раздел показывает сильные стороны каждой платформы и где она стыкуется с Prometheus / OpenTelemetry / Jaeger.

## Полезные ссылки

### Основные документы
- [datadog](datadog.md) — унифицированная SaaS-платформа (APM + infra + logs + RUM)
- [Elastic APM](elastic-apm.md) — APM поверх Elastic Stack (ES + Kibana)
- [New Relic](new-relic.md) — APM, NRQL, Java-агент, distributed tracing

### Соседние разделы
- [Monitoring](../../basics/README.md)
- [Metrics](../../basics/README.md) — Prometheus/Grafana/Micrometer
- [Tracing](../../basics/README.md) — Jaeger, Zipkin, OpenTelemetry
- [Logging](../../basics/README.md)
- [Alerting](../../basics/README.md)

### Внешние ресурсы
- [OpenTelemetry](https://opentelemetry.io/) — единый стандарт телеметрии
- [Datadog APM Docs](https://docs.datadoghq.com/tracing/)
- [Elastic APM Docs](https://www.elastic.co/guide/en/apm/guide/current/index.html)
- [New Relic Docs](https://docs.newrelic.com/)

## Содержание

- [Карта инструментов](#карта-инструментов)
- [Когда какой инструмент](#когда-какой-инструмент)
- [APM vs Prometheus/Jaeger](#apm-vs-prometheusjaeger)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта инструментов

| APM-платформа | Характеристика |
|---|---|
| Datadog | SaaS, широкий инструментарий, собственный агент, интеграция с k8s |
| Elastic APM | self-hosted поверх ES/Kibana, APM-server, OTel-совместимость |
| New Relic | SaaS, NRQL, мощный Java-агент, distributed tracing из коробки |

## Когда какой инструмент

| Задача | Инструмент |
|--------|-----------|
| SaaS-APM с готовыми дашбордами и инфра-мониторингом | [datadog](datadog.md) |
| Хочется остаться в Elastic Stack, корреляция с логами | [Elastic APM](elastic-apm.md) |
| Глубокий Java-агент, distributed tracing, NRQL-запросы | [New Relic](new-relic.md) |
| Полный self-hosted на OSS | Prometheus + [jaeger](../tracing/jaeger.md) + [ELK](../logging/elk-stack.md) |
| Vendor-neutral SDK и инструментация | [opentelemetry](../tracing/opentelemetry.md) |

## APM vs Prometheus/Jaeger

- **Prometheus** ([prometheus](../metrics/prometheus.md)) — pull-модель, time-series, PromQL. Отличен для инфра и JVM-метрик.
- **Jaeger/Zipkin** ([jaeger](../tracing/jaeger.md), [zipkin](../tracing/zipkin.md)) — distributed tracing, корреляция вызовов между сервисами.
- **APM-платформы** объединяют оба потока + ошибки + dependency map + profiling в одном UI, часто за счёт стоимости и vendor lock-in.
- Через [opentelemetry](../tracing/opentelemetry.md) можно писать код один раз и переключать backend (Datadog, New Relic, Elastic APM, Jaeger) без переинструментирования.

## Маршруты чтения

- **Выбор APM для стартапа:** `Datadog` (быстрый старт) vs `Elastic APM` (self-hosted) vs `New Relic`.
- **Мигрируем с ELK на APM:** `Elastic APM` + связка с `../logging/elk-stack.md`.
- **Java-приложение с Spring Boot:** `New Relic` (Java-агент) или `OpenTelemetry` + Jaeger.

## Куда идти дальше

- Метрики — [README](../../basics/README.md)
- Трейсинг — [README](../../basics/README.md)
- Логирование — [README](../../basics/README.md)
- Observability — [observability-guide](../observability-guide.md)
