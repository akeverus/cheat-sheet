---
title: "Enterprise Patterns — обзор"
description: "Обзор паттернов корпоративной архитектуры: представление данных (Repository, UoW, Active Record vs Data Mapper, Specification), бизнес-логика, интеграция (Gateway, ACL, Outbox), распределённые системы (Circuit Breaker, Saga). Для каждого: код, когда применять, trade-offs."
tags:
  - architecture
  - enterprise-patterns
  - enterprise-patterns-overview
  - design-patterns
  - integration
type: "overview"
difficulty: "intermediate"
aliases:
  - "Enterprise Patterns — обзор"
  - "enterprise patterns overview"
prerequisites:
  - "[[design-principles]]"
related:
  - "[[architecture-patterns]]"
  - "[[ddd]]"
next:
  - "[[system-design-basics]]"
updated: "2026-04-26"
---
# Enterprise Patterns — обзор

Enterprise Patterns — это устоявшиеся решения для типовых задач крупных
приложений: доступ к данным, бизнес-логика, интеграция и распределённые
транзакции. Большинство пришло из «Patterns of Enterprise Application
Architecture» Мартина Фаулера (2003), но остаются релевантными — Spring,
Hibernate, JPA, Resilience4j опираются ровно на них.

Раздел даёт каркас: что такое каждый паттерн, как он выглядит в коде на
Spring/Java, когда применять, какие trade-offs. Глубокое погружение по
каждому — в соседних документах (`ddd.md`, `architecture-patterns.md`,
`interview/architecture/`).

## Полезные ссылки

- [Patterns of Enterprise Application Architecture (Martin Fowler)](https://martinfowler.com/books/eaa.html) — оригинал
- [Enterprise Integration Patterns (Hohpe, Woolf)](https://www.enterpriseintegrationpatterns.com/) — каноничные intеграционные паттерны
- [microservices.io patterns](https://microservices.io/patterns/) — современные распределённые паттерны
- [Refactoring Guru: Design Patterns](https://refactoring.guru/design-patterns) — хорошая визуализация GoF-паттернов

### См. также

- [Архитектурные паттерны](../architecture-patterns.md) — стили архитектуры (монолит, микросервисы)
- [DDD](../ddd.md) — стратегические и тактические DDD-паттерны
- [System Design Basics](../system-design/system-design-basics.md)
- [Resilience patterns (interview)](../../interview/architecture/resilience-patterns-interview.md)

## Содержание

- [Что такое Enterprise Patterns](#что-такое-enterprise-patterns)
- [Паттерны представления данных](#паттерны-представления-данных)
  - [Repository](#repository)
  - [Unit of Work](#unit-of-work)
  - [Active Record vs Data Mapper](#active-record-vs-data-mapper)
  - [Specification](#specification)
- [Паттерны бизнес-логики](#паттерны-бизнес-логики)
  - [Service Layer](#service-layer)
  - [Domain Model](#domain-model)
  - [Anemic vs Rich Domain](#anemic-vs-rich-domain)
- [Паттерны интеграции](#паттерны-интеграции)
  - [Gateway](#gateway)
  - [Anti-Corruption Layer](#anti-corruption-layer)
  - [Message Channel](#message-channel)
  - [Transactional Outbox](#transactional-outbox)
- [Паттерны распределённых систем](#паттерны-распределённых-систем)
  - [Circuit Breaker](#circuit-breaker)
  - [Retry](#retry)
  - [Bulkhead](#bulkhead)
  - [Saga](#saga)
- [Когда использовать Enterprise Patterns](#когда-использовать-enterprise-patterns)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [См. также](#см-также-1)

## Что такое Enterprise Patterns

Enterprise Patterns решают задачи, которые повторяются во всех крупных
приложениях:

- Как разделить бизнес-логику и инфраструктуру.
- Как сохранять и читать данные не размазывая SQL по контроллерам.
- Как интегрироваться с внешними системами без жёсткой связности.
- Как пережить отказ соседнего сервиса без каскадного падения.

Применять их «по умолчанию» не нужно — каждый паттерн добавляет
сложность, и для CRUD-сервиса с одной БД часть паттернов будет
ovеrengineering. Правило: внедряем паттерн, когда видим конкретную
боль, которую он лечит.

## Паттерны представления данных

### Repository

**Цель.** Абстракция доступа к данным: бизнес-логика работает с
коллекцией объектов, а не с SQL.

```java
public interface UserRepository {
    Optional<User> findById(Long id);
    List<User> findAll();
    User save(User user);
    void deleteById(Long id);
}

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final EntityManager em;

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    public User save(User user) {
        return em.merge(user);
    }
}
```

**Когда применять.**

- Бизнес-логика должна тестироваться без БД (мок репозитория).
- Возможна замена реализации (JPA → JDBC, Postgres → Mongo).
- Хочешь иметь чёткий контракт работы с агрегатом из DDD.

**Когда НЕ применять.**

- Простой CRUD без сложной бизнес-логики — Spring Data `JpaRepository`
  избыточно достаточен, ручной интерфейс лишний.

**Trade-offs.** Дополнительная абстракция стоит копипасты
интерфейс/реализация. На простых случаях этот оверхед не окупается.
В Spring Data уже встроен Repository паттерн — ручные интерфейсы делают
только когда нужна полная независимость от Spring Data.

### Unit of Work

**Цель.** Накапливать изменения и применять одной транзакцией —
консистентность и снижение количества round-trip-ов в БД.

```java
class UnitOfWork {
    private final List<Entity> newEntities = new ArrayList<>();
    private final List<Entity> modified = new ArrayList<>();
    private final List<Entity> deleted = new ArrayList<>();

    public void registerNew(Entity e) { newEntities.add(e); }
    public void registerDirty(Entity e) { modified.add(e); }
    public void registerDeleted(Entity e) { deleted.add(e); }

    @Transactional
    public void commit() {
        newEntities.forEach(em::persist);
        modified.forEach(em::merge);
        deleted.forEach(em::remove);
    }
}
```

**Когда применять.**

- Сложные use case, изменяющие несколько агрегатов в одной транзакции.
- Нужно явно контролировать порядок и группировку изменений.

**Когда НЕ применять.**

- В Spring/JPA эту роль играет `@Transactional` + `EntityManager` —
  реализовывать вручную чаще всего overengineering. Используй Spring,
  пока не упёрся в его ограничения.

### Active Record vs Data Mapper

Два подхода к маппингу объект-таблица.

**Active Record:** объект знает, как сохраниться. Удобно для простых
CRUD, плохо для сложного домена.

```java
public class User {
    private Long id;
    private String email;

    public void save() { /* SQL.UPDATE ... WHERE id = ? */ }
    public static User findById(Long id) { /* SQL.SELECT ... */ }
}
```

**Data Mapper:** объект ничего не знает о персистентности — за него это
делает отдельный mapper. JPA, Hibernate, Spring Data — это Data Mapper.

```java
public class User {
    private Long id;
    private String email;
    // только бизнес-методы, никакого SQL
}

@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
```

| Критерий | Active Record | Data Mapper |
|---|---|---|
| Сложность | Низкая | Выше |
| Тестируемость домена | Сложно (нужна БД или mock) | Просто (pure POJO) |
| Подходит для DDD | Плохо (нарушает SRP) | Идеально |
| Примеры | Rails ActiveRecord, Laravel Eloquent | Spring Data JPA, Hibernate, MikroORM |

В Java/Spring экосистеме Data Mapper — стандарт. Active Record почти
не встречается.

### Specification

**Цель.** Инкапсуляция бизнес-правил выборки, чтобы их можно было
переиспользовать и комбинировать.

```java
public interface Specification<T> {
    boolean isSatisfiedBy(T candidate);
    default Specification<T> and(Specification<T> other) {
        return c -> this.isSatisfiedBy(c) && other.isSatisfiedBy(c);
    }
}

class ActiveUserSpecification implements Specification<User> {
    public boolean isSatisfiedBy(User u) {
        return u.isEnabled() && !u.isLocked();
    }
}

class PremiumUserSpecification implements Specification<User> {
    public boolean isSatisfiedBy(User u) {
        return u.getSubscription().isPremium();
    }
}

// Композиция:
Specification<User> spec = new ActiveUserSpecification().and(new PremiumUserSpecification());
```

В Spring Data — `org.springframework.data.jpa.domain.Specification` с
поддержкой Criteria API:

```java
public class UserSpecs {
    public static Specification<User> isActive() {
        return (root, q, cb) -> cb.and(
            cb.isTrue(root.get("enabled")),
            cb.isFalse(root.get("locked")));
    }
}

repository.findAll(UserSpecs.isActive().and(UserSpecs.isPremium()));
```

**Когда применять.** Когда условия выборки повторяются и требуют
динамической композиции (сложные фильтры в admin-panel, бизнес-правила
из ТЗ типа «активный премиум-пользователь без долгов»).

**Когда НЕ применять.** Простые статические запросы — Spring Data
derived queries или JPQL читаются проще.

## Паттерны бизнес-логики

### Service Layer

**Цель.** Точка входа для use case: оркестрирует репозитории, применяет
транзакции, бросает доменные события.

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    private final ApplicationEventPublisher events;

    @Transactional
    public Order createOrder(OrderRequest request) {
        validateRequest(request);
        inventoryService.reserve(request.items());
        Order order = Order.from(request);
        paymentService.charge(order);
        Order saved = orderRepository.save(order);
        events.publishEvent(new OrderCreatedEvent(saved.getId()));
        return saved;
    }
}
```

**Когда применять.** Use case затрагивает несколько агрегатов или
требует чёткой границы транзакции. То есть — практически всегда в
business-сервисе сложнее CRUD.

### Domain Model

**Цель.** Бизнес-логика живёт внутри объекта домена, а не размазана по
сервисам. Объект защищает свои инварианты.

```java
public class Order {
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public void addItem(Product product, int quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Cannot modify confirmed order");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        items.add(new OrderItem(product, quantity));
    }

    public Money calculateTotal() {
        return items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(Money.ZERO, Money::add);
    }

    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Empty order cannot be confirmed");
        }
        this.status = OrderStatus.CONFIRMED;
    }
}
```

`Order` гарантирует, что:

- Нельзя добавить item в подтверждённый заказ.
- Нельзя добавить отрицательное количество.
- Нельзя подтвердить пустой заказ.

Эти инварианты — часть `Order`, а не разбросаны по сервисам и
контроллерам.

### Anemic vs Rich Domain

**Anemic Domain Model** — объект-DTO с геттерами/сеттерами, вся логика
в сервисах. Антипаттерн (по Фаулеру), но широко распространён в
Spring-проектах из-за привычки к JavaBeans.

```java
// Anemic — плохо
public class Order {
    private List<OrderItem> items;
    private OrderStatus status;
    // только getters/setters
}

@Service
public class OrderService {
    public void addItem(Order order, Product p, int qty) {
        if (order.getStatus() != OrderStatus.DRAFT) throw ...
        order.getItems().add(new OrderItem(p, qty));
    }
}
```

**Rich Domain Model** — логика в самой модели (как в `Domain Model`
выше). Сервис только координирует, не дублирует правила.

| Признак anemic | Что делать |
|---|---|
| Сервис проверяет инварианты, которые должна охранять сама модель | Перенести проверку в метод модели |
| Все поля имеют setters | Сделать поля private final, изменения через бизнес-методы |
| Бизнес-правила в нескольких местах | Централизовать в модели |
| Объект не делает ничего, только хранит данные | Это DTO, не модель |

Anemic уместен в случаях: DTO для API, проекции для отчётов, простой
CRUD без бизнес-правил. В этих случаях это не антипаттерн.

## Паттерны интеграции

### Gateway

**Цель.** Изолировать внешнюю систему за интерфейсом. Внутренний код
работает с доменными терминами, а не с API провайдера.

```java
public interface PaymentGateway {
    PaymentResult charge(Money amount, PaymentMethod method);
    PaymentResult refund(String transactionId, Money amount);
}

@Component
@RequiredArgsConstructor
public class StripePaymentGateway implements PaymentGateway {

    private final StripeClient stripeClient;

    public PaymentResult charge(Money amount, PaymentMethod method) {
        ChargeCreateParams params = ChargeCreateParams.builder()
            .setAmount(amount.toMinorUnits())
            .setCurrency(amount.currency().getCode())
            .setSource(method.token())
            .build();
        Charge charge = stripeClient.charges().create(params);
        return new PaymentResult(charge.getId(), charge.getStatus());
    }
}
```

**Когда применять.** Любая интеграция с внешним API. Без gateway
Stripe-объекты протекают в бизнес-код и привязывают тебя к Stripe.

### Anti-Corruption Layer

**Цель.** Защита домена от внешней модели данных. Особенно важно при
интеграции с legacy-системой, чья модель не соответствует твоей.

```java
// Внешняя legacy-система отдаёт записи в чудовищном формате
public class LegacyCustomerDto {
    public String CUST_NAME;     // первые 30 символов имени
    public String CUST_NM_EXT;   // продолжение имени
    public String CUST_TYP;      // "I" — individual, "C" — corporate
    public Date REG_DT;          // дата в локальном tz, часть полей null
}

// Anti-corruption layer переводит в наш домен
@Component
public class CustomerTranslator {
    public Customer fromLegacy(LegacyCustomerDto dto) {
        String fullName = (dto.CUST_NAME + nullSafe(dto.CUST_NM_EXT)).trim();
        CustomerType type = "C".equals(dto.CUST_TYP)
            ? CustomerType.CORPORATE
            : CustomerType.INDIVIDUAL;
        Instant registeredAt = dto.REG_DT == null ? null : dto.REG_DT.toInstant();
        return new Customer(fullName, type, registeredAt);
    }
}
```

**Когда применять.** Интеграция с системой, чью модель ты не
контролируешь, особенно если она устроена «неправильно». Один
translator-класс на источник.

### Message Channel

**Цель.** Асинхронная коммуникация между компонентами через брокер
сообщений. Снижает связность и даёт буферизацию при пиковых нагрузках.

```java
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(), order.getTotal(), order.getCustomerId());
        rabbitTemplate.convertAndSend("order.events", "order.created", event);
    }
}

@Component
public class InventoryListener {
    @RabbitListener(queues = "inventory.order-created")
    public void onOrderCreated(OrderCreatedEvent event) {
        inventoryService.reserveForOrder(event.orderId(), event.items());
    }
}
```

**Когда применять.** Слабая связность между сервисами, неравномерная
нагрузка, fan-out (одно событие — несколько потребителей).

### Transactional Outbox

**Цель.** Решает классическую проблему: «как сохранить изменение в БД
И отправить сообщение в брокер атомарно?». Прямой подход (`save` +
`publish`) ломается, если приложение упадёт между ними.

```text
┌──────────┐  insert order + insert outbox_event
│ DB tx    │  (одна транзакция)
└──────────┘
      │
      ▼
┌──────────┐
│ outbox   │  poller / CDC читает таблицу
│ table    │
└──────────┘
      │
      ▼
┌──────────┐
│  Kafka   │  publish, mark as sent
└──────────┘
```

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(OrderRequest request) {
        Order order = orderRepository.save(Order.from(request));

        OutboxEvent event = new OutboxEvent(
            "order.created",
            order.getId().toString(),
            objectMapper.writeValueAsString(order));
        outboxRepository.save(event);

        return order;
    }
}

// Отдельный poller или Debezium через CDC отправляет события из outbox
```

**Когда применять.** Любые сервисы, которые публикуют доменные события
по результатам транзакций — это стандарт для микросервисной интеграции
без распределённых транзакций.

## Паттерны распределённых систем

### Circuit Breaker

**Цель.** Защита от каскадных сбоев: если соседний сервис «лежит» — не
бомбить его повторными запросами, а быстро возвращать ошибку.

```java
@Component
public class PaymentClient {

    @CircuitBreaker(name = "payment", fallbackMethod = "fallback")
    public PaymentResult charge(Money amount) {
        return paymentApi.charge(amount);
    }

    public PaymentResult fallback(Money amount, Throwable t) {
        return PaymentResult.deferred(amount);
    }
}
```

```yaml
resilience4j.circuitbreaker:
  instances:
    payment:
      failure-rate-threshold: 50
      wait-duration-in-open-state: 30s
      sliding-window-size: 20
```

Состояния: `CLOSED` (нормально), `OPEN` (запросы сразу падают),
`HALF_OPEN` (пробный запрос — если успех, обратно в CLOSED).

**Когда применять.** Любой синхронный вызов внешнего сервиса в
production. Стандарт для микросервисов.

### Retry

**Цель.** Преодолеть transient failures (kratкие сетевые сбои, 503).

```java
@Retry(name = "payment", fallbackMethod = "fallback")
@CircuitBreaker(name = "payment", fallbackMethod = "fallback")
public PaymentResult charge(Money amount) {
    return paymentApi.charge(amount);
}
```

```yaml
resilience4j.retry:
  instances:
    payment:
      max-attempts: 3
      wait-duration: 100ms
      enable-exponential-backoff: true
      exponential-backoff-multiplier: 2
      retry-exceptions:
        - java.io.IOException
        - org.springframework.web.client.HttpServerErrorException
```

**Когда применять.** Идемпотентные операции (GET, PUT, DELETE по ID).
Для не-идемпотентных операций — только с `Idempotency-Key` или с
проверкой не «улетела» ли уже первая попытка.

> Retry без exponential backoff и jitter создаёт retry storm — все
> клиенты бьются одновременно после восстановления сервиса. С jitter —
> запросы размазаны во времени.

### Bulkhead

**Цель.** Изоляция ресурсов: один медленный downstream-сервис не съедает
все потоки/соединения. Аналогия с переборками корабля — пробоина в
одном отсеке не топит судно.

```java
@Bulkhead(name = "payment", type = Bulkhead.Type.SEMAPHORE)
public PaymentResult charge(Money amount) {
    return paymentApi.charge(amount);
}
```

```yaml
resilience4j.bulkhead:
  instances:
    payment:
      max-concurrent-calls: 10  # не больше 10 параллельных вызовов
```

**Когда применять.** Когда несколько downstream-сервисов делят
тред-пул, и медленный из них может монополизировать его. Часто
сочетается с Circuit Breaker и Retry.

### Saga

**Цель.** Распределённая транзакция через цепочку локальных транзакций
с компенсациями. Альтернатива двухфазному коммиту (2PC), который плохо
масштабируется.

**Choreography (event-driven):** каждый сервис реагирует на события
других. Без центрального координатора. Проще, но логика размазана.

```text
Order created → Inventory reserves → Payment charges → Shipping
   ↓ if fail        ↓ if fail            ↓ if fail        ↓
Compensate ← Compensate inventory ← Compensate payment
```

**Orchestration:** центральный orchestrator вызывает сервисы и решает,
что делать при ошибках. Логика в одном месте, но сам orchestrator —
SPOF.

```java
public class OrderSaga {

    public void execute(Order order) {
        SagaContext context = new SagaContext();
        try {
            context.add(inventoryService.reserve(order));
            context.add(paymentService.charge(order));
            context.add(shippingService.schedule(order));
        } catch (Exception e) {
            log.error("Saga failed for order {}, compensating", order.getId(), e);
            compensate(context);
            throw e;
        }
    }

    private void compensate(SagaContext context) {
        // в обратном порядке
        Collections.reverse(context.steps());
        for (SagaStep step : context.steps()) {
            try {
                step.compensate();
            } catch (Exception e) {
                log.error("Compensation failed: {}", step.name(), e);
                // продолжаем компенсировать остальное, ошибки в лог + alert
            }
        }
    }
}
```

**Когда применять.** Бизнес-операция охватывает несколько сервисов,
каждый со своей БД. 2PC недоступен или нежелателен.

**Trade-offs:**

- **Eventual consistency.** В процессе исполнения данные несогласованы.
  Клиент должен это учитывать или получать «pending» статус.
- **Компенсации не всегда полны.** Нельзя «отписать sms» — при
  компенсации доставленных побочных эффектов нужны другие приёмы
  (отправить корректирующее sms, например).
- **Сложность отладки.** Поток размазан по сервисам — критичны
  distributed tracing и correlation ID.

## Когда использовать Enterprise Patterns

| Сценарий | Паттерны |
|---|---|
| CRUD-сервис на одной БД | Repository (через Spring Data), Service Layer. Остальное — overengineering |
| Сложная бизнес-логика | + Domain Model, Specification |
| Интеграция с внешним API | + Gateway, Anti-Corruption Layer |
| Микросервисы, async | + Message Channel, Transactional Outbox |
| Множество синхронных вызовов | + Circuit Breaker, Retry, Bulkhead |
| Распределённая транзакция | + Saga (choreography или orchestration) |
| Legacy-система с ужасной моделью | + Anti-Corruption Layer (на её границе) |

## Лучшие практики

- Не внедряй паттерн «на всякий случай». Каждый — это сложность,
  которая должна окупаться конкретной задачей.
- Domain Model > Anemic Domain. Логика, охраняющая инварианты, должна
  быть внутри объекта. Сервис — координатор, не дубль логики.
- Один Gateway / Anti-Corruption Layer на одну внешнюю систему.
  Не размазывай маппинг по разным местам.
- Outbox + CDC (Debezium) > ручной poller. CDC надёжнее и быстрее, но
  требует настройки репликации.
- Circuit Breaker + Retry + Bulkhead — не альтернативы, а слои защиты.
  Combinируй.
- Saga compensations должны быть идемпотентны. Любая компенсация может
  выполниться повторно при сбое.
- Specification Pattern полезен только при динамической композиции.
  Для статичных запросов — JPQL/derived queries проще.
- Всегда логируй идемпотентность критичных операций — без этого
  retry/saga превращаются в дубли заказов.

## Решение проблем

| Симптом | Возможная причина | Решение |
|---|---|---|
| Бизнес-логика дублируется в нескольких сервисах | Anemic domain — логика «всплыла» в сервисы | Перенести в Domain Model, оставить сервису координацию |
| Тесты домена требуют поднимать БД | Active Record или прямой доступ к ORM | Repository + Data Mapper; тестировать домен mock-ами |
| Запросы похожи, но дублируются | Нет переиспользования критериев | Specification Pattern для композиции |
| После save() сообщение в Kafka не отправилось | Прямая публикация без транзакции | Transactional Outbox |
| Каскадный отказ при недоступности downstream | Нет защиты от cascading failures | Circuit Breaker + Retry с jitter + timeout |
| Один медленный downstream съел все потоки | Общий пул потоков | Bulkhead (отдельный пул на каждого downstream) |
| Saga оставляет несогласованные данные | Compensation падает молча | Логи + alerting на каждую неуспешную компенсацию; manual remediation |
| Маппинг к Stripe SDK размазан по коду | Нет Gateway | Внедрить `PaymentGateway` интерфейс, перенести маппинг в одну реализацию |

## Частые вопросы

**Repository — это паттерн или просто обёртка?** Когда внутри
один-единственный `Spring Data` репозиторий и интерфейс просто
делегирует — это обёртка ради обёртки. Паттерн ценен, когда есть
доменная логика на уровне коллекции (поиск по бизнес-критериям, версии,
optimistic locking).

**Зачем Service Layer, если есть Domain Model?** Доменная модель
охраняет инварианты одного агрегата. Service координирует несколько
агрегатов и определяет границу транзакции — это разные роли.

**Saga vs 2PC?** 2PC требует, чтобы все участники поддерживали
двухфазный коммит и были одновременно доступны. Не масштабируется и
не подходит для микросервисов с разными БД. Saga — eventual consistency
плюс компенсации, всегда работает.

**Choreography или Orchestration для Saga?** Choreography проще для
2-3 сервисов и понятного потока. Orchestration лучше для длинных и
ветвистых сценариев — логика в одном месте.

**Anti-corruption layer и Gateway — одно и то же?** Близко, но не
тождественно. Gateway — это интерфейс к внешней системе. ACL добавляет
переводчик между моделями (когда внешняя модель «неправильная»).
Часто Gateway внутри использует ACL.

**Когда не нужен Transactional Outbox?** Если потеря части событий
допустима (метрики, аналитика, не-критичные уведомления). Для
бизнес-критичных событий outbox обязателен.

## См. также

- [Архитектурные паттерны](../architecture-patterns.md) — стили (монолит, микросервисы, layered)
- [Design Principles](../design-principles/design-principles.md) — SOLID, DRY, KISS
- [DDD](../ddd.md) — стратегические и тактические DDD-паттерны
- [System Design Basics](../system-design/system-design-basics.md) — capacity, CAP, компоненты
- [Microservices](../software-architecture/microservices.md) — микросервисная архитектура
- [Resilience patterns (interview)](../../interview/architecture/resilience-patterns-interview.md) — глубокое покрытие Circuit Breaker / Bulkhead / Retry
- [Saga pattern (interview)](../../interview/architecture/saga-pattern-interview.md) — углублённый разбор Saga
