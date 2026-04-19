---
title: "Artillery"
description: "Точка входа в раздел Artillery: нагрузочное тестирование на Node.js со сценариями в YAML."
tags:
  - meta
  - index
  - testing
  - performance-testing
  - artillery
type: "index"
updated: "2026-04-17"
---
# Artillery

Artillery — инструмент нагрузочного тестирования на Node.js. Сценарии описываются в YAML (или в JavaScript для сложной логики), поддерживает фазы нагрузки, HTTP и WebSocket, custom processor-ы, отчёты и Artillery Cloud.

Применяйте, когда команда живёт в экосистеме Node.js и хочет низкий порог входа: YAML-файл версионируется, прогон запускается через npx, а thresholds (`ensure`) падают CI при превышении порогов latency и error rate. Для глубоких протокольных сценариев (JDBC, JMS) или тяжёлой распределённой нагрузки обычно выбирают другой инструмент.

## Полезные ссылки

### Основные документы
- [Artillery](artillery.md) — YAML-сценарии, фазы, processor, CI/CD

### Соседние разделы
- [Performance Testing](../README.md)
- [JMeter](../jmeter/README.md)
- [Gatling](../gatling/README.md)
- [k6](../k6/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)

### Внешние ресурсы
- [Artillery Docs](https://www.artillery.io/docs)
- [Core Concepts](https://www.artillery.io/docs/get-started/core-concepts)
- [Test Script Reference](https://www.artillery.io/docs/reference/test-script)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: сравнение perf-инструментов](#когда-использовать-сравнение-perf-инструментов)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| YAML-сценарии, phases, scenarios | [artillery.md](artillery.md) |
| Processor на JavaScript: токены, кастомные данные | [artillery.md](artillery.md) |
| HTTP / WebSocket | [artillery.md](artillery.md) |
| Отчёты, Artillery Cloud, CI/CD | [artillery.md](artillery.md) |

## Когда использовать: сравнение perf-инструментов

| Инструмент | Язык сценариев | Runtime | Распределённость | Отчёты |
|-----------|----------------|---------|------------------|--------|
| **Artillery** | YAML + JS | Node.js | Artillery Cloud / Fargate | HTML, Cloud |
| JMeter | GUI + XML (.jmx), Groovy | JVM | встроенная (master/slaves) | HTML-report, плагины |
| Gatling | Scala/Java DSL | JVM | Gatling Enterprise | HTML (графики) |
| k6 | JavaScript ES2015+ | Go | k6 Cloud / k6-operator | CLI, Cloud, Grafana |

Artillery — лучший выбор, когда стек уже на Node.js, нужна простая YAML-декларация и нет требований к экстремальной нагрузке с одной машины.

## Маршруты чтения

- **Быстрый старт (30 мин):** введение → установка → первый YAML-сценарий → `ensure`.
- **Production-setup (2 ч):** весь документ + processor + CI/CD + Cloud/отчёты.

## Куда идти дальше

- Обзор нагрузочного тестирования — [../README.md](../README.md)
- Альтернатива на JVM — [../gatling/README.md](../gatling/README.md)
- Альтернатива на Go/JS — [../k6/README.md](../k6/README.md)
