---
title: "Elastic APM"
description: "APM на базе стека Elastic: метрики приложений, трейсы и интеграция с Elasticsearch/Kibana."
tags:
  - monitoring
  - apm
  - elastic-apm
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Elastic APM

APM на базе стека Elastic: метрики приложений, трейсы и интеграция с Elasticsearch/Kibana.

## Введение

**Elastic APM** — часть Elastic Stack (Elasticsearch, Kibana, Beats). Собирает транзакции, span'ы, метрики и ошибки; данные хранятся в Elasticsearch, визуализация в Kibana. Поддерживает Java (elastic-apm-agent), открытый протокол и интеграцию с OpenTelemetry.

## Полезные ссылки

- [Elastic APM Documentation](https://www.elastic.co/guide/en/apm/get-started/current/index.html)
- [Elastic APM Java Agent](https://www.elastic.co/guide/en/apm/agent/java/current/index.html)
- [APM Server](https://www.elastic.co/guide/en/apm/server/current/index.html)

## Содержание

- [Введение](#введение)
- [Архитектура](#архитектура)
- [Интеграция с Java](#интеграция-с-java)
- [Метрики и дашборды в Kibana](#метрики-и-дашборды-в-kibana)
- [Ошибки и алертинг](#ошибки-и-алертинг)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)

## Архитектура

- **APM Agent** (в процессе приложения) отправляет данные в APM Server.
- **APM Server** нормализует и пишет в Elasticsearch.
- **Kibana** — дашборды APM: сервисы, транзакции, зависимости, ошибки, распределённые трейсы.


## Интеграция с Java

Подключить агент при запуске JVM:

```bash
java -javaagent:/path/to/elastic-apm-agent.jar \
  -Delastic.apm.service_name=my-service \
  -Delastic.apm.server_urls=http://apm-server:8200 \
  -jar app.jar
```

Конфигурация через переменные окружения или `elastic-apm.properties`. Агент поддерживает сервлеты, Spring, JMS, JDBC, Redis и другие библиотеки.


## Метрики и дашборды в Kibana

- **Services**: список сервисов, latency, throughput, error rate.
- **Transactions**: разбивка по эндпоинтам, внешние вызовы, БД.
- **Dependencies**: карта зависимостей между сервисами.
- **Distributed Tracing**: цепочка span'ов по запросу.
- **Errors**: группировка исключений, stack traces.


## Ошибки и алертинг

В Kibana можно создавать правила алертинга на основе APM-данных (пороги latency, error rate). Интеграция с Slack, email, webhook.


## Лучшие практики

- Использовать единые имена сервисов и окружений (env) для фильтрации.
- Настроить sampling при высокой нагрузке для снижения объёма данных.
- Хранить секреты (API key для APM Server) в переменных окружения или vault.
- Комбинировать APM с логами и метриками в одном стеке Elastic для полной картины.


## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Данные не появляются в Kibana APM | APM Server недоступен или неверный URL | Проверить `elastic.apm.server_urls`, логи APM Server, сетевую связность |
| Высокая latency после подключения агента | Агент инструментирует слишком много вызовов | Настроить `transaction_sample_rate`, отключить ненужные плагины инструментации |
| Ошибки "queue is full" в логах агента | Агент не успевает отправлять данные | Увеличить размер очереди или включить sampling; проверить пропускную способность APM Server |

## Частые вопросы

**Можно ли использовать Elastic APM без APM Server?**
С версии 7.x данные можно отправлять напрямую в Elasticsearch, но APM Server рекомендуется для нормализации, обогащения и контроля нагрузки на кластер.

**Как интегрировать Elastic APM с OpenTelemetry?**
Elastic APM поддерживает приём данных по протоколу OTLP. Можно использовать OpenTelemetry SDK для инструментации, а APM Server — как бэкенд для хранения и визуализации.

**Чем Elastic APM отличается от Datadog и New Relic?**
Elastic APM — open-source решение, данные хранятся в вашем кластере Elasticsearch. Нет vendor lock-in и оплаты за объём данных, но требуется самостоятельное развёртывание и поддержка инфраструктуры.


## См. также

- [[datadog|Datadog APM]]
- [[new-relic|New Relic APM]]
