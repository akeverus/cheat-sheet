# Архитектура (Architecture)

**Паттерны архитектуры, системное проектирование, enterprise паттерны, принципы проектирования**

## 📋 Описание

Этот раздел содержит руководства по архитектуре программного обеспечения: микросервисы, распределенные системы, паттерны проектирования, принципы SOLID, DDD, CQRS, Event Sourcing и другие enterprise паттерны.

## 📚 Структура раздела

### 🏗️ [Software Architecture](software-architecture/)
**Архитектурные стили и паттерны**

- **Layered Architecture** - Слоистая архитектура
- **Hexagonal Architecture** - Гексагональная архитектура
- **Clean Architecture** - Чистая архитектура
- **Microservices Architecture** - Микросервисная архитектура

### 🎯 [System Design](system-design/)
**Системное проектирование**

- **Scalability** - Масштабируемость
- **Reliability** - Надежность
- **Performance** - Производительность
- **Load Balancing** - Балансировка нагрузки
- **Caching Strategies** - Стратегии кэширования

### 📐 [Design Principles](design-principles/)
**Принципы проектирования**

- **SOLID Principles** - SOLID принципы
- **DRY (Don't Repeat Yourself)** - Не повторяйся
- **KISS (Keep It Simple, Stupid)** - Будь проще
- **YAGNI (You Aren't Gonna Need It)** - Тебе это не понадобится

### 🏢 [Enterprise Patterns](enterprise-patterns/)
**Enterprise паттерны**

- **[Microservices](microservices.md)** - Микросервисы
- **[Event-Driven Architecture](event-driven.md)** - Event-driven архитектура
- **[CQRS](cqrs.md)** - Command Query Responsibility Segregation
- **[Event Sourcing](event-sourcing.md)** - Event Sourcing
- **[Domain-Driven Design](ddd.md)** - Domain-Driven Design
- **[SOA](soa.md)** - Service-Oriented Architecture
- **[Serverless](serverless.md)** - Serverless архитектура

### 📝 [Architectural Decision Records](architectural-decision-records/)
**Записи архитектурных решений**

- **ADR Template** - Шаблон ADR
- **Examples** - Примеры ADR

## 🎯 Для кого этот раздел

### Архитекторы
- **Выбор архитектуры** - какой стиль использовать
- **Паттерны проектирования** - enterprise паттерны
- **Системное проектирование** - масштабируемые системы

### Senior Developers
- **Понимание архитектуры** - как проектировать системы
- **Микросервисы** - когда и как использовать
- **Best practices** - лучшие практики

## 📖 Рекомендуемый порядок изучения

### Для начинающих
```
1. Design Principles
   ├── SOLID принципы
   └── DRY, KISS, YAGNI

2. Software Architecture
   ├── Layered Architecture
   └── Clean Architecture

3. Enterprise Patterns
   ├── Microservices
   └── Event-Driven Architecture
```

### Для опытных
```
1. System Design
   ├── Scalability patterns
   ├── Reliability patterns
   └── Performance optimization

2. Advanced Patterns
   ├── CQRS + Event Sourcing
   ├── Domain-Driven Design
   └── Service Mesh
```

## 🔗 Кросс-ссылки

### Связанные разделы
- **[Frameworks](../frameworks/)** - Фреймворки для реализации архитектуры
- **[Databases](../databases/)** - Выбор БД для архитектуры
- **[Patterns](../patterns/)** - Design patterns
- **[Interview](../interview/architecture/)** - Вопросы на собеседовании

### Специфичные связи
- **Microservices** → [Spring Cloud](../frameworks/java-frameworks/spring/spring-cloud.md)
- **Event-Driven** → [Kafka](../messaging/kafka.md)
- **CQRS** → [Event Sourcing](event-sourcing.md)
- **DDD** → [Design Patterns](../patterns/)

## 📚 Полезные ресурсы

### Книги
- "Designing Data-Intensive Applications" - Martin Kleppmann
- "Building Microservices" - Sam Newman
- "Domain-Driven Design" - Eric Evans
- "Clean Architecture" - Robert C. Martin

### Статьи
- [Microservices Patterns](https://microservices.io/patterns/)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)

## 🎯 Следующие шаги

После изучения архитектуры:
1. **Изучите паттерны** → [Patterns](../patterns/)
2. **Освойте микросервисы** → [Microservices](microservices.md)
3. **Подготовьтесь к интервью** → [Interview](../interview/architecture/)

---

[⬆️ Наверх](../README.md) | [Следующий раздел ➡️](../testing/)

*Обновлено: 2026-01-25*
