---
title: "k6"
description: "Точка входа в раздел k6: нагрузочное тестирование на JavaScript поверх Go-рантайма от Grafana Labs."
tags:
  - meta
  - index
  - testing
  - performance-testing
  - k6
type: "index"
updated: "2026-04-17"
---
# k6

Grafana k6 — современный инструмент нагрузочного тестирования. Сценарии пишутся на JavaScript (ES2015+), а исполняются в высокопроизводительном Go-рантайме. Поддерживает виртуальных пользователей (VUs), stages, scenarios, thresholds, HTTP/WebSocket/gRPC и тесную интеграцию с Grafana/InfluxDB/Prometheus.

Применяйте, когда нужна низкая стоимость нагрузочного прогона на одной машине и естественная интеграция с observability-стеком Grafana. Сценарии версионируются как код, thresholds автоматически превращают прогон в pass/fail в CI без сторонних парсеров.

## Полезные ссылки

### Основные документы
- [k6](k6.md) — VUs, stages, thresholds, WebSocket/gRPC, CI/CD

### Соседние разделы
- [Performance Testing](../README.md)
- [JMeter](../jmeter/README.md)
- [Gatling](../gatling/README.md)
- [Artillery](../artillery/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)

### Внешние ресурсы
- [k6 Docs](https://k6.io/docs/)
- [Scenarios](https://k6.io/docs/using-k6/scenarios/)
- [Thresholds](https://k6.io/docs/using-k6/thresholds/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: сравнение perf-инструментов](#когда-использовать-сравнение-perf-инструментов)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Структура теста: VUs, stages, scenarios | [k6.md](k6.md) |
| HTTP, checks, группы, thresholds | [k6.md](k6.md) |
| WebSocket, gRPC | [k6.md](k6.md) |
| Отчёты, Grafana/Prometheus, CI/CD | [k6.md](k6.md) |

## Когда использовать: сравнение perf-инструментов

| Инструмент | Язык сценариев | Runtime | Распределённость | Отчёты |
|-----------|----------------|---------|------------------|--------|
| **k6** | JavaScript (ES2015+) | Go | k6 Cloud, k6-operator (K8s) | CLI, Cloud, Grafana/Prom |
| JMeter | GUI + XML, Groovy | JVM | master/slaves | HTML-report, плагины |
| Gatling | Scala/Java DSL | JVM | Gatling Enterprise | HTML (графики) |
| Artillery | YAML + JS | Node.js | Artillery Cloud | HTML, Cloud |

k6 — оптимален, когда важны скорость разработки на JS, нативная интеграция с Grafana и лёгкий CI-runner.

## Маршруты чтения

- **Быстрый старт (30 мин):** введение → установка → первый `default` экспорт → thresholds.
- **Production-setup (2 ч):** весь документ + scenarios → Grafana/Prometheus → CI/CD → k6-operator.

## Куда идти дальше

- Обзор нагрузочного тестирования — [../README.md](../README.md)
- JVM-альтернатива — [../gatling/README.md](../gatling/README.md)
- Node.js-альтернатива — [../artillery/README.md](../artillery/README.md)
