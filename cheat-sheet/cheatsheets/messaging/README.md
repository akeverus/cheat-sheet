# Messaging системы (Messaging)

**Комплексные руководства по messaging системам: Kafka, RabbitMQ, ActiveMQ, NATS**

## 📋 Описание

Этот раздел содержит детальные руководства по messaging системам для асинхронной коммуникации в распределенных системах: Apache Kafka, RabbitMQ, Apache ActiveMQ, NATS.

## 📚 Структура раздела

### 🚀 [Apache Kafka](kafka.md)
**Распределенная streaming платформа**

- **Kafka Basics** - Основы Kafka
- **Kafka Producers** - Producers
- **Kafka Consumers** - Consumers
- **Kafka Streams** - Kafka Streams
- **Kafka Connect** - Kafka Connect

### 🐰 [RabbitMQ](rabbitmq.md)
**Message broker**

- **RabbitMQ Basics** - Основы RabbitMQ
- **RabbitMQ Advanced** - Продвинутые темы
- **RabbitMQ Patterns** - Паттерны использования

### 📨 [Apache ActiveMQ](activemq.md)
**Message broker**

- **ActiveMQ Basics** - Основы ActiveMQ

### ⚡ [NATS](nats.md)
**Легковесный messaging**

- **NATS Basics** - Основы NATS

### 🔧 [Advanced Topics](.)
**Продвинутые темы**

- **[Kafka Advanced](kafka-advanced.md)** - Продвинутые темы Kafka
- **[RabbitMQ Advanced](rabbitmq-advanced.md)** - Продвинутые темы RabbitMQ

## 🎯 Для кого этот раздел

### Backend Developers
- **Асинхронная коммуникация** - messaging паттерны
- **Event-driven архитектура** - event-driven системы
- **Микросервисы** - коммуникация между сервисами

### Architects
- **Выбор messaging системы** - Kafka vs RabbitMQ vs NATS
- **Архитектурные паттерны** - pub/sub, message queue
- **Масштабирование** - распределенные системы

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. Messaging Basics
   ├── Message Queue vs Pub/Sub
   └── Message Patterns

2. RabbitMQ
   ├── RabbitMQ Basics
   └── RabbitMQ Patterns

3. Kafka Basics
   └── Kafka Producers/Consumers
```

### Для опытных
```
1. Kafka Advanced
   ├── Kafka Streams
   ├── Kafka Connect
   └── Kafka Performance

2. Architecture Patterns
   ├── Event Sourcing
   ├── CQRS
   └── Saga Pattern

3. Production Ready
   ├── Monitoring
   ├── Security
   └── Disaster Recovery
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Frameworks](../frameworks/)** - Spring Kafka, Spring Messaging
- **[Architecture](../architecture/)** - Event-Driven Architecture
- **[Interview](../interview/messaging/)** - Вопросы на собеседовании
- **[DevOps](../devops/messaging/)** - Messaging в DevOps

### Специфичные связи
- **Spring Kafka** → [Spring](../frameworks/java-frameworks/spring/spring-kafka.md)
- **Event-Driven** → [Architecture](../architecture/event-driven.md)
- **Kafka** → [Interview](../interview/messaging/kafka-interview.md)

## 📚 Полезные ресурсы

### Официальная документация
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [RabbitMQ Documentation](https://www.rabbitmq.com/documentation.html)
- [NATS Documentation](https://docs.nats.io/)

### Учебные материалы
- [Kafka Tutorial](https://kafka.apache.org/quickstart)
- [RabbitMQ Tutorial](https://www.rabbitmq.com/getstarted.html)

## 🎯 Следующие шаги

После изучения messaging:
1. **Изучите Spring Kafka** → [Spring Kafka](../frameworks/java-frameworks/spring/spring-kafka.md)
2. **Освойте Event-Driven** → [Event-Driven Architecture](../architecture/event-driven.md)
3. **Подготовьтесь к интервью** → [Interview](../interview/messaging/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../api/)

*Обновлено: 2026-01-25*
