---
title: "Micronaut: JMS Integration - Message Queues и Topics"
description: "Полное руководство по интеграции с JMS в Micronaut: message queues, topics, producers, consumers и best practices"
tags: ["micronaut", "jms", "messaging", "queues", "topics", "java", "kotlin"]
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-reactive.md"]
next: ["micronaut-reactive.md", "micronaut-kafka.md"]
updated: "2026-02-11"
related: ["micronaut-reactive.md", "micronaut-kafka.md"]
---

# Micronaut: JMS Integration - Message Queues и Topics



## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: JMS Integration - Message Queues и Topics](#micronaut-jms-integration-message-queues-и-topics)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка JMS](#настройка-jms)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [JMS Producers](#jms-producers)
  - [Queue Producer](#queue-producer)
  - [Topic Producer](#topic-producer)
- [JMS Consumers](#jms-consumers)
  - [Queue Consumer](#queue-consumer)
  - [Topic Consumer](#topic-consumer)
- [Message Headers](#message-headers)
  - [Custom Headers](#custom-headers)
- [Transactions](#transactions)
  - [Transactional Producer](#transactional-producer)
  - [Transactional Consumer](#transactional-consumer)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте connection pooling](#1-используйте-connection-pooling)
- [✅ Хорошо](#хорошо)
  - [2. Обрабатывайте ошибки](#2-обрабатывайте-ошибки)
  - [3. Используйте transactions для критических операций](#3-используйте-transactions-для-критических-операций)
- [Message Selectors](#message-selectors)
  - [Selector-based Consumption](#selector-based-consumption)
- [Dead Letter Queue](#dead-letter-queue)
  - [DLQ Configuration](#dlq-configuration)
- [Message Persistence](#message-persistence)
  - [Persistent Messages](#persistent-messages)
- [Message Priority](#message-priority)
  - [Priority-based Messaging](#priority-based-messaging)
- [JMS Connection Pooling](#jms-connection-pooling)
  - [Pool Configuration](#pool-configuration)
- [JMS Message Expiration](#jms-message-expiration)
  - [Message TTL](#message-ttl)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** предоставляет поддержку **JMS** (`Java Message Service`) для асинхронной обработки сообщений. Это позволяет создавать приложения с использованием **message queues** и **topics** для надежной доставки сообщений.

### Основные возможности

- **JMS Producers**: Отправка сообщений в очереди и топики
- **JMS Consumers**: Получение сообщений из очередей и топиков
- **Message Queues**: **Point-to-point messaging**
- **Topics**: **Pub**/**sub messaging**
- **Transactions**: Транзакционная обработка сообщений

## Настройка **JMS**

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.jms:micronaut-jms-core")
    implementation("io.micronaut.jms:micronaut-jms-sqs")
    // или для ActiveMQ
    implementation("io.micronaut.jms:micronaut-jms-activemq-classic")
}
```

### Конфигурация

**application.yml:**

```yaml
jms:
  connection-factory:
    url: tcp://localhost:61616
    username: admin
    password: admin
```

## JMS Producers

### Queue Producer

```java
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;
import jakarta.inject.Singleton;

@JMSProducer("connectionFactory")
public interface QueueProducer {
    
    @Queue("user.queue")
    void sendUser(@MessageBody User user);
    
    @Queue("order.queue")
    void sendOrder(@MessageBody Order order);
}
```

### Topic Producer

```java
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Topic;
import io.micronaut.messaging.annotation.MessageBody;

@JMSProducer("connectionFactory")
public interface TopicProducer {
    
    @Topic("user.events")
    void publishUserEvent(@MessageBody UserEvent event);
    
    @Topic("order.events")
    void publishOrderEvent(@MessageBody OrderEvent event);
}
```

## JMS Consumers

### Queue Consumer

```java
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;
import jakarta.inject.Singleton;

@JMSListener("connectionFactory")
public class QueueConsumer {
    
    @Queue("user.queue")
    public void receiveUser(@MessageBody User user) {
        System.out.println("Received user: " + user.getName());
        // Обработка пользователя
    }
    
    @Queue("order.queue")
    public void receiveOrder(@MessageBody Order order) {
        System.out.println("Received order: " + order.getId());
        // Обработка заказа
    }
}
```

### Topic Consumer

```java
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Topic;
import io.micronaut.messaging.annotation.MessageBody;
import jakarta.inject.Singleton;

@JMSListener("connectionFactory")
public class TopicConsumer {
    
    @Topic("user.events")
    public void receiveUserEvent(@MessageBody UserEvent event) {
        System.out.println("Received user event: " + event.getType());
        // Обработка события пользователя
    }
}
```

## Message Headers

### Custom Headers

```java
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;
import io.micronaut.messaging.annotation.MessageHeader;

@JMSProducer("connectionFactory")
public interface HeaderProducer {
    
    @Queue("user.queue")
    void sendUserWithHeaders(
        @MessageBody User user,
        @MessageHeader("X-User-Id") String userId,
        @MessageHeader("X-Priority") Integer priority
    );
}
```

## Transactions

### Transactional Producer

```java
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.transaction.annotation.Transactional;

@JMSProducer("connectionFactory")
public interface TransactionalProducer {
    
    @Queue("user.queue")
    @Transactional
    void sendUser(@MessageBody User user);
}
```

### Transactional Consumer

```java
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;

@JMSListener("connectionFactory")
public class TransactionalConsumer {
    
    @Queue("user.queue")
    @Transactional
    public void receiveUser(@MessageBody User user) {
        userRepository.save(user);
        // Если произойдет ошибка, транзакция откатится
    }
}
```

## Лучшие практики

### 1. Используйте **connection pooling**

```yaml
# ✅ Хорошо
jms:
  connection-factory:
    pool:
      max-connections: 10
```

### 2. Обрабатывайте ошибки

```java
// ✅ Хорошо
@JMSListener("connectionFactory")
public class ErrorHandlingConsumer {
    
    @Queue("user.queue")
    public void receiveUser(@MessageBody User user) {
        try {
            // Обработка пользователя
        } catch (Exception e) {
            log.error("Error processing user", e);
            // Отправка в DLQ
        }
    }
}
```

### 3. Используйте **transactions** для критических операций

```java
// ✅ Хорошо
@Transactional
public void receiveUser(@MessageBody User user) {
    // Критическая операция
}
```

## Message Selectors

### Selector-based Consumption

```java
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;
import jakarta.inject.Singleton;

@JMSListener("connectionFactory")
public class SelectorConsumer {
    
    @Queue(value = "user.queue", selector = "priority > 5")
    public void receiveHighPriorityUser(@MessageBody User user) {
        // Обработка пользователей с высоким приоритетом
    }
    
    @Queue(value = "user.queue", selector = "type = 'VIP'")
    public void receiveVipUser(@MessageBody User user) {
        // Обработка VIP пользователей
    }
}
```

## Dead Letter Queue

### DLQ Configuration

```java
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Queue;
import jakarta.inject.Singleton;

@JMSListener("connectionFactory")
public class DeadLetterQueueConsumer {
    
    @Queue("user.queue.dlq")
    public void handleDeadLetter(@MessageBody User user) {
        // Обработка сообщений из dead letter queue
        log.error("Failed to process user: {}", user);
    }
}
```

## Message Persistence

### Persistent Messages

```java
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;

@JMSProducer("connectionFactory")
public interface PersistentProducer {
    
    @Queue(value = "user.queue", deliveryMode = DeliveryMode.PERSISTENT)
    void sendPersistentUser(@MessageBody User user);
}
```

## Message Priority

### Priority-based Messaging

```java
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;
import io.micronaut.messaging.annotation.MessageHeader;

@JMSProducer("connectionFactory")
public interface PriorityProducer {
    
    @Queue("user.queue")
    void sendUserWithPriority(
        @MessageBody User user,
        @MessageHeader("JMSPriority") int priority
    );
}
```

## JMS Connection Pooling

### Pool Configuration

**application.yml:**

```yaml
jms:
  connection-factory:
    pool:
      enabled: true
      max-connections: 20
      min-connections: 5
      max-idle-time: 30m
```

## JMS Message Expiration

### Message TTL

```java
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.messaging.annotation.MessageBody;

@JMSProducer("connectionFactory")
public interface TTLProducer {
    
    @Queue(value = "user.queue", timeToLive = 60000) // 60 seconds
    void sendUserWithTTL(@MessageBody User user);
}
```


## Заключение

**Micronaut JMS** предоставляет мощные инструменты для интеграции с **JMS**. Поддержка **producers**, **consumers**, **queues**, **topics**, **transactions**, **message headers**, **message selectors**, **dead letter queue**, **message persistence**, **priority**, **connection pooling**, **message expiration** и других продвинутых возможностей позволяет создавать надежные приложения для асинхронной обработки сообщений.

## Дополнительные ресурсы

- [**Micronaut JMS** Documentation](https://micronaut-projects.github.io/micronaut-jms/snapshot/guide/)
- [**JMS** Specification](https://jakarta.ee/specifications/messaging/)
- [**ActiveMQ** Documentation](https://activemq.apache.org/components/classic/documentation/)
- [**Apache Artemis** Documentation](https://activemq.apache.org/components/artemis/documentation/)
- [**JMS Best Practices**](https://www.baeldung.com/java-message-service-jms)
