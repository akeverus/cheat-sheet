---
title: "Hexagonal Architecture (Ports & Adapters)"
description: "Hexagonal Architecture: порты, адаптеры, изоляция домена, применение в Java/Spring Boot."
tags:
  - "architecture"
  - "hexagonal-architecture"
  - "ports-and-adapters"
  - "design-patterns"
type: "reference"
difficulty: "intermediate"
aliases:
  - "Hexagonal Architecture (Ports & Adapters)"
  - "hexagonal architecture"
updated: "2026-04-20"
---
# Hexagonal Architecture (Ports & Adapters)

Hexagonal Architecture (Алистер Кокбёрн, 2005) — архитектурный стиль, изолирующий бизнес-логику от внешних систем через **порты** (интерфейсы) и **адаптеры** (реализации).

Другое название: **Ports & Adapters**. Гексагон — условная форма: у приложения много «сторон», через каждую из которых можно войти или выйти.

## Полезные ссылки

### Официальная документация
- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/) — оригинальная статья автора

### См. также
- [Clean Architecture](clean-architecture.md) — схожий подход Роберта Мартина
- [SOLID](design-principles/solid-principles.md) — принципы, лежащие в основе
- [Паттерны создания](../patterns/creational/creational-patterns.md) — паттерны GoF
- [Микросервисная архитектура](software-architecture/microservices.md) — применение Hexagonal в микросервисах

## Содержание

- [Концепция](#концепция)
- [Domain + Ports](#domain-ports)
- [Application (Use Case)](#application-use-case)
- [Adapters](#adapters)
  - [Driving Adapter (REST)](#driving-adapter-rest)
  - [Driven Adapter (JPA)](#driven-adapter-jpa)
  - [Driven Adapter (Kafka)](#driven-adapter-kafka)
- [Структура пакетов](#структура-пакетов)
- [Тестирование](#тестирование)
- [Hexagonal vs Clean Architecture](#hexagonal-vs-clean-architecture)
- [Преимущества и недостатки](#преимущества-и-недостатки)
- [See also](#see-also)

## Концепция

```text
         [ HTTP Client ]    [ CLI ]
               |               |
         [ REST Adapter ] [ CLI Adapter ]   ← Driving Adapters (входящие)
               |               |
     ┌─────────▼───────────────▼─────────┐
     │   PRIMARY PORT (Input Port)       │
     │                                   │
     │         APPLICATION               │
     │         (Domain + Use Cases)      │
     │                                   │
     │   SECONDARY PORT (Output Port)    │
     └─────────┬───────────────┬─────────┘
               |               |
        [ JPA Adapter ] [ Kafka Adapter ]   ← Driven Adapters (исходящие)
               |               |
         [ PostgreSQL ]     [ Kafka ]
```

- **Primary Ports** (Driving / Input Ports) — интерфейсы, которые приложение **предоставляет** внешнему миру (Use Case interfaces).
- **Secondary Ports** (Driven / Output Ports) — интерфейсы, которые приложение **использует** (Repository, EventPublisher).
- **Adapters** — реализации портов (REST controller, JPA repository, CLI handler).

## Domain + Ports

```java
// === DOMAIN ===
public class Product {
    private final ProductId id;
    private String name;
    private Money price;
    private int stock;

    public void reduceStock(int quantity) {
        if (quantity > stock) throw new InsufficientStockException(id, quantity, stock);
        this.stock -= quantity;
    }
}

// === PRIMARY PORT (входящий — что умеет приложение) ===
public interface CreateOrderPort {
    OrderId createOrder(CustomerId customerId, List<OrderLine> lines);
}

// === SECONDARY PORTS (исходящие — что приложению нужно) ===
public interface ProductRepository {
    Optional<Product> findById(ProductId id);
    void save(Product product);
}

public interface OrderRepository {
    void save(Order order);
}

public interface OrderEventPort {
    void publish(OrderCreated event);
}
```

## Application (Use Case)

Use Case реализует primary port и использует secondary ports — только через интерфейсы.

```java
@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderPort {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderEventPort eventPort;

    @Override
    @Transactional
    public OrderId createOrder(CustomerId customerId, List<OrderLine> lines) {
        // Доменная логика
        List<Product> products = lines.stream()
            .map(line -> productRepository.findById(line.productId())
                .orElseThrow(() -> new ProductNotFoundException(line.productId())))
            .toList();

        Order order = Order.create(customerId, lines, products);
        products.forEach(p -> productRepository.save(p));
        orderRepository.save(order);
        eventPort.publish(new OrderCreated(order.getId()));
        return order.getId();
    }
}
```

## Adapters

### Driving Adapter (REST)

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestAdapter {
    private final CreateOrderPort createOrder;  // ← зависит от порта, не от сервиса

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest req) {
        OrderId id = createOrder.createOrder(
            new CustomerId(req.customerId()),
            OrderLineMapper.toLines(req.items())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new OrderResponse(id.value()));
    }
}
```

### Driven Adapter (JPA)

```java
@Repository
@RequiredArgsConstructor
public class JpaProductRepository implements ProductRepository {
    private final ProductJpaRepository jpa;
    private final ProductMapper mapper;

    @Override
    public Optional<Product> findById(ProductId id) {
        return jpa.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public void save(Product product) {
        jpa.save(mapper.toEntity(product));
    }
}
```

### Driven Adapter (Kafka)

```java
@Component
@RequiredArgsConstructor
public class KafkaOrderEventAdapter implements OrderEventPort {
    private final KafkaTemplate<String, OrderCreatedEvent> kafka;

    @Override
    public void publish(OrderCreated event) {
        kafka.send("orders.created", event.orderId().toString(),
            new OrderCreatedEvent(event.orderId(), event.timestamp()));
    }
}
```

## Структура пакетов

```text
com.example.shop
├── domain/
│   ├── model/          ← Order, Product, Customer, value objects
│   └── exception/      ← InsufficientStockException и т.д.
├── application/
│   ├── port/
│   │   ├── in/         ← Primary ports (use case interfaces)
│   │   │   └── CreateOrderPort.java
│   │   └── out/        ← Secondary ports
│   │       ├── ProductRepository.java
│   │       └── OrderEventPort.java
│   └── service/        ← Use case implementations
│       └── CreateOrderService.java
└── adapter/
    ├── in/
    │   └── web/        ← REST controllers
    │       └── OrderRestAdapter.java
    └── out/
        ├── persistence/  ← JPA adapters
        │   └── JpaProductRepository.java
        └── messaging/    ← Kafka adapters
            └── KafkaOrderEventAdapter.java
```

## Тестирование

```java
// Unit test — только домен и use case, без Spring
class CreateOrderServiceTest {
    private final ProductRepository products = mock(ProductRepository.class);
    private final OrderRepository orders = mock(OrderRepository.class);
    private final OrderEventPort events = mock(OrderEventPort.class);

    private final CreateOrderPort useCase =
        new CreateOrderService(products, orders, events);

    @Test
    void shouldCreateOrderAndPublishEvent() {
        when(products.findById(any())).thenReturn(Optional.of(aProduct()));

        OrderId id = useCase.createOrder(customerId, orderLines);

        assertThat(id).isNotNull();
        verify(orders).save(any(Order.class));
        verify(events).publish(any(OrderCreated.class));
    }
}

// Integration test — только persistence adapter
@DataJpaTest
class JpaProductRepositoryTest {
    @Autowired
    private ProductJpaRepository jpa;

    private JpaProductRepository adapter;

    @BeforeEach
    void setUp() {
        adapter = new JpaProductRepository(jpa, new ProductMapper());
    }

    @Test
    void shouldPersistAndRetrieveProduct() { ... }
}
```

## Hexagonal vs Clean Architecture

| Критерий | Hexagonal | Clean Architecture |
|----------|-----------|-------------------|
| Автор | Алистер Кокбёрн | Роберт Мартин |
| Слои | 3 (domain, application, adapters) | 4 (entities, use cases, adapters, frameworks) |
| Терминология | Ports & Adapters | Use Cases, Gateways, Controllers |
| Детализация | Меньше правил | Строгие правила зависимостей |
| Совместимость | Совместимы | Совместимы |

На практике оба подхода часто применяют вместе — они описывают одну и ту же идею на разных уровнях детализации.

## Преимущества и недостатки

**Преимущества:**
- Бизнес-логика независима от фреймворка, БД, протокола.
- Лёгкое тестирование домена и use cases без Spring context.
- Можно заменить адаптер (PostgreSQL → MongoDB) без изменения бизнес-логики.
- Явные boundaries между слоями.

**Недостатки:**
- Больше кода и классов (mapping между слоями).
- Избыточно для простых CRUD-сервисов.
- Требует дисциплины команды.

## Антипаттерны

Типовые ошибки при внедрении Hexagonal:

| Антипаттерн | Почему плохо | Как исправить |
|---|---|---|
| Один порт, у которого 20 методов | Превращается в God-интерфейс, одни клиенты используют половину | ISP: разделить на узкие порты по cases (`CreateOrderPort`, `CancelOrderPort`) |
| Output port возвращает JPA-сущность | Domain протекает в инфраструктуру | Adapter маппит JPA в domain-объект; в port — только domain |
| Driving adapter знает про Output ports напрямую | Нарушает направление зависимостей | REST-adapter общается с Application через Input port; Output ports внутри Application |
| Spring-аннотации в port-интерфейсах (`@Cacheable`) | Привязка к Spring | Аннотации — на реализации (adapter или service), не на интерфейсе |
| `domain` импортирует Spring | Domain должен быть pure-Java | Spring живёт в `infrastructure/`; домен зависит только от JDK + Lombok |
| Маппинг inline в use case (`return new Dto(order.id(), ...)` ) | Use case становится грязным | Отдельный mapper-класс per adapter |

## ArchUnit для контроля Ports & Adapters

```java
@AnalyzeClasses(packages = "com.example.shop")
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domainHasNoSpringDependencies =
            classes().that().resideInAPackage("..domain..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage(
                            "..domain..", "java..", "lombok..");

    @ArchTest
    static final ArchRule applicationOnlyDependsOnDomain =
            classes().that().resideInAPackage("..application..")
                    .should().onlyDependOnClassesThat().resideInAnyPackage(
                            "..application..", "..domain..", "java..", "lombok..",
                            "org.springframework.stereotype..", "org.springframework.transaction..");

    @ArchTest
    static final ArchRule adaptersImplementPorts =
            classes().that().resideInAPackage("..adapter.out..")
                    .and().areAnnotatedWith("org.springframework.stereotype.Component")
                    .or().areAnnotatedWith("org.springframework.stereotype.Repository")
                    .should().implement(JavaClass.Predicates.resideInAPackage("..application.port.out.."));
}
```

Без этих правил hexagonal быстро размывается: Spring проникает в
domain, port-интерфейсы превращаются в utility-классы, маппинг
расползается по контроллерам.

## See also

- [Clean Architecture](clean-architecture.md) — схожий подход Роберта Мартина
- [Domain-Driven Design](ddd.md) — Bounded Context, Aggregate
- [SOLID принципы](design-principles/solid-principles.md) — Dependency Inversion
- [Hexagonal Architecture Interview](../interview/architecture/hexagonal-architecture-interview.md) — вопросы на собеседовании
- [Spring Modulith](../frameworks/java-frameworks/spring/spring-modulith.md) — модульный монолит
