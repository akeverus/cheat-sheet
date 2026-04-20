---
title: "RabbitMQ"
description: "Точка входа в раздел RabbitMQ: AMQP-модель, exchanges и queues, Spring AMQP, кластеризация, HA-очереди, Federation, Shovel, мониторинг и best practices."
tags:
  - meta
  - index
  - messaging
  - rabbitmq
type: "index"
updated: "2026-04-17"
---
# RabbitMQ

RabbitMQ — открытый брокер сообщений, реализующий протокол AMQP 0.9.1 с расширениями и поддержкой MQTT, STOMP. Работает в режиме классической pub-sub и RPC-over-queue, хорошо подходит для надёжной доставки транзакционных событий, RPC-паттернов, fan-out рассылок. Для высокопропускных потоков событий — см. `kafka/`.

Раздел разбит на два документа: базовый гайд с полной картой exchanges/queues/bindings и примерами Spring AMQP, и продвинутый — с HA-кластером, quorum queues, streams, federation, shovel и production-runbook.

## Полезные ссылки

### Основные документы
- [[rabbitmq|RabbitMQ для Java (основы)]] — exchanges, queues, Spring AMQP, надёжная доставка
- [[rabbitmq-advanced|RabbitMQ Advanced]] — кластеризация, HA, Federation, performance tuning

### Соседние разделы
- [[README|Messaging (обзор)]]
- [[README|Kafka]]
- [[README|ActiveMQ]]
- [[README|NATS]]
- [[README|Event-driven patterns]]
- [[README|Spring Boot]]
- [[README|Distributed tracing]]

### Официальная документация
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [AMQP 0-9-1 Model Explained](https://www.rabbitmq.com/tutorials/amqp-concepts.html)
- [Spring AMQP Reference](https://docs.spring.io/spring-amqp/reference/)
- [RabbitMQ Management HTTP API](https://www.rabbitmq.com/management.html)

## Содержание

- [Что брать из какого файла](#что-брать-из-какого-файла)
- [Exchange types — шпаргалка](#exchange-types--шпаргалка)
- [Когда брать RabbitMQ, а когда Kafka](#когда-брать-rabbitmq-а-когда-kafka)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Что брать из какого файла

| Вопрос | Файл |
|--------|------|
| Что такое exchange, queue, binding, routing key | [[rabbitmq]] |
| Как объявить `@RabbitListener` в Spring Boot | [[rabbitmq]] |
| Publisher confirms, mandatory, return callback | [[rabbitmq]] |
| Dead Letter Exchange и retry-паттерны | [[rabbitmq]], [[rabbitmq-advanced]] |
| Как собрать кластер из 3 нод | [[rabbitmq-advanced#настройка-кластера]] |
| Quorum queues и HA-политики | [[rabbitmq-advanced]] |
| Federation и Shovel между дата-центрами | [[rabbitmq-advanced#federation-и-shovel]] |
| Метрики и алерты в Prometheus | [[rabbitmq-advanced]] |

## Exchange types — шпаргалка

| Тип | Routing | Применение |
|-----|---------|------------|
| `direct` | routing key = binding key | точная маршрутизация по ключу |
| `fanout` | игнорирует routing key | broadcast на все очереди |
| `topic` | pattern matching (`orders.*.paid`) | иерархическая маршрутизация |
| `headers` | по заголовкам сообщения | сложные условия, когда routing key мало |
| `x-consistent-hash` (plugin) | хэширование | равномерное распределение |

## Когда брать RabbitMQ, а когда Kafka

| Критерий | RabbitMQ | Kafka |
|----------|----------|-------|
| Модель | push (брокер шлёт в consumer) | pull (consumer читает log) |
| Доставка | at-least-once, exactly-once через confirms + idempotency | at-least-once, exactly-once через transactions |
| Очерёдность | по queue | по partition |
| Retention | пока не прочитано (или TTL) | время/размер, потребление не удаляет |
| Пропускная способность | десятки тыс. msg/sec | миллионы msg/sec |
| Типовое использование | транзакционные события, RPC, fan-out | event streaming, CDC, analytics |

## Маршруты чтения

- **Базовое использование в микросервисе (1 ч):** [[rabbitmq]] → настройка Spring AMQP → publisher confirms → DLX.
- **Production deployment (3 ч):** [[rabbitmq-advanced]] → quorum queues → мониторинг → runbook.
- **Миграция с Kafka → RabbitMQ:** exchange types → routing → retention → DLX.

## Куда идти дальше

- Паттерны событийной архитектуры — [[README|architecture/software-architecture/]]
- Idempotency, Outbox Pattern — [[README|patterns/]]
- Observability: трассировка, метрики — [[README|monitoring/tracing/]]
- Spring Cloud Stream для абстракции над брокерами — [[README|frameworks/java-frameworks/spring/]]
