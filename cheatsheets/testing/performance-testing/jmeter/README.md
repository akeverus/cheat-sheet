---
title: "Apache JMeter"
description: "Точка входа в раздел JMeter: GUI-планы тестов, CLI-запуск, HTTP/JDBC/JMS нагрузочное и функциональное тестирование."
tags:
  - meta
  - index
  - testing
  - performance-testing
  - jmeter
type: "index"
updated: "2026-04-17"
---
# Apache JMeter

Apache JMeter — зрелый инструмент нагрузочного, стрессового и функционального тестирования. Поддерживает HTTP/HTTPS, JDBC, JMS, SOAP, LDAP, TCP. План теста (Test Plan) строится в GUI как дерево Thread Group → Samplers → Listeners → Assertions, а прогоняется в non-GUI (CLI) для минимального overhead.

Применяйте, когда нужен максимально широкий набор протоколов (не только HTTP), богатая экосистема плагинов и готовый распределённый запуск (master/slaves). JMeter остаётся стандартом де-факто в enterprise-окружениях и QA-отделах, где GUI-конструктор ускоряет сборку сложных планов.

## Полезные ссылки

### Основные документы
- [JMeter](jmeter.md) — Thread Group, Samplers, Listeners, CLI, отчёты

### Соседние разделы
- [Performance Testing](../README.md)
- [Gatling](../gatling/README.md)
- [k6](../k6/README.md)
- [Artillery](../artillery/README.md)
- [Testing Tools Overview](../../testing-tools/testing-tools-overview.md)

### Внешние ресурсы
- [Apache JMeter](https://jmeter.apache.org/)
- [User's Manual](https://jmeter.apache.org/usermanual/index.html)
- [Best Practices](https://jmeter.apache.org/usermanual/best-practices.html)

## Содержание

- [Что внутри](#что-внутри)
- [Когда использовать: сравнение perf-инструментов](#когда-использовать-сравнение-perf-инструментов)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что внутри

| Тема | Где читать |
|------|-----------|
| Thread Group, Samplers, Listeners, Assertions | [jmeter.md](jmeter.md) |
| GUI для разработки плана, CLI для прогона | [jmeter.md](jmeter.md) |
| HTML-report, плагины, JSR223 (Groovy) | [jmeter.md](jmeter.md) |
| Распределённый запуск master/slaves, CI/CD | [jmeter.md](jmeter.md) |

## Когда использовать: сравнение perf-инструментов

| Инструмент | Язык сценариев | Runtime | Распределённость | Отчёты |
|-----------|----------------|---------|------------------|--------|
| **JMeter** | GUI + XML (.jmx), Groovy | JVM | встроенная (master/slaves) | HTML-report, плагины |
| Gatling | Scala/Java DSL | JVM | Gatling Enterprise | HTML (графики) |
| k6 | JavaScript | Go | k6 Cloud / operator | CLI, Cloud, Grafana |
| Artillery | YAML + JS | Node.js | Artillery Cloud | HTML, Cloud |

JMeter — лучший выбор для enterprise-стека, кросс-протокольных сценариев (JDBC, JMS, LDAP) и QA-команд, предпочитающих GUI-конструктор.

## Маршруты чтения

- **Быстрый старт (30 мин):** введение → первый Test Plan → запуск в non-GUI → HTML-отчёт.
- **Production-setup (3 ч):** весь документ + распределённый запуск + JSR223 + CI/CD.

## Куда идти дальше

- Обзор нагрузочного тестирования — [../README.md](../README.md)
- «Scenarios as code» — [../gatling/README.md](../gatling/README.md)
- Современный легковесный — [../k6/README.md](../k6/README.md)
