---
title: "Вопросы на собеседовании: Spring Events"
description: "Spring Events: ApplicationEvent, @EventListener, ApplicationEventPublisher, @TransactionalEventListener, async events, @DomainEvents, Spring Modulith"
tags:
  - interview
  - spring
  - spring-events-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Events"
  - "Spring Events interview"
  - "Spring Events собеседование"
prerequisites:
  - "[[spring-events]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Spring Events`

`Spring Events` — механизм слабого связывания компонентов через события: публикатор не знает о подписчиках. По умолчанию синхронны и транзакционны. `@TransactionalEventListener` — ключевой инструмент для domain events в DDD-архитектуре.

## Полезные ссылки

### Официальная документация

- [Spring Application Events Reference](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events) — reference
- [Spring Modulith Events](https://docs.spring.io/spring-modulith/reference/events.html) — externalization

### Baeldung tutorials

- [Spring Events](https://www.baeldung.com/spring-events) — основы
- [Spring Context Events](https://www.baeldung.com/spring-context-events) — системные события
- [How to Test Spring Application Events](https://www.baeldung.com/spring-test-application-events)
- [DDD aggregates and @DomainEvents](https://www.baeldung.com/spring-data-ddd)
- [Event Externalization with Spring Modulith](https://www.baeldung.com/spring-modulith-event-externalization)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Spring Events и зачем нужны?](#q1-что-такое-spring-events-и-зачем-нужны)
- [Q2. Как опубликовать и обработать событие?](#q2-как-опубликовать-и-обработать-событие)
- [Q3. Как создать кастомное событие?](#q3-как-создать-кастомное-событие)
- [Q4. Какие встроенные Spring-события (context events) существуют?](#q4-какие-встроенные-spring-события-context-events-существуют)

**Продвинутые возможности**
- [Q5. (!) Как сделать обработку события асинхронной?](#q5-как-сделать-обработку-события-асинхронной)
- [Q6. (!) Что такое `@TransactionalEventListener` и зачем он нужен?](#q6-что-такое-transactionaleventlistener-и-зачем-он-нужен)
- [Q7. Какие фазы транзакции поддерживает `@TransactionalEventListener`?](#q7-какие-фазы-транзакции-поддерживает-transactionaleventlistener)
- [Q8. Как управлять порядком обработчиков одного события?](#q8-как-управлять-порядком-обработчиков-одного-события)
- [Q9. Как условно обработать событие?](#q9-как-условно-обработать-событие)
- [Q10. Может ли `@EventListener` возвращать значение?](#q10-может-ли-eventlistener-возвращать-значение)

**DDD и интеграция**
- [Q11. (!) Что такое `@DomainEvents` в Spring Data?](#q11-что-такое-domainevents-в-spring-data)
- [Q12. Зачем нужен `@AfterDomainEventPublication`?](#q12-зачем-нужен-afterdomaineventpublication)
- [Q13. Что такое Spring Modulith и как события используются для межмодульного взаимодействия?](#q13-что-такое-spring-modulith-и-как-события-используются-для-межмодульного-взаимодействия)

**Тестирование**
- [Q14. Как тестировать публикацию и обработку Spring Events?](#q14-как-тестировать-публикацию-и-обработку-spring-events)

**Архитектурные вопросы**
- [Q15. Когда использовать Spring Events, а когда прямой вызов метода?](#q15-когда-использовать-spring-events-а-когда-прямой-вызов-метода)
- [Q16. В чём разница между Spring Events и Kafka/RabbitMQ-сообщениями?](#q16-в-чём-разница-между-spring-events-и-kafkarabbitmq-сообщениями)

---

## Q1. Что такое Spring Events и зачем нужны?

**Spring Events** — реализация паттерна Observer/Publisher-Subscriber в рамках Spring-контекста. Позволяет компонентам общаться, не зная друг о друге.

```
OrderService.save() → publishEvent(OrderCreatedEvent)
                              ↓
        ┌─────────────────────────────────┐
        ↓                                 ↓
EmailListener.onOrderCreated()    InventoryListener.onOrderCreated()
```

**Зачем нужны:**
- Избежать прямых зависимостей между модулями (`OrderService` не зависит от `EmailService`)
- Реализовать side effects после бизнес-операций
- Реализовать domain events в DDD (событие из агрегата)
- Уведомить несколько компонентов об одном действии без изменения кода публикатора

**Когда НЕ нужны:**
- Когда логика должна выполниться синхронно и является частью транзакции
- Когда нужен гарантированный результат от обработчика (events — fire-and-forget)
- Для взаимодействия между микросервисами (там нужен MessageBroker)

## Q2. Как опубликовать и обработать событие?

**Публикатор:**

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository repository;

    @Transactional
    public Order createOrder(OrderRequest req) {
        Order order = repository.save(new Order(req));
        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        return order;
    }
}
```

**Слушатель:**

```java
@Component
@Slf4j
public class OrderNotificationListener {

    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Order created: {}", event.getOrderId());
        // отправить email, обновить статистику и т.д.
    }
}
```

**Альтернатива — инжектировать `ApplicationContext`** (не рекомендуется — слишком широкий контракт):

```java
applicationContext.publishEvent(new OrderCreatedEvent(order));
```

Лучше — инжектировать `ApplicationEventPublisher` (только публикация, без других возможностей контекста).

## Q3. Как создать кастомное событие?

**Вариант 1 — POJO (рекомендован, начиная со Spring 4.2):**

```java
@Getter
@AllArgsConstructor
public class OrderCreatedEvent {
    private final Long orderId;
    private final String customerId;
    private final BigDecimal total;
}
```

**Вариант 2 — расширение `ApplicationEvent` (старый стиль):**

```java
public class OrderCreatedEvent extends ApplicationEvent {

    private final Long orderId;

    public OrderCreatedEvent(Object source, Long orderId) {
        super(source);  // source — обычно this (публикатор)
        this.orderId = orderId;
    }

    public Long getOrderId() { return orderId; }
}
```

**Предпочтение:** POJO-события — проще, не требуют знания о Spring. `ApplicationEvent`-подход — легаси.

**Immutability:** события должны быть immutable (`@Value`, `record`, `final` поля) — один объект события может читать несколько слушателей.

```java
public record OrderCreatedEvent(Long orderId, String customerId, Instant occurredAt) { }
```

## Q4. Какие встроенные Spring-события (context events) существуют?

| Событие | Когда публикуется |
|---|---|
| `ContextRefreshedEvent` | После инициализации/обновления контекста |
| `ContextStartedEvent` | При вызове `context.start()` |
| `ContextStoppedEvent` | При вызове `context.stop()` |
| `ContextClosedEvent` | При закрытии контекста |
| `ApplicationReadyEvent` | Когда приложение готово обслуживать запросы (Spring Boot) |
| `ApplicationStartedEvent` | После запуска, до `CommandLineRunner` (Spring Boot) |
| `ApplicationFailedEvent` | При ошибке старта (Spring Boot) |
| `RequestHandledEvent` | После обработки HTTP-запроса (Spring MVC) |

```java
@Component
public class StartupListener {

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("Application is ready! Warming up cache...");
        cacheService.warmUp();
    }

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        log.info("Context is closing. Cleanup...");
    }
}
```

`ApplicationReadyEvent` — предпочтительный момент для действий "при старте": к этому времени все бины инициализированы, Tomcat слушает порт, CommandLineRunners отработали.

## Q5. Как сделать обработку события асинхронной?

По умолчанию Spring Events синхронны: `publishEvent()` не возвращается, пока все слушатели не отработают.

`@Async` на слушателе делает обработку асинхронной:

```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("eventsExecutor")
    public Executor eventsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("event-");
        executor.initialize();
        return executor;
    }
}

@Component
public class NotificationListener {

    @Async("eventsExecutor")
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        // Выполнится в отдельном потоке, не блокируя публикатора
        emailService.send(event.getCustomerId(), "Order created");
    }
}
```

**Важные последствия async-слушателей:**
- Исключение в слушателе не propagates к публикатору
- Нельзя перехватить через `EventListener + транзакция публикатора` (они разорваны)
- Нужен отдельный `@Transactional` в слушателе, если нужна транзакция

## Q6. Что такое `@TransactionalEventListener` и зачем он нужен?

Проблема с `@EventListener`: если слушатель вызывается внутри транзакции и транзакция откатится — слушатель уже отработал (возможно, отправил email о "созданном" заказе, который потом откатился).

`@TransactionalEventListener` решает это, привязывая выполнение к фазе транзакции:

```java
@Service
@Transactional
public class OrderService {
    public Order createOrder(OrderRequest req) {
        Order order = repository.save(order);
        publisher.publishEvent(new OrderCreatedEvent(order.getId()));
        // Если транзакция откатится — слушатель НЕ выполнится (при AFTER_COMMIT)
        return order;
    }
}

@Component
public class OrderNotificationListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        // Выполнится ТОЛЬКО если транзакция успешно закоммичена
        emailService.sendConfirmation(event.getOrderId());
    }
}
```

**Ключевое:** `@TransactionalEventListener` по умолчанию выполняется в **той же транзакции** что и публикатор (но уже после commit, то есть транзакция закрыта). Для новой транзакции: `@Transactional(propagation = REQUIRES_NEW)`.

## Q7. Какие фазы транзакции поддерживает `@TransactionalEventListener`?

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
```

| Фаза | Когда выполняется |
|---|---|
| `AFTER_COMMIT` (default) | После успешного commit |
| `AFTER_ROLLBACK` | После rollback |
| `AFTER_COMPLETION` | После завершения транзакции (commit или rollback) |
| `BEFORE_COMMIT` | Перед commit (ещё в рамках транзакции) |

```java
// Для BEFORE_COMMIT — можно ещё участвовать в транзакции
@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
@Transactional(propagation = Propagation.MANDATORY)
public void auditBeforeCommit(OrderCreatedEvent event) {
    auditRepository.save(new AuditEntry(event));
}

// Для AFTER_ROLLBACK — например, компенсирующие действия
@TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
public void onFailure(OrderCreatedEvent event) {
    log.error("Order creation rolled back: {}", event.getOrderId());
    alertService.notify("Order rollback", event.getOrderId());
}
```

**Подводный камень:** если у публикатора нет активной транзакции, `@TransactionalEventListener` по умолчанию **не выполнится**. Параметр `fallbackExecution = true` позволяет выполняться и без транзакции:

```java
@TransactionalEventListener(fallbackExecution = true)
public void handle(MyEvent event) { }
```

## Q8. Как управлять порядком обработчиков одного события?

`@Order` на методе с `@EventListener`:

```java
@Component
public class OrderLifecycleListeners {

    @Order(1)
    @EventListener
    public void validateOrder(OrderCreatedEvent event) {
        // Выполнится первым
    }

    @Order(2)
    @EventListener
    public void auditOrder(OrderCreatedEvent event) {
        // Выполнится вторым
    }

    @Order(3)
    @EventListener
    public void notifyCustomer(OrderCreatedEvent event) {
        // Выполнится третьим
    }
}
```

Без `@Order` порядок не гарантирован. Меньшее число — выше приоритет.

`Ordered.HIGHEST_PRECEDENCE = Integer.MIN_VALUE` — самый высокий приоритет.

**Важно:** порядок работает только для синхронных слушателей. Для async-слушателей порядок не определён.

## Q9. Как условно обработать событие?

`condition` в `@EventListener` — SpEL-выражение:

```java
@Component
public class PremiumOrderListener {

    @EventListener(condition = "#event.total > 10000")
    public void onLargeOrder(OrderCreatedEvent event) {
        // Только для заказов на сумму > 10000
        vipService.assignManager(event.getCustomerId());
    }

    @EventListener(condition = "#event.orderId != null && #event.customerId.startsWith('VIP')")
    public void onVipOrder(OrderCreatedEvent event) {
        priorityService.fastTrack(event.getOrderId());
    }
}
```

Параметры SpEL:
- `#event` — объект события (имя совпадает с параметром метода)
- `#root.event` — то же самое через root object
- Можно обращаться к полям и методам объекта

## Q10. Может ли `@EventListener` возвращать значение?

Да — если метод возвращает объект, он становится новым событием и публикуется автоматически:

```java
@Component
public class OrderEventChain {

    @EventListener
    public OrderShippedEvent onOrderPaid(OrderPaidEvent event) {
        shippingService.arrangeShipping(event.getOrderId());
        return new OrderShippedEvent(event.getOrderId(), LocalDate.now().plusDays(3));
        // Spring автоматически опубликует OrderShippedEvent
    }

    @EventListener
    public void onOrderShipped(OrderShippedEvent event) {
        emailService.sendShippingNotification(event);
    }
}
```

Если вернуть коллекцию — каждый элемент публикуется как отдельное событие:

```java
@EventListener
public List<NotificationEvent> onOrderCreated(OrderCreatedEvent event) {
    return List.of(
        new EmailNotification(event.getCustomerId()),
        new PushNotification(event.getCustomerId())
    );
}
```

## Q11. Что такое `@DomainEvents` в Spring Data?

Spring Data позволяет агрегатам самим хранить domain events и публиковать их при сохранении:

```java
public class Order extends AbstractAggregateRoot<Order> {
    private Long id;
    private String customerId;
    private List<OrderItem> items;
    private OrderStatus status;

    public Order pay(PaymentInfo payment) {
        this.status = OrderStatus.PAID;
        registerEvent(new OrderPaidEvent(this.id, payment.getAmount()));
        // registerEvent — из AbstractAggregateRoot
        return this;
    }
}
```

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> { }
```

При вызове `orderRepository.save(order)` Spring Data автоматически:
1. Сохраняет агрегат
2. Публикует все накопленные events через `ApplicationEventPublisher`
3. Очищает список events (через `@AfterDomainEventPublication`)

**Без `AbstractAggregateRoot`** — можно сделать вручную через `@DomainEvents`:

```java
public class Order {
    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    @DomainEvents
    public List<Object> domainEvents() { return domainEvents; }

    @AfterDomainEventPublication
    public void clearEvents() { domainEvents.clear(); }
}
```

## Q12. Зачем нужен `@AfterDomainEventPublication`?

`@AfterDomainEventPublication` — метод очистки domain events после их публикации:

```java
public class Order extends AbstractAggregateRoot<Order> {
    // AbstractAggregateRoot уже реализует @AfterDomainEventPublication
    // Но если вручную:

    @DomainEvents
    Collection<Object> domainEvents() { return List.copyOf(events); }

    @AfterDomainEventPublication
    void clearDomainEvents() { events.clear(); }
}
```

**Зачем важен:** без очистки один и тот же event будет публиковаться при каждом следующем `save()`. Это приводит к дублированию emails, созданию дублирующих записей и т.д.

`AbstractAggregateRoot` автоматически управляет этим — в большинстве случаев используйте его.

## Q13. Что такое Spring Modulith и как события используются для межмодульного взаимодействия?

**Spring Modulith** — библиотека для модульных монолитов. Модули общаются через события, не имея прямых зависимостей:

```java
// Модуль Order публикует событие
@Service
@Transactional
public class OrderService {
    public Order complete(Long orderId) {
        Order order = repository.completeOrder(orderId);
        publisher.publishEvent(new OrderCompleted(orderId));
        return order;
    }
}

// Модуль Inventory обрабатывает событие — без зависимости на OrderService
@ApplicationModuleListener  // = @TransactionalEventListener + @Async + retries
public class InventoryModule {
    @ApplicationModuleListener
    void onOrderCompleted(OrderCompleted event) {
        inventory.releaseReservation(event.orderId());
    }
}
```

**Externalization** — публикация событий во внешние брокеры (Kafka, RabbitMQ):

```java
@Externalized("orders.completed")  // → Kafka topic
public record OrderCompleted(Long orderId) { }
```

Spring Modulith хранит невыполненные события в БД и гарантирует доставку (outbox pattern из коробки).

## Q14. Как тестировать публикацию и обработку Spring Events?

**`ApplicationEvents` в тестах (Spring Boot 2.7+):**

```java
@SpringBootTest
@RecordApplicationEvents  // включить запись событий
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired ApplicationEvents events;

    @Test
    void createOrder_shouldPublishOrderCreatedEvent() {
        orderService.createOrder(new OrderRequest("CUST-1"));

        assertThat(events.stream(OrderCreatedEvent.class)).hasSize(1);
        OrderCreatedEvent event = events.stream(OrderCreatedEvent.class)
            .findFirst().orElseThrow();
        assertThat(event.getCustomerId()).isEqualTo("CUST-1");
    }
}
```

**Через `@SpyBean` на слушателе:**

```java
@SpringBootTest
class EventHandlingTest {

    @SpyBean NotificationListener notificationListener;
    @Autowired ApplicationEventPublisher publisher;

    @Test
    void shouldCallNotificationOnOrderCreated() {
        publisher.publishEvent(new OrderCreatedEvent(1L, "CUST-1"));
        verify(notificationListener, times(1)).onOrderCreated(any());
    }
}
```

**Через `MockEventListener`:**

```java
@TestComponent
public class TestOrderCreatedListener {
    private final List<OrderCreatedEvent> receivedEvents = new ArrayList<>();

    @EventListener
    public void capture(OrderCreatedEvent event) {
        receivedEvents.add(event);
    }

    public List<OrderCreatedEvent> getEvents() { return receivedEvents; }
}
```

## Q15. Когда использовать Spring Events, а когда прямой вызов метода?

| Критерий | Прямой вызов | Spring Events |
|---|---|---|
| Coupling | Тесная связь | Слабая связь |
| Несколько получателей | Нужно вызывать все | Один publish → все слушатели |
| Транзакционность | Контролируемая | Через `@TransactionalEventListener` |
| Тестируемость | Мокируем зависимость | Мокируем publisher / spy listener |
| Понятность | Очевидный flow | Неявный flow (нужно искать слушателей) |
| Side effects | Смешиваются с логикой | Вынесены из публикатора |

**Использовать Events когда:**
- Несколько компонентов реагируют на одно действие
- Хотим отвязать модули (DDD domain events)
- Side effect не должен выполняться при rollback (`@TransactionalEventListener`)
- Side effect можно выполнить позже (async)

**Прямой вызов когда:**
- Одна зависимость, простая логика
- Нужен результат от "получателя"
- Flow должен быть очевиден при чтении кода

## Q16. В чём разница между Spring Events и Kafka/RabbitMQ-сообщениями?

| Параметр | Spring Events | Kafka/RabbitMQ |
|---|---|---|
| Scope | Внутри JVM-процесса | Между процессами/сервисами |
| Персистентность | Нет (in-memory) | Да |
| Гарантия доставки | При ошибке — потеряно | At-least-once / exactly-once |
| Replay | Нет | Да (Kafka) |
| Масштабирование | Не нужно (один JVM) | Горизонтальное |
| Latency | Микросекунды | Миллисекунды |
| Setup | Zero config | Внешняя инфраструктура |

**Spring Events + Spring Modulith Outbox** — bridge между двумя мирами: события хранятся в БД (гарантия), потом публикуются в Kafka/RabbitMQ.

**Типичная архитектура:**
```
Бизнес-логика → Spring Event → @TransactionalEventListener → publish to Kafka
                                                             (в отдельной транзакции)
```

---

## See also

- [Spring Framework](spring-framework-interview.md) — ApplicationContext, BeanFactory, жизненный цикл бинов
- [Spring Boot](spring-boot-interview.md) — ApplicationReadyEvent, ApplicationStartedEvent для startup логики
- [Spring AOP](spring-aop-interview.md) — события реализованы без AOP, но часто используются вместе
- [Spring Data JPA](spring-data-jpa-interview.md) — @DomainEvents, AbstractAggregateRoot, @AfterDomainEventPublication
- [Spring Scheduling](spring-scheduling-interview.md) — @Scheduled vs события для периодических задач
- [Event-Driven Patterns](../../architecture/event-driven-patterns-interview.md) — общие паттерны event-driven архитектуры
- [Apache Kafka](../../messaging/kafka-interview.md) — для межсервисных событий с гарантией доставки
- [Domain-Driven Design](../../architecture/ddd-interview.md) — domain events, aggregates, bounded context
- [Распределённые системы](../../architecture/distributed-systems-interview.md) — transactional outbox, at-least-once delivery
- [Unit Testing](../../testing/unit-testing-interview.md) — @RecordApplicationEvents, ApplicationEvents, тестирование listeners
- [Шпаргалка: Spring Events — события приложения](../../../frameworks/java-frameworks/spring/spring-events.md) — теория
