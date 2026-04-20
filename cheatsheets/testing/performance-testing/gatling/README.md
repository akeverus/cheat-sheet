---
title: "Gatling"
description: "Точка входа в раздел Gatling: нагрузочное тестирование на JVM со Scala/Java DSL."
tags:
  - meta
  - index
  - testing
  - performance-testing
  - gatling
type: "index"
updated: "2026-04-17"
---
# Gatling

Gatling — инструмент нагрузочного тестирования на JVM. Тесты (Simulation) пишутся в виде кода на Scala или Java DSL, что даёт типизацию, рефакторинг и переиспользование. Гибкие профили инжекции (atOnceUsers, rampUsers, stressPeakUsers), подробный HTML-отчёт и интеграция с Grafana/InfluxDB и CI/CD.

Применяйте, когда команда уже на JVM и хочет писать сценарии «как код» с контролем версий и ревью, а не через GUI-редактор планов (как в JMeter). Gatling хорошо держит высокую нагрузку с одной машины благодаря асинхронному ядру на Netty.

## Полезные ссылки

### Основные документы
- [[gatling]] — Simulation, инжекция, отчёты, CI/CD

### Соседние разделы
- [[README|Performance Testing]]
- [[README|JMeter]]
- [[README|k6]]
- [[README|Artillery]]
- [[testing-tools-overview|Testing Tools Overview]]

### Внешние ресурсы
- [Gatling Docs](https://gatling.io/docs/gatling/)
- [Quick Start](https://gatling.io/docs/gatling/quickstart/)
- [Cheat Sheet](https://gatling.io/docs/gatling/reference/current/cheat-sheet/)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: сравнение perf-инструментов](#когда-использовать-сравнение-perf-инструментов)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Структура Simulation, Scenario, HTTP | [[gatling]] |
| Инжекция: atOnceUsers, rampUsers, stressPeak | [[gatling]] |
| Параметризация, feeder-ы, checks | [[gatling]] |
| Отчёты (HTML), Grafana/InfluxDB, CI/CD | [[gatling]] |

## Когда использовать: сравнение perf-инструментов

| Инструмент | Язык сценариев | Runtime | Распределённость | Отчёты |
|-----------|----------------|---------|------------------|--------|
| **Gatling** | Scala/Java DSL | JVM (Netty) | Gatling Enterprise | HTML (графики, перцентили) |
| JMeter | GUI + XML (.jmx), Groovy | JVM | master/slaves | HTML-report, плагины |
| k6 | JavaScript | Go | k6 Cloud / operator | CLI, Cloud, Grafana |
| Artillery | YAML + JS | Node.js | Artillery Cloud / Fargate | HTML, Cloud |

Gatling — оптимален для JVM-команд, которые хотят «scenarios as code», высокую нагрузку с одной машины и красивые built-in-отчёты.

## Маршруты чтения

- **Быстрый старт (30 мин):** введение установка первая Simulation HTML-отчёт.
- **Production-setup (2 ч):** весь документ + feeder checks инжекция под реальный профиль CI/CD.

## Куда идти дальше

- Обзор нагрузочного тестирования — [[README]]
- Альтернатива JMeter — [[README]]
- Легковесный k6 — [[README]]
