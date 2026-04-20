---
title: "Apache ActiveMQ"
description: "Точка входа в раздел ActiveMQ: JMS-брокер для очередей и топиков, классическое enterprise-мессенджинг-решение."
tags:
  - meta
  - index
  - messaging
  - activemq
  - jms
type: "index"
updated: "2026-04-17"
---
# Apache ActiveMQ

**Apache ActiveMQ** — open-source message broker, реализующий спецификацию JMS (Java Message Service) и поддерживающий множество протоколов (OpenWire, STOMP, MQTT, AMQP). Два продукта: классический **ActiveMQ Classic** (зрелый, Java-ориентированный) и **ActiveMQ Artemis** (следующее поколение, выше производительность, HA через replication).

Для кого: инженеры, работающие в enterprise-стеке на Java/Spring с требованиями JMS (очереди с транзакциями, durable subscribers, request-reply). Раздел описывает архитектуру, паттерны обмена сообщениями и сравнение с альтернативами (Kafka, RabbitMQ, NATS).

## Полезные ссылки

### Основные документы
- [[activemq]] — архитектура, установка, работа с очередями/топиками, Spring JMS, HA

### Соседние разделы
- [[README|Родительский раздел: Messaging]]
- [[README|Kafka]] — альтернатива для высокого throughput и стриминга
- [RabbitMQ](../rabbitmq/) — альтернатива с exchange/binding-моделью
- [[README|NATS]] — lightweight cloud-native альтернатива
- [Event-Driven Architecture](../../../architecture/enterprise-patterns/)
- [Spring Boot](../../../frameworks/java-frameworks/spring/)

### Внешние ресурсы
- [Apache ActiveMQ Classic Documentation](https://activemq.apache.org/components/classic/documentation)
- [Apache ActiveMQ Artemis](https://activemq.apache.org/components/artemis/)
- [JMS Specification (Jakarta Messaging)](https://jakarta.ee/specifications/messaging/)
- [Spring JMS Reference](https://docs.spring.io/spring-framework/reference/integration/jms.html)

## Содержание

- [Когда выбирать ActiveMQ](#когда-выбирать-activemq)
- [ActiveMQ vs Kafka vs RabbitMQ vs NATS](#activemq-vs-kafka-vs-rabbitmq-vs-nats)
- [Карта тем](#карта-тем)
- [Production-каталог](#production-каталог)
- [Маршруты чтения](#маршруты-чтения)
- [Куда идти дальше](#куда-идти-дальше)

## Когда выбирать ActiveMQ

Подходит:
- JMS-ориентированные Java/Spring-приложения, транзакционные очереди (XA);
- классический enterprise-стек: request-reply, durable subscribers, message selectors;
- интеграция с протоколами не только Java: STOMP, MQTT, AMQP 1.0;
- команды, уже использующие ActiveMQ и довольные им — нет причин переезжать.

Не подходит:
- высокий throughput (сотни тысяч msg/s) — Kafka лучше;
- event sourcing / streaming / replay истории — Kafka;
- cloud-native с минимальным footprint — NATS;
- сложный routing (fanout / topic с шаблонами) как основной паттерн — RabbitMQ.

## ActiveMQ vs Kafka vs RabbitMQ vs NATS

| Критерий | ActiveMQ | Kafka | RabbitMQ | NATS |
|----------|----------|-------|----------|------|
| Модель | Queue / Topic (JMS) | Log (partitioned) | Exchange → Queue | Subject / JetStream |
| Надёжность | Persistent, transactions | Log, replication | Persistent, mirrors | Core at-most-once; JetStream persist |
| Throughput | Средний (10-50k msg/s) | Очень высокий (100k-1M+) | Средний | Очень высокий (lightweight) |
| Latency | Средняя | Низкая-средняя | Низкая | Очень низкая (sub-ms) |
| Replay сообщений | Ограниченно | Нативно (offset) | Нет | JetStream — да |
| Ordering | FIFO в очереди | Per-partition | Per-queue | Subject order |
| Протоколы | OpenWire, STOMP, MQTT, AMQP | Kafka-specific | AMQP 0.9.1 | NATS-specific |
| JMS | Да | Нет | Нет (есть сторонние) | Нет |
| Кейс | Enterprise JMS, request-reply | Event log, stream, analytics | Сложный routing | IoT, microservices, low-latency |

## Карта тем

| Тема | Где смотреть |
|------|--------------|
| Архитектура (broker, connector, store) | [[activemq#архитектура-и-компоненты]] |
| Установка и конфигурация | [[activemq#установка-и-конфигурация]] |
| Queue vs Topic, durable subscribers | [[activemq]] |
| Spring JMS интеграция (`@JmsListener`) | [[activemq]] |
| HA: Master-Slave, Network of Brokers, Replication (Artemis) | [[activemq]] |
| Мониторинг (JMX, Web Console) | [[activemq]] |

## Production-каталог

- [ ] Persistent message store (KahaDB / JDBC / Replication)
- [ ] Dead Letter Queue настроен для poison messages
- [ ] Redelivery policy и exponential backoff
- [ ] Транзакции согласованы с БД (XA или best-effort 1PC)
- [ ] Мониторинг: queue depth, consumer count, dispatch rate
- [ ] HA: replication или master-slave; проверен failover
- [ ] Security: TLS, user/password, JAAS, policy по очередям
- [ ] Лимиты: memory, disk, producer flow control

## Маршруты чтения

- **Первый Spring JMS-потребитель:** `activemq.md` → Spring JMS `@JmsListener` → очередь точка-точка.
- **Миграция с RabbitMQ:** перевести exchange-binding на селекторы JMS; учесть ordering.
- **Production hardening:** persistent store → DLQ → HA-конфигурация → мониторинг.

## Куда идти дальше

- Kafka для событийного логирования и стриминга — [[README]]
- RabbitMQ для гибкого routing — [../rabbitmq/](../rabbitmq/)
- NATS для cloud-native сценариев — [[README]]
- Event-Driven архитектура — [../../../architecture/enterprise-patterns/](../../../architecture/enterprise-patterns/)
