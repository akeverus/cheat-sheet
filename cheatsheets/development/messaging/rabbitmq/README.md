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
- [RabbitMQ для Java (основы)](rabbitmq.md) — exchanges, queues, Spring AMQP, надёжная доставка
- [RabbitMQ Advanced](rabbitmq-advanced.md) — кластеризация, HA, Federation, performance tuning

### Соседние разделы
- [Messaging (обзор)](../README.md)
- [Kafka](../kafka/README.md)
- [ActiveMQ](../activemq/README.md)
- [NATS](../nats/README.md)
- [Event-driven patterns](../../../architecture/software-architecture/README.md)
- [Spring Boot](../../../frameworks/java-frameworks/spring/README.md)
- [Distributed tracing](../../../monitoring/tracing/README.md)

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
| Что такое exchange, queue, binding, routing key | [rabbitmq.md](rabbitmq.md) |
| Как объявить `@RabbitListener` в Spring Boot | [rabbitmq.md](rabbitmq.md) |
| Publisher confirms, mandatory, return callback | [rabbitmq.md](rabbitmq.md) |
| Dead Letter Exchange и retry-паттерны | [rabbitmq.md](rabbitmq.md), [rabbitmq-advanced.md](rabbitmq-advanced.md) |
| Как собрать кластер из 3 нод | [rabbitmq-advanced.md](rabbitmq-advanced.md#настройка-кластера) |
| Quorum queues и HA-политики | [rabbitmq-advanced.md](rabbitmq-advanced.md) |
| Federation и Shovel между дата-центрами | [rabbitmq-advanced.md](rabbitmq-advanced.md#federation-и-shovel) |
| Метрики и алерты в Prometheus | [rabbitmq-advanced.md](rabbitmq-advanced.md) |

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

- **Базовое использование в микросервисе (1 ч):** [rabbitmq.md](rabbitmq.md) → настройка Spring AMQP → publisher confirms → DLX.
- **Production deployment (3 ч):** [rabbitmq-advanced.md](rabbitmq-advanced.md) → quorum queues → мониторинг → runbook.
- **Миграция с Kafka → RabbitMQ:** exchange types → routing → retention → DLX.

## Куда идти дальше

- Паттерны событийной архитектуры — [architecture/software-architecture/](../../../architecture/software-architecture/README.md)
- Idempotency, Outbox Pattern — [patterns/](../../../patterns/README.md)
- Observability: трассировка, метрики — [monitoring/tracing/](../../../monitoring/tracing/README.md)
- Spring Cloud Stream для абстракции над брокерами — [frameworks/java-frameworks/spring/](../../../frameworks/java-frameworks/spring/README.md)
