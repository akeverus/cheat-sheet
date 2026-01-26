# Domain-Driven Design (DDD)

Domain-Driven Design (DDD) — это подход к разработке программного обеспечения, который фокусируется на моделировании бизнес-домена и использовании единого языка (Ubiquitous Language) для общения между разработчиками и экспертами предметной области. DDD помогает создавать сложные системы, которые точно отражают бизнес-логику.

**Дата последнего обновления:** 2026-01-23

## Полезные ссылки

### Официальная документация
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [DDD Reference](https://www.domainlanguage.com/ddd/reference/)
- [Spring Data DDD Support](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories)

### См. также
- `event-driven.md` - Event-Driven Architecture
- `cqrs.md` - CQRS паттерн
- `event-sourcing.md` - Event Sourcing
- `architecture-patterns.md` - Архитектурные паттерны

## Содержание

- [Введение в Domain-Driven Design](#введение-в-domain-driven-design)
- [Ubiquitous Language](#ubiquitous-language)
- [Bounded Contexts](#bounded-contexts)
- [Entities и Value Objects](#entities-и-value-objects)
- [Aggregates](#aggregates)
- [Domain Services](#domain-services)
- [Repositories](#repositories)
- [Factories](#factories)
- [Domain Events](#domain-events)
- [Application Services](#application-services)
- [Infrastructure Layer](#infrastructure-layer)
- [Реализация на Spring](#реализация-на-spring)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Введение в Domain-Driven Design

Domain-Driven Design — это методология разработки, которая ставит домен (бизнес-логику) в центр внимания. Основная идея заключается в том, что сложность программного обеспечения должна отражать сложность бизнес-домена, а не техническую сложность.

### Основные принципы DDD

**Фокус на домене**
Вся разработка сосредоточена на бизнес-домене и его логике, а не на технических деталях.

**Ubiquitous Language**
Единый язык используется для общения между разработчиками и экспертами предметной области.

**Модель отражает домен**
Программная модель должна точно отражать бизнес-модель.

**Bounded Contexts**
Разные части системы могут иметь разные модели одного и того же понятия.

### Преимущества DDD

- **Понятность** — код отражает бизнес-логику
- **Поддерживаемость** — изменения в домене легко отражаются в коде
- **Тестируемость** — доменная логика изолирована и легко тестируется
- **Масштабируемость** — четкое разделение контекстов позволяет масштабировать систему

### Когда использовать DDD

- Сложная бизнес-логика
- Большая команда разработчиков
- Долгосрочный проект
- Необходимость точного отражения бизнес-правил

## Ubiquitous Language

Ubiquitous Language — это единый язык, используемый всеми участниками проекта (разработчиками, бизнес-аналитиками, экспертами предметной области) для описания домена.

### Примеры Ubiquitous Language

```java
// Плохо: технические термины
public class UserDAO {
    public void insertUser(UserDTO dto) {
        // ...
    }
}

// Хорошо: термины из домена
public class Customer {
    public void register(CustomerRegistration registration) {
        // ...
    }
}

// Примеры терминов из домена электронной коммерции:
// - Customer (не User)
// - Order (не Transaction)
// - Product (не Item)
// - Shopping Cart (не Basket)
// - Checkout (не Payment Process)
```

### Использование Ubiquitous Language в коде

```java
// Доменные термины в коде
public class Order {
    private OrderId orderId;
    private CustomerId customerId;
    private OrderStatus status;
    private List<OrderLine> orderLines;
    private Money totalAmount;
    
    // Методы используют термины домена
    public void confirm() {
        if (status != OrderStatus.DRAFT) {
            throw new OrderCannotBeConfirmedException("Only draft orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
        DomainEventPublisher.publish(new OrderConfirmedEvent(orderId));
    }
    
    public void cancel() {
        if (status == OrderStatus.SHIPPED) {
            throw new OrderCannotBeCancelledException("Shipped orders cannot be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
        DomainEventPublisher.publish(new OrderCancelledEvent(orderId));
    }
}
```

## Bounded Contexts

Bounded Context — это граница, внутри которой определенная модель имеет четкое значение. Разные контексты могут иметь разные модели одного и того же понятия.

### Примеры Bounded Contexts

```java
// Контекст: Order Management
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    
    public void confirm() {
        // Логика подтверждения заказа
    }
}

// Контекст: Shipping
public class Shipment {
    private ShipmentId id;
    private OrderReference orderRef; // Ссылка на заказ из другого контекста
    private Address deliveryAddress;
    private ShippingStatus status;
    
    public void ship() {
        // Логика отправки
    }
}

// Контекст: Billing
public class Invoice {
    private InvoiceId id;
    private OrderReference orderRef;
    private Money amount;
    private InvoiceStatus status;
    
    public void issue() {
        // Логика выставления счета
    }
}
```

### Контекстные карты (Context Mapping)

```java
// Shared Kernel - общий код между контекстами
public class Money {
    private final BigDecimal amount;
    private final Currency currency;
    // Общая реализация для всех контекстов
}

// Customer-Supplier - один контекст зависит от другого
// Order Management зависит от Customer Management
public class Order {
    private CustomerId customerId; // Из контекста Customer Management
    
    public void create(CustomerId customerId) {
        // Валидация через Customer Management контекст
        if (!customerService.exists(customerId)) {
            throw new CustomerNotFoundException();
        }
        this.customerId = customerId;
    }
}

// Anticorruption Layer - защита от изменений в другом контексте
public class CustomerAdapter {
    private final CustomerManagementClient client;
    
    public CustomerInfo getCustomerInfo(CustomerId id) {
        CustomerDTO dto = client.getCustomer(id.getValue());
        return toCustomerInfo(dto); // Преобразование в доменную модель
    }
    
    private CustomerInfo toCustomerInfo(CustomerDTO dto) {
        // Адаптация внешней модели к внутренней
        return new CustomerInfo(
            CustomerId.of(dto.getId()),
            dto.getName(),
            dto.getEmail()
        );
    }
}
```

## Entities и Value Objects

### Entities (Сущности)

Entity — это объект, который имеет уникальный идентификатор и может изменяться со временем.

```java
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private OrderId id; // Уникальный идентификатор
    
    private CustomerId customerId;
    private OrderStatus status;
    private List<OrderLine> orderLines;
    
    // Entity может изменяться
    public void addItem(ProductId productId, Quantity quantity) {
        OrderLine line = new OrderLine(productId, quantity);
        this.orderLines.add(line);
    }
    
    public void confirm() {
        if (this.status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only draft orders can be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
    }
    
    // Равенство определяется по идентификатору
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// Value Object для идентификатора
public class OrderId {
    private final String value;
    
    private OrderId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or blank");
        }
        this.value = value;
    }
    
    public static OrderId of(String value) {
        return new OrderId(value);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
```

### Value Objects (Объекты-значения)

Value Object — это объект, который определяется своими атрибутами, а не идентификатором. Value Objects неизменяемы.

```java
// Value Object: Money
public class Money {
    private final BigDecimal amount;
    private final Currency currency;
    
    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }
    
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add money with different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), this.currency);
    }
    
    public boolean isGreaterThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies");
        }
        return this.amount.compareTo(other.amount) > 0;
    }
    
    // Value Objects сравниваются по значениям
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) && currency == money.currency;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}

// Value Object: Address
public class Address {
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;
    
    public Address(String street, String city, String postalCode, String country) {
        this.street = validateStreet(street);
        this.city = validateCity(city);
        this.postalCode = validatePostalCode(postalCode);
        this.country = validateCountry(country);
    }
    
    private String validateStreet(String street) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or blank");
        }
        return street;
    }
    
    // Address неизменяем, создается новый объект при изменении
    public Address withStreet(String newStreet) {
        return new Address(newStreet, this.city, this.postalCode, this.country);
    }
    
    // Getters
    public String getStreet() {
        return street;
    }
    
    // ...
}
```

## Aggregates

Aggregate — это кластер связанных объектов (Entities и Value Objects), которые рассматриваются как единое целое. Aggregate имеет корневой объект (Aggregate Root), который является единственной точкой доступа к агрегату.

### Правила Aggregates

1. **Единственная точка доступа** — доступ к агрегату только через Aggregate Root
2. **Границы транзакций** — изменения агрегата происходят в одной транзакции
3. **Ссылки на другие агрегаты** — используются идентификаторы, а не прямые ссылки
4. **Неизменяемость вне границ** — объекты внутри агрегата не могут изменяться извне

```java
// Aggregate Root: Order
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private OrderStatus status;
    private List<OrderLine> orderLines; // Часть агрегата
    private Money totalAmount;
    
    // Единственная точка доступа
    public static Order create(CustomerId customerId) {
        Order order = new Order();
        order.id = OrderId.generate();
        order.customerId = customerId;
        order.status = OrderStatus.DRAFT;
        order.orderLines = new ArrayList<>();
        order.totalAmount = Money.zero(Currency.USD);
        return order;
    }
    
    // Изменение агрегата только через методы Aggregate Root
    public void addItem(ProductId productId, Quantity quantity, Money unitPrice) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot add items to non-draft order");
        }
        
        OrderLine line = new OrderLine(productId, quantity, unitPrice);
        orderLines.add(line);
        recalculateTotal();
    }
    
    public void removeItem(OrderLineId lineId) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot remove items from non-draft order");
        }
        
        orderLines.removeIf(line -> line.getId().equals(lineId));
        recalculateTotal();
    }
    
    public void confirm() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only draft orders can be confirmed");
        }
        if (orderLines.isEmpty()) {
            throw new IllegalStateException("Cannot confirm empty order");
        }
        
        this.status = OrderStatus.CONFIRMED;
        DomainEventPublisher.publish(new OrderConfirmedEvent(id, customerId));
    }
    
    private void recalculateTotal() {
        Money total = Money.zero(Currency.USD);
        for (OrderLine line : orderLines) {
            total = total.add(line.getSubtotal());
        }
        this.totalAmount = total;
    }
    
    // Доступ к внутренним объектам только через Aggregate Root
    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }
    
    // Getters для чтения
    public OrderId getId() {
        return id;
    }
    
    public CustomerId getCustomerId() {
        return customerId;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public Money getTotalAmount() {
        return totalAmount;
    }
}

// Entity внутри агрегата: OrderLine
public class OrderLine {
    private OrderLineId id;
    private ProductId productId; // Ссылка на другой агрегат через ID
    private Quantity quantity;
    private Money unitPrice;
    
    public OrderLine(ProductId productId, Quantity quantity, Money unitPrice) {
        this.id = OrderLineId.generate();
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    public Money getSubtotal() {
        return unitPrice.multiply(quantity.getValue());
    }
    
    // Getters
    public OrderLineId getId() {
        return id;
    }
    
    public ProductId getProductId() {
        return productId;
    }
    
    public Quantity getQuantity() {
        return quantity;
    }
}
```

## Domain Services

Domain Service — это операция, которая не принадлежит ни одному Entity или Value Object, но является частью доменной логики.

```java
// Domain Service: Transfer Service
public class MoneyTransferService {
    
    private final AccountRepository accountRepository;
    
    public void transfer(AccountId fromAccountId, AccountId toAccountId, Money amount) {
        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new AccountNotFoundException(fromAccountId));
        
        Account toAccount = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new AccountNotFoundException(toAccountId));
        
        // Бизнес-правила перевода
        if (fromAccount.getBalance().isLessThan(amount)) {
            throw new InsufficientFundsException();
        }
        
        if (fromAccount.getCurrency() != toAccount.getCurrency()) {
            throw new CurrencyMismatchException();
        }
        
        // Выполнение перевода
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}

// Domain Service: Pricing Service
public class PricingService {
    
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;
    
    public Money calculatePrice(Order order) {
        Money total = Money.zero(Currency.USD);
        
        for (OrderLine line : order.getOrderLines()) {
            Product product = productRepository.findById(line.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(line.getProductId()));
            
            Money linePrice = product.getPrice().multiply(line.getQuantity().getValue());
            
            // Применение скидок
            List<Discount> discounts = discountRepository.findApplicableDiscounts(
                order.getCustomerId(),
                line.getProductId()
            );
            
            for (Discount discount : discounts) {
                linePrice = discount.apply(linePrice);
            }
            
            total = total.add(linePrice);
        }
        
        return total;
    }
}
```

## Repositories

Repository — это абстракция для доступа к агрегатам. Repository скрывает детали хранения данных и предоставляет интерфейс, похожий на коллекцию объектов.

```java
// Repository интерфейс в доменном слое
public interface OrderRepository {
    Order findById(OrderId id);
    List<Order> findByCustomerId(CustomerId customerId);
    List<Order> findByStatus(OrderStatus status);
    void save(Order order);
    void delete(OrderId id);
}

// Реализация Repository в инфраструктурном слое
@Repository
public class JpaOrderRepository implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;
    private final OrderMapper mapper;
    
    @Override
    public Order findById(OrderId id) {
        OrderEntity entity = jpaRepository.findById(id.getValue())
            .orElseThrow(() -> new OrderNotFoundException(id));
        return mapper.toDomain(entity);
    }
    
    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        List<OrderEntity> entities = jpaRepository.findByCustomerId(customerId.getValue());
        return entities.stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void save(Order order) {
        OrderEntity entity = mapper.toEntity(order);
        jpaRepository.save(entity);
    }
    
    @Override
    @Transactional
    public void delete(OrderId id) {
        jpaRepository.deleteById(id.getValue());
    }
}

// Использование Spring Data JPA
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByCustomerId(String customerId);
    List<OrderEntity> findByStatus(String status);
}

// Entity для JPA
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private String id;
    private String customerId;
    private String status;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderLineEntity> orderLines;
    
    // Getters and setters
}
```

## Factories

Factory — это объект, ответственный за создание других объектов. Factories используются для инкапсуляции сложной логики создания объектов.

```java
// Factory для создания Order
public class OrderFactory {
    
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    
    public Order createOrder(CreateOrderCommand command) {
        // Валидация
        Customer customer = customerRepository.findById(command.getCustomerId())
            .orElseThrow(() -> new CustomerNotFoundException(command.getCustomerId()));
        
        if (!customer.isActive()) {
            throw new CustomerNotActiveException(customer.getId());
        }
        
        // Создание агрегата
        Order order = Order.create(customer.getId());
        
        // Добавление позиций
        for (CreateOrderLineCommand lineCommand : command.getLines()) {
            Product product = productRepository.findById(lineCommand.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(lineCommand.getProductId()));
            
            if (!product.isAvailable()) {
                throw new ProductNotAvailableException(product.getId());
            }
            
            order.addItem(
                product.getId(),
                Quantity.of(lineCommand.getQuantity()),
                product.getPrice()
            );
        }
        
        return order;
    }
}

// Использование Factory
@Service
public class OrderApplicationService {
    
    private final OrderFactory orderFactory;
    private final OrderRepository orderRepository;
    
    @Transactional
    public OrderId createOrder(CreateOrderCommand command) {
        Order order = orderFactory.createOrder(command);
        orderRepository.save(order);
        return order.getId();
    }
}
```

## Domain Events

Domain Event — это событие, которое произошло в домене и представляет интерес для других частей системы.

```java
// Domain Event
public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    private final String eventType;
    
    protected DomainEvent(String eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.eventType = eventType;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public Instant getOccurredOn() {
        return occurredOn;
    }
    
    public String getEventType() {
        return eventType;
    }
}

public class OrderConfirmedEvent extends DomainEvent {
    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money totalAmount;
    
    public OrderConfirmedEvent(OrderId orderId, CustomerId customerId, Money totalAmount) {
        super("OrderConfirmed");
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
    }
    
    // Getters
}

// Публикация событий из Aggregate
public class Order {
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    public void confirm() {
        // Изменение состояния
        this.status = OrderStatus.CONFIRMED;
        
        // Публикация события
        domainEvents.add(new OrderConfirmedEvent(id, customerId, totalAmount));
    }
    
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
}

// Domain Event Publisher
public class DomainEventPublisher {
    private static final ThreadLocal<List<DomainEventHandler>> handlers = new ThreadLocal<>();
    
    public static void publish(DomainEvent event) {
        List<DomainEventHandler> eventHandlers = handlers.get();
        if (eventHandlers != null) {
            eventHandlers.forEach(handler -> handler.handle(event));
        }
    }
    
    public static void registerHandler(DomainEventHandler handler) {
        handlers.get().add(handler);
    }
}
```

## Application Services

Application Service — это слой, который координирует выполнение доменных операций. Application Services не содержат бизнес-логику, они делегируют выполнение Domain Services и Aggregates.

```java
@Service
@Transactional
public class OrderApplicationService {
    
    private final OrderRepository orderRepository;
    private final OrderFactory orderFactory;
    private final DomainEventPublisher eventPublisher;
    
    public OrderId createOrder(CreateOrderCommand command) {
        // Создание агрегата через Factory
        Order order = orderFactory.createOrder(command);
        
        // Сохранение
        orderRepository.save(order);
        
        // Публикация событий
        order.getDomainEvents().forEach(eventPublisher::publish);
        order.clearDomainEvents();
        
        return order.getId();
    }
    
    public void confirmOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        // Делегирование доменной логике
        order.confirm();
        
        orderRepository.save(order);
        
        // Публикация событий
        order.getDomainEvents().forEach(eventPublisher::publish);
        order.clearDomainEvents();
    }
    
    public OrderView getOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        return OrderView.from(order);
    }
}
```

## Infrastructure Layer

Infrastructure Layer содержит технические детали реализации: доступ к базе данных, внешние API, файловую систему и т.д.

```java
// Infrastructure: JPA Repository
@Repository
public class JpaOrderRepository implements OrderRepository {
    // Реализация с использованием JPA
}

// Infrastructure: External Service Adapter
@Component
public class PaymentGatewayAdapter implements PaymentGateway {
    
    private final RestTemplate restTemplate;
    private final PaymentGatewayConfig config;
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        PaymentGatewayRequest gatewayRequest = toGatewayRequest(request);
        
        ResponseEntity<PaymentGatewayResponse> response = restTemplate.postForEntity(
            config.getUrl() + "/payments",
            gatewayRequest,
            PaymentGatewayResponse.class
        );
        
        return toPaymentResult(response.getBody());
    }
    
    private PaymentGatewayRequest toGatewayRequest(PaymentRequest request) {
        // Адаптация доменной модели к внешнему API
        return new PaymentGatewayRequest(
            request.getAmount().getAmount(),
            request.getAmount().getCurrency().getCurrencyCode(),
            request.getCardNumber()
        );
    }
}

// Infrastructure: Email Service
@Component
public class SmtpEmailService implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Override
    public void sendEmail(Email email) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        
        try {
            helper.setTo(email.getRecipient());
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), email.isHtml());
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send email", e);
        }
    }
}
```

## Реализация на Spring

### Структура проекта

```
src/main/java/com/example/
├── domain/              # Доменный слой
│   ├── model/          # Entities, Value Objects, Aggregates
│   ├── services/       # Domain Services
│   ├── repositories/   # Repository интерфейсы
│   └── events/         # Domain Events
├── application/         # Application Services
├── infrastructure/      # Инфраструктурный слой
│   ├── persistence/   # JPA реализации
│   └── external/      # Внешние интеграции
└── presentation/       # REST контроллеры
```

### Конфигурация

```java
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.infrastructure.persistence")
@EntityScan(basePackages = "com.example.infrastructure.persistence")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### REST Controller

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderApplicationService orderService;
    
    @PostMapping
    public ResponseEntity<OrderId> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = CreateOrderCommand.builder()
            .customerId(CustomerId.of(request.getCustomerId()))
            .lines(request.getLines().stream()
                .map(line -> CreateOrderLineCommand.builder()
                    .productId(ProductId.of(line.getProductId()))
                    .quantity(line.getQuantity())
                    .build())
                .collect(Collectors.toList()))
            .build();
        
        OrderId orderId = orderService.createOrder(command);
        return ResponseEntity.ok(orderId);
    }
    
    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable String id) {
        orderService.confirmOrder(OrderId.of(id));
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderView> getOrder(@PathVariable String id) {
        OrderView order = orderService.getOrder(OrderId.of(id));
        return ResponseEntity.ok(order);
    }
}
```

## Best Practices

### 1. Избегайте Anemic Domain Model

```java
// Плохо: Anemic Domain Model
public class Order {
    private String id;
    private String status;
    // Только геттеры и сеттеры, логика в сервисах
}

// Хорошо: Rich Domain Model
public class Order {
    private OrderId id;
    private OrderStatus status;
    
    public void confirm() {
        // Логика в доменной модели
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException();
        }
        this.status = OrderStatus.CONFIRMED;
    }
}
```

### 2. Используйте Value Objects для примитивов

```java
// Плохо: примитивные типы
public class Order {
    private String customerId;
    private BigDecimal totalAmount;
}

// Хорошо: Value Objects
public class Order {
    private CustomerId customerId;
    private Money totalAmount;
}
```

### 3. Защищайте инварианты

```java
public class Order {
    public void addItem(ProductId productId, Quantity quantity) {
        // Защита инвариантов
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot add items to non-draft order");
        }
        if (quantity.getValue() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        // Добавление позиции
        orderLines.add(new OrderLine(productId, quantity));
    }
}
```

### 4. Используйте Domain Events для слабой связанности

```java
public class Order {
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
        DomainEventPublisher.publish(new OrderConfirmedEvent(id, customerId));
    }
}
```

## Troubleshooting

### Проблема: Слишком большие Aggregates

**Решение:** Разбейте агрегат на несколько меньших агрегатов.

### Проблема: Anemic Domain Model

**Решение:** Переместите бизнес-логику из сервисов в доменные модели.

### Проблема: Нарушение границ Bounded Context

**Решение:** Используйте Context Mapping для определения отношений между контекстами.

---

**Дата последнего обновления:** 2026-01-23

