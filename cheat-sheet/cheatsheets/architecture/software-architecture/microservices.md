# Microservices Architecture

Кратко: архитектура микросервисов, принципы проектирования, паттерны, коммуникация между сервисами, API Gateway, Service Discovery, Circuit Breaker и другие аспекты микросервисной архитектуры.

**Дата последнего обновления:** 2025-01-11

## Полезные ссылки

### Официальная документация
- [Microservices Patterns](https://microservices.io/patterns/)
- [Martin Fowler on Microservices](https://martinfowler.com/articles/microservices.html)
- [Microservices.io](https://microservices.io/)

### Baeldung
- [Microservices Tutorial](https://www.baeldung.com/cs/microservices)

## Содержание

- [Введение в микросервисы](#введение-в-микросервисы)
- [Монолит vs Микросервисы](#монолит-vs-микросервисы)
- [Принципы проектирования](#принципы-проектирования)
- [Паттерны микросервисов](#паттерны-микросервисов)
- [Коммуникация между сервисами](#коммуникация-между-сервисами)
- [API Gateway](#api-gateway)
- [Service Discovery](#service-discovery)
- [Circuit Breaker](#circuit-breaker)
- [Database per Service](#database-per-service)
- [Event Sourcing и CQRS](#event-sourcing-и-cqrs)
- [Saga Pattern](#saga-pattern)
- [Bulkhead Pattern](#bulkhead-pattern)
- [Deployment Strategies](#deployment-strategies)
- [Мониторинг и Observability](#мониторинг-и-observability)
- [Тестирование микросервисов](#тестирование-микросервисов)

## Введение в микросервисы

**Микросервисная архитектура** — это архитектурный подход к разработке приложений, при котором приложение разбивается на набор небольших, независимых сервисов, которые взаимодействуют через хорошо определенные API.

### Определение микросервисов

Микросервисы — это подход к разработке единого приложения как набора небольших сервисов, каждый из которых:
- Запускается в собственном процессе
- Взаимодействует через легковесные механизмы (обычно HTTP REST API)
- Развертывается независимо
- Может быть написан на разных языках программирования
- Имеет собственную базу данных

### Ключевые характеристики

1. **Независимое развертывание**: Каждый сервис можно развертывать отдельно
2. **Технологическая разнородность**: Разные сервисы могут использовать разные технологии
3. **Децентрализованное управление данными**: Каждый сервис имеет свою БД
4. **Отказоустойчивость**: Падение одного сервиса не должно приводить к падению всей системы
5. **Масштабируемость**: Каждый сервис может масштабироваться независимо

## Монолит vs Микросервисы

### Монолитная архитектура

**Монолит** — это единое приложение, которое развертывается как один процесс.

**Преимущества монолита:**
- Простота разработки на начальном этапе
- Простое тестирование
- Простое развертывание
- Производительность (вызовы внутри процесса)

**Недостатки монолита:**
- Сложность поддержки при росте кодовой базы
- Технологическое закрепление
- Проблемы с масштабированием
- Одно место отказа
- Медленный цикл разработки

### Микросервисная архитектура

**Преимущества микросервисов:**
- Независимое развертывание
- Технологическая свобода
- Масштабирование по сервисам
- Изоляция отказов
- Параллельная разработка командами

**Недостатки микросервисов:**
- Сложность коммуникации между сервисами
- Распределенные транзакции
- Сложность тестирования
- Необходимость инфраструктуры (мониторинг, логирование, discovery)
- Overhead на сетевые вызовы

### Когда использовать микросервисы?

**Используйте микросервисы, когда:**
- Команда большая (multiple teams)
- Масштаб приложения большой
- Разные части имеют разные требования к масштабированию
- Нужна технологическая разнородность
- Есть требования к высокой доступности

**НЕ используйте микросервисы, когда:**
- Команда небольшая
- Приложение простое
- Нет проблем с монолитом
- Нет опыта работы с распределенными системами

## Принципы проектирования

### Single Responsibility Principle (SRP)

Каждый микросервис должен отвечать за одну бизнес-функцию или домен.

**Пример:**
- User Service — управление пользователями
- Order Service — управление заказами
- Payment Service — обработка платежей
- Notification Service — отправка уведомлений

### Domain-Driven Design (DDD)

Используйте доменно-ориентированное проектирование для определения границ сервисов:
- **Bounded Context**: Четкие границы домена
- **Ubiquitous Language**: Общий язык для домена
- **Aggregates**: Группы связанных сущностей

### API First Design

Сначала проектируйте API, затем реализуйте:
- Используйте OpenAPI/Swagger для спецификации
- Версионируйте API
- Обратная совместимость важна

### Failure Isolation

Изолируйте отказы:
- Circuit Breaker для защиты от каскадных отказов
- Timeouts для всех внешних вызовов
- Retry с exponential backoff
- Graceful degradation

## Паттерны микросервисов

### 1. API Gateway Pattern

Единая точка входа для всех клиентов.

**Задачи API Gateway:**
- Маршрутизация запросов к соответствующим сервисам
- Аутентификация и авторизация
- Rate limiting
- Кэширование
- Логирование и мониторинг
- Load balancing
- Protocol translation

**Примеры:**
- Kong
- AWS API Gateway
- Zuul (Netflix)
- Spring Cloud Gateway

### 2. Service Discovery Pattern

Автоматическое обнаружение сетевых расположений сервисов.

**Типы:**
- **Client-side discovery**: Клиент запрашивает у Service Registry
- **Server-side discovery**: Router/Load Balancer запрашивает у Service Registry

**Примеры:**
- Consul
- Eureka (Netflix)
- etcd
- Kubernetes Service Discovery

### 3. Circuit Breaker Pattern

Защита от каскадных отказов.

**Состояния:**
- **Closed**: Нормальная работа
- **Open**: Сервис недоступен, запросы блокируются
- **Half-Open**: Проверка восстановления сервиса

**Примеры:**
- Resilience4j (Java)
- Hystrix (Netflix, deprecated)
- Polly (.NET)

### 4. Database per Service Pattern

Каждый микросервис имеет свою базу данных.

**Преимущества:**
- Независимость данных
- Технологическая свобода
- Изоляция отказов

**Недостатки:**
- Распределенные транзакции
- Сложность запросов через несколько БД
- Eventual consistency

### 5. Saga Pattern

Управление распределенными транзакциями.

**Типы:**
- **Choreography**: Каждый сервис публикует события
- **Orchestration**: Центральный оркестратор управляет транзакцией

**Пример:**
```
Order Service → Payment Service → Inventory Service → Shipping Service
```

### 6. Event Sourcing Pattern

Хранение всех изменений состояния как последовательности событий.

**Преимущества:**
- Полная история изменений
- Audit trail
- Временные запросы

**Недостатки:**
- Сложность реализации
- Рост размера хранилища

### 7. CQRS (Command Query Responsibility Segregation)

Разделение операций чтения и записи.

**Command Side:**
- Обработка команд (изменение состояния)
- Event Sourcing
- Оптимизация для записи

**Query Side:**
- Обработка запросов (чтение)
- Денормализованные представления
- Оптимизация для чтения

### 8. Bulkhead Pattern

Изоляция ресурсов для предотвращения каскадных отказов.

**Типы изоляции:**
- Thread pool isolation
- Connection pool isolation
- Resource isolation

## Коммуникация между сервисами

### Синхронная коммуникация

**REST API:**
- HTTP/HTTPS
- JSON
- Стандартные методы (GET, POST, PUT, DELETE)
- Простота реализации

**gRPC:**
- HTTP/2
- Protocol Buffers
- Высокая производительность
- Типобезопасность
- Поддержка streaming

**GraphQL:**
- Гибкие запросы
- Единая конечная точка
- Клиент определяет данные

### Асинхронная коммуникация

**Message Brokers:**
- RabbitMQ
- Apache Kafka
- Amazon SQS
- Azure Service Bus

**Паттерны:**
- Pub/Sub
- Point-to-Point
- Request/Reply

**События:**
- Domain Events
- Integration Events
- Event Streaming

### Выбор типа коммуникации

**Используйте синхронную коммуникацию, когда:**
- Нужен немедленный ответ
- Простые запросы
- Низкая задержка критична

**Используйте асинхронную коммуникацию, когда:**
- Можно принять eventual consistency
- Высокая нагрузка
- Долгие операции
- Нужна отказоустойчивость

## API Gateway

API Gateway — это единая точка входа для всех клиентов.

### Функции API Gateway

1. **Маршрутизация**
   - Route requests to appropriate services
   - Path-based routing
   - Header-based routing

2. **Аутентификация и авторизация**
   - JWT validation
   - OAuth 2.0
   - API keys

3. **Rate Limiting**
   - Защита от перегрузки
   - Quota management
   - Throttling

4. **Трансформация запросов/ответов**
   - Protocol translation
   - Data transformation
   - Response aggregation

5. **Кэширование**
   - Response caching
   - Cache invalidation

6. **Мониторинг**
   - Request logging
   - Metrics collection
   - Analytics

### Пример конфигурации (Kong)

```yaml
services:
  - name: user-service
    url: http://user-service:8080
    routes:
      - name: user-route
        paths:
          - /api/users

plugins:
  - name: rate-limiting
    config:
      minute: 100
  - name: jwt
    config:
      secret_is_base64: false
```

## Service Discovery

Service Discovery позволяет сервисам находить друг друга в сети.

### Client-Side Discovery

Клиент запрашивает у Service Registry адреса сервисов:

```
Client → Service Registry → Service Instances
Client → Service Instance (direct call)
```

**Преимущества:**
- Прямое взаимодействие
- Меньше точек отказа

**Недостатки:**
- Клиент должен знать о Registry
- Усложнение клиента

### Server-Side Discovery

Router/Load Balancer запрашивает у Service Registry:

```
Client → Load Balancer → Service Registry → Service Instances
Client → Load Balancer → Service Instance
```

**Преимущества:**
- Клиент не знает о Registry
- Централизованное управление

**Недостатки:**
- Дополнительный компонент
- Точка отказа

### Реализации

**Consul:**
- Service discovery
- Health checking
- Key-value store
- Multi-datacenter support

**Eureka (Netflix):**
- Self-preservation mode
- Client-side caching
- Spring Cloud integration

**Kubernetes:**
- Built-in service discovery
- DNS-based
- Service objects

## Circuit Breaker

Circuit Breaker предотвращает каскадные отказы.

### Состояния Circuit Breaker

**Closed (Закрыт):**
- Нормальная работа
- Мониторинг ошибок
- При превышении threshold → Open

**Open (Открыт):**
- Запросы блокируются немедленно
- Возвращается ошибка без вызова сервиса
- По истечении timeout → Half-Open

**Half-Open (Полуоткрыт):**
- Пробные запросы для проверки восстановления
- При успехе → Closed
- При ошибке → Open

### Реализация (Resilience4j)

```java
CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendService");

Supplier<String> decoratedSupplier = CircuitBreaker
    .decorateSupplier(circuitBreaker, backendService::doSomething);

String result = Try.ofSupplier(decoratedSupplier)
    .recover(throwable -> "Hello from Recovery")
    .get();
```

### Конфигурация

```java
CircuitBreakerConfig config = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)
    .waitDurationInOpenState(Duration.ofMillis(1000))
    .slidingWindowSize(10)
    .build();
```

## Database per Service

Каждый микросервис имеет свою базу данных.

### Принципы

1. **Независимость данных**
   - Каждый сервис управляет своими данными
   - Нет прямого доступа к БД другого сервиса

2. **Технологическая свобода**
   - SQL для одних сервисов
   - NoSQL для других

3. **Изоляция отказов**
   - Проблемы с БД одного сервиса не влияют на другие

### Вызовы

**Распределенные транзакции:**
- Нет ACID транзакций между сервисами
- Используйте Saga pattern

**Запросы через несколько БД:**
- API composition
- CQRS с materialized views

**Данные в нескольких сервисах:**
- Eventual consistency
- Event-driven updates

## Event Sourcing и CQRS

### Event Sourcing

Хранение всех изменений состояния как последовательности событий.

**Пример:**
```
Events: OrderCreated → PaymentReceived → OrderShipped → OrderDelivered
Current State: Order { status: "DELIVERED", ... }
```

**Преимущества:**
- Полная история
- Audit trail
- Временные запросы
- Отладка упрощается

**Недостатки:**
- Сложность
- Рост хранилища
- Eventual consistency

### CQRS

Разделение операций чтения и записи.

**Command Side:**
- Принимает команды
- Валидация
- Изменение состояния
- Публикация событий

**Query Side:**
- Обработка запросов
- Денормализованные представления
- Оптимизировано для чтения

**Преимущества:**
- Независимое масштабирование
- Оптимизация для чтения/записи
- Гибкость в выборе технологий

## Saga Pattern

Управление распределенными транзакциями.

### Choreography (Хореография)

Каждый сервис публикует события и реагирует на события других.

```
Order Service → OrderCreated event
Payment Service → listens → PaymentProcessed event
Inventory Service → listens → InventoryReserved event
```

**Преимущества:**
- Децентрализация
- Гибкость

**Недостатки:**
- Сложность отслеживания
- Сложность отката

### Orchestration (Оркестрация)

Центральный оркестратор управляет транзакцией.

```
Orchestrator:
  1. Call Order Service
  2. Call Payment Service
  3. Call Inventory Service
  4. If any fails → compensate
```

**Преимущества:**
- Централизованное управление
- Легче отслеживать
- Проще откат

**Недостатки:**
- Точка отказа
- Дополнительный компонент

### Compensating Transactions

Откат изменений при ошибке:

```java
try {
    reserveInventory();
    processPayment();
    createOrder();
} catch (Exception e) {
    releaseInventory();  // compensate
    refundPayment();     // compensate
    throw e;
}
```

## Bulkhead Pattern

Изоляция ресурсов для предотвращения каскадных отказов.

### Типы изоляции

**Thread Pool Isolation:**
- Отдельный thread pool для каждого сервиса
- Предотвращение блокировки всех потоков

**Connection Pool Isolation:**
- Отдельный пул соединений
- Защита от исчерпания соединений

**Resource Isolation:**
- Изоляция CPU, памяти
- Контейнеризация (Docker, Kubernetes)

### Пример (Hystrix)

```java
HystrixThreadPoolProperties.Setter()
    .withCoreSize(10)
    .withMaximumSize(10)
    .withMaxQueueSize(-1);
```

## Deployment Strategies

### Blue-Green Deployment

Два идентичных окружения:
- **Blue**: Текущая версия (production)
- **Green**: Новая версия (staging)

Переключение трафика происходит мгновенно.

### Canary Deployment

Постепенное развертывание:
- Небольшой процент трафика на новую версию
- Мониторинг метрик
- Постепенное увеличение

### Rolling Deployment

Постепенное обновление экземпляров:
- Обновление по одному/несколько экземпляров
- Без downtime
- Медленнее чем blue-green

## Мониторинг и Observability

### Три столпа Observability

1. **Metrics (Метрики)**
   - Количественные данные
   - Prometheus, Grafana
   - CPU, Memory, Request rate, Error rate

2. **Logs (Логи)**
   - События с временными метками
   - ELK Stack, Loki
   - Структурированное логирование

3. **Traces (Трейсы)**
   - Распределенное трассирование
   - Jaeger, Zipkin
   - End-to-end request tracking

### Distributed Tracing

Трассировка запроса через несколько сервисов:

```
Request → API Gateway → User Service → Order Service → Payment Service
Trace ID: abc123
Span IDs: gateway-1, user-2, order-3, payment-4
```

### Health Checks

Проверка здоровья сервисов:

```java
@GetMapping("/health")
public ResponseEntity<Health> health() {
    Health health = Health.status(getServiceHealth())
        .withDetail("database", checkDatabase())
        .withDetail("external-service", checkExternalService())
        .build();
    return ResponseEntity.ok(health);
}
```

## Тестирование микросервисов

### Типы тестов

1. **Unit Tests**
   - Тестирование компонентов изолированно
   - Mock внешних зависимостей

2. **Integration Tests**
   - Тестирование взаимодействия между компонентами
   - Test containers для БД

3. **Contract Tests**
   - Проверка совместимости API
   - Pact, Spring Cloud Contract

4. **End-to-End Tests**
   - Тестирование полного потока
   - Сложно поддерживать

### Test Containers

Запуск реальных зависимостей в тестах:

```java
@Testcontainers
class UserServiceTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13");
    
    // tests
}
```

### Contract Testing (Pact)

```java
@Pact(consumer = "UserService", provider = "OrderService")
public RequestResponsePact getUserPact(PactDslWithProvider builder) {
    return builder
        .given("user exists")
        .uponReceiving("a request for user")
        .path("/users/123")
        .method("GET")
        .willRespondWith()
        .status(200)
        .body(...)
        .toPact();
}
```

> **Примечание**: Это базовая информация о микросервисной архитектуре. Для более детального изучения см. официальную документацию и книги по микросервисам.
