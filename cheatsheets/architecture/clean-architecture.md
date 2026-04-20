---
title: "Clean Architecture"
description: "Clean Architecture Роберта Мартина: слои, зависимости, Dependency Rule, применение в Java/Spring."
tags:
  - architecture
  - clean-architecture
  - solid
  - design-patterns
difficulty: "intermediate"
updated: "2026-04-20"
---
# Clean Architecture

Clean Architecture (Роберт Мартин, «Дядя Боб») — архитектурный подход, в котором **бизнес-логика не зависит от фреймворков, БД и UI**. Зависимости направлены строго внутрь: внешние слои зависят от внутренних, никогда наоборот.

## Полезные ссылки

### Официальная документация
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) — оригинальная статья дяди Боба

### См. также
- [[hexagonal-architecture|Hexagonal Architecture]] — архитектура «Порты и адаптеры»
- [[solid-principles|SOLID]] — принципы, лежащие в основе
- [[creational-patterns|Паттерны создания]] — паттерны GoF
- [[microservices|Микросервисная архитектура]] — применение Clean Architecture в микросервисах

## Содержание

- [Слои (изнутри наружу)](#слои-изнутри-наружу)
  - [Dependency Rule](#dependency-rule)
- [Entities (Domain)](#entities-domain)
- [Use Cases (Application)](#use-cases-application)
- [Interface Adapters](#interface-adapters)
- [Frameworks & Drivers](#frameworks-drivers)
- [Структура пакетов](#структура-пакетов)
- [Dependency Inversion в деталях](#dependency-inversion-в-деталях)
- [Тестирование](#тестирование)
- [Когда применять](#когда-применять)
- [See also](#see-also)

## Слои (изнутри наружу)

```text
┌─────────────────────────────────────────┐
│  Frameworks & Drivers                   │  ← Spring, Hibernate, REST, DB
│  ┌───────────────────────────────────┐  │
│  │  Interface Adapters               │  │  ← Controllers, Presenters, Gateways
│  │  ┌─────────────────────────────┐  │  │
│  │  │  Application / Use Cases    │  │  │  ← Бизнес-правила приложения
│  │  │  ┌───────────────────────┐  │  │  │
│  │  │  │  Entities / Domain    │  │  │  │  ← Бизнес-сущности и правила
│  │  │  └───────────────────────┘  │  │  │
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### Dependency Rule

> Зависимости исходного кода могут указывать **только внутрь**.

- Entities не знают о Use Cases, Adapters, Frameworks.
- Use Cases не знают о Frameworks.
- Adapters зависят от Use Cases, но не от Frameworks напрямую.

## Entities (Domain)

Бизнес-объекты с самыми стабильными правилами. Не зависят ни от чего.

```java
// Чистая бизнес-сущность — никаких аннотаций фреймворков
public class Order {
    private final OrderId id;
    private final List<OrderItem> items;
    private OrderStatus status;

    public Money calculateTotal() {
        return items.stream()
            .map(OrderItem::subtotal)
            .reduce(Money.ZERO, Money::add);
    }

    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Can only confirm PENDING orders");
        }
        this.status = OrderStatus.CONFIRMED;
    }
}
```

## Use Cases (Application)

Оркестрируют сущности для выполнения бизнес-сценария. Определяют интерфейсы портов (input/output ports).

```java
// Input port — интерфейс use case
public interface PlaceOrderUseCase {
    OrderId execute(PlaceOrderCommand command);
}

// Output port — абстракция для внешних зависимостей
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}

// Реализация use case
public class PlaceOrderService implements PlaceOrderUseCase {
    private final OrderRepository orderRepository;
    private final InventoryPort inventory;
    private final EventPublisher events;

    @Override
    public OrderId execute(PlaceOrderCommand command) {
        // Бизнес-логика без Spring, без JPA, без HTTP
        Order order = Order.create(command.customerId(), command.items());
        inventory.reserve(order.getItems());
        orderRepository.save(order);
        events.publish(new OrderPlaced(order.getId()));
        return order.getId();
    }
}
```

## Interface Adapters

Конвертируют данные между Use Cases и внешним миром (HTTP, DB, etc.).

```java
// Controller — входящий адаптер
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final PlaceOrderUseCase placeOrder;

    @PostMapping("/api/orders")
    public ResponseEntity<OrderDto> place(@RequestBody @Valid OrderRequest req) {
        PlaceOrderCommand cmd = OrderMapper.toCommand(req);
        OrderId id = placeOrder.execute(cmd);
        return ResponseEntity.created(URI.create("/api/orders/" + id)).body(new OrderDto(id));
    }
}

// Repository adapter — исходящий адаптер (реализует output port)
@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {
    private final OrderJpaRepository jpa;

    @Override
    public void save(Order order) {
        jpa.save(OrderEntityMapper.toEntity(order));
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return jpa.findById(id.value()).map(OrderEntityMapper::toDomain);
    }
}
```

## Frameworks & Drivers

Spring Boot, Hibernate, PostgreSQL, Kafka — детали реализации. Подключаются снаружи через DI.

```java
@Configuration
public class UseCaseConfig {
    @Bean
    public PlaceOrderUseCase placeOrderUseCase(
            OrderRepository orderRepository,
            InventoryPort inventory,
            EventPublisher events) {
        return new PlaceOrderService(orderRepository, inventory, events);
    }
}
```

## Структура пакетов

```text
com.example.shop
├── domain/                     ← Entities
│   ├── order/
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── OrderStatus.java
│   └── inventory/
├── application/                ← Use Cases + Ports
│   ├── order/
│   │   ├── PlaceOrderUseCase.java
│   │   ├── PlaceOrderCommand.java
│   │   └── PlaceOrderService.java
│   └── ports/
│       ├── OrderRepository.java   (output port)
│       └── InventoryPort.java     (output port)
├── adapters/                   ← Interface Adapters
│   ├── in/
│   │   └── web/
│   │       ├── OrderController.java
│   │       └── OrderRequest.java
│   └── out/
│       ├── persistence/
│       │   └── JpaOrderRepository.java
│       └── messaging/
│           └── KafkaEventPublisher.java
└── infrastructure/             ← Frameworks config
    ├── SpringConfig.java
    └── DatabaseConfig.java
```

## Dependency Inversion в деталях

Use Case определяет output port как интерфейс; adapter в Frameworks слое реализует его. Spring DI подключает реализацию.

```text
PlaceOrderService ──uses──> OrderRepository (interface, domain/application)
                                    ▲
                            implements
                                    │
                         JpaOrderRepository (adapters/out)
```

## Тестирование

```java
// Unit test Use Case — никаких Spring, никакого DB
class PlaceOrderServiceTest {
    private final OrderRepository repo = mock(OrderRepository.class);
    private final InventoryPort inventory = mock(InventoryPort.class);
    private final EventPublisher events = mock(EventPublisher.class);

    private final PlaceOrderUseCase useCase =
        new PlaceOrderService(repo, inventory, events);

    @Test
    void shouldPlaceOrderAndPublishEvent() {
        PlaceOrderCommand cmd = new PlaceOrderCommand(customerId, items);

        useCase.execute(cmd);

        verify(repo).save(any(Order.class));
        verify(events).publish(any(OrderPlaced.class));
    }
}
```

Domain и Use Case тесты — быстрые, изолированные, без фреймворков. Adapters тестируются интеграционно (с реальной DB, HTTP).

## Когда применять

**Подходит:**
- Сложная бизнес-логика с множеством правил и сценариев.
- Долгоживущий проект с планируемой заменой инфраструктуры.
- Команда хочет чёткого разделения ответственности и высокого покрытия unit-тестами.

**Излишне:**
- CRUD-приложения с тонкой логикой.
- Прототипы и MVP.
- Маленькие сервисы с одним-двумя сценариями.

## See also

- [[hexagonal-architecture|Hexagonal Architecture]] — порты и адаптеры (схожая идея)
- [[ddd|Domain-Driven Design]] — модели предметной области
- [[solid-principles|SOLID]] — принципы, лежащие в основе
- [[clean-architecture-interview|Clean Architecture Interview]] — вопросы на собеседовании
- [[spring-modulith|Spring Modulith]] — модульный монолит
