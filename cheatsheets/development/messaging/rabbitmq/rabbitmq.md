---
title: "RabbitMQ для Java"
description: "Комплексное руководство по работе с RabbitMQ в Java-приложениях: AMQP протокол, exchanges и queues, Spring AMQP интеграция, кластерная архитектура, мониторинг и best practices."
tags:
  - development
  - messaging
  - rabbitmq
type: "overview"
difficulty: "intermediate"
aliases:
  - "RabbitMQ для Java"
  - "rabbitmq"
prerequisites: []
next:
  - "[[spring-messaging]]"
updated: "2026-04-20"
---
# RabbitMQ для Java

Комплексное руководство по работе с **RabbitMQ** в **Java**-приложениях: **AMQP** протокол, **exchanges** и **queues**, **Spring AMQP** интеграция, кластерная архитектура, мониторинг и **best practices**.

## Полезные ссылки

### Официальная документация
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html) — официальная документация **RabbitMQ**
- [AMQP 0-9-1 Model](https://www.rabbitmq.com/tutorials/amqp-concepts.html) — спецификация **AMQP**
- [RabbitMQ Tutorials](https://www.rabbitmq.com/getstarted.html) — туториалы **RabbitMQ**

### Java интеграции
- [Spring AMQP](https://spring.io/projects/spring-amqp) — **Spring Boot** интеграция
- [RabbitMQ Java Client](https://www.rabbitmq.com/java-client.html) — официальный **Java** клиент
- [Spring Cloud Stream](https://spring.io/projects/spring-cloud-stream) — стриминг и биндинги

### Мониторинг и инструменты
- [RabbitMQ Management Plugin](https://www.rabbitmq.com/management.html) — веб-интерфейс управления
- [RabbitMQ Prometheus Plugin](https://www.rabbitmq.com/prometheus.html) — метрики для **Prometheus**
- [RabbitMQ Shovel Plugin](https://www.rabbitmq.com/shovel.html) — репликация сообщений

### См. также
- [REST API Best Practices](../../api/rest/rest-api-best-practices.md) — **REST API** практики
- [gRPC](../../api/grpc/grpc.md) — **gRPC**
- [Kafka](../kafka/kafka.md) — **Kafka** для **high-throughput**
- [Spring Messaging](../../../frameworks/java-frameworks/spring/spring-messaging.md) — **Spring Messaging**
- [Distributed Tracing](../../../monitoring/tracing/distributed-tracing.md) — мониторинг и трейсинг
- [Вопросы на собеседовании](../../../interview/messaging/rabbitmq-interview.md) — подготовка к интервью

## Содержание

- [Введение в RabbitMQ](#введение-в-rabbitmq)
  - [Почему RabbitMQ?](#почему-rabbitmq)
  - [RabbitMQ vs другие brokers](#rabbitmq-vs-другие-brokers)
    - [RabbitMQ vs Kafka](#rabbitmq-vs-kafka)
    - [RabbitMQ vs ActiveMQ](#rabbitmq-vs-activemq)
  - [Когда использовать RabbitMQ?](#когда-использовать-rabbitmq)
    - [Идеально подходит для:](#идеально-подходит-для)
    - [Не подходит для:](#не-подходит-для)
- [AMQP модель](#amqp-модель)
  - [Основные концепции AMQP](#основные-концепции-amqp)
    - [Messages (Сообщения)](#messages-сообщения)
    - [Exchanges (Обменники)](#exchanges-обменники)
    - [Queues (Очереди)](#queues-очереди)
    - [Bindings (Связки)](#bindings-связки)
  - [Delivery guarantees](#delivery-guarantees)
    - [At most once](#at-most-once)
    - [At least once](#at-least-once)
    - [Exactly once](#exactly-once)
- [Архитектура RabbitMQ](#архитектура-rabbitmq)
  - [Компоненты системы](#компоненты-системы)
    - [Broker (Брокер)](#broker-брокер)
    - [Connections и Channels](#connections-и-channels)
    - [Erlang VM](#erlang-vm)
  - [Плагины](#плагины)
    - [Встроенные плагины](#встроенные-плагины)
    - [Популярные плагины](#популярные-плагины)
- [Установка и настройка](#установка-и-настройка)
  - [Docker установка](#docker-установка)
    - [Single node развертывание](#single-node-развертывание)
    - [Cluster развертывание](#cluster-развертывание)
    - [Kubernetes развертывание](#kubernetes-развертывание)
  - [Конфигурация](#конфигурация)
    - [Основной конфигурационный файл (rabbitmq.conf)](#основной-конфигурационный-файл-rabbitmqconf)
    - [Advanced конфигурация (advanced.config)](#advanced-конфигурация-advancedconfig)
  - [Проверка установки](#проверка-установки)
  - [Конфигурация](#конфигурация-1)
    - [rabbitmq.conf](#rabbitmqconf)
    - [advanced.config (Erlang)](#advancedconfig-erlang)
- [RabbitMQ Java Producer](#rabbitmq-java-producer)
  - [Basic Producer](#basic-producer)
    - [Connection setup](#connection-setup)
    - [Message publishing](#message-publishing)
  - [Advanced Producer Features](#advanced-producer-features)
    - [Publisher Confirms](#publisher-confirms)
    - [Transactions](#transactions)
- [RabbitMQ Java Consumer](#rabbitmq-java-consumer)
  - [Basic Consumer](#basic-consumer)
    - [Message Listener](#message-listener)
    - [Batch Consumer](#batch-consumer)
  - [Advanced Consumer Features](#advanced-consumer-features)
    - [Consumer with Retry](#consumer-with-retry)
    - [Consumer with Dead Letter Queue](#consumer-with-dead-letter-queue)
    - [Consumer with Priority](#consumer-with-priority)
- [Spring AMQP интеграция](#spring-amqp-интеграция)
  - [Spring Boot авто-конфигурация](#spring-boot-авто-конфигурация)
    - [Application Properties](#application-properties)
    - [Авто-конфигурированные бины](#авто-конфигурированные-бины)
  - [Advanced Spring AMQP](#advanced-spring-amqp)
    - [Custom MessageConverter](#custom-messageconverter)
    - [@RabbitListener аннотации](#rabbitlistener-аннотации)
    - [RabbitMQ Health Checks](#rabbitmq-health-checks)
- [Routing и Exchanges](#routing-и-exchanges)
  - [Exchange Types](#exchange-types)
    - [Direct Exchange](#direct-exchange)
    - [Topic Exchange](#topic-exchange)
    - [Headers Exchange](#headers-exchange)
    - [Fanout Exchange](#fanout-exchange)
- [Безопасность](#безопасность)
  - [SSL/TLS](#ssltls)
    - [SSL конфигурация](#ssl-конфигурация)
    - [Java SSL setup](#java-ssl-setup)
  - [Authentication](#authentication)
    - [RabbitMQ authentication](#rabbitmq-authentication)
    - [Java authentication](#java-authentication)
  - [Authorization](#authorization)
    - [Permissions](#permissions)
    - [Policies](#policies)
- [Кластеризация](#кластеризация)
  - [Cluster setup](#cluster-setup)
    - [Manual cluster setup](#manual-cluster-setup)
    - [Docker cluster](#docker-cluster)
  - [Load balancing](#load-balancing)
    - [HAProxy configuration](#haproxy-configuration)
    - [Java client configuration](#java-client-configuration)
  - [Federation](#federation)
    - [Federation setup](#federation-setup)
    - [Federation configuration](#federation-configuration)
- [Мониторинг и управление](#мониторинг-и-управление)
  - [Management Plugin](#management-plugin)
    - [Web UI](#web-ui)
    - [REST API](#rest-api)
  - [Metrics](#metrics)
    - [Prometheus metrics](#prometheus-metrics)
    - [Micrometer integration](#micrometer-integration)
  - [Health Checks](#health-checks)
    - [Spring Boot health check](#spring-boot-health-check)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Connection pooling](#connection-pooling)
    - [Connection factory optimization](#connection-factory-optimization)
  - [Message optimization](#message-optimization)
    - [Message size optimization](#message-size-optimization)
  - [Consumer optimization](#consumer-optimization)
    - [Prefetch settings](#prefetch-settings)
  - [Queue optimization](#queue-optimization)
    - [Queue settings](#queue-settings)
- [Тестирование](#тестирование)
  - [Модульное тестирование](#модульное-тестирование)
    - [Producer testing](#producer-testing)
    - [Consumer testing](#consumer-testing)
  - [Интеграционное тестирование](#интеграционное-тестирование)
    - [Embedded RabbitMQ](#embedded-rabbitmq)
    - [E2E Testing](#e2e-testing)
- [Решение проблем](#решение-проблем)
  - [Распространенные проблемы](#распространенные-проблемы)
    - [Connection issues](#connection-issues)
    - [Message delivery issues](#message-delivery-issues)
    - [Performance issues](#performance-issues)
  - [Инструменты отладки](#инструменты-отладки)
    - [RabbitMQ tracing](#rabbitmq-tracing)
    - [Java debug logging](#java-debug-logging)
    - [Connection monitoring](#connection-monitoring)
- [Лучшие практики](#лучшие-практики)
  - [Архитектурные рекомендации](#архитектурные-рекомендации)
    - [1. Message Design](#1-message-design)
    - [2. Error Handling Strategy](#2-error-handling-strategy)
    - [3. Idempotency](#3-idempotency)
  - [Производительность](#производительность)
    - [4. Connection Management](#4-connection-management)
    - [5. Message Routing Patterns](#5-message-routing-patterns)
  - [Мониторинг](#мониторинг)
    - [6. Comprehensive Monitoring](#6-comprehensive-monitoring)
    - [7. Alerting Rules](#7-alerting-rules)
  - [Безопасность](#безопасность-1)
    - [8. Лучшие практики безопасности](#8-лучшие-практики-безопасности)
  - [Масштабирование](#масштабирование)
    - [9. Horizontal Scaling](#9-horizontal-scaling)
    - [10. Cluster Management](#10-cluster-management)
- [Заключение](#заключение)
  - [Ключевые преимущества RabbitMQ:](#ключевые-преимущества-rabbitmq)
  - [Архитектурные паттерны:](#архитектурные-паттерны)
    - [Work Queues (Task Distribution)](#work-queues-task-distribution)
    - [Publish-Subscribe](#publish-subscribe)
    - [Routing](#routing)
    - [RPC (Request-Response)](#rpc-request-response)
  - [Когда выбирать RabbitMQ:](#когда-выбирать-rabbitmq)
  - [Когда НЕ выбирать:](#когда-не-выбирать)
  - [Рекомендации по внедрению:](#рекомендации-по-внедрению)
  - [Типы Exchanges по назначению:](#типы-exchanges-по-назначению)
    - [Direct Exchange:](#direct-exchange-1)
    - [Topic Exchange:](#topic-exchange-1)
    - [Headers Exchange:](#headers-exchange-1)
    - [Fanout Exchange:](#fanout-exchange-1)
  - [Сводка лучших практик](#сводка-лучших-практик)

## Введение в RabbitMQ

**RabbitMQ** — это надежный и зрелый **message broker**, реализующий **AMQP** (Advanced `Message Queuing` Protocol). **RabbitMQ** разработан на **Erlang** и известен своей надежностью, гибкостью **routing**'а и богатой экосистемой плагинов.

### Почему RabbitMQ?

**RabbitMQ** решает критические проблемы надежной асинхронной коммуникации:**

1. **Гарантированная доставка** — **at-least-once** и **exactly-once semantics**
2. **Гибкий routing** — **exchanges**, **bindings**, **routing keys**
3. **Persistence** — сохранение сообщений на диск
4. **Reliability** — **acknowledgments**, **dead letter queues**
5. **Распределенная обработка** — **clustering** и **federation**
6. **Широкая поддержка** — множество языков и протоколов
7. **Мониторинг** — встроенные инструменты управления
8. **Плагины** — расширяемая функциональность

### RabbitMQ vs другие brokers

#### RabbitMQ vs Kafka

Ниже — сравнение **RabbitMQ** и **Kafka** (текст).
```text
# Сравнение: RabbitMQ — гарантии доставки, Kafka — пропускная способность
RabbitMQ:                   Kafka:
- Гарантированная доставка   - Высокая пропускная способность
- Сложный routing           - Append-only logs
- AMQP протокол             - Custom протокол
- Erlang/OTP                - Scala/Java
- Менее 1M msg/sec          - >1M msg/sec
```

#### RabbitMQ vs ActiveMQ

Ниже — сравнение **RabbitMQ** и **ActiveMQ** (текст).
```text
# Сравнение RabbitMQ и ActiveMQ по производительности и настройке
RabbitMQ:                   ActiveMQ:
- Лучшая производительность  - Более простая настройка
- Богатые routing возможности- JMS стандарт
- Erlang/OTP                - Java
- Лучшая кластеризация      - Более зрелая экосистема
```

### Когда использовать RabbitMQ?

#### Идеально подходит для:
- **Request-response** — **RPC** через **messaging**
- **Task queues** — распределение задач между **workers**
- **Event notification** — **publish-subscribe** паттерн
- **Complex routing** — различные типы **exchanges**
- **Guaranteed delivery** — надежная доставка сообщений
- **Legacy integration** — поддержка различных протоколов
- **Microservices** — асинхронная коммуникация

#### Не подходит для:
- **High-throughput** — >100K **msg**/**sec** (используйте Kafka)
- **Big data** — **large message payloads**
- **Real-time analytics** — **stream processing**
- **Log aggregation** — **append-only** сценарии
- **Simple pub-sub** — без **complex routing**

## AMQP модель

### Основные концепции AMQP

#### Messages (Сообщения)
```java
// Структура AMQP сообщения
public class AMQPMessage {
    private byte[] body;              // Тело сообщения
    private Map<String, Object> headers;  // Заголовки
    private String contentType;       // Тип содержимого
    private String contentEncoding;   // Кодировка
    private String messageId;         // Уникальный ID
    private String correlationId;     // ID для корреляции
    private String replyTo;           // Очередь для ответа
    private String userId;            // ID отправителя
    private String appId;             // ID приложения
    private Instant timestamp;        // Временная метка
    private String type;              // Тип сообщения
    private Integer deliveryMode;     // 1=non-persistent, 2=persistent
    private Integer priority;         // Приоритет (0-255)
    private Integer expiration;       // TTL в мс
}
```

#### Exchanges (Обменники)
**Компоненты, которые маршрутизируют сообщения в очереди:**

**Direct Exchange** — точная маршрутизация по **routing key**:**
```text
# Direct: маршрутизация по точному совпадению routing key
Exchange: order.direct
Routing Key: order.created → Queue: order-processing
Routing Key: order.cancelled → Queue: order-cancellation
```

**Topic Exchange** — паттернная маршрутизация:**
```text
# Topic: маршрутизация по шаблону (order.us.*, order.eu.*)
Exchange: order.topic
Routing Key: order.us.created → Queue: us-orders
Routing Key: order.eu.* → Queue: eu-orders
Routing Key: order.*.urgent → Queue: urgent-orders
```

**Headers Exchange** — маршрутизация по заголовкам:**
```text
# Headers: маршрутизация по заголовкам сообщения
Exchange: order.headers
Headers: region=us, priority=high → Queue: us-high-priority
Headers: type=urgent → Queue: urgent-orders
```

**Fanout Exchange** — **broadcast** всем очередям:**
```text
# Fanout: рассылка во все привязанные очереди
Exchange: notification.fanout
→ Queue: email-service
→ Queue: sms-service
→ Queue: push-service
```

#### Queues (Очереди)
**Хранилища сообщений для **consumer**'ов:**

```java
// Параметры очереди
Queue queue = QueueBuilder.durable("order-processing")
    .autoDelete()                    // Автоудаление при отсутствии consumer'ов
    .exclusive()                     // Эксклюзивная очередь
    .withArgument("x-max-length", 1000)        // Максимальная длина
    .withArgument("x-message-ttl", 60000)      // TTL сообщений (60 сек)
    .withArgument("x-dead-letter-exchange", "dlx") // Dead letter exchange
    .withArgument("x-dead-letter-routing-key", "order.failed") // DL routing key
    .build();
```

#### Bindings (Связки)
**Правила маршрутизации между **exchanges** и **queues**:**

```java
// Direct binding
channel.queueBind("order-processing", "order.direct", "order.created");

// Topic binding
channel.queueBind("us-orders", "order.topic", "order.us.*");
channel.queueBind("urgent-orders", "order.topic", "*.urgent");

// Headers binding
Map<String, Object> headers = Map.of("region", "us", "priority", "high");
channel.queueBind("us-high-priority", "order.headers", "", headers);
```

### Delivery guarantees

#### At most once
**Сообщение может быть потеряно, но не дублировано:**

```java
// Publisher confirms отключены
channel.basicPublish(exchange, routingKey, null, message.getBytes());

// Auto-acknowledgment
channel.basicConsume(queue, true, consumer);
```

#### At least once
**Сообщение может быть дублировано, но не потеряно:**

```java
// Publisher confirms включены
channel.confirmSelect();
channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_BASIC, message.getBytes());
channel.waitForConfirms();

// Manual acknowledgment
channel.basicConsume(queue, false, consumer);
channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
```

#### Exactly once
**Идеальная гарантия (сложно реализовать):**

```java
// Использование транзакций
channel.txSelect();
try {
    channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_BASIC, message.getBytes());
    channel.txCommit();
} catch (Exception e) {
    channel.txRollback();
    throw e;
}
```

## Архитектура RabbitMQ

### Компоненты системы

#### Broker (Брокер)
**Основной сервер **RabbitMQ**:**

```mermaid
flowchart TB
    subgraph Broker[RabbitMQ Broker]
        subgraph Conn[Connections]
            CM1[Connection Manager]
            CM2[Connection Manager]
        end
        subgraph Chan[Channels]
            ChM1[Channel Manager]
            ChM2[Channel Manager]
        end
        subgraph Routing[Routing]
            EM[Exchange Manager]
            QM[Queue Manager]
        end
        subgraph Store[Storage]
            MS1[Message Store]
            MS2[Message Store]
        end
    end
```

#### Connections и Channels
**Многоплексная коммуникация:**

```java
// Connection - физическое TCP соединение
ConnectionFactory factory = new ConnectionFactory();
factory.setHost("localhost");
factory.setPort(5672);
Connection connection = factory.newConnection();

// Channel - виртуальное соединение внутри connection
Channel channel = connection.createChannel();
// Можно создавать multiple channels per connection
Channel channel2 = connection.createChannel();
```

#### Erlang `VM`
**RabbitMQ** работает на **Erlang** `VM`:**

**Преимущества `Erlang`:**
- **Concurrency** — легковесные процессы
- **Fault tolerance** — **supervision trees**
- **Hot code swapping** — **zero-downtime upgrades**
- **Distribution** — встроенная кластеризация

**Особенности:**
- **OTP framework** — **battle-tested patterns**
- **Mnesia** — **distributed database** для метаданных
- **inet** — **TCP**/`IP` **stack**
- **ssl** — **SSL**/**TLS** поддержка

### Плагины

#### Встроенные плагины
- **rabbitmq_management** — **Web** `UI` и **REST API**
- **rabbitmq_mqtt** — **MQTT** протокол
- **rabbitmq_stomp** — **STOMP** протокол
- **rabbitmq_web_mqtt** — **WebSocket MQTT**
- **rabbitmq_amqp1_0** — **AMQP** `1.0` протокол

#### Популярные плагины
- **rabbitmq_shovel** — репликация сообщений
- **rabbitmq_federation** — **federation** между кластерами
- **rabbitmq_consistent_hash_exchange** — **consistent hashing**
- **rabbitmq_delayed_message_exchange** — **delayed messages**
- **rabbitmq_message_timestamp** — **timestamps** для сообщений

## Установка и настройка

### Docker установка

**Docker** обеспечивает самый быстрый и надежный способ развертывания **RabbitMQ** для разработки и тестирования. Контейнеры **RabbitMQ** полностью изолированы и легко масштабируемы.

#### Single node развертывание

**Single node setup** подходит для разработки, тестирования и небольших **production** сред. В этой конфигурации запускается один экземпляр **RabbitMQ** с **management plugin**.

```yaml
version: '3.8'
services:
  # RabbitMQ с management интерфейсом
  rabbitmq:
    # Используем официальный образ с management UI
    image: rabbitmq:3.12-management-alpine

    # Порты для внешнего доступа
    ports:
      - "5672:5672"   # AMQP порт - основной протокол для клиентов
      - "15672:15672" # Management UI порт - веб-интерфейс администратора

    # Переменные окружения для конфигурации
    environment:
      # Имя пользователя по умолчанию (admin - стандартное)
      RABBITMQ_DEFAULT_USER: admin
      # Пароль пользователя (меняйте в production!)
      RABBITMQ_DEFAULT_PASS: admin123
      # Виртуальный хост по умолчанию
      RABBITMQ_DEFAULT_VHOST: /

    # Volumes для персистентности данных
    volumes:
      # rabbitmq_data - хранит очереди, сообщения, метаданные
      - rabbitmq_data:/var/lib/rabbitmq
      # Опционально: конфигурационные файлы
      # - ./rabbitmq.conf:/etc/rabbitmq/rabbitmq.conf:ro

    # Health check для проверки готовности сервиса
    healthcheck:
      # Команда проверки: ping через rabbitmq-diagnostics
      test: ["CMD", "rabbitmq-diagnostics", "ping"]
      # Интервал между проверками
      interval: 30s
      # Таймаут на выполнение проверки
      timeout: 10s
      # Количество неудачных проверок для признания unhealthy
      retries: 5
      # Начальная задержка перед первой проверкой
      start_period: 40s

    # Перезапуск политики
    restart: unless-stopped

    # Networks для изоляции
    networks:
      - rabbitmq-network

# Определение volumes
volumes:
  rabbitmq_data:
    # Использовать local driver для лучшей производительности
    driver: local

# Определение networks
networks:
  rabbitmq_network:
    driver: bridge
    # Опционально: настраиваемые параметры сети
    # driver_opts:
    #   com.docker.network.bridge.name: rabbitmq_bridge
```

**Запуск single node:**
```bash
# Запуск в фоне
docker-compose up -d

# Проверка логов
docker-compose logs -f rabbitmq

# Проверка health
docker-compose ps

# Подключение к management UI
# http://localhost:15672
# login: admin, password: admin123
```

**Что происходит при запуске:**
1. **RabbitMQ сервер** стартует и инициализирует **Mnesia database**
2. **Management plugin** активируется автоматически
3. **Default user** создается с заданными **credentials**
4. **AMQP порт 5672** открывается для клиентских подключений
5. **Management порт 15672** открывается для веб-интерфейса
6. **Health check** начинает мониторить готовность сервиса

#### Cluster развертывание

**RabbitMQ кластер** обеспечивает высокую доступность и распределение нагрузки. Кластер состоит из нескольких узлов, которые делят очереди и обмениваются метаданными.

```yaml
version: '3.8'
services:
  # Первый узел кластера (seed node)
  rabbitmq1:
    image: rabbitmq:3.12-management-alpine
    # Уникальное имя хоста для кластера
    hostname: rabbitmq1
    container_name: rabbitmq1

    environment:
      # Erlang cookie - секретный ключ для кластерной аутентификации
      RABBITMQ_ERLANG_COOKIE: rabbitmq_cluster_secret_key_12345
      # Пользовательские credentials
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: secure_password_123
      # Отключаем loopback_users для кластерной работы
      RABBITMQ_LOOPBACK_USERS: none

    volumes:
      # Персистентное хранилище для первого узла
      - rabbitmq1_data:/var/lib/rabbitmq
      # Файлы для кластерной конфигурации
      - ./cluster-entrypoint.sh:/usr/local/bin/cluster-entrypoint.sh:ro

    ports:
      - "5672:5672"   # AMQP для первого узла
      - "15672:15672" # Management UI для первого узла

    networks:
      - rabbitmq-cluster
    restart: unless-stopped

  # Второй узел кластера
  rabbitmq2:
    image: rabbitmq:3.12-management-alpine
    hostname: rabbitmq2
    container_name: rabbitmq2

    # Зависимость от первого узла
    depends_on:
      - rabbitmq1

    environment:
      # Тот же Erlang cookie для аутентификации в кластере
      RABBITMQ_ERLANG_COOKIE: rabbitmq_cluster_secret_key_12345
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: secure_password_123
      RABBITMQ_LOOPBACK_USERS: none

    volumes:
      - rabbitmq2_data:/var/lib/rabbitmq
      - ./cluster-entrypoint.sh:/usr/local/bin/cluster-entrypoint.sh:ro

    ports:
      - "5673:5672"   # AMQP на другом порту (5673)
      - "15673:15672" # Management UI на другом порту (15673)

    networks:
      - rabbitmq-cluster
    restart: unless-stopped

  # Третий узел кластера
  rabbitmq3:
    image: rabbitmq:3.12-management-alpine
    hostname: rabbitmq3
    container_name: rabbitmq3

    # Зависимость от первых двух узлов
    depends_on:
      - rabbitmq1
      - rabbitmq2

    environment:
      RABBITMQ_ERLANG_COOKIE: rabbitmq_cluster_secret_key_12345
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: secure_password_123
      RABBITMQ_LOOPBACK_USERS: none

    volumes:
      - rabbitmq3_data:/var/lib/rabbitmq
      - ./cluster-entrypoint.sh:/usr/local/bin/cluster-entrypoint.sh:ro

    ports:
      - "5674:5672"   # AMQP на порту 5674
      - "15674:15672" # Management UI на порту 15674

    networks:
      - rabbitmq-cluster
    restart: unless-stopped

volumes:
  rabbitmq1_data:
    driver: local
  rabbitmq2_data:
    driver: local
  rabbitmq3_data:
    driver: local

networks:
  rabbitmq-cluster:
    driver: bridge
```

**Скрипт для кластерной инициализации:**
```bash
#!/bin/bash
# cluster-entrypoint.sh

# Ожидание запуска RabbitMQ
sleep 30

# Проверка, является ли этот узел seed node
if [ "$HOSTNAME" = "rabbitmq1" ]; then
    echo "This is the seed node (rabbitmq1)"
    # Seed node просто запускается
else
    echo "Joining cluster from $HOSTNAME to rabbitmq1"
    # Присоединение к кластеру
    rabbitmqctl stop_app
    rabbitmqctl join_cluster rabbit@rabbitmq1
    rabbitmqctl start_app

    # Настройка политик кластера (опционально)
    rabbitmqctl set_policy ha-all ".*" '{"ha-mode":"all","ha-sync-mode":"automatic"}'
fi

# Держим контейнер запущенным
tail -f /dev/null
```

**Запуск кластера:**
```bash
# Создание entrypoint скрипта
chmod +x cluster-entrypoint.sh

# Запуск кластера
docker-compose up -d

# Проверка статуса кластера
docker exec rabbitmq1 rabbitmqctl cluster_status

# Проверка, что все узлы присоединились
docker exec rabbitmq1 rabbitmqctl list_connections
```

**Кластеризация в действии:**
1. **rabbitmq1** запускается как **seed node**
2. **rabbitmq2** и **rabbitmq3** присоединяются к кластеру
3. **Очереди автоматически** распределяются между узлами
4. **High availability** обеспечивается репликацией
5. **Load balancing** происходит автоматически

#### Kubernetes развертывание

**Kubernetes** предоставляет оркестрацию для **production** развертываний **RabbitMQ**.

```yaml
# rabbitmq-cluster.yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: rabbitmq-cluster
spec:
  serviceName: rabbitmq
  replicas: 3
  selector:
    matchLabels:
      app: rabbitmq
  template:
    metadata:
      labels:
        app: rabbitmq
    spec:
      containers:
      - name: rabbitmq
        image: rabbitmq:3.12-management-alpine
        ports:
        - containerPort: 5672
          name: amqp
        - containerPort: 15672
          name: management
        env:
        - name: RABBITMQ_ERLANG_COOKIE
          value: "RABBITMQ_CLUSTER_COOKIE_SECRET"
        - name: RABBITMQ_DEFAULT_USER
          value: "admin"
        - name: RABBITMQ_DEFAULT_PASS
          valueFrom:
            secretKeyRef:
              name: rabbitmq-secret
              key: password
        volumeMounts:
        - name: rabbitmq-data
          mountPath: /var/lib/rabbitmq
        - name: rabbitmq-config
          mountPath: /etc/rabbitmq
        livenessProbe:
          exec:
            command: ["rabbitmq-diagnostics", "ping"]
          initialDelaySeconds: 60
          periodSeconds: 60
          timeoutSeconds: 15
        readinessProbe:
          exec:
            command: ["rabbitmq-diagnostics", "ping"]
          initialDelaySeconds: 20
          periodSeconds: 30
          timeoutSeconds: 10
  volumeClaimTemplates:
  - metadata:
      name: rabbitmq-data
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 10Gi

---
apiVersion: v1
kind: Service
metadata:
  name: rabbitmq-service
spec:
  selector:
    app: rabbitmq
  ports:
  - name: amqp
    port: 5672
    targetPort: 5672
  - name: management
    port: 15672
    targetPort: 15672
  clusterIP: None  # Headless service для stateful set
```

### Конфигурация

#### Основной конфигурационный файл (rabbitmq.conf)

**Файл rabbitmq.conf** содержит все настройки **RabbitMQ** в современном формате (RabbitMQ 3.7+). Он использует **sysctl-like** синтаксис и заменяет старый формат.

```ini
# ===========================================
# RabbitMQ Configuration File (rabbitmq.conf)
# ===========================================

# СЕТЕВЫЕ НАСТРОЙКИ
# Настройки сетевых интерфейсов и протоколов

# TCP listeners для AMQP
listeners.tcp.default = 5672

# Дополнительные TCP listeners
# listeners.tcp.1 = 127.0.0.1:5673
# listeners.tcp.2 = 0.0.0.0:5674

# Management plugin настройки
management.tcp.port = 15672
management.tcp.ip = 0.0.0.0

# SSL/TLS настройки (для шифрования)
# listeners.ssl.default = 5671
# ssl_options.certfile = /path/to/cert.pem
# ssl_options.keyfile = /path/to/key.pem
# ssl_options.cacertfile = /path/to/ca.pem

# БЕЗОПАСНОСТЬ
# Настройки аутентификации и авторизации

# SASL аутентификация
auth_mechanisms.1 = PLAIN
auth_mechanisms.2 = AMQPLAIN

# Backing file для Mnesia
# mnesia.dir = /var/lib/rabbitmq/mnesia

# РЕСУРСНЫЕ ОГРАНИЧЕНИЯ
# Настройки памяти и диска

# Порог свободного места на диске (абсолютное значение)
disk_free_limit.absolute = 50MB

# Порог свободного места на диске (относительно RAM)
# disk_free_limit.relative = 1.0

# Порог использования памяти (относительно доступной RAM)
vm_memory_high_watermark.relative = 0.6

# Абсолютный порог использования памяти
# vm_memory_high_watermark.absolute = 1GB

# КЛАСТЕРНЫЕ НАСТРОЙКИ
# Конфигурация для кластеров

# Имя кластера
cluster_name = rabbit@rabbitmq-cluster

# Стратегия обработки сетевых партиций
cluster_partition_handling = pause_minority

# Автоматическое восстановление после сетевых проблем
# cluster_partition_handling = autoheal

# Настройки для Erlang кластерной коммуникации
# erlang.distribution.connection_retries = 5

# ПОЛИТИКИ И ПЛАГИНЫ
# Настройки политик и плагинов

# Включение management plugin (автоматически)
# management.load_definitions = /etc/rabbitmq/definitions.json

# HEARTBEAT И TIMEOUT'Ы
# Настройки сетевых таймаутов

# Heartbeat интервал в секундах
heartbeat = 60

# Handshake timeout
handshake_timeout = 10000

# Channel operation timeout
channel_operation_timeout = 15000

# Consumer timeout
consumer_timeout = 1800000

# ЛОГИРОВАНИЕ
# Настройки логирования

# Уровень логирования
log.default.level = info

# Файлы логов
log.file = true
log.file.path = /var/log/rabbitmq/rabbitmq.log

# Ротация логов
log.file.rotation.date = $D0
log.file.rotation.count = 10

# Console logging
log.console = false

# Логирование соединений
log.connection.level = info

# ДОПОЛНИТЕЛЬНЫЕ НАСТРОЙКИ
# Специфические параметры

# Максимальный размер сообщения
# max_message_size = 134217728

# Максимальное количество сообщений в очереди
# max_queue_length = 1000000

# Сборщик мусора Erlang
# erlang.gc.minor.interval = 5000

# TCP backlog
tcp_listen_options.backlog = 128

# Socket buffer sizes
tcp_listen_options.sndbuf = 196608
tcp_listen_options.recbuf = 196608
```

#### Advanced конфигурация (advanced.config)

**Файл advanced.config** использует **Erlang** синтаксис для сложных настроек, недоступных в **rabbitmq.conf**.

```erlang
[
  {rabbit, [
    % Настройки TCP
    {tcp_listen_options, [
      {backlog, 128},
      {sndbuf, 196608},
      {recbuf, 196608},
      {keepalive, true},
      {nodelay, true}
    ]},

    % Настройки SSL
    {ssl_listeners, []},
    {ssl_options, [
      {certfile, "/etc/rabbitmq/ssl/cert.pem"},
      {keyfile, "/etc/rabbitmq/ssl/key.pem"},
      {cacertfile, "/etc/rabbitmq/ssl/ca.pem"},
      {verify, verify_peer},
      {fail_if_no_peer_cert, true}
    ]},

    % Настройки кластерных партиций
    {cluster_partition_handling, pause_minority},
    {cluster_keepalive_interval, 10000},

    % Настройки Mnesia
    {mnesia_table_loading_retry_timeout, 30000},
    {mnesia_table_loading_retry_limit, 10},

    % Настройки очередей
    {maximum_queue_length, 1000000},
    {maximum_message_size, 134217728},

    % Настройки consumer'ов
    {consumer_timeout, 1800000},
    {consumer_credit, 1000}
  ]},

  % Настройки для плагинов
  {rabbitmq_management, [
    {load_definitions, "/etc/rabbitmq/definitions.json"},
    {rates_mode, detailed}
  ]},

  % Настройки для shovel plugin
  {rabbitmq_shovel, [
    {shovels, [
      {my_shovel, [
        {sources, [
          {broker, "amqp://user:pass@host:port/vhost"}
        ]},
        {destinations, [
          {broker, "amqp://user:pass@host:port/vhost"}
        ]},
        {queue, <<"source-queue">>},
        {prefetch_count, 1000}
      ]}
    ]}
  ]}
].
```

**РАЗЛИЧНЫЕ КОНФИГУРАЦИИ:**

**Development конфигурация:**
```ini
# Минимальные настройки для разработки
listeners.tcp.default = 5672
management.tcp.port = 15672
management.tcp.ip = 0.0.0.0

# Отключаем строгие лимиты
disk_free_limit.absolute = 10MB
vm_memory_high_watermark.relative = 0.8

# Детальное логирование для debugging
log.default.level = debug
log.console = true
```

**Production конфигурация:**
```ini
# Оптимизированные настройки для production
listeners.tcp.default = 5672
management.tcp.ip = 127.0.0.1  # Management только localhost

# Строгие лимиты ресурсов
disk_free_limit.absolute = 1GB
vm_memory_high_watermark.relative = 0.4

# Минимальное логирование
log.default.level = warning
log.file = true
log.console = false

# SSL для безопасности
listeners.ssl.default = 5671
ssl_options.certfile = /etc/ssl/rabbitmq/cert.pem
ssl_options.keyfile = /etc/ssl/rabbitmq/key.pem

# Кластеризация
cluster_name = prod-rabbitmq-cluster
cluster_partition_handling = pause_minority
```

**High-performance конфигурация:**
```ini
# Настройки для максимальной производительности
tcp_listen_options.backlog = 1024
tcp_listen_options.sndbuf = 1048576
tcp_listen_options.recbuf = 1048576

# Высокие лимиты
vm_memory_high_watermark.relative = 0.8
disk_free_limit.absolute = 10GB

# Оптимизация очередей
maximum_queue_length = 10000000
maximum_message_size = 268435456

# Consumer credits
consumer_credit = 5000

# Минимальное логирование
log.default.level = error
```

### Проверка установки

**После установки важно протестировать функциональность:**

```bash
# Проверка статуса RabbitMQ
rabbitmqctl status

# Проверка запущенных плагинов
rabbitmq-plugins list

# Создание тестовой очереди
rabbitmqadmin declare queue name=test-queue durable=true

# Публикация тестового сообщения
rabbitmqadmin publish exchange=amq.default routing_key=test-queue payload="Hello RabbitMQ"

# Чтение сообщения
rabbitmqadmin get queue=test-queue

# Проверка соединений
rabbitmqctl list_connections

# Проверка каналов
rabbitmqctl list_channels

# Проверка очередей
rabbitmqctl list_queues

# Проверка exchanges
rabbitmqctl list_exchanges
```

**Web `Management Interface`:**
- **URL: http**://**localhost**:15672
- **Login: admin**/**admin123** (или настроенные credentials)
- **Возможности:**
  - Просмотр очередей и **exchanges**
  - Мониторинг соединений и каналов
  - Управление пользователями и **permissions**
  - Просмотр сообщений в очередях
  - Графики производительности

### Конфигурация

#### rabbitmq.conf
```ini
# Network settings
listeners.tcp.default = 5672
management.tcp.port = 15672
management.tcp.ip = 0.0.0.0

# Security
ssl_options.verify = verify_peer
ssl_options.fail_if_no_peer_cert = false

# Resource limits
disk_free_limit.absolute = 50MB
vm_memory_high_watermark.relative = 0.6

# Clustering
cluster_name = rabbit@rabbitmq1
cluster_partition_handling = pause_minority

# Policies
cluster_partition_handling = pause_minority
```

#### advanced.config (Erlang)
```erlang
[
  {rabbit, [
    {tcp_listeners, [5672]},
    {ssl_listeners, []},
    {vm_memory_high_watermark, 0.6},
    {disk_free_limit, {mem_relative, 1.0}},
    {log_levels, [{connection, info}]},
    {heartbeat, 60},
    {default_user, <<"admin">>},
    {default_pass, <<"admin123">>},
    {loopback_users, []}
  ]},

  {rabbitmq_management, [
    {load_definitions, "/etc/rabbitmq/definitions.json"}
  ]},

  {rabbitmq_shovel, [
    {shovels, [
      {my_shovel, [
        {sources, [
          {brokers, ["amqp://localhost"]},
          {declarations, [
            {'queue.declare', [{queue, <<"source_queue">>}]}
          ]}
        ]},
        {destinations, [
          {brokers, ["amqp://remote-host"]},
          {declarations, [
            {'queue.declare', [{queue, <<"destination_queue">>}]}
          ]}
        ]},
        {queue, <<"source_queue">>},
        {prefetch_count, 1000}
      ]}
    ]}
  ]}
].
```

## RabbitMQ Java Producer

### Basic Producer

#### Connection setup
```java
// Конфигурация очередей, обменников и биндингов
@Configuration
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin123");

        // Connection pooling
        connectionFactory.setChannelCacheSize(10);
        connectionFactory.setConnectionCacheSize(5);

        // Connection timeout
        connectionFactory.setConnectionTimeout(60000);

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // Message converter
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());

        // Retry configuration
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
        retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy());
        rabbitTemplate.setRetryTemplate(retryTemplate);

        // Confirm callback
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("Message confirmed: " + correlationData);
            } else {
                System.err.println("Message rejected: " + cause);
            }
        });

        // Return callback
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.err.println("Message returned: " + replyText);
        });

        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
```

#### Message publishing
```java
// Отправка сообщений в exchange с routing key
@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderCreated(Order order) {
        // Direct exchange
        rabbitTemplate.convertAndSend("order.direct", "order.created", order);
    }

    public void sendOrderCancelled(String orderId) {
        // Topic exchange
        rabbitTemplate.convertAndSend("order.topic", "order.cancelled", orderId);
    }

    public void sendNotification(Notification notification) {
        // Fanout exchange - broadcast to all queues
        rabbitTemplate.convertAndSend("notification.fanout", "", notification);
    }

    public void sendDelayedMessage(String message, long delayMillis) {
        MessageProperties properties = new MessageProperties();
        properties.setDelay((int) delayMillis);

        Message messageObj = MessageBuilder
            .withBody(message.getBytes())
            .andProperties(properties)
            .build();

        rabbitTemplate.send("delayed.exchange", "delayed.routing.key", messageObj);
    }

    public void sendWithCustomHeaders(Order order) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader("priority", "high");
        properties.setHeader("region", "us-east");
        properties.setHeader("version", "1.0");

        Message message = MessageBuilder
            .withBody(serializeOrder(order))
            .andProperties(properties)
            .build();

        rabbitTemplate.send("order.headers", "", message);
    }

    private byte[] serializeOrder(Order order) {
        // Serialization logic
        return "{}".getBytes();
    }
}
```

### Advanced Producer Features

#### Publisher Confirms
```java
@Configuration
public class PublisherConfirmConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // Enable publisher confirms
        rabbitTemplate.setMandatory(true);

        // Confirm callback
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData != null) {
                String messageId = correlationData.getId();
                if (ack) {
                    confirmService.markConfirmed(messageId);
                    System.out.println("Message confirmed: " + messageId);
                } else {
                    confirmService.markRejected(messageId, cause);
                    System.err.println("Message rejected: " + messageId + ", cause: " + cause);
                }
            }
        });

        // Return callback for unroutable messages
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String messageId = message.getMessageProperties().getMessageId();
            returnService.handleReturnedMessage(messageId, replyCode, replyText);
            System.err.println("Message returned: " + messageId + ", code: " + replyCode);
        });

        return rabbitTemplate;
    }
}

@Service
public class ConfirmService {

    private final Map<String, CompletableFuture<Boolean>> pendingConfirms = new ConcurrentHashMap<>();

    public void markConfirmed(String messageId) {
        CompletableFuture<Boolean> future = pendingConfirms.remove(messageId);
        if (future != null) {
            future.complete(true);
        }
    }

    public void markRejected(String messageId, String cause) {
        CompletableFuture<Boolean> future = pendingConfirms.remove(messageId);
        if (future != null) {
            future.completeExceptionally(new RuntimeException(cause));
        }
    }

    public CompletableFuture<Boolean> waitForConfirm(String messageId) {
        return pendingConfirms.computeIfAbsent(messageId, k -> new CompletableFuture<>());
    }
}

@Service
public class AdvancedProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ConfirmService confirmService;

    public AdvancedProducer(RabbitTemplate rabbitTemplate, ConfirmService confirmService) {
        this.rabbitTemplate = confirmService;
        this.confirmService = confirmService;
    }

    public void sendWithConfirm(Order order) {
        String messageId = UUID.randomUUID().toString();

        CorrelationData correlationData = new CorrelationData(messageId);
        rabbitTemplate.convertAndSend("order.direct", "order.created", order, correlationData);

        // Wait for confirmation with timeout
        try {
            boolean confirmed = confirmService.waitForConfirm(messageId)
                .get(30, TimeUnit.SECONDS);

            if (confirmed) {
                System.out.println("Order sent and confirmed: " + order.getId());
            } else {
                throw new RuntimeException("Message was not confirmed");
            }
        } catch (Exception e) {
            System.err.println("Failed to confirm message: " + e.getMessage());
            // Handle confirmation timeout or failure
        }
    }
}
```

#### Transactions
```java
@Service
public class TransactionalProducer {

    private final RabbitTemplate rabbitTemplate;
    private final OrderService orderService;

    public TransactionalProducer(RabbitTemplate rabbitTemplate, OrderService orderService) {
        this.rabbitTemplate = rabbitTemplate;
        this.orderService = orderService;
    }

    @Transactional
    public void createOrderAndSendEvent(CreateOrderRequest request) {
        // Create order in database
        Order order = orderService.createOrder(request);

        // Send event - will be rolled back if transaction fails
        rabbitTemplate.convertAndSend("order.direct", "order.created", order);

        // If database operation fails, message won't be sent
        // If message send fails, database transaction will be rolled back
    }
}
```

## RabbitMQ Java Consumer

### Basic Consumer

#### Message Listener
```java
@Service
public class OrderConsumer {

    private final OrderService orderService;

    public OrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "order-processing")
    public void handleOrderCreated(Order order, Message message, Channel channel) throws IOException {
        try {
            System.out.println("Received order: " + order.getId());

            // Process the order
            orderService.processOrder(order);

            // Manual acknowledgment
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            System.err.println("Failed to process order: " + e.getMessage());

            // Reject message and send to dead letter queue
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    @RabbitListener(queues = "order-cancellation")
    public void handleOrderCancelled(String orderId, Message message, Channel channel) throws IOException {
        try {
            orderService.cancelOrder(orderId);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
```

#### Batch Consumer
```java
@Configuration
public class BatchConsumerConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory batchContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // Batch settings
        factory.setBatchListener(true);
        factory.setBatchSize(10);
        factory.setReceiveTimeout(10000L);
        factory.setConsumerBatchEnabled(true);

        return factory;
    }
}

@Service
public class BatchOrderConsumer {

    private final OrderService orderService;

    public BatchOrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "order-batch-processing", containerFactory = "batchContainerFactory")
    public void handleBatchOrders(List<Message> messages, Channel channel) throws IOException {
        System.out.println("Received batch of " + messages.size() + " orders");

        List<Long> deliveryTags = new ArrayList<>();
        List<Order> orders = new ArrayList<>();

        try {
            for (Message message : messages) {
                deliveryTags.add(message.getMessageProperties().getDeliveryTag());
                Order order = (Order) rabbitTemplate.getMessageConverter().fromMessage(message);
                orders.add(order);
            }

            // Process batch
            orderService.processOrderBatch(orders);

            // Acknowledge all messages
            for (Long deliveryTag : deliveryTags) {
                channel.basicAck(deliveryTag, false);
            }

        } catch (Exception e) {
            System.err.println("Failed to process batch: " + e.getMessage());

            // Reject all messages in batch
            for (Long deliveryTag : deliveryTags) {
                channel.basicReject(deliveryTag, false);
            }
        }
    }
}
```

### Advanced Consumer Features

#### Consumer with Retry
```java
@Configuration
public class RetryConsumerConfig {

    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
            .maxAttempts(3)
            .backOffOptions(1000, 2.0, 10000) // initial, multiplier, max
            .recoverer(new RejectAndDontRequeueRecoverer())
            .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory retryContainerFactory(
            ConnectionFactory connectionFactory,
            RetryOperationsInterceptor retryInterceptor) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(retryInterceptor);

        return factory;
    }
}

@Service
public class RetryOrderConsumer {

    private final OrderService orderService;

    public RetryOrderConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "order-retry", containerFactory = "retryContainerFactory")
    public void handleOrderWithRetry(Order order) {
        // If this method throws an exception, it will be retried
        // After max attempts, message goes to dead letter queue
        orderService.processOrder(order);
    }
}
```

#### Consumer with Dead Letter Queue
```java
@Configuration
public class DeadLetterConfig {

    @Bean
    public Queue orderProcessingQueue() {
        return QueueBuilder.durable("order-processing")
            .withArgument("x-dead-letter-exchange", "dlx")
            .withArgument("x-dead-letter-routing-key", "order.failed")
            .withArgument("x-message-ttl", 60000) // 60 seconds TTL
            .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("order-dead-letter").build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx");
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
            .to(deadLetterExchange())
            .with("order.failed");
    }
}

@Service
public class DeadLetterConsumer {

    @RabbitListener(queues = "order-dead-letter")
    public void handleDeadLetter(Message failedMessage, @Header("x-death") Map<String, Object> death) {
        System.err.println("Received dead letter: " + failedMessage);

        // Extract failure information
        List<Map<String, Object>> deathList = (List<Map<String, Object>>) death.get("x-death");
        if (deathList != null && !deathList.isEmpty()) {
            Map<String, Object> deathInfo = deathList.get(0);
            String reason = (String) deathInfo.get("reason");
            String queue = (String) deathInfo.get("queue");
            Long count = (Long) deathInfo.get("count");

            System.err.println("Failure reason: " + reason);
            System.err.println("Original queue: " + queue);
            System.err.println("Retry count: " + count);
        }

        // Handle failed message (send alert, save to database, etc.)
        handleFailedMessage(failedMessage);
    }

    private void handleFailedMessage(Message message) {
        // Implementation depends on business requirements
        // Could send email alert, save to error table, etc.
    }
}
```

#### Consumer with Priority
```java
@Configuration
public class PriorityConsumerConfig {

    @Bean
    public Queue priorityOrderQueue() {
        return QueueBuilder.durable("order-priority")
            .withArgument("x-max-priority", 10) // Priority levels 0-10
            .build();
    }

    @Bean
    public DirectExchange priorityOrderExchange() {
        return new DirectExchange("order.priority");
    }

    @Bean
    public Binding priorityOrderBinding() {
        return BindingBuilder.bind(priorityOrderQueue())
            .to(priorityOrderExchange())
            .with("order.created");
    }
}

@Service
public class PriorityOrderConsumer {

    @RabbitListener(queues = "order-priority")
    public void handlePriorityOrder(Order order, @Header("priority") Integer priority) {
        // Messages with higher priority values are processed first
        System.out.println("Processing order with priority: " + priority);

        if (priority != null && priority >= 8) {
            // High priority processing
            processHighPriorityOrder(order);
        } else {
            // Normal processing
            processNormalOrder(order);
        }
    }

    private void processHighPriorityOrder(Order order) {
        // Expedited processing logic
    }

    private void processNormalOrder(Order order) {
        // Standard processing logic
    }
}

@Service
public class PriorityProducer {

    private final RabbitTemplate rabbitTemplate;

    public PriorityProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendHighPriorityOrder(Order order) {
        MessageProperties properties = new MessageProperties();
        properties.setPriority(9); // High priority

        Message message = MessageBuilder
            .withBody(serializeOrder(order))
            .andProperties(properties)
            .build();

        rabbitTemplate.send("order.priority", "order.created", message);
    }

    public void sendNormalOrder(Order order) {
        MessageProperties properties = new MessageProperties();
        properties.setPriority(1); // Normal priority

        Message message = MessageBuilder
            .withBody(serializeOrder(order))
            .andProperties(properties)
            .build();

        rabbitTemplate.send("order.priority", "order.created", message);
    }
}
```

## Spring AMQP интеграция

### Spring Boot авто-конфигурация

#### Application Properties
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123
    virtual-host: /
    connection-timeout: 60000

    # Connection pooling
    cache:
      channel:
        size: 10
      connection:
        size: 5

    # Listener settings
    listener:
      simple:
        concurrency: 5
        max-concurrency: 10
        prefetch: 1
        acknowledge-mode: manual
        retry:
          enabled: true
          max-attempts: 3
          initial-interval: 1000ms
          multiplier: 2.0
          max-interval: 10000ms

    # Template settings
    template:
      mandatory: true
      retry:
        enabled: true
        max-attempts: 3
        initial-interval: 1000ms
```

#### Авто-конфигурированные бины
```java
@Service
public class AutoConfiguredProducer {

    private final RabbitTemplate rabbitTemplate;

    public AutoConfiguredProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String routingKey, Object message) {
        rabbitTemplate.convertAndSend(routingKey, message);
    }

    public void sendToExchange(String exchange, String routingKey, Object message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}

@Service
public class AutoConfiguredConsumer {

    @RabbitListener(queues = "auto-queue")
    public void handleMessage(String message) {
        System.out.println("Received: " + message);
        // Spring Boot automatically configures everything
    }

    @RabbitListener(queues = "json-queue")
    public void handleJsonMessage(Order order) {
        System.out.println("Received order: " + order.getId());
        // JSON conversion is automatic
    }
}
```

### Advanced Spring AMQP

#### Custom MessageConverter
```java
@Configuration
public class CustomConverterConfig {

    @Bean
    public MessageConverter customMessageConverter() {
        return new AbstractMessageConverter() {
            @Override
            protected Message createMessage(Object object, MessageProperties messageProperties) {
                // Custom serialization
                byte[] body = serializeToCustomFormat(object);
                messageProperties.setContentType("application/custom");
                return new Message(body, messageProperties);
            }

            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                // Custom deserialization
                return deserializeFromCustomFormat(message.getBody());
            }
        };
    }

    @Bean
    public RabbitTemplate customRabbitTemplate(ConnectionFactory connectionFactory,
                                               MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
```

#### @RabbitListener аннотации
```java
@Service
public class AdvancedListeners {

    // Basic listener
    @RabbitListener(queues = "basic-queue")
    public void basicListener(String message) {
        System.out.println("Basic: " + message);
    }

    // Listener with headers
    @RabbitListener(queues = "header-queue")
    public void headerListener(String message, @Header("priority") String priority) {
        System.out.println("Priority: " + priority + ", Message: " + message);
    }

    // Listener with message object
    @RabbitListener(queues = "message-queue")
    public void messageListener(Message message) {
        System.out.println("Full message: " + message);
    }

    // Conditional listener
    @RabbitListener(queues = "conditional-queue",
                   condition = "@environment.getProperty('app.feature.enabled') == 'true'")
    public void conditionalListener(String message) {
        System.out.println("Conditional: " + message);
    }

    // Batch listener
    @RabbitListener(queues = "batch-queue", containerFactory = "batchFactory")
    public void batchListener(List<String> messages) {
        System.out.println("Batch of " + messages.size() + " messages");
    }

    // Listener with custom container factory
    @RabbitListener(queues = "custom-queue", containerFactory = "customFactory")
    public void customListener(String message) {
        System.out.println("Custom: " + message);
    }
}
```

#### RabbitMQ Health Checks
```java
@Component
public class RabbitMQHealthIndicator implements HealthIndicator {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQHealthIndicator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Health health() {
        try {
            // Send test message to verify connection
            rabbitTemplate.convertAndSend("health-check", "ping");

            // Try to consume from a test queue
            Message message = rabbitTemplate.receive("health-check", 1000);
            if (message != null) {
                return Health.up()
                    .withDetail("connection", "ok")
                    .withDetail("messageFlow", "ok")
                    .build();
            } else {
                return Health.down()
                    .withDetail("connection", "ok")
                    .withDetail("messageFlow", "failed - no response")
                    .build();
            }

        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## Routing и Exchanges

### Exchange Types

#### Direct Exchange
```java
@Configuration
public class DirectExchangeConfig {

    @Bean
    public DirectExchange orderDirectExchange() {
        return new DirectExchange("order.direct");
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable("order-created").build();
    }

    @Bean
    public Queue orderUpdatedQueue() {
        return QueueBuilder.durable("order-updated").build();
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue())
            .to(orderDirectExchange())
            .with("order.created");
    }

    @Bean
    public Binding orderUpdatedBinding() {
        return BindingBuilder.bind(orderUpdatedQueue())
            .to(orderDirectExchange())
            .with("order.updated");
    }
}

@Service
public class DirectExchangeProducer {

    private final RabbitTemplate rabbitTemplate;

    public DirectExchangeProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderCreated(Order order) {
        rabbitTemplate.convertAndSend("order.direct", "order.created", order);
    }

    public void sendOrderUpdated(Order order) {
        rabbitTemplate.convertAndSend("order.direct", "order.updated", order);
    }
}
```

#### Topic Exchange
```java
@Configuration
public class TopicExchangeConfig {

    @Bean
    public TopicExchange orderTopicExchange() {
        return new TopicExchange("order.topic");
    }

    @Bean
    public Queue usOrdersQueue() {
        return QueueBuilder.durable("us-orders").build();
    }

    @Bean
    public Queue euOrdersQueue() {
        return QueueBuilder.durable("eu-orders").build();
    }

    @Bean
    public Queue urgentOrdersQueue() {
        return QueueBuilder.durable("urgent-orders").build();
    }

    @Bean
    public Binding usOrdersBinding() {
        return BindingBuilder.bind(usOrdersQueue())
            .to(orderTopicExchange())
            .with("order.us.*");
    }

    @Bean
    public Binding euOrdersBinding() {
        return BindingBuilder.bind(euOrdersQueue())
            .to(orderTopicExchange())
            .with("order.eu.*");
    }

    @Bean
    public Binding urgentOrdersBinding() {
        return BindingBuilder.bind(urgentOrdersQueue())
            .to(orderTopicExchange())
            .with("order.*.urgent");
    }
}

@Service
public class TopicExchangeProducer {

    private final RabbitTemplate rabbitTemplate;

    public TopicExchangeProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUsOrder(Order order) {
        rabbitTemplate.convertAndSend("order.topic", "order.us.created", order);
    }

    public void sendEuUrgentOrder(Order order) {
        rabbitTemplate.convertAndSend("order.topic", "order.eu.urgent", order);
    }

    public void sendGlobalOrder(Order order) {
        rabbitTemplate.convertAndSend("order.topic", "order.global.created", order);
    }
}
```

#### Headers Exchange
```java
@Configuration
public class HeadersExchangeConfig {

    @Bean
    public HeadersExchange orderHeadersExchange() {
        return new HeadersExchange("order.headers");
    }

    @Bean
    public Queue highPriorityUsQueue() {
        return QueueBuilder.durable("high-priority-us").build();
    }

    @Bean
    public Queue urgentQueue() {
        return QueueBuilder.durable("urgent-orders").build();
    }

    @Bean
    public Binding highPriorityUsBinding() {
        return BindingBuilder.bind(highPriorityUsQueue())
            .to(orderHeadersExchange())
            .whereAll("region", "us", "priority", "high").exist();
    }

    @Bean
    public Binding urgentBinding() {
        return BindingBuilder.bind(urgentQueue())
            .to(orderHeadersExchange())
            .where("type").matches("urgent");
    }
}

@Service
public class HeadersExchangeProducer {

    private final RabbitTemplate rabbitTemplate;

    public HeadersExchangeProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendHighPriorityUsOrder(Order order) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader("region", "us");
        properties.setHeader("priority", "high");
        properties.setHeader("type", "normal");

        Message message = MessageBuilder
            .withBody(serializeOrder(order))
            .andProperties(properties)
            .build();

        rabbitTemplate.send("order.headers", "", message);
    }

    public void sendUrgentOrder(Order order) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader("region", "eu");
        properties.setHeader("priority", "normal");
        properties.setHeader("type", "urgent");

        Message message = MessageBuilder
            .withBody(serializeOrder(order))
            .andProperties(properties)
            .build();

        rabbitTemplate.send("order.headers", "", message);
    }
}
```

#### Fanout Exchange
```java
@Configuration
public class FanoutExchangeConfig {

    @Bean
    public FanoutExchange notificationFanoutExchange() {
        return new FanoutExchange("notification.fanout");
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable("email-notifications").build();
    }

    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable("sms-notifications").build();
    }

    @Bean
    public Queue pushQueue() {
        return QueueBuilder.durable("push-notifications").build();
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue()).to(notificationFanoutExchange());
    }

    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue()).to(notificationFanoutExchange());
    }

    @Bean
    public Binding pushBinding() {
        return BindingBuilder.bind(pushQueue()).to(notificationFanoutExchange());
    }
}

@Service
public class FanoutExchangeProducer {

    private final RabbitTemplate rabbitTemplate;

    public FanoutExchangeProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(Notification notification) {
        // Fanout exchange ignores routing key
        // Message goes to all bound queues
        rabbitTemplate.convertAndSend("notification.fanout", "", notification);
    }
}

@Service
public class NotificationConsumers {

    @RabbitListener(queues = "email-notifications")
    public void sendEmail(Notification notification) {
        System.out.println("Sending email: " + notification.getMessage());
    }

    @RabbitListener(queues = "sms-notifications")
    public void sendSms(Notification notification) {
        System.out.println("Sending SMS: " + notification.getMessage());
    }

    @RabbitListener(queues = "push-notifications")
    public void sendPush(Notification notification) {
        System.out.println("Sending push notification: " + notification.getMessage());
    }
}
```

## Безопасность

### SSL/TLS

#### SSL конфигурация
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5671  # SSL port
    ssl:
      enabled: true
      algorithm: TLSv1.2
      key-store: classpath:rabbit-keystore.jks
      key-store-password: keystore_password
      trust-store: classpath:rabbit-truststore.jks
      trust-store-password: truststore_password
      validate-server-certificate: true
      verify-hostname: true
```

#### Java SSL setup
```java
@Configuration
public class SSLRabbitMQConfig {

    @Bean
    public ConnectionFactory sslConnectionFactory() throws Exception {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5671); // SSL port

        // SSL configuration
        connectionFactory.getRabbitConnectionFactory().useSslProtocol();

        // Custom SSL context
        SSLContext sslContext = SSLContexts.custom()
            .loadKeyMaterial(loadKeyStore(), "keystore_password".toCharArray())
            .loadTrustMaterial(loadTrustStore(), null)
            .build();

        connectionFactory.getRabbitConnectionFactory().setSocketConfigurator(
            (socket) -> {
                ((SSLSocket) socket).setSSLParameters(createSSLParameters());
            });

        return connectionFactory;
    }

    private KeyStore loadKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        try (InputStream is = getClass().getResourceAsStream("/rabbit-keystore.jks")) {
            keyStore.load(is, "keystore_password".toCharArray());
        }
        return keyStore;
    }

    private KeyStore loadTrustStore() throws Exception {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream is = getClass().getResourceAsStream("/rabbit-truststore.jks")) {
            trustStore.load(is, "truststore_password".toCharArray());
        }
        return trustStore;
    }

    private SSLParameters createSSLParameters() {
        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
        sslParameters.setCipherSuites(new String[]{
            "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
            "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"
        });
        return sslParameters;
    }
}
```

### Authentication

#### RabbitMQ authentication
```ini
# rabbitmq.conf
auth_backends.1 = rabbit_auth_backend_internal
auth_backends.2 = rabbit_auth_backend_ldap

# LDAP configuration
auth_ldap.servers.1 = ldap.example.com
auth_ldap.port = 389
auth_ldap.user_dn_pattern = cn=${username},ou=users,dc=example,dc=com
auth_ldap.use_ssl = false
```

#### Java authentication
```java
@Configuration
public class AuthRabbitMQConfig {

    @Bean
    public ConnectionFactory authConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);

        // Basic authentication
        connectionFactory.setUsername("app-user");
        connectionFactory.setPassword("app-password");

        // Virtual host
        connectionFactory.setVirtualHost("/app-vhost");

        return connectionFactory;
    }
}
```

### Authorization

#### Permissions
```bash
# Create virtual host
rabbitmqctl add_vhost /app-vhost

# Create user
rabbitmqctl add_user app-user app-password

# Set permissions (configure, write, read)
rabbitmqctl set_permissions -p /app-vhost app-user ".*" ".*" ".*"

# Set topic permissions for topic exchange authorization
rabbitmqctl set_topic_permissions -p /app-vhost app-user "order.*" "order.*" "order.*"
```

#### Policies
```bash
# Create policy for high availability
rabbitmqctl set_policy ha-all ".*" '{"ha-mode":"all","ha-sync-mode":"automatic"}'

# Create policy for message TTL
rabbitmqctl set_policy ttl-1h ".*" '{"message-ttl":3600000}'

# Create policy for max length
rabbitmqctl set_policy max-len-1000 ".*" '{"max-length":1000}'
```

## Кластеризация

### Cluster setup

#### Manual cluster setup
```bash
# On first node (rabbitmq1)
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl start_app

# On second node (rabbitmq2)
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster rabbit@rabbitmq1
rabbitmqctl start_app

# On third node (rabbitmq3)
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster rabbit@rabbitmq1
rabbitmqctl start_app

# Check cluster status
rabbitmqctl cluster_status
```

#### Docker cluster
```yaml
version: '3.8'
services:
  rabbitmq1:
    image: rabbitmq:3.12-management-alpine
    hostname: rabbitmq1
    environment:
      RABBITMQ_ERLANG_COOKIE: rabbitmq_cookie
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    ports:
      - "15672:15672"
      - "5672:5672"
    volumes:
      - rabbitmq1_data:/var/lib/rabbitmq

  rabbitmq2:
    image: rabbitmq:3.12-management-alpine
    hostname: rabbitmq2
    depends_on:
      - rabbitmq1
    environment:
      RABBITMQ_ERLANG_COOKIE: rabbitmq_cookie
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    ports:
      - "15673:15672"
      - "5673:5672"
    volumes:
      - rabbitmq2_data:/var/lib/rabbitmq

  rabbitmq3:
    image: rabbitmq:3.12-management-alpine
    hostname: rabbitmq3
    depends_on:
      - rabbitmq1
      - rabbitmq2
    environment:
      RABBITMQ_ERLANG_COOKIE: rabbitmq_cookie
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    ports:
      - "15674:15672"
      - "5674:5672"
    volumes:
      - rabbitmq3_data:/var/lib/rabbitmq
```

### Load balancing

#### HAProxy configuration
```haproxy
frontend rabbitmq_front
    bind *:5672
    default_backend rabbitmq_back

backend rabbitmq_back
    balance roundrobin
    server rabbit1 rabbitmq1:5672 check
    server rabbit2 rabbitmq2:5672 check
    server rabbit3 rabbitmq3:5672 check
```

#### Java client configuration
```java
@Configuration
public class LoadBalancedConfig {

    @Bean
    public ConnectionFactory loadBalancedConnectionFactory() {
        List<Address> addresses = Arrays.asList(
            new Address("rabbitmq1", 5672),
            new Address("rabbitmq2", 5672),
            new Address("rabbitmq3", 5672)
        );

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses);

        // Connection recovery
        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(10000);

        return connectionFactory;
    }
}
```

### Federation

#### Federation setup
```bash
# Enable federation plugin
rabbitmq-plugins enable rabbitmq_federation
rabbitmq-plugins enable rabbitmq_federation_management

# Create upstream
rabbitmqctl set_parameter federation-upstream remote-cluster \
    '{"uri":"amqp://admin:admin123@remote-host:5672","expires":3600000}'

# Create policy for federation
rabbitmqctl set_policy federation-policy "federated.*" \
    '{"federation-upstream-set":"all"}' \
    --apply-to exchanges
```

#### Federation configuration
```java
@Configuration
public class FederationConfig {

    @Bean
    public Exchange federatedExchange() {
        return ExchangeBuilder.directExchange("federated.orders")
            .durable(true)
            .build();
    }

    @Bean
    public Queue localQueue() {
        return QueueBuilder.durable("local-orders").build();
    }

    @Bean
    public Binding localBinding() {
        return BindingBuilder.bind(localQueue())
            .to(federatedExchange())
            .with("order.created");
    }
}
```

## Мониторинг и управление

### Management Plugin

#### Web `UI`
```bash
# Enable management plugin
rabbitmq-plugins enable rabbitmq_management

# Access at http://localhost:15672
# Default credentials: guest/guest
```

#### REST API
```bash
# Get overview
curl -u admin:admin123 http://localhost:15672/api/overview

# Get queues
curl -u admin:admin123 http://localhost:15672/api/queues

# Get connections
curl -u admin:admin123 http://localhost:15672/api/connections

# Get exchanges
curl -u admin:admin123 http://localhost:15672/api/exchanges
```

### Metrics

#### Prometheus metrics
```bash
# Enable Prometheus plugin
rabbitmq-plugins enable rabbitmq_prometheus

# Metrics available at http://localhost:15692/metrics
```

#### Micrometer integration
```java
@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public RabbitTemplate monitoredRabbitTemplate(ConnectionFactory connectionFactory,
                                                   MeterRegistry meterRegistry) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        // Add custom metrics
        template.setBeforePublishPostProcessors(message -> {
            Counter.builder("rabbitmq.messages.sent")
                .tag("exchange", message.getMessageProperties().getReceivedExchange())
                .register(meterRegistry)
                .increment();
        });

        return template;
    }
}

@Service
public class RabbitMQMetricsService {

    private final MeterRegistry meterRegistry;

    public RabbitMQMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordMessageProcessed(String queueName, long processingTime) {
        Timer.builder("rabbitmq.message.processing.time")
            .tag("queue", queueName)
            .register(meterRegistry)
            .record(processingTime, TimeUnit.MILLISECONDS);
    }

    public void recordMessageFailed(String queueName, String errorType) {
        Counter.builder("rabbitmq.messages.failed")
            .tag("queue", queueName)
            .tag("error_type", errorType)
            .register(meterRegistry)
            .increment();
    }

    public void recordQueueSize(String queueName, int size) {
        Gauge.builder("rabbitmq.queue.size", () -> size)
            .tag("queue", queueName)
            .register(meterRegistry);
    }
}
```

### Health Checks

#### Spring Boot health check
```java
@Component
public class RabbitMQHealthIndicator implements HealthIndicator {

    private final RabbitTemplate rabbitTemplate;
    private final String healthCheckQueue = "health-check-queue";

    public RabbitMQHealthIndicator(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Health health() {
        try {
            // Send test message
            String testMessage = "health-check-" + System.currentTimeMillis();
            rabbitTemplate.convertAndSend("", healthCheckQueue, testMessage);

            // Try to receive it back
            Message received = rabbitTemplate.receive(healthCheckQueue, 2000);

            if (received != null && testMessage.equals(new String(received.getBody()))) {
                return Health.up()
                    .withDetail("connection", "ok")
                    .withDetail("messageFlow", "ok")
                    .build();
            } else {
                return Health.down()
                    .withDetail("connection", "ok")
                    .withDetail("messageFlow", "failed")
                    .build();
            }

        } catch (Exception e) {
            return Health.down(e)
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## Производительность и оптимизация

### Connection pooling

#### Connection factory optimization
```java
@Configuration
public class OptimizedConnectionConfig {

    @Bean
    public ConnectionFactory optimizedConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin123");

        // Connection pooling
        connectionFactory.setChannelCacheSize(25);
        connectionFactory.setConnectionCacheSize(10);

        // Connection limits
        connectionFactory.setConnectionLimit(50);

        // Connection timeout
        connectionFactory.setConnectionTimeout(60000);

        // Publisher confirms
        connectionFactory.getRabbitConnectionFactory().setRequestedHeartbeat(60);
        connectionFactory.getRabbitConnectionFactory().setConnectionTimeout(60000);

        return connectionFactory;
    }
}
```

### Message optimization

#### Message size optimization
```java
@Service
public class MessageOptimizer {

    private final ObjectMapper objectMapper;

    public MessageOptimizer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Message createOptimizedMessage(Object payload) throws JsonProcessingException {
        // Use efficient serialization
        byte[] body = objectMapper.writeValueAsBytes(payload);

        // Compress large messages
        if (body.length > 1024) {
            body = compress(body);
        }

        MessageProperties properties = new MessageProperties();
        properties.setContentType("application/json");
        properties.setContentEncoding(body.length > 1024 ? "gzip" : "identity");
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        properties.setPriority(0);

        return new Message(body, properties);
    }

    public <T> T extractOptimizedMessage(Message message, Class<T> type) throws IOException {
        byte[] body = message.getBody();
        String encoding = message.getMessageProperties().getContentEncoding();

        // Decompress if needed
        if ("gzip".equals(encoding)) {
            body = decompress(body);
        }

        return objectMapper.readValue(body, type);
    }

    private byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(output)) {
            gzip.write(data);
        }
        return output.toByteArray();
    }

    private byte[] decompress(byte[] data) throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        try (GZIPInputStream gzip = new GZIPInputStream(input);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzip.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
            return output.toByteArray();
        }
    }
}
```

### Consumer optimization

#### Prefetch settings
```java
@Configuration
public class ConsumerOptimizationConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory optimizedListenerContainerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // Concurrency settings
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(20);

        // Prefetch optimization
        factory.setPrefetchCount(5); // Process 5 messages at a time per consumer

        // Batch settings for efficiency
        factory.setBatchListener(true);
        factory.setBatchSize(10);
        factory.setConsumerBatchEnabled(true);

        // Manual acknowledgment for better control
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}

@Service
public class OptimizedConsumer {

    @RabbitListener(queues = "optimized-queue", containerFactory = "optimizedListenerContainerFactory")
    public void handleMessages(List<Message> messages, Channel channel) throws IOException {
        // Process batch efficiently
        List<Long> deliveryTags = new ArrayList<>();

        try {
            for (Message message : messages) {
                deliveryTags.add(message.getMessageProperties().getDeliveryTag());
                processMessage(message);
            }

            // Acknowledge all at once
            if (!deliveryTags.isEmpty()) {
                channel.basicAck(deliveryTags.get(deliveryTags.size() - 1), true);
            }

        } catch (Exception e) {
            // Reject all messages in batch
            for (Long deliveryTag : deliveryTags) {
                channel.basicReject(deliveryTag, false);
            }
        }
    }

    private void processMessage(Message message) {
        // Processing logic
    }
}
```

### Queue optimization

#### Queue settings
```java
@Configuration
public class QueueOptimizationConfig {

    @Bean
    public Queue optimizedQueue() {
        return QueueBuilder.durable("optimized-queue")
            // Message TTL
            .withArgument("x-message-ttl", 3600000) // 1 hour

            // Queue length limit
            .withArgument("x-max-length", 10000)
            .withArgument("x-overflow", "reject-publish")

            // Dead letter exchange
            .withArgument("x-dead-letter-exchange", "dlx")
            .withArgument("x-dead-letter-routing-key", "failed")

            // Priority support
            .withArgument("x-max-priority", 10)

            // Lazy queue for memory efficiency
            .withArgument("x-queue-mode", "lazy")

            .build();
    }

    @Bean
    public Queue lazyQueue() {
        // Lazy queues keep messages on disk to reduce memory usage
        return QueueBuilder.durable("lazy-queue")
            .withArgument("x-queue-mode", "lazy")
            .build();
    }

    @Bean
    public Queue quorumQueue() {
        // Quorum queues for high availability
        return QueueBuilder.durable("quorum-queue")
            .withArgument("x-queue-type", "quorum")
            .build();
    }
}
```

## Тестирование

### Модульное тестирование

#### Producer testing
```java
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ProducerServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderProducer orderProducer;

    @Test
    void testSendOrderCreated() {
        // Given
        Order order = createTestOrder();
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), any(), any());

        // When
        orderProducer.sendOrderCreated(order);

        // Then
        verify(rabbitTemplate).convertAndSend("order.direct", "order.created", order);
    }

    @Test
    void testSendOrderWithConfirm() {
        // Given
        Order order = createTestOrder();
        SendResult<String, Order> sendResult = mock(SendResult.class);
        when(rabbitTemplate.sendAndReceive(anyString(), anyString(), any(Message.class)))
            .thenReturn(sendResult);

        // When
        SendResult<String, Order> result = orderProducer.sendOrderWithConfirm(order);

        // Then
        assertThat(result).isEqualTo(sendResult);
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setCustomerId(123L);
        order.setAmount(BigDecimal.valueOf(99.99));
        return order;
    }
}
```

#### Consumer testing
```java
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ConsumerServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Channel channel;

    @InjectMocks
    private OrderConsumer orderConsumer;

    @Test
    void testHandleOrderCreatedSuccess() throws IOException {
        // Given
        Order order = createTestOrder();
        ConsumerRecord<String, Order> record = new ConsumerRecord<>(
            "order-created", 0, 123L, "order-key", order);

        // When
        orderConsumer.handleOrderCreated(order, record, channel);

        // Then
        verify(orderService).processOrder(order);
        verify(channel).basicAck(123L, false);
    }

    @Test
    void testHandleOrderCreatedFailure() throws IOException {
        // Given
        Order order = createTestOrder();
        ConsumerRecord<String, Order> record = new ConsumerRecord<>(
            "order-created", 0, 123L, "order-key", order);

        doThrow(new RuntimeException("Processing failed"))
            .when(orderService).processOrder(order);

        // When
        orderConsumer.handleOrderCreated(order, record, channel);

        // Then
        verify(orderService).processOrder(order);
        verify(channel).basicReject(123L, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}
```

### Интеграционное тестирование

#### Embedded RabbitMQ
```java
@SpringBootTest
@Testcontainers
public class RabbitMQIntegrationTest {

    @Container
    private static final RabbitMQContainer rabbitMQ = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.12-management-alpine"))
        .withExposedPorts(5672, 15672);

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderProducer orderProducer;

    @Test
    void testSendAndReceiveOrder() {
        // Given
        Order order = createTestOrder();

        // When
        orderProducer.sendOrderCreated(order);

        // Then
        Message received = rabbitTemplate.receive("order-created", 5000);
        assertThat(received).isNotNull();

        Order receivedOrder = (Order) rabbitTemplate.getMessageConverter()
            .fromMessage(received);
        assertThat(receivedOrder.getId()).isEqualTo(order.getId());
    }

    @Test
    void testMessagePersistence() {
        // Given
        Order order = createTestOrder();

        // When
        orderProducer.sendOrderCreated(order);

        // Simulate broker restart by checking message survives
        // In real scenario, you might restart the container

        // Then
        Message received = rabbitTemplate.receive("order-created", 5000);
        assertThat(received).isNotNull();
    }
}
```

#### E2E Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testCompleteOrderFlow() {
        // 1. Create order via REST API
        OrderRequest request = new OrderRequest();
        request.setCustomerId(123L);
        request.setAmount(BigDecimal.valueOf(99.99));

        ResponseEntity<Order> createResponse = restTemplate.postForEntity(
            "/api/orders", request, Order.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Order createdOrder = createResponse.getBody();

        // 2. Verify message was sent to RabbitMQ
        Message message = rabbitTemplate.receive("order-created", 10000);
        assertThat(message).isNotNull();

        // 3. Simulate consumer processing
        // In real scenario, consumer would be running
        // Here we manually process the message
        Order messageOrder = (Order) rabbitTemplate.getMessageConverter()
            .fromMessage(message);
        assertThat(messageOrder.getId()).isEqualTo(createdOrder.getId());

        // 4. Verify order was processed
        await().atMost(10, TimeUnit.SECONDS)
            .until(() -> orderRepository.findById(createdOrder.getId()).get().isProcessed());
    }
}
```

## Решение проблем

### Распространенные проблемы

#### Connection issues

**Проблема: `Connection refused`**
```text
java.net.ConnectException: Connection refused
```

**Решение:**
```java
// Check RabbitMQ is running
rabbitmqctl status

// Check firewall settings
sudo ufw allow 5672

// Check connection parameters
@Configuration
public class ConnectionDebugConfig {

    @Bean
    public ConnectionFactory debugConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin123");

        // Debug logging
        factory.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(true);
        factory.getRabbitConnectionFactory().setNetworkRecoveryInterval(10000);

        return factory;
    }
}
```

**Проблема: `Authentication failed`**
```text
com.rabbitmq.client.AuthenticationFailureException: ACCESS_REFUSED
```

**Решение:**
```bash
# Create user
rabbitmqctl add_user testuser testpass

# Set permissions
rabbitmqctl set_permissions -p / testuser ".*" ".*" ".*"

# Check user exists
rabbitmqctl list_users
```

#### Message delivery issues

**Проблема: `Messages not being consumed`**
```text
Messages published but not consumed
```

**Решение:**
```java
// Check queue exists
rabbitmqctl list_queues

// Check bindings
rabbitmqctl list_bindings

// Debug consumer
@Service
public class DebugConsumer {

    @RabbitListener(queues = "debug-queue")
    public void debugHandler(String message) {
        System.out.println("DEBUG: Received message: " + message);
    }
}
```

**Проблема: `High unacknowledged messages`**
```text
Unacked messages growing
```

**Решение:**
```java
// Check consumer health
@Configuration
public class ConsumerHealthConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory healthCheckFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // Monitor consumer health
        factory.getContainerProperties().setConsumerStartTimeout(30000);
        factory.getContainerProperties().setConsumerStopTimeout(30000);

        return factory;
    }
}
```

#### Performance issues

**Проблема: `High latency`**
```text
Message processing taking too long
```

**Решение:**
```java
@Configuration
public class PerformanceOptimizationConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory performanceFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // Optimize for low latency
        factory.setPrefetchCount(1);
        factory.setConcurrentConsumers(10);
        factory.setMaxConcurrentConsumers(50);

        // Use batch processing for high throughput
        factory.setBatchListener(true);
        factory.setBatchSize(50);
        factory.setConsumerBatchEnabled(true);

        return factory;
    }
}
```

### Инструменты отладки

#### RabbitMQ tracing
```bash
# Enable firehose tracer
rabbitmqctl trace_on

# Create traced queue
rabbitmqctl add_user tracer tracer
rabbitmqctl set_permissions -p / tracer ".*" ".*" ".*"

# Messages will be published to amq.rabbitmq.trace
```

#### Java debug logging
```xml
<!-- logback.xml -->
<logger name="com.rabbitmq" level="DEBUG"/>
<logger name="org.springframework.amqp" level="DEBUG"/>
<logger name="org.springframework.rabbit" level="DEBUG"/>
```

#### Connection monitoring
```java
@Configuration
public class ConnectionMonitorConfig {

    @Bean
    public ConnectionFactory monitoredConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);

        // Add connection listener
        factory.getRabbitConnectionFactory().addConnectionListener(new ConnectionListener() {
            @Override
            public void onConnection(Connection connection) {
                System.out.println("Connected to RabbitMQ");
            }

            @Override
            public void onConnectionFailed(Connection connection, Throwable throwable) {
                System.err.println("Connection failed: " + throwable.getMessage());
            }

            @Override
            public void onConnectionRecovery(Connection connection) {
                System.out.println("Connection recovered");
            }
        });

        return factory;
    }
}
```

## Лучшие практики

### Архитектурные рекомендации

#### 1. Message Design
```java
// Good message structure
public class OrderEvent {
    private String eventId;
    private String eventType; // "ORDER_CREATED", "ORDER_UPDATED", etc.
    private Instant timestamp;
    private String source;    // Service that generated the event
    private OrderData data;   // Event payload
    private Map<String, Object> metadata; // Additional context

    // Version for schema evolution
    private String version = "1.0";
}

public class OrderData {
    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private String currency;
    private List<OrderItem> items;
    private Address shippingAddress;
}

// Avoid large messages
public class OrderSummaryEvent {
    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private String status;
    // Include only essential data
}
```

#### 2. Error Handling Strategy
```java
@Service
public class ErrorHandlingConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final MetricsService metricsService;

    @RabbitListener(queues = "order-events", containerFactory = "errorHandlingFactory")
    public void handleOrderEvent(OrderEvent event, Message message, Channel channel) throws IOException {
        try {
            processOrderEvent(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            metricsService.recordSuccess("order-event-processing");

        } catch (ValidationException e) {
            // Business validation error - reject permanently
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            metricsService.recordError("order-event-processing", "validation");

        } catch (TemporaryException e) {
            // Temporary error - retry with backoff
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            metricsService.recordError("order-event-processing", "temporary");

        } catch (Exception e) {
            // Unexpected error - send to dead letter queue
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            metricsService.recordError("order-event-processing", "unexpected");

            // Log for investigation
            logger.error("Unexpected error processing order event: " + event, e);
        }
    }

    private void processOrderEvent(OrderEvent event) {
        // Processing logic
    }
}
```

#### 3. Idempotency
```java
@Service
public class IdempotentConsumer {

    private final RedisTemplate<String, String> redisTemplate;
    private final OrderService orderService;

    @RabbitListener(queues = "order-events")
    public void handleOrderEvent(OrderEvent event, Message message, Channel channel) throws IOException {
        String eventId = event.getEventId();
        String processedKey = "processed:" + eventId;

        // Check if already processed
        Boolean alreadyProcessed = redisTemplate.hasKey(processedKey);

        if (Boolean.TRUE.equals(alreadyProcessed)) {
            // Duplicate message - acknowledge and skip
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            logger.info("Skipping duplicate event: {}", eventId);
            return;
        }

        try {
            processOrderEvent(event);

            // Mark as processed
            redisTemplate.opsForValue().set(processedKey, "true",
                Duration.ofHours(24)); // Keep for 24 hours

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            // Don't mark as processed on failure - will retry
            logger.error("Failed to process event: {}", eventId, e);
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    private void processOrderEvent(OrderEvent event) {
        // Processing logic
    }
}
```

### Производительность

#### 4. Connection Management
```java
@Configuration
public class ConnectionManagementConfig {

    @Bean
    public ConnectionFactory pooledConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("app-user");
        factory.setPassword("app-password");

        // Connection pooling
        factory.setChannelCacheSize(50);      // Channels per connection
        factory.setConnectionCacheSize(10);   // Total connections
        factory.setConnectionLimit(20);       // Max connections

        // Connection recovery
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(10000);
        factory.setTopologyRecoveryEnabled(true);

        // Timeouts
        factory.setConnectionTimeout(60000);
        factory.setRequestedHeartBeat(60);

        return factory;
    }
}
```

#### 5. Message Routing Patterns
```java
// Content-based routing
@Configuration
public class ContentBasedRouter {

    private final RabbitTemplate rabbitTemplate;

    public void routeOrder(Order order) {
        String routingKey;

        if (order.getAmount().compareTo(BigDecimal.valueOf(1000)) > 0) {
            routingKey = "order.high-value";
        } else if (order.getPriority() == OrderPriority.URGENT) {
            routingKey = "order.urgent";
        } else if ("US".equals(order.getRegion())) {
            routingKey = "order.us";
        } else {
            routingKey = "order.standard";
        }

        rabbitTemplate.convertAndSend("order.content-based", routingKey, order);
    }
}

// Recipient list pattern
@Service
public class RecipientListRouter {

    private final RabbitTemplate rabbitTemplate;

    public void routeNotification(Notification notification) {
        // Send to multiple services based on notification type
        List<String> targets = determineTargets(notification);

        for (String target : targets) {
            rabbitTemplate.convertAndSend("notification.fanout", target, notification);
        }
    }

    private List<String> determineTargets(Notification notification) {
        switch (notification.getType()) {
            case ORDER_STATUS:
                return Arrays.asList("email-service", "sms-service", "push-service");
            case PAYMENT_FAILED:
                return Arrays.asList("email-service", "sms-service");
            case ACCOUNT_CREATED:
                return Arrays.asList("email-service", "push-service");
            default:
                return Arrays.asList("email-service");
        }
    }
}
```

### Мониторинг

#### 6. Comprehensive Monitoring
```java
@Configuration
public class MonitoringConfig {

    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public RabbitTemplate monitoredRabbitTemplate(ConnectionFactory connectionFactory,
                                                   MeterRegistry meterRegistry) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        // Message sent metrics
        template.setBeforePublishPostProcessors(message -> {
            Counter.builder("rabbitmq.messages.sent")
                .tag("exchange", message.getMessageProperties().getReceivedExchange())
                .tag("routing_key", message.getMessageProperties().getReceivedRoutingKey())
                .register(meterRegistry)
                .increment();
        });

        // Confirm callback metrics
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (correlationData != null) {
                String status = ack ? "confirmed" : "rejected";
                Counter.builder("rabbitmq.messages.confirm")
                    .tag("status", status)
                    .register(meterRegistry)
                    .increment();
            }
        });

        return template;
    }
}

@Service
public class ConsumerMetricsService {

    private final MeterRegistry meterRegistry;

    @RabbitListener(queues = "order-events")
    public void handleWithMetrics(OrderEvent event, Message message, Channel channel) throws IOException {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            processOrderEvent(event);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

            sample.stop(Timer.builder("rabbitmq.consumer.processing")
                .tag("queue", "order-events")
                .tag("status", "success")
                .register(meterRegistry));

        } catch (Exception e) {
            sample.stop(Timer.builder("rabbitmq.consumer.processing")
                .tag("queue", "order-events")
                .tag("status", "error")
                .register(meterRegistry));

            Counter.builder("rabbitmq.consumer.errors")
                .tag("queue", "order-events")
                .tag("error_type", e.getClass().getSimpleName())
                .register(meterRegistry)
                .increment();

            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    private void processOrderEvent(OrderEvent event) {
        // Processing logic
    }
}
```

#### 7. Alerting Rules
```yaml
# Prometheus alerting rules for RabbitMQ
groups:
  - name: rabbitmq
    rules:
      - alert: RabbitMQDown
        expr: up{job="rabbitmq"} == 0
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "RabbitMQ is down"

      - alert: RabbitMQHighConnectionCount
        expr: rabbitmq_connectionsTotal > 1000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High connection count"

      - alert: RabbitMQHighQueueLength
        expr: rabbitmq_queue_messages_ready{queue="order-events"} > 10000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Queue length is too high"

      - alert: RabbitMQConsumerLag
        expr: rate(rabbitmq_queue_messages_published_total[5m]) > rate(rabbitmq_queue_messages_delivered_total[5m]) * 2
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "Consumer lag detected"
```

### Безопасность

#### 8. Лучшие практики безопасности
```java
@Configuration
public class SecurityConfig {

    @Bean
    public ConnectionFactory secureConnectionFactory() throws Exception {
        CachingConnectionFactory factory = new CachingConnectionFactory();

        // SSL configuration
        factory.getRabbitConnectionFactory().useSslProtocol();
        factory.getRabbitConnectionFactory().enableHostnameVerification();

        // Custom SSL context
        SSLContext sslContext = createSSLContext();
        factory.getRabbitConnectionFactory().setSocketFactory(sslContext.getSocketFactory());

        // Authentication
        factory.setUsername("app-user");
        factory.setPassword("app-password");

        // Virtual host isolation
        factory.setVirtualHost("/app-vhost");

        return factory;
    }

    private SSLContext createSSLContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream is = getClass().getResourceAsStream("/rabbit-client.p12")) {
            keyStore.load(is, "keystore_password".toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "key_password".toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = loadTrustStore();
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return sslContext;
    }
}
```

### Масштабирование

#### 9. Horizontal Scaling
```java
@Configuration
public class ScalingConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory scaledListenerFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // Scale consumers based on queue load
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(20);

        // Consumer scaling policy
        factory.getContainerProperties().setConsumerScalingPolicy(
            new SimpleConsumerScalingPolicy(10, 100, 60)); // min, max, scale up delay

        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory batchScaledFactory(
            ConnectionFactory connectionFactory) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // Batch processing for high throughput
        factory.setBatchListener(true);
        factory.setBatchSize(50);
        factory.setConsumerBatchEnabled(true);

        // Scale based on batch processing time
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(50);

        return factory;
    }
}
```

#### 10. Cluster Management
```java
@Service
public class ClusterManagementService {

    private final AdminClient adminClient;

    public ClusterManagementService(@Value("${spring.rabbitmq.host}") String host,
                                   @Value("${spring.rabbitmq.username}") String username,
                                   @Value("${spring.rabbitmq.password}") String password) {
        Properties props = new Properties();
        props.put("host", host);
        props.put("username", username);
        props.put("password", password);
        this.adminClient = new AdminClient(props);
    }

    public void createQueue(String queueName, Map<String, Object> arguments) {
        adminClient.declareQueue(new Queue(queueName, true, false, false, arguments));
    }

    public void createExchange(String exchangeName, String type) {
        BuiltinExchangeType exchangeType = BuiltinExchangeType.valueOf(type.toUpperCase());
        adminClient.declareExchange(new Exchange(exchangeName, exchangeType, true));
    }

    public void createBinding(String queue, String exchange, String routingKey) {
        adminClient.declareBinding(new Binding(queue, Binding.DestinationType.QUEUE,
            exchange, routingKey, Map.of()));
    }

    public Map<String, Object> getQueueInfo(String queueName) {
        return adminClient.getQueueInfo(queueName);
    }

    public List<Map<String, Object>> listQueues() {
        return adminClient.listQueues();
    }

    public void deleteQueue(String queueName) {
        adminClient.deleteQueue(queueName);
    }

    @PreDestroy
    public void close() {
        adminClient.close();
    }
}
```


## Заключение

**RabbitMQ** — это мощный и надежный **message broker**, который отлично подходит для построения распределенных систем. Его поддержка **AMQP**, гибкая система **routing**'а и надежность делают его идеальным выбором для **enterprise** приложений.

### Ключевые преимущества RabbitMQ:

1. **Гарантированная доставка** — различные уровни гарантий доставки
2. **Гибкий routing** — 4 типа **exchanges** для различных сценариев
3. **Надежность** — **persistence**, **acknowledgments**, **dead letter queues**
4. **Широкая поддержка** — множество языков и протоколов
5. **Управление** — отличные инструменты мониторинга и управления
6. **Кластеризация** — высокая доступность и масштабируемость
7. **Плагины** — расширяемая функциональность

### Архитектурные паттерны:

#### Work Queues (Task Distribution)
```text
Producer → Queue ← Worker 1
                ← Worker 2
                ← Worker 3
```

#### Publish-Subscribe
```text
Producer → Exchange → Queue 1 → Consumer 1
                → Queue 2 → Consumer 2
                → Queue 3 → Consumer 3
```

#### Routing
```text
Producer → Exchange → Queue (routing key match)
                → Queue (routing key match)
                → Queue (no match - discarded)
```

#### RPC (Request-Response)
```mermaid
sequenceDiagram
    participant Client
    participant CQ as Callback Queue
    participant Server
    Client->>Server: Request
    Server->>CQ: Response
    CQ->>Client: Response
```

### Когда выбирать RabbitMQ:

**Требуется гарантированная доставка** — **at-least-once** или **exactly-once**
**Complex routing logic** — **topic**, **headers exchanges**
**Request-response pattern** — **RPC** через **messaging**
**Legacy system integration** — поддержка различных протоколов
**Enterprise requirements** — надежность, мониторинг, управление
**Clustering needs** — высокая доступность
**Message persistence** — сохранение сообщений при сбоях

### Когда НЕ выбирать:

**High-throughput streaming** — используйте **Kafka** (>100K msg/sec)
**Big data processing** — **Kafka** лучше для больших объемов
**Simple `fire-and`-forget** — **Kafka** или **Redis pub**/**sub** проще
**Real-time analytics** — **Kafka Streams** или **Apache Flink**
**Large message payloads** — накладные расходы на **routing**

### Рекомендации по внедрению:

1. **Выберите правильный exchange type** — **direct** для точной маршрутизации, **topic** для паттернов
2. **Используйте persistent queues** — для важных сообщений
3. **Настройте dead letter queues** — для обработки ошибок
4. **Мониторьте queue lengths** — алерты на переполнение
5. **Используйте connection pooling** — для высокой производительности
6. **Планируйте кластер** — 3+ узла для высокой доступности
7. **Тестируйте failover** — симулируйте сбои узлов
8. **Документируйте топики** — **routing keys** и их назначение

### Типы Exchanges по назначению:

#### Direct Exchange:
- **Точная маршрутизация** — `user.registration`, `order.created`
- **Service-specific messages** — `email-service`, `sms-service`
- **Command routing** — `user.create`, `order.cancel`

#### Topic Exchange:
- **Иерархическая маршрутизация** — `user.us.registration`, `order.eu.urgent`
- **Multi-level categorization** — `event.`user.login`.success`, `event.`order.payment`.failed`
- **Geographic routing** — `data.`us-east`.insert`, `data.`eu-west`.update`

#### Headers Exchange:
- **Complex conditions** — **region**=us **AND priority**=**high**
- **Metadata-based routing** — **content-type**=**json**, **version**=2.0
- **Custom logic** — **application-specific headers**

#### Fanout Exchange:
- **Broadcast notifications** — все сервисы получают копию
- **Cache invalidation** — очистка кешей во всех инстансах
- **System events** — **server restart**, **configuration change**

### Сводка лучших практик

1. **Message Design** — **versioned**, **self-contained**, **immutable messages**
2. **Error Handling** — **dead letter queues**, **retry policies**, **circuit breakers**
3. **Monitoring** — **queue lengths**, **consumer lag**, **connection health**
4. **Security** — **SSL**, **authentication**, **authorization**, **virtual hosts**
5. **Performance** — **connection pooling**, **prefetch**, **batch processing**
6. **Reliability** — **acknowledgments**, **confirmations**, **clustering**
7. **Operations** — **health checks**, **metrics**, **alerting**, **backups**

**RabbitMQ** остается одним из лучших выборов для **message-oriented middleware** в **enterprise** системах. Его зрелость, надежность и богатая экосистема делают его отличным фундаментом для построения масштабируемых и надежных распределенных систем.

**Далее: `Testing frameworks` (JUnit, Mockito, AssertJ)**
