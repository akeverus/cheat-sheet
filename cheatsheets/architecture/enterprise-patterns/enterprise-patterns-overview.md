---
title: "Enterprise Patterns — обзор"
description: "Паттерны проектирования для корпоративных приложений."
tags: ["architecture", "enterprise-patterns", "enterprise-patterns-overview"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Enterprise Patterns — обзор

Паттерны проектирования для корпоративных приложений.

**Дата последнего обновления:** 2026-02-06



## Полезные ссылки

- [Enterprise Integration Patterns](https://www.enterpriseintegrationpatterns.com/)
- [Patterns of Enterprise Application Architecture (Martin Fowler)](https://martinfowler.com/books/eaa.html)

## Содержание

- [**Enterprise Patterns** — Обзор](#enterprise-patterns--обзор)
- [Что такое **Enterprise Patterns**](#что-такое-enterprise-patterns)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Основные категории паттернов](#основные-категории-паттернов)
  - [Паттерны представления данных](#паттерны-представления-данных)
    - [**Repository Pattern**](#repository-pattern)
    - [**Unit** of **Work Pattern**](#unit-of-work-pattern)
  - [Паттерны бизнес-логики](#паттерны-бизнес-логики)
    - [**Service Layer Pattern**](#service-layer-pattern)
    - [**Domain Model Pattern**](#domain-model-pattern)
  - [Паттерны интеграции](#паттерны-интеграции)
    - [**Gateway Pattern**](#gateway-pattern)
    - [**Message Channel Pattern**](#message-channel-pattern)
  - [Паттерны распределённых систем](#паттерны-распределённых-систем)
    - [**Circuit Breaker Pattern**](#circuit-breaker-pattern)
    - [**Saga Pattern**](#saga-pattern)
- [Когда использовать **Enterprise Patterns**](#когда-использовать-enterprise-patterns)
- [См. также](#см-также)

## Что такое **Enterprise Patterns**

**Enterprise Patterns** — это архитектурные паттерны, предназначенные для решения типичных задач в крупных корпоративных приложениях: распределённые системы, интеграция, управление транзакциями, безопасность.

## Основные категории паттернов

### Паттерны представления данных

#### **Repository Pattern**
**Цель:** Абстракция доступа к данным

Ниже — пример **Repository** (**Java**).
```java
interface UserRepository {
    User findById(Long id);
    List<User> findAll();
    void save(User user);
    void delete(Long id);
}

class JpaUserRepository implements UserRepository {
    @PersistenceContext
    private EntityManager em;
    
    public User findById(Long id) {
        return em.find(User.class, id);
    }
    
    public void save(User user) {
        em.persist(user);
    }
}
```

#### **Unit** of **Work Pattern**
**Цель:** Управление транзакциями

```java
class UnitOfWork {
    private List<Entity> newEntities = new ArrayList<>();
    private List<Entity> modifiedEntities = new ArrayList<>();
    private List<Entity> deletedEntities = new ArrayList<>();
    
    void registerNew(Entity entity) {
        newEntities.add(entity);
    }
    
    void commit() {
        // Сохранение всех изменений в одной транзакции
        entityManager.getTransaction().begin();
        newEntities.forEach(em::persist);
        modifiedEntities.forEach(em::merge);
        deletedEntities.forEach(em::remove);
        entityManager.getTransaction().commit();
    }
}
```

### Паттерны бизнес-логики

#### **Service Layer Pattern**
**Цель:** Инкапсуляция бизнес-логики

```java
@Service
class OrderService {
    private OrderRepository orderRepository;
    private PaymentService paymentService;
    private InventoryService inventoryService;
    
    @Transactional
    public Order createOrder(OrderRequest request) {
        // Бизнес-логика создания заказа
        validateRequest(request);
        checkInventory(request.getItems());
        Order order = createOrderEntity(request);
        processPayment(order);
        orderRepository.save(order);
        return order;
    }
}
```

#### **Domain Model Pattern**
**Цель:** Богатая модель предметной области

```java
class Order {
    private List<OrderItem> items;
    private OrderStatus status;
    
    void addItem(Product product, int quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify confirmed order");
        }
        items.add(new OrderItem(product, quantity));
    }
    
    Money calculateTotal() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);
    }
}
```

### Паттерны интеграции

#### **Gateway Pattern**
**Цель:** Интеграция с внешними системами

```java
interface PaymentGateway {
    PaymentResult processPayment(PaymentRequest request);
}

class StripePaymentGateway implements PaymentGateway {
    public PaymentResult processPayment(PaymentRequest request) {
        // Интеграция со Stripe API
    }
}

class PayPalPaymentGateway implements PaymentGateway {
    public PaymentResult processPayment(PaymentRequest request) {
        // Интеграция с PayPal API
    }
}
```

#### **Message Channel Pattern**
**Цель:** Асинхронная коммуникация

```java
@Component
class OrderEventPublisher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(order.getId());
        rabbitTemplate.convertAndSend("order.events", event);
    }
}
```

### Паттерны распределённых систем

#### **Circuit Breaker Pattern**
**Цель:** Защита от каскадных сбоев

```java
@Component
class CircuitBreaker {
    private CircuitState state = CircuitState.CLOSED;
    private int failureCount = 0;
    private long lastFailureTime = 0;
    
    <T> T execute(Supplier<T> operation) {
        if (state == CircuitState.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > 60000) {
                state = CircuitState.HALF_OPEN;
            } else {
                throw new CircuitBreakerOpenException();
            }
        }
        
        try {
            T result = operation.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
}
```

#### **Saga Pattern**
**Цель:** Управление распределёнными транзакциями

```java
class OrderSaga {
    void execute(Order order) {
        try {
            reserveInventory(order);
            processPayment(order);
            shipOrder(order);
        } catch (Exception e) {
            compensate(order);
        }
    }
    
    void compensate(Order order) {
        releaseInventory(order);
        refundPayment(order);
        cancelShipping(order);
    }
}
```

## Когда использовать **Enterprise Patterns**

Паттерны из этой категории стоит применять в следующих случаях:

- **Крупные приложения:** сложная бизнес-логика, множество интеграций
- **Распределённые системы:** микросервисы, облачные приложения
- **Высокие требования:** масштабируемость, надёжность, безопасность


## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Транзакции размазаны по сервисам | Нет единой границы транзакции | Unit of Work; для распределённых — Saga, компенсации |
| Слой сервисов раздут, домен пустой | Anemic domain | Перенос логики в Domain Model |
| Интеграции хрупкие | Прямые вызовы | Gateway, Message Channel, Circuit Breaker |

## Частые вопросы

**Repository и Unit of Work?** Repository — доступ к сущностям; UoW — одна транзакция на несколько изменений. Часто вместе.

**Когда Service Layer?** Когда координация use case не в одном агрегате или нужна точка входа для транзакции.

**Saga vs 2PC?** 2PC плохо масштабируется. Saga — локальные транзакции + компенсации.
## См. также

- [Architectural Decision Records](../architectural-decision-records/adr-template.md) — шаблон ADR
- [Design Principles](../design-principles/design-principles.md) — принципы проектирования
- [System Design Basics](../system-design/system-design-basics.md) — основы проектирования систем
- [Microservices](../software-architecture/microservices.md) — микросервисная архитектура

---
