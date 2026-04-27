---
title: "Clean Architecture"
description: "Clean Architecture Роберта Мартина: слои, зависимости, Dependency Rule, применение в Java/Spring."
tags:
  - "architecture"
  - "clean-architecture"
  - "solid"
  - "design-patterns"
type: "reference"
difficulty: "intermediate"
aliases:
  - "Clean Architecture"
updated: "2026-04-20"
---
# Clean Architecture

Clean Architecture (Роберт Мартин, «Дядя Боб») — архитектурный подход, в котором **бизнес-логика не зависит от фреймворков, БД и UI**. Зависимости направлены строго внутрь: внешние слои зависят от внутренних, никогда наоборот.

## Полезные ссылки

### Официальная документация
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) — оригинальная статья дяди Боба

### См. также
- [Hexagonal Architecture](hexagonal-architecture.md) — архитектура «Порты и адаптеры»
- [SOLID](design-principles/solid-principles.md) — принципы, лежащие в основе
- [Паттерны создания](../patterns/creational/creational-patterns.md) — паттерны GoF
- [Микросервисная архитектура](software-architecture/microservices.md) — применение Clean Architecture в микросервисах

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

## Сравнение с другими архитектурами

| Подход | Слои | Где живёт бизнес-логика | Зависимости |
|---|---|---|---|
| Layered (классический) | Controller → Service → Repository → DB | В Service | Сверху вниз; Service знает Repository (JPA) |
| Clean Architecture | Frameworks → Adapters → Use Cases → Entities | В Entities + Use Cases | Только внутрь; внутренние слои не знают внешних |
| Hexagonal (Ports & Adapters) | Adapters → Application → Domain | В Domain + Application | Domain в центре, изолирован через Ports |
| Onion | Infrastructure → Application → Domain Services → Domain Model | В Domain Model | Кольца, наружу нельзя |

Clean, Hexagonal и Onion — родственные: одна идея «зависимости только
внутрь», разный уровень детализации. На практике их часто комбинируют,
суть одна — изолировать домен от инфраструктуры.

## Миграция от Layered к Clean

Типовой путь, когда CRUD-приложение «выросло» и Service-слой стал
неуправляемым:

1. **Шаг 1 — выделить домен.** Создать `domain/` с чистыми классами
   (POJO без JPA). Логика, охраняющая инварианты, переезжает в эти
   классы из Service.
2. **Шаг 2 — Use Cases.** Создать `application/usecase/` с `*UseCase`
   интерфейсами. Service-методы переименовать и перенести как реализации.
3. **Шаг 3 — Output Ports.** Из Service вытащить интерфейсы
   `*Repository`, `*Gateway` в `application/port/out/`. JPA-реализации
   ушли в `adapters/out/persistence/` и реализуют эти порты.
4. **Шаг 4 — Mapping.** Между `domain.Order` (чистый) и
   `infrastructure.OrderEntity` (JPA) — отдельный mapper в adapter.
   Никаких `@Entity` в `domain/`.
5. **Шаг 5 — DI.** Spring подключает реализации к интерфейсам
   автоматически (через `@Component`). Конфигурация — в `infrastructure/`.

Не пытайся мигрировать всё сразу. Применяй паттерн к новым use case-ам
и постепенно переноси старые при изменениях.

## Антипаттерны

| Антипаттерн | Почему плохо | Как исправить |
|---|---|---|
| `@Entity` в domain-классах | Привязка домена к JPA, нельзя сменить ORM | Domain — чистый POJO, отдельная `*Entity` в `infrastructure/persistence/` |
| Use Case вызывает Spring-аннотации (`@Cacheable`, `@Transactional`) на самом use case | Аспекты привязывают к Spring | Применять аспекты на adapter-слое или через контракт Use Case |
| Use Case возвращает JPA Entity | Слой adapters протекает | Возвращать domain-объекты или dedicated DTO |
| Adapters → Use Cases в обе стороны | Циклическая зависимость, нарушает Dependency Rule | Output ports наружу, input ports внутрь — проверить ArchUnit правилами |
| Один большой "facade" Use Case со всем подряд | Use Case теряет смысл, превращается в Service | Один use case — одна бизнес-операция; делить по агрегатам |
| Mapping между слоями раскидан по контроллерам и сервисам | Дублирование, рассинхронизация | Отдельные mapper-классы (`OrderMapper`) в каждом adapter |
| Domain-логика в маппере | Нарушение SRP, маппер становится сервисом | Маппер — только конвертация; логика — в domain или use case |

## ArchUnit для контроля зависимостей

Без автоматического контроля Dependency Rule быстро размывается.
ArchUnit позволяет зафиксировать правила как тесты:

```java
@AnalyzeClasses(packages = "com.example.shop")
class CleanArchitectureRulesTest {

    @ArchTest
    static final ArchRule domainHasNoSpringDependencies =
            classes().that().resideInAPackage("..domain..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage(
                            "..domain..", "java..", "lombok..");

    @ArchTest
    static final ArchRule applicationDoesNotKnowAdapters =
            noClasses().that().resideInAPackage("..application..")
                    .should().dependOnClassesThat().resideInAPackage("..adapter..");

    @ArchTest
    static final ArchRule cleanLayers =
            layeredArchitecture()
                    .layer("Domain").definedBy("..domain..")
                    .layer("Application").definedBy("..application..")
                    .layer("Adapters").definedBy("..adapter..")
                    .whereLayer("Domain").mayNotAccessAnyLayer()
                    .whereLayer("Application").mayOnlyAccessLayers("Domain")
                    .whereLayer("Adapters").mayOnlyAccessLayers("Application", "Domain");
}
```

Эти правила превращают «договорённость» в зелёный/красный тест.
В CI они защищают от ошибок ревью и эволюционных компромиссов.

## See also

- [Hexagonal Architecture](hexagonal-architecture.md) — порты и адаптеры (схожая идея)
- [Domain-Driven Design](ddd.md) — модели предметной области
- [SOLID](design-principles/solid-principles.md) — принципы, лежащие в основе
- [Clean Architecture Interview](../interview/architecture/clean-architecture-interview.md) — вопросы на собеседовании
- [Spring Modulith](../frameworks/java-frameworks/spring/spring-modulith.md) — модульный монолит
