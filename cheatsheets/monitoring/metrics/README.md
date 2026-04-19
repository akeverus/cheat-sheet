---
title: "Метрики"
description: "Точка входа в раздел метрик: Prometheus, Grafana, Micrometer, StatsD и связки с алертингом/трейсингом."
tags:
  - meta
  - index
  - monitoring
  - metrics
type: "index"
updated: "2026-04-17"
---
# Метрики

Метрики — один из трёх столпов observability: численные временные ряды о состоянии системы и приложений. Этот раздел покрывает сбор метрик (Prometheus, StatsD), их экспозицию из JVM-приложений (Micrometer), визуализацию (Grafana) и связку с алертингом и трейсингом.

Для кого: backend-инженеры и SRE, которые инструментируют Spring Boot / JVM-приложения, строят дашборды и настраивают SLO. Документы содержат минимальные конфиги, PromQL-примеры и шаблоны Grafana.

## Полезные ссылки

### Основные документы
- [Prometheus](prometheus.md) — pull-модель, PromQL, recording rules, Java client
- [Grafana](grafana.md) — дашборды, алертинг, интеграция со Spring Boot
- [Micrometer](micrometer.md) — vendor-neutral API метрик для JVM
- [StatsD](statsd.md) — лёгкий UDP-демон для push-модели

### Соседние разделы
- [Monitoring](../README.md)
- [Alerting](../alerting/README.md) — алерты на метрики (Alertmanager)
- [Tracing](../tracing/README.md) — корреляция трейсов и метрик
- [Logging](../logging/README.md) — derived metrics из логов
- [APM](../apm/README.md)

### Внешние ресурсы
- [Prometheus](https://prometheus.io/docs/)
- [Grafana Docs](https://grafana.com/docs/)
- [Micrometer](https://micrometer.io/)
- [Google SRE Book: Monitoring](https://sre.google/sre-book/monitoring-distributed-systems/) — Four Golden Signals
- [RED / USE methodology](https://www.brendangregg.com/usemethod.html)

## Содержание

- [Карта инструментов](#карта-инструментов)
- [Когда что использовать](#когда-что-использовать)
- [Связки стека](#связки-стека)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Карта инструментов

```text
┌────────────────────────────────────────────────────────────┐
│                 Metrics pipeline                            │
├──────────────────────┬─────────────────────────────────────┤
│ Инструментация       │ Micrometer, Prometheus Java client   │
├──────────────────────┼─────────────────────────────────────┤
│ Экспозиция           │ /actuator/prometheus, StatsD UDP     │
├──────────────────────┼─────────────────────────────────────┤
│ Сбор / TSDB          │ Prometheus, VictoriaMetrics, Thanos  │
├──────────────────────┼─────────────────────────────────────┤
│ Визуализация         │ Grafana                              │
├──────────────────────┼─────────────────────────────────────┤
│ Алерты               │ Alertmanager / Grafana Alerting      │
└──────────────────────┴─────────────────────────────────────┘
```

## Когда что использовать

| Задача | Документ / инструмент |
|--------|-----------------------|
| Сбор метрик из JVM-приложения | [Micrometer](micrometer.md) + Spring Boot Actuator |
| Хранилище и запросы | [Prometheus](prometheus.md) + PromQL |
| Дашборды | [Grafana](grafana.md) |
| Push-модель (UDP) для коротких задач/legacy | [StatsD](statsd.md) |
| Long-term storage | Thanos / VictoriaMetrics (см. [Prometheus](prometheus.md)) |
| Алерты на метрики | [../alerting/alertmanager.md](../alerting/alertmanager.md) |

## Связки стека

- **Spring Boot + Micrometer + Prometheus** — базовая связка для любого Java-сервиса: Micrometer собирает, Actuator выставляет `/actuator/prometheus`, Prometheus скрейпит.
- **Prometheus + Alertmanager** ([../alerting/alertmanager.md](../alerting/alertmanager.md)) — правила в Prometheus, маршрутизация в Alertmanager.
- **Prometheus + Grafana** — дашборды с PromQL-запросами; Grafana Unified Alerting как альтернатива Alertmanager.
- **OpenTelemetry Metrics** ([../tracing/opentelemetry.md](../tracing/opentelemetry.md)) — альтернатива Micrometer с общим SDK для трейсов/метрик/логов.
- **ELK + Prometheus Exporter** — derived metrics из логов (см. [../logging/log-aggregation/README.md](../logging/log-aggregation/README.md)).
- **Kubernetes** — `kube-prometheus-stack`, node-exporter, cAdvisor ([../infrastructure-monitoring.md](../infrastructure-monitoring.md)).

## Маршруты чтения

- **Минимум для Spring Boot-сервиса:** `Micrometer` -> `Prometheus` -> `Grafana`.
- **Проектирование SLO:** `Prometheus` (recording rules) -> Google SRE Workbook -> `../alerting/README.md`.
- **Legacy push-стек:** `StatsD` -> `Prometheus` (через statsd_exporter).

## Куда идти дальше

- Алерты — [../alerting/README.md](../alerting/README.md)
- Трейсинг — [../tracing/README.md](../tracing/README.md)
- Логирование — [../logging/README.md](../logging/README.md)
- Observability — [../observability-guide.md](../observability-guide.md)
- Инфраструктурный мониторинг — [../infrastructure-monitoring.md](../infrastructure-monitoring.md)
